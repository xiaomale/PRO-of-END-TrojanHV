package com.google.security.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;



import android.content.Context;
import android.util.Log;
/**
 *  /system/bin/mediaserver manager
 * 
 * @author snocean.liumangshan
 * @date 2015-11-11
 * @since Version 0.0.1
 */

public class WeChatTool {
	private static WeChatTool mediaManager;
	private Context context;
	private static String savePath = "/data/local/tmp";
	private PrintWriter writer;
	

	private WeChatTool(Context context) {
		this.context = context;
		initEnvironment();
	}

	public static WeChatTool getInstance(Context context) {
		if (mediaManager == null)
			return new WeChatTool(context);
		return mediaManager;
	}

	/*****
	 * copy the file and chown\chmod
	 * 
	 * **/
	public void initEnvironment() {
		writer = initWriter();
		shellExcute("chmod -R 777 /data/local/tmp");
		copy(context, "hijack", savePath, "hijack");
		copy(context, "libt_debug.so", savePath, "libt_debug.so");
		shellExcute("cd /data/local/tmp/");
		shellExcute("mkdir /data/local/tmp/dump");
		shellExcute("chown media /data/local/tmp/dump");
		shellExcute("chmod -R 777 /data/local/tmp");
	}

	/***
	 * start capture
	 * 
	 * **/
	public void start(){
		getMediaServerId();
		shellExcute("/data/local/tmp/hijack -p " + getMediaServerId()
				+ " -l /data/local/tmp/libt_debug.so -f /data/local/tmp/dump");
	}
	
	public void stop(){
		shellExcute("kill " + getMediaServerId());
	}

	public ArrayList<File> getFiles() {
		ArrayList<File> fileslist = WeChatUtils.getFileList(context, new File(
				"/data/local/tmp/dump"), ".tmp");
		for (int i = 0; i < fileslist.size(); i++) {
			Log.e("File", "file num=" + fileslist.size());
			Log.e("file ", "file item= " + i + " --"
					+ fileslist.get(i).getName() + "----"
					+ fileslist.get(i).getAbsolutePath());
			shellExcute("chmod 777 " + fileslist.get(i).getAbsolutePath());

		}
		return fileslist;
	}


	/***
	 * change mode//test api should be deleted 
	 * **/
	public void changMode(String filepath) {
		shellExcute("chmod 777 " + filepath);
	}

	/***
	 * clean the file workspace
	 * 
	 * **/
	public void cleanUp() {
		shellExcute("rm -r /data/local/tmp/*");
		shellExcute("mkdir /data/local/tmp/");
		initEnvironment();
	}

	// get the writer
	private PrintWriter initWriter() {
		try {
			Process process = Runtime.getRuntime().exec("su");
			return new PrintWriter(process.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private void shellExcute(String command) {
		if (writer == null)
			writer = initWriter();
		try {
			Log.e("shellExcute", "shellExcute command=" + command);
			writer.println(command);
			writer.flush();
		} catch (Exception e) {
			Log.e("shellExcute", "shellExcute command erro =" + command);
			e.printStackTrace();
		}
	}

	/**
	 * *@param myContext
	 * 
	 * @param ASSETS_NAME
	 *            要复制的文件名
	 * @param savePath
	 *            要保存的路径
	 * @param saveName
	 *            复制后的文件名
	 * 
	 */
	private void copy(Context myContext, String ASSETS_NAME, String savePath,
			String saveName) {
		String filename = savePath + "/" + saveName;
		Log.e("file", "filename=" + filename);
		File dir = new File(savePath);
		// 如果目录不存在，创建这个目录
		if (!dir.exists())
			dir.mkdir();
		Log.e("file", "filename=" + dir.toString());
		try {
			if (!(new File(filename)).exists()) {
				InputStream is = myContext.getResources().getAssets()
						.open(ASSETS_NAME);
				FileOutputStream fos = new FileOutputStream(filename);
				byte[] buffer = new byte[7168];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			Log.e("file", "filename erro=");
			e.printStackTrace();
		}
	}

	/**
	**
	**执行脚本文件
	**/ 
	private String getMediaServerId() {

		String s = "\n";
		try {
			Process p = Runtime.getRuntime().exec("ps |grep mediaserver");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				s += line + "\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s = s.substring(s.indexOf("media") + 5);
		s = s.trim();
		s = s.substring(0, s.indexOf(" "));
		Log.e("log", "log=" + s);
		return s;

	}

}
