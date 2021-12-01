package com.winapp.sot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

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
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.NewProductWebService;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;

public class SalesOrderCustomer extends SherlockFragmentActivity implements
SlideMenuFragment.MenuClickInterFace {
//	ImageView back;
	EditText soedcodefield, soednamefield, edtdate, eddlvrydt, edcrrncycd,
			edcrrncynm, edcrrncyrt, edremarks;
	CustomAlertAdapterSupp arrayAdapterSupp;
	int textlength = 0;
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	LinearLayout salesOrder_layout,customer_layout;
	Calendar myCalendar;
	String serverdate, settingvalue, crrncynm, crrncyrt, crrncycode,
			crrncyrate, currencyname, companyCode="",select_van="";
	String valid_url;
	private AlertDialog myalertDialog = null;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	ArrayList<String> alsaleorder = new ArrayList<String>();
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> getArraylsitcur = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> searchResults;
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetCustomer";
	static String custresult;
	String suppTxt = null;
	ArrayList<String> alclcrrncy = new ArrayList<String>();
	ArrayList<String> cstmrgrpcdal = new ArrayList<String>();

	ArrayList<HashMap<String, String>> currencycode = new ArrayList<HashMap<String, String>>();
	TextView listing_screen, customer_screen, addProduct_screen,summary_screen;
	HashMap<String, String> hashmap = new HashMap<String, String>();
	Button so_addProduct;
	private SlidingMenu menu;
	ImageButton search, back, printer,addnew;
	String customer_outstanding="",tranblock_abovelimit="", credit_limit="";
	LinearLayout invoi_creditlimitlayout,
			invoi_outstandingLayout, invoi_balancelimitLayout;
	TextView  invoi_outstandingAmt, invoi_creditlimit, invoi_balancelimit;
	// Offline
			LinearLayout offlineLayout;
			private OfflineDatabase offlineDatabase;
			boolean checkOffline;
			String onlineMode, offlineDialogStatus;
			private OfflineCommon offlineCommon;
			OfflineSettingsManager spManager;
	static List<HashMap<String,String>> invoiceExceedTernsArr = new ArrayList<HashMap<String,String>>();
	static List<HashMap<String,String>> invoiceCreditLimitArr = new ArrayList<HashMap<String,String>>();
	String tran_block_credit_limit="",tran_block_terms="",mobileHaveOfflineMode="";
	private boolean editTransaction=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.salesorder_customer);
		
		ActionBar ab = getSupportActionBar();
		  ab.setHomeButtonEnabled(true);
		  View customNav = LayoutInflater.from(this).inflate(
		  R.layout.slidemenu_actionbar_title, null);
		  TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		  txt.setText("Sales Order Customer");
		  
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
		  
		// @Offline
			onlineMode = OfflineDatabase.getOnlineMode();
			offlineDatabase = new OfflineDatabase(SalesOrderCustomer.this);
			offlineCommon = new OfflineCommon(SalesOrderCustomer.this);
			checkOffline = OfflineCommon.isConnected(SalesOrderCustomer.this);
			new OfflineSalesOrderWebService(SalesOrderCustomer.this);
			OfflineDatabase.init(SalesOrderCustomer.this);
			offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);
			spManager = new OfflineSettingsManager(SalesOrderCustomer.this);
			companyCode = spManager.getCompanyType();
		
		salesOrder_layout = (LinearLayout) findViewById(R.id.salesOrderCust_layout);
		soedcodefield = (EditText) findViewById(R.id.so_codefield);
		soednamefield = (EditText) findViewById(R.id.so_namefield);
		edcrrncycd = (EditText) findViewById(R.id.so_edCurcode);
		edtdate = (EditText) findViewById(R.id.editTextDate);
		eddlvrydt = (EditText) findViewById(R.id.editTextDelivDate);
		edcrrncynm = (EditText) findViewById(R.id.so_edCurlbl);
		edcrrncyrt = (EditText) findViewById(R.id.so_edCurRate);
		edremarks = (EditText) findViewById(R.id.so_editTextRemarks);
//		back = (ImageView) findViewById(R.id.back);
		so_addProduct = (Button) findViewById(R.id.so_addProduct);
		
		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen= (TextView) findViewById(R.id.sum_screen);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);
		
		customer_layout.setVisibility(View.GONE);
//		customer_screen.setBackgroundColor(Color.parseColor("#00AFF0"));
		customer_screen.setTextColor(Color.parseColor("#FFFFFF"));
		customer_screen.setBackgroundResource(drawable.tab_select);

		invoi_creditlimitlayout = (LinearLayout) findViewById(R.id.invoi_creditlimitlayout);
		invoi_outstandingLayout = (LinearLayout) findViewById(R.id.invoi_outstandingLayout);
		invoi_balancelimitLayout = (LinearLayout) findViewById(R.id.invoi_balancelimitLayout);
		invoi_creditlimit = (TextView) findViewById(R.id.invoi_creditlimit);
		invoi_outstandingAmt = (TextView) findViewById(R.id.invoi_outstandingAmt);
		invoi_balancelimit = (TextView) findViewById(R.id.invoi_balancelimit);
		//offline
				if (onlineMode.matches("True")) {
					offlineLayout.setVisibility(View.GONE);
				} else if (onlineMode.matches("False")) {
					offlineLayout.setVisibility(View.VISIBLE);
				}
		
		myCalendar = Calendar.getInstance();
		al.clear();
		currencycode.clear();
		cstmrgrpcdal.clear();
		FWMSSettingsDatabase.init(SalesOrderCustomer.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,SalesOrderCustomer.this);
		new SalesOrderWebService(valid_url);
		SOTDatabase.init(SalesOrderCustomer.this );
		select_van = SOTDatabase.getVandriver();
		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		editTransaction = FormSetterGetter.isEditTransactionDate();

		if(editTransaction == false){
			edtdate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			edtdate.setFocusable(false);
			edtdate.setBackgroundResource(drawable.labelbg);
		}else{
			edtdate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_calendar, 0);
			edtdate.setFocusableInTouchMode(true);
			edtdate.setBackgroundResource(drawable.edittext_bg);
		}


		tran_block_credit_limit = SalesOrderSetGet.getTranBlockCreditLimit();
		tran_block_terms = SalesOrderSetGet.getTranBlockTerms();

		if(tran_block_credit_limit!=null && !tran_block_credit_limit.isEmpty()){

		}else{
			tran_block_credit_limit="";
		}

		if(tran_block_terms!=null && !tran_block_terms.isEmpty()){

		}else{
			tran_block_terms="";
		}

		if(select_van!=null && !select_van.isEmpty()){			
		}else{
			select_van="";
		}

//		String edit_sales = ConvertToSetterGetter.getSoNo();
		Cursor cursr = SOTDatabase.getCursor();
		int productCount = cursr.getCount();
		Log.d("productCount", ""+productCount);

		if(productCount>0){
			soedcodefield.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			soedcodefield.setBackgroundResource(R.drawable.labelbg);
			soedcodefield.setFocusable(false);
		}else{
			soedcodefield.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0);
			soedcodefield.setBackgroundResource(R.drawable.edittext_bg);
			soedcodefield.setFocusable(true);
		}

		AsyncCallWSSaleOrder soAcws = new AsyncCallWSSaleOrder();
		soAcws.execute();

		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Cursor cursor = SOTDatabase.getCursor();
				int count = cursor.getCount();
				if(count>0){
					alertDialog();
				}else{
					Intent i = new Intent(SalesOrderCustomer.this, SalesOrderHeader.class);
					startActivity(i);
					SalesOrderCustomer.this.finish();
				}
			}
		});

		addProduct_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (soedcodefield.getText().length() != 0
						&& soedcodefield.getText().toString() != ""
						&& soednamefield.getText().length() != 0
						&& soednamefield.getText().toString() != "") {
					AsyncCallWSCustGrop task = new AsyncCallWSCustGrop();
					task.execute("Add Product");
					customerSetGet();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please Enter CustomerCode ", Toast.LENGTH_SHORT)
							.show();
				}
			

			}
		});

		summary_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (soedcodefield.getText().length() != 0
						&& soedcodefield.getText().toString() != ""
						&& soednamefield.getText().length() != 0
						&& soednamefield.getText().toString() != "") {
					AsyncCallWSCustGrop task = new AsyncCallWSCustGrop();
					task.execute("Summary");
					customerSetGet();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please Enter CustomerCode ", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		

		so_addProduct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (soedcodefield.getText().length() != 0
						&& soedcodefield.getText().toString() != ""
						&& soednamefield.getText().length() != 0
						&& soednamefield.getText().toString() != "") {
					AsyncCallWSCustGrop task = new AsyncCallWSCustGrop();
					task.execute("Add Product");
					customerSetGet();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please Enter CustomerCode ", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		soedcodefield
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						soedcodefield) {
					@Override
					public boolean onDrawableClick() {
						custalertDialogSearch();
						return true;

					}

				});
		edcrrncycd
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						soedcodefield) {
					@Override
					public boolean onDrawableClick() {
						currencyalertdialog();
						return true;

					}

				});

		soedcodefield.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					boolean res = false;
					for (String alphabet : alsaleorder) {
						if (alphabet.toLowerCase().equals(
								soedcodefield.getText().toString()
										.toLowerCase())) {
							res = true;
							break;
						}
					}
					if (res == true) {
						Set<Entry<String, String>> keys = hashmap.entrySet();
						Iterator<Entry<String, String>> iterator = keys
								.iterator();
						while (iterator.hasNext()) {
							@SuppressWarnings("rawtypes")
							Entry mapEntry = iterator.next();
							String keyValue = (String) mapEntry.getKey();
							String value = (String) mapEntry.getValue();
							if (soedcodefield.getText().toString()
									.toLowerCase()
									.equals(keyValue.toLowerCase())) {
								soednamefield.setText(value);
							}
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"Invalid Customer Code", Toast.LENGTH_SHORT)
								.show();
						soedcodefield.setText("");
						soednamefield.setText("");
					}
					soedcodefield.addTextChangedListener(new TextWatcher() {
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

							textlength = soedcodefield.getText().length();
							soednamefield.setText("");
						}
					});

				}

				return false;
			}
		});
		final DatePickerDialog.OnDateSetListener sodate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				soDate();
			}
		};

		edtdate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					if(editTransaction == true) {
					new DatePickerDialog(SalesOrderCustomer.this, sodate,
							myCalendar.get(Calendar.YEAR), myCalendar
							.get(Calendar.MONTH), myCalendar
							.get(Calendar.DAY_OF_MONTH)).show();
					}
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
		eddlvrydt.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(SalesOrderCustomer.this, delivdate,
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

		eddlvrydt.setText(sdf.format(myCalendar.getTime()));
	}

	private void soDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		edtdate.setText(sdf.format(myCalendar.getTime()));

	}

	public void custalertDialogSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				SalesOrderCustomer.this);
		final EditText editText = new EditText(SalesOrderCustomer.this);
		final ListView listview = new ListView(SalesOrderCustomer.this);
		LinearLayout layout = new LinearLayout(SalesOrderCustomer.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Customer");
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
		arrayAdapterSupp = new CustomAlertAdapterSupp(SalesOrderCustomer.this,
				al);
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
					
					Log.d("onclick loc", ""+values);
					
					if(values.contains("~")){
						String[] parts = values.split("~");
						String name = parts[0];
//						String location = parts[1];

						soedcodefield.setText(keyValues);
						soednamefield.setText(name);
					}else{
						soedcodefield.setText(keyValues);
						soednamefield.setText(values);
					}

					AsyncCallWSCustomerOutstanding taskAmt = new AsyncCallWSCustomerOutstanding();
					taskAmt.execute();
//					soedcodefield.setText(keyValues);
//					soednamefield.setText(values);
					soedcodefield.addTextChangedListener(new TextWatcher() {
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

							textlength = soedcodefield.getText().length();
							soednamefield.setText("");

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

				arrayAdapterSupp = new CustomAlertAdapterSupp(
						SalesOrderCustomer.this, searchResults);
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

	private class AsyncCallWSCustomerOutstanding extends
			AsyncTask<Void, Void, Void> {
		String dialogStatus,customerCode;

		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(SalesOrderCustomer.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesOrder_layout, false);
			progressBar = new ProgressBar(SalesOrderCustomer.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
			customer_outstanding = "";
			dialogStatus = checkInternetStatus();
			customerCode = soedcodefield.getText().toString();


			invoiceExceedTernsArr.clear();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {



				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) { // Temp Offline
							// customer_outstanding =
							// OfflineSalesOrderWebService.getCrrncyRateOffline(crrncycode);
						} else {
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else { // Online
						customer_outstanding = SalesOrderWebService
								.getCustomerOutstandingAmount(customerCode,
										"fncGetCustomer",""); //getTaxCode

						if(tran_block_terms.matches("1")){
							invoiceExceedTernsArr = SalesOrderWebService.getInvoiceHeaderExceedsTerms(customerCode,"fncGetInvoiceHeaderExceedsTerms");
						}

					}

				} else if (onlineMode.matches("False")) { // Permt Offline
					// customer_outstanding =
					// OfflineSalesOrderWebService.getCrrncyRateOffline(crrncycode);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try{

				if(customer_outstanding!=null && !customer_outstanding.isEmpty()){

//					StringTokenizer tokens = new StringTokenizer(customer_outstanding.toString(), ",");
					String outstanding="";

					String[] tokens = customer_outstanding.toString().split(",");

					if(tokens.length>0) {
						outstanding = tokens[0];
						credit_limit = tokens[1];
					}
					double outstndng=0, crdt_lmt=0, baln_lmt = 0;
					if(outstanding !=null && !outstanding.isEmpty() && credit_limit !=null & !credit_limit.isEmpty()){
						outstndng = Double.parseDouble(outstanding);
						crdt_lmt = Double.parseDouble(credit_limit);
						baln_lmt = crdt_lmt-outstndng;

						if(baln_lmt<0){
							baln_lmt=0;
						}

						if(tranblock_abovelimit!=null && !tranblock_abovelimit.isEmpty() && tranblock_abovelimit.matches("1")){
							if(outstndng>crdt_lmt){
								Toast.makeText(SalesOrderCustomer.this, "Outstanding exceeds credit limit", Toast.LENGTH_LONG).show();
							}
						}

					}
					invoi_creditlimit.setText(""+crdt_lmt);
					invoi_outstandingAmt.setText(""+outstndng);
					invoi_balancelimit.setText(""+baln_lmt);

				}

				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesOrder_layout, true);

					if (invoiceExceedTernsArr.size() > 0) {
						popup();
					}else{
						new AsyncCallTranBlockCreditLimit().execute();
					}



			}catch(NoSuchElementException e){
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}

		}
	}

	private class AsyncCallTranBlockCreditLimit extends AsyncTask<Void, Void, Void> {
		String custCode="";
		@Override
		protected void onPreExecute() {

			spinnerLayout = new LinearLayout(SalesOrderCustomer.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesOrder_layout, false);
			progressBar = new ProgressBar(SalesOrderCustomer.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);


			custCode = soedcodefield.getText().toString();
			invoiceCreditLimitArr.clear();
			Log.d("customerCode","cu "+custCode);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
		try{
			if(tran_block_credit_limit.matches("1")){
				invoiceCreditLimitArr = SalesOrderWebService.fncGetOutstandingSummary(custCode,"fncGetOutstandingSummary");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("invoiceCreditLimitArr",""+invoiceCreditLimitArr.size());

			if (invoiceCreditLimitArr.size() > 0) {
				popup();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesOrder_layout, true);
		}
	}

	public void currencyalertdialog() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				SalesOrderCustomer.this);
		final EditText editText = new EditText(SalesOrderCustomer.this);
		final ListView listview = new ListView(SalesOrderCustomer.this);
		LinearLayout layout = new LinearLayout(SalesOrderCustomer.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Currency");
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
		arrayAdapterSupp = new CustomAlertAdapterSupp(SalesOrderCustomer.this,
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

				arrayAdapterSupp = new CustomAlertAdapterSupp(
						SalesOrderCustomer.this, searchResults);
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
	
	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Deleting");
		alertDialog.setMessage("SalesOrder products will clear. Do you want to proceed");
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(SalesOrderCustomer.this, SalesOrderHeader.class);
						startActivity(i);
						SalesOrderCustomer.this.finish();

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
	
	/**  AsyncTask Start    **/
	
	private class AsyncCallWSSaleOrder extends AsyncTask<Void, Void, Void> {
		String dialogStatus;
		@Override
		protected void onPreExecute() {
			al.clear();
			currencycode.clear();
			loadprogress();
			dialogStatus = checkInternetStatus();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
			String finalDate = timeFormat.format(new Date());
			System.out.println(finalDate);
			try {
				
				if (onlineMode.matches("True")) {
					if (checkOffline == true) { //temp_offline
						
						if (dialogStatus.matches("true")) {
							serverdate = finalDate;
							al = getCustomerOffline();
							currencycode = OfflineSalesOrderWebService.getAllCurrencyOffline();
							settingvalue = OfflineSalesOrderWebService.generalSettingsServiceOffline();
							alclcrrncy =  OfflineSalesOrderWebService.getCurrencyValuesOffline(settingvalue);			
						} else {

							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {  //Onllineine
						
						serverdate = DateWebservice.getDateService("fncGetServerDate");
						currencycode = SalesOrderWebService.getAllCurrency("fncGetCurrency");
					
						getOnlineCustomer();
						settingvalue = SalesOrderWebService.generalSettingsService("fncGetGeneralSettings");
						alclcrrncy = SalesOrderWebService.getCurrencyValues("fncGetCurrency", settingvalue);
					}

				} else if (onlineMode.matches("False")) {  // permanent_offline
					serverdate = finalDate;
					al = getCustomerOffline();
					currencycode = OfflineSalesOrderWebService.getAllCurrencyOffline();
					settingvalue = OfflineSalesOrderWebService.generalSettingsServiceOffline();
					alclcrrncy =  OfflineSalesOrderWebService.getCurrencyValuesOffline(settingvalue);
				}
				
				
				crrncynm = alclcrrncy.get(0);
				crrncyrt = alclcrrncy.get(1);
				
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			gettingSetGet();
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesOrder_layout, true);
		}
	}
	
	private class AsyncCallWSCustGrop extends AsyncTask<String, Void, Void> {
		String dialogStatus;
		String navigateTo="",customercode="";
		@Override
		protected void onPreExecute() {
			cstmrgrpcdal.clear();
			loadprogress();
			dialogStatus = checkInternetStatus();

			customercode = soedcodefield.getText().toString();
		}

		@Override
		protected Void doInBackground(String... arg) {
			
			navigateTo = arg[0]; 
			
			try {
				
				if (onlineMode.matches("True")) {
					if (checkOffline == true) { //temp_offline
						
						if (dialogStatus.matches("true")) {
							cstmrgrpcdal = OfflineSalesOrderWebService.getCustGroupCodeOffline(customercode);
							OfflineSalesOrderWebService.getCustomerTaxOffline(customercode);
						} else {

							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {  //Onllineine
						
						Log.d("checkOffline Status -->", "False");
						cstmrgrpcdal = SalesOrderWebService.getCustGroupCode(customercode,"fncGetCustomer");
						SalesOrderWebService.getCustomerTax(customercode, "fncGetCustomer");

					}

				} else if (onlineMode.matches("False")) {  // permanent_offline
					
					cstmrgrpcdal = OfflineSalesOrderWebService.getCustGroupCodeOffline(customercode);
					OfflineSalesOrderWebService.getCustomerTaxOffline(customercode);
				}
				
				Log.d("Customer values", "" + cstmrgrpcdal);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesOrder_layout, true);

				if(cstmrgrpcdal.size()>0){
					SalesOrderSetGet.setSuppliercode(cstmrgrpcdal.get(0));
					SalesOrderSetGet.setSuppliergroupcode(cstmrgrpcdal.get(1));

					if(navigateTo.matches("Add Product")){
						Intent i = new Intent(SalesOrderCustomer.this,
								SalesAddProduct.class);
						startActivity(i);
						SalesOrderCustomer.this.finish();
					}else if(navigateTo.matches("Summary")){
						Intent i = new Intent(SalesOrderCustomer.this,
								SalesSummary.class);
						startActivity(i);
						SalesOrderCustomer.this.finish();
					}else{
						Intent i = new Intent(SalesOrderCustomer.this,
								SalesAddProduct.class);
						startActivity(i);
						SalesOrderCustomer.this.finish();
					}


			}


		}
	}
	
	private class AsyncCallWSCurrcode extends AsyncTask<Void, Void, Void> {
		String dialogStatus;
		@Override
		protected void onPreExecute() {
			loadprogress();
			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				
				if (onlineMode.matches("True")) {
					if (checkOffline == true) { //temp_offline
						
						if (dialogStatus.matches("true")) {
							crrncyrate = OfflineSalesOrderWebService.getCrrncyRateOffline(crrncycode);	
						} else {

							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {  //Onllineine
						
						crrncyrate = SalesOrderWebService.getCrrncyRate(crrncycode, "fncGetCurrency");
					}

				} else if (onlineMode.matches("False")) {  // permanent_offline
					
					crrncyrate = OfflineSalesOrderWebService.getCrrncyRateOffline(crrncycode);
				}
				
				Log.d("crrncycode", crrncycode);
			} catch (Exception e) {
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
			enableViews(salesOrder_layout, true);
		}
	}
	
	
	/**  AsyncTask End    **/
	
	/** Offline Start **/
	
	public void getOnlineCustomer(){
		SoapObject request = new SoapObject(NAMESPACE, "fncGetCustomerForSearch");
//		PropertyInfo companyCode = new PropertyInfo();
//
//		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
//
//		companyCode.setName("CompanyCode");
//		companyCode.setValue(cmpnyCode);
//		companyCode.setType(String.class);
//		request.addProperty(companyCode);
		
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("CompanyCode", companyCode);
		hm.put("VanCode", select_van);
		
		for (Entry<String, String> entry : hm.entrySet()) {

			PropertyInfo prodCode = new PropertyInfo();

			prodCode.setName(entry.getKey());
			prodCode.setValue(entry.getValue());
			prodCode.setType(String.class);
			request.addProperty(prodCode);

			System.out.printf("%s", entry.getKey());
			System.out.printf("%s%n", entry.getValue());
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				valid_url);
		try {
			androidHttpTransport.call(SOAP_ACTION + "fncGetCustomerForSearch", envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			suppTxt = response.toString();
			custresult = " { SaleOCustomer : " + suppTxt + "}";
			JSONObject jsonResponse;
			try {
				jsonResponse = new JSONObject(custresult);
				JSONArray jsonMainNode = jsonResponse
						.optJSONArray("SaleOCustomer");
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode
							.getJSONObject(i);

					String customercode = jsonChildNode.optString(
							"CustomerCode").toString();
					String customername = jsonChildNode.optString(
							"CustomerName").toString();
					String ReferenceLocation = jsonChildNode.optString(
							"ReferenceLocation").toString();
				
					Log.d("ReferenceLocation", ""+ReferenceLocation);
					
					HashMap<String, String> customerhm = new HashMap<String, String>();
					if(ReferenceLocation!=null && !ReferenceLocation.isEmpty()){
						customerhm.put(customercode, customername+"~"+ReferenceLocation);
					}else{
						customerhm.put(customercode, customername);	
					}
//					HashMap<String, String> customerhm = new HashMap<String, String>();
//					customerhm.put(customercode, customername);
					al.add(customerhm);
					hashmap.putAll(customerhm);
					alsaleorder.add(customercode);

				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			suppTxt = "Error occured";
		}
	}
	
	public ArrayList<HashMap<String, String>> getCustomerOffline(){
		String customer_jsonString = "";
		HashMap<String, String> customerhashValue = new HashMap<String, String>();
		ArrayList<HashMap<String, String>> custList = new ArrayList<HashMap<String, String>>();
		
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		customerhashValue.put("CompanyCode", cmpnyCode);
		customerhashValue.put("CustomerCode", "");
		customerhashValue.put("NeedOutstandingAmount", "");
		customerhashValue.put("AreaCode", "");
		customerhashValue.put("VanCode", select_van);
		
		customer_jsonString = OfflineDatabase.getCustomersList(customerhashValue);
		
		try {

			JSONObject customer_jsonResponse = new JSONObject(customer_jsonString);
			JSONArray customer_jsonMainNode = customer_jsonResponse.optJSONArray("JsonArray");

			int lengthJsonArr = customer_jsonMainNode.length();
			Log.d("customerlist size", "-->" + lengthJsonArr);
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode;
				try {

					jsonChildNode = customer_jsonMainNode.getJSONObject(i);
					String cust_code = jsonChildNode.optString("CustomerCode")
							.toString();
					String cust_Name = jsonChildNode.optString("CustomerName")
							.toString();
					String ReferenceLocation = jsonChildNode.optString(
							"ReferenceLocation").toString();
				
					Log.d("ReferenceLocation", ""+ReferenceLocation);
					
					HashMap<String, String> customerhm = new HashMap<String, String>();
					if(ReferenceLocation!=null && !ReferenceLocation.isEmpty()){
						customerhm.put(cust_code, cust_Name+"~"+ReferenceLocation);
					}else{
						customerhm.put(cust_code, cust_Name);	
					}
					
//					HashMap<String, String> customerhm = new HashMap<String, String>();
//					customerhm.put(cust_code, cust_Name);
					custList.add(customerhm);
					hashmap.putAll(customerhm);
					alsaleorder.add(cust_code);

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return custList;
	}
	 
	 private String checkInternetStatus() {
		 String internetStatus = "";
		 String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		 if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			 if(mobileHaveOfflineMode.matches("1")){

			checkOffline = OfflineCommon.isConnected(SalesOrderCustomer.this);
//			String internetStatus = "";
			if (onlineMode.matches("True")) {
				if (checkOffline == true) {
					String Off_dialog = OfflineDatabase.getInternetMode("OfflineDialog");
					if (Off_dialog.matches("true")) {
						internetStatus = "true";
					} else {
						offlineCommon.OfflineAlertDialog();
						Boolean dialogStatus = offlineCommon.showDialog();
						OfflineDatabase.updateInternetMode("OfflineDialog",dialogStatus + "");
						Log.d("Offline DialogStatus", "" + dialogStatus);
						internetStatus = "" + dialogStatus;
					}
				} else if (checkOffline == false) {
					String on_dialog = OfflineDatabase.getInternetMode("OnlineDialog");
					if (on_dialog.matches("true")) {
						internetStatus = "true";
					} else {
						offlineCommon.onlineAlertDialog();
						boolean dialogStatus = offlineCommon.showDialog();
						OfflineDatabase.updateInternetMode("OnlineDialog",dialogStatus + "");
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
						offlineLayout.setBackgroundResource(drawable.temp_offline_pattern_bg);
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
	 
	 
	 public void loadprogress(){
		 spinnerLayout = new LinearLayout(SalesOrderCustomer.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesOrder_layout, false);
			progressBar = new ProgressBar(SalesOrderCustomer.this);
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

	public void customerSetGet() {
		SalesOrderSetGet.setCurrencycode(edcrrncycd.getText().toString());
		SalesOrderSetGet.setSaleorderdate(edtdate.getText().toString());
		SalesOrderSetGet.setDeliverydate(eddlvrydt.getText().toString());
		SalesOrderSetGet.setCustomercode(soedcodefield.getText().toString());
		SalesOrderSetGet.setCustomername(soednamefield.getText().toString());
		SalesOrderSetGet.setCurrencyrate(edcrrncyrt.getText().toString());
		SalesOrderSetGet.setRemarks(edremarks.getText().toString());
	//	SOTDatabase.deleteAttribute();
//		ConvertToSetterGetter.setSoNo("");
	}

	public void gettingSetGet() {
		soedcodefield.setText(SalesOrderSetGet.getCustomercode());
		soednamefield.setText(SalesOrderSetGet.getCustomername());
		edremarks.setText(SalesOrderSetGet.getRemarks());

		if (SalesOrderSetGet.getSaleorderdate().equals("")) {
			edtdate.setText(serverdate);
		} else {
			edtdate.setText(SalesOrderSetGet.getSaleorderdate());
		}

		if (SalesOrderSetGet.getDeliverydate().equals("")) {
			eddlvrydt.setText(serverdate);
		} else {
			eddlvrydt.setText(SalesOrderSetGet.getDeliverydate());
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


	public void popup()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_exceeds_terms, null);
		final TextView textView = (TextView) dialogView.findViewById(R.id.textView);
		final ListView listview = (ListView) dialogView.findViewById(R.id.listview);
		final LinearLayout popup_header_layout = (LinearLayout) dialogView.findViewById(R.id.popup_header_layout);
		final TextView popup_title = (TextView) dialogView.findViewById(R.id.popup_title);

		if(invoiceExceedTernsArr.size()>0){
			popup_header_layout.setVisibility(View.VISIBLE);
			popup_title.setText("Exceeds Terms");
			textView.setText("Terms Exceeds. Cannot able to create order");
			// Keys used in Hashmap
			String[] from = { "InvoiceNo","InvoiceDate","NetTotal" };
			// Ids of views in listview_layout
			int[] to = { R.id.textView1,R.id.textView2,R.id.textView3};
			SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), invoiceExceedTernsArr, R.layout.dialog_exceeds_terms_list_item, from, to);
			listview.setAdapter(adapter);
		}else if(invoiceCreditLimitArr.size()>0){
			popup_header_layout.setVisibility(View.GONE);
			popup_title.setText("Exceeds Credit Limit");
			textView.setText("Credit Limit Exceeds. Cannot able to create order");
			// Keys used in Hashmap
			String[] from = { "Customercode","CustomerName","BalanceAmount" };
			// Ids of views in listview_layout
			int[] to = { R.id.textView1,R.id.textView2,R.id.textView3};
			SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), invoiceCreditLimitArr, R.layout.credit_limit_list_item, from, to);
			listview.setAdapter(adapter);
		}

		dialogBuilder.setView(dialogView);

		dialogBuilder.setNegativeButton("Ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		//Create alert dialog object via builder
		final AlertDialog alertDialogObject = dialogBuilder.create();

		//Show the dialog
		alertDialogObject.show();
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(SalesOrderCustomer.this, SalesOrderHeader.class);
		startActivity(i);
		SalesOrderCustomer.this.finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onListItemClick(String item) {
		
		 menu.toggle();
	}
	  
}
