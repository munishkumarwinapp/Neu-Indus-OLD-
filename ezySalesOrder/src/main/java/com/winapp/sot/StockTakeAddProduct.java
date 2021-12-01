package com.winapp.sot;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
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
import android.widget.ArrayAdapter;
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
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterProd;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.NewProductWebService;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.helper.DateTime;
import com.winapp.model.Schedule;
import com.winapp.model.StockCount;
import com.winapp.sot.FilterCS.OnFilterCompletionListener;
import com.winapp.trackwithmap.DeliveryOrderNewHeader;
import com.winapp.trackwithmap.ScheduleDataNew;
import com.winapp.zxing.CustomScannerActivity;
import com.winapp.zxing.SmallCaptureActivity;

public class StockTakeAddProduct extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace, OnClickListener, AdapterView.OnItemClickListener, OnTouchListener, Constants {

	private SlidingMenu menu;
	private TextView mtitle_tv, mtotal_tv,
			mSummary, mDetail, mListing, mAddProduct, mStocktakeNo,
			mStocktake_lbl;
	private ActionBar mactionBar;
	private EditText mproductCode_ed, mproductName_ed, mcarton_ed, mloose_ed,
			mqty_ed, mcurrentQty_tv, mpcsperQty_tv;
	private ListView mListView;
	private CustomAlertAdapterProd arrayAdapterProd;
	private ArrayList<HashMap<String, String>> msearchResults, malhmProducts,
			alhmsearchProduct, malhmgetal, alhmbarcode;
	private String cmpnyCode, locCode, jsonString, valid_url, productCodeStr,
			productNameStr, stockTakeNo = null, 
					strCartonPerQty="", haveBatch="", haveExpiry="", calCarton="", select_category="", select_subcategory="";
	private AlertDialog malertDialog;
	private int textlength = 0;
	private LinearLayout mspinnerLayout;
	private ProgressBar mprogressBar;
	private HashMap<String, String> mparam;
	private StockCountAdapter stockAdapter;
	private ArrayList<StockCount> alstockCount, mEditStockCount,tempArr;
	private LinearLayout customer_layout;
	private ArrayList<String> malprodcode, malbarcode;
	private HashMap<String, String> hmprodCodeName;
	private JSONObject jsonResponse;
	private JSONArray jsonMainNode;
	private TableLayout mtblLayout;
	private ImageButton mClear, mAdd, mfilter;
	private Intent mIntent;
	public int sl_no = 1,stockQtyCount=0;
	private Bundle mbundle;
	//private FilterSearch mFilterSearch;
	private TableRow mtblRow1, mtblRow2;
	private final int WAIT_TIME = 800;
	private Cursor cursor;
	private  FilterCS filtercs;
//	private ImageView barcode_icon;
	BatchDialog batchDialog;
	private LinearLayout slcodelayout,carton_layout,grid_layout,product_layout;
	private TableRow tableRow4,tableRow5,tableRow7;
	private EditText editText1, sl_codefield, sl_namefield, sl_cartonQty,
			sl_looseQty, sl_qty, sl_uom, sl_cartonPerQty,sl_stock,sl_total, sl_tax,
			sl_netTotal;
	private Button sl_addProduct;
	private EditText editText;
	private AlertDialog myalertDialog = null;
	private ArrayList<HashMap<String, String>> searchResults;
	private ArrayList<HashMap<String, String>> getArraylist = new ArrayList<HashMap<String, String>>();
	private String sales_prodCode = "",values = "", slPrice = "", slUomCode = "", slCartonPerQty = "",taxValue = "",Weight = ""
			, LocationCode = "", stock,product_stock_jsonString = null,taxType = "",ss_Cqty = "",beforeLooseQty,str_ssupdate,
			str_sscancel,str_sssno,mBarcodeJsonString = "",mSearchJsonString = "";
	private ArrayList<String> getSalesProdArr = new ArrayList<String>();
	private ArrayList<HashMap<String, String>> alBatchStock = new ArrayList<HashMap<String, String>>();
	private HashMap<String, String> producthashValue = new HashMap<String, String>();
	private JSONObject product_stock_jsonResponse = null ,mBarcodeJsonObject = null,mSearchJsonObject = null;
	private JSONArray product_stock_jsonMainNode = null ,mBarcodeJsonArray = null,mSearchJsonArray = null;
	private TextWatcher cqtyTW, lqtyTW, qtyTW;
	private double tt,d_oldqty;
	private HashMap<String, String> mHashMap;
	private ArrayList<HashMap<String, String>> searchProductArr = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter adapter;
	private ListView productFilterList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.stock_take_addproduct);

		mactionBar = getSupportActionBar();
		mactionBar.setHomeButtonEnabled(true);
		//ActionBarCustom Layout
		View customNav = LayoutInflater.from(this).inflate(
  				R.layout.addproduct_actionbar_title, null);
		//Custom TextView
		mtitle_tv = (TextView) customNav.findViewById(R.id.pageTitle);
		mtitle_tv.setText("Add Product");
		//Custom ImageButton
		mfilter = (ImageButton) customNav.findViewById(R.id.filter);
		mactionBar.setCustomView(customNav);
		mactionBar.setDisplayShowCustomEnabled(true);

		mactionBar.setDisplayHomeAsUpEnabled(false);
		//SlideMenu
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.setShadowWidthRes(R.dimen.shadow_width);

		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);
     
		//EditText		
		mproductCode_ed = (EditText) findViewById(R.id.st_codefield);
		mproductName_ed = (EditText) findViewById(R.id.st_namefield);
		mcarton_ed = (EditText) findViewById(R.id.st_cartonQty);
		mloose_ed = (EditText) findViewById(R.id.st_looseQty);
		mqty_ed = (EditText) findViewById(R.id.st_qty);
		mcurrentQty_tv = (EditText) findViewById(R.id.st_currentQty);
		mpcsperQty_tv = (EditText) findViewById(R.id.st_pcsperQty);
//		barcode_icon = (ImageView) findViewById(R.id.barcode_icon);
//		barcode_icon.setVisibility(View.VISIBLE);
		//ListView
		mListView = (ListView) findViewById(R.id.listView);
		
		//TableLayout
		mtblLayout = (TableLayout) findViewById(R.id.parent_tblLayout);
	  
		//ImageButton
		mClear = (ImageButton) findViewById(R.id.clear_btn);
		mAdd = (ImageButton) findViewById(R.id.plus_btn);
		
		//TextView
		mtotal_tv = (TextView) findViewById(R.id.st_total);
		mSummary = (TextView) findViewById(R.id.sum_screen);
		mDetail = (TextView) findViewById(R.id.customer_screen);
		mListing = (TextView) findViewById(R.id.listing_screen);
		mAddProduct = (TextView) findViewById(R.id.addProduct_screen);
		mStocktakeNo = (TextView) findViewById(R.id.stocktakeNo);
		mStocktake_lbl = (TextView) findViewById(R.id.stocktake_lbl);
		slcodelayout = (LinearLayout) findViewById(R.id.slcodelayout);
		carton_layout = (LinearLayout) findViewById(R.id.carton_layout);
		grid_layout = (LinearLayout) findViewById(R.id.grid_layout);
		product_layout = (LinearLayout) findViewById(R.id.product_layout);
		tableRow4 = (TableRow) findViewById(R.id.tableRow4);
		tableRow5 = (TableRow) findViewById(R.id.tableRow5);
		tableRow7 = (TableRow) findViewById(R.id.tableRow7);

		sl_codefield = (EditText) findViewById(R.id.sl_codefield);
		sl_namefield = (EditText) findViewById(R.id.sl_namefield);
		sl_cartonQty = (EditText) findViewById(R.id.sl_cartonQty);
		sl_looseQty = (EditText) findViewById(R.id.sl_looseQty);
		sl_qty = (EditText) findViewById(R.id.sl_qty);
		sl_uom = (EditText) findViewById(R.id.sl_uom);
		sl_cartonPerQty = (EditText) findViewById(R.id.sl_cartonPerQty);
		sl_stock = (EditText) findViewById(R.id.sl_stock);
		sl_total = (EditText) findViewById(R.id.sl_total);
		sl_tax = (EditText) findViewById(R.id.sl_tax);
		sl_netTotal = (EditText) findViewById(R.id.sl_netTotal);
		sl_addProduct = (Button) findViewById(R.id.sl_addProduct);
		productFilterList = (ListView) findViewById(R.id.productFilterList);
		mDetail.setVisibility(View.GONE);
		//LinearLayout
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);
      
		//TableRow
		mtblRow1 = (TableRow) findViewById(R.id.tableRow2);
		mtblRow2 = (TableRow) findViewById(R.id.tableRow3);
		
		mparam = new HashMap<String, String>();
		mHashMap = new HashMap<String, String>();

		alstockCount = new ArrayList<StockCount>();
		tempArr = new ArrayList<StockCount>();
		mEditStockCount = new ArrayList<StockCount>();

		malprodcode = new ArrayList<String>();
		msearchResults = new ArrayList<HashMap<String, String>>();
		malhmProducts = new ArrayList<HashMap<String, String>>();
		alhmbarcode = new ArrayList<HashMap<String, String>>();
		malhmgetal = new ArrayList<HashMap<String, String>>();
		alhmsearchProduct = new ArrayList<HashMap<String, String>>();
		malbarcode = new ArrayList<String>();
		hmprodCodeName = new HashMap<String, String>();
		
		//ArrayList Clear
		msearchResults.clear();
		malhmProducts.clear();
		malprodcode.clear();
		alhmbarcode.clear();
		malhmgetal.clear();
		hmprodCodeName.clear();
		alhmsearchProduct.clear();
		mEditStockCount.clear();
		alstockCount.clear();
		mparam.clear();
		
		batchDialog = new BatchDialog();
		
		//mFilterSearch = new FilterSearch(this);
		 filtercs = new FilterCS(StockTakeAddProduct.this);
		mIntent = new Intent();
		//Init url
		FWMSSettingsDatabase.init(StockTakeAddProduct.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SalesProductWebService(valid_url);
		new SalesOrderWebService(valid_url);
		new NewProductWebService(valid_url);

		int modeid = FWMSSettingsDatabase.getStockModeId();
		if (modeid == 1) {
			Log.d("Stocktake","stocktake");
			FWMSSettingsDatabase.updateStockMode(1);
			Log.d("updatemode", String.valueOf(modeid));
			slcodelayout.setVisibility(View.GONE);
			carton_layout.setVisibility(View.GONE);
			product_layout.setVisibility(View.VISIBLE);
			tableRow4.setVisibility(View.VISIBLE);
			tableRow5.setVisibility(View.VISIBLE);
			tableRow7.setVisibility(View.VISIBLE);
		} else {
			Log.d("transferadd","transferadd");
			FWMSSettingsDatabase.updateStockMode(0);
			Log.d("updatemode", String.valueOf(modeid));
			slcodelayout.setVisibility(View.VISIBLE);
			carton_layout.setVisibility(View.VISIBLE);
			product_layout.setVisibility(View.GONE);
			tableRow4.setVisibility(View.GONE);
			tableRow5.setVisibility(View.GONE);
			tableRow7.setVisibility(View.GONE);
		}
		
		cmpnyCode = SupplierSetterGetter.getCompanyCode();
		locCode = SalesOrderSetGet.getLocationcode();
		taxType = SalesOrderSetGet.getCompanytax();
		//OnClickListener
		mClear.setOnClickListener(this);
		mAdd.setOnClickListener(this);
		mSummary.setOnClickListener(this);
		mfilter.setOnClickListener(this);
		mDetail.setOnClickListener(this);
		mListing.setOnClickListener(this);
		mqty_ed.addTextChangedListener(qtywatcher);
		
		mcarton_ed.setEnabled(false);
		mcarton_ed.setFocusableInTouchMode(false);
		mcarton_ed.setBackgroundResource(drawable.labelbg);
		mloose_ed.setEnabled(false);
		mloose_ed.setFocusableInTouchMode(false);
		mloose_ed.setBackgroundResource(drawable.labelbg);
		mqty_ed.setEnabled(false);
		mqty_ed.setFocusableInTouchMode(false);
		mqty_ed.setBackgroundResource(drawable.labelbg);
		calCarton = LogOutSetGet.getCalcCarton();
		
       //Init db
		SOTDatabase.init(StockTakeAddProduct.this);
		cursor = SOTDatabase.getCursor();
		if (cursor != null && cursor.getCount() > 0) {
			sl_no = cursor.getCount();
			sl_no++;
		} else {
			sl_no = 1;
		}
		mDetail.setText("Detail");
		customer_layout.setVisibility(View.GONE);
		
		mAddProduct.setTextColor(Color.parseColor("#FFFFFF"));
		mAddProduct.setBackgroundResource(drawable.tab_select);

		stockTakeNo = SalesOrderSetGet.getStockTakeNo();
		if ((stockTakeNo != null && !stockTakeNo.isEmpty())) {

			mStocktakeNo.setText(stockTakeNo);
		} else {
			mStocktakeNo.setVisibility(View.INVISIBLE);
			mStocktake_lbl.setVisibility(View.INVISIBLE);
		}

		mListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		//Call GetStockCountDetailByNo
		new GetStockCountDetailByNo().execute(mStocktakeNo.getText().toString());
		//Call GetBarCode

		sl_codefield.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
				sl_codefield) {
			@Override
			public boolean onDrawableClick() {

				alertDialogSearch();

				return true;

			}
		});

		sl_codefield.setOnEditorActionListener(new TextView.OnEditorActionListener() {

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

		sl_addProduct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if ((str_ssupdate != null) || (str_sscancel != null)) {
					Toast.makeText(StockTakeAddProduct.this, "Updated ",
							Toast.LENGTH_LONG).show();
					storeInDatabase();
					Intent i = new Intent(StockTakeAddProduct.this,
							TransferSummary.class);
					startActivity(i);
					StockTakeAddProduct.this.finish();
				} else {
					storeInDatabase();
				}

			}
		});

		productFilterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
				progressBarOpen();
				new AsyncCallSaleProduct("true").execute();
			}
		});


		/*barcode_icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new IntentIntegrator(StockTakeAddProduct.this).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class).initiateScan();
			}
		});*/

		mproductCode_ed.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.d("mproductCode_ed","-->"+mproductCode_ed.getText().toString());
				mtblRow1.setVisibility(View.GONE);
				System.out.println("Text [" + s + "]");
				stockAdapter.setFilterType("Product");
				stockAdapter.getFilter().filter(s.toString());
//				stockAdapter.notifyDataSetChanged();

				/*if(mproductCode_ed.getText().toString().matches("")) {
					mtblRow1.setVisibility(View.GONE);
					System.out.println("Text [" + s + "]");
					stockAdapter.setFilterType("Product");
					stockAdapter.getFilter().filter(s.toString());
					stockAdapter.notifyDataSetChanged();

				}*/
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

//	mproductCode_ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//										  KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_DONE) {
////					hideKeyboard();
//
//					String prodcode = mproductCode_ed.getText().toString();
//
//					new GetBarCode(prodcode).execute();
//					return true;
//				}
//				return false;
//			}
//		});

		mproductCode_ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
										  KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard();


					Log.d("mproductCode_ed1","-->"+mproductCode_ed.getText().toString());

					String prodcode = mproductCode_ed.getText().toString();

					if(!prodcode.matches("")){
						new GetBarCode(prodcode).execute();
					}else{
						Log.d("prodcode","null");
						mtblRow1.setVisibility(View.GONE);
						System.out.println("Text [" + mproductCode_ed.getText().toString() + "]");
						stockAdapter.setFilterType("Product");
						stockAdapter.getFilter().filter(mproductCode_ed.getText().toString());
					}

//					if(!prodcode.matches("")){
//						Log.d("prodcode1","null");
//						mtblRow1.setVisibility(View.GONE);
//						System.out.println("Text [" + prodcode + "]");
//						stockAdapter.setFilterType("Product");
//						stockAdapter.getFilter().filter(prodcode);
//						progressBarOpen();
//					}

					return true;
				}
				return false;
			}
		});


		sl_cartonQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

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

		sl_looseQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

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

		/*lqtyTW = new TextWatcher() {

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
*/

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

								if (calCarton.matches("0")) {

								} else {
									looseQtyCalc();
								}
							}}
					}}

		};

		sl_qty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

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

		sl_cartonQty.addTextChangedListener(cqtyTW);
		sl_looseQty.addTextChangedListener(lqtyTW);
		sl_qty.addTextChangedListener(qtyTW);


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
			Toast.makeText(StockTakeAddProduct.this, "Select product code",
					Toast.LENGTH_SHORT).show();
		} else if (qtyStr.matches("")) {

			if (calCarton.matches("0")) {

				if (cartonQtyStr.matches("")) {
					Toast.makeText(StockTakeAddProduct.this,
							"Enter the carton/quantity", Toast.LENGTH_SHORT)
							.show();
					sl_cartonQty.requestFocus();
				} else {
					save();
				}

			} else {
				Toast.makeText(StockTakeAddProduct.this, "Enter the quantity",
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
		/*if (mflag.equalsIgnoreCase("ReceiveStock")) {
			nameStr = sl_namereceivefield.getText().toString();
		}else{*/
			nameStr = sl_namefield.getText().toString();
		//}
		String cartonQtyStr = sl_cartonQty.getText().toString();
		String looseQtyStr = sl_looseQty.getText().toString();
		String qtyStr = sl_qty.getText().toString();
		String uomStr = sl_uom.getText().toString();
		String cartonPerQtyStr = sl_cartonPerQty.getText().toString();
		String totalStr = sl_total.getText().toString();
		String taxStr = sl_tax.getText().toString();
		String netTotalStr = sl_netTotal.getText().toString();
		String stockstr = sl_stock.getText().toString();

		if(!stockstr.matches("")){

		}else{
			stockstr = "0";
		}

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
			queryValues.put("StockinHand", stockstr);
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
				queryValues.put("StockinHand", stockstr);
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

				Log.d("qty","qt"+qty);

				HashMap<String, String> queryValues = new HashMap<String, String>();
				queryValues.put("slNo", sl_no+"");
				queryValues.put("ProductCode", codeStr);
				queryValues.put("ProductName", nameStr);
				queryValues.put("CQty", cartonQty+"");
				queryValues.put("LQty", looseQty+"");
				queryValues.put("Qty", qty+"");
				queryValues.put("StockinHand", stockstr);
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
					Log.d("qtycheck",qty);
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
					/*	if (priceflag.matches("0")){
							productTotal(qty_nt);
						}else{
							productTotalNew();
						}*/

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

	/*public void looseQtyCalc() {
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

			Log.d("quantitycheck1", String.valueOf(qty));

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
			Log.d("quantitycheck2", String.valueOf(qty));
			sl_qty.removeTextChangedListener(qtyTW);
			sl_qty.setText("" + qty);
			sl_qty.addTextChangedListener(qtyTW);

			if (sl_qty.length() != 0) {
				sl_qty.setSelection(sl_qty.length());
			}

			productTotal(qty);
		}
	}*/

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

			Log.d("quantitycheck3", String.valueOf(qty));

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

	public void alertDialogSearch() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				StockTakeAddProduct.this);
		editText = new EditText(StockTakeAddProduct.this);
		final ListView listview = new ListView(StockTakeAddProduct.this);
		LinearLayout layout = new LinearLayout(StockTakeAddProduct.this);
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

		arrayAdapterProd = new CustomAlertAdapterProd(StockTakeAddProduct.this,
				malhmProducts);
		listview.setAdapter(arrayAdapterProd);
		listview.setOnItemClickListener(StockTakeAddProduct.this);

		searchResults = new ArrayList<HashMap<String, String>>(malhmProducts);
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
				for (int i = 0; i < malhmProducts.size(); i++) {
					String supplierName = malhmProducts.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(malhmProducts.get(i));
					}
				}

				arrayAdapterProd = new CustomAlertAdapterProd(
						StockTakeAddProduct.this, searchResults);
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
		Set<Map.Entry<String, String>> keys = datavalue.entrySet();
		Iterator<Map.Entry<String, String>> iterator = keys.iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry mapEntry = iterator.next();
			sales_prodCode = (String) mapEntry.getKey();
			values = (String) mapEntry.getValue();

			sl_codefield.setText(sales_prodCode);
			sl_namefield.setText(values);
			progressBarOpen();
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
					sl_uom.setText("");
					sl_stock.setText("");
					sl_cartonPerQty.setText("");
				}
			});
		}

	}


	TextWatcher qtywatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			qtyReverseCalc();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.clear_btn:
			clearViews();
			break;
		case R.id.plus_btn:
//			addQuantity();
			break;
		case R.id.sum_screen:
//			for (StockCount tempcount : alstockCount) {
//				if(tempcount.getCountCQty()>0 || tempcount.getCountLQty()>0 || tempcount.getCountQty()>0){
//					tempArr.add(tempcount);
//				}
//			}

			int modeid = FWMSSettingsDatabase.getStockModeId();
			if (modeid == 1) {
				FWMSSettingsDatabase.updateStockMode(1);
				Log.d("updatemode", String.valueOf(modeid));
				new AsyncSaveStock().execute();
			} else {
				FWMSSettingsDatabase.updateStockMode(0);
				Log.d("updatemode", String.valueOf(modeid));
				tempArr.clear();
				Schedule.setStockcount(tempArr);
				Intent i= new Intent(StockTakeAddProduct.this, StockTakeSummary.class);
				startActivity(i);
				finish();
			}
			
			break;
		case R.id.filter:
			filterSearch();
			break;
		case R.id.customer_screen:
//			mIntent.setClass(StockTakeAddProduct.this,
//					StockTakeHeaderDetail.class);
//			mIntent.putExtra(ACTIVITY_STOCK_TAKE_PRODUCT, alstockCount);
//			startActivity(mIntent);
//			finish();
			break;

		case R.id.listing_screen:
			
			Cursor cursor = SOTDatabase.getCursor();
			int count = cursor.getCount();
			if(count>0){
				alertDialog();
			}else{
				mIntent.setClass(StockTakeAddProduct.this, StockTakeHeader.class);
				startActivity(mIntent);
				finish();
			}
			
			
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.st_cartonQty:
			if (mcarton_ed.getText().toString().equals("0")) {
				mcarton_ed.setText("");
			} else {
				mcarton_ed.setSelection(mcarton_ed.getText().length());
			}
			break;
		case R.id.st_looseQty:
			if (mloose_ed.getText().toString().equals("0")) {
				mloose_ed.setText("");
			} else {
				mloose_ed.setSelection(mloose_ed.getText().length());
			}
			break;
		case R.id.st_qty:
			if (mqty_ed.getText().toString().equals("0")) {
				mqty_ed.setText("");
			} else {
				mqty_ed.setSelection(mqty_ed.getText().length());
			}
			break;

		default:
			break;
		}
		return false;
	}

	/*public void scanCustomScanner(View view) {
		new IntentIntegrator(this).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class).initiateScan();
	}*/

	public void scanMarginScanner(View view) {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.setOrientationLocked(false);
		integrator.setCaptureActivity(SmallCaptureActivity.class);
		integrator.initiateScan();
	}

	private void clearViews() {
//		hideKeyboard();
//		mproductCode_ed.setText("");
		mproductName_ed.setText("");
		mcarton_ed.setText("");
		mloose_ed.setText("");
		mqty_ed.setText("");
//		mtotal_tv.setText("");
//		mcurrentQty_tv.setText("");
//		mpcsperQty_tv.setText("");
	}

	private void visibleViews() {
		if (mtblRow1.getVisibility() == View.GONE) {
			mtblRow1.setVisibility(View.VISIBLE);
		}
//		if (mtblRow2.getVisibility() == View.GONE) {
//			mtblRow2.setVisibility(View.VISIBLE);
//		}
	}

	private void invisibleViews() {
		if (mtblRow1.getVisibility() == View.VISIBLE) {
			mtblRow1.setVisibility(View.GONE);
		}
//		if (mtblRow2.getVisibility() == View.VISIBLE) {
//			mtblRow2.setVisibility(View.GONE);
//		}
	}

	private void filterSearch() {
		   filtercs .filterDialog();
	       
	       filtercs.OnFilterCompletionListener(new OnFilterCompletionListener() {
	     
	     @Override
	     public void onFilterCompleted(String category, String subcategory) {
	      
	          Log.d("catStr", "----->"+category);
	          Log.d("subCatStr", "-->"+subcategory);

			 select_category = category;
			 select_subcategory = subcategory;

			 int modeid = FWMSSettingsDatabase.getStockModeId();
			 if (modeid == 1) {
				 Log.d("Stocktake","stocktake");
				 FWMSSettingsDatabase.updateStockMode(1);
				 String filterType = "NULL", filterValue="";
				 if((category!=null && !category.isEmpty()) && (subcategory!=null && !subcategory.isEmpty())){
					 filterType= "Category && SubCategory";
					 filterValue = category + "," + subcategory;
				 }else if(category!=null && !category.isEmpty()){
					 filterType= "Category";
					 filterValue = category;
				 }else if(subcategory!=null && !subcategory.isEmpty()){
					 filterType= "SubCategory";
					 filterValue = subcategory;
				 }else{
					 filterType= "NULL";
				 }

				 stockAdapter.setFilterType(filterType);
				 stockAdapter.getFilter().filter(filterValue);
			 } else {
				 Log.d("transferadd","transferadd");
				 FWMSSettingsDatabase.updateStockMode(0);

				 new AsyncCallWSSearchProduct().execute(mStocktakeNo.getText().toString());
			 }

	          
	     }
	    });
		}
 
	//Reverse Qty Calculation
	private void qtyReverseCalc() {
		if (!mproductCode_ed.getText().toString().equals("")) {
			int cqty = 0, lqty = 0, pcs = 1, qty = 0;

			String pcsprcarton = mpcsperQty_tv.getText().toString();
			String quantity = mqty_ed.getText().toString();
			qty = quantity.equals("") ? 0 : Integer.valueOf(quantity);

//			mcarton_ed.removeTextChangedListener(clqtywatcher);
//			mloose_ed.removeTextChangedListener(clqtywatcher);

			try {
				
				if (pcsprcarton != null && !pcsprcarton.isEmpty()
						&& !pcsprcarton.equals("0")) {

					pcs = Integer.valueOf(pcsprcarton);
				}
				if (quantity != null && !quantity.isEmpty()) {
					qty = Integer.valueOf(quantity);
					
					
					if (calCarton.matches("0")) {
						
					}else{
						if (qty > 0) {
							cqty = qty / pcs;
							lqty = qty % pcs;

							mcarton_ed.setText("" + cqty);
							mloose_ed.setText("" + lqty);
						} 
					}

				}
			} catch (ArithmeticException e) {
				e.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();
			} catch (StackOverflowError e) {
				e.printStackTrace();
			}
//			mcarton_ed.addTextChangedListener(clqtywatcher);
//			mloose_ed.addTextChangedListener(clqtywatcher);
		}
	}
  
    //Display total qty
	private void totalQty() {
		int totalQuantity = 0;
		if(alstockCount.size()>0){
			for (StockCount stockcount : alstockCount) {
				if(stockcount.getCountQty()>0){
					totalQuantity += stockcount.getCountQty();
					Log.d(stockcount.getProductCode()+" : totalQuantity", "->" + totalQuantity);
				}
			}
		}
		mtotal_tv.setText(String.valueOf(totalQuantity));
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("deprecation")
	public void progressBarOpen() {
		getActionBar().setHomeButtonEnabled(false);
		menu.setSlidingEnabled(false);
		mspinnerLayout = new LinearLayout(StockTakeAddProduct.this);
		mspinnerLayout.setGravity(Gravity.CENTER);
		addContentView(mspinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		mspinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(mtblLayout, false);
		mprogressBar = new ProgressBar(StockTakeAddProduct.this);
		mprogressBar.setProgress(android.R.attr.progressBarStyle);
		mprogressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));

		mspinnerLayout.addView(mprogressBar);
	}

	public void progressBarClose() {
		mprogressBar.setVisibility(View.GONE);
		mspinnerLayout.setVisibility(View.GONE);
		enableViews(mtblLayout, true);
		getActionBar().setHomeButtonEnabled(true);
		menu.setSlidingEnabled(true);
	}
     /**************************FNC_GETSTOCKCOUNTDETAILBYNO*******************/
	private class GetStockCountDetailByNo extends AsyncTask<Object, Void, Void> {

		@Override
		protected void onPreExecute() {
			alstockCount.clear();
			invisibleViews();
			progressBarOpen();
		}

		@Override
		protected Void doInBackground(Object... param) {
			String stockTake = (String) param[0];
			//Log.d("stockTake-->", "sssssssssssssssss" + stockTake);
			mparam.put("CompanyCode", cmpnyCode);
			mparam.put("LocationCode", locCode);
			mparam.put("StockTakeNo", stockTake);
			try {
			jsonString = SalesOrderWebService.getSODetail(mparam, FNC_GETSTOCKCOUNTDETAILBYNO);

			Log.d("jsonString ", "" + jsonString);

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					StockCount stockcount = new StockCount();
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String productcodes = jsonChildNode.getString(PRODUCT_CODE);
					String productnames = jsonChildNode.getString(PRODUCT_NAME);
					stockcount.setProductCode(productcodes);
					stockcount.setProductName(productnames);
					stockcount.setCategoryCode(jsonChildNode.getString(CATEGORYCODE));
					stockcount.setSubcategoryCode(jsonChildNode.getString(SUBCATEGORYCODE));
					
					stockcount.setHavebatch(jsonChildNode.getString(HAVEBATCH));
					stockcount.setHavexpiry(jsonChildNode.getString(HAVEEXPIRY));
					
					String pcspercarton = jsonChildNode.getString(PCSPERCARTON);

					if (pcspercarton != null && !pcspercarton.isEmpty()) {
						String ppc = String.valueOf(pcspercarton).split("\\.")[0];

						stockcount.setPcsPerCarton(Integer.valueOf(ppc));
					} else {
						stockcount.setPcsPerCarton(0);
					}
					String noofcarton = jsonChildNode.getString(NOOFCARTON);
					if (noofcarton != null && !noofcarton.isEmpty()) {
						String nofcarton = String.valueOf(noofcarton).split(
								"\\.")[0];
						stockcount.setNoOfCarton(Integer.valueOf(nofcarton));
					} else {
						stockcount.setNoOfCarton(0);
					}
					String qty = jsonChildNode.getString(QTY);
					if (qty != null && !qty.isEmpty()) {
						String quantity = String.valueOf(qty).split("\\.")[0];
						stockcount.setQty(Integer.valueOf(quantity));
					} else {
						stockcount.setQty(0);
					}
					String countlqty = jsonChildNode.getString(COUNTLQTY);
					if (countlqty != null && !countlqty.isEmpty()) {
						String lqty = String.valueOf(countlqty).split("\\.")[0];
						stockcount.setCountLQty(Integer.valueOf(lqty));
					} else {
						stockcount.setCountLQty(0);
					}
					String countqty = jsonChildNode.getString(COUNTQTY);
					if (countqty != null && !countqty.isEmpty()) {
						String ctqty = String.valueOf(countqty).split("\\.")[0];
						stockcount.setCountQty(Integer.valueOf(ctqty));
					} else {
						stockcount.setCountQty(0);
					}

					String countcqty = jsonChildNode.getString(COUNTCQTY);
					if (countcqty != null && !countcqty.isEmpty()) {
						String ccqty = String.valueOf(countcqty).split("\\.")[0];
						stockcount.setCountCQty(Integer.valueOf(ccqty));
					} else {
						stockcount.setCountCQty(0);
					}
					alstockCount.add(stockcount);

					HashMap<String, String> producthm = new HashMap<String, String>();
					producthm.put(productcodes, productnames);
					malhmProducts.add(producthm);
					hmprodCodeName.putAll(producthm);
					malprodcode.add(productcodes);
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
			progressBarClose();
			if (alstockCount.size() > 0) {
				stockAdapter = new StockCountAdapter(StockTakeAddProduct.this,
						 R.layout.stock_take_addprod_listitem, alstockCount);
				mListView.setAdapter(stockAdapter);
				if (cursor != null && cursor.getCount() > 0) {
					updateStockListRow();
				}
//				stockAdapter.notifyDataSetChanged();
			} else {
				alstockCount.clear();
				stockAdapter = new StockCountAdapter(StockTakeAddProduct.this,
						 R.layout.stock_take_addprod_listitem, alstockCount);
				mListView.setAdapter(stockAdapter);
				Toast.makeText(getApplicationContext(), "No Stock found",
						Toast.LENGTH_SHORT).show();
			}
			totalQty();

		}

	}
	
	/** Store stock Take Start   **/
	
	private void saveStockListRow() {

		ArrayList<String> stockList = new ArrayList<String>();
		stockList.clear();
	//	Log.d("tempArr",""+tempArr.size());
		try {
//			for (StockCount stockcount : alstockCount) {
			for(int i=0;i<alstockCount.size();i++){
				String productcode = alstockCount.get(i).getProductCode();
				
				String product_Code = SOTDatabase.getProductCode(productcode);
				Log.d("product_Code", "pp " +product_Code);
				if ((product_Code != null && !product_Code.isEmpty())) {
					SOTDatabase.updateStockProduct(productcode, alstockCount.get(i).getCountCQty()+"", alstockCount.get(i).getCountLQty()+"", alstockCount.get(i).getCountQty()+"");

					Log.d("Updated", product_Code);
				} else {
					
					if(alstockCount.get(i).getCountCQty()>0 || alstockCount.get(i).getCountLQty()>0 || alstockCount.get(i).getCountQty()>0){
					SOTDatabase.storeProduct(sl_no,
							productcode, alstockCount.get(i).getProductName(), alstockCount.get(i).getCountCQty(), alstockCount.get(i).getCountLQty(), alstockCount.get(i).getCountQty(),
							alstockCount.get(i).getQty(), 0.0, 0.0, "",alstockCount.get(i).getPcsPerCarton(), 0.0, 0.0, "", "", "", "", "", "", "", "", "","","", "","","");

					sl_no++;
					Log.d("Store", "store");
					}else{
//						break;
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
	
	/** Store Stock Take End   **/
//Update List Row
	private void updateStockListRow() {

		ArrayList<String> stockList = new ArrayList<String>();
		stockList.clear();
		try {
			for (StockCount stockcount : alstockCount) {
				String productcode = stockcount.getProductCode();
				stockList = SOTDatabase.getStockProduct(productcode);
				if (stockList.size() > 0) {
					String cartonq = stockList.get(0);
					String looseq = stockList.get(1);
					String qqty = stockList.get(2);

					stockcount.setCountCQty(Integer.parseInt(cartonq));
					stockcount.setCountLQty(Integer.parseInt(looseq));
					stockcount.setCountQty(Integer.parseInt(qqty));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
     /*********************FNC_GETPRODUCTBARCODE***********/
	private class GetBarCode extends AsyncTask<Void, Void, Void> {
		 String barcode = "", productbarcode = "", productCode = "";

		 private GetBarCode(String productCode)
		 {

		 	this.productCode = productCode;

		 }
		 @Override
		protected void onPreExecute() {

			progressBarOpen();
			barcode = mproductCode_ed.getText().toString();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			mparam.put("CompanyCode", cmpnyCode);
			mparam.put("ProductCode", "");
			mparam.put("Barcode", barcode);
			jsonString = SalesOrderWebService.getSODetail(mparam,
					FNC_GETPRODUCTBARCODE);

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

			progressBarClose();
			mproductCode_ed.requestFocus();
			showKeyboard(mproductCode_ed);

			if(!productbarcode.matches("")){
				mproductCode_ed.setText(""+productbarcode);
			}

			mtblRow1.setVisibility(View.GONE);
			System.out.println("Text [" + mproductCode_ed.getText().toString() + "]");
			stockAdapter.setFilterType("Product");
			stockAdapter.getFilter().filter(mproductCode_ed.getText().toString());

//			productbarcode = "";
			Log.d("show","keyboard --"+stockAdapter.getCount());
//			if(stockAdapter.getCount()==1){
//				Log.d("show","keyboard");
//				stockAdapter.qtyCount(0);
//				stockAdapter.notifyDataSetChanged();
//			}
		}
	}
	
	
	
	
	/** Adapter Start    **/
	
	public class StockCountAdapter extends ArrayAdapter<StockCount> implements Filterable {

		private ArrayList<StockCount> mOriginalData;
		private Context mContext;
		private Filter sampleFilter;
		private String filterType;
		public ArrayList<StockCount> mData;
		private int qtyCount = -1,focus=-1;
		public StockCountAdapter(Context context, int resource, ArrayList<StockCount> data) {
			super(context, resource, data);
			this.mContext = context;
			this.mData = new ArrayList<StockCount>();
			this.mData.addAll(data);
			this.mOriginalData = new ArrayList<StockCount>();
			this.mOriginalData.addAll(data);
		}




		public void qtyCount(int qtyCount){
			this.qtyCount = qtyCount;
		}
//
//		@Override
//		public StockCount getItem(int position) {
//			return mOriginalData.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
		
		public void setFilterType(String filterType) {
			this.filterType = filterType;
		}

		public String getFilterType() {
			return filterType;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			
			final StockCount stockcount = mData.get(position);
			
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.stock_take_addprod_listitem, null);
				
				final EditText countcqty = (EditText) view.findViewById(R.id.textView7);
				final EditText countlqty = (EditText) view.findViewById(R.id.textView8);
				final EditText countqty = (EditText) view.findViewById(R.id.textView9);

				// attach the TextWatcher listener to the EditText
				countcqty.addTextChangedListener(new CQtyTextWatcher(view));
				countlqty.addTextChangedListener(new LQtyTextWatcher(view));
				countqty.addTextChangedListener(new QuantityTextWatcher(view));

			} 
			
			final TextView code = (TextView) view.findViewById(R.id.textView1);
			final TextView name = (TextView) view.findViewById(R.id.textView2);
			final TextView category = (TextView) view.findViewById(R.id.textView3);
			final TextView subcategory = (TextView) view.findViewById(R.id.textView4);

			final TextView pcspercarton = (TextView) view.findViewById(R.id.textView5);
			final TextView noofcarton = (TextView) view.findViewById(R.id.textView6);
			final TextView currentqty = (TextView) view.findViewById(R.id.textView10);
			
			code.setText(stockcount.getProductCode());
			name.setText(stockcount.getProductName());
			category.setText(stockcount.getCategoryCode());
			subcategory.setText(stockcount.getSubcategoryCode());
			pcspercarton.setText(String.valueOf(stockcount.getPcsPerCarton()));
			noofcarton.setText(String.valueOf(stockcount.getNoOfCarton()));
			currentqty.setText(String.valueOf(stockcount.getQty()));

			// Quantity
			final EditText countqty = (EditText) view.findViewById(R.id.textView9);
			countqty.setTag(stockcount);
			if (stockcount.getCountQty() != 0) {
				countqty.setText(String.valueOf(stockcount.getCountQty()));
			} else {
				countqty.setText("0");
			}
//			if(qtyCount == position){
//				Log.d("Keyboard","qtykey");
//				showKeyboard(countqty);
//				//stockcount.setQtyFocus(false);
//			}else{
////				hideKeyboard();
//			}
			// Carton Quantity
			final EditText countcqty = (EditText) view.findViewById(R.id.textView7);
			countcqty.setTag(stockcount);
			if (stockcount.getCountCQty() != 0) {
				countcqty.setText(String.valueOf(stockcount.getCountCQty()));
			} else {
				countcqty.setText("0");
			}

			// Loose Quantity
			final EditText countlqty = (EditText) view.findViewById(R.id.textView8);
			countlqty.setTag(stockcount);
			if (stockcount.getCountLQty() != 0) {
				countlqty.setText(String.valueOf(stockcount.getCountLQty()));
			} else {
				countlqty.setText("0");
			}

			Log.d("focus","--"+focus);
			if (focus==0){
				Log.d("focus","zero");
				countcqty.setFocusable(false);
				countcqty.setFocusableInTouchMode(false);
				countqty.setFocusable(false);
				countqty.setFocusableInTouchMode(false);
				//	countcqty.setEnabled(false);
				//	countqty.setEnabled(false);

			}else if (focus==1){
				Log.d("focus","one");
				countcqty.setFocusable(true);
							countcqty.setFocusableInTouchMode(true);
				countqty.setFocusable(true);
							countqty.setFocusableInTouchMode(true);
				//	countcqty.setEnabled(false);
				//	countqty.setEnabled(false);

			}

			if(qtyCount == 0){
				Log.d("Keyboard","qtykey");
				if (stockcount.getPcsPerCarton() > 1) {
					countcqty.setFocusable(true);
					countcqty.setFocusableInTouchMode(true);
					countcqty.requestFocus();

				//	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				//	imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
					showKeyboard(countcqty);
					//countcqty.requestFocus();

				//	countcqty.setFocusable(false);
				}else{
					countqty.setFocusable(true);
					countqty.setFocusableInTouchMode(true);
					countqty.requestFocus();
				//	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					//imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

					showKeyboard(countqty);
					//countqty.requestFocus();

					Log.d("countqty","requestfocus");
				//	countqty.setFocusable(false);
				}

			}else{
				/*Log.d("focus","--"+focus);
				if (focus==0){
					Log.d("focus","zero");
					countcqty.setFocusable(false);
//					countqty.setFocusable(false);
				}*/
			}





			if (stockcount.getPcsPerCarton() > 1) {
				Log.d("product code", stockcount.getProductCode());
				Log.d("product name", stockcount.getProductName());
				countcqty.setEnabled(true);
				countlqty.setEnabled(true);
				countlqty.setBackgroundResource(drawable.edittext_bg);
				countcqty.setBackgroundResource(drawable.edittext_bg);
				/*if(qtyCount == 0){

				}else {

				}*/

			} else {
				countcqty.setEnabled(false);
				countlqty.setEnabled(false);
				countlqty.setBackgroundColor(Color.parseColor("#ececec"));
				countcqty.setBackgroundColor(Color.parseColor("#ececec"));
//				if(qtyCount == 0){
//					Log.d("Keyboard","qtykey");
//					countqty.requestFocus();
//					showKeyboard(countqty);
//
//				}
			}

			countcqty.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					mtblRow1.setVisibility(View.GONE);
					EditText qy = (EditText) v;
					qy.setFocusable(true);
					qy.setFocusableInTouchMode(true);
					clearViews();
					if (!v.hasFocus()) {
                        v.requestFocus();
                    }
					showKeyboard(countcqty);
					if (stockcount.getCountCQty() == 0) {
						countcqty.setText("");
					}
					return false;
				}
			});
			countlqty.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					mtblRow1.setVisibility(View.GONE);
					EditText qy = (EditText) v;
					qy.setFocusable(true);
					qy.setFocusableInTouchMode(true);
					clearViews();
					if (!v.hasFocus()) {
                        v.requestFocus();
                    }
					showKeyboard(countlqty);
					if (stockcount.getCountLQty() == 0) {
						countlqty.setText("");
					}
					return false;
				}
			});
			countqty.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					mtblRow1.setVisibility(View.GONE);
					EditText qy = (EditText) v;
					qy.setFocusable(true);
					qy.setFocusableInTouchMode(true);
					clearViews();
					if (!v.hasFocus()) {
						v.requestFocus();

                    }
					showKeyboard(countqty);
					if (stockcount.getCountQty() == 0) {
						countqty.setText("");
					}
	//				focus=0;
					return false;
				}
			});

			countcqty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_DONE) {

						if(mproductCode_ed.length()>0){
							mproductCode_ed.setText("");
							mproductCode_ed.requestFocus();
//							mproductCode_ed.setFocusable(true);
//							mproductCode_ed.setFocusableInTouchMode(true);

							showKeyboard(mproductCode_ed);

							mtblRow1.setVisibility(View.GONE);
							System.out.println("Text [" + mproductCode_ed.getText().toString() + "]");
							setFilterType("Product");
							getFilter().filter(mproductCode_ed.getText().toString());
						}else{
							hideKeyboard();
						}
						qtyCount=-1;
						focus =0;
						return true;
					}

					return false;
				}
			});

			countlqty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

					if (actionId == EditorInfo.IME_ACTION_DONE) {

						if(mproductCode_ed.length()>0){
							mproductCode_ed.setText("");
							mproductCode_ed.requestFocus();
//							mproductCode_ed.setFocusable(true);
//							mproductCode_ed.setFocusableInTouchMode(true);

							showKeyboard(mproductCode_ed);

							mtblRow1.setVisibility(View.GONE);
							System.out.println("Text [" + mproductCode_ed.getText().toString() + "]");
							setFilterType("Product");
							getFilter().filter(mproductCode_ed.getText().toString());
						}else{
							hideKeyboard();
						}
						qtyCount=-1;
						focus =0;
						return true;
					}


					return false;
				}
			});

			countqty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
											  KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_DONE) {

						if(mproductCode_ed.length()>0){
							mproductCode_ed.setText("");
							mproductCode_ed.requestFocus();
//							mproductCode_ed.setFocusable(true);
//							mproductCode_ed.setFocusableInTouchMode(true);

							showKeyboard(mproductCode_ed);

							mtblRow1.setVisibility(View.GONE);
							System.out.println("Text [" + mproductCode_ed.getText().toString() + "]");
							setFilterType("Product");
							getFilter().filter(mproductCode_ed.getText().toString());
						}else{
							hideKeyboard();
						}
						qtyCount=-1;
						focus =0;
						return true;
					}
					return false;
				}
			});

			view.setOnClickListener(new OnClickListener() {

			    @Override
			    public void onClick(View v) {
			    	 visibleViews();
			    	 hideKeyboard();
						StockCount selectedProduct = stockAdapter.getItem(position);

//						mproductCode_ed.setText(selectedProduct.getProductCode());
						mproductName_ed.setText(selectedProduct.getProductName());
						
						Cursor tcursor = SOTDatabase.getCursor();
				        int tslno = tcursor.getCount()+1;
				        Log.d("tslno", ":"+tslno);       
				        SOTDatabase.deleteBatchSno(tslno+"");

						mpcsperQty_tv.setText(String.valueOf(selectedProduct.getPcsPerCarton()));
						mcurrentQty_tv.setText(String.valueOf(selectedProduct.getQty()));
						mqty_ed.setText(String.valueOf(selectedProduct.getQty()));
						
//						HashMap<String, EditText> hm = new HashMap<String, EditText>();
//						
//						hm.put("Productcode", mproductCode_ed);
//						hm.put("Productname", mproductName_ed);
//						hm.put("Cartonqty", mcarton_ed);
//						hm.put("Looseqty", mloose_ed);
//						hm.put("Qty", mqty_ed);
//						hm.put("Stock", mcurrentQty_tv);
//						hm.put("Cartonperqty", mpcsperQty_tv);
//						
//						strCartonPerQty = String.valueOf(selectedProduct.getPcsPerCarton());
//						haveBatch = String.valueOf(selectedProduct.getHavebatch());
//						haveExpiry = String.valueOf(selectedProduct.getHavexpiry());
//						
//						String haveBatchOnStockIn = SalesOrderSetGet.getHaveBatchOnStockIn();
//						if (haveBatchOnStockIn.matches("True")) {
//							Log.d("haveBatchOnStockIn", haveBatchOnStockIn);
//							if (haveBatch.matches("True") || haveExpiry.matches("True")) {
//								Log.d("haveBatch", "haveExpiry");
//								String code = selectedProduct.getProductCode();
//								String name = selectedProduct.getProductName();
//								batchDialog.initiateBatchPopupWindow(
//										StockTakeAddProduct.this, haveBatch, haveExpiry,
//										code, name, strCartonPerQty, hm, "");
////								noBatchvalue();
//							} else {
//								Log.d("no haveBatch", "no haveExpiry");
////								noBatchvalue();
//							}
//
//						} else {
//							Log.d("no haveBatchOnStockIn", "no haveBatchOnStockIn");
////							noBatchvalue();
//						}

			    }
			});

			return view;
		}
	//
//		class ViewHolder {
//			TextView code;
//			TextView name;
//			TextView category;
//			TextView subcategory;
//			TextView pcspercarton;
//			TextView noofcarton;
//			TextView currentqty;
////			EditText countcqty, countlqty, countqty;
//		}


		@Override
		public Filter getFilter() {
			Log.d("getFilter",""+"getFilter");
			if (sampleFilter == null)
				sampleFilter = new SampleFilter();
			return sampleFilter;
		}
		public int getCount() {
			return mData.size();
		}
		private class SampleFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				String category = null, subcategory = null;
				// We implement here the filter logic
				Log.d("getFilterType",""+getFilterType());
					if (constraint != null && constraint.toString().length() > 0) {
					// We perform filtering operation
					if (getFilterType().matches("Category && SubCategory")) {
						
						StringTokenizer tokens = new StringTokenizer(constraint.toString(), ",");
						 category = tokens.nextToken();
						 subcategory = tokens.nextToken();
						 Log.d("filter category", ""+category);
						  Log.d("filter subcategory", ""+subcategory);
					}
					
					
					ArrayList<StockCount> FilteredArrList = new ArrayList<StockCount>();
					FilteredArrList.clear();
					for (int i = 0, l = mOriginalData.size(); i < l; i++) {
						StockCount stockcount = mOriginalData.get(i);

					if (getFilterType().matches("Category")) {
//						Log.d("filter Category", "-> Category");
						Log.d("CategoryCode()",stockcount.getCategoryCode());
						Log.d("constraint", String.valueOf(constraint));
					//	if (stockcount.getCategoryCode().toLowerCase(Locale.getDefault()).contains(constraint)){
						if (stockcount.getCategoryCode().equalsIgnoreCase(String.valueOf(constraint))){
							FilteredArrList.add(stockcount);
						}
						
					} else if (getFilterType().matches("SubCategory")) {
//						Log.d("filter SubCategory", "-> SubCategory");
					//	if (stockcount.getSubcategoryCode().toLowerCase(Locale.getDefault()).contains(constraint))
						if (stockcount.getSubcategoryCode().equalsIgnoreCase(String.valueOf(constraint))){
							FilteredArrList.add(stockcount);
						}

						
					} else if (getFilterType().matches("Product")) {
						
						 if((select_category!=null && !select_category.isEmpty()) && (select_subcategory!=null && !select_subcategory.isEmpty())){
//							 Log.d("filter Category && SubCategory && Product", "-> Category && SubCategory && Product");
								if ((stockcount.getProductCode().equalsIgnoreCase(String.valueOf(constraint))
										|| stockcount.getProductName().equalsIgnoreCase(String.valueOf(constraint)))
										&& stockcount.getCategoryCode().equalsIgnoreCase(select_category)
										&& (stockcount.getSubcategoryCode().equalsIgnoreCase(select_subcategory)))
									FilteredArrList.add(stockcount);
								
				          }else if(select_category!=null && !select_category.isEmpty()){
//				        	  Log.d("filter Category && Product", "-> Category && Product");
								if ((stockcount.getProductCode().equalsIgnoreCase(String.valueOf(constraint))
										|| stockcount.getProductName().equalsIgnoreCase(String.valueOf(constraint)))
										&& stockcount.getCategoryCode().equalsIgnoreCase(select_category))
									FilteredArrList.add(stockcount);
								
				          }else if(select_subcategory!=null && !select_subcategory.isEmpty()){
//				        	  Log.d("filter SubCategory && Product", "-> SubCategory && Product");
								if ((stockcount.getProductCode().equalsIgnoreCase(String.valueOf(constraint))
										|| stockcount.getProductName().equalsIgnoreCase(String.valueOf(constraint)))
										&& (stockcount.getSubcategoryCode().equalsIgnoreCase(select_subcategory)))
									FilteredArrList.add(stockcount);
								
				          }else{
				        	  Log.d("filterProduct", "-> Product");
								if (stockcount.getProductCode().toUpperCase().contains(String.valueOf(constraint).toUpperCase())
										|| stockcount.getProductName().toUpperCase().contains(String.valueOf(constraint).toUpperCase())) {
									FilteredArrList.add(stockcount);

//									if (stockcount.getProductCode().toLowerCase(Locale.getDefault()).m(constraint))
//										Log.d("show","keyboard");
//									}

								}


				          }
						
					}else if (getFilterType().matches("Category && SubCategory")) {
//						Log.d("filter Category && SubCategory", "-> Category && SubCategory");
						if (category != null && category.toString().length() > 0 && subcategory != null && subcategory.toString().length() > 0 ) {
							/*if (stockcount.getCategoryCode().toLowerCase(Locale.getDefault()).contains(category)
									&& (stockcount.getSubcategoryCode().toLowerCase(Locale.getDefault()).contains(subcategory)))*/
							if (stockcount.getCategoryCode().equalsIgnoreCase(category)
									&& (stockcount.getSubcategoryCode().equalsIgnoreCase(subcategory))){
								FilteredArrList.add(stockcount);
							}

						}
						
					}else{
						Log.d("filter else", "-> else");
						FilteredArrList.add(stockcount);
						
					}
					}

					// set the Filtered result to return
					results.count = FilteredArrList.size();
					results.values = FilteredArrList;
					
					Log.d("results.count", ""+FilteredArrList.size());
					Log.d("results.values", ""+FilteredArrList);
				}else {
					synchronized (this) {
						results.values = mOriginalData;
						results.count = mOriginalData.size();
					}

				}

				return results;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				
				mData = (ArrayList<StockCount>) results.values;
//				notifyDataSetChanged();
				clear();
                if(mData.size()==1){
                	qtyCount = 0;
					focus=1;
				}else{
					qtyCount = -1;
					focus=0;
				}

// else{
//					mData.get(i).setQtyFocus(false);
//				}
				for (int i = 0, l = mData.size(); i < l; i++)

					//{
					add(mData.get(i));

				//}
				notifyDataSetInvalidated();
//				if(mproductCode_ed.getText().toString().matches("")){
//					qtyCount = -1;
//				}else{
//					new GetBarCode().execute();
//				}
//				String prodcode = mproductCode_ed.getText().toString();
//				if(!prodcode.matches("")){
//					new GetBarCode().execute();
//				}

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

				String qtyString = s.toString().trim();
				int quantity = qtyString.equals("") ? 0 : Integer.valueOf(qtyString);

				EditText qtyView = (EditText) view.findViewById(R.id.textView9);

				EditText cqtyView = (EditText) view.findViewById(R.id.textView7);

				EditText lqtyView = (EditText) view.findViewById(R.id.textView8);

				StockCount stockcount = (StockCount) qtyView.getTag();
				qtyView.setSelection(qtyView.getText().length());

				if (stockcount.getCountQty() != quantity) {


					if (!calCarton.matches("0")) {

						stockcount.setCountQty(quantity);
						
						int cqty = quantity / stockcount.getPcsPerCarton();
						stockcount.setCountCQty(cqty);

						int lqty = quantity % stockcount.getPcsPerCarton();
						stockcount.setCountLQty(lqty);

						Log.d("lqty", "" + lqty);
						Log.d("cqty", "" + cqty);
						
						if (stockcount.getCountQty() != 0) {
							qtyView.setText(String.valueOf(stockcount.getCountQty()));
						} else {
							qtyView.setText("0");
						}

						if (stockcount.getCountCQty() != 0) {
							cqtyView.setText(String.valueOf(stockcount.getCountCQty()));
						} else {
							cqtyView.setText("0");
						}

						if (stockcount.getCountLQty() != 0) {
							lqtyView.setText(String.valueOf(stockcount.getCountLQty()));
						} else {
							lqtyView.setText("0");
						}


					} else {

						stockcount.setCountQty(quantity);

						if (stockcount.getCountQty() != 0) {
							qtyView.setText(String.valueOf(stockcount.getCountQty()));
						} else {
							qtyView.setText("0");
						}
					}

				}

				totalQty();
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

				String cqtyString = s.toString().trim();
				int cquantity = cqtyString.equals("") ? 0 : Integer
						.valueOf(cqtyString);

				EditText qtyView = (EditText) view.findViewById(R.id.textView9);

				EditText cqtyView = (EditText) view.findViewById(R.id.textView7);

				StockCount stockcount = (StockCount) cqtyView.getTag();
				cqtyView.setSelection(cqtyView.getText().length());

				if (stockcount.getCountCQty() != cquantity) {

					if (!calCarton.matches("0")) {
						if (stockcount.getCountLQty() > 0) {

							int qty = cquantity * stockcount.getPcsPerCarton()
									+ stockcount.getCountLQty();
							Log.d("qty", "" + qty);
							stockcount.setCountQty(qty);
						} else {
							int qty = cquantity * stockcount.getPcsPerCarton();
							Log.d("qty", "" + qty);
							stockcount.setCountQty(qty);

						}
						stockcount.setCountCQty(cquantity);
						if (stockcount.getCountCQty() != 0) {
							cqtyView.setText(String.valueOf(stockcount.getCountCQty()));
						} else {
							cqtyView.setText("0");
						}

						if (stockcount.getCountQty() != 0) {

							qtyView.setText(String.valueOf(stockcount.getCountQty()));
						} else {
							qtyView.setText("0");
						}

					} else {
						stockcount.setCountCQty(cquantity);

					}

				}
//				totalQty();

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
				String cqtyString = s.toString().trim();
				int lquantity = cqtyString.equals("") ? 0 : Integer.valueOf(cqtyString);

				EditText qtyView = (EditText) view.findViewById(R.id.textView9);

				EditText cqtyView = (EditText) view.findViewById(R.id.textView7);

				EditText lqtyView = (EditText) view.findViewById(R.id.textView8);

				StockCount stockcount = (StockCount) lqtyView.getTag();
				lqtyView.setSelection(lqtyView.getText().length());

				if (stockcount.getCountLQty() != lquantity) {

					if (!calCarton.matches("0")) {
						
							if (cqtyView.getText().toString().equals("")
									|| cqtyView.getText().length() == 0) {
								stockcount.setCountQty(lquantity);
								stockcount.setCountLQty(lquantity);
							} else {
								int qty = stockcount.getCountCQty() * stockcount.getPcsPerCarton()
										+ lquantity;
								Log.d("qty", "" + qty);
								stockcount.setCountQty(qty);
								stockcount.setCountLQty(lquantity);
							}


						if (stockcount.getCountCQty() != 0) {
							cqtyView.setText(String.valueOf(stockcount.getCountCQty()));
						} else {
							cqtyView.setText("0");
						}

						if (stockcount.getCountLQty() != 0) {
							lqtyView.setText(String.valueOf(stockcount.getCountLQty()));
						} else {
							lqtyView.setText("0");
						}

						if (stockcount.getCountLQty() != 0) {

							qtyView.setText(String.valueOf(stockcount.getCountLQty()));
						} else {
							qtyView.setText("0");
						}

					} else {
						stockcount.setCountLQty(lquantity);
					
					}
				}

//				totalQty();
				return;
			}
		}
	}
	
	/** Adapter End    **/

	public class AsyncSaveStock extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {

				saveStockListRow();
				// End
			}catch(Exception e){
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Schedule.setStockcount(alstockCount);
		Intent i= new Intent(StockTakeAddProduct.this, StockTakeSummary.class);
//		i.putExtra(ACTIVITY_STOCK_TAKE_PRODUCT, alstockCount);
		startActivity(i);
		finish();
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

	protected void showKeyboard(EditText editText) {
		Log.d("showKeyboard", "Show");
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(editText, 0);
		editText.requestFocus();
	}

	protected void showForcedKeyboard(EditText editText) {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
		editText.requestFocus();
	}

	protected void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (getCurrentFocus() != null) {
			inputManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onListItemClick(String item) {
		menu.toggle();
	}

	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Deleting");
		alertDialog.setMessage("Stock Take products will clear. Do you want to proceed");
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						mIntent.setClass(StockTakeAddProduct.this, StockTakeHeader.class);
						startActivity(mIntent);
						finish();
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
	public void onBackPressed() {
		if ((mStocktakeNo.getText().toString() != null && !mStocktakeNo
				.getText().toString().isEmpty())) {
			mIntent.setClass(StockTakeAddProduct.this, StockTakeHeader.class);
		} else {
//			mIntent.setClass(StockTakeAddProduct.this,
//					StockTakeHeaderDetail.class);
		}
		startActivity(mIntent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if(result != null) {
			if(result.getContents() == null) {
				Log.d("MainActivity", "Cancelled scan");
				Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
			} else {

//				Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

				mproductCode_ed.setText(""+result.getContents());
				Log.d("MainActivity", "Scanned");
				showKeyboard(mproductCode_ed);

				if(!result.getContents().matches("")){
					new GetBarCode(result.getContents()).execute();
				}
			}
		} else {
			// This is important, otherwise the result will not be passed to the fragment
			super.onActivityResult(requestCode, resultCode, data);
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
					sales_prodCode, "fncGetProduct");

			/*alBatchStock = SalesProductWebService.getProductBatchStock(
					sales_prodCode, "fncGetProductBatchStock");*/

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

				HashMap<String, EditText> hm = new HashMap<String, EditText>();

				hm.put("Productcode", sl_codefield);
				hm.put("Productname", sl_namefield);
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
						/*if (mflag.equalsIgnoreCase("ReceiveStock")) {
							name = sl_namereceivefield.getText().toString();
						}else{*/
							name = sl_namefield.getText().toString();
					//	}

						Log.d("alBatchStock d",
								"batch click" + alBatchStock.toString());

						/*if (!alBatchStock.isEmpty()) {
							transferBatchDialog.initiateBatchPopupWindow(
									TransferAddProduct.this, haveBatch,
									haveExpiry, code, name, slCartonPerQty,
									slPrice, hm, alBatchStock);
						} else {*/
							noBatchvalue();
						//}

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
			}

			if(filterClick!=null && !filterClick.isEmpty() && filterClick.matches("true")){
				String db_pcode = SOTDatabase.getProductCode(codefield);
				Log.d("db_pcode", "->"+db_pcode);

				if(db_pcode != null && !db_pcode.isEmpty()){
					double pqty = SOTDatabase.getProductQty(db_pcode);
					sl_qty.setText(""+pqty);
					double cqty = SOTDatabase.getCartonQty(db_pcode);
					sl_cartonQty.setText(""+cqty);
					double lqty = SOTDatabase.getLooseQty(db_pcode);
					sl_looseQty.setText(""+lqty);
					sl_qty.requestFocus();
					sl_qty.setSelection(sl_qty.length());
				}
			}
			progressBarClose();
		}
	}

	private void noBatchvalue() {

		sl_codefield.setText("");
		sl_namefield.setText("");
		sl_cartonQty.setText("");
		sl_looseQty.setText("");
		sl_qty.setText("");
		sl_uom.setText("");
		sl_stock.setText("");
		sl_cartonPerQty.setText("");

		Toast.makeText(StockTakeAddProduct.this, "No item to transfer",
				Toast.LENGTH_LONG).show();

	    progressBarClose();

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

		progressBarClose();
	}

	private class ProductStockAsyncCall extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			cmpnyCode = SupplierSetterGetter.getCompanyCode();
			LocationCode = SalesOrderSetGet.getLocationcode();

			Log.d("cmpnyCode",cmpnyCode);
			Log.d("LocationCode",LocationCode);
			Log.d("sales_prodCode",sales_prodCode);

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

	private class CheckProductBarcode extends AsyncTask<Void, Void, Void> {

		String ed_code = "";

		@Override
		protected void onPreExecute() {
			mBarcodeJsonString = "";
			progressBarOpen();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			ed_code = sl_codefield.getText().toString();
			Log.d("barcodecheck",ed_code);
			mHashMap.clear();
			mHashMap.put("CompanyCode", cmpnyCode);
			mHashMap.put("Barcode", ed_code);

			try {
				mBarcodeJsonString = SalesOrderWebService.getSODetail(mHashMap,
						"fncGetProductBarCode");
				Log.d("mBarcodeJsonString", "-->" + mBarcodeJsonString);
				mBarcodeJsonObject = new JSONObject(mBarcodeJsonString);
				mBarcodeJsonArray = mBarcodeJsonObject
						.optJSONArray("SODetails");
				int lengthJsonBarcodeArr = mBarcodeJsonArray.length();

				if (lengthJsonBarcodeArr > 0) {
					JSONObject jsonChildNode = mBarcodeJsonArray
							.getJSONObject(0);
					String productbarcode = jsonChildNode.optString(
							PRODUCT_CODE).toString();
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

		/*	String ed_code = sl_codefield.getText().toString();
			if(!ed_code.matches("")){*/
		boolean flag = false;
			if (mBarcodeJsonString != null && !mBarcodeJsonString.isEmpty()) {
				for (int i = 0; i < alstockCount.size(); i++) {
					if (alstockCount.get(i).getProductCode().equals(sales_prodCode)) {
						Log.d("if", "if");
						flag = true;
						break;

					} else {
						Log.d("else", "else");
						flag = false;
						//Toast.makeText(StockTakeAddProduct.this, "No Product Found", Toast.LENGTH_SHORT).show();
					}
				}

				if(!flag){
					Toast.makeText(StockTakeAddProduct.this, "No Product Found", Toast.LENGTH_SHORT).show();
				}else{
					new AsyncCallSaleProduct("false").execute();
				}
			}else{
				sales_prodCode = ed_code;
				new AsyncCallSaleProduct("false").execute();
			}
			//}
		}

	}

	private class AsyncCallWSSearchProduct extends AsyncTask<Object, Void, Void> {

		private HashMap<String, String> sHashMap = new HashMap<>();
		@Override
		protected void onPreExecute() {
			progressBarOpen();
			searchProductArr.clear();
			sHashMap.clear();
		}

		@Override
		protected Void doInBackground(Object... param) {
			String stockTake = (String) param[0];

			/*searchProductArr = NewProductWebService.searchProduct(select_category,
					select_subcategory, "fncGetStockCountDetailByNo");*/


			sHashMap.put("CompanyCode", cmpnyCode);
			sHashMap.put("LocationCode", locCode);
			sHashMap.put("StockTakeNo", stockTake);
			sHashMap.put("CategoryCode", select_category);
			sHashMap.put("SubCategoryCode", select_subcategory);

			try {
				mSearchJsonString = SalesOrderWebService.getSODetail(sHashMap,
						"fncGetStockCountDetailByNo");
				Log.d("mBarcodeJsonString", "-->" + mSearchJsonString);
				mSearchJsonObject = new JSONObject(mSearchJsonString);
				mSearchJsonArray = mSearchJsonObject
						.optJSONArray("SODetails");
				int lengthJsonArr = mSearchJsonArray.length();

				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = mSearchJsonArray.getJSONObject(i);

					String ProductCode = jsonChildNode.optString("ProductCode")
							.toString();
					String ProductName = jsonChildNode.optString("ProductName")
							.toString();
					/*String WholeSalePrice = jsonChildNode.optString(
							"WholeSalePrice").toString();*/
					String countcqty = jsonChildNode.getString(COUNTCQTY);
					String countlqty = jsonChildNode.getString(COUNTLQTY);
					String countqty = jsonChildNode.getString(COUNTQTY);

					HashMap<String, String> hm = new HashMap<String, String>();

					hm.put("ProductCode", ProductCode);
					hm.put("ProductName", ProductName);
					hm.put("Carton", countcqty);
					hm.put("Loose", countlqty);
					hm.put("Qty", countqty);
					searchProductArr.add(hm);

					Log.d("Product search List", searchProductArr.toString());

				}

			} catch (JSONException e) {
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
				searchProductList("");
			} catch (ParseException e) {

				e.printStackTrace();
			}

			progressBarClose();
		}
	}

	public void searchProductList(String flag) throws ParseException {

		adapter = new AddSimpleAdapter(StockTakeAddProduct.this,
				searchProductArr, R.layout.sale_productitem,flag, new String[] {
				"ProductCode", "ProductName", "WholeSalePrice" },
				new int[] { R.id.txt_code, R.id.txt_name, R.id.txt_price });
		productFilterList.setAdapter(adapter);
	}
}