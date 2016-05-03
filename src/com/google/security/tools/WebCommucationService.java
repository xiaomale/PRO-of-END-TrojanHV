package com.google.security.tools;

import java.util.Timer;
import java.util.TimerTask;

import de.tavendo.autobahn.WebSocketConnection;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WebCommucationService extends Service {
	WebSocket webSocketconnect;
	private final WebSocketConnection mConnection = new WebSocketConnection();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.e("websocket", "成功启动websocket服务.......");
		webSocketconnect = new WebSocket(mConnection,
				WebCommucationService.this);
		webSocketconnect.startWebSocketConnection();

		// final Timer timer = new Timer(true);
		// TimerTask task = new TimerTask() {
		// public void run() {
		//
		// try {
		//
		// Log.e("Websocket",
		// " network state:" + mConnection.isConnected());
		// mConnection.disconnect();
		// webSocketconnect.startWebSocketConnection();
		// Log.e("Websocket",
		// " network state:" + mConnection.isConnected());
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		// };
		// timer.schedule(task, 0, 5 * 60 * 1000);
		super.onCreate();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mConnection.disconnect();
		webSocketconnect.startWebSocketConnection();

		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
