package com.winapp.sotdetails;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.FastScroll.FastSearchListView;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.googlemaps.MapActivity;
import com.winapp.helper.Constants;
import com.winapp.helper.Customer;
import com.winapp.model.Product;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.printer.PDFActivity;
import com.winapp.sot.CaptureSignature;
import com.winapp.sot.Company;
import com.winapp.sot.ConsignmentAddProduct;
import com.winapp.sot.ConvertToSetterGetter;
import com.winapp.sot.DeliveryAddProduct;
import com.winapp.sot.GPSTracker;
import com.winapp.sot.InvoiceAddProduct;
import com.winapp.sot.InvoiceCashCollection;
import com.winapp.sot.OfflineSalesOrderWebService;
import com.winapp.sot.RowItem;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SOTSummaryWebService;
import com.winapp.sot.SalesAddProduct;
import com.winapp.sot.SalesOrderHeader;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SalesProductWebService;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.sot.WebServiceClass;

public class CustomerListActivity extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace,Constants,LocationListener {

	LinearLayout offlineLayout;

	LinearLayout customerList_layout;
	LinearLayout pagetitle_layout, search_btn_layout, auto_search_layout,
			header_layout;
	ImageButton product_search, new_add;
	TextView customer_Title;
	FastSearchListView customer_list;
	AutoCompleteTextView actv_search;
	ArrayList<HashMap<String, String>> vanlist = new ArrayList<HashMap<String, String>>();
	ArrayAdapter<String> auto_adapter;
	ArrayList<String> Customer_Name_List = new ArrayList<String>();
	CustomerListCustomAdapter Adapter;
	ArrayList<SOTdetailsGetSet> CustomerListArray = new ArrayList<SOTdetailsGetSet>();
	HashMap<String, String> customer_hashValue = new HashMap<String, String>();
	// String customer_jsonString = null;
	// JSONObject customer_jsonResponse;
	// JSONArray customer_jsonMainNode;
	String customer_jsonString = null, areacode_jsonString = null;
	JSONObject customer_jsonResponse, areacode_jsonResponse;
	JSONArray customer_jsonMainNode, area_jsonMainNode;
	String LocationCode = "", selectedArea = "";
	String valid_url = "";
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	SlidingMenu menu;
	String cust_id = "", intentString, serverdate = "", currencyCode = "",
			currencyName = "", currencyRate = "", custName = "";
	ArrayList<String> cstmrgrpcdal = new ArrayList<String>();
	TableLayout table_layout;
	ArrayList<HashMap<String, String>> al_cust = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> al_areacode = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> customerhashValue = new HashMap<String, String>();
	private ArrayList<HashMap<String, String>> areaArrHm;

	private EditText ed_custcode, ed_custname, ed_areacode, ed_description, ed_vancode, ed_vanname;
	private Button bt_search;
	private ArrayList<String> alclcrrncy;
	// @Offline
	boolean checkOffline;
	String onlineMode, offlineDialogStatus;
	private OfflineSettingsManager offlinemanager;
	private OfflineCommon offlineCommon;
	private OfflineDatabase offlineDatabase;
	private String deliveryOrderStr, salesOrderStr,  invoiceStr, receiptsStr,select_van, dialogStatus="",consignmentStr="";
	private LocationManager locationManager;
	EditText remarks;
	EditText location_txt;
	ImageView sig_image,signature,location,cam_image,camera;
	double setLatitude, setLongitude;
	String product_img="",mCurrentPhotoPath;
	String sign_img="",address1,address2,signature_img,mCurrentgetPath;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	public static final int SIGNATURE_ACTIVITY = 2;
	private static final int PICK_FROM_CAMERA = 1;
	// check if GPS enabled
	GPSTracker gpsTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.customer_list_main);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.customer_list_title, null);
		customer_Title = (TextView) customNav.findViewById(R.id.customer_Title);
		actv_search = (AutoCompleteTextView) customNav.findViewById(R.id.auto_edit_search);
		customer_Title.setText("Customer List");
		product_search = (ImageButton) customNav.findViewById(R.id.search);
		new_add = (ImageButton) customNav.findViewById(R.id.newcustomerbutton);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SOTSummaryWebService(valid_url);

		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayHomeAsUpEnabled(false);
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		// menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);
		SOTDatabase.init(CustomerListActivity.this);

		final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// @Offline
		onlineMode = OfflineDatabase.getOnlineMode();
		offlineDatabase = new OfflineDatabase(CustomerListActivity.this);
		offlineCommon = new OfflineCommon(CustomerListActivity.this);
		checkOffline = OfflineCommon.isConnected(CustomerListActivity.this);
		OfflineDatabase.init(CustomerListActivity.this);
		new OfflineSalesOrderWebService(CustomerListActivity.this);

		offlineLayout = (LinearLayout) findViewById(R.id.offlineLayout);
		customerList_layout = (LinearLayout) findViewById(R.id.customerList_layout);
		table_layout = (TableLayout) findViewById(R.id.table_layout);
		customer_list = (FastSearchListView) findViewById(R.id.customer_list);
		ed_custcode = (EditText) findViewById(R.id.ed_custcode);
		ed_custname = (EditText) findViewById(R.id.ed_custname);
		ed_areacode = (EditText) findViewById(R.id.ed_areacode);
		ed_description = (EditText) findViewById(R.id.ed_description);
		bt_search = (Button) findViewById(R.id.bt_search);
		ed_vancode = (EditText) findViewById(R.id.ed_vancode);
		ed_vanname = (EditText) findViewById(R.id.ed_vanname);
		
		alclcrrncy = new ArrayList<String>();
		areaArrHm = new ArrayList<HashMap<String, String>>();
		areaArrHm.clear();
		alclcrrncy.clear();
		boolean addCustRslt = FormSetterGetter.isCustomerAdd();

		if (onlineMode.matches("True")) {
			offlineLayout.setVisibility(View.GONE);
		} else if (onlineMode.matches("False")) {
			offlineLayout.setVisibility(View.VISIBLE);
		}

		if (addCustRslt == true) {
			new_add.setVisibility(View.VISIBLE);

		} else if (addCustRslt == false) {
			new_add.setVisibility(View.GONE);

		}

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String invnumber = getIntent().getStringExtra("InvoiceNo");
			String companyCode = SupplierSetterGetter.getCompanyCode();
			if(invnumber!=null && !invnumber.isEmpty()){
				new PDFActivity(CustomerListActivity.this,valid_url+"/"+FNCA4INVOICEGENERATE+"?CompanyCode="+companyCode+"&sInvoiceNo="+invnumber, "report.pdf").execute();
			}
		}

		FWMSSettingsDatabase.init(CustomerListActivity.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new WebServiceClass(valid_url);
		new SalesProductWebService(valid_url);
		new SalesOrderWebService(valid_url);
		new DateWebservice(valid_url,CustomerListActivity.this);
		selectedArea = SOTDatabase.getAreaCode();
		if (selectedArea.matches("")) {
			ed_areacode.setText("");
		} else {
			ed_areacode.setText(selectedArea);
		}
		
		select_van = SOTDatabase.getVandriver();
		
		if (select_van.matches("")) {
			ed_vancode.setText("");
		} else {
			ed_vancode.setText(select_van);
		}

		CustomersyncCall customerservice = new CustomersyncCall();
		customerservice.execute();

		actv_search.setThreshold(1);// will start working from first character

		customer_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				
				TextView Cust_Code = (TextView) v.findViewById(R.id.in_cust_code);

				String valCustCode = Cust_Code.getText().toString();
				Log.d("valCustCode", valCustCode);
				SalesOrderSetGet.setCustomercode(valCustCode);
				ConvertToSetterGetter.setDoNo("");
				ConvertToSetterGetter.setSoNo("");
				ConvertToSetterGetter.setEdit_do_no("");
				Intent in = new Intent(CustomerListActivity.this, AddCustomer.class);
				in.putExtra("CustCode", valCustCode);
				startActivity(in);
//				CustomerListActivity.this.finish();
				
			}
		});

		registerForContextMenu(customer_list);

		new_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CustomerListActivity.this,
						AddCustomer.class);
				startActivity(i);
//				CustomerListActivity.this.finish();
			}
		});

		bt_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				new CustomerSearchAsyncCall().execute();
			}
		});

		ed_custcode.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				System.out.println("Text [" + s + "]");
				Adapter.getFilter().filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() > 0) {
					actv_search.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							0, 0);
				} else {
					actv_search.setCompoundDrawablesWithIntrinsicBounds(
							drawable.search, 0, 0, 0);
				}
			}
		});

		ed_vancode.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
				ed_vancode) {
			@Override
			public boolean onDrawableClick() {
				ArrayList<HashMap<String, String>> vanArrHm = new ArrayList<HashMap<String, String>>();
				vanArrHm.clear();
				vanArrHm = SOTDatabase.getVan();
				if (vanArrHm.isEmpty()) {
					Log.d("al_vancode", "--->"+ vanlist.toString());
					new Customer(CustomerListActivity.this, ed_vancode, ed_vanname, vanlist, "Van");

				} else {
					new Customer(CustomerListActivity.this, ed_vancode, ed_vanname, vanArrHm, "Van");
					Log.d("vanArrHm", "--->"+ vanArrHm.size());
					Log.d("vanArrHm", "--->"+ vanArrHm);

				}
				return true;

			}

		});
		
		// actv_search
		// .setOnEditorActionListener(new TextView.OnEditorActionListener() {
		// @Override
		// public boolean onEditorAction(TextView v, int actionId,
		// KeyEvent event) {
		// if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		//
		// String auto_Text = actv_search.getText().toString();
		// Log.d("auto_Text", auto_Text);
		// if (!auto_Text.matches("")) {
		// new_add.setVisibility(View.VISIBLE);
		//
		// Adapter.getFilter().filter(auto_Text);
		//
		// customer_Title.setVisibility(View.VISIBLE);
		// actv_search.setVisibility(View.GONE);
		// inputMethodManager.hideSoftInputFromWindow(
		// actv_search.getWindowToken(), 0);
		// } else {
		// new_add.setVisibility(View.GONE);
		//
		// Adapter.getFilter().filter(auto_Text);
		// }
		// return true;
		// }
		// return false;
		// }
		// });
		// ed_custcode
		// .setOnTouchListener(new
		// DrawableClickListener.RightDrawableClickListener(
		// ed_custcode) {
		// @Override
		// public boolean onDrawableClick() {
		// // new AsyncCallWSCustomer().execute();
		// Log.d("al", "test------------>" + al_cust);
		// new Customer(CustomerListActivity.this, ed_custcode,
		// ed_custname, al_cust);
		// return true;
		//
		// }
		//
		// });

		ed_areacode
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						ed_areacode) {
					@Override
					public boolean onDrawableClick() {
						// new AsyncCallWSCustomer().execute();
						
						areaArrHm = SOTDatabase.getArea();
						if (areaArrHm.isEmpty()) {
							new Customer(CustomerListActivity.this,
									ed_areacode, ed_description, al_areacode,"Area");

							Log.d("al_areacode", "al_areacode------------>"
									+ al_areacode.size());
						} else {
							new Customer(CustomerListActivity.this,
									ed_areacode, ed_description, areaArrHm,"Area");
							Log.d("areaArrHm", "areaArrHm------------>"
									+ areaArrHm.size());
							Log.d("areaArrHm", "areaArrHm------------>"
									+ areaArrHm);

						}
						return true;

					}

				});

		product_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (table_layout.getVisibility() == View.GONE) {
					table_layout.setVisibility(View.VISIBLE);
				} else if (table_layout.getVisibility() == View.VISIBLE) {
					table_layout.setVisibility(View.GONE);
				}
			}
		});

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

	@Override
	public void onLocationChanged(Location location) {
//		location_txt.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());

		Log.d("LatitudeandLongitude","-->"+location.getLatitude()+"--"+location.getLongitude());
		String address="";
		try {
			Geocoder geocoder = new Geocoder(CustomerListActivity.this, Locale.getDefault());

			List<Address> addresses  = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);

			ArrayList<String> addressFragments = new ArrayList();

			for(int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {

				addressFragments.add(addresses.get(0).getAddressLine(i));
			}
			for(int j=0;j<addressFragments.size();j++){
				address =addressFragments.get(j);

			}
			Log.d("ListOfAddressCheck","-->"+address);

			setLatitude = location.getLatitude();
			setLongitude =location.getLongitude();

			location_txt.setText(address);


//			location_txt.setText(" ");

//			String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
			String city = addresses.get(0).getLocality();
			String state = addresses.get(0).getAdminArea();
			String country = addresses.get(0).getCountryName();
			String postalCode = addresses.get(0).getPostalCode();
			String knownName = addresses.get(0).getFeatureName();
//			location_txt.setText(address+city+country+postalCode);

		}catch(Exception e)
		{

		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(CustomerListActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
	}

	private class CustomersyncCall extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

			CustomerListArray.clear();
			Customer_Name_List.clear();
			al_cust.clear();
			al_areacode.clear();
			vanlist.clear();
			
			customer_jsonString="";
			areacode_jsonString="";

			// Customer_AreaCode_List.clear();
			loadprogress();
			dialogStatus="";
			dialogStatus = checkInternetStatus();
			boolean check= isNetworkConnected();
			if(check == false){
				finish();
			}

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

			customerhashValue.put("CompanyCode", cmpnyCode);
			customerhashValue.put("CustomerCode", "");
			customerhashValue.put("NeedOutstandingAmount", "");
			customerhashValue.put("AreaCode", selectedArea);
			customerhashValue.put("VanCode", select_van);
			
			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					if (dialogStatus.matches("true")) { // temp offline
						customer_jsonString = OfflineDatabase
								.getCustomersList(customerhashValue);
						String areacode = "";
						areacode_jsonString = OfflineDatabase.getArea(areacode);
					} else {
						Log.d("CheckOffline Alert -->", "False");
						finish();
					}

				} else {// Online
					customer_jsonString = WebServiceClass.parameterService(customerhashValue, "fncGetCustomerForSearch");
					areacode_jsonString = WebServiceClass.URLService("fncGetArea");
					vanlist = SalesOrderWebService.getAllVan("fncGetVan");				
				}

			} else if (onlineMode.matches("False")) { // permt Offline
				
				customer_jsonString = OfflineDatabase.getCustomersList(customerhashValue);
				String areacode = "";
				areacode_jsonString = OfflineDatabase.getArea(areacode);
			}
			
			try {

				customer_jsonResponse = new JSONObject(customer_jsonString);
				customer_jsonMainNode = customer_jsonResponse.optJSONArray("JsonArray");

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
						/*
						 * String cust_areacode =
						 * jsonChildNode.optString("AreaCode") .toString();
						 */

						SOTdetailsGetSet cust_set = new SOTdetailsGetSet();
						cust_set.setCust_Code(cust_code);
						cust_set.setCust_Name(cust_Name);
						// cust_set.setCust_AreaCode(cust_areacode);
						CustomerListArray.add(cust_set);
						// Customer_AreaCode_List.add(cust_areacode);
						Customer_Name_List.add(cust_Name);
						HashMap<String, String> customerhm = new HashMap<String, String>();
						customerhm.put(cust_code, cust_Name);
						al_cust.add(customerhm);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				

			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				areacode_jsonResponse = new JSONObject(areacode_jsonString);
				area_jsonMainNode = areacode_jsonResponse.optJSONArray("JsonArray");
			/******* Fetch node values **********/

			int lengthJsonArr1 = area_jsonMainNode.length();
			for (int j = 0; j < lengthJsonArr1; j++) {

				JSONObject jsonChildNode1;
				try {

					jsonChildNode1 = area_jsonMainNode.getJSONObject(j);

					String area_Code = jsonChildNode1.optString("Code")
							.toString();
					String area_Description = jsonChildNode1.optString(
							"Description").toString();

					HashMap<String, String> customerhm = new HashMap<String, String>();
					customerhm.put(area_Code, area_Description);
					al_areacode.add(customerhm);

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
			
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("Customerlist","success");

			auto_adapter = new ArrayAdapter<String>(CustomerListActivity.this,
					android.R.layout.select_dialog_item, Customer_Name_List);
			Adapter = new CustomerListCustomAdapter(CustomerListActivity.this,
					CustomerListArray);

			customer_list.setAdapter(Adapter);
			Adapter.notifyDataSetChanged();
			actv_search.setAdapter(auto_adapter);

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(customerList_layout, true);
		}
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
	}

	private class CustomerSearchAsyncCall extends AsyncTask<Void, Void, Void> {

		String area="",edcustcode;
		@Override
		protected void onPreExecute() {
			customerhashValue.clear();
			CustomerListArray.clear();
			loadprogress();
			dialogStatus="";
			area = ed_areacode.getText().toString();
			edcustcode=ed_custcode.getText().toString();
			dialogStatus = checkInternetStatus();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();


			customerhashValue.put("CompanyCode", cmpnyCode);
			customerhashValue.put("CustomerCode", edcustcode);
			customerhashValue.put("NeedOutstandingAmount", "");
			customerhashValue.put("AreaCode", area);
			customerhashValue.put("VanCode", select_van);
			
			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					if (dialogStatus.matches("true")) { // temp offline
						customer_jsonString = OfflineDatabase
								.getCustomersList(customerhashValue);
					} else {
						Log.d("CheckOffline Alert -->", "False");
						finish();
					}
				} else {// Online
					customer_jsonString = WebServiceClass.parameterService(
							customerhashValue, "fncGetCustomerForSearch");
				}
			} else if (onlineMode.matches("False")) { // permt Offline
				customer_jsonString = OfflineDatabase
						.getCustomersList(customerhashValue);
			}
			
			

			try {

				customer_jsonResponse = new JSONObject(customer_jsonString);
				customer_jsonMainNode = customer_jsonResponse
						.optJSONArray("JsonArray");

			} catch (JSONException e) {
				e.printStackTrace();
			}

			int lengthJsonArr = customer_jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode;
				try {

					jsonChildNode = customer_jsonMainNode.getJSONObject(i);
					String cust_code = jsonChildNode.optString("CustomerCode")
							.toString();
					String cust_Name = jsonChildNode.optString("CustomerName")
							.toString();

					SOTdetailsGetSet cust_set = new SOTdetailsGetSet();
					cust_set.setCust_Code(cust_code);
					cust_set.setCust_Name(cust_Name);
					CustomerListArray.add(cust_set);

				} catch (JSONException e) {

					e.printStackTrace();
				}
				/******* Fetch node values **********/

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("Customerlt", CustomerListArray.toString());

			Adapter = new CustomerListCustomAdapter(CustomerListActivity.this,
					CustomerListArray);
			customer_list.setAdapter(Adapter);
			if (CustomerListArray.isEmpty()) {
				Toast.makeText(CustomerListActivity.this, "No matches found",
						Toast.LENGTH_SHORT).show();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(customerList_layout, true);
		}
	}

	public class CustomerListCustomAdapter extends BaseAdapter implements
			Filterable , SectionIndexer {

		private ArrayList<SOTdetailsGetSet> listarray = new ArrayList<SOTdetailsGetSet>();
		private ArrayList<SOTdetailsGetSet> mOriginalValues = new ArrayList<SOTdetailsGetSet>();
		LayoutInflater mInflater;
		CustomHolder holder = new CustomHolder();
		SOTdetailsGetSet user;
		private Filter sampleFilter;
		private String sections = "abcdefghilmnopqrstuvz";
		public CustomerListCustomAdapter(Context context,
				ArrayList<SOTdetailsGetSet> productsList) {
			listarray.clear();
			mOriginalValues.clear();
			this.listarray = productsList;
			this.mOriginalValues = productsList;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return listarray.size();
		}

		@Override
		public SOTdetailsGetSet getItem(int position) {
			return listarray.get(position);
		}

		@Override
		public long getItemId(int position) {
			return listarray.get(position).hashCode();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View row = convertView;
			holder = null;

			if (row == null) {
				row = mInflater.inflate(R.layout.customer_list_item, null);
				holder = new CustomHolder();
				holder.custCode = (TextView) row
						.findViewById(R.id.in_cust_code);
				holder.custName = (TextView) row
						.findViewById(R.id.in_cust_name);
				row.setTag(holder);
			} else {
				holder = (CustomHolder) row.getTag();
			}

			user = getItem(position);
			holder.custCode.setText(user.getCust_Code());
			holder.custName.setText(user.getCust_Name());

			holder.custName.setTag(position);

			if (position % 2 == 0) {

//				row.setBackgroundResource(drawable.list_item_even_bg);
				holder.custCode.setTextColor(Color.parseColor("#035994"));
				holder.custName.setTextColor(Color.parseColor("#035994"));

			} else {

//				row.setBackgroundResource(drawable.list_item_odd_bg);
				holder.custCode.setTextColor(Color.parseColor("#646464"));
				holder.custName.setTextColor(Color.parseColor("#646464"));
			}

			return row;
		}

		@Override
		public int getPositionForSection(int section) {
			Log.d("ListView", "Get position for section");
			for (int i=0; i < this.getCount(); i++) {
//				String itemCode = this.getItem(i).getCust_Code();
				String itemName = this.getItem(i).getCust_Name();
				if (itemName.charAt(0) == sections.charAt(section))
					return i;
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int arg0) {
			Log.d("ListView", "Get section");
			return 0;
		}

		@Override
		public Object[] getSections() {
			Log.d("ListView", "Get sections");
			String[] sectionsArr = new String[sections.length()];
			for (int i=0; i < sections.length(); i++)
				sectionsArr[i] = "" + sections.charAt(i);

			return sectionsArr;

		}

		final class CustomHolder {

			TextView custCode;
			TextView custName;

		}

		public void resetData() {
			listarray = mOriginalValues;
		}

		@Override
		public Filter getFilter() {
			if (sampleFilter == null)
				sampleFilter = new SampleFilter();

			return sampleFilter;
		}

		private class SampleFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				// We implement here the filter logic
				if (constraint == null || constraint.length() == 0) {
					// No filter implemented we return all the list
					results.values = mOriginalValues;
					results.count = mOriginalValues.size();
				} else {
					// We perform filtering operation
					ArrayList<SOTdetailsGetSet> FilteredArrList = new ArrayList<SOTdetailsGetSet>();
					constraint = constraint.toString().toLowerCase();
					for (int i = 0; i < mOriginalValues.size(); i++) {
						String data = mOriginalValues.get(i).getCust_Name();
						String data1 = mOriginalValues.get(i).getCust_Code();
						if (data.toLowerCase().contains(constraint.toString())
								|| data1.toLowerCase().contains(
										constraint.toString())) {
							FilteredArrList.add(new SOTdetailsGetSet(
									mOriginalValues.get(i).getCust_Code(),
									mOriginalValues.get(i).getCust_Name()));
						}

					}
					// set the Filtered result to return
					results.count = FilteredArrList.size();
					results.values = FilteredArrList;

				}
				return results;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				if (results.count == 0) {
					listarray = (ArrayList<SOTdetailsGetSet>) results.values;
					notifyDataSetChanged();
				} else {
					listarray = (ArrayList<SOTdetailsGetSet>) results.values;
					notifyDataSetChanged();
				}

			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		SOTdetailsGetSet so = Adapter.getItem(info.position);

		cust_id = so.getCust_Code().toString();
		custName = so.getCust_Name().toString();
		menu.setHeaderTitle(cust_id);
		SalesOrderSetGet.setCustomercode(cust_id);
		ConvertToSetterGetter.setEdit_do_no("");
		menu.add(0, v.getId(), 0, "Edit");

		salesOrderStr = FormSetterGetter.getSalesOrder();
		deliveryOrderStr = FormSetterGetter.getDeliveryOrder();
		consignmentStr=FormSetterGetter.getConsignment();
		invoiceStr = FormSetterGetter.getInvoice();
		receiptsStr = FormSetterGetter.getReceipts();
		
		
		if (offlineLayout.getVisibility() == View.GONE) {
			menu.add(0, v.getId(), 0, "Location");
			menu.add(0, v.getId(), 0, "Outlet/Delivery Address");
			
			if (salesOrderStr.matches("Sales Order")) {			
					menu.add(0, v.getId(), 0, "Sales Order");
			}
			if (deliveryOrderStr.matches("Delivery Order")) {	
					menu.add(0, v.getId(), 0, "Delivery Order");
			}
			if (consignmentStr.matches("Consignment")) {
				menu.add(0, v.getId(), 0, "Consignment");
			}
			if (invoiceStr.matches("Invoice")) {
					menu.add(0, v.getId(), 0, "Invoice");
			}
			if (receiptsStr.matches("Receipts")) {
					menu.add(0, v.getId(), 0, "Cash Collection");
			}
			if(SalesOrderSetGet.getShortCode().matches("HEZOM")) {
				menu.add(0, v.getId(), 0, "Visited");
			}
			
		} else {
			
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		ConvertToSetterGetter.setDoNo("");
		ConvertToSetterGetter.setSoNo("");
		ConvertToSetterGetter.setEdit_do_no("");

		SOTDatabase.deleteallbatch();
		
		if (item.getTitle() == "Edit") {
			Intent in = new Intent(CustomerListActivity.this, AddCustomer.class);
			in.putExtra("CustCode", cust_id);
			startActivity(in);
			CustomerListActivity.this.finish();
		}else
		if (item.getTitle() == "Location") {	
			GetLocation task = new GetLocation();
			task.execute();
			
		}else if (item.getTitle() == "Outlet/Delivery Address") {
		
			Intent in = new Intent(CustomerListActivity.this, CustomerAddress.class);
			in.putExtra("CustomerCode", cust_id);
			in.putExtra("CustomerName", custName);
			startActivity(in);
//			CustomerListActivity.this.finish();
			
		} else if (item.getTitle() == "Sales Order") {

			intentString = "SalesOrder";
			new GetSalesCustomer().execute();

		} else if (item.getTitle() == "Delivery Order") {
			SOTDatabase.deleteImage();
			SOTDatabase.deleteBarcode();
			SOTDatabase.deleteSODetailQuantity();
			intentString = "DeliveryOrder";
			new GetSalesCustomer().execute();

		} else if (item.getTitle() == "Invoice") {

			SalesOrderSetGet.setSoRemarks("");
			SalesOrderSetGet.setSoAdditionalInfo("");
			SOTDatabase.deleteImage();
			SOTDatabase.deleteBarcode();
			ConvertToSetterGetter.setEdit_inv_no("");

			intentString = "Invoice";

			new GetSalesCustomer().execute();
		} else if (item.getTitle() == "Cash Collection") {
			Intent i = new Intent(CustomerListActivity.this,
					InvoiceCashCollection.class);
			i.putExtra("customerCode", cust_id);
			startActivity(i);
			SalesOrderSetGet.setHeader_flag("CustomerHeader");
			CustomerListActivity.this.finish();

		}else if (item.getTitle() == "Consignment") {
			SOTDatabase.deleteImage();
			SOTDatabase.deleteBarcode();
			SOTDatabase.deleteSODetailQuantity();
			intentString = "Consignment";
			new GetSalesCustomer().execute();

		} else if(item.getTitle() == "Visited"){
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			alertVisitedDialog(cust_id);
		}else {
			return false;
		}
		return true;
	}

	private void alertVisitedDialog(final String cust_id) {

		Log.d("Customer_id","-->"+cust_id);



		final Dialog dialog = new Dialog(CustomerListActivity.this);

		dialog.setContentView(R.layout.activity_alert);
		dialog.setTitle("Visited Dialog");
		remarks = (EditText) dialog.findViewById(R.id.editTextRemarks);
		Button close = (Button) dialog.findViewById(R.id.close_ic);
		signature = (ImageView) dialog.findViewById(R.id.sm_sign_iv);
		cam_image =(ImageView) dialog.findViewById(R.id.prod_photo);
		camera =(ImageView)dialog.findViewById(R.id.sm_camera_iv);
		sig_image =(ImageView)dialog.findViewById(R.id.sm_signature);
		location =(ImageView) dialog .findViewById(R.id.sm_loc_iv);
		location_txt =(EditText) dialog.findViewById(R.id.sm_location);

		clearView();

//		location_txt.setText(address1);
		if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

		}

		getLocationcheck();



		Button save=(Button) dialog.findViewById(R.id.deli_addProduct);

		if (product_img != null && !product_img.isEmpty()) {
			Log.d("product_imgvalue", product_img);
			cam_image.setImageDrawable(null);

//			try {
//
//				mCurrentgetPath = Product.getPath();
//
//				byte[] encodePhotoByte = Base64.decode(product_img, Base64.DEFAULT);
//				Bitmap photo = BitmapFactory.decodeByteArray(encodePhotoByte, 0,
//						encodePhotoByte.length);
//				//photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
//				ByteArrayOutputStream stream = new ByteArrayOutputStream();
//				photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
//
//				cam_image.setImageBitmap(photo);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		} else {

		}

		if (signature_img != null && !signature_img.isEmpty()) {
			Log.d("signature_imgValue", signature_img);

			sig_image.setImageDrawable(null);

//			try {
//				byte[] encodeByte = Base64
//						.decode(signature_img, Base64.DEFAULT);
//
//				Bitmap photo = BitmapFactory.decodeByteArray(encodeByte, 0,
//						encodeByte.length);
//				photo = Bitmap.createScaledBitmap(photo, 240, 80, true);
//				sig_image.setImageBitmap(photo);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

		} else {

		}

		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String remark =remarks.getText().toString();

				signature_img = SOTDatabase.getSignatureImage();

				mCurrentgetPath = Product.getPath();


				if (mCurrentgetPath != null) {
					product_img = ImagePathToBase64Str(mCurrentgetPath);
				}


				if (location_txt.getText().length() != 0
						&& location_txt.getText().toString() != "") {


					address1 = location_txt.getText().toString();
					Log.d("address1", "-->" + address1 + "-->"+location_txt.getText().length() );

//					if ((signature_img != null
//							&& signature_img != "") &&  (product_img != null
//							&& product_img != "")) {

					Log.d("signature_img", ":" + signature_img);
					Log.d("product_img", ":" + product_img);

					String signatureResult = SOTSummaryWebService
							.saveSignatureImage(cust_id, ""
											+ setLatitude, "" + setLongitude,
									signature_img, product_img,
									"fncSaveInvoiceImages", "VI", address1,
									"" + "^" + remark);

					Log.d("signatureResult-->", "" + address1 + "-->" + address2);


					Log.d("fncSaveInvoiceImages", "" + cust_id + " "
							+ setLatitude + " " + setLongitude + " VI"
							+ address1 + address2 + "signature_img "
							+ signature_img + "product_img " + product_img);

					if (!signatureResult.matches("")) {
						Log.d("Cap Image", "Saved");
						deleteDirectory(new File(Environment.getExternalStorageDirectory() + "/" + "SFA", "Image"));
						Log.d("Cap Image", "Saved");
						Toast.makeText(CustomerListActivity.this, "Saved Successfully",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();

					} else {
						Log.d("Cap Image", "Not Saved");
					}

				} else {
					Toast.makeText(getApplicationContext(),
							"Waiting For Location!!!", Toast.LENGTH_SHORT)
							.show();
					getLocationcheck();
				}


			}
		});

		signature.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(CustomerListActivity.this,
						CaptureSignature.class);
				startActivityForResult(i, SIGNATURE_ACTIVITY);
			}
		});

		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CameraAction(PICK_FROM_CAMERA);
			}
		});




		close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});

		dialog.show();
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

						sig_image.setImageBitmap(bitmap);

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] bitMapData = stream.toByteArray();
						String signature_image = Base64.encodeToString(
								bitMapData, Base64.DEFAULT);
						SOTDatabase.init(CustomerListActivity.this);
						Cursor ImgCursor = SOTDatabase.getImageCursor();
						if (ImgCursor.getCount() > 0) {
							String camera_image = SOTDatabase.getProductImage();
							SOTDatabase.updateImage(1, signature_image,
									camera_image);
						} else {
							SOTDatabase.storeImage(1, signature_image, "");
						}

						Log.d("Signature_Image", "Sig" + signature_image);
					}
				}
				break;
		}
//		}
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


	private void handleCameraPhoto(int actionCode) {
		Log.d("mCurrentPhotoPath", "mCurrentPhotoPath-->"+mCurrentPhotoPath);
		if (mCurrentPhotoPath != null) {
			switch (actionCode) {
				case PICK_FROM_CAMERA: {
					Product.setPath(mCurrentPhotoPath);
					decodeImagePathFile(mCurrentPhotoPath,cam_image);
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
			SOTDatabase.init(CustomerListActivity.this);

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

	private void getLocationcheck() {
		try {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}


	private void clearView() {
		remarks.setText("");
		location_txt.setText("");
		cam_image.setImageDrawable(null);
		sig_image.setImageDrawable(null);
	}

	private class GetLocation extends AsyncTask<Void, Void, Void> {
		 HashMap<String, String> hm= new HashMap<String, String>();
		 HashMap<String, String> hash= new HashMap<String, String>();
		 JSONArray jsonarray = null;
			@Override
			protected void onPreExecute() {
				hm.clear();
				hash.clear();
				loadprogress();
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				try {
				String cmpnyCode = SupplierSetterGetter.getCompanyCode();
				
				hm.put("CompanyCode", cmpnyCode);
				hm.put("CustomerCode", cust_id);
				
				
				jsonarray = WebServiceClass.parameterWebservice(hm, "fncGetCustomer");
				
				if(jsonarray.length()>0){
				
					for(int i=0 ; i<jsonarray.length(); i++){
						JSONObject jsonobject;
						
							jsonobject = jsonarray.getJSONObject(i);
						
						String CustomerCode = jsonobject.optString("CustomerCode")
								.toString();
						String CustomerName = jsonobject.optString("CustomerName")
								.toString();						
						String Latitude = jsonobject.optString("Latitude")
								.toString();					
						String Longitude = jsonobject.optString("Longitude")
								.toString();
						
						hash.put("CustomerCode", CustomerCode);
						hash.put("CustomerName", CustomerName);
						hash.put("Latitude", Latitude);
						hash.put("Longitude", Longitude);
						
					}
					
				}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				
				Intent in = new Intent(CustomerListActivity.this, MapActivity.class);
				in.putExtra("CustomerLocation", hash);
				startActivity(in);
//				CustomerListActivity.this.finish();
				
				
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(customerList_layout, true);
			}
		}
	 
	
	private class GetSalesCustomer extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			cstmrgrpcdal.clear();
			alclcrrncy.clear();
			loadprogress();
			dialogStatus="";
			dialogStatus = checkInternetStatus();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				
				SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
				String finalDate = timeFormat.format(new Date());
				System.out.println(finalDate);
				Log.d("Customer values", "" + cstmrgrpcdal);
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						if (dialogStatus.matches("true")) { // temp offline
							serverdate = finalDate;
							currencyCode = OfflineSalesOrderWebService.generalSettingsServiceOffline();
							alclcrrncy = OfflineSalesOrderWebService.getCurrencyValuesOffline(currencyCode);
							cstmrgrpcdal = offlineDatabase.getCustGroupCode(cust_id);
							offlineDatabase.getCustomerTax(cust_id);
						} else {
							Log.d("CheckOffline Alert -->", "False");
							finish();
						}
					} else {// Online
						serverdate = DateWebservice.getDateService("fncGetServerDate");
						currencyCode = SalesOrderWebService.generalSettingsService("fncGetGeneralSettings");
						alclcrrncy = SalesOrderWebService.getCurrencyValues("fncGetCurrency", currencyCode);
						cstmrgrpcdal = SalesOrderWebService.getCustGroupCode(cust_id, "fncGetCustomer");
						SalesOrderWebService.getCustomerTax(cust_id,"fncGetCustomer");
					}
				} else if (onlineMode.matches("False")) { // permt Offline
					serverdate = finalDate;
					currencyCode = OfflineSalesOrderWebService.generalSettingsServiceOffline();
					alclcrrncy = OfflineSalesOrderWebService.getCurrencyValuesOffline(currencyCode);
					cstmrgrpcdal = offlineDatabase.getCustGroupCode(cust_id);
					offlineDatabase.getCustomerTax(cust_id);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(customerList_layout, true);

			SOTDatabase.init(CustomerListActivity.this);
			SOTDatabase.deleteAllProduct();
			SOTDatabase.deleteBillDisc();
			SOTDatabase.deleteImage();
			SOTDatabase.deleteBarcode();

			currencyName = alclcrrncy.get(0);
			currencyRate = alclcrrncy.get(1);

			SalesOrderSetGet.setSaleorderdate(serverdate);
			SalesOrderSetGet.setDeliverydate(serverdate);
			SalesOrderSetGet.setCustomercode(cust_id);
			SalesOrderSetGet.setCustomername(custName);
			SalesOrderSetGet.setCurrencyrate(currencyRate);
			SalesOrderSetGet.setCurrencycode(currencyCode);
			SalesOrderSetGet.setCurrencyname(currencyName);
			SalesOrderSetGet.setRemarks("");

			SalesOrderSetGet.setSuppliercode(cstmrgrpcdal.get(0));
			SalesOrderSetGet.setSuppliergroupcode(cstmrgrpcdal.get(1));

			if (intentString.matches("SalesOrder")) {

				Intent i = new Intent(CustomerListActivity.this,
						SalesAddProduct.class);
				i.putExtra("SalesOrder", intentString);
				startActivity(i);
				SalesOrderSetGet.setHeader_flag("CustomerHeader");
				CustomerListActivity.this.finish();

			} else if (intentString.matches("Invoice")) {

				Intent i = new Intent(CustomerListActivity.this,
						InvoiceAddProduct.class);
				i.putExtra("Invoice", intentString);
				startActivity(i);
				SalesOrderSetGet.setHeader_flag("CustomerHeader");
				CustomerListActivity.this.finish();
			} else if (intentString.matches("DeliveryOrder")) {

				Intent i = new Intent(CustomerListActivity.this,
						DeliveryAddProduct.class);
				i.putExtra("DeliveryOrder", intentString);
				startActivity(i);
				SalesOrderSetGet.setHeader_flag("CustomerHeader");
				CustomerListActivity.this.finish();
			}else if (intentString.matches("Consignment")) {

				Intent i = new Intent(CustomerListActivity.this,
						ConsignmentAddProduct.class);
				i.putExtra("Consignment", intentString);
				startActivity(i);
				SalesOrderSetGet.setHeader_flag("CustomerHeader");
				CustomerListActivity.this.finish();
			}
		}
	}
	
	// offline
	private String checkInternetStatus() {
		checkOffline = OfflineCommon.isConnected(CustomerListActivity.this);
		String internetStatus = "";
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

		String deviceId = RowItem.getDeviceID();
		Log.d("device id", "dev " + deviceId);
		return internetStatus;
	}

	
	private void loadprogress(){
		spinnerLayout = new LinearLayout(CustomerListActivity.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(customerList_layout, false);
		progressBar = new ProgressBar(CustomerListActivity.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		spinnerLayout.addView(progressBar);
	}

	@Override
	public void onListItemClick(String item) {
		// TODO Auto-generated method stub
		menu.toggle();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(CustomerListActivity.this, LandingActivity.class);

		startActivity(i);
		CustomerListActivity.this.finish();
	}

}
