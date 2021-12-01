package com.winapp.sot;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;

public class ReceiptDetails extends Activity {

	LinearLayout customer_detail_layout;
	ImageView back;
	TextView page_Title;
	EditText receipt_detail_no, receipt_detail_invno, receipt_detail_nettotal,
			receipt_detail_paidamt, receipt_detail_crdamt;
	ArrayList<HashMap<String, String>> DetailsListArray = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> details_hashValue;
	String details_jsonString = null;
	JSONObject details_jsonResponse;
	JSONArray details_jsonMainNode;
	String val_receipt_no = "", valid_url = "";
	String ReceiptNo = "", InvoiceNo = "", NetAmount = "", PaidAmount = "",
			CreditAmount = "";
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	
	//Offline 
    LinearLayout offlineLayout;
    private OfflineDatabase offlineDatabase;
    boolean checkOffline;
	String onlineMode, offlineDialogStatus,mobileHaveOfflineMode="";
	private OfflineCommon offlineCommon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.receipt_details);

		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		// @Offline
		onlineMode = OfflineDatabase.getOnlineMode();
		offlineDatabase = new OfflineDatabase(ReceiptDetails.this);
		offlineCommon = new OfflineCommon(ReceiptDetails.this);
		checkOffline = OfflineCommon.isConnected(ReceiptDetails.this);
		OfflineDatabase.init(ReceiptDetails.this);
		offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);
		
		customer_detail_layout = (LinearLayout) findViewById(R.id.receipt_detail_layout);
		page_Title = (TextView) findViewById(R.id.page_Title);

		receipt_detail_no = (EditText) findViewById(R.id.receipt_detail_no_ed);
		receipt_detail_invno = (EditText) findViewById(R.id.receipt_detail_invno_ed);
		receipt_detail_nettotal = (EditText) findViewById(R.id.receipt_detail_nettotal_ed);
		receipt_detail_paidamt = (EditText) findViewById(R.id.receipt_detail_paidamt_ed);
		receipt_detail_crdamt = (EditText) findViewById(R.id.receipt_detail_crdamt_ed);

		FWMSSettingsDatabase.init(ReceiptDetails.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new WebServiceClass(valid_url);

		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Log.e("extras", "Extra NULL");
		} else {
			val_receipt_no = extras.getString("val_receipt_no");
		}

		CustomersyncCall customerservice = new CustomersyncCall();
		customerservice.execute();

		back = (ImageView) findViewById(R.id.back);

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ReceiptDetails.this, ReceiptHeader.class);
				startActivity(i);
				ReceiptDetails.this.finish();
			}
		});

	}

	private void enableViews(View v, boolean enabled) {
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				enableViews(vg.getChildAt(i), enabled);
			}
		}
		v.setEnabled(enabled);
	}

	private class CustomersyncCall extends AsyncTask<Void, Void, Void> {
		String dialogStatus;
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			spinnerLayout = new LinearLayout(ReceiptDetails.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(customer_detail_layout, false);
			progressBar = new ProgressBar(ReceiptDetails.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
			
			dialogStatus = checkInternetStatus();
			details_jsonString = "";
			details_jsonResponse = null;
			details_jsonMainNode = null;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			details_hashValue = new HashMap<String, String>();
			details_hashValue.put("CompanyCode", cmpnyCode);
			details_hashValue.put("ReceiptNo", val_receipt_no);
			try {
				
			if (onlineMode.matches("True")) {
				if (checkOffline == true) {     // Temp offline

					if (dialogStatus.matches("true")) {
						details_jsonString = offlineDatabase.getReceiptDetail(details_hashValue);
						details_jsonResponse = new JSONObject(details_jsonString);
						details_jsonMainNode = details_jsonResponse.optJSONArray("SODetails");
					} else {
						Log.d("CheckOffline Alert -->", "False");
					//	finish();
						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					}

				} else {   //Online
					details_jsonString = WebServiceClass.parameterService(details_hashValue, "fncGetReceiptDetail");
					details_jsonResponse = new JSONObject(details_jsonString);
					details_jsonMainNode = details_jsonResponse.optJSONArray("JsonArray");
				}

			} else if (onlineMode.matches("False")) {   // permanant offline
				details_jsonString = offlineDatabase.getReceiptDetail(details_hashValue);
				details_jsonResponse = new JSONObject(details_jsonString);
				details_jsonMainNode = details_jsonResponse.optJSONArray("SODetails");
			}


			/*********** Process each JSON Node ************/
			int lengthJsonArr = details_jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode;
				try {

					jsonChildNode = details_jsonMainNode.getJSONObject(i);

					ReceiptNo = jsonChildNode.optString("ReceiptNo").toString();
					InvoiceNo = jsonChildNode.optString("InvoiceNo").toString();
					NetAmount = jsonChildNode.optString("NetTotal").toString();
					PaidAmount = jsonChildNode.optString("PaidAmount").toString();
					CreditAmount = jsonChildNode.optString("CreditAmount").toString();

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			
			} catch (JSONException e) {
				e.printStackTrace();
			}


			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			receipt_detail_no.setText(ReceiptNo);
			receipt_detail_invno.setText(InvoiceNo);
			receipt_detail_nettotal.setText(NetAmount);
			receipt_detail_paidamt.setText(PaidAmount);
			receipt_detail_crdamt.setText(CreditAmount);

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(customer_detail_layout, true);
		}
	}
	
	
	//offline
		private String checkInternetStatus() {
			String internetStatus = "";
			String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
			if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
				if(mobileHaveOfflineMode.matches("1")){
			checkOffline = OfflineCommon.isConnected(ReceiptDetails.this);
//			String internetStatus = "";
			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					String Off_dialog = OfflineDatabase
							.getInternetMode("OfflineDialog");
					if (Off_dialog.matches("true")) {
						internetStatus = "true";
					} else {
						offlineCommon.OfflineAlertDialog();
						Boolean dialogStatus = offlineCommon.showDialog();
						OfflineDatabase.updateInternetMode("OfflineDialog",
								dialogStatus + "");
						Log.d("Offline DialogStatus", "" + dialogStatus);
						internetStatus = "" + dialogStatus;
					}
				} else if (checkOffline == false) {
					String on_dialog = OfflineDatabase
							.getInternetMode("OnlineDialog");
					if (on_dialog.matches("true")) {
						internetStatus = "true";
					} else {
						offlineCommon.onlineAlertDialog();
						boolean dialogStatus = offlineCommon.showDialog();
						OfflineDatabase.updateInternetMode("OnlineDialog",
								dialogStatus + "");
						Log.d("Online DialogStatus", "" + dialogStatus);
						internetStatus = "" + dialogStatus;
					}
				}
			}
			onlineMode = OfflineDatabase.getOnlineMode();
			if (onlineMode.matches("True")) {
				offlineLayout.setVisibility(View.GONE);
				if (checkOffline == true) {
					if (internetStatus.matches("true")) {
					offlineLayout.setVisibility(View.VISIBLE);
				    offlineLayout.setBackgroundResource(drawable.temp_offline_pattern_bg);
					}
				}
				
			} else if (onlineMode.matches("False")) {
				offlineLayout.setVisibility(View.VISIBLE);
			}
			
			String deviceId = RowItem.getDeviceID();
			
			Log.d("device id", "dev "+ deviceId);
				}else{
					internetStatus = "false";
				}
			}else{
				internetStatus = "false";
			}
			return internetStatus;
		}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(ReceiptDetails.this, ReceiptHeader.class);
		startActivity(i);
		ReceiptDetails.this.finish();
	}
}
