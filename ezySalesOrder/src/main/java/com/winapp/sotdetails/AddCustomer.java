package com.winapp.sotdetails;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterProd;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.TaxData;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineCustomerSync;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.WebServiceClass;

public class AddCustomer extends Activity /*implements OnItemClickListener*/ {

	LinearLayout offlineLayout;
	LinearLayout customer_add_layout, cust_add_code_ll;
	ImageView back, mEdit, mSave;
	TextView page_Title;
	EditText ed_filter_areacode,ed_filter_vancode, cust_add_code, cust_add_name, cust_add_person,
			cust_add_add1, cust_add_add2, cust_add_add3, cust_add_phn,
			cust_add_handphone, cust_add_faxno, cust_add_email,
			cust_add_creditlimit, cust_add_areacode, cust_add_tax,cust_add_vancode;
	Spinner cust_add_grpcode, cust_add_termcode;
	//RadioButton tax_yes, tax_no;
	ArrayList<HashMap<String, String>> CustomerListArray = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> CustomerSaveArray = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> getArraylist = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> areacode_al = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> vancode_al = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> Savehm = new HashMap<String, String>();
	HashMap<String, String> customer_hashValue = new HashMap<String, String>();
	ArrayList<String> Customer_termcode_List = new ArrayList<String>();
	ArrayList<String> Customer_grpCode_List = new ArrayList<String>();

	String customer_jsonString = null, grpcode_jsonString = null, taxcode_jsonString="",
			areacode_jsonString = null, termcode_jsonString = null, vancode_jsonString = null;
	JSONObject customer_jsonResponse, grpcode_jsonResponse,
			termcode_jsonResponse, areacode_jsonResponse,vancode_jsonResponse;
	JSONArray customer_jsonMainNode, grpcode_jsonMainNode,
			termcode_jsonMainNode, areacode_jsonMainNode,vancode_jsonMainNode;
	String CustCode = "", valid_url = "", SaveResult = "";
	String CustomerCode = "", CustomerName = "", ContactPerson = "",
			Address1 = "", Address2 = "", Address3 = "", PhoneNo = "",
			HandphoneNo = "", Email = "", HaveTax = "",TaxCode = "", CustomerGroupCode = "",
			TermCode = "", CreditLimit = "", AreaCode = "",VanCode = "";
	ProgressBar progressBar;
	LinearLayout spinnerLayout, customerLayout;
	private CustomAlertAdapterProd arrayAdapterProd;
	private AlertDialog myalertDialog = null;
	private ArrayList<HashMap<String, String>> searchResults;
	private ArrayList<HashMap<String, String>> areaArrHm;
	private ArrayList<HashMap<String, String>> vanArrHm;
	private int textlength = 0;
	ArrayList<String> customerall_Arr = new ArrayList<String>();
	// @Offline
	boolean checkOffline;
	String onlineMode, offlineDialogStatus;
	private OfflineSettingsManager offlinemanager;
	private OfflineCommon offlineCommon;
	private OfflineDatabase offlineDatabase;
	String db_ccode = "",select_van="";

	private OfflineCustomerSync offlinecustomerSynch;
	Spinner cust_taxcode;
	JSONObject taxcode_jsonResponse;
	JSONArray taxcode_jsonMainNode;
	String taxCode="";
	int select_pos_tax_code=0;
	ArrayList<TaxData> Customer_taxCode_List = new ArrayList<TaxData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addcustomer);

		offlineLayout = (LinearLayout) findViewById(R.id.offlineLayout);

		final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// @Offline
		onlineMode = OfflineDatabase.getOnlineMode();
		offlineDatabase = new OfflineDatabase(this);
		offlineCommon = new OfflineCommon(this);
		checkOffline = OfflineCommon.isConnected(this);
		Log.d("Customer checkOffline ", "-->" + checkOffline);
		OfflineDatabase.init(AddCustomer.this);
		offlinecustomerSynch = new OfflineCustomerSync(this);

		if (onlineMode.matches("True")) {
			offlineLayout.setVisibility(View.GONE);
		} else if (onlineMode.matches("False")) {
			offlineLayout.setVisibility(View.VISIBLE);
		}

		customer_add_layout = (LinearLayout) findViewById(R.id.customer_add_layout);
		cust_add_code_ll = (LinearLayout) findViewById(R.id.cust_add_code_ll);
		page_Title = (TextView) findViewById(R.id.page_Title);

		customerLayout = (LinearLayout) findViewById(R.id.customer_add_layout);
		cust_add_code = (EditText) findViewById(R.id.cust_add_code_ed);
		cust_add_name = (EditText) findViewById(R.id.cust_add_name_ed);
		cust_add_person = (EditText) findViewById(R.id.cust_add_person_ed);
		cust_add_add1 = (EditText) findViewById(R.id.cust_add_add1_ed);
		cust_add_add2 = (EditText) findViewById(R.id.cust_add_add2_ed);
		cust_add_add3 = (EditText) findViewById(R.id.cust_add_add3_ed);
		cust_add_phn = (EditText) findViewById(R.id.cust_add_phn_ed);
		cust_add_handphone = (EditText) findViewById(R.id.cust_add_handphn_ed);
		cust_add_faxno = (EditText) findViewById(R.id.cust_add_faxno_ed);
		cust_add_email = (EditText) findViewById(R.id.cust_add_email_ed);
		cust_add_tax = (EditText) findViewById(R.id.cust_add_tax_ed);
		/*tax_yes = (RadioButton) findViewById(R.id.tax_yes);
		tax_no = (RadioButton) findViewById(R.id.tax_no);*/

		cust_add_grpcode = (Spinner) findViewById(R.id.cust_add_grpcode_ed);
		cust_add_termcode = (Spinner) findViewById(R.id.cust_add_termcode_ed);
		cust_add_areacode = (EditText) findViewById(R.id.cust_add_areacode_ed);

		cust_add_vancode = (EditText) findViewById(R.id.cust_add_vancode_ed);

		cust_add_creditlimit = (EditText) findViewById(R.id.cust_add_creditlimit_ed);
		cust_taxcode= (Spinner) findViewById( R.id.cust_taxcode);

		back = (ImageView) findViewById(R.id.back);
		mEdit = (ImageView) findViewById(R.id.edit_customer);
		mSave = (ImageView) findViewById(R.id.save_customer);
		areaArrHm = new ArrayList<HashMap<String, String>>();
		areaArrHm.clear();
		areacode_al.clear();

		vanArrHm = new ArrayList<HashMap<String, String>>();
		vanArrHm.clear();
		vancode_al.clear();

		cust_add_code.requestFocus();
		inputMethodManager.toggleSoftInputFromWindow(
				cust_add_code_ll.getApplicationWindowToken(),
				InputMethodManager.SHOW_FORCED, 0);

		FWMSSettingsDatabase.init(AddCustomer.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new WebServiceClass(valid_url);
		new SalesOrderWebService(valid_url);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			CustCode = extras.getString("CustCode");

		} else {
			Log.e("extras", "Extra NULL");
			mEdit.setVisibility(View.GONE);
			mSave.setVisibility(View.VISIBLE);
			String taxtype = SalesOrderSetGet.getCompanytax();
			if (taxtype.matches("E") || taxtype.matches("I")) {
				//tax_yes.setChecked(true);
				cust_add_tax.setText("True");
			} else {
			//	tax_no.setChecked(true);
				cust_add_tax.setText("False");
			}

		}
		SOTDatabase.init(AddCustomer.this);
		select_van = SOTDatabase.getVandriver();

		if (select_van.matches("")) {
			cust_add_vancode.setText("");
		} else {
			cust_add_vancode.setText(select_van);
		}

		CustomersyncCall customerservice = new CustomersyncCall();
		customerservice.execute();

		String enableCustomercode = SalesOrderSetGet.getEnablecustomercode();

		if (enableCustomercode != null && !enableCustomercode.isEmpty()) {
			if (enableCustomercode.matches("0")) {
				cust_add_code_ll.setVisibility(View.GONE);
			} else {
				cust_add_code_ll.setVisibility(View.VISIBLE);
			}
		}

	/*	tax_yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				RadioButton radio1 = (RadioButton) v;

				if (radio1.isChecked()) {
					cust_add_tax.setText("True");
					tax_yes.setChecked(true);
					tax_no.setChecked(false);

				}
			}
		});

		tax_no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				RadioButton radio2 = (RadioButton) v;

				if (radio2.isChecked()) {
					cust_add_tax.setText("False");
					tax_no.setChecked(true);
					tax_yes.setChecked(false);

				}
			}
		});
*/
		cust_add_areacode
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						cust_add_areacode) {
					@Override
					public boolean onDrawableClick() {
						// TODO Auto-generated method stub

						/*final int DRAWABLE_LEFT = 0;
			            final int DRAWABLE_TOP = 1;
			            final int DRAWABLE_RIGHT = 2;
			            final int DRAWABLE_BOTTOM = 3;*/
						
						
						Drawable[] drawables = cust_add_areacode
								.getCompoundDrawables();
						if (drawables != null) {
							Log.d("drawables", "not null -> "
									+ drawables.length);
							Drawable drawableright = drawables[2];
							if (drawableright != null) {
								Log.d("drawableright", "not null ");
								if (drawableright.getConstantState().equals(
										getResources().getDrawable(
												R.mipmap.ic_search_normal)
												.getConstantState())) {
									Log.d("drawableright", "drawableright");
									areaArrHm = SOTDatabase.getArea();
									if (areaArrHm.isEmpty()) {
										areaCodeFilter(areacode_al);
									} else {
										areaCodeFilter(areaArrHm);
									}

								}
							}
						}

						return true;

					}
				});

		cust_add_vancode
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						cust_add_vancode) {
					@Override
					public boolean onDrawableClick() {
						// TODO Auto-generated method stub

						/*final int DRAWABLE_LEFT = 0;
			            final int DRAWABLE_TOP = 1;
			            final int DRAWABLE_RIGHT = 2;
			            final int DRAWABLE_BOTTOM = 3;*/


						Drawable[] drawables = cust_add_vancode
								.getCompoundDrawables();
						if (drawables != null) {
							Log.d("drawables", "not null -> "
									+ drawables.length);
							Drawable drawableright = drawables[2];
							if (drawableright != null) {
								Log.d("drawableright", "not null ");
								if (drawableright.getConstantState().equals(
										getResources().getDrawable(
												R.mipmap.ic_search_normal)
												.getConstantState())) {
									Log.d("drawableright", "drawableright");
									vanArrHm = SOTDatabase.getVan();
									if (vanArrHm.isEmpty()) {
										vanCodeFilter(vancode_al);
									} else {
										vanCodeFilter(vanArrHm);
									}

								}
							}
						}

						return true;

					}
				});
		cust_add_grpcode
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parentView,
							View selectedItemView, int position, long id) {

						parentView.getItemAtPosition(position).toString();

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		cust_add_termcode
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parentView,
							View selectedItemView, int position, long id) {

						parentView.getItemAtPosition(position).toString();

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		cust_taxcode
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parentView,
											   View selectedItemView, int position, long id) {


//						parentView.getItemAtPosition(position).toString();
						select_pos_tax_code	=position;
						taxCode=Customer_taxCode_List.get(position).getTaxCode();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		mEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				enableView();
			}
		});

		mSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String ccode = cust_add_code.getText().toString();
				String cname = cust_add_name.getText().toString();
				String cperson = cust_add_person.getText().toString();
				String cadd1 = cust_add_add1.getText().toString();
				String cadd2 = cust_add_add2.getText().toString();
				String cadd3 = cust_add_add3.getText().toString();
				String cphn = cust_add_phn.getText().toString();
				String chandphn = cust_add_handphone.getText().toString();
				String cfaxno = cust_add_faxno.getText().toString();
				String cemail = cust_add_email.getText().toString();
				String ctax = "";
				String careacode = cust_add_areacode.getText().toString();
				String cvancode = cust_add_vancode.getText().toString();
				String cgrpcode = cust_add_grpcode.getSelectedItem().toString();
				String cterncode = cust_add_termcode.getSelectedItem()
						.toString();
				String ccrdlimit = cust_add_creditlimit.getText().toString();
				String cuser = SupplierSetterGetter.getUsername();
				String ctaxtype = SalesOrderSetGet.getCompanytax();
				String ctaxvalue = SalesOrderSetGet.getCompanytaxvalue();
				String cTaxCode = taxCode;


				Log.d("ctaxtype", ctaxtype);

			/*	if (tax_yes.isChecked()) {
					ctax = "1";
				} else {
					ctax = "0";
				}

				Log.d("ctax", ctax);*/
				if (cTaxCode.equalsIgnoreCase("Select")) {
					Log.d("if","if");
					ctax = SupplierSetterGetter.getTaxCode();
				}else{
					Log.d("else","else");
					ctax = cTaxCode;
				}

				if (cgrpcode.matches("Select")) {

					cgrpcode = "";

				}
				if (cterncode.matches("Select")) {

					cterncode = "";

				}

				if (ccrdlimit.matches("")) {
					ccrdlimit = "0";
				}

				if (cname.matches("")) {
					cust_add_name.setError("");
				} else if (!ccode.matches("")
						&& cust_add_code.getText().length() > 10) {

					Toast.makeText(AddCustomer.this,
							"Customer code is not greater than 10 character",
							Toast.LENGTH_LONG).show();
				} else {

					String cmpnyCode = SupplierSetterGetter.getCompanyCode();

					Savehm.put("CustomerCode", ccode);
					Savehm.put("CustomerName", cname);
					Savehm.put("ContactPerson", cperson);
					Savehm.put("Address1", cadd1);
					Savehm.put("Address2", cadd2);
					Savehm.put("Address3", cadd3);
					Savehm.put("PhoneNo", cphn);
					Savehm.put("HandphoneNo", chandphn);
					Savehm.put("Email", cemail);
					Savehm.put("HaveTax", ctax);
					Savehm.put("CustomerGroupCode", cgrpcode);

					Savehm.put("User", cuser);
					Savehm.put("TermCode", cterncode);
					Savehm.put("IsActive", "1");
					Savehm.put("CreditLimit", ccrdlimit);
					Savehm.put("TaxType", ctaxtype);
					Savehm.put("TaxPerc", ctaxvalue);
					Savehm.put("CompanyCode", cmpnyCode);
					Savehm.put("FaxNo", cfaxno);
					Savehm.put("AreaCode", careacode);
					Savehm.put("VanCode", cvancode);

					Log.d("Savehm", Savehm.toString());

					loading();

					if (CustCode != null && !CustCode.isEmpty()) {
						CustomerSavesyncCall savecustomerservice = new CustomerSavesyncCall();
						savecustomerservice.execute();
					} else {
						AsyncCallWSAllCustomer task = new AsyncCallWSAllCustomer();
						task.execute();
					}

				}
			}
		});

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Intent i = new Intent(AddCustomer.this,
				 CustomerListActivity.class);
				 startActivity(i);
				AddCustomer.this.finish();
			}
		});

	}

	private class EditCustomer extends AsyncTask<Void, Void, Void> {
		String dialogStatus;

		@Override
		protected void onPreExecute() {
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			customer_hashValue.put("CompanyCode", cmpnyCode);
			customer_hashValue.put("CustomerCode", CustCode);
			customer_hashValue.put("NeedOutstandingAmount", "");
			customer_hashValue.put("AreaCode", "");

			if (onlineMode.matches("False")) {
				Log.d("Offline", "-->" + "true");
				customer_jsonString = OfflineDatabase
						.getCustomersList(customer_hashValue);
			} else if (onlineMode.matches("True")) {
				Log.d("Login Online Mode", onlineMode);
				offlineLayout.setVisibility(View.GONE);
				if (checkOffline == true) {
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) {
						Log.d("CheckOffline Alert -->", "True");
						offlineLayout.setVisibility(View.VISIBLE);
						offlineLayout.setBackgroundResource(drawable.temp_offline_pattern_bg);
						customer_jsonString = OfflineDatabase
								.getCustomersList(customer_hashValue);
					} else {
						Log.d("CheckOffline Alert -->", "False");
						finish();
					}

				} else {
					customer_jsonString = WebServiceClass.parameterService(
							customer_hashValue, "fncGetCustomer");
				}

			}

			try {

				customer_jsonResponse = new JSONObject(customer_jsonString);
				customer_jsonMainNode = customer_jsonResponse
						.optJSONArray("JsonArray");

			} catch (JSONException e) {

				e.printStackTrace();
			}

			/*********** Process each JSON Node ************/
			int lengthJsonArr = customer_jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
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
					VanCode = jsonChildNode.optString("VanCode").toString();
					CreditLimit = jsonChildNode.optString("CreditLimit")
							.toString();
					TaxCode = jsonChildNode.optString("TaxCode").toString();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/******* Fetch node values **********/
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (CustomerGroupCode.matches("-1")
					|| CustomerGroupCode.matches("null")
					|| CustomerGroupCode.matches("")) {
				CustomerGroupCode = "Select";
			}
			if (TermCode.matches("-1") || TermCode.matches("null")
					|| TermCode.matches("")) {
				TermCode = "Select";
			}
			/*if (HaveTax.matches("-1") || HaveTax.matches("null")
					|| HaveTax.matches("")) {
				HaveTax = "Select";
			}

			if(HaveTax.matches("True") || HaveTax.matches("true")){
				HaveTax="Select";
			}*/

			if (TaxCode.matches("-1") || TaxCode.matches("null")
					|| TaxCode.matches("")) {
				TaxCode = "Select";
			}
			page_Title.setText(CustomerName);

			Log.d("selected CustomerCode", CustomerCode + "->" + CustomerName);
			cust_add_code.setText(CustomerCode);
			cust_add_code.setFocusable(false);
			cust_add_code.setEnabled(false);
			cust_add_code.setClickable(false);
			cust_add_name.setText(CustomerName);
			cust_add_person.setText(ContactPerson);
			cust_add_add1.setText(Address1);
			cust_add_add2.setText(Address2);
			cust_add_add3.setText(Address3);

			if (Address2 != null && !Address2.isEmpty()) {
				cust_add_add2.setVisibility(View.VISIBLE);
			} else {
				cust_add_add2.setVisibility(View.GONE);
			}

			if (Address3 != null && !Address3.isEmpty()) {
				cust_add_add3.setVisibility(View.VISIBLE);
			} else {
				cust_add_add3.setVisibility(View.GONE);
			}

			cust_add_phn.setText(PhoneNo);
			cust_add_handphone.setText(HandphoneNo);
			cust_add_email.setText(Email);
			cust_add_areacode.setText(AreaCode);
			cust_add_vancode.setText(VanCode);
			/*if (HaveTax.matches("True") || HaveTax.matches("true")) {
				//tax_yes.setChecked(true);
				HaveTax="Select";
				cust_add_tax.setText("True");
			} else {
			//	tax_no.setChecked(true);
				HaveTax="Select";
				cust_add_tax.setText("False");
				cust_add_tax.setText("False");
			}*/

			int select_pos_cust_group = 0;
			for (int i = 0; i < Customer_grpCode_List.size(); i++) {
				if (Customer_grpCode_List.get(i).contains(CustomerGroupCode)) {
					select_pos_cust_group = i;

				}
			}
			cust_add_grpcode.setSelection(select_pos_cust_group);

			int select_pos_term_code = 0;
			for (int i = 0; i < Customer_termcode_List.size(); i++) {
				if (Customer_termcode_List.get(i).contains(TermCode)) {
					select_pos_term_code = i;

				}
			}

		//	int select_pos_tax_code=0;
		//	cust_taxcode.setSelection(select_pos_tax_code);

			/*for (int i = 0; i < Customer_taxCode_List.size(); i++) {
				if (Customer_taxCode_List.get(i).getTaxCode().contains(HaveTax)) {
					select_pos_tax_code = i;

				}
			}*/
			for (int i = 0; i < Customer_taxCode_List.size(); i++) {
				if (Customer_taxCode_List.get(i).getTaxCode().matches(TaxCode)) {
					Log.d("taxcodecheck",Customer_taxCode_List.get(i).getTaxCode());
					select_pos_tax_code = i;
				}
			}
			cust_taxcode.setSelection(select_pos_tax_code);

			cust_add_termcode.setSelection(select_pos_term_code);
			cust_add_creditlimit.setText(CreditLimit);

			disableView();

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(customerLayout, true);

		}
	}

	private class CustomersyncCall extends AsyncTask<Void, Void, Void> {

		String dialogStatus;

		@Override
		protected void onPreExecute() {
			Customer_grpCode_List.clear();
			Customer_termcode_List.clear();
			Customer_taxCode_List.clear();
			taxcode_jsonString="";
			grpcode_jsonString = "";
			termcode_jsonString = "";
			areacode_jsonString = "";
			vancode_jsonString = "";
			loading();
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if (onlineMode.matches("False")) {
				Log.d("Offline", "-->" + "true");
				String areacode = "";
				grpcode_jsonString = OfflineDatabase.getCustomerGroup();
				termcode_jsonString = OfflineDatabase.getTerms();
				taxcode_jsonString =OfflineDatabase.getTax();
				areacode_jsonString = OfflineDatabase.getArea(areacode);
			} else if (onlineMode.matches("True")) {
				Log.d("Login Online Mode", onlineMode);
				offlineLayout.setVisibility(View.GONE);
				if (checkOffline == true) {
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) {
						Log.d("CheckOffline Alert -->", "True");
						offlineLayout.setVisibility(View.VISIBLE);
						offlineLayout.setBackgroundResource(drawable.temp_offline_pattern_bg);
						String areacode = "";
						grpcode_jsonString = OfflineDatabase.getCustomerGroup();
						termcode_jsonString = OfflineDatabase.getTerms();
						taxcode_jsonString =OfflineDatabase.getTax();
						areacode_jsonString = OfflineDatabase.getArea(areacode);
					} else {
						Log.d("CheckOffline Alert -->", "False");
						finish();
					}

				} else {
					grpcode_jsonString = WebServiceClass
							.URLService("fncGetCustomerGroup");
					termcode_jsonString = WebServiceClass
							.URLService("fncGetTerms");
					taxcode_jsonString =WebServiceClass
							.URLService("fncGetTax");
					areacode_jsonString = WebServiceClass
							.URLService("fncGetArea");
					vancode_jsonString = WebServiceClass
							.URLService("fncGetVan");
				}

			}

			try {
				grpcode_jsonResponse = new JSONObject(grpcode_jsonString);
				grpcode_jsonMainNode = grpcode_jsonResponse
						.optJSONArray("JsonArray");

				termcode_jsonResponse = new JSONObject(termcode_jsonString);
				termcode_jsonMainNode = termcode_jsonResponse
						.optJSONArray("JsonArray");

				taxcode_jsonResponse= new JSONObject(taxcode_jsonString);
				taxcode_jsonMainNode =taxcode_jsonResponse
						.optJSONArray("JsonArray");

				areacode_jsonResponse = new JSONObject(areacode_jsonString);
				areacode_jsonMainNode = areacode_jsonResponse
						.optJSONArray("JsonArray");

				vancode_jsonResponse = new JSONObject(vancode_jsonString);
				vancode_jsonMainNode = vancode_jsonResponse
						.optJSONArray("JsonArray");

				Customer_grpCode_List.add("Select");
				Customer_termcode_List.add("Select");

				TaxData taxData=new TaxData();
				taxData.setTaxName("Select");
				taxData.setTaxCode("Select");
				Customer_taxCode_List.add(taxData);

				/*********** Process each JSON Node grpcode ************/
				int lengthJsonArr1 = grpcode_jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr1; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject grpcode_jsonChildNode;
					try {

						grpcode_jsonChildNode = grpcode_jsonMainNode
								.getJSONObject(i);

						String grp_Code = grpcode_jsonChildNode.optString(
								"Description").toString();
						Customer_grpCode_List.add(grp_Code);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				int lengthJsonArr2 = areacode_jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr2; i++) {

					JSONObject areacode_jsonChildNode;
					try {

						areacode_jsonChildNode = areacode_jsonMainNode
								.getJSONObject(i);

						String area_code = areacode_jsonChildNode.optString(
								"Code").toString();
						String area_description = areacode_jsonChildNode
								.optString("Description").toString();
						HashMap<String, String> areacodehm = new HashMap<String, String>();
						areacodehm.put(area_code, area_description);
						areacode_al.add(areacodehm);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					/******* Fetch node values **********/
				}

				int lengthJsonArr3 = termcode_jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr3; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject termcode_jsonChildNode;
					try {

						termcode_jsonChildNode = termcode_jsonMainNode
								.getJSONObject(i);

						String term_description = termcode_jsonChildNode
								.optString("Description").toString();
						Customer_termcode_List.add(term_description);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				int lengthJsonArr4 = vancode_jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr4; i++) {

					JSONObject vancode_jsonChildNode;
					try {

						vancode_jsonChildNode = vancode_jsonMainNode
								.getJSONObject(i);

						String van_code = vancode_jsonChildNode.optString(
								"Code").toString();
						String van_description = vancode_jsonChildNode
								.optString("Description").toString();
						HashMap<String, String> vancodehm = new HashMap<String, String>();
						vancodehm.put(van_code, van_description);
						vancode_al.add(vancodehm);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					/******* Fetch node values **********/
				}

				int lengthJsonArr5 = taxcode_jsonMainNode.length();
				Log.d("lengthJsonArr5",""+lengthJsonArr5);
				for (int i = 0; i < lengthJsonArr5; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject taxcode_jsonChildNode;
					try {

						taxcode_jsonChildNode = taxcode_jsonMainNode
								.getJSONObject(i);

						TaxData taxDatas=new TaxData();

						String tax_Code = taxcode_jsonChildNode.optString(
								"TaxCode").toString();
						String tax_Name =taxcode_jsonChildNode.optString("TaxName").toString();

						taxDatas.setTaxCode(tax_Code);
						taxDatas.setTaxName(tax_Name);
						Customer_taxCode_List.add(taxDatas);


					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				Log.d("Customer_taxCode_List",""+Customer_taxCode_List.size());

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (Customer_grpCode_List.size() != 0) {
				cust_add_grpcode.setAdapter(new MyCustomAdapter(
						AddCustomer.this, R.layout.row, Customer_grpCode_List));
			}
			if (Customer_termcode_List.size() != 0) {
				cust_add_termcode
						.setAdapter(new MyCustomAdapter(AddCustomer.this,
								R.layout.row, Customer_termcode_List));
			}

			if (Customer_taxCode_List.size() != 0) {
				cust_taxcode
						.setAdapter(new CustomAdapter(AddCustomer.this,
								R.layout.row, Customer_taxCode_List));
			}

			if (CustCode != null && !CustCode.isEmpty()) {
				EditCustomer edit = new EditCustomer();
				edit.execute();
			} else {
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(customerLayout, true);
			}

		}
	}

	private class AsyncCallWSAllCustomer extends AsyncTask<Void, Void, Void> {
		String dialogStatus;

		@Override
		protected void onPreExecute() {
			dialogStatus = checkInternetStatus();
			customerall_Arr.clear();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			if (onlineMode.matches("True")) {
				if (checkOffline == true) { // Temp offline
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) {
						Log.d("CheckOffline Alert -->", "True");
						customerall_Arr = OfflineDatabase.getAllCustomer();
					} else {
						Log.d("CheckOffline Alert -->", "False");
						finish();
					}

				} else { // Online
					customerall_Arr = SalesOrderWebService
							.getAllCustomer("fncGetCustomerForSearch");
				}

			} else if (onlineMode.matches("False")) { // permanant offline
				customerall_Arr = OfflineDatabase.getAllCustomer();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			boolean res = false;
			for (String alphabet : customerall_Arr) {
				if (alphabet.toLowerCase().equals(
						cust_add_code.getText().toString().toLowerCase())) {

					res = true;
					break;
				}
			}

			if (res == true) {
				Toast.makeText(getApplicationContext(),
						"Customer code already exist", Toast.LENGTH_SHORT)
						.show();
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(customerLayout, true);
			} else {

				CustomerSavesyncCall savecustomerservice = new CustomerSavesyncCall();
				savecustomerservice.execute();
			}

		}
	}

	private class CustomerSavesyncCall extends AsyncTask<Void, Void, Void> {
		String dialogStatus;

		@Override
		protected void onPreExecute() {

			dialogStatus = checkInternetStatus();
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected Void doInBackground(Void... arg0) {

			if (onlineMode.matches("False")) {
				Log.d("Offline", "-->" + "true");
				Savehm.put("DownloadStatus", "1");
				SaveResult = offlineDBSave(Savehm);

			} else if (onlineMode.matches("True")) {
				Log.d("Offline Online Mode", onlineMode);

				if (checkOffline == true) {
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) {
						Log.d("CheckOffline Alert -->", "True");
						offlineLayout.setBackgroundResource(drawable.temp_offline_pattern_bg);
						Savehm.put("DownloadStatus", "1");
						SaveResult = offlineDBSave(Savehm);
					} else {
						Log.d("CheckOffline Alert -->", "False");
						finish();
					}

				} else {

					customer_jsonString = WebServiceClass.SaveCustomerService(
							Savehm, "fncSaveCustomer");

					Log.d("customer_jsonString", customer_jsonString);
					try {

						customer_jsonResponse = new JSONObject(
								customer_jsonString);
						customer_jsonMainNode = customer_jsonResponse
								.optJSONArray("saveArray");

					} catch (JSONException e) {
						e.printStackTrace();
					}

					// *********** Process each JSON Node ************//
					int lengthJsonArr = customer_jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {
						// ****** Get Object for each JSON node. ***********//
						JSONObject jsonChildNode;
						try {

							jsonChildNode = customer_jsonMainNode
									.getJSONObject(i);

							SaveResult = jsonChildNode.optString("Result")
									.toString();
							// offline
							if (SaveResult.matches("True")) {
								offlinecustomerSynch.synchCustomerData();
								// Savehm.put("DownloadStatus", "0");
								// SaveResult = offlineDBSave(Savehm);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (SaveResult.matches("True")) {
				Intent i = new Intent(AddCustomer.this,
						CustomerListActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				AddCustomer.this.finish();
				Toast.makeText(getApplication(), "Saved Successfully",
						Toast.LENGTH_SHORT).show();
			} else if (SaveResult.matches("False")) {
				Log.d("False", "--> False");
				Toast.makeText(getApplication(),
						"CustomerCode " + db_ccode + " Already Exist",
						Toast.LENGTH_LONG).show();
			} else {

			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(customerLayout, true);
		}
	}

	public class CustomAdapter extends ArrayAdapter<TaxData> {

		ArrayList<TaxData> adapterList = new ArrayList<TaxData>();

		public CustomAdapter(Context context, int textViewResourceId,
							 ArrayList<TaxData> objects) {
			super(context,textViewResourceId,objects);
			this.adapterList.clear();
			this.adapterList = objects;

		}

		@Override
		public View getDropDownView(int position, View convertView,
									ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
								  ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);
			TaxData taxData = adapterList.get(position);
			TextView label = (TextView) row.findViewById(R.id.locationspinner);
			ImageView icon = (ImageView) row.findViewById(R.id.spinnericon);
			Log.d("getTaxnameDta",""+taxData.getTaxName());
			label.setText(taxData.getTaxName());
			icon.setVisibility(View.GONE);
			return row;
		}
	}


	public class MyCustomAdapter extends ArrayAdapter<String> {

		ArrayList<String> adapterList = new ArrayList<String>();

		public MyCustomAdapter(Context context, int textViewResourceId,
				ArrayList<String> objects) {
			super(context, textViewResourceId, objects);
			adapterList.clear();
			adapterList = objects;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);
			TextView label = (TextView) row.findViewById(R.id.locationspinner);
			ImageView icon = (ImageView) row.findViewById(R.id.spinnericon);
			label.setText(adapterList.get(position));
			icon.setVisibility(View.GONE);
			return row;
		}
	}

	@SuppressWarnings("deprecation")
	public void loading() {

		spinnerLayout = new LinearLayout(AddCustomer.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(customerLayout, false);
		progressBar = new ProgressBar(AddCustomer.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		spinnerLayout.addView(progressBar);
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

	private void areaCodeFilter(final ArrayList<HashMap<String, String>> area) {
		// TODO Auto-generated method stub
		AlertDialog.Builder myDialog = new AlertDialog.Builder(AddCustomer.this);
		ed_filter_areacode = new EditText(AddCustomer.this);
		final ListView listview = new ListView(AddCustomer.this);
		LinearLayout layout = new LinearLayout(AddCustomer.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Area Code");
		ed_filter_areacode.setCompoundDrawablesWithIntrinsicBounds(
				drawable.search, 0, 0, 0);
		layout.addView(ed_filter_areacode);
		layout.addView(listview);
		myDialog.setView(layout);
		ed_filter_areacode
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							myalertDialog
									.getWindow()
									.setSoftInputMode(
											WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
						}
					}
				});
		arrayAdapterProd = new CustomAlertAdapterProd(AddCustomer.this, area);
		listview.setAdapter(arrayAdapterProd);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				myalertDialog.dismiss();
				getArraylist = arrayAdapterProd.getArrayList();

				HashMap<String, String> datavalue = getArraylist.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String key = (String) mapEntry.getKey();
					String values = (String) mapEntry.getValue();

					cust_add_areacode.setText(key);

				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(area);
		ed_filter_areacode.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				textlength = ed_filter_areacode.getText().length();
				searchResults.clear();
				for (int i = 0; i < area.size(); i++) {
					String supplierName = area.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								ed_filter_areacode.getText().toString()
										.toLowerCase().trim()))
							searchResults.add(area.get(i));
					}
				}

				arrayAdapterProd = new CustomAlertAdapterProd(AddCustomer.this,
						searchResults);
				listview.setAdapter(arrayAdapterProd);
			}
		});
		myDialog.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		myalertDialog = myDialog.show();

	}

	//Van code dialog filter
	private void vanCodeFilter(final ArrayList<HashMap<String, String>> van) {
		// TODO Auto-generated method stub
		AlertDialog.Builder myDialog = new AlertDialog.Builder(AddCustomer.this);
		ed_filter_vancode = new EditText(AddCustomer.this);
		final ListView listview = new ListView(AddCustomer.this);
		LinearLayout layout = new LinearLayout(AddCustomer.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Van Code");
		ed_filter_vancode.setCompoundDrawablesWithIntrinsicBounds(
				drawable.search, 0, 0, 0);
		layout.addView(ed_filter_vancode);
		layout.addView(listview);
		myDialog.setView(layout);
		ed_filter_vancode
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							myalertDialog
									.getWindow()
									.setSoftInputMode(
											WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
						}
					}
				});
		arrayAdapterProd = new CustomAlertAdapterProd(AddCustomer.this, van);
		listview.setAdapter(arrayAdapterProd);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				myalertDialog.dismiss();
				getArraylist = arrayAdapterProd.getArrayList();

				HashMap<String, String> datavalue = getArraylist.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String key = (String) mapEntry.getKey();
					String values = (String) mapEntry.getValue();

					cust_add_vancode.setText(key);

				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(van);
		ed_filter_vancode.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				textlength = ed_filter_vancode.getText().length();
				searchResults.clear();
				for (int i = 0; i < van.size(); i++) {
					String supplierName = van.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								ed_filter_vancode.getText().toString()
										.toLowerCase().trim()))
							searchResults.add(van.get(i));
					}
				}

				arrayAdapterProd = new CustomAlertAdapterProd(AddCustomer.this,
						searchResults);
				listview.setAdapter(arrayAdapterProd);
			}
		});
		myDialog.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		myalertDialog = myDialog.show();

	}

	public String offlineDBSave(HashMap<String, String> Savehm) {
		String saveResult = "";
		String ccode = "";

		SimpleDateFormat timeFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		String finalDate = timeFormat.format(new Date());
		System.out.println(finalDate);
		Savehm.put("DateTime", finalDate);

		if (CustCode != null && !CustCode.isEmpty()) {
			OfflineDatabase.update_customer(Savehm);
			saveResult = "True";
		} else {

			String custCode = cust_add_code.getText().toString();
			if (custCode != null && !custCode.isEmpty()) {
				Log.d("CustomerCode", "-->" + custCode);
				ccode = custCode;
			} else {
				Log.d(" empty CustomerCode", "-->" + custCode);
				ccode = OfflineDatabase.getNextCustomerRunningNumber(
						"CustomerPrefix", "CustomerNextNo");
				// OfflineDatabase.updateNextRunningNumber("CustomerNextNo");
			}

			Savehm.put("OfflineCustomerCode", ccode);

			db_ccode = OfflineDatabase.getCustomerCode(ccode);
			Log.d("CustomerCode in DB", "-->" + db_ccode);
			if (db_ccode != null && !db_ccode.isEmpty()) {
				saveResult = "False";
			} else {
				OfflineDatabase.save_customer(Savehm);
				if (custCode != null && !custCode.isEmpty()) {
				} else {
					OfflineDatabase.updateNextRunningNumber("CustomerNextNo");
				}
				saveResult = "True";
			}
		}

		return saveResult;
	}

	private void disableView() {
		mEdit.setVisibility(View.VISIBLE);
		mSave.setVisibility(View.GONE);
		cust_add_tax.setVisibility(View.VISIBLE);
		/*tax_yes.setVisibility(View.GONE);
		tax_no.setVisibility(View.GONE);*/

		cust_add_name.setFocusable(false);
		cust_add_name.setBackgroundResource(drawable.labelbg);
		cust_add_name.setCursorVisible(false);

		cust_add_person.setFocusable(false);
		cust_add_person.setBackgroundResource(drawable.labelbg);
		cust_add_person.setCursorVisible(false);

		cust_add_add1.setFocusable(false);
		cust_add_add1.setBackgroundResource(drawable.labelbg);
		cust_add_add1.setCursorVisible(false);

		cust_add_add2.setFocusable(false);
		cust_add_add2.setBackgroundResource(drawable.labelbg);
		cust_add_add2.setFocusable(false);

		cust_add_add3.setFocusable(false);
		cust_add_add3.setBackgroundResource(drawable.labelbg);
		cust_add_add3.setCursorVisible(false);

		cust_add_phn.setFocusable(false);
		cust_add_phn.setBackgroundResource(drawable.labelbg);
		cust_add_phn.setCursorVisible(false);

		cust_add_handphone.setFocusable(false);
		cust_add_handphone.setBackgroundResource(drawable.labelbg);
		cust_add_handphone.setCursorVisible(false);

		cust_add_faxno.setFocusable(false);
		cust_add_faxno.setBackgroundResource(drawable.labelbg);
		cust_add_faxno.setCursorVisible(false);

		cust_add_email.setFocusable(false);
		cust_add_email.setBackgroundResource(drawable.labelbg);
		cust_add_email.setCursorVisible(false);

		cust_add_creditlimit.setFocusable(false);
		cust_add_creditlimit.setBackgroundResource(drawable.labelbg);
		cust_add_creditlimit.setCursorVisible(false);

		cust_add_areacode.setFocusable(false);
		cust_add_areacode.setBackgroundResource(drawable.labelbg);
		cust_add_areacode.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.mipmap.ic_search_inactive, 0);
		cust_add_areacode.setCursorVisible(false);

		cust_add_vancode.setFocusable(false);
		cust_add_vancode.setBackgroundResource(drawable.labelbg);
		cust_add_vancode.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.mipmap.ic_search_inactive, 0);
		cust_add_vancode.setCursorVisible(false);

		cust_add_grpcode
				.setBackgroundResource(drawable.customer_spinner_disabled);
		cust_add_grpcode.setClickable(false);
		cust_add_termcode
				.setBackgroundResource(drawable.customer_spinner_disabled);
		cust_add_termcode.setClickable(false);
		cust_taxcode
				.setBackgroundResource(drawable.customer_spinner_disabled);
		cust_taxcode.setClickable(false);
	}

	private void enableView() {
		mEdit.setVisibility(View.GONE);
		mSave.setVisibility(View.VISIBLE);
		cust_add_tax.setVisibility(View.GONE);
		/*tax_yes.setVisibility(View.VISIBLE);
		tax_no.setVisibility(View.VISIBLE);*/

		cust_add_name.setFocusableInTouchMode(true);
		cust_add_name.setBackgroundResource(drawable.edittext_bg);
		cust_add_name.setCursorVisible(true);

		cust_add_person.setFocusableInTouchMode(true);
		cust_add_person.setBackgroundResource(drawable.edittext_bg);
		cust_add_person.setCursorVisible(true);

		cust_add_add1.setFocusableInTouchMode(true);
		cust_add_add1.setBackgroundResource(drawable.edittext_bg);
		cust_add_add1.setCursorVisible(true);

		cust_add_add2.setVisibility(View.VISIBLE);

		cust_add_add2.setFocusableInTouchMode(true);
		cust_add_add2.setBackgroundResource(drawable.edittext_bg);
		cust_add_add2.setCursorVisible(true);

		cust_add_add3.setVisibility(View.VISIBLE);

		cust_add_add3.setFocusableInTouchMode(true);
		cust_add_add3.setBackgroundResource(drawable.edittext_bg);
		cust_add_add3.setCursorVisible(true);

		cust_add_phn.setFocusableInTouchMode(true);
		cust_add_phn.setBackgroundResource(drawable.edittext_bg);
		cust_add_phn.setCursorVisible(true);

		cust_add_handphone.setFocusableInTouchMode(true);
		cust_add_handphone.setBackgroundResource(drawable.edittext_bg);
		cust_add_handphone.setCursorVisible(true);

		cust_add_faxno.setFocusableInTouchMode(true);
		cust_add_faxno.setBackgroundResource(drawable.edittext_bg);
		cust_add_faxno.setCursorVisible(true);

		cust_add_email.setFocusableInTouchMode(true);
		cust_add_email.setBackgroundResource(drawable.edittext_bg);
		cust_add_email.setCursorVisible(true);

		cust_add_creditlimit.setFocusableInTouchMode(true);
		cust_add_creditlimit.setBackgroundResource(drawable.edittext_bg);
		cust_add_creditlimit.setCursorVisible(true);

		cust_add_areacode.setFocusableInTouchMode(true);
		cust_add_areacode.setBackgroundResource(drawable.edittext_bg);
		cust_add_areacode.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.mipmap.ic_search_normal, 0);
		cust_add_areacode.setCursorVisible(false);

		cust_add_vancode.setFocusableInTouchMode(true);
		cust_add_vancode.setBackgroundResource(drawable.edittext_bg);
		cust_add_vancode.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.mipmap.ic_search_normal, 0);
		cust_add_vancode.setCursorVisible(false);

		cust_add_grpcode.setBackgroundResource(drawable.customer_spinner);
		cust_add_grpcode.setClickable(true);
		cust_add_termcode.setBackgroundResource(drawable.customer_spinner);
		cust_add_termcode.setClickable(true);
		cust_taxcode.setBackgroundResource(drawable.customer_spinner);
		cust_taxcode.setClickable(true);
	}

	private String checkInternetStatus() {
		checkOffline = OfflineCommon.isConnected(AddCustomer.this);
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
		 Intent i = new Intent(AddCustomer.this, CustomerListActivity.class);
		startActivity(i);
		AddCustomer.this.finish();
	}

}
