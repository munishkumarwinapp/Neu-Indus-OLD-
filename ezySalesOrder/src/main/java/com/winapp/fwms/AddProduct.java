package com.winapp.fwms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;

public class AddProduct extends Activity implements OnItemClickListener {

	ImageButton back, logout, add, edit_product, newadd;
	Intent callSummary, callNew, calllogout, callsupplier;
	Button newPalette, summary, stupButton, stdownButton, ok, edupButton,
			eddownButton;
	EditText edpalettecount, edcodefield, ednamefield, edweight, edbarcode,
			edtotal, edpieceperqty, editText;
	TextView prodid, stnumber, ednumber;

	String edit_id, productTxt = null, barcodeTxt = null, OutputData = "",
			keyValue, value, productendson, productstartson, valid_url,
			keyP = "FWMS", editBrcd;
	ListView lv;
	int count = 1, textlength = 0, stuprange = 50, stdownrange = 1, stwght = 1,
			eduprange = 50, eddownrange = 1, edwght = 1, palettecount;
 
	static String productresult, barcoderesult;
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetProduct";
	private static String webMethNamebar = "fncGetProductBarCode";
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String PRODUCT_NAME = "ProductName";
	private static final String PRODUCT_WEIGHT = "Weight";
	private static final String PRODUCT_STARTSON = "WeightBarcodeStartsOn";
	private static final String PRODUCT_ENDSON = "WeightBarcodeEndsOn";
	private static final String PRODUCTNAME_BARCODE = "ProductCode";
	private static final String PRODUCT_BARCODE = "Barcode";

	AlertDialog alert;
	AlertDialog.Builder builder;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> paletteids = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> albarsplit = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> hashmap = new HashMap<String, String>();
	ArrayList<HashMap<String, String>> albarcode = new ArrayList<HashMap<String, String>>();
	ArrayList<String> alprodcode = new ArrayList<String>();
	ArrayList<String> albar = new ArrayList<String>();
	private AlertDialog myalertDialog = null;

	HashMap<String, String> hmsplitbc = new HashMap<String, String>();
	CustomAlertAdapterProd arrayAdapterProd;
	ArrayList<HashMap<String, String>> searchResults;
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> productweight = new ArrayList<HashMap<String, String>>();
	MySQLiteDataBase sqldb = new MySQLiteDataBase(this);

	ProductListAdapter crsrAdptr;
	Cursor cursor;

	ArrayList<HashMap<String, String>> datadb;

	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	LinearLayout addproduct_layout;
	ArrayList<String> editProductArr = new ArrayList<String>();
	ArrayList<String> editBarcodeArr = new ArrayList<String>();

	String keyProdCode, ProductCode, ProductName, WeightBarcodeStartsOn,
			WeightBarcodeEndsOn, HaveBatch, HaveExpiry, HaveMfgDate, weightFld,
			CategoryCode, SubCategoryCode, UOMCode, PcsPerCarton;
	InputMethodManager mgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addproduct_screen);

		FWMSSettingsDatabase.init(AddProduct.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new NewProductWebService(valid_url);
		new NewProductWebService(valid_url);
		Bundle b = getIntent().getExtras();
		datadb = sqldb.getAllProducts();
		addproduct_layout = (LinearLayout) findViewById(R.id.addproduct_layout);
		edit_product = (ImageButton) findViewById(R.id.edit_product);
		back = (ImageButton) findViewById(R.id.back);
		add = (ImageButton) findViewById(R.id.btplus);
		newPalette = (Button) findViewById(R.id.button1);
		summary = (Button) findViewById(R.id.button2);
		newadd = (ImageButton) findViewById(R.id.newproductbutton);
		lv = (ListView) findViewById(R.id.listView1);
		edcodefield = (EditText) findViewById(R.id.codefield);
		edpalettecount = (EditText) findViewById(R.id.palettecount);
		ednamefield = (EditText) findViewById(R.id.namefield);
		edweight = (EditText) findViewById(R.id.weightresult);
		edbarcode = (EditText) findViewById(R.id.weightbarcode);
		edtotal = (EditText) findViewById(R.id.edtotalweight);
		edpieceperqty = (EditText) findViewById(R.id.weightpieceperqty);

		String addProd = LogOutSetGet.getApplicationType();
		boolean addProdRslt = FormSetterGetter.isProductAdd();

		if (addProd.matches("S")) {
			newadd.setVisibility(View.INVISIBLE);
			edit_product.setVisibility(View.INVISIBLE);
		} else if (addProd.matches("W") || addProdRslt == true) {
			newadd.setVisibility(View.VISIBLE);
			edit_product.setVisibility(View.VISIBLE);
		}

		if (SupplierSetterGetter.getExpheader() == 0) {
			palettecount = sqldb.maxpalettecount();
			Log.d("palettecount", "" + palettecount);
			edpalettecount.setText("" + palettecount);
			if (b != null) {
				String keyS = b.getString("key");
				if (palettecount != 0) {
					if (keyP.equals(keyS)) {
						alertDialog();
					}
				} else {
					edpalettecount.setText("" + ++palettecount);
					Log.d("count1", "" + palettecount);

				}
			} else {
				if (palettecount != 0) {
					edpalettecount.setText("" + palettecount);
					Log.d("palettecount2", "" + palettecount);
				} else {
					edpalettecount.setText("" + ++palettecount);
					Log.d("count3", "" + palettecount);
				}
			}
		} else {
			edpalettecount.setText("" + SupplierSetterGetter.getExpheader());

			Log.d("palettecount setter getter", "" + palettecount);
		}
		editProductArr.clear();
		al.clear();
		AsyncCallWSADDPRD task = new AsyncCallWSADDPRD();
		task.execute();
		AsyncCallBARCODE barcodetask = new AsyncCallBARCODE();
		barcodetask.execute();
		registerForContextMenu(lv);
		getListView();
		double tot = sqldb.getTotal(edpalettecount.getText().toString());
		edtotal.setText(String.format("%.2f", tot));

		edit_product.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialogSearchForEditProduct();

			}
		});

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				callsupplier = new Intent(AddProduct.this, AddSupplier.class);
				startActivity(callsupplier);
				AddProduct.this.finish();
				SupplierSetterGetter.setExpheader(0);
			}
		});
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (edpieceperqty.getText().length() != 0
						&& edpieceperqty.getText().toString() != "") {
					addListitemPPerQty();
				} else {
					addListItemfield();

				}
			}
		});

		newPalette.setOnClickListener(new OnClickListener() {
			int palecount = SupplierSetterGetter.getExpheader();

			@Override
			public void onClick(View arg0) {

				if (!crsrAdptr.isEmpty()) {
					if (SupplierSetterGetter.getExpheader() == 0) {

						edpalettecount.setText("" + ++palettecount);
					} else {

						++palecount;
						edpalettecount.setText("" + palecount);

					}
					double tot = sqldb.getTotal(edpalettecount.getText()
							.toString());
					edtotal.setText(String.format("%.2f", tot));
					edweight.setText("");
					getListView();
				} else {
					Toast.makeText(getApplicationContext(), "Palette is Empty",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		edcodefield
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						edcodefield) {
					@Override
					public boolean onDrawableClick() {

						alertDialogSearch();
						return true;
					}
				});

		newadd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				LogOutSetGet.setAddProduct("StockIn");
				ClearFieldSetterGetter.setClearProduct(true);
				AddProductSetterGetter.setUpdate(false);
				callSummary = new Intent(AddProduct.this,
						AndroidTabLayoutActivity.class);
				startActivity(callSummary);
				AddProduct.this.finish();
			}
		});

		summary.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				callSummary = new Intent(AddProduct.this, Summary.class);
				startActivity(callSummary);
				AddProduct.this.finish();
				SupplierSetterGetter.setExpheader(0);
			}
		});

		edcodefield.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					editCodeField();
					return true;
				}
				return false;
			}
		});

		edbarcode.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT
						|| event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					if (!edbarcode.getText().toString().matches("")) {
						editBarCodeField();
					} else {

					}
					return true;
				}
				return false;
			}
		});

		edweight.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					if (edcodefield.getText().length() != 0
							&& edcodefield.getText().toString() != "") {
						if (ednamefield.getText().length() != 0
								&& ednamefield.getText().toString() != "") {
							if (edbarcode.getText().toString().matches("")
									&& edweight.getText().toString()
											.matches("")) {
								Toast.makeText(getApplicationContext(),
										"Please Enter Weight ",
										Toast.LENGTH_SHORT).show();
							}
							if (edweight.getText().length() != 0
									&& edweight.getText().toString() != "") {

								if (edpieceperqty.getText().length() != 0
										&& edpieceperqty.getText().toString() != "") {
									donePPQtyMethod();
								} else {
									doneMethod();
								}

								edweight.requestFocus();
							}

						} else {
							Toast.makeText(getApplicationContext(),
									"Please Select Product ",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getApplicationContext(),
								" Please Enter Code ", Toast.LENGTH_SHORT)
								.show();
					}
					return true;
				}
				return false;
			}
		});
	}

	public void addListItemfield() {
		int sno = 1;
		String prd;

		if (edcodefield.getText().length() != 0
				&& edcodefield.getText().toString() != "") {
			if (ednamefield.getText().length() != 0
					&& ednamefield.getText().toString() != "") {
				if (edweight.getText().toString().matches("")) {
					barcodeValue();
				}

				if (edweight.getText().length() != 0
						&& edweight.getText().toString() != "") {
					HashMap<String, String> queryValues = new HashMap<String, String>();
					if (datadb != null) {
						prd = sqldb
								.getProd(edpalettecount.getText().toString());
						Log.d("prd", prd);
						if (prd.equals(edpalettecount.getText().toString())) {
							int snos = sqldb.getnum(edpalettecount.getText()
									.toString());
							int sns = snos + 1;
							queryValues.put("paletteId", edpalettecount
									.getText().toString());
							queryValues.put("code", edcodefield.getText()
									.toString());
							queryValues.put("name", ednamefield.getText()
									.toString());
							if (edbarcode.getText().toString().equals("")) {
								String zero = "0";
								queryValues.put("barcode", zero);
							} else {
								queryValues.put("barcode", edbarcode.getText()
										.toString());
							}
							queryValues.put("weight", edweight.getText()
									.toString());
							queryValues.put("snum", "" + sns);
							Log.d("1", "" + sno);
						} else {
							queryValues.put("paletteId", edpalettecount
									.getText().toString());
							queryValues.put("code", edcodefield.getText()
									.toString());
							queryValues.put("name", ednamefield.getText()
									.toString());
							if (edbarcode.getText().toString().equals("")) {
								String zero = "0";
								queryValues.put("barcode", zero);
							} else {

								queryValues.put("barcode", edbarcode.getText()
										.toString());
							}
							queryValues.put("weight", edweight.getText()
									.toString());
							queryValues.put("snum", "" + sno);
							Log.d("2", "" + sno);
						}

					} else {
						queryValues.put("paletteId", edpalettecount.getText()
								.toString());
						queryValues.put("code", edcodefield.getText()
								.toString());
						queryValues.put("name", ednamefield.getText()
								.toString());
						if (edbarcode.getText().toString().equals("")) {
							String zero = "0";
							queryValues.put("barcode", zero);
						} else {
							queryValues.put("barcode", edbarcode.getText()
									.toString());
						}
						queryValues
								.put("weight", edweight.getText().toString());
						queryValues.put("snum", "" + sno);
						Log.d("3", "" + sno);
					}
					sqldb.insertproduct(queryValues);
					getListView();
					double tot = sqldb.getTotal(edpalettecount.getText()
							.toString());
					edtotal.setText(String.format("%.2f", tot));
					edweight.setText("");
					edbarcode.setText("");
				} else if (edweight.getText().toString().matches("")
						&& edbarcode.getText().toString().matches("")) {
					Toast.makeText(getApplicationContext(),
							"Please Enter Barcode or Weight ",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(getApplicationContext(),
						"Please Select Product ", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), " Please Enter Code ",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void addListitemPPerQty() {
		int sno = 1;
		String prd;
		int pieceperqty = Integer.parseInt(edpieceperqty.getText().toString());
		if(pieceperqty<500){
		
			if (edcodefield.getText().length() != 0
					&& edcodefield.getText().toString() != "") {
				if (ednamefield.getText().length() != 0
						&& ednamefield.getText().toString() != "") {

					if (edweight.getText().toString().matches("")) {
						barcodeValue();
					}

					if (edweight.getText().length() != 0
							&& edweight.getText().toString() != "") {
						
						for (int i = 0; i < pieceperqty; i++) {
						HashMap<String, String> queryValues = new HashMap<String, String>();
						if (datadb != null) {
							prd = sqldb.getProd(edpalettecount.getText()
									.toString());
							Log.d("prd", prd);
							if (prd.equals(edpalettecount.getText().toString())) {
								int snos = sqldb.getnum(edpalettecount
										.getText().toString());
								int sns = snos + 1;
								queryValues.put("paletteId", edpalettecount
										.getText().toString());
								queryValues.put("code", edcodefield.getText()
										.toString());
								queryValues.put("name", ednamefield.getText()
										.toString());
								if (edbarcode.getText().toString().equals("")) {
									String zero = "0";
									queryValues.put("barcode", zero);
								} else {
									queryValues.put("barcode", edbarcode
											.getText().toString());
								}
								queryValues.put("weight", edweight.getText()
										.toString());
								queryValues.put("snum", "" + sns);
								Log.d("1", "" + sno);
							} else {
								queryValues.put("paletteId", edpalettecount
										.getText().toString());
								queryValues.put("code", edcodefield.getText()
										.toString());
								queryValues.put("name", ednamefield.getText()
										.toString());
								if (edbarcode.getText().toString().equals("")) {
									String zero = "0";
									queryValues.put("barcode", zero);
								} else {

									queryValues.put("barcode", edbarcode
											.getText().toString());
								}
								queryValues.put("weight", edweight.getText()
										.toString());
								queryValues.put("snum", "" + sno);
								Log.d("2", "" + sno);
							}

						} else {
							queryValues.put("paletteId", edpalettecount
									.getText().toString());
							queryValues.put("code", edcodefield.getText()
									.toString());
							queryValues.put("name", ednamefield.getText()
									.toString());
							if (edbarcode.getText().toString().equals("")) {
								String zero = "0";
								queryValues.put("barcode", zero);
							} else {
								queryValues.put("barcode", edbarcode.getText()
										.toString());
							}
							queryValues.put("weight", edweight.getText()
									.toString());
							queryValues.put("snum", "" + sno);
							Log.d("3", "" + sno);
						}
						sqldb.insertproduct(queryValues);
						getListView();
						double tot = sqldb.getTotal(edpalettecount.getText()
								.toString());
						edtotal.setText(String.format("%.2f", tot));
						}
					} else if (edweight.getText().toString().matches("")
							&& edbarcode.getText().toString().matches("")) {
						Toast.makeText(getApplicationContext(),
								"Please Enter Weight ", Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(getApplicationContext(),
							"Please Select Product ", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(getApplicationContext(), " Please Enter Code ",
						Toast.LENGTH_SHORT).show();
			}
		}
		
		edweight.setText("");
		edbarcode.setText("");
		edpieceperqty.setText("");
	}

	public void getListView() {
		this.cursor = this.sqldb.getPalette(this.edpalettecount.getText()
				.toString());
		/*this.cursor = this.sqldb.getPalette(this.edpalettecount.getText()
				.toString());
		String[] columns = { "paletteId", "productcode", "productname",
				"barcode", "weight", "snum" };
		int[] to = { R.id.idprodpalatte, R.id.productcode, R.id.productname,
				R.id.productbarcode, R.id.productweight, R.id.productsno };
		crsrAdptr = new SimpleCursorAdapter(this,
				R.layout.addproduct_list_item, this.cursor, columns, to, 0);
		lv.setAdapter(this.crsrAdptr);*/
		
		 crsrAdptr = new ProductListAdapter(this, cursor);
		  lv.setAdapter(crsrAdptr);
	}

	public void donePPQtyMethod() {

		int sno = 1;
		String prd;
		int pieceperqty = Integer.parseInt(edpieceperqty.getText().toString());
		if(pieceperqty<500){
		for (int i = 0; i < pieceperqty; i++) {
			HashMap<String, String> queryValues = new HashMap<String, String>();
			if (datadb != null) {
				prd = sqldb.getProd(edpalettecount.getText().toString());
				Log.d("prd", prd);
				if (prd.equals(edpalettecount.getText().toString())) {
					int snos = sqldb
							.getnum(edpalettecount.getText().toString());
					int sns = snos + 1;
					queryValues.put("paletteId", edpalettecount.getText()
							.toString());
					queryValues.put("code", edcodefield.getText().toString());
					queryValues.put("name", ednamefield.getText().toString());
					if (edbarcode.getText().toString().equals("")) {
						String zero = "0";
						queryValues.put("barcode", zero);
					} else {

						queryValues.put("barcode", edbarcode.getText()
								.toString());
					}
					queryValues.put("weight", edweight.getText().toString());
					queryValues.put("snum", "" + sns);
					Log.d("1", "" + sno);
				} else {

					queryValues.put("paletteId", edpalettecount.getText()
							.toString());
					queryValues.put("code", edcodefield.getText().toString());
					queryValues.put("name", ednamefield.getText().toString());
					if (edbarcode.getText().toString().equals("")) {
						String zero = "0";
						queryValues.put("barcode", zero);
					} else {

						queryValues.put("barcode", edbarcode.getText()
								.toString());
					}
					queryValues.put("weight", edweight.getText().toString());
					queryValues.put("snum", "" + sno);
					Log.d("2", "" + sno);
				}
			} else {

				queryValues.put("paletteId", edpalettecount.getText()
						.toString());
				queryValues.put("code", edcodefield.getText().toString());
				queryValues.put("name", ednamefield.getText().toString());
				if (edbarcode.getText().toString().equals("")) {
					String zero = "0";
					queryValues.put("barcode", zero);
				} else {

					queryValues.put("barcode", edbarcode.getText().toString());
				}
				queryValues.put("weight", edweight.getText().toString());
				queryValues.put("snum", "" + sno);
				Log.d("3", "" + sno);
			}
			sqldb.insertproduct(queryValues);
			getListView();
			double tot = sqldb.getTotal(edpalettecount.getText().toString());
			edtotal.setText(String.format("%.2f", tot));
		}
		}
		edpieceperqty.setText("");
		edbarcode.setText("");
		edweight.setText("");
	}

	public void doneMethod() {

		int sno = 1;
		String prd;
		HashMap<String, String> queryValues = new HashMap<String, String>();
		if (datadb != null) {
			prd = sqldb.getProd(edpalettecount.getText().toString());
			Log.d("prd", prd);
			if (prd.equals(edpalettecount.getText().toString())) {
				int snos = sqldb.getnum(edpalettecount.getText().toString());
				int sns = snos + 1;
				queryValues.put("paletteId", edpalettecount.getText()
						.toString());
				queryValues.put("code", edcodefield.getText().toString());
				queryValues.put("name", ednamefield.getText().toString());
				if (edbarcode.getText().toString().equals("")) {
					String zero = "0";
					queryValues.put("barcode", zero);
				} else {

					queryValues.put("barcode", edbarcode.getText().toString());
				}
				queryValues.put("weight", edweight.getText().toString());
				queryValues.put("snum", "" + sns);
				Log.d("1", "" + sno);
			} else {

				queryValues.put("paletteId", edpalettecount.getText()
						.toString());
				queryValues.put("code", edcodefield.getText().toString());
				queryValues.put("name", ednamefield.getText().toString());
				if (edbarcode.getText().toString().equals("")) {
					String zero = "0";
					queryValues.put("barcode", zero);
				} else {

					queryValues.put("barcode", edbarcode.getText().toString());
				}
				queryValues.put("weight", edweight.getText().toString());
				queryValues.put("snum", "" + sno);
				Log.d("2", "" + sno);
			}
		} else {

			queryValues.put("paletteId", edpalettecount.getText().toString());
			queryValues.put("code", edcodefield.getText().toString());
			queryValues.put("name", ednamefield.getText().toString());
			if (edbarcode.getText().toString().equals("")) {
				String zero = "0";
				queryValues.put("barcode", zero);
			} else {

				queryValues.put("barcode", edbarcode.getText().toString());
			}
			queryValues.put("weight", edweight.getText().toString());
			queryValues.put("snum", "" + sno);
			Log.d("3", "" + sno);
		}
		sqldb.insertproduct(queryValues);
		getListView();
		double tot = sqldb.getTotal(edpalettecount.getText().toString());
		edtotal.setText(String.format("%.2f", tot));
		edweight.setText("");
		edbarcode.setText("");
	}

	public void alertDialogSearchForEditProduct() {
		// TODO : insert code to perform on clicking of the
		// RIGHT drawable image...
		AlertDialog.Builder myDialog = new AlertDialog.Builder(AddProduct.this);
		editText = new EditText(AddProduct.this);
		final ListView listview = new ListView(AddProduct.this);
		LinearLayout layout = new LinearLayout(AddProduct.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Product");
		editText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.search, 0,
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

		arrayAdapterProd = new CustomAlertAdapterProd(AddProduct.this, al);
		listview.setAdapter(arrayAdapterProd);
		listview.setOnItemClickListener(AddProduct.this);

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

				arrayAdapterProd = new CustomAlertAdapterProd(AddProduct.this,
						searchResults);
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

	private class AsyncCallWSEditProduct extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			editProductArr.clear();
			spinnerLayout = new LinearLayout(AddProduct.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(addproduct_layout, false);
			progressBar = new ProgressBar(AddProduct.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
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
					PcsPerCarton = editProductArr.get(11);

				}
				LogOutSetGet.setAddProduct("StockIn");
				Intent i = new Intent(AddProduct.this,
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
				startActivity(i);
				AddProduct.this.finish();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(addproduct_layout, true);
		}
	}

	public void alertDialogSearch() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(AddProduct.this);
		editText = new EditText(AddProduct.this);
		final ListView listview = new ListView(AddProduct.this);
		LinearLayout layout = new LinearLayout(AddProduct.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Product");
		editText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.search, 0,
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

		arrayAdapterProd = new CustomAlertAdapterProd(AddProduct.this, al);
		listview.setAdapter(arrayAdapterProd);
		listview.setOnItemClickListener(AddProduct.this);

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

				arrayAdapterProd = new CustomAlertAdapterProd(AddProduct.this,
						searchResults);
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

	public void editBarCodeField() {
		if (edbarcode.getText().toString() != ""
				&& edbarcode.getText().length() != 0) {
			String getbarcode = edbarcode.getText().toString();
			Set<Entry<String, String>> keys = hmsplitbc.entrySet();
			Iterator<Entry<String, String>> iterator = keys.iterator();
			while (iterator.hasNext()) {
				@SuppressWarnings("rawtypes")
				Entry mapEntry = iterator.next();
				String keyValue = (String) mapEntry.getKey();
				String value = (String) mapEntry.getValue();

				String stbarc = edcodefield.getText().toString();

				if (stbarc.toLowerCase().equals(keyValue.toLowerCase())) {
					Log.d("hmKey", keyValue);
					Log.d("hmvalue", value);
					String[] parts = value.split(",");
					String strpart1 = parts[0];
					String strpart2 = parts[1];
					Log.d("strpart1", strpart1);
					Log.d("strpart2", strpart2);

					if (!strpart1.equals(strpart2) && !strpart1.equals(null)
							&& !strpart1.equals(null) && !strpart1.equals("0")) {
						int startwe = Integer.valueOf(parts[0]);
						int endwe = Integer.valueOf(parts[1]);
						if (startwe < endwe) {
							Log.d("startwe", "" + startwe);
							Log.d("endwe", "" + endwe);
							String[] pairs = value.split(",");
							int part1 = Integer.valueOf(pairs[0]);
							int part2 = Integer.valueOf(pairs[1]);
							if (part2 <= edbarcode.getText().length()) {
								int part3 = part1 - 1;
								Log.d("part2", "" + part2);
								String substr = getbarcode.substring(part3,
										part2);
								int length = substr.length();
								if (length >= 3) {
									String str = new StringBuilder(substr)
											.insert(substr.length() - 2, ".")
											.toString();
									edweight.setText(str);
									Log.d("length3", "" + length);
									Log.d("str3", str);
									if (edpieceperqty.getText().length() != 0
											&& edpieceperqty.getText()
													.toString() != "") {
										donePPQtyMethod();
									} else {
										doneMethod();
									}
									break;
								} else if (length >= 2) {
									String str = new StringBuilder(substr)
											.insert(substr.length() - 2, ".")
											.toString();
									String seg = "0" + str;
									edweight.setText(seg);
									Log.d("str2", str);
									Log.d("length2", "" + length);
									if (edpieceperqty.getText().length() != 0
											&& edpieceperqty.getText()
													.toString() != "") {
										donePPQtyMethod();
									} else {
										doneMethod();
									}
									break;
								} else if (length >= 1) {
									String str = substr + ".00";
									Log.d("str1", str);
									edweight.setText(str);
									if (edpieceperqty.getText().length() != 0
											&& edpieceperqty.getText()
													.toString() != "") {
										donePPQtyMethod();
									} else {
										doneMethod();
									}
									break;
								}
							} else {
								Log.d("tpart2", "" + part2);
								Toast.makeText(
										getApplicationContext(),
										"Use At least " + part2
												+ " digit barcode  ",
										Toast.LENGTH_SHORT).show();
								break;
							}
						} else {
							edweight.setText("0.0");
							setStartsEndsOn();
							break;
						}
					}

					else {
						edweight.setText("0.0");
						setStartsEndsOn();
						break;
					}

				}
			}
		}

		edbarcode.addTextChangedListener(new TextWatcher() {
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
				textlength = edbarcode.getText().length();
				edweight.setText("");
			}
		});
	}

	public void editCodeField() {

		if (edcodefield.getText().toString() != ""
				&& edcodefield.getText().length() != 0) {
//			String vv = albarcode.toString();
//			String albarstr = albar.toString().toLowerCase().trim();
//			String alprosrt = alprodcode.toString().toLowerCase().trim();

			ArrayList<String> mergeResult = new ArrayList<String>();
			mergeResult.addAll(alprodcode);
			mergeResult.addAll(albar);
			boolean res = false;
			for (String alphabet : mergeResult) {
				if (alphabet.toLowerCase().equals(
						edcodefield.getText().toString().toLowerCase())
						|| alphabet.toLowerCase().equals(
								edcodefield.getText().toString().toLowerCase())) {
					res = true;
					break;
				}
			}

			if (res == true) {
				edbarcode.requestFocus();

				Iterator<Entry<String, String>> a = hashmap.entrySet()
						.iterator();
				while (a.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry amapEntry = a.next();
					String keyValue = (String) amapEntry.getKey();
					String value = (String) amapEntry.getValue();

					if (edcodefield.getText().toString().toLowerCase()
							.contains(keyValue.toLowerCase())) {
						ednamefield.setText(value);
					}
					for (int i = 0; i < albarcode.size(); i++) {
						HashMap<String, String> barhm = new HashMap<String, String>();
						barhm = albarcode.get(i);
						Iterator<Entry<String, String>> b = barhm.entrySet()
								.iterator();
						while (b.hasNext()) {
							@SuppressWarnings("rawtypes")
							Entry bmapEntry = b.next();
							String keybar = (String) bmapEntry.getKey();
							String valuebar = (String) bmapEntry.getValue();
							if (edcodefield.getText().toString()
									.equals(valuebar)) {
								edcodefield.setText(keybar);
								ednamefield.setText(value);

								if (!productweight.isEmpty()) {
									for (int j = 0; j < productweight.size(); j++) {
										HashMap<String, String> wghtvalue = productweight
												.get(j);
										Set<Entry<String, String>> wghtkeys = wghtvalue
												.entrySet();
										Iterator<Entry<String, String>> iterators = wghtkeys
												.iterator();
										while (iterators.hasNext()) {
											@SuppressWarnings("rawtypes")
											Entry mapEntrys = iterators
													.next();
											String wghtkey = (String) mapEntrys
													.getKey();
											String wghtvalues = (String) mapEntrys
													.getValue();
											if (keybar.matches(wghtkey)) {
												if (wghtvalues.matches("0.0")
														|| wghtvalues
																.matches("0.00")) {
													edweight.setText("");
												} else {
													edweight.setText(wghtvalues);
												}
												break;
											}
										}
									}
								}

							}
						}
					}
				}
			}

			else {
				ednamefield.requestFocus();
				Toast.makeText(getApplicationContext(), " Invalid Code ",
						Toast.LENGTH_SHORT).show();
				edcodefield.setText("");
				ednamefield.setText("");
				edbarcode.setText("");
				edweight.setText("");

			}

			edcodefield.addTextChangedListener(new TextWatcher() {
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
					textlength = edcodefield.getText().length();
					ednamefield.setText("");
					edweight.setText("");
				}
			});
		} else {
			ednamefield.requestFocus();
			Toast.makeText(getApplicationContext(), " Enter Code ",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void barcodeValue() {
		if (edbarcode.getText().toString() != ""
				&& edbarcode.getText().length() != 0) {
			String getbarcode = edbarcode.getText().toString();
			Set<Entry<String, String>> keys = hmsplitbc.entrySet();
			Iterator<Entry<String, String>> iterator = keys.iterator();
			while (iterator.hasNext()) {
				@SuppressWarnings("rawtypes")
				Entry mapEntry = iterator.next();
				String keyValue = (String) mapEntry.getKey();
				String value = (String) mapEntry.getValue();

				String stbarc = edcodefield.getText().toString();

				if (stbarc.toLowerCase().equals(keyValue.toLowerCase())) {

					String[] parts = value.split(",");
					String strpart1 = parts[0];
					String strpart2 = parts[1];
					Log.d("strpart1", strpart1);
					Log.d("strpart2", strpart2);

					if (!strpart1.equals(strpart2) && !strpart1.equals(null)
							&& !strpart1.equals(null) && !strpart1.equals("0")) {
						int startwe = Integer.valueOf(parts[0]);
						int endwe = Integer.valueOf(parts[1]);
						if (startwe < endwe) {
							Log.d("startwe", "" + startwe);
							Log.d("endwe", "" + endwe);
							String[] pairs = value.split(",");
							int part1 = Integer.valueOf(pairs[0]);
							int part2 = Integer.valueOf(pairs[1]);
							if (part2 <= edbarcode.getText().length()) {
								int part3 = part1 - 1;
								Log.d("part2", "" + part2);
								String substr = getbarcode.substring(part3,
										part2);
								int length = substr.length();
								if (length >= 3) {
									String str = new StringBuilder(substr)
											.insert(substr.length() - 2, ".")
											.toString();
									edweight.setText(str);
									Log.d("length3", "" + length);
									Log.d("str3", str);
									break;
								} else if (length >= 2) {
									String str = new StringBuilder(substr)
											.insert(substr.length() - 2, ".")
											.toString();
									String seg = "0" + str;
									edweight.setText(seg);
									Log.d("str2", str);
									Log.d("length2", "" + length);
									break;
								} else if (length >= 1) {
									String str = substr + ".00";
									Log.d("str1", str);
									edweight.setText(str);
									break;
								}
							} else {
								Log.d("tpart2", "" + part2);
								Toast.makeText(
										getApplicationContext(),
										"Use At least " + part2
												+ " digit barcode  ",
										Toast.LENGTH_SHORT).show();
								edweight.setText("");
								break;
							}
						} else {
							setStartsEndsOn();
							break;
						}
					}

					else {
						setStartsEndsOn();
						break;
					}

				}
			}
		}

	}

	public void setStartsEndsOn() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		dialog.setContentView(R.layout.weightstartsendson);

		stwght = 1;
		edwght = 1;

		stnumber = (TextView) dialog.findViewById(R.id.stnumber);

		stupButton = (Button) dialog.findViewById(R.id.stupBtn);
		stupButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stdownButton
						.setBackgroundResource(R.mipmap.timepicker_down_normal);
				stupButton
						.setBackgroundResource(R.mipmap.timepicker_up_pressed);
				if (stwght >= stdownrange && stwght <= stuprange)
					stwght++;
				if (stwght > stuprange)
					stwght = stdownrange;
				stnumber.setText("" + stwght);

			}
		});

		stdownButton = (Button) dialog.findViewById(R.id.stdownBtn);
		stdownButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stdownButton
						.setBackgroundResource(R.mipmap.timepicker_down_pressed);
				stupButton
						.setBackgroundResource(R.mipmap.timepicker_up_normal);
				if (stwght >= stdownrange && stwght <= stuprange)
					stwght--;

				if (stwght < stdownrange)
					stwght = stuprange;

				stnumber.setText(stwght + "");
			}
		});

		ednumber = (TextView) dialog.findViewById(R.id.ednumber);

		edupButton = (Button) dialog.findViewById(R.id.edupBtn);
		edupButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				eddownButton
						.setBackgroundResource(R.mipmap.timepicker_down_normal);
				edupButton
						.setBackgroundResource(R.mipmap.timepicker_up_pressed);
				if (edwght >= eddownrange && edwght <= eduprange)
					edwght++;
				if (edwght > eduprange)
					edwght = eddownrange;
				ednumber.setText("" + edwght);

			}
		});

		eddownButton = (Button) dialog.findViewById(R.id.eddownBtn);
		eddownButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				eddownButton
						.setBackgroundResource(R.mipmap.timepicker_down_pressed);
				edupButton
						.setBackgroundResource(R.mipmap.timepicker_up_normal);
				if (edwght >= eddownrange && edwght <= eduprange)
					edwght--;

				if (edwght < eddownrange)
					edwght = eduprange;

				ednumber.setText(edwght + "");
			}
		});

		ok = (Button) dialog.findViewById(R.id.okbutton);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (stwght < edwght) {
					AsyncCallWSSetWeight task = new AsyncCallWSSetWeight();
					task.execute();
					dialog.dismiss();
				} else {
					Toast.makeText(getApplicationContext(),
							"End Weight must be greater than Start Weight",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		dialog.show();
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

	private class AsyncCallWSSetWeight extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(AddProduct.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(addproduct_layout, false);
			progressBar = new ProgressBar(AddProduct.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				if (!edcodefield.getText().toString().matches("")) {
					String proCode = edcodefield.getText().toString();
					NewProductWebService.setWeightService(
							"fncSaveProductWeightBarcode", proCode, stwght,
							edwght,"");
				}
			} catch (JSONException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			} catch (XmlPullParserException e) {

				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(addproduct_layout, true);

			AsyncCallWSADDPRD task = new AsyncCallWSADDPRD();
			task.execute();
			AsyncCallBARCODE barcodetask = new AsyncCallBARCODE();
			barcodetask.execute();

		}
	}

	private class AsyncCallWSADDPRD extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			al.clear();
			spinnerLayout = new LinearLayout(AddProduct.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(addproduct_layout, false);
			progressBar = new ProgressBar(AddProduct.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));

			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String productstartend;
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
						String productweights = jsonChildNode.optString(
								PRODUCT_WEIGHT).toString();
						productstartson = jsonChildNode.optString(
								PRODUCT_STARTSON).toString();
						productendson = jsonChildNode.optString(PRODUCT_ENDSON)
								.toString();

						if (productstartson.equals("")
								&& productendson.equals("")) {
							productstartend = "0" + "," + "0";
						} else {
							productstartend = productstartson + ","
									+ productendson;
						}

						HashMap<String, String> producthm = new HashMap<String, String>();
						producthm.put(productcodes, productnames);
						al.add(producthm);
						hashmap.putAll(producthm);
						HashMap<String, String> productsplithm = new HashMap<String, String>();
						productsplithm.put(productcodes, productstartend);
						albarsplit.add(productsplithm);
						hmsplitbc.putAll(productsplithm);
						alprodcode.add(productcodes);

						HashMap<String, String> producthmweight = new HashMap<String, String>();
						producthmweight.put(productcodes, productweights);
						productweight.add(producthmweight);
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
			enableViews(addproduct_layout, true);
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

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		prodid = (TextView) v.findViewById(R.id.idprod);
		menu.add(0, v.getId(), 0, "Edit");
		menu.add(0, v.getId(), 0, "Delete");

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		if (item.getTitle() == "Edit") {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			LinearLayout lila1 = new LinearLayout(this);
			lila1.setOrientation(1);
			final EditText editcode = new EditText(this);
			final EditText editname = new EditText(this);
			final EditText editbarcode = new EditText(this);
			final EditText editweight = new EditText(this);
			lila1.addView(editcode);
			lila1.addView(editname);
			editcode.setKeyListener(null);
			editname.setKeyListener(null);
			lila1.addView(editbarcode);
			lila1.addView(editweight);
			editweight.setRawInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			alert.setView(lila1);
			alert.setIcon(R.mipmap.edit);
			alert.setTitle("Edit");
			edit_id = this.cursor.getString(this.cursor.getColumnIndex("_id"));
			ArrayList<HashMap<String, String>> getprodinfo = new ArrayList<HashMap<String, String>>();
			getprodinfo = sqldb.getProductInfo(edit_id);
			Log.d("getprodinfo", "" + getprodinfo);
			for (int i = 0; i < getprodinfo.size(); i++) {
				String arraylistelement = getprodinfo.get(i).toString();
				String les = arraylistelement.replace("{", "");
				String lee = les.replace("}", "");
				String[] pairs = lee.split(",");
				String[] splitbarcode = pairs[0].split("=");

				String finalbarcode = splitbarcode[1];
				editbarcode.setText(finalbarcode);
				Log.d("finalbarcode", finalbarcode);// }

				String[] splitweight = pairs[1].split("=");
				String finalweight = splitweight[1];
				editweight.setText(finalweight);
				Log.d("finalweight", finalweight);

				String[] splitcode = pairs[3].split("=");
				String finalcode = splitcode[1];
				editcode.setText(finalcode);
				Log.d("finalcode", finalcode);

				String[] splitname = pairs[4].split("=");
				String finalname = splitname[1];
				editname.setText(finalname);
				Log.d("finalname", finalname);

				editweight.setFocusable(true);
				editweight.requestFocus();
				editweight.setRawInputType(InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);
				mgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			}
			alert.setPositiveButton("Update",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (editbarcode.getText().toString().matches("")
									|| editweight.getText().toString()
											.matches("")) {
							} else {

								HashMap<String, String> queryValues = new HashMap<String, String>();
								queryValues.put("id", edit_id);
								queryValues.put("productcode", editcode
										.getText().toString());
								queryValues.put("productname", editname
										.getText().toString());
								queryValues.put("barcode", editbarcode
										.getText().toString());
								queryValues.put("weight", editweight.getText()
										.toString());
								sqldb.updateProductList(queryValues);

								getListView();

								double tot = sqldb.getTotal(edpalettecount
										.getText().toString());
								edtotal.setText(String.format("%.2f", tot));
								mgr.toggleSoftInput(0, 0);
							}
							// edtotal.setText(""+tot);
						}
					});
			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
							mgr.toggleSoftInput(0, 0);
						}
					});
			alert.show();
		} else if (item.getTitle() == "Delete") {

			String id = cursor.getString(cursor.getColumnIndex("_id"));
			sqldb.deleteProduct(id);
			if (id != null) {
				ArrayList<String> snoCount = new ArrayList<String>();

				snoCount = sqldb
						.snoCountID(edpalettecount.getText().toString());
				for (int i = 0; i < snoCount.size(); i++) {
					int sno = 1 + i;
					HashMap<String, String> queryValues = new HashMap<String, String>();
					queryValues.put("_id", "" + snoCount.get(i));
					queryValues.put("snum", "" + sno);
					sqldb.updateSNO(queryValues);
				}
			}
			crsrAdptr.notifyDataSetChanged();
			getListView();
			double tot = sqldb.getTotal(edpalettecount.getText().toString());
			edtotal.setText(String.format("%.2f", tot));
			Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
		} else {
			return false;
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		myalertDialog.dismiss();
		getArraylsit = arrayAdapterProd.getArrayList();
		HashMap<String, String> datavalue = getArraylsit.get(position);
		Set<Entry<String, String>> keys = datavalue.entrySet();
		Iterator<Entry<String, String>> iterator = keys.iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Entry mapEntry = iterator.next();
			String keyValues = (String) mapEntry.getKey();
			String values = (String) mapEntry.getValue();
			edcodefield.setText(keyValues);
			ednamefield.setText(values);

			if (!productweight.isEmpty()) {
				for (int i = 0; i < productweight.size(); i++) {
					HashMap<String, String> wghtvalue = productweight.get(i);
					Set<Entry<String, String>> wghtkeys = wghtvalue.entrySet();
					Iterator<Entry<String, String>> iterators = wghtkeys
							.iterator();
					while (iterators.hasNext()) {
						@SuppressWarnings("rawtypes")
						Entry mapEntrys = iterators.next();
						String wghtkey = (String) mapEntrys.getKey();
						String wghtvalues = (String) mapEntrys.getValue();
						if (keyValues.matches(wghtkey)) {
							if (wghtvalues.matches("0.0")
									|| wghtvalues.matches("0.00")) {
								edweight.setText("");
							} else {
								edweight.setText(wghtvalues);
							}
							break;
						}
					}
				}
			}

			edbarcode.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

			imm.toggleSoftInputFromWindow(
					edbarcode.getApplicationWindowToken(),
					InputMethodManager.SHOW_FORCED, 0);

			edcodefield.addTextChangedListener(new TextWatcher() {
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

					textlength = edcodefield.getText().length();
					ednamefield.setText("");
					edweight.setText("");
				}
			});
		}
	}

	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Restore offline data");
		alertDialog.setMessage("Do you want to restore old data?");
		alertDialog.setIcon(R.mipmap.ic_save);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						sqldb.removeAll();
						crsrAdptr.notifyDataSetChanged();
						getListView();
						edtotal.setText("");
						palettecount = 1;
						edpalettecount.setText("" + palettecount);
					}
				});

		alertDialog.show();
	}
	private class ProductListAdapter extends ResourceCursorAdapter {

		  @SuppressWarnings("deprecation")
		  public ProductListAdapter(Context context, Cursor cursor) {
		   super(context, R.layout.addproduct_list_item, cursor);
		  }

		  @Override
		  public void bindView(View view, Context context, Cursor cursor) {
		 
		   TextView paletteid = (TextView) view
		     .findViewById(R.id.idprodpalatte);
		   paletteid.setText(cursor.getString(cursor
		     .getColumnIndex(MySQLiteDataBase.KEY_PALETTEID)));
		   
		   TextView productcode = (TextView) view
		     .findViewById(R.id.productcode);
		   productcode.setText(cursor.getString(cursor
		     .getColumnIndex(MySQLiteDataBase.KEY_CODE)));
		   
		   TextView productname = (TextView) view
		     .findViewById(R.id.productname);
		   productname.setText(cursor.getString(cursor
		     .getColumnIndex(MySQLiteDataBase.KEY_NAME)));

		   TextView barcode = (TextView) view.findViewById(R.id.productbarcode);
		   barcode.setText(cursor.getString(cursor
		     .getColumnIndex(MySQLiteDataBase.KEY_BARCODE)));

		   TextView weight = (TextView) view.findViewById(R.id.productweight);
		   weight.setText(""+cursor.getDouble(cursor
		     .getColumnIndex(MySQLiteDataBase.KEY_WEIGHT)));

		   TextView snum = (TextView) view.findViewById(R.id.productsno);
		   snum.setText(cursor.getString(cursor
		     .getColumnIndex(MySQLiteDataBase.KEY_SNUM)));

		   

		  }
		 }
	@Override
	public void onBackPressed() {
		Intent i = new Intent(AddProduct.this, AddSupplier.class);
		startActivity(i);
		AddProduct.this.finish();
		SupplierSetterGetter.setExpheader(0);
	}
}
