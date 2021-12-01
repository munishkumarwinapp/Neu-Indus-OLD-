package com.winapp.sot;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.adapter.Attribute;
import com.winapp.adapter.ProductSearchAdapter;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterProd;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.LoginActivity;
import com.winapp.fwms.NewProductWebService;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.XMLParser;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.printer.UIHelper;
import com.winapp.sot.FilterCS.OnFilterCompletionListener;
import com.winapp.sotdetails.CustomerListActivity;
import com.winapp.util.ErrorLog;
import com.winapp.util.XMLAccessTask;

public class SalesAddProduct extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace {
	
	private static String webMethName = "fncGetProduct";
	private static String webMethNamebar = "fncGetProductBarCode";
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String PRODUCT_NAME = "ProductName";
	private static final String PRODUCTNAME_BARCODE = "ProductCode";
	private static final String PRODUCT_BARCODE = "Barcode";
	private String mBarcodeJsonString="";
	private JSONObject mBarcodeJsonObject=null;
	private JSONArray mBarcodeJsonArray=null;
	private HashMap<String, String> mHashMap;
	SlidingMenu menu;
	TextView textView1,invoice_retail;
	EditText editText1;
	ArrayList<HashMap<String, String>> searchProductArr = new ArrayList<HashMap<String, String>>();
	Button sl_addProduct, sl_summary;
	EditText sl_searchfield, sl_codefield, sl_namefield, sl_cartonQty, sl_looseQty, sl_qty,
			sl_foc, sl_price, sl_itemDiscount, sl_uom, sl_cartonPerQty,
			sl_total, sl_tax, sl_netTotal, sl_cprice, sl_exchange,sl_total_inclusive,sl_retail;
	AlertDialog myalertDialog = null;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	EditText editText, sl_stock;
	CustomAlertAdapterProd arrayAdapterProd;
	ArrayList<HashMap<String, String>> searchResults;
	int textlength = 0;
	ArrayList<HashMap<String, String>> getArraylist = new ArrayList<HashMap<String, String>>();
	ProgressBar progressBar;
	LinearLayout spinnerLayout, customer_layout, sales_carton_header,
			pcs_txt_layout, pcs_layout;
	LinearLayout salesproduct_layout, foc_layout, foc_header_layout,
			price_header_layout, grid_layout, carton_layout;
	String valid_url, productTxt, productresult, barcoderesult, barcodeTxt;
	HashMap<String, String> hmsplitbc = new HashMap<String, String>();
	String slPrice = "", slUomCode = "", slCartonPerQty = "",
			MinimumSellingPrice = "",MinimumCartonSellingPrice="";
	ArrayList<String> getSalesProdArr = new ArrayList<String>();
	String keyValues = "", values = "";
	static ArrayList<String> companyArr = new ArrayList<String>();
	String taxType = "", taxValue = "", sales_prodCode,haveBatch="",haveExpiry="";
	Cursor cursor;
	String beforeLooseQty, beforeFoc;
	ListView productFilterList;
	String catStr = "", subCatStr = "", cstcode = "", cstgrpcd = "",
			newPrice = "", cprice = "", newcprice = "", priceflag = "",
			calCarton = "", ss_Cqty = "", comapanyCode = "",haveMultipleCustomerPrice="";;
	int sl_no = 1;
	ArrayList<String> productcode = new ArrayList<String>();
	boolean prdcode = false;
	double itmDisc = 0;
	double netTtal = 0, taxAmount = 0;
	String str_ssupdate, str_sscancel, str_sssno;
	double tt;
	ArrayList<String> priceArr = new ArrayList<String>();
	ImageView expand;
	TextView price_txt,txt_cprice,txt_price;
	TextView listing_screen, customer_screen, addProduct_screen,
			summary_screen;
	TextWatcher cqtyTW, lqtyTW, qtyTW;
	String intentString = "";
	// FilterSearch filterSearch;
	private FilterCS filtercs;
	HashMap<String, String> producthashValue = new HashMap<String, String>();
	String product_stock_jsonString = null;
	JSONObject product_stock_jsonResponse;
	JSONArray product_stock_jsonMainNode;
	String cmpnyCode = "", LocationCode = "", stock,Weight="",mobileHaveOfflineMode="";
	SimpleAdapter adapter;

	// Offline
	LinearLayout offlineLayout,retailPrice_layout;
	private OfflineDatabase offlineDatabase;
	boolean checkOffline;
	private String onlineMode, offlineDialogStatus,haveAttribute="";
	private OfflineCommon offlineCommon;
	OfflineSettingsManager spManager;
	private ArrayList<HashMap<String, String>> customerGroupCodeArr = new ArrayList<HashMap<String, String>>();
	ImageButton productTag,filter;
	private ProductSearchAdapter mProductSearchAdapter;
	private ArrayList<Attribute> mAttributeArr;
	ArrayList<HashMap<String, String>> colorArr = new ArrayList<HashMap<String, String>>();
	ColorAttributeDialog productModifierDialog;
	private double screenInches;
	static final String ProductImg = "ProductImage";
	private ErrorLog errorLog;
	private Bitmap bitmap;
	private UIHelper helper;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.sales_addproduct);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.addproduct_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		ImageButton pricetag = (ImageButton) customNav
				.findViewById(R.id.priceTag);
		filter = (ImageButton) customNav.findViewById(R.id.filter);

		productTag = (ImageButton) customNav
				  .findViewById(R.id.product);

		txt.setText("Add Product");
		errorLog = new ErrorLog();
		helper = new UIHelper(SalesAddProduct.this);

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
		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		haveMultipleCustomerPrice = SalesOrderSetGet.getHaveMultipleCustomerPrice();
		
		if(haveMultipleCustomerPrice!=null && !haveMultipleCustomerPrice.isEmpty()){
			
		}else{
			haveMultipleCustomerPrice="";
		}

		haveAttribute = SalesOrderSetGet.getHaveAttribute();

		if(haveAttribute!=null && !haveAttribute.isEmpty()){

		}else{
			haveAttribute="";
		}

		screenInches = displayMetrics();
		// @Offline
		onlineMode = OfflineDatabase.getOnlineMode();
		offlineDatabase = new OfflineDatabase(SalesAddProduct.this);
		offlineCommon = new OfflineCommon(SalesAddProduct.this);
		checkOffline = OfflineCommon.isConnected(SalesAddProduct.this);
		OfflineDatabase.init(SalesAddProduct.this);
		new OfflineSalesOrderWebService(SalesAddProduct.this);
		
		offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);
		spManager = new OfflineSettingsManager(SalesAddProduct.this);
		comapanyCode = spManager.getCompanyType();

		// filterSearch = new FilterSearch(this);
		filtercs = new FilterCS(SalesAddProduct.this);

		salesproduct_layout = (LinearLayout) findViewById(R.id.salesproduct_layout);
		foc_layout = (LinearLayout) findViewById(R.id.foc_layout);

		pcs_txt_layout = (LinearLayout) findViewById(R.id.pcs_txt_layout);
		pcs_layout = (LinearLayout) findViewById(R.id.pcs_layout);

		sl_summary = (Button) findViewById(R.id.sl_summary);
		sl_addProduct = (Button) findViewById(R.id.sl_addProduct);

		foc_header_layout = (LinearLayout) findViewById(R.id.sales_foc_header_layout);
		price_header_layout = (LinearLayout) findViewById(R.id.sales_price_header_layout);
		grid_layout = (LinearLayout) findViewById(R.id.sales_grid_layout);
		carton_layout = (LinearLayout) findViewById(R.id.sales_carton_layout);
		 sl_searchfield = (EditText) findViewById(R.id.sl_searchfield);
		sl_codefield = (EditText) findViewById(R.id.sl_codefield);
		sl_namefield = (EditText) findViewById(R.id.sl_namefield);
		sl_cartonQty = (EditText) findViewById(R.id.sl_cartonQty);
		sl_looseQty = (EditText) findViewById(R.id.sl_looseQty);
		sl_qty = (EditText) findViewById(R.id.sl_qty);
		sl_foc = (EditText) findViewById(R.id.sl_foc);
		sl_price = (EditText) findViewById(R.id.sl_price);
		txt_cprice= (TextView) findViewById(R.id.txt_cprice);
		sl_itemDiscount = (EditText) findViewById(R.id.sl_itemDiscount);
		sl_uom = (EditText) findViewById(R.id.sl_uom);
		sl_cartonPerQty = (EditText) findViewById(R.id.sl_cartonPerQty);
		sl_total_inclusive = (EditText) findViewById(R.id.sl_total_inclusive);
		sl_total = (EditText) findViewById(R.id.sl_total);
		sl_tax = (EditText) findViewById(R.id.sl_tax);
		sl_netTotal = (EditText) findViewById(R.id.sl_netTotal);

		sl_cprice = (EditText) findViewById(R.id.sl_cprice);
		sl_exchange = (EditText) findViewById(R.id.sl_exchange);
		price_txt = (TextView) findViewById(R.id.sales_pricetxt);
		expand = (ImageView) findViewById(R.id.expand);

		sl_stock = (EditText) findViewById(R.id.sl_stock);

		txt_price= (TextView) findViewById(R.id.txt_price);

		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen = (TextView) findViewById(R.id.sum_screen);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);

		productFilterList = (ListView) findViewById(R.id.productFilterList);
		sales_carton_header = (LinearLayout) findViewById(R.id.sales_carton_header);
		retailPrice_layout = (LinearLayout)findViewById(R.id.retailPrice_layout);
		invoice_retail = (TextView)findViewById(R.id.invoice_retail);
		sl_retail = (EditText)findViewById(R.id.sl_retail);
		customer_layout.setVisibility(View.GONE);
		// addProduct_screen.setBackgroundColor(Color.parseColor("#00AFF0"));

		addProduct_screen.setTextColor(Color.parseColor("#FFFFFF"));
		addProduct_screen.setBackgroundResource(drawable.tab_select);
		mAttributeArr = new ArrayList<>();

		customerGroupCodeArr.clear();
		productModifierDialog = new ColorAttributeDialog();
		// offline
		if (onlineMode.matches("True")) {
			offlineLayout.setVisibility(View.GONE);
		} else if (onlineMode.matches("False")) {
			offlineLayout.setVisibility(View.VISIBLE);
		}

		valid_url = FWMSSettingsDatabase.getUrl();
		new SalesProductWebService(valid_url);
		new NewProductWebService(valid_url);
		new SalesOrderWebService(valid_url);
		sl_itemDiscount.setRawInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);

		priceflag = SalesOrderSetGet.getCartonpriceflag();
//		priceflag = "0";
		calCarton = LogOutSetGet.getCalcCarton();

		if (priceflag.matches("null") || priceflag.matches("")) {
			priceflag = "0";
		}

		if (priceflag.matches("1")) {
			sl_price.setText("");
			sl_price.setVisibility(View.INVISIBLE);
			txt_price.setVisibility(View.GONE);

		} else {
			sl_price.setVisibility(View.VISIBLE);
			txt_price.setVisibility(View.VISIBLE);
		}

		// priceflag="0";
//		calCarton="1";
		  /** Amount will Gone or Visible Based on Hide Price From GetUserPermission ***/
		  if(FormSetterGetter.getHidePrice()!=null && !FormSetterGetter.getHidePrice().isEmpty()){ 
		   if(FormSetterGetter.getHidePrice().matches("Hide Price")){  
		     ((LinearLayout) findViewById(R.id.total_txt_layout)).setVisibility(View.GONE);
		       ((LinearLayout) findViewById(R.id.total_edt_layout)).setVisibility(View.GONE);
		       sl_price.setVisibility(View.INVISIBLE);
		       expand.setVisibility(View.INVISIBLE);
		       price_txt.setVisibility(View.INVISIBLE);       
		       sl_cprice.setVisibility(View.GONE);
		       price_header_layout.setVisibility(View.GONE);
		   }else{
		    ((LinearLayout) findViewById(R.id.total_txt_layout)).setVisibility(View.VISIBLE);
		       ((LinearLayout) findViewById(R.id.total_edt_layout)).setVisibility(View.VISIBLE);
		       sl_price.setVisibility(View.VISIBLE);
		       expand.setVisibility(View.VISIBLE);
		     if (priceflag.matches("1")) {
		      sl_cprice.setVisibility(View.VISIBLE);
		      price_txt.setVisibility(View.GONE);
		      price_header_layout.setVisibility(View.VISIBLE);
		     } else {
		      sl_cprice.setVisibility(View.GONE);
		      price_txt.setVisibility(View.VISIBLE);
		      price_header_layout.setVisibility(View.GONE);
		     }
		   }
		  }else{
		   ((LinearLayout) findViewById(R.id.total_txt_layout)).setVisibility(View.VISIBLE);
		      ((LinearLayout) findViewById(R.id.total_edt_layout)).setVisibility(View.VISIBLE);
		      sl_price.setVisibility(View.VISIBLE);
		      expand.setVisibility(View.VISIBLE);
		    if (priceflag.matches("1")) {
		     sl_cprice.setVisibility(View.VISIBLE);
		     price_txt.setVisibility(View.GONE);
		     price_header_layout.setVisibility(View.VISIBLE);
		    } else {
		     sl_cprice.setVisibility(View.GONE);
		     price_txt.setVisibility(View.VISIBLE);
		     price_header_layout.setVisibility(View.GONE);
		    }
		  }
		SOTDatabase.init(SalesAddProduct.this);

//		screenInches = displayMetrics();
//		screenInches = 8;
		Log.d("Display Inche", "" + screenInches);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			intentString = extras.getString("SalesOrder");

			sales_prodCode = extras.getString("SOT_ssproductcode");

			sl_codefield.setText(extras.getString("SOT_ssproductcode"));
			sl_namefield.setText(extras.getString("SOT_str_ssprodname"));
			sl_cartonQty.setText(extras.getString("SOT_str_sscq"));
			sl_looseQty.setText(extras.getString("SOT_str_sslq"));
			sl_qty.setText(extras.getString("SOT_str_ssqty"));
			sl_foc.setText(extras.getString("SOT_str_ssfoc"));
			sl_price.setText(extras.getString("SOT_str_ssprice"));
			sl_itemDiscount.setText(extras.getString("SOT_str_ssdisc"));
			sl_uom.setText(extras.getString("SOT_str_ssuom"));
			sl_cartonPerQty.setText(extras.getString("SOT_st_sscpqty"));
			sl_total.setText(extras.getString("SOT_st_sstotal"));
			//sl_tax.setText(extras.getString("SOT_st_sstax"));
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
			sl_cprice.setText(extras.getString("SOT_str_sscprice"));
			sl_exchange.setText(extras.getString("SOT_str_ssexchqty"));

			//taxType = extras.getString("SOT_str_sstaxtype");
			taxValue = extras.getString("SOT_st_sstaxvalue");
			slCartonPerQty = extras.getString("SOT_st_sscpqty");

			MinimumSellingPrice = extras.getString("SOT_str_minselling_price");

			Log.d("MinimumSellingPrice", "MinimumSellingPrice"
					+ MinimumSellingPrice);
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

				sales_carton_header.setVisibility(View.GONE);

			} else {

				sales_carton_header.setVisibility(View.VISIBLE);

				if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
						|| slCartonPerQty.matches("")) {

					sl_cartonQty.setFocusable(false);
					sl_cartonQty.setBackgroundResource(drawable.labelbg);

					sl_looseQty.setFocusable(false);
					sl_looseQty.setBackgroundResource(drawable.labelbg);

				} else {
					sl_cartonQty.setFocusableInTouchMode(true);
					sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

					sl_looseQty.setFocusableInTouchMode(true);
					sl_looseQty.setBackgroundResource(drawable.edittext_bg);
				}
			}
		}

		getSalesProdArr.clear();
		companyArr.clear();
		searchProductArr.clear();
		mHashMap = new HashMap<String, String>();
		Log.d("SOTaxT", SalesOrderSetGet.getCompanytax());
		taxType = SalesOrderSetGet.getCompanytax();

		cstcode = SalesOrderSetGet.getSuppliercode();
		cstgrpcd = SalesOrderSetGet.getSuppliergroupcode();
		cmpnyCode = SupplierSetterGetter.getCompanyCode();
		
//		AsyncCallWSADDPRD proTask = new AsyncCallWSADDPRD();
//		proTask.execute();

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
		 new GetProduct().execute();

		if((FormSetterGetter.getHidePrice()!=null && !FormSetterGetter.getHidePrice().isEmpty()) &&
				FormSetterGetter.getHidePrice().matches("Hide Price")){
			Log.d("getHidePrice","Hide Price");
		}else{
			Log.d("ShowPrice","Show Price");

			if (FormSetterGetter.isEditPrice()) {
				pricetag.setVisibility(View.GONE);
				sl_price.setEnabled(true);
				sl_price.setBackgroundResource(drawable.edittext_bg);
				sl_price.setFocusableInTouchMode(true);

				sl_cprice.setEnabled(true);
				sl_cprice.setBackgroundResource(drawable.edittext_bg);
				sl_cprice.setFocusableInTouchMode(true);
			} else {
				pricetag.setVisibility(View.VISIBLE);
				// sl_price.setKeyListener(null);
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
		}

		sl_namefield.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				productNameAlert();
			}
		});

		 productTag.setOnClickListener(new OnClickListener() {

			   @Override
			   public void onClick(View arg0) {
			    
//			    haveMultipleCustomerPrice = SalesOrderSetGet.getHaveMultipleCustomerPrice();
			    
			    if(haveMultipleCustomerPrice.matches("1")){
			     alertDialogCustomergroup(customerGroupCodeArr);
			    }else{
			     
			    }
			   }
			  });
		 sl_searchfield.addTextChangedListener(new TextWatcher()
         {
             @Override
             public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3)
             {
             mProductSearchAdapter.filter(cs.toString());
              //adapter.getFilter().filter(cs);
             }
             @Override
             public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
             {
                 // TODO Auto-generated method stub
             }

             @Override
             public void afterTextChanged(Editable arg0)
             {
                 // TODO Auto-generated method stub
             }
         });
		filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				filtercs.filterDialog();

				filtercs.OnFilterCompletionListener(new OnFilterCompletionListener() {

					@Override
					public void onFilterCompleted(String category,
							String subcategory) {

						catStr = category;
						subCatStr = subcategory;

						Log.d("catStr", "----->" + catStr);
						Log.d("subCatStr", "-->" + subCatStr);

						 new AsyncCallWSSearchProduct().execute();
					}
				});
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

					Intent i = new Intent(SalesAddProduct.this,
							SalesOrderHeader.class);
					startActivity(i);
					SalesAddProduct.this.finish();

				}

			}
		});

		customer_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(!sl_qty.getText().toString().matches("")){
					dialog();
				}else{
					Intent i = new Intent(SalesAddProduct.this,
							SalesOrderCustomer.class);
					startActivity(i);
					SalesAddProduct.this.finish();
				}



			}
		});

		summary_screen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!sl_qty.getText().toString().matches("")){
					dialog();
				}else {
					Intent i = new Intent(SalesAddProduct.this, SalesSummary.class);
					startActivity(i);
					SalesAddProduct.this.finish();
				}
			}
		});

		pricetag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(priceflag.matches("1")){
					new SOTAdmin(SalesAddProduct.this, sl_price,sl_cprice,
							salesproduct_layout);
				}else{
					new SOTAdmin(SalesAddProduct.this, sl_price,
							salesproduct_layout);
				}


			}
		});

		productFilterList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {

				sales_prodCode = searchProductArr.get(position).get("ProductCode");
				Log.d("sales_prodCode", sales_prodCode);
				sl_codefield.setText(sales_prodCode);
//				new CheckProductBarcode().execute();
				loadprogress();
				new AsyncCallSaleProduct("true").execute();
			}
		});

		sl_addProduct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.d("MinimumSellingPrice", MinimumSellingPrice);

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

					double slprice = 0, minSellingPrice = 0,slcprice=0;
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

					Log.d("slcprice", "cp "+slcprice);

					if (priceflag.matches("1")) {
						if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
								|| slCartonPerQty.matches("")) {
							if (minSellingPrice > slcprice) {
								Toast.makeText(
										SalesAddProduct.this,
										"Price must be greater than minimum selling price $ "
												+ MinimumSellingPrice + " pp" + slcprice,
										Toast.LENGTH_LONG).show();
							} else {
								if(qty>0){
									if (pcs == 1){
										if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
											if(cprices !=0){
												storeInDatabase();
												Intent i = new Intent(SalesAddProduct.this,
														SalesSummary.class);
												startActivity(i);
												SalesAddProduct.this.finish();
												Toast.makeText(SalesAddProduct.this, "Updated ",
														Toast.LENGTH_LONG).show();
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter Carton Price",Toast.LENGTH_SHORT).show();
											}
										}else{
											if( prices !=0){
												storeInDatabase();
												Intent i = new Intent(SalesAddProduct.this,
														SalesSummary.class);
												startActivity(i);
												SalesAddProduct.this.finish();
												Toast.makeText(SalesAddProduct.this, "Updated ",
														Toast.LENGTH_LONG).show();
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter Price",Toast.LENGTH_SHORT).show();
											}
										}
									}else if (pcs > 1){
										Log.d("column_price2",cprice+","+pcs+"price:"+price);
										if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
											if(cprices !=0 && prices !=0){
												storeInDatabase();
												Intent i = new Intent(SalesAddProduct.this,
														SalesSummary.class);
												startActivity(i);
												SalesAddProduct.this.finish();
												Toast.makeText(SalesAddProduct.this, "Updated ",
														Toast.LENGTH_LONG).show();
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
											}
										}else{
											if( prices !=0){
												storeInDatabase();
												Intent i = new Intent(SalesAddProduct.this,
														SalesSummary.class);
												startActivity(i);
												SalesAddProduct.this.finish();
												Toast.makeText(SalesAddProduct.this, "Updated ",
														Toast.LENGTH_LONG).show();
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter  Price",Toast.LENGTH_SHORT).show();
											}
										}
									}
								}else if(qty ==0){
									if(foc !=0 || exch!=0){
										storeInDatabase();
										Intent i = new Intent(SalesAddProduct.this,
												SalesSummary.class);
										startActivity(i);
										SalesAddProduct.this.finish();
										Toast.makeText(SalesAddProduct.this, "Updated ",
												Toast.LENGTH_LONG).show();
									}else{
										Toast.makeText(getApplicationContext(),"Please Enter Quantity",Toast.LENGTH_SHORT).show();
									}
								}
							}
						}else{
							if(qty>0){
								if (pcs == 1){
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0){
											storeInDatabase();
											Intent i = new Intent(SalesAddProduct.this,
													SalesSummary.class);
											startActivity(i);
											SalesAddProduct.this.finish();
											Toast.makeText(SalesAddProduct.this, "Updated ",
													Toast.LENGTH_LONG).show();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if( prices !=0){
											storeInDatabase();
											Intent i = new Intent(SalesAddProduct.this,
													SalesSummary.class);
											startActivity(i);
											SalesAddProduct.this.finish();
											Toast.makeText(SalesAddProduct.this, "Updated ",
													Toast.LENGTH_LONG).show();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Price",Toast.LENGTH_SHORT).show();
										}
									}
								}else if (pcs > 1){
									Log.d("column_price2",cprice+","+pcs+"price:"+price);
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0 && prices !=0){
											storeInDatabase();
											Intent i = new Intent(SalesAddProduct.this,
													SalesSummary.class);
											startActivity(i);
											SalesAddProduct.this.finish();
											Toast.makeText(SalesAddProduct.this, "Updated ",
													Toast.LENGTH_LONG).show();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if( prices !=0){
											storeInDatabase();
											Intent i = new Intent(SalesAddProduct.this,
													SalesSummary.class);
											startActivity(i);
											SalesAddProduct.this.finish();
											Toast.makeText(SalesAddProduct.this, "Updated ",
													Toast.LENGTH_LONG).show();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter  Price",Toast.LENGTH_SHORT).show();
										}
									}
								}
							}else if(qty ==0){
								if(foc !=0 || exch!=0){
									storeInDatabase();
									Intent i = new Intent(SalesAddProduct.this,
											SalesSummary.class);
									startActivity(i);
									SalesAddProduct.this.finish();
									Toast.makeText(SalesAddProduct.this, "Updated ",
											Toast.LENGTH_LONG).show();
								}else{
									Toast.makeText(getApplicationContext(),"Please Enter Quantity",Toast.LENGTH_SHORT).show();
								}
							}
						}

					} else {
						if (minSellingPrice > slprice) {
							Toast.makeText(
									SalesAddProduct.this,
									"Price must be greater than minimum selling price $ "
											+ MinimumSellingPrice,
									Toast.LENGTH_LONG).show();
						} else {
							if(qty>0){
								if (pcs == 1){
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0){
											storeInDatabase();
											Intent i = new Intent(SalesAddProduct.this,
													SalesSummary.class);
											startActivity(i);
											SalesAddProduct.this.finish();
											Toast.makeText(SalesAddProduct.this, "Updated ",
													Toast.LENGTH_LONG).show();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if( prices !=0){
											storeInDatabase();
											Intent i = new Intent(SalesAddProduct.this,
													SalesSummary.class);
											startActivity(i);
											SalesAddProduct.this.finish();
											Toast.makeText(SalesAddProduct.this, "Updated ",
													Toast.LENGTH_LONG).show();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Price",Toast.LENGTH_SHORT).show();
										}
									}
								}else if (pcs > 1){
									Log.d("column_price2",cprice+","+pcs+"price:"+price);
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0 && prices !=0){
											storeInDatabase();
											Intent i = new Intent(SalesAddProduct.this,
													SalesSummary.class);
											startActivity(i);
											SalesAddProduct.this.finish();
											Toast.makeText(SalesAddProduct.this, "Updated ",
													Toast.LENGTH_LONG).show();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price & Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if( prices !=0){
											storeInDatabase();
											Intent i = new Intent(SalesAddProduct.this,
													SalesSummary.class);
											startActivity(i);
											SalesAddProduct.this.finish();
											Toast.makeText(SalesAddProduct.this, "Updated ",
													Toast.LENGTH_LONG).show();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter  Price",Toast.LENGTH_SHORT).show();
										}
									}
								}
							}else if(qty ==0){
								if(foc !=0 || exch!=0){
									storeInDatabase();
									Intent i = new Intent(SalesAddProduct.this,
											SalesSummary.class);
									startActivity(i);
									SalesAddProduct.this.finish();
									Toast.makeText(SalesAddProduct.this, "Updated ",
											Toast.LENGTH_LONG).show();
								}else{
									Toast.makeText(getApplicationContext(),"Please Enter Quantity",Toast.LENGTH_SHORT).show();
								}
							}
						}
					}


				} else {
					double slprice = 0,piecePerCarton = 0, minSellingPrice = 0,miniCartonSellingPrice=0,slcprice=0;
					String price = sl_price.getText().toString();
					String cprice = sl_cprice.getText().toString();

					Log.d("cprice&price","-->"+cprice+"--"+price);

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
					if (slCartonPerQty!=null && !slCartonPerQty.isEmpty() ) {
						piecePerCarton = Double.parseDouble(slCartonPerQty);
					}
					if (!MinimumCartonSellingPrice.matches("")) {
						miniCartonSellingPrice = Double
								.parseDouble(MinimumCartonSellingPrice);
					}
					if (priceflag.matches("1")) {
						/*if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
								|| slCartonPerQty.matches("")) {
							if (minSellingPrice > slcprice) {
								Toast.makeText(
										SalesAddProduct.this,
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
									SalesAddProduct.this,
									"Carton Price must be greater than Minimum carton selling price $ "
											+ MinimumCartonSellingPrice,
									Toast.LENGTH_LONG).show();
						} else if (piecePerCarton>1){
							if(minSellingPrice > slprice) {
								sl_price.requestFocus();
								Toast.makeText(
										SalesAddProduct.this,
										"Price must be greater than minimum selling price $ "
												+ MinimumSellingPrice,
										Toast.LENGTH_LONG).show();
							}else{
								if(qty>0){
									if (pcs == 1){
										if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
											if(cprices !=0){
												storeInDatabase();
											}else{
												Toast.makeText(getApplicationContext(),"Please Enter Carton Price",Toast.LENGTH_SHORT).show();
											}
										}else{
											if( prices !=0){
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
											if( prices !=0){
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
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if( prices !=0){
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
										if( prices !=0){
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

					} else {
						if (minSellingPrice > slprice) {
							Toast.makeText(
									SalesAddProduct.this,
									"Price must be greater than minimum selling price $ "
											+ MinimumSellingPrice,
									Toast.LENGTH_LONG).show();
						} else {
							if(qty>0){
								if (pcs == 1){
									if(SalesOrderSetGet.getCartonpriceflag().matches("1")){
										if(cprices !=0){
											storeInDatabase();
										}else{
											Toast.makeText(getApplicationContext(),"Please Enter Carton Price",Toast.LENGTH_SHORT).show();
										}
									}else{
										if( prices !=0){
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
										if( prices !=0){
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
				}
				mAttributeArr.clear();
			}
		});

//		sl_addProduct.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				Log.d("MinimumSellingPrice", MinimumSellingPrice);
//
//				if ((str_ssupdate != null) || (str_sscancel != null)) {
//					Toast.makeText(SalesAddProduct.this, "Updated ",
//							Toast.LENGTH_LONG).show();
//					double slprice = 0, minSellingPrice = 0,slcprice=0;
//					String price = sl_price.getText().toString();
//					String cprice = sl_cprice.getText().toString();
//					cursor = SOTDatabase.getCursor();
//					if (!price.matches("")) {
//						slprice = Double.parseDouble(price);
//					}
//					if (!MinimumSellingPrice.matches("")) {
//						minSellingPrice = Double
//								.parseDouble(MinimumSellingPrice);
//					}
//
//					if (!cprice.matches("")) {
//						slcprice = Double.parseDouble(cprice);
//					}
//
//					Log.d("slcprice", "cp "+slcprice);
//
//					if (priceflag.matches("1")) {
//						if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
//								|| slCartonPerQty.matches("")) {
//							if (minSellingPrice > slcprice) {
//								Toast.makeText(
//										SalesAddProduct.this,
//										"Price must be greater than minimum selling price $ "
//												+ MinimumSellingPrice + " pp" + slcprice,
//										Toast.LENGTH_LONG).show();
//							} else {
//								storeInDatabase();
//
//
//							}
//						}else{
//									storeInDatabase();
//						}
//
//					} else {
//						if (minSellingPrice > slprice) {
//							Toast.makeText(
//									SalesAddProduct.this,
//									"Price must be greater than minimum selling price $ "
//											+ MinimumSellingPrice,
//									Toast.LENGTH_LONG).show();
//						} else {
//
//							storeInDatabase();
//						}
//					}
//
//					Intent i = new Intent(SalesAddProduct.this,
//							SalesSummary.class);
//					startActivity(i);
//					SalesAddProduct.this.finish();
//				} else {
//					double slprice = 0,piecePerCarton = 0, minSellingPrice = 0,miniCartonSellingPrice=0,slcprice=0;
//					String price = sl_price.getText().toString();
//					String cprice = sl_cprice.getText().toString();
//					cursor = SOTDatabase.getCursor();
//					if (!price.matches("")) {
//						slprice = Double.parseDouble(price);
//					}
//					if (!MinimumSellingPrice.matches("")) {
//						minSellingPrice = Double
//								.parseDouble(MinimumSellingPrice);
//					}
//					if (!cprice.matches("")) {
//						slcprice = Double.parseDouble(cprice);
//					}
//					if (slCartonPerQty!=null && !slCartonPerQty.isEmpty() ) {
//						piecePerCarton = Double.parseDouble(slCartonPerQty);
//					}
//					if (!MinimumCartonSellingPrice.matches("")) {
//						miniCartonSellingPrice = Double
//								.parseDouble(MinimumCartonSellingPrice);
//					}
//					if (priceflag.matches("1")) {
//						/*if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
//								|| slCartonPerQty.matches("")) {
//							if (minSellingPrice > slcprice) {
//								Toast.makeText(
//										SalesAddProduct.this,
//										"Price must be greater than minimum selling price $ "
//												+ MinimumSellingPrice,
//										Toast.LENGTH_LONG).show();
//							} else {
//								storeInDatabase();
//							}
//						}else{
//							storeInDatabase();
//						}*/
//						if (miniCartonSellingPrice > slcprice) {
//							sl_cprice.requestFocus();
//							Toast.makeText(
//									SalesAddProduct.this,
//									"Carton Price must be greater than Minimum carton selling price $ "
//											+ MinimumCartonSellingPrice,
//									Toast.LENGTH_LONG).show();
//						} else if (piecePerCarton>1){
//							if(minSellingPrice > slprice) {
//								sl_price.requestFocus();
//								Toast.makeText(
//										SalesAddProduct.this,
//										"Price must be greater than minimum selling price $ "
//												+ MinimumSellingPrice,
//										Toast.LENGTH_LONG).show();
//							}else{
//								storeInDatabase();
//							}
//						}
//
//						else {
//									storeInDatabase();
//						}
//
//					} else {
//						if (minSellingPrice > slprice) {
//							Toast.makeText(
//									SalesAddProduct.this,
//									"Price must be greater than minimum selling price $ "
//											+ MinimumSellingPrice,
//									Toast.LENGTH_LONG).show();
//						} else {
//							storeInDatabase();
//
//						}
//						mAttributeArr.clear();
//
//			}
//		});

		sl_summary.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(SalesAddProduct.this, SalesSummary.class);
				startActivity(i);
				SalesAddProduct.this.finish();
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

	/*	sl_codefield
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						sl_codefield) {
					@Override
					public boolean onDrawableClick() {
						// TODO Auto-generated method stub
						alertDialogSearch();
						return true;

					}
				});

		sl_codefield.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					new CheckProductBarcode().execute();
					return true;
				}
				return false;
			}
		});*/

		sl_codefield
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						sl_codefield) {
					@Override
					public boolean onDrawableClick() {
						// TODO Auto-generated method stub
						if(!sl_qty.getText().toString().matches("")){
							dialog();
						}else {
							alertDialogSearch();
						}
						return true;

					}
				});

		sl_codefield.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
										  KeyEvent event) {
				if(!sl_qty.getText().toString().matches("")){
					dialog();
				}else {
					if (actionId == EditorInfo.IME_ACTION_NEXT || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
						new CheckProductBarcode().execute();
						return true;
					}}
				return false;
			}
		});

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

		sl_cprice.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {

					String qty = sl_qty.getText().toString();

					if (sl_price.getText().toString().equals("0.00")
							|| sl_cprice.getText().toString().equals("0")
							|| sl_cprice.getText().toString().equals("0.0")
							|| sl_cprice.getText().toString().equals(".0")
							|| sl_cprice.getText().toString().equals("0.000")
							|| sl_cprice.getText().toString().equals("0.0000")
							|| sl_cprice.getText().toString().equals("0.00000")) {
						sl_price.setText("");
					}

					if (!qty.matches("")) {
						double qtyCalc = Double.parseDouble(qty);
						String cPrice = sl_cprice.getText().toString();

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
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

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
				Log.d("slPriceCheck","-->"+slPrice);
				if (priceflag.matches("0")) {
					if(slPrice.matches("0.000")){
						itemDiscountCalcs();
					}else{
						itemDiscountCalc();
					}

				} else if (priceflag.matches("1")) {
					itemDiscountCalcNew();
				}

			}
		});

		sl_cartonQty.addTextChangedListener(cqtyTW);
		sl_looseQty.addTextChangedListener(lqtyTW);
		sl_qty.addTextChangedListener(qtyTW);
	}

	private void itemDiscountCalcs() {

		try {
			String prc;
			String itmDscnt = sl_itemDiscount.getText().toString();
			String qty = sl_qty.getText().toString();
			prc = sl_cprice.getText().toString();

			if (itmDscnt.matches("")) {
				itmDscnt = "0";
			}

			Log.d("prceCheck","-->"+prc);

			if (!itmDscnt.matches("") && !qty.matches("") && !prc.matches("")) {

				double itemDiscountCalc = 0.0;

				itemDiscountCalc = Double.parseDouble(itmDscnt);

				double quantityCalc = Double.parseDouble(qty);
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

	private void dialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Clear?");
		alertDialog
				.setMessage("Do You Want Clear Data?");
		//alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton("CLEAR",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						clearData();
						dialog.dismiss();
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

	public void itemDiscountCalc() {

		try {
			String prc;
			String itmDscnt = sl_itemDiscount.getText().toString();
			String qty = sl_qty.getText().toString();
			prc = sl_price.getText().toString();

			if (itmDscnt.matches("")) {
				itmDscnt = "0";
			}

			Log.d("prcCheck","-->"+prc);

			if (!itmDscnt.matches("") && !qty.matches("") && !prc.matches("")) {

				double itemDiscountCalc = 0.0;

				itemDiscountCalc = Double.parseDouble(itmDscnt);

				double quantityCalc = Double.parseDouble(qty);
				double priceCalc = Double.parseDouble(prc);

				tt = (quantityCalc * priceCalc) - itemDiscountCalc;

				Log.d("ttl", "" + tt);
				String Prodtotal = twoDecimalPoint(tt);
				sl_total.setText("" + Prodtotal);
				sl_total_inclusive.setText("" + Prodtotal);

				double taxAmount = 0.0, netTotal = 0.0;
				Log.d("taxTypeValue","-->"+taxType +","+taxValue);
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

				Log.d("TaxtypeValue","-->"+taxType+"-"+taxValue);

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
							sl_netTotal.setText("" + Prodtotal);
						}

					} else {
						sl_tax.setText("0.0");
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);
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

	public void itemDiscountCalcNew() {

		try {
			String itmDscnt = sl_itemDiscount.getText().toString();
			// String qty = sl_qty.getText().toString();
			// String prc = sl_price.getText().toString();
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
		cursor = SOTDatabase.getCursor();

		if (cursor != null && cursor.getCount() > 0) {
			sl_no = cursor.getCount();

			sl_no++;
		}
		Log.d("CursorCounts","-->"+cursor.getCount());

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

		try {
			if (codeStr.matches("")) {
				Toast.makeText(SalesAddProduct.this, "Select product code",
						Toast.LENGTH_SHORT).show();
			}  else if(nameStr.matches("")){
				Toast.makeText(SalesAddProduct.this, "Select product name",
						Toast.LENGTH_SHORT).show();
			} else if (calCarton.matches("1") && qtyStr.matches("")
					&& focStr.matches("") && exQtyStr.matches("")) {
				Toast.makeText(SalesAddProduct.this, "Enter the quantity",
						Toast.LENGTH_SHORT).show();

			} else if (calCarton.matches("0") && cartonQtyStr.matches("")
					&& looseQtyStr.matches("") && qtyStr.matches("")) {
				Toast.makeText(SalesAddProduct.this, "Enter the carton/quantity",
						Toast.LENGTH_SHORT).show();
			}else {

				if (!searchProductArr.isEmpty()) {
					carton_layout.setVisibility(View.GONE);
				}

//				int foc = 0;
				double price = 0, discount = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0,
						cartonQty = 0, looseQty = 0, qty = 0,cartonPerQty = 0,foc = 0;
				String sbTtl = "";
				String netT = "";
				if (!cartonQtyStr.matches("")) {
					cartonQty = Double.parseDouble(cartonQtyStr);
				}
				if (!looseQtyStr.matches("")) {
					looseQty = Double.parseDouble(looseQtyStr);
				}
				if (!qtyStr.matches("")) {
					qty = Double.parseDouble(qtyStr);
				}
				if (!focStr.matches("")) {
					foc = Double.parseDouble(focStr);
				}
				if (!cartonPerQtyStr.matches("")) {
					cartonPerQty = Double.parseDouble(cartonPerQtyStr);
				}


				Log.d("priceStr","-->"+priceStr);

				if (priceStr.matches("0.000")) {
					Log.d("cpriceStr","-->"+cpriceStr);
					price = Double.parseDouble(cpriceStr);
				}else if(!priceStr.matches("")){
					price = Double.parseDouble(priceStr);
				}else{
					price = Double.parseDouble("0.000");
				}

				if (priceflag.matches("0")) {
					Log.d("priceString","-->"+priceStr);
					if(priceStr.matches("0.000")){
						itemDiscountCalcs();
					}else{
						itemDiscountCalc();
					}
				} else if (priceflag.matches("1")) {
					itemDiscountCalcNew();
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

				Log.d("totalStr","-->"+totalStr+"tt"+tt);

				if(totalStr.matches("0.00")){
					total =tt;

					String itemDisc = sl_itemDiscount.getText().toString();
					if (!itemDisc.matches("")) {
						itmDisc = Double.parseDouble(itemDisc);
						subTotal = total;
					} else {
						subTotal = total;
					}

					sbTtl = twoDecimalPoint(subTotal);

					if ((str_ssupdate != null) || (str_sscancel != null)) {
						int i_sssno = Integer.parseInt(str_sssno);
						SOTDatabase.updateBillDisc(i_sssno, codeStr, sbTtl);
					} else {
						Log.d("codeStr1","-->"+codeStr);
						SOTDatabase.storeBillDisc(sl_no, codeStr, sbTtl);
					}


				}else if (!totalStr.matches("")) {
					total = Double.parseDouble(totalStr);

					String itemDisc = sl_itemDiscount.getText().toString();
					if (!itemDisc.matches("")) {
						itmDisc = Double.parseDouble(itemDisc);
						subTotal = total;
					} else {
						subTotal = total;
					}

					sbTtl = twoDecimalPoint(subTotal);

					if ((str_ssupdate != null) || (str_sscancel != null)) {
						int i_sssno = Integer.parseInt(str_sssno);
						SOTDatabase.updateBillDisc(i_sssno, codeStr, sbTtl);
					} else {
						Log.d("codeStr","-->"+codeStr+","+sbTtl);
						SOTDatabase.storeBillDisc(sl_no, codeStr, sbTtl);
					}

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

					SOTDatabase.updateProduct(codeStr, nameStr, cartonQty,
							looseQty, qty, foc, price, discount, uomStr,
							cartonPerQty, tt + dis, tax, sbTtl, netT, i_sssno,
							cpriceStr, exQtyStr);

				} else {

					if (SOTDatabase.getCursor() != null && SOTDatabase.getCursor().getCount() > 0) {
						productcode = SOTDatabase.getProductCode();
						Log.d("ProductCodeCheck", "" + productcode);
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
							SOTDatabase.storeProduct(sl_no, codeStr, nameStr,
									cartonQty, looseQty, qty, foc, price, discount,
									uomStr, cartonPerQty, tt + dis, tax, netT,
									taxType, taxValue, newPrice, sbTtl, cpriceStr,
									exQtyStr, MinimumSellingPrice, "", "", "",MinimumCartonSellingPrice,"","");
							Log.d("sotdb3", "original");
						}
					} else {

						if (!totalStr.matches("")) {
							SOTDatabase.storeBillDisc(sl_no, codeStr, sbTtl);
						}
						SOTDatabase.storeProduct(sl_no, codeStr, nameStr,
								cartonQty, looseQty, qty, foc, price, discount,
								uomStr, cartonPerQty, tt + dis, tax, netT, taxType,
								taxValue, newPrice, sbTtl, cpriceStr, exQtyStr,
								MinimumSellingPrice, "", "", "",MinimumCartonSellingPrice,"","");
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


//					String productid = SOTDatabase.getProductId(codeStr);
//
//					if (productid != null && !productid.isEmpty()) {
//
//						int i_sssno = Integer.parseInt(productid);
//						SOTDatabase.updateProduct(codeStr, nameStr, cartonQty,
//								looseQty, qty, foc, price, discount, uomStr,
//								cartonPerQty, tt + dis, tax, sbTtl, netT, i_sssno,
//								cpriceStr, exQtyStr);
//
//						SOTDatabase.updateBillDisc(i_sssno, codeStr, sbTtl);
//
//						final String productSlNo = SOTDatabase.getProdSlno(codeStr);
//						String attributeProductCode =  SOTDatabase.getAttributeProduct(productSlNo,codeStr);
//						if (attributeProductCode != null && !attributeProductCode.isEmpty()) {
//							SOTDatabase.deleteAttributeProducts(productSlNo,codeStr);
//							if(mAttributeArr.size()>0){
//								SOTDatabase.storeAttribute(productSlNo,codeStr, nameStr,mAttributeArr);
//							}
//						}else {
//							Log.d("mAttributeArr","-sAP->"+ mAttributeArr.size());
//
//							if(mAttributeArr.size()>0){
//								SOTDatabase.storeAttribute(productSlNo,codeStr, nameStr,mAttributeArr);
//							}
//						}
//					} else {
//
//						SOTDatabase.storeProduct(sl_no, codeStr, nameStr,
//								cartonQty, looseQty, qty, foc, price, discount,
//								uomStr, cartonPerQty, tt + dis, tax, netT, taxType,
//								taxValue, newPrice, sbTtl, cpriceStr, exQtyStr,
//								MinimumSellingPrice, "", "", "",MinimumCartonSellingPrice,haveBatch,
//								haveExpiry);
//
//						Log.d("mAttributeArr","-sAP->"+ mAttributeArr.size());
//
//						if(mAttributeArr.size()>0){
//							SOTDatabase.storeAttribute(String.valueOf(sl_no),codeStr, nameStr,mAttributeArr);
//						}
//					}
//
//
//					sl_codefield.setText("");
//					sl_namefield.setText("");
//					sl_cartonQty.setText("");
//					sl_looseQty.setText("");
//					sl_qty.setText("");
//					sl_foc.setText("");
//					sl_itemDiscount.setText("");
//					sl_total.setText("0");
//					sl_total_inclusive.setText("0");
//					sl_tax.setText("0");
//					sl_netTotal.setText("0");
//
//					sl_price.setText("");
//					sl_uom.setText("");
//					sl_cartonPerQty.setText("");
//					sl_stock.setText("");
//
//					sl_cprice.setText("");
//					sl_exchange.setText("");
//
//					sl_codefield.requestFocus();
//
//					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.hideSoftInputFromWindow(sl_codefield.getWindowToken(),
//							0);
//
//					sl_cartonQty.setEnabled(true);
//					sl_cartonQty.setFocusable(true);
//					sl_cartonQty.setBackgroundResource(drawable.edittext_bg);
//
//					sl_looseQty.setEnabled(true);
//					sl_looseQty.setFocusable(true);
//					sl_looseQty.setBackgroundResource(drawable.edittext_bg);

					sl_qty.setEnabled(true);
					sl_qty.setBackgroundResource(drawable.edittext_bg);
					sl_qty.setFocusableInTouchMode(true);

				}
//					if (SOTDatabase.getCursor() != null
//							&& SOTDatabase.getCursor().getCount() > 0) {
//
//						productcode = SOTDatabase.getProductCode();
//						Log.d("ProductCode", "" + productcode);
//						for (String prodcode : productcode) {
//							if (prodcode.matches(sl_codefield.getText().toString())) {
//								prdcode = true;
//								Log.d("ProductCode", prodcode);
//								break;
//							}
//						}
//						if (prdcode == true) {
//
//							productCodeFilter(sl_no, codeStr, nameStr, cartonQty,
//									looseQty, qty, foc, price, discount, uomStr,
//									cartonPerQty, tt + dis, tax, netT, taxType,
//									taxValue, newPrice, sbTtl, cpriceStr, exQtyStr);
//							Log.d("sotdb2", "matches");
//							prdcode = false;
//
//						} else {
//
//							if (!totalStr.matches("")) {
//								SOTDatabase.storeBillDisc(sl_no, codeStr, sbTtl);
//							}
//							SOTDatabase.storeProduct(sl_no, codeStr, nameStr,
//									cartonQty, looseQty, qty, foc, price, discount,
//									uomStr, cartonPerQty, tt + dis, tax, netT,
//									taxType, taxValue, newPrice, sbTtl, cpriceStr,
//									exQtyStr, MinimumSellingPrice, "", "", "",MinimumCartonSellingPrice,"","");
//							Log.d("sotdb3", "original");
//						}
//					} else {
//
//						if (!totalStr.matches("")) {
//							SOTDatabase.storeBillDisc(sl_no, codeStr, sbTtl);
//						}
//						SOTDatabase.storeProduct(sl_no, codeStr, nameStr,
//								cartonQty, looseQty, qty, foc, price, discount,
//								uomStr, cartonPerQty, tt + dis, tax, netT, taxType,
//								taxValue, newPrice, sbTtl, cpriceStr, exQtyStr,
//								MinimumSellingPrice, "", "", "",MinimumCartonSellingPrice,"","");
//
//					}
//
//					Log.d("CPRICE", cpriceStr);
//
//					sl_codefield.setText("");
//					sl_namefield.setText("");
//					sl_cartonQty.setText("");
//					sl_looseQty.setText("");
//					sl_qty.setText("");
//					sl_foc.setText("");
//					sl_itemDiscount.setText("");
//					sl_total.setText("0");
//					sl_total_inclusive.setText("0");
//					sl_tax.setText("0");
//					sl_netTotal.setText("0");
//
//					sl_price.setText("");
//					sl_uom.setText("");
//					sl_stock.setText("");
//					sl_cartonPerQty.setText("");
//
//					sl_cprice.setText("");
//					sl_exchange.setText("");
//
//					sl_codefield.requestFocus();
//
//					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.hideSoftInputFromWindow(sl_codefield.getWindowToken(), 0);
//					// imm.showSoftInput(sl_codefield,
//					// InputMethodManager.SHOW_IMPLICIT);
//
//					sl_cartonQty.setEnabled(true);
//					sl_cartonQty.setFocusable(true);
//					sl_cartonQty.setBackgroundResource(drawable.edittext_bg);
//
//					sl_looseQty.setEnabled(true);
//					sl_looseQty.setFocusable(true);
//					sl_looseQty.setBackgroundResource(drawable.edittext_bg);
//					// Toast.makeText(SalesAddProduct.this, "Stored in database",
//					// Toast.LENGTH_LONG).show();
//				}
//
//				if (!searchProductArr.isEmpty()) {
//					adapter.notifyDataSetChanged();
//				}
//
//				if ((str_ssupdate != null) || (str_sscancel != null)) {
//					Toast.makeText(SalesAddProduct.this, "Updated ",
//							Toast.LENGTH_LONG).show();
//					Intent i = new Intent(SalesAddProduct.this,
//							InvoiceSummary.class);
//					startActivity(i);
//					SalesAddProduct.this.finish();
//				}
//
			}
//
			if (!searchProductArr.isEmpty()) {
				mProductSearchAdapter.notifyDataSetChanged();
			}
			if ((str_ssupdate != null) || (str_sscancel != null)) {
					Toast.makeText(SalesAddProduct.this, "Updated ",
							Toast.LENGTH_LONG).show();
					Intent i = new Intent(SalesAddProduct.this,
							SalesSummary.class);
					startActivity(i);
					SalesAddProduct.this.finish();
				}
//

		} catch (Exception e) {
			e.printStackTrace();
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
				SalesAddProduct.this);

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
								SalesAddProduct.this);
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
                                .equals(keyValue.toLowerCase())) {

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

        }
        */
	public void alertDialogCustomergroup(final ArrayList<HashMap<String, String>> allList) {

		  AlertDialog.Builder myDialog = new AlertDialog.Builder(
		    SalesAddProduct.this);
		  editText = new EditText(SalesAddProduct.this);
		  final ListView listview = new ListView(SalesAddProduct.this);
		  LinearLayout layout = new LinearLayout(SalesAddProduct.this);
		  layout.setOrientation(LinearLayout.VERTICAL);
		  myDialog.setTitle("CustomerGroupCode");
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
		  arrayAdapterProd = new CustomAlertAdapterProd(SalesAddProduct.this, allList);
		  listview.setAdapter(arrayAdapterProd);
		  
		  listview.setOnItemClickListener(new OnItemClickListener() {

		   @Override
		   public void onItemClick(AdapterView<?> arg0, View arg1, int position,
		     long arg3) {

		    myalertDialog.dismiss();
		    getArraylist = arrayAdapterProd.getArrayList();

		    HashMap<String, String> datavalue = getArraylist.get(position);
		    Set<Entry<String, String>> keys = datavalue.entrySet();
		    Iterator<Entry<String, String>> iterator = keys.iterator();
		    while (iterator.hasNext()) {
		     @SuppressWarnings("rawtypes")
		     Entry mapEntry = iterator.next();
		     String custCode = (String) mapEntry.getKey();
		     String custgroupcode = (String) mapEntry.getValue();
		     
		     SalesOrderSetGet.setSuppliergroupcode(custgroupcode);
		     cstgrpcd = SalesOrderSetGet.getSuppliergroupcode();
		     
		     AsyncCallWSProductWithCustomerPrice task = new AsyncCallWSProductWithCustomerPrice();
		     task.execute();
		     
		    }
		   }
		  });
		  
		  
		  searchResults = new ArrayList<HashMap<String, String>>(allList);
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
		    for (int i = 0; i < allList.size(); i++) {
		     String supplierName = allList.get(i).toString();
		     if (textlength <= supplierName.length()) {
		      if (supplierName.toLowerCase().contains(
		        editText.getText().toString().toLowerCase()
		          .trim()))
		       searchResults.add(allList.get(i));
		     }
		    }

		    arrayAdapterProd = new CustomAlertAdapterProd(
		      SalesAddProduct.this, searchResults);
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

	public void alertDialogSearch() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				SalesAddProduct.this);
		editText = new EditText(SalesAddProduct.this);
		final ListView listview = new ListView(SalesAddProduct.this);
		LinearLayout layout = new LinearLayout(SalesAddProduct.this);
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
		arrayAdapterProd = new CustomAlertAdapterProd(SalesAddProduct.this, al);
		listview.setAdapter(arrayAdapterProd);
//		listview.setOnItemClickListener(SalesAddProduct.this);

		listview.setOnItemClickListener(new OnItemClickListener() {

			   @Override
			   public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			     long arg3) {
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
						loadprogress();
						AsyncCallSaleProduct task = new AsyncCallSaleProduct("false");
						task.execute();

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

								sl_price.setText("");
								sl_uom.setText("");
								sl_cartonPerQty.setText("");
								sl_stock.setText("");
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

				arrayAdapterProd = new CustomAlertAdapterProd(
						SalesAddProduct.this, searchResults);
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

	/*@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		myalertDialog.dismiss();
		getArraylist = arrayAdapterProd.getArrayList();

		HashMap<String, String> datavalue = getArraylist.get(position);
		Set<Entry<String, String>> keys = datavalue.entrySet();
		Iterator<Entry<String, String>> iterator = keys.iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry mapEntry = iterator.next();
			sales_prodCode = (String) mapEntry.getKey();
			values = (String) mapEntry.getValue();

			sl_codefield.setText(sales_prodCode);
			sl_namefield.setText(values);

			AsyncCallSaleProduct task = new AsyncCallSaleProduct();
			task.execute();

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

					sl_price.setText("");
					sl_uom.setText("");
					sl_cartonPerQty.setText("");
					sl_stock.setText("");
				}
			});
		}

	}*/

	/** AsyncCall Start **/

	private class GetProduct extends AsyncTask<Void, Void, Void> {
		String dialogStatus;

		@SuppressLint("NewApi")
		@Override
		protected void onPreExecute() {
			al.clear();
			customerGroupCodeArr.clear();
			getActionBar().setHomeButtonEnabled(false);
			menu.setSlidingEnabled(false);
			loadprogress();
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				if (onlineMode.matches("True")) {
					if (checkOffline == true) { // temp_offline

						if (dialogStatus.matches("true")) {
							productTxt = offlineDatabase.getProduct();
							productresult = " { SODetails : " + productTxt
									+ "}";
						} else {

							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else { // Onllineine
					//	productresult = getproductOnline();
						
						
						mHashMap.put("CompanyCode", comapanyCode);
						productresult = SalesOrderWebService.getSODetail(mHashMap, "fncGetProductForSearch");
						mHashMap.put("CustomerCode", cstcode);
					   customerGroupCodeArr = SalesOrderWebService.getCustomerPriceMultiple(mHashMap, "fncGetCustomerPrice_Multiple");
						
					}

				} else if (onlineMode.matches("False")) { // permanent_offline
					productTxt = offlineDatabase.getProduct();
					productresult = " { SODetails : " + productTxt + "}";
				}

			} catch (Exception e) {
				e.printStackTrace();

			}

			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(productresult);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SODetails");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String productcodes = jsonChildNode.optString(PRODUCT_CODE)
							.toString();
					String productnames = jsonChildNode.optString(PRODUCT_NAME)
							.toString();

					HashMap<String, String> producthm = new HashMap<String, String>();
					producthm.put(productcodes, productnames);
					al.add(producthm);
					/*hashmap.putAll(producthm);
					HashMap<String, String> productsplithm = new HashMap<String, String>();
					albarsplit.add(productsplithm);
					hmsplitbc.putAll(productsplithm);
					alprodcode.add(productcodes);*/
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Void result) {
			
			if(haveMultipleCustomerPrice.matches("1")){
				if(customerGroupCodeArr.size()>0){
					productTag.setVisibility(View.VISIBLE);
					  filter.setEnabled(false);
					  sl_codefield.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
					  disableView(sl_codefield);
				}else{
					productTag.setVisibility(View.GONE);
				}
		    }else{
		    	productTag.setVisibility(View.GONE);
		    }
			
	
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);
			getActionBar().setHomeButtonEnabled(true);
			menu.setSlidingEnabled(true);
		}

	}
	public static void disableView(View v) {

	     v.setOnTouchListener(new OnTouchListener() {
	         @Override
	         public boolean onTouch(View v, MotionEvent event) {
	             return true;
	         }
	     });
	 }
	private class CheckProductBarcode extends AsyncTask<Void, Void, Void> {

		String dialogStatus="",ed_code="";
		@Override
		protected void onPreExecute() {
			mBarcodeJsonString="";
			dialogStatus = checkInternetStatus();
			ed_code = sl_codefield.getText().toString();
			loadprogress();
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
						} else {
							Log.d("CheckOffline Alert -->", "False");
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {
						Log.d("checkOffline Status -->", "False");

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
			 new AsyncCallSaleProduct("false").execute();
			}else{
				sales_prodCode = ed_code;
				new AsyncCallSaleProduct("false").execute();
			}
			
		}


	}
/*	private class AsyncCallBARCODE extends AsyncTask<Void, Void, Void> {
		String dialogStatus;

		@Override
		protected void onPreExecute() {
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if (onlineMode.matches("True")) {
				if (checkOffline == true) { // temp_offline

					if (dialogStatus.matches("true")) {
						productTxt = offlineDatabase.getProductBacode();
						barcoderesult = " { BarCodeDetails : " + productTxt
								+ "}";
					} else {

						finish();
					}

				} else { // Onllineine

					barcoderesult = getbarcodeOnline();
				}

			} else if (onlineMode.matches("False")) { // permanent_offline

				productTxt = offlineDatabase.getProductBacode();
				barcoderesult = " { BarCodeDetails : " + productTxt + "}";
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
	}*/

	private class AsyncCallSaleProduct extends AsyncTask<Void, Void, Void> {

		String dialogStatus="",filterClick="",codefield="",namefield="",retailPrice,havePos = "";
		public AsyncCallSaleProduct(String fromFilterClick){
			filterClick = fromFilterClick;
		}
		@Override
		protected void onPreExecute() {

			getSalesProdArr.clear();
			colorArr.clear();
			newPrice = "";
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
					if (checkOffline == true) { // temp_offline

						if (dialogStatus.matches("true")) {
							getSalesProdArr = offlineDatabase
									.getSaleProduct(sales_prodCode, "SalesAddProduct");
							priceArr = OfflineDatabase.getCustomerPrice(
									cstgrpcd, sales_prodCode);
						} else {

							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else { // Onllineine

						getSalesProdArr = SalesProductWebService
								.getSaleProduct(sales_prodCode, webMethName, "SalesAddProduct");
						priceArr = SalesProductWebService.getProductPrice(
								cstgrpcd, cstcode, sales_prodCode,
								"fncGetProductPriceForSales", "SO");
						if(haveAttribute.equalsIgnoreCase("2")){

							colorArr = SalesProductWebService.getProductAttribute(
									sales_prodCode, "fncGetProductAttribute");
						}

					}

				} else if (onlineMode.matches("False")) { // permanent_offline
					getSalesProdArr = offlineDatabase
							.getSaleProduct(sales_prodCode, "SalesAddProduct");
					priceArr = OfflineDatabase.getCustomerPrice(cstgrpcd,
							sales_prodCode);
				}

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

				slPrice = getSalesProdArr.get(0);
				slUomCode = getSalesProdArr.get(1);
				slCartonPerQty = getSalesProdArr.get(2);
				cprice = getSalesProdArr.get(4);
				MinimumSellingPrice = getSalesProdArr.get(5);
				// taxValue = SalesOrderSetGet.getTaxValue();
				taxValue = getSalesProdArr.get(3);
				haveBatch = getSalesProdArr.get(6);
				haveExpiry = getSalesProdArr.get(7);
				codefield = getSalesProdArr.get(8);
				namefield = getSalesProdArr.get(9);				
				Weight = getSalesProdArr.get(10);
				MinimumCartonSellingPrice = getSalesProdArr.get(11);
				retailPrice = getSalesProdArr.get(13);
				havePos = SalesOrderSetGet.getHavePos();
				Log.d("Weight",""+ Weight);
				Log.d("HaveposValue",""+havePos+"-->"+retailPrice);
				sl_codefield.setText(codefield);
				sl_namefield.setText(namefield);


				if(havePos.matches("1")){
					retailPrice_layout.setVisibility(View.VISIBLE);
					invoice_retail.setVisibility(View.VISIBLE);
					sl_retail.setText(retailPrice);
				}

				if(haveBatch!=null && !haveBatch.isEmpty() && haveExpiry!=null && !haveExpiry.isEmpty() ){
					if(haveBatch.equalsIgnoreCase("True") && haveExpiry.equalsIgnoreCase("True")){
						sl_exchange.setEnabled(false);
						sl_exchange.setFocusable(false);
						sl_exchange.setGravity(Gravity.CENTER);
						sl_exchange.setBackgroundResource(drawable.labelbg);
						sl_exchange.setFocusableInTouchMode(false);
					}else {
						sl_exchange.setEnabled(true);
						sl_exchange.setBackgroundResource(drawable.edittext_bg);
						sl_exchange.setFocusableInTouchMode(true);
					}
				}else {
					sl_exchange.setEnabled(true);
					sl_exchange.setBackgroundResource(drawable.edittext_bg);
					sl_exchange.setFocusableInTouchMode(true);
				}

				if (FormSetterGetter.isEditPrice()) {
					sl_price.setBackgroundResource(drawable.edittext_bg);
					sl_cprice.setBackgroundResource(drawable.edittext_bg);
				} else {

					double priceCheckZero = 0;
					if(!slPrice.matches("")){
						priceCheckZero = Double.parseDouble(slPrice);
					}

					if(priceCheckZero>0){
						sl_price.setEnabled(false);
						sl_price.setFocusable(false);
						sl_price.setGravity(Gravity.CENTER);
						sl_price.setBackgroundResource(drawable.labelbg);
						sl_price.setFocusableInTouchMode(false);
					}else{

						sl_price.setEnabled(true);
						sl_price.setBackgroundResource(drawable.edittext_bg);
						sl_price.setFocusableInTouchMode(true);


					}

					double cpriceCheckZero = 0;
					if(!cprice.matches("")){
						cpriceCheckZero = Double.parseDouble(cprice);
					}

					if(cpriceCheckZero>0){
						sl_cprice.setEnabled(false);
						sl_cprice.setFocusable(false);
						sl_cprice.setGravity(Gravity.CENTER);
						sl_cprice.setBackgroundResource(drawable.labelbg);
						sl_cprice.setFocusableInTouchMode(false);
					}else{

						sl_cprice.setEnabled(true);
						sl_cprice.setBackgroundResource(drawable.edittext_bg);
						sl_cprice.setFocusableInTouchMode(true);
					}
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


				String code = sl_codefield.getText().toString();
				String name = sl_namefield.getText().toString();
				Log.d("codecheck",code);

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

				if(FormSetterGetter.getHidePrice().matches("Hide Price")){
					/*((LinearLayout) findViewById(R.id.total_txt_layout)).setVisibility(View.GONE);
					((LinearLayout) findViewById(R.id.total_edt_layout)).setVisibility(View.GONE);
					sl_price.setVisibility(View.INVISIBLE);
					expand.setVisibility(View.INVISIBLE);
					price_txt.setVisibility(View.INVISIBLE);
					sl_cprice.setVisibility(View.GONE);
					price_header_layout.setVisibility(View.GONE);*/
					sl_price.setVisibility(View.INVISIBLE);
					txt_price.setVisibility(View.GONE);
				}else{
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

				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesproduct_layout, true);

				sl_cartonQty.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(sl_cartonQty,
						InputMethodManager.SHOW_IMPLICIT);

			}
			else{
				sl_codefield.requestFocus();
				Toast.makeText(getApplicationContext(), " Invalid Code ",
						Toast.LENGTH_SHORT).show();
				sl_codefield.setText("");
				sl_namefield.setText("");
			}
			
			if(filterClick!=null && !filterClick.isEmpty() && filterClick.matches("true")){
				String db_pcode = SOTDatabase.getProductCode(codefield);
				Log.d("db_pcode", "->"+db_pcode);
					
					if(db_pcode != null && !db_pcode.isEmpty()){
						double pqty = SOTDatabase.getProductQty(db_pcode);
						sl_qty.setText(""+pqty);
						sl_qty.requestFocus();
						sl_qty.setSelection(sl_qty.length());
					}
			}

			if (!colorArr.isEmpty()) {
				Log.d("checkup",colorArr.toString());
				getAttributeValue();

			} else {
				Log.d("no color data","no color data");
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);

		}
	}


	public void getAttributeValue(){
		String code = sl_codefield.getText().toString();
		String name = sl_namefield.getText().toString();
		String productCode = SOTDatabase.getProductCode(code);
		if (productCode != null && !productCode.isEmpty()) {
			Toast.makeText(SalesAddProduct.this,
					"This product is already added",
					Toast.LENGTH_SHORT).show();

			clearData();

		} else {
			productModifierDialog.initiatePopupWindow(
					SalesAddProduct.this, code, name, colorArr, sl_qty, screenInches);

			productModifierDialog.show(getFragmentManager(), "dialog");

			productModifierDialog.setOnCompletionListener(new ColorAttributeDialog.OnCompletionListener() {
				@Override
				public void onCompleted(String qty, ArrayList<Attribute> AttributeArr) {
					Log.d("AttributeArr", "onComplete-->" + AttributeArr.size());
					mAttributeArr = new ArrayList<>();
					mAttributeArr = AttributeArr;
					Log.d("AttributeArr", "onafter-->" + mAttributeArr.size());

					if (FormSetterGetter.isEditPrice()) {
						sl_price.setBackgroundResource(drawable.edittext_bg);
						sl_cprice.setBackgroundResource(drawable.edittext_bg);
					} else {
						double priceCheckZero = 0;
						if (!slPrice.matches("")) {
							priceCheckZero = Double.parseDouble(slPrice);
						}

						if (priceCheckZero > 0) {
							sl_price.setEnabled(false);
							sl_price.setFocusable(false);
							sl_price.setGravity(Gravity.CENTER);
							sl_price.setBackgroundResource(drawable.labelbg);
							sl_price.setFocusableInTouchMode(false);
						} else {
							sl_price.setEnabled(true);
							sl_price.setBackgroundResource(drawable.edittext_bg);
							sl_price.setFocusableInTouchMode(true);
						}

						double cpriceCheckZero = 0;
						if (!cprice.matches("")) {
							cpriceCheckZero = Double.parseDouble(cprice);
						}

						if (cpriceCheckZero > 0) {
							sl_cprice.setEnabled(false);
							sl_cprice.setFocusable(false);
							sl_cprice.setGravity(Gravity.CENTER);
							sl_cprice.setBackgroundResource(drawable.labelbg);
							sl_cprice.setFocusableInTouchMode(false);
						} else {
							sl_cprice.setEnabled(true);
							sl_cprice.setBackgroundResource(drawable.edittext_bg);
							sl_cprice.setFocusableInTouchMode(true);
						}
					}

					sl_cartonQty.setEnabled(false);
					sl_cartonQty.setFocusable(false);
					sl_cartonQty.setFocusableInTouchMode(false);
					sl_cartonQty.setGravity(Gravity.CENTER);
					sl_cartonQty.setBackgroundResource(drawable.labelbg);

					sl_looseQty.setEnabled(false);
					sl_looseQty.setFocusable(false);
					sl_looseQty.setFocusableInTouchMode(false);
					sl_looseQty.setGravity(Gravity.CENTER);
					sl_looseQty.setBackgroundResource(drawable.labelbg);

					sl_qty.setEnabled(false);
					sl_qty.setFocusable(false);
					sl_qty.setFocusableInTouchMode(false);
					sl_qty.setGravity(Gravity.CENTER);
					sl_qty.setBackgroundResource(drawable.labelbg);
				}
			});
			productModifierDialog.setOnDismissDialogListener(new ColorAttributeDialog.DismissDialogListener() {
				@Override
				public void onCompleted() {
					clearData();
				}
			});
		}
		Log.d("soap",colorArr.toString());
	}

	public void clearData(){
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
		sl_cartonPerQty.setText("");
		sl_stock.setText("");

		sl_cprice.setText("");
		sl_exchange.setText("");

		sl_codefield.requestFocus();

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(sl_codefield.getWindowToken(),
				0);

		sl_cartonQty.setEnabled(true);
		sl_cartonQty.setFocusable(true);
		sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

		sl_looseQty.setEnabled(true);
		sl_looseQty.setFocusable(true);
		sl_looseQty.setBackgroundResource(drawable.edittext_bg);

		sl_qty.setEnabled(true);
		sl_qty.setBackgroundResource(drawable.edittext_bg);
		sl_qty.setFocusableInTouchMode(true);
	}

	private class AsyncCallWSProductWithCustomerPrice extends AsyncTask<Void, Void, Void> {

		String dialogStatus;

		@Override
		protected void onPreExecute() {
			searchProductArr.clear();
			dialogStatus = checkInternetStatus();
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {

						if (dialogStatus.matches("true")) {
//							searchProductArr = offlineDatabase.searchProduct(
//									catStr, subCatStr);
						} else {
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {
						HashMap<String, String> hm = new HashMap<String, String>();
					      hm.put("CustomerGroupCode", cstgrpcd);
					      searchProductArr = NewProductWebService.customerPriceProduct(hm, "fncGetCustomerPrice");
					}

				} else if (onlineMode.matches("False")) {
//					searchProductArr = offlineDatabase.searchProduct(catStr,
//							subCatStr);

				}
				Log.d("search result", "" + searchProductArr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// searchProductArr = NewProductWebService.searchProduct(catStr,
			// subCatStr, "fncGetProduct");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (!searchProductArr.isEmpty()) {
				carton_layout.setVisibility(View.GONE);
				grid_layout.setVisibility(View.VISIBLE);
				sl_searchfield.setVisibility(View.VISIBLE);
			    sl_searchfield.requestFocus();
			} else {
				Toast.makeText(getApplicationContext(), "No matches found",
						Toast.LENGTH_SHORT).show();
			}

			try {
				searchProductList();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);
		}
	}


	private class AsyncCallWSSearchProduct extends AsyncTask<Void, Void, Void> {

		String dialogStatus;

		@Override
		protected void onPreExecute() {
			dialogStatus = checkInternetStatus();
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {

						if (dialogStatus.matches("true")) {
							searchProductArr = offlineDatabase.searchProduct(
									catStr, subCatStr);
						} else {
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {
						searchProductArr = NewProductWebService.searchProduct(
								catStr, subCatStr, "fncGetProduct");
					}

				} else if (onlineMode.matches("False")) {
					searchProductArr = offlineDatabase.searchProduct(catStr,
							subCatStr);

				}
				Log.d("search result", "" + searchProductArr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// searchProductArr = NewProductWebService.searchProduct(catStr,
			// subCatStr, "fncGetProduct");

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);
		}
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

						if (dialogStatus.matches("true")) {
							product_stock_jsonString = offlineDatabase
									.getStockValue(LocationCode, sales_prodCode);
						} else {
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}
					} else {
						product_stock_jsonString = stock();
					}

				} else if (onlineMode.matches("False")) {
					product_stock_jsonString = offlineDatabase.getStockValue(
							LocationCode, sales_prodCode);

				}

			} catch (Exception e) {
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
					
					Log.d("stock qty", stock);

				}
			} catch (Exception e) {

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (!sl_codefield.getText().toString().matches("")) {
			sl_stock.setText(" " + stock);
			}
			}
	}

	public void searchProductList() throws ParseException {

/*		adapter = new AddSimpleAdapter(SalesAddProduct.this, searchProductArr,
				R.layout.sale_productitem, new String[] { "ProductCode",
						"ProductName", "WholeSalePrice","Qty"   }, new int[] {
						R.id.txt_code, R.id.txt_name, R.id.txt_price, R.id.txt_qty })
		{
		    @Override
		    public View getView(int position, View convertView, ViewGroup parent) {
		        View view = super.getView(position, convertView, parent);
		        TextView text = (TextView) view.findViewById(R.id.txt_price);
		        *//******** Amount will Gone or Visible Based on Hide Price From GetUserPermission  *********//*
		        if(FormSetterGetter.getHidePrice()!=null && !FormSetterGetter.getHidePrice().isEmpty()){	
					if(FormSetterGetter.getHidePrice().matches("Hide Price")){		
						text.setVisibility(View.GONE);
			        	 ((TextView) findViewById(R.id.txt3)).setVisibility(View.GONE);
					}else{
						 text.setVisibility(View.VISIBLE);
			        	 ((TextView) findViewById(R.id.txt3)).setVisibility(View.VISIBLE);
					}
		        }else{
		        	 text.setVisibility(View.VISIBLE);
		        	 ((TextView) findViewById(R.id.txt3)).setVisibility(View.VISIBLE);
		        }
		        
		       
		        return view;
		    }
		};


*/
		mProductSearchAdapter = new  ProductSearchAdapter(SalesAddProduct.this,searchProductArr);  
		  productFilterList.setAdapter(mProductSearchAdapter);
		  /*** Amount will Gone or Visible Based on Hide Price From GetUserPermission  ****/
		        if(FormSetterGetter.getHidePrice()!=null && !FormSetterGetter.getHidePrice().isEmpty()){ 
		   if(FormSetterGetter.getHidePrice().matches("Hide Price")){    
		           ((TextView) findViewById(R.id.txt3)).setVisibility(View.GONE);
		   }else{    
		           ((TextView) findViewById(R.id.txt3)).setVisibility(View.VISIBLE);
		   }
		        }else{         
		          ((TextView) findViewById(R.id.txt3)).setVisibility(View.VISIBLE);
		        }
	}

	/** AsyncCall End **/

	/** Offline Start **/

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
	}*/

	/*public String getproductOnline() {
		String productresult = "";
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
		HttpTransportSE androidHttpTransport = new HttpTransportSE(valid_url);
		try {

			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			productTxt = response.toString();
			productresult = " { ProductDetails : " + productTxt + "}";
		} catch (Exception e) {
			productresult = "";
		}
		return productresult;

	}*/

	private String checkInternetStatus() {
		String internetStatus = "";
		String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			if(mobileHaveOfflineMode.matches("1")){

		checkOffline = OfflineCommon.isConnected(SalesAddProduct.this);
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

	/** Offline End **/

	public void loadprogress() {
		spinnerLayout = new LinearLayout(SalesAddProduct.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(salesproduct_layout, false);
		progressBar = new ProgressBar(SalesAddProduct.this);
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

	@Override
	public void onBackPressed() {
		if ((str_ssupdate != null) || (str_sscancel != null)) {
			Intent i = new Intent(SalesAddProduct.this, SalesSummary.class);
			startActivity(i);
			SalesAddProduct.this.finish();
		} else if (intentString.matches("SalesOrder")) {

			Intent i = new Intent(SalesAddProduct.this,
					CustomerListActivity.class);
			startActivity(i);
			SalesAddProduct.this.finish();

		} else if (intentString.matches("Route SalesOrder")) {

			Intent i = new Intent(SalesAddProduct.this, RouteHeader.class);
			startActivity(i);
			SalesAddProduct.this.finish();

		} else {
			Intent i = new Intent(SalesAddProduct.this,
					SalesOrderCustomer.class);
			startActivity(i);
			SalesAddProduct.this.finish();
		}
	}

	@Override
	public void onListItemClick(String item) {
		// TODO Auto-generated method stub
		menu.toggle();
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

	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Deleting");
		alertDialog
				.setMessage("SalesOrder products will clear. Do you want to proceed");
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(SalesAddProduct.this,
								SalesOrderHeader.class);
						startActivity(i);
						SalesAddProduct.this.finish();

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


}