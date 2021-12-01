package com.winapp.sot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.NewProductWebService;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.model.Product;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineCustomerSync;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSetterGetter;
import com.winapp.offline.SoapAccessTask;
import com.winapp.offline.SoapAccessTask.CallbackInterface;
import com.winapp.printcube.printcube.BluetoothService;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.PrinterZPL;
import com.winapp.printer.UIHelper;
import com.winapp.sotdetails.CustomerListActivity;
import com.winapp.sotdetails.CustomerSetterGetter;

@SuppressLint("UseValueOf")
public class InvoiceSummary extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace,Constants, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
	private  ArrayList<HashMap<String, String>> salesreturnArr;
	Cursor cursor, billCursor;
	ImageView camera, signature, location, prodphoto, sosign, arrow,
			arrowUpDown;
	ImageButton save, back, barcode_edit, remarks_edit;
	EditText sm_total, sm_subTotal,sm_subTotal_inclusive, sm_netTotal, sm_billDisc, sm_tax,
			sm_total_new, sm_itemDisc, sm_location, sm_billDiscPercentage,
			sl_remarks, sl_additionalinfo, sm_totl_cqty, sm_totl_lqty,
			sm_totl_qty, getremarks;
	double total = 0, smTax = 0;
	Button stupButton, stdownButton, ok, cancel;
	LinearLayout sm_header_layout, slsummary_layout, noofprint_layout,
			sm_slno_layout, sm_code_layout, sm_name_layout, sm_cty_layout,
			sm_lqty_layout, sm_qty_layout, sm_foc_layout, sm_price_layout,
			sm_cprice_layout, sm_total_layout, sm_itemdisc_layout,
			sm_subtotal_layout, sm_bottom_layout, sm_signature_layout,
			sm_tax_layout, sm_nettotal_layout, sm_arrow_layout;
	String valid_url = "", summaryResult = "", ReceiptNo = "";
	double billDiscount = 0, taxAmt = 0;
	String subTot = "", totTax = "", netTot = "", tot = "", invoiceNumber = "";
	ProgressBar progressBar;
	LinearLayout spinnerLayout, remarks_layout, soadditionalinfo_layout;
	private static final int PICK_FROM_CAMERA = 1;
	public static final int SIGNATURE_ACTIVITY = 2;
	double itemDisc = 0, sbTtl = 0, netTotal = 0;

	private String provider;
	String beforeChange = "";
	double tota = 0;
	private ListView mListView;
	private Intent intent;
	private UIHelper helper;
	ArrayList<HashMap<String, String>> doDetailsArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> doHeaderArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> soHeaderArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> soDetailsArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> InvoiceBarcodeArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> InvDetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> InvHeadersArr = new ArrayList<HashMap<String, String>>();

	ArrayList<HashMap<String, String>> InvoiceHeaderArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> InvoiceDetailsArr = new ArrayList<HashMap<String, String>>();

	ArrayList<HashMap<String, String>> datadb = new ArrayList<HashMap<String, String>>();
	ArrayList<ProductDetails> productdet, productdetReceipt, product,
			product_batchArr, footerArr;
	private ArrayList<String> listarray = new ArrayList<String>();
	ArrayList<String> SOResultArr = new ArrayList<String>();
	ArrayList<ProdDetails> prdctdetails;
	String slNo = "", DoNo = "", SoNo = "", Inv_No = "", appType = "";
	CheckBox enableprint, cash_checkbox, mDeliveryCheckBox;
	HashMap<String, String> hm = new HashMap<String, String>();

	JSONObject jsonResponse, jsonResp, custjsonResp, batch_jsonResp,
			jsonRespfooter;
	JSONArray jsonMainNode, jsonSecNode, custjsonMainNode, batch_jsonSecNode,
			jsonSecNodefooter;
	int stuprange = 3, stdownrange = 1, stwght = 1, orientation;
	TextView stnumber, custname, mEmpty;
	ArrayList<String> sortproduct = new ArrayList<String>();
	List<String> sort = new ArrayList<String>();
	HashSet<String> hs = new HashSet<String>();
	List<String> printsortHeader = new ArrayList<String>();
	HashMap<String, String> hashValue, hashVl;
	String jsonString = "", totalbalance, jsonStr = null, jsonSt, gnrlStngs,
			batchjsonStr, username;
	double setLatitude, setLongitude, cQtyTtl = 0, lQtyTtl = 0, qtyTtl = 0,
			screenInches = 0;
	String signature_img, product_img, header, address1 = "", address2 = "",
			customer_outstanding = "";
	SOTDatabase sqldb;
	boolean arrowflag = true, bottomflag = true, refreshview = true;
	String addview = "true", getSignatureimage = "", getPhotoimage="", custjsonStr = "",
			soAdditionalInfo = "";
	ProductListAdapter adapter;
	TextView listing_screen, customer_screen, addProduct_screen,
			summary_screen;
	String Prodtotal, subTtl, prodTax, ProdNetTotal, cartonprice, credit_limit;
	private SlidingMenu menu;
	InvoiceBatchDialog invoiceBatchDialog;
	int nofcopies;
	// Offline
	LinearLayout offlineLayout;
	private OfflineDatabase offlineDatabase;
	boolean checkOffline;
	String onlineMode, offlineDialogStatus, tranblock_abovelimit = "",
			tranblocknegativestock = "";
	private OfflineCommon offlineCommon;
	private OfflineCustomerSync offlinecustomerSynch;
	ArrayList<HashMap<String, String>> invoicedeleteArr = new ArrayList<HashMap<String, String>>();
	
	// /edit invoice
	private AlertDialog.Builder builder;
	private String edit_ssno = "", slPrice = "", slUomCode = "",
			slCartonPerQty = "", taxType = "", minselling_price = "",
			taxValue = "", ss_Cqty, beforeLooseQty, priceflag = "",
			edit_prodcode = "", calCarton="";
	private EditText sl_codefield, sl_cartonQty, sl_namefield, sl_looseQty,
			sl_qty, sl_foc, sl_price, sl_itemDiscount, sl_uom, sl_total,
			sl_tax, sl_netTotal, sl_cartonPerQty, sl_cprice, sl_exchange,
			sl_stock,sl_return;
	private LinearLayout uomcperqty_ll, foc_header_layout, foc_layout,
			price_header_layout, header_portrait, header_landscape,
			customer_layout_black;
	private ImageView expand;
	private TextView price_txt, cust_name_black;
	private double tt, itmDisc = 0, netTtal = 0, taxAmount = 0;
	private TextWatcher cqtyTW, lqtyTW, qtyTW;
	// get location

	private boolean isGPSEnabled = false, isNetworkEnabled = false,emailIschecked=false;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	TransferBatchDialog transferBatchDialog;

	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private String lat, lon,printStr="";
	private LocationManager locationManager;
	private Location mLastLocation;
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetCustomer";
	private ArrayList<ProductDetails> mSRHeaderDetailArr;
	private String contextmenu_click="",mobileHaveOfflineMode="";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private String mCurrentgetPath="",mCurrentPhotoPath="";
	private View eulaLayout;
	int count;
	Handler handler;
	@SuppressLint("UseValueOf")
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		Mint.initAndStartSession(InvoiceSummary.this, "29088aa0");
		setContentView(R.layout.invoice_summary);
		GlobalData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		GlobalData.mService = new BluetoothService(this, mHandler);
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(false);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.summary_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Invoice Summary");
		back = (ImageButton) customNav.findViewById(R.id.back);
		barcode_edit = (ImageButton) customNav.findViewById(R.id.barcode_edit);
		save = (ImageButton) customNav.findViewById(R.id.save);
		remarks_edit = (ImageButton) customNav.findViewById(R.id.remarks_edit);
		remarks_edit.setVisibility(View.VISIBLE);

		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);

		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);
		back.setVisibility(View.INVISIBLE);

		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		// @Offline
		onlineMode = OfflineDatabase.getOnlineMode();
		offlineDatabase = new OfflineDatabase(InvoiceSummary.this);
		offlineCommon = new OfflineCommon(InvoiceSummary.this);
		checkOffline = OfflineCommon.isConnected(InvoiceSummary.this);
		OfflineDatabase.init(InvoiceSummary.this);
		new OfflineSalesOrderWebService(InvoiceSummary.this);
		offlinecustomerSynch = new OfflineCustomerSync(InvoiceSummary.this);
		offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int dens = dm.densityDpi;
		double wi = (double) width / (double) dens;
		double hi = (double) height / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		screenInches = Math.sqrt(x + y);

		Log.d("Display Inche", "" + screenInches);

		calCarton = LogOutSetGet.getCalcCarton();
		
		String editinvoice = ConvertToSetterGetter.getEdit_inv_no();
		tranblocknegativestock = SalesOrderSetGet.getTranblocknegativestock();

		if (editinvoice != null && !editinvoice.isEmpty()) {

		} else {
			invoicedeleteArr.clear();
		}

		cartonprice = SalesOrderSetGet.getCartonpriceflag();

		if (cartonprice.matches("null") || cartonprice.matches("")) {
			cartonprice = "0";
		}

		username = SupplierSetterGetter.getUsername();

		// cartonprice="1";

		remarks_layout = (LinearLayout) findViewById(R.id.remarks_layout);
		soadditionalinfo_layout = (LinearLayout) findViewById(R.id.soadditionalinfo_layout);
		sl_remarks = (EditText) findViewById(R.id.sl_remarks);
		sl_additionalinfo = (EditText) findViewById(R.id.sl_additionalinfo);
		sm_total = (EditText) findViewById(R.id.sm_total);
		sm_subTotal = (EditText) findViewById(R.id.sm_subTotal);
		sm_subTotal_inclusive = (EditText) findViewById(R.id.sm_subTotal_inclusive);
		sm_netTotal = (EditText) findViewById(R.id.sm_netTotal);
		sm_billDisc = (EditText) findViewById(R.id.sm_billDisc);
		sm_tax = (EditText) findViewById(R.id.sm_tax);
		sm_location = (EditText) findViewById(R.id.sm_location);
		sm_total_new = (EditText) findViewById(R.id.sm_total_new);
		sm_itemDisc = (EditText) findViewById(R.id.sm_itemDisc);
		camera = (ImageView) findViewById(R.id.sm_camera_iv);
		signature = (ImageView) findViewById(R.id.sm_sign_iv);
		location = (ImageView) findViewById(R.id.sm_loc_iv);
		prodphoto = (ImageView) findViewById(R.id.prod_photo);
		sosign = (ImageView) findViewById(R.id.sm_signature);
		mListView = (ListView) findViewById(android.R.id.list);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		custname = (TextView) findViewById(R.id.cust_name);
		cust_name_black = (TextView) findViewById(R.id.cust_name_black);
		listing_screen = (TextView) findViewById(R.id.listing_screen_tab);
		customer_screen = (TextView) findViewById(R.id.customer_screen_tab);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen_tab);
		summary_screen = (TextView) findViewById(R.id.sum_screen_tab);
		arrowUpDown = (ImageView) findViewById(R.id.arrow_image);
		sm_bottom_layout = (LinearLayout) findViewById(R.id.sm_bottom_layout);
		sm_signature_layout = (LinearLayout) findViewById(R.id.sm_signature_layout);
		arrow = (ImageView) findViewById(R.id.imageView1);
		sm_slno_layout = (LinearLayout) findViewById(R.id.sm_slno_layout);
		sm_code_layout = (LinearLayout) findViewById(R.id.sm_code_layout);
		sm_name_layout = (LinearLayout) findViewById(R.id.sm_name_layout);
		sm_cty_layout = (LinearLayout) findViewById(R.id.sm_cty_layout);
		sm_lqty_layout = (LinearLayout) findViewById(R.id.sm_lqty_layout);
		sm_qty_layout = (LinearLayout) findViewById(R.id.sm_qty_layout);
		sm_foc_layout = (LinearLayout) findViewById(R.id.sm_foc_layout);
		sm_price_layout = (LinearLayout) findViewById(R.id.sm_price_layout);
		sm_cprice_layout = (LinearLayout) findViewById(R.id.sm_cprice_layout);
		sm_total_layout = (LinearLayout) findViewById(R.id.sm_total_layout);
		sm_itemdisc_layout = (LinearLayout) findViewById(R.id.sm_itemdisc_layout);
		sm_subtotal_layout = (LinearLayout) findViewById(R.id.sm_subtotal_layout);
		sm_tax_layout = (LinearLayout) findViewById(R.id.sm_tax_layout);
		sm_nettotal_layout = (LinearLayout) findViewById(R.id.sm_nettotal_layout);
		sm_arrow_layout = (LinearLayout) findViewById(R.id.sm_arrow_layout);
		sm_header_layout = (LinearLayout) findViewById(R.id.sm_header_layout);
		slsummary_layout = (LinearLayout) findViewById(R.id.slsummary_layout);
		sm_billDiscPercentage = (EditText) findViewById(R.id.sm_billDiscPercentage);
		customer_layout_black = (LinearLayout) findViewById(R.id.customer_layout_black);
		// summary_screen.setBackgroundColor(Color.parseColor("#00AFF0"));

		customer_layout_black.setVisibility(View.VISIBLE);

		summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
		summary_screen.setBackgroundResource(drawable.tab_select);

		sm_totl_cqty = (EditText) findViewById(R.id.sm_totl_cqty);
		sm_totl_lqty = (EditText) findViewById(R.id.sm_totl_lqty);
		sm_totl_qty = (EditText) findViewById(R.id.sm_totl_qty);

		// /
		header_portrait = (LinearLayout) findViewById(R.id.linear_layout1);
		header_landscape = (LinearLayout) findViewById(R.id.linear_layout2);

		ab.setBackgroundDrawable(getResources().getDrawable(
				drawable.header_bg));

		header_portrait.setVisibility(View.GONE);
		header_landscape.setVisibility(View.VISIBLE);

		summary_screen.setBackgroundResource(drawable.tab_select_right);
		// /

		checkInternetStatus();

		prdctdetails = new ArrayList<ProdDetails>();
		product = new ArrayList<ProductDetails>();
		productdet = new ArrayList<ProductDetails>();
		footerArr = new ArrayList<ProductDetails>();
		productdetReceipt = new ArrayList<ProductDetails>();
		product_batchArr = new ArrayList<ProductDetails>();
		hashValue = new HashMap<String, String>();
		hashVl = new HashMap<String, String>();
		sqldb = new SOTDatabase(InvoiceSummary.this);
		FWMSSettingsDatabase.init(InvoiceSummary.this);

		invoiceBatchDialog = new InvoiceBatchDialog();
		transferBatchDialog = new TransferBatchDialog();
		valid_url = FWMSSettingsDatabase.getUrl();
		new SOTSummaryWebService(valid_url);
		new SalesOrderWebService(valid_url);
		helper = new UIHelper(InvoiceSummary.this);
		SOTDatabase.init(InvoiceSummary.this);
		appType = LogOutSetGet.getApplicationType();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		header = SalesOrderSetGet.getHeader_flag();

		doHeaderArr.clear();
		doDetailsArr.clear();
		soDetailsArr.clear();
		soHeaderArr.clear();
		InvoiceDetailsArr.clear();
		InvoiceHeaderArr.clear();
		InvoiceBarcodeArr.clear();

		
		tranblock_abovelimit = SalesOrderSetGet.getTranblockinvoiceabovelimit();
		// tranblock_abovelimit="1";

		if (tranblock_abovelimit != null && !tranblock_abovelimit.isEmpty()) {

		} else {
			tranblock_abovelimit = "";
		}

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		buildGoogleApiClient();


		if (!mGoogleApiClient.isConnected())
			mGoogleApiClient.connect();



		Bundle extras = getIntent().getExtras();

		if (extras != null) {

			SOTDatabase.deleteAllProduct();
			SOTDatabase.deleteBillDisc();

			refreshview = false;
			doDetailsArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("DODetails");
			soDetailsArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("SODetails");
			soHeaderArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("SOHeader");
			doHeaderArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("DOHeader");

			InvoiceDetailsArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("InvDetails");
			InvoiceHeaderArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("InvHeader");
			InvoiceBarcodeArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("InvBarcode");
			getSignatureimage = (String) getIntent().getSerializableExtra(
					"getSignatureimage");
			getPhotoimage = (String) getIntent().getSerializableExtra(
					"getPhotoimage");
			
		
			soAdditionalInfo = extras.getString("soAdditionalInfo");

			SOTDatabase.storeImage(1, getSignatureimage, getPhotoimage);

			Log.d("DOdetails", "" + doDetailsArr);
			Log.d("SOdetails", "" + soDetailsArr);
			Log.d("SOHeader", "" + soHeaderArr);
			Log.d("DOHeader", "" + doHeaderArr);
			Log.d("Inv Detail", "" + InvoiceDetailsArr);
			Log.d("Inv Header", "" + InvoiceHeaderArr);
			Log.d("Inv Barcode", "" + InvoiceBarcodeArr);
			int sl_no;
			if (InvoiceHeaderArr != null) {
				for (int i = 0; i < InvoiceHeaderArr.size(); i++) {

					Inv_No = InvoiceHeaderArr.get(i).get("Invoive_No");
					invoiceNumber = Inv_No;
					// ConvertToSetterGetter.setSoNo(Inv_No);
					ConvertToSetterGetter.setEdit_inv_no(invoiceNumber);

					// String SoDate = SalesOrderSetGet.getCurrentdate();

					String SoDate = InvoiceHeaderArr.get(i).get("Invoive_Date")
							.split("\\ ")[0];
					String LocationCode = InvoiceHeaderArr.get(i).get(
							"LocationCode");
					String CustomerCode = InvoiceHeaderArr.get(i).get(
							"CustomerCode");
					String Total = InvoiceHeaderArr.get(i).get("Total");
					String ItemDiscount = InvoiceHeaderArr.get(i).get(
							"ItemDiscount");
					String BillDIscount = InvoiceHeaderArr.get(i).get(
							"BillDIscount");
					String Remarks = InvoiceHeaderArr.get(i).get("Remarks");
					String CurrencyCode = InvoiceHeaderArr.get(i).get(
							"CurrencyCode");
					String CurrencyRate = InvoiceHeaderArr.get(i).get(
							"CurrencyRate");

					String disc_Percent =InvoiceHeaderArr.get(i).get(
							"BillDiscountPercentage");

					Log.d("DiscountPercentage2","-->"+disc_Percent);

					sm_billDisc.setText(BillDIscount);
					sm_billDiscPercentage.setText(disc_Percent);

					CustomerSetterGetter.setBillDiscount(BillDIscount);
					CustomerSetterGetter.setDiscountPercentage(disc_Percent);

					if (!Total.matches("")) {
						double tot = Double.parseDouble(Total);
						if (!ItemDiscount.matches("")) {
							double itmDisc = Double.parseDouble(ItemDiscount);

							double ttl = tot - itmDisc;
							String totl = twoDecimalPoint(ttl);
							sm_total.setText(totl);
						} else {
							sm_total.setText(Total);
						}
					}

					SalesOrderSetGet.setCustomername(SalesOrderSetGet
							.getCustname());
					SalesOrderSetGet.setSaleorderdate(SoDate);
					SalesOrderSetGet.setDeliverydate("");
					SalesOrderSetGet.setLocationcode(LocationCode);
					SalesOrderSetGet.setCustomercode(CustomerCode);
					SalesOrderSetGet.setRemarks(Remarks);
					SalesOrderSetGet.setCurrencycode(CurrencyCode);
					SalesOrderSetGet.setCurrencyrate(CurrencyRate);
					SalesOrderSetGet.setCurrencyname("");
					SalesOrderSetGet.setDoNo("");
					CustomerSetterGetter.setDiscountPercentage(disc_Percent);

				}
			} else if (soHeaderArr != null) {
				for (int i = 0; i < soHeaderArr.size(); i++) {

					SoNo = soHeaderArr.get(i).get("SoNo");

					ConvertToSetterGetter.setSoNo(SoNo);

					String SoDate = SalesOrderSetGet.getCurrentdate();

					String DeliveryDate = soHeaderArr.get(i)
							.get("DeliveryDate");
					String LocationCode = soHeaderArr.get(i)
							.get("LocationCode");
					String CustomerCode = soHeaderArr.get(i)
							.get("CustomerCode");
					String Total = soHeaderArr.get(i).get("Total");
					String ItemDiscount = soHeaderArr.get(i)
							.get("ItemDiscount");
					String BillDIscount = soHeaderArr.get(i)
							.get("BillDIscount");

					String Remarks = soHeaderArr.get(i).get("Remarks");

					String CurrencyCode = soHeaderArr.get(i)
							.get("CurrencyCode");
					String CurrencyRate = soHeaderArr.get(i)
							.get("CurrencyRate");
					String ModifyUser = soHeaderArr.get(i).get("ModifyUser");

					String DiscountPercentage = soHeaderArr.get(i).get("DiscountPercentage");

					Log.d("DiscountPercentage1","-->"+DiscountPercentage);

					CustomerSetterGetter.setDiscountPercentage(DiscountPercentage);
					username = ModifyUser;

					sm_billDisc.setText(BillDIscount);

					sm_billDiscPercentage.setText(DiscountPercentage);

					if (!Total.matches("")) {
						double tot = Double.parseDouble(Total);
						if (!ItemDiscount.matches("")) {
							double itmDisc = Double.parseDouble(ItemDiscount);

							double ttl = tot - itmDisc;
							String totl = twoDecimalPoint(ttl);
							sm_total.setText(totl);
						} else {
							sm_total.setText(Total);
						}
					}
					SalesOrderSetGet.setCustomername(SalesOrderSetGet
							.getCustname());
					SalesOrderSetGet.setSaleorderdate(SoDate);
					SalesOrderSetGet.setDeliverydate(DeliveryDate);
					SalesOrderSetGet.setLocationcode(LocationCode);
					SalesOrderSetGet.setCustomercode(CustomerCode);
					SalesOrderSetGet.setRemarks(Remarks);
					SalesOrderSetGet.setCurrencycode(CurrencyCode);
					SalesOrderSetGet.setCurrencyrate(CurrencyRate);
					SalesOrderSetGet.setCurrencyname("");
					SalesOrderSetGet.setDoNo("");
					SalesOrderSetGet.setSoRemarks(Remarks);
					SalesOrderSetGet.setSoAdditionalInfo(soAdditionalInfo);
				}

				String soRemarks = SalesOrderSetGet.getSoRemarks();
				if (soRemarks != null && !soRemarks.isEmpty()) {
					sl_remarks.setText(soRemarks);
					remarks_layout.setVisibility(View.VISIBLE);
				}

				String soaddinfo = SalesOrderSetGet.getSoAdditionalInfo();
				if (soaddinfo != null && !soaddinfo.isEmpty()) {
					sl_additionalinfo.setText(soaddinfo);
					soadditionalinfo_layout.setVisibility(View.VISIBLE);
				}

			} else if (doHeaderArr != null) {

				for (int i = 0; i < doHeaderArr.size(); i++) {

					DoNo = doHeaderArr.get(i).get("DoNo");

					Log.d("DOnoCheck",DoNo);

					ConvertToSetterGetter.setDoNo(DoNo);

					String SoDate = SalesOrderSetGet.getCurrentdate();
					
					String DoDate = doHeaderArr.get(i).get("DoDate");
					String LocationCode = doHeaderArr.get(i)
							.get("LocationCode");
					String CustomerCode = doHeaderArr.get(i)
							.get("CustomerCode");
					String Total = doHeaderArr.get(i).get("Total");
					String ItemDiscount = doHeaderArr.get(i)
							.get("ItemDiscount");
					String BillDIscount = doHeaderArr.get(i)
							.get("BillDIscount");

					String Remarks = doHeaderArr.get(i).get("Remarks");

					String CurrencyCode = doHeaderArr.get(i)
							.get("CurrencyCode");
					String CurrencyRate = doHeaderArr.get(i)
							.get("CurrencyRate");

					sm_billDisc.setText(BillDIscount);

					if (!Total.matches("")|| Total!=null) {
						double tot = Double.parseDouble(Total);
						if (!ItemDiscount.matches("") || ItemDiscount!=null) {
							double itmDisc = Double.parseDouble(ItemDiscount);

							double ttl = tot - itmDisc;
							String totl = twoDecimalPoint(ttl);
							sm_total.setText(totl);
						} else {
							sm_total.setText(Total);
						}
					}
					SalesOrderSetGet.setCustomername(SalesOrderSetGet
							.getCustname());
					SalesOrderSetGet.setSaleorderdate(SoDate);
					SalesOrderSetGet.setDeliverydate(DoDate);
					SalesOrderSetGet.setLocationcode(LocationCode);
					SalesOrderSetGet.setCustomercode(CustomerCode);
					SalesOrderSetGet.setRemarks(Remarks);
					SalesOrderSetGet.setCurrencycode(CurrencyCode);
					SalesOrderSetGet.setCurrencyrate(CurrencyRate);
					SalesOrderSetGet.setCurrencyname("");
					SalesOrderSetGet.setDoNo(DoNo);
				}

			}

			if (doDetailsArr != null) {
				for (int i = 0; i < doDetailsArr.size(); i++) {

					slNo = doDetailsArr.get(i).get("slNo");

					String ProductCode = doDetailsArr.get(i).get("ProductCode");
					String ProductName = doDetailsArr.get(i).get("ProductName");
					String CQty = doDetailsArr.get(i).get("CQty");
					String LQty = doDetailsArr.get(i).get("LQty");
					String Qty = doDetailsArr.get(i).get("Qty");
					String FOCQty = doDetailsArr.get(i).get("FOCQty");
					String PcsPerCarton = doDetailsArr.get(i).get(
							"PcsPerCarton");
					String RetailPrice = doDetailsArr.get(i).get("RetailPrice");
					String Price = doDetailsArr.get(i).get("Price");
					String Total = doDetailsArr.get(i).get("Total");
					String ItemDiscount = doDetailsArr.get(i).get(
							"ItemDiscount");

					String SubTotal = doDetailsArr.get(i).get("SubTotal");
					String Tax = doDetailsArr.get(i).get("Tax");
					String NetTotal = doDetailsArr.get(i).get("NetTotal");
					String TaxType = doDetailsArr.get(i).get("TaxType");
					String TaxPerc = doDetailsArr.get(i).get("TaxPerc");
					SalesOrderSetGet.setCompanytax(TaxType);
//					SalesOrderSetGet.setTaxValue(TaxPerc);
					String ExchangeQty = doDetailsArr.get(i).get("ExchangeQty");
					String CartonPrice = doDetailsArr.get(i).get("CartonPrice");
					String MinimumSellingPrice = doDetailsArr.get(i).get(
							"MinimumSellingPrice");
					String ItemRemarks = doDetailsArr.get(i).get("ItemRemarks");

					if (ItemRemarks.matches("null")) {
						ItemRemarks = "";
					}

					sl_no = i + 1;

					if (!Total.matches("")) {
						double tot = Double.parseDouble(Total);
						if (!ItemDiscount.matches("")) {
							double itmDisc = Double.parseDouble(ItemDiscount);

							double ttl = tot - itmDisc;
							String totl = twoDecimalPoint(ttl);
							SOTDatabase.storeBillDisc(sl_no, ProductCode, totl);
						} else {
							SOTDatabase.storeBillDisc(sl_no, ProductCode,
									SubTotal);
						}
					}

					Log.d("ProductCode", "" + ProductCode);
					Log.d("ProductName", "" + ProductName);
					Log.d("TaxType", "" + TaxType);
					Log.d("TaxPerc", "" + TaxPerc);

					Log.d("RetailPrice", "" + RetailPrice);

					int slno = Integer.parseInt(slNo);

					Double dcQty = new Double(CQty);
					Double dlQty = new Double(LQty);
					Double dfocQty = new Double(FOCQty);
					Double dqty = new Double(Qty);
					Double dpcsPerCarton = 0.0;
					if (!PcsPerCarton.matches("")) {
						dpcsPerCarton = new Double(PcsPerCarton);
					}

					int cQty = dcQty.intValue();
					int lQty = dlQty.intValue();
					int qty = dqty.intValue();
					int focQty = dfocQty.intValue();
					int pcsPerCarton = dpcsPerCarton.intValue();

					Log.d("C Qty", "" + cQty);
					Log.d("L Qty", "" + lQty);
					Log.d("Qty", "" + qty);
					Log.d("focQty", "" + focQty);
					Log.d("pcsPerCarton", "" + pcsPerCarton);

					double price = Double.parseDouble(Price);
					double itemDiscount = Double.parseDouble(ItemDiscount);
					double total = Double.parseDouble(Total);
					double tax = Double.parseDouble(Tax);

					Log.d("price", "" + price);
					Log.d("itemDiscount", "" + itemDiscount);
					Log.d("total", "" + total);
					Log.d("tax", "" + tax);

					SOTDatabase.storeProduct(slno, ProductCode, ProductName,
							cQty, lQty, qty, focQty, price, itemDiscount, "",
							pcsPerCarton, total, tax, NetTotal, TaxType,
							TaxPerc, RetailPrice, SubTotal, CartonPrice,
							ExchangeQty, MinimumSellingPrice, ItemRemarks, "",
							"","","","");
				}
			} else if (soDetailsArr != null) {
				for (int i = 0; i < soDetailsArr.size(); i++) {

					slNo = soDetailsArr.get(i).get("slNo");
					String ProductCode = soDetailsArr.get(i).get("ProductCode");
					String ProductName = soDetailsArr.get(i).get("ProductName");
					String CQty = soDetailsArr.get(i).get("CQty");
					String LQty = soDetailsArr.get(i).get("LQty");
					String Qty = soDetailsArr.get(i).get("Qty");
					String FOCQty = soDetailsArr.get(i).get("FOCQty");
					String PcsPerCarton = soDetailsArr.get(i).get(
							"PcsPerCarton");
					String RetailPrice = soDetailsArr.get(i).get("RetailPrice");
					String Price = soDetailsArr.get(i).get("Price");
					String Total = soDetailsArr.get(i).get("Total");
					String ItemDiscount = soDetailsArr.get(i).get(
							"ItemDiscount");
					String BillDiscount = soDetailsArr.get(i).get(
							"BillDiscount");
					String SubTotal = soDetailsArr.get(i).get("SubTotal");
					String Tax = soDetailsArr.get(i).get("Tax");
					String NetTotal = soDetailsArr.get(i).get("NetTotal");
					String TaxType = soDetailsArr.get(i).get("TaxType");
					String TaxPerc = soDetailsArr.get(i).get("TaxPerc");
					SalesOrderSetGet.setCompanytax(TaxType);
//					SalesOrderSetGet.setTaxValue(TaxPerc);
					String ExchangeQty = soDetailsArr.get(i).get("ExchangeQty");
					String CartonPrice = soDetailsArr.get(i).get("CartonPrice");
					String MinimumSellingPrice = soDetailsArr.get(i).get(
							"MinimumSellingPrice");

					String MinimumCartonSellingPrice = soDetailsArr.get(i).get(
							"MinimumCartonSellingPrice");

					String ItemRemarks = soDetailsArr.get(i).get("ItemRemarks");

					String InvoiceQty = soDetailsArr.get(i).get("InvoiceQty");
					String InvoiceFocQty = soDetailsArr.get(i).get(
							"InvoiceFocQty");
					String productStock = soDetailsArr.get(i).get("QtyOnHand");

					if (ItemRemarks.matches("null")) {
						ItemRemarks = "";
					}

					billDiscount = Double.parseDouble(BillDiscount);
					subTot = SubTotal;
					totTax = Tax;
					netTot = NetTotal;
					tot = Total;

					sl_no = i + 1;

					Log.d("ProductCode", "" + ProductCode);
					Log.d("ProductName", "" + ProductName);
					Log.d("TaxType", "" + TaxType);
					Log.d("TaxPerc", "" + TaxPerc);

					Log.d("RetailPrice", "" + RetailPrice);

					int slno = Integer.parseInt(slNo);

					Double dcQty = new Double(CQty);
					Double dlQty = new Double(LQty);
					Double dfocQty = new Double(FOCQty);
					Double dqty = new Double(Qty);
					Double invqty = new Double(InvoiceQty);
					Double invfocqty = new Double(InvoiceFocQty);
					Double dpcsPerCarton = 0.0;
					if (!PcsPerCarton.matches("")) {
						dpcsPerCarton = new Double(PcsPerCarton);
					}

					int cQty = dcQty.intValue();
					int lQty = dlQty.intValue();
					int qty = dqty.intValue();
					int focQty = dfocQty.intValue();

					int invoiceQty = invqty.intValue();
					int invoiceFocQty = invfocqty.intValue();

					int pcsPerCarton = dpcsPerCarton.intValue();

					Log.d("C Qty", "" + cQty);
					Log.d("L Qty", "" + lQty);
					Log.d("Qty", "" + qty);
					Log.d("focQty", "" + focQty);
					Log.d("pcsPerCarton", "" + pcsPerCarton);
					//
					qty = qty - invoiceQty;
					focQty = focQty - invoiceFocQty;

					String calCarton = LogOutSetGet.getCalcCarton();

					try {

						if (calCarton.matches("0")) {

						} else {
							cQty = (qty / pcsPerCarton);
							lQty = (qty % pcsPerCarton);
						}

					} catch (ArithmeticException e) {
						System.out.println("Err: Divided by Zero");
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (CartonPrice.matches("")) {
						CartonPrice = "0";
					}

					if (CartonPrice.matches("0.00")
							|| CartonPrice.matches("0.0")
							|| CartonPrice.matches("0")) {
						productTotal(qty, Price, ItemDiscount, TaxType, TaxPerc);
					} else {
						productTotalNew(cQty, lQty, CartonPrice, Price,
								ItemDiscount, TaxType, TaxPerc);
					}

					// End

					double price = Double.parseDouble(Price);
					double itemDiscount = Double.parseDouble(ItemDiscount);
					double total = Double.parseDouble(Prodtotal);
					double tax = Double.parseDouble(prodTax);

					Log.d("price", "" + price);
					Log.d("itemDiscount", "" + itemDiscount);
					Log.d("total", "" + total);
					Log.d("tax", "" + tax);

					if (!Total.matches("")) {
						double tot = Double.parseDouble(Total);
						if (!ItemDiscount.matches("")) {
							double itmDisc = Double.parseDouble(ItemDiscount);

							double ttl = tot - itmDisc;
							// String totl = twoDecimalPoint(ttl);
							SOTDatabase.storeBillDisc(sl_no, ProductCode,
									Prodtotal);
						} else {
							SOTDatabase.storeBillDisc(sl_no, ProductCode,
									subTtl);
						}
					}
					if (appType.matches("S")) {
						if (qty != 0) {
							SOTDatabase.storeProduct(slno, ProductCode,
									ProductName, cQty, lQty, qty, focQty,
									price, itemDiscount, "", pcsPerCarton,
									total, tax, ProdNetTotal, TaxType, TaxPerc,
									RetailPrice, subTtl, CartonPrice,
									ExchangeQty, MinimumSellingPrice,
									ItemRemarks, slNo, productStock,MinimumCartonSellingPrice,"","");
						}
					} else {
						if (invoiceQty == 0) {
							SOTDatabase.storeProduct(slno, ProductCode,
									ProductName, cQty, lQty, qty, focQty,
									price, itemDiscount, "", pcsPerCarton,
									total, tax, ProdNetTotal, TaxType, TaxPerc,
									RetailPrice, subTtl, CartonPrice,
									ExchangeQty, MinimumSellingPrice,
									ItemRemarks, slNo, productStock,MinimumCartonSellingPrice,"","");
						}
					}
				
					String cmpnyCode = SupplierSetterGetter.getCompanyCode();

					ArrayList<PropertyInfo> params = new ArrayList<PropertyInfo>();
					params.add(newPropertyInfo("CompanyCode", cmpnyCode));
					params.add(newPropertyInfo("ProductCode", ProductCode));

					String haveBatchOnStockOut = SalesOrderSetGet
							.getHaveBatchOnStockOut();

//					haveBatchOnStockOut="True";
					
					if (haveBatchOnStockOut != null
							&& !haveBatchOnStockOut.isEmpty()) {

						if (haveBatchOnStockOut.matches("True")) {
							Log.d("haveBatchOnStockOut", haveBatchOnStockOut);
							new SoapAccessTask(InvoiceSummary.this, valid_url,
									"fncGetProduct", params, new GetProduct(
											soDetailsArr.get(i).get("ProductCode"),slNo)).execute();
						}
					}
				
				}
			} else if (InvoiceDetailsArr != null) {
				for (int i = 0; i < InvoiceDetailsArr.size(); i++) {

					slNo = InvoiceDetailsArr.get(i).get("slNo");
					String ProductCode = InvoiceDetailsArr.get(i).get(
							"ProductCode");
					String ProductName = InvoiceDetailsArr.get(i).get(
							"ProductName");
					String CQty = InvoiceDetailsArr.get(i).get("CQty");
					String LQty = InvoiceDetailsArr.get(i).get("LQty");
					String Qty = InvoiceDetailsArr.get(i).get("Qty");
					String FOCQty = InvoiceDetailsArr.get(i).get("FOCQty");
					String PcsPerCarton = InvoiceDetailsArr.get(i).get(
							"PcsPerCarton");
					String RetailPrice = InvoiceDetailsArr.get(i).get(
							"RetailPrice");
					String Price = InvoiceDetailsArr.get(i).get("Price");

					String Total = InvoiceDetailsArr.get(i).get("Total");
					String ItemDiscount = InvoiceDetailsArr.get(i).get(
							"ItemDiscount");
					String BillDiscount = InvoiceDetailsArr.get(i).get(
							"BillDiscount");
					String SubTotal = InvoiceDetailsArr.get(i).get("SubTotal");
					String Tax = InvoiceDetailsArr.get(i).get("Tax");
					String NetTotal = InvoiceDetailsArr.get(i).get("NetTotal");
					String TaxType = InvoiceDetailsArr.get(i).get("TaxType");
					String TaxPerc = InvoiceDetailsArr.get(i).get("TaxPerc");

					SalesOrderSetGet.setCompanytax(TaxType);
//					SalesOrderSetGet.setTaxValue(TaxPerc);

					String ExchangeQty = InvoiceDetailsArr.get(i).get(
							"ExchangeQty");
					String CartonPrice = InvoiceDetailsArr.get(i).get(
							"CartonPrice");
					String ItemRemarks = InvoiceDetailsArr.get(i).get(
							"ItemRemarks");

					if (ItemRemarks != null && !ItemRemarks.isEmpty()) {

					} else {
						ItemRemarks = "";
					}

					if (ExchangeQty != null && !ExchangeQty.isEmpty()) {

					} else {
						ExchangeQty = "0";
					}

					billDiscount = Double.parseDouble(BillDiscount);

					subTot = SubTotal;
					totTax = Tax;
					netTot = NetTotal;
					tot = Total;

					sl_no = i + 1;

					if (!Total.matches("")) {
						double tot = Double.parseDouble(Total);
						if (!ItemDiscount.matches("")) {
							double itmDisc = Double.parseDouble(ItemDiscount);

							double ttl = tot - itmDisc;
							String totl = twoDecimalPoint(ttl);
							SOTDatabase.storeBillDisc(sl_no, ProductCode, totl);
						} else {
							SOTDatabase.storeBillDisc(sl_no, ProductCode,
									SubTotal);
						}
					}

					Log.d("ProductCode", "" + ProductCode);
					Log.d("ProductName", "" + ProductName);
					Log.d("TaxType", "" + TaxType);
					Log.d("TaxPerc", "" + TaxPerc);
					Log.d("RetailPrice", "" + RetailPrice);
					Log.d("editinv billDiscount", "bb " + billDiscount);

					/*double sbtot = SOTDatabase.getsumsubTot();
					Log.d("editinv sbtot", "bb " + sbtot);
					if(billDiscount>0){
						billDiscount = billDiscount / sbtot;
					}else{
						billDiscount = 0;
					}

					taxCalc();*/

					int slno = Integer.parseInt(slNo);

					Double dcQty = new Double(CQty);
					Double dlQty = new Double(LQty);
					Double dfocQty = new Double(FOCQty);
					Double dqty = new Double(Qty);
					Double dpcsPerCarton = 0.0;
					if (!PcsPerCarton.matches("")) {
						dpcsPerCarton = new Double(PcsPerCarton);
					}

					int cQty = dcQty.intValue();
					int lQty = dlQty.intValue();
					// int qty = dqty.intValue();
					int focQty = dfocQty.intValue();
					int pcsPerCarton = dpcsPerCarton.intValue();

					Log.d("C Qty", "" + cQty);
					Log.d("L Qty", "" + lQty);
					// Log.d("Qty", "" + qty);
					Log.d("focQty", "" + focQty);
					Log.d("pcsPerCarton", "" + pcsPerCarton);

					double price = Double.parseDouble(Price);
					double itemDiscount = Double.parseDouble(ItemDiscount);
					double total = Double.parseDouble(Total);
					double tax = Double.parseDouble(Tax);

					Log.d("price", "" + price);
					Log.d("itemDiscount", "" + itemDiscount);
					Log.d("total", "" + total);
					Log.d("tax", "" + tax);

					SOTDatabase.storeProductForEditInvoice(slno, ProductCode,
							ProductName, cQty, lQty, dqty, focQty, price,
							itemDiscount, "", pcsPerCarton, total, tax,
							NetTotal, TaxType, TaxPerc, RetailPrice, SubTotal,
							CartonPrice, ExchangeQty, ItemRemarks, "");

					String cmpnyCode = SupplierSetterGetter.getCompanyCode();

					ArrayList<PropertyInfo> params = new ArrayList<PropertyInfo>();
					params.add(newPropertyInfo("CompanyCode", cmpnyCode));
					params.add(newPropertyInfo("ProductCode", ProductCode));

					String haveBatchOnStockOut = SalesOrderSetGet
							.getHaveBatchOnStockOut();

//					haveBatchOnStockOut="True";

					if (haveBatchOnStockOut != null
							&& !haveBatchOnStockOut.isEmpty()) {

						if (haveBatchOnStockOut.matches("True")) {
							Log.d("haveBatchOnStockOut", haveBatchOnStockOut);
							new SoapAccessTask(InvoiceSummary.this, valid_url,
									"fncGetProduct", params, new GetProduct(
											InvoiceDetailsArr.get(i).get("ProductCode"),slNo)).execute();
						}
					}
				}
			}
			if (InvoiceBarcodeArr != null) {
				for (int i = 0; i < InvoiceBarcodeArr.size(); i++) {

					HashMap<String, String> queryValues = new HashMap<String, String>();
					String slNo = InvoiceBarcodeArr.get(i).get("slNo");
					String SeqNo = InvoiceBarcodeArr.get(i).get("SeqNo");
					String ProductName = InvoiceBarcodeArr.get(i).get(
							"ProductName");
					String Weight = InvoiceBarcodeArr.get(i).get("Weight");
					String ProductCode = InvoiceBarcodeArr.get(i).get(
							"ProductCode");
					String WeightBarcode = InvoiceBarcodeArr.get(i).get(
							"WeightBarcode");

					Log.d("slNo", "" + slNo);
					Log.d("ProductCode", "" + ProductCode);
					Log.d("ProductName", "" + ProductName);
					Log.d("SeqNo", "" + SeqNo);
					Log.d("WeightBarcode", "" + WeightBarcode);
					Log.d("Weight", "" + Weight);

					queryValues.put("paletteId", slNo);
					queryValues.put("code", ProductCode);
					queryValues.put("name", ProductName);
					queryValues.put("barcode", WeightBarcode);
					queryValues.put("weight", Weight);
					queryValues.put("snum", SeqNo);
					queryValues.put("productId", slNo);
					sqldb.insertproduct(queryValues);

					SOTDatabase.updateBarcodeStatus(slNo, ProductCode, 1);
				}
			}

			double sbtot = SOTDatabase.getsumsubTot();
			double billDiscCalc=0;
			String billdi = sm_billDisc.getText().toString();
			if (!billdi.matches("")) {
				billDiscCalc = Double.parseDouble(billdi);
			}
			if(billDiscCalc>0){
				billDiscount = billDiscCalc / sbtot;
			}else{
				billDiscount = 0;
			}

		}

		String customercode = SalesOrderSetGet.getCustomercode();
		if(customercode!=null && !customercode.isEmpty()){
			getOnlineCustomer(customercode);
		}

		String remarks = SalesOrderSetGet.getSoRemarks();
		if (!remarks.matches("")) {
			sl_remarks.setText(remarks);
			remarks_layout.setVisibility(View.VISIBLE);
		}

		String soaddinfo = SalesOrderSetGet.getSoAdditionalInfo();
		if (soaddinfo != null && !soaddinfo.isEmpty()) {
			sl_additionalinfo.setText(soaddinfo);
			soadditionalinfo_layout.setVisibility(View.VISIBLE);
		}

		signature_img = SOTDatabase.getSignatureImage();
		product_img = SOTDatabase.getProductImage();

		datadb = sqldb.getAllProducts();
		sm_code_layout.setVisibility(View.GONE);
		sm_foc_layout.setVisibility(View.GONE);
		sm_subtotal_layout.setVisibility(View.GONE);
		sm_itemdisc_layout.setVisibility(View.GONE);
		sm_tax_layout.setVisibility(View.GONE);
		sm_nettotal_layout.setVisibility(View.GONE);

		if ((SalesOrderSetGet.getCartonpriceflag() != null && !SalesOrderSetGet.getCartonpriceflag().isEmpty())) {
			if (SalesOrderSetGet.getCartonpriceflag().matches("1")) {
				sm_cty_layout.setVisibility(View.VISIBLE);
				sm_lqty_layout.setVisibility(View.VISIBLE);
				sm_qty_layout.setVisibility(View.GONE);
				Log.d("C ---->", "1");
			} else if (SalesOrderSetGet.getCartonpriceflag().matches(
					"0")) {
				sm_cty_layout.setVisibility(View.GONE);
				sm_lqty_layout.setVisibility(View.GONE);
				sm_qty_layout.setVisibility(View.VISIBLE);

				Log.d("L ---->", "0");
			}
		}

		arrowUpDown.setImageResource(drawable.arrow_up);
		sm_bottom_layout.setVisibility(View.GONE);
		sm_signature_layout.setVisibility(View.VISIBLE);

		if (product_img != null && !product_img.isEmpty()) {
			Log.d("invoice sum photo if ", product_img);
			try {

				mCurrentgetPath = Product.getPath();

//    }
				byte[] encodePhotoByte = Base64.decode(product_img, Base64.DEFAULT);
				Bitmap photo = BitmapFactory.decodeByteArray(encodePhotoByte, 0,
						encodePhotoByte.length);
				//photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 100, stream);

//
				prodphoto.setImageBitmap(photo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

		}

		if (signature_img != null && !signature_img.isEmpty()) {
			Log.d("invoice sum sign if ", signature_img);
			try {
				byte[] encodeByte = Base64.decode(signature_img, Base64.DEFAULT);

				// String s;
				// try {
				// s = new String(encodeByte1, "UTF-8");
				// encodeByte = Base64.decode(s, Base64.DEFAULT);
				//
				//
				// } catch (Exception e) {
				// e.printStackTrace();
				// encodeByte = encodeByte1;
				// }

				Bitmap sign = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
				sign = Bitmap.createScaledBitmap(sign, 240, 80, true);
				sosign.setImageBitmap(sign);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {

		}

		cursor = SOTDatabase.getCursor();
		String COLUMN_QUANTITY="";
		sm_billDisc.setRawInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);

		if (cursor != null && cursor.getCount() > 0) {

			sm_header_layout.setVisibility(View.VISIBLE);
			save.setVisibility(View.VISIBLE);
			sm_billDisc.setEnabled(true);
			sm_billDiscPercentage.setEnabled(true);
			tota = SOTDatabase.getTotal();

			String smtotal = twoDecimalPoint(tota);
			double tot_item_disc = SOTDatabase.getTotalItemDisc();
			String tot_itemDisc = twoDecimalPoint(tot_item_disc);
			sm_itemDisc.setText(tot_itemDisc);
			sm_total_new.setText("" + smtotal);

			smTax = SOTDatabase.getTax();
			String ProdTax = fourDecimalPoint(smTax);
			sm_tax.setText("" + ProdTax);

			sbTtl = SOTDatabase.getSubTotal();
			String sub = twoDecimalPoint(sbTtl);
			sm_subTotal.setText("" + sub);

			cQtyTtl = SOTDatabase.getTotalCqty();
			int tot_cqty = (int) cQtyTtl;
			sm_totl_cqty.setText("" + tot_cqty);

			lQtyTtl = SOTDatabase.getTotalLqty();
			int tot_lqty = (int) lQtyTtl;
			sm_totl_lqty.setText("" + tot_lqty);

			qtyTtl = SOTDatabase.getTotalQty();
			int tot_qty = (int) qtyTtl;
			sm_totl_qty.setText("" + qtyTtl);

			if (extras == null) {
				sm_total.setText("" + sub);
			}

			String taxType = SalesOrderSetGet.getCompanytax();

			if (taxType != null && !taxType.isEmpty()) {
				if (taxType.matches("I") || taxType.matches("Z")) {
					netTotal = sbTtl;
				} else {
					netTotal = sbTtl + smTax;
				}
			} else {
				netTotal = sbTtl + smTax;
			}

			String ProdNettotal = twoDecimalPoint(netTotal);
			sm_netTotal.setText("" + ProdNettotal);

			// Added New 03.04.2017
			if (taxType != null && !taxType.isEmpty()) {
				if (taxType.matches("I")) {
					sm_subTotal.setVisibility(View.GONE);
					sm_subTotal_inclusive.setVisibility(View.VISIBLE);
					double subt = netTotal - smTax;
					String subto = twoDecimalPoint(subt);
					sm_subTotal_inclusive.setText("" + subto);
				}else{
					sm_subTotal.setVisibility(View.VISIBLE);
					sm_subTotal_inclusive.setVisibility(View.GONE);
				}
			}

			mEmpty.setVisibility(View.GONE);

			if (!datadb.isEmpty()) {

				if (barcode_edit.getVisibility() == View.GONE) {
					barcode_edit.setVisibility(View.VISIBLE);
				}
			}
			

		} else {
			mEmpty.setVisibility(View.VISIBLE);
			sm_header_layout.setVisibility(View.GONE);
			save.setVisibility(View.INVISIBLE);
			sm_billDisc.setFocusable(false);
			sm_billDiscPercentage.setFocusable(false);

			if (datadb.isEmpty()) {
				if (barcode_edit.getVisibility() == View.VISIBLE) {
					barcode_edit.setVisibility(View.GONE);
				}

			}
		}
		cursor =SOTDatabase.getCursor();
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				COLUMN_QUANTITY = cursor.getString(cursor
						.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
			} while (cursor.moveToNext());
		}
		Log.d("COLUMNVALIDVALUE","-->"+COLUMN_QUANTITY);

		// setListAdapter(new ProductListAdapter(this, cursor));
		adapter = new ProductListAdapter(this, cursor);
		mListView.setAdapter(adapter);
		registerForContextMenu(mListView);

		taxCalc(); // recalculate tax value
		smTax = SOTDatabase.getTax();
		String ProdTax = fourDecimalPoint(smTax);
		sm_tax.setText("" + ProdTax);

		//code repeated for bill discount recalculation
		sbTtl = SOTDatabase.getSubTotal();
		String sub = twoDecimalPoint(sbTtl);
		sm_subTotal.setText("" + sub);

		location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// if (!gps.canGetLocation()) {
				// gps.showSettingsAlert();
				// }
			}
		});

		arrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (arrowflag) {

					arrowflag = false;
					arrow.setImageResource(drawable.ic_arrow_left);

//					sm_code_layout.setVisibility(View.VISIBLE);
					sm_cty_layout.setVisibility(View.VISIBLE);
					sm_lqty_layout.setVisibility(View.VISIBLE);
					sm_foc_layout.setVisibility(View.VISIBLE);
					sm_subtotal_layout.setVisibility(View.VISIBLE);
					sm_itemdisc_layout.setVisibility(View.VISIBLE);
					sm_tax_layout.setVisibility(View.VISIBLE);
					sm_nettotal_layout.setVisibility(View.VISIBLE);
					sm_qty_layout.setVisibility(View.VISIBLE);
					adapter.selectAll(false);
				} else if (arrowflag == false) {
					arrow.setImageResource(drawable.ic_arrow_right);
					arrowflag = true;

					sm_code_layout.setVisibility(View.GONE);
					// sm_cty_layout.setVisibility(View.GONE);
					// sm_lqty_layout.setVisibility(View.GONE);
					sm_foc_layout.setVisibility(View.GONE);
					sm_subtotal_layout.setVisibility(View.GONE);
					sm_itemdisc_layout.setVisibility(View.GONE);
					sm_tax_layout.setVisibility(View.GONE);
					sm_nettotal_layout.setVisibility(View.GONE);

					if ((SalesOrderSetGet.getCartonpriceflag() != null && !SalesOrderSetGet
							.getCartonpriceflag().isEmpty())) {
						if (SalesOrderSetGet.getCartonpriceflag()
								.matches("1")) {

							sm_cty_layout.setVisibility(View.VISIBLE);
							sm_lqty_layout.setVisibility(View.VISIBLE);
							sm_qty_layout.setVisibility(View.GONE);
							Log.d("C ---->", "1");
						} else if (SalesOrderSetGet
								.getCartonpriceflag().matches("0")) {
							sm_cty_layout.setVisibility(View.GONE);
							sm_lqty_layout.setVisibility(View.GONE);

							Log.d("L ---->", "0");
						}
					}

					adapter.selectAll(true);

				}
			}
		});
		/*arrowUpDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (bottomflag) {
					bottomflag = false;
					arrowUpDown.setImageResource(drawable.arrow_up);
					sm_bottom_layout.setVisibility(View.GONE);
					sm_signature_layout.setVisibility(View.VISIBLE);
				} else if (bottomflag == false) {
					bottomflag = true;
					arrowUpDown.setImageResource(drawable.arrow_down);
					sm_bottom_layout.setVisibility(View.VISIBLE);
					sm_signature_layout.setVisibility(View.VISIBLE);
				}
			}
		});*/

		//created on 28/07/147 by saravana
		arrowUpDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (bottomflag) {
					bottomflag = false;
					arrowUpDown.setImageResource(drawable.arrow_down);
					sm_bottom_layout.setVisibility(View.VISIBLE);
				} else {
					bottomflag = true;
					arrowUpDown.setImageResource(drawable.arrow_up);
					sm_bottom_layout.setVisibility(View.GONE);
				}
			}
		});

		sm_billDisc.addTextChangedListener(new TextWatcher() {

			int sl_no =1;

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				beforeChange = sm_billDisc.getText().toString();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				try {

					String billDisc = sm_billDisc.getText().toString();

					Log.d("sm_billDisc","-->"+billDisc);

					if(sm_billDisc.length() == 0){
						billDisc = "0";
//						billDisc =CustomerSetterGetter.getDiscountPercentage();
//						Log.d("sm_billDisc1","-->"+billDisc);
					}
					
					
					/*if (sm_billDisc.length() == 0) {

						billDiscount = 0;
						taxCalc();

						String ProdTax = fourDecimalPoint(smTax);
						sm_tax.setText("" + ProdTax);

						String sub = twoDecimalPoint(sbTtl);
						sm_subTotal.setText("" + sub);

						double netTotal = 0;

						String taxType = SalesOrderSetGet.getCompanytax();
						if (taxType != null && !taxType.isEmpty()) {
							if (taxType.matches("I") || taxType.matches("Z")) {
								netTotal = sbTtl;
							} else {
								netTotal = sbTtl + smTax;
							}
						} else {
							netTotal = sbTtl + smTax;
						}

						String ProdNettotal = twoDecimalPoint(netTotal);
						sm_netTotal.setText("" + ProdNettotal);
					}*/

					if (!billDisc.matches("")) {

						double billDiscCalc = Double.parseDouble(billDisc);
						double sbtot = SOTDatabase.getsumsubTot();
						double sbTotal = SOTDatabase.getsubTotal(sl_no);
						
						Log.d("billDiscCalc","-->"+billDiscCalc+"-->"+sbtot +"sbTotal"+sbTotal);

						if(billDiscCalc>0){
//							billDiscount = sbtot / billDiscCalc;
							billDiscount =billDiscCalc/sbtot;
							Log.d("billDiscount","-->"+billDiscount);
						}else{
							billDiscount = 0;
						}
						
						taxCalc();
						String txAt = fourDecimalPoint(taxAmt);
						
						if(sm_tax.getText().toString().matches("") || sm_tax.getText().toString().matches("0") || 
						sm_tax.getText().toString().matches("0.0") || sm_tax.getText().toString().matches("0.00") ||
						sm_tax.getText().toString().matches("0.000") || sm_tax.getText().toString().matches("0.0000")){
							sm_tax.setText("0.00");
						}else{

							sm_tax.setText("" + txAt);
						}
						

						String ta = sm_tax.getText().toString();
						double sbt = SOTDatabase.getSubTotal();

						if (!ta.matches("")) {
							double taxAt = Double.parseDouble(ta);

							double net = 0;
							String taxType = SalesOrderSetGet.getCompanytax();
							if (taxType != null && !taxType.isEmpty()) {
								if (taxType.matches("I")
										|| taxType.matches("Z")) {
									net = sbt;
								} else {
									net = taxAt + sbt;
								}
							} else {
								net = taxAt + sbt;
							}

							String netTo = twoDecimalPoint(net);
							String subTo = twoDecimalPoint(sbt);

							sm_netTotal.setText(netTo);
							sm_subTotal.setText(subTo);

							// Added New 03.04.2017
							if (taxType != null && !taxType.isEmpty()) {
								if (taxType.matches("I")) {
									sm_subTotal.setVisibility(View.GONE);
									sm_subTotal_inclusive.setVisibility(View.VISIBLE);
									double subt = net - taxAt;
									String subto = twoDecimalPoint(subt);
									sm_subTotal_inclusive.setText("" + subto);
								}else{
									sm_subTotal.setVisibility(View.VISIBLE);
									sm_subTotal_inclusive.setVisibility(View.GONE);
								}
							}

						}

					} else {

					}
				} catch (Exception e) {

				}
			}

		});

		sm_billDiscPercentage.addTextChangedListener(new TextWatcher() {

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

				try {

					double tota = SOTDatabase.getTotal();					
					double tot_item_disc = SOTDatabase.getTotalItemDisc();
					
					double subT = tota - tot_item_disc;
					
					Log.d("billdiscperc subT", ""+subT);


//					CustomerSetterGetter
					
					String sub = sm_subTotal.getText().toString();
					String discPer = sm_billDiscPercentage.getText().toString();

					String perrcent =sm_billDisc.getText().toString();

					Log.d("sm_billDiscPercentage","-->"+discPer +"--> "+perrcent+"-->"+sub);


					if (sm_billDiscPercentage.length() == 0) {



						sm_billDisc.setText("");

						billDiscount = 0;
						taxCalc();

						String ProdTax = fourDecimalPoint(smTax);
						sm_tax.setText("" + ProdTax);

						String subTotal = twoDecimalPoint(sbTtl);
						sm_subTotal.setText("" + subTotal);

						double netTotal = 0;

						String taxType = SalesOrderSetGet.getCompanytax();
						if (taxType != null && !taxType.isEmpty()) {
							if (taxType.matches("I") || taxType.matches("Z")) {
								netTotal = sbTtl;
							} else {
								netTotal = sbTtl + smTax;
							}
						} else {
							netTotal = sbTtl + smTax;
						}

						String ProdNettotal = twoDecimalPoint(netTotal);
						sm_netTotal.setText("" + ProdNettotal);

						// Added New 03.04.2017
						if (taxType != null && !taxType.isEmpty()) {
							if (taxType.matches("I")) {
								sm_subTotal.setVisibility(View.GONE);
								sm_subTotal_inclusive.setVisibility(View.VISIBLE);
								double subt = netTotal - smTax;
								String subto = twoDecimalPoint(subt);
								sm_subTotal_inclusive.setText("" + subto);
							}else{
								sm_subTotal.setVisibility(View.VISIBLE);
								sm_subTotal_inclusive.setVisibility(View.GONE);
							}
						}
					}

					if (!sub.matches("") && !discPer.matches("")) {
//						double subTotal = Double.parseDouble(sub);
						double discPercent = Double.parseDouble(discPer);
						double Disc = subT * (discPercent / 100);
						String billDisc = twoDecimalPoint(Disc);
						sm_billDisc.setText("" + billDisc);
					}
				} catch (Exception e) {

				}
			}

		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String appType = LogOutSetGet.getApplicationType();

				if (appType.matches("W")) {

					Log.d("AppType--w->", "" + appType);
					// if ((header.matches("InvoiceHeader")) ||
					// (header.matches("SalesOrderHeader")) ||
					// (header.matches("CustomerHeader")))
					// {
					Intent i = new Intent(InvoiceSummary.this, AddBarcode.class);
					i.putExtra("SOT_ssid", ((TextView) v
							.findViewById(R.id.ss_slid)).getText().toString());
					i.putExtra("SOT_ssproductcode", ((TextView) v
							.findViewById(R.id.ss_prodcode)).getText()
							.toString());
					i.putExtra("SOT_str_ssprodname", ((TextView) v
							.findViewById(R.id.ss_name)).getText().toString());
					i.putExtra("SOT_str_ssno", ((TextView) v
							.findViewById(R.id.ss_slno)).getText().toString());
					i.putExtra("SOT_str_c_qty", ((TextView) v
							.findViewById(R.id.ss_c_qty)).getText().toString());

					i.putExtra("SOT_str_itemremarks", ((TextView) v
							.findViewById(R.id.ss_item_remarks)).getText()
							.toString());
					i.putExtra("Barcodefrom", "InvoiceSummary");
					startActivity(i);
					InvoiceSummary.this.finish();
				} else {
					Log.d("AppType--s->", "" + appType);
				}
				// }
			}
		});
		barcode_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(InvoiceSummary.this,
						AddBarcodeSummary.class);
				i.putExtra("Barcodefrom", "InvoiceSummary");
				startActivity(i);
				InvoiceSummary.this.finish();
			}
		});
		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CameraAction(PICK_FROM_CAMERA);
			}
		});

		signature.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent i = new Intent(InvoiceSummary.this,
						CaptureSignature.class);
				startActivityForResult(i, SIGNATURE_ACTIVITY);

			}
		});
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				save.setVisibility(View.GONE);
				saveAlertDialog();

			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sm_billDisc.setText("");
				sm_billDiscPercentage.setText("");
				Intent i = new Intent(InvoiceSummary.this,
						InvoiceAddProduct.class);
				startActivity(i);
				InvoiceSummary.this.finish();
			}
		});

		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Cursor cursor = SOTDatabase.getCursor();
				int count = cursor.getCount();
				if (count > 0) {
					alertDialog();
				} else {

					Intent i = new Intent(InvoiceSummary.this,
							InvoiceHeader.class);
					startActivity(i);
					InvoiceSummary.this.finish();
				}

			}
		});

		customer_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(InvoiceSummary.this,
						InvoiceCustomer.class);
				startActivity(i);
				InvoiceSummary.this.finish();

			}
		});

		addProduct_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(InvoiceSummary.this,
						InvoiceAddProduct.class);
				startActivity(i);
				InvoiceSummary.this.finish();

			}
		});

		remarks_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				remarksDialog();
			}
		});

		String custCode = SalesOrderSetGet.getCustomercode();
		String custName = SalesOrderSetGet.getCustomername();
		custname.setText(custName + " (" + custCode + ")");
		cust_name_black.setText(custName + " (" + custCode + ")");

		String billdescPerc = strValidate(CustomerSetterGetter.getDiscountPercentage());
		String billdesc = strValidate(CustomerSetterGetter.getBillDiscount());

		Log.d("billdescPercentage","-->"+billdescPerc+"-->"+billdesc);

		if(!billdesc.matches("")){
			double dbilldisc = Double.parseDouble(billdesc);
			if(dbilldisc>0){
				sm_billDisc.setText(billdesc);
			}
		}else if(!billdescPerc.matches("")){
			double dbilldiscperc = Double.parseDouble(billdescPerc);
			if(dbilldiscperc>0){
				sm_billDiscPercentage.setText(billdescPerc);
			}
		}

	}

	public void getOnlineCustomer(String custcode) {
		String result="",custresult="";

		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("CompanyCode", cmpnyCode);
		hm.put("CustomerCode", custcode);

		for (Map.Entry<String, String> entry : hm.entrySet()) {

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
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			result = response.toString();
			custresult = " { SaleOCustomer : " + result + "}";
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
					String TaxCode = jsonChildNode.optString("TaxCode")
							.toString();
					String mTaxType= jsonChildNode.optString("TaxType")
							.toString();

					String mTaxPerc= jsonChildNode.optString("TaxValue")
							.toString();
					Log.d("TaxCode", "" + TaxCode);

					String Email= jsonChildNode.optString("Email")
							.toString();
					CustomerSetterGetter.setCustomerEmail(Email);

					if (TaxCode != null
							&& !TaxCode.isEmpty()) {
						SalesOrderSetGet.setTaxCode(TaxCode);
						SalesOrderSetGet.setTaxType(mTaxType);
						SalesOrderSetGet.setTaxPerc(mTaxPerc);
					}

				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			result = "Error occured";
		}
	}

	@SuppressWarnings("deprecation")
	public void taxCalc() {

		taxAmt = 0;
		int sl_no = 1;
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					double net_tot = 0;
					double subTot = 0, tx = 0, txVl = 0, tax = 0, net = 0;

					String total = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TOTAL));

					String taxValue = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));


//					String taxValue = SalesOrderSetGet.getTaxValue();

					billCursor = SOTDatabase.getBillCursor();

					if (billCursor != null && billCursor.getCount() > 0) {

						double sbTotal = SOTDatabase.getsubTotal(sl_no);
						Log.d("sbTotal","-->"+sbTotal+"");
						// subTot = sbTotal - billDiscount;
						double billDisc = sbTotal * billDiscount;
						Log.d("taxcalcBillDiscount", "" + billDiscount+"billDisc"+billDisc);
						subTot = sbTotal - billDisc;

					}

					String taxType = SalesOrderSetGet.getCompanytax();
					if (taxType != null && !taxType.isEmpty()) {
						if (taxType.matches("I")) {
							if (!taxValue.matches("")) {
								txVl = Double.parseDouble(taxValue);

								if (!total.matches("")) {
									double tt = Double.parseDouble(total);
									tx = (tt * txVl) / (100 + txVl);
								}

							}
						} else if(taxType.matches("Z")){
							tx = 0 ;
						} else {
							if (!taxValue.matches("")) {
								txVl = Double.parseDouble(taxValue);
								tx = (subTot * txVl) / 100;
							}
						}
					} else {
						tx = 0 ;
					}
					Log.d("subTot", "" + subTot);
					Log.d("taxValue", "" + taxValue);
					Log.d("tax amount", "" + tx);
					Log.d("taxType", "" + taxType);

					String updTx = fourDecimalPoint(tx);

					if (!updTx.matches("")) {
						tax = Double.parseDouble(updTx);

						if (!total.matches("")) {

							if (taxType != null && !taxType.isEmpty()) {
								if (taxType.matches("I")
										|| taxType.matches("Z")) {
									net_tot = subTot;
								} else {
									net_tot = subTot + tax;
								}
							} else {
								net_tot = subTot + tax;
							}

							String ntTtl = twoDecimalPoint(net_tot);

							net = Double.parseDouble(ntTtl);
//							String subtotal = twoDecimalPoint(subTot);
							String subtotal = fourDecimalPoint(subTot);
							SOTDatabase.updateSummary(tax,
									Double.parseDouble(subtotal), net, sl_no);
						}
					}

					sl_no++;
					taxAmt = taxAmt + tx;

				} while (cursor.moveToNext());
				cursor.requery();
			}
		}
	}

	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}

	public String fourDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setMinimumFractionDigits(4);
		String tot = df.format(d);

		return tot;
	}

	private class ProductListAdapter extends ResourceCursorAdapter {
		String COLUMN_QUANTITYS="";
		@SuppressWarnings("deprecation")
		public ProductListAdapter(Context context, Cursor cursor) {
			super(context, R.layout.invoice_summary_list_item, cursor);


		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			LinearLayout ss_listitem_layout = (LinearLayout) view
					.findViewById(R.id.ss_listitem_layout);
			LinearLayout ss_prodcode_layout = (LinearLayout) view
					.findViewById(R.id.ss_prodcode_layout);
			LinearLayout ss_cqty_layout = (LinearLayout) view
					.findViewById(R.id.ss_cqty_layout);
			LinearLayout ss_lqty_layout = (LinearLayout) view
					.findViewById(R.id.ss_lqty_layout);
			LinearLayout ss_foc_layout = (LinearLayout) view
					.findViewById(R.id.ss_foc_layout);
			LinearLayout ss_total_layout = (LinearLayout) view
					.findViewById(R.id.ss_total_layout);
			LinearLayout ss_itemdisc_layout = (LinearLayout) view
					.findViewById(R.id.ss_itemdisc_layout);
			LinearLayout ss_tax_layout = (LinearLayout) view
					.findViewById(R.id.ss_tax_layout);
			LinearLayout ss_netTotal_layout = (LinearLayout) view
					.findViewById(R.id.ss_netTotal_layout);
			LinearLayout ss_qty_layout = (LinearLayout) view
					.findViewById(R.id.ss_qty_layout);

			LinearLayout ss_cprice_layout = (LinearLayout) view
					.findViewById(R.id.ss_cprice_layout);
			LinearLayout ss_subTotal_layout = (LinearLayout) view
					.findViewById(R.id.ss_subTotal_layout);
			

			if (addview == "false") {

				ss_prodcode_layout.setVisibility(View.VISIBLE);
				ss_cqty_layout.setVisibility(View.VISIBLE);
				ss_lqty_layout.setVisibility(View.VISIBLE);
				ss_foc_layout.setVisibility(View.VISIBLE);
				ss_total_layout.setVisibility(View.VISIBLE);
				ss_subTotal_layout.setVisibility(View.VISIBLE);
				ss_itemdisc_layout.setVisibility(View.VISIBLE);
				ss_tax_layout.setVisibility(View.VISIBLE);
				ss_netTotal_layout.setVisibility(View.VISIBLE);
				ss_qty_layout.setVisibility(View.VISIBLE);

				if (cartonprice.matches("1")) {
					ss_cprice_layout.setVisibility(View.VISIBLE);
					sm_cprice_layout.setVisibility(View.VISIBLE);
				}
				ss_listitem_layout.getLayoutParams().width = 1850;
			} else {
				
				// Get Device Width
				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			    Display display = wm.getDefaultDisplay();
			    DisplayMetrics metrics = new DisplayMetrics();
			    display.getMetrics(metrics);
			    int width = metrics.widthPixels;
			    int height = metrics.heightPixels;
//			    Log.d("width", ""+width);
				
				if (screenInches > 7) {
					orientation = getResources().getConfiguration().orientation;
					if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
						// Landscape
						ss_listitem_layout.getLayoutParams().width = width;
					} else {
						// portrait
						ss_listitem_layout.getLayoutParams().width = width;
					}
				} else {
					// mobile portrait
					ss_listitem_layout.getLayoutParams().width = width;
				}
				ss_prodcode_layout.setVisibility(View.GONE);
				// ss_cqty_layout.setVisibility(View.GONE);
				// ss_lqty_layout.setVisibility(View.GONE);
				ss_foc_layout.setVisibility(View.GONE);
				ss_total_layout.setVisibility(View.VISIBLE);
				ss_subTotal_layout.setVisibility(View.GONE);
				ss_itemdisc_layout.setVisibility(View.GONE);
				ss_tax_layout.setVisibility(View.GONE);
				ss_netTotal_layout.setVisibility(View.GONE);

				if (cartonprice.matches("1")) {
					ss_cprice_layout.setVisibility(View.VISIBLE);
					sm_cprice_layout.setVisibility(View.VISIBLE);
				}

				if ((SalesOrderSetGet.getCartonpriceflag() != null && !SalesOrderSetGet
						.getCartonpriceflag().isEmpty())) {
					if (SalesOrderSetGet.getCartonpriceflag().matches(
							"1")) {

						ss_cqty_layout.setVisibility(View.VISIBLE);
						ss_lqty_layout.setVisibility(View.VISIBLE);
						ss_qty_layout.setVisibility(View.GONE);
						if (cartonprice.matches("1")) {
							ss_cprice_layout.setVisibility(View.VISIBLE);
							sm_cprice_layout.setVisibility(View.VISIBLE);
						}
//						Log.d("C ---->", "C");
					} else if (SalesOrderSetGet.getCartonpriceflag()
							.matches("0")) {
						ss_cqty_layout.setVisibility(View.GONE);
						ss_lqty_layout.setVisibility(View.GONE);
						if (cartonprice.matches("1")) {
							ss_cprice_layout.setVisibility(View.GONE);
							sm_cprice_layout.setVisibility(View.GONE);
						}

//						Log.d("L ---->", "L");
					}
				}
			}

			COLUMN_QUANTITYS = cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));

			Log.d("COLUMNVALUESADAPTER","-->"+COLUMN_QUANTITYS);

			TextView ss_sl_id = (TextView) view.findViewById(R.id.ss_slid);
			ss_sl_id.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_ID)));

			TextView ss_sl_no = (TextView) view.findViewById(R.id.ss_slno);
			ss_sl_no.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO)));

			TextView ss_prodcode = (TextView) view
					.findViewById(R.id.ss_prodcode);
			ss_prodcode.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE)));

			TextView ss_name = (TextView) view.findViewById(R.id.ss_name);
			ss_name.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME)));

			TextView ss_qty = (TextView) view.findViewById(R.id.ss_qty);
			ss_qty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));

			Log.d("COLUMN_QUANTITY","-->"+cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));

			TextView ss_price = (TextView) view.findViewById(R.id.ss_price);
			ss_price.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE)));

			TextView ss_net_total = (TextView) view
					.findViewById(R.id.ss_net_total);
			ss_net_total.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_NETTOTAL)));

			TextView ss_c_qty = (TextView) view.findViewById(R.id.ss_c_qty);
			ss_c_qty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));

			Log.d("COLUMN_CARTON_QTY","-->"+cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));

			TextView ss_l_qty = (TextView) view.findViewById(R.id.ss_l_qty);
			ss_l_qty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));

			Log.d("COLUMN_LOOSE_QTY","-->"+cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));

			TextView ss_foc = (TextView) view.findViewById(R.id.ss_foc);
			ss_foc.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_FOC)));

			TextView ss_item_disc = (TextView) view
					.findViewById(R.id.ss_item_disc);
			ss_item_disc.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT)));

			TextView ss_total = (TextView) view.findViewById(R.id.ss_total);
			ss_total.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_TOTAL)));

			TextView ss_tax = (TextView) view.findViewById(R.id.ss_tax);
			ss_tax.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_TAX)));

			TextView ss_subTotal = (TextView) view
					.findViewById(R.id.ss_subTotal);
			ss_subTotal.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL)));

			TextView ss_cprice = (TextView) view.findViewById(R.id.ss_cprice);
			ss_cprice.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE)));

			TextView ss_exch_qty = (TextView) view
					.findViewById(R.id.ss_exch_qty);
			ss_exch_qty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY)));

			TextView ss_minselling_price = (TextView) view
					.findViewById(R.id.ss_minselling_price);
			ss_minselling_price.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_MINIMUM_SELLING_PRICE)));

	

			TextView ss_mincartonselling_price = (TextView) view
					.findViewById(R.id.ss_mincartonselling_price);
			ss_mincartonselling_price.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_MINIMUM_CARTON_SELLING_PRICE)));
			TextView itemRemarks = (TextView) view
					.findViewById(R.id.ss_item_remarks);
			itemRemarks.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_ITEM_REMARKS)));

//			Log.d("refreshview", ".........." + refreshview);

			if (refreshview == false) {
				if (!appType.matches("")) {
					if (appType.matches("W")) {
						int barcodeStatust = SOTDatabase
								.getBarcodeStatus(ss_sl_id.getText().toString());
						Log.d("productbarcode", "ghfghfgh" + barcodeStatust);
						if (barcodeStatust == 1) {
							view.setBackgroundResource(drawable.list_item_selected_bg);
						} else {
							view.setBackgroundResource(drawable.list_selector);
						}
					}
				}
			} else {

				if (!appType.matches("")) {
					if (appType.matches("W")) {
						int barcodeStatust = SOTDatabase
								.getBarcodeStatus(ss_sl_id.getText().toString());
						Log.d("productbarcode", "ghfghfgh" + barcodeStatust);
						if (barcodeStatust == 1) {
							view.setBackgroundResource(drawable.list_item_selected_bg);
						} else {
							view.setBackgroundResource(drawable.list_selector);
						}
					} else {
						String sono = ConvertToSetterGetter.getSoNo();
						if (sono != null && !sono.isEmpty()) {

							// tranblocknegativestock="1";
							if (tranblocknegativestock != null
									&& !tranblocknegativestock.isEmpty()
									&& tranblocknegativestock.matches("1")) {
								double stock_avail = 0, qty_entered = 0;
								stock_avail = SOTDatabase
										.getStockQty(ss_prodcode.getText()
												.toString());
								qty_entered = SOTDatabase
										.getProductQty(ss_prodcode.getText()
												.toString());
								Log.d("stock_avail", "->" + stock_avail);
								Log.d("qty_entered", "->" + qty_entered);
								if (stock_avail < qty_entered) {
									view.setBackgroundResource(drawable.list_item_red_bg);
								} else {
									view.setBackgroundResource(drawable.list_selector);
								}
							} else {
								view.setBackgroundResource(drawable.list_selector);
							}

						} else {
							view.setBackgroundResource(drawable.list_selector);
						}
					}
				}
				
				//////////////////////////////////////////////////////////////////////////////
				
				String pCode = cursor.getString(cursor
						.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
				
				String SRslno = cursor.getString(cursor
						.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));

				HashMap<String, String> batchHave = new HashMap<String, String>();
				batchHave = SOTDatabase.getProductHav(pCode);

				String haveBatch = batchHave.get("have_batch");
				String haveExpiry = batchHave.get("have_expiry");

				// Log.d(" list haveBatch", ""+haveBatch);
				// Log.d(" list haveExpiry", ""+haveExpiry);

				String haveBatchOnStockOut = SalesOrderSetGet.getHaveBatchOnStockOut();
				Log.d("t haveBatchOnTransfer", "check it" + haveBatchOnStockOut);
	
//				haveExpiry="True";
//				haveBatch="True";
//				haveBatchOnStockOut="True";
				
				if(haveBatchOnStockOut!=null && !haveBatchOnStockOut.isEmpty()){
					
				}else{
					haveBatchOnStockOut="";
				}
				
				if (haveBatchOnStockOut.matches("True")) {

					if ((haveBatch != null && !haveBatch.isEmpty())
							|| (haveExpiry != null && !haveExpiry.isEmpty())) {
						if (haveBatch.matches("True") || haveExpiry.matches("True")) {

							Cursor batchCursor = SOTDatabase.getBatchCursorWithSR(pCode,SRslno);
							
							String totQty = SOTDatabase.getBatchQtySR(pCode,SRslno);
		

							Log.d("batchCursor count",
									"check it" + batchCursor.getCount());

							if (batchCursor.getCount() > 0) {
								if (totQty != null && !totQty.isEmpty()) {
									double totqty = Double.parseDouble(totQty);

									if (totqty > 0) {
										view.setBackgroundResource(drawable.list_item_bg_normal);
									} else {
										Log.d("Enter", "Batch");

										if (calCarton.matches("0")) {

											String tQty = SOTDatabase.getTotaBatchQtySR(pCode,SRslno);
											
					
											double tqty = 0;

											if (!tQty.matches("")) {
												tqty = Double.parseDouble(tQty);
											}

								int tCqty = SOTDatabase.getTotalBatchCQtySR(pCode,SRslno);
								
			
											if (tqty > 0 || tCqty > 0) {
												view.setBackgroundResource(drawable.list_item_bg_normal);
											} else {
												view.setBackgroundResource(drawable.list_item_red_bg);
											}

										} else {
											view.setBackgroundResource(drawable.list_item_red_bg);
										}
									}
								} else {
									view.setBackgroundResource(drawable.list_item_red_bg);
								}
							} else {
								view.setBackgroundResource(drawable.list_item_red_bg);
							}
						} else {
							view.setBackgroundResource(drawable.list_item_bg_normal);
						}
					} else {
						view.setBackgroundResource(drawable.list_item_bg_normal);
					}
				} else {
					view.setBackgroundResource(drawable.list_item_bg_normal);
				}

				
				
				/////////////////////////////////////////////////////////////////////
				

			}
		}

		public void selectAll(boolean select) {

			if (select == true) {
				addview = "true";
			} else if (select == false) {
				addview = "false";
			}
			notifyDataSetChanged();
		}

		public void refreshAll(boolean refresh) {

			if (refresh == true) {
				refreshview = true;
			} else if (refresh == false) {
				refreshview = false;
			}
			notifyDataSetChanged();
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

	private class AsyncCallWSSummary extends AsyncTask<Void, Void, Void> {

		String dialogStatus = "", offline_invoiceno = "", bllDsc="";

		@Override
		protected void onPreExecute() {

			SOResultArr.clear();
			spinnerLayout = new LinearLayout(InvoiceSummary.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(slsummary_layout, false);
			progressBar = new ProgressBar(InvoiceSummary.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
			bllDsc = sm_billDisc.getText().toString();
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			double discnt = 0.0;

			if (!bllDsc.matches("")) {
				discnt = Double.parseDouble(bllDsc);
			}
			try {

				if (ConvertToSetterGetter.getEdit_inv_no() == null
						|| ConvertToSetterGetter.getEdit_inv_no().matches("")) {
					invoiceNumber = "";
				} else {
					invoiceNumber = ConvertToSetterGetter.getEdit_inv_no();
				}
				if (ConvertToSetterGetter.getSoNo() != null
						&& !ConvertToSetterGetter.getSoNo().isEmpty()) {

					SoNo = ConvertToSetterGetter.getSoNo();
				} else {
					SoNo = "";
				}
				if (ConvertToSetterGetter.getDoNo() != null
						&& !ConvertToSetterGetter.getDoNo().isEmpty()) {

					DoNo = ConvertToSetterGetter.getDoNo();
				} else {
					DoNo = "";
				}

				// offline
					if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");
							SOResultArr = OfflineSalesOrderWebService
									.summaryInvoiceServiceOffline(
											"fncSaveInvoice", discnt, subTot,
											totTax, netTot,
											Double.toString(tota), slNo, SoNo,
											DoNo, invoiceNumber, "1");

							summaryResult = SOResultArr.get(0);
							ReceiptNo = SOResultArr.get(1);

						} else {
							Log.d("CheckOffline Alert -->", "False");
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
							finish();
						}

					} else {
						Log.d("checkOffline Status -->", "False"); // Online
																	// Mode
						SOResultArr = SOTSummaryWebService
								.summaryInvoiceService("fncSaveInvoice",
										discnt, subTot, totTax, netTot,
										Double.toString(tota), slNo, SoNo,
										DoNo, invoiceNumber, username,sm_billDiscPercentage.getText().toString());

						summaryResult = SOResultArr.get(0);
						ReceiptNo = SOResultArr.get(1);

						if (!summaryResult.matches("failed")) {

							// Simultanously save in offline
							ArrayList<String> offlineResult = new ArrayList<String>();
							offlineResult = OfflineSalesOrderWebService
									.summaryInvoiceServiceOffline(
											"fncSaveInvoice", discnt, subTot,
											totTax, netTot,
											Double.toString(tota), slNo, SoNo,
											DoNo, summaryResult, "0");

							if (offlineResult.size() > 0) {
								offline_invoiceno = offlineResult.get(0);
							} else {
								offline_invoiceno = "";
							}

							String cmpnyCode = SupplierSetterGetter
									.getCompanyCode();
							hm.put("CompanyCode", cmpnyCode);

							hm.put("InvoiceNo", summaryResult);
							jsonString = SalesOrderWebService.getSODetail(hm,
									"fncGetInvoiceHeaderByInvoiceNo");
							jsonResponse = new JSONObject(jsonString);
							jsonMainNode = jsonResponse
									.optJSONArray("SODetails");

							int lengthJsonAr = jsonMainNode.length();

							for (int k = 0; k < lengthJsonAr; k++) {
								/****** Get Object for each JSON node. ***********/
								JSONObject jsonChildNodes;

								jsonChildNodes = jsonMainNode.getJSONObject(k);
								totalbalance = jsonChildNodes.optString(
										"TotalBalance").toString();
								ProdDetails.setTotalbalance(totalbalance);
							}
						}
					}

				} else if (onlineMode.matches("False")) {

					SOResultArr = OfflineSalesOrderWebService
							.summaryInvoiceServiceOffline("fncSaveInvoice",
									discnt, subTot, totTax, netTot,
									Double.toString(tota), slNo, SoNo, DoNo,
									invoiceNumber, "1");

					summaryResult = SOResultArr.get(0);
					ReceiptNo = SOResultArr.get(1);

				}
			}

			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
					} else { // online
						if (offline_invoiceno.matches(summaryResult)) {
							Log.d("inv sum online", "inv sum online");
							offlinecustomerSynch.synchNextRunningNo(valid_url);
						}
					}
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (summaryResult.matches("failed")) {

				Toast.makeText(InvoiceSummary.this, "Failed",
						Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(slsummary_layout, true);
			} else {

				signature_img = SOTDatabase.getSignatureImage();
				//product_img = SOTDatabase.getProductImage();

				mCurrentgetPath = Product.getPath();
				Log.d("pathfind",mCurrentgetPath);
				product_img = ImagePathToBase64Str(mCurrentgetPath);

				if (signature_img == null) {
					signature_img = "";
				}
				if (product_img == null) {
					product_img = "";
				}

				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
					} else { // online
						String imgResult = SOTSummaryWebService
								.saveSignatureImage(summaryResult, ""
										+ setLatitude, "" + setLongitude,
										signature_img, product_img,
										"fncSaveInvoiceImages", "IN", address1,
										address2);

						Log.d("fncSaveInvoiceImages", "" + summaryResult + " "
								+ setLatitude + " " + setLongitude + " IN"
								+ address1 + address2 + "signature_img "
								+ signature_img + "product_img " + product_img);

						if (!imgResult.matches("")) {
							Log.d("CapImage", "Saved");
							deleteDirectory(new File(Environment.getExternalStorageDirectory()+ "/" +"SFA","Image"));
						} else {
							Log.d("CapImage", "Not Saved");
						}
					}
				}

				Toast.makeText(InvoiceSummary.this, "Saved Successfully",
						Toast.LENGTH_SHORT).show();
				// SOTDatabase.deleteImage();
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
			enableViews(slsummary_layout, true);

				String customeremail = strValidate(CustomerSetterGetter.getCustomerEmail());
				Log.d("customeremail", "aa " + customeremail);
				if (emailIschecked == true) {

					if(!customeremail.matches("")){
						new AsyncCallWSEmail().execute();
					}else{
						Toast.makeText(InvoiceSummary.this,"Customer has no email address",Toast.LENGTH_LONG).show();
					}

				}else if (emailIschecked == false) {
					Log.d("email", "no email");
				}

			String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
			
			Log.d("printertype", "ptpp "+printertype);
			
				if (enableprint.isChecked() || mDeliveryCheckBox.isChecked()) {

					if (FWMSSettingsDatabase
							.getPrinterAddress()
							.matches(
									"[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$") && 
									printertype.matches("Zebra iMZ320") ||(printertype.matches("4 Inch Bluetooth"))||(printertype.matches("3 Inch Bluetooth Generic"))||
							printertype.matches("Zebra iMZ320 4 Inch")) {

						if (cash_checkbox.isChecked()) {
							if (ReceiptNo.matches("null")
									|| ReceiptNo.matches("")) {
								Toast.makeText(InvoiceSummary.this,
										" Receipt Failed", Toast.LENGTH_SHORT)
										.show();
							} else {
								helper.showProgressDialog(R.string.generating_receipt);
								new AsyncGeneralSettings().execute();

							}
						} else {

							new AsyncGeneralSettings().execute();
							helper.showProgressDialog(R.string.generating_invoice);
						}

					} else {
						
						//if(printertype.matches("Zebra iMZ320")||(printertype.matches("4 Inch Bluetooth"))){
							if(printertype.matches("Zebra iMZ320")||(printertype.matches("4 Inch Bluetooth"))||(printertype.matches("3 Inch Bluetooth Generic"))||
									printertype.matches("Zebra iMZ320 4 Inch")){
							helper.showLongToast(R.string.error_configure_printer);
							clearView();
						}else{					
							
							if (cash_checkbox.isChecked()) {
								if (ReceiptNo.matches("null")
										|| ReceiptNo.matches("")) {
									Toast.makeText(InvoiceSummary.this,
											" Receipt Failed", Toast.LENGTH_SHORT)
											.show();
								} else {
									Log.d("PDFActivity", "PDFActivity");														
									}
								clearViewforPDF();
								
							} else {
								Log.d("PDFActivity", "PDFActivity");														
								}
							clearViewforPDF();	
							}

					}
				} else {
					clearView();
				}
			}
		}
	}

	public boolean deleteDirectory(File path) {
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isFile()) {
					files[i].delete();
				}
			}
		}
		return( path.delete() );
	}

	@SuppressWarnings("deprecation")
	public void clearView() {

		barcode_edit.setVisibility(View.GONE);

		save.setVisibility(View.INVISIBLE);
		SOTDatabase.deleteAllProduct();
		mListView.setAdapter(null);
		cursor.requery();

		sm_total.setText("");
		sm_billDisc.setText("");
		sm_billDiscPercentage.setText("");
		CustomerSetterGetter.setBillDiscount("");
		sm_tax.setText("");
		sm_billDisc.setFocusable(false);
		sm_billDiscPercentage.setFocusable(false);
		sm_header_layout.setVisibility(View.GONE);
		sm_subTotal.setText("");
		sm_netTotal.setText("");
		sm_totl_cqty.setText("");
		sm_totl_lqty.setText("");
		sm_totl_qty.setText("");

		ArrayList<HashMap<String, String>> empty = new ArrayList<HashMap<String, String>>();
		empty.clear();
		ConvertToSetterGetter.setStockRequestBatchDetailArr(empty);

		SOTDatabase.deleteBillDisc();
		SOTDatabase.deleteBarcode();

		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");
		SalesOrderSetGet.setCustomerTaxPerc("");
		SalesOrderSetGet.setTaxCode("");
		SalesOrderSetGet.setTaxType("");
		SalesOrderSetGet.setTaxPerc("");
		ConvertToSetterGetter.setEdit_inv_no("");
		CustomerSetterGetter.setDiscountPercentage("");
		SalesOrderSetGet.setDoNo("");
		mEmpty.setVisibility(View.VISIBLE);
		/*
		 * Intent i = new Intent(InvoiceSummary.this, InvoiceHeader.class);
		 * startActivity(i);
		 */
		if (header.matches("InvoiceHeader")) {
			intent = new Intent(InvoiceSummary.this, InvoiceHeader.class);
		} else if (header.matches("DeliveryOrderHeader")) {
			intent = new Intent(InvoiceSummary.this, DeliveryOrderHeader.class);
		} else if (header.matches("SalesOrderHeader")) {
			intent = new Intent(InvoiceSummary.this, SalesOrderHeader.class);
		} else if (header.matches("CustomerHeader")) {
			intent = new Intent(InvoiceSummary.this, CustomerListActivity.class);
		} else if (header.matches("RouteHeader")) {
			intent = new Intent(InvoiceSummary.this, RouteHeader.class);
		} else if (header.matches("ConsignmentHeader")) {
			intent = new Intent(InvoiceSummary.this, ConsignmentHeader.class);
		}else {
			intent = new Intent(InvoiceSummary.this, LandingActivity.class);
		}
		startActivity(intent);
		InvoiceSummary.this.finish();

	}
	
	@SuppressWarnings("deprecation")
	public void clearViewforPDF() {

		barcode_edit.setVisibility(View.GONE);

		save.setVisibility(View.INVISIBLE);
		SOTDatabase.deleteAllProduct();
		mListView.setAdapter(null);
		cursor.requery();

		sm_total.setText("");
		sm_billDisc.setText("");
		sm_billDiscPercentage.setText("");
		CustomerSetterGetter.setBillDiscount("");
		sm_tax.setText("");
		sm_billDisc.setFocusable(false);
		sm_billDiscPercentage.setFocusable(false);
		sm_header_layout.setVisibility(View.GONE);
		sm_subTotal.setText("");
		sm_netTotal.setText("");
		sm_totl_cqty.setText("");
		sm_totl_lqty.setText("");
		sm_totl_qty.setText("");

		sm_subTotal_inclusive.setText("");
		
		ArrayList<HashMap<String, String>> empty = new ArrayList<HashMap<String, String>>();
		empty.clear();
		ConvertToSetterGetter.setStockRequestBatchDetailArr(empty);

		SOTDatabase.deleteBillDisc();
		SOTDatabase.deleteBarcode();

		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");
		SalesOrderSetGet.setCustomerTaxPerc("");
		ConvertToSetterGetter.setEdit_inv_no("");
		CustomerSetterGetter.setDiscountPercentage("");
		SalesOrderSetGet.setDoNo("");
		mEmpty.setVisibility(View.VISIBLE);
		
		if (header.matches("InvoiceHeader")) {
			intent = new Intent(InvoiceSummary.this, InvoiceHeader.class);
			intent.putExtra("InvoiceNo", summaryResult);
		} else if (header.matches("DeliveryOrderHeader")) {
			intent = new Intent(InvoiceSummary.this, DeliveryOrderHeader.class);
			intent.putExtra("InvoiceNo", summaryResult);
		} else if (header.matches("SalesOrderHeader")) {
			intent = new Intent(InvoiceSummary.this, SalesOrderHeader.class);
			intent.putExtra("InvoiceNo", summaryResult);
		} else if (header.matches("CustomerHeader")) {
			intent = new Intent(InvoiceSummary.this, CustomerListActivity.class);
			intent.putExtra("InvoiceNo", summaryResult);
		} else if (header.matches("RouteHeader")) {
			intent = new Intent(InvoiceSummary.this, RouteHeader.class);
			intent.putExtra("InvoiceNo", summaryResult);
		} else if (header.matches("ConsignmentHeader")) {
			intent = new Intent(InvoiceSummary.this, ConsignmentHeader.class);
			intent.putExtra("InvoiceNo", summaryResult);
		}else {
			intent = new Intent(InvoiceSummary.this, LandingActivity.class);
		}
		startActivity(intent);
		InvoiceSummary.this.finish();
	
	}

//	public void CameraAction() {
//		/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//		intent.putExtra("crop", "true");
//		intent.putExtra("aspectX", 0);
//		intent.putExtra("aspectY", 0);
//		intent.putExtra("outputX", 200);
//		intent.putExtra("outputY", 150);
//
//			intent.putExtra("return-data", true);
//			startActivityForResult(intent, PICK_FROM_CAMERA);*/
//		try {
//			 Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//	         if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//	             startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
//	         }
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

			case PICK_FROM_CAMERA:
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
//						SOTDatabase.init(InvoiceSummary.this);
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

			case SIGNATURE_ACTIVITY:
				if (resultCode == RESULT_OK) {
					// Bundle extras = data.getExtras();
					byte[] bytes = data.getByteArrayExtra("status");
					if (bytes != null) {
						// Bitmap photo = extras.getParcelable("status");

						Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
								bytes.length);
						bitmap = Bitmap.createScaledBitmap(bitmap, 240, 80,
								true);

						sosign.setImageBitmap(bitmap);

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] bitMapData = stream.toByteArray();
						String signature_image = Base64.encodeToString(
								bitMapData, Base64.DEFAULT);
						SOTDatabase.init(InvoiceSummary.this);
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
			}
//		}
	}

	private void handleCameraPhoto(int actionCode) {
		Log.d("mCurrentPhotoPath", "mCurrentPhotoPath-->"+mCurrentPhotoPath);
		if (mCurrentPhotoPath != null) {
			switch (actionCode) {
				case PICK_FROM_CAMERA: {
					Product.setPath(mCurrentPhotoPath);
					decodeImagePathFile(mCurrentPhotoPath,prodphoto);
					break;
				}
			}
			//	galleryAddPic();
			mCurrentPhotoPath = null;
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

			String camera_image=ImagePathToBase64Str(imageString);

//			ByteArrayOutputStream stream = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//			byte[] bitMapData = stream.toByteArray();
//			String camera_image = Base64.encodeToString(bitMapData,
//					Base64.DEFAULT);
			SOTDatabase.init(InvoiceSummary.this);

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
		String temp="";
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] b = baos.toByteArray();
			temp = Base64.encodeToString(b, Base64.DEFAULT);
		}catch (Exception e){
			e.printStackTrace();
		}
		return temp;
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
			//	helper.dismissProgressView(salesO_parent);
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
			//helper.dismissProgressView(salesO_parent);
		}
		return imagePath;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);


		sm_billDisc.setText("");
		sm_billDiscPercentage.setText("");
		contextmenu_click="";

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		View TargetV = info.targetView;
		TextView tvProdCode = (TextView) TargetV.findViewById(R.id.ss_prodcode);
		String strProdCode = tvProdCode.getText().toString();

		Log.d("code", strProdCode);
		String havebatch = SOTDatabase.getprodcodefrombatch(strProdCode);
		if (!havebatch.matches("")) {
			menu.add(0, v.getId(), 0, "Edit Batch");
		}

		menu.add(0, v.getId(), 0, "Edit");
		menu.add(0, v.getId(), 0, "Delete");

	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		if (item.getTitle() == "Edit Batch") {
			contextmenu_click="Edit Batch";
			String id = ((TextView) info.targetView.findViewById(R.id.ss_slid))
					.getText().toString();
			String code = ((TextView) info.targetView
					.findViewById(R.id.ss_prodcode)).getText().toString();
			String name = ((TextView) info.targetView
					.findViewById(R.id.ss_name)).getText().toString();
			String price = ((TextView) info.targetView
					.findViewById(R.id.ss_price)).getText().toString();
			String sno = ((TextView) info.targetView.findViewById(R.id.ss_slno))
					.getText().toString();

			ArrayList<String> batchHave = new ArrayList<String>();
			batchHave = SOTDatabase.getBatchHave(code);

			String haveBatch = batchHave.get(0);
			String haveExpiry = batchHave.get(1);
			String slCartonPerQty = batchHave.get(2);

			String haveBatchOnStockIn = SalesOrderSetGet
					.getHaveBatchOnStockIn();
			String haveBatchOnStockOut = SalesOrderSetGet
					.getHaveBatchOnStockOut();
			if (haveBatchOnStockIn.matches("True") || haveBatchOnStockOut.matches("True")) {
				Log.d("haveBatchOnStockIn", ""+haveBatchOnStockIn);
				Log.d("haveBatchOnStockOut", ""+haveBatchOnStockOut);
				if (haveBatch.matches("True") || haveExpiry.matches("True")) {
					Log.d("haveBatch", "haveExpiry");

					HashMap<String, EditText> hm = new HashMap<String, EditText>();

					hm.put("sm_total", sm_total);
					hm.put("sm_total_new", sm_total_new);
					hm.put("sm_itemDisc", sm_itemDisc);
					hm.put("sm_subTotal", sm_subTotal);
					hm.put("sm_tax", sm_tax);
					hm.put("sm_netTotal", sm_netTotal);
					hm.put("sm_subTotal_inclusive", sm_subTotal_inclusive);



						invoiceBatchDialog.initiateBatchPopupWindow(
								InvoiceSummary.this, id, sno, haveBatch,
								haveExpiry, code, name, slCartonPerQty, cursor,
								price, hm);
										
					
				} else {
					Log.d("no haveBatch", "no haveExpiry");

				}

			} else {
				Log.d("no haveBatchOnStockIn", "no haveBatchOnStockIn");
			}

		} else if (item.getTitle() == "Edit") {
			contextmenu_click="Edit";
			if (screenInches < 7) {

				Intent i = new Intent(InvoiceSummary.this,
						InvoiceAddProduct.class);
				i.putExtra("SOT_ssno", ((TextView) info.targetView
						.findViewById(R.id.ss_slid)).getText().toString());
				i.putExtra("SOT_ssproductcode", ((TextView) info.targetView
						.findViewById(R.id.ss_prodcode)).getText().toString());
				i.putExtra("SOT_str_ssprodname", ((TextView) info.targetView
						.findViewById(R.id.ss_name)).getText().toString());
				i.putExtra("SOT_str_sscq", ((TextView) info.targetView
						.findViewById(R.id.ss_c_qty)).getText().toString());
				i.putExtra("SOT_str_sslq", ((TextView) info.targetView
						.findViewById(R.id.ss_l_qty)).getText().toString());
				i.putExtra("SOT_str_ssqty", ((TextView) info.targetView
						.findViewById(R.id.ss_qty)).getText().toString());
				i.putExtra("SOT_str_ssfoc", ((TextView) info.targetView
						.findViewById(R.id.ss_foc)).getText().toString());
				i.putExtra("SOT_str_ssprice", ((TextView) info.targetView
						.findViewById(R.id.ss_price)).getText().toString());
				i.putExtra("SOT_str_ssdisc", ((TextView) info.targetView
						.findViewById(R.id.ss_item_disc)).getText().toString());
				i.putExtra("SOT_str_sscprice", ((TextView) info.targetView
						.findViewById(R.id.ss_cprice)).getText().toString());
				i.putExtra("SOT_str_ssexchqty", ((TextView) info.targetView
						.findViewById(R.id.ss_exch_qty)).getText().toString());

				i.putExtra("SOT_str_minselling_price",
						((TextView) info.targetView
								.findViewById(R.id.ss_minselling_price))
								.getText().toString());

				i.putExtra("SOT_str_minCartonselling_price",
						((TextView) info.targetView
								.findViewById(R.id.ss_mincartonselling_price))
								.getText().toString());


				String id = ((TextView) info.targetView
						.findViewById(R.id.ss_slid)).getText().toString();
				String taxValue = SOTDatabase.getTaxValue(id);
				String uom = SOTDatabase.getUOM(id);
				String pieceperqty = SOTDatabase.getPiecePerQty(id);
				String taxtype = SOTDatabase.getTaxType(id);

				i.putExtra("SOT_str_ssuom", uom);
				i.putExtra("SOT_st_sscpqty", pieceperqty);
				i.putExtra("SOT_st_sstaxvalue", taxValue);
				i.putExtra("SOT_str_sstaxtype", taxtype);

				i.putExtra("SOT_st_sstotal", ((TextView) info.targetView
						.findViewById(R.id.ss_subTotal)).getText().toString());
				i.putExtra("SOT_st_sstax", ((TextView) info.targetView
						.findViewById(R.id.ss_tax)).getText().toString());
				i.putExtra("SOT_st_ssnettot", ((TextView) info.targetView
						.findViewById(R.id.ss_net_total)).getText().toString());
				i.putExtra("SOT_ssupdate", "Update");
				i.putExtra("SOT_sscancel", "Cancel");
				startActivity(i);
				InvoiceSummary.this.finish();

			} else {

				String edit_taxValue = "", edit_uom = "", edit_pieceperqty = "", edit_taxtype = "", edit_minselling_price = "";

				edit_ssno = ((TextView) info.targetView
						.findViewById(R.id.ss_slid)).getText().toString();
				edit_prodcode = ((TextView) info.targetView
						.findViewById(R.id.ss_prodcode)).getText().toString();
				// edit_sscprice = ;
				// edit_ssexchqty = ((TextView)
				// info.targetView.findViewById(R.id.ss_exch_qty)).getText().toString();
				edit_minselling_price = ((TextView) info.targetView
						.findViewById(R.id.ss_minselling_price)).getText()
						.toString();
				// edit_itemdisc = ;
				edit_taxValue = SOTDatabase.getTaxValue(edit_ssno);
				edit_uom = SOTDatabase.getUOM(edit_ssno);
				edit_pieceperqty = SOTDatabase.getPiecePerQty(edit_ssno);
				edit_taxtype = SOTDatabase.getTaxType(edit_ssno);
				//
				// edit_sstotal = ((TextView)
				// info.targetView.findViewById(R.id.ss_subTotal)).getText().toString();
				// edit_sstax = ((TextView)
				// info.targetView.findViewById(R.id.ss_tax)).getText().toString();
				// edit_ssnettot = ((TextView)
				// info.targetView.findViewById(R.id.ss_net_total)).getText().toString();

				builder = new AlertDialog.Builder(InvoiceSummary.this);
				builder.setTitle(((TextView) info.targetView
						.findViewById(R.id.ss_prodcode)).getText().toString());
				LayoutInflater adbInflater = LayoutInflater
						.from(InvoiceSummary.this);
				View cartview = adbInflater.inflate(R.layout.invoice_edit_item,
						null);

				foc_layout = (LinearLayout) cartview
						.findViewById(R.id.foc_layout);
				sl_codefield = (EditText) cartview
						.findViewById(R.id.sl_codefield);
				sl_namefield = (EditText) cartview
						.findViewById(R.id.sl_namefield);
				sl_cartonQty = (EditText) cartview
						.findViewById(R.id.sl_cartonQty);
				sl_looseQty = (EditText) cartview
						.findViewById(R.id.sl_looseQty);
				sl_qty = (EditText) cartview.findViewById(R.id.sl_qty);
				sl_foc = (EditText) cartview.findViewById(R.id.sl_foc);
				sl_price = (EditText) cartview.findViewById(R.id.sl_price);
				sl_itemDiscount = (EditText) cartview
						.findViewById(R.id.sl_itemDiscount);
				sl_cartonPerQty = (EditText) cartview
						.findViewById(R.id.sl_cartonPerQty);
				sl_uom = (EditText) cartview.findViewById(R.id.sl_uom);
				sl_total = (EditText) cartview.findViewById(R.id.sl_total);
				sl_tax = (EditText) cartview.findViewById(R.id.sl_tax);
				sl_netTotal = (EditText) cartview
						.findViewById(R.id.sl_netTotal);

				sl_cprice = (EditText) cartview.findViewById(R.id.sl_cprice);
				sl_exchange = (EditText) cartview
						.findViewById(R.id.sl_exchange);
				price_txt = (TextView) cartview.findViewById(R.id.price_txt);
				expand = (ImageView) cartview.findViewById(R.id.expand);

				price_header_layout = (LinearLayout) cartview
						.findViewById(R.id.price_header_layout);
				foc_layout = (LinearLayout) cartview
						.findViewById(R.id.foc_layout);
				foc_header_layout = (LinearLayout) cartview
						.findViewById(R.id.foc_header_layout);
				sl_stock = (EditText) cartview.findViewById(R.id.sl_stock);
				sl_return = (EditText) cartview.findViewById(R.id.sl_return);

				uomcperqty_ll = (LinearLayout) cartview
						.findViewById(R.id.uomcperqty_ll);
				// final Spinner prompt = (Spinner)
				// cartview.findViewById(R.id.weight_status);
				// prompt.setVisibility(View.GONE);
				builder.setView(cartview);
				uomcperqty_ll.setVisibility(View.VISIBLE);

				taxType = edit_taxtype;
				taxValue = edit_taxValue;
				slUomCode = edit_uom;
				slCartonPerQty = edit_pieceperqty;
				minselling_price = edit_minselling_price;
				slPrice = ((TextView) info.targetView
						.findViewById(R.id.ss_price)).getText().toString();

				ProductStockAsyncCall task = new ProductStockAsyncCall();
				task.execute();

				priceflag = SalesOrderSetGet.getCartonpriceflag();

				if (priceflag.matches("null") || priceflag.matches("")) {
					priceflag = "0";
				}

				if (priceflag.matches("1")) {
					sl_cprice.setVisibility(View.VISIBLE);
					price_header_layout.setVisibility(View.VISIBLE);
					price_txt.setVisibility(View.GONE);
				} else {
					sl_cprice.setVisibility(View.GONE);
					price_header_layout.setVisibility(View.GONE);
					price_txt.setVisibility(View.VISIBLE);
				}

				sl_codefield.setText(((TextView) info.targetView
						.findViewById(R.id.ss_prodcode)).getText().toString());
				sl_namefield.setText(((TextView) info.targetView
						.findViewById(R.id.ss_name)).getText().toString());
				sl_cartonQty.setText(((TextView) info.targetView
						.findViewById(R.id.ss_c_qty)).getText().toString());
				sl_looseQty.setText(((TextView) info.targetView
						.findViewById(R.id.ss_l_qty)).getText().toString());
				sl_qty.setText(((TextView) info.targetView
						.findViewById(R.id.ss_qty)).getText().toString());
				sl_price.setText(((TextView) info.targetView
						.findViewById(R.id.ss_price)).getText().toString());
				sl_foc.setText(((TextView) info.targetView
						.findViewById(R.id.ss_foc)).getText().toString());
				sl_itemDiscount.setText(((TextView) info.targetView
						.findViewById(R.id.ss_item_disc)).getText().toString());
				sl_uom.setText(slUomCode);
				sl_cprice.setText(((TextView) info.targetView
						.findViewById(R.id.ss_cprice)).getText().toString());
				sl_exchange.setText(((TextView) info.targetView
						.findViewById(R.id.ss_exch_qty)).getText().toString());
				sl_total.setText(((TextView) info.targetView
						.findViewById(R.id.ss_subTotal)).getText().toString());
				sl_tax.setText(((TextView) info.targetView
						.findViewById(R.id.ss_tax)).getText().toString());
				sl_netTotal.setText(((TextView) info.targetView
						.findViewById(R.id.ss_net_total)).getText().toString());


				if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
						|| slCartonPerQty.matches("")) {

					// sl_cartonQty.setEnabled(false);
					sl_cartonQty.setFocusable(false);
					sl_cartonQty.setBackgroundResource(drawable.labelbg);

					// sl_looseQty.setEnabled(false);
					sl_looseQty.setFocusable(false);
					sl_looseQty.setBackgroundResource(drawable.labelbg);

					sl_qty.requestFocus();

				} else {
					sl_cartonQty.setFocusableInTouchMode(true);
					sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

					sl_looseQty.setFocusableInTouchMode(true);
					sl_looseQty.setBackgroundResource(drawable.edittext_bg);

					sl_cartonQty.requestFocus();
				}
				sl_qty.setFocusableInTouchMode(true);
				sl_qty.setBackgroundResource(drawable.edittext_bg);

				if (FormSetterGetter.isEditPrice()) {
					sl_price.setEnabled(true);
					sl_price.setFocusableInTouchMode(true);
					sl_price.setBackgroundResource(drawable.edittext_bg);
				} else {
					sl_price.setEnabled(false);
					sl_price.setFocusable(false);
					sl_price.setGravity(Gravity.LEFT);
					sl_price.setBackgroundResource(drawable.labelbg);
				}
				sl_foc.setFocusableInTouchMode(true);
				sl_foc.setBackgroundResource(drawable.edittext_bg);
				sl_itemDiscount.setFocusableInTouchMode(true);
				sl_itemDiscount.setBackgroundResource(drawable.edittext_bg);
				sl_uom.setFocusableInTouchMode(true);
				sl_uom.setBackgroundResource(drawable.edittext_bg);
				sl_cartonPerQty.setText(slCartonPerQty);

				expand.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						if (foc_layout.getVisibility() == View.VISIBLE) {
							// Its visible
							foc_layout.setVisibility(View.GONE);
							foc_header_layout.setVisibility(View.GONE);

						} else {
							foc_layout.setVisibility(View.VISIBLE);
							foc_header_layout.setVisibility(View.VISIBLE);
							// Either gone or invisible
						}

					}
				});

				sl_cartonQty
						.setOnEditorActionListener(new OnEditorActionListener() {

							// public boolean onEditorAction(TextView v, int
							// actionId,
							// KeyEvent event) {
							@Override
							public boolean onEditorAction(TextView v,
									int actionId, KeyEvent event) {
								if (actionId == EditorInfo.IME_ACTION_NEXT) {
									// TODO Auto-generated method stub
									slPrice = sl_price.getText().toString();
									if (priceflag.matches("0")) {
										cartonQty();
									} else if (priceflag.matches("1")) {
										cartonQtyNew();
									}

									sl_looseQty.requestFocus();
									return true;
								}
								return false;
							}
						});

				cqtyTW = new TextWatcher() {

					@Override
					public void afterTextChanged(Editable s) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub
						ss_Cqty = sl_cartonQty.getText().toString();
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						slPrice = sl_price.getText().toString();
						if (priceflag.matches("0")) {
							cartonQty();
						} else if (priceflag.matches("1")) {
							cartonQtyNew();
						}
						int length = sl_cartonQty.length();
						if (length == 0) {

							String lqty = sl_looseQty.getText().toString();
							if (lqty.matches("")) {
								lqty = "0";
							}
							if (!lqty.matches("")) {
								sl_qty.removeTextChangedListener(qtyTW);
								sl_qty.setText(lqty);
								sl_qty.addTextChangedListener(qtyTW);

								if (sl_qty.length() != 0) {
									sl_qty.setSelection(sl_qty.length());
								}
								double lsQty = Double.parseDouble(lqty);

								if (priceflag.matches("0")) {
									productTotal(lsQty);
								} else if (priceflag.matches("1")) {
									// String cprice =
									// sl_price.getText().toString();
									productTotalNew();
								}
							}
						}
					}
				};
				sl_looseQty
						.setOnEditorActionListener(new OnEditorActionListener() {

							@Override
							public boolean onEditorAction(TextView v,
									int actionId, KeyEvent event) {
								if (actionId == EditorInfo.IME_ACTION_NEXT) {
									slPrice = sl_price.getText().toString();
									if (priceflag.matches("0")) {
										looseQtyCalc();
									} else if (priceflag.matches("1")) {
										looseQtyCalcNew();
									}

									sl_qty.requestFocus();
									return true;
								}
								return false;
							}
						});

				lqtyTW = new TextWatcher() {

					@Override
					public void afterTextChanged(Editable s) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						beforeLooseQty = sl_looseQty.getText().toString();
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						slPrice = sl_price.getText().toString();
						if (priceflag.matches("0")) {
							looseQtyCalc();
						} else if (priceflag.matches("1")) {
							looseQtyCalcNew();
						}

						int length = sl_looseQty.length();
						if (length == 0) {
							String qty = sl_qty.getText().toString();
							if (!beforeLooseQty.matches("") && !qty.matches("")) {

								int qtyCnvrt = Integer.parseInt(qty);
								int lsCnvrt = Integer.parseInt(beforeLooseQty);

								sl_qty.removeTextChangedListener(qtyTW);
								sl_qty.setText("" + (qtyCnvrt - lsCnvrt));
								sl_qty.addTextChangedListener(qtyTW);

								if (sl_qty.length() != 0) {
									sl_qty.setSelection(sl_qty.length());
								}

								if (priceflag.matches("0")) {
									looseQtyCalc();
								} else if (priceflag.matches("1")) {
									looseQtyCalcNew();
								}
							}
						}
					}
				};

				sl_qty.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_NEXT) {

							slPrice = sl_price.getText().toString();
							String qty = sl_qty.getText().toString();
							if (!qty.matches("")) {
								clQty();
							}

							if (priceflag.matches("1")) {
								sl_cprice.requestFocus();
							} else {
								sl_price.requestFocus();
							}
							return true;
						}
						return false;
					}
				});

				qtyTW = new TextWatcher() {

					@Override
					public void afterTextChanged(Editable s) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

						slPrice = sl_price.getText().toString();
						String qty = sl_qty.getText().toString();
						if (!qty.matches("")) {
							clQty();
						}

						int length = sl_qty.length();
						if (length == 0) {
							productTotal(0);
							sl_cartonQty.removeTextChangedListener(cqtyTW);
							sl_cartonQty.setText("");
							sl_cartonQty.addTextChangedListener(cqtyTW);

							sl_looseQty.removeTextChangedListener(lqtyTW);
							sl_looseQty.setText("");
							sl_looseQty.addTextChangedListener(lqtyTW);
						}
					}

				};

				sl_foc.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_NEXT) {

							if (sl_price.getText().toString().equals("0.00")
									|| sl_price.getText().toString()
											.equals("0")
									|| sl_price.getText().toString()
											.equals("0.0")
									|| sl_price.getText().toString()
											.equals(".0")) {
								sl_price.setText("");
							}
							sl_price.requestFocus();
							return true;
						}
						return false;
					}
				});

				sl_foc.addTextChangedListener(new TextWatcher() {

					@Override
					public void afterTextChanged(Editable s) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

					}

				});
				sl_price.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_NEXT) {

							String qty = sl_qty.getText().toString();
							if (!qty.matches("")) {
								double qtyCalc = Double.parseDouble(qty);
								slPrice = sl_price.getText().toString();
								if (priceflag.matches("0")) {
									productTotal(qtyCalc);
								} else if (priceflag.matches("1")) {
									productTotalNew();
								}
							}
							sl_itemDiscount.requestFocus();
							return true;
						}
						return false;
					}
				});
				sl_price.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {

						if (sl_price.getText().toString().equals("0.00")
								|| sl_price.getText().toString().equals("0")
								|| sl_price.getText().toString().equals("0.0")
								|| sl_price.getText().toString().equals(".0")) {
							sl_price.setText("");
						}
						return false;
					}
				});
				sl_price.addTextChangedListener(new TextWatcher() {

					@Override
					public void afterTextChanged(Editable s) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

						String qty = sl_qty.getText().toString();
						if (!qty.matches("")) {
							double qtyCalc = Double.parseDouble(qty);
							slPrice = sl_price.getText().toString();
							if (priceflag.matches("0")) {
								productTotal(qtyCalc);
							} else if (priceflag.matches("1")) {
								productTotalNew();
							}
						}
					}

				});
				sl_itemDiscount
						.setOnEditorActionListener(new OnEditorActionListener() {

							@Override
							public boolean onEditorAction(TextView v,
									int actionId, KeyEvent event) {
								if (actionId == EditorInfo.IME_ACTION_NEXT
										|| actionId == EditorInfo.IME_ACTION_DONE) {

									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(
											sl_itemDiscount.getWindowToken(), 0);
									return true;
								}
								return false;
							}
						});

				sl_cprice
						.setOnEditorActionListener(new OnEditorActionListener() {

							@Override
							public boolean onEditorAction(TextView v,
									int actionId, KeyEvent event) {
								if (actionId == EditorInfo.IME_ACTION_NEXT) {

									String qty = sl_qty.getText().toString();
									if (!qty.matches("")) {
										double qtyCalc = Double
												.parseDouble(qty);
										String cPrice = sl_cprice.getText()
												.toString();

										if (priceflag.matches("0")) {
											productTotal(qtyCalc);
										} else if (priceflag.matches("1")) {
											productTotalNew();
										}
									}
									sl_price.requestFocus();
									return true;
								}
								return false;
							}
						});
				sl_cprice.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {

						if (sl_cprice.getText().toString().equals("0.00")
								|| sl_cprice.getText().toString().equals("0")
								|| sl_cprice.getText().toString().equals("0.0")
								|| sl_cprice.getText().toString().equals(".0")) {
							sl_cprice.setText("");
						}
						return false;
					}
				});
				sl_cprice.addTextChangedListener(new TextWatcher() {

					@Override
					public void afterTextChanged(Editable s) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

						String qty = sl_qty.getText().toString();
						if (!qty.matches("")) {
							double qtyCalc = Double.parseDouble(qty);
							String cPrice = sl_cprice.getText().toString();

							if (priceflag.matches("0")) {
								productTotal(qtyCalc);
							} else if (priceflag.matches("1")) {
								productTotalNew();
							}
						}
					}

				});

				sl_itemDiscount.addTextChangedListener(new TextWatcher() {

					@Override
					public void afterTextChanged(Editable s) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

						slPrice = sl_price.getText().toString();

						if (priceflag.matches("0")) {
							itemDiscountCalc();
						} else if (priceflag.matches("1")) {
							itemDiscountCalcNew();
						}

					}
				});

				sl_cartonQty.addTextChangedListener(cqtyTW);
				sl_looseQty.addTextChangedListener(lqtyTW);
				sl_qty.addTextChangedListener(qtyTW);


				builder.setNegativeButton("cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				builder.setPositiveButton("Update",

						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								double cartTotal, cartTax, cartNetTotal;
								double slprice = 0, slcprice = 0, minSellingPrice = 0;

								String codeStr = sl_codefield.getText()
										.toString();
								String nameStr = sl_namefield.getText()
										.toString();
								String cartonQtyStr = sl_cartonQty.getText()
										.toString();
								String looseQtyStr = sl_looseQty.getText()
										.toString();
								String qtyStr = sl_qty.getText().toString();
								String focStr = sl_foc.getText().toString();
								String priceStr = sl_price.getText().toString();
								String dicountStr = sl_itemDiscount.getText()
										.toString();
								String cartonPerQtyStr = sl_cartonPerQty
										.getText().toString();
								String totalStr = sl_total.getText().toString();
								String taxStr = sl_tax.getText().toString();
								String netTotalStr = sl_netTotal.getText()
										.toString();
								String cpriceStr = sl_cprice.getText()
										.toString();
								String exQtyStr = sl_exchange.getText()
										.toString();
								String uomStr = sl_uom.getText().toString();
								String slret = sl_return.getText().toString();

								if (!priceStr.matches("")) {
									slprice = Double.parseDouble(priceStr);
								}

								if (!cpriceStr.matches("")) {
									slcprice = Double.parseDouble(cpriceStr);
								}
								if (!minselling_price.matches("")) {
									minSellingPrice = Double
											.parseDouble(minselling_price);
								}

								if (minSellingPrice > slprice) {
									Toast.makeText(
											InvoiceSummary.this,
											"Price must be greater than minimum selling price $ "
													+ minselling_price,
											Toast.LENGTH_LONG).show();
								} else {

								}

								if (priceStr.matches("")) {
									priceStr = "0";
								}

								if (cpriceStr.matches("")) {
									cpriceStr = "0";
								}

								try {
									if (priceStr.matches("")
											|| qtyStr.matches("")) {
										Toast.makeText(InvoiceSummary.this,
												"Please enter the value",
												Toast.LENGTH_SHORT).show();
									} else {

										int cartonQty = 0, looseQty = 0, qty = 0, foc = 0, cartonPerQty = 0;
										double price = 0, discount = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0, cprices = 0, exch = 0;
										String sb_ttl = "";
										String netT = "";
										if (!cartonQtyStr.matches("")) {
											cartonQty = Integer
													.parseInt(cartonQtyStr);
										}
										if (!looseQtyStr.matches("")) {
											looseQty = Integer
													.parseInt(looseQtyStr);
										}
										if (!qtyStr.matches("")) {
											qty = Integer.parseInt(qtyStr);
										}
										if (!focStr.matches("")) {
											foc = Integer.parseInt(focStr);
										}
										if (!cartonPerQtyStr.matches("")) {
											cartonPerQty = Integer
													.parseInt(cartonPerQtyStr);
										}
										if (!priceStr.matches("")) {
											price = Double
													.parseDouble(priceStr);
										}

										if (!cpriceStr.matches("")) {
											cprices = Double
													.parseDouble(cpriceStr);
										}

										if (cpriceStr.matches("")) {
											cpriceStr = "0.00";
										}

										if (priceflag.matches("0")) {
											cpriceStr = "0.00";
										}

										if (exQtyStr.matches("")) {
											exQtyStr = "0";
										}

										if (!totalStr.matches("")) {
											total = Double
													.parseDouble(totalStr);

											String itemDisc = sl_itemDiscount
													.getText().toString();
											if (!itemDisc.matches("")) {
												itmDisc = Double
														.parseDouble(itemDisc);
												subTotal = total;
											} else {
												subTotal = total;
											}

											sb_ttl = twoDecimalPoint(subTotal);

										}
										if (!taxStr.matches("")) {
											tax = Double.parseDouble(taxStr);
										}
										if (!netTotalStr.matches("")) {
											if (taxType != null
													&& !taxType.isEmpty()) {
												if (taxType.matches("I")
														|| taxType.matches("Z")) {
													ntTot = subTotal;
												} else {
													ntTot = subTotal + tax;
												}
											} else {
												ntTot = subTotal + tax;
											}

											netT = twoDecimalPoint(ntTot);
										}

										if (taxValue.matches("")
												|| taxValue == null) {
											taxValue = "0";
										}

										if (priceflag.matches("0")) {
											itemDiscountCalc();
										} else if (priceflag.matches("1")) {
											itemDiscountCalcNew();
										}

										String disctStr = sl_itemDiscount
												.getText().toString();
										if (!disctStr.matches("")) {
											discount = Double
													.parseDouble(disctStr);

										}
										String totl = twoDecimalPoint(tt);
										Log.d("total" + tt, totl);

										double dis = 0.0;
										if (!dicountStr.matches("")) {
											dis = Double
													.parseDouble(dicountStr);
										}
										Log.d("tt+dis", "->" + tt + dis);

										if (!totalStr.matches("")) {
											SOTDatabase.updateBillDisc(
													Integer.parseInt(edit_ssno),
													codeStr, sb_ttl);
										}

										if(qty>0){
											if (cartonPerQty == 1){
												Log.d("CheckValue",SalesOrderSetGet.getCartonpriceflag());
												if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
													if(cprices !=0){
														SOTDatabase.updateProductForInvoice(
																codeStr, nameStr, cartonQty,
																looseQty, qty, foc, price,
																discount, uomStr, cartonPerQty,
																tt + dis, tax, sb_ttl, netT,
																Integer.parseInt(edit_ssno),
																cpriceStr, exQtyStr,slret);

														cursor.requery();
														Toast.makeText(InvoiceSummary.this,
																"Updated", Toast.LENGTH_SHORT)
																.show();

														smTax = SOTDatabase.getTax();
														String ProdTax = fourDecimalPoint(smTax);
														sm_tax.setText("" + ProdTax);

														sbTtl = SOTDatabase.getSubTotal();
														String sub = twoDecimalPoint(sbTtl);

														Log.d("sbTtl","-->"+sbTtl+"smTax"+smTax);

														sm_subTotal.setText("" + sub);

														cQtyTtl = SOTDatabase.getTotalCqty();
														int tot_cqty = (int) cQtyTtl;
														sm_totl_cqty.setText("" + tot_cqty);

														lQtyTtl = SOTDatabase.getTotalLqty();
														int tot_lqty = (int) lQtyTtl;
														sm_totl_lqty.setText("" + tot_lqty);

														qtyTtl = SOTDatabase.getTotalQty();
														int tot_qty = (int) qtyTtl;
														sm_totl_qty.setText("" + qtyTtl);

														String taxType = SalesOrderSetGet
																.getCompanytax();

														if (taxType != null
																&& !taxType.isEmpty()) {
															if (taxType.matches("I")
																	|| taxType.matches("Z")) {
																netTotal = sbTtl;
															} else {
																netTotal = sbTtl + smTax;
															}
														} else {
															netTotal = sbTtl + smTax;
														}

														String ProdNettotal = twoDecimalPoint(netTotal);
														sm_netTotal.setText("" + ProdNettotal);

														// Added New 03.04.2017
														if (taxType != null && !taxType.isEmpty()) {
															if (taxType.matches("I")) {
																sm_subTotal.setVisibility(View.GONE);
																sm_subTotal_inclusive.setVisibility(View.VISIBLE);
																double subt = netTotal - smTax;
																String subto = twoDecimalPoint(subt);
																sm_subTotal_inclusive.setText("" + subto);
															}else{
																sm_subTotal.setVisibility(View.VISIBLE);
																sm_subTotal_inclusive.setVisibility(View.GONE);
															}
														}
													}else{
														Toast.makeText(getApplicationContext(),"Please Enter Carton Price",Toast.LENGTH_SHORT).show();
													}
												}else{
													if(price !=0){
														SOTDatabase.updateProductForInvoice(
																codeStr, nameStr, cartonQty,
																looseQty, qty, foc, price,
																discount, uomStr, cartonPerQty,
																tt + dis, tax, sb_ttl, netT,
																Integer.parseInt(edit_ssno),
																cpriceStr, exQtyStr,slret);

														cursor.requery();
														Toast.makeText(InvoiceSummary.this,
																"Updated", Toast.LENGTH_SHORT)
																.show();

														smTax = SOTDatabase.getTax();
														String ProdTax = fourDecimalPoint(smTax);
														sm_tax.setText("" + ProdTax);

														sbTtl = SOTDatabase.getSubTotal();
														String sub = twoDecimalPoint(sbTtl);

														Log.d("sbTtl","-->"+sbTtl+"smTax"+smTax);

														sm_subTotal.setText("" + sub);

														cQtyTtl = SOTDatabase.getTotalCqty();
														int tot_cqty = (int) cQtyTtl;
														sm_totl_cqty.setText("" + tot_cqty);

														lQtyTtl = SOTDatabase.getTotalLqty();
														int tot_lqty = (int) lQtyTtl;
														sm_totl_lqty.setText("" + tot_lqty);

														qtyTtl = SOTDatabase.getTotalQty();
														int tot_qty = (int) qtyTtl;
														sm_totl_qty.setText("" + qtyTtl);

														String taxType = SalesOrderSetGet
																.getCompanytax();

														if (taxType != null
																&& !taxType.isEmpty()) {
															if (taxType.matches("I")
																	|| taxType.matches("Z")) {
																netTotal = sbTtl;
															} else {
																netTotal = sbTtl + smTax;
															}
														} else {
															netTotal = sbTtl + smTax;
														}

														String ProdNettotal = twoDecimalPoint(netTotal);
														sm_netTotal.setText("" + ProdNettotal);

														// Added New 03.04.2017
														if (taxType != null && !taxType.isEmpty()) {
															if (taxType.matches("I")) {
																sm_subTotal.setVisibility(View.GONE);
																sm_subTotal_inclusive.setVisibility(View.VISIBLE);
																double subt = netTotal - smTax;
																String subto = twoDecimalPoint(subt);
																sm_subTotal_inclusive.setText("" + subto);
															}else{
																sm_subTotal.setVisibility(View.VISIBLE);
																sm_subTotal_inclusive.setVisibility(View.GONE);
															}
														}
													}else{
														Toast.makeText(getApplicationContext(),"Please Enter Price",Toast.LENGTH_SHORT).show();
													}
												}
											}else if (cartonPerQty > 1){
												Log.d("column_price2",cprices+","+cartonPerQty+"price:"+price);
												if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
													if(cprices !=0 && price !=0){
														SOTDatabase.updateProductForInvoice(
																codeStr, nameStr, cartonQty,
																looseQty, qty, foc, price,
																discount, uomStr, cartonPerQty,
																tt + dis, tax, sb_ttl, netT,
																Integer.parseInt(edit_ssno),
																cpriceStr, exQtyStr,slret);

														cursor.requery();
														Toast.makeText(InvoiceSummary.this,
																"Updated", Toast.LENGTH_SHORT)
																.show();

														smTax = SOTDatabase.getTax();
														String ProdTax = fourDecimalPoint(smTax);
														sm_tax.setText("" + ProdTax);

														sbTtl = SOTDatabase.getSubTotal();
														String sub = twoDecimalPoint(sbTtl);

														Log.d("sbTtl","-->"+sbTtl+"smTax"+smTax);

														sm_subTotal.setText("" + sub);

														cQtyTtl = SOTDatabase.getTotalCqty();
														int tot_cqty = (int) cQtyTtl;
														sm_totl_cqty.setText("" + tot_cqty);

														lQtyTtl = SOTDatabase.getTotalLqty();
														int tot_lqty = (int) lQtyTtl;
														sm_totl_lqty.setText("" + tot_lqty);

														qtyTtl = SOTDatabase.getTotalQty();
														int tot_qty = (int) qtyTtl;
														sm_totl_qty.setText("" + qtyTtl);

														String taxType = SalesOrderSetGet
																.getCompanytax();

														if (taxType != null
																&& !taxType.isEmpty()) {
															if (taxType.matches("I")
																	|| taxType.matches("Z")) {
																netTotal = sbTtl;
															} else {
																netTotal = sbTtl + smTax;
															}
														} else {
															netTotal = sbTtl + smTax;
														}

														String ProdNettotal = twoDecimalPoint(netTotal);
														sm_netTotal.setText("" + ProdNettotal);

														// Added New 03.04.2017
														if (taxType != null && !taxType.isEmpty()) {
															if (taxType.matches("I")) {
																sm_subTotal.setVisibility(View.GONE);
																sm_subTotal_inclusive.setVisibility(View.VISIBLE);
																double subt = netTotal - smTax;
																String subto = twoDecimalPoint(subt);
																sm_subTotal_inclusive.setText("" + subto);
															}else{
																sm_subTotal.setVisibility(View.VISIBLE);
																sm_subTotal_inclusive.setVisibility(View.GONE);
															}
														}
													}else{
														Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
													}
												}else{
													if(price !=0){
														SOTDatabase.updateProductForInvoice(
																codeStr, nameStr, cartonQty,
																looseQty, qty, foc, price,
																discount, uomStr, cartonPerQty,
																tt + dis, tax, sb_ttl, netT,
																Integer.parseInt(edit_ssno),
																cpriceStr, exQtyStr,slret);

														cursor.requery();
														Toast.makeText(InvoiceSummary.this,
																"Updated", Toast.LENGTH_SHORT)
																.show();

														smTax = SOTDatabase.getTax();
														String ProdTax = fourDecimalPoint(smTax);
														sm_tax.setText("" + ProdTax);

														sbTtl = SOTDatabase.getSubTotal();
														String sub = twoDecimalPoint(sbTtl);

														Log.d("sbTtl","-->"+sbTtl+"smTax"+smTax);

														sm_subTotal.setText("" + sub);

														cQtyTtl = SOTDatabase.getTotalCqty();
														int tot_cqty = (int) cQtyTtl;
														sm_totl_cqty.setText("" + tot_cqty);

														lQtyTtl = SOTDatabase.getTotalLqty();
														int tot_lqty = (int) lQtyTtl;
														sm_totl_lqty.setText("" + tot_lqty);

														qtyTtl = SOTDatabase.getTotalQty();
														int tot_qty = (int) qtyTtl;
														sm_totl_qty.setText("" + qtyTtl);

														String taxType = SalesOrderSetGet
																.getCompanytax();

														if (taxType != null
																&& !taxType.isEmpty()) {
															if (taxType.matches("I")
																	|| taxType.matches("Z")) {
																netTotal = sbTtl;
															} else {
																netTotal = sbTtl + smTax;
															}
														} else {
															netTotal = sbTtl + smTax;
														}

														String ProdNettotal = twoDecimalPoint(netTotal);
														sm_netTotal.setText("" + ProdNettotal);

														// Added New 03.04.2017
														if (taxType != null && !taxType.isEmpty()) {
															if (taxType.matches("I")) {
																sm_subTotal.setVisibility(View.GONE);
																sm_subTotal_inclusive.setVisibility(View.VISIBLE);
																double subt = netTotal - smTax;
																String subto = twoDecimalPoint(subt);
																sm_subTotal_inclusive.setText("" + subto);
															}else{
																sm_subTotal.setVisibility(View.VISIBLE);
																sm_subTotal_inclusive.setVisibility(View.GONE);
															}
														}
													}else{
														Toast.makeText(getApplicationContext(),"Please Enter Price",Toast.LENGTH_SHORT).show();
													}
												}
											}
										}else if(qty ==0){
											if(foc !=0 || exch!=0){
												SOTDatabase.updateProductForInvoice(
														codeStr, nameStr, cartonQty,
														looseQty, qty, foc, price,
														discount, uomStr, cartonPerQty,
														tt + dis, tax, sb_ttl, netT,
														Integer.parseInt(edit_ssno),
														cpriceStr, exQtyStr,slret);

												cursor.requery();
												Toast.makeText(InvoiceSummary.this,
														"Updated", Toast.LENGTH_SHORT)
														.show();

												smTax = SOTDatabase.getTax();
												String ProdTax = fourDecimalPoint(smTax);
												sm_tax.setText("" + ProdTax);

												sbTtl = SOTDatabase.getSubTotal();
												String sub = twoDecimalPoint(sbTtl);

												Log.d("sbTtl","-->"+sbTtl+"smTax"+smTax);

												sm_subTotal.setText("" + sub);

												cQtyTtl = SOTDatabase.getTotalCqty();
												int tot_cqty = (int) cQtyTtl;
												sm_totl_cqty.setText("" + tot_cqty);

												lQtyTtl = SOTDatabase.getTotalLqty();
												int tot_lqty = (int) lQtyTtl;
												sm_totl_lqty.setText("" + tot_lqty);

												qtyTtl = SOTDatabase.getTotalQty();
												int tot_qty = (int) qtyTtl;
												sm_totl_qty.setText("" + qtyTtl);

												String taxType = SalesOrderSetGet
														.getCompanytax();

												if (taxType != null
														&& !taxType.isEmpty()) {
													if (taxType.matches("I")
															|| taxType.matches("Z")) {
														netTotal = sbTtl;
													} else {
														netTotal = sbTtl + smTax;
													}
												} else {
													netTotal = sbTtl + smTax;
												}

												String ProdNettotal = twoDecimalPoint(netTotal);
												sm_netTotal.setText("" + ProdNettotal);

												// Added New 03.04.2017
												if (taxType != null && !taxType.isEmpty()) {
													if (taxType.matches("I")) {
														sm_subTotal.setVisibility(View.GONE);
														sm_subTotal_inclusive.setVisibility(View.VISIBLE);
														double subt = netTotal - smTax;
														String subto = twoDecimalPoint(subt);
														sm_subTotal_inclusive.setText("" + subto);
													}else{
														sm_subTotal.setVisibility(View.VISIBLE);
														sm_subTotal_inclusive.setVisibility(View.GONE);
													}
												}
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter Quantity",Toast.LENGTH_SHORT).show();
											}
										}

									}
								}catch (Exception e){
									e.printStackTrace();
								}
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}

		} else if (item.getTitle() == "Delete") {
			contextmenu_click="Delete";
			String id = ((TextView) info.targetView.findViewById(R.id.ss_slid))
					.getText().toString();
			String code = ((TextView) info.targetView
					.findViewById(R.id.ss_prodcode)).getText().toString();
			String serlno = ((TextView) info.targetView
					.findViewById(R.id.ss_slno)).getText().toString();
			// offline check pending

			HashMap<String, String> hash = new HashMap<String, String>();
			ArrayList<String> qtyArr = new ArrayList<String>();
			String qty = "0", foc = "0", exchange = "0";

			String editinvoice = ConvertToSetterGetter.getEdit_inv_no();

			if (!editinvoice.matches("")) {

				Log.d("Delete edit offline", "Delete edit offline mode");

				qtyArr = SOTDatabase.getquantityforstock(code);

				if (!qtyArr.isEmpty()) {
					qty = qtyArr.get(0);
					foc = qtyArr.get(1);
					exchange = qtyArr.get(2);
				}

				if (exchange != null && !exchange.isEmpty()) {

				} else {
					exchange = "0";
				}

				String locationCode = SalesOrderSetGet.getLocationcode();
				double getQty = OfflineDatabase.GetQty(locationCode, code);

				hash.put("InvoiceNo", invoiceNumber);
				hash.put("ProductCode", code);
				hash.put("FOCQty", foc);
				hash.put("ExchangeQty", exchange);

				invoicedeleteArr = OfflineSetterGetter.getStockUpdateArr();
				double dqty = 0.00, stockQty = 0.0;
				if (!qty.matches("")) {
					dqty = Double.parseDouble(qty);
				}

				if (invoicedeleteArr.size() > 0) {

					for (int i = 0; i < invoicedeleteArr.size(); i++) {
						HashMap<String, String> datavalue = invoicedeleteArr
								.get(i);
						String pcode = datavalue.get("ProductCode");
						String sQty = datavalue.get("Qty");
						Log.d("sQty", " ss " + sQty);
						double dsQty = 0.00;

						if (!sQty.matches("")) {
							dsQty = Double.parseDouble(sQty);
						}

						if (pcode.matches(code)) {
							double stkQty = 0.0;
							stkQty = dsQty + dqty;
							Log.d("stkQty", " bb " + stkQty);
							datavalue.remove("Qty");
							datavalue.put("Qty", stkQty + "");
							break;
						} else {
							int size = (invoicedeleteArr.size() - 1);
							Log.d("invoicedeleteArr.size()", ".." + size);
							if (size == i) {
								stockQty = getQty + dqty;
								Log.d("stockQty", " bb " + stockQty);
								hash.put("Qty", stockQty + "");
								invoicedeleteArr.add(hash);
								break;
							}
						}
					}

				} else {
					stockQty = getQty + dqty;
					hash.put("Qty", stockQty + "");
					invoicedeleteArr.add(hash);
				}

				OfflineSetterGetter.setStockUpdateArr(invoicedeleteArr);

				Log.d("invoicedeleteArr",
						"" + OfflineSetterGetter.getStockUpdateArr());

				// OfflineDatabase.updateProductStock(invoicedeleteArr);

			} else {
				Log.d("Delete direct offline", "Delete direct offline mode");
			}

			// /////////////////////////////

			Log.d("id", id);
			SOTDatabase.deleteProduct(id);
			SOTDatabase.deleteBillDiscount(id);
			SOTDatabase.deleteProdId(id);
			SOTDatabase.deleteBatchProductSR(code, serlno);
			sm_billDisc.setText("");
			sm_billDiscPercentage.setText("");
			cursor.requery();
			ArrayList<String> snoCount = new ArrayList<String>();
			snoCount = SOTDatabase.snoCountID();
			Log.d("snocount", "" + snoCount);
			for (int i = 0; i < snoCount.size(); i++) {
				int sno = 1 + i;
				String old_slno = SOTDatabase.getProductSno(snoCount.get(i));
				HashMap<String, String> queryValues = new HashMap<String, String>();
				queryValues.put("_id", "" + snoCount.get(i));
				queryValues.put("snum", "" + sno);
				SOTDatabase.updateSNO(queryValues);
				SOTDatabase.updateBillSNO(queryValues);
				SOTDatabase.updateBatchSR_slno(old_slno, "" + sno);

			}
			if (cursor != null && cursor.getCount() > 0) {
				cursor.requery();
				tota = SOTDatabase.getTotal();
				smTax = SOTDatabase.getTax();
				String ProdTax = fourDecimalPoint(smTax);
				sm_tax.setText("" + ProdTax);

				String smtotal = twoDecimalPoint(tota);
				double tot_item_disc = SOTDatabase.getTotalItemDisc();
				String tot_itemDisc = twoDecimalPoint(tot_item_disc);
				sm_itemDisc.setText(tot_itemDisc);
				sm_total_new.setText("" + smtotal);

				sbTtl = SOTDatabase.getSubTotal();
				String sub = twoDecimalPoint(sbTtl);
				sm_subTotal.setText("" + sub);
				sm_total.setText("" + sub);

				cQtyTtl = SOTDatabase.getTotalCqty();
				int tot_cqty = (int) cQtyTtl;
				sm_totl_cqty.setText("" + tot_cqty);

				lQtyTtl = SOTDatabase.getTotalLqty();
				int tot_lqty = (int) lQtyTtl;
				sm_totl_lqty.setText("" + tot_lqty);

				qtyTtl = SOTDatabase.getTotalQty();
				int tot_qty = (int) qtyTtl;
				sm_totl_qty.setText("" + qtyTtl);

				String taxType = SalesOrderSetGet.getCompanytax();
				if (taxType != null && !taxType.isEmpty()) {
					if (taxType.matches("I") || taxType.matches("Z")) {
						netTotal = sbTtl;
					} else {
						netTotal = sbTtl + smTax;
					}
				} else {
					netTotal = sbTtl + smTax;
				}

				String ProdNettotal = twoDecimalPoint(netTotal);
				sm_netTotal.setText("" + ProdNettotal);

				// Added New 03.04.2017
				if (taxType != null && !taxType.isEmpty()) {
					if (taxType.matches("I")) {
						sm_subTotal.setVisibility(View.GONE);
						sm_subTotal_inclusive.setVisibility(View.VISIBLE);
						double subt = netTotal - smTax;
						String subto = twoDecimalPoint(subt);
						sm_subTotal_inclusive.setText("" + subto);
					}else{
						sm_subTotal.setVisibility(View.VISIBLE);
						sm_subTotal_inclusive.setVisibility(View.GONE);
					}
				}

				String billdesc = strValidate(CustomerSetterGetter.getBillDiscount());

				if(!billdesc.matches("")){
					double dbilldisc = Double.parseDouble(billdesc);
					Log.d("dbilldisc","-->"+dbilldisc);
					if(dbilldisc>0){
						sm_billDisc.setText(billdesc);
					}
				}

				Toast.makeText(InvoiceSummary.this, "Deleted",
						Toast.LENGTH_LONG).show();
			} else {
				sm_header_layout.setVisibility(View.GONE);
				save.setVisibility(View.INVISIBLE);
				barcode_edit.setVisibility(View.GONE);
				mEmpty.setVisibility(View.VISIBLE);
				sm_billDisc.setFocusable(false);
				sm_billDiscPercentage.setFocusable(false);
				sm_total.setText("");
				sm_billDisc.setText("");
				sm_billDiscPercentage.setText("");

				sm_tax.setText("");
				sm_subTotal.setText("");
				sm_netTotal.setText("");
				sm_itemDisc.setText("");
				sm_total_new.setText("");
				sm_totl_cqty.setText("");
				sm_totl_lqty.setText("");
				sm_totl_qty.setText("");

				sm_subTotal_inclusive.setText("");
			}

		} else {
			return false;
		}

		return true;

	}

	@Override
	public void onContextMenuClosed(Menu menu) {
		super.onContextMenuClosed(menu);

		if(contextmenu_click.equalsIgnoreCase("Edit") || contextmenu_click.equalsIgnoreCase("Delete")) {
			Log.d("closed matches",contextmenu_click);
		}else{
			Log.d("closednotmatches","not "+contextmenu_click);
			String billdesc = strValidate(CustomerSetterGetter.getBillDiscount());
			if (!billdesc.matches("")) {
				double dbilldisc = Double.parseDouble(billdesc);
				if (dbilldisc > 0) {
					sm_billDisc.setText(billdesc);
				}
			}
		}

	}

	public void saveAlertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setIcon(R.mipmap.ic_save);
		alertDialog.setTitle("Save");
		alertDialog.setMessage("Do you want to Save");
		LayoutInflater adbInflater = LayoutInflater.from(this);
		eulaLayout = adbInflater.inflate(R.layout.print_checkbox,
				null);
		noofprint_layout = (LinearLayout) eulaLayout
				.findViewById(R.id.noofcopieslblLayout);

		mDeliveryCheckBox = (CheckBox) eulaLayout
				.findViewById(R.id.delivery_checkbox);

		cash_checkbox = (CheckBox) eulaLayout.findViewById(R.id.cash_checkbox);
		cash_checkbox.setVisibility(View.VISIBLE);
		String receiptOnInvoice = SalesOrderSetGet.getReceiptoninvoice();

		if (receiptOnInvoice.matches("1")) {
			cash_checkbox.setChecked(true);
			cash_checkbox.setClickable(false);
			cash_checkbox.setTextColor(Color.parseColor("#cccccc"));
		} else {
			cash_checkbox.setChecked(false);
		}

		enableprint = (CheckBox) eulaLayout.findViewById(R.id.checkbox);

		CheckBox email_ch  = (CheckBox) eulaLayout.findViewById(R.id.email_ch);
		email_ch.setText("Email");

		String haveEmail = strValidate(SalesOrderSetGet.getHaveemailintegration());

		if(haveEmail.matches("1")){
			email_ch.setVisibility(View.VISIBLE);
		}else{
			email_ch.setVisibility(View.GONE);
		}

		cash_checkbox.setText("Cash Collected");
		enableprint.setText("Invoice Print");
		mDeliveryCheckBox.setText("Delivery Order Print");
		mDeliveryCheckBox.setVisibility(View.VISIBLE);
		if (offlineLayout.getVisibility() == View.GONE) {
			mDeliveryCheckBox.setVisibility(View.VISIBLE);
		} else {
			mDeliveryCheckBox.setVisibility(View.GONE);
		}

		SalesOrderSetGet.setsCollectCash("");

		if (cash_checkbox.isChecked()) {
			SalesOrderSetGet.setsCollectCash("1");
		} else {
			SalesOrderSetGet.setsCollectCash("0");
		}

		String doIninvoiceprint = FWMSSettingsDatabase.getDOPrintMode();

		if (doIninvoiceprint != null && !doIninvoiceprint.isEmpty()) {

			if (doIninvoiceprint.matches("1")) {
				mDeliveryCheckBox.setChecked(true);
			} else {
				mDeliveryCheckBox.setChecked(false);

			}
		}

		cash_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked == true) {
					SalesOrderSetGet.setsCollectCash("1");
				} else {
					SalesOrderSetGet.setsCollectCash("0");
				}

			}
		});

		email_ch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {
				if (isChecked == true) {
					emailIschecked = true;
				} else if (isChecked == false) {
					emailIschecked = false;
				}
			}
		});

		enableprint.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked == true) {
					// FWMSSettingsDatabase.init(InvoiceSummary.this);f

					noofprint_layout.setVisibility(View.VISIBLE);
					stnumber = (TextView) eulaLayout
							.findViewById(R.id.stnumber);
					stupButton = (Button) eulaLayout.findViewById(R.id.stupBtn);
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

					stdownButton = (Button) eulaLayout
							.findViewById(R.id.stdownBtn);
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
					nofcopies = Integer.parseInt(stnumber.getText().toString());
				} else if (isChecked == false) {
					noofprint_layout.setVisibility(View.GONE);

				}
			}
		});

		alertDialog.setView(eulaLayout);
		int modeid = FWMSSettingsDatabase.getModeId();
		if (modeid == 1) {
			FWMSSettingsDatabase.updateMode(1);
			enableprint.setChecked(true);
		} else {
			FWMSSettingsDatabase.updateMode(0);
			enableprint.setChecked(false);
		}
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int indexSelected) {

						String billDisc = sm_billDisc.getText().toString();
						subTot = sm_subTotal.getText().toString();
						totTax = sm_tax.getText().toString();
						netTot = sm_netTotal.getText().toString();
						tot = sm_total.getText().toString();
						if (!billDisc.matches("")) {
							double billDiscCalc = Double.parseDouble(billDisc);
							double sbtot = SOTDatabase.getsumsubTot();
							billDiscount = billDiscCalc / sbtot;
						}
						if (cursor != null && cursor.getCount() > 0) {

							String sono = ConvertToSetterGetter.getSoNo();

							if (sono != null && !sono.isEmpty()) {

								Log.d("DO No ", "-> Null ");
//								tranblocknegativestock="1";
								if (tranblocknegativestock != null
										&& !tranblocknegativestock.isEmpty()
										&& tranblocknegativestock.matches("1")) {
									int count = cursor.getCount();
									int sl_no = 1;
									if (cursor != null && cursor.getCount() > 0) {
										if (cursor.moveToFirst()) {
											do {
												double stock_avail = 0, qty_entered = 0;
												String pCode = cursor.getString(cursor
														.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
												stock_avail = SOTDatabase
														.getStockQty(pCode);
												qty_entered = SOTDatabase
														.getProductTotalQty(pCode);
												Log.d("stock_avail", "->"
														+ stock_avail);
												Log.d("qty_entered", "->"
														+ qty_entered);
												
												
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////												
												
												
											String SRslno = cursor.getString(cursor
														.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
												
												Log.d("invoice summary pCode", "->"+pCode);

//												String haveBatch = cursor.getString(cursor
//														.getColumnIndex(SOTDatabase.COLUMN_HAVE_BATCH));
//
//												String haveExpiry = cursor.getString(cursor
//														.getColumnIndex(SOTDatabase.COLUMN_HAVE_EXPIRY));
												
												ArrayList<String> batchHave = new ArrayList<String>();
												batchHave = SOTDatabase.getBatchHave(pCode);

												String haveBatch="",haveExpiry="";
												if(batchHave.size()>0){
													haveBatch = batchHave.get(0);
													haveExpiry = batchHave.get(1);
												}

//												String slCartonPerQty = batchHave.get(2);

												String haveBatchOnStockOut = SalesOrderSetGet
														.getHaveBatchOnStockOut();
//												String haveBatchOnTransferToLocation = SalesOrderSetGet
//														.getHaveBatchOnTransferToLocation();
//												haveExpiry="True";
//												haveBatch="True";
//												haveBatchOnStockOut="True";
												if (haveBatchOnStockOut.matches("True")) {
													if ((haveBatch != null && !haveBatch
															.isEmpty())
															|| (haveExpiry != null && !haveExpiry
																	.isEmpty())) {
														if (haveBatch.matches("True")
																|| haveExpiry
																		.matches("True")) {

															String totQty = SOTDatabase.getBatchQtySR(pCode,SRslno);
																																						

															if (totQty != null
																	&& !totQty
																			.isEmpty()) {

																double totqty = Double
																		.parseDouble(totQty);

																if (totqty > 0) {

																} else {

																	if (calCarton
																			.matches("0")) {

																		String tQty = SOTDatabase.getTotaBatchQtySR(pCode,SRslno);
																											
																		
																		double tqty = 0;

																		if (!tQty
																				.matches("")) {
																			tqty = Double
																					.parseDouble(tQty);
																		}

																		int tCqty = SOTDatabase.getTotalBatchCQtySR(pCode,SRslno);


																		if (tqty > 0
																				|| tCqty > 0) {

																		} else {
																			Toast.makeText(
																					InvoiceSummary.this,
																					"Please enter the batch for product code "
																							+ pCode,
																					Toast.LENGTH_LONG)
																					.show();
																			cursor.requery();
																			adapter.notifyDataSetChanged();
																			adapter.refreshAll(true);
																			break;
																		}

																	} else {

																		Toast.makeText(
																				InvoiceSummary.this,
																				"Please enter the batch for product code "
																						+ pCode,
																				Toast.LENGTH_LONG)
																				.show();
																		cursor.requery();
																		adapter.notifyDataSetChanged();
																		adapter.refreshAll(true);
																		break;
																	}
																}
															} else {

																Toast.makeText(
																		InvoiceSummary.this,
																		"No batch for the product "
																				+ pCode
																				+ " Please delete this product",
																		Toast.LENGTH_LONG)
																		.show();

																cursor.requery();
																adapter.notifyDataSetChanged();
																adapter.refreshAll(true);
																break;

															}
														} else {
															
														}
													} else {
														Log.d("No batch & No Expiry",
																"No batch & No Expiry");
													}
												} else {
													
												}
//												if (count == sl_no) {
//													AsyncCallWSSummary task = new AsyncCallWSSummary();
//													task.execute();
//												}

											
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
												
												
//												if (stock_avail < qty_entered) {
//													Toast.makeText(
//															InvoiceSummary.this,
//															"Low Stock",
//															Toast.LENGTH_SHORT)
//															.show();
//													cursor.requery();
//													adapter.refreshAll(true);
//													break;
//												}
												Log.d("sl_no", "->" + sl_no);
												if (count == sl_no) {
													if (tranblock_abovelimit
															.matches("1")) {
														AsyncCallWSGetOutstanding task = new AsyncCallWSGetOutstanding();
														task.execute();
													} else {
														AsyncCallWSSummary task = new AsyncCallWSSummary();
														task.execute();
													}
												}

												sl_no++;

											} while (cursor.moveToNext());
										}
									}

								} else {

									if (tranblock_abovelimit.matches("1")) {
										AsyncCallWSGetOutstanding task = new AsyncCallWSGetOutstanding();
										task.execute();
									} else {
										AsyncCallWSSummary task = new AsyncCallWSSummary();
										task.execute();
									}

								}

							} else {
								Log.d("DO No ", "-> Not null");
								if (tranblock_abovelimit.matches("1")) {
									AsyncCallWSGetOutstanding task = new AsyncCallWSGetOutstanding();
									task.execute();
								} else {
									AsyncCallWSSummary task = new AsyncCallWSSummary();
									task.execute();
								}
							}
						} else {
							Toast.makeText(InvoiceSummary.this,
									"No data found", Toast.LENGTH_SHORT).show();
						}
					}
				});
		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						save.setVisibility(View.VISIBLE);
						dialog.dismiss();
					}
				});

		alertDialog.show();
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
			hmemail.put("sTranNo", summaryResult);
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
				Toast.makeText(InvoiceSummary.this,"Email sent successfully",Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(InvoiceSummary.this,"Failed to sent email",Toast.LENGTH_LONG).show();
			}

		}
	}


	private class AsyncCallWSGetOutstanding extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String customerCode = SalesOrderSetGet.getCustomercode();

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					// offline
				} else {
					// online
					try {
						customer_outstanding = SalesOrderWebService
								.getCustomerOutstandingAmount(customerCode,
										"fncGetCustomer","");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else if (onlineMode.matches("False")) {

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (customer_outstanding != null && !customer_outstanding.isEmpty()) {

				StringTokenizer tokens = new StringTokenizer(
						customer_outstanding.toString(), ",");
				String outstanding = tokens.nextToken();
				credit_limit = tokens.nextToken();
				String nettotal = sm_netTotal.getText().toString();

				if (outstanding != null && !outstanding.isEmpty()
						&& credit_limit != null & !credit_limit.isEmpty()) {
					double outstndng = 0, crdt_lmt = 0, net_tot = 0, baln = 0;
					outstndng = Double.parseDouble(outstanding);
					crdt_lmt = Double.parseDouble(credit_limit);
					net_tot = Double.parseDouble(nettotal);
					baln = crdt_lmt - outstndng;

					if (baln < 0) {
						baln = 0;
					}

					if (tranblock_abovelimit.matches("1")) {

						if (crdt_lmt > 0) {
							if (outstndng + net_tot > crdt_lmt) {
								Toast.makeText(
										InvoiceSummary.this,
										"Outstanding exceeds credit limit. You have only balance $"
												+ baln, Toast.LENGTH_LONG)
										.show();
							} else {
								AsyncCallWSSummary task = new AsyncCallWSSummary();
								task.execute();
							}
						} else {
							AsyncCallWSSummary task = new AsyncCallWSSummary();
							task.execute();
						}

					}

				} else {
					AsyncCallWSSummary task = new AsyncCallWSSummary();
					task.execute();
				}
			} else {
				AsyncCallWSSummary task = new AsyncCallWSSummary();
				task.execute();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void print() throws IOException {
		mSRHeaderDetailArr = new ArrayList<>();
		salesreturnArr = new ArrayList<HashMap<String, String>>();
		final String customerCode = SalesOrderSetGet.getCustomercode();
		final String customerName = SalesOrderSetGet.getCustomername();
		final String soDate = SalesOrderSetGet.getSaleorderdate();
		final String doNo = SalesOrderSetGet.getDoNo();
		String bllDsc = sm_billDisc.getText().toString();
		final String showcartonloose = SalesOrderSetGet.getCartonpriceflag();
		if (!bllDsc.matches("")) {
		}

		final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
		try {
			if(printertype.matches("Zebra iMZ320")){
			helper.dismissProgressDialog();
			Printer printer = new Printer(InvoiceSummary.this, macaddress);
			printer.setOnCompletionListener(new Printer.OnCompletionListener() {

				@Override
				public void onCompleted() {

					if (mDeliveryCheckBox.isChecked()) {
						Printer printer1 = new Printer(InvoiceSummary.this,
								macaddress);
						printer1.setOnCompletionListener(new Printer.OnCompletionListener() {
							@Override
							public void onCompleted() {

								finish();
								if (header.matches("InvoiceHeader")) {
									intent = new Intent(InvoiceSummary.this,
											InvoiceHeader.class);

								} else if (header
										.matches("DeliveryOrderHeader")) {
									intent = new Intent(InvoiceSummary.this,
											DeliveryOrderHeader.class);

								} else if (header.matches("SalesOrderHeader")) {
									intent = new Intent(InvoiceSummary.this,
											SalesOrderHeader.class);

								} else if (header.matches("CustomerHeader")) {
									intent = new Intent(InvoiceSummary.this,
											CustomerListActivity.class);
								} else if (header.matches("RouteHeader")) {
									intent = new Intent(InvoiceSummary.this,
											RouteHeader.class);
								} else if (header.matches("ConsignmentHeader")) {
									intent = new Intent(InvoiceSummary.this, ConsignmentHeader.class);
								}else {
									intent = new Intent(InvoiceSummary.this,
											LandingActivity.class);

								}
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
								InvoiceSummary.this.finish();
								SOTDatabase.deleteImage();

								save.setVisibility(View.INVISIBLE);
								SOTDatabase.deleteAllProduct();
								mListView.setAdapter(null);
								cursor.requery();
								barcode_edit.setVisibility(View.GONE);
								mEmpty.setVisibility(View.VISIBLE);
								sm_total.setText("");
								sm_billDisc.setText("");
								sm_billDiscPercentage.setText("");
								CustomerSetterGetter.setBillDiscount("");
								sm_tax.setText("");
								sm_billDisc.setFocusable(false);
								sm_billDiscPercentage.setFocusable(false);
								sm_header_layout.setVisibility(View.GONE);
								sm_subTotal.setText("");
								sm_netTotal.setText("");
								sm_totl_cqty.setText("");
								sm_totl_lqty.setText("");
								sm_totl_qty.setText("");

								sm_subTotal_inclusive.setText("");

								SOTDatabase.deleteBarcode();

								SOTDatabase.deleteBillDisc();
								SalesOrderSetGet.setCustomercode("");
								SalesOrderSetGet.setCustomerTaxPerc("");
								SalesOrderSetGet.setCustomername("");
							}
						});
						try {

							String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
							if(printertype.matches("Zebra iMZ320")) {
								printer1.printDeliveryOnInvoice(summaryResult,
										soDate, customerCode, customerName,
										product, productdet, printsortHeader,
										gnrlStngs, 1, product_batchArr, footerArr);
							}
					/*	else if(printertype.matches("4 Inch Bluetooth")){
								printStr = "DeliveryOnInvoice";
								if (BluetoothAdapter.checkBluetoothAddress(macaddress))
								{
									BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
									// Attempt to connect to the device
									GlobalData.mService.setHandler(mHandler);
									GlobalData.mService.connect(device,true);

								}

							}*/

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {

						finish();
						if (header.matches("InvoiceHeader")) {
							intent = new Intent(InvoiceSummary.this,
									InvoiceHeader.class);

						} else if (header.matches("DeliveryOrderHeader")) {
							intent = new Intent(InvoiceSummary.this,
									DeliveryOrderHeader.class);

						} else if (header.matches("SalesOrderHeader")) {
							intent = new Intent(InvoiceSummary.this,
									SalesOrderHeader.class);

						} else if (header.matches("CustomerHeader")) {
							intent = new Intent(InvoiceSummary.this,
									CustomerListActivity.class);
						} else if (header.matches("RouteHeader")) {
							intent = new Intent(InvoiceSummary.this,
									RouteHeader.class);
						} else if (header.matches("ConsignmentHeader")) {
							intent = new Intent(InvoiceSummary.this, ConsignmentHeader.class);
						}else {
							intent = new Intent(InvoiceSummary.this,
									LandingActivity.class);

						}
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						InvoiceSummary.this.finish();
						SOTDatabase.deleteImage();

						save.setVisibility(View.INVISIBLE);
						SOTDatabase.deleteAllProduct();
						mListView.setAdapter(null);
						cursor.requery();
						barcode_edit.setVisibility(View.GONE);
						mEmpty.setVisibility(View.VISIBLE);
						sm_total.setText("");
						sm_billDisc.setText("");
						sm_billDiscPercentage.setText("");
						CustomerSetterGetter.setBillDiscount("");
						sm_tax.setText("");
						sm_billDisc.setFocusable(false);
						sm_billDiscPercentage.setFocusable(false);
						sm_header_layout.setVisibility(View.GONE);
						sm_subTotal.setText("");
						sm_netTotal.setText("");
						sm_totl_cqty.setText("");
						sm_totl_lqty.setText("");
						sm_totl_qty.setText("");
						sm_subTotal_inclusive.setText("");

						SOTDatabase.deleteBarcode();

						SOTDatabase.deleteBillDisc();
						SalesOrderSetGet.setCustomercode("");
						SalesOrderSetGet.setCustomerTaxPerc("");
						SalesOrderSetGet.setCustomername("");

					}

				}
			});
			stnumber = (TextView) eulaLayout
						.findViewById(R.id.stnumber);
			nofcopies = Integer.parseInt(stnumber.getText().toString());
			
			if (cash_checkbox.isChecked()) {
				printer.printReceipt(customerCode, customerName, ReceiptNo,
						soDate, productdetReceipt, printsortHeader, gnrlStngs,
						nofcopies, true, footerArr,salesreturnArr,mSRHeaderDetailArr);
			} else {
				if (enableprint.isChecked()) {
						printer.printInvoice(summaryResult, soDate, customerCode,
								customerName, product, productdet, printsortHeader,
								gnrlStngs, nofcopies, product_batchArr, footerArr,"","","",doNo);
					
				} else if (mDeliveryCheckBox.isChecked()) {
					mDeliveryCheckBox.setChecked(false);
										
						printer.printDeliveryOnInvoice(summaryResult, soDate,
								customerCode, customerName, product, productdet,
								printsortHeader, gnrlStngs, 1, product_batchArr,
								footerArr);
					
				}
			}
			}else if(printertype.matches("4 Inch Bluetooth")){

				String macAddress = FWMSSettingsDatabase.getPrinterAddress();
				helper.showProgressDialog(InvoiceSummary.this.getString(R.string.print),
						InvoiceSummary.this.getString(R.string.creating_file_for_printing));
				if (BluetoothAdapter.checkBluetoothAddress(macAddress))
				{
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macAddress);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device,true);
				}
				helper.dismissProgressDialog();
			}
			else if(printertype.matches("3 Inch Bluetooth Generic")){
				helper.dismissProgressDialog();
				try {
					final CubePrint print = new CubePrint(InvoiceSummary.this,macaddress);
					print.initGenericPrinter();
					print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
						@Override
						public void initCompleted() {
							if (cash_checkbox.isChecked()) {
								nofcopies = Integer.parseInt(stnumber.getText().toString());
								Log.d("cash_checkbox","-->cash_checkbox");
								print.printInvoice(summaryResult, soDate, customerCode,
										customerName, product, productdet, printsortHeader,
										gnrlStngs, nofcopies, product_batchArr, footerArr,"");
							} else {

								if (enableprint.isChecked()) {
									nofcopies = Integer.parseInt(stnumber.getText().toString());
									Log.d("invoice_check","-->invoice_check");
									print.printInvoice(summaryResult, soDate, customerCode,
											customerName, product, productdet, printsortHeader,
											gnrlStngs, nofcopies, product_batchArr, footerArr,"");
								}
								else if (mDeliveryCheckBox.isChecked())
								{
									Log.d("mDeliveryCheck","-->mDeliveryCheck");
									print.DeliveryOnInvoicePrint(summaryResult, soDate,
											customerCode, customerName, product, productdet,
											printsortHeader, gnrlStngs, 1, product_batchArr,
											footerArr);

								}
							}

							print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
								@Override
								public void onCompleted() {
									helper.showLongToast(R.string.printed_successfully);
									finish();
									if (header.matches("InvoiceHeader")) {
										intent = new Intent(InvoiceSummary.this,
												InvoiceHeader.class);

									} else if (header.matches("DeliveryOrderHeader")) {
										intent = new Intent(InvoiceSummary.this,
												DeliveryOrderHeader.class);

									} else if (header.matches("SalesOrderHeader")) {
										intent = new Intent(InvoiceSummary.this,
												SalesOrderHeader.class);

									} else if (header.matches("CustomerHeader")) {
										intent = new Intent(InvoiceSummary.this,
												CustomerListActivity.class);
									} else if (header.matches("RouteHeader")) {
										intent = new Intent(InvoiceSummary.this,
												RouteHeader.class);
									} else if (header.matches("ConsignmentHeader")) {
										intent = new Intent(InvoiceSummary.this, ConsignmentHeader.class);
									}else {
										intent = new Intent(InvoiceSummary.this,
												LandingActivity.class);

									}
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
									InvoiceSummary.this.finish();
									SOTDatabase.deleteImage();

									save.setVisibility(View.INVISIBLE);
									SOTDatabase.deleteAllProduct();
									mListView.setAdapter(null);
									cursor.requery();
									barcode_edit.setVisibility(View.GONE);
									mEmpty.setVisibility(View.VISIBLE);
									sm_total.setText("");
									sm_billDisc.setText("");
									sm_billDiscPercentage.setText("");
									CustomerSetterGetter.setBillDiscount("");
									sm_tax.setText("");
									sm_billDisc.setFocusable(false);
									sm_billDiscPercentage.setFocusable(false);
									sm_header_layout.setVisibility(View.GONE);
									sm_subTotal.setText("");
									sm_netTotal.setText("");
									sm_totl_cqty.setText("");
									sm_totl_lqty.setText("");
									sm_totl_qty.setText("");
									sm_subTotal_inclusive.setText("");
									SOTDatabase.deleteBarcode();

									SOTDatabase.deleteBillDisc();
									SalesOrderSetGet.setCustomercode("");
									SalesOrderSetGet.setCustomerTaxPerc("");
									SalesOrderSetGet.setCustomername("");
								}
							});
						}
					});

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(printertype.matches("Zebra iMZ320 4 Inch")){
				helper.dismissProgressDialog();
				PrinterZPL printer = new PrinterZPL(InvoiceSummary.this, macaddress);
				printer.setOnCompletionListener(new PrinterZPL.OnCompletionListener() {

					@Override
					public void onCompleted() {

						if (mDeliveryCheckBox.isChecked()) {
							PrinterZPL printer1 = new PrinterZPL(InvoiceSummary.this,
									macaddress);
							printer1.setOnCompletionListener(new PrinterZPL.OnCompletionListener() {
								@Override
								public void onCompleted() {

									finish();
									if (header.matches("InvoiceHeader")) {
										intent = new Intent(InvoiceSummary.this,
												InvoiceHeader.class);

									} else if (header
											.matches("DeliveryOrderHeader")) {
										intent = new Intent(InvoiceSummary.this,
												DeliveryOrderHeader.class);

									} else if (header.matches("SalesOrderHeader")) {
										intent = new Intent(InvoiceSummary.this,
												SalesOrderHeader.class);

									} else if (header.matches("CustomerHeader")) {
										intent = new Intent(InvoiceSummary.this,
												CustomerListActivity.class);
									} else if (header.matches("RouteHeader")) {
										intent = new Intent(InvoiceSummary.this,
												RouteHeader.class);
									} else if (header.matches("ConsignmentHeader")) {
										intent = new Intent(InvoiceSummary.this, ConsignmentHeader.class);
									}else {
										intent = new Intent(InvoiceSummary.this,
												LandingActivity.class);

									}
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
									InvoiceSummary.this.finish();
									SOTDatabase.deleteImage();

									save.setVisibility(View.INVISIBLE);
									SOTDatabase.deleteAllProduct();
									mListView.setAdapter(null);
									cursor.requery();
									barcode_edit.setVisibility(View.GONE);
									mEmpty.setVisibility(View.VISIBLE);
									sm_total.setText("");
									sm_billDisc.setText("");
									sm_billDiscPercentage.setText("");
									CustomerSetterGetter.setBillDiscount("");
									sm_tax.setText("");
									sm_billDisc.setFocusable(false);
									sm_billDiscPercentage.setFocusable(false);
									sm_header_layout.setVisibility(View.GONE);
									sm_subTotal.setText("");
									sm_netTotal.setText("");
									sm_totl_cqty.setText("");
									sm_totl_lqty.setText("");
									sm_totl_qty.setText("");
									sm_subTotal_inclusive.setText("");
									SOTDatabase.deleteBarcode();

									SOTDatabase.deleteBillDisc();
									SalesOrderSetGet.setCustomercode("");
									SalesOrderSetGet.setCustomerTaxPerc("");
									SalesOrderSetGet.setCustomername("");
								}
							});
							try {

								//String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
								//if(printertype.matches("Zebra iMZ320")) {
									printer1.printDeliveryOnInvoice(summaryResult,
											soDate, customerCode, customerName,
											product, productdet, printsortHeader,
											gnrlStngs, 1, product_batchArr, footerArr);
								//}
					/*	else if(printertype.matches("4 Inch Bluetooth")){
								printStr = "DeliveryOnInvoice";
								if (BluetoothAdapter.checkBluetoothAddress(macaddress))
								{
									BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
									// Attempt to connect to the device
									GlobalData.mService.setHandler(mHandler);
									GlobalData.mService.connect(device,true);

								}

							}*/

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {

							finish();
							if (header.matches("InvoiceHeader")) {
								intent = new Intent(InvoiceSummary.this,
										InvoiceHeader.class);

							} else if (header.matches("DeliveryOrderHeader")) {
								intent = new Intent(InvoiceSummary.this,
										DeliveryOrderHeader.class);

							} else if (header.matches("SalesOrderHeader")) {
								intent = new Intent(InvoiceSummary.this,
										SalesOrderHeader.class);

							} else if (header.matches("CustomerHeader")) {
								intent = new Intent(InvoiceSummary.this,
										CustomerListActivity.class);
							} else if (header.matches("RouteHeader")) {
								intent = new Intent(InvoiceSummary.this,
										RouteHeader.class);
							} else if (header.matches("ConsignmentHeader")) {
								intent = new Intent(InvoiceSummary.this, ConsignmentHeader.class);
							}else {
								intent = new Intent(InvoiceSummary.this,
										LandingActivity.class);

							}
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							InvoiceSummary.this.finish();
							SOTDatabase.deleteImage();

							save.setVisibility(View.INVISIBLE);
							SOTDatabase.deleteAllProduct();
							mListView.setAdapter(null);
							cursor.requery();
							barcode_edit.setVisibility(View.GONE);
							mEmpty.setVisibility(View.VISIBLE);
							sm_total.setText("");
							sm_billDisc.setText("");
							sm_billDiscPercentage.setText("");
							CustomerSetterGetter.setBillDiscount("");
							sm_tax.setText("");
							sm_billDisc.setFocusable(false);
							sm_billDiscPercentage.setFocusable(false);
							sm_header_layout.setVisibility(View.GONE);
							sm_subTotal.setText("");
							sm_netTotal.setText("");
							sm_totl_cqty.setText("");
							sm_totl_lqty.setText("");
							sm_totl_qty.setText("");
							sm_subTotal_inclusive.setText("");
							SOTDatabase.deleteBarcode();

							SOTDatabase.deleteBillDisc();
							SalesOrderSetGet.setCustomercode("");
							SalesOrderSetGet.setCustomerTaxPerc("");
							SalesOrderSetGet.setCustomername("");

						}

					}
				});

				nofcopies = Integer.parseInt(stnumber.getText().toString());

				if (cash_checkbox.isChecked()) {
					printer.printReceipt(customerCode, customerName, ReceiptNo,
							soDate, productdetReceipt, printsortHeader, gnrlStngs,
							nofcopies, true, footerArr,salesreturnArr);
				} else {
					if (enableprint.isChecked()) {
						printer.printInvoice(summaryResult, soDate, customerCode,
								customerName, product, productdet, printsortHeader,
								gnrlStngs, nofcopies, product_batchArr, footerArr);

					} else if (mDeliveryCheckBox.isChecked()) {
						mDeliveryCheckBox.setChecked(false);

						printer.printDeliveryOnInvoice(summaryResult, soDate,
								customerCode, customerName, product, productdet,
								printsortHeader, gnrlStngs, 1, product_batchArr,
								footerArr);

					}
				}

				/*try {
					final CubePrintZPL print = new CubePrintZPL(InvoiceSummary.this,macaddress);
					print.printZPL();
					print.setInitCompletionListener(new CubePrintZPL.InitCompletionListener() {
						@Override
						public void initCompleted() {
							if (cash_checkbox.isChecked()) {
								nofcopies = Integer.parseInt(stnumber.getText().toString());
								Log.d("cash_checkbox","-->cash_checkbox");
								print.printReceipt(customerCode, customerName, ReceiptNo,
										soDate, productdetReceipt, printsortHeader, gnrlStngs,
										nofcopies, true, footerArr,2,salesreturnArr);
							} else {

								if (enableprint.isChecked()) {
									nofcopies = Integer.parseInt(stnumber.getText().toString());
									Log.d("invoice_check","-->invoice_check");
									print.printInvoice(summaryResult, soDate, customerCode,
											customerName, product, productdet, printsortHeader,
											gnrlStngs, nofcopies, product_batchArr, footerArr);
								}
								else if (mDeliveryCheckBox.isChecked())
								{
									Log.d("mDeliveryCheck","-->mDeliveryCheck");
									print.DeliveryOnInvoicePrint(summaryResult, soDate,
											customerCode, customerName, product, productdet,
											printsortHeader, gnrlStngs, 1, product_batchArr,
											footerArr);

								}
							}

							print.setOnCompletedListener(new CubePrintZPL.OnCompletedListener() {
								@Override
								public void onCompleted() {
									helper.showLongToast(R.string.printed_successfully);
									finish();
									if (header.matches("InvoiceHeader")) {
										intent = new Intent(InvoiceSummary.this,
												InvoiceHeader.class);

									} else if (header.matches("DeliveryOrderHeader")) {
										intent = new Intent(InvoiceSummary.this,
												DeliveryOrderHeader.class);

									} else if (header.matches("SalesOrderHeader")) {
										intent = new Intent(InvoiceSummary.this,
												SalesOrderHeader.class);

									} else if (header.matches("CustomerHeader")) {
										intent = new Intent(InvoiceSummary.this,
												CustomerListActivity.class);
									} else if (header.matches("RouteHeader")) {
										intent = new Intent(InvoiceSummary.this,
												RouteHeader.class);
									} else {
										intent = new Intent(InvoiceSummary.this,
												LandingActivity.class);

									}
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
									InvoiceSummary.this.finish();
									SOTDatabase.deleteImage();

									save.setVisibility(View.INVISIBLE);
									SOTDatabase.deleteAllProduct();
									mListView.setAdapter(null);
									cursor.requery();
									barcode_edit.setVisibility(View.GONE);
									mEmpty.setVisibility(View.VISIBLE);
									sm_total.setText("");
									sm_billDisc.setText("");
									sm_billDiscPercentage.setText("");
									sm_tax.setText("");
									sm_billDisc.setFocusable(false);
									sm_billDiscPercentage.setFocusable(false);
									sm_header_layout.setVisibility(View.GONE);
									sm_subTotal.setText("");
									sm_netTotal.setText("");
									sm_totl_cqty.setText("");
									sm_totl_lqty.setText("");
									sm_totl_qty.setText("");

									SOTDatabase.deleteBarcode();

									SOTDatabase.deleteBillDisc();
									SalesOrderSetGet.setCustomercode("");
									SalesOrderSetGet.setCustomername("");
								}
							});
						}
					});


				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			*/}
		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		} catch (Exception e) {
			e.printStackTrace();
			helper.showLongToast("Error while printing");
		}

	}

	private class AsyncGeneralSettings extends AsyncTask<Void, Void, Void> {
		String dialogStatus;

		@Override
		protected void onPreExecute() {
			jsonString = "";
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus.matches("true")) {
						Log.d("CheckOffline Alert -->", "True");
						// offlineLayout.setVisibility(View.VISIBLE);
						// offlineLayout.setBackgroundResource(R.drawable.temp_offline_pattern_bg);
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

				String result = offlineDatabase.getGeneralSettingsValue();
				jsonString = " { JsonArray: " + result + "}";

			}
			Log.d("jsonString ", jsonString);

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("JsonArray");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*********** Process each JSON Node ************/
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject jsonChildNode;

				try {
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
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		String dialogStatus = "", catjsonString = "", scatjsonString = "",
				custjsonStr = "";

		@Override
		protected void onPreExecute() {
			sortproduct.clear();
			dialogStatus = checkInternetStatus();
			catjsonString = "";
			scatjsonString = "";
			custjsonStr = "";
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {

				String cmpnyCode = SupplierSetterGetter
						.getCompanyCode();
				String customerCode = SalesOrderSetGet
						.getCustomercode();

				HashMap<String, String> custhash = new HashMap<String, String>();

				custhash.put("CompanyCode", cmpnyCode);
				custhash.put("CustomerCode", customerCode);
				
				if (onlineMode.matches("True")) {
					if (checkOffline == true) { // Temp Offline
						if (dialogStatus.matches("true")) {
							if (gnrlStngs.matches("C")) {
								String result = offlineDatabase.getCategory();
								catjsonString = " { JsonArray: " + result + "}";
							} else if (gnrlStngs.matches("S")) {
								String result = offlineDatabase
										.getSubCategory();
								scatjsonString = " { JsonArray: " + result
										+ "}";
							}
							
							custjsonStr = OfflineDatabase.getCustomersList(
									custhash);
							if (custjsonStr != null && !custjsonStr.isEmpty()) {

								custjsonResp = new JSONObject(custjsonStr);

								custjsonMainNode = custjsonResp
										.optJSONArray("JsonArray");
							}
							
						} else {
							Log.d("CheckOffline Alert -->", "False");
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
							//finish();
						}
					} else { // Online
						if (gnrlStngs.matches("C")) {
							catjsonString = WebServiceClass
									.URLService("fncGetCategory");
						} else if (gnrlStngs.matches("S")) {
							scatjsonString = WebServiceClass
									.URLService("fncGetSubCategory");
						}

						custjsonStr = SalesOrderWebService.getSODetail(
								custhash, "fncGetCustomer");
						
						if (custjsonStr != null && !custjsonStr.isEmpty()) {

							custjsonResp = new JSONObject(custjsonStr);

							custjsonMainNode = custjsonResp
									.optJSONArray("SODetails");
						}

					}
				} else if (onlineMode.matches("False")) { // Permnt Offline
					if (gnrlStngs.matches("C")) {
						String result = offlineDatabase.getCategory();
						catjsonString = " { JsonArray: " + result + "}";
					} else if (gnrlStngs.matches("S")) {
						String result = offlineDatabase.getSubCategory();
						scatjsonString = " { JsonArray: " + result + "}";
					}
					
					custjsonStr = OfflineDatabase.getCustomersList(
							custhash);
					if (custjsonStr != null && !custjsonStr.isEmpty()) {

						custjsonResp = new JSONObject(custjsonStr);

						custjsonMainNode = custjsonResp
								.optJSONArray("JsonArray");
					}
				}

				/** Retrive data from JSON String start **/

				try {
					Log.d("custjsonStr ", custjsonStr);
					int custJsonArr = custjsonMainNode.length();
					if (custJsonArr>0) {
//
//						custjsonResp = new JSONObject(custjsonStr);
//
//						custjsonMainNode = custjsonResp
//								.optJSONArray("SODetails");

						
						for (int i = 0; i < custJsonArr; i++) {

							JSONObject jsonChildNode;

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
							String PhoneNo = jsonChildNode.optString("PhoneNo")
									.toString();
							String HandphoneNo = jsonChildNode.optString(
									"HandphoneNo").toString();
							String Email = jsonChildNode.optString("Email")
									.toString();
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
							CustomerSetterGetter
									.setTotalOutstanding(OutstandingAmount);


						}
					}

				} catch (Exception e) {

					e.printStackTrace();
				}

				if (gnrlStngs.matches("C")) {

					Log.d("catjsonString ", catjsonString);

					try {

						jsonResponse = new JSONObject(catjsonString);
						jsonMainNode = jsonResponse.optJSONArray("JsonArray");

						/*********** Process each JSON Node ************/
						int lengthJsonArr = jsonMainNode.length();
						for (int i = 0; i < lengthJsonArr; i++) {
							/****** Get Object for each JSON node. ***********/
							JSONObject jsonChildNode;

							jsonChildNode = jsonMainNode.getJSONObject(i);

							String categorycode = jsonChildNode.optString(
									"CategoryCode").toString();

							sortproduct.add(categorycode);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (gnrlStngs.matches("S")) {

					try {
						Log.d("scatjsonString ", scatjsonString);

						jsonResponse = new JSONObject(scatjsonString);
						jsonSecNode = jsonResponse.optJSONArray("JsonArray");

						/*********** Process each JSON Node ************/
						int lengJsonArr = jsonSecNode.length();
						for (int i = 0; i < lengJsonArr; i++) {
							/****** Get Object for each JSON node. ***********/
							JSONObject jsonChildNode;

							jsonChildNode = jsonSecNode.getJSONObject(i);

							String subcategorycode = jsonChildNode.optString(
									"SubCategoryCode").toString();

							sortproduct.add(subcategorycode);

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				/** Retrive data from JSON String End **/

			} catch (Exception e) {
				e.printStackTrace();
			}

			// if (gnrlStngs.matches("C")) {
			//
			// if (onlineMode.matches("True")) {
			// if (checkOffline == true) {
			// Log.d("DialogStatus", "" + dialogStatus);
			//
			// if (dialogStatus.matches("true")) {
			// Log.d("CheckOffline Alert -->", "True");
			// String result = offlineDatabase.getCategory();
			// jsonString = " { JsonArray: " + result + "}";
			// } else {
			// Log.d("CheckOffline Alert -->", "False");
			// finish();
			// }
			//
			// } else {
			// // online
			// Log.d("checkOffline Status -->", "False");
			// jsonString = WebServiceClass
			// .URLService("fncGetCategory");
			//
			// String cmpnyCode = SupplierSetterGetter
			// .getCompanyCode();
			// String customerCode = SalesOrderSetGet
			// .getCustomercode();
			//
			// HashMap<String, String> custhash = new HashMap<String, String>();
			//
			// custhash.put("CompanyCode", cmpnyCode);
			// custhash.put("CustomerCode", customerCode);
			//
			// custjsonStr = SalesOrderWebService.getSODetail(
			// custhash, "fncGetCustomer");
			//
			//
			//
			// }
			//
			// } else if (onlineMode.matches("False")) {
			// Log.d("Customer Online Mode -->", onlineMode);
			// String result = offlineDatabase.getCategory();
			// jsonString = " { JsonArray: " + result + "}";
			//
			// }
			//
			// Log.d("jsonString ", jsonString);
			//
			// try {
			//
			// jsonResponse = new JSONObject(jsonString);
			// jsonMainNode = jsonResponse.optJSONArray("JsonArray");
			//
			// /*********** Process each JSON Node ************/
			// int lengthJsonArr = jsonMainNode.length();
			// for (int i = 0; i < lengthJsonArr; i++) {
			// /****** Get Object for each JSON node. ***********/
			// JSONObject jsonChildNode;
			//
			// jsonChildNode = jsonMainNode.getJSONObject(i);
			//
			// String categorycode = jsonChildNode.optString(
			// "CategoryCode").toString();
			//
			// sortproduct.add(categorycode);
			// }
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// } else if (gnrlStngs.matches("S")) {
			//
			// if (onlineMode.matches("True")) {
			// if (checkOffline == true) {
			// Log.d("DialogStatus", "" + dialogStatus);
			//
			// if (dialogStatus.matches("true")) {
			// Log.d("CheckOffline Alert -->", "True");
			// String result = offlineDatabase.getSubCategory();
			// jsonStr = " { JsonArray: " + result + "}";
			// } else {
			// Log.d("CheckOffline Alert -->", "False");
			// finish();
			// }
			//
			// } else {//online
			// Log.d("checkOffline Status -->", "False");
			// jsonStr = WebServiceClass
			// .URLService("fncGetSubCategory");
			//
			// String cmpnyCode = SupplierSetterGetter
			// .getCompanyCode();
			// String customerCode = SalesOrderSetGet
			// .getCustomercode();
			//
			// HashMap<String, String> custhash = new HashMap<String, String>();
			//
			// custhash.put("CompanyCode", cmpnyCode);
			// custhash.put("CustomerCode", customerCode);
			//
			// custjsonStr = SalesOrderWebService.getSODetail(
			// custhash, "fncGetCustomer");
			//
			// Log.d("custjsonStr ", custjsonStr);
			//
			// try {
			// custjsonResp = new JSONObject(custjsonStr);
			// } catch (JSONException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			// custjsonMainNode = custjsonResp
			// .optJSONArray("SODetails");
			//
			// int custJsonArr = custjsonMainNode.length();
			// for (int i = 0; i < custJsonArr; i++) {
			//
			// JSONObject jsonChildNode;
			//
			// try {
			// jsonChildNode = custjsonMainNode
			// .getJSONObject(i);
			//
			// String CustomerCode = jsonChildNode.optString(
			// "CustomerCode").toString();
			// String CustomerName = jsonChildNode.optString(
			// "CustomerName").toString();
			// String Address1 = jsonChildNode.optString(
			// "Address1").toString();
			// String Address2 = jsonChildNode.optString(
			// "Address2").toString();
			// String Address3 = jsonChildNode.optString(
			// "Address3").toString();
			// String PhoneNo = jsonChildNode.optString(
			// "PhoneNo").toString();
			// String HandphoneNo = jsonChildNode.optString(
			// "HandphoneNo").toString();
			// String Email = jsonChildNode.optString("Email")
			// .toString();
			// String TermName = jsonChildNode.optString(
			// "TermName").toString();
			// String OutstandingAmount = jsonChildNode
			// .optString("OutstandingAmount")
			// .toString();
			//
			// CustomerSetterGetter
			// .setCustomerCode(CustomerCode);
			// CustomerSetterGetter
			// .setCustomerName(CustomerName);
			// CustomerSetterGetter
			// .setCustomerAddress1(Address1);
			// CustomerSetterGetter
			// .setCustomerAddress2(Address2);
			// CustomerSetterGetter
			// .setCustomerAddress3(Address3);
			// CustomerSetterGetter.setCustomerPhone(PhoneNo);
			// CustomerSetterGetter.setCustomerHP(HandphoneNo);
			// CustomerSetterGetter.setCustomerEmail(Email);
			// CustomerSetterGetter.setCustomerTerms(TermName);
			// CustomerSetterGetter
			// .setTotalOutstanding(OutstandingAmount);
			//
			// Log.d("mobile settings Customer code header",
			// CustomerSetterGetter.getCustomerCode());
			//
			// } catch (JSONException e) {
			//
			// e.printStackTrace();
			// }
			// }
			//
			// }
			//
			// } else if (onlineMode.matches("False")) {
			// Log.d("Customer Online Mode -->", onlineMode);
			// String result = offlineDatabase.getSubCategory();
			// jsonStr = " { JsonArray: " + result + "}";
			//
			// }
			// Log.d("jsonStr ", jsonStr);
			//
			// try {
			//
			// jsonResponse = new JSONObject(jsonStr);
			// jsonSecNode = jsonResponse.optJSONArray("JsonArray");
			//
			// /*********** Process each JSON Node ************/
			// int lengJsonArr = jsonSecNode.length();
			// for (int i = 0; i < lengJsonArr; i++) {
			// /****** Get Object for each JSON node. ***********/
			// JSONObject jsonChildNode;
			//
			// jsonChildNode = jsonSecNode.getJSONObject(i);
			//
			// String subcategorycode = jsonChildNode.optString(
			// "SubCategoryCode").toString();
			//
			// sortproduct.add(subcategorycode);
			//
			// }
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			/*
			 * AsyncPrintCall printcal = new AsyncPrintCall();
			 * printcal.execute();
			 */

			String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
			if (cash_checkbox.isChecked()) {

				if(printertype.matches("4 Inch Bluetooth")||(printertype.matches("3 Inch Bluetooth Generic"))) {
					new AsyncInvoicePrintCall().execute();
				}
				else{
					new AsyncReceiptPrintCall().execute();
				}
				/*
				 * if(mDeliveryCheckBox.isChecked()){ new
				 * AsyncInvoicePrintCall().execute(); }
				 */

			} else {
				if (mDeliveryCheckBox.isChecked() || enableprint.isChecked()) {
					new AsyncInvoicePrintCall().execute();
				}
			}
		}

	}

	public void sortCatagory() {
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

	// Invoice print call
	private class AsyncInvoicePrintCall extends
			AsyncTask<Void, Void, ArrayList<String>> {
		@Override
		protected void onPreExecute() {
			product.clear();
			productdet.clear();
			footerArr.clear();
			jsonSt = "";
			jsonSecNodefooter = null;
			jsonRespfooter = null;
		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {

			String decimalpts = ".00";
			String showcartonloose = SalesOrderSetGet
					.getCartonpriceflag();
			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("InvoiceNo", summaryResult);

			hashVl.put("CompanyCode", cmpnyCode);

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					// Offline
//					jsonString = offlineDatabase
//							.getInvoiceHeaderDetail(hashValue);
							
							if (showcartonloose.equalsIgnoreCase("1")) {
								jsonString = offlineDatabase
										.getInvoiceDetailPrint(hashValue);

							} else {
								jsonString = offlineDatabase
										.getInvoiceHeaderDetail(hashValue);
							}		
							
					jsonStr = offlineDatabase.getInvoiceHeader(hashValue);
					
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

					jsonSt = SalesOrderWebService.getSODetail(hashVl,
							"fncGetMobilePrintFooter");

					Log.d("jsonSt", "" + jsonSt);

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
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			} else if (onlineMode.matches("False")) { // perman offline
//				jsonString = offlineDatabase.getInvoiceHeaderDetail(hashValue);
				if (showcartonloose.equalsIgnoreCase("1")) {
					jsonString = offlineDatabase
							.getInvoiceDetailPrint(hashValue);

				} else {
					jsonString = offlineDatabase
							.getInvoiceHeaderDetail(hashValue);
				}
				
				jsonStr = offlineDatabase.getInvoiceHeader(hashValue);
				
			}

			/*
			 * jsonString = SalesOrderWebService.getSODetail(hashValue,
			 * "fncGetInvoiceDetail"); jsonStr =
			 * SalesOrderWebService.getSODetail(hashValue,
			 * "fncGetInvoiceHeaderByInvoiceNo");
			 */

			Log.d("jsonString ", "" + jsonString);
			Log.d("jsonStr ", "" + jsonStr);

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");

				jsonResp = new JSONObject(jsonStr);
				jsonSecNode = jsonResp.optJSONArray("SODetails");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/********
			 * Show Default carton,loose,foc,exchange qty and price Based On
			 * General settings
			 *********/
			/*********** Process each JSON Node ************/
			int lengthJsonArr = jsonMainNode.length();
			if (showcartonloose.equalsIgnoreCase("1")) {
				Log.d("showcartonloose", "if" + lengthJsonArr);
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					ProductDetails productdetail = new ProductDetails();
					try {
						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);
						String transType = jsonChildNode.optString("TranType")
								.toString();
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

						productdetail.setUOMCode(jsonChildNode
								.optString("UOMCode").toString());
						
						String uomCode = jsonChildNode
								.optString("UOMCode").toString();
						
						if(uomCode!=null && !uomCode.isEmpty()){
							
						}else{
							uomCode="";
						}
						
						 if(uomCode.matches("null")){
							 uomCode="";
						 }
						
						Log.d("uomCode", "u " +uomCode);

						if (transType.equalsIgnoreCase("Ctn")) {
							String cQty = jsonChildNode.optString("CQty")
									.toString();

							String cPrice = jsonChildNode.optString(
									"CartonPrice").toString();

							if (cQty != null && !cQty.isEmpty()
									&& cPrice != null && !cPrice.isEmpty()) {
								productdetail.setQty(cQty.split("\\.")[0]);
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

							if (lQty != null && !lQty.isEmpty()
									&& lPrice != null && !lPrice.isEmpty()) {
								productdetail.setQty(lQty.split("\\.")[0]+ " " + uomCode);
								productdetail.setPrice(twoDecimalPoint(Double
										.valueOf(lPrice)));
								double lqty = Double.valueOf(lQty);
								double lprice = Double.valueOf(lPrice);
								double total = lqty * lprice;
								String tot = twoDecimalPoint(total);
								productdetail.setTotal(tot);
							} else {
								productdetail.setQty("0"+ " " + uomCode);
								productdetail.setPrice("0.00");
								productdetail.setTotal("0.00");
							}

						} else if (transType.equalsIgnoreCase("FOC")) {
							String focQty = jsonChildNode.optString("FOCQty")
									.toString();
							if (focQty != null && !focQty.isEmpty()) {
								productdetail.setQty(focQty.split("\\.")[0]);
							} else {
								productdetail.setQty("0"+ " " + uomCode);
							}
							productdetail.setPrice("0.00");
							productdetail.setTotal("0.00");

						} else if (transType.equalsIgnoreCase("Exc")) {
							String excQty = jsonChildNode.optString(
									"ExchangeQty").toString();
							if (excQty != null && !excQty.isEmpty()) {
								productdetail.setQty(excQty.split("\\.")[0]);
							} else {
								productdetail.setQty("0"+ " " + uomCode);
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
						productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
						productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
						productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
						product.add(productdetail);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} else {
				Log.d("showcartonloose", "else" + lengthJsonArr);
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

						productdetail.setUOMCode(jsonChildNode
								.optString("UOMCode").toString());

						if (onlineMode.matches("True")) {
							if (checkOffline == true) {
								// Offline
							} else {
								// Online
								// SHow batch number
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
						// ///

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
						String focvalue = jsonChildNode.optString("FOCQty")
								.toString().split("\\.")[0];

						String ExchangeQty = jsonChildNode
								.optString("ExchangeQty").toString()
								.split("\\.")[0];

						Log.d("foc", focvalue);

						Log.d("exchange", ExchangeQty);

						if (focvalue != null && !focvalue.isEmpty()) {
							productdetail.setFocqty(Integer.valueOf(focvalue));
						} else {
							productdetail.setFocqty(0);
						}

						if (ExchangeQty != null && !ExchangeQty.isEmpty()) {
							productdetail.setExchangeqty(Integer
									.valueOf(ExchangeQty));
						} else {
							productdetail.setExchangeqty(0);
						}
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
						productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
						productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
						productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
						product.add(productdetail);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			/*********** Process each JSON Node ************/
			int lengJsonArr = jsonSecNode.length();
			for (int i = 0; i < lengJsonArr; i++) {
				/****** Get Object for each JSON node. ***********/
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
					String paidamount = jsonChildNode.optString("PaidAmount")
							.toString();

					String BalanceAmount = jsonChildNode.optString("BalanceAmount")
							.toString();

					productdetail.setPaidamount(paidamount);

					productdetail.setBalanceAmount(BalanceAmount);
					productdetail.setRemarks(jsonChildNode.optString("Remarks")
							.toString());
					productdetail.setTotaloutstanding(jsonChildNode.optString(
							"TotalBalance").toString());
					productdetail.setCreateDate(jsonChildNode.optString(
							"CreateDate").toString());

					productdet.add(productdetail);

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

			try {
				sortCatagory();
				print();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private class AsyncReceiptPrintCall extends
			AsyncTask<Void, Void, ArrayList<String>> {

		@Override
		protected void onPreExecute() {
			listarray.clear();
			footerArr.clear();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("ReceiptNo", ReceiptNo);
			hashVl.put("CompanyCode", cmpnyCode);

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					// Offline
					jsonStr = offlineDatabase.getReceiptDetail(hashValue);
					hashValue.put("FromDate", "");
					hashValue.put("ToDate", "");
					hashValue.put("User", "");
					jsonString = offlineDatabase.getReceiptHeader(hashValue);

				} else { // online

					jsonStr = SalesOrderWebService.getSODetail(hashValue,
							"fncGetReceiptDetail");
					jsonString = SalesOrderWebService.getSODetail(hashValue,
							"fncGetReceiptHeaderByReceiptNo");
					jsonSt = SalesOrderWebService.getSODetail(hashVl,
							"fncGetMobilePrintFooter");

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

							jsonChildNode = jsonSecNodefooter.getJSONObject(i);

							String ReceiptMessage = jsonChildNode.optString(
									"ReceiptMessage").toString();
							String SortOrder = jsonChildNode.optString(
									"SortOrder").toString();

							productdetail.setReceiptMessage(ReceiptMessage);
							productdetail.setSortOrder(SortOrder);
							footerArr.add(productdetail);

						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			} else if (onlineMode.matches("False")) { // perman offline

				jsonStr = offlineDatabase.getReceiptDetail(hashValue);
				hashValue.put("FromDate", "");
				hashValue.put("ToDate", "");
				hashValue.put("User", "");
				jsonString = offlineDatabase.getReceiptHeader(hashValue);

			}

			/*
			 * jsonStr = SalesOrderWebService.getSODetail(hashValue,
			 * "fncGetReceiptDetail"); jsonString =
			 * SalesOrderWebService.getSODetail(hashValue,
			 * "fncGetReceiptHeaderByReceiptNo");
			 */
			// Log.d("jsonStr ", ""+jsonStr);
			// Log.d("jsonSt", ""+jsonSt);

			try {

				jsonResp = new JSONObject(jsonStr);
				jsonSecNode = jsonResp.optJSONArray("SODetails");

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");

			} catch (JSONException e) {

				e.printStackTrace();
			}
			int lengJsonArr = jsonSecNode.length();
			for (int i = 0; i < lengJsonArr; i++) {

				JSONObject jsonChildNode;

				try {
					jsonChildNode = jsonSecNode.getJSONObject(i);

					jsonChildNode.optString("ReceiptNo").toString();
					String invoiceno = jsonChildNode.optString("InvoiceNo")
							.toString();
					jsonChildNode.optString("NetTotal").toString();
					jsonChildNode.optString("PaidAmount").toString();
					listarray.add(invoiceno);
				} catch (JSONException e) {

					e.printStackTrace();
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

					StringTokenizer tokens = new StringTokenizer(datetime, " ");
					String chequedate = tokens.nextToken();
					In_Cash.setPay_Mode(paymode);
					In_Cash.setBank_code(bankcode);
					// In_Cash.setBank_Name("");
					In_Cash.setCheck_No(chequeno);
					In_Cash.setCheck_Date(chequedate);

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
			if (listarray.isEmpty()) {
				helper.dismissProgressDialog();
			} else {
				new InvoiceProductWebservice().execute();
			}

		}

	}

	// Receipt Invoice print call
	private class InvoiceProductWebservice extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			prdctdetails.clear();
			// productdet.clear();
			productdetReceipt.clear();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String str4 = listarray.get(0);
			Log.d("InvoiceNo", str4);
			String showcartonloose = SalesOrderSetGet
					.getCartonpriceflag();
			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			hm.put("CompanyCode", cmpnyCode);
			hm.put("InvoiceNo", str4);

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
//					showcartonloose = "";

				} else { // online
					/********
					 * Show Default carton,loose,foc,exchange qty and price
					 * Based On General settings
					 *********/
					if (showcartonloose.equalsIgnoreCase("1")) {
						jsonString = SalesOrderWebService.getSODetail(hm,
								"fncGetInvoiceDetailWithCarton");

					} else {
						jsonString = SalesOrderWebService.getSODetail(hm,
								"fncGetInvoiceDetail");
					}
					jsonStr = SalesOrderWebService.getSODetail(hm,
							"fncGetInvoiceHeaderByInvoiceNo");
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
//				showcartonloose = "";
			}

			/*
			 * jsonString = SalesOrderWebService.getSODetail(hm,
			 * "fncGetInvoiceDetail"); jsonStr =
			 * SalesOrderWebService.getSODetail(hm,
			 * "fncGetInvoiceHeaderByInvoiceNo");
			 */
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

			int lengJsonArr = jsonSecNode.length();
			for (int j = 0; j < lengJsonArr; j++) {

				JSONObject jsonChildNode;
				ProductDetails productdetail = new ProductDetails();
				try {
					jsonChildNode = jsonSecNode.getJSONObject(j);
					productdetail.setItemno(jsonChildNode
							.optString("InvoiceNo").toString());
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

				/********
				 * Show Default carton,loose,foc,exchange qty and price Based On
				 * General settings
				 *********/
				int lengthJsonArr = jsonMainNode.length();

				Log.d("lengthJsonArr", "" + lengthJsonArr);

				if (showcartonloose.equalsIgnoreCase("1")) {
					Log.d("showcartonloose", "if-->" + lengthJsonArr);
					for (int i = 0; i < lengthJsonArr; i++) {
						/*** Get Object for each JSON node. *****/
						ProdDetails proddetails = new ProdDetails();
						try {
							JSONObject jsonChildNodes = jsonMainNode
									.getJSONObject(i);
							String transType = jsonChildNodes.optString(
									"TranType").toString();
							int s = i + 1;
							proddetails.setSno(String.valueOf(s));

							proddetails.setItemnum(jsonChildNodes.optString(
									"InvoiceNo").toString());
							proddetails.setItemcode(jsonChildNodes.optString(
									"ProductCode").toString());

							proddetails.setDescription(jsonChildNodes
									.optString("ProductName").toString());
							
							proddetails.setUOMCode(jsonChildNodes
									.optString("UOMCode").toString());
							
							String uomCode = jsonChildNodes
									.optString("UOMCode").toString();
							
							if(uomCode!=null && !uomCode.isEmpty()){
								
							}else{
								uomCode="";
							}
							
							 if(uomCode.matches("null")){
								 uomCode="";
							 }
							 
							Log.d("uomCode", "u " +uomCode);
							
							if (transType.equalsIgnoreCase("Ctn")) {
								String cQty = jsonChildNodes.optString("CQty")
										.toString();

								String cPrice = jsonChildNodes.optString(
										"CartonPrice").toString();

								if (cQty != null && !cQty.isEmpty()
										&& cPrice != null && !cPrice.isEmpty()) {
									proddetails.setQty(cQty.split("\\.")[0]);
									proddetails.setPrice(twoDecimalPoint(Double
											.valueOf(cPrice)));
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
									proddetails.setPrice(twoDecimalPoint(Double
											.valueOf(lPrice)));
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
								if (focQty != null && !focQty.isEmpty()) {
									proddetails.setQty(focQty.split("\\.")[0]);
								} else {
									proddetails.setQty("0"+ " " + uomCode);
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
							proddetails.setTaxType(jsonChildNodes.optString("TaxType").toString());
							proddetails.setTaxPerc(jsonChildNodes.optString("TaxPerc").toString());
							proddetails.setSubtotal(jsonChildNodes.optString("SubTotal").toString());


							prdctdetails.add(proddetails);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} else {
					Log.d("showcartonloose", "else-->" + lengthJsonArr);
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
							proddetails.setDescription(jsonChildNodes
									.optString("ProductName").toString());

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

							proddetails.setPrice(jsonChildNodes.optString(
									"Price").toString());
							proddetails.setTotal(jsonChildNodes.optString(
									"Total").toString());

							String focvalue = jsonChildNodes
									.optString("FOCQty").toString()
									.split("\\.")[0];

							String ExchangeQty = jsonChildNodes
									.optString("ExchangeQty").toString()
									.split("\\.")[0];

							Log.d("foc", focvalue);

							Log.d("exchange", ExchangeQty);

							if (focvalue != null && !focvalue.isEmpty()) {
								proddetails
										.setFocqty(Integer.valueOf(focvalue));
							} else {
								proddetails.setFocqty(0);
							}

							if (ExchangeQty != null && !ExchangeQty.isEmpty()) {
								proddetails.setExchangeqty(Integer
										.valueOf(ExchangeQty));
							} else {
								proddetails.setExchangeqty(0);
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
							proddetails.setTaxType(jsonChildNodes.optString("TaxType").toString());
							proddetails.setTaxPerc(jsonChildNodes.optString("TaxPerc").toString());
							proddetails.setSubtotal(jsonChildNodes.optString("SubTotal").toString());

							prdctdetails.add(proddetails);
						} catch (JSONException e) {

							e.printStackTrace();
						}
					}
				}
				productdetail.setProductsDetails(prdctdetails);
				productdetReceipt.add(productdetail);
			}

			// }
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			hm.clear();
			try {

				if (mDeliveryCheckBox.isChecked()) {
					new AsyncInvoicePrintCall().execute();
				} else {
					sortCatagory();
					print();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void productTotal(double qty, String slPrice, String itmDscnt,
			String taxType, String taxValue) {

		try {
			double taxAmount = 0.0, netTotal = 0.0;
			double taxAmount1 = 0.0, netTotal1 = 0.0;
			double itmDisc = 0, tt = 0;
			Prodtotal = "0";
			subTtl = "0";
			prodTax = "0";
			ProdNetTotal = "0";

			if (slPrice.matches("")) {
				slPrice = "0";
			}

			if (!slPrice.matches("")) {

				double slPriceCalc = Double.parseDouble(slPrice);

				// String itmDscnt = sl_itemDiscount.getText().toString();
				if (!itmDscnt.matches("")) {
					tt = (qty * slPriceCalc);
				} else {
					tt = qty * slPriceCalc;
				}

				Prodtotal = twoDecimalPoint(tt);

				double subTotal = 0.0;

				// String itemDisc = sl_itemDiscount.getText().toString();
				if (!itmDscnt.matches("")) {
					itmDisc = Double.parseDouble(itmDscnt);
					subTotal = tt - itmDisc;
				} else {
					subTotal = tt;
				}

				subTtl = twoDecimalPoint(subTotal);

				// sl_total.setText("" + sbTtl);

				if (!taxType.matches("") && !taxValue.matches("")) {

					double taxValueCalc = Double.parseDouble(taxValue);

					if (taxType.matches("E")) {

						if (!itmDscnt.matches("")) {
							taxAmount1 = (subTotal * taxValueCalc) / 100;
							prodTax = fourDecimalPoint(taxAmount1);

							netTotal1 = subTotal + taxAmount1;
							ProdNetTotal = twoDecimalPoint(netTotal1);

						} else {

							taxAmount = (tt * taxValueCalc) / 100;
							prodTax = fourDecimalPoint(taxAmount);

							netTotal = tt + taxAmount;
							ProdNetTotal = twoDecimalPoint(netTotal);
						}

					} else if (taxType.matches("I")) {
						if (!itmDscnt.matches("")) {
							taxAmount1 = (subTotal * taxValueCalc)
									/ (100 + taxValueCalc);
							prodTax = fourDecimalPoint(taxAmount1);

							// netTotal1 = subTotal + taxAmount1;
							netTotal1 = subTotal;
							ProdNetTotal = twoDecimalPoint(netTotal1);
						} else {
							taxAmount = (tt * taxValueCalc)
									/ (100 + taxValueCalc);
							prodTax = fourDecimalPoint(taxAmount);

							// netTotal = tt + taxAmount;
							netTotal = tt;
							ProdNetTotal = twoDecimalPoint(netTotal);
						}

					} else if (taxType.matches("Z")) {

						prodTax = "0.0";
						if (!itmDscnt.matches("")) {
							// netTotal1 = subTotal + taxAmount;
							netTotal1 = subTotal;
							ProdNetTotal = twoDecimalPoint(netTotal1);

						} else {
							// netTotal = tt + taxAmount;
							netTotal = tt;
							ProdNetTotal = twoDecimalPoint(netTotal);
						}

					} else {
						prodTax = "0.0";
						ProdNetTotal = Prodtotal;
					}

				} else if (taxValue.matches("")) {
					prodTax = "0.0";
					ProdNetTotal = Prodtotal;
				} else {
					prodTax = "0.0";
					ProdNetTotal = Prodtotal;
				}
			}
		} catch (Exception e) {

		}
	}

	public void productTotalNew(int cqty, int lqty, String cPrice,
			String lPrice, String itmDscnt, String taxType, String taxValue) {

		try {
			double taxAmount = 0.0, netTotal = 0.0;
			double taxAmount1 = 0.0, netTotal1 = 0.0;
			double tt = 0.0, itmDisc = 0.0;
			Prodtotal = "0";
			subTtl = "0";
			prodTax = "0";
			ProdNetTotal = "0";

			if (lPrice.matches("")) {
				lPrice = "0";
			}

			if (cPrice.matches("")) {
				cPrice = "0";
			}

			double lPriceCalc = Double.parseDouble(lPrice);
			double cPriceCalc = Double.parseDouble(cPrice);

			double cqtyCalc = cqty;
			double lqtyCalc = lqty;

			tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc);

			Prodtotal = twoDecimalPoint(tt);

			double subTotal = 0.0;

			String itemDisc = itmDscnt;
			if (!itemDisc.matches("")) {
				itmDisc = Double.parseDouble(itemDisc);
				subTotal = tt - itmDisc;
			} else {
				subTotal = tt;
			}

			subTtl = twoDecimalPoint(subTotal);

			if (!taxType.matches("") && !taxValue.matches("")) {

				double taxValueCalc = Double.parseDouble(taxValue);

				if (taxType.matches("E")) {

					if (!itemDisc.matches("")) {
						taxAmount1 = (subTotal * taxValueCalc) / 100;
						prodTax = fourDecimalPoint(taxAmount1);

						netTotal1 = subTotal + taxAmount1;
						ProdNetTotal = twoDecimalPoint(netTotal1);
					} else {

						taxAmount = (tt * taxValueCalc) / 100;
						prodTax = fourDecimalPoint(taxAmount);

						netTotal = tt + taxAmount;
						ProdNetTotal = twoDecimalPoint(netTotal);
					}

				} else if (taxType.matches("I")) {
					if (!itemDisc.matches("")) {
						taxAmount1 = (subTotal * taxValueCalc)
								/ (100 + taxValueCalc);
						prodTax = fourDecimalPoint(taxAmount1);

						// netTotal1 = subTotal + taxAmount;
						netTotal1 = subTotal;
						ProdNetTotal = twoDecimalPoint(netTotal1);

					} else {
						taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
						prodTax = fourDecimalPoint(taxAmount);

						// netTotal = tt + taxAmount;
						netTotal = tt;
						ProdNetTotal = twoDecimalPoint(netTotal);
					}

				} else if (taxType.matches("Z")) {

					prodTax = "0.0";
					if (!itemDisc.matches("")) {
						// netTotal1 = subTotal + taxAmount;
						netTotal1 = subTotal;
						ProdNetTotal = twoDecimalPoint(netTotal1);

					} else {
						// netTotal = tt + taxAmount;
						netTotal = tt;
						ProdNetTotal = twoDecimalPoint(netTotal);

					}

				} else {
					prodTax = "0.0";
					ProdNetTotal = Prodtotal;

				}

			} else if (taxValue.matches("")) {
				prodTax = "0.0";
				ProdNetTotal = Prodtotal;
			} else {
				prodTax = "0.0";
				ProdNetTotal = Prodtotal;
			}

		} catch (Exception e) {

		}
	}

	/** Invoice Edit **/
	public void cartonQty() {
		String crtnQty = sl_cartonQty.getText().toString();

		if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

			int cartonQtyCalc = Integer.parseInt(crtnQty);

			int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);

			int qty = 0;

			String lsQty = sl_looseQty.getText().toString();

			if (!lsQty.matches("")) {
				int lsQtyCnvrt = Integer.parseInt(lsQty);
				qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

			} else {
				qty = cartonQtyCalc * cartonPerQtyCalc;
			}

			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + qty);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			productTotal(qty);
		}
	}

	public void looseQtyCalc() {
		String crtnQty = sl_cartonQty.getText().toString();
		String lsQty = sl_looseQty.getText().toString();

		if (lsQty.matches("")) {
			lsQty = "0";
		}

		if (!slCartonPerQty.matches("") && !crtnQty.matches("")
				&& !lsQty.matches("")) {

			int cartonQtyCalc = Integer.parseInt(crtnQty);
			int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);
			int looseQtyCalc = Integer.parseInt(lsQty);

			int qty;

			qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + qty);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			productTotal(qty);
		}

		if (!lsQty.matches("")) {

			int looseQtyCalc = Integer.parseInt(lsQty);
			int qty;

			if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
				int cartonQtyCalc = Integer.parseInt(crtnQty);
				int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);

				qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
			} else {
				qty = looseQtyCalc;
			}

			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + qty);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			productTotal(qty);
		}
	}

	public void itemDiscountCalc() {

		try {
			String itmDscnt = sl_itemDiscount.getText().toString();
			String qty = sl_qty.getText().toString();
			String prc = sl_price.getText().toString();

			if (itmDscnt.matches("")) {
				itmDscnt = "0";
			}

			if (!itmDscnt.matches("") && !qty.matches("") && !prc.matches("")) {

				double itemDiscountCalc = 0.0;

				itemDiscountCalc = Double.parseDouble(itmDscnt);

				int quantityCalc = Integer.parseInt(qty);
				double priceCalc = Double.parseDouble(prc);

				tt = (quantityCalc * priceCalc) - itemDiscountCalc;

				Log.d("ttl", "" + tt);
				String Prodtotal = twoDecimalPoint(tt);
				sl_total.setText("" + Prodtotal);

				double taxAmount = 0.0, netTotal = 0.0;
				if (!taxType.matches("") && !taxValue.matches("")) {

					double taxValueCalc = Double.parseDouble(taxValue);

					if (taxType.matches("E")) {

						taxAmount = (tt * taxValueCalc) / 100;
						String prodTax = fourDecimalPoint(taxAmount);
						sl_tax.setText("" + prodTax);

						netTotal = tt + taxAmount;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);

					} else if (taxType.matches("I")) {

						taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
						String prodTax = fourDecimalPoint(taxAmount);
						sl_tax.setText("" + prodTax);

						// netTotal = tt + taxAmount;
						netTotal = tt;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);

					} else if (taxType.matches("Z")) {

						sl_tax.setText("0.0");

						// netTotal = tt + taxAmount;
						netTotal = tt;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);

					} else {
						sl_tax.setText("0.0");
						sl_netTotal.setText("" + Prodtotal);
					}
				} else if (taxValue.matches("")) {
					sl_tax.setText("0.0");
					sl_netTotal.setText("" + Prodtotal);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void productTotal(double qty) {

		try {

			double taxAmount = 0.0, netTotal = 0.0;
			double taxAmount1 = 0.0, netTotal1 = 0.0;

			if (slPrice.matches("")) {
				slPrice = "0";
			}

			if (!slPrice.matches("")) {

				double slPriceCalc = Double.parseDouble(slPrice);
				String itmDscnt = sl_itemDiscount.getText().toString();
				if (!itmDscnt.matches("")) {

					tt = (qty * slPriceCalc);

				} else {

					tt = qty * slPriceCalc;

				}

				String Prodtotal = twoDecimalPoint(tt);

				double subTotal = 0.0;

				String itemDisc = sl_itemDiscount.getText().toString();
				if (!itemDisc.matches("")) {
					itmDisc = Double.parseDouble(itemDisc);
					subTotal = tt - itmDisc;
				} else {
					subTotal = tt;
				}

				String sbTtl = twoDecimalPoint(subTotal);

				sl_total.setText("" + sbTtl);

				if (!taxType.matches("") && !taxValue.matches("")) {

					double taxValueCalc = Double.parseDouble(taxValue);

					if (taxType.matches("E")) {

						if (!itemDisc.matches("")) {
							taxAmount1 = (subTotal * taxValueCalc) / 100;
							String prodTax = fourDecimalPoint(taxAmount1);
							sl_tax.setText("" + prodTax);

							netTotal1 = subTotal + taxAmount1;
							String ProdNetTotal = twoDecimalPoint(netTotal1);
							sl_netTotal.setText("" + ProdNetTotal);
						} else {

							taxAmount = (tt * taxValueCalc) / 100;
							String prodTax = fourDecimalPoint(taxAmount);
							sl_tax.setText("" + prodTax);

							netTotal = tt + taxAmount;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);
						}

					} else if (taxType.matches("I")) {
						if (!itemDisc.matches("")) {
							taxAmount1 = (subTotal * taxValueCalc)
									/ (100 + taxValueCalc);
							String prodTax = fourDecimalPoint(taxAmount1);
							sl_tax.setText("" + prodTax);

							// netTotal1 = subTotal + taxAmount;
							netTotal1 = subTotal;
							String ProdNetTotal = twoDecimalPoint(netTotal1);
							sl_netTotal.setText("" + ProdNetTotal);
						} else {
							taxAmount = (tt * taxValueCalc)
									/ (100 + taxValueCalc);
							String prodTax = fourDecimalPoint(taxAmount);
							sl_tax.setText("" + prodTax);

							// netTotal = tt + taxAmount;
							netTotal = tt;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);
						}

					} else if (taxType.matches("Z")) {

						sl_tax.setText("0.0");
						if (!itemDisc.matches("")) {
							// netTotal1 = subTotal + taxAmount;
							netTotal1 = subTotal;
							String ProdNetTotal = twoDecimalPoint(netTotal1);
							sl_netTotal.setText("" + ProdNetTotal);
						} else {
							// netTotal = tt + taxAmount;
							netTotal = tt;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);
						}

					} else {
						sl_tax.setText("0.0");
						sl_netTotal.setText("" + Prodtotal);
					}

				} else if (taxValue.matches("")) {
					sl_tax.setText("0.0");
					sl_netTotal.setText("" + Prodtotal);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void clQty() {
		String qty = sl_qty.getText().toString();
		String crtnperQty = sl_cartonPerQty.getText().toString();
		int q = 0, r = 0;

		if (crtnperQty.matches("0") || crtnperQty.matches("null")
				|| crtnperQty.matches("0.00")) {
			crtnperQty = "1";
		}

		if (!crtnperQty.matches("")) {
			if (!qty.matches("")) {
				try {
					int qty_nt = Integer.parseInt(qty);
					int pcs_nt = Integer.parseInt(crtnperQty);

					Log.d("qty_nt", "" + qty_nt);
					Log.d("pcs_nt", "" + pcs_nt);

					q = qty_nt / pcs_nt;
					r = qty_nt % pcs_nt;

					Log.d("cqty", "" + q);
					Log.d("lqty", "" + r);

					sl_cartonQty.setText("" + q);
					sl_looseQty.setText("" + r);

				} catch (ArithmeticException e) {
					System.out.println("Err: Divided by Zero");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void cartonQtyNew() {
		String crtnQty = sl_cartonQty.getText().toString();

		if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

			int cartonQtyCalc = Integer.parseInt(crtnQty);
			int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);
			int qty = 0;

			String lsQty = sl_looseQty.getText().toString();

			if (!lsQty.matches("")) {
				int lsQtyCnvrt = Integer.parseInt(lsQty);
				qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

			} else {
				qty = cartonQtyCalc * cartonPerQtyCalc;
			}

			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + qty);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			productTotalNew();
		}
	}

	public void looseQtyCalcNew() {
		String crtnQty = sl_cartonQty.getText().toString();
		String lsQty = sl_looseQty.getText().toString();

		if (lsQty.matches("")) {
			lsQty = "0";
		}

		if (!slCartonPerQty.matches("") && !crtnQty.matches("")
				&& !lsQty.matches("")) {

			int cartonQtyCalc = Integer.parseInt(crtnQty);
			int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);
			int looseQtyCalc = Integer.parseInt(lsQty);

			int qty;
			qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + qty);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			productTotalNew();
		}

		if (!lsQty.matches("")) {

			int looseQtyCalc = Integer.parseInt(lsQty);
			int qty;

			if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
				int cartonQtyCalc = Integer.parseInt(crtnQty);
				int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);

				qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
			} else {
				qty = looseQtyCalc;
			}

			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + qty);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			productTotalNew();
		}
	}

	public void itemDiscountCalcNew() {

		try {
			String itmDscnt = sl_itemDiscount.getText().toString();

			if (itmDscnt.matches("")) {
				itmDscnt = "0";
			}

			String lPrice = sl_price.getText().toString();
			String cPrice = sl_cprice.getText().toString();
			String cqty = sl_cartonQty.getText().toString();
			String lqty = sl_looseQty.getText().toString();

			if (lPrice.matches("")) {
				lPrice = "0";
			}

			if (cPrice.matches("")) {
				cPrice = "0";
			}

			if (cqty.matches("")) {
				cqty = "0";
			}

			if (lqty.matches("")) {
				lqty = "0";
			}

			if (!itmDscnt.matches("")) {
				double itemDiscountCalc = 0.0;
				itemDiscountCalc = Double.parseDouble(itmDscnt);

				double lPriceCalc = Double.parseDouble(lPrice);
				double cPriceCalc = Double.parseDouble(cPrice);

				double cqtyCalc = Double.parseDouble(cqty);
				double lqtyCalc = Double.parseDouble(lqty);

				tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc)
						- itemDiscountCalc;

				Log.d("ttl", "" + tt);
				String Prodtotal = twoDecimalPoint(tt);
				sl_total.setText("" + Prodtotal);

				double taxAmount = 0.0, netTotal = 0.0;
				if (!taxType.matches("") && !taxValue.matches("")) {

					double taxValueCalc = Double.parseDouble(taxValue);

					if (taxType.matches("E")) {

						taxAmount = (tt * taxValueCalc) / 100;
						String prodTax = fourDecimalPoint(taxAmount);
						sl_tax.setText("" + prodTax);

						netTotal = tt + taxAmount;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);

					} else if (taxType.matches("I")) {

						taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
						String prodTax = fourDecimalPoint(taxAmount);
						sl_tax.setText("" + prodTax);

						// netTotal = tt + taxAmount;
						netTotal = tt;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);

					} else if (taxType.matches("Z")) {

						sl_tax.setText("0.0");

						// netTotal = tt + taxAmount;
						netTotal = tt;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);

					} else {
						sl_tax.setText("0.0");
						sl_netTotal.setText("" + Prodtotal);
					}
				} else if (taxValue.matches("")) {
					sl_tax.setText("0.0");
					sl_netTotal.setText("" + Prodtotal);
				}
			}

		} catch (Exception e) {

		}
	}

	public void productTotalNew() {

		try {
			double taxAmount = 0.0, netTotal = 0.0;
			double taxAmount1 = 0.0, netTotal1 = 0.0;

			String lPrice = sl_price.getText().toString();
			String cPrice = sl_cprice.getText().toString();
			String cqty = sl_cartonQty.getText().toString();
			String lqty = sl_looseQty.getText().toString();

			if (lPrice.matches("")) {
				lPrice = "0";
			}

			if (cPrice.matches("")) {
				cPrice = "0";
			}

			if (cqty.matches("")) {
				cqty = "0";
			}

			if (lqty.matches("")) {
				lqty = "0";
			}

			double lPriceCalc = Double.parseDouble(lPrice);
			double cPriceCalc = Double.parseDouble(cPrice);

			double cqtyCalc = Double.parseDouble(cqty);
			double lqtyCalc = Double.parseDouble(lqty);

			tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc);

			String Prodtotal = twoDecimalPoint(tt);

			double subTotal = 0.0;

			String itemDisc = sl_itemDiscount.getText().toString();
			if (!itemDisc.matches("")) {
				itmDisc = Double.parseDouble(itemDisc);
				subTotal = tt - itmDisc;
			} else {
				subTotal = tt;
			}

			String sbTtl = twoDecimalPoint(subTotal);

			sl_total.setText("" + sbTtl);

			if (!taxType.matches("") && !taxValue.matches("")) {

				double taxValueCalc = Double.parseDouble(taxValue);

				if (taxType.matches("E")) {

					if (!itemDisc.matches("")) {
						taxAmount1 = (subTotal * taxValueCalc) / 100;
						String prodTax = fourDecimalPoint(taxAmount1);
						sl_tax.setText("" + prodTax);

						netTotal1 = subTotal + taxAmount1;
						String ProdNetTotal = twoDecimalPoint(netTotal1);
						sl_netTotal.setText("" + ProdNetTotal);
					} else {

						taxAmount = (tt * taxValueCalc) / 100;
						String prodTax = fourDecimalPoint(taxAmount);
						sl_tax.setText("" + prodTax);

						netTotal = tt + taxAmount;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);
					}

				} else if (taxType.matches("I")) {
					if (!itemDisc.matches("")) {
						taxAmount1 = (subTotal * taxValueCalc)
								/ (100 + taxValueCalc);
						String prodTax = fourDecimalPoint(taxAmount1);
						sl_tax.setText("" + prodTax);

						// netTotal1 = subTotal + taxAmount1;
						netTotal1 = subTotal;
						String ProdNetTotal = twoDecimalPoint(netTotal1);
						sl_netTotal.setText("" + ProdNetTotal);
					} else {
						taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
						String prodTax = fourDecimalPoint(taxAmount);
						sl_tax.setText("" + prodTax);

						// netTotal = tt + taxAmount;
						netTotal = tt;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);
					}

				} else if (taxType.matches("Z")) {

					sl_tax.setText("0.0");
					if (!itemDisc.matches("")) {
						// netTotal1 = subTotal + taxAmount;
						netTotal1 = subTotal;
						String ProdNetTotal = twoDecimalPoint(netTotal1);
						sl_netTotal.setText("" + ProdNetTotal);
					} else {
						// netTotal = tt + taxAmount;
						netTotal = tt;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);
					}

				} else {
					sl_tax.setText("0.0");
					sl_netTotal.setText("" + Prodtotal);
				}

			} else if (taxValue.matches("")) {
				sl_tax.setText("0.0");
				sl_netTotal.setText("" + Prodtotal);
			}

		} catch (Exception e) {

		}
	}

	private class ProductStockAsyncCall extends AsyncTask<Void, Void, Void> {
		String dialogStatus, LocationCode, product_stock_jsonString, stock;
		JSONObject product_stock_jsonResponse;
		JSONArray product_stock_jsonMainNode;

		@Override
		protected void onPreExecute() {
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			LocationCode = SalesOrderSetGet.getLocationcode();
			try {
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");
							product_stock_jsonString = offlineDatabase
									.getStockValue(LocationCode, edit_prodcode);

						} else {
							Log.d("CheckOffline Alert -->", "False");
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
							//finish();
						}

					} else {
						Log.d("checkOffline Status -->", "False");
						product_stock_jsonString = stock();
					}

				} else if (onlineMode.matches("False")) {
					product_stock_jsonString = offlineDatabase.getStockValue(
							LocationCode, edit_prodcode);

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {

				product_stock_jsonResponse = new JSONObject(
						product_stock_jsonString);
				product_stock_jsonMainNode = product_stock_jsonResponse
						.optJSONArray("JsonArray");

				int lengthJsonArr = product_stock_jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode;

					jsonChildNode = product_stock_jsonMainNode.getJSONObject(i);

					stock = jsonChildNode.optString("Qty").toString();

					Log.d("stock qty", stock);

				}
			} catch (JSONException e) {

				e.printStackTrace();
			} catch (Exception e) {

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			sl_stock.setText(" " + stock);
		}
	}

	public String stock() {
		String product_stock_jsonString = "";
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		String LocationCode = SalesOrderSetGet.getLocationcode();

		HashMap<String, String> hmvalue = new HashMap<String, String>();
		hmvalue.put("CompanyCode", cmpnyCode);
		hmvalue.put("LocationCode", LocationCode);
		hmvalue.put("ProductCode", edit_prodcode);
		product_stock_jsonString = WebServiceClass.parameterService(hmvalue,
				"fncGetProductWithStock");

		return product_stock_jsonString;
	}

	/** Invoice Edit End **/

	private String checkInternetStatus() {
		String internetStatus = "";
		String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			if(mobileHaveOfflineMode.matches("1")){
		checkOffline = OfflineCommon.isConnected(InvoiceSummary.this);
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

//		return internetStatus;
	}else{
		internetStatus = "false";
	}
}else{
		internetStatus = "false";
		}
		return internetStatus;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}


	public void onListItemClick(String item) {
		// TODO Auto-generated method stub
		menu.toggle();
	}

	/*@Override
	public void onLocationChanged(Location location) {
		// Getting reference to TextView tv_longitude
		Log.d("Location", "gps " + location.getLatitude());
		try {

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
				} else { // online
					getAddress(location.getLatitude(), location.getLongitude());
				}
			}

		} catch (Exception e) {
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
	}*/

	/* Request updates at startup */
	/*@Override
	protected void onResume() {
		super.onResume();
		// locationManager.requestLocationUpdates(provider, 1000, 1, this);
	}*/

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		if (mGoogleApiClient.isConnected()) {
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
			mGoogleApiClient.disconnect();
		}
	}

	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Deleting");
		alertDialog
				.setMessage("Invoice products will clear. Do you want to proceed");
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Intent i = new Intent(InvoiceSummary.this,
								InvoiceHeader.class);
						startActivity(i);
						InvoiceSummary.this.finish();
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

	public void remarksDialog() {

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;

		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.remarks_dialog);

		getremarks = (EditText) dialog.findViewById(R.id.getremarks);

		String remarks = SalesOrderSetGet.getRemarks();

		if (remarks != null && !remarks.isEmpty()) {
			getremarks.setText(remarks);
		} else {
			getremarks.setText("");
		}

		getremarks.requestFocus();
		getremarks.setSelection(getremarks.length());
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(getremarks, InputMethodManager.SHOW_IMPLICIT);

		ok = (Button) dialog.findViewById(R.id.okbutton);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				SalesOrderSetGet.setRemarks(getremarks.getText().toString());
				dialog.dismiss();
			}
		});

		cancel = (Button) dialog.findViewById(R.id.cancelbutton);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.getWindow().setLayout((6 * width) / 7, (2 * height) / 5);
		dialog.show();
	}
	
	private class GetProduct implements CallbackInterface {

		String prod_code = "",SR_slNo="";

		public GetProduct(String productCode,String SR_slNo) {
			this.prod_code = productCode;
			this.SR_slNo = SR_slNo;
			
			Log.d("SR_slNo", "SR_slNo "+ SR_slNo);
		}

		@Override
		public void onSuccess(JSONArray jsonArray) {
			try {
				int len = jsonArray.length();
				Log.d("len", "jsonArray "+ jsonArray.length());
				for (int i = 0; i < len; i++) {
					JSONObject object = jsonArray.getJSONObject(i);

					String haveBatch = object.getString("HaveBatch");
					String haveExpiry = object.getString("HaveExpiry");
					String prodName = object.getString("ProductName");
					String PcsPerCarton = object.getString("PcsPerCarton");

					Log.d("thaveBatch", haveBatch);
					Log.d("thaveExpiry", haveExpiry);
					Log.d("tprod_code", prod_code);

					SOTDatabase.updateproductbatch(prod_code, haveBatch,
							haveExpiry);

					if (haveBatch.matches("True") || haveExpiry.matches("True")) {

						String cmpnyCode = SupplierSetterGetter
								.getCompanyCode();
						// String locCode = SalesOrderSetGet.getFromLoc();
						String locCode = SalesOrderSetGet.getLocationcode();

						ArrayList<PropertyInfo> params = new ArrayList<PropertyInfo>();
						params.add(newPropertyInfo("CompanyCode", cmpnyCode));
						params.add(newPropertyInfo("LocationCode", locCode));
						params.add(newPropertyInfo("ProductCode", prod_code));

						Log.d("tcmpnyCode", cmpnyCode);
						Log.d("tlocCode", locCode);						
						
						if(InvoiceDetailsArr!=null){
							
							Log.d("editinvoice batch", ""+slNo);
							
							ArrayList<PropertyInfo> params1 = new ArrayList<PropertyInfo>();
							params1.add(newPropertyInfo("InvoiceNo", ConvertToSetterGetter.getEdit_inv_no()));
							params1.add(newPropertyInfo("CompanyCode", cmpnyCode));
							params1.add(newPropertyInfo("slNo", slNo));
							params1.add(newPropertyInfo("ProductCode", prod_code));
																		
							new SoapAccessTask(InvoiceSummary.this, valid_url,
									"fncGetInvoiceBatchDetail", params1,
									new GetBatchStockFromEditInvoice(haveBatch, haveExpiry,
											prodName, prod_code, SR_slNo,PcsPerCarton)).execute();
						}else{
							new SoapAccessTask(InvoiceSummary.this, valid_url,
									"fncGetProductBatchStock", params,
									new GetBatchStock(haveBatch, haveExpiry,
											prodName, prod_code, SR_slNo)).execute();	
						}
						
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(Exception error) {
			Log.d("Failure", "ff "+error.toString());
		}
	}

	public static PropertyInfo newPropertyInfo(String name, String value) {
		PropertyInfo propertyInfo = new PropertyInfo();
		propertyInfo.setName(name);
		propertyInfo.setValue(value);
		return propertyInfo;
	}

	
	private class GetBatchStock implements CallbackInterface {

		String havebatch = "", haveexpiry = "", prodName = "", prodCode = "", SR_slNo="";

		public GetBatchStock(String haveBatch, String haveExpiry,
				String prodName, String prod_code,String SR_slNo) {
			this.havebatch = haveBatch;
			this.haveexpiry = haveExpiry;
			this.prodName = prodName;
			this.prodCode = prod_code;
			this.SR_slNo = SR_slNo;
			
			Log.d("prodCode", "" + prodCode);
		}

		@Override
		public void onSuccess(JSONArray jsonArray) {
			try {

				int len = jsonArray.length();
				for (int i = 0; i < len; i++) {
					JSONObject object = jsonArray.getJSONObject(i);
//					int qty_nt = 0;
					double q = 0, r = 0,qty_nt=0;
					String cqty = "", lqty = "";

					String qty = object.getString("Qty");
					String cartonPerQty = object.getString("PcsPerCarton");
					String NoOfCarton = object.getString("NoOfCarton");
					double dQty = Double.parseDouble(qty);
					Log.d("qty", "" + qty);
					Log.d("cartonPerQty", "" + cartonPerQty);
					if (!cartonPerQty.matches("")) {
						if (!qty.matches("")) {
							try {
//								qty_nt = (int) dQty;
//								int pcs_nt = Integer.parseInt(cartonPerQty);
//
//								Log.d("qty_nt", "" + qty_nt);
//								Log.d("pcs_nt", "" + pcs_nt);
//
//								q = qty_nt / pcs_nt;
//								r = qty_nt % pcs_nt;
//
//								cqty = "" + q;
//								lqty = "" + r;
								
								qty_nt = Double.parseDouble(qty);
								double pcs_nt = Double.parseDouble(cartonPerQty);

								Log.d("qty_nt", "" + qty_nt);
								Log.d("pcs_nt", "" + pcs_nt);

								q = (int)(qty_nt / pcs_nt);
								r = (qty_nt % pcs_nt);

								cqty = "" + q;
								lqty = "" + r;
								
								Log.d("cqty", "" + q);
								Log.d("lqty", "" + r);

							} catch (ArithmeticException e) {
								System.out.println("Err: Divided by Zero");

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}

					calCarton = LogOutSetGet.getCalcCarton();
					if (calCarton.matches("0")) {
						cqty = NoOfCarton;
						lqty = "0";

					}

					double cartonqty = 0;

					if (calCarton.matches("0")) {

						if (!cqty.matches("")) {
							cartonqty = Double.parseDouble(cqty);

							if (cartonqty > 0 || qty_nt > 0) {
								int batchSlNo = i + 1;

								HashMap<String, String> hmValue = new HashMap<String, String>();
								// hmValue.put("id", bat_id);
								hmValue.put("slNo", batchSlNo + "");
								hmValue.put("ProductCode", prodCode);
								hmValue.put("ProductName", prodName);
								hmValue.put("BatchNo",
										object.getString("BatchNo"));
								hmValue.put("ExpiryDate",
										object.getString("ExpiryDate"));
								hmValue.put("AvailCQty", cqty);
								hmValue.put("AvailLQty", lqty);
								hmValue.put("AvailQty", "" + qty_nt);
								hmValue.put("CQty", "0");
								hmValue.put("LQty", "0");
								hmValue.put("Qty", "0");
								hmValue.put("PcsPerCarton", cartonPerQty);
								hmValue.put("HaveBatch", havebatch);
								hmValue.put("HaveExpiry", haveexpiry);
								hmValue.put("SR_Slno", SR_slNo);
								
								Log.d("hmValue", "" + hmValue.toString());

								SOTDatabase.storeBatch(hmValue);
							}
						}

					} else {
						if (qty_nt > 0) {
							int batchSlNo = i + 1;

							HashMap<String, String> hmValue = new HashMap<String, String>();
							// hmValue.put("id", bat_id);
							hmValue.put("slNo", batchSlNo + "");
							hmValue.put("ProductCode", prodCode);
							hmValue.put("ProductName", prodName);
							hmValue.put("BatchNo", object.getString("BatchNo"));
							hmValue.put("ExpiryDate",
									object.getString("ExpiryDate"));
							hmValue.put("AvailCQty", cqty);
							hmValue.put("AvailLQty", lqty);
							hmValue.put("AvailQty", "" + qty_nt);
							hmValue.put("CQty", "0");
							hmValue.put("LQty", "0");
							hmValue.put("Qty", "0");
							hmValue.put("PcsPerCarton", cartonPerQty);
							hmValue.put("HaveBatch", havebatch);
							hmValue.put("HaveExpiry", haveexpiry);
							hmValue.put("SR_Slno", SR_slNo);
							
							Log.d("hmValue", "" + hmValue.toString());

							SOTDatabase.storeBatch(hmValue);
						}
					}

				}

			} catch (JSONException e) {
			}
		}

		@Override
		public void onFailure(Exception error) {

		}
	}
	
	private class GetBatchStockFromEditInvoice implements CallbackInterface {

		String havebatch = "", haveexpiry = "", prodName = "", prodCode = "", SR_slNo="",PcsPerCarton="";

		public GetBatchStockFromEditInvoice(String haveBatch, String haveExpiry,
				String prodName, String prod_code,String SR_slNo,String PcsPerCarton) {
			this.havebatch = haveBatch;
			this.haveexpiry = haveExpiry;
			this.prodName = prodName;
			this.prodCode = prod_code;
			this.SR_slNo = SR_slNo;
			this.PcsPerCarton= PcsPerCarton;
			
			Log.d("prodCode", "" + prodCode);
		}

		@Override
		public void onSuccess(JSONArray jsonArray) {
			try {

				int len = jsonArray.length();
				for (int i = 0; i < len; i++) {
					JSONObject object = jsonArray.getJSONObject(i);
//					int qty_nt = 0;
					double q = 0, r = 0,qty_nt=0;
//					String cqty = "", lqty = "";

					String qty = object.getString("Qty");
//					String cartonPerQty = object.getString("PcsPerCarton");
//					String NoOfCarton = object.getString("NoOfCarton");
					String cqty = object.getString("CQty");
					String lqty = object.getString("LQty");
				
					Log.d("qty", "" + qty);
					
					if (!qty.matches("")) {
						qty_nt = Double.parseDouble(qty);
					}
					
//					Log.d("cartonPerQty", "" + cartonPerQty);
				/*	if (!cartonPerQty.matches("")) {
						if (!qty.matches("")) {
							try {

								
								qty_nt = Double.parseDouble(qty);
								double pcs_nt = Double.parseDouble(cartonPerQty);

								Log.d("qty_nt", "" + qty_nt);
								Log.d("pcs_nt", "" + pcs_nt);

								q = (int)(qty_nt / pcs_nt);
								r = (qty_nt % pcs_nt);

								cqty = "" + q;
								lqty = "" + r;
								
								Log.d("cqty", "" + q);
								Log.d("lqty", "" + r);

							} catch (ArithmeticException e) {
								System.out.println("Err: Divided by Zero");

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}*/

					calCarton = LogOutSetGet.getCalcCarton();
//					if (calCarton.matches("0")) {
//						cqty = NoOfCarton;
//						lqty = "0";
//
//					}

					double cartonqty = 0;

					if (calCarton.matches("0")) {

						if (!cqty.matches("")) {
							cartonqty = Double.parseDouble(cqty);

							if (cartonqty > 0 || qty_nt > 0) {
								int batchSlNo = i + 1;

								HashMap<String, String> hmValue = new HashMap<String, String>();
								// hmValue.put("id", bat_id);
								hmValue.put("slNo", batchSlNo + "");
								hmValue.put("ProductCode", prodCode);
								hmValue.put("ProductName", prodName);
								hmValue.put("BatchNo",
										object.getString("BatchNo"));
								hmValue.put("ExpiryDate",
										object.getString("ExpiryDate"));
								hmValue.put("AvailCQty", cqty);
								hmValue.put("AvailLQty", lqty);
								hmValue.put("AvailQty", "" + qty_nt);
								hmValue.put("CQty", "0");
								hmValue.put("LQty", "0");
								hmValue.put("Qty", "0");
								hmValue.put("PcsPerCarton", "1"); //need to change
								hmValue.put("HaveBatch", havebatch);
								hmValue.put("HaveExpiry", haveexpiry);
								hmValue.put("SR_Slno", SR_slNo);
								
								Log.d("hmValue", "" + hmValue.toString());

								SOTDatabase.storeBatch(hmValue);
							}
						}

					} else {
						if (qty_nt > 0) {
							int batchSlNo = i + 1;

							HashMap<String, String> hmValue = new HashMap<String, String>();
							// hmValue.put("id", bat_id);
							hmValue.put("slNo", batchSlNo + "");
							hmValue.put("ProductCode", prodCode);
							hmValue.put("ProductName", prodName);
							hmValue.put("BatchNo", object.getString("BatchNo"));
							hmValue.put("ExpiryDate",
									object.getString("ExpiryDate"));
							hmValue.put("AvailCQty", cqty);
							hmValue.put("AvailLQty", lqty);
							hmValue.put("AvailQty", "" + qty_nt);
							hmValue.put("CQty", "0");
							hmValue.put("LQty", "0");
							hmValue.put("Qty", "0");
							hmValue.put("PcsPerCarton", PcsPerCarton);
							hmValue.put("HaveBatch", havebatch);
							hmValue.put("HaveExpiry", haveexpiry);
							hmValue.put("SR_Slno", SR_slNo);
							
							Log.d("hmValue", "" + hmValue.toString());

							SOTDatabase.storeBatch(hmValue);
						}
					}

				}

			} catch (JSONException e) {
			}
		}

		@Override
		public void onFailure(Exception error) {

		}
	}


	@Override
	public void onConnected(Bundle bundle) {
		Log.d("onConnected", "onConnected");

		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000); // Update location every second
		mLocationRequest.setFastestInterval(1 * 1000);



		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
					mGoogleApiClient);


		Log.d("mLastLocation","-->"+mLastLocation);

//		double lati=0,longi=0;

		if (mLastLocation != null) {
			lat = String.valueOf(mLastLocation.getLatitude());
			lon = String.valueOf(mLastLocation.getLongitude());

			if(lat!=null && !lat.isEmpty()){
				setLatitude = mLastLocation.getLatitude();
			}

			if(lon!=null && !lon.isEmpty()){
				setLongitude = mLastLocation.getLongitude();
			}
		}else{
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		}

		boolean interntConnection = isNetworkConnected();
		if (interntConnection == true) {

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if (isGPSEnabled == true) {
				Log.d("isGPSEnabled", ""+isGPSEnabled);
//				new getServerDateTime(lati, longi, "1").execute();

				updateUI(setLatitude,setLongitude);
			}else {
				Log.d("gpsLocation", " null "+"isGPSOff");
//				new getServerDateTime(0, 0, "0").execute();
				updateUI(0,0);

//					Toast.makeText(getApplicationContext(),
//							"GPS is OFF. Please turn ON",
//							Toast.LENGTH_LONG).show();
			}

		} else {
			/*Toast.makeText(getApplicationContext(),
					"No Internet Connection",
					Toast.LENGTH_LONG).show();*/
		}
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onLocationChanged(Location location) {
		lat = String.valueOf(location.getLatitude());
		lon = String.valueOf(location.getLongitude());
//		updateUI();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		buildGoogleApiClient();
	}

	synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();


	}

	void updateUI(double lat,double lon) {
		try {

			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
				} else { // online
					getAddress(lat, lon);
				}
			}


		}catch (Exception e){
			e.printStackTrace();
		}
	}


	public void getAddress(double latitude, double longitude) throws Exception {
		address1 = "";
		address2 = "";
		Geocoder geocoder;
		List<Address> addresses;
		try {
			boolean interntConnection = isNetworkConnected();
			if (interntConnection == true) {
				geocoder = new Geocoder(this, Locale.getDefault());

				addresses = geocoder.getFromLocation(latitude, longitude, 1);
				Log.d("addressesSize","--->"+addresses.size());
				if (addresses != null && addresses.size() > 0) {

					address1 = addresses.get(0).getAddressLine(0);
					address2 = addresses.get(0).getAddressLine(1);


					Log.d("address2","--->"+address1+"  "+address2);

					sm_location.setText(address1 + "," + address2);

					/*Address address = addresses.get(0);
					ArrayList<String> addressFragments = new ArrayList<String>();

					// Fetch the address lines using getAddressLine,
					// join them, and send them to the thread.
					for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
						addressFragments.add(address.getAddressLine(i));
					}*/

				}

//				Toast.makeText(
//						getApplicationContext(),
//						"Mobile Location : " + "\nLatitude: "
//								+ latitude + "\nLongitude: "
//								+ longitude + "\nAddress 1: "
//								+ address1 + "\nAddress 2: "
//								+ address2, Toast.LENGTH_LONG)
//						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}
	/*@Override
	public void onResume()
	{
		super.onResume();
		//if(D) Log.e(TAG, "--- onResume ---");

	}*/
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

				Log.d("GlobalDataBluetooth","GlobalDataBluetoothAdapter enable");
				//Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				//startActivity(enableIntent);*/
				// Otherwise, setup the chat session
			} else {
				if (GlobalData.mService == null) {
					Log.d("GlobalData.mService","GlobalData.mService null");
					GlobalData.mService = new BluetoothService(this, mHandler);
				}
				Log.d("GlobalData.mService","GlobalData.mService outside null");
			}
		}
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
				//	Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
						//	Toast.LENGTH_SHORT).show();
					/*if (BluetoothAdapter.checkBluetoothAddress(macaddress))
					{
						BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
						// Attempt to connect to the device
						GlobalData.mService.setHandler(mHandler);
						GlobalData.mService.connect(device,true);
					//	print4Inch();

					}*/


					Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
							Toast.LENGTH_SHORT).show();


					reconnectDialog(msg.getData().getString("toast"));
				/*	finish();
					if (header.matches("InvoiceHeader")) {
						intent = new Intent(InvoiceSummary.this,
								InvoiceHeader.class);

					} else if (header.matches("DeliveryOrderHeader")) {
						intent = new Intent(InvoiceSummary.this,
								DeliveryOrderHeader.class);

					} else if (header.matches("SalesOrderHeader")) {
						intent = new Intent(InvoiceSummary.this,
								SalesOrderHeader.class);

					} else if (header.matches("CustomerHeader")) {
						intent = new Intent(InvoiceSummary.this,
								CustomerListActivity.class);
					} else if (header.matches("RouteHeader")) {
						intent = new Intent(InvoiceSummary.this,
								RouteHeader.class);
					} else {
						intent = new Intent(InvoiceSummary.this,
								LandingActivity.class);

					}
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					InvoiceSummary.this.finish();
					SOTDatabase.deleteImage();

					save.setVisibility(View.INVISIBLE);
					SOTDatabase.deleteAllProduct();
					mListView.setAdapter(null);
					cursor.requery();
					barcode_edit.setVisibility(View.GONE);
					mEmpty.setVisibility(View.VISIBLE);
					sm_total.setText("");
					sm_billDisc.setText("");
					sm_billDiscPercentage.setText("");
					sm_tax.setText("");
					sm_billDisc.setFocusable(false);
					sm_billDiscPercentage.setFocusable(false);
					sm_header_layout.setVisibility(View.GONE);
					sm_subTotal.setText("");
					sm_netTotal.setText("");
					sm_totl_cqty.setText("");
					sm_totl_lqty.setText("");
					sm_totl_qty.setText("");

					SOTDatabase.deleteBarcode();

					SOTDatabase.deleteBillDisc();
					SalesOrderSetGet.setCustomercode("");
					SalesOrderSetGet.setCustomername("");*/

					break;
			}
			//helper.dismissProgressDialog();
		}
	};
	public void reconnectDialog(String msg){

		final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		final Dialog dialog = new Dialog(InvoiceSummary.this);

		dialog.setContentView(R.layout.dialog_reconnect);
		dialog.setTitle(msg);
		ImageView reconnect = (ImageView) dialog.findViewById(R.id.reconnect);

		reconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (BluetoothAdapter.checkBluetoothAddress(macaddress))
				{
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
				//	unpairDevice(device);
				//	pairDevice(device);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device,true);



				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	protected void onDestroy(){
		super.onDestroy();
		if(GlobalData.mService!=null){
			GlobalData.mService.stop();
		}
	}
	/*private void unpairDevice(BluetoothDevice device) {
		try {
			Method method = device.getClass().getMethod("removeBond", (Class[]) null);
			method.invoke(device, (Object[]) null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void pairDevice(BluetoothDevice device) {
		try {
			Method method = device.getClass().getMethod("createBond", (Class[]) null);
			method.invoke(device, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	public void print4Inch(){

		final String customerCode = SalesOrderSetGet.getCustomercode();
		 final String customerName = SalesOrderSetGet.getCustomername();
		final String soDate = SalesOrderSetGet.getSaleorderdate();
		CubePrint mPrintCube = new CubePrint(InvoiceSummary.this,FWMSSettingsDatabase.getPrinterAddress());
		mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
			@Override
			public void onCompleted() {
				//	helper.dismissProgressDialog();

				if (mDeliveryCheckBox.isChecked())
				{
					CubePrint mPrintCube1 = new CubePrint(InvoiceSummary.this,FWMSSettingsDatabase.getPrinterAddress());
					mPrintCube1.DeliveryOnInvoicePrint(summaryResult, soDate,
							customerCode, customerName, product, productdet,
							printsortHeader, gnrlStngs, 1, product_batchArr,
							footerArr);

					mPrintCube1.setOnCompletedListener(new CubePrint.OnCompletedListener() {
						@Override
						public void onCompleted() {
							helper.showLongToast(R.string.printed_successfully);
							finish();
							if (header.matches("InvoiceHeader")) {
								intent = new Intent(InvoiceSummary.this,
										InvoiceHeader.class);

							} else if (header.matches("DeliveryOrderHeader")) {
								intent = new Intent(InvoiceSummary.this,
										DeliveryOrderHeader.class);

							} else if (header.matches("SalesOrderHeader")) {
								intent = new Intent(InvoiceSummary.this,
										SalesOrderHeader.class);

							} else if (header.matches("CustomerHeader")) {
								intent = new Intent(InvoiceSummary.this,
										CustomerListActivity.class);
							} else if (header.matches("RouteHeader")) {
								intent = new Intent(InvoiceSummary.this,
										RouteHeader.class);
							} else if (header.matches("ConsignmentHeader")) {
								intent = new Intent(InvoiceSummary.this, ConsignmentHeader.class);
							}else {
								intent = new Intent(InvoiceSummary.this,
										LandingActivity.class);

							}
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							InvoiceSummary.this.finish();
							SOTDatabase.deleteImage();

							save.setVisibility(View.INVISIBLE);
							SOTDatabase.deleteAllProduct();
							mListView.setAdapter(null);
							cursor.requery();
							barcode_edit.setVisibility(View.GONE);
							mEmpty.setVisibility(View.VISIBLE);
							sm_total.setText("");
							sm_billDisc.setText("");
							sm_billDiscPercentage.setText("");
							CustomerSetterGetter.setBillDiscount("");
							sm_tax.setText("");
							sm_billDisc.setFocusable(false);
							sm_billDiscPercentage.setFocusable(false);
							sm_header_layout.setVisibility(View.GONE);
							sm_subTotal.setText("");
							sm_netTotal.setText("");
							sm_totl_cqty.setText("");
							sm_totl_lqty.setText("");
							sm_totl_qty.setText("");
							sm_subTotal_inclusive.setText("");
							SOTDatabase.deleteBarcode();

							SOTDatabase.deleteBillDisc();
							SalesOrderSetGet.setCustomercode("");
							SalesOrderSetGet.setCustomername("");
							SalesOrderSetGet.setCustomerTaxPerc("");
						}
					});

				}else {
					helper.showLongToast(R.string.printed_successfully);
					finish();
					if (header.matches("InvoiceHeader")) {
						intent = new Intent(InvoiceSummary.this,
								InvoiceHeader.class);

					} else if (header.matches("DeliveryOrderHeader")) {
						intent = new Intent(InvoiceSummary.this,
								DeliveryOrderHeader.class);

					} else if (header.matches("SalesOrderHeader")) {
						intent = new Intent(InvoiceSummary.this,
								SalesOrderHeader.class);

					} else if (header.matches("CustomerHeader")) {
						intent = new Intent(InvoiceSummary.this,
								CustomerListActivity.class);
					} else if (header.matches("RouteHeader")) {
						intent = new Intent(InvoiceSummary.this,
								RouteHeader.class);
					} else if (header.matches("ConsignmentHeader")) {
						intent = new Intent(InvoiceSummary.this, ConsignmentHeader.class);
					}else {
						intent = new Intent(InvoiceSummary.this,
								LandingActivity.class);

					}
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					InvoiceSummary.this.finish();
					SOTDatabase.deleteImage();

					save.setVisibility(View.INVISIBLE);
					SOTDatabase.deleteAllProduct();
					mListView.setAdapter(null);
					cursor.requery();
					barcode_edit.setVisibility(View.GONE);
					mEmpty.setVisibility(View.VISIBLE);
					sm_total.setText("");
					sm_billDisc.setText("");
					sm_billDiscPercentage.setText("");
					CustomerSetterGetter.setBillDiscount("");
					sm_tax.setText("");
					sm_billDisc.setFocusable(false);
					sm_billDiscPercentage.setFocusable(false);
					sm_header_layout.setVisibility(View.GONE);
					sm_subTotal.setText("");
					sm_netTotal.setText("");
					sm_totl_cqty.setText("");
					sm_totl_lqty.setText("");
					sm_totl_qty.setText("");
					sm_subTotal_inclusive.setText("");
					SOTDatabase.deleteBarcode();

					SOTDatabase.deleteBillDisc();
					SalesOrderSetGet.setCustomercode("");
					SalesOrderSetGet.setCustomername("");
					SalesOrderSetGet.setCustomerTaxPerc("");
				}



			/*	if(GlobalData.mService!=null){
					GlobalData.mService.stop();
				}*/

			}
		});
		if (cash_checkbox.isChecked()) {
			nofcopies = Integer.parseInt(stnumber.getText().toString());
			Log.d("cash_checkbox","-->cash_checkbox");
			mPrintCube.printInvoice(summaryResult, soDate, customerCode,
					customerName, product, productdet, printsortHeader,
					gnrlStngs, nofcopies, product_batchArr, footerArr,"");
			/*mPrintCube.printReceipt(customerCode, customerName, ReceiptNo,
					soDate, productdetReceipt, printsortHeader, gnrlStngs,
					nofcopies, true, footerArr);*/
		} else {

			if (enableprint.isChecked()) {
				nofcopies = Integer.parseInt(stnumber.getText().toString());
				Log.d("invoice_check","-->invoice_check");
				mPrintCube.printInvoice(summaryResult, soDate, customerCode,
						customerName, product, productdet, printsortHeader,
						gnrlStngs, nofcopies, product_batchArr, footerArr,"");
			}
			else if (mDeliveryCheckBox.isChecked())
			{
//				mDeliveryCheckBox.setChecked(false);
				Log.d("mDeliveryCheck","-->mDeliveryCheck");
				mPrintCube.DeliveryOnInvoicePrint(summaryResult, soDate,
								customerCode, customerName, product, productdet,
								printsortHeader, gnrlStngs, 1, product_batchArr,
								footerArr);

			}
		}

	}


	private String strValidate(String value){
		String mValue = "";

		if(value != null && !value.isEmpty()){
			mValue= value;
		}
		return mValue;
	}
	/*@Override
	public synchronized void onResume()
	{
		super.onResume();
	//	if(D) Log.e(TAG, "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity returns.

			if (GlobalData.mService != null)
			{
				// Only if the state is STATE_NONE, do we know that we haven't started already
				if (GlobalData.mService.getState() == GlobalData.STATE_NONE)
				{
					// Start the Bluetooth chat services
					GlobalData.mService.start();
				}
			}

	}*/

	@Override
	protected void onResume() {
		super.onResume();
		//Now lets connect to the API
		mGoogleApiClient.connect();
	}



	@Override
	public void onBackPressed() {
		sm_billDisc.setText("");
		sm_billDiscPercentage.setText("");
		Intent i = new Intent(InvoiceSummary.this, InvoiceAddProduct.class);
		startActivity(i);
		InvoiceSummary.this.finish();
	}

}