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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.AddProductSetterGetter;
import com.winapp.fwms.AndroidTabLayoutActivity;
import com.winapp.fwms.ClearFieldSetterGetter;
import com.winapp.fwms.CustomAlertAdapterProd;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.NewProductWebService;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.sot.FilterCS.OnFilterCompletionListener;

public class GraAddProduct extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace, OnItemClickListener {

	private static String webMethName = "fncGetProduct";
	private static String webMethNamebar = "fncGetProductBarCode";
	private static String webMethNameSearch = "fncGetProductForSearch";
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String PRODUCT_NAME = "ProductName";
	private static final String PRODUCTNAME_BARCODE = "ProductCode";
	private static final String PRODUCT_BARCODE = "Barcode";
    private String mProductJsonString ="",mBarcodeJsonString="";
    private JSONObject mProductJsonObject =null,mBarcodeJsonObject=null;
	private JSONArray mProductJsonArray=null,mBarcodeJsonArray=null;
	SlidingMenu menu;
	TextView textView1;
	EditText editText1, sl_stock;
	ArrayList<HashMap<String, String>> searchProductArr = new ArrayList<HashMap<String, String>>();
	Button sl_addProduct, sl_summary;
	ImageView newprodbutton, ed_product;
	EditText sl_codefield, sl_namefield, sl_cartonQty, sl_looseQty, sl_qty,
			sl_foc, sl_price, sl_itemDiscount, sl_uom, sl_cartonPerQty,
			sl_total, sl_tax, sl_netTotal,sl_total_inclusive,sl_cprice;
	AlertDialog myalertDialog = null;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> albarsplit = new ArrayList<HashMap<String, String>>();
	EditText editText,sl_exchange;
	CustomAlertAdapterProd arrayAdapterProd;
	ArrayList<HashMap<String, String>> searchResults;
	int textlength = 0;
	ArrayList<HashMap<String, String>> getArraylist = new ArrayList<HashMap<String, String>>();
	ArrayList<String> albar = new ArrayList<String>();
	ArrayList<String> alprodcode = new ArrayList<String>();
	HashMap<String, String> hashmap = new HashMap<String, String>();
	ProgressBar progressBar;
	LinearLayout spinnerLayout, customer_layout, foc_layout, foc_header_layout,
			grid_layout, carton_layout, pcs_txt_layout, pcs_layout,price_header_layout;
	LinearLayout salesproduct_layout, gra_carton_header,expand_layout;
	String valid_url, productTxt, productresult, barcoderesult, barcodeTxt;
	HashMap<String, String> hmsplitbc = new HashMap<String, String>();
	String slPrice = "", slUomCode = "", slCartonPerQty = "";
	ArrayList<String> getSalesProdArr = new ArrayList<String>();
	String keyValues = "", values = "";
	static ArrayList<String> companyArr = new ArrayList<String>();
	String taxType = "", taxValue = "",priceflag = "", sales_prodCode,cprice = "",MinimumSellingPrice = "",MinimumCartonSellingPrice="";;
	Cursor cursor;
	String beforeLooseQty, beforeFoc;
	ListView productFilterList;
	String catStr = "", subCatStr = "", cstcode = "", cstgrpcd = "",
			newPrice = "",newcprice = "";
	String ss_Cqty = "";
	int sl_no = 1;
	double itmDisc = 0;
	double netTtal = 0, taxAmount = 0;
	Bundle extra;
	String str_ssupdate, str_sscancel, str_sssno;
	double tt;
	TextWatcher cqtyTW, lqtyTW, qtyTW;
	TextView listing_screen, customer_screen, addProduct_screen,
			summary_screen, price_txt,txt_price;
	// FilterSearch filterSearch;
	private FilterCS filtercs;
	ImageView expand;
	HashMap<String, String> producthashValue = new HashMap<String, String>();
	String product_stock_jsonString = null;
	JSONObject product_stock_jsonResponse;
	JSONArray product_stock_jsonMainNode;
	String cmpnyCode = "", LocationCode = "", stock, calCarton="",appType="",Weight="";
	BatchDialog batchDialog;
	SimpleAdapter adapter;
	private HashMap<String, String> mHashMap;
	ArrayList<String> priceArr = new ArrayList<String>();
	private boolean doubleClick,expandcheck = false;
	private DialogPcsPerCarton mDialogPcsPerCarton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		// getActionBar().setTitle("Add Product");
		setContentView(R.layout.gra_addproduct);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.addproduct_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		ImageButton filter = (ImageButton) customNav.findViewById(R.id.filter);
		ImageButton pricetag = (ImageButton) customNav
				.findViewById(R.id.priceTag);
		newprodbutton = (ImageButton) customNav
				.findViewById(R.id.new_prod);
		ed_product = (ImageButton) customNav.findViewById(R.id.edit_prod); 
		txt.setText("Add Product");
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

		// filterSearch = new FilterSearch(this);
		filtercs = new FilterCS(GraAddProduct.this);
		salesproduct_layout = (LinearLayout) findViewById(R.id.salesproduct_layout);

		pcs_txt_layout = (LinearLayout) findViewById(R.id.pcs_txt_layout);
		pcs_layout = (LinearLayout) findViewById(R.id.pcs_layout);
		price_header_layout = (LinearLayout) findViewById(R.id.sales_price_header_layout);
		foc_layout = (LinearLayout) findViewById(R.id.foc_layout);
		foc_header_layout = (LinearLayout) findViewById(R.id.gra_foc_header_layout);
		grid_layout = (LinearLayout) findViewById(R.id.gra_grid_layout);
		carton_layout = (LinearLayout) findViewById(R.id.gra_carton_layout);
		sl_summary = (Button) findViewById(R.id.sl_summary);
		sl_addProduct = (Button) findViewById(R.id.sl_addProduct);

		expand_layout= (LinearLayout) findViewById(R.id.expand_layout);

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
		price_txt = (TextView) findViewById(R.id.sales_pricetxt);
		sl_total_inclusive = (EditText) findViewById(R.id.sl_total_inclusive);
		sl_total = (EditText) findViewById(R.id.sl_total);
		sl_tax = (EditText) findViewById(R.id.sl_tax);
		sl_netTotal = (EditText) findViewById(R.id.sl_netTotal);

		sl_cprice = (EditText) findViewById(R.id.sl_cprice);

		sl_exchange = (EditText) findViewById(R.id.sl_exchange);
		productFilterList = (ListView) findViewById(R.id.productFilterList);
		txt_price= (TextView) findViewById(R.id.txt_price);
		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen = (TextView) findViewById(R.id.sum_screen);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);

		expand = (ImageView) findViewById(R.id.expand);
		sl_stock = (EditText) findViewById(R.id.sl_stock);
		gra_carton_header = (LinearLayout) findViewById(R.id.gra_carton_header);

		customer_layout.setVisibility(View.GONE);
		customer_screen.setText("Supplier");
//		addProduct_screen.setBackgroundColor(Color.parseColor("#00AFF0"));
		addProduct_screen.setTextColor(Color.parseColor("#FFFFFF"));
		addProduct_screen.setBackgroundResource(drawable.tab_select);
		
		// batchDialog = new BatchDialog(GraAddProduct.this);
		batchDialog = new BatchDialog();
		valid_url = FWMSSettingsDatabase.getUrl();
		new SalesProductWebService(valid_url);
		new NewProductWebService(valid_url);
		sl_itemDiscount.setRawInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);

		SOTDatabase.init(GraAddProduct.this);

		Log.d("GRATaxT", SalesOrderSetGet.getCompanytax());
		taxType = SalesOrderSetGet.getCompanytax();

		appType = LogOutSetGet.getApplicationType();
		calCarton = LogOutSetGet.getCalcCarton();
		mHashMap = new HashMap<String, String>();

		priceflag = SalesOrderSetGet.getCartonpriceflag();
		calCarton = LogOutSetGet.getCalcCarton();

		if (priceflag.matches("null") || priceflag.matches("")) {
			priceflag = "0";
		}

	// priceflag="0";
//		calCarton="1";
		/** Amount will Gone or Visible Based on Hide Price From GetUserPermission ***/
//		if(FormSetterGetter.getHidePrice()!=null && !FormSetterGetter.getHidePrice().isEmpty()){
//			if(FormSetterGetter.getHidePrice().matches("Hide Price")){
//				((LinearLayout) findViewById(R.id.total_txt_layout)).setVisibility(View.GONE);
//				((LinearLayout) findViewById(R.id.total_edt_layout)).setVisibility(View.GONE);
//				sl_price.setVisibility(View.INVISIBLE);
//				expand.setVisibility(View.INVISIBLE);
//				price_txt.setVisibility(View.INVISIBLE);
//				sl_cprice.setVisibility(View.GONE);
//				price_header_layout.setVisibility(View.GONE);
//			}else{
//				((LinearLayout) findViewById(R.id.total_txt_layout)).setVisibility(View.VISIBLE);
//				((LinearLayout) findViewById(R.id.total_edt_layout)).setVisibility(View.VISIBLE);
//				sl_price.setVisibility(View.VISIBLE);
//				expand.setVisibility(View.VISIBLE);
//				if (priceflag.matches("1")) {
//					sl_cprice.setVisibility(View.VISIBLE);
//					price_txt.setVisibility(View.GONE);
//					price_header_layout.setVisibility(View.VISIBLE);
//				} else {
//					sl_cprice.setVisibility(View.GONE);
//					price_txt.setVisibility(View.VISIBLE);
//					price_header_layout.setVisibility(View.GONE);
//				}
//			}
//		}else{
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
//		}

		if(appType.matches("W")){
			newprodbutton.setVisibility(View.VISIBLE);
		}else{
			newprodbutton.setVisibility(View.GONE);
		}
		
		cmpnyCode = SupplierSetterGetter.getCompanyCode();

		sl_namefield.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					productNameAlert();
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			gra_carton_header.setVisibility(View.VISIBLE);

			sales_prodCode = extras.getString("SOT_ssproductcode");

			ProductStockAsyncCall task = new ProductStockAsyncCall();
			task.execute();

			sl_codefield.setText(extras.getString("SOT_ssproductcode"));
			sl_namefield.setText(extras.getString("SOT_str_ssprodname"));
			
			sl_qty.setText(extras.getString("SOT_str_ssqty"));
			sl_cartonQty.setText(extras.getString("SOT_str_sscq"));
			sl_looseQty.setText(extras.getString("SOT_str_sslq"));
			
			sl_foc.setText(extras.getString("SOT_str_ssfoc"));
			sl_price.setText(extras.getString("SOT_str_ssprice"));
			sl_cprice.setText(extras.getString("SOT_str_sscprice"));
			sl_itemDiscount.setText(extras.getString("SOT_str_ssdisc"));
			sl_uom.setText(extras.getString("SOT_str_ssuom"));
			sl_cartonPerQty.setText(extras.getString("SOT_st_sscpqty"));
			sl_total.setText(extras.getString("SOT_st_sstotal"));

			String netTotal = extras.getString("SOT_st_ssnettot");
			String tax = extras.getString("SOT_st_sstax");

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
			taxValue = extras.getString("SOT_st_sstaxvalue");
			slCartonPerQty = extras.getString("SOT_st_sscpqty");
			MinimumSellingPrice = extras.getString("SOT_str_minselling_price");
			MinimumCartonSellingPrice = extras.getString("SOT_str_minselling_cartonprice");


            Log.d("MinimumSellingPrice", "->"+MinimumSellingPrice);
            Log.d("MiniCartonSellingPrice", "->"+MinimumCartonSellingPrice);

			sl_netTotal.setText(netTotal);
			str_sssno = extras.getString("SOT_ssno");
			str_ssupdate = extras.getString("SOT_ssupdate");
			str_sscancel = extras.getString("SOT_sscancel");
			if ((str_ssupdate != null) || (str_sscancel != null)) {
				sl_addProduct.setText(str_ssupdate);
				sl_summary.setText(str_sscancel);
			}
			
			if(calCarton.matches("0")){
				
			}else{
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
			
			//Edit product with batch
			   String pCode = extras.getString("SOT_ssproductcode");
			   Cursor pCursor = SOTDatabase.getBatchCursor(pCode);
			   int pCount = pCursor.getCount();
			   if(pCount>0){
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

		getSalesProdArr.clear();
		companyArr.clear();
		searchProductArr.clear();


		cstcode = SalesOrderSetGet.getSuppliercode();
		cstgrpcd = SalesOrderSetGet.getSuppliergroupcode();




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

		GetProduct proTask = new GetProduct();
		proTask.execute();
		/*if((FormSetterGetter.getHidePrice()!=null && !FormSetterGetter.getHidePrice().isEmpty()) &&
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
		}*/
		sl_cartonPerQty.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String productCode = sl_codefield.getText().toString();
				doubleClick(productCode);
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

						AsyncCallWSSearchProduct task = new AsyncCallWSSearchProduct();
						task.execute();
					}
				});

			}
		});
		
		newprodbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				AddProductSetterGetter.setUpdate(false);
				LogOutSetGet.setAddProduct("GraAddProduct");
				ClearFieldSetterGetter.setClearProduct(true);
				Intent i = new Intent(GraAddProduct.this,
						AndroidTabLayoutActivity.class);
				startActivity(i);
				GraAddProduct.this.finish();
			}
		});

		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Cursor cursor = SOTDatabase.getCursor();
				int count = cursor.getCount();
				if(count>0){
					alertDialog();
				}else{
					Intent i = new Intent(GraAddProduct.this, GraHeader.class);
					startActivity(i);
					GraAddProduct.this.finish();
				}
				
				

			}
		});

		customer_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(GraAddProduct.this, GraCustomer.class);
				startActivity(i);
				GraAddProduct.this.finish();

			}
		});

		summary_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(GraAddProduct.this, GraSummary.class);
				startActivity(i);
				GraAddProduct.this.finish();

			}
		});
		/*pricetag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(priceflag.matches("1")){
					new SOTAdmin(GraAddProduct.this, sl_price,sl_cprice,
							salesproduct_layout);
				}else{
					new SOTAdmin(GraAddProduct.this, sl_price,
							salesproduct_layout);
				}


			}
		});*/
		productFilterList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {

				sales_prodCode = searchProductArr.get(position).get("ProductCode");
				sl_codefield.setText(sales_prodCode);
			//	editCodeField();
				loadprogress();
				AsyncCallSaleProduct task = new AsyncCallSaleProduct("true");
				task.execute();
			}
		});

		sl_addProduct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if ((str_ssupdate != null) || (str_sscancel != null)) {
					Toast.makeText(GraAddProduct.this, "Updated ",
							Toast.LENGTH_LONG).show();
					storeInDatabase();
					Intent i = new Intent(GraAddProduct.this, GraSummary.class);
					startActivity(i);
					GraAddProduct.this.finish();
				} else {
					storeInDatabase();
				}

			//	Log.d("MinimumSellingPrice", MinimumSellingPrice);

			/*	if ((str_ssupdate != null) || (str_sscancel != null)) {

					double slprice = 0, minSellingPrice = 0,slcprice=0,miniCartonSellingPrice=0,piecePerCarton=0;
					String price = sl_price.getText().toString();
					String cprice = sl_cprice.getText().toString();

					if (!price.matches("")) {
						slprice = Double.parseDouble(price);
					}
					if (!MinimumSellingPrice.matches("")) {
						minSellingPrice = Double
								.parseDouble(MinimumSellingPrice);
					}
					if (slCartonPerQty!=null && !slCartonPerQty.isEmpty() ) {
						piecePerCarton = Double.parseDouble(slCartonPerQty);
					}
					if (!MinimumCartonSellingPrice.matches("")) {
						miniCartonSellingPrice = Double
								.parseDouble(MinimumCartonSellingPrice);
					}
					if (!cprice.matches("")) {
						slcprice = Double.parseDouble(cprice);
					}

					Log.d("slcprice", "cp "+slcprice);

					Log.d("priceflag", "cp "+priceflag);
					if (priceflag.matches("1")) {
						*//*if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
								|| slCartonPerQty.matches("")) {
							if (minSellingPrice > slcprice) {
								Toast.makeText(
										GraAddProduct.this,
										"Price must be greater than minimum selling price $ "
												+ MinimumSellingPrice + " pp" + slcprice,
										Toast.LENGTH_LONG).show();
							} else {
								storeInDatabase();
							}
						}else{
							storeInDatabase();
						}*//*


						if (miniCartonSellingPrice > slcprice) {

							Log.d("miniCartonSellingPrice", "if "+miniCartonSellingPrice);
							Log.d("slcprice", "if "+slcprice);

							sl_cprice.requestFocus();
							Toast.makeText(
									GraAddProduct.this,
									"Carton Price must be greater than Minimum carton selling price $ "
											+ MinimumCartonSellingPrice,
									Toast.LENGTH_LONG).show();
						} else if (piecePerCarton>1){
							Log.d("minSellingPrice", "else "+minSellingPrice);
							Log.d("slprice", "else "+slprice);
							if(minSellingPrice > slprice) {

								Log.d("minSellingPrice", "e >  "+minSellingPrice);
								Log.d("slprice", "e >   "+slprice);

								sl_price.requestFocus();
								Toast.makeText(
										GraAddProduct.this,
										"Price must be greater than minimum selling price $ "
												+ MinimumSellingPrice,
										Toast.LENGTH_LONG).show();
							}else{

								Log.d("minSellingPrice", "eeee "+minSellingPrice);
								Log.d("slprice", "eeee"+slprice);

								storeInDatabase();
								Toast.makeText(GraAddProduct.this, "Updated ",
										Toast.LENGTH_LONG).show();
								Intent i = new Intent(GraAddProduct.this,
										GraSummary.class);
								startActivity(i);
								GraAddProduct.this.finish();
							}
						}

						else {
							storeInDatabase();
							Toast.makeText(GraAddProduct.this, "Updated ",
									Toast.LENGTH_LONG).show();
							Intent i = new Intent(GraAddProduct.this,
									GraSummary.class);
							startActivity(i);
							GraAddProduct.this.finish();
						}

					} else {
						if (minSellingPrice > slprice) {
							Toast.makeText(
									GraAddProduct.this,
									"Price must be greater than minimum selling price $ "
											+ MinimumSellingPrice,
									Toast.LENGTH_LONG).show();
						} else {
							storeInDatabase();
							Toast.makeText(GraAddProduct.this, "Updated ",
									Toast.LENGTH_LONG).show();
							Intent i = new Intent(GraAddProduct.this,
									GraSummary.class);
							startActivity(i);
							GraAddProduct.this.finish();
						}
					}


				} else {
					double slprice = 0,piecePerCarton = 0, minSellingPrice = 0,miniCartonSellingPrice=0,slcprice=0;
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
					if (slCartonPerQty!=null && !slCartonPerQty.isEmpty() ) {
						piecePerCarton = Double.parseDouble(slCartonPerQty);
					}
					if (!MinimumCartonSellingPrice.matches("")) {
						miniCartonSellingPrice = Double
								.parseDouble(MinimumCartonSellingPrice);
					}
					if (priceflag.matches("1")) {

						if (miniCartonSellingPrice > slcprice) {
							sl_cprice.requestFocus();
							Toast.makeText(
									GraAddProduct.this,
									"Carton Price must be greater than Minimum carton selling price $ "
											+ MinimumCartonSellingPrice,
									Toast.LENGTH_LONG).show();
						} else if (piecePerCarton>1){
							if(minSellingPrice > slprice) {
								sl_price.requestFocus();
								Toast.makeText(
										GraAddProduct.this,
										"Price must be greater than minimum selling price $ "
												+ MinimumSellingPrice,
										Toast.LENGTH_LONG).show();
							}else{
								storeInDatabase();
							}
						}

						else {
							storeInDatabase();
						}

					} else {
						if (minSellingPrice > slprice) {
							Toast.makeText(
									GraAddProduct.this,
									"Price must be greater than minimum selling price $ "
											+ MinimumSellingPrice,
									Toast.LENGTH_LONG).show();
						} else {
							storeInDatabase();

						}
					}
				}*/


			}
		});

		sl_summary.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent i = new Intent(GraAddProduct.this, GraSummary.class);
				startActivity(i);
				GraAddProduct.this.finish();
			}
		});

		expand.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Log.d("expandcheck","ff "+expandcheck);
//				if (foc_layout.getVisibility() == View.VISIBLE) {
				if (expandcheck == true) {
					expandcheck = false;
					// Its visible
//					foc_layout.setVisibility(View.GONE);
//					foc_header_layout.setVisibility(View.GONE);
//					pcs_txt_layout.setVisibility(View.GONE);
//					pcs_layout.setVisibility(View.GONE);
					Log.d("expand cllck","GONE");
					expand_layout.setVisibility(View.GONE);
				} else {
					expandcheck = true;
					expand_layout.setVisibility(View.VISIBLE);
					Log.d("expand cllck","VISIBLE");
//					foc_layout.setVisibility(View.VISIBLE);
//					foc_header_layout.setVisibility(View.VISIBLE);
//					pcs_txt_layout.setVisibility(View.VISIBLE);
//					pcs_layout.setVisibility(View.VISIBLE);
					// Either gone or invisible
				}

			}
		});

		sl_codefield
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
				if (actionId == EditorInfo.IME_ACTION_NEXT || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					// TODO Auto-generated method stub
					
					CheckProductBarcode task = new CheckProductBarcode();
					task.execute();

				//	editCodeField();
					return true;
				}
				return false;
			}
		});

	/*	sl_cartonQty.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					slPrice = sl_price.getText().toString();
					
					if(calCarton.matches("0")){
						cartonQtyPcsOne(0);
						int length = sl_cartonQty.length();
						if (length == 0) {
								cartonQtyPcsOne(0);				
							}
					}else{
						cartonQty();
					}
					
					sl_looseQty.requestFocus();
					return true;
				}
				return false;
			}
		});
*/
		/*cqtyTW = new TextWatcher() {

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
				if(calCarton.matches("0")){
					cartonQtyPcsOne(0);
					int length = sl_cartonQty.length();
					if (length == 0) {
							cartonQtyPcsOne(0);				
						}
				}else{
				cartonQty();
				}
				
				int length = sl_cartonQty.length();
				if (length == 0) {
					if(calCarton.matches("0")){
						
					}else{
					String lqty = sl_looseQty.getText().toString();
					if(lqty.matches("")){
						lqty="0";
				       }
					
					if (!lqty.matches("")) {
						sl_qty.removeTextChangedListener(qtyTW);
						sl_qty.setText(lqty);
						sl_qty.addTextChangedListener(qtyTW);

						if (sl_qty.length() != 0) {
							sl_qty.setSelection(sl_qty.length());
						}
						double lsQty = Double.parseDouble(lqty);

						productTotal(lsQty);
					}
					}

				}
			}

		};
*/

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

//		sl_looseQty.setOnEditorActionListener(new OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_NEXT) {
//					slPrice = sl_price.getText().toString();
//					if(calCarton.matches("0")){
//						looseQtyCalcPcsOne(0);
//					}else{
//					looseQtyCalc();
//					}
//					sl_qty.requestFocus();
//					return true;
//				}
//				return false;
//			}
//		});
//
//		lqtyTW = new TextWatcher() {
//
//			@Override
//			public void afterTextChanged(Editable s) {
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				beforeLooseQty = sl_looseQty.getText().toString();
//
//			}
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				slPrice = sl_price.getText().toString();
//				if(calCarton.matches("0")){
//					looseQtyCalcPcsOne(0);
//					int length = sl_looseQty.length();
//					if (length == 0) {
//						looseQtyCalcPcsOne(0);
//						}
//				}else{
//				looseQtyCalc();
//				}
//
//				int length = sl_looseQty.length();
//				if (length == 0) {
//					if(calCarton.matches("0")){
//
//					}else{
//					/*String qty = sl_qty.getText().toString();
//					if (!beforeLooseQty.matches("") && !qty.matches("")) {
//
//						int qtyCnvrt = Integer.parseInt(qty);
//						int lsCnvrt = Integer.parseInt(beforeLooseQty);
//
//						sl_qty.removeTextChangedListener(qtyTW);
//						sl_qty.setText("" + (qtyCnvrt - lsCnvrt));
//						sl_qty.addTextChangedListener(qtyTW);
//
//						if (sl_qty.length() != 0) {
//							sl_qty.setSelection(sl_qty.length());
//						}
//						if(calCarton.matches("0")){
//
//						}else{
//						looseQtyCalc();
//						}
//					}*/
//
//
//						String qty = sl_qty.getText().toString();
//						if (!beforeLooseQty.matches("") && !qty.matches("")) {
//
//							double qtyCnvrt = Double.parseDouble(qty);
//							double lsCnvrt = Double.parseDouble(beforeLooseQty);
//							sl_qty.removeTextChangedListener(qtyTW);
//							sl_qty.setText("" + (qtyCnvrt - lsCnvrt));
//							sl_qty.addTextChangedListener(qtyTW);
//
//							if (sl_qty.length() != 0) {
//								sl_qty.setSelection(sl_qty.length());
//							}
//
//							if(calCarton.matches("0")){
//
//							}else{
//							looseQtyCalc();
//							}
//						}
//					}
//				}
//			}
//
//		};

	/*	sl_qty.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {

					slPrice = sl_price.getText().toString();
					String qty = sl_qty.getText().toString();
					if (!qty.matches("")) {
						double qtyCalc = Double.parseDouble(qty);
						if(calCarton.matches("0")){
							
						}else{
						clQty();
						}
						productTotal(qtyCalc);
					}

					sl_foc.requestFocus();
					return true;
				}
				return false;
			}
		});
*/
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
		/*qtyTW = new TextWatcher() {

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
				if (!qty.matches("")) {
					double qtyCalc = Double.parseDouble(qty);
					if(calCarton.matches("0")){
						
					}else{
					clQty();
					}
					productTotal(qtyCalc);
				}

				int length = sl_qty.length();
				if (length == 0) {
					productTotal(0);

					if(calCarton.matches("0")){
						
					}else{
					sl_cartonQty.removeTextChangedListener(cqtyTW);
					sl_cartonQty.setText("");
					sl_cartonQty.addTextChangedListener(cqtyTW);

					sl_looseQty.removeTextChangedListener(lqtyTW);
					sl_looseQty.setText("");
					sl_looseQty.addTextChangedListener(lqtyTW);
					}
				}
			}

		};
*/
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
		/*sl_foc.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					if (sl_price.getText().toString().equals("0.00")
							|| sl_price.getText().toString().equals("0")
							|| sl_price.getText().toString().equals("0.0")
							|| sl_price.getText().toString().equals(".0")
							|| sl_price.getText().toString().equals("0.000")
							|| sl_price.getText().toString().equals("0.0000")
							|| sl_price.getText().toString().equals("0.00000")) {
						sl_price.setText("");
					}
					sl_price.requestFocus();
					return true;
				}
				return false;
			}
		});
*/
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

		/*sl_price.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {

					String qty = sl_qty.getText().toString();
					if (!qty.matches("")) {
						double qtyCalc = Double.parseDouble(qty);
						slPrice = sl_price.getText().toString();
						productTotal(qtyCalc);
					}
					sl_itemDiscount.requestFocus();
					return true;
				}
				return false;
			}
		});*/
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
					//productTotal(qtyCalc);
					if (priceflag.matches("0")) {
						productTotal(qtyCalc);
					} else if (priceflag.matches("1")) {
						productTotalNew();
					}
				}
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

				//slPrice = sl_price.getText().toString();
				//itemDiscountCalc();
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
	public void doubleClick(String productCode) {
		if (doubleClick) {
			String pcsctn = sl_cartonPerQty.getText().toString();
			mDialogPcsPerCarton = new DialogPcsPerCarton(GraAddProduct.this, salesproduct_layout);
			mDialogPcsPerCarton.pcsDialog(productCode,pcsctn);
			mDialogPcsPerCarton.OnCompleteListener(new DialogPcsPerCarton.OnCompleteListener() {
				@Override
				public void OnCompleted(boolean result,String pcspercarton) {
					Log.d("bResult", "-->"+result);
					Log.d("pcspercarton", "-->"+pcspercarton);
					if(result){
						int pcs = 0;
						if(pcspercarton!=null && !pcspercarton.isEmpty()){
							pcs = Integer.valueOf(pcspercarton);
						}
						if(pcs>1){

							sl_cartonQty.setFocusableInTouchMode(true);
							sl_cartonQty.setBackgroundResource(drawable.edittext_bg);
							sl_looseQty.setFocusableInTouchMode(true);
							sl_looseQty.setBackgroundResource(drawable.edittext_bg);
							txt_price.setVisibility(View.VISIBLE);
							sl_price.setVisibility(View.VISIBLE);
							sl_cartonQty.setFocusable(true);
							sl_cartonQty.requestFocus();
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
						}else{
							sl_cartonQty.setFocusable(false);
							sl_cartonQty.setBackgroundResource(drawable.labelbg);
							sl_looseQty.setFocusable(false);
							sl_looseQty.setBackgroundResource(drawable.labelbg);
							txt_price.setVisibility(View.INVISIBLE);
							sl_price.setVisibility(View.INVISIBLE);
							sl_qty.setFocusable(true);
							sl_qty.requestFocus();
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
						}
						sl_cartonPerQty.setText(pcspercarton);
						sl_cartonQty.setText("");
						sl_looseQty.setText("");
						slCartonPerQty = pcspercarton;

					}else{
						Toast.makeText(GraAddProduct.this,getResources().getString(R.string.update_pcs),Toast.LENGTH_SHORT).show();
					}
				}
			});
			return;
		}
		this.doubleClick = true;
		/*if(context!=null){
			Toast.makeText(context, "Click back again to exit", Toast.LENGTH_SHORT)
					.show();
		}else{
			Toast.makeText(this, "Click back again to exit", Toast.LENGTH_SHORT)
					.show();
		}*/
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleClick = false;
			}
		}, 2000);
	}
	private void updatePcsPerCartonDialog(){

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
	}

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
	}*/
	
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

			//	productTotal(qty);
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

			//	productTotal(qty);
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

				//productTotal(qty);
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
									
					//productTotal(qty_nt);
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

//						netTotal = tt + taxAmount;
						netTotal = tt ;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);

						double dTotalIncl = netTotal - taxAmount;
						String totalIncl = twoDecimalPoint(dTotalIncl);
						Log.d("totalIncl", "" + totalIncl);
						sl_total_inclusive.setText(totalIncl);

					} else if (taxType.matches("Z")) {

						sl_tax.setText("0.0");

//						netTotal = tt + taxAmount;
						netTotal = tt;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);

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

//							netTotal1 = subTotal + taxAmount;
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

//							netTotal = tt + taxAmount;
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
//							netTotal1 = subTotal + taxAmount;
							netTotal1 = subTotal;
							String ProdNetTotal = twoDecimalPoint(netTotal1);
							sl_netTotal.setText("" + ProdNetTotal);
						} else {
//							netTotal = tt + taxAmount;
							netTotal = tt;
							String ProdNetTotal = twoDecimalPoint(netTotal);
							sl_netTotal.setText("" + ProdNetTotal);
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
		String exQtyStr = sl_exchange.getText().toString();
		String cpriceStr = sl_cprice.getText().toString();

		if (codeStr.matches("")) {
			Toast.makeText(GraAddProduct.this, "Select product code",
					Toast.LENGTH_SHORT).show();
		} else if (calCarton.matches("1") && qtyStr.matches("")
				&& focStr.matches("") && exQtyStr.matches("")) {
			Toast.makeText(GraAddProduct.this, "Enter the quantity",
					Toast.LENGTH_SHORT).show();

		} else if (calCarton.matches("0") && cartonQtyStr.matches("")
				&& looseQtyStr.matches("") && qtyStr.matches("")) {
			Toast.makeText(GraAddProduct.this, "Enter the carton/quantity",
					Toast.LENGTH_SHORT).show();
		}else {

			if(!searchProductArr.isEmpty()){
				carton_layout.setVisibility(View.GONE);
			}
			
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
			if (!priceStr.matches("")) {
				price = Double.parseDouble(priceStr);
			}
			if (cpriceStr.matches("")) {
				cpriceStr = "0.00";
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

				Log.d("total gra","gr "+total);

				sbTtl = twoDecimalPoint(subTotal);

				if ((str_ssupdate != null) || (str_sscancel != null)) {
					int i_sssno = Integer.parseInt(str_sssno);
					SOTDatabase.updateBillDisc(i_sssno, codeStr, sbTtl);
					Log.d("updateBillDisc","updateBillDisc "+i_sssno);
				} else {
					SOTDatabase.storeBillDisc(sl_no, codeStr, sbTtl);
					Log.d("storeBillDisc","storeBillDisc " +sl_no);
				}
			}
			if (!taxStr.matches("")) {
				tax = Double.parseDouble(taxStr);
			}
			if (!netTotalStr.matches("")) {
				if(taxType!=null && !taxType.isEmpty()){
					if (taxType.matches("I") || taxType.matches("Z")) {
						ntTot = subTotal;
					}else{
						ntTot = subTotal + tax;
					}
				}else{
					ntTot = subTotal + tax;
				}
				netT = twoDecimalPoint(ntTot);
			}
			if (newPrice.matches("") || newPrice == null) {
				newPrice = sl_price.getText().toString();
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
//			Log.d("total" + tt, totl);
			double dis = 0.0;
			if (!dicountStr.matches("")) {
				dis = Double.parseDouble(dicountStr);
			}

			if ((str_ssupdate != null) || (str_sscancel != null)) {
				int i_sssno = Integer.parseInt(str_sssno);

				SOTDatabase.updateProduct(codeStr, nameStr, cartonQty,
						looseQty, qty, foc, price, discount, uomStr,
						cartonPerQty, tt + dis, tax, sbTtl, netT, i_sssno,
						cpriceStr, "0");

			} else {

				Log.d("Store gra" ,"Store gra");

				String productid = SOTDatabase.getProductId(codeStr);
				
//				if (productid != null && !productid.isEmpty()) {
//
//					int i_sssno = Integer.parseInt(productid);
//					SOTDatabase.updateProduct(codeStr, nameStr, cartonQty,
//							looseQty, qty, foc, price, discount, uomStr,
//							cartonPerQty, tt + dis, tax, sbTtl, netT, i_sssno,
//							cpriceStr, "0");
//
//				} else {
					Log.d("Store" ,"Store final");
					/*SOTDatabase.storeProduct(sl_no, codeStr, nameStr, cartonQty,
							looseQty, qtyStr, foc, price, discount, uomStr,
							cartonPerQty, tt + dis, tax, netT, taxType, taxValue,
							newPrice, sbTtl, cpriceStr, "0", MinimumSellingPrice, "", "", "",MinimumCartonSellingPrice);*/

					SOTDatabase.storeProduct(sl_no, codeStr, nameStr,
							cartonQty, looseQty, qty, foc, price, discount,
							uomStr, cartonPerQty, tt + dis, tax, netT, taxType,
							taxValue, newPrice, sbTtl, cpriceStr, exQtyStr,
							MinimumSellingPrice, "", "", "",MinimumCartonSellingPrice,"","");
//				}
	
				clearData();

			}
			
			if (!searchProductArr.isEmpty()) {
			      adapter.notifyDataSetChanged();
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
		sl_cprice.setText("");
		sl_price.setText("");
		sl_uom.setText("");
		sl_stock.setText("");
		sl_cartonPerQty.setText("");
		sl_exchange.setText("");
		sl_codefield.requestFocus();

		sl_cartonQty.setEnabled(true);
		sl_cartonQty.setFocusable(true);
		sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

		sl_looseQty.setEnabled(true);
		sl_looseQty.setFocusable(true);
		sl_looseQty.setBackgroundResource(drawable.edittext_bg);

		sl_qty.setEnabled(true);
		sl_qty.setFocusable(true);
		sl_qty.setBackgroundResource(drawable.edittext_bg);

		sl_foc.setEnabled(true);
		sl_foc.setFocusable(true);
		sl_foc.setBackgroundResource(drawable.edittext_bg);
	}

	/*public void editCodeField() {

		if (sl_codefield.getText().toString() != ""
				&& sl_codefield.getText().length() != 0) {

			Log.d("alprodcode", alprodcode.toString());
			Log.d("albar", albar.toString());
			
			ArrayList<String> mergeResult = new ArrayList<String>();
			mergeResult.addAll(alprodcode);
			mergeResult.addAll(albar);
			boolean res = false;
			for (String alphabet : mergeResult) {
				Log.d("alphabet", alphabet);
				
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
				
				
			loop:for (int i = 0; i < productwithBarcodeArrHm.size(); i++) {					
					String productCode = productwithBarcodeArrHm.get(i).get("ProductCode");
					String productName = productwithBarcodeArrHm.get(i).get("ProductName");					
					String barCode = productwithBarcodeArrHm.get(i).get("BarCode");	
					if (sl_codefield.getText().toString().toLowerCase()
							.matches(productCode.toLowerCase())||sl_codefield.getText().toString().toLowerCase()
							.matches(barCode.toLowerCase())) {
						sl_codefield.setText(productCode);
						sl_namefield.setText(productName);
						sales_prodCode = productCode;
						break loop;
						
					}
					
				
				}
				

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
								
								Cursor tcursor = SOTDatabase.getCursor();
						        int tslno = tcursor.getCount()+1;
						        Log.d("tslno", ":"+tslno);       
						        SOTDatabase.deleteBatchSno(tslno+"");
								
								sales_prodCode = keybar;
							}
						}
					}
				}
				loadprogress();
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
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				GraAddProduct.this);
		editText = new EditText(GraAddProduct.this);
		final ListView listview = new ListView(GraAddProduct.this);
		LinearLayout layout = new LinearLayout(GraAddProduct.this);
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

		arrayAdapterProd = new CustomAlertAdapterProd(GraAddProduct.this, al);
		listview.setAdapter(arrayAdapterProd);
		listview.setOnItemClickListener(GraAddProduct.this);

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
						GraAddProduct.this, searchResults);
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
			
			Cursor tcursor = SOTDatabase.getCursor();
	        int tslno = tcursor.getCount()+1;
	        Log.d("tslno", ":"+tslno);       
	        SOTDatabase.deleteBatchSno(tslno+"");
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
					sl_stock.setText("");
					sl_cartonPerQty.setText("");
				}
			});
		}
	}
	
	
private class CheckProductBarcode extends AsyncTask<Void, Void, Void> {

	String ed_code="";
	@SuppressWarnings("deprecation")
	@Override
	protected void onPreExecute() {
//		getActionBar().setHomeButtonEnabled(false);
//		menu.setSlidingEnabled(false);
		/*spinnerLayout = new LinearLayout(GraAddProduct.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(salesproduct_layout, false);
		progressBar = new ProgressBar(GraAddProduct.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		spinnerLayout.addView(progressBar);*/
		ed_code = sl_codefield.getText().toString();
		mBarcodeJsonString="";
		loadprogress();
	}

	@Override
	protected Void doInBackground(Void... arg0) {		

		mHashMap.clear();
		mHashMap.put("CompanyCode", cmpnyCode);		
		mHashMap.put("Barcode",ed_code);
	
		try {
			mBarcodeJsonString = SalesOrderWebService.getSODetail(mHashMap, webMethNamebar);
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
			AsyncCallSaleProduct task = new AsyncCallSaleProduct("false");
			task.execute();
		}else{
			sales_prodCode = ed_code;
			AsyncCallSaleProduct task = new AsyncCallSaleProduct("false");
			task.execute();
		}
		
	}


}
	private class GetProduct extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			al.clear();
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
		
			mHashMap.put("CompanyCode", cmpnyCode);
			try {
				
				mProductJsonString = SalesOrderWebService.getSODetail(mHashMap, webMethNameSearch);
				mProductJsonObject = new JSONObject(mProductJsonString);
				mProductJsonArray = mProductJsonObject.optJSONArray("SODetails");

				int lengthJsonProductArr = mProductJsonArray.length();
					if (lengthJsonProductArr > 0) {	
					for (int i = 0; i < lengthJsonProductArr; i++) {

						JSONObject jsonChildNode = mProductJsonArray.getJSONObject(i);

						String productcodes = jsonChildNode.optString(
								PRODUCT_CODE).toString();
						String productnames = jsonChildNode.optString(
								PRODUCT_NAME).toString();

						HashMap<String, String> producthm = new HashMap<String, String>();
						producthm.put(productcodes, productnames);
						al.add(producthm);

					}
					}

				} catch (JSONException e) {

					e.printStackTrace();
				}
			
			/*

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
				productTxt = response.toString();
				productresult = " { ProductDetails : " + productTxt + "}";
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
						
						HashMap<String, String> hmproduct = new HashMap<String, String>();
						hmproduct.put("ProductCode", productcodes);
						hmproduct.put("ProductName", productnames);
						productArrHm.add(hmproduct);
						
						
						hashmap.putAll(hmproduct);
						
						
						
						HashMap<String, String> productsplithm = new HashMap<String, String>();
						albarsplit.add(productsplithm);
						hmsplitbc.putAll(productsplithm);
						alprodcode.add(productcodes);

					}

				} catch (JSONException e) {

					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();

			}

			return null;
		*/
			return null;
			}

		@Override
		protected void onPostExecute(Void result) {

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);
//			getActionBar().setHomeButtonEnabled(true);
//			menu.setSlidingEnabled(true);

		}

	}

/*	private class AsyncCallBARCODE extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {

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
			HttpTransportSE androidHttpTransport = new HttpTransportSE(
					valid_url);
			try {

				androidHttpTransport.call(SOAP_ACTION + webMethNamebar,
						envelope);
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				barcodeTxt = response.toString();
				barcoderesult = " { BarCodeDetails : " + barcodeTxt + "}";

				JSONObject jsonResponse;
				try {

					jsonResponse = new JSONObject(barcoderesult);

					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("BarCodeDetails");

					int lengthJsonArr = jsonMainNode.length();

					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);

						String productbarcode = jsonChildNode.optString(
								PRODUCTNAME_BARCODE).toString();
						String barcode = jsonChildNode.optString(
								PRODUCT_BARCODE).toString();
						
						
						
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("ProductCode", productbarcode);
						hm.put("BarCode", barcode);
						barcodeArrHm.add(hm);
						String productCode = hashmap.get(productbarcode);
						
					//	loop: for(int j=0;j<productArrHm.size();j++){
						//	String productCode = productArrHm.get(i).get("ProductCode");
						//	String productName = productArrHm.get(i).get("ProductName");
						//	loop:for(int j=0;j<barcodeArrHm.size();j++){			
							//	String productBarCode = barcodeArrHm.get(j).get("ProductCode");	
							//	String barCode = barcodeArrHm.get(j).get("BarCode");	
							//	if(productCode.matches(productbarcode)){
									HashMap<String, String> productwithBarcodehm = new HashMap<String, String>();
									productwithBarcodehm.put("ProductCode", productbarcode);
									productwithBarcodehm.put("ProductName", productCode);
									productwithBarcodehm.put("BarCode", barcode);
									productwithBarcodeArrHm.add(productwithBarcodehm);
								//	break loop;
							//	}
								
							//}
						//}
						
						
						HashMap<String, String> barcodehm = new HashMap<String, String>();
						barcodehm.put(productbarcode, barcode);
						albarcode.add(barcodehm);
						albar.add(barcode);

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
		//	mergeArrHm();

		}
	}*/
/*private void mergeArrHm(){
	try{
		for(int i=0;i<productArrHm.size();i++){
			String productCode = productArrHm.get(i).get("ProductCode");
			String productName = productArrHm.get(i).get("ProductName");
			loop:for(int j=0;j<barcodeArrHm.size();j++){			
				String productBarCode = barcodeArrHm.get(j).get("ProductCode");	
				String barCode = barcodeArrHm.get(j).get("BarCode");	
				if(productCode.matches(productBarCode)){
					HashMap<String, String> productwithBarcodehm = new HashMap<String, String>();
					productwithBarcodehm.put("ProductCode", productCode);
					productwithBarcodehm.put("ProductName", productName);
					productwithBarcodehm.put("BarCode", barCode);
					productwithBarcodeArrHm.add(productwithBarcodehm);
					break loop;
				}
				
			}
		}
	}catch(Exception e ){
		e.printStackTrace();
	}
	
}*/
	private class AsyncCallSaleProduct extends AsyncTask<Void, Void, Void> {

		String filterClick="",codefield="",namefield="";
		public AsyncCallSaleProduct(String fromFilterClick){
			filterClick = fromFilterClick;
		}
		@Override
		protected void onPreExecute() {
			priceArr.clear();
			getSalesProdArr.clear();
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
			sl_cprice.setText("");
			sl_cartonPerQty.setText("");
			sl_total.setText("0");
			sl_total_inclusive.setText("0");
			sl_tax.setText("0");
			sl_netTotal.setText("0");
		
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			getSalesProdArr = SalesProductWebService.getGraProduct(sales_prodCode, webMethName);

			priceArr = SalesProductWebService.getSuppProductPrice(cstgrpcd,
					cstcode, sales_prodCode, "fncGetProductPriceForPurchase");

			newPrice = priceArr.get(0);
			newcprice = priceArr.get(1);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			ProductStockAsyncCall task = new ProductStockAsyncCall();
			task.execute();

			carton_layout.setVisibility(View.VISIBLE);
			if (!searchProductArr.isEmpty()) {
				grid_layout.setVisibility(View.VISIBLE);
			}

			if (!getSalesProdArr.isEmpty()) {
				Log.d("getSalesProdArr", getSalesProdArr.toString());
				Log.d("purchasePrice", newPrice);
				slPrice = getSalesProdArr.get(0);
				slUomCode = getSalesProdArr.get(1);
				slCartonPerQty = getSalesProdArr.get(2);
//				taxValue = SalesOrderSetGet.getTaxValue();
				taxValue = getSalesProdArr.get(3);
				String haveBatch = getSalesProdArr.get(4);
				String haveExpiry = getSalesProdArr.get(5);
				codefield = getSalesProdArr.get(6);
				namefield = getSalesProdArr.get(7);
				Weight = getSalesProdArr.get(8);

				cprice = getSalesProdArr.get(9);
				MinimumSellingPrice = getSalesProdArr.get(10);
				MinimumCartonSellingPrice = getSalesProdArr.get(11);

				Log.d("MinimumSellingPrice","-->"+ MinimumSellingPrice);
				Log.d("MiniCartonSellingPrice","-"+ MinimumCartonSellingPrice);

				sl_codefield.setText(codefield);
				sl_namefield.setText(namefield);
			/*	if (FormSetterGetter.isEditPrice()) {
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
				}*/
				Log.d("haveBatch", haveBatch);
				Log.d("haveExpiry", haveExpiry);

				HashMap<String, EditText> hm = new HashMap<String, EditText>();
				
				hm.put("Productcode", sl_codefield);
				hm.put("Productname", sl_namefield);
				hm.put("Cartonqty", sl_cartonQty);
				hm.put("Looseqty", sl_looseQty);
				hm.put("Qty", sl_qty);
				hm.put("Foc", sl_foc);
				hm.put("Uom", sl_uom);
				hm.put("Stock", sl_stock);
				hm.put("Cartonperqty", sl_cartonPerQty);
				hm.put("Price", sl_price);
							
				String haveBatchOnStockIn = SalesOrderSetGet.getHaveBatchOnStockIn();
				if (haveBatchOnStockIn.matches("True")) {
					Log.d("haveBatchOnStockIn", haveBatchOnStockIn);
					if (haveBatch.matches("True") || haveExpiry.matches("True")) {
						Log.d("haveBatch", "haveExpiry");
						String code = sl_codefield.getText().toString();
						String name = sl_namefield.getText().toString();
						batchDialog.initiateBatchPopupWindow(
								GraAddProduct.this, haveBatch, haveExpiry,
								code, name, slCartonPerQty,slPrice, hm);
						noBatchvalue();
					} else {
						Log.d("no haveBatch", "no haveExpiry");
						noBatchvalue();
					}

				} else {
					Log.d("no haveBatchOnStockIn", "no haveBatchOnStockIn");
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

			/*String productCode = SOTDatabase.getProductCode(sales_prodCode);
			if (productCode != null && !productCode.isEmpty()) {
				Toast.makeText(GraAddProduct.this,
						"This product is already added",
						Toast.LENGTH_SHORT).show();

				clearData();

			}*/



			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);

		}
	}

	private class AsyncCallWSSearchProduct extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
		/*	spinnerLayout = new LinearLayout(GraAddProduct.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesproduct_layout, false);
			progressBar = new ProgressBar(GraAddProduct.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);*/
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			searchProductArr = NewProductWebService.searchProduct(catStr,
					subCatStr, "fncGetProduct");

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

	public void searchProductList() throws ParseException {

		adapter = new AddSimpleAdapter(GraAddProduct.this,
				searchProductArr, R.layout.sale_productitem,"", new String[] {
						"ProductCode", "ProductName", "WholeSalePrice" },
				new int[] { R.id.txt_code, R.id.txt_name, R.id.txt_price });
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

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			cmpnyCode = SupplierSetterGetter.getCompanyCode();
			LocationCode = SalesOrderSetGet.getLocationcode();

			producthashValue.put("CompanyCode", cmpnyCode);
			producthashValue.put("LocationCode", LocationCode);
			producthashValue.put("ProductCode", sales_prodCode);
			product_stock_jsonString = WebServiceClass.parameterService(
					producthashValue, "fncGetProductWithStock");

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

					Log.d("stock qty", stock);

				}
			} catch (JSONException e) {

				e.printStackTrace();
			} catch (Exception e) {

			}

			/******* Fetch node values **********/

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			sl_stock.setText(" " + stock);
		}
	}

	public void noBatchvalue() {

		if (!newPrice.matches("")) {
			sl_price.setText(newPrice);
		} else if (newPrice.matches("0.00") || newPrice.matches("0")) {

			sl_price.setText(slPrice);
		} else {
			Log.d("price", slPrice);
			sl_price.setText(slPrice);
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

			if(calCarton.matches("0")){
				sl_cartonQty.requestFocus();
				
				sl_cartonQty.setFocusableInTouchMode(true);
				sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

				sl_looseQty.setFocusableInTouchMode(true);
				sl_looseQty.setBackgroundResource(drawable.edittext_bg);

				  sl_qty.setFocusableInTouchMode(true);
				  sl_qty.setBackgroundResource(drawable.edittext_bg);

				  sl_foc.setFocusableInTouchMode(true);
				  sl_foc.setBackgroundResource(drawable.edittext_bg);
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(sl_cartonQty, InputMethodManager.SHOW_IMPLICIT);	
			}else{
			
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

//		sl_cartonQty.requestFocus();
		
	}
	
	private void loadprogress(){
//		getActionBar().setHomeButtonEnabled(false);
//		menu.setSlidingEnabled(false);
		spinnerLayout = new LinearLayout(GraAddProduct.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(salesproduct_layout, false);
		progressBar = new ProgressBar(GraAddProduct.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		spinnerLayout.addView(progressBar);
	}

	@Override
	public void onBackPressed() {
		if ((str_ssupdate != null) || (str_sscancel != null)) {
			Intent i = new Intent(GraAddProduct.this, GraSummary.class);
			startActivity(i);
			GraAddProduct.this.finish();
		} else {
			Intent i = new Intent(GraAddProduct.this, GraCustomer.class);
			startActivity(i);
			GraAddProduct.this.finish();
		}
	}

	@Override
	public void onListItemClick(String item) {
		menu.toggle();
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
							Intent i = new Intent(GraAddProduct.this, GraHeader.class);
							startActivity(i);
							GraAddProduct.this.finish();
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
}