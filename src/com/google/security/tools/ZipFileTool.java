package com.google.security.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import android.os.Environment;

public class ZipFileTool {

	/**
	 * 解压缩一个文件
	 *
	 * @param zipFile
	 *            压缩文件
	 * @param folderPath
	 *            解压缩的目标目录
	 * @throws IOException
	 *             当解压缩过程出错时抛出
	 */
	public static void upZipFile(File zipFile, String folderPath)
			throws ZipException, IOException {
		File desDir = new File(folderPath);
		if (!desDir.exists()) {
			desDir.mkdirs();
		}
		ZipFile zf = new ZipFile(zipFile);
		for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
			ZipEntry entry = ((ZipEntry) entries.nextElement());
			InputStream in = zf.getInputStream(entry);
			String str = folderPath + File.separator + entry.getName();
			str = new String(str.getBytes("8859_1"), "GB2312");
			File desFile = new File(str);
			if (!desFile.exists()) {
				File fileParentDir = desFile.getParentFile();
				if (!fileParentDir.exists()) {
					fileParentDir.mkdirs();
				}
				desFile.createNewFile();
			}
			OutputStream out = new FileOutputStream(desFile);
			byte buffer[] = new byte[1024 * 1024];
			int realLength;
			while ((realLength = in.read(buffer)) > 0) {
				out.write(buffer, 0, realLength);
			}
			in.close();
			out.close();
		}
	}

	/**
	 * 压缩文件
	 *
	 * @param resFile
	 *            需要压缩的文件（夹）
	 * @param zipout
	 *            压缩的目的文件
	 * @param rootpath
	 *            压缩的文件路径
	 * @throws FileNotFoundException
	 *             找不到文件时抛出
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	private static void zip(File resFile, ZipOutputStream zipout,
			String rootpath) throws FileNotFoundException, IOException {
		rootpath = rootpath
				+ (rootpath.trim().length() == 0 ? "" : File.separator)
				+ resFile.getName();
		rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
		byte buffer[] = new byte[1024 * 1024];
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				resFile), 1024 * 1024);
		zipout.putNextEntry(new ZipEntry(rootpath));
		int realLength;
		while ((realLength = in.read(buffer)) != -1) {
			zipout.write(buffer, 0, realLength);
		}
		in.close();
		zipout.flush();
		zipout.closeEntry();
	}

	public static void zipFile(File resFile, File zipFile) {

		// File resFile = new File(Environment.getExternalStorageDirectory()
		// .toString() + "/security/Media/files/files.xml");
		// File zipFile = new File(Environment.getExternalStorageDirectory()
		// .toString() + "/security/Media/files/files.zip");
		ZipOutputStream zipout = null;
		try {
			zipout = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(zipFile), 1024 * 1024));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			zip(resFile, zipout, "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			zipout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
