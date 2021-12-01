package com.winapp.sot;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
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
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.LocationGetSet;
import com.winapp.model.Product;
import com.winapp.model.Receipt;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.printcube.printcube.BluetoothService;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.printer.PDFActivity;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.PrinterZPL;
import com.winapp.printer.ReceiptPrintPreview;
import com.winapp.printer.UIHelper;
import com.winapp.sotdetails.CustomerSetterGetter;

import static com.winapp.helper.Constants.FNCA4RECEIPTGENERATE;

@SuppressLint("NewApi")
public class ReceiptHeader extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace {
	private  ArrayList<HashMap<String, String>> salesreturnArr;
	String valid_url, serverdate, sDate, eDate, cuscode, currentdate, username,back_navigate="";
	int textlength = 0;
	int month, day, year, printid;
	EditText edCustomerCode, starteditTextDate, endeditTextDate, edprintDate,edCustCode,edCustoName,
			receipt_userCode, edprintDate1,sl_namefield;
	Button btcstmrsrch;
	ListView so_lv;
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	ArrayList<HashMap<String, String>> searchResults;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> userArr = new ArrayList<HashMap<String, String>>();
	CustomAlertAdapterSupp arrayAdapterSupp;
	private AlertDialog myalertDialog = null;
	LinearLayout salesO_parent, searchCstmrlayout, codelayout,
			totaloutstanding_ll;
	Calendar startCalendar, endCalendar, printerCalendar;
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	// ArrayList<SO> searchCstCdArr = new ArrayList<SO>();
	ArrayList<String> sortproduct = new ArrayList<String>();
	static ArrayList<HashMap<String, String>> soDetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> doDetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> soHeadersArr = new ArrayList<HashMap<String, String>>();
	private ArrayList<ProductDetails> mSRHeaderDetailArr;
	static String headeresult, Deletestring = "";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetReceiptHeader";
	private static String NAMESPACE = "http://tempuri.org/";
	String sNo = "",printType="",product_stock_jsonString;
	boolean mnnbldsbl;
	CheckBox so_checkbox;
	ArrayList<SO> list = new ArrayList<SO>();
	HeaderAdapter receiptadapter;
	String sosno, sodate, socustomercode, soamount, sostatus, socustomername,select_van,companyCode="",
			custjsonStr = null;
	private UIHelper helper;

	ArrayList<ProdDetails> product;
	ArrayList<ProductDetails> productdet,footerArr;
	String jsonString = null, jsonStr = null, gnrlStngs,jsonSt;
	JSONObject jsonResponse, jsonResp, jsonRespfooter, custjsonResp;
	JSONArray jsonMainNode, jsonSecNode, jsonSecNodefooter, rdjsonSecNode, custjsonMainNode;
	HashMap<String, String> hashValue = new HashMap<String, String>();
	HashMap<String, String> hashVl = new HashMap<String, String>();
	private ArrayList<String> listarray = new ArrayList<String>();
	private ArrayList<Product> sngleArray = new ArrayList<Product>();
	private ArrayList<Receipt> receiptlist,receiptinvoicelist;
	HashMap<String, String> hm = new HashMap<String, String>();
	Cursor cursor;
	ImageButton search, back, printer, addnew, re_print;
	List<String> sort = new ArrayList<String>();
	HashSet<String> hs = new HashSet<String>();
	List<String> printsortHeader = new ArrayList<String>();
	TextView totaloutstanding;
	private SlidingMenu menu;
	boolean receiptAll = false;

	// Offline
	LinearLayout offlineLayout;
	private OfflineDatabase offlineDatabase;
	boolean checkOffline;
	String onlineMode, offlineDialogStatus,mobileHaveOfflineMode="";
	private OfflineCommon offlineCommon;
	OfflineSettingsManager spManager;
	static String custresult;
	String suppTxt = null,cmpnyCode;
	HashMap<String, String> hashmap = new HashMap<String, String>();
	ArrayList<String> alinvoice = new ArrayList<String>();
	JSONObject product_stock_jsonResponse;
	JSONArray product_stock_jsonMainNode;
	String flag= "";
	double qty;
	List<LocationGetSet> locationList, lctnList;
	double calculate =0.00;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.receipt_header);
		GlobalData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		GlobalData.mService = new BluetoothService(this, mHandler);
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText(getResources().getString(R.string.receipts));
		search = (ImageButton) customNav.findViewById(R.id.search_img);
		printer = (ImageButton) customNav.findViewById(R.id.printer);
		addnew = (ImageButton) customNav.findViewById(R.id.custcode_img);

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

		addnew.setVisibility(View.GONE);

		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();

		// @Offline
		onlineMode = OfflineDatabase.getOnlineMode();
		offlineDatabase = new OfflineDatabase(ReceiptHeader.this);
		offlineCommon = new OfflineCommon(ReceiptHeader.this);
		checkOffline = OfflineCommon.isConnected(ReceiptHeader.this);
		OfflineDatabase.init(ReceiptHeader.this);
		offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);

		edCustomerCode = (EditText) findViewById(R.id.receiptCustCode);
		starteditTextDate = (EditText) findViewById(R.id.starteditTextDate);
		endeditTextDate = (EditText) findViewById(R.id.endeditTextDate);

		receipt_userCode = (EditText) findViewById(R.id.receipt_userCode);

		btcstmrsrch = (Button) findViewById(R.id.receipt_btsearch);
		so_lv = (ListView) findViewById(R.id.receipt_listView1);

		totaloutstanding_ll = (LinearLayout) findViewById(R.id.totaloutstanding_ll);
		re_print = (ImageButton) findViewById(R.id.re_print);
		totaloutstanding = (TextView) findViewById(R.id.totaloutstanding);
		salesO_parent = (LinearLayout) findViewById(R.id.receipt_parent);
		codelayout = (LinearLayout) findViewById(R.id.codelayout);
		searchCstmrlayout = (LinearLayout) findViewById(R.id.searchlayout);

		sl_namefield= (EditText) findViewById(R.id.sl_namefield);
		
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			back_navigate = getIntent().getStringExtra("dashboard");
		}


		searchCstmrlayout.setVisibility(View.GONE);
		receiptlist = new ArrayList<Receipt>();
		receiptinvoicelist= new ArrayList<Receipt>();
		product = new ArrayList<ProdDetails>();
		productdet = new ArrayList<ProductDetails>();
		al.clear();
		userArr.clear();
		// searchCstCdArr.clear();
		startCalendar = Calendar.getInstance();
		endCalendar = Calendar.getInstance();
		printerCalendar = Calendar.getInstance();
		helper = new UIHelper(ReceiptHeader.this);
		SOTDatabase.init(ReceiptHeader.this);
		FWMSSettingsDatabase.init(ReceiptHeader.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,ReceiptHeader.this);
		new SalesOrderWebService(valid_url);
		new WebServiceClass(valid_url);
		footerArr = new ArrayList<ProductDetails>();

		
//		user = SupplierSetterGetter.getUsername();
		username = SupplierSetterGetter.getUsername();
		receipt_userCode.setText(username);

		receiptAll = FormSetterGetter.isReceiptAll();

		if (receiptAll == true) {
			receipt_userCode.setFocusableInTouchMode(true);
			receipt_userCode.setBackgroundResource(drawable.edittext_bg);
			receipt_userCode.setCursorVisible(true);
		}

		// Offline
		if (onlineMode.matches("True")) {
			offlineLayout.setVisibility(View.GONE);
		} else if (onlineMode.matches("False")) {
			offlineLayout.setVisibility(View.VISIBLE);
		}

		spManager = new OfflineSettingsManager(ReceiptHeader.this);
		companyCode = spManager.getCompanyType();
		select_van = SOTDatabase.getVandriver();
		
		if(select_van!=null && !select_van.isEmpty()){			
		}else{
			select_van="";
		}
		// if(offlineLayout.getVisibility() == View.GONE){
		// re_print.setVisibility(View.VISIBLE);
		//
		// }else{
		// re_print.setVisibility(View.INVISIBLE);
		// }


		AsyncCallWSSalesOrder salesOAC = new AsyncCallWSSalesOrder();
		salesOAC.execute();
		AsyncGeneralSettings asyncgs = new AsyncGeneralSettings();
		asyncgs.execute();
		new ShowLocations().execute();

		so_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				TextView receipt_no = (TextView) v.findViewById(R.id.sno);

				String val_receipt_no = receipt_no.getText().toString();
				Log.d("valCustCode", val_receipt_no);
				Intent in = new Intent(ReceiptHeader.this, ReceiptDetails.class);
				in.putExtra("val_receipt_no", val_receipt_no);
				startActivity(in);
				ReceiptHeader.this.finish();
			}
		});
		registerForContextMenu(so_lv);

		edCustomerCode
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						edCustomerCode) {
					@Override
					public boolean onDrawableClick() {
						alertDialogSearch();
						return true;
					}
				});

		receipt_userCode
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						receipt_userCode) {
					@Override
					public boolean onDrawableClick() {

						if (receiptAll == true) {
							alertDialogUserSearch();
						}

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
					new DatePickerDialog(ReceiptHeader.this, startDate,
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
					new DatePickerDialog(ReceiptHeader.this, endDate,
							endCalendar.get(Calendar.YEAR), endCalendar
									.get(Calendar.MONTH), endCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		btcstmrsrch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				cuscode = edCustomerCode.getText().toString();
				sDate = starteditTextDate.getText().toString();
				eDate = endeditTextDate.getText().toString();

//				if (sDate.matches("")) {
//					Toast.makeText(ReceiptHeader.this, "Enter Start Date",
//							Toast.LENGTH_SHORT).show();
//				} else if (eDate.matches("")) {
//					Toast.makeText(ReceiptHeader.this, "Enter End Date",
//							Toast.LENGTH_SHORT).show();
//				} else {

					spinnerLayout = new LinearLayout(ReceiptHeader.this);
					spinnerLayout.setGravity(Gravity.CENTER);
					ReceiptHeader.this
							.addContentView(
									spinnerLayout,
									new LayoutParams(
											ViewGroup.LayoutParams.FILL_PARENT,
											ViewGroup.LayoutParams.FILL_PARENT));
					spinnerLayout.setBackgroundColor(Color
							.parseColor("#80000000"));
					enableViews(salesO_parent, false);
					progressBar = new ProgressBar(ReceiptHeader.this);
					progressBar.setProgress(android.R.attr.progressBarStyle);
					progressBar.setIndeterminateDrawable(getResources()
							.getDrawable(drawable.greenprogress));
					spinnerLayout.addView(progressBar);

//					AsyncCallWSReceiptHeader task = new AsyncCallWSReceiptHeader();
//					task.execute();
				new AsycSearchRecipt().execute();
					
//				}
			}
		});
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				InputMethodManager inputMethodManager = (InputMethodManager) ReceiptHeader.this
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
				if (cursor.getCount() != 0) {
					if (RowItem.getPrintoption().equals("True")) {
						printType = "Receipt";
						SO so = receiptadapter.getItem(receiptadapter
								.getSelectedPosition());
						sosno = so.getSno().toString();

						if (onlineMode.matches("True")) {
							if (checkOffline == true) {

								// Offline
								String offlinePrintStatus = OfflineDatabase
										.getPrintStatus("tblGetReceiptDetail",
												"ReceiptNo", sosno);
								Log.d("offlinePrintStatus", ""
										+ offlinePrintStatus);
								if (offlinePrintStatus != null
										&& !offlinePrintStatus.isEmpty()) {

									helper.showProgressDialog(R.string.generating_invoice);
									AsyncPrintCall task = new AsyncPrintCall();
									task.execute();
								} else {
									Toast.makeText(ReceiptHeader.this,
											"Online Receipt cannot be print",
											Toast.LENGTH_SHORT).show();
								}

							} else { // Online
								helper.showProgressDialog(R.string.generating_receipt);
								AsyncPrintCall task = new AsyncPrintCall();
								task.execute();

							}
						} else if (onlineMode.matches("False")) { // perman
																	// offline

							// Offline
							String offlinePrintStatus = OfflineDatabase
									.getPrintStatus("tblGetReceiptDetail",
											"ReceiptNo", sosno);
							Log.d("offlinePrintStatus", "" + offlinePrintStatus);

							if (offlinePrintStatus != null
									&& !offlinePrintStatus.isEmpty()) {
								helper.showProgressDialog(R.string.generating_receipt);
								AsyncPrintCall task = new AsyncPrintCall();
								task.execute();
							} else {
								Toast.makeText(ReceiptHeader.this,
										"Online Receipt cannot be print",
										Toast.LENGTH_SHORT).show();
							}

						}

						// if (onlineMode.matches("True")) {
						// if (checkOffline == true) {
						//
						// }else{
						// helper.showProgressDialog(R.string.generating_receipt);
						// AsyncPrintCall task = new AsyncPrintCall();
						// task.execute();
						// }
						// }

					} else {
						Toast.makeText(ReceiptHeader.this,
								"Please Enable CheckBox", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(ReceiptHeader.this,
							"Please Configure the printer", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		re_print.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SOTDatabase.deleteImage();
				getOnlineCustomer();
				String locatnCode = SalesOrderSetGet.getLocationcode();
				Log.d("LocationCodeCheck","-->"+locatnCode+"-->"+LocationGetSet.getIsMainLocation());
				if(!LocationGetSet.getIsMainLocation().matches(locatnCode)){
					if (Company.getShortCode().matches("RAJAGRO")) {
						new AsyncGetProductStock().execute();
//					getAlert();
					}else{
						getAlert();
					}
				}else{
					getAlert();
				}

			}
		});
	}

	private void getAlert() {
			cursor = FWMSSettingsDatabase.getPrinter();
			if (cursor.getCount() != 0) {

				AlertDialog.Builder alert = new AlertDialog.Builder(
						ReceiptHeader.this);
				TextView textView0 = new TextView(ReceiptHeader.this);
				TextView textview = new TextView(ReceiptHeader.this);
				TextView textview1 = new TextView(ReceiptHeader.this);
				edprintDate = new EditText(ReceiptHeader.this);
				edprintDate1 = new EditText(ReceiptHeader.this);
				LinearLayout layout = new LinearLayout(ReceiptHeader.this);
				layout.setOrientation(LinearLayout.VERTICAL);
				alert.setTitle(getResources().getString(R.string.print));
				alert.setCancelable(true);
				layout.setPadding(15, 15, 15, 15);
				textview1.setPadding(0, 15, 0, 0);

				textView0.setText("Choose Customer");
				textview.setText(getResources().getString(R.string.enter_from_date));
				textview1.setText(getResources().getString(R.string.enter_to_date));

				edCustCode = new EditText(ReceiptHeader.this);
				edCustoName = new EditText(ReceiptHeader.this);

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

				edprintDate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.mipmap.ic_calendar, 0);
				edprintDate.setCursorVisible(false);
				edprintDate.setText(starteditTextDate.getText().toString());

				edprintDate1.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.mipmap.ic_calendar, 0);
				edprintDate1.setCursorVisible(false);
				edprintDate1.setText(endeditTextDate.getText().toString());

				edprintDate.setText(serverdate);
				edprintDate1.setText(serverdate);

				edprintDate.setFocusable(false);
				edprintDate1.setFocusable(false);


				layout.addView(textView0);
				layout.addView(edCustCode);
				layout.addView(textview);
				layout.addView(edprintDate);
				layout.addView(textview1);
				layout.addView(edprintDate1);
				alert.setView(layout);

				String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

				if (printertype.matches("A4 Wifi Direct")) {
					textview.setVisibility(View.GONE);
					edprintDate.setVisibility(View.GONE);
				} else {
					textview.setVisibility(View.VISIBLE);
					edprintDate.setVisibility(View.VISIBLE);
				}


				final DatePickerDialog.OnDateSetListener ptdate = new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
										  int monthOfYear, int dayOfMonth) {
						printerCalendar.set(Calendar.YEAR, year);
						printerCalendar.set(Calendar.MONTH, monthOfYear);
						printerCalendar.set(Calendar.DAY_OF_MONTH,
								dayOfMonth);
						printerDate();
					}
				};

				final DatePickerDialog.OnDateSetListener ptdate1 = new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
										  int monthOfYear, int dayOfMonth) {
						printerCalendar.set(Calendar.YEAR, year);
						printerCalendar.set(Calendar.MONTH, monthOfYear);
						printerCalendar.set(Calendar.DAY_OF_MONTH,
								dayOfMonth);
						printerDate1();
					}
				};

				edprintDate.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (MotionEvent.ACTION_UP == event.getAction())
							new DatePickerDialog(ReceiptHeader.this,
									ptdate, printerCalendar
									.get(Calendar.YEAR),
									printerCalendar.get(Calendar.MONTH),
									printerCalendar
											.get(Calendar.DAY_OF_MONTH))
									.show();
						return false;
					}
				});

				edprintDate1.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (MotionEvent.ACTION_UP == event.getAction())
							new DatePickerDialog(ReceiptHeader.this,
									ptdate1, printerCalendar
									.get(Calendar.YEAR),
									printerCalendar.get(Calendar.MONTH),
									printerCalendar
											.get(Calendar.DAY_OF_MONTH))
									.show();
						return false;
					}
				});

				edCustCode.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						alertDialogCustomerSearch();
					}
				});

				alert.setPositiveButton(getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int whichButton) {
								printType = "CurrentDateReceipt";

								currentdate = edprintDate.getText()
										.toString();
								String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
								if (printertype.matches("A4 Wifi Direct")) {
									String cmpnyCode = SupplierSetterGetter.getCompanyCode();
									Log.d("PDFActivity", "PDFActivity");
									new PDFActivity(ReceiptHeader.this, valid_url + "/" + FNCA4RECEIPTGENERATE + "?CompanyCode=" + cmpnyCode + "&TranDate=" + edprintDate1.getText().toString()
											+ "&User=" + username, "report.pdf").execute();
								} else {
									new AsyncCurrentDatePrintCall(edprintDate.getText().toString(), edprintDate1.getText().toString(), edCustCode.getText().toString()).execute();
								}

							} // End of onClick(DialogInterface dialog, int
							// whichButton)
						}); // End of alert.setPositiveButton
				alert.setNegativeButton(getResources().getString(R.string.cancel),
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
				Toast.makeText(ReceiptHeader.this,
						"Please Configure the printer", Toast.LENGTH_SHORT)
						.show();
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

	private void alertDialogCustomerSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				ReceiptHeader.this);
		final EditText editText = new EditText(ReceiptHeader.this);
		final ListView listview = new ListView(ReceiptHeader.this);
		LinearLayout layout = new LinearLayout(ReceiptHeader.this);
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(ReceiptHeader.this, al);
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
						ReceiptHeader.this, searchResults);
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

	private void printerDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		edprintDate.setText(sdf.format(printerCalendar.getTime()));
	}

	private void printerDate1() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		edprintDate1.setText(sdf.format(printerCalendar.getTime()));
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		SO so = receiptadapter.getItem(info.position);
		sosno = so.getSno().toString();

		menu.setHeaderTitle(sosno);

		if (offlineLayout.getVisibility() == View.GONE) {
			if (FormSetterGetter.isDeleteReceipt() == true) {
				menu.add(0, v.getId(), 0, "Delete Receipt");
				menu.add(0, v.getId(), 1, "Print Preview");
			} else {
				menu.add(0, v.getId(), 0, "Print Preview");
			}
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();

		SO so = receiptadapter.getItem(adapterInfo.position);

		sosno = so.getSno().toString();
		sodate = so.getDate().toString();
		socustomercode = so.getCustomerCode().toString();

		if (item.getTitle() == "Print Preview") {
			helper.showProgressDialog(R.string.receipt_printpreview);
			AsyncPrintCall task = new AsyncPrintCall();
			task.execute();
			for (HashMap<String, String> hashMap : al) {
				for (String key : hashMap.keySet()) {
					if (key.equals(socustomercode)) {
						System.out.println(hashMap.get(key));
						socustomername = hashMap.get(key);
					}
				}

			}
		} else if (item.getTitle() == "Delete Receipt") {
			deleteAlertDialog();

		} else {
			return false;
		}
		return true;
	}

	public void deleteAlertDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ReceiptHeader.this);
		builder.setTitle("Confirm Delete");
		builder.setIcon(R.mipmap.delete);
		builder.setMessage("Do you want to delete?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				AsyncCallWSSalesDelete salesDelete = new AsyncCallWSSalesDelete();
				salesDelete.execute();
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

	private class AsyncCallWSSalesDelete extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(ReceiptHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			ReceiptHeader.this.addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesO_parent, false);
			progressBar = new ProgressBar(ReceiptHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String user = SupplierSetterGetter.getUsername();

			HashMap<String, String> hashValue = new HashMap<String, String>();

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("ReceiptNo", sosno);
			hashValue.put("User", user);

			String jsonString = WebServiceClass.parameterService(hashValue,
					"fncDeleteReceipt");

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("JsonArray");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = jsonMainNode.length();

				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode;
					jsonChildNode = jsonMainNode.getJSONObject(i);
					Deletestring = jsonChildNode.optString("Result").toString();
				}

			} catch (JSONException e) {

			} catch (Exception e) {

				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (!Deletestring.matches("")) {

				AsyncCallWSReceiptHeader task = new AsyncCallWSReceiptHeader();
				task.execute();

				Toast.makeText(ReceiptHeader.this, "Delete Successfully",
						Toast.LENGTH_LONG).show();

			} else {

				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesO_parent, true);

				Toast.makeText(ReceiptHeader.this, "Failed", Toast.LENGTH_LONG)
						.show();
			}

		}
	}

	private class AsyncCallWSSalesOrder extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		String dialogStatus;

		@Override
		protected void onPreExecute() {
			al.clear();
			// userArr.clear();
			spinnerLayout = new LinearLayout(ReceiptHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			ReceiptHeader.this.addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesO_parent, false);
			progressBar = new ProgressBar(ReceiptHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			// Offline
			SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
			String finalDate = timeFormat.format(new Date());
			System.out.println(finalDate);

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) {
						Log.d("CheckOffline Alert -->", "True");
						al = getCustomerOffline();
						userArr = getAllUserOffline();
						serverdate = finalDate;
					} else {
						Log.d("CheckOffline Alert -->", "False");
						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
						finish();
					}

				} else {
					Log.d("checkOffline Status -->", "False");

					HashMap<String, String> hm = new HashMap<String, String>();
							hm.put("CompanyCode", companyCode);
							hm.put("VanCode", select_van);
					
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("Code", "W!napp@!@#^");
							
					al = SalesOrderWebService.getWholeCustomer(hm, "fncGetCustomerForSearch");
					userArr = SalesOrderWebService
							.getAllUser("fncGetUserMaster",params);
					serverdate = DateWebservice
							.getDateService("fncGetServerDate");
				}

			} else if (onlineMode.matches("False")) {
				Log.d("Customer Online", onlineMode);
				al = getCustomerOffline();
				userArr = getAllUserOffline();
				serverdate = finalDate;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (serverdate != null) {
				// starteditTextDate.setText(DateTime.date(serverdate));
			//	starteditTextDate.setText(serverdate.split("\\ ")[0]);
			//	endeditTextDate.setText(serverdate.split("\\ ")[0]);

				starteditTextDate.setText("");  // added new
				endeditTextDate.setText("");
				
				AsyncCallWSReceiptHeader task = new AsyncCallWSReceiptHeader();
				task.execute();
			}else{
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesO_parent, true);
			}
		}
	}

	public void alertDialogSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				ReceiptHeader.this);
		final EditText editText = new EditText(ReceiptHeader.this);
		final ListView listview = new ListView(ReceiptHeader.this);
		LinearLayout layout = new LinearLayout(ReceiptHeader.this);
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(ReceiptHeader.this, al);
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
						ReceiptHeader.this, searchResults);
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

	public void alertDialogUserSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				ReceiptHeader.this);
		final EditText editText = new EditText(ReceiptHeader.this);
		final ListView listview = new ListView(ReceiptHeader.this);
		LinearLayout layout = new LinearLayout(ReceiptHeader.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle(getResources().getString(R.string.user));
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(ReceiptHeader.this,
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
					String keyValues = (String) mapEntry.getKey();
					String name = (String) mapEntry.getValue();

					receipt_userCode.setText(name);
//					sl_namefield.setText(name);
					receipt_userCode.addTextChangedListener(new TextWatcher() {
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
							textlength = receipt_userCode.getText().length();
							sl_namefield.setText("");
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
						ReceiptHeader.this, searchResults);
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

	// private class AsyncCallWSSearchCustCode extends AsyncTask<Void, Void,
	// Void> {
	// @SuppressWarnings("deprecation")
	// @Override
	// protected void onPreExecute() {
	// spinnerLayout = new LinearLayout(ReceiptHeader.this);
	// spinnerLayout.setGravity(Gravity.CENTER);
	// ReceiptHeader.this.addContentView(spinnerLayout, new LayoutParams(
	// android.view.ViewGroup.LayoutParams.FILL_PARENT,
	// android.view.ViewGroup.LayoutParams.FILL_PARENT));
	// spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
	// enableViews(salesO_parent, false);
	// progressBar = new ProgressBar(ReceiptHeader.this);
	// progressBar.setProgress(android.R.attr.progressBarStyle);
	// progressBar.setIndeterminateDrawable(getResources().getDrawable(
	// drawable.greenprogress));
	// spinnerLayout.addView(progressBar);
	// }
	//
	// @Override
	// protected Void doInBackground(Void... arg0) {
	// searchCstCdArr = SalesOrderWebService.SearchReceipt(cuscode, sDate,
	// eDate, receipt_userCode.getText().toString(),"fncGetReceiptHeader");
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void result) {
	//
	// if (!searchCstCdArr.isEmpty()) {
	// try {
	// searchCustCode();
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// } else {
	// searchCstCdArr.clear();
	// try {
	// searchCustCode();
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// Toast.makeText(ReceiptHeader.this, "No matches found",
	// Toast.LENGTH_SHORT).show();
	// }
	//
	// progressBar.setVisibility(View.GONE);
	// spinnerLayout.setVisibility(View.GONE);
	// enableViews(salesO_parent, true);
	// }
	// }

	// public void searchCustCode() throws ParseException {
	//
	// receiptadapter = new HeaderAdapter(ReceiptHeader.this,
	// R.layout.invoice_list_item, null, searchCstCdArr);
	// so_lv.setAdapter(receiptadapter);
	//
	// if (SO.getTotalamount() > 0) {
	// totaloutstanding.setText(twoDecimalPoint(SO.getTotalamount()));
	// } else {
	// totaloutstanding.setText("" + SO.getTotalamount());
	// }
	// }

	private class AsyncCallWSReceiptHeader extends AsyncTask<Void, Void, Void> {

		String dialogStatus;
		String selectedCustomerCode = "", selectedFromDate = "", selectedToDate = "", selectedUser = "";
		@Override
		protected void onPreExecute() {
			list.clear();
			headeresult = "";
			dialogStatus = checkInternetStatus();

			selectedCustomerCode = edCustomerCode.getText().toString();
			selectedFromDate = starteditTextDate.getText().toString();
			selectedToDate = endeditTextDate.getText().toString();
			selectedUser = receipt_userCode.getText().toString();

			if(selectedFromDate.matches("")){
				selectedFromDate = serverdate;
			}

			if(selectedToDate.matches("")){
				selectedToDate = serverdate;
			}

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			double totalamount = 0.00;

			try {
				SO.setTotalamount(totalamount);
				String cmpnyCode = SupplierSetterGetter.getCompanyCode();

				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put("CustomerCode", selectedCustomerCode);
				hm.put("FromDate", selectedFromDate);
				hm.put("ToDate", selectedToDate);
				hm.put("User", selectedUser);

				Log.d("receiptpara",""+hm.toString());

				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");
							headeresult = offlineDatabase.getIReceiptHeader(hm);

						} else {
							Log.d("CheckOffline Alert -->", "False");
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						//	finish();
						}

					} else {
						Log.d("checkOffline Status -->", "False");
						headeresult = OnlineRecepitHeader(hm);
					}

				} else if (onlineMode.matches("False")) {
					Log.d("Customer Online", onlineMode);
					headeresult = offlineDatabase.getIReceiptHeader(hm);
				}

				if(headeresult!=null) {
                    JSONObject jsonResponse;

                    jsonResponse = new JSONObject(headeresult);
                    JSONArray jsonMainNode = jsonResponse
                            .optJSONArray("ReceiptHeader");

                    int lengthJsonArr = jsonMainNode.length();
                    list = new ArrayList<SO>();
                    for (int i = 0; i < lengthJsonArr; i++) {

                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                        String ccSno = jsonChildNode.optString("ReceiptNo")
                                .toString();
                        String ccDate = jsonChildNode.optString("ReceiptDate")
                                .toString();
                        String customerName = jsonChildNode.optString("CustomerName")
                                .toString();
                        String customerCode = jsonChildNode.optString(
                                "CustomerCode").toString();
                        String amount = jsonChildNode.optString("PaidAmount")
                                .toString();
                        String createDte =  jsonChildNode.optString("CreateDate")
								.toString();


                        SO so = new SO();
                        so.setSno(ccSno);
                        so.setCustomerCode(customerCode);
                        so.setCustomerName(customerName);
                        so.setNettotal(amount);
                        totalamount = totalamount + Double.valueOf(amount);
                        if(Company.getShortCode().matches("JUBI")){
							StringTokenizer tokens = new StringTokenizer(createDte,
									" ");
							String date = tokens.nextToken();
							String time = tokens.nextToken();
							so.setDate(date+" "+time);
						}else {
							if (ccDate != null) {
								StringTokenizer tokens = new StringTokenizer(ccDate,
										" ");
								String date = tokens.nextToken();
								so.setDate(date);
							} else {
								so.setDate(ccDate);
							}
						}
                        list.add(so);
                    }
                    SO.setTotalamount(totalamount);
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {

			//	starteditTextDate.setText("");  // added new
			//	endeditTextDate.setText("");
				
				if (!list.isEmpty()) {
					headerCustCode();
				} else {
					list.clear();
					headerCustCode();
					Toast.makeText(ReceiptHeader.this, "No Data found",
							Toast.LENGTH_SHORT).show();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);
		}
	}

	public void headerCustCode() throws ParseException {

		receiptadapter = new HeaderAdapter(ReceiptHeader.this,
				R.layout.invoice_list_item_latest, null, list);
		so_lv.setAdapter(receiptadapter);

		if (SO.getTotalamount() > 0) {
			totaloutstanding.setText(twoDecimalPoint(SO.getTotalamount()));
		} else {
			totaloutstanding.setText("" + SO.getTotalamount());
		}
	}

	public void clearSetterGetter() {
		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");
		SalesOrderSetGet.setSaleorderdate("");
		SalesOrderSetGet.setCurrencycode("");
		SalesOrderSetGet.setCurrencyname("");
		SalesOrderSetGet.setCurrencyrate("");
		SalesOrderSetGet.setRemarks("");
		SalesOrderSetGet.setSrinvoiceno("");
	}

	private class InvoiceProductWebservice extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			product.clear();
			productdet.clear();
			footerArr.clear();
			
		}

		@Override
		protected Void doInBackground(Void... arg0) {

				String str4 = listarray.get(0);
				Log.d("InvoiceNo", str4);
				String showcartonloose = SalesOrderSetGet.getCartonpriceflag();
				String cmpnyCode = SupplierSetterGetter.getCompanyCode();
				hm.put("CompanyCode", cmpnyCode);
				hm.put("InvoiceNo", str4);
				hashVl.put("CompanyCode", cmpnyCode);

				if (onlineMode.matches("True")) {
					if (checkOffline == true) {

						// Offline
//					jsonString = offlineDatabase.getInvoiceHeaderDetail(hm);

						if (showcartonloose.equalsIgnoreCase("1")) {
							jsonString = offlineDatabase
									.getInvoiceDetailPrint(hm);

						} else {
							jsonString = offlineDatabase
									.getInvoiceHeaderDetail(hm);
						}

						jsonStr = offlineDatabase.getInvoiceHeader(hm);
//					showcartonloose="";
					} else { // online
						/******
						 * Show Default carton,loose,foc,exchange qty and price
						 * Based On General settings
						 *******/
						if (showcartonloose.equalsIgnoreCase("1")) {
							jsonString = SalesOrderWebService.getSODetail(
									hm, "fncGetInvoiceDetailWithCarton");

						} else {
							jsonString = SalesOrderWebService.getSODetail(hm,
									"fncGetInvoiceDetail");
						}
						jsonStr = SalesOrderWebService.getSODetail(hm,
								"fncGetInvoiceHeaderByInvoiceNo");
						try {
							jsonSt = SalesOrderWebService.getSODetail(hashVl, "fncGetMobilePrintFooter");

							Log.d("jsonSt", "" + jsonSt);

							jsonRespfooter = new JSONObject(jsonSt);
							jsonSecNodefooter = jsonRespfooter.optJSONArray("SODetails");

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
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} else if (onlineMode.matches("False")) { // perman offline

//				jsonString = offlineDatabase.getInvoiceHeaderDetail(hm);

					if (showcartonloose.equalsIgnoreCase("1")) {
						jsonString = offlineDatabase
								.getInvoiceDetailPrint(hm);

					} else {
						jsonString = offlineDatabase
								.getInvoiceHeaderDetail(hm);
					}

					jsonStr = offlineDatabase.getInvoiceHeader(hm);
//				showcartonloose="";
				}

				// jsonString =
				// SalesOrderWebService.getSODetail(hm,"fncGetInvoiceDetail");
				// jsonStr = SalesOrderWebService.getSODetail(hm,
				// "fncGetInvoiceHeaderByInvoiceNo");

				Log.d("jsonString ", "" + jsonString);
				Log.d("jsonStr ", "" + jsonStr);


				try {

					jsonResponse = new JSONObject(jsonString);
					jsonMainNode = jsonResponse.optJSONArray("SODetails");

					jsonResp = new JSONObject(jsonStr);
					jsonSecNode = jsonResp.optJSONArray("SODetails");


				} catch (JSONException e) {

					e.printStackTrace();
				}

				String invNo = "";
				int lengJsonArr = jsonSecNode.length();
				for (int j = 0; j < lengJsonArr; j++) {

					JSONObject jsonChildNode;
					ProductDetails productdetail = new ProductDetails();
					try {
						jsonChildNode = jsonSecNode.getJSONObject(j);
						invNo = jsonChildNode.optString(
								"InvoiceNo").toString();
						productdetail.setItemno(invNo);
						String dateTime = jsonChildNode.optString("InvoiceDate")
								.toString();
						StringTokenizer tokens = new StringTokenizer(dateTime, " ");
						String invDate = tokens.nextToken();
						productdetail.setItemdate(invDate);
						productdetail.setItemdisc(jsonChildNode.optString(
								"ItemDiscount").toString());
						productdetail.setBilldisc(jsonChildNode.optString(
								"BillDIscount").toString());
						productdetail.setSubtotal(jsonChildNode.optString(
								"SubTotal").toString());
						productdetail.setTax(twoDecimalPoint(jsonChildNode
								.optDouble("Tax")));
						productdetail.setNettot(jsonChildNode.optString("NetTotal")
								.toString());
						productdetail.setPaidamount(jsonChildNode.optString(
								"PaidAmount").toString());
						productdetail.setCreditAmount(jsonChildNode.optString(
								"CreditAmount").toString());
						productdetail.setRemarks(jsonChildNode.optString("Remarks")
								.toString());
						productdetail.setTotaloutstanding(jsonChildNode.optString(
								"TotalBalance").toString());

					} catch (JSONException e) {

						e.printStackTrace();
					}

					/*********** Receipt Detail ************/
					int lengArr = rdjsonSecNode.length();
					for (int a = 0; a < lengArr; a++) {
						/****** Get Object for each JSON node. ***********/
						JSONObject jsonNode;
//                            ProductDetails proddet = new ProductDetails();
						try {
							jsonNode = rdjsonSecNode.getJSONObject(a);
							String recinvno = jsonNode.optString(
									"InvoiceNo").toString();

							if (invNo.matches(recinvno)) {
								productdetail.setPaidamount(jsonNode.optString(
										"PaidAmount").toString());
								productdetail.setCreditAmount(jsonNode.optString(
										"CreditAmount").toString());
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					/****** End *******/

					int lengthJsonArr = jsonMainNode.length();

					if (showcartonloose.equalsIgnoreCase("1")) {

						for (int k = 0; k < lengthJsonArr; k++) {
							/****** Get Object for each JSON node. ***********/
							ProdDetails proddetails = new ProdDetails();
							try {
								JSONObject jsonChildNodes = jsonMainNode
										.getJSONObject(k);
								String transType = jsonChildNodes.optString(
										"TranType").toString();
								int s = k + 1;
								proddetails.setSno(String.valueOf(s));

								proddetails.setItemnum(jsonChildNodes.optString(
										"InvoiceNo").toString());
								proddetails.setItemcode(jsonChildNodes.optString(
										"ProductCode").toString());

								proddetails.setDescription(jsonChildNodes
										.optString("ProductName").toString());

								proddetails.setUOMCode(jsonChildNodes
										.optString("UOMCode").toString());

								String uomCode = jsonChildNodes.optString("UOMCode").toString();
								Log.d("uomCode", "aaaaaaa " + uomCode);
								if (uomCode != null && !uomCode.isEmpty()) {

								} else {
									uomCode = "";
								}

								if (uomCode.matches("null")) {
									uomCode = "";
								}

								Log.d("uomCode", "u " + uomCode);

								if (transType.equalsIgnoreCase("Ctn")) {
									String cQty = jsonChildNodes.optString("CQty")
											.toString();

									String cPrice = jsonChildNodes.optString(
											"CartonPrice").toString();

									if (cQty != null && !cQty.isEmpty()
											&& cPrice != null && !cPrice.isEmpty()) {
										proddetails.setQty(cQty.split("\\.")[0]);
										proddetails.setPrice(twoDecimalPoint(Double.valueOf(cPrice)));
										double cqty = Double.valueOf(cQty);
										double cprice = Double.valueOf(cPrice);
										double total = cqty * cprice;
										String tot = twoDecimalPoint(total);
										proddetails.setTotal(tot);
									} else {
										proddetails.setQty("0");
										proddetails.setPrice("0.00");
										proddetails.setTotal("0.00");
									}

								} else if (transType.equalsIgnoreCase("Loose")) {

									String lQty = jsonChildNodes.optString("LQty")
											.toString();
									String lPrice = jsonChildNodes.optString(
											"Price").toString();

									if (lQty != null && !lQty.isEmpty()
											&& lPrice != null && !lPrice.isEmpty()) {
										proddetails.setQty(lQty.split("\\.")[0] + " " + uomCode);
										proddetails.setPrice(twoDecimalPoint(Double.valueOf(lPrice)));
										double lqty = Double.valueOf(lQty);
										double lprice = Double.valueOf(lPrice);
										double total = lqty * lprice;
										String tot = twoDecimalPoint(total);
										proddetails.setTotal(tot);
									} else {
										proddetails.setQty("0" + " " + uomCode);
										proddetails.setPrice("0.00");
										proddetails.setTotal("0.00");
									}

								} else if (transType.equalsIgnoreCase("FOC")) {
									String focQty = jsonChildNodes.optString(
											"FOCQty").toString();
									productdetail.setFocqty(jsonChildNodes
											.optDouble("FOCQty"));
									Log.d("FOCQtyValue", "-->" + jsonChildNodes.optDouble("FOCQty") + "(FOC)");
									if (focQty != null && !focQty.isEmpty()) {
										proddetails.setQty(focQty.split("\\.")[0] + "(FOC)");
									} else {
										proddetails.setQty("0" + " " + uomCode);
									}
									proddetails.setPrice("0.00");
									proddetails.setTotal("0.00");

								} else if (transType.equalsIgnoreCase("Exc")) {
									String excQty = jsonChildNodes.optString(
											"ExchangeQty").toString();
									if (excQty != null && !excQty.isEmpty()) {
										proddetails.setQty(excQty.split("\\.")[0]);
									} else {
										proddetails.setQty("0" + " " + uomCode);
									}
									proddetails.setPrice("0.00");
									proddetails.setTotal("0.00");

								}

								if (gnrlStngs.matches("C")) {
									proddetails.setSortproduct(jsonChildNodes
											.optString("CategoryCode").toString());
								} else if (gnrlStngs.matches("S")) {
									proddetails.setSortproduct(jsonChildNodes
											.optString("SubCategoryCode")
											.toString());
								} else if (gnrlStngs.matches("N")) {
									proddetails.setSortproduct("");
								} else {
									proddetails.setSortproduct("");
								}
								proddetails.setTax(jsonChildNodes.optString("Tax").toString());
								proddetails.setSubtotal(jsonChildNodes.optString("SubTotal").toString());
								proddetails.setTaxPerc(jsonChildNodes.optString("TaxPerc").toString());
								proddetails.setTaxType(jsonChildNodes.optString("TaxType").toString());
								product.add(proddetails);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}


					} else {

						for (int k = 0; k < lengthJsonArr; k++) {

							JSONObject jsonChildNodes;
							ProdDetails proddetails = new ProdDetails();
							try {
								jsonChildNodes = jsonMainNode.getJSONObject(k);
								int s = k + 1;
								proddetails.setSno(String.valueOf(s));
								proddetails.setItemnum(jsonChildNodes.optString(
										"InvoiceNo").toString());
								proddetails.setItemcode(jsonChildNodes.optString(
										"ProductCode").toString());
								proddetails.setDescription(jsonChildNodes.optString(
										"ProductName").toString());

								String recqty = jsonChildNodes.optString("Qty")
										.toString();
								if (recqty.contains(".")) {
									StringTokenizer tokens = new StringTokenizer(
											recqty, ".");
									String qty = tokens.nextToken();
									proddetails.setQty(qty);
								} else {
									proddetails.setQty(recqty);
								}

								proddetails.setPrice(jsonChildNodes.optString("Price")
										.toString());
								proddetails.setTotal(jsonChildNodes.optString("Total")
										.toString());

//						proddetails.setFocqty(2.00);

								proddetails.setFocqty(jsonChildNodes
										.optDouble("FOCQty"));

								proddetails.setExchangeqty(jsonChildNodes
										.optDouble("ExchangeQty"));

								if (gnrlStngs.matches("C")) {
									proddetails.setSortproduct(jsonChildNodes
											.optString("CategoryCode").toString());
								} else if (gnrlStngs.matches("S")) {
									proddetails.setSortproduct(jsonChildNodes
											.optString("SubCategoryCode").toString());
								} else if (gnrlStngs.matches("N")) {
									proddetails.setSortproduct("");
								} else {
									proddetails.setSortproduct("");
								}
								proddetails.setTax(jsonChildNodes.optString("Tax").toString());
								proddetails.setTaxType(jsonChildNodes.optString("TaxType").toString());
								proddetails.setSubtotal(jsonChildNodes.optString("SubTotal").toString());
								proddetails.setTaxPerc(jsonChildNodes.optString("TaxPerc").toString());
								proddetails.setTaxType(jsonChildNodes.optString("TaxType").toString());
								Log.d("jsonChildNodes", "-->" + jsonChildNodes.optString("TaxType").toString());
								product.add(proddetails);
							} catch (JSONException e) {

								e.printStackTrace();
							}
						}
					}
					productdetail.setProductsDetails(product);
					productdet.add(productdetail);
				}


				hashVl.clear();
				// }
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			hm.clear();
			if (printid == R.id.printer) {
				try {
					printid = 0;
					sortCatagory();
					print(sngleArray);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Log.d("receiptprintpreview", "receiptprintpreview");
				Log.d("product", "" + product);
				Log.d("productdet", "" + productdet);
				helper.dismissProgressDialog();
				Intent i = new Intent(ReceiptHeader.this,
						ReceiptPrintPreview.class);
				i.putExtra("no", sosno);
				i.putExtra("date", sodate);
				i.putExtra("customerCode", socustomercode);
				i.putExtra("customerName", socustomername);
				i.putExtra("Key", "Receipt");
				i.putExtra("sort", sortproduct);
				i.putExtra("gnrlStngs", gnrlStngs);
//				i.putExtra("arrayDetails",sngleArray);
				PreviewPojo.setReceiptproducts(product);
				PreviewPojo.setProductsDetails(productdet);
				startActivity(i);
			}

		}

	}

	private class AsyncPrintCall extends
			AsyncTask<Void, Void, ArrayList<String>> {

		@Override
		protected void onPreExecute() {
			listarray.clear();
			sngleArray.clear();

		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("ReceiptNo", sosno);
			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					// Offline
					jsonStr = offlineDatabase.getReceiptDetail(hashValue);
					hashValue.put("FromDate", "");
					hashValue.put("ToDate", "");
					hashValue.put("User", "");
					jsonString = offlineDatabase.getReceiptHeader(hashValue);
					OfflineSalesOrderWebService
							.getCustomerTaxOffline(socustomercode);

				} else { // online
					try {
					Log.d("socustomercode","getCustomerTax-->"+socustomercode);
//					SalesOrderWebService.getCustomerTax(socustomercode, "fncGetCustomer");
						HashMap<String, String> custhash = new HashMap<String, String>();
						custhash.put("CompanyCode", cmpnyCode);
						custhash.put("CustomerCode", socustomercode);
						custjsonStr = SalesOrderWebService.getSODetail(custhash,
								"fncGetCustomer");
					} catch (Exception e) {
						e.printStackTrace();
					}
					jsonStr = SalesOrderWebService.getSODetail(hashValue,
							"fncGetReceiptDetail");
					jsonString = SalesOrderWebService.getSODetail(hashValue,
							"fncGetReceiptHeaderByReceiptNo");
				}

			} else if (onlineMode.matches("False")) { // perman offline

				jsonStr = offlineDatabase.getReceiptDetail(hashValue);
				hashValue.put("FromDate", "");
				hashValue.put("ToDate", "");
				hashValue.put("User", "");
				jsonString = offlineDatabase.getReceiptHeader(hashValue);

				OfflineSalesOrderWebService
						.getCustomerTaxOffline(socustomercode);

			}
			

			// jsonStr = SalesOrderWebService.getSODetail(hashValue,
			// "fncGetReceiptDetail");
			// jsonString = SalesOrderWebService.getSODetail(hashValue,
			// "fncGetReceiptHeaderByReceiptNo");
			Log.d("jsonStr ", jsonStr);
			Log.d("custjsonStr ", "" + custjsonStr);
			try {

				jsonResp = new JSONObject(jsonStr);
				rdjsonSecNode = jsonResp.optJSONArray("SODetails");

			} catch (JSONException e) {

				e.printStackTrace();
			}
			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");

			} catch (JSONException e) {

				e.printStackTrace();
			}

			try{
				custjsonResp = new JSONObject(custjsonStr);
				custjsonMainNode = custjsonResp.optJSONArray("SODetails");
			} catch (Exception e){
				e.printStackTrace();
			}


			int lengJsonArr = rdjsonSecNode.length();
			if(lengJsonArr == 1) {
				for (int i = 0; i < lengJsonArr; i++) {

					JSONObject jsonChildNode;

					try {
						jsonChildNode = rdjsonSecNode.getJSONObject(i);

						jsonChildNode.optString("ReceiptNo").toString();
						String invoiceno = jsonChildNode.optString("InvoiceNo")
								.toString();
						jsonChildNode.optString("NetTotal").toString();
						jsonChildNode.optString("PaidAmount").toString();
						jsonChildNode.optString("CreditAmount").toString();

						listarray.add(invoiceno);
					} catch (JSONException e) {

						e.printStackTrace();
					}
				}
			}else{
				for (int i = 0; i < lengJsonArr; i++) {

					JSONObject jsonChildNode;

					try {
						jsonChildNode = rdjsonSecNode.getJSONObject(i);

						jsonChildNode.optString("ReceiptNo").toString();
						String invoiceno = jsonChildNode.optString("InvoiceNo")
								.toString();
						jsonChildNode.optString("NetTotal").toString();
						String invoiceDte = jsonChildNode.optString("InvoiceDate").toString();
						StringTokenizer tokens = new StringTokenizer(invoiceDte,
								" ");
						String date = tokens.nextToken();
						String paid = jsonChildNode.optString("PaidAmount").toString();
						jsonChildNode.optString("CreditAmount").toString();
						double pay_amt = Double.parseDouble(paid);
						calculate += pay_amt;

						Log.d("calculateVlue","-->"+calculate);

						Product product = new Product();
						String slno = String.valueOf(i+1);
						product.setNo(slno);
						product.setCode(invoiceno);
						product.setTranDate(date);
						product.setDescription(paid);
						sngleArray.add(product);

					} catch (JSONException e) {

						e.printStackTrace();
					}
				}
			}
			int lengJsonArray = jsonMainNode.length();
			for (int i = 0; i < lengJsonArray; i++) {

				JSONObject jsonChildNodes;

				try {
					jsonChildNodes = jsonMainNode.getJSONObject(i);

					String paymode = jsonChildNodes.optString("Paymode")
							.toString();
					String bankcode = jsonChildNodes.optString("BankCode")
							.toString();
					String chequeno = jsonChildNodes.optString("ChequeNo")
							.toString();
					String datetime = jsonChildNodes.optString("ChequeDate")
							.toString();
					String createDate = jsonChildNodes.optString("CreateDate")
							.toString();
//					String datetime ="10/10/2018";
					Log.d("datetime","-->"+datetime);

					if(createDate != null && !createDate.matches("")){
						if(Company.getShortCode().matches("JUBI")){
							StringTokenizer tokens = new StringTokenizer(createDate, " ");
							String chequedate = tokens.nextToken();
							String time = tokens.nextToken();
							In_Cash.setCheck_Date(chequedate+" "+time);
						}else{
							StringTokenizer tokens = new StringTokenizer(createDate, " ");
							String chequedate = tokens.nextToken();
							In_Cash.setCheck_Date(chequedate);
						}
						In_Cash.setPay_Mode(paymode);
						In_Cash.setBank_code(bankcode);
						// In_Cash.setBank_Name("");
						In_Cash.setCheck_No(chequeno);
					}else {
						In_Cash.setPay_Mode(paymode);
						In_Cash.setBank_code(bankcode);
						In_Cash.setCheck_No(chequeno);
					}



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

					String HaveTax = jsonChildNode.optString("HaveTax")
							.toString();

					String TaxType = jsonChildNode.optString("TaxType").toString();
//					ProductDetails.setTaxtype(TaxType);

//					String TaxValue = jsonChildNode.optString("TaxValue")
//							.toString();

					if (HaveTax.matches("True")) {
//						SalesOrderSetGet.setTaxValue(TaxValue);
						SalesOrderSetGet.setCompanytax(TaxType);
						SalesOrderSetGet.setCustomerTaxPerc(TaxValue);
					} else {
//						SalesOrderSetGet.setTaxValue("");
						SalesOrderSetGet.setCompanytax("Z");
						SalesOrderSetGet.setCustomerTaxPerc("0.00");
					}
					Log.d("CustomerTaxType", ""+TaxType);
					Log.d("CustomerTaxValue", ""+TaxValue);
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

			hashValue.clear();

			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			if(sngleArray.size() !=0){
				hm.clear();
				if (printid == R.id.printer) {
					try {
						printid = 0;
						sortCatagory();
						print(sngleArray);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					Log.d("receiptprintpreview", "receiptprintpreview"+sngleArray.size());
					Log.d("product", "" + product);
					Log.d("productdet", "" + productdet);
					helper.dismissProgressDialog();
					Intent i = new Intent(ReceiptHeader.this,
							ReceiptPrintPreview.class);
					i.putExtra("no", sosno);
					i.putExtra("date", sodate);
					i.putExtra("customerCode", socustomercode);
					i.putExtra("customerName", socustomername);
					i.putExtra("Key", "Receipt Multi");
					i.putExtra("sort", sortproduct);
					i.putExtra("gnrlStngs", gnrlStngs);
					PreviewPojo.setmAttributeDetails(sngleArray);
					PreviewPojo.setReceiptproducts(product);
					PreviewPojo.setProductsDetails(productdet);
					startActivity(i);

				}

			}else{
				InvoiceProductWebservice task = new InvoiceProductWebservice();
				task.execute();
			}


		}

	}

	private void print(ArrayList<Product> sngleArray) throws IOException {
		mSRHeaderDetailArr = new ArrayList<>();
		salesreturnArr = new ArrayList<HashMap<String, String>>();
		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		try {
			String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
			if (receiptadapter.getSelectedPosition() != -1) {
				SO so = receiptadapter.getItem(receiptadapter
						.getSelectedPosition());
				sosno = so.getSno().toString();
				sodate = so.getDate().toString();
				Log.d("sodate",sodate);
				Log.d("calculateVlue","-->"+calculate);
				socustomercode = so.getCustomerCode().toString();
				soamount = so.getNettotal().toString();
				for (HashMap<String, String> hashMap : al) {
					for (String key : hashMap.keySet()) {
						if (key.equals(socustomercode)) {
							System.out.println(hashMap.get(key));
							socustomername = hashMap.get(key);
						}
					}
				}
			}
			if(printertype.matches("Zebra iMZ320")) {
				helper.dismissProgressDialog();
				Printer printer = new Printer(ReceiptHeader.this, macaddress);

				if(sngleArray.size() !=0){
					Log.d("multiprint()","started!!");
					printer.printMultiReceipt(socustomercode,socustomername,sosno,sodate, sngleArray,1,true,calculate);
				}else{
					printer.printReceipt(socustomercode, socustomername, sosno,
							sodate, productdet, printsortHeader, gnrlStngs, 1, true, footerArr,salesreturnArr,mSRHeaderDetailArr);
				}


			}else if(printertype.matches("4 Inch Bluetooth")){
				//helper.updateProgressDialog(ReceiptHeader.this.getString(R.string.creating_file_for_printing));
				if (BluetoothAdapter.checkBluetoothAddress(macaddress))
				{
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device,true);


				}

				helper.dismissProgressDialog();
			} else if(printertype.matches("Zebra iMZ320 4 Inch")) {
				helper.dismissProgressDialog();
				PrinterZPL printer = new PrinterZPL(ReceiptHeader.this, macaddress);

				printer.printReceipt(socustomercode, socustomername, sosno,
						sodate, productdet, printsortHeader, gnrlStngs, 1, true, footerArr,salesreturnArr);
			}  else if(printertype.matches("3 Inch Bluetooth Generic")) {
				helper.dismissProgressDialog();
				final CubePrint print = new CubePrint(ReceiptHeader.this,macaddress);
				print.initGenericPrinter();
				print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
					@Override
					public void initCompleted() {
						print.printReceipt(socustomercode,
								socustomername, sosno, sodate,
								productdet, printsortHeader, gnrlStngs, 1, true, footerArr, 1,salesreturnArr);
						print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
							@Override
							public void onCompleted() {
								helper.showLongToast(R.string.printed_successfully);
							}
						});
					}
				});

			}
		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}
	}

	private class AsyncGeneralSettings extends AsyncTask<Void, Void, Void> {
		String dialogStatus;

		@Override
		protected void onPreExecute() {
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) {
						Log.d("CheckOffline Alert -->", "True");

						String result = offlineDatabase
								.getGeneralSettingsValue();
						jsonString = " { JsonArray: " + result + "}";
					} else {
						Log.d("CheckOffline Alert -->", "False");
						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					//	finish();
					}

				} else {
					Log.d("checkOffline Status -->", "False");
					jsonString = WebServiceClass
							.URLService("fncGetGeneralSettings");

				}

			} else if (onlineMode.matches("False")) {
				Log.d("Customer Online", onlineMode);
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
						// Offline
						String result = offlineDatabase.getSubCategory();
						jsonStr = " { JsonArray: " + result + "}";
					} else { // Online

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

				} catch (JSONException e) {

					e.printStackTrace();
				}

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
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d("sortproduct", "" + sortproduct);
		}
	}

	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tax = df.format(d);
		return tax;
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

	public void sortCatagory() {
		for (int i = 0; i < sortproduct.size(); i++) {
			String catagory = sortproduct.get(i).toString();
			for (ProdDetails products : product) {

				if (catagory.matches(products.getSortproduct())) {

					sort.add(catagory);
				}
			}
		}
		hs.addAll(sort);
		printsortHeader.clear();
		printsortHeader.addAll(hs);
	}

	private class AsyncCurrentDatePrintCall extends
			AsyncTask<Void, Void, ArrayList<String>> {

		String startdt="",enddt="",cust_code;
		
		public AsyncCurrentDatePrintCall(String startdt, String enddt, String custCode){
			this.startdt = startdt;
			this.enddt = enddt;
			this.cust_code = custCode;
		}
		
		@Override
		protected void onPreExecute() {
			receiptlist.clear();
			receiptinvoicelist.clear();
			footerArr.clear();
			helper.showProgressDialog(R.string.generating_receipt);
		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {
			// double cashamount=0.00,chequeamount=0.00;
			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			String recieptinvoicedetail = MobileSettingsSetterGetter.getPrintReceiptSummary_PrintInvoiceDetail();

			Log.d("recieptinvoicedetail",recieptinvoicedetail);
			Log.d("startdt","-->"+startdt+"enddt:"+enddt);

			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("CustomerCode", cust_code);
			hashValue.put("FromDate", startdt);
			hashValue.put("ToDate", enddt);
			hashValue.put("User", username);
			
			hashVl.put("CompanyCode", cmpnyCode);
			
			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					// Offline
					jsonString = offlineDatabase.getReceiptHeader(hashValue);

				} else { // online

					if(recieptinvoicedetail.equalsIgnoreCase("True")){
						jsonString = SalesOrderWebService.getSODetail(hashValue,
								"fncGetReceiptHeaderWithDetail");
					}else{
						jsonString = SalesOrderWebService.getSODetail(hashValue,
								"fncGetReceiptHeader");
					}

					jsonSt = SalesOrderWebService.getSODetail(hashVl,"fncGetMobilePrintFooter");
					
					try{
					Log.d("jsonSt", ""+jsonSt);
					
					jsonRespfooter = new JSONObject(jsonSt);
					jsonSecNodefooter = jsonRespfooter.optJSONArray("SODetails");
					
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
					
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			} else if (onlineMode.matches("False")) { // perman offline
				jsonString = offlineDatabase.getReceiptHeader(hashValue);

			}

			Log.d("jsonStr ", "" + jsonString);

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");

			} catch (JSONException e) {

				e.printStackTrace();
			}
			int lengJsonArray = jsonMainNode.length();
			for (int i = 0; i < lengJsonArray; i++) {
				Receipt receipt = new Receipt();
				JSONObject jsonChildNodes;

				try {
					jsonChildNodes = jsonMainNode.getJSONObject(i);

					String receiptno = jsonChildNodes.optString("ReceiptNo")
							.toString();
					String datetime = jsonChildNodes.optString("ReceiptDate")
							.toString();
					String customercode = jsonChildNodes.optString(
							"CustomerCode").toString();
					String customername = jsonChildNodes.optString(
							"CustomerName").toString();
					double paidamount = jsonChildNodes.optDouble("PaidAmount");
					double creditamount = jsonChildNodes
							.optDouble("CreditAmount");
					String paymode = jsonChildNodes.optString("Paymode")
							.toString();
					String bankcode = jsonChildNodes.optString("BankCode")
							.toString();
					String chequeno = jsonChildNodes.optString("ChequeNo")
							.toString();
					String chequedate = jsonChildNodes.optString("ChequeDate")
							.toString();

					StringTokenizer tokens = new StringTokenizer(datetime, " ");
					String receiptdate = tokens.nextToken();
					receipt.setReceiptno(receiptno);
					receipt.setReceiptdate(receiptdate);
					receipt.setCustomercode(customercode);
					receipt.setCustomername(customername);
					receipt.setPaidamount(paidamount);
					receipt.setPaymode(paymode);
					receipt.setCreditamount(creditamount);
					receipt.setBankcode(bankcode);
					receipt.setChequeno(chequeno);
					receipt.setChequedate(chequedate);
					receiptlist.add(receipt);

					if(recieptinvoicedetail.equalsIgnoreCase("True")) {
						Log.d("recieptinvoicedetail", "true");
						String Detail = jsonChildNodes.optString("Detail")
								.toString();

						JSONArray jsonArray = new JSONArray(Detail);
						int leng = jsonArray.length();
						for (int j = 0; j < leng; j++) {
							Receipt receiptinvoice = new Receipt();
							JSONObject jsonChild = jsonArray.getJSONObject(j);

							String InvoiceNo = jsonChild.optString("InvoiceNo")
									.toString();
							String PaidAmount = jsonChild.optString("PaidAmount")
									.toString();
							String CreditAmount = jsonChild.optString("CreditAmount")
									.toString();

							double creditamt=0,paidamt=0, paidamountfinal=0;
							if(CreditAmount!=null && !CreditAmount.isEmpty()){
								creditamt = Double.parseDouble(CreditAmount);
							}

							if(PaidAmount!=null && !PaidAmount.isEmpty()){
								paidamt = Double.parseDouble(PaidAmount);
							}

							paidamountfinal = paidamt + creditamt;

							receiptinvoice.setInvoiceno(InvoiceNo);
							receiptinvoice.setInv_paidamount(""+PaidAmount);
							receiptinvoice.setInv_creditamount(CreditAmount);
							receiptinvoice.setInv_receiptno(receiptno);
							receiptinvoice.setReceiptno(receiptno);
							Log.d("receipt invoiceno",InvoiceNo);
							receiptinvoicelist.add(receiptinvoice);

						}
					}else{
						Log.d("recieptinvoicedetail", "false");
//						receiptlist.add(receipt);
					}


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
			if (receiptlist.size() > 0) {
				try {
					currrentDatePrint();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				helper.dismissProgressDialog();
				Toast.makeText(ReceiptHeader.this, "No Receipt found",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	/*private void currrentDatePrint() throws IOException {

		helper.dismissProgressDialog();
		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		try {
			Printer printer = new Printer(ReceiptHeader.this, macaddress);

			printer.printCurrentDateReceipt(currentdate, receiptlist, footerArr);

		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}
	}*/

	private void currrentDatePrint() throws IOException {
		String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		try {
			if(printertype.matches("Zebra iMZ320")) {
				helper.dismissProgressDialog();
				Printer printer = new Printer(ReceiptHeader.this, macaddress);

				printer.printCurrentDateReceipt(currentdate, receiptlist, footerArr,receiptinvoicelist,edCustCode.getText().toString(),
						edCustoName.getText().toString());
			}else if(printertype.matches("4 Inch Bluetooth")){
				helper.updateProgressDialog(ReceiptHeader.this.getString(R.string.creating_file_for_printing));
				if (BluetoothAdapter.checkBluetoothAddress(macaddress))
				{
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device,true);
				}
				helper.dismissProgressDialog();
			}
			else if(printertype.matches("Zebra iMZ320 4 Inch")){
				try {
					helper.dismissProgressDialog();
					PrinterZPL printer = new PrinterZPL(ReceiptHeader.this, macaddress);

					printer.printCurrentDateReceipt(currentdate, receiptlist, footerArr);

				} catch (IllegalArgumentException e) {
					helper.showLongToast(R.string.error_configure_printer);
				}
			}else if(printertype.matches("3 Inch Bluetooth Generic")) {
				helper.dismissProgressDialog();
				final CubePrint print = new CubePrint(ReceiptHeader.this,macaddress);
				print.initGenericPrinter();
				print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
					@Override
					public void initCompleted() {
						print.printCurrentDateReceipt(currentdate, receiptlist, footerArr,receiptinvoicelist);
						print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
							@Override
							public void onCompleted() {
								helper.showLongToast(R.string.printed_successfully);
							}
						});
					}
				});

			}

			else{
				helper.dismissProgressDialog();


			}

		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}
	}

	// offline Start

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

	public ArrayList<HashMap<String, String>> getAllUserOffline() {
		String user_jsonString = "";
		ArrayList<HashMap<String, String>> userList = new ArrayList<HashMap<String, String>>();

		user_jsonString = OfflineDatabase.getAllUserList();

		try {
			jsonResponse = new JSONObject(user_jsonString);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("UserArray");
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				String username = jsonChildNode.optString("UserName")
						.toString();
				String UserGroupCode = jsonChildNode.optString("UserGroupCode")
						.toString();

				HashMap<String, String> customerhm = new HashMap<String, String>();
				customerhm.put(username, UserGroupCode);
				userList.add(customerhm);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userList;
	}

	private String OnlineRecepitHeader(HashMap<String, String> hm) {

		String result = "";
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		SoapObject request = new SoapObject(NAMESPACE, webMethName);

		PropertyInfo sDate = new PropertyInfo();
		PropertyInfo eDate = new PropertyInfo();
		PropertyInfo suser = new PropertyInfo();
		PropertyInfo sCustomer = new PropertyInfo();
		PropertyInfo companyCode = new PropertyInfo();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		sCustomer.setName("CustomerCode");
		sCustomer.setValue(hm.get("CustomerCode"));
		sCustomer.setType(String.class);
		request.addProperty(sCustomer);

		sDate.setName("FromDate");
		sDate.setValue(hm.get("FromDate"));
		sDate.setType(String.class);
		request.addProperty(sDate);

		eDate.setName("ToDate");
		eDate.setValue(hm.get("ToDate"));
		eDate.setType(String.class);
		request.addProperty(eDate);

		suser.setName("User");
		suser.setValue(hm.get("User"));
		suser.setType(String.class);
		request.addProperty(suser);

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
			result = " { ReceiptHeader : " + suppTxt + "}";

			Log.d("ReceiptHeader","rr "+result);
		} catch (Exception e) {
		}
		return result;
	}

	private String checkInternetStatus() {
		String internetStatus = "";
		String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			if(mobileHaveOfflineMode.matches("1")){
		checkOffline = OfflineCommon.isConnected(ReceiptHeader.this);
//		String internetStatus = "";
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
					offlineLayout
							.setBackgroundResource(drawable.temp_offline_pattern_bg);
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

	// End

	private void enableViews(View v, boolean enabled) {
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				enableViews(vg.getChildAt(i), enabled);
			}
		}
		v.setEnabled(enabled);
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
	protected void onDestroy(){
		super.onDestroy();
		if(GlobalData.mService!=null){
			GlobalData.mService.stop();
		}
	}
	public void reconnectDialog(String msg){
		final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		final Dialog dialog = new Dialog(ReceiptHeader.this);

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
							// startActivity(intent);
							Log.d("case","STATE_CONNECTED");
							print4Inch();
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
					reconnectDialog(msg.getData().getString("toast"));
					Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
							Toast.LENGTH_SHORT).show();
                   /* helper.dismissProgressDialog();
                    finish();
     *//*Intent i = new Intent(InvoiceCashCollection.this,
       InvoiceHeader.class);
     i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
     startActivity(i);*//*
                    if (header.matches("InvoiceHeader")) {
                        intent = new Intent(InvoiceCashCollection.this,
                                InvoiceHeader.class);

                    } else if (header.matches("CustomerHeader")) {
                        intent = new Intent(InvoiceCashCollection.this,
                                CustomerListActivity.class);

                    } else if (header.matches("RouteHeader")) {
                        intent = new Intent(InvoiceCashCollection.this,
                                RouteHeader.class);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    InvoiceCashCollection.this.finish();*/
					break;
			}

		}
	};
	public int noOfInvoice(){
		int noOfInvoice = 0;
		for(int i =0;i<listarray.size();i++){
			noOfInvoice = noOfInvoice+1;
		}
		return noOfInvoice;
	}

	public int noOfInvoices(){
		int noOfInvoice = 0;
		for(int i =0;i<sngleArray.size();i++){
			noOfInvoice = noOfInvoice+1;
		}
		return noOfInvoice;
	}
	/*public void print4Inch(){
		int noofInvoice = noOfInvoice();
		CubePrint mPrintCube = new CubePrint(ReceiptHeader.this,FWMSSettingsDatabase.getPrinterAddress());
		mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
			@Override
			public void onCompleted() {

				helper.showLongToast(R.string.printed_successfully);
			}
		});
		mPrintCube.printReceipt(socustomercode, socustomername, sosno,
				sodate, productdet, printsortHeader, gnrlStngs, 1, true, footerArr,noofInvoice);
	}*/

	public void print4Inch(){
		int noofInvoice = noOfInvoice();
		CubePrint mPrintCube = new CubePrint(ReceiptHeader.this,FWMSSettingsDatabase.getPrinterAddress());
		mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
			@Override
			public void onCompleted() {

				helper.showLongToast(R.string.printed_successfully);
			}
		});
		if(printType.equals("Receipt")){
			mPrintCube.printReceipt(socustomercode, socustomername, sosno,
					sodate, productdet, printsortHeader, gnrlStngs, 1, true, footerArr,noofInvoice,salesreturnArr);
		}else if(printType.equals("CurrentDateReceipt")){
			ArrayList<Receipt> empty = new ArrayList<>();
			mPrintCube.printCurrentDateReceipt(currentdate, receiptlist, footerArr,empty);
		}

	}

	@Override
	public void onBackPressed() {
		
		if(back_navigate.matches("dashboard")){
			ReceiptHeader.this.finish();
		}else{
			Intent i = new Intent(ReceiptHeader.this, LandingActivity.class);
			startActivity(i);
			ReceiptHeader.this.finish();
		}
			
	}

	private class AsycSearchRecipt extends AsyncTask<Void, Void, Void>{
		String dialogStatus;
		String selectedCustomerCode = "", selectedFromDate = "", selectedToDate = "", selectedUser = "";
		@Override
		protected void onPreExecute() {
			Log.d("SearchReceipt","-->"+"searchResult");
			list.clear();
			headeresult = "";
			dialogStatus = checkInternetStatus();

			selectedCustomerCode = edCustomerCode.getText().toString();
			selectedFromDate = starteditTextDate.getText().toString();
			selectedToDate = endeditTextDate.getText().toString();
			selectedUser = receipt_userCode.getText().toString();

//			if(selectedFromDate.matches("")){
//				selectedFromDate = serverdate;
//			}
//
//			if(selectedToDate.matches("")){
//				selectedToDate = serverdate;
//			}
		}

		@Override
		protected Void doInBackground(Void... voids) {
			double totalamount = 0.00;

			try {
				SO.setTotalamount(totalamount);
				String cmpnyCode = SupplierSetterGetter.getCompanyCode();

				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put("CustomerCode", selectedCustomerCode);
				hm.put("FromDate", selectedFromDate);
				hm.put("ToDate", selectedToDate);
				hm.put("User", selectedUser);

				Log.d("receiptpara",""+hm.toString());

				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");
							headeresult = offlineDatabase.getIReceiptHeader(hm);

						} else {
							Log.d("CheckOffline Alert -->", "False");
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
							//	finish();
						}

					} else {
						Log.d("checkOffline Status -->", "False");
						headeresult = OnlineRecepitHeader(hm);
					}

				} else if (onlineMode.matches("False")) {
					Log.d("Customer Online", onlineMode);
					headeresult = offlineDatabase.getIReceiptHeader(hm);
				}

				if(headeresult!=null) {
					JSONObject jsonResponse;

					jsonResponse = new JSONObject(headeresult);
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("ReceiptHeader");

					int lengthJsonArr = jsonMainNode.length();
					list = new ArrayList<SO>();
					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

						String ccSno = jsonChildNode.optString("ReceiptNo")
								.toString();
						String ccDate = jsonChildNode.optString("ReceiptDate")
								.toString();
						String customerName = jsonChildNode.optString("CustomerName")
								.toString();
						String customerCode = jsonChildNode.optString(
								"CustomerCode").toString();
						String amount = jsonChildNode.optString("PaidAmount")
								.toString();

						SO so = new SO();
						so.setSno(ccSno);
						so.setCustomerCode(customerCode);
						so.setCustomerName(customerName);
						so.setNettotal(amount);
						totalamount = totalamount + Double.valueOf(amount);
						if (ccDate != null) {
							StringTokenizer tokens = new StringTokenizer(ccDate,
									" ");
							String date = tokens.nextToken();
							so.setDate(date);
						} else {
							so.setDate(ccDate);
						}
						list.add(so);
					}
					SO.setTotalamount(totalamount);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			try {

				//	starteditTextDate.setText("");  // added new
				//	endeditTextDate.setText("");

				if (!list.isEmpty()) {
					headerCustCode();
				} else {
					list.clear();
					headerCustCode();
					Toast.makeText(ReceiptHeader.this, "No Data found",
							Toast.LENGTH_SHORT).show();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);
		}
	}

	private class AsyncGetProductStock extends AsyncTask<Void, Void, Void>{
		ArrayList<Double> list;
		@Override
		protected void onPreExecute() {
			list= new ArrayList<>();
			list.clear();

		}

		@Override
		protected Void doInBackground(Void... voids) {
			companyCode = SupplierSetterGetter.getCompanyCode();
			String locationCode = SalesOrderSetGet.getLocationcode();
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("CompanyCode", companyCode);
			hm.put("LocationCode", locationCode);

			product_stock_jsonString = WebServiceClass.parameterService(
					hm, "fncGetProductStockAvailabilityByLocation");

			try {
				product_stock_jsonResponse = new JSONObject(
                        product_stock_jsonString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			product_stock_jsonMainNode = product_stock_jsonResponse
					.optJSONArray("JsonArray");

			int lengthJsonArr = product_stock_jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode;

				try {
					jsonChildNode = product_stock_jsonMainNode.getJSONObject(i);



					String pro_Quantity = jsonChildNode.optString("HaveStock")
							.toString();
					String LocationCode = jsonChildNode.optString(
							"LocationCode").toString();
					qty = Double.parseDouble(pro_Quantity);
					Log.d("qtyCheck",""+qty);
					if(qty > 0){
						list.add(qty);
						Log.d("listCheck",""+list.size());
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}


			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {

			Log.d("qtyCheck",""+list.size());
			if(list.size() > 0){
				openAlert();
			}else{
				getAlert();
			}

		}
	}

	private void openAlert() {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(ReceiptHeader.this);
		builder1.setMessage("Closing Stock not completed!!");
		builder1.setCancelable(true);

		builder1.setPositiveButton(
				"OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert11 = builder1.create();
		alert11.show();
	}

	private class ShowLocations extends AsyncTask<String, String, String> {
		HashMap<String, String> hashValue = new HashMap<String, String>();

		@Override
		protected void onPreExecute() {
			locationList = new ArrayList<>();
			locationList.clear();
		}

		@Override
		protected String doInBackground(String... strings) {

			try {
				cmpnyCode = SupplierSetterGetter.getCompanyCode();
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
							String mainLocation = jsonObject.getString("isMainLocation");
							Log.d("locationCodeCheck", locationCode);
							LocationGetSet locationGetSet = new LocationGetSet();
							if (mainLocation.matches("True")) {
								Log.d("locationCodeMain",locationCode);
								LocationGetSet.setIsMainLocation(locationCode);
							}
							Log.d("setLocationChecks",LocationGetSet.getIsMainLocation());							locationGetSet.setLocatn(locationCode);
							locationList.add(locationGetSet);
							/*HashMap<String, String> locationhm = new HashMap<String, String>();
							locationhm.put(locationCode, locationName);
							locationArrHm.add(locationhm);*/
						}


					}
				}
			} catch (Exception e) {
				e.printStackTrace();

			}

			return null;
		}

		@Override
		protected void onPostExecute(String s) {

		}
	}
}