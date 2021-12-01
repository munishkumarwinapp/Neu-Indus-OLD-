package com.winapp.fwms;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RandomDeviceIDDataBase extends SQLiteOpenHelper{
	static SQLiteDatabase database = null;
	static RandomDeviceIDDataBase instance = null;
	static final String DATABASE_NAME = "RandomDeviceID.db";
	static final int DATABASE_VERSION = 1;
	public static final String RANDOM_TABLE = "random_deviceno";
	public static final String COLUMN_RANDOM_ID = "_id";
	public static final String COLUMN_RANDOM_DEVICE_ID = "device_id";
	public static void init(Context context) {
		if (null == instance) {
			instance = new RandomDeviceIDDataBase(context);
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
	public RandomDeviceIDDataBase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + RANDOM_TABLE + " ( "
				+ COLUMN_RANDOM_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_RANDOM_DEVICE_ID + " TEXT)");
	}
	

	public static void deviceID(String deviceno) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_RANDOM_DEVICE_ID, deviceno);
		getDatabase().insert(RANDOM_TABLE, null, cv);
	}
	
	public static Cursor getCursor() {

		String[] columns = new String[] {COLUMN_RANDOM_ID,COLUMN_RANDOM_DEVICE_ID};
		return getDatabase().query(RANDOM_TABLE, columns, null, null, null,
				null, null);
	}
	
	public static String getDeviceId() {
		String randomdeviceno="";
		String Query = "SELECT device_id FROM random_deviceno";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				randomdeviceno = cursor.getString(cursor.getColumnIndex(COLUMN_RANDOM_DEVICE_ID));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return randomdeviceno;
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
