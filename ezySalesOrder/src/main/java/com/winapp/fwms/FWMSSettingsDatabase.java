package com.winapp.fwms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.winapp.model.UrlList;

import java.util.ArrayList;

public class FWMSSettingsDatabase extends SQLiteOpenHelper {
	static FWMSSettingsDatabase instance = null;
	static SQLiteDatabase database = null;

	static final String DATABASE_NAME = "FWMSSettings.db";
	static final int DATABASE_VERSION = 9;

	public static final String SETTINGS_TABLE = "settings";
	public static final String COLUMN_SETTINGS_ID = "_id";
	public static final String COLUMN_SETTINGS_NAME = "url";
	public static final String PRINTER_TABLE = "printerMACAddress";
	public static final String COLUMN_MACADDRESS = "macaddress";
	public static final String SWITCH_TABLE = "OnOff";
	public static final String COLUMN_MODE = "mode";
	public static final String COLUMN_PRINTER_TYPE = "printertype";
	public static final String COLUMN_CATALOG_TYPE = "catalogtype";
	public static final String STOCKCHECK_TABLE = "stockcheck";
	public static final String INVOICEUSERENABLE_TABLE = "invoiceuserenable";
	
	public static final String DELIVERYONINVOICE_TABLE = "deliveryoninvoice";
	public static final String PODPENDING_TABLE = "PODPending";
	public static final String INVOICEADDPRODUCTTAB_TABLE = "invoiceaddproducttab";
	public static final String PRINTER_TYPE = "printer_type";
	public static final String CATALOG_TYPE_TABLE = "catalog_type_table";

	public static final String COLUMN_NAME = "name";
	public static final String SETTINGS_ALTER_TABLE = "settings_temp";
//	public static final String COLUMN_SETTINGS_NAME = "url";

	public static void init(Context context) {
		if (null == instance) {
			instance = new FWMSSettingsDatabase(context);
		}
	}

	public static SQLiteDatabase getDatabase() {
		if (null == database) {
			database = instance.getWritableDatabase();
		}
		return database;
	}

	public static void deactivate() {
		if (null != database && database.isOpen()) {
			database.close();
		}
		database = null;
		instance = null;
	}

	public static void storeUrl(String url) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_SETTINGS_NAME, url);
		getDatabase().insert(SETTINGS_TABLE, null, cv);
	}

	public static String getUrl() {
		String url = null;
		String alarmNameQuery = "SELECT url FROM settings";
		Cursor cursor = getDatabase().rawQuery(alarmNameQuery, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				url = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return url;
	}

	public static void updateUrl(String url) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_SETTINGS_NAME, url);
		// Log.d("Updated URL", url);
		getDatabase().update(SETTINGS_TABLE, cv, "_id = 1", null);
	}

	public FWMSSettingsDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		db.execSQL("CREATE TABLE IF NOT EXISTS " + SETTINGS_TABLE + " ( "
				+ COLUMN_SETTINGS_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_SETTINGS_NAME + " TEXT NOT NULL)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + PRINTER_TABLE + " ( "
				+ COLUMN_SETTINGS_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_MACADDRESS + " TEXT)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + SWITCH_TABLE + " ( "
				+ COLUMN_SETTINGS_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_MODE + " INTEGER)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + STOCKCHECK_TABLE + " ( "
				+ COLUMN_SETTINGS_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_MODE + " INTEGER)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + INVOICEUSERENABLE_TABLE + " ( "
				+ COLUMN_SETTINGS_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_MODE + " INTEGER)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + DELIVERYONINVOICE_TABLE
				+ " ( " + COLUMN_SETTINGS_ID
				+ " INTEGER primary key autoincrement, " + COLUMN_MODE
				+ " INTEGER)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + PODPENDING_TABLE + " ( "
				+ COLUMN_SETTINGS_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_MODE + " INTEGER)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + INVOICEADDPRODUCTTAB_TABLE
				+ " ( " + COLUMN_SETTINGS_ID
				+ " INTEGER primary key autoincrement, " + COLUMN_MODE
				+ " INTEGER)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + PRINTER_TYPE + " ( "
				+ COLUMN_SETTINGS_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_PRINTER_TYPE + " TEXT)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + CATALOG_TYPE_TABLE + " ( "
				+ COLUMN_SETTINGS_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_CATALOG_TYPE + " TEXT)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + SETTINGS_ALTER_TABLE + " ( "
				+ COLUMN_SETTINGS_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_SETTINGS_NAME + " TEXT NOT NULL, " + COLUMN_NAME + " TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE);
		onCreate(db);
	}

	public static void printerAddress(String macaddress) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MACADDRESS, macaddress);
		getDatabase().insert(PRINTER_TABLE, null, cv);
	}

	public static Cursor getPrinter() {

		String[] columns = new String[] { COLUMN_SETTINGS_ID, COLUMN_MACADDRESS };
		return getDatabase().query(PRINTER_TABLE, columns, null, null, null,
				null, null);
	}

	public static String getPrinterAddress() {
		String printer = "";
		String Query = "SELECT macaddress FROM printerMACAddress";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				printer = cursor.getString(cursor
						.getColumnIndex(COLUMN_MACADDRESS));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return printer;
	}

	public static void updatePrinterAddress(String macaddress) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MACADDRESS, macaddress);
		getDatabase().update(PRINTER_TABLE, cv, "_id = 1", null);
	}

	public static int getPrinterCount() {
		int printercount = 0;
		String Query = "SELECT COUNT(_id) FROM printerMACAddress";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor.moveToFirst()) {
			do {

				printercount = cursor.getInt(cursor
						.getColumnIndex("COUNT(_id)"));

			} while (cursor.moveToNext());
		}
		return printercount;
	}

	public static void setMode(int mode) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MODE, mode);
		getDatabase().insert(SWITCH_TABLE, null, cv);
	}

	public static void setStockMode(int mode) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MODE, mode);
		getDatabase().insert(STOCKCHECK_TABLE, null, cv);
	}

	public static void setInvoiceuserMode(int mode) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MODE, mode);
		getDatabase().insert(INVOICEUSERENABLE_TABLE, null, cv);
	}

	public static void updateMode(int mode) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MODE, mode);
		getDatabase().update(SWITCH_TABLE, cv, "_id = 1", null);
	}

	public static void updateStockMode(int mode) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MODE, mode);
		getDatabase().update(STOCKCHECK_TABLE, cv, "_id = 1", null);
	}

	public static void updateInvoiceuserMode(int mode) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MODE, mode);
		getDatabase().update(INVOICEUSERENABLE_TABLE, cv, "_id = 1", null);
	}

	public static String getMode() {
		String mode = "";
		String Query = "SELECT mode FROM OnOff";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				mode = cursor.getString(cursor.getColumnIndex(COLUMN_MODE));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return mode;
	}

	public static String getStockMode() {
		String mode = "";
		String Query = "SELECT mode FROM stockcheck";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				mode = cursor.getString(cursor.getColumnIndex(COLUMN_MODE));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return mode;
	}

	public static String getInvoiceuserMode() {
		String mode = "";
		String Query = "SELECT mode FROM invoiceuserenable";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				mode = cursor.getString(cursor.getColumnIndex(COLUMN_MODE));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return mode;
	}

	public static int getModeId() {
		int mode = 0;
		String Query = "SELECT mode FROM OnOff where _id='" + 1 + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				mode = cursor.getInt(cursor.getColumnIndex(COLUMN_MODE));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return mode;
	}

	public static int getStockModeId() {
		int mode = 0;
		String Query = "SELECT mode FROM stockcheck where _id='" + 1 + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				mode = cursor.getInt(cursor.getColumnIndex(COLUMN_MODE));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return mode;
	}

	public static int getInvoiceuserModeId() {
		int mode = 0;
		String Query = "SELECT mode FROM invoiceuserenable where _id='" + 1 + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				mode = cursor.getInt(cursor.getColumnIndex(COLUMN_MODE));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return mode;
	}

	public static void saveDOPrintMode(int mode) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MODE, mode);
		getDatabase().insert(DELIVERYONINVOICE_TABLE, null, cv);
	}

	public static void updateDOPrintMode(int mode) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MODE, mode);
		getDatabase().update(DELIVERYONINVOICE_TABLE, cv, "_id = 1", null);
	}
	
	public static void savePODPending(int mode) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MODE, mode);
		getDatabase().insert(PODPENDING_TABLE, null, cv);
	}

	public static void updatePODPending(int mode) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MODE, mode);
		getDatabase().update(PODPENDING_TABLE, cv, "_id = 1", null);
	}
	
	public static void saveInvoiceaddproducttab(int mode) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MODE, mode);
		getDatabase().insert(INVOICEADDPRODUCTTAB_TABLE, null, cv);
	}

	public static void updateInvoiceaddproducttab(int mode) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_MODE, mode);
		getDatabase().update(INVOICEADDPRODUCTTAB_TABLE, cv, "_id = 1", null);
	}


	public static String getDOPrintMode() {
		String mode = "";
		String Query = "SELECT mode FROM deliveryoninvoice";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				mode = cursor.getString(cursor.getColumnIndex(COLUMN_MODE));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return mode;
	}
	
	public static String getPODPending() {
		String mode = "";
		String Query = "SELECT mode FROM PODPending";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				mode = cursor.getString(cursor.getColumnIndex(COLUMN_MODE));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return mode;
	}
	
	public static String getInvoiceaddproducttab() {
		String mode = "";
		String Query = "SELECT mode FROM invoiceaddproducttab";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				mode = cursor.getString(cursor.getColumnIndex(COLUMN_MODE));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return mode;
	}

	public static Cursor getDOPrintCursor() {

		String[] columns = new String[] { COLUMN_SETTINGS_ID, COLUMN_MODE };
		return getDatabase().query(DELIVERYONINVOICE_TABLE, columns, null,
				null, null, null, null);
	}

	public static Cursor getStockType() {

		String[] columns = new String[] { COLUMN_SETTINGS_ID, COLUMN_MODE };
		return getDatabase().query(STOCKCHECK_TABLE, columns, null, null, null,
				null, null);
	}

	public static Cursor getInvoiceuserType() {

		String[] columns = new String[] { COLUMN_SETTINGS_ID, COLUMN_MODE };
		return getDatabase().query(INVOICEUSERENABLE_TABLE, columns, null, null, null,
				null, null);
	}
	
	public static Cursor getPODPendingCursor() {

		String[] columns = new String[] { COLUMN_SETTINGS_ID, COLUMN_MODE };
		return getDatabase().query(PODPENDING_TABLE, columns, null,
				null, null, null, null);
	}
	
	public static Cursor getInvoiceaddproducttabCursor() {

		String[] columns = new String[] { COLUMN_SETTINGS_ID, COLUMN_MODE };
		return getDatabase().query(INVOICEADDPRODUCTTAB_TABLE, columns, null,
				null, null, null, null);
	}


	public static void storePrinterType(String printertype) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PRINTER_TYPE, printertype);
		getDatabase().insert(PRINTER_TYPE, null, cv);
	}

	public static Cursor getPrinterType() {

		String[] columns = new String[] { COLUMN_SETTINGS_ID, COLUMN_PRINTER_TYPE };
		return getDatabase().query(PRINTER_TYPE, columns, null, null, null,
				null, null);
	}

	public static String getPrinterTypeStr() {
		String printer = "";
		String Query = "SELECT printertype FROM printer_type";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				printer = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRINTER_TYPE));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return printer;
	}

	public static void updatePrinterType(String printertype) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PRINTER_TYPE, printertype);
		getDatabase().update(PRINTER_TYPE, cv, "_id = 1", null);
	}

	// catalog save type
	public static void storeCatalogType(String cattype) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_CATALOG_TYPE, cattype);
		getDatabase().insert(CATALOG_TYPE_TABLE, null, cv);
	}

	public static Cursor getCatalogType() {

		String[] columns = new String[] { COLUMN_SETTINGS_ID, COLUMN_CATALOG_TYPE };
		return getDatabase().query(CATALOG_TYPE_TABLE, columns, null, null, null,
				null, null);
	}

	public static String getCatalogTypeStr() {
		String printer = "";
		String Query = "SELECT catalogtype FROM catalog_type_table";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				printer = cursor.getString(cursor
						.getColumnIndex(COLUMN_CATALOG_TYPE));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return printer;
	}

	public static void updateCatalogType(String printertype) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_CATALOG_TYPE, printertype);
		getDatabase().update(CATALOG_TYPE_TABLE, cv, "_id = 1", null);
	}

	public static ArrayList<UrlList> getTempUrl(){
		ArrayList<UrlList> urlListData = new ArrayList<UrlList>();
		String get_Temp_url = "SELECT * FROM settings_temp";
		Cursor cursor = getDatabase().rawQuery(get_Temp_url, null);

		if (cursor.moveToFirst()) {
			do {
				UrlList urlList=new UrlList();
				urlList.setDef_name(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
				urlList.setId(cursor.getString(cursor.getColumnIndex(COLUMN_SETTINGS_ID)));
				urlList.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_SETTINGS_NAME)));
				urlListData.add(urlList);
			} while (cursor.moveToNext());
		}
		Log.d("ListURL","-->"+urlListData+urlListData.size());
		return urlListData;
	}

	public static void storeTempUrl(String url) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NAME,"");
		cv.put(COLUMN_SETTINGS_NAME, url);
		getDatabase().insert(SETTINGS_ALTER_TABLE, null, cv);
		Log.d("storeTempUrl","-->"+cv);
	}

	public static void updateTempUrl(String url, String get_id) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NAME,"default");
		cv.put(COLUMN_SETTINGS_NAME, url);
		cv.put(COLUMN_SETTINGS_ID,get_id);
		getDatabase().update(SETTINGS_ALTER_TABLE, cv, "_id ="+get_id, null);
		Log.d("updateTempUrl","-->"+cv);
	}

	public static void deleteTempUrl(String settingId){

		String deleteQuery = "DELETE FROM settings_temp where _id='"
				+ settingId + "'";
		Log.d("query", deleteQuery);
		getDatabase().execSQL(deleteQuery);

	}

}