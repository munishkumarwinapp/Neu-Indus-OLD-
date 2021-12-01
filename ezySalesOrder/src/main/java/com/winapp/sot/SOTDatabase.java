package com.winapp.sot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;

import com.winapp.adapter.Attribute;
import com.winapp.model.Product;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SOTDatabase extends SQLiteOpenHelper {

	static SOTDatabase instance = null;
	static SQLiteDatabase database = null;

	static final String DATABASE_NAME = "SOT.db";
	static final int DATABASE_VERSION = 15;

	public static final String PRODUCT_TABLE = "sales_product";
	public static final String BILLDISC_TABLE = "bill_disc";
	public static final String IMAGE_TABLE = "image_table";
	public static final String AREA_TABLE = "area_table";
	public static final String BATCH_TABLE = "batch_table";
	public static final String CONSIGNMENT_STOCK_TABLE = "consignment_table";
	public static final String EXPENSE_TABLE = "expense_table";
	public static final String VANDRIVER_TABLE = "vandriver_table";
	public static final String MERCHANDISE_TABLE = "merchandise_table";
	public static final String USERTACKING_MASTER_TABLE = "usertracking_master_table";
	public static final String SO_DETAIL_TABLE = "so_detail_table";
	public static final String ATTRIBUTE_TABLE = "attribute_table";
	public  static final String TAX_TABLE = "tax_table";
	public  static final String MANUAL_STOCK_TABLE = "manual_stock_table";
	public static final String CLOSE_STOCk_TABLE = "close_stock";
	public static final String DENOMINATION_TABLE = "denomination_table";


	public static final String COLUMN_PRODUCT_ID = "_id";
	public static final String COLUMN_PRODUCT_CODE = "product_code";
	public static final String COLUMN_PRODUCT_NAME = "product_name";
	public static final String COLUMN_CARTON_QTY = "carton_qty";
	public static final String COLUMN_LOOSE_QTY = "loose_qty";
	public static final String COLUMN_QUANTITY = "quantity";
	public static final String COLUMN_FOC = "foc";
	public static final String COLUMN_PRODUCT_PRICE = "price";
	public static final String COLUMN_ITEM_DISCOUNT = "item_discount";
	public static final String COLUMN_BILL_DISCOUNT = "bill_discount";
	public static final String COLUMN_TOTAL_DISCOUNT = "total_discount";
	public static final String COLUMN_MINIMUM_SELLING_PRICE = "MinimumSellingPrice";
	public static final String COLUMN_MINIMUM_CARTON_SELLING_PRICE = "minimumCartonSellingPrice";
	public static final String COLUMN_PRODUCT_UOM = "uom";
	public static final String COLUMN_PIECE_PERQTY = "piece_per_qty";
	public static final String COLUMN_TOTAL = "total";
	public static final String COLUMN_TAX = "tax";
	public static final String COLUMN_NETTOTAL = "net_total";
	public static final String COLUMN_TAXCODE = "tax_code";
	public static final String COLUMN_TAXNAME = "tax_name";
	public static final String COLUMN_RETURN = "return_qty";

	public static final String COLUMN_TAXTYPE = "tax_type";
	public static final String COLUMN_TAXVALUE = "tax_value";
	public static final String COLUMN_RETAILPRICE = "retail_price";

	public static final String COLUMN_PRODUCT_SLNO = "sl_no";
	public static final String COLUMN_SUB_TOTAL = "sub_total";
	//public static final String COLUMN_CURRENT_QTY = "current_qty";
	public static final String COLUMN_ORIGINAL_QTY = "original_qty";

	public static final String COLUMN_RETURN_CARTON = "return_carton";
	public static final String COLUMN_RETURN_LOOSE = "return_loose";
	public static final String COLUMN_RETURN_QTY = "return_qty";

	public static final String COLUMN_RETURN_SUBTOTAL = "return_total";
	public static final String COLUMN_RETURN_TAX = "return_tax";
	public static final String COLUMN_RETURN_NETTOTAL = "return_net_total";

	public static final String COLUMN_IMAGE_ID = "image_id";
	public static final String COLUMN_SIGNATURE_IMAGE = "signature_image";
	public static final String COLUMN_PRODUCT_IMAGE = "product_image";

	public static final String COLUMN_CARTONPRICE = "carton_price";
	public static final String COLUMN_EXCHANGEQTY = "exchange_qty";

	public static final String BARCODE_TABLE = "barcode_table";
	public static final String COLUMN_PALETTEID = "paletteId";
	public static final String COLUMN_CODE = "productcode";
	public static final String COLUMN_NAME = "productname";
	public static final String COLUMN_BARCODE = "barcode";
	public static final String COLUMN_WEIGHT = "weight";
	public static final String COLUMN_SNUM = "snum";
	public static final String COLUMN_PRODUCTID = "productId";

	public static final String COLUMN_AREACODE = "areacode";
	public static final String COLUMN_AREANAME = "areaname";
	public static final String COLUMN_BOX = "box";

	public static final String COLUMN_ITEM_REMARKS = "ItemRemarks";

	public static final String COLUMN_BARCODE_STATUS = "BarcodeStatus";

	public static final String COLUMN_SO_SLNO = "SOSlno";
	
	public static final String COLUMN_BARCODE_BOXES = "Boxes";
	// Batch

	public static final String COLUMN_BATCH_NO = "batch_no";
	public static final String COLUMN_EXPIRY_DATE = "expiry_date";
	public static final String COLUMN_MFG_DATE = "MfgDate";
	public static final String COLUMN_CON_EXPIRY_DATE ="con_exp_date";

	public static final String COLUMN_HAVE_BATCH = "have_batch";
	public static final String COLUMN_HAVE_EXPIRY = "have_expiry";

	public static final String COLUMN_AVAILABLE_CARTON = "available_carton";
	public static final String COLUMN_AVAILABLE_LOOSE = "available_loose";
	public static final String COLUMN_AVAILABLE_QTY = "available_qty";
	public static final String COLUMN_SR_SLNO = "SRSlno";
	public static final String COLUMN_REMARKS = "Remarks";

	public static final String COLUMN_CONSIGNMENT_NO ="consignment_no";
	public static final String COLUMN_CONSIGNMENT_NAME="consignment_name";
	public static final String COLUMN_DURATION="duration";
	public static final String COLUMN_UOM="uom";
	public static final String COLUMN_CUSTOMER_CODE ="cust_code";


	/* Expanse */
	public static final String COLUMN_SNO = "SlNo";
	public static final String COLUMN_ACCOUNTNO = "AccountNo";
	public static final String COLUMN_DESC = "Description";
	public static final String COLUMN_AMOUNT = "Amount";

	/* Vandriver */
	public static final String COLUMN_VAN_ID = "_id";
	public static final String COLUMN_VANCODE = "vancode";

	public static final String COLUMN_STOCK = "QtyOnHand";
	
	public static final String COLUMN_TYPE = "Type";
	public static final String COLUMN_IMAGE = "Image";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_STATUS = "status";
	
	public static final String COLUMN_COMPANYCODE = "CompanyCode";
	public static final String COLUMN_USERNAME = "UserName";
	public static final String COLUMN_CURRENTLOCATIONTRACKING = "CurrentLocationTracking";
	public static final String COLUMN_ROUTETRACKING = "RouteTracking";
	public static final String COLUMN_TRACKINGINTERVAL = "TrackingInterval";
	public static final String COLUMN_MON = "Mon";
	public static final String COLUMN_TUE = "Tue";
	public static final String COLUMN_WED = "Wed";
	public static final String COLUMN_THU = "Thu";
	public static final String COLUMN_FRI = "Fri";
	public static final String COLUMN_SAT = "Sat";
	public static final String COLUMN_SUN = "Sun";
	public static final String COLUMN_MONFROMTIME = "MonFromTime";
	public static final String COLUMN_MONTOTIME = "MonToTime";
	public static final String COLUMN_TUEFROMTIME = "TueFromTime";
	public static final String COLUMN_TUETOTIME = "TueToTime";
	public static final String COLUMN_WEDFROMTIME = "WedFromTime";
	public static final String COLUMN_WEDTOTIME = "WedToTime";
	public static final String COLUMN_THUFROMTIME = "ThuFromTime";
	public static final String COLUMN_THUTOTIME = "ThuToTime";
	public static final String COLUMN_FRIFROMTIME = "FriFromTime";
	public static final String COLUMN_FRITOTIME= "FriToTime";
	public static final String COLUMN_SATFROMTIME = "SatFromTime";
	public static final String COLUMN_SATTOTIME = "SatToTime";
	public static final String COLUMN_SUNFROMTIME = "SunFromTime";
	public static final String COLUMN_SUNTOTIME= "SunToTime";
	
	public static final String COLUMN_DV_ORIGINAL_QTY = "OriginalQty"; //Delivery Verification
	 public static final String COLUMN_DV_ORIGINAL_CQTY = "OriginalCQty";

	public static final String COLUMN_COLOR_CODE = "color_code";
	public static final String COLUMN_COLOR_NAME = "color_name";
	public static final String COLUMN_SIZE_CODE = "size_code";
	public static final String COLUMN_SIZE_NAME = "size_name";
	public static final String COLUMN_FLAG = "flag";

	public static void init(Context context) {
		if (null == instance) {
			instance = new SOTDatabase(context);
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

	public static void storeInvoiceReturn(HashMap<String, String> hashvalue) {

		ContentValues cv = new ContentValues();

		cv.put(COLUMN_PRODUCT_SLNO, hashvalue.get("slNo"));
		cv.put(COLUMN_PRODUCT_CODE, hashvalue.get("ProductCode"));
		cv.put(COLUMN_PRODUCT_NAME, hashvalue.get("ProductName"));
		cv.put(COLUMN_CARTON_QTY, hashvalue.get("CQty"));
		cv.put(COLUMN_LOOSE_QTY, hashvalue.get("LQty"));
		cv.put(COLUMN_QUANTITY, hashvalue.get("Qty"));
		cv.put(COLUMN_ORIGINAL_QTY, hashvalue.get("OriginalQty"));
		cv.put(COLUMN_RETURN_CARTON, hashvalue.get("RCQty"));
		cv.put(COLUMN_RETURN_LOOSE, hashvalue.get("RLQty"));
		cv.put(COLUMN_RETURN_QTY, hashvalue.get("RQty"));
		cv.put(COLUMN_FOC, hashvalue.get("FOCQty"));
		cv.put(COLUMN_PRODUCT_PRICE, hashvalue.get("Price"));
		cv.put(COLUMN_PIECE_PERQTY, hashvalue.get("PcsPerCarton"));
		cv.put(COLUMN_ITEM_DISCOUNT, hashvalue.get("ItemDiscount"));
		cv.put(COLUMN_TOTAL, hashvalue.get("Total"));
		cv.put(COLUMN_TAX, hashvalue.get("Tax"));
		cv.put(COLUMN_NETTOTAL, hashvalue.get("NetTotal"));
		cv.put(COLUMN_SUB_TOTAL, hashvalue.get("SubTotal"));
		cv.put(COLUMN_TAXTYPE, hashvalue.get("TaxType"));
		cv.put(COLUMN_TAXVALUE, hashvalue.get("TaxPerc"));
		cv.put(COLUMN_RETAILPRICE, hashvalue.get("RetailPrice"));
		cv.put(COLUMN_EXCHANGEQTY, hashvalue.get("ExchangeQty"));
		cv.put(COLUMN_CARTONPRICE, hashvalue.get("CartonPrice"));
		cv.put(COLUMN_ITEM_REMARKS, hashvalue.get("ItemRemarks"));
		cv.put(COLUMN_PRODUCT_UOM, "");
		cv.put(COLUMN_RETURN_TAX, hashvalue.get("ReturnTax"));
		cv.put(COLUMN_RETURN_NETTOTAL, hashvalue.get("ReturnNetTotal"));
		cv.put(COLUMN_RETURN_SUBTOTAL, hashvalue.get("ReturnSubTotal"));

		getDatabase().insert(PRODUCT_TABLE, null, cv);
	}

	public static void storeProduct(int sl_no, String prodCode,
									String prodName, double cartonQty, double looseQty, double qty, double foc,
									double price, double discount, String uom, double cartonPerQty,
									double total, double tax, String netTotal, String taxType,
									String taxValue, String retailPrice, String subTotal,
									String cprice, String exqty, String MinimumSellingPrice,
									String ItemRemarks, String SOSlno, String stock, String MinimumCartonSellingPrice,
									String haveBatch, String haveExpiry) {

		String ttl = twoDecimalPoint(total);
		Log.d("storeProduct","-->"+"storeProduct"+"sl_no :"+sl_no+"cartonPerQty:"+cartonPerQty);
		Log.d("checkValues",":"+subTotal+" "+netTotal+" "+tax+price);
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_PRODUCT_SLNO, sl_no);
		cv.put(COLUMN_PRODUCT_CODE, prodCode);
		cv.put(COLUMN_PRODUCT_NAME, prodName);
		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_QUANTITY, qty);
		cv.put(COLUMN_FOC, foc);
		cv.put(COLUMN_ITEM_DISCOUNT, discount);
		cv.put(COLUMN_PRODUCT_UOM, uom);
		cv.put(COLUMN_PIECE_PERQTY, cartonPerQty);
		cv.put(COLUMN_TOTAL, ttl);
		cv.put(COLUMN_TAX, tax);
		cv.put(COLUMN_NETTOTAL, netTotal);
		cv.put(COLUMN_SUB_TOTAL, subTotal);
		cv.put(COLUMN_TAXTYPE, taxType);
		cv.put(COLUMN_TAXVALUE, taxValue);
		if (cartonPerQty == 1){
			Log.d("column_price1",cprice+","+cartonPerQty);
			if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
				cv.put(COLUMN_RETAILPRICE, cprice);
				cv.put(COLUMN_CARTONPRICE, cprice);
				cv.put(COLUMN_PRODUCT_PRICE, cprice);
			}else{
				Log.d("priceCheck",""+price);
				cv.put(COLUMN_RETAILPRICE, price);
				cv.put(COLUMN_CARTONPRICE, price);
				cv.put(COLUMN_PRODUCT_PRICE, price);
			}
		}else if (cartonPerQty > 1){
			Log.d("column_price2",cprice+","+cartonPerQty+"price:"+price);
			if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
				cv.put(COLUMN_RETAILPRICE, cprice);
				cv.put(COLUMN_CARTONPRICE, cprice);
				cv.put(COLUMN_PRODUCT_PRICE, price);
			}else{
				cv.put(COLUMN_RETAILPRICE, price);
				cv.put(COLUMN_CARTONPRICE, price);
				cv.put(COLUMN_PRODUCT_PRICE, price);
			}
		}

		cv.put(COLUMN_EXCHANGEQTY, exqty);
		cv.put(COLUMN_MINIMUM_SELLING_PRICE, MinimumSellingPrice);
		cv.put(COLUMN_ITEM_REMARKS, ItemRemarks);
		cv.put(COLUMN_BARCODE_STATUS, 0);
		cv.put(COLUMN_SO_SLNO, SOSlno);
//		cv.put(COLUMN_CURRENT_QTY, qty);
		cv.put(COLUMN_STOCK, stock);
		cv.put(COLUMN_MINIMUM_CARTON_SELLING_PRICE, MinimumCartonSellingPrice);
		cv.put(COLUMN_HAVE_BATCH, haveBatch);
		cv.put(COLUMN_HAVE_EXPIRY, haveExpiry);
		cv.put(COLUMN_RETURN,SOSlno);
//		cv.put(COLUMN_BILL_DISCOUNT,bill_disc);
//		cv.put(COLUMN_TOTAL_DISCOUNT,total_disc);

		Log.d("Storeinsotdb",cv.toString());

		getDatabase().insert(PRODUCT_TABLE, null, cv);
	}

	public static void storeProductGra(int sl_no, String prodCode,
			String prodName, double cartonQty, double looseQty, String qty, double foc,
			double price, double discount, String uom, double cartonPerQty,
			double total, double tax, String netTotal, String taxType,
			String taxValue, String retailPrice, String subTotal,
			String cprice, String exqty, String MinimumSellingPrice,
			String ItemRemarks, String SOSlno) {

		String ttl = twoDecimalPoint(total);

		ContentValues cv = new ContentValues();

		cv.put(COLUMN_PRODUCT_SLNO, sl_no);
		cv.put(COLUMN_PRODUCT_CODE, prodCode);
		cv.put(COLUMN_PRODUCT_NAME, prodName);
		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_QUANTITY, qty);
		cv.put(COLUMN_FOC, foc);
		cv.put(COLUMN_PRODUCT_PRICE, price);
		cv.put(COLUMN_ITEM_DISCOUNT, discount);
		cv.put(COLUMN_PRODUCT_UOM, uom);
		cv.put(COLUMN_PIECE_PERQTY, cartonPerQty);
		cv.put(COLUMN_TOTAL, ttl);
		cv.put(COLUMN_TAX, tax);
		cv.put(COLUMN_NETTOTAL, netTotal);
		cv.put(COLUMN_SUB_TOTAL, subTotal);
		cv.put(COLUMN_TAXTYPE, taxType);
		cv.put(COLUMN_TAXVALUE, taxValue);
		cv.put(COLUMN_RETAILPRICE, retailPrice);
		cv.put(COLUMN_EXCHANGEQTY, exqty);
		cv.put(COLUMN_CARTONPRICE, cprice);
		cv.put(COLUMN_MINIMUM_SELLING_PRICE, MinimumSellingPrice);
		cv.put(COLUMN_ITEM_REMARKS, ItemRemarks);
		cv.put(COLUMN_BARCODE_STATUS, 0);
		cv.put(COLUMN_SO_SLNO, SOSlno);

		getDatabase().insert(PRODUCT_TABLE, null, cv);
	}

	public static void storeProductForTransfer(int sl_no, String prodCode,
			String prodName, int cartonQty, int looseQty, int qty,
			double price, int cartonPerQty) {

		ContentValues cv = new ContentValues();

		cv.put(COLUMN_PRODUCT_SLNO, sl_no);
		cv.put(COLUMN_PRODUCT_CODE, prodCode);
		cv.put(COLUMN_PRODUCT_NAME, prodName);
		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_QUANTITY, qty);
		cv.put(COLUMN_PRODUCT_PRICE, price);
		cv.put(COLUMN_PIECE_PERQTY, cartonPerQty);
		cv.put(COLUMN_PRODUCT_UOM, "");
		cv.put(COLUMN_TOTAL, "");
		cv.put(COLUMN_TAX, "");
		cv.put(COLUMN_NETTOTAL, "");
		cv.put(COLUMN_SUB_TOTAL, "");
		cv.put(COLUMN_TAXTYPE, "");
		cv.put(COLUMN_TAXVALUE, "");
		cv.put(COLUMN_SO_SLNO, sl_no);
		// cv.put(COLUMN_HAVE_BATCH, haveBatch);
		// cv.put(COLUMN_HAVE_EXPIRY, haveExpiry);

		getDatabase().insert(PRODUCT_TABLE, null, cv);
	}

	public static void storeProductForEditInvoice(int sl_no, String prodCode,
			String prodName, int cartonQty, int looseQty, double qty, int foc,
			double price, double discount, String uom, int cartonPerQty,
			double total, double tax, String netTotal, String taxType,
			String taxValue, String retailPrice, String subTotal,
			String cprice, String exqty, String ItemRemarks, String stock) {

		Log.d("checkValues",":"+subTotal+" "+netTotal+" "+tax);

		String ttl = twoDecimalPoint(total);

		ContentValues cv = new ContentValues();

		cv.put(COLUMN_PRODUCT_SLNO, sl_no);
		cv.put(COLUMN_PRODUCT_CODE, prodCode);
		cv.put(COLUMN_PRODUCT_NAME, prodName);
		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_QUANTITY, qty);
		cv.put(COLUMN_FOC, foc);
		cv.put(COLUMN_PRODUCT_PRICE, price);
		cv.put(COLUMN_ITEM_DISCOUNT, discount);
		cv.put(COLUMN_PRODUCT_UOM, uom);
		cv.put(COLUMN_PIECE_PERQTY, cartonPerQty);
		cv.put(COLUMN_TOTAL, ttl);
		cv.put(COLUMN_TAX, tax);
		cv.put(COLUMN_NETTOTAL, netTotal);
		cv.put(COLUMN_SUB_TOTAL, subTotal);
		cv.put(COLUMN_TAXTYPE, taxType);
		cv.put(COLUMN_TAXVALUE, taxValue);
		cv.put(COLUMN_RETAILPRICE, retailPrice);
		cv.put(COLUMN_EXCHANGEQTY, exqty);
		cv.put(COLUMN_CARTONPRICE, cprice);
		cv.put(COLUMN_BARCODE_STATUS, 0);
		cv.put(COLUMN_ITEM_REMARKS, ItemRemarks);
		cv.put(COLUMN_STOCK, stock);
		
		getDatabase().insert(PRODUCT_TABLE, null, cv);
	}

	public static void storeImage(int id, String signature_image,
			String product_image) {

		ContentValues cv = new ContentValues();

		cv.put(COLUMN_IMAGE_ID, id);
		cv.put(COLUMN_SIGNATURE_IMAGE, signature_image);
		cv.put(COLUMN_PRODUCT_IMAGE, product_image);

		getDatabase().insert(IMAGE_TABLE, null, cv);
	}

	//consignmentStock
	public static void storeConsignmentStock(HashMap<String, String> batchValues){
		Log.d("StoreConsignment", "Consignment"+batchValues.get("SlNo"));
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PRODUCT_SLNO, batchValues.get("SlNo"));
		cv.put(COLUMN_PRODUCT_CODE, batchValues.get("productCode"));
		cv.put(COLUMN_PRODUCT_NAME, batchValues.get("productName"));
		cv.put(COLUMN_BATCH_NO, batchValues.get("BatchNo"));
		cv.put(COLUMN_EXPIRY_DATE, batchValues.get("exp_date"));
		cv.put(COLUMN_MFG_DATE, batchValues.get("mfgDate"));
		cv.put(COLUMN_AVAILABLE_CARTON, batchValues.get("AvailCQty"));
		cv.put(COLUMN_AVAILABLE_LOOSE, batchValues.get("AvailLQty"));
		cv.put(COLUMN_AVAILABLE_QTY, batchValues.get("AvailQty"));
		cv.put(COLUMN_CARTON_QTY, batchValues.get("cQty"));
		cv.put(COLUMN_LOOSE_QTY, batchValues.get("lqty"));
		cv.put(COLUMN_QUANTITY, batchValues.get("qty"));
		cv.put(COLUMN_CUSTOMER_CODE, batchValues.get("cust_code"));
		cv.put(COLUMN_CONSIGNMENT_NO, batchValues.get("con_no"));
		cv.put(COLUMN_CONSIGNMENT_NAME,batchValues.get("con_name"));
		cv.put(COLUMN_UOM, batchValues.get("uom"));
		cv.put(COLUMN_DURATION, batchValues.get("duration"));
		cv.put(COLUMN_PIECE_PERQTY, batchValues.get("pcsPerCarton"));
		cv.put(COLUMN_CON_EXPIRY_DATE, batchValues.get("con_expiry"));
		cv.put(COLUMN_SR_SLNO, batchValues.get("SR_Slno"));
			Log.d("COLUMN_AVAILABLE_values","-->"+batchValues.get("AvailCQty")+" "+batchValues.get("AvailLQty")
			+batchValues.get("AvailQty")+batchValues.get("cQty")+batchValues.get("lqty")+batchValues.get("qty"));


		getDatabase().insert(CONSIGNMENT_STOCK_TABLE, null, cv);
	}

	// Batch
	public static void storeBatch(HashMap<String, String> batchValues) {

		Log.d("Store", "Batch");
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PRODUCT_SLNO, batchValues.get("slNo"));
		cv.put(COLUMN_PRODUCT_CODE, batchValues.get("ProductCode"));
		cv.put(COLUMN_PRODUCT_NAME, batchValues.get("ProductName"));
		cv.put(COLUMN_BATCH_NO, batchValues.get("BatchNo"));
		cv.put(COLUMN_EXPIRY_DATE, batchValues.get("ExpiryDate"));
		cv.put(COLUMN_MFG_DATE, batchValues.get("MfgDate"));
		cv.put(COLUMN_AVAILABLE_CARTON, batchValues.get("AvailCQty"));
		cv.put(COLUMN_AVAILABLE_LOOSE, batchValues.get("AvailLQty"));
		cv.put(COLUMN_AVAILABLE_QTY, batchValues.get("AvailQty"));
		cv.put(COLUMN_CARTON_QTY, batchValues.get("CQty"));
		cv.put(COLUMN_LOOSE_QTY, batchValues.get("LQty"));
		cv.put(COLUMN_QUANTITY, batchValues.get("Qty"));
		cv.put(COLUMN_FOC, batchValues.get("FOCQty"));
		cv.put(COLUMN_PIECE_PERQTY, batchValues.get("PcsPerCarton"));
		cv.put(COLUMN_HAVE_BATCH, batchValues.get("HaveBatch"));
		cv.put(COLUMN_HAVE_EXPIRY, batchValues.get("HaveExpiry"));
		cv.put(COLUMN_PRODUCT_PRICE, batchValues.get("Price"));
		cv.put(COLUMN_SR_SLNO, batchValues.get("SR_Slno"));
		cv.put(COLUMN_REMARKS, batchValues.get("Remarks"));

		getDatabase().insert(BATCH_TABLE, null, cv);
	}

	//MANUAL_STOCK-TABLE

	public static void storeManualStock(String slno, String slno1, String productCode, String productName, String cQty, String lQty, String pcs, String qty,String stock) {

		Log.d("ReturnQty",productCode+"..."+productName);
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_PRODUCT_CODE,productCode);
		cv.put(COLUMN_PRODUCT_NAME,productName);
		cv.put(COLUMN_CARTON_QTY,cQty);
		cv.put(COLUMN_LOOSE_QTY,lQty);
		cv.put(COLUMN_PIECE_PERQTY,pcs);
		cv.put(COLUMN_QUANTITY,qty);
		cv.put(COLUMN_STOCK,stock);
		Log.d("Storeinsotdb",cv.toString());

		getDatabase().insert(MANUAL_STOCK_TABLE, null, cv);
	}



	public static  void storeCloseStock(String slno,String prod_code,String pro_name,int cQty,
										String lqty,String pcs,String price,String quantity){

		Log.d("detailsCheking","-->"+slno+"code:"+prod_code+"name:"+pro_name+"qty:"+quantity);

		ContentValues cv = new ContentValues();

		cv.put(COLUMN_PRODUCT_SLNO,slno);
		cv.put(COLUMN_PRODUCT_CODE,prod_code);
		cv.put(COLUMN_PRODUCT_NAME,pro_name);
		cv.put(COLUMN_QUANTITY,quantity);
		cv.put(COLUMN_CARTON_QTY,cQty);
		cv.put(COLUMN_LOOSE_QTY,lqty);
		cv.put(COLUMN_PIECE_PERQTY,pcs);
		cv.put(COLUMN_PRODUCT_PRICE,price);

		Log.d("Storeinsotdb",cv.toString());

		getDatabase().insert(CLOSE_STOCk_TABLE, null, cv);

	}

	public static void storeDenomination(String slno, String productCode, String productName, String Qty) {

		Log.d("CurrenycodeCheck",productCode+"..."+productName+"slno"+slno);
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_ID,slno);
		cv.put(COLUMN_PRODUCT_SLNO,slno);
		cv.put(COLUMN_PRODUCT_CODE,productCode);
		cv.put(COLUMN_PRODUCT_NAME,productName);
		cv.put(COLUMN_QUANTITY,Qty);
		Log.d("Storeinsotdb",cv.toString());

		getDatabase().insert(DENOMINATION_TABLE, null, cv);
	}


	public static int updateDenomination(String slno, String currncy,
										 String denomination,String total) {

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PRODUCT_SLNO, slno);
		cv.put(COLUMN_PRODUCT_CODE, currncy);
		cv.put(COLUMN_PRODUCT_NAME, denomination);
		cv.put(COLUMN_QUANTITY, total);
		Log.d("updateinstdb",cv.toString());

		return getDatabase().update(DENOMINATION_TABLE, cv,
				"sl_no=" + "'" + slno + "'", null);
	}

	public static void updateInvoiceReturn(HashMap<String, String> values) {

		ContentValues cv = new ContentValues();

		String ss_id = values.get("ss_slid");
		Log.d("db..ss_slid", ss_id);

		cv.put(COLUMN_PRODUCT_SLNO, values.get("slNo"));
		cv.put(COLUMN_PRODUCT_CODE, values.get("ProductCode"));
		cv.put(COLUMN_PRODUCT_NAME, values.get("ProductName"));
		cv.put(COLUMN_CARTON_QTY, values.get("CQty"));
		cv.put(COLUMN_LOOSE_QTY, values.get("LQty"));
		cv.put(COLUMN_QUANTITY, values.get("Qty"));
		cv.put(COLUMN_RETURN_CARTON, values.get("ReturnCQty"));
		cv.put(COLUMN_RETURN_LOOSE, values.get("ReturnLQty"));
		cv.put(COLUMN_RETURN_QTY, values.get("ReturnQty"));
		cv.put(COLUMN_PIECE_PERQTY, values.get("PcsPerCarton"));
		cv.put(COLUMN_PRODUCT_PRICE, values.get("Price"));
		cv.put(COLUMN_CARTONPRICE, values.get("CPrice"));
		cv.put(COLUMN_TOTAL, values.get("Total"));
		cv.put(COLUMN_SUB_TOTAL, values.get("SubTotal"));
		cv.put(COLUMN_TAX, values.get("Tax"));
		cv.put(COLUMN_NETTOTAL, values.get("NetTotal"));

		cv.put(COLUMN_RETURN_SUBTOTAL, values.get("ReturnSubTotal"));
		cv.put(COLUMN_RETURN_TAX, values.get("ReturnTax"));
		cv.put(COLUMN_RETURN_NETTOTAL, values.get("ReturnNetTotal"));
		cv.put(COLUMN_TAXTYPE, values.get("TaxType"));
		cv.put(COLUMN_TAXVALUE, values.get("TaxPerc"));

		getDatabase().update(PRODUCT_TABLE, cv, "_id='" + ss_id + "'", null);
	}

	public static int updateBatch(HashMap<String, String> batchValues) {

		String bat_id = batchValues.get("id");
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_BATCH_NO, batchValues.get("BatchNo"));
		cv.put(COLUMN_EXPIRY_DATE, batchValues.get("ExpiryDate"));
		cv.put(COLUMN_CARTON_QTY, batchValues.get("CQty"));
		cv.put(COLUMN_LOOSE_QTY, batchValues.get("LQty"));
		cv.put(COLUMN_QUANTITY, batchValues.get("Qty"));
		cv.put(COLUMN_FOC, batchValues.get("FOCQty"));
		cv.put(COLUMN_REMARKS, batchValues.get("Remarks"));

		return getDatabase().update(BATCH_TABLE, cv, "_id='" + bat_id + "'",
				null);
	}

	public static int updateproductbatch(String productcode, String have_batch,
			String have_expiry) {

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_HAVE_BATCH, have_batch);
		cv.put(COLUMN_HAVE_EXPIRY, have_expiry);

		return getDatabase().update(PRODUCT_TABLE, cv,
				"product_code=" + "'" + productcode + "'", null);
	}

	public static int updateBatchSR_slno(String oldsno, String newsno) {

		ContentValues values = new ContentValues();
		values.put("SRSlno", newsno);
		return getDatabase().update("batch_table", values, "SRSlno" + " = ?",
				new String[] { oldsno });
	}

	public static int updateConsignmentSR_slno(String oldsno, String newsno) {

		ContentValues values = new ContentValues();
		values.put("SRSlno", newsno);
		return getDatabase().update("consignment_table", values, "SRSlno" + " = ?",
				new String[] { oldsno });
	}

	public static int updateImage(int id, String signature_image,
			String product_image) {

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SIGNATURE_IMAGE, signature_image);
		cv.put(COLUMN_PRODUCT_IMAGE, product_image);

		return getDatabase().update(IMAGE_TABLE, cv,
				"image_id=" + "'" + "1" + "'", null);
	}

	public static int updateConsignmentStock(HashMap<String, String> batchValues) {

		String bat_id = batchValues.get("id");
		String sl_no =batchValues.get("slNo");
		String con_no =batchValues.get("BatchNo");
		String productCode = batchValues.get("product");
		Log.d("con_no","-->"+con_no+"productCode :"+productCode);

		ContentValues cv = new ContentValues();

			cv.put(COLUMN_CONSIGNMENT_NO, batchValues.get("BatchNo"));
			cv.put(COLUMN_PRODUCT_CODE,batchValues.get("product"));
			cv.put(COLUMN_EXPIRY_DATE, batchValues.get("ExpiryDate"));
			cv.put(COLUMN_CARTON_QTY, batchValues.get("CQty"));
			cv.put(COLUMN_LOOSE_QTY, batchValues.get("LQty"));
			cv.put(COLUMN_QUANTITY, batchValues.get("Qty"));

		Log.d("AvailableUpdate","-->"+bat_id +"cv"+cv);

		return getDatabase().update(CONSIGNMENT_STOCK_TABLE, cv, "consignment_no=" + "'" + con_no + "' AND product_code = '" + productCode
						+ "'",
				null);
	}

	// public static int updateproduct(String produtcode,String havebatch,String
	// haveexpiry) {
	//
	// ContentValues cv = new ContentValues();
	// cv.put(COLUMN_HAVE_BATCH, havebatch);
	// cv.put(COLUMN_HAVE_EXPIRY, haveexpiry);
	//
	// return getDatabase().update(PRODUCT_TABLE, cv,
	// "product_code=" + "'" + produtcode + "'", null);
	// }

	public static void storeBillDisc(int sl_no, String prodCode, String subTot) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_PRODUCT_SLNO, sl_no);
		cv.put(COLUMN_PRODUCT_CODE, prodCode);
		cv.put(COLUMN_SUB_TOTAL, subTot);

		getDatabase().insert(BILLDISC_TABLE, null, cv);
	}

	public static Cursor getImageCursor() {

		String[] columns = new String[] { COLUMN_IMAGE_ID,
				COLUMN_SIGNATURE_IMAGE, COLUMN_PRODUCT_IMAGE };
		return getDatabase().query(IMAGE_TABLE, columns, null, null, null,
				null, null);
	}

	public static Cursor getCursor() {

		String[] columns = new String[] { COLUMN_PRODUCT_SLNO,
				COLUMN_PRODUCT_ID, COLUMN_PRODUCT_CODE, COLUMN_PRODUCT_NAME,
				COLUMN_CARTON_QTY, COLUMN_LOOSE_QTY, COLUMN_QUANTITY,
				COLUMN_ORIGINAL_QTY, COLUMN_RETURN_CARTON, COLUMN_RETURN_LOOSE,
				COLUMN_RETURN_QTY, COLUMN_FOC, COLUMN_PRODUCT_PRICE,
				COLUMN_ITEM_DISCOUNT, COLUMN_PRODUCT_UOM, COLUMN_PIECE_PERQTY,
				COLUMN_TOTAL, COLUMN_RETURN_SUBTOTAL, COLUMN_RETURN_TAX,
				COLUMN_RETURN_NETTOTAL, COLUMN_TAX, COLUMN_NETTOTAL,
				COLUMN_TAXTYPE, COLUMN_TAXVALUE, COLUMN_RETAILPRICE,
				COLUMN_SUB_TOTAL, COLUMN_EXCHANGEQTY, COLUMN_CARTONPRICE,
				COLUMN_MINIMUM_SELLING_PRICE,COLUMN_MINIMUM_CARTON_SELLING_PRICE, COLUMN_ITEM_REMARKS,
				COLUMN_BARCODE_STATUS, COLUMN_SO_SLNO, COLUMN_HAVE_BATCH,
				COLUMN_DV_ORIGINAL_CQTY, COLUMN_DV_ORIGINAL_QTY,
				COLUMN_HAVE_EXPIRY , COLUMN_STOCK};
		return getDatabase().query(PRODUCT_TABLE, columns, null, null, null,
				null, null);
	}
	
	public static Cursor getCursorForProductCode(String productcode) {

		  String[] columns = new String[] { COLUMN_PRODUCT_SLNO,
		    COLUMN_PRODUCT_ID, COLUMN_PRODUCT_CODE, COLUMN_PRODUCT_NAME,
		    COLUMN_CARTON_QTY, COLUMN_LOOSE_QTY, COLUMN_QUANTITY,
		    COLUMN_ORIGINAL_QTY, COLUMN_RETURN_CARTON, COLUMN_RETURN_LOOSE,
		    COLUMN_RETURN_QTY, COLUMN_FOC, COLUMN_PRODUCT_PRICE,
		    COLUMN_ITEM_DISCOUNT, COLUMN_PRODUCT_UOM, COLUMN_PIECE_PERQTY,
		    COLUMN_TOTAL, COLUMN_RETURN_SUBTOTAL, COLUMN_RETURN_TAX,
		    COLUMN_RETURN_NETTOTAL, COLUMN_TAX, COLUMN_NETTOTAL,
		    COLUMN_TAXTYPE, COLUMN_TAXVALUE, COLUMN_RETAILPRICE,
		    COLUMN_SUB_TOTAL, COLUMN_EXCHANGEQTY, COLUMN_CARTONPRICE,
		    COLUMN_MINIMUM_SELLING_PRICE,COLUMN_MINIMUM_CARTON_SELLING_PRICE, COLUMN_ITEM_REMARKS,
		    COLUMN_BARCODE_STATUS, COLUMN_SO_SLNO, COLUMN_HAVE_BATCH,
		    COLUMN_DV_ORIGINAL_CQTY, COLUMN_DV_ORIGINAL_QTY,
		    COLUMN_HAVE_EXPIRY , COLUMN_STOCK };
		  return getDatabase().query(PRODUCT_TABLE, columns, "product_code= '" + productcode + "' LIMIT 1", null, null, null, null);
		 }

	// Batch
	public static Cursor getBatCursor() {

		String[] columns = new String[] { COLUMN_PRODUCT_SLNO,
				COLUMN_PRODUCT_ID, COLUMN_PRODUCT_CODE, COLUMN_PRODUCT_NAME,
				COLUMN_BATCH_NO, COLUMN_EXPIRY_DATE, COLUMN_CARTON_QTY,
				COLUMN_LOOSE_QTY, COLUMN_QUANTITY, COLUMN_FOC,
				COLUMN_PIECE_PERQTY, COLUMN_HAVE_BATCH, COLUMN_HAVE_EXPIRY,
				COLUMN_PRODUCT_PRICE, COLUMN_AVAILABLE_CARTON,
				COLUMN_AVAILABLE_LOOSE, COLUMN_AVAILABLE_QTY, COLUMN_SR_SLNO,
				COLUMN_REMARKS, COLUMN_MFG_DATE };
		return getDatabase().query(BATCH_TABLE, columns, "have_batch= 'True'",
				null, null, null, null);
	}

	public static Cursor getBatchCursor(String productcode) {

		String[] columns = new String[] { COLUMN_PRODUCT_SLNO,
				COLUMN_PRODUCT_ID, COLUMN_PRODUCT_CODE, COLUMN_PRODUCT_NAME,
				COLUMN_BATCH_NO, COLUMN_EXPIRY_DATE, COLUMN_CARTON_QTY,
				COLUMN_LOOSE_QTY, COLUMN_QUANTITY, COLUMN_FOC,
				COLUMN_PIECE_PERQTY, COLUMN_HAVE_BATCH, COLUMN_HAVE_EXPIRY,
				COLUMN_PRODUCT_PRICE, COLUMN_AVAILABLE_CARTON,
				COLUMN_AVAILABLE_LOOSE, COLUMN_AVAILABLE_QTY, COLUMN_SR_SLNO,
				COLUMN_REMARKS, COLUMN_MFG_DATE  };
		return getDatabase().query(BATCH_TABLE, columns,
				"product_code= '" + productcode + "'", null, null, null, null);
	}

	public static Cursor getBatchCursorWithSR(String productcode, String SRSlno) {

		String[] columns = new String[] { COLUMN_PRODUCT_SLNO,
				COLUMN_PRODUCT_ID, COLUMN_PRODUCT_CODE, COLUMN_PRODUCT_NAME,
				COLUMN_BATCH_NO, COLUMN_EXPIRY_DATE, COLUMN_CARTON_QTY,
				COLUMN_LOOSE_QTY, COLUMN_QUANTITY, COLUMN_FOC,
				COLUMN_PIECE_PERQTY, COLUMN_HAVE_BATCH, COLUMN_HAVE_EXPIRY,
				COLUMN_PRODUCT_PRICE, COLUMN_AVAILABLE_CARTON,
				COLUMN_AVAILABLE_LOOSE, COLUMN_AVAILABLE_QTY, COLUMN_SR_SLNO,
				COLUMN_REMARKS, COLUMN_MFG_DATE };
		return getDatabase().query(
				BATCH_TABLE,
				columns,
				"product_code= '" + productcode + "' AND SRSlno= '" + SRSlno
						+ "'", null, null, null, null);
	}

	public static Cursor getConsignmentStock(String prod_code,String SRSlno){
		Log.d("dataShown","-->"+SRSlno+" "+prod_code.trim());
		String[] columns = new String[] { COLUMN_PRODUCT_ID, COLUMN_PRODUCT_SLNO,
				COLUMN_PRODUCT_CODE, COLUMN_PRODUCT_NAME,
				COLUMN_BATCH_NO, COLUMN_MFG_DATE, COLUMN_EXPIRY_DATE, COLUMN_AVAILABLE_CARTON,
				COLUMN_AVAILABLE_LOOSE, COLUMN_AVAILABLE_QTY, COLUMN_CARTON_QTY,
				COLUMN_LOOSE_QTY, COLUMN_QUANTITY, COLUMN_CUSTOMER_CODE,
				COLUMN_PIECE_PERQTY, COLUMN_CONSIGNMENT_NO, COLUMN_CONSIGNMENT_NAME,
				COLUMN_CON_EXPIRY_DATE, COLUMN_DURATION, COLUMN_UOM,COLUMN_SR_SLNO };
		return getDatabase().query(CONSIGNMENT_STOCK_TABLE, columns,
				"product_code= '" + prod_code.trim() + "' AND SRSlno= '" + SRSlno
						+ "'", null, null, null, null);
	}


	public static Cursor getConsignmentStock(){

		String[] columns = new String[] { COLUMN_PRODUCT_ID, COLUMN_PRODUCT_SLNO,
				COLUMN_PRODUCT_CODE, COLUMN_PRODUCT_NAME,
				COLUMN_BATCH_NO, COLUMN_MFG_DATE, COLUMN_EXPIRY_DATE, COLUMN_AVAILABLE_CARTON,
				COLUMN_AVAILABLE_LOOSE, COLUMN_AVAILABLE_QTY, COLUMN_CARTON_QTY,
				COLUMN_LOOSE_QTY, COLUMN_QUANTITY, COLUMN_CUSTOMER_CODE,
				COLUMN_PIECE_PERQTY, COLUMN_CONSIGNMENT_NO, COLUMN_CONSIGNMENT_NAME,
				COLUMN_CON_EXPIRY_DATE, COLUMN_DURATION, COLUMN_UOM,COLUMN_SR_SLNO };
		return getDatabase().query(CONSIGNMENT_STOCK_TABLE, columns, null, null, null,
				null, null);
	}
	public static Cursor getConsignmentStockList(String prod_code,String dono){
		Log.d("prod_codeCheck","-->"+prod_code+"-->"+dono);
		String[] columns = new String[] { COLUMN_PRODUCT_ID, COLUMN_PRODUCT_SLNO,
				COLUMN_PRODUCT_CODE, COLUMN_PRODUCT_NAME,
				COLUMN_BATCH_NO, COLUMN_MFG_DATE, COLUMN_EXPIRY_DATE, COLUMN_AVAILABLE_CARTON,
				COLUMN_AVAILABLE_LOOSE, COLUMN_AVAILABLE_QTY, COLUMN_CARTON_QTY,
				COLUMN_LOOSE_QTY, COLUMN_QUANTITY, COLUMN_CUSTOMER_CODE,
				COLUMN_PIECE_PERQTY, COLUMN_CONSIGNMENT_NO, COLUMN_CONSIGNMENT_NAME,
				COLUMN_CON_EXPIRY_DATE, COLUMN_DURATION, COLUMN_UOM ,COLUMN_SR_SLNO};
		return getDatabase().query(CONSIGNMENT_STOCK_TABLE, columns,
				"product_code= '" + prod_code+ "' AND consignment_no= '" + dono
						+ "'", null, null, null, null);
	}




	public static Cursor getConsignmentStocks(String prod_code){
		Log.d("prod_code","-->"+prod_code);
		String[] columns = new String[] { COLUMN_PRODUCT_ID, COLUMN_PRODUCT_SLNO,
				COLUMN_PRODUCT_CODE, COLUMN_PRODUCT_NAME,
				COLUMN_BATCH_NO, COLUMN_MFG_DATE, COLUMN_EXPIRY_DATE, COLUMN_AVAILABLE_CARTON,
				COLUMN_AVAILABLE_LOOSE, COLUMN_AVAILABLE_QTY, COLUMN_CARTON_QTY,
				COLUMN_LOOSE_QTY, COLUMN_QUANTITY, COLUMN_CUSTOMER_CODE,
				COLUMN_PIECE_PERQTY, COLUMN_CONSIGNMENT_NO, COLUMN_CONSIGNMENT_NAME,
				COLUMN_CON_EXPIRY_DATE, COLUMN_DURATION, COLUMN_UOM ,COLUMN_SR_SLNO};
		return getDatabase().query(CONSIGNMENT_STOCK_TABLE, columns,
				"product_code= '" + prod_code + "'", null, null, null, null);
	}

	public static Cursor getDenominationCursors(String curcode) {
		Log.d("curcode","-->"+curcode);

		String[] columns = new String[] {
				COLUMN_ID, COLUMN_PRODUCT_CODE,COLUMN_PRODUCT_NAME,COLUMN_CARTON_QTY,COLUMN_LOOSE_QTY,COLUMN_PIECE_PERQTY,COLUMN_STOCK,COLUMN_QUANTITY};
		return getDatabase().query(MANUAL_STOCK_TABLE, columns, "product_code= '" + curcode + "'",null, null,
				null, null);
	}

	public static Cursor getManualStockCursor() {

		String[] columns = new String[] {
				COLUMN_ID, COLUMN_PRODUCT_CODE,COLUMN_PRODUCT_NAME,COLUMN_CARTON_QTY,COLUMN_LOOSE_QTY,COLUMN_PIECE_PERQTY,COLUMN_STOCK,COLUMN_QUANTITY};
		return getDatabase().query(MANUAL_STOCK_TABLE, columns, null, null, null,
				null, null);
	}


	public static Cursor getManualStockCursors(String prodCode) {

		Log.d("prodcodeSlno",""+prodCode);

		String[] columns = new String[] {COLUMN_ID,
				COLUMN_PRODUCT_CODE,COLUMN_PRODUCT_NAME,COLUMN_CARTON_QTY,COLUMN_LOOSE_QTY,COLUMN_PIECE_PERQTY,COLUMN_STOCK,COLUMN_QUANTITY};
		return getDatabase().query(MANUAL_STOCK_TABLE, columns,
				"product_code= '" + prodCode + "'", null, null, null, null);
	}

	public static Cursor getCloseStockCursor() {

		String[] columns = new String[] {
				COLUMN_PRODUCT_SLNO, COLUMN_PRODUCT_CODE,COLUMN_PRODUCT_NAME,COLUMN_CARTON_QTY,COLUMN_LOOSE_QTY,COLUMN_PIECE_PERQTY,COLUMN_PRODUCT_PRICE,COLUMN_QUANTITY};
		return getDatabase().query(CLOSE_STOCk_TABLE, columns, null, null, null,
				null, null);
	}

	public static Cursor getDenominationCursor() {

		String[] columns = new String[] {
				COLUMN_ID,COLUMN_PRODUCT_SLNO, COLUMN_PRODUCT_CODE,COLUMN_PRODUCT_NAME,COLUMN_QUANTITY};
		return getDatabase().query(DENOMINATION_TABLE, columns, null, null, null,
				null, null);
	}


	public static Cursor getBillCursor() {

		String[] columns = new String[] { COLUMN_PRODUCT_ID,
				COLUMN_PRODUCT_SLNO, COLUMN_SUB_TOTAL };
		return getDatabase().query(BILLDISC_TABLE, columns, null, null, null,
				null, null);
	}

	public static double getsubTotal(int slno) {
		double sbT = 0;
		String Query = "SELECT sub_total FROM bill_disc where sl_no= " + slno;
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				sbT = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return sbT;
	}

	public static double getTotal() {
		double total = 0;
		String Query = "SELECT sum(total) FROM sales_product";
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

	public static double getTotalAmt() {
		double total = 0;
		String Query = "SELECT sum(quantity) FROM denomination_table";
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

	public static double getTotalItemDisc() {
		double totalItemDisc = 0.00;
		String Query = "SELECT sum(item_discount) FROM sales_product";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalItemDisc = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalItemDisc;
	}

	public static String getSignatureImage() {
		String image = "";
		String Query = "SELECT signature_image FROM image_table";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				image = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return image;
	}

	public static double getReturnSubTotal() {
		double total = 0;
		String Query = "SELECT sum(return_total) FROM sales_product";
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

	public static double getReturnTax() {
		double total = 0;
		String Query = "SELECT sum(return_tax) FROM sales_product";
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

	public static double getReturnNetTotal() {
		double total = 0;
		String Query = "SELECT sum(return_net_total) FROM sales_product";
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

	public static String getProductImage() {
		String image = "";
		String Query = "SELECT product_image FROM image_table";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				image = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return image;
	}

	public static double getsumsubTot() {
		double total = 0;
		String Query = "SELECT SUM(sub_total) FROM bill_disc";
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

	public static double updateSubTotal(double subTot, int sl_no) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SUB_TOTAL, subTot);
		return getDatabase().update(PRODUCT_TABLE, cv,
				"sl_no=" + "'" + sl_no + "'", null);
	}

	public static double updateSummary(double tax, double subTot,
			double net_tot, int sl_no) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_TAX, tax);
		cv.put(COLUMN_SUB_TOTAL, subTot);
		cv.put(COLUMN_NETTOTAL, net_tot);
		return getDatabase().update(PRODUCT_TABLE, cv,
				"sl_no=" + "'" + sl_no + "'", null);
	}

	public static int updateProduct(String prodcode, String prodname,
			double cartonQty, double looseQty, double qty, double foc, double price,
			double discount, String uom, double cartonPerQty, double total,
			double tax, String subTot, String net_tot, int sl_no,
			String cprice, String exqty) {
		Log.d("updateProduct","-->"+"updateProduct"+sl_no);
		Log.d("CARTON_QTY","-->"+cartonQty);
		Log.d("LOOSE_QTY","-->"+looseQty);
		Log.d("COLUMN_QUANTITY","-->"+qty);
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PRODUCT_CODE, prodcode);
		cv.put(COLUMN_PRODUCT_NAME, prodname);
		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_QUANTITY, qty);
		cv.put(COLUMN_FOC, foc);
		if (cartonPerQty == 1){
			Log.d("column_price1",cprice+","+cartonPerQty);
			if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
				cv.put(COLUMN_RETAILPRICE, cprice);
				cv.put(COLUMN_CARTONPRICE, cprice);
				cv.put(COLUMN_PRODUCT_PRICE, cprice);
			}else{
				Log.d("updatePrice","--?"+price);
				cv.put(COLUMN_RETAILPRICE, price);
				cv.put(COLUMN_CARTONPRICE, price);
				cv.put(COLUMN_PRODUCT_PRICE, price);
			}
		}else if (cartonPerQty > 1){
			Log.d("column_price2",cprice+","+cartonPerQty+"price:"+price);
			if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
				cv.put(COLUMN_RETAILPRICE, cprice);
				cv.put(COLUMN_CARTONPRICE, cprice);
				cv.put(COLUMN_PRODUCT_PRICE, price);
			}else{
				cv.put(COLUMN_RETAILPRICE, price);
				cv.put(COLUMN_CARTONPRICE, price);
				cv.put(COLUMN_PRODUCT_PRICE, price);
			}
		}
		cv.put(COLUMN_ITEM_DISCOUNT, discount);
		cv.put(COLUMN_PRODUCT_UOM, uom);
		cv.put(COLUMN_PIECE_PERQTY, cartonPerQty);
		cv.put(COLUMN_TOTAL, total);
		cv.put(COLUMN_TAX, tax);
		cv.put(COLUMN_SUB_TOTAL, subTot);
		cv.put(COLUMN_NETTOTAL, net_tot);
		cv.put(COLUMN_EXCHANGEQTY, exqty);

		return getDatabase().update(PRODUCT_TABLE, cv,
				"_id=" + "'" + sl_no + "'", null);
	}

	public static int updateProductForInvoice(String prodcode, String prodname,
			double cartonQty, double looseQty, double qty, double foc, double price,
			double discount, String uom, double cartonPerQty, double total,
			double tax, String subTot, String net_tot, int sl_no,
			String cprice, String exqty,String returnQty) {
		Log.d("prodcodeCheck","-->"+prodcode);
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PRODUCT_CODE, prodcode);
		cv.put(COLUMN_PRODUCT_NAME, prodname);
		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_QUANTITY, qty);
		cv.put(COLUMN_FOC, foc);
		cv.put(COLUMN_ITEM_DISCOUNT, discount);
		cv.put(COLUMN_PRODUCT_UOM, uom);
		cv.put(COLUMN_PIECE_PERQTY, cartonPerQty);
		if (cartonPerQty == 1){
			Log.d("column_price1",cprice+","+cartonPerQty);
			if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
				cv.put(COLUMN_RETAILPRICE, cprice);
				cv.put(COLUMN_CARTONPRICE, cprice);
				cv.put(COLUMN_PRODUCT_PRICE, cprice);
			}else{
				Log.d("updatePrice","--?"+price);
				cv.put(COLUMN_RETAILPRICE, price);
				cv.put(COLUMN_CARTONPRICE, price);
				cv.put(COLUMN_PRODUCT_PRICE, price);
			}
		}else if (cartonPerQty > 1){
			Log.d("column_price2",cprice+","+cartonPerQty+"price:"+price);
			if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
				cv.put(COLUMN_RETAILPRICE, cprice);
				cv.put(COLUMN_CARTONPRICE, cprice);
				cv.put(COLUMN_PRODUCT_PRICE, price);
			}else{
				cv.put(COLUMN_RETAILPRICE, price);
				cv.put(COLUMN_CARTONPRICE, price);
				cv.put(COLUMN_PRODUCT_PRICE, price);
			}
		}
		cv.put(COLUMN_TOTAL, total);
		cv.put(COLUMN_TAX, tax);
		cv.put(COLUMN_SUB_TOTAL, subTot);
		cv.put(COLUMN_NETTOTAL, net_tot);
		cv.put(COLUMN_EXCHANGEQTY, exqty);
		cv.put(COLUMN_RETURN,returnQty);

		return getDatabase().update(PRODUCT_TABLE, cv,
				"_id=" + "'" + sl_no + "'", null);
	}

	public static int updateProductForTransfer(String prodcode, int cartonQty,
			double looseQty, double qty) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_QUANTITY, qty);

		return getDatabase().update(PRODUCT_TABLE, cv,
				"product_code=" + "'" + prodcode + "'", null);
	}

	public static int updateProductForTransferSR(String prodcode,
			int cartonQty, double looseQty, double qty, String srno) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_QUANTITY, qty);

		return getDatabase().update(
				PRODUCT_TABLE,
				cv,
				"product_code=" + "'" + prodcode + "' AND sl_no = '" + srno
						+ "'", null);
	}

	public static int updateProductForConsignmentStock(String prodcode,
												 int cartonQty, double looseQty, double qty, String srno) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_QUANTITY, qty);

		return getDatabase().update(
				PRODUCT_TABLE,
				cv,
				"product_code=" + "'" + prodcode + "' AND sl_no = '" + srno
						+ "'", null);
	}

	public static int updateBillDisc(int sl_no, String prodcode, String subTot) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_PRODUCT_CODE, prodcode);
		cv.put(COLUMN_SUB_TOTAL, subTot);

		return getDatabase().update(BILLDISC_TABLE, cv,
				"_id=" + "'" + sl_no + "'", null);

	}

	public static int updateBillDiscForInvoice(int sl_no, String prodcode, String subTot) {
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_PRODUCT_CODE, prodcode);
		cv.put(COLUMN_SUB_TOTAL, subTot);

		return getDatabase().update(BILLDISC_TABLE, cv,
				"product_code=" + "'" + prodcode + "'", null);

	}



	public static int updateProductCodeFilter(int fsl_no, String fcodeStr,
			String fnameStr, double fcartonQty, double flooseQty, double fqty, double ffoc,
			double fprice, double fdiscount, String fuomStr, double fcartonPerQty,
			double ftotal, double ftax, String fnetT, String ftaxType,
			String ftaxValue, String fretailPrice, String fsbTtl,
											  String cprice, String exqty, String MinimumSellingPrice,
											  String ItemRemarks, String SOSlno, String stock,String MinimumCartonSellingPrice) {
		Log.d("getValuesUpdate","-->"+fnetT+","+ftotal+","+fsbTtl);

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PRODUCT_CODE, fcodeStr);
		cv.put(COLUMN_PRODUCT_NAME, fnameStr);
		cv.put(COLUMN_CARTON_QTY, fcartonQty);
		cv.put(COLUMN_LOOSE_QTY, flooseQty);
		cv.put(COLUMN_QUANTITY, fqty);
		cv.put(COLUMN_FOC, ffoc);
		cv.put(COLUMN_PRODUCT_PRICE, fprice);
		cv.put(COLUMN_ITEM_DISCOUNT, fdiscount);
		cv.put(COLUMN_PRODUCT_UOM, fuomStr);
		cv.put(COLUMN_PIECE_PERQTY, fcartonPerQty);
		cv.put(COLUMN_TOTAL, ftotal);
		cv.put(COLUMN_TAX, ftax);
		cv.put(COLUMN_NETTOTAL, fnetT);
		cv.put(COLUMN_TAXTYPE, ftaxType);
		cv.put(COLUMN_TAXVALUE, ftaxValue);
		cv.put(COLUMN_RETAILPRICE, fretailPrice);
		cv.put(COLUMN_SUB_TOTAL, fsbTtl);

		cv.put(COLUMN_EXCHANGEQTY, exqty);
		cv.put(COLUMN_CARTONPRICE, cprice);
		cv.put(COLUMN_MINIMUM_SELLING_PRICE, MinimumSellingPrice);
		cv.put(COLUMN_ITEM_REMARKS, ItemRemarks);
		cv.put(COLUMN_BARCODE_STATUS, 0);
		cv.put(COLUMN_SO_SLNO, SOSlno);
		cv.put(COLUMN_STOCK, stock);
		cv.put(COLUMN_MINIMUM_CARTON_SELLING_PRICE, MinimumCartonSellingPrice);
		return getDatabase().update(PRODUCT_TABLE, cv,
				"product_code=" + "'" + fcodeStr + "'", null);
	}

/*	public static ArrayList<ProductDetails> products() {
		ArrayList<ProductDetails> productList = new ArrayList<ProductDetails>();
		String selectQuery = "SELECT  * FROM sales_product";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				ProductDetails productdetail = new ProductDetails();
				productdetail.setSno(cursor.getString(cursor
						.getColumnIndex(COLUMN_PRODUCT_SLNO)));
				productdetail.setItemcode(cursor.getString(cursor
						.getColumnIndex(COLUMN_PRODUCT_CODE)));
				productdetail.setDescription(cursor.getString(cursor
						.getColumnIndex(COLUMN_PRODUCT_NAME)));
				productdetail.setQty(cursor.getString(cursor
						.getColumnIndex(COLUMN_QUANTITY)));
				productdetail.setPrice(cursor.getString(cursor
						.getColumnIndex(COLUMN_PRODUCT_PRICE)));
				productdetail.setTotal(cursor.getString(cursor
						.getColumnIndex(COLUMN_TOTAL)));
				productdetail.setFocqty(cursor.getDouble(cursor
						.getColumnIndex(COLUMN_FOC)));
				productList.add(productdetail);
			} while (cursor.moveToNext());
		}
		return productList;

	}*/
	 public static ArrayList<ProductDetails> products() {
			ArrayList<ProductDetails> productList = new ArrayList<ProductDetails>();
			String selectQuery = "SELECT  * FROM sales_product";
			Cursor cursor = getDatabase().rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					ProductDetails productdetail = new ProductDetails();
					productdetail.setSno(cursor.getString(cursor
							.getColumnIndex(COLUMN_PRODUCT_SLNO)));
					productdetail.setItemcode(cursor.getString(cursor
							.getColumnIndex(COLUMN_PRODUCT_CODE)));
					productdetail.setDescription(cursor.getString(cursor
							.getColumnIndex(COLUMN_PRODUCT_NAME)));					
					productdetail.setQty(cursor.getString(cursor
							.getColumnIndex(COLUMN_QUANTITY)));
					productdetail.setPrice(cursor.getString(cursor
							.getColumnIndex(COLUMN_PRODUCT_PRICE)));
					productdetail.setTotal(cursor.getString(cursor
							.getColumnIndex(COLUMN_TOTAL)));
					productdetail.setFocqty(cursor.getDouble(cursor
							.getColumnIndex(COLUMN_FOC)));
					productdetail.setSubtotal(cursor.getString(cursor
							.getColumnIndex(COLUMN_SUB_TOTAL)));
					productdetail.setTax(cursor.getString(cursor
							.getColumnIndex(COLUMN_TAX)));
					productdetail.setTaxType(cursor.getString(cursor
							.getColumnIndex(COLUMN_TAXTYPE)));
					productdetail.setTaxPerc(cursor.getString(cursor
							.getColumnIndex(COLUMN_TAXVALUE)));
					productList.add(productdetail);
				} while (cursor.moveToNext());
			}
			return productList;

		}
	public static ArrayList<String> snoCountID() {
		ArrayList<String> snoCountID;
		snoCountID = new ArrayList<String>();
		String selectQuery = "SELECT _id FROM sales_product";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String id = cursor.getString(cursor.getColumnIndex("_id"));
				snoCountID.add(id);
			} while (cursor.moveToNext());
		}
		return snoCountID;
	}

	public static ArrayList<String> snoCountPCode() {
		ArrayList<String> snoCountPCode;
		snoCountPCode = new ArrayList<String>();
		String selectQuery = "SELECT product_code FROM sales_product";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String pCode = cursor.getString(cursor.getColumnIndex("product_code"));
				snoCountPCode.add(pCode);
			} while (cursor.moveToNext());
		}
		return snoCountPCode;
	}

	public static int updateSNO(HashMap<String, String> queryValues) {

		ContentValues values = new ContentValues();
		values.put("sl_no", queryValues.get("snum"));
		return getDatabase().update("sales_product", values, "_id" + " = ?",
				new String[] { queryValues.get("_id") });
	}

	public static int updateATTRSLNO(HashMap<String, String> queryValues) {

		ContentValues values = new ContentValues();
		values.put("sl_no", queryValues.get("snum"));
		return getDatabase().update("attribute_table", values, "product_code" + " = ?",
				new String[] { queryValues.get("product_code") });
	}

	public static int updateBillSNO(HashMap<String, String> queryValues) {

		ContentValues values = new ContentValues();
		values.put("sl_no", queryValues.get("snum"));
		return getDatabase().update("bill_disc", values, "_id" + " = ?",
				new String[] { queryValues.get("_id") });
	}

	public static int countprodcode(String productcode) {
		int prodcode = 0;
		String Query = "SELECT DISTINCT COUNT(product_code) FROM sales_product where product_code='"
				+ productcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {

				prodcode = cursor.getInt(cursor
						.getColumnIndex("COUNT(product_code)"));

			} while (cursor.moveToNext());
		}
		cursor.close();
		return prodcode;
	}

	public void deleteProd(String productcode) {

		SQLiteDatabase db = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM sales_product where product_code='"
				+ productcode + "'";
		Log.d("query", deleteQuery);
		db.execSQL(deleteQuery);
		db.close();

	}

	// public void deletebatchbyprodcode(String productcode) {
	//
	// SQLiteDatabase db = this.getWritableDatabase();
	// String deleteQuery = "DELETE FROM batch_table where product_code='"
	// + productcode + "'";
	// Log.d("query", deleteQuery);
	// db.execSQL(deleteQuery);
	// db.close();
	//
	// }

	public void deleteBillPrd(String productcode) {

		SQLiteDatabase db = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM bill_disc where product_code='"
				+ productcode + "'";
		Log.d("query", deleteQuery);
		db.execSQL(deleteQuery);
		db.close();

	}

	public static double getsubTot(int slno) {
		double sbT = 0;
		String Query = "SELECT sub_total FROM so_subtot where sl_no= '" + slno
				+ "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				sbT = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return sbT;
	}

	public static boolean deleteProduct(String sl_no) {

		return getDatabase().delete(PRODUCT_TABLE,
				COLUMN_PRODUCT_ID + "=" + sl_no, null) > 0;

	}

	public static boolean deleteBillDiscount(String sl_no) {

		return getDatabase().delete(BILLDISC_TABLE,
				COLUMN_PRODUCT_ID + "=" + sl_no, null) > 0;

	}

	// Batch
	public static boolean deleteBatch(String sl_no) {

		return getDatabase().delete(BATCH_TABLE,
				COLUMN_PRODUCT_ID + "=" + sl_no, null) > 0;

	}

	public static boolean deleteConsignmentStock(String sl_no){
	 	Log.d("tableDelete","-->"+sl_no);
		return getDatabase().delete(CONSIGNMENT_STOCK_TABLE,
				COLUMN_PRODUCT_ID + "=" + sl_no, null) > 0;

	}


	public static ArrayList<String> getProductCode() {
		ArrayList<String> productcode = new ArrayList<String>();
		String Query = "SELECT product_code FROM sales_product";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				String prodcode = cursor.getString(cursor
						.getColumnIndex("product_code"));
				productcode.add(prodcode);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return productcode;
	}

	public static double getTax() {
		double tax = 0;
		String Query = "SELECT sum(tax) FROM sales_product";
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

	public static double getTotalCqty() {
		double totalqty = 0.00;
		String Query = "SELECT sum(carton_qty) FROM sales_product";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalqty = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalqty;
	}

	public static double getTotalLqty() {
		double totalqty = 0.00;
		String Query = "SELECT sum(loose_qty) FROM sales_product";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalqty = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalqty;
	}

	public static String getTotaBatchQty(String prodcode) {
		Log.d("getTotaBatchQty","-->"+"getTotaBatchQty");
		String totalqty = "";
		String Query = "SELECT sum(quantity) FROM batch_table where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalqty = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalqty;
	}

	public static String getTotaBatchQtySR(String prodcode, String srno) {
		Log.d("getTotaBatchQtySR","-->"+"getTotaBatchQtySR");
		String totalqty = "";
		String Query = "SELECT sum(quantity) FROM batch_table where product_code='"
				+ prodcode + "' AND SRSlno = '" + srno + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalqty = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalqty;
	}

	public static int getTotalBatchCQtySR(String prodcode, String srno) {
		int carton_qty = 0;
		String Query = "SELECT sum(carton_qty) FROM batch_table where product_code='"
				+ prodcode + "' AND SRSlno = '" + srno + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				carton_qty = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return carton_qty;
	}

	public static int getTotalBatchLQtySR(String prodcode, String srno) {
		int loose_qty = 0;
		String Query = "SELECT sum(loose_qty) FROM batch_table where product_code='"
				+ prodcode + "' AND SRSlno = '" + srno + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				loose_qty = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return loose_qty;
	}

	public static int getTotalConsignmentStockQty(String prodcode, String srno) {
		int totalqty = 0;
		String Query = "SELECT sum(quantity) FROM consignment_table where product_code='"
				+ prodcode + "' AND SRSlno = '" + srno + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalqty = cursor.getInt(0);
				Log.d("totalqty","-->"+totalqty);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalqty;
	}

	public static int getTotalConsignmentQty(String prodcode) {
		int bqty = 0;
		String selectQuery = "SELECT SUM(quantity) FROM consignment_table where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				bqty = cursor.getInt(cursor.getColumnIndex("SUM(quantity)"));
			} while (cursor.moveToNext());
		}
		return bqty;
	}

	public static int getTotalConsignmentLQty(String prodcode, String srno) {
		int loose_qty = 0;
		String Query = "SELECT sum(loose_qty) FROM consignment_table where product_code='"
				+ prodcode + "' AND SRSlno = '" + srno + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				loose_qty = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return loose_qty;
	}


	public static int getTotalConsignmentLQty(String prodcode) {
		int loose_qty = 0;
		String Query = "SELECT sum(loose_qty) FROM consignment_table where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				loose_qty = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return loose_qty;
	}


	public static int getTotalConsignmentStockCQty(String prodcode, String srno) {
		int carton_qty = 0;
		String Query = "SELECT sum(carton_qty) FROM consignment_table where product_code='"
				+ prodcode + "' AND SRSlno = '" + srno + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				carton_qty = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return carton_qty;
	}


	public static int getTotalConsignmentCQty(String prodcode) {
		int carton_qty = 0;
		String Query = "SELECT sum(carton_qty) FROM consignment_table where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				carton_qty = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return carton_qty;
	}

//	public static int getTotalConsignmentFocQty(String conProdCode, String s_no) {
//		int totalqty = 0;
//		String Query = "SELECT sum(foc) FROM consignment_table where product_code='"
//				+ conProdCode + "' AND consignment_no = '" + s_no + "'";
//		Cursor cursor = getDatabase().rawQuery(Query, null);
//		if (cursor != null && cursor.getCount() > 0) {
//			cursor.moveToFirst();
//			do {
//				totalqty = cursor.getInt(0);
//			} while (cursor.moveToNext());
//		}
//		cursor.close();
//		return totalqty;
//	}



	public static int getTotaBatchFocQtySR(String prodcode, String srno) {
		int totalqty = 0;
		String Query = "SELECT sum(foc) FROM batch_table where product_code='"
				+ prodcode + "' AND SRSlno = '" + srno + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalqty = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalqty;
	}

	public static int getTotalBatchCQty(String prodcode) {
		int carton_qty = 0;
		String Query = "SELECT sum(carton_qty) FROM batch_table where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				carton_qty = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return carton_qty;
	}

	public static int getTotalBatchLQty(String prodcode) {
		int loose_qty = 0;
		String Query = "SELECT sum(loose_qty) FROM batch_table where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				loose_qty = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return loose_qty;
	}

	public static int getTotaBatchFocQty(String prodcode) {
		int totalqty = 0;
		String Query = "SELECT sum(foc) FROM batch_table where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalqty = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalqty;
	}

	public static String getprodcodefrombatch(String prodcode) {
		String code = "";
		String Query = "SELECT product_code FROM batch_table where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				code = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return code;
	}

	public static ArrayList<String> getBatchHave(String prodcode) {
		ArrayList<String> batch_have = new ArrayList<String>();
		String havebatch, haveexpiry, piece_per_qty;
		String Query = "SELECT have_batch,have_expiry,piece_per_qty FROM batch_table WHERE product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				havebatch = cursor.getString(cursor
						.getColumnIndex("have_batch"));
				haveexpiry = cursor.getString(cursor
						.getColumnIndex("have_expiry"));
				piece_per_qty = cursor.getString(cursor
						.getColumnIndex("piece_per_qty"));
				batch_have.add(havebatch);
				batch_have.add(haveexpiry);
				batch_have.add(piece_per_qty);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return batch_have;
	}

	public static double getTotalQty() {
		double totalqty = 0.00;
		String Query = "SELECT sum(quantity) FROM sales_product";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalqty = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalqty;
	}

	public static double getitemDisc() {
		double itemDisc = 0;
		String Query = "SELECT sum(item_discount) FROM sales_product";
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
	public static double getNetTotal() {
		  double subTot = 0;
		  String Query = "SELECT sum(net_total) FROM sales_product";
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
	public static double getSubTotal() {
		double subTot = 0;
		String Query = "SELECT sum(sub_total) FROM sales_product";
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

	public static String getTaxValue(String id) {
		String taxvalue = "";
		String Query = "SELECT tax_value FROM sales_product where _id= " + id;
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				taxvalue = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return taxvalue;
	}

	public static String getTaxType(String id) {
		String taxtype = "";
		String Query = "SELECT tax_type FROM sales_product where _id= " + id;
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				taxtype = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return taxtype;
	}

	public static String getPrice(String id) {
		String price = "";
		String Query = "SELECT price FROM sales_product where _id= " + id;
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				price = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return price;
	}

	public static String getUOM(String id) {
		String uom = "";
		String Query = "SELECT uom FROM sales_product where _id= " + id;
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				uom = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return uom;
	}

	public static String getPiecePerQty(String id) {
		String ppqty = "";
		String Query = "SELECT piece_per_qty FROM sales_product where _id= "
				+ id;
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				ppqty = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return ppqty;
	}

	public static ArrayList<ProductDetails> requestproducts() {
		ArrayList<ProductDetails> productList = new ArrayList<ProductDetails>();
		String selectQuery = "SELECT  * FROM sales_product";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				ProductDetails productdetail = new ProductDetails();
				productdetail.setItemcode(cursor.getString(cursor
						.getColumnIndex(COLUMN_PRODUCT_CODE)));
				productdetail.setDescription(cursor.getString(cursor
						.getColumnIndex(COLUMN_PRODUCT_NAME)));
				String reqqty = cursor.getString(cursor
						.getColumnIndex(COLUMN_QUANTITY));
				if (reqqty.contains(".")) {
					StringTokenizer tokens = new StringTokenizer(reqqty, ".");
					String qty = tokens.nextToken();
					productdetail.setQty(qty);
				} else {
					productdetail.setQty(reqqty);
				}
				productdetail.setCqty(cursor.getString(cursor
						.getColumnIndex(COLUMN_CARTON_QTY)));
				productdetail.setLqty(cursor.getString(cursor
						.getColumnIndex(COLUMN_LOOSE_QTY)));
				productdetail.setTotalqty(cursor.getDouble(cursor
						.getColumnIndex(COLUMN_QUANTITY)));
				productList.add(productdetail);
			} while (cursor.moveToNext());
		}
		return productList;

	}

	public static int deleteAllProduct() {
		return getDatabase().delete("sales_product", null, null);

	}

	public static int deleteBillDisc() {
		return getDatabase().delete("bill_disc", null, null);

	}

	public static int deleteSubTOt() {

		return getDatabase().delete("so_subtot", null, null);

	}

	public static int deleteImage() {
		return getDatabase().delete("image_table", null, null);

	}

	public static int deleteBarcode() {
		return getDatabase().delete("barcode_table", null, null);

	}

	public static int deleteallbatch() {
		return getDatabase().delete("batch_table", null, null);

	}
	public static int deleteallConsignment() {
		return getDatabase().delete("consignment_table", null, null);

	}

	public static int deleteallexpense() {
		return getDatabase().delete("expense_table", null, null);
	}

	public SOTDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		db.execSQL("CREATE TABLE IF NOT EXISTS " + PRODUCT_TABLE + " ( "
				+ COLUMN_PRODUCT_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_PRODUCT_SLNO + " INTEGER NOT NULL,"
				+ COLUMN_PRODUCT_CODE + " TEXT NOT NULL," + COLUMN_PRODUCT_NAME
				+ " TEXT NOT NULL," + COLUMN_CARTON_QTY + " REAL NOT NULL,"
				+ COLUMN_LOOSE_QTY + " REAL," + COLUMN_QUANTITY
				+ " REAL NOT NULL," + COLUMN_FOC + " INTEGER,"
				+ COLUMN_PRODUCT_PRICE + " REAL NOT NULL,"
				+ COLUMN_ITEM_DISCOUNT + " REAL," + COLUMN_PRODUCT_UOM
				+ " TEXT NOT NULL," + COLUMN_PIECE_PERQTY
				+ " REAL NOT NULL," + COLUMN_TOTAL + " REAL NOT NULL,"
				+ COLUMN_TAX + " REAL," + COLUMN_NETTOTAL + " REAL NOT NULL,"
				+ COLUMN_TAXTYPE + " TEXT," + COLUMN_TAXVALUE + " TEXT,"
				+ COLUMN_SUB_TOTAL + " REAL," + COLUMN_CARTONPRICE
				+ " REAL ," + COLUMN_EXCHANGEQTY + " INTEGER,"
				+ COLUMN_RETAILPRICE + " TEXT," + COLUMN_MINIMUM_SELLING_PRICE
				+ " TEXT," + COLUMN_MINIMUM_CARTON_SELLING_PRICE
				+ " TEXT," +

				COLUMN_ITEM_REMARKS + " TEXT," + COLUMN_HAVE_BATCH
				+ " TEXT," + COLUMN_HAVE_EXPIRY + " TEXT,"
				+ COLUMN_BARCODE_STATUS + " INTEGER," + COLUMN_SO_SLNO
				+ " INTEGER," + COLUMN_ORIGINAL_QTY + " INTEGER,"
				+ COLUMN_RETURN_CARTON + " TEXT," + COLUMN_RETURN_LOOSE
				+ " TEXT," + COLUMN_RETURN_QTY + " TEXT,"
				+ COLUMN_STOCK + " TEXT,"
				+ COLUMN_DV_ORIGINAL_CQTY + " TEXT," + COLUMN_DV_ORIGINAL_QTY + " TEXT,"
				+ COLUMN_RETURN_SUBTOTAL + " TEXT," + COLUMN_RETURN_TAX
				+ " TEXT," + COLUMN_RETURN_NETTOTAL + " TEXT)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + BILLDISC_TABLE + " ( "
				+ COLUMN_PRODUCT_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_PRODUCT_SLNO + " INTEGER NOT NULL,"
				+ COLUMN_PRODUCT_CODE + " TEXT NOT NULL," + COLUMN_SUB_TOTAL
				+ " TEXT)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + IMAGE_TABLE + " ( "
				+ COLUMN_IMAGE_ID + " INTEGER NOT NULL, "
				+ COLUMN_SIGNATURE_IMAGE + " TEXT, " + COLUMN_PRODUCT_IMAGE
				+ " TEXT)");

		// String query =
		// "CREATE TABLE IF NOT EXISTS barcode (_id INTEGER PRIMARY KEY, paletteId INTEGER, productcode TEXT, productname TEXT, barcode TEXT, weight REAL, snum INTEGER)";
		db.execSQL("CREATE TABLE IF NOT EXISTS " + BARCODE_TABLE + " ("
				+ COLUMN_PRODUCT_ID + " INTEGER primary key autoincrement,"
				+ COLUMN_PALETTEID + " INTEGER, " + COLUMN_CODE
				+ "  TEXT NOT NULL," + COLUMN_NAME + " TEXT NOT NULL,"
				+ COLUMN_BARCODE + " TEXT NOT NULL," + COLUMN_WEIGHT
				+ " REAL NOT NULL," + COLUMN_SNUM + " INTEGER NOT NULL,"
				+ COLUMN_PRODUCTID + " INTEGER NOT NULL,"
				+ COLUMN_STATUS + " INTEGER,"
				+ COLUMN_BARCODE_BOXES + " TEXT)");

		db.execSQL("CREATE TABLE IF NOT EXISTS " + AREA_TABLE + " ( "
				+ COLUMN_PRODUCT_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_AREACODE + " TEXT ," + COLUMN_AREANAME + " TEXT,"
				+ COLUMN_BOX + " TEXT)");
		// Batch
		db.execSQL("CREATE TABLE IF NOT EXISTS " + BATCH_TABLE + " ( "
				+ COLUMN_PRODUCT_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_PRODUCT_SLNO + " INTEGER NOT NULL,"
				+ COLUMN_PRODUCT_CODE + " TEXT NOT NULL," + COLUMN_PRODUCT_NAME
				+ " TEXT NOT NULL," + COLUMN_BATCH_NO + " TEXT,"
				+ COLUMN_MFG_DATE + " TEXT,"
				+ COLUMN_EXPIRY_DATE + " TEXT," + COLUMN_CARTON_QTY
				+ " INTEGER NOT NULL," + COLUMN_LOOSE_QTY + " INTEGER,"
				+ COLUMN_QUANTITY + " INTEGER NOT NULL," + COLUMN_FOC
				+ " INTEGER," + COLUMN_PIECE_PERQTY + " INTEGER NOT NULL,"
				+ COLUMN_HAVE_BATCH + " TEXT," + COLUMN_HAVE_EXPIRY + " TEXT,"
				+ COLUMN_PRODUCT_PRICE + " TEXT," + COLUMN_AVAILABLE_CARTON
				+ " TEXT," + COLUMN_AVAILABLE_LOOSE + " TEXT,"
				+ COLUMN_AVAILABLE_QTY + " TEXT," + COLUMN_SR_SLNO + " TEXT,"
				+ COLUMN_REMARKS + " TEXT)");

		// Expense
		db.execSQL("CREATE TABLE IF NOT EXISTS " + EXPENSE_TABLE + " ( "
				+ COLUMN_PRODUCT_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_SNO + " INTEGER NOT NULL," + COLUMN_ACCOUNTNO
				+ " INTEGER NOT NULL," + COLUMN_DESC + " TEXT NOT NULL,"
				+ COLUMN_AMOUNT + " TEXT NOT NULL," + COLUMN_TAXTYPE + " TEXT NOT NULL," + COLUMN_TAXVALUE + " TEXT NOT NULL,"
				+ COLUMN_SUB_TOTAL + " TEXT NOT NULL,"
				+ COLUMN_TAX + " TEXT NOT NULL," + COLUMN_NETTOTAL + " TEXT NOT NULL," + COLUMN_TAXNAME+ " TEXT NOT NULL,"
				+ COLUMN_TAXCODE + " TEXT)");
		
		// Van Driver
				db.execSQL("CREATE TABLE IF NOT EXISTS " + VANDRIVER_TABLE + " ( "
						+ COLUMN_VAN_ID + " INTEGER primary key autoincrement, "
						+ COLUMN_PRODUCT_SLNO + " INTEGER NOT NULL,"
						+ COLUMN_VANCODE + " INTEGER NOT NULL,"+ COLUMN_DESC + " TEXT)");
				
				// Usertracking Master
				db.execSQL("CREATE TABLE IF NOT EXISTS " + USERTACKING_MASTER_TABLE + " ( "
								+ COLUMN_PRODUCT_ID + " INTEGER primary key autoincrement, "
								+ COLUMN_SNO + " INTEGER NOT NULL," + COLUMN_COMPANYCODE + " TEXT,"
								+ COLUMN_USERNAME + " TEXT," + COLUMN_CURRENTLOCATIONTRACKING + " TEXT,"
								+ COLUMN_ROUTETRACKING + " TEXT," + COLUMN_TRACKINGINTERVAL + " TEXT,"
								+ COLUMN_MON + " TEXT," + COLUMN_TUE + " TEXT,"
								+ COLUMN_WED + " TEXT," + COLUMN_THU + " TEXT,"
								+ COLUMN_FRI + " TEXT," + COLUMN_SAT + " TEXT,"
								+ COLUMN_SUN + " TEXT," 
								+ COLUMN_MONFROMTIME + " TEXT," + COLUMN_MONTOTIME + " TEXT,"
								+ COLUMN_TUEFROMTIME + " TEXT," + COLUMN_TUETOTIME+ " TEXT,"
								+ COLUMN_WEDFROMTIME + " TEXT," + COLUMN_WEDTOTIME+ " TEXT,"
								+ COLUMN_THUFROMTIME + " TEXT," + COLUMN_THUTOTIME + " TEXT,"
								+ COLUMN_FRIFROMTIME + " TEXT," + COLUMN_FRITOTIME + " TEXT,"
								+ COLUMN_SATFROMTIME + " TEXT," + COLUMN_SATTOTIME + " TEXT,"
								+ COLUMN_SUNFROMTIME + " TEXT," + COLUMN_SUNTOTIME + " TEXT)");
					
				
		db.execSQL("CREATE TABLE IF NOT EXISTS " + MERCHANDISE_TABLE + " ( "
						+ COLUMN_ID + " INTEGER primary key autoincrement, "
						+ COLUMN_SNO + " INTEGER NOT NULL,"
						+ COLUMN_TYPE + " TEXT,"
						+ COLUMN_STATUS + " INTEGER NOT NULL,"
						+ COLUMN_IMAGE + " TEXT,"
					    + COLUMN_REMARKS + " TEXT)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + SO_DETAIL_TABLE + " ( "
			    + COLUMN_ID + " INTEGER primary key autoincrement, "
			    + COLUMN_PRODUCT_SLNO + " INTEGER NOT NULL,"
			    + COLUMN_PRODUCT_CODE + " TEXT NOT NULL,"    
			       + COLUMN_QUANTITY + " INTEGER NOT NULL)");

		//create attribute table

		db.execSQL("CREATE TABLE IF NOT EXISTS " + ATTRIBUTE_TABLE + " ( "
				+ COLUMN_PRODUCT_SLNO + " INTEGER NOT NULL, "
				+ COLUMN_PRODUCT_NAME + " TEXT,"
				+ COLUMN_PRODUCT_CODE + " TEXT NOT NULL,"
				+ COLUMN_COLOR_CODE + " INTEGER NOT NULL,"
				+ COLUMN_COLOR_NAME + " TEXT,"
				+ COLUMN_SIZE_CODE + " INTEGER NOT NULL,"
				+ COLUMN_SIZE_NAME + " TEXT,"
				+ COLUMN_QUANTITY + " INTEGER NOT NULL,"
				+ COLUMN_FLAG + " TEXT)");


		//CONSIGNMENT stock table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + CONSIGNMENT_STOCK_TABLE + " ( "
				+ COLUMN_PRODUCT_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_PRODUCT_SLNO + " INTEGER NOT NULL,"
				+ COLUMN_PRODUCT_CODE + " TEXT NOT NULL,"
				+ COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
				+ COLUMN_BATCH_NO + " TEXT NOT NULL,"
				+ COLUMN_EXPIRY_DATE + " TEXT NOT NULL,"
				+ COLUMN_MFG_DATE + " TEXT NOT NULL,"
				+ COLUMN_AVAILABLE_CARTON + " TEXT NOT NULL,"
				+ COLUMN_AVAILABLE_LOOSE + " TEXT NOT NULL,"
				+ COLUMN_AVAILABLE_QTY + " TEXT NOT NULL,"
				+ COLUMN_CARTON_QTY + " TEXT NOT NULL,"
				+ COLUMN_LOOSE_QTY + " TEXT NOT NULL,"
				+ COLUMN_QUANTITY + " TEXT NOT NULL,"
				+ COLUMN_CUSTOMER_CODE + " TEXT NOT NULL,"
				+ COLUMN_CONSIGNMENT_NO + " TEXT NOT NULL,"
				+ COLUMN_CONSIGNMENT_NAME + " TEXT NOT NULL,"
				+ COLUMN_UOM + " TEXT NOT NULL,"
				+ COLUMN_DURATION + " TEXT NOT NULL,"
				+ COLUMN_PIECE_PERQTY + " TEXT NOT NULL,"
				+ COLUMN_SR_SLNO + " TEXT,"
				+ COLUMN_CON_EXPIRY_DATE + " TEXT NOT NULL)");

		//tax table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TAX_TABLE + " ( "
				+ COLUMN_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_TAXCODE + " TEXT NOT NULL,"
				+ COLUMN_TAXNAME + " TEXT NOT NULL,"
				+ COLUMN_TAXVALUE + " TEXT NOT NULL,"
				+ COLUMN_TAXTYPE + " TEXT)");

		//manual_stock_table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + MANUAL_STOCK_TABLE + " ( "
				+ COLUMN_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_PRODUCT_CODE + " TEXT NOT NULL,"
				+ COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
				+ COLUMN_CARTON_QTY + " TEXT NOT NULL,"
				+ COLUMN_LOOSE_QTY + " TEXT NOT NULL,"
				+ COLUMN_PIECE_PERQTY + " TEXT NOT NULL,"
				+ COLUMN_STOCK + " TEXT NOT NULL,"
				+ COLUMN_QUANTITY + " TEXT)");

		//close stock table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + CLOSE_STOCk_TABLE + " ( "
				+ COLUMN_PRODUCT_SLNO + " TEXT NOT NULL, "
				+ COLUMN_PRODUCT_CODE + " TEXT NOT NULL,"
				+ COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
				+ COLUMN_CARTON_QTY + " TEXT NOT NULL,"
				+ COLUMN_LOOSE_QTY + " TEXT NOT NULL,"
				+ COLUMN_PIECE_PERQTY + " TEXT NOT NULL,"
				+ COLUMN_PRODUCT_PRICE + " TEXT NOT NULL,"
				+ COLUMN_QUANTITY + " TEXT)");


		//denom,ination table
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DENOMINATION_TABLE + " ( "
				+ COLUMN_ID + " INTEGER primary key autoincrement, "
				+ COLUMN_PRODUCT_SLNO + " TEXT NOT NULL, "
				+ COLUMN_PRODUCT_CODE + " TEXT NOT NULL,"
				+ COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
				+ COLUMN_QUANTITY + " TEXT)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + BILLDISC_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + BARCODE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + BATCH_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + MERCHANDISE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SO_DETAIL_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + ATTRIBUTE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CONSIGNMENT_STOCK_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + TAX_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + MANUAL_STOCK_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CLOSE_STOCk_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DENOMINATION_TABLE);


		onCreate(db);
	}

	private void dropTable(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + BILLDISC_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + BARCODE_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + BATCH_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + MERCHANDISE_TABLE + ";");
			 db.execSQL("DROP TABLE IF EXISTS " + SO_DETAIL_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + ATTRIBUTE_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + CONSIGNMENT_STOCK_TABLE + ";");
			db.execSQL("DROP TABLE IF EXISTS " + TAX_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + MANUAL_STOCK_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + CLOSE_STOCk_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DENOMINATION_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void truncateTables() {
		dropTable(getWritableDatabase());
		onCreate(getWritableDatabase());
	}

	public void insertproduct(HashMap<String, String> queryValues) {
		// SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("paletteId", queryValues.get("paletteId"));
		values.put("productcode", queryValues.get("code"));
		values.put("productname", queryValues.get("name"));
		values.put("barcode", queryValues.get("barcode"));
		values.put("weight", queryValues.get("weight"));
		// Log.d("weight-->dwb", queryValues.get("weight"));
		values.put("snum", queryValues.get("snum"));
		values.put("productId", queryValues.get("productId"));
		values.put("Boxes", queryValues.get("Boxes"));
		values.put("status", queryValues.get("status"));

		getDatabase().insert(BARCODE_TABLE, null, values);

		// database.close();
	}

	public int updateBarcodeStatus(HashMap<String, String> queryValues) {
		// SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("status", queryValues.get("status"));
		
		return getDatabase().update("barcode_table", values, null,null);
	}
	
	public Cursor getStatus() {

		String[] columns = new String[] { COLUMN_ID, COLUMN_STATUS };
		return getDatabase().query(BARCODE_TABLE, columns, "status" + " = ?",
				new String[] { "0" }, null, null,
				null, null);
	}
	
	public String getProd(String productId) {
		String productid = "";
		String selectQuery = "SELECT * FROM barcode_table where productId='"
				+ productId + "'";
		Log.d("selectQuery", "selectQuery");
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				productid = cursor
						.getString(cursor.getColumnIndex("productId"));

			} while (cursor.moveToNext());
		}
		return productid;
	}

	public int getnum(String productId) {
		int prodsnos = 0;
		String selectQuery = "SELECT * FROM barcode_table where productId='"
				+ productId + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				prodsnos = cursor.getInt(cursor.getColumnIndex("snum"));

			} while (cursor.moveToNext());
		}
		return prodsnos;
	}

	public int maxsnovalue() {
		int sno = 0;
		String selectQuery = "SELECT MAX(snum) FROM barcode_table";

		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				sno = cursor.getInt(cursor.getColumnIndex("MAX(snum)"));
			} while (cursor.moveToNext());
		}
		return sno;
	}

	public ArrayList<HashMap<String, String>> getAllProducts() {
		ArrayList<HashMap<String, String>> productList = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT  * FROM barcode_table";

		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
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
		String selectQuery = "SELECT * FROM barcode_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
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
		String selectQuery = "SELECT DISTINCT COUNT(productcode) FROM barcode_table where productcode='"
				+ productcode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				cartonno = cursor.getString(cursor
						.getColumnIndex("COUNT(productcode)"));

			} while (cursor.moveToNext());
		}
		return cartonno;
	}

	public double getTotal(String productid) {
		double sum = 0;
		Cursor cursor = getDatabase().rawQuery(
				"SELECT SUM(weight),_id FROM barcode_table where productId='"
						+ productid + "'", null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				sum = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return sum;
	}

	public void deleteProds(String id) {
		// Log.d(LOGCAT, "delete");
		SQLiteDatabase db = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM barcode_table where _id='" + id + "'";
		Log.d("query", deleteQuery);
		db.execSQL(deleteQuery);

	}

	public static void deleteProdId(String productid) {
		// Log.d(LOGCAT, "delete");
		// SQLiteDatabase db = this.getWritableDatabase();
		// String deleteQuery = "DELETE FROM barcode_table where paletteId ='" +
		// sno + "'AND productId ='"+ productid +"'";
		String deleteQuery = "DELETE FROM barcode_table where productId='"
				+ productid + "'";
		getDatabase().execSQL(deleteQuery);
		Log.d("query", deleteQuery);

	}

	public ArrayList<HashMap<String, String>> getProductInfo(String id) {
		ArrayList<HashMap<String, String>> ahmp = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> productList = new HashMap<String, String>();
		String selectQuery = "SELECT * FROM barcode_table where _id='" + id
				+ "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				productList.put("_id", cursor.getString(0));
				// productList.put("paletteId", cursor.getString(1));
				productList.put("productcode", cursor.getString(2));
				productList.put("productname", cursor.getString(3));
				productList.put("barcode", cursor.getString(4));
				productList.put("weight", "" + cursor.getDouble(5));
				// Log.d("weight-->db", ""+cursor.getDouble((5)));
				ahmp.add(productList);
			} while (cursor.moveToNext());
		}
		return ahmp;
	}

	public ArrayList<String> getCountid() {
		ArrayList<String> sumproduct;
		sumproduct = new ArrayList<String>();
		String selectQuery = "SELECT DISTINCT productId FROM barcode_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String paletteid = cursor.getString(cursor
						.getColumnIndex("productId"));
				sumproduct.add(paletteid);
			} while (cursor.moveToNext());
		}

		return sumproduct;
	}

	public ArrayList<String> getProductPalette(String productId) {
		ArrayList<String> sumproduct;
		sumproduct = new ArrayList<String>();

		String paletteQuery = "SELECT * FROM barcode_table where productId='"
				+ productId + "'";
		Cursor cursor = getDatabase().rawQuery(paletteQuery, null);
		if (cursor.moveToFirst()) {
			do {

				String proid = cursor.getString(cursor.getColumnIndex("_id"));
				
				sumproduct.add(proid);

			} while (cursor.moveToNext());
		}

		return sumproduct;
	}

	public ArrayList<String> totalpaletteweight(String productId) {
		ArrayList<String> sumpalette;
		sumpalette = new ArrayList<String>();
		double sum = 0;
		Cursor cursor = getDatabase().rawQuery(
				"SELECT SUM(weight),_id FROM barcode_table where productId='"
						+ productId + "'", null);
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
		String selectQuery = "SELECT * FROM barcode_table where _id='" + id
				+ "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				// productList.put("_id", cursor.getString(0));
				String barcode = cursor.getString(cursor
						.getColumnIndex("barcode"));
				String totweight = ""
						+ cursor.getDouble(cursor.getColumnIndex("weight"));
				String probarcodeweight = barcode + '=' + totweight;
				ahmp.add(probarcodeweight);
			} while (cursor.moveToNext());
		}
		return ahmp;
	}

	public double getprodweight(String productcode) {
		double weight = 0;
		String selectQuery = "SELECT * FROM barcode_table where productcode='"
				+ productcode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				weight = Double.parseDouble(cursor.getString(5));
			} while (cursor.moveToNext());
		}

		return weight;
	}

	public double getprodcode(String productcode) {
		double weight = 0;
		String selectQuery = "SELECT productcode FROM barcode_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
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
		String selectQuery = "SELECT productcode FROM barcode_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
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
		Cursor cursor = getDatabase().rawQuery(
				"SELECT SUM(weight) FROM barcode_table", null);
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
		ContentValues values = new ContentValues();
		// values.put("paletteId", queryValues.get("paletteId"));
		values.put("productcode", queryValues.get("productcode"));
		values.put("productname", queryValues.get("productname"));
		values.put("barcode", queryValues.get("barcode"));
		values.put("weight", queryValues.get("weight"));
		return getDatabase().update("barcode_table", values, "_id" + " = ?",
				new String[] { queryValues.get("id") });
	}

	/*
	 * public int updateProducts(HashMap<String, String> queryValues) { //
	 * SQLiteDatabase database = this.getWritableDatabase(); ContentValues
	 * values = new ContentValues(); values.put("productname",
	 * queryValues.get("productname")); values.put("weight",
	 * queryValues.get("weight")); return getDatabase().update("barcode_table",
	 * values, "_id" + " = ?", new String[] { queryValues.get("id") }); }
	 */
	public int updateProducts(HashMap<String, String> queryValues) {
		// SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("barcode", queryValues.get("barcode"));
		values.put("weight", queryValues.get("weight"));
		return getDatabase().update("barcode_table", values, "_id" + " = ?",
				new String[] { queryValues.get("id") });
	}

	public ArrayList<String> producode() {
		ArrayList<String> sumproduct;
		sumproduct = new ArrayList<String>();
		String selectQuery = "SELECT productcode FROM barcode_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
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

		String selectQuery = "SELECT DISTINCT productcode FROM barcode_table order by _id";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String prodcode = cursor.getString(cursor
						.getColumnIndex("productcode"));
				sumproduct.add(prodcode);
			} while (cursor.moveToNext());
		}

		return sumproduct;
	}

	public String distinctProdCode(String productId) {
		String prodcode = "";
		String selectQuery = "SELECT DISTINCT productcode FROM barcode_table where productId ='"
				+ productId + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				prodcode = cursor.getString(cursor
						.getColumnIndex("productcode"));
			} while (cursor.moveToNext());
		}

		return prodcode;
	}

	public String distinctProdName(String productId) {
		String prodname = "";
		String selectQuery = "SELECT DISTINCT productname FROM barcode_table where productId ='"
				+ productId + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				prodname = cursor.getString(cursor
						.getColumnIndex("productname"));
			} while (cursor.moveToNext());
		}

		return prodname;
	}

	public static String distinctProdBarCodeId(String productid) {
		String prodid = "";
		String selectQuery = "SELECT DISTINCT productId FROM barcode_table where productId ='"
				+ productid + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				prodid = cursor.getString(cursor.getColumnIndex("productId"));
			} while (cursor.moveToNext());
		}

		return prodid;
	}

	public String prodBarCodeId(String id) {
		String prodid = "";
		String selectQuery = "SELECT productId FROM barcode_table where _id ='"
				+ id + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				prodid = cursor.getString(cursor.getColumnIndex("productId"));
			} while (cursor.moveToNext());
		}

		return prodid;
	}

	public ArrayList<String> distinctprodname() {
		ArrayList<String> sumproduct;
		sumproduct = new ArrayList<String>();

		String selectQuery = "SELECT DISTINCT productname FROM barcode_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
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

		String selectQuery = "SELECT productname FROM barcode_table where productcode='"
				+ productcode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
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
		String selectQuery = "SELECT SUM(weight) FROM barcode_table where productcode='"
				+ productcode + "'";
		// Cursor cursor = db.rawQuery(
		// "SELECT SUM(weight) FROM products where productcode='"+ productcode
		// +"'", null);
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
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
		String selectQuery = "SELECT barcode FROM barcode_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
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
		String selectQuery = "SELECT productname FROM barcode_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
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
		String selectQuery = "SELECT weight FROM barcode_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String weight = cursor.getString(cursor
						.getColumnIndex("weight"));
				sumproduct.add(weight);
			} while (cursor.moveToNext());
		}

		return sumproduct;
	}

	/*
	 * public ArrayList<String> palettecode() { ArrayList<String> sumproduct;
	 * sumproduct = new ArrayList<String>(); String selectQuery =
	 * "SELECT paletteId FROM barcode_table"; Cursor cursor =
	 * getDatabase().rawQuery(selectQuery, null); if (cursor.moveToFirst()) { do
	 * { String palettecode = cursor.getString(cursor
	 * .getColumnIndex("paletteId")); sumproduct.add(palettecode); } while
	 * (cursor.moveToNext()); }
	 * 
	 * return sumproduct; }
	 */

	public Cursor getBarcodeCursor() {
		String[] columns = new String[] { COLUMN_PRODUCT_ID, COLUMN_PALETTEID,
				COLUMN_CODE, COLUMN_NAME, COLUMN_BARCODE, COLUMN_WEIGHT };
		Cursor cursor = getDatabase().query(BARCODE_TABLE, columns, null, null,
				null, null, null);

		return cursor;
	}

	public Cursor getPalette(String productId) {
		String[] columns = new String[] { COLUMN_PRODUCT_ID, COLUMN_PALETTEID,
				COLUMN_CODE, COLUMN_NAME, COLUMN_BARCODE, COLUMN_WEIGHT,
				COLUMN_SNUM };
		Cursor cursor = getDatabase().query(BARCODE_TABLE, columns,
				"productId= " + productId, null, null, null,
				COLUMN_SNUM + " desc", null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public void removeAll() {

		getDatabase().delete("barcode_table", null, null);

	}

	/*
	 * public Cursor getProductDetails() { String[] columns = new String[] {
	 * COLUMN_PRODUCT_ID, COLUMN_PALETTEID, COLUMN_CODE, COLUMN_NAME,
	 * COLUMN_BARCODE, COLUMN_WEIGHT }; Cursor cursor =
	 * getDatabase().query(BARCODE_TABLE, columns, null, null, null, null,
	 * null);
	 * 
	 * if (cursor != null) { cursor.moveToFirst(); } return cursor; }
	 */
	public String summarypalettecount(String productId) {
		String palettecount = "";
		String selectQuery = "SELECT DISTINCT COUNT(productId) FROM barcode_table where productId='"
				+ productId + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				palettecount = cursor.getString(cursor
						.getColumnIndex("COUNT(productId)"));

			} while (cursor.moveToNext());
		}
		Log.d("query", selectQuery);
		return palettecount;
	}

	public ArrayList<String> summaryprodsno(String Id) {
		ArrayList<String> prodsnos = new ArrayList<String>();
		String selectQuery = "SELECT * FROM barcode_table where _id='" + Id
				+ "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				String prodsnum = cursor.getString(cursor
						.getColumnIndex("snum"));
				prodsnos.add(prodsnum);
			} while (cursor.moveToNext());
		}
		return prodsnos;
	}

	public ArrayList<String> snoCountID(String productId) {
		ArrayList<String> snoCountID;
		snoCountID = new ArrayList<String>();
		String selectQuery = "SELECT _id FROM barcode_table where productId='"
				+ productId + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String id = cursor.getString(cursor.getColumnIndex("_id"));
				snoCountID.add(id);
			} while (cursor.moveToNext());
		}
		Log.d("query", selectQuery);
		return snoCountID;
	}

	public int updateSNUM(HashMap<String, String> queryValues) {
		ContentValues values = new ContentValues();
		values.put("snum", queryValues.get("snum"));
		return getDatabase().update("barcode_table", values, "_id" + " = ?",
				new String[] { queryValues.get("_id") });
	}

	public String getProductWeight(String productid) {
		double sum = 0.00;

		String strsum = "";
		Cursor cursor = getDatabase().rawQuery(
				"SELECT SUM(weight) FROM barcode_table where productId='"
						+ productid + "'", null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				sum = cursor.getDouble(0);
				strsum = (String.format("%.3f", sum));
			} while (cursor.moveToNext());
		}

		cursor.close();
		return strsum;

	}
	
	public int getBoxesSum(String productid) {
		int sum = 0;

		Cursor cursor = getDatabase().rawQuery(
				"SELECT SUM(Boxes) FROM barcode_table where productId='"
						+ productid + "'", null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				sum = cursor.getInt(0);
				
			} while (cursor.moveToNext());
		}

		cursor.close();
		return sum;

	}

	/*
	 * public int updateQty(String productid, String qty) { ContentValues cv =
	 * new ContentValues(); cv.put(COLUMN_QUANTITY, qty); return
	 * getDatabase().update(PRODUCT_TABLE, cv, "_id=" + "'" + productid + "'",
	 * null); }
	 */
	public int updateQty(String productid, String qty, int barcodestatus) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_QUANTITY, qty);
		cv.put(COLUMN_BARCODE_STATUS, barcodestatus);
		return getDatabase().update(PRODUCT_TABLE, cv,
				"_id=" + "'" + productid + "'", null);
	}

	public int updateBarcodestatus(String productid, int barcodestatus) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_BARCODE_STATUS, barcodestatus);
		return getDatabase().update(PRODUCT_TABLE, cv,
				"_id=" + "'" + productid + "'", null);
	}

	/*
	 * public int updateQty(HashMap<String, String> queryValues) { //
	 * SQLiteDatabase database = this.getWritableDatabase(); ContentValues
	 * values = new ContentValues(); values.put("quantity",
	 * queryValues.get("qty")); return getDatabase().update("sales_product",
	 * values, "product_code" + " = ?", new String[] {
	 * queryValues.get("productcode") }); }
	 */

	public ArrayList<String> getProducts(String productid) {
		Log.d("checkSlNo","-->"+productid);
		ArrayList<String> prod_arr = new ArrayList<String>();
		String selectQuery = "SELECT * FROM sales_product where sl_no ='"
				+ productid + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				prod_arr.add(cursor.getString(cursor.getColumnIndex("quantity")));
				prod_arr.add(cursor.getString(cursor
						.getColumnIndex("carton_price")));
				prod_arr.add(cursor.getString(cursor.getColumnIndex("price")));
				prod_arr.add(cursor.getString(cursor.getColumnIndex("tax_type")));
				prod_arr.add(cursor.getString(cursor
						.getColumnIndex("tax_value")));
				prod_arr.add(cursor.getString(cursor
						.getColumnIndex("piece_per_qty")));
				prod_arr.add(cursor.getString(cursor
						.getColumnIndex("item_discount")));

			} while (cursor.moveToNext());
		}
		return prod_arr;
	}

	public int updateProductValues(String productid, int cartonQty,
			double looseQty, String price, String discount, String total,
			String subTot, String tax, String net_tot) {

		ContentValues cv = new ContentValues();

		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_PRODUCT_PRICE, price);
		cv.put(COLUMN_ITEM_DISCOUNT, discount);

		// cv.put(COLUMN_PIECE_PERQTY, cartonPerQty);
		cv.put(COLUMN_TOTAL, total);
		cv.put(COLUMN_TAX, tax);
		cv.put(COLUMN_SUB_TOTAL, subTot);
		cv.put(COLUMN_NETTOTAL, net_tot);

		// cv.put(COLUMN_CARTONPRICE, cprice);

		return getDatabase().update(PRODUCT_TABLE, cv,
				"_id=" + "'" + productid + "'", null);
	}

	public int updateProductValues1(String productid, int cartonQty,
			double looseQty, String price, String discount, String total,
			String subTot, String tax, String net_tot) {

		ContentValues cv = new ContentValues();

		// cv.put(COLUMN_CARTON_QTY, cartonQty);
		// cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_PRODUCT_PRICE, price);
		cv.put(COLUMN_ITEM_DISCOUNT, discount);

		// cv.put(COLUMN_PIECE_PERQTY, cartonPerQty);
		cv.put(COLUMN_TOTAL, total);
		cv.put(COLUMN_TAX, tax);
		cv.put(COLUMN_SUB_TOTAL, subTot);
		cv.put(COLUMN_NETTOTAL, net_tot);

		// cv.put(COLUMN_CARTONPRICE, cprice);

		return getDatabase().update(PRODUCT_TABLE, cv,
				"_id=" + "'" + productid + "'", null);
	}

	public int updateProductForBatch(String productid, int cartonQty,
			double looseQty, String price, String discount, String total,
			String subTot, String tax, String net_tot, String qty, String foc) {

		Log.d("productid","productid"+productid);

		ContentValues cv = new ContentValues();

		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_QUANTITY, qty);
		cv.put(COLUMN_FOC, foc);
		cv.put(COLUMN_PRODUCT_PRICE, price);
		cv.put(COLUMN_ITEM_DISCOUNT, discount);
		cv.put(COLUMN_TOTAL, total);
		cv.put(COLUMN_TAX, tax);
		cv.put(COLUMN_SUB_TOTAL, subTot);
		cv.put(COLUMN_NETTOTAL, net_tot);

		Log.d("cvValues","-->"+cv);

		return getDatabase().update(PRODUCT_TABLE, cv,
				"_id" + "'" + productid + "'", null);
	}

	public int updateProductForConsignment(String productid, int cartonQty,
									 double looseQty, String price, String discount, String total,
									 String subTot, String tax, String net_tot, String qty, String foc) {

		Log.d("productid","productid"+productid);

		ContentValues cv = new ContentValues();

		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_QUANTITY, qty);
		cv.put(COLUMN_FOC, foc);
		cv.put(COLUMN_PRODUCT_PRICE, price);
		cv.put(COLUMN_ITEM_DISCOUNT, discount);
		cv.put(COLUMN_TOTAL, total);
		cv.put(COLUMN_TAX, tax);
		cv.put(COLUMN_SUB_TOTAL, subTot);
		cv.put(COLUMN_NETTOTAL, net_tot);

		return getDatabase().update(PRODUCT_TABLE, cv,
				"sl_no=" + "'" + productid + "'", null);
	}

	public int updateStockAdjustmentSummary(HashMap<String, String> hm) {

		String productid = hm.get("product_id");
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_CARTON_QTY, hm.get("carton_qty"));
		cv.put(COLUMN_LOOSE_QTY, hm.get("loose_qty"));
		cv.put(COLUMN_QUANTITY, hm.get("quantity"));

		return getDatabase().update(PRODUCT_TABLE, cv,
				"_id=" + "'" + productid + "'", null);
	}

	public ArrayList<ProductBarcode> getAllprodValues(String productId) {
		ArrayList<ProductBarcode> products = new ArrayList<ProductBarcode>();

		String selectQuery = "SELECT * FROM barcode_table where productId='"
				+ productId + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				ProductBarcode prodbarcode = new ProductBarcode();

				prodbarcode.setSno(cursor.getString(cursor
						.getColumnIndex("productId")));
				prodbarcode.setProductcode(cursor.getString(cursor
						.getColumnIndex("productcode")));
				prodbarcode.setSeqno(cursor.getString(cursor
						.getColumnIndex("snum")));
				prodbarcode.setBarcode(cursor.getString(cursor
						.getColumnIndex("barcode")));
				prodbarcode.setWeight(cursor.getDouble(cursor
						.getColumnIndex("weight")));

				products.add(prodbarcode);
			} while (cursor.moveToNext());
		}

		return products;
	}

	public static String getProductSno(String productId) {
		String sno = "";
		String selectQuery = "SELECT sl_no FROM sales_product where _id='"
				+ productId + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				sno = cursor.getString(cursor.getColumnIndex("sl_no"));
			} while (cursor.moveToNext());

		}
		return sno;
	}

	public static String getProdSlno(String productCode) {
		String sno = "";
		String selectQuery = "SELECT sl_no FROM sales_product where product_code='"
				+ productCode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				sno = cursor.getString(cursor.getColumnIndex("sl_no"));
			} while (cursor.moveToNext());

		}
		return sno;
	}

	public static ArrayList<String> getStockProduct(String productcode) {
		ArrayList<String> stockcount = new ArrayList<String>();
		stockcount.clear();
		String selectQuery = "SELECT carton_qty,loose_qty,quantity FROM sales_product where product_code='"
				+ productcode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String loose_qty = cursor.getString(cursor
						.getColumnIndex("loose_qty"));
				String carton_qty = cursor.getString(cursor
						.getColumnIndex("carton_qty"));
				String qty = cursor
						.getString(cursor.getColumnIndex("quantity"));

				stockcount.add(carton_qty);
				stockcount.add(loose_qty);
				stockcount.add(qty);
			} while (cursor.moveToNext());

		}
		cursor.close();
		return stockcount;

	}

	public void deleteBarcodeProduct(String productcode) {

		SQLiteDatabase db = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM barcode_table where productcode='"
				+ productcode + "'";
		Log.d("query", deleteQuery);
		db.execSQL(deleteQuery);
		db.close();

	}

	public static int updateBarcodeStatus(String sno, String productcode,
			int barcodestatus) {

		String[] args = new String[] { sno, productcode };
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_BARCODE_STATUS, barcodestatus);
		return getDatabase().update(PRODUCT_TABLE, cv,
				"sl_no=? AND product_code=?", args);
	}



	public int updateBarcodeStatus(String productid, int barcodestatus) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_BARCODE_STATUS, barcodestatus);
		return getDatabase().update(PRODUCT_TABLE, cv,
				"_id=" + "'" + productid + "'", null);
	}

	public String barcodeCQty(String productid) {
		String cqty = "";
		String selectQuery = "SELECT carton_qty FROM sales_product where _id='"
				+ productid + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				cqty = cursor.getString(cursor
						.getColumnIndex(COLUMN_CARTON_QTY));

			} while (cursor.moveToNext());
		}
		return cqty;
	}

	public ArrayList<String> distinctprodId() {
		ArrayList<String> seqno = new ArrayList<String>();

		String selectQuery = "SELECT DISTINCT productId FROM barcode_table order by _id";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String seqnum = cursor.getString(cursor
						.getColumnIndex("productId"));
				seqno.add(seqnum);
			} while (cursor.moveToNext());
		}

		return seqno;
	}

	public int updateBilldiscProductValues(String productId, String subTot) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SUB_TOTAL, subTot);
		return getDatabase().update(BILLDISC_TABLE, cv,
				"sl_no=" + "'" + productId + "'", null);
	}

	public ArrayList<String> getBarcodeStatus() {
		ArrayList<String> idArr = new ArrayList<String>();
		idArr.clear();
		String selectQuery = "SELECT _id  FROM sales_product where BarcodeStatus ='"
				+ 1 + "'";

		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String id = cursor.getString(cursor.getColumnIndex("_id"));
				idArr.add(id);
			} while (cursor.moveToNext());
		}

		return idArr;
	}

	public static int getBarcodeStatus(String productid) {
		int barcodestatus = 0;
		String selectQuery = "SELECT BarcodeStatus FROM sales_product where _id ='"
				+ productid + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				barcodestatus = cursor.getInt(cursor
						.getColumnIndex("BarcodeStatus"));
			} while (cursor.moveToNext());
		}

		return barcodestatus;
	}

	public static String getAreaCode() {
		String selectedArea = "";
		String selectQuery = "SELECT areacode FROM area_table ";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				do {
					/*
					 * String areacode = cursor.getString(cursor
					 * .getColumnIndex(SOTDatabase.COLUMN_AREACODE));
					 */
					String areacode = cursor.getString(cursor
							.getColumnIndex("areacode"));
					selectedArea += "'" + areacode + "'" + ",";
					Log.d("selectedArea---------=>", selectedArea);
				} while (cursor.moveToNext());

			}

			// if(!selectedArea.matches("")){
			selectedArea = selectedArea.substring(0, selectedArea.length() - 1);
		}

		return selectedArea;
	}

	public String getItemRemarks(String productid) {
		String itemRemarks = "";
		String selectQuery = "SELECT ItemRemarks FROM sales_product where _id='"
				+ productid + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				itemRemarks = cursor.getString(cursor
						.getColumnIndex(COLUMN_ITEM_REMARKS));

			} while (cursor.moveToNext());
		}
		return itemRemarks;
	}

	public static ArrayList<Area> checkedArea() {
		ArrayList<Area> area = new ArrayList<Area>();
		String selectQuery = "SELECT * FROM area_table ";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				/*
				 * String areacode = cursor.getString(cursor
				 * .getColumnIndex(SOTDatabase.COLUMN_AREACODE));
				 */
				String areacode = cursor.getString(cursor
						.getColumnIndex("areacode"));
				String areaname = cursor.getString(cursor
						.getColumnIndex("areaname"));
				String box = cursor.getString(cursor.getColumnIndex("box"));
				Area ara = new Area(areacode, areaname, true);
				area.add(ara);
			} while (cursor.moveToNext());

		}

		return area;
	}

	public static ArrayList<HashMap<String, String>> getArea() {
		ArrayList<HashMap<String, String>> areaArrHm = new ArrayList<HashMap<String, String>>();
		areaArrHm.clear();
		String selectQuery = "SELECT * FROM area_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				String areacode = cursor.getString(cursor
						.getColumnIndex("areacode"));
				String areaname = cursor.getString(cursor
						.getColumnIndex("areaname"));

				HashMap<String, String> areahm = new HashMap<String, String>();

				if ((areacode != null && !areacode.isEmpty())
						&& (areaname != null && !areaname.isEmpty())) {
					areahm.put(areacode, areaname);
					areaArrHm.add(areahm);
				}
			} while (cursor.moveToNext());

		}

		return areaArrHm;
	}
	
	public static ArrayList<HashMap<String, String>> getVan() {
		ArrayList<HashMap<String, String>> vanArrHm = new ArrayList<HashMap<String, String>>();
		vanArrHm.clear();
		String selectQuery = "SELECT * FROM vandriver_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				String vancode = cursor.getString(cursor
						.getColumnIndex("vancode"));
				String Description = cursor.getString(cursor
						.getColumnIndex("Description"));

				HashMap<String, String> areahm = new HashMap<String, String>();

				if ((vancode != null && !vancode.isEmpty())
						&& (Description != null && !Description.isEmpty())) {
					areahm.put(vancode, Description);
					vanArrHm.add(areahm);
				}
			} while (cursor.moveToNext());

		}

		return vanArrHm;
	}

	public static String getProductCode(String productCode) {
		String productcode = null;
		String selectQuery = "SELECT product_code FROM sales_product where product_code='"
				+ productCode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
 
				productcode = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRODUCT_CODE));

			} while (cursor.moveToNext());
		}
		cursor.close();
		return productcode;
	}
	 public static HashMap<String, String> getDBProducts(String prodcode) {

		  HashMap<String, String> hm = new HashMap<String, String>();
		  
		  String Query = "SELECT * FROM sales_product WHERE product_code='"
		    + prodcode + "'";
		  Cursor cursor = getDatabase().rawQuery(Query, null);
		  if (cursor != null && cursor.getCount() > 0) {
		   cursor.moveToFirst();
		   do {    
		    hm.put(COLUMN_CARTON_QTY, cursor.getString(cursor
		      .getColumnIndex(COLUMN_CARTON_QTY)));
		    hm.put(COLUMN_LOOSE_QTY, cursor.getString(cursor
		      .getColumnIndex(COLUMN_LOOSE_QTY)));
		    hm.put(COLUMN_QUANTITY, cursor.getString(cursor
		      .getColumnIndex(COLUMN_QUANTITY)));
		    hm.put(COLUMN_FOC, cursor.getString(cursor
		      .getColumnIndex(COLUMN_FOC)));
		    hm.put(COLUMN_PRODUCT_PRICE, cursor.getString(cursor
		      .getColumnIndex(COLUMN_PRODUCT_PRICE)));
		    hm.put(COLUMN_CARTONPRICE, cursor.getString(cursor
		      .getColumnIndex(COLUMN_CARTONPRICE)));
		    hm.put(COLUMN_EXCHANGEQTY, cursor.getString(cursor
		      .getColumnIndex(COLUMN_EXCHANGEQTY)));
		    
		    hm.put(COLUMN_ITEM_DISCOUNT, cursor.getString(cursor
		      .getColumnIndex(COLUMN_ITEM_DISCOUNT)));
		    
		    hm.put(COLUMN_TOTAL, cursor.getString(cursor
		      .getColumnIndex(COLUMN_TOTAL)));
		    
		    hm.put(COLUMN_SUB_TOTAL, cursor.getString(cursor
		      .getColumnIndex(COLUMN_SUB_TOTAL)));
		    
		    hm.put(COLUMN_TAX, cursor.getString(cursor
		      .getColumnIndex(COLUMN_TAX)));
		    
		    hm.put(COLUMN_NETTOTAL, cursor.getString(cursor
		      .getColumnIndex(COLUMN_NETTOTAL)));
		    
		    hm.put(COLUMN_TAXTYPE, cursor.getString(cursor
		      .getColumnIndex(COLUMN_TAXTYPE)));
		    
		    hm.put(COLUMN_TAXVALUE, cursor.getString(cursor
		      .getColumnIndex(COLUMN_TAXVALUE)));

		    hm.put(COLUMN_PIECE_PERQTY, cursor.getString(cursor
					   .getColumnIndex(COLUMN_PIECE_PERQTY)));
		   
		   } while (cursor.moveToNext());
		  }
		  cursor.close();
		  

		  return hm;
		 }
	 public static int updateProductBillDisc(String prodcode, String subTot) {
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_PRODUCT_CODE, prodcode);
			cv.put(COLUMN_SUB_TOTAL, subTot);
			return getDatabase().update(BILLDISC_TABLE, cv,
					"product_code=" + "'" + prodcode + "'", null);
		}
	 public static int updateProductDetails(
			   HashMap<String, String> queryValues) {

			  String productCode = queryValues.get("ProductCode");
			  ContentValues cv = new ContentValues();  
			  cv.put(COLUMN_CARTON_QTY, queryValues.get("CQty"));
			  cv.put(COLUMN_LOOSE_QTY, queryValues.get("LQty"));
			  cv.put(COLUMN_QUANTITY, queryValues.get("Qty")); 
			  cv.put(COLUMN_FOC, queryValues.get("Foc")); 
			  cv.put(COLUMN_PRODUCT_PRICE, queryValues.get("Price"));
			  cv.put(COLUMN_RETAILPRICE, queryValues.get("RetailPrice"));  
			  cv.put(COLUMN_CARTONPRICE, queryValues.get("CPrice"));
			  cv.put(COLUMN_ITEM_DISCOUNT, queryValues.get("Discount"));
			  cv.put(COLUMN_EXCHANGEQTY, queryValues.get("ExchangeQty"));
			  cv.put(COLUMN_TOTAL, queryValues.get("Total"));
			  cv.put(COLUMN_SUB_TOTAL, queryValues.get("SubTotal"));
			  cv.put(COLUMN_TAX, queryValues.get("Tax"));
			  cv.put(COLUMN_NETTOTAL, queryValues.get("NetTotal"));

			  return getDatabase().update(PRODUCT_TABLE, cv,
			    "product_code=" + "'" + productCode + "'", null);
			 }
	public static int updateStockProduct(String productcode, String cartonQty,
			String looseQty, String qty) {

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_QUANTITY, qty);
		return getDatabase().update(PRODUCT_TABLE, cv,
				"product_code=" + "'" + productcode + "'", null);
	}

	public static String getProductCodeSR(String productCode, String srno) {
		String productcode = null;
		String selectQuery = "SELECT product_code FROM sales_product where product_code='"
				+ productCode + "' AND sl_no = '" + srno + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				productcode = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRODUCT_CODE));

			} while (cursor.moveToNext());
		}
		return productcode;
	}

	public static int updateStockPrductDetail(String id, String productcode,
			String carton, String loose, String qty) {

		String[] args = new String[] { id, productcode };
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_CARTON_QTY, carton);
		cv.put(COLUMN_LOOSE_QTY, loose);
		cv.put(COLUMN_QUANTITY, qty);

		return getDatabase().update(PRODUCT_TABLE, cv,
				"_id=? AND product_code=?", args);
	}

	public static void deleteStockProduct(String productid, String productcode) {

		String deleteQuery = "DELETE FROM sales_product where _id='"
				+ productid + "' AND product_code='" + productcode + "'";
		getDatabase().execSQL(deleteQuery);
		Log.d("query", deleteQuery);

	}

	public static void storeArea(String areacode, String areaname, String box) {
		ContentValues values = new ContentValues();
		values.put("areacode", areacode);
		values.put("areaname", areaname);
		values.put("box", box);
		getDatabase().insert(AREA_TABLE, null, values);
	}

	public static int deleteArea() {
		return getDatabase().delete("area_table", null, null);
	}

	public static int deleteManualStock() {
		return getDatabase().delete(MANUAL_STOCK_TABLE, null, null);
	}

	public static int deleteSettlement() {
		return getDatabase().delete(DENOMINATION_TABLE, null, null);
	}

	public static int deleteCloseStock() {
		return getDatabase().delete(CLOSE_STOCk_TABLE, null, null);
	}

	public static ArrayList<String> batchSnoCountID() {
		ArrayList<String> snoCountID;
		snoCountID = new ArrayList<String>();
		String selectQuery = "SELECT _id FROM batch_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String id = cursor.getString(cursor.getColumnIndex("_id"));
				snoCountID.add(id);
			} while (cursor.moveToNext());
		}
		return snoCountID;
	}

	public static ArrayList<String> consignSnoCountID() {
		ArrayList<String> snoCountID;
		snoCountID = new ArrayList<String>();
		String selectQuery = "SELECT _id FROM consignment_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String id = cursor.getString(cursor.getColumnIndex("_id"));
				snoCountID.add(id);
			} while (cursor.moveToNext());
		}
		return snoCountID;
	}

	public static int updateBatchSNO(HashMap<String, String> queryValues) {

		ContentValues values = new ContentValues();
		values.put("sl_no", queryValues.get("batch_sno"));
		return getDatabase().update("batch_table", values, "_id" + " = ?",
				new String[] { queryValues.get("_id") });
	}
	
	public static int updateBatchSerialNo(HashMap<String, String> queryValues) {

		ContentValues values = new ContentValues();
		values.put("SRSlno", queryValues.get("SRSlno"));
		return getDatabase().update("batch_table", values, "product_code" + " = ?",
				new String[] { queryValues.get("product_code") });
	}

	public static String getBatchNo(String prodCode) {
		String bno = "";
		String selectQuery = "SELECT product_code FROM batch_table where product_code='"
				+ prodCode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				bno = cursor.getString(cursor.getColumnIndex("product_code"));
			} while (cursor.moveToNext());
		}
		return bno;
	}

	public static String getBatchAvlQty(String pid, String prodCode) {
		String avlQty = "";
		String selectQuery = "SELECT available_qty FROM batch_table where _id='"
				+ pid + "'" + " AND" + " product_code='" + prodCode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				avlQty = cursor.getString(cursor
						.getColumnIndex("available_qty"));
			} while (cursor.moveToNext());
		}
		return avlQty;
	}


	public static String  getConsignmentAvlQty(String pid, String prodCode,String consCode) {
		String avlQty = "";
		String selectQuery = "SELECT available_qty FROM consignment_table where consignment_no='"
				+ consCode + "'" + " AND" + " product_code='" + prodCode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				avlQty = cursor.getString(cursor
						.getColumnIndex("available_qty"));
			} while (cursor.moveToNext());
		}
		return avlQty;
	}

	public static String getBatchAvlcQty(String pid, String prodCode) {
		String avlcQty = "";
		String selectQuery = "SELECT available_carton FROM batch_table where _id='"
				+ pid + "'" + " AND" + " product_code='" + prodCode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				avlcQty = cursor.getString(cursor
						.getColumnIndex("available_carton"));
			} while (cursor.moveToNext());
		}
		return avlcQty;
	}

	public static String getConsignmentAvlcQty(String pid, String prodCode) {
		String avlcQty = "";
		String selectQuery = "SELECT available_carton FROM consignment_table where _id='"
				+ pid + "'" + " AND" + " product_code='" + prodCode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				avlcQty = cursor.getString(cursor
						.getColumnIndex("available_carton"));
			} while (cursor.moveToNext());
		}
		return avlcQty;
	}

	public static String getBatchId(String batchid) {
		String bid = "";
		String selectQuery = "SELECT batch_no FROM batch_table where _id='"
				+ batchid + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				bid = cursor.getString(cursor.getColumnIndex("batch_no"));
			} while (cursor.moveToNext());
		}
		return bid;
	}

	public static String getBatchNoSR(String prodCode, String srno) {
		String bno = "";
		String selectQuery = "SELECT product_code FROM batch_table where product_code='"
				+ prodCode + "' AND SRSlno = '" + srno + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				bno = cursor.getString(cursor.getColumnIndex("product_code"));
			} while (cursor.moveToNext());
		}
		return bno;
	}

	public static String getConsignmentStockDetail(String prodCode) {
		String bno = "";
		String selectQuery = "SELECT product_code FROM consignment_table where product_code='" + prodCode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				bno = cursor.getString(cursor.getColumnIndex("product_code"));
			} while (cursor.moveToNext());
		}
		return bno;
	}

	public static String getBatchQty(String prodcode) {
		String bqty = "";
		String selectQuery = "SELECT SUM(quantity) FROM batch_table where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				bqty = cursor.getString(cursor.getColumnIndex("SUM(quantity)"));
			} while (cursor.moveToNext());
		}
		return bqty;
	}

	public static String getConsignmentQty(String prodcode) {

		String bqty = "";
		String selectQuery = "SELECT SUM(quantity) FROM consignment_table where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				bqty = cursor.getString(cursor.getColumnIndex("SUM(quantity)"));
				Log.d("bqty","-->"+bqty);
			} while (cursor.moveToNext());
		}
		return bqty;
	}

	public static String getTotalConsignmentQty() {

		String bqty = "";
		String selectQuery = "SELECT SUM(quantity) FROM sales_product ";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				bqty = cursor.getString(cursor.getColumnIndex("SUM(quantity)"));
				Log.d("TotalQuantity","-->"+bqty);
			} while (cursor.moveToNext());
		}
		return bqty;
	}

	public static String getBatchQtySR(String prodcode, String srno) {
		String bqty = "";
		String selectQuery = "SELECT SUM(quantity) FROM batch_table where product_code='"
				+ prodcode + "' AND SRSlno = '" + srno + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				bqty = cursor.getString(cursor.getColumnIndex("SUM(quantity)"));
			} while (cursor.moveToNext());
		}
		return bqty;
	}

	public static String getConsignmentStockQty(String prodcode, String srno) {
		Log.d("getConsignmentStockQty","--?"+srno);
		String bqty = "";
		String selectQuery = "SELECT SUM(quantity) FROM consignment_table where product_code='"
				+ prodcode + "' AND SRSlno = '" + srno + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				bqty = cursor.getString(cursor.getColumnIndex("SUM(quantity)"));
			} while (cursor.moveToNext());
		}
		return bqty;
	}

	public static void updateBatchQty(int pid, boolean checkstatus) {

		String cQty = "", lQty = "", qty = "";

		if (checkstatus == true) {
			String selectQuery = "SELECT available_carton,available_loose,available_qty FROM batch_table where _id='"
					+ pid + "' LIMIT 1";

			Cursor cursor = getDatabase().rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					cQty = cursor.getString(cursor
							.getColumnIndex("available_carton"));
					lQty = cursor.getString(cursor
							.getColumnIndex("available_loose"));
					qty = cursor.getString(cursor
							.getColumnIndex("available_qty"));

				} while (cursor.moveToNext());
			}
		} else {
			cQty = "0";
			lQty = "0";
			qty = "0";
		}
		Log.d("Avaliable", cQty + "-->" + lQty + "-->" + qty);

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_CARTON_QTY, cQty);
		cv.put(COLUMN_LOOSE_QTY, lQty);
		cv.put(COLUMN_QUANTITY, qty);
		getDatabase().update(BATCH_TABLE, cv, "_id = '" + pid + "'", null);

	}
	public static void updateConsignmentQty(int pid, boolean checkstatus) {

		String cQty = "", lQty = "", qty = "";
		Log.d("checkstatus","-->"+checkstatus +" "+pid);
		if (checkstatus == true) {
			String selectQuery = "SELECT available_carton,available_loose,available_qty FROM consignment_table where _id='"
					+ pid + "' LIMIT 1";

			Cursor cursor = getDatabase().rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {

						cQty = cursor.getString(cursor
								.getColumnIndex("available_carton"));
						lQty = cursor.getString(cursor
								.getColumnIndex("available_loose"));
						qty = cursor.getString(cursor
								.getColumnIndex("available_qty"));



				} while (cursor.moveToNext());
			}
		} else {
			cQty = "0";
			lQty = "0";
			qty = "0";
		}
		Log.d("AvaliableValues", cQty + "-->" + lQty + "-->" + qty);

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_CARTON_QTY, cQty);
		cv.put(COLUMN_LOOSE_QTY, lQty);
		cv.put(COLUMN_QUANTITY, qty);
		getDatabase().update(CONSIGNMENT_STOCK_TABLE, cv, "_id = '" + pid + "'", null);

	}

	// public static HashMap<String, String> getBatchQtySum(String prodcode) {
	// String bcqty = "", blqty = "", bqty = "";
	// HashMap<String, String> hm = new HashMap<String, String>();
	// String selectQuery =
	// "SELECT SUM(carton_qty), SUM(loose_qty), SUM(quantity) FROM batch_table where product_code='"+
	// prodcode+ "'";
	// Cursor cursor = getDatabase().rawQuery(selectQuery, null);
	// if (cursor.moveToFirst()) {
	// do {
	// bcqty = cursor.getString(cursor.getColumnIndex("SUM(carton_qty)"));
	// blqty = cursor.getString(cursor.getColumnIndex("SUM(loose_qty)"));
	// bqty = cursor.getString(cursor.getColumnIndex("SUM(quantity)"));
	// } while (cursor.moveToNext());
	// }
	//
	// hm.put("carton_qty", bcqty);
	// hm.put("loose_qty", blqty);
	// hm.put("quantity", bqty);
	// return hm;
	// }

	public static String getProductId(String productCode) {
		String productid = "";
		String selectQuery = "SELECT _id FROM sales_product where product_code='"
				+ productCode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				productid = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRODUCT_ID));

			} while (cursor.moveToNext());
		}
		return productid;
	}

	public static String getSalesId(String slno) {
		String productid = null;
		String selectQuery = "SELECT _id FROM sales_product where sl_no='"
				+ slno + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				productid = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRODUCT_ID));

			} while (cursor.moveToNext());
		}
		return productid;
	}

	public static String getStockSno(String id) {
		String productid = null;
		String selectQuery = "SELECT SOSlno FROM sales_product where _id='"
				+ id + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				productid = cursor.getString(cursor
						.getColumnIndex(COLUMN_SO_SLNO));

			} while (cursor.moveToNext());
		}
		return productid;
	}

	public static String getAddDetect(String productCode) {
		String addDetect = "";
		String selectQuery = "SELECT exchange_qty FROM sales_product where product_code='"
				+ productCode + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				addDetect = cursor.getString(cursor
						.getColumnIndex(COLUMN_EXCHANGEQTY));

			} while (cursor.moveToNext());
		}
		return addDetect;
	}

	// Batch product detele
	public static boolean deleteBatchProduct(String productcode) {

		return getDatabase().delete(BATCH_TABLE,
				COLUMN_PRODUCT_CODE + "='" + productcode + "'", null) > 0;

	}

	public static boolean deleteBatchProductSR(String productcode, String srno) {

		return getDatabase().delete(
				BATCH_TABLE,
				COLUMN_PRODUCT_CODE + "='" + productcode + "' AND SRSlno ='"
						+ srno + "'", null) > 0;

	}

	public static boolean deleteConsignmentWithSR(String productcode, String srno) {

		return getDatabase().delete(
				CONSIGNMENT_STOCK_TABLE,
				COLUMN_PRODUCT_CODE + "='" + productcode + "' AND SRSlno ='"
						+ srno + "'", null) > 0;

	}

	public static boolean deleteBatchSno(String srno) {

		return getDatabase()
				.delete(BATCH_TABLE, "SRSlno ='" + srno + "'", null) > 0;

	}

	public static HashMap<String, String> getProductHave(String prodcode) {

		HashMap<String, String> hm = new HashMap<String, String>();
		String havebatch = "", haveexpiry = "";
		String Query = "SELECT have_batch,have_expiry FROM batch_table WHERE product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				havebatch = cursor.getString(cursor
						.getColumnIndex("have_batch"));
				haveexpiry = cursor.getString(cursor
						.getColumnIndex("have_expiry"));
			} while (cursor.moveToNext());
		}
		cursor.close();
		hm.put("have_batch", havebatch);
		hm.put("have_expiry", haveexpiry);

		return hm;
	}

	public static HashMap<String, String> getProductHav(String prodcode) {

		HashMap<String, String> hm = new HashMap<String, String>();
		String havebatch = "", haveexpiry = "";
		String Query = "SELECT have_batch,have_expiry FROM sales_product WHERE product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				havebatch = cursor.getString(cursor
						.getColumnIndex("have_batch"));
				haveexpiry = cursor.getString(cursor
						.getColumnIndex("have_expiry"));
			} while (cursor.moveToNext());
		}
		cursor.close();
		hm.put("have_batch", havebatch);
		hm.put("have_expiry", haveexpiry);

		return hm;
	}

	public static void storeStockAdjustmentProduct(
			HashMap<String, String> queryValues) {

		ContentValues cv = new ContentValues();

		cv.put(COLUMN_PRODUCT_SLNO, queryValues.get("slNo"));
		cv.put(COLUMN_PRODUCT_CODE, queryValues.get("ProductCode"));
		cv.put(COLUMN_PRODUCT_NAME, queryValues.get("ProductName"));
		cv.put(COLUMN_CARTON_QTY, queryValues.get("CQty"));
		cv.put(COLUMN_LOOSE_QTY, queryValues.get("LQty"));
		cv.put(COLUMN_QUANTITY, queryValues.get("Qty"));
		cv.put(COLUMN_FOC, queryValues.get("StockinHand"));
		cv.put(COLUMN_PRODUCT_PRICE, queryValues.get("Price"));
		cv.put(COLUMN_ITEM_DISCOUNT, queryValues.get("Dicount"));
		cv.put(COLUMN_PRODUCT_UOM, queryValues.get("Uom"));
		cv.put(COLUMN_PIECE_PERQTY, queryValues.get("PcsPerCarton"));
		cv.put(COLUMN_TOTAL, queryValues.get("Total"));
		cv.put(COLUMN_TAX, queryValues.get("Tax"));
		cv.put(COLUMN_NETTOTAL, queryValues.get("NetTotal"));
		cv.put(COLUMN_SUB_TOTAL, queryValues.get("SubTotal"));
		cv.put(COLUMN_TAXTYPE, queryValues.get("TaxType"));
		cv.put(COLUMN_TAXVALUE, queryValues.get("TaxPerc"));
		cv.put(COLUMN_RETAILPRICE, queryValues.get("RetailPrice"));
		cv.put(COLUMN_EXCHANGEQTY, queryValues.get("AddDetect"));
		cv.put(COLUMN_CARTONPRICE, queryValues.get("CartonPrice"));
		cv.put(COLUMN_MINIMUM_SELLING_PRICE,
				queryValues.get("MinimumSellingPrice"));
		cv.put(COLUMN_ITEM_REMARKS, queryValues.get("ItemRemarks"));
		cv.put(COLUMN_BARCODE_STATUS, 0);
		cv.put(COLUMN_SO_SLNO, queryValues.get("SOSlno"));

		getDatabase().insert(PRODUCT_TABLE, null, cv);
	}

	public static int updateStockAdjustmentProduct(
			HashMap<String, String> queryValues) {

		String productId = queryValues.get("SOSlno");
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PRODUCT_CODE, queryValues.get("ProductCode"));
		cv.put(COLUMN_PRODUCT_NAME, queryValues.get("ProductName"));
		cv.put(COLUMN_CARTON_QTY, queryValues.get("CQty"));
		cv.put(COLUMN_LOOSE_QTY, queryValues.get("LQty"));
		cv.put(COLUMN_QUANTITY, queryValues.get("Qty"));
		cv.put(COLUMN_FOC, queryValues.get("StockinHand"));
		cv.put(COLUMN_PRODUCT_PRICE, queryValues.get("Price"));
		cv.put(COLUMN_ITEM_DISCOUNT, queryValues.get("Dicount"));
		cv.put(COLUMN_PRODUCT_UOM, queryValues.get("Uom"));
		cv.put(COLUMN_PIECE_PERQTY, queryValues.get("PcsPerCarton"));
		cv.put(COLUMN_TOTAL, queryValues.get("Total"));
		cv.put(COLUMN_TAX, queryValues.get("Tax"));
		cv.put(COLUMN_SUB_TOTAL, queryValues.get("SubTotal"));
		cv.put(COLUMN_NETTOTAL, queryValues.get("NetTotal"));
		cv.put(COLUMN_CARTONPRICE, queryValues.get("CartonPrice"));
		cv.put(COLUMN_EXCHANGEQTY, queryValues.get("AddDetect"));

		return getDatabase().update(PRODUCT_TABLE, cv,
				"_id=" + "'" + productId + "'", null);
	}

	public static ArrayList<String> getquantityforstock(String productcode) {
		ArrayList<String> qtyArr = new ArrayList<String>();
		String Query = "SELECT * FROM sales_product where product_code = '"
				+ productcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				String qty = cursor
						.getString(cursor.getColumnIndex("quantity"));
				String foc = cursor.getString(cursor.getColumnIndex("foc"));
				String exchange = cursor.getString(cursor
						.getColumnIndex("exchange_qty"));

				Log.d("qty", "q " + qty);

				qtyArr.add(qty);
				qtyArr.add(foc);
				qtyArr.add(exchange);

			} while (cursor.moveToNext());
		}
		cursor.close();
		return qtyArr;
	}

	public static int updateStockReqBatchDetailProduct(String productCode,
			String batchCode, String expiryDate, String carton, String loose,
			String qty, String SR_Slno, String Remarks) {
		String where;

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_CARTON_QTY, carton);
		cv.put(COLUMN_LOOSE_QTY, loose);
		cv.put(COLUMN_QUANTITY, qty);
		cv.put(COLUMN_REMARKS, Remarks);
		if (batchCode != null && !batchCode.isEmpty()) {
			where = "product_code = '" + productCode + "' AND batch_no = '"
					+ batchCode + "' AND expiry_date = '" + expiryDate
					+ "' AND  SRSlno = '" + SR_Slno + "'";
		} else {
			where = "product_code = '" + productCode + "' AND  expiry_date = '"
					+ expiryDate + "' AND  SRSlno = '" + SR_Slno + "'";
		}
		return getDatabase().update(BATCH_TABLE, cv, where, null);

	}

	public static String getBatchQty(String BatchNo, String prodCode,
			String ExpiryDate) {
		String qty = "";
		String selectQuery;
		if (BatchNo != null && !BatchNo.isEmpty()) {
			selectQuery = "SELECT quantity FROM batch_table where product_code='"
					+ prodCode
					+ "' AND batch_no='"
					+ BatchNo
					+ "' AND expiry_date='" + ExpiryDate + "'";
		} else {
			selectQuery = "SELECT quantity FROM batch_table where product_code='"
					+ prodCode + "' AND expiry_date='" + ExpiryDate + "'";
		}

		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				qty = cursor.getString(cursor.getColumnIndex("quantity"));
			} while (cursor.moveToNext());
		}
		return qty;
	}

	public static String getBatchcQty(String BatchNo, String prodCode,
			String ExpiryDate) {
		String cQty = "";
		String selectQuery;
		if (BatchNo != null && !BatchNo.isEmpty()) {
			selectQuery = "SELECT carton_qty FROM batch_table where product_code='"
					+ prodCode
					+ "' AND batch_no='"
					+ BatchNo
					+ "' AND expiry_date='" + ExpiryDate + "'";
		} else {
			selectQuery = "SELECT carton_qty FROM batch_table where product_code='"
					+ prodCode + "' AND expiry_date='" + ExpiryDate + "'";
		}
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				cQty = cursor.getString(cursor.getColumnIndex("carton_qty"));
			} while (cursor.moveToNext());
		}
		return cQty;
	}

	public static String getBatchlQty(String BatchNo, String prodCode,
			String ExpiryDate) {
		String lQty = "";
		String selectQuery;
		if (BatchNo != null && !BatchNo.isEmpty()) {
			selectQuery = "SELECT loose_qty FROM batch_table where product_code='"
					+ prodCode
					+ "' AND batch_no='"
					+ BatchNo
					+ "' AND expiry_date='" + ExpiryDate + "'";
		} else {
			selectQuery = "SELECT loose_qty FROM batch_table where product_code='"
					+ prodCode + "' AND  expiry_date='" + ExpiryDate + "'";
		}
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				lQty = cursor.getString(cursor.getColumnIndex("loose_qty"));
			} while (cursor.moveToNext());
		}
		return lQty;
	}

	public static String getBatchQtySR(String BatchNo, String prodCode,
			String ExpiryDate, String srno) {
		String qty = "";
		String selectQuery;
		if (BatchNo != null && !BatchNo.isEmpty()) {
			selectQuery = "SELECT quantity FROM batch_table where product_code='"
					+ prodCode
					+ "' AND batch_no='"
					+ BatchNo
					+ "' AND expiry_date = '"
					+ ExpiryDate
					+ "' AND SRSlno = '"
					+ srno + "'";
		} else {
			selectQuery = "SELECT quantity FROM batch_table where product_code='"
					+ prodCode
					+ "' AND expiry_date='"
					+ ExpiryDate
					+ "' AND SRSlno = '" + srno + "'";
		}

		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				qty = cursor.getString(cursor.getColumnIndex("quantity"));
			} while (cursor.moveToNext());
		}
		return qty;
	}

	public static String getBatchcQtySR(String BatchNo, String prodCode,
			String ExpiryDate, String srno) {
		String cQty = "";
		String selectQuery;
		if (BatchNo != null && !BatchNo.isEmpty()) {
			selectQuery = "SELECT carton_qty FROM batch_table where product_code='"
					+ prodCode
					+ "' AND batch_no='"
					+ BatchNo
					+ "' AND expiry_date='"
					+ ExpiryDate
					+ "' AND SRSlno = '"
					+ srno + "'";
		} else {
			selectQuery = "SELECT carton_qty FROM batch_table where product_code='"
					+ prodCode
					+ "' AND expiry_date='"
					+ ExpiryDate
					+ "' AND SRSlno = '" + srno + "'";
		}
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				cQty = cursor.getString(cursor.getColumnIndex("carton_qty"));
			} while (cursor.moveToNext());
		}
		return cQty;
	}

	public static String getBatchlQtySR(String BatchNo, String prodCode,
			String ExpiryDate, String srno) {
		String lQty = "";
		String selectQuery;
		if (BatchNo != null && !BatchNo.isEmpty()) {
			selectQuery = "SELECT loose_qty FROM batch_table where product_code='"
					+ prodCode
					+ "' AND batch_no='"
					+ BatchNo
					+ "' AND expiry_date='"
					+ ExpiryDate
					+ "' AND SRSlno = '"
					+ srno + "'";
		} else {
			selectQuery = "SELECT loose_qty FROM batch_table where product_code='"
					+ prodCode
					+ "' AND  expiry_date='"
					+ ExpiryDate
					+ "' AND SRSlno = '" + srno + "'";
		}
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				lQty = cursor.getString(cursor.getColumnIndex("loose_qty"));
			} while (cursor.moveToNext());
		}
		return lQty;
	}

	public static double getProductQty(String prodcode) {
		double totalqty = 0.00;
		String Query = "SELECT sum(quantity) FROM sales_product where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalqty = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalqty;
	}

	public static double getCartonQty(String prodcode) {
		double carton_qty = 0.00;
		String Query = "SELECT sum(carton_qty) FROM sales_product where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				carton_qty = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return carton_qty;
	}

	public static double getLooseQty(String prodcode) {
		double loose_qty = 0.00;
		String Query = "SELECT sum(loose_qty) FROM sales_product where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				loose_qty = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return loose_qty;
	}


	public static double getProductTotalQty(String prodcode) {
		double totalqty = 0.00;
		String Query = "SELECT sum(quantity+foc+exchange_qty) FROM sales_product where product_code='"
				+ prodcode + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalqty = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalqty;
	}

	/** Expense Start **/

	public static Cursor getExpenseCursor() {

		String[] columns = new String[] { COLUMN_PRODUCT_ID, COLUMN_SNO,
				COLUMN_ACCOUNTNO, COLUMN_DESC, COLUMN_AMOUNT, COLUMN_TAXTYPE, COLUMN_TAXVALUE, COLUMN_SUB_TOTAL ,
				COLUMN_TAX, COLUMN_NETTOTAL , COLUMN_TAXNAME ,COLUMN_TAXCODE,};
		return getDatabase().query(EXPENSE_TABLE, columns, null, null, null,
				null, null);
	}

	public static Cursor getTaxCursor(){
		String[] columns = new String[] { COLUMN_ID , COLUMN_TAXCODE, COLUMN_TAXNAME, COLUMN_TAXVALUE ,COLUMN_TAXTYPE};
		return getDatabase().query(TAX_TABLE, columns, null, null, null,
				null, null);
	}


	public static void storeExpanse(HashMap<String, String> hmvalues) {

		Log.d("taxCredentials",","+hmvalues.get("tax_name"));
		Log.d("taxCredentials",","+hmvalues.get("tax_value"));
		Log.d("taxCredentials",","+hmvalues.get("tax_type"));
		Log.d("taxCredentials",","+hmvalues.get("tax_code")+"subTotal:"+hmvalues.get("subTotal"));

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_SNO, hmvalues.get("SlNo"));
		cv.put(COLUMN_ACCOUNTNO, hmvalues.get("AccountNo"));
		cv.put(COLUMN_DESC, hmvalues.get("Description"));
		cv.put(COLUMN_AMOUNT, hmvalues.get("Amount"));
		cv.put(COLUMN_TAXTYPE, hmvalues.get("tax_type"));
		cv.put(COLUMN_TAXVALUE ,hmvalues.get("tax_value"));
		cv.put(COLUMN_SUB_TOTAL ,hmvalues.get("subTotal"));
		cv.put(COLUMN_TAX ,hmvalues.get("tax"));
		cv.put(COLUMN_NETTOTAL ,hmvalues.get("netTotal"));
		cv.put(COLUMN_TAXNAME , hmvalues.get("tax_name"));
		cv.put(COLUMN_TAXCODE,hmvalues.get("tax_code"));
		getDatabase().insert(EXPENSE_TABLE, null, cv);
	}

	public static void storeTax (HashMap<String, String> hmvalues){

		ContentValues cv = new ContentValues();
		cv.put(COLUMN_ID ,hmvalues.get("SlNo"));
		cv.put(COLUMN_TAXCODE,hmvalues.get("taxcode"));
		cv.put(COLUMN_TAXNAME,hmvalues.get("taxname"));
		cv.put(COLUMN_TAXVALUE,hmvalues.get("taxperc"));
		cv.put(COLUMN_TAXTYPE,hmvalues.get("tax_type"));

		getDatabase().insert(TAX_TABLE, null, cv);
	}

	public static boolean deleteExpense(String sl_no) {

		return getDatabase().delete(EXPENSE_TABLE,
				COLUMN_PRODUCT_ID + "=" + sl_no, null) > 0;

	}

	public static String getExpenseTotalAmt() {
		String totalAmt = "";
		String Query = "SELECT sum(Amount) FROM expense_table";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalAmt = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalAmt;
	}

	public static  String getExpenseTaxName(String expnseNo){
		String taxName = "";
		String Query = "SELECT tax_name FROM expense_table where AccountNo ='"
				+ expnseNo + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				taxName = cursor.getString(cursor
						.getColumnIndex(SOTDatabase.COLUMN_TAXNAME));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return taxName;
	}

	public static String getExpenseTotalSubTot() {
		String totalAmt = "";
		String Query = "SELECT sum(sub_total) FROM expense_table";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalAmt = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalAmt;
	}

	public static String getExpenseTotalTax() {
		String totalAmt = "";
		String Query = "SELECT sum(tax) FROM expense_table";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalAmt = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalAmt;
	}

	public static String getExpenseTotalNetTot() {
		String totalAmt = "";
		String Query = "SELECT sum(net_total) FROM expense_table";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalAmt = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalAmt;
	}

	public static String getTaxCode(String taxName) {
		String totalqty = "";
		String Query = "SELECT tax_code FROM tax_table where tax_name ='"
				+ taxName + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalqty = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalqty;
	}

	public static ArrayList<ProductDetails> requestExpenseProducts() {
		ArrayList<ProductDetails> productList = new ArrayList<ProductDetails>();
		String selectQuery = "SELECT  * FROM expense_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				ProductDetails productdetail = new ProductDetails();
				// productdetail.setItemcode(cursor.getString(cursor
				// .getColumnIndex(COLUMN_ACCOUNTNO)));
				productdetail.setDescription(cursor.getString(cursor
						.getColumnIndex(COLUMN_DESC)));
				productdetail.setNettot(cursor.getString(cursor
						.getColumnIndex(COLUMN_AMOUNT)));
				productList.add(productdetail);
			} while (cursor.moveToNext());
		}
		return productList;

	}

	public static ArrayList<String> expenseIdcount() {
		ArrayList<String> snoCountID;
		snoCountID = new ArrayList<String>();
		String selectQuery = "SELECT _id FROM expense_table";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String id = cursor.getString(cursor.getColumnIndex("_id"));
				snoCountID.add(id);
			} while (cursor.moveToNext());
		}
		return snoCountID;
	}

	public static int updateExpenseSlNO(HashMap<String, String> queryValues) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_SNO, queryValues.get("SlNo"));
		return getDatabase().update("expense_table", values, "_id" + " = ?",
				new String[] { queryValues.get("_id") });
	}

	public static int updateExpenseAmount(String id, String amount) {

		ContentValues values = new ContentValues();
		values.put(COLUMN_AMOUNT, amount);
		return getDatabase().update("expense_table", values, "_id" + " = ?",
				new String[] { id });
	}
	public static void updateMultiValues (String id,String taxAmt ,String netty ,String subby , String mAmt){
		ContentValues values = new ContentValues();
		values.put(COLUMN_AMOUNT, mAmt);
		values.put(COLUMN_TAX , taxAmt);
		values.put(COLUMN_NETTOTAL , netty);
		values.put(COLUMN_SUB_TOTAL ,subby);
		getDatabase().update(EXPENSE_TABLE, values, "_id = '" + id + "'", null);
	}

	public static String getAccountNo(String acccountNo) {
		String acctNo = "";
		String selectQuery = "SELECT AccountNo FROM expense_table where AccountNo ='" + acccountNo + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				acctNo = cursor.getString(cursor.getColumnIndex(COLUMN_ACCOUNTNO));
			} while (cursor.moveToNext());
		}
		return acctNo;
	}
	
	public static String getColumnTaxtype (String id) {
		String acctNo = "";
		String selectQuery = "SELECT tax_type FROM expense_table where _id ='" + id + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				acctNo = cursor.getString(cursor.getColumnIndex(COLUMN_TAXTYPE));
			} while (cursor.moveToNext());
		}
		return acctNo;
	}

	public static String getColumnTaxValue (String id) {
		String acctNo = "";
		String selectQuery = "SELECT tax_value FROM expense_table where _id ='" + id + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				acctNo = cursor.getString(cursor.getColumnIndex(COLUMN_TAXVALUE));
			} while (cursor.moveToNext());
		}
		return acctNo;
	}
	
	public static double getExpenseAmt(String acccountNo) {
		double totalAmt = 0.00;
		String Query = "SELECT sum(Amount) FROM expense_table where AccountNo ='" + acccountNo + "'";
		Cursor cursor = getDatabase().rawQuery(Query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				totalAmt = cursor.getDouble(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return totalAmt;
	}

	/** Expense End **/
	
	/** Van driver start **/
	
	public static void storeVandriver(String code, String name) {
		ContentValues values = new ContentValues();
		
		String  db_id = "";
		String selectQuery = "SELECT sl_no FROM vandriver_table WHERE sl_no = '1'";
		Log.d("selectQuery", selectQuery);
	    Cursor cursor = null;
		cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				db_id = cursor.getString(cursor.getColumnIndex("sl_no"));
			} while (cursor.moveToNext());
		}
		
		values.put("sl_no", "1");
		values.put("vancode", code);
		values.put("Description", name);
		
		if (db_id != null && !db_id.isEmpty()) {
			String where = "sl_no = '1'";
			getDatabase().update(VANDRIVER_TABLE, values, where, null);
		} else {;
		getDatabase().insert(VANDRIVER_TABLE, null, values);
		}	
		
	}
	
	public static String getVandriver() {
		String code = "";
		String selectQuery = "SELECT vancode FROM vandriver_table where sl_no = '1'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				code = cursor.getString(cursor
						.getColumnIndex(COLUMN_VANCODE));

			} while (cursor.moveToNext());
		}
		return code;
	}
	public static String getVandriverName(String vanCode) {
		  String description = "";
		  String selectQuery = "SELECT Description FROM vandriver_table where sl_no = '1'  AND vancode='" + vanCode + "'";
		  Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		  if (cursor.moveToFirst()) {
		   do {

		    description = cursor.getString(cursor
		      .getColumnIndex(COLUMN_DESC));

		   } while (cursor.moveToNext());
		  }
		  return description;
		 }
	public static int deleteVan() {
		return getDatabase().delete("vandriver_table", null, null);
	}
	
	/** Van driver End **/

	public static double getStockQty(String prodcode) {
		  double stockqty = 0.00;
		  String Query = "SELECT QtyOnHand FROM sales_product where product_code='"+ prodcode + "'";
		  Cursor cursor = getDatabase().rawQuery(Query, null);
		  if (cursor != null && cursor.getCount() > 0) {
		   cursor.moveToFirst();
		   do {
		    stockqty = cursor.getDouble(0);
		   } while (cursor.moveToNext());
		  }
		  cursor.close();
		  return stockqty;
		 }
	
	public static void storeMerchandiseImage(int slNo,String type,int status,String image,String remarks) {
		  ContentValues values = new ContentValues();
		  values.put("SlNo", slNo);
		  values.put("Type", type);
		  values.put("status", status); 
		  values.put("Image", image);
		  values.put("Remarks", remarks);
		  getDatabase().insert(MERCHANDISE_TABLE, null, values);
		  
		 }
	public static ArrayList<Product> getMerchandiseImage(String type){
		ArrayList<Product> merchandiseImage = new ArrayList<Product>();
		 String Query;
		 Query = "SELECT * FROM merchandise_table";
		if(type!=null && !type.isEmpty()){
			 Query += " where Type='"+ type + "'";
		}
			Query += " ORDER BY _id DESC";
			Log.d("selectQuery", Query);
		  Cursor cursor = getDatabase().rawQuery(Query, null);
		  if (cursor != null && cursor.getCount() > 0) {
		   cursor.moveToFirst();
		   do {
			  Product product = new Product();
			  product.setSlNo(cursor.getString(cursor
						.getColumnIndex(COLUMN_SNO)));
			  product.setType(cursor.getString(cursor
						.getColumnIndex(COLUMN_TYPE)));
			  product.setStatus(cursor.getString(cursor
						.getColumnIndex(COLUMN_STATUS)));
			  product.setProductImage(cursor.getString(cursor
						.getColumnIndex(COLUMN_IMAGE)));
			  
			  merchandiseImage.add(product);

		   } while (cursor.moveToNext());
		  }
		  cursor.close();
		  return merchandiseImage;
	}
	
	public static int deleteAllMerchandiseImage() {
		return getDatabase().delete("merchandise_table", null, null);
	}
	
	public static Cursor getMerchandiseAllImage(String type,String status) {
		String where = null;
		/*if(type!=null && !type.isEmpty()){
			where = "Type= '" + type + "' AND status='" + status + "'";
		}*/	
		if(type!=null && !type.isEmpty()){
		 where = "Type= '" + type + "'";
		}  
		if(status!=null && !status.isEmpty()){
		where += " AND status='"+ status + "'";
		}
	   Log.d("where", ""+where);
		String[] columns = new String[] { COLUMN_ID, COLUMN_SNO, COLUMN_TYPE,COLUMN_STATUS, COLUMN_IMAGE, COLUMN_REMARKS };		
		return getDatabase().query(MERCHANDISE_TABLE, columns,
				where, null, null, null, null);
	}
	public static int maxSlno() {
		int sno = 0;
		String selectQuery = "SELECT MAX(SlNo) FROM merchandise_table";		
		Cursor cursor =  getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				sno = cursor.getInt(cursor.getColumnIndex("MAX(SlNo)"));
			} while (cursor.moveToNext());
		}
		return sno;
	}
	public static void deleteMerchandiseImage(String slno, String type) {

		String deleteQuery = "DELETE FROM merchandise_table where SlNo='"
				+ slno + "' AND Type='" + type + "'";
		getDatabase().execSQL(deleteQuery);
		Log.d("query", deleteQuery);

	}
	public static void deleteMerchandiseSign(String type) {

		String deleteQuery = "DELETE FROM merchandise_table where Type='"
				+ type + "'";
		getDatabase().execSQL(deleteQuery);
		Log.d("query", deleteQuery);

	}
	public static String getStatus(String slno, String type) {
		String mStatus = "";
		  String selectQuery = "SELECT status FROM merchandise_table where Type='"+ type + "' AND SlNo = '"+slno+"'";	  
		  
		  
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				mStatus = cursor.getString(cursor
						.getColumnIndex(COLUMN_STATUS));

			} while (cursor.moveToNext());
		}
		Log.d("mStatus", ""+mStatus);
		return mStatus;
	}
	public static String getSignature(String type) {
		String mSign = "";
		  String selectQuery = "SELECT * FROM merchandise_table where Type='"+ type + "'";		  
		  Log.d("selectQuery", ""+selectQuery);
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				mSign = cursor.getString(cursor
						.getColumnIndex(COLUMN_IMAGE));

			} while (cursor.moveToNext());
		}
		Log.d("mSign", ""+mSign);
		return mSign;
	}
	
	public static String getType(String type) {
		String mType = "";
		  String selectQuery = "SELECT Type FROM merchandise_table where Type='"+ type + "'";		  
		  
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				mType = cursor.getString(cursor
						.getColumnIndex(COLUMN_TYPE));

			} while (cursor.moveToNext());
		}
		return mType;
	}	
	public static int updateMerchandiseSign(String type,String sign) {
		String where = "Type = '"+type+"'";
		ContentValues values = new ContentValues();
		values.put(COLUMN_IMAGE, sign);	
		Log.d("type", ""+type);
		return getDatabase().update("merchandise_table",values,where,null);
	}
	public static int updateStatus(String slno,String type) {
		String where = "SlNo = '"+slno+"' AND Type = '"+type+"'";
		ContentValues values = new ContentValues();
		values.put(COLUMN_STATUS, 1);	
		Log.d("slno", ""+slno);
		return getDatabase().update("merchandise_table",values,where,null);
	}	
	public static void deleteAllMerchandiseImage(int status) {

		String deleteQuery = "DELETE FROM merchandise_table where status='" + status + "'";
		getDatabase().execSQL(deleteQuery);
		Log.d("query", deleteQuery);

	}
	
	
/** Stroe UsertackingMaster  Start**/
	
	public static Cursor getuserTrackingMasterCursor() {

		String[] columns = new String[] { COLUMN_PRODUCT_ID, COLUMN_COMPANYCODE, COLUMN_USERNAME, COLUMN_CURRENTLOCATIONTRACKING, COLUMN_ROUTETRACKING, 
				COLUMN_TRACKINGINTERVAL, COLUMN_MON, COLUMN_TUE, COLUMN_WED, COLUMN_THU, COLUMN_FRI, COLUMN_SAT, COLUMN_SUN,
				COLUMN_MONFROMTIME, COLUMN_MONTOTIME, COLUMN_TUEFROMTIME, COLUMN_TUETOTIME, COLUMN_WEDFROMTIME, COLUMN_WEDTOTIME,
				COLUMN_THUFROMTIME, COLUMN_THUTOTIME, COLUMN_FRIFROMTIME, COLUMN_FRITOTIME,
				COLUMN_SATFROMTIME, COLUMN_SATTOTIME, COLUMN_SUNFROMTIME, COLUMN_SUNTOTIME };
		return getDatabase().query(USERTACKING_MASTER_TABLE, columns, null, null, null, null, null);
	}
	
	public static String store_usertracking(JSONArray jsonArray) {
		String mResult = "";
		database.beginTransaction();
		try {
			
			int len = jsonArray.length();
			if(len>0){
			for (int i = 0; i < len; i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				String db_sno = "";
				
				String selectQuery = "SELECT SlNo FROM usertracking_master_table WHERE SlNo = '1'";
//				Log.d("selectQuery", selectQuery);
			     Cursor cursor = null;
				cursor = getDatabase().rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					do {
						db_sno = cursor.getString(cursor.getColumnIndex("SlNo"));
					} while (cursor.moveToNext());
				}
				
				ContentValues cv = new ContentValues();
				cv.put(COLUMN_SNO, "1");
				cv.put(COLUMN_COMPANYCODE, object.getString("CompanyCode"));
				cv.put(COLUMN_USERNAME, object.getString("UserName"));
				cv.put(COLUMN_CURRENTLOCATIONTRACKING, object.getString("CurrentLocationTracking"));
				cv.put(COLUMN_ROUTETRACKING, "");//object.getString("RouteTracking")
				cv.put(COLUMN_TRACKINGINTERVAL, object.getString("TrackingInterval"));
				
				cv.put(COLUMN_MON, object.getString("Mon"));
				cv.put(COLUMN_TUE, object.getString("Tue"));
				cv.put(COLUMN_WED, object.getString("Wed"));
				cv.put(COLUMN_THU, object.getString("Thu"));
				cv.put(COLUMN_FRI, object.getString("Fri"));
				cv.put(COLUMN_SAT, object.getString("Sat"));
				cv.put(COLUMN_SUN, object.getString("Sun"));
				
				cv.put(COLUMN_MONFROMTIME, object.getString("MonFromTime"));
				cv.put(COLUMN_MONTOTIME, object.getString("MonToTime"));
				cv.put(COLUMN_TUEFROMTIME, object.getString("TueFromTime"));
				cv.put(COLUMN_TUETOTIME, object.getString("TueToTime"));
				cv.put(COLUMN_WEDFROMTIME, object.getString("WedFromTime"));
				cv.put(COLUMN_WEDTOTIME, object.getString("WedToTime"));
				cv.put(COLUMN_THUFROMTIME, object.getString("ThuFromTime"));
				cv.put(COLUMN_THUTOTIME, object.getString("ThuToTime"));
				cv.put(COLUMN_FRIFROMTIME, object.getString("FriFromTime"));
				cv.put(COLUMN_FRITOTIME, object.getString("FriToTime"));
				cv.put(COLUMN_SATFROMTIME, object.getString("SatFromTime"));
				cv.put(COLUMN_SATTOTIME, object.getString("SatToTime"));
				cv.put(COLUMN_SUNFROMTIME, object.getString("SunFromTime"));
				cv.put(COLUMN_SUNTOTIME, object.getString("SunToTime"));
				
				if (db_sno != null && !db_sno.isEmpty()) {
					String where = "SlNo = '1'";
					getDatabase().update(USERTACKING_MASTER_TABLE, cv, where, null);
					Log.d("userTrackingMaster db", "update");
				} else {
					getDatabase().insert(USERTACKING_MASTER_TABLE, null, cv);
					Log.d("userTrackingMaster db", "insert");
				}
				if(cursor != null)
					 cursor.close();
			}
			mResult = "done";
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("Error in transaction", e.toString());
		} finally {
			 
			database.endTransaction();
		}
		return mResult;
	}
	
	public static String getTrackingInterval(String sno) {
		String trackingInterval = "";
		String selectQuery = "SELECT TrackingInterval FROM usertracking_master_table where SlNo ='" + sno + "'";
		Log.d("selectQuery", selectQuery);
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				trackingInterval = cursor.getString(cursor.getColumnIndex("TrackingInterval"));
			} while (cursor.moveToNext());
		}

		return trackingInterval;
	}
	
	public static String getDayofweek(String day) {
		String mday = "";
		String selectQuery = "SELECT * FROM usertracking_master_table where SlNo ='1'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				mday = cursor.getString(cursor.getColumnIndex(day));
				Log.d("DB Day", day+"->"+mday);
			} while (cursor.moveToNext());
		}

		return mday;
	}
	/** Store UsertackingMaster  End**/
	
	/** DeliveryVerification  Start**/
	 
	 public static void storeDeliveryVerification(ArrayList<HashMap<String, String>>  mValue,String mIsQtyVerified) {
		 
	  if(mValue.size()>0){
	   for(int i=0;i<mValue.size();i++){
	   HashMap<String, String> hashvalue = new HashMap<String, String>();
	   hashvalue = mValue.get(i);
	   ContentValues cv = new ContentValues();
	   
	   cv.put(COLUMN_PRODUCT_SLNO, hashvalue.get("slNo"));
	    cv.put(COLUMN_PRODUCT_CODE, hashvalue.get("ProductCode"));
	    cv.put(COLUMN_PRODUCT_NAME, hashvalue.get("ProductName"));
	    cv.put(COLUMN_PIECE_PERQTY, hashvalue.get("PcsPerCarton"));
	    cv.put(COLUMN_RETAILPRICE, hashvalue.get("RetailPrice"));
	    cv.put(COLUMN_PRODUCT_PRICE, hashvalue.get("Price"));
	    cv.put(COLUMN_ITEM_DISCOUNT, hashvalue.get("ItemDiscount"));
	    cv.put(COLUMN_TAXTYPE, hashvalue.get("TaxType"));
	    cv.put(COLUMN_TAXVALUE, hashvalue.get("TaxPerc"));
	    cv.put(COLUMN_EXCHANGEQTY, hashvalue.get("ExchangeQty"));
	    cv.put(COLUMN_CARTONPRICE, hashvalue.get("CartonPrice"));
	    cv.put(COLUMN_DV_ORIGINAL_CQTY, hashvalue.get("OriginalCQty"));
	    cv.put(COLUMN_DV_ORIGINAL_QTY, hashvalue.get("OriginalQty"));
	    cv.put(COLUMN_PRODUCT_UOM, "");
	    
	    if(mIsQtyVerified.matches("1")){
	     cv.put(COLUMN_CARTON_QTY, hashvalue.get("CQty"));
	     cv.put(COLUMN_LOOSE_QTY, hashvalue.get("LQty"));
	     cv.put(COLUMN_QUANTITY, hashvalue.get("Qty"));
	     cv.put(COLUMN_FOC, hashvalue.get("FOCQty"));
	     cv.put(COLUMN_TOTAL, hashvalue.get("Total"));
	     cv.put(COLUMN_TAX, hashvalue.get("Tax"));
	     cv.put(COLUMN_NETTOTAL, hashvalue.get("NetTotal"));
	     cv.put(COLUMN_SUB_TOTAL, hashvalue.get("SubTotal"));
	    }else{
	     cv.put(COLUMN_CARTON_QTY, "0");
	     cv.put(COLUMN_LOOSE_QTY, "0");
	     cv.put(COLUMN_QUANTITY, "0");
	     cv.put(COLUMN_FOC, "0");
	     cv.put(COLUMN_TOTAL, "0");
	     cv.put(COLUMN_TAX, "0");
	     cv.put(COLUMN_NETTOTAL, "0");
	     cv.put(COLUMN_SUB_TOTAL, "0");
	    }
	 
	   getDatabase().insert(PRODUCT_TABLE, null, cv);
	   }
	  }
	 }
	 
	 public static int updateDeliveryVerification(String id, double cartonQty, double qty, double total,
			   String tax, String subTot, String net_tot) {
			  ContentValues cv = new ContentValues();
			  cv.put(COLUMN_CARTON_QTY, cartonQty);
			  cv.put(COLUMN_QUANTITY, qty);
			  cv.put(COLUMN_TOTAL, total);
			  cv.put(COLUMN_TAX, tax);
			  cv.put(COLUMN_SUB_TOTAL, subTot);
			  cv.put(COLUMN_NETTOTAL, net_tot);


			  return getDatabase().update(PRODUCT_TABLE, cv, "_id=" + "'" + id + "'", null);
			 }
	 
	 /**  DeliveryVerification  End**/
	  public static void storeSODetailProduct(HashMap<String, String> queryValues) {
		   ContentValues values = new ContentValues();
		   values.put(COLUMN_PRODUCT_SLNO, queryValues.get("slNo"));
		   values.put(COLUMN_PRODUCT_CODE, queryValues.get("ProductCode"));
		   values.put(COLUMN_QUANTITY, queryValues.get("Qty"));

		   getDatabase().insert(SO_DETAIL_TABLE, null, values);

		  }
		  public static int getQuantity(String slno,String productCode) {
		   int qty = 0;
		   String selectQuery = "SELECT quantity FROM so_detail_table where sl_no ='"
		     +  slno + "' AND product_code = '"+ productCode +"'";
		   Log.d("selectQuery", ""+selectQuery);
		   Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		   if (cursor.moveToFirst()) {
		    do {
		     qty = cursor.getInt(cursor
		       .getColumnIndex("quantity"));
		     Log.d("Quantity","-->"+qty);
		    } while (cursor.moveToNext());
		   }

		   return qty;
		  }
		  public static String checkDoQtyWithSo(String slno,String productCode) {
			   String productcode = "";
			   String selectQuery = "SELECT product_code FROM so_detail_table where sl_no ='"
			     +  slno + "' AND product_code = '"+ productCode +"'";
			   Log.d("selectQuery", ""+selectQuery);
			   Cursor cursor = getDatabase().rawQuery(selectQuery, null);
			   if (cursor.moveToFirst()) {
			    do {
			     productcode = cursor.getString(cursor
			       .getColumnIndex("product_code"));
			    } while (cursor.moveToNext());
			   }

			   return productcode;
			  }
		  public static int updateQuantity(int sl_no,
		    String qty,String productCode) {
		   ContentValues cv = new ContentValues();
		   cv.put(COLUMN_PRODUCT_CODE, productCode);
		   cv.put(COLUMN_STOCK, qty);

		   return getDatabase().update(PRODUCT_TABLE, cv,
		     "_id=" + "'" + sl_no + "' AND product_code = '"+ productCode +"'", null);
		  }
		  public static Cursor getSODetailQuantity() {
		   String[] columns = new String[] { COLUMN_PRODUCT_SLNO, COLUMN_PRODUCT_CODE, COLUMN_QUANTITY };
		   return getDatabase().query(SO_DETAIL_TABLE, columns, null, null, null, null, null);
		  }
		  //SO To Do Delete SODetailQty.
		  //EditDo No NeedTo Delete.
		  public static int deleteSODetailQuantity() {
		   return getDatabase().delete("so_detail_table", null, null);
		  }
		/*  public static double getTotalQty(String column) {
		   double totalqty = 0.00;
		   String Query = "SELECT sum("+ column +") FROM sales_product";  
		   Cursor cursor = getDatabase().rawQuery(Query, null);
		   if (cursor != null && cursor.getCount() > 0) {
		    cursor.moveToFirst();
		    do {
		     totalqty = cursor.getDouble(0);
		    } while (cursor.moveToNext());
		   }
		   cursor.close();
		   return totalqty;
		  }*/
		  
		  public static Cursor getCursorForId(String id) {

			    String[] columns = new String[] { COLUMN_PRODUCT_SLNO,
			      COLUMN_PRODUCT_ID, COLUMN_PRODUCT_CODE, COLUMN_PRODUCT_NAME,
			      COLUMN_CARTON_QTY, COLUMN_LOOSE_QTY, COLUMN_QUANTITY,
			      COLUMN_ORIGINAL_QTY, COLUMN_RETURN_CARTON, COLUMN_RETURN_LOOSE,
			      COLUMN_RETURN_QTY, COLUMN_FOC, COLUMN_PRODUCT_PRICE,
			      COLUMN_ITEM_DISCOUNT, COLUMN_PRODUCT_UOM, COLUMN_PIECE_PERQTY,
			      COLUMN_TOTAL, COLUMN_RETURN_SUBTOTAL, COLUMN_RETURN_TAX,
			      COLUMN_RETURN_NETTOTAL, COLUMN_TAX, COLUMN_NETTOTAL,
			      COLUMN_TAXTYPE, COLUMN_TAXVALUE, COLUMN_RETAILPRICE,
			      COLUMN_SUB_TOTAL, COLUMN_EXCHANGEQTY, COLUMN_CARTONPRICE,
			      COLUMN_MINIMUM_SELLING_PRICE,COLUMN_MINIMUM_CARTON_SELLING_PRICE, COLUMN_ITEM_REMARKS,
			      COLUMN_BARCODE_STATUS, COLUMN_SO_SLNO, COLUMN_HAVE_BATCH,
			      COLUMN_DV_ORIGINAL_CQTY, COLUMN_DV_ORIGINAL_QTY,
			      COLUMN_HAVE_EXPIRY , COLUMN_STOCK };
			    return getDatabase().query(PRODUCT_TABLE, columns, "_id= '" + id + "' LIMIT 1", null, null, null, null);
			   }
		  public static int maxSOSlno() {
			    int sno = 0;
			    String selectQuery = "SELECT MAX(SOSlno) FROM sales_product";  
			    Cursor cursor =  getDatabase().rawQuery(selectQuery, null);
			    if (cursor.moveToFirst()) {
			     do {
			      sno = cursor.getInt(cursor.getColumnIndex("MAX(SOSlno)"));
			     } while (cursor.moveToNext());
			    }
			    return sno;
			   }
		  public static int getSOSLNO(int slno,String productCode) {
		      int soslno = 0;
		      String selectQuery = "SELECT SOSlno FROM sales_product where sl_no ='"
		        +  slno + "' AND product_code = '"+ productCode +"'";
		      Log.d("selectQuery", ""+selectQuery);
		      Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		      if (cursor.moveToFirst()) {
		       do {
		        soslno = cursor.getInt(cursor
		          .getColumnIndex(COLUMN_SO_SLNO));
		       } while (cursor.moveToNext());
		      }

		      return soslno;
		     }
		  public static Cursor getCursorForEditProduct(String id) {

			    String[] columns = new String[] { COLUMN_PRODUCT_SLNO,
			      COLUMN_PRODUCT_ID, COLUMN_PRODUCT_CODE, COLUMN_PRODUCT_NAME,
			      COLUMN_CARTON_QTY, COLUMN_LOOSE_QTY, COLUMN_QUANTITY,
			      COLUMN_ORIGINAL_QTY, COLUMN_RETURN_CARTON, COLUMN_RETURN_LOOSE,
			      COLUMN_RETURN_QTY, COLUMN_FOC, COLUMN_PRODUCT_PRICE,
			      COLUMN_ITEM_DISCOUNT, COLUMN_PRODUCT_UOM, COLUMN_PIECE_PERQTY,
			      COLUMN_TOTAL, COLUMN_RETURN_SUBTOTAL, COLUMN_RETURN_TAX,
			      COLUMN_RETURN_NETTOTAL, COLUMN_TAX, COLUMN_NETTOTAL,
			      COLUMN_TAXTYPE, COLUMN_TAXVALUE, COLUMN_RETAILPRICE,
			      COLUMN_SUB_TOTAL, COLUMN_EXCHANGEQTY, COLUMN_CARTONPRICE,
			      COLUMN_MINIMUM_SELLING_PRICE,COLUMN_MINIMUM_CARTON_SELLING_PRICE, COLUMN_ITEM_REMARKS,
			      COLUMN_BARCODE_STATUS, COLUMN_SO_SLNO, COLUMN_HAVE_BATCH,
			      COLUMN_DV_ORIGINAL_CQTY, COLUMN_DV_ORIGINAL_QTY,
			      COLUMN_HAVE_EXPIRY , COLUMN_STOCK };
			    return getDatabase().query(PRODUCT_TABLE, columns, "_id = '" + id + "'", null, null, null, null);
			   }

	//insert query

	public static void storeAttribute(String slno,String productCode,String productName,ArrayList<Attribute> mAttribute) {

		ContentValues cv = new ContentValues();
		Log.d("attributes","-->"+ mAttribute.size());
		for(Attribute rowItem1 : mAttribute){
			cv.put(COLUMN_PRODUCT_SLNO, slno);
			cv.put(COLUMN_PRODUCT_NAME, productName);
			cv.put(COLUMN_PRODUCT_CODE, productCode);
			cv.put(COLUMN_COLOR_CODE, rowItem1.getCode());
			cv.put(COLUMN_COLOR_NAME, rowItem1.getName());
			cv.put(COLUMN_SIZE_CODE,  rowItem1.getSizecode());
			cv.put(COLUMN_SIZE_NAME,  rowItem1.getSizename());
			String qtySize = rowItem1.getSizeQty();
			cv.put(COLUMN_QUANTITY, rowItem1.getSizeQty());
			cv.put(COLUMN_FLAG, String.valueOf(rowItem1.isSelected()));


			Log.d("attribute_items","insert-->"+cv.toString());

			getDatabase().insert(ATTRIBUTE_TABLE, null, cv);
		}

	}

	public static void storeAttribute(String slno,String productCode,String colorCode,String sizeCode,
									  String colorName,String sizeName,String qty) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_PRODUCT_SLNO, slno);
		cv.put(COLUMN_PRODUCT_NAME, "");
		cv.put(COLUMN_PRODUCT_CODE, productCode);
		cv.put(COLUMN_COLOR_CODE,colorCode );
		cv.put(COLUMN_COLOR_NAME, colorName);
		cv.put(COLUMN_SIZE_CODE,  sizeCode);
		cv.put(COLUMN_SIZE_NAME, sizeName);
		cv.put(COLUMN_QUANTITY, qty);
		Log.d("getqty",qty);
		Log.d("slno",slno);
		double quantity = qty.equals("") ? 0 : Double.valueOf(qty);
		if(quantity>0){
			cv.put(COLUMN_FLAG, "true");
		}else{
			cv.put(COLUMN_FLAG, "false");
		}
		Log.d("attribute_itemvalue","insert-->"+cv.toString());
		getDatabase().insert(ATTRIBUTE_TABLE, null, cv);
	}

//	public static void storeAttribute(String slno,String productCode,String colorCode,String sizeCode,
//									  String colorName,String sizeName,String qty) {
//		ContentValues cv = new ContentValues();
//		cv.put(COLUMN_PRODUCT_SLNO, slno);
//		cv.put(COLUMN_PRODUCT_NAME, "");
//		cv.put(COLUMN_PRODUCT_CODE, productCode);
//		cv.put(COLUMN_COLOR_CODE,colorCode );
//		cv.put(COLUMN_COLOR_NAME, colorName);
//		cv.put(COLUMN_SIZE_CODE,  sizeCode);
//		cv.put(COLUMN_SIZE_NAME, sizeName);
//		cv.put(COLUMN_QUANTITY, qty);
//		double quantity = qty.equals("") ? 0 : Double.valueOf(qty);
//		if(quantity>0){
//			cv.put(COLUMN_FLAG, "true");
//		}else{
//			cv.put(COLUMN_FLAG, "false");
//		}
//		getDatabase().insert(ATTRIBUTE_TABLE, null, cv);
//
//	}

//update query

/*	public static void  updateAttribute(String slno,String productCode,ArrayList<Attribute> mAttribute) {
		ContentValues cv = new ContentValues();
		for(Attribute rowItem1 : mAttribute){
			String colorcode=rowItem1.getCode();
			String sizecode=rowItem1.getSizecode();
			String qtySize = rowItem1.getSizeQty();
			cv.put(COLUMN_QUANTITY, qtySize);
			String qty =rowItem1.getSizeQty();
			int iQty = Integer.valueOf(qty);
			cv.put(COLUMN_FLAG, String.valueOf(rowItem1.isSelected()));
			Log.d("update attribute_items",cv.toString());
			if(iQty>0){
				getDatabase().update(ATTRIBUTE_TABLE, cv, "sl_no=" +"'" + slno + "' AND product_code=" + "'" + productCode + "' AND color_code = " + "'" + colorcode + "' AND size_code = " + "'" + sizecode + "'",
						null);
			}
		}

	}*/


//getcolumns data query

	public static Cursor getAttributeCursor() {

		String[] columns = new String[] { COLUMN_PRODUCT_SLNO,COLUMN_PRODUCT_NAME,
				COLUMN_PRODUCT_CODE, COLUMN_COLOR_CODE, COLUMN_COLOR_NAME,
				COLUMN_SIZE_CODE, COLUMN_SIZE_NAME, COLUMN_QUANTITY,COLUMN_FLAG
		};
		return getDatabase().query(ATTRIBUTE_TABLE, columns,
				null, null, null, null, null);
	}

//delete query

	public static void deleteAttributeProducts(String slno,String productcode) {

		String deleteQuery = "DELETE FROM attribute_table where product_code='"
				+ productcode + "' AND sl_no = '" + slno
				+ "'";
		Log.d("query", deleteQuery);

		getDatabase().execSQL(deleteQuery);

	}

	public static int deleteAttribute() {
		return getDatabase().delete("attribute_table", null, null);

	}



//get values
//public static void  updateAttribute(String slno,String productCode,ArrayList<Attribute> mAttributeArr) {
//		ContentValues cv = new ContentValues();
//		for(Attribute mAttribute : mAttributeArr){
//			String colorCode = mAttribute.getCode();
//			String colorName = mAttribute.getName();
//			String sizeCode = mAttribute.getSizecode();
//			String sizeName = mAttribute.getSizename();
//			String qtySize = mAttribute.getSizeQty();
//			cv.put(COLUMN_QUANTITY, qtySize);
//			String qty = mAttribute.getSizeQty();
//			int iQty = Integer.valueOf(qty);
//			cv.put(COLUMN_FLAG, String.valueOf(mAttribute.isSelected()));
//			Log.d("update attribute_items",cv.toString());
//			if(iQty>0){
//				String product_code =  getAttributeProduct(productCode,colorCode,sizeCode,slno);
//				if(product_code!=null && !product_code.isEmpty()){
//					String where = "sl_no = '" + slno + "' AND product_code = '" + productCode + "' AND color_code = '"+ colorCode +"' AND size_code = '"+ sizeCode +"'";
//					getDatabase().update(ATTRIBUTE_TABLE,cv,where,null);
//				}else{
//					cv.put(COLUMN_PRODUCT_SLNO, slno);
//					cv.put(COLUMN_PRODUCT_NAME, "");
//					cv.put(COLUMN_PRODUCT_CODE, productCode);
//					cv.put(COLUMN_COLOR_CODE,colorCode);
//					cv.put(COLUMN_COLOR_NAME, colorName);
//					cv.put(COLUMN_SIZE_CODE,  sizeCode);
//					cv.put(COLUMN_SIZE_NAME, sizeName);
//					getDatabase().insert(ATTRIBUTE_TABLE, null, cv);
//				}
//			}
//		}
//	}

	public static void  updateAttribute(String slno,String productCode,ArrayList<Attribute> mAttributeArr) {
		ContentValues cv = new ContentValues();
		for(Attribute mAttribute : mAttributeArr){
			String colorCode = mAttribute.getCode();
			String colorName = mAttribute.getName();
			String sizeCode = mAttribute.getSizecode();
			String sizeName = mAttribute.getSizename();
			String qtySize = mAttribute.getSizeQty();
			Log.d("qtySizevalue",qtySize);
			cv.put(COLUMN_QUANTITY, qtySize);
			String qty = mAttribute.getSizeQty();
			int iQty = Integer.valueOf(qty);
			cv.put(COLUMN_FLAG, String.valueOf(mAttribute.isSelected()));
			Log.d("update attribute_items",cv.toString());
			if(iQty>=0){
				String product_code =  getAttributeProduct(productCode,colorCode,sizeCode,slno);
				//if(product_code!=null && !product_code.isEmpty()){

					String where = "sl_no = '" + slno + "' AND product_code = '" + productCode + "' AND color_code = '"+ colorCode +"' AND size_code = '"+ sizeCode +"'";
					getDatabase().update(ATTRIBUTE_TABLE,cv,where,null);
				//}else{

//					String where = "sl_no = '" + slno + "' AND product_code = '" + productCode + "' AND color_code = '"+ colorCode +"' AND size_code = '"+ sizeCode +"'";
//					getDatabase().update(ATTRIBUTE_TABLE,cv,where,null);

				//}
			}
		}
	}
 public static String getAttributeProduct(String productCode,String colorCode,String sizeCode,String slno){
		String attributeProduct = "";
		String query = "SELECT * FROM attribute_table where product_code ='"
				+ productCode + "' AND color_code = '" + colorCode + "'";
		if(sizeCode!=null && !sizeCode.isEmpty()){
			query +=  "AND size_code = '" + sizeCode + "'";
		}
		if(slno!=null && !slno.isEmpty()){
			query +=  "AND sl_no = '" + slno + "'";
		}
		Cursor cursor = getDatabase().rawQuery(query,null);
		if(cursor.moveToFirst()){
			do{
				attributeProduct = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_CODE));
			}while (cursor.moveToNext());
		}
		return attributeProduct;
	}
 public static ArrayList<Attribute> getAttributeSizeValues(String productId, String slno) {
		ArrayList<Attribute> rowItem = new ArrayList<Attribute>();
		String selectQuery = "SELECT * FROM attribute_table where product_code='"
				+ productId + "' AND sl_no='"+ slno + "'";

		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Attribute rowItem1 = new Attribute();

				rowItem1.setCode(cursor.getString(cursor
						.getColumnIndex(COLUMN_COLOR_CODE)));
				String colorName = cursor.getString(cursor
						.getColumnIndex(COLUMN_COLOR_NAME));
				rowItem1.setName(colorName);
				rowItem1.setSizecode(cursor.getString(cursor
						.getColumnIndex(COLUMN_SIZE_CODE)));
				rowItem1.setSizename(cursor.getString(cursor
						.getColumnIndex(COLUMN_SIZE_NAME)));
				Log.d("colorname","-->"+ cursor.getString(cursor
						.getColumnIndex(COLUMN_COLOR_NAME)));
				Log.d("Size code","-->"+ cursor.getString(cursor
						.getColumnIndex(COLUMN_SIZE_CODE)));
				Log.d("Size Qty","-->"+ cursor.getString(cursor
						.getColumnIndex(COLUMN_QUANTITY)));
				int qty = cursor.getInt(cursor
						.getColumnIndex(COLUMN_QUANTITY));
				if(qty>0){
					rowItem1.setColor(colorName);
				}else{
					rowItem1.setColor("");
				}
				rowItem1.setSizeQty(String.valueOf(qty));
				rowItem.add(rowItem1);
			} while (cursor.moveToNext());
		}

		String query = "SELECT * FROM attribute_table where product_code='" + productId + "' AND sl_no = '0'";
		Cursor cursor1 = getDatabase().rawQuery(query, null);
		if(cursor1.getCount()>0) {
			if (cursor1.moveToFirst()) {
				do {
					Attribute mAttribute = new Attribute();
					mAttribute.setCode(cursor1.getString(cursor1
							.getColumnIndex(COLUMN_COLOR_CODE)));
					String colorName = cursor1.getString(cursor1
							.getColumnIndex(COLUMN_COLOR_NAME));
					mAttribute.setName(colorName);
					mAttribute.setSizecode(cursor1.getString(cursor1
							.getColumnIndex(COLUMN_SIZE_CODE)));
					mAttribute.setSizename(cursor1.getString(cursor1
							.getColumnIndex(COLUMN_SIZE_NAME)));
					int qty = cursor1.getInt(cursor1
							.getColumnIndex(COLUMN_QUANTITY));
					if (qty > 0) {
						mAttribute.setColor(colorName);
					} else {
						mAttribute.setColor("");
					}
					mAttribute.setSizeQty(String.valueOf(qty));
					rowItem.add(mAttribute);
				} while (cursor1.moveToNext());
			}
		}

		return rowItem;
	}


	public static ArrayList<Attribute> getAttributeColorValues(String productId, String slno) {
		ArrayList<Attribute> mAttributeArr = new ArrayList<Attribute>();
		String selectQuery = "SELECT * FROM attribute_table where product_code='"
				+ productId + "' AND sl_no='"+ slno + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Attribute mAttribute = new Attribute();
				String colorCode = cursor.getString(cursor.getColumnIndex(COLUMN_COLOR_CODE));
				mAttribute.setName(cursor.getString(cursor.getColumnIndex(COLUMN_COLOR_NAME)));
				String slNo = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_SLNO));
				String productcode = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_CODE));
				mAttribute.setCode(colorCode);
				int colorQty = getTotalSizeQty(slNo,productcode,colorCode);
				String colorQuantity = String.valueOf(colorQty);
				mAttribute.setColorQty(colorQuantity);
				mAttributeArr.add(mAttribute);
			} while (cursor.moveToNext());
		}
		String query = "SELECT * FROM attribute_table where product_code='" + productId + "' AND sl_no = '0'";
		Cursor cursor1 = getDatabase().rawQuery(query, null);
		if(cursor1.getCount()>0) {
			if (cursor1.moveToFirst()) {
				do {
					Attribute mAttribute = new Attribute();
					mAttribute.setName(cursor1.getString(cursor1.getColumnIndex(COLUMN_COLOR_NAME)));
					mAttribute.setCode(cursor1.getString(cursor1.getColumnIndex(COLUMN_COLOR_CODE)));
					mAttribute.setColorQty(cursor1.getString(cursor1.getColumnIndex(COLUMN_QUANTITY)));
					mAttributeArr.add(mAttribute);
				} while (cursor1.moveToNext());
			}
		}
		Log.d("colorArr", "dbmAttributeArr-->" + mAttributeArr.size());
		return mAttributeArr;
	}
	/*public static ArrayList<Attribute> getAttributeSizeValues(String productId, String slno) {
		ArrayList<Attribute> rowItem = new ArrayList<Attribute>();

		String selectQuery = "SELECT * FROM attribute_table where product_code='"
				+ productId + "' AND sl_no='"+ slno + "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Attribute rowItem1 = new Attribute();

				rowItem1.setCode(cursor.getString(cursor
						.getColumnIndex(COLUMN_COLOR_CODE)));
				String colorName = cursor.getString(cursor
						.getColumnIndex(COLUMN_COLOR_NAME));
				rowItem1.setName(colorName);
				rowItem1.setSizecode(cursor.getString(cursor
						.getColumnIndex(COLUMN_SIZE_CODE)));
				rowItem1.setSizename(cursor.getString(cursor
						.getColumnIndex(COLUMN_SIZE_NAME)));
				Log.d("colorname","-->"+ cursor.getString(cursor
						.getColumnIndex(COLUMN_COLOR_NAME)));
				Log.d("Size code","-->"+ cursor.getString(cursor
						.getColumnIndex(COLUMN_SIZE_CODE)));
				Log.d("Size Qty","-->"+ cursor.getString(cursor
						.getColumnIndex(COLUMN_QUANTITY)));
				int qty = cursor.getInt(cursor
						.getColumnIndex(COLUMN_QUANTITY));
				if(qty>0){
					rowItem1.setColor(colorName);
				}else{
					rowItem1.setColor("");
				}
				rowItem1.setSizeQty(String.valueOf(qty));
				rowItem.add(rowItem1);
			} while (cursor.moveToNext());
		}

		return rowItem;
	}


	public static ArrayList<Attribute> getAttributeColorValues(String productId, String slno) {
		ArrayList<Attribute> rowItem = new ArrayList<Attribute>();
		String selectQuery = "SELECT * FROM attribute_table where product_code='"
				+ productId + "' AND sl_no='"+ slno + "'";

		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Attribute rowItem1 = new Attribute();
				String colorCode = cursor.getString(cursor
						.getColumnIndex(COLUMN_COLOR_CODE));
				rowItem1.setCode(colorCode);
				rowItem1.setName(cursor.getString(cursor
						.getColumnIndex(COLUMN_COLOR_NAME)));

				String slno1 = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRODUCT_SLNO));

				String productcode = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRODUCT_CODE));
				int colorQty = getTotalSizeQty(slno1,productcode,colorCode);
				String colorQuantity = String.valueOf(colorQty);
				rowItem1.setColorQty(colorQuantity);
				//rowItem1.setColorQty(cursor.getString(cursor.getColumnIndex(COLUMN_COLOR_QUANTITY)));

				rowItem.add(rowItem1);
			} while (cursor.moveToNext());
		}

		return rowItem;
	}*/
	public static int getTotalSizeQty(String slno,String productCode,String colorCode){
		int qty =0;
		String selectQuery = "SELECT SUM(quantity) FROM attribute_table where sl_no ='"
				+ slno + "' AND product_code = '" + productCode + "' AND color_code = '"+ colorCode +"'" ;
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				qty += cursor.getInt(cursor.getColumnIndex("SUM(quantity)"));
			} while (cursor.moveToNext());
		}
		return qty;
	}

//getproduct values

	public static String getAttributeProduct(String slno,String productCode) {
		String product_code = "";
		String selectQuery = "SELECT product_code FROM attribute_table where product_code='"
				+ productCode + "'AND sl_no = '" + slno
				+ "'";
		Cursor cursor = getDatabase().rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {

				product_code = cursor.getString(cursor
						.getColumnIndex(COLUMN_PRODUCT_CODE));

			} while (cursor.moveToNext());
		}
		return product_code;
	}









	public static String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}

	public String fourDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setMinimumFractionDigits(4);
		String tot = df.format(d);

		return tot;
	}


	public static void storeProduct(int sl_no, String prodCode,
									String prodName, double cartonQty, double looseQty, double qty, double foc,
									double price, double discount, String uom, double cartonPerQty,
									double total, double tax, String netTotal, String taxType,
									String taxValue, String retailPrice, String subTotal,
									String cprice, String exqty, String MinimumSellingPrice,
									String ItemRemarks, String SOSlno, String stock, String MinimumCartonSellingPrice,
									String haveBatch, String haveExpiry, String billDiscount, String totalDiscount) {

		String ttl = twoDecimalPoint(total);

		ContentValues cv = new ContentValues();

		cv.put(COLUMN_PRODUCT_SLNO, sl_no);
		cv.put(COLUMN_PRODUCT_CODE, prodCode);
		cv.put(COLUMN_PRODUCT_NAME, prodName);
		cv.put(COLUMN_CARTON_QTY, cartonQty);
		cv.put(COLUMN_LOOSE_QTY, looseQty);
		cv.put(COLUMN_QUANTITY, qty);
		cv.put(COLUMN_FOC, foc);
		cv.put(COLUMN_PRODUCT_PRICE, price);
		cv.put(COLUMN_ITEM_DISCOUNT, discount);
		cv.put(COLUMN_PRODUCT_UOM, uom);
		cv.put(COLUMN_PIECE_PERQTY, cartonPerQty);
		cv.put(COLUMN_TOTAL, ttl);
		cv.put(COLUMN_TAX, tax);
		cv.put(COLUMN_NETTOTAL, netTotal);
		cv.put(COLUMN_SUB_TOTAL, subTotal);
		cv.put(COLUMN_TAXTYPE, taxType);
		cv.put(COLUMN_TAXVALUE, taxValue);
		cv.put(COLUMN_RETAILPRICE, retailPrice);
		cv.put(COLUMN_EXCHANGEQTY, exqty);
		cv.put(COLUMN_CARTONPRICE, cprice);
		cv.put(COLUMN_MINIMUM_SELLING_PRICE, MinimumSellingPrice);
		cv.put(COLUMN_ITEM_REMARKS, ItemRemarks);
		cv.put(COLUMN_BARCODE_STATUS, 0);
		cv.put(COLUMN_SO_SLNO, SOSlno);
		//	cv.put(COLUMN_CURRENT_QTY, qty);
		cv.put(COLUMN_STOCK, stock);
		cv.put(COLUMN_MINIMUM_CARTON_SELLING_PRICE, MinimumCartonSellingPrice);
		cv.put(COLUMN_HAVE_BATCH, haveBatch);
		cv.put(COLUMN_HAVE_EXPIRY, haveExpiry);
		cv.put(COLUMN_BILL_DISCOUNT,billDiscount);
		cv.put(COLUMN_TOTAL_DISCOUNT,totalDiscount);

		Log.d("Storeinsotdb",cv.toString());

		getDatabase().insert(PRODUCT_TABLE, null, cv);

	}


	public static int updateManualStock(String productCode, String productName, String cQty, String lQty, String pcs, String qty,String stock) {
		ContentValues cv = new ContentValues();
		Log.d("QuantityUpdate",qty);
		cv.put(COLUMN_PRODUCT_CODE,productCode);
		cv.put(COLUMN_PRODUCT_NAME,productName);
		cv.put(COLUMN_CARTON_QTY,cQty);
		cv.put(COLUMN_LOOSE_QTY,lQty);
		cv.put(COLUMN_PIECE_PERQTY,pcs);
		cv.put(COLUMN_QUANTITY,qty);
		cv.put(COLUMN_STOCK,stock);
		Log.d("Updateinsotdb",cv.toString());

		return getDatabase().update(MANUAL_STOCK_TABLE, cv, "product_code = '" + productCode + "'", null);
	}
}