package com.winapp.sot;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.Spinner;
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
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.sotdetails.CustomerSetterGetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

public class CashInvoiceCustomer extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace {

	private EditText inedcodefield, inednamefield, edtdate, edcrrncycd,
			edcrrncynm, edcrrncyrt, edremarks, inv_address;
	private CustomAlertAdapterSupp arrayAdapterSupp;
	private int textlength = 0;
	private ProgressBar progressBar;
	private LinearLayout spinnerLayout, invoice_layout, customer_layout, invoi_creditlimitlayout,
			invoi_outstandingLayout, invoi_balancelimitLayout;
	private TextView listing_screen, customer_screen, addProduct_screen,
			summary_screen, invoi_outstandingAmt, invoi_creditlimit, invoi_balancelimit;
	Calendar myCalendar;
	private String serverdate, settingvalue, crrncynm, crrncyrt, crrncycode,
			crrncyrate, currencyname, select_van, customer_outstanding;
	String valid_url;
	private AlertDialog myalertDialog = null;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	ArrayList<String> alinvoice = new ArrayList<String>();
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> getArraylsitcur = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> searchResults;
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetCustomer";
	static String custresult;
	String suppTxt = null,tranblock_abovelimit="", credit_limit="";
	ArrayList<String> alclcrrncy = new ArrayList<String>();
	ArrayList<String> cstmrgrpcdal = new ArrayList<String>();
	ArrayList<HashMap<String, String>> currencycode = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> hashmap = new HashMap<String, String>();
	Button in_addProduct;
	private SlidingMenu menu;
	ImageButton search, back, printer, addnew;
	Spinner invoice_taxtype;
	// Offline
	LinearLayout offlineLayout,header_portrait,header_landscape;
	private OfflineDatabase offlineDatabase;
	boolean checkOffline;
	String onlineMode, offlineDialogStatus,mobileHaveOfflineMode="",Address1="",Address2="",Address3="";
	private OfflineCommon offlineCommon;
	private JSONArray custjsonMainNode;
	private JSONObject custjsonResp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.invoice_customer);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Cash Invoice Customer");

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
		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		// @Offline
		onlineMode = OfflineDatabase.getOnlineMode();
		offlineDatabase = new OfflineDatabase(CashInvoiceCustomer.this);
		offlineCommon = new OfflineCommon(CashInvoiceCustomer.this);
		checkOffline = OfflineCommon.isConnected(CashInvoiceCustomer.this);
		OfflineDatabase.init(CashInvoiceCustomer.this);
		new OfflineSalesOrderWebService(CashInvoiceCustomer.this);
		offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);

		invoice_layout = (LinearLayout) findViewById(R.id.invoiceCust_layout);
		inedcodefield = (EditText) findViewById(R.id.invoi_codefield);
		inednamefield = (EditText) findViewById(R.id.invoi_namefield);
		edcrrncycd = (EditText) findViewById(R.id.invoi_edCurcode);
		edtdate = (EditText) findViewById(R.id.editTextDate);
		edcrrncynm = (EditText) findViewById(R.id.invoi_edCurlbl);
		edcrrncyrt = (EditText) findViewById(R.id.invoi_edCurRate);
		edremarks = (EditText) findViewById(R.id.invoi_editTextRemarks);
		// back = (ImageView) findViewById(R.id.back);
		in_addProduct = (Button) findViewById(R.id.invoi_addProduct);
		invoice_taxtype = (Spinner) findViewById(R.id.invoice_taxtype);

		invoi_creditlimitlayout = (LinearLayout) findViewById(R.id.invoi_creditlimitlayout);
		invoi_outstandingLayout = (LinearLayout) findViewById(R.id.invoi_outstandingLayout);
		invoi_balancelimitLayout = (LinearLayout) findViewById(R.id.invoi_balancelimitLayout);
		invoi_creditlimit = (TextView) findViewById(R.id.invoi_creditlimit);
		invoi_outstandingAmt = (TextView) findViewById(R.id.invoi_outstandingAmt);
		invoi_balancelimit = (TextView) findViewById(R.id.invoi_balancelimit);

		listing_screen = (TextView) findViewById(R.id.listing_screen_tab);
		customer_screen = (TextView) findViewById(R.id.customer_screen_tab);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen_tab);
		summary_screen = (TextView) findViewById(R.id.sum_screen_tab);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);

		inv_address = (EditText) findViewById(R.id.inv_address);

		customer_layout.setVisibility(View.GONE);
		// customer_screen.setBackgroundColor(Color.parseColor("#00AFF0"));
		customer_screen.setTextColor(Color.parseColor("#FFFFFF"));
		customer_screen.setBackgroundResource(drawable.tab_select);

		///
				header_portrait = (LinearLayout) findViewById(R.id.linear_layout1);
				header_landscape = (LinearLayout) findViewById(R.id.linear_layout2);
				
				ab.setBackgroundDrawable(getResources().getDrawable(
						drawable.header_bg));
				
				header_portrait.setVisibility(View.GONE);
				header_landscape.setVisibility(View.VISIBLE);
							
				customer_screen.setBackgroundResource(drawable.tab_select);
		///
		
		myCalendar = Calendar.getInstance();
		al.clear();
		currencycode.clear();
		cstmrgrpcdal.clear();
		FWMSSettingsDatabase.init(CashInvoiceCustomer.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,CashInvoiceCustomer.this);
		new SalesOrderWebService(valid_url);

		SOTDatabase.init(CashInvoiceCustomer.this);
		select_van = SOTDatabase.getVandriver();

		if (select_van != null && !select_van.isEmpty()) {
		} else {
			select_van = "";
		}

		if (onlineMode.matches("True")) {
			offlineLayout.setVisibility(View.GONE);
		} else if (onlineMode.matches("False")) {
			offlineLayout.setVisibility(View.VISIBLE);
		}

		tranblock_abovelimit = SalesOrderSetGet.getTranblockinvoiceabovelimit();
		
		if(tranblock_abovelimit!=null && !tranblock_abovelimit.isEmpty()){
		
		}else{
			tranblock_abovelimit="";
		}

//		String edit_invoice = ConvertToSetterGetter.getEdit_inv_no();
		Cursor cursr = SOTDatabase.getCursor();
		int productCount = cursr.getCount();
		Log.d("productCount", ""+productCount);

		/*if(productCount>0){
			inedcodefield.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			inedcodefield.setBackgroundResource(R.drawable.labelbg);
			inedcodefield.setFocusable(false);
		}else{
			inedcodefield.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0);
			inedcodefield.setBackgroundResource(R.drawable.edittext_bg);
			inedcodefield.setFocusable(true);
		}*/

//		tranblock_abovelimit="1";
		AsyncCallWSSaleOrder inAcws = new AsyncCallWSSaleOrder();
		inAcws.execute();

		// invoice_taxtype.setOnItemSelectedListener(new
		// OnItemSelectedListener() {
		// @Override
		// public void onItemSelected(AdapterView<?> arg0, View view,
		// int position, long id) {
		//
		// int item = invoice_taxtype.getSelectedItemPosition();
		//
		// Log.d("item", ""+item);
		// }
		// @Override
		// public void onNothingSelected(AdapterView<?> arg0) {
		// }
		// });

		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Cursor cursor = SOTDatabase.getCursor();
				int count = cursor.getCount();
				if (count > 0) {
					alertDialog();
				} else {
					Intent i = new Intent(CashInvoiceCustomer.this,
							CashInvoiceHeader.class);
					startActivity(i);
					CashInvoiceCustomer.this.finish();
				}

			}
		});

		addProduct_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				ConvertToSetterGetter.setEdit_inv_no("");
				if (inedcodefield.getText().length() != 0
						&& inedcodefield.getText().toString() != ""
						&& inednamefield.getText().length() != 0
						&& inednamefield.getText().toString() != "") {
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

//				ConvertToSetterGetter.setEdit_inv_no("");
				if (inedcodefield.getText().length() != 0
						&& inedcodefield.getText().toString() != ""
						&& inednamefield.getText().length() != 0
						&& inednamefield.getText().toString() != "") {
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

		in_addProduct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				ConvertToSetterGetter.setEdit_inv_no("");
				if (inedcodefield.getText().length() != 0
						&& inedcodefield.getText().toString() != ""
						&& inednamefield.getText().length() != 0
						&& inednamefield.getText().toString() != "") {
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

		inedcodefield
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						inedcodefield) {
					@Override
					public boolean onDrawableClick() {
						custalertDialogSearch();
						return true;

					}

				});
		edcrrncycd
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						inedcodefield) {
					@Override
					public boolean onDrawableClick() {
						currencyalertdialog();
						return true;

					}

				});

		inedcodefield.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					boolean res = false;
					for (String alphabet : alinvoice) {
						if (alphabet.toLowerCase().equals(
								inedcodefield.getText().toString()
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
							if (inedcodefield.getText().toString()
									.toLowerCase()
									.equals(keyValue.toLowerCase())) {
								inednamefield.setText(value);
							}
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"Invalid Customer Code", Toast.LENGTH_SHORT)
								.show();
						inedcodefield.setText("");
						inednamefield.setText("");
					}
					inedcodefield.addTextChangedListener(new TextWatcher() {
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

							textlength = inedcodefield.getText().length();
							inednamefield.setText("");
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
					new DatePickerDialog(CashInvoiceCustomer.this, indate,
							myCalendar.get(Calendar.YEAR), myCalendar
									.get(Calendar.MONTH), myCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});

	}

	private void inDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		edtdate.setText(sdf.format(myCalendar.getTime()));

	}

	public void custalertDialogSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				CashInvoiceCustomer.this);
		final EditText editText = new EditText(CashInvoiceCustomer.this);
		final ListView listview = new ListView(CashInvoiceCustomer.this);
		LinearLayout layout = new LinearLayout(CashInvoiceCustomer.this);
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(CashInvoiceCustomer.this, al);
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

					Log.d("onclick loc", "" + values);

					if (values.contains("~")) {
						String[] parts = values.split("~");
						String name = parts[0];
						// String location = parts[1];

						inedcodefield.setText(keyValues);
						inednamefield.setText(name);
					} else {
						inedcodefield.setText(keyValues);
						inednamefield.setText(values);
					}



					AsyncCallWSCustomerOutstanding taskAmt = new AsyncCallWSCustomerOutstanding();
					taskAmt.execute();

					inedcodefield.addTextChangedListener(new TextWatcher() {
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

							textlength = inedcodefield.getText().length();
							inednamefield.setText("");

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
						CashInvoiceCustomer.this, searchResults);
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

		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				CashInvoiceCustomer.this);
		final EditText editText = new EditText(CashInvoiceCustomer.this);
		final ListView listview = new ListView(CashInvoiceCustomer.this);
		LinearLayout layout = new LinearLayout(CashInvoiceCustomer.this);
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
		arrayAdapterSupp = new CustomAlertAdapterSupp(CashInvoiceCustomer.this,
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
						CashInvoiceCustomer.this, searchResults);
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
		String dialogStatus;

		@Override
		protected void onPreExecute() {
			al.clear();
			currencycode.clear();
			spinnerLayout = new LinearLayout(CashInvoiceCustomer.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(invoice_layout, false);
			progressBar = new ProgressBar(CashInvoiceCustomer.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);

			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
			String finalDate = timeFormat.format(new Date());
			System.out.println(finalDate);
			try {
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");
							serverdate = finalDate;
							al = getCustomerOffline();
							currencycode = OfflineSalesOrderWebService
									.getAllCurrencyOffline();
							settingvalue = OfflineSalesOrderWebService
									.generalSettingsServiceOffline();
							alclcrrncy = OfflineSalesOrderWebService
									.getCurrencyValuesOffline(settingvalue);
						} else {
							Log.d("CheckOffline Alert -->", "False");
							//finish();
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {
						Log.d("checkOffline Status -->", "False");
						serverdate = DateWebservice
								.getDateService("fncGetServerDate");
						currencycode = SalesOrderWebService
								.getAllCurrency("fncGetCurrency");
						getOnlineCustomer();
						settingvalue = SalesOrderWebService
								.generalSettingsService("fncGetGeneralSettings");
						alclcrrncy = SalesOrderWebService.getCurrencyValues(
								"fncGetCurrency", settingvalue);
					}

				} else if (onlineMode.matches("False")) {
					Log.d("Online Mode -->", onlineMode);
					serverdate = finalDate;
					al = getCustomerOffline();
					currencycode = OfflineSalesOrderWebService
							.getAllCurrencyOffline();
					settingvalue = OfflineSalesOrderWebService
							.generalSettingsServiceOffline();
					alclcrrncy = OfflineSalesOrderWebService
							.getCurrencyValuesOffline(settingvalue);
				}

				crrncynm = alclcrrncy.get(0);
				crrncyrt = alclcrrncy.get(1);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			gettingSetGet();
			// custalertDialogSearch(); // newly added
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(invoice_layout, true);
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
		String dialogStatus, navigateTo = "";

		@Override
		protected void onPreExecute() {
			cstmrgrpcdal.clear();
			spinnerLayout = new LinearLayout(CashInvoiceCustomer.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(invoice_layout, false);
			progressBar = new ProgressBar(CashInvoiceCustomer.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);

			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(String... arg) {

			navigateTo = arg[0];

			try {
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");
							cstmrgrpcdal = OfflineSalesOrderWebService
									.getCustGroupCodeOffline(inedcodefield.getText().toString());
							OfflineSalesOrderWebService
									.getCustomerTaxOffline(inedcodefield.getText().toString());
						} else {
							Log.d("CheckOffline Alert -->", "False");
						//	finish();
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {
						Log.d("checkOffline Status -->", "False");
						cstmrgrpcdal = SalesOrderWebService.getCustGroupCode(
								inedcodefield.getText().toString(),
								"fncGetCustomer");
						SalesOrderWebService.getCustomerTax(inedcodefield.getText().toString(), "fncGetCustomer");
					}

				} else if (onlineMode.matches("False")) {
					Log.d("Customer Online", onlineMode);
					cstmrgrpcdal = OfflineSalesOrderWebService
							.getCustGroupCodeOffline(inedcodefield.getText().toString());
					OfflineSalesOrderWebService
							.getCustomerTaxOffline(inedcodefield.getText().toString());
				}
				Log.d("Customer values", "" + cstmrgrpcdal);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(invoice_layout, true);

			if (cstmrgrpcdal.size() > 0) {
				SalesOrderSetGet.setSuppliercode(cstmrgrpcdal.get(0));
				SalesOrderSetGet.setSuppliergroupcode(cstmrgrpcdal.get(1));

				if (navigateTo.matches("Add Product")) {
					Intent i = new Intent(CashInvoiceCustomer.this,
							CashInvoiceAddProduct.class);
					startActivity(i);
					CashInvoiceCustomer.this.finish();
				} else if (navigateTo.matches("Summary")) {
					Intent i = new Intent(CashInvoiceCustomer.this,
							CashInvoiceSummary.class);
					startActivity(i);
					CashInvoiceCustomer.this.finish();
				} else {
					Intent i = new Intent(CashInvoiceCustomer.this,
							CashInvoiceAddProduct.class); // Emergency exit
					startActivity(i);
					CashInvoiceCustomer.this.finish();
				}

			}
		}
	}

	private class AsyncCallWSCurrcode extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		String dialogStatus;

		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(CashInvoiceCustomer.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(invoice_layout, false);
			progressBar = new ProgressBar(CashInvoiceCustomer.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);

			dialogStatus = checkInternetStatus();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");
							crrncyrate = OfflineSalesOrderWebService
									.getCrrncyRateOffline(crrncycode);
						} else {
							Log.d("CheckOffline Alert -->", "False");
						//	finish();
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else {
						Log.d("checkOffline Status -->", "False");
						crrncyrate = SalesOrderWebService.getCrrncyRate(
								crrncycode, "fncGetCurrency");
					}

				} else if (onlineMode.matches("False")) {
					Log.d("Customer Online", onlineMode);
					crrncyrate = OfflineSalesOrderWebService
							.getCrrncyRateOffline(crrncycode);
				}
				Log.d("Customer values", "" + cstmrgrpcdal);
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
			enableViews(invoice_layout, true);
		}
	}

	public void customerSetGet() {
		SalesOrderSetGet.setCurrencycode(edcrrncycd.getText().toString());
		SalesOrderSetGet.setSaleorderdate(edtdate.getText().toString());
		SalesOrderSetGet.setCustomercode(inedcodefield.getText().toString());
		SalesOrderSetGet.setCustomername(inednamefield.getText().toString());
		SalesOrderSetGet.setCurrencyrate(edcrrncyrt.getText().toString());
		SalesOrderSetGet.setRemarks(edremarks.getText().toString());
		ConvertToSetterGetter.setSoNo("");
		CustomerSetterGetter.setDiscountPercentage("");
		CustomerSetterGetter.setBillDiscount("");
	}

	public void gettingSetGet() {
		inedcodefield.setText(SalesOrderSetGet.getCustomercode());
		inednamefield.setText(SalesOrderSetGet.getCustomername());
		edremarks.setText(SalesOrderSetGet.getRemarks());

		if (SalesOrderSetGet.getSaleorderdate().equals("")) {
			edtdate.setText(serverdate);
		} else {
			edtdate.setText(SalesOrderSetGet.getSaleorderdate());
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

	public void getOnlineCustomer() {
		SoapObject request = new SoapObject(NAMESPACE, webMethName);
		// PropertyInfo companyCode = new PropertyInfo();
		//
		String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		//
		// companyCode.setName("CompanyCode");
		// companyCode.setValue(cmpnyCode);
		// companyCode.setType(String.class);
		// request.addProperty(companyCode);

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("CompanyCode", cmpnyCode);
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
		HttpTransportSE androidHttpTransport = new HttpTransportSE(valid_url);
		try {
			androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
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
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					String customercode = jsonChildNode.optString(
							"CustomerCode").toString();
					String customername = jsonChildNode.optString(
							"CustomerName").toString();
					String ReferenceLocation = jsonChildNode.optString(
							"ReferenceLocation").toString();

					Log.d("ReferenceLocation", "" + ReferenceLocation);

					HashMap<String, String> customerhm = new HashMap<String, String>();
					if (ReferenceLocation != null
							&& !ReferenceLocation.isEmpty()) {
						customerhm.put(customercode, customername + "~"
								+ ReferenceLocation);
					} else {
						customerhm.put(customercode, customername);
					}

					al.add(customerhm);
					hashmap.putAll(customerhm);
					alinvoice.add(customercode);

				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			suppTxt = "Error occured";
		}
	}

	public ArrayList<HashMap<String, String>> getCustomerOffline() {
		String customer_jsonString = "";
		HashMap<String, String> customerhashValue = new HashMap<String, String>();
		ArrayList<HashMap<String, String>> custList = new ArrayList<HashMap<String, String>>();

		String cmpnyCode = SupplierSetterGetter.getCompanyCode();

		customerhashValue.put("CompanyCode", cmpnyCode);
		customerhashValue.put("CustomerCode", "");
		customerhashValue.put("NeedOutstandingAmount", "");
		customerhashValue.put("AreaCode", "");
		customerhashValue.put("VanCode", select_van);

		customer_jsonString = OfflineDatabase
				.getCustomersList(customerhashValue);

		try {

			JSONObject customer_jsonResponse = new JSONObject(
					customer_jsonString);
			JSONArray customer_jsonMainNode = customer_jsonResponse
					.optJSONArray("JsonArray");

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

					Log.d("ReferenceLocation", "" + ReferenceLocation);

					HashMap<String, String> customerhm = new HashMap<String, String>();
					if (ReferenceLocation != null
							&& !ReferenceLocation.isEmpty()) {
						customerhm.put(cust_code, cust_Name + "~"
								+ ReferenceLocation);
					} else {
						customerhm.put(cust_code, cust_Name);
					}

					// HashMap<String, String> customerhm = new HashMap<String,
					// String>();
					// customerhm.put(cust_code, cust_Name);
					custList.add(customerhm);
					hashmap.putAll(customerhm);
					alinvoice.add(cust_code);

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return custList;
	}

	private class AsyncCallWSCustomerOutstanding extends
			AsyncTask<Void, Void, Void> {
		String dialogStatus,customerCode;

		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(CashInvoiceCustomer.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(invoice_layout, false);
			progressBar = new ProgressBar(CashInvoiceCustomer.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
			customer_outstanding = "";
			dialogStatus = checkInternetStatus();
			customerCode = inedcodefield.getText().toString();
			inv_address.setText("");
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
						//	finish();
							if(mobileHaveOfflineMode.matches("1")){
								finish();
							}
						}

					} else { // Online
						customer_outstanding = SalesOrderWebService
								.getCustomerOutstandingAmount(customerCode,
										"fncGetCustomer",""); // getTaxCode

						String cmpnyCode = SupplierSetterGetter.getCompanyCode();
						HashMap<String, String> custhash = new HashMap<String, String>();
						custhash.put("CompanyCode", cmpnyCode);
						custhash.put("CustomerCode", customerCode);

						String custjsonStr = SalesOrderWebService.getSODetail(custhash,
								"fncGetCustomer");
						custjsonResp = new JSONObject(custjsonStr);
						custjsonMainNode = custjsonResp.optJSONArray("SODetails");

						int custJsonArr = custjsonMainNode.length();
						for (int i = 0; i < custJsonArr; i++) {

							JSONObject jsonChildNode;

							try {
								jsonChildNode = custjsonMainNode.getJSONObject(i);

								Address1 = jsonChildNode.optString("Address1")
										.toString();
								Address2 = jsonChildNode.optString("Address2")
										.toString();
								Address3 = jsonChildNode.optString("Address3")
										.toString();

							} catch (JSONException e) {

								e.printStackTrace();
							}
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

				Log.d("Address1",Address1);
				Log.d("Address2",Address2);
				Log.d("Address3",Address3);

				inv_address.setText(Address1 +" "+ Address2 +" "+ Address3);

			if(customer_outstanding!=null && !customer_outstanding.isEmpty()){	
				
			StringTokenizer tokens = new StringTokenizer(customer_outstanding.toString(), ",");
		    String outstanding= tokens.nextToken();
		    credit_limit = tokens.nextToken();
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
					Toast.makeText(CashInvoiceCustomer.this, "Outstanding exceeds credit limit", Toast.LENGTH_LONG).show();
				}
				}
				
			}
			invoi_creditlimit.setText(""+crdt_lmt);
			invoi_outstandingAmt.setText(""+outstndng);
			invoi_balancelimit.setText(""+baln_lmt);
			
			}
			
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(invoice_layout, true);
			
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(CashInvoiceCustomer.this, CashInvoiceHeader.class);
		startActivity(i);
		CashInvoiceCustomer.this.finish();
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

	// offline
	private String checkInternetStatus() {
		String internetStatus = "";
		String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			if(mobileHaveOfflineMode.matches("1")){
		checkOffline = OfflineCommon.isConnected(CashInvoiceCustomer.this);
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
			invoi_creditlimitlayout.setVisibility(View.VISIBLE);
			invoi_outstandingLayout.setVisibility(View.VISIBLE);
			invoi_balancelimitLayout.setVisibility(View.VISIBLE);
			if (checkOffline == true) {
				if (internetStatus.matches("true")) {
					offlineLayout.setVisibility(View.VISIBLE);
					invoi_creditlimitlayout.setVisibility(View.GONE);
					invoi_outstandingLayout.setVisibility(View.GONE);
					invoi_balancelimitLayout.setVisibility(View.GONE);
					offlineLayout
							.setBackgroundResource(drawable.temp_offline_pattern_bg);
				}
			}

		} else if (onlineMode.matches("False")) {
			offlineLayout.setVisibility(View.VISIBLE);
			invoi_creditlimitlayout.setVisibility(View.GONE);
			invoi_outstandingLayout.setVisibility(View.GONE);
			invoi_balancelimitLayout.setVisibility(View.GONE);
		}

//		return internetStatus;
	}else{
		internetStatus = "false";
	}
}else{
		internetStatus = "false";
		}
		return internetStatus;
	}

	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Deleting");
		alertDialog
				.setMessage("Invoice products will clear. Do you want to proceed");
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(CashInvoiceCustomer.this,
								CashInvoiceHeader.class);
						startActivity(i);
						CashInvoiceCustomer.this.finish();
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
