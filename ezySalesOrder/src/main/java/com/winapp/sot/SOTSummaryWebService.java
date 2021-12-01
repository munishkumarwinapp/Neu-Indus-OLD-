package com.winapp.sot;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Catalog;
import com.winapp.helper.Constants;
import com.winapp.model.LocationGetSet;
import com.winapp.sotdetails.CustomerSetterGetter;
import com.winapp.sotdetails.DBCatalog;

public class SOTSummaryWebService {

	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	static String result;
	static String validURL;
	static String header_json = "", detail_json = "",barcode_json = "",attribute_json="",batch_json ="",stock_json ="";
	static Cursor cursor, billdiscursor,batch_cursor;
	static Context context;
	static String sum_result;
	static String deviceId,device_name;
	static ArrayList<String> productid = new ArrayList<String>();
	static ArrayList<ProductBarcode> allproducts = new ArrayList<ProductBarcode>();
	static ArrayList<String> SOResultArr = new ArrayList<String>();
	static ArrayList<String> SOConsignmentArr = new ArrayList<String>();
	static ArrayList<String> SOCloseStockArr = new ArrayList<String>();
	static SOTDatabase sqldb;
	static Cursor attributecursor;
	public SOTSummaryWebService(String url) {
		validURL = url;
		deviceId = RowItem.getDeviceID();
		device_name = RowItem.getDeviceName();
	}

	public static String summarySOTService(String webMethName,
			double billDisct, String subTotal, String totTax, String netTotal,
			String ttl,String soNo) throws JSONException, IOException,
			XmlPullParserException {

		SOTDatabase.init(context);
		cursor = SOTDatabase.getCursor();
		double totalDiscount = 0, subTot = 0, tx = 0, ntTtl = 0;
		int slNo = 1;
		detail_json = "";
		header_json = "";
		attribute_json ="";
		double sbtot = SOTDatabase.getsumsubTot();
		double billDiscount = billDisct / sbtot;
		double billDiscs;
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					String pCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String pName = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
					String cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
					String foc = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_FOC));
					String price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
					String total = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TOTAL));
					String itemDiscount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT));
//					String taxType = cursor.getString(cursor
//							.getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
					String taxValue = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));
					String taxType = SalesOrderSetGet.getCompanytax();
//					String taxValue = SalesOrderSetGet.getTaxValue();

					String retail_Price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_RETAILPRICE));

					String sub_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL));
					String net_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
					String tax_amount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAX));
					
					String cprice = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));
					
					String exchangeQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));

					double sbTotal = SOTDatabase.getsubTotal(slNo);
					billDiscs = billDiscount * sbTotal;

					totalDiscount = billDiscs
							+ Double.parseDouble(itemDiscount);
					subTot = Double.parseDouble(sub_ToTal);
					ntTtl = Double.parseDouble(net_ToTal);

					tx = Double.parseDouble(tax_amount);
					String billDisc = twoDecimalPoint(billDiscs);
					String subTtl = twoDecimalPoint(subTot);
					String netTtl = twoDecimalPoint(ntTtl);
					String txAmt = fourDecimalPoint(tx);
					String totalDisc = twoDecimalPoint(totalDiscount);
					Log.d("priceTXtValue","-->"+price);

					detail_json += slNo + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^" + foc + "^"
							+ pcsPerCarton + "^" + retail_Price + "^" + price
							+ "^" + total + "^" + itemDiscount + "^" + billDisc
							+ "^" + totalDisc + "^" + subTtl + "^" + txAmt
							+ "^" + netTtl + "^" + taxType + "^" + taxValue
							+ "^" + "I" + "^" + exchangeQty +"^" + cprice + "!";


					slNo++;
				} while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);

			Log.d("DetailSO", detail_json);
		}

		attributecursor = SOTDatabase.getAttributeCursor();

		boolean isSizeQty = false;
		if (attributecursor != null && attributecursor.getCount() > 0) {

			if (attributecursor.moveToFirst()) {
				do {
					String slno = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
					String pName = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
					String productcode = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String colorcode = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_COLOR_CODE));
					String colorname = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_COLOR_NAME));
					String sizecode = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_SIZE_CODE));
					String sizename = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_SIZE_NAME));
					String quantity = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
					String flag = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_FLAG));

					int iQty = Integer.valueOf(quantity);
					if(iQty>0) {
						isSizeQty = true;
						attribute_json += slno + "^" + productcode + "^" + colorcode + "^"
								+ sizecode + "^" + "0" + "^" + "" + "^" + "0" + "^"
								+ "0" + "^" + quantity + "^" + "0"
								+ "^" + "0" +  "!";
					}

					slNo++;
				} while (attributecursor.moveToNext());
			}

			if(isSizeQty){
				attribute_json = attribute_json.substring(0, attribute_json.length() - 1);
			}
			Log.d("AttributeDetail", attribute_json);
		}

		String locationCode = SalesOrderSetGet.getLocationcode();
		String deliveryDate = SalesOrderSetGet.getDeliverydate();
		String customerCode = SalesOrderSetGet.getCustomercode();
		String currencyCode = SalesOrderSetGet.getCurrencycode();
		String remarks = SalesOrderSetGet.getRemarks();
		String currencyRate = SalesOrderSetGet.getCurrencyrate();
		String soDate = SalesOrderSetGet.getSaleorderdate();
		String user = SupplierSetterGetter.getUsername();

		double blDsc = billDisct;
		double itemDisc = SOTDatabase.getitemDisc();
		double totDisc = blDsc + itemDisc;

		header_json =  soNo + "^" + soDate + "^" + deliveryDate + "^" + locationCode
				+ "^" + customerCode + "^" + ttl + "^" + itemDisc + "^" + blDsc
				+ "^" + totDisc + "^" + subTotal + "^" + totTax + "^"
				+ netTotal + "^" + remarks + "^" + "0" + "^" + currencyCode
				+ "^" + currencyRate + "^" + deviceId + "^" + user;

		Log.d("ProductheaderSO", header_json);

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo productJson = new PropertyInfo();
		PropertyInfo barcodeJson = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo AttributeJson = new PropertyInfo();
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		productJson.setName("sHeader");
		productJson.setValue(header_json);
		productJson.setType(String.class);
		request.addProperty(productJson);

		barcodeJson.setName("sDetail");
		barcodeJson.setValue(detail_json);
		barcodeJson.setType(String.class);
		request.addProperty(barcodeJson);

		AttributeJson.setName("sAttributeDetail");
		AttributeJson.setValue(attribute_json);
		AttributeJson.setType(String.class);
		request.addProperty(AttributeJson);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

		androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		String resTxt = response.toString();

		Log.d("Summary Product", resTxt);

		result = " { SOResult : " + resTxt + "}";

		JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(result);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");

			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				sum_result = jsonChildNode.optString("Result").toString();

				if (sum_result.matches("")) {
					sum_result = "failed";
					Log.d("Summary Result", sum_result);
				}

			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return sum_result;
	}

	/*public static String summaryDOService(String webMethName, double billDisct,
			String subTotal, String totTax, String netTotal, String ttl,
			String srlNo, String sono,String dono) throws JSONException, IOException,
			XmlPullParserException {

		SOTDatabase.init(context);
		cursor = SOTDatabase.getCursor();
		sqldb = new SOTDatabase(context);
		productid.clear();
		allproducts.clear();
		double totalDiscount = 0, subTot = 0, tx = 0, ntTtl = 0;
		int slNo = 1,sno=0;
		double sbtot = SOTDatabase.getsumsubTot();
		double billDiscount = billDisct / sbtot;
		double billDiscs;
		detail_json = "";
		header_json = "";
		barcode_json = "";
		String appType = LogOutSetGet.getApplicationType();
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					String pSlno = cursor.getString(cursor
						       .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
					String pCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String pName = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
					String cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
					String foc = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_FOC));
					String price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
					String total = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TOTAL));
					String itemDiscount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT));
//					String taxType = cursor.getString(cursor
//							.getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
					String taxValue = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));
					String taxType = SalesOrderSetGet.getCompanytax();
//					String taxValue = SalesOrderSetGet.getTaxValue();
					String retail_Price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_RETAILPRICE));
					String sub_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL));
					String net_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
					String tax_amount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAX));
					
					String cprice = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));
					
					String exchangeQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));

					String ItemRemarks = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_ITEM_REMARKS));
					
					String SOSlno = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_SO_SLNO));
					
					double sbTotal = SOTDatabase.getsubTotal(slNo);
					billDiscs = billDiscount * sbTotal;

					totalDiscount = billDiscs
							+ Double.parseDouble(itemDiscount);
					subTot = Double.parseDouble(sub_ToTal);
					ntTtl = Double.parseDouble(net_ToTal);

					tx = Double.parseDouble(tax_amount);

					String subTtl = twoDecimalPoint(subTot);
					String netTtl = twoDecimalPoint(ntTtl);
					String txAmt = fourDecimalPoint(tx);
					String billDisc = twoDecimalPoint(billDiscs);
					String totalDisc = twoDecimalPoint(totalDiscount);
					
					if (ConvertToSetterGetter.getSoNo()==null || ConvertToSetterGetter.getSoNo().trim().equals("") && 
						       ConvertToSetterGetter.getEdit_do_no()==null || ConvertToSetterGetter.getEdit_do_no().trim().equals("")) {
						      sno = slNo;
						     }
						     else{
						      int SOSlNo = SOSlno.equals("") ? 0:Integer.valueOf(SOSlno);
						      if(SOSlNo>0){
						       sno = SOSlNo;
						      }else{
						       sno = slNo;
						      }
						     }
				     
				     Log.d("sno", "-->"+sno);

					detail_json += sno + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^" + foc + "^"
							+ pcsPerCarton + "^" + retail_Price + "^" + price
							+ "^" + total + "^" + itemDiscount + "^" + billDisc
							+ "^" + totalDisc + "^" + subTtl + "^" + txAmt
							+ "^" + netTtl + "^" + taxType + "^" + taxValue
							+ "^" + "I" + "^" + SOSlno + "^" + exchangeQty +"^" + cprice +  "^" + ItemRemarks + "!";


					slNo++;
				} while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);
			Log.d("Detail", detail_json);
		}
		// Add barcode for product
		if (appType.matches("W")) {
			//	  productid = sqldb.distinctprodId();
			productid = sqldb.getBarcodeStatus();
				  
		  if(!productid.isEmpty()){
					  
				  for (int i = 0; i < productid.size(); i++) {
				   String prodtid = productid.get(i);
				   allproducts = sqldb.getAllprodValues(prodtid);  
				   String productSno = SOTDatabase.getProductSno(prodtid);
				   for (ProductBarcode prodbarcode : allproducts) {

				 //   int prodsno = i+1;//prodbarcode.getSno();
				  //  String prodsno = prodbarcode.getSno();
				    String prodcode = prodbarcode.getProductcode();
				    String prodseqno = prodbarcode.getSeqno();
				    String prodweightbarcode = prodbarcode.getBarcode();
				    double prodweight = prodbarcode.getWeight();

				    barcode_json += productSno + "^" + prodcode + "^" + prodseqno + "^"
				      + prodweightbarcode + "^" + prodweight + "^" + "!";
				   }
				   
				  }
 
				  barcode_json = barcode_json.substring(0, barcode_json.length() - 1);
				  Log.d("barcode_json", barcode_json);
				  			 
	     }
			}	
		
		String locationCode = SalesOrderSetGet.getLocationcode();
		String customerCode = SalesOrderSetGet.getCustomercode();
		String currencyCode = SalesOrderSetGet.getCurrencycode();
		String remarks = SalesOrderSetGet.getRemarks();
		String currencyRate = SalesOrderSetGet.getCurrencyrate();
		String soDate = SalesOrderSetGet.getSaleorderdate();
		String user = SupplierSetterGetter.getUsername();

		double blDsc = billDisct;
		double itemDisc = SOTDatabase.getitemDisc();
		double totDisc = blDsc + itemDisc;	

		header_json = dono + "^" + soDate + "^" + locationCode + "^" + customerCode
				+ "^" + ttl + "^" + itemDisc + "^" + blDsc + "^" + totDisc
				+ "^" + subTotal + "^" + totTax + "^" + netTotal + "^"
				+ remarks + "^" + "" + "^" + "" + "^" + currencyCode + "^"
				+ currencyRate + "^" + deviceId + "^" + "I" + "^" + user + "^"
				+ sono;

		Log.d("Product header", header_json);

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo headerJson = new PropertyInfo();
		PropertyInfo detailJson = new PropertyInfo();
		PropertyInfo barcodeJson = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		headerJson.setName("sHeader");
		headerJson.setValue(header_json);
		headerJson.setType(String.class);
		request.addProperty(headerJson);
		
		detailJson.setName("sDetail");
		detailJson.setValue(detail_json);
		detailJson.setType(String.class);
		request.addProperty(detailJson);		

		barcodeJson.setName("sCartonDetail");
		barcodeJson.setValue(barcode_json);
		barcodeJson.setType(String.class);
		request.addProperty(barcodeJson);		

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

		androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		String resTxt = response.toString();

		Log.d("Summary Product", resTxt);

		result = " { SOResult : " + resTxt + "}";

		JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(result);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");

			*//*********** Process each JSON Node ************//*
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				sum_result = jsonChildNode.optString("Result").toString();

				if (sum_result.matches("")) {
					sum_result = "failed";
					Log.d("Summary Result", sum_result);
				}
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return sum_result;
	}*/


	public static String summaryDOService(String webMethName, double billDisct,
										  String subTotal, String totTax, String netTotal, String ttl,
										  String srlNo, String sono, String dono, String type) throws JSONException, IOException,
			XmlPullParserException {

		SOTDatabase.init(context);
		cursor = SOTDatabase.getCursor();
		sqldb = new SOTDatabase(context);
		productid.clear();
		allproducts.clear();
		double totalDiscount = 0, subTot = 0, tx = 0, ntTtl = 0;
		int slNo = 1,sno=0;
		double sbtot = SOTDatabase.getsumsubTot();
		double billDiscount = billDisct / sbtot;
		double billDiscs;
		detail_json = "";
		header_json = "";
		barcode_json = "";
		String appType = LogOutSetGet.getApplicationType();
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					String pSlno = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
					String pCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String pName = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
					String cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
					String foc = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_FOC));
					String price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
					String total = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TOTAL));
					String itemDiscount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT));
//					String taxType = cursor.getString(cursor
//							.getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
					String taxValue = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));
					String taxType = SalesOrderSetGet.getCompanytax();
//					String taxValue = SalesOrderSetGet.getTaxValue();
					String retail_Price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_RETAILPRICE));
					String sub_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL));
					String net_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
					String tax_amount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAX));

					String cprice = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));

					String exchangeQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));

					String ItemRemarks = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_ITEM_REMARKS));

					String SOSlno = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_SO_SLNO));

					double sbTotal = SOTDatabase.getsubTotal(slNo);
					billDiscs = billDiscount * sbTotal;

					totalDiscount = billDiscs
							+ Double.parseDouble(itemDiscount);
					subTot = Double.parseDouble(sub_ToTal);
					ntTtl = Double.parseDouble(net_ToTal);

					tx = Double.parseDouble(tax_amount);

					String subTtl = twoDecimalPoint(subTot);
					String netTtl = twoDecimalPoint(ntTtl);
					String txAmt = fourDecimalPoint(tx);
					String billDisc = twoDecimalPoint(billDiscs);
					String totalDisc = twoDecimalPoint(totalDiscount);

					if (ConvertToSetterGetter.getSoNo()==null || ConvertToSetterGetter.getSoNo().trim().equals("") &&
							ConvertToSetterGetter.getEdit_do_no()==null || ConvertToSetterGetter.getEdit_do_no().trim().equals("")) {
						sno = slNo;
					}
					else{
						int SOSlNo = SOSlno.equals("") ? 0:Integer.valueOf(SOSlno);
						if(SOSlNo>0){
							sno = SOSlNo;
						}else{
							sno = slNo;
						}
					}

					Log.d("sno", "-->"+sno);


					detail_json += sno + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^" + foc + "^"
							+ pcsPerCarton + "^" + retail_Price + "^" + price
							+ "^" + total + "^" + itemDiscount + "^" + billDisc
							+ "^" + totalDisc + "^" + subTtl + "^" + txAmt
							+ "^" + netTtl + "^" + taxType + "^" + taxValue
							+ "^" + "I" + "^" + SOSlno + "^" + exchangeQty +"^" + cprice +  "^" + ItemRemarks + "!";


					slNo++;
				} while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);
			Log.e("Detail", detail_json);
		}
		// Add barcode for product
		if (appType.matches("W")) {
			//	  productid = sqldb.distinctprodId();
			productid = sqldb.getBarcodeStatus();

			if(!productid.isEmpty()){

				for (int i = 0; i < productid.size(); i++) {
					String prodtid = productid.get(i);
					allproducts = sqldb.getAllprodValues(prodtid);
					String productSno = SOTDatabase.getProductSno(prodtid);
					for (ProductBarcode prodbarcode : allproducts) {

						//   int prodsno = i+1;//prodbarcode.getSno();
						//  String prodsno = prodbarcode.getSno();
						String prodcode = prodbarcode.getProductcode();
						String prodseqno = prodbarcode.getSeqno();
						String prodweightbarcode = prodbarcode.getBarcode();
						double prodweight = prodbarcode.getWeight();

						barcode_json += productSno + "^" + prodcode + "^" + prodseqno + "^"
								+ prodweightbarcode + "^" + prodweight + "^" + "!";
					}

				}

				barcode_json = barcode_json.substring(0, barcode_json.length() - 1);
				Log.d("barcode_json", barcode_json);

			}
		}

		String locationCode = SalesOrderSetGet.getLocationcode();
		String customerCode = SalesOrderSetGet.getCustomercode();
		String currencyCode = SalesOrderSetGet.getCurrencycode();
		String remarks = SalesOrderSetGet.getRemarks();
		String currencyRate = SalesOrderSetGet.getCurrencyrate();
		String soDate = SalesOrderSetGet.getSaleorderdate();
		String user = SupplierSetterGetter.getUsername();

		double blDsc = billDisct;
		double itemDisc = SOTDatabase.getitemDisc();
		double totDisc = blDsc + itemDisc;

		header_json = dono + "^" + soDate + "^" + locationCode + "^" + customerCode
				+ "^" + ttl + "^" + itemDisc + "^" + blDsc + "^" + totDisc
				+ "^" + subTotal + "^" + totTax + "^" + netTotal + "^"
				+ remarks + "^" + "" + "^" + "" + "^" + currencyCode + "^"
				+ currencyRate + "^" + deviceId + "^" + "I" + "^" + user + "^"
				+ sono + "^" +type;

		Log.d("Productheader", header_json);

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo headerJson = new PropertyInfo();
		PropertyInfo detailJson = new PropertyInfo();
		PropertyInfo barcodeJson = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		headerJson.setName("sHeader");
		headerJson.setValue(header_json);
		headerJson.setType(String.class);
		request.addProperty(headerJson);

		detailJson.setName("sDetail");
		detailJson.setValue(detail_json);
		detailJson.setType(String.class);
		request.addProperty(detailJson);

		barcodeJson.setName("sCartonDetail");
		barcodeJson.setValue(barcode_json);
		barcodeJson.setType(String.class);
		request.addProperty(barcodeJson);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

		androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		String resTxt = response.toString();

		Log.d("Summary Product", resTxt);

		Log.e("Request Date", String.valueOf(request));

		result = " { SOResult : " + resTxt + "}";

		JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(result);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");

			/********** Process each JSON Node ***********/
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				sum_result = jsonChildNode.optString("Result").toString();

				if (sum_result.matches("")) {
					sum_result = "failed";
					Log.d("Summary Result", sum_result);
				}
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return sum_result;
	}

	public static ArrayList<String> summaryCloseStock(String webMethName,String location) throws IOException, XmlPullParserException {
		SOCloseStockArr.clear();
		detail_json = "";
		header_json = "";

		SOTDatabase.init(context);
		cursor = SOTDatabase.getCloseStockCursor();
		Log.d("cursor.getCounts","-->"+cursor.getCount()+ "-->"+webMethName);

		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					String srslno = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
					String pCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String pName = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));

					String cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
					String pcs = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
					String price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));


					/*detail_json += srslno + "^" + pCode + "^" + qty + "^"
							+ pName + "!" ;*/
					detail_json += srslno + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^"
							+ pcs + "^" + price + "^" + "0" + "^"
							+ "I" + "!";

				} while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);
			Log.d("Detail", detail_json);

		}
		String frmlocationcode = SalesOrderSetGet.getLocationcode();
		String user = SupplierSetterGetter.getUsername();
		String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
		Log.d("Datecheck","--?>"+date);

		header_json = "^" + date + "^" + frmlocationcode + "^"
				+ location + "^" + "0" + "^" + "" + "^" + "closing stock"
				+ "^" + deviceId + "^" + user;


//		header_json = location;

		Log.d("header_json",header_json);

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo headerJson = new PropertyInfo();
		PropertyInfo detailJson = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		headerJson.setName("sHeader");
		headerJson.setValue(header_json);
		headerJson.setType(String.class);
		request.addProperty(headerJson);

		detailJson.setName("sDetail");
		detailJson.setValue(detail_json);
		detailJson.setType(String.class);
		request.addProperty(detailJson);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request); // prepare request
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);
		Log.d("valodurlcheck","-->"+validURL);
		androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		String resTxt = response.toString();

		Log.d("SummaryProduct", resTxt);

		result = " { SOResult : " + resTxt + "}";

		JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(result);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");

			/*********** Process each JSON Node ************/
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

				/******* Fetch node values **********/
				sum_result = jsonChildNode.optString("Result").toString();


				if (sum_result.matches("")) {
					sum_result = "failed";
					Log.d("Summary Result", sum_result);
				}

				SOCloseStockArr.add(sum_result);

			}

		} catch (JSONException e) {

			e.printStackTrace();
		}


		return  SOCloseStockArr;

	}

	public static ArrayList<String> summaryInvoiceService(String webMethName,
			double billDisct, String subTotal, String totTax, String netTotal,
			String ttl, String SoSlNo, String SoNo, String DoNo,
			String invoiceNumber, String username,String dis_perc) throws JSONException, IOException,
			XmlPullParserException {
		
		productid.clear();
		allproducts.clear();
		SOResultArr.clear();
		
		SOTDatabase.init(context);
		SOTDatabase sqldb = new SOTDatabase(context);
		cursor = SOTDatabase.getCursor();
		double totalDiscount = 0, subTot = 0, tx = 0, ntTtl = 0;
		int slNo = 1;
		double sbtot = SOTDatabase.getsumsubTot();
		double billDiscount = billDisct / sbtot;
		double billDiscs;
		detail_json = "";
		header_json = "";
		barcode_json = "";
		String batch_json="";
		String ReceiptNo="";
		String appType = LogOutSetGet.getApplicationType();

		Log.d("dis_perc","-->"+dis_perc);
		
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					String srslno = cursor.getString(cursor
						       .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
					String pCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String pName = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
					String cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
					String foc = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_FOC));
					String price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
					String total = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TOTAL));
					String itemDiscount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT));
//					String taxType = cursor.getString(cursor
//							.getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
					String taxValue = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));
					String taxType = SalesOrderSetGet.getCompanytax();
//					String taxValue = SalesOrderSetGet.getTaxValue();
					String retail_Price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_RETAILPRICE));
					String sub_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL));
					String net_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
					String tax_amount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAX));
					
					String cprice = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));
					
					String exchangeQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));
					
					String ItemRemarks = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_ITEM_REMARKS));
					
					String SOSlno = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_SO_SLNO));
					
					if(SOSlno!=null && !SOSlno.isEmpty()){
						
					}else{
						SOSlno="";
					}
					if(exchangeQty!=null && !exchangeQty.isEmpty()){
						
					}else{
						exchangeQty="";
					}
					
					double sbTotal = SOTDatabase.getsubTotal(slNo);
					billDiscs = billDiscount * sbTotal;

					totalDiscount = billDiscs
							+ Double.parseDouble(itemDiscount);

					subTot = Double.parseDouble(sub_ToTal);
					ntTtl = Double.parseDouble(net_ToTal);
					tx = Double.parseDouble(tax_amount);

					String subTtl = fourDecimalPoint(subTot);
					String netTtl = twoDecimalPoint(ntTtl);
					String txAmt = fourDecimalPoint(tx);
					String billDisc = fourDecimalPoint(billDiscs);
					String totalDisc = fourDecimalPoint(totalDiscount);

					String taxcode = SalesOrderSetGet.getTaxCode();

					if(taxcode!=null && !taxcode.isEmpty()){

					}else{
						taxcode="";
					}

					Log.d("parameters","tt "+slNo +" pCode"+pCode +"pName"+pName
					+"cQty"+cQty+"lQty"+lQty+"qty"+qty+"foc"+foc+"pcsPerCarton"+pcsPerCarton
					+"retail_Price"+retail_Price+"price"+price+"total"+total+"itemDiscount"+itemDiscount
					+"billDisc"+billDisc+"totalDisc"+totalDisc+"subTtl"+subTtl+"txAmt"+txAmt
					+"netTtl"+netTtl+"taxType"+taxType+"taxValue"+taxValue+"SOSlno"+SOSlno+"exchangeQty"+exchangeQty
					+"cprice"+cprice+"ItemRemarks"+ItemRemarks+"taxcode"+taxcode);




					detail_json += slNo + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^" + foc + "^"
							+ pcsPerCarton + "^" + retail_Price + "^" + price
							+ "^" + total + "^" + itemDiscount + "^" + billDisc
							+ "^" + totalDisc + "^" + subTtl + "^" + txAmt
							+ "^" + netTtl + "^" + taxType + "^" + taxValue
							+ "^" + "I" + "^" + SOSlno + "^" + exchangeQty +"^" + cprice + "^" + ItemRemarks + "^"+ taxcode + "!";
					
					
					Cursor cursor = SOTDatabase.getBatchCursorWithSR(pCode,srslno);
					
					if (cursor != null && cursor.getCount() > 0) {

						if (cursor.moveToFirst()) {
							do {
								
								String bpCode = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
								
										
								String bNo = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_BATCH_NO));	
								String exDate = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_EXPIRY_DATE));	
								String mfgDate = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_MFG_DATE));
								String bcQty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
								String blQty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
								String bqty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
								String bfoc = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_FOC));
								String bprice = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
								
								
								if(exDate!=null && !exDate.isEmpty()){
									
								}else{
									exDate= "01/01/1900";
								}
								
								if(mfgDate!=null && !mfgDate.isEmpty()){
									
								}else{
									mfgDate= "01/01/1900";
								}
								Log.d("save batch", "mfgDate : "+mfgDate);
								
								double cqy=0,lqy=0,qy=0,fqy=0;
								
								if(bcQty!=null && !bcQty.isEmpty()){
									cqy = Double.parseDouble(bcQty);
								}else{
									bcQty="";
								}
								
								if(blQty!=null && !blQty.isEmpty()){
									lqy = Double.parseDouble(blQty);
								}else{
									blQty="";
								}
								
								if(bqty!=null && !bqty.isEmpty()){
									qy = Double.parseDouble(bqty);
								}else{
									bqty="";
						        }
								
								if(bfoc!=null && !bfoc.isEmpty()){
									fqy = Double.parseDouble(bfoc);
								}else{
							         bfoc="";
						        }
								
								if(cqy>0 || lqy>0 || qy>0 || fqy>0){
									batch_json += slNo + "^" + bpCode + "^" + bNo + "^" + exDate + "^"+ mfgDate + "^" + "" + "^"
											+ bcQty + "^" + blQty + "^" + bqty + "^" + bfoc + "^"
											+ bprice + "!";
								}
																																
							} while (cursor.moveToNext());
						} 
					}
			
					slNo++;
				} while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);
			Log.d("Detail", detail_json);
			
			if(batch_json.matches("")){
			}else{
				batch_json = batch_json.substring(0, batch_json.length() - 1);
			}
			
			Log.d("batch_json", ""+batch_json);
		}
		
		// Add barcode for product
		if (appType.matches("W")) {
			//	  productid = sqldb.distinctprodId();
			productid = sqldb.getBarcodeStatus();
				  
		  if(!productid.isEmpty()){
					  
				  for (int i = 0; i < productid.size(); i++) {
				   String prodtid = productid.get(i);
				   allproducts = sqldb.getAllprodValues(prodtid);  
				   String productSno = SOTDatabase.getProductSno(prodtid);
				   for (ProductBarcode prodbarcode : allproducts) {

				 //   int prodsno = i+1;//prodbarcode.getSno();
				  //  String prodsno = prodbarcode.getSno();
				    String prodcode = prodbarcode.getProductcode();
				    String prodseqno = prodbarcode.getSeqno();
				    String prodweightbarcode = prodbarcode.getBarcode();
				    double prodweight = prodbarcode.getWeight();

				    barcode_json += productSno + "^" + prodcode + "^" + prodseqno + "^"
				      + prodweightbarcode + "^" + prodweight + "^" + "!";
				   }
				  }	
				  
				  barcode_json = barcode_json.substring(0, barcode_json.length() - 1);
				  
				  Log.d("barcode_json", barcode_json);
				  			 
	     }
			}		  	
			  			  
		String locationCode = SalesOrderSetGet.getLocationcode();
		String customerCode = SalesOrderSetGet.getCustomercode();
		String currencyCode = SalesOrderSetGet.getCurrencycode();
		String remarks = SalesOrderSetGet.getRemarks();
		String currencyRate = SalesOrderSetGet.getCurrencyrate();
		String soDate = SalesOrderSetGet.getSaleorderdate();
		String user = SupplierSetterGetter.getUsername();
		String disPerc =CustomerSetterGetter.getDiscountPercentage();

		Log.d("disPerc","->"+disPerc);

		double blDsc = billDisct;
		double itemDisc = SOTDatabase.getitemDisc();
		double totDisc = blDsc + itemDisc;

		SoNo = ConvertToSetterGetter.getSoNo();
		DoNo = ConvertToSetterGetter.getDoNo();

		String taxtype = SalesOrderSetGet.getTaxType();

		if(taxtype!=null && !taxtype.isEmpty()){

		}else{
			taxtype="";
		}

		String taxPerc = SalesOrderSetGet.getTaxPerc();

		if(taxPerc!=null && !taxPerc.isEmpty()){

		}else{
			taxPerc="";
		}

		String taxcode = SalesOrderSetGet.getTaxCode();

		if(taxcode!=null && !taxcode.isEmpty()){

		}else{
			taxcode="";
		}



		header_json = invoiceNumber + "^" + soDate + "^" + locationCode + "^"
				+ customerCode + "^" + ttl + "^" + itemDisc + "^" + blDsc + "^"
				+ totDisc + "^" + subTotal + "^" + totTax + "^" + netTotal
				+ "^" + remarks + "^" + currencyCode + "^" + currencyRate + "^"
				+ SoNo + "^" + DoNo + "^" + deviceId + "^" + "W" + "^" + device_name + "^" + "M" + "^" + taxcode + "^" +taxtype + "^"+ taxPerc + "^"+
				"I" + "^" + user + "^" + disPerc;

		Log.d("Productheader", header_json);

		String doNumbers = DoNo;

		Log.d("Do Numbers", "do"+DoNo);

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo productJson = new PropertyInfo();
		PropertyInfo barcodeJson = new PropertyInfo();
		PropertyInfo doNumbersJson = new PropertyInfo();
		PropertyInfo productBarcodeJson = new PropertyInfo();
		PropertyInfo sCollectCash = new PropertyInfo();
		PropertyInfo BatchDetail = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo usernameinfo = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		String collectCash = SalesOrderSetGet.getsCollectCash();
		
		Log.d("collectCash", "collectCash"+collectCash);
		
		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		productJson.setName("sHeader");
		productJson.setValue(header_json);
		productJson.setType(String.class);
		request.addProperty(productJson);

		barcodeJson.setName("sDetail");
		barcodeJson.setValue(detail_json);
		barcodeJson.setType(String.class);
		request.addProperty(barcodeJson);

		productBarcodeJson.setName("sCartonDetail");
		productBarcodeJson.setValue(barcode_json);
		productBarcodeJson.setType(String.class);
		request.addProperty(productBarcodeJson);
		
		doNumbersJson.setName("sDoNumbers");
		doNumbersJson.setValue(doNumbers);
		doNumbersJson.setType(String.class);
		request.addProperty(doNumbersJson);


		sCollectCash.setName("sCollectCash");
		sCollectCash.setValue(collectCash);
		sCollectCash.setType(String.class);
		request.addProperty(sCollectCash);
		
		BatchDetail.setName("sBatchDetail");
		BatchDetail.setValue(batch_json);
		BatchDetail.setType(String.class);
		request.addProperty(BatchDetail);
		
		usernameinfo.setName("SalesMan");
		usernameinfo.setValue(username);
		usernameinfo.setType(String.class);
		request.addProperty(usernameinfo);
		
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request); // prepare request
		envelope.bodyOut = request;

		Log.d("validurlCheck","-->"+validURL);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

		androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		String resTxt = response.toString();

		Log.d("Summary Product", resTxt);

		result = " { SOResult : " + resTxt + "}";

		JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(result);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");

			/*********** Process each JSON Node ************/
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

				/******* Fetch node values **********/
				sum_result = jsonChildNode.optString("Result").toString();
				ReceiptNo = jsonChildNode.optString("ReceiptNo").toString();

				if (sum_result.matches("")) {
					sum_result = "failed";
					Log.d("Summary Result", sum_result);
				}
				
				SOResultArr.add(sum_result);
				SOResultArr.add(ReceiptNo);
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return SOResultArr;
	}

	public static String summaryGRAService(String webMethName,
			double billDisct, String subTotal, String totTax, String netTotal,
			String ttl,String graNo) throws JSONException, IOException,
			XmlPullParserException {

		productid.clear();
		allproducts.clear();
		
		SOTDatabase.init(context);
		sqldb = new SOTDatabase(context);
		cursor = SOTDatabase.getCursor();
		billdiscursor = SOTDatabase.getBillCursor();
		double totalDiscount = 0, subTot = 0, tx = 0, ntTtl = 0;
		int slNo = 1;
		detail_json = "";
		header_json = "";
		barcode_json = "";
		String batch_json="";
		double sbtot = SOTDatabase.getsumsubTot();
		double billDiscount = billDisct / sbtot;
		double billDiscs;
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					String pCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String pName = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
					String cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
					String foc = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_FOC));
					String price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
					String cprice = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));
					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
					String total = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TOTAL));
					String itemDiscount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT));
//					String taxType = cursor.getString(cursor
//							.getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
					String taxValue = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));
					String taxType = SalesOrderSetGet.getCompanytax();
//					String taxValue = SalesOrderSetGet.getTaxValue();
//					String retail_Price = cursor.getString(cursor
//							.getColumnIndex(SOTDatabase.COLUMN_RETAILPRICE));
					String sub_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL));
					String net_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
					String tax_amount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAX));

					double sbTotal = SOTDatabase.getsubTotal(slNo);
					billDiscs = billDiscount * sbTotal;

					totalDiscount = billDiscs
							+ Double.parseDouble(itemDiscount);

					subTot = Double.parseDouble(sub_ToTal);
					ntTtl = Double.parseDouble(net_ToTal);

					tx = Double.parseDouble(tax_amount);

					String subTtl = twoDecimalPoint(subTot);
					String netTtl = twoDecimalPoint(ntTtl);
					String txAmt = fourDecimalPoint(tx);
					String billDisc = twoDecimalPoint(billDiscs);
					String totalDisc = twoDecimalPoint(totalDiscount);

					detail_json += slNo + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^" + foc + "^"
							+ pcsPerCarton + "^" + price + "^" + price
							+ "^" + total + "^" + itemDiscount + "^" + billDisc
							+ "^" + totalDisc + "^" + subTtl + "^" + txAmt
							+ "^" + netTtl + "^" + taxType + "^" + taxValue
							+ "^" + "I" + "^" + cprice + "!";


					
					
					Cursor cursor = SOTDatabase.getBatchCursorWithSR(pCode, slNo+"");
										
					if (cursor != null && cursor.getCount() > 0) {

						if (cursor.moveToFirst()) {
							do {
								
								String bpCode = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
								
//								String batchSlNo = SOTDatabase.getProdSlno(slNo+"");					
								String bNo = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_BATCH_NO));	
								String exDate = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_EXPIRY_DATE));						
								String bcQty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
								String blQty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
								String bqty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
								String bfoc = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_FOC));
								String bprice = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
									
								if(exDate!=null && !exDate.isEmpty()){
									
								}else{
									exDate= "01/01/1900";
								}
								
								batch_json += slNo + "^" + bpCode + "^" + bNo + "^" + exDate + "^"+ "01/01/1900" + "^" + "" + "^"
										+ bcQty + "^" + blQty + "^" + bqty + "^" + bfoc + "^"
										+ bprice + "!";
								
							} while (cursor.moveToNext());
						} 
					}
					 
					slNo++;
				} while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);
			Log.d("Detail", detail_json);
			
			if(batch_json.matches("")){
			}else{
				batch_json = batch_json.substring(0, batch_json.length() - 1);
			}
			
			Log.d("batch_json", batch_json);
		}
		 
		 String appType = LogOutSetGet.getApplicationType();
		// Add barcode for product
			if (appType.matches("W")) {
				//	  productid = sqldb.distinctprodId();
				productid = sqldb.getBarcodeStatus();
					  
			  if(!productid.isEmpty()){
						  
					  for (int i = 0; i < productid.size(); i++) {
					   String prodtid = productid.get(i);
					   allproducts = sqldb.getAllprodValues(prodtid);  
					   String productSno = SOTDatabase.getProductSno(prodtid);
					   for (ProductBarcode prodbarcode : allproducts) {

					 //   int prodsno = i+1;//prodbarcode.getSno();
					  //  String prodsno = prodbarcode.getSno();
					    String prodcode = prodbarcode.getProductcode();
					    String prodseqno = prodbarcode.getSeqno();
					    String prodweightbarcode = prodbarcode.getBarcode();
					    double prodweight = prodbarcode.getWeight();

					    barcode_json += productSno + "^" + prodcode + "^" + prodseqno + "^"
					      + prodweightbarcode + "^" + prodweight + "^" + "!";
					   }
					  }	
					  
					  barcode_json = barcode_json.substring(0, barcode_json.length() - 1);
					  
					  Log.d("barcode_json", barcode_json);
					  			 
		     }
				}
		 
		String locationCode = SalesOrderSetGet.getLocationcode();
		String supplierCode = SalesOrderSetGet.getSuppliercode();
		String currencyCode = SalesOrderSetGet.getCurrencycode();
		String remarks = SalesOrderSetGet.getRemarks();
		String currencyRate = SalesOrderSetGet.getCurrencyrate();
		String soDate = SalesOrderSetGet.getSaleorderdate();
		String user = SupplierSetterGetter.getUsername();
		String grainvoiceno = SalesOrderSetGet.getGrainvoiceno();
		String gradono = SalesOrderSetGet.getGradono();
		String grainvoicedate = SalesOrderSetGet.getGrainvoicedate();
		String gradodate = SalesOrderSetGet.getGradodate();
		String customername = SalesOrderSetGet.getCustomername();
		double blDsc = billDisct;
		double itemDisc = SOTDatabase.getitemDisc();
		double totDisc = blDsc + itemDisc;

		header_json = graNo +"^" + soDate + "^" + grainvoiceno + "^" + grainvoicedate
				+ "^" + gradono + "^" + gradodate + "^" + locationCode + "^"
				+ supplierCode + "^" + ttl + "^" + itemDisc + "^" + blDsc + "^"
				+ totDisc + "^" + subTotal + "^" + totTax + "^" + netTotal
				+ "^" + remarks + "^" + currencyCode + "^" + currencyRate + "^"
				+ deviceId + "^" + "I" + "^" + user +"^" +customername;

		Log.d("Product header", header_json);

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo productJson = new PropertyInfo();
		PropertyInfo barcodeJson = new PropertyInfo();
		PropertyInfo sBatchDetail = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo productBarcodeJson = new PropertyInfo();
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		productJson.setName("sHeader");
		productJson.setValue(header_json);
		productJson.setType(String.class);
		request.addProperty(productJson);

		barcodeJson.setName("sDetail");
		barcodeJson.setValue(detail_json);
		barcodeJson.setType(String.class);
		request.addProperty(barcodeJson);
		
		sBatchDetail.setName("sBatchDetail");
		sBatchDetail.setValue(batch_json);
		sBatchDetail.setType(String.class);
		request.addProperty(sBatchDetail);
		
		productBarcodeJson.setName("sCartonDetail");
		productBarcodeJson.setValue(barcode_json);
		productBarcodeJson.setType(String.class);
		request.addProperty(productBarcodeJson);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

		androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		String resTxt = response.toString();

		Log.d("Summary Product", resTxt);

		result = " { SOResult : " + resTxt + "}";

		JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(result);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				sum_result = jsonChildNode.optString("Result").toString();

				if (sum_result.matches("")) {
					sum_result = "failed";
					Log.d("Summary Result", sum_result);
				}
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return sum_result;
	}

	public static String summarySRService(String SRNo,String webMethName, double billDisct,
			String subTotal, String totTax, String netTotal, String ttl)
			throws JSONException, IOException, XmlPullParserException {

		SOTDatabase.init(context);
		cursor = SOTDatabase.getCursor();
		double totalDiscount = 0, subTot = 0, tx = 0, ntTtl = 0;
		int slNo = 1;
		detail_json = "";
		header_json = "";
		String batch_json="";
		attribute_json ="";
		double sbtot = SOTDatabase.getsumsubTot();
		double billDiscount = billDisct / sbtot;
		double billDiscs;
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					String srslno = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
					String pCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String pName = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
					String cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
					String foc = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_FOC));
					String price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
					String total = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TOTAL));
					String itemDiscount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT));
//					String taxType = cursor.getString(cursor
//							.getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
					String taxValue = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));

					String taxType = SalesOrderSetGet.getCompanytax();
//					String taxValue = SalesOrderSetGet.getTaxValue();

					String retail_Price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_RETAILPRICE));

					String sub_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL));
					String net_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
					String tax_amount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAX));
					String stockadj_type = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));
					String cprice = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));

					double sbTotal = SOTDatabase.getsubTotal(slNo);
					billDiscs = billDiscount * sbTotal;

					totalDiscount = billDiscs
							+ Double.parseDouble(itemDiscount);
					subTot = Double.parseDouble(sub_ToTal);
					ntTtl = Double.parseDouble(net_ToTal);

					tx = Double.parseDouble(tax_amount);
					String billDisc = twoDecimalPoint(billDiscs);
					String subTtl = twoDecimalPoint(subTot);
					String netTtl = twoDecimalPoint(ntTtl);
					String txAmt = fourDecimalPoint(tx);
					String totalDisc = twoDecimalPoint(totalDiscount);

					detail_json += slNo + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^" + foc + "^"
							+ pcsPerCarton + "^" + retail_Price + "^" + price
							+ "^" + total + "^" + itemDiscount + "^" + billDisc
							+ "^" + totalDisc + "^" + subTtl + "^" + txAmt
							+ "^" + netTtl + "^" + taxType + "^" + taxValue
							+ "^" + "I" + "^" + stockadj_type + "^" + cprice +"!";


					//////////////////////////////////////////////////////////////
					// Batch start

					Cursor cursor = SOTDatabase.getBatchCursorWithSR(pCode,srslno);

					if (cursor != null && cursor.getCount() > 0) {

						if (cursor.moveToFirst()) {
							do {

								String bpCode = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));


								String bNo = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_BATCH_NO));
								String exDate = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_EXPIRY_DATE));
								String mfgDate = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_MFG_DATE));
								String bcQty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
								String blQty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
								String bqty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
								String bfoc = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_FOC));
								String bprice = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));


								if(exDate!=null && !exDate.isEmpty()){

								}else{
									exDate= "01/01/1900";
								}

								if(mfgDate!=null && !mfgDate.isEmpty()){

								}else{
									mfgDate= "01/01/1900";
								}
								Log.d("save batch", "mfgDate : "+mfgDate);

								double cqy=0,lqy=0,qy=0,fqy=0;

								if(bcQty!=null && !bcQty.isEmpty()){
									cqy = Double.parseDouble(bcQty);
								}else{
									bcQty="";
								}

								if(blQty!=null && !blQty.isEmpty()){
									lqy = Double.parseDouble(blQty);
								}else{
									blQty="";
								}

								if(bqty!=null && !bqty.isEmpty()){
									qy = Double.parseDouble(bqty);
								}else{
									bqty="";
								}

								if(bfoc!=null && !bfoc.isEmpty()){
									fqy = Double.parseDouble(bfoc);
								}else{
									bfoc="";
								}

								if(cqy>0 || lqy>0 || qy>0 || fqy>0){
									batch_json += slNo + "^" + bpCode + "^" + bNo + "^" + exDate + "^"+ mfgDate + "^" + "" + "^"
											+ bcQty + "^" + blQty + "^" + bqty + "^" + bfoc + "^"
											+ bprice + "!";
								}

							} while (cursor.moveToNext());
						}

						batch_json = batch_json.substring(0, batch_json.length() - 1);
						Log.d("batch_json", batch_json);
						//////////////////////////////////////////////////////
						// Batch End
					}
					slNo++;
				} while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);

		}
		Log.d("Detail", detail_json);

		attributecursor = SOTDatabase.getAttributeCursor();

		boolean isSizeQty = false;
		if (attributecursor != null && attributecursor.getCount() > 0) {

			if (attributecursor.moveToFirst()) {
				do {
					String slno = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
					String pName = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
					String productcode = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String colorcode = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_COLOR_CODE));
					String colorname = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_COLOR_NAME));
					String sizecode = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_SIZE_CODE));
					String sizename = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_SIZE_NAME));
					String quantity = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
					String flag = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_FLAG));

					int iQty = Integer.valueOf(quantity);
					if(iQty>0) {
						isSizeQty = true;
						attribute_json += slno + "^" + productcode + "^" + colorcode + "^"
								+ sizecode + "^" + "0" + "^" + "" + "^" + "0" + "^"
								+ "0" + "^" + quantity + "^" + "0"
								+ "^" + "0" +  "!";
					}

					slNo++;
				} while (attributecursor.moveToNext());
			}

			if(isSizeQty){
				attribute_json = attribute_json.substring(0, attribute_json.length() - 1);
			}
			Log.d("AttributeDetail", attribute_json);
		}

		String locationCode = SalesOrderSetGet.getLocationcode();
		String customerCode = SalesOrderSetGet.getCustomercode();
		String remarks = SalesOrderSetGet.getRemarks();
		String soDate = SalesOrderSetGet.getSaleorderdate();
		String user = SupplierSetterGetter.getUsername();
		String invoiceno = SalesOrderSetGet.getSrinvoiceno();

		String currencycode = SalesOrderSetGet.getLocalCurrency();
		String currencyrate = SalesOrderSetGet.getCurrencyrate();

		double blDsc = billDisct;
		double itemDisc = SOTDatabase.getitemDisc();
		double totDisc = blDsc + itemDisc;

		header_json = SRNo+"^" + soDate + "^" + invoiceno + "^" + locationCode + "^"
				+ customerCode + "^" + ttl + "^" + itemDisc + "^" + blDsc + "^"
				+ totDisc + "^" + subTotal + "^" + totTax + "^" + netTotal
				+ "^" + remarks + "^" + deviceId + "^" + "I" + "^" + user
				+"^" + currencycode +"^" + currencyrate;

		Log.d("ProductHeader", header_json);

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo productJson = new PropertyInfo();
		PropertyInfo barcodeJson = new PropertyInfo();
		PropertyInfo BatchDetail = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo AttributeJson = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		productJson.setName("sHeader");
		productJson.setValue(header_json);
		productJson.setType(String.class);
		request.addProperty(productJson);

		barcodeJson.setName("sDetail");
		barcodeJson.setValue(detail_json);
		barcodeJson.setType(String.class);
		request.addProperty(barcodeJson);

		BatchDetail.setName("sBatchDetail");
		BatchDetail.setValue(batch_json);
		BatchDetail.setType(String.class);
		request.addProperty(BatchDetail);

		AttributeJson.setName("sAttributeDetail");
		AttributeJson.setValue(attribute_json);
		AttributeJson.setType(String.class);
		request.addProperty(AttributeJson);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

		androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		String resTxt = response.toString();

		Log.d("Summary Product", resTxt);

		result = " { SOResult : " + resTxt + "}";

		JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(result);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

				sum_result = jsonChildNode.optString("Result").toString();

				if (sum_result.matches("")) {
					sum_result = "failed";
					Log.d("Summary Result", sum_result);
				}

			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return sum_result;
	}

	public static String StockRequestSummary(String webMethName,String stockReqNo)
			throws IOException, XmlPullParserException {
		
		header_json="";
		detail_json="";
		SOTDatabase.init(context);
		cursor = SOTDatabase.getCursor();
		int slNo = 1;
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {

					String pCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String pName = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));

					String price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));

					String cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));

					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));

					detail_json += slNo + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^"
							+ pcsPerCarton + "^" + "0" + "^" + price + "^"
							+ "I" + "!";

					slNo++;
				} while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);
			Log.d("Detail", detail_json);
		}
		String user = SupplierSetterGetter.getUsername();
		String serverdate = SalesOrderSetGet.getSaleorderdate();

		String fromlocationcode = SupplierSetterGetter.getLocationcode();
		HashMap<String, String> location_code_name = new HashMap<String, String>();
		location_code_name = SupplierSetterGetter.getLoc_code_name();
		String fromlocation_code = location_code_name.get(fromlocationcode);
		String tolocationcode = SupplierSetterGetter.getLocCode();
		String remarks = SalesOrderSetGet.getRemarks();

		if(remarks!=null && !remarks.isEmpty()){

		}else{
			remarks="";
		}

		header_json = stockReqNo +"^" + serverdate + "^" + fromlocation_code + "^"
				+ tolocationcode + "^" + remarks + "^" + "0" + "^" + deviceId
				+ "^" + user;

		Log.d("Product header", header_json);

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo productJson = new PropertyInfo();
		PropertyInfo barcodeJson = new PropertyInfo();

		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		productJson.setName("sHeader");
		productJson.setValue(header_json);
		productJson.setType(String.class);
		request.addProperty(productJson);

		barcodeJson.setName("sDetail");
		barcodeJson.setValue(detail_json);
		barcodeJson.setType(String.class);
		request.addProperty(barcodeJson);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

		androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		String resTxt = response.toString();

		Log.d("Summary Stock Request", resTxt);

		result = " { SRResult : " + resTxt + "}";

		JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(result);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("SRResult");

			/*********** Process each JSON Node ************/
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				sum_result = jsonChildNode.optString("Result").toString();

				if (sum_result.matches("")) {
					sum_result = "failed";
					Log.d("Summary Result", sum_result);
				}
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return sum_result;
	}

	public static String SaveTransferReceive(String webMethName, String transferno)
			throws IOException, XmlPullParserException {

		String user = SupplierSetterGetter.getUsername();
		String remarks = SalesOrderSetGet.getRemarks();
		detail_json = "";
		SOTDatabase.init(context);
		sqldb = new SOTDatabase(context);
		cursor = SOTDatabase.getCursor();
		int slNo = 1;
		String appType = LogOutSetGet.getApplicationType();
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {

					String pCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));

					String srslno = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));

					String pName = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));

					String cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));

					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));


//					if (!transferno.matches("")) {
//						slNo = cursor.getInt(cursor.getColumnIndex(SOTDatabase.COLUMN_SO_SLNO));
//					}

					detail_json += slNo + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^"
							+ pcsPerCarton +"!";

					slNo++;
				} while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);

			Log.d("Detail", detail_json +"   "+user);
		}

			SoapObject request = new SoapObject(NAMESPACE, webMethName);

			PropertyInfo User = new PropertyInfo();
			PropertyInfo TransferNo = new PropertyInfo();
			PropertyInfo DetailArr = new PropertyInfo();
			PropertyInfo companyCode = new PropertyInfo();

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			TransferNo.setName("TransferNo");
			TransferNo.setValue(transferno);
			TransferNo.setType(String.class);
			request.addProperty(TransferNo);

			DetailArr.setName("sDetail");
			DetailArr.setValue(detail_json);
			DetailArr.setType(String.class);
			request.addProperty(DetailArr);

			User.setName("User");
			User.setValue(user);
			User.setType(String.class);
			request.addProperty(User);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			String resTxt = response.toString();

			Log.d("TransferReceive", resTxt);

			result = " { TRResult : " + resTxt + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse.optJSONArray("TRResult");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					sum_result = jsonChildNode.optString("Result").toString();

					if (sum_result.matches("")) {
						sum_result = "failed";
						Log.d("Summary Result", sum_result);
					}
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

			return sum_result;

	}

	public static String TransferSummary(String webMethName, String stockReqNo)
			throws IOException, XmlPullParserException {
		
		String batch_json="";
		header_json="";
		detail_json="";
		barcode_json = "";
		SOTDatabase.init(context);
		sqldb = new SOTDatabase(context);
		cursor = SOTDatabase.getCursor();
		int slNo = 1;
		String appType = LogOutSetGet.getApplicationType();
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {

					String pCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					
					String srslno = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
					
					String pName = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));

					String price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));

					String cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));

					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));

					
					if(!stockReqNo.matches("")){
						slNo = cursor.getInt(cursor.getColumnIndex(SOTDatabase.COLUMN_SO_SLNO));
					}
										
					detail_json += slNo + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^"
							+ pcsPerCarton + "^" + price + "^" + "0" + "^"
							+ "I" + "!";

					Cursor cursor = SOTDatabase.getBatchCursorWithSR(pCode,srslno);
					
					// sr delete
//					Cursor cursor = null;
//					
//					if(!stockReqNo.matches("")){
//						cursor = SOTDatabase.getBatchCursorWithSR(pCode,srslno);
//					}else{
//						cursor = SOTDatabase.getBatchCursor(pCode);
//					}
					
					if (cursor != null && cursor.getCount() > 0) {

						if (cursor.moveToFirst()) {
							do {
								
								String bpCode = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));							
//								String batchSlNo = SOTDatabase.getProdSlno(pCode);					
								String bNo = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_BATCH_NO));	
								String exDate = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_EXPIRY_DATE));						
								String bcQty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
								String blQty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
								String bqty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
									
								if(exDate!=null && !exDate.isEmpty()){
									
								}else{
									exDate= "01/01/1900";
								}
								
//								if(!stockReqNo.matches("")){
//									batchSlNo = srslno;
//								}
								
								batch_json += slNo + "^" + bpCode + "^" + bNo + "^" + exDate + "^"+ "01/01/1900" + "^" + "" + "^"
										+ bcQty + "^" + blQty + "^" + bqty + "!";
								
							} while (cursor.moveToNext());
						}
	 
					}
		         slNo++;
				} while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);
			
			if(!batch_json.matches("")){
				batch_json = batch_json.substring(0, batch_json.length() - 1);
			}
			
			Log.d("Detail", detail_json);
			
			 Log.d("batch_json", batch_json);
		}

		// Add barcode for product
		if (appType.matches("W")) {
			//	  productid = sqldb.distinctprodId();
			productid = sqldb.getBarcodeStatus();

			if(!productid.isEmpty()){

				for (int i = 0; i < productid.size(); i++) {
					String prodtid = productid.get(i);
					allproducts = sqldb.getAllprodValues(prodtid);
					String productSno = SOTDatabase.getProductSno(prodtid);
					for (ProductBarcode prodbarcode : allproducts) {

						//   int prodsno = i+1;//prodbarcode.getSno();
						//  String prodsno = prodbarcode.getSno();
						String prodcode = prodbarcode.getProductcode();
						String prodseqno = prodbarcode.getSeqno();
						String prodweightbarcode = prodbarcode.getBarcode();
						double prodweight = prodbarcode.getWeight();

						barcode_json += productSno + "^" + prodcode + "^" + prodseqno + "^"
								+ prodweightbarcode + "^" + prodweight + "^" + "!";
					}
				}

				barcode_json = barcode_json.substring(0, barcode_json.length() - 1);

				Log.d("barcode_json", barcode_json);

			}
		}

		String fromlocation_code;
		String user = SupplierSetterGetter.getUsername();
		String serverdate = SalesOrderSetGet.getSaleorderdate();
		HashMap<String, String> location_code_name = new HashMap<String, String>();
		location_code_name = SupplierSetterGetter.getLoc_code_name();
		if (SalesOrderSetGet.getTransferchangefromloc().matches("1")) {
			String fromlocationname = SalesOrderSetGet.getTransferfromloc();
			fromlocation_code = location_code_name.get(fromlocationname);
		} else {
			String fromlocationname = SupplierSetterGetter.getLocationcode();
			fromlocation_code = location_code_name.get(fromlocationname);
		}

		String tolocationcode = SupplierSetterGetter.getLocCode();
		String remarks = SalesOrderSetGet.getRemarks();

		if(!stockReqNo.matches("")){
			fromlocation_code = SalesOrderSetGet.getToLoc();
			tolocationcode = SalesOrderSetGet.getFromLoc();
		}
		
		header_json = "^" + serverdate + "^" + fromlocation_code + "^"
				+ tolocationcode + "^" + "0" + "^" + stockReqNo + "^" + remarks
				+ "^" + deviceId + "^" + user;

		Log.d("Productheader", header_json);
		try {
			SoapObject request = new SoapObject(NAMESPACE, webMethName);

			PropertyInfo productJson = new PropertyInfo();
			PropertyInfo barcodeJson = new PropertyInfo();
			PropertyInfo sBatchDetailJson = new PropertyInfo();
			PropertyInfo productBarcodeJson = new PropertyInfo();

			PropertyInfo companyCode = new PropertyInfo();

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			productJson.setName("sHeader");
			productJson.setValue(header_json);
			productJson.setType(String.class);
			request.addProperty(productJson);

			barcodeJson.setName("sDetail");
			barcodeJson.setValue(detail_json);
			barcodeJson.setType(String.class);
			request.addProperty(barcodeJson);
			
			sBatchDetailJson.setName("sBatchDetail");
			sBatchDetailJson.setValue(batch_json);
			sBatchDetailJson.setType(String.class);
			request.addProperty(sBatchDetailJson);

			productBarcodeJson.setName("sCartonDetail");
			productBarcodeJson.setValue(barcode_json);
			productBarcodeJson.setType(String.class);
			request.addProperty(productBarcodeJson);
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			String resTxt = response.toString();

			Log.d("Summary Transfer", resTxt);

			result = " { SRResult : " + resTxt + "}";

			JSONObject jsonResponse;

			jsonResponse = new JSONObject(result);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("SRResult");

			/*********** Process each JSON Node ************/
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

				/******* Fetch node values **********/
				sum_result = jsonChildNode.optString("Result").toString();

				if (sum_result.matches("")) {
					sum_result = "failed";
					Log.d("Summary Result", sum_result);
				}
			}

		} catch (JSONException e) {
			sum_result = "failed";
			e.printStackTrace();
		} catch (IOException e) {
			sum_result = "failed";
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			sum_result = "failed";
			e.printStackTrace();
		}

		return sum_result;
	}
	
	public static String StockAjustmentSummary(String webMethName, String stockReqNo)
			throws IOException, XmlPullParserException {
		
		  String add_batch_json = "", deduct_batch_json="";
		  header_json="";
		  detail_json="";
		  SOTDatabase.init(context);
		  cursor = SOTDatabase.getCursor();
		  int slNo = 1;
		  if (cursor != null && cursor.getCount() > 0) {

		   if (cursor.moveToFirst()) {
		    do {

		     String pCode = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
		     String pName = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));

		     String price = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));

		     String cQty = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
		     String lQty = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
		     String qty = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
		     
		     String qtyinhand = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_FOC));
		     
		     String adddetect = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));

		     String pcsPerCarton = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));

		     detail_json += slNo + "^" + pCode + "^" + pName + "^" + adddetect + "^" 
		       + cQty + "^" + lQty + "^" + qty + "^"
		       + pcsPerCarton + "^" + qtyinhand +"^" + price + "!";
		     
		     Cursor cursor = null;
		     if (adddetect.matches("1")) {
			     cursor = SOTDatabase.getBatchCursorWithSR(pCode, slNo+"");
		     } else if (adddetect.matches("-1")) {
		    	cursor = SOTDatabase.getBatchCursor(pCode);
		     }
		    
		     if (cursor != null && cursor.getCount() > 0) {

		      if (cursor.moveToFirst()) {
		       do {
		        
		        String bpCode = cursor.getString(cursor
		          .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));       
		        String batchSlNo = SOTDatabase.getProdSlno(pCode);     
		        String bNo = cursor.getString(cursor
		          .getColumnIndex(SOTDatabase.COLUMN_BATCH_NO)); 
		        String exDate = cursor.getString(cursor
		          .getColumnIndex(SOTDatabase.COLUMN_EXPIRY_DATE));      
		        String bcQty = cursor.getString(cursor
		          .getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
		        String blQty = cursor.getString(cursor
		          .getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
		        String bqty = cursor.getString(cursor
		          .getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
		         
		        if(exDate!=null && !exDate.isEmpty()){
		         
		        }else{
		         exDate= "01/01/1900";
		        }
		        
		        if (adddetect.matches("1")) {
		         add_batch_json += slNo + "^" + bpCode + "^" + bNo + "^" + exDate + "^"+ "01/01/1900" + "^" + "" + "^"
		           + bcQty + "^" + blQty + "^" + bqty + "!";
		         
		        } else if (adddetect.matches("-1")) {
		         deduct_batch_json += slNo + "^" + bpCode + "^" + bNo + "^" + exDate + "^"+ "01/01/1900" + "^" + "" + "^"
		           + bcQty + "^" + blQty + "^" + bqty + "!";
		         
		        }
		        
		       } while (cursor.moveToNext());
		      }
		  
		     }
		     
		     slNo++;
		    } while (cursor.moveToNext());
		   }
		   
		   detail_json = detail_json.substring(0, detail_json.length() - 1);
		   
		   if(!add_batch_json.matches("")){
		    add_batch_json = add_batch_json.substring(0, add_batch_json.length() - 1);
		   }
		   
		   if(!deduct_batch_json.matches("")){
		    deduct_batch_json = deduct_batch_json.substring(0, deduct_batch_json.length() - 1);
		   }
		   
		   Log.d("Detail", detail_json);
		   Log.d("add_batch_json", add_batch_json);
		      Log.d("detect_batch_json", deduct_batch_json);
		  }

		  String user = SupplierSetterGetter.getUsername();
		  String serverdate = SalesOrderSetGet.getCurrentdate();
		  String remarks = SalesOrderSetGet.getRemarks();
		  String locationcode = SalesOrderSetGet.getLocationcode();
		  
		  header_json = "^" + serverdate + "^" + locationcode + "^" + remarks
				    + "^" + user +  "^" + deviceId;

				  Log.d("Product header", header_json);
				  try {
				   SoapObject request = new SoapObject(NAMESPACE, webMethName);

				   PropertyInfo productJson = new PropertyInfo();
				   PropertyInfo barcodeJson = new PropertyInfo();

				   PropertyInfo companyCode = new PropertyInfo();
				   PropertyInfo addbatchJson = new PropertyInfo();
				   PropertyInfo detectbatchJson = new PropertyInfo();

				   String cmpnyCode = SupplierSetterGetter.getCompanyCode();

				   companyCode.setName("CompanyCode");
				   companyCode.setValue(cmpnyCode);
				   companyCode.setType(String.class);
				   request.addProperty(companyCode);

				   productJson.setName("sHeader");
				   productJson.setValue(header_json);
				   productJson.setType(String.class);
				   request.addProperty(productJson);

				   barcodeJson.setName("sDetail");
				   barcodeJson.setValue(detail_json);
				   barcodeJson.setType(String.class);
				   request.addProperty(barcodeJson);
				   
				   addbatchJson.setName("sBatchAdd");
				   addbatchJson.setValue(add_batch_json);
				   addbatchJson.setType(String.class);
				   request.addProperty(addbatchJson);
				   
				   detectbatchJson.setName("sBatchDeduct");
				   detectbatchJson.setValue(deduct_batch_json);
				   detectbatchJson.setType(String.class);
				   request.addProperty(detectbatchJson);

				   SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				     SoapEnvelope.VER11);
				   envelope.dotNet = true;
				   envelope.setOutputSoapObject(request);
				   envelope.bodyOut = request;

				   HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

				   androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
				   SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				   String resTxt = response.toString();

				   Log.d("Stock Adjustment", resTxt);

				   result = " { StockAdjustmentResult : " + resTxt + "}";

				   JSONObject jsonResponse;

				   jsonResponse = new JSONObject(result);

				   JSONArray jsonMainNode = jsonResponse.optJSONArray("StockAdjustmentResult");

				   
				   int lengthJsonArr = jsonMainNode.length();
				   for (int i = 0; i < lengthJsonArr; i++) {
				   
				    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				    
				    sum_result = jsonChildNode.optString("Result").toString();

				    if (sum_result.matches("")) {
				     sum_result = "failed";
				     Log.d("Summary Result", sum_result);
				    }
				   }

				  } catch (JSONException e) {
				   sum_result = "failed";
				   e.printStackTrace();
				  } catch (IOException e) {
				   sum_result = "failed";
				   e.printStackTrace();
				  } catch (XmlPullParserException e) {
				   sum_result = "failed";
				   e.printStackTrace();
				  }

				  return sum_result;	
	}

	public static String TransferToStockAjustmentSave(String webMethName,String transferno)
			throws IOException, XmlPullParserException {

		String add_batch_json = "", deduct_batch_json="";
		header_json="";
		detail_json="";
		SOTDatabase.init(context);
		cursor = SOTDatabase.getCursor();
		int slNo = 1;
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {

					String pCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String pName = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));

					String price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));

					String cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));

					String qtyinhand = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_FOC));

					String adddetect = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));

					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));

					if(adddetect!=null && !adddetect.isEmpty()){

					}else{
						adddetect="";
					}

					if(adddetect.equalsIgnoreCase("") || adddetect.equalsIgnoreCase("0")){

					}else{
						detail_json += slNo + "^" + pCode + "^" + pName + "^" + adddetect + "^"
								+ cQty + "^" + lQty + "^" + qty + "^"
								+ pcsPerCarton + "^" + qtyinhand +"^" + price + "!";
					}



					/*Cursor cursor = null;
					if (adddetect.matches("1")) {
						cursor = SOTDatabase.getBatchCursorWithSR(pCode, slNo+"");
					} else if (adddetect.matches("-1")) {
						cursor = SOTDatabase.getBatchCursor(pCode);
					}

					if (cursor != null && cursor.getCount() > 0) {

						if (cursor.moveToFirst()) {
							do {

								String bpCode = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
								String batchSlNo = SOTDatabase.getProdSlno(pCode);
								String bNo = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_BATCH_NO));
								String exDate = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_EXPIRY_DATE));
								String bcQty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
								String blQty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
								String bqty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));

								if(exDate!=null && !exDate.isEmpty()){

								}else{
									exDate= "01/01/1900";
								}

								if (adddetect.matches("1")) {
									add_batch_json += slNo + "^" + bpCode + "^" + bNo + "^" + exDate + "^"+ "01/01/1900" + "^" + "" + "^"
											+ bcQty + "^" + blQty + "^" + bqty + "!";

								} else if (adddetect.matches("-1")) {
									deduct_batch_json += slNo + "^" + bpCode + "^" + bNo + "^" + exDate + "^"+ "01/01/1900" + "^" + "" + "^"
											+ bcQty + "^" + blQty + "^" + bqty + "!";

								}

							} while (cursor.moveToNext());
						}

					}*/

					slNo++;
				} while (cursor.moveToNext());
			}

			detail_json = detail_json.substring(0, detail_json.length() - 1);

			/*if(!add_batch_json.matches("")){
				add_batch_json = add_batch_json.substring(0, add_batch_json.length() - 1);
			}

			if(!deduct_batch_json.matches("")){
				deduct_batch_json = deduct_batch_json.substring(0, deduct_batch_json.length() - 1);
			}*/
			//03000004
			Log.d("Detail", detail_json);
//			Log.d("add_batch_json", add_batch_json);
//			Log.d("detect_batch_json", deduct_batch_json);
		}

		String user = SupplierSetterGetter.getUsername();
		String serverdate = SalesOrderSetGet.getCurrentdate();
//		String remarks = SalesOrderSetGet.getRemarks();
		String locationcode = SalesOrderSetGet.getLocationcode();

		header_json = "^" + serverdate + "^" + locationcode + "^" + transferno
				+ "^" + user +  "^" + deviceId;

		Log.d("Productheader", header_json);
		try {
			SoapObject request = new SoapObject(NAMESPACE, webMethName);

			PropertyInfo productJson = new PropertyInfo();
			PropertyInfo barcodeJson = new PropertyInfo();

			PropertyInfo companyCode = new PropertyInfo();
//			PropertyInfo addbatchJson = new PropertyInfo();
//			PropertyInfo detectbatchJson = new PropertyInfo();

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			productJson.setName("sHeader");
			productJson.setValue(header_json);
			productJson.setType(String.class);
			request.addProperty(productJson);

			barcodeJson.setName("sDetail");
			barcodeJson.setValue(detail_json);
			barcodeJson.setType(String.class);
			request.addProperty(barcodeJson);

//			addbatchJson.setName("sBatchAdd");
//			addbatchJson.setValue(add_batch_json);
//			addbatchJson.setType(String.class);
//			request.addProperty(addbatchJson);

//			detectbatchJson.setName("sBatchDeduct");
//			detectbatchJson.setValue(deduct_batch_json);
//			detectbatchJson.setType(String.class);
//			request.addProperty(detectbatchJson);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			String resTxt = response.toString();

			Log.d("Stock Adjustment", resTxt);

			result = " { StockAdjustmentResult : " + resTxt + "}";

			JSONObject jsonResponse;

			jsonResponse = new JSONObject(result);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("StockAdjustmentResult");


			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

				sum_result = jsonChildNode.optString("Result").toString();

				if (sum_result.matches("")) {
					sum_result = "failed";
					Log.d("Summary Result", sum_result);
				}
			}

		} catch (JSONException e) {
			sum_result = "failed";
			e.printStackTrace();
		} catch (IOException e) {
			sum_result = "failed";
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			sum_result = "failed";
			e.printStackTrace();
		}

		return sum_result;
	}

	public static String saveSignatureImage(String invoiceno, String lat,
			String longtitude, String signature, String productImage,String webMethNameGet,
			String transType, String address1, String address2) {

		String imgResult = "";
		String user = SupplierSetterGetter.getUsername();

		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);

		PropertyInfo InvoiceNo = new PropertyInfo();
		PropertyInfo Latitude = new PropertyInfo();
		PropertyInfo Longitude = new PropertyInfo();
		PropertyInfo Signature = new PropertyInfo();
		PropertyInfo User = new PropertyInfo();
		PropertyInfo RefPhoto = new PropertyInfo();
		
		PropertyInfo TranType = new PropertyInfo();
		PropertyInfo Address1 = new PropertyInfo();
		PropertyInfo Address2 = new PropertyInfo();
		
		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		Latitude.setName("InvoiceNo");
		Latitude.setValue(invoiceno);
		Latitude.setType(String.class);
		request.addProperty(Latitude);

		InvoiceNo.setName("Latitude");
		InvoiceNo.setValue(lat);
		InvoiceNo.setType(String.class);
		request.addProperty(InvoiceNo);

		Longitude.setName("Longitude");
		Longitude.setValue(longtitude);
		Longitude.setType(String.class);
		request.addProperty(Longitude);

		Signature.setName("Signature");
		Signature.setValue(signature);
		Signature.setType(Byte.class);
		request.addProperty(Signature);

		RefPhoto.setName("RefPhoto");
		RefPhoto.setValue(productImage);
		RefPhoto.setType(Byte.class);
		request.addProperty(RefPhoto);

		User.setName("User");
		User.setValue(user);
		User.setType(String.class);
		request.addProperty(User);

		TranType.setName("TranType");
		TranType.setValue(transType);
		TranType.setType(String.class);
		request.addProperty(TranType);

		Address1.setName("Address1");
		Address1.setValue(address1);
		Address1.setType(String.class);
		request.addProperty(Address1);

		Address2.setName("Address2");
		Address2.setValue(address2);
		Address2.setType(String.class);
		request.addProperty(Address2);

		Log.d("invoiceno","invoiceno"+invoiceno);
		Log.d("Latitude","Latitude"+lat);
		Log.d("Longitude","Longitude"+longtitude);
		Log.d("transType","transType"+transType);
		Log.d("address1","address1"+address1);
		Log.d("address2","address2"+address2);
	
		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;

			Log.e("Save Image Request data", request.toString());

			envelope.setOutputSoapObject(request);


			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

			androidHttpTransport = new HttpTransportSE(validURL);

			androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			String value = response.toString();
			Log.d("sigserviceresponse", value);
			result = " { SaveSignature : " + value + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SaveSignature");

				int lengthJsonArr = jsonMainNode.length();

				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					imgResult = jsonChildNode.optString("Result").toString();
					Log.d("ImageResult", imgResult);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return imgResult;
	}

	public static String placeOrder(String webMethName) throws JSONException,
			IOException, XmlPullParserException {

		DBCatalog.init(context);
		cursor = DBCatalog.getCursor();
		int slNo = 1;
		detail_json = "";
		header_json = "";
		double ntTtl, totl, smTax, sbTtl, tx, subTot, totlitemDisc, nettot;
		String totltax, subtotl;
		double billDisct;
		totl = DBCatalog.getTotal();

		nettot = DBCatalog.getNetTotal();

		totlitemDisc = DBCatalog.getitemDisc();

		smTax = DBCatalog.getTax();
		totltax = fourDecimalPoint(smTax);

		sbTtl = DBCatalog.getSubTotal();
		subtotl = twoDecimalPoint(sbTtl);

		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					String pCode = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PRODUCT_CODE));
					String pName = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PRODUCT_NAME));
					String cQty = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_LOOSE_QTY));
					String price = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PRICE));
					String qty = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_QUANTITY));
					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PCSPERCARTON));
					String wholesaleprice = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_WHOLESALEPRICE));
					String total = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_TOTAL));
					String foc = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_FOC));
					String tax = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_TAX));
					String taxtype = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_TAXTYPE));
					String taxtvalue = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_TAXVALUE));
					String itemdiscount = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_ITEM_DISCOUNT));
					String nettotal = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_NETTOTAL));
					String sub_ToTal = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_SUB_TOTAL));
					
					String cprice = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));
					
					String exchangeQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));
					
					
					tx = Double.parseDouble(tax);
					ntTtl = Double.parseDouble(nettotal);

					subTot = Double.parseDouble(sub_ToTal);
					String subTtl = twoDecimalPoint(subTot);
					String txAmt = fourDecimalPoint(tx);

					detail_json += slNo + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^" + foc // --->
																		// FOCQty
							+ "^" + pcsPerCarton + "^" + wholesaleprice // ----->retail
																		// price
							+ "^" + price + "^" + total + "^" + itemdiscount // --->
																				// itemdiscount
							+ "^" + "" // ---> BillDisc
							+ "^" + "" // ---> Total disc
							+ "^" + subTtl // ---> subtotal
							+ "^" + txAmt // ---> tax
							+ "^" + ntTtl // ---->net total
							+ "^" + taxtype // ---> tax type
							+ "^" + taxtvalue // ---> tax value
							+ "^" + "I" 
							+ "^" + exchangeQty // ---> exchange qty
							+ "^" + cprice // ---> carton price
							+ "!";
					Log.d("DetailSO", detail_json);

					slNo++;
				} while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);
		}
		attributecursor = SOTDatabase.getAttributeCursor();

		boolean isSizeQty = false;
		if (attributecursor != null && attributecursor.getCount() > 0) {

			if (attributecursor.moveToFirst()) {
				do {
					String slno = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
					String pName = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
					String productcode = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String colorcode = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_COLOR_CODE));
					String colorname = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_COLOR_NAME));
					String sizecode = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_SIZE_CODE));
					String sizename = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_SIZE_NAME));
					String quantity = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
					String flag = attributecursor.getString(attributecursor
							.getColumnIndex(SOTDatabase.COLUMN_FLAG));

					int iQty = Integer.valueOf(quantity);
					if(iQty>0) {
						isSizeQty = true;
						attribute_json += slno + "^" + productcode + "^" + colorcode + "^"
								+ sizecode + "^" + "0" + "^" + "" + "^" + "0" + "^"
								+ "0" + "^" + quantity + "^" + "0"
								+ "^" + "0" +  "!";
					}

					slNo++;
				} while (attributecursor.moveToNext());
			}

			if(isSizeQty){
				attribute_json = attribute_json.substring(0, attribute_json.length() - 1);
			}
			Log.d("AttributeDetail", attribute_json);
		}

		String locationCode = SalesOrderSetGet.getLocationcode();
		String customerCode = Catalog.getCustomerCode();
		String soDate = SalesOrderSetGet.getSaleorderdate();
		String user = SupplierSetterGetter.getUsername();
		String currencyCode = SalesOrderSetGet.getCurrencycode();
		String currencyRate = SalesOrderSetGet.getCurrencyrate();
		  
//		Log.d("currencyCode", "cc"+currencyCode);
//		Log.d("currencyRate", "cr"+currencyRate);
		
		header_json = "^" + soDate + "^" + soDate // ---> DeliveryDate
				+ "^" + locationCode + "^" + customerCode + "^" + totl // --->
																		// Total
				+ "^" + totlitemDisc // ---> ItemDiscount
				+ "^" + "" // ---> BillDIscount
				+ "^" + "" // ---> TotalDiscount
				+ "^" + subtotl // ---> SubTotal
				+ "^" + totltax // ---> Tot Tax
				+ "^" + nettot + "^" + "" // ---> Remarks
				+ "^" + "0" // ---> Status
				+ "^" + currencyCode // ---> CurrencyCode
				+ "^" + currencyRate // ---> CurrencyRate
				+ "^" + deviceId + "^" + user;

		Log.d("ProductHeaderSO", header_json);

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo productJson = new PropertyInfo();
		PropertyInfo barcodeJson = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo attributeJson = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		productJson.setName("sHeader");
		productJson.setValue(header_json);
		productJson.setType(String.class);
		request.addProperty(productJson);

		barcodeJson.setName("sDetail");
		barcodeJson.setValue(detail_json);
		barcodeJson.setType(String.class);
		request.addProperty(barcodeJson);

		attributeJson.setName("sAttributeDetail");
		attributeJson.setValue(attribute_json);
		attributeJson.setType(String.class);
		request.addProperty(attributeJson);

		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			String resTxt = response.toString();

			Log.d("Summary Product", resTxt);

			result = " { SOResult : " + resTxt + "}";

			JSONObject jsonResponse;

			jsonResponse = new JSONObject(result);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");

			/*********** Process each JSON Node ************/
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

				/******* Fetch node values **********/
				sum_result = jsonChildNode.optString("Result").toString();

				if (sum_result == null || sum_result.matches("")) {
					sum_result = "failed";
					Log.d("Summary Result", sum_result);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sum_result;
	}

	public static String placeOrderInvoice(String webMethName) throws JSONException,
			IOException, XmlPullParserException {

		DBCatalog.init(context);
		cursor = DBCatalog.getCursor();
		int slNo = 1;
		detail_json = "";
		header_json = "";
		double ntTtl, totl, smTax, sbTtl, tx, subTot, totlitemDisc, nettot;
		String totltax, subtotl;
		double billDiscs;
		double billDisct =0.00;
		double sbtot = SOTDatabase.getsumsubTot();
		String bill_disc =CustomerSetterGetter.getDiscountPercentage();
		Log.d("bill_disc","-->"+bill_disc);
		double disc =Double.parseDouble(bill_disc);
		if(disc >0){
			 billDisct =Double.parseDouble(bill_disc);
		}

		double billDiscount = billDisct / sbtot;
		totl = DBCatalog.getTotal();

		nettot = DBCatalog.getNetTotal();

		totlitemDisc = DBCatalog.getitemDisc();

		smTax = DBCatalog.getTax();
		totltax = fourDecimalPoint(smTax);

		sbTtl = DBCatalog.getSubTotal();
		subtotl = twoDecimalPoint(sbTtl);

		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					String pCode = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PRODUCT_CODE));
					String pName = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PRODUCT_NAME));
					String cQty = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_LOOSE_QTY));
					String price = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PRICE));
					String qty = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_QUANTITY));
					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_PCSPERCARTON));
					String wholesaleprice = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_WHOLESALEPRICE));
					String total = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_TOTAL));
					String foc = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_FOC));
					String tax = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_TAX));
					String taxtype = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_TAXTYPE));
					String taxtvalue = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_TAXVALUE));
					String itemdiscount = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_ITEM_DISCOUNT));
					String nettotal = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_NETTOTAL));
					String sub_ToTal = cursor.getString(cursor
							.getColumnIndex(Constants.COLUMN_SUB_TOTAL));

					String cprice = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));

					String exchangeQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));
//					String SOSlno = cursor.getString(cursor
//							.getColumnIndex(SOTDatabase.COLUMN_SO_SLNO));
//
//					if(SOSlno!=null && !SOSlno.isEmpty()){
//
//					}else{
//						SOSlno="0";
//					}

					tx = Double.parseDouble(tax);
					ntTtl = Double.parseDouble(nettotal);

					subTot = Double.parseDouble(sub_ToTal);
					String subTtl = twoDecimalPoint(subTot);
					String txAmt = fourDecimalPoint(tx);

					double sbTotal = SOTDatabase.getsubTotal(slNo);
					Log.d("sbTotal","-->"+sbTotal);
					billDiscs = billDiscount * sbTotal;

					Log.d("billDiscs","-->"+billDiscs);

					/*detail_json += slNo + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^" + foc // --->
							// FOCQty
							+ "^" + pcsPerCarton + "^" + wholesaleprice // ----->retail
							// price
							+ "^" + price + "^" + total + "^" + itemdiscount // --->
							// itemdiscount
							+ "^" + "" // ---> BillDisc
							+ "^" + "" // ---> Total disc
							+ "^" + subTtl // ---> subtotal
							+ "^" + txAmt // ---> tax
							+ "^" + ntTtl // ---->net total
							+ "^" + taxtype // ---> tax type
							+ "^" + taxtvalue // ---> tax value
							+ "^" + "I"
							+ "^" + exchangeQty // ---> exchange qty
							+ "^" + cprice // ---> carton price
							+ "!";*/

					String taxcode = CustomerSetterGetter.getTaxCode();

					if(taxcode!=null && !taxcode.isEmpty()){

					}else{
						taxcode="";
					}

					Log.d("parameters","tt "+slNo +" pCode"+pCode +"pName"+pName
							+"cQty"+cQty+"lQty"+lQty+"qty"+qty+"foc"+foc+"pcsPerCarton"+pcsPerCarton
							+"retail_Price"+wholesaleprice+"price"+price+"total"+total+"itemDiscount"+itemdiscount
							+"billDisc"+disc+"totalDisc"+"0"+"subTtl"+subTtl+"txAmt"+txAmt
							+"netTtl"+ntTtl+"taxType"+taxtype+"taxValue"+taxtvalue+"SOSlno"+"I"+"exchangeQty"+exchangeQty
							+"cprice"+cprice+"ItemRemarks"+""+"taxcode"+taxcode);



					detail_json += slNo + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^" + foc + "^"
							+ pcsPerCarton + "^" + wholesaleprice + "^" + price
							+ "^" + total + "^" + itemdiscount + "^" + disc
							+ "^" + "0" + "^" + subTtl + "^" + txAmt
							+ "^" + ntTtl + "^" + taxtype + "^" + taxtvalue
							+ "^" + "I" + "^" + "0" + "^" + exchangeQty +"^" + cprice + "^" + "" + "^"+ taxcode + "!";


//					detail_json += slNo + "^" + pCode + "^" + pName + "^"
//							+ cQty + "^" + lQty + "^" + qty + "^" + foc + "^"
//							+ pcsPerCarton + "^" + retail_Price + "^" + price
//							+ "^" + total + "^" + itemDiscount + "^" + billDisc
//							+ "^" + totalDisc + "^" + subTtl + "^" + txAmt
//							+ "^" + netTtl + "^" + taxType + "^" + taxValue
//							+ "^" + "I" + "^" + SOSlno + "^" + exchangeQty +"^" + cprice + "^" + ItemRemarks + "^"+ taxcode + "!";

					Log.d("DetailInvoice", detail_json);

					slNo++;
				} while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);
		}

		String locationCode = SalesOrderSetGet.getLocationcode();
		String customerCode = Catalog.getCustomerCode();
		String soDate = SalesOrderSetGet.getSaleorderdate();
		String user = SupplierSetterGetter.getUsername();
		String currencyCode = SalesOrderSetGet.getCurrencycode();
		String currencyRate = SalesOrderSetGet.getCurrencyrate();

//		Log.d("currencyCode", "cc"+currencyCode);
//		Log.d("currencyRate", "cr"+currencyRate);

		/*header_json = "^" + soDate + "^" + soDate // ---> DeliveryDate
				+ "^" + locationCode + "^" + customerCode + "^" + totl // --->
				// Total
				+ "^" + totlitemDisc // ---> ItemDiscount
				+ "^" + "" // ---> BillDIscount
				+ "^" + "" // ---> TotalDiscount
				+ "^" + subtotl // ---> SubTotal
				+ "^" + totltax // ---> Tot Tax
				+ "^" + nettot + "^" + "" // ---> Remarks
				+ "^" + "0" // ---> Status
				+ "^" + currencyCode // ---> CurrencyCode
				+ "^" + currencyRate // ---> CurrencyRate
				+ "^" + deviceId + "^" + user;*/

		String taxtype = CustomerSetterGetter.getTaxType();

		if(taxtype!=null && !taxtype.isEmpty()){

		}else{
			taxtype="";
		}

		String taxPerc = CustomerSetterGetter.getTaxPerc();

		if(taxPerc!=null && !taxPerc.isEmpty()){

		}else{
			taxPerc="";
		}

		String taxcode = CustomerSetterGetter.getTaxCode();

		if(taxcode!=null && !taxcode.isEmpty()){

		}else{
			taxcode="";
		}

		Log.d("header_jsonparameters","-->"+soDate+"locationCode"+locationCode
		+"customerCode"+customerCode+"totl"+totl+"totlitemDisc"+totlitemDisc+"subtotl"+subtotl+"totltax"+totltax
		+"nettot"+nettot+"currencyCode"+currencyCode+"currencyRate"+deviceId+"device_name"+device_name
		+"taxcode"+taxcode+"taxtype"+taxtype+"taxPerc"+taxPerc);


		header_json = "" + "^" + soDate + "^" + locationCode + "^"
				+ customerCode + "^" + totl + "^" + totlitemDisc + "^" + "" + "^"
				+ "" + "^" + subtotl + "^" + totltax + "^" + nettot
				+ "^" + "" + "^" + currencyCode + "^" + currencyRate + "^"
				+ "" + "^" + "" + "^" + deviceId + "^" + "W" + "^" + device_name + "^" + "M" + "^" + taxcode + "^" +taxtype + "^"+ taxPerc + "^"+
				"I" + "^" + user+ "^" + disc;

		Log.d("ProductheaderInvoice", header_json);

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo productJson = new PropertyInfo();
		PropertyInfo barcodeJson = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		productJson.setName("sHeader");
		productJson.setValue(header_json);
		productJson.setType(String.class);
		request.addProperty(productJson);

		barcodeJson.setName("sDetail");
		barcodeJson.setValue(detail_json);
		barcodeJson.setType(String.class);
		request.addProperty(barcodeJson);
		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			String resTxt = response.toString();

			Log.d("Summary Product", resTxt);

			result = " { SOResult : " + resTxt + "}";

			JSONObject jsonResponse;

			jsonResponse = new JSONObject(result);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");

			/*********** Process each JSON Node ************/
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

				/******* Fetch node values **********/
				sum_result = jsonChildNode.optString("Result").toString();

				if (sum_result == null || sum_result.matches("")) {
					sum_result = "failed";
					Log.d("Summary Result", sum_result);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sum_result;
	}
		
	public static String summaryInvoiceReturnService(String webMethName,
			double billDisct, String return_subTotal, String return_tax, String return_netTotal, String subTotal, String totTax, String netTotal,
			String ttl,String invNo) throws JSONException, IOException,
			XmlPullParserException {

		allproducts.clear();
		
		SOTDatabase.init(context);
		sqldb = new SOTDatabase(context);
		cursor = SOTDatabase.getCursor();
		double totalDiscount = 0, subTot = 0, tx = 0, ntTtl = 0, rtn_subTot = 0, rtn_tx = 0, rtn_ntTtl = 0;
		int slNo = 1;
		detail_json = "";
		header_json = "";
		String batch_json = "";
		double sbtot = SOTDatabase.getsumsubTot();
		double billDiscount = billDisct / sbtot;
		double billDiscs;
		
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {

					String invslno = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
					String pCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String pName = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
					String cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
					String retn_cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_RETURN_CARTON));
					String retn_lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_RETURN_LOOSE));
					String retn_qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_RETURN_QTY));
					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
					String retail_Price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_RETAILPRICE));
					String price = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
					String total = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TOTAL));
					String itemDiscount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT));
					String sub_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL));
					String tax_amount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAX));
					String net_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
					String retn_sub_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_RETURN_SUBTOTAL));
					String retn_tax_amount = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_RETURN_TAX));
					String retn_net_ToTal = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_RETURN_NETTOTAL));
					String taxType = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
					String taxValue = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));
					String exchangeQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));
					String cartonPrice = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));
					String itemRemark = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_ITEM_REMARKS));
					
					if (exchangeQty != null && !exchangeQty.isEmpty()) {
						
					}else{
						exchangeQty="";
					}
					
					if (itemRemark != null && !itemRemark.isEmpty()) {
						
					}else{
						itemRemark="";
					}
					
					double sbTotal = SOTDatabase.getsubTotal(slNo);
					billDiscs = billDiscount * sbTotal;

					totalDiscount = billDiscs
							+ Double.parseDouble(itemDiscount);
					subTot = Double.parseDouble(sub_ToTal);
					ntTtl = Double.parseDouble(net_ToTal);
					tx = Double.parseDouble(tax_amount);
					
					rtn_subTot = Double.parseDouble(retn_sub_ToTal);
					rtn_ntTtl = Double.parseDouble(retn_net_ToTal);
					rtn_tx = Double.parseDouble(retn_tax_amount);
					
					
					String billDisc = twoDecimalPoint(billDiscs);
					String subTtl = twoDecimalPoint(subTot);
					String netTtl = twoDecimalPoint(ntTtl);
					String txAmt = fourDecimalPoint(tx);
					String rtn_subTtl = twoDecimalPoint(rtn_subTot);
					String rtn_netTtl = twoDecimalPoint(rtn_ntTtl);
					String rtn_txAmt = fourDecimalPoint(rtn_tx);
					String totalDisc = twoDecimalPoint(totalDiscount);

					detail_json += slNo + "^" + pCode + "^" + pName + "^"
							+ cQty + "^" + lQty + "^" + qty + "^" 
							+ retn_cQty + "^" + retn_lQty + "^" + retn_qty + "^" 
							+ pcsPerCarton + "^" + price + "^" + price + "^" 
							+ total + "^" + itemDiscount + "^" + billDisc + "^" + totalDisc + "^" 
							+ subTtl + "^" + txAmt + "^" + netTtl + "^" 
							+ rtn_subTtl + "^" + rtn_txAmt + "^" + rtn_netTtl + "^" 
							+ taxType + "^" + taxValue + "^"
							+ "I" + "^" + "" + "^"
							+ exchangeQty + "^" + cartonPrice + "^" + itemRemark + "!";
					Log.d("Detail", detail_json);

					// Batch start

//					Cursor cursor = SOTDatabase.getBatchCursorWithSR(pCode,""+invslno);

					Cursor cursor = SOTDatabase.getBatchCursor(pCode);

					if (cursor != null && cursor.getCount() > 0) {

						if (cursor.moveToFirst()) {
							do {

								String bpCode = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));


								String bNo = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_BATCH_NO));
								String exDate = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_EXPIRY_DATE));
								String mfgDate = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_MFG_DATE));
								String bcQty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
								String blQty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
								String bqty = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
								String bfoc = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_FOC));
								String bprice = cursor.getString(cursor
										.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));


								if(exDate!=null && !exDate.isEmpty()){

								}else{
									exDate= "01/01/1900";
								}

								if(mfgDate!=null && !mfgDate.isEmpty()){

								}else{
									mfgDate= "01/01/1900";
								}
								Log.d("save batch", "mfgDate : "+mfgDate);

								double cqy=0,lqy=0,qy=0,fqy=0;

								if(bcQty!=null && !bcQty.isEmpty()){
									cqy = Double.parseDouble(bcQty);
								}else{
									bcQty="";
								}

								if(blQty!=null && !blQty.isEmpty()){
									lqy = Double.parseDouble(blQty);
								}else{
									blQty="";
								}

								if(bqty!=null && !bqty.isEmpty()){
									qy = Double.parseDouble(bqty);
								}else{
									bqty="";
								}

								if(bfoc!=null && !bfoc.isEmpty()){
									fqy = Double.parseDouble(bfoc);
								}else{
									bfoc="";
								}

								if(cqy>0 || lqy>0 || qy>0 || fqy>0){
									batch_json += slNo + "^" + bpCode + "^" + bNo + "^" + exDate + "^"+ mfgDate + "^" + "" + "^"
											+ bcQty + "^" + blQty + "^" + bqty + "^" + bfoc + "^"
											+ bprice + "!";
								}

							} while (cursor.moveToNext());
						}
						batch_json = batch_json.substring(0, batch_json.length() - 1);
						Log.d("batch_json", batch_json);
					}
					//////////////////////////////////////////////////////
					// Batch End

					slNo++;
				} while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);
			
			Log.d("detail", detail_json);
		}
		 
		String locationCode = SalesOrderSetGet.getLocationcode();
		String customerCode = SalesOrderSetGet.getCustomercode();
		String remarks = SalesOrderSetGet.getRemarks();
		String user = SupplierSetterGetter.getUsername();
		String salesReturnDate = SalesOrderSetGet.getSaleorderdate();

		double blDsc = billDisct;
		double itemDisc = SOTDatabase.getitemDisc();
		double totDisc = blDsc + itemDisc;

		header_json = "^" + salesReturnDate + "^" + invNo + "^" + locationCode + "^"
				+ customerCode + "^" + ttl + "^" + itemDisc + "^" + blDsc + "^"
				+ totDisc + "^" + subTotal + "^" + totTax + "^" + netTotal + "^" 
				+ return_subTotal + "^" + return_tax + "^" + return_netTotal + "^" + remarks + "^"
				+ deviceId + "^" + "I" + "^" + user;

		Log.d("header", header_json);

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo productJson = new PropertyInfo();
		PropertyInfo barcodeJson = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo sBatchDetailJson = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		productJson.setName("sHeader");
		productJson.setValue(header_json);
		productJson.setType(String.class);
		request.addProperty(productJson);

		barcodeJson.setName("sDetail");
		barcodeJson.setValue(detail_json);
		barcodeJson.setType(String.class);
		request.addProperty(barcodeJson);

		sBatchDetailJson.setName("sBatchDetail");
		sBatchDetailJson.setValue(batch_json);
		sBatchDetailJson.setType(String.class);
		request.addProperty(sBatchDetailJson);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

		androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		String resTxt = response.toString();

		result = " { SOResult : " + resTxt + "}";
		
		Log.d("Summary Product", result);

		JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(result);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				sum_result = jsonChildNode.optString("Result").toString();

				if (sum_result.matches("")) {
					sum_result = "failed";
					Log.d("Summary Result", sum_result);
				}
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return sum_result;
	}
	
	public static String summaryExpenseService(String webMethName,String soNo) throws JSONException, IOException,
	   XmlPullParserException {

	  SOTDatabase.init(context);
	  cursor = SOTDatabase.getExpenseCursor();
	  int slNo = 1;
	  detail_json = "";
	  header_json = "";
	  String taxtype = "";
	  
	  if (cursor != null && cursor.getCount() > 0) {
	   if (cursor.moveToFirst()) {
	    do {
	     String expno = cursor.getString(cursor
	       .getColumnIndex(SOTDatabase.COLUMN_ACCOUNTNO));
	     String expdesc = cursor.getString(cursor
	       .getColumnIndex(SOTDatabase.COLUMN_DESC));
	     String amt = cursor.getString(cursor
	       .getColumnIndex(SOTDatabase.COLUMN_AMOUNT));
	     String subTot = cursor.getString(cursor
		 	.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL));
	     String tax = cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_TAX));
	     String netTot =  cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
	     String taxType = cursor.getString(cursor
				 .getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
	     String taxPerc = cursor.getString(cursor
				 .getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));

	     if(taxType.matches("Tax Inclusive")){
	     	taxtype = "1";
		 }else if (taxType.matches("Standard Rate")){
	     	taxtype = "2";
		 }else if (taxType.matches("Zero Rate")){
			taxtype = "3";
		 }

	     detail_json += slNo + "^" + expno + "^" + expdesc + "^" + "" + "^" + "1" + "^" + amt
		 		        + "^" + subTot + "^" + tax + "^" + netTot + "^" + taxtype + "^" + taxPerc
		 				+ "^" + amt + "^" + subTot + "^" + tax + "^" + netTot + "!";
	     slNo++;
	    } while (cursor.moveToNext());
	   }
	   detail_json = detail_json.substring(0, detail_json.length() - 1);
	   Log.d("DetailExpense", detail_json);
	  }

	  String totalamt = SOTDatabase.getExpenseTotalAmt();
	  String remarks = SalesOrderSetGet.getRemarks();
	  String expDate = SalesOrderSetGet.getSaleorderdate();
	  String user = SupplierSetterGetter.getUsername();
	  String loccode = SalesOrderSetGet.getLocationcode();
	  String subCode = SalesOrderSetGet.getSuppliercode();
	  String subName = SalesOrderSetGet.getSuppliergroupcode();
	  String subTot = SOTDatabase.getExpenseTotalSubTot();
	  String tax = SOTDatabase .getExpenseTotalTax();
	  String netTot = SOTDatabase.getExpenseTotalNetTot();
	  String payTo = SalesOrderSetGet.getPayTo();

	  header_json =   soNo + "^" + expDate + "^" + "E" + "^" + loccode + "^" + "" + "^" + subCode
	  				   + "^" + subName + "^" + payTo + "^" + remarks + "^" + subTot + "^"
			           + tax + "^" + netTot + "^" + subTot + "^" + tax + "^" + netTot + "^" + "SGD"
			           + "^" + "1" + "^" + deviceId + "^" + user;

	  Log.d("HeaderExpense", header_json);

	  SoapObject request = new SoapObject(NAMESPACE, webMethName);

	  PropertyInfo productJson = new PropertyInfo();
	  PropertyInfo barcodeJson = new PropertyInfo();

	  PropertyInfo companyCode = new PropertyInfo();

	  String cmpnyCode = SupplierSetterGetter.getCompanyCode();

	  companyCode.setName("CompanyCode");
	  companyCode.setValue(cmpnyCode);
	  companyCode.setType(String.class);
	  request.addProperty(companyCode);

	  productJson.setName("sHeader");
	  productJson.setValue(header_json);
	  productJson.setType(String.class);
	  request.addProperty(productJson);

	  barcodeJson.setName("sDetail");
	  barcodeJson.setValue(detail_json);
	  barcodeJson.setType(String.class);
	  request.addProperty(barcodeJson);

	  SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
	    SoapEnvelope.VER11);
	  envelope.dotNet = true;
	  envelope.setOutputSoapObject(request);
	  envelope.bodyOut = request;

	  HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

	  androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
	  SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
	  String resTxt = response.toString();

	  Log.d("Summary Result", resTxt);

	  result = " { Result : " + resTxt + "}";

	  JSONObject jsonResponse;
	  try {
	   jsonResponse = new JSONObject(result);

	   JSONArray jsonMainNode = jsonResponse.optJSONArray("Result");

	   int lengthJsonArr = jsonMainNode.length();
	   for (int i = 0; i < lengthJsonArr; i++) {

	    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
	    sum_result = jsonChildNode.optString("Result").toString();

	    if (sum_result.matches("")) {
	     sum_result = "failed";
	     Log.d("Summary Result", sum_result);
	    }

	   }

	  } catch (JSONException e) {

	   e.printStackTrace();
	  }

	  return sum_result;
	 }
	

	public static String summaryDeliveryVerificationService(String webMethName, double billDisct, String soNo) throws JSONException, IOException, XmlPullParserException {

		  SOTDatabase.init(context);
		  cursor = SOTDatabase.getCursor();
		  double totalDiscount = 0, subTot = 0, tx = 0, ntTtl = 0;
		  int slNo = 1;
		  detail_json = "";
		  header_json = "";
		  double subTotal = SOTDatabase.getsumsubTot(); // bill disc sub total
		  double billDiscount = billDisct / subTotal;
		  double billDiscs;
		  
		  if (cursor != null && cursor.getCount() > 0) {

		   if (cursor.moveToFirst()) {
		    do {
		     String pCode = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
		     String pName = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
		     String cQty = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
		     String lQty = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
		     String qty = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
		     String foc = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_FOC));
		     String price = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
		     String pcsPerCarton = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
		     String total = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_TOTAL));
		     String itemDiscount = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT));
		     String taxType = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
		     String taxValue = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));
		     String retail_Price = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_RETAILPRICE));

		     String sub_ToTal = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL));
		     String net_ToTal = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
		     String tax_amount = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_TAX));
		     
		     String cprice = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));
		     
		     String exchangeQty = cursor.getString(cursor
		       .getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));
		     
		     String OriginalCQty = cursor.getString(cursor.getColumnIndex(SOTDatabase.COLUMN_DV_ORIGINAL_CQTY));
		     String OriginalQty = cursor.getString(cursor.getColumnIndex(SOTDatabase.COLUMN_DV_ORIGINAL_QTY));

		     double sbTotal = SOTDatabase.getsubTotal(slNo);
		     billDiscs = billDiscount * sbTotal;

		     totalDiscount = billDiscs + Double.parseDouble(itemDiscount);
		     subTot = Double.parseDouble(sub_ToTal);
		     ntTtl = Double.parseDouble(net_ToTal);

		     tx = Double.parseDouble(tax_amount);
		     String billDisc = twoDecimalPoint(billDiscs);
		     String subTtl = twoDecimalPoint(subTot);
		     String netTtl = twoDecimalPoint(ntTtl);
		     String txAmt = fourDecimalPoint(tx);
		     String totalDisc = twoDecimalPoint(totalDiscount);
		     double dCarton = 0, dWeight =0;
		     
		     if(itemDiscount != null && !itemDiscount.isEmpty()){
		      itemDiscount = "0"; 
		     }
		     
		     if(billDisc != null && !billDisc.isEmpty()){
		      billDisc = "0"; 
		     }
		     
		     if(totalDisc != null && !totalDisc.isEmpty()){
		         totalDisc = "0"; 
		     }
		     
		     if(cQty != null && !cQty.isEmpty()){
		      dCarton = Double.parseDouble(cQty); 
		     }
		     
		     if(qty != null && !qty.isEmpty()){
		      dWeight = Double.parseDouble(qty);  
		     }
		     
//		     if(dCarton>0 || dWeight>0){

		     detail_json += slNo + "^" + pCode + "^" + pName + "^"
		       + cQty + "^" + lQty + "^" + qty + "^" + foc + "^"
		       + pcsPerCarton + "^" + retail_Price + "^" + price
		       + "^" + total + "^" + itemDiscount + "^" + billDisc
		       + "^" + totalDisc + "^" + subTtl + "^" + txAmt
		       + "^" + netTtl + "^" + taxType + "^" + taxValue
		       + "^" + "I" + "^" + exchangeQty +"^" + cprice
		       + "^" + OriginalCQty +"^" + OriginalQty +"!";
		     slNo++;
//		     }
		    } while (cursor.moveToNext());
		   }
		   detail_json = detail_json.substring(0, detail_json.length() - 1);
		   Log.d("Detail", detail_json);
		  }
		  

		  String locationCode = SalesOrderSetGet.getLocationcode();
		  String deliveryDate = SalesOrderSetGet.getDeliverydate();
		  String customerCode = SalesOrderSetGet.getCustomercode();
		  String currencyCode = SalesOrderSetGet.getCurrencycode();
		  String remarks = SalesOrderSetGet.getRemarks();
		  String currencyRate = SalesOrderSetGet.getCurrencyrate();
		  String soDate = SalesOrderSetGet.getSaleorderdate();
		  String user = SupplierSetterGetter.getUsername();
		  
		  double ttl_subTotal = SOTDatabase.getSubTotal();
		  double ttl_netTotal = SOTDatabase.getNetTotal();
		  double ttl_Tax = SOTDatabase.getTax();
		  double ttl_total = SOTDatabase.getTotal();

		  double blDsc = billDisct;
		  double itemDisc = SOTDatabase.getitemDisc();
		  double totDisc = blDsc + itemDisc;

		  header_json =  soNo + "^" + soDate + "^" + deliveryDate + "^" + locationCode
		    + "^" + customerCode + "^" + ttl_total + "^" + itemDisc + "^" + blDsc
		    + "^" + totDisc + "^" + ttl_subTotal + "^" + ttl_Tax + "^"
		    + ttl_netTotal + "^" + remarks + "^" + "0" + "^" + currencyCode
		    + "^" + currencyRate + "^" + deviceId + "^" + user;

		  Log.d("Product header", header_json);

		  SoapObject request = new SoapObject(NAMESPACE, webMethName);

		  PropertyInfo productJson = new PropertyInfo();
		  PropertyInfo barcodeJson = new PropertyInfo();

		  PropertyInfo companyCode = new PropertyInfo();

		  String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		  companyCode.setName("CompanyCode");
		  companyCode.setValue(cmpnyCode);
		  companyCode.setType(String.class);
		  request.addProperty(companyCode);

		  productJson.setName("sHeader");
		  productJson.setValue(header_json);
		  productJson.setType(String.class);
		  request.addProperty(productJson);

		  barcodeJson.setName("sDetail");
		  barcodeJson.setValue(detail_json);
		  barcodeJson.setType(String.class);
		  request.addProperty(barcodeJson);

		  SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
		    SoapEnvelope.VER11);
		  envelope.dotNet = true;
		  envelope.setOutputSoapObject(request);
		  envelope.bodyOut = request;

		  HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

		  androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		  SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		  String resTxt = response.toString();

		  Log.d("Summary Product", resTxt);

		  result = " { SOResult : " + resTxt + "}";

		  JSONObject jsonResponse;
		  try {
		   jsonResponse = new JSONObject(result);

		   JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");

		   int lengthJsonArr = jsonMainNode.length();
		   for (int i = 0; i < lengthJsonArr; i++) {

		    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
		    sum_result = jsonChildNode.optString("Result").toString();

		    if (sum_result.matches("")) {
		     sum_result = "failed";
		     Log.d("Summary Result", sum_result);
		    }

		   }

		  } catch (JSONException e) {

			  e.printStackTrace();
		  }

		  return sum_result;
		 }
	
	public static String summaryPackingService(String webMethName, String details, String soNo) throws JSONException, IOException, XmlPullParserException {

		  SoapObject request = new SoapObject(NAMESPACE, webMethName);

		  PropertyInfo productJson = new PropertyInfo();
		  PropertyInfo barcodeJson = new PropertyInfo();
		  PropertyInfo companyCode = new PropertyInfo();

		  String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		  companyCode.setName("CompanyCode");
		  companyCode.setValue(cmpnyCode);
		  companyCode.setType(String.class);
		  request.addProperty(companyCode);

		  productJson.setName("InvoiceNo");
		  productJson.setValue(soNo);
		  productJson.setType(String.class);
		  request.addProperty(productJson);

		  barcodeJson.setName("sDetail");
		  barcodeJson.setValue(details);
		  barcodeJson.setType(String.class);
		  request.addProperty(barcodeJson);

		  SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
		    SoapEnvelope.VER11);
		  envelope.dotNet = true;
		  envelope.setOutputSoapObject(request);
		  envelope.bodyOut = request;

		  HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

		  androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		  SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		  String resTxt = response.toString();

		  Log.d("Summary Packing", resTxt);

		  result = " { SOResult : " + resTxt + "}";

		  JSONObject jsonResponse;
		  try {
		   jsonResponse = new JSONObject(result);

		   JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");

		   int lengthJsonArr = jsonMainNode.length();
		   for (int i = 0; i < lengthJsonArr; i++) {

		    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
		    sum_result = jsonChildNode.optString("Result").toString();

		    if (sum_result.matches("")) {
		     sum_result = "failed";
		     Log.d("Summary Result", sum_result);
		    }

		   }

		  } catch (JSONException e) {

			  e.printStackTrace();
		  }

		  return sum_result;
		 }
	
	
	
	
	public static String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}

	public static String fourDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setMinimumFractionDigits(4);
		String tot = df.format(d);

		return tot;
	}

	public static ArrayList<String> summaryConsignmentService(String webMethName, double billDisct,
												   String subTotal, String totTax, String netTotal, String ttl,
												   String srlNo, String sono, String dono, String o) throws JSONException, IOException,
			XmlPullParserException {

			SOTDatabase.init(context);
			cursor = SOTDatabase.getCursor();

			sqldb = new SOTDatabase(context);
			productid.clear();
			allproducts.clear();
		SOConsignmentArr.clear();
			double totalDiscount = 0, subTot = 0, tx = 0, ntTtl = 0;
			int slNo = 1,sno=0;
			double sbtot = SOTDatabase.getsumsubTot();
			double billDiscount = billDisct / sbtot;
			double billDiscs;
			detail_json = "";
			header_json = "";
			barcode_json = "";
		    batch_json = "";
		    stock_json="";
			String ReceiptNo="";
			String con_no="",stockRefNo = "",s_No="",cQty="",lQty="",qty="",pcsPerCarton="",qty_no="",c_Qty="",l_Qty="",quantity = "";
			String appType = LogOutSetGet.getApplicationType();


			if (cursor != null && cursor.getCount() > 0) {

				if (cursor.moveToFirst()) {
					do {
						String pSlno = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
						String pCode = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
						String pName = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
						pcsPerCarton = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));

						String  totQty =cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));;
						Log.d("pcsperCarton","-->"+pcsPerCarton+"-->"+totQty);
						Log.d("SalesorderSLNO","-->"+pSlno);
//						String totQty = SOTDatabase.getConsignmentQty(pCode);
//						Log.d("totQty","-->"+totQty);
//						SalesOrderSetGet.setResult_value(totQty);

						double pcs=Double.parseDouble(pcsPerCarton);
					/*	if(SalesOrderSetGet.getTranType().matches("COR")||SalesOrderSetGet.getTranType().matches("COI")){

							String Total_Qty = SOTDatabase.getConsignmentQty(pCode);
							Log.d("getResult_value","-->"+totQty);
							if(Total_Qty!=null && !Total_Qty.matches("")){
							double Qty = Double.parseDouble(Total_Qty);
							double result = Qty / pcs;
							double result1 = Qty % pcs;
							int res_value =(int)result;
							int res_value1 =(int)result1;
							cQty = String.valueOf(res_value);
							lQty = String.valueOf(res_value1);
							qty = String.valueOf(Qty);
						}
						}else{*/
							 cQty = cursor.getString(cursor
									.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
							 lQty = cursor.getString(cursor
									.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
							 qty = cursor.getString(cursor
									.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
//						}

						String foc = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_FOC));
						String price = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));

						String total = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_TOTAL));
						String itemDiscount = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT));
//					String taxType = cursor.getString(cursor
//							.getColumnIndex(SOTDatabase.COLUMN_TAXTYPE));
						String taxValue = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));
						String taxType = SalesOrderSetGet.getCompanytax();
//					String taxValue = SalesOrderSetGet.getTaxValue();
						String retail_Price = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_RETAILPRICE));
						String sub_ToTal = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL));
						String net_ToTal = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_NETTOTAL));
						String tax_amount = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_TAX));

						String cprice = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE));

						String exchangeQty = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));

						String ItemRemarks = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_ITEM_REMARKS));

						String SOSlno = cursor.getString(cursor
								.getColumnIndex(SOTDatabase.COLUMN_SO_SLNO));
						String avg_cost =SalesOrderSetGet.getAvg_cost();


						Log.d("AverageCostDetails",avg_cost+" "+itemDiscount);

						double sbTotal = SOTDatabase.getsubTotal(slNo);
						billDiscs = billDiscount * sbTotal;


						if(itemDiscount.matches("0")){
							totalDiscount = billDiscs;
						}else{
							totalDiscount = billDiscs
									+ Double.parseDouble(itemDiscount);
						}
						Log.d("totalDiscount","-->"+totalDiscount);
						subTot = Double.parseDouble(sub_ToTal);
						ntTtl = Double.parseDouble(net_ToTal);

						tx = Double.parseDouble(tax_amount);

						String subTtl = twoDecimalPoint(subTot);
						String netTtl = twoDecimalPoint(ntTtl);
						String txAmt = fourDecimalPoint(tx);
//						String billDisc = twoDecimalPoint(billDiscs);
						String billDisc ="0.00";
						String totalDisc = twoDecimalPoint(totalDiscount);

						if(totalDisc.matches("NaN")){
							totalDisc="0.00";
						}else {
							totalDisc = twoDecimalPoint(totalDiscount);
						}

						if(cprice.matches("")){
							cprice="0";
						}
						if(exchangeQty.matches("")){
							exchangeQty="0";
						}
						if(ItemRemarks.matches("")){
							ItemRemarks =" ";
						}

						String taxPerc =SalesOrderSetGet.getTaxPerc();

						if(taxPerc!=null&& !taxPerc.isEmpty()){
							taxPerc =SalesOrderSetGet.getTaxPerc();
						}else{
							taxPerc ="0";
						}

						String taxcode = SalesOrderSetGet.getTaxCode();

						if(taxcode!=null && !taxcode.isEmpty()){
						taxcode = SalesOrderSetGet.getTaxCode();
						}else{
						taxcode=Company.getTaxcode();
						}
						if(SalesOrderSetGet.getTranType().matches("COR")||SalesOrderSetGet.getTranType().matches("COI")) {
						int slno = SalesOrderSetGet.getSl_no();
						Log.d("slno","-->"+slno);
						Cursor cursors = SOTDatabase.getConsignmentStocks(pCode);
						Log.d("cursorsCount","-->"+cursors.getCount());
										if (cursors != null && cursors.getCount() > 0) {

											if (cursors.moveToFirst()) {
												do {
													stockRefNo = cursors.getString(cursors.
															getColumnIndex(SOTDatabase.COLUMN_CONSIGNMENT_NO));

													Log.d("stockRefNo",""+stockRefNo+"Pcode" +pCode);

													Cursor cursor = SOTDatabase.getConsignmentStockList(pCode,stockRefNo);

													if(cursor!=null && cursor.getCount()>0) {

														if (cursor.moveToFirst()) {
															do {
																String slerial_no = cursor.getString(cursor
																		.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
																String csPCode = cursor.getString(cursor
																		.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
																String bNo = cursor.getString(cursor
																		.getColumnIndex(SOTDatabase.COLUMN_BATCH_NO));
																String exDate = cursor.getString(cursor
																		.getColumnIndex(SOTDatabase.COLUMN_EXPIRY_DATE));
																String mfgDate = cursor.getString(cursor
																		.getColumnIndex(SOTDatabase.COLUMN_MFG_DATE));
																String cscQty = cursor.getString(cursor
																		.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
																String cslQty = cursor.getString(cursor
																		.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
																String csqty = cursor.getString(cursor
																		.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
//										String csfoc = cursors.getString(cursors
//												.getColumnIndex(SOTDatabase.COLUMN_FOC));
//														String csprice = cursors.getString(cursors
//																.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
																String pcsper = cursor.getString(cursor
																		.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));

																stockRefNo = cursor.getString(cursor.
																		getColumnIndex(SOTDatabase.COLUMN_CONSIGNMENT_NO));

																Log.d("csPCodeCheck", "-->" + csPCode + "cscQty :" + cscQty + "cslQty :" + cslQty + "csqty:" + csqty+"stockRefNo :"+stockRefNo);

																if (exDate != null && !exDate.isEmpty()) {

																} else {
																	exDate = "01/01/1900";
																}

																if (mfgDate != null && !mfgDate.isEmpty()) {

																} else {
																	mfgDate = "01/01/1900";
																}
																Log.d("save batch", "mfgDate : " + mfgDate);

																double cqy = 0, lqy = 0, qy = 0, fqy = 0;

																if (cscQty != null && !cscQty.isEmpty()) {
																	cqy = Double.parseDouble(cscQty);
																} else {
																	cscQty = "";
																}

																if (cslQty != null && !cslQty.isEmpty()) {
																	lqy = Double.parseDouble(cslQty);
																} else {
																	cslQty = "";
																}

																if (csqty != null && !csqty.isEmpty()) {
																	qy = Double.parseDouble(csqty);
																} else {
																	csqty = "";
																}

																if (csqty != null && !csqty.isEmpty()) {
																	fqy = Double.parseDouble(csqty);
																} else {
																	csqty = "";
																}

																if(csqty.matches("0")){

																}else {
																	stock_json += slerial_no + "^" + csPCode + "^" + cscQty + "^" + cslQty + "^" +
																			csqty + "^" + "0.00" + "^" + exchangeQty + "^" + cprice + "^" + price + "^" + "0"
																			+ "^" + exDate + "^" + pcsper + "^" + stockRefNo + "!";
																}
															}while (cursor.moveToNext());
														}
													}

									} while (cursors.moveToNext());
								}
							}
						}else{
							stock_json += pSlno + "^" + pCode + "^" + cQty + "^" + lQty + "^" +
									qty + "^" + foc + "^" + exchangeQty + "^" + cprice + "^" + price + "^" + "0"
									+ "^" + "10/10/1990" + "^" + pcsPerCarton + "^" + stockRefNo + "!";
						}



						Log.d("QuantityValue", "-->" + cQty + " " + lQty + " " + qty);

						detail_json += pSlno + "^" + pCode + "^" + pName + "^"
								+ cQty + "^" + lQty + "^" + qty + "^" + foc + "^"
								+ pcsPerCarton + "^" + retail_Price + "^" + price + "^"
								+ total + "^" + itemDiscount + "^" + billDisc + "^"
								+ totalDisc + "^" + subTtl + "^" + txAmt
								+ "^" + netTtl + "^" + taxType + "^" + taxValue
								+ "^" + "I" + "^" + SOSlno + "^" + avg_cost + "^" + cprice + "^" + ItemRemarks + "^" + taxcode + "^" + exchangeQty + "!";


						Cursor cursor = SOTDatabase.getBatchCursorWithSR(pCode,pSlno);

						if (cursor != null && cursor.getCount() > 0) {

							if (cursor.moveToFirst()) {
								do {

									String bpCode = cursor.getString(cursor
											.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));


									String bNo = cursor.getString(cursor
											.getColumnIndex(SOTDatabase.COLUMN_BATCH_NO));
									String exDate = cursor.getString(cursor
											.getColumnIndex(SOTDatabase.COLUMN_EXPIRY_DATE));
									String mfgDate = cursor.getString(cursor
											.getColumnIndex(SOTDatabase.COLUMN_MFG_DATE));
									String bcQty = cursor.getString(cursor
											.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
									String blQty = cursor.getString(cursor
											.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
									String bqty = cursor.getString(cursor
											.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
									String bfoc = cursor.getString(cursor
											.getColumnIndex(SOTDatabase.COLUMN_FOC));
									String bprice = cursor.getString(cursor
											.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
									String exchange =cursor.getString(cursor
											.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY));


									if(exDate!=null && !exDate.isEmpty()){

									}else{
										exDate= "";
									}


									double cqy=0,lqy=0,qy=0,fqy=0;

									if(bcQty!=null && !bcQty.isEmpty()){
										cqy = Double.parseDouble(bcQty);
									}else{
										bcQty="";
									}

									if(blQty!=null && !blQty.isEmpty()){
										lqy = Double.parseDouble(blQty);
									}else{
										blQty="";
									}

									if(bqty!=null && !bqty.isEmpty()){
										qy = Double.parseDouble(bqty);
									}else{
										bqty="";
									}

									if(bfoc!=null && !bfoc.isEmpty()){
										fqy = Double.parseDouble(bfoc);
									}else{
										bfoc="";
									}
									if(mfgDate!=null && !mfgDate.isEmpty()){

									}else{
										mfgDate= "01/01/1900";
									}
									Log.d("save batch", "mfgDate : "+mfgDate);

									batch_json += slNo + "^" + bpCode + "^" + bNo + "^" + exDate + "^"+ mfgDate + "^"
											+ "" + "^" + bcQty + "^" + blQty + "^" + bqty + "^"
												+ bfoc + "^" + bprice + "^" + cprice + "^"
												+ exchange + "!";


								} while (cursor.moveToNext());
							}
						}
						else{
//							batch_json+= "0" + "^" + "0" + "^" + "0" + "^" + "0" + "^"+ "0" + "^" + "0" + "^"
//									+ "0" + "^" + "0" + "^" + "0" + "^" + "0" + "^"
//									+ "0" + "!";
						}

						slNo++;
					} while (cursor.moveToNext());
				}
				detail_json = detail_json.substring(0, detail_json.length() - 1);
				Log.d("Detail", detail_json);

				if(stock_json.matches("")) {

				}else {
					stock_json = stock_json.substring(0, stock_json.length() - 1);

				}
				Log.d("stock_json", ""+stock_json);
				if(batch_json.matches("")){
				}else{
					batch_json = batch_json.substring(0, batch_json.length() - 1);
				}

				Log.d("batch_json", ""+batch_json);
			}


			// Add barcode for product
//			if (appType.matches("W")) {
//				//	  productid = sqldb.distinctprodId();
//				productid = sqldb.getBarcodeStatus();
//
//				if(!productid.isEmpty()){
//
//					for (int i = 0; i < productid.size(); i++) {
//						String prodtid = productid.get(i);
//						allproducts = sqldb.getAllprodValues(prodtid);
//						String productSno = SOTDatabase.getProductSno(prodtid);
//						for (ProductBarcode prodbarcode : allproducts) {
//
//							//   int prodsno = i+1;//prodbarcode.getSno();
//							//  String prodsno = prodbarcode.getSno();
//							String prodcode = prodbarcode.getProductcode();
//							String prodseqno = prodbarcode.getSeqno();
//							String prodweightbarcode = prodbarcode.getBarcode();
//							double prodweight = prodbarcode.getWeight();
//
//							barcode_json += productSno + "^" + prodcode + "^" + prodseqno + "^"
//									+ prodweightbarcode + "^" + prodweight + "^" + "!";
//						}
//
//					}
//
//					barcode_json = barcode_json.substring(0, barcode_json.length() - 1);
//					Log.d("barcode_json", barcode_json);
//
//				}
//			}

			String locationCode = SalesOrderSetGet.getLocationcode();
			String customerCode = SalesOrderSetGet.getCustomercode();
			String customerName =SalesOrderSetGet.getCustomername();
			String currencyCode = SalesOrderSetGet.getCurrencycode();
			String remarks = SalesOrderSetGet.getRemarks();
			String currencyRate = SalesOrderSetGet.getCurrencyrate();
			String soDate = SalesOrderSetGet.getSaleorderdate();
			String user = SupplierSetterGetter.getUsername();
			String durationIndays = SalesOrderSetGet.getDuration();
			String orderNo =SalesOrderSetGet.getOrderNo();
			String tran_type =SalesOrderSetGet.getTranType();
			String orderDate = SalesOrderSetGet.getOrderDate();
			if (durationIndays.matches("")){
				durationIndays ="0";
			}
			if(orderNo.matches("")){
				orderNo="0";
			}
			if(orderDate.matches("")){
				orderDate ="";
			}
		String taxcode = SalesOrderSetGet.getTaxCode();

		if(taxcode!=null && !taxcode.isEmpty()){
			taxcode = SalesOrderSetGet.getTaxCode();
		}else{
			taxcode=Company.getTaxcode();
		}
		String taxperc = SalesOrderSetGet.getTaxPerc();

		if(taxperc!=null && !taxperc.isEmpty()){
		 taxperc = SalesOrderSetGet.getTaxPerc();
		}else{
			taxperc="0";
		}

		Log.d("taxcode","tt "+taxcode +"  "+taxperc);

		String taxType =SalesOrderSetGet.getTaxType();
		if(taxType!=null && !taxType.isEmpty()){
			taxType =SalesOrderSetGet.getTaxType();
		}else{
			taxType="I";
		}


			double blDsc = billDisct;
			double itemDisc = SOTDatabase.getitemDisc();
			double totDisc = blDsc + itemDisc;
			Log.d("Trantypeheader_json","-->"+tran_type+"DurationIndays :"+durationIndays);
			header_json = dono + "^" + soDate + "^" + durationIndays + "^" + orderNo
					+ "^" + orderDate + "^" + tran_type + "^" + locationCode + "^" + customerCode
					+ "^" + customerName + "^" + totTax + "^" + itemDisc + "^"
					+ blDsc + "^" + totDisc + "^" + subTotal + "^" + totTax + "^"
					+ netTotal + "^" + remarks + "^" + currencyCode + "^" + currencyRate + "^"
					+ sono + "^" + dono + "^" + deviceId + "^" + taxcode + "^"
					+ taxType + "^" + taxperc + "^" +  "I" + "^" + user ;


			Log.d("Productheader", header_json);

			SoapObject request = new SoapObject(NAMESPACE, webMethName);

			PropertyInfo headerJson = new PropertyInfo();
			PropertyInfo detailJson = new PropertyInfo();
			PropertyInfo barcodeJson = new PropertyInfo();
			PropertyInfo companyCode = new PropertyInfo();
			PropertyInfo stockJson = new PropertyInfo();
			PropertyInfo sCollectCash = new PropertyInfo();

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		String collectCash = SalesOrderSetGet.getsCollectCash();

		Log.d("collectCash", "collectCash"+collectCash);

			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			headerJson.setName("sHeader");
			headerJson.setValue(header_json);
			headerJson.setType(String.class);
			request.addProperty(headerJson);

			detailJson.setName("sDetail");
			detailJson.setValue(detail_json);
			detailJson.setType(String.class);
			request.addProperty(detailJson);

			barcodeJson.setName("sBatchDetail");
			barcodeJson.setValue(batch_json);
			barcodeJson.setType(String.class);
			request.addProperty(barcodeJson);

			stockJson.setName("sStockDetail");
			stockJson.setValue(stock_json);
			stockJson.setType(String.class);
			request.addProperty(stockJson);

			sCollectCash.setName("sCollectCash");
			sCollectCash.setValue(collectCash);
			sCollectCash.setType(String.class);
			request.addProperty(sCollectCash);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			String resTxt = response.toString();

			Log.d("Summary Product", resTxt);

			result = " { SOResult : " + resTxt + "}";

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(result);

				JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");

				/********** Process each JSON Node ***********/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					sum_result = jsonChildNode.optString("Result").toString();
					ReceiptNo = jsonChildNode.optString("ReceiptNo").toString();



					if (sum_result.matches("")) {
						sum_result = "failed";
						Log.d("Summary Result", sum_result);
					}

					SOConsignmentArr.add(sum_result);
					SOConsignmentArr.add(ReceiptNo);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

			return SOConsignmentArr;
		}

	public static String summaryQuickTransfer(String webMethName, List<LocationGetSet> stockList, List<LocationGetSet> lctnList,String mainLocation) throws IOException, XmlPullParserException {
		SOTDatabase.init(context);
		detail_json = "";
		String code,element="",total,lctn = "",slno,pro_name,pcs;

		String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
		Log.d("Datecheck","--?>"+date);
		String user = SupplierSetterGetter.getUsername();
		String cmpnycCode = SupplierSetterGetter.getCompanyCode();

		Log.d("stockListCheck","-->"+stockList.size());

		for(int i=0;i<stockList.size();i++) {
			JSONArray array = stockList.get(i).getLocation_code();
			Log.d("productStockListSize", "-->" + array.length());
			slno = stockList.get(i).getSlno();
			code = stockList.get(i).getPro_code();
			total = stockList.get(i).getTotal();
			pro_name = stockList.get(i).getPro_name();
			Log.d("productNamecheck","-->"+pro_name);
			pcs = stockList.get(i).getPcsperCarton();
			for (int j = 1; j < array.length(); j++) {
				try {
					element = array.get(j).toString();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.d("elemntChecking", "-->" + element);
				for (int k = 0; k <= j; k++) {
					lctn = lctnList.get(k).getLocatn();
				}
				detail_json += cmpnycCode + "^" + "" + "^" + date + "^" + slno + "^" + code + "^" + pro_name + "^" + pcs + "^" + mainLocation + "^" + lctn + "^" + element + "^" + "0" + "^" + user + "!";
				Log.d("DetailQuickCheck", detail_json);
			}
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);
			Log.d("DetailQuickTransfer", detail_json);


		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo headerJson = new PropertyInfo();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		headerJson.setName("sDetail");
		headerJson.setValue(detail_json);
		headerJson.setType(String.class);
		request.addProperty(headerJson);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

		androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
		String resTxt = response.toString();

		Log.d("SummaryProduct", resTxt);

		result = " { SOResult : " + resTxt + "}";

		JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(result);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");

			/********** Process each JSON Node ***********/
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				sum_result = jsonChildNode.optString("Result").toString();

				if (sum_result.matches("")) {
					sum_result = "failed";
					Log.d("SummaryResult", sum_result);
				}
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return sum_result;
	}

	public static String saveSettlement(String date_edt, String totl_amt, String webMethNames) {
		SOTDatabase.init(context);
		detail_json = "";
		header_json = "";
		cursor = SOTDatabase.getDenominationCursor();
		String user = SupplierSetterGetter.getUsername();
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					String serialNo = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
					String productCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					Log.d("takeData", productCode);
					String productName = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));

					detail_json +=  serialNo + "^" + productCode + "^" + productName + "^" + qty + "!";

				}while (cursor.moveToNext());
			}
			detail_json = detail_json.substring(0, detail_json.length() - 1);

			Log.d("DetailConsignAddress", detail_json);
		}

		String locationCode = SalesOrderSetGet.getLocationcode();
		deviceId = RowItem.getDeviceID();

		header_json =   cmpnyCode + "^" + locationCode + "^" + "" + "^" + date_edt
				+ "^" + totl_amt + "^" + user + "^" + "1" + "^" + user + "^"
				+ deviceId;

		Log.d("ProductheaderSO", header_json);

		SoapObject request = new SoapObject(NAMESPACE, webMethNames);

		PropertyInfo productJson = new PropertyInfo();
		PropertyInfo barcodeJson = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		productJson.setName("sHeader");
		productJson.setValue(header_json);
		productJson.setType(String.class);
		request.addProperty(productJson);

		barcodeJson.setName("sDetail");
		barcodeJson.setValue(detail_json);
		barcodeJson.setType(String.class);
		request.addProperty(barcodeJson);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(validURL);

		try {
			androidHttpTransport.call(SOAP_ACTION + webMethNames, envelope);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		SoapPrimitive response = null;
		try {
			response = (SoapPrimitive) envelope.getResponse();
		} catch (SoapFault soapFault) {
			soapFault.printStackTrace();
		}
		String resTxt = response.toString();

		Log.d("SummaryProduct", resTxt);

		result = " { saveSettlement : " + resTxt + "}";

		JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(result);

			JSONArray jsonMainNode = jsonResponse.optJSONArray("saveSettlement");

			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				sum_result = jsonChildNode.optString("Result").toString();

			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return  sum_result;


	}
}



//							if(SalesOrderSetGet.getTranType().matches("COR")||(SalesOrderSetGet.getTranType().matches("COI"))) {
//							Log.d("getHashMap_list", "-->" + SalesOrderSetGet.getHashMap_list().size() + " " + SalesOrderSetGet.getHashMap().size());
//							for (int j = 0; j < SalesOrderSetGet.getHashMap_list().size(); j++) {
//										HashMap<String, String> datavalues = SalesOrderSetGet.getHashMap_list().get(j);
//										Set<Map.Entry<String, String>> key = datavalues.entrySet();
//										Iterator<Map.Entry<String, String>> iterators = key.iterator();
//										while (iterators.hasNext()) {
//											@SuppressWarnings("rawtypes")
//											Map.Entry mapEntrys = iterators.next();
//											con_no = (String) mapEntrys.getKey();
//											s_No = (String) mapEntrys.getValue();
//											int sNo = Integer.parseInt(s_No);
//											stockRefNo = con_no;
//											sno = sNo;
//
//											Log.d("StockrefNo", "-->" + stockRefNo + " " + sno);
//
//											for (int i = 0; i < SalesOrderSetGet.getHashMap().size(); i++) {
//
//												HashMap<String, String> datavalue = SalesOrderSetGet.getHashMap().get(i);
//												Set<Map.Entry<String, String>> keys = datavalue.entrySet();
//												Iterator<Map.Entry<String, String>> iterator = keys.iterator();
//												while (iterator.hasNext()) {
//													@SuppressWarnings("rawtypes")
//													Map.Entry mapEntry = iterator.next();
//													String cons_no = (String) mapEntry.getKey();
//													qty_no = (String) mapEntry.getValue();
//													Log.d("ConsignmentRet", "-->" + cons_no + " " + qty_no);
//													double Qty = Double.parseDouble(qty_no);
//													double result = Qty / pcs;
//													double result1 = Qty % pcs;
//													String cty, lty, quan;
//													cty = String.valueOf(result);
//													lty = String.valueOf(result1);
//													quan = String.valueOf(Qty);
//													if(con_no.matches(cons_no)){
//														c_Qty = cty;
//														l_Qty = lty;
//														quantity = quan;
//													}
//
//													Log.d("Results", "-->" + result + " " + result1);
//												}}
//
//											Log.d("QuantityCount", "-->" + cQty + " " + lQty + " " + qty);
//
//											stock_json += sno + "^" + pCode + "^" + c_Qty + "^" + l_Qty + "^" +
//													quantity + "^" + foc + "^" + exchangeQty + "^" + cprice + "^" + price + "^" + "0"
//													+ "^" + "10/10/1990" + "^" + pcsPerCarton + "^" + stockRefNo + "!";
//										}
//
//									}
//
//
//							}else{
//							stockRefNo ="";
//							if (ConvertToSetterGetter.getSoNo() == null || ConvertToSetterGetter.getSoNo().trim().equals("") &&
//									ConvertToSetterGetter.getEdit_do_no() == null || ConvertToSetterGetter.getEdit_do_no().trim().equals("")) {
//								sno = slNo;
//							} else {
//								int SOSlNo = SOSlno.equals("") ? 0 : Integer.valueOf(SOSlno);
//								if (SOSlNo > 0) {
//									sno = SOSlNo;
//								} else {
//									sno = slNo;
//								}
//							}
//
//
//						}
