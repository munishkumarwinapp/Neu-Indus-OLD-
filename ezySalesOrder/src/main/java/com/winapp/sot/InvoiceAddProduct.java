package com.winapp.sot;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.adapter.CatSubAdapter;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterProd;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.NewProductWebService;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.Product;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSetterGetter;
import com.winapp.sot.FilterCS.OnFilterCompletionListener;
import com.winapp.sotdetails.CustomerListActivity;

public class InvoiceAddProduct extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace,
		OnItemClickListener, OnClickListener {

	private static String webMethProduct = "fncGetProduct";
	private static String webMethNamebar = "fncGetProductBarCode";
	private static String webMethProductWithPrice = "fncGetProductWithPrice";
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String PRODUCT_NAME = "ProductName";
	private static final String PRODUCTNAME_BARCODE = "ProductCode";
	private static final String PRODUCT_BARCODE = "Barcode";
	private ListView mListView;
	private EditText mEditValue;
	private Spinner mSpinnerSotBy;
	private int orientation, orderFocQty = 0, orderExchQty = 0, orderQty = 0,
			orderCQty, orderLQty,orderRetQty =0;
	private TextView mTotalItem, mTotalItemDiscount, mSubTotalItem, mTaxItem,
			mNetTotalItem;
	private CatSubAdapter mCatSubAdapter;
	private ArrayList<Product> productList, mCategoryArr, mSubCategoryArr;
	private ProductListAdapter productAdapter;
	private TextView mCategoryText, mSubCategoryText;
	private SimpleAdapter categoryAdapter, subcategoryAdapter, prodAdapter;
	private JSONObject category_jsonResponse, subcategory_jsonResponse,mBarcodeJsonObject=null;
	private JSONArray category_jsonMainNode, subcategory_jsonMainNode,mBarcodeJsonArray=null;
	private String category_jsonString = null, subcategory_jsonString = null,
			sortStr = "",mBarcodeJsonString="";
	private HashMap<String, String> mHashMap;
	private List<HashMap<String, String>> category_ArrList,
			subcategory_ArrList/*, product_ArrList*/;
	private Double orderTotalItemDiscount = 0.00, orderTotal = 0.00,
			orderTax = 0.00, orderSubTotal = 0.00, orderNetTotal = 0.00;
	private Button mKeyBtnOne, mKeyBtnTwo, mKeyBtnThree, mKeyBtnFour,
			mKeyBtnFive, mKeyBtnSix, mKeyBtnSeven, mKeyBtnEight, mKeyBtnNine,
			mKeyBtnZero, mKeyBtnDot, mKeyBtnClear;
	private AutoCompleteTextView mCategoryAutoCompleteText, mSubCategoryAutoCompleteText;
	SlidingMenu menu;
	TextView textView1;
	EditText editText1;
	ArrayList<HashMap<String, String>> searchProductArr = new ArrayList<HashMap<String, String>>();
	Button sl_addProduct;
	Button sl_summary;
	EditText mProductEd, sl_codefield, sl_namefield, sl_cartonQty, sl_looseQty,
			sl_qty, sl_foc, sl_price, sl_itemDiscount, sl_uom, sl_cartonPerQty,
			sl_total, sl_tax, sl_netTotal, sl_cprice, sl_exchange,sl_total_inclusive,sl_ret;
	AlertDialog myalertDialog = null;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	EditText editText, sl_stock;
	CustomAlertAdapterProd arrayAdapterProd;
	ArrayList<HashMap<String, String>> searchResults;
	int textlength = 0;
	ArrayList<HashMap<String, String>> getArraylist = new ArrayList<HashMap<String, String>>();
	ProgressBar progressBar;
	LinearLayout spinnerLayout, foc_layout, invoice_carton_header,
			customer_layout, pcs_txt_layout, pcs_layout;
	LinearLayout salesproduct_layout, foc_header_layout, price_header_layout,
			grid_layout, carton_layout,cLabel,lLabel;
	String valid_url, productTxt, productresult, barcoderesult, barcodeTxt;
	String slPrice = "", slUomCode = "", slCartonPerQty = "";
	ArrayList<String> getSalesProdArr = new ArrayList<String>();
	String keyValues = "", values = "";
	static ArrayList<String> companyArr = new ArrayList<String>();
	String taxType = "", taxValue = "", sales_prodCode,
			MinimumSellingPrice = "",MinimumCartonSellingPrice="";
	Cursor cursor;
	String beforeLooseQty, beforeFoc;
	ListView productFilterList;
	String catStr = "", subCatStr = "", cstcode = "", cstgrpcd = "",
			newPrice = "", cprice = "", newcprice = "", priceflag = "",
			tranblocknegativestock = "", calCarton = "", locationcode = "";
	String ss_Cqty = "";
	int sl_no = 1;
	double itmDisc = 0;
	double netTtal = 0, taxAmount = 0;
	String str_ssupdate, str_sscancel, str_sssno, intentString = "";
	double tt;
	ArrayList<String> productcode = new ArrayList<String>();
	boolean prdcode = false;
	ArrayList<String> priceArr = new ArrayList<String>();
	ImageView expand;
	TextView price_txt, pageTitle,txt_price;
	TextWatcher cqtyTW, lqtyTW, qtyTW;
	TextView listing_screen, customer_screen, addProduct_screen,
			summary_screen, listing_screen_tab, customer_screen_tab,
			addProduct_screen_tab, summary_screen_tab;
	// FilterSearch filterSearch;
	private FilterCS filtercs;
	HashMap<String, String> producthashValue = new HashMap<String, String>();
	String product_stock_jsonString = null;
	JSONObject product_stock_jsonResponse;
	JSONArray product_stock_jsonMainNode;
	String cmpnyCode = "", LocationCode = "", stock="",mobileHaveOfflineMode="";
	InvoiceBatchDialog invoiceBatchDialog;
	ArrayList<HashMap<String, String>> alBatchStock = new ArrayList<HashMap<String, String>>();
	SimpleAdapter adapter;
	private double screenInches;
	// Offline
	LinearLayout offlineLayout, header_landscape, header_portrait;
	private OfflineDatabase offlineDatabase;
	boolean checkOffline;
	String onlineMode, offlineDialogStatus, dialogStatus = "",Weight="";
	private OfflineCommon offlineCommon;
	String oldQty = "", invoiceaddproducttab = "", haveBatchOnStockIn = "";
	ArrayList<HashMap<String, String>> invoiceEditArr = new ArrayList<HashMap<String, String>>();
	ImageButton swipe;
	View customNav;
	RelativeLayout keyboard;
	ActionBar ab;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.invoice_addproduct);

		header_portrait = (LinearLayout) findViewById(R.id.linear_layout1);
		header_landscape = (LinearLayout) findViewById(R.id.linear_layout2);

		screenInches = displayMetrics();
		Log.d("Display Inche", "" + screenInches); 

		ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setBackgroundDrawable(getResources().getDrawable(drawable.header_bg));

		FWMSSettingsDatabase.init(InvoiceAddProduct.this);
		invoiceaddproducttab = FWMSSettingsDatabase.getInvoiceaddproducttab();
		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();

		if (screenInches > 7) {

			if (invoiceaddproducttab != null && !invoiceaddproducttab.isEmpty()) {
				Log.d("invoiceaddproducttab", "in " + invoiceaddproducttab);
				if (invoiceaddproducttab.matches("1")) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					orientation = InvoiceAddProduct.this.getResources()
							.getConfiguration().orientation;
					// ab.setBackgroundDrawable(getResources().getDrawable(
					// R.drawable.header_bg));
					Log.d("orienation L", "oo " + orientation);

					header_portrait.setVisibility(View.GONE);
					header_landscape.setVisibility(View.VISIBLE);

					customNav = LayoutInflater.from(this).inflate(
							R.layout.addproduct_actionbar_title_tab, null);

					Log.d("LANDSCAPE", "LANDSCAPE");

					keyboard = (RelativeLayout) customNav
							.findViewById(R.id.num_keyboard);

					mKeyBtnOne = (Button) customNav.findViewById(R.id.key_one);
					mKeyBtnTwo = (Button) customNav.findViewById(R.id.key_two);
					mKeyBtnThree = (Button) customNav
							.findViewById(R.id.key_three);
					mKeyBtnFour = (Button) customNav
							.findViewById(R.id.key_four);
					mKeyBtnFive = (Button) customNav
							.findViewById(R.id.key_five);
					mKeyBtnSix = (Button) customNav.findViewById(R.id.key_six);
					mKeyBtnSeven = (Button) customNav
							.findViewById(R.id.key_seven);
					mKeyBtnEight = (Button) customNav
							.findViewById(R.id.key_eight);
					mKeyBtnNine = (Button) customNav
							.findViewById(R.id.key_nine);
					mKeyBtnZero = (Button) customNav
							.findViewById(R.id.key_zero);
					mKeyBtnDot = (Button) customNav.findViewById(R.id.key_dot);
					mKeyBtnClear = (Button) customNav
							.findViewById(R.id.key_clear);

					mKeyBtnOne.setOnClickListener(this);
					mKeyBtnTwo.setOnClickListener(this);
					mKeyBtnThree.setOnClickListener(this);
					mKeyBtnFour.setOnClickListener(this);
					mKeyBtnFive.setOnClickListener(this);
					mKeyBtnSix.setOnClickListener(this);
					mKeyBtnSeven.setOnClickListener(this);
					mKeyBtnEight.setOnClickListener(this);
					mKeyBtnNine.setOnClickListener(this);
					mKeyBtnZero.setOnClickListener(this);
					mKeyBtnDot.setOnClickListener(this);
					mKeyBtnClear.setOnClickListener(this);

				} else {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					// ab.setBackgroundDrawable(getResources().getDrawable(
					// R.drawable.home_bg));
					orientation = InvoiceAddProduct.this.getResources()
							.getConfiguration().orientation;

					header_portrait.setVisibility(View.GONE);
					header_landscape.setVisibility(View.VISIBLE);

					Log.d("orienation P", "oo " + orientation);

					Log.d("PORTRAIT", "PORTRAIT");
					customNav = LayoutInflater.from(this).inflate(
							R.layout.addproduct_actionbar_title_tab, null);

					keyboard = (RelativeLayout) customNav
							.findViewById(R.id.num_keyboard);
				}

			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				// ab.setBackgroundDrawable(getResources().getDrawable(
				// R.drawable.home_bg));
				orientation = InvoiceAddProduct.this.getResources()
						.getConfiguration().orientation;

				header_portrait.setVisibility(View.GONE);
				header_landscape.setVisibility(View.VISIBLE);

				Log.d("orienation P", "oo " + orientation);

				Log.d("PORTRAIT", "PORTRAIT");
				customNav = LayoutInflater.from(this).inflate(
						R.layout.addproduct_actionbar_title_tab, null);

				keyboard = (RelativeLayout) customNav
						.findViewById(R.id.num_keyboard);
			}

			mKeyBtnOne = (Button) customNav.findViewById(R.id.key_one);
			mKeyBtnTwo = (Button) customNav.findViewById(R.id.key_two);
			mKeyBtnThree = (Button) customNav.findViewById(R.id.key_three);
			mKeyBtnFour = (Button) customNav.findViewById(R.id.key_four);
			mKeyBtnFive = (Button) customNav.findViewById(R.id.key_five);
			mKeyBtnSix = (Button) customNav.findViewById(R.id.key_six);
			mKeyBtnSeven = (Button) customNav.findViewById(R.id.key_seven);
			mKeyBtnEight = (Button) customNav.findViewById(R.id.key_eight);
			mKeyBtnNine = (Button) customNav.findViewById(R.id.key_nine);
			mKeyBtnZero = (Button) customNav.findViewById(R.id.key_zero);
			mKeyBtnDot = (Button) customNav.findViewById(R.id.key_dot);
			mKeyBtnClear = (Button) customNav.findViewById(R.id.key_clear);

			mKeyBtnOne.setOnClickListener(this);
			mKeyBtnTwo.setOnClickListener(this);
			mKeyBtnThree.setOnClickListener(this);
			mKeyBtnFour.setOnClickListener(this);
			mKeyBtnFive.setOnClickListener(this);
			mKeyBtnSix.setOnClickListener(this);
			mKeyBtnSeven.setOnClickListener(this);
			mKeyBtnEight.setOnClickListener(this);
			mKeyBtnNine.setOnClickListener(this);
			mKeyBtnZero.setOnClickListener(this);
			mKeyBtnDot.setOnClickListener(this);
			mKeyBtnClear.setOnClickListener(this);
		} else {
			// mobile
			// ab.setBackgroundDrawable(getResources().getDrawable(
			// R.drawable.home_bg));
			customNav = LayoutInflater.from(this).inflate(
					R.layout.addproduct_actionbar_title, null);
			header_portrait.setVisibility(View.GONE);
			header_landscape.setVisibility(View.VISIBLE);
		}

		pageTitle = (TextView) customNav.findViewById(R.id.pageTitle);

		ImageButton pricetag = (ImageButton) customNav
				.findViewById(R.id.priceTag);

		final ImageButton filter = (ImageButton) customNav
				.findViewById(R.id.filter);

		swipe = (ImageButton) customNav.findViewById(R.id.swipe);
		pageTitle.setText("Add Product");

		// ab.setBackgroundDrawable(new
		// ColorDrawable(Color.parseColor("#626776")));

		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);

		ab.setDisplayHomeAsUpEnabled(false);
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);

		// @Offline
		onlineMode = OfflineDatabase.getOnlineMode();
		offlineDatabase = new OfflineDatabase(InvoiceAddProduct.this);
		offlineCommon = new OfflineCommon(InvoiceAddProduct.this);
		checkOffline = OfflineCommon.isConnected(InvoiceAddProduct.this);
		OfflineDatabase.init(InvoiceAddProduct.this);
		new OfflineSalesOrderWebService(InvoiceAddProduct.this);
		offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);

		SOTDatabase.init(InvoiceAddProduct.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SalesProductWebService(valid_url);
		new NewProductWebService(valid_url);
		new SalesOrderWebService(valid_url);

		// filterSearch = new FilterSearch(this);
		filtercs = new FilterCS(InvoiceAddProduct.this);
		mHashMap = new HashMap<String, String>();
		productList = new ArrayList<Product>();
		mCategoryArr = new ArrayList<Product>();
		mSubCategoryArr = new ArrayList<Product>();
		category_ArrList = new ArrayList<HashMap<String, String>>();
		subcategory_ArrList = new ArrayList<HashMap<String, String>>();
	//	product_ArrList = new ArrayList<HashMap<String, String>>();
		mListView = (ListView) findViewById(R.id.listView1);
		mSpinnerSotBy = (Spinner) findViewById(R.id.sortBy);
		mTotalItem = (TextView) findViewById(R.id.totalItem);
		mSubTotalItem = (TextView) findViewById(R.id.subTotalItem);

		mTaxItem = (TextView) findViewById(R.id.taxItem);
		mNetTotalItem = (TextView) findViewById(R.id.netTotalItem);

		mTotalItemDiscount = (TextView) findViewById(R.id.totalItemDiscount);
		mCategoryAutoCompleteText = (AutoCompleteTextView) findViewById(R.id.categorySearch_act);
		mSubCategoryAutoCompleteText = (AutoCompleteTextView) findViewById(R.id.subcategorySearch_act);
		mProductEd = (EditText) findViewById(R.id.productSearch_ed);

		mCategoryText = (TextView) findViewById(R.id.category_tv);
		mSubCategoryText = (TextView) findViewById(R.id.subcategory_tv);

		salesproduct_layout = (LinearLayout) findViewById(R.id.salesproduct_layout);
		foc_layout = (LinearLayout) findViewById(R.id.foc_layout);

		pcs_txt_layout = (LinearLayout) findViewById(R.id.pcs_txt_layout);
		pcs_layout = (LinearLayout) findViewById(R.id.pcs_layout);

		foc_header_layout = (LinearLayout) findViewById(R.id.invoice_foc_header_layout);
		price_header_layout = (LinearLayout) findViewById(R.id.invoice_price_header_layout);
		grid_layout = (LinearLayout) findViewById(R.id.invoice_grid_layout);
		carton_layout = (LinearLayout) findViewById(R.id.invoice_carton_layout);
		invoice_carton_header = (LinearLayout) findViewById(R.id.invoice_carton_header);
		cLabel = (LinearLayout)findViewById(R.id.cLabel);
		lLabel = (LinearLayout)findViewById(R.id.lLabel);
		sl_stock = (EditText) findViewById(R.id.sl_stock);

		sl_summary = (Button) findViewById(R.id.sl_summary);
		sl_addProduct = (Button) findViewById(R.id.sl_addProduct);

		sl_codefield = (EditText) findViewById(R.id.sl_codefield);
		sl_namefield = (EditText) findViewById(R.id.sl_namefield);
		sl_cartonQty = (EditText) findViewById(R.id.sl_cartonQty);
		sl_looseQty = (EditText) findViewById(R.id.sl_looseQty);
		sl_qty = (EditText) findViewById(R.id.sl_qty);
		sl_foc = (EditText) findViewById(R.id.sl_foc);
		sl_price = (EditText) findViewById(R.id.sl_price);
		sl_itemDiscount = (EditText) findViewById(R.id.sl_itemDiscount);
		sl_uom = (EditText) findViewById(R.id.sl_uom);
		sl_cartonPerQty = (EditText) findViewById(R.id.sl_cartonPerQty);
		sl_total_inclusive = (EditText) findViewById(R.id.sl_total_inclusive);
		sl_total = (EditText) findViewById(R.id.sl_total);
		sl_tax = (EditText) findViewById(R.id.sl_tax);
		sl_netTotal = (EditText) findViewById(R.id.sl_netTotal);

		sl_cprice = (EditText) findViewById(R.id.sl_cprice);
		sl_exchange = (EditText) findViewById(R.id.sl_exchange);
		sl_ret = (EditText)findViewById(R.id.sl_return);
		price_txt = (TextView) findViewById(R.id.invoice_pricetxt);
		expand = (ImageView) findViewById(R.id.expand);
		txt_price= (TextView) findViewById(R.id.txt_price);
		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen = (TextView) findViewById(R.id.sum_screen);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);

		listing_screen_tab = (TextView) findViewById(R.id.listing_screen_tab);
		customer_screen_tab = (TextView) findViewById(R.id.customer_screen_tab);
		addProduct_screen_tab = (TextView) findViewById(R.id.addProduct_screen_tab);
		summary_screen_tab = (TextView) findViewById(R.id.sum_screen_tab);

		productFilterList = (ListView) findViewById(R.id.productFilterList);

		// offline
		if (onlineMode.matches("True")) {
			offlineLayout.setVisibility(View.GONE);
		} else if (onlineMode.matches("False")) {
			offlineLayout.setVisibility(View.VISIBLE);
		}

		customer_layout.setVisibility(View.GONE);
		// addProduct_screen.setBackgroundColor(Color.parseColor("#00AFF0"));
		addProduct_screen.setTextColor(Color.parseColor("#FFFFFF"));
		addProduct_screen.setBackgroundResource(drawable.tab_select);

		addProduct_screen_tab.setBackgroundResource(drawable.tab_select);

		if (screenInches > 7) {
			if (invoiceaddproducttab != null && !invoiceaddproducttab.isEmpty()) {

				if (invoiceaddproducttab.matches("1")) {
					// Landscape
					// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					findViewById(R.id.verticalScrollView).setVisibility(
							View.GONE);
					findViewById(R.id.sl_total_layout).setVisibility(View.GONE);
					findViewById(R.id.label_total_layout).setVisibility(
							View.GONE);
					findViewById(R.id.relativeLayout).setVisibility(
							View.VISIBLE);

					swipe.setVisibility(View.VISIBLE);
					keyboard.setVisibility(View.VISIBLE);
					pageTitle.setVisibility(View.GONE);
					filter.setVisibility(View.INVISIBLE);
					// keyboard.setVisibility(View.VISIBLE);
					addProduct_screen
							.setBackgroundResource(drawable.tab_select);
				} else {
					// Portrait
					// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					findViewById(R.id.verticalScrollView).setVisibility(
							View.VISIBLE);
					findViewById(R.id.sl_total_layout).setVisibility(
							View.VISIBLE);
					findViewById(R.id.label_total_layout).setVisibility(
							View.VISIBLE);
					findViewById(R.id.relativeLayout).setVisibility(View.GONE);

					findViewById(R.id.verticalScrollView).setVisibility(
							View.VISIBLE);
					findViewById(R.id.sl_total_layout).setVisibility(
							View.VISIBLE);
					findViewById(R.id.label_total_layout).setVisibility(
							View.VISIBLE);
					findViewById(R.id.relativeLayout).setVisibility(View.GONE);

					swipe.setVisibility(View.VISIBLE);
					keyboard.setVisibility(View.GONE);
					pageTitle.setVisibility(View.VISIBLE);
					filter.setVisibility(View.VISIBLE);
					addProduct_screen
							.setBackgroundResource(drawable.tab_select);
				}

				cursor = SOTDatabase.getCursor();
				if (cursor != null && cursor.getCount() > 0) {
					orderNetTotal = SOTDatabase.getNetTotal();
					orderSubTotal = SOTDatabase.getSubTotal();
					orderTax = SOTDatabase.getTax();
					mSubTotalItem.setText(twoDecimalPoint(SOTDatabase
							.getSubTotal()));
					mTaxItem.setText(fourDecimalPoint(SOTDatabase.getTax()));
					mNetTotalItem.setText(twoDecimalPoint(SOTDatabase
							.getNetTotal()));

				}

			} else {

				// Portrait
				// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				findViewById(R.id.verticalScrollView).setVisibility(
						View.VISIBLE);
				findViewById(R.id.sl_total_layout).setVisibility(View.VISIBLE);
				findViewById(R.id.label_total_layout).setVisibility(
						View.VISIBLE);
				findViewById(R.id.relativeLayout).setVisibility(View.GONE);

				findViewById(R.id.verticalScrollView).setVisibility(
						View.VISIBLE);
				findViewById(R.id.sl_total_layout).setVisibility(View.VISIBLE);
				findViewById(R.id.label_total_layout).setVisibility(
						View.VISIBLE);
				findViewById(R.id.relativeLayout).setVisibility(View.GONE);

				swipe.setVisibility(View.VISIBLE);
				keyboard.setVisibility(View.GONE);
				pageTitle.setVisibility(View.VISIBLE);
				filter.setVisibility(View.VISIBLE);
				addProduct_screen.setBackgroundResource(drawable.tab_select);
				// keyboard.setVisibility(View.GONE);

				cursor = SOTDatabase.getCursor();
				if (cursor != null && cursor.getCount() > 0) {
					orderNetTotal = SOTDatabase.getNetTotal();
					orderSubTotal = SOTDatabase.getSubTotal();
					orderTax = SOTDatabase.getTax();
					mSubTotalItem.setText(twoDecimalPoint(SOTDatabase
							.getSubTotal()));
					mTaxItem.setText(fourDecimalPoint(SOTDatabase.getTax()));
					mNetTotalItem.setText(twoDecimalPoint(SOTDatabase
							.getNetTotal()));

				}

			}
		} else {
			// mobile
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			findViewById(R.id.verticalScrollView).setVisibility(View.VISIBLE);
			findViewById(R.id.sl_total_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.label_total_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.relativeLayout).setVisibility(View.GONE);
			// keyboard.setVisibility(View.GONE);
			swipe.setVisibility(View.GONE);

			filter.setVisibility(View.VISIBLE);
		}

		sl_itemDiscount.setRawInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);

		invoiceBatchDialog = new InvoiceBatchDialog();

		priceflag = SalesOrderSetGet.getCartonpriceflag();
		calCarton = LogOutSetGet.getCalcCarton();

		if (priceflag.matches("null") || priceflag.matches("")) {
			priceflag = "0";
		}
		// calCarton="1";
//		 priceflag = "0";

		if(SalesOrderSetGet.getCarton_quantity().matches("0")){
			cLabel.setVisibility(View.GONE);
			lLabel.setVisibility(View.GONE);
		}else{
			cLabel.setVisibility(View.VISIBLE);
			lLabel.setVisibility(View.VISIBLE);
		}

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			oldQty = "0";

			intentString = extras.getString("Invoice");

			sales_prodCode = extras.getString("SOT_ssproductcode");

			ProductStockAsyncCall task = new ProductStockAsyncCall();
			task.execute();

			sl_codefield.setText(extras.getString("SOT_ssproductcode"));
			sl_namefield.setText(extras.getString("SOT_str_ssprodname"));
			sl_cartonQty.setText(extras.getString("SOT_str_sscq"));
			sl_looseQty.setText(extras.getString("SOT_str_sslq"));
			sl_qty.setText(extras.getString("SOT_str_ssqty"));

			oldQty = extras.getString("SOT_str_ssqty");

			sl_foc.setText(extras.getString("SOT_str_ssfoc"));
			sl_price.setText(extras.getString("SOT_str_ssprice"));
			sl_itemDiscount.setText(extras.getString("SOT_str_ssdisc"));
			sl_uom.setText(extras.getString("SOT_str_ssuom"));
			sl_cartonPerQty.setText(extras.getString("SOT_st_sscpqty"));
			sl_total.setText(extras.getString("SOT_st_sstotal"));

			String netTotal = extras.getString("SOT_st_ssnettot");
			String tax = extras.getString("SOT_st_sstax");
			taxType = extras.getString("SOT_str_sstaxtype");
			// Added New 13.04.2017
			if (taxType != null && !taxType.isEmpty()) {
				if (taxType.matches("I")) {
					double dTax = tax.equals("") ? 0 : Double.valueOf(tax);
					double dNetTotal = netTotal.equals("") ? 0 : Double.valueOf(netTotal);
					double dTotalIncl = dNetTotal - dTax;
					String totIncl = twoDecimalPoint(dTotalIncl);
					sl_total_inclusive.setText(totIncl);
				}else{
					sl_total_inclusive.setText(extras.getString("SOT_st_sstotal"));
				}
			}else{
				sl_total_inclusive.setText(extras.getString("SOT_st_sstotal"));
			}

			sl_tax.setText(tax);

			//sl_tax.setText(extras.getString("SOT_st_sstax"));

			sl_cprice.setText(extras.getString("SOT_str_sscprice"));
			sl_exchange.setText(extras.getString("SOT_str_ssexchqty"));

			taxType = extras.getString("SOT_str_sstaxtype");
			taxValue = extras.getString("SOT_st_sstaxvalue");
			slCartonPerQty = extras.getString("SOT_st_sscpqty");
			Log.e("slCartonPerQty", slCartonPerQty);

			MinimumSellingPrice = extras.getString("SOT_str_minselling_price");

			MinimumCartonSellingPrice = extras.getString("SOT_str_minCartonselling_price");

			Log.d("MinimumSellingPrice", "MinimumSellingPrice"
					+ MinimumSellingPrice);

			Log.d("MinCartonSePrice", "MinCartonSelgPrice"
					+ MinimumCartonSellingPrice);

			sl_netTotal.setText(netTotal);
			//sl_netTotal.setText(extras.getString("SOT_st_ssnettot"));
			str_sssno = extras.getString("SOT_ssno");
			str_ssupdate = extras.getString("SOT_ssupdate");
			str_sscancel = extras.getString("SOT_sscancel");
			if ((str_ssupdate != null) || (str_sscancel != null)) {
				sl_addProduct.setText(str_ssupdate);
				sl_summary.setText(str_sscancel);
			}
			if (intentString != null && !intentString.isEmpty()) {

				invoice_carton_header.setVisibility(View.GONE);
			} else {
				invoice_carton_header.setVisibility(View.VISIBLE);

				if (calCarton.matches("0")) {

				} else {
					if (slCartonPerQty.matches("1")
							|| slCartonPerQty.matches("0")
							|| slCartonPerQty.matches("")) {

						sl_cartonQty.setFocusable(false);
						sl_cartonQty.setBackgroundResource(drawable.labelbg);

						sl_looseQty.setFocusable(false);
						sl_looseQty.setBackgroundResource(drawable.labelbg);

					} else {
						sl_cartonQty.setFocusableInTouchMode(true);
						sl_cartonQty
								.setBackgroundResource(drawable.edittext_bg);

						sl_looseQty.setFocusableInTouchMode(true);
						sl_looseQty
								.setBackgroundResource(drawable.edittext_bg);
					}
				}
			}
			Log.d("Carton per Qty", "" + slCartonPerQty);

			// Edit product with batch
			String pCode = extras.getString("SOT_ssproductcode");
			Cursor pCursor = SOTDatabase.getBatchCursor(pCode);
			int pCount = pCursor.getCount();
			if (pCount > 0) {
				sl_cartonQty.setFocusable(false);
				sl_cartonQty.setBackgroundResource(drawable.labelbg);

				sl_looseQty.setFocusable(false);
				sl_looseQty.setBackgroundResource(drawable.labelbg);

				sl_qty.setFocusable(false);
				sl_qty.setBackgroundResource(drawable.labelbg);

				sl_foc.setFocusable(false);
				sl_foc.setBackgroundResource(drawable.labelbg);

			}
		}

		Log.d("calCarton", "cc " + calCarton);
		Log.e("priceflagvalues", priceflag+", "+slCartonPerQty);

		if (priceflag.matches("1")) {
			sl_cprice.setVisibility(View.VISIBLE);
			price_txt.setVisibility(View.GONE);
			price_header_layout.setVisibility(View.VISIBLE);
			findViewById(R.id.cartonPrice_layout).setVisibility(View.VISIBLE);
		} else {
			sl_cprice.setVisibility(View.GONE);
			price_txt.setVisibility(View.VISIBLE);
			price_header_layout.setVisibility(View.GONE);
			findViewById(R.id.cartonPrice_layout).setVisibility(View.GONE);
		}
		if (priceflag.matches("1")) {

			if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
					|| slCartonPerQty.matches("")) {
				sl_price.setText("");
				sl_price.setVisibility(View.INVISIBLE);
				txt_price.setVisibility(View.GONE);
			}else{
				sl_price.setVisibility(View.VISIBLE);
				txt_price.setVisibility(View.VISIBLE);
			}

		} else {
			sl_price.setVisibility(View.VISIBLE);
			txt_price.setVisibility(View.VISIBLE);
		}

		getSalesProdArr.clear();
		companyArr.clear();
		searchProductArr.clear();

		Log.d("InvoiceTaxT", SalesOrderSetGet.getCompanytax());
		taxType = SalesOrderSetGet.getCompanytax();
		locationcode = SalesOrderSetGet.getLocationcode();
		cstcode = SalesOrderSetGet.getSuppliercode();
		cstgrpcd = SalesOrderSetGet.getSuppliergroupcode();
		tranblocknegativestock = SalesOrderSetGet.getTranblocknegativestock();
		
		cmpnyCode = SupplierSetterGetter.getCompanyCode();
		// Added New 13.04.2017
		if (taxType != null && !taxType.isEmpty()) {
			if (taxType.matches("I")) {
				sl_total.setVisibility(View.GONE);
				sl_total_inclusive.setVisibility(View.VISIBLE);
			}else{
				sl_total.setVisibility(View.VISIBLE);
				sl_total_inclusive.setVisibility(View.GONE);
			}
		}else{
			sl_total.setVisibility(View.VISIBLE);
			sl_total_inclusive.setVisibility(View.GONE);
		}

		// tranblocknegativestock="1";
		
		
//		AsyncCallWSADDPRD proTask = new AsyncCallWSADDPRD();
//		proTask.execute();
		
		 new GetProduct().execute();
		
//		AsyncCallBARCODE barcodetask = new AsyncCallBARCODE();
//		barcodetask.execute();
		if (FormSetterGetter.isEditPrice()) {
//			pricetag.setVisibility(View.GONE);
			sl_price.setEnabled(true);
			sl_price.setBackgroundResource(drawable.edittext_bg);
			sl_price.setFocusableInTouchMode(true);

			sl_cprice.setEnabled(true);
			sl_cprice.setBackgroundResource(drawable.edittext_bg);
			sl_cprice.setFocusableInTouchMode(true);
		} else {
//			pricetag.setVisibility(View.VISIBLE);
			sl_price.setEnabled(false);
			sl_price.setFocusable(false);
			sl_price.setFocusableInTouchMode(false);
			sl_price.setGravity(Gravity.CENTER);
			sl_price.setBackgroundResource(drawable.labelbg);

			sl_cprice.setEnabled(false);
			sl_cprice.setFocusable(false);
			sl_cprice.setFocusableInTouchMode(false);
			sl_cprice.setGravity(Gravity.CENTER);
			sl_cprice.setBackgroundResource(drawable.labelbg);
		}

		sl_namefield.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				productNameAlert();
			}
		});


		mSpinnerSotBy.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				String spinnerValue = mSpinnerSotBy.getSelectedItem()
						.toString();
				if (spinnerValue.matches("Products")) {
					sortStr = "SortProduct";
					Sorting(sortStr, productList);
				} else if (spinnerValue.matches("Category")) {
					sortStr = "SortCategory";
					Sorting(sortStr, productList);

				} else if (spinnerValue.matches("SubCategory")) {
					sortStr = "SortSubCategory";
					Sorting(sortStr, productList);

				} else {
					sortStr = "Sort By";
					Sorting(sortStr, productList);
				}
				mCategoryText.setText("");
				mCategoryAutoCompleteText.setText("");
				mSubCategoryText.setText("");
				mSubCategoryAutoCompleteText.setText("");
				mProductEd.setText("");
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

		/**
		 * Defining an itemclick event listener for the category
		 * autocompletetextview
		 */
		OnItemClickListener itemClickListenerCatAutoCompleteText = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				Product product = mCategoryArr.get(position);
				mCategoryText.setText(product.getCode());
				mCategoryAutoCompleteText.setText(product.getDescription());

			}

		};
		/**
		 * Defining an itemclick event listener for the subcategory
		 * autocompletetextview
		 */
		OnItemClickListener itemClickListenersubCatAutoCompleteText = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				Product product = mSubCategoryArr.get(position);
				mSubCategoryText.setText(product.getCode());
				mSubCategoryAutoCompleteText.setText(product.getDescription());
			}

		};

		/** Setting the itemclick event listener */
		mCategoryAutoCompleteText
				.setOnItemClickListener(itemClickListenerCatAutoCompleteText);
		mSubCategoryAutoCompleteText
				.setOnItemClickListener(itemClickListenersubCatAutoCompleteText);

		mCategoryAutoCompleteText.setThreshold(1);
		mSubCategoryAutoCompleteText.setThreshold(1);

		findViewById(R.id.search).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (mCategoryAutoCompleteText.length() == 0) {
					// clear category hidden text
					mCategoryText.setText("");
				}
				if (mSubCategoryAutoCompleteText.length() == 0) {
					// clear subcategory hidden text
					mSubCategoryText.setText("");
				}
				String category = mCategoryText.getText().toString();
				String subcategory = mSubCategoryText.getText().toString();
				String product = mProductEd.getText().toString();

				if (category.length() == 0 && subcategory.length() == 0
						&& product.length() == 0) {
					productAdapter.setFilterType("");
					productAdapter.getFilter().filter(null);
				}

				if (category != null && !category.isEmpty()
						&& subcategory.length() == 0 && product.length() == 0) {
					productAdapter.setFilterType("Category");
					productAdapter.getFilter().filter(category);
				} else if (subcategory != null && !subcategory.isEmpty()
						&& category.length() == 0 && product.length() == 0) {
					productAdapter.setFilterType("SubCategory");
					productAdapter.getFilter().filter(subcategory);
				} else if (product != null && !product.isEmpty()
						&& category.length() == 0 && subcategory.length() == 0) {
					productAdapter.setFilterType("Product");
					productAdapter.getFilter().filter(product);
				} else if (category != null && !category.isEmpty()
						&& subcategory != null && !subcategory.isEmpty()
						&& product.length() == 0) {
					productAdapter.setFilterType("Category && SubCategory");
					productAdapter.getFilter().filter(
							category + "," + subcategory);
				} else if (category != null && !category.isEmpty()
						&& product != null && !product.isEmpty()
						&& subcategory.length() == 0) {
					productAdapter.setFilterType("Category && Product");
					productAdapter.getFilter().filter(category + "," + product);
				} else if (subcategory != null && !subcategory.isEmpty()
						&& product != null && !product.isEmpty()
						&& category.length() == 0) {
					productAdapter.setFilterType("SubCategory && Product");
					productAdapter.getFilter().filter(
							subcategory + "," + product);
				} else if (category != null && !category.isEmpty()
						&& subcategory != null && !subcategory.isEmpty()
						&& product != null && !product.isEmpty()) {
					productAdapter
							.setFilterType("Category && SubCategory && Product");
					productAdapter.getFilter().filter(
							category + "," + subcategory + "," + product);
				}

			}
		});

		mProductEd.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mEditValue = mProductEd;
				return false;
			}
		});

		mProductEd.addTextChangedListener(new TextWatcher() {

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

				productAdapter.setFilterType("Product");
				productAdapter.getFilter().filter(s.toString());
			}
		});

		mCategoryAutoCompleteText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mEditValue = mCategoryAutoCompleteText;
				return false;
			}
		});

		mSubCategoryAutoCompleteText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mEditValue = mSubCategoryAutoCompleteText;
				return false;
			}
		});

		swipe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				orientation = getResources().getConfiguration().orientation;

				if (screenInches > 7) {
					if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
						// orderFocQty = 0;
						// orderExchQty = 0;
						// mNetTotalItem.setText("0");
						// Portrait
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
						// ab.setBackgroundDrawable(getResources().getDrawable(
						// R.drawable.home_bg));
						findViewById(R.id.verticalScrollView).setVisibility(
								View.VISIBLE);
						findViewById(R.id.sl_total_layout).setVisibility(
								View.VISIBLE);
						findViewById(R.id.label_total_layout).setVisibility(
								View.VISIBLE);
						findViewById(R.id.relativeLayout).setVisibility(
								View.GONE);

						swipe.setVisibility(View.VISIBLE);
						keyboard.setVisibility(View.GONE);
						pageTitle.setVisibility(View.VISIBLE);
						filter.setVisibility(View.VISIBLE);
						addProduct_screen
								.setBackgroundResource(drawable.tab_select);

						header_portrait.setVisibility(View.GONE);
						header_landscape.setVisibility(View.VISIBLE);
					} else {
						// Landscape
						// ab.setBackgroundDrawable(getResources().getDrawable(
						// R.drawable.header_bg));
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						findViewById(R.id.verticalScrollView).setVisibility(
								View.GONE);
						findViewById(R.id.sl_total_layout).setVisibility(
								View.GONE);
						findViewById(R.id.label_total_layout).setVisibility(
								View.GONE);
						findViewById(R.id.relativeLayout).setVisibility(
								View.VISIBLE);

						swipe.setVisibility(View.VISIBLE);
						keyboard.setVisibility(View.VISIBLE);
						pageTitle.setVisibility(View.GONE);
						filter.setVisibility(View.INVISIBLE);
						addProduct_screen
								.setBackgroundResource(drawable.tab_select);

						header_portrait.setVisibility(View.GONE);
						header_landscape.setVisibility(View.VISIBLE);
					}
				}

			}
		});

		filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				filtercs.filterDialog();

				filtercs.OnFilterCompletionListener(new OnFilterCompletionListener() {

					@Override
					public void onFilterCompleted(String category, String subcategory) {

						catStr = category;
						subCatStr = subcategory;

						Log.d("catStr", "----->" + catStr);
						Log.d("subCatStr", "-->" + subCatStr);

						AsyncCallWSSearchProduct task = new AsyncCallWSSearchProduct();
						task.execute();
					}
				});

			}
		});

		// listing_screen.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// Cursor cursor = SOTDatabase.getCursor();
		// int count = cursor.getCount();
		// if (count > 0) {
		// alertDialog();
		// } else {
		// Intent i = new Intent(InvoiceAddProduct.this,
		// InvoiceHeader.class);
		// startActivity(i);
		// InvoiceAddProduct.this.finish();
		// }
		//
		// }
		// });
		//
		// customer_screen.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// screenInches = displayMetrics();
		// if (screenInches > 7) {
		// if (orderQty > 0 ||orderCQty > 0||orderLQty > 0
		// || orderFocQty > 0 || orderExchQty > 0) {
		// productAdapter.storeProducts("Customer");
		//
		// } else {
		// Intent i = new Intent(InvoiceAddProduct.this,
		// InvoiceCustomer.class);
		// startActivity(i);
		// InvoiceAddProduct.this.finish();
		// }
		// } else {
		// Intent i = new Intent(InvoiceAddProduct.this,
		// InvoiceCustomer.class);
		// startActivity(i);
		// InvoiceAddProduct.this.finish();
		// }
		// }
		// });
		//
		// summary_screen.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// screenInches = displayMetrics();
		// if (screenInches > 7) {
		// if (orderQty > 0 ||orderCQty > 0||orderLQty > 0
		// || orderFocQty > 0 || orderExchQty > 0) {
		// Log.d("db", "db call summ ");
		// productAdapter.storeProducts("Summary");
		//
		// } else {
		//
		// Intent i = new Intent(InvoiceAddProduct.this,
		// InvoiceSummary.class);
		// startActivity(i);
		// InvoiceAddProduct.this.finish();
		// }
		// } else {
		//
		//
		// Intent i = new Intent(InvoiceAddProduct.this,
		// InvoiceSummary.class);
		// startActivity(i);
		// InvoiceAddProduct.this.finish();
		// }
		// }
		// });

		listing_screen_tab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Cursor cursor = SOTDatabase.getCursor();
				int count = cursor.getCount();
				if (count > 0) {
					alertDialog();
				} else {
					Intent i = new Intent(InvoiceAddProduct.this,
							InvoiceHeader.class);
					startActivity(i);
					InvoiceAddProduct.this.finish();
				}

			}
		});

		customer_screen_tab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				screenInches = displayMetrics();
				if (screenInches > 7) {
					if (orderQty > 0 || orderCQty > 0 || orderLQty > 0
							|| orderFocQty > 0 || orderExchQty > 0) {
						productAdapter.storeProducts("Customer");

					} else {
						Intent i = new Intent(InvoiceAddProduct.this,
								InvoiceCustomer.class);
						startActivity(i);
						InvoiceAddProduct.this.finish();
					}
				} else {
					Intent i = new Intent(InvoiceAddProduct.this,
							InvoiceCustomer.class);
					startActivity(i);
					InvoiceAddProduct.this.finish();
				}
			}
		});

		summary_screen_tab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				screenInches = displayMetrics();
				if (screenInches > 7) {
					if (orderQty > 0 || orderCQty > 0 || orderLQty > 0
							|| orderFocQty > 0 || orderExchQty > 0) {

						productAdapter.storeProducts("Summary");

					} else {
						Intent i = new Intent(InvoiceAddProduct.this,
								InvoiceSummary.class);
						startActivity(i);
						InvoiceAddProduct.this.finish();
					}
				} else {
					Intent i = new Intent(InvoiceAddProduct.this,
							InvoiceSummary.class);
					startActivity(i);
					InvoiceAddProduct.this.finish();
				}
			}
		});

		pricetag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				new SOTAdmin(InvoiceAddProduct.this, sl_price, sl_cprice,
						salesproduct_layout);
			}
		});

		productFilterList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {

				sales_prodCode = searchProductArr.get(position).get(
						"ProductCode");
				sl_codefield.setText(sales_prodCode);
//				new CheckProductBarcode().execute();
				loadProgress();
				new AsyncCallSaleProduct().execute();
			}
		});

		sl_addProduct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String qtyStr = sl_qty.getText().toString();
				String focStr = sl_foc.getText().toString();
				String priceStr = sl_price.getText().toString();
				String cpriceStr = sl_cprice.getText().toString();
				String cartonPerQtyStr = sl_cartonPerQty.getText().toString();
				String exQtyStr = sl_exchange.getText().toString();

				double qty = 0,foc =0,exch=0,prices =0,cprices=0,pcs =0;

				if (qtyStr != null && !qtyStr.isEmpty()) {
					qty = Double.parseDouble(qtyStr);
				}
				if (focStr!= null &&!focStr.isEmpty()) {
					foc = Double.parseDouble(focStr);
				}

				if (exQtyStr!= null &&!exQtyStr.isEmpty()) {
					exch = Double.parseDouble(exQtyStr);
				}

				if (priceStr!= null &&!priceStr.isEmpty()) {
					prices = Double.parseDouble(priceStr);
				}
				if (cpriceStr!= null &&!cpriceStr.isEmpty()) {
					cprices = Double.parseDouble(cpriceStr);
				}
				if (cartonPerQtyStr!= null &&!cartonPerQtyStr.isEmpty()) {
					pcs = Double.parseDouble(cartonPerQtyStr);
				}


				if ((str_ssupdate != null) || (str_sscancel != null)) {
					Log.d("MinimumSellingPrice", "" + MinimumSellingPrice);
					double slprice = 0, minSellingPrice = 0,miniCartonSellingPrice=0,slcprice=0,piecePerCarton=0;
					String price = sl_price.getText().toString();
					String cprice = sl_cprice.getText().toString();

					if (!price.matches("")) {
						slprice = Double.parseDouble(price);
					}
					if (!MinimumSellingPrice.matches("")) {
						minSellingPrice = Double
								.parseDouble(MinimumSellingPrice);
					}
					if (!MinimumCartonSellingPrice.matches("")) {
						miniCartonSellingPrice = Double
								.parseDouble(MinimumCartonSellingPrice);
					}
					if (!cprice.matches("")) {
						slcprice = Double.parseDouble(cprice);
					}
					if (slCartonPerQty!=null && !slCartonPerQty.isEmpty() ) {
						piecePerCarton = Double.parseDouble(slCartonPerQty);
					}
					Log.d("slcprice", "cp "+slcprice);

					if (priceflag.matches("1")) {
					/*	if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
								|| slCartonPerQty.matches("")) {
							if (minSellingPrice > slcprice) {
								Toast.makeText(
										InvoiceAddProduct.this,
										"Price must be greater than minimum selling price $ "
												+ MinimumSellingPrice + " pp" + slcprice,
										Toast.LENGTH_LONG).show();
							} else {
								storeInDatabase();
							}
						}else{
							storeInDatabase();
						}*/
						if (miniCartonSellingPrice > slcprice) {
							sl_cprice.requestFocus();
							Toast.makeText(
									InvoiceAddProduct.this,
									"Carton Price must be greater than Minimum carton selling price $ "
											+ MinimumCartonSellingPrice,
									Toast.LENGTH_LONG).show();
						} else if (piecePerCarton>1){
							if(minSellingPrice > slprice) {
								sl_price.requestFocus();
								Toast.makeText(
										InvoiceAddProduct.this,
										"Price must be greater than minimum selling price $ "
												+ MinimumSellingPrice,
										Toast.LENGTH_LONG).show();
							}else{
								if(qty>0){
									if (pcs == 1){
										Log.d("CheckValue",SalesOrderSetGet.getCartonpriceflag());
										if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
											if(cprices !=0){
												storeInDatabase();
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter Carton Price",Toast.LENGTH_SHORT).show();
											}
										}else{
											if(prices !=0){
												storeInDatabase();
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter Price",Toast.LENGTH_SHORT).show();
											}
										}
									}else if (pcs > 1){
										Log.d("column_price2",cprice+","+pcs+"price:"+price);
										if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
											if(cprices !=0 && prices !=0){
												storeInDatabase();
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
											}
										}else{
											if(prices !=0){
												storeInDatabase();
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter Price",Toast.LENGTH_SHORT).show();
											}
										}
									}
								}else if(qty ==0){
									if(foc !=0 || exch!=0){
										storeInDatabase();
									}else{
										Toast.makeText(getApplicationContext(),"Please Enter Quantity",Toast.LENGTH_SHORT).show();
									}
								}

							}
						}

						else {
							if(qty>0){
								if (pcs == 1){
									Log.d("CheckValue",SalesOrderSetGet.getCartonpriceflag());
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if(prices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Price",Toast.LENGTH_SHORT).show();
										}
									}
								}else if (pcs > 1){
									Log.d("column_price2",cprice+","+pcs+"price:"+price);
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0 && prices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if(prices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
										}
									}
								}
							}else if(qty ==0){
								if(foc !=0 || exch!=0){
									storeInDatabase();
								}else{
									Toast.makeText(getApplicationContext(),"Please Enter Quantity",Toast.LENGTH_SHORT).show();
								}
							}
							/*Toast.makeText(InvoiceAddProduct.this, "Updated ",
									Toast.LENGTH_LONG).show();
							Intent i = new Intent(InvoiceAddProduct.this,
									InvoiceSummary.class);
							startActivity(i);
							InvoiceAddProduct.this.finish();*/
						}
					} else {
						if (minSellingPrice > slprice) {
							Toast.makeText(
									InvoiceAddProduct.this,
									"Price must be greater than minimum selling price $ "
											+ MinimumSellingPrice,
									Toast.LENGTH_LONG).show();
						} else {
							if(qty>0){
								if (pcs == 1){
									Log.d("CheckValue",SalesOrderSetGet.getCartonpriceflag());
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if(prices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Price",Toast.LENGTH_SHORT).show();
										}
									}
								}else if (pcs > 1){
									Log.d("column_price2",cprice+","+pcs+"price:"+price);
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0 && prices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if(prices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
										}
									}
								}
							}else if(qty ==0){
								if(foc !=0 || exch!=0){
									storeInDatabase();
								}else{
									Toast.makeText(getApplicationContext(),"Please Enter Quantity",Toast.LENGTH_SHORT).show();
								}
							}
							/*Toast.makeText(InvoiceAddProduct.this, "Updated ",
									Toast.LENGTH_LONG).show();
							Intent i = new Intent(InvoiceAddProduct.this,
									InvoiceSummary.class);
							startActivity(i);
							InvoiceAddProduct.this.finish();*/

						}
					}


				} else {
					Log.d("MinimumSellingPrices", MinimumSellingPrice);
					double slprice = 0,piecePerCarton=0, minSellingPrice = 0,slcprice=0,miniCartonSellingPrice=0;
					String price = sl_price.getText().toString();
					String cprice = sl_cprice.getText().toString();
					if (!price.matches("")) {
						slprice = Double.parseDouble(price);
					}
					if (!MinimumSellingPrice.matches("")) {
						minSellingPrice = Double
								.parseDouble(MinimumSellingPrice);
					}
					if (!cprice.matches("")) {
						slcprice = Double.parseDouble(cprice);
					}
					if (!MinimumCartonSellingPrice.matches("")) {
						miniCartonSellingPrice = Double
								.parseDouble(MinimumCartonSellingPrice);
					}
					if (slCartonPerQty!=null && !slCartonPerQty.isEmpty() ) {
						piecePerCarton = Double.parseDouble(slCartonPerQty);
					}
					if (priceflag.matches("1")) {
						/*if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
								|| slCartonPerQty.matches("")) {
							if (minSellingPrice > slcprice) {
								Toast.makeText(
										InvoiceAddProduct.this,
										"Price must be greater than minimum selling price $ "
												+ MinimumSellingPrice,
										Toast.LENGTH_LONG).show();
							} else {
								storeInDatabase();
							}
						}else{
							storeInDatabase();
						}*/
						if (miniCartonSellingPrice > slcprice) {
							sl_cprice.requestFocus();
							Toast.makeText(
									InvoiceAddProduct.this,
									"Carton Price must be greater than Minimum carton selling price $ "
											+ MinimumCartonSellingPrice,
									Toast.LENGTH_LONG).show();
						} else if (piecePerCarton>1){
							if(minSellingPrice > slprice) {
								sl_price.requestFocus();
								Toast.makeText(
										InvoiceAddProduct.this,
										"Price must be greater than minimum selling price $ "
												+ MinimumSellingPrice,
										Toast.LENGTH_LONG).show();
							}else{
								if(qty>0){
									if (pcs == 1){
										Log.d("CheckValue",SalesOrderSetGet.getCartonpriceflag());
										if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
											if(cprices !=0){
												storeInDatabase();
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter Carton Price",Toast.LENGTH_SHORT).show();
											}
										}else{
											if(prices !=0){
												storeInDatabase();
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter Price",Toast.LENGTH_SHORT).show();
											}
										}
									}else if (pcs > 1){
										Log.d("column_price2",cprice+","+pcs+"price:"+price);
										if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
											if(cprices !=0 && prices !=0){
												storeInDatabase();
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
											}
										}else{
											if(prices !=0){
												storeInDatabase();
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
											}
										}
									}
								}else if(qty ==0){
									if(foc !=0 || exch!=0){
										storeInDatabase();
									}else{
										Toast.makeText(getApplicationContext(),"Please Enter Quantity",Toast.LENGTH_SHORT).show();
									}
								}

							}
						}

						else {
							if(qty>0){
								if (pcs == 1){
									Log.d("CheckValue",SalesOrderSetGet.getCartonpriceflag());
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if(prices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Price",Toast.LENGTH_SHORT).show();
										}
									}
								}else if (pcs > 1){
									Log.d("column_price2",cprice+","+pcs+"price:"+price);
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0 && prices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if(prices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
										}
									}
								}
							}else if(qty ==0){
								if(foc !=0 || exch!=0){
									storeInDatabase();
								}else{
									Toast.makeText(getApplicationContext(),"Please Enter Quantity",Toast.LENGTH_SHORT).show();
								}
							}
						}
					} else {
						if (minSellingPrice > slprice) {
							Toast.makeText(
									InvoiceAddProduct.this,
									"Price must be greater than minimum selling price $ "
											+ MinimumSellingPrice,
									Toast.LENGTH_LONG).show();
						} else {
							if(qty>0){
								if (pcs == 1){
									Log.d("CheckValue",SalesOrderSetGet.getCartonpriceflag());
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if(prices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Price",Toast.LENGTH_SHORT).show();
										}
									}
								}else if (pcs > 1){
									Log.d("column_price2",cprice+","+pcs+"price:"+price);
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0 && prices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if(prices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
										}
									}
								}
							}else if(qty ==0){
								if(foc !=0 || exch!=0){
									storeInDatabase();
								}else{
									Toast.makeText(getApplicationContext(),"Please Enter Quantity",Toast.LENGTH_SHORT).show();
								}
							}

						}
					}
				}

			}
		});

		sl_summary.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(InvoiceAddProduct.this,
						InvoiceSummary.class);
				startActivity(i);
				InvoiceAddProduct.this.finish();

			}
		});

		expand.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (foc_layout.getVisibility() == View.VISIBLE) {
					// Its visible
					foc_layout.setVisibility(View.GONE);
					foc_header_layout.setVisibility(View.GONE);
					pcs_txt_layout.setVisibility(View.GONE);
					pcs_layout.setVisibility(View.GONE);
				} else {
					foc_layout.setVisibility(View.VISIBLE);
					foc_header_layout.setVisibility(View.VISIBLE);
					pcs_txt_layout.setVisibility(View.VISIBLE);
					pcs_layout.setVisibility(View.VISIBLE);
					// Either gone or invisible
				}

			}
		});

		sl_codefield
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						sl_codefield) {
					@Override
					public boolean onDrawableClick() {

						alertDialogSearch();
						return true;

					}
				});

		sl_codefield.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					new CheckProductBarcode().execute();
					return true;
				}
				return false;
			}
		});

		/*sl_cartonQty.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {

					slPrice = sl_price.getText().toString();

					if (calCarton.matches("0")) {
						if (priceflag.matches("0")) {

						} else if (priceflag.matches("1")) {
							productTotalNew();
						}

					} else {
						if (priceflag.matches("0")) {
							cartonQty();
						} else if (priceflag.matches("1")) {
							cartonQtyNew();
						}
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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				ss_Cqty = sl_cartonQty.getText().toString();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				slPrice = sl_price.getText().toString();

				if (calCarton.matches("0")) {

					if (priceflag.matches("0")) {

					} else if (priceflag.matches("1")) {
						productTotalNew();
					}
				} else {

					if (priceflag.matches("0")) {
						cartonQty();
					} else if (priceflag.matches("1")) {
						cartonQtyNew();
					}

					int length = sl_cartonQty.length();
					if (length == 0) {
						if (calCarton.matches("0")) {

						} else {
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
								// ///////////
								if (priceflag.matches("0")) {
									productTotal(lsQty);
								} else if (priceflag.matches("1")) {
									// String cprice =
									// sl_price.getText().toString();
									productTotalNew();
								}
							}
							// //////////
						}
					}
				}

			}
		};

		sl_looseQty.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					slPrice = sl_price.getText().toString();

					if (calCarton.matches("0")) {
						if (priceflag.matches("0")) {

						} else if (priceflag.matches("1")) {
							productTotalNew();
						}

					} else {
						if (priceflag.matches("0")) {
							looseQtyCalc();
						} else if (priceflag.matches("1")) {
							looseQtyCalcNew();
						}
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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				beforeLooseQty = sl_looseQty.getText().toString();

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				slPrice = sl_price.getText().toString();

				if (calCarton.matches("0")) {
					if (priceflag.matches("0")) {

					} else if (priceflag.matches("1")) {
						productTotalNew();
					}

				} else {
					if (priceflag.matches("0")) {
						looseQtyCalc();
					} else if (priceflag.matches("1")) {
						looseQtyCalcNew();
					}

					int length = sl_looseQty.length();
					if (length == 0) {
						if (calCarton.matches("0")) {

						} else {
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
						double qtyCalc = Double.parseDouble(qty);
						// productTotal(qtyCalc);
						if (calCarton.matches("0")) {
							String pcsPerCarton = sl_cartonPerQty.getText()
									.toString();
							if (!pcsPerCarton.matches("")) {
								int pcsPerCartonCalc = Integer
										.parseInt(pcsPerCarton);
								if (pcsPerCartonCalc == 1) {
									productTotal(qtyCalc);
								}
							}

						} else {
							clQty();
						}
					}

					if (priceflag.matches("1")) {

						if (sl_cprice.getText().toString().equals("0.00")
								|| sl_cprice.getText().toString().equals("0")
								|| sl_cprice.getText().toString().equals("0.0")
								|| sl_cprice.getText().toString().equals(".0")
								|| sl_cprice.getText().toString()
										.equals("0.000")
								|| sl_cprice.getText().toString()
										.equals("0.0000")
								|| sl_cprice.getText().toString()
										.equals("0.00000")) {
							sl_cprice.setText("");
						}

						sl_cprice.requestFocus();
					} else {
						if (sl_price.getText().toString().equals("0.00")
								|| sl_price.getText().toString().equals("0")
								|| sl_price.getText().toString().equals("0.0")
								|| sl_price.getText().toString().equals(".0")
								|| sl_price.getText().toString()
										.equals("0.000")
								|| sl_price.getText().toString()
										.equals("0.0000")
								|| sl_price.getText().toString()
										.equals("0.00000")) {
							sl_price.setText("");
						}

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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				slPrice = sl_price.getText().toString();
				String qty = sl_qty.getText().toString();

				if (qty.matches("")) {
					qty = "0";
				}

				if (calCarton.matches("0")) {

					if (priceflag.matches("0")) {

						if (!qty.matches("")) {
							int quantity = Integer.parseInt(qty);
							productTotal(quantity);

							int length = sl_qty.length();
							if (length == 0) {
								if (calCarton.matches("0")) {
									productTotal(0);
								} else {
									productTotal(0);

									sl_cartonQty
											.removeTextChangedListener(cqtyTW);
									sl_cartonQty.setText("");
									sl_cartonQty.addTextChangedListener(cqtyTW);

									sl_looseQty
											.removeTextChangedListener(lqtyTW);
									sl_looseQty.setText("");
									sl_looseQty.addTextChangedListener(lqtyTW);
								}
							}
						}
					} else if (priceflag.matches("1")) {
						String pcsPerCarton = sl_cartonPerQty.getText()
								.toString();
						if (!pcsPerCarton.matches("")) {
							int pcsPerCartonCalc = Integer
									.parseInt(pcsPerCarton);
							if (pcsPerCartonCalc == 1) {

								if (!qty.matches("")) {
									int quantity = Integer.parseInt(qty);
									productTotal(quantity);
								}

							}
						}

						int length = sl_qty.length();
						if (length == 0) {
							if (calCarton.matches("0")) {

							} else {
								productTotal(0);

								sl_cartonQty.removeTextChangedListener(cqtyTW);
								sl_cartonQty.setText("");
								sl_cartonQty.addTextChangedListener(cqtyTW);

								sl_looseQty.removeTextChangedListener(lqtyTW);
								sl_looseQty.setText("");
								sl_looseQty.addTextChangedListener(lqtyTW);
							}
						}

					}

				} else {
					if (!qty.matches("")) {
						clQty();
					}

					int length = sl_qty.length();
					if (length == 0) {
						if (calCarton.matches("0")) {

						} else {
							productTotal(0);

							sl_cartonQty.removeTextChangedListener(cqtyTW);
							sl_cartonQty.setText("");
							sl_cartonQty.addTextChangedListener(cqtyTW);

							sl_looseQty.removeTextChangedListener(lqtyTW);
							sl_looseQty.setText("");
							sl_looseQty.addTextChangedListener(lqtyTW);
						}
					}
				}
			}

		};*/
		
		sl_cartonQty.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (sl_cartonQty.getText().toString().equals("0.00")
						|| sl_cartonQty.getText().toString().equals("0")
						|| sl_cartonQty.getText().toString().equals("0.0")
						|| sl_cartonQty.getText().toString().equals(".0")
						|| sl_cartonQty.getText().toString().equals("0.000")
						|| sl_cartonQty.getText().toString().equals("0.0000")
						|| sl_cartonQty.getText().toString().equals("0.00000")) {
					sl_cartonQty.setText("");
				}
				return false;
			}
		});
		
		sl_cartonQty.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {

					slPrice = sl_price.getText().toString();
					
					if (calCarton.matches("0")) {
						if (priceflag.matches("0")) {
							cartonQtyPcsOne(0);
						} else if (priceflag.matches("1")) {
//							cartonQtyNew();
							cartonQtyPcsOne(1);
						}
					} else {
						if (priceflag.matches("0")) {
							cartonQty();
						} else if (priceflag.matches("1")) {
							cartonQtyNew();
						}
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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				ss_Cqty = sl_cartonQty.getText().toString();
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				slPrice = sl_price.getText().toString();
				Log.e("changevalues", "changevalues");

				if (calCarton.matches("0")) {
					if (priceflag.matches("0")) {
						cartonQtyPcsOne(0);
					} else if (priceflag.matches("1")) {
//						cartonQtyNew();
						cartonQtyPcsOne(1);
					}
					
					int length = sl_cartonQty.length();
					if (length == 0) {
					if (priceflag.matches("0")) {
						cartonQtyPcsOne(0);
					} else if (priceflag.matches("1")) {
//						cartonQtyNew();
						cartonQtyPcsOne(1);
					}
					}
					
				} else {
					if (priceflag.matches("0")) {
						cartonQty();
					} else if (priceflag.matches("1")) {
						cartonQtyNew();
					}

					int length = sl_cartonQty.length();
					if (length == 0) {
						if (calCarton.matches("0")) {
							
						} else {
							String lqty = sl_looseQty.getText().toString();

							if (lqty.matches("")) {
								lqty = "0";
							}

							if (!lqty.matches("")) {
								sl_qty.removeTextChangedListener(qtyTW);
								sl_qty.setText(lqty);
								sl_qty.addTextChangedListener(qtyTW);

								Log.e("changevalues", "changevalues2");

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
				}
			}
		};

		sl_looseQty.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (sl_looseQty.getText().toString().equals("0.00")
						|| sl_looseQty.getText().toString().equals("0")
						|| sl_looseQty.getText().toString().equals("0.0")
						|| sl_looseQty.getText().toString().equals(".0")
						|| sl_looseQty.getText().toString().equals("0.000")
						|| sl_looseQty.getText().toString().equals("0.0000")
						|| sl_looseQty.getText().toString().equals("0.00000")) {
					sl_looseQty.setText("");
				}
				return false;
			}
		});
		
		sl_looseQty.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					slPrice = sl_price.getText().toString();
					
					if (calCarton.matches("0")) {

						if (priceflag.matches("0")) {
							looseQtyCalcPcsOne(0);
						} else if (priceflag.matches("1")) {
//							looseQtyCalcNew();
							looseQtyCalcPcsOne(1);
						}
					}else{

						if (priceflag.matches("0")) {
							looseQtyCalc();
						} else if (priceflag.matches("1")) {
							looseQtyCalcNew();
						}
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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				beforeLooseQty = sl_looseQty.getText().toString();

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				slPrice = sl_price.getText().toString();
				
				if (calCarton.matches("0")) {
					if (priceflag.matches("0")) {
						looseQtyCalcPcsOne(0);
					} else if (priceflag.matches("1")) {
//						looseQtyCalcNew();
						looseQtyCalcPcsOne(1);
					}
				}else{
					if (priceflag.matches("0")) {
						looseQtyCalc();
					} else if (priceflag.matches("1")) {
						looseQtyCalcNew();
					}

				int length = sl_looseQty.length();
				if (length == 0) {
					
					if (calCarton.matches("0")) {
						
					}else{
					String qty = sl_qty.getText().toString();
					if (!beforeLooseQty.matches("") && !qty.matches("")) {

						double qtyCnvrt = Double.parseDouble(qty);
						double lsCnvrt = Double.parseDouble(beforeLooseQty);
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
					}}
				}}
			}

		};

		sl_qty.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (sl_qty.getText().toString().equals("0.00")
						|| sl_qty.getText().toString().equals("0")
						|| sl_qty.getText().toString().equals("0.0")
						|| sl_qty.getText().toString().equals(".0")
						|| sl_qty.getText().toString().equals("0.000")
						|| sl_qty.getText().toString().equals("0.0000")
						|| sl_qty.getText().toString().equals("0.00000")) {
					sl_qty.setText("");
				}
				return false;
			}
		});
		
		sl_qty.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {

					slPrice = sl_price.getText().toString();
					String qty = sl_qty.getText().toString();
					if (!qty.matches("")) {
						double qtyCalc = Double.parseDouble(qty);
						// productTotal(qtyCalc);
						if (calCarton.matches("0")) {
							String pcsPerCarton = sl_cartonPerQty.getText()
									.toString();
							if (!pcsPerCarton.matches("")) {
								double pcsPerCartonCalc = Double.parseDouble(pcsPerCarton);
								if (pcsPerCartonCalc == 1) {
									productTotal(qtyCalc);
								}
							}

						}else{
							Log.d("sl_qty","-->"+qty);
							clQty();
						}											
					}

					if (priceflag.matches("1")) {

						if (sl_cprice.getText().toString().equals("0.00")
								|| sl_cprice.getText().toString().equals("0")
								|| sl_cprice.getText().toString().equals("0.0")
								|| sl_cprice.getText().toString().equals(".0")
								|| sl_cprice.getText().toString()
										.equals("0.000")
								|| sl_cprice.getText().toString()
										.equals("0.0000")
								|| sl_cprice.getText().toString()
										.equals("0.00000")) {
							sl_cprice.setText("");
						}
						sl_cprice.requestFocus();

					} else {
						if (sl_price.getText().toString().equals("0.00")
								|| sl_price.getText().toString().equals("0")
								|| sl_price.getText().toString().equals("0.0")
								|| sl_price.getText().toString().equals(".0")
								|| sl_price.getText().toString()
										.equals("0.000")
								|| sl_price.getText().toString()
										.equals("0.0000")
								|| sl_price.getText().toString()
										.equals("0.00000")) {
							sl_price.setText("");
						}
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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				slPrice = sl_price.getText().toString();
				String qty = sl_qty.getText().toString();
				if(qty.matches("")){
					qty="0";
				}
				
				
				if (!qty.matches("")) {
					
					if (calCarton.matches("0")) {

						if (priceflag.matches("0")) {

							Log.d("priceflag", ""+priceflag);
							
							if (!qty.matches("")) {
								double quantity = Double.parseDouble(qty);
								productTotal(quantity);

								int length = sl_qty.length();
								if (length == 0) {
									if (calCarton.matches("0")) {
										productTotal(0);
									} else {
										productTotal(0);

										sl_cartonQty
												.removeTextChangedListener(cqtyTW);
										sl_cartonQty.setText("");
										sl_cartonQty.addTextChangedListener(cqtyTW);

										sl_looseQty
												.removeTextChangedListener(lqtyTW);
										sl_looseQty.setText("");
										sl_looseQty.addTextChangedListener(lqtyTW);
									}
								}
							}
						} else if (priceflag.matches("1")) {
							String pcsPerCarton = sl_cartonPerQty.getText()
									.toString();
							if (!pcsPerCarton.matches("")) {
								double pcsPerCartonCalc = Double.parseDouble(pcsPerCarton);
								if (pcsPerCartonCalc == 1) {

									if (!qty.matches("")) {
										double quantity = Double.parseDouble(qty);
										productTotal(quantity);
									}

								}
							}

							int length = sl_qty.length();
							if (length == 0) {
								if (calCarton.matches("0")) {

								} else {
									productTotal(0);

									sl_cartonQty.removeTextChangedListener(cqtyTW);
									sl_cartonQty.setText("");
									sl_cartonQty.addTextChangedListener(cqtyTW);

									sl_looseQty.removeTextChangedListener(lqtyTW);
									sl_looseQty.setText("");
									sl_looseQty.addTextChangedListener(lqtyTW);
								}
							}

						}

					
					}else{
						
						Log.d("else", ""+qty);
						
						if (!qty.matches("")) {
							clQty();
						}

						int length = sl_qty.length();
						if (length == 0) {
							if (calCarton.matches("0")) {

							} else {
								productTotal(0);

								sl_cartonQty.removeTextChangedListener(cqtyTW);
								sl_cartonQty.setText("");
								sl_cartonQty.addTextChangedListener(cqtyTW);

								sl_looseQty.removeTextChangedListener(lqtyTW);
								sl_looseQty.setText("");
								sl_looseQty.addTextChangedListener(lqtyTW);
							}
						}
					}
					
				}

			}

		};

		sl_foc.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {

					sl_exchange.requestFocus();
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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

		});

		sl_price.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				slPrice = sl_price.getText().toString();
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					String qty = sl_qty.getText().toString();

					if (calCarton.matches("0")) {
						if (priceflag.matches("0")) {
							if (!qty.matches("")) {
								int quantity = Integer.parseInt(qty);
								productTotal(quantity);
							}
						} else if (priceflag.matches("1")) {
							productTotalNew();
						}
					} else {
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

					sl_foc.requestFocus();
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
						|| sl_price.getText().toString().equals(".0")
						|| sl_price.getText().toString().equals("0.000")
						|| sl_price.getText().toString().equals("0.0000")
						|| sl_price.getText().toString().equals("0.00000")) {
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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				slPrice = sl_price.getText().toString();
				String qty = sl_qty.getText().toString();

				if (calCarton.matches("0")) {
					if (priceflag.matches("0")) {
						if (!qty.matches("")) {

							double quantity = Double.parseDouble(qty);
							productTotal(quantity);
						}
					} else if (priceflag.matches("1")) {
						productTotalNew();
					}
				} else {

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
			}

		});

		// ///////////
		sl_cprice.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {

					String qty = sl_qty.getText().toString();

					if (sl_price.getText().toString().equals("0.00")
							|| sl_price.getText().toString().equals("0")
							|| sl_price.getText().toString().equals("0.0")
							|| sl_price.getText().toString().equals(".0")
							|| sl_price.getText().toString().equals("0.000")
							|| sl_price.getText().toString().equals("0.0000")
							|| sl_price.getText().toString().equals("0.00000")) {
						sl_price.setText("");
					}

					if (calCarton.matches("0")) {
						if (priceflag.matches("0")) {

						} else if (priceflag.matches("1")) {
							productTotalNew();
						}
					} else {

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
						|| sl_cprice.getText().toString().equals(".0")
						|| sl_cprice.getText().toString().equals("0.000")
						|| sl_cprice.getText().toString().equals("0.0000")
						|| sl_cprice.getText().toString().equals("0.00000")) {
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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String qty = sl_qty.getText().toString();

				if (calCarton.matches("0")) {
					if (priceflag.matches("0")) {

					} else if (priceflag.matches("1")) {
						productTotalNew();
					}
				} else {

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
			}

		});

		// /////////////

		sl_itemDiscount.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
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

		sl_itemDiscount.addTextChangedListener(new TextWatcher() {

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
	}

	/*public void clQty() {
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
	}*/
	
	public void clQty() {
		try{
		String qty = sl_qty.getText().toString();
		
		Log.d("qty recalc", ""+qty);
		
		String crtnperQty = sl_cartonPerQty.getText().toString();
		double q = 0, r = 0;

		if (crtnperQty.matches("0") || crtnperQty.matches("null")
				|| crtnperQty.matches("0.00")) {
			crtnperQty = "1";
		}
 
		if (!crtnperQty.matches("")) {
			if (!qty.matches("")) {
				try {
					double qty_nt = Double.parseDouble(qty);
					double pcs_nt = Double.parseDouble(crtnperQty);

					Log.d("qty_nt", "" + qty_nt);
					Log.d("pcs_nt", "" + pcs_nt);

					q = (int)(qty_nt / pcs_nt);
					r = (qty_nt % pcs_nt);

					Log.d("cqty", "" + q);
					Log.d("lqty", "" + r);

					String ctn = twoDecimalPoint(q);
					String loose = twoDecimalPoint(r);
					
					sl_cartonQty.removeTextChangedListener(cqtyTW);
					sl_cartonQty.setText("" + ctn);
					sl_cartonQty.addTextChangedListener(cqtyTW);

					sl_looseQty.removeTextChangedListener(lqtyTW);
					sl_looseQty.setText("" + loose);
					sl_looseQty.addTextChangedListener(lqtyTW);
									
//					productTotal(qty_nt);
					if (priceflag.matches("0")){
						productTotal(qty_nt);
					}else{
						productTotalNew();
					}

				} catch (ArithmeticException e) {
					System.out.println("Err: Divided by Zero");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		}catch(NumberFormatException e){
			e.printStackTrace();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/*public void cartonQty() {
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
	}*/
	
	public void cartonQty() {
		try{
		String crtnQty = sl_cartonQty.getText().toString();

		if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

			double cartonQtyCalc = Double.parseDouble(crtnQty);
			double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
			double qty = 0;

			String lsQty = sl_looseQty.getText().toString();

			if (!lsQty.matches("")) {
				double lsQtyCnvrt = Double.parseDouble(lsQty);
				qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

			} else {
				qty = cartonQtyCalc * cartonPerQtyCalc;
			}
			
			String quantity = twoDecimalPoint(qty);
			
			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + quantity);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			productTotal(qty);
		}
		}catch(NumberFormatException e){
			e.printStackTrace();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void cartonQtyPcsOne(int priceFlag) {
		try{		
			
		String crtnQty = sl_cartonQty.getText().toString();

		if (crtnQty.matches("")){
			crtnQty="0";
		}
		
		if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

			double cartonQtyCalc = Double.parseDouble(crtnQty);
			double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
			double qty = 0;
			double ProductWeight=0,lsQtyCnvrt=0;
			String lsQty = sl_looseQty.getText().toString();

		/*	if (!lsQty.matches("")) {
				double lsQtyCnvrt = Double.parseDouble(lsQty);
				qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

			} else {
				qty = cartonQtyCalc * cartonPerQtyCalc;
			}*/
			
			if (!Weight.matches("")) {
				ProductWeight = Double.parseDouble(Weight);
			}else{
				ProductWeight=0;
			}
			
			if (!lsQty.matches("")) {
				lsQtyCnvrt = Double.parseDouble(lsQty);
			}else{
				lsQtyCnvrt=0;
			}
			
			if (cartonPerQtyCalc > 1) {
				
					 if (ProductWeight > 0) {
						 qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
					 }else{
						 qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
					 }					
		   
            }
            else {
                if (ProductWeight > 0) {
                	qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
                } else {
                	
                }
            }
			
			
			String quantity = twoDecimalPoint(qty);
			
			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + quantity);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			if(priceFlag==0){
				productTotal(qty);
			}else{
				productTotalNew();
			}
			
		}
		}catch(NumberFormatException e){
			e.printStackTrace();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void looseQtyCalcPcsOne(int priceFlag) {
		try{
		String crtnQty = sl_cartonQty.getText().toString();
		String lsQty = sl_looseQty.getText().toString();

		if (lsQty.matches("")) {
			lsQty = "0";
		}

		if (!slCartonPerQty.matches("") && !crtnQty.matches("")
				&& !lsQty.matches("")) {

			double cartonQtyCalc = Double.parseDouble(crtnQty);
			double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
			double looseQtyCalc = Double.parseDouble(lsQty);
			double qty=0;
			double ProductWeight=0,lsQtyCnvrt=0;
//			qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
			
			if (!Weight.matches("")) {
				ProductWeight = Double.parseDouble(Weight);
			}else{
				ProductWeight=0;
			}
			
			if (!lsQty.matches("")) {
				lsQtyCnvrt = Double.parseDouble(lsQty);
			}else{
				lsQtyCnvrt=0;
			}
			
			if (cartonPerQtyCalc > 1) {			
					 if (ProductWeight > 0) {
						 qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
					 }else{
						 qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
					 }							   
            }
            else {
                if (ProductWeight > 0) {
                	qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
                } else {
                	
                }
            }

			String quantity = twoDecimalPoint(qty);
			
			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + quantity);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			if(priceFlag==0){
				productTotal(qty);
			}else{
				productTotalNew();
			}
		}

		if (!lsQty.matches("")) {

			
			double looseQtyCalc = Double.parseDouble(lsQty);
			double qty=0;
			double ProductWeight=0,lsQtyCnvrt=0;

			/*if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
				double cartonQtyCalc = Double.parseDouble(crtnQty);
				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);

				qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
			} else {
				qty = looseQtyCalc;
			}*/
			
			if (!Weight.matches("")) {
				ProductWeight = Double.parseDouble(Weight);
			}else{
				ProductWeight=0;
			}
			
			if (!lsQty.matches("")) {
				lsQtyCnvrt = Double.parseDouble(lsQty);
			}else{
				lsQtyCnvrt=0;
			}
			
			if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
				double cartonQtyCalc = Double.parseDouble(crtnQty);
				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
				
				
			if (cartonPerQtyCalc > 1) {
				
					 if (ProductWeight > 0) {
						 qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
					 }else{
						 qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
					 }					
		   
            }
            else {
                if (ProductWeight > 0) {
                	qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
                } else {
                	
                }
            }
			}else{
				qty = looseQtyCalc* ProductWeight;
			}
			String quantity = twoDecimalPoint(qty);
			
			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + quantity);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			if(priceFlag==0){
				productTotal(qty);
			}else{
				productTotalNew();
			}
		}
		}catch(NumberFormatException e){
			e.printStackTrace();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void cartonQtyNew() {
		try{
		String crtnQty = sl_cartonQty.getText().toString();

		if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

			double cartonQtyCalc = Double.parseDouble(crtnQty);
			double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
			double qty = 0;

			String lsQty = sl_looseQty.getText().toString();

			if (!lsQty.matches("")) {
				double lsQtyCnvrt = Double.parseDouble(lsQty);
				qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

			} else {
				qty = cartonQtyCalc * cartonPerQtyCalc;
			}
			
			String quantity = twoDecimalPoint(qty);
			
			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + quantity);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			productTotalNew();
		}}catch(NumberFormatException e){
			e.printStackTrace();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	
	
	
	public void looseQtyCalc() {
		try{
		String crtnQty = sl_cartonQty.getText().toString();
		String lsQty = sl_looseQty.getText().toString();

		if (lsQty.matches("")) {
			lsQty = "0";
		}

		if (!slCartonPerQty.matches("") && !crtnQty.matches("")
				&& !lsQty.matches("")) {

			double cartonQtyCalc = Double.parseDouble(crtnQty);
			double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
			double looseQtyCalc = Double.parseDouble(lsQty);
			double qty;

			qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

			String quantity = twoDecimalPoint(qty);
			
			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + quantity);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			productTotal(qty);
		}

		if (!lsQty.matches("")) {

			double looseQtyCalc = Double.parseDouble(lsQty);
			double qty;

			if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
				double cartonQtyCalc = Double.parseDouble(crtnQty);
				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);

				qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
			} else {
				qty = looseQtyCalc;
			}
			
			String quantity = twoDecimalPoint(qty);
			
			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + quantity);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			productTotal(qty);
		}
		}catch(NumberFormatException e){
			e.printStackTrace();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public void looseQtyCalcNew() {
		try{
		String crtnQty = sl_cartonQty.getText().toString();
		String lsQty = sl_looseQty.getText().toString();

		if (lsQty.matches("")) {
			lsQty = "0";
		}

		if (!slCartonPerQty.matches("") && !crtnQty.matches("")
				&& !lsQty.matches("")) {

			double cartonQtyCalc = Double.parseDouble(crtnQty);
			double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
			double looseQtyCalc = Double.parseDouble(lsQty);

			double qty;
			qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

			String quantity = twoDecimalPoint(qty);
			
			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + quantity);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			productTotalNew();
		}

		if (!lsQty.matches("")) {

			double looseQtyCalc = Double.parseDouble(lsQty);
			double qty;

			if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
				double cartonQtyCalc = Double.parseDouble(crtnQty);
				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);

				qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
			} else {
				qty = looseQtyCalc;
			}
			
			String quantity = twoDecimalPoint(qty);
			
			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + quantity);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			productTotalNew();
		}
		}catch(NumberFormatException e){
			e.printStackTrace();
		
		}catch(Exception e){
			e.printStackTrace();
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
				sl_total_inclusive.setText("" + Prodtotal);
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


						double dTotalIncl = netTotal - taxAmount;
						String totalIncl = twoDecimalPoint(dTotalIncl);
						Log.d("totalIncl", "" + totalIncl);
						sl_total_inclusive.setText(totalIncl);

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
				} else {
					sl_tax.setText("0.0");
					sl_netTotal.setText("" + Prodtotal);
				}
			}

		} catch (Exception e) {

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
                sl_total_inclusive.setText("" + Prodtotal);

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


						double dTotalIncl = netTotal - taxAmount;
						String totalIncl = twoDecimalPoint(dTotalIncl);
						Log.d("totalIncl", "" + totalIncl);
						sl_total_inclusive.setText(totalIncl);

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
				} else {
					sl_tax.setText("0.0");
					sl_netTotal.setText("" + Prodtotal);
				}
			}

		} catch (Exception e) {

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
				Log.e("ProductTot", Prodtotal);

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
				sl_total_inclusive.setText("" + sbTtl);

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


							double dTotalIncl = netTotal1 - taxAmount1;
							String totalIncl = twoDecimalPoint(dTotalIncl);
							Log.d("totalIncl", "" + totalIncl);
							sl_total_inclusive.setText(totalIncl);

						} else {
							taxAmount = (tt * taxValueCalc)
									/ (100 + taxValueCalc);
							String prodTax = fourDecimalPoint(taxAmount);
							sl_tax.setText("" + prodTax);

							// netTotal = tt + taxAmount;
							netTotal = tt;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);


							double dTotalIncl = netTotal - taxAmount;
							String totalIncl = twoDecimalPoint(dTotalIncl);
							Log.d("totalIncl", "" + totalIncl);
							sl_total_inclusive.setText(totalIncl);
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
				} else {
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
			sl_total_inclusive.setText("" + sbTtl);

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


						double dTotalIncl = netTotal1 - taxAmount1;
						String totalIncl = twoDecimalPoint(dTotalIncl);
						Log.d("totalIncl", "" + totalIncl);
						sl_total_inclusive.setText(totalIncl);
					} else {
						taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
						String prodTax = fourDecimalPoint(taxAmount);
						sl_tax.setText("" + prodTax);

						// netTotal = tt + taxAmount;
						netTotal = tt;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);


						double dTotalIncl = netTotal - taxAmount;
						String totalIncl = twoDecimalPoint(dTotalIncl);
						Log.d("totalIncl", "" + totalIncl);
						sl_total_inclusive.setText(totalIncl);
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
			} else {
				sl_tax.setText("0.0");
				sl_netTotal.setText("" + Prodtotal);
			}

		} catch (Exception e) {

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

	public void storeInDatabase() {

		Log.d("Total value ", "" + tt);

		cursor = SOTDatabase.getCursor();

		if (cursor != null && cursor.getCount() > 0) {
			sl_no = cursor.getCount();
			sl_no++;
		}

		String codeStr = sl_codefield.getText().toString();
		String nameStr = sl_namefield.getText().toString();
		String cartonQtyStr = sl_cartonQty.getText().toString();
		String looseQtyStr = sl_looseQty.getText().toString();
		String qtyStr = sl_qty.getText().toString();
		String focStr = sl_foc.getText().toString();
		String priceStr = sl_price.getText().toString();
		String dicountStr = sl_itemDiscount.getText().toString();
		String uomStr = sl_uom.getText().toString();
		String cartonPerQtyStr = sl_cartonPerQty.getText().toString();
		String totalStr = sl_total.getText().toString();
		String taxStr = sl_tax.getText().toString();
		String netTotalStr = sl_netTotal.getText().toString();
		String cpriceStr = sl_cprice.getText().toString();
		String exQtyStr = sl_exchange.getText().toString();
		String stock = sl_stock.getText().toString();
		String slret = sl_ret.getText().toString();
		
		int exch=0;
		double price = 0, discount = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0, update_qty = 0, update_looseQty = 0,
				cartonQty = 0, looseQty = 0, qty = 0,cartonPerQty = 0,foc = 0;
		String sbTtl = "";
		String netT = "";
		
		double stock_avail = 0, qty_entered = 0,tot_qty=0;
		if (stock != null && !stock.isEmpty()) {
			stock_avail = Double.parseDouble(stock);
		}
		if (qtyStr != null && !qtyStr.isEmpty()) {
			qty_entered = Double.parseDouble(qtyStr);
		}
		if (focStr!= null &&!focStr.isEmpty()) {
			foc = Double.parseDouble(focStr);
		}
		
		if (exQtyStr!= null &&!exQtyStr.isEmpty()) {
			exch = Integer.parseInt(exQtyStr);
		}
		
		Log.d("stock_avail", "" + stock_avail);
		Log.d("qty_entered", "" + qty_entered);
		
		tot_qty = qty_entered + foc + exch;
		
		Log.d("tot_qty", "" + tot_qty);
		
		String tranblocknegativestock = SalesOrderSetGet
				.getTranblocknegativestock();
		// tranblocknegativestock="1";
		if (codeStr.matches("")) {
			Toast.makeText(InvoiceAddProduct.this, "Select product code",
					Toast.LENGTH_SHORT).show();
		} else if (calCarton.matches("1") && qtyStr.matches("")
				&& focStr.matches("") && exQtyStr.matches("")) {
			Toast.makeText(InvoiceAddProduct.this, "Enter the quantity",
					Toast.LENGTH_SHORT).show();

		} else if (calCarton.matches("0") && cartonQtyStr.matches("")
				&& looseQtyStr.matches("") && qtyStr.matches("")) {
			Toast.makeText(InvoiceAddProduct.this, "Enter the carton/quantity",
					Toast.LENGTH_SHORT).show();
		}

		else if (tranblocknegativestock != null
				&& !tranblocknegativestock.isEmpty()
				&& tranblocknegativestock.matches("1")
				&& stock_avail < tot_qty) {

			Toast.makeText(InvoiceAddProduct.this, "Low Stock " + stock,
					Toast.LENGTH_SHORT).show();

		} else {
			if (!searchProductArr.isEmpty()) {
				carton_layout.setVisibility(View.GONE);
			}
			
			/*if (!cartonQtyStr.matches("")) {
				cartonQty = Integer.parseInt(cartonQtyStr);
			}*/
			if (!cartonQtyStr.matches("")) {
				cartonQty = Double.parseDouble(cartonQtyStr);
			}
			if (!looseQtyStr.matches("")) {

				if ((str_ssupdate != null) || (str_sscancel != null)) {
					update_looseQty = Double.parseDouble(looseQtyStr);
				} else {
//					looseQty = Integer.parseInt(looseQtyStr);
					looseQty = Double.parseDouble(looseQtyStr);
				}

			}
			if (!qtyStr.matches("")) {

				if ((str_ssupdate != null) || (str_sscancel != null)) {
					update_qty = Double.parseDouble(qtyStr);
				} else {
//					qty = Integer.parseInt(qtyStr);
					qty = Double.parseDouble(qtyStr);
				}

			}
			
			/*if (!cartonPerQtyStr.matches("")) {
				cartonPerQty = Integer.parseInt(cartonPerQtyStr);
			}*/
			if (!cartonPerQtyStr.matches("")) {
				cartonPerQty = Double.parseDouble(cartonPerQtyStr);
			}
			if (!priceStr.matches("")) {
				price = Double.parseDouble(priceStr);
			}

		/*	if(!priceStr.matches("")){
				cartonPerQty = Double.parseDouble(cartonPerQtyStr);
				if(cartonPerQty==1){
					price = Double.parseDouble(cpriceStr);
					Log.e("Pricevalue2", String.valueOf(price));
				} else {
					price = Double.parseDouble(priceStr);
					Log.e("Pricevalue3", String.valueOf(price));
				}
			} else {
				cartonPerQty = Double.parseDouble(cartonPerQtyStr);
				if(cartonPerQty==1){
					price = Double.parseDouble(cpriceStr);
					Log.e("Pricevalue2", String.valueOf(price));
				} else {
					price = Double.parseDouble(priceStr);
					Log.e("Pricevalue3", String.valueOf(price));
				}
			}*/

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
				total = Double.parseDouble(totalStr);

				String itemDisc = sl_itemDiscount.getText().toString();
				if (!itemDisc.matches("")) {
					itmDisc = Double.parseDouble(itemDisc);
					subTotal = total;
				} else {
					subTotal = total;
				}

				sbTtl = twoDecimalPoint(subTotal);

			}
			if (!taxStr.matches("")) {
				tax = Double.parseDouble(taxStr);
			}

			if (!netTotalStr.matches("")) {

				if (taxType != null && !taxType.isEmpty()) {
					if (taxType.matches("I") || taxType.matches("Z")) {
						ntTot = subTotal;
					} else {
						ntTot = subTotal + tax;
					}
				} else {
					ntTot = subTotal + tax;
				}

				netT = twoDecimalPoint(ntTot);
			}
			if (newPrice.matches("") || newPrice == null) {
				newPrice = "0";
			}
			if (taxValue.matches("") || taxValue == null) {
				taxValue = "0";
			}

			if (priceflag.matches("0")) {
				itemDiscountCalc();
			} else if (priceflag.matches("1")) {
				itemDiscountCalcNew();
			}

			String disctStr = sl_itemDiscount.getText().toString();
			if (!disctStr.matches("")) {
				discount = Double.parseDouble(disctStr);
			}
			String totl = twoDecimalPoint(tt);
			Log.d("total" + tt, totl);

			double dis = 0.0;
			if (!dicountStr.matches("")) {
				dis = Double.parseDouble(dicountStr);
			}
			if ((str_ssupdate != null) || (str_sscancel != null)) {
				int i_sssno = Integer.parseInt(str_sssno);

				if (!totalStr.matches("")) {
					SOTDatabase.updateBillDisc(i_sssno, codeStr, sbTtl);
				}

				double c_price = Double.parseDouble(cpriceStr);
				double e_qty = Double.parseDouble(exQtyStr);
				SOTDatabase.updateProductForInvoice(codeStr, nameStr,
						cartonQty, update_looseQty, update_qty, foc, price,
						discount, uomStr, cartonPerQty, tt + dis, tax, sbTtl,
						netT, i_sssno, cpriceStr, exQtyStr,slret);


				// check offline pending

				double oldQ = 0.0;

				if (!oldQty.matches("")) {
					oldQ = Double.parseDouble(oldQty);
				}

				String locationCode = SalesOrderSetGet.getLocationcode();

				Log.d("locationCode", locationCode);

				double getQty = OfflineDatabase.GetQty(locationCode, codeStr);
				//
				Log.d("getQty", "qty aa " + getQty);

				double excQty = 0, stockQty = 0;

				if (!exQtyStr.matches("")) {
					excQty = Double.parseDouble(exQtyStr);
				}

				stockQty = (getQty + oldQ) - (update_qty + foc + excQty);

				Log.d("stockQty", "stockqty aa " + stockQty);

				String editinvoiceno = ConvertToSetterGetter.getEdit_inv_no();

				if (!editinvoiceno.matches("")) {

					HashMap<String, String> hash = new HashMap<String, String>();

					Log.d("procode", codeStr);

					hash.put("InvoiceNo", editinvoiceno);
					hash.put("ProductCode", codeStr);
					hash.put("FOCQty", "" + 0);
					hash.put("Qty", "" + stockQty);
					hash.put("ExchangeQty", "" + 0);

					invoiceEditArr = OfflineSetterGetter.getStockUpdateArr();

					Log.d("invoiceEdit array",
							"" + invoiceEditArr.size());

					if (invoiceEditArr.size() > 0) {
						for (int i = 0; i < invoiceEditArr.size(); i++) {
							HashMap<String, String> datavalue = invoiceEditArr
									.get(i);

							String pcode = datavalue.get("ProductCode");
							String sQty = datavalue.get("Qty");
							Log.d("sQty", " ss " + sQty);
							double dsQty = 0.00;

							if (!sQty.matches("")) {
								dsQty = Double.parseDouble(sQty);
							}

							if (pcode.matches(codeStr)) {
								double stkQty = 0.0;
								stkQty = (dsQty + oldQ)
										- (update_qty + foc + excQty);
								Log.d("stkQty", " aa " + stkQty);
								datavalue.remove("Qty");
								datavalue.put("Qty", stkQty + "");
								break;
							} else {
								int size = (invoiceEditArr.size() - 1);
								Log.d("i, size", i + ".." + size);
								if (size == i) {
									Log.d("stockQty", " bb " + stockQty);
									invoiceEditArr.add(hash);
									break;
								}
							}
						}

					} else {
						invoiceEditArr.add(hash);
					}
					OfflineSetterGetter.setStockUpdateArr(invoiceEditArr);

					Log.d("invoiceEditArr",
							"" + OfflineSetterGetter.getStockUpdateArr());

					// OfflineDatabase.updateProductStock(invoiceDetails);

				}

			} else {
				if (SOTDatabase.getCursor() != null
						&& SOTDatabase.getCursor().getCount() > 0) {

					productcode = SOTDatabase.getProductCode();
					Log.d("ProductCode", "" + productcode);
					for (String prodcode : productcode) {
						if (prodcode.matches(sl_codefield.getText().toString())) {
							prdcode = true;
							Log.d("ProductCode", prodcode);
							break;
						}
					}
					if (prdcode == true) {

						productCodeFilter(sl_no, codeStr, nameStr, cartonQty,
								looseQty, qty, foc, price, discount, uomStr,
								cartonPerQty, tt + dis, tax, netT, taxType,
								taxValue, newPrice, sbTtl, cpriceStr, exQtyStr);
						Log.d("sotdb2", "matches");
						prdcode = false;

					} else {

						if (!totalStr.matches("")) {
							SOTDatabase.storeBillDisc(sl_no, codeStr, sbTtl);
						}
						double c_price = Double.parseDouble(cpriceStr);
						double e_qty = Double.parseDouble(exQtyStr);
						SOTDatabase.storeProduct(sl_no, codeStr, nameStr,
								cartonQty, looseQty, qty, foc, price, discount,
								uomStr, cartonPerQty, tt + dis, tax, netT,
								taxType, taxValue, newPrice, sbTtl, cpriceStr,
								exQtyStr, MinimumSellingPrice, "", slret, "",MinimumCartonSellingPrice,"","");
						Log.d("sotdb3", "original");



					}
				} else {

					if (!totalStr.matches("")) {
						SOTDatabase.storeBillDisc(sl_no, codeStr, sbTtl);
					}

					double c_price = Double.parseDouble(cpriceStr);
					double e_qty = Double.parseDouble(exQtyStr);
					SOTDatabase.storeProduct(sl_no, codeStr, nameStr,
							cartonQty, looseQty, qty, foc, price, discount,
							uomStr, cartonPerQty, tt + dis, tax, netT, taxType,
							taxValue, newPrice, sbTtl, cpriceStr, exQtyStr,
							MinimumSellingPrice, "", slret, "",MinimumCartonSellingPrice,"","");
					Log.d("sotdb4", "original");

				}

				Log.d("CPRICE", cpriceStr);

				sl_codefield.setText("");
				sl_namefield.setText("");
				sl_cartonQty.setText("");
				sl_looseQty.setText("");
				sl_qty.setText("");
				sl_foc.setText("");
				sl_itemDiscount.setText("");
				sl_total.setText("0");
				sl_total_inclusive.setText("0");
				sl_tax.setText("0");
				sl_netTotal.setText("0");

				sl_price.setText("");
				sl_uom.setText("");
				sl_stock.setText("");
				sl_cartonPerQty.setText("");

				sl_cprice.setText("");
				sl_exchange.setText("");

				sl_codefield.requestFocus();

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(sl_codefield.getWindowToken(), 0);
				// imm.showSoftInput(sl_codefield,
				// InputMethodManager.SHOW_IMPLICIT);

				sl_cartonQty.setEnabled(true);
				sl_cartonQty.setFocusable(true);
				sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

				sl_looseQty.setEnabled(true);
				sl_looseQty.setFocusable(true);
				sl_looseQty.setBackgroundResource(drawable.edittext_bg);
				// Toast.makeText(SalesAddProduct.this, "Stored in database",
				// Toast.LENGTH_LONG).show();
			}

			if (!searchProductArr.isEmpty()) {
				adapter.notifyDataSetChanged();
			}
			
			if ((str_ssupdate != null) || (str_sscancel != null)) {
				Toast.makeText(InvoiceAddProduct.this, "Updated ",
						Toast.LENGTH_LONG).show();
				Intent i = new Intent(InvoiceAddProduct.this,
				InvoiceSummary.class);
				startActivity(i);
				InvoiceAddProduct.this.finish();
			}
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

/*	public void editCodeField() {

		if (sl_codefield.getText().toString() != ""
				&& sl_codefield.getText().length() != 0) {

			ArrayList<String> mergeResult = new ArrayList<String>();
			mergeResult.addAll(alprodcode);
			mergeResult.addAll(albar);
			boolean res = false;
			for (String alphabet : mergeResult) {
				if (alphabet.toLowerCase().equals(
						sl_codefield.getText().toString().toLowerCase())
						|| alphabet.toLowerCase()
								.equals(sl_codefield.getText().toString()
										.toLowerCase())) {
					res = true;
					break;
				}
			}

			if (res == true) {
				sl_cartonQty.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(sl_cartonQty,
						InputMethodManager.SHOW_IMPLICIT);

				Iterator<Entry<String, String>> a = hashmap.entrySet()
						.iterator();
				while (a.hasNext()) {
					@SuppressWarnings("rawtypes")
					Map.Entry amapEntry = a.next();
					String keyValue = (String) amapEntry.getKey();
					String value = (String) amapEntry.getValue();

					if (sl_codefield.getText().toString().toLowerCase()
							.contains(keyValue.toLowerCase())) {

						sl_namefield.setText(value);

						sales_prodCode = keyValue;
					}
					for (int i = 0; i < albarcode.size(); i++) {
						HashMap<String, String> barhm = new HashMap<String, String>();
						barhm = albarcode.get(i);
						Iterator<Entry<String, String>> b = barhm.entrySet()
								.iterator();
						while (b.hasNext()) {
							@SuppressWarnings("rawtypes")
							Map.Entry bmapEntry = b.next();
							String keybar = (String) bmapEntry.getKey();
							String valuebar = (String) bmapEntry.getValue();
							if (sl_codefield.getText().toString()
									.equals(valuebar)) {
								sl_codefield.setText(keybar);
								sl_namefield.setText(value);
								sales_prodCode = keybar;
							}
						}
					}
				}

				AsyncCallSaleProduct task = new AsyncCallSaleProduct();
				task.execute();
			}

			else {
				sl_namefield.requestFocus();
				Toast.makeText(getApplicationContext(), " Invalid Code ",
						Toast.LENGTH_SHORT).show();
				sl_codefield.setText("");
				sl_namefield.setText("");

			}

			sl_codefield.addTextChangedListener(new TextWatcher() {
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
					textlength = sl_codefield.getText().length();
					sl_namefield.setText("");
				}
			});
		} else {
			sl_namefield.requestFocus();
			Toast.makeText(getApplicationContext(), " Enter Code ",
					Toast.LENGTH_SHORT).show();
		}

	}*/

	public void alertDialogSearch() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(InvoiceAddProduct.this);
		editText = new EditText(InvoiceAddProduct.this);
		final ListView listview = new ListView(InvoiceAddProduct.this);
		LinearLayout layout = new LinearLayout(InvoiceAddProduct.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Product");
		editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0, 0, 0);
		layout.addView(editText);
		layout.addView(listview);
		myDialog.setView(layout);
		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					myalertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		arrayAdapterProd = new CustomAlertAdapterProd(InvoiceAddProduct.this, al);
		listview.setAdapter(arrayAdapterProd);
		listview.setOnItemClickListener(InvoiceAddProduct.this);

		searchResults = new ArrayList<HashMap<String, String>>(al);
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				/*textlength = editText.getText().length();
				searchResults.clear();
				for (int i = 0; i < al.size(); i++) {
					String supplierName = al.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
					//	if(editText.getText().toString().matches(supplierName))
							searchResults.add(al.get(i));
					}
				}

				arrayAdapterProd = new CustomAlertAdapterProd(
						InvoiceAddProduct.this, searchResults);
				listview.setAdapter(arrayAdapterProd);*/

				textlength = editText.getText().length();
				searchResults.clear();
				String edit=editText.getText().toString().toLowerCase();

				for (int i = 0; i < al.size(); i++) {
					HashMap<String,String> supplierName = al.get(i);
					if(supplierName!=null){
						String productCode=(supplierName.keySet().toArray())[0].toString();
						String prductName= supplierName.get(productCode).toLowerCase();
						Log.d("checktext",""+productCode+" "+prductName);
						if(prductName.contains(edit)||productCode.contains(edit)){
							searchResults.add(al.get(i));
						}
					}
				}
				arrayAdapterProd = new CustomAlertAdapterProd(InvoiceAddProduct.this, searchResults);
				listview.setAdapter(arrayAdapterProd);
			}
		});
		myDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		myalertDialog = myDialog.show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

		myalertDialog.dismiss();
		getArraylist = arrayAdapterProd.getArrayList();

		HashMap<String, String> datavalue = getArraylist.get(position);
		Set<Entry<String, String>> keys = datavalue.entrySet();
		Iterator<Entry<String, String>> iterator = keys.iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Entry mapEntry = iterator.next();
			sales_prodCode = (String) mapEntry.getKey();
			values = (String) mapEntry.getValue();

			sl_codefield.setText(sales_prodCode);
			sl_namefield.setText(values);
              loadProgress();
			 new AsyncCallSaleProduct().execute();

			sl_codefield.addTextChangedListener(new TextWatcher() {
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

					textlength = sl_codefield.getText().length();
					sl_namefield.setText("");

					sl_cprice.setText("");
					sl_price.setText("");
					sl_uom.setText("");
					sl_stock.setText("");
					sl_cartonPerQty.setText("");
				}
			});
		}

	}

	private class GetProduct extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		String dialogStatus;

		@Override
		protected void onPreExecute() {
			al.clear();
			category_ArrList.clear();
			subcategory_ArrList.clear();
			
			loadProgress();

			mHashMap.clear();
			dialogStatus = checkInternetStatus();

			if (screenInches > 7) {
				swipe.setVisibility(View.GONE);
				keyboard.setVisibility(View.GONE);
			}
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");
							productTxt = offlineDatabase.getProduct();
							productresult = " { SODetails : " + productTxt
									+ "}";

							String category_result = offlineDatabase
									.getCategory();
							category_jsonString = " { JsonArray: "
									+ category_result + "}";

							String subcategory_result = offlineDatabase
									.getSubCategory();
							subcategory_jsonString = " { JsonArray: "
									+ subcategory_result + "}";

						} else {
							Log.d("CheckOffline Alert -->", "False");
							//finish();
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {
						Log.d("checkOfflineStatus -->", "False");
						//productresult = getproductOnline();		
						
						mHashMap.put("CompanyCode", cmpnyCode);
						mHashMap.put("LocationCode", locationcode);
						if (screenInches > 7) {

							if (invoiceaddproducttab != null && !invoiceaddproducttab.isEmpty()) {

								if (invoiceaddproducttab.matches("1")) {
									mHashMap.put("CustomerCode", cstcode);
								}
							}
						}

						productresult = SalesOrderWebService.getSODetail(mHashMap, webMethProductWithPrice);
//						mProductJsonObject = new JSONObject(mProductJsonString);
//						mProductJsonArray = mProductJsonObject.optJSONArray("SODetails");

						category_jsonString = WebServiceClass
								.URLService("fncGetCategory");

						subcategory_jsonString = WebServiceClass
								.URLService("fncGetSubCategory");
					}

				} else if (onlineMode.matches("False")) {
					Log.d("Customer Online", onlineMode);
					productTxt = offlineDatabase.getProduct();
					productresult = " { SODetails : " + productTxt + "}";

					String category_result = offlineDatabase.getCategory();
					category_jsonString = " { JsonArray: " + category_result
							+ "}";

					String subcategory_result = offlineDatabase
							.getSubCategory();
					subcategory_jsonString = " { JsonArray: "
							+ subcategory_result + "}";
				}
				Log.d("getProduct", "res " + productresult);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(productresult);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SODetails");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					Log.d("lengthJsonArr","-->"+lengthJsonArr);
					Product product = new Product();
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String productcodes = jsonChildNode.optString(PRODUCT_CODE).toString();
					String productnames = jsonChildNode.optString(PRODUCT_NAME).toString();
					String haveBatch = jsonChildNode.optString("HaveBatch").toString();
					String haveExpiry = jsonChildNode.optString("HaveExpiry").toString();
					product.setProductCode(productcodes);
					product.setProductName(productnames);
					product.setHaveBatch(haveBatch);
					product.setHaveExpiry(haveExpiry);

					String dbProductCode = SOTDatabase
							.getProductCode(productcodes);
//					Log.d("dbProductCodeCheck",dbProductCode);
					if (dbProductCode != null && !dbProductCode.isEmpty()) {
						Log.d("dbProductCode","-->"+dbProductCode);
						HashMap<String, String> hm = SOTDatabase
								.getDBProducts(productcodes);

						String exchangeqty = hm
								.get(SOTDatabase.COLUMN_EXCHANGEQTY);
						String focqty = hm.get(SOTDatabase.COLUMN_FOC);
						String itemdiscount = hm
								.get(SOTDatabase.COLUMN_ITEM_DISCOUNT);

						if (exchangeqty != null && !exchangeqty.isEmpty()
								&& !exchangeqty.matches("null")) {

						} else {
							exchangeqty = "0";
						}

						if (focqty != null && !focqty.isEmpty()
								&& !focqty.matches("null")) {

						} else {
							focqty = "0";
						}

						if (itemdiscount != null && !itemdiscount.isEmpty()
								&& !itemdiscount.matches("null")) {

						} else {
							itemdiscount = "0";
						}

						product.setLprice(Double.valueOf(hm
								.get(SOTDatabase.COLUMN_PRODUCT_PRICE)));
						product.setCprice(Double.valueOf(hm
								.get(SOTDatabase.COLUMN_CARTONPRICE)));
						product.setCQty(Integer.valueOf(hm
								.get(SOTDatabase.COLUMN_CARTON_QTY)));
						product.setLQty(Integer.valueOf(hm
								.get(SOTDatabase.COLUMN_LOOSE_QTY)));
						product.setQuantity(Integer.valueOf(hm
								.get(SOTDatabase.COLUMN_QUANTITY)));
						product.setPcs(Integer.valueOf(hm
								.get(SOTDatabase.COLUMN_PIECE_PERQTY)));
						product.setFocQty(Integer.valueOf(focqty));
						product.setExchangeQty(Integer.valueOf(exchangeqty));
						product.setDiscount(Double.valueOf(itemdiscount));
						product.setTaxPer(Double.valueOf(hm
								.get(SOTDatabase.COLUMN_TAX)));
						product.setUom(String.valueOf(hm
								.get(SOTDatabase.COLUMN_PRODUCT_UOM)));
						 product.setTaxType(hm.get(SOTDatabase.COLUMN_TAXTYPE));
						 product.setTaxPerc(hm.get(SOTDatabase.COLUMN_TAXVALUE));
						product.setExt(Double.valueOf(hm
								.get(SOTDatabase.COLUMN_TOTAL)));
						product.setSubTot(Double.valueOf(hm
								.get(SOTDatabase.COLUMN_SUB_TOTAL)));
						product.setNetTot(Double.valueOf(hm
								.get(SOTDatabase.COLUMN_NETTOTAL)));

						productList.add(product);

						HashMap<String, String> producthm = new HashMap<String, String>();
						producthm.put(productcodes, productnames);
						al.add(producthm);

						/*String productDisplayOrder = jsonChildNode
								.optString("ProdDisplayOrder");
						if (productDisplayOrder != null
								&& !productDisplayOrder.isEmpty()) {
							product.setProdDisplayOrder(productDisplayOrder);
						} else {
							product.setProdDisplayOrder("0");
						}*/

					} else {

//						if (screenInches > 7) {
//									Log.d("productlist","no tab view data");
//						} else {

							String wholesaleprice = jsonChildNode
									.optString("WholeSalePrice");
							if (wholesaleprice != null && !wholesaleprice.isEmpty()) {
								product.setLprice(Double.valueOf(wholesaleprice));
							} else {
								product.setLprice(0.00);
							}
							String retailprice = jsonChildNode.optString("RetailPrice");
							Log.d("retailprice","r "+retailprice);
							if (retailprice != null && !retailprice.isEmpty()) {
								product.setCprice(Double.valueOf(retailprice));
							} else {
								product.setCprice(0.00);
							}
							product.setCQty(0);
							product.setLQty(0);
							product.setQuantity(0);
							product.setFocQty(0);
							product.setExchangeQty(0);
//						}
						// product.setLprice(jsonChildNode.optDouble("WholeSalePrice"));
						// product.setCprice(jsonChildNode.optDouble("RetailPrice"));
						product.setPcs(jsonChildNode.optInt("PcsPerCarton"));
						product.setCategoryCode(jsonChildNode
								.optString("CategoryCode"));
						product.setSubCategoryCode(jsonChildNode
								.optString("SubCategoryCode"));
						product.setUom(jsonChildNode.optString("UOMCode"));
						product.setTax(jsonChildNode.optString("TaxPerc"));
						product.setTaxPerc(jsonChildNode.optString("TaxPerc"));
						// product.setMinimumSellingPrice(jsonChildNode.optDouble("MinimumSellingPrice"));
						String minimumsellingprice = jsonChildNode
								.optString("MinimumSellingPrice");
						if (minimumsellingprice != null
								&& !minimumsellingprice.isEmpty()) {
							product.setMinimumSellingPrice(Double
									.valueOf(minimumsellingprice));
						} else {
							product.setMinimumSellingPrice(0.00);
						}
						String minimumcartonsellingprice = jsonChildNode
								.optString("MinimumCartonSellingPrice");
						if (minimumcartonsellingprice != null
								&& !minimumcartonsellingprice.isEmpty()) {
							product.setMinimumCartonSellingPrice(Double
									.valueOf(minimumcartonsellingprice));
						} else {
							product.setMinimumCartonSellingPrice(0.00);
						}
						String stock = jsonChildNode.optString("Qty");
						if (stock != null && !stock.isEmpty()) {
							product.setStock(Double.valueOf(stock));
						} else {
							product.setStock(0.00);
						}
						// product.setStock(jsonChildNode.optDouble("Qty"));
						String catName = jsonChildNode.optString("CatName");
						if (catName != null && !catName.isEmpty()) {
							product.setCategoryName(catName);
						} else {
							product.setCategoryName("0");
						}
						String subcatName = jsonChildNode.optString("SubCatName");
						if (subcatName != null && !subcatName.isEmpty()) {
							product.setSubCategoryName(subcatName);
						} else {
							product.setSubCategoryName("0");
						}

						String catDisplayOrder = jsonChildNode
								.optString("CatDisplayOrder");
						if (catDisplayOrder != null && !catDisplayOrder.isEmpty()) {
							product.setCatDisplayOrder(catDisplayOrder);
						} else {
							product.setCatDisplayOrder("0");
						}
						String subcatDisplayOrder = jsonChildNode
								.optString("SubCatDisplayOrder");
						if (subcatDisplayOrder != null
								&& !subcatDisplayOrder.isEmpty()) {
							product.setSubCatDisplayOrder(subcatDisplayOrder);
						} else {
							product.setSubCatDisplayOrder("0");
						}

						String productDisplayOrder = jsonChildNode
								.optString("ProdDisplayOrder");
						if (productDisplayOrder != null
								&& !productDisplayOrder.isEmpty()) {
							product.setProdDisplayOrder(productDisplayOrder);
						} else {
							product.setProdDisplayOrder("0");
						}
						product.setNewCategoryCode("");
						product.setNewSubCategoryCode("");
						productList.add(product);
					}
					HashMap<String, String> producthm = new HashMap<String, String>();
					producthm.put(productcodes, productnames);
					al.add(producthm);					

				}

			} catch (Exception e) {
				e.printStackTrace();

			}
			try {

				category_jsonResponse = new JSONObject(category_jsonString);
				category_jsonMainNode = category_jsonResponse
						.optJSONArray("JsonArray");

				/*********** Process each JSON Node ************/
				int lengJsonArr = category_jsonMainNode.length();
				for (int i = 0; i < lengJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = category_jsonMainNode
							.getJSONObject(i);
					Product product = new Product();
					String categorycode = jsonChildNode.optString(
							"CategoryCode").toString();

					String description = jsonChildNode.optString("Description")
							.toString();
					product.setCode(categorycode);
					product.setDescription(description);

					mCategoryArr.add(product);

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {

				subcategory_jsonResponse = new JSONObject(
						subcategory_jsonString);
				subcategory_jsonMainNode = subcategory_jsonResponse
						.optJSONArray("JsonArray");

				/*********** Process each JSON Node ************/
				int lengJsonArr = subcategory_jsonMainNode.length();
				for (int i = 0; i < lengJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = subcategory_jsonMainNode
							.getJSONObject(i);

					Product product = new Product();
					String subcategorycode = jsonChildNode.optString(
							"SubCategoryCode").toString();

					String description = jsonChildNode.optString("Description")
							.toString();
					product.setCode(subcategorycode);
					product.setDescription(description);
					mSubCategoryArr.add(product);

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Void result) {

			Log.d("productList1Check","-->"+productList.size());

			if (productList.size() > 0) {
				// Default sortby spinner selections
				mSpinnerSotBy.setSelection(1);
				 productAdapter = new ProductListAdapter(
						InvoiceAddProduct.this, R.layout.product_listitem,
						productList);
				 productAdapter.setFilterType("");
				 productAdapter.getFilter().filter(null);
				// Assign adapter to ListView
				mListView.setAdapter(productAdapter);
			}
			mCatSubAdapter = new CatSubAdapter(InvoiceAddProduct.this,
					R.layout.autotext_listitem, R.id.textView_titlevalue,
					mCategoryArr);
			mCategoryAutoCompleteText.setAdapter(mCatSubAdapter);

			mCatSubAdapter = new CatSubAdapter(InvoiceAddProduct.this,
					R.layout.autotext_listitem, R.id.textView_titlevalue,
					mSubCategoryArr);
			mSubCategoryAutoCompleteText.setAdapter(mCatSubAdapter);

			orientation = getResources().getConfiguration().orientation;
			if (screenInches > 7) {
				swipe.setVisibility(View.VISIBLE);
				if (Configuration.ORIENTATION_LANDSCAPE == orientation) {

					keyboard.setVisibility(View.VISIBLE);
				} else {
					keyboard.setVisibility(View.GONE);
					pageTitle.setVisibility(View.VISIBLE);
				}
			}

			// Setting the adapter to the listView
			// mProductAutoCompleteText.setAdapter(prodAdapter);
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);
			getActionBar().setHomeButtonEnabled(true);
			menu.setSlidingEnabled(true);
		}
	}

	private class CheckProductBarcode extends AsyncTask<Void, Void, Void> {

		String dialogStatus="",ed_code="";
		@Override
		protected void onPreExecute() {
			ed_code = sl_codefield.getText().toString();
			mBarcodeJsonString="";
			dialogStatus = checkInternetStatus();
			loadProgress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {		

			mHashMap.clear();
			mHashMap.put("CompanyCode",cmpnyCode);		
			mHashMap.put("Barcode",ed_code);
		
			try {
				
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");
							productTxt = offlineDatabase.getProductBarcode(ed_code);
							mBarcodeJsonString = " { SODetails : " + productTxt + "}";
//							mBarcodeJsonObject = new JSONObject(barcoderesult);
//							mBarcodeJsonArray = mBarcodeJsonObject.optJSONArray("BarCodeDetails");
						} else {
							Log.d("CheckOffline Alert -->", "False");
						//	finish();
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {
						Log.d("checkOffline Status -->", "False");
						//barcoderesult = getbarcodeOnline();

						mBarcodeJsonString = SalesOrderWebService.getSODetail(mHashMap, webMethNamebar);
					    Log.d("mBarcodeJsonString","-->"+mBarcodeJsonString);
						
					}

				} else if (onlineMode.matches("False")) {
					Log.d("Customer Online", onlineMode);
					productTxt = offlineDatabase.getProductBarcode(ed_code);
					mBarcodeJsonString = " { SODetails : " + productTxt + "}";
				}
				Log.d("barcode result", "" + mBarcodeJsonString);
				mBarcodeJsonObject = new JSONObject(mBarcodeJsonString);
				mBarcodeJsonArray = mBarcodeJsonObject.optJSONArray("SODetails");
				int lengthJsonBarcodeArr = mBarcodeJsonArray.length();										
				
					if (lengthJsonBarcodeArr > 0) {	
						JSONObject jsonChildNode = mBarcodeJsonArray.getJSONObject(0);
						String productbarcode = jsonChildNode.optString(
								PRODUCTNAME_BARCODE).toString();
						String barcode = jsonChildNode.optString(
								PRODUCT_BARCODE).toString();					
						sales_prodCode = productbarcode;
					}else{
						mBarcodeJsonString="";
					}
			} catch (JSONException e) {
				mBarcodeJsonString="";
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(mBarcodeJsonString!=null && !mBarcodeJsonString.isEmpty()){
			 new AsyncCallSaleProduct().execute();
			}else{
				sales_prodCode = ed_code;
				new AsyncCallSaleProduct().execute();
			}
			
		}


	}
	/*private class AsyncCallBARCODE extends AsyncTask<Void, Void, Void> {

		String dialogStatus;

		@Override
		protected void onPreExecute() {
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");
							productTxt = offlineDatabase.getProductBacode();
							barcoderesult = " { BarCodeDetails : " + productTxt
									+ "}";
						} else {
							Log.d("CheckOffline Alert -->", "False");
							finish();
						}

					} else {
						Log.d("checkOffline Status -->", "False");
						barcoderesult = getbarcodeOnline();
					}

				} else if (onlineMode.matches("False")) {
					Log.d("Customer Online Mode -->", onlineMode);
					productTxt = offlineDatabase.getProductBacode();
					barcoderesult = " { BarCodeDetails : " + productTxt + "}";
				}
				Log.d("barcode result", "" + barcoderesult);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JSONObject jsonResponse;
			try {

				jsonResponse = new JSONObject(barcoderesult);

				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("BarCodeDetails");

				int lengthJsonArr = jsonMainNode.length();

				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String productbarcode = jsonChildNode.optString(
							PRODUCTNAME_BARCODE).toString();
					String barcode = jsonChildNode.optString(PRODUCT_BARCODE)
							.toString();
					HashMap<String, String> barcodehm = new HashMap<String, String>();
					barcodehm.put(productbarcode, barcode);
					albarcode.add(barcodehm);
					albar.add(barcode);

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

		}
	}
*/
	private class AsyncCallSaleProduct extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		String dialogStatus;

		@Override
		protected void onPreExecute() {

			getSalesProdArr.clear();
			alBatchStock.clear();

			newPrice = "";
			newcprice = "";
			slPrice = "";
			slUomCode = "";
			slCartonPerQty = "";
			taxValue = "";

			sl_cartonQty.setText("");
			sl_looseQty.setText("");
			sl_qty.setText("");
			sl_foc.setText("");
			sl_itemDiscount.setText("");
			sl_price.setText("");
			sl_uom.setText("");
			sl_stock.setText("");
			sl_cartonPerQty.setText("");
			sl_total.setText("0");
			sl_total_inclusive.setText("0");
			sl_tax.setText("0");
			sl_netTotal.setText("0");

			sl_cprice.setText("");
			sl_exchange.setText("");

			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {

				if (cstgrpcd.matches("-1")) {
					cstgrpcd = "";
				}

				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");
							getSalesProdArr = offlineDatabase.getSaleProduct(
									sales_prodCode, "");
							priceArr = OfflineDatabase.getCustomerPrice(
									cstgrpcd, sales_prodCode);

						} else {
							Log.d("CheckOffline Alert -->", "False");
						//	finish();
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {
						// online

						getSalesProdArr = SalesProductWebService
								.getSaleProduct(sales_prodCode, webMethProduct,
										"");

						alBatchStock = SalesProductWebService
								.getProductBatchStockAdjustment(sales_prodCode,
										"fncGetProductBatchStock");

						priceArr = SalesProductWebService.getProductPrice(
								cstgrpcd, cstcode, sales_prodCode,
								"fncGetProductPriceForSales", "IN");
					}

				} else if (onlineMode.matches("False")) {
					Log.d("add prod Online", onlineMode);
					getSalesProdArr = offlineDatabase.getSaleProduct(
							sales_prodCode, "");
					priceArr = OfflineDatabase.getCustomerPrice(cstgrpcd,
							sales_prodCode);
				}
				Log.d("add product result", "" + getSalesProdArr);
				Log.d("Customprice", "..>" + priceArr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			newPrice = priceArr.get(0);
			newcprice = priceArr.get(1);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			ProductStockAsyncCall task = new ProductStockAsyncCall();
			task.execute();

			if (!getSalesProdArr.isEmpty()) {

				carton_layout.setVisibility(View.VISIBLE);
				if (!searchProductArr.isEmpty()) {
					grid_layout.setVisibility(View.VISIBLE);
				}
				Log.d("getSalesProdArr", getSalesProdArr.toString());
				
				slUomCode = getSalesProdArr.get(1);
				slCartonPerQty = getSalesProdArr.get(2);
				cprice = getSalesProdArr.get(4);

				MinimumSellingPrice = getSalesProdArr.get(5);

				String haveBatch = getSalesProdArr.get(6);
				String haveExpiry = getSalesProdArr.get(7);
				String codefield = getSalesProdArr.get(8);
				String namefield = getSalesProdArr.get(9);
				Weight = getSalesProdArr.get(10);
				MinimumCartonSellingPrice = getSalesProdArr.get(11);

				Log.d("Weight",""+ Weight);
				Log.d("haveBatch", ""+haveBatch);
				Log.d("haveExpiry", ""+haveExpiry);
				
				
				sl_codefield.setText(codefield);
				sl_namefield.setText(namefield);

				slPrice = getSalesProdArr.get(0);
				
				Log.d("slPrice","p(0) " +slPrice);
				
				Log.d("slUomCode","u(0) " +slUomCode);
				
				// taxValue = SalesOrderSetGet.getTaxValue();
				taxValue = getSalesProdArr.get(3);
				// Log.d("getproduct tax value", "getprod tax val "+
				// getSalesProdArr.get(3));
				if (FormSetterGetter.isEditPrice()) {

					sl_price.setBackgroundResource(drawable.edittext_bg);
					sl_cprice.setBackgroundResource(drawable.edittext_bg);

				} else {
					sl_price.setEnabled(false);
					sl_price.setFocusable(false);
					sl_price.setGravity(Gravity.CENTER);
					sl_price.setBackgroundResource(drawable.labelbg);
					sl_price.setFocusableInTouchMode(false);

					sl_cprice.setEnabled(false);
					sl_cprice.setFocusable(false);
					sl_cprice.setGravity(Gravity.CENTER);
					sl_cprice.setBackgroundResource(drawable.labelbg);
					sl_cprice.setFocusableInTouchMode(false);
				}

				HashMap<String, EditText> hm = new HashMap<String, EditText>();

				hm.put("Productcode", sl_codefield);
				hm.put("Productname", sl_namefield);
				hm.put("Cartonqty", sl_cartonQty);
				hm.put("Looseqty", sl_looseQty);
				hm.put("Qty", sl_qty);
				hm.put("Uom", sl_uom);
				hm.put("Foc", sl_foc);
				hm.put("Stock", sl_stock);
				hm.put("Cartonperqty", sl_cartonPerQty);
				hm.put("Price", sl_price);
				hm.put("CPrice", sl_cprice);
				
				Log.d("sl_price", "pppp " + slPrice);

				haveBatchOnStockIn = SalesOrderSetGet.getHaveBatchOnStockIn();
				if (offlineLayout.getVisibility() == View.GONE) {
					if (haveBatchOnStockIn.matches("True")) {
						Log.d("haveBatchOnStockIn", haveBatchOnStockIn);
						if (haveBatch.matches("True")
								|| haveExpiry.matches("True")) {
							Log.d("haveBatch", "haveExpiry");
							String code = sl_codefield.getText().toString();
							String name = sl_namefield.getText().toString();

							if (!alBatchStock.isEmpty()) {
								invoiceBatchDialog.initiateBatchPopupWindow(
										InvoiceAddProduct.this, haveBatch,
										haveExpiry, code, name, slCartonPerQty,
										slPrice, hm, alBatchStock);
								noBatchvalue();
							} else {

								Toast.makeText(getApplicationContext(),
										"No Batch data", Toast.LENGTH_SHORT)
										.show();
								sl_codefield.setText("");
								sl_namefield.setText("");
								// noBatchvalue();
							}

						} else {
							Log.d("no haveBatch", "no haveExpiry");
							noBatchvalue();
						}

					} else {
						Log.d("no haveBatchOnStockIn", "no haveBatchOnStockIn");
						noBatchvalue();
					}
				} else {
					Log.d("offline no batch", "offline no batch");
					Log.d("slPrice","pp " +slPrice);
					noBatchvalue();
				}
			}
			else{
				sl_codefield.requestFocus();
				Toast.makeText(getApplicationContext(), " Invalid Code ",
						Toast.LENGTH_SHORT).show();
				sl_codefield.setText("");
				sl_namefield.setText("");
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);
		}
	}

	private class AsyncCallWSSearchProduct extends AsyncTask<Void, Void, Void> {

		String dialogStatus;

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(InvoiceAddProduct.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesproduct_layout, false);
			progressBar = new ProgressBar(InvoiceAddProduct.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);

			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			try {
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");
							searchProductArr = offlineDatabase.searchProduct(
									catStr, subCatStr);

						} else {
							Log.d("CheckOffline Alert -->", "False");
						//	finish();
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {
						Log.d("checkOffline Status -->", "False");
						searchProductArr = NewProductWebService.searchProduct(
								catStr, subCatStr, "fncGetProduct");
					}

				} else if (onlineMode.matches("False")) {
					Log.d("Customer Online", onlineMode);
					searchProductArr = offlineDatabase.searchProduct(catStr,
							subCatStr);

				}
				Log.d("search result", "" + searchProductArr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (!searchProductArr.isEmpty()) {
				carton_layout.setVisibility(View.GONE);
				grid_layout.setVisibility(View.VISIBLE);
			} else {
				Toast.makeText(getApplicationContext(), "No matches found",
						Toast.LENGTH_SHORT).show();
			}
			try {
				searchProductList();
			} catch (ParseException e) {

				e.printStackTrace();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);
		}
	}

	public void searchProductList() throws ParseException {

		adapter = new AddSimpleAdapter(InvoiceAddProduct.this,
				searchProductArr, R.layout.sale_productitem,"",
				new String[] { "ProductCode", "ProductName", "WholeSalePrice",
						"Qty" }, new int[] { R.id.txt_code, R.id.txt_name,
						R.id.txt_price, R.id.txt_qty });
		productFilterList.setAdapter(adapter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		if (menu.isMenuShowing()) {
			// enableViews(salesproduct_layout, false);
		} else {
			// enableViews(salesproduct_layout, true);
		}

		return super.onOptionsItemSelected(item);
	}

	private class ProductStockAsyncCall extends AsyncTask<Void, Void, Void> {
		String dialogStatus;

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
									.getStockValue(LocationCode, sales_prodCode);

						} else {
							Log.d("CheckOffline Alert -->", "False");
						//	finish();
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {
						Log.d("checkOffline Status -->", "False");
						product_stock_jsonString = stock();
					}

				} else if (onlineMode.matches("False")) {
					Log.d("Customer Online", onlineMode);
					product_stock_jsonString = offlineDatabase.getStockValue(
							LocationCode, sales_prodCode);

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

				/*********** Process each JSON Node ************/
				int lengthJsonArr = product_stock_jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode;

					jsonChildNode = product_stock_jsonMainNode.getJSONObject(i);

					stock = jsonChildNode.optString("Qty").toString();

					if(stock!=null && !stock.isEmpty()){
						
					}else{
						stock="";
					}
					
					Log.d("stock qty", ""+stock);

				}
			} catch (JSONException e) {

				e.printStackTrace();
			} catch (Exception e) {

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (!sl_codefield.getText().toString().matches("")) {
			sl_stock.setText("" + stock);
			}
		}
	}

	public void productCodeFilter(final int fsl_no, final String fcodeStr,
			final String fnameStr, final double fcartonQty, final double flooseQty,
			final double fqty, final double ffoc, final double fprice,
			final double fdiscount, final String fuomStr,
			final double fcartonPerQty, final double fd, final double ftax,
			final String fnetT, final String ftaxType, final String ftaxValue,
			final String fnewPrice, final String fsbTtl,
			final String cpriceStr, final String exQtyStr) {
		String productname = sl_namefield.getText().toString();
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				InvoiceAddProduct.this);

		alertDialog.setTitle(productname);
		alertDialog.setMessage(productname + " is already exists");
		alertDialog.setPositiveButton("Add",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						SOTDatabase.storeProduct(fsl_no, fcodeStr, fnameStr,
								fcartonQty, flooseQty, fqty, ffoc, fprice,
								fdiscount, fuomStr, fcartonPerQty, fd, ftax,
								fnetT, ftaxType, ftaxValue, fnewPrice, fsbTtl,
								cpriceStr, exQtyStr, MinimumSellingPrice, "",
								"", "","","","");

						SOTDatabase.storeBillDisc(fsl_no, fcodeStr, fsbTtl);

						Log.d("fsbTtl add",""+fsbTtl);
						Log.d("fsl_no add",""+fsl_no);

					}
				});

		alertDialog.setNeutralButton("Replace",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						int cts = 1;
						String appType = LogOutSetGet.getApplicationType();
						int count = SOTDatabase.countprodcode(fcodeStr);

						SOTDatabase sotdb = new SOTDatabase(
								InvoiceAddProduct.this);
						if (appType.matches("W")) {
							sotdb.deleteBarcodeProduct(fcodeStr);
						}
						if (cts == count) {
							SOTDatabase.updateProductCodeFilter(fsl_no,
									fcodeStr, fnameStr, fcartonQty, flooseQty,
									fqty, ffoc, fprice, fdiscount, fuomStr,
									fcartonPerQty, fd, ftax, fnetT, ftaxType,
									ftaxValue, fnewPrice, fsbTtl,
									cpriceStr, exQtyStr,
									MinimumSellingPrice, "", "", "", "");
							SOTDatabase
									.updateBillDiscForInvoice(fsl_no, fcodeStr, fsbTtl);

							Log.d("fsbTtl replace",""+fsbTtl);
							Log.d("fsl_no replace",""+fsl_no);
						} else {
							if (count > 1) {

								sotdb.deleteProd(fcodeStr);
								sotdb.deleteBillPrd(fcodeStr);

								SOTDatabase.storeProduct(fsl_no, fcodeStr,
										fnameStr, fcartonQty, flooseQty, fqty,
										ffoc, fprice, fdiscount, fuomStr,
										fcartonPerQty, fd, ftax, fnetT,
										ftaxType, ftaxValue, fnewPrice, fsbTtl,
										cpriceStr, exQtyStr,
										MinimumSellingPrice, "", "", "", "","","");
								SOTDatabase.storeBillDisc(fsl_no, fcodeStr,
										fsbTtl);
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
						}
					}
				});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		alertDialog.show();
	}

	private void noBatchvalue() {
		
		Log.d("newPrice", "new p "+newPrice+" " +newcprice);
		
		Log.d("slPrice", "new sl "+slPrice +" "+cprice);
		

		if (newPrice.matches("0")) {
			sl_price.setText(slPrice);
		} else {
			sl_price.setText(newPrice);
		}

		if (newcprice.matches("0")) {
			sl_cprice.setText(cprice);
		} else {
			sl_cprice.setText(newcprice);
		}

		sl_uom.setText(slUomCode);


		if (priceflag.matches("1")) {

			if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
					|| slCartonPerQty.matches("")) {
				sl_price.setText("");
				sl_price.setVisibility(View.INVISIBLE);
				txt_price.setVisibility(View.GONE);
			}else{
				sl_price.setVisibility(View.VISIBLE);
				txt_price.setVisibility(View.VISIBLE);
			}

		} else {
			sl_price.setVisibility(View.VISIBLE);
			txt_price.setVisibility(View.VISIBLE);
		}

		if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
				|| slCartonPerQty.matches("")) {

			if (calCarton.matches("0")) {

				sl_cartonQty.setFocusableInTouchMode(true);
				sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

				sl_looseQty.setFocusableInTouchMode(true);
				sl_looseQty.setBackgroundResource(drawable.edittext_bg);

				sl_qty.setFocusableInTouchMode(true);
				sl_qty.setBackgroundResource(drawable.edittext_bg);

				sl_foc.setFocusableInTouchMode(true);
				sl_foc.setBackgroundResource(drawable.edittext_bg);

				sl_cartonQty.requestFocus();

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(sl_cartonQty,
						InputMethodManager.SHOW_IMPLICIT);

			} else {

				sl_cartonQty.setFocusable(false);
				sl_cartonQty.setBackgroundResource(drawable.labelbg);

				sl_looseQty.setFocusable(false);
				sl_looseQty.setBackgroundResource(drawable.labelbg);

				sl_qty.setFocusableInTouchMode(true);
				sl_qty.setBackgroundResource(drawable.edittext_bg);

				sl_foc.setFocusableInTouchMode(true);
				sl_foc.setBackgroundResource(drawable.edittext_bg);

				sl_qty.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(sl_qty, InputMethodManager.SHOW_IMPLICIT);
			}

		} else {
			sl_cartonQty.setFocusableInTouchMode(true);
			sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

			sl_looseQty.setFocusableInTouchMode(true);
			sl_looseQty.setBackgroundResource(drawable.edittext_bg);

			sl_qty.setFocusableInTouchMode(true);
			sl_qty.setBackgroundResource(drawable.edittext_bg);

			sl_foc.setFocusableInTouchMode(true);
			sl_foc.setBackgroundResource(drawable.edittext_bg);

			sl_cartonQty.requestFocus();

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(sl_cartonQty, InputMethodManager.SHOW_IMPLICIT);
		}

		sl_cartonPerQty.setText(slCartonPerQty);
		sl_uom.setText(slUomCode);

		progressBar.setVisibility(View.GONE);
		spinnerLayout.setVisibility(View.GONE);
		enableViews(salesproduct_layout, true);
	}

	@Override
	public void onBackPressed() {
		if ((str_ssupdate != null) || (str_sscancel != null)) {
			Intent i = new Intent(InvoiceAddProduct.this, InvoiceSummary.class);
			startActivity(i);
			InvoiceAddProduct.this.finish();
		} else if (intentString.matches("Invoice")) {

			Intent i = new Intent(InvoiceAddProduct.this,
					CustomerListActivity.class);
			startActivity(i);
			InvoiceAddProduct.this.finish();

		} else if (intentString.matches("Route Invoice")) {

			Intent i = new Intent(InvoiceAddProduct.this, RouteHeader.class);
			startActivity(i);
			InvoiceAddProduct.this.finish();

		} else {
			// Intent i = new Intent(InvoiceAddProduct.this,
			// InvoiceCustomer.class);
			// startActivity(i);
			// InvoiceAddProduct.this.finish();
			screenInches = displayMetrics();
			if (screenInches > 7) {
				if (orderQty > 0 || orderCQty > 0 || orderLQty > 0
						|| orderFocQty > 0 || orderExchQty > 0) {
					productAdapter.storeProducts("Customer");

				} else {
					Intent i = new Intent(InvoiceAddProduct.this,
							InvoiceCustomer.class);
					startActivity(i);
					InvoiceAddProduct.this.finish();
				}
			} else {
				Intent i = new Intent(InvoiceAddProduct.this,
						InvoiceCustomer.class);
				startActivity(i);
				InvoiceAddProduct.this.finish();
			}
		}

		// screenInches = displayMetrics();
		// if (screenInches > 7) {
		// if (orderQty > 0 ||orderCQty > 0||orderLQty > 0
		// || orderFocQty > 0 || orderExchQty > 0) {
		// productAdapter.storeProducts("Customer");
		//
		// } else {
		// Intent i = new Intent(InvoiceAddProduct.this,
		// InvoiceCustomer.class);
		// startActivity(i);
		// InvoiceAddProduct.this.finish();
		// }
		// } else {
		// Intent i = new Intent(InvoiceAddProduct.this,
		// InvoiceCustomer.class);
		// startActivity(i);
		// InvoiceAddProduct.this.finish();
		// }

	}

	@Override
	public void onListItemClick(String item) {
		menu.toggle();
	}

/*	public String getproductOnline() {
		String productresult = "";
		SoapObject request = new SoapObject(NAMESPACE, webMethProductWithPrice);

		PropertyInfo companyCode = new PropertyInfo();
		PropertyInfo customerCode = new PropertyInfo();
		PropertyInfo locationCode = new PropertyInfo();
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		companyCode.setName("CompanyCode");
		companyCode.setValue(cmpnyCode);
		companyCode.setType(String.class);
		request.addProperty(companyCode);

		locationCode.setName("LocationCode");
		locationCode.setValue(locationcode);
		locationCode.setType(String.class);
		request.addProperty(locationCode);

		customerCode.setName("CustomerCode");
		customerCode.setValue(cstcode);
		customerCode.setType(String.class);
		request.addProperty(customerCode);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(valid_url);
		try {

			androidHttpTransport.call(SOAP_ACTION + webMethProductWithPrice,
					envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			productTxt = response.toString();
			productresult = " { ProductDetails : " + productTxt + "}";
		} catch (Exception e) {
			productresult = "";
		}
		return productresult;

	}*/

/*	public String getbarcodeOnline() {
		String barcoderesult = "";
		SoapObject request = new SoapObject(NAMESPACE, webMethNamebar);
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
		HttpTransportSE androidHttpTransport = new HttpTransportSE(valid_url);
		try {

			androidHttpTransport.call(SOAP_ACTION + webMethNamebar, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			barcodeTxt = response.toString();
			barcoderesult = " { BarCodeDetails : " + barcodeTxt + "}";
		} catch (Exception e) {
			barcoderesult = "";
		}
		return barcoderesult;
	}
*/
	public String stock() {

		cmpnyCode = SupplierSetterGetter.getCompanyCode();
		LocationCode = SalesOrderSetGet.getLocationcode();

		producthashValue.put("CompanyCode", cmpnyCode);
		producthashValue.put("LocationCode", LocationCode);
		producthashValue.put("ProductCode", sales_prodCode);
		product_stock_jsonString = WebServiceClass.parameterService(
				producthashValue, "fncGetProductWithStock");

		return product_stock_jsonString;
	}

	private String checkInternetStatus() {
		String internetStatus = "";
		String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			if(mobileHaveOfflineMode.matches("1")){

		checkOffline = OfflineCommon.isConnected(InvoiceAddProduct.this);
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
						Intent i = new Intent(InvoiceAddProduct.this,
								InvoiceHeader.class);
						startActivity(i);
						InvoiceAddProduct.this.finish();
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

	public class ProductListAdapter extends ArrayAdapter<Product> {
		private ArrayList<Product> productList;
		private ArrayList<Product> originalList;
		private ProductFilter filter;
		private String filterType, productCode = null;
		private Context context;
		private DecimalFormat df;
		private ViewHolder holder = null;
		public int qtyFlag = 0, cartonFlag = 0, looseFlag = 0,
				exchangeQtyFlag = 0, focQtyFlag = 0,retQtyFlag =0;

		public ProductListAdapter(Context context, int textViewResourceId,
				ArrayList<Product> productList) {
			super(context, textViewResourceId, productList);
			Log.d("productListcount",""+productList.size());
			this.context = context;
			this.productList = new ArrayList<Product>();
			this.productList.addAll(productList);
			this.originalList = new ArrayList<Product>();
			this.originalList.addAll(productList);
		}

		@Override
		public Filter getFilter() {
			if (filter == null) {
				filter = new ProductFilter();
			}
			return filter;
		}

		public void setFilterType(String filterType) {
			this.filterType = filterType;
		}

		public String getFilterType() {
			return filterType;

		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			df = new DecimalFormat("0.00##");
			final Product product = productList.get(position);
			if (view == null) {
				holder = new ViewHolder();
				LayoutInflater vi = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.product_listitem, null);
				holder.underLine = view.findViewById(R.id.underline);
				holder.itemNumber = (TextView) view
						.findViewById(R.id.itemNumber);
				holder.description = (TextView) view
						.findViewById(R.id.description);
				holder.pcs = (TextView) view.findViewById(R.id.pcs);
				holder.uom = (TextView) view.findViewById(R.id.uom);
				holder.ext = (TextView) view.findViewById(R.id.ext);
				holder.mSubTotal = (TextView) view.findViewById(R.id.subTotal);
				holder.mTax = (TextView) view.findViewById(R.id.tax);
				holder.mNetTotal = (TextView) view.findViewById(R.id.netTotal);
				holder.mStock = (TextView) view.findViewById(R.id.stock);
				holder.mExpand = (ImageView) view.findViewById(R.id.expand);

				holder.mExpandLayoutLabel1 = (LinearLayout) view
						.findViewById(R.id.linearLayout2);
				holder.mExpandLayoutLabel2 = (LinearLayout) view
						.findViewById(R.id.linearLayout3);
				holder.cQuantityPlusImgbtn = (ImageButton) view
						.findViewById(R.id.cQuantity_plus);
				holder.cQuantityMinusImgbtn = (ImageButton) view
						.findViewById(R.id.cQuantity_minus);
				holder.lQuantityPlusImgbtn = (ImageButton) view
						.findViewById(R.id.lQuantity_plus);
				holder.lQuantityMinusImgbtn = (ImageButton) view
						.findViewById(R.id.lQuantity_minus);
				holder.quantityPlusImgbtn = (ImageButton) view
						.findViewById(R.id.quantity_plus);
				holder.quantityMinusImgbtn = (ImageButton) view
						.findViewById(R.id.quantity_minus);

				holder.focQtyPlusImgbtn = (ImageButton) view
						.findViewById(R.id.focQty_plus);
				holder.focQtyMinusImgbtn = (ImageButton) view
						.findViewById(R.id.focQty_minus);

				holder.exchangeQtyPlusImgbtn = (ImageButton) view
						.findViewById(R.id.exchangeQty_plus);
				holder.exchangeQtyMinusImgbtn = (ImageButton) view
						.findViewById(R.id.exchangeQty_minus);
				holder.retQtyPlusImgbtn = (ImageButton) view
						.findViewById(R.id.retQty_plus);
				holder.retQtyMinusImgbtn = (ImageButton) view
						.findViewById(R.id.retQty_minus);
				holder.mBatch = (ImageView) view.findViewById(R.id.batch);

				holder.quantity = (EditText) view.findViewById(R.id.quantity);
				holder.cqty = (EditText) view.findViewById(R.id.cQuantity);
				holder.lqty = (EditText) view.findViewById(R.id.lQuantity);
				holder.discount = (EditText) view.findViewById(R.id.discount);
				holder.price = (EditText) view.findViewById(R.id.price);
				holder.cprice = (EditText) view.findViewById(R.id.cprice);
				holder.cprice_layout = (LinearLayout) view
						.findViewById(R.id.cprice_layout);
				holder.mFocQty = (EditText) view.findViewById(R.id.focQty);
				holder.mExchangeQty = (EditText) view
						.findViewById(R.id.exchangeQty);
				holder.mRetQty = (EditText) view
						.findViewById(R.id.retQty);
				holder.mExpandLayoutLabel3 = (LinearLayout) view
						.findViewById(R.id.linearLayout1);
				holder.mExpandLayoutLabel1 = (LinearLayout) view
						.findViewById(R.id.linearLayout2);
				holder.mExpandLayoutLabel2 = (LinearLayout) view
						.findViewById(R.id.linearLayout3);
				holder.clinear = (LinearLayout) view
						.findViewById(R.id.clinear);
				holder.llinear = (LinearLayout) view
						.findViewById(R.id.llinear);


				// attach the TextWatcher listener to the EditText
				holder.cqty.addTextChangedListener(new CQtyTextWatcher(view));
				holder.lqty.addTextChangedListener(new LQtyTextWatcher(view));
				holder.quantity.addTextChangedListener(new QuantityTextWatcher(
						view));
				holder.price.addTextChangedListener(new PriceTextWatcher(view));
				holder.cprice
						.addTextChangedListener(new CPriceTextWatcher(view));
				holder.discount.addTextChangedListener(new DiscountTextWatcher(
						view));
				holder.mFocQty.addTextChangedListener(new FocQtyTextWatcher(
						view));
				holder.mExchangeQty
						.addTextChangedListener(new ExchangeQtyTextWatcher(view));

				holder.mRetQty
						.addTextChangedListener(new ReturnQtyTextWatcher(view));
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			if(SalesOrderSetGet.getCarton_quantity().matches("0")){
				holder.clinear.setVisibility(View.GONE);
				holder.llinear.setVisibility(View.GONE);
			}else{
				holder.clinear.setVisibility(View.VISIBLE);
				holder.llinear.setVisibility(View.VISIBLE);
			}

			// Set position id
			view.setId(position);
			holder.cQuantityPlusImgbtn.setId(position);
			holder.cQuantityMinusImgbtn.setId(position);
			holder.lQuantityPlusImgbtn.setId(position);
			holder.lQuantityMinusImgbtn.setId(position);
			holder.quantityPlusImgbtn.setId(position);
			holder.quantityMinusImgbtn.setId(position);
			holder.focQtyPlusImgbtn.setId(position);
			holder.focQtyMinusImgbtn.setId(position);
			holder.exchangeQtyPlusImgbtn.setId(position);
			holder.exchangeQtyMinusImgbtn.setId(position);
			holder.retQtyPlusImgbtn.setId(position);
			holder.retQtyMinusImgbtn.setId(position);
			holder.mExpand.setId(position);
			holder.mBatch.setId(position);
			// Set tag id
			holder.quantity.setTag(product);
			holder.cqty.setTag(product);
			holder.lqty.setTag(product);
			holder.discount.setTag(product);
			holder.pcs.setTag(product);
			holder.price.setTag(product);
			holder.cprice.setTag(product);
			holder.mFocQty.setTag(product);
			holder.mExchangeQty.setTag(product);
			holder.mRetQty.setTag(product);
			String haveBatch = product.getHaveBatch();
			String haveExpiry = product.getHaveExpiry();

			haveBatchOnStockIn = SalesOrderSetGet.getHaveBatchOnStockIn();
			if (sortStr.matches("SortCategory")) {

				String newcategory = product.getNewCategoryCode();
				if (newcategory != null && !newcategory.isEmpty()) {
					holder.underLine.setVisibility(View.VISIBLE);					
				} else {
					holder.underLine.setVisibility(View.GONE);
				}

			} else if (sortStr.matches("SortSubCategory")) {
				String newsubcategory = product.getNewSubCategoryCode();
				if (newsubcategory != null && !newsubcategory.isEmpty()) {
					holder.underLine.setVisibility(View.VISIBLE);
				} else {
					holder.underLine.setVisibility(View.GONE);
				}

			} else {
				holder.underLine.setVisibility(View.GONE);
			}
			if (tranblocknegativestock != null
					&& !tranblocknegativestock.isEmpty()
					&& tranblocknegativestock.matches("1")) {
				if (product.getStock() > 0) {
					// view.setBackgroundResource(R.drawable.list_item_bg_normal);
					holder.mExpandLayoutLabel1.setBackgroundColor(Color
							.parseColor("#F2FAFD"));
					holder.mExpandLayoutLabel2.setBackgroundColor(Color
							.parseColor("#F2FAFD"));
					holder.mExpandLayoutLabel3
							.setBackgroundResource(drawable.white_row);

					if (haveBatchOnStockIn.matches("True")) {
						if (haveBatch.matches("True")
								|| haveExpiry.matches("True")) {
							holder.mBatch.setVisibility(View.VISIBLE);
						} else {
							holder.mBatch.setVisibility(View.INVISIBLE);
						}
					} else {
						holder.mBatch.setVisibility(View.INVISIBLE);
					}

				} else {
					// view.setBackgroundResource(R.drawable.list_item_lightred_bg);
					holder.mExpandLayoutLabel1
							.setBackgroundResource(drawable.list_red);
					holder.mExpandLayoutLabel2
							.setBackgroundResource(drawable.list_red);
					holder.mExpandLayoutLabel3
							.setBackgroundResource(drawable.list_red);
					holder.mBatch.setVisibility(View.INVISIBLE);
				}
			} else {
				if (haveBatchOnStockIn.matches("True")) {
					if (haveBatch.matches("True") || haveExpiry.matches("True")) {
						holder.mBatch.setVisibility(View.VISIBLE);
					} else {
						holder.mBatch.setVisibility(View.INVISIBLE);
					}
				} else {
					holder.mBatch.setVisibility(View.INVISIBLE);
				}
			}

			// if product Code matches mExpandLayoutLabel1 and
			// mExpandLayoutLabel2 will expand or else collapse
			if (productCode != null && !productCode.isEmpty()) {
				if (productCode.matches(product.getProductCode())) {
					holder.mExpandLayoutLabel1.setVisibility(View.VISIBLE);
					holder.mExpandLayoutLabel2.setVisibility(View.VISIBLE);
				} else {
					holder.mExpandLayoutLabel1.setVisibility(View.GONE);
					holder.mExpandLayoutLabel2.setVisibility(View.GONE);
				}
			} else {
				holder.mExpandLayoutLabel1.setVisibility(View.GONE);
				holder.mExpandLayoutLabel2.setVisibility(View.GONE);
			}
			// Product code
			holder.itemNumber.setText(product.getProductCode());
			// Product name
			holder.description.setText(product.getProductName());
			holder.quantity
					.addTextChangedListener(new QuantityTextWatcher(view));
			// Quantity
			if (product.getQuantity() != 0) {
				holder.quantity.setText(String.valueOf(product.getQuantity()));
			} else {
				holder.quantity.setText("0");
			}
			// Carton Quantity
			if (product.getCQty() != 0) {
				holder.cqty.setText(String.valueOf(product.getCQty()));
			} else {
				holder.cqty.setText("0");
			}
			// Loose Quantity
			if (product.getLQty() != 0) {
				holder.lqty.setText(String.valueOf(product.getLQty()));
			} else {
				holder.lqty.setText("0");
			}
			// Discount
			if (product.getDiscount() != 0) {
				holder.discount.setText(String.valueOf(product.getDiscount()));
			} else {
				holder.discount.setText("0");
			}
			// Pieces Per Carton
			holder.pcs.setText("" + product.getPcs());
			// if pcs greater than one carton and loose can edit or else can't
			if (product.getPcs() > 1) {

				if (holder.mBatch.getVisibility() == View.VISIBLE) {
					holder.quantity.setEnabled(false);
					holder.lqty.setEnabled(false);
					holder.cqty.setEnabled(false);
					holder.mFocQty.setEnabled(false);

					holder.quantity
							.setBackgroundResource(drawable.edittext_bg_light_disable);
					holder.cqty
							.setBackgroundResource(drawable.edittext_bg_light_disable);
					holder.lqty
							.setBackgroundResource(drawable.edittext_bg_light_disable);
					holder.mFocQty
							.setBackgroundResource(drawable.edittext_bg_light_disable);

					holder.quantityPlusImgbtn.setEnabled(false);
					holder.lQuantityPlusImgbtn.setEnabled(false);
					holder.cQuantityPlusImgbtn.setEnabled(false);
					holder.focQtyPlusImgbtn.setEnabled(false);

					holder.quantityMinusImgbtn.setEnabled(false);
					holder.lQuantityMinusImgbtn.setEnabled(false);
					holder.cQuantityMinusImgbtn.setEnabled(false);
					holder.focQtyMinusImgbtn.setEnabled(false);

					holder.quantityPlusImgbtn
							.setImageResource(R.mipmap.inactive_plus);
					holder.quantityMinusImgbtn
							.setImageResource(R.mipmap.inactive_minus);
					holder.lQuantityPlusImgbtn
							.setImageResource(R.mipmap.inactive_plus);
					holder.lQuantityMinusImgbtn
							.setImageResource(R.mipmap.inactive_minus);
					holder.cQuantityPlusImgbtn
							.setImageResource(R.mipmap.inactive_plus);
					holder.cQuantityMinusImgbtn
							.setImageResource(R.mipmap.inactive_minus);
					holder.focQtyPlusImgbtn
							.setImageResource(R.mipmap.inactive_plus);
					holder.focQtyMinusImgbtn
							.setImageResource(R.mipmap.inactive_minus);

				} else if (holder.mBatch.getVisibility() == View.INVISIBLE) {

					holder.cqty.setEnabled(true);
					holder.lqty.setEnabled(true);
					holder.quantity.setEnabled(true);
					holder.mFocQty.setEnabled(true);

					holder.lqty
							.setBackgroundResource(drawable.edittext_bg_light);
					holder.cqty
							.setBackgroundResource(drawable.edittext_bg_light);
					holder.quantity
							.setBackgroundResource(drawable.edittext_bg_light);
					holder.mFocQty
							.setBackgroundResource(drawable.edittext_bg_light);

					holder.quantityPlusImgbtn.setEnabled(true);
					holder.lQuantityPlusImgbtn.setEnabled(true);
					holder.cQuantityPlusImgbtn.setEnabled(true);
					holder.focQtyPlusImgbtn.setEnabled(true);

					holder.quantityMinusImgbtn.setEnabled(true);
					holder.lQuantityMinusImgbtn.setEnabled(true);
					holder.cQuantityMinusImgbtn.setEnabled(true);
					holder.focQtyMinusImgbtn.setEnabled(true);

					holder.cQuantityPlusImgbtn
							.setImageResource(R.mipmap.active_plus);
					holder.cQuantityMinusImgbtn
							.setImageResource(R.mipmap.active_minus);
					holder.lQuantityPlusImgbtn
							.setImageResource(R.mipmap.active_plus);
					holder.lQuantityMinusImgbtn
							.setImageResource(R.mipmap.active_minus);

					holder.quantityPlusImgbtn
							.setImageResource(R.mipmap.active_plus);
					holder.quantityMinusImgbtn
							.setImageResource(R.mipmap.active_minus);
					holder.focQtyPlusImgbtn
							.setImageResource(R.mipmap.active_plus);
					holder.focQtyMinusImgbtn
							.setImageResource(R.mipmap.active_minus);

				}
			} else {

				if (holder.mBatch.getVisibility() == View.VISIBLE) {
					holder.quantity.setEnabled(false);
					holder.lqty.setEnabled(false);
					holder.cqty.setEnabled(false);
					holder.mFocQty.setEnabled(false);

					holder.quantity
							.setBackgroundResource(drawable.edittext_bg_light_disable);
					holder.cqty
							.setBackgroundResource(drawable.edittext_bg_light_disable);
					holder.lqty
							.setBackgroundResource(drawable.edittext_bg_light_disable);
					holder.mFocQty
							.setBackgroundResource(drawable.edittext_bg_light_disable);

					holder.lQuantityPlusImgbtn.setEnabled(false);
					holder.cQuantityPlusImgbtn.setEnabled(false);
					holder.quantityPlusImgbtn.setEnabled(false);
					holder.focQtyPlusImgbtn.setEnabled(false);

					holder.lQuantityMinusImgbtn.setEnabled(false);
					holder.cQuantityMinusImgbtn.setEnabled(false);
					holder.quantityMinusImgbtn.setEnabled(false);
					holder.focQtyMinusImgbtn.setEnabled(false);

					holder.quantityPlusImgbtn
							.setImageResource(R.mipmap.inactive_plus);
					holder.quantityMinusImgbtn
							.setImageResource(R.mipmap.inactive_minus);
					holder.lQuantityPlusImgbtn
							.setImageResource(R.mipmap.inactive_plus);
					holder.lQuantityMinusImgbtn
							.setImageResource(R.mipmap.inactive_minus);
					holder.cQuantityPlusImgbtn
							.setImageResource(R.mipmap.inactive_plus);
					holder.cQuantityMinusImgbtn
							.setImageResource(R.mipmap.inactive_minus);
					holder.focQtyPlusImgbtn
							.setImageResource(R.mipmap.inactive_plus);
					holder.focQtyMinusImgbtn
							.setImageResource(R.mipmap.inactive_minus);
				} else if (holder.mBatch.getVisibility() == View.INVISIBLE) {

					holder.cqty.setEnabled(false);
					holder.lqty.setEnabled(false);
					holder.quantity.setEnabled(true);
					holder.mFocQty.setEnabled(true);

					holder.lqty
							.setBackgroundResource(drawable.edittext_bg_light_disable);
					holder.cqty
							.setBackgroundResource(drawable.edittext_bg_light_disable);
					holder.quantity
							.setBackgroundResource(drawable.edittext_bg_light);
					holder.mFocQty
							.setBackgroundResource(drawable.edittext_bg_light);

					holder.cQuantityPlusImgbtn.setEnabled(false);
					holder.lQuantityPlusImgbtn.setEnabled(false);
					holder.quantityPlusImgbtn.setEnabled(true);
					holder.focQtyPlusImgbtn.setEnabled(true);

					holder.cQuantityMinusImgbtn.setEnabled(false);
					holder.lQuantityMinusImgbtn.setEnabled(false);
					holder.quantityMinusImgbtn.setEnabled(true);
					holder.focQtyMinusImgbtn.setEnabled(true);

					holder.cQuantityPlusImgbtn
							.setImageResource(R.mipmap.inactive_plus);
					holder.cQuantityMinusImgbtn
							.setImageResource(R.mipmap.inactive_minus);
					holder.lQuantityPlusImgbtn
							.setImageResource(R.mipmap.inactive_plus);
					holder.lQuantityMinusImgbtn
							.setImageResource(R.mipmap.inactive_minus);

					holder.quantityPlusImgbtn
							.setImageResource(R.mipmap.active_plus);
					holder.quantityMinusImgbtn
							.setImageResource(R.mipmap.active_minus);
					holder.focQtyPlusImgbtn
							.setImageResource(R.mipmap.active_plus);
					holder.focQtyMinusImgbtn
							.setImageResource(R.mipmap.active_minus);
				}
			}

			// Price
			if (product.getLprice() != 0) {
				holder.price.setText(df.format(product.getLprice()));
			} else {
				holder.price.setText("0.0");
			}
			// Carton Price
			if (product.getCprice() != 0) {
				holder.cprice.setText(df.format(product.getCprice()));
			} else {
				holder.cprice.setText("0.0");
			}
			// Based on general settings , Price flag is visible or gone
			if (priceflag.matches("1")) {
				holder.cprice_layout.setVisibility(View.VISIBLE);
			} else {
				holder.cprice_layout.setVisibility(View.GONE);
			}
			// Based on general settings , Price can edit or disable
			if (FormSetterGetter.isEditPrice()) {
				holder.price.setEnabled(true);
				holder.cprice.setEnabled(true);
			} else {
				holder.price.setEnabled(false);
				holder.cprice.setEnabled(false);
			}
			// Uom
			holder.uom.setText(product.getUom().toString());
			if (product.getQuantity() != 0) {
				holder.ext.setText(df.format(product.getExt()));
			} else {
				holder.ext.setText("0.00");
			}
			// SubTotal
			if (product.getSubTot() != 0) {
				holder.mSubTotal.setText(df.format(product.getSubTot()));
			} else {
				holder.mSubTotal.setText("0.00");
			}
			// Tax
			if (product.getTaxPer() != 0) {
				holder.mTax.setText(fourDecimalPoint(product.getTaxPer()));
			} else {
				holder.mTax.setText("0.0000");
			}
			// NetTotal
			if (product.getNetTot() != 0) {
				holder.mNetTotal.setText(fourDecimalPoint(product.getNetTot()));
			} else {
				holder.mNetTotal.setText("0.00");
			}
			// Foc Quantity
			if (product.getFocQty() != 0) {
				holder.mFocQty.setText(String.valueOf(product.getFocQty()));
			} else {
				holder.mFocQty.setText("0");
			}
			// Exchange Quantity
			if (product.getExchangeQty() != 0) {
				holder.mExchangeQty.setText(String.valueOf(product
						.getExchangeQty()));
			} else {
				holder.mExchangeQty.setText("0");
			}
			if(product.getReturn_qty() != 0){
				holder.mRetQty.setText(String.valueOf(product
						.getReturn_qty()));
			}else{
				holder.mRetQty.setText("0");
			}

			// Stock
			holder.mStock.setText(String.valueOf(product.getStock()));
			holder.cqty.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Product product = (Product) v.getTag();
					EditText cqty = (EditText) v;
					if (product.getCQty() == 0) {
						cqty.setText("");
					} else {
						cqty.setSelection(cqty.getText().length());
					}
					cqty.requestFocus();
					mEditValue = cqty;
					return false;
				}
			});
			holder.lqty.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Product product = (Product) v.getTag();
					EditText lqty = (EditText) v;
					if (product.getLQty() == 0) {
						lqty.setText("");
					} else {
						lqty.setSelection(lqty.getText().length());
					}
					lqty.requestFocus();
					mEditValue = lqty;
					return false;
				}
			});
			holder.quantity.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Product product = (Product) v.getTag();
					EditText qty = (EditText) v;
					if (product.getQuantity() == 0) {
						qty.setText("");
					} else {
						qty.setSelection(qty.getText().length());
					}
					qty.requestFocus();
					mEditValue = qty;
					return false;
				}
			});
			holder.mFocQty.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Product product = (Product) v.getTag();
					EditText focqty = (EditText) v;
					if (product.getFocQty() == 0) {
						focqty.setText("");
					} else {
						focqty.setSelection(focqty.getText().length());
					}
					focqty.requestFocus();
					mEditValue = focqty;
					return false;
				}
			});
			holder.mExchangeQty.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Product product = (Product) v.getTag();
					EditText exchangeqty = (EditText) v;
					if (product.getExchangeQty() == 0) {
						exchangeqty.setText("");
					} else {
						exchangeqty
								.setSelection(exchangeqty.getText().length());
					}
					exchangeqty.requestFocus();
					mEditValue = exchangeqty;
					return false;
				}
			});

			holder.mRetQty.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Product product = (Product) v.getTag();
					EditText returnqty = (EditText) v;
					if (product.getReturn_qty() == 0) {
						returnqty.setText("");
					} else {
						returnqty
								.setSelection(returnqty.getText().length());
					}
					returnqty.requestFocus();
					mEditValue = returnqty;
					return false;
				}
			});
			holder.discount.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Product product = (Product) v.getTag();
					EditText discount = (EditText) v;
					if (product.getDiscount() == 0) {
						discount.setText("");
					} else {
						discount.setSelection(discount.getText().length());
					}
					discount.requestFocus();
					mEditValue = discount;
					return false;
				}
			});
			holder.cprice.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Product product = (Product) v.getTag();
					EditText cprice = (EditText) v;
					if (product.getCprice() == 0) {
						cprice.setText("");
					} else {
						cprice.setSelection(cprice.getText().length());
					}
					cprice.requestFocus();
					mEditValue = cprice;
					return false;
				}
			});
			holder.price.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Product product = (Product) v.getTag();
					EditText price = (EditText) v;
					if (product.getLprice() == 0) {
						price.setText("");
					} else {
						price.setSelection(price.getText().length());
					}
					price.requestFocus();
					mEditValue = price;
					return false;
				}
			});
			holder.mExpand.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int position = v.getId();

					Product product = productList.get(position);
					if (product.getProductCode() == productCode) {
						productCode = null;
					} else {
						productCode = product.getProductCode();
					}
					notifyDataSetChanged();
				}
			});
			holder.cQuantityPlusImgbtn
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							cartonPlusAction(v);
						}
					});
			holder.cQuantityMinusImgbtn
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							cartonMinusAction(v);
						}
					});
			holder.lQuantityPlusImgbtn
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							loosePlusAction(v);
						}
					});
			holder.lQuantityMinusImgbtn
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							looseMinusAction(v);
						}
					});
			holder.quantityPlusImgbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					qtyPlusAction(v);
				}
			});
			holder.quantityMinusImgbtn
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							qtyMinusAction(v);
						}
					});

			holder.focQtyPlusImgbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					focQtyPlusAction(v);
				}
			});
			holder.focQtyMinusImgbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					focQtyMinusAction(v);
				}
			});

			holder.exchangeQtyPlusImgbtn
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							exchangeQtyPlusAction(v);

						}
					});
			holder.exchangeQtyMinusImgbtn
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							exchangeQtyMinusAction(v);
						}
					});

			holder.retQtyPlusImgbtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					returnQtyPlusAction(v);
				}
			});

			holder.retQtyMinusImgbtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					returnQtyMinusAction(v);
				}
			});

			holder.mBatch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int position = v.getId();
					Product product = productList.get(position);
					new AsyncCallSaleProductBatch(product, v).execute();
					notifyDataSetChanged();
				}
			});

			return view;

		}

		private class ViewHolder {
			EditText quantity;
			EditText cqty;
			EditText lqty;
			EditText discount;
			EditText price;
			EditText cprice;
			LinearLayout cprice_layout;
			EditText mFocQty;
			EditText mExchangeQty;
			EditText mRetQty;
			TextView itemNumber;
			TextView description;
			TextView pcs;
			TextView uom;
			TextView ext;
			TextView mSubTotal;
			TextView mTax;
			TextView mNetTotal;
			TextView mStock;
			ImageButton cQuantityPlusImgbtn;
			ImageButton cQuantityMinusImgbtn;
			ImageButton lQuantityPlusImgbtn;
			ImageButton lQuantityMinusImgbtn;
			ImageButton quantityPlusImgbtn;
			ImageButton quantityMinusImgbtn;
			ImageButton focQtyPlusImgbtn;
			ImageButton focQtyMinusImgbtn;
			ImageButton exchangeQtyPlusImgbtn;
			ImageButton exchangeQtyMinusImgbtn;
			ImageButton retQtyPlusImgbtn;
			ImageButton retQtyMinusImgbtn;
			ImageView mExpand, mBatch;
			LinearLayout mExpandLayoutLabel1;
			LinearLayout mExpandLayoutLabel2;
			LinearLayout mExpandLayoutLabel3;
			LinearLayout clinear,llinear;
			View underLine;
		}

		private class ProductFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults result = new FilterResults();
				String category = null, subcategory = null, prod = null;

				if (constraint != null && constraint.toString().length() > 0) {
					constraint = constraint.toString().toLowerCase(
							Locale.getDefault());
					// Searching filter
					//Log.d("getFilterType()",getFilterType());
					if (getFilterType().matches("Category && SubCategory")) {
						StringTokenizer tokens = new StringTokenizer(
								constraint.toString(), ",");
						category = tokens.nextToken();
						subcategory = tokens.nextToken();
					} else if (getFilterType().matches("Category && Product")) {
						StringTokenizer tokens = new StringTokenizer(
								constraint.toString(), ",");
						category = tokens.nextToken();
						prod = tokens.nextToken();

					} else if (getFilterType()
							.matches("SubCategory && Product")) {
						StringTokenizer tokens = new StringTokenizer(
								constraint.toString(), ",");
						subcategory = tokens.nextToken();
						prod = tokens.nextToken();
					}

					else if (getFilterType().matches(
							"Category && SubCategory && Product")) {
						StringTokenizer tokens = new StringTokenizer(
								constraint.toString(), ",");
						category = tokens.nextToken();
						subcategory = tokens.nextToken();
						prod = tokens.nextToken();

					}

					ArrayList<Product> filteredItems = new ArrayList<Product>();
					// ArrayList<Product> filterSearchItems = new
					// ArrayList<Product>();

					// Searching filter
					for (int i = 0, l = originalList.size(); i < l; i++) {
						Product product = originalList.get(i);
						if (getFilterType().matches("Category")) {
							if (product.getCategoryCode()
									.toLowerCase(Locale.getDefault())
									.contains(constraint)) {
								filteredItems.add(product);
								// filterSearchItems.add(product);

							}
						} else if (getFilterType().matches("SubCategory")) {
							if (product.getSubCategoryCode()
									.toLowerCase(Locale.getDefault())
									.contains(constraint)) {
								filteredItems.add(product);
								// filterSearchItems.add(product);

							}
						} else if (getFilterType().matches("Product")) {
							Log.d("checkcode","checkcode");
							if (product.getProductName()
									.toLowerCase(Locale.getDefault())
									.contains(constraint)
									|| product.getProductCode()
											.toLowerCase(Locale.getDefault())
											.contains(constraint)) {
								filteredItems.add(product);
								// filterSearchItems.add(product);

							}
						} else if (getFilterType().matches(
								"Category && SubCategory")) {

							if (category != null
									&& category.toString().length() > 0
									&& subcategory != null
									&& subcategory.toString().length() > 0) {
								if (product.getCategoryCode()
										.toLowerCase(Locale.getDefault())
										.contains(category)
										&& (product.getSubCategoryCode()
												.toLowerCase(
														Locale.getDefault())
												.contains(subcategory))) {
									filteredItems.add(product);
									// filterSearchItems.add(product);

								}
							}

						} else if (getFilterType().matches(
								"Category && Product")) {

							if (category != null
									&& category.toString().length() > 0
									&& prod != null
									&& prod.toString().length() > 0) {
								if (product.getCategoryCode()
										.toLowerCase(Locale.getDefault())
										.contains(category)
										&& (product.getProductName()
												.toLowerCase(
														Locale.getDefault())
												.contains(prod))) {
									filteredItems.add(product);
									// filterSearchItems.add(product);

								}
							}

						} else if (getFilterType().matches(
								"SubCategory && Product")) {

							if (subcategory != null
									&& subcategory.toString().length() > 0
									&& prod != null
									&& prod.toString().length() > 0) {
								if (product.getCategoryCode()
										.toLowerCase(Locale.getDefault())
										.contains(subcategory)
										&& (product.getProductName()
												.toLowerCase(
														Locale.getDefault())
												.contains(prod))) {
									filteredItems.add(product);
									// filterSearchItems.add(product);

								}
							}
						}

						else if (getFilterType().matches(
								"Category && SubCategory && Product")) {
							if (category != null
									&& category.toString().length() > 0
									&& subcategory != null
									&& subcategory.toString().length() > 0
									&& prod != null
									&& prod.toString().length() > 0) {
								if (product.getCategoryCode()
										.toLowerCase(Locale.getDefault())
										.contains(category)
										&& (product.getSubCategoryCode()
												.toLowerCase(
														Locale.getDefault())
												.contains(subcategory))
										&& (product.getProductName()
												.toLowerCase(
														Locale.getDefault())
												.contains(prod))) {
									filteredItems.add(product);
									// filterSearchItems.add(product);

								}
							}
						} else {
							filteredItems.add(product);
							// filterSearchItems.add(product);

						}
					}

					for (Product product : filteredItems) {
						if (sortStr.matches("SortCategory")) {
							product.setNewCategoryCode("");
						} else if (sortStr.matches("SortSubCategory")) {
							product.setNewSubCategoryCode("");
						}

					}

					result.count = filteredItems.size();
					result.values = filteredItems;
				} else {
					Log.d("empty", "--- empty");
					String oldCategory = "", oldSubCategory = "";
					for (Product product : originalList) {

						if (sortStr.matches("SortCategory")) {

							String newCategory = product.getCategoryCode();

							if (oldCategory.matches(newCategory)) {
								product.setNewCategoryCode("");
							} else {
								Log.d("oldCategory", "->" + oldCategory);
								if (oldCategory != null && !oldCategory.isEmpty()) {
									product.setNewCategoryCode(newCategory);
									oldCategory = newCategory;
									Log.d("oldCategory", "->not null" + oldCategory);
								} else {
									product.setNewCategoryCode("");
									oldCategory = newCategory;
									Log.d("oldCategory", "-> null"+ oldCategory);
								}
							}

						} else if (sortStr.matches("SortSubCategory")) {
							String newSubCategory = product
									.getSubCategoryCode();
							if (oldSubCategory.matches(newSubCategory)) {
								product.setNewSubCategoryCode("");
							} else {
								Log.d("oldCategory", "->" + oldCategory);
								if (oldSubCategory != null
										&& !oldSubCategory.isEmpty()) {
									product.setNewSubCategoryCode(newSubCategory);
									oldSubCategory = newSubCategory;
									Log.d("oldCategory", "->not null"
											+ oldSubCategory);
								} else {
									product.setNewSubCategoryCode("");
									oldSubCategory = newSubCategory;
									Log.d("oldCategory", "-> null"
											+ oldSubCategory);
								}
							}
						} else {
							product.setNewCategoryCode("");
							product.setNewSubCategoryCode("");
						}
					}
					synchronized (this) {

						result.values = originalList;
						result.count = originalList.size();
					}
				}
				return result;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				productList = (ArrayList<Product>) results.values;
				notifyDataSetChanged();
				clear();
				for (int i = 0, l = productList.size(); i < l; i++)
					add(productList.get(i));
				notifyDataSetInvalidated();
			}

		}

		// Custom keyboard setting fields
		private void customKeyboardBtn(String numbers) {
			if (mEditValue != null) {
				mEditValue.requestFocus();
				mEditValue.append(numbers);
			}

		}

		// Custom keyboard Clear fields
		private void customKeyboardClearBtn() {
			if (mEditValue != null) {
				int slength = mEditValue.length();
				if (slength > 0) {
					String qty = mEditValue.getText().toString();
					mEditValue.setText(qty.substring(0, qty.length() - 1));
					mEditValue.setSelection(mEditValue.getText().length());
				}
			}
		}

		public void batchAction(View v) {
			int position = v.getId();
			Product product = productList.get(position);

			String dbPcode = SOTDatabase.getBatchNo(product.getProductCode());

			if (dbPcode != null && !dbPcode.isEmpty()) {
				String totQty = SOTDatabase.getTotaBatchQty(dbPcode);
				int totCqty = SOTDatabase.getTotalBatchCQty(dbPcode);
				int totLqty = SOTDatabase.getTotalBatchLQty(dbPcode);
				int totFocQty = SOTDatabase.getTotaBatchFocQty(dbPcode);
				product.setQuantity(Integer.parseInt(totQty));
				product.setCQty(totCqty);
				product.setLQty(totLqty);
				product.setFocQty(totFocQty);
				qtyCalc(v, product, false);
			}
		}

		// Carton Plus Button Action
		private void cartonPlusAction(View v) {
			int position = v.getId();
			Product product = productList.get(position);
			if (tranblocknegativestock != null
					&& !tranblocknegativestock.isEmpty()
					&& tranblocknegativestock.matches("1")) {
				if (product.getStock() > 0) {
					int CQty = product.getCQty();
					if (CQty != 0) {
						cartonFlag = CQty;
					} else {
						cartonFlag = 0;
					}
					product.setCQty(++cartonFlag);
					int totalcurrCQtyDiff = product.getCQty() - CQty;
					orderCQty += totalcurrCQtyDiff;
					cQtyCalc(v, product);
				} else {
					Toast.makeText(InvoiceAddProduct.this, "Low Stock",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				int CQty = product.getCQty();
				if (CQty != 0) {
					cartonFlag = CQty;
				} else {
					cartonFlag = 0;
				}
				product.setCQty(++cartonFlag);
				int totalcurrCQtyDiff = product.getCQty() - CQty;
				orderCQty += totalcurrCQtyDiff;
				cQtyCalc(v, product);
			}

		}

		// Carton Minus Button Action
		private void cartonMinusAction(View v) {
			int position = v.getId();
			Product product = productList.get(position);
			if (tranblocknegativestock != null
					&& !tranblocknegativestock.isEmpty()
					&& tranblocknegativestock.matches("1")) {
				if (product.getStock() > 0) {
					int CQty = product.getCQty();
					if (CQty != 0) {
						cartonFlag = CQty;
						if (cartonFlag > 0) {
							product.setCQty(--cartonFlag);
						} else {
							product.setCQty(0);
						}
					}
					int totalcurrCQtyDiff = product.getCQty() - CQty;
					orderCQty += totalcurrCQtyDiff;
					cQtyCalc(v, product);
				} else {
					Toast.makeText(InvoiceAddProduct.this, "Low Stock",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				int CQty = product.getCQty();
				if (CQty != 0) {
					cartonFlag = CQty;
					if (cartonFlag > 0) {
						product.setCQty(--cartonFlag);
					} else {
						product.setCQty(0);
					}
				}
				int totalcurrCQtyDiff = product.getCQty() - CQty;
				orderCQty += totalcurrCQtyDiff;
				cQtyCalc(v, product);
			}

		}

		// Loose Plus Button Action
		private void loosePlusAction(View v) {
			int position = v.getId();
			Product product = productList.get(position);
			if (tranblocknegativestock != null
					&& !tranblocknegativestock.isEmpty()
					&& tranblocknegativestock.matches("1")) {
				if (product.getStock() > 0) {
					int lQty = product.getLQty();
					if (lQty != 0) {
						looseFlag = lQty;
					} else {
						looseFlag = 0;
					}
					product.setLQty(++looseFlag);
					int totalcurrLQtyDiff = product.getLQty() - lQty;
					orderLQty += totalcurrLQtyDiff;
					lQtyCalc(v, product);
				} else {
					Toast.makeText(InvoiceAddProduct.this, "Low Stock",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				int lQty = product.getLQty();
				if (lQty != 0) {
					looseFlag = lQty;
				} else {
					looseFlag = 0;
				}
				product.setLQty(++looseFlag);
				int totalcurrLQtyDiff = product.getLQty() - lQty;
				orderLQty += totalcurrLQtyDiff;
				lQtyCalc(v, product);
			}
		}

		// Loose Minus Button Action
		private void looseMinusAction(View v) {
			int position = v.getId();
			Product product = productList.get(position);
			if (tranblocknegativestock != null
					&& !tranblocknegativestock.isEmpty()
					&& tranblocknegativestock.matches("1")) {
				if (product.getStock() > 0) {
					int lQty = product.getLQty();
					if (lQty != 0) {
						looseFlag = lQty;
						if (looseFlag > 0) {
							product.setLQty(--looseFlag);
						} else {
							product.setLQty(0);
						}
					}
					int totalcurrLQtyDiff = product.getLQty() - lQty;
					orderLQty += totalcurrLQtyDiff;
					lQtyCalc(v, product);
				} else {
					Toast.makeText(InvoiceAddProduct.this, "Low Stock",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				int lQty = product.getLQty();
				if (lQty != 0) {
					looseFlag = lQty;
					if (looseFlag > 0) {
						product.setLQty(--looseFlag);
					} else {
						product.setLQty(0);
					}
				}
				int totalcurrLQtyDiff = product.getLQty() - lQty;
				orderLQty += totalcurrLQtyDiff;
				lQtyCalc(v, product);
			}
		}

		// Quantity Plus Button Action
		private void qtyPlusAction(View v) {
			int position = v.getId();
			Product product = productList.get(position);
			if (tranblocknegativestock != null
					&& !tranblocknegativestock.isEmpty()
					&& tranblocknegativestock.matches("1")) {
				if (product.getStock() > 0) {
					int qty = product.getQuantity();
					if (qty != 0) {
						qtyFlag = qty;
					} else {
						qtyFlag = 0;
					}
					product.setQuantity(++qtyFlag);
					int totalcurrQtyDiff = product.getQuantity() - qty;
					orderQty += totalcurrQtyDiff;
					qtyCalc(v, product, true);
				} else {
					Toast.makeText(InvoiceAddProduct.this, "Low Stock",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				int qty = product.getQuantity();
				if (qty != 0) {
					qtyFlag = qty;
				} else {
					qtyFlag = 0;
				}
				product.setQuantity(++qtyFlag);
				int totalcurrQtyDiff = product.getQuantity() - qty;
				orderQty += totalcurrQtyDiff;
				qtyCalc(v, product, true);
			}

		}

		// Quantity Minus Button Action
		private void qtyMinusAction(View v) {
			int position = v.getId();
			Product product = getItem(position);
			if (tranblocknegativestock != null
					&& !tranblocknegativestock.isEmpty()
					&& tranblocknegativestock.matches("1")) {
				if (product.getStock() > 0) {
					int qty = product.getQuantity();
					if (qty != 0) {
						qtyFlag = qty;
						if (qtyFlag > 0) {
							product.setQuantity(--qtyFlag);
						} else {
							product.setQuantity(0);
						}
					}
					int totalcurrQtyDiff = product.getQuantity() - qty;
					orderQty += totalcurrQtyDiff;
					qtyCalc(v, product, true);
				} else {
					Toast.makeText(InvoiceAddProduct.this, "Low Stock",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				int qty = product.getQuantity();
				if (qty != 0) {
					qtyFlag = qty;
					if (qtyFlag > 0) {
						product.setQuantity(--qtyFlag);
					} else {
						product.setQuantity(0);
					}
				}
				int totalcurrQtyDiff = product.getQuantity() - qty;
				orderQty += totalcurrQtyDiff;
				qtyCalc(v, product, true);
			}

		}

		// Foc Quantity Plus Button Action
		private void focQtyPlusAction(View v) {
			int position = v.getId();
			Product product = productList.get(position);
			if (tranblocknegativestock != null
					&& !tranblocknegativestock.isEmpty()
					&& tranblocknegativestock.matches("1")) {
				if (product.getStock() > 0) {
					int foc = product.getFocQty();
					if (foc != 0) {
						focQtyFlag = foc;
					} else {
						focQtyFlag = 0;
					}
					product.setFocQty(++focQtyFlag);
					int totalcurrFocQtyDiff = product.getFocQty() - foc;
					orderFocQty += totalcurrFocQtyDiff;
					notifyDataSetChanged();
				} else {
					Toast.makeText(InvoiceAddProduct.this, "Low Stock",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				int foc = product.getFocQty();
				if (foc != 0) {
					focQtyFlag = foc;
				} else {
					focQtyFlag = 0;
				}
				product.setFocQty(++focQtyFlag);
				int totalcurrFocQtyDiff = product.getFocQty() - foc;
				orderFocQty += totalcurrFocQtyDiff;
				notifyDataSetChanged();
			}

		}

		// Foc Quantity Minus Button Action
		private void focQtyMinusAction(View v) {
			int position = v.getId();
			Product product = getItem(position);
			if (tranblocknegativestock != null
					&& !tranblocknegativestock.isEmpty()
					&& tranblocknegativestock.matches("1")) {
				if (product.getStock() > 0) {
					int foc = product.getFocQty();
					if (foc != 0) {
						focQtyFlag = foc;
						if (focQtyFlag > 0) {
							product.setFocQty(--focQtyFlag);
						} else {
							product.setFocQty(0);
						}
					}
					int totalcurrFocQtyDiff = product.getFocQty() - foc;
					orderFocQty += totalcurrFocQtyDiff;
					notifyDataSetChanged();
				} else {
					Toast.makeText(InvoiceAddProduct.this, "Low Stock",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				int foc = product.getFocQty();
				if (foc != 0) {
					focQtyFlag = foc;
					if (focQtyFlag > 0) {
						product.setFocQty(--focQtyFlag);
					} else {
						product.setFocQty(0);
					}
				}
				int totalcurrFocQtyDiff = product.getFocQty() - foc;
				orderFocQty += totalcurrFocQtyDiff;
				notifyDataSetChanged();
			}

		}

		// Exchange Quantity Plus Button Action
		private void exchangeQtyPlusAction(View v) {
			int position = v.getId();
			Product product = productList.get(position);
			if (tranblocknegativestock != null
					&& !tranblocknegativestock.isEmpty()
					&& tranblocknegativestock.matches("1")) {
				if (product.getStock() > 0) {
					int exchangeQty = product.getExchangeQty();
					if (exchangeQty != 0) {
						exchangeQtyFlag = exchangeQty;
					} else {
						exchangeQtyFlag = 0;
					}
					product.setExchangeQty(++exchangeQtyFlag);
					int totalcurrExchangeQtyDiff = product.getExchangeQty()
							- exchangeQty;
					orderExchQty += totalcurrExchangeQtyDiff;
					notifyDataSetChanged();
				} else {
					Toast.makeText(InvoiceAddProduct.this, "Low Stock",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				int exchangeQty = product.getExchangeQty();
				if (exchangeQty != 0) {
					exchangeQtyFlag = exchangeQty;
				} else {
					exchangeQtyFlag = 0;
				}
				product.setExchangeQty(++exchangeQtyFlag);
				int totalcurrExchangeQtyDiff = product.getExchangeQty()
						- exchangeQty;
				orderExchQty += totalcurrExchangeQtyDiff;
				notifyDataSetChanged();
			}

		}

		// Exchange Quantity Minus Button Action
		private void exchangeQtyMinusAction(View v) {
			int position = v.getId();
			Product product = getItem(position);
			if (tranblocknegativestock != null
					&& !tranblocknegativestock.isEmpty()
					&& tranblocknegativestock.matches("1")) {
				if (product.getStock() > 0) {
					int exchangeQty = product.getExchangeQty();
					if (exchangeQty != 0) {
						exchangeQtyFlag = exchangeQty;
						if (exchangeQtyFlag > 0) {
							product.setExchangeQty(--exchangeQtyFlag);
						} else {
							product.setExchangeQty(0);
						}
					}
					int totalcurrexchangeQtyDiff = product.getExchangeQty()
							- exchangeQty;
					orderExchQty += totalcurrexchangeQtyDiff;
					notifyDataSetChanged();
				} else {
					Toast.makeText(InvoiceAddProduct.this, "Low Stock",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				int exchangeQty = product.getExchangeQty();
				if (exchangeQty != 0) {
					exchangeQtyFlag = exchangeQty;
					if (exchangeQtyFlag > 0) {
						product.setExchangeQty(--exchangeQtyFlag);
					} else {
						product.setExchangeQty(0);
					}
				}
				int totalcurrexchangeQtyDiff = product.getExchangeQty()
						- exchangeQty;
				orderExchQty += totalcurrexchangeQtyDiff;
				notifyDataSetChanged();
			}

		}

		private void returnQtyPlusAction(View v) {
			int position = v.getId();
			Product product = productList.get(position);
			if (tranblocknegativestock != null
					&& !tranblocknegativestock.isEmpty()
					&& tranblocknegativestock.matches("1")) {
				if (product.getStock() > 0) {
					int retQty = product.getReturn_qty();
					if (retQty != 0) {
						retQtyFlag = retQty;
					} else {
						retQtyFlag = 0;
					}
					product.setReturn_qty(++retQtyFlag);
					int totalcurrExchangeQtyDiff = product.getReturn_qty()
							- retQty;
					orderRetQty += totalcurrExchangeQtyDiff;
					notifyDataSetChanged();
				} else {
					Toast.makeText(InvoiceAddProduct.this, "Low Stock",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				int retQty = product.getReturn_qty();
				if (retQty != 0) {
					retQtyFlag = retQty;
				} else {
					retQtyFlag = 0;
				}
				product.setReturn_qty(++retQtyFlag);
				int totalcurrExchangeQtyDiff = product.getReturn_qty()
						- retQty;
				orderRetQty += totalcurrExchangeQtyDiff;
				notifyDataSetChanged();
			}

		}

		private void returnQtyMinusAction(View v) {
			int position = v.getId();
			Product product = getItem(position);
			if (tranblocknegativestock != null
					&& !tranblocknegativestock.isEmpty()
					&& tranblocknegativestock.matches("1")) {
				if (product.getStock() > 0) {
					int retQty = product.getReturn_qty();
					if (retQty != 0) {
						retQtyFlag = retQty;
						if (retQtyFlag > 0) {
							product.setReturn_qty(--retQtyFlag);
						} else {
							product.setReturn_qty(0);
						}
					}
					int totalcurrexchangeQtyDiff = product.getReturn_qty()
							- retQty;
					orderRetQty += totalcurrexchangeQtyDiff;
					notifyDataSetChanged();
				} else {
					Toast.makeText(InvoiceAddProduct.this, "Low Stock",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				int returnQty = product.getReturn_qty();
				if (returnQty != 0) {
					retQtyFlag = returnQty;
					if (retQtyFlag > 0) {
						product.setReturn_qty(--retQtyFlag);
					} else {
						product.setReturn_qty(0);
					}
				}
				int totalcurrexchangeQtyDiff = product.getReturn_qty()
						- returnQty;
				orderRetQty += totalcurrexchangeQtyDiff;
				notifyDataSetChanged();
			}

		}

		public void cQtyCalc(View v, Product product) {
			try {
				Double extTotal = 0.00, subTotal = 0.00;
				Double currTotal = product.getExt();
				Double currSubTotal = product.getSubTot();
				// /Qty recalc,if calcarton equal to One
				if (calCarton.matches("1")) {
					if (product.getLQty() > 0) {
						int qty = product.getCQty() * product.getPcs()
								+ product.getLQty();
						product.setQuantity(qty);
					} else {
						int qty = product.getCQty() * product.getPcs();
						product.setQuantity(qty);
					}
					/*
					 * if (tranblocknegativestock != null &&
					 * !tranblocknegativestock.isEmpty() &&
					 * tranblocknegativestock.matches("1")) { int stock =
					 * product.getStock().intValue(); if (stock <
					 * product.getQuantity()) {
					 * Toast.makeText(InvoiceAddProduct.this, "Low Stock",
					 * Toast.LENGTH_SHORT).show(); product.setQuantity(0); } }
					 */
					// if priceflag 1 mean carton price visible
					if (priceflag.matches("1")) {
						Double cprice = product.getCQty() * product.getCprice();
						Double lprice = product.getLQty() * product.getLprice();
						extTotal = cprice + lprice;
					} else {
						// if priceflag 0 mean carton price gone
						extTotal = product.getQuantity() * product.getLprice();
					}
					Double totalDiff = Double.valueOf(df.format(extTotal
							- currTotal));
					product.setExt(Double.valueOf(extTotal));
					orderTotal += totalDiff;
					if (product.getDiscount() != 0) {
						subTotal = product.getExt() - product.getDiscount();
					} else {
						subTotal = product.getExt();
					}
					product.setSubTot(subTotal);
					Double subTotalDiff = Double.valueOf(df.format(subTotal
							- currSubTotal));
					orderSubTotal += subTotalDiff;
					taxCalc(v, product, false);
				}
				notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void lQtyCalc(View v, Product product) {
			try {
				Double extTotal = 0.00, subTotal = 0.00;
				Double currTotal = product.getExt();
				Double currSubTotal = product.getSubTot();
				// Qty recalc,if calcarton equal to One
				if (calCarton.matches("1")) {

					/*
					 * if (tranblocknegativestock != null &&
					 * !tranblocknegativestock.isEmpty() &&
					 * tranblocknegativestock.matches("1")) {
					 * 
					 * int stock = product.getStock().intValue();
					 * 
					 * if (stock < product.getQuantity()) {
					 * Toast.makeText(InvoiceAddProduct.this, "Low Stock",
					 * Toast.LENGTH_SHORT).show();
					 * 
					 * product.setQuantity(0); } }
					 */
					// if priceflag 1 mean carton price visible
					if (priceflag.matches("1")) {
						int qty = product.getCQty() * product.getPcs()
								+ product.getLQty();
						Log.d("qty", "" + qty);
						product.setQuantity(qty);
						Double cprice = product.getCQty() * product.getCprice();
						Double lprice = product.getLQty() * product.getLprice();
						extTotal = cprice + lprice;
					} else {

						int qty = product.getCQty() * product.getPcs()
								+ product.getLQty();
						Log.d("qty", "" + qty);
						product.setQuantity(qty);

						extTotal = product.getQuantity() * product.getLprice();
					}

					Double totalDiff = Double.valueOf(df.format(extTotal
							- currTotal));

					product.setExt(Double.valueOf(extTotal));

					orderTotal += totalDiff;

					if (product.getDiscount() != 0) {
						subTotal = product.getExt() - product.getDiscount();
					} else {
						subTotal = product.getExt();
					}
					product.setSubTot(subTotal);

					Double subTotalDiff = Double.valueOf(df.format(subTotal
							- currSubTotal));

					orderSubTotal += subTotalDiff;

					taxCalc(v, product, false);

				}
				notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void qtyCalc(View v, Product product, boolean flag) {
			try {
				Double extTotal = 0.00, subTotal = 0.00;

				Double currTotal = product.getExt();
				Double currSubTotal = product.getSubTot();

				// Qty recalc,if calcarton equal to One
				if (calCarton.matches("1")) {

					if (flag) {
						int cqty = product.getQuantity() / product.getPcs();
						product.setCQty(cqty);
						int lqty = product.getQuantity() % product.getPcs();
						product.setLQty(lqty);
					}

					// if priceflag 1 mean carton price visible
					if (priceflag.matches("1")) {
						Double cprice = product.getCQty() * product.getCprice();
						Double lprice = product.getLQty() * product.getLprice();
						extTotal = cprice + lprice;
					} else {
						// if priceflag 0 mean carton price gone
						extTotal = product.getQuantity() * product.getLprice();
					}

					Double priceDiff = Double.valueOf(df.format(extTotal
							- currTotal));
					product.setExt(extTotal);
					orderTotal += priceDiff;
					if (product.getDiscount() != 0) {
						subTotal = product.getExt() - product.getDiscount();
					} else {
						subTotal = product.getExt();
					}
					product.setSubTot(subTotal);
					Double subTotalDiff = Double.valueOf(df.format(subTotal
							- currSubTotal));
					orderSubTotal += subTotalDiff;
					taxCalc(v, product, false);
				} else {
					extTotal = product.getQuantity() * product.getLprice();
					Double priceDiff = Double.valueOf(df.format(extTotal
							- currTotal));
					product.setExt(extTotal);
					orderTotal += priceDiff;
					if (product.getDiscount() != 0) {
						subTotal = product.getExt() - product.getDiscount();
					} else {
						subTotal = product.getExt();
					}
					product.setSubTot(subTotal);
					Double subTotalDiff = Double.valueOf(df.format(subTotal
							- currSubTotal));
					orderSubTotal += subTotalDiff;
					taxCalc(v, product, false);

				}
				notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Quantity Text Watcher
		class QuantityTextWatcher implements TextWatcher {

			private View view;

			public QuantityTextWatcher(View view) {
				this.view = view;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// do nothing
			}

			@Override
			public void afterTextChanged(Editable s) {
				Double extTotal = 0.00, subTotal = 0.00;

				DecimalFormat df = new DecimalFormat("0.00##");
				String qtyString = s.toString().trim();
				int quantity = qtyString.equals("") ? 0 : Integer
						.valueOf(qtyString);

				EditText qtyView = (EditText) view.findViewById(R.id.quantity);

				EditText cqtyView = (EditText) view
						.findViewById(R.id.cQuantity);

				EditText lqtyView = (EditText) view
						.findViewById(R.id.lQuantity);

				TextView mSubTotal = (TextView) view
						.findViewById(R.id.subTotal);

				Product product = (Product) qtyView.getTag();
				qtyView.setSelection(qtyView.getText().length());

				if (product.getQuantity() != quantity) {
					int totalcurrQtyDiff = quantity - product.getQuantity();
					Double currTotal = product.getExt();
					Double currSubTotal = product.getSubTot();

					// Qty recalc,if calcarton equal to One
					if (calCarton.matches("1")) {

						product.setQuantity(quantity);

						int cqty = quantity / product.getPcs();
						product.setCQty(cqty);

						int lqty = quantity % product.getPcs();
						product.setLQty(lqty);

						Log.d("lqty", "" + lqty);
						Log.d("cqty", "" + cqty);
						Log.d("extPrice", "" + extTotal);
						// }
						// if priceflag 1 mean carton price visible
						if (priceflag.matches("1")) {
							Double cprice = product.getCQty()
									* product.getCprice();
							Double lprice = product.getLQty()
									* product.getLprice();
							extTotal = cprice + lprice;

						} else {
							// if priceflag 0 mean carton price gone
							extTotal = quantity * product.getLprice();
						}

						Double priceDiff = Double.valueOf(df.format(extTotal
								- currTotal));

						product.setExt(extTotal);
						TextView ext = (TextView) view.findViewById(R.id.ext);

						ext.setText(df.format(product.getExt()));

						if (product.getQuantity() != 0) {
							qtyView.setText(String.valueOf(product
									.getQuantity()));
						} else {
							qtyView.setText("0");
						}

						if (product.getCQty() != 0) {
							cqtyView.setText(String.valueOf(product.getCQty()));
						} else {
							cqtyView.setText("0");
						}

						if (product.getLQty() != 0) {
							lqtyView.setText(String.valueOf(product.getLQty()));
						} else {
							lqtyView.setText("0");
						}

						int stock = product.getStock().intValue();
						if (tranblocknegativestock != null
								&& !tranblocknegativestock.isEmpty()
								&& tranblocknegativestock.matches("1")) {

							if (stock < quantity) {
								Toast.makeText(InvoiceAddProduct.this,
										"Low Stock", Toast.LENGTH_SHORT).show();
								qtyView.setText("0");
							}
						}
						orderQty += totalcurrQtyDiff;
						orderTotal += priceDiff;

						if (product.getDiscount() != 0) {
							subTotal = product.getExt() - product.getDiscount();
						} else {
							subTotal = product.getExt();
						}
						product.setSubTot(subTotal);

						mSubTotal.setText(df.format(product.getSubTot()));

						Double subTotalDiff = Double.valueOf(df.format(subTotal
								- currSubTotal));

						orderSubTotal += subTotalDiff;

						taxCalc(view, product, true);

					} else {
						extTotal = quantity * product.getLprice();

						Double priceDiff = Double.valueOf(df.format(extTotal
								- currTotal));

						product.setQuantity(quantity);
						product.setExt(extTotal);

						TextView ext = (TextView) view.findViewById(R.id.ext);

						ext.setText(df.format(product.getExt()));

						if (product.getQuantity() != 0) {
							qtyView.setText(String.valueOf(product
									.getQuantity()));
						} else {
							qtyView.setText("0");
						}
						orderQty += totalcurrQtyDiff;
						orderTotal += priceDiff;

						if (product.getDiscount() != 0) {
							subTotal = product.getExt() - product.getDiscount();
						} else {
							subTotal = product.getExt();
						}
						product.setSubTot(subTotal);

						mSubTotal.setText(df.format(product.getSubTot()));

						Double subTotalDiff = Double.valueOf(df.format(subTotal
								- currSubTotal));

						orderSubTotal += subTotalDiff;

						taxCalc(view, product, true);

					}

				}

				return;
			}
		}

		// Carton Quantity Text Watcher
		class CQtyTextWatcher implements TextWatcher {

			private View view;

			public CQtyTextWatcher(View view) {
				this.view = view;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				Double extTotal = 0.00, subTotal = 0.00;
				DecimalFormat df = new DecimalFormat("0.00##");
				String cqtyString = s.toString().trim();

				int cquantity = cqtyString.equals("") ? 0 : Integer
						.valueOf(cqtyString);

				EditText qtyView = (EditText) view.findViewById(R.id.quantity);

				EditText cqtyView = (EditText) view
						.findViewById(R.id.cQuantity);

				TextView mSubTotal = (TextView) view
						.findViewById(R.id.subTotal);

				Product product = (Product) cqtyView.getTag();
				cqtyView.setSelection(cqtyView.getText().length());

				if (product.getCQty() != cquantity) {
					int totalcurrCQtyDiff = cquantity - product.getCQty();
					Double currTotal = product.getExt();
					Double currSubTotal = product.getSubTot();
					// /Qty recalc,if calcarton equal to One
					if (calCarton.matches("1")) {
						if (product.getLQty() > 0) {

							int qty = cquantity * product.getPcs()
									+ product.getLQty();
							Log.d("qty", "" + qty);
							product.setQuantity(qty);
						} else {
							int qty = cquantity * product.getPcs();
							Log.d("qty", "" + qty);
							product.setQuantity(qty);

						}

						product.setCQty(cquantity);
						// if priceflag 1 mean carton price visible
						if (priceflag.matches("1")) {
							Double cprice = product.getCQty()
									* product.getCprice();
							Double lprice = product.getLQty()
									* product.getLprice();
							extTotal = cprice + lprice;
						} else {
							// if priceflag 0 mean carton price gone
							extTotal = product.getQuantity()
									* product.getLprice();
						}

						Double totalDiff = Double.valueOf(df.format(extTotal
								- currTotal));

						product.setExt(Double.valueOf(extTotal));

						TextView ext = (TextView) view.findViewById(R.id.ext);
						ext.setText(df.format(product.getExt()));

						if (product.getCQty() != 0) {
							cqtyView.setText(String.valueOf(product.getCQty()));
						} else {
							cqtyView.setText("0");
						}

						if (product.getQuantity() != 0) {

							qtyView.setText(String.valueOf(product
									.getQuantity()));
						} else {
							qtyView.setText("0");
						}
						if (tranblocknegativestock != null
								&& !tranblocknegativestock.isEmpty()
								&& tranblocknegativestock.matches("1")) {

							int stock = product.getStock().intValue();

							if (stock < product.getQuantity()) {
								Toast.makeText(InvoiceAddProduct.this,
										"Low Stock", Toast.LENGTH_SHORT).show();

								qtyView.setText("0");
							}
						}

						orderCQty += totalcurrCQtyDiff;
						orderTotal += totalDiff;

						if (product.getDiscount() != 0) {
							subTotal = product.getExt() - product.getDiscount();
						} else {
							subTotal = product.getExt();
						}
						product.setSubTot(subTotal);

						mSubTotal.setText(df.format(product.getSubTot()));

						Double subTotalDiff = Double.valueOf(df.format(subTotal
								- currSubTotal));

						orderSubTotal += subTotalDiff;

						taxCalc(view, product, true);
					} else {
						product.setCQty(cquantity);
						orderCQty += totalcurrCQtyDiff;

					}

				}

				return;
			}
		}

		// Loose Quantity Text Watcher
		class LQtyTextWatcher implements TextWatcher {

			private View view;

			public LQtyTextWatcher(View view) {
				this.view = view;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				Double extTotal = 0.00, subTotal = 0.00;

				DecimalFormat df = new DecimalFormat("0.00##");
				String cqtyString = s.toString().trim();
				int lquantity = cqtyString.equals("") ? 0 : Integer
						.valueOf(cqtyString);

				EditText qtyView = (EditText) view.findViewById(R.id.quantity);

				EditText cqtyView = (EditText) view
						.findViewById(R.id.cQuantity);

				EditText lqtyView = (EditText) view
						.findViewById(R.id.lQuantity);

				TextView mSubTotal = (TextView) view
						.findViewById(R.id.subTotal);

				Product product = (Product) lqtyView.getTag();
				lqtyView.setSelection(lqtyView.getText().length());

				if (product.getLQty() != lquantity) {
					int totalcurrLQtyDiff = lquantity - product.getLQty();
					Double currTotal = product.getExt();
					Double currSubTotal = product.getSubTot();
					// Qty recalc,if calcarton equal to One
					if (calCarton.matches("1")) {
						// if priceflag 1 mean carton price visible
						if (priceflag.matches("1")) {
							int qty = product.getCQty() * product.getPcs()
									+ lquantity;
							Log.d("qty", "" + qty);
							product.setQuantity(qty);
							product.setLQty(lquantity);
							Double cprice = product.getCQty()
									* product.getCprice();
							Double lprice = product.getLQty()
									* product.getLprice();
							extTotal = cprice + lprice;
						} else {
							// if priceflag 0 mean carton price gone
							if (cqtyView.getText().toString().equals("")
									|| cqtyView.getText().length() == 0) {
								product.setQuantity(lquantity);
								product.setLQty(lquantity);
							} else {
								int qty = product.getCQty() * product.getPcs()
										+ lquantity;
								Log.d("qty", "" + qty);
								product.setQuantity(qty);
								product.setLQty(lquantity);
							}

							extTotal = product.getQuantity()
									* product.getLprice();
						}

						Double totalDiff = Double.valueOf(df.format(extTotal
								- currTotal));

						product.setExt(Double.valueOf(extTotal));

						TextView ext = (TextView) view.findViewById(R.id.ext);

						ext.setText(df.format(product.getExt()));

						if (product.getCQty() != 0) {
							cqtyView.setText(String.valueOf(product.getCQty()));
						} else {
							cqtyView.setText("0");
						}

						if (product.getLQty() != 0) {
							lqtyView.setText(String.valueOf(product.getLQty()));
						} else {
							lqtyView.setText("0");
						}

						if (product.getQuantity() != 0) {

							qtyView.setText(String.valueOf(product
									.getQuantity()));
						} else {
							qtyView.setText("0");
						}
						if (tranblocknegativestock != null
								&& !tranblocknegativestock.isEmpty()
								&& tranblocknegativestock.matches("1")) {

							int stock = product.getStock().intValue();

							if (stock < product.getQuantity()) {
								Toast.makeText(InvoiceAddProduct.this,
										"Low Stock", Toast.LENGTH_SHORT).show();

								qtyView.setText("0");
							}
						}
						orderLQty += totalcurrLQtyDiff;
						orderTotal += totalDiff;

						if (product.getDiscount() != 0) {
							subTotal = product.getExt() - product.getDiscount();
						} else {
							subTotal = product.getExt();
						}
						product.setSubTot(subTotal);

						mSubTotal.setText(df.format(product.getSubTot()));

						Double subTotalDiff = Double.valueOf(df.format(subTotal
								- currSubTotal));

						orderSubTotal += subTotalDiff;

						taxCalc(view, product, true);

					} else {
						product.setLQty(lquantity);
						orderLQty += totalcurrLQtyDiff;

					}
				}

				return;
			}
		}

		// Discount Text Watcher
		class DiscountTextWatcher implements TextWatcher {

			private View view;

			public DiscountTextWatcher(View view) {
				this.view = view;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			@SuppressLint("UseValueOf")
			public void afterTextChanged(Editable s) {
				Double subTotal = 0.00;
				String discountStr = s.toString().trim();

				double discount;
				try {
					discount = new Double(discountStr);
					// discount = discountStr.equals("") ? 0 :
					// Double.valueOf(discountStr);
				} catch (NumberFormatException e) {
					discount = 0; // default value
				}

				/*
				 * int discount = discountStr.equals("") ? 0 : Integer
				 * .valueOf(discountStr);
				 */

				EditText discountView = (EditText) view
						.findViewById(R.id.discount);
				TextView mSubTotal = (TextView) view
						.findViewById(R.id.subTotal);
				Product product = (Product) discountView.getTag();
				discountView.setSelection(discountView.getText().length());

				if (product.getDiscount() != discount) {
					Double totalItemDiscountDiff = discount
							- product.getDiscount();
					Double currSubTotal = product.getSubTot();
					product.setDiscount(discount);

					if (product.getDiscount() != 0) {
						subTotal = product.getExt() - discount;
					} else {
						subTotal = product.getExt();
					}

					product.setSubTot(subTotal);

					if (product.getDiscount() != 0) {
						discountView.setText(String.valueOf(product
								.getDiscount()));
					} else {
						discountView.setText("0");
					}

					mSubTotal.setText(df.format(product.getSubTot()));

					Double subTotalDiff = Double.valueOf(df.format(subTotal
							- currSubTotal));

					Log.d("subTotal", "" + subTotal);

					Log.d("currSubTotal", "" + currSubTotal);

					Log.d("orderSubTotal", "" + orderSubTotal);

					Log.d("subTotalDiff", "" + subTotalDiff);

					// double x = Math.abs(subTotalDiff);

					orderSubTotal += subTotalDiff;

					orderTotalItemDiscount += totalItemDiscountDiff;

					taxCalc(view, product, true);
				}

				return;
			}
		}

		// Price Text Watcher
		class PriceTextWatcher implements TextWatcher {

			private View view;

			public PriceTextWatcher(View view) {
				this.view = view;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			@SuppressLint("UseValueOf")
			public void afterTextChanged(Editable s) {
				Double extTotal = 0.00, subTotal = 0.00;
				String priceStr = s.toString().trim();
				double price, minimumSellingPrice = 0;
				try {
					price = new Double(priceStr);
				} catch (NumberFormatException e) {
					price = 0; // default value
				}
				// Double price = priceStr.equals("") ? 0.0 :
				// Double.valueOf(priceStr);
				EditText priceEdt = (EditText) view.findViewById(R.id.price);
				Product product = (Product) priceEdt.getTag();
				priceEdt.setSelection(priceEdt.getText().length());
				if (product.getLprice() != price) {
					/*
					 * Log.d("minimum selling price", "" +
					 * product.getMinimumSellingPrice());
					 */
					product.setLprice(price);
					if (product.getMinimumSellingPrice() > price) {
						Toast.makeText(
								InvoiceAddProduct.this,
								"Price must be greater than minimum selling price $ "
										+ product.getMinimumSellingPrice(),
								Toast.LENGTH_LONG).show();
						product.setLprice(minimumSellingPrice);
					}

					Double currTotal = product.getExt();
					Double currSubTotal = product.getSubTot();
					// Qty recalc,if calcarton equal to One
					if (calCarton.matches("1")) {
						// if priceflag 1 mean carton price visible
						if (priceflag.matches("1")) {
							Double cprice = product.getCQty()
									* product.getCprice();
							Double lprice = product.getLQty()
									* product.getLprice();
							extTotal = cprice + lprice;
						} else {
							// if priceflag 0 mean carton price gone
							extTotal = product.getQuantity()
									* product.getLprice();
						}
					} else {
						extTotal = product.getQuantity() * product.getLprice();
					}
					Double totalDiff = Double.valueOf(df.format(extTotal
							- currTotal));

					product.setExt(Double.valueOf(extTotal));

					TextView ext = (TextView) view.findViewById(R.id.ext);
					// if (product.getCQty() != 0) {

					ext.setText(df.format(product.getExt()));

					TextView mSubTotal = (TextView) view
							.findViewById(R.id.subTotal);
					/*
					 * } else { ext.setText(""); }
					 */

					orderTotal += totalDiff;

					if (product.getDiscount() != 0) {
						subTotal = product.getExt() - product.getDiscount();
					} else {
						subTotal = product.getExt();
					}
					product.setSubTot(subTotal);

					mSubTotal.setText(df.format(product.getSubTot()));

					Double subTotalDiff = Double.valueOf(df.format(subTotal
							- currSubTotal));

					Log.d("currSubTotal", "" + currSubTotal);
					Log.d("subTotal", "" + subTotal);
					Log.d("subTotalDiff", "" + subTotalDiff);

					orderSubTotal += subTotalDiff;

					taxCalc(view, product, true);

				}
				return;
			}
		}

		// Carton price Text Watcher
		class CPriceTextWatcher implements TextWatcher {

			private View view;

			public CPriceTextWatcher(View view) {
				this.view = view;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			@SuppressLint("UseValueOf")
			public void afterTextChanged(Editable s) {
				Double extTotal = 0.00, subTotal = 0.00;
				String cpriceStr = s.toString().trim();
				double cartonprice,minimumCartonSellingPrice=0;
				try {
					cartonprice = new Double(cpriceStr);
				} catch (NumberFormatException e) {
					cartonprice = 0; // default value
				}
				// Double cartonprice = cpriceStr.equals("") ? 0 :
				// Double.valueOf(cpriceStr);
				EditText cpriceEdt = (EditText) view.findViewById(R.id.cprice);
				Product product = (Product) cpriceEdt.getTag();
				cpriceEdt.setSelection(cpriceEdt.getText().length());

				if (product.getCprice() != cartonprice) {
					product.setCprice(cartonprice);
					Double currTotal = product.getExt();
					Double currSubTotal = product.getSubTot();
					// Qty recalc,if calcarton equal to One
					if (calCarton.matches("1")) {
						// if priceflag 1 mean carton price visible
						if (priceflag.matches("1")) {

							if (product.getMinimumCartonSellingPrice() > cartonprice) {
								Toast.makeText(
										InvoiceAddProduct.this,
										"Carton Price must be greater than minimum carton selling price $ "
												+ product.getMinimumCartonSellingPrice(),
										Toast.LENGTH_LONG).show();
								product.setCprice(minimumCartonSellingPrice);
							}

							Double cprice = product.getCQty()
									* product.getCprice();
							Double lprice = product.getLQty()
									* product.getLprice();
							extTotal = cprice + lprice;
						} else {
							// if priceflag 0 mean carton price gone
							extTotal = product.getQuantity()
									* product.getLprice();
						}

						Double totalDiff = Double.valueOf(df.format(extTotal
								- currTotal));

						product.setExt(Double.valueOf(extTotal));

						TextView ext = (TextView) view.findViewById(R.id.ext);

						ext.setText(df.format(product.getExt()));

						TextView mSubTotal = (TextView) view
								.findViewById(R.id.subTotal);

						orderTotal += totalDiff;

						if (product.getDiscount() != 0) {
							subTotal = product.getExt() - product.getDiscount();
						} else {
							subTotal = product.getExt();
						}
						product.setSubTot(subTotal);

						mSubTotal.setText(df.format(product.getSubTot()));

						Double subTotalDiff = Double.valueOf(df.format(subTotal
								- currSubTotal));

						orderSubTotal += subTotalDiff;

						taxCalc(view, product, true);
					}

				}

				return;
			}
		}

		// FocQty Text Watcher
		class FocQtyTextWatcher implements TextWatcher {

			private View view;

			public FocQtyTextWatcher(View view) {
				this.view = view;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			@SuppressLint("UseValueOf")
			public void afterTextChanged(Editable s) {
				String focStr = s.toString().trim();
				int foc;
				try {
					foc = focStr.equals("") ? 0 : Integer.valueOf(focStr);
				} catch (NumberFormatException e) {
					foc = 0; // default value
				}

				EditText mFocQty = (EditText) view.findViewById(R.id.focQty);
				Product product = (Product) mFocQty.getTag();
				mFocQty.setSelection(mFocQty.getText().length());

				if (product.getFocQty() != foc) {
					int totalcurrFocQtyDiff = foc - product.getFocQty();
					product.setFocQty(foc);
					if (product.getFocQty() != 0) {
						mFocQty.setText(String.valueOf(product.getFocQty()));
					} else {
						mFocQty.setText("0");
					}
					if (tranblocknegativestock != null
							&& !tranblocknegativestock.isEmpty()
							&& tranblocknegativestock.matches("1")) {
						if (product.getStock() > 0) {
							orderFocQty += totalcurrFocQtyDiff;
						} else {
							mFocQty.setText("0");
							Toast.makeText(InvoiceAddProduct.this, "Low Stock",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						orderFocQty += totalcurrFocQtyDiff;
					}
				}
				return;
			}
		}

		// ExchangeQty Text Watcher
		class ExchangeQtyTextWatcher implements TextWatcher {

			private View view;

			public ExchangeQtyTextWatcher(View view) {
				this.view = view;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			@SuppressLint("UseValueOf")
			public void afterTextChanged(Editable s) {

				String exchangeStr = s.toString().trim();

				int exchangeQty;
				try {
					exchangeQty = exchangeStr.equals("") ? 0 : Integer
							.valueOf(exchangeStr);
				} catch (NumberFormatException e) {
					exchangeQty = 0; // default value
				}

				/*
				 * int exchangeQty = exchangeStr.equals("") ? 0 : Integer
				 * .valueOf(exchangeStr);
				 */
				EditText mExchangeQty = (EditText) view
						.findViewById(R.id.exchangeQty);
				Product product = (Product) mExchangeQty.getTag();
				mExchangeQty.setSelection(mExchangeQty.getText().length());

				if (product.getExchangeQty() != exchangeQty) {
					int totalcurrExchQtyDiff = exchangeQty
							- product.getExchangeQty();
					product.setExchangeQty(exchangeQty);
					if (product.getExchangeQty() != 0) {
						mExchangeQty.setText(String.valueOf(product
								.getExchangeQty()));
					} else {
						mExchangeQty.setText("0");
					}

					if (tranblocknegativestock != null
							&& !tranblocknegativestock.isEmpty()
							&& tranblocknegativestock.matches("1")) {
						if (product.getStock() > 0) {
							orderExchQty += totalcurrExchQtyDiff;
						} else {
							mExchangeQty.setText("0");
							Toast.makeText(InvoiceAddProduct.this, "Low Stock",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						orderExchQty += totalcurrExchQtyDiff;
					}

				}

				return;
			}
		}

		//return Text watcher

		class ReturnQtyTextWatcher implements TextWatcher {

			private View view;

			public ReturnQtyTextWatcher(View view) {
				this.view = view;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {

			}

			@Override
			@SuppressLint("UseValueOf")
			public void afterTextChanged(Editable s) {

				String returnStr = s.toString().trim();

				int exchangeQty;
				try {
					exchangeQty = returnStr.equals("") ? 0 : Integer
							.valueOf(returnStr);
				} catch (NumberFormatException e) {
					exchangeQty = 0; // default value
				}

				/*
				 * int exchangeQty = exchangeStr.equals("") ? 0 : Integer
				 * .valueOf(exchangeStr);
				 */
				EditText mExchangeQty = (EditText) view
						.findViewById(R.id.retQty);
				Product product = (Product) mExchangeQty.getTag();
				mExchangeQty.setSelection(mExchangeQty.getText().length());

				if (product.getReturn_qty() != exchangeQty) {
					int totalcurrExchQtyDiff = exchangeQty
							- product.getReturn_qty();
					product.setReturn_qty(exchangeQty);
					if (product.getReturn_qty() != 0) {
						mExchangeQty.setText(String.valueOf(product
								.getReturn_qty()));
					} else {
						mExchangeQty.setText("0");
					}

					if (tranblocknegativestock != null
							&& !tranblocknegativestock.isEmpty()
							&& tranblocknegativestock.matches("1")) {
						if (product.getStock() > 0) {
							orderRetQty += totalcurrExchQtyDiff;
						} else {
							mExchangeQty.setText("0");
							Toast.makeText(InvoiceAddProduct.this, "Low Stock",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						orderRetQty += totalcurrExchQtyDiff;
					}

				}

				return;
			}
		}

		public void taxCalc(View view, Product product, boolean isView) {
			Double total = 0.00, subTotal = 0.00, taxAmount = 0.00, netTotal = 0.00;
			String taxPerc = product.getTaxPerc();
			TextView mTax = (TextView) view.findViewById(R.id.tax);
			TextView mNetTotal = (TextView) view.findViewById(R.id.netTotal);
			total = product.getExt();
			subTotal = product.getSubTot();
			Double currNetTotal = product.getNetTot();
			Double currTax = product.getTaxPer();
			try {
				double taxValueCalc = Double.parseDouble(taxPerc);
				if (taxType.matches("E")) {

					if (product.getDiscount() != 0) {
						taxAmount = (subTotal * taxValueCalc) / 100;
						product.setTaxPer(Double
								.valueOf(fourDecimalPoint(taxAmount)));
						netTotal = subTotal + taxAmount;
						product.setNetTot(netTotal);
					} else {

						taxAmount = (total * taxValueCalc) / 100;
						product.setTaxPer(Double
								.valueOf(fourDecimalPoint(taxAmount)));
						netTotal = total + taxAmount;
						product.setNetTot(netTotal);
					}

				} else if (taxType.matches("I")) {
					if (product.getDiscount() != 0) {
						taxAmount = (subTotal * taxValueCalc)
								/ (100 + taxValueCalc);
						product.setTaxPer(Double
								.valueOf(fourDecimalPoint(taxAmount)));
						netTotal = subTotal;
						product.setNetTot(netTotal);
					} else {
						taxAmount = (total * taxValueCalc)
								/ (100 + taxValueCalc);
						product.setTaxPer(Double
								.valueOf(fourDecimalPoint(taxAmount)));
						netTotal = total;
						product.setNetTot(netTotal);
					}
				} else if (taxType.matches("Z")) {
					if (product.getDiscount() != 0) {
						netTotal = subTotal;
						product.setNetTot(netTotal);
					} else {
						netTotal = total;
						product.setNetTot(netTotal);
					}
					product.setTaxPer(0.0000);
					taxAmount = 0.0000;
				} else {
					product.setTaxPer(0.0000);
					product.setNetTot(total);
					netTotal = total;
					taxAmount = 0.0000;
				}
				if (isView) {
					if (product.getTaxPer() != 0) {
						mTax.setText(fourDecimalPoint(product.getTaxPer()));
					} else {
						mTax.setText("0.0000");
					}
					if (product.getNetTot() != 0) {
						mNetTotal.setText(twoDecimalPoint(product.getNetTot()));
					} else {
						mNetTotal.setText("0.00");
					}
				}
				Double taxDiff = Double.valueOf(fourDecimalPoint(taxAmount
						- currTax));
				orderTax += taxDiff;
				Double netTotalDiff = Double.valueOf(df.format(netTotal
						- currNetTotal));
				orderNetTotal += netTotalDiff;
				mTotalItemDiscount.setText(String
						.valueOf(orderTotalItemDiscount));
				mTotalItem.setText(df.format(orderTotal));
				mTaxItem.setText(fourDecimalPoint(orderTax));
				// double x = Math.abs(orderSubTotal);
				mSubTotalItem.setText(df.format(orderSubTotal));
				mNetTotalItem.setText(df.format(orderNetTotal));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void storeProducts(String flag) {
			try {
				cursor = SOTDatabase.getCursor();
				int slno = 0;
				if (cursor != null && cursor.getCount() > 0) {
					slno = cursor.getCount();
				}
				for (int i = 0; i < originalList.size(); i++) {
					Product product = originalList.get(i);

					String dbProductCode = SOTDatabase.getProductCode(product
							.getProductCode());
					Log.d("product.getProd",
							"" + product.getProductCode());
					if (dbProductCode != null && !dbProductCode.isEmpty()) {
						HashMap<String, String> hm = new HashMap<String, String>();

						hm.put("ProductCode", product.getProductCode());
						hm.put("CQty", String.valueOf(product.getCQty()));
						hm.put("LQty", String.valueOf(product.getLQty()));
						hm.put("Qty", String.valueOf(product.getQuantity()));
						hm.put("Foc", String.valueOf(product.getFocQty()));
						hm.put("Price", String.valueOf(product.getLprice()));
						hm.put("RetailPrice",
								String.valueOf(product.getCprice()));
						hm.put("CPrice", String.valueOf(product.getCprice()));
						hm.put("Discount",
								String.valueOf(product.getDiscount()));
						hm.put("ExchangeQty",
								String.valueOf(product.getExchangeQty()));
						hm.put("Total", String.valueOf(product.getExt()));
						hm.put("SubTotal", String.valueOf(product.getSubTot()));
						hm.put("Tax", String.valueOf(product.getTaxPer()));
						hm.put("NetTotal", String.valueOf(product.getNetTot()));
						SOTDatabase.updateProductDetails(hm);

						SOTDatabase.updateProductBillDisc(
								product.getProductCode(),
								String.valueOf(product.getSubTot()));

						HashMap<String, String> queryValues = new HashMap<String, String>();
						queryValues.put("product_code",
								"" + product.getProductCode());
						queryValues.put("SRSlno", "" + slno);
						SOTDatabase.updateBatchSerialNo(queryValues);

					} else {
						if (product.getCQty() != 0 || product.getLQty() != 0
								|| product.getQuantity() != 0
								|| product.getFocQty() != 0
								|| product.getExchangeQty() != 0) {
							slno++;
							SOTDatabase.storeBillDisc(slno,
									product.getProductCode(),
									String.valueOf(product.getSubTot()));
							SOTDatabase.storeProduct(slno, product
									.getProductCode(),
									product.getProductName(),
									product.getCQty(), product.getLQty(),
									product.getQuantity(), product.getFocQty(),
									product.getLprice(), product.getDiscount(),
									product.getUom(), product.getPcs(), product
											.getExt(), Double
											.valueOf(fourDecimalPoint(product
													.getTaxPer())),
									twoDecimalPoint(product.getNetTot()),
									taxType, product.getTax(), String
											.valueOf(product.getCprice()),
									String.valueOf(product.getSubTot()), String
											.valueOf(product.getCprice()),
									String.valueOf(product.getExchangeQty()),
									String.valueOf(product
											.getMinimumSellingPrice()), "", "",
									"", "","","");

							HashMap<String, String> queryValues = new HashMap<String, String>();
							queryValues.put("product_code",
									"" + product.getProductCode());
							queryValues.put("SRSlno", "" + slno);
							SOTDatabase.updateBatchSerialNo(queryValues);

						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (flag.matches("Listing")) {
					Intent i = new Intent(InvoiceAddProduct.this,
							InvoiceHeader.class);
					startActivity(i);
					InvoiceAddProduct.this.finish();
				} else if (flag.matches("Customer")) {
					Intent i = new Intent(InvoiceAddProduct.this,
							InvoiceCustomer.class);
					startActivity(i);
					InvoiceAddProduct.this.finish();
				} else if (flag.matches("Summary")) {
					Intent i = new Intent(InvoiceAddProduct.this,
							InvoiceSummary.class);
					startActivity(i);
					InvoiceAddProduct.this.finish();
				}
			}

		}
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

	/*
	 * public void saveDialog(final String flag) {
	 * 
	 * AlertDialog.Builder alertDialog = new AlertDialog.Builder(
	 * InvoiceAddProduct.this); alertDialog.setMessage("Do you want to Save");
	 * alertDialog.setPositiveButton("YES", new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * 
	 * storeProducts(flag); }
	 * 
	 * });
	 * 
	 * alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener()
	 * {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * 
	 * dialog.dismiss(); } }); alertDialog.show(); }
	 */

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.key_one:
			productAdapter.customKeyboardBtn("1");
			break;
		case R.id.key_two:
			productAdapter.customKeyboardBtn("2");
			break;
		case R.id.key_three:
			productAdapter.customKeyboardBtn("3");
			break;
		case R.id.key_four:
			productAdapter.customKeyboardBtn("4");
			break;
		case R.id.key_five:
			productAdapter.customKeyboardBtn("5");
			break;
		case R.id.key_six:
			productAdapter.customKeyboardBtn("6");
			break;
		case R.id.key_seven:
			productAdapter.customKeyboardBtn("7");
			break;
		case R.id.key_eight:
			productAdapter.customKeyboardBtn("8");
			break;
		case R.id.key_nine:
			productAdapter.customKeyboardBtn("9");
			break;
		case R.id.key_zero:
			productAdapter.customKeyboardBtn("0");
			break;
		case R.id.key_dot:
			productAdapter.customKeyboardBtn(".");
			break;
		case R.id.key_clear:
			productAdapter.customKeyboardClearBtn();
			break;
		default:
			break;

		}

	}

	/** Batch Start **/

	private class AsyncCallSaleProductBatch extends AsyncTask<Void, Void, Void> {

		private Product sel_product;
		private View view;

		public AsyncCallSaleProductBatch(Product product, View v) {
			sel_product = product;
			view = v;
		}

		@Override
		protected void onPreExecute() {
			loadProgress();
			alBatchStock.clear();
			dialogStatus = "";
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {

				String prodcode = sel_product.getProductCode();

				if (cstgrpcd.matches("-1")) {
					cstgrpcd = "";
				}

				if (onlineMode.matches("True")) {
					if (checkOffline == true) { // temp Offline

					} else { // Online

						alBatchStock = SalesProductWebService
								.getProductBatchStockAdjustment(prodcode,
										"fncGetProductBatchStock");
					}

				} else if (onlineMode.matches("False")) {
					// Perm Offline
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			String haveBatch = sel_product.getHaveBatch();
			String haveExpiry = sel_product.getHaveExpiry();
			String code = sel_product.getProductCode();
			String name = sel_product.getProductName();
			int cartonPerQty = sel_product.getPcs();
			double price = sel_product.getLprice();
			Log.d("list code", "" + code);
			Log.d("list price", "" + price);
			Log.d("list haveBatch", "" + haveBatch);
			Log.d("list haveExpiry", "" + haveExpiry);

			haveBatchOnStockIn = SalesOrderSetGet.getHaveBatchOnStockIn();
			if (offlineLayout.getVisibility() == View.GONE) {
				if (haveBatchOnStockIn.matches("True")) {
					Log.d("haveBatchOnStockIn", haveBatchOnStockIn);
					if (haveBatch.matches("True") || haveExpiry.matches("True")) {
						Log.d("haveBatch", "haveExpiry");

						if (!alBatchStock.isEmpty()) {
							invoiceBatchDialog.initiateBatchPopupWindow(
									InvoiceAddProduct.this, haveBatch,
									haveExpiry, code, name, cartonPerQty + "",
									price + "", alBatchStock, productAdapter,
									view);
						} else {

							Toast.makeText(getApplicationContext(),
									"No Batch data", Toast.LENGTH_SHORT).show();
							productAdapter.notifyDataSetChanged();
						}

					} else {
						Log.d("no haveBatch", "no haveExpiry");
						productAdapter.notifyDataSetChanged();
					}

				} else {
					Log.d("no haveBatchOnStockIn", "no haveBatchOnStockIn");
					productAdapter.notifyDataSetChanged();
				}
			} else {
				Log.d("offline no batch", "offline no batch");
				productAdapter.notifyDataSetChanged();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);
		}
	}

	/** Batch End **/
	private void Sorting(String type, ArrayList<Product> originalList) {

		ArrayList<Product> filteredItems = new ArrayList<Product>();
		Log.d("type", "->" + type);
		String oldCategory = "", oldSubCategory = "";
		if (!type.matches("Sort By")) {
			Log.d("Sort By", "Sort By");
			Collections.sort(originalList, new Comparator<Product>() {
				@Override
				public int compare(Product lhs, Product rhs) {
					String productName1 = lhs.getProductName().toLowerCase(
							Locale.getDefault());
					String productName2 = rhs.getProductName().toLowerCase(
							Locale.getDefault());
					// alphabetical order
					return productName1.compareTo(productName2);
				}
			});
		}
		if (type.matches("SortProduct")) {
			int productDisplayOrderCount = 0, productSize = 0;
			for (int j = 0; j < originalList.size(); j++) {
				Product product = originalList.get(j);
				String prodDisplayOrder = product.getProdDisplayOrder();
				int productDisplayOrder = prodDisplayOrder.equals("") ? 0
						: Integer.valueOf(prodDisplayOrder);
				if (productDisplayOrder == 0) {
					productDisplayOrderCount++;
				}
				productSize++;
				if (productSize == originalList.size()) {

					if (productDisplayOrderCount == originalList.size()) {
						filteredItems.addAll(originalList);
					} else {
						filteredItems.addAll(originalList);
						Collections.sort(filteredItems,
								new Comparator<Product>() {
									@Override
									public int compare(Product lhs, Product rhs) {
										String prodDisplayOrder1 = lhs
												.getProdDisplayOrder();
										String prodDisplayOrder2 = rhs
												.getProdDisplayOrder();
										int prodDisplayOrd1 = 0,prodDisplayOrd2 = 0;
										try          {
											if(prodDisplayOrder1 != null && !prodDisplayOrder1.isEmpty()){
												prodDisplayOrd1 = Integer.parseInt(prodDisplayOrder1);}

											if(prodDisplayOrder2 != null && !prodDisplayOrder2.isEmpty()){
												prodDisplayOrd2 = Integer.parseInt(prodDisplayOrder2);}
										}
										catch (NumberFormatException e)          {
											prodDisplayOrd1 = 0;
											prodDisplayOrd2 = 0;
										}
										return prodDisplayOrd1 - prodDisplayOrd2;
										/*// display order
										return prodDisplayOrder1
												.compareToIgnoreCase(prodDisplayOrder2);*/
									}
								});
					}
					break;
				}
			}

			// Alphabetical SubCategory Sort
		} else if (type.matches("SortCategory")) {
			int catDisplayOrderCount = 0, categorySize = 0;
			for (int j = 0; j < originalList.size(); j++) {
				Product product = originalList.get(j);
				String catDisplayOrder = product.getCatDisplayOrder();
				int categoryDisplayOrder = catDisplayOrder.equals("") ? 0
						: Integer.valueOf(catDisplayOrder);
				if (categoryDisplayOrder == 0) {
					catDisplayOrderCount++;
				}
				categorySize++;
				if (categorySize == originalList.size()) {

					filteredItems.addAll(originalList);
					Collections.sort(filteredItems, new Comparator<Product>() {
						@Override
						public int compare(Product lhs, Product rhs) {
							String categoryName1 = lhs.getCategoryCode();
							String categoryName2 = rhs.getCategoryCode();
							// alphabetical order
							return categoryName1
									.compareToIgnoreCase(categoryName2);
						}
					});
					if (catDisplayOrderCount == originalList.size()) {

					} else {
						// filteredItems.addAll(bubblesort(originalList));
						// filteredItems.addAll(filteredItems);
						Collections.sort(filteredItems,
								new Comparator<Product>() {
									@Override
									public int compare(Product lhs, Product rhs) {
										String categoryDisplayOrder1 = lhs
												.getCatDisplayOrder();
										String categoryDisplayOrder2 = rhs
												.getCatDisplayOrder();
										int categoryDisplayOrd1 = 0,categoryDisplayOrd2 = 0;
										try          {
											if(categoryDisplayOrder1 != null && !categoryDisplayOrder1.isEmpty()){
												categoryDisplayOrd1 = Integer.parseInt(categoryDisplayOrder1);}

											if(categoryDisplayOrder2 != null && !categoryDisplayOrder2.isEmpty()){
												categoryDisplayOrd2 = Integer.parseInt(categoryDisplayOrder2);}
										}
										catch (NumberFormatException e)          {
											categoryDisplayOrd1 = 0;
											categoryDisplayOrd2 = 0;
										}
										// display order
										return categoryDisplayOrd1 - categoryDisplayOrd2;
									}
								});
					}

					break;
				}
			}

			// Alphabetical SubCategory Sort
		} else if (type.matches("SortSubCategory")) {
			int subcatDisplayOrderCount = 0, subcategorySize = 0;
			for (int j = 0; j < originalList.size(); j++) {
				Product product = originalList.get(j);
				String subcatDisplayOrder = product.getSubCatDisplayOrder();
				int subcategoryDisplayOrder = subcatDisplayOrder.equals("") ? 0
						: Integer.valueOf(subcatDisplayOrder);
				if (subcategoryDisplayOrder == 0) {
					subcatDisplayOrderCount++;
				}
				subcategorySize++;
				if (subcategorySize == originalList.size()) {

					filteredItems.addAll(originalList);
					Collections.sort(filteredItems, new Comparator<Product>() {
						@Override
						public int compare(Product lhs, Product rhs) {
							String subCategoryName1 = lhs.getSubCategoryCode();
							String subCategoryName2 = rhs.getSubCategoryCode();
							// alphabetical order
							return subCategoryName1
									.compareToIgnoreCase(subCategoryName2);
						}
					});

					if (subcatDisplayOrderCount == originalList.size()) {

					} else {
						// filteredItems.addAll(filteredItems);
						Collections.sort(filteredItems,
								new Comparator<Product>() {
									@Override
									public int compare(Product lhs, Product rhs) {
										String subcatDisplayOrder1 = lhs
												.getSubCatDisplayOrder();
										String subcatDisplayOrder2 = rhs
												.getSubCatDisplayOrder();
										int subcategoryDisplayOrd1 = 0,subcategoryDisplayOrd2 = 0;
										try          {
											if(subcatDisplayOrder1 != null && !subcatDisplayOrder1.isEmpty()){
												subcategoryDisplayOrd1 = Integer.parseInt(subcatDisplayOrder1);}

											if(subcatDisplayOrder2 != null && !subcatDisplayOrder2.isEmpty()){
												subcategoryDisplayOrd2 = Integer.parseInt(subcatDisplayOrder2);}
										}
										catch (NumberFormatException e)          {
											subcategoryDisplayOrd1 = 0;
											subcategoryDisplayOrd2 = 0;
										}
										// display order
										return subcategoryDisplayOrd1 - subcategoryDisplayOrd2;
									}
								});
					}
					break;
				}
			}

		} else {
			filteredItems = originalList;
		}

		for (Product product : filteredItems) {

			if (type.matches("SortCategory")) {

				String newCategory = product.getCategoryCode();

				if (oldCategory.matches(newCategory)) {
					product.setNewCategoryCode("");
				} else {
					Log.d("oldCategory", "->" + oldCategory);
					if (oldCategory != null && !oldCategory.isEmpty()) {
						product.setNewCategoryCode(newCategory);
						oldCategory = newCategory;
						Log.d("oldCategory", "->not null" + oldCategory);
					} else {
						product.setNewCategoryCode("");
						oldCategory = newCategory;
						Log.d("oldCategory", "-> null" + oldCategory);
					}
				}

			} else if (type.matches("SortSubCategory")) {

				String newSubCategory = product.getSubCategoryCode();
				if (oldSubCategory.matches(newSubCategory)) {
					product.setNewSubCategoryCode("");
				} else {
					Log.d("oldCategory", "->" + oldCategory);
					if (oldSubCategory != null && !oldSubCategory.isEmpty()) {
						product.setNewSubCategoryCode(newSubCategory);
						oldSubCategory = newSubCategory;
						Log.d("oldCategory", "->not null" + oldSubCategory);
					} else {
						product.setNewSubCategoryCode("");
						oldSubCategory = newSubCategory;
						Log.d("oldCategory", "-> null" + oldSubCategory);
					}
				}

			} else {
				product.setNewCategoryCode("");
				product.setNewSubCategoryCode("");
			}
		}

		productAdapter = new ProductListAdapter(InvoiceAddProduct.this,
				R.layout.product_listitem, filteredItems);
		// Assign adapter to ListView
		mListView.setAdapter(productAdapter);

	}

	public void productNameAlert() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText edittext = new EditText(this);
//		alert.setMessage("Enter Your Message");
		alert.setTitle("Product Name");

//		int left = 5;
//		int top = 20;
//		int right = 5;
//		int bottom = 5;
//
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		params.setMargins(left, top, right, bottom);

		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

		edittext.setText(sl_namefield.getText().toString());
		edittext.setSelection(edittext.length());

//		edittext.setLayoutParams(params);

		alert.setView(edittext);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//What ever you want to do with the value
//				Editable YouEditTextValue = edittext.getText();
				//OR
				String productName = edittext.getText().toString();

				sl_namefield.setText(productName);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// what ever you want to do with No option.
			}
		});

		alert.show();
	}

	public void loadProgress() {
		spinnerLayout = new LinearLayout(InvoiceAddProduct.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(salesproduct_layout, false);
		progressBar = new ProgressBar(InvoiceAddProduct.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));

		spinnerLayout.addView(progressBar);
	}
}