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
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.NewProductWebService;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.LocationGetSet;
import com.winapp.sot.FilterCS.OnFilterCompletionListener;


public class StockRequestAddProduct extends SherlockFragmentActivity implements
SlideMenuFragment.MenuClickInterFace, OnItemClickListener {

	private static String webMethName = "fncGetProduct";
	private static String webMethNamebar = "fncGetProductBarCode";
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String PRODUCT_NAME = "ProductName";
	private static final String PRODUCTNAME_BARCODE = "ProductCode";
	private static final String PRODUCT_BARCODE = "Barcode";
	private String cmpnyCode = "", LocationCode = "", stock, product_stock_jsonString = null, str_ssupdate, str_sscancel, str_sssno, ss_Cqty = "", catStr = "", subCatStr = "", cstcode = "", cstgrpcd = "", calCarton = "", beforeLooseQty, beforeFoc, taxType = "", taxValue = "", sales_prodCode, keyValues = "", values = "", slPrice = "", slUomCode = "", slCartonPerQty = "", valid_url, productTxt, productresult, barcoderesult, barcodeTxt, mProductJsonString = "", mBarcodeJsonString = "",mainStock="",stock_jsonString = "";
	private JSONObject product_stock_jsonResponse = null, mProductJsonObject = null, mBarcodeJsonObject = null,stock_jsonResponse = null;
	private JSONArray product_stock_jsonMainNode = null, mProductJsonArray = null, mBarcodeJsonArray = null,stock_jsonNode = null;
	private HashMap<String, String> mHashMap;
	private SlidingMenu menu;
	private TextView textView1,txt_main_stock;
	private ArrayList<HashMap<String, String>> searchProductArr = new ArrayList<HashMap<String, String>>();
	private Button sl_addProduct, sl_summary;
	private EditText editText, sl_stock, editText1, sl_codefield, sl_namefield, sl_cartonQty, sl_looseQty, sl_qty,
			sl_uom, sl_cartonPerQty, sl_total, sl_tax, sl_netTotal,sl_main_stock;
	private AlertDialog myalertDialog = null;
	private ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	private CustomAlertAdapterProd arrayAdapterProd;
	private ArrayList<HashMap<String, String>> searchResults;
	private int textlength = 0, sl_no = 1;
	private ArrayList<HashMap<String, String>> getArraylist = new ArrayList<HashMap<String, String>>();
	private ProgressBar progressBar;
	private LinearLayout spinnerLayout, salesproduct_layout;
	private ArrayList<String> getSalesProdArr = new ArrayList<String>();
	private static ArrayList<String> companyArr = new ArrayList<String>();
	private Cursor cursor;
	private ListView productFilterList;
	private double netTtal = 0, taxAmount = 0;
	private Bundle extra;
	private double tt;
	private TextWatcher cqtyTW, lqtyTW, qtyTW;
	private FilterCS filtercs;
	private HashMap<String, String> producthashValue = new HashMap<String, String>();
	private HashMap<String, String> productValue = new HashMap<String, String>();
	private SimpleAdapter adapter;
	private String Weight = "";
	JSONObject jsonResponse;
	JSONArray jsonMainNode;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.stockrequest_addproduct);

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
		filtercs = new FilterCS(StockRequestAddProduct.this);
		salesproduct_layout = (LinearLayout) findViewById(R.id.salesproduct_layout);

		sl_summary = (Button) findViewById(R.id.sl_summary);
		sl_addProduct = (Button) findViewById(R.id.sl_addProduct);
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
		productFilterList = (ListView) findViewById(R.id.productFilterList);
		txt_main_stock = (TextView)findViewById(R.id.txt_main_stock);
		sl_main_stock =(EditText)findViewById(R.id.sl_main_stock);

		valid_url = FWMSSettingsDatabase.getUrl();
		new SalesProductWebService(valid_url);
		new NewProductWebService(valid_url);
		new SalesOrderWebService(valid_url);
		SOTDatabase.init(StockRequestAddProduct.this);
		calCarton = LogOutSetGet.getCalcCarton();
		mHashMap = new HashMap<String, String>();

//		calCarton="0";

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
		}

		getSalesProdArr.clear();
		companyArr.clear();
		searchProductArr.clear();
//		Log.d("GRATaxT", SalesOrderSetGet.getCompanytax());
		taxType = SalesOrderSetGet.getCompanytax();

		cstcode = SalesOrderSetGet.getSuppliercode();
		cstgrpcd = SalesOrderSetGet.getSuppliergroupcode();

		cmpnyCode = SupplierSetterGetter.getCompanyCode();

		new GetProduct().execute();

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
//			    
				filtercs.filterDialog();
				filtercs.OnFilterCompletionListener(new OnFilterCompletionListener() {
					@Override
					public void onFilterCompleted(String category, String subcategory) {

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
					if(SalesOrderSetGet.getTransfer_stockReq().matches("1")) {
						Log.d("mainStockCheck", mainStock);
						double qty = Double.parseDouble(sl_qty.getText().toString());
						double m_stock = Double.parseDouble(mainStock);
						Log.d("m_stockCheck", "-->" + m_stock + "qty:" + qty);
						if (qty > m_stock) {
							Toast.makeText(StockRequestAddProduct.this, "No Stock in main location",
									Toast.LENGTH_SHORT).show();
						}else{
							storeInDatabase();
							Toast.makeText(StockRequestAddProduct.this, "Updated ",
									Toast.LENGTH_LONG).show();
							Intent i = new Intent(StockRequestAddProduct.this,
									StockRequestSummary.class);
							startActivity(i);
							StockRequestAddProduct.this.finish();
						}
					}else{
						storeInDatabase();
						Toast.makeText(StockRequestAddProduct.this, "Updated ",
								Toast.LENGTH_LONG).show();
						Intent i = new Intent(StockRequestAddProduct.this,
								StockRequestSummary.class);
						startActivity(i);
						StockRequestAddProduct.this.finish();
					}


				} else {
					if(SalesOrderSetGet.getTransfer_stockReq().matches("1")) {
						Log.d("mainStockCheck", mainStock);
						double qty = Double.parseDouble(sl_qty.getText().toString());
						double m_stock = Double.parseDouble(mainStock);
						Log.d("m_stockCheck", "-->" + m_stock + "qty:" + qty);
						if (qty > m_stock) {
							Toast.makeText(StockRequestAddProduct.this, "No Stock in main location",
									Toast.LENGTH_SHORT).show();
						}else{
							storeInDatabase();
						}
					}else{
						storeInDatabase();
					}

				}

			}
		});

		sl_summary.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(StockRequestAddProduct.this,
						StockRequestSummary.class);
				startActivity(i);
				StockRequestAddProduct.this.finish();
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
					// TODO Auto-generated method stub

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
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				beforeLooseQty = sl_looseQty.getText().toString();

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				if (calCarton.matches("0")) {
					looseQtyCalcPcsOne(0);
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

	public void clQty() {
		String qty = sl_qty.getText().toString();
		String crtnperQty = sl_cartonPerQty.getText().toString();
		int q = 0, r = 0;

		if (crtnperQty.matches("0") || crtnperQty.matches("null") || crtnperQty.matches("0.00")) {
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
		try {

			String crtnQty = sl_cartonQty.getText().toString();

			if (crtnQty.matches("")) {
				crtnQty = "0";
			}

			if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

				double cartonQtyCalc = Double.parseDouble(crtnQty);
				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
				double qty = 0;
				double ProductWeight = 0, lsQtyCnvrt = 0;
				String lsQty = sl_looseQty.getText().toString();

		/*	if (!lsQty.matches("")) {
				double lsQtyCnvrt = Double.parseDouble(lsQty);
				qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

			} else {
				qty = cartonQtyCalc * cartonPerQtyCalc;
			}*/

				if (!Weight.matches("")) {
					ProductWeight = Double.parseDouble(Weight);
				} else {
					ProductWeight = 0;
				}

				if (!lsQty.matches("")) {
					lsQtyCnvrt = Double.parseDouble(lsQty);
				} else {
					lsQtyCnvrt = 0;
				}

				if (cartonPerQtyCalc > 1) {

					if (ProductWeight > 0) {
						qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
					} else {
						qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
					}

				} else {
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
		} catch (NumberFormatException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void looseQtyCalcPcsOne(int priceFlag) {
		try {
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
				double qty = 0;
				double ProductWeight = 0, lsQtyCnvrt = 0;
//			qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

				if (!Weight.matches("")) {
					ProductWeight = Double.parseDouble(Weight);
				} else {
					ProductWeight = 0;
				}

				if (!lsQty.matches("")) {
					lsQtyCnvrt = Double.parseDouble(lsQty);
				} else {
					lsQtyCnvrt = 0;
				}

				if (cartonPerQtyCalc > 1) {
					if (ProductWeight > 0) {
						qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
					} else {
						qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
					}
				} else {
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
				double qty = 0;
				double ProductWeight = 0, lsQtyCnvrt = 0;

			/*if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
				double cartonQtyCalc = Double.parseDouble(crtnQty);
				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);

				qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
			} else {
				qty = looseQtyCalc;
			}*/

				if (!Weight.matches("")) {
					ProductWeight = Double.parseDouble(Weight);
				} else {
					ProductWeight = 0;
				}

				if (!lsQty.matches("")) {
					lsQtyCnvrt = Double.parseDouble(lsQty);
				} else {
					lsQtyCnvrt = 0;
				}

				if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
					double cartonQtyCalc = Double.parseDouble(crtnQty);
					double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);


					if (cartonPerQtyCalc > 1) {

						if (ProductWeight > 0) {
							qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
						} else {
							qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
						}

					} else {
						if (ProductWeight > 0) {
							qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
						} else {

						}
					}
				} else {
					qty = looseQtyCalc * ProductWeight;
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
		} catch (NumberFormatException e) {
			e.printStackTrace();

		} catch (Exception e) {
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
			Toast.makeText(StockRequestAddProduct.this, "Select product code",
					Toast.LENGTH_SHORT).show();
		} else if (qtyStr.matches("")) {

				if (calCarton.matches("0")) {
					if (cartonQtyStr.matches("")) {
						Toast.makeText(StockRequestAddProduct.this, "Enter the carton",
								Toast.LENGTH_SHORT).show();
						sl_cartonQty.requestFocus();
					} else {
						save();
					}

				} else {
					Toast.makeText(StockRequestAddProduct.this, "Enter the quantity",
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
		String nameStr = sl_namefield.getText().toString();
		String cartonQtyStr = sl_cartonQty.getText().toString();
		String looseQtyStr = sl_looseQty.getText().toString();
		String qtyStr = sl_qty.getText().toString();
		String uomStr = sl_uom.getText().toString();
		String cartonPerQtyStr = sl_cartonPerQty.getText().toString();
		String totalStr = sl_total.getText().toString();
		String taxStr = sl_tax.getText().toString();
		String netTotalStr = sl_netTotal.getText().toString();


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

			SOTDatabase.updateProduct(codeStr, nameStr, cartonQty,
					looseQty, qty, 0, price, 0, uomStr, cartonPerQty, tt,
					tax, sbTtl, netT, i_sssno, "0.00", "0");

		} else {
			SOTDatabase.storeProduct(sl_no, codeStr, nameStr, cartonQty,
					looseQty, qty, 0, price, 0.0, uomStr, cartonPerQty, tt,
					tax, netT, taxType, taxValue, "", sbTtl, "0.00", "0", "0", "", "", "", "", "", "");

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
			sl_main_stock.setText("");
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
					// (edcodefield.getText().toString().equals(keyValue))
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
				StockRequestAddProduct.this);
		editText = new EditText(StockRequestAddProduct.this);
		final ListView listview = new ListView(StockRequestAddProduct.this);
		LinearLayout layout = new LinearLayout(StockRequestAddProduct.this);
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
				StockRequestAddProduct.this, al);
		listview.setAdapter(arrayAdapterProd);
		listview.setOnItemClickListener(StockRequestAddProduct.this);

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
						StockRequestAddProduct.this, searchResults);
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
				new AsyncCallSaleProduct().execute();
			} else {
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

				mProductJsonString = SalesOrderWebService.getSODetail(mHashMap, "fncGetProductForSearch");
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
			enableViews(salesproduct_layout, true);
		}

	}
/*	private class AsyncCallWSADDPRD extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			al.clear();
			getActionBar().setHomeButtonEnabled(false);
			menu.setSlidingEnabled(false);
			spinnerLayout = new LinearLayout(StockRequestAddProduct.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesproduct_layout, false);
			progressBar = new ProgressBar(StockRequestAddProduct.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
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
			// TODO Auto-generated method stub
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

	private class AsyncCallSaleProduct extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {

			getSalesProdArr.clear();
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

			getSalesProdArr = SalesProductWebService.getGraProduct(
					sales_prodCode, webMethName);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			new ProductStockAsyncCall().execute();

			if (!getSalesProdArr.isEmpty()) {

				slPrice = getSalesProdArr.get(0);
				slUomCode = getSalesProdArr.get(1);
				slCartonPerQty = getSalesProdArr.get(2);
				taxValue = getSalesProdArr.get(3);
				String codefield = getSalesProdArr.get(6);
				String namefield = getSalesProdArr.get(7);
				Weight = getSalesProdArr.get(8);

				Log.d("Weight", "" + Weight);

				sl_codefield.setText(codefield);
				sl_namefield.setText(namefield);

				sl_uom.setText(slUomCode);


				if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
						|| slCartonPerQty.matches("")) {

					if (calCarton.matches("0")) {
						sl_cartonQty.requestFocus();
					} else {
						sl_cartonQty.setFocusable(false);
						sl_cartonQty.setBackgroundResource(drawable.labelbg);

						sl_looseQty.setFocusable(false);
						sl_looseQty.setBackgroundResource(drawable.labelbg);

						sl_qty.requestFocus();
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.showSoftInput(sl_qty, InputMethodManager.SHOW_IMPLICIT);

					}

				} else {
					sl_cartonQty.setFocusableInTouchMode(true);
					sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

					sl_looseQty.setFocusableInTouchMode(true);
					sl_looseQty.setBackgroundResource(drawable.edittext_bg);

					sl_cartonQty.requestFocus();
				}

				sl_cartonPerQty.setText(slCartonPerQty);

				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesproduct_layout, true);

				sl_cartonQty.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(sl_cartonQty,
						InputMethodManager.SHOW_IMPLICIT);

			} else {
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
			enableViews(salesproduct_layout, true);
		}
	}

	public void searchProductList() throws ParseException {

		adapter = new AddSimpleAdapter(
				StockRequestAddProduct.this, searchProductArr,
				R.layout.sale_productitem, "", new String[]{"ProductCode",
				"ProductName", "WholeSalePrice"}, new int[]{
				R.id.txt_code, R.id.txt_name, R.id.txt_price});
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

					stock = jsonChildNode.optString("Qty")
							.toString();

					Log.d("stockQtyCheck", stock);


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

			if (SalesOrderSetGet.getTransfer_stockReq().matches("1")) {
				new ShowAllLocation().execute();
			}

		}
	}

	private void loadprogress() {
		spinnerLayout = new LinearLayout(StockRequestAddProduct.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(salesproduct_layout, false);
		progressBar = new ProgressBar(StockRequestAddProduct.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		spinnerLayout.addView(progressBar);
	}

	@Override
	public void onListItemClick(String item) {
		menu.toggle();
	}

	@Override
	public void onBackPressed() {
		if ((str_ssupdate != null) || (str_sscancel != null)) {
			Intent i = new Intent(StockRequestAddProduct.this,
					StockRequestSummary.class);
			startActivity(i);
			StockRequestAddProduct.this.finish();

		} else {
			Intent i = new Intent(StockRequestAddProduct.this,
					StockRequestHeaderDetail.class);
			startActivity(i);
			StockRequestAddProduct.this.finish();

		}
	}


	private class ShowAllLocation extends AsyncTask<String, String, String> {
		HashMap<String, String> hashValue = new HashMap<String, String>();

		@Override
		protected void onPreExecute() {

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
							locationGetSet.setLocatn(locationCode);
							if (mainLocation.matches("True")) {
								LocationGetSet.setIsMainLocation(locationCode);
							}
							Log.d("getIsmainlocation",LocationGetSet.getIsMainLocation());
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
			String mainLocation = LocationGetSet.getIsMainLocation();
			Log.d("mainLocation",mainLocation);
			new ProductStocksAsyncCall(mainLocation).execute();
			txt_main_stock.setVisibility(View.VISIBLE);
			sl_main_stock.setVisibility(View.VISIBLE);
		}
	}

	private class ProductStocksAsyncCall extends AsyncTask<Void, Void, Void> {
		String locatnCode;

		public ProductStocksAsyncCall(String mainLocation) {
			this.locatnCode = mainLocation;
			Log.d("locatnCode",locatnCode);
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			cmpnyCode = SupplierSetterGetter.getCompanyCode();

			productValue.put("CompanyCode", cmpnyCode);
			productValue.put("LocationCode", locatnCode);
			productValue.put("ProductCode", sales_prodCode);
			stock_jsonString = WebServiceClass.parameterService(
					productValue, "fncGetProductWithStock");

			try {

				stock_jsonResponse = new JSONObject(
						stock_jsonString);
				stock_jsonNode = stock_jsonResponse
						.optJSONArray("JsonArray");

				/*********** Process each JSON Node ************/
				int lengthJsonArr = stock_jsonNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode;

					jsonChildNode = stock_jsonNode.getJSONObject(i);

					mainStock = jsonChildNode.optString("Qty")
							.toString();

					Log.d("stockMainLocationCheck", mainStock);


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
				sl_main_stock.setText(" " + mainStock);
			}
		}
	}
}