package com.winapp.sotdetails;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.fwms.DrawableClickListener;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.theappguruz.imagezoom.ImageViewTouch;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.AddProductSetterGetter;
import com.winapp.fwms.AndroidTabLayoutActivity;
import com.winapp.fwms.ClearFieldSetterGetter;
import com.winapp.fwms.CustomAlertAdapterProd;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.NewProductWebService;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.XMLParser;
import com.winapp.model.Barcode;
import com.winapp.model.LocationGetSet;
import com.winapp.printer.Printer;
import com.winapp.printer.UIHelper;
import com.winapp.sot.InvoiceSummary;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SOTSummaryWebService;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.sot.StockRequestHeader;
import com.winapp.sot.WebServiceClass;
import com.winapp.util.XMLAccessTask;
import com.winapp.util.XMLAccessTask.CallbackInterface;
import com.winapp.util.XMLAccessTask.ErrorType;

public class ProductStockActivity extends SherlockFragmentActivity implements
		OnItemClickListener, SlideMenuFragment.MenuClickInterFace {

	LinearLayout productstock_layout;
	LinearLayout pagetitle_layout, search_btn_layout, auto_search_layout,
			header_layout;
	TextView product_Title, mBalanceTxtV, mAllTxtV,login_lctn;
	ListView product_list;
	AutoCompleteTextView actv_search;
	int textlength = 0;
	ArrayAdapter<String> auto_adapter;
	ArrayList<String> Product_Name_List = new ArrayList<String>();
	ArrayList<String> editBarcodeArr = new ArrayList<String>();
	ArrayList<Barcode> barcodeArray = new ArrayList<Barcode>();
	ProductCustomAdapter Adapter;
	ArrayList<ProductStockGetSet> ProductListArray, onlyBalanceProductArray, mProductStockArrList,
			productstockArr, closed_stock_arr;
	List<LocationGetSet> productStockList;
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> producthashValue = new HashMap<String, String>();
	HashMap<String, String> stockHashValue = new HashMap<String, String>();
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> searchResults;
	String product_stock_jsonString = null, stock_jsonString = null;
	JSONObject product_stock_jsonResponse, stock_jsonResponse;
	JSONArray product_stock_jsonMainNode, stock_jsonMainNode, json_main_Node;
	String LocationCode = "", valid_url = "", cmpnyCode = "", userId = "",
			saveResult = "";
	ImageButton newprodbutton, product_search, printIcon, mClose, save;
	private EditText editText, edprintDate, searchEdt;
	private AlertDialog myalertDialog = null;
	CustomAlertAdapterProd arrayAdapterProd;
	ArrayList<String> editProductArr = new ArrayList<String>();
	LinearLayout spinnerLayout;
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String PRODUCT_NAME = "ProductName";
	private static String webMethName = "fncGetProductForSearch";
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	ProgressBar progressBar;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int SELECT_PICTURE = 2;
	private static final int VIEW_IMAGE = 3;
	private static final int CROP_PIC = 4;
	private Uri picUri;
	private ArrayList<String> submenuArra;
	private HashMap<String, String> params;

	private ImageView img_progress;
	private Button bt_DialogAction1, bt_DialogAction2, bt_DialogAction3,
			bt_DialogAction4, mSearchBtn;
	private int divierId;
	private View divider;

	String keyProdCode, ProductCode, ProductName, WeightBarcodeStartsOn,
			WeightBarcodeEndsOn, HaveBatch, HaveExpiry, HaveMfgDate, weightFld,
			CategoryCode, SubCategoryCode, UOMCode, PcsPerCarton, cartonPrice,
			units, serverdatetime, appType = "", jsonString = null,
			prod_name = "", prod_code = "", currentdate, binDetail;
	private HashMap<String, String> hashValue;
	JSONObject jsonResponse;
	JSONArray jsonMainNode;
	SlidingMenu menu;
	private Cursor cursor;
	private UIHelper helper;
	private Calendar printerCalendar;
	boolean addProdRslt;
	private CheckBox mCheckBox;
	TextView listing_screen, customer_screen, addProduct_screen, summary_screen;
	LinearLayout customer_layout, row1, row2;
	String bar_productCode;
	RecyclerView loctn;
	LocationAdapter adapter;
	List<LocationGetSet> locationList, lctnList;
	HashMap<String, String> pStockLocatin;
	HorizontalScrollView hview;
	RecyclerView recyclerView;
	StockCustomAdapter adapter_recycler;
	Stockadapter adapter_stock;
	LinearLayout linear;
	List<LocationGetSet> lctn_fnl_list;
	Button closed_stock;
	ArrayList<String> soCloseStockArr;
	String summaryResult;
	boolean flag,screen = true,chooseTab = true;
	String lctnCode;
	TextView view1,view2,view,view3;
	//	private Uri selectedImageUri;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.productstock);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.productstock_title, null);
		product_Title = (TextView) customNav.findViewById(R.id.product_Title);
		actv_search = (AutoCompleteTextView) customNav
				.findViewById(R.id.auto_edit_search);
		product_Title.setText("Product Stock");
		newprodbutton = (ImageButton) customNav
				.findViewById(R.id.newprodbutton);
		mCheckBox = (CheckBox) findViewById(R.id.checkBox1);
		login_lctn = (TextView)customNav.findViewById(R.id.login_loctn);
		login_lctn.setVisibility(View.VISIBLE);
		lctnCode = SalesOrderSetGet.getLocationcode();
		login_lctn.setText(lctnCode);

		// ed_product = (ImageButton) customNav.findViewById(R.id.ed_product);
		// //unused
		product_search = (ImageButton) customNav.findViewById(R.id.search);
		printIcon = (ImageButton) customNav.findViewById(R.id.print_icon);
		mClose = (ImageButton) customNav.findViewById(R.id.close);
		getSupportActionBar().setCustomView(customNav);
		getSupportActionBar().setDisplayShowCustomEnabled(true);

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
		final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		helper = new UIHelper(ProductStockActivity.this);
		hashValue = new HashMap<String, String>();
		ProductListArray = new ArrayList<ProductStockGetSet>();
		productstockArr = new ArrayList<ProductStockGetSet>();
		onlyBalanceProductArray = new ArrayList<ProductStockGetSet>();
		mProductStockArrList = new ArrayList<>();
		closed_stock_arr = new ArrayList<>();
		printerCalendar = Calendar.getInstance();
		hashValue.clear();
		onlyBalanceProductArray.clear();
		Product_Name_List.clear();
		ProductListArray.clear();
		productstock_layout = (LinearLayout) findViewById(R.id.productstock_layout);
		mBalanceTxtV = (TextView) findViewById(R.id.balance);
		mAllTxtV = (TextView) findViewById(R.id.all);
		searchEdt = (EditText) findViewById(R.id.searchEdt);
		mSearchBtn = (Button) findViewById(R.id.searchBtns);
		row1 = (LinearLayout) findViewById(R.id.row1);
		row2 = (LinearLayout) findViewById(R.id.row2);
		hview = (HorizontalScrollView) findViewById(R.id.horizontalView);
		linear = (LinearLayout) findViewById(R.id.linear);
		recyclerView = (RecyclerView) findViewById(R.id.recycler);
		recyclerView.setHasFixedSize(true);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductStockActivity.this);
		recyclerView.setLayoutManager(linearLayoutManager);
		closed_stock = (Button) findViewById(R.id.searchcc);
		soCloseStockArr = new ArrayList<>();

		product_list = (ListView) findViewById(R.id.product_list);
		params = new HashMap<String, String>();
		registerForContextMenu(product_list);


		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen = (TextView) findViewById(R.id.sum_screen);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);
		/*loctn = (RecyclerView) findViewById(R.id.recyclerView);
		loctn.setHasFixedSize(true);
		loctn.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));*/

		customer_layout.setVisibility(View.GONE);
//		customer_screen.setVisibility(View.GONE);
		addProduct_screen.setVisibility(View.GONE);

		listing_screen.setText("Stock Balance");
		customer_screen.setText("Stock Products");
		summary_screen.setText("All Location Stock");

		listing_screen.setTextColor(Color.parseColor("#FFFFFF"));
		listing_screen.setBackgroundResource(drawable.tab_select_left);


		appType = LogOutSetGet.getApplicationType();

		addProdRslt = FormSetterGetter.isProductAdd();

		if (addProdRslt == true) {
			newprodbutton.setVisibility(View.VISIBLE);
			// ed_product.setVisibility(View.VISIBLE);
		} else if (addProdRslt == false) {
			newprodbutton.setVisibility(View.GONE);
			// ed_product.setVisibility(View.GONE);
		}

		LocationCode = SalesOrderSetGet.getLocationcode();
		userId = SupplierSetterGetter.getUsername();
		cmpnyCode = SupplierSetterGetter.getCompanyCode();
		Log.d("Location Code", "" + LocationCode);

		FWMSSettingsDatabase.init(ProductStockActivity.this);
		SOTDatabase.init(ProductStockActivity.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new WebServiceClass(valid_url);
		new NewProductWebService(valid_url);
		new SalesOrderWebService(valid_url);
		new DateWebservice(valid_url, ProductStockActivity.this);
		new SOTSummaryWebService(valid_url);



		ProductStockAsyncCall Productservice = new ProductStockAsyncCall();
		Productservice.execute();

		AsyncCallWSADDPRD task = new AsyncCallWSADDPRD();
		task.execute();

		new ShowLocations().execute();


		Log.d("onlyBalanceProductArray123","-->"+onlyBalanceProductArray.size());

		actv_search.setThreshold(1);

		row1.setVisibility(View.VISIBLE);
		row2.setVisibility(View.GONE);
		hview.setVisibility(View.GONE);

		mBalanceTxtV.setBackgroundResource(drawable.chart_selection);
		mBalanceTxtV.setTextColor(Color.parseColor("#ffffff"));
		mBalanceTxtV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBalanceTxtV.setBackgroundResource(drawable.chart_selection);
				mBalanceTxtV.setTextColor(Color.parseColor("#ffffff"));
				mAllTxtV.setBackgroundColor(Color.parseColor("#ffffff"));
				mAllTxtV.setTextColor(Color.parseColor("#015FA0"));
				Adapter = new ProductCustomAdapter(ProductStockActivity.this,
						onlyBalanceProductArray, "");
				product_list.setAdapter(Adapter);
				Adapter.notifyDataSetChanged();
			}
		});
		mAllTxtV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAllTxtV.setBackgroundResource(drawable.chart_selection);
				mAllTxtV.setTextColor(Color.parseColor("#ffffff"));
				mBalanceTxtV.setBackgroundColor(Color.parseColor("#ffffff"));
				mBalanceTxtV.setTextColor(Color.parseColor("#015FA0"));
				Adapter = new ProductCustomAdapter(ProductStockActivity.this,
						mProductStockArrList, "");
				product_list.setAdapter(Adapter);
				Adapter.notifyDataSetChanged();
			}
		});
		mSearchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("flagCheck","-->"+flag);
				if(flag == true){
					inputMethodManager.hideSoftInputFromWindow(
							actv_search.getWindowToken(), 0);
					String searchStr = searchEdt.getText().toString();
					Adapter.getFilter().filter(searchStr);
				}else{
					int orientation = getResources().getConfiguration().orientation;
					if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
						inputMethodManager.hideSoftInputFromWindow(
								actv_search.getWindowToken(), 0);
						String searchStr = searchEdt.getText().toString();
						adapter_stock.getFilter().filter(searchStr);
					}else{
						inputMethodManager.hideSoftInputFromWindow(
								actv_search.getWindowToken(), 0);
						String searchStr = searchEdt.getText().toString();
						adapter_recycler.getFilter().filter(searchStr);
					}

				}

			}
		});
		searchEdt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (searchEdt.getText().length() > 0) {

					searchEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_clear_btn, 0);
					searchEdt.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(searchEdt) {
						@Override
						public boolean onDrawableClick() {
							Log.d("TExtFilter","-->"+searchEdt.getText().toString());
								searchEdt.setText("");
								String searchStr = searchEdt.getText().toString();
							if(flag == true) {
								Adapter.getFilter().filter(searchStr);
							}else{
								int orientation = getResources().getConfiguration().orientation;
								if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
									adapter_stock.getFilter().filter(searchStr);
								}else{
									adapter_recycler.getFilter().filter(searchStr);
								}

							}
							return true;

						}

					});

				} else {
					searchEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				}
			}
		});

		product_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
									long arg3) {

				Log.e("Productstock", FormSetterGetter.getShowProductAnalysis());

				// TODO Auto-generated method stub
				TextView text = (TextView) v.findViewById(R.id.pro_code);
				TextView textName = (TextView) v.findViewById(R.id.productName);
				// TextView get_productCode = (TextView)
				// v.findViewById(R.id.productCode);
				String valProductCode = text.getText().toString();
				String valProductName = textName.getText().toString();
				Log.d("valProductCode->", valProductCode);

				if (!FormSetterGetter.getShowProductAnalysis().matches("")) {
					Intent in = new Intent(ProductStockActivity.this, ProductStockDetailActivity.class);
					in.putExtra("valProductCode", valProductCode);
					in.putExtra("ProductName", valProductName);
					in.putExtra("LocationCode", LocationCode);
					startActivity(in);
					ProductStockActivity.this.finish();
				} else {
					Toast.makeText(ProductStockActivity.this, valProductName, Toast.LENGTH_SHORT).show();
				}
			}
		});

		printIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SOTDatabase.deleteImage();
				cursor = FWMSSettingsDatabase.getPrinter();
				if (cursor.getCount() != 0) {
					// dialogPrinter();
					String mobileproductstockprint = SalesOrderSetGet
							.getMobileproductstockprint();
					if (!mobileproductstockprint.matches("")) {
						if (mobileproductstockprint.matches("1")) {
							dialogDate();
						} else if (mobileproductstockprint.matches("0")) {
							dialogPrinter();
						}

					}
				} else {
					Toast.makeText(ProductStockActivity.this,
							"Please Configure the printer", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		newprodbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddProductSetterGetter.setUpdate(false);
				LogOutSetGet.setAddProduct("ProductStock");
				ClearFieldSetterGetter.setClearProduct(true);
				Intent i = new Intent(ProductStockActivity.this,
						AndroidTabLayoutActivity.class);
				startActivity(i);
				ProductStockActivity.this.finish();
			}
		});


		actv_search
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
												  KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {

							String auto_Text = actv_search.getText().toString();

							if (product_Title.getVisibility() == View.VISIBLE) {

								newprodbutton.setVisibility(View.INVISIBLE);
								printIcon.setVisibility(View.INVISIBLE);
								product_Title.setVisibility(View.INVISIBLE);
								mClose.setVisibility(View.VISIBLE);
								actv_search.setVisibility(View.VISIBLE);
								actv_search.requestFocus();
								inputMethodManager.toggleSoftInputFromWindow(
										actv_search.getApplicationWindowToken(),
										InputMethodManager.SHOW_FORCED, 0);
								mCheckBox.setVisibility(View.VISIBLE);

							} else {
								inputMethodManager.hideSoftInputFromWindow(
										actv_search.getWindowToken(), 0);
								loadProduct();

								Adapter.getFilter().filter(auto_Text);

							}

							return true;
						}
						return false;
					}
				});

		actv_search.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {

				inputMethodManager.hideSoftInputFromWindow(
						actv_search.getWindowToken(), 0);
			}
		});

		actv_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				try {
					System.out.println("Text [" + s + "]");
					Adapter.getFilter().filter(s.toString());
					loadProduct();
				} catch (Exception e) {
					e.printStackTrace();
				}
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
							drawable.ic_search, 0, 0, 0);
				}
				if (actv_search.getText().length() == 0) {

					actv_search.setCompoundDrawablesWithIntrinsicBounds(
							drawable.ic_search, 0, 0, 0);
				} else {

					actv_search
							.setCompoundDrawablesWithIntrinsicBounds(
									drawable.ic_search, 0,
									R.mipmap.ic_clear_btn, 0);

					actv_search
							.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
									actv_search) {
								@Override
								public boolean onDrawableClick() {

									actv_search.setText("");
									return true;

								}

							});

				}
			}
		});
		mClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCheckBox.setVisibility(View.GONE);
				printIcon.setVisibility(View.VISIBLE);
				newprodbutton.setVisibility(View.VISIBLE);
				product_Title.setVisibility(View.VISIBLE);
				mClose.setVisibility(View.GONE);
				actv_search.setVisibility(View.GONE);
				inputMethodManager.hideSoftInputFromWindow(
						actv_search.getWindowToken(), 0);
			}
		});
		product_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String auto_Text = actv_search.getText().toString();

				if (product_Title.getVisibility() == View.VISIBLE) {

					newprodbutton.setVisibility(View.GONE);
					printIcon.setVisibility(View.GONE);
					product_Title.setVisibility(View.GONE);
					mClose.setVisibility(View.VISIBLE);
					actv_search.setVisibility(View.VISIBLE);
					actv_search.requestFocus();
					inputMethodManager.toggleSoftInputFromWindow(
							actv_search.getApplicationWindowToken(),
							InputMethodManager.SHOW_FORCED, 0);
					mCheckBox.setVisibility(View.VISIBLE);

				} else {
					inputMethodManager.hideSoftInputFromWindow(
							actv_search.getWindowToken(), 0);
					loadProduct();

					Adapter.getFilter().filter(auto_Text);

				}

			}
		});

		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
				summary_screen.setBackgroundResource(drawable.rounded_tab_right);

				customer_screen.setTextColor(Color.parseColor("#FFFFFF"));
				customer_screen.setBackgroundResource(drawable.rounded_tab_center);

				listing_screen.setTextColor(Color.parseColor("#FFFFFF"));
				listing_screen.setBackgroundResource(drawable.tab_select_left);

				Adapter = new ProductCustomAdapter(ProductStockActivity.this,
						onlyBalanceProductArray, "");
				product_list.setAdapter(Adapter);
				Adapter.notifyDataSetChanged();
				flag = true;
				screen = true;
				chooseTab = true;

				linear.setVisibility(View.VISIBLE);
				printIcon.setVisibility(View.VISIBLE);
				newprodbutton.setVisibility(View.VISIBLE);

				row1.setVisibility(View.VISIBLE);
				row2.setVisibility(View.GONE);
				hview.setVisibility(View.GONE);
				product_list.setVisibility(View.VISIBLE);
				recyclerView.setVisibility(View.GONE);

				String lctnCode = SalesOrderSetGet.getLocationcode();
				Log.d("lctnCodeCheck","-->"+lctnCode+"-->"+LocationGetSet.getIsMainLocation());

				if(onlyBalanceProductArray.size()==0){
					closed_stock.setVisibility(View.GONE);
				}else{
					if(LocationGetSet.getIsMainLocation().matches(lctnCode)){
						closed_stock.setVisibility(View.GONE);
					}else{
						closed_stock.setVisibility(View.VISIBLE);
					}

				}


			}
		});
		customer_screen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listing_screen.setTextColor(Color.parseColor("#FFFFFF"));
				listing_screen.setBackgroundResource(drawable.rounded_tab_left);

				customer_screen.setTextColor(Color.parseColor("#FFFFFF"));
				customer_screen.setBackgroundResource(drawable.tab_select);

				summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
				summary_screen.setBackgroundResource(drawable.rounded_tab_right);

				linear.setVisibility(View.VISIBLE);
				printIcon.setVisibility(View.VISIBLE);
				newprodbutton.setVisibility(View.VISIBLE);
				flag = true;
				screen = false;
				chooseTab = false;

				row1.setVisibility(View.VISIBLE);
				row2.setVisibility(View.GONE);
				hview.setVisibility(View.GONE);
				product_list.setVisibility(View.VISIBLE);
				recyclerView.setVisibility(View.GONE);

				Adapter = new ProductCustomAdapter(ProductStockActivity.this,
						mProductStockArrList, "");
				product_list.setAdapter(Adapter);
				Adapter.notifyDataSetChanged();
				closed_stock.setVisibility(View.GONE);
			}
		});

		summary_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				customer_screen.setTextColor(Color.parseColor("#FFFFFF"));
				customer_screen.setBackgroundResource(drawable.rounded_tab_center);

				listing_screen.setTextColor(Color.parseColor("#FFFFFF"));
				listing_screen.setBackgroundResource(drawable.rounded_tab_left);

				summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
				summary_screen.setBackgroundResource(drawable.tab_select_right);

//				linear.setVisibility(View.GONE);
				flag = false;
				screen = false;

				printIcon.setVisibility(View.INVISIBLE);
				newprodbutton.setVisibility(View.INVISIBLE);

				int orientation = getResources().getConfiguration().orientation;
				if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					// In landscape
					Log.d("orientation", "-->" + orientation);
					new ShowLocation().execute();
					new AsyncLocation().execute();
				} else {

					new ShowAllLocation().execute();
					new AsyncAllLocation().execute();
				}


				row1.setVisibility(View.GONE);
				row2.setVisibility(View.VISIBLE);
				hview.setVisibility(View.VISIBLE);
				product_list.setVisibility(View.GONE);
				recyclerView.setVisibility(View.VISIBLE);


				closed_stock.setVisibility(View.GONE);


//				adapter= new LocationAdapter(ProductStockActivity.this,locationList);
//				loctn.setAdapter(adapter);

				/*Adapter = new ProductCustomAdapter(ProductStockActivity.this,
						mProductStockArrList,"all");
				product_list.setAdapter(Adapter);
				Adapter.notifyDataSetChanged();*/


			}
		});

		closed_stock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closed_stock.setVisibility(View.GONE);
				Log.d("checkclosedArr", "-->" + closed_stock_arr.size());
				for (int i = 0; i < closed_stock_arr.size(); i++) {
//					String slno = closed_stock_arr.get(i).getSlno();
					String slno = String.valueOf(i+1);
					String pro_code = closed_stock_arr.get(i).getProduct_Code();
					String pro_name = closed_stock_arr.get(i).getProduct_Name();
					String qty = closed_stock_arr.get(i).getProduct_Quantity();
					int cQty = closed_stock_arr.get(i).getCartonqty();
					String lQty = closed_stock_arr.get(i).getLooseqty();
					String pcs = closed_stock_arr.get(i).getProduct_PcsPerCarton();
					String price = closed_stock_arr.get(i).getWholeSalePrice();
					Log.d("checkEntites", slno + "code:" + pro_code + "name:" + pro_name + "qty:" + qty);
					Log.d("CheckAdditional","--?>"+cQty+"-->"+lQty+"pcs:"+pcs+"-->"+price);

					SOTDatabase.storeCloseStock(slno, pro_code, pro_name,cQty,lQty,pcs,price,qty);

				}
//				new ShowLocations().execute();
				new AsyncCloseStock(locationList).execute();
			}
		});


	}

	/*public void startProgress(){
		spinnerLayout = new LinearLayout(ProductStockActivity.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(productstock_layout, false);
		progressBar = new ProgressBar(ProductStockActivity.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));

		spinnerLayout.addView(progressBar);
	}*/

	public void alertDialogSearchForEditProduct() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				ProductStockActivity.this);
		editText = new EditText(ProductStockActivity.this);
		final ListView listview = new ListView(ProductStockActivity.this);
		LinearLayout layout = new LinearLayout(ProductStockActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Product");
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

		arrayAdapterProd = new CustomAlertAdapterProd(
				ProductStockActivity.this, al);
		listview.setAdapter(arrayAdapterProd);
		listview.setOnItemClickListener(ProductStockActivity.this);

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
									int position, long arg3) {

				myalertDialog.dismiss();
				getArraylsit = arrayAdapterProd.getArrayList();

				HashMap<String, String> datavalue = getArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					keyProdCode = (String) mapEntry.getKey();

					AsyncCallWSEditProduct task = new AsyncCallWSEditProduct();
					task.execute();
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

				arrayAdapterProd = new CustomAlertAdapterProd(
						ProductStockActivity.this, searchResults);
				listview.setAdapter(arrayAdapterProd);
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

	public void progressBar() {
		spinnerLayout = new LinearLayout(ProductStockActivity.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(productstock_layout, false);
		progressBar = new ProgressBar(ProductStockActivity.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));

		spinnerLayout.addView(progressBar);
	}

	private class AsyncCallWSEditProduct extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			editProductArr.clear();
			/*
			 * spinnerLayout = new LinearLayout(ProductStockActivity.this);
			 * spinnerLayout.setGravity(Gravity.CENTER);
			 * addContentView(spinnerLayout, new LayoutParams(
			 * android.view.ViewGroup.LayoutParams.FILL_PARENT,
			 * android.view.ViewGroup.LayoutParams.FILL_PARENT));
			 * spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			 * enableViews(productstock_layout, false); progressBar = new
			 * ProgressBar(ProductStockActivity.this);
			 * progressBar.setProgress(android.R.attr.progressBarStyle);
			 * progressBar.setIndeterminateDrawable(getResources().getDrawable(
			 * drawable.greenprogress));
			 * 
			 * spinnerLayout.addView(progressBar);
			 */
			progressBar();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			editProductArr = NewProductWebService.getEditProductService(
					keyProdCode, "fncGetProduct");

			editBarcodeArr = NewProductWebService.getEditBarcodeService(
					keyProdCode, "fncGetProductBarCode");

			NewProductWebService.setBarcodeArr(editBarcodeArr);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (!editProductArr.isEmpty()) {
				Log.d("Edit Product", editProductArr.toString());

				for (int i = 0; i < editProductArr.size(); i++) {
					ProductCode = editProductArr.get(0);
					ProductName = editProductArr.get(1);
					WeightBarcodeStartsOn = editProductArr.get(2);
					WeightBarcodeEndsOn = editProductArr.get(3);
					HaveBatch = editProductArr.get(4);
					HaveExpiry = editProductArr.get(5);
					HaveMfgDate = editProductArr.get(6);
					CategoryCode = editProductArr.get(7);
					SubCategoryCode = editProductArr.get(8);
					UOMCode = editProductArr.get(9);
					weightFld = editProductArr.get(10);
					cartonPrice = editProductArr.get(11);
					units = editProductArr.get(12);
					PcsPerCarton = editProductArr.get(13);
					binDetail = editProductArr.get(14);

				}

				LogOutSetGet.setAddProduct("ProductStock");
				Intent i = new Intent(ProductStockActivity.this,
						AndroidTabLayoutActivity.class);
				i.putExtra("ProductCode", ProductCode);
				i.putExtra("ProductName", ProductName);
				i.putExtra("WeightBarcodeStartsOn", WeightBarcodeStartsOn);
				i.putExtra("WeightBarcodeEndsOn", WeightBarcodeEndsOn);
				i.putExtra("HaveBatch", HaveBatch);
				i.putExtra("HaveExpiry", HaveExpiry);
				i.putExtra("HaveMfgDate", HaveMfgDate);
				i.putExtra("CategoryCode", CategoryCode);
				i.putExtra("SubCategoryCode", SubCategoryCode);
				i.putExtra("UOMCode", UOMCode);
				i.putExtra("PcsPerCarton", PcsPerCarton);
				i.putExtra("weight", weightFld);

				i.putExtra("cartonPrice", cartonPrice);
				i.putExtra("units", units);
				i.putExtra("bindetail", binDetail);

				startActivity(i);
				ProductStockActivity.this.finish();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(productstock_layout, true);
		}
	}

	private class AsyncCallWSADDPRD extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			al.clear();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			SoapObject request = new SoapObject(NAMESPACE, webMethName);
			PropertyInfo companyCode = new PropertyInfo();

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

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
				String productTxt = response.toString();
				String productresult = " { ProductDetails : " + productTxt
						+ "}";
				JSONObject jsonResponse;
				try {

					jsonResponse = new JSONObject(productresult);

					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("ProductDetails");

					int lengthJsonArr = jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);

						String productcodes = jsonChildNode.optString(
								PRODUCT_CODE).toString();
						String productnames = jsonChildNode.optString(
								PRODUCT_NAME).toString();

						HashMap<String, String> producthm = new HashMap<String, String>();
						producthm.put(productcodes, productnames);
						al.add(producthm);

					}

				} catch (JSONException e) {

					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

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

	private class ProductStockAsyncCall extends AsyncTask<Void, Void, Void> {
		String barcode, pro_Code;

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			ProductListArray.clear();
			Product_Name_List.clear();
			mProductStockArrList.clear();
			product_search.setEnabled(false);

			spinnerLayout = new LinearLayout(ProductStockActivity.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(productstock_layout, false);
			progressBar = new ProgressBar(ProductStockActivity.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));

			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			producthashValue.put("CompanyCode", cmpnyCode);
			producthashValue.put("LocationCode", LocationCode);
			product_stock_jsonString = WebServiceClass.parameterService(
					producthashValue, "fncGetProductWithStock");
			barcodeArray = NewProductWebService.getEditBarcode("fncGetProductBarCode");

			try {
				serverdatetime = DateWebservice
						.getDateService("fncGetServerDateTime");
				product_stock_jsonResponse = new JSONObject(
						product_stock_jsonString);
				product_stock_jsonMainNode = product_stock_jsonResponse
						.optJSONArray("JsonArray");

			} catch (JSONException e) {

				e.printStackTrace();
			} catch (Exception e) {

			}

//			if (!barcodeArray.isEmpty()) {
//				for(int j=0;j<barcodeArray.size();j++) {
//					bar_productCode = barcodeArray.get(j).getProductCode();
//					Log.d("bar_productCode", "-->" + bar_productCode + "barcode:" + barcode);
//					try {
//						/*********** Process each JSON Node ************/
//						int lengthJsonArr = product_stock_jsonMainNode.length();
//						for (int i = 0; i < lengthJsonArr; i++) {
//							/****** Get Object for each JSON node. ***********/
//							JSONObject jsonChildNode;
//
//							jsonChildNode = product_stock_jsonMainNode.getJSONObject(i);
//
//							pro_Code = jsonChildNode.optString("ProductCode")
//									.toString();
//							Log.d("pro_Code", "-->" + pro_Code);
//
//							if (pro_Code != null && !pro_Code.isEmpty()) {
//								if (bar_productCode.matches(prod_code)) {
//									String pro_Name = jsonChildNode.optString("ProductName")
//											.toString();
//									String pro_Quantity = jsonChildNode.optString("Qty")
//											.toString();
//									String pro_PcsPerCarton = jsonChildNode.optString(
//											"PcsPerCarton").toString();
//
//									String WholeSalePrice = jsonChildNode.optString(
//											"WholeSalePrice").toString();
//
//									String LocationCode = jsonChildNode.optString(
//											"LocationCode").toString();
//
//									String Weight = jsonChildNode.optString("Weight")
//											.toString();
//
//
//									ProductStockGetSet pro_set = new ProductStockGetSet();
//									if (pro_Quantity != null && !pro_Quantity.isEmpty()) {
//										if (Double.parseDouble(pro_Quantity) > 0) {
//											int cartonqty = (int) (Double
//													.parseDouble(pro_Quantity) / Double
//													.parseDouble(pro_PcsPerCarton));
//											int lqty = (int) (Double.parseDouble(pro_Quantity) % Double
//													.parseDouble(pro_PcsPerCarton));
//											String looseqty = twoDecimalPoint(lqty);
//
//											Log.d("cartonqtyCheck", "-->" + cartonqty);
//
//											pro_set.setProduct_Name(pro_Name);
//											pro_set.setProduct_Code(pro_Code);
//											pro_set.setProduct_Quantity(pro_Quantity);
//											pro_set.setProduct_PcsPerCarton(pro_PcsPerCarton);
//											pro_set.setCartonqty(cartonqty);
//											pro_set.setLooseqty(looseqty);
//											pro_set.setWholeSalePrice(WholeSalePrice);
//											pro_set.setLocation(LocationCode);
//											pro_set.setWeight(Weight);
//
//											onlyBalanceProductArray.add(pro_set);
//										} else {
//
//											int cartonqty = (int) (Double
//													.parseDouble(pro_Quantity) / Double
//													.parseDouble(pro_PcsPerCarton));
//											int lqty = (int) (Double.parseDouble(pro_Quantity) % Double
//													.parseDouble(pro_PcsPerCarton));
//											String looseqty = twoDecimalPoint(lqty);
//											pro_set.setProduct_Name(pro_Name);
//											pro_set.setProduct_Code(pro_Code);
//											pro_set.setProduct_Quantity(pro_Quantity);
//											pro_set.setProduct_PcsPerCarton(pro_PcsPerCarton);
//											pro_set.setCartonqty(cartonqty);
//											pro_set.setLooseqty(looseqty);
//											pro_set.setWholeSalePrice(WholeSalePrice);
//											pro_set.setLocation(LocationCode);
//											pro_set.setWeight(Weight);
//
//											ProductListArray.add(pro_set);
//
//										}
//									} else {
//										pro_set.setProduct_Name(pro_Name);
//										pro_set.setProduct_Code(pro_Code);
//										pro_set.setProduct_Quantity(pro_Quantity);
//										pro_set.setProduct_PcsPerCarton(pro_PcsPerCarton);
//										pro_set.setCartonqty(0);
//										pro_set.setLooseqty("0");
//										pro_set.setWholeSalePrice(WholeSalePrice);
//										pro_set.setLocation(LocationCode);
//										pro_set.setWeight(Weight);
//
//										ProductListArray.add(pro_set);
//									}
//									mProductStockArrList.add(pro_set);
//									Product_Name_List.add(pro_Name);
//								} else {
////									Toast.makeText(getApplicationContext(), "No Product Code Detected!!", Toast.LENGTH_LONG).show();
//								}
//							}
//						}
//					} catch (JSONException e) {
//
//						e.printStackTrace();
//					}
//				}
//			} else {
			try {
				/*********** Process each JSON Node ************/
				int lengthJsonArr = product_stock_jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode;

					jsonChildNode = product_stock_jsonMainNode.getJSONObject(i);

					String pro_Name = jsonChildNode.optString("ProductName")
							.toString();
					String pro_Code = jsonChildNode.optString("ProductCode")
							.toString();
					String pro_Quantity = jsonChildNode.optString("Qty")
							.toString();
					String pro_PcsPerCarton = jsonChildNode.optString(
							"PcsPerCarton").toString();

					String WholeSalePrice = jsonChildNode.optString(
							"WholeSalePrice").toString();

					String LocationCode = jsonChildNode.optString(
							"LocationCode").toString();

					String Weight = jsonChildNode.optString("Weight")
							.toString();
					String barcode = jsonChildNode.optString("Barcode");
//						String barcode = "AMAA";
					ProductStockGetSet pro_set = new ProductStockGetSet();
					ProductStockGetSet pro_set1 = new ProductStockGetSet();
					if (pro_Quantity != null && !pro_Quantity.isEmpty()) {
						if (Double.parseDouble(pro_Quantity) > 0) {
							int cartonqty = (int) (Double
									.parseDouble(pro_Quantity) / Double
									.parseDouble(pro_PcsPerCarton));
							int lqty = (int) (Double.parseDouble(pro_Quantity) % Double
									.parseDouble(pro_PcsPerCarton));
							String looseqty = twoDecimalPoint(lqty);

							Log.d("cartonqtyCheck", "-->" + cartonqty);

							pro_set.setProduct_Name(pro_Name);
							pro_set.setProduct_Code(pro_Code);
							pro_set.setProduct_Quantity(pro_Quantity);
							pro_set.setProduct_PcsPerCarton(pro_PcsPerCarton);
							pro_set.setCartonqty(cartonqty);
							pro_set.setLooseqty(looseqty);
							pro_set.setWholeSalePrice(WholeSalePrice);
							pro_set.setLocation(LocationCode);
							pro_set.setWeight(Weight);
							pro_set.setBarcode(barcode);
							onlyBalanceProductArray.add(pro_set);

							pro_set1.setSlno("" + (i + 1));
							pro_set1.setProduct_Code(pro_Code);
							pro_set1.setProduct_Name(pro_Name);
							pro_set1.setProduct_Quantity(pro_Quantity);
							pro_set1.setCartonqty(cartonqty);
							pro_set1.setLooseqty(looseqty);
							pro_set1.setProduct_PcsPerCarton(pro_PcsPerCarton);
							pro_set1.setWholeSalePrice(WholeSalePrice);
							closed_stock_arr.add(pro_set1);


						} else {

							int cartonqty = (int) (Double
									.parseDouble(pro_Quantity) / Double
									.parseDouble(pro_PcsPerCarton));
							int lqty = (int) (Double.parseDouble(pro_Quantity) % Double
									.parseDouble(pro_PcsPerCarton));
							String looseqty = twoDecimalPoint(lqty);
							pro_set.setProduct_Name(pro_Name);
							pro_set.setProduct_Code(pro_Code);
							pro_set.setProduct_Quantity(pro_Quantity);
							pro_set.setProduct_PcsPerCarton(pro_PcsPerCarton);
							pro_set.setCartonqty(cartonqty);
							pro_set.setLooseqty(looseqty);
							pro_set.setWholeSalePrice(WholeSalePrice);
							pro_set.setLocation(LocationCode);
							pro_set.setWeight(Weight);
							pro_set.setBarcode(barcode);
							ProductListArray.add(pro_set);

						}
					} else {
						pro_set.setProduct_Name(pro_Name);
						pro_set.setProduct_Code(pro_Code);
						pro_set.setProduct_Quantity(pro_Quantity);
						pro_set.setProduct_PcsPerCarton(pro_PcsPerCarton);
						pro_set.setCartonqty(0);
						pro_set.setLooseqty("0");
						pro_set.setWholeSalePrice(WholeSalePrice);
						pro_set.setLocation(LocationCode);
						pro_set.setWeight(Weight);
						pro_set.setBarcode(barcode);

						ProductListArray.add(pro_set);
					}


					Log.d("closed_stock_arr", "-->" + closed_stock_arr.size());
					Log.d("onlyBalanceProductArray","-->"+onlyBalanceProductArray.size());
					mProductStockArrList.add(pro_set);
					Product_Name_List.add(pro_Name);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

			/******* Fetch node values **********/

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("ProductListArray Result", ProductListArray.toString());
			Log.d("ProductName_List", Product_Name_List.toString());

			auto_adapter = new ArrayAdapter<String>(ProductStockActivity.this,
					android.R.layout.select_dialog_item, Product_Name_List);
			loadProduct();
			/*
			 * Adapter = new ProductCustomAdapter(ProductStockActivity.this,
			 * ProductListArray); product_list.setAdapter(Adapter);
			 */
			actv_search.setAdapter(auto_adapter);
			if (ProductListArray.isEmpty()) {
				printIcon.setVisibility(View.GONE);
			} else {
				printIcon.setVisibility(View.VISIBLE);
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(productstock_layout, true);

			product_search.setEnabled(true);
		}
	}

	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}

	public void loadProduct() {
		if (mCheckBox.isChecked()) {
			Adapter = new ProductCustomAdapter(ProductStockActivity.this,
					onlyBalanceProductArray, "");
			Log.d("onlyBalanceProductArray", "onlyBalanceProductArray");

		} else {
			Adapter = new ProductCustomAdapter(ProductStockActivity.this,
					ProductListArray, "");

			Log.d("ProductArray", "ProductArray");

		}
		product_list.setAdapter(Adapter);
		Adapter.notifyDataSetChanged();
	}

	public class ProductCustomAdapter extends BaseAdapter implements Filterable {

		private ArrayList<ProductStockGetSet> listarray = new ArrayList<ProductStockGetSet>();
		private ArrayList<ProductStockGetSet> mOriginalValues = new ArrayList<ProductStockGetSet>();
		LayoutInflater mInflater;
		CustomHolder holder = new CustomHolder();
		ProductStockGetSet user;
		private Filter sampleFilter;
		String allOther;

		public ProductCustomAdapter(Context context,
									ArrayList<ProductStockGetSet> productsList, String all) {
			listarray.clear();
			mOriginalValues.clear();
			this.listarray = productsList;
			this.mOriginalValues = productsList;
			mInflater = LayoutInflater.from(context);
			this.allOther = all;
		}

		@Override
		public int getCount() {
			return listarray.size();
		}

		@Override
		public ProductStockGetSet getItem(int position) {
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
			holder = new CustomHolder();
				row = mInflater.inflate(R.layout.productstock_list_item, null);

				holder.productName = (TextView) row
						.findViewById(R.id.productName);
				holder.productCode = (TextView) row.findViewById(R.id.pro_code);
				holder.Quantity = (TextView) row.findViewById(R.id.Quantity);
				holder.productPCS = (TextView) row.findViewById(R.id.pcs);
				holder.cartonqty = (TextView) row.findViewById(R.id.carqty);
				holder.looseqty = (TextView) row.findViewById(R.id.looqty);
				holder.qty_txt = (TextView) row.findViewById(R.id.quantity_txt);
				holder.pcs_txt = (TextView) row.findViewById(R.id.pcs_txt);
				holder.wholesaleprice = (TextView) row
						.findViewById(R.id.stock_wholesaleprice);

				holder.weight = (TextView) row.findViewById(R.id.wt);
				holder.wt_hide = (TextView) row.findViewById(R.id.wt_hide);
				holder.wt_txt = (TextView) row.findViewById(R.id.wt_txt);
				holder.allLayout = (LinearLayout) row.findViewById(R.id.allLayout);
				holder.linear_data = (LinearLayout) row.findViewById(R.id.linear_data);

				row.setTag(holder);


			if (allOther.matches("all")) {
				holder.linear_data.setVisibility(View.GONE);
				holder.allLayout.setVisibility(View.VISIBLE);
			} else {
				holder.linear_data.setVisibility(View.VISIBLE);
				holder.allLayout.setVisibility(View.GONE);
			}

			row.setId(position);
			user = getItem(position);
			holder.productName.setText(user.getProduct_Name());
			holder.productCode.setText(user.getProduct_Code());

			if (user.getProduct_Quantity().matches(".00")
					|| user.getProduct_Quantity().matches("0.00")
					|| user.getProduct_Quantity().matches("0.0")) {
				holder.Quantity.setText("0");
			} else {

				StringTokenizer tokens = new StringTokenizer(
						user.getProduct_Quantity(), ".");
				String qty = tokens.nextToken();

				holder.Quantity.setText(qty);
			}

			holder.cartonqty.setText(user.getCartonqty() + "");
			holder.productPCS.setText(user.getProduct_PcsPerCarton());

			if (user.getLooseqty().matches(".00")
					|| user.getLooseqty().matches("0.00")
					|| user.getLooseqty().matches("0.0")) {
				holder.looseqty.setText("0");
			} else {
				holder.looseqty.setText(user.getLooseqty());
			}

			holder.wholesaleprice.setText(user.getWholeSalePrice());

			if (!appType.matches("")) {

				if (appType.matches("W")) {
					holder.wt_hide.setVisibility(View.GONE);
					holder.wt_txt.setVisibility(View.VISIBLE);
					holder.weight.setVisibility(View.VISIBLE);
					holder.weight.setText(user.getWeight());
				} else {
					holder.wt_hide.setVisibility(View.VISIBLE);
					holder.wt_txt.setVisibility(View.GONE);
					holder.weight.setVisibility(View.GONE);
				}
			}

			// holder.weight.setText(user.getWeight());

			holder.productCode.setTag(position);

		/*	if (position % 2 == 0) {

				row.setBackgroundResource(R.drawable.list_item_even_bg);
				holder.productName.setTextColor(Color.parseColor("#035994"));
				holder.productCode.setTextColor(Color.parseColor("#035994"));
				holder.Quantity.setTextColor(Color.parseColor("#035994"));
				holder.productPCS.setTextColor(Color.parseColor("#035994"));
				holder.cartonqty.setTextColor(Color.parseColor("#035994"));
				holder.looseqty.setTextColor(Color.parseColor("#035994"));
				holder.qty_txt.setTextColor(Color.parseColor("#035994"));
				holder.pcs_txt.setTextColor(Color.parseColor("#035994"));
				holder.wholesaleprice.setTextColor(Color.parseColor("#035994"));
				holder.weight.setTextColor(Color.parseColor("#035994"));

			} else {

				row.setBackgroundResource(R.drawable.list_item_odd_bg);
				holder.productName.setTextColor(Color.parseColor("#646464"));
				holder.productCode.setTextColor(Color.parseColor("#646464"));
				holder.Quantity.setTextColor(Color.parseColor("#646464"));
				holder.productPCS.setTextColor(Color.parseColor("#646464"));
				holder.cartonqty.setTextColor(Color.parseColor("#646464"));
				holder.looseqty.setTextColor(Color.parseColor("#646464"));
				holder.qty_txt.setTextColor(Color.parseColor("#646464"));
				holder.pcs_txt.setTextColor(Color.parseColor("#646464"));
				holder.wholesaleprice.setTextColor(Color.parseColor("#646464"));
				holder.weight.setTextColor(Color.parseColor("#646464"));

			}*/

			return row;
		}

		final class CustomHolder {
			TextView productName;
			TextView productCode;
			TextView Quantity;
			TextView productPCS;
			TextView cartonqty;
			TextView looseqty;

			TextView qty_txt;
			TextView pcs_txt;
			TextView wholesaleprice;
			TextView weight;
			TextView wt_hide;
			TextView wt_txt;
			LinearLayout linear_data, allLayout;

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
					ArrayList<ProductStockGetSet> FilteredArrList = new ArrayList<ProductStockGetSet>();
					constraint = constraint.toString().toLowerCase();
					for (int i = 0; i < mOriginalValues.size(); i++) {
						String data = mOriginalValues.get(i).getProduct_Name();
						String code = mOriginalValues.get(i).getProduct_Code();
						String barcode = mOriginalValues.get(i).getBarcode();
						if (data.toLowerCase().contains(constraint.toString()) || code.toLowerCase().contains(constraint.toString()) || barcode.toLowerCase().contains(constraint.toString())) {
							FilteredArrList.add(new ProductStockGetSet(
									mOriginalValues.get(i).getProduct_Name(),
									mOriginalValues.get(i).getProduct_Code(),
									mOriginalValues.get(i)
											.getProduct_Quantity(),
									mOriginalValues.get(i)
											.getProduct_PcsPerCarton(),
									mOriginalValues.get(i).getCartonqty(),
									mOriginalValues.get(i).getLooseqty()));
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
					listarray = (ArrayList<ProductStockGetSet>) results.values;
					notifyDataSetChanged();
				} else {
					listarray = (ArrayList<ProductStockGetSet>) results.values;
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
		ProductStockGetSet so = Adapter.getItem(info.position);
		prod_code = so.getProduct_Code().toString();
		prod_name = so.getProduct_Name().toString();
		menu.setHeaderTitle(prod_code);

		if (addProdRslt == true) {
			menu.add(0, v.getId(), 0, "Edit");
		}
		/*menu.add(1, v.getId(), 1, "View other Location");
		menu.add(2, v.getId(), 2, "Image");*/
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		if (item.getTitle() == "Edit") {
			keyProdCode = prod_code;
			AsyncCallWSEditProduct task = new AsyncCallWSEditProduct();
			task.execute();

		} else if (item.getTitle() == "View other Location") {
			Intent i = new Intent(ProductStockActivity.this,
					StockOtherLocation.class);
			i.putExtra("ProductCode", prod_code);
			startActivity(i);
			ProductStockActivity.this.finish();

		} else if (item.getTitle() == "Image") {
			progressBar();
			params.put("CompanyCode", cmpnyCode);
			params.put("PageNo", "");
			params.put("ProductCode", prod_code);
			params.put("CategoryCode", "");
			params.put("SubCategoryCode", "");
			params.put("CustomerGroupCode", "");
			params.put("CustomerCode", "");
			params.put("TranType", "");
			params.put("ProductName", "");
			new XMLAccessTask(this, valid_url, "fncGetProductMainImage",
					params, false, new GetProductStockImage()).execute();
		} else {
			return false;
		}
		return true;
	}

	public class GetProductStockImage implements CallbackInterface {

		@Override
		public void onSuccess(NodeList nl) {

			String productstockimage = null;
			for (int i = 0; i < nl.getLength(); i++) {

				Element e = (Element) nl.item(i);
				productstockimage = XMLParser.getValue(e, "ProductImage");
			}

			// dialogSubMenu(productstockimage);

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(productstock_layout, true);
			if (productstockimage != null && !productstockimage.isEmpty()) {

				// submenuArra.add("View Image");
				zoomView(productstockimage, VIEW_IMAGE);
			} else {
				dialogSubMenu();
			}
		}

		@Override
		public void onFailure(ErrorType error) {
			onError(error);
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(productstock_layout, true);
		}

	}

	private void dialogSubMenu() {
		submenuArra = new ArrayList<String>();
		submenuArra.clear();

		// Create dialog
		final Dialog dialog = new Dialog(ProductStockActivity.this);
		dialog.setTitle("Image");
		dialog.setContentView(R.layout.image_dialog);

		LinearLayout imageLayout = (LinearLayout) dialog
				.findViewById(R.id.image_layout);
		ListView listview = (ListView) dialog.findViewById(R.id.listView1);

		imageLayout.setVisibility(View.GONE);
		listview.setVisibility(View.VISIBLE);
		/*
		 * if(productstockimage != null && !productstockimage.isEmpty()){
		 * 
		 * submenuArra.add("View Image"); } else{
		 * submenuArra.add("Take a Picture"); submenuArra.add("Gallery"); }
		 */
		submenuArra.add("Take a Picture");
		submenuArra.add("Gallery");

		ArrayAdapter<String> submenuAdaptet = new ArrayAdapter<String>(
				ProductStockActivity.this, android.R.layout.select_dialog_item,
				submenuArra);
		listview.setAdapter(submenuAdaptet);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
									int position, long id) {

				/*
				 * if(productstockimage != null &&
				 * !productstockimage.isEmpty()){ if(position == 0){ //
				 * Toast.makeText(getApplicationContext(), "View Image",
				 * Toast.LENGTH_SHORT).show();
				 * zoomView(productstockimage,VIEW_IMAGE); dialog.dismiss(); }
				 * 
				 * }
				 */
				// else{
				if (position == 0) {
					// Toast.makeText(getApplicationContext(), "Take a picture",
					// Toast.LENGTH_SHORT).show();
					CameraAction();
					dialog.dismiss();

				} else if (position == 1) {
					// Toast.makeText(getApplicationContext(), "Gallery",
					// Toast.LENGTH_SHORT).show();
					GalleryAction();
					dialog.dismiss();
				}
				// }

			}
		});

		// Show the dialog
		dialog.show();
	}

/*	public void CameraAction() {
		*//*try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, PICK_FROM_CAMERA);
		} catch (ActivityNotFoundException e) {
		}*//*
		
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

	}*/

	/*	public void GalleryAction() {

        //	ImageCropFunction();

    //		Intent intent = new Intent();
    //		intent.setType("image*//*");
//		intent.setAction(Intent.ACTION_GET_CONTENT);
//		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
//				SELECT_PICTURE);

		*//*Intent intent = new Intent();
// call android default gallery
		intent.setType("image*//**//*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
// ******** code for crop image
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 0);
		intent.putExtra("aspectY", 0);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 150);

		try {
			intent.putExtra("return-data", true);
			startActivityForResult(Intent.createChooser(intent,
					"Complete action using"), SELECT_PICTURE);

		} catch (ActivityNotFoundException e) {
// Do nothing for now
		}*//*
	}*/
	public void CameraAction() {
		try {
			// use standard intent to capture an image
			Intent captureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			// we will handle the returned data in onActivityResult
			startActivityForResult(captureIntent, PICK_FROM_CAMERA);

		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void GalleryAction() {
		Intent intent = new Intent();
		// call android default gallery
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		// ******** code for crop image
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 0);
		intent.putExtra("aspectY", 0);
		try {
			intent.putExtra("return-data", true);
			startActivityForResult(intent, SELECT_PICTURE);

		} catch (ActivityNotFoundException e) {
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {

				case PICK_FROM_CAMERA:
					if (requestCode == PICK_FROM_CAMERA) {
						// get the Uri for the captured image
						picUri = data.getData();
						performCrop();
					}
					break;
				case CROP_PIC:
					// user is returning from cropping the image
					if (requestCode == CROP_PIC) {
						Bundle extras = data.getExtras();
						Log.d("data", "" + data);
						if (extras != null) {
							try {

								Bitmap bp = (Bitmap) data.getExtras().get(
										"data");
								Log.d("bp", "" + bp);

								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								bp.compress(Bitmap.CompressFormat.JPEG, 90,
										baos);
								byte[] b = baos.toByteArray();

								Log.d("byte array", "" + b);

								String imageEncoded = Base64.encodeToString(b,
										Base64.DEFAULT);

								Log.d("img encode", imageEncoded);

								zoomView(imageEncoded, SELECT_PICTURE);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
			/*case PICK_FROM_CAMERA:
				if (requestCode == PICK_FROM_CAMERA) {
					if (resultCode == RESULT_OK) {

						Bundle extras = data.getExtras();

						if (extras != null) {
							try {
								Bitmap bp = (Bitmap) data.getExtras().get(
										"data");
								Log.d("bp", ""+bp);

//								selectedImageUri = data.getData();

								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								bp.compress(Bitmap.CompressFormat.JPEG, 90,
										baos);
								byte[] b = baos.toByteArray();
								
								Log.d("byte array", ""+b);
								
								String imageEncoded = Base64.encodeToString(b,
										Base64.DEFAULT);
								
								Log.d("img encode", imageEncoded);
								
								zoomView(imageEncoded, SELECT_PICTURE);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else if (resultCode == RESULT_CANCELED) {

						// user cancelled Image capture
						Toast.makeText(getApplicationContext(),
								"User cancelled gallery image",
								Toast.LENGTH_SHORT).show();

					} else {
						// failed to capture image
						Toast.makeText(getApplicationContext(),
								"Sorry! Failed to load gallery image",
								Toast.LENGTH_SHORT).show();
					}

				}*/
					break;
				case SELECT_PICTURE:
					if (requestCode == SELECT_PICTURE) {
						//if (resultCode == RESULT_OK) {
						Bundle extras = data.getExtras();
						Log.d("data", "-->" + data);
						if (extras != null) {
							Bitmap bp = (Bitmap) data.getExtras().get("data");
							Log.d("galllery bp", "" + bp);

							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							bp.compress(Bitmap.CompressFormat.JPEG, 90,
									baos);
							byte[] b = baos.toByteArray();

							Log.d("galllery byte array", "" + b);

							String imageEncoded = Base64.encodeToString(b,
									Base64.DEFAULT);
							zoomView(imageEncoded, SELECT_PICTURE);
						}
						/*Log.d("data gallery","dd "+data.getData());

						try {
							Uri selectedImageUri = data.getData();
							String selectedImagePath = getPath(selectedImageUri);
							Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
							Bitmap bp = BitmapFactory.decodeFile(selectedImagePath);
							
							 Log.d("selectedImagePath", ""+selectedImagePath);
						     Log.d("bitmap String ", ""+bitmap);

							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							bp.compress(Bitmap.CompressFormat.JPEG, 90, baos);
							byte[] b = baos.toByteArray();
							
							Log.d("byte array", Arrays.toString(b));	
							Log.d("byte length", ""+b.length);
							
							String imageEncoded = Base64.encodeToString(b,
									Base64.DEFAULT);
							zoomView(imageEncoded, SELECT_PICTURE);

							
						} catch (Exception e) {
							e.printStackTrace();
						}*/
					/*} else if (resultCode == RESULT_CANCELED) {

						// user cancelled Image capture
						Toast.makeText(getApplicationContext(),
								"User cancelled image capture",
								Toast.LENGTH_SHORT).show();

					} else {
						// failed to capture image
						Toast.makeText(getApplicationContext(),
								"Sorry! Failed to capture image",
								Toast.LENGTH_SHORT).show();
					}*/

					}
					break;
			}
		}
	}

	/**
	 * this function does the crop operation.
	 */
	private void performCrop() {
		// take care of exceptions
		try {
			// call the standard crop action intent (the user device may not
			// support it)
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			// indicate image type and Uri
			cropIntent.setDataAndType(picUri, "image/*");
			// set crop properties
			cropIntent.putExtra("crop", "true");
			// indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 0);
			cropIntent.putExtra("aspectY", 0);
			// indicate output X and Y
			//cropIntent.putExtra("outputX", 256);
			//cropIntent.putExtra("outputY", 256);
			// retrieve data on return
			cropIntent.putExtra("return-data", true);
			// start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, CROP_PIC);
		}
		// respond to users whose devices do not support the crop action
		catch (ActivityNotFoundException anfe) {
			Toast toast = Toast
					.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	/*public void ImageCropFunction() {

		// Image Crop Code
		try {
			Intent CropIntent = new Intent("com.android.camera.action.CROP");

			CropIntent.setType("image*//*");

			CropIntent.putExtra("crop", "true");
			CropIntent.putExtra("outputX", 180);
			CropIntent.putExtra("outputY", 180);
			CropIntent.putExtra("aspectX", 3);
			CropIntent.putExtra("aspectY", 4);
			CropIntent.putExtra("scaleUpIfNeeded", true);
			CropIntent.putExtra("return-data", true);

			startActivityForResult(CropIntent, SELECT_PICTURE);

		} catch (ActivityNotFoundException e) {

		}
	}*/
	//Image Crop Code End Here

	/**
	 * Image Zoom View Start
	 **/
	private void zoomView(final String imagestring, final int resultCode) {

		byte[] encodeByte;
		Bitmap myBitmap;
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inSampleSize = 2;

		byte[] encodeByte1 = Base64.decode(imagestring, Base64.DEFAULT);

		Log.d("First time img", imagestring);

		String s;
		try {
			s = new String(encodeByte1, "UTF-8");
//			Log.d("s-->", ""+s);
			encodeByte = Base64.decode(s, Base64.DEFAULT);

		} catch (Exception e) {

			encodeByte = encodeByte1;

		}

		final Dialog dialog = new Dialog(ProductStockActivity.this);
		dialog.setTitle(prod_name);
		dialog.setContentView(R.layout.image_dialog);
		ImageViewTouch imageView1 = (ImageViewTouch) dialog
				.findViewById(R.id.imageView2);
		bt_DialogAction1 = (Button) dialog.findViewById(R.id.button1);
		bt_DialogAction2 = (Button) dialog.findViewById(R.id.button2);
		bt_DialogAction3 = (Button) dialog.findViewById(R.id.button3);
		bt_DialogAction4 = (Button) dialog.findViewById(R.id.button4);

		divierId = dialog.getContext().getResources()
				.getIdentifier("android:id/titleDivider", null, null);
		divider = dialog.findViewById(divierId);
		img_progress = (ImageView) dialog.findViewById(R.id.imgProgress);
		AnimationDrawable frameAnimation = (AnimationDrawable) img_progress
				.getDrawable();
		frameAnimation.setCallback(img_progress);
		frameAnimation.setVisible(true, true);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

//		Bitmap myBitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
//				encodeByte.length, options);

		if (options.outWidth > 3000 || options.outHeight > 2000) {
			options.inSampleSize = 4;
		} else if (options.outWidth > 2000 || options.outHeight > 1500) {
			options.inSampleSize = 3;
		} else if (options.outWidth > 1000 || options.outHeight > 1000) {
			options.inSampleSize = 2;
		}

		options.inJustDecodeBounds = false;
		myBitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
				encodeByte.length, options);

//		myBitmap = reduceImageSize(imageView1,imagestring);

		// imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
		// imageView1.setAdjustViewBounds(true);
		imageView1.setImageBitmapReset(myBitmap, 0, true);

		// imageView1.setImageBitmap(myBitmap);
		bt_DialogAction1.setText(R.string.camera);
		bt_DialogAction2.setText(R.string.gallery);
		bt_DialogAction3.setText(R.string.save);
		bt_DialogAction4.setText(R.string.cncl);

		if (resultCode == VIEW_IMAGE) {
			bt_DialogAction1.setVisibility(View.VISIBLE);
			bt_DialogAction2.setVisibility(View.VISIBLE);
			bt_DialogAction3.setVisibility(View.GONE);
			bt_DialogAction4.setVisibility(View.VISIBLE);
		} else if (resultCode == SELECT_PICTURE) {

			bt_DialogAction1.setVisibility(View.GONE);
			bt_DialogAction2.setVisibility(View.GONE);
			bt_DialogAction3.setVisibility(View.VISIBLE);
			bt_DialogAction4.setVisibility(View.VISIBLE);
		}
		bt_DialogAction1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CameraAction();
				dialog.dismiss();
			}
		});
		bt_DialogAction2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				GalleryAction();
				dialog.dismiss();

			}
		});
		bt_DialogAction3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				divider.setBackgroundColor(getResources().getColor(
						android.R.color.transparent));
				img_progress.setVisibility(View.VISIBLE);
				bt_DialogAction1.setEnabled(false);
				new AsyncSaveProductImage(dialog).execute(imagestring);

			}
		});
		bt_DialogAction4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				dialog.dismiss();

			}
		});
		dialog.setCancelable(false);
		dialog.show();

	}

	public Bitmap reduceImageSize(ImageView imageView, String imageString) {
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

		return bitmap;
	}

	@SuppressWarnings("deprecation")
	public String getPath(Uri uri) {
		// just some safety built in
		if (uri == null) {
			// TODO perform some logging or show user feedback
			return null;
		}
		// try to retrieve the image from the media store first
		// this will only work for images selected from gallery
		String[] projection = {MediaColumns.DATA};
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		// this is our fallback here
		return uri.getPath();
	}

	private class AsyncSaveProductImage extends AsyncTask<String, Void, String> {
		Dialog dialog;
		String resTxt = "";

		public AsyncSaveProductImage(Dialog dialog) {
			// TODO Auto-generated constructor stub
			this.dialog = dialog;
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {
			String strProductImage = params[0];

			Log.d("", "" + strProductImage);

			try {

				SoapObject request = new SoapObject(NAMESPACE, "fncUpdateProductImage");

				PropertyInfo CompanyCode = new PropertyInfo();
				PropertyInfo ProductCode = new PropertyInfo();
				PropertyInfo FileName = new PropertyInfo();
				PropertyInfo ImagesBytes = new PropertyInfo();

				CompanyCode.setName("CompanyCode");
				CompanyCode.setValue(cmpnyCode);
				CompanyCode.setType(String.class);
				request.addProperty(CompanyCode);

				ProductCode.setName("ProductCode");
				ProductCode.setValue(prod_code);
				ProductCode.setType(String.class);
				request.addProperty(ProductCode);

				FileName.setName("FileName");
				FileName.setValue("product.png");
				FileName.setType(String.class);
				request.addProperty(FileName);

				ImagesBytes.setName("ImagesBytes");
				ImagesBytes.setValue(strProductImage);
				ImagesBytes.setType(Byte.class);
				request.addProperty(ImagesBytes);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;
				envelope.setOutputSoapObject(request);
				envelope.bodyOut = request;
				HttpTransportSE androidHttpTransport = new HttpTransportSE(valid_url);
				androidHttpTransport.call(SOAP_ACTION + "fncUpdateProductImage", envelope);
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				resTxt = response.toString();
				jsonString = " { SODetails : " + resTxt + "}";

			} catch (Exception e) {
				e.printStackTrace();

			}

			Log.d("jsonStr ", "" + jsonString);

			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			img_progress.setVisibility(View.GONE);
			if ((resTxt != null && !resTxt.isEmpty())) {

				if (resTxt.matches("true")) {
					Toast.makeText(getApplication(), "Saved Successfully",
							Toast.LENGTH_SHORT).show();
				} else {

					Toast.makeText(ProductStockActivity.this, "Failed",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(ProductStockActivity.this, "Failed",
						Toast.LENGTH_SHORT).show();
			}
			divider.setBackgroundColor(getResources().getColor(R.color.blue));
			bt_DialogAction1.setEnabled(true);
			dialog.dismiss();
		}

	}

	private class AsyncProductStockPrintCall extends
			AsyncTask<Void, Void, ArrayList<String>> {

		@Override
		protected void onPreExecute() {
			productstockArr.clear();
			helper.showProgressDialog(R.string.generating_ps);
		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {
			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("LocationCode", LocationCode);
			hashValue.put("TransferDate", currentdate);

			jsonString = SalesOrderWebService.getSODetail(hashValue,
					"fncGetProductStockWithTransferQty");
			Log.d("jsonStr ", "" + jsonString);

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");

			} catch (JSONException e) {

				e.printStackTrace();
			}
			int lengJsonArray = jsonMainNode.length();
			for (int i = 0; i < lengJsonArray; i++) {
				ProductStockGetSet productstock = new ProductStockGetSet();
				JSONObject jsonChildNodes;

				try {
					jsonChildNodes = jsonMainNode.getJSONObject(i);

					String productname = jsonChildNodes
							.optString("ProductName").toString();
					String qty = jsonChildNodes.optString("Qty").toString();
					String issueqty = jsonChildNodes.optString("InQty")
							.toString();
					productstock.setProduct_Name(productname);
					productstock.setQty(qty);
					productstock.setIssueqty(issueqty);

					productstockArr.add(productstock);
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
			hashValue.clear();

			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			if (productstockArr.size() > 0) {
				try {
					print();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				helper.dismissProgressDialog();
				Toast.makeText(ProductStockActivity.this,
						"No Product stock found", Toast.LENGTH_SHORT).show();
			}
		}

	}

	public void dialogPrinter() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Print");
		alertDialog.setMessage("Do you want to Print");
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						try {
							print();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						/*
						 * String mobileproductstockprint =
						 * SalesOrderSetGet.getMobileproductstockprint();
						 * if(!mobileproductstockprint.matches("")){
						 * if(mobileproductstockprint.matches("1")){
						 * dialogDate(); dialog.dismiss(); }else
						 * if(mobileproductstockprint.matches("0")){ try {
						 * print(); } catch (IOException e) { // TODO
						 * Auto-generated catch block e.printStackTrace(); } }
						 * 
						 * }
						 */
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

	public void dialogDate() {

		/*
		 * helper.showProgressDialog(R.string.generating_receipt);
		 * AsyncPrintCall task = new AsyncPrintCall(); task.execute();
		 */
		AlertDialog.Builder alert = new AlertDialog.Builder(
				ProductStockActivity.this);
		alert.setTitle("Print"); // Set Alert dialog title here
		alert.setMessage("Date"); // Message here

		// Set an EditText view to get user input
		edprintDate = new EditText(ProductStockActivity.this);
		edprintDate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.mipmap.ic_calendar, 0);
		edprintDate.setCursorVisible(false);
		/*
		 * edprintDate.setBackgroundResource(R.drawable.border); final
		 * LayoutParams lparams = new LayoutParams(10,50); // Width , height
		 * edprintDate.setLayoutParams(lparams);
		 */
		StringTokenizer tokens = new StringTokenizer(serverdatetime, " ");
		String serverdate = tokens.nextToken();

		edprintDate.setText(serverdate);
		alert.setView(edprintDate);

		final DatePickerDialog.OnDateSetListener ptdate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
								  int dayOfMonth) {
				printerCalendar.set(Calendar.YEAR, year);
				printerCalendar.set(Calendar.MONTH, monthOfYear);
				printerCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				printerDate();
			}
		};

		edprintDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(ProductStockActivity.this, ptdate,
							printerCalendar.get(Calendar.YEAR), printerCalendar
							.get(Calendar.MONTH), printerCalendar
							.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// You will get as string input data in this variable.
				// here we convert the input to a string and show in a toast.
				currentdate = edprintDate.getEditableText().toString();
				// Toast.makeText(ProductStockActivity.this,currentdate,Toast.LENGTH_LONG).show();

				new AsyncProductStockPrintCall().execute();

			} // End of onClick(DialogInterface dialog, int whichButton)
		}); // End of alert.setPositiveButton
		alert.setNegativeButton("CANCEL",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						dialog.cancel();
					}
				}); // End of alert.setNegativeButton
		AlertDialog alertDialog = alert.create();
		alertDialog.show();

	}

	private void printerDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		edprintDate.setText(sdf.format(printerCalendar.getTime()));
	}

	private void print() throws IOException {
		helper.dismissProgressDialog();
		String locationName = SalesOrderSetGet.getLocationname();
		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		// HashMap<String, String> location =
		// SupplierSetterGetter.getLoc_code_name();
		// String locationName = location.get(LocationCode);
		System.out.println("locationName==>" + locationName);
		Log.d("onlyBalanceProductBal","-->"+onlyBalanceProductArray.size()+"chooseTab:"+chooseTab);
		Log.d("ProductListArrayTot","-->"+ProductListArray.size());

		try {
			Printer printer = new Printer(ProductStockActivity.this, macaddress);
			if(chooseTab == true){
				printer.printProductStock(LocationCode, locationName,
						serverdatetime, onlyBalanceProductArray, productstockArr);
			}else {
				printer.printProductStock(LocationCode, locationName,
						serverdatetime, ProductListArray, productstockArr);
			}

		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(ProductStockActivity.this, LandingActivity.class);
		startActivity(i);
		ProductStockActivity.this.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListItemClick(String item) {
		// TODO Auto-generated method stub
		menu.toggle();

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

	private class LocationAdapter extends RecyclerView.Adapter<MyViewHolder> {
		Context context;
		List<LocationGetSet> list;
		LayoutInflater inflater;

		public LocationAdapter(Context context, List<LocationGetSet> locationList) {
			Log.d("listCheck", "" + locationList.size());
			this.context = context;
			this.list = locationList;
		}

		@Override
		public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.location_txt, null);
			return new MyViewHolder(view);
		}

		@Override
		public void onBindViewHolder(MyViewHolder holder, int position) {

			LocationGetSet locationGetSet = list.get(position);

			Log.d("listData", "-->" + locationGetSet.getLocation_code().toString());
			String pos = locationGetSet.getLocation_code().toString();
			Log.d("pocCheck", pos);
			holder.loctnText.setText(pos);

		}

		@Override
		public int getItemCount() {
			return list.size();
		}
	}

	private class MyViewHolder extends RecyclerView.ViewHolder {
		TextView loctnText;

		public MyViewHolder(View itemView) {
			super(itemView);
			loctnText = (TextView) itemView.findViewById(R.id.txt);
		}
	}

	private class ShowAllLocation extends AsyncTask<String, String, String> {
		HashMap<String, String> hashValue = new HashMap<String, String>();

		@Override
		protected void onPreExecute() {
			locationList = new ArrayList<>();
			locationList.clear();
		}

		@Override
		protected String doInBackground(String... strings) {

			try {
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
							Log.d("setLocationCheck",LocationGetSet.getIsMainLocation());
							locationGetSet.setLocatn(locationCode);
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
			row2.removeAllViews();
			LocationCode = SalesOrderSetGet.getLocationcode();
			view1 = new TextView(ProductStockActivity.this);
//			view1.setText("");
			Log.d("getText()","-->"+view1.getText().toString());
			if (view1.getText().toString().matches("")) {
				view1.setWidth(60);
				view1.setGravity(Gravity.CENTER);
				view1.setText("SNo");
				view1.setTextColor(Color.WHITE);
				view1.setPadding(10, 10, 10, 10);
				view1.setTypeface(view1.getTypeface(), Typeface.BOLD);
				row2.addView(view1);
			} else {
			}
			view2 = new TextView(ProductStockActivity.this);
//			view2.setText("");
			if (view2.getText().toString().matches("")) {
				view2.setWidth(300);
				view2.setMaxLines(2);
				view2.setGravity(Gravity.CENTER);
				view2.setText("Product Name");
				view2.setGravity(Gravity.CENTER);
				view2.setTextColor(Color.WHITE);
				view2.setPadding(10, 10, 10, 10);
				view2.setTypeface(view2.getTypeface(), Typeface.BOLD);
				row2.addView(view2);
			} else {
			}

			for (int i = 0; i < locationList.size(); i++) {
				view = new TextView(ProductStockActivity.this);
//				view.setText("");
				view.setGravity(Gravity.CENTER);
				view.setTextColor(Color.WHITE);
				view.setWidth(100);
//				view.setPadding(10,10,10,10);
				view.setText(locationList.get(i).getLocatn());
				view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				view1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				view2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

				view.setTypeface(view.getTypeface(), Typeface.BOLD);
				row2.addView(view);
			}
			view3 = new TextView(ProductStockActivity.this);
//			view3.setText("");
			if (view3.getText().toString().matches("")) {
				view3.setWidth(300);
				view3.setText("Total");
				view3.setGravity(Gravity.CENTER);
				view3.setTextColor(Color.WHITE);
				view3.setPadding(10, 10, 10, 10);
				view3.setTypeface(view3.getTypeface(), Typeface.BOLD);
				view3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				row2.addView(view3);

			} else {
			}

//			adapter = new LocationAdapter(ProductStockActivity.this, locationList);
//			loctn.setAdapter(adapter);
//			for(int i=0;i<locationList.size();i++){
//				if(LocationCode.matches(locationList.get(i).toString())){
//					Log.d("LocationCheck",locationList.get(i).toString());
//					int pos =locationList.indexOf(LocationCode);
//					Log.d("pos",""+pos);
//					Collections.swap(locationList, 0, pos);
//					Log.d("LocationListCheck","-->"+locationList.get(i).toString());
//				}
//				Log.d("LocationListCheck1","-->"+locationList.get(i).toString());
//			}


		}
	}

	private class AsyncAllLocation extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			productStockList = new ArrayList<>();
			pStockLocatin = new HashMap<>();
			pStockLocatin.clear();
			lctnList = new ArrayList<>();
			lctnList.clear();

		}

		@Override
		protected Void doInBackground(Void... voids) {
			stockHashValue.put("CompanyCode", cmpnyCode);
//			stockHashValue.put("ProductCode","0000004");
			stock_jsonString = WebServiceClass.parameterService(
					stockHashValue, "fncGetAllLocationProductStock");
			try {
				stock_jsonResponse = new JSONObject(
						stock_jsonString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			stock_jsonMainNode = stock_jsonResponse.optJSONArray("JsonArray");

			try {
				/*********** Process each JSON Node ************/
				int lengthJsonArr = stock_jsonMainNode.length();

				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode;

					jsonChildNode = stock_jsonMainNode.getJSONObject(i);

					String lctn_code = jsonChildNode.optString("LocationCode")
							.toString();
					json_main_Node = jsonChildNode.optJSONArray("LocationCode");

					for (int j = 0; j < json_main_Node.length(); j++) {
						Log.d("json_main_Node", "-->" + json_main_Node.get(j).toString());
					}

					String pro_Code = jsonChildNode.optString("ProductCode")
							.toString();

					String Total = jsonChildNode.optString("Total")
							.toString();
					String pro_name = jsonChildNode.optString("ProductName").toString();


					/*StringBuilder builder = new StringBuilder(lctn_code);
					builder.deleteCharAt(0);
					StringBuilder builder1 = new StringBuilder(builder.toString());
					builder1.deleteCharAt(builder.toString().length()-1);
					Log.d("buldercheck",""+builder1+"builder->"+builder);




					LocationGetSet productStockGetSet = new LocationGetSet();
					String[] namesList = builder1.toString().split(",");
					for(int j=0;j<namesList.length;j++){
						String name = namesList[j];
						Log.d("nameCheck",name);
						StringBuilder builder2 = new StringBuilder(name);
						builder2.deleteCharAt(0);
						StringBuilder builder3 = new StringBuilder(builder2.toString());
						builder3.deleteCharAt(builder2.toString().length() - 1);
						Log.d("removesymbol", "-->" + builder3);
						productStockGetSet.setLocation_code(builder3.toString());
						lctnList.add(productStockGetSet);
//						lctn_fnl_list.add(lctnList);
						Log.d("lctnList","-->"+lctnList.get(j).getLocation_code());
						Log.d("lctn_fnl_list","-->"+lctn_fnl_list.get(j).getLocation_code());
					}*/
					LocationGetSet productStockGetSet = new LocationGetSet();
					productStockGetSet.setSlno(String.valueOf(i + 1));
					productStockGetSet.setLocation_code(json_main_Node);
					productStockGetSet.setPro_code(pro_Code);
					productStockGetSet.setTotal(Total);
					productStockGetSet.setPro_name(pro_name);
					productStockList.add(productStockGetSet);
					Log.d("productStockListCheck", "" + productStockList.size());


				}

			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			adapter_recycler = new StockCustomAdapter(ProductStockActivity.this, productStockList, lctnList);
			recyclerView.setAdapter(adapter_recycler);
		}
	}

	public class StockCustomAdapter extends RecyclerView.Adapter<CheckViewHolder> implements Filterable {
		Context context;
		List<LocationGetSet> lists;
		List<LocationGetSet> filterLists = new ArrayList<>();
		LayoutInflater inflater;
		HashMap<String, String> locatnwithQty;
		List<LocationGetSet> lctnlist;
		LocationShowAdapter adapter;
		JSONArray json_node;
		private Filter samplesFilter;

		public StockCustomAdapter(Context productStockActivity, List<LocationGetSet> productStockList, List<LocationGetSet> locationList) {
			this.context = productStockActivity;
			this.lists = productStockList;
			this.filterLists = productStockList;
			this.lctnlist = locationList;
			Log.d("ListCountCheck", "" + lists.size() + "-->" + lctnlist.size());
		}

		@Override
		public CheckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.stock_adapters, null);
			return new CheckViewHolder(view);
		}

		@Override
		public void onBindViewHolder(CheckViewHolder holder, int position) {

			LocationGetSet productStockGetSet = lists.get(position);
			json_node = productStockGetSet.getLocation_code();

			TextView view1 = new TextView(context);
			view1.setWidth(60);

			if (view1.getText().toString().matches("")) {
				view1.setText(productStockGetSet.getSlno());
			} else {

			}
			TextView view2 = new TextView(context);
			view2.setWidth(300);

			if (view2.getText().toString().matches("")) {
				view2.setText(productStockGetSet.getPro_name());
			} else {

			}

			holder.rows2.addView(view1);
			holder.rows2.addView(view2);
			view1.setGravity(Gravity.CENTER);
			view1.setTextColor(Color.BLACK);
			view1.setPadding(10, 10, 10, 10);
			//view1.setTypeface(view1.getTypeface(), Typeface.BOLD);

			view2.setGravity(Gravity.LEFT);
//				view2.setMaxLines(2);
			view2.setSingleLine(true);
			view2.setTextColor(Color.BLACK);
			view2.setPadding(10, 10, 10, 10);
			//view2.setTypeface(view2.getTypeface(), Typeface.BOLD);

			for (int j = 0; j < json_node.length(); j++) {
				TextView view = new TextView(context);
				view.setGravity(Gravity.CENTER);
				view.setTextColor(Color.BLACK);
				view.setWidth(100);
//					view.setPadding(10,10,10,10);
				try {
					view.setText(json_node.get(j).toString());
					Log.d("checkAdaptervalues", "--?" + json_node.get(j).toString() + "productCode:" + productStockGetSet.getPro_code());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				view1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				view2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				//view.setTypeface(view.getTypeface(), Typeface.BOLD);
				holder.rows2.addView(view);
			}

			TextView view3 = new TextView(context);

			if (view3.getText().toString().matches("")) {
				view3.setText(productStockGetSet.getTotal());
			} else {

			}
			view3.setWidth(300);
			view3.setGravity(Gravity.CENTER);
			view3.setTextColor(Color.BLACK);
			view3.setPadding(10, 10, 10, 10);
			//view3.setTypeface(view3.getTypeface(), Typeface.BOLD);
			view3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			holder.rows2.addView(view3);
			holder.setIsRecyclable(false);

//			Log.d("checkDetails",productStockGetSet.getSlno());
			/*holder.slno.setText(productStockGetSet.getSlno());
			holder.pname.setText(productStockGetSet.getPro_code());
			holder.total.setText(productStockGetSet.getTotal());*/

//			holder.recyclerview.setHasFixedSize(true);
//			holder.recyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true));
//			adapter = new LocationShowAdapter(context,lctnlist);
//			holder.recyclerview.setAdapter(adapter);

		}

		@Override
		public int getItemCount() {
			return lists.size();
		}

		@Override
		public Filter getFilter() {
			Log.d("startsExecuting","--?"+"");
			if (samplesFilter == null)
				samplesFilter = new SamplesFilter();

			return samplesFilter;

		}

		private class SamplesFilter extends Filter {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				Log.d("constraint","-->"+constraint);
				FilterResults results = new FilterResults();
				// We implement here the filter logic
				if (constraint == null || constraint.length() == 0) {
					// No filter implemented we return all the list
					results.values = filterLists;
					results.count = filterLists.size();
				} else {
					// We perform filtering operation
					ArrayList<LocationGetSet> FilteredArrList = new ArrayList<LocationGetSet>();
					constraint = constraint.toString().toLowerCase();
					for (int i = 0; i < filterLists.size(); i++) {
						String data = filterLists.get(i).getPro_name();
						String code = filterLists.get(i).getPro_code();
//						String barcode = mOriginalValues.get(i).getBarcode();
						if (data.toLowerCase().contains(constraint.toString()) || code.toLowerCase().contains(constraint.toString())) {
							FilteredArrList.add(new LocationGetSet(
									filterLists.get(i).getPro_name(),
									filterLists.get(i).getPro_code(),
									filterLists.get(i)
											.getSlno(),
									filterLists.get(i)
											.getLocatn(),
									filterLists.get(i).getPcsperCarton(),
									filterLists.get(i).getLocation_code(),
									filterLists.get(i).getTotal()));
						}


					}
					// set the Filtered result to return
					results.count = FilteredArrList.size();
					results.values = FilteredArrList;

				}
				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {

				if (results.count == 0) {
					lists = (ArrayList<LocationGetSet>) results.values;
					adapter_recycler.notifyDataSetChanged();
				} else {
					lists = (ArrayList<LocationGetSet>) results.values;
					adapter_recycler.notifyDataSetChanged();
				}
			}
		}
	}

	private class CheckViewHolder extends RecyclerView.ViewHolder {
		TextView slno, pname, total, locationCode;
		RecyclerView recyclerview;
		LinearLayout rows2;

		public CheckViewHolder(View itemView) {
			super(itemView);
			rows2 = (LinearLayout) itemView.findViewById(R.id.rows2);
			/*slno = (TextView) itemView.findViewById(R.id.ss_prodcode);
			pname = (TextView) itemView.findViewById(R.id.ss_name);
			total = (TextView) itemView.findViewById(R.id.ss_c_qty);
			locationCode = (TextView)itemView.findViewById(R.id.locationCode);
			recyclerview = (RecyclerView) itemView.findViewById(R.id.recyclerView);*/
		}
	}

	private class LocationShowAdapter extends RecyclerView.Adapter<OurViewHolder> {
		Context context;
		List<LocationGetSet> lctnlist;
		LayoutInflater inflater;

		public LocationShowAdapter(Context context, List<LocationGetSet> locationList) {
			this.context = context;
			this.lctnlist = locationList;
			Log.d("locationListChck", "-->" + lctnlist.size());
		}

		@Override
		public OurViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.location_txts, null);
			return new OurViewHolder(view);
		}

		@Override
		public void onBindViewHolder(OurViewHolder holder, int position) {
			LocationGetSet locationGetSet = lctnlist.get(position);
			Log.d("listDataChecking", "-->" + locationGetSet.getLocation_code());
			String pos = locationGetSet.getLocation_code().toString();
			holder.loctnText.setText(pos);
			Log.d("pocCheckchecking", pos);
		}

		@Override
		public int getItemCount() {
			return lctnlist.size();
		}
	}

	private class OurViewHolder extends RecyclerView.ViewHolder {
		TextView loctnText;

		public OurViewHolder(View itemView) {
			super(itemView);
			loctnText = (TextView) itemView.findViewById(R.id.txts);
		}
	}

	private class ShowLocation extends AsyncTask<String, String, String> {
		HashMap<String, String> hashValue = new HashMap<String, String>();

		@Override
		protected void onPreExecute() {
			locationList = new ArrayList<>();
			locationList.clear();
		}

		@Override
		protected String doInBackground(String... strings) {

			try {
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
								LocationGetSet.setIsMainLocation(locationCode);
							}
							locationGetSet.setLocatn(locationCode);
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
			row2.removeAllViews();
			LocationCode = SalesOrderSetGet.getLocationcode();
			TextView view1 = new TextView(ProductStockActivity.this);
			view1.setWidth(100);
			view1.setGravity(Gravity.CENTER);
			view1.setText("SNo");
			TextView view2 = new TextView(ProductStockActivity.this);
			view2.setWidth(350);
			view2.setMaxLines(2);
			view2.setGravity(Gravity.CENTER);
			view2.setText("Product Name");
			TextView view3 = new TextView(ProductStockActivity.this);
			view3.setText("Total");
			view3.setWidth(300);
			row2.addView(view1);
			row2.addView(view2);
			view1.setGravity(Gravity.CENTER);
			view1.setTextColor(Color.WHITE);
			view1.setPadding(10, 10, 10, 10);
			view1.setTypeface(view1.getTypeface(), Typeface.BOLD);

			view2.setGravity(Gravity.CENTER);
			view2.setTextColor(Color.WHITE);
			view2.setPadding(10, 10, 10, 10);
			view2.setTypeface(view2.getTypeface(), Typeface.BOLD);

			view3.setGravity(Gravity.CENTER);
			view3.setTextColor(Color.WHITE);
			view3.setPadding(10, 10, 10, 10);
			view3.setTypeface(view3.getTypeface(), Typeface.BOLD);
			for (int i = 0; i < locationList.size(); i++) {
				TextView view = new TextView(ProductStockActivity.this);
				view.setGravity(Gravity.CENTER);
				view.setTextColor(Color.WHITE);
				view.setWidth(150);
//				view.setPadding(10,10,10,10);
				view.setText(locationList.get(i).getLocatn());
				view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				view1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				view2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				view3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				view.setTypeface(view.getTypeface(), Typeface.BOLD);
				row2.addView(view);
			}
			row2.addView(view3);

			adapter = new LocationAdapter(ProductStockActivity.this, locationList);
//			loctn.setAdapter(adapter);
//			for(int i=0;i<locationList.size();i++){
//				if(LocationCode.matches(locationList.get(i).toString())){
//					Log.d("LocationCheck",locationList.get(i).toString());
//					int pos =locationList.indexOf(LocationCode);
//					Log.d("pos",""+pos);
//					Collections.swap(locationList, 0, pos);
//					Log.d("LocationListCheck","-->"+locationList.get(i).toString());
//				}
//				Log.d("LocationListCheck1","-->"+locationList.get(i).toString());
//			}


		}
	}

	private class AsyncLocation extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			productStockList = new ArrayList<>();
			pStockLocatin = new HashMap<>();
			pStockLocatin.clear();
			lctnList = new ArrayList<>();
			lctnList.clear();

		}

		@Override
		protected Void doInBackground(Void... voids) {
			stockHashValue.put("CompanyCode", cmpnyCode);
//			stockHashValue.put("ProductCode","0000004");
			stock_jsonString = WebServiceClass.parameterService(
					stockHashValue, "fncGetAllLocationProductStock");
			try {
				stock_jsonResponse = new JSONObject(
						stock_jsonString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			stock_jsonMainNode = stock_jsonResponse.optJSONArray("JsonArray");

			try {
				/*********** Process each JSON Node ************/
				int lengthJsonArr = stock_jsonMainNode.length();

				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode;

					jsonChildNode = stock_jsonMainNode.getJSONObject(i);

					String lctn_code = jsonChildNode.optString("LocationCode")
							.toString();
					json_main_Node = jsonChildNode.optJSONArray("LocationCode");

					for (int j = 0; j < json_main_Node.length(); j++) {
						Log.d("json_main_Node", "-->" + json_main_Node.get(j).toString());
					}

					String pro_Code = jsonChildNode.optString("ProductCode")
							.toString();

					String Total = jsonChildNode.optString("Total")
							.toString();

					String pro_name = jsonChildNode.optString("ProductName").toString();

					String ppc = jsonChildNode.optString("PcsPerCarton").toString();


					/*StringBuilder builder = new StringBuilder(lctn_code);
					builder.deleteCharAt(0);
					StringBuilder builder1 = new StringBuilder(builder.toString());
					builder1.deleteCharAt(builder.toString().length()-1);
					Log.d("buldercheck",""+builder1+"builder->"+builder);




					LocationGetSet productStockGetSet = new LocationGetSet();
					String[] namesList = builder1.toString().split(",");
					for(int j=0;j<namesList.length;j++){
						String name = namesList[j];
						Log.d("nameCheck",name);
						StringBuilder builder2 = new StringBuilder(name);
						builder2.deleteCharAt(0);
						StringBuilder builder3 = new StringBuilder(builder2.toString());
						builder3.deleteCharAt(builder2.toString().length() - 1);
						Log.d("removesymbol", "-->" + builder3);
						productStockGetSet.setLocation_code(builder3.toString());
						lctnList.add(productStockGetSet);
//						lctn_fnl_list.add(lctnList);
						Log.d("lctnList","-->"+lctnList.get(j).getLocation_code());
						Log.d("lctn_fnl_list","-->"+lctn_fnl_list.get(j).getLocation_code());
					}*/
					LocationGetSet productStockGetSet = new LocationGetSet();
					productStockGetSet.setSlno(String.valueOf(i + 1));
					productStockGetSet.setLocation_code(json_main_Node);
					productStockGetSet.setPro_code(pro_Code);
					productStockGetSet.setTotal(Total);
					productStockGetSet.setPro_name(pro_name);
					productStockGetSet.setPcsperCarton(ppc);
					productStockList.add(productStockGetSet);
					Log.d("productStockListCheck", "" + productStockList.size());


				}

			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			adapter_stock = new Stockadapter(ProductStockActivity.this, productStockList);
			recyclerView.setAdapter(adapter_stock);
		}
	}

	private class Stockadapter extends RecyclerView.Adapter<HViewHolder> implements Filterable{
		Context context;
		List<LocationGetSet> lists;
		List<LocationGetSet> filterLists = new ArrayList<>();
		LayoutInflater inflater;
		JSONArray json_node;
		private Filter samplesFilter;

		public Stockadapter(Context productStockActivity, List<LocationGetSet> productStockList) {
			this.context = productStockActivity;
			this.lists = productStockList;
			this.filterLists = productStockList;
		}

		@Override
		public HViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.stock_adapters, null);
			return new HViewHolder(view);
		}

		@Override
		public void onBindViewHolder(HViewHolder holder, int position) {
			LocationGetSet productStockGetSet = lists.get(position);
			json_node = productStockGetSet.getLocation_code();

			TextView view1 = new TextView(context);
			view1.setWidth(100);

			if (view1.getText().toString().matches("")) {
				view1.setText(productStockGetSet.getSlno());
			} else {

			}
			TextView view2 = new TextView(context);
			view2.setWidth(350);

			if (view2.getText().toString().matches("")) {
				view2.setText(productStockGetSet.getPro_name());
			} else {

			}

			holder.rows2.addView(view1);
			holder.rows2.addView(view2);
			view1.setGravity(Gravity.CENTER);
			view1.setTextColor(Color.BLACK);
			view1.setPadding(10, 10, 10, 10);
			//view1.setTypeface(view1.getTypeface(), Typeface.BOLD);

			view2.setGravity(Gravity.LEFT);
//				view2.setMaxLines(2);
			view2.setSingleLine(true);
			view2.setTextColor(Color.BLACK);
			view2.setPadding(10, 10, 10, 10);
			//view2.setTypeface(view2.getTypeface(), Typeface.BOLD);

			for (int j = 0; j < json_node.length(); j++) {
				TextView view = new TextView(context);
				view.setGravity(Gravity.CENTER);
				view.setTextColor(Color.BLACK);
				view.setWidth(150);
//					view.setPadding(10,10,10,10);
				try {
					view.setText(json_node.get(j).toString());
					Log.d("checkAdaptervalues", "--?" + json_node.get(j).toString() + "productCode:" + productStockGetSet.getPro_code());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				view1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				view2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				//view.setTypeface(view.getTypeface(), Typeface.BOLD);
				holder.rows2.addView(view);
			}

			TextView view3 = new TextView(context);

			if (view3.getText().toString().matches("")) {
				view3.setText(productStockGetSet.getTotal());
			} else {

			}
			view3.setWidth(300);
			view3.setGravity(Gravity.CENTER);
			view3.setTextColor(Color.BLACK);
			view3.setPadding(10, 10, 10, 10);
			//view3.setTypeface(view3.getTypeface(), Typeface.BOLD);
			view3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			holder.rows2.addView(view3);
			holder.setIsRecyclable(false);
		}


		@Override
		public int getItemCount() {
			return lists.size();
		}

		@Override
		public Filter getFilter() {
			Log.d("startsExecuting","--?"+"");
			if (samplesFilter == null)
				samplesFilter = new SamplesFilter();

			return samplesFilter;

		}

		private class SamplesFilter extends Filter {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				Log.d("constraint","-->"+constraint);
				FilterResults results = new FilterResults();
				// We implement here the filter logic
				if (constraint == null || constraint.length() == 0) {
					// No filter implemented we return all the list
					results.values = filterLists;
					results.count = filterLists.size();
				} else {
					// We perform filtering operation
					ArrayList<LocationGetSet> FilteredArrList = new ArrayList<LocationGetSet>();
					constraint = constraint.toString().toLowerCase();
					for (int i = 0; i < filterLists.size(); i++) {
						String data = filterLists.get(i).getPro_name();
						String code = filterLists.get(i).getPro_code();
//						String barcode = mOriginalValues.get(i).getBarcode();
						if (data.toLowerCase().contains(constraint.toString()) || code.toLowerCase().contains(constraint.toString())) {
							FilteredArrList.add(new LocationGetSet(
									filterLists.get(i).getPro_name(),
									filterLists.get(i).getPro_code(),
									filterLists.get(i)
											.getSlno(),
									filterLists.get(i)
											.getLocatn(),
									filterLists.get(i).getPcsperCarton(),
									filterLists.get(i).getLocation_code(),
									filterLists.get(i).getTotal()));
						}


					}
					// set the Filtered result to return
					results.count = FilteredArrList.size();
					results.values = FilteredArrList;

				}
				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {

				if (results.count == 0) {
					lists = (ArrayList<LocationGetSet>) results.values;
					adapter_stock.notifyDataSetChanged();
				} else {
					lists = (ArrayList<LocationGetSet>) results.values;
					adapter_stock.notifyDataSetChanged();
				}
			}
		}
	}

	private class HViewHolder extends RecyclerView.ViewHolder {
		LinearLayout rows2;

		public HViewHolder(View itemView) {
			super(itemView);
			rows2 = (LinearLayout) itemView.findViewById(R.id.rows2);
		}
	}

	private class AsyncCloseStock extends AsyncTask<Void, Void, Void> {
		String locationCode;
		List<LocationGetSet> lists;

		public AsyncCloseStock(List<LocationGetSet> locationList) {
			this.lists = locationList;
			Log.d("listcount", "-->" + lists.size());
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... voids) {

//			for(int i=0;i<lists.size();i++){
//				locationCode=lists.get(i).getIsMainLocation();
//			}
			locationCode = LocationGetSet.getIsMainLocation();
			Log.d("locationCodeChek", locationCode);
			try {
				soCloseStockArr = SOTSummaryWebService.summaryCloseStock("fncSaveTransfer", locationCode);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}


			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			summaryResult = soCloseStockArr.get(0);
			if (summaryResult.matches("failed")) {
				Toast.makeText(ProductStockActivity.this, "Failed",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ProductStockActivity.this, "Closing Stock closed",
						Toast.LENGTH_SHORT).show();
				Intent i = new Intent(ProductStockActivity.this, LandingActivity.class);
				startActivity(i);
				ProductStockActivity.this.finish();

			}
		}
	}


	private class ShowLocations extends AsyncTask<String, String, String> {
		HashMap<String, String> hashValue = new HashMap<String, String>();

		@Override
		protected void onPreExecute() {
//			locationList = new ArrayList<>();
//			locationList.clear();
		}

		@Override
		protected String doInBackground(String... strings) {

			try {
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
//							locationList.add(locationGetSet);
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
			if(screen==true){
				lctnCode = SalesOrderSetGet.getLocationcode();
				Log.d("lctnCodeCheck","-->"+lctnCode+"-->"+LocationGetSet.getIsMainLocation());
				if(onlyBalanceProductArray.size()==0){
					closed_stock.setVisibility(View.GONE);
				}else{
					if(LocationGetSet.getIsMainLocation().matches(lctnCode)){
						closed_stock.setVisibility(View.GONE);
					}else{
						closed_stock.setVisibility(View.VISIBLE);
					}

				}
			}
		}
	}
}
