package com.winapp.sot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.view.MenuItem;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.adapter.Attribute;
import com.winapp.catalog.CatalogProductDetailFragment;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterProd;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.NewProductWebService;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Catalog;
import com.winapp.helper.Constants;
import com.winapp.helper.XMLParser;
import com.winapp.model.Product;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.UIHelper;
import com.winapp.sot.FilterCS.OnFilterCompletionListener;
import com.winapp.sotdetails.DBCatalog;
import com.winapp.util.XMLAccessTask;
import com.winapp.zxing.SmallCaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class TransferAddProduct extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace, OnItemClickListener {

	private static String webMethName = "fncGetProduct";
	private static String webMethNamebar = "fncGetProductBarCode";
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String PRODUCT_NAME = "ProductName";
	private static final String PRODUCTNAME_BARCODE = "ProductCode";
	private static final String PRODUCT_BARCODE = "Barcode";
	private JSONObject product_stock_jsonResponse = null,
			mProductJsonObject = null, mBarcodeJsonObject = null;
	private JSONArray product_stock_jsonMainNode = null,
			mProductJsonArray = null, mBarcodeJsonArray = null;
	private HashMap<String, String> mHashMap;
	private SlidingMenu menu;
	private TextView listing_screen, customer_screen, addProduct_screen,
			summary_screen, textView1,oldqty,newqty;
	private ArrayList<HashMap<String, String>> searchProductArr = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> productStockArr = new ArrayList<HashMap<String, String>>();
	private Button sl_addProduct, sl_summary;
	private EditText editText1, sl_codefield, sl_namefield, sl_cartonQty,sl_namereceivefield,
			sl_looseQty, sl_qty, sl_uom, sl_cartonPerQty, sl_total, sl_tax,
			sl_netTotal;
	private AlertDialog myalertDialog = null;
	private ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	private EditText editText, sl_stock;
	private CustomAlertAdapterProd arrayAdapterProd;
	private ArrayList<HashMap<String, String>> searchResults;
	private int textlength = 0, sl_no = 1;
	private ArrayList<HashMap<String, String>> getArraylist = new ArrayList<HashMap<String, String>>();
	private ProgressBar progressBar;
	private LinearLayout spinnerLayout, salesproduct_layout, customer_layout;
	private ArrayList<String> getSalesProdArr = new ArrayList<String>();
	static ArrayList<String> companyArr = new ArrayList<String>();
	private Cursor cursor;
	private ListView productFilterList;
	private double netTtal = 0, taxAmount = 0;
	private Bundle extra;
	private double tt;
	private TextWatcher cqtyTW, lqtyTW, qtyTW;
	private ImageButton transferall;
	private FilterCS filtercs;
	private HashMap<String, String> producthashValue = new HashMap<String, String>();
	private String  mProductJsonString = "", mBarcodeJsonString = "", productTxt, productresult, barcoderesult,
			barcodeTxt, slPrice = "", slUomCode = "", slCartonPerQty = "",
			keyValues = "", values = "", taxType = "", taxValue = "",
			sales_prodCode, beforeLooseQty, beforeFoc, ss_Cqty = "",mflag = "",
			catStr = "", subCatStr = "", cstcode = "", cstgrpcd = "",
			str_ssupdate, str_sscancel, str_sssno,
			product_stock_jsonString = null, cmpnyCode = "", LocationCode = "",
			stock, fromlocation_code = "", calCarton = "",Weight="";
	private TransferBatchDialog transferBatchDialog;
	private ArrayList<HashMap<String, String>> alBatchStock = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter adapter;
	private LinearLayout sl_codelayout,catalog_layout,mGridLayout,mSearchLayout,load_more,grid_layout,carton_layout,
			slcodelayout,slcodereceivelayout;
	private ImageButton changelayout_ic,changeview;
	private double screenInches,d_oldqty;
	private int orientation;
	private GridView mGridView;
	private ListView mListView;
	private int mPosition = 0,totalItem,pageNo = 0,currentPage = -1;
	private EditText mEditValue;
	private View keyViewActionDone;
	private UIHelper helper;
	private ArrayList<Attribute> mAttributeArr;
	private CatalogProductDetailFragment catalogproductdetailfragment;
	private ProductMainImageAdapter mainImageAdapter;
	private ArrayList<Product> productList,loadmoreProduct;
	private HashMap<String, String> params;
	private boolean isScrollListener = false;
	private static final String KEY_PRODUCTCODE = "ProductCode";
	private static final String KEY_PRODUCTNAME = "ProductName";
	private static final String KEY_CATEGORYCODE = "CategoryCode";
	private static final String KEY_SUBCATEGORYCODE = "SubCategoryCode";
	private static final String KEY_UOMCODE = "UomCode";
	private static final String KEY_PCSPERCARTON = "PcsPerCarton";
	private static final String KEY_WHOLESALEPRICE = "WholeSalePrice";
	private static final String KEY_PRODUCTIMAGE = "ProductImage";
	private static final String KEY_RETAILPRICE = "RetailPrice";
	private static final String KEY_SPECIFICATION = "Specification";
	private static final String KEY_HAVEATTRIBUTE = "HaveAttribute";
	private String mkeyActionDoneStr = "", mCategory = "",
			mChangingView = "ListView", mSubcategory = "", valid_url = "",catalogSaveType = "",
			mCustomerGroupCode = "", jsonString = null, mDialogStatus = "",mCustomerCode = "",mProductName = "";
	public static TextView mTitle,mCartText;
	private String flag = "";
	private EditText ed_productsearch;
	private ImageButton back_icon;
	private JSONObject jsonResponse;
	private JSONArray jsonMainNode;
	public static ImageButton mChangeView,mCartIcon,mFilterIcon,mSearchIcon,mCartSaveIcon,mListingSearchIcon,mCartClearAll,mClose,mBack;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.transfer_addproduct);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.addproduct_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		transferall = (ImageButton) customNav.findViewById(R.id.transferall);
		ImageButton filter = (ImageButton) customNav.findViewById(R.id.filter);
		changelayout_ic = (ImageButton) customNav.findViewById(R.id.changelayout_ic);
		changeview = (ImageButton) customNav.findViewById(R.id.right_right_image_button);
		mCartIcon = (ImageButton) customNav.findViewById(R.id.right_button);
		mCartText = (TextView) customNav.findViewById(R.id.cart_txt);
		txt.setText("Add Product");
		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);

		ab.setDisplayHomeAsUpEnabled(false);
		menu = new SlidingMenu(TransferAddProduct.this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		// menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);

		filtercs = new FilterCS(TransferAddProduct.this);
		calCarton = LogOutSetGet.getCalcCarton();
		salesproduct_layout = (LinearLayout) findViewById(R.id.salesproduct_layout);
		sl_summary = (Button) findViewById(R.id.sl_summary);
		sl_addProduct = (Button) findViewById(R.id.sl_addProduct);

		sl_codefield = (EditText) findViewById(R.id.sl_codefield);
		sl_namefield = (EditText) findViewById(R.id.sl_namefield);
		sl_namereceivefield = (EditText) findViewById(R.id.sl_namereceivefield);
		sl_cartonQty = (EditText) findViewById(R.id.sl_cartonQty);
		sl_looseQty = (EditText) findViewById(R.id.sl_looseQty);
		sl_qty = (EditText) findViewById(R.id.sl_qty);
		sl_uom = (EditText) findViewById(R.id.sl_uom);
		sl_cartonPerQty = (EditText) findViewById(R.id.sl_cartonPerQty);

		sl_total = (EditText) findViewById(R.id.sl_total);
		sl_tax = (EditText) findViewById(R.id.sl_tax);
		sl_netTotal = (EditText) findViewById(R.id.sl_netTotal);
		productFilterList = (ListView) findViewById(R.id.productFilterList);
		sl_stock = (EditText) findViewById(R.id.sl_stock);
		ed_productsearch = (EditText) findViewById(R.id.ed_productsearch);
		back_icon = (ImageButton)findViewById(R.id.back_icon);
		back_icon.setVisibility(View.GONE);

		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen = (TextView) findViewById(R.id.sum_screen);
		oldqty = (TextView)findViewById(R.id.oldtxt);
		newqty = (TextView)findViewById(R.id.txt4);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);
		catalog_layout = (LinearLayout) findViewById(R.id.catalog_layout);
		mGridView = (GridView) findViewById(R.id.gridViewCustom);
		mListView = (ListView) findViewById(R.id.listView);
		sl_codelayout = (LinearLayout)findViewById(R.id.sl_codelayout);
		slcodelayout = (LinearLayout)findViewById(R.id.slcodelayout);
		slcodereceivelayout = (LinearLayout)findViewById(R.id.slcodereceivelayout);
		mGridLayout = (LinearLayout) findViewById(R.id.gridView_layout);
		mSearchLayout = (LinearLayout) findViewById(R.id.search_layout);
		load_more = (LinearLayout) findViewById(R.id.load_more);
		grid_layout = (LinearLayout)findViewById(R.id.grid_layout);
		carton_layout = (LinearLayout)findViewById(R.id.carton_layout);
		customer_layout.setVisibility(View.GONE);

		customer_screen.setText("Location");

		addProduct_screen.setTextColor(Color.parseColor("#FFFFFF"));
		addProduct_screen.setBackgroundResource(drawable.tab_select);

		if (transferall.getVisibility() == View.GONE) {
			transferall.setVisibility(View.VISIBLE);
		}

		transferBatchDialog = new TransferBatchDialog();
		valid_url = FWMSSettingsDatabase.getUrl();
		helper = new UIHelper(this);
		mAttributeArr = new ArrayList<>();
		productList = new ArrayList<>();
		params = new HashMap<String, String>();
		mainImageAdapter = new ProductMainImageAdapter();
		new SalesProductWebService(valid_url);
		new SalesOrderWebService(valid_url);
		new NewProductWebService(valid_url);
		SOTDatabase.init(TransferAddProduct.this);
		searchProductArr.clear();
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			mflag = extras.getString("flag");
			if (mflag.equalsIgnoreCase("ReceiveStock")) {

			} else {
			sales_prodCode = extras.getString("SOT_ssproductcode");

			ProductStockAsyncCall task = new ProductStockAsyncCall();
			task.execute();

			sl_codefield.setText(extras.getString("SOT_ssproductcode"));
			sl_namefield.setText(extras.getString("SOT_str_ssprodname"));
			sl_namereceivefield.setText(extras.getString("SOT_str_ssprodname"));
			sl_cartonQty.setText(extras.getString("SOT_str_sscq"));
			sl_looseQty.setText(extras.getString("SOT_str_sslq"));
			sl_qty.setText(extras.getString("SOT_str_ssqty"));
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
				sl_summary.setText(str_sscancel);
			}

			if (calCarton.matches("0")) {

			} else {
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

			}

			String titlevalue = extras.getString("title");
			if (titlevalue != null && !titlevalue.isEmpty()) {

			} else {
				titlevalue = "";
			}

			if (titlevalue.matches("Transfer")) {

				String sno_str = extras.getString("No");
				String date_str = extras.getString("Date");
				String custcode_str = extras.getString("customerCode");
				String custname_str = extras.getString("customerName");
			}
		}
		}

		searchProductArr = PreviewPojo.getSearchProductArr();

		if (!searchProductArr.isEmpty()){
			mflag = "ReceiveStock";
			oldqty.setVisibility(View.VISIBLE);
			newqty.setText("New Qty");
			filter.setVisibility(View.INVISIBLE);
			changelayout_ic.setVisibility(View.INVISIBLE);
			changeview.setVisibility(View.INVISIBLE);
			transferall.setVisibility(View.INVISIBLE);
			carton_layout.setVisibility(View.GONE);
			grid_layout.setVisibility(View.VISIBLE);
			slcodereceivelayout.setVisibility(View.VISIBLE);
			sl_namefield.setVisibility(View.GONE);
			sl_codefield.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);

		//	searchProductArr = PreviewPojo.getSearchProductArr();
			try {
				searchProductList("ReceiveStock");
			} catch (ParseException e) {

				e.printStackTrace();
			}
		}

		getSalesProdArr.clear();
		companyArr.clear();
		mHashMap = new HashMap<String, String>();

		Log.d("GRATaxT", SalesOrderSetGet.getCompanytax());
		taxType = SalesOrderSetGet.getCompanytax();

		cstcode = SalesOrderSetGet.getSuppliercode();
		cstgrpcd = SalesOrderSetGet.getSuppliergroupcode();
		cmpnyCode = SupplierSetterGetter.getCompanyCode();

		new GetProduct().execute();

		changelayout_ic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//ed_productsearch.setText("");
				if(sl_codelayout.getVisibility()==View.VISIBLE){
					sl_codelayout.setVisibility(View.GONE);
					catalog_layout.setVisibility(View.VISIBLE);
					changeview.setVisibility(View.VISIBLE);
					if(productList.size()>0){
						Cursor cursor = SOTDatabase.getCursor();
						int count = cursor.getCount();
						if (count > 0) {
							for (Product product:productList) {
								String pcode = product.getProductCode();
								Log.d("pcode", pcode);
								Double qty = SOTDatabase.getProductQty(pcode);
								Double cqty = SOTDatabase.getCartonQty(pcode);
								String quantity = String.valueOf(qty);
								String cartonqty = String.valueOf(cqty);
								Log.d("quantity", quantity);
								Log.d("cartonqty", cartonqty);
								if(quantity.matches("0.0")){
									product.setQty("1");
									product.setCqty("1");
								}else{
									product.setQty(quantity);
									product.setCqty(cartonqty);
								}
							}
						}
						screenInches = displayMetrics();
						orientation = getResources().getConfiguration().orientation;
						if (mChangingView.matches("GridView")) {
							changeview.setImageResource(R.mipmap.grid_1);
							mChangingView = "GridView";
							if (screenInches > 7) {
								if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
									// Landscape
									mGridView.setNumColumns(2);
								} else {
									// Portrait
									mGridView.setNumColumns(1);
								}
							} else {
								mGridView.setNumColumns(1);
							}
							mainImageAdapter = new ProductMainImageAdapter(
									TransferAddProduct.this, R.layout.transfer_listitem,
									productList);
						} else if (mChangingView.matches("ListView")) {
							changeview.setImageResource(R.mipmap.grid_2);
							mChangingView = "ListView";
							if (screenInches > 7) {
								if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
									// Landscape
									mGridView.setNumColumns(4);
								} else {
									// Portrait
									mGridView.setNumColumns(2);
								}
							} else {
								mGridView.setNumColumns(2);
							}
							mainImageAdapter = new ProductMainImageAdapter(
									TransferAddProduct.this, R.layout.transfer_grid_item,
									productList);
						}
						mGridView.setAdapter(mainImageAdapter);
						mGridView.setSelection(totalItem);
					}else{
						ed_productsearch.setText("");
						back_icon.setVisibility(View.GONE);
						params.put("ProductCode", "");
						params.put("CategoryCode", mCategory);
						params.put("SubCategoryCode", mSubcategory);
						params.put("CustomerGroupCode", mCustomerGroupCode);
						params.put("PageNo", String.valueOf(pageNo));
						params.put("CustomerCode", mCustomerCode);
						params.put("ProductName", mProductName);
						params.put("TranType", catalogSaveType);

						try {
							helper.showProgressView(catalog_layout);
							Log.d("loadProductMainImage >", "1");
							loadProductMainImage(params);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else{
					sl_codelayout.setVisibility(View.VISIBLE);
					catalog_layout.setVisibility(View.GONE);
					changeview.setVisibility(View.INVISIBLE);
				}
			}
		});

		Log.d("mGridView.getCount()", "-->"+mGridView.getCount());
		mGridView.setOnScrollListener(new InfiniteScrollListener(mGridView.getCount()) {
			@Override
			public void loadMore(int page, int totalItemsCount) {
				Log.d("totalItemsCount",""+totalItemsCount);
				if (isScrollListener) {
					Log.d(".....More.....","...........");
					load_more.setVisibility(View.VISIBLE);
					HashMap<String,String> params = new HashMap<String, String>();
					params.put("PageNo", String.valueOf(page));
					params.put("ProductCode", "");
					params.put("CategoryCode", mCategory);
					params.put("SubCategoryCode", mSubcategory);
					params.put("CustomerGroupCode", mCustomerGroupCode);
					params.put("CustomerCode", mCustomerCode);
					params.put("TranType", catalogSaveType);
					params.put("ProductName", mProductName);
					loadMoreProducts(params);
				}
			}

		});

		changeview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				screenInches = displayMetrics();
				orientation = getResources().getConfiguration().orientation;
				if (mChangingView.matches("GridView")) {
					changeview.setImageResource(R.mipmap.grid_2);
					mChangingView = "ListView";
					if (screenInches > 7) {
						if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
							// Landscape
							mGridView.setNumColumns(4);
						} else {
							// Portrait
							mGridView.setNumColumns(2);
						}
					} else {
						mGridView.setNumColumns(2);
					}
					mainImageAdapter = new ProductMainImageAdapter(
							TransferAddProduct.this, R.layout.transfer_grid_item,
							productList);
				} else if (mChangingView.matches("ListView")) {
					changeview.setImageResource(R.mipmap.grid_1);
					mChangingView = "GridView";
					if (screenInches > 7) {
						if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
							// Landscape
							mGridView.setNumColumns(2);
						} else {
							// Portrait
							mGridView.setNumColumns(1);
						}
					} else {
						mGridView.setNumColumns(1);
					}
					mainImageAdapter = new ProductMainImageAdapter(
							TransferAddProduct.this, R.layout.transfer_listitem,
							productList);
				}
				mGridView.setAdapter(mainImageAdapter);
				mGridView.setSelection(totalItem);
			}
		});

		ed_productsearch.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					Log.i("TAG","Enter pressed");
					new GetBarCode().execute();
				}
				return false;
			}
		});

		back_icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				back_icon.setVisibility(View.GONE);
				mProductName = "";
				ed_productsearch.setText("");
				hideKeyboard();
				pageNo = 0;
				currentPage = 0;
				params.put("ProductCode", "");
				params.put("CategoryCode", "");
				params.put("SubCategoryCode", "");
				params.put("CustomerGroupCode", mCustomerGroupCode);
				params.put("PageNo", String.valueOf(pageNo));
				params.put("CustomerCode", mCustomerCode);
				params.put("TranType", catalogSaveType);
				params.put("ProductName", mProductName);

				try {
					helper.showProgressView(catalog_layout);
					Log.d("loadProductMainImage >", "1");
					loadProductMainImage(params);

				} catch (Exception e) {
					e.printStackTrace();
				}
//				showView();
//				animCartIcon();
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
					Intent i = new Intent(TransferAddProduct.this,
							TransferHeader.class);
					startActivity(i);
					TransferAddProduct.this.finish();
				}

			}
		});

		customer_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(TransferAddProduct.this,
						TransferHeaderDetail.class);
				startActivity(i);
				TransferAddProduct.this.finish();

			}
		});

		summary_screen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent i = new Intent(TransferAddProduct.this,
						TransferSummary.class);
				startActivity(i);
				TransferAddProduct.this.finish();
			}
		});

		productFilterList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {

				sales_prodCode = searchProductArr.get(position).get(
						"ProductCode");
				String old_qty = searchProductArr.get(position).get(
						"Qty");
				d_oldqty = Double.parseDouble(old_qty);
//				int iqty = (int)d_oldqty;
				sl_codefield.setText(sales_prodCode);
				Log.d("d_oldqty", String.valueOf(d_oldqty));
//				sl_qty.setText(""+iqty);
				loadprogress();
				new AsyncCallSaleProduct("true").execute();
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

				if ((str_ssupdate != null) || (str_sscancel != null)) {
					Toast.makeText(TransferAddProduct.this, "Updated ",
							Toast.LENGTH_LONG).show();
					storeInDatabase();
					Intent i = new Intent(TransferAddProduct.this,
							TransferSummary.class);
					startActivity(i);
					TransferAddProduct.this.finish();
				} else {
					storeInDatabase();
				}

			}
		});

		sl_summary.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(TransferAddProduct.this,
						TransferSummary.class);
				startActivity(i);
				TransferAddProduct.this.finish();
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
					// editCodeField();
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
					} else {
						cartonQty();
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
				if (calCarton.matches("0")) {
					cartonQtyPcsOne(0);

					int length = sl_cartonQty.length();
					if (length == 0) {
						cartonQtyPcsOne(0);
					
					}
				} else {
					cartonQty();
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

							productTotal(lsQty);
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
					if (calCarton.matches("0")) {
						looseQtyCalcPcsOne(0);
					} else {
						looseQtyCalc();
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

				if (calCarton.matches("0")) {
					looseQtyCalcPcsOne(0);
					int length = sl_looseQty.length();
					if (length == 0) {
						looseQtyCalcPcsOne(0);
						}
				} else {
					looseQtyCalc();
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
							if (calCarton.matches("0")) {

							} else {
								looseQtyCalc();
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

					String qty = sl_qty.getText().toString();
					if (!qty.matches("")) {
						double qtyCalc = Double.parseDouble(qty);
						if (calCarton.matches("0")) {

						} else {
							clQty();
						}
						productTotal(qtyCalc);
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

				String qty = sl_qty.getText().toString();
				if (!qty.matches("")) {
					double qtyCalc = Double.parseDouble(qty);
					if (calCarton.matches("0")) {

					} else {
						clQty();
					}
					productTotal(qtyCalc);
				}

				int length = sl_qty.length();
				if (length == 0) {

					if (calCarton.matches("0")) {
						productTotal(0);
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
		};

		transferall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Toast.makeText(TransferAddProduct.this, "Transfer All",
				// Toast.LENGTH_SHORT).show();

				AsyncCallWSProductStock task = new AsyncCallWSProductStock();
				task.execute();

			}
		});

		sl_cartonQty.addTextChangedListener(cqtyTW);
		sl_looseQty.addTextChangedListener(lqtyTW);
		sl_qty.addTextChangedListener(qtyTW);

	}

	public void scanMarginScanner(View view) {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.setOrientationLocked(false);
		integrator.setCaptureActivity(SmallCaptureActivity.class);
		integrator.initiateScan();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if(result != null) {
			if(result.getContents() == null) {
				Log.d("MainActivityTransfer", "Cancelled scan");
				Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
			} else {
				Log.d("MainActivityTransfer", "Scanned");

				sl_codefield.setText(""+result.getContents());

				if(!result.getContents().matches("")){
					new CheckProductBarcode().execute();
				}
			}
		} else {
			// This is important, otherwise the result will not be passed to the fragment
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void hideKeyboard() {
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(ed_productsearch.getWindowToken(), 0);
	}

	public void clQty() {
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
				} else {
					sl_tax.setText("0.0");
					sl_netTotal.setText("" + Prodtotal);
				}
			}
		} catch (Exception e) {

		}
	}

	public String noDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#");
		df.setMinimumFractionDigits(0);
		String tot = df.format(d);

		return tot;
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
		String cartonQtyStr = sl_cartonQty.getText().toString();
		String qtyStr = sl_qty.getText().toString();

		if (codeStr.matches("")) {
			Toast.makeText(TransferAddProduct.this, "Select product code",
					Toast.LENGTH_SHORT).show();
		} else if (qtyStr.matches("")) {

			if (calCarton.matches("0")) {

				if (cartonQtyStr.matches("")) {
					Toast.makeText(TransferAddProduct.this,
							"Enter the carton/quantity", Toast.LENGTH_SHORT)
							.show();
					sl_cartonQty.requestFocus();
				} else {
					save();
				}

			} else {
				Toast.makeText(TransferAddProduct.this, "Enter the quantity",
						Toast.LENGTH_SHORT).show();
			}

		} else {
			save();

		}

		if (!searchProductArr.isEmpty()) {
			adapter.notifyDataSetChanged();
		}

	}

	public void save() {

		String codeStr = sl_codefield.getText().toString();
		String nameStr = "";
		if (mflag.equalsIgnoreCase("ReceiveStock")) {
			nameStr = sl_namereceivefield.getText().toString();
		}else{
			nameStr = sl_namefield.getText().toString();
		}
		String cartonQtyStr = sl_cartonQty.getText().toString();
		String looseQtyStr = sl_looseQty.getText().toString();
		String qtyStr = sl_qty.getText().toString();
		String uomStr = sl_uom.getText().toString();
		String cartonPerQtyStr = sl_cartonPerQty.getText().toString();
		String totalStr = sl_total.getText().toString();
		String taxStr = sl_tax.getText().toString();
		String netTotalStr = sl_netTotal.getText().toString();

		int cartonPerQty = 0;
		double price = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0,cartonQty = 0, looseQty = 0, qty = 0;
		String sbTtl = "";
		String netT = "";

		if (!slPrice.matches("")) {
			price = Double.parseDouble(slPrice);
		}

		if (!cartonQtyStr.matches("")) {
			cartonQty = Double.parseDouble(cartonQtyStr);
		}
		if (!looseQtyStr.matches("")) {
			looseQty = Double.parseDouble(looseQtyStr);
		}
		if (!qtyStr.matches("")) {
			qty = Double.parseDouble(qtyStr);
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

		if (!searchProductArr.isEmpty()) {
			carton_layout.setVisibility(View.GONE);
		}

		String addDetect="";
		if(d_oldqty==qty){
			addDetect = "0";
		}else if(d_oldqty<qty){
			addDetect = "-1";
		}else if(d_oldqty>qty){
			addDetect = "+1";
		}else{
			addDetect= "0";
		}

		Log.d("addDetect","at "+addDetect);

		if ((str_ssupdate != null) || (str_sscancel != null)) {
			int i_sssno = Integer.parseInt(str_sssno);

//			SOTDatabase.updateProduct(codeStr, nameStr, cartonQty, looseQty,
//					qty, 0, price, 0, uomStr, cartonPerQty, tt, tax, sbTtl,
//					netT, i_sssno, "0.00", "0");

			HashMap<String, String> queryValues = new HashMap<String, String>();
			queryValues.put("ProductCode", codeStr);
			queryValues.put("ProductName", nameStr);
			queryValues.put("CQty", cartonQty+"");
			queryValues.put("LQty", looseQty+"");
			queryValues.put("Qty", qty+"");
			queryValues.put("StockinHand", "0");
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

			Log.d("Price", "" + price);

			String productid = SOTDatabase.getProductId(codeStr);

			Log.d("productidcheck",productid);

			if (productid != null && !productid.isEmpty()) {

				int i_sssno = Integer.parseInt(productid);
//				SOTDatabase.updateProduct(codeStr, nameStr, cartonQty,
//						looseQty, qty, 0, price, 0, uomStr,
//						cartonPerQty, tt , tax, sbTtl, netT, i_sssno,
//						"0.00", "0");
//
//				SOTDatabase.updateBillDisc(i_sssno, codeStr, sbTtl);

				HashMap<String, String> queryValues = new HashMap<String, String>();
				queryValues.put("ProductCode", codeStr);
				queryValues.put("ProductName", nameStr);
				queryValues.put("CQty", cartonQty+"");
				queryValues.put("LQty", looseQty+"");
				queryValues.put("Qty", qty+"");
				queryValues.put("StockinHand", "0");
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


			}else{
//				SOTDatabase.storeProduct(sl_no, codeStr, nameStr, cartonQty,
//						looseQty, qty, 0, price, 0.0, uomStr, cartonPerQty, tt,
//						tax, netT, taxType, taxValue, "", sbTtl, "0.00", "0", "0",
//						"", "", "", "","","");

				HashMap<String, String> queryValues = new HashMap<String, String>();
				queryValues.put("slNo", sl_no+"");
				queryValues.put("ProductCode", codeStr);
				queryValues.put("ProductName", nameStr);
				queryValues.put("CQty", cartonQty+"");
				queryValues.put("LQty", looseQty+"");
				queryValues.put("Qty", qty+"");
				queryValues.put("StockinHand", "0");
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

			}






			sl_codefield.setText("");
			sl_namefield.setText("");
			sl_namereceivefield.setText("");
			sl_cartonQty.setText("");
			sl_looseQty.setText("");
			sl_qty.setText("");
			sl_total.setText("0");
			sl_tax.setText("0");
			sl_netTotal.setText("0");
			sl_uom.setText("");
			sl_stock.setText("");
			sl_cartonPerQty.setText("");

			sl_codefield.requestFocus();

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(sl_codefield.getWindowToken(), 0);

			sl_cartonQty.setEnabled(true);
			sl_cartonQty.setFocusable(true);
			sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

			sl_looseQty.setEnabled(true);
			sl_looseQty.setFocusable(true);
			sl_looseQty.setBackgroundResource(drawable.edittext_bg);

			sl_qty.setEnabled(true);
			sl_qty.setFocusable(true);
			sl_qty.setBackgroundResource(drawable.edittext_bg);

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

	public void alertDialogSearch() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				TransferAddProduct.this);
		editText = new EditText(TransferAddProduct.this);
		final ListView listview = new ListView(TransferAddProduct.this);
		LinearLayout layout = new LinearLayout(TransferAddProduct.this);
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

		arrayAdapterProd = new CustomAlertAdapterProd(TransferAddProduct.this,
				al);
		listview.setAdapter(arrayAdapterProd);
		listview.setOnItemClickListener(TransferAddProduct.this);

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
						TransferAddProduct.this, searchResults);
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
			sl_namereceivefield.setText(values);
			loadprogress();
			new AsyncCallSaleProduct("false").execute();

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
					sl_namereceivefield.setText("");
					sl_uom.setText("");
					sl_stock.setText("");
					sl_cartonPerQty.setText("");
				}
			});
		}

	}

	private class CheckProductBarcode extends AsyncTask<Void, Void, Void> {

		String ed_code = "";

		@Override
		protected void onPreExecute() {
			mBarcodeJsonString = "";
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			ed_code = sl_codefield.getText().toString();
			mHashMap.clear();
			mHashMap.put("CompanyCode", cmpnyCode);
			mHashMap.put("Barcode", ed_code);

			try {
				mBarcodeJsonString = SalesOrderWebService.getSODetail(mHashMap,
						webMethNamebar);
				Log.d("mBarcodeJsonString", "-->" + mBarcodeJsonString);
				mBarcodeJsonObject = new JSONObject(mBarcodeJsonString);
				mBarcodeJsonArray = mBarcodeJsonObject
						.optJSONArray("SODetails");
				int lengthJsonBarcodeArr = mBarcodeJsonArray.length();

				if (lengthJsonBarcodeArr > 0) {
					JSONObject jsonChildNode = mBarcodeJsonArray
							.getJSONObject(0);
					String productbarcode = jsonChildNode.optString(
							PRODUCTNAME_BARCODE).toString();
					String barcode = jsonChildNode.optString(PRODUCT_BARCODE)
							.toString();
					sales_prodCode = productbarcode;
				} else {
					mBarcodeJsonString = "";
				}
			} catch (JSONException e) {
				mBarcodeJsonString = "";
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mBarcodeJsonString != null && !mBarcodeJsonString.isEmpty()) {
				new AsyncCallSaleProduct("false").execute();
			} else {
				sales_prodCode = ed_code;
				new AsyncCallSaleProduct("false").execute();
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

				mProductJsonString = SalesOrderWebService.getSODetail(mHashMap,
						"fncGetProductForSearch");
				mProductJsonObject = new JSONObject(mProductJsonString);
				mProductJsonArray = mProductJsonObject
						.optJSONArray("SODetails");

				int lengthJsonProductArr = mProductJsonArray.length();

				if (lengthJsonProductArr > 0) {
					for (int i = 0; i < lengthJsonProductArr; i++) {

						JSONObject jsonChildNode = mProductJsonArray
								.getJSONObject(i);

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
			enableViews(salesproduct_layout, true);
			if (mflag.equalsIgnoreCase("ReceiveStock")) {
				changelayout_ic.setVisibility(View.INVISIBLE);
			}else{
				changelayout_ic.setVisibility(View.VISIBLE);
			}
		}
	}

	private class AsyncCallSaleProduct extends AsyncTask<Void, Void, Void> {
		String filterClick="", codefield = "" ;

		public AsyncCallSaleProduct(String fromFilterClick) {
			filterClick = fromFilterClick;
		}

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
			sl_uom.setText("");
			sl_stock.setText("");
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

			alBatchStock = SalesProductWebService.getProductBatchStock(
					sales_prodCode, "fncGetProductBatchStock");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			 new ProductStockAsyncCall().execute();

			if (!getSalesProdArr.isEmpty()) {

				carton_layout.setVisibility(View.VISIBLE);
				if (!searchProductArr.isEmpty()) {
					grid_layout.setVisibility(View.VISIBLE);
				}

				Log.d("getSalesProdArr", getSalesProdArr.toString());
				slPrice = getSalesProdArr.get(0);
				slUomCode = getSalesProdArr.get(1);
				slCartonPerQty = getSalesProdArr.get(2);
				taxValue = getSalesProdArr.get(3);

//				sl_uom.setText(slUomCode);
//				sl_cartonPerQty.setText(slCartonPerQty);

				String haveBatch = getSalesProdArr.get(4);
				String haveExpiry = getSalesProdArr.get(5);
				codefield = getSalesProdArr.get(6);
				String namefield = getSalesProdArr.get(7);
				Weight = getSalesProdArr.get(8);
				
				
				Log.d("haveBatch s", haveBatch);
				Log.d("haveExpiry s", haveExpiry);

				sl_codefield.setText(codefield);
				sl_namefield.setText(namefield);
				sl_namereceivefield.setText(namefield);

				HashMap<String, EditText> hm = new HashMap<String, EditText>();

				hm.put("Productcode", sl_codefield);
				if (mflag.equalsIgnoreCase("ReceiveStock")) {
					hm.put("Productname", sl_namereceivefield);
				}else{
					hm.put("Productname", sl_namefield);
				}
				hm.put("Cartonqty", sl_cartonQty);
				hm.put("Looseqty", sl_looseQty);
				hm.put("Qty", sl_qty);
				hm.put("Uom", sl_uom);
				hm.put("Stock", sl_stock);
				hm.put("Cartonperqty", sl_cartonPerQty);

				String haveBatchOnTransfer = SalesOrderSetGet
						.getHaveBatchOnTransfer();
				String haveBatchOnTransferToLocation = SalesOrderSetGet
						.getHaveBatchOnTransferToLocation();
				if (haveBatchOnTransfer != null
						&& !haveBatchOnTransfer.isEmpty()
						&& haveBatchOnTransfer.matches("True")
						|| haveBatchOnTransferToLocation != null
						&& !haveBatchOnTransferToLocation.isEmpty()
						&& haveBatchOnTransferToLocation.matches("True")) {
					Log.d("haveBatchOnStockIn", haveBatchOnTransfer);
					if (haveBatch.matches("True") || haveExpiry.matches("True")) {
						Log.d("haveBatch", "haveExpiry");
						String code = sl_codefield.getText().toString();
						String name = "";
						if (mflag.equalsIgnoreCase("ReceiveStock")) {
							name = sl_namereceivefield.getText().toString();
						}else{
							name = sl_namefield.getText().toString();
						}

						Log.d("alBatchStock d",
								"batch click" + alBatchStock.toString());

						if (!alBatchStock.isEmpty()) {
							transferBatchDialog.initiateBatchPopupWindow(
									TransferAddProduct.this, haveBatch,
									haveExpiry, code, name, slCartonPerQty,
									slPrice, hm, alBatchStock);
						} else {
							noBatchvalue();
						}

					} else {
						Log.d("no haveBatch", "no haveExpiry");
						noBatch();
					}

				} else {
					Log.d("no haveBatchOnStockIn", "no haveBatchOnStockIn");
					noBatch();
				}
			} else {
				sl_codefield.requestFocus();
				Toast.makeText(getApplicationContext(), " Invalid Code ",
						Toast.LENGTH_SHORT).show();
				sl_codefield.setText("");
				sl_namefield.setText("");
				sl_namereceivefield.setText("");
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
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);

		}
	}

	private void noBatchvalue() {

		sl_codefield.setText("");
		sl_namefield.setText("");
		sl_namereceivefield.setText("");
		sl_cartonQty.setText("");
		sl_looseQty.setText("");
		sl_qty.setText("");
		sl_uom.setText("");
		sl_stock.setText("");
		sl_cartonPerQty.setText("");

		Toast.makeText(TransferAddProduct.this, "No item to transfer",
				Toast.LENGTH_LONG).show();

		progressBar.setVisibility(View.GONE);
		spinnerLayout.setVisibility(View.GONE);
		enableViews(salesproduct_layout, true);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(sl_cartonQty.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(sl_qty.getWindowToken(), 0);
	}

	private void noBatch() {

		Log.d("noBatch", "Batch no");

		if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
				|| slCartonPerQty.matches("")) {

			if (calCarton.matches("0")) {

				sl_cartonQty.setFocusableInTouchMode(true);
				sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

				sl_looseQty.setFocusableInTouchMode(true);
				sl_looseQty.setBackgroundResource(drawable.edittext_bg);

				sl_qty.setFocusableInTouchMode(true);
				sl_qty.setBackgroundResource(drawable.edittext_bg);

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

			// if (calCarton.matches("0")) {
			sl_cartonQty.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(sl_cartonQty, 0);
			// } else {
			// sl_qty.requestFocus();
			// InputMethodManager imm = (InputMethodManager)
			// getSystemService(Context.INPUT_METHOD_SERVICE);
			// imm.showSoftInput(sl_qty, 0);
			// }

		}

		sl_uom.setText(slUomCode);
		sl_cartonPerQty.setText(slCartonPerQty);

		progressBar.setVisibility(View.GONE);
		spinnerLayout.setVisibility(View.GONE);
		enableViews(salesproduct_layout, true);
	}

	private class AsyncCallWSSearchProduct extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			loadprogress();
			searchProductArr.clear();
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
				searchProductList("");
			} catch (ParseException e) {

				e.printStackTrace();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);
		}
	}

	private class AsyncCallWSProductStock extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			productStockArr.clear();
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			// String fromLocation = SalesOrderSetGet.getTransferfromloc();
			HashMap<String, String> location_code_name = new HashMap<String, String>();
			location_code_name = SupplierSetterGetter.getLoc_code_name();
			String fromlocationname = SalesOrderSetGet.getTransferfromloc();
			String fromlocation_code = location_code_name.get(fromlocationname);

			Log.d("fromLocation", fromlocation_code);
			productStockArr = NewProductWebService.getProductStock(
					fromlocation_code, "fncGetProductWithStock");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (!productStockArr.isEmpty()) {

				// int slNo=1;
				for (int i = 0; i < productStockArr.size(); i++) {

					String ProductCode = productStockArr.get(i).get(
							"ProductCode");
					String ProductName = productStockArr.get(i).get(
							"ProductName");
					String Qty = productStockArr.get(i).get("Qty");
					String WholeSalePrice = productStockArr.get(i).get(
							"WholeSalePrice");
					String UOMCode = productStockArr.get(i).get("UOMCode");
					String PcsPerCarton = productStockArr.get(i).get(
							"PcsPerCarton");

					int qty_nt = 0, pcs_nt = 0;
					double price = 0;
					if (PcsPerCarton.matches("0")
							|| PcsPerCarton.matches("null")
							|| PcsPerCarton.matches("0.00")) {
						PcsPerCarton = "1";
					}

					if (!WholeSalePrice.matches("")) {
						price = Double.parseDouble(WholeSalePrice);
					}

					qty_nt = (int) (Double.parseDouble(Qty));

					int cartonqty = (int) (Double.parseDouble(Qty) / Double
							.parseDouble(PcsPerCarton));
					int lqty = (int) (Double.parseDouble(Qty) % Double
							.parseDouble(PcsPerCarton));
					// String looseqty = twoDecimalPoint(lqty);

					// if(qty_nt>0){

					Log.d("sl_no", "" + i);

					SOTDatabase.storeProduct(i + 1, ProductCode, ProductName,
							cartonqty, lqty, qty_nt, 0, price, 0.0, UOMCode,
							pcs_nt, 0.0, 0, "0", "0", "0", "", "0", "0.00",
							"0", "0", "", "", "", "","","");
					// }

					// slNo++;
				}

				Intent i = new Intent(TransferAddProduct.this,
						TransferSummary.class);
				startActivity(i);
				finish();

			} else {
				Toast.makeText(getApplicationContext(), "No item to transfer",
						Toast.LENGTH_SHORT).show();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesproduct_layout, true);
		}
	}

	public void searchProductList(String flag) throws ParseException {

		adapter = new AddSimpleAdapter(TransferAddProduct.this,
				searchProductArr, R.layout.sale_productitem,flag, new String[] {
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
			if (!sl_codefield.getText().toString().matches("")) {
				sl_stock.setText(" " + stock);
			}

		}
	}

	private void loadprogress() {
		spinnerLayout = new LinearLayout(TransferAddProduct.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(salesproduct_layout, false);
		progressBar = new ProgressBar(TransferAddProduct.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		spinnerLayout.addView(progressBar);
	}

	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Deleting");
		alertDialog
				.setMessage("Transfer products will clear. Do you want to proceed");
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(TransferAddProduct.this,
								TransferHeader.class);
						startActivity(i);
						TransferAddProduct.this.finish();

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
	public void onListItemClick(String item) {
		menu.toggle();
	}

	@Override
	public void onBackPressed() {
		if ((str_ssupdate != null) || (str_sscancel != null)) {
			Intent i = new Intent(TransferAddProduct.this,
					TransferSummary.class);
			startActivity(i);
			TransferAddProduct.this.finish();
		} else {
			if(mflag.equalsIgnoreCase("ReceiveStock")){
				Intent i = new Intent(TransferAddProduct.this,
						TransferHeader.class);
				startActivity(i);
				TransferAddProduct.this.finish();
			}else{
				Intent i = new Intent(TransferAddProduct.this,
						TransferHeaderDetail.class);
				startActivity(i);
				TransferAddProduct.this.finish();
			}
		}
	}

	public class ProductMainImageAdapter extends BaseAdapter implements
			Constants {

		private Activity mContext;
		private Product product;
		private LayoutInflater inflater;
		private List<Product> mProductList;
		private int resourceId;
		private String pcspercarton, priceflag = "";
		private ViewHolder holder;
		private DBCatalog dbhelper;
		private HashMap<String, String> productvalues;
		private double qtyFlag = 0, cartonFlag = 0;

		public ProductMainImageAdapter() {
		}

		public ProductMainImageAdapter(Activity context, int resourceId,
									   List<Product> productList) {
			mContext = context;
			dbhelper = new DBCatalog(context);
			this.mProductList = new ArrayList<Product>();
			this.productvalues = new HashMap<String, String>();
			mProductList.clear();
			this.mProductList = productList;

			this.resourceId = resourceId;
			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			priceflag = SalesOrderSetGet.getCartonpriceflag();

		}

		@Override
		public int getCount() {
			return mProductList.size();
		}

		@Override
		public Product getItem(int position) {
			return mProductList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
							ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				holder = new ViewHolder();
				row = inflater.inflate(resourceId, parent, false);

				holder.productCode = (TextView) row
						.findViewById(R.id.productCode);
				holder.productName = (TextView) row
						.findViewById(R.id.productName);
				holder.wholesalePrice = (TextView) row
						.findViewById(R.id.wholeSalePrice);
				holder.cartonPrice = (TextView) row
						.findViewById(R.id.cartonPrice);
				holder.productImage = (ImageView) row
						.findViewById(R.id.productImage);
				holder.qty = (EditText) row.findViewById(R.id.qty);
				holder.qtyMinus = (ImageView) row.findViewById(R.id.qty_minus);
				holder.qtyPlus = (ImageView) row.findViewById(R.id.qty_plus);
				holder.carton = (EditText) row.findViewById(R.id.carton);
				holder.cartonMinus = (ImageView) row
						.findViewById(R.id.carton_minus);
				holder.cartonPlus = (ImageView) row
						.findViewById(R.id.carton_plus);

				holder.add = (Button) row.findViewById(R.id.add);
				holder.carton_lbl = (TextView) row
						.findViewById(R.id.carton_lbl);
				holder.qty_lbl = (TextView) row.findViewById(R.id.qty_lbl);
				holder.loose = (TextView) row.findViewById(R.id.loose);
				holder.pcspercarton = (TextView) row
						.findViewById(R.id.pcspercarton);
				holder.ordered_qty = (TextView) row.findViewById(R.id.ordered_qty);
				holder.mOrderLayout = (LinearLayout) row.findViewById(R.id.orderLayout);
				holder.mAddToCartLayout = (LinearLayout) row.findViewById(R.id.addToCartLayout);
				holder.carton_layout = (LinearLayout) row.findViewById(R.id.carton_layout);
				holder.price = (TextView) row.findViewById(R.id.price);
				holder.order = (Button) row.findViewById(R.id.order);
				//holder.arrowUpDown = (ImageView)row.findViewById(R.id.arrow_image);
				row.setTag(holder);
			} else {
				holder = (ViewHolder) row.getTag();
			}

			product = getItem(position);
			row.setId(position);

			holder.qty.setId(position);
			holder.qtyMinus.setId(position);
			holder.qtyPlus.setId(position);

			holder.carton.setId(position);
			holder.cartonMinus.setId(position);
			holder.cartonPlus.setId(position);

			holder.add.setId(position);
			holder.productImage.setId(position);

			holder.order.setId(position);


			holder.productCode.setText(product.getProductCode());
			holder.productName.setText(product.getProductName());
			String price = product.getWholesalePrice();
			double dPrice=0.00,dCPrice = 0.00;
			if(price!=null && !price.isEmpty()){
				dPrice = Double.valueOf(price);
			}else{
				dPrice = 0.00;
			}
			holder.wholesalePrice.setText(twoDecimalPoint(dPrice));
			String retailprice = product.getRetailPrice();
			if(retailprice!=null && !retailprice.isEmpty()){
				dCPrice  = Double.valueOf(retailprice);
			}else{
				dCPrice = 0.00;
			}
			holder.cartonPrice.setText(twoDecimalPoint(dCPrice));
			holder.carton.setText(product.getCqty());
			holder.carton.setSelection(holder.carton.getText().length());
			holder.loose.setText(product.getLqty());
			holder.qty.setText(product.getQty());
			holder.qty.setSelection(holder.qty.getText().length());

			// taxValue = product.getTaxPerc();

			holder.pcspercarton.setText("PcsPerCarton :"
					+ product.getPcspercarton());


			pcspercarton = product.getPcspercarton();
			if (pcspercarton != null && !pcspercarton.isEmpty()) {
				if (Double.valueOf(pcspercarton) > 1) {
					holder.carton.setVisibility(View.VISIBLE);
					holder.carton_lbl.setVisibility(View.VISIBLE);
					holder.cartonMinus.setVisibility(View.VISIBLE);
					holder.cartonPlus.setVisibility(View.VISIBLE);
					holder.qty_lbl.setVisibility(View.VISIBLE);
					holder.carton_layout.setVisibility(View.VISIBLE);

				} else {
					holder.carton.setVisibility(View.GONE);
					holder.carton_lbl.setVisibility(View.GONE);
					holder.cartonMinus.setVisibility(View.GONE);
					holder.cartonPlus.setVisibility(View.GONE);
					holder.carton_layout.setVisibility(View.GONE);
				}
			}
			String showProductCode = SalesOrderSetGet.getSelfOrderShowProductCode();
			if (showProductCode != null && !showProductCode.isEmpty()) {
				if(showProductCode.matches("1")){
					holder.productCode.setVisibility(View.VISIBLE);
				}else{
					holder.productCode.setVisibility(View.GONE);
				}
			}
			else{
				holder.productCode.setVisibility(View.GONE);
			}
			String productCode = SOTDatabase.getProductCode(product
					.getProductCode());
			if (productCode != null && !productCode.isEmpty()) {

//				Double quantity = SOTDatabase.getProductQty(productCode);
//				Log.d("quantity", String.valueOf(quantity));
//				String qty = String.valueOf(quantity);
//				//product.setQty(qty);
//				holder.qty.setText(""+quantity);

				holder.add.setBackgroundResource(drawable.add_cart_selected);
				holder.add.setText("Added");
				holder.add.setTextColor(Color.parseColor("#FFFFFF"));
			} else {
				//holder.qty.setText("1");
				holder.add.setBackgroundResource(drawable.add_cart);
				holder.add.setText("Add");
				holder.add.setTextColor(Color.parseColor("#000000"));
			}

//			String showAddToCart = SalesOrderSetGet.getShowAddToCart();
//			if(showAddToCart!=null && !showAddToCart.isEmpty()){
//				if(showAddToCart.equalsIgnoreCase("0")){
//					holder.wholesalePrice.setVisibility(View.GONE);
//					holder.mOrderLayout.setVisibility(View.VISIBLE);
//					holder.mAddToCartLayout.setVisibility(View.GONE);
//					if(productCode != null && !productCode.isEmpty()){
//						holder.ordered_qty.setVisibility(View.VISIBLE);
//						holder.ordered_qty.setText(product.getQty());
//					}else{
//						holder.ordered_qty.setVisibility(View.INVISIBLE);
//					}
//				}else{
//					holder.mOrderLayout.setVisibility(View.GONE);
//					holder.wholesalePrice.setVisibility(View.VISIBLE);
//					holder.mAddToCartLayout.setVisibility(View.VISIBLE);
//				}
//			}else{
//				if(productCode != null && !productCode.isEmpty()){
//					holder.ordered_qty.setVisibility(View.VISIBLE);
//					holder.ordered_qty.setText(product.getQty());
//				}else{
//					holder.ordered_qty.setVisibility(View.INVISIBLE);
//				}
//				holder.wholesalePrice.setVisibility(View.GONE);
//				holder.mOrderLayout.setVisibility(View.VISIBLE);
//				holder.mAddToCartLayout.setVisibility(View.GONE);
//			}
			if (screenInches > 7) {
				if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
					if (mPosition == position) {
						if (mkeyActionDoneStr.matches("Qty")) {
							holder.qty
									.setBackgroundResource(drawable.edittext_focused);
							holder.carton
									.setBackgroundResource(drawable.edittext_normal);
						} else if (mkeyActionDoneStr.matches("CQty")) {
							holder.carton
									.setBackgroundResource(drawable.edittext_focused);
							holder.qty
									.setBackgroundResource(drawable.edittext_normal);
						}
					} else {
						holder.qty
								.setBackgroundResource(drawable.edittext_normal);
						holder.carton
								.setBackgroundResource(drawable.edittext_normal);
					}

				}
			}

			Bitmap bitmap = product.getBitmap();
			if(bitmap!=null){
				holder.productImage.setImageBitmap(bitmap);
			}else{
				holder.productImage.setImageResource(R.mipmap.no_image);
			}

//			holder.arrowUpDown.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//
//					if ( holder.carton_layout.getVisibility() == View.VISIBLE) {
//						holder.carton_layout.setVisibility(View.GONE);
//						holder.arrowUpDown.setImageResource(R.drawable.arrow_down);
//					} else {
//						holder.carton_layout.setVisibility(View.VISIBLE);
//						holder.arrowUpDown.setImageResource(R.drawable.arrow_up);
//					}
//				}
//			});

			holder.qty.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent motionevent) {
					EditText edQty = (EditText) v;
					orientation = getResources().getConfiguration().orientation;
					if (screenInches > 7) {
						if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
							mPosition = v.getId();
							hideKeyboard(edQty);
							mEditValue = edQty;
							mkeyActionDoneStr = "Qty";
							keyViewActionDone = v;
							edQty.setFocusable(false);
							edQty.setFocusableInTouchMode(false);
							notifyDataSetChanged();
						} else if (Configuration.ORIENTATION_PORTRAIT == orientation) {
							Log.d("PORTRAIT", "PORTRAIT");
							edQty.requestFocus();
							showKeyboard(edQty);
						}
					} else {
						Log.d("mobile", "mobile");
						edQty.requestFocus();
						showKeyboard(edQty);
					}
					return false;
				}
			});
			holder.qty.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
											  KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						qtyActionDone(v);
						return true;
					}
					return false;
				}
			});
			holder.qtyMinus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					qtyMinusAction(v);
				}
			});
			holder.qtyPlus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					qtyPlusAction(v);

				}
			});
			holder.qty.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent motionevent) {
					EditText edQty = (EditText) v;
					edQty.requestFocus();
					showKeyboard(edQty);
					edQty.addTextChangedListener(new QtyWatcher(v,edQty));

					return false;
				}
			});
			/*holder.carton.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent motionevent) {
					EditText edCarton = (EditText) v;
					edCarton.requestFocus();
					showKeyboard(edCarton);
					edCarton.addTextChangedListener(new CartonQtyWatcher(v,edCarton));

					return false;
				}
			});*/
			holder.loose.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent motionevent) {
					EditText edLoose = (EditText) v;
					edLoose.requestFocus();
					showKeyboard(edLoose);
					edLoose.addTextChangedListener(new LooseQtyWatcher(v,edLoose));

					return false;
				}
			});
			holder.carton.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent motionevent) {
					EditText edCarton = (EditText) v;
					orientation = getResources().getConfiguration().orientation;
					if (screenInches > 7) {
						if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
							mPosition = v.getId();
							hideKeyboard(edCarton);
							mEditValue = edCarton;
							mkeyActionDoneStr = "CQty";
							keyViewActionDone = v;
							edCarton.setFocusable(false);
							edCarton.setFocusableInTouchMode(false);
							notifyDataSetChanged();
						} else if (Configuration.ORIENTATION_PORTRAIT == orientation) {
							edCarton.setFocusable(true);
							edCarton.setFocusableInTouchMode(true);
							edCarton.requestFocus();
							edCarton.addTextChangedListener(new CartonQtyWatcher(v,edCarton));
							showKeyboard(edCarton);

						}
					} else {
						edCarton.requestFocus();
						showKeyboard(edCarton);
						edCarton.addTextChangedListener(new CartonQtyWatcher(v,edCarton));
					}
					return false;
				}
			});
			holder.carton
					.setOnEditorActionListener(new OnEditorActionListener() {

						@Override
						public boolean onEditorAction(TextView v, int actionId,
													  KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_DONE) {
								cartonActionDone(v);
								return true;
							}
							return false;
						}
					});

			holder.cartonMinus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					cartonMinusAction(v);
				}
			});

			holder.cartonPlus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					cartonPlusAction(v);
				}
			});
			holder.order.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//addToCart(v,false);
				}
			});
			holder.add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {

					addToCart(v,true);
					//save();
				}
			});

			holder.productImage.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
//					String haveAttributeFlag = SalesOrderSetGet.getHaveAttribute();
//					if (haveAttributeFlag.matches("2")) {
//						int position = v.getId();
//						product = getItem(position);
//						String haveAttribute = product.getHaveAttribute();
//						if (haveAttribute.equalsIgnoreCase("1")) {
//							String productCode = SOTDatabase
//									.getProductCode(product.getProductCode());
//							if (productCode != null && !productCode.isEmpty()) {
//								Toast.makeText(
//										mContext,
//										"This product is already added to the cart",
//										Toast.LENGTH_SHORT).show();
//							} else {
//								String code = product.getProductCode();
//								String name = product.getProductName();
//								String productImage = product.getProductImage();
//								new GetProductAttribute(v, false, code, name, productImage).execute();
//							}
//						} else {
//							//launchActivity(v);
//						}
//					}else {
//						//launchActivity(v);
//					}

				}
			});
			String haveAttributeFlag = SalesOrderSetGet.getHaveAttribute();
			if (haveAttributeFlag.matches("2")) {
				String haveAttribute = product.getHaveAttribute();
//				if(!haveAttribute.equalsIgnoreCase("0")){
//					holder.carton.setEnabled(false);
//					holder.loose.setEnabled(false);
//					holder.qty.setEnabled(false);
//					holder.cartonMinus.setImageResource(R.mipmap.inactive_minus);
//					holder.cartonPlus.setImageResource(R.mipmap.inactive_plus);
//					holder.qtyPlus.setImageResource(R.mipmap.inactive_plus);
//					holder.qtyMinus.setImageResource(R.mipmap.inactive_minus);
//					holder.cartonMinus.setEnabled(false);
//					holder.cartonPlus.setEnabled(false);
//					holder.qtyPlus.setEnabled(false);
//					holder.qtyMinus.setEnabled(false);
//				}
			}

			return row;

		}

		class ViewHolder {
			private TextView productCode,productName, carton_lbl, wholesalePrice,
					cartonPrice, qty_lbl, loose, pcspercarton,ordered_qty,price;

			private ImageView productImage, qtyMinus, qtyPlus, cartonMinus,
					cartonPlus;
			private EditText qty, carton;
			//private ImageView arrowUpDown;
			// private ImageButton qtyMinus, qtyPlus, cartonMinus, cartonPlus;
			private LinearLayout mOrderLayout,mAddToCartLayout,carton_layout;
			private Button add,order;

		}
		private void addToCart(View v,boolean isAddBtn){
			Log.d("addToCart","addToCart");
			flag = "Catalog";
			String customercode = Catalog.getCustomerCode();

			//if (customercode != null && !customercode.isEmpty()) {

			final Button b = (Button) v;
			int position = v.getId();
			product = getItem(position);

			String productCode = SOTDatabase
					.getProductCode(product.getProductCode());
			if (productCode != null && !productCode.isEmpty()) {
				Toast.makeText(
						mContext,
						"This product is already added to the cart",
						Toast.LENGTH_SHORT).show();
			} else {
				String haveAttributeFlag = SalesOrderSetGet.getHaveAttribute();
				if(haveAttributeFlag!=null && !haveAttributeFlag.isEmpty()){
					if(haveAttributeFlag.equalsIgnoreCase("2")){
						String haveAttribute = product.getHaveAttribute();
						if(haveAttribute!=null && !haveAttribute.isEmpty()){
							if(haveAttribute.equalsIgnoreCase("1")){
//								String code = product.getProductCode();
//								String name = product.getProductName();
//								String productImage = product.getProductImage();
//								new GetProductAttribute(v,isAddBtn,code,name,productImage).execute();
								storeInDb(position,v);
							}else{
								String showAddToCart = SalesOrderSetGet.getShowAddToCart();
								if(showAddToCart!=null && !showAddToCart.isEmpty()) {
									if (showAddToCart.equalsIgnoreCase("0")) {
										try {
											Log.d("showAddToCart","showAddToCart 0");
											//storeInDatabase();
											storeInDb(position,v);
											//launchActivity(v);
										} catch (Exception e) {
											e.printStackTrace();
										}

									}else{
										if ((product.getCqty() != null && !product
												.getCqty().isEmpty())
												&& (product.getQty() != null && !product
												.getQty().isEmpty())) {

											if ((Double.valueOf(product.getCqty()) > 0)
													|| (0 < Double.valueOf(product
													.getQty()))) {

												totalCalc(v);
												animCartIcon();
												if(isAddBtn){
													b.setText("Added");
													b.setBackgroundResource(drawable.add_cart_selected);
													b.setTextColor(Color.parseColor("#FFFFFF"));
												}


											} else {

												Log.d("product.getCqty()",
														"" + product.getCqty());

												Log.d("product.getQty()",
														"" + product.getQty());
												Toast.makeText(mContext,
														"Please Enter the Value",
														Toast.LENGTH_SHORT).show();
											}
										} else {
											Log.d("product.getCqty()",
													"null" + product.getCqty());

											Log.d("product.getQty()",
													"null" + product.getQty());

											Toast.makeText(mContext,
													"Please Enter the Value",
													Toast.LENGTH_SHORT).show();
										}

									}
								}else{
									try {
										Log.d("showAddToCart","showAddToCart else");
										storeInDb(position,v);
										//launchActivity(v);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}else{
							if ((product.getCqty() != null && !product
									.getCqty().isEmpty())
									&& (product.getQty() != null && !product
									.getQty().isEmpty())) {

								if ((Double.valueOf(product.getCqty()) > 0)
										|| (0 < Double.valueOf(product
										.getQty()))) {

									totalCalc(v);
									animCartIcon();
									if(isAddBtn){
										b.setText("Added");
										b.setBackgroundResource(drawable.add_cart_selected);
										b.setTextColor(Color.parseColor("#FFFFFF"));
									}


								} else {

									Log.d("product.getCqty()",
											"" + product.getCqty());

									Log.d("product.getQty()",
											"" + product.getQty());
									Toast.makeText(mContext,
											"Please Enter the Value",
											Toast.LENGTH_SHORT).show();
								}
							} else {
								Log.d("product.getCqty()",
										"null" + product.getCqty());

								Log.d("product.getQty()",
										"null" + product.getQty());

								Toast.makeText(mContext,
										"Please Enter the Value",
										Toast.LENGTH_SHORT).show();
							}

						}
					}else{
						if ((product.getCqty() != null && !product
								.getCqty().isEmpty())
								&& (product.getQty() != null && !product
								.getQty().isEmpty())) {

							if ((Double.valueOf(product.getCqty()) > 0)
									|| (0 < Double.valueOf(product
									.getQty()))) {

								totalCalc(v);
								animCartIcon();
								if(isAddBtn){
									b.setText("Added");
									b.setBackgroundResource(drawable.add_cart_selected);
									b.setTextColor(Color.parseColor("#FFFFFF"));
								}
							} else {

								Log.d("product.getCqty()",
										"" + product.getCqty());

								Log.d("product.getQty()",
										"" + product.getQty());
								Toast.makeText(mContext,
										"Please Enter the Value",
										Toast.LENGTH_SHORT).show();
							}
						} else {
							Log.d("product.getCqty()",
									"null" + product.getCqty());

							Log.d("product.getQty()",
									"null" + product.getQty());

							Toast.makeText(mContext,
									"Please Enter the Value",
									Toast.LENGTH_SHORT).show();
						}

					}
				}else{
					if ((product.getCqty() != null && !product
							.getCqty().isEmpty())
							&& (product.getQty() != null && !product
							.getQty().isEmpty())) {

						if ((Double.valueOf(product.getCqty()) > 0)
								|| (0 < Double.valueOf(product
								.getQty()))) {

							totalCalc(v);
							animCartIcon();
							if(isAddBtn){
								b.setText("Added");
								b.setBackgroundResource(drawable.add_cart_selected);
								b.setTextColor(Color.parseColor("#FFFFFF"));
							}


						} else {

							Log.d("product.getCqty()",
									"" + product.getCqty());

							Log.d("product.getQty()",
									"" + product.getQty());
							Toast.makeText(mContext,
									"Please Enter the Value",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Log.d("product.getCqty()",
								"null" + product.getCqty());

						Log.d("product.getQty()",
								"null" + product.getQty());

						Toast.makeText(mContext,
								"Please Enter the Value",
								Toast.LENGTH_SHORT).show();
					}
				}
			}

			//} else {
			//mPager.setCurrentItem(0);
			//	}

		}

		private void storeInDb(int position,View view) {
			cursor = SOTDatabase.getCursor();

			if (cursor != null && cursor.getCount() > 0) {
				sl_no = cursor.getCount();
				sl_no++;
			}

			product = getItem(position);

			String codeStr = product.getProductCode();
			String cartonQtyStr = product.getCqty();
			String qtyStr = product.getQty();

			Log.d("cartonQtyStr",cartonQtyStr);
			Log.d("qtyStr",qtyStr);

			if (codeStr.matches("")) {
				Toast.makeText(TransferAddProduct.this, "Select product code",
						Toast.LENGTH_SHORT).show();
			} else if (qtyStr.matches("0.0") || qtyStr.matches("0.00")) {

				if (calCarton.matches("0")) {

					if (cartonQtyStr.matches("")) {
						Toast.makeText(TransferAddProduct.this,
								"Enter the carton/quantity", Toast.LENGTH_SHORT)
								.show();
						sl_cartonQty.requestFocus();
					} else {
						//save();
						saveInDb(position,view);
					}

				} else {
					Toast.makeText(TransferAddProduct.this, "Enter the quantity",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				saveInDb(position,view);

			}

			if (!searchProductArr.isEmpty()) {
				adapter.notifyDataSetChanged();
			}
		}

		private void saveInDb(int position,View view) {

			final Button b = (Button) view;

			product = getItem(position);
			String codeStr = product.getProductCode();
			String nameStr = product.getProductName();
			String cartonQtyStr = product.getCqty();
			String looseQtyStr = product.getLqty();
			String qtyStr = product.getQty();
			String uomStr = product.getUom();
			String cartonPerQtyStr = product.getPcspercarton();
			//	String totalStr = product.getTotal();
			//String taxStr = product.getTax();
			//String netTotalStr = product.getNetTotal();

			int cartonPerQty = 0;
			double price = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0,cartonQty = 0, looseQty = 0, qty = 0,pcs = 0;
			String sbTtl = "";
			String netT = "";

			if (!slPrice.matches("")) {
				price = Double.parseDouble(slPrice);
			}

			if (!cartonQtyStr.matches("")) {
				cartonQty = Double.parseDouble(cartonQtyStr);
			}
			if (!looseQtyStr.matches("")) {
				looseQty = Double.parseDouble(looseQtyStr);
			}
			if (!qtyStr.matches("")) {
				qty = Double.parseDouble(qtyStr);
			}

			if (!cartonPerQtyStr.matches("")) {
				cartonPerQty = Integer.parseInt(cartonPerQtyStr);
			}

//			if (!totalStr.matches("")) {
//				total = Double.parseDouble(totalStr);
//
//				subTotal = total;
//
//				sbTtl = twoDecimalPoint(subTotal);
//
//				if ((str_ssupdate != null) || (str_sscancel != null)) {
//					int i_sssno = Integer.parseInt(str_sssno);
//					SOTDatabase.updateBillDisc(i_sssno, codeStr, sbTtl);
//				} else {
//					SOTDatabase.storeBillDisc(sl_no, codeStr, sbTtl);
//				}
//
//			}
//			if (!taxStr.matches("")) {
//				tax = Double.parseDouble(taxStr);
//			}
//			if (!netTotalStr.matches("")) {
//				ntTot = subTotal + tax;
//
//				netT = twoDecimalPoint(ntTot);
//			}

			if (taxValue.matches("") || taxValue == null) {
				taxValue = "0";
			}

			if ((str_ssupdate != null) || (str_sscancel != null)) {
				int i_sssno = Integer.parseInt(str_sssno);

				SOTDatabase.updateProduct(codeStr, nameStr, cartonQty, looseQty,
						qty, 0, price, 0, uomStr, cartonPerQty, tt, tax, sbTtl,
						netT, i_sssno, "0.00", "0");

			} else {

				Log.d("Price", "" + price);

				SOTDatabase.storeProduct(sl_no, codeStr, nameStr, cartonQty,
						looseQty, qty, 0, price, 0.0, uomStr, cartonPerQty, tt,
						tax, netT, taxType, taxValue, "", sbTtl, "0.00", "0", "0",
						"", "", "", "","","");

				Toast.makeText(TransferAddProduct.this,"Added Successfully",Toast.LENGTH_SHORT).show();

				b.setText("Added");
				b.setBackgroundResource(drawable.add_cart_selected);
				b.setTextColor(Color.parseColor("#FFFFFF"));

				sl_codefield.setText("");
				sl_namefield.setText("");
				sl_namereceivefield.setText("");
				sl_cartonQty.setText("");
				sl_looseQty.setText("");
				sl_qty.setText("");
				sl_total.setText("0");
				sl_tax.setText("0");
				sl_netTotal.setText("0");
				sl_uom.setText("");
				sl_stock.setText("");
				sl_cartonPerQty.setText("");

				sl_codefield.requestFocus();

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(sl_codefield.getWindowToken(), 0);

				sl_cartonQty.setEnabled(true);
				sl_cartonQty.setFocusable(true);
				sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

				sl_looseQty.setEnabled(true);
				sl_looseQty.setFocusable(true);
				sl_looseQty.setBackgroundResource(drawable.edittext_bg);

				sl_qty.setEnabled(true);
				sl_qty.setFocusable(true);
				sl_qty.setBackgroundResource(drawable.edittext_bg);

			}
		}

		private class GetProductAttribute extends AsyncTask<String, String, String> {
			private ArrayList<HashMap<String, String>> colorArr;
			private View v;
			private String result = "",productCode = "",productName="",productImage="";
			private boolean isAddBtn;
			public GetProductAttribute(View v,boolean isAddBtn,String productCode,String productName,String productImage){
				mAttributeArr.clear();
				this.v = v;
				this.isAddBtn = isAddBtn;
				this.productCode = productCode;
				this.productName = productName;
				this.productImage = productImage;
				this.colorArr = new ArrayList<HashMap<String, String>>();
			}
			@Override
			protected void onPreExecute() {
				helper.showProgressView(catalog_layout);
			}
			@Override
			protected String doInBackground(String... params) {
				colorArr = SalesProductWebService.getProductAttribute(
						productCode, "fncGetProductAttribute");
				return result;
			}


			@Override
			protected void onPostExecute(String result) {
				helper.dismissProgressView(catalog_layout);
				if(colorArr.size()>0){
					ColorAttributeDialog  productModifierDialog = new ColorAttributeDialog();
					productModifierDialog.setProductImage(productImage);
					productModifierDialog.initiatePopupWindow(
							TransferAddProduct.this,productCode,productName,colorArr,null,0);
					productModifierDialog.show(getFragmentManager(), "dialog");

					productModifierDialog.setOnCompletionListener(new ColorAttributeDialog.OnCompletionListener() {
						@Override
						public void onCompleted(String qty,ArrayList<Attribute> attributeArr) {
							attributeQtyCalc(v,qty,isAddBtn,attributeArr);

						}
					});
				}

			}
		}
		private void attributeQtyCalc(View v,String quantity,boolean isAddBtn,ArrayList<Attribute> attributeArr){

			double cqty = 0, qty = 0, pcs = 0, lqty = 0;
			mAttributeArr = attributeArr;
			Log.d("mProductAttributeArr","-->"+mAttributeArr.size());
			product.setQty(quantity);
			String pcspercarton = product.getPcspercarton();
			try {
				if (pcspercarton != null && !pcspercarton.isEmpty()){
					pcs = Double.valueOf(pcspercarton);
				}
				if (quantity != null && !quantity.isEmpty()){
					qty = Double.valueOf(quantity);
				}
				if (qty > 0) {

					cqty = qty / pcs;
					lqty = qty % pcs;

					product.setLqty(String.valueOf(lqty));
					product.setCqty(String.valueOf(cqty).split("\\.")[0]);
					product.setQty(String.valueOf(qty));

				}
				totalCalc(v);
			//	animCartIcon();
				//if(isAddBtn){
					Button b = (Button) v;
					b.setText(R.string.added);
				//}
				helper.showLongToast(R.string.added);
				notifyDataSetChanged();

			} catch (Exception e) {
				e.printStackTrace();
			} catch (StackOverflowError e) {
				e.printStackTrace();
			}

		}
		private class QtyWatcher implements TextWatcher{
			EditText edQty;
			private View view;
			private QtyWatcher(View view,EditText edQty ) {
				this.view = view;
				this.edQty = edQty;
			}

			@Override
			public void afterTextChanged(Editable s) {
				try{
					String qtyString = s.toString().trim();
					//	String qtyStr = s.toString().trim().split("\\.")[0];
					double quantity = qtyString.equals("") ? 0:Double.valueOf(qtyString);
					int position = view.getId();
					product = getItem(position);
					double qty = product.getQty().equals("") ? 0:Double.valueOf(product.getQty());
					if(quantity!=qty){
						product.setQty(String.valueOf(quantity));
						notifyDataSetChanged();
						priceflag = SalesOrderSetGet.getCartonpriceflag();
						if (priceflag.matches("1")) {
							qtyCalc(view);
						}else{

						}



					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
										  int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub

			}

		}
		private class CartonQtyWatcher implements TextWatcher{
			EditText edCQty;
			private View view;
			private CartonQtyWatcher(View view,EditText edCQty) {
				this.view = view;
				this.edCQty = edCQty;
			}

			@Override
			public void afterTextChanged(Editable s) {
				try{
					String cqtyString = s.toString().trim();
					double cquantity = cqtyString.equals("") ? 0:Double.valueOf(cqtyString);
					int position = view.getId();
					product = getItem(position);
					double cqty = product.getCqty().equals("") ? 0:Double.valueOf(product.getCqty());
					if(cquantity!=cqty){
						product.setCqty(String.valueOf(cquantity).split("\\.")[0]);
						notifyDataSetChanged();
						cQtyCalc(view);

					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
										  int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub

			}

		}
		private class LooseQtyWatcher implements TextWatcher{
			EditText edLQty;
			private View view;
			private LooseQtyWatcher(View view,EditText edLQty) {
				this.view = view;
				this.edLQty = edLQty;
			}

			@Override
			public void afterTextChanged(Editable s) {
				try{
					String lqtyString = s.toString().trim();
					double lquantity = lqtyString.equals("") ? 0:Double.valueOf(lqtyString);
					int position = view.getId();
					product = getItem(position);
					double lqty = product.getLqty().equals("") ? 0:Double.valueOf(product.getLqty());
					if(lquantity!=lqty){
						product.setLqty(String.valueOf(lquantity));
						notifyDataSetChanged();
						cQtyCalc(view);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
										  int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub

			}

		}
		public void decodeBase64File(String imageString,ImageView imageView) {
			try{
				byte[] encodeByte = Base64.decode(imageString, Base64.DEFAULT);

				/*** There isn't enough memory to open up more than a couple camera photos **/
				/** So pre-scale the target bitmap into which the file is decoded ***/

				/** Get the size of the ImageView ***/
				int targetW = imageView.getWidth();
				int targetH = imageView.getHeight();

				/*** Get the size of the image ***/
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inJustDecodeBounds = true;
				//BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
				Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,encodeByte.length, bmOptions);
				int photoW = bmOptions.outWidth;
				int photoH = bmOptions.outHeight;

				/*** Figure out which way needs to be reduced less **/
				int scaleFactor = 1;
				if ((targetW > 0) || (targetH > 0)) {
					scaleFactor = Math.min(photoW/targetW, photoH/targetH);
				}

				/** Set bitmap options to scale the image decode target **/
				bmOptions.inJustDecodeBounds = false;
				bmOptions.inSampleSize = scaleFactor;
				bmOptions.inPurgeable = true;

				/** Decode the JPEG file into a Bitmap **/
				// Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
				bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
						encodeByte.length, bmOptions);


				imageView.setImageBitmap(bitmap);
			}catch (OutOfMemoryError e){
				e.printStackTrace();
			}
		}

//		public void launchActivity(View v) throws TransactionTooLargeException {
//			if (Catalog.getCustomerCode() != null
//					&& !Catalog.getCustomerCode().isEmpty()
//					&& Catalog.getCustomerName() != null
//					&& !Catalog.getCustomerName().isEmpty()) {
//
//
////				HashMap<String ,ImageButton> mHashMapIcon = new HashMap<>();
////				mHashMapIcon.put("CartIcon",mCartIcon);
////
////				HashMap<String, TextView> mHashMapTextView = new HashMap<>();
////				mHashMapTextView.put("CartText",mCartTxt);
//
//				int position = v.getId();
//				product = getItem(position);
//				CatalogProductDetails.setProductCode(product.getProductCode());
//				CatalogProductDetails.setProductName(product.getProductName());
//				CatalogProductDetails
//						.setCategoryCode(product.getCategoryCode());
//				CatalogProductDetails.setSubCategoryCode(product
//						.getSubCategoryCode());
//				CatalogProductDetails.setUom(product.getUom());
//				CatalogProductDetails
//						.setPcsPerCarton(product.getPcspercarton());
//				CatalogProductDetails.setWholesalePrice(product
//						.getWholesalePrice());
//				CatalogProductDetails
//						.setProductImage(product.getProductImage());
//				CatalogProductDetails.setSpecification(product
//						.getSpecification());
//				CatalogProductDetails.setRetailPrice(product.getRetailPrice());
//				CatalogProductDetails.setHaveAttribute(product.getHaveAttribute());
//				catalogproductdetailfragment = CatalogProductDetailFragment.newInstance(/*
//						mHashMapIcon, mHashMapTextView*/);
//				FragmentTransaction transaction = getChildFragmentManager()
//						.beginTransaction();
//				// Store the Fragment in stack
//				transaction.addToBackStack(null);
//				transaction.replace(R.id.fragment_mainLayout,
//						catalogproductdetailfragment).commit();
//				SOCatalogActivity.changeview.setVisibility(View.GONE);
//				SOCatalogActivity.mFilterIcon.setVisibility(View.GONE);
//				SOCatalogActivity.mSearchIcon.setVisibility(View.GONE);
//				SOCatalogActivity.mBack.setVisibility(View.GONE);
//				SOCatalogActivity.mClose.setVisibility(View.GONE);
//				SOCatalogActivity.mSearchEd.setVisibility(View.GONE);
//				Catalog.setChildFragmentVisible(true);
//			} else {
//				//mPager.setCurrentItem(0);
//			}
//
//		}

		private void qtyPlusAction(View v) {
			int position = v.getId();
			mPosition = position;
			product = getItem(position);
			Log.d("product.getQty()",product.getQty());
			if (product.getQty() != null && !product.getQty().isEmpty()) {
				qtyFlag = Double.valueOf(product.getQty().toString());
			} else {
				qtyFlag = 0;
			}
			product.setQty(String.valueOf(++qtyFlag));
			mkeyActionDoneStr = "Qty";
			notifyDataSetChanged();
			qtyCalc(v);
		}

		private void qtyMinusAction(View v) {
			int position = v.getId();
			mPosition = position;
			product = getItem(position);
			if (product.getQty() != null && !product.getQty().isEmpty()) {
				qtyFlag = Double.valueOf(product.getQty().toString());
				if (qtyFlag > 1) {
					product.setQty(String.valueOf(--qtyFlag));
				} else {
					product.setQty("1");
				}
			}
			mkeyActionDoneStr = "Qty";
			notifyDataSetChanged();
			qtyCalc(v);
		}

		private void qtyActionDone(View v) {
			EditText edQty = (EditText) v;
			int position = v.getId();
			mPosition = position;
			product = getItem(position);
			product.setQty(edQty.getText().toString());

			notifyDataSetChanged();
			qtyCalc(v);
			hideKeyboard(edQty);
		}

		private void cartonPlusAction(View v) {
			int position = v.getId();
			mPosition = position;
			product = getItem(position);
			if (product.getCqty() != null && !product.getCqty().isEmpty()) {
				cartonFlag = Double.valueOf(product.getCqty().toString());
			} else {
				cartonFlag = 1;
			}
			product.setCqty(String.valueOf(++cartonFlag));
			mkeyActionDoneStr = "CQty";
			notifyDataSetChanged();
			cQtyCalc(v);
		}

		private void cartonMinusAction(View v) {
			int position = v.getId();
			mPosition = position;
			product = getItem(position);
			if (product.getCqty() != null && !product.getCqty().isEmpty()) {
				cartonFlag = Double.valueOf(product.getCqty().toString());
				if (cartonFlag > 1) {
					product.setCqty(String.valueOf(--cartonFlag));
				} else {
					product.setCqty("1");
				}
			}
			mkeyActionDoneStr = "CQty";
			notifyDataSetChanged();
			cQtyCalc(v);
		}

		private void cartonActionDone(View v) {
			EditText edCarton = (EditText) v;
			int position = v.getId();
			mPosition = position;
			product = getItem(position);
			product.setCqty(edCarton.getText().toString());

			notifyDataSetChanged();

			cQtyCalc(v);
			hideKeyboard(edCarton);
		}

		private void qtyCalc(View v) {
			double cqty = 0, qty = 0, pcs = 0, lqty = 0;
			int position = v.getId();
			product = getItem(position);

			String quantity = product.getQty();
			String pcspercarton = product.getPcspercarton();

			try {

				if ((pcspercarton != null && !pcspercarton.isEmpty() && !pcspercarton
						.equals("0"))
						&& (quantity != null && !quantity.isEmpty())) {

					pcs = Double.valueOf(pcspercarton);

					qty = Double.valueOf(quantity);

					if (qty > 0) {
						cqty = (int) (qty / pcs);
						lqty = qty % pcs;

						String ctn = twoDecimalPoint(cqty);
						String loose = twoDecimalPoint(lqty);

						product.setLqty(String.valueOf(loose));
						product.setCqty(String.valueOf(ctn).split("\\.")[0]);

						Log.d("lQtyCalc", "" + loose);
						Log.d("cQtyCalc", "" + ctn);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} catch (StackOverflowError e) {
				e.printStackTrace();
			}
			notifyDataSetChanged();
		}

		// cQtyCalc Calculation
		private void cQtyCalc(View v) {

			double cqty = 0, pcs = 1, lqty = 0, qty = 0;
			int position = v.getId();
			product = getItem(position);

			String cartonQty = product.getCqty();
			String pcspercarton = product.getPcspercarton();

			try {
				if ((cartonQty != null && !cartonQty.isEmpty())
						&& (pcspercarton != null && !pcspercarton.isEmpty())) {
					cqty = Double.valueOf(cartonQty);

					pcs = Double.valueOf(pcspercarton);

					if (cqty > 0) {
						qty = cqty * pcs;
					}

					String quantity = twoDecimalPoint(qty);

					Log.d("cQtyCalc", "" + quantity);
					product.setQty(""+quantity);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} catch (StackOverflowError e) {
				e.printStackTrace();

			}
			notifyDataSetChanged();
		}

		// TotalCalc Calculation
		private void totalCalc(View v) {
			double dTotal = 0.00, dQty = 0.00, dCQty = 0.00, dLQty = 0.00, dPrice = 0.00, dCPrice = 0.00;
			int position = v.getId();
			product = getItem(position);
			String quantity = product.getQty();
			String cqty = product.getCqty();
			String lqty = product.getLqty();
			String wholesaleprice = product.getWholesalePrice();
			String cPrice = product.getRetailPrice();

			if (wholesaleprice != null && !wholesaleprice.isEmpty()) {

			} else {
				wholesaleprice = "0";
			}

			if (cPrice != null && !cPrice.isEmpty()) {

			} else {
				cPrice = "0";
			}

			if (cqty != null && !cqty.isEmpty()) {

			} else {
				cqty = "0";
			}

			if (lqty != null && !lqty.isEmpty()) {

			} else {
				lqty = "0";
			}

			if (quantity != null && !quantity.isEmpty()) {

			} else {
				quantity = "0";
			}

			try {

				priceflag = SalesOrderSetGet.getCartonpriceflag();
				if (priceflag.matches("1")) {

					dPrice = Double.parseDouble(wholesaleprice);
					dCPrice = Double.parseDouble(cPrice);

					dCQty = Double.parseDouble(cqty);
					dLQty = Double.parseDouble(lqty);

					dTotal = (dCQty * dCPrice) + (dLQty * dPrice);

				} else {
					dQty = Double.valueOf(quantity);
					dPrice = Double.valueOf(wholesaleprice);
					Log.d("dQty", "" + dQty);
					Log.d("dPrice", "" + dPrice);
					dTotal = dQty * dPrice;
				}

			} catch (Exception e) {
				e.printStackTrace();

			}
			Log.d("dTotal", "--" + dTotal);
			taxCalc(position, dTotal);
		}

//		public void taxCalc(int position, double dTotal) {
//			product = getItem(position);
//			double dSubTotal = 0.00, dTaxValue = 0.00, dTaxAmount = 0.00, dNetTotal = 0.00;
//			String total = "0.00", tax = "0.000", netTotal = "0.00", subTotal = "0.00";
//
//			String taxType = SalesOrderSetGet.getCompanytax();
//			String taxValue = SalesOrderSetGet.getTaxValue();
//
//			if (taxType != null && !taxType.isEmpty()) {
//
//			} else {
//				taxType = "Z";
//			}
//			if (taxValue != null && !taxValue.isEmpty()) {
//
//			} else {
//				taxValue = "0.0";
//			}
//
//			Log.d("taxType", taxType);
//			Log.d("taxValue adapter", taxValue);
//
//			try {
//
//				dSubTotal = dTotal;
//
//				if ((taxType != null && !taxType.isEmpty())
//						&& (taxValue != null && !taxValue.isEmpty())) {
//
//					dTaxValue = Double.parseDouble(taxValue);
//
//					if (taxType.matches("E")) {
//
//						dTaxAmount = (dTotal * dTaxValue) / 100;
//						tax = fourDecimalPoint(dTaxAmount);
//
//						dNetTotal = dTotal + dTaxAmount;
//						netTotal = twoDecimalPoint(dNetTotal);
//					} else if (taxType.matches("I")) {
//
//						dTaxAmount = (dTotal * dTaxValue) / (100 + dTaxValue);
//						tax = fourDecimalPoint(dTaxAmount);
//
//						// dNetTotal = dTotal + dTaxAmount;
//						dNetTotal = dTotal;
//						netTotal = twoDecimalPoint(dNetTotal);
//
//					} else if (taxType.matches("Z")) {
//
//						// dNetTotal = dTotal + dTaxAmount;
//						dNetTotal = dTotal;
//						netTotal = twoDecimalPoint(dNetTotal);
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			total = twoDecimalPoint(dTotal);
//			subTotal = twoDecimalPoint(dSubTotal);
//			// netTotal = twoDecimalPoint(dNetTotal);
//
//			Log.d("total", "" + total);
//			Log.d("subTotal", "" + subTotal);
//			Log.d("taxType", "" + taxType);
//			Log.d("taxValue", "" + taxValue);
//			Log.d("tax", "" + tax);
//			Log.d("netTotal", "" + netTotal);
//
//			Log.d("getCqty", "" + product.getCqty());
//			Log.d("getLqty", "" + product.getLqty());
//			Log.d("getQty", "" + product.getQty());
//
//			int slNo = dbhelper.getCount();
//			Log.d("dbhelper.getCount()", "" + slNo);
//
//			String sno = String.valueOf(++slNo);
//			productvalues.put(COLUMN_PRODUCT_SLNO, sno);
//			productvalues.put(COLUMN_PRODUCT_CODE, product.getProductCode());
//			productvalues.put(COLUMN_PRODUCT_NAME, product.getProductName());
//			productvalues.put(COLUMN_CARTON_QTY, product.getCqty());
//			productvalues.put(COLUMN_LOOSE_QTY, product.getLqty());
//			productvalues.put(COLUMN_QUANTITY, product.getQty());
//			productvalues.put(COLUMN_PRICE, product.getWholesalePrice());
//			productvalues.put(COLUMN_WHOLESALEPRICE,
//					product.getWholesalePrice());
//			productvalues.put(COLUMN_UOMCODE, product.getUom());
//			productvalues.put(COLUMN_PCSPERCARTON, product.getPcspercarton());
//			productvalues.put(COLUMN_TAXTYPE, taxType);
//			productvalues.put(COLUMN_TAXVALUE, taxValue);
//			productvalues.put(COLUMN_TAX, tax);
//			productvalues.put(COLUMN_TOTAL, total);
//			productvalues.put(COLUMN_SUB_TOTAL, subTotal);
//			productvalues.put(COLUMN_NETTOTAL, netTotal);
//			productvalues.put(COLUMN_PRODUCT_IMAGE, product.getProductImage());
//
//			productvalues.put(COLUMN_FOC, "0");
//			productvalues.put(COLUMN_ITEM_DISCOUNT, "0");
//			productvalues.put(COLUMN_CARTONPRICE, product.getRetailPrice());
//			productvalues.put(COLUMN_EXCHANGEQTY, "0");
//
//			DBCatalog.storeCatalog(productvalues);
//			Log.d("mProductAttributeArr","dn-->"+mAttributeArr.size());
//
//			if(mAttributeArr.size()>0){
//				SOTDatabase.storeAttribute(sno,product.getProductCode(), product.getProductName(),mAttributeArr);
//			}
//
//		}

		public void taxCalc(int position, double dTotal) {
			product = getItem(position);
			double dSubTotal = 0.00, dTaxValue = 0.00, dTaxAmount = 0.00, dNetTotal = 0.00;
			String total = "0.00", tax = "0.000", netTotal = "0.00", subTotal = "0.00";

			String taxType = SalesOrderSetGet.getCompanytax();
			String taxValue = SalesOrderSetGet.getTaxValue();

			if (taxType != null && !taxType.isEmpty()) {

			} else {
				taxType = "Z";
			}
			if (taxValue != null && !taxValue.isEmpty()) {

			} else {
				taxValue = "0.0";
			}

			Log.d("taxType", taxType);
			Log.d("taxValue adapter", taxValue);

			try {

				dSubTotal = dTotal;

				if ((taxType != null && !taxType.isEmpty())
						&& (taxValue != null && !taxValue.isEmpty())) {

					dTaxValue = Double.parseDouble(taxValue);

					if (taxType.matches("E")) {

						dTaxAmount = (dTotal * dTaxValue) / 100;
						tax = fourDecimalPoint(dTaxAmount);

						dNetTotal = dTotal + dTaxAmount;
						netTotal = twoDecimalPoint(dNetTotal);
					} else if (taxType.matches("I")) {

						dTaxAmount = (dTotal * dTaxValue) / (100 + dTaxValue);
						tax = fourDecimalPoint(dTaxAmount);

						// dNetTotal = dTotal + dTaxAmount;
						dNetTotal = dTotal;
						netTotal = twoDecimalPoint(dNetTotal);

					} else if (taxType.matches("Z")) {

						// dNetTotal = dTotal + dTaxAmount;
						dNetTotal = dTotal;
						netTotal = twoDecimalPoint(dNetTotal);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			total = twoDecimalPoint(dTotal);
			subTotal = twoDecimalPoint(dSubTotal);
			// netTotal = twoDecimalPoint(dNetTotal);

			Log.d("total", "" + total);
			Log.d("subTotal", "" + subTotal);
			Log.d("taxType", "" + taxType);
			Log.d("taxValue", "" + taxValue);
			Log.d("tax", "" + tax);
			Log.d("netTotal", "" + netTotal);

			Log.d("getCqty", "" + product.getCqty());
			Log.d("getLqty", "" + product.getLqty());
			Log.d("getQty", "" + product.getQty());

			cursor = SOTDatabase.getCursor();

			if (cursor != null && cursor.getCount() > 0) {
				sl_no = cursor.getCount();
				sl_no++;
			}
//			productvalues.put(COLUMN_PRODUCT_SLNO, sno);
//			productvalues.put(COLUMN_PRODUCT_CODE, product.getProductCode());
//			productvalues.put(COLUMN_PRODUCT_NAME, product.getProductName());
//			productvalues.put(COLUMN_CARTON_QTY, product.getCqty());
//			productvalues.put(COLUMN_LOOSE_QTY, product.getLqty());
//			productvalues.put(COLUMN_QUANTITY, product.getQty());
//			productvalues.put(COLUMN_PRICE, product.getWholesalePrice());
//			productvalues.put(COLUMN_WHOLESALEPRICE,
//					product.getWholesalePrice());
//			productvalues.put(COLUMN_UOMCODE, product.getUom());
//			productvalues.put(COLUMN_PCSPERCARTON, product.getPcspercarton());
//			productvalues.put(COLUMN_TAXTYPE, taxType);
//			productvalues.put(COLUMN_TAXVALUE, taxValue);
//			productvalues.put(COLUMN_TAX, tax);
//			productvalues.put(COLUMN_TOTAL, total);
//			productvalues.put(COLUMN_SUB_TOTAL, subTotal);
//			productvalues.put(COLUMN_NETTOTAL, netTotal);
//			productvalues.put(COLUMN_PRODUCT_IMAGE, product.getProductImage());
//
//			productvalues.put(COLUMN_FOC, "0");
//			productvalues.put(COLUMN_ITEM_DISCOUNT, "0");
//			productvalues.put(COLUMN_CARTONPRICE, product.getRetailPrice());
//			productvalues.put(COLUMN_EXCHANGEQTY, "0");
//
//			DBCatalog.storeCatalog(productvalues);
			String codeStr = product.getProductCode();
			String nameStr = product.getProductName();
			String cartonQtyStr = product.getCqty();
			String looseQtyStr = product.getLqty();
			String qtyStr = product.getQty();
			String uomStr = product.getUom();
			String cartonPerQtyStr = product.getPcspercarton();

			int cartonPerQty = 0;
			double price = 0, ntTot = 0,cartonQty = 0, looseQty = 0, qty = 0,pcs = 0;
			String sbTtl = "";
			String netT = "";

			if (!slPrice.matches("")) {
				price = Double.parseDouble(slPrice);
			}

			if (!cartonQtyStr.matches("")) {
				cartonQty = Double.parseDouble(cartonQtyStr);
			}
			if (!looseQtyStr.matches("")) {
				looseQty = Double.parseDouble(looseQtyStr);
			}
			if (!qtyStr.matches("")) {
				qty = Double.parseDouble(qtyStr);
			}

			if (!cartonPerQtyStr.matches("")) {
				cartonPerQty = Integer.parseInt(cartonPerQtyStr);
			}


			SOTDatabase.storeProduct(sl_no, codeStr, nameStr, cartonQty,
					looseQty, qty, 0, price, 0.0, uomStr, cartonPerQty, tt,
					Double.parseDouble(tax), netT, taxType, taxValue, "", sbTtl, "0.00", "0", "0",
					"", "", "", "","","");
			Log.d("mProductAttributeArr","dn-->"+mAttributeArr.size());

			if(mAttributeArr.size()>0){
				SOTDatabase.storeAttribute(String.valueOf(sl_no),product.getProductCode(), product.getProductName(),mAttributeArr);
			}

		}

		public void bitmap() {

		}

		protected void showKeyboard(EditText editText) {
			InputMethodManager inputManager = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(editText,
					InputMethodManager.SHOW_IMPLICIT);
		}

		public void hideKeyboard(EditText edittext) {
			InputMethodManager inputmethodmanager = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmethodmanager.hideSoftInputFromWindow(
					edittext.getWindowToken(), 0);

		}

		protected String twoDecimalPoint(double d) {
			DecimalFormat df = new DecimalFormat("#.##");
			df.setMinimumFractionDigits(2);
			String tot = df.format(d);
			return tot;
		}

		protected String fourDecimalPoint(double d) {
			DecimalFormat df = new DecimalFormat("#.####");
			df.setMinimumFractionDigits(4);
			String tot = df.format(d);
			return tot;
		}
	}

	public void animCartIcon() {
		Cursor cursor = DBCatalog.getCursor();
		int cartSize = cursor.getCount();
		if (cartSize > 0) {
			if (Catalog.isSearchVisible()) {
				mCartIcon.setVisibility(View.GONE);
				mCartText.setVisibility(View.GONE);
			} else {
				mCartIcon.setVisibility(View.VISIBLE);
				mCartText.setVisibility(View.VISIBLE);
				ObjectAnimator anim = ObjectAnimator.ofFloat(mCartText,
						"rotationY", 0.0f, 360.0f);
				anim.setDuration(1000);
				anim.start();
				mCartIcon.setVisibility(View.VISIBLE);
				mCartText.setText(cartSize + "");
			}
		} else {
			mCartText.setVisibility(View.GONE);
			mCartText.setText("");
		}
	}

	public abstract class InfiniteScrollListener implements
			AbsListView.OnScrollListener {
		private int bufferItemCount = 12;

		private int itemCount = 0,mScrollState;
		private boolean isLoading = true;

		public InfiniteScrollListener(int bufferItemCount) {
			this.bufferItemCount = bufferItemCount;
		}

		public abstract void loadMore(int page, int totalItemsCount);

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			mScrollState = scrollState;
			if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
				changeview.setClickable(true);
			}
			InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (TransferAddProduct.this.getWindow().getCurrentFocus() != null) {
				inputManager.hideSoftInputFromWindow(TransferAddProduct.this.getWindow().getCurrentFocus().getWindowToken(), 0);
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
							 int visibleItemCount, int totalItemCount) {

			totalItem = firstVisibleItem;

			if (totalItemCount < itemCount) {
				this.itemCount = totalItemCount;
				if (totalItemCount == 0) {
					this.isLoading = true;
				}
			}

			if (isLoading && (totalItemCount > itemCount)) {
				isLoading = false;
				itemCount = totalItemCount;
				currentPage++;

			}

			if (!isLoading
					&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + bufferItemCount)) {
				int pageNo = currentPage + 1;
				//	Log.d("loadMore","-->"+page);
				//	Log.d("loadMore","--> true");
				loadMore(pageNo, totalItemCount);
				isLoading = true;
			}

			if (mScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
				changeview.setClickable(true);
			}
			else{
				changeview.setClickable(false);
			}
		}
	}






	private void loadMoreProducts(HashMap<String, String> params) {
		loadmoreProduct = new ArrayList<Product>();
		loadmoreProduct.clear();
		new XMLAccessTask(TransferAddProduct.this, valid_url, "fncGetProductMainImage",
				params, false, new XMLAccessTask.CallbackInterface() {

			@Override
			public void onSuccess(NodeList nl) {
				try {
					for (int i = 0; i < nl.getLength(); i++) {
						// creating new HashMap
						Element e = (Element) nl.item(i);
						Product product = new Product();
						// adding each child node to Product Pojo class
						// => value
						product.setProductCode(XMLParser.getValue(e,
								KEY_PRODUCTCODE));
						product.setProductName(XMLParser.getValue(e,
								KEY_PRODUCTNAME));
						product.setCategoryCode(XMLParser.getValue(e,
								KEY_CATEGORYCODE));
						product.setSubCategoryCode(XMLParser.getValue(
								e, KEY_SUBCATEGORYCODE));
						product.setUom(XMLParser.getValue(e,
								KEY_UOMCODE));
						String pcspercarton = XMLParser.getValue(e,
								KEY_PCSPERCARTON);
						double pcs1 = Double.valueOf(pcspercarton);
						String pcsp = noDecimalPoint(pcs1);
						product.setPcspercarton(pcsp);
						// product.setPcspercarton(parser.getValue(e,
						// KEY_PCSPERCARTON));
						product.setWholesalePrice(XMLParser.getValue(e,
								KEY_WHOLESALEPRICE));
						product.setProductImage(XMLParser.getValue(e,
								KEY_PRODUCTIMAGE));
						product.setSpecification(XMLParser.getValue(e,
								KEY_SPECIFICATION));
						product.setRetailPrice(XMLParser.getValue(e,
								KEY_RETAILPRICE));
						product.setHaveAttribute(XMLParser.getValue(e,
								KEY_HAVEATTRIBUTE));
						Log.d("retail",
								XMLParser.getValue(e, KEY_RETAILPRICE));

						String base = XMLParser.getValue(e,KEY_PRODUCTIMAGE);

						if(base!=null && !base.isEmpty()){
							byte[] imageAsBytes = Base64.decode(base.getBytes(),Base64.DEFAULT);
							Bitmap bmp = decodeSampledBitmapFromResource(imageAsBytes, 80,80);
							product.setBitmap(bmp);

						}else{
							product.setBitmap(null);
						}

						if (pcspercarton != null
								&& !pcspercarton.isEmpty()) {
							if (Double.valueOf(pcspercarton) > 1) {
								// product.setCqty("1");
								double pcs = Double.valueOf(pcspercarton);
								double carton = pcs * 1;
								product.setQty(String.valueOf(carton));
							} else {
								product.setQty("1");
								// product.setCqty("0");
							}
						}
						product.setCqty("1");
						product.setLqty("0");

						// adding all childnode to ArrayList
						loadmoreProduct.add(product);
					}

					Cursor cursor = SOTDatabase.getCursor();
					int count = cursor.getCount();
					if (count > 0) {
						for (Product product:loadmoreProduct) {
							String pcode = product.getProductCode();
							Log.d("pcode", pcode);
							Double qty = SOTDatabase.getProductQty(pcode);
							Double cqty = SOTDatabase.getCartonQty(pcode);
							String quantity = String.valueOf(qty);
							String cartonqty = String.valueOf(cqty);
							Log.d("quantity", quantity);
							Log.d("cartonqty", cartonqty);
							if(quantity.matches("0.0")){
								product.setQty("1");
								product.setCqty("1");
							}else{
								product.setQty(quantity);
								product.setCqty(cartonqty);
							}
						}
					}
					load_more.setVisibility(View.GONE);
					productList.addAll(loadmoreProduct);
					mainImageAdapter.notifyDataSetChanged();


				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(XMLAccessTask.ErrorType error) {
				onError(error);
			}

		}).execute();

	}


	private void loadProductMainImage(HashMap<String, String> params) {
		productList.clear();
		Log.d("product load", "loadProductMainImage");
		new XMLAccessTask(TransferAddProduct.this, valid_url, "fncGetProductMainImage",
				params, false, new XMLAccessTask.CallbackInterface() {

			@Override
			public void onSuccess(NodeList nl) {
				isScrollListener = true;
				//Log.d("catalog load more", "more more");

				for (int i = 0; i < nl.getLength(); i++) {

					//	Log.d("product load", "loading");

					// creating new HashMap
					Element e = (Element) nl.item(i);
					Product product = new Product();
					// adding each child node to Product Pojo class =>
					// value
						/*	Log.d("product p",
									"pp"
											+ XMLParser.getValue(e,
													KEY_PRODUCTCODE));*/
					product.setProductCode(XMLParser.getValue(e,
							KEY_PRODUCTCODE));
					product.setProductName(XMLParser.getValue(e,
							KEY_PRODUCTNAME));
					product.setCategoryCode(XMLParser.getValue(e,
							KEY_CATEGORYCODE));
					product.setSubCategoryCode(XMLParser.getValue(e,
							KEY_SUBCATEGORYCODE));
					product.setUom(XMLParser.getValue(e, KEY_UOMCODE));
					String pcspercarton = XMLParser.getValue(e,
							KEY_PCSPERCARTON);
					double pcs1 = Double.valueOf(pcspercarton);
					String pcsp = noDecimalPoint(pcs1);
					product.setPcspercarton(pcsp);
					// product.setPcspercarton(parser.getValue(e,
					// KEY_PCSPERCARTON));
					product.setWholesalePrice(XMLParser.getValue(e,
							KEY_WHOLESALEPRICE));
					product.setProductImage(XMLParser.getValue(e,
							KEY_PRODUCTIMAGE));
					product.setSpecification(XMLParser.getValue(e,
							KEY_SPECIFICATION));
					String base = XMLParser.getValue(e,KEY_PRODUCTIMAGE);

					if(base!=null && !base.isEmpty()){
						byte[] imageAsBytes = Base64.decode(base.getBytes(),Base64.DEFAULT);
						Bitmap bmp = decodeSampledBitmapFromResource(imageAsBytes, 80,80);
						product.setBitmap(bmp);

					}else{
						product.setBitmap(null);
					}
					product.setHaveAttribute(XMLParser.getValue(e,KEY_HAVEATTRIBUTE));

					String retailPrice = null;
					retailPrice = XMLParser
							.getValue(e, KEY_RETAILPRICE);
					if (retailPrice != null && !retailPrice.isEmpty()) {
						product.setRetailPrice(XMLParser.getValue(e,
								KEY_RETAILPRICE));
					} else {
						product.setRetailPrice("0");
					}

					if (pcspercarton != null && !pcspercarton.isEmpty()) {
						if (Double.valueOf(pcspercarton) > 1) {

							double pcs = Double.valueOf(pcspercarton);
							double carton = pcs * 1;
							product.setQty(String.valueOf(carton));
						} else {
							product.setQty("1");

						}
					}
					product.setCqty("1");
					product.setLqty("0");

					// adding all childnode to ArrayList
					productList.add(product);


				}
				Log.d("product size", "-->>>>" + productList.size());
				helper.dismissProgressView(catalog_layout);

				Cursor cursor = SOTDatabase.getCursor();
				int count = cursor.getCount();
				if (count > 0) {
					for (Product product:productList) {
						String pcode = product.getProductCode();
						Log.d("pcode", pcode);
						Double qty = SOTDatabase.getProductQty(pcode);
						Double cqty = SOTDatabase.getCartonQty(pcode);
						String quantity = String.valueOf(qty);
						String cartonqty = String.valueOf(cqty);
						Log.d("quantity", quantity);
						Log.d("cartonqty", cartonqty);
						if(quantity.matches("0.0")){
							product.setQty("1");
							product.setCqty("1");
						}else{
							product.setQty(quantity);
							product.setCqty(cartonqty);
						}
					}
				}
				Log.d("product size", "<<<--" + productList.size());
				if (productList.size() > 0) {

//					mainImageAdapter = new ProductMainImageAdapter(
//							TransferAddProduct.this, R.layout.transfer_grid_item,
//							productList);
//					mGridView.setAdapter(mainImageAdapter);
////							isScrollListener = true;
//					mainImageAdapter.notifyDataSetChanged();
//					screenInches = displayMetrics();
//					if (screenInches > 7) {
//						// Check orientation
//						orientation = getResources().getConfiguration().orientation;
//						if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
//							// Landscape
//							mGridView.setNumColumns(4);
//						} else {
//							// Portrait
//							mGridView.setNumColumns(2);
//						}
//					} else {
//						mGridView.setNumColumns(2);
//					}
//					mGridLayout.setVisibility(View.VISIBLE);
//					mSearchLayout.setVisibility(View.GONE);

					screenInches = displayMetrics();
					orientation = getResources().getConfiguration().orientation;
					if (mChangingView.matches("GridView")) {
						changeview.setImageResource(R.mipmap.grid_1);
						mChangingView = "GridView";
						if (screenInches > 7) {
							if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
								// Landscape
								mGridView.setNumColumns(2);
							} else {
								// Portrait
								mGridView.setNumColumns(1);
							}
						} else {
							mGridView.setNumColumns(1);
						}
						mainImageAdapter = new ProductMainImageAdapter(
								TransferAddProduct.this, R.layout.transfer_listitem,
								productList);
					} else if (mChangingView.matches("ListView")) {
						changeview.setImageResource(R.mipmap.grid_2);
						mChangingView = "ListView";
						if (screenInches > 7) {
							if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
								// Landscape
								mGridView.setNumColumns(4);
							} else {
								// Portrait
								mGridView.setNumColumns(2);
							}
						} else {
							mGridView.setNumColumns(2);
						}
						mainImageAdapter = new ProductMainImageAdapter(
								TransferAddProduct.this, R.layout.transfer_grid_item,
								productList);
					}
					mGridView.setAdapter(mainImageAdapter);
					mGridView.setSelection(totalItem);

				}
				else {
					// helper.showLongToast(R.string.no_data_found);
					Log.d("No product found", "No product found");

				}

			}

			@Override
			public void onFailure(XMLAccessTask.ErrorType error) {
				onError(error);
				helper.dismissProgressView(catalog_layout);
			}

		}).execute();
	}

	public static Bitmap decodeSampledBitmapFromResource(byte[] data, int   reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		//options.inPurgeable = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
											int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	private void onError(final XMLAccessTask.ErrorType error) {
		new Thread() {
			@Override
			public void run() {
				if (TransferAddProduct.this != null) {
					TransferAddProduct.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {

							if (error == XMLAccessTask.ErrorType.NETWORK_UNAVAILABLE) {
								helper.showLongToast(R.string.error_showing_image_no_network_connection);
							} else {
								// helper.showLongToast(R.string.error_showing_image);

								load_more.setVisibility(View.GONE);
								mainImageAdapter.notifyDataSetChanged();
							}
						}
					});
				}
			}
		}.start();
	}
	public Double displayMetrics() {
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int dens = dm.densityDpi;
		double wi = (double) width / (double) dens;
		double hi = (double) height / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		return screenInches = Math.sqrt(x + y);
	}

	private class GetBarCode extends AsyncTask<Void, Void, Void> {
		String barcode="",productbarcode="";
		private HashMap<String, String> mparam;
		@Override
		protected void onPreExecute() {
			helper.showProgressView(catalog_layout);
			barcode = ed_productsearch.getText().toString();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			mparam = new HashMap<String, String>();
			mparam.put("CompanyCode", cmpnyCode);
			mparam.put("ProductCode", "");
			mparam.put("Barcode", barcode);
			jsonString = SalesOrderWebService.getSODetail(mparam,
					"fncGetProductBarCode");

			Log.d("jsonString ", "" + jsonString);
			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			try {
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					productbarcode = jsonChildNode.optString(
							PRODUCT_CODE).toString();
					String barcode = jsonChildNode.optString(PRODUCT_BARCODE)
							.toString();
//					HashMap<String, String> barcodehm = new HashMap<String, String>();
//					barcodehm.put(productbarcode, barcode);
//					alhmbarcode.add(barcodehm);
//					malbarcode.add(barcode);


				}

			} catch (JSONException e) {

				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			mparam.clear();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if(!productbarcode.matches("")){
				ed_productsearch.setText(""+productbarcode);
			}
			searchProduct(ed_productsearch.getText().toString());


		}
	}

	private void searchProduct(String productName){
		try {
			back_icon.setVisibility(View.VISIBLE);
			pageNo = 0;
			currentPage = 0;
			params.put("ProductCode", "");
			params.put("CategoryCode", "");
			params.put("SubCategoryCode", "");
			params.put("CustomerGroupCode", mCustomerGroupCode);
			params.put("PageNo", String.valueOf(pageNo));
			params.put("CustomerCode", mCustomerCode);
			params.put("TranType", catalogSaveType);
			params.put("ProductName", productName);
			hideKeyboard();
			try {
				//helper.showProgressView(catalog_layout);
				Log.d("loadProductMainImage >", "1");
				loadProductMainImage(params);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//showView();
		//animCartIcon();
	}
}