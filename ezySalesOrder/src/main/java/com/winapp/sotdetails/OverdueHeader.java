package com.winapp.sotdetails;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LandingActivity;
import com.winapp.helper.Customer;
import com.winapp.helper.DateTime;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.printer.InvoicePrintPreview;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.UIHelper;
import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.SO;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SalesProductWebService;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.sot.WebServiceClass;

public class OverdueHeader extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace {
	private SlidingMenu menu;
	private ProgressBar progressBar;
	private LinearLayout spinnerLayout, overdue_parent, searchlayout;
	private ImageButton searchIcon, addIcon;
	private EditText ed_custcode,locationcode_filter, ed_custname, ed_areacode,ed_description, ed_vancode, ed_vanname, starteditTextDate, endeditTextDate;
	private Button bt_search;
	private ListView overdue_list;
	private String valid_url="",loccode="", companyCode="", serverdate="", gnrlStngs="", invNoStr="", invDateStr="",custCodeStr="", custNameStr="",
					select_customer="", select_sdate="", select_edata="", select_area="", select_van="" ;
	private ArrayList<SO> overduelistArray = new ArrayList<SO>();

	ArrayList<HashMap<String, String>> getCustomerArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> getAreaArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> getVanArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> customerlist = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> arealist = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> vanlist = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> customerResults, areaResults, vanResults;
	int textlength = 0;
	ArrayList<HashMap<String, String>>  locationArrHm,searchResults,getArraylsit;
	private ArrayList<ProductDetails> product, productdet, product_batchArr, footerArr;
	ArrayList<String> sortproduct;
	private OverdueAdapter overdueAdapter;
	private UIHelper helper;
	private Calendar startCalendar, endCalendar;
	CustomAlertAdapterSupp customerAdapter, areaAdapter, vanAdapter;
	private AlertDialog customerDialog = null, areaDialog = null, vanDialog = null,myalertDialog = null;
	OfflineSettingsManager spManager;
	ArrayList<Integer> mColor = new ArrayList<Integer>();
	CustomAlertAdapterSupp arrayAdapterSupp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.overdue_header);
		
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Overdue");
		searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
		addIcon = (ImageButton) customNav.findViewById(R.id.custcode_img);

		addIcon.setVisibility(View.GONE);

		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);

		ab.setDisplayHomeAsUpEnabled(false);
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);
		
		FWMSSettingsDatabase.init(OverdueHeader.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new WebServiceClass(valid_url);
		new SalesProductWebService(valid_url);
		new SalesOrderWebService(valid_url);
		new DateWebservice(valid_url,OverdueHeader.this);
		helper = new UIHelper(OverdueHeader.this);
		SOTDatabase.init(OverdueHeader.this);
		spManager = new OfflineSettingsManager(OverdueHeader.this);
		companyCode = spManager.getCompanyType();

		locationArrHm = new ArrayList<>();
		searchResults = new ArrayList<>();
		getArraylsit = new ArrayList<>();

		product = new ArrayList<ProductDetails>();
		productdet = new ArrayList<ProductDetails>();
		product_batchArr = new ArrayList<ProductDetails>();
		footerArr = new ArrayList<ProductDetails>();
		sortproduct = new ArrayList<String>();
		
		startCalendar = Calendar.getInstance();
		endCalendar = Calendar.getInstance();
		
		ed_custcode = (EditText) findViewById(R.id.ed_custcode);
		ed_custname = (EditText) findViewById(R.id.ed_custname);
		ed_areacode = (EditText) findViewById(R.id.ed_areacode);
		ed_description = (EditText) findViewById(R.id.ed_description);
		ed_vancode = (EditText) findViewById(R.id.ed_vancode);
		ed_vanname = (EditText) findViewById(R.id.ed_vanname);
		starteditTextDate = (EditText) findViewById(R.id.starteditTextDate);
		endeditTextDate = (EditText) findViewById(R.id.endeditTextDate);
		bt_search = (Button) findViewById(R.id.btn_search);
		
		searchlayout = (LinearLayout) findViewById(R.id.searchlayout);
		overdue_parent = (LinearLayout) findViewById(R.id.overdue_parent);
		overdue_list = (ListView) findViewById(R.id.overdue_list);
		locationcode_filter = (EditText) findViewById(R.id.locationcode_filter);
		mColor.clear();
		  
		  mColor.add(R.color.list_1);
		  mColor.add(R.color.list_2);
		  mColor.add(R.color.list_3);
		  mColor.add(R.color.list_4);
		
		searchlayout.setVisibility(View.GONE);
		
		select_area = SOTDatabase.getAreaCode();
		Log.d("select_area", " &&& "+ select_area);
		if (select_area.matches("")) {
			ed_areacode.setText("");
		} else {
			ed_areacode.setText(select_area);
		}
		
		select_van = SOTDatabase.getVandriver();
	
		if (select_van.matches("")) {
			ed_vancode.setText("");
		} else {
			ed_vancode.setText(select_van);
		}
		boolean showalllocatoin = FormSetterGetter.isShowAllLocation();
		//showalllocatoin = true;
		if (showalllocatoin == true) {
			locationcode_filter.setFocusableInTouchMode(true);
			locationcode_filter.setBackgroundResource(drawable.edittext_bg);
			locationcode_filter.setCursorVisible(true);
		}else{
			loccode = SalesOrderSetGet.getLocationcode();
			String locName = SalesOrderSetGet.getLocationname();
			locationcode_filter.setText(locName);
			locationcode_filter.setEnabled(false);
			locationcode_filter.setFocusableInTouchMode(false);
			locationcode_filter.setBackgroundResource(R.drawable.labelbg);
			locationcode_filter.setCursorVisible(false);
			locationcode_filter.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
		}
		commonAsyncCall common = new commonAsyncCall();
		common.execute();
		
		overdue_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				TextView invNo = (TextView) v.findViewById(R.id.invoice_no);
				TextView invDate = (TextView) v.findViewById(R.id.invoice_date);
				TextView custCode = (TextView) v.findViewById(R.id.customer_code);
				TextView custName = (TextView) v.findViewById(R.id.customer_name);
				
				invNoStr = invNo.getText().toString();
				invDateStr = invDate.getText().toString();
				custCodeStr = custCode.getText().toString();
				custNameStr = custName.getText().toString();
				
				helper.showProgressDialog(R.string.invoice_printpreview);
				AsyncPrintCall task = new AsyncPrintCall();
				task.execute();
			}
		});
		
		final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				startCalendar.set(Calendar.YEAR, year);
				startCalendar.set(Calendar.MONTH, monthOfYear);
				startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				strtDate();
			}
		};
		
		final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {
		
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				endCalendar.set(Calendar.YEAR, year);
				endCalendar.set(Calendar.MONTH, monthOfYear);
				endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				edDate();
			}
		};
		
		starteditTextDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(OverdueHeader.this, startDate,
							startCalendar.get(Calendar.YEAR), startCalendar
									.get(Calendar.MONTH), startCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		
		endeditTextDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(OverdueHeader.this, endDate,
							endCalendar.get(Calendar.YEAR), endCalendar
									.get(Calendar.MONTH), endCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		
		searchIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				InputMethodManager inputMethodManager = (InputMethodManager) OverdueHeader.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);

				if (searchlayout.getVisibility() == View.VISIBLE) {
					searchlayout.setVisibility(View.GONE);
					inputMethodManager.hideSoftInputFromWindow(ed_custcode.getWindowToken(), 0);
				} else {
					searchlayout.setVisibility(View.VISIBLE);
					ed_custcode.requestFocus();
					inputMethodManager.toggleSoftInputFromWindow(ed_custcode.getApplicationWindowToken(),
							InputMethodManager.SHOW_FORCED, 0);
				}
			}
		});
		locationcode_filter
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						locationcode_filter) {
					@Override
					public boolean onDrawableClick() {
						if(locationArrHm.size()>0){
							alertDialogLocation();
						}else{
							new ShowAllLocation().execute();
						}
						return true;
					}
				});
		ed_custcode.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(ed_custcode) {
			@Override
			public boolean onDrawableClick() {
				alertDialogCustomerSearch();
				return true;
			}
		});
		
		ed_areacode.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(ed_areacode) {
			@Override
			public boolean onDrawableClick() {
				ArrayList<HashMap<String, String>> areaArrHm = new ArrayList<HashMap<String, String>>();
				areaArrHm = SOTDatabase.getArea();
				if (areaArrHm.isEmpty()) {
					Log.d("al_areacode", "--->"+ arealist.toString());
					new Customer(OverdueHeader.this, ed_areacode, ed_description, arealist, "Area");
	
				} else {
					new Customer(OverdueHeader.this, ed_areacode, ed_description, areaArrHm, "Area");
					Log.d("areaArrHm", "--->"+ areaArrHm.size());
					Log.d("areaArrHm", "--->"+ areaArrHm);

				}
				return true;

			}

		});
		
		
		ed_vancode.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
				ed_vancode) {
			@Override
			public boolean onDrawableClick() {
				ArrayList<HashMap<String, String>> vanArrHm = new ArrayList<HashMap<String, String>>();
				vanArrHm.clear();
				vanArrHm = SOTDatabase.getVan();
				if (vanArrHm.isEmpty()) {
					Log.d("al_vancode", "--->"+ vanlist.toString());
					new Customer(OverdueHeader.this, ed_vancode, ed_vanname, vanlist, "Van");

				} else {
					new Customer(OverdueHeader.this, ed_vancode, ed_vanname, vanArrHm, "Van");
					Log.d("vanArrHm", "--->"+ vanArrHm.size());
					Log.d("vanArrHm", "--->"+ vanArrHm);

				}
				return true;

			}

		});
		
		bt_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				loadprogress();
				overdueAsyncCall overdue = new overdueAsyncCall();
				overdue.execute();
//				searchAsyncCall overdueSearch = new searchAsyncCall();
//				overdueSearch.execute();
			}
		});

		
	}
	
	
/** AsyncTask Strat**/
	
	public class commonAsyncCall extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			customerlist.clear();

			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("CompanyCode", companyCode);
			hm.put("CustomerCode", "");
			hm.put("NeedOutstandingAmount", "");
			hm.put("AreaCode", select_area);
			hm.put("VanCode", select_van);
			
			customerlist = SalesOrderWebService.getWholeCustomer(hm, "fncGetCustomer");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			commonAsyncCall2 call2 = new commonAsyncCall2();
			call2.execute();

		}
	}

	public class commonAsyncCall2 extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {

			arealist.clear();
			vanlist.clear();

		}

		@Override
		protected Void doInBackground(Void... arg0) {


			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("CompanyCode", companyCode);
			hm.put("CustomerCode", "");
			hm.put("NeedOutstandingAmount", "");
			hm.put("AreaCode", select_area);
			hm.put("VanCode", select_van);

			arealist = SalesOrderWebService.getAllArea("fncGetArea");
			vanlist = SalesOrderWebService.getAllVan("fncGetVan");
			serverdate = DateWebservice.getDateService("fncGetServerDate");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (serverdate != null) {
				starteditTextDate.setText(DateTime.date(serverdate));
				endeditTextDate.setText(serverdate);
				AsyncGeneralSettings generalAsync = new AsyncGeneralSettings();
				generalAsync.execute();
			}
		}
	}
	
	private class AsyncGeneralSettings extends AsyncTask<Void, Void, Void> {
		String jsonString="";
		JSONObject jsonResponse=null;
		JSONArray jsonMainNode=null;
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				 jsonString = WebServiceClass.URLService("fncGetGeneralSettings");
			

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("JsonArray");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode;

					jsonChildNode = jsonMainNode.getJSONObject(i);

					String SettingID = jsonChildNode.optString("SettingID")
							.toString();

					if (SettingID.matches("APPPRINTGROUP")) {
						String settingValue = jsonChildNode.optString(
								"SettingValue").toString();

						if (settingValue.matches("C")) {
							gnrlStngs = settingValue;
							Log.d("result ", gnrlStngs);
						} else if (settingValue.matches("S")) {
							gnrlStngs = settingValue;
							Log.d("result ", gnrlStngs);
						} else if (settingValue.matches("N")) {
							gnrlStngs = settingValue;
							Log.d("result ", gnrlStngs);
						} else {
							gnrlStngs = settingValue;
							Log.d("result ", gnrlStngs);
						}

					}

				}

			} catch (JSONException e) {

				e.printStackTrace();
			
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			APPPrintGroup apprintgroup = new APPPrintGroup();
			apprintgroup.execute();

		}

	}
	
	private class APPPrintGroup extends AsyncTask<Void, Void, Void> {

		String jsonString="";
		JSONObject jsonResponse =null;
		JSONArray jsonMainNode =null;
		
		@Override
		protected void onPreExecute() {
			sortproduct.clear();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if (gnrlStngs.matches("C")) {
								   
				jsonString = WebServiceClass.URLService("fncGetCategory");
				Log.d("jsonString ", jsonString);

				try {

					jsonResponse = new JSONObject(jsonString);
					jsonMainNode = jsonResponse.optJSONArray("JsonArray");

				} catch (JSONException e) {

					e.printStackTrace();
				}
				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode;

					try {
						jsonChildNode = jsonMainNode.getJSONObject(i);

						String categorycode = jsonChildNode.optString(
								"CategoryCode").toString();

						sortproduct.add(categorycode);
					} catch (JSONException e) {

						e.printStackTrace();
					}
				}

			} else if (gnrlStngs.matches("S")) {
								    
				jsonString = WebServiceClass.URLService("fncGetSubCategory");											    
				Log.d("jsonString ", jsonString);
		

				try {

					jsonResponse = new JSONObject(jsonString);
					jsonMainNode = jsonResponse.optJSONArray("JsonArray");

					int lengJsonArr = jsonMainNode.length();
					for (int i = 0; i < lengJsonArr; i++) {

						JSONObject jsonChildNode;

						try {
							jsonChildNode = jsonMainNode.getJSONObject(i);

							String subcategorycode = jsonChildNode.optString(
									"SubCategoryCode").toString();

							sortproduct.add(subcategorycode);
						} catch (JSONException e) {

							e.printStackTrace();
						}
					}
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			overdueAsyncCall overdue = new overdueAsyncCall();
			overdue.execute();
		}

	}
	
	private class overdueAsyncCall extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			
			overduelistArray.clear();
			select_customer=ed_custcode.getText().toString();
			select_sdate=starteditTextDate.getText().toString();;
			select_edata=endeditTextDate.getText().toString();
			select_area= ed_areacode.getText().toString();
			select_van=ed_vancode.getText().toString();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {

				HashMap<String, String> hmvalue = new HashMap<String, String>();
				hmvalue.put("CompanyCode", companyCode);
				hmvalue.put("CustomerCode", select_customer);
				hmvalue.put("Fromdate", select_sdate);
				hmvalue.put("ToDate", select_edata);
				hmvalue.put("AreaCode", select_area);
				hmvalue.put("VanCode", select_van);
				hmvalue.put("LocationCode", loccode);
				overduelistArray = SalesProductWebService.getInvoiceOverdue(hmvalue, "fncGetInvoiceHeaderByTerms");
			
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("overduelistArray Result", overduelistArray.toString());
			
			if (!overduelistArray.isEmpty()) {
				try {
					headerCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				overduelistArray.clear();
				try {
					headerCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Toast.makeText(OverdueHeader.this, "No matches found",
						Toast.LENGTH_SHORT).show();
			}
			
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(overdue_parent, true);
		}
	}
	
	public void headerCustCode() throws ParseException {
		
		overdueAdapter = new OverdueAdapter(OverdueHeader.this, R.layout.overdue_list_item, overduelistArray);
		overdue_list.setAdapter(overdueAdapter);
	
}

	
//	private class searchAsyncCall extends AsyncTask<Void, Void, Void> {
//		@Override
//		protected void onPreExecute() {
//			
//			overduelistArray.clear();		
//		}
//
//		@Override
//		protected Void doInBackground(Void... arg0) {
//
//			try {
//				
//				
//				select_customer=ed_custcode.getText().toString();
//				select_sdate=starteditTextDate.getText().toString();;
//				select_edata=endeditTextDate.getText().toString();
//				select_area=ed_areacode.getText().toString();
//				select_van=ed_vancode.getText().toString();
//	
//				HashMap<String, String> hmvalue = new HashMap<String, String>();
//				hmvalue.put("CompanyCode", comapanyCode);
//				hmvalue.put("CustomerCode", select_customer);
//				hmvalue.put("Fromdate", select_sdate);
//				hmvalue.put("ToDate", select_edata);
//				hmvalue.put("AreaCode", select_area);
//				hmvalue.put("VanCode", select_van);
//				
//				overduelistArray = SalesProductWebService.getInvoiceOverdue(hmvalue, "fncGetInvoiceHeaderByTerms");
//			
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//
//			if (!overduelistArray.isEmpty()) {
//				try {
//					headerCustCode();
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//			} else {
//				overduelistArray.clear();
//				try {
//					headerCustCode();
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				Toast.makeText(OverdueHeader.this, "No matches found",
//						Toast.LENGTH_SHORT).show();
//			}
//
//			progressBar.setVisibility(View.GONE);
//			spinnerLayout.setVisibility(View.GONE);
//			enableViews(overdue_parent, true);
//		}
//	}
	

//	public void searchCustCode() throws ParseException {
//
//		if (!overduelistArray.isEmpty()) {  
//			overdueAdapter = new OverdueAdapter(OverdueHeader.this, R.layout.overdue_list_item, overduelistArray);
//			overdue_list.setAdapter(overdueAdapter);
//		}
//
//	}
	
	
	
	private class AsyncPrintCall extends AsyncTask<Void, Void, ArrayList<String>> {

	String jsonString = "", jsonStr = "",  custjsonStr="", jsonSt="", batchjsonStr ="";
	JSONObject jsonResponse, jsonResp, custjsonResp, jsonRespfooter, batch_jsonResp;
	JSONArray jsonMainNode, jsonSecNode, custjsonMainNode, jsonSecNodefooter, batch_jsonSecNode;
	
		@Override
		protected void onPreExecute() {
			product.clear();
			productdet.clear();
			footerArr.clear();
			product_batchArr.clear();
		}
		
		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {
		
			String decimalpts = ".00";
		
			HashMap<String, String> hashValue = new HashMap<String, String>();	
			hashValue.put("CompanyCode", companyCode);
			hashValue.put("InvoiceNo", invNoStr);
			
			HashMap<String, String> custhash = new HashMap<String, String>();			
			custhash.put("CompanyCode", companyCode);
			custhash.put("CustomerCode", custCodeStr);	
		
			HashMap<String, String> hashVl = new HashMap<String, String>();
			hashVl.put("CompanyCode", companyCode);
			
		//	 if (onlineMode.matches("True")) {
		//		    if (checkOffline == true) {	
		//		      //Offline
		//				jsonString = offlineDatabase.getInvoiceHeaderDetail(hashValue);					    
		//				jsonStr = offlineDatabase.getInvoiceHeader(hashValue);					    
		//				custjsonStr = offlineDatabase.getCustomerDetail(custhash);				      
		//		    
		//	
		//		    } else { // online				    
						jsonString = SalesOrderWebService.getSODetail(hashValue,"fncGetInvoiceDetail");
						jsonStr = SalesOrderWebService.getSODetail(hashValue,"fncGetInvoiceHeaderByInvoiceNo");						
						custjsonStr = SalesOrderWebService.getSODetail(custhash,"fncGetCustomer");						
						jsonSt = SalesOrderWebService.getSODetail(hashVl,"fncGetMobilePrintFooter");
		//		    }
		//
		//		   } else if (onlineMode.matches("False")) { // perman offline
		//		    
		//			jsonString = offlineDatabase.getInvoiceHeaderDetail(hashValue);				    
		//			jsonStr = offlineDatabase.getInvoiceHeader(hashValue);				    
		//			custjsonStr = offlineDatabase.getCustomerDetail(custhash);				
		//
		//		   }
		
		
		
			Log.d("jsonString ", jsonString);
			Log.d("jsonStr ", jsonStr);
			Log.d("custjsonStr ", custjsonStr);
		
			Log.d("jsonSt", jsonSt);
			
			try {
		
				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");
		
				jsonResp = new JSONObject(jsonStr);
				jsonSecNode = jsonResp.optJSONArray("SODetails");
				
				custjsonResp = new JSONObject(custjsonStr);
				custjsonMainNode = custjsonResp.optJSONArray("SODetails");
				
				jsonRespfooter = new JSONObject(jsonSt);
				jsonSecNodefooter = jsonRespfooter.optJSONArray("SODetails");
		
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
			/*********** Process each JSON Node ************/
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode;
				ProductDetails productdetail = new ProductDetails();
				try {
					jsonChildNode = jsonMainNode.getJSONObject(i);
					int s = i + 1;
					productdetail.setSno(String.valueOf(s));
					
					String slNo = jsonChildNode.optString("slNo").toString();
					String productCode = jsonChildNode.optString("ProductCode").toString();					
					productdetail.setItemcode(productCode);
					
//					if (onlineMode.matches("True")) {
//						 if (checkOffline == true) {
//							// Offline
//						 }else{				
							// Online
					// Show batch number
					
					String showbatch = MobileSettingsSetterGetter.getShowBatchDetails();
					
					if(showbatch.matches("True")){
											
					hashValue.put("slNo", slNo);
					hashValue.put("ProductCode", productCode);					
					Log.d("hashValue ", ""+hashValue);					
					batchjsonStr = SalesOrderWebService.getSODetail(hashValue,"fncGetInvoiceBatchDetail");					
					Log.d("batchjsonStr ", "bat test "+batchjsonStr);
					try {												
						batch_jsonResp = new JSONObject(batchjsonStr);
						batch_jsonSecNode = batch_jsonResp.optJSONArray("SODetails");
		
					} catch (JSONException e) {
						
						e.printStackTrace();
					}
		
					int batch_lengthJsonArr = batch_jsonSecNode.length();
					for (int k = 0; k < batch_lengthJsonArr; k++) {
						/****** Get Object for each JSON node. ***********/					
						ProductDetails prodBatch = new ProductDetails();
						try {
							JSONObject batch_jsonChildNode = batch_jsonSecNode.getJSONObject(k);
							prodBatch.setProduct_batchno(batch_jsonChildNode.optString(
									"BatchNo").toString());
							prodBatch.setBatch_productcode(batch_jsonChildNode.optString(
									"ProductCode").toString());
							
							product_batchArr.add(prodBatch);
							
							Log.d("BatchNo", batch_jsonChildNode.optString(
									"BatchNo").toString());
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
					}
					
					}
//						 }
//					}
					productdetail.setDescription(jsonChildNode.optString(
							"ProductName").toString());
		
					String invoiceqty = jsonChildNode.optString("Qty")
							.toString();
					if (invoiceqty.contains(".")) {
						StringTokenizer tokens = new StringTokenizer(
								invoiceqty, ".");
						String qty = tokens.nextToken();
						productdetail.setQty(qty);
					} else {
						productdetail.setQty(invoiceqty);
					}
		
					String pricevalue = jsonChildNode.optString("Price")
							.toString();
					String totalvalve = jsonChildNode.optString("Total")
							.toString();
		
					String issueQty = jsonChildNode.optString("IssueQty").toString();
				    String returnQty = jsonChildNode.optString("ReturnQty").toString();	
				    
				    if (issueQty!=null && !issueQty.isEmpty()) {
				    	
						productdetail.setIssueQty(issueQty.split("\\.")[0]);
					}
					else{
						productdetail.setIssueQty("0");
					}
				    if (returnQty!=null && !returnQty.isEmpty()) {
				    	
						productdetail.setReturnQty(returnQty.split("\\.")[0]);
					}
					else{
						productdetail.setReturnQty("0");
					}
					
					productdetail.setFocqty(jsonChildNode.optDouble("FOCQty"));
					
					productdetail.setExchangeqty(jsonChildNode.optDouble("ExchangeQty"));
					if(pricevalue.contains(".")){
						productdetail.setPrice(pricevalue);
					}else{
						productdetail.setPrice(pricevalue + decimalpts);
					}
					if (totalvalve.contains(".")) {
						
						productdetail.setTotal(totalvalve);
					} else {
						
						productdetail.setTotal(totalvalve + decimalpts);
					}
					if (gnrlStngs.matches("C")) {
						productdetail.setSortproduct(jsonChildNode.optString(
								"CategoryCode").toString());
					} else if (gnrlStngs.matches("S")) {
						productdetail.setSortproduct(jsonChildNode.optString(
								"SubCategoryCode").toString());
					} else if (gnrlStngs.matches("N")) {
						productdetail.setSortproduct("");
					} else {
						productdetail.setSortproduct("");
					}
		
					product.add(productdetail);
		
				} catch (JSONException e) {
		
					e.printStackTrace();
				}
			}
		
			int lengJsonArr = jsonSecNode.length();
			for (int i = 0; i < lengJsonArr; i++) {
		
				JSONObject jsonChildNode;
				ProductDetails productdetail = new ProductDetails();
				try {
					jsonChildNode = jsonSecNode.getJSONObject(i);
		
					String itemdiscvalue = jsonChildNode.optString(
							"ItemDiscount").toString();
					String billdiscvalue = jsonChildNode.optString(
							"BillDIscount").toString();
					String subtotalvalue = jsonChildNode.optString("SubTotal")
							.toString();
		
					productdetail.setTax(twoDecimalPoint(jsonChildNode
							.optDouble("Tax")));
					String nettotalvalue = jsonChildNode.optString("NetTotal")
							.toString();
		
					if (itemdiscvalue.contains(".")
							&& billdiscvalue.contains(".")
							&& subtotalvalue.contains(".")
							&& nettotalvalue.contains(".")) {
						productdetail.setItemdisc(itemdiscvalue);
						productdetail.setBilldisc(billdiscvalue);
						productdetail.setSubtotal(subtotalvalue);
						productdetail.setNettot(nettotalvalue);
					} else {
						productdetail.setItemdisc(itemdiscvalue + decimalpts);
						productdetail.setBilldisc(billdiscvalue + decimalpts);
						productdetail.setSubtotal(subtotalvalue + decimalpts);
						productdetail.setNettot(nettotalvalue + decimalpts);
					}
					productdetail.setRemarks(jsonChildNode.optString("Remarks")
							.toString());
					productdetail.setTotaloutstanding(jsonChildNode.optString(
							"TotalBalance").toString());
					productdet.add(productdetail);
		
				} catch (JSONException e) {
		
					e.printStackTrace();
				}
			}
						
			int custJsonArr = custjsonMainNode.length();
			for (int i = 0; i < custJsonArr; i++) {
		
				JSONObject jsonChildNode;
			
				try {
					jsonChildNode = custjsonMainNode.getJSONObject(i);
		
					String CustomerCode = jsonChildNode.optString(
							"CustomerCode").toString();
					String CustomerName = jsonChildNode.optString(
							"CustomerName").toString();
					String Address1 = jsonChildNode.optString(
							"Address1").toString();
					String Address2 = jsonChildNode.optString(
							"Address2").toString();
					String Address3 = jsonChildNode.optString(
							"Address3").toString();
					String PhoneNo = jsonChildNode.optString(
							"PhoneNo").toString();
					String HandphoneNo = jsonChildNode.optString(
							"HandphoneNo").toString();
					String Email = jsonChildNode.optString(
							"Email").toString();
					String TermName = jsonChildNode.optString(
							"TermName").toString();
					String OutstandingAmount = jsonChildNode.optString(
							"OutstandingAmount").toString();
										
					CustomerSetterGetter.setCustomerCode(CustomerCode);
					CustomerSetterGetter.setCustomerName(CustomerName);
					CustomerSetterGetter.setCustomerAddress1(Address1);
					CustomerSetterGetter.setCustomerAddress2(Address2);
					CustomerSetterGetter.setCustomerAddress3(Address3);
					CustomerSetterGetter.setCustomerPhone(PhoneNo);
					CustomerSetterGetter.setCustomerHP(HandphoneNo);
					CustomerSetterGetter.setCustomerEmail(Email);
					CustomerSetterGetter.setCustomerTerms(TermName);
					CustomerSetterGetter.setTotalOutstanding(OutstandingAmount);
					
					Log.d("mobile settings", CustomerSetterGetter.getCustomerCode());
		
				} catch (JSONException e) {
		
					e.printStackTrace();
				}
			}
			
			/*********** Print Footer  ************/
			int lengJsonArr1 = jsonSecNodefooter.length();
			for (int i = 0; i < lengJsonArr1; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode;
				ProductDetails productdetail = new ProductDetails();
				try {
					jsonChildNode = jsonSecNodefooter.getJSONObject(i);
		
					String ReceiptMessage = jsonChildNode.optString(
							"ReceiptMessage").toString();
					String SortOrder = jsonChildNode.optString(
							"SortOrder").toString();
					
					productdetail.setReceiptMessage(ReceiptMessage);
					productdetail.setSortOrder(SortOrder);
					footerArr.add(productdetail);
		
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			hashVl.clear();
			hashValue.clear();
			return null;
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> result) {
		
//			if (printid == R.id.printer) {
//			
//				try {
//					Log.d("isPrint", "isPrint");
//					printid = 0;
//					sortCatagory();
//					if(isInvoicePrint){
//						print();
//					}else{
//						invoiceOnDeliveryOrderPrint();
//					}
//					
//				} catch (IOException e) {
//		
//				}
//			} else {
				Log.d("receiptprintpreview", "receiptprintpreview");
				Log.d("product", "" + product);
				Log.d("productdet", "" + productdet);
				helper.dismissProgressDialog();
				Intent i = new Intent(OverdueHeader.this, InvoicePrintPreview.class);
				i.putExtra("invNo", invNoStr);
				i.putExtra("invDate", invDateStr);
				i.putExtra("customerCode", custCodeStr);
				i.putExtra("customerName", custNameStr);
				i.putExtra("sort", sortproduct);
				i.putExtra("gnrlStngs", gnrlStngs);
				i.putExtra("tranType","COI");
				i.putExtra("dono","");
				i.putExtra("Invoicetype","Consignment");
				PreviewPojo.setProducts(product);
				PreviewPojo.setProductsDetails(productdet);
				startActivity(i);
		
				
//			}
		}
		
		}
	
/** Adapter Class Start  **/
	
	public class OverdueAdapter extends BaseAdapter {
		Context ctx;
		ArrayList<SO> listarray = new ArrayList<SO>();
		private LayoutInflater mInflater = null;
		int resource;

		public OverdueAdapter(Activity activty, int resource, ArrayList<SO> list) {
			this.listarray.clear();
			this.ctx = activty;
			if(!list.isEmpty()){
				this.listarray = list;
			}	
			this.resource = resource;
			mInflater = activty.getLayoutInflater();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listarray.size();
		}

		@Override
		public SO getItem(int position) {
			// TODO Auto-generated method stub
			return listarray.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final SO so = listarray.get(position);
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(resource, null);
				holder.so_custcode = (TextView) convertView.findViewById(R.id.customer_code);
				holder.so_custname = (TextView) convertView.findViewById(R.id.customer_name);
				holder.so_invno = (TextView) convertView.findViewById(R.id.invoice_no);
				holder.so_invdate = (TextView) convertView.findViewById(R.id.invoice_date);
				holder.so_balamount = (TextView) convertView.findViewById(R.id.overdue_amount);
				holder.so_pending = (TextView) convertView.findViewById(R.id.pending);
				holder.slide_img = (ImageView) convertView.findViewById(R.id.slide_img);
				convertView.setTag(holder);
				convertView.setId(position);
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.so_custcode.setText(so.getCustomerCode());
			holder.so_custname.setText(so.getCustomerName());
			holder.so_invno.setText(so.getSno());
			holder.so_invdate.setText(so.getDate());
			holder.so_balamount.setText(so.getBalanceamount());
			holder.so_pending.setText(so.getPending());
			
			
			int count = mColor.size();
			   
			   if(position>=mColor.size()){
			    int pos = position % count ;
			   
			    int color = mColor.get(pos);
			   
			    holder.slide_img.setBackgroundResource(color);
			   }else{
			   
			    int color = mColor.get(position);
			   
			    holder.slide_img.setBackgroundResource(color);
			   }
			
			return convertView;
		}

		class ViewHolder {
			TextView so_custcode;
			TextView so_custname;
			TextView so_invno;
			TextView so_invdate;
			TextView so_balamount;
			TextView so_pending;
			ImageView slide_img;
		}

	}
	
	
	/** Adapter Class End   **/
	
	/** AlerDialog Start   **/
	
	public void alertDialogCustomerSearch() {
		textlength = 0;
		AlertDialog.Builder myDialog = new AlertDialog.Builder(OverdueHeader.this);
		final EditText editText = new EditText(OverdueHeader.this);
		final ListView listview = new ListView(OverdueHeader.this);
		LinearLayout layout = new LinearLayout(OverdueHeader.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Customer");
		editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,
				0, 0);
		layout.addView(editText);
		layout.addView(listview);
		myDialog.setView(layout);

		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					customerDialog
							.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		customerAdapter = new CustomAlertAdapterSupp(OverdueHeader.this, customerlist);
		listview.setAdapter(customerAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				customerDialog.dismiss();
				getCustomerArraylsit = customerAdapter.getArrayList();
				HashMap<String, String> datavalue = getCustomerArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String keyValues = (String) mapEntry.getKey();
					mapEntry.getValue();
					ed_custcode.setText(keyValues);
					ed_custcode.addTextChangedListener(new TextWatcher() {
						@Override
						public void afterTextChanged(Editable s) {

						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {
						}

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {
							textlength = ed_custcode.getText().length();
						}
					});
				}
			}
		});

		customerResults = new ArrayList<HashMap<String, String>>(customerlist);
		editText.addTextChangedListener(new TextWatcher() {
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
				textlength = editText.getText().length();
				customerResults.clear();
				for (int i = 0; i < customerlist.size(); i++) {
					String supplierName = customerlist.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							customerResults.add(customerlist.get(i));
					}
				}

				customerAdapter = new CustomAlertAdapterSupp(OverdueHeader.this, customerResults);
				listview.setAdapter(customerAdapter);
			}
		});
		myDialog.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		customerDialog = myDialog.show();
	}
	
	/** AlertDialog End    **/
	private class ShowAllLocation extends AsyncTask<String,String,String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			helper.showProgressView(overdue_parent);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				HashMap<String, String> hashValue = new HashMap<String, String>();
				hashValue.put("CompanyCode", companyCode);
				String jsonString = SalesOrderWebService.getSODetail(hashValue, "fncGetLocation");
				if (jsonString != null && !jsonString.isEmpty()) {
					JSONObject jsonResponse = new JSONObject(jsonString);
					JSONArray jsonMainNode = jsonResponse.optJSONArray("SODetails");
					int lengJsonArray = jsonMainNode.length();
					if (lengJsonArray > 0) {
						for (int i = 0; i < lengJsonArray; i++) {
							JSONObject jsonObject = jsonMainNode.getJSONObject(i);
							String locationCode = jsonObject.getString("LocationCode");
							String locationName = jsonObject.getString("LocationName");
							HashMap<String, String> locationhm = new HashMap<String, String>();
							locationhm.put(locationCode, locationName);
							locationArrHm.add(locationhm);
						}
					}
				}
			}catch (Exception e){
				e.printStackTrace();

			}
			return null;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			Log.d("locationArrHm","-->"+locationArrHm.size());
			helper.dismissProgressView(overdue_parent);
			alertDialogLocation();
		}
	}
	public void alertDialogLocation() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				OverdueHeader.this);
		final EditText editText = new EditText(OverdueHeader.this);
		final ListView listview = new ListView(OverdueHeader.this);
		LinearLayout layout = new LinearLayout(OverdueHeader.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle(getResources().getString(R.string.location));
		editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,
				0, 0);
		layout.addView(editText);
		layout.addView(listview);
		myDialog.setView(layout);

		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(OverdueHeader.this,
				locationArrHm);
		listview.setAdapter(arrayAdapterSupp);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {

				myalertDialog.dismiss();
				getArraylsit = arrayAdapterSupp.getArrayList();
				HashMap<String, String> datavalue = getArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					loccode = (String) mapEntry.getKey();
					String locationName = (String) mapEntry.getValue();
					locationcode_filter.setText(locationName);

					locationcode_filter.addTextChangedListener(new TextWatcher() {
						@Override
						public void afterTextChanged(Editable s) {

						}

						@Override
						public void beforeTextChanged(CharSequence s,
													  int start, int count, int after) {
						}

						@Override
						public void onTextChanged(CharSequence s, int start,
												  int before, int count) {
							textlength = locationcode_filter.getText().length();
						}
					});
				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(locationArrHm);
		editText.addTextChangedListener(new TextWatcher() {
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
				textlength = editText.getText().length();
				searchResults.clear();
				for (int i = 0; i < locationArrHm.size(); i++) {
					String supplierName = locationArrHm.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(locationArrHm.get(i));
					}
				}

				arrayAdapterSupp = new CustomAlertAdapterSupp(
						OverdueHeader.this, searchResults);
				listview.setAdapter(arrayAdapterSupp);
			}
		});
		myDialog.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		myalertDialog = myDialog.show();
	}
	public void loadprogress(){
		spinnerLayout = new LinearLayout(OverdueHeader.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(overdue_parent, false);
		progressBar = new ProgressBar(OverdueHeader.this);
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
	
	private void strtDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		starteditTextDate.setText(sdf.format(startCalendar.getTime()));
	}

	private void edDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		endeditTextDate.setText(sdf.format(endCalendar.getTime()));
	}
	
	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tax = df.format(d);
		return tax;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(String item) {
		// TODO Auto-generated method stub
		menu.toggle();
	}
	
	@Override
	public void onBackPressed() {		
		 Intent i = new Intent(OverdueHeader.this, LandingActivity.class);
		 startActivity(i);
		 OverdueHeader.this.finish();
	}

}
