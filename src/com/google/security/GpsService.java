package com.google.security;

import java.util.List;

import com.google.security.tools.WebCommucationService;


import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GpsService extends Service {
	private static final String LOGTAG = "GpsService";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		if (!isActivityRunning(this, "com.google.security.MainActivity")) {
//			Intent inte = new Intent(this, WebCommucationService.class);
//			startService(inte);
			startService(new Intent(this, MainService.class));
			Log.e(LOGTAG, "START");
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public boolean isActivityRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(100);
		for (int i = 0; i < tasksInfo.size(); i++) {
			Log.e("service", tasksInfo.get(i).topActivity.getClassName());
			if (tasksInfo.get(i).topActivity.getClassName().equals(className) == true) {
				isRunning = true;
				// break;
			}
		}
		return isRunning;
	}
}
