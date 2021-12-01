package com.winapp.sot;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.winapp.fwms.CustomAlertAdapterProd;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.NewProductWebService;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.sot.FilterCS.OnFilterCompletionListener;

public class StockAdjustmentAddproduct extends SherlockFragmentActivity implements
SlideMenuFragment.MenuClickInterFace, OnItemClickListener {
	
	private static String webMethName = "fncGetProduct";
	private static String webMethNameSearch = "fncGetProductForSearch";
	private static String webMethNamebar = "fncGetProductBarCode";
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String PRODUCT_NAME = "ProductName";
	private static final String PRODUCTNAME_BARCODE = "ProductCode";
	private static final String PRODUCT_BARCODE = "Barcode";
	private String mProductJsonString ="",mBarcodeJsonString="";
	private JSONObject mProductJsonObject =null,mBarcodeJsonObject=null;
	private JSONArray mProductJsonArray=null,mBarcodeJsonArray=null;
	private HashMap<String, String> mHashMap;
	SlidingMenu menu;
	TextView textView1;
	EditText editText1,stadj_stockinhand;
	ArrayList<HashMap<String, String>> searchProductArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> productStockArr = new ArrayList<HashMap<String, String>>();
	
	Button sl_addProduct,stadj_plus, stadj_minus;
	EditText sl_codefield, sl_namefield, sl_cartonQty, sl_looseQty, sl_qty,
			sl_uom, sl_cartonPerQty, sl_total, sl_tax, sl_netTotal;
	AlertDialog myalertDialog = null;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	EditText editText;
	CustomAlertAdapterProd arrayAdapterProd;
	ArrayList<HashMap<String, String>> searchResults;
	int textlength = 0;
	ArrayList<HashMap<String, String>> getArraylist = new ArrayList<HashMap<String, String>>();
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	LinearLayout stAdjustproduct_layout,pcs_txt_layout,pcs_layout,customer_layout;
	String valid_url, productTxt, productresult, barcoderesult, barcodeTxt;
	String slPrice = "", slUomCode = "", slCartonPerQty = "", haveBatch = "", haveExpiry  ="";
	ArrayList<String> getSalesProdArr = new ArrayList<String>();
	String keyValues = "", values = "";
	static ArrayList<String> companyArr = new ArrayList<String>();
	String taxType = "", taxValue = "", sales_prodCode;
	Cursor cursor;
	String beforeLooseQty, beforeFoc;
	ListView productFilterList;
	String catStr = "", subCatStr = "", cstcode = "", cstgrpcd = "";
	String ss_Cqty = "";
	int sl_no = 1;
	double netTtal = 0, taxAmount = 0;
	Bundle extra;
	String str_ssupdate, str_sscancel, str_sssno;
	double tt;
	TextWatcher cqtyTW,lqtyTW,qtyTW; 
	//String adddetect="";
	private  FilterCS filtercs;
	HashMap<String, String> producthashValue = new HashMap<String, String>();
	String product_stock_jsonString = null;
	JSONObject product_stock_jsonResponse;
	JSONArray product_stock_jsonMainNode;
	String cmpnyCode="",LocationCode="",stock="",calCarton="",Weight="";
	ImageView expand;
	TextView listing_screen, customer_screen, addProduct_screen,summary_screen;
//	View customer_view;
	BatchDialog batchDialog;
	TransferBatchDialog transferBatchDialog;
	SimpleAdapter adapter;
	
	ArrayList<HashMap<String, String>> alBatchStock = new ArrayList<HashMap<String, String>>();
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.stockadjustment_addproduct);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.addproduct_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		
		ImageButton filter = (ImageButton) customNav
			    .findViewById(R.id.filter);
		txt.setText("Add Product");
		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);

		ab.setDisplayHomeAsUpEnabled(false);
		menu = new SlidingMenu(StockAdjustmentAddproduct.this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		// menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);

		filtercs = new FilterCS(StockAdjustmentAddproduct.this);
		
		stAdjustproduct_layout = (LinearLayout) findViewById(R.id.stAdjustproduct_layout);
	
		sl_addProduct = (Button) findViewById(R.id.stadj_addProduct);

		pcs_txt_layout = (LinearLayout) findViewById(R.id.pcs_txt_layout);
		pcs_layout = (LinearLayout) findViewById(R.id.pcs_layout);
		
		sl_codefield = (EditText) findViewById(R.id.stadj_codefield);
		sl_namefield = (EditText) findViewById(R.id.stadj_namefield);
		sl_cartonQty = (EditText) findViewById(R.id.stadj_cartonQty);
		sl_looseQty = (EditText) findViewById(R.id.stadj_looseQty);
		sl_qty = (EditText) findViewById(R.id.stadj_qty);
		sl_uom = (EditText) findViewById(R.id.stadj_uom);
		sl_cartonPerQty = (EditText) findViewById(R.id.stadj_cartonPerQty);
		expand = (ImageView) findViewById(R.id.expand);
		sl_total = (EditText) findViewById(R.id.stadj_total);
		sl_tax = (EditText) findViewById(R.id.stadj_tax);
		sl_netTotal = (EditText) findViewById(R.id.stadj_netTotal);
		productFilterList = (ListView) findViewById(R.id.productFilterList);
	
		stadj_stockinhand = (EditText) findViewById(R.id.stadj_stock);
	   
		stadj_plus = (Button) findViewById(R.id.stadj_plus);
		stadj_minus = (Button) findViewById(R.id.stadj_minus);
		
		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen= (TextView) findViewById(R.id.sum_screen);
		
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);
//		customer_view = findViewById(R.id.customer_view);
			
//		customer_view.setVisibility(View.GONE);
		customer_screen.setVisibility(View.GONE);
		customer_layout.setVisibility(View.GONE);
		addProduct_screen.setBackgroundColor(Color.parseColor("#00AFF0"));
//		adddetect ="";
		addProduct_screen.setTextColor(Color.parseColor("#FFFFFF"));
		addProduct_screen.setBackgroundResource(drawable.tab_select);
		
		calCarton = LogOutSetGet.getCalcCarton();
		
//		calCarton="0";
		
		transferBatchDialog = new TransferBatchDialog();
		batchDialog = new BatchDialog();
		valid_url = FWMSSettingsDatabase.getUrl();
		new SalesProductWebService(valid_url);
		new NewProductWebService(valid_url);
		new SalesOrderWebService(valid_url);
		SOTDatabase.init(StockAdjustmentAddproduct.this);
		
		stadj_plus.setBackgroundResource(drawable.button_focus);
		stadj_plus.setId(1);
//		adddetect = "1";
		stadj_minus.setBackgroundResource(drawable.button_normal);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			sales_prodCode = extras.getString("SOT_ssproductcode");
			
			ProductStockAsyncCall task = new ProductStockAsyncCall();
			task.execute();
			
			sl_codefield.setText(extras.getString("SOT_ssproductcode"));
			sl_namefield.setText(extras.getString("SOT_str_ssprodname"));
			sl_cartonQty.setText(extras.getString("SOT_str_sscq"));
			sl_looseQty.setText(extras.getString("SOT_str_sslq"));
			sl_qty.setText(extras.getString("SOT_str_ssqty"));
			stadj_stockinhand.setText(extras.getString("SOT_str_ssfoc"));
			String adddetect = extras.getString("SOT_str_ssexchqty");
//			String adddetect = extras.getString("SOT_str_ssfoc");
			Log.d("adddetect",adddetect);
			if(adddetect.matches("1")){
				stadj_plus.setBackgroundResource(drawable.button_focus);
				stadj_plus.setId(1);
				stadj_minus.setBackgroundResource(drawable.button_normal);
			}else if(adddetect.startsWith("-1")){
				stadj_minus.setBackgroundResource(drawable.button_focus);
				stadj_plus.setId(-1);
//				stadj_minus.setEnabled(false);
				stadj_plus.setBackgroundResource(drawable.button_normal);
				
			}
			
			slPrice = extras.getString("SOT_str_ssprice");
			sl_uom.setText(extras.getString("SOT_str_ssuom"));
			sl_cartonPerQty.setText(extras.getString("SOT_st_sscpqty"));
			sl_total.setText(extras.getString("SOT_st_sstotal"));
			sl_tax.setText(extras.getString("SOT_st_sstax"));
			taxValue = extras.getString("SOT_st_sstaxvalue");
			slCartonPerQty = extras.getString("SOT_st_sscpqty");
			sl_netTotal.setText(extras.getString("SOT_st_ssnettot"));
			str_sssno = extras.getString("SOT_ssno");
			str_ssupdate = extras.getString("SOT_ssupdate");
			str_sscancel = extras.getString("SOT_sscancel");
			if ((str_ssupdate != null) || (str_sscancel != null)) {
				sl_addProduct.setText(str_ssupdate);
			
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
		}

		getSalesProdArr.clear();
		companyArr.clear();
		searchProductArr.clear();
		mHashMap = new HashMap<String, String>();
		Log.d("GRATaxT", SalesOrderSetGet.getCompanytax());
		taxType = SalesOrderSetGet.getCompanytax();
		cmpnyCode = SupplierSetterGetter.getCompanyCode();
		cstcode = SalesOrderSetGet.getSuppliercode();
		cstgrpcd = SalesOrderSetGet.getSuppliergroupcode();

		 new GetProduct().execute();
		 
		 
		 
		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Cursor cursor = SOTDatabase.getCursor();
				int count = cursor.getCount();
				if(count>0){
					alertDialog();
				}else{

					Intent i = new Intent(StockAdjustmentAddproduct.this, StockAdjustmentHeader.class);
					startActivity(i);
					StockAdjustmentAddproduct.this.finish();

				}
				
			
			}
		});

		summary_screen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(StockAdjustmentAddproduct.this, StockAdjustmentSummary.class);
				startActivity(i);
				StockAdjustmentAddproduct.this.finish();
			}
		});
		
		stadj_plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				stadj_plus.setBackgroundResource(drawable.button_focus);
				stadj_plus.setId(1);
//				adddetect = "1";
				stadj_minus.setBackgroundResource(drawable.button_normal);
			}
		});
		
		stadj_minus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				stadj_minus.setBackgroundResource(drawable.button_focus);
				//stadj_minus.setId(-1);
				stadj_plus.setId(-1);
//				adddetect = "-1";
				stadj_plus.setBackgroundResource(drawable.button_normal);

				String qty = sl_qty.getText().toString();
				double qty_i= Double.parseDouble(qty);
				String s_adjust = stadj_stockinhand.getText().toString();
				double adjust_i = Double.parseDouble(s_adjust);
				 if(qty_i>adjust_i){
				 	Toast.makeText(getApplicationContext(),"Quantity is greater than StockInHand",Toast.LENGTH_SHORT).show();
				 }

			}
		});
		
		
		productFilterList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {

				sales_prodCode = searchProductArr.get(position).get(
						"ProductCode");
				sl_codefield.setText(sales_prodCode);
				loadprogress();
				new AsyncCallSaleProduct().execute();
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
		
		sl_addProduct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final String stock = stadj_stockinhand.getText().toString();
				Log.d("stockCheckValue",stock);
				if ((str_ssupdate != null) || (str_sscancel != null)) {
					if(stock.contains("-")){
						Toast.makeText(getApplicationContext(),"Stock Count is less than 0",Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(StockAdjustmentAddproduct.this, "Updated ",
								Toast.LENGTH_LONG).show();
						storeInDatabase();

						Intent i = new Intent(StockAdjustmentAddproduct.this,
								StockAdjustmentSummary.class);
						startActivity(i);
						StockAdjustmentAddproduct.this.finish();

					}

				} else {

					final String code = sl_codefield.getText().toString();
					String name = sl_namefield.getText().toString();

					if (code.matches("")) {
						Toast.makeText(StockAdjustmentAddproduct.this,
								"Select product code", Toast.LENGTH_SHORT)
								.show();
					} else {
						
						String dbAddDetect = SOTDatabase.getAddDetect(code);
						String dbbatchid = SOTDatabase.getBatchNo(code);
						
						final String addDetect = stadj_plus.getId() + "";
						Log.d("stadj_plus", "->" + stadj_plus.getId());
						Log.d("dbAddDetect", "->" + dbAddDetect);
						if (dbAddDetect != null && !dbAddDetect.isEmpty()) {
							if (dbAddDetect.matches(addDetect)) {
								if(stock.contains("-")){
									Toast.makeText(getApplicationContext(),"Stock Count is less than 0",Toast.LENGTH_SHORT).show();
								}else{
									storeInDatabase();
								}
							} else {

								AlertDialog.Builder alertDialog = new AlertDialog.Builder(
										StockAdjustmentAddproduct.this);

								alertDialog.setTitle(name);
								alertDialog.setMessage(name
										+ " is already exists");
								alertDialog.setPositiveButton("Replace",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												SOTDatabase
														.deleteBatchProduct(code);
												if(stock.contains("-")){
													Toast.makeText(getApplicationContext(),"Stock Count is less than 0",Toast.LENGTH_SHORT).show();
												}else{
													storeInDatabase();
												}
											}
										});

								alertDialog.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										});

								alertDialog.show();
							}
						} else {
							
							if (dbbatchid != null && !dbbatchid.isEmpty()) {
								SOTDatabase.deleteBatchProduct(code);
							}
							//else{
							if(stock.contains("-")){
								Toast.makeText(getApplicationContext(),"Stock Count is less than 0",Toast.LENGTH_SHORT).show();
							}else{
								storeInDatabase();
							}
							//}
							
						}
					}
				}
			}
		});
		
		expand.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				if (pcs_layout.getVisibility() == View.VISIBLE) {
					// Its visible			
					pcs_txt_layout.setVisibility(View.GONE);
					pcs_layout.setVisibility(View.GONE);
				} else {				
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

		sl_cartonQty.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					if (calCarton.matches("0")) {
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

		cqtyTW=new TextWatcher() {

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
				if (calCarton.matches("0")) {
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

					String lqty = sl_looseQty.getText().toString();
					if(lqty.matches("")){
						lqty="0";
				       }
					if (!lqty.matches("")) {
						sl_qty.removeTextChangedListener(qtyTW);
						sl_qty.setText(lqty);
						sl_qty.addTextChangedListener(qtyTW);
						
						if(sl_qty.length()!=0){
							sl_qty.setSelection(sl_qty.length());
						}
						double lsQty = Double.parseDouble(lqty);

						productTotal(lsQty);
					}

				}
			}

		};

		sl_looseQty.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					if (calCarton.matches("0")) {
						looseQtyCalcPcsOne(0);
						int length = sl_looseQty.length();
						if (length == 0) {
							looseQtyCalcPcsOne(0);
							}
					}else{
					looseQtyCalc();
					}
					sl_qty.requestFocus();
					return true;
				}
				return false;
			}
		});

		lqtyTW=new TextWatcher() {

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
				if (calCarton.matches("0")) {
					looseQtyCalcPcsOne(0);
					int length = sl_looseQty.length();
					if (length == 0) {
						looseQtyCalcPcsOne(0);
						}
				}else{
				looseQtyCalc();
				}
				if (calCarton.matches("0")) {
					
				}else{
				int length = sl_looseQty.length();
				if (length == 0) {
					String qty = sl_qty.getText().toString();
					if (!beforeLooseQty.matches("") && !qty.matches("")) {

						int qtyCnvrt = Integer.parseInt(qty);
						int lsCnvrt = Integer.parseInt(beforeLooseQty);

						sl_qty.removeTextChangedListener(qtyTW);
						sl_qty.setText("" + (qtyCnvrt - lsCnvrt));
						sl_qty.addTextChangedListener(qtyTW);
						
						if(sl_qty.length()!=0){
							sl_qty.setSelection(sl_qty.length());
						}
						
						
						looseQtyCalc();
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

					String qty = sl_qty.getText().toString();
					if (!qty.matches("")) {
						double qtyCalc = Double.parseDouble(qty);
						if (calCarton.matches("0")) {
							
						}else{
						clQty();
						}
						productTotal(qtyCalc);
					}
					stadj_stockinhand.requestFocus();
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

				String qty = sl_qty.getText().toString();
				if (!qty.matches("")) {
					double qtyCalc = Double.parseDouble(qty);
					if (calCarton.matches("0")) {
						
					}else{
					clQty();
					}
					productTotal(qtyCalc);
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
		
		
		
		 sl_cartonQty.addTextChangedListener(cqtyTW) ;
		  sl_looseQty.addTextChangedListener(lqtyTW);
		    sl_qty.addTextChangedListener(qtyTW);
	}
	
	public void clQty(){
		  String qty = sl_qty.getText().toString();
		  String crtnperQty = sl_cartonPerQty.getText().toString();
		  int q = 0, r = 0;
		  
		  if(crtnperQty.matches("0") || crtnperQty.matches("null") || crtnperQty.matches("0.00")){
			  crtnperQty="1";
		  }
		  
		  if (!crtnperQty.matches("")) {
		   if (!qty.matches("")) {
		    try {
		     int qty_nt = Integer.parseInt(qty);
		     int pcs_nt = Integer.parseInt(crtnperQty);
		     
		     Log.d("qty_nt", ""+qty_nt);
		     Log.d("pcs_nt", ""+pcs_nt);
		     
		     q =  qty_nt / pcs_nt;
		     r =  qty_nt % pcs_nt;

		     Log.d("cqty", ""+q);
		     Log.d("lqty", ""+r);
	     
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

				productTotal(qty);
	
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

				productTotal(qty);
	
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

				productTotal(qty);
		
		}
		}catch(NumberFormatException e){
			e.printStackTrace();
		
		}catch(Exception e){
			e.printStackTrace();
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
			
			if(sl_qty.length()!=0){
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
			
			if(sl_qty.length()!=0){
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
			
			if(sl_qty.length()!=0){
				sl_qty.setSelection(sl_qty.length());
			}

			productTotal(qty);
		}
	}

	public void productTotal(double qty) {
		
		try {
			double taxAmount = 0.0, netTotal = 0.0;
			if (slPrice.matches("")) {
				slPrice = "0";
			}

			if (!slPrice.matches("")) {

				double slPriceCalc = Double.parseDouble(slPrice);

				tt = qty * slPriceCalc;

				String Prodtotal = twoDecimalPoint(tt);

				double subTotal = 0.0;

				subTotal = tt;

				String sbTtl = twoDecimalPoint(subTotal);

				sl_total.setText("" + sbTtl);

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

						netTotal = tt + taxAmount;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);

					} else if (taxType.matches("Z")) {

						sl_tax.setText("0.0");

						netTotal = tt + taxAmount;
						String ProdNetTotal = twoDecimalPoint(netTotal);
						sl_netTotal.setText("" + ProdNetTotal);

					} else {
						sl_tax.setText("0.0");
						sl_netTotal.setText("" + Prodtotal);
					}

				} else if (taxValue.matches("")) {
					sl_tax.setText("0.0");
					sl_netTotal.setText("" + Prodtotal);
				}else{
					sl_tax.setText("0.0");
					sl_netTotal.setText("" + Prodtotal);
				}
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
		Log.d("started","-->"+cursor.getCount());
		if (cursor != null && cursor.getCount() > 0) {
			sl_no = cursor.getCount();
			sl_no++;
		}

		String codeStr = sl_codefield.getText().toString();		
		String cartonQtyStr = sl_cartonQty.getText().toString();	
		String qtyStr = sl_qty.getText().toString();
	
				
		if (codeStr.matches("")) {
			Toast.makeText(StockAdjustmentAddproduct.this, "Select product code",
					Toast.LENGTH_SHORT).show();
		} else if (qtyStr.matches("")) {
			
			if (calCarton.matches("0")) {
				
				if(cartonQtyStr.matches("")){
					Toast.makeText(StockAdjustmentAddproduct.this, "Enter the carton/quantity",
							Toast.LENGTH_SHORT).show();
					sl_cartonQty.requestFocus();
				}else{
					save();
				}
				
			}else{
				Toast.makeText(StockAdjustmentAddproduct.this, "Enter the quantity",
						Toast.LENGTH_SHORT).show();
			}	
		} else {
			save();
		}
		
		if (!searchProductArr.isEmpty()) {
		      adapter.notifyDataSetChanged();
		     }
	}
	
	public void save(){

		int Qtyinhand=0;
		final String addDetect = stadj_plus.getId() + "";
		Log.d("addDetect", "->" + addDetect);
		
		String codeStr = sl_codefield.getText().toString();
		String nameStr = sl_namefield.getText().toString();
		String cartonQtyStr = sl_cartonQty.getText().toString();
		String looseQtyStr = sl_looseQty.getText().toString();
		String qtyStr = sl_qty.getText().toString();
		String uomStr = sl_uom.getText().toString();
		String cartonPerQtyStr = sl_cartonPerQty.getText().toString();
		String totalStr = sl_total.getText().toString();
		String taxStr = sl_tax.getText().toString();
		String netTotalStr = sl_netTotal.getText().toString();
		String qtyinhand = stadj_stockinhand.getText().toString();

		int cartonQty = 0, looseQty = 0, qty = 0, cartonPerQty = 0;
		double price = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0;
		String sbTtl = "";
		String netT = "";

		if (!slPrice.matches("")) {
			price = Double.parseDouble(slPrice);
		}

		if (!cartonQtyStr.matches("")) {
			cartonQty = Integer.parseInt(cartonQtyStr);
		}
		if (!looseQtyStr.matches("")) {
			looseQty = Integer.parseInt(looseQtyStr);
		}
		if (!qtyStr.matches("")) {
			qty = Integer.parseInt(qtyStr);
		}

		if (!cartonPerQtyStr.matches("")) {
			cartonPerQty = Integer.parseInt(cartonPerQtyStr);
		}

		if (!totalStr.matches("")) {
			total = Double.parseDouble(totalStr);

			subTotal = total;

			sbTtl = twoDecimalPoint(subTotal);

			if ((str_ssupdate != null) || (str_sscancel != null)) {
				int i_sssno = Integer.parseInt(str_sssno);
				SOTDatabase.updateBillDisc(i_sssno, codeStr, sbTtl);
			} else {
				SOTDatabase.storeBillDisc(sl_no, codeStr, sbTtl);
			}

		}
		if (!taxStr.matches("")) {
			tax = Double.parseDouble(taxStr);
		}
		if (!netTotalStr.matches("")) {
			ntTot = subTotal + tax;

			netT = twoDecimalPoint(ntTot);
		}

		if (taxValue.matches("") || taxValue == null) {
			taxValue = "0";
		}

		 if ((str_ssupdate != null) || (str_sscancel != null)) {
			    
			    int i_sssno = Integer.parseInt(str_sssno);
			    HashMap<String, String> queryValues = new HashMap<String, String>();
			    queryValues.put("ProductCode", codeStr);
			    queryValues.put("ProductName", nameStr);
			    queryValues.put("CQty", cartonQty+"");
			    queryValues.put("LQty", looseQty+"");
			    queryValues.put("Qty", qty+"");
			    queryValues.put("StockinHand", qtyinhand);
			    queryValues.put("Price", price+"");
			    queryValues.put("Dicount", "0.0" );
			    queryValues.put("Uom", uomStr);
			    queryValues.put("PcsPerCarton", cartonPerQty+"");
			    queryValues.put("Total", tt+"" );
			    queryValues.put("Tax", tax+"");
			    queryValues.put("NetTotal", netT);
			    queryValues.put("SubTotal", sbTtl);
			    queryValues.put("CartonPrice", "0.00");
			    queryValues.put("AddDetect", addDetect);
			    queryValues.put("SOSlno", i_sssno+"");

			    SOTDatabase.updateStockAdjustmentProduct(queryValues);

			   } else {
			    
			    Log.d("Price", ""+price);
			    
			    HashMap<String, String> queryValues = new HashMap<String, String>();
			    queryValues.put("slNo", sl_no+"");
			    queryValues.put("ProductCode", codeStr);
			    queryValues.put("ProductName", nameStr);
			    queryValues.put("CQty", cartonQty+"");
			    queryValues.put("LQty", looseQty+"");
			    queryValues.put("Qty", qty+"");
			    queryValues.put("StockinHand", qtyinhand);
			    queryValues.put("Price", price+"");
			    queryValues.put("Dicount", "0.0" );
			    queryValues.put("Uom", uomStr);
			    queryValues.put("PcsPerCarton", cartonPerQty+"");
			    queryValues.put("Total", tt+"" );
			    queryValues.put("Tax", tax+"");
			    queryValues.put("NetTotal", netT);
			    queryValues.put("TaxType", taxType+"");
			    queryValues.put("TaxPerc", taxValue+"");
			    queryValues.put("RetailPrice", "");
			    queryValues.put("SubTotal", sbTtl);
			    queryValues.put("CartonPrice", "0.00");
			    queryValues.put("AddDetect", addDetect);
			    queryValues.put("MinimumSellingPrice", "0");
			    queryValues.put("ItemRemarks", "");
			    queryValues.put("SOSlno", "");
			    
			    SOTDatabase.storeStockAdjustmentProduct(queryValues);
			    SOTDatabase.updateproductbatch(codeStr,haveBatch,haveExpiry);
			sl_codefield.setText("");
			sl_namefield.setText("");
			sl_cartonQty.setText("");
			sl_looseQty.setText("");
			sl_qty.setText("");
			stadj_stockinhand.setText("");
			sl_total.setText("0");
			sl_tax.setText("0");
			sl_netTotal.setText("0");
			sl_uom.setText("");
//			sl_stock.setText("");
			sl_cartonPerQty.setText("");

			stadj_plus.setBackgroundResource(drawable.button_focus);
			stadj_plus.setId(1);
			stadj_minus.setBackgroundResource(drawable.button_normal);

			sl_codefield.requestFocus();

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(
					sl_codefield.getWindowToken(), 0);

			sl_cartonQty.setEnabled(true);
			sl_cartonQty.setFocusable(true);
			sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

			sl_looseQty.setEnabled(true);
			sl_looseQty.setFocusable(true);
			sl_looseQty.setBackgroundResource(drawable.edittext_bg);

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

	}
*/
	public void alertDialogSearch() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				StockAdjustmentAddproduct.this);
		editText = new EditText(StockAdjustmentAddproduct.this);
		final ListView listview = new ListView(StockAdjustmentAddproduct.this);
		LinearLayout layout = new LinearLayout(StockAdjustmentAddproduct.this);
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

		arrayAdapterProd = new CustomAlertAdapterProd(StockAdjustmentAddproduct.this,
				al);
		listview.setAdapter(arrayAdapterProd);
		listview.setOnItemClickListener(StockAdjustmentAddproduct.this);

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
						StockAdjustmentAddproduct.this, searchResults);
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
					sl_uom.setText("");
//					sl_stock.setText("");
					sl_cartonPerQty.setText("");
				}
			});
		}

	}

	private class CheckProductBarcode extends AsyncTask<Void, Void, Void> {

		String ed_code="";
		@Override
		protected void onPreExecute() {
			mBarcodeJsonString="";
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {		
			ed_code = sl_codefield.getText().toString(); 
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
			 new AsyncCallSaleProduct().execute();
			}else{
				sales_prodCode = ed_code;
				new AsyncCallSaleProduct().execute();
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
				
				
				return null;
				}

			@Override
			protected void onPostExecute(Void result) {
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(stAdjustproduct_layout, true);
			}

		}
	private class AsyncCallSaleProduct extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			getSalesProdArr.clear();
			alBatchStock.clear();
			slPrice = "";
			slUomCode = "";
			slCartonPerQty = "";
			taxValue = "";
			sl_cartonQty.setText("");
			sl_looseQty.setText("");
			sl_qty.setText("");
			stadj_stockinhand.setText("");
			sl_uom.setText("");
//			sl_stock.setText("");
			sl_cartonPerQty.setText("");
			sl_total.setText("0");
			sl_tax.setText("0");
			sl_netTotal.setText("0");			
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			
			getSalesProdArr = SalesProductWebService.getGraProduct(
					sales_prodCode, webMethName);
			
			alBatchStock = SalesProductWebService.getProductBatchStockAdjustment(sales_prodCode, "fncGetProductBatchStock");
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			ProductStockAsyncCall task = new ProductStockAsyncCall();
			task.execute();
			
			if (!getSalesProdArr.isEmpty()) {
				Log.d("getSalesProdArr", getSalesProdArr.toString());
				slPrice = getSalesProdArr.get(0);
				slUomCode = getSalesProdArr.get(1);
				slCartonPerQty = getSalesProdArr.get(2);
				taxValue = getSalesProdArr.get(3);
				sl_uom.setText(slUomCode);
				
				haveBatch = getSalesProdArr.get(4);
				haveExpiry = getSalesProdArr.get(5);
				
				String codefield = getSalesProdArr.get(6);
				String namefield = getSalesProdArr.get(7);				
				Weight = getSalesProdArr.get(8);
				
				Log.d("Weight",""+ Weight);	
				sl_codefield.setText(codefield);
				sl_namefield.setText(namefield);
				Log.d("haveBatch", haveBatch);
				Log.d("haveExpiry", haveExpiry);

				if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
						|| slCartonPerQty.matches("")) {

					sl_cartonQty.setFocusable(false);
					sl_cartonQty.setBackgroundResource(drawable.labelbg);

					sl_looseQty.setFocusable(false);
					sl_looseQty.setBackgroundResource(drawable.labelbg);

					sl_qty.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(sl_qty, InputMethodManager.SHOW_IMPLICIT);

				} else {
					sl_cartonQty.setFocusableInTouchMode(true);
					sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

					sl_looseQty.setFocusableInTouchMode(true);
					sl_looseQty.setBackgroundResource(drawable.edittext_bg);

					sl_cartonQty.requestFocus();
				}

				sl_cartonPerQty.setText(slCartonPerQty);
				
				if (!alBatchStock.isEmpty()) {
					alertDialogWithBotton();
				}

//				progressBar.setVisibility(View.GONE);
//				spinnerLayout.setVisibility(View.GONE);
//				enableViews(stAdjustproduct_layout, true);

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
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(stAdjustproduct_layout, true);

		}
	}

	private class AsyncCallWSSearchProduct extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
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
			enableViews(stAdjustproduct_layout, true);
		}
	}
	
	
	public void searchProductList() throws ParseException {

		adapter = new AddSimpleAdapter(StockAdjustmentAddproduct.this,
				searchProductArr, R.layout.sale_productitem,"", new String[] {
						"ProductCode", "ProductName", "WholeSalePrice" },
				new int[] { R.id.txt_code, R.id.txt_name, R.id.txt_price });
		productFilterList.setAdapter(adapter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		if (menu.isMenuShowing()) {
			// enableViews(stAdjustproduct_layout, false);
		} else {
			// enableViews(stAdjustproduct_layout, true);
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
				if(lengthJsonArr>0){
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode;

					jsonChildNode = product_stock_jsonMainNode.getJSONObject(i);

					stock = jsonChildNode.optString("Qty")
							.toString();
					
					Log.d("stock qty", stock);
					
					
				}
				}else{
					stock ="";
				}
			} catch (JSONException e) {
				stock ="";
				e.printStackTrace();
			} catch (Exception e) {
				stock ="";
			}

			/******* Fetch node values **********/

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			double stck;
			int stk=0;
			
			if(!stock.matches("")){
				if(stock.startsWith("-")){
					stadj_minus.setBackgroundResource(drawable.button_focus);
					//stadj_minus.setId(-1);
					stadj_plus.setId(-1);
					stadj_minus.setEnabled(false);
					stadj_plus.setBackgroundResource(drawable.button_normal);
				}else{
					stadj_plus.setBackgroundResource(drawable.button_focus);
					stadj_plus.setId(1);
//				adddetect = "1";
					stadj_minus.setBackgroundResource(drawable.button_normal);
				}
				stck = Double.parseDouble(stock);				
				stk = (int) stck;
				if (!sl_codefield.getText().toString().matches("")) {
					stadj_stockinhand.setText(" "+stk);
				}
			}


					
		}
	}

	public void gotoBatch(){
		
		HashMap<String, Object> hm = new HashMap<String, Object>();

		hm.put("Productcode", sl_codefield);
		hm.put("Productname", sl_namefield);
		hm.put("Cartonqty", sl_cartonQty);
		hm.put("Looseqty", sl_looseQty);
		hm.put("Qty", sl_qty);
		hm.put("StockInHand", stadj_stockinhand);
		hm.put("Total", sl_total);
		hm.put("Tax", sl_tax);
		hm.put("Net_Total", sl_netTotal);
		hm.put("Uom", sl_uom);
//		hm.put("Stock", sl_stock);
		hm.put("Cartonperqty", sl_cartonPerQty);
		hm.put("AddStock", stadj_plus);
		hm.put("MinusStock", stadj_minus);
		
		 Log.d("HaveBatchOnStockAdjustment","-->"+SalesOrderSetGet.getHaveBatchOnStockAdjustment());
		 String haveBatchOnStockAdjustment = SalesOrderSetGet.getHaveBatchOnStockAdjustment();
		 if (haveBatchOnStockAdjustment.matches("True")) {
				if (haveBatch.matches("True") || haveExpiry.matches("True")) {
					Log.d("haveBatch", "haveExpiry");
					String code = sl_codefield.getText().toString();
					String name = sl_namefield.getText().toString();
					batchDialog.initiateBatchPopupWindow(StockAdjustmentAddproduct.this, haveBatch, haveExpiry, code, name, slCartonPerQty, hm);
					//storeInDatabase();
				} else {
					Log.d("no haveBatch", "no haveExpiry");
					storeInDatabase();
				}

			} else {
				Log.d("no haveBatchOnStockIn", "no haveBatchOnStockIn");
				storeInDatabase();
			}
	}
	
	public void gotoBatchTransfer(){
		
		HashMap<String, Object> hm = new HashMap<String, Object>();

		hm.put("Productcode", sl_codefield);
		hm.put("Productname", sl_namefield);
		hm.put("Cartonqty", sl_cartonQty);
		hm.put("Looseqty", sl_looseQty);
		hm.put("Qty", sl_qty);
		hm.put("StockInHand", stadj_stockinhand);
		hm.put("Total", sl_total);
		hm.put("Tax", sl_tax);
		hm.put("Net_Total", sl_netTotal);
		hm.put("Uom", sl_uom);
//		hm.put("Stock", sl_stock);
		hm.put("Cartonperqty", sl_cartonPerQty);
		hm.put("AddStock", stadj_plus);
		hm.put("MinusStock", stadj_minus);
		
		 Log.d("HaveBatchOnStockAdjustment","-->"+SalesOrderSetGet.getHaveBatchOnStockAdjustment());
		 String haveBatchOnStockAdjustment = SalesOrderSetGet.getHaveBatchOnStockAdjustment();
		 if (haveBatchOnStockAdjustment.matches("True")) {
				if (haveBatch.matches("True") || haveExpiry.matches("True")) {
					Log.d("haveBatch", "haveExpiry");
					String code = sl_codefield.getText().toString();
					String name = sl_namefield.getText().toString();
					
				if (!alBatchStock.isEmpty()) {
					transferBatchDialog.initiateBatchPopupWindow(
							StockAdjustmentAddproduct.this, haveBatch, haveExpiry,
							code, name, slCartonPerQty, hm,
							alBatchStock);
				} else {
					sl_codefield.setText("");
					sl_namefield.setText("");
					sl_cartonQty.setText("");
					sl_looseQty.setText("");
					sl_qty.setText("");
					stadj_stockinhand.setText("");
					sl_total.setText("0");
					sl_tax.setText("0");
					sl_netTotal.setText("0");
					sl_uom.setText("");
//					sl_stock.setText("");
					sl_cartonPerQty.setText("");

					stadj_plus.setBackgroundResource(drawable.button_focus);
					stadj_plus.setId(1);
					stadj_minus.setBackgroundResource(drawable.button_normal);
					
					Toast.makeText(StockAdjustmentAddproduct.this, "No item to transfer", Toast.LENGTH_LONG).show();
				}
					
					
				} else {
					Log.d("no haveBatch", "no haveExpiry");
					storeInDatabase();
				}

			} else {
				Log.d("no haveBatchOnStockIn", "no haveBatchOnStockIn");
				storeInDatabase();
			}
		
	
	}
	private void loadprogress() {
		spinnerLayout = new LinearLayout(StockAdjustmentAddproduct.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(stAdjustproduct_layout, false);
		progressBar = new ProgressBar(StockAdjustmentAddproduct.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		spinnerLayout.addView(progressBar);
		
	}

	@Override
	public void onListItemClick(String item) {
		menu.toggle();
	}
	
	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Deleting");
		alertDialog
				.setMessage("Stock Adjustment products will clear. Do you want to proceed");
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Intent i = new Intent(StockAdjustmentAddproduct.this, StockAdjustmentHeader.class);
						startActivity(i);
						StockAdjustmentAddproduct.this.finish();

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
	
	
	public void storeStockadjtData(){
		
		final String code = sl_codefield.getText().toString();
		String name = sl_namefield.getText().toString();
		
		String dbAddDetect = SOTDatabase.getAddDetect(code);
		String dbbatchid = SOTDatabase.getBatchNo(code);
		
		final String addDetect = stadj_plus.getId() + "";
		Log.d("stadj_plus", "->" + stadj_plus.getId());
		Log.d("dbAddDetect", "->" + dbAddDetect);
		if (dbAddDetect != null && !dbAddDetect.isEmpty()) {
			if (dbAddDetect.matches(addDetect)) {
				if (addDetect.matches("1")) {
					gotoBatch();
				} else if (addDetect.matches("-1")) {
					gotoBatchTransfer();
				}
			} else {

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						StockAdjustmentAddproduct.this);

				alertDialog.setTitle(name);
				alertDialog.setMessage(name
						+ " is already exists");
				alertDialog.setPositiveButton("Replace",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								SOTDatabase
										.deleteBatchProduct(code);
								if (addDetect.matches("1")) {
									gotoBatch();
								} else if (addDetect
										.matches("-1")) {
									gotoBatchTransfer();
								}
							}
						});

				alertDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alertDialog.show();
			}
		} else {
			
			if (dbbatchid != null && !dbbatchid.isEmpty()) {
				SOTDatabase.deleteBatchProduct(code);
			}
			//else{
				if (addDetect.matches("1")) {
					gotoBatch();
				} else if (addDetect.matches("-1")) {
					gotoBatchTransfer();
				}
			//}
			
		}
	}
	
	public void alertDialogWithBotton(){
		
		final Dialog myDialog = new Dialog(StockAdjustmentAddproduct.this);
        myDialog.setContentView(R.layout.stockadjustment_alert);
        myDialog.setTitle("Add/Detect");
        myDialog.setCancelable(false);

        Button plus = (Button) myDialog.findViewById(R.id.stadj_alert_plus);
        Button minus = (Button) myDialog.findViewById(R.id.stadj_alert_minus);
        plus.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	stadj_plus.setId(1);
            	storeStockadjtData();
            	myDialog.dismiss();
            }
        });

        minus.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	stadj_plus.setId(-1);
            	storeStockadjtData();
            	myDialog.dismiss();
            }
        });

        myDialog.show();
        myDialog.getWindow().setLayout(300, 250);
	}
	
	
	@Override
	public void onBackPressed() {
		if ((str_ssupdate != null) || (str_sscancel != null)) {
			Intent i = new Intent(StockAdjustmentAddproduct.this,
					StockAdjustmentSummary.class);
			startActivity(i);
			StockAdjustmentAddproduct.this.finish();
		} else {
			Intent i = new Intent(StockAdjustmentAddproduct.this,
					StockAdjustmentHeader.class);
			startActivity(i);
			StockAdjustmentAddproduct.this.finish();
			
		}
	}
}