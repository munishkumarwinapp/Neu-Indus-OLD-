package com.winapp.sot;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.DateTime;
import com.winapp.printcube.printcube.BluetoothService;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.printer.CommonPreviewPrint;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

public class TransferHeader extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace {
	ImageButton search, back, addnew, printer;
	LinearLayout searchlayout, stockR_parent, spinnerLayout;
	EditText edtextfromloc, starteditTextDate, endeditTextDate;
//	RadioButton transfer_on, transfer_off;
	Button btsearch;
	ListView lv;
	Calendar startCalendar, endCalendar;
	Spinner fromspinner, tospinner, statusspinner;
	ProgressBar progressBar;

	String strfromloc;
	String fromlocation, serverdate, valid_url, fromdate, todate, tolocation,
			fromlocation_code, tolocation_code, status;
	int month, day, year, printid;

	ArrayList<String> arr_from_loc = new ArrayList<String>();
	ArrayList<String> arr_to_loc = new ArrayList<String>();
	ArrayList<String> arr_status = new ArrayList<String>();

	String[] str_arr_loc;
	Set<String> hashset_loc;
	ArrayList<SO> searchArr;
	ArrayList<SO> headerArr;
	static String headeresult;
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetTransferHeader";
	private static String NAMESPACE = "http://tempuri.org/";
	HeaderAdapter transferadAdapter;
	private String sosno, sodate, sofromlocation, soamount, sostatus, sotolocation,tabcheck="OUT",so_remarks;
	private UIHelper helper;
	ArrayList<ProductDetails> product;
	ArrayList<HashMap<String, String>> searchProductArr = new ArrayList<HashMap<String, String>>();
	String jsonString = null, jsonStr = null,statusvalue="";
	JSONObject jsonResponse, jsonResp;
	JSONArray jsonMainNode, jsonSecNode;
	HashMap<String, String> hashValue = new HashMap<String, String>();
	Cursor cursor;
	private SlidingMenu menu;
	private TextView listing_screen, customer_screen, addProduct_screen,summary_screen;
	private LinearLayout customer_layout;
//	private String qty;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		Mint.initAndStartSession(TransferHeader.this, "29088aa0");
		setContentView(R.layout.transfer_header);
		
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Transfer");
		search = (ImageButton) customNav.findViewById(R.id.search_img);
		printer = (ImageButton) customNav.findViewById(R.id.printer);
		addnew = (ImageButton) customNav.findViewById(R.id.custcode_img);

		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);

//		ActionBar ab = getSupportActionBar();
//		ab.setHomeButtonEnabled(true);
//		View customNav = LayoutInflater.from(this).inflate(
//				R.layout.slidemenu_actionbar_title, null);
//		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
//		txt.setText("Transfer");
//		search = (ImageButton) customNav.findViewById(R.id.search_img);
//		printer = (ImageButton) customNav.findViewById(R.id.printer);
//		addnew = (ImageButton) customNav.findViewById(R.id.custcode_img);
//
//		ab.setCustomView(customNav);
//		ab.setDisplayShowCustomEnabled(true);

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

		GlobalData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		GlobalData.mService = new BluetoothService(this, mHandler);

		arr_from_loc.clear();
		arr_to_loc.clear();

		// search = (ImageButton) findViewById(R.id.search);
		// back = (ImageButton) findViewById(R.id.back);
		// printer = (ImageButton) findViewById(R.id.printer);
		btsearch = (Button) findViewById(R.id.btsearch);
		// addnew = (ImageButton) findViewById(R.id.addnew);
		stockR_parent = (LinearLayout) findViewById(R.id.stockR_parent);
		searchlayout = (LinearLayout) findViewById(R.id.searchlayout);
//		edtextfromloc = (EditText) findViewById(R.id.fromeditTextLoc);
		starteditTextDate = (EditText) findViewById(R.id.starteditTextDate);
		endeditTextDate = (EditText) findViewById(R.id.endeditTextDate);
//		transfer_on = (RadioButton) findViewById(R.id.transferon_radio_btn);
//		transfer_off = (RadioButton) findViewById(R.id.transferoff_radio_btn);
		fromspinner = (Spinner) findViewById(R.id.stockrequest_spFromLoc);
		tospinner = (Spinner) findViewById(R.id.stockrequest_spToLoc);
		statusspinner = (Spinner) findViewById(R.id.stockrequest_status);
		lv = (ListView) findViewById(R.id.listView1);
		startCalendar = Calendar.getInstance();
		endCalendar = Calendar.getInstance();
		product = new ArrayList<ProductDetails>();
		helper = new UIHelper(TransferHeader.this);
		hashset_loc = new HashSet<String>();
		FWMSSettingsDatabase.init(TransferHeader.this);
		SOTDatabase.init(TransferHeader.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,TransferHeader.this);
		new SalesOrderWebService(valid_url);
		searchlayout.setVisibility(View.GONE);
		strfromloc = SupplierSetterGetter.getLocationcode();

		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen= (TextView) findViewById(R.id.sum_screen);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);

		customer_layout.setVisibility(View.GONE);
		customer_screen.setVisibility(View.GONE);
		addProduct_screen.setVisibility(View.GONE);

		listing_screen.setText("Transfer IN");
		summary_screen.setText("Transfer OUT");

		summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
		summary_screen.setBackgroundResource(drawable.tab_select_right);

//		transfer_on.setChecked(true);
//		transfer_off.setChecked(false);

		arr_from_loc.add("Select");
		fromspinner.setAdapter(new SpinnerAdapter(TransferHeader.this,
				R.layout.row, arr_from_loc));
		fromspinner.setClickable(false);

		arr_to_loc.add(strfromloc);
		Log.d("arr_from_loc", arr_to_loc.toString());
		tospinner.setAdapter(new SpinnerAdapter(TransferHeader.this,
				R.layout.row, arr_to_loc));

		fromlocation=strfromloc;
		tolocation="Select";
		onSetGet();

		AsyncCallWSServerDate serverdateAO = new AsyncCallWSServerDate();
		serverdateAO.execute();
		registerForContextMenu(lv);

		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
				summary_screen.setBackgroundResource(drawable.rounded_tab_right);

				listing_screen.setTextColor(Color.parseColor("#FFFFFF"));
				listing_screen.setBackgroundResource(drawable.tab_select_left);
				tabcheck = "IN";
				fromlocation="Select";
				tolocation=strfromloc;
				searchResult();

			}
		});

		summary_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				listing_screen.setTextColor(Color.parseColor("#FFFFFF"));
				listing_screen.setBackgroundResource(drawable.rounded_tab_left);

				summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
				summary_screen.setBackgroundResource(drawable.tab_select_right);
				tabcheck = "OUT";
				fromlocation=strfromloc;
				tolocation="Select";
				searchResult();
			}
		});

		addnew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				SOTDatabase.deleteImage();
				ConvertToSetterGetter.setEdit_stockreq_no("");
				SalesOrderSetGet.setFromLoc("");
				SalesOrderSetGet.setToLoc("");
				ArrayList<HashMap<String, String>> empty = new ArrayList<HashMap<String, String>>();
				empty.clear();
				ConvertToSetterGetter.setStockRequestBatchDetailArr(empty);
				searchProductArr.clear();
				PreviewPojo.setSearchProductArr(searchProductArr);
				
				Intent calllanding = new Intent(TransferHeader.this,
						TransferHeaderDetail.class);
				startActivity(calllanding);
				finish();
			}
		});

		btsearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(tabcheck.matches("IN")){
					fromlocation="Select";
					tolocation=strfromloc;
				}else{
					fromlocation=strfromloc;
					tolocation="Select";
				}

				searchResult();
			}
		});

		/*transfer_on.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				RadioButton radio1 = (RadioButton) v;
				arr_from_loc.clear();
				arr_to_loc.clear();
				if (radio1.isChecked()) {

					transfer_on.setChecked(true);
					transfer_off.setChecked(false);
					arr_to_loc.add(strfromloc);
					tospinner.setAdapter(new SpinnerAdapter(
							TransferHeader.this, R.layout.row, arr_to_loc));
					tospinner.setClickable(true);
					fromspinner.setClickable(false);
					arr_from_loc.add("Select");
					Log.d("arr_from_loc", arr_from_loc.toString());
					fromspinner.setAdapter(new SpinnerAdapter(
							TransferHeader.this, R.layout.row, arr_from_loc));
				}
			}
		});*/

//		transfer_off.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				RadioButton radio2 = (RadioButton) v;
//				arr_from_loc.clear();
//				arr_to_loc.clear();
//				if (radio2.isChecked()) {
//					transfer_on.setChecked(false);
//					transfer_off.setChecked(true);
//					arr_from_loc.add(strfromloc);
//					fromspinner.setAdapter(new SpinnerAdapter(
//							TransferHeader.this, R.layout.row, arr_from_loc));
//					fromspinner.setClickable(true);
//					tospinner.setClickable(false);
//					arr_to_loc.add("Select");
//					Log.d("arr_to_loc", arr_to_loc.toString());
//					tospinner.setAdapter(new SpinnerAdapter(
//							TransferHeader.this, R.layout.row, arr_to_loc));
//				}
//			}
//		});
		printer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				printid = v.getId();
				SOTDatabase.deleteImage();
				cursor = FWMSSettingsDatabase.getPrinter();
				if (cursor.getCount() != 0) {
					if (RowItem.getPrintoption().equals("True")) {
						SO so = transferadAdapter.getItem(transferadAdapter
								.getSelectedPosition());
						sosno = so.getSno().toString();
						helper.showProgressDialog(R.string.generating_transfer);

						AsyncPrintCall task = new AsyncPrintCall();
						task.execute();
					} else {
						Toast.makeText(TransferHeader.this,
								"Please Enable CheckBox", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(TransferHeader.this,
							"Please Configure the printer", Toast.LENGTH_SHORT)
							.show();
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
					new DatePickerDialog(TransferHeader.this, startDate,
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
					new DatePickerDialog(TransferHeader.this, endDate,
							endCalendar.get(Calendar.YEAR), endCalendar
									.get(Calendar.MONTH), endCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
	}

	private void searchResult(){

//		fromlocation = fromspinner.getSelectedItem().toString();
//		tolocation = tospinner.getSelectedItem().toString();

		String spn_status = statusspinner.getSelectedItem().toString();

//		if (spn_status.matches("Open")) {
//			status = "0";
//		}
				/*else if (spn_status.matches("view")) {
					status = "1";
				} else if (spn_status.matches("partial")) {
					status = "2";
				} */
		if (spn_status.matches("Pending Transfer")) {
			status = "1";
		}
		else if (spn_status.matches("Closed")) {
			status = "2";
		}
//				else if (spn_status.matches("All")) {
//					status = "5";
//				}
		Log.d("frmloc", fromlocation);
		Log.d("toloc", tolocation);
		Log.d("spn_status", spn_status);

		fromdate = starteditTextDate.getText().toString();
		todate = endeditTextDate.getText().toString();
		Log.d("fromdate", fromdate);
		Log.d("todate", todate);
		onSetGet();
		if (fromdate.matches("")) {
			Toast.makeText(TransferHeader.this, "Enter Start Date",
					Toast.LENGTH_SHORT).show();
		} else if (todate.matches("")) {
			Toast.makeText(TransferHeader.this, "Enter End Date",
					Toast.LENGTH_SHORT).show();
		} else {
			AsyncCallSearchtransfer searchCustCode = new AsyncCallSearchtransfer();
			searchCustCode.execute();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		SO so = transferadAdapter.getItem(info.position);
		sosno = so.getSno().toString();
		sodate = so.getDate().toString();
		sofromlocation = so.getFromlocation().toString();
		so_remarks =so.getRemarks().toString();
		sotolocation = so.getTolocation().toString();
		statusvalue = so.getStatus().toString();

		menu.setHeaderTitle(sosno);
		menu.add(0, v.getId(), 0, "Print Preview");
		Log.d("statusvalue",statusvalue);
		Log.d("tabcheck",tabcheck +"  "+SalesOrderSetGet.getStatusCheck());
		if(statusvalue.equalsIgnoreCase("Pending Transfer")){
//			if(SalesOrderSetGet.getStatusCheck().matches("0")){
				menu.add(0, v.getId(), 0, "Recieve Stock");
//			}
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		if (item.getTitle() == "Print Preview") {
			helper.showProgressDialog(R.string.Transfer_printpreview);
			new AsyncPrintCall().execute();
		}else if (item.getTitle() == "Recieve Stock") {
			helper.showProgressDialog("Recieve Stock");
			new AsyncPrintCall().execute();
		}
		else {
			return false;
		}
		return true;
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

	public class SpinnerAdapter extends ArrayAdapter<String> {
		ArrayList<String> selectArray = new ArrayList<String>();

		public SpinnerAdapter(Context context, int textViewResourceId,
				ArrayList<String> objects) {
			super(context, textViewResourceId, objects);
			selectArray.clear();
			selectArray = objects;
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
			label.setText(selectArray.get(position));
			icon.setVisibility(View.GONE);

			return row;
		}
	}

	private class AsyncCallWSServerDate extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			spinnerLayout = new LinearLayout(TransferHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(stockR_parent, false);
			progressBar = new ProgressBar(TransferHeader.this);
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

			if (serverdate != null) {
				if(serverdate.contains("-")){
					starteditTextDate.setText(DateTime.date_(serverdate));
				}else if(serverdate.contains("/")){
					starteditTextDate.setText(DateTime.date(serverdate));
				}

					endeditTextDate.setText(serverdate);

				AsyncCallTransferHeader task = new AsyncCallTransferHeader();
				task.execute();
			}
		}
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
	}

	private class AsyncCallTransferHeader extends AsyncTask<Void, Void, Void> {
		String fromdate;
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if(serverdate.contains("-")){
				fromdate = DateTime.date_(serverdate);
			}else if(serverdate.contains("/")){
				fromdate = DateTime.date(serverdate);
			}



			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

			SoapObject request = new SoapObject(NAMESPACE, webMethName);

			PropertyInfo sDate = new PropertyInfo();
			PropertyInfo eDate = new PropertyInfo();
			PropertyInfo companyCode = new PropertyInfo();
			PropertyInfo FromLocation = new PropertyInfo();
			PropertyInfo ToLocation = new PropertyInfo();

			Log.d("from location","print "+strfromloc);

			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			sDate.setName("FromDate");
			sDate.setValue(fromdate);
			sDate.setType(String.class);
			request.addProperty(sDate);

			eDate.setName("ToDate");
			eDate.setValue(serverdate);
			eDate.setType(String.class);
			request.addProperty(eDate);

			FromLocation.setName("FromLocation");
			FromLocation.setValue(fromlocation_code);
			FromLocation.setType(String.class);
			request.addProperty(FromLocation);

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

				JSONObject jsonResponse;

				try {
					jsonResponse = new JSONObject(headeresult);
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("StockReqHeader");
					headerArr = new ArrayList<SO>();
					int lengthJsonArr = jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);
						String ccSno = jsonChildNode.optString("TransferNo")
								.toString();
						String ccDate = jsonChildNode.optString("TransferDate")
								.toString();
						String fromloca = jsonChildNode.optString(
								"FromLocation").toString();
						String toloca = jsonChildNode.optString("ToLocation")
								.toString();
						String status = jsonChildNode.optString("Status")
								.toString();
//						String status = "1";
						String remarks = jsonChildNode.optString("Remarks")
								.toString();

						Log.d("setStatusCheck","-->"+status);

						SO so = new SO();
							so.setRemarks(remarks);
							SalesOrderSetGet.setRemarks(remarks);
							so.setSno(ccSno);
							SalesOrderSetGet.setStatusCheck(status);
							so.setFromlocation(fromloca);
							so.setTolocation(toloca);

							Log.d("getStatusCheck","-->"+SalesOrderSetGet.getStatusCheck());
						if (status.matches("0")) {
							so.setStatus("open");
						}else if (status.matches("1")){
							so.setStatus("Pending Transfer");
						} else {
							so.setStatus("closed");
						}
							so.setRemarks1(remarks);

							if (ccDate != null) {
								StringTokenizer tokens = new StringTokenizer(
										ccDate, " ");
								String date = tokens.nextToken();
								so.setDate(date);
							} else {
								so.setDate(ccDate);
							}
							headerArr.add(so);
					}

				} catch (JSONException e) {

					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				suppTxt = "Error occured";
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d("ArrayList", "" + headerArr.size());
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

	public void headerCustCode() throws ParseException {
		if(!headerArr.isEmpty()){
		transferadAdapter = new HeaderAdapter(TransferHeader.this,
				R.layout.transferheader_list_item, null, headerArr);
		lv.setAdapter(transferadAdapter);
		}
	}

	private class AsyncCallSearchtransfer extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(TransferHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(stockR_parent, false);
			progressBar = new ProgressBar(TransferHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			searchArr = SalesOrderWebService.SearchTransfer(fromdate, todate,
					fromlocation_code, tolocation_code, status,
					"fncGetTransferHeader");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("searchArr",""+searchArr.size());

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
				Toast.makeText(TransferHeader.this, "No matches found",
						Toast.LENGTH_SHORT).show();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(stockR_parent, true);

		}
	}

	public void searchCustCode() throws ParseException {

		transferadAdapter = new HeaderAdapter(TransferHeader.this,
				R.layout.transferheader_list_item, null, searchArr);

		lv.setAdapter(transferadAdapter);

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
		location_code_name = SupplierSetterGetter.getLoc_code_name();
		if (fromlocation.matches("Select")) {
			fromlocation_code = "";
		} else {
			fromlocation_code = location_code_name.get(fromlocation);
			Log.d("fromlocation_code", ""+fromlocation_code);
		}

		if (tolocation.matches("Select")) {
			tolocation_code = "";
		} else {
			tolocation_code = location_code_name.get(tolocation);
			Log.d("tolocation_code", tolocation_code);
		}

	}

	private class AsyncPrintCall extends
			AsyncTask<Void, Void, ArrayList<String>> {
		@Override
		protected void onPreExecute() {
			product.clear();

		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("TransferNo", sosno);

			jsonString = SalesOrderWebService.getSODetail(hashValue,
					"fncGetTransferDetail");

			Log.d("jsonString ", jsonString);

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

					String ProductCode = jsonChildNode.optString(
							"ProductCode").toString();
					productdetail.setItemcode(ProductCode);
					String productname = jsonChildNode.optString(
							"ProductName").toString();
					productdetail.setDescription(productname);

					String transCqty = jsonChildNode.optString("CQty").toString();
				     String transLqty = jsonChildNode.optString("LQty").toString();
					
					String transqty = jsonChildNode.optString("Qty").toString();
					if (transqty.contains(".")) {
						StringTokenizer tokens = new StringTokenizer(transqty,
								".");
						String qty = tokens.nextToken();
						productdetail.setQty(qty);
					} else {
						productdetail.setQty(transqty);
					}

					productdetail.setCqty(transCqty);
				    productdetail.setLqty(transLqty);
				    Double qty = jsonChildNode.getDouble("Qty");
					productdetail.setTotalqty(qty);
					String WholeSalePrice = jsonChildNode.optString("AverageCost").toString();

					product.add(productdetail);

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("ProductCode", ProductCode);
					hm.put("ProductName", productname);
					hm.put("WholeSalePrice", WholeSalePrice);
					hm.put("Qty", String.valueOf(qty));
					searchProductArr.add(hm);

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
				Log.d("else","else");
				helper.dismissProgressDialog();
					if(statusvalue.equalsIgnoreCase("Pending Transfer")){
					SOTDatabase.deleteImage();
					SOTDatabase.deleteAllProduct();
					ConvertToSetterGetter.setEdit_stockreq_no("");
					SalesOrderSetGet.setFromLoc("");
					SalesOrderSetGet.setToLoc("");
					ArrayList<HashMap<String, String>> empty = new ArrayList<HashMap<String, String>>();
					empty.clear();
					ConvertToSetterGetter.setStockRequestBatchDetailArr(empty);
					Intent i = new Intent(TransferHeader.this,
							TransferAddProduct.class);
					i.putExtra("title", "Transfer");
					i.putExtra("No", sosno);
					i.putExtra("Date", sodate);
					i.putExtra("customerCode", sofromlocation);
					i.putExtra("customerName", sotolocation);
					i.putExtra("flag","ReceiveStock");
					PreviewPojo.setProducts(product);
					ConvertToSetterGetter.setEdit_stockreq_no(sosno);
					SalesOrderSetGet.setFromLoc(sofromlocation);
					SalesOrderSetGet.setRemarks(so_remarks);
					SalesOrderSetGet.setToLoc(sotolocation);
					PreviewPojo.setSearchProductArr(searchProductArr);
					startActivity(i);
					finish();
				}else {
					Intent i = new Intent(TransferHeader.this,
							CommonPreviewPrint.class);
					i.putExtra("title", "Transfer");
					i.putExtra("Invoicetype","Consignment");
					i.putExtra("No", sosno);
					i.putExtra("Date", sodate);
					i.putExtra("customerCode", sofromlocation);
					i.putExtra("cus_remarks",so_remarks);
					i.putExtra("customerName", sotolocation);
					PreviewPojo.setProducts(product);
					startActivity(i);
					finish();
				}

			}
		}
	}

	private void print() throws IOException {

		String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

		final String title = "TRANSFER";
		helper.dismissProgressDialog();
		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		try {
			Printer printer = new Printer(TransferHeader.this, macaddress);
			if (transferadAdapter.getSelectedPosition() != -1) {
				SO so = transferadAdapter.getItem(transferadAdapter
						.getSelectedPosition());

				sosno = so.getSno().toString();
				sodate = so.getDate().toString();
				sofromlocation = so.getFromlocation().toString();
				sotolocation = so.getTolocation().toString();
				so_remarks=so.getRemarks().toString();


				if (printertype.matches("Zebra iMZ320")) {
					helper.dismissProgressDialog();
					printer.printStockRequest(sosno, sodate, sofromlocation,
							sotolocation, product, title, 1, so_remarks);
				} else if (printertype.matches("4 Inch Bluetooth")) {
					helper.updateProgressDialog(TransferHeader.this.getString(R.string.creating_file_for_printing));
					if (BluetoothAdapter.checkBluetoothAddress(macaddress)) {
						BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
						// Attempt to connect to the device
						GlobalData.mService.setHandler(mHandler);
						GlobalData.mService.connect(device, true);

					}
					helper.dismissProgressDialog();
				} else if (printertype.matches("3 Inch Bluetooth Generic")) {
					helper.dismissProgressDialog();
					try {
						final CubePrint print = new CubePrint(TransferHeader.this, macaddress);
						print.initGenericPrinter();
						print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
							@Override
							public void initCompleted() {
								try {
								print.printTransfer(sosno, sodate, sofromlocation,
										sotolocation, product, title, 1);
								}catch (Exception e){
									e.printStackTrace();
								}
								print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
									@Override
									public void onCompleted() {
										helper.showLongToast(R.string.printed_successfully);
									}
								});
							}
						});

					}catch (Exception e){
						e.printStackTrace();
					}

				}

			}
		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		//if(D) Log.e(TAG, "--- onStart ---");
		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

		if (printertype.matches("4 Inch Bluetooth")) {
			if (!GlobalData.mBluetoothAdapter.isEnabled()) {
				GlobalData.mBluetoothAdapter.enable();
				//Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				//startActivity(enableIntent);*/
				// Otherwise, setup the chat session
			} else {
				if (GlobalData.mService == null) {
					GlobalData.mService = new BluetoothService(this, mHandler);
				}
			}
		}
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case GlobalData.MESSAGE_STATE_CHANGE:
					Log.d("case", "MESSAGE_STATE_CHANGE");
					//if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
					switch (msg.arg1) {
						case GlobalData.STATE_CONNECTED:
							//mTitle.setText(R.string.title_connected_to);
							//mTitle.append(mConnectedDeviceName);
							//Intent intent = new Intent(BluetoothActivity.this,PrintActivity.class);
							//intent.putExtra("COMM", 0);//0-BLUETOOTH
							// Set result and finish this Activity
							//	startActivity(intent);
							Log.d("case", "STATE_CONNECTED");
							print4Inch();
							//helper.dismissProgressDialog();
							break;
						case GlobalData.STATE_CONNECTING:
							//mTitle.setText(R.string.title_connecting);
							Log.d("case", "STATE_CONNECTING");

							break;
						case GlobalData.STATE_LISTEN:
							Log.d("case", "STATE_LISTEN");
							break;
						case GlobalData.STATE_NONE:
							Log.d("case", "STATE_NONE");
							//mTitle.setText(R.string.title_not_connected);
							break;
					}
					break;
				case GlobalData.MESSAGE_DEVICE_NAME:
					// save the connected device's name
					String mConnectedDeviceName = msg.getData().getString("device_name");
					Toast.makeText(getApplicationContext(), "Connected to "
							+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
					break;
				case GlobalData.MESSAGE_TOAST:

					//String macaddress = FWMSSettingsDatabase.getPrinterAddress();
					Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
							Toast.LENGTH_SHORT).show();

					reconnectDialog(msg.getData().getString("toast"));
					/*if (BluetoothAdapter.checkBluetoothAddress(macaddress))
					{
						BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
						// Attempt to connect to the device
						GlobalData.mService.setHandler(mHandler);
						GlobalData.mService.connect(device,true);

						//print4Inch();
					}*/
					//	helper.dismissProgressDialog();
					break;
			}
			//helper.dismissProgressDialog();
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		if (GlobalData.mService != null) {
			GlobalData.mService.stop();
		}
	}

	public void reconnectDialog(String msg) {
		final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		final Dialog dialog = new Dialog(TransferHeader.this);

		dialog.setContentView(R.layout.dialog_reconnect);
		dialog.setTitle(msg);
		ImageView reconnect = (ImageView) dialog.findViewById(R.id.reconnect);

		reconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (BluetoothAdapter.checkBluetoothAddress(macaddress)) {
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device, true);

				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void print4Inch() {
		CubePrint mPrintCube = new CubePrint(TransferHeader.this, FWMSSettingsDatabase.getPrinterAddress());
		mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
			@Override
			public void onCompleted() {
				helper.showLongToast(R.string.printed_successfully);

			}
		});
		/*mPrintCube.printSalesReturn(sosno, sodate, socustomercode, socustomername,
				product, productdet, printsortHeader, null, 1,
				null, footerArr);*/

//		mPrintCube.printSalesReturn(sosno, sodate, socustomercode,
//				socustomername, product, productdet, 1);


			String title = "TRANSFER";
//			helper.dismissProgressDialog();
//			String macaddress = FWMSSettingsDatabase.getPrinterAddress();
			try {
//				Printer printer = new Printer(TransferHeader.this, macaddress);
				if (transferadAdapter.getSelectedPosition() != -1) {
					SO so = transferadAdapter.getItem(transferadAdapter
							.getSelectedPosition());

					sosno = so.getSno().toString();
					sodate = so.getDate().toString();
					sofromlocation = so.getFromlocation().toString();
					sotolocation = so.getTolocation().toString();

					mPrintCube.printTransfer(sosno, sodate, sofromlocation,
							sotolocation, product, title, 1);
				}
			} catch (IllegalArgumentException e) {
				helper.showLongToast(R.string.error_configure_printer);
			} catch (Exception e){
				e.printStackTrace();
			}

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
		
		 Intent i = new Intent(TransferHeader.this, LandingActivity.class);
		 startActivity(i);
		TransferHeader.this.finish();
	}
}
