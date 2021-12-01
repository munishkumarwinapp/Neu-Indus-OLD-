package com.winapp.sot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.offline.SoapAccessTask;
import com.winapp.offline.SoapAccessTask.CallbackInterface;

public class GraSummary extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

	Cursor cursor, billCursor;
	ImageView camera, signature, location, prodphoto, sosign, arrow,
			arrowUpDown;

	ImageButton back, sm_save, barcode_edit;
	EditText sm_total, sm_subTotal,sm_subTotal_inclusive, sm_netTotal, sm_billDisc, sm_tax,
			sm_itemDisc, sm_total_new, sm_location, sm_billDiscPercentage;
	double total = 0, smTax = 0;
	ArrayList<HashMap<String, String>> datadb = new ArrayList<HashMap<String, String>>();
	LinearLayout sm_header_layout, slsummary_layout;
	String valid_url = "", summaryResult = "";
	double billDiscount = 0, taxAmt = 0;
	String subTot = "", totTax = "", netTot = "", tot = "";
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	private static final int PICK_FROM_CAMERA = 1;
	public static final int SIGNATURE_ACTIVITY = 2;
	double itemDisc = 0, sbTtl = 0, netTotal = 0;
	GPSTracker gps;
	String beforeChange = "", addview = "true";
	private ListView mListView;
	double tota = 0;
	ArrayList<HashMap<String, String>> GraDetailsArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> GraHeadersArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> GraBatchArr = new ArrayList<HashMap<String, String>>();
	String slNo = "", GRANo = "", graNumber = "";
	private SlidingMenu menu;
	private ProductListAdapter productListAdapter;
	TextView mEmpty, custname;
	boolean arrowflag = true, bottomflag = true;
	LinearLayout noofprint_layout, sm_slno_layout, sm_code_layout,
			sm_name_layout, sm_cty_layout, sm_lqty_layout, sm_qty_layout,
			sm_foc_layout, sm_price_layout, sm_total_layout,
			sm_itemdisc_layout, sm_subtotal_layout, sm_bottom_layout,
			sm_signature_layout, sm_tax_layout, sm_nettotal_layout,
			sm_arrow_layout;
	TextView listing_screen, customer_screen, addProduct_screen,
			summary_screen;
	BatchDialog batchDialog;
	SOTDatabase sqldb;
	HashMap<String, String> batch_hm;
	String HaveBatch, HaveExpiry, ProductName, PcsPerCarton;
	private int bat_i = 0;
	private String provider;
	double setLatitude, setLongitude;
	String signature_img, product_img, address1="", address2="", getSignatureimage = "", getPhotoimage="";
	// get location

	Location mLastLocation;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	String lat, lon;
	private LocationManager locationManager;
	private boolean isGPSEnabled = false, isNetworkEnabled = false;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.gra_summary);
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(false);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.summary_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		barcode_edit = (ImageButton) customNav.findViewById(R.id.barcode_edit);
		txt.setText("Gra Summary");
		back = (ImageButton) customNav.findViewById(R.id.back);

		sm_save = (ImageButton) customNav.findViewById(R.id.save);
		back = (ImageButton) customNav.findViewById(R.id.back);
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

		sm_subTotal_inclusive = (EditText) findViewById(R.id.sm_subTotal_inclusive);
		sm_total = (EditText) findViewById(R.id.sm_total);
		sm_subTotal = (EditText) findViewById(R.id.sm_subTotal);
		sm_netTotal = (EditText) findViewById(R.id.sm_netTotal);
		sm_billDisc = (EditText) findViewById(R.id.sm_billDisc);
		sm_tax = (EditText) findViewById(R.id.sm_tax);
		sm_location = (EditText) findViewById(R.id.sm_location);
		custname = (TextView) findViewById(R.id.cust_name);
		sm_total_new = (EditText) findViewById(R.id.sm_total_new);
		sm_itemDisc = (EditText) findViewById(R.id.sm_itemDisc);

		camera = (ImageView) findViewById(R.id.sm_camera_iv);
		signature = (ImageView) findViewById(R.id.sm_sign_iv);
		location = (ImageView) findViewById(R.id.sm_loc_iv);
		prodphoto = (ImageView) findViewById(R.id.prod_photo);
		sosign = (ImageView) findViewById(R.id.sm_signature);
		mListView = (ListView) findViewById(android.R.id.list);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		// custname = (TextView) findViewById(R.id.cust_name);
		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen = (TextView) findViewById(R.id.sum_screen);
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
		sm_total_layout = (LinearLayout) findViewById(R.id.sm_total_layout);
		sm_itemdisc_layout = (LinearLayout) findViewById(R.id.sm_itemdisc_layout);
		sm_subtotal_layout = (LinearLayout) findViewById(R.id.sm_subtotal_layout);
		sm_tax_layout = (LinearLayout) findViewById(R.id.sm_tax_layout);
		sm_nettotal_layout = (LinearLayout) findViewById(R.id.sm_nettotal_layout);
		sm_arrow_layout = (LinearLayout) findViewById(R.id.sm_arrow_layout);

		sm_header_layout = (LinearLayout) findViewById(R.id.sm_header_layout);
		slsummary_layout = (LinearLayout) findViewById(R.id.slsummary_layout);

		sm_billDiscPercentage = (EditText) findViewById(R.id.sm_billDiscPercentage);

		customer_screen.setText("Supplier");
//		summary_screen.setBackgroundColor(Color.parseColor("#00AFF0"));
		summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
		summary_screen.setBackgroundResource(drawable.tab_select_right);

		FWMSSettingsDatabase.init(GraSummary.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SOTSummaryWebService(valid_url);

		batchDialog = new BatchDialog();
		sqldb = new SOTDatabase(GraSummary.this);
		
		SOTDatabase.init(GraSummary.this);
		signature_img = SOTDatabase.getSignatureImage();
		product_img = SOTDatabase.getProductImage();
		
		if (product_img != null && !product_img.isEmpty()) {
			Log.d("photo if ", product_img);
			try {
			byte[] encodePhotoByte = Base64.decode(product_img, Base64.DEFAULT);
			Bitmap photo = BitmapFactory.decodeByteArray(encodePhotoByte, 0,
					encodePhotoByte.length);
			photo = Bitmap.createScaledBitmap(photo, 95, 80, true);

			prodphoto.setImageBitmap(photo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

		}

		if (signature_img != null && !signature_img.isEmpty()) {
			byte[] encodeByte = Base64.decode(signature_img, Base64.DEFAULT);
			Bitmap photo = BitmapFactory.decodeByteArray(encodeByte, 0,
					encodeByte.length);
			photo = Bitmap.createScaledBitmap(photo, 300, 80, true);
			sosign.setImageBitmap(photo);
		} else {

		}

		// Getting LocationManager object
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		buildGoogleApiClient();

		if (!mGoogleApiClient.isConnected())
			mGoogleApiClient.connect();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			GraDetailsArr.clear();
			GraHeadersArr.clear();
			GraBatchArr.clear();

			GraDetailsArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("GraDetails");
			GraHeadersArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("GraHeader");
			GraBatchArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("GraBatch");
			getSignatureimage = (String) getIntent().getSerializableExtra(
					"getSignatureimage");
			getPhotoimage = (String) getIntent().getSerializableExtra(
					"getPhotoimage");

			SOTDatabase.storeImage(1, getSignatureimage, getPhotoimage);

			Log.d("GraDetailsArr", "" + GraDetailsArr);
			Log.d("GraHeadersArr", "" + GraHeadersArr);
			Log.d("GraBatchArr", "" + GraBatchArr);
			int sl_no;
			if (GraHeadersArr != null) {
				for (int i = 0; i < GraHeadersArr.size(); i++) {

					GRANo = GraHeadersArr.get(i).get("GRANo");
					graNumber = GRANo;
					// // ConvertToSetterGetter.setSoNo(Inv_No);
					ConvertToSetterGetter.setEdit_gra_no(GRANo);

					String GRADate = GraHeadersArr.get(i).get("GRADate").split("\\ ")[0];
					String InvoiceNo = GraHeadersArr.get(i).get("InvoiceNo");
					String InvoiceDate = GraHeadersArr.get(i)
							.get("InvoiceDate").split("\\ ")[0];
					String DONo = GraHeadersArr.get(i).get("DONo");
					String DODate = GraHeadersArr.get(i).get("DODate").split("\\ ")[0];
					String LocationCode = GraHeadersArr.get(i).get(
							"LocationCode");
					String SupplierCode = GraHeadersArr.get(i).get(
							"SupplierCode");

					String Total = GraHeadersArr.get(i).get("Total");
					String ItemDiscount = GraHeadersArr.get(i).get(
							"ItemDiscount");
					String BillDIscount = GraHeadersArr.get(i).get(
							"BillDIscount");
					String Remarks = GraHeadersArr.get(i).get("Remarks");
					String Status = GraHeadersArr.get(i).get("Status");
					String CurrencyCode = GraHeadersArr.get(i).get(
							"CurrencyCode");
					String CurrencyRate = GraHeadersArr.get(i).get(
							"CurrencyRate");
					String TransferNo = GraHeadersArr.get(i).get("TransferNo");
					String TransferLocation = GraHeadersArr.get(i).get(
							"TransferLocation");

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

					// SalesOrderSetGet.setSuppliergroupcode(cstmrgrpcdal.get(1));
					SalesOrderSetGet.setCurrencycode(CurrencyCode);
					SalesOrderSetGet.setCurrencyname(SalesOrderSetGet
							.getCustname());
					SalesOrderSetGet.setSaleorderdate(GRADate);
					SalesOrderSetGet.setCurrencyrate(CurrencyRate);
					SalesOrderSetGet.setRemarks(Remarks);
					SalesOrderSetGet.setGrainvoiceno(InvoiceNo);
					SalesOrderSetGet.setGradono(DONo);
					SalesOrderSetGet.setGrainvoicedate(InvoiceDate);
					SalesOrderSetGet.setGradodate(DODate);

					SalesOrderSetGet.setCustomername(SalesOrderSetGet
							.getCustname());
					SalesOrderSetGet.setCustomercode(SupplierCode);
				}
			}

			if (GraDetailsArr != null) {
				for (int i = 0; i < GraDetailsArr.size(); i++) {

					slNo = GraDetailsArr.get(i).get("slNo");

					String ProductCode = GraDetailsArr.get(i)
							.get("ProductCode");
					String ProductName = GraDetailsArr.get(i)
							.get("ProductName");
					String CQty = GraDetailsArr.get(i).get("CQty");
					String LQty = GraDetailsArr.get(i).get("LQty");
					String Qty = GraDetailsArr.get(i).get("Qty");
					String FOCQty = GraDetailsArr.get(i).get("FOCQty");
					String PcsPerCarton = GraDetailsArr.get(i).get(
							"PcsPerCarton");
					String UnitPrice = GraDetailsArr.get(i).get("UnitPrice");
					String GrossPrice = GraDetailsArr.get(i).get("GrossPrice");
					String CartonPrice = GraDetailsArr.get(i).get("CartonPrice");

					String Total = GraDetailsArr.get(i).get("Total");
					String ItemDiscount = GraDetailsArr.get(i).get(
							"ItemDiscount");
					String SubTotal = GraDetailsArr.get(i).get("SubTotal");
					String Tax = GraDetailsArr.get(i).get("Tax");
					String NetTotal = GraDetailsArr.get(i).get("NetTotal");
					String TaxType = GraDetailsArr.get(i).get("TaxType");
					String TaxPerc = GraDetailsArr.get(i).get("TaxPerc");
					SalesOrderSetGet.setCompanytax(TaxType);
//					SalesOrderSetGet.setTaxValue(TaxPerc);
					String AverageCost = GraDetailsArr.get(i)
							.get("AverageCost");
					String QtyOnHand = GraDetailsArr.get(i).get("QtyOnHand");

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

					Log.d("UnitPrice", "" + UnitPrice);
					Log.d("GrossPrice", "" + GrossPrice);

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

					double price = Double.parseDouble(UnitPrice);
					double itemDiscount = Double.parseDouble(ItemDiscount);
					double total = Double.parseDouble(Total);
					double tax = Double.parseDouble(Tax);

					Log.d("price", "" + price);
					Log.d("itemDiscount", "" + itemDiscount);
					Log.d("total", "" + total);
					Log.d("tax", "" + tax);

					SOTDatabase.storeProduct(sl_no, ProductCode, ProductName,
							cQty, lQty, qty, focQty, price, itemDiscount, "",
							pcsPerCarton, total, tax, NetTotal, TaxType,
							TaxPerc, UnitPrice, SubTotal, CartonPrice, "0", "0", "",
							"","","","","");
					
					if (GraBatchArr != null) {
					      for (bat_i = 0; bat_i < GraBatchArr.size(); bat_i++) {

					       Log.d("batch size", "" + GraBatchArr.size());
					       Log.d("pd code", "" + "" + GraBatchArr.get(bat_i).get("ProductCode"));
					       String cmpnyCode = SupplierSetterGetter.getCompanyCode();
					       String pdt_code = GraBatchArr.get(bat_i).get("ProductCode");
					       
					       if(pdt_code.matches(ProductCode)){
					        List<PropertyInfo> params = new ArrayList<PropertyInfo>();
					        params.add(newPropertyInfo("CompanyCode", cmpnyCode));
					        params.add(newPropertyInfo("ProductCode",
					          GraBatchArr.get(bat_i).get("ProductCode")));
					        
					        new SoapAccessTask(GraSummary.this, valid_url,
					          "fncGetProduct", params, new GetProduct(bat_i, sl_no))
					          .execute();
					       }

					      }
					     }
				}
			}
		}

		datadb = sqldb.getAllProducts();

		if ((SalesOrderSetGet.getCartonpriceflag() != null && !SalesOrderSetGet
				.getCartonpriceflag().isEmpty())) {
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

		sm_code_layout.setVisibility(View.GONE);
		sm_foc_layout.setVisibility(View.GONE);
		sm_total_layout.setVisibility(View.GONE);
		sm_itemdisc_layout.setVisibility(View.GONE);

		sm_tax_layout.setVisibility(View.GONE);
		sm_nettotal_layout.setVisibility(View.GONE);

		arrowUpDown.setImageResource(drawable.arrow_up);
		sm_bottom_layout.setVisibility(View.GONE);
		sm_signature_layout.setVisibility(View.VISIBLE);

		SOTDatabase.init(GraSummary.this);
		cursor = SOTDatabase.getCursor();

		sm_billDisc.setRawInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);

		if (cursor != null && cursor.getCount() > 0) {

			sm_header_layout.setVisibility(View.VISIBLE);
			sm_billDisc.setEnabled(true);

			tota = SOTDatabase.getTotal();
			Log.d("Tota", "" + tota);
			String smtotal = twoDecimalPoint(tota);

			smTax = SOTDatabase.getTax();
			String ProdTax = fourDecimalPoint(smTax);
			sm_tax.setText("" + ProdTax);

			sbTtl = SOTDatabase.getSubTotal();
			String sub = twoDecimalPoint(sbTtl);
			sm_subTotal.setText("" + sub);

			double tot_item_disc = SOTDatabase.getTotalItemDisc();
			String tot_itemDisc = twoDecimalPoint(tot_item_disc);

			sm_itemDisc.setText(tot_itemDisc);

			sm_total.setText("" + sub);

			sm_total_new.setText("" + smtotal);
		
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

			sm_save.setVisibility(View.VISIBLE);
			mEmpty.setVisibility(View.GONE);

			if (!datadb.isEmpty()) {

				if (barcode_edit.getVisibility() == View.GONE) {
					barcode_edit.setVisibility(View.VISIBLE);
				}
			}

		} else {
			mEmpty.setVisibility(View.VISIBLE);
			sm_save.setVisibility(View.INVISIBLE);
			sm_header_layout.setVisibility(View.GONE);
			sm_billDisc.setFocusable(false);

			if (datadb.isEmpty()) {
				if (barcode_edit.getVisibility() == View.VISIBLE) {
					barcode_edit.setVisibility(View.GONE);
				}

			}
		}

		// setListAdapter(new ProductListAdapter(this, cursor));
		productListAdapter = new ProductListAdapter(this, cursor);
		mListView.setAdapter(productListAdapter);
		registerForContextMenu(mListView);

		taxCalc(); // recalculate tax value
		smTax = SOTDatabase.getTax();
		String ProdTax = fourDecimalPoint(smTax);
		sm_tax.setText("" + ProdTax);

		//code repeated for bill discount recalculation
		sbTtl = SOTDatabase.getSubTotal();
		String sub = twoDecimalPoint(sbTtl);
		sm_subTotal.setText("" + sub);



		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Cursor cursor = SOTDatabase.getCursor();
				int count = cursor.getCount();
				if(count>0){
					alertDialog();
				}else{

					Intent i = new Intent(GraSummary.this, GraHeader.class);
					startActivity(i);
					GraSummary.this.finish();
				}
				

			}
		});

		customer_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(GraSummary.this, GraCustomer.class);
				startActivity(i);
				GraSummary.this.finish();

			}
		});

		addProduct_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(GraSummary.this, GraAddProduct.class);
				startActivity(i);
				GraSummary.this.finish();

			}
		});

		barcode_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(GraSummary.this, AddBarcodeSummary.class);
				i.putExtra("Barcodefrom", "GraSummary");
				startActivity(i);
				GraSummary.this.finish();
			}
		});

		arrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (arrowflag) {

					arrowflag = false;
					arrow.setImageResource(drawable.ic_arrow_left);

					sm_code_layout.setVisibility(View.VISIBLE);
					sm_cty_layout.setVisibility(View.VISIBLE);
					sm_lqty_layout.setVisibility(View.VISIBLE);
					sm_foc_layout.setVisibility(View.VISIBLE);
					sm_total_layout.setVisibility(View.VISIBLE);
					sm_itemdisc_layout.setVisibility(View.VISIBLE);
					sm_tax_layout.setVisibility(View.VISIBLE);
					sm_nettotal_layout.setVisibility(View.VISIBLE);
					sm_qty_layout.setVisibility(View.VISIBLE);
					productListAdapter.selectAll(false);

				} else if (arrowflag == false) {

					arrow.setImageResource(drawable.ic_arrow_right);
					arrowflag = true;

					sm_code_layout.setVisibility(View.GONE);
					// sm_cty_layout.setVisibility(View.GONE);
					// sm_lqty_layout.setVisibility(View.GONE);
					sm_foc_layout.setVisibility(View.GONE);
					sm_total_layout.setVisibility(View.GONE);
					sm_itemdisc_layout.setVisibility(View.GONE);
					sm_tax_layout.setVisibility(View.GONE);
					sm_nettotal_layout.setVisibility(View.GONE);
					// sm_qty_layout.setVisibility(View.GONE);

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

					productListAdapter.selectAll(true);

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
		});
*/

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
		sm_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

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
					Toast.makeText(GraSummary.this, "No data found",
							Toast.LENGTH_SHORT).show();
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

					Intent i = new Intent(GraSummary.this, AddBarcode.class);
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
					i.putExtra("Barcodefrom", "GraSummary");
					startActivity(i);
					GraSummary.this.finish();
				} else {
					Log.d("AppType--s->", "" + appType);
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

					if (!billDisc.matches("")) {

						double billDiscCalc = Double.parseDouble(billDisc);
						double sbtot = SOTDatabase.getsumsubTot();
//						billDiscount = billDiscCalc / sbtot;

//						Log.d("billdisc", "" + billDiscount);
						
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
							if(taxType!=null && !taxType.isEmpty()){
								if (taxType.matches("I") || taxType.matches("Z")) {
									net = sbt;
								}else{
									net = taxAt + sbt;
								}
							}else{
								net = taxAt + sbt;
							}

							String netTo = twoDecimalPoint(net);
							String subTo = twoDecimalPoint(sbt);

							Log.d("subtotal", subTo);

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
				CameraAction();
			}
		});

		signature.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent i = new Intent(GraSummary.this, CaptureSignature.class);
				startActivityForResult(i, SIGNATURE_ACTIVITY);

			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(GraSummary.this, GraAddProduct.class);
				startActivity(i);
				GraSummary.this.finish();
			}
		});

		String custCode = SalesOrderSetGet.getCustomercode();
		String custName = SalesOrderSetGet.getCustomername();
		custname.setText(custName + " (" + custCode + ")");
	}

	public class GetProduct implements CallbackInterface {
		int i,sr_slno;

		public GetProduct(int bat_i, int slno) {
			this.i = bat_i;
			this.sr_slno = slno;
		}

		@Override
		public void onSuccess(JSONArray jsonArray) {
			try {
				int len = jsonArray.length();
				Log.d("Json length", "lllll" + len);
				for (int i = 0; i < len; i++) {
					JSONObject object = jsonArray.getJSONObject(i);

					HaveBatch = object.getString("HaveBatch");
					HaveExpiry = object.getString("HaveExpiry");
					ProductName = object.getString("ProductName");
					PcsPerCarton = object.getString("PcsPerCarton");

					batch_hm = new HashMap<String, String>();

					batch_hm.put("ProductName", ProductName);
					batch_hm.put("PcsPerCarton", PcsPerCarton);
					batch_hm.put("HaveBatch", HaveBatch);
					batch_hm.put("HaveExpiry", HaveExpiry);

					Log.d("HaveBatch", HaveBatch);

				}
				batch(i, sr_slno, ProductName, PcsPerCarton, HaveBatch, HaveExpiry);
			}

			catch (JSONException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onFailure(Exception error) {
			/*if (error == ErrorType.NETWORK_UNAVAILABLE) {
				Toast.makeText(GraSummary.this, "Error, Try Again",
						Toast.LENGTH_LONG).show();
			} else {*/
				Toast.makeText(GraSummary.this, "Error, Try Again",
						Toast.LENGTH_LONG).show();
			//}
		}

	}

	/*public void getAddress(double latitude, double longitude) throws Exception {
		Log.d("getaddress", "gps "+latitude);
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
						Log.d("sl_no=", "" + sl_no);

						double sbTotal = SOTDatabase.getsubTotal(sl_no);
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
		// System.out.print(df.format(d));
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}

	public String fourDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.####");
		// System.out.print(df.format(d));
		df.setMinimumFractionDigits(4);
		String tot = df.format(d);

		return tot;
	}

	private class ProductListAdapter extends ResourceCursorAdapter {

		@SuppressWarnings("deprecation")
		public ProductListAdapter(Context context, Cursor cursor) {
			super(context, R.layout.sales_summary_list, cursor);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			LinearLayout ss_prodcode_layout = (LinearLayout) view
					.findViewById(R.id.ss_prodcode_layout);
			LinearLayout ss_cqty_layout = (LinearLayout) view
					.findViewById(R.id.ss_cqty_layout);
			LinearLayout ss_lqty_layout = (LinearLayout) view
					.findViewById(R.id.ss_lqty_layout);
			LinearLayout ss_qty_layout = (LinearLayout) view
					.findViewById(R.id.ss_qty_layout);
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

				Log.d("false-->", "false");
			} else {

				ss_prodcode_layout.setVisibility(View.GONE);
				ss_foc_layout.setVisibility(View.GONE);
				ss_total_layout.setVisibility(View.GONE);
				ss_itemdisc_layout.setVisibility(View.GONE);
				ss_tax_layout.setVisibility(View.GONE);
				ss_netTotal_layout.setVisibility(View.GONE);

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

				Log.d("true-->", "true");

			}

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
			TextView ss_cprice = (TextView) view.findViewById(R.id.ss_cprice);
			ss_cprice.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE)));
			TextView ss_item_disc = (TextView) view
					.findViewById(R.id.ss_item_disc);
			ss_item_disc.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT)));

			TextView ss_total = (TextView) view.findViewById(R.id.ss_total);
			ss_total.setText(""
					+ cursor.getDouble(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TOTAL)));

			TextView ss_tax = (TextView) view.findViewById(R.id.ss_tax);
			ss_tax.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_TAX)));

			TextView ss_subTotal = (TextView) view
					.findViewById(R.id.ss_subTotal);
			ss_subTotal.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL)));
			Log.d("Total",
					""
							+ cursor.getDouble(cursor
									.getColumnIndex(SOTDatabase.COLUMN_TOTAL)));

			TextView ss_minselling_price = (TextView) view.findViewById(R.id.ss_minselling_price);
			ss_minselling_price.setText(cursor.getString(cursor.getColumnIndex(SOTDatabase.COLUMN_MINIMUM_SELLING_PRICE)));

			TextView ss_minselling_cartonprice = (TextView) view.findViewById(R.id.ss_minselling_cartonprice);
			ss_minselling_cartonprice.setText(cursor.getString(cursor.getColumnIndex(SOTDatabase.COLUMN_MINIMUM_CARTON_SELLING_PRICE)));

		}

		public void selectAll(boolean select) {

			if (select == true) {
				addview = "true";
			} else if (select == false) {
				addview = "false";
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
		String bllDsc;
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(GraSummary.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(slsummary_layout, false);
			progressBar = new ProgressBar(GraSummary.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
			bllDsc = sm_billDisc.getText().toString();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			double discnt = 0.0;

			if (!bllDsc.matches("")) {
				discnt = Double.parseDouble(bllDsc);
			}

			try {

				if (ConvertToSetterGetter.getEdit_gra_no() == null
						|| ConvertToSetterGetter.getEdit_gra_no().matches("")) {
					graNumber = "";
				} else {
					graNumber = ConvertToSetterGetter.getEdit_gra_no();
				}

				summaryResult = SOTSummaryWebService.summaryGRAService(
						"fncSaveGRA", discnt, subTot, totTax, netTot,
						Double.toString(tota), graNumber);
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

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Void result) {

			if (summaryResult.matches("failed")) {

				Toast.makeText(GraSummary.this, "Failed", Toast.LENGTH_SHORT)
						.show();

			} else {
							
				signature_img = SOTDatabase.getSignatureImage();
				product_img = SOTDatabase.getProductImage();
				
				if(signature_img==null){
					signature_img ="";
				}
				if(product_img==null){
					product_img="";
				}
          
//				if (onlineMode.matches("True")) {
//					if (checkOffline == true) {
//					} else { // online
						String imgResult = SOTSummaryWebService.saveSignatureImage(
								summaryResult, "" + setLatitude, "" + setLongitude,
								signature_img, product_img, "fncSaveInvoiceImages", "GRA", address1, address2);

						Log.d("fncSaveInvoiceImages", "" + summaryResult + " "
								+ setLatitude + " " + setLongitude + " GRA " + address1 + address2 +"signature_img "
								+ signature_img + "product_img " + product_img);

						if (!imgResult.matches("")) {
							Log.d("Cap Image", "Saved");
						} else {
							Log.d("Cap Image", "Not Saved");
						}
//					}
//				}
				
				barcode_edit.setVisibility(View.GONE);
				SOTDatabase.deleteAllProduct();
				mListView.setAdapter(productListAdapter);
				cursor.requery();
				mEmpty.setVisibility(View.VISIBLE);
				sm_total.setText("");
				sm_subTotal_inclusive.setText("");
				sm_billDisc.setText("");
				sm_tax.setText("");
				sm_billDisc.setFocusable(false);
				sm_header_layout.setVisibility(View.GONE);
				sm_subTotal.setText("");
				sm_netTotal.setText("");
				sm_save.setVisibility(View.INVISIBLE);
				SOTDatabase.deleteBillDisc();
				SOTDatabase.deleteallbatch();
				SalesOrderSetGet.setCustomercode("");
				SalesOrderSetGet.setCustomername("");

				Toast.makeText(GraSummary.this, "Saved Successfully",
						Toast.LENGTH_SHORT).show();
				Intent i = new Intent(GraSummary.this, GraHeader.class);
				startActivity(i);
				GraSummary.this.finish();

			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(slsummary_layout, true);
		}
	}

	public void CameraAction() {
		/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 0);
		intent.putExtra("aspectY", 0);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 150);
		try {
			intent.putExtra("return-data", true);
			startActivityForResult(intent, PICK_FROM_CAMERA);
		} catch (ActivityNotFoundException e) {
		}*/
		
		try {				
			 Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	         if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	             startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
	         }
	         
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {

			case PICK_FROM_CAMERA:
				if (requestCode == PICK_FROM_CAMERA) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						Bitmap photo = extras.getParcelable("data");
						photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
						prodphoto.setImageBitmap(photo);

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] bitMapData = stream.toByteArray();
						String camera_image = Base64.encodeToString(bitMapData,
								Base64.DEFAULT);
						SOTDatabase.init(GraSummary.this);

						Cursor ImgCursor = SOTDatabase.getImageCursor();
						if (ImgCursor.getCount() > 0) {
							String signature_image = SOTDatabase
									.getSignatureImage();
							SOTDatabase.updateImage(1, signature_image,
									camera_image);
						} else {
							SOTDatabase.storeImage(1, "", camera_image);
						}

						Log.d("Camera Image", "cam" + camera_image);
					}
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
						SOTDatabase.init(GraSummary.this);
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
		}
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

		Log.d("code", strProdCode);
		String havebatch = SOTDatabase.getprodcodefrombatch(strProdCode);
		if (!havebatch.matches("")) {
			menu.add(0, v.getId(), 0, "Edit Batch");
		}

		menu.add(0, v.getId(), 0, "Edit Product");
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
			String sno = ((TextView) info.targetView.findViewById(R.id.ss_slno))
					.getText().toString();
			String code = ((TextView) info.targetView
					.findViewById(R.id.ss_prodcode)).getText().toString();
			String name = ((TextView) info.targetView
					.findViewById(R.id.ss_name)).getText().toString();
			String price = ((TextView) info.targetView
					.findViewById(R.id.ss_price)).getText().toString();

			ArrayList<String> batchHave = new ArrayList<String>();
			batchHave = SOTDatabase.getBatchHave(code);

			String haveBatch = batchHave.get(0);
			String haveExpiry = batchHave.get(1);
			String slCartonPerQty = batchHave.get(2);

			String haveBatchOnStockIn = SalesOrderSetGet.getHaveBatchOnStockIn();
			if (haveBatchOnStockIn.matches("True")) {
				Log.d("haveBatchOnStockIn", haveBatchOnStockIn);
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

					batchDialog.initiateBatchPopupWindow(GraSummary.this, id, sno,
							haveBatch, haveExpiry, code, name, slCartonPerQty,
							cursor, price, hm);
				} else {
					Log.d("no haveBatch", "no haveExpiry");

				}

			} else {
				Log.d("no haveBatchOnStockIn", "no haveBatchOnStockIn");

			}

		} else if (item.getTitle() == "Edit Product") {

			Intent i = new Intent(GraSummary.this, GraAddProduct.class);
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
			i.putExtra("SOT_str_ssuom", cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_UOM)));
			i.putExtra("SOT_st_sscpqty", cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY)));
			i.putExtra("SOT_st_sstaxvalue", cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE)));
			i.putExtra("SOT_st_sstotal", ((TextView) info.targetView
					.findViewById(R.id.ss_subTotal)).getText().toString());
			i.putExtra("SOT_st_sstax", ((TextView) info.targetView
					.findViewById(R.id.ss_tax)).getText().toString());
			i.putExtra("SOT_st_ssnettot", ((TextView) info.targetView
					.findViewById(R.id.ss_net_total)).getText().toString());
			i.putExtra("SOT_str_sscprice", ((TextView) info.targetView
					.findViewById(R.id.ss_cprice)).getText().toString());
			i.putExtra("SOT_str_minselling_price", ((TextView) info.targetView
					.findViewById(R.id.ss_minselling_price)).getText()
					.toString());
			i.putExtra("SOT_str_minselling_cartonprice", ((TextView) info.targetView
					.findViewById(R.id.ss_minselling_cartonprice)).getText()
					.toString());
			i.putExtra("SOT_ssupdate", "Update");
			i.putExtra("SOT_sscancel", "Cancel");
			startActivity(i);
			GraSummary.this.finish();
		} else if (item.getTitle() == "Delete") {
			String id = ((TextView) info.targetView.findViewById(R.id.ss_slid))
					.getText().toString();
			String serlno = ((TextView) info.targetView.findViewById(R.id.ss_slno))
					.getText().toString();
			String prodCode = ((TextView) info.targetView
					.findViewById(R.id.ss_prodcode)).getText().toString();
			Log.d("id", id);
			SOTDatabase.deleteProduct(id);
			SOTDatabase.deleteBillDiscount(id);
			SOTDatabase.deleteBatchProductSR(prodCode,serlno);
//			SOTDatabase.deleteBatchProduct(prodCode);
			
			sm_billDisc.setText("");
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
				SOTDatabase.updateBatchSR_slno(old_slno,"" + sno);
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
				Toast.makeText(GraSummary.this, "Deleted", Toast.LENGTH_LONG)
						.show();
			} else {
				sm_header_layout.setVisibility(View.GONE);
				sm_billDisc.setFocusable(false);
				mEmpty.setVisibility(View.VISIBLE);
				sm_total.setText("");
				sm_billDisc.setText("");
				sm_tax.setText("");
				sm_subTotal.setText("");
				sm_netTotal.setText("");
				sm_itemDisc.setText("");
				sm_total_new.setText("");
				sm_save.setVisibility(View.INVISIBLE);
				barcode_edit.setVisibility(View.GONE);
			}

		} else {
			return false;
		}
		return true;

	}

	public void batch(int i, int sr_slno,String productName, String pcsPerCarton,
			String haveBatch, String haveExpiry) {

		HashMap<String, String> queryValues = new HashMap<String, String>();
		String slNo = GraBatchArr.get(i).get("slNo");
		String ProductCode = GraBatchArr.get(i).get("ProductCode");
		String BatchNo = GraBatchArr.get(i).get("BatchNo");
		String ExpiryDate = GraBatchArr.get(i).get("ExpiryDate");
		String Remarks = GraBatchArr.get(i).get("Remarks");
		String CQty = GraBatchArr.get(i).get("CQty");
		String LQty = GraBatchArr.get(i).get("LQty");
		String Qty = GraBatchArr.get(i).get("Qty");
		String FOCQty = GraBatchArr.get(i).get("FOCQty");
		String UnitCost = GraBatchArr.get(i).get("UnitCost");

		Log.d("slNo", "" + slNo);
		Log.d("ProductCode", "" + ProductCode);
		Log.d("ProductName", "" + productName);
		Log.d("BatchNo", "" + BatchNo);
		Log.d("ExpiryDate", "" + ExpiryDate);
		Log.d("Remarks", "" + Remarks);
		Log.d("CQty", "" + CQty);
		Log.d("LQty", "" + LQty);
		Log.d("Qty", "" + Qty);
		Log.d("FOCQty", "" + FOCQty);
		Log.d("UnitCost", "" + UnitCost);
		Log.d("pcsPerCarton", "" + pcsPerCarton);
		Log.d("haveBatch", "" + haveBatch);
		Log.d("haveExpiry", "" + haveExpiry);

		queryValues.put("slNo", slNo);
		queryValues.put("ProductCode", ProductCode);
		queryValues.put("ProductName", productName);
		queryValues.put("BatchNo", BatchNo);
		queryValues.put("ExpiryDate", ExpiryDate);
		queryValues.put("Remarks", Remarks);
		queryValues.put("CQty", CQty);
		queryValues.put("LQty", LQty);
		queryValues.put("Qty", Qty);
		queryValues.put("FOCQty", FOCQty);
		queryValues.put("Price", UnitCost);
		queryValues.put("PcsPerCarton", pcsPerCarton);
		queryValues.put("HaveBatch", haveBatch);
		queryValues.put("HaveExpiry", haveExpiry);
		queryValues.put("SR_Slno", sr_slno+"");
		
		SOTDatabase.storeBatch(queryValues);
	}

	public static PropertyInfo newPropertyInfo(String name, String value) {
		PropertyInfo propertyInfo = new PropertyInfo();
		propertyInfo.setName(name);
		propertyInfo.setValue(value);
		return propertyInfo;
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(GraSummary.this, GraAddProduct.class);
		startActivity(i);
		GraSummary.this.finish();
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
	
	/*@Override
	public void onLocationChanged(Location location) {
		// Getting reference to TextView tv_longitude
		Log.d("Location", "gps "+location.getLatitude());
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
//	    locationManager.requestLocationUpdates(provider, 1000, 1, this);
	  }

	  *//* Remove the locationlistener updates when Activity is paused *//*
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	  }*/

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
//				new getServerDateTime(lati, longi, "1").execute();

				updateUI(setLatitude,setLongitude);
			}else {
//				Log.d("gpsLocation", " null ");
//				new getServerDateTime(0, 0, "0").execute();
				updateUI(0,0);
//					Toast.makeText(getApplicationContext(),
//							"GPS is OFF. Please turn ON",
//							Toast.LENGTH_LONG).show();
			}

		} else {
			Toast.makeText(getApplicationContext(),
					"No Internet Connection",
					Toast.LENGTH_LONG).show();
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

	  public void alertDialog() {

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle("Deleting");
			alertDialog.setMessage("Gra products will clear. Do you want to proceed");
			alertDialog.setIcon(R.mipmap.ic_exit);
			alertDialog.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							Intent i = new Intent(GraSummary.this, GraHeader.class);
							startActivity(i);
							GraSummary.this.finish();
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
	  
}