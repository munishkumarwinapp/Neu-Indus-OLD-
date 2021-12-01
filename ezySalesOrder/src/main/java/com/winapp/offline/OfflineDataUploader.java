package com.winapp.offline;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.ProductBarcode;
import com.winapp.sot.RowItem;
import com.winapp.sot.SalesOrderSetGet;

public class OfflineDataUploader {
	
	public interface OnUploadCompletionListener {
		public void onCompleted();

	}

	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
//	private static int TimeOut = 20000;
	private static String URL; // , deviceId;
	private static String uploadFlag;
	private OfflineHelper helper;
	private OfflineDatabase offlinedb;
	private OfflineDataDownloader dataDownloader;
	private Activity mActivity;
	private ArrayList<String> updateCustomerCode = new ArrayList<String>();
	private OfflineDataSynch offlineDataSynch;
	static ArrayList<ProductBarcode> allproducts = new ArrayList<ProductBarcode>();
    static ArrayList<String> resultArr = new ArrayList<String>();
	static String header_json = "", detail_json = "";
	String webmethod="";
	private OnUploadCompletionListener listener;
	private Exception exception = null;
	
	public OfflineDataUploader(String url, Activity activity) {
		mActivity = activity;
		helper = new OfflineHelper(activity);
		URL = url;
		this.offlinedb = new OfflineDatabase(activity);
	}
	
	public void setOnUploadCompletionListener(OnUploadCompletionListener listener) {
		this.listener = listener;
	}

	public void startUpload(final String flag, final String webMethNameGet,
			final Activity activity) {
		uploadFlag = flag;
		webmethod=webMethNameGet;
//		helper.showProgressDialog(R.string.upload_customer);
//		new Thread() {
//			@Override
//			public void run() {
//				// offlinedb.truncateTables();
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
						AsyncCallUpload upload = new AsyncCallUpload();
						upload.execute();
//						
//					}
//				});
//			}
//		}.start();
	}
	
	private class AsyncCallUpload extends AsyncTask<Void, Void, Void> {
		String customer = "", invoice = "", receipt = "", so="", sign_image="";
		String gnrlStngs ="";
		
		@Override
		protected void onPreExecute() {
			exception = null;
			helper.showProgressDialog(R.string.upload_data);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			try {
				
				Log.d("upload url", "uuuu"+ URL);
				
				 customer = uploadCustomer(webmethod);
					so = uploadSO("fnUploadSO");
				 invoice = uploadInvoice("fnUploadInvoice");
				 receipt = uploadReceipt("fncUploadReceipt");

				 sign_image = uploadImage("fncSaveInvoiceImages");
				
				 mobileSettings(URL,"fncGetMobileSettings");
				 generalSettingsService(URL,"fncGetGeneralSettings");
				 
			} catch (Exception e) {
				exception = e;
				customer = "";
				invoice = ""; 
				receipt = "";
				so = "";
				sign_image="";
			}
				
			return null;
		}
 
		@Override
		protected void onPostExecute(Void result) {
	
		//customer	
			
			if((!customer.matches("") && !customer.matches("failed"))){
				if(customer.matches("upload")){
					helper.showLongToast(R.string.customer_uploaded_successfully);
				}
//				else if(customer.matches("empty")){
//					helper.showLongToast(R.string.no_data_upload);
//				}
				
			}else{
				helper.showLongToast(R.string.customer_upload_failed);
			}
			
		//invoice
			
			if((!invoice.matches("") && !invoice.matches("failed"))){
				
				if(invoice.matches("upload")){
					helper.showLongToast(R.string.invoice_uploaded_successfully);
				}
//				else if(invoice.matches("empty")){
//					helper.showLongToast(R.string.no_data_upload);
//				}
				
				
			}else{
				helper.showLongToast(R.string.invoice_upload_failed);
			}
			
		//receipt
			
			if((!receipt.matches("") && !receipt.matches("failed"))){
				
				if(receipt.matches("upload")){
					helper.showLongToast(R.string.receipt_uploaded_successfully);
				}
//				else if(receipt.matches("empty")){
//					helper.showLongToast(R.string.no_data_upload);
//				}		
				
			}else{
				helper.showLongToast(R.string.receipt_upload_failed);
			}
			
		//salesorder	
			
			if((!so.matches("") && !so.matches("failed"))){
				
				if(so.matches("upload")){
					helper.showLongToast(R.string.so_uploaded_successfully);
				}
//				else if(receipt.matches("empty")){
//					helper.showLongToast(R.string.no_data_upload);
//				}		
				
			}else{
				helper.showLongToast(R.string.so_upload_failed);
			}
			
			
		//signature image
				
				if((!sign_image.matches("") && !sign_image.matches("failed"))){
					
					if(sign_image.matches("upload")){
						helper.showLongToast(R.string.signimage_uploaded_successfully);
					}
	//				else if(receipt.matches("empty")){
	//					helper.showLongToast(R.string.no_data_upload);
	//				}		
					
				}else{
					helper.showLongToast(R.string.signimage_upload_failed);
				}
					
			if((!customer.matches("") && !customer.matches("failed")) && (!invoice.matches("") && !invoice.matches("failed"))
					&& (!receipt.matches("") && !receipt.matches("failed")) && (!so.matches("") && !so.matches("failed")) 
					&& (!sign_image.matches("") && !sign_image.matches("failed"))){
				
				if(exception == null){
					
					if(customer.matches("empty") && invoice.matches("empty") && receipt.matches("empty") && so.matches("empty") && sign_image.matches("empty")){
						helper.showLongToast(R.string.no_data_upload);
					}else{
						helper.showLongToast(R.string.data_uploaded_successfully);
					}
					
					
						if (uploadFlag.matches("upload")) {
		 					FWMSSettingsDatabase.init(mActivity);
							String valid_url = FWMSSettingsDatabase.getUrl();
							dataDownloader = new OfflineDataDownloader(mActivity, valid_url);
							dataDownloader.startDownload(true,"Synchronizing data");
						} else{
							if (listener != null) {
								Log.d("OfflineDataUploader ", "listener ");
								listener.onCompleted();
							}
					}
				}else{
					helper.showErrorDialog(Log.getStackTraceString(exception));	
				}
				
				 	
			}else{
				if(exception != null){
					helper.showErrorDialog(Log.getStackTraceString(exception));		
				}
			}
		
			helper.dismissProgressDialog();
		}
			
	}
	
	/* Upload Customer*/
	public String uploadCustomer(String webMethNameGet) {
		
		String result = "";

		String deviceId = OfflineDatabase.getDeviceId();
		String nextCustomerNo = OfflineDatabase.getNextNo("CustomerNextNo");
		String mData = "";
		
		Cursor cursor = OfflineDatabase.getCustomersCursorvalue();
		Log.d("cursor Count", "-->" + cursor.getCount());
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					String CustomerCode = cursor
							.getString(cursor
									.getColumnIndex(OfflineDatabase.COLUMN_CUSTOMERCODE));
					String CustomerName = cursor
							.getString(cursor
									.getColumnIndex(OfflineDatabase.COLUMN_CUSTOMERNAME));
					String ContactPerson = cursor
							.getString(cursor
									.getColumnIndex(OfflineDatabase.COLUMN_CONTACTPERSON));
					String Address1 = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.COLUMN_ADDRESS1));
					String Address2 = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.COLUMN_ADDRESS2));
					String Address3 = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.COLUMN_ADDRESS3));
					String PhoneNo = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.COLUMN_PHONENO));
					String HandphoneNo = cursor
							.getString(cursor
									.getColumnIndex(OfflineDatabase.COLUMN_HANDPHONENO));
					String Email = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.COLUMN_EMAIL));
					String HaveTax = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.COLUMN_HAVETAX));
					
					if(HaveTax.matches("True")){
						HaveTax = "1";
					}else{
						HaveTax = "0";
					}
					
					String CustomerGroupCode = cursor
							.getString(cursor
									.getColumnIndex(OfflineDatabase.COLUMN_CUSTOMERGROUPCODE));

					String CreateUser = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.CREATE_USER));
					String CreateDate = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.CREATE_DATE));

					String ModifyUser = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.MODIFY_USER));
					String ModifyDate = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.MODIFY_DATE));

					String TermCode = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.COLUMN_TERMCODE));
					String IsActive = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.COLUMN_ISACTIVE));
					String CreditLimit = cursor
							.getString(cursor
									.getColumnIndex(OfflineDatabase.COLUMN_CREDITLIMIT));
					String TaxType = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.COLUMN_TAXTYPE));
					String TaxPerc = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.COLUMN_TAXVALUE));
					String CompanyCode = cursor
							.getString(cursor
									.getColumnIndex(OfflineDatabase.COLUMN_COMPANYCODE));
					String FaxNo = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.COLUMN_FAXNO));
					String AreaCode = cursor.getString(cursor
							.getColumnIndex(OfflineDatabase.COLUMN_AREACODE));

					mData += CustomerCode + "^" + CustomerName + "^"
							+ ContactPerson + "^" + Address1 + "^" + Address2
							+ "^" + Address3 + "^" + PhoneNo + "^"
							+ HandphoneNo + "^" + Email + "^" + HaveTax + "^"
							+ CustomerGroupCode + "^" + CreateUser + "^"
							+ CreateDate + "^" + ModifyUser + "^" + ModifyDate
							+ "^" + TermCode + "^" + IsActive + "^"
							+ CreditLimit + "^" + TaxType + "^" + TaxPerc + "^"
							+ CompanyCode + "^" + FaxNo + "^" + AreaCode + "!";
					updateCustomerCode.add(CustomerCode);

				} while (cursor.moveToNext());
			}
			mData = mData.substring(0, mData.length() - 1);
			Log.d("Customer mData", mData);
			Log.d("Customer DeviceId", deviceId);
			Log.d("Customer nextCustomerNo", nextCustomerNo);

			SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
			PropertyInfo data = new PropertyInfo();
			PropertyInfo deviceid = new PropertyInfo();
			PropertyInfo nextno = new PropertyInfo();

			data.setName("sData");
			data.setValue(mData);
			data.setType(String.class);
			request.addProperty(data);

			deviceid.setName("DeviceId");
			deviceid.setValue(deviceId);
			deviceid.setType(String.class);
			request.addProperty(deviceid);

			nextno.setName("NextCustomerNo");
			nextno.setValue(nextCustomerNo);
			nextno.setType(String.class);
			request.addProperty(nextno);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			envelope.bodyOut = request;
			try {
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

				androidHttpTransport = new HttpTransportSE(URL);
				androidHttpTransport.call(SOAP_ACTION + webMethNameGet,envelope);
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				result = response.toString();
				result = " { JsonArray: " + result + "}";
				Log.d("Result", result);

				JSONObject customer_jsonResponse = new JSONObject(result);
				JSONArray customer_jsonMainNode = customer_jsonResponse
						.optJSONArray("JsonArray");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = customer_jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode;
					try {

						jsonChildNode = customer_jsonMainNode.getJSONObject(i);

						result = jsonChildNode.optString("Result").toString();

					} catch (JSONException e) {
						if(exception == null){
							exception = e;
						}
					}
				}
				
				if (result.matches("")) {
					result = "failed";
					Log.d("Summary Result", result);
				}else{
					result = "upload";
					OfflineDatabase.getCustomerUpdateStaus(updateCustomerCode);
				}
		
			} catch (Exception e) {
				result = "failed";
				if(exception == null){
					exception = e;
				}
			}
		}else{
			result = "empty";
		}
	
		return result;
	}

	/* Upload Invoice*/
	public String uploadInvoice(String webMethName){
		
		
		int mCursorCount = 0, uploadcount = 0;
		String deviceId = OfflineDatabase.getDeviceId();
		
		String nextInvoiceNo = OfflineDatabase.getNextNo("NextInvoiceNo");
		Cursor invoiceHeadercursor = OfflineDatabase.getCursorForUploadHeader("tblGetInvoiceHeader");
		mCursorCount = invoiceHeadercursor.getCount();
		String result = "";
		detail_json = "";
		header_json = "";
//		barcode_json = "";
		String batch_json="";
		String ReceiptNo="";
		String appType = LogOutSetGet.getApplicationType();
		
//		String currencyCode = SalesOrderSetGet.getCurrencycode();
//		String remarks = SalesOrderSetGet.getRemarks();
//		String currencyRate = SalesOrderSetGet.getCurrencyrate();
//		String soDate = SalesOrderSetGet.getSaleorderdate();

		try {
			
		if (invoiceHeadercursor != null && invoiceHeadercursor.getCount() > 0) {

			if (invoiceHeadercursor.moveToFirst()) {
				do {
					String sum_result = "";
					header_json = "";
					detail_json = "";
					String invoiceNumber = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_INVOICENO));
					
					String iDate = invoiceHeadercursor.getString(invoiceHeadercursor.getColumnIndex(OfflineDatabase.COLUMN_INVOICEDATE));

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date myDate = dateFormat.parse(iDate);
					SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
					
					String invoiceDate = timeFormat.format(myDate);
					
					String locationCode = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_LOCATIONCODE));
					
					String customerCode = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_CUSTOMERCODE));
					
					String ttl = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_TOTAL));
					
					String itemDisc = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_ITEMDISCOUNT));
					
					String blDsc = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_BILLDISCOUNT));
					
					String totDisc = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_TOTALDISCOUNT));
					
					String subTotal = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_SUBTOTAL));
					
					String totTax = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_TAX));
					
					String netTotal = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_NETTOTAL));
					
					String remarks = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_REMARKS));
					
					String currencyCode = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_CURRENCYCODE));
					
					String currencyRate = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_CURRENCYRATE));
					
					String dbCreateUser = invoiceHeadercursor.getString(invoiceHeadercursor
							.getColumnIndex(OfflineDatabase.CREATE_USER));
					
					String SoNo="";
					
					String DoNo="";
					String device_name = RowItem.getDeviceName();

					String taxtype = SalesOrderSetGet.getTaxType();

					if(taxtype!=null && !taxtype.isEmpty()){

					}else{
						taxtype="";
					}
					Log.d("taxtype","tt "+taxtype);

					String taxPerc = SalesOrderSetGet.getTaxPerc();

					if(taxPerc!=null && !taxPerc.isEmpty()){

					}else{
						taxPerc="";
					}
					Log.d("taxPerc","tt "+taxPerc);

					String taxcode = SalesOrderSetGet.getTaxCode();

					if(taxcode!=null && !taxcode.isEmpty()){

					}else{
						taxcode="";
					}

					header_json = invoiceNumber + "^" + invoiceDate + "^" + locationCode + "^"
							+ customerCode + "^" + ttl + "^" + itemDisc + "^" + blDsc + "^"
							+ totDisc + "^" + subTotal + "^" + totTax + "^" + netTotal
							+ "^" + remarks + "^" + currencyCode + "^" + currencyRate + "^"
							+ SoNo + "^" + DoNo + "^" + deviceId + "^" + "W" + "^" + device_name + "^" + "M" + "^" + taxcode +"^"+ taxtype + "^"+ taxPerc + "^"
							+ "I" + "^" + dbCreateUser ;
					
					
					int slNo = 1;
					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put("ColumnName", "InvoiceNo");
					hm.put("ColumnValue", invoiceNumber);
					Cursor invoiceDetailcursor = OfflineDatabase.getCursorForUploadDetails("tblGetInvoiceDetail", hm);

					if (invoiceDetailcursor != null && invoiceDetailcursor.getCount() > 0) {

						if (invoiceDetailcursor.moveToFirst()) {
							do {
								String pCode = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_PRODUCTCODE));
								String pName = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_PRODUCTNAME));
								String cQty = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_CQTY));
								String lQty = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_LQTY));
								String qty = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_QTY));
								String foc = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_FOCQTY));
								String price = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_PRICE));
								String pcsPerCarton = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_PCSPERCARTON));
								String total = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_TOTAL));
								String itemDiscount = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_ITEMDISCOUNT));
								String taxType = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_TAXTYPE));
								String taxValue = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_TAXPERC));
								String retail_Price = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_RETAILPRICE));
								String sub_ToTal = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_SUBTOTAL));
								String net_ToTal = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_NETTOTAL));
								String tax_amount = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_TAX));
								
								String cprice = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_CARTONPRICE));
								
								String exchangeQty = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_EXCHANGEQTY));
								
								String totaldisc =  invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_TOTALDISCOUNT));
								
								String billdisc = invoiceDetailcursor.getString(invoiceDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_BILLDISCOUNT));
								
//								String ItemRemarks = invoiceDetailcursor.getString(invoiceDetailcursor
//										.getColumnIndex(OfflineDatabase.COLUMN_ITEM_REMARKS));
								String ItemRemarks = "";
								
//								String SOSlno = invoiceDetailcursor.getString(invoiceDetailcursor
//										.getColumnIndex(OfflineDatabase.COLUMN_SO_SLNO));
								
								String SOSlno = "";
								
								if(SOSlno!=null && !SOSlno.isEmpty()){
									
								}else{
									SOSlno="";
								}
								
								if(exchangeQty!=null && !exchangeQty.isEmpty()){
									
								}else{
									exchangeQty="";
								}

								detail_json += slNo + "^" + pCode + "^" + pName + "^"
										+ cQty + "^" + lQty + "^" + qty + "^" + foc + "^"
										+ pcsPerCarton + "^" + retail_Price + "^" + price
										+ "^" + total + "^" + itemDiscount + "^" + billdisc
										+ "^" + totaldisc + "^" + sub_ToTal + "^" + tax_amount
										+ "^" + net_ToTal + "^" + taxType + "^" + taxValue
										+ "^" + "I" + "^" + SOSlno + "^" + exchangeQty +"^" + cprice + "^" + ItemRemarks + "^"+ taxcode + "!";
								
								slNo++;
							} while (invoiceDetailcursor.moveToNext());
						}
						detail_json = detail_json.substring(0, detail_json.length() - 1);
					}
					Log.d("Detail", detail_json);
					Log.d("Header", header_json);
					
					
					SoapObject request = new SoapObject(NAMESPACE, webMethName);

					PropertyInfo productJson = new PropertyInfo();
					PropertyInfo barcodeJson = new PropertyInfo();
					PropertyInfo doNumbersJson = new PropertyInfo();
					PropertyInfo productBarcodeJson = new PropertyInfo();
					PropertyInfo nextNoJson = new PropertyInfo();
					
					PropertyInfo companyCode = new PropertyInfo();

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
					
					nextNoJson.setName("NextInvoiceNo");
					nextNoJson.setValue(nextInvoiceNo);
					nextNoJson.setType(String.class);
					request.addProperty(nextNoJson);


					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					envelope.dotNet = true;
					envelope.setOutputSoapObject(request); // prepare request
					envelope.bodyOut = request;

					try {
					HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

					androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
					SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
					String resTxt = response.toString();

					Log.d("Summary Product", resTxt);

					result = " { SOResult : " + resTxt + "}";

					JSONObject jsonResponse;
				
						jsonResponse = new JSONObject(result);

						JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");

						int lengthJsonArr = jsonMainNode.length();
						for (int i = 0; i < lengthJsonArr; i++) {
							JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

							sum_result = jsonChildNode.optString("Result").toString();
							ReceiptNo = jsonChildNode.optString("ReceiptNo").toString();

							if (sum_result.matches("")) {
								sum_result = "failed";
								Log.d("Summary Result", sum_result);
							}else{
								OfflineDatabase.getCursorForUpdateStaus("tblGetInvoiceHeader", hm);
								OfflineDatabase.getCursorForUpdateStaus("tblGetInvoiceDetail", hm);
								uploadcount = uploadcount+1;
							}
						}
						
					} catch (Exception e) {
						result = "failed";
						if(exception == null){
							exception = e;
						}
					}
					
				} while (invoiceHeadercursor.moveToNext());
			}
			
			
			if (mCursorCount == uploadcount) {
				result = "upload";
			} else {
				int diff = mCursorCount - uploadcount;
				Log.d("not uploaded data Count", "... "+ diff);
				result = "failed";
			}
			
		}else{
			result = "empty";
		}

		} catch (Exception e) {
			result = "failed";
			if(exception == null){
				exception = e;
			}
		}

		return result;
	}
	
	/* Upload Receipt*/
	public String uploadReceipt(String webMethName) throws ParseException {

//		helper.updateProgressDialog(R.string.upload_receipt);
		String result = "";
		
		int mCursorCount = 0, uploadcount = 0;
		String deviceId = OfflineDatabase.getDeviceId();
		
		String nextReceiptNo = OfflineDatabase.getNextNo("NextReceiptNo");

		Cursor receiptHeadercursor = OfflineDatabase
				.getCursorForUploadHeader("tblGetReceiptHeader");
		mCursorCount = receiptHeadercursor.getCount();

		try {
			if (receiptHeadercursor != null
					&& receiptHeadercursor.getCount() > 0) {

				if (receiptHeadercursor.moveToFirst()) {
					do {
						
						String sum_result = "";
						header_json = "";
						detail_json = "";

						String receiptNumber = receiptHeadercursor
								.getString(receiptHeadercursor
										.getColumnIndex(OfflineDatabase.COLUMN_RECEIPTNO));

						String iDate = receiptHeadercursor
								.getString(receiptHeadercursor
										.getColumnIndex(OfflineDatabase.COLUMN_RECEIPTDATE));

						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						Date myDate = dateFormat.parse(iDate);
						SimpleDateFormat timeFormat = new SimpleDateFormat(
								"dd/MM/yyyy");

						String serverdate = timeFormat.format(myDate);

						String cust_Code = receiptHeadercursor
								.getString(receiptHeadercursor
										.getColumnIndex(OfflineDatabase.COLUMN_CUSTOMERCODE));

						String dbPaidAmount = receiptHeadercursor
								.getString(receiptHeadercursor
										.getColumnIndex(OfflineDatabase.COLUMN_PAIDAMOUNT));

						String dbCreditAmount = receiptHeadercursor
								.getString(receiptHeadercursor
										.getColumnIndex(OfflineDatabase.COLUMN_CREDITAMOUNT));

						String payment = receiptHeadercursor
								.getString(receiptHeadercursor
										.getColumnIndex(OfflineDatabase.COLUMN_PAYMODE));

						String bank_Code = receiptHeadercursor
								.getString(receiptHeadercursor
										.getColumnIndex(OfflineDatabase.COLUMN_BANKCODE));

						String check_No = receiptHeadercursor
								.getString(receiptHeadercursor
										.getColumnIndex(OfflineDatabase.COLUMN_CHEQUENO));

						String cust_Date = receiptHeadercursor
								.getString(receiptHeadercursor
										.getColumnIndex(OfflineDatabase.COLUMN_CHEQUEDATE));

						String dbCreateUser = receiptHeadercursor
								.getString(receiptHeadercursor
										.getColumnIndex(OfflineDatabase.CREATE_USER));

						header_json = receiptNumber + "^" + serverdate + "^" + cust_Code + "^"
								+ dbPaidAmount + "^" + dbCreditAmount + "^"
								+ payment + "^" + bank_Code + "^" + check_No
								+ "^" + cust_Date + "^" + deviceId + "^"
								+ dbCreateUser;


						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("ColumnName", "ReceiptNo");
						hm.put("ColumnValue", receiptNumber);
						Cursor receiptDetailcursor = OfflineDatabase
								.getCursorForUploadDetails("tblGetReceiptDetail", hm);

						if (receiptDetailcursor != null
								&& receiptDetailcursor.getCount() > 0) {

							if (receiptDetailcursor.moveToFirst()) {
								do {
									String invNo = receiptDetailcursor
											.getString(receiptDetailcursor
													.getColumnIndex(OfflineDatabase.COLUMN_INVOICENO));
									String netTotal = receiptDetailcursor
											.getString(receiptDetailcursor
													.getColumnIndex(OfflineDatabase.COLUMN_NETTOTAL));
									String paidAmt = receiptDetailcursor
											.getString(receiptDetailcursor
													.getColumnIndex(OfflineDatabase.COLUMN_PAIDAMOUNT));
									String creditAmt = receiptDetailcursor
											.getString(receiptDetailcursor
													.getColumnIndex(OfflineDatabase.COLUMN_CREDITAMOUNT));

									detail_json += invNo + "^" + netTotal + "^"
											+ paidAmt + "^" + creditAmt + "!";

								} while (receiptDetailcursor.moveToNext());
							}
							detail_json = detail_json.substring(0,
									detail_json.length() - 1);
						}
						Log.d("Receipt Detail", detail_json);
						Log.d("Receipt Header", header_json);

						SoapObject request = new SoapObject(NAMESPACE,
								webMethName);

						PropertyInfo detail = new PropertyInfo();
						PropertyInfo header = new PropertyInfo();
						PropertyInfo companyCode = new PropertyInfo();
						PropertyInfo nextReceiptno = new PropertyInfo();

						String cmpnyCode = SupplierSetterGetter
								.getCompanyCode();

						Log.d("cmpnyCode","check"+cmpnyCode);
						Log.d("nextReceiptNo",nextReceiptNo);

						companyCode.setName("CompanyCode");
						companyCode.setValue(cmpnyCode);
						companyCode.setType(String.class);
						request.addProperty(companyCode);

						header.setName("sHeader");
						header.setValue(header_json);
						header.setType(String.class);
						request.addProperty(header);

						detail.setName("sDetail");
						detail.setValue(detail_json);
						detail.setType(String.class);
						request.addProperty(detail);

						nextReceiptno.setName("NextReceiptNo");
						nextReceiptno.setValue(nextReceiptNo);
						nextReceiptno.setType(String.class);
						request.addProperty(nextReceiptno);

						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
								SoapEnvelope.VER11);
						envelope.dotNet = true;
						envelope.setOutputSoapObject(request);
						envelope.bodyOut = request;
						try {
							HttpTransportSE androidHttpTransport = new HttpTransportSE(
									URL);

							androidHttpTransport = new HttpTransportSE(URL);
							androidHttpTransport.call(
									SOAP_ACTION + webMethName, envelope);
							SoapPrimitive response = (SoapPrimitive) envelope
									.getResponse();

							String resTxt = response.toString();
							result = " { JsonArray: " + resTxt + "}";
							Log.d("Result", result);
							
							JSONObject jsonResponse;				
							jsonResponse = new JSONObject(result);
							JSONArray jsonMainNode = jsonResponse.optJSONArray("JsonArray");

							int lengthJsonArr = jsonMainNode.length();
							for (int i = 0; i < lengthJsonArr; i++) {
								JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

								sum_result = jsonChildNode.optString("Result").toString();
								Log.d("sum_result", sum_result);
								if (sum_result.matches("")) {
									sum_result = "failed";
									Log.d("Summary Result", sum_result);
								}else{
									OfflineDatabase.getCursorForUpdateStaus("tblGetReceiptHeader", hm);
									OfflineDatabase.getCursorForUpdateStaus("tblGetReceiptDetail", hm);
									uploadcount = uploadcount+1;
								}
							}

						} catch (Exception e) {
							result = "failed";
							if(exception == null){
								exception = e;
							}
						}

					} while (receiptHeadercursor.moveToNext());
				}


				if (mCursorCount == uploadcount) {
					result = "upload";
				} else {
					int diff = mCursorCount - uploadcount;
					Log.d("receipt not uploaded data Count", "... " + diff);
					result = "failed";
				}

			} else{
				result = "empty";
			}

		} catch (Exception e) {
			result = "failed";
			if(exception == null){
				exception = e;
			}
		}

		return result;
	}
	
	/* Upload Invoice*/
	public String uploadSO(String webMethName){
		
		int mCursorCount = 0, uploadcount = 0;
		String deviceId = OfflineDatabase.getDeviceId();
		
		String nextSoNo = OfflineDatabase.getNextNo("NextSoNo");
		Cursor soHeadercursor = OfflineDatabase.getCursorForUploadHeader("tblGetSOHeader");
		mCursorCount = soHeadercursor.getCount();
		String result = "";
		detail_json = "";
		header_json = "";
//		barcode_json = "";
		String appType = LogOutSetGet.getApplicationType();
		
//		String currencyCode = SalesOrderSetGet.getCurrencycode();
//		String remarks = SalesOrderSetGet.getRemarks();
//		String currencyRate = SalesOrderSetGet.getCurrencyrate();
//		String soDate = SalesOrderSetGet.getSaleorderdate();

		try {
			
		if (soHeadercursor != null && soHeadercursor.getCount() > 0) {

			if (soHeadercursor.moveToFirst()) {
				do {
					String sum_result = "";
					header_json = "";
					detail_json = "";
					String soNumber = soHeadercursor.getString(soHeadercursor.getColumnIndex(OfflineDatabase.COLUMN_SONO));
					
					String sDate = soHeadercursor.getString(soHeadercursor.getColumnIndex(OfflineDatabase.COLUMN_SODATE));
					String dDate = soHeadercursor.getString(soHeadercursor.getColumnIndex(OfflineDatabase.COLUMN_DELIVERYDATE));

					SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
					
					Date SoDate = inputFormat.parse(sDate);
					Date DeliveryDate = inputFormat.parse(dDate);
					
					String so_Date = outputFormat.format(SoDate);
					String delivery_Date = outputFormat.format(DeliveryDate);
					
					String locationCode = soHeadercursor.getString(soHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_LOCATIONCODE));
					
					String customerCode = soHeadercursor.getString(soHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_CUSTOMERCODE));
					
					String ttl = soHeadercursor.getString(soHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_TOTAL));
					
					String itemDisc = soHeadercursor.getString(soHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_ITEMDISCOUNT));
					
					String blDsc = soHeadercursor.getString(soHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_BILLDISCOUNT));
					
					String totDisc = soHeadercursor.getString(soHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_TOTALDISCOUNT));
					
					String subTotal = soHeadercursor.getString(soHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_SUBTOTAL));
					
					String totTax = soHeadercursor.getString(soHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_TAX));
					
					String netTotal = soHeadercursor.getString(soHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_NETTOTAL));
					
					String remarks = soHeadercursor.getString(soHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_REMARKS));
					
					String currencyCode = soHeadercursor.getString(soHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_CURRENCYCODE));
					
					String currencyRate = soHeadercursor.getString(soHeadercursor
							.getColumnIndex(OfflineDatabase.COLUMN_CURRENCYRATE));
					
					String dbCreateUser = soHeadercursor.getString(soHeadercursor
							.getColumnIndex(OfflineDatabase.CREATE_USER));
					
					header_json =  soNumber + "^" + so_Date + "^" + delivery_Date + "^" + locationCode
							+ "^" + customerCode + "^" + ttl + "^" + itemDisc + "^" + blDsc
							+ "^" + totDisc + "^" + subTotal + "^" + totTax + "^"
							+ netTotal + "^" + remarks + "^" + "0" + "^" + currencyCode
							+ "^" + currencyRate + "^" + deviceId + "^" + dbCreateUser;
					
					
					int slNo = 1;
					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put("ColumnName", "SoNo");
					hm.put("ColumnValue", soNumber);
					Cursor soDetailcursor = OfflineDatabase.getCursorForUploadDetails("tblGetSODetail", hm);

					if (soDetailcursor != null && soDetailcursor.getCount() > 0) {

						if (soDetailcursor.moveToFirst()) {
							do {
								String pCode = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_PRODUCTCODE));
								
								String pName = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_PRODUCTNAME));
								
								String cQty = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_CQTY));
								
								String lQty = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_LQTY));
								
								String qty = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_QTY));
								
								String foc = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_FOCQTY));
								
								String price = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_PRICE));
								
								String pcsPerCarton = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_PCSPERCARTON));
								
								String total = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_TOTAL));
								
								String itemDiscount = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_ITEMDISCOUNT));
								
								String taxType = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_TAXTYPE));
								
								String taxValue = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_TAXPERC));
								
								String retail_Price = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_RETAILPRICE));
								
								String subTtl = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_SUBTOTAL));
								
								String netTtl = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_NETTOTAL));
								
								String txAmt = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_TAX));
								
								String cprice = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_CARTONPRICE));
								
								String exchangeQty = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_EXCHANGEQTY));
								
								String totalDisc =  soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_TOTALDISCOUNT));
								
								String billDisc = soDetailcursor.getString(soDetailcursor
										.getColumnIndex(OfflineDatabase.COLUMN_BILLDISCOUNT));
								
//								String ItemRemarks = soDetailcursor.getString(soDetailcursor
//										.getColumnIndex(OfflineDatabase.COLUMN_ITEM_REMARKS));
								String ItemRemarks = "";
								
//								String SOSlno = soDetailcursor.getString(soDetailcursor
//										.getColumnIndex(OfflineDatabase.COLUMN_SO_SLNO));
								
								String SOSlno = "";
								
								if(SOSlno!=null && !SOSlno.isEmpty()){
									
								}else{
									SOSlno="";
								}
								
								if(exchangeQty!=null && !exchangeQty.isEmpty()){
									
								}else{
									exchangeQty="";
								}
								
								detail_json += slNo + "^" + pCode + "^" + pName + "^"
										+ cQty + "^" + lQty + "^" + qty + "^" + foc + "^"
										+ pcsPerCarton + "^" + retail_Price + "^" + price
										+ "^" + total + "^" + itemDiscount + "^" + billDisc
										+ "^" + totalDisc + "^" + subTtl + "^" + txAmt
										+ "^" + netTtl + "^" + taxType + "^" + taxValue
										+ "^" + "I" + "^" + exchangeQty +"^" + cprice + "!";
								
								slNo++;
							} while (soDetailcursor.moveToNext());
						}
						detail_json = detail_json.substring(0, detail_json.length() - 1);
					}
					Log.d("Detail", detail_json);
					Log.d("Header", header_json);
					
					
					SoapObject request = new SoapObject(NAMESPACE, webMethName);

					PropertyInfo productJson = new PropertyInfo();
					PropertyInfo barcodeJson = new PropertyInfo();
					
					PropertyInfo nextNoJson = new PropertyInfo();
					
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
					
					nextNoJson.setName("NextSONo");
					nextNoJson.setValue(nextSoNo);
					nextNoJson.setType(String.class);
					request.addProperty(nextNoJson);


					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.dotNet = true;
					envelope.setOutputSoapObject(request); // prepare request
					envelope.bodyOut = request;

					try {
					HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

					androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
					SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
					String resTxt = response.toString();

					Log.d("SOResult", resTxt);

					result = " { SOResult : " + resTxt + "}";

					JSONObject jsonResponse;
				
						jsonResponse = new JSONObject(result);

						JSONArray jsonMainNode = jsonResponse.optJSONArray("SOResult");

						int lengthJsonArr = jsonMainNode.length();
						for (int i = 0; i < lengthJsonArr; i++) {
							JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

							sum_result = jsonChildNode.optString("Result").toString();
							
							Log.d("Summary Result", sum_result);

							if (sum_result.matches("")) {
								sum_result = "failed";
								Log.d("Summary Result", sum_result);
							}else{
								OfflineDatabase.getCursorForUpdateStaus("tblGetSOHeader", hm);
								OfflineDatabase.getCursorForUpdateStaus("tblGetSODetail", hm);
								uploadcount = uploadcount+1;
							}
						}
						
					} catch (Exception e) {
						result = "failed";
						if(exception == null){
							exception = e;
						}
					}
					
				} while (soHeadercursor.moveToNext());
			}
			
			
			if (mCursorCount == uploadcount) {
				result = "upload";
			} else {
				int diff = mCursorCount - uploadcount;
				Log.d("not uploaded data Count", "... "+ diff);
				result = "failed";
			}
			
		}else{
			result = "empty";
		}

		} catch (Exception e) {
			result = "failed";
			if(exception == null){
				exception = e;
			}
		}

		return result;
	}
	
	/* Upload Invoice*/
	public String uploadImage(String webMethName){
		
		int mCursorCount = 0, uploadcount = 0;
		
		Cursor imageCursor = OfflineDatabase.getCursorForUploadImage("tblGetImage");
		mCursorCount = imageCursor.getCount();
		Log.d("uploadImage mCursorCount", "... "+ imageCursor.getCount());
		String result = "";

		try {
			
		if (imageCursor != null && imageCursor.getCount() > 0) {

			if (imageCursor.moveToFirst()) {
				do {
					String sum_result = "";
					
					String ComapanyCode = imageCursor.getString(imageCursor.getColumnIndex(OfflineDatabase.COLUMN_COMPANYCODE));
					String ImageNo = imageCursor.getString(imageCursor.getColumnIndex(OfflineDatabase.COLUMN_IMAGE_NO));
					
					String ImageType = imageCursor.getString(imageCursor
							.getColumnIndex(OfflineDatabase.COLUMN_IMAGE_TYPE));
					
					String SignatureImage = imageCursor.getString(imageCursor
							.getColumnIndex(OfflineDatabase.COLUMN_SIGNATURE_IMAGE));
					
					String ProductImage = imageCursor.getString(imageCursor
							.getColumnIndex(OfflineDatabase.COLUMN_PRODUCT_IMAGE));
					
					
					String user = SupplierSetterGetter.getUsername();

					SoapObject request = new SoapObject(NAMESPACE, webMethName);

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

					companyCode.setName("CompanyCode");
					companyCode.setValue(ComapanyCode);
					companyCode.setType(String.class);
					request.addProperty(companyCode);

					Latitude.setName("InvoiceNo");
					Latitude.setValue(ImageNo);
					Latitude.setType(String.class);
					request.addProperty(Latitude);

					InvoiceNo.setName("Latitude");
					InvoiceNo.setValue("");
					InvoiceNo.setType(String.class);
					request.addProperty(InvoiceNo);

					Longitude.setName("Longitude");
					Longitude.setValue("");
					Longitude.setType(String.class);
					request.addProperty(Longitude);

					Signature.setName("Signature");
					Signature.setValue(SignatureImage);
					Signature.setType(Byte.class);
					request.addProperty(Signature);
					
					RefPhoto.setName("RefPhoto");
					RefPhoto.setValue(ProductImage);
					RefPhoto.setType(Byte.class);
					request.addProperty(RefPhoto);

					User.setName("User");
					User.setValue(user);
					User.setType(String.class);
					request.addProperty(User);
					
					TranType.setName("TranType");
					TranType.setValue(ImageType);
					TranType.setType(String.class);
					request.addProperty(TranType);
					
					Address1.setName("Address1");
					Address1.setValue("");
					Address1.setType(String.class);
					request.addProperty(Address1);
					
					Address2.setName("Address2");
					Address2.setValue("");
					Address2.setType(String.class);
					request.addProperty(Address2);
					
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					envelope.dotNet = true;
					envelope.setOutputSoapObject(request); // prepare request
					envelope.bodyOut = request;
				
					try {
						HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

						androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
						SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
						String resTxt = response.toString();

						Log.d("SaveSignature", resTxt);

						result = " { SaveSignature : " + resTxt + "}";

						JSONObject jsonResponse;
					
							jsonResponse = new JSONObject(result);

							JSONArray jsonMainNode = jsonResponse.optJSONArray("SaveSignature");

							int lengthJsonArr = jsonMainNode.length();

							for (int i = 0; i < lengthJsonArr; i++) {
								JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
								sum_result = jsonChildNode.optString("Result").toString();

								if (sum_result.matches("")) {
									sum_result = "failed";
									Log.d("Summary Result", sum_result);
								}else{
									
//									HashMap<String, String> hm = new HashMap<String, String>();
//									hm.put("ColumnName", "ImageNo");
//									hm.put("ColumnValue", ImageNo);
//									OfflineDatabase.getCursorForUpdateStaus("tblGetImage", hm);
//									
									
									HashMap<String, String> hm = new HashMap<String, String>();
									hm.put("ImageNo", ImageNo);
									hm.put("ImageType", "DO");
									OfflineDatabase.deleteUploadImage(hm);
									OfflineDatabase.deleteInvoiceHeaderWithoutDOSign(ImageNo);
									
									uploadcount = uploadcount+1;
								}
							}
					} catch (Exception e) {

						result = "failed";
						if(exception == null){
							exception = e;
						}
					}
					
					
				} while (imageCursor.moveToNext());
			}
			
			
			if (mCursorCount == uploadcount) {
				result = "upload";
			} else {
				int diff = mCursorCount - uploadcount;
				Log.d("not uploaded data Count", "... "+ diff);
				result = "failed";
			}
			
		}else{
			result = "empty";
		}

		} catch (Exception e) {
			result = "failed";
			if(exception == null){
				exception = e;
			}
		}

		return result;
	}
	
	//General Setting
	public String generalSettingsService(String URL,
			String webMethNameGet) throws JSONException, IOException,
			XmlPullParserException {
		
		String SettingID = "", SettingValue = "",
				gnrlStngs = "";
		String resTxt = null;

		SoapObject request = new SoapObject(NAMESPACE, webMethNameGet);
		
		OfflineSettingsManager offlinemanager = new OfflineSettingsManager(mActivity);
		String comapanyCode = offlinemanager.getCompanyType();

		PropertyInfo companyCode = new PropertyInfo();
		companyCode.setName("CompanyCode");
				companyCode.setValue(comapanyCode);
				companyCode.setType(String.class);
				request.addProperty(companyCode);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11); 
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.bodyOut = request;
		try {
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
		androidHttpTransport.call(SOAP_ACTION + webMethNameGet, envelope);
		SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

		resTxt = response.toString();
		Log.d("General Settings", resTxt);

		String result = " { GeneralSettings : " + resTxt + "}";
		JSONObject jsonResponse;

		jsonResponse = new JSONObject(result);
		JSONArray jsonMainNode = jsonResponse.optJSONArray("GeneralSettings");
		
		int lengthJsonArr = jsonMainNode.length();
		for (int i = 0; i < lengthJsonArr; i++) {
			
			JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
			SettingID = jsonChildNode.optString("SettingID").toString();

			if (SettingID.matches("APPTYPE")) {
				SettingValue = jsonChildNode.optString("SettingValue")
						.toString();

				if (SettingValue.matches("S")) {
					gnrlStngs = SettingValue;
					Log.d("result ", gnrlStngs);
				} else if (SettingValue.matches("W")) {
					gnrlStngs = SettingValue;
					Log.d("result ", gnrlStngs);
				}

			}
			
			if (SettingID.matches("CALCCARTON")) {
				SettingValue = jsonChildNode.optString("SettingValue")
						.toString();

				if (SettingValue.matches("0")) {
					Log.d("CALCCARTON", LogOutSetGet.getCalcCarton());
					LogOutSetGet.setCalcCarton("0");
				} else if (SettingValue.matches("1")) {
					Log.d("CALCCARTON", LogOutSetGet.getCalcCarton());
					LogOutSetGet.setCalcCarton("1");
				}

			}
		  	
			if (SettingID.matches("CARTONPRICE")) {
				SettingValue = jsonChildNode.optString("SettingValue")
						.toString();

				if (SettingValue.matches("1")) {
					SalesOrderSetGet.setCartonpriceflag("1");
					Log.d("CartonPriceFlag", SalesOrderSetGet.getCartonpriceflag());
				} else if (SettingValue.matches("0")) {
					SalesOrderSetGet.setCartonpriceflag("0");
				}
			}
			
			if (SettingID.matches("RECEIPTONINVOICE")) {
				SettingValue = jsonChildNode.optString("SettingValue")
						.toString();
				
				Log.d("SettingValue receipt invoice", "errg"+SettingValue);

				if (SettingValue.matches("1")) {
					SalesOrderSetGet.setReceiptoninvoice("1");
				} else if (SettingValue.matches("0")) {
					SalesOrderSetGet.setReceiptoninvoice("0");
				}

			}
			
			 if(SettingID.matches("MOBILEPRODUCTSTOCKPRINT")){
				    SettingValue = jsonChildNode.optString("SettingValue")
				      .toString();
				    
				    Log.d("SettingValue mobile product stock print", "errg"+SettingValue);

				    if (SettingValue.matches("1")) {
				     SalesOrderSetGet.setMobileproductstockprint("1");
				    } else if (SettingValue.matches("0")) {
				     SalesOrderSetGet.setMobileproductstockprint("0");
				    }
				   }
			 
			 if(SettingID.matches("AUTOBATCHNO")){
				    SettingValue = jsonChildNode.optString("SettingValue")
				      .toString();
				    
				    Log.d("SettingValue mobile batch", "bat"+SettingValue);

				    if (SettingValue.matches("1")) {
				     SalesOrderSetGet.setAutoBatchNo("1");
				    } else if (SettingValue.matches("0")) {
				     SalesOrderSetGet.setAutoBatchNo("0");
				    }else{
					     SalesOrderSetGet.setAutoBatchNo("0");
					    }
				   }
			 
			 if(SettingID.matches("MOBILEPRINTINVOICEDETAIL")){
				 SettingValue = jsonChildNode.optString("SettingValue")
					      .toString();
				 
				 Log.d("SettingValue invoice print", SettingValue);
				 
				 if (SettingValue.matches("1")) {
					 
					 SalesOrderSetGet.setInvoiceprintdetail("1");
					 
				 } else if (SettingValue.matches("0")) {
					 SalesOrderSetGet.setInvoiceprintdetail("0");
				 }else{
					 SalesOrderSetGet.setInvoiceprintdetail("0");
				 }
			 }
			 			 
			 if(SettingID.matches("MOBILELOGINPAGE")){
				 SettingValue = jsonChildNode.optString("SettingValue")
					      .toString();
				 
				 Log.d("SettingValue mobile login", SettingValue);
				 
				 if (SettingValue.matches("M")) {
					 
					 SalesOrderSetGet.setMobileloginpage("M");
					 
				 } else if (SettingValue.matches("S")) {
					 SalesOrderSetGet.setMobileloginpage("S");
				 }else{
					 SalesOrderSetGet.setMobileloginpage("S");
				 }
			 }
			 
			 if(SettingID.matches("ENABLECUSTOMERCODE")){
				 SettingValue = jsonChildNode.optString("SettingValue")
					      .toString();
				 
				 Log.d("ENABLECUSTOMERCODE", SettingValue);
				 
				 if (SettingValue.matches("0")) {
					 
					 SalesOrderSetGet.setEnablecustomercode("0");
					 
				 } else if (SettingValue.matches("1")) {
					 SalesOrderSetGet.setEnablecustomercode("1");
				 }else{
					 SalesOrderSetGet.setEnablecustomercode("1");
				 }
			 }
			 
			 if(SettingID.matches("MOBILEOVERDUEALERT")){
			     SettingValue = jsonChildNode.optString("SettingValue").toString();
			     
			     Log.d("MOBILEOVERDUEALERT", SettingValue);
			     
			     if (SettingValue.matches("0")) {
			      
			      SalesOrderSetGet.setMobileoverduealert("0");
			      
			     } else if (SettingValue.matches("1")) {
			      SalesOrderSetGet.setMobileoverduealert("1");
			     }else{
			      SalesOrderSetGet.setMobileoverduealert("0");
			     }
			    }
			 
			 if (SettingID.matches("TRANSFERCHANGEFROMLOC")) {
					String settingValue = jsonChildNode.optString(
							"SettingValue").toString();

					if (settingValue.matches("1")) {
						SalesOrderSetGet.setTransferchangefromloc("1");
					} else if (settingValue.matches("0")) {
						SalesOrderSetGet.setTransferchangefromloc("0");
					} else {
						SalesOrderSetGet.setTransferchangefromloc("0");
					}
				}

//				if (SettingID.matches("DEFAUTSHOWCARTONORLOOSE")) {
			/* if (SettingID.matches("CARTONPRICE")) {
					String settingValue = jsonChildNode.optString(
							"SettingValue").toString();

					if (settingValue.matches("1")) {
						SalesOrderSetGet.setCartonpriceflag("1");
					} else if (settingValue.matches("0")) {
						SalesOrderSetGet.setCartonpriceflag("0");
					} else {
						SalesOrderSetGet.setCartonpriceflag("");
					}
				}*/
				
				 if (SettingID.matches("TRANBLOCKNEGATIVESTOCK")) {
						String settingValue = jsonChildNode.optString(
								"SettingValue").toString();

						if (settingValue.matches("1")) {
							SalesOrderSetGet.setTranblocknegativestock("1");
						} else if (settingValue.matches("0")) {
							SalesOrderSetGet.setTranblocknegativestock("0");
						} else {
							SalesOrderSetGet.setTranblocknegativestock("0");
						}
					}
				 
				 if (SettingID.matches("TRANBLOCKINVOICEABOVELIMIT")) {
						String settingValue = jsonChildNode.optString(
								"SettingValue").toString();

						if (settingValue.matches("1")) {
							SalesOrderSetGet.setTranblockinvoiceabovelimit("1");
						} else if (settingValue.matches("0")) {
							SalesOrderSetGet.setTranblockinvoiceabovelimit("0");
						} else {
							SalesOrderSetGet.setTranblockinvoiceabovelimit("0");
						}
					}
	 
				 if (SettingID.matches("HAVEMULTIPLECUSTOMERPRICE")) {
			           SettingValue = jsonChildNode.optString("SettingValue").toString();
			           if (SettingValue.matches("1")) {
			            SalesOrderSetGet.setHaveMultipleCustomerPrice("1");
			           } else if (SettingValue.matches("0")) {
			           SalesOrderSetGet.setHaveMultipleCustomerPrice("0");
			     //      SalesOrderSetGet.setSchedulingType("DO");
			           } else {
			            SalesOrderSetGet.setHaveMultipleCustomerPrice("0");
			           }

			          }

			if (SettingID.matches("MALAYSIASHOWGST")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("1")) {
					SalesOrderSetGet.setMalaysiaShowGST("1");
				} else if (SettingValue.matches("0")) {
					SalesOrderSetGet.setMalaysiaShowGST("0");
				} else {
					SalesOrderSetGet.setMalaysiaShowGST("0");
				}
			}

			if (SettingID.matches("TRAN_BLOCK_TERMS")) {
				SettingValue =jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("1")) {
					SalesOrderSetGet.setTranBlockTerms("1");
				} else if (SettingValue.matches("0")) {
					SalesOrderSetGet.setTranBlockTerms("0");
				} else {
					SalesOrderSetGet.setTranBlockTerms("0");
				}
			}

			if (SettingID.matches("TRAN_BLOCK_CREDITLIMIT")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("1")) {
					SalesOrderSetGet.setTranBlockCreditLimit("1");
				} else if (SettingValue.matches("0")) {
					SalesOrderSetGet.setTranBlockCreditLimit("0");
				} else {
					SalesOrderSetGet.setTranBlockCreditLimit("0");
				}
			}

			if (SettingID.matches("HAVEEMAILINTEGRATION")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("1")) {
					SalesOrderSetGet.setHaveemailintegration("1");
				} else if (SettingValue.matches("0")) {
					SalesOrderSetGet.setHaveemailintegration("0");
				} else {
					SalesOrderSetGet.setHaveemailintegration("0");
				}
			}

			if (SettingID.matches("HAVEATTRIBUTE")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("2")) {
					SalesOrderSetGet.setHaveAttribute("2");
				} else if (SettingValue.matches("1")) {
					SalesOrderSetGet.setHaveAttribute("1");
				} else {
					SalesOrderSetGet.setHaveAttribute("1");
				}
			}

			if (SettingID.matches("SHOW_UNITCOST_STOCKTAKE")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setShowUnitCostStockTake("0");
				} else if (SettingValue.matches("1")) {
					SalesOrderSetGet.setShowUnitCostStockTake("1");
				} else {
					SalesOrderSetGet.setShowUnitCostStockTake("0");
				}
			}
			if (SettingID.matches("SELFORDERSHOWADDTOCART")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setShowAddToCart("0");
				} else if (SettingValue.matches("1")) {
					SalesOrderSetGet.setShowAddToCart("1");
				} else {
					SalesOrderSetGet.setShowAddToCart("0");
				}
			}
			if (SettingID.matches("MOBILE_SHOW_CODE_ONSEARCH")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setMobileShowCodeOnSearch("0");
				} else if (SettingValue.matches("1")) {
					SalesOrderSetGet.setMobileShowCodeOnSearch("1");
				} else {
					SalesOrderSetGet.setMobileShowCodeOnSearch("0");
				}
			}
			if (SettingID.matches("SELFORDER_SHOW_PRODUCTCODE")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setSelfOrderShowProductCode("0");
				} else if (SettingValue.matches("1")) {
					SalesOrderSetGet.setSelfOrderShowProductCode("1");
				} else {
					SalesOrderSetGet.setSelfOrderShowProductCode("0");
				}
			}

			if (SettingID.matches("MOBILE_HAVE_OFFLINEMODE")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setMobileHaveOfflineMode("0");
				} else if (SettingValue.matches("1")) {
					SalesOrderSetGet.setMobileHaveOfflineMode("1");
				} else {
					SalesOrderSetGet.setMobileHaveOfflineMode("0");
				}
//				SalesOrderSetGet.setMobileHaveOfflineMode("0");
			}

			if (SettingID.matches("RECEIPT_AUTO_CREDIT_AMOUNT")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setReceiptAutoCreditAmount("0");
				} else {
					SalesOrderSetGet.setReceiptAutoCreditAmount(SettingValue);
				}
			}

			if (SettingID.matches("HOSTING_VALIDATION")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setHostingValidation("0");
				} else {
					SalesOrderSetGet.setHostingValidation(SettingValue);
				}
			}

			if (SettingID.matches("CUSTOMER_HAVE_CASHBILL")) {
				SettingValue = jsonChildNode.optString("SettingValue").toString();
				if (SettingValue.matches("0")) {
					SalesOrderSetGet.setCustomerHaveCashbill("0");
				} else {
					SalesOrderSetGet.setCustomerHaveCashbill(SettingValue);
				}
			}
		}
		} catch (Exception e) {
			gnrlStngs = "error";
			if(exception == null){
				exception = e;
			}
		}
		
		return gnrlStngs;
	}
	
	//Mobile Setting
	
	public void mobileSettings(String url,String webMethName) {
		
		Log.d("mbl stg url", "mmm"+url);
		
		String value="", result="";
		
		OfflineSettingsManager offlinemanager = new OfflineSettingsManager(mActivity);
		String comapanyCode = offlinemanager.getCompanyType();
		
		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		PropertyInfo companycode = new PropertyInfo();
			
		companycode.setName("CompanyCode");
		companycode.setValue(comapanyCode);
		companycode.setType(String.class);
		request.addProperty(companycode);
		try {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
		

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			value = response.toString();
			Log.d("MobileSettings", value);
			result = " { MobileSettings : " + value + "}";
			JSONObject jsonResponse;
			jsonResponse = new JSONObject(result);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("MobileSettings");
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				
				String ShowLogo = jsonChildNode.optString("ShowLogo")
						.toString();
				String ShowAddress1 = jsonChildNode.optString("ShowAddress1")
						.toString();
				String ShowAddress2 = jsonChildNode.optString("ShowAddress2")
						.toString();
				String ShowAddress3 = jsonChildNode.optString("ShowAddress3")
						.toString();
				String ShowCountryPostal = jsonChildNode.optString("ShowCountryPostal")
						.toString();
				String ShowPhone = jsonChildNode.optString("ShowPhone")
						.toString();
				String ShowFax = jsonChildNode.optString("ShowFax")
						.toString();
				String ShowEmail = jsonChildNode.optString("ShowEmail")
						.toString();
				String ShowTaxRegNo = jsonChildNode.optString("ShowTaxRegNo")
						.toString();
				String ShowBizRegNo = jsonChildNode.optString("ShowBizRegNo")
						.toString();				
				String ShowCustomerCode = jsonChildNode.optString("ShowCustomerCode")
						.toString();
				String ShowCustomerName = jsonChildNode.optString("ShowCustomerName")
						.toString();
				String ShowCustomerAddress1 = jsonChildNode.optString("ShowCustomerAddress1")
						.toString();
				String ShowCustomerAddress2 = jsonChildNode.optString("ShowCustomerAddress2")
						.toString();
				String ShowCustomerAddress3 = jsonChildNode.optString("ShowCustomerAddress3")
						.toString();
				String ShowCustomerPhone = jsonChildNode.optString("ShowCustomerPhone")
						.toString();
				String ShowCustomerHP = jsonChildNode.optString("ShowCustomerHP")
						.toString();
				String ShowCustomerEmail = jsonChildNode.optString("ShowCustomerEmail")
						.toString();
				String ShowCustomerTerms = jsonChildNode.optString("ShowCustomerTerms")
						.toString();
				String ShowProductFullName = jsonChildNode.optString("ShowProductFullName")
						.toString();
				String ShowTotalOutstanding = jsonChildNode.optString("ShowTotalOutstanding")
						.toString();
				String ShowFooter = jsonChildNode.optString("ShowFooter")
						.toString();
				String ShowBatchDetails = jsonChildNode.optString("ShowBatchDetails")
						.toString();
				String CompanyNameAlias= jsonChildNode.optString("CompanyNameAlias").toString();

				String fixPrecision = "3";

				Log.d("ShowTaxRegNo","-->"+ShowTaxRegNo);
										
				MobileSettingsSetterGetter.setShowLogo(ShowLogo);
				MobileSettingsSetterGetter.setShowAddress1(ShowAddress1);
				MobileSettingsSetterGetter.setShowAddress2(ShowAddress2);
				MobileSettingsSetterGetter.setShowAddress3(ShowAddress3);
				MobileSettingsSetterGetter.setShowCountryPostal(ShowCountryPostal);
				MobileSettingsSetterGetter.setShowPhone(ShowPhone);
				MobileSettingsSetterGetter.setShowFax(ShowFax);
				MobileSettingsSetterGetter.setShowEmail(ShowEmail);
				MobileSettingsSetterGetter.setShowTaxRegNo(ShowTaxRegNo);
				MobileSettingsSetterGetter.setShowBizRegNo(ShowBizRegNo);
				MobileSettingsSetterGetter.setShowCustomerCode(ShowCustomerCode);
				MobileSettingsSetterGetter.setShowCustomerName(ShowCustomerName);
				MobileSettingsSetterGetter.setShowCustomerAddress1(ShowCustomerAddress1);
				MobileSettingsSetterGetter.setShowCustomerAddress2(ShowCustomerAddress2);
				MobileSettingsSetterGetter.setShowCustomerAddress3(ShowCustomerAddress3);
				MobileSettingsSetterGetter.setShowCustomerPhone(ShowCustomerPhone);
				MobileSettingsSetterGetter.setShowCustomerHP(ShowCustomerHP);
				MobileSettingsSetterGetter.setShowCustomerEmail(ShowCustomerEmail);
				MobileSettingsSetterGetter.setShowCustomerTerms(ShowCustomerTerms);
				MobileSettingsSetterGetter.setShowProductFullName(ShowProductFullName);
				MobileSettingsSetterGetter.setShowTotalOutstanding(ShowTotalOutstanding);
				MobileSettingsSetterGetter.setShowFooter(ShowFooter);
				MobileSettingsSetterGetter.setShowBatchDetails(ShowBatchDetails);
				MobileSettingsSetterGetter.setCompanyNameAlias(CompanyNameAlias);
				MobileSettingsSetterGetter.setDecimalPoints(fixPrecision);
				
				Log.d("MobileSettings ", ShowLogo +" " +ShowAddress1);

			}

		} catch (Exception e) {
			if(exception == null){
				exception = e;
			}
		}

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

}
