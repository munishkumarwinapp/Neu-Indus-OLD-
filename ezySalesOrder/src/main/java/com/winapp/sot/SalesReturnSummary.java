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
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
import com.winapp.adapter.Attribute;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.Product;
import com.winapp.offline.SoapAccessTask;
import com.winapp.printcube.printcube.BluetoothService;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.printer.CommonPreviewPrint;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.PrinterZPL;
import com.winapp.printer.UIHelper;

public class SalesReturnSummary extends SherlockFragmentActivity implements
SlideMenuFragment.MenuClickInterFace, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,ColorAttributeDialog.ProductModifierDialogListener{

	Cursor cursor, billCursor;
	ImageView camera, signature, location, prodphoto, sosign,arrowUpDown;
	EditText sm_total, sm_subTotal,sm_subTotal_inclusive, sm_netTotal, sm_billDisc, sm_tax,sm_total_new,sm_itemDisc,
			sm_location;
	ImageButton save, back;
	double total = 0, smTax = 0;
	Button stupButton, stdownButton;
	LinearLayout sm_header_layout, slsummary_layout, noofprint_layout,sm_bottom_layout,sm_signature_layout;
	String valid_url = "", summaryResult = "";
	double billDiscount = 0, taxAmt = 0;
	String subTot = "", totTax = "", netTot = "", tot = "";
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	private static final int PICK_FROM_CAMERA = 1;
	public static final int SIGNATURE_ACTIVITY = 2;
	double itemDisc = 0, sbTtl = 0, netTotal = 0;
	GPSTracker gps;
	String beforeChange = "";
	double tota = 0;
	private ListView mListView;
	private UIHelper helper;
	CheckBox enableprint;
	int stuprange = 3, stdownrange = 1, stwght = 1;
	TextView stnumber,mEmpty,custname,listing_screen,customer_screen,addProduct_screen,summary_screen;
	private SlidingMenu menu;
	private ProductListAdapter adapter;
	private ArrayList<HashMap<String, String>> SRDetailsArr;
	private ArrayList<HashMap<String, String>> SRHeaderArr;
	private String slNo = "";
	String signature_img, product_img, address1 = "", address2 = "",photo_img="", sign_img="",companyCode="",appPrintGroup="",decimalpts = ".00",
			getSignatureimage = "", getPhotoimage="";
	double setLatitude, setLongitude;
	// get location

	Location mLastLocation;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	String lat, lon;
	private LocationManager locationManager;
	private boolean isGPSEnabled = false,bottomflag = true;

	private ArrayList<ProductDetails> product;
	private ArrayList<ProductDetails> productdet;
	private String jsonString = null, jsonStr = null,calCarton="";
	private JSONObject jsonResponse, jsonResp;
	private JSONArray jsonMainNode, jsonSecNode;
	private ArrayList<String> sortCatSubCat;
	private List<String> sort;
	private HashSet<String> hs;
	private List<String> printsortHeader;
	private HashMap<String, String> hashValue;

	private InvoiceBatchDialog invoiceBatchDialog;
	private boolean refreshview = true;
	private String CustomerCode="";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private String mCurrentgetPath="",mCurrentPhotoPath="";
	private ArrayList<Product> mSOAttributeDetail;
	private ArrayList<Attribute> tempSOAttributeDetails;
	private String haveAttribute="",pCode,pName,slno,slid;
	private double screenInches;
	ColorAttributeDialog productModifierDialog;
	ArrayList<Attribute> colorarrvalues=new ArrayList<>();
	ArrayList<Attribute> sizearrvalues=new ArrayList<>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.salesreturn_summary);

		GlobalData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		GlobalData.mService = new BluetoothService(this, mHandler);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(false);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.summary_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Sales Return Summary");
		back = (ImageButton) customNav.findViewById(R.id.back);
		
		save = (ImageButton) customNav.findViewById(R.id.save);

		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);

		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);

		back.setVisibility(View.INVISIBLE);
		invoiceBatchDialog = new InvoiceBatchDialog();
		//back = (ImageButton) findViewById(R.id.back);

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


		//created on 28/02/17 by saravana
		arrowUpDown = (ImageView) findViewById(R.id.arrow_image);
		sm_bottom_layout = (LinearLayout) findViewById(R.id.sm_bottom_layout);
		sm_signature_layout = (LinearLayout) findViewById(R.id.sm_signature_layout);

		sm_header_layout = (LinearLayout) findViewById(R.id.sm_header_layout);
		slsummary_layout = (LinearLayout) findViewById(R.id.slsummary_layout);
/////////////////////////

		custname = (TextView) findViewById(R.id.cust_name);
		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen = (TextView) findViewById(R.id.sum_screen);
		summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
		summary_screen.setBackgroundResource(drawable.tab_select_right);
/////////////
		//save = (ImageButton) findViewById(R.id.save);
		mListView = (ListView) findViewById(android.R.id.list);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		helper = new UIHelper(SalesReturnSummary.this);
		FWMSSettingsDatabase.init(SalesReturnSummary.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SOTSummaryWebService(valid_url);
		SOTDatabase.init(SalesReturnSummary.this);
		cursor = SOTDatabase.getCursor();

		hashValue = new HashMap<String, String>();
		sortCatSubCat = new ArrayList<String>();
		sort = new ArrayList<String>();
		hs = new HashSet<String>();
		printsortHeader = new ArrayList<String>();
		product = new ArrayList<ProductDetails>();
		productdet = new ArrayList<ProductDetails>();
		mSOAttributeDetail = new ArrayList<>();
		tempSOAttributeDetails = new ArrayList<>();
		screenInches = displayMetrics();
		haveAttribute = SalesOrderSetGet.getHaveAttribute();

		if(haveAttribute!=null && !haveAttribute.isEmpty()){

		}else{
			haveAttribute="";
		}
		productModifierDialog = new ColorAttributeDialog();

		SRDetailsArr = new ArrayList<HashMap<String, String>>();
		SRHeaderArr = new ArrayList<HashMap<String, String>>();

		calCarton = LogOutSetGet.getCalcCarton();

		sm_billDisc.setRawInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);

		//created on 28/02/17 by saravana
		arrowUpDown.setImageResource(drawable.arrow_up);
		sm_bottom_layout.setVisibility(View.GONE);
		sm_signature_layout.setVisibility(View.VISIBLE);


		appPrintGroup = SalesOrderSetGet.getAppPrintGroup();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			refreshview = false;
			mSOAttributeDetail.clear();
			SOTDatabase.deleteAllProduct();
			SOTDatabase.deleteAttribute();
			SOTDatabase.deleteBillDisc();

			SRHeaderArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("SRHeader");
			SRDetailsArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("SRDetails");
			Log.d("SR Header", "" + SRHeaderArr);
			Log.d("SR Detail", "" + SRDetailsArr);
			int sl_no;

			if (extras.containsKey("SOAttributeDetail")) {
				mSOAttributeDetail = getIntent().getParcelableArrayListExtra("SOAttributeDetail");
			}
// Edit SR Header
			if (SRHeaderArr != null) {
				for (int i = 0; i < SRHeaderArr.size(); i++) {

					String SRNo = SRHeaderArr.get(i).get("SalesReturnNo");

					ConvertToSetterGetter.setEdit_salesreturn_no(SRNo);

					String SRDate = SRHeaderArr.get(i).get("SalesReturnDate")
							.split("\\ ")[0];

					/*
					 * String DeliveryDate = doHeaderArr.get(i) .get("DoDate");
					 */

					String LocationCode = SRHeaderArr.get(i)
							.get("LocationCode");
					CustomerCode = SRHeaderArr.get(i)
							.get("CustomerCode");
					String Total = SRHeaderArr.get(i).get("Total");
					String ItemDiscount = SRHeaderArr.get(i)
							.get("ItemDiscount");
					String BillDIscount = SRHeaderArr.get(i)
							.get("BillDIscount");

					String Remarks = SRHeaderArr.get(i).get("Remarks");
					String InvoiceNo = SRHeaderArr.get(i).get("InvoiceNo");

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
					SalesOrderSetGet.setSaleorderdate(SRDate);
					SalesOrderSetGet.setLocationcode(LocationCode);
					SalesOrderSetGet.setCustomercode(CustomerCode);
					SalesOrderSetGet.setRemarks(Remarks);
					SalesOrderSetGet.setCurrencyname("");
					SalesOrderSetGet.setSrinvoiceno(InvoiceNo);
				}
			}
			// Edit SR Detail
			if (SRDetailsArr != null) {
				for (int i = 0; i < SRDetailsArr.size(); i++) {

					slNo = SRDetailsArr.get(i).get("slNo");
					String ProductCode = SRDetailsArr.get(i).get("ProductCode");
					String ProductName = SRDetailsArr.get(i).get("ProductName");
					String CQty = SRDetailsArr.get(i).get("CQty");
					String LQty = SRDetailsArr.get(i).get("LQty");
					String Qty = SRDetailsArr.get(i).get("Qty");
					String FOCQty = SRDetailsArr.get(i).get("FOCQty");
					String PcsPerCarton = SRDetailsArr.get(i).get(
							"PcsPerCarton");
					String RetailPrice = SRDetailsArr.get(i).get("RetailPrice");
					String Price = SRDetailsArr.get(i).get("Price");
					String Total = SRDetailsArr.get(i).get("Total");
					String ItemDiscount = SRDetailsArr.get(i).get(
							"ItemDiscount");
					String SubTotal = SRDetailsArr.get(i).get("SubTotal");
					String Tax = SRDetailsArr.get(i).get("Tax");
					String NetTotal = SRDetailsArr.get(i).get("NetTotal");
					String TaxType = SRDetailsArr.get(i).get("TaxType");
					String TaxPerc = SRDetailsArr.get(i).get("TaxPerc");

					SalesOrderSetGet.setCompanytax(TaxType);
//					SalesOrderSetGet.setTaxValue(TaxPerc);

//					String ExchangeQty = SRDetailsArr.get(i).get("ExchangeQty");
					String CartonPrice = SRDetailsArr.get(i).get("CartonPrice");
					String StockAdjRefCode = SRDetailsArr.get(i).get("StockAdjRefCode");

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
							StockAdjRefCode, "", "",slNo,
							String.valueOf(qty),"","","");


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
							new SoapAccessTask(SalesReturnSummary.this, valid_url,
									"fncGetProduct", params, new GetProduct(
									SRDetailsArr.get(i).get("ProductCode"),slNo)).execute();
						}
					}
				}
			}

			if (mSOAttributeDetail != null) {

				for(Product product : mSOAttributeDetail){
					slno = product.getSlNo();
					pCode = product.getProductCode();
					pName = product.getProductName();
					String colorCode = product.getColorCode();
					String sizeCode = product.getSizeCode();
					String colorName = product.getColorName();
					String sizeName = product.getSizeName();
					String qty = product.getQty();
					//Log.d("mSOAttributeDetail", "" + mSOAttributeDetail.size());
					Log.d("storemethod","storemethod");
					Log.d("serialnopass",slno);
					Attribute obj = new Attribute();
					obj.setNo(slno);
					obj.setProductcodeno(pCode);
					obj.setCode(colorCode);
					obj.setName(colorName);
					obj.setSizecode(sizeCode);
					obj.setSizename(sizeName);
					obj.setSizeQty(qty);
					double quantity = qty.equals("") ? 0 : Double.valueOf(qty);
					if(quantity>0){
						obj.setSelected(true);
					}else{
						obj.setSelected(false);
					}
					tempSOAttributeDetails.add(obj);
				}

				ArrayList<Product> mDistinctColorArr = new ArrayList<>();
//    mDistinctColorArr.add(mSOAttributeDetail.get(0));
				for (Product prod : mSOAttributeDetail) {
					boolean flag = false;
					for (Product colorUnique : mDistinctColorArr) {
						if (colorUnique.getProductCode().equals(prod.getProductCode())) {
							flag = true;
						}
					}
					if (!flag){
						if(!prod.getSlNo().equals("0")){
							mDistinctColorArr.add(prod);
						}
					}}

				for(Product produc : mDistinctColorArr){
					for(Product attr : mSOAttributeDetail){
						if(produc.getProductCode().equals(attr.getProductCode())){
							//attr.setNo(produc.getSlNo());
							Log.d("produc.getSlNo()",produc.getSlNo());

							String sl_No = produc.getSlNo();
							String pCode = attr.getProductCode();
							String pName = attr.getProductName();
							String colorCode = attr.getColorCode();
							String sizeCode = attr.getSizeCode();
							String colorName = attr.getColorName();
							String sizeName = attr.getSizeName();
							String qty = attr.getQty();

							SOTDatabase.storeAttribute(sl_No,pCode,colorCode,sizeCode,colorName,sizeName,qty);
						}
					}
				}

			}

		}

		photo_img = SOTDatabase.getProductImage();
		sign_img = SOTDatabase.getSignatureImage();

		if (photo_img != null && !photo_img.isEmpty()) {
			Log.d("invoice sum photo if ", photo_img);
			try {

				mCurrentgetPath = Product.getPath();

//    }
				byte[] encodePhotoByte = Base64.decode(photo_img, Base64.DEFAULT);
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
		}else {

		}

		if (sign_img != null && !sign_img.isEmpty()) {
			byte[] encodeByte = Base64.decode(sign_img, Base64.DEFAULT);
			Bitmap sign = BitmapFactory.decodeByteArray(encodeByte, 0,
					encodeByte.length);
			sign = Bitmap.createScaledBitmap(sign, 300, 80, true);

			sosign.setImageBitmap(sign);
		} else {

		}

		if (cursor != null && cursor.getCount() > 0) {

			sm_header_layout.setVisibility(View.VISIBLE);
			sm_billDisc.setEnabled(true);

			tota = SOTDatabase.getTotal();

			String smtotal= twoDecimalPoint(tota);
			double  tot_item_disc = SOTDatabase.getTotalItemDisc();
			String tot_itemDisc = twoDecimalPoint(tot_item_disc);
			sm_itemDisc.setText(tot_itemDisc);
			sm_total_new.setText("" + smtotal);

			smTax = SOTDatabase.getTax();
			String ProdTax = fourDecimalPoint(smTax);
			sm_tax.setText("" + ProdTax);

			sbTtl = SOTDatabase.getSubTotal();
			String sub = twoDecimalPoint(sbTtl);
			sm_subTotal.setText("" + sub);

			sm_total.setText("" + sub);

			////
			String taxType = SalesOrderSetGet.getCompanytax();
			if(taxType!=null && !taxType.isEmpty()){
				if (taxType.matches("I") || taxType.matches("Z")) {
					netTotal = sbTtl;
				}else{
					netTotal = sbTtl + smTax;
				}
			}else{
				netTotal = sbTtl + smTax;
			}
			////
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
			save.setVisibility(View.VISIBLE);
		} else {
			save.setVisibility(View.INVISIBLE);
			mEmpty.setVisibility(View.VISIBLE);
			sm_header_layout.setVisibility(View.GONE);
			sm_billDisc.setFocusable(false);
		}

		//setListAdapter(new ProductListAdapter(this, cursor));
		adapter = new ProductListAdapter(this, cursor);
		mListView.setAdapter(adapter);
		registerForContextMenu(mListView);

		taxCalc(); // recalculate tax value
		smTax = SOTDatabase.getTax();
		String ProdTax = fourDecimalPoint(smTax);
		sm_tax.setText("" + ProdTax);

		// Getting LocationManager object
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		buildGoogleApiClient();

		if (!mGoogleApiClient.isConnected())
			mGoogleApiClient.connect();

		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				saveAlertDialog();

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
						
						////
						String taxType = SalesOrderSetGet.getCompanytax();
						if(taxType!=null && !taxType.isEmpty()){
							if (taxType.matches("I") || taxType.matches("Z")) {
								netTotal = sbTtl;
							}else{
								netTotal = sbTtl + smTax;
							}
						}else{
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
							////
							String taxType = SalesOrderSetGet.getCompanytax();
							if(taxType!=null && !taxType.isEmpty()){
								if (taxType.matches("I") || taxType.matches("Z")) {
									net = sbt;
								}else{
									net = taxAt + sbt;
								}
							}else{
								net = taxAt + sbt;
							}
							////
							
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

		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CameraAction(PICK_FROM_CAMERA);
			}
		});

		signature.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent i = new Intent(SalesReturnSummary.this,
						CaptureSignature.class);
				startActivityForResult(i, SIGNATURE_ACTIVITY);

			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				sm_billDisc.setText("");
				Intent i = new Intent(SalesReturnSummary.this,
						SalesReturnAddProduct.class);
				startActivity(i);
				SalesReturnSummary.this.finish();
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
					Intent i = new Intent(SalesReturnSummary.this,
							SalesReturnHeader.class);
					startActivity(i);
					SalesReturnSummary.this.finish();

				}

			}
		});

		customer_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(SalesReturnSummary.this,
						SalesReturnCustomer.class);
				startActivity(i);
				SalesReturnSummary.this.finish();

			}
		});

		addProduct_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(SalesReturnSummary.this, SalesReturnAddProduct.class);
				startActivity(i);
				SalesReturnSummary.this.finish();

			}
		});

		String custCode = SalesOrderSetGet.getCustomercode();
		String custName = SalesOrderSetGet.getCustomername();
		custname.setText(custName + " (" + custCode + ")");
	}

	public Double displayMetrics() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int dens = dm.densityDpi;
		double wi = (double) width / (double) dens;
		double hi = (double) height / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		return screenInches = Math.sqrt(x + y);
	}

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
						// subTot = sbTotal - billDiscount;
						double billDisc = sbTotal * billDiscount;

						subTot = sbTotal - billDisc;
					}

					String taxType = SalesOrderSetGet.getCompanytax();
					if(taxType!=null && !taxType.isEmpty()){
						if (taxType.matches("I") || taxType.matches("Z")) {
							if (!taxValue.matches("")) {
								txVl = Double.parseDouble(taxValue);
								
								if (!total.matches("")) {
									double tt = Double.parseDouble(total);
									tx = (tt * txVl) / (100 + txVl);
								}
													
							}
						}else{
							if (!taxValue.matches("")) {
								txVl = Double.parseDouble(taxValue);
								tx = (subTot * txVl) / 100;
							}
						}
					}else{
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

							if(taxType!=null && !taxType.isEmpty()){
								if (taxType.matches("I") || taxType.matches("Z")) {
									net_tot = subTot;
								}else{
									net_tot = subTot + tax;
								}
							}else{
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
		setValue();
		adapter.notifyDataSetChanged();
	}

	public void setValue(){

		if (cursor != null && cursor.getCount() > 0) {

			sm_header_layout.setVisibility(View.VISIBLE);
			save.setVisibility(View.VISIBLE);
			sm_billDisc.setEnabled(true);
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
			sm_total.setText("" + sub);
			// //
			String taxType = SalesOrderSetGet.getCompanytax();
			Log.d("taxType", "-->"+taxType);
			if (taxType != null && !taxType.isEmpty()) {
				if (taxType.matches("I") || taxType.matches("Z")) {
					netTotal = sbTtl;
				} else {
					netTotal = sbTtl + smTax;
				}
			} else {
				netTotal = sbTtl + smTax;
			}
			// //
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
		} else {
			save.setVisibility(View.INVISIBLE);
			mEmpty.setVisibility(View.VISIBLE);
			sm_header_layout.setVisibility(View.GONE);
			sm_billDisc.setFocusable(false);
			sm_total.setText("");
			sm_billDisc.setText("");
			sm_tax.setText("");
			sm_subTotal.setText("");
			sm_netTotal.setText("");
			sm_itemDisc.setText("");
			sm_total_new.setText("");
			sm_subTotal_inclusive.setText("");
		}
	}


	private class GetProduct implements SoapAccessTask.CallbackInterface {

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

						Log.d("tcmpnyCode", cmpnyCode);
						Log.d("tlocCode", locCode);

						if(SRDetailsArr!=null){

							Log.d("edit salesreturn batch", ""+slNo);

							ArrayList<PropertyInfo> params1 = new ArrayList<PropertyInfo>();
							params1.add(newPropertyInfo("SalesReturnNo", ConvertToSetterGetter.getEdit_salesreturn_no()));
							params1.add(newPropertyInfo("CompanyCode", cmpnyCode));
							params1.add(newPropertyInfo("slNo", slNo));
							params1.add(newPropertyInfo("ProductCode", prod_code));

							new SoapAccessTask(SalesReturnSummary.this, valid_url,
									"fncGetSalesReturnBatchDetail", params1,
									new GetBatchStockFromEditSalesReturn(haveBatch, haveExpiry,
											prodName, prod_code, SR_slNo,PcsPerCarton)).execute();
						}else{

							ArrayList<PropertyInfo> params = new ArrayList<PropertyInfo>();
							params.add(newPropertyInfo("CompanyCode", cmpnyCode));
//							params.add(newPropertyInfo("LocationCode", locCode));
							params.add(newPropertyInfo("ProductCode", prod_code));
							params.add(newPropertyInfo("CustomerCode", CustomerCode));

							new SoapAccessTask(SalesReturnSummary.this, valid_url,
									"fncGetInvoiceBatchDetailByCustomer", params,
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


	private class GetBatchStock implements SoapAccessTask.CallbackInterface {

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

	private class GetBatchStockFromEditSalesReturn implements SoapAccessTask.CallbackInterface {

		String havebatch = "", haveexpiry = "", prodName = "", prodCode = "", SR_slNo="",PcsPerCarton="";

		public GetBatchStockFromEditSalesReturn(String haveBatch, String haveExpiry,
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

					calCarton = LogOutSetGet.getCalcCarton();

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

	private class ProductListAdapter extends ResourceCursorAdapter {

		@SuppressWarnings("deprecation")
		public ProductListAdapter(Context context, Cursor cursor) {
			super(context, R.layout.sales_summary_list, cursor);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

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

			TextView ss_cprice = (TextView) view.findViewById(R.id.ss_cprice);
			ss_cprice.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE)));

			TextView ss_subTotal = (TextView) view
					.findViewById(R.id.ss_subTotal);
			ss_subTotal.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL)));

			TextView ss_stockadj_type = (TextView) view
					.findViewById(R.id.ss_exch_qty);
			ss_stockadj_type.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY)));

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
		String bllDsc="",SRNo="";
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(SalesReturnSummary.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(slsummary_layout, false);
			progressBar = new ProgressBar(SalesReturnSummary.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
			bllDsc = sm_billDisc.getText().toString();

			SRNo =ConvertToSetterGetter.getEdit_salesreturn_no();

			if(SRNo!=null || !SRNo.isEmpty()){

			}else{
				SRNo="";
			}

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			double discnt = 0.0;

			if (!bllDsc.matches("")) {
				discnt = Double.parseDouble(bllDsc);
			}

			try {
				summaryResult = SOTSummaryWebService.summarySRService(SRNo,
						"fncSaveSalesReturn", discnt, subTot, totTax, netTot,
						Double.toString(tota));
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (summaryResult.matches("failed")) {

				Toast.makeText(SalesReturnSummary.this, "Failed",
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

				Log.d("signature_img", ":"+signature_img);
				Log.d("product_img", ":"+product_img);

				// if (onlineMode.matches("True")) {
				// if (checkOffline == true) {
				// } else { // online
				String imgResult = SOTSummaryWebService.saveSignatureImage(
						summaryResult, "" + setLatitude, "" + setLongitude,
						signature_img, product_img, "fncSaveInvoiceImages",
						"SR", address1, address2);

				Toast.makeText(SalesReturnSummary.this, "Saved Successfully",
						Toast.LENGTH_SHORT).show();

				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(slsummary_layout, true);
				if (enableprint.isChecked()) {

					if (FWMSSettingsDatabase
							.getPrinterAddress()
							.matches(
									"[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$")) {

						helper.showProgressDialog(R.string.generating_sr);
						try {
						//	print();
							new AsyncCatSub().execute();
						} catch (Exception e) {
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

	@SuppressWarnings("deprecation")
	public void clearView() {
		mEmpty.setVisibility(View.VISIBLE);
		save.setVisibility(View.INVISIBLE);
		SOTDatabase.deleteAllProduct();
		SOTDatabase.deleteAttribute();
		mListView.setAdapter(null);
		cursor.requery();

		sm_total.setText("");
		sm_billDisc.setText("");
		sm_tax.setText("");
		sm_billDisc.setFocusable(false);
		sm_header_layout.setVisibility(View.GONE);
		sm_subTotal.setText("");
		sm_netTotal.setText("");
		sm_subTotal_inclusive.setText("");
		SOTDatabase.deleteBillDisc();

		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");
		SalesOrderSetGet.setCustomerTaxPerc("");
		ConvertToSetterGetter.setEdit_salesreturn_no("");
		Intent i = new Intent(SalesReturnSummary.this, SalesReturnHeader.class);
		startActivity(i);
		SalesReturnSummary.this.finish();

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
//						//	photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
//
//						ByteArrayOutputStream stream = new ByteArrayOutputStream();
//						photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
//						prodphoto.setImageBitmap(photo);
//						byte[] bitMapData = stream.toByteArray();
//						String camera_image = Base64.encodeToString(bitMapData,
//								Base64.DEFAULT);
//						SOTDatabase.init(SalesReturnSummary.this);
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
					//Bundle extras = data.getExtras();
					 byte[] bytes = data.getByteArrayExtra("status");
					if (bytes != null) {
						//Bitmap photo = extras.getParcelable("status");
								
						Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
						bitmap = Bitmap.createScaledBitmap(bitmap, 240, 80, true);							
						
						sosign.setImageBitmap(bitmap);
						
						 ByteArrayOutputStream stream = new ByteArrayOutputStream();
							bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] bitMapData = stream.toByteArray();
						String signature_image = Base64.encodeToString(
								bitMapData, Base64.DEFAULT);
						SOTDatabase.init(SalesReturnSummary.this);
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
			SOTDatabase.init(SalesReturnSummary.this);

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

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		View TargetV = info.targetView;
		TextView tvProdCode = (TextView) TargetV.findViewById(R.id.ss_prodcode);
		String strProdCode = tvProdCode.getText().toString();
		TextView ss_sl_id = (TextView) TargetV.findViewById(R.id.ss_slid);
		slid = ss_sl_id.getText().toString();

		String sno = ((TextView) TargetV.findViewById(R.id.ss_slno))
				.getText().toString();

		Log.d("code", strProdCode);
		String havebatch = SOTDatabase.getprodcodefrombatch(strProdCode);
		if (!havebatch.matches("")) {
			menu.add(0, v.getId(), 0, "Edit Batch");
		}
		colorarrvalues = SOTDatabase.getAttributeColorValues(strProdCode, sno);

		Log.d("strProdCode","spd "+strProdCode);
		Log.d("sno","ssn "+sno);
		Log.d("colorarrvalues","colo  "+colorarrvalues.size());

		if(haveAttribute.matches("2")){

			if(colorarrvalues.size()>0){
				menu.add(0, v.getId(), 0, "Edit Color");
			}
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
					hm.put("sm_subTotal_inclusive", sm_subTotal_inclusive);
					hm.put("sm_tax", sm_tax);
					hm.put("sm_netTotal", sm_netTotal);

					invoiceBatchDialog.initiateBatchPopupWindow(
							SalesReturnSummary.this, id, sno, haveBatch,
							haveExpiry, code, name, slCartonPerQty, cursor,
							price, hm);


				} else {
					Log.d("no haveBatch", "no haveExpiry");

				}

			} else {
				Log.d("no haveBatchOnStockIn", "no haveBatchOnStockIn");

			}

		} else if (item.getTitle() == "Edit") {
			Intent i = new Intent(SalesReturnSummary.this,
					SalesReturnAddProduct.class);
			i.putExtra("SOT_ssno", ((TextView) info.targetView
					.findViewById(R.id.ss_slid)).getText().toString());
			i.putExtra("SOT_slno", ((TextView) info.targetView
					.findViewById(R.id.ss_slno)).getText().toString());
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
			i.putExtra("SOT_str_sscprice", ((TextView) info.targetView
					.findViewById(R.id.ss_cprice)).getText().toString());
			i.putExtra("SOT_str_ssdisc", ((TextView) info.targetView
					.findViewById(R.id.ss_item_disc)).getText().toString());

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
			i.putExtra("SOT_stockadjustment_type", ((TextView) info.targetView
					.findViewById(R.id.ss_exch_qty)).getText().toString());
			i.putExtra("SOT_ssupdate", "Update");
			i.putExtra("SOT_sscancel", "Cancel");
			startActivity(i);
			SalesReturnSummary.this.finish();
		} else if (item.getTitle() == "Delete") {
			String id = ((TextView) info.targetView.findViewById(R.id.ss_slid))
					.getText().toString();
			String pcode = ((TextView) info.targetView
					.findViewById(R.id.ss_prodcode)).getText().toString();
			String slno = ((TextView) info.targetView.findViewById(R.id.ss_slno))
					.getText().toString();
			Log.d("id", id);
			SOTDatabase.deleteProduct(id);
			SOTDatabase.deleteAttributeProducts(slno,pcode);
			SOTDatabase.deleteBillDiscount(id);
			sm_billDisc.setText("");
			cursor.requery();
			ArrayList<String> snoCount = new ArrayList<String>();
			snoCount = SOTDatabase.snoCountID();
			Log.d("snocount", "" + snoCount);
			ArrayList<String> snoCountpcode = new ArrayList<String>();
			snoCountpcode = SOTDatabase.snoCountPCode();
			Log.d("snocountpcode SO", "" + snoCountpcode);
			for (int i = 0; i < snoCount.size(); i++) {
				int sno = 1 + i;
				HashMap<String, String> queryValues = new HashMap<String, String>();
				queryValues.put("_id", "" + snoCount.get(i));
				queryValues.put("snum", "" + sno);
				SOTDatabase.updateSNO(queryValues);
				SOTDatabase.updateBillSNO(queryValues);
			}
			for (int i = 0; i < snoCountpcode.size(); i++) {
				int sno = 1 + i;
				HashMap<String, String> queryValues = new HashMap<String, String>();
				queryValues.put("product_code", "" + snoCountpcode.get(i));
				queryValues.put("snum", "" + sno);
				Log.d("product_code", snoCountpcode.get(i));
				Log.d("snum1", String.valueOf(sno));
				SOTDatabase.updateATTRSLNO(queryValues);
				// SOTDatabase.getAttributeColorValues(snoCountpcode.get(i), String.valueOf(sno));
			}
			if (cursor != null && cursor.getCount() > 0) {
				cursor.requery();
				tota = SOTDatabase.getTotal();
				smTax = SOTDatabase.getTax();
				String ProdTax = fourDecimalPoint(smTax);
				sm_tax.setText("" + ProdTax);

				String smtotal= twoDecimalPoint(tota);
				double  tot_item_disc = SOTDatabase.getTotalItemDisc();
				String tot_itemDisc = twoDecimalPoint(tot_item_disc);
				sm_itemDisc.setText(tot_itemDisc);
				sm_total_new.setText("" + smtotal);

				sbTtl = SOTDatabase.getSubTotal();
				String sub = twoDecimalPoint(sbTtl);
				sm_subTotal.setText("" + sub);
				sm_total.setText("" + sub);

				String taxType = SalesOrderSetGet.getCompanytax();
				if(taxType!=null && !taxType.isEmpty()){
					if (taxType.matches("I") || taxType.matches("Z")) {
						netTotal = sbTtl;
					}else{
						netTotal = sbTtl + smTax;
					}
				}else{
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
				Toast.makeText(SalesReturnSummary.this, "Deleted",
						Toast.LENGTH_LONG).show();
				mEmpty.setVisibility(View.GONE);
			} else {
				save.setVisibility(View.INVISIBLE);
				mEmpty.setVisibility(View.VISIBLE);
				sm_header_layout.setVisibility(View.GONE);
				sm_billDisc.setFocusable(false);
				sm_total.setText("");
				sm_billDisc.setText("");
				sm_tax.setText("");
				sm_subTotal.setText("");
				sm_netTotal.setText("");
				sm_itemDisc.setText("");
				sm_total_new.setText("");
				sm_subTotal_inclusive.setText("");
			}

		} else if (item.getTitle() == "Edit Color") {
			try {
				TextView tvProdCode = (TextView) info.targetView.findViewById(R.id.ss_prodcode);
				String strProdCode = tvProdCode.getText().toString();
				String sno = ((TextView) info.targetView.findViewById(R.id.ss_slno))
						.getText().toString();
				String name = ((TextView) info.targetView.findViewById(R.id.ss_name)).getText().toString();

				ArrayList<Attribute> mDistinctColorArr = new ArrayList<>();
				mDistinctColorArr.add(colorarrvalues.get(0));
				for (Attribute colour : colorarrvalues) {
					boolean flag = false;
					for (Attribute colorUnique : mDistinctColorArr) {
						if (colorUnique.getCode().equals(colour.getCode())) {
							flag = true;
						}
					}
					if (!flag)
						mDistinctColorArr.add(colour);

				}

				sizearrvalues = SOTDatabase.getAttributeSizeValues(strProdCode, sno);
				Log.d("sizearrvalues", "" + sizearrvalues.size());
				Log.d("mDistinctColorArr", "" + mDistinctColorArr.size());
				Log.d("colorarrvalues", "" + colorarrvalues.size());

				productModifierDialog.setAttributeArr(
						SalesReturnSummary.this, sno, name, strProdCode, mDistinctColorArr, sizearrvalues,slid,screenInches);
				productModifierDialog.show(getFragmentManager(), "dialog");
			}catch (Exception e){
				e.printStackTrace();
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

						int count = cursor.getCount();
						int sl_no = 1;
						if (cursor != null && cursor.getCount() > 0) {


							// added batch

							//////////////////////////////////////////////////////////////////

							if (cursor.moveToFirst()) {
								do {

									String pCode = cursor.getString(cursor
											.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
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
									if(batchHave.size()>0) {
										haveBatch = batchHave.get(0);
										haveExpiry = batchHave.get(1);
//												String slCartonPerQty = batchHave.get(2);
									}
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
																SalesReturnSummary.this,
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
															SalesReturnSummary.this,
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
													SalesReturnSummary.this,
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
									Log.d("sl_no", "->" + sl_no);
									if (count == sl_no) {

										AsyncCallWSSummary task = new AsyncCallWSSummary();
										task.execute();

									}

									sl_no++;

								} while (cursor.moveToNext());
							}

							// End
							///////////////////////////////////////////////////////////////////////////

						} else {
							Toast.makeText(SalesReturnSummary.this,
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
	private class AsyncCatSub extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			sortCatSubCat.clear();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if (appPrintGroup.matches("C")) {
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

						sortCatSubCat.add(categorycode);
					} catch (JSONException e) {

						e.printStackTrace();
					}
				}

			} else if (appPrintGroup.matches("S")) {
				jsonStr = WebServiceClass.URLService("fncGetSubCategory");
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
							sortCatSubCat.add(subcategorycode);
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
			new AsyncPrintCall().execute();
		}

	}
	public void sortCatSubCat() {
		for (int i = 0; i < sortCatSubCat.size(); i++) {
			String catSub = sortCatSubCat.get(i).toString();
			for (ProductDetails products : product) {

				if (catSub.matches(products.getSortproduct())) {

					sort.add(catSub);
				}
			}
		}
		hs.addAll(sort);
		printsortHeader.clear();
		printsortHeader.addAll(hs);
	}
	private class AsyncPrintCall extends
			AsyncTask<Void, Void, ArrayList<String>> {
		@Override
		protected void onPreExecute() {
			product.clear();
			productdet.clear();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			String showcartonloose = SalesOrderSetGet.getCartonpriceflag();
			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("SRNumber", summaryResult);

			if (showcartonloose.equalsIgnoreCase("1")) {
				jsonString = SalesOrderWebService.getSODetail(
						hashValue, "fncGetSalesReturnDetailWithCarton");
			} else {
				jsonString = SalesOrderWebService.getSODetail(
						hashValue, "fncGetSalesReturnDetail");
			}

//            jsonString = SalesOrderWebService.getSODetail(hashValue,
//                    "fncGetSalesReturnDetail");
			jsonStr = SalesOrderWebService.getSODetail(hashValue,
					"fncGetSalesReturnHeaderByInvoiceNo");

			Log.d("jsonString ", jsonString);
			Log.d("jsonStr ", jsonStr);

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");

				jsonResp = new JSONObject(jsonStr);
				jsonSecNode = jsonResp.optJSONArray("SODetails");

			} catch (JSONException e) {

				e.printStackTrace();
			}

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
						int s = i + 1;
						productdetail.setSno(String.valueOf(s));
						String slNo = jsonChildNode.optString("slNo")
								.toString();
						String productCode = jsonChildNode.optString(
								"ProductCode").toString();
						productdetail.setItemcode(productCode);

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

						Log.d("uomCode", "u " + uomCode);

						if (transType.equalsIgnoreCase("Ctn")) {
							String cQty = jsonChildNode.optString("CQty")
									.toString();

							String cPrice = jsonChildNode.optString(
									"CartonPrice").toString();

							Log.d("cPrice", "-->" + cPrice);

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

							Log.d("lPrice", "--->" + lPrice);

							if (lQty != null && !lQty.isEmpty()
									&& lPrice != null && !lPrice.isEmpty()) {
								productdetail.setQty(lQty.split("\\.")[0] + " "
										+ uomCode);
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
							if (focQty != null && !focQty.isEmpty()) {
								productdetail.setQty(focQty.split("\\.")[0]);
							} else {
								productdetail.setQty("0" + " " + uomCode);
							}
							productdetail.setPrice("0.00");
							productdetail.setTotal("0.00");

						} else if (transType.equalsIgnoreCase("Exc")) {
							String excQty = jsonChildNode.optString(
									"ExchangeQty").toString();
							if (excQty != null && !excQty.isEmpty()) {
								productdetail.setQty(excQty.split("\\.")[0]);
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
						if (appPrintGroup.matches("C")) {
							productdetail.setSortproduct(jsonChildNode
									.optString("CategoryCode").toString());
						} else if (appPrintGroup.matches("S")) {
							productdetail.setSortproduct(jsonChildNode
									.optString("SubCategoryCode").toString());
						} else if (appPrintGroup.matches("N")) {
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

			}else{
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

						productdetail.setFocqty(jsonChildNode
								.optDouble("FOCQty"));

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
						if (appPrintGroup.matches("C")) {
							productdetail.setSortproduct(jsonChildNode
									.optString("CategoryCode").toString());
						} else if (appPrintGroup.matches("S")) {
							productdetail.setSortproduct(jsonChildNode
									.optString("SubCategoryCode").toString());
						} else if (appPrintGroup.matches("N")) {
							productdetail.setSortproduct("");
						} else {
							productdetail.setSortproduct("");
						}
						productdetail.setTax(jsonChildNode.optString("Tax").toString());
						productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
						productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
						productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
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
					productdet.add(productdetail);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			hashValue.clear();
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {

				try {
					sortCatSubCat();
					print();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}

	}
	@SuppressWarnings("deprecation")
	private void print() throws IOException {
		final int nofcopies = Integer.parseInt(stnumber.getText().toString());

		final String customerCode = SalesOrderSetGet.getCustomercode();
		final String customerName = SalesOrderSetGet.getCustomername();
		final String soDate = SalesOrderSetGet.getSaleorderdate();
		/*ArrayList<ProductDetails> product = new ArrayList<ProductDetails>();
		final ArrayList<ProductDetails> productdet = new ArrayList<ProductDetails>();

		ProductDetails productdetail = new ProductDetails();
		double itemDisc = SOTDatabase.getitemDisc();
		double discnt = 0.0;
		String bllDsc = sm_billDisc.getText().toString();
		if (!bllDsc.matches("")) {
			discnt = Double.parseDouble(bllDsc);
		}*/

		String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
			if(printertype.matches("Zebra iMZ320")){

		helper.dismissProgressDialog();
		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		/*product = SOTDatabase.products();
		productdetail.setItemdisc(Double.toString(itemDisc));
		productdetail.setBilldisc(Double.toString(discnt));
		productdetail.setSubtotal(subTot);
		productdetail.setTax(totTax);
		productdetail.setNettot(netTot);
		productdet.add(productdetail);
*/
		try {
			Printer printer = new Printer(SalesReturnSummary.this, macaddress);
			printer.setOnCompletionListener(new Printer.OnCompletionListener() {

				@Override
				public void onCompleted() {
					finish();
					Intent i = new Intent(SalesReturnSummary.this,
							SalesReturnHeader.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					SalesReturnSummary.this.finish();
				}
			});
			printer.printSalesReturn(summaryResult, soDate, customerCode,
					customerName, product, productdet,printsortHeader,appPrintGroup, nofcopies);
		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}
		save.setVisibility(View.INVISIBLE);
		SOTDatabase.deleteAllProduct();
		mListView.setAdapter(null);
		cursor.requery();
		mEmpty.setVisibility(View.GONE);
		sm_total.setText("");
		sm_billDisc.setText("");
		sm_tax.setText("");
		sm_billDisc.setFocusable(false);
		sm_header_layout.setVisibility(View.GONE);
		sm_subTotal.setText("");
		sm_netTotal.setText("");
		sm_subTotal_inclusive.setText("");
		SOTDatabase.deleteBillDisc();

		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");
		SalesOrderSetGet.setCustomerTaxPerc("");
			}else if(printertype.matches("4 Inch Bluetooth")){

				String macAddress = FWMSSettingsDatabase.getPrinterAddress();
				helper.showProgressDialog(SalesReturnSummary.this.getString(R.string.print),
						SalesReturnSummary.this.getString(R.string.creating_file_for_printing));
				if (BluetoothAdapter.checkBluetoothAddress(macAddress))
				{
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macAddress);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device,true);
				}
				helper.dismissProgressDialog();
			}
			else if(printertype.matches("3 Inch Bluetooth Generic")) {
				helper.dismissProgressDialog();
				String macaddress = FWMSSettingsDatabase.getPrinterAddress();
				/*product = SOTDatabase.products();
				productdetail.setItemdisc(Double.toString(itemDisc));
				productdetail.setBilldisc(Double.toString(discnt));
				productdetail.setSubtotal(subTot);
				productdetail.setTax(totTax);
				productdetail.setNettot(netTot);
				productdet.add(productdetail);*/

				try {
					final CubePrint print = new CubePrint(SalesReturnSummary.this, macaddress);
					print.initGenericPrinter();
					final ArrayList<ProductDetails> finalProduct = product;
					print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
						@Override
						public void initCompleted() {
							print.printSalesReturn(summaryResult, soDate, customerCode,
									customerName, finalProduct, productdet, nofcopies);
							print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
								@Override
								public void onCompleted() {
									save.setVisibility(View.INVISIBLE);
									SOTDatabase.deleteAllProduct();
									mListView.setAdapter(null);
									cursor.requery();
									mEmpty.setVisibility(View.GONE);
									sm_total.setText("");
									sm_billDisc.setText("");
									sm_tax.setText("");
									sm_billDisc.setFocusable(false);
									sm_header_layout.setVisibility(View.GONE);
									sm_subTotal.setText("");
									sm_netTotal.setText("");
									sm_subTotal_inclusive.setText("");
									SOTDatabase.deleteBillDisc();

									SalesOrderSetGet.setCustomercode("");
									SalesOrderSetGet.setCustomername("");
									SalesOrderSetGet.setCustomerTaxPerc("");
									finish();
									Intent i = new Intent(SalesReturnSummary.this,
											SalesReturnHeader.class);
									i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(i);
									SalesReturnSummary.this.finish();
									helper.showLongToast(R.string.printed_successfully);
								}
							});
						}
					});


				} catch (IllegalArgumentException e) {
					helper.showLongToast(R.string.error_configure_printer);
				}

			}else if(printertype.matches("Zebra iMZ320 4 Inch")){
				helper.dismissProgressDialog();
				String macaddress = FWMSSettingsDatabase.getPrinterAddress();
				/*product = SOTDatabase.products();
				productdetail.setItemdisc(Double.toString(itemDisc));
				productdetail.setBilldisc(Double.toString(discnt));
				productdetail.setSubtotal(subTot);
				productdetail.setTax(totTax);
				productdetail.setNettot(netTot);
				productdet.add(productdetail);*/

				try {
					PrinterZPL printer = new PrinterZPL(SalesReturnSummary.this, macaddress);
					printer.setOnCompletionListener(new PrinterZPL.OnCompletionListener() {

						@Override
						public void onCompleted() {
							finish();
							Intent i = new Intent(SalesReturnSummary.this,
									SalesReturnHeader.class);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(i);
							SalesReturnSummary.this.finish();
						}
					});
					printer.printSalesReturn(summaryResult, soDate, customerCode,
							customerName, product, productdet,printsortHeader,appPrintGroup, nofcopies);
				} catch (IllegalArgumentException e) {
					helper.showLongToast(R.string.error_configure_printer);
				}
				save.setVisibility(View.INVISIBLE);
				SOTDatabase.deleteAllProduct();
				mListView.setAdapter(null);
				cursor.requery();
				mEmpty.setVisibility(View.GONE);
				sm_total.setText("");
				sm_billDisc.setText("");
				sm_tax.setText("");
				sm_billDisc.setFocusable(false);
				sm_header_layout.setVisibility(View.GONE);
				sm_subTotal.setText("");
				sm_netTotal.setText("");
				sm_subTotal_inclusive.setText("");
				SOTDatabase.deleteBillDisc();

				SalesOrderSetGet.setCustomercode("");
				SalesOrderSetGet.setCustomername("");
				SalesOrderSetGet.setCustomerTaxPerc("");
			}

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

					Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
							Toast.LENGTH_SHORT).show();


					reconnectDialog(msg.getData().getString("toast"));


					break;
			}
		}
	};
	public void reconnectDialog(String msg){

		final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		final Dialog dialog = new Dialog(SalesReturnSummary.this);

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

	public void print4Inch(){
		{
			int nofcopies = Integer.parseInt(stnumber.getText().toString());
			ArrayList<ProductDetails> product = new ArrayList<ProductDetails>();
			ArrayList<ProductDetails> productdet = new ArrayList<ProductDetails>();
			String customerCode = SalesOrderSetGet.getCustomercode();
			String customerName = SalesOrderSetGet.getCustomername();
			String soDate = SalesOrderSetGet.getSaleorderdate();
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
				productdetail.setItemdisc(Double.toString(itemDisc));
				productdetail.setBilldisc(Double.toString(discnt));
				productdetail.setSubtotal(subTot);
				productdetail.setTax(totTax);
				productdetail.setNettot(netTot);
				productdet.add(productdetail);

				try {
					CubePrint mPrintCube = new CubePrint(SalesReturnSummary.this,FWMSSettingsDatabase.getPrinterAddress());
					mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
						@Override
						public void onCompleted() {
							finish();
							Intent i = new Intent(SalesReturnSummary.this,
									SalesReturnHeader.class);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(i);
							SalesReturnSummary.this.finish();
						}
					});
					mPrintCube.printSalesReturn(summaryResult, soDate, customerCode,
							customerName, product, productdet, nofcopies);
				} catch (IllegalArgumentException e) {
					helper.showLongToast(R.string.error_configure_printer);
				}
				save.setVisibility(View.INVISIBLE);
				SOTDatabase.deleteAllProduct();
				mListView.setAdapter(null);
				cursor.requery();
				mEmpty.setVisibility(View.GONE);
				sm_total.setText("");
				sm_billDisc.setText("");
				sm_tax.setText("");
				sm_billDisc.setFocusable(false);
				sm_header_layout.setVisibility(View.GONE);
				sm_subTotal.setText("");
				sm_netTotal.setText("");
			    sm_subTotal_inclusive.setText("");
				SOTDatabase.deleteBillDisc();

				SalesOrderSetGet.setCustomercode("");
				SalesOrderSetGet.setCustomername("");
		    	SalesOrderSetGet.setCustomerTaxPerc("");

		}
	}

	protected void onDestroy(){
		super.onDestroy();
		if(GlobalData.mService!=null){
			GlobalData.mService.stop();
		}
	}

	@Override
	public void onBackPressed() {
		sm_billDisc.setText("");
		Intent i = new Intent(SalesReturnSummary.this,
				SalesReturnAddProduct.class);
		startActivity(i);
		SalesReturnSummary.this.finish();
	}
	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Deleting");
		alertDialog
				.setMessage("SalesReturn products will clear. Do you want to proceed");
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(SalesReturnSummary.this,
								SalesReturnHeader.class);
						startActivity(i);
						SalesReturnSummary.this.finish();
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
}