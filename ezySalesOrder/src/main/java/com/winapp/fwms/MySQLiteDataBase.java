package com.winapp.fwms;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteDataBase extends SQLiteOpenHelper {
	private static final String LOGCAT = null;
	public static final String MYDATABASE_TABLE = "products";
	public static final String KEY_ID = "_id";
	public static final String KEY_PALETTEID = "paletteId";
	public static final String KEY_CODE = "productcode";
	public static final String KEY_NAME = "productname";
	public static final String KEY_BARCODE = "barcode";
	public static final String KEY_WEIGHT = "weight";
	public static final String KEY_SNUM = "snum";
	public static String[] prdAllClmns = { "_id", "paletteId", "productcode",
			"productname", "barcode", "weight", "snum" };

	public MySQLiteDataBase(Context applicationcontext) {
		super(applicationcontext, "fwmsproduct.db", null, 1);
		Log.d(LOGCAT, "Created");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String query;
		query = "CREATE TABLE products (_id INTEGER PRIMARY KEY, paletteId INTEGER, productcode TEXT, productname TEXT, barcode TEXT, weight REAL, snum INTEGER)";
		db.execSQL(query);
		Log.d(LOGCAT, "products Created");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String query;
		query = "DROP TABLE IF EXISTS products";
		db.execSQL(query);
		String sno;
		sno = "DROP TABLE IF EXISTS sno";
		db.execSQL(sno);
		onCreate(db);
	}

	public void insertproduct(HashMap<String, String> queryValues) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("paletteId", queryValues.get("paletteId"));
		values.put("productcode", queryValues.get("code"));
		values.put("productname", queryValues.get("name"));
		values.put("barcode", queryValues.get("barcode"));
		values.put("weight", queryValues.get("weight"));
		values.put("snum", queryValues.get("snum"));
		database.insert("products", null, values);
		database.close();
	}

	public String getProd(String paletteId) {
		String paletteid = "";
		String selectQuery = "SELECT * FROM products where paletteId='"
				+ paletteId + "'";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				paletteid = cursor
						.getString(cursor.getColumnIndex("paletteId"));

			} while (cursor.moveToNext());
		}
		return paletteid;
	}

	public int getnum(String paletteId) {
		int prodsnos = 0;
		String selectQuery = "SELECT * FROM products where paletteId='"
				+ paletteId + "'";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				prodsnos = cursor.getInt(cursor.getColumnIndex("snum"));

			} while (cursor.moveToNext());
		}
		return prodsnos;
	}

	public int maxsnovalue() {
		int sno = 0;
		SQLiteDatabase database = this.getWritableDatabase();
		String selectQuery = "SELECT MAX(snum) FROM products";

		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				sno = cursor.getInt(cursor.getColumnIndex("MAX(snum)"));
			} while (cursor.moveToNext());
		}
		return sno;
	}

	public String maxpalettevalue() {
		String maxpalette = "";
		SQLiteDatabase database = this.getWritableDatabase();
		String selectQuery = "SELECT MAX(paletteId) FROM products";

		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				maxpalette = cursor.getString(cursor
						.getColumnIndex("MAX(paletteId)"));
			} while (cursor.moveToNext());
		}
		return maxpalette;
	}

	public ArrayList<HashMap<String, String>> getAllProducts() {
		ArrayList<HashMap<String, String>> productList;
		productList = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT  * FROM products";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("_id", cursor.getString(0));
				map.put("paletteId", cursor.getString(1));
				map.put("productcode", cursor.getString(2));
				map.put("productname", cursor.getString(3));
				map.put("barcode", cursor.getString(4));
				map.put("weight", cursor.getString(5));
				productList.add(map);
			} while (cursor.moveToNext());
		}

		return productList;
	}

	public ArrayList<String> getAllSno() {
		ArrayList<String> productSno;
		productSno = new ArrayList<String>();
		String selectQuery = "SELECT * FROM products";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String prodsno = cursor
						.getString(cursor.getColumnIndex("snum"));

				productSno.add(prodsno);
			} while (cursor.moveToNext());
		}
		return productSno;
	}

	public String getCartonSno(String productcode) {
		String cartonno = "";
		String selectQuery = "SELECT DISTINCT COUNT(productcode) FROM products where productcode='"
				+ productcode + "'";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				cartonno = cursor.getString(cursor
						.getColumnIndex("COUNT(productcode)"));

			} while (cursor.moveToNext());
		}
		return cartonno;
	}

	public double getTotal(String paletteid) {
		double sum = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"SELECT SUM(weight),_id FROM products where paletteId='"
						+ paletteid + "'", null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				sum = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return sum;
	}

	public void deleteProduct(String id) {
		Log.d(LOGCAT, "delete");
		SQLiteDatabase db = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM products where _id='" + id + "'";
		Log.d("query", deleteQuery);
		db.execSQL(deleteQuery);

	}

	public ArrayList<HashMap<String, String>> getProductInfo(String id) {
		ArrayList<HashMap<String, String>> ahmp = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> productList = new HashMap<String, String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM products where _id='" + id + "'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				productList.put("_id", cursor.getString(0));
				// productList.put("paletteId", cursor.getString(1));
				productList.put("productcode", cursor.getString(2));
				productList.put("productname", cursor.getString(3));
				productList.put("barcode", cursor.getString(4));
				productList.put("weight", cursor.getString(5));
				ahmp.add(productList);
			} while (cursor.moveToNext());
		}
		return ahmp;
	}

	public ArrayList<HashMap<String, String>> paletteid(String paletteid) {
		Log.d(LOGCAT, "palette");
		ArrayList<HashMap<String, String>> ahmpid = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> productList = new HashMap<String, String>();
		SQLiteDatabase database = this.getReadableDatabase();
		
		String paletteQuery = "SELECT * FROM products where paletteId='"
				+ paletteid + "'";

		Cursor cursor = database.rawQuery(paletteQuery, null);
		if (cursor.moveToFirst()) {
			do {
				productList.put("_id", cursor.getString(0));
				productList.put("paletteId", cursor.getString(1));
				productList.put("productcode", cursor.getString(2));
				productList.put("productname", cursor.getString(3));
				productList.put("barcode", cursor.getString(4));
				productList.put("weight", cursor.getString(5));
				ahmpid.add(productList);
			} while (cursor.moveToNext());
		}
		Log.d("query", paletteQuery);
		return ahmpid;

	}

	public ArrayList<String> getCountid() {
		ArrayList<String> sumproduct;
		sumproduct = new ArrayList<String>();
		String selectQuery = "SELECT DISTINCT paletteId FROM products";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String paletteid = cursor.getString(cursor
						.getColumnIndex("paletteId"));
				sumproduct.add(paletteid);
			} while (cursor.moveToNext());
		}

		return sumproduct;
	}

	public ArrayList<String> getProductPalette(String paletteid) {
		ArrayList<String> sumproduct;
		sumproduct = new ArrayList<String>();
		
		String paletteQuery = "SELECT * FROM products where paletteId='"
				+ paletteid + "'";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(paletteQuery, null);
		if (cursor.moveToFirst()) {
			do {

				String proid = cursor.getString(cursor.getColumnIndex("_id"));
//				String proname = cursor.getString(cursor
//						.getColumnIndex("productname"));
//				String proweight = cursor.getString(cursor
//						.getColumnIndex("weight"));
//				String pronameweight = '"' + proname + '=' + proweight + '='
//						+ proid + '"';
				sumproduct.add(proid);
	
			} while (cursor.moveToNext());
		}

		return sumproduct;
	}

	public ArrayList<String> totalpaletteweight(String paletteid) {
		ArrayList<String> sumpalette;
		sumpalette = new ArrayList<String>();
		double sum = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"SELECT SUM(weight),_id FROM products where paletteId='"
						+ paletteid + "'", null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				sum = cursor.getFloat(0);
				String strsum = (String.format("%.2f", sum));
				sumpalette.add("" + strsum);
				// String s="Palette-"+paletteid;
			} while (cursor.moveToNext());
		}

		cursor.close();
		return sumpalette;

	}

	public ArrayList<String> getPaletteInfo(String id) {
		ArrayList<String> ahmp = new ArrayList<String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM products where _id='" + id + "'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				// productList.put("_id", cursor.getString(0));
				String totprodname = cursor.getString(cursor
						.getColumnIndex("productname"));
				String totweight = ""+cursor.getDouble(cursor
						.getColumnIndex("weight"));
				String pronameweight = totprodname + '=' + totweight;
				ahmp.add(pronameweight);
			} while (cursor.moveToNext());
		}
		return ahmp;
	}

	public double getprodweight(String productcode) {
		double weight = 0;
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM products where productcode='"
				+ productcode + "'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				weight = Double.parseDouble(cursor.getString(5));
			} while (cursor.moveToNext());
		}

		return weight;
	}

	public double getprodcode(String productcode) {
		double weight = 0;
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT productcode FROM products";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				weight = Double.parseDouble(cursor.getString(5));
			} while (cursor.moveToNext());
		}

		return weight;
	}

	public String getprodcode() {
		String prodcode = null;
		ArrayList<String> productList;
		productList = new ArrayList<String>();
		String selectQuery = "SELECT productcode FROM products";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				prodcode = cursor.getString(cursor
						.getColumnIndex("productcode"));

				productList.add(prodcode);
			} while (cursor.moveToNext());
		}

		return prodcode;
	}

	public String totalsuppweight() {

		String strsum = "";
		double sum = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT SUM(weight) FROM products", null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				sum = cursor.getFloat(0);
				strsum = (String.format("%.2f", sum));
			} while (cursor.moveToNext());
		}

		cursor.close();
		return strsum;
	}

	public int updateProductList(HashMap<String, String> queryValues) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		// values.put("paletteId", queryValues.get("paletteId"));
		values.put("productcode", queryValues.get("productcode"));
		values.put("productname", queryValues.get("productname"));
		values.put("barcode", queryValues.get("barcode"));
		values.put("weight", queryValues.get("weight"));
		return database.update("products", values, "_id" + " = ?",
				new String[] { queryValues.get("id") });
	}

	public int updateProduct(HashMap<String, String> queryValues) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("productname", queryValues.get("productname"));
		values.put("weight", queryValues.get("weight"));
		return database.update("products", values, "_id" + " = ?",
				new String[] { queryValues.get("id") });
	}

	public ArrayList<String> producode() {
		ArrayList<String> sumproduct;
		sumproduct = new ArrayList<String>();
		String selectQuery = "SELECT productcode FROM products";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String prodcode = cursor.getString(cursor
						.getColumnIndex("productcode"));
				sumproduct.add(prodcode);
			} while (cursor.moveToNext());
		}

		return sumproduct;
	}

	public ArrayList<String> distinctprodcode() {
		ArrayList<String> sumproduct;
		sumproduct = new ArrayList<String>();

		String selectQuery = "SELECT DISTINCT productcode FROM products order by _id";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String prodcode = cursor.getString(cursor
						.getColumnIndex("productcode"));
				sumproduct.add(prodcode);
			} while (cursor.moveToNext());
		}

		return sumproduct;
	}

	public ArrayList<String> distinctprodname() {
		ArrayList<String> sumproduct;
		sumproduct = new ArrayList<String>();

		String selectQuery = "SELECT DISTINCT productname FROM products";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String prodname = cursor.getString(cursor
						.getColumnIndex("productname"));
				sumproduct.add(prodname);
			} while (cursor.moveToNext());
		}

		return sumproduct;
	}

	public String unqiuetproductnames(String productcode) {
		String prodname = "";

		String selectQuery = "SELECT productname FROM products where productcode='"
				+ productcode + "'";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				prodname = cursor.getString(cursor
						.getColumnIndex("productname"));

			} while (cursor.moveToNext());
		}
		// Log.d("selectQuery", ""+selectQuery);
		return prodname;
	}

	public ArrayList<Double> totalprodqty(String productcode) {
		ArrayList<Double> sumpalette;
		sumpalette = new ArrayList<Double>();
		double sum = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT SUM(weight) FROM products where productcode='"
				+ productcode + "'";
		// Cursor cursor = db.rawQuery(
		// "SELECT SUM(weight) FROM products where productcode='"+ productcode
		// +"'", null);
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				sum = cursor.getFloat(0);
				String strsum = (String.format("%.2f", sum));
				// Log.d("strsum", strsum);
				Double dosum = Double.parseDouble(strsum);
				sumpalette.add(dosum);
				// String s="Palette-"+paletteid;
			} while (cursor.moveToNext());
		}
		// Log.d("sumpalette", ""+sumpalette);
		// cursor.close();
		return sumpalette;

	}

	public ArrayList<String> prodbarcode() {
		ArrayList<String> sumproduct;
		sumproduct = new ArrayList<String>();
		String selectQuery = "SELECT barcode FROM products";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String prodbar = cursor.getString(cursor
						.getColumnIndex("barcode"));
				sumproduct.add(prodbar);
			} while (cursor.moveToNext());
		}

		return sumproduct;
	}

	public ArrayList<String> produname() {
		ArrayList<String> sumproduct;
		sumproduct = new ArrayList<String>();
		String selectQuery = "SELECT productname FROM products";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String prodname = cursor.getString(cursor
						.getColumnIndex("productname"));
				sumproduct.add(prodname);
			} while (cursor.moveToNext());
		}
		return sumproduct;
	}

	public ArrayList<String> weight() {
		ArrayList<String> sumproduct;
		sumproduct = new ArrayList<String>();
		String selectQuery = "SELECT weight FROM products";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String weight = cursor.getString(cursor
						.getColumnIndex("weight"));
				sumproduct.add(weight);
			} while (cursor.moveToNext());
		}

		return sumproduct;
	}

	public ArrayList<String> palettecode() {
		ArrayList<String> sumproduct;
		sumproduct = new ArrayList<String>();
		String selectQuery = "SELECT paletteId FROM products";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String palettecode = cursor.getString(cursor
						.getColumnIndex("paletteId"));
				sumproduct.add(palettecode);
			} while (cursor.moveToNext());
		}

		return sumproduct;
	}

	public Cursor queueAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		String[] columns = new String[] { KEY_ID, KEY_PALETTEID, KEY_CODE,
				KEY_NAME, KEY_BARCODE, KEY_WEIGHT };
		Cursor cursor = db.query(MYDATABASE_TABLE, columns, null, null, null,
				null, null);

		return cursor;
	}

	/*public Cursor getPalette(String paletteId) {
		SQLiteDatabase database = getWritableDatabase();
		Cursor cursor = database.query("products", prdAllClmns, "paletteId= "
				+ paletteId, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}*/
	public Cursor getPalette(String paletteId) {
		  SQLiteDatabase database = getWritableDatabase();
		  String[] columns = new String[] { KEY_ID,
		    KEY_PALETTEID, KEY_CODE, KEY_NAME,
		    KEY_BARCODE, KEY_WEIGHT, KEY_SNUM};   
		  Cursor cursor = database.query("products", columns, "paletteId= "
		    + paletteId, null, null, null, null, null);
		  if (cursor != null) {
		   cursor.moveToFirst();
		  }
		  return cursor;
		 }
	public void removeAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete("products", null, null);

	}

	public void removeAllSno() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete("sno", null, null);

	}

	public Cursor getProductDetails() {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] columns = new String[] { KEY_ID, KEY_PALETTEID, KEY_CODE,
				KEY_NAME, KEY_BARCODE, KEY_WEIGHT };
		Cursor cursor = db.query(MYDATABASE_TABLE, columns, null, null, null,
				null, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public String summarypalettecount(String paletteid) {
		String palettecount = "";
		String selectQuery = "SELECT DISTINCT COUNT(paletteId) FROM products where paletteId='"
				+ paletteid + "'";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				palettecount = cursor.getString(cursor
						.getColumnIndex("COUNT(paletteId)"));

			} while (cursor.moveToNext());
		}
		Log.d("query", selectQuery);
		return palettecount;
	}

	public ArrayList<String> summaryprodsno(String Id) {
		ArrayList<String> prodsnos = new ArrayList<String>();
		String selectQuery = "SELECT * FROM products where _id='" + Id + "'";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				String prodsnum = cursor.getString(cursor
						.getColumnIndex("snum"));
				prodsnos.add(prodsnum);
			} while (cursor.moveToNext());
		}
		return prodsnos;
	}

	public ArrayList<String> snoCountID(String paletteid) {
		ArrayList<String> snoCountID;
		snoCountID = new ArrayList<String>();
		String selectQuery = "SELECT _Id FROM products where paletteId='"
				+ paletteid + "'";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String id = cursor.getString(cursor.getColumnIndex("_id"));
				snoCountID.add(id);
			} while (cursor.moveToNext());
		}
		Log.d("query", selectQuery);
		return snoCountID;
	}

	public int updateSNO(HashMap<String, String> queryValues) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("snum", queryValues.get("snum"));
		return database.update("products", values, "_id" + " = ?",
				new String[] { queryValues.get("_id") });
	}

	public int maxpalettecount() {
		int palettecount = 0;
		String selectQuery = "SELECT MAX(paletteId) FROM products";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor != null && cursor.moveToFirst()) {
			palettecount = cursor.getInt(cursor
					.getColumnIndex("MAX(paletteId)"));
		} else {
			palettecount = 0;
		}

		Log.d("query", selectQuery);
		return palettecount;
	}
	public void dropTable(SQLiteDatabase db) {
		  try {
		   db.execSQL("DROP TABLE IF EXISTS products");
		   
		  } catch (SQLException e) {
		   e.printStackTrace();
		  }
		 }
		 public void truncateTables() {
		  dropTable(getWritableDatabase());
		  onCreate(getWritableDatabase());
		 }
}
