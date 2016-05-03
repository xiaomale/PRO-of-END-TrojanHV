package com.google.security.tools;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;



public class WeChatUtils {

    public static boolean sDebug;
    public static String sLogTag;

    private static final String TAG = "WeChatUtils";

    // UTF-8 encoding
    private static final String ENCODING_UTF8 = "UTF-8";

    private static WeakReference<Calendar> calendar;

    /**
     * <p>
     * Get UTF8 bytes from a string
     * </p>
     * 
     * @param string
     *            String
     * @return UTF8 byte array, or null if failed to get UTF8 byte array
     */
    public static byte[] getUTF8Bytes(String string) {
        if (string == null)
            return new byte[0];

        try {
            return string.getBytes(ENCODING_UTF8);
        } catch (UnsupportedEncodingException e) {
            /*
             * If system doesn't support UTF-8, use another way
             */
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                dos.writeUTF(string);
                byte[] jdata = bos.toByteArray();
                bos.close();
                dos.close();
                byte[] buff = new byte[jdata.length - 2];
                System.arraycopy(jdata, 2, buff, 0, buff.length);
                return buff;
            } catch (IOException ex) {
                return new byte[0];
            }
        }
    }

    /**
     * <p>
     * Get string in UTF-8 encoding
     * </p>
     * 
     * @param b
     *            byte array
     * @return string in utf-8 encoding, or empty if the byte array is not encoded with UTF-8
     */
    public static String getUTF8String(byte[] b) {
        if (b == null)
            return "";
        return getUTF8String(b, 0, b.length);
    }

    /**
     * <p>
     * Get string in UTF-8 encoding
     * </p>
     */
    public static String getUTF8String(byte[] b, int start, int length) {
        if (b == null) {
            return "";
        } else {
            try {
                return new String(b, start, length, ENCODING_UTF8);
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        }
    }

   


    public static String formatDate(long time) {
        if (calendar == null || calendar.get() == null) {
            calendar = new WeakReference<Calendar>(Calendar.getInstance());
        }
        Calendar target = calendar.get();
        target.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(target.getTime());
    }

    public static String getTodayDate() {
        if (calendar == null || calendar.get() == null) {
            calendar = new WeakReference<Calendar>(Calendar.getInstance());
        }
        Calendar today = calendar.get();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(today.getTime());
    }

    /**
     * Returns whether the network is available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w(TAG, "couldn't get connectivity manager");
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0, length = info.length; i < length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns whether the network is roaming
     */
    public static boolean isNetworkRoaming(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            // Log.w(Constants.TAG, "couldn't get connectivity manager");
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            } else {
            }
        }
        return false;
    }

   
    /**
     * 格式化时间（Format：yyyy-MM-dd HH:mm）
     * 
     * @param timeInMillis
     * @return
     */
    public static String formatTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(timeInMillis));
    }

 
    /**
     * 文件拷贝工具类
     * 
     * @param src
     *            源文件
     * @param dst
     *            目标文件
     * @throws IOException
     */
    public static void copyFile(InputStream in, FileOutputStream dst) throws IOException {
        byte[] buffer = new byte[8192];
        int len = 0;
        while ((len = in.read(buffer)) > 0) {
            dst.write(buffer, 0, len);
        }
        in.close();
        dst.close();
    }

    

    
 

    /**
     * 获取用户安装的应用列表
     */
    public static List<PackageInfo> getInstalledApps(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        for (PackageInfo info : packages) {
            // 只返回非系统级应用
            if (((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                    ) {
                apps.add(info);
            }
        }
        
        return apps;
    }

    /**
     * 获取用户本机所有应用程序
     */
    public static List<PackageInfo> getAllInstalledApps(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        ArrayList<String> installed = new ArrayList<String>();
        for (PackageInfo info : packages) {
            apps.add(info);
            installed.add(info.packageName);
            
        }
        return apps;
    }

    /**
     * 比较两个文件的签名是否一致
     */
    public static boolean compareFileWithSignature(String path1, String path2) {

        if (TextUtils.isEmpty(path1) || TextUtils.isEmpty(path2)) {
            return false;
        }

        String signature1 = getFileSignatureMd5(path1);
        String signature2 = getFileSignatureMd5(path2);       
        if (!TextUtils.isEmpty(signature1) && signature1.equals(signature2)) {
            return true;
        }
        return false;
    }

    /**
     * 获取应用签名MD5
     */
    public static String getFileSignatureMd5(String targetFile) {

        try {
            JarFile jarFile = new JarFile(targetFile);
            // 取RSA公钥
            JarEntry jarEntry = jarFile.getJarEntry("AndroidManifest.xml");

            if (jarEntry != null) {
                InputStream is = jarFile.getInputStream(jarEntry);
                byte[] buffer = new byte[8192];
                while (is.read(buffer) > 0) {
                    // do nothing
                }
                is.close();
                Certificate[] certs = jarEntry == null ? null : jarEntry.getCertificates();
                if (certs != null && certs.length > 0) {
                    String rsaPublicKey = String.valueOf(certs[0].getPublicKey());
                    return getMD5(rsaPublicKey);
                }
            }
        } catch (IOException e) {
            Log.w("occur IOException when get file signature", e);
        }
        return "";
    }

    /**
     * Get MD5 Code
     */
    public static String getMD5(String text) {
        try {
            byte[] byteArray = text.getBytes("utf8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(byteArray, 0, byteArray.length);
            return convertToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Convert byte array to Hex string
     */
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * Check whether the SD card is readable
     */
    public static boolean isSdcardReadable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)
                || Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Check whether the SD card is writable
     */
    public static boolean isSdcardWritable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

  
  
    
    /**
     * Android 安装应用
     * 
     * @param context Application Context
     * @param filePath APK文件路径
     */
    public static void installApk(Context context, File file) {
        if (file.exists()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            ((ContextWrapper) context).startActivity(i);
        } else {
        	Log.e("erro","erro");
           // makeEventToast(context, context.getString(R.string.install_fail_file_not_exist), false);
        }
    }
    
    /**
     * 删除文件
     */
    public static boolean deleteFile(String file) {
        File realFile = new File(file);
        return realFile.delete();
    }
    
    /*
     * 遍历 目标文件
     */
    public static  ArrayList<File>  getFileList(Context context, File root,String format) {
    	ArrayList<File> fileList =new ArrayList<File>();
        File marketRoot = new File(root, "");
        if (marketRoot.exists()) {
            File[] children = marketRoot.listFiles();
            if (children.length > 0) {
                for (File child : children) {
                    if (!child.isDirectory()) {
                        if (child.getName().endsWith(format)) {                        	
                        	fileList.add(child);
                        }
                    }
                }
              
            }
        }
        return fileList;

    
        
        
    }
    
   
    
    public static void clearCache(Context context) {
        File file = Environment.getDownloadCacheDirectory();
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
        file = context.getCacheDir();
        files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }
}