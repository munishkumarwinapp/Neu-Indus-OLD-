package com.winapp.offline;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.winapp.SFA.R;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LoginActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.sot.WebServiceClass;

public class OfflineDataSynch {
	private OfflineHelper helper;
	private Activity  activity;
	int lengthJsonArr1;
	String valid_url="";
	public OfflineDataSynch(){
	}
	
	public OfflineDataSynch(Activity activity){
		this.activity = activity;
		this.helper = new OfflineHelper(activity);
		FWMSSettingsDatabase.init(activity);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,activity);
	}
	
	
	public void startSync(final Activity activity) {
	
//		helper.showProgressDialog(R.string.upload_customer);
//		new Thread() {
//			@Override
//			public void run() {
//				// offlinedb.truncateTables();
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
		
		if(activity instanceof LoginActivity){
						
		}else{
			AsyncCallUpSync sync = new AsyncCallUpSync();
			sync.execute();
		}
						
//					}
//				});
//			}
//		}.start();
	}
	

	private class AsyncCallUpSync extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			
			helper.showProgressDialog(R.string.sync_data);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			try {
				 synchCustomerData();
				
			} catch (Exception e) {
				e.printStackTrace();
				helper.dismissProgressDialog();
				helper.showErrorDialog(Log.getStackTraceString(e));
			}
				
			return null;
		}
 
		@Override
		protected void onPostExecute(Void result) {
			
			if(activity instanceof LoginActivity){
				
				if(lengthJsonArr1>0){
					helper.showLongToast("Data Synchronized");
				}
					
			}else{
				if(lengthJsonArr1>0){
					helper.showLongToast("Data Synchronized");
				}else{
					helper.showLongToast("Data not synchronized");
				}
			}
			helper.dismissProgressDialog();
		}
			
	}
	
	
	public void synchCustomerData() {

		String dateString = "", jsonString = "", invHeaderjsonString = "", soHeaderjsonString ="", getproductjsonString="";
		JSONObject jsonResponse;
		JSONArray jsonArray = null;

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		String downloadDate = OfflineDatabase.getServerDate();
		
		OfflineSettingsManager offlinemanager = new OfflineSettingsManager(activity);
		String comapanyCode = offlinemanager.getCompanyType();
		
		Log.d("cmpnyCode :" + cmpnyCode, "customer :" + downloadDate);
		
		HashMap<String, String> customerValue = new HashMap<String, String>();
		customerValue.put("CompanyCode", cmpnyCode);
		customerValue.put("ModifyDate", downloadDate);
		
		HashMap<String, String> invoiceValue = new HashMap<String, String>();
		invoiceValue.put("CompanyCode", cmpnyCode);
		invoiceValue.put("BalanceFlag", "0");
		
		HashMap<String, String> soValue = new HashMap<String, String>();
		soValue.put("CompanyCode", cmpnyCode);
		
		HashMap<String, String> getproduct = new HashMap<String, String>();
		getproduct.put("CompanyCode", cmpnyCode);
//		invoiceValue.put("ModifyDate", "");
		
		dateString = DateWebservice.getDateService("fncGetServerDateTime");
		jsonString = WebServiceClass.parameterService(customerValue,"fncGetCustomer");
		invHeaderjsonString = WebServiceClass.parameterService(invoiceValue,"fncGetInvoiceHeader");
		soHeaderjsonString = WebServiceClass.parameterService(invoiceValue,"fncGetSOHeader");
		
		getproductjsonString = WebServiceClass.parameterService(soValue,"fncGetProduct");
		

		try {
						
			//Synch Customer
			try {
				jsonResponse = null;
				jsonArray = null;
				jsonResponse = new JSONObject(jsonString);
				jsonArray = jsonResponse.optJSONArray("JsonArray");
				
				OfflineDatabase.store_customer(comapanyCode, dateString, jsonArray);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			//Synch Invoice Header
			try {
				jsonResponse = null;
				jsonArray = null;
				jsonResponse = new JSONObject(invHeaderjsonString);
				jsonArray = jsonResponse.optJSONArray("JsonArray");
				
				OfflineDatabase.store_invoiceheader(comapanyCode,dateString,jsonArray);

			} catch (JSONException e) {
				e.printStackTrace();
				helper.dismissProgressDialog();
				helper.showErrorDialog(Log.getStackTraceString(e));
			}
			
			//Synch Invoice Header
			try {
				jsonResponse = null;
				jsonArray = null;
				jsonResponse = new JSONObject(soHeaderjsonString);
				jsonArray = jsonResponse.optJSONArray("JsonArray");
				
				OfflineDatabase.store_soheader(comapanyCode,dateString,jsonArray);

			} catch (JSONException e) {
				e.printStackTrace();
				helper.dismissProgressDialog();
				helper.showErrorDialog(Log.getStackTraceString(e));
			}
			
			//Synch getproduct
			try {
				
				jsonResponse = null;
				jsonArray = null;
				jsonResponse = new JSONObject(getproductjsonString);
				jsonArray = jsonResponse.optJSONArray("JsonArray");

				OfflineDatabase.store_product(comapanyCode,dateString,jsonArray);
			
			} catch (JSONException e) {
				e.printStackTrace();
				helper.dismissProgressDialog();
				helper.showErrorDialog(Log.getStackTraceString(e));
			}

			//Synch Date
			OfflineDatabase.store_datetime(dateString, comapanyCode);	

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			helper.dismissProgressDialog();
			helper.showErrorDialog(Log.getStackTraceString(e));
		}

		if(activity instanceof LoginActivity){
			
			OfflineCustomerSync custoffline = new OfflineCustomerSync();			
			custoffline.synchCustomerData();
				
		}
		
	}

}
