package com.winapp.sot;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.adapter.PopWindowAdapter;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.helper.XMLParser;
import com.winapp.model.Product;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.printer.CommonPreviewPrint;
import com.winapp.printer.PDFActivity;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.UIHelper;
import com.winapp.sotdetails.CustomerSetterGetter;
import com.winapp.util.XMLAccessTask;
import com.winapp.util.XMLAccessTask.CallbackInterface;
import com.winapp.util.XMLAccessTask.ErrorType;

public class SalesOrderHeader extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace,Constants {
	String valid_url, serverdate, sDate, eDate, cuscode="", statusvalue, status="0";
	int textlength = 0;
	int month, day, year, printid;
	EditText edCustomerCode, starteditTextDate, endeditTextDate,sl_namefield,locationcode_filter;
	Button btcstmrsrch;
	ListView so_lv;
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	ArrayList<HashMap<String, String>> searchResults,locationArrHm;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	CustomAlertAdapterSupp arrayAdapterSupp;
	private AlertDialog myalertDialog = null;
	LinearLayout salesO_parent, searchCstmrlayout, codelayout;
	Calendar startCalendar, endCalendar;
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
//	ArrayList<SO> searchCstCdArr = new ArrayList<SO>();

	static ArrayList<HashMap<String, String>> soDetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> doDetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> soHeadersArr = new ArrayList<HashMap<String, String>>();

	static String headeresult;
	String sNo = "";
	boolean mnnbldsbl;
	CheckBox so_checkbox;
	ArrayList<SO> list;
	HeaderAdapter soadapter;
	String sosno, sodate, socustomercode, soamount, sostatus, socustomername,
			deleteSOResult, cusCode = "",soaddress;
	private UIHelper helper;
	Spinner spinner_status;
	ArrayList<ProductDetails> product;
	ArrayList<ProductDetails> productdet;
	String jsonString = null,custjsonStr = null, jsonStr = null;
	JSONObject jsonResponse,custjsonResp, jsonResp;
	JSONArray jsonMainNode,custjsonMainNode, jsonSecNode;
	HashMap<String, String> hashValue = new HashMap<String, String>();
	Cursor cursor;
	SlidingMenu menu;
	ImageButton searchIcon, custsearchIcon, printer;
	private TextView tv_so_sno, tv_so_cutomer, tv_so_status;
	private ImageView iv_arrow;
	private boolean arrowflag = true;
	private SOTDatabase sotdb;
	AlertDialog.Builder myDialog;
	private String cartonOrLoose = "",soAdditionalInfo="",loccode="",username;
	private String deliveryOrderStr, invoiceStr, getSignatureimage = "", getPhotoimage="", companyCode="",select_van;
	private HashMap<String, String> params;
	// Offline
		LinearLayout offlineLayout;
		private OfflineDatabase offlineDatabase;
		boolean checkOffline;
		String onlineMode, offlineDialogStatus;
		private OfflineCommon offlineCommon;
		OfflineSettingsManager spManager;
	private String trantype="SO",haveAttribute="",mobileHaveOfflineMode="";
	ArrayList<Product> mGetSOAttributeDetail = new ArrayList<Product>();
	//ArrayList<Product> mAttributeProdWithSno = new ArrayList<>();
	ArrayList<Product> mAttributeDetail = new ArrayList<Product>();
	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		Mint.initAndStartSession(SalesOrderHeader.this, "29088aa0");
		setContentView(R.layout.salesorder_fragment);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("SalesOrder");
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

		edCustomerCode = (EditText) findViewById(R.id.salesOCustCode);
		starteditTextDate = (EditText) findViewById(R.id.starteditTextDate);
		endeditTextDate = (EditText) findViewById(R.id.endeditTextDate);
		btcstmrsrch = (Button) findViewById(R.id.saleO_btsearch);
		so_lv = (ListView) findViewById(R.id.saleO_listView1);
		salesO_parent = (LinearLayout) findViewById(R.id.salesOrder_parent);
		codelayout = (LinearLayout) findViewById(R.id.codelayout);
		searchCstmrlayout = (LinearLayout) findViewById(R.id.searchlayout);
		spinner_status = (Spinner) findViewById(R.id.stockrequest_status);
		iv_arrow = (ImageView) findViewById(R.id.sm_arrow);
		tv_so_sno = (TextView) findViewById(R.id.salesOSno);
		tv_so_cutomer = (TextView) findViewById(R.id.salesOCustomer);
		tv_so_status = (TextView) findViewById(R.id.salesOStatus);
		so_checkbox = (CheckBox) findViewById(R.id.checkbox);

		sl_namefield= (EditText) findViewById(R.id.sl_namefield);
		locationcode_filter = (EditText) findViewById(R.id.locationcode_filter);

		list = new ArrayList<SO>();
		product = new ArrayList<ProductDetails>();
		productdet = new ArrayList<ProductDetails>();
		sotdb = new SOTDatabase(SalesOrderHeader.this);
		searchCstmrlayout.setVisibility(View.GONE);
		al.clear();
		list.clear();
		startCalendar = Calendar.getInstance();
		endCalendar = Calendar.getInstance();
		helper = new UIHelper(SalesOrderHeader.this);
		FWMSSettingsDatabase.init(SalesOrderHeader.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,SalesOrderHeader.this);
		new SalesOrderWebService(valid_url);
		SOTDatabase.init(SalesOrderHeader.this);
		haveAttribute = SalesOrderSetGet.getHaveAttribute();
		locationArrHm = new ArrayList<>();

		if(haveAttribute!=null && !haveAttribute.isEmpty()){

		}else{
			haveAttribute="";
		}
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String SOnumber = getIntent().getStringExtra("InvoiceNo");
			String companyCode = SupplierSetterGetter.getCompanyCode();
			if(SOnumber!=null && !SOnumber.isEmpty()){
				new PDFActivity(SalesOrderHeader.this,valid_url+"/"+FNCA4INVOICEGENERATE+"?CompanyCode="+companyCode+"&sInvoiceNo="+SOnumber
						+"&TranType="+trantype, "report.pdf").execute();
			}
		}

		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();

		boolean showalllocatoin = FormSetterGetter.isShowAllLocation();

		Log.d("showAllLocationCheck",""+showalllocatoin);

//		showalllocatoin = true;
		if (showalllocatoin == true) {
			locationcode_filter.setFocusableInTouchMode(true);
			locationcode_filter.setBackgroundResource(drawable.edittext_bg);
			locationcode_filter.setCursorVisible(true);
		}else{
			loccode = SalesOrderSetGet.getLocationcode();
			Log.d("locationCodeCheck",loccode);
			String locName = SalesOrderSetGet.getLocationname();
			locationcode_filter.setText(locName);
			locationcode_filter.setEnabled(false);
			locationcode_filter.setFocusableInTouchMode(false);
			locationcode_filter.setBackgroundResource(R.drawable.labelbg);
			locationcode_filter.setCursorVisible(false);
			locationcode_filter.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
		}

		// @Offline
				onlineMode = OfflineDatabase.getOnlineMode();
				offlineDatabase = new OfflineDatabase(SalesOrderHeader.this);
				offlineCommon = new OfflineCommon(SalesOrderHeader.this);
				checkOffline = OfflineCommon.isConnected(SalesOrderHeader.this);
				OfflineDatabase.init(SalesOrderHeader.this);
				offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);
				spManager = new OfflineSettingsManager(SalesOrderHeader.this);
				companyCode = spManager.getCompanyType();
		
		tv_so_sno.setVisibility(View.GONE);
		tv_so_status.setVisibility(View.GONE);
		tv_so_cutomer.setText("CustName");
		
		// Offline
				if (onlineMode.matches("True")) {
					offlineLayout.setVisibility(View.GONE);
				} else if (onlineMode.matches("False")) {
					offlineLayout.setVisibility(View.VISIBLE);
				}

				select_van = SOTDatabase.getVandriver();
				
				if(select_van!=null && !select_van.isEmpty()){			
				}else{
					select_van="";
				}		
				/******** Amount will Gone or Visible Based on Hide Price From GetUserPermission  *********/
				if(FormSetterGetter.getHidePrice()!=null && !FormSetterGetter.getHidePrice().isEmpty()){	
				if(FormSetterGetter.getHidePrice().matches("Hide Price")){			
					((TextView) findViewById(R.id.salesOAmount)).setVisibility(View.GONE);					
				}else{
					((TextView) findViewById(R.id.salesOAmount)).setVisibility(View.VISIBLE);
				}
				}else{
					((TextView) findViewById(R.id.salesOAmount)).setVisibility(View.VISIBLE);	
				}

		AsyncCallWSSalesOrder salesOAC = new AsyncCallWSSalesOrder();
		salesOAC.execute();

		so_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {

				SO so = soadapter.getItem(position);
				sosno = so.getSno().toString();
				socustomername = so.getCustomerName().toString();
				Toast.makeText(SalesOrderHeader.this, socustomername,
						Toast.LENGTH_LONG).show();
			}
		});
		registerForContextMenu(so_lv);

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

		edCustomerCode
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						edCustomerCode) {
					@Override
					public boolean onDrawableClick() {
						alertDialogSearch();
						return true;
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
					new DatePickerDialog(SalesOrderHeader.this, startDate,
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
					new DatePickerDialog(SalesOrderHeader.this, endDate,
							endCalendar.get(Calendar.YEAR), endCalendar
									.get(Calendar.MONTH), endCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		iv_arrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (arrowflag) {

					arrowflag = false;
					iv_arrow.setImageResource(drawable.ic_arrow_left);
					tv_so_sno.setVisibility(View.GONE);
					tv_so_status.setVisibility(View.VISIBLE);
					tv_so_cutomer.setText("CustCode");
					soadapter.showAll(true);
				} else if (arrowflag == false) {

					iv_arrow.setImageResource(drawable.ic_arrow_right);
					tv_so_sno.setVisibility(View.GONE);
					tv_so_status.setVisibility(View.GONE);
					tv_so_cutomer.setText("CustName");
					arrowflag = true;
					soadapter.showAll(false);
				}
			}
		});
		btcstmrsrch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				cuscode = edCustomerCode.getText().toString();
				sDate = starteditTextDate.getText().toString();
				eDate = endeditTextDate.getText().toString();
				spinnerstatus();
//				if (sDate.matches("")) {
//					Toast.makeText(SalesOrderHeader.this, "Enter Start Date",
//							Toast.LENGTH_SHORT).show();
//				} else if (eDate.matches("")) {
//					Toast.makeText(SalesOrderHeader.this, "Enter End Date",
//							Toast.LENGTH_SHORT).show();
//				} else {
					loadprogress();
					asyncSOHeader searchCustCode = new asyncSOHeader();
					searchCustCode.execute();
//				}
			}
		});
		searchIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				InputMethodManager inputMethodManager = (InputMethodManager) SalesOrderHeader.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);

				if (searchCstmrlayout.getVisibility() == View.VISIBLE) {
					searchCstmrlayout.setVisibility(View.GONE);
					inputMethodManager.hideSoftInputFromWindow(
							codelayout.getWindowToken(), 0);
				} else {
					searchCstmrlayout.setVisibility(View.VISIBLE);
					edCustomerCode.requestFocus();
					inputMethodManager.toggleSoftInputFromWindow(
							codelayout.getApplicationWindowToken(),
							InputMethodManager.SHOW_FORCED, 0);
				}
			}
		});

		printer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				printid = v.getId();
				SOTDatabase.deleteImage();
				cursor = FWMSSettingsDatabase.getPrinter();

				String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
				Log.d("printertype", printertype);
				String cmpy = SupplierSetterGetter.getCompanyCode();
				if(printertype.matches("Zebra iMZ320")||(printertype.matches("4 Inch Bluetooth"))||(printertype.matches("3 Inch Bluetooth Generic"))||
						printertype.matches("Zebra iMZ320 4 Inch")) {
					if (cursor.getCount() != 0) {
						if (RowItem.getPrintoption().equals("True")) {
							SO so = soadapter.getItem(soadapter.getSelectedPosition());
							sosno = so.getSno().toString();
							socustomercode = so.getCustomerCode().toString();
							Log.d("socustomercode", "" + socustomercode);

							params = new HashMap<String, String>();
							params.put("CompanyCode", cmpy);
							params.put("InvoiceNo", sosno);
							params.put("TranType", "SO");
							Log.d("sosno", "" + sosno);

							new XMLAccessTask(SalesOrderHeader.this, valid_url,
									"fncGetInvoicePhoto", params, false,
									new GetSalesOrderImage()).execute();

							new XMLAccessTask(SalesOrderHeader.this, valid_url,
									"fncGetInvoiceSignature", params, false,
									new GetSalesOrderSignatureforReprint())
									.execute();

//						helper.showProgressDialog(R.string.generating_so);

							loadPrintData(R.string.generating_so);

							// temp
//						AsyncPrintCall task = new AsyncPrintCall();
//						task.execute();
						} else {
							Toast.makeText(SalesOrderHeader.this,
									"Please Enable CheckBox", Toast.LENGTH_SHORT)
									.show();
						}
					} else {
						Toast.makeText(SalesOrderHeader.this,
								"Please Configure the printer", Toast.LENGTH_SHORT)
								.show();
					}
				}else{
						Log.d("PDFActivity", "PDFActivity");
						SO so = soadapter.getItem(soadapter
								.getSelectedPosition());
						sosno = so.getSno().toString();
						new PDFActivity(SalesOrderHeader.this,valid_url+"/"+FNCA4INVOICEGENERATE+"?CompanyCode="+cmpy+"&sInvoiceNo="+sosno
								+"&TranType="+trantype, "report.pdf").execute();

//new DownloadFile().execute("http://SFA.com:81/SFAAPI.asmx/fncA4InvoiceGenerate?CompanyCode=1&sInvoiceNo=HQ2016-000366", "report.pdf");
					}
			}
		});

		custsearchIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				clearSetterGetter();
				Intent i = new Intent(SalesOrderHeader.this,
						SalesOrderCustomer.class);
				startActivity(i);
				SalesOrderHeader.this.finish();
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		SO so = soadapter.getItem(info.position);
		sosno = so.getSno().toString();
		sodate = so.getDate().toString();
		socustomercode = so.getCustomerCode().toString();
		socustomername = so.getCustomerName().toString();
		sostatus = so.getStatus().toString();
		soaddress = so.getCustomeraddress1()+" "+so.getCustomeraddress2()+" "+so.getCustomeraddress3();
		menu.setHeaderTitle(sosno);
		
		Log.d("sosno", "-----" + sosno);
		
		String dstatus = OfflineDatabase.getSODownloadStatus(sosno);
		
		if (sostatus.matches("open")) {
			
			if(offlineLayout.getVisibility() == View.VISIBLE){
				
				if(dstatus.matches("1")){
					menu.add(0, v.getId(), 0, "Edit SO");
//					menu.add(0, v.getId(), 0, "Convert To Invoice");
				}
				
			}else{
				menu.add(0, v.getId(), 0, "Edit SO");
				menu.add(0, v.getId(), 0, "Delete SO");
				menu.add(0, v.getId(), 0, "Convert To Invoice");
				menu.add(0, v.getId(), 0, "Convert To DO");
			}
			
		}

		deliveryOrderStr = FormSetterGetter.getDeliveryOrder();
		invoiceStr = FormSetterGetter.getInvoice();

		if (invoiceStr.matches("Invoice")) {
			if (sostatus.matches("InProgress Invoice")) {
				
				if(offlineLayout.getVisibility() == View.VISIBLE){
					if(dstatus.matches("1")){
						menu.add(0, v.getId(), 0, "Convert To Invoice");
					}
				}else{
					menu.add(0, v.getId(), 0, "Convert To Invoice");
				}	
			}
		}

		if (deliveryOrderStr.matches("Delivery Order")) {
			if (sostatus.matches("InProgress DO")) {
				
				if(offlineLayout.getVisibility() == View.VISIBLE){
					if(dstatus.matches("1")){
//						menu.add(0, v.getId(), 0, "Convert To DO");
					}
				}else{
					menu.add(0, v.getId(), 0, "Convert To DO");
				}	
				
			}
		}

		if (sostatus.matches("closed")) {
			Toast.makeText(SalesOrderHeader.this, "Closed", Toast.LENGTH_SHORT)
					.show();
		}
		
		if(offlineLayout.getVisibility() == View.VISIBLE){
			// don't show
		}else{
			menu.add(0, v.getId(), 0, "Print Preview");
			menu.add(0, v.getId(), 0, "Re-Order");
		}	

	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		SO so = soadapter.getItem(adapterInfo.position);
		sosno = so.getSno().toString();
		cusCode = so.getCustomerCode().toString();

		ConvertToSetterGetter.setDoNo("");
		ConvertToSetterGetter.setSoNo("");
		ConvertToSetterGetter.setEdit_do_no("");

		if (item.getTitle() == "Edit SO") {

			SOTDatabase.deleteAttribute();
			SOTDatabase.deleteImage();
		/*	for (HashMap<String, String> hashMap : al) {
				for (String key : hashMap.keySet()) {
					if (key.equals(socustomercode)) {
						System.out.println(hashMap.get(key));
						socustomername = hashMap.get(key);
					}
				}
			}*/
			Log.d("customer Name", "Edit" + socustomername);
			SalesOrderSetGet.setCustname(socustomername);
			sotdb.truncateTables();
			// new AsyncCallWSEditSO().execute();
			// temp
			new AsyncCallWSGetSignature(false,"").execute();
		} else if (item.getTitle() == "Delete SO") {
			deleteAlertDialog();
		}

		if (item.getTitle() == "Convert To DO") {
			SOTDatabase.deleteImage();
			SOTDatabase.deleteSODetailQuantity();
			SOTDatabase.deleteBarcode();
		/*	for (HashMap<String, String> hashMap : al) {
				for (String key : hashMap.keySet()) {
					if (key.equals(cusCode)) {
						System.out.println(hashMap.get(key));
						socustomername = hashMap.get(key);
						SalesOrderSetGet.setCustname(socustomername);
						Log.d("customer Name", "abcde" + socustomername);
					}
				}
			}*/
			SalesOrderSetGet.setCustname(socustomername);
			Log.d("customer Name", "Convert To DO" + socustomername);
			// temp

			AsyncCallWSDODetail task = new AsyncCallWSDODetail();
			task.execute();

		} else if (item.getTitle() == "Convert To Invoice") {
			SalesOrderSetGet.setSoRemarks("");
			SalesOrderSetGet.setSoAdditionalInfo("");
			CustomerSetterGetter.setDiscountPercentage("");
			CustomerSetterGetter.setBillDiscount("");
			SOTDatabase.deleteImage();
			SOTDatabase.deleteBarcode();
			ConvertToSetterGetter.setEdit_inv_no("");
			ArrayList<HashMap<String, String>> empty = new ArrayList<HashMap<String, String>>();
			empty.clear();
			ConvertToSetterGetter.setStockRequestBatchDetailArr(empty);
			
			SalesOrderSetGet.setCustname(socustomername);
			Log.d("customer Name", "Convert To Invoic" + socustomername);
			// temp
			AsyncCallWSSODetail task = new AsyncCallWSSODetail();
			task.execute();

		}
		if (item.getTitle() == "Print Preview") {
			helper.showProgressDialog(R.string.salesorder_printpreview);

			// temp
			new AsyncPrintCall().execute();
		/*	for (HashMap<String, String> hashMap : al) {
				for (String key : hashMap.keySet()) {
					if (key.equals(socustomercode)) {
						System.out.println(hashMap.get(key));
						socustomername = hashMap.get(key);
					}
				}

			}*/

		} 
		if (item.getTitle() == "Re-Order") {
			   SOTDatabase.deleteImage();  
			   Log.d("customer Name", "Edit" + socustomername);
			   SalesOrderSetGet.setCustname(socustomername);
			   
			   SalesOrderSetGet.setSaleorderdate(serverdate);
			   SalesOrderSetGet.setDeliverydate(serverdate);
			   sotdb.truncateTables();  
			   new AsyncCallWSGetSignature(false,"ReOrder").execute();
			  }
		else {
			return false;
		}
		return true;
	}

	public void spinnerstatus() {
		statusvalue = spinner_status.getSelectedItem().toString();
		Log.d("statusvalue", "-->" + statusvalue);
		if (statusvalue.matches("Open")) {
			status = "0";
		} else if (statusvalue.matches("InProgress Invoice")) {
			status = "2";
		} else if (statusvalue.matches("InProgress DO")) {
			status = "1";
		} else if (statusvalue.matches("Closed")) {
			status = "3";
		}
		// else if (statusvalue.matches("Deleted")) {
		// status = "4";
		// }
		else if (statusvalue.matches("All")) {
			status = "5";
		}
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

	public void alertDialogSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				SalesOrderHeader.this);
		final EditText editText = new EditText(SalesOrderHeader.this);
		final ListView listview = new ListView(SalesOrderHeader.this);
		LinearLayout layout = new LinearLayout(SalesOrderHeader.this);
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
					myalertDialog
							.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		arrayAdapterSupp = new CustomAlertAdapterSupp(SalesOrderHeader.this, al);
		listview.setAdapter(arrayAdapterSupp);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				myalertDialog.dismiss();
				getArraylsit = arrayAdapterSupp.getArrayList();
				HashMap<String, String> datavalue = getArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String keyValues = (String) mapEntry.getKey();
					String name = (String) mapEntry.getValue();
					
					edCustomerCode.setText(keyValues);
					sl_namefield.setText(name);
					edCustomerCode.addTextChangedListener(new TextWatcher() {
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
							textlength = edCustomerCode.getText().length();
							sl_namefield.setText("");
						}
					});
				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(al);
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
				for (int i = 0; i < al.size(); i++) {
					String supplierName = al.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(al.get(i));
					}
				}

				arrayAdapterSupp = new CustomAlertAdapterSupp(
						SalesOrderHeader.this, searchResults);
				listview.setAdapter(arrayAdapterSupp);
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

	
	

	private void print() throws IOException {
		helper.dismissProgressDialog();
		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		try {
			Printer printer = new Printer(SalesOrderHeader.this, macaddress);
			if (soadapter.getSelectedPosition() != -1) {
				SO so = soadapter.getItem(soadapter.getSelectedPosition());

				sosno = so.getSno().toString();
				sodate = so.getDate().toString();
				socustomercode = so.getCustomerCode().toString();
				soamount = so.getNettotal().toString();
				sostatus = so.getStatus().toString();
				socustomername =so.getCustomerName().toString();
				/*for (HashMap<String, String> hashMap : al) {
					for (String key : hashMap.keySet()) {
						if (key.equals(socustomercode)) {
							System.out.println(hashMap.get(key));
							socustomername = hashMap.get(key);
						}
					}

				}*/

				printer.printSalesOrder(sosno, sodate, socustomercode,
						socustomername, product, productdet, 1);
			}
		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}
	}

	PopupWindow popupWindow;

	public void initiatePopupWindow() {

		myDialog = new AlertDialog.Builder(SalesOrderHeader.this);
		LayoutInflater li = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.popupwindow, null, false);
		myDialog.setView(v);

		LinearLayout popup_qty_layout = (LinearLayout) v
				.findViewById(R.id.popup_qty_layout);
		LinearLayout popup_cqty_layout = (LinearLayout) v
				.findViewById(R.id.popup_cqty_layout);
		LinearLayout popup_lqty_layout = (LinearLayout) v
				.findViewById(R.id.popup_lqty_layout);

		ListView lv_popup = (ListView) v.findViewById(R.id.popupList);

		PopWindowAdapter popupAdapter = new PopWindowAdapter(SalesOrderHeader.this, soDetailsArr);
		lv_popup.setAdapter(popupAdapter);

		myDialog.setNeutralButton(R.string.invoice_summary,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(SalesOrderHeader.this,InvoiceSummary.class);
						i.putExtra("SODetails", soDetailsArr);
						i.putExtra("SOHeader", soHeadersArr);
						i.putExtra("soAdditionalInfo", soAdditionalInfo);
						startActivity(i);
						SalesOrderSetGet.setHeader_flag("SalesOrderHeader");
						SalesOrderHeader.this.finish();
					}
				});

		if (!cartonOrLoose.matches("")) {
			if (cartonOrLoose.matches("1")) {
				popup_qty_layout.setVisibility(View.GONE);
				popup_cqty_layout.setVisibility(View.VISIBLE);
				popup_lqty_layout.setVisibility(View.VISIBLE);
			}
			if (cartonOrLoose.matches("0")) {
				popup_qty_layout.setVisibility(View.VISIBLE);
				popup_cqty_layout.setVisibility(View.GONE);
				popup_lqty_layout.setVisibility(View.GONE);
			}
		}

		myDialog.show();

	}

	PopupWindow popupWindow1;

	public void initiatePopupWindow1() {

		myDialog = new AlertDialog.Builder(SalesOrderHeader.this);
		LayoutInflater li = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.popupwindow, null, false);
		myDialog.setView(v);

		LinearLayout popup_qty_layout = (LinearLayout) v
				.findViewById(R.id.popup_qty_layout);
		LinearLayout popup_cqty_layout = (LinearLayout) v
				.findViewById(R.id.popup_cqty_layout);
		LinearLayout popup_lqty_layout = (LinearLayout) v
				.findViewById(R.id.popup_lqty_layout);

		ListView lv_popup = (ListView) v.findViewById(R.id.popupList);

		PopWindowAdapter popupAdapter = new PopWindowAdapter(
				SalesOrderHeader.this, soDetailsArr);
		lv_popup.setAdapter(popupAdapter);

		myDialog.setNeutralButton(R.string.delivery_summary,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(SalesOrderHeader.this,
								DeliverySummary.class);
						i.putExtra("SODetails", soDetailsArr);
						i.putExtra("SOHeader", soHeadersArr);
						startActivity(i);
						SalesOrderSetGet.setHeader_flag("SalesOrderHeader");
						SalesOrderHeader.this.finish();
					}
				});

		if (!cartonOrLoose.matches("")) {
			if (cartonOrLoose.matches("1")) {
				popup_qty_layout.setVisibility(View.GONE);
				popup_cqty_layout.setVisibility(View.VISIBLE);
				popup_lqty_layout.setVisibility(View.VISIBLE);
			}
			if (cartonOrLoose.matches("0")) {
				popup_qty_layout.setVisibility(View.VISIBLE);
				popup_cqty_layout.setVisibility(View.GONE);
				popup_lqty_layout.setVisibility(View.GONE);
			}
		}

		myDialog.show();

	}
	
	public class GetSalesOrderImage implements CallbackInterface {
		public GetSalesOrderImage() {
		}
		@Override
		public void onSuccess(NodeList nl) {

			getPhotoimage = "";
			for (int i = 0; i < nl.getLength(); i++) {

				Element e = (Element) nl.item(i);
				getPhotoimage = XMLParser.getValue(e, "RefPhoto");
			}

			Log.d("getPhotoimage", "getPhotoimage" + getPhotoimage);
//			if(isPrintCall){
//				
//					SOTDatabase.storeImage(1, getSignatureimage, "");
//					progressBar.setVisibility(View.GONE);
//					spinnerLayout.setVisibility(View.GONE);
//					enableViews(deliveryO_parent, true);
//					printCallDialog();
//				}
		}

		@Override
		public void onFailure(ErrorType error) {
//			if(isPrintCall){
//				progressBar.setVisibility(View.GONE);
//				spinnerLayout.setVisibility(View.GONE);
//				enableViews(deliveryO_parent, true);
//				printCallDialog();
//			}
			onError(error);
		}

	}

	public class GetSalesOrderSignature implements CallbackInterface {
		boolean isPrintCall;

		public GetSalesOrderSignature(boolean printCall) {
			isPrintCall = printCall;
		}

		@Override
		public void onSuccess(NodeList nl) {

			getSignatureimage = "";
			for (int i = 0; i < nl.getLength(); i++) {

				Element e = (Element) nl.item(i);
				getSignatureimage = XMLParser.getValue(e, "RefSignature");
			}

			Log.d("getSignatureimage", "getSignatureimage" + getSignatureimage);
			// if (isPrintCall) {

			SOTDatabase.storeImage(1, getSignatureimage, getPhotoimage);
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);
			// printCallDialog();
			// }
		}

		@Override
		public void onFailure(ErrorType error) {

			// if (isPrintCall) {
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);
			// printCallDialog();
			// }
			onError(error);
		}

	}

	public class GetSalesOrderSignatureforReprint implements CallbackInterface {

		@Override
		public void onSuccess(NodeList nl) {

			getSignatureimage = "";
			for (int i = 0; i < nl.getLength(); i++) {

				Element e = (Element) nl.item(i);
				getSignatureimage = XMLParser.getValue(e, "RefSignature");
			}

			Log.d("getSignatureimage", "getSignatureimage" + getSignatureimage);

			SOTDatabase.storeImage(1, getSignatureimage, getPhotoimage);

		}

		@Override
		public void onFailure(ErrorType error) {

			onError(error);
		}

	}

	private void onError(final ErrorType error) {
		new Thread() {
			@Override
			public void run() {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						if (error == ErrorType.NETWORK_UNAVAILABLE) {
							helper.showLongToast(R.string.error_showing_image_no_network_connection);
						} else {

						}
					}
				});
			}
		}.start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(SalesOrderHeader.this, LandingActivity.class);
		startActivity(i);
		SalesOrderHeader.this.finish();
	}

	@Override
	public void onListItemClick(String item) {

		menu.toggle();

	}

	public void deleteAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SalesOrderHeader.this);
		builder.setTitle("Confirm Delete");
		builder.setIcon(R.mipmap.delete);
		builder.setMessage("Do you want to delete?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				new AsyncCallWSSalesDelete().execute();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	/** Asynctask Start **/

	private class AsyncCallWSGetSignature extends AsyncTask<Void, Void, Void> {

		boolean isPrintCall;
		 String dialogStatus,mFlag="";
		  public AsyncCallWSGetSignature(boolean printCall,String flag) {
		   isPrintCall = printCall;
		   mFlag = flag;
		  }

		@Override
		protected void onPreExecute() {
			loadprogress();
			dialogStatus = checkInternetStatus();
			getPhotoimage = "";
			getSignatureimage="";
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String cmpy = SupplierSetterGetter.getCompanyCode();
			params = new HashMap<String, String>();
			params.put("CompanyCode", cmpy);
			params.put("InvoiceNo", sosno);
			params.put("TranType", "SO");
			Log.d("sosno", "" + sosno);
			
			if (onlineMode.matches("True")) {
				if (checkOffline == true) { //temp_offline
					
					if (dialogStatus.matches("true")) {
						//need					
					} else {

						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					}

				} else {  //Onllineine
					
					new XMLAccessTask(SalesOrderHeader.this, valid_url,
							"fncGetInvoicePhoto", params, false,
							new GetSalesOrderImage()).execute();
					
					new XMLAccessTask(SalesOrderHeader.this, valid_url,
							"fncGetInvoiceSignature", params, false,
							new GetSalesOrderSignature(isPrintCall)).execute();
				}

			} else if (onlineMode.matches("False")) {  // permanent_offline
				
				//need
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			// if (!isPrintCall) {
			// Log.d("!isPrintCall", "!isPrintCall");
			new AsyncCallWSEditSO(mFlag).execute();
			// }

		}
	}

	private class AsyncCallWSSalesOrder extends AsyncTask<Void, Void, Void> {
		String dialogStatus;
		@Override
		protected void onPreExecute() {
			dialogStatus = checkInternetStatus();
			al.clear();
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			// Offline
				SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
				String finalDate = timeFormat.format(new Date());
				System.out.println(finalDate);
			
			if (onlineMode.matches("True")) {
				if (checkOffline == true) { //temp_offline
					
					if (dialogStatus.matches("true")) {
						al = getCustomerOffline();
						serverdate = finalDate;						
					} else {

						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
						finish();
					}

				} else {  //Onllineine
					
					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put("CompanyCode", companyCode);
					hm.put("VanCode", select_van);
			
		        	al = SalesOrderWebService.getWholeCustomer(hm, "fncGetCustomerForSearch");
		        	
					serverdate = DateWebservice.getDateService("fncGetServerDate");
				}

			} else if (onlineMode.matches("False")) {  // permanent_offline
				al = getCustomerOffline();
				serverdate = finalDate;			
			}	
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (serverdate != null) {
//				starteditTextDate.setText(DateTime.date(serverdate));
//				endeditTextDate.setText(serverdate);
				
				starteditTextDate.setText("");  // added new
				endeditTextDate.setText("");
				
				cuscode = edCustomerCode.getText().toString();
				sDate = starteditTextDate.getText().toString();
				eDate = endeditTextDate.getText().toString();

				asyncSOHeader task = new asyncSOHeader();
				task.execute();
			}else{
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesO_parent, true);
			}

		}
	}

	private class asyncSOHeader extends AsyncTask<Void, Void, Void> {
		String dialogStatus;
		@Override
		protected void onPreExecute() {
			dialogStatus = checkInternetStatus();
			list.clear();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			Log.d("status", "-->" + status);
			HashMap<String, String> hm = new HashMap<String, String>();

			int modeid = FWMSSettingsDatabase.getInvoiceuserModeId();
			Log.d("ModeIdCheck",""+modeid);
			if (modeid == 1) {
				FWMSSettingsDatabase.updateInvoiceuserMode(1);
				username = SupplierSetterGetter.getUsername();
			} else {
				FWMSSettingsDatabase.updateInvoiceuserMode(0);
				username = "";
			}

			hm.put("CompanyCode", companyCode);
			hm.put("CustomerCode", cuscode);
			hm.put("FromDate", sDate);
			hm.put("ToDate", eDate);
			hm.put("VanCode", select_van);
			hm.put("IsPacked", "");
			hm.put("LocationCode", loccode);
			hm.put("User",username);

			if (onlineMode.matches("True")) {
				if (checkOffline == true) { //temp_offline
					
					if (dialogStatus.matches("true")) {
						
					      try {
					    	  list = offlineSOHeader(cuscode, sDate, eDate, status);		
							} catch (ParseException e) {
								e.printStackTrace();
							}					
					} else {
						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					}
				} else {  //Online
					
					list = SalesOrderWebService.getSOHeaderList(hm, status, "fncGetSOHeader");
				}
			} else if (onlineMode.matches("False")) {  // permanent_offline
				
				  try {
			    	  list = offlineSOHeader(cuscode, sDate, eDate, status);		
					} catch (ParseException e) {
						e.printStackTrace();
					}
			}
			
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d("list.size()", "--"+list.size());
			if (!list.isEmpty()) {
				try {
					searchCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				list.clear();
				try {
					searchCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Toast.makeText(SalesOrderHeader.this, "No matches found",
						Toast.LENGTH_SHORT).show();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);
		}
	}

//	private class AsyncCallWSSOHeader extends AsyncTask<Void, Void, Void> {
//		@Override
//		protected void onPreExecute() {
//
//			// list.clear();
//		}
//
//		@Override
//		protected Void doInBackground(Void... arg0) {
//			String fromdate = DateTime.date(serverdate);
//
//			SoapObject request = new SoapObject(NAMESPACE, webMethName);
//
//			PropertyInfo sDate = new PropertyInfo();
//			PropertyInfo eDate = new PropertyInfo();
//
//			PropertyInfo companyCode = new PropertyInfo();
//
//			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
//
//			companyCode.setName("CompanyCode");
//			companyCode.setValue(cmpnyCode);
//			companyCode.setType(String.class);
//			request.addProperty(companyCode);
//
//			sDate.setName("FromDate");
//			sDate.setValue(fromdate);
//			sDate.setType(String.class);
//			request.addProperty(sDate);
//
//			eDate.setName("ToDate");
//			eDate.setValue(serverdate);
//			eDate.setType(String.class);
//			request.addProperty(eDate);
//
//			String suppTxt = null;
//			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
//					SoapEnvelope.VER11);
//			envelope.dotNet = true;
//			envelope.bodyOut = request;
//			envelope.setOutputSoapObject(request);
//			HttpTransportSE androidHttpTransport = new HttpTransportSE(
//					valid_url);
//			try {
//				androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
//				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
//				suppTxt = response.toString();
//				headeresult = " { SOHeader : " + suppTxt + "}";
//				JSONObject jsonResponse;
//
//				try {
//					jsonResponse = new JSONObject(headeresult);
//					JSONArray jsonMainNode = jsonResponse
//							.optJSONArray("SOHeader");
//
//					int lengthJsonArr = jsonMainNode.length();
//					list = new ArrayList<SO>();
//					for (int i = 0; i < lengthJsonArr; i++) {
//
//						JSONObject jsonChildNode = jsonMainNode
//								.getJSONObject(i);
//						String ccSno = jsonChildNode.optString("SoNo")
//								.toString();
//						/*
//						 * String ccDate = jsonChildNode.optString("SoDate")
//						 * .toString();
//						 */
//						String ccDate = jsonChildNode.optString("DeliveryDate")
//								.toString();
//						String customerCode = jsonChildNode.optString(
//								"CustomerCode").toString();
//						String customerName = jsonChildNode.optString(
//								"CustomerName").toString();
//						String amount = jsonChildNode.optString("NetTotal")
//								.toString();
//						String status = jsonChildNode.optString("Status")
//								.toString();
//
//						SO so = new SO();
//
//						if (status.matches("0")) {
//							so.setSno(ccSno);
//							so.setCustomerCode(customerCode);
//							so.setCustomerName(customerName);
//							so.setNettotal(amount);
//							so.setStatus("open");
//
//							if (ccDate != null && !ccDate.isEmpty()) {
//								StringTokenizer tokens = new StringTokenizer(
//										ccDate, " ");
//								String date = tokens.nextToken();
//								so.setDate(date);
//							} else {
//								so.setDate(ccDate);
//							}
//							list.add(so);
//						}
//					}
//
//				} catch (JSONException e) {
//
//					e.printStackTrace();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				suppTxt = "Error occured";
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			if (SalesOrderHeader.this != null) {
//				try {
//					headerCustCode();
//				} catch (ParseException e) {
//					e.printStackTrace();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				progressBar.setVisibility(View.GONE);
//				spinnerLayout.setVisibility(View.GONE);
//				enableViews(salesO_parent, true);
//
//			}
//		}
//	}

	private class AsyncPrintCall extends AsyncTask<Void, Void, ArrayList<String>> {
		String dialogStatus;
		@Override
		protected void onPreExecute() {
			product.clear();
			productdet.clear();
			jsonString = "";
			jsonStr ="";
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			String locationcode = SalesOrderSetGet.getLocationcode();
					
			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("SoNo", sosno);
			hashValue.put("LocationCode", locationcode);
			
			
			if (onlineMode.matches("True")) {
				if (checkOffline == true) { //temp_offline
					
					if (dialogStatus.matches("true")) {
						jsonString = offlineDatabase.getSODetailJson(hashValue);
						jsonStr = offlineDatabase.getSOHeaderJson(hashValue);			
					} else {

						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					}

				} else {  //Online
					HashMap<String, String> custhash = new HashMap<String, String>();					
					custhash.put("CompanyCode", cmpnyCode);
					custhash.put("CustomerCode", socustomercode);
					Log.d("socustomercode", ""+socustomercode);
					jsonString = SalesOrderWebService.getSODetail(hashValue,"fncGetSODetail");
					jsonStr = SalesOrderWebService.getSODetail(hashValue,"fncGetSOHeaderBySoNo");	
					custjsonStr = SalesOrderWebService.getSODetail(custhash,"fncGetCustomer");
					
					Log.d("custjsonStr ", custjsonStr);
					
					try {
						custjsonResp = new JSONObject(custjsonStr);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					custjsonMainNode = custjsonResp.optJSONArray("SODetails");
					
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
							String TaxValue = jsonChildNode.optString("TaxValue")
									.toString();
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
							SalesOrderSetGet.setCustomerTaxPerc(TaxValue);
							
							Log.d("mobile settings", CustomerSetterGetter.getCustomerCode());

						} catch (JSONException e) {

							e.printStackTrace();
						}
					}
				}

			} else if (onlineMode.matches("False")) {  // permanent_offline
				
				jsonString = offlineDatabase.getSODetailJson(hashValue);
				jsonStr = offlineDatabase.getSOHeaderJson(hashValue);	
			}
			
			Log.d("jsonString ", jsonString);
			Log.d("jsonStr ", jsonStr);

			try {

			if(jsonString != null && !jsonString.isEmpty()){
				
				  jsonResponse = new JSONObject(jsonString);
				  jsonMainNode = jsonResponse.optJSONArray("SODetails");
				

			/*********** Process each JSON Node ************/
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode;
				ProductDetails productdetail = new ProductDetails();
				try {
					jsonChildNode = jsonMainNode.getJSONObject(i);

					productdetail.setItemcode(jsonChildNode.optString(
							"ProductCode").toString());
					productdetail.setDescription(jsonChildNode.optString(
							"ProductName").toString());

					String salesOrderqty = jsonChildNode.optString("Qty")
							.toString();
				/*	if (salesOrderqty.contains(".")) {
						StringTokenizer tokens = new StringTokenizer(
								salesOrderqty, ".");
						String qty = tokens.nextToken();
						productdetail.setQty(qty);
					} else {*/
						productdetail.setQty(salesOrderqty);
				//	}

					productdetail.setPrice(jsonChildNode.optString("Price")
							.toString());
					productdetail.setTotal(jsonChildNode.optString("Total")
							.toString());
					productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
					productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
					product.add(productdetail);
					Log.d("SO Header ", ":" + product.toString());

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
			}
			
			if(jsonStr != null && !jsonStr.isEmpty()){
				
				jsonResp = new JSONObject(jsonStr);
				jsonSecNode = jsonResp.optJSONArray("SODetails");

			int lengJsonArr = jsonSecNode.length();
			for (int i = 0; i < lengJsonArr; i++) {

				JSONObject jsonChildNode;
				ProductDetails productdetail = new ProductDetails();
				try {
					jsonChildNode = jsonSecNode.getJSONObject(i);
					productdetail.setItemdisc(jsonChildNode.optString(
							"ItemDiscount").toString());
					productdetail.setBilldisc(jsonChildNode.optString(
							"BillDIscount").toString());
					productdetail.setSubtotal(jsonChildNode.optString(
							"SubTotal").toString());
					productdetail.setTax(jsonChildNode.optString("Tax")
							.toString());
					productdetail.setNettot(jsonChildNode.optString("NetTotal")
							.toString());

					productdetail.setCustomeraddress1(jsonChildNode.optString("CustomerAddress1"));
					productdetail.setCustomeraddress2(jsonChildNode.optString("CustomerAddress2"));
					productdetail.setCustomeraddress3(jsonChildNode.optString("CustomerAddress3"));

					productdet.add(productdetail);
					
					Log.d("SOHeader ", ":" + productdet.toString());

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
			}
			
			} catch (Exception e) {

				e.printStackTrace();
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

				new PreviewSOAttributeDetail().execute();
				/*helper.dismissProgressDialog();
				Intent i = new Intent(SalesOrderHeader.this,
						CommonPreviewPrint.class);
				i.putExtra("title", "SalesOrder");
				i.putExtra("No", sosno);
				i.putExtra("Date", sodate);
				i.putExtra("customerCode", socustomercode);
				i.putExtra("customerName", socustomername);
				PreviewPojo.setProducts(product);
				PreviewPojo.setProductsDetails(productdet);
				startActivity(i);*/
			}
		}

	}

	private class PreviewSOAttributeDetail extends AsyncTask<Void, Void, Void> {
		String dialogStatus;
		@Override
		protected void onPreExecute() {
			mAttributeDetail.clear();
			jsonString = "";
			dialogStatus = checkInternetStatus();
		}
		@Override
		protected Void doInBackground(Void... voids) {
			try {
				String cmpnyCode = SupplierSetterGetter.getCompanyCode();

				hashValue.put("CompanyCode", cmpnyCode);
				hashValue.put("SONo", sosno);


				if (onlineMode.matches("True")) {
					if (checkOffline == true) { //temp_offline
						if (dialogStatus.matches("true")) {

						} else {

							if (mobileHaveOfflineMode.matches("1")) {
								finish();
							}
						}

					} else {  //Online
						jsonString = SalesOrderWebService.getSODetail(hashValue, "fncGetSOAttributeDetail");
						Log.d("SOAttributeDetail ", "jsonString-->"+ jsonString);
						if(jsonString!=null && !jsonString.isEmpty()){
							Log.d("SOAttributeDetail ", "-->"+ jsonString);
							JSONObject jsonObject = new JSONObject(jsonString);
							JSONArray mJsonArray = jsonObject.optJSONArray("SODetails");
							int jsonLength = mJsonArray.length();
							if (jsonLength > 0) {
								for(int i=0;i<jsonLength;i++){
									Product product = new Product();
									JSONObject mJsonObject = mJsonArray.getJSONObject(i);
									product.setSlNo(mJsonObject.getString("slNo"));
									product.setProductCode(mJsonObject.getString("ProductCode"));
									product.setColorCode(mJsonObject.getString("ColorCode"));
									product.setColorName(mJsonObject.getString("ColorName"));
									product.setSizeCode(mJsonObject.getString("SizeCode"));
									product.setSizeName(mJsonObject.getString("SizeName"));
									product.setcQty(mJsonObject.getString("CQty"));
									product.setLqty(mJsonObject.getString("LQty"));
									String qty = mJsonObject.getString("Qty");
									product.setQty(qty);
									product.setPrice(mJsonObject.getString("Price"));
									double quantity = qty.equals("") ? 0 : Double.valueOf(qty);
									if(quantity>0){
										mAttributeDetail.add(product);
									}
								}
							}
						}

					}

				} else if (onlineMode.matches("False")) {  // permanent_offline


				}

			}catch (Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			helper.dismissProgressDialog();
			Intent i = new Intent(SalesOrderHeader.this,
					CommonPreviewPrint.class);
			i.putExtra("title", "SalesOrder");
			i.putExtra("No", sosno);
			i.putExtra("Date", sodate);
			i.putExtra("Invoicetype", "sales");
			i.putExtra("cus_remarks","");
			i.putExtra("customerCode", socustomercode);
			i.putExtra("customerName", socustomername);
			i.putExtra("customeraddress", soaddress);
			PreviewPojo.setProducts(product);
			PreviewPojo.setProductsDetails(productdet);
			PreviewPojo.setAttributeDetail(mAttributeDetail);
			startActivity(i);
		}
	}

	private class AsyncCallWSSODetail extends AsyncTask<Void, Void, Void> {
		String dialogStatus;
		@Override
		protected void onPreExecute() {
			loadprogress();
			dialogStatus = checkInternetStatus();
			soDetailsArr.clear();
			soHeadersArr.clear();
		
			soAdditionalInfo="";
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			if (onlineMode.matches("True")) {
				if (checkOffline == true) { //temp_offline
					
					if (dialogStatus.matches("true")) {
						soHeadersArr = offlineDatabase.getSOHeader(sosno);
						soDetailsArr = offlineDatabase.getSODetails(sosno);	
						OfflineSalesOrderWebService.getCustomerTaxOffline(cusCode);			
					} else {

						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					}

				} else {  //Online
					
					soDetailsArr = SalesOrderWebService.getSODetails(sosno, "fncGetSODetail");
					soHeadersArr = SalesOrderWebService.getSOHeader(sosno, "fncGetSOHeaderBySoNo");
					soAdditionalInfo = SalesOrderWebService.getSOAdditionalInfo(sosno, "fncGetSOAdditionalInfo");
				
					
					try {
						SalesOrderWebService.getCustomerTax(cusCode, "fncGetCustomer");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}

			} else if (onlineMode.matches("False")) {  // permanent_offline
				
				soHeadersArr = offlineDatabase.getSOHeader(sosno);
				soDetailsArr = offlineDatabase.getSODetails(sosno);	
				OfflineSalesOrderWebService.getCustomerTaxOffline(cusCode);	
			}
			
			
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			boolean remarks = false;
			if (soDetailsArr != null) {
				for (int i = 0; i < soDetailsArr.size(); i++) {
					String ItemRemarks = soDetailsArr.get(i).get("ItemRemarks");

					Log.d("ItemRemarks", "re" + ItemRemarks);

					if (!ItemRemarks.matches("")) {
						Log.d("Itemremarks", "not null");
						remarks = true;
						break;
					}
				}

			}

			if (remarks == true) {
				initiatePopupWindow();
			} else {

				Intent i = new Intent(SalesOrderHeader.this,
						InvoiceSummary.class);
				i.putExtra("SODetails", soDetailsArr);
				i.putExtra("SOHeader", soHeadersArr);
				i.putExtra("soAdditionalInfo", soAdditionalInfo);
				
				startActivity(i);
				SalesOrderSetGet.setHeader_flag("SalesOrderHeader");
				SalesOrderHeader.this.finish();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);

		}
	}

	private class AsyncCallWSDODetail extends AsyncTask<Void, Void, Void> {

		String dialogStatus;
		@Override
		protected void onPreExecute() {
			soDetailsArr.clear();
			soHeadersArr.clear();
			dialogStatus = checkInternetStatus();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			if (onlineMode.matches("True")) {
				if (checkOffline == true) { //temp_offline
					
					if (dialogStatus.matches("true")) {
						soHeadersArr = offlineDatabase.getSOHeader(sosno);
						soDetailsArr = offlineDatabase.getSODetails(sosno);	
						OfflineSalesOrderWebService.getCustomerTaxOffline(cusCode);				
					} else {

						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					}

				} else {  //Onllineine
					soDetailsArr = SalesOrderWebService.getSODetails(sosno,"fncGetSODetail");
					soHeadersArr = SalesOrderWebService.getSOHeader(sosno,"fncGetSOHeaderBySoNo");
					try {
						SalesOrderWebService.getCustomerTax(cusCode, "fncGetCustomer");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else if (onlineMode.matches("False")) {  // permanent_offline
				
				soHeadersArr = offlineDatabase.getSOHeader(sosno);
				soDetailsArr = offlineDatabase.getSODetails(sosno);	
				OfflineSalesOrderWebService.getCustomerTaxOffline(cusCode);	
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			boolean remarks = false;
			if (soDetailsArr != null) {
				for (int i = 0; i < soDetailsArr.size(); i++) {
					String ItemRemarks = soDetailsArr.get(i).get("ItemRemarks");

					Log.d("ItemRemarks", "re" + ItemRemarks);

					if (!ItemRemarks.matches("")) {
						Log.d("Itemremarks", "not null");
						remarks = true;
						break;
					}
				}

			}

			if (remarks == true) {
				initiatePopupWindow1();
			} else {
				Intent i = new Intent(SalesOrderHeader.this, DeliverySummary.class);
				i.putExtra("SODetails", soDetailsArr);
				i.putExtra("SOHeader", soHeadersArr);
				startActivity(i);
				SalesOrderSetGet.setHeader_flag("SalesOrderHeader");
				SalesOrderHeader.this.finish();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);

		}
	}

	private class AsyncCallWSEditSO extends AsyncTask<Void, Void, Void> {

		String dialogStatus,mFlag="";
		  public AsyncCallWSEditSO(String flag) {  
		   mFlag = flag;
		  }
		@Override
		protected void onPreExecute() {
			dialogStatus = checkInternetStatus();
			soDetailsArr.clear();
			soHeadersArr.clear();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			if (onlineMode.matches("True")) {
				if (checkOffline == true) { //temp_offline
					
					if (dialogStatus.matches("true")) {
						soHeadersArr = offlineDatabase.getSOHeader(sosno);
						soDetailsArr = offlineDatabase.getSODetails(sosno);	
						OfflineSalesOrderWebService.getCustomerTaxOffline(cusCode);	
					} else {

						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					}

				} else {  //Onllineine
					soDetailsArr = SalesOrderWebService.getSODetails(sosno, "fncGetSODetail");
					soHeadersArr = SalesOrderWebService.getSOHeader(sosno, "fncGetSOHeaderBySoNo");
					try {
						SalesOrderWebService.getCustomerTax(cusCode, "fncGetCustomer");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (onlineMode.matches("False")) {  // permanent_offline
				
				soHeadersArr = offlineDatabase.getSOHeader(sosno);
				soDetailsArr = offlineDatabase.getSODetails(sosno);	
				OfflineSalesOrderWebService.getCustomerTaxOffline(cusCode);	
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if(haveAttribute.equalsIgnoreCase("2")){
				new GetSOAttributeDetail(mFlag).execute();
			}else {
				Intent i = new Intent(SalesOrderHeader.this, SalesSummary.class);
				i.putExtra("SODetails", soDetailsArr);
				i.putExtra("SOHeader", soHeadersArr);
				i.putExtra("getSignatureimage", getSignatureimage);
				i.putExtra("getPhotoimage", getPhotoimage);
				i.putExtra("ReOrder", mFlag);
				startActivity(i);
				SalesOrderSetGet.setHeader_flag("SalesOrderHeader");
				SalesOrderHeader.this.finish();

				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesO_parent, true);
			}
		}
	}

	private class GetSOAttributeDetail extends  AsyncTask<Void, Void, Void> {
		String dialogStatus,mFlag="",cmpnyCode="",response="";
		HashMap<String, String> hm;
		public GetSOAttributeDetail(String flag) {
			mFlag = flag;
		}
		@Override
		protected void onPreExecute() {
			//mAttributeProdWithSno.clear();
			mGetSOAttributeDetail.clear();
			dialogStatus = checkInternetStatus();
			hm = new HashMap<String, String>();
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				cmpnyCode = SupplierSetterGetter.getCompanyCode();
				hm.put("CompanyCode", cmpnyCode);
				hm.put("SONo", sosno);
				if (onlineMode.matches("True")) {
					if (checkOffline == true) { //temp_offline

						if (dialogStatus.matches("true")) {

						} else {

							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {  //Online
						response = SalesOrderWebService.getSODetail(hm, "fncGetSOAttributeDetail");
				}

				} else if (onlineMode.matches("False")) {  // permanent_offline

				}
				Log.d("SOAttributeDetail ", "-->"+ response);
				JSONObject mJsonObject = new JSONObject(response);
				JSONArray mJsonArray = mJsonObject.optJSONArray("SODetails");
				int jsonLength = mJsonArray.length();
				if (jsonLength > 0) {
					for(int i=0;i<jsonLength;i++){
						Product product = new Product();
						mJsonObject = mJsonArray.getJSONObject(i);
						String slno =  mJsonObject.getString("slNo");
						String productCode =  mJsonObject.getString("ProductCode");
						if(slno!=null && !slno.isEmpty()){
						}else{
							slno ="0";
						}
						product.setSlNo(slno);
						product.setProductCode(productCode);
						product.setColorCode(mJsonObject.getString("ColorCode"));
						product.setColorName(mJsonObject.getString("ColorName"));
						product.setSizeCode(mJsonObject.getString("SizeCode"));
						product.setSizeName(mJsonObject.getString("SizeName"));
						product.setcQty(mJsonObject.getString("CQty"));
						product.setLqty(mJsonObject.getString("LQty"));
						String qty = mJsonObject.getString("Qty");
						if(qty!=null && !qty.isEmpty()){

						}else{
							qty ="0";
						}
						product.setQty(qty);
						product.setPrice(mJsonObject.getString("Price"));
						/*if(slno!=null && !slno.isEmpty()){
							mAttributeProdWithSno.add(product);
						}*/
						mGetSOAttributeDetail.add(product);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
		/*	ArrayList<Product> mDistinctAttribueArr = new ArrayList<>();
			mDistinctAttribueArr.add(mAttributeProdWithSno.get(0));
			for (Product Product : mAttributeProdWithSno) {
				boolean flag = false;
				for (Product productUnique : mDistinctAttribueArr) {
					if (productUnique.getSlNo().equals(Product.getSlNo()) && productUnique.getProductCode().equals(Product.getProductCode())) {
						flag = true;
					}
				}
				if (!flag)
					mDistinctAttribueArr.add(Product);

			}
			//set SlNo
			for (Product product : mDistinctAttribueArr) {
				for (Product productAttr : mGetSOAttributeDetail) {
					if (productAttr.getProductCode().equals(product.getProductCode())) {
						productAttr.setSlNo(product.getSlNo());
					}
				}
			}*/
			Log.d("Attribute", "-->" + mGetSOAttributeDetail.size());

			Intent i = new Intent(SalesOrderHeader.this, SalesSummary.class);
			i.putExtra("SODetails", soDetailsArr);
			i.putExtra("SOHeader", soHeadersArr);
			i.putParcelableArrayListExtra("SOAttributeDetail",mGetSOAttributeDetail);
			i.putExtra("getSignatureimage", getSignatureimage);
			i.putExtra("getPhotoimage", getPhotoimage);
			i.putExtra("ReOrder",mFlag);
			startActivity(i);
			SalesOrderSetGet.setHeader_flag("SalesOrderHeader");
			SalesOrderHeader.this.finish();

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);

		}
	}

	private class AsyncCallWSSalesDelete extends AsyncTask<Void, Void, Void> {
		String dialogStatus, jsonString;
		@Override
		protected void onPreExecute() {
			loadprogress();
			jsonString ="";
			deleteSOResult = "";
			dialogStatus = checkInternetStatus();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			Log.d("snono-->", sosno);
			String user = SupplierSetterGetter.getUsername();
			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			HashMap<String, String> hashValue = new HashMap<String, String>();
			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("SoNo", sosno);
			hashValue.put("UserID", user);
			
			if (onlineMode.matches("True")) {
				if (checkOffline == true) { //temp_offline
					
					if (dialogStatus.matches("true")) {
						//need					
					} else {

						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					}

				} else {  //Onllineine
					
					jsonString = WebServiceClass.parameterService(hashValue, "fncDeleteSO");
				}

			} else if (onlineMode.matches("False")) {  // permanent_offline
				
				//need
			}

			
			if(jsonString != null && !jsonString.isEmpty()){

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("JsonArray");

				int lengthJsonArr = jsonMainNode.length();

				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode;
					jsonChildNode = jsonMainNode.getJSONObject(i);
					deleteSOResult = jsonChildNode.optString("Result").toString();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
			hashValue.clear();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			tv_so_sno.setVisibility(View.GONE);
			tv_so_status.setVisibility(View.GONE);
			tv_so_cutomer.setText("CustName");

			if (!deleteSOResult.matches("")) {

				new asyncSOHeader().execute();

				Toast.makeText(SalesOrderHeader.this, "Delete Successfully",
						Toast.LENGTH_LONG).show();

			} else {

				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesO_parent, true);

				Toast.makeText(SalesOrderHeader.this, "Failed",
						Toast.LENGTH_LONG).show();

			}

		}
	}

	/** Asynctask End **/
	
	/** Offline Start   **/
	
	public void loadPrintData(int generatingSO){

		
		if (onlineMode.matches("True")) {
			if (checkOffline == true) {

				// Offline
				String offlinePrintStatus = OfflineDatabase.getPrintStatus("tblGetSODetail","SoNo", sosno);
				Log.d("offlinePrintStatus", ""
						+ offlinePrintStatus);
				if (offlinePrintStatus != null&& !offlinePrintStatus.isEmpty()) {
					helper.showProgressDialog(generatingSO);
					AsyncPrintCall task = new AsyncPrintCall();
					task.execute();
				}else{
					Toast.makeText(SalesOrderHeader.this,"Online SalesOrder cannot be print",Toast.LENGTH_SHORT).show();
				}
					

			} else { // Online
				helper.showProgressDialog(generatingSO);
				AsyncPrintCall task = new AsyncPrintCall();
				task.execute();

			}
		} else if (onlineMode.matches("False")) { // perman offline
			    
			  //Offline
			    String offlinePrintStatus = OfflineDatabase.getPrintStatus("tblGetSODetail","SoNo", sosno);
			    Log.d("offlinePrintStatus",""+offlinePrintStatus);
				if(offlinePrintStatus!=null && !offlinePrintStatus.isEmpty()){
					helper.showProgressDialog(generatingSO);
					AsyncPrintCall task = new AsyncPrintCall();
					task.execute();
				}
				else{
			    	Toast.makeText(SalesOrderHeader.this,"Online SalesOrder cannot be print", Toast.LENGTH_SHORT).show();								    
			    }

			   }
		

	}
	
	public ArrayList<HashMap<String, String>> getCustomerOffline() {
		String customer_jsonString = "";
		HashMap<String, String> customerhashValue = new HashMap<String, String>();
		ArrayList<HashMap<String, String>> custList = new ArrayList<HashMap<String, String>>();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		customerhashValue.put("CompanyCode", cmpnyCode);
		customerhashValue.put("CustomerCode", "");
		customerhashValue.put("NeedOutstandingAmount", "");
		customerhashValue.put("AreaCode", "");
		customerhashValue.put("VanCode", select_van);

		customer_jsonString = OfflineDatabase
				.getCustomersList(customerhashValue);

		try {

			JSONObject customer_jsonResponse = new JSONObject(
					customer_jsonString);
			JSONArray customer_jsonMainNode = customer_jsonResponse
					.optJSONArray("JsonArray");

			int lengthJsonArr = customer_jsonMainNode.length();
			Log.d("customerlist size", "-->" + lengthJsonArr);
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode;
				try {

					jsonChildNode = customer_jsonMainNode.getJSONObject(i);
					String cust_code = jsonChildNode.optString("CustomerCode")
							.toString();
					String cust_Name = jsonChildNode.optString("CustomerName")
							.toString();

					String ReferenceLocation = jsonChildNode.optString(
							"ReferenceLocation").toString();
				
					Log.d("ReferenceLocation", ""+ReferenceLocation);
					
					HashMap<String, String> customerhm = new HashMap<String, String>();
					if(ReferenceLocation!=null && !ReferenceLocation.isEmpty()){
						customerhm.put(cust_code, cust_Name+"/"+ReferenceLocation);
					}else{
						customerhm.put(cust_code, cust_Name);	
					}
					
//					HashMap<String, String> customerhm = new HashMap<String, String>();
//					customerhm.put(cust_code, cust_Name);
					custList.add(customerhm);

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return custList;
	}
	
	public ArrayList<SO> offlineSOHeader(String customercode, String startmonthdate, String lastmonthdate, String status) throws ParseException {
		
		ArrayList<SO> resultlist = new ArrayList<SO>();
		Log.d("Offline Search", "Offline Search");

		Date startDate=null,endDate=null;
		String from="",to="";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		if(startmonthdate!=null && !startmonthdate.isEmpty()){
			startDate  = dateFormat.parse(startmonthdate);
		}

		if(lastmonthdate!=null && !lastmonthdate.isEmpty()){
			endDate  = dateFormat.parse(lastmonthdate);
		}

		SimpleDateFormat regateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(startDate!=null){
			from = regateFormat.format(startDate)+" 00:00:00";
		}
		if(endDate!=null){
			to = regateFormat.format(endDate)+" 24:00:00";
		}

		Log.d("fromDate", from);
		Log.d("toDate", to);
		
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("CustomerCode", customercode);
		hm.put("FromDate", from);
		hm.put("ToDate", to);
		hm.put("Status", status);
		
		resultlist = OfflineDatabase.getSOHeaderList(hm);
		
		Log.d("resultlist Size", resultlist.size()+"");
		
		return resultlist;
	}

	
	private String checkInternetStatus() {
		String internetStatus = "";
		String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			if(mobileHaveOfflineMode.matches("1")){

		checkOffline = OfflineCommon.isConnected(SalesOrderHeader.this);
//		String internetStatus = "";
		if (onlineMode.matches("True")) {
			if (checkOffline == true) {
				String Off_dialog = OfflineDatabase.getInternetMode("OfflineDialog");
				if (Off_dialog.matches("true")) {
					internetStatus = "true";
				} else {
					offlineCommon.OfflineAlertDialog();
					Boolean dialogStatus = offlineCommon.showDialog();
					OfflineDatabase.updateInternetMode("OfflineDialog",dialogStatus + "");
					Log.d("Offline DialogStatus", "" + dialogStatus);
					internetStatus = "" + dialogStatus;
				}
			} else if (checkOffline == false) {
				String on_dialog = OfflineDatabase.getInternetMode("OnlineDialog");
				if (on_dialog.matches("true")) {
					internetStatus = "true";
				} else {
					offlineCommon.onlineAlertDialog();
					boolean dialogStatus = offlineCommon.showDialog();
					OfflineDatabase.updateInternetMode("OnlineDialog",dialogStatus + "");
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

		// if(offlineLayout.getVisibility() == View.GONE){
		// re_print.setVisibility(View.VISIBLE);
		//
		// }else{
		// re_print.setVisibility(View.INVISIBLE);
		// }

		String deviceId = RowItem.getDeviceID();

		Log.d("device id", "dev " + deviceId);
			}else{
				internetStatus = "false";
			}
		}else{
			internetStatus = "false";
		}
		return internetStatus;
	}

	
	/** Offline End **/
	
	public void searchCustCode() throws ParseException {

		soadapter = new HeaderAdapter(SalesOrderHeader.this,R.layout.invoice_list_item_latest, null, list);
		so_lv.setAdapter(soadapter);

		iv_arrow.setImageResource(drawable.ic_arrow_right);
		tv_so_sno.setVisibility(View.GONE);
		tv_so_status.setVisibility(View.GONE);
		tv_so_cutomer.setText("CustName");

	}

//	public void headerCustCode() throws ParseException, Exception {
//
//		if (!list.isEmpty()) {
//			soadapter = new HeaderAdapter(SalesOrderHeader.this,
//					R.layout.salesorder_list_item, null, list);
//			so_lv.setAdapter(soadapter);
//		}
//	}

	public void clearSetterGetter() {
		SalesOrderSetGet.setHeader_flag("SalesOrderHeader");
		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");
		SalesOrderSetGet.setSaleorderdate("");
		SalesOrderSetGet.setDeliverydate("");
		SalesOrderSetGet.setCurrencycode("");
		SalesOrderSetGet.setCurrencyname("");
		SalesOrderSetGet.setCurrencyrate("");
		SalesOrderSetGet.setRemarks("");

		SOTDatabase.init(SalesOrderHeader.this);
		SOTDatabase.deleteAllProduct();
		SOTDatabase.deleteBillDisc();
		SOTDatabase.deleteImage();
		SOTDatabase.deleteallbatch();
		SOTDatabase.deleteSODetailQuantity();
		SOTDatabase.deleteAttribute();
		ConvertToSetterGetter.setSoNo("");
		ConvertToSetterGetter.setEdit_do_no("");
	}
	
	public void loadprogress(){
		spinnerLayout = new LinearLayout(SalesOrderHeader.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		SalesOrderHeader.this.addContentView(spinnerLayout,
				new LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(salesO_parent, false);
		progressBar = new ProgressBar(SalesOrderHeader.this);
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

	public void alertDialogLocation() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				SalesOrderHeader.this);
		final EditText editText = new EditText(SalesOrderHeader.this);
		final ListView listview = new ListView(SalesOrderHeader.this);
		LinearLayout layout = new LinearLayout(SalesOrderHeader.this);
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(SalesOrderHeader.this,
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
						SalesOrderHeader.this, searchResults);
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

	private class ShowAllLocation extends AsyncTask<String,String,String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			hashValue.clear();
			helper.showProgressView(salesO_parent);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				String cmpnyCode = SupplierSetterGetter.getCompanyCode();
				hashValue.put("CompanyCode", cmpnyCode);
				String jsonString = SalesOrderWebService.getSODetail(hashValue, "fncGetLocation");
				if (jsonString != null && !jsonString.isEmpty()) {
					jsonResponse = new JSONObject(jsonString);
					jsonMainNode = jsonResponse.optJSONArray("SODetails");
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
			helper.dismissProgressView(salesO_parent);
			alertDialogLocation();
		}
	}
}
