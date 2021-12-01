package com.winapp.sot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
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

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;

public class GraCustomer extends SherlockFragmentActivity implements
SlideMenuFragment.MenuClickInterFace {
//	ImageView back;
	EditText gracodefield, granamefield, edtdate, edcrrncycd, edcrrncynm,
			edcrrncyrt, edremarks, edtinvoiceno, edtdono, edgradodate,
			edgrainvoicedate, nxtbatchno_edlbl;
	CustomAlertAdapterSupp arrayAdapterSupp;
	int textlength = 0;
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	LinearLayout gra_layout,customer_layout,nxtbatchno_Layout;
	Calendar myCalendar;
	String serverdate, settingvalue, crrncynm, crrncyrt, crrncycode,
			crrncyrate, currencyname;
	String valid_url;
	private AlertDialog myalertDialog = null;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	ArrayList<String> aldelivery = new ArrayList<String>();
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> getArraylsitcur = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> searchResults;
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetSupplier";
	static String custresult;
	String suppTxt = null;
	ArrayList<String> alclcrrncy = new ArrayList<String>();
	ArrayList<String> cstmrgrpcdal = new ArrayList<String>();
	ArrayList<HashMap<String, String>> currencycode = new ArrayList<HashMap<String, String>>();
	TextView listing_screen, customer_screen, addProduct_screen,summary_screen;
	HashMap<String, String> hashmap = new HashMap<String, String>();
	Button gra_addProduct;
	private SlidingMenu menu;
	ImageButton search, back, printer,addnew;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 getActionBar().setIcon(drawable.ic_menu);
		 setContentView(R.layout.gra_customer);
		
		  ActionBar ab = getSupportActionBar();
		  ab.setHomeButtonEnabled(true);
		  View customNav = LayoutInflater.from(this).inflate(
		  R.layout.slidemenu_actionbar_title, null);
		  TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		  txt.setText("Select Supplier");
		  
		  search = (ImageButton) customNav.findViewById(R.id.search_img);
		  printer = (ImageButton) customNav.findViewById(R.id.printer);
		  addnew = (ImageButton) customNav.findViewById(R.id.custcode_img);
		  
		  ab.setCustomView(customNav);
		  ab.setDisplayShowCustomEnabled(true);

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
		  
		  addnew.setVisibility(View.INVISIBLE);
		  search.setVisibility(View.GONE);
		  printer.setVisibility(View.GONE);
		
		gra_layout = (LinearLayout) findViewById(R.id.invoiceCust_layout);
		gracodefield = (EditText) findViewById(R.id.gra_codefield);
		granamefield = (EditText) findViewById(R.id.gra_namefield);
		edcrrncycd = (EditText) findViewById(R.id.gra_edCurcode);
		edtdate = (EditText) findViewById(R.id.editTextDate);
		edtinvoiceno = (EditText) findViewById(R.id.invoiceno_edlbl);
		edtdono = (EditText) findViewById(R.id.dono_edlbl);
		edgrainvoicedate = (EditText) findViewById(R.id.grainvoice_editTextDate);
		edgradodate = (EditText) findViewById(R.id.grado_editTextDate);

		edcrrncynm = (EditText) findViewById(R.id.gra_edCurlbl);
		edcrrncyrt = (EditText) findViewById(R.id.gra_edCurRate);
		edremarks = (EditText) findViewById(R.id.gra_editTextRemarks);
		//back = (ImageView) findViewById(R.id.back);
		SOTDatabase.init(GraCustomer.this);

		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen= (TextView) findViewById(R.id.sum_screen);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);
		
		nxtbatchno_Layout = (LinearLayout) findViewById(R.id.nxtbatchno_Layout);

		nxtbatchno_edlbl = (EditText) findViewById(R.id.nxtbatchno_edlbl);
		
		customer_layout.setVisibility(View.GONE);
		customer_screen.setText("Supplier");
//		customer_screen.setBackgroundColor(Color.parseColor("#00AFF0"));
		customer_screen.setTextColor(Color.parseColor("#FFFFFF"));
		customer_screen.setBackgroundResource(drawable.tab_select);
		
		gra_addProduct = (Button) findViewById(R.id.gra_addProduct);
		myCalendar = Calendar.getInstance();
		al.clear();
		currencycode.clear();
		cstmrgrpcdal.clear();
		FWMSSettingsDatabase.init(GraCustomer.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,GraCustomer.this);
		new SalesOrderWebService(valid_url);
		
		String autoBatch=SalesOrderSetGet.getAutoBatchNo();
		String haveBatchOnStockIn=SalesOrderSetGet.getHaveBatchOnStockIn();
		
		if (haveBatchOnStockIn.matches("True") && autoBatch.matches("1")){
			nxtbatchno_Layout.setVisibility(View.VISIBLE);
			String NextBatchNo = SalesOrderSetGet.getNextBatchNo();
			nxtbatchno_edlbl.setText(NextBatchNo);
		}else{
			nxtbatchno_Layout.setVisibility(View.GONE);
		}

//		String edit_gra = ConvertToSetterGetter.getEdit_gra_no();
		Cursor cursr = SOTDatabase.getCursor();
		int productCount = cursr.getCount();
		Log.d("productCount", ""+productCount);

		if(productCount>0){
			gracodefield.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			gracodefield.setBackgroundResource(R.drawable.labelbg);
			gracodefield.setFocusable(false);
		}else{
			gracodefield.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0);
			gracodefield.setBackgroundResource(R.drawable.edittext_bg);
			gracodefield.setFocusable(true);
		}

		AsyncCallWSSaleOrder inAcws = new AsyncCallWSSaleOrder();
		inAcws.execute();
		
		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Cursor cursor = SOTDatabase.getCursor();
				int count = cursor.getCount();
				if(count>0){
					alertDialog();
				}else{
					Intent i = new Intent(GraCustomer.this, GraHeader.class);
					startActivity(i);
					GraCustomer.this.finish();
				}
				
			}
		});

		addProduct_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				ConvertToSetterGetter.setEdit_gra_no("");
				if (gracodefield.getText().length() != 0
						&& gracodefield.getText().toString() != ""
						&& granamefield.getText().length() != 0
						&& granamefield.getText().toString() != "") {

						AsyncCallWSCustGrop task = new AsyncCallWSCustGrop();
						task.execute("Add Product");
						customerSetGet();

				} else {
					Toast.makeText(getApplicationContext(),
							"Please Enter SupplierCode ", Toast.LENGTH_SHORT)
							.show();
				}
							
			}
		});
		
		gra_addProduct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				ConvertToSetterGetter.setEdit_gra_no("");
				if (gracodefield.getText().length() != 0
						&& gracodefield.getText().toString() != ""
						&& granamefield.getText().length() != 0
						&& granamefield.getText().toString() != "") {

						AsyncCallWSCustGrop task = new AsyncCallWSCustGrop();
						task.execute("Add Product");
						customerSetGet();

				} else {
					Toast.makeText(getApplicationContext(),
							"Please Enter SupplierCode ", Toast.LENGTH_SHORT)
							.show();
				}
							
			}
		});

		summary_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				ConvertToSetterGetter.setEdit_gra_no("");
				if (gracodefield.getText().length() != 0
						&& gracodefield.getText().toString() != ""
						&& granamefield.getText().length() != 0
						&& granamefield.getText().toString() != "") {

						AsyncCallWSCustGrop task = new AsyncCallWSCustGrop();
						task.execute("Summary");
						customerSetGet();

				} else {
					Toast.makeText(getApplicationContext(),
							"Please Enter SupplierCode ", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		gracodefield
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						gracodefield) {
					@Override
					public boolean onDrawableClick() {
						custalertDialogSearch();
						return true;

					}

				});
		edcrrncycd
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						gracodefield) {
					@Override
					public boolean onDrawableClick() {
						currencyalertdialog();
						return true;

					}

				});

		gracodefield.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					boolean res = false;
					for (String alphabet : aldelivery) {
						if (alphabet.toLowerCase()
								.equals(gracodefield.getText().toString()
										.toLowerCase())) {
							res = true;
							break;
						}
					}
					if (res == true) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.showSoftInput(edtinvoiceno,
								InputMethodManager.SHOW_IMPLICIT);

						Set<Entry<String, String>> keys = hashmap.entrySet();
						Iterator<Entry<String, String>> iterator = keys
								.iterator();
						while (iterator.hasNext()) {
							@SuppressWarnings("rawtypes")
							Entry mapEntry = iterator.next();
							String keyValue = (String) mapEntry.getKey();
							String value = (String) mapEntry.getValue();
							if (gracodefield.getText().toString().toLowerCase()
									.equals(keyValue.toLowerCase())) {
								granamefield.setText(value);

							}
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"Invalid Supplier Code", Toast.LENGTH_SHORT)
								.show();
						gracodefield.setText("");
						granamefield.setText("");

					}
					gracodefield.addTextChangedListener(new TextWatcher() {
						@Override
						public void afterTextChanged(Editable s) {

						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {

						}

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {

							textlength = gracodefield.getText().length();
							granamefield.setText("");
						}
					});

				}

				return false;
			}
		});
		final DatePickerDialog.OnDateSetListener indate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				inDate();
			}
		};

		edtdate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(GraCustomer.this, indate, myCalendar
							.get(Calendar.YEAR),
							myCalendar.get(Calendar.MONTH), myCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		final DatePickerDialog.OnDateSetListener invoicedate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				invoiceDate();
			}
		};
		edgrainvoicedate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(GraCustomer.this, invoicedate,
							myCalendar.get(Calendar.YEAR), myCalendar
									.get(Calendar.MONTH), myCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		final DatePickerDialog.OnDateSetListener delivdate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				delivDate();
			}
		};
		edgradodate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(GraCustomer.this, delivdate,
							myCalendar.get(Calendar.YEAR), myCalendar
									.get(Calendar.MONTH), myCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
	}

	private void delivDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		edgradodate.setText(sdf.format(myCalendar.getTime()));
	}

	private void invoiceDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		edgrainvoicedate.setText(sdf.format(myCalendar.getTime()));
	}

	private void inDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		edtdate.setText(sdf.format(myCalendar.getTime()));

	}

	public void custalertDialogSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(GraCustomer.this);
		final EditText editText = new EditText(GraCustomer.this);
		final ListView listview = new ListView(GraCustomer.this);
		LinearLayout layout = new LinearLayout(GraCustomer.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Supplier");
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
		arrayAdapterSupp = new CustomAlertAdapterSupp(GraCustomer.this, al);
		listview.setAdapter(arrayAdapterSupp);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				myalertDialog.dismiss();
				getArraylsit = arrayAdapterSupp.getArrayList();
				HashMap<String, String> datavalue = getArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String keyValues = (String) mapEntry.getKey();
					String values = (String) mapEntry.getValue();
					gracodefield.setText(keyValues);
					granamefield.setText(values);
					gracodefield.addTextChangedListener(new TextWatcher() {
						@Override
						public void afterTextChanged(Editable s) {

						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {

						}

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {

							textlength = gracodefield.getText().length();
							granamefield.setText("");

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

				arrayAdapterSupp = new CustomAlertAdapterSupp(GraCustomer.this,
						searchResults);
				listview.setAdapter(arrayAdapterSupp);
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

	public void currencyalertdialog() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(GraCustomer.this);
		final EditText editText = new EditText(GraCustomer.this);
		final ListView listview = new ListView(GraCustomer.this);
		LinearLayout layout = new LinearLayout(GraCustomer.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Currency");
		editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,
				0, 0);
		layout.addView(editText);
		layout.addView(listview);
		myDialog.setView(layout);
		arrayAdapterSupp = new CustomAlertAdapterSupp(GraCustomer.this,
				currencycode);
		listview.setAdapter(arrayAdapterSupp);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				myalertDialog.dismiss();
				getArraylsitcur = arrayAdapterSupp.getArrayList();
				HashMap<String, String> datavalue = getArraylsitcur
						.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String keyValues = (String) mapEntry.getKey();
					String values = (String) mapEntry.getValue();
					edcrrncycd.setText(keyValues);

					crrncycode = keyValues;
					currencyname = values;
					AsyncCallWSCurrcode taskcc = new AsyncCallWSCurrcode();
					taskcc.execute();
					edcrrncycd.addTextChangedListener(new TextWatcher() {
						@Override
						public void afterTextChanged(Editable s) {

						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {

						}

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {

							textlength = edcrrncycd.getText().length();
							edcrrncynm.setText("");
							edcrrncyrt.setText("");

						}
					});
				}
			}
		});
		searchResults = new ArrayList<HashMap<String, String>>(currencycode);
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
				for (int i = 0; i < currencycode.size(); i++) {
					String custName = currencycode.get(i).toString();
					if (textlength <= custName.length()) {
						if (custName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(currencycode.get(i));
					}
				}

				arrayAdapterSupp = new CustomAlertAdapterSupp(GraCustomer.this,
						searchResults);
				listview.setAdapter(arrayAdapterSupp);
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

	private class AsyncCallWSSaleOrder extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			al.clear();
			currencycode.clear();
			spinnerLayout = new LinearLayout(GraCustomer.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(gra_layout, false);
			progressBar = new ProgressBar(GraCustomer.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			serverdate = DateWebservice.getDateService("fncGetServerDate");
			currencycode = SalesOrderWebService
					.getAllCurrency("fncGetCurrency");

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
				suppTxt = response.toString();
				custresult = " { GraSupplier : " + suppTxt + "}";
				JSONObject jsonResponse;
				try {
					jsonResponse = new JSONObject(custresult);
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("GraSupplier");
					int lengthJsonArr = jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {
						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);

						String customercode = jsonChildNode.optString(
								"SupplierCode").toString();
						String customername = jsonChildNode.optString(
								"SupplierName").toString();

						HashMap<String, String> customerhm = new HashMap<String, String>();
						customerhm.put(customercode, customername);
						al.add(customerhm);
						hashmap.putAll(customerhm);
						aldelivery.add(customercode);

					}
					settingvalue = SalesOrderWebService
							.generalSettingsService("fncGetGeneralSettings");
					alclcrrncy = SalesOrderWebService.getCurrencyValues(
							"fncGetCurrency", settingvalue);
					crrncynm = alclcrrncy.get(0);
					crrncyrt = alclcrrncy.get(1);

				} catch (JSONException e) {

					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
				suppTxt = "Error occured";
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			gettingSetGet();
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(gra_layout, true);
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

	private class AsyncCallWSCustGrop extends AsyncTask<String, Void, Void> {
		@SuppressWarnings("deprecation")
		String navigateTo="";
		@Override
		protected void onPreExecute() {
			cstmrgrpcdal.clear();
			spinnerLayout = new LinearLayout(GraCustomer.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(gra_layout, false);
			progressBar = new ProgressBar(GraCustomer.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(String... arg) {
			navigateTo = arg[0];
			try {
				cstmrgrpcdal = SalesOrderWebService.getSuppTermCode(
						gracodefield.getText().toString(), "fncGetSupplier");
				SalesOrderWebService.getSupplierTax(gracodefield.getText()
						.toString(), "fncGetSupplier");
				Log.d("Suppliervalues", "" + cstmrgrpcdal);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(gra_layout, true);

			try{
			SalesOrderSetGet.setSuppliercode(cstmrgrpcdal.get(0));
			SalesOrderSetGet.setSuppliergroupcode(cstmrgrpcdal.get(1));
			}catch(Exception e){
				
			}
			
			if(navigateTo.matches("Add Product")){
				Intent i = new Intent(GraCustomer.this, GraAddProduct.class);
				startActivity(i);
				GraCustomer.this.finish();
			}else if(navigateTo.matches("Summary")){
				Intent i = new Intent(GraCustomer.this, GraSummary.class);
				startActivity(i);
				GraCustomer.this.finish();
			}else{
				Intent i = new Intent(GraCustomer.this, GraAddProduct.class);
				startActivity(i);
				GraCustomer.this.finish();
			}
			
		

		}
	}

	private class AsyncCallWSCurrcode extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(GraCustomer.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(gra_layout, false);
			progressBar = new ProgressBar(GraCustomer.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				crrncyrate = SalesOrderWebService.getCrrncyRate(crrncycode,
						"fncGetCurrency");
				Log.d("crrncycode", crrncycode);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			edcrrncynm.setText(currencyname);
			edcrrncyrt.setText(crrncyrate);
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(gra_layout, true);
		}
	}

	public void customerSetGet() {
		SalesOrderSetGet.setCurrencycode(edcrrncycd.getText().toString());
		SalesOrderSetGet.setCurrencyname(edcrrncynm.getText().toString());
		SalesOrderSetGet.setSaleorderdate(edtdate.getText().toString());
		SalesOrderSetGet.setCustomercode(gracodefield.getText().toString());
		SalesOrderSetGet.setCustomername(granamefield.getText().toString());
		SalesOrderSetGet.setCurrencyrate(edcrrncyrt.getText().toString());
		SalesOrderSetGet.setRemarks(edremarks.getText().toString());
//		SalesOrderSetGet.setGrainvoiceno(edtinvoiceno.getText().toString());
//		SalesOrderSetGet.setGradono(edtdono.getText().toString());
		SalesOrderSetGet.setGrainvoicedate(edgrainvoicedate.getText()
				.toString());
		SalesOrderSetGet.setGradodate(edgradodate.getText().toString());
		SalesOrderSetGet.setGrainvoiceno(edtinvoiceno.getText().toString());
		SalesOrderSetGet.setGradono(edtdono.getText().toString());
	}

	public void gettingSetGet() {
		gracodefield.setText(SalesOrderSetGet.getCustomercode());
		granamefield.setText(SalesOrderSetGet.getCustomername());
		edtinvoiceno.setText(SalesOrderSetGet.getGrainvoiceno());
		edtdono.setText(SalesOrderSetGet.getGradono());
		edremarks.setText(SalesOrderSetGet.getRemarks());

		if (SalesOrderSetGet.getSaleorderdate().equals("")) {
			edtdate.setText(serverdate);
		} else {
			edtdate.setText(SalesOrderSetGet.getSaleorderdate());
		}
		if (SalesOrderSetGet.getGrainvoicedate().equals("")) { 
			edgrainvoicedate.setText(serverdate);
		} else {
			edgrainvoicedate.setText(SalesOrderSetGet.getGrainvoicedate());
		}
		if (SalesOrderSetGet.getGradodate().equals("")) {
			edgradodate.setText(serverdate);
		} else {
			edgradodate.setText(SalesOrderSetGet.getGradodate());
		}
		if (SalesOrderSetGet.getCurrencycode().equals("")) {
			edcrrncycd.setText(settingvalue);
		} else {
			edcrrncycd.setText(SalesOrderSetGet.getCurrencycode());
		}
		if (SalesOrderSetGet.getCurrencyname().equals("")) {
			edcrrncynm.setText(crrncynm);
		} else {
			edcrrncynm.setText(SalesOrderSetGet.getCurrencyname());
		}
		if (SalesOrderSetGet.getCurrencyrate().equals("")) {
			edcrrncyrt.setText(crrncyrt);
		} else {
			edcrrncyrt.setText(SalesOrderSetGet.getCurrencyrate());
		}
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(GraCustomer.this, GraHeader.class);
		startActivity(i);
		GraCustomer.this.finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		menu.toggle();
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onListItemClick(String item) {
		// TODO Auto-generated method stub
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
							Intent i = new Intent(GraCustomer.this, GraHeader.class);
							startActivity(i);
							GraCustomer.this.finish();
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
}
