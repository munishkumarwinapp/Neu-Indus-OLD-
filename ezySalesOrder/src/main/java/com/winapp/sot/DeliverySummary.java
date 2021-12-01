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
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
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
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.model.Product;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.PrinterZPL;
import com.winapp.printer.UIHelper;
import com.winapp.sotdetails.CustomerListActivity;
import com.winapp.sotdetails.SummaryEditDialogFragment;

@SuppressLint("UseValueOf")
public class DeliverySummary extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace,SummaryEditDialogFragment.SummaryEditDialogListener, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

	Cursor cursor, billCursor, barcodeCursor;
	ImageView camera, signature, location, prodphoto, sosign, arrow,
			arrowUpDown;
	EditText sm_total,sm_subTotal_inclusive, sm_subTotal, sm_netTotal, sm_billDisc, sm_tax,
			sm_total_new, sm_itemDisc, sm_location, sm_billDiscPercentage,
			getremarks;
	ImageButton back, save, barcode_edit, remarks_edit;
	double total = 0, smTax = 0;
	Button stupButton, stdownButton, ok, cancel;
	LinearLayout sm_header_layout, slsummary_layout, noofprint_layout,
			sm_slno_layout, sm_code_layout, sm_name_layout, sm_cty_layout,
			sm_lqty_layout, sm_qty_layout, sm_foc_layout, sm_price_layout,
			sm_total_layout, sm_itemdisc_layout, sm_subtotal_layout,
			sm_tax_layout, sm_nettotal_layout, sm_signature_layout,
			sm_bottom_layout, sm_arrow_layout;
	String valid_url = "", summaryResult = "";
	double billDiscount = 0, taxAmt = 0;
	String subTot = "", totTax = "", netTot = "", tot = "", doNo = "";
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	private static final int PICK_FROM_CAMERA = 1;
	public static final int SIGNATURE_ACTIVITY = 2;
	double itemDisc = 0, sbTtl = 0, netTotal = 0;
	GPSTracker gps;
	String beforeChange = "";
	double tota = 0;
	private ListView mListView;
	ArrayList<HashMap<String, String>> soDetailsArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> soHeaderArr = new ArrayList<HashMap<String, String>>();
	String slNo = "", soNo = "", appType;
	CheckBox enableprint;
	private UIHelper helper;
	int stuprange = 3, stdownrange = 1, stwght = 1;
	TextView stnumber, custname, mEmpty;
	String photo_img="", sign_img="", header;
	boolean arrowflag = true, bottomflag = true;
	String addview = "true";
	ProductListAdapter adapter;
	private Intent intent;
	TextView listing_screen, customer_screen, addProduct_screen,
			summary_screen;
	private SlidingMenu menu;
	String Prodtotal = "", subTtl = "", prodTax = "", ProdNetTotal = "";
	private SOTDatabase sqldb;
	private ArrayList<HashMap<String, String>> doHeaderArr;
	private ArrayList<HashMap<String, String>> doDetailsArr;
	private ArrayList<HashMap<String, String>> doBarcodeArr;
	private String provider;
	String signature_img, product_img, address1 = "", address2 = "",
			getSignatureimage = "", getPhotoimage="";
	double setLatitude, setLongitude;

	// get location
	Location mLastLocation;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	String lat, lon;
	private LocationManager locationManager;

	private boolean isGPSEnabled = false, isNetworkEnabled = false;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	
	private int mWidth, mHeight;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private String mCurrentgetPath="",mCurrentPhotoPath="";

	@SuppressLint({"UseValueOf", "WrongThread"})
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		Mint.initAndStartSession(DeliverySummary.this, "29088aa0");
		setContentView(R.layout.delivery_summary);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(false);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.summary_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Delivery Order Summary");
		back = (ImageButton) customNav.findViewById(R.id.back);
		save = (ImageButton) customNav.findViewById(R.id.save);
		barcode_edit = (ImageButton) customNav.findViewById(R.id.barcode_edit);
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

		sm_billDiscPercentage = (EditText) findViewById(R.id.sm_billDiscPercentage);
		arrow = (ImageView) findViewById(R.id.imageView1);
		sm_slno_layout = (LinearLayout) findViewById(R.id.sm_slno_layout);
		sm_code_layout = (LinearLayout) findViewById(R.id.sm_code_layout);
		sm_name_layout = (LinearLayout) findViewById(R.id.sm_name_layout);
		sm_cty_layout = (LinearLayout) findViewById(R.id.sm_cty_layout);
		sm_lqty_layout = (LinearLayout) findViewById(R.id.sm_lqty_layout);
		sm_qty_layout = (LinearLayout) findViewById(R.id.sm_qty_layout);
		sm_foc_layout = (LinearLayout) findViewById(R.id.sm_foc_layout);
		sm_price_layout = (LinearLayout) findViewById(R.id.sm_price_layout);
		sm_total_layout = (LinearLayout) findViewById(R.id.sm_total_layout);
		sm_itemdisc_layout = (LinearLayout) findViewById(R.id.sm_itemdisc_layout);
		sm_subtotal_layout = (LinearLayout) findViewById(R.id.sm_subtotal_layout);
		sm_tax_layout = (LinearLayout) findViewById(R.id.sm_tax_layout);
		sm_nettotal_layout = (LinearLayout) findViewById(R.id.sm_nettotal_layout);
		sm_arrow_layout = (LinearLayout) findViewById(R.id.sm_arrow_layout);
		sm_header_layout = (LinearLayout) findViewById(R.id.sm_header_layout);
		slsummary_layout = (LinearLayout) findViewById(R.id.slsummary_layout);

		arrowUpDown = (ImageView) findViewById(R.id.arrow_image);
		sm_bottom_layout = (LinearLayout) findViewById(R.id.sm_bottom_layout);
		sm_signature_layout = (LinearLayout) findViewById(R.id.sm_signature_layout);
		summary_screen = (TextView) findViewById(R.id.sum_screen);
		// summary_screen.setBackgroundColor(Color.parseColor("#00AFF0"));

		summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
		summary_screen.setBackgroundResource(drawable.tab_select_right);

		mListView = (ListView) findViewById(android.R.id.list);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		custname = (TextView) findViewById(R.id.cust_name);
		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		doHeaderArr = new ArrayList<HashMap<String, String>>();
		doDetailsArr = new ArrayList<HashMap<String, String>>();
		doBarcodeArr = new ArrayList<HashMap<String, String>>();
		helper = new UIHelper(DeliverySummary.this);
		sqldb = new SOTDatabase(DeliverySummary.this);
		
		// Get Device Width
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay();
	    DisplayMetrics metrics = new DisplayMetrics();
	    display.getMetrics(metrics);
	    mWidth = metrics.widthPixels;
	    mHeight = metrics.heightPixels;
	    Log.d("width", ""+mWidth);   

		doHeaderArr.clear();
		doDetailsArr.clear();
		doBarcodeArr.clear();
		FWMSSettingsDatabase.init(DeliverySummary.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SOTSummaryWebService(valid_url);

		SOTDatabase.init(DeliverySummary.this);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		header = SalesOrderSetGet.getHeader_flag();
		photo_img = SOTDatabase.getProductImage();
		sign_img = SOTDatabase.getSignatureImage();
		appType = LogOutSetGet.getApplicationType();
		
		if (photo_img != null && !photo_img.isEmpty()) {
			byte[] encodePhotoByte = Base64.decode(photo_img, Base64.DEFAULT);
			Bitmap photo = BitmapFactory.decodeByteArray(encodePhotoByte, 0,
					encodePhotoByte.length);
			//photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
			prodphoto.setImageBitmap(photo);
		} else {

		}
		
		if (sign_img != null && !sign_img.isEmpty()) {
			byte[] encodeByte = Base64.decode(sign_img, Base64.DEFAULT);
			Bitmap sign = BitmapFactory.decodeByteArray(encodeByte, 0,
					encodeByte.length);
			sign = Bitmap.createScaledBitmap(sign, 300, 80, true);

			sosign.setImageBitmap(sign);
		} else {

		}

		// Getting LocationManager object
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		buildGoogleApiClient();

		if (!mGoogleApiClient.isConnected())
			mGoogleApiClient.connect();

		Bundle extras = getIntent().getExtras();

		if (extras != null) {

			SOTDatabase.deleteAllProduct();
			SOTDatabase.deleteBillDisc();

			soDetailsArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("SODetails");
			soHeaderArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("SOHeader");

			doHeaderArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("DOHeader");
			doDetailsArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("DODetails");
			doBarcodeArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("DOBarcode");

			getSignatureimage = (String) getIntent().getSerializableExtra(
					"getSignatureimage");
			
			getPhotoimage = (String) getIntent().getSerializableExtra(
					"getPhotoimage");

			SOTDatabase.storeImage(1, getSignatureimage, getPhotoimage);

			Log.d("SO details", "" + soDetailsArr);
			Log.d("SO Header", "" + soHeaderArr);
			Log.d("DO Header", "" + doHeaderArr);
			Log.d("DO Detail", "" + doDetailsArr);
			Log.d("DO Barcode", "" + doBarcodeArr);
			int sl_no;

			if (soHeaderArr != null) {
				for (int i = 0; i < soHeaderArr.size(); i++) {

					soNo = soHeaderArr.get(i).get("SoNo");

					ConvertToSetterGetter.setSoNo(soNo);

					// String dateTime = soHeaderArr.get(i).get("SoDate");
					// StringTokenizer tokens = new StringTokenizer(dateTime,
					// " ");
					// String SoDate = tokens.nextToken();

					// String SoDate = SalesOrderSetGet.getCurrentdate();

					String SoDate = SalesOrderSetGet.getCurrentdate();
//					String SoDate = soHeaderArr.get(i).get("SoDate");
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

					sm_billDisc.setText(BillDIscount);

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
				}
			}

			if (soDetailsArr != null) {
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
					String SubTotal = soDetailsArr.get(i).get("SubTotal");
					String Tax = soDetailsArr.get(i).get("Tax");
					String NetTotal = soDetailsArr.get(i).get("NetTotal");
					String TaxType = soDetailsArr.get(i).get("TaxType");
					String TaxPerc = soDetailsArr.get(i).get("TaxPerc");
					SalesOrderSetGet.setCompanytax(TaxType);
//					SalesOrderSetGet.setTaxValue(TaxPerc);
					String DOQty = soDetailsArr.get(i).get("DOQty");
					String DOFocQty = soDetailsArr.get(i).get("DOFocQty");

					String ExchangeQty = soDetailsArr.get(i).get("ExchangeQty");
					String CartonPrice = soDetailsArr.get(i).get("CartonPrice");
					String MinimumSellingPrice = soDetailsArr.get(i).get(
							"MinimumSellingPrice");
					String ItemRemarks = soDetailsArr.get(i).get("ItemRemarks");

					if (ItemRemarks.matches("null")) {
						ItemRemarks = "";
					}

					sl_no = i + 1;
					// SOTDatabase.storeBillDisc(sl_no, SubTotal);

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
					Double dpcsPerCarton = new Double(PcsPerCarton);
					Double dDOQty = new Double(DOQty);
					Double dDOFocQty = new Double(DOFocQty);

					int cQty = dcQty.intValue();
					int lQty = dlQty.intValue();
					int qty = dqty.intValue();
					int focQty = dfocQty.intValue();
					int pcsPerCarton = dpcsPerCarton.intValue();
					int DOqty = dDOQty.intValue();
					int DOfocQty = dDOFocQty.intValue();

					Log.d("C Qty", "" + cQty);
					Log.d("L Qty", "" + lQty);
					Log.d("Qty", "" + qty);
					Log.d("focQty", "" + focQty);
					Log.d("pcsPerCarton", "" + pcsPerCarton);
					Log.d("DOqty", "" + DOqty);
					Log.d("DOfocQty", "" + DOfocQty);

					if (DOQty != null && !DOQty.isEmpty()) {
						try {
							qty = qty - DOqty;
							cQty = (qty / pcsPerCarton);
							lQty = (qty % pcsPerCarton);
						} catch (ArithmeticException e) {
							System.out.println("Err: Divided by Zero");

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					if (DOFocQty != null && !DOFocQty.isEmpty()) {
						focQty = focQty - DOfocQty;
					}

					Log.d("CartonPrice", "ctn prc" + CartonPrice);

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

					double price = Double.parseDouble(Price);
					double itemDiscount = Double.parseDouble(ItemDiscount);
					double total = Double.parseDouble(Prodtotal);
					double tax = Double.parseDouble(prodTax);

					Log.d("price", "" + price);
					Log.d("itemDiscount", "" + itemDiscount);
					Log.d("total", "" + total);
					Log.d("Sub total", "" + subTtl);
					Log.d("Net total", "" + ProdNetTotal);
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
						Log.d("app S qty", ""+qty);
						if (qty != 0) {
							Log.d("app S qty not zero", ""+qty);
							SOTDatabase.storeProduct(slno, ProductCode,
									ProductName, cQty, lQty, qty, focQty,
									price, itemDiscount, "", pcsPerCarton,
									total, tax, ProdNetTotal, TaxType, TaxPerc,
									RetailPrice, subTtl, CartonPrice,
									ExchangeQty, MinimumSellingPrice,
									ItemRemarks, slNo,String.valueOf(qty),"","","");
						}
					} else {
						if (DOqty == 0) {
							SOTDatabase.storeProduct(slno, ProductCode,
									ProductName, cQty, lQty, qty, focQty,
									price, itemDiscount, "", pcsPerCarton,
									total, tax, ProdNetTotal, TaxType, TaxPerc,
									RetailPrice, subTtl, CartonPrice,
									ExchangeQty, MinimumSellingPrice,
									ItemRemarks,  slNo,String.valueOf(qty),"","","");
						}
					}
				}
			}
			// Edit Do Header
			if (doHeaderArr != null) {
				for (int i = 0; i < doHeaderArr.size(); i++) {

					String doNo = doHeaderArr.get(i).get("DoNo");

					ConvertToSetterGetter.setEdit_do_no(doNo);

					// String DoDate =
					// SalesOrderSetGet.getCurrentdate().split("\\ ")[0];

					String DoDate = doHeaderArr.get(i).get("DoDate")
							.split("\\ ")[0];

					/*
					 * String DeliveryDate = doHeaderArr.get(i) .get("DoDate");
					 */

					String LocationCode = doHeaderArr.get(i)
							.get("LocationCode");
					String CustomerCode = doHeaderArr.get(i)
							.get("CustomerCode");
					String Total = doHeaderArr.get(i).get("Total");
					String ItemDiscount = doHeaderArr.get(i)
							.get("ItemDiscount");
					String BillDIscount = doHeaderArr.get(i)
							.get("BillDIscount");

					/*
					 * String TotalDiscount = doHeaderArr.get(i)
					 * .get("TotalDiscount");
					 * 
					 * String SubTotal = doHeaderArr.get(i) .get("SubTotal");
					 * String Tax = doHeaderArr.get(i) .get("Tax"); String
					 * NetTotal = doHeaderArr.get(i) .get("NetTotal");
					 */

					String Remarks = doHeaderArr.get(i).get("Remarks");
					String CurrencyCode = doHeaderArr.get(i)
							.get("CurrencyCode");
					String CurrencyRate = doHeaderArr.get(i)
							.get("CurrencyRate");

					sm_billDisc.setText(BillDIscount);

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
					SalesOrderSetGet.setSaleorderdate(DoDate);
					// SalesOrderSetGet.setDeliverydate(DeliveryDate);
					SalesOrderSetGet.setLocationcode(LocationCode);
					SalesOrderSetGet.setCustomercode(CustomerCode);
					SalesOrderSetGet.setRemarks(Remarks);
					SalesOrderSetGet.setCurrencycode(CurrencyCode);
					SalesOrderSetGet.setCurrencyrate(CurrencyRate);
					SalesOrderSetGet.setCurrencyname("");
				}
			}
			// Edit Do Detail
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

					// sm_billDisc.setText(BillDiscount);

					sl_no = i + 1;
					// SOTDatabase.storeBillDisc(sl_no, SubTotal);

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
					Double dpcsPerCarton = new Double(PcsPerCarton);

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
							ExchangeQty, MinimumSellingPrice, ItemRemarks,slNo,
						    String.valueOf(qty),"","","");
				}
			}
			// Edit Do Barcode
			if (doBarcodeArr != null) {
				for (int i = 0; i < doBarcodeArr.size(); i++) {

					HashMap<String, String> queryValues = new HashMap<String, String>();
					String slNo = doBarcodeArr.get(i).get("slNo");
					String SeqNo = doBarcodeArr.get(i).get("SeqNo");
					String ProductName = doBarcodeArr.get(i).get("ProductName");
					String Weight = doBarcodeArr.get(i).get("Weight");
					String ProductCode = doBarcodeArr.get(i).get("ProductCode");
					String WeightBarcode = doBarcodeArr.get(i).get(
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
		}
		  /********Based on ShowPriceDO Amount will Gone or Visible *********/
		  if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("false")){ 
		   ((LinearLayout) findViewById(R.id.linearlayout1)).setVisibility(View.INVISIBLE);
		   ((LinearLayout) findViewById(R.id.linearlayout2)).setVisibility(View.GONE);
		   ((LinearLayout) findViewById(R.id.linearlayout3)).setVisibility(View.VISIBLE); 
		   sm_price_layout.setVisibility(View.GONE);
		   sm_subtotal_layout.setVisibility(View.GONE);
		   sm_cty_layout.setVisibility(View.GONE);
		   sm_lqty_layout.setVisibility(View.GONE);
		   arrow.setVisibility(View.GONE); 
		   double totalQty = 0.00;
	/*	   Cursor cursorEditDO = SOTDatabase.getSODetailQuantity(); 
		    if(SalesOrderSetGet.getDoQtyValidateWithSo().matches("1")){
		    //From DO Edit
		     if (cursorEditDO != null && cursorEditDO.getCount() > 0) {
		    	 
		    	String productCode =  SOTDatabase.checkDoQtyWithSo("0","0");
		    	 if(productCode.matches("0")){
		    		//From DO Edit With Null SoNo
		    		  totalQty = SOTDatabase.getTotalQty("quantity");
		    	 }else{
		    		//From DO Edit With SoNo
		    		  totalQty = SOTDatabase.getTotalQty("QtyOnHand");
		    	 }		     
		     }else{
		      // Convert SO to DO
		      totalQty = SOTDatabase.getTotalQty("quantity");
		     } 
		    }else{
		     totalQty = SOTDatabase.getTotalQty("quantity");
		    }*/
		      
		   totalQty = SOTDatabase.getTotalQty();
		   Log.d("totalQty","-->"+totalQty);   
		   ((TextView) findViewById(R.id.sm_TotalQty)).setText(String.valueOf(totalQty));
		   ((LinearLayout) findViewById(R.id.sm_name_layout)).getLayoutParams().width  = (mWidth-180);
		   ((LinearLayout) findViewById(R.id.sm_qty_layout)).getLayoutParams().width = 100;   
		   ((LinearLayout) findViewById(R.id.sm_header_layout)).getLayoutParams().width = mWidth;    
		   
		  }else{
		   ((LinearLayout) findViewById(R.id.linearlayout1)).setVisibility(View.VISIBLE);
		   ((LinearLayout) findViewById(R.id.linearlayout2)).setVisibility(View.VISIBLE);
		   ((LinearLayout) findViewById(R.id.linearlayout3)).setVisibility(View.GONE);
		   
		  if ((SalesOrderSetGet.getCartonpriceflag() != null && !SalesOrderSetGet
		    .getCartonpriceflag().isEmpty())) {
		   if (SalesOrderSetGet.getCartonpriceflag().matches("1")) {
		    sm_cty_layout.setVisibility(View.VISIBLE);
		    sm_lqty_layout.setVisibility(View.VISIBLE);
		    sm_qty_layout.setVisibility(View.GONE);
		    Log.d("C ---->", "C");
		   } else if (SalesOrderSetGet.getCartonpriceflag().matches("0")) {
		    sm_cty_layout.setVisibility(View.GONE);
		    sm_lqty_layout.setVisibility(View.GONE);
		    sm_qty_layout.setVisibility(View.VISIBLE);
		    Log.d("L ---->", "L");
		   }
		  }
		  }
		/*if ((SalesOrderSetGet.getDefaultshowcartonorloose() != null && !SalesOrderSetGet
				.getDefaultshowcartonorloose().isEmpty())) {
			if (SalesOrderSetGet.getDefaultshowcartonorloose().matches("C")) {
				sm_cty_layout.setVisibility(View.VISIBLE);
				sm_lqty_layout.setVisibility(View.VISIBLE);
				sm_qty_layout.setVisibility(View.GONE);

				Log.d("C ---->", "C");
			} else if (SalesOrderSetGet.getDefaultshowcartonorloose().matches(
					"L")) {
				sm_cty_layout.setVisibility(View.GONE);
				sm_lqty_layout.setVisibility(View.GONE);
				sm_qty_layout.setVisibility(View.VISIBLE);

				Log.d("L ---->", "L");
			}
		}*/
		sm_code_layout.setVisibility(View.GONE);
		sm_foc_layout.setVisibility(View.GONE);
		sm_total_layout.setVisibility(View.GONE);
		sm_itemdisc_layout.setVisibility(View.GONE);

		sm_tax_layout.setVisibility(View.GONE);
		sm_nettotal_layout.setVisibility(View.GONE);

		arrowUpDown.setImageResource(R.drawable.arrow_up);
		sm_bottom_layout.setVisibility(View.GONE);
		sm_signature_layout.setVisibility(View.VISIBLE);

		signature_img = SOTDatabase.getSignatureImage();
		product_img = SOTDatabase.getProductImage();

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

				Bitmap photo = BitmapFactory.decodeByteArray(encodeByte, 0,
						encodeByte.length);
				photo = Bitmap.createScaledBitmap(photo, 240, 80, true);
				sosign.setImageBitmap(photo);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {

		}

		cursor = SOTDatabase.getCursor();
		
		Log.d("cursor size", ""+cursor.getCount());

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
			if (extras == null) {
				sm_total.setText("" + sub);
			}

			//
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
			//

			String ProdNettotal = twoDecimalPoint(netTotal);
			sm_netTotal.setText("" + ProdNettotal);

			// Added New 12.04.2017
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

			if (appType.matches("W")) {
				barcodeCursor = sqldb.getBarcodeCursor();
				if (barcodeCursor.getCount() == 0) {

					if (barcode_edit.getVisibility() == View.VISIBLE) {
						barcode_edit.setVisibility(View.GONE);
					}
				} else {
					if (barcode_edit.getVisibility() == View.GONE) {
						barcode_edit.setVisibility(View.VISIBLE);
					}
				}
			}

		} else {
			sm_header_layout.setVisibility(View.GONE);
			save.setVisibility(View.INVISIBLE);
			sm_billDisc.setFocusable(false);
			sm_billDiscPercentage.setFocusable(false);
			mEmpty.setVisibility(View.VISIBLE);
		}

		// setListAdapter(new ProductListAdapter(this, cursor));
		adapter = new ProductListAdapter(this, cursor);
		mListView.setAdapter(adapter);
		registerForContextMenu(mListView);

		taxCalc(); // recalculate tax value
		smTax = SOTDatabase.getTax();
		String ProdTax = fourDecimalPoint(smTax);
		sm_tax.setText("" + ProdTax);

		/*try {

			// Getting LocationManager object
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			// // Creating an empty criteria object
			// Criteria criteria = new Criteria();
			//
			// // Getting the name of the provider that meets the criteria
			// provider = locationManager.getBestProvider(criteria, false);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {

				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 20000, 0, this);
					Log.d("GPS Enabled", "GPS Enabled");
					if (locationManager != null) {
						Location location = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							Log.d("location", "not null");
							// if (onlineMode.matches("True")) {
							// if (checkOffline == true) {
							// } else { // online
							onLocationChanged(location);
							// }
							// }
						}
					}
				} else if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 20000, 0, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						Location location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							Log.d("location", "not null");
							// if (onlineMode.matches("True")) {
							// if (checkOffline == true) {
							// } else { // online
							onLocationChanged(location);
							// }
							// }
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}*/

		arrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (arrowflag) {

					arrowflag = false;
					arrow.setImageResource(R.drawable.ic_arrow_left);

//					sm_code_layout.setVisibility(View.VISIBLE);
					sm_cty_layout.setVisibility(View.VISIBLE);
					sm_lqty_layout.setVisibility(View.VISIBLE);
					sm_foc_layout.setVisibility(View.VISIBLE);
					sm_total_layout.setVisibility(View.VISIBLE);
					sm_itemdisc_layout.setVisibility(View.VISIBLE);
					sm_tax_layout.setVisibility(View.VISIBLE);
					sm_nettotal_layout.setVisibility(View.VISIBLE);
					sm_qty_layout.setVisibility(View.VISIBLE);
					adapter.selectAll(false);

				} else if (arrowflag == false) {

					arrow.setImageResource(R.drawable.ic_arrow_right);
					arrowflag = true;

					sm_code_layout.setVisibility(View.GONE);
					// sm_cty_layout.setVisibility(View.GONE);
					// sm_lqty_layout.setVisibility(View.GONE);
					sm_foc_layout.setVisibility(View.GONE);
					sm_total_layout.setVisibility(View.GONE);
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
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int arg2,
					long arg3) {
				// String appType = LogOutSetGet.getApplicationType();

				if (appType.matches("W")) {

					Log.d("AppType--w->", "" + appType);

					Intent i = new Intent(DeliverySummary.this,
							AddBarcode.class);
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

					i.putExtra("SOT_str_itemremarks", "");
					i.putExtra("Barcodefrom", "DeliverySummary");
					startActivity(i);
					DeliverySummary.this.finish();
				} else {
					Log.d("AppType--s->", "" + appType);
				}

			}
		});
		barcode_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(DeliverySummary.this,
						AddBarcodeSummary.class);
				i.putExtra("Barcodefrom", "DeliverySummary");
				startActivity(i);
				DeliverySummary.this.finish();
			}
		});

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

					if(sm_billDisc.length() == 0){
						billDisc = "0";
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
//						billDiscount = billDiscCalc / sbtot;
						
						if(billDiscCalc>0){
							billDiscount = billDiscCalc / sbtot;
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

							// Added New 12.04.2017
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
					
					Log.d("subT", ""+subT);
					
					String sub = sm_subTotal.getText().toString();
					String discPer = sm_billDiscPercentage.getText().toString();

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

						// Added New 12.04.2017
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

		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CameraAction(PICK_FROM_CAMERA);
			}
		});

		signature.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent i = new Intent(DeliverySummary.this,
						CaptureSignature.class);
				startActivityForResult(i, SIGNATURE_ACTIVITY);

			}
		});

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveAlertDialog();
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sm_billDisc.setText("");
				sm_billDiscPercentage.setText("");
				Intent i = new Intent(DeliverySummary.this,
						DeliveryAddProduct.class);
				startActivity(i);
				DeliverySummary.this.finish();
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

					Intent i = new Intent(DeliverySummary.this,
							DeliveryOrderHeader.class);
					startActivity(i);
					DeliverySummary.this.finish();

				}

			}
		});

		customer_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(DeliverySummary.this,
						DeliveryCustomer.class);
				startActivity(i);
				DeliverySummary.this.finish();

			}
		});

		addProduct_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(DeliverySummary.this,
						DeliveryAddProduct.class);
				startActivity(i);
				DeliverySummary.this.finish();

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
	}

	/*public void getAddress(double latitude, double longitude) throws Exception {
		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(this, Locale.getDefault());

		setLatitude = latitude;
		setLongitude = longitude;

		addresses = geocoder.getFromLocation(latitude, longitude, 1);
		if (addresses != null && addresses.size() > 0) {

			address1 = addresses.get(0).getAddressLine(0);
			address2 = addresses.get(0).getAddressLine(1);

			sm_location.setText(address1 + "," + address2);
			locationManager.removeUpdates(this);
		}
	}*/

	@SuppressWarnings("deprecation")
	/*public void taxCalc() {

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

					billCursor = SOTDatabase.getBillCursor();

					if (billCursor != null && billCursor.getCount() > 0) {

						double sbTotal = SOTDatabase.getsubTotal(sl_no);
						double billDisc = sbTotal * billDiscount;

						subTot = sbTotal - billDisc;

					}

					String taxType = SalesOrderSetGet.getCompanytax();
					if (taxType != null && !taxType.isEmpty()) {
						if (taxType.matches("I") || taxType.matches("Z")) {
							if (!taxValue.matches("")) {
								txVl = Double.parseDouble(taxValue);

								if (!total.matches("")) {
									double tt = Double.parseDouble(total);
									tx = (tt * txVl) / (100 + txVl);
								}

							}
						} else {
							if (!taxValue.matches("")) {
								txVl = Double.parseDouble(taxValue);
								tx = (subTot * txVl) / 100;
							}
						}
					} else {
						if (!taxValue.matches("")) {
							txVl = Double.parseDouble(taxValue);
							tx = (subTot * txVl) / 100;
						}
					}

					Log.d("tx", "" + tx);

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
							String subtotal = twoDecimalPoint(subTot);
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
	}*/

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
						// subTot = sbTotal - billDiscount;
						double billDisc = sbTotal * billDiscount;
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
						/*if (!taxValue.matches("")) {
							txVl = Double.parseDouble(taxValue);
							tx = (subTot * txVl) / 100;
						}*/
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
							String subtotal = twoDecimalPoint(subTot);
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

	@Override
	public void refreshAdapter() {
		cursor.requery();
		updateValues();
		adapter.notifyDataSetChanged();
	}

	private class ProductListAdapter extends ResourceCursorAdapter {

		@SuppressWarnings("deprecation")
		public ProductListAdapter(Context context, Cursor cursor) {
			super(context, R.layout.summary_list_item, cursor);
		}

		@Override
		public void bindView(View view, final Context context, Cursor cursor) {

			LinearLayout ss_listitem_layout = (LinearLayout) view.findViewById(R.id.ss_listitem_layout);

			   LinearLayout ss_prodcode_layout = (LinearLayout) view
			     .findViewById(R.id.ss_prodcode_layout);
			   
			   LinearLayout ss_prodname_layout = (LinearLayout) view
			     .findViewById(R.id.ss_name_layout);
			   
			   LinearLayout ss_cqty_layout = (LinearLayout) view
			     .findViewById(R.id.ss_cqty_layout);
			   LinearLayout ss_qty_layout = (LinearLayout) view
			     .findViewById(R.id.ss_qty_layout);
			   LinearLayout ss_lqty_layout = (LinearLayout) view
			     .findViewById(R.id.ss_lqty_layout);
			   
			   LinearLayout ss_price_layout = (LinearLayout) view
			     .findViewById(R.id.ss_price_layout);
			   
			   LinearLayout ss_foc_layout = (LinearLayout) view
			     .findViewById(R.id.ss_foc_layout);
			   LinearLayout ss_total_layout = (LinearLayout) view
			     .findViewById(R.id.ss_total_layout);
			   LinearLayout ss_itemdisc_layout = (LinearLayout) view
			     .findViewById(R.id.ss_itemdisc_layout);
			   LinearLayout ss_tax_layout = (LinearLayout) view
			     .findViewById(R.id.ss_tax_layout);
			   
			   LinearLayout ss_subtotal_layout = (LinearLayout) view
			     .findViewById(R.id.ss_subTotal_layout);
			   
			   LinearLayout ss_netTotal_layout = (LinearLayout) view
			     .findViewById(R.id.ss_netTotal_layout);

			if (addview == "false") {

				ss_prodcode_layout.setVisibility(View.VISIBLE);
				ss_cqty_layout.setVisibility(View.VISIBLE);
				ss_lqty_layout.setVisibility(View.VISIBLE);
				ss_foc_layout.setVisibility(View.VISIBLE);
				ss_total_layout.setVisibility(View.VISIBLE);
				ss_itemdisc_layout.setVisibility(View.VISIBLE);
				ss_tax_layout.setVisibility(View.VISIBLE);
				ss_netTotal_layout.setVisibility(View.VISIBLE);
				ss_qty_layout.setVisibility(View.VISIBLE);
				
				ss_listitem_layout.getLayoutParams().width = 1850;
			} else {
				
				// Get Device Width
//				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//			    Display display = wm.getDefaultDisplay();
//			    DisplayMetrics metrics = new DisplayMetrics();
//			    display.getMetrics(metrics);
//			    int width = metrics.widthPixels;
//			    int height = metrics.heightPixels;
			    Log.d("width", ""+mWidth);
			    				
			    ss_listitem_layout.getLayoutParams().width = mWidth;
			    
			    /********Based on ShowPriceDO Amount will Gone or Visible *********/
			    if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("false")){  
			     ss_price_layout.setVisibility(View.GONE);
			     ss_subtotal_layout.setVisibility(View.GONE);
			     ss_cqty_layout.setVisibility(View.GONE);
			     ss_lqty_layout.setVisibility(View.GONE);  
			     
			    }else{  
			    
			    if ((SalesOrderSetGet.getCartonpriceflag() != null && !SalesOrderSetGet
			      .getCartonpriceflag().isEmpty())) {
			     if (SalesOrderSetGet.getCartonpriceflag().matches(
			       "1")) {

			      ss_cqty_layout.setVisibility(View.VISIBLE);
			      ss_lqty_layout.setVisibility(View.VISIBLE);
			      ss_qty_layout.setVisibility(View.GONE);
			      Log.d("C ---->", "1");
			     } else if (SalesOrderSetGet.getCartonpriceflag()
			       .matches("0")) {
			      ss_cqty_layout.setVisibility(View.GONE);
			      ss_lqty_layout.setVisibility(View.GONE);

			      Log.d("L ---->", "0");
			     }
			    }
			    
			    ss_price_layout.setVisibility(View.VISIBLE);
			    ss_subtotal_layout.setVisibility(View.VISIBLE);   
			    
			    }
			    ss_prodcode_layout.setVisibility(View.GONE);
			    // ss_cqty_layout.setVisibility(View.GONE);
			    // ss_lqty_layout.setVisibility(View.GONE);
			    ss_foc_layout.setVisibility(View.GONE);
			    ss_total_layout.setVisibility(View.GONE);
			    ss_itemdisc_layout.setVisibility(View.GONE);
			    ss_tax_layout.setVisibility(View.GONE);
			    ss_netTotal_layout.setVisibility(View.GONE);
			}

			final TextView ss_sl_id = (TextView) view.findViewById(R.id.ss_slid);
			ss_sl_id.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_ID)));
			
			 TextView ss_slNo = (TextView) view.findViewById(R.id.slNo);
			   ss_slNo.setText(cursor.getString(cursor
			     .getColumnIndex(SOTDatabase.COLUMN_SO_SLNO)));
			   
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

			TextView ss_l_qty = (TextView) view.findViewById(R.id.ss_l_qty);
			ss_l_qty.setText(cursor.getString(cursor
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
			
			
			 TextView ss_dv_original_qty = (TextView) view
				     .findViewById(R.id.ss_deli_qty);
				   ss_dv_original_qty.setText(cursor.getString(cursor
				     .getColumnIndex(SOTDatabase.COLUMN_STOCK)));

			if (!appType.matches("")) {
				if (appType.matches("W")) {
					int barcodeStatust = SOTDatabase.getBarcodeStatus(ss_sl_id
							.getText().toString());
					if (barcodeStatust == 1) {
						view.setBackgroundResource(drawable.list_item_selected_bg);
					} else {

						view.setBackgroundResource(drawable.list_selector);
					}
				}

			}
			ImageView mEditImageV = (ImageView) view.findViewById(R.id.edit);

			mEditImageV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String id = ss_sl_id.getText().toString();
					SummaryEditDialogFragment dialogFragment = SummaryEditDialogFragment.newInstance(id);
					dialogFragment.show(getFragmentManager(), "dialog");

				}
			});

			ImageView mDeleteImageV = (ImageView) view.findViewById(R.id.delete);
			mDeleteImageV.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String id = ss_sl_id.getText().toString();
					deleteDialog(context,id);


				}
			});

		}

		public void selectAll(boolean select) {

			if (select == true) {
				addview = "true";
			} else if (select == false) {
				addview = "false";
			}
			notifyDataSetChanged();
		}

		// Delete
		public void deleteDialog(Context context,final String id) {
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which){
						case DialogInterface.BUTTON_POSITIVE:
							SOTDatabase.deleteProduct(id);
							SOTDatabase.deleteBillDiscount(id);
							cursor.requery();
							notifyDataSetChanged();
							updateValues();
							dialog.dismiss();
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							dialog.dismiss();
							break;
					}



				}
			};
			AlertDialog.Builder ab = new AlertDialog.Builder(context);
			ab.setMessage("Are you sure to delete?")
					.setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();
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
	public void updateValues(){
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

			// Added New 12.04.2017
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

//			Toast.makeText(DeliverySummary.this, "Deleted",
//					Toast.LENGTH_LONG).show();
			mEmpty.setVisibility(View.GONE);
		} else {
			barcode_edit.setVisibility(View.GONE);
			sm_header_layout.setVisibility(View.GONE);
			save.setVisibility(View.INVISIBLE);
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
			sm_subTotal_inclusive.setText("");
		}
	}
	private class AsyncCallWSSummary extends AsyncTask<Void, Void, Void> {

		String bllDsc="";
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(DeliverySummary.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(slsummary_layout, false);
			progressBar = new ProgressBar(DeliverySummary.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));

			spinnerLayout.addView(progressBar);
			bllDsc = sm_billDisc.getText().toString();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			double discnt = 0.0;

			if (!bllDsc.matches("")) {
				discnt = Double.parseDouble(bllDsc);
			}
			if (ConvertToSetterGetter.getEdit_do_no() != null
					&& !ConvertToSetterGetter.getEdit_do_no().isEmpty()) {

				doNo = ConvertToSetterGetter.getEdit_do_no();
			} else {
				doNo = "";
			}
			try {
				summaryResult = SOTSummaryWebService.summaryDOService(
						"fncSaveDO", discnt, subTot, totTax, netTot,
						Double.toString(tota), slNo,
						ConvertToSetterGetter.getSoNo(), doNo,"DO");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(slsummary_layout, true);
			if (summaryResult.matches("failed")) {

				Toast.makeText(DeliverySummary.this, "Failed",
						Toast.LENGTH_SHORT).show();

			} else {

				signature_img = SOTDatabase.getSignatureImage();
				//product_img = SOTDatabase.getProductImage();

				mCurrentgetPath = Product.getPath();
				//Log.d("pathfind",mCurrentgetPath);
				product_img = ImagePathToBase64Str(mCurrentgetPath);

				if (signature_img == null) {
					signature_img = "";
				}
				if (product_img == null) {
					product_img = "";
				}
				
				Log.d("signature_img", ":"+signature_img);
				Log.d("product_img", ":"+product_img);

				// if (onlineMode.matches("True")) {
				// if (checkOffline == true) {
				// } else { // online
				String imgResult = SOTSummaryWebService.saveSignatureImage(
						summaryResult, "" + setLatitude, "" + setLongitude,
						signature_img, product_img, "fncSaveInvoiceImages",
						"DO", address1, address2);

				Log.d("fncSaveInvoiceImages", "" + summaryResult + " "
						+ setLatitude + " " + setLongitude + " DO" + address1
						+ address2 + "signature_img " + signature_img
						+ "product_img " + product_img);

				if (!imgResult.matches("")) {
					Log.d("Cap Image", "Saved");
					deleteDirectory(new File(Environment.getExternalStorageDirectory()+ "/" +"SFA","Image"));
				} else {
					Log.d("Cap Image", "Not Saved");
				}
				// }
				// }

				Toast.makeText(DeliverySummary.this, "Saved Successfully",
						Toast.LENGTH_SHORT).show();

				if (enableprint.isChecked()) {

					if (FWMSSettingsDatabase
							.getPrinterAddress()
							.matches(
									"[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$")) {

						helper.showProgressDialog(R.string.generating_do);
						try {
							print();
						} catch (IOException e) {
							e.printStackTrace();
						}

					} else {
						helper.showLongToast(R.string.error_configure_printer);
						clearView();
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
		mEmpty.setVisibility(View.GONE);
		save.setVisibility(View.INVISIBLE);
		SOTDatabase.deleteAllProduct();
		mListView.setAdapter(null);
		cursor.requery();

		sm_total.setText("");
		sm_billDisc.setText("");
		sm_billDiscPercentage.setText("");
		sm_tax.setText("");
		sm_billDisc.setFocusable(false);
		sm_billDiscPercentage.setFocusable(false);
		sm_header_layout.setVisibility(View.GONE);
		sm_subTotal.setText("");
		sm_netTotal.setText("");
		sm_subTotal_inclusive.setText("");
		SOTDatabase.deleteBillDisc();
		SOTDatabase.deleteBarcode();
		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");
		SalesOrderSetGet.setCustomerTaxPerc("");
		SOTDatabase.deleteSODetailQuantity();
		ConvertToSetterGetter.setSoNo("");
		ConvertToSetterGetter.setEdit_do_no("");
		/*
		 * Intent i = new Intent(DeliverySummary.this,
		 * DeliveryOrderHeader.class); //i.putExtra("activitylaunch",
		 * "salesorderstart");
		 * 
		 * startActivity(i);
		 */
		if (header.matches("DeliveryOrderHeader")) {
			intent = new Intent(DeliverySummary.this, DeliveryOrderHeader.class);
		} else if (header.matches("SalesOrderHeader")) {
			intent = new Intent(DeliverySummary.this, SalesOrderHeader.class);
		} else if (header.matches("CustomerHeader")) {
			intent = new Intent(DeliverySummary.this,
					CustomerListActivity.class);
		} else if (header.matches("RouteHeader")) {
			intent = new Intent(DeliverySummary.this, RouteHeader.class);
		} else {
			intent = new Intent(DeliverySummary.this, LandingActivity.class);
		}
		startActivity(intent);
		DeliverySummary.this.finish();

	}

//	public void CameraAction() {
//		/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT,
//				MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
//		intent.putExtra("crop", "true");
//		intent.putExtra("aspectX", 0);
//		intent.putExtra("aspectY", 0);
//		intent.putExtra("outputX", 200);
//		intent.putExtra("outputY", 150);
//		try {
//			intent.putExtra("return-data", true);
//			startActivityForResult(intent, PICK_FROM_CAMERA);
//		} catch (ActivityNotFoundException e) {
//		}*/
//
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
//					//	photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
//
//
//						ByteArrayOutputStream stream = new ByteArrayOutputStream();
//						photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
//						prodphoto.setImageBitmap(photo);
//						byte[] bitMapData = stream.toByteArray();
//						String camera_image = Base64.encodeToString(bitMapData,
//								Base64.DEFAULT);
//						SOTDatabase.init(DeliverySummary.this);
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
						SOTDatabase.init(DeliverySummary.this);
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
			SOTDatabase.init(DeliverySummary.this);

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
		ByteArrayOutputStream baos=new  ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
		byte [] b=baos.toByteArray();
		String temp=Base64.encodeToString(b, Base64.DEFAULT);
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
		menu.add(0, v.getId(), 0, "Edit");
		menu.add(0, v.getId(), 0, "Delete");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getTitle() == "Edit") {

			Intent i = new Intent(DeliverySummary.this,
					DeliveryAddProduct.class);
			i.putExtra("SOT_ssno", ((TextView) info.targetView
					.findViewById(R.id.ss_slid)).getText().toString());
			i.putExtra("SOT_slNo", ((TextView) info.targetView
				     .findViewById(R.id.slNo)).getText().toString());
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

			i.putExtra("SOT_str_minselling_price", ((TextView) info.targetView
					.findViewById(R.id.ss_minselling_price)).getText()
					.toString());

			   
			   i.putExtra("SOT_str_deli_qty", ((TextView) info.targetView
					      .findViewById(R.id.ss_deli_qty)).getText()
					      .toString());  
			   
			String id = ((TextView) info.targetView.findViewById(R.id.ss_slid))
					.getText().toString();
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
			DeliverySummary.this.finish();
		} else if (item.getTitle() == "Delete") {
			String id = ((TextView) info.targetView.findViewById(R.id.ss_slid))
					.getText().toString();

			Log.d("id", id);
			SOTDatabase.deleteProduct(id);
			SOTDatabase.deleteBillDiscount(id);
			SOTDatabase.deleteProdId(id);
			sm_billDisc.setText("");
			sm_billDiscPercentage.setText("");
			cursor.requery();
			 if (ConvertToSetterGetter.getSoNo()==null || ConvertToSetterGetter.getSoNo().trim().equals("") && 
				     ConvertToSetterGetter.getEdit_do_no()==null || ConvertToSetterGetter.getEdit_do_no().trim().equals("")) {
			ArrayList<String> snoCount = new ArrayList<String>();
			snoCount = SOTDatabase.snoCountID();
			Log.d("snocount", "" + snoCount);
			for (int i = 0; i < snoCount.size(); i++) {
				int sno = 1 + i;
				HashMap<String, String> queryValues = new HashMap<String, String>();
				queryValues.put("_id", "" + snoCount.get(i));
				queryValues.put("snum", "" + sno);
				SOTDatabase.updateSNO(queryValues);
				SOTDatabase.updateBillSNO(queryValues);
			}
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
				// Added New 12.04.2017
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
				Toast.makeText(DeliverySummary.this, "Deleted",
						Toast.LENGTH_LONG).show();
				mEmpty.setVisibility(View.GONE);
			} else {
				barcode_edit.setVisibility(View.GONE);
				sm_header_layout.setVisibility(View.GONE);
				save.setVisibility(View.INVISIBLE);
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
				sm_subTotal_inclusive.setText("");
			}

		} else {
			return false;
		}
		return true;

	}

	public void saveAlertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setIcon(R.mipmap.ic_save);
		alertDialog.setTitle("Save");
		alertDialog.setMessage("Do you want to Save");
		LayoutInflater adbInflater = LayoutInflater.from(this);
		final View eulaLayout = adbInflater.inflate(R.layout.print_checkbox,
				null);
		noofprint_layout = (LinearLayout) eulaLayout
				.findViewById(R.id.noofcopieslblLayout);
		enableprint = (CheckBox) eulaLayout.findViewById(R.id.checkbox);
		enableprint.setText("Print");

		enableprint.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked == true) {
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
							AsyncCallWSSummary task = new AsyncCallWSSummary();
							task.execute();
						} else {
							Toast.makeText(DeliverySummary.this,
									"No data found", Toast.LENGTH_SHORT).show();
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

	@SuppressWarnings("deprecation")
	private void print() throws IOException {
		String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
		final int nofcopies = Integer.parseInt(stnumber.getText().toString());
		ArrayList<ProductDetails> product = new ArrayList<ProductDetails>();
		final ArrayList<ProductDetails> productdet = new ArrayList<ProductDetails>();
		final String customerCode = SalesOrderSetGet.getCustomercode();
		final String customerName = SalesOrderSetGet.getCustomername();
		final String soDate = SalesOrderSetGet.getSaleorderdate();
		ProductDetails productdetail = new ProductDetails();
		double itemDisc = SOTDatabase.getitemDisc();
		double discnt = 0.0;
		String bllDsc = sm_billDisc.getText().toString();
		if (!bllDsc.matches("")) {
			discnt = Double.parseDouble(bllDsc);
		}
		helper.dismissProgressDialog();
		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		product = SOTDatabase.products();
/*		 if(SalesOrderSetGet.getDoQtyValidateWithSo().matches("1")){
				
		Cursor cursorEditDO = SOTDatabase.getSODetailQuantity();			 					 
				//From DO Edit
		if (cursorEditDO != null && cursorEditDO.getCount() > 0) {
		
			String productCode =  SOTDatabase.checkDoQtyWithSo("0","0");
	    	 if(productCode.matches("0")){
    		//From DO Edit With Null SoNo
    		 product = SOTDatabase.products(SOTDatabase.COLUMN_QUANTITY);
    	 }else{
    		 if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("false")){ 
    		//From DO Edit With SoNo 
    		 product = SOTDatabase.products(SOTDatabase.COLUMN_STOCK);	
    		 }else{
    			//From DO Edit With SoNo
        		 product = SOTDatabase.products(SOTDatabase.COLUMN_QUANTITY);	
    		 }
    	 }		
		
		}else{
		//CONVERT SO TO DO
		product = SOTDatabase.products(SOTDatabase.COLUMN_QUANTITY);
		}
		}else {
		product = SOTDatabase.products(SOTDatabase.COLUMN_QUANTITY);
		}*/
		productdetail.setItemdisc(Double.toString(itemDisc));
		productdetail.setBilldisc(Double.toString(discnt));
		productdetail.setSubtotal(subTot);
		productdetail.setTax(totTax);
		productdetail.setNettot(netTot);
		productdet.add(productdetail);
		try {
			if (printertype.matches("Zebra iMZ320")) {
				Printer printer = new Printer(DeliverySummary.this, macaddress);
				printer.setOnCompletionListener(new Printer.OnCompletionListener() {

					@Override
					public void onCompleted() {
						// TODO Auto-generated method stub
						finish();
					/*
					 * Intent i = new
					 * Intent(DeliverySummary.this,DeliveryOrderHeader.class);
					 * i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 * startActivity(i);
					 */
						if (header.matches("DeliveryOrderHeader")) {
							intent = new Intent(DeliverySummary.this,
									DeliveryOrderHeader.class);

						} else if (header.matches("SalesOrderHeader")) {
							intent = new Intent(DeliverySummary.this,
									SalesOrderHeader.class);

						} else if (header.matches("CustomerHeader")) {
							intent = new Intent(DeliverySummary.this,
									CustomerListActivity.class);

						} else if (header.matches("RouteHeader")) {
							intent = new Intent(DeliverySummary.this,
									RouteHeader.class);
						} else {
							intent = new Intent(DeliverySummary.this,
									LandingActivity.class);
						}
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						DeliverySummary.this.finish();
						SOTDatabase.deleteImage();
					}
				});
				printer.printDeliveryOrder(summaryResult, soDate, customerCode,
						customerName, product, productdet, nofcopies);

			}else if(printertype.matches("Zebra iMZ320 4 Inch")){
				PrinterZPL printer = new PrinterZPL(DeliverySummary.this, macaddress);
				printer.setOnCompletionListener(new PrinterZPL.OnCompletionListener() {
					@Override
					public void onCompleted() {
						// TODO Auto-generated method stub
						finish();
						if (header.matches("DeliveryOrderHeader")) {
							intent = new Intent(DeliverySummary.this,
									DeliveryOrderHeader.class);

						} else if (header.matches("SalesOrderHeader")) {
							intent = new Intent(DeliverySummary.this,
									SalesOrderHeader.class);

						} else if (header.matches("CustomerHeader")) {
							intent = new Intent(DeliverySummary.this,
									CustomerListActivity.class);

						} else if (header.matches("RouteHeader")) {
							intent = new Intent(DeliverySummary.this,
									RouteHeader.class);
						} else {
							intent = new Intent(DeliverySummary.this,
									LandingActivity.class);
						}
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						DeliverySummary.this.finish();
						SOTDatabase.deleteImage();
					}
				});
				printer.printDeliveryOrder(summaryResult, soDate, customerCode,
						customerName, product, productdet, nofcopies);



			}
			else if(printertype.matches("3 Inch Bluetooth Generic")){
				final CubePrint print = new CubePrint(DeliverySummary.this,macaddress);
				print.initGenericPrinter();
				final ArrayList<ProductDetails> finalProduct = product;
				print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
					@Override
					public void initCompleted() {
						Log.d("customerName","-->"+customerName);
						print.printDeliveryOrder(summaryResult, soDate, customerCode,
								customerName, finalProduct, productdet, nofcopies);
						print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
							@Override
							public void onCompleted() {
								finish();
								if (header.matches("DeliveryOrderHeader")) {
									intent = new Intent(DeliverySummary.this,
											DeliveryOrderHeader.class);

								} else if (header.matches("SalesOrderHeader")) {
									intent = new Intent(DeliverySummary.this,
											SalesOrderHeader.class);

								} else if (header.matches("CustomerHeader")) {
									intent = new Intent(DeliverySummary.this,
											CustomerListActivity.class);

								} else if (header.matches("RouteHeader")) {
									intent = new Intent(DeliverySummary.this,
											RouteHeader.class);
								} else {
									intent = new Intent(DeliverySummary.this,
											LandingActivity.class);
								}
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
								DeliverySummary.this.finish();
								SOTDatabase.deleteImage();
							}
						});
					}
				});

			}
		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}
		barcode_edit.setVisibility(View.GONE);
		save.setVisibility(View.INVISIBLE);
		SOTDatabase.deleteAllProduct();
		mListView.setAdapter(null);
		cursor.requery();
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
		sm_subTotal_inclusive.setText("");
		SOTDatabase.deleteBillDisc();
		SOTDatabase.deleteBarcode();
		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");
		SalesOrderSetGet.setCustomerTaxPerc("");
		SOTDatabase.deleteSODetailQuantity();
		ConvertToSetterGetter.setSoNo("");
		ConvertToSetterGetter.setEdit_do_no("");
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

	public void productTotal(int qty, String Price, String itmDscnt,
			String taxType, String taxValue) {

		try {
			double taxAmount = 0.0, netTotal = 0.0;
			double taxAmount1 = 0.0, netTotal1 = 0.0;
			double tt = 0.0, itmDisc = 0.0;
			Prodtotal = "0";
			subTtl = "0";
			prodTax = "0";
			ProdNetTotal = "0";

			if (Price.matches("")) {
				Price = "0";
			}

			if (!Price.matches("")) {

				double slPriceCalc = Double.parseDouble(Price);

				if (!itmDscnt.matches("")) {

					tt = (qty * slPriceCalc);

				} else {
					tt = qty * slPriceCalc;
				}

				Prodtotal = twoDecimalPoint(tt);
				Log.d("Prodtotal", Prodtotal);
				double subTotal = 0.0;

				String itemDisc = itmDscnt;
				if (!itemDisc.matches("")) {
					itmDisc = Double.parseDouble(itemDisc);
					subTotal = tt - itmDisc;
				} else {
					subTotal = tt;
				}

				subTtl = twoDecimalPoint(subTotal);
				Log.d("subTtl", subTtl);
				// sl_total.setText("" + sbTtl);

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

							netTotal1 = subTotal + taxAmount;
							ProdNetTotal = twoDecimalPoint(netTotal1);

						} else {
							taxAmount = (tt * taxValueCalc)
									/ (100 + taxValueCalc);
							prodTax = fourDecimalPoint(taxAmount);

							netTotal = tt + taxAmount;
							ProdNetTotal = twoDecimalPoint(netTotal);
						}

					} else if (taxType.matches("Z")) {
						prodTax = "0.0";
						if (!itemDisc.matches("")) {
							netTotal1 = subTotal + taxAmount;
							ProdNetTotal = twoDecimalPoint(netTotal1);

						} else {
							netTotal = tt + taxAmount;
							ProdNetTotal = twoDecimalPoint(netTotal);

						}

					} else {
						prodTax = "0.0";
						ProdNetTotal = Prodtotal;

					}

				} else if (taxValue.matches("")) {
					prodTax = "0.0";
					ProdNetTotal = Prodtotal;
					Log.d("Nettt tot", "" + ProdNetTotal);
				} else {
					prodTax = "0.0";
					ProdNetTotal = Prodtotal;
					Log.d("else Nettt tot", "" + ProdNetTotal);
				}
			}
		} catch (Exception e) {
			// Toast.makeText(SalesAddProduct.this, "Error",
			// Toast.LENGTH_SHORT).show();
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

						netTotal1 = subTotal + taxAmount;
						ProdNetTotal = twoDecimalPoint(netTotal1);

					} else {
						taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
						prodTax = fourDecimalPoint(taxAmount);

						netTotal = tt + taxAmount;
						ProdNetTotal = twoDecimalPoint(netTotal);
					}

				} else if (taxType.matches("Z")) {

					prodTax = "0.0";
					if (!itemDisc.matches("")) {
						netTotal1 = subTotal + taxAmount;
						ProdNetTotal = twoDecimalPoint(netTotal1);

					} else {
						netTotal = tt + taxAmount;
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

	/*@Override
	public void onLocationChanged(Location location) {
		// Getting reference to TextView tv_longitude
		Log.d("Location", "gps " + location.getLatitude());
		try {
			getAddress(location.getLatitude(), location.getLongitude());
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
	}

	*//* Request updates at startup *//*
	@Override
	protected void onResume() {
		super.onResume();
		// locationManager.requestLocationUpdates(provider, 1000, 1, this);
	}

	*//* Remove the locationlistener updates when Activity is paused *//*
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}*/

	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Deleting");
		alertDialog
				.setMessage("Delivery Order products will clear. Do you want to proceed");
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(DeliverySummary.this,
								DeliveryOrderHeader.class);
						startActivity(i);
						DeliverySummary.this.finish();
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

	@Override
	public void onConnected(Bundle bundle) {
		Log.d("onConnected", "onConnected");

		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(100); // Update location every second

		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);

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
		}

		boolean interntConnection = isNetworkConnected();
		if (interntConnection == true) {

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if (isGPSEnabled == true) {
				Log.d("isGPSEnabled", ""+isGPSEnabled);
//				new AppLocationService.getServerDateTime(lati, longi, "1").execute();

				updateUI(setLatitude,setLongitude);
			}else {
				Log.d("gpsLocation", " null ");
//				new AppLocationService.getServerDateTime(0, 0, "0").execute();
				updateUI(0,0);
//					Toast.makeText(getApplicationContext(),
//							"GPS is OFF. Please turn ON",
//							Toast.LENGTH_LONG).show();
			}

		} else {
//			Toast.makeText(getApplicationContext(),
//					"No Internet Connection",
//					Toast.LENGTH_LONG).show();
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
			getAddress(lat, lon);
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
				if (addresses != null && addresses.size() > 0) {

					address1 = addresses.get(0).getAddressLine(0);
					address2 = addresses.get(0).getAddressLine(1);

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

	@Override
	public void onBackPressed() {
		sm_billDisc.setText("");
		sm_billDiscPercentage.setText("");
		Intent i = new Intent(DeliverySummary.this, DeliveryAddProduct.class);
		startActivity(i);
		DeliverySummary.this.finish();
	}
}