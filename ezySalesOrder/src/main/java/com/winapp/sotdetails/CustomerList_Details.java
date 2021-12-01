package com.winapp.sotdetails;/*package com.winapp.sotdetails;

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
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.sot.WebServiceClass;

public class CustomerList_Details extends Activity {

	LinearLayout offlineLayout;
	LinearLayout customer_detail_layout;
	ImageView back;
	TextView page_Title;
	EditText cust_detail_code, cust_detail_name, cust_detail_person,
			cust_detail_add1, cust_detail_add2, cust_detail_add3,
			cust_detail_phn, cust_detail_handphone, cust_detail_email,
			cust_detail_tax, cust_detail_grpcode, cust_detail_termcode,
			cust_detail_creditlimit, cust_detail_outstanding,
			cust_detail_creditbalance, cust_detail_areacode;

	ArrayList<HashMap<String, String>> CustomerListArray = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> customer_hashValue = new HashMap<String, String>();
	HashMap<String, String> areacode_hashValue = new HashMap<String, String>();
	String customer_jsonString = null, areacode_jsonString = null;
	JSONObject customer_jsonResponse, areacode_jsonResponse;
	JSONArray customer_jsonMainNode, areacode_jsonMainNode;
	// String customer_jsonString = null;
	// JSONObject customer_jsonResponse;
	// JSONArray customer_jsonMainNode;
	String CustCode = "", valid_url = "";
	String CustomerCode = "", CustomerName = "", ContactPerson = "",
			Address1 = "", Address2 = "", Address3 = "", PhoneNo = "",
			HandphoneNo = "", Email = "", HaveTax = "", CustomerGroupCode = "",
			TermCode = "", CreditLimit = "", OutstandingAmount = "",
			AreaCode = "", AreaName = "";
	ProgressBar progressBar;
	LinearLayout spinnerLayout;

	// @Offline
	boolean checkOffline;
	String onlineMode, offlineDialogStatus;
	private OfflineSettingsManager offlinemanager;
	private OfflineCommon offlineCommon;
	private OfflineDatabase offlineDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.customer_details);

		// @Offline
		offlineDatabase = new OfflineDatabase(this);
		offlineCommon = new OfflineCommon(this);
		checkOffline = OfflineCommon.isConnected(this);
		Log.d("Customer checkOffline ", "-->" + checkOffline);
		OfflineDatabase.init(CustomerList_Details.this);

		offlineLayout = (LinearLayout) findViewById(R.id.offlineLayout);
		customer_detail_layout = (LinearLayout) findViewById(R.id.customer_detail_layout);
		page_Title = (TextView) findViewById(R.id.page_Title);

		cust_detail_code = (EditText) findViewById(R.id.cust_detail_code_ed);
		cust_detail_name = (EditText) findViewById(R.id.cust_detail_name_ed);
		cust_detail_person = (EditText) findViewById(R.id.cust_detail_person_ed);
		cust_detail_add1 = (EditText) findViewById(R.id.cust_detail_add1_ed);
		cust_detail_add2 = (EditText) findViewById(R.id.cust_detail_add2_ed);
		cust_detail_add3 = (EditText) findViewById(R.id.cust_detail_add3_ed);
		cust_detail_phn = (EditText) findViewById(R.id.cust_detail_phn_ed);
		cust_detail_handphone = (EditText) findViewById(R.id.cust_detail_handphn_ed);
		cust_detail_email = (EditText) findViewById(R.id.cust_detail_email_ed);
		cust_detail_tax = (EditText) findViewById(R.id.cust_detail_tax_ed);
		cust_detail_grpcode = (EditText) findViewById(R.id.cust_detail_grpcode_ed);
		cust_detail_termcode = (EditText) findViewById(R.id.cust_detail_termcode_ed);
		cust_detail_creditlimit = (EditText) findViewById(R.id.cust_detail_creditlimit_ed);
		cust_detail_areacode = (EditText) findViewById(R.id.cust_detail_areacode_ed);
		cust_detail_outstanding = (EditText) findViewById(R.id.cust_detail_totaloutstanding_ed);
		cust_detail_creditbalance = (EditText) findViewById(R.id.cust_detail_creditbalance_ed);
		
		onlineMode = OfflineDatabase.getOnlineMode();		
		if (onlineMode.matches("True")) {
			offlineLayout.setVisibility(View.GONE);
		} else if (onlineMode.matches("False")) {
			offlineLayout.setVisibility(View.VISIBLE);
		}

		FWMSSettingsDatabase.init(CustomerList_Details.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new WebServiceClass(valid_url);

		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Log.e("extras", "Extra NULL");
		} else {
			CustCode = extras.getString("valCustCode");
		}

		CustomersyncCall customerservice = new CustomersyncCall();
		customerservice.execute();

		back = (ImageView) findViewById(R.id.back);

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CustomerList_Details.this, CustomerListActivity.class);
				startActivity(i);
				CustomerList_Details.this.finish();
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
		@Override
		protected void onPreExecute() {

			spinnerLayout = new LinearLayout(CustomerList_Details.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(customer_detail_layout, false);
			progressBar = new ProgressBar(CustomerList_Details.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));

			spinnerLayout.addView(progressBar);
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			customer_hashValue.put("CompanyCode", cmpnyCode);
			customer_hashValue.put("CustomerCode", CustCode);
			customer_hashValue.put("NeedOutstandingAmount", "");
			customer_hashValue.put("AreaCode", "");
			customer_hashValue.put("VanCode", "");

			if (onlineMode.matches("False")) {
				Log.d("Offline", "-->" + "true");
				customer_jsonString = OfflineDatabase.getCustomersList(customer_hashValue);
			} else if (onlineMode.matches("True")) {
				Log.d("Login Online Mode", onlineMode);
				offlineLayout.setVisibility(View.GONE);
				if (checkOffline == true) {
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) {
						Log.d("CheckOffline Alert -->", "True");
						offlineLayout.setVisibility(View.VISIBLE);
					      offlineLayout.setBackgroundResource(R.drawable.temp_offline_pattern_bg);
						customer_jsonString = OfflineDatabase.getCustomersList(customer_hashValue);
					} else {
						Log.d("CheckOffline Alert -->", "False");
						finish();
					}

				} else {
					customer_jsonString = WebServiceClass.parameterService(customer_hashValue, "fncGetCustomer");
				}
			
			}

			try {

				customer_jsonResponse = new JSONObject(customer_jsonString);
				customer_jsonMainNode = customer_jsonResponse
						.optJSONArray("JsonArray");

			} catch (JSONException e) {

				e.printStackTrace();
			}

			*//*********** Process each JSON Node ************//*
			int lengthJsonArr = customer_jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				*//****** Get Object for each JSON node. ***********//*
				JSONObject jsonChildNode;
				try {

					jsonChildNode = customer_jsonMainNode.getJSONObject(i);

					CustomerCode = jsonChildNode.optString("CustomerCode")
							.toString();
					CustomerName = jsonChildNode.optString("CustomerName")
							.toString();
					ContactPerson = jsonChildNode.optString("ContactPerson")
							.toString();
					Address1 = jsonChildNode.optString("Address1").toString();
					Address2 = jsonChildNode.optString("Address2").toString();
					Address3 = jsonChildNode.optString("Address3").toString();
					PhoneNo = jsonChildNode.optString("PhoneNo").toString();
					HandphoneNo = jsonChildNode.optString("HandphoneNo")
							.toString();
					Email = jsonChildNode.optString("Email").toString();
					HaveTax = jsonChildNode.optString("HaveTax").toString();
					CustomerGroupCode = jsonChildNode.optString(
							"CustomerGroupCode").toString();
					TermCode = jsonChildNode.optString("TermCode").toString();

					AreaCode = jsonChildNode.optString("AreaCode").toString();
					CreditLimit = jsonChildNode.optString("CreditLimit")
							.toString();
					OutstandingAmount = jsonChildNode.optString(
							"OutstandingAmount").toString();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (!AreaCode.matches("")) {
				areacode_hashValue.put("CompanyCode", cmpnyCode);
				areacode_hashValue.put("AreaCode", AreaCode);
				
				if (onlineMode.matches("False")) {
					Log.d("Offline", "-->" + "true");
					areacode_jsonString = OfflineDatabase.getArea(AreaCode);
				} else {
					areacode_jsonString = WebServiceClass.parameterService(
							areacode_hashValue, "fncGetArea");
				}
				
//				areacode_jsonString = WebServiceClass.parameterService(
//						areacode_hashValue, "fncGetArea");

				try {
					areacode_jsonResponse = new JSONObject(areacode_jsonString);
					areacode_jsonMainNode = areacode_jsonResponse
							.optJSONArray("JsonArray");

				} catch (JSONException e) {

					e.printStackTrace();
				}
				*//***** Process each JSON Node ******//*
				int lengthJsonArr1 = areacode_jsonMainNode.length();
				for (int j = 0; j < lengthJsonArr1; j++) {

					JSONObject jsonChildNode;
					try {

						jsonChildNode = areacode_jsonMainNode.getJSONObject(j);

						AreaName = jsonChildNode.optString("Description")
								.toString();

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (CustomerGroupCode.matches("-1")
					|| CustomerGroupCode.matches("null")) {
				CustomerGroupCode = "";
			}
			if (TermCode.matches("-1") || TermCode.matches("null")) {
				TermCode = "";
			}
			page_Title.setText(CustomerName);
			cust_detail_code.setText(CustomerCode);
			cust_detail_name.setText(CustomerName);
			cust_detail_person.setText(ContactPerson);
			cust_detail_add1.setText(Address1);
			cust_detail_add2.setText(Address2);
			cust_detail_add3.setText(Address3);
			cust_detail_phn.setText(PhoneNo);
			cust_detail_handphone.setText(HandphoneNo);
			cust_detail_email.setText(Email);
			cust_detail_tax.setText(HaveTax);
			cust_detail_grpcode.setText(CustomerGroupCode);
			cust_detail_termcode.setText(TermCode);
			cust_detail_areacode.setText(AreaName);
			cust_detail_creditlimit.setText(CreditLimit);
			cust_detail_outstanding.setText(OutstandingAmount);
			try{
			double creditBalance = 0.00;
			Log.d("CreditLimit", "--"+CreditLimit);
			if (!CreditLimit.matches("")) {

				double credit = Double.parseDouble(CreditLimit);
				if (!OutstandingAmount.matches("")) {
					double outstanding = Double.parseDouble(OutstandingAmount);
					creditBalance = credit - outstanding;
				} else {
					creditBalance = credit;
				}
			}
			

			cust_detail_creditbalance.setText("" + creditBalance);
			}catch (Exception e){
				
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(customer_detail_layout, true);
		}
	}
	
	private String checkInternetStatus() {
		checkOffline = OfflineCommon.isConnected(CustomerList_Details.this);
		String internetStatus = "";
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
		} else if (onlineMode.matches("False")) {
			offlineLayout.setVisibility(View.VISIBLE);
		}
		
		return internetStatus;
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(CustomerList_Details.this,
				CustomerListActivity.class);
		startActivity(i);
		CustomerList_Details.this.finish();
	}
}
*/