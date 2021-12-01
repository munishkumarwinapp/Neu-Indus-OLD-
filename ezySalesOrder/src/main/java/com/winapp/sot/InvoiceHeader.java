package com.winapp.sot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SetStockInDetail;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.helper.XMLParser;
import com.winapp.model.Product;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSetterGetter;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.printcube.printcube.BluetoothService;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.printer.InvoicePrintPreview;
import com.winapp.printer.PDFActivity;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.Printer.OnCompletionListener;
import com.winapp.printer.PrinterZPL;
import com.winapp.printer.UIHelper;
import com.winapp.sotdetails.CustomerSetterGetter;
import com.winapp.util.ComparableUtil;
import com.winapp.util.XMLAccessTask;
import com.winapp.util.XMLAccessTask.CallbackInterface;
import com.winapp.util.XMLAccessTask.ErrorType;
import com.winapp.zxing.SmallCaptureActivity;

public class InvoiceHeader extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace, LocationListener,Constants {
	String valid_url, serverdate, sDate, eDate, cuscode, gnrlStngs,
			statusvalue, status, back = "", fromDateInvoice = "",
			toDateInvoice = "", userInvoice = "",mInvoiceDatePrintFlag = "",mWebMethodName="",userCustomer= "",userCustomerNme;
	int textlength = 0, month, day, year, stwght, printid;
	EditText edCustomerCode, starteditTextDate, endeditTextDate, edprintDate,
			edprintDate1, edprintUser,edCustCode,edCustoName,sl_namefield,locationcode_filter;
	private Button btcstmrsrch, stupButton, stdownButton, tab_notpaid, tab_overdue,tab_paid,tab_all;
	ListView so_lv;
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	ArrayList<HashMap<String, String>> searchResults,locationArrHm;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	CustomAlertAdapterSupp arrayAdapterSupp;
	private AlertDialog myalertDialog = null;
	LinearLayout salesO_parent, searchCstmrlayout, codelayout,
			mNoOfPrintCopiesLayout, mSignatureLayout, mCameraLayout,
			totaloutstanding_ll;
	Calendar startCalendar, endCalendar, printCalendar;
	static ArrayList<HashMap<String, String>> InvDetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> InvHeadersArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> InvConsignmentArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> doDetailsArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> doHeaderArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> InvBarcodeArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> userArr;
	ArrayList<SO> searchCstCdArr = new ArrayList<SO>();
	static String headeresult;
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetInvoiceHeader";
	private static String NAMESPACE = "http://tempuri.org/";
	boolean mnnbldsbl, isInvoicePrint;
	private CheckBox so_checkbox, mInvoiceCheckBox, mDOCheckBox;

	ArrayList<Product> invoicePrintDateArr;

	String invNo = "", Deletestring = "";
	ArrayList<SO> list = new ArrayList<SO>();
	ArrayList<SO> offlinelist;
	HeaderAdapter invoiceadapter;
	String sosno, sodate, socustomercode, socustomername, soamount, sostatus,soTranType,soaddress,soDONO;
	ArrayList<ProductDetails> product, product_batchArr, productdet, footerArr;
	private static final int REQUEST_ENABLE_BT = 3;
	private UIHelper helper;
	String jsonString = null, jsonStr = null, custjsonStr = null,
			batchjsonStr = "", jsonSt;
	JSONObject jsonResponse, jsonResp, custjsonResp, batch_jsonResp,
			jsonRespfooter;
	JSONArray jsonMainNode, jsonSecNode, custjsonMainNode, batch_jsonSecNode,
			jsonSecNodefooter;
	HashMap<String, String> hashValue = new HashMap<String, String>();
	HashMap<String, String> hashVl = new HashMap<String, String>();
	Cursor cursor;

	Spinner spinner_status;
	SlidingMenu menu;
	ImageButton searchIcon, custsearchIcon, printer, mInvoiceProductPrint, qrcode;
	ArrayList<String> sortproduct = new ArrayList<String>();
	List<String> sort = new ArrayList<String>();
	HashSet<String> hs = new HashSet<String>();
	List<String> printsortHeader = new ArrayList<String>();
	String notpaid = "0", getPhotoimage = "", getSignatureimage = "",loccode=
			"",
			companyCode = "", select_van = "";
	private ImageView iv_arrow, mSignature, mChangeSignBtn, prodphoto,
			sm_camera_iv;
	private boolean arrowflag = true;
	private TextView totaloutstanding,txt_totaloutstanding, /*tv_invoice_sno, tv_invoice_cutomer,*/
			stnumber,symbol;
	SOTDatabase sotdb;
	private HashMap<String, String> params;
	String receiptsStr, custCode = "", podpending = "",printStr="";
	private static final int PICK_FROM_CAMERA = 1;

	public static final int SIGNATURE_ACTIVITY = 2;
	// Offline
	LinearLayout offlineLayout;
	private OfflineDatabase offlineDatabase;
	boolean checkOffline;
	String onlineMode, offlineDialogStatus;
	private OfflineCommon offlineCommon;
	OfflineSettingsManager spManager;
	private String dialogStatus = "", addInvoice = "";

	private double mLatitude = 0, mLongitude = 0;
	private LocationManager locationManager;
	ArrayList<SO> overdue_list = new ArrayList<SO>();
	private String trantype="IN",mobileHaveOfflineMode="";
	private boolean showalllocatoin=false;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private String mCurrentgetPath="",mCurrentPhotoPath="",isClosedval="",IsPostedval="";
	String deleteResult="";
	IntentResult barcoderesult;
	static String custresult;
	String suppTxt = null;
	HashMap<String, String> hashmap = new HashMap<String, String>();
	ArrayList<String> alinvoice = new ArrayList<String>();
	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		Mint.initAndStartSession(InvoiceHeader.this, "29088aa0");
		setContentView(R.layout.invoice_fragment);

		GlobalData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		GlobalData.mService = new BluetoothService(this, mHandler);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.invoice_slidemenu, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText(getResources().getString(R.string.invoice));
		searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
		qrcode = (ImageButton) customNav.findViewById(R.id.qrcode);
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
		ConvertToSetterGetter.setDoNo("");

		ab.setBackgroundDrawable(getResources().getDrawable(
				drawable.header_bg));

		addInvoice = FormSetterGetter.getAddInvoice();
		Log.d("addInvoice", "" + addInvoice);
		if (addInvoice.matches("Add Invoice")) {
			custsearchIcon.setVisibility(View.VISIBLE);
		} else {
			custsearchIcon.setVisibility(View.GONE);
		}

		locationcode_filter = (EditText) findViewById(R.id.locationcode_filter);

		locationArrHm = new ArrayList<>();
		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();


//		int modeid = FWMSSettingsDatabase.getInvoiceuserModeId();
//		Log.d("modeid","-->"+modeid);
//
//		if (modeid == 1) {
//
//			loccode = SalesOrderSetGet.getLocationcode();
//			String locName = SalesOrderSetGet.getLocationname();
//			locationcode_filter.setText(locName);
//			locationcode_filter.setEnabled(false);
//			locationcode_filter.setFocusableInTouchMode(false);
//			locationcode_filter.setBackgroundResource(drawable.labelbg);
//			locationcode_filter.setCursorVisible(false);
//			locationcode_filter.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
//
//		} else {
//
//
//			locationcode_filter.setFocusableInTouchMode(true);
//			locationcode_filter.setBackgroundResource(drawable.edittext_bg);
//			locationcode_filter.setCursorVisible(true);
//
//		}
		showalllocatoin = FormSetterGetter.isShowAllLocation();
//		showalllocatoin =false;
		Log.d("showalllocatoin",""+showalllocatoin);
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
			locationcode_filter.setBackgroundResource(drawable.labelbg);
			locationcode_filter.setCursorVisible(false);
			locationcode_filter.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
		}

		// @Offline
		onlineMode = OfflineDatabase.getOnlineMode();
		offlineDatabase = new OfflineDatabase(InvoiceHeader.this);
		offlineCommon = new OfflineCommon(InvoiceHeader.this);
		checkOffline = OfflineCommon.isConnected(InvoiceHeader.this);
		OfflineDatabase.init(InvoiceHeader.this);
		offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);

		totaloutstanding_ll = (LinearLayout) findViewById(R.id.totaloutstanding_ll);
		salesO_parent = (LinearLayout) findViewById(R.id.invoiceOrder_parent);
		searchCstmrlayout = (LinearLayout) findViewById(R.id.invoiceOsearchlayout);
		edCustomerCode = (EditText) findViewById(R.id.invoiceOCustCode);
		starteditTextDate = (EditText) findViewById(R.id.starteditTextDate);
		endeditTextDate = (EditText) findViewById(R.id.endeditTextDate);
		btcstmrsrch = (Button) findViewById(R.id.invoiceO_btsearch);
		so_lv = (ListView) findViewById(R.id.invoiceO_listView1);
		codelayout = (LinearLayout) findViewById(R.id.codelayout);
		so_checkbox = (CheckBox) findViewById(R.id.checkbox);
		spinner_status = (Spinner) findViewById(R.id.invoice_status);
		totaloutstanding = (TextView) findViewById(R.id.totaloutstanding);
		mInvoiceProductPrint = (ImageButton) findViewById(R.id.invoiceProductPrint);

		tab_notpaid = (Button) findViewById(R.id.tab_notpaid);  // tab view
		tab_overdue = (Button) findViewById(R.id.tab_overdue);
		tab_paid = (Button) findViewById(R.id.tab_paid);
		tab_all	= (Button) findViewById(R.id.tab_all);

		iv_arrow = (ImageView) findViewById(R.id.sm_arrow);
//		tv_invoice_sno = (TextView) findViewById(R.id.invoiceSno);
//		tv_invoice_cutomer = (TextView) findViewById(R.id.sInvoiceCustomer);

		sl_namefield= (EditText) findViewById(R.id.sl_namefield);

		SOTDatabase.init(InvoiceHeader.this);
		product = new ArrayList<ProductDetails>();
		productdet = new ArrayList<ProductDetails>();
		product_batchArr = new ArrayList<ProductDetails>();
		sotdb = new SOTDatabase(InvoiceHeader.this);
		searchCstmrlayout.setVisibility(View.GONE);
		al.clear();
		searchCstCdArr.clear();
		startCalendar = Calendar.getInstance();
		endCalendar = Calendar.getInstance();
		printCalendar = Calendar.getInstance();
		offlinelist = new ArrayList<SO>();
		helper = new UIHelper(InvoiceHeader.this);
		FWMSSettingsDatabase.init(InvoiceHeader.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url, InvoiceHeader.this);
		new SalesOrderWebService(valid_url);
		new SOTSummaryWebService(valid_url);
		new WebServiceClass(valid_url);
		footerArr = new ArrayList<ProductDetails>();
		userArr = new ArrayList<HashMap<String, String>>();
		invoicePrintDateArr = new ArrayList<Product>();
		txt_totaloutstanding = (TextView)findViewById(R.id.txt_totaloutstanding);
		symbol = (TextView)findViewById(R.id.symbol);
		status = "0";


//		tv_invoice_sno.setVisibility(View.GONE);
//		tv_invoice_cutomer.setText("CustName");

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			Log.d("pageRedirection","yes");

			back = getIntent().getStringExtra("dashboard");

			String invnumber = getIntent().getStringExtra("InvoiceNo");
			String companyCode = SupplierSetterGetter.getCompanyCode();
			if(invnumber!=null && !invnumber.isEmpty()){
				new PDFActivity(InvoiceHeader.this,valid_url+"/"+FNCA4INVOICEGENERATE+"?CompanyCode="+companyCode+"&sInvoiceNo="+invnumber
						+"&TranType="+trantype, "report.pdf").execute();
			}


		}

		// Offline
		if (onlineMode.matches("True")) {
			if (checkOffline == true) {
				// Offline
				totaloutstanding_ll.setVisibility(View.GONE);
			}else{
				// online
				totaloutstanding_ll.setVisibility(View.VISIBLE);
			}
			offlineLayout.setVisibility(View.GONE);
		} else if (onlineMode.matches("False")) {
			// perman offline
			offlineLayout.setVisibility(View.VISIBLE);
			totaloutstanding_ll.setVisibility(View.GONE);
		}

		podpending = FWMSSettingsDatabase.getPODPending();

		if (podpending != null && !podpending.isEmpty()) {

		}else{
			podpending = "";
		}

//		if (podpending != null && !podpending.isEmpty()) {

			if (podpending.matches("1")) {
				spinner_status.setSelection(3);
				notpaid = "3";
			} else {
				spinner_status.setSelection(0);
				notpaid = "0";
			}
//		} else {
//			spinner_status.setSelection(0);
//			notpaid = "0";
//		}

		spinnerstatus();

		spManager = new OfflineSettingsManager(InvoiceHeader.this);
		companyCode = spManager.getCompanyType();
		select_van = SOTDatabase.getVandriver();

		if (select_van != null && !select_van.isEmpty()) {
		} else {
			select_van = "";
		}

		Log.d("getMobileSettings","-->"+MobileSettingsSetterGetter.getShowFooter());

		AsyncCallWSSalesOrder salesOAC = new AsyncCallWSSalesOrder();
		salesOAC.execute();
		AsyncGeneralSettings asyncgs = new AsyncGeneralSettings();
		asyncgs.execute();
//		AsyncCallWSSearchCustCode searchCustCode = new AsyncCallWSSearchCustCode();
//		searchCustCode.execute();

		so_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {

				SO so = invoiceadapter.getItem(position);
				sosno = so.getSno().toString();
				sodate = so.getDate().toString();
				socustomercode = so.getCustomerCode().toString();
				soamount = so.getNettotal().toString();
				socustomername = so.getCustomerName().toString();
				Toast.makeText(InvoiceHeader.this, socustomername,
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
					new DatePickerDialog(InvoiceHeader.this, startDate,
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
					new DatePickerDialog(InvoiceHeader.this, endDate,
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
//					tv_invoice_sno.setVisibility(View.VISIBLE);
//					tv_invoice_cutomer.setText("CustCode");
					invoiceadapter.showAll(true);
				} else if (arrowflag == false) {

					iv_arrow.setImageResource(drawable.ic_arrow_right);
//					tv_invoice_sno.setVisibility(View.GONE);
//					tv_invoice_cutomer.setText("CustName");
					arrowflag = true;
					invoiceadapter.showAll(false);
				}
			}
		});

		btcstmrsrch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				cuscode = edCustomerCode.getText().toString();
				sDate = starteditTextDate.getText().toString();
				eDate = endeditTextDate.getText().toString();

				Log.d("clickEvent",loccode);

				int modeId=FWMSSettingsDatabase.getInvoiceuserModeId();
				Log.d("modeid","-->"+modeId);
				if(modeId==1){
					status="2";
				}else
				{
					spinnerstatus();
				}

				SalesOrderSetGet.setClick("click");

//				if (sDate.matches("")) {
//					Toast.makeText(InvoiceHeader.this, "Enter Start Date",
//							Toast.LENGTH_SHORT).show();
//				} else if (eDate.matches("")) {
//					Toast.makeText(InvoiceHeader.this, "Enter End Date",
//							Toast.LENGTH_SHORT).show();
//				} else {
					AsyncCallWSSearchCustCode searchCustCode = new AsyncCallWSSearchCustCode();
					searchCustCode.execute();
//				}
			}
		});

		searchIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				InputMethodManager inputMethodManager = (InputMethodManager) InvoiceHeader.this
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
				printStr = "InvoiceHeader";
				String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

				Log.d("printertype", printertype);
				String cmpy = SupplierSetterGetter.getCompanyCode();
				if(printertype.matches("Zebra iMZ320")||(printertype.matches("4 Inch Bluetooth"))||(printertype.matches("3 Inch Bluetooth Generic"))||
						printertype.matches("Zebra iMZ320 4 Inch")){
					cursor = FWMSSettingsDatabase.getPrinter();
					if (cursor.getCount() != 0) {
						if (RowItem.getPrintoption().equals("True")) {
							SO so = invoiceadapter.getItem(invoiceadapter.getSelectedPosition());
							sosno = so.getSno().toString();
							socustomercode = so.getCustomerCode().toString();
							isInvoicePrint = true;

							params = new HashMap<String, String>();
							params.put("CompanyCode", cmpy);
							params.put("InvoiceNo", sosno);
							params.put("TranType", "IN");
							Log.d("sosno", "" + sosno);

							new XMLAccessTask(InvoiceHeader.this, valid_url,
									"fncGetInvoicePhoto", params, false,
									new GetInvoiceImage()).execute();

							new XMLAccessTask(InvoiceHeader.this, valid_url,
									"fncGetInvoiceSignature", params, false,
									new GetInvoiceSignatureforReprint()).execute();

							loadPrintData(R.string.generating_invoice);

						} else {
							Toast.makeText(InvoiceHeader.this,
									"Please Enable CheckBox", Toast.LENGTH_SHORT)
									.show();
						}
					} else {
						Toast.makeText(InvoiceHeader.this,
								"Please Configure the printer", Toast.LENGTH_SHORT)
								.show();
					}
				}else{
					Log.d("PDFActivity", "PDFActivity");
					SO so = invoiceadapter.getItem(invoiceadapter
							.getSelectedPosition());
					sosno = so.getSno().toString();
					new PDFActivity(InvoiceHeader.this,valid_url+"/"+FNCA4INVOICEGENERATE+"?CompanyCode="+cmpy+"&sInvoiceNo="+sosno
							+"&TranType="+trantype, "report.pdf").execute();

//new DownloadFile().execute("http://SFA.com:81/SFAAPI.asmx/fncA4InvoiceGenerate?CompanyCode=1&sInvoiceNo=HQ2016-000366", "report.pdf");
				}
			}
		});

		custsearchIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				clearSetterGetter();
				Intent i = new Intent(InvoiceHeader.this, InvoiceCustomer.class);
				startActivity(i);
				InvoiceHeader.this.finish();
			}
		});

		mInvoiceProductPrint.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(userArr.size()>0){
					invoiceDatePrintDialog();
				}else{
					new AsyncCallWSUserMaster().execute();
				}
			}
		});

		tab_notpaid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				status = "3"; // POD Pending
				status = "0"; // Not Paid
				((TextView) findViewById(R.id.txt_totaloutstanding)).setText(getResources().getString(R.string.totaloutstanding));
				tab_notpaid.setBackgroundResource(R.mipmap.tab_blue_bg_select);
				tab_overdue.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_paid.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_all.setBackgroundResource(R.mipmap.tab_blue_bg);

				try {
//					headerCustCode();
					AsyncCallWSSearchCustCode searchCustCode = new AsyncCallWSSearchCustCode();
					searchCustCode.execute();
				}catch (Exception e){
					e.printStackTrace();
				}

			}
		});

		tab_overdue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Toast.makeText(InvoiceHeader.this,"Pending",Toast.LENGTH_SHORT).show();
				((TextView) findViewById(R.id.txt_totaloutstanding)).setText(getResources().getString(R.string.totaloutstanding));
				status = "4";
				tab_notpaid.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_overdue.setBackgroundResource(R.mipmap.tab_blue_bg_select);
				tab_paid.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_all.setBackgroundResource(R.mipmap.tab_blue_bg);
				try {
					OverdueList();
				}catch (Exception e){
					e.printStackTrace();
				}

			}
		});

		tab_paid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				status = "1"; // Paid
				tab_notpaid.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_overdue.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_paid.setBackgroundResource(R.mipmap.tab_blue_bg_select);
				tab_all.setBackgroundResource(R.mipmap.tab_blue_bg);

				AsyncCallWSSearchCustCode searchCustCode = new AsyncCallWSSearchCustCode();
				searchCustCode.execute();
			}
		});

		tab_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				status = "2";  // all
				tab_notpaid.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_overdue.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_paid.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_all.setBackgroundResource(R.mipmap.tab_blue_bg_select);
				AsyncCallWSSearchCustCode searchCustCode = new AsyncCallWSSearchCustCode();
				searchCustCode.execute();
			}
		});

	}

	public void scanMarginScanner(View view) {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.setOrientationLocked(false);
		integrator.setCaptureActivity(SmallCaptureActivity.class);
		integrator.initiateScan();
	}

	public void invoiceDatePrintDialog() {

		cursor = FWMSSettingsDatabase.getPrinter();
		if (cursor.getCount() != 0) {

			AlertDialog.Builder alert = new AlertDialog.Builder(
					InvoiceHeader.this);
			TextView textView0 = new TextView(InvoiceHeader.this);
			TextView textview = new TextView(InvoiceHeader.this);
			TextView textview1 = new TextView(InvoiceHeader.this);
			final TextView textview2 = new TextView(InvoiceHeader.this);

			RadioGroup radioGroup = new RadioGroup(InvoiceHeader.this);
			final RadioButton[] radioBtb = new RadioButton[2];
			radioBtb[0] = new RadioButton(InvoiceHeader.this);
			radioBtb[1] = new RadioButton(InvoiceHeader.this);
			radioGroup.setOrientation(RadioGroup.VERTICAL);

//			LinearLayout layout1 = new LinearLayout(InvoiceHeader.this);
//			layout1.setOrientation(LinearLayout.HORIZONTAL);
//			layout1.setPadding(15, 15, 15, 15);
//			textView0.setPadding(0, 15, 0, 0);

//			layout1.addView(edCustCode);
//			layout1.addView(edCustoName);

			edCustCode = new EditText(InvoiceHeader.this);
			edCustoName = new EditText(InvoiceHeader.this);

			edprintUser = new EditText(InvoiceHeader.this);
			edprintDate = new EditText(InvoiceHeader.this);
			edprintDate1 = new EditText(InvoiceHeader.this);

			LinearLayout layout = new LinearLayout(InvoiceHeader.this);
			layout.setOrientation(LinearLayout.VERTICAL);
			alert.setTitle("Print");
			alert.setCancelable(true);
			layout.setPadding(15, 15, 15, 15);
			textview1.setPadding(0, 15, 0, 0);

			textView0.setText("Choose Customer");
			textview.setText("Enter from date");
			textview1.setText("Enter to date");
			textview2.setText("User");

			radioBtb[0].setText("Invoice By Product");
			radioBtb[1].setText("Invoice Summary");

			edCustCode.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					drawable.ic_search, 0);
			edCustCode.setPadding(5, 0, 0, 0);
			edCustCode.setBackgroundResource(drawable.edittext_bg);
			edCustCode.setCursorVisible(false);

			edCustoName.setBackgroundResource(R.drawable.labelbg);
//			edCustoName.setPadding(5, 0, 0, 0);
			edCustoName.setTextSize(12);
			edCustoName.setCursorVisible(false);
			edCustoName.setEnabled(false);

			edprintDate.setBackgroundResource(drawable.edittext_bg);
			edprintDate1.setBackgroundResource(drawable.edittext_bg);

			edprintUser.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					drawable.ic_search, 0);
			edprintUser.setCursorVisible(false);
			edprintUser.setText(SupplierSetterGetter.getUsername());

			edprintDate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.mipmap.ic_calendar, 0);
			edprintDate.setCursorVisible(false);
			edprintDate.setText(endeditTextDate.getText().toString());

			edprintDate1.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.mipmap.ic_calendar, 0);
			edprintDate1.setCursorVisible(false);
			edprintDate1.setText(endeditTextDate.getText().toString());

			radioGroup.addView(radioBtb[0]);
			radioGroup.addView(radioBtb[1]);
			layout.addView(radioGroup);
			layout.addView(textView0);
			layout.addView(edCustCode);
//			layout.addView(edCustoName);
			layout.addView(textview2);
			layout.addView(edprintUser);
			layout.addView(textview);
			layout.addView(edprintDate);
			layout.addView(textview1);
			layout.addView(edprintDate1);
			alert.setView(layout);

			edprintDate.setText(serverdate);
			edprintDate1.setText(serverdate);

			//Default radio button selection
			radioBtb[0].setChecked(true);
			//Default flag
			mInvoiceDatePrintFlag = "Invoice By Product";
			//Default WebMethodName
			mWebMethodName = FNC_GETINVOICEPRODUCTSUMMARY;

			if (FormSetterGetter.isReceiptAll()) {
				edprintUser.setEnabled(true);
				edprintUser.setBackgroundResource(drawable.edittext_bg);

				Log.d("True", "" + FormSetterGetter.isReceiptAll());
			} else {
				edprintUser.setPadding(5, 0, 0, 0);
				edprintUser
						.setBackgroundResource(drawable.edittext_bg_light_disable);
				edprintUser.setEnabled(false);
				Log.d("False", "" + FormSetterGetter.isReceiptAll());
			}
			radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						public void onCheckedChanged(RadioGroup group,
								int checkedId) {

							if (radioBtb[0].isChecked()) {
								//Invoice By Product
								mWebMethodName = FNC_GETINVOICEPRODUCTSUMMARY;
								mInvoiceDatePrintFlag = "Invoice By Product";
								//textview2.setVisibility(View.VISIBLE);
								//edprintUser.setVisibility(View.VISIBLE);

							} else if (radioBtb[1].isChecked()) {
								//Invoice Summary
								mWebMethodName = FNC_GETINVOICEHEADERBYUSER;
								mInvoiceDatePrintFlag = "Invoice Summary";
								//edprintUser.setVisibility(View.GONE);
								//textview2.setVisibility(View.GONE);

							}
						}
					});

			final DatePickerDialog.OnDateSetListener ptdate = new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					printCalendar.set(Calendar.YEAR, year);
					printCalendar.set(Calendar.MONTH, monthOfYear);
					printCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					printFromDate();
				}
			};

			final DatePickerDialog.OnDateSetListener ptdate1 = new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					printCalendar.set(Calendar.YEAR, year);
					printCalendar.set(Calendar.MONTH, monthOfYear);
					printCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					printToDate();
				}
			};
			edprintUser.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					alertDialogUserSearch();
				}
			});
			edCustCode.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialogCustomerSearch();
				}
			});
			edprintDate.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (MotionEvent.ACTION_UP == event.getAction())
						new DatePickerDialog(InvoiceHeader.this, ptdate,
								printCalendar.get(Calendar.YEAR), printCalendar
										.get(Calendar.MONTH), printCalendar
										.get(Calendar.DAY_OF_MONTH)).show();
					return false;
				}
			});

			edprintDate1.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (MotionEvent.ACTION_UP == event.getAction())
						new DatePickerDialog(InvoiceHeader.this, ptdate1,
								printCalendar.get(Calendar.YEAR), printCalendar
										.get(Calendar.MONTH), printCalendar
										.get(Calendar.DAY_OF_MONTH)).show();
					return false;
				}
			});

			alert.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// You will get as string input data in this
							// variable.
							// here we convert the input to a string and
							// show in a toast.
							userInvoice = edprintUser.getText().toString();
							fromDateInvoice = edprintDate.getText().toString();
							toDateInvoice = edprintDate1.getText().toString();
							userCustomer = edCustCode.getText().toString();
							userCustomerNme = edCustoName.getText().toString();
							// Toast.makeText(ReceiptHeader.this,currentdate,Toast.LENGTH_LONG).show();

							printStr = "InvoiceSummary";

							new GetInvoiceDatePrint().execute(mInvoiceDatePrintFlag,mWebMethodName,
									fromDateInvoice, toDateInvoice,userInvoice,userCustomer);

						} // End of onClick(DialogInterface dialog, int
							// whichButton)
					}); // End of alert.setPositiveButton
			alert.setNegativeButton("CANCEL",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
							dialog.cancel();
						}
					}); // End of alert.setNegativeButton
			AlertDialog alertDialog = alert.create();
			alertDialog.show();

		} else {
			Toast.makeText(InvoiceHeader.this, "Please Configure the printer",
					Toast.LENGTH_SHORT).show();
		}

	}

	private void alertDialogCustomerSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				InvoiceHeader.this);
		final EditText editText = new EditText(InvoiceHeader.this);
		final ListView listview = new ListView(InvoiceHeader.this);
		LinearLayout layout = new LinearLayout(InvoiceHeader.this);
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(InvoiceHeader.this, al);
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
					String keyValues = (String) mapEntry.getKey();
					String values = (String) mapEntry.getValue();

					Log.d("onclick loc", "" + values);

					if (values.contains("~")) {
						String[] parts = values.split("~");
						String name = parts[0];
						// String location = parts[1];

						edCustCode.setText(keyValues);
						edCustoName.setText(name);
					} else {
						edCustCode.setText(keyValues);
						edCustoName.setText(values);
					}



//					AsyncCallWSCustomerOutstanding taskAmt = new AsyncCallWSCustomerOutstanding();
//					taskAmt.execute();

					edCustCode.addTextChangedListener(new TextWatcher() {
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

							textlength = edCustCode.getText().length();
							edCustoName.setText("");

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
						InvoiceHeader.this, searchResults);
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

	private void printFromDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		edprintDate.setText(sdf.format(printCalendar.getTime()));
	}

	private void printToDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		edprintDate1.setText(sdf.format(printCalendar.getTime()));
	}

	public void alertDialogUserSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				InvoiceHeader.this);
		final EditText editText = new EditText(InvoiceHeader.this);
		final ListView listview = new ListView(InvoiceHeader.this);
		LinearLayout layout = new LinearLayout(InvoiceHeader.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("User");
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(InvoiceHeader.this,
				userArr);
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
					String key = (String) mapEntry.getKey();
					String value = (String) mapEntry.getValue();

					edprintUser.setText(value);
					edprintUser.addTextChangedListener(new TextWatcher() {
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
							textlength = edprintUser.getText().length();
						}
					});
				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(userArr);
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
				for (int i = 0; i < userArr.size(); i++) {
					String supplierName = userArr.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(userArr.get(i));
					}
				}

				arrayAdapterSupp = new CustomAlertAdapterSupp(
						InvoiceHeader.this, searchResults);
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

	private class GetInvoiceDatePrint extends
			AsyncTask<String, Void, ArrayList<Product>> {

		@Override
		protected void onPreExecute() {
			invoicePrintDateArr.clear();
			helper.showProgressDialog(R.string.generating_invoice);
		}

		@Override
		protected ArrayList<Product> doInBackground(String... param) {
			try {
				String flag = param[0].toString();
				String methodName = param[1].toString();

				Log.d("flag", "" + param[0].toString());
				Log.d("methodName", "" + param[1].toString());

				hashValue.put("CompanyCode", companyCode);
				hashValue.put("FromDate", param[2].toString());
				if(methodName.matches(FNC_GETINVOICEHEADERBYUSER)){
					hashValue.put("ToDate", param[3].toString());
				}else{
					hashValue.put("toDate", param[3].toString());
				}


				if(methodName.matches(FNC_GETINVOICEHEADERBYUSER)){
					hashValue.put("User", param[4].toString());
					Log.d("User", "" + param[4].toString());
				}else{
					hashValue.put("CreateUser", param[4].toString());
					Log.d("CreateUser", "" + param[4].toString());
				}
				hashValue.put("CustomerCode",param[5].toString());

				Log.d("CustomerCode",param[5].toString());


				userInvoice = param[4].toString();

				Log.d("FromDate", "" + param[2].toString());
				Log.d("ToDate", "" + param[3].toString());


				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						// Offline
						// jsonString =
						// offlineDatabase.getReceiptHeader(hashValue);

					} else { // online
						jsonString = SalesOrderWebService.getSODetail(hashValue, methodName);

					}
				} else if (onlineMode.matches("False")) { // perman offline
					// jsonString = offlineDatabase.getReceiptHeader(hashValue);
				}
				Log.d("jsonStr ", "" + jsonString);
				if (jsonString != null && !jsonString.isEmpty()) {
					jsonResponse = new JSONObject(jsonString);
					jsonMainNode = jsonResponse.optJSONArray("SODetails");

					int lengJsonArray = jsonMainNode.length();
					if (lengJsonArray > 0) {
						if(flag.matches("Invoice By Product")){
						for (int i = 0; i < lengJsonArray; i++) {
							try {
								JSONObject 	jsonChildNodes = jsonMainNode.getJSONObject(i);
								Product product = new Product();
								String productname = jsonChildNodes.optString(
										"ProductName").toString();
								String cqty = jsonChildNodes.optString("CQty")
										.toString().split("\\.")[0];
								String lqty = jsonChildNodes.optString("LQty")
										.toString().split("\\.")[0];
//								String lqty = "2";
								String qty1 = jsonChildNodes.optString("Qty").toString();
								String qty = jsonChildNodes.optString("Qty")
										.toString().split("\\.")[0];
								String focQty =jsonChildNodes.optString( "FOCQty").toString();
								String exchangeQty =jsonChildNodes.optString("ExchangeQty").toString();

//								String focQty ="2.00";
//								String exchangeQty = "3.00";
								Log.d("focQty","-->"+focQty+" "+exchangeQty+" "+qty);

								if(focQty.matches("null")){
									focQty="0";
								}else{
									focQty = focQty.split("\\.")[0];
								}
								if(exchangeQty.matches("null")){
									exchangeQty="0";
								}else{
									exchangeQty = exchangeQty.split("\\.")[0];
								}
								int cartonQty = cqty.equals("") ? 0 : Integer
										.valueOf(cqty);


								String numberD = qty1.substring (qty1.indexOf ( "." ) );
								double value =Double.parseDouble(numberD);
								double tot_qty = Double.parseDouble(qty1);
								Log.d("DecimalValue","-->"+ twoDecimalPoint(tot_qty));

								product.setProductName(productname);
								product.setCqty(cqty);
								product.setLqty(lqty);
								product.setFoc(focQty);
								product.setExchange(exchangeQty);
								if(value>0){
									product.setQty(twoDecimalPoint(tot_qty));
								}else{
									product.setQty(qty);
								}
								invoicePrintDateArr.add(product);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						}else if(flag.matches("Invoice Summary")){
							for (int i = 0; i < lengJsonArray; i++) {
								try {
									JSONObject 	jsonChildNodes = jsonMainNode.getJSONObject(i);
									Product product = new Product();
									String invoiceNo = jsonChildNodes.optString(
											"InvoiceNo").toString();
									String customerName = jsonChildNodes.optString("CustomerName")
											.toString();
									String netTotal = jsonChildNodes.optString("NetTotal")
											.toString();
									String CreditAmount = jsonChildNodes.optString("CreditAmount")
											.toString();
									String paidAmount = jsonChildNodes.optString("PaidAmount")
											.toString();
                                    String BalanceAmount = jsonChildNodes.optString("BalanceAmount")
                                            .toString();

									double creditamt=0,paidamt=0, paidamountfinal=0,balanceamountfinal=0;
									if(CreditAmount!=null && !CreditAmount.isEmpty()){
										creditamt = Double.parseDouble(CreditAmount);
									}

									if(paidAmount!=null && !paidAmount.isEmpty()){
										paidamt = Double.parseDouble(paidAmount);
									}

                                    if(BalanceAmount!=null && !BalanceAmount.isEmpty()){
                                        balanceamountfinal = Double.parseDouble(BalanceAmount);
                                    }

									paidamountfinal = paidamt + creditamt;

									Log.d("creditamt",""+creditamt);
									Log.d("nettotalfinal",""+paidamountfinal);
                                    Log.d("balanceamountfinal",""+balanceamountfinal);

									product.setNo(invoiceNo);
									product.setName(customerName);
//									product.setNetTotal(""+paidamountfinal); // changed on 31.08.2017 given by sheik
                                    product.setNetTotal(""+netTotal);
                                    product.setBalanceAmount(balanceamountfinal);
									product.setPaidAmount(paidamountfinal);
									invoicePrintDateArr.add(product);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
						return invoicePrintDateArr;
					} else {
						return null;
					}
				} else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Product> result) {
			if (result != null && !result.isEmpty()) {
				try {

					Collections.sort(invoicePrintDateArr, new ComparableUtil());
					for (Product employee : invoicePrintDateArr) {
						System.out.println(""+employee.getBalanceAmount());
					}

					invoiceDatePrint();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				helper.dismissProgressDialog();
				Toast.makeText(InvoiceHeader.this,
						"No "+ mInvoiceDatePrintFlag +" found", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

	private void invoiceDatePrint() throws IOException {

		helper.dismissProgressDialog();
		final String locationName = SalesOrderSetGet.getLocationname();
		final String locationCode = SalesOrderSetGet.getLocationcode();
		final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		final String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
		if(printertype.matches("Zebra iMZ320")) {

			try {
				Printer printer = new Printer(InvoiceHeader.this, macaddress);
				printer.printInvoiceDate(mInvoiceDatePrintFlag,userInvoice,fromDateInvoice,
						toDateInvoice, locationCode, locationName,
						invoicePrintDateArr,userCustomer,userCustomerNme);

			} catch (IllegalArgumentException e) {
				helper.showLongToast(R.string.error_configure_printer);
			}
		}
		else if(printertype.matches("4 Inch Bluetooth")){
//			helper.updateProgressDialog(InvoiceHeader.this.getString(R.string.creating_file_for_printing));
			if (BluetoothAdapter.checkBluetoothAddress(macaddress))
			{
				BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
				// Attempt to connect to the device
				GlobalData.mService.setHandler(mHandler);
				GlobalData.mService.connect(device,true);

			}

			}
		else if(printertype.matches("3 Inch Bluetooth Generic")){
			try {
				final CubePrint print = new CubePrint(InvoiceHeader.this,macaddress);
				print.initGenericPrinter();
				print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
					@Override
					public void initCompleted() {
						print.InvoiceSummmaryPrint(mInvoiceDatePrintFlag,userInvoice,fromDateInvoice,
								toDateInvoice, locationCode, locationName,
								invoicePrintDateArr);
						print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
							@Override
							public void onCompleted() {
								helper.showLongToast(R.string.printed_successfully);
							}
						});
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(printertype.matches("Zebra iMZ320 4 Inch")){
			try {
				PrinterZPL printer = new PrinterZPL(InvoiceHeader.this, macaddress);
				printer.printInvoiceDate(mInvoiceDatePrintFlag,userInvoice,fromDateInvoice,
						toDateInvoice, locationCode, locationName,
						invoicePrintDateArr);

			} catch (IllegalArgumentException e) {
				helper.showLongToast(R.string.error_configure_printer);
			}
		}
	}

	public void loadPrintData(int generatingInvoice) {

		if (onlineMode.matches("True")) {
			if (checkOffline == true) {

				// Offline
				String offlinePrintStatus = OfflineDatabase.getPrintStatus(
						"tblGetInvoiceDetail", "InvoiceNo", sosno);
				Log.d("offlinePrintStatus", "" + offlinePrintStatus);
				if (offlinePrintStatus != null && !offlinePrintStatus.isEmpty()) {

					helper.showProgressDialog(generatingInvoice);
					AsyncPrintCall task = new AsyncPrintCall();
					task.execute();
				} else {
					if (isInvoicePrint) {
						Toast.makeText(InvoiceHeader.this,
								"Online Invoice cannot be print",
								Toast.LENGTH_SHORT).show();
					} else {
						if (status.matches("3")) {
							helper.showProgressDialog(generatingInvoice);
							AsyncPrintCall task = new AsyncPrintCall();
							task.execute();
						} else {
							Toast.makeText(InvoiceHeader.this,
									"Online Invoice cannot be print",
									Toast.LENGTH_SHORT).show();
						}
					}
				}

			} else { // Online
				helper.showProgressDialog(generatingInvoice);
				AsyncPrintCall task = new AsyncPrintCall();
				task.execute();

			}
		} else if (onlineMode.matches("False")) { // perman offline

			// Offline
			String offlinePrintStatus = OfflineDatabase.getPrintStatus(
					"tblGetInvoiceDetail", "InvoiceNo", sosno);
			Log.d("offlinePrintStatus", "" + offlinePrintStatus);
			if (offlinePrintStatus != null && !offlinePrintStatus.isEmpty()) {
				helper.showProgressDialog(generatingInvoice);
				AsyncPrintCall task = new AsyncPrintCall();
				task.execute();
			} else {
				if (isInvoicePrint) {
					Toast.makeText(InvoiceHeader.this,
							"Online Invoice cannot be print",
							Toast.LENGTH_SHORT).show();
				} else {
					if (status.matches("3")) {
						helper.showProgressDialog(generatingInvoice);
						AsyncPrintCall task = new AsyncPrintCall();
						task.execute();
					} else {
						Toast.makeText(InvoiceHeader.this,
								"Online Invoice cannot be print",
								Toast.LENGTH_SHORT).show();
					}
				}
			}

		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		SO so = invoiceadapter.getItem(info.position);
		sosno = so.getSno().toString();
		socustomercode = so.getCustomerCode().toString();
		socustomername = so.getCustomerName().toString();
		soDONO = so.getDono().toString();
		sodate = so.getDate().toString();

		ConvertToSetterGetter.setDoNo("");
//		statusvalue = so.getStatus().toString();
		Log.d("socustomercode",socustomercode+"socustomername"+socustomername+"statusvalue"+soDONO);
		TextView IsClosed_status = (TextView) v.findViewById(R.id.IsClosed_status);
		TextView IsPosted_status = (TextView) v.findViewById(R.id.IsPosted_status);
		isClosedval = IsClosed_status.getText().toString();
		IsPostedval = IsPosted_status.getText().toString();

		if(isClosedval!=null && !isClosedval.isEmpty()){

		}else{
			isClosedval="";
		}

		if(IsPostedval!=null && !IsPostedval.isEmpty()){

		}else{
			IsPostedval="";
		}

		Log.d("isClosedval","c "+isClosedval);
		Log.d("isClosedval","p "+IsPostedval);

		menu.setHeaderTitle(sosno);
		if (offlineLayout.getVisibility() == View.GONE) {

			if (addInvoice.matches("Add Invoice")) {
				menu.add(0, v.getId(), 0, getResources().getString(R.string.returns));
			}
		}

		receiptsStr = FormSetterGetter.getReceipts();
		if (receiptsStr.matches("Receipts")) {

			menu.add(0, v.getId(), 0, getResources().getString(R.string.cash_collection));
		}

		if (FormSetterGetter.isEditInvoice() == true) {
			// offline
			ArrayList<HashMap<String, String>> tempArr = new ArrayList<HashMap<String, String>>();
			tempArr.clear();
			OfflineSetterGetter.setStockUpdateArr(tempArr);

			String dstatus = OfflineDatabase.getInvoiceDownloadStatus(sosno);

			if (offlineLayout.getVisibility() == View.VISIBLE) {
				if (dstatus.matches("1")) {
					if(statusvalue.matches("Paid")||(status.matches("1"))){

					}else{
						menu.add(0, v.getId(), 0, getResources().getString(R.string.edit_invoice));
					}
				}
			} else {
				if(statusvalue.matches("Paid")||(status.matches("1"))){

				}else{
					if(Company.getShortCode().matches("RAJAGRO")){
						Calendar cal = Calendar.getInstance();
						DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
						System.out.println("Today'sDateis "+dateFormat.format(cal.getTime()));
						String date = dateFormat.format(cal.getTime());

						cal.add(Calendar.DATE, -1);
						System.out.println("Yesterday'sdate was "+dateFormat.format(cal.getTime()));
						Log.d("yesterdayDate","-->"+dateFormat.format(cal.getTime()));
						String previousDate = dateFormat.format(cal.getTime());
						if((sodate.matches(date)) || (sodate.matches(previousDate))){
							menu.add(0, v.getId(), 0, getResources().getString(R.string.edit_invoice));
						}
					}else{
						menu.add(0, v.getId(), 0, getResources().getString(R.string.edit_invoice));
					}

				}

			}
		}
		if (offlineLayout.getVisibility() == View.GONE) {
			if (FormSetterGetter.isDeleteInvoice() == true) {
				if(statusvalue.matches("Paid")||(status.matches("1"))){

				}else {
					menu.add(0, v.getId(), 0, getResources().getString(R.string.delete_invoice));
				}
			}

			menu.add(1, v.getId(), 0, getResources().getString(R.string.print_preview));

		}

		menu.add(1, v.getId(), 0, getResources().getString(R.string.print_delivery_order));

		String haveEmail = strValidate(SalesOrderSetGet.getHaveemailintegration());

		if(haveEmail.matches("1")){
			menu.add(1, v.getId(), 0, getResources().getString(R.string.send_email));
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		SO so = invoiceadapter.getItem(adapterInfo.position);
		sosno = so.getSno().toString();
		sodate = so.getDate().toString();
		socustomercode = so.getCustomerCode().toString();
		socustomername = so.getCustomerName().toString();
		soDONO = so.getDono().toString();
		soTranType = so.getSalesType().toString();
		soaddress = so.getCustomeraddress1()+" "+so.getCustomeraddress2()+" "+so.getCustomeraddress3();
		Log.d("sodate", sodate);
		Log.d("socustomername", socustomername);
		if (item.getTitle() == getResources().getString(R.string.cash_collection)) {
			SOTDatabase.deleteImage();
			Intent i = new Intent(InvoiceHeader.this,
					InvoiceCashCollection.class);
			i.putExtra("invNo", sosno);
			i.putExtra("customerCode", socustomercode);
			startActivity(i);
			SalesOrderSetGet.setHeader_flag("InvoiceHeader");
			InvoiceHeader.this.finish();

		}
		if (item.getTitle() == getResources().getString(R.string.print_preview)) {
			SOTDatabase.deleteImage();
			helper.showProgressDialog(R.string.invoice_printpreview);
			AsyncPrintCall task = new AsyncPrintCall();
			task.execute();
			/*
			 * for (HashMap<String, String> hashMap : al) { for (String key :
			 * hashMap.keySet()) { if (key.equals(socustomercode)) {
			 * System.out.println(hashMap.get(key)); socustomername =
			 * hashMap.get(key); } } }
			 */

		} else if (item.getTitle() == getResources().getString(R.string.edit_invoice)) {

			Log.d("DBCheckEdit","values"+SOTDatabase.getBatCursor().getCount());

			if(isClosedval.equalsIgnoreCase("True") ||
					IsPostedval.equalsIgnoreCase("True")){

				if(isClosedval.equalsIgnoreCase("True")){
					Toast.makeText(InvoiceHeader.this,"This invoice is Closed. " +
							"Cannot able to edit or delete",Toast.LENGTH_LONG).show();
				} else if(IsPostedval.equalsIgnoreCase("True")){
					Toast.makeText(InvoiceHeader.this,"This invoice is Posted. " +
							"Cannot able to edit or delete",Toast.LENGTH_LONG).show();
				}

			}else{
				SalesOrderSetGet.setSoRemarks("");
				SalesOrderSetGet.setSoAdditionalInfo("");
				CustomerSetterGetter.setDiscountPercentage("");
				CustomerSetterGetter.setBillDiscount("");
				SOTDatabase.deleteImage();
			/*
			 * for (HashMap<String, String> hashMap : al) { for (String key :
			 * hashMap.keySet()) { if (key.equals(socustomercode)) {
			 * System.out.println(hashMap.get(key)); socustomername =
			 * hashMap.get(key); } } }
			 */

				SalesOrderSetGet.setCustname(socustomername);
				sotdb.truncateTables();

				new AsyncCallWSGetSignature(false).execute();

			}


		} else if (item.getTitle() == getResources().getString(R.string.delete_invoice)) {

			if(isClosedval.equalsIgnoreCase("True") ||
					IsPostedval.equalsIgnoreCase("True")){

				if(isClosedval.equalsIgnoreCase("True")){
					Toast.makeText(InvoiceHeader.this,"This invoice is Closed. " +
							"Cannot able to edit or delete",Toast.LENGTH_LONG).show();
				} else if(IsPostedval.equalsIgnoreCase("True")){
					Toast.makeText(InvoiceHeader.this,"This invoice is Posted. " +
							"Cannot able to edit or delete",Toast.LENGTH_LONG).show();
				}
			}else{
				Log.d("getTranType", "-->" + soTranType);
					deleteAlertDialog();


			}


		} else if (item.getTitle() == getResources().getString(R.string.returns)) {
			SOTDatabase.deleteAllProduct();
			SOTDatabase.deleteBillDisc();
			SOTDatabase.deleteImage();
			sotdb.truncateTables();
			Intent i = new Intent(InvoiceHeader.this, InvoiceReturn.class);
			i.putExtra("invNo", sosno);
			i.putExtra("CustomerCode", socustomercode);
			startActivity(i);
			SalesOrderSetGet.setHeader_flag("InvoiceHeader");
			InvoiceHeader.this.finish();
		} else if (item.getTitle() == getResources().getString(R.string.print_delivery_order)) {
//			custCode = socustomercode;
			SOTDatabase.deleteImage();
			isInvoicePrint = false;
			new AsyncCallWSGetSignature(true).execute();
		}else if (item.getTitle() == getResources().getString(R.string.send_email)) {
			new AsyncCallWSCustomerEmail().execute();
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

	public void spinnerstatus() {
		statusvalue = spinner_status.getSelectedItem().toString();
		Log.d("getShortCodeDescription","--->"+Company.getShortCode());
		if(Company.getShortCode().matches("SUPERSTAR")){
			txt_totaloutstanding.setVisibility(View.VISIBLE);
			totaloutstanding.setVisibility(View.VISIBLE);
			symbol.setVisibility(View.VISIBLE);
		}else {
			if (statusvalue.matches(getResources().getString(R.string.not_paid))) {
				status = "0";
				Log.e("searchstatus", status);
				((TextView) findViewById(R.id.txt_totaloutstanding)).setText(getResources().getString(R.string.totaloutstanding));

				tab_notpaid.setBackgroundResource(R.mipmap.tab_blue_bg_select);
				tab_overdue.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_paid.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_all.setBackgroundResource(R.mipmap.tab_blue_bg);
			}
			if (statusvalue.matches(getResources().getString(R.string.paid))) {
				status = "1";
				Log.e("searchstatus", status);
				((TextView) findViewById(R.id.txt_totaloutstanding)).setText(getResources().getString(R.string.total_paid));

				tab_notpaid.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_overdue.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_paid.setBackgroundResource(R.mipmap.tab_blue_bg_select);
				tab_all.setBackgroundResource(R.mipmap.tab_blue_bg);
			}
			if (statusvalue.matches(getResources().getString(R.string.all))) {
				status = "2";
				Log.e("searchstatus", status);
				((TextView) findViewById(R.id.txt_totaloutstanding)).setText(getResources().getString(R.string.total_amount));

				tab_notpaid.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_overdue.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_paid.setBackgroundResource(R.mipmap.tab_blue_bg);
				tab_all.setBackgroundResource(R.mipmap.tab_blue_bg_select);
			}
			if (statusvalue.matches(getResources().getString(R.string.pod_pending))) {
				status = "3";
				Log.e("searchstatus", status);
				((TextView) findViewById(R.id.txt_totaloutstanding)).setText(getResources().getString(R.string.totaloutstanding));
			}
		}

	}

	public void deleteAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				InvoiceHeader.this);
		builder.setTitle("Confirm Delete");
		builder.setIcon(R.mipmap.delete);
		builder.setMessage("Do you want to delete?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// AsyncCallWSSalesDelete salesDelete = new
				// AsyncCallWSSalesDelete();
				// salesDelete.execute();

				Log.d("getTranType", "-->" + soTranType);
					AsyncCallWSCheckDelete checkDelete = new AsyncCallWSCheckDelete();
					checkDelete.execute();


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

	@SuppressWarnings("deprecation")
	public void okDialog() {
		AlertDialog ad = new AlertDialog.Builder(this).create();
		ad.setCancelable(false); // This blocks the 'BACK' button
		ad.setMessage("Amount already collected for this invoice. Please delete the corresponding receipt");
		ad.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		ad.show();
	}

	private class AsyncCallWSGetSignature extends AsyncTask<Void, Void, Void> {

		boolean isPrintCall;
		private String transtype = "";

		public AsyncCallWSGetSignature(boolean printCall) {
			isPrintCall = printCall;
		}

		@Override
		protected void onPreExecute() {
			loadprogress();
			dialogStatus = "";
			dialogStatus = checkInternetStatus();
			getCurrentlocation();
			getPhotoimage = "";
			getSignatureimage = "";
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if (isPrintCall == true) {
				transtype = "POD";
			} else {
				transtype = "IN";
			}

			String cmpy = SupplierSetterGetter.getCompanyCode();
			params = new HashMap<String, String>();
			params.put("CompanyCode", cmpy);
			params.put("InvoiceNo", sosno);
			params.put("TranType", transtype);

			Log.d("sosno", "" + sosno);

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) { // temp offline
						getPhotoimage = offlineDatabase.getPhotoImage(sosno,
								transtype);
						getSignatureimage = offlineDatabase.getSignImage(sosno,
								transtype);
					} else {
						Log.d("CheckOffline Alert -->", "False");
//						finish();
						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					}

				} else {// Online

					new XMLAccessTask(InvoiceHeader.this, valid_url,
							"fncGetInvoicePhoto", params, false,
							new GetInvoiceImage()).execute();

					new XMLAccessTask(InvoiceHeader.this, valid_url,
							"fncGetInvoiceSignature", params, false,
							new GetInvoiceSignature(isPrintCall)).execute();
				}

			} else if (onlineMode.matches("False")) { // permt Offline
				getSignatureimage = offlineDatabase.getSignImage(sosno,
						transtype);
				getPhotoimage = offlineDatabase.getPhotoImage(sosno, transtype);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (isPrintCall == false) {
				Log.d("!isPrintCall", "!isPrintCall");
				AsyncCallWSInvoceDetail task = new AsyncCallWSInvoceDetail();
				task.execute();
			}

			if (isPrintCall == true) {
				if (onlineMode.matches("True")) {
					if (checkOffline == true) { // temp offline
						SOTDatabase.storeImage(1, getSignatureimage,
								getPhotoimage);
						progressBar.setVisibility(View.GONE);
						spinnerLayout.setVisibility(View.GONE);
						enableViews(salesO_parent, true);
						printCallDialog();

					} else {// Online

					}

				} else if (onlineMode.matches("False")) { // permt Offline
					SOTDatabase.storeImage(1, getSignatureimage, getPhotoimage);
					progressBar.setVisibility(View.GONE);
					spinnerLayout.setVisibility(View.GONE);
					enableViews(salesO_parent, true);
					printCallDialog();
				}
			}
		}
	}

	private class AsyncCallWSInvoceDetail extends AsyncTask<Void, Void, Void> {
		String appType = LogOutSetGet.getApplicationType();
		String dialogStatus;
		String sales_prodCode="";

		@Override
		protected void onPreExecute() {

			SalesOrderSetGet.setBalanceAmount("0");
			SalesOrderSetGet.setNetTotal("0");
			InvDetailsArr.clear();
			InvHeadersArr.clear();
			InvBarcodeArr.clear();
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			Log.d("Invoice No:", sosno);
			try {
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");

							InvDetailsArr = offlineDatabase
									.getInvoiceDetails(sosno);
							InvHeadersArr = offlineDatabase
									.getInvoiceHeader(sosno);
							OfflineSalesOrderWebService.getCustomerTaxOffline(socustomercode);
						} else {
							Log.d("CheckOffline Alert -->", "False");
//							finish();
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {
						Log.d("checkOffline Status -->", "Online");
						InvDetailsArr = SalesOrderWebService.getInvoiceDetails(
								sosno, "fncGetInvoiceDetail");
						InvHeadersArr = SalesOrderWebService.getInvoiceHeader(
								sosno, "fncGetInvoiceHeaderByInvoiceNo");
						SalesOrderWebService.getCustomerTax(socustomercode, "fncGetCustomer");
						if (appType.matches("W")) {
							InvBarcodeArr = SalesOrderWebService
									.getProductBarcode(sosno, "InvoiceNo",
											"fncGetInvoiceCartonDetail");
						}


						if(soTranType.matches("C")){

							if (InvDetailsArr != null) {
								for (int i = 0; i < InvDetailsArr.size(); i++) {
									sales_prodCode = InvDetailsArr.get(i).get("ProductCode");
								}
							}
							Log.d("CustomerCode", "-->"  + sales_prodCode);
							InvConsignmentArr = SalesOrderWebService.
									getStockConsignmentArrs("fncGetConsignmentProductStock", "", sales_prodCode);
						}
					}

				} else if (onlineMode.matches("False")) {
					Log.d("Invoice Online Mode -->", onlineMode);

					InvDetailsArr = offlineDatabase.getInvoiceDetails(sosno);
					InvHeadersArr = offlineDatabase.getInvoiceHeader(sosno);
					OfflineSalesOrderWebService.getCustomerTaxOffline(socustomercode);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.d("InvDetailsArr", "off" + InvDetailsArr.toString());

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {


			// Log.d("getSignatureimage",
			// "getSignatureimage"+getSignatureimage);

			if (!SalesOrderSetGet.getNetTotal().matches(
					SalesOrderSetGet.getBalanceAmount())) {
				okDialog();
			} else {
				Log.d("getSalesType","-->"+soTranType);
				if(soTranType.matches("C")){
					Intent i = new Intent(InvoiceHeader.this, ConsignmentSummary.class);
					i.putExtra("InvDetails", InvDetailsArr);
					i.putExtra("InvHeader", InvHeadersArr);
					if (appType.matches("W")) {
						i.putExtra("InvBarcode", InvBarcodeArr);
					}
//					i.putExtra("InvConsignment",InvConsignmentArr);
					i.putExtra("getSignatureimage", getSignatureimage);
					i.putExtra("getPhotoimage", getPhotoimage);
					startActivity(i);
					SalesOrderSetGet.setTranType("COI");
					SalesOrderSetGet.setHeader_flag("InvoiceHeader");
					InvoiceHeader.this.finish();
				}else{
					Intent i = new Intent(InvoiceHeader.this, InvoiceSummary.class);
					i.putExtra("InvDetails", InvDetailsArr);
					i.putExtra("InvHeader", InvHeadersArr);
					if (appType.matches("W")) {
						i.putExtra("InvBarcode", InvBarcodeArr);
					}
					i.putExtra("getSignatureimage", getSignatureimage);
					i.putExtra("getPhotoimage", getPhotoimage);
					startActivity(i);
					SalesOrderSetGet.setHeader_flag("InvoiceHeader");
					InvoiceHeader.this.finish();
				}
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);
		}
	}

	public class GetInvoiceImage implements CallbackInterface {
		public GetInvoiceImage() {
		}

		@Override
		public void onSuccess(NodeList nl) {

			getPhotoimage = "";
			for (int i = 0; i < nl.getLength(); i++) {

				Element e = (Element) nl.item(i);
				getPhotoimage = XMLParser.getValue(e, "RefPhoto");
			}

			Log.d("getPhotoimage", "getPhotoimage" + getPhotoimage);
			// if(isPrintCall){
			//
			// SOTDatabase.storeImage(1, getSignatureimage, "");
			// progressBar.setVisibility(View.GONE);
			// spinnerLayout.setVisibility(View.GONE);
			// enableViews(deliveryO_parent, true);
			// printCallDialog();
			// }
		}

		@Override
		public void onFailure(ErrorType error) {
			// if(isPrintCall){
			// progressBar.setVisibility(View.GONE);
			// spinnerLayout.setVisibility(View.GONE);
			// enableViews(deliveryO_parent, true);
			// printCallDialog();
			// }
			onError(error);
		}

	}

	public class GetInvoiceSignature implements CallbackInterface {
		boolean isPrintCall;

		public GetInvoiceSignature(boolean printCall) {
			isPrintCall = printCall;
		}

		@Override
		public void onSuccess(NodeList nl) {

			getSignatureimage = "";
			for (int i = 0; i < nl.getLength(); i++) {

				Element e = (Element) nl.item(i);
				getSignatureimage = XMLParser.getValue(e, "RefSignature");
			}

			Log.d("getPhotoimage", "getPhotoimage" + getPhotoimage);
			Log.d("getSignatureimage", "getSignatureimage" + getSignatureimage);
			if (isPrintCall) {

				SOTDatabase.storeImage(1, getSignatureimage, getPhotoimage);
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesO_parent, true);
				printCallDialog();
			}
		}

		@Override
		public void onFailure(ErrorType error) {

			if (isPrintCall) {
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesO_parent, true);
				printCallDialog();
			}
			onError(error);
		}

	}

	public class GetInvoiceSignatureforReprint implements CallbackInterface {

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

	private class AsyncCallWSCheckDelete extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			SalesOrderWebService.getInvoiceHeader(sosno,
					"fncGetInvoiceHeaderByInvoiceNo");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (!SalesOrderSetGet.getNetTotal().matches(
					SalesOrderSetGet.getBalanceAmount())) {
				okDialog();
			} else {
				AsyncCallWSSalesDelete deleteinvoice = new AsyncCallWSSalesDelete();
				deleteinvoice.execute();
			}
		}
	}

	private class AsyncCallWSSalesDelete extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(InvoiceHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			InvoiceHeader.this.addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesO_parent, false);
			progressBar = new ProgressBar(InvoiceHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(drawable.greenprogress));
			spinnerLayout.addView(progressBar);

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String user = SupplierSetterGetter.getUsername();
			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			HashMap<String, String> hashValue = new HashMap<String, String>();
			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("InvoiceNo", sosno);
			hashValue.put("User", user);

			String jsonString = WebServiceClass.parameterService(hashValue,
					"fncDeleteInvoice");

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("JsonArray");

				int lengthJsonArr = jsonMainNode.length();

				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode;
					jsonChildNode = jsonMainNode.getJSONObject(i);
					Deletestring = jsonChildNode.optString("Result").toString();

					Log.d("Deletestring","-->"+Deletestring);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (!Deletestring.matches("")&& Deletestring.matches("True")) {

				if(soTranType.matches("C")){
						new AsyncCallWSConsignmentDelete().execute();
					}else {
						Toast.makeText(InvoiceHeader.this, "Delete Successfully", Toast.LENGTH_LONG).show();
						AsyncCallWSIOHeader task = new AsyncCallWSIOHeader();
						task.execute();
					}
			} else {
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesO_parent, true);

				Toast.makeText(InvoiceHeader.this, "Failed", Toast.LENGTH_LONG).show();
			}
		}
	}

	private class AsyncCallWSSalesOrder extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		String dialogStatus;

		@Override
		protected void onPreExecute() {
			params = new HashMap<String, String>();
			al.clear();
			userArr.clear();
			spinnerLayout = new LinearLayout(InvoiceHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			InvoiceHeader.this.addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesO_parent, false);
			progressBar = new ProgressBar(InvoiceHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(drawable.greenprogress));
			spinnerLayout.addView(progressBar);

			dialogStatus = checkInternetStatus();
			Log.d("dialogcheck1",dialogStatus);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			// Offline
			SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
			String finalDate = timeFormat.format(new Date());
			System.out.println(finalDate);

			checkOffline = OfflineCommon.isConnected(InvoiceHeader.this);
			onlineMode = OfflineDatabase.getOnlineMode();

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) {
						Log.d("CheckOffline Alert -->", "True");
						al = getCustomerOffline();
						serverdate = finalDate;
					} else {
						Log.d("CheckOffline Alert -->", "False");
						finish();
						Toast.makeText(getApplicationContext(),"No Internet Connection!!",Toast.LENGTH_SHORT).show();
						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					}

				} else {
					Log.d("checkOffline Status -->", "False");
					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put("CompanyCode", companyCode);
					hm.put("VanCode", select_van);

					al = SalesOrderWebService.getWholeCustomer(hm,
							"fncGetCustomerForSearch");

					serverdate = DateWebservice
							.getDateService("fncGetServerDate");

//					params.put("Code", "W!napp@!@#^");
//					loccode = SetStockInDetail.getLocationcode(
//							"fncGetLocation", companyCode);
				}

			} else if (onlineMode.matches("False")) {
				al = getCustomerOffline();
				serverdate = finalDate;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (serverdate != null) {
			//	starteditTextDate.setText(DateTime.date(serverdate));
//				endeditTextDate.setText(serverdate);

				starteditTextDate.setText("");  // added new
				endeditTextDate.setText("");

				sDate = starteditTextDate.getText().toString();
				eDate = endeditTextDate.getText().toString();

				AsyncCallWSIOHeader task = new AsyncCallWSIOHeader();
				task.execute();
			}else {
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesO_parent, true);
			}


		}
	}

	private class AsyncCallWSUserMaster extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			params = new HashMap<String, String>();

			userArr.clear();
			spinnerLayout = new LinearLayout(InvoiceHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			InvoiceHeader.this.addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesO_parent, false);
			progressBar = new ProgressBar(InvoiceHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);


		}

		@Override
		protected Void doInBackground(Void... arg0) {
			params.put("Code", "W!napp@!@#^");
			userArr = SalesOrderWebService.getAllUser(
					"fncGetUserMaster", params);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);

			getOnlineCustomer();
			invoiceDatePrintDialog();
		}

	}

	private void getOnlineCustomer() {
		SoapObject request = new SoapObject(NAMESPACE, "fncGetCustomerForSearch");
		// PropertyInfo companyCode = new PropertyInfo();
		//
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		// companyCode.setName("CompanyCode");
		// companyCode.setValue(cmpnyCode);
		// companyCode.setType(String.class);
		// request.addProperty(companyCode);

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("CompanyCode", cmpnyCode);
		hm.put("VanCode", select_van);

		for (Entry<String, String> entry : hm.entrySet()) {

			PropertyInfo prodCode = new PropertyInfo();

			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);

			System.out.printf("%s", entry.getKey());
			System.out.printf("%s%n", entry.getValue());
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(valid_url);
		try {
			androidHttpTransport.call(SOAP_ACTION + "fncGetCustomerForSearch", envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			suppTxt = response.toString();
			custresult = " { SaleOCustomer : " + suppTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(custresult);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SaleOCustomer");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String customercode = jsonChildNode.optString(
							"CustomerCode").toString();
					String customername = jsonChildNode.optString(
							"CustomerName").toString();
					String ReferenceLocation = jsonChildNode.optString(
							"ReferenceLocation").toString();

					Log.d("ReferenceLocation", "" + ReferenceLocation);

					HashMap<String, String> customerhm = new HashMap<String, String>();
					if (ReferenceLocation != null
							&& !ReferenceLocation.isEmpty()) {
						customerhm.put(customercode, customername + "~"
								+ ReferenceLocation);
					} else {
						customerhm.put(customercode, customername);
					}

					al.add(customerhm);
					hashmap.putAll(customerhm);
					alinvoice.add(customercode);

				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			suppTxt = "Error occured";
		}
	}


	public void alertDialogSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				InvoiceHeader.this);
		final EditText editText = new EditText(InvoiceHeader.this);
		final ListView listview = new ListView(InvoiceHeader.this);
		LinearLayout layout = new LinearLayout(InvoiceHeader.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle(getResources().getString(R.string.customer_name));
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
		arrayAdapterSupp = new CustomAlertAdapterSupp(InvoiceHeader.this, al);
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
						InvoiceHeader.this, searchResults);
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

	private class AsyncCallWSSearchCustCode extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			loadprogress();
			dialogStatus = "";
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) { // temp offline
						try {
							searchCstCdArr = offlineSearchInvoiceHeader(
									cuscode, sDate, eDate, status);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						Log.d("CheckOffline Alert -->", "False");
//						finish();
						if(mobileHaveOfflineMode.matches("1")){
							finish();


						}
					}

				} else {// Online
					if (status.matches("3")) {
						searchCstCdArr = SalesOrderWebService.SearchIOCustCode(
								cuscode, sDate, eDate, "", select_van,"",
								"fncGetInvoiceHeaderWithoutDOSign");

					} else {
						Log.d("searchCstCdArr","-->"+cuscode+"  "+sDate+"  "+eDate+" "+status +" "+loccode);

						Log.d("DBCheck","values"+SOTDatabase.getBatCursor().getCount());

						Log.d("LocationCodeCheck",loccode);
						searchCstCdArr = SalesOrderWebService.SearchIOCustCode(cuscode, sDate, eDate, status, select_van,
								loccode, "fncGetInvoiceHeader");
						Log.d("fncGetInvoiceHeaders", "->" + searchCstCdArr.size() +" "+loccode);
					}
				}

			} else if (onlineMode.matches("False")) { // permt Offline

				try {
					searchCstCdArr = offlineSearchInvoiceHeader(cuscode, sDate,
							eDate, status);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

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
				Toast.makeText(InvoiceHeader.this, "No matches found",
						Toast.LENGTH_SHORT).show();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);
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

		Log.d("statusValue",status);
		if (InvoiceHeader.this != null) {

			invoiceadapter = new HeaderAdapter(InvoiceHeader.this,
					R.layout.invoice_list_item_latest, status, searchCstCdArr);
			so_lv.setAdapter(invoiceadapter);

		}
		Log.d("SearchgetTotalamount()",""+SO.getTotalamount());

		if (SO.getTotalamount() > 0) {

			totaloutstanding.setText(twoDecimalPoint(SO.getTotalamount()));// ~ arun ~ change total outstanding as nettotal to balance amount

			if(status.matches("0")) {
				((TextView) findViewById(R.id.txt_totaloutstanding)).setText(getResources().getString(R.string.totaloutstanding));
			}else if(status.matches("1")) {
				((TextView) findViewById(R.id.txt_totaloutstanding)).setText(getResources().getString(R.string.total_paid));

			}else if(status.matches("2")) {
				((TextView) findViewById(R.id.txt_totaloutstanding)).setText(getResources().getString(R.string.total_amount));
				totaloutstanding.setText(twoDecimalPoint(SO.getTotalamount()));
			}else{
				((TextView) findViewById(R.id.txt_totaloutstanding)).setText(getResources().getString(R.string.totaloutstanding));
			}

		} else {
			totaloutstanding.setText("0.00");
		}
	}

	public void OverdueList() throws ParseException {

		if (InvoiceHeader.this != null) {

//			if(overdue_list.size()>0){
				invoiceadapter = new HeaderAdapter(InvoiceHeader.this,
						R.layout.invoice_list_item_latest, status, overdue_list);
				so_lv.setAdapter(invoiceadapter);
//			}else{
//				Toast.makeText(InvoiceHeader.this,"No data found",Toast.LENGTH_SHORT).show();
//			}

		}
		if (SO.getOverdueTotalAmount() > 0) {
			totaloutstanding.setText(twoDecimalPoint(SO.getOverdueTotalAmount())); // ~ arun ~ change total outstanding as nettotal to balance amount
		} else {
			totaloutstanding.setText("0.00");
		}
	}

	private class AsyncCallWSIOHeader extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			list.clear();
			dialogStatus = "";
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			// Offline
			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) {

						try {
							list = offlineInvoiceHeader();
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else {
						Log.d("CheckOffline Alert -->", "False");
//						finish();
						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					}

				} else { // Online

//					if (podpending != null && !podpending.isEmpty()) {

					Log.d("podpending",""+podpending);

						if (podpending.matches("1")) {
							list = onlineInvoiceHeader(
									"fncGetInvoiceHeaderWithoutDOSign", "");
						} else {
							Log.d("DbCheckValues","-->"+SOTDatabase.getBatCursor().getCount());
							list = onlineInvoiceHeader("fncGetInvoiceHeader",
									"0");
						}
//					} else {
//						list = onlineInvoiceHeader("fncGetInvoiceHeader", "0");
//					}

				}

			} else if (onlineMode.matches("False")) {
				Log.d("Customer", onlineMode);

				try {
					list = offlineInvoiceHeader();
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
			Log.d("list Size", list.size() + "");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			try {
				headerCustCode();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);

		}

	}

	public void headerCustCode() throws ParseException {
		if (!list.isEmpty()) {

			invoiceadapter = new HeaderAdapter(InvoiceHeader.this,
					R.layout.invoice_list_item_latest, notpaid, list);
			so_lv.setAdapter(invoiceadapter);

			Log.d("SO.getTotalamount()","sss "+ SO.getTotalamount());

			if (SO.getTotalamount() > 0) {
				totaloutstanding.setText(twoDecimalPoint(SO.getTotalamount()));
			} else {
				totaloutstanding.setText("0.00");
			}
		}
	}

	public void clearSetterGetter() {
		SalesOrderSetGet.setHeader_flag("InvoiceHeader");
		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");
		SalesOrderSetGet.setSaleorderdate("");
		SalesOrderSetGet.setCurrencycode("");
		SalesOrderSetGet.setCurrencyname("");
		SalesOrderSetGet.setCurrencyrate("");
		SalesOrderSetGet.setRemarks("");
		SalesOrderSetGet.setTaxCode("");
		SalesOrderSetGet.setTaxType("");
		SalesOrderSetGet.setTaxPerc("");
		ConvertToSetterGetter.setEdit_inv_no("");
		SOTDatabase.init(InvoiceHeader.this);
		SOTDatabase.deleteAllProduct();
		SOTDatabase.deleteBillDisc();
		SOTDatabase.deleteImage();
		SOTDatabase.deleteBarcode();
		SOTDatabase.deleteallbatch();

		// offline
		ArrayList<HashMap<String, String>> tempArr = new ArrayList<HashMap<String, String>>();
		tempArr.clear();
		OfflineSetterGetter.setStockUpdateArr(tempArr);
	}

	private class AsyncPrintCall extends
			AsyncTask<Void, Void, ArrayList<String>> {
		@Override
		protected void onPreExecute() {
			product.clear();
			productdet.clear();
			footerArr.clear();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {

			String decimalpts = ".00";
			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			String showcartonloose = SalesOrderSetGet
					.getCartonpriceflag();
			Log.d("showcartonloose","-->"+showcartonloose);
			/*
			 * hashValue.put("CompanyCode", cmpnyCode);
			 * hashValue.put("InvoiceNo", sosno);
			 *
			 * jsonString = SalesOrderWebService.getSODetail(hashValue,
			 * "fncGetInvoiceDetail"); jsonStr =
			 * SalesOrderWebService.getSODetail(hashValue,
			 * "fncGetInvoiceHeaderByInvoiceNo"); HashMap<String, String>
			 * custhash = new HashMap<String, String>();
			 *
			 * custhash.put("CompanyCode", cmpnyCode);
			 * custhash.put("CustomerCode", custCode);
			 *
			 * custjsonStr = SalesOrderWebService.getSODetail(custhash,
			 * "fncGetCustomer");
			 */

			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("InvoiceNo", sosno);

			Log.d("socustomercodeCheck",socustomercode);
			HashMap<String, String> custhash = new HashMap<String, String>();
			custhash.put("CompanyCode", cmpnyCode);
			custhash.put("CustomerCode", socustomercode);

			hashVl.put("CompanyCode", cmpnyCode);

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					// Offline
					// jsonString = offlineDatabase
					// .getInvoiceHeaderDetail(hashValue);

					if (showcartonloose.equalsIgnoreCase("1")) {
						jsonString = offlineDatabase
								.getInvoiceDetailPrint(hashValue);

					} else {
						jsonString = offlineDatabase
								.getInvoiceHeaderDetail(hashValue);
					}

					jsonStr = offlineDatabase.getInvoiceHeader(hashValue);
					custjsonStr = offlineDatabase.getCustomerDetail(custhash);
					// showcartonloose = "";

				} else { // online
					/********
					 * Show Default carton,loose,foc,exchange qty and price
					 * Based On General settings
					 *********/
					if (showcartonloose.equalsIgnoreCase("1")) {
						jsonString = SalesOrderWebService.getSODetail(
								hashValue, "fncGetInvoiceDetailWithCarton");
					} else {
						jsonString = SalesOrderWebService.getSODetail(
								hashValue, "fncGetInvoiceDetail");
					}
					jsonStr = SalesOrderWebService.getSODetail(hashValue,
							"fncGetInvoiceHeaderByInvoiceNo");
					custjsonStr = SalesOrderWebService.getSODetail(custhash,
							"fncGetCustomer");

					jsonSt = SalesOrderWebService.getSODetail(hashVl,
							"fncGetMobilePrintFooter");

					Log.d("jsonSt", jsonSt);
					try {
						jsonRespfooter = new JSONObject(jsonSt);
						jsonSecNodefooter = jsonRespfooter
								.optJSONArray("SODetails");

						/*********** Print Footer ************/
						int lengJsonArr1 = jsonSecNodefooter.length();
						for (int i = 0; i < lengJsonArr1; i++) {
							/****** Get Object for each JSON node. ***********/
							JSONObject jsonChildNode;
							ProductDetails productdetail = new ProductDetails();
							try {
								jsonChildNode = jsonSecNodefooter
										.getJSONObject(i);

								String ReceiptMessage = jsonChildNode
										.optString("ReceiptMessage").toString();
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
					} catch (Exception e) {

						e.printStackTrace();
					}

				}
			} else if (onlineMode.matches("False")) { // perman offline

				// jsonString =
				// offlineDatabase.getInvoiceHeaderDetail(hashValue);
				if (showcartonloose.equalsIgnoreCase("1")) {
					jsonString = offlineDatabase
							.getInvoiceDetailPrint(hashValue);

				} else {
					jsonString = offlineDatabase
							.getInvoiceHeaderDetail(hashValue);
				}
				jsonStr = offlineDatabase.getInvoiceHeader(hashValue);
				custjsonStr = offlineDatabase.getCustomerDetail(custhash);
				// showcartonloose = "";
			}

			Log.d("jsonString ", "" + jsonString);
			Log.d("jsonStr ", "" + jsonStr);
			Log.d("custjsonStr ", "" + custjsonStr);

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");

				jsonResp = new JSONObject(jsonStr);
				jsonSecNode = jsonResp.optJSONArray("SODetails");

				custjsonResp = new JSONObject(custjsonStr);
				custjsonMainNode = custjsonResp.optJSONArray("SODetails");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			/********
			 * Show Default carton,loose,foc,exchange qty and price Based On
			 * General settings
			 *********/
			/*********** Process each JSON Node ************/
			int lengthJsonArr = jsonMainNode.length();
			if (showcartonloose.equalsIgnoreCase("1")) {
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					ProductDetails productdetail = new ProductDetails();
					try {
						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);
						String transType = jsonChildNode.optString("TranType")
								.toString();
//						String transType ="FOC";
						int s = i + 1;
						productdetail.setSno(String.valueOf(s));
						String slNo = jsonChildNode.optString("slNo")
								.toString();
						String productCode = jsonChildNode.optString(
								"ProductCode").toString();
						productdetail.setItemcode(productCode);
						if (onlineMode.matches("True")) {
							if (checkOffline == true) {
								// Offline
							} else {
								// Online
								// Show batch number
								String showbatch = MobileSettingsSetterGetter
										.getShowBatchDetails();
								if (showbatch.matches("True")) {
									hashValue.put("slNo", slNo);
									hashValue.put("ProductCode", productCode);
									Log.d("hashValue ", "" + hashValue);
									batchjsonStr = SalesOrderWebService
											.getSODetail(hashValue,
													"fncGetInvoiceBatchDetail");
									Log.d("batchjsonStr ", "bat test "
											+ batchjsonStr);
									try {
										batch_jsonResp = new JSONObject(
												batchjsonStr);
										batch_jsonSecNode = batch_jsonResp
												.optJSONArray("SODetails");

									} catch (JSONException e) {

										e.printStackTrace();
									}
									int batch_lengthJsonArr = batch_jsonSecNode
											.length();
									for (int k = 0; k < batch_lengthJsonArr; k++) {
										/****** Get Object for each JSON node. ***********/
										ProductDetails prodBatch = new ProductDetails();
										try {
											JSONObject batch_jsonChildNode = batch_jsonSecNode
													.getJSONObject(k);
											prodBatch
													.setProduct_batchno(batch_jsonChildNode
															.optString(
																	"BatchNo")
															.toString());
											prodBatch
													.setBatch_productcode(batch_jsonChildNode
															.optString(
																	"ProductCode")
															.toString());

											product_batchArr.add(prodBatch);

											Log.d("BatchNo",
													batch_jsonChildNode
															.optString(
																	"BatchNo")
															.toString());

										} catch (JSONException e) {
											e.printStackTrace();
										}

									}

								}
							}
						}
						productdetail.setDescription(jsonChildNode.optString(
								"ProductName").toString());
						productdetail.setUOMCode(jsonChildNode.optString(
								"UOMCode").toString());

						String uomCode = jsonChildNode.optString("UOMCode")
								.toString();

						if (uomCode != null && !uomCode.isEmpty()) {

						} else {
							uomCode = "";
						}

						if (uomCode.matches("null")) {
							uomCode = "";
						}

						Log.d("uomCode", "u " + uomCode +transType);

						if (transType.equalsIgnoreCase("Ctn")) {
							String cQty = jsonChildNode.optString("CQty")
									.toString();
//							String cQty ="28.25";
							ProductDetails.setFocQtys("FOC");
							String cPrice = jsonChildNode.optString(
									"CartonPrice").toString();

							Log.d("cPrice", "-->" + cPrice);

							if (cQty != null && !cQty.isEmpty()
									&& cPrice != null && !cPrice.isEmpty()) {
								String numberD = cQty.substring ( cQty.indexOf ( "." ) );
								Log.d("cQty","-->"+cQty.split("\\.")[0]+" "+numberD);
								double value =Double.parseDouble(numberD);

								double tot_qty = Double.parseDouble(cQty);
								Log.d("DecimalValue","-->"+ twoDecimalPoint(tot_qty));
								if(value>0){
									productdetail.setQty(twoDecimalPoint(tot_qty));
								}else{
									productdetail.setQty(cQty.split("\\.")[0]);
								}
								productdetail.setPrice(twoDecimalPoint(Double
										.valueOf(cPrice)));
								double cqty = Double.valueOf(cQty);
								double cprice = Double.valueOf(cPrice);
								double total = cqty * cprice;
								String tot = twoDecimalPoint(total);
								productdetail.setTotal(tot);
							} else {
								productdetail.setQty("0");
								productdetail.setPrice("0.00");
								productdetail.setTotal("0.00");
							}

						} else if (transType.equalsIgnoreCase("Loose")) {

							String lQty = jsonChildNode.optString("LQty")
									.toString();
							String lPrice = jsonChildNode.optString("Price")
									.toString();
							ProductDetails.setFocQtys("FOC");
							Log.d("lPrice", "--->" + lPrice);

							if (lQty != null && !lQty.isEmpty()
									&& lPrice != null && !lPrice.isEmpty()) {
								String numberD = lQty.substring ( lQty.indexOf ( "." ) );
								double value =Double.parseDouble(numberD);
								double tot_qty = Double.parseDouble(lQty);
								Log.d("DecimalValue","-->"+ twoDecimalPoint(tot_qty));
								if(value>0){
									productdetail.setQty(twoDecimalPoint(tot_qty)+" "+uomCode);
								}else{
									productdetail.setQty(lQty.split("\\.")[0] + " "
											+ uomCode);
								}

								productdetail.setPrice(twoDecimalPoint(Double
										.valueOf(lPrice)));
								double lqty = Double.valueOf(lQty);
								double lprice = Double.valueOf(lPrice);
								double total = lqty * lprice;
								String tot = twoDecimalPoint(total);
								productdetail.setTotal(tot);
							} else {
								productdetail.setQty("0" + " " + uomCode);
								productdetail.setPrice("0.00");
								productdetail.setTotal("0.00");
							}

						} else if (transType.equalsIgnoreCase("FOC")) {
							String focQty = jsonChildNode.optString("FOCQty")
									.toString();
							ProductDetails.setFocQtys("FOC");
							productdetail.setFocqty(Double.parseDouble(focQty));
							Log.d("FOCQtyValue","-->"+jsonChildNode.optDouble("FOCQty"));
							if (focQty != null && !focQty.isEmpty()) {

								String numberD = focQty.substring ( focQty.indexOf ( "." ) );
								double value =Double.parseDouble(numberD);
								double tot_qty = Double.parseDouble(focQty);
								Log.d("DecimalValue","-->"+ twoDecimalPoint(tot_qty));
								if(value>0){
									productdetail.setQty( twoDecimalPoint(tot_qty)+"(FOC)");
								}else{
									productdetail.setQty(focQty.split("\\.")[0]+"(FOC)");
								}

							} else {
								productdetail.setQty("0" + " " + uomCode);
							}
							productdetail.setPrice("0.00");
							productdetail.setTotal("0.00");

						} else if (transType.equalsIgnoreCase("Exc")) {
							String excQty = jsonChildNode.optString(
									"ExchangeQty").toString();
							if (excQty != null && !excQty.isEmpty()) {
								String numberD = excQty.substring ( excQty.indexOf ( "." ) );
								double value =Double.parseDouble(numberD);
								double tot_qty = Double.parseDouble(excQty);
								Log.d("DecimalValue","-->"+ twoDecimalPoint(tot_qty));
								if(value>0){
									productdetail.setQty( twoDecimalPoint(tot_qty));
								}else{
									productdetail.setQty(excQty.split("\\.")[0]);
								}

							} else {
								productdetail.setQty("0" + " " + uomCode);
							}
							productdetail.setPrice("0.00");
							productdetail.setTotal("0.00");

						}
						String issueQty = jsonChildNode.optString("IssueQty")
								.toString();
						String returnQty = jsonChildNode.optString("ReturnQty")
								.toString();

						if (issueQty != null && !issueQty.isEmpty()) {

							productdetail.setIssueQty(issueQty.split("\\.")[0]);
						} else {
							productdetail.setIssueQty("0");
						}
						if (returnQty != null && !returnQty.isEmpty()) {

							productdetail
									.setReturnQty(returnQty.split("\\.")[0]);
						} else {
							productdetail.setReturnQty("0");
						}
						if (gnrlStngs.matches("C")) {
							productdetail.setSortproduct(jsonChildNode
									.optString("CategoryCode").toString());
						} else if (gnrlStngs.matches("S")) {
							productdetail.setSortproduct(jsonChildNode
									.optString("SubCategoryCode").toString());
						} else if (gnrlStngs.matches("N")) {
							productdetail.setSortproduct("");
						} else {
							productdetail.setSortproduct("");
						}
						productdetail.setTax(jsonChildNode.optString("Tax").toString());
						productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
						productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
						productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
						Log.d("setTaxType","-->"+jsonChildNode.optString("TaxType").toString());

						product.add(productdetail);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} else {
				/******** Print qty and price *********/
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode;
					ProductDetails productdetail = new ProductDetails();
					try {
						jsonChildNode = jsonMainNode.getJSONObject(i);
						int s = i + 1;
						productdetail.setSno(String.valueOf(s));

						String slNo = jsonChildNode.optString("slNo")
								.toString();
						String productCode = jsonChildNode.optString(
								"ProductCode").toString();
						productdetail.setItemcode(productCode);

						productdetail.setUOMCode(jsonChildNode.optString(
								"UOMCode").toString());

						if (onlineMode.matches("True")) {
							if (checkOffline == true) {
								// Offline
							} else {
								// Online
								// Show batch number

								String showbatch = MobileSettingsSetterGetter
										.getShowBatchDetails();

								if (showbatch.matches("True")) {

									hashValue.put("slNo", slNo);
									hashValue.put("ProductCode", productCode);
									Log.d("hashValue ", "" + hashValue);
									batchjsonStr = SalesOrderWebService
											.getSODetail(hashValue,
													"fncGetInvoiceBatchDetail");
									Log.d("batchjsonStr ", "bat test "
											+ batchjsonStr);
									try {
										batch_jsonResp = new JSONObject(
												batchjsonStr);
										batch_jsonSecNode = batch_jsonResp
												.optJSONArray("SODetails");

									} catch (JSONException e) {

										e.printStackTrace();
									}

									int batch_lengthJsonArr = batch_jsonSecNode
											.length();
									for (int k = 0; k < batch_lengthJsonArr; k++) {
										/****** Get Object for each JSON node. ***********/
										ProductDetails prodBatch = new ProductDetails();
										try {
											JSONObject batch_jsonChildNode = batch_jsonSecNode
													.getJSONObject(k);
											prodBatch
													.setProduct_batchno(batch_jsonChildNode
															.optString(
																	"BatchNo")
															.toString());
											prodBatch
													.setBatch_productcode(batch_jsonChildNode
															.optString(
																	"ProductCode")
															.toString());

											product_batchArr.add(prodBatch);

											Log.d("BatchNo",
													batch_jsonChildNode
															.optString(
																	"BatchNo")
															.toString());

										} catch (JSONException e) {
											e.printStackTrace();
										}

									}

								}
							}
						}
						productdetail.setDescription(jsonChildNode.optString(
								"ProductName").toString());

						String invoiceqty = jsonChildNode.optString("Qty")
								.toString();
						Log.d("invoiceqty","-->"+invoiceqty);
						if (invoiceqty.contains(".000")) {
							StringTokenizer tokens = new StringTokenizer(
									invoiceqty, ".");
							String qty = tokens.nextToken();
							productdetail.setQty(qty);
						} else {
							productdetail.setQty(invoiceqty);
						}

						String pricevalue = jsonChildNode.optString("Price")
								.toString();
						Log.d("pricevalue", "--->" + pricevalue);

						String totalvalve = jsonChildNode.optString("Total")
								.toString();

						String issueQty = jsonChildNode.optString("IssueQty")
								.toString();
						String returnQty = jsonChildNode.optString("ReturnQty")
								.toString();

						if (issueQty != null && !issueQty.isEmpty()) {

							productdetail.setIssueQty(issueQty.split("\\.")[0]);
						} else {
							productdetail.setIssueQty("0");
						}
						if (returnQty != null && !returnQty.isEmpty()) {

							productdetail
									.setReturnQty(returnQty.split("\\.")[0]);
						} else {
							productdetail.setReturnQty("0");
						}
						Log.d("FOCQtyValues1","-->"+jsonChildNode.optDouble("FOCQty"));
						if(jsonChildNode.optDouble("FOCQty")>0){
							ProductDetails.setFocQtys("FOC");
							productdetail.setFocqty(jsonChildNode
									.optDouble("FOCQty"));
						}else{
							ProductDetails.setFocQtys("FOC");
						}

						productdetail.setExchangeqty(jsonChildNode
								.optDouble("ExchangeQty"));
						if (pricevalue.contains(".")) {
							productdetail.setPrice(pricevalue);
						} else {
							productdetail.setPrice(pricevalue + decimalpts);
						}
						if (totalvalve.contains(".")) {

							productdetail.setTotal(totalvalve);
						} else {

							productdetail.setTotal(totalvalve + decimalpts);
						}
						if (gnrlStngs.matches("C")) {
							productdetail.setSortproduct(jsonChildNode
									.optString("CategoryCode").toString());
						} else if (gnrlStngs.matches("S")) {
							productdetail.setSortproduct(jsonChildNode
									.optString("SubCategoryCode").toString());
						} else if (gnrlStngs.matches("N")) {
							productdetail.setSortproduct("");
						} else {
							productdetail.setSortproduct("");
						}
						productdetail.setTax(jsonChildNode.optString("Tax").toString());
						productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
						productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
						productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
						Log.d("setTaxType","-->"+jsonChildNode.optString("TaxType").toString());
						Log.d("getTaxType","-->"+productdetail.getTaxType());
						product.add(productdetail);

					} catch (JSONException e) {

						e.printStackTrace();
					}
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

					String paidamount = jsonChildNode.optString("PaidAmount")
							.toString();

					String BalanceAmount = jsonChildNode.optString("BalanceAmount")
							.toString();

					productdetail.setCustomeraddress1(jsonChildNode.optString(""));
					productdetail.setCustomeraddress2(jsonChildNode.optString("CustomerAddress1"));
					productdetail.setCustomeraddress3(jsonChildNode.optString("CustomerAddress3"));

					productdetail.setPaidamount(paidamount);

					productdetail.setBalanceAmount(BalanceAmount);

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
					productdetail.setCreateDate(jsonChildNode.optString(
							"CreateDate").toString());

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
					String Address1 = jsonChildNode.optString("Address1")
							.toString();
					String Address2 = jsonChildNode.optString("Address2")
							.toString();
					String Address3 = jsonChildNode.optString("Address3")
							.toString();
					String PhoneNo = jsonChildNode.optString("PhoneNo")
							.toString();
					String HandphoneNo = jsonChildNode.optString("HandphoneNo")
							.toString();
					String Email = jsonChildNode.optString("Email").toString();
					String TermName = jsonChildNode.optString("TermName")
							.toString();
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
					/*
					 * Log.d("mobile settings Customer code",
					 * CustomerSetterGetter.getCustomerCode());
					 */
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

			hashVl.clear();
			hashValue.clear();
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {

			if (printid == R.id.printer) {

				try {
					Log.d("isPrint", "isPrint");
					printid = 0;
					sortCatSubCat();
					if (isInvoicePrint) {
						Log.d("productdet", "" + productdet);
						print();
					} else {
						invoiceOnDeliveryOrderPrint();
					}

				} catch (IOException e) {

				}
			} else {
				Log.d("receiptprintpreview", "receiptprintpreview");
				Log.d("product", "" + product);

				// for(ProductDetails prod: product){
				// Log.d("price",""+prod.getPrice());
				// }

				Log.d("productdet", "" + productdet);
				helper.dismissProgressDialog();
				Intent i = new Intent(InvoiceHeader.this,
						InvoicePrintPreview.class);
				i.putExtra("invNo", sosno);
				i.putExtra("invDate", sodate);
				i.putExtra("customerCode", socustomercode);
				i.putExtra("Invoicetype", "Invoice");
				i.putExtra("customerName", socustomername);
				i.putExtra("sort", sortproduct);
				i.putExtra("gnrlStngs", gnrlStngs);
				i.putExtra("tranType","");
				i.putExtra("CustomerAddress", soaddress);
				i.putExtra("dono",soDONO);
				PreviewPojo.setProducts(product);
				PreviewPojo.setProductsDetails(productdet);
				startActivity(i);

			}
		}

	}

	public void sortCatSubCat() {
		for (int i = 0; i < sortproduct.size(); i++) {
			String catagory = sortproduct.get(i).toString();
			for (ProductDetails products : product) {

				if (catagory.matches(products.getSortproduct())) {

					sort.add(catagory);
				}
			}
		}
		hs.addAll(sort);
		printsortHeader.clear();
		printsortHeader.addAll(hs);
	}

	private void printCallDialog() {
		dialogStatus = "";
		dialogStatus = checkInternetStatus();
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setIcon(R.mipmap.ic_menu_print);
		alertDialog.setTitle("Print");
		alertDialog.setMessage("Do you want to Print ?");
		LayoutInflater adbInflater = LayoutInflater.from(this);
		final View eulaLayout = adbInflater.inflate(R.layout.print_checkbox,
				null);
		mNoOfPrintCopiesLayout = (LinearLayout) eulaLayout
				.findViewById(R.id.noofcopieslblLayout);
		mSignatureLayout = (LinearLayout) eulaLayout
				.findViewById(R.id.signatureLayout);
		mCameraLayout = (LinearLayout) eulaLayout
				.findViewById(R.id.cameraLayout);

		stnumber = (TextView) eulaLayout.findViewById(R.id.stnumber);
		stupButton = (Button) eulaLayout.findViewById(R.id.stupBtn);
		mChangeSignBtn = (ImageView) eulaLayout.findViewById(R.id.changeSign);
		mSignature = (ImageView) eulaLayout.findViewById(R.id.signature);

		sm_camera_iv = (ImageView) eulaLayout.findViewById(R.id.sm_camera_iv);
		prodphoto = (ImageView) eulaLayout.findViewById(R.id.prod_photo);
		mInvoiceCheckBox = (CheckBox) eulaLayout.findViewById(R.id.checkbox);
		mDOCheckBox = (CheckBox) eulaLayout.findViewById(R.id.checkbox_do);

		mCameraLayout.setVisibility(View.VISIBLE);
		mInvoiceCheckBox.setVisibility(View.VISIBLE);
		mDOCheckBox.setVisibility(View.VISIBLE);

		mDOCheckBox.setText("Print DO");
		int modeid = FWMSSettingsDatabase.getModeId();
		if (modeid == 1) {
//			FWMSSettingsDatabase.updateMode(1);
			mDOCheckBox.setChecked(true);
		} else {
//			FWMSSettingsDatabase.updateMode(0);
			mDOCheckBox.setChecked(false);
		}
		mInvoiceCheckBox.setText("Print Invoice");

		// mDeliveryCheckBox.setChecked(true);
		mSignatureLayout.setVisibility(View.VISIBLE);
		mNoOfPrintCopiesLayout.setVisibility(View.VISIBLE);

		loadSignatureImage();

		mChangeSignBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(InvoiceHeader.this,
						CaptureSignature.class);
				startActivityForResult(i, SIGNATURE_ACTIVITY);
			}
		});

		sm_camera_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CameraAction(PICK_FROM_CAMERA);
			}
		});

		// mDeliveryCheckBox.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		//
		//
		// }
		// });

		if (!PreviewPojo.getNofcopies().matches("")) {
			stnumber.setText("" + PreviewPojo.getNofcopies());
			stwght = Integer.valueOf(PreviewPojo.getNofcopies());
		}

		stupButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stdownButton
						.setBackgroundResource(R.mipmap.numpicker_down_normal);
				stupButton
						.setBackgroundResource(R.mipmap.numpicker_up_pressed);
				if (stwght < 3) {
					stnumber.setText("" + ++stwght);
				}

			}
		});

		stdownButton = (Button) eulaLayout.findViewById(R.id.stdownBtn);
		stdownButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stdownButton
						.setBackgroundResource(R.mipmap.numpicker_down_pressed);
				stupButton
						.setBackgroundResource(R.mipmap.numpicker_up_normal);
				if (stwght > 1) {
					stnumber.setText(--stwght + "");
				}
			}
		});

		alertDialog.setView(eulaLayout);

		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int indexSelected) {
						printid = R.id.printer;
						cursor = FWMSSettingsDatabase.getPrinter();
						if(mInvoiceCheckBox.isChecked() && !mDOCheckBox.isChecked()){
							printStr = "InvoiceHeader";
						}else if(!mInvoiceCheckBox.isChecked() && mDOCheckBox.isChecked()){
							printStr = "DeliveryOnInvoice";
						}else if(mInvoiceCheckBox.isChecked() && mDOCheckBox.isChecked()){
							printStr = "InvoiceHeaderDeliveryOnInvoice";
						}
						if (mDOCheckBox.isChecked()) {

							if (cursor.getCount() != 0) {
								isInvoicePrint = false;


								if (sosno != null && !sosno.isEmpty()) {
									Log.d("Lat ", mLatitude + " Long "
											+ mLongitude);
									//String photo_img = SOTDatabase
											//.getProductImage();
									String signature_img = SOTDatabase
											.getSignatureImage();
									mCurrentgetPath = Product.getHeaderpath();
									//Log.d("pathfind",mCurrentgetPath);
									String photo_img = ImagePathToBase64Str(mCurrentgetPath);

									if (onlineMode.matches("True")) {
										if (checkOffline == true) {
											if (dialogStatus.matches("true")) { // temp
																				// offline
												HashMap<String, String> hm = new HashMap<String, String>();
												hm.put("ImageNo", sosno);
												hm.put("ImageType", "POD");
												hm.put("SignatureImage",
														signature_img);
												hm.put("ProductImage",
														photo_img);

												String imageno = offlineDatabase
														.getImageNo(sosno);
												Log.d("imageno", "..."
														+ imageno);
												if (imageno != null
														&& !imageno.isEmpty()) {
													offlineDatabase
															.updateImage(hm);
												} else {
													offlineDatabase
															.storeImage(hm);
												}

												offlineDatabase
														.updateSignStaus(sosno);

											} else {
												Log.d("CheckOffline Alert -->",
														"False");
//												finish();
												if(mobileHaveOfflineMode.matches("1")){
													finish();
												}
											}

										} else {// Online
											String signatureResult = SOTSummaryWebService
													.saveSignatureImage(
															sosno,
															"" + mLatitude,
															"" + mLongitude,
															signature_img,
															photo_img,
															"fncSaveInvoiceImages",
															"POD", "", "");
											Log.d("signatureResult-->", ""
													+ signatureResult);
										}
									} else if (onlineMode.matches("False")) { // permt
																				// Offline
										HashMap<String, String> hm = new HashMap<String, String>();
										hm.put("ImageNo", sosno);
										hm.put("ImageType", "POD");
										hm.put("SignatureImage", signature_img);
										hm.put("ProductImage", photo_img);

										String imageno = offlineDatabase
												.getImageNo(sosno);
										Log.d("imageno", "..." + imageno);
										if (imageno != null
												&& !imageno.isEmpty()) {
											offlineDatabase.updateImage(hm);
										} else {
											offlineDatabase.storeImage(hm);
										}
										offlineDatabase.updateSignStaus(sosno);
									}

									// refresh
									// loadprogress();
									AsyncCallWSSearchCustCode task = new AsyncCallWSSearchCustCode();
									task.execute();
								}
								// Print method
								loadPrintData(R.string.generating_do);
							} else {
								Toast.makeText(InvoiceHeader.this,
										"Please Configure the printer",
										Toast.LENGTH_SHORT).show();
							}

						} else {
							if (sosno != null && !sosno.isEmpty()) {
								Log.d("Lat ", mLatitude + " Long " + mLongitude);
								//String photo_img = SOTDatabase
										//.getProductImage();
								String signature_img = SOTDatabase
										.getSignatureImage();

								mCurrentgetPath = Product.getHeaderpath();
								//Log.d("pathfind",mCurrentgetPath);
								String photo_img = ImagePathToBase64Str(mCurrentgetPath);

								if (onlineMode.matches("True")) {
									if (checkOffline == true) {
										if (dialogStatus.matches("true")) { // temp
																			// offline
											HashMap<String, String> hm = new HashMap<String, String>();
											hm.put("ImageNo", sosno);
											hm.put("ImageType", "POD");
											hm.put("SignatureImage",
													signature_img);
											hm.put("ProductImage", photo_img);

											String imageno = offlineDatabase
													.getImageNo(sosno);
											Log.d("imageno", "..." + imageno);
											if (imageno != null
													&& !imageno.isEmpty()) {
												offlineDatabase.updateImage(hm);
											} else {
												offlineDatabase.storeImage(hm);
											}
											offlineDatabase
													.updateSignStaus(sosno);
										} else {
											Log.d("CheckOffline Alert -->",
													"False");
//											finish();
											if(mobileHaveOfflineMode.matches("1")){
												finish();
											}
										}

									} else {// Online

										Log.d("savePhotoImage", "->"
												+ photo_img);

										String signatureResult = SOTSummaryWebService
												.saveSignatureImage(sosno, ""
														+ mLatitude, ""
														+ mLongitude,
														signature_img,
														photo_img,
														"fncSaveInvoiceImages",
														"POD", "", "");
										Log.d("signatureResult-->", ""
												+ signatureResult);
									}
								} else if (onlineMode.matches("False")) { // permt
																			// Offline
									HashMap<String, String> hm = new HashMap<String, String>();
									hm.put("ImageNo", sosno);
									hm.put("ImageType", "POD");
									hm.put("SignatureImage", signature_img);
									hm.put("ProductImage", photo_img);

									String imageno = offlineDatabase
											.getImageNo(sosno);
									Log.d("imageno", "..." + imageno);
									if (imageno != null && !imageno.isEmpty()) {
										offlineDatabase.updateImage(hm);
									} else {
										offlineDatabase.storeImage(hm);
									}
									offlineDatabase.updateSignStaus(sosno);
								}
								// refresh
								// loadprogress();
								AsyncCallWSSearchCustCode task = new AsyncCallWSSearchCustCode();
								task.execute();

							}
						}

					}
				});
		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		alertDialog.show();

	}

	public String ImagePathToBase64Str(String path) {

		String base64Image ="";
		try {
			// Decode deal_image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, o);
			// The new size we want to scale to
			final int REQUIRED_SIZE = 128;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;
			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			base64Image = BitMapToString(BitmapFactory.decodeFile(path, o2));
			return base64Image;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	public String BitMapToString(Bitmap bitmap){
		ByteArrayOutputStream baos=new  ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
		byte [] b=baos.toByteArray();
		String temp=Base64.encodeToString(b, Base64.DEFAULT);
		return temp;
	}
	private void loadSignatureImage() {
		try {
			String photo_img = SOTDatabase.getProductImage();
			String signature_img = SOTDatabase.getSignatureImage();

			if (photo_img != null && !photo_img.isEmpty()) {
				byte[] encodePhotoByte;

				byte[] encodePhotoByte1 = Base64.decode(photo_img,
						Base64.DEFAULT);

				String s;
				try {
					s = new String(encodePhotoByte1, "UTF-8");
					encodePhotoByte = Base64.decode(s, Base64.DEFAULT);
				} catch (Exception e) {
					encodePhotoByte = encodePhotoByte1;
				}

				Bitmap photo = BitmapFactory.decodeByteArray(encodePhotoByte,
						0, encodePhotoByte.length);
				photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
				prodphoto.setImageBitmap(photo);
			}

			if (signature_img != null && !signature_img.isEmpty()) {
				byte[] encodeByte;

				byte[] encodeByte1 = Base64.decode(signature_img,
						Base64.DEFAULT);

				String s;
				try {
					s = new String(encodeByte1, "UTF-8");
					encodeByte = Base64.decode(s, Base64.DEFAULT);
				} catch (Exception e) {
					encodeByte = encodeByte1;
				}

				Bitmap photo = BitmapFactory.decodeByteArray(encodeByte, 0,
						encodeByte.length);
				photo = Bitmap.createScaledBitmap(photo, 240, 80, true);
				mSignature.setImageBitmap(photo);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void invoiceOnDeliveryOrderPrint() throws IOException {

		final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		final String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
		try {

			int nofcopies = Integer.parseInt(stnumber.getText().toString());
		/*	if (mInvoiceCheckBox.isChecked()) {
				if (printertype.matches("Zebra iMZ320")) {
					helper.dismissProgressDialog();
					Printer printer = new Printer(InvoiceHeader.this, macaddress);
					printer.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompleted() {
							// TODO Auto-generated method stub

							//if (printertype.matches("Zebra iMZ320")) {
							Printer reprinter = new Printer(InvoiceHeader.this,
									macaddress);
							try {

								reprinter.printInvoice(sosno, sodate,
										socustomercode, socustomername, product,
										productdet, printsortHeader, gnrlStngs, 1,
										product_batchArr, footerArr);

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						//	}
					});
				}else if (printertype.matches("Zebra iMZ320 4 Inch")) {

					PrinterZPL printer = new PrinterZPL(InvoiceHeader.this, macaddress);
					printer.printDeliveryOnInvoice(sosno, sodate, socustomercode,
							socustomername, product, productdet, printsortHeader,
							gnrlStngs, nofcopies, product_batchArr, footerArr);
					printer.setOnCompletionListener(new PrinterZPL.OnCompletionListener() {
						@Override
						public void onCompleted() {
							PrinterZPL reprinter = new PrinterZPL(InvoiceHeader.this,
									macaddress);
							try {
								helper.dismissProgressDialog();
								reprinter.printInvoice(sosno, sodate,
										socustomercode, socustomername, product,
										productdet, printsortHeader, gnrlStngs, 1,
										product_batchArr, footerArr);

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});

				}
			}*/

				if (printertype.matches("Zebra iMZ320")) {
//					Printer reprinter = new Printer(InvoiceHeader.this,
//							macaddress);
					helper.dismissProgressDialog();
					Printer printer = new Printer(InvoiceHeader.this,macaddress);
					if (mInvoiceCheckBox.isChecked()) {
					printer.setOnCompletionListener(new OnCompletionListener() {
						@Override
						public void onCompleted() {

								Printer reprinter = new Printer(InvoiceHeader.this,
										macaddress);
								try {
									helper.dismissProgressDialog();
									reprinter.printInvoice(sosno, sodate,
											socustomercode, socustomername, product,
											productdet, printsortHeader, gnrlStngs, 1,
											product_batchArr, footerArr,"","","",soDONO);

								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

					});
					}
					printer.printDeliveryOnInvoice(sosno, sodate, socustomercode,
							socustomername, product, productdet, printsortHeader,
							gnrlStngs, nofcopies, product_batchArr, footerArr);

				}else if (printertype.matches("Zebra iMZ320 4 Inch")) {
//					Printer reprinter = new Printer(InvoiceHeader.this,
//							macaddress);
				helper.dismissProgressDialog();
				PrinterZPL printer = new PrinterZPL(InvoiceHeader.this,
						macaddress);
				if (mInvoiceCheckBox.isChecked()) {
				printer.setOnCompletionListener(new PrinterZPL.OnCompletionListener() {
					@Override
					public void onCompleted() {

							PrinterZPL reprinter = new PrinterZPL(InvoiceHeader.this,
									macaddress);
							try {
								helper.dismissProgressDialog();
								reprinter.printInvoice(sosno, sodate,
										socustomercode, socustomername, product,
										productdet, printsortHeader, gnrlStngs, 1,
										product_batchArr, footerArr);

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

					}
				});
					}
				printer.printDeliveryOnInvoice(sosno, sodate, socustomercode,
						socustomername, product, productdet, printsortHeader,
						gnrlStngs, nofcopies, product_batchArr, footerArr);

			}
				else if (printertype.matches("4 Inch Bluetooth")) {
					//printStr = "DeliveryOnInvoice";
					helper.dismissProgressDialog();
//					helper.updateProgressDialog(InvoiceHeader.this.getString(R.string.creating_file_for_printing));
					if (BluetoothAdapter.checkBluetoothAddress(macaddress)) {
						BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
						// Attempt to connect to the device
						GlobalData.mService.setHandler(mHandler);
						GlobalData.mService.connect(device, true);
					}
//				helper.dismissProgressDialog();
				}

		}catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}
	}


	/*private void invoiceOnDeliveryOrderPrint() throws IOException {
		helper.dismissProgressDialog();
		final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		try {
			Printer printer = new Printer(InvoiceHeader.this, macaddress);
			int nofcopies = Integer.parseInt(stnumber.getText().toString());
			if (mInvoiceCheckBox.isChecked()) {

				printer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompleted() {
						// TODO Auto-generated method stub
						Printer reprinter = new Printer(InvoiceHeader.this,
								macaddress);
						try {
							reprinter.printInvoice(sosno, sodate,
									socustomercode, socustomername, product,
									productdet, printsortHeader, gnrlStngs, 1,
									product_batchArr, footerArr);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
			printer.printDeliveryOnInvoice(sosno, sodate, socustomercode,
					socustomername, product, productdet, printsortHeader,
					gnrlStngs, nofcopies, product_batchArr, footerArr);
		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}
	}*/

	private void print() throws IOException {

		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		try {
			String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
			SO so = invoiceadapter
					.getItem(invoiceadapter.getSelectedPosition());
			sosno = so.getSno().toString();
			sodate = so.getDate().toString();
			socustomercode = so.getCustomerCode().toString();
			socustomername = so.getCustomerName().toString();
			soamount = so.getNettotal().toString();
			soDONO = so.getDono().toString();

			if(printertype.matches("Zebra iMZ320")){
				helper.dismissProgressDialog();
				Printer printer = new Printer(InvoiceHeader.this, macaddress);
				printer.printInvoice(sosno, sodate, socustomercode, socustomername,
						product, productdet, printsortHeader, gnrlStngs, 1,
						product_batchArr, footerArr,"","","",soDONO);
			}
			else if(printertype.matches("4 Inch Bluetooth")){
				/*helper.showProgressDialog(InvoiceHeader.this.getString(R.string.print),
						InvoiceHeader.this.getString(R.string.creating_file_for_printing));*/
//				helper.updateProgressDialog(InvoiceHeader.this.getString(R.string.creating_file_for_printing));
				if (BluetoothAdapter.checkBluetoothAddress(macaddress))
				{
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device,true);
				}
				helper.dismissProgressDialog();
				//helper.dismissProgressDialog();
			}
			else if(printertype.matches("3 Inch Bluetooth Generic")) {
				helper.dismissProgressDialog();
				try {
					final CubePrint print = new CubePrint(InvoiceHeader.this, macaddress);
					print.initGenericPrinter();
					print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
						@Override
						public void initCompleted() {
							print.printInvoice(sosno, sodate, socustomercode, socustomername,
									product, productdet, printsortHeader, gnrlStngs, 1,
									product_batchArr, footerArr,"");

							print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
								@Override
								public void onCompleted() {
									helper.showLongToast(R.string.printed_successfully);
								}
							});
						}
					});

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(printertype.matches("Zebra iMZ320 4 Inch")){
				helper.dismissProgressDialog();
				PrinterZPL printer = new PrinterZPL(InvoiceHeader.this, macaddress);
				printer.printInvoice(sosno, sodate, socustomercode, socustomername,
						product, productdet, printsortHeader, gnrlStngs, 1,
						product_batchArr, footerArr);
			}
			/*
			 * for (HashMap<String, String> hashMap : al) { for (String key :
			 * hashMap.keySet()) { if (key.equals(socustomercode)) {
			 * System.out.println(hashMap.get(key)); socustomername =
			 * hashMap.get(key); } }
			 *
			 * }
			 */


		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}
	}

	private class AsyncGeneralSettings extends AsyncTask<Void, Void, Void> {
		String dialogStatus;

		@Override
		protected void onPreExecute() {
			dialogStatus = checkInternetStatus();
			Log.d("dialogcheck2",dialogStatus);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) {
						Log.d("CheckOfflineAlert -->", "True");

						String result = offlineDatabase
								.getGeneralSettingsValue();
						jsonString = " { JsonArray: " + result + "}";
					} else {
						Log.d("CheckOffline Alert -->", "False");
						finish();
						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					}

				} else {
					Log.d("checkOffline Status -->", "False");
					jsonString = WebServiceClass
							.URLService("fncGetGeneralSettings");

				}

			} else if (onlineMode.matches("False")) {

				String result = offlineDatabase.getGeneralSettingsValue();
				jsonString = " { JsonArray: " + result + "}";

			}

			try {

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

		@Override
		protected void onPreExecute() {
			sortproduct.clear();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if (gnrlStngs.matches("C")) {
				// jsonString = WebServiceClass.URLService("fncGetCategory");
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						// Offline
						String result = offlineDatabase.getCategory();
						jsonString = " { JsonArray: " + result + "}";

					} else { // online
						jsonString = WebServiceClass
								.URLService("fncGetCategory");
					}

				} else if (onlineMode.matches("False")) { // perman offline
					// Offline

					String result = offlineDatabase.getCategory();
					jsonString = " { JsonArray: " + result + "}";

				}

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
				// jsonStr = WebServiceClass.URLService("fncGetSubCategory");
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {

						// temp offline
						String result = offlineDatabase.getSubCategory();
						jsonStr = " { JsonArray: " + result + "}";

					} else { // online
						jsonStr = WebServiceClass
								.URLService("fncGetSubCategory");
					}

				} else if (onlineMode.matches("False")) { // perman offline
					String result = offlineDatabase.getSubCategory();
					jsonStr = " { JsonArray: " + result + "}";
				}
				Log.d("jsonStr ", jsonStr);

				try {

					jsonResponse = new JSONObject(jsonStr);
					jsonSecNode = jsonResponse.optJSONArray("JsonArray");

					int lengJsonArr = jsonSecNode.length();
					for (int i = 0; i < lengJsonArr; i++) {

						JSONObject jsonChildNode;

						try {
							jsonChildNode = jsonSecNode.getJSONObject(i);

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
			// test();
		}

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

//	public void CameraAction() {
//		/*
//		 * Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		 *
//		 * intent.putExtra("crop", "true"); intent.putExtra("aspectX", 0);
//		 * intent.putExtra("aspectY", 0); intent.putExtra("outputX", 200);
//		 * intent.putExtra("outputY", 150); try { intent.putExtra("return-data",
//		 * true); startActivityForResult(intent, PICK_FROM_CAMERA); } catch
//		 * (ActivityNotFoundException e) { }
//		 */
//
//		try {
//			Intent takePictureIntent = new Intent(
//					MediaStore.ACTION_IMAGE_CAPTURE);
//			if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//				startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
//			}
//
//		} catch (ActivityNotFoundException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	private void CameraAction(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			f = setUpPhotoFile();

			mCurrentPhotoPath = f.getAbsolutePath();


			Log.d("mCurrentPhotoPath", "dispatchTakePictureIntent--->"+mCurrentPhotoPath);
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}
		startActivityForResult(takePictureIntent, actionCode);
	}

	private File setUpPhotoFile() throws IOException {

		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private File getAlbumDir() {
		File storageDir = null;
		String folder_main = "SFA";
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir= new File(Environment.getExternalStorageDirectory()+ "/" + folder_main, "Image");
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (data != null) {
			switch (requestCode) {

			case SIGNATURE_ACTIVITY:
				Log.d("requestCode",""+requestCode);
				if (resultCode == RESULT_OK) {
					// Bundle extras = data.getExtras();
					byte[] bytes = data.getByteArrayExtra("status");
					if (bytes != null) {
						// Bitmap photo = extras.getParcelable("status");

						Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
								bytes.length);
						bitmap = Bitmap.createScaledBitmap(bitmap, 240, 80,
								true);

						mSignature.setImageBitmap(bitmap);

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] bitMapData = stream.toByteArray();
						String signature_image = Base64.encodeToString(
								bitMapData, Base64.DEFAULT);
						SOTDatabase.init(InvoiceHeader.this);
						Cursor ImgCursor = SOTDatabase.getImageCursor();
						if (ImgCursor.getCount() > 0) {
							String camera_image = SOTDatabase.getProductImage();
							SOTDatabase.updateImage(1, signature_image,
									camera_image);
						} else {
							SOTDatabase.storeImage(1, signature_image, "");
						}

						Log.d("Signature Image", "Sig" + signature_image);
					}
				}
				break;

			case PICK_FROM_CAMERA:
				Log.d("requestCode",""+requestCode);
				if (requestCode == PICK_FROM_CAMERA) {
//					Bundle extras = data.getExtras();
//					if (extras != null) {
						getRightAngleImage(mCurrentPhotoPath);

						handleCameraPhoto(PICK_FROM_CAMERA);
//						Bitmap photo = extras.getParcelable("data");
//						photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
//						prodphoto.setImageBitmap(photo);
//
//						ByteArrayOutputStream stream = new ByteArrayOutputStream();
//						photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
//						byte[] bitMapData = stream.toByteArray();
//						String camera_image = Base64.encodeToString(bitMapData,
//								Base64.DEFAULT);
//						SOTDatabase.init(InvoiceHeader.this);
//
//						Cursor ImgCursor = SOTDatabase.getImageCursor();
//						if (ImgCursor.getCount() > 0) {
//							String signature_image = SOTDatabase
//									.getSignatureImage();
//							SOTDatabase.updateImage(1, signature_image,
//									camera_image);
//						} else {
//							SOTDatabase.storeImage(1, "", camera_image);
//						}
//
//						Log.d("Camera Image", "cam" + camera_image);
					//}
				}
				break;
			}
//		}

		barcoderesult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if(barcoderesult != null) {
			if(barcoderesult.getContents() == null) {
				Log.d("MainActivity", "Cancelled scan");
				Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
			} else {
				Log.e("InvoiceScanned", ""+barcoderesult.getContents());
				Barcodesearch barcodesearch = new Barcodesearch();
				barcodesearch.execute();
			}
		} else {
			// This is important, otherwise the result will not be passed to the fragment
			super.onActivityResult(requestCode, resultCode, data);
		}

	}

	private class Barcodesearch extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			loadprogress();
			dialogStatus = "";
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) { // temp offline
						try {
							searchCstCdArr = offlineSearchInvoiceHeader(
									cuscode, sDate, eDate, status);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						Log.d("CheckOffline Alert -->", "False");
//						finish();
						if(mobileHaveOfflineMode.matches("1")){
							finish();


						}
					}

				} else {// Online
					if (status.matches("3")) {
						searchCstCdArr = SalesOrderWebService.SearchIOCustCode(
								cuscode, sDate, eDate, "", select_van,"",
								"fncGetInvoiceHeaderWithoutDOSign");

					} else {
						Log.d("searchCstCdArr","-->"+cuscode+"  "+sDate+"  "+eDate+" "+status +" "+loccode);

						Log.d("DBCheck","values"+SOTDatabase.getBatCursor().getCount());

						Log.d("LocationCodeCheck",loccode);
						searchCstCdArr = SalesOrderWebService.Barcodesearch(barcoderesult.getContents(), cuscode, sDate, eDate, status, select_van,
								loccode, "fncGetInvoiceHeader");
						Log.d("fncGetInvoiceHeaders", "->" + searchCstCdArr.size() +" "+loccode);
					}
				}

			} else if (onlineMode.matches("False")) { // permt Offline

				try {
					searchCstCdArr = offlineSearchInvoiceHeader(cuscode, sDate,
							eDate, status);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

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
				Toast.makeText(InvoiceHeader.this, "No matches found",
						Toast.LENGTH_SHORT).show();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);
		}
	}

	private void handleCameraPhoto(int actionCode) {
		Log.d("mCurrentPhotoPath", "mCurrentPhotoPath-->"+mCurrentPhotoPath);
		if (mCurrentPhotoPath != null) {

			switch (actionCode) {
				case PICK_FROM_CAMERA: {
					Product.setHeaderpath(mCurrentPhotoPath);
					decodeImagePathFile(mCurrentPhotoPath,prodphoto);
					break;
				}
			}

			//	galleryAddPic();
			mCurrentPhotoPath = null;
		}

	}

	public void decodeBase64File(String imageString,ImageView imageView) {
		try{
			byte[] encodeByte = Base64.decode(imageString, Base64.DEFAULT);

			/* There isn't enough memory to open up more than a couple camera photos */
			/* So pre-scale the target bitmap into which the file is decoded */

			/* Get the size of the ImageView */
			int targetW = imageView.getWidth();
			int targetH = imageView.getHeight();

			/* Get the size of the image */
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			//BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
			Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,encodeByte.length, bmOptions);
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;

			/* Figure out which way needs to be reduced less */
			int scaleFactor = 1;
			if ((targetW > 0) || (targetH > 0)) {
				scaleFactor = Math.min(photoW/targetW, photoH/targetH);
			}

			/* Set bitmap options to scale the image decode target */
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;
			bmOptions.inPurgeable = true;

			/* Decode the JPEG file into a Bitmap */
			//	Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
			bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
					encodeByte.length, bmOptions);

//			Bitmap bitmaprst = rotate(bitmap,90);

			imageView.setImageBitmap(bitmap);

//			ByteArrayOutputStream stream = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//						byte[] bitMapData = stream.toByteArray();
//						String camera_image = Base64.encodeToString(bitMapData,
//								Base64.DEFAULT);
//						SOTDatabase.init(InvoiceHeader.this);
//
//						Cursor ImgCursor = SOTDatabase.getImageCursor();
//						if (ImgCursor.getCount() > 0) {
//							String signature_image = SOTDatabase
//									.getSignatureImage();
//							SOTDatabase.updateImage(1, signature_image,
//									camera_image);
//						} else {
//							SOTDatabase.storeImage(1, "", camera_image);
//						}
//						Log.d("Camera Image", "cam" + camera_image);
		}catch (OutOfMemoryError e){
			e.printStackTrace();
		}
	}

	public void decodeImagePathFile(String imageString,ImageView imageView) {
		try {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
			int targetW = imageView.getWidth();
			int targetH = imageView.getHeight();

		/* Get the size of the image */
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imageString, bmOptions);
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
			int scaleFactor = 1;
			if ((targetW > 0) || (targetH > 0)) {
				scaleFactor = Math.min(photoW / targetW, photoH / targetH);
			}

		/* Set bitmap options to scale the image decode target */
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;
			bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
			Bitmap bitmap = BitmapFactory.decodeFile(imageString, bmOptions);

//		Bitmap bitmaprst = rotate(bitmap,90);

			imageView.setImageBitmap(bitmap);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] bitMapData = stream.toByteArray();
			String camera_image = Base64.encodeToString(bitMapData,
					Base64.DEFAULT);
			SOTDatabase.init(InvoiceHeader.this);

			Cursor ImgCursor = SOTDatabase.getImageCursor();
			if (ImgCursor.getCount() > 0) {
				Log.d("if","if");
				String signature_image = SOTDatabase
						.getSignatureImage();
				SOTDatabase.updateImage(1, signature_image,
						camera_image);
			} else {
				Log.d("else","else");
				SOTDatabase.storeImage(1, "", camera_image);
			}
			Log.d("Camera Image", "cam" + camera_image);

		}catch (OutOfMemoryError e){
			e.printStackTrace();
		}

	}

	private String getRightAngleImage(String photoPath) {

		try {
			ExifInterface ei = new ExifInterface(photoPath);
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			int degree = 0;

			switch (orientation) {
				case ExifInterface.ORIENTATION_NORMAL:
					degree = 0;
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				case ExifInterface.ORIENTATION_UNDEFINED:
					degree = 90;
					break;
				default:
					degree = 90;
			}

			return rotateImage(degree,photoPath);

		} catch (Exception e) {
			e.printStackTrace();
			helper.dismissProgressView(salesO_parent);
		}

		return photoPath;
	}

	private String rotateImage(int degree, String imagePath){
		Log.d("degree", "-->"+degree);
		if(degree<=0){
			return imagePath;
		}
		try{
			Bitmap b= BitmapFactory.decodeFile(imagePath);
			Matrix matrix = new Matrix();
			if(b.getWidth()>b.getHeight()){
				matrix.setRotate(degree);
//				b.recycle();
				b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
						matrix, true);
			}

			FileOutputStream fOut = new FileOutputStream(imagePath);
			String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
			String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

			FileOutputStream out = new FileOutputStream(imagePath);
			if (imageType.equalsIgnoreCase("png")) {
				b.compress(Bitmap.CompressFormat.PNG, 100, out);
			}else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
				b.compress(Bitmap.CompressFormat.JPEG, 100, out);
			}
			fOut.flush();
			fOut.close();

			b.recycle();
		}catch (Exception e){
			e.printStackTrace();
			helper.dismissProgressView(salesO_parent);
		}
		return imagePath;
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

	public ArrayList<SO> onlineInvoiceHeader(String webMethName, String notpaid) {
		double totalamount = 0.00,overduetotalamount = 0.00;
		ArrayList<SO> resultlist = new ArrayList<SO>();
		overdue_list.clear();
		String username = "";

		int modeid = FWMSSettingsDatabase.getInvoiceuserModeId();
		if (modeid == 1) {
			FWMSSettingsDatabase.updateInvoiceuserMode(1);
			username = SupplierSetterGetter.getUsername();
		} else {
			FWMSSettingsDatabase.updateInvoiceuserMode(0);
			username = "";
		}

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		Log.d("webMethName",webMethName);

		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo balanceflag = new PropertyInfo();
		PropertyInfo VanCode = new PropertyInfo();
		PropertyInfo LocationCode = new PropertyInfo();
		PropertyInfo User = new PropertyInfo();
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		Log.d("cmpnyCode",cmpnyCode);

		balanceflag.setName("BalanceFlag");
		balanceflag.setValue(notpaid);
		balanceflag.setType(String.class);
		request.addProperty(balanceflag);

		Log.d("notpaid",notpaid);

		VanCode.setName("VanCode");
		VanCode.setValue(select_van);
		VanCode.setType(String.class);
		request.addProperty(VanCode);

		Log.d("select_van",select_van);

		if(webMethName.matches("fncGetInvoiceHeader")){
			LocationCode.setName("LocationCode");
			LocationCode.setValue(loccode);
			LocationCode.setType(String.class);
			request.addProperty(LocationCode);

			Log.d("loccode","--->"+loccode);

			User.setName("User");
			User.setValue(username);
			User.setType(String.class);
			request.addProperty(User);

			Log.d("username",username);
		}
		String suppTxt = null;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(valid_url);
		try {
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			suppTxt = response.toString();
			headeresult = " { SOHeader : " + suppTxt + "}";
			JSONObject jsonResponse;

			try {
				jsonResponse = new JSONObject(headeresult);
				JSONArray jsonMainNode = jsonResponse.optJSONArray("SOHeader");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					String ccSno = jsonChildNode.optString("InvoiceNo")
							.toString();
					String ccDate = jsonChildNode.optString("InvoiceDate")
							.toString();
					String customerCode = jsonChildNode.optString(
							"CustomerCode").toString();
					String customerName = jsonChildNode.optString(
							"CustomerName").toString();
					String amount = jsonChildNode.optString("NetTotal")
							.toString();
					String BalanceAmount = jsonChildNode.optString("BalanceAmount")
							.toString();

					String NoOfDays = jsonChildNode.optString("NoOfDays")
							.toString();
					String DueDays = jsonChildNode.optString("DueDays")
							.toString();
					String InvoiceSigned = jsonChildNode.optString("InvoiceSigned")
							.toString();
					String IsClosed = jsonChildNode.optString("IsClosed")
							.toString();
					String IsPosted = jsonChildNode.optString("IsPosted")
							.toString();
					String dono =jsonChildNode.optString("DoNo").toString();
					String salesType =jsonChildNode.optString("SalesType").toString();

					String customeraddress1 = jsonChildNode.optString("CustomerAddress1").toString();
					String customeraddress2 = jsonChildNode.optString("CustomerAddress2").toString();
					String customeraddress3 = jsonChildNode.optString("CustomerAddress3").toString();
					String createDate = jsonChildNode.optString("CreateDate").toString();

					Log.d("DonoValue","-->"+dono);


					SO so = new SO();
					so.setSno(ccSno);
					so.setCustomerCode(customerCode);
					so.setCustomerName(customerName);
					so.setNettotal(amount);
					so.setBalanceamount(BalanceAmount);
					so.setNoOfDays(NoOfDays);
					so.setDueDays(DueDays);
					so.setInvoiceSigned(InvoiceSigned);
					so.setIsClosed(IsClosed);
					so.setIsPosted(IsPosted);
					so.setDono(dono);
					so.setSalesType(salesType);

					so.setCustomeraddress1(customeraddress1);
					so.setCustomeraddress2(customeraddress2);
					so.setCustomeraddress3(customeraddress3);

					/*double net = 0;
					if (amount != null && !amount.isEmpty()) {
						net = Double.parseDouble(amount);
					}
					totalamount = totalamount + Double.valueOf(net);*/

					double bal = 0;
					if (BalanceAmount != null && !BalanceAmount.isEmpty()) {
						bal = Double.parseDouble(BalanceAmount);
					}

					if(bal>0) {
						totalamount = totalamount + Double.valueOf(bal);
					}
					String date="";
					if(Company.getShortCode().matches("JUBI")){
						StringTokenizer tokens = new StringTokenizer(ccDate,
								" ");
						date = tokens.nextToken();
						StringTokenizer tokens1 = new StringTokenizer(createDate,
								" ");
						String date1 = tokens1.nextToken();
						String time = tokens1.nextToken();
						so.setDate(date+" "+time);
					}else {
						if (ccDate != null && !ccDate.isEmpty()) {
							StringTokenizer tokens = new StringTokenizer(ccDate,
									" ");
							date = tokens.nextToken();
							so.setDate(date);
						} else {
							so.setDate(ccDate);
						}
					}
					resultlist.add(so);

					SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
					long diffday=0;
					try {
						Date date1 = myFormat.parse(serverdate);
						Date date2 = myFormat.parse(date);
						long diff = date1.getTime() - date2.getTime();

						diffday = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

						System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
					} catch (ParseException e) {
						e.printStackTrace();
					}

					int noofd = 0;
					if (NoOfDays != null && !NoOfDays.isEmpty() && !NoOfDays.equalsIgnoreCase("null")) {
						noofd = Integer.parseInt(NoOfDays);
					}

					int dued = 0;
					if (DueDays != null && !DueDays.isEmpty()&& !DueDays.equalsIgnoreCase("null")) {
						dued = Integer.parseInt(DueDays);
					}

//					int overduedays = noofd - dued;
					long overduedays=0;
					if(dued>0){
						overduedays = diffday - dued;
						Log.d("overduedays",""+overduedays);
						so.setOverdueDays(""+overduedays);
					}

					if(overduedays >0){
						overdue_list.add(so);

						double overduebal = 0;
						if (BalanceAmount != null && !BalanceAmount.isEmpty()) {
							overduebal = Double.parseDouble(BalanceAmount);
						}

						if(overduebal>0) {
							overduetotalamount = overduetotalamount + Double.valueOf(overduebal);
						}
					}


				}
				SO.setTotalamount(totalamount);
				SO.setOverdueTotalAmount(overduetotalamount);
			} catch (JSONException e) {

				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			suppTxt = "Error occured";
		}
		return resultlist;
	}

	public ArrayList<SO> offlineInvoiceHeader() throws ParseException {

		ArrayList<SO> resultlist = new ArrayList<SO>();
		Log.d("Offline", "Offline");

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("CustomerCode", "");
		hm.put("FromDate", "");
		hm.put("ToDate", "");
		hm.put("BalanceFlag", "0");

		if (podpending != null && !podpending.isEmpty()) {

			if (podpending.matches("1")) {
				hm.put("BalanceFlag", "");
				resultlist = OfflineDatabase
						.getRetriveInvoiceHeaderWithoutDOSign(hm);
			} else {
				resultlist = OfflineDatabase.getRetriveInvoiceHeader(hm);
			}
		} else {
			resultlist = OfflineDatabase.getRetriveInvoiceHeader(hm);
		}

		Log.d("resultlist Size", resultlist.size() + "");

		return resultlist;
	}

	public ArrayList<SO> offlineSearchInvoiceHeader(String customercode,
			String startmonthdate, String lastmonthdate, String status)
			throws ParseException {

		String from="",to="";
		Date startDate=null,endDate=null;
		ArrayList<SO> resultlist = new ArrayList<SO>();
		Log.d("Offline Search", "Offline Search");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		if(startmonthdate!=null && !startmonthdate.isEmpty()){
			startDate = dateFormat.parse(startmonthdate);
		}

		if(lastmonthdate!=null && !lastmonthdate.isEmpty()){
			endDate = dateFormat.parse(lastmonthdate);
		}

		SimpleDateFormat regateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(startmonthdate!=null && !startmonthdate.isEmpty()){
			from = regateFormat.format(startDate) + " 00:00:00";
		}

		if(lastmonthdate!=null && !lastmonthdate.isEmpty()){
			to = regateFormat.format(endDate) + " 24:00:00";
		}

		Log.d("fromDate", from);
		Log.d("toDate", to);

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("CustomerCode", customercode);
		hm.put("FromDate", from);
		hm.put("ToDate", to);
		hm.put("BalanceFlag", status);

		if (status.matches("3")) {
			hm.put("BalanceFlag", "");
			resultlist = OfflineDatabase
					.getRetriveInvoiceHeaderWithoutDOSign(hm);

		} else {
			resultlist = OfflineDatabase.getRetriveInvoiceHeader(hm);
			Log.d("getInvoiceHeader ", "->" + resultlist.size());
		}

		// resultlist = OfflineDatabase.getRetriveInvoiceHeader(hm);

		Log.d("resultlist Size", resultlist.size() + "");

		return resultlist;
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

					Log.d("ReferenceLocation", "" + ReferenceLocation);

					HashMap<String, String> customerhm = new HashMap<String, String>();
					if (ReferenceLocation != null
							&& !ReferenceLocation.isEmpty()) {
						customerhm.put(cust_code, cust_Name + "/"
								+ ReferenceLocation);
					} else {
						customerhm.put(cust_code, cust_Name);
					}

					// HashMap<String, String> customerhm = new HashMap<String,
					// String>();
					// customerhm.put(cust_code, cust_Name);
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

	// offline
	private String checkInternetStatus() {

		String internetStatus = "";
		String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			if(mobileHaveOfflineMode.matches("1")){

		checkOffline = OfflineCommon.isConnected(InvoiceHeader.this);
 		onlineMode = OfflineDatabase.getOnlineMode();
 		Log.d("checkOnlinemode",""+checkOffline+","+onlineMode);
		if (onlineMode.matches("True")) {
			if (checkOffline == true) {
				String Off_dialog = OfflineDatabase
						.getInternetMode("OfflineDialog");
				Log.d("Off_dialog",Off_dialog);
				if (Off_dialog.matches("true")) {
					internetStatus = "true";
				} else {
					offlineCommon.OfflineAlertDialog();
					Boolean dialogStatus = offlineCommon.showDialog();
					OfflineDatabase.updateInternetMode("OfflineDialog",
							dialogStatus + "");
					Log.d("Offline DialogStatus", "" + dialogStatus);
					internetStatus = "" + dialogStatus;
					Toast.makeText(getApplicationContext(),"No Internet Connection!!!",Toast.LENGTH_SHORT).show();
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
		}else{
			Toast.makeText(getApplicationContext(),"No Internet Connection!!!",Toast.LENGTH_SHORT).show();
		}
		onlineMode = OfflineDatabase.getOnlineMode();
		if (onlineMode.matches("True")) {
			offlineLayout.setVisibility(View.GONE);
			if (checkOffline == true) {
				if (internetStatus.matches("true")) {
					offlineLayout.setVisibility(View.VISIBLE);
					offlineLayout
							.setBackgroundResource(drawable.temp_offline_pattern_bg);
				}
			}

		} else if (onlineMode.matches("False")) {
			offlineLayout.setVisibility(View.VISIBLE);
		}

		String deviceId = RowItem.getDeviceID();

		Log.d("device id", "dev " + deviceId);

//		return internetStatus;
			}else{
				internetStatus = "false";
			}
		}else{
			internetStatus = "false";
		}
		return internetStatus;

	}

	/** Location Start **/

	public void getCurrentlocation() {

		try {
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			// getting GPS status
			boolean isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			boolean isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 20000, 0, this);
					Log.d("Network", "Network Enabled");
					if (locationManager != null) {
						Location location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							Log.d("location", "not null");
							if (onlineMode.matches("True")) {
								if (checkOffline == true) {
								} else { // online
									onLocationChanged(location);
								}
							}
							// onLocationChanged(location);
						} else {
							Log.d("location", "null");
							locationManager.requestLocationUpdates(
									LocationManager.NETWORK_PROVIDER, 20000, 0,
									this);
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 20000, 0, this);
					Log.d("GPS", "GPS Enabled");
					if (locationManager != null) {
						Location location = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							Log.d("location", "not null");
							if (onlineMode.matches("True")) {
								if (checkOffline == true) {
								} else { // online
									onLocationChanged(location);
								}
							}
							// onLocationChanged(location);
						} else {
							Log.d("location", "null");
							locationManager.requestLocationUpdates(
									LocationManager.GPS_PROVIDER, 20000, 0,
									this);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onLocationChanged(Location location) {

		try {

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
				} else { // online
					mLatitude = location.getLatitude();
					mLongitude = location.getLongitude();

					Log.d("Lat ", mLatitude + " Long " + mLongitude);

					String cityName = "";
					Geocoder geocoder = new Geocoder(this, Locale.getDefault());
					List<Address> addresses;

					addresses = geocoder.getFromLocation(mLatitude, mLongitude,
							1);

					if (addresses.size() > 0) {
						cityName = addresses.get(0).getAddressLine(0);
					}

					locationManager.removeUpdates(this);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	/** Location End **/

	private void loadprogress() {
		spinnerLayout = new LinearLayout(InvoiceHeader.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		InvoiceHeader.this.addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(salesO_parent, false);
		progressBar = new ProgressBar(InvoiceHeader.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		spinnerLayout.addView(progressBar);
	}
	@Override
	public void onResume()
	{
		super.onResume();
		//if(D) Log.e(TAG, "--- onResume ---");

	}
	@Override
	public void onStart() {
		super.onStart();
		//if(D) Log.e(TAG, "--- onStart ---");
		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

		if(printertype.matches("4 Inch Bluetooth")) {
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
	public void fourInch(){

	/*	Thread networkThread = new Thread() {
			@Override
			public void run() {
				try {


					runOnUiThread (new Runnable(){
						public void run() {
							//first completed second will start

						}
					});
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		networkThread.start();*/





	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case GlobalData.MESSAGE_STATE_CHANGE:
					Log.d("case","MESSAGE_STATE_CHANGE");
					//if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
					switch (msg.arg1) {
						case GlobalData.STATE_CONNECTED:
							//mTitle.setText(R.string.title_connected_to);
							//mTitle.append(mConnectedDeviceName);
							//Intent intent = new Intent(BluetoothActivity.this,PrintActivity.class);
							//intent.putExtra("COMM", 0);//0-BLUETOOTH
							// Set result and finish this Activity
						//	startActivity(intent);
                            Log.d("case","STATE_CONNECTED");
							print4Inch();
							//helper.dismissProgressDialog();
							break;
						case GlobalData.STATE_CONNECTING:
							//mTitle.setText(R.string.title_connecting);
							Log.d("case","STATE_CONNECTING");

							break;
						case GlobalData.STATE_LISTEN:
							Log.d("case","STATE_LISTEN");
							break;
						case GlobalData.STATE_NONE:
							Log.d("case","STATE_NONE");
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
	protected void onDestroy(){
		super.onDestroy();
		if(GlobalData.mService!=null){
			GlobalData.mService.stop();
		}
	}
	public void reconnectDialog(String msg){
		final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
	   final Dialog dialog = new Dialog(InvoiceHeader.this);

		dialog.setContentView(R.layout.dialog_reconnect);
		dialog.setTitle(msg);
        ImageView reconnect = (ImageView) dialog.findViewById(R.id.reconnect);

		reconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
         if (BluetoothAdapter.checkBluetoothAddress(macaddress))
				{
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device,true);

				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	public void print4Inch(){
		String locationName = SalesOrderSetGet.getLocationname();
		String locationCode = SalesOrderSetGet.getLocationcode();
		Log.d("printStr","ss "+printStr);
//		int nofcopies = Integer.parseInt(stnumber.getText().toString());
		final CubePrint mPrintCube = new CubePrint(InvoiceHeader.this,FWMSSettingsDatabase.getPrinterAddress());
		mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
			@Override
			public void onCompleted() {
				/*if(printStr.equals("InvoiceHeaderDeliveryOnInvoice")){
					mPrintCube.printInvoice(sosno, sodate, socustomercode, socustomername,
							product, productdet, printsortHeader, gnrlStngs, 1,
							product_batchArr, footerArr);
				}else{*/
				helper.showLongToast(R.string.printed_successfully);
				//}

			}
		});

		if(printStr.equals("InvoiceSummary")){
			mPrintCube.InvoiceSummmaryPrint(mInvoiceDatePrintFlag,userInvoice,fromDateInvoice,
					toDateInvoice, locationCode, locationName,
					invoicePrintDateArr);
		}else if(printStr.equals("InvoiceHeader")){
			mPrintCube.printInvoice(sosno, sodate, socustomercode, socustomername,
					product, productdet, printsortHeader, gnrlStngs, 1,
					product_batchArr, footerArr,"");
		}else if(printStr.equals("DeliveryOnInvoice")){
			int nofcopies = Integer.parseInt(stnumber.getText().toString());
			mPrintCube.DeliveryOnInvoicePrint(sosno, sodate, socustomercode,
					socustomername, product, productdet, printsortHeader,
					gnrlStngs, nofcopies, product_batchArr, footerArr);
		}
		else if(printStr.equals("InvoiceHeaderDeliveryOnInvoice")){
			int nofcopies = Integer.parseInt(stnumber.getText().toString());
			mPrintCube.DeliveryOnInvoicePrint(sosno, sodate, socustomercode,
					socustomername, product, productdet, printsortHeader,
					gnrlStngs, nofcopies, product_batchArr, footerArr);

			mPrintCube.printInvoice(sosno, sodate, socustomercode, socustomername,
					product, productdet, printsortHeader, gnrlStngs, 1,
					product_batchArr, footerArr,"");

		}
	}

	private class AsyncCallWSCustomerEmail extends AsyncTask<Void, Void, Void> {
		String response="",jsonStringemail = null;
		private JSONObject jsonResponseemail=null;
		private JSONArray jsonMainNodeemail=null;
		HashMap<String,String> hmemail = new HashMap<String,String>();
		@Override
		protected void onPreExecute() {
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			hmemail.put("CompanyCode", cmpnyCode);
			hmemail.put("CustomerCode", socustomercode);
			try {
				jsonStringemail = WebServiceClass.parameterService(hmemail,"fncGetCustomer");

				jsonResponseemail = new JSONObject(jsonStringemail);
				jsonMainNodeemail = jsonResponseemail.optJSONArray("JsonArray");


				int lengthJsonArr = jsonMainNodeemail.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode;

					jsonChildNode = jsonMainNodeemail.getJSONObject(i);
					response = jsonChildNode.optString("Email")
							.toString();
				}
				Log.d("response",response.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if(!response.matches("")){
				new AsyncCallWSEmail().execute();
			}else{
				Toast.makeText(InvoiceHeader.this,"Customer has no email address",Toast.LENGTH_LONG).show();

				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesO_parent, true);
			}



		}
	}

	private class AsyncCallWSEmail extends AsyncTask<Void, Void, Void> {
		String response="",jsonStringemail = null;
		private JSONObject jsonResponseemail=null;
		private JSONArray jsonMainNodeemail=null;
		HashMap<String,String> hmemail = new HashMap<String,String>();
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			hmemail.put("CompanyCode", cmpnyCode);
			hmemail.put("sTranNo", sosno);
			hmemail.put("TranType", "IN");
			try {
				jsonStringemail = WebServiceClass.parameterService(hmemail,"fncEmailInvoice");

				jsonResponseemail = new JSONObject(jsonStringemail);
				jsonMainNodeemail = jsonResponseemail.optJSONArray("JsonArray");


				int lengthJsonArr = jsonMainNodeemail.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode;

					jsonChildNode = jsonMainNodeemail.getJSONObject(i);
					response = jsonChildNode.optString("Result")
							.toString();
				}
				Log.d("response",response.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if(response.equalsIgnoreCase("True")){
				Toast.makeText(InvoiceHeader.this,"Email sent successfully",Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(InvoiceHeader.this,"Failed to sent email",Toast.LENGTH_LONG).show();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);

		}
	}

	private String strValidate(String value){
		String mValue = "";

		if(value != null && !value.isEmpty()){
			mValue= value;
		}
		return mValue;
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
				hashValue.put("CompanyCode", companyCode);
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
 public void alertDialogLocation() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				InvoiceHeader.this);
		final EditText editText = new EditText(InvoiceHeader.this);
		final ListView listview = new ListView(InvoiceHeader.this);
		LinearLayout layout = new LinearLayout(InvoiceHeader.this);
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(InvoiceHeader.this,
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
						InvoiceHeader.this, searchResults);
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

	/*public void print4Inch(){

		Log.d("printStr","ss "+printStr);
//		int nofcopies = Integer.parseInt(stnumber.getText().toString());
		CubePrint mPrintCube = new CubePrint(InvoiceHeader.this,FWMSSettingsDatabase.getPrinterAddress());
		mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
			@Override
			public void onCompleted() {
				helper.showLongToast(R.string.printed_successfully);

			}
		});
		String locationName = SalesOrderSetGet.getLocationname();
		String locationCode = SalesOrderSetGet.getLocationcode();

		if(printStr.equals("InvoiceSummary")){
			mPrintCube.InvoiceSummmaryPrint(mInvoiceDatePrintFlag,userInvoice,fromDateInvoice,
					toDateInvoice, locationCode, locationName,
					invoicePrintDateArr);
		}else if(printStr.equals("InvoiceHeader")){
			mPrintCube.printInvoice(sosno, sodate, socustomercode, socustomername,
					product, productdet, printsortHeader, gnrlStngs, 1,
					product_batchArr, footerArr);
		}else if(printStr.equals("DeliveryOnInvoice")){
			int nofcopies = Integer.parseInt(stnumber.getText().toString());
			mPrintCube.DeliveryOnInvoicePrint(sosno, sodate, socustomercode,
					socustomername, product, productdet, printsortHeader,
					gnrlStngs, nofcopies, product_batchArr, footerArr);
		}


	}*/
	@Override
	public void onBackPressed() {

		if (back!=null && !back.isEmpty() && back.matches("dashboard")) {
			// Intent i = new Intent(InvoiceHeader.this,
			// CombinedChartActivity.class);
			// startActivity(i);
			InvoiceHeader.this.finish();
		} else {
			Intent i = new Intent(InvoiceHeader.this, LandingActivity.class);
			startActivity(i);
			InvoiceHeader.this.finish();
		}

	}

	private class AsyncCallWSConsignmentDelete extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
//			spinnerLayout = new LinearLayout(InvoiceHeader.this);
//			spinnerLayout.setGravity(Gravity.CENTER);
//			InvoiceHeader.this.addContentView(spinnerLayout,
//					new LinearLayout.LayoutParams(
//							ViewGroup.LayoutParams.FILL_PARENT,
//							ViewGroup.LayoutParams.FILL_PARENT));
//			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
//			enableViews(salesO_parent, false);
//			progressBar = new ProgressBar(InvoiceHeader.this);
//			progressBar.setProgress(android.R.attr.progressBarStyle);
//			progressBar.setIndeterminateDrawable(getResources().getDrawable(
//					drawable.greenprogress));
//			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {


			String user = SupplierSetterGetter.getUsername();
			HashMap<String, String> hashValue = new HashMap<String, String>();
			hashValue.put("CompanyCode", companyCode);
			hashValue.put("ConsignmentNo", sosno);
			hashValue.put("TranType", "COI");
			hashValue.put("User", user);


			String jsonString = WebServiceClass.parameterService(hashValue,
					"fncDeleteConsignment");

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("JsonArray");

				int lengthJsonArr = jsonMainNode.length();

				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode;
					jsonChildNode = jsonMainNode.getJSONObject(i);
					deleteResult = jsonChildNode.optString("Result").toString();
					Log.d("deleteResult", "-->" + deleteResult);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (deleteResult.matches("True")) {

				AsyncCallWSIOHeader task = new AsyncCallWSIOHeader();
				task.execute();
//				new LoadConsignmentHeader().execute();

				Toast.makeText(InvoiceHeader.this, "Delete Successfully",
						Toast.LENGTH_LONG).show();

			} else {

//				progressBar.setVisibility(View.GONE);
//				spinnerLayout.setVisibility(View.GONE);
//				enableViews(salesO_parent, true);

				Toast.makeText(InvoiceHeader.this, "Failed",
						Toast.LENGTH_LONG).show();

			}

		}
	}
}
