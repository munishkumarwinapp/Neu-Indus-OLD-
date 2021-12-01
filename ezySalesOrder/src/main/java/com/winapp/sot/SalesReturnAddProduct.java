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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.adapter.Attribute;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterProd;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.NewProductWebService;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.sot.FilterCS.OnFilterCompletionListener;

public class SalesReturnAddProduct extends SherlockFragmentActivity implements
SlideMenuFragment.MenuClickInterFace, OnItemClickListener {

	private static String webMethName = "fncGetProduct";
	private static String webMethNamebar = "fncGetProductBarCode";
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String PRODUCT_NAME = "ProductName";
	private static final String PRODUCTNAME_BARCODE = "ProductCode";
	private static final String PRODUCT_BARCODE = "Barcode";
	private String priceflag="",cmpnyCode="",LocationCode="",stock,calCarton="", product_stock_jsonString = null, str_ssupdate, str_sscancel, str_sssno,ss_Cqty = "", catStr = "", subCatStr = "", cstcode = "", cstgrpcd = "",
			newcprice = "",newPrice = "",cprice = "",beforeLooseQty, beforeFoc,taxType = "", taxValue = "", sales_prodCode,keyValues = "", values = "",slPrice = "", slUomCode = "", slCartonPerQty = "",valid_url, productTxt, productresult, barcoderesult, barcodeTxt,mProductJsonString ="",mBarcodeJsonString="",
					Weight="";
	private JSONObject product_stock_jsonResponse =null,mProductJsonObject =null,mBarcodeJsonObject=null;
	private JSONArray product_stock_jsonMainNode = null , mProductJsonArray=null,mBarcodeJsonArray=null;
	private HashMap<String, String> mHashMap;
	private SlidingMenu menu;
	private TextView textView1,listing_screen,customer_screen,addProduct_screen,summary_screen,price_txt,txt_price;
	private EditText editText1, editText,sl_stock;
	private ArrayList<HashMap<String, String>> searchProductArr = new ArrayList<HashMap<String, String>>();
	private Button sl_addProduct, sl_summary;
	private EditText sl_codefield, sl_namefield, sl_cartonQty, sl_looseQty, sl_qty,
			sl_foc, sl_price, sl_itemDiscount, sl_uom, sl_cartonPerQty,sl_cprice,sl_exchange,
			sl_total, sl_tax, sl_netTotal,sl_total_inclusive;
	private AlertDialog myalertDialog = null;
	private ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	private CustomAlertAdapterProd arrayAdapterProd;
	private ArrayList<HashMap<String, String>> searchResults;
	private int textlength = 0,sl_no = 1;
	private ArrayList<HashMap<String, String>> getArraylist = new ArrayList<HashMap<String, String>>();
	private ProgressBar progressBar;
	private LinearLayout salesproduct_layout,spinnerLayout,customer_layout,foc_layout,pcs_txt_layout,pcs_layout,price_header_layout,foc_header_layout,carton_layout,grid_layout,sales_stock_adjust_layout;
	private ArrayList<String> getSalesProdArr = new ArrayList<String>();
	static ArrayList<String> companyArr = new ArrayList<String>();
	private ArrayList<String> priceArr = new ArrayList<String>();
	private Cursor cursor;
	private ListView productFilterList;
	
	private Spinner stock_adustment_type;
	private double tt,itmDisc = 0,netTtal = 0, taxAmount = 0;

	private TextWatcher cqtyTW,lqtyTW,qtyTW; 
	private FilterCS filtercs;
	private HashMap<String, String> producthashValue = new HashMap<String, String>();
	
	private SimpleAdapter adapter;
	ArrayList<HashMap<String,String>> stockadjtype_List = new ArrayList<HashMap<String,String>>();
	String stockadj_code="",stockadj_name="",stockadj_type_edit;
	private ImageView expand;

	ArrayList<HashMap<String, String>> alBatchStock = new ArrayList<HashMap<String, String>>();
	private String haveBatchOnStockIn = "",haveAttribute = "",snostr;
	InvoiceBatchDialog invoiceBatchDialog;
	ArrayList<HashMap<String, String>> colorArr = new ArrayList<HashMap<String, String>>();
	ColorAttributeDialog productModifierDialog;
	private ArrayList<Attribute> mAttributeArr;
	ArrayList<Attribute> colorarrvalues=new ArrayList<>();
	private double screenInches;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.salesreturn_addproduct);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.addproduct_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Add Product");
		ImageButton filter = (ImageButton) customNav
			    .findViewById(R.id.filter);
		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);

		ab.setDisplayHomeAsUpEnabled(false);
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);

		 filtercs = new FilterCS(SalesReturnAddProduct.this);
		
		salesproduct_layout = (LinearLayout) findViewById(R.id.salesproduct_layout);
		sl_summary = (Button) findViewById(R.id.sl_summary);
		///////////////
		foc_layout = (LinearLayout) findViewById(R.id.foc_layout);
		pcs_txt_layout = (LinearLayout) findViewById(R.id.pcs_txt_layout);
		pcs_layout = (LinearLayout) findViewById(R.id.pcs_layout);
		foc_header_layout = (LinearLayout) findViewById(R.id.sales_foc_header_layout);
		price_header_layout = (LinearLayout) findViewById(R.id.sales_price_header_layout);
		sales_stock_adjust_layout = (LinearLayout) findViewById(R.id.sales_stock_adjust_layout);
		grid_layout = (LinearLayout) findViewById(R.id.sales_grid_layout);
		carton_layout = (LinearLayout) findViewById(R.id.sales_carton_layout);
		//////////
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
//////////////
		sl_cprice = (EditText) findViewById(R.id.sl_cprice);
		sl_exchange = (EditText) findViewById(R.id.sl_exchange);
		price_txt = (TextView) findViewById(R.id.sales_pricetxt);
		expand = (ImageView) findViewById(R.id.expand);
		txt_price= (TextView) findViewById(R.id.txt_price);
/////////////////

		sl_stock = (EditText) findViewById(R.id.sl_stock);
		productFilterList = (ListView) findViewById(R.id.productFilterList);


		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen = (TextView) findViewById(R.id.sum_screen);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);

		customer_layout.setVisibility(View.GONE);

		addProduct_screen.setTextColor(Color.parseColor("#FFFFFF"));
		addProduct_screen.setBackgroundResource(drawable.tab_select);

		stock_adustment_type = (Spinner) findViewById(R.id.stock_adustment_type);

		valid_url = FWMSSettingsDatabase.getUrl();
		new SalesProductWebService(valid_url);
		new NewProductWebService(valid_url);
		new SalesOrderWebService(valid_url);
		sl_itemDiscount.setRawInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);

		SOTDatabase.init(SalesReturnAddProduct.this);
		getSalesProdArr.clear();
		companyArr.clear();
		searchProductArr.clear();
		mHashMap = new HashMap<String, String>();
		Log.d("SOTaxT", SalesOrderSetGet.getCompanytax());
		taxType = SalesOrderSetGet.getCompanytax();
		cmpnyCode = SupplierSetterGetter.getCompanyCode();
		cstcode = SalesOrderSetGet.getSuppliercode();
		cstgrpcd = SalesOrderSetGet.getSuppliergroupcode();
		invoiceBatchDialog = new InvoiceBatchDialog();
		productModifierDialog = new ColorAttributeDialog();
		mAttributeArr = new ArrayList<>();
		screenInches = displayMetrics();

		calCarton = LogOutSetGet.getCalcCarton();
		///////////////
		priceflag = SalesOrderSetGet.getCartonpriceflag();
		if (priceflag.matches("null") || priceflag.matches("")) {
			priceflag = "0";
		}
		if (priceflag.matches("1")) {
			sl_cprice.setVisibility(View.VISIBLE);
			price_txt.setVisibility(View.GONE);
			price_header_layout.setVisibility(View.VISIBLE);
		} else {
			sl_cprice.setVisibility(View.GONE);
			price_txt.setVisibility(View.VISIBLE);
			price_header_layout.setVisibility(View.GONE);
		}
////////////////////
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
			sl_foc.setText(extras.getString("SOT_str_ssfoc"));
			sl_price.setText(extras.getString("SOT_str_ssprice"));
			sl_itemDiscount.setText(extras.getString("SOT_str_ssdisc"));
			sl_uom.setText(extras.getString("SOT_str_ssuom"));
			sl_cartonPerQty.setText(extras.getString("SOT_st_sscpqty"));
			sl_total.setText(extras.getString("SOT_st_sstotal"));
			//sl_tax.setText(extras.getString("SOT_st_sstax"));

			/////////////////////////////
			sl_cprice.setText(extras.getString("SOT_str_sscprice"));
			////////////////////

			taxType = extras.getString("SOT_str_sstaxtype");


			//taxType = extras.getString("SOT_str_sstaxtype");

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

			taxValue = extras.getString("SOT_st_sstaxvalue");
			slCartonPerQty = extras.getString("SOT_st_sscpqty");
			sl_tax.setText(tax);
			sl_netTotal.setText(netTotal);
			//sl_netTotal.setText(extras.getString("SOT_st_ssnettot"));
			str_sssno = extras.getString("SOT_ssno");
			str_ssupdate = extras.getString("SOT_ssupdate");
			str_sscancel = extras.getString("SOT_sscancel");
			snostr = extras.getString("SOT_slno");
			haveAttribute = SalesOrderSetGet.getHaveAttribute();

			if(haveAttribute!=null && !haveAttribute.isEmpty()){

			}else{
				haveAttribute="";
			}

			
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

			if ((str_ssupdate != null) || (str_sscancel != null)) {
				colorarrvalues = SOTDatabase.getAttributeColorValues(sales_prodCode, snostr);

				if(haveAttribute.matches("2")){

					if(colorarrvalues.size()>0){
						sl_cartonQty.setEnabled(false);
						sl_cartonQty.setFocusable(false);
						sl_cartonQty.setGravity(Gravity.CENTER);
						sl_cartonQty.setBackgroundResource(R.drawable.labelbg);

						sl_looseQty.setEnabled(false);
						sl_looseQty.setFocusable(false);
						sl_looseQty.setGravity(Gravity.CENTER);
						sl_looseQty.setBackgroundResource(R.drawable.labelbg);

//						sl_price.setVisibility(View.INVISIBLE);
//						txt_price.setVisibility(View.INVISIBLE);

						sl_qty.setEnabled(false);
						sl_qty.setFocusable(false);
						sl_qty.setGravity(Gravity.CENTER);
						sl_qty.setBackgroundResource(R.drawable.labelbg);
					}
				}
				sl_addProduct.setText(str_ssupdate);
				sl_summary.setText(str_sscancel);
			}

			stockadj_type_edit = extras.getString("SOT_stockadjustment_type");
			Log.d("stockadj_type","aadj " + stockadj_type_edit);

			
		}
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

		sl_namefield.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				productNameAlert();
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
		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Cursor cursor = SOTDatabase.getCursor();
				int count = cursor.getCount();
				if (count > 0) {
					alertDialog();
				} else {

					Intent i = new Intent(SalesReturnAddProduct.this,
							SalesReturnHeader.class);
					startActivity(i);
					SalesReturnAddProduct.this.finish();

				}

			}
		});

		customer_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(SalesReturnAddProduct.this,
						SalesReturnCustomer.class);
				startActivity(i);
				SalesReturnAddProduct.this.finish();

			}
		});

		summary_screen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent i = new Intent(SalesReturnAddProduct.this, SalesReturnSummary.class);
				startActivity(i);
				SalesReturnAddProduct.this.finish();
			}
		});

		filter.setOnClickListener(new OnClickListener() {

			   @Override
			   public void onClick(View arg0) {//			    
			   filtercs .filterDialog();		       
		       filtercs.OnFilterCompletionListener(new OnFilterCompletionListener() {		     
		     @Override
		     public void onFilterCompleted(String category, String subcategory) {		      
		       catStr = category;
		          subCatStr = subcategory;		      
		          Log.d("catStr", "----->"+catStr);
		          Log.d("subCatStr", "-->"+subCatStr);		          
		       new AsyncCallWSSearchProduct().execute();
		     }
		    });
			   }
			  });

		stock_adustment_type
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parentView,
											   View selectedItemView, int position, long id) {
						String name  = parentView.getItemAtPosition(position).toString();
						stockadj_code  = stockadjtype_List.get(position).get("Code");
						stockadj_name  = stockadjtype_List.get(position).get("Desription");
						Log.d("name", ""+stockadj_name);
						Log.d("code", ""+stockadj_code);

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		sl_addProduct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if ((str_ssupdate != null) || (str_sscancel != null)) {
					Toast.makeText(SalesReturnAddProduct.this, "Updated ",
							Toast.LENGTH_LONG).show();
					storeInDatabase();
					Intent i = new Intent(SalesReturnAddProduct.this,
							SalesReturnSummary.class);
					startActivity(i);
					SalesReturnAddProduct.this.finish();
				} else {
					storeInDatabase();
				}
				mAttributeArr.clear();

			}
		});

		//////////
		expand.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (foc_layout.getVisibility() == View.VISIBLE) {
					// Its visible
					foc_layout.setVisibility(View.GONE);
					foc_header_layout.setVisibility(View.GONE);
					pcs_txt_layout.setVisibility(View.GONE);
					pcs_layout.setVisibility(View.GONE);
					sales_stock_adjust_layout.setVisibility(View.GONE);
				} else {
					foc_layout.setVisibility(View.VISIBLE);
					foc_header_layout.setVisibility(View.VISIBLE);
					pcs_txt_layout.setVisibility(View.VISIBLE);
					pcs_layout.setVisibility(View.VISIBLE);
					sales_stock_adjust_layout.setVisibility(View.VISIBLE);
					// Either gone or invisible
				}

			}
		});
		//////////////
		sl_summary.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(SalesReturnAddProduct.this,
						SalesReturnSummary.class);
				startActivity(i);
				SalesReturnAddProduct.this.finish();
			}
		});

//		sl_codefield
//				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
//						sl_codefield) {
//					@Override
//					public boolean onDrawableClick() {
//						alertDialogSearch();
//						return true;
//
//					}
//				});
//
//		sl_codefield.setOnEditorActionListener(new OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_NEXT) {
//				//	editCodeField();
//					new CheckProductBarcode().execute();
//					return true;
//				}
//				return false;
//			}
//		});


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
	/*	sl_cartonQty.setOnEditorActionListener(new OnEditorActionListener() {

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
						*//*int length = sl_cartonQty.length();
						if (length == 0) {
							cartonQtyPcsOne(0);
						
						}*//*
					}else{
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

				}else{
					if (priceflag.matches("0")) {
						cartonQty();
					} else if (priceflag.matches("1")) {
						cartonQtyNew();
					}
				}
				
				int length = sl_cartonQty.length();
				if (length == 0) {
					if (calCarton.matches("0")) {
						
					}else{
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

						if (priceflag.matches("0")) {
							productTotal(lsQty);
						} else if (priceflag.matches("1")) {
							// String cprice =
							// sl_price.getText().toString();
							productTotalNew();
						}
					}}
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
					//	looseQtyCalcPcsOne(0);
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
				slPrice = sl_price.getText().toString();
				
				if (calCarton.matches("0")) {
					if (priceflag.matches("0")) {
						looseQtyCalcPcsOne(0);
					} else if (priceflag.matches("1")) {
//						looseQtyCalcNew();
						looseQtyCalcPcsOne(1);
					}
					int length = sl_looseQty.length();
					if (length == 0) {
						looseQtyCalcPcsOne(0);
					}
				}else{
					if (priceflag.matches("0")) {
						looseQtyCalc();
					} else if (priceflag.matches("1")) {
						looseQtyCalcNew();
					}
				}
				
				int length = sl_looseQty.length();
				if (length == 0) {
					
					if (calCarton.matches("0")) {
						
					}else{
					
					String qty = sl_qty.getText().toString();
					if (!beforeLooseQty.matches("") && !qty.matches("")) {

					//	int qtyCnvrt = Integer.parseInt(qty);
					//	int lsCnvrt = Integer.parseInt(beforeLooseQty);
						double qtyCnvrt = Double.parseDouble(qty);
						double lsCnvrt = Double.parseDouble(beforeLooseQty);
						sl_qty.removeTextChangedListener(qtyTW);
						sl_qty.setText("" + (qtyCnvrt - lsCnvrt));
						sl_qty.addTextChangedListener(qtyTW);
						
						if(sl_qty.length()!=0){
							sl_qty.setSelection(sl_qty.length());
						}
						if (priceflag.matches("0")) {
							looseQtyCalc();
						} else if (priceflag.matches("1")) {
							looseQtyCalcNew();
						}
					}}
				}
			}

		};

		sl_qty.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {

					slPrice = sl_price.getText().toString();
					
					if (calCarton.matches("0")) {
						
					}else{					
					String qty = sl_qty.getText().toString();
					if (!qty.matches("")) {
						double qtyCalc = Double.parseDouble(qty);
						clQty();
						productTotal(qtyCalc);
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
					sl_foc.requestFocus();
					return true;
				}
				return false;
			}
		});*/

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
				
				if (calCarton.matches("0")) {
					*//*if (!qty.matches("")) {
						double quantity = Double.parseDouble(qty);
						productTotal(quantity);
					}*//*
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
				if (!qty.matches("")) {
					double qtyCalc = Double.parseDouble(qty);
					clQty();
					productTotal(qtyCalc);
				}
				}
				int length = sl_qty.length();
				
				if (length == 0) {
					productTotal(0);
					if (calCarton.matches("0")) {
						
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


		sl_foc.setOnEditorActionListener(new OnEditorActionListener() {

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

				if (sl_cprice.getText().toString().equals("0.00") || sl_cprice.getText().toString().equals("0.000")
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
				if (priceflag.matches("0")) {
					itemDiscountCalc();
				} else if (priceflag.matches("1")) {
					itemDiscountCalcNew();
				}

			}
		});
		
		 sl_cartonQty.addTextChangedListener(cqtyTW) ;
		  sl_looseQty.addTextChangedListener(lqtyTW);
		    sl_qty.addTextChangedListener(qtyTW);
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

	//	public void clQty(){
//		  String qty = sl_qty.getText().toString();
//		  String crtnperQty = sl_cartonPerQty.getText().toString();
//		double q = 0, r = 0;
//
//		  if(crtnperQty.matches("0") || crtnperQty.matches("null") || crtnperQty.matches("0.00")){
//			  crtnperQty="1";
//		  }
//
//		  if (!crtnperQty.matches("")) {
//		   if (!qty.matches("")) {
//		    try {
//				double qty_nt = Double.parseDouble(qty);
//				double pcs_nt = Double.parseDouble(crtnperQty);
//
//		     Log.d("qty_nt", ""+qty_nt);
//		     Log.d("pcs_nt", ""+pcs_nt);
//
//		     q =  qty_nt / pcs_nt;
//		     r =  qty_nt % pcs_nt;
//
//		     Log.d("cqty", ""+q);
//		     Log.d("lqty", ""+r);
//
//				String ctn = twoDecimalPoint(q);
//				String loose = twoDecimalPoint(r);
//				sl_cartonQty.removeTextChangedListener(cqtyTW);
//				sl_cartonQty.setText("" + ctn);
//				sl_cartonQty.addTextChangedListener(cqtyTW);
//
//				sl_looseQty.removeTextChangedListener(lqtyTW);
//				sl_looseQty.setText("" + loose);
//				sl_looseQty.addTextChangedListener(lqtyTW);
//
//				if (priceflag.matches("0")){
//					productTotal(qty_nt);
//				}else{
//					productTotalNew();
//				}
//
//		    } catch (ArithmeticException e) {
//		     System.out.println("Err: Divided by Zero");
//
//		    } catch (Exception e) {
//		     e.printStackTrace();
//		    }
//		   }
//
//		  }
//		}
//
//	public void cartonQtyPcsOne(int priceFlag) {
//		try{
//
//		String crtnQty = sl_cartonQty.getText().toString();
//
//		if (crtnQty.matches("")){
//			crtnQty="0";
//		}
//
//		if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {
//
//			double cartonQtyCalc = Double.parseDouble(crtnQty);
//			double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
//			double qty = 0;
//			double ProductWeight=0,lsQtyCnvrt=0;
//			String lsQty = sl_looseQty.getText().toString();
//
//		/*	if (!lsQty.matches("")) {
//				double lsQtyCnvrt = Double.parseDouble(lsQty);
//				qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
//
//			} else {
//				qty = cartonQtyCalc * cartonPerQtyCalc;
//			}*/
//
//			if (!Weight.matches("")) {
//				ProductWeight = Double.parseDouble(Weight);
//			}else{
//				ProductWeight=0;
//			}
//
//			if (!lsQty.matches("")) {
//				lsQtyCnvrt = Double.parseDouble(lsQty);
//			}else{
//				lsQtyCnvrt=0;
//			}
//
//			if (cartonPerQtyCalc > 1) {
//
//					 if (ProductWeight > 0) {
//						 qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
//					 }else{
//						 qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
//					 }
//
//            }
//            else {
//                if (ProductWeight > 0) {
//                	qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
//                } else {
//
//                }
//            }
//
//
//			String quantity = twoDecimalPoint(qty);
//
//			sl_qty.removeTextChangedListener(qtyTW);
//			sl_qty.setText("" + quantity);
//			sl_qty.addTextChangedListener(qtyTW);
//
//			if (sl_qty.length() != 0) {
//				sl_qty.setSelection(sl_qty.length());
//			}
//			if(priceFlag==0){
//				productTotal(qty);
//			}else{
//				productTotalNew();
//			}
//
//		}
//		}catch(NumberFormatException e){
//			e.printStackTrace();
//
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	public void looseQtyCalcPcsOne(int priceFlag) {
//		try{
//		String crtnQty = sl_cartonQty.getText().toString();
//		String lsQty = sl_looseQty.getText().toString();
//
//		if (lsQty.matches("")) {
//			lsQty = "0";
//		}
//
//		if (!slCartonPerQty.matches("") && !crtnQty.matches("")
//				&& !lsQty.matches("")) {
//
//			double cartonQtyCalc = Double.parseDouble(crtnQty);
//			double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
//			double looseQtyCalc = Double.parseDouble(lsQty);
//			double qty=0;
//			double ProductWeight=0,lsQtyCnvrt=0;
////			qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
//
//			if (!Weight.matches("")) {
//				ProductWeight = Double.parseDouble(Weight);
//			}else{
//				ProductWeight=0;
//			}
//
//			if (!lsQty.matches("")) {
//				lsQtyCnvrt = Double.parseDouble(lsQty);
//			}else{
//				lsQtyCnvrt=0;
//			}
//
//			if (cartonPerQtyCalc > 1) {
//					 if (ProductWeight > 0) {
//						 qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
//					 }else{
//						 qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
//					 }
//            }
//            else {
//                if (ProductWeight > 0) {
//                	qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
//                } else {
//
//                }
//            }
//
//			String quantity = twoDecimalPoint(qty);
//
//			sl_qty.removeTextChangedListener(qtyTW);
//			sl_qty.setText("" + quantity);
//			sl_qty.addTextChangedListener(qtyTW);
//
//			if (sl_qty.length() != 0) {
//				sl_qty.setSelection(sl_qty.length());
//			}
//
//			if(priceFlag==0){
//				productTotal(qty);
//			}else{
//				productTotalNew();
//			}
//
//		}
//
//		if (!lsQty.matches("")) {
//
//
//			double looseQtyCalc = Double.parseDouble(lsQty);
//			double qty=0;
//			double ProductWeight=0,lsQtyCnvrt=0;
//
//			/*if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
//				double cartonQtyCalc = Double.parseDouble(crtnQty);
//				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
//
//				qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
//			} else {
//				qty = looseQtyCalc;
//			}*/
//
//			if (!Weight.matches("")) {
//				ProductWeight = Double.parseDouble(Weight);
//			}else{
//				ProductWeight=0;
//			}
//
//			if (!lsQty.matches("")) {
//				lsQtyCnvrt = Double.parseDouble(lsQty);
//			}else{
//				lsQtyCnvrt=0;
//			}
//
//			if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
//				double cartonQtyCalc = Double.parseDouble(crtnQty);
//				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
//
//
//			if (cartonPerQtyCalc > 1) {
//
//					 if (ProductWeight > 0) {
//						 qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
//					 }else{
//						 qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
//					 }
//
//            }
//            else {
//                if (ProductWeight > 0) {
//                	qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
//                } else {
//
//                }
//            }
//			}else{
//				qty = looseQtyCalc* ProductWeight;
//			}
//			String quantity = twoDecimalPoint(qty);
//
//			sl_qty.removeTextChangedListener(qtyTW);
//			sl_qty.setText("" + quantity);
//			sl_qty.addTextChangedListener(qtyTW);
//
//			if (sl_qty.length() != 0) {
//				sl_qty.setSelection(sl_qty.length());
//			}
//
//			if(priceFlag==0){
//				productTotal(qty);
//			}else{
//				productTotalNew();
//			}
//
//		}
//		}catch(NumberFormatException e){
//			e.printStackTrace();
//
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//
//
//	public void cartonQty() {
//		String crtnQty = sl_cartonQty.getText().toString();
//
//		if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {
//
//			double cartonQtyCalc = Double.parseDouble(crtnQty);
//			double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
//			double qty = 0;
//
//			String lsQty = sl_looseQty.getText().toString();
//
//			if (!lsQty.matches("")) {
//				double lsQtyCnvrt = Double.parseDouble(lsQty);
//				qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
//
//			} else {
//				qty = cartonQtyCalc * cartonPerQtyCalc;
//			}
//			String quantity = twoDecimalPoint(qty);
//			sl_qty.removeTextChangedListener(qtyTW);
//			sl_qty.setText("" + quantity);
//			sl_qty.addTextChangedListener(qtyTW);
//
//			if(sl_qty.length()!=0){
//				sl_qty.setSelection(sl_qty.length());
//			}
//
//			productTotal(qty);
//		}
//	}
//	public void cartonQtyNew() {
//		try{
//			String crtnQty = sl_cartonQty.getText().toString();
//
//			if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {
//
//				double cartonQtyCalc = Double.parseDouble(crtnQty);
//				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
//				double qty = 0;
//
//				String lsQty = sl_looseQty.getText().toString();
//
//				if (!lsQty.matches("")) {
//					double lsQtyCnvrt = Double.parseDouble(lsQty);
//					qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
//
//				} else {
//					qty = cartonQtyCalc * cartonPerQtyCalc;
//				}
//
//				String quantity = twoDecimalPoint(qty);
//
//				sl_qty.removeTextChangedListener(qtyTW);
//				sl_qty.setText("" + quantity);
//				sl_qty.addTextChangedListener(qtyTW);
//
//				if (sl_qty.length() != 0) {
//					sl_qty.setSelection(sl_qty.length());
//				}
//
//				productTotalNew();
//			}
//		}catch(NumberFormatException e){
//			e.printStackTrace();
//
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	public void looseQtyCalc() {
//		String crtnQty = sl_cartonQty.getText().toString();
//		String lsQty = sl_looseQty.getText().toString();
//
//		if (lsQty.matches("")) {
//			lsQty = "0";
//		}
//
//		if (!slCartonPerQty.matches("") && !crtnQty.matches("")
//				&& !lsQty.matches("")) {
//
//			double cartonQtyCalc = Double.parseDouble(crtnQty);
//			double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
//			double looseQtyCalc = Double.parseDouble(lsQty);
//			double qty;
//
//
//			qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
//
//			String quantity = twoDecimalPoint(qty);
//
//			sl_qty.removeTextChangedListener(qtyTW);
//			sl_qty.setText("" + quantity);
//			sl_qty.addTextChangedListener(qtyTW);
//
//			if(sl_qty.length()!=0){
//				sl_qty.setSelection(sl_qty.length());
//			}
//
//			productTotal(qty);
//		}
//
//		if (!lsQty.matches("")) {
//
//
//			double looseQtyCalc = Double.parseDouble(lsQty);
//			double qty;
//
//
//			if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
//				double cartonQtyCalc = Double.parseDouble(crtnQty);
//				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
//
//				qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
//			} else {
//				qty = looseQtyCalc;
//			}
//
//			String quantity = twoDecimalPoint(qty);
//
//			sl_qty.removeTextChangedListener(qtyTW);
//			sl_qty.setText("" + quantity);
//			sl_qty.addTextChangedListener(qtyTW);
//
//
//			if(sl_qty.length()!=0){
//				sl_qty.setSelection(sl_qty.length());
//			}
//
//			productTotal(qty);
//		}
//	}
//	public void looseQtyCalcNew() {
//		try{
//			String crtnQty = sl_cartonQty.getText().toString();
//			String lsQty = sl_looseQty.getText().toString();
//
//			if (lsQty.matches("")) {
//				lsQty = "0";
//			}
//
//			if (!slCartonPerQty.matches("") && !crtnQty.matches("")
//					&& !lsQty.matches("")) {
//
//				double cartonQtyCalc = Double.parseDouble(crtnQty);
//				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
//				double looseQtyCalc = Double.parseDouble(lsQty);
//
//				double qty;
//				qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
//
//				String quantity = twoDecimalPoint(qty);
//
//				sl_qty.removeTextChangedListener(qtyTW);
//				sl_qty.setText("" + quantity);
//				sl_qty.addTextChangedListener(qtyTW);
//
//				if (sl_qty.length() != 0) {
//					sl_qty.setSelection(sl_qty.length());
//				}
//
//				productTotalNew();
//			}
//
//			if (!lsQty.matches("")) {
//
//				double looseQtyCalc = Double.parseDouble(lsQty);
//				double qty;
//
//				if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
//					double cartonQtyCalc = Double.parseDouble(crtnQty);
//					double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
//
//					qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
//				} else {
//					qty = looseQtyCalc;
//				}
//
//				String quantity = twoDecimalPoint(qty);
//
//				sl_qty.removeTextChangedListener(qtyTW);
//				sl_qty.setText("" + quantity);
//				sl_qty.addTextChangedListener(qtyTW);
//
//				if (sl_qty.length() != 0) {
//					sl_qty.setSelection(sl_qty.length());
//				}
//
//				productTotalNew();
//			}
//		}catch(NumberFormatException e){
//			e.printStackTrace();
//
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//	public void itemDiscountCalc() {
//
//		try {
//			String itmDscnt = sl_itemDiscount.getText().toString();
//			String qty = sl_qty.getText().toString();
//			String prc = sl_price.getText().toString();
//
//			if (itmDscnt.matches("")) {
//				itmDscnt = "0";
//			}
//
//			if (!itmDscnt.matches("") && !qty.matches("") && !prc.matches("")) {
//
//				double itemDiscountCalc = 0.0;
//
//				itemDiscountCalc = Double.parseDouble(itmDscnt);
//
//				double quantityCalc = Double.parseDouble(qty);
//				double priceCalc = Double.parseDouble(prc);
//
//				tt = (quantityCalc * priceCalc) - itemDiscountCalc;
//
//				Log.d("ttl", "" + tt);
//				String Prodtotal = twoDecimalPoint(tt);
//				sl_total.setText("" + Prodtotal);
//
//				double taxAmount = 0.0, netTotal = 0.0;
//				if (!taxType.matches("") && !taxValue.matches("")) {
//
//					double taxValueCalc = Double.parseDouble(taxValue);
//
//					if (taxType.matches("E")) {
//
//						taxAmount = (tt * taxValueCalc) / 100;
//						String prodTax = fourDecimalPoint(taxAmount);
//						sl_tax.setText("" + prodTax);
//
//						netTotal = tt + taxAmount;
//						String ProdNetTotal = twoDecimalPoint(netTotal);
//						sl_netTotal.setText("" + ProdNetTotal);
//
//					} else if (taxType.matches("I")) {
//
//						taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
//						String prodTax = fourDecimalPoint(taxAmount);
//						sl_tax.setText("" + prodTax);
//
////						netTotal = tt + taxAmount;
//						netTotal = tt;
//						String ProdNetTotal = twoDecimalPoint(netTotal);
//						sl_netTotal.setText("" + ProdNetTotal);
//
//					} else if (taxType.matches("Z")) {
//
//						sl_tax.setText("0.0");
//
////						netTotal = tt + taxAmount;
//						netTotal = tt;
//						String ProdNetTotal = twoDecimalPoint(netTotal);
//						sl_netTotal.setText("" + ProdNetTotal);
//
//					} else {
//						sl_tax.setText("0.0");
//						sl_netTotal.setText("" + Prodtotal);
//					}
//				} else if (taxValue.matches("")) {
//					sl_tax.setText("0.0");
//					sl_netTotal.setText("" + Prodtotal);
//				}else{
//					sl_tax.setText("0.0");
//					sl_netTotal.setText("" + Prodtotal);
//				}
//			}
//
//		} catch (Exception e) {
//
//		}
//
//	}
//	public void itemDiscountCalcNew() {
//
//		try {
//			String itmDscnt = sl_itemDiscount.getText().toString();
//			// String qty = sl_qty.getText().toString();
//			// String prc = sl_price.getText().toString();
//			if (itmDscnt.matches("")) {
//				itmDscnt = "0";
//			}
//
//			String lPrice = sl_price.getText().toString();
//			String cPrice = sl_cprice.getText().toString();
//			String cqty = sl_cartonQty.getText().toString();
//			String lqty = sl_looseQty.getText().toString();
//
//			if (lPrice.matches("")) {
//				lPrice = "0";
//			}
//
//			if (cPrice.matches("")) {
//				cPrice = "0";
//			}
//
//			if (cqty.matches("")) {
//				cqty = "0";
//			}
//
//			if (lqty.matches("")) {
//				lqty = "0";
//			}
//
//			if (!itmDscnt.matches("")) {
//				double itemDiscountCalc = 0.0;
//				itemDiscountCalc = Double.parseDouble(itmDscnt);
//
//				double lPriceCalc = Double.parseDouble(lPrice);
//				double cPriceCalc = Double.parseDouble(cPrice);
//
//				double cqtyCalc = Double.parseDouble(cqty);
//				double lqtyCalc = Double.parseDouble(lqty);
//
//				tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc)
//						- itemDiscountCalc;
//
//				Log.d("ttl", "" + tt);
//				String Prodtotal = twoDecimalPoint(tt);
//				sl_total.setText("" + Prodtotal);
//
//				double taxAmount = 0.0, netTotal = 0.0;
//				if (!taxType.matches("") && !taxValue.matches("")) {
//
//					double taxValueCalc = Double.parseDouble(taxValue);
//
//					if (taxType.matches("E")) {
//
//						taxAmount = (tt * taxValueCalc) / 100;
//						String prodTax = fourDecimalPoint(taxAmount);
//						sl_tax.setText("" + prodTax);
//
//						netTotal = tt + taxAmount;
//						String ProdNetTotal = twoDecimalPoint(netTotal);
//						sl_netTotal.setText("" + ProdNetTotal);
//
//					} else if (taxType.matches("I")) {
//
//						taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
//						String prodTax = fourDecimalPoint(taxAmount);
//						sl_tax.setText("" + prodTax);
//
//						// netTotal = tt + taxAmount;
//						netTotal = tt;
//						String ProdNetTotal = twoDecimalPoint(netTotal);
//						sl_netTotal.setText("" + ProdNetTotal);
//
//					} else if (taxType.matches("Z")) {
//
//						sl_tax.setText("0.0");
//
//						// netTotal = tt + taxAmount;
//						netTotal = tt;
//						String ProdNetTotal = twoDecimalPoint(netTotal);
//						sl_netTotal.setText("" + ProdNetTotal);
//
//					} else {
//						sl_tax.setText("0.0");
//						sl_netTotal.setText("" + Prodtotal);
//					}
//				} else if (taxValue.matches("")) {
//					sl_tax.setText("0.0");
//					sl_netTotal.setText("" + Prodtotal);
//				} else {
//					sl_tax.setText("0.0");
//					sl_netTotal.setText("" + Prodtotal);
//				}
//			}
//
//		} catch (Exception e) {
//
//		}
//	}
//
//	public void productTotal(double qty) {
//
//		try {
//			double taxAmount = 0.0, netTotal = 0.0;
//			double taxAmount1 = 0.0, netTotal1 = 0.0;
//
//			if (slPrice.matches("")) {
//				slPrice = "0";
//			}
//
//			if (!slPrice.matches("")) {
//
//				double slPriceCalc = Double.parseDouble(slPrice);
//				String itmDscnt = sl_itemDiscount.getText().toString();
//				if (!itmDscnt.matches("")) {
//					tt = (qty * slPriceCalc);
//
//				} else {
//
//					tt = qty * slPriceCalc;
//
//				}
//
//				String Prodtotal = twoDecimalPoint(tt);
//
//				double subTotal = 0.0;
//
//				String itemDisc = sl_itemDiscount.getText().toString();
//				if (!itemDisc.matches("")) {
//					itmDisc = Double.parseDouble(itemDisc);
//					subTotal = tt - itmDisc;
//				} else {
//					subTotal = tt;
//				}
//
//				String sbTtl = twoDecimalPoint(subTotal);
//
//				sl_total.setText("" + sbTtl);
//
//				if (!taxType.matches("") && !taxValue.matches("")) {
//
//					double taxValueCalc = Double.parseDouble(taxValue);
//
//					if (taxType.matches("E")) {
//
//						if (!itemDisc.matches("")) {
//							taxAmount1 = (subTotal * taxValueCalc) / 100;
//							String prodTax = fourDecimalPoint(taxAmount1);
//							sl_tax.setText("" + prodTax);
//
//							netTotal1 = subTotal + taxAmount1;
//							String ProdNetTotal = twoDecimalPoint(netTotal1);
//							sl_netTotal.setText("" + ProdNetTotal);
//						} else {
//
//							taxAmount = (tt * taxValueCalc) / 100;
//							String prodTax = fourDecimalPoint(taxAmount);
//							sl_tax.setText("" + prodTax);
//
//							netTotal = tt + taxAmount;
//							String ProdNetTotal = twoDecimalPoint(netTotal);
//							sl_netTotal.setText("" + ProdNetTotal);
//						}
//
//					} else if (taxType.matches("I")) {
//						if (!itemDisc.matches("")) {
//							taxAmount1 = (subTotal * taxValueCalc)
//									/ (100 + taxValueCalc);
//							String prodTax = fourDecimalPoint(taxAmount1);
//							sl_tax.setText("" + prodTax);
//
////							netTotal1 = subTotal + taxAmount;
//							netTotal1 = subTotal;
//							String ProdNetTotal = twoDecimalPoint(netTotal1);
//							sl_netTotal.setText("" + ProdNetTotal);
//						} else {
//							taxAmount = (tt * taxValueCalc)
//									/ (100 + taxValueCalc);
//							String prodTax = fourDecimalPoint(taxAmount);
//							sl_tax.setText("" + prodTax);
//
////							netTotal = tt + taxAmount;
//							netTotal = tt;
//							String ProdNetTotal = twoDecimalPoint(netTotal);
//							sl_netTotal.setText("" + ProdNetTotal);
//						}
//
//					} else if (taxType.matches("Z")) {
//
//						sl_tax.setText("0.0");
//						if (!itemDisc.matches("")) {
////							netTotal1 = subTotal + taxAmount;
//							netTotal1 = subTotal;
//							String ProdNetTotal = twoDecimalPoint(netTotal1);
//							sl_netTotal.setText("" + ProdNetTotal);
//						} else {
////							netTotal = tt + taxAmount;
//							netTotal = tt;
//							String ProdNetTotal = twoDecimalPoint(netTotal);
//							sl_netTotal.setText("" + ProdNetTotal);
//						}
//
//					} else {
//						sl_tax.setText("0.0");
//						sl_netTotal.setText("" + Prodtotal);
//					}
//
//				} else if (taxValue.matches("")) {
//					sl_tax.setText("0.0");
//					sl_netTotal.setText("" + Prodtotal);
//				}else{
//					sl_tax.setText("0.0");
//					sl_netTotal.setText("" + Prodtotal);
//				}
//			}
//		} catch (Exception e) {
//
//		}
//
//	}
//
//	public void productTotalNew() {
//
//		try {
//			double taxAmount = 0.0, netTotal = 0.0;
//			double taxAmount1 = 0.0, netTotal1 = 0.0;
//
//			String lPrice = sl_price.getText().toString();
//			String cPrice = sl_cprice.getText().toString();
//			String cqty = sl_cartonQty.getText().toString();
//			String lqty = sl_looseQty.getText().toString();
//
//			if (lPrice.matches("")) {
//				lPrice = "0";
//			}
//
//			if (cPrice.matches("")) {
//				cPrice = "0";
//			}
//
//			if (cqty.matches("")) {
//				cqty = "0";
//			}
//
//			if (lqty.matches("")) {
//				lqty = "0";
//			}
//
//			double lPriceCalc = Double.parseDouble(lPrice);
//			double cPriceCalc = Double.parseDouble(cPrice);
//
//			double cqtyCalc = Double.parseDouble(cqty);
//			double lqtyCalc = Double.parseDouble(lqty);
//
//			tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc);
//
//			String Prodtotal = twoDecimalPoint(tt);
//
//			double subTotal = 0.0;
//
//			String itemDisc = sl_itemDiscount.getText().toString();
//			if (!itemDisc.matches("")) {
//				itmDisc = Double.parseDouble(itemDisc);
//				subTotal = tt - itmDisc;
//			} else {
//				subTotal = tt;
//			}
//
//			String sbTtl = twoDecimalPoint(subTotal);
//
//			sl_total.setText("" + sbTtl);
//
//			if (!taxType.matches("") && !taxValue.matches("")) {
//
//				double taxValueCalc = Double.parseDouble(taxValue);
//
//				if (taxType.matches("E")) {
//
//					if (!itemDisc.matches("")) {
//						taxAmount1 = (subTotal * taxValueCalc) / 100;
//						String prodTax = fourDecimalPoint(taxAmount1);
//						sl_tax.setText("" + prodTax);
//
//						netTotal1 = subTotal + taxAmount1;
//						String ProdNetTotal = twoDecimalPoint(netTotal1);
//						sl_netTotal.setText("" + ProdNetTotal);
//					} else {
//
//						taxAmount = (tt * taxValueCalc) / 100;
//						String prodTax = fourDecimalPoint(taxAmount);
//						sl_tax.setText("" + prodTax);
//
//						netTotal = tt + taxAmount;
//						String ProdNetTotal = twoDecimalPoint(netTotal);
//						sl_netTotal.setText("" + ProdNetTotal);
//					}
//
//				} else if (taxType.matches("I")) {
//					if (!itemDisc.matches("")) {
//						taxAmount1 = (subTotal * taxValueCalc)
//								/ (100 + taxValueCalc);
//						String prodTax = fourDecimalPoint(taxAmount1);
//						sl_tax.setText("" + prodTax);
//
//						// netTotal1 = subTotal + taxAmount1;
//						netTotal1 = subTotal;
//						String ProdNetTotal = twoDecimalPoint(netTotal1);
//						sl_netTotal.setText("" + ProdNetTotal);
//					} else {
//						taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
//						String prodTax = fourDecimalPoint(taxAmount);
//						sl_tax.setText("" + prodTax);
//
//						// netTotal = tt + taxAmount;
//						netTotal = tt;
//						String ProdNetTotal = twoDecimalPoint(netTotal);
//						sl_netTotal.setText("" + ProdNetTotal);
//					}
//
//				} else if (taxType.matches("Z")) {
//
//					sl_tax.setText("0.0");
//					if (!itemDisc.matches("")) {
//						// netTotal1 = subTotal + taxAmount;
//						netTotal1 = subTotal;
//						String ProdNetTotal = twoDecimalPoint(netTotal1);
//						sl_netTotal.setText("" + ProdNetTotal);
//					} else {
//						// netTotal = tt + taxAmount;
//						netTotal = tt;
//						String ProdNetTotal = twoDecimalPoint(netTotal);
//						sl_netTotal.setText("" + ProdNetTotal);
//					}
//
//				} else {
//					sl_tax.setText("0.0");
//					sl_netTotal.setText("" + Prodtotal);
//				}
//
//			} else if (taxValue.matches("")) {
//				sl_tax.setText("0.0");
//				sl_netTotal.setText("" + Prodtotal);
//			} else {
//				sl_tax.setText("0.0");
//				sl_netTotal.setText("" + Prodtotal);
//			}
//
//		} catch (Exception e) {
//
//		}
//	}
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
			String itmDscnt = sl_itemDiscount.getText().toString();
			String qty = sl_qty.getText().toString();
			String prc = sl_price.getText().toString();

			if (itmDscnt.matches("")) {
				itmDscnt = "0";
			}

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

		/////////////////
		String cpriceStr = sl_cprice.getText().toString();
		/////////////////

//		String stockadj_type = stock_adustment_type.getSelectedItem().toString();

		if (stockadj_code!=null && !stockadj_code.isEmpty()) {
		}else{
			stockadj_code = "";
		}
		Log.d("stockadj_type","ss "+stockadj_code);

		if (codeStr.matches("")) {
			Toast.makeText(SalesReturnAddProduct.this, "Select product code",
					Toast.LENGTH_SHORT).show();
		} else if(nameStr.matches("")){
			Toast.makeText(SalesReturnAddProduct.this, "Select product name",
					Toast.LENGTH_SHORT).show();
		}
		else if (calCarton.matches("1") && qtyStr.matches("")
				&& focStr.matches("")) {
			Toast.makeText(SalesReturnAddProduct.this, "Enter the quantity",
					Toast.LENGTH_SHORT).show();

		} else if (calCarton.matches("0") && cartonQtyStr.matches("")
				&& looseQtyStr.matches("") && qtyStr.matches("")) {
			Toast.makeText(SalesReturnAddProduct.this, "Enter the carton/quantity",
					Toast.LENGTH_SHORT).show();
		}else {

			if (!searchProductArr.isEmpty()) {
				carton_layout.setVisibility(View.GONE);
			}

			int foc = 0, cartonPerQty = 0;
			double price = 0, discount = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0,cartonQty = 0, looseQty = 0, qty = 0;
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
				foc = Integer.parseInt(focStr);
			}
			if (!cartonPerQtyStr.matches("")) {
				cartonPerQty = Integer.parseInt(cartonPerQtyStr);
			}
			if (!priceStr.matches("")) {
				price = Double.parseDouble(priceStr);
			}

			if (cpriceStr.matches("")) {
				cpriceStr = "0.00";
			}

			if (priceflag.matches("0")) {
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

				SOTDatabase.updateProduct(codeStr, nameStr, cartonQty,
						looseQty, qty, foc, price, discount, uomStr,
						cartonPerQty, tt + dis, tax, sbTtl, netT, i_sssno,cpriceStr,stockadj_code);

			} else {
				//			SOTDatabase.storeProduct(sl_no, codeStr, nameStr, cartonQty,
//						looseQty, qty, foc, price, discount, uomStr,
//						cartonPerQty, tt + dis, tax, netT, taxType, taxValue,
//						newPrice, sbTtl,cpriceStr,stockadj_code,"0","","","","","","");
//
//				sl_codefield.setText("");
//				sl_namefield.setText("");
//				sl_cartonQty.setText("");
//				sl_looseQty.setText("");
//				sl_qty.setText("");
//				sl_foc.setText("");
//				sl_itemDiscount.setText("");
//				sl_total.setText("0");
//				sl_total_inclusive.setText("0");
//				sl_tax.setText("0");
//				sl_netTotal.setText("0");
//				/////////
//				sl_cprice.setText("");
//				/////////
//				sl_price.setText("");
//				sl_uom.setText("");
//				sl_stock.setText("");
//				sl_cartonPerQty.setText("");
//
//				sl_codefield.requestFocus();
//
//				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//				imm.hideSoftInputFromWindow(
//						sl_codefield.getWindowToken(), 0);
//
//				sl_cartonQty.setEnabled(true);
//				sl_cartonQty.setFocusable(true);
//				sl_cartonQty.setBackgroundResource(drawable.edittext_bg);
//
//				sl_looseQty.setEnabled(true);
//				sl_looseQty.setFocusable(true);
//				sl_looseQty.setBackgroundResource(drawable.edittext_bg);

				String productid = SOTDatabase.getProductId(codeStr);

				if (productid != null && !productid.isEmpty()) {

					int i_sssno = Integer.parseInt(productid);
					SOTDatabase.updateProduct(codeStr, nameStr, cartonQty,
							looseQty, qty, foc, price, discount, uomStr,
							cartonPerQty, tt + dis, tax, sbTtl, netT, i_sssno,cpriceStr,stockadj_code);

					SOTDatabase.updateBillDisc(i_sssno, codeStr, sbTtl);

					final String productSlNo = SOTDatabase.getProdSlno(codeStr);
					String attributeProductCode =  SOTDatabase.getAttributeProduct(productSlNo,codeStr);
					if (attributeProductCode != null && !attributeProductCode.isEmpty()) {
						SOTDatabase.deleteAttributeProducts(productSlNo,codeStr);
						if(mAttributeArr.size()>0){
							SOTDatabase.storeAttribute(productSlNo,codeStr, nameStr,mAttributeArr);
						}
					}else {
						Log.d("mAttributeArr","-sAP->"+ mAttributeArr.size());

						if(mAttributeArr.size()>0){
							SOTDatabase.storeAttribute(productSlNo,codeStr, nameStr,mAttributeArr);
						}
					}
				} else {

					SOTDatabase.storeProduct(sl_no, codeStr, nameStr, cartonQty,
							looseQty, qty, foc, price, discount, uomStr,
							cartonPerQty, tt + dis, tax, netT, taxType, taxValue,
							newPrice, sbTtl,cpriceStr,stockadj_code,"0","","","","","","");

					Log.d("mAttributeArr","-sAP->"+ mAttributeArr.size());

					if(mAttributeArr.size()>0){
						SOTDatabase.storeAttribute(String.valueOf(sl_no),codeStr, nameStr,mAttributeArr);
					}
				}


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

	/*public void editCodeField() {

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

		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				SalesReturnAddProduct.this);
		editText = new EditText(SalesReturnAddProduct.this);
		final ListView listview = new ListView(SalesReturnAddProduct.this);
		LinearLayout layout = new LinearLayout(SalesReturnAddProduct.this);
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
				SalesReturnAddProduct.this, al);
		listview.setAdapter(arrayAdapterProd);
		listview.setOnItemClickListener(SalesReturnAddProduct.this);

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
						SalesReturnAddProduct.this, searchResults);
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

			loadprogress();
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

					sl_price.setText("");
					sl_uom.setText("");
					sl_stock.setText("");
					sl_cartonPerQty.setText("");
				}
			});
		}

	}

/*	private class AsyncCallWSADDPRD extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			al.clear();
			getActionBar().setHomeButtonEnabled(false);
			menu.setSlidingEnabled(false);
			spinnerLayout = new LinearLayout(SalesReturnAddProduct.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesproduct_layout, false);
			progressBar = new ProgressBar(SalesReturnAddProduct.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));

			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

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
						hashmap.putAll(producthm);
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
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);
			getActionBar().setHomeButtonEnabled(true);
			menu.setSlidingEnabled(true);
		}

	}

	private class AsyncCallBARCODE extends AsyncTask<Void, Void, Void> {
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

		}
	}*/
	private class CheckProductBarcode extends AsyncTask<Void, Void, Void> {

		String ed_code="";
		@Override
		protected void onPreExecute() {
			mBarcodeJsonString="";
			loadprogress();
			ed_code = sl_codefield.getText().toString();
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
			 new AsyncCallSaleProduct().execute();
			}else{
				sales_prodCode = ed_code;
				new AsyncCallSaleProduct().execute();
			}
			
		}


	}
		private class GetProduct extends AsyncTask<Void, Void, Void> {
			String grpcode_jsonString = null;
			JSONArray  stockadj_jsonMainNode;
			JSONObject stockadj_jsonResponse;
			@Override
			protected void onPreExecute() {
				al.clear();
				stockadjtype_List.clear();

				loadprogress();
			}

			@Override
			protected Void doInBackground(Void... arg0) {		
				mHashMap.put("CompanyCode", cmpnyCode);
				try {
					
					mProductJsonString = SalesOrderWebService.getSODetail(mHashMap, "fncGetProductForSearch");
					mProductJsonObject = new JSONObject(mProductJsonString);
					mProductJsonArray = mProductJsonObject.optJSONArray("SODetails");
					grpcode_jsonString = WebServiceClass
							.URLService("fncGetStockAdjustmentMaster");

					stockadj_jsonResponse = new JSONObject(grpcode_jsonString);
					stockadj_jsonMainNode = stockadj_jsonResponse
							.optJSONArray("JsonArray");

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

					HashMap<String, String> hasmap = new HashMap<String, String>();
					hasmap.put("Code","0");
					hasmap.put("Description","Select");
					stockadjtype_List.add(hasmap);
					/*********** Process each JSON Node grpcode ************/
					int lengthJsonArr1 = stockadj_jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr1; i++) {
						/****** Get Object for each JSON node. ***********/
						JSONObject grpcode_jsonChildNode;
						try {

							grpcode_jsonChildNode = stockadj_jsonMainNode
									.getJSONObject(i);

							String Code = grpcode_jsonChildNode.optString(
									"Code").toString();
							String Description = grpcode_jsonChildNode.optString(
									"Description").toString();
							HashMap<String, String> stadjhm = new HashMap<String, String>();
							stadjhm.put("Code", Code);
							stadjhm.put("Description", Description);
							stockadjtype_List.add(stadjhm);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
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

				if (stockadjtype_List.size() != 0) {
					stock_adustment_type.setAdapter(new MyCustomAdapter(
							SalesReturnAddProduct.this, R.layout.row, stockadjtype_List));
				}


				if ((str_ssupdate != null) || (str_sscancel != null)) {
					if (stockadj_type_edit.matches("-1")
							|| stockadj_type_edit.matches("null")
							|| stockadj_type_edit.matches("")) {
						stockadj_type_edit = "Select";
					}

					Log.d("adj size",""+stockadjtype_List.size());

					if(stockadjtype_List.size()>0){
						int select_pos = 0;
						for (int i = 0; i < stockadjtype_List.size(); i++) {

							Log.d("cod",stockadjtype_List.get(i).get("Code"));

							if (stockadjtype_List.get(i).get("Code").matches(stockadj_type_edit)) {
								select_pos = i;

							}
						}
						stock_adustment_type.setSelection(select_pos);
						Log.d("select_pos",""+select_pos);
					}
				}

				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesproduct_layout, true);
			}

		}


	public class MyCustomAdapter extends ArrayAdapter<HashMap<String,String>> {

		ArrayList<HashMap<String,String>> adapterList = new ArrayList<HashMap<String,String>>();

		public MyCustomAdapter(Context context, int textViewResourceId,
							   ArrayList<HashMap<String,String>> objects) {
			super(context, textViewResourceId, objects);
			adapterList.clear();
			adapterList = objects;
		}

		@Override
		public View getDropDownView(int position, View convertView,
									ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
								  ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);
			TextView label = (TextView) row.findViewById(R.id.locationspinner);
			ImageView icon = (ImageView) row.findViewById(R.id.spinnericon);
			TextView code_txt = (TextView) row.findViewById(R.id.code_txt);
			label.setText(adapterList.get(position).get("Description"));
			code_txt.setText(adapterList.get(position).get("Code"));
			icon.setVisibility(View.GONE);
			return row;
		}
	}
	private class AsyncCallSaleProduct extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

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
			sl_cartonPerQty.setText("");
			sl_total.setText("0");
			sl_total_inclusive.setText("0");
			sl_tax.setText("0");
			sl_netTotal.setText("0");
			//////
			sl_cprice.setText("");
			//////
			alBatchStock.clear();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			getSalesProdArr = SalesProductWebService.getSaleProduct(
					sales_prodCode, webMethName, "");

			if (cstgrpcd.matches("-1")) {
				cstgrpcd = "";
			}

			priceArr = SalesProductWebService
					.getProductPrice(cstgrpcd, cstcode, sales_prodCode,
							"fncGetProductPriceForSales", "SR");

			alBatchStock = SalesProductWebService
					.getProductBatchReturn(sales_prodCode,cstcode,"",
							"fncGetInvoiceBatchDetailByCustomer");

			colorArr = SalesProductWebService.getProductAttribute(
					sales_prodCode, "fncGetProductAttribute");
			
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

				//////////
				cprice = getSalesProdArr.get(4);
				//////////
//				taxValue = SalesOrderSetGet.getTaxValue();
				taxValue = getSalesProdArr.get(3);
				String haveBatch = getSalesProdArr.get(6);
				String haveExpiry = getSalesProdArr.get(7);
				String codefield = getSalesProdArr.get(8);
				String namefield = getSalesProdArr.get(9);
				Weight = getSalesProdArr.get(10);
				
				Log.d("Weight",""+ Weight);
				Log.d("haveBatch", ""+haveBatch);
				Log.d("haveExpiry", ""+haveExpiry);

				sl_codefield.setText(codefield);
				sl_namefield.setText(namefield);

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
//				if (offlineLayout.getVisibility() == View.GONE) {
					if (haveBatchOnStockIn.matches("True")) {
						Log.d("haveBatchOnStockIn", haveBatchOnStockIn);
						if (haveBatch.matches("True")
								|| haveExpiry.matches("True")) {
							Log.d("haveBatch", "haveExpiry");
							String code = sl_codefield.getText().toString();
							String name = sl_namefield.getText().toString();

							if (!alBatchStock.isEmpty()) {
								invoiceBatchDialog.initiateBatchPopupWindow(
										SalesReturnAddProduct.this, haveBatch,
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
//				} else {
//					Log.d("offline no batch", "offline no batch");
//					Log.d("slPrice","pp " +slPrice);
//					noBatchvalue();
//				}
			}
			else{
				sl_codefield.requestFocus();
				Toast.makeText(getApplicationContext(), " Invalid Code ",
						Toast.LENGTH_SHORT).show();
				sl_codefield.setText("");
				sl_namefield.setText("");
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
			Toast.makeText(SalesReturnAddProduct.this,
					"This product is already added",
					Toast.LENGTH_SHORT).show();

			clearData();

		} else {
			productModifierDialog.initiatePopupWindow(
					SalesReturnAddProduct.this, code, name, colorArr, sl_qty, screenInches);

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

						Log.d("slPrice",""+slPrice);
						Log.d("cprice",""+cprice);

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
						if (!sl_cprice.getText().toString().matches("")) {
							cpriceCheckZero = Double.parseDouble(sl_cprice.getText().toString());
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

	public void noBatchvalue(){
		if (newPrice.matches("0")) {
			sl_price.setText(slPrice);
		} else {
			sl_price.setText(newPrice);
		}
		//////
		if (newcprice.matches("0")) {
			sl_cprice.setText(cprice);
		} else {

			sl_cprice.setText(newcprice);
		}
		/////////
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

		progressBar.setVisibility(View.GONE);
		spinnerLayout.setVisibility(View.GONE);
		enableViews(salesproduct_layout, true);

		sl_cartonQty.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(sl_cartonQty,
				InputMethodManager.SHOW_IMPLICIT);
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

		adapter = new AddSimpleAdapter(
				SalesReturnAddProduct.this, searchProductArr,
				R.layout.sale_productitem,"", new String[] { "ProductCode",
						"ProductName", "WholeSalePrice" }, new int[] {
						R.id.txt_code, R.id.txt_name, R.id.txt_price });
		productFilterList.setAdapter(adapter);
	}
    private void loadprogress(){
    	spinnerLayout = new LinearLayout(SalesReturnAddProduct.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(salesproduct_layout, false);
		progressBar = new ProgressBar(SalesReturnAddProduct.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));

		spinnerLayout.addView(progressBar);
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

					stock = jsonChildNode.optString("Qty")
							.toString();
					
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

			if(stock!=null && !stock.isEmpty()){

            }else{
                stock="";
            }

			if (!sl_codefield.getText().toString().matches("")) {
					sl_stock.setText(" "+stock);
			}
		}
	}
	@Override
	public void onListItemClick(String item) {
		menu.toggle();
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
						Intent i = new Intent(SalesReturnAddProduct.this,
								SalesReturnHeader.class);
						startActivity(i);
						SalesReturnAddProduct.this.finish();

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
	@Override
	public void onBackPressed() {
		if ((str_ssupdate != null) || (str_sscancel != null)) {
			Intent i = new Intent(SalesReturnAddProduct.this,
					SalesReturnSummary.class);
			startActivity(i);
			SalesReturnAddProduct.this.finish();
		} else {
			Intent i = new Intent(SalesReturnAddProduct.this,
					SalesReturnCustomer.class);
			startActivity(i);
			SalesReturnAddProduct.this.finish();
		}
	}
}