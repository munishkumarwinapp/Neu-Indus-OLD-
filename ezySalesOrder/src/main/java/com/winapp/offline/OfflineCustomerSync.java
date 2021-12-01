package com.winapp.offline;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;

import android.app.Activity;
import android.util.Log;

import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LoginActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.offline.SoapAccessTask.CallbackInterface;
import com.winapp.sot.WebServiceClass;

public class OfflineCustomerSync {
	private OfflineHelper helper;
	private OfflineDatabase offlineDatabase;
	private Activity  activity;
	int lengthJsonArr1;
	String valid_url="";
	public OfflineCustomerSync(){
	}
	
	public OfflineCustomerSync(Activity activity){
		this.activity = activity;
		this.helper = new OfflineHelper(activity);
		this.offlineDatabase = new OfflineDatabase(activity);
		FWMSSettingsDatabase.init(activity);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,activity);
	}
	
	public void synchCustomerData() {

		String jsonString, dateString;
		JSONObject jsonResponse;
		JSONArray jsonArray = null;
		
		OfflineSettingsManager offlinemanager = new OfflineSettingsManager(activity);
		String comapanyCode = offlinemanager.getCompanyType();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		String downloadDate = OfflineDatabase.getServerDate();
		Log.d("cmpnyCode :" + cmpnyCode, "customer :" + downloadDate);
		HashMap<String, String> customerValue = new HashMap<String, String>();
		customerValue.put("CompanyCode", cmpnyCode);
		customerValue.put("ModifyDate", downloadDate);
		
		jsonString = WebServiceClass.parameterService(customerValue,"fncGetCustomer");
		dateString = DateWebservice.getDateService("fncGetServerDateTime");

		try {

			jsonResponse = new JSONObject(jsonString);
			jsonArray = jsonResponse.optJSONArray("JsonArray");
			
			OfflineDatabase.store_customer(comapanyCode, dateString, jsonArray);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			helper.dismissProgressDialog();
			helper.showErrorDialog(Log.getStackTraceString(e));
		}

		OfflineDatabase.store_datetime(dateString, comapanyCode);	

		if(activity instanceof LoginActivity){
			
			if(lengthJsonArr1>0){
				helper.showLongToast("Data Synchronized");
			}
				
		}
		
	}
	
	public void synchUsermaster(final String webserviceUrl) {

		new Thread() {
			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						offlineDatabase.dropUserMaster();
						
						ArrayList<PropertyInfo> params = new ArrayList<PropertyInfo>();
						params.clear();
						params.add(newPropertyInfo("Code", "W!napp@!@#^"));
						
						new SoapAccessTask(activity, webserviceUrl,
								"fncGetUserMaster", params, new GetUserMaster())
								.execute();
					}
				});
			}
		}.start();
	}

	public class GetUserMaster implements CallbackInterface {
		@Override
		public void onSuccess(JSONArray jsonArray) {
			try {
				OfflineDatabase.store_usermaster(jsonArray);
				
			} catch (Exception e) {
				e.printStackTrace();
				helper.dismissProgressDialog();
				helper.showErrorDialog(Log.getStackTraceString(e));
			}
		}
		@Override
		public void onFailure(Exception error) {
			onError(error);
		}
	}
	
	
	public void synchcompany(final String webserviceUrl) {

		new Thread() {
			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
 
						new SoapAccessTask(activity, webserviceUrl,
								"fncGetCompany", null, new GetCompany())
								.execute();
					}
				});
			}
		}.start();
	}

	public class GetCompany implements CallbackInterface {
		@Override
		public void onSuccess(JSONArray jsonArray) {
			try {
				OfflineDatabase.store_company(jsonArray);
				
			} catch (Exception e) {
				e.printStackTrace();
				helper.dismissProgressDialog();
				helper.showErrorDialog(Log.getStackTraceString(e));
			}
		}

		@Override
		public void onFailure(Exception error) {
			onError(error);
		}
	}
	
	/** Server date  **//*
	public void synchdatetime(final String webserviceUrl) {

		new Thread() {
			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						OfflineSettingsManager offlinemanager = new OfflineSettingsManager(activity);
						String comapanyCode = offlinemanager.getCompanyType();
						
						ArrayList<PropertyInfo> params = new ArrayList<PropertyInfo>();
						params.clear();
						params.add(newPropertyInfo("CompanyCode", comapanyCode));
 
						new SoapAccessTask(activity, webserviceUrl,
								"fncGetServerDateTime", params, new GetServerDateTime(comapanyCode))
								.execute();
					}
				});
			}
		}.start();
	}

	public class GetServerDateTime implements CallbackInterface {
		String companycode = "";

		public GetServerDateTime(String companyCode) {
			this.companycode = companyCode;
		}
		
		@Override
		public void onSuccess(JSONArray jsonArray) {
		
			Log.d("ServerDateTime Download", "... "+companycode);
			try {
				int len = jsonArray.length();
				
				Log.d("ServerDate", "ServerDate");
				for (int i = 0; i < len; i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					OfflineDatabase.store_datetime(object.getString("ServerDate"), companycode);
				}
				
		
			} catch (Exception e) {
				e.printStackTrace();
				helper.dismissProgressDialog();
				helper.showErrorDialog(Log.getStackTraceString(e));
			}
		}

		@Override
		public void onFailure(Exception error) {
			onError(error);
		}
	}
	
	*//** general setting  **//*
	
	public void synchgeneralsettings(final String webserviceUrl) {

		new Thread() {
			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						OfflineSettingsManager offlinemanager = new OfflineSettingsManager(activity);
						String comapanyCode = offlinemanager.getCompanyType();
						
						ArrayList<PropertyInfo> params = new ArrayList<PropertyInfo>();
						params.clear();
						params.add(newPropertyInfo("CompanyCode", comapanyCode));
						new SoapAccessTask(activity, webserviceUrl,"fncGetGeneralSettings", params, new GetGeneralSettings(comapanyCode)).execute();
					}
				});
			}
		}.start();
	}
	
	public class GetGeneralSettings implements CallbackInterface {
		
		String companycode = "";

		public GetGeneralSettings(String companyCode) {
			this.companycode = companyCode;
		}
		
		@Override
		public void onSuccess(JSONArray jsonArray) {
			Log.d("GetGeneralSettings Download", "... "+companycode);
			try {
				OfflineDatabase.store_generalsetting(companycode, jsonArray);
				
			} catch (Exception e) {
				e.printStackTrace();
				helper.dismissProgressDialog();
				helper.showErrorDialog(Log.getStackTraceString(e));
			}
		}

		@Override
		public void onFailure(Exception error) {
			onError(error);
		}
	}*/
	
	public void synchNextRunningNo(final String webserviceUrl) {

		new Thread() {
			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						Log.d("synchNextRunningNo", "synchNextRunningNo");
						String deviceId = OfflineDatabase.getDeviceId();					 
						ArrayList<PropertyInfo> params = new ArrayList<PropertyInfo>();
						params.clear();
						params.add(newPropertyInfo("DeviceID", deviceId));
						new SoapAccessTask(activity, webserviceUrl,
								"fncGetNextRunningNumber", params, new GetNextRunningNo())
								.execute();
					}
				});
			}
		}.start();
	}

	public class GetNextRunningNo implements CallbackInterface {
		@Override
		public void onSuccess(JSONArray jsonArray) {
			try {
				Log.d("synchNextRunningNo", "GetNextRunningNo");
				OfflineDatabase.store_NextRunningNumber(jsonArray);	
			} catch (Exception e) {
				e.printStackTrace();
				helper.dismissProgressDialog();
				helper.showErrorDialog(Log.getStackTraceString(e));
			}
		}

		@Override
		public void onFailure(Exception error) {
			onError(error);
		}
	}
	
	public static PropertyInfo newPropertyInfo(String name, String value) {
		PropertyInfo propertyInfo = new PropertyInfo();
		propertyInfo.setName(name);
		propertyInfo.setValue(value);
		return propertyInfo;
	}
	
	public void onError(final Exception error) {
		new Thread() {
			@Override
			public void run() {

				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						helper.dismissProgressDialog();
						/*if (error == ErrorType.NETWORK_UNAVAILABLE) {
							helper.showLongToast(R.string.error_downloading_data_no_network_connection);
						} else {
							helper.showLongToast(R.string.error_downloading_data);
						}*/
						helper.showErrorDialog(Log.getStackTraceString(error));
//						activity.finish();
					}
				});
			}
		}.start();
	}

}
