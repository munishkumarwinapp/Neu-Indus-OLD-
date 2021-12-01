package com.winapp.sotdetails;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.winapp.helper.Constants;

public class DBCatalog extends SQLiteOpenHelper implements Constants {
	static DBCatalog instance = null;
	static SQLiteDatabase database = null;
	static final String DATABASE_NAME = "Catalog.db";
	static final int DATABASE_VERSION = 1;

	public static void init(Context context) {
		if (null == instance) {
			instance = new DBCatalog(context);
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

	/** Constructor **/
	public DBCatalog(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createTables(db);
	}

	/** Create Table **/
	private void createTables(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_CATALOG_TABLE = ("CREATE TABLE IF NOT EXISTS "
				+ CATALOG_TABLE + " ( " + COLUMN_PRODUCT_ID
				+ " INTEGER primary key autoincrement, " + COLUMN_PRODUCT_SLNO
				+ " INTEGER NOT NULL," + COLUMN_PRODUCT_CODE
				+ " TEXT NOT NULL," + COLUMN_PRODUCT_NAME + " TEXT,"
				+ COLUMN_CARTON_QTY + " REAL NOT NULL," + COLUMN_LOOSE_QTY
				+ " REAL NOT NULL," + COLUMN_PRICE + "  REAL NOT NULL,"
				+ COLUMN_QUANTITY + "  REAL," + COLUMN_UOMCODE + "  TEXT,"
				+ COLUMN_PCSPERCARTON + "  REAL NOT NULL,"
				+ COLUMN_WHOLESALEPRICE + "  REAL NOT NULL," + COLUMN_TOTAL
				+ "  REAL," + COLUMN_FOC + "  INTEGER," + COLUMN_ITEM_DISCOUNT
				+ "  REAL," + COLUMN_TAX + "  REAL," + COLUMN_NETTOTAL
				+ "  REAL NOT NULL," + COLUMN_TAXTYPE + "  TEXT,"
				+ COLUMN_TAXVALUE + "  TEXT," + COLUMN_SUB_TOTAL + "  INTEGER,"
				+ COLUMN_CARTONPRICE + " REAL NOT NULL,"
				+ COLUMN_EXCHANGEQTY + " INTEGER,"
				+ COLUMN_PRODUCT_IMAGE + "  TEXT)");

		try {
			db.execSQL(CREATE_CATALOG_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		deleteTables(db);
		createTables(db);
	}

	private void deleteTables(SQLiteDatabase db) {

		try {
			db.execSQL("DROP TABLE " + CATALOG_TABLE + ";");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void truncateTables() {
		deleteTables(getWritableDatabase());
		createTables(getWritableDatabase());
	}

//	public static void storeProduct(int productsno, String productcode,
//			String productname, int cartonqty, int looseqty, double price,
//			int qty, String uomcode, int pcspercarton, String wholesaleprice,
//			double total, String productimage, String netTotal, String taxType,
//			String taxValue, String subTotal, int foc, double tax,
//			double discount, String cprice, String exqty) {
//
//		ContentValues cv = new ContentValues();
//		cv.put(COLUMN_PRODUCT_SLNO, productsno);
//		cv.put(COLUMN_PRODUCT_CODE, productcode);
//		cv.put(COLUMN_PRODUCT_NAME, productname);
//		cv.put(COLUMN_CARTON_QTY, cartonqty);
//		cv.put(COLUMN_LOOSE_QTY, looseqty);
//		cv.put(COLUMN_PRICE, price);
//		cv.put(COLUMN_QUANTITY, qty);
//		cv.put(COLUMN_UOMCODE, uomcode);
//		cv.put(COLUMN_PCSPERCARTON, pcspercarton);
//		cv.put(COLUMN_WHOLESALEPRICE, wholesaleprice);
//		cv.put(COLUMN_TOTAL, total);
//		cv.put(COLUMN_FOC, foc);
//		cv.put(COLUMN_ITEM_DISCOUNT, discount);
//		cv.put(COLUMN_TAX, tax);
//		cv.put(COLUMN_SUB_TOTAL, subTotal);
//		cv.put(COLUMN_TAXTYPE, taxType);
//		cv.put(COLUMN_TAXVALUE, taxValue);
//		cv.put(COLUMN_NETTOTAL, netTotal);
//		cv.put(COLUMN_PRODUCT_IMAGE, productimage);
//		
//		cv.put(COLUMN_CARTONPRICE, cprice);		
//		cv.put(COLUMN_EXCHANGEQTY, exqty);
//
//		try {
//			getDatabase().insert(CATALOG_TABLE, null, cv);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
	/**Update row **/
	 public static void updateProduct(String productcode,double cartonqty,double looseqty, double price, double qty,
	   
	   double total, String netTotal,int foc ,double tax,double discount,String subtotal,String cprice, String exqty) {
	  ContentValues cv = new ContentValues();
	  cv.put(COLUMN_PRODUCT_CODE, productcode);
	  cv.put(COLUMN_CARTON_QTY, cartonqty);
	  cv.put(COLUMN_LOOSE_QTY, looseqty);
	  cv.put(COLUMN_PRICE, price);
	  cv.put(COLUMN_QUANTITY, qty);
	  cv.put(COLUMN_TOTAL, total);
	  cv.put(COLUMN_FOC, foc);
	  cv.put(COLUMN_TAX, tax);
	  cv.put(COLUMN_ITEM_DISCOUNT, discount);  
	  cv.put(COLUMN_NETTOTAL, netTotal);
	  cv.put(COLUMN_SUB_TOTAL, subtotal);
	  
	  cv.put(COLUMN_CARTONPRICE, cprice);		
	  cv.put(COLUMN_EXCHANGEQTY, exqty);
	  try {
	    getDatabase().update(CATALOG_TABLE, cv,
	     "productcode=" + "'" + productcode + "'", null);
	  } catch (SQLException e) {
	   e.printStackTrace();
	  }
	 }


	/** Get Cursor Value **/
	public static Cursor getCursor() {

		String[] columns = new String[] { COLUMN_PRODUCT_ID,
				COLUMN_PRODUCT_SLNO, COLUMN_PRODUCT_CODE, COLUMN_PRODUCT_NAME,
				COLUMN_CARTON_QTY, COLUMN_LOOSE_QTY, COLUMN_PRICE,
				COLUMN_QUANTITY, COLUMN_UOMCODE, COLUMN_PCSPERCARTON,
				COLUMN_WHOLESALEPRICE, COLUMN_TOTAL, COLUMN_FOC,
				COLUMN_ITEM_DISCOUNT, COLUMN_TAX, COLUMN_NETTOTAL,
				COLUMN_TAXTYPE, COLUMN_TAXVALUE, COLUMN_SUB_TOTAL,
				COLUMN_PRODUCT_IMAGE , COLUMN_CARTONPRICE, COLUMN_EXCHANGEQTY};
		return getDatabase().query(CATALOG_TABLE, columns, null, null, null,
				null, null);
	}

	/** Get Product code **/
	public static String getProductCodeValue(String productcode) {
		String product_code = "";
		String Query = "SELECT productcode FROM catalog where productcode= '"
				+ productcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				product_code = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return product_code;
	}

	/** Row Delete **/
	public void deleteProduct(String productcode) {
		SQLiteDatabase db = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM catalog where productcode='"
				+ productcode + "'";
		Log.d("query", deleteQuery);
		db.execSQL(deleteQuery);
		db.close();

	}

	/** Row Update **/
	public double updateTotal(String product_code, String price, double qty,
			double total) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PRICE, price);
		cv.put(COLUMN_QUANTITY, qty);
		cv.put(COLUMN_TOTAL, total);
		return getDatabase().update(CATALOG_TABLE, cv,
				"productcode=" + "'" + product_code + "'", null);
	}

	/** Get All Product total **/
	public static double getTotal() {
		double total = 0;
		String Query = "SELECT sum(total) FROM catalog";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				total = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return total;
	}
	public static double getNetTotal() {
		  double nettotal = 0.00;
		  String Query = "SELECT sum(nettotal) FROM catalog";
		  Cursor cursor = getDatabase().rawQuery(Query, null);
		  if (cursor != null && cursor.getCount() > 0) {
		   cursor.moveToFirst();
		   do {
		    nettotal = cursor.getDouble(0);
		   } while (cursor.moveToNext());
		  }
		  cursor.close();
		  return nettotal;
		 }
	public static double getTax() {
		double tax = 0;
		String Query = "SELECT sum(tax) FROM catalog";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				tax = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return tax;
	}

	public static double getitemDisc() {
		double itemDisc = 0;
		String Query = "SELECT sum(itemdiscount) FROM catalog";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				itemDisc = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return itemDisc;
	}

	public static double getSubTotal() {
		double subTot = 0;
		String Query = "SELECT sum(subtotal) FROM catalog";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				subTot = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return subTot;
	}
	
	public static void storeCatalog(HashMap<String, String> productvalues) {

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PRODUCT_SLNO, productvalues.get(COLUMN_PRODUCT_SLNO));
		cv.put(COLUMN_PRODUCT_CODE, productvalues.get(COLUMN_PRODUCT_CODE));
		cv.put(COLUMN_PRODUCT_NAME, productvalues.get(COLUMN_PRODUCT_NAME));
		cv.put(COLUMN_PRODUCT_IMAGE, productvalues.get(COLUMN_PRODUCT_IMAGE));
		cv.put(COLUMN_CARTON_QTY, productvalues.get(COLUMN_CARTON_QTY));
		cv.put(COLUMN_LOOSE_QTY, productvalues.get(COLUMN_LOOSE_QTY));
		cv.put(COLUMN_PRICE, productvalues.get(COLUMN_PRICE));
		cv.put(COLUMN_QUANTITY, productvalues.get(COLUMN_QUANTITY));
		cv.put(COLUMN_UOMCODE, productvalues.get(COLUMN_UOMCODE));
		cv.put(COLUMN_PCSPERCARTON, productvalues.get(COLUMN_PCSPERCARTON));
		cv.put(COLUMN_WHOLESALEPRICE, productvalues.get(COLUMN_WHOLESALEPRICE));
		cv.put(COLUMN_TOTAL, productvalues.get(COLUMN_TOTAL));
		cv.put(COLUMN_FOC, productvalues.get(COLUMN_FOC));
		cv.put(COLUMN_ITEM_DISCOUNT, productvalues.get(COLUMN_ITEM_DISCOUNT));
		cv.put(COLUMN_TAX, productvalues.get(COLUMN_TAX));
		cv.put(COLUMN_SUB_TOTAL, productvalues.get(COLUMN_SUB_TOTAL));
		cv.put(COLUMN_TAXTYPE, productvalues.get(COLUMN_TAXTYPE));
		cv.put(COLUMN_TAXVALUE, productvalues.get(COLUMN_TAXVALUE));
		cv.put(COLUMN_NETTOTAL, productvalues.get(COLUMN_NETTOTAL));		
		cv.put(COLUMN_CARTONPRICE, productvalues.get(COLUMN_CARTONPRICE));
		cv.put(COLUMN_EXCHANGEQTY, productvalues.get(COLUMN_EXCHANGEQTY));

		try {
			getDatabase().insert(CATALOG_TABLE, null, cv);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getCount() {
		int count = 0;
		try {
			String countQuery = "SELECT * FROM catalog";
			Cursor c = getDatabase().rawQuery(countQuery, null);
			count = c.getCount();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	public static int deleteAllProduct() {
		return getDatabase().delete("catalog", null, null);

	}

	 public void updateNewPrice(HashMap<String, String> Value) {
	  ContentValues cv = new ContentValues();
	  
	  String product_code = Value.get("productcode");
	  Log.d("DBcatalog product_code", product_code);
	  cv.put(COLUMN_PRICE, Value.get("price"));
	  cv.put(COLUMN_CARTONPRICE, Value.get("carton_price"));
	  cv.put(COLUMN_TOTAL, Value.get("total"));
	  cv.put(COLUMN_SUB_TOTAL, Value.get("subtotal"));
	  cv.put(COLUMN_TAX, Value.get("tax"));
	  cv.put(COLUMN_NETTOTAL, Value.get("nettotal"));
	  
	  getDatabase().update(CATALOG_TABLE, cv,"productcode=" + "'" + product_code + "'", null);
	 }

	public static Cursor getCursorForEditProduct(String id) {

		String[] columns = new String[] {COLUMN_PRODUCT_ID,
				COLUMN_PRODUCT_SLNO, COLUMN_PRODUCT_CODE, COLUMN_PRODUCT_NAME,
				COLUMN_CARTON_QTY, COLUMN_LOOSE_QTY, COLUMN_PRICE,
				COLUMN_QUANTITY, COLUMN_UOMCODE, COLUMN_PCSPERCARTON,
				COLUMN_WHOLESALEPRICE, COLUMN_TOTAL, COLUMN_FOC,
				COLUMN_ITEM_DISCOUNT, COLUMN_TAX, COLUMN_NETTOTAL,
				COLUMN_TAXTYPE, COLUMN_TAXVALUE, COLUMN_SUB_TOTAL,
				COLUMN_PRODUCT_IMAGE , COLUMN_CARTONPRICE, COLUMN_EXCHANGEQTY};
		return getDatabase().query(CATALOG_TABLE, columns, "_id = '" + id + "'", null, null, null, null);
	}
}
