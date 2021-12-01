package com.winapp.sot;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.GetUserPermission;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.DateTime;
import com.winapp.printer.CommonPreviewPrint;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.UIHelper;

public class StockRequestHeader extends 
SherlockFragmentActivity implements
SlideMenuFragment.MenuClickInterFace {

	ImageButton search, back, printer, addnew;
	LinearLayout searchlayout, stockR_parent, spinnerLayout;
	EditText edtexttoloc, starteditTextDate, endeditTextDate;
	Button btsearch;
	ListView lv;
	String fromlocation, serverdate, valid_url, fromdate, todate, tolocation,
			fromlocation_code, tolocation_code, status, statusvalue;
	int month, day, year, printid;
	Calendar startCalendar, endCalendar;
	Spinner spinner_loc, spinner_status;
	ProgressBar progressBar;
	String sosno, sodate, sofromlocation, soamount, sostatus, sotolocation,sosts,so_remarks;
	private UIHelper helper;
	List<String> arr_loc;
	HeaderAdapter sradapter;
	String[] str_arr_loc;
	Set<String> hashset_loc;
	ArrayList<SO> searchArr;
	ArrayList<SO> headerArr;
	static String headeresult;
	Cursor cursor;
	ArrayList<ProductDetails> product;
	String jsonString = null, jsonStr = null;
	JSONObject jsonResponse, jsonResp;
	JSONArray jsonMainNode, jsonSecNode;
	HashMap<String, String> hashValue = new HashMap<String, String>();
	static ArrayList<HashMap<String, String>> stockreqDetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> stockreqHeadersArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> stockreqBatchDetailArr = new ArrayList<HashMap<String, String>>();
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetStockRequestHeader";
	private static String NAMESPACE = "http://tempuri.org/";
	private SlidingMenu menu;
	String transferStr,toLocationCode="";
	TextView listing_screen, customer_screen, addProduct_screen,summary_screen;
	LinearLayout customer_layout;
	ArrayList<HashMap<String, String>> searchProductArr = new ArrayList<HashMap<String, String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 
		  getActionBar().setIcon(drawable.ic_menu);
		  Mint.initAndStartSession(StockRequestHeader.this, "29088aa0");
		  setContentView(R.layout.stockrequest_header);

		  ActionBar ab = getSupportActionBar();
		  ab.setHomeButtonEnabled(true);
		  View customNav = LayoutInflater.from(this).inflate(
		    R.layout.slidemenu_actionbar_title, null);
		  TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		  txt.setText("Stock Request");
		  search = (ImageButton) customNav.findViewById(R.id.search_img);
		  printer = (ImageButton) customNav.findViewById(R.id.printer);
		  addnew = (ImageButton) customNav
		    .findViewById(R.id.custcode_img);

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
			
		btsearch = (Button) findViewById(R.id.btsearch);
		lv = (ListView) findViewById(R.id.listView1);
		stockR_parent = (LinearLayout) findViewById(R.id.stockR_parent);
		searchlayout = (LinearLayout) findViewById(R.id.searchlayout);
		edtexttoloc = (EditText) findViewById(R.id.toeditTextLoc);
		starteditTextDate = (EditText) findViewById(R.id.starteditTextDate);
		endeditTextDate = (EditText) findViewById(R.id.endeditTextDate);
		spinner_loc = (Spinner) findViewById(R.id.stockrequest_spfromLoc);
		spinner_status = (Spinner) findViewById(R.id.stockrequest_status);

		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen= (TextView) findViewById(R.id.sum_screen);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);

		customer_layout.setVisibility(View.GONE);
		customer_screen.setVisibility(View.GONE);
		addProduct_screen.setVisibility(View.GONE);

		listing_screen.setText("Request Receive");
		summary_screen.setText("Request Sent");

		listing_screen.setTextColor(Color.parseColor("#FFFFFF"));
		listing_screen.setBackgroundResource(drawable.tab_select_left);
		SupplierSetterGetter.setLocationCheck("ToLocation");

		SOTDatabase.init(StockRequestHeader.this);
		
		startCalendar = Calendar.getInstance();
		endCalendar = Calendar.getInstance();
		product = new ArrayList<ProductDetails>();
		helper = new UIHelper(StockRequestHeader.this);
		hashset_loc = new HashSet<String>();
		arr_loc = new ArrayList<String>();
		arr_loc.clear();
		toLocationCode = SalesOrderSetGet.getLocationcode();
		FWMSSettingsDatabase.init(StockRequestHeader.this);
		
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,StockRequestHeader.this);
		new SalesOrderWebService(valid_url);
		searchArr = new ArrayList<SO>();
		searchlayout.setVisibility(View.GONE);
		fromlocation = SupplierSetterGetter.getLocationcode();
		edtexttoloc.setText(fromlocation);

		arr_loc = SalesOrderSetGet.getFrom_location();

		for (int i = 0; i < arr_loc.size(); i++) {
			if (arr_loc.get(i).contains(fromlocation)) {
				arr_loc.remove(fromlocation);
			}
		}
		spinner_loc.setAdapter(new SpinnerAdapter(StockRequestHeader.this,
				R.layout.row, arr_loc));

		AsyncCallWSServerDate serverdateAO = new AsyncCallWSServerDate();
		serverdateAO.execute();
		registerForContextMenu(lv);
	
		printer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				printid = v.getId();
				SOTDatabase.deleteImage();
				cursor = FWMSSettingsDatabase.getPrinter();
				if (cursor.getCount() != 0) {
					if (RowItem.getPrintoption().equals("True")) {
						SO so = sradapter.getItem(sradapter
								.getSelectedPosition());
						sosno = so.getSno().toString();
						helper.showProgressDialog(R.string.generating_sr);
						AsyncPrintCall task = new AsyncPrintCall();
						task.execute();
					} else {
						Toast.makeText(StockRequestHeader.this,
								"Please Enable CheckBox", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(StockRequestHeader.this,
							"Please Configure the printer", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		addnew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

//				SOTDatabase.deleteImage();
				clearData();
				Intent calllanding = new Intent(StockRequestHeader.this,
						StockRequestHeaderDetail.class);
				startActivity(calllanding);
				finish();

			}
		});
		btsearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				fromdate = starteditTextDate.getText().toString();
				todate = endeditTextDate.getText().toString();

				if (fromdate.matches("")) {
					Toast.makeText(StockRequestHeader.this, "Enter Start Date",
							Toast.LENGTH_SHORT).show();
				} else if (todate.matches("")) {
					Toast.makeText(StockRequestHeader.this, "Enter End Date",
							Toast.LENGTH_SHORT).show();
				} 
//				else if (spinner_loc.getSelectedItem().toString()
//						.matches("Select Location")) {
//					Toast.makeText(getApplicationContext(),
//							"Please Select Location", Toast.LENGTH_SHORT)
//							.show();
//				} 
				else {
					summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
					summary_screen.setBackgroundResource(drawable.rounded_tab_right);

					listing_screen.setTextColor(Color.parseColor("#FFFFFF"));
					listing_screen.setBackgroundResource(drawable.tab_select_left);

					onSetGet();
					AsyncCallSearchStockReq searchCustCode = new AsyncCallSearchStockReq();
					searchCustCode.execute();
				}
			}
		});
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (searchlayout.getVisibility() == View.VISIBLE) {
					// Its visible
					searchlayout.setVisibility(View.GONE);
				} else {
					searchlayout.setVisibility(View.VISIBLE);
					// Either gone or invisible
				}

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
					new DatePickerDialog(StockRequestHeader.this, startDate,
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
					new DatePickerDialog(StockRequestHeader.this, endDate,
							endCalendar.get(Calendar.YEAR), endCalendar
									.get(Calendar.MONTH), endCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});

		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startProgress();

				summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
				summary_screen.setBackgroundResource(drawable.rounded_tab_right);

				listing_screen.setTextColor(Color.parseColor("#FFFFFF"));
				listing_screen.setBackgroundResource(drawable.tab_select_left);

				AsyncCallStockReqHeader task = new AsyncCallStockReqHeader();
				task.execute("ToLocation");
				SupplierSetterGetter.setLocationCheck("ToLocation");
			}
		});

		summary_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startProgress();

				listing_screen.setTextColor(Color.parseColor("#FFFFFF"));
				listing_screen.setBackgroundResource(drawable.rounded_tab_left);

				summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
				summary_screen.setBackgroundResource(drawable.tab_select_right);

				AsyncCallStockReqHeader task = new AsyncCallStockReqHeader();
				task.execute("FromLocation");
				SupplierSetterGetter.setLocationCheck("FromLocation");
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		SO so = sradapter.getItem(info.position);
		sosno = so.getSno().toString();
		sodate = so.getDate().toString();
		sofromlocation = so.getFromlocation().toString();
		sotolocation = so.getTolocation().toString();
		so_remarks =so.getRemarks().toString();
		sosts = so.getStatus().toString();
		transferStr = FormSetterGetter.getTransfer();
		
		Log.i("sosts", "get"+sosts);
		
		menu.setHeaderTitle(sosno);

		menu.add(0, v.getId(), 0, "Print Preview");
		if (transferStr.matches("Transfer")) {
			
			if(!sosts.matches("Closed")){
				menu.add(0, v.getId(), 0, "Edit");
				if(SupplierSetterGetter.getLocationCheck().matches("ToLocation")){
					menu.add(0, v.getId(), 0, "Convert to transfer");
				}
			}

		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		if (item.getTitle() == "Print Preview") {
			helper.showProgressDialog(R.string.stockrequest_printpreview);
			new AsyncPrintCall().execute();
		}

		else if (item.getTitle() == "Convert to transfer") {

//			SOTDatabase.deleteImage();
//			SOTDatabase.deleteallbatch();
//			SalesOrderSetGet.setFromLoc("");
//			SalesOrderSetGet.setToLoc("");
//			ConvertToSetterGetter.setEdit_stockreq_no("");
//			ArrayList<HashMap<String, String>> empty = new ArrayList<HashMap<String, String>>();
//			empty.clear();
//			ConvertToSetterGetter.setStockRequestBatchDetailArr(empty);
			clearData();
			AsyncCallWSStockRequestDetail task = new AsyncCallWSStockRequestDetail();
			task.execute();
		}else if (item.getTitle() == "Edit") {

			clearData();

			new AsyncCallWSStockRequestDetailEdit().execute();
		}  else {
			return false;
		}
		return true;
	}

	public void clearData(){
		SOTDatabase.deleteImage();
		SOTDatabase.deleteallbatch();
		SalesOrderSetGet.setFromLoc("");
		SalesOrderSetGet.setToLoc("");
		ConvertToSetterGetter.setEdit_stockreq_no("");
		ArrayList<HashMap<String, String>> empty = new ArrayList<HashMap<String, String>>();
		empty.clear();
		ConvertToSetterGetter.setStockRequestBatchDetailArr(empty);
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
	
	private class AsyncCallWSStockRequestDetail extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			stockreqDetailsArr.clear();
			stockreqHeadersArr.clear();
			stockreqBatchDetailArr.clear();
			SalesOrderSetGet.setStocktotransfer("");
			spinnerLayout = new LinearLayout(StockRequestHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(stockR_parent, false);
			progressBar = new ProgressBar(StockRequestHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			HashMap<String, String> to_loccode = new HashMap<String, String>();
			
			to_loccode = SupplierSetterGetter.getLoc_code_name();
			String to_code = to_loccode.get(fromlocation);
			Log.d("tolocation_code", to_code);
			
			try {
				GetUserPermission.getLocationHaveBatchTransfer("fncGetLocation", to_code);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			stockreqDetailsArr = SalesOrderWebService.getStockReqDetails(sosno,
					"fncGetStockRequestDetail");
			stockreqHeadersArr = SalesOrderWebService.getStockReqHeader(sosno,
					"fncGetStockRequestHeaderByReqNo");			
			stockreqBatchDetailArr = SalesOrderWebService.getStockReqBatchDetail(sosno,
					"fncGetStockRequestBatchDetail");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
	
			String stocktotransfer = SalesOrderSetGet.getStocktotransfer();
			if(stocktotransfer.matches("False")){
				Toast.makeText(StockRequestHeader.this, "No quantity to transfer",
						Toast.LENGTH_SHORT).show();
			}else{
				PreviewPojo.setSearchProductArr(searchProductArr);
				Intent i = new Intent(StockRequestHeader.this, TransferSummary.class);
				i.putExtra("StockReqDetails", stockreqDetailsArr);
				i.putExtra("StockReqHeader", stockreqHeadersArr);
				i.putExtra("StockReqBatchDetail", stockreqBatchDetailArr);
				startActivity(i);
				StockRequestHeader.this.finish();
			}
							
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(stockR_parent, true);

		}
	}

	private class AsyncCallWSStockRequestDetailEdit extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			stockreqDetailsArr.clear();
			stockreqHeadersArr.clear();
			stockreqBatchDetailArr.clear();
			SalesOrderSetGet.setStocktotransfer("");
			spinnerLayout = new LinearLayout(StockRequestHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(stockR_parent, false);
			progressBar = new ProgressBar(StockRequestHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);

		}

		@Override
		protected Void doInBackground(Void... arg0) {

//			HashMap<String, String> to_loccode = new HashMap<String, String>();
//
//			to_loccode = SupplierSetterGetter.getLoc_code_name();
//			String to_code = to_loccode.get(fromlocation);
//			Log.d("tolocation_code", to_code);

			/*try {
				GetUserPermission.getLocationHaveBatchTransfer("fncGetLocation", to_code);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

			stockreqDetailsArr = SalesOrderWebService.getStockReqDetails(sosno,
					"fncGetStockRequestDetail");
			stockreqHeadersArr = SalesOrderWebService.getStockReqHeader(sosno,
					"fncGetStockRequestHeaderByReqNo");
			stockreqBatchDetailArr = SalesOrderWebService.getStockReqBatchDetail(sosno,
					"fncGetStockRequestBatchDetail");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

//			String stocktotransfer = SalesOrderSetGet.getStocktotransfer();
//			if(stocktotransfer.matches("False")){
//				Toast.makeText(StockRequestHeader.this, "No quantity to transfer",
//						Toast.LENGTH_SHORT).show();
//			}else{
				Intent i = new Intent(StockRequestHeader.this, StockRequestSummary.class);
				i.putExtra("StockReqDetails", stockreqDetailsArr);
				i.putExtra("StockReqHeader", stockreqHeadersArr);
				i.putExtra("StockReqBatchDetail", stockreqBatchDetailArr);
				startActivity(i);
				StockRequestHeader.this.finish();
//			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(stockR_parent, true);

		}
	}

	public class SpinnerAdapter extends ArrayAdapter<String> {
		List<String> arrlist = new ArrayList<String>();

		public SpinnerAdapter(Context context, int textViewResourceId,
				List<String> arr_loc) {

			super(context, textViewResourceId, arr_loc);
			arrlist.clear();
			arrlist = arr_loc;
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
			label.setText(arrlist.get(position));
			icon.setVisibility(View.GONE);

			return row;
		}
	}

	private class AsyncCallWSServerDate extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			spinnerLayout = new LinearLayout(StockRequestHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(stockR_parent, false);
			progressBar = new ProgressBar(StockRequestHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
			boolean check= isNetworkConnected();
			if(check == false){
				finish();
			}

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			serverdate = DateWebservice.getDateService("fncGetServerDate");
			SalesOrderSetGet.setSaleorderdate(serverdate);
			return null;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected void onPostExecute(Void result) {
			//try{
			String fromdate = null;
			if(serverdate.contains("-")){
				fromdate = DateTime.date_(serverdate);
			}else if(serverdate.contains("/")){
				fromdate = DateTime.date(serverdate);
			}

				if (serverdate != null) {
					if(fromdate.contains("-")){
						starteditTextDate.setText(DateTime.date_(fromdate));
					}else if(fromdate.contains("/")){
						starteditTextDate.setText(DateTime.date(fromdate));
					}
					endeditTextDate.setText(serverdate);
					AsyncCallStockReqHeader task = new AsyncCallStockReqHeader();
					task.execute("ToLocation");
				}else{
					progressBar.setVisibility(View.GONE);
					spinnerLayout.setVisibility(View.GONE);
					enableViews(stockR_parent, true);
				}
			/*}catch (Exception e){
				e.printStackTrace();
			}*/
		}
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
	}

	private class AsyncCallSearchStockReq extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(StockRequestHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(stockR_parent, false);
			progressBar = new ProgressBar(StockRequestHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			searchArr = SalesOrderWebService.SearchStockRequest(fromdate,
					todate, tolocation_code, fromlocation_code, status,
					"fncGetStockRequestHeader");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (!searchArr.isEmpty()) {
				try {
					searchCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				searchArr.clear();
				try {
					searchCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Toast.makeText(StockRequestHeader.this, "No matches found",
						Toast.LENGTH_SHORT).show();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(stockR_parent, true);

		}
	}

	public void searchCustCode() throws ParseException {

		sradapter = new HeaderAdapter(StockRequestHeader.this,
				R.layout.salesorder_list_item, null, searchArr);

		lv.setAdapter(sradapter);

	}

	private class  AsyncCallStockReqHeader extends AsyncTask<String, Void, Void> {

		String fromdt,todt;
		@Override
		protected void onPreExecute() {
			headerArr = new ArrayList<SO>();
			fromdt = starteditTextDate.getText().toString();
			todt = endeditTextDate.getText().toString();
		}

		@Override
		protected Void doInBackground(String... arg0) {

			String Location  = arg0[0];

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

			SoapObject request = new SoapObject(NAMESPACE, webMethName);

			PropertyInfo sDate = new PropertyInfo();
			PropertyInfo eDate = new PropertyInfo();
			PropertyInfo companyCode = new PropertyInfo();
			PropertyInfo toLoc = new PropertyInfo();
			PropertyInfo FromLoc = new PropertyInfo();


			toLocationCode = SalesOrderSetGet.getLocationcode();
			Log.d("toLocationCodeName", toLocationCode);
//			Log.d("fromdate", fromdate);
//			Log.d("todate", serverdate);

			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			sDate.setName("FromDate");
			sDate.setValue(fromdt);
			//sDate.setValue("12/06/2018 ");
			sDate.setType(String.class);
			request.addProperty(sDate);

			eDate.setName("ToDate");
			eDate.setValue(todt);
//			eDate.setValue("12/07/2018");
			eDate.setType(String.class);
			request.addProperty(eDate);


			Log.d("FromANDToDate",""+fromdt +" "+todt);

			if(Location.matches("ToLocation")){
				Log.d("ToLocationName",""+toLocationCode );
				toLoc.setName("ToLocation");
				toLoc.setValue(toLocationCode);
				toLoc.setType(String.class);
				request.addProperty(toLoc);
			}
			if(Location.matches("FromLocation")){
				Log.d("FromLocationNAme",""+toLocationCode);
				FromLoc.setName("FromLocation");
				FromLoc.setValue(toLocationCode);
				FromLoc.setType(String.class);
				request.addProperty(FromLoc);
			}




			String suppTxt = null;
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(
					valid_url);
			try {
				androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				suppTxt = response.toString();
				headeresult = " { StockReqHeader : " + suppTxt + "}";
				Log.d("headeresult", "" + headeresult);
				JSONObject jsonResponse;

				try {
					jsonResponse = new JSONObject(headeresult);
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("StockReqHeader");


					int lengthJsonArr = jsonMainNode.length();

					if (lengthJsonArr==0){
						Log.d("NoDataFound",""+"NoData");

					}else {
						for (int i = 0; i < lengthJsonArr; i++) {
							JSONObject jsonChildNode = jsonMainNode
									.getJSONObject(i);
							String ccSno = jsonChildNode.optString("StockReqNo")
									.toString();
							String ccDate = jsonChildNode.optString("StockReqDate")
									.toString();
							String fromloca = jsonChildNode.optString(
									"FromLocation").toString();
							String toloca = jsonChildNode.optString("ToLocation")
									.toString();
							String remarks =jsonChildNode.optString("Remarks").toString();
							String status = jsonChildNode.optString("Status")
									.toString();

							int status_pt =Integer.parseInt(status);
							SalesOrderSetGet.setRemarks(remarks);

							SO so = new SO();

							so.setRemarks(remarks);

							if (status_pt>=0) {

//							HashMap<String, String> location_code_name = new HashMap<String, String>();
//							location_code_name = SupplierSetterGetter.getLoc_code_name();
//							fromlocation_code = location_code_name.get(fromlocation);
//							if(fromlocation_code.matches(toloca)){
								so.setSno(ccSno);
								so.setFromlocation(fromloca);
								so.setTolocation(toloca);
//									so.setStatus("open");
								if (status.matches("0")) {
									so.setStatus("open");
								} else  {
									so.setStatus("closed");
								}

								if (ccDate != null) {
									StringTokenizer tokens = new StringTokenizer(
											ccDate, " ");
									String date = tokens.nextToken();
									so.setDate(date);
								} else {
									so.setDate(ccDate);
								}
								headerArr.add(so);
//							}
							}


						}
					}

				} catch (JSONException e) {

					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d("ArrayList", "" + headerArr);
			try {
				headerCustCode();
			} catch (ParseException e) {

				e.printStackTrace();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(stockR_parent, true);

		}
	}

	public void startProgress(){
		spinnerLayout = new LinearLayout(StockRequestHeader.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(stockR_parent, false);
		progressBar = new ProgressBar(StockRequestHeader.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		spinnerLayout.addView(progressBar);
	}

	public void headerCustCode() throws ParseException {
		if(headerArr.size()>0){
		sradapter = new HeaderAdapter(StockRequestHeader.this,
				R.layout.salesorder_list_item, null, headerArr);
		lv.setAdapter(sradapter);
		}
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

	public void onSetGet() {
		HashMap<String, String> location_code_name = new HashMap<String, String>();
		tolocation = spinner_loc.getSelectedItem().toString();

		statusvalue = spinner_status.getSelectedItem().toString();
		if (statusvalue.matches("Open")) {
			status = "0";
		}
		if (statusvalue.matches("InProgress")) {
			status = "1";
		}
		if (statusvalue.matches("Closed")) {
			status = "2";
		}
//		if (statusvalue.matches("Closed")) {
//			status = "3";
//		}
//		if (statusvalue.matches("Deleted")) {
//			status = "4";
//		}
//		if (statusvalue.matches("All")) {
//			   status = "5";
//			  }
		location_code_name = SupplierSetterGetter.getLoc_code_name();
		fromlocation_code = location_code_name.get(fromlocation);
		Log.d("fromlocation_code", fromlocation_code);

		tolocation_code = location_code_name.get(tolocation);
		
		if((tolocation_code != null && !tolocation_code.isEmpty())){
			
		}else{
			tolocation_code="";
		}

	}

	private class AsyncPrintCall extends
			AsyncTask<Void, Void, ArrayList<String>> {
		@Override
		protected void onPreExecute() {
			product.clear();
			Log.d("sno", sosno);
		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("StockReqNo", sosno);

			jsonString = SalesOrderWebService.getSODetail(hashValue,
					"fncGetStockRequestDetail");

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");

			} catch (JSONException e) {
				e.printStackTrace();
			}

			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode;
				ProductDetails productdetail = new ProductDetails();
				try {
					jsonChildNode = jsonMainNode.getJSONObject(i);

					productdetail.setItemcode(jsonChildNode.optString(
							"ProductCode").toString());
					productdetail.setDescription(jsonChildNode.optString(
							"ProductName").toString());

					String stckeqqty = jsonChildNode.optString("ReqQty")
							.toString();
					if (stckeqqty.contains(".")) {
						StringTokenizer tokens = new StringTokenizer(stckeqqty,
								".");
						String qty = tokens.nextToken();
						productdetail.setQty(qty);
					} else {
						productdetail.setQty(stckeqqty);
					}

					productdetail
							.setTotalqty(jsonChildNode.getDouble("ReqQty"));

					product.add(productdetail);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			hashValue.clear();
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			if (printid == R.id.printer) {
				try {
					printid = 0;
					print();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				helper.dismissProgressDialog();
				Intent i = new Intent(StockRequestHeader.this,
						CommonPreviewPrint.class);
				Log.d("RemarksStatus1","->"+so_remarks);
				i.putExtra("title", "StockRequest");
				i.putExtra("No", sosno);
				i.putExtra("Date", sodate);
				i.putExtra("customerCode", sofromlocation);
				i.putExtra("cus_remarks",so_remarks);
				i.putExtra("Invoicetype", "Consignment");
				i.putExtra("customerName", sotolocation);
				PreviewPojo.setProducts(product);
				startActivity(i);
//				finish();
			}
		}

	}

	private void print() throws IOException {
		String title = "STOCK REQUEST";
		helper.dismissProgressDialog();
		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		try {
			Printer printer = new Printer(StockRequestHeader.this, macaddress);
			if (sradapter.getSelectedPosition() != -1) {
				SO so = sradapter.getItem(sradapter.getSelectedPosition());

				sosno = so.getSno().toString();
				sodate = so.getDate().toString();
				sofromlocation = so.getFromlocation().toString();
				sotolocation = so.getTolocation().toString();
				so_remarks =so.getRemarks().toString();
				printer.printStockRequest(sosno, sodate, sofromlocation,
						sotolocation, product, title, 1,so_remarks);
			}
		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}
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
	public void showViews(boolean show, int... resId) {
		int visibility = show ? View.VISIBLE : View.GONE;
		for (int id : resId) {
			findViewById(id).setVisibility(visibility);
		}

	}

	public static void showViews(boolean show, View... views) {
		int visibility = show ? View.VISIBLE : View.GONE;
		for (View view : views) {
			view.setVisibility(visibility);
		}
	}

	@Override
	public void onBackPressed() {
		Intent calllanding = new Intent(StockRequestHeader.this,
				LandingActivity.class);
		startActivity(calllanding);
		finish();
	}
}
