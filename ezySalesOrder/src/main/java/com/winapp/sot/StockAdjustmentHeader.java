package com.winapp.sot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

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
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.printer.UIHelper;

public class StockAdjustmentHeader extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace {
	String valid_url, serverdate, sDate, eDate, cuscode, statusvalue, status;
	int textlength = 0;
	int month, day, year, printid;
	EditText starteditTextDate, endeditTextDate;
	Button stock_adj_btsearch;
	ListView stockadj_listView;
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	ArrayList<HashMap<String, String>> searchResults;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	CustomAlertAdapterSupp arrayAdapterSupp;
	LinearLayout stockadjustment_parent, searchCstmrlayout, codelayout;
	Calendar startCalendar, endCalendar;
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<SO> searchCstCdArr = new ArrayList<SO>();

	static String headeresult;
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetStockAdjustmentHeader";
	private static String NAMESPACE = "http://tempuri.org/";
	String sNo = "";
	boolean mnnbldsbl;
	CheckBox so_checkbox;
	ArrayList<SO> list;
	HeaderAdapter soadapter;
		
	private UIHelper helper;
	
	ArrayList<ProductDetails> product;
	ArrayList<ProductDetails> productdet;
	String jsonString = null, jsonStr = null;
	JSONObject jsonResponse, jsonResp;
	JSONArray jsonMainNode, jsonSecNode;
	HashMap<String, String> hashValue = new HashMap<String, String>();
	Cursor cursor;
	SlidingMenu menu;
	ImageButton searchIcon, custsearchIcon, printer;
	
	private boolean arrowflag = true;
	private SOTDatabase sotdb;
	AlertDialog.Builder myDialog;
	private String cartonOrLoose = "";

	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);

		setContentView(R.layout.stockadjustment_header);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Stock Adjustment");
		searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
		printer = (ImageButton) customNav.findViewById(R.id.printer);
		custsearchIcon = (ImageButton) customNav
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

		cartonOrLoose = SalesOrderSetGet.getCartonpriceflag();

		starteditTextDate = (EditText) findViewById(R.id.starteditTextDate);
		endeditTextDate = (EditText) findViewById(R.id.endeditTextDate);
		stock_adj_btsearch = (Button) findViewById(R.id.stock_adj_btsearch);
		stockadj_listView = (ListView) findViewById(R.id.stockadj_listView);
		stockadjustment_parent = (LinearLayout) findViewById(R.id.stockadjustment_parent);
		codelayout = (LinearLayout) findViewById(R.id.codelayout);
		searchCstmrlayout = (LinearLayout) findViewById(R.id.searchlayout);

		searchIcon.setVisibility(View.GONE);
		
		so_checkbox = (CheckBox) findViewById(R.id.checkbox);
		product = new ArrayList<ProductDetails>();
		productdet = new ArrayList<ProductDetails>();
		sotdb = new SOTDatabase(StockAdjustmentHeader.this);
		searchCstmrlayout.setVisibility(View.GONE);
		al.clear();
		searchCstCdArr.clear();
		startCalendar = Calendar.getInstance();
		endCalendar = Calendar.getInstance();
		helper = new UIHelper(StockAdjustmentHeader.this);
		
		FWMSSettingsDatabase.init(StockAdjustmentHeader.this);
		valid_url = FWMSSettingsDatabase.getUrl();	
		new SalesOrderWebService(valid_url);
		SOTDatabase.init(StockAdjustmentHeader.this);

		AsyncCallWSStockAdjustmentHeader task = new AsyncCallWSStockAdjustmentHeader();
		task.execute();
		
//		stockadj_listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View v, int position,
//					long arg3) {
//
//				SO so = soadapter.getItem(position);
//				sosno = so.getSno().toString();
//				socustomername = so.getCustomerName().toString();
//				Toast.makeText(StockAdjustmentHeader.this, socustomername,
//						Toast.LENGTH_LONG).show();
//			}
//		});
	

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
					new DatePickerDialog(StockAdjustmentHeader.this, startDate,
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
					new DatePickerDialog(StockAdjustmentHeader.this, endDate,
							endCalendar.get(Calendar.YEAR), endCalendar
									.get(Calendar.MONTH), endCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		
		stock_adj_btsearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				sDate = starteditTextDate.getText().toString();
				eDate = endeditTextDate.getText().toString();
				// spinnerstatus();
				if (sDate.matches("")) {
					Toast.makeText(StockAdjustmentHeader.this,
							"Enter Start Date", Toast.LENGTH_SHORT).show();
				} else if (eDate.matches("")) {
					Toast.makeText(StockAdjustmentHeader.this,
							"Enter End Date", Toast.LENGTH_SHORT).show();
				} else {
					AsyncCallWSSearchCustCode searchCustCode = new AsyncCallWSSearchCustCode();
					searchCustCode.execute();
				}
			}
		});
		searchIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				InputMethodManager inputMethodManager = (InputMethodManager) StockAdjustmentHeader.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);

				if (searchCstmrlayout.getVisibility() == View.VISIBLE) {
					searchCstmrlayout.setVisibility(View.GONE);
					inputMethodManager.hideSoftInputFromWindow(
							codelayout.getWindowToken(), 0);
				} else {
					searchCstmrlayout.setVisibility(View.VISIBLE);

					inputMethodManager.toggleSoftInputFromWindow(
							codelayout.getApplicationWindowToken(),
							InputMethodManager.SHOW_FORCED, 0);
				}
			}
		});

//		printer.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				printid = v.getId();
//				cursor = FWMSSettingsDatabase.getPrinter();
//				if (cursor.getCount() != 0) {
//					if (RowItem.getPrintoption().equals("True")) {
//						SO so = soadapter.getItem(soadapter
//								.getSelectedPosition());
//						sosno = so.getSno().toString();
//						helper.showProgressDialog(R.string.generating_so);
//						AsyncPrintCall task = new AsyncPrintCall();
//						task.execute();
//					} else {
//						Toast.makeText(StockAdjustmentHeader.this,
//								"Please Enable CheckBox", Toast.LENGTH_SHORT)
//								.show();
//					}
//				} else {
//					Toast.makeText(StockAdjustmentHeader.this,
//							"Please Configure the printer", Toast.LENGTH_SHORT)
//							.show();
//				}
//			}
//		});

		custsearchIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				clearSetterGetter();
				Intent i = new Intent(StockAdjustmentHeader.this,
						StockAdjustmentAddproduct.class);
				startActivity(i);
				StockAdjustmentHeader.this.finish();
			}
		});
	}

	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// super.onCreateContextMenu(menu, v, menuInfo);
	//
	// AdapterView.AdapterContextMenuInfo info =
	// (AdapterView.AdapterContextMenuInfo) menuInfo;
	// SO so = soadapter.getItem(info.position);
	// sosno = so.getSno().toString();
	// sodate = so.getDate().toString();
	// socustomercode = so.getCustomerCode().toString();
	// sostatus= so.getStatus().toString();
	// menu.setHeaderTitle(sosno);
	// if(sostatus.matches("open")){
	// menu.add(0, v.getId(), 0, "Edit SO");
	// menu.add(0, v.getId(), 0, "Delete SO");
	// menu.add(0, v.getId(), 0, "Convert To Invoice");
	// menu.add(0, v.getId(), 0, "Convert To DO");
	// }
	//
	// if(sostatus.matches("InProgress Invoice")){
	// menu.add(0, v.getId(), 0, "Convert To Invoice");
	// }
	//
	// if(sostatus.matches("InProgress DO")){
	// menu.add(0, v.getId(), 0, "Convert To DO");
	// }
	//
	// if(sostatus.matches("closed")){
	// Toast.makeText(StockAdjustmentHeader.this, "Closed",
	// Toast.LENGTH_SHORT).show();
	// }
	//
	// menu.add(0, v.getId(), 0, "Print Preview");
	//
	// }
	//
	// public boolean onContextItemSelected(android.view.MenuItem item) {
	//
	// AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo) item
	// .getMenuInfo();
	// SO so = soadapter.getItem(adapterInfo.position);
	// sosno = so.getSno().toString();
	// cusCode = so.getCustomerCode().toString();
	//
	//
	// ConvertToSetterGetter.setDoNo("");
	// ConvertToSetterGetter.setSoNo("");
	// ConvertToSetterGetter.setEdit_do_no("");
	//
	// if(item.getTitle() == "Edit SO"){
	// for (HashMap<String, String> hashMap : al) {
	// for (String key : hashMap.keySet()) {
	// if (key.equals(socustomercode)) {
	// System.out.println(hashMap.get(key));
	// socustomername = hashMap.get(key);
	// }
	// }
	// }
	//
	// SalesOrderSetGet.setCustname(socustomername);
	// sotdb.truncateTables();
	// new AsyncCallWSEditSO().execute();
	// }
	// else if(item.getTitle() == "Delete SO"){
	// deleteAlertDialog();
	// }
	//
	// if (item.getTitle() == "Convert To DO") {
	// SOTDatabase.deleteImage();
	// SOTDatabase.deleteBarcode();
	// for (HashMap<String, String> hashMap : al) {
	// for (String key : hashMap.keySet()) {
	// if (key.equals(cusCode)) {
	// System.out.println(hashMap.get(key));
	// socustomername = hashMap.get(key);
	// SalesOrderSetGet.setCustname(socustomername);
	// Log.d("customer Name", "abcde" + socustomername);
	// }
	// }
	// }
	//
	// AsyncCallWSDODetail task = new AsyncCallWSDODetail();
	// task.execute();
	//
	// } else if (item.getTitle() == "Convert To Invoice") {
	// SalesOrderSetGet.setSoRemarks("");
	// SOTDatabase.deleteImage();
	// SOTDatabase.deleteBarcode();
	// ConvertToSetterGetter.setEdit_inv_no("");
	// for (HashMap<String, String> hashMap : al) {
	// for (String key : hashMap.keySet()) {
	// if (key.equals(cusCode)) {
	// System.out.println(hashMap.get(key));
	// socustomername = hashMap.get(key);
	// SalesOrderSetGet.setCustname(socustomername);
	// Log.d("customer Name", "abcde" + socustomername);
	// }
	// }
	// }
	// AsyncCallWSSODetail task = new AsyncCallWSSODetail();
	// task.execute();
	//
	// }
	// if (item.getTitle() == "Print Preview") {
	// helper.showProgressDialog(R.string.salesorder_printpreview);
	// new AsyncPrintCall().execute();
	// for (HashMap<String, String> hashMap : al) {
	// for (String key : hashMap.keySet()) {
	// if (key.equals(socustomercode)) {
	// System.out.println(hashMap.get(key));
	// socustomername = hashMap.get(key);
	// }
	// }
	//
	// }
	//
	// } else {
	// return false;
	// }
	// return true;
	// }

	// public void spinnerstatus() {
	// statusvalue = spinner_status.getSelectedItem().toString();
	// Log.d("statusvalue","-->"+statusvalue);
	// if (statusvalue.matches("Open")) {
	// status = "0";
	// }
	// else if (statusvalue.matches("InProgress Invoice")) {
	// status = "1";
	// }
	// else if (statusvalue.matches("InProgress DO")) {
	// status = "2";
	// }
	// else if (statusvalue.matches("Closed")) {
	// status = "3";
	// }
	// // else if (statusvalue.matches("Deleted")) {
	// // status = "4";
	// // }
	// else if (statusvalue.matches("All")) {
	// status = "5";
	// }
	// }

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

	private class AsyncCallWSSearchCustCode extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			searchCstCdArr.clear();
			spinnerLayout = new LinearLayout(StockAdjustmentHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			StockAdjustmentHeader.this.addContentView(spinnerLayout,
					new LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT,
							ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(stockadjustment_parent, false);
			progressBar = new ProgressBar(StockAdjustmentHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			Log.d("status", "-->" + status);
			searchCstCdArr = SalesOrderWebService.SearchStockAdjustment("fncGetStockAdjustmentHeader");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (!searchCstCdArr.isEmpty()) {
				try {
					searchCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				searchCstCdArr.clear();
				try {
					searchCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Toast.makeText(StockAdjustmentHeader.this, "No matches found",
						Toast.LENGTH_SHORT).show();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(stockadjustment_parent, true);
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

	public void searchCustCode() throws ParseException {

		soadapter = new HeaderAdapter(StockAdjustmentHeader.this,
				R.layout.salesorder_list_item, null, searchCstCdArr);
		stockadj_listView.setAdapter(soadapter);

	}

	private class AsyncCallWSStockAdjustmentHeader extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {

			spinnerLayout = new LinearLayout(StockAdjustmentHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			StockAdjustmentHeader.this.addContentView(spinnerLayout,
					new LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT,
							ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(stockadjustment_parent, false);
			progressBar = new ProgressBar(StockAdjustmentHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			SoapObject request = new SoapObject(NAMESPACE, webMethName);

			PropertyInfo companyCode = new PropertyInfo();
			PropertyInfo LocationCode = new PropertyInfo();
			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			String locationcode = SalesOrderSetGet.getLocationcode();
			
			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			LocationCode.setName("LocationCode");
			LocationCode.setValue(locationcode);
			LocationCode.setType(String.class);
			request.addProperty(LocationCode);

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
				headeresult = " { StockAdjHeader : " + suppTxt + "}";
				JSONObject jsonResponse;

				try {
					jsonResponse = new JSONObject(headeresult);
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("StockAdjHeader");

					int lengthJsonArr = jsonMainNode.length();
					list = new ArrayList<SO>();
					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);

						String StockAdjNo = jsonChildNode.optString("StockAdjNo").toString();
						String StockAdjDate = jsonChildNode.optString("StockAdjDate")
								.toString();
						String LocationCd = jsonChildNode.optString(
								"LocationCode").toString();
						
						SO so = new SO();
						so.setStAdjust_no(StockAdjNo);
						so.setStAdjust_date(StockAdjDate);
						so.setStAdjust_location(LocationCd);
														
							list.add(so);
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
			if (StockAdjustmentHeader.this != null) {
				try {
					headerCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(stockadjustment_parent, true);

			}
		}
	}

	public void headerCustCode() throws ParseException, Exception {

		if (!list.isEmpty()) {
			soadapter = new HeaderAdapter(StockAdjustmentHeader.this,
					R.layout.salesorder_list_item, null, list);
			stockadj_listView.setAdapter(soadapter);
		}
	}

	public void clearSetterGetter() {
		SalesOrderSetGet.setHeader_flag("StockAdjustmentHeader");	
		SalesOrderSetGet.setSaleorderdate("");
		SalesOrderSetGet.setDeliverydate("");
		SalesOrderSetGet.setCurrencycode("");
		SalesOrderSetGet.setRemarks("");

		SOTDatabase.init(StockAdjustmentHeader.this);
		SOTDatabase.deleteAllProduct();
		SOTDatabase.deleteBillDisc();
		SOTDatabase.deleteImage();
		SOTDatabase.deleteallbatch();

	}

//	private class AsyncPrintCall extends
//			AsyncTask<Void, Void, ArrayList<String>> {
//		@Override
//		protected void onPreExecute() {
//			product.clear();
//			productdet.clear();
//		}
//
//		@Override
//		protected ArrayList<String> doInBackground(Void... arg0) {
//
//			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
//			hashValue.put("CompanyCode", cmpnyCode);
//			hashValue.put("SoNo", sosno);
//
//			jsonString = SalesOrderWebService.getSODetail(hashValue,
//					"fncGetSODetail");
//			jsonStr = SalesOrderWebService.getSODetail(hashValue,
//					"fncGetSOHeaderBySoNo");
//
//			Log.d("jsonString ", jsonString);
//			Log.d("jsonStr ", jsonStr);
//
//			try {
//
//				jsonResponse = new JSONObject(jsonString);
//				jsonMainNode = jsonResponse.optJSONArray("SODetails");
//
//				jsonResp = new JSONObject(jsonStr);
//				jsonSecNode = jsonResp.optJSONArray("SODetails");
//
//			} catch (JSONException e) {
//
//				e.printStackTrace();
//			}
//
//			/*********** Process each JSON Node ************/
//			int lengthJsonArr = jsonMainNode.length();
//			for (int i = 0; i < lengthJsonArr; i++) {
//				/****** Get Object for each JSON node. ***********/
//				JSONObject jsonChildNode;
//				ProductDetails productdetail = new ProductDetails();
//				try {
//					jsonChildNode = jsonMainNode.getJSONObject(i);
//
//					productdetail.setItemcode(jsonChildNode.optString(
//							"ProductCode").toString());
//					productdetail.setDescription(jsonChildNode.optString(
//							"ProductName").toString());
//
//					String salesOrderqty = jsonChildNode.optString("Qty")
//							.toString();
//					if (salesOrderqty.contains(".")) {
//						StringTokenizer tokens = new StringTokenizer(
//								salesOrderqty, ".");
//						String qty = tokens.nextToken();
//						productdetail.setQty(qty);
//					} else {
//						productdetail.setQty(salesOrderqty);
//					}
//
//					productdetail.setPrice(jsonChildNode.optString("Price")
//							.toString());
//					productdetail.setTotal(jsonChildNode.optString("Total")
//							.toString());
//					product.add(productdetail);
//
//				} catch (JSONException e) {
//
//					e.printStackTrace();
//				}
//			}
//
//			int lengJsonArr = jsonSecNode.length();
//			for (int i = 0; i < lengJsonArr; i++) {
//
//				JSONObject jsonChildNode;
//				ProductDetails productdetail = new ProductDetails();
//				try {
//					jsonChildNode = jsonSecNode.getJSONObject(i);
//					productdetail.setItemdisc(jsonChildNode.optString(
//							"ItemDiscount").toString());
//					productdetail.setBilldisc(jsonChildNode.optString(
//							"BillDIscount").toString());
//					productdetail.setSubtotal(jsonChildNode.optString(
//							"SubTotal").toString());
//					productdetail.setTax(jsonChildNode.optString("Tax")
//							.toString());
//					productdetail.setNettot(jsonChildNode.optString("NetTotal")
//							.toString());
//					productdet.add(productdetail);
//
//				} catch (JSONException e) {
//
//					e.printStackTrace();
//				}
//			}
//			hashValue.clear();
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(ArrayList<String> result) {
//			if (printid == R.id.printer) {
//				try {
//					printid = 0;
//					print();
//				} catch (IOException e) {
//
//					e.printStackTrace();
//				}
//			} else {
//				helper.dismissProgressDialog();
//				Intent i = new Intent(StockAdjustmentHeader.this,
//						CommonPreviewPrint.class);
//				i.putExtra("title", "SalesOrder");
//				i.putExtra("No", sosno);
//				i.putExtra("Date", sodate);
//				i.putExtra("customerCode", socustomercode);
//				i.putExtra("customerName", socustomername);
//				PreviewPojo.setProducts(product);
//				PreviewPojo.setProductsDetails(productdet);
//				startActivity(i);
//			}
//		}
//
//	}
//
//	private void print() throws IOException {
//		helper.dismissProgressDialog();
//		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
//		try {
//			Printer printer = new Printer(StockAdjustmentHeader.this,
//					macaddress);
//			if (soadapter.getSelectedPosition() != -1) {
//				SO so = soadapter.getItem(soadapter.getSelectedPosition());
//
//				sosno = so.getSno().toString();
//				sodate = so.getDate().toString();
//				socustomercode = so.getCustomerCode().toString();
//				soamount = so.getNettotal().toString();
//				sostatus = so.getStatus().toString();
//				for (HashMap<String, String> hashMap : al) {
//					for (String key : hashMap.keySet()) {
//						if (key.equals(socustomercode)) {
//							System.out.println(hashMap.get(key));
//							socustomername = hashMap.get(key);
//						}
//					}
//
//				}
//
//				printer.printSalesOrder(sosno, sodate, socustomercode,
//						socustomername, product, productdet, 1);
//			}
//		} catch (IllegalArgumentException e) {
//			helper.showLongToast(R.string.error_configure_printer);
//		}
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(StockAdjustmentHeader.this, LandingActivity.class);
		startActivity(i);
		StockAdjustmentHeader.this.finish();
	}

	@Override
	public void onListItemClick(String item) {
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
}
