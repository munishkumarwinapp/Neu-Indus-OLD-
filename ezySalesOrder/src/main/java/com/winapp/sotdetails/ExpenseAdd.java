package com.winapp.sotdetails;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
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

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.winapp.model.TaxData;
import com.winapp.sot.InvoiceCustomer;
import com.winapp.sot.SO;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.sot.WebServiceClass;

public class ExpenseAdd extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace {
	SlidingMenu menu;
	ProgressBar progressBar;
	LinearLayout spinnerLayout, expenseadd_layout, customer_layout;
	ImageButton searchIcon, addIcon, expense_add;
	TextView listing_screen, customer_screen, addProduct_screen,summary_screen,sup_code;
	EditText exp_date, exp_remarks, exp_desc, exp_amt,supplier,sl_total,sl_tax,sl_netTotal,payTo;
	ListView expensemaster_list;
	
	ArrayList<SO> exapnselistArray = new ArrayList<SO>();
	ArrayList<HashMap<String, String>> getExpenseArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> expenselist = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> taxAl = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> searchResults;
	ExpenseMasterAdapter expenseAdapter;
	CustomAlertAdapterSupp customAdapter;
	TextView exp_code,tax_code;
	int textlength = 0;
	String serverdate = "", valid_url ="", expensemaster_jsonString ="",taxcode_jsonString="";
	ArrayList<HashMap<String, String>> currencycode = new ArrayList<HashMap<String, String>>();
	JSONObject expensemaster_jsonResponse;
	JSONArray expensemaster_jsonNode;
	private AlertDialog myalertDialog = null;
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetSupplier";
	static String custresult,cusResult;
	private static String webMethNames = "fncGetTax";
	String suppTxt = null,taxCode="",taxtype = "",tran_type="";
	ArrayList<TaxData> Customer_taxCode_List = new ArrayList<TaxData>();
	ArrayList<TaxData> list = new ArrayList<>();
	Spinner tax_desc;
	int select_pos_tax_code = 0;
	JSONObject taxcode_jsonResponse;
	Calendar myCalendar;
	JSONArray taxcode_jsonMainNode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.expense_add);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Add Expense");
		searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
		addIcon = (ImageButton) customNav.findViewById(R.id.custcode_img);

		searchIcon.setVisibility(View.INVISIBLE);
		addIcon.setVisibility(View.INVISIBLE);
		
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
		
		FWMSSettingsDatabase.init(ExpenseAdd.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,ExpenseAdd.this);
		new WebServiceClass(valid_url);
		SOTDatabase.init(ExpenseAdd.this);
		myCalendar = Calendar.getInstance();
		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen= (TextView) findViewById(R.id.sum_screen);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);
		customer_screen.setVisibility(View.GONE);
		customer_layout.setVisibility(View.GONE);
//		addProduct_screen.setBackgroundColor(Color.parseColor("#626776"));
		addProduct_screen.setTextColor(Color.parseColor("#FFFFFF"));
		addProduct_screen.setBackgroundResource(drawable.tab_select);
		addProduct_screen.setText("Expense");
		
		expenseadd_layout = (LinearLayout) findViewById(R.id.expenseadd_layout);
		exp_date = (EditText) findViewById(R.id.exp_date);
		exp_remarks = (EditText) findViewById(R.id.exp_remarks);
		exp_code = (TextView) findViewById(R.id.exp_code);
		tax_code = (TextView) findViewById(R.id.tax_code);
		exp_desc = (EditText) findViewById(R.id.exp_desc);
		supplier = (EditText) findViewById(R.id.supplier);
		tax_desc = (Spinner) findViewById(R.id.tax_desc);
		sl_total = (EditText) findViewById(R.id.sl_total);
		sl_tax = (EditText) findViewById(R.id.sl_tax);
		sl_netTotal = (EditText) findViewById(R.id.sl_netTotal);
		exp_amt = (EditText) findViewById(R.id.exp_amt);	
		expense_add = (ImageButton) findViewById(R.id.expense_add);
		expensemaster_list = (ListView) findViewById(R.id.expensemaster_list);
		sup_code = (TextView) findViewById(R.id.sup_code);
		payTo = (EditText) findViewById(R.id.payTo);
		supplier.setText("");
		payTo.setText("");

		String remark = SalesOrderSetGet.getRemarks();
		
		if(remark!=null && !remark.isEmpty()){
			exp_remarks.setText(remark);
		}

		Cursor mCursor = SOTDatabase.getExpenseCursor();
		Log.d("mCursorgetCount", "" + mCursor.getCount());
		int count = mCursor.getCount();

		if(count==0){
			ExpenseAsyncCall expense = new ExpenseAsyncCall();
			expense.execute();
		}else{
			ExpenseAsyncCall expense = new ExpenseAsyncCall();
			expense.execute();

			sl_total.setText(SOTDatabase.getExpenseTotalSubTot());
			sl_tax.setText(SOTDatabase.getExpenseTotalTax());
			sl_netTotal.setText(SOTDatabase.getExpenseTotalNetTot());
		}
		


		/**--  OnClick Start   --**/
		
		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Cursor cursor = SOTDatabase.getExpenseCursor();
				int count = cursor.getCount();
				if(count>0){
					alertDialog();
				}else{

					Intent i = new Intent(ExpenseAdd.this, ExpenseHeader.class);
					startActivity(i);
					ExpenseAdd.this.finish();
				}
				supplier.setText("");
				payTo.setText("");
				exp_date.setText("");
			}
		});

		summary_screen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomerSetGet();
				SalesOrderSetGet.setRemarks(exp_remarks.getText().toString());
				Intent i = new Intent(ExpenseAdd.this, ExpenseSummary.class);
				startActivity(i);
				ExpenseAdd.this.finish();
			}
		});
		
		exp_desc.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(exp_desc) {
			@Override
			public boolean onDrawableClick() {
				alertDialogExpenseSearch();
				return true;
			}
		});

		supplier.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
				supplier) {
			@Override
			public boolean onDrawableClick() {
				alertDialogSupplierSearch();
				return true;
			}
		});


		if(SalesOrderSetGet.getExpense_dateChange() != null && !SalesOrderSetGet.getExpense_dateChange().matches("")) {
			Log.d("getExpense_dateChange", SalesOrderSetGet.getExpense_dateChange());
			if (SalesOrderSetGet.getExpense_dateChange().matches("0")) {
				exp_date.setEnabled(false);
				exp_date.setCursorVisible(false);
				exp_date.setBackgroundResource(R.drawable.labelbg);
			} else {
				exp_date.setEnabled(true);
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

				exp_date.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (MotionEvent.ACTION_UP == event.getAction())
							new DatePickerDialog(ExpenseAdd.this, indate,
									myCalendar.get(Calendar.YEAR), myCalendar
									.get(Calendar.MONTH), myCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
						return false;
					}
				});
			}
		}else{
			exp_date.setEnabled(true);
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

			exp_date.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (MotionEvent.ACTION_UP == event.getAction())
						new DatePickerDialog(ExpenseAdd.this, indate,
								myCalendar.get(Calendar.YEAR), myCalendar
								.get(Calendar.MONTH), myCalendar
								.get(Calendar.DAY_OF_MONTH)).show();
					return false;
				}
			});
		}

		tax_desc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parentView,
											   View selectedItemView, int position, long id) {


//						parentView.getItemAtPosition(position).toString();
						select_pos_tax_code	=position;
						taxCode=Customer_taxCode_List.get(position).getTaxCode();
						taxtype = Customer_taxCode_List.get(position).getTaxName();
						String taxPerc = Customer_taxCode_List.get(position).getTaxPerc();
						tran_type = Customer_taxCode_List.get(position).getTaxType();
						Log.d("taxCodeCheck",taxCode+"taxType :"+taxtype);
						tax_code.setText(taxPerc);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

//		tax_desc.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(tax_desc) {
//			@Override
//			public boolean onDrawableClick() {
//				alertTaxDatas();
//				return true;
//			}
//		});


		expense_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int slno;
				String code="", desc="", amount="",taxPerc = "";
				double subTot=0.00,tax=0.00,NetTot=0.00,totTax,amt,taxvalue;
				code = exp_code.getText().toString();
				desc = exp_desc.getText().toString();
				amount = exp_amt.getText().toString();
//				taxtype = tax_desc.getText().toString();
				Log.d("taxType",tran_type);
				if(taxtype.matches("Select")){
					taxPerc = "0.00";
				}else{
					taxPerc = tax_code.getText().toString();
				}
				String supplierName = supplier.getText().toString();
				String payTo_value = payTo.getText().toString();

				if((payTo_value != null && !payTo_value.matches(""))|| ((supplierName != null && !supplierName.matches("")))) {

					if (amount != null && !amount.matches("")) {

						amt = Double.parseDouble(amount);
						taxvalue = Double.parseDouble(taxPerc);
						if (tran_type.matches("I")) {
							tax = (amt / (100 + taxvalue)) * taxvalue;
							subTot = amt - tax;
							NetTot = amt;
						} else if (tran_type.matches("E")) {
							tax = (amt / 100) * taxvalue;
							NetTot = amt + tax;
							subTot = amt;
						} else if (tran_type.matches("Z")) {
							NetTot = amt;
							tax = 0.00;
							subTot = amt;

						}

						Log.d("TaxCredentials", "," + twoDecimalPoint(tax) + "," + twoDecimalPoint(subTot) + "," + twoDecimalPoint(NetTot));

						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(exp_desc.getWindowToken(), 0);

						if (desc != null && !desc.isEmpty()) {
							if (amount != null && !amount.isEmpty()) {
								if(!taxtype.matches("Select")) {
									Cursor mCursor = SOTDatabase.getExpenseCursor();
									Log.d("mCursor", "" + mCursor.getCount());
									int count = mCursor.getCount();
									slno = count + 1;
									HashMap<String, String> hmvalues = new HashMap<String, String>();
									hmvalues.put("SlNo", slno + "");
									hmvalues.put("AccountNo", code);
									hmvalues.put("Description", desc);
									hmvalues.put("Amount", amount);
									hmvalues.put("tax_type", tran_type);
									hmvalues.put("tax_value", taxPerc);
									hmvalues.put("subTotal", twoDecimalPoint(subTot));
									hmvalues.put("tax", twoDecimalPoint(tax));
									hmvalues.put("netTotal", twoDecimalPoint(NetTot));
									hmvalues.put("tax_name", taxtype);
									hmvalues.put("tax_code", taxCode);

									SOTDatabase.storeExpanse(hmvalues);

									sl_total.setText(SOTDatabase.getExpenseTotalSubTot());
									sl_tax.setText(SOTDatabase.getExpenseTotalTax());
									sl_netTotal.setText(SOTDatabase.getExpenseTotalNetTot());

									exp_code.setText("");
									exp_desc.setText("");
									exp_amt.setText("");
//								tax_desc.setText("");
									payTo.setText("");

									expenseAdapter.notifyDataSetChanged();
								}else{
									Toast.makeText(ExpenseAdd.this, "Select taxtype", Toast.LENGTH_SHORT).show();
								}
							} else {
								Toast.makeText(ExpenseAdd.this, "Enter amount", Toast.LENGTH_SHORT).show();
								exp_amt.requestFocus();
							}

						} else {
							Toast.makeText(ExpenseAdd.this, "Select expense", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(ExpenseAdd.this, "Please Enter Amt", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(ExpenseAdd.this, "Please select Supplier or PayTo", Toast.LENGTH_SHORT).show();
				}
				CustomerSetGet();
			}
		});
		
		expensemaster_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
				HashMap<String, String> datavalue = expenselist.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String mkey = (String) mapEntry.getKey();
					String mValues = (String) mapEntry.getValue();
					mapEntry.getValue();
					exp_code.setText(mkey);
					exp_desc.setText(mValues);
					exp_amt.requestFocus();
					String taxName = SOTDatabase.getExpenseTaxName(mkey);

					Log.d("taxNameCheck",taxName);

					for (int i = 0; i < Customer_taxCode_List.size(); i++) {
						if (Customer_taxCode_List.get(i).getTaxName().matches(taxName)) {
							Log.d("taxcodecheck",Customer_taxCode_List.get(i).getTaxName());
							select_pos_tax_code = i;
						}
					}
					tax_desc.setSelection(select_pos_tax_code);


//					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.showSoftInputFromInputMethod(exp_amt.getWindowToken(), 0);
					
					((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
			        .showSoftInput(exp_amt, 2);
				}
			}
		});
		
		/**--  OnClick End   --**/
		
	}

	private void inDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		exp_date.setText(sdf.format(myCalendar.getTime()));

	}

	private void CustomerSetGet() {
		SalesOrderSetGet.setDeliverydate(exp_date.getText().toString());
		SalesOrderSetGet.setSuppliercode(sup_code.getText().toString());
		SalesOrderSetGet.setSuppliergroupcode(supplier.getText().toString());
		SalesOrderSetGet.setPayTo(payTo.getText().toString());
	}


	private void alertTaxDatas() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(ExpenseAdd.this);
		final EditText editText = new EditText(ExpenseAdd.this);
		final ListView listview = new ListView(ExpenseAdd.this);
		LinearLayout layout = new LinearLayout(ExpenseAdd.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Tax");
		editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,0, 0);
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

		customAdapter = new CustomAlertAdapterSupp(ExpenseAdd.this, taxAl);
		listview.setAdapter(customAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {

				myalertDialog.dismiss();
				getExpenseArraylsit = customAdapter.getArrayList();
				HashMap<String, String> datavalue = getExpenseArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String mkey = (String) mapEntry.getKey();
					String mValues = (String) mapEntry.getValue();
					mapEntry.getValue();
					Log.d("valuesCheck",mkey+","+mValues);
					tax_code.setText(mkey);
//					tax_desc.setText(mValues);
					taxCode = SOTDatabase.getTaxCode(mValues);
					Log.d("TaxcodeCheck",taxCode);
//					exp_amt.requestFocus();

//					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.showSoftInputFromInputMethod(exp_amt.getWindowToken(), 0);

					((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
							.showSoftInput(exp_amt, 2);

//					exp_desc.addTextChangedListener(new TextWatcher() {
//						@Override
//						public void afterTextChanged(Editable s) {
//
//						}
//
//						@Override
//						public void beforeTextChanged(CharSequence s,
//								int start, int count, int after) {
//						}
//
//						@Override
//						public void onTextChanged(CharSequence s, int start,
//								int before, int count) {
//							textlength = exp_desc.getText().length();
//						}
//					});
				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(taxAl);
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
				for (int i = 0; i < Customer_taxCode_List.size(); i++) {
					String code = Customer_taxCode_List.get(i).getTaxCode();
					String name = Customer_taxCode_List.get(i).getTaxName();
					String perc = Customer_taxCode_List.get(i).getTaxPerc();
					String type = Customer_taxCode_List.get(i).getTaxType();
					if (textlength <= code.length()) {
						if (code.toLowerCase().contains(editText.getText().toString().toLowerCase().trim()))
							searchResults.add(taxAl.get(i));
					}
					if(textlength<=name.length()) {
						if (name.toLowerCase().contains(editText.getText().toString().toLowerCase().trim()))
							searchResults.add(taxAl.get(i));
					}
					if(textlength<=type.length()) {
						if (type.toLowerCase().contains(editText.getText().toString().toLowerCase().trim()))
							searchResults.add(taxAl.get(i));
					}
				}

				customAdapter = new CustomAlertAdapterSupp(ExpenseAdd.this, searchResults);
				listview.setAdapter(customAdapter);
			}
		});
		myDialog.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		myalertDialog = myDialog.show();
	}

	private void alertDialogSupplierSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(ExpenseAdd.this);
		final EditText editText = new EditText(ExpenseAdd.this);
		final ListView listview = new ListView(ExpenseAdd.this);
		LinearLayout layout = new LinearLayout(ExpenseAdd.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Supplier");
		editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,0, 0);
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

		customAdapter = new CustomAlertAdapterSupp(ExpenseAdd.this, al);
		listview.setAdapter(customAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {

				myalertDialog.dismiss();
				getExpenseArraylsit = customAdapter.getArrayList();
				HashMap<String, String> datavalue = getExpenseArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String mkey = (String) mapEntry.getKey();
					String mValues = (String) mapEntry.getValue();
					mapEntry.getValue();
					Log.d("valuesCheck",mValues);
					sup_code.setText(mkey);
					supplier.setText(mValues);
//					exp_amt.requestFocus();

//					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.showSoftInputFromInputMethod(exp_amt.getWindowToken(), 0);

					((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
							.showSoftInput(exp_amt, 2);

//					exp_desc.addTextChangedListener(new TextWatcher() {
//						@Override
//						public void afterTextChanged(Editable s) {
//
//						}
//
//						@Override
//						public void beforeTextChanged(CharSequence s,
//								int start, int count, int after) {
//						}
//
//						@Override
//						public void onTextChanged(CharSequence s, int start,
//								int before, int count) {
//							textlength = exp_desc.getText().length();
//						}
//					});
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

				customAdapter = new CustomAlertAdapterSupp(ExpenseAdd.this, searchResults);
				listview.setAdapter(customAdapter);
			}
		});
		myDialog.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		myalertDialog = myDialog.show();
	}

	/** AsyncTask Strat**/
	
	public class ExpenseAsyncCall extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			expenselist.clear();
			expensemaster_jsonString ="";
			expensemaster_jsonResponse = null;
			expensemaster_jsonNode = null;
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			
			HashMap<String, String> hmvalue = new HashMap<String, String>();
			hmvalue.put("CompanyCode", cmpnyCode);
			hmvalue.put("AccountNo", "");
			
			expensemaster_jsonString = WebServiceClass.parameterService(hmvalue, "fncGetExpenseMaster");
			serverdate = DateWebservice.getDateService("fncGetServerDate");
			
			try {
				expensemaster_jsonResponse = new JSONObject(expensemaster_jsonString);
				expensemaster_jsonNode = expensemaster_jsonResponse.optJSONArray("JsonArray");
				int lengthJsonArr = expensemaster_jsonNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = expensemaster_jsonNode.getJSONObject(i);
					String code = jsonChildNode.optString("Code").toString();
					String name = jsonChildNode.optString("Description").toString();

					HashMap<String, String> customerhm = new HashMap<String, String>();
					customerhm.put(code ,name);
					expenselist.add(customerhm);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d("expenselist", "->"+expenselist.toString());
			gettingSetGet();
			if (serverdate != null) {
				if (SalesOrderSetGet.getExpense_dateChange().matches("0")) {
					exp_date.setText(serverdate);
				}
				if(expenselist.size()>0){
					expenseAdapter = new ExpenseMasterAdapter(ExpenseAdd.this, expenselist);
					expensemaster_list.setAdapter(expenseAdapter);
				}
			}

			AsyncCallWSSaleOrder inAcws = new AsyncCallWSSaleOrder();
			inAcws.execute();
		}
	}

	private void gettingSetGet() {
		if (SalesOrderSetGet.getDeliverydate().equals("")) {
			exp_date.setText(serverdate);
		} else {
			exp_date.setText(SalesOrderSetGet.getDeliverydate());
		}
		supplier.setText(SalesOrderSetGet.getSuppliergroupcode());
		payTo.setText(SalesOrderSetGet.getPayTo());
		sup_code.setText(SalesOrderSetGet.getSuppliercode());
	}

	/** AsyncTask End**/
	
	/** Adpater Start **/
	
	public class ExpenseMasterAdapter extends BaseAdapter {

		Context ctx;
		String datavalue;
		ArrayList<HashMap<String, String>> listarray = new ArrayList<HashMap<String, String>>();
		private LayoutInflater mInflater = null;
		String db_AcctNo ="";

		public ExpenseMasterAdapter(Activity activty, ArrayList<HashMap<String, String>> al) {
			this.listarray.clear();
			this.ctx = activty;
			mInflater = activty.getLayoutInflater();
			this.listarray = al;
		}

		@Override
		public int getCount() {
			return listarray.size();
		}

		@Override
		public Object getItem(int position) {
			return listarray.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			View convertView = view;
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.expense_list_item, null);
				holder.titlecode = (TextView) convertView.findViewById(R.id.item1);
				holder.titlename = (TextView) convertView.findViewById(R.id.item2);
				holder.titlevalue = (TextView) convertView.findViewById(R.id.item3);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			HashMap<String, String> datavalue = listarray.get(position);
			Set<Entry<String, String>> keys = datavalue.entrySet();
			Iterator<Entry<String, String>> iterator = keys.iterator();
			while (iterator.hasNext()) {
				@SuppressWarnings("rawtypes")
				Entry mapEntry = iterator.next();
				String keyValue = (String) mapEntry.getKey();
				String value = (String) mapEntry.getValue();
				holder.titlecode.setText(keyValue);
				holder.titlename.setText(value);
				db_AcctNo = SOTDatabase.getAccountNo(keyValue);
			}
			
			if (position % 2 == 0) {
				
				if(db_AcctNo != null && !db_AcctNo.isEmpty()){
					double amt = SOTDatabase.getExpenseAmt(db_AcctNo);
					Log.d("Adapteramount", "->"+amt);
					holder.titlevalue.setText(amt+"");
					convertView.setBackgroundResource(drawable.list_item_selected_bg);
				}else{
					convertView.setBackgroundResource(drawable.list_item_even_bg);
					holder.titlevalue.setText("0");
				}

				holder.titlecode.setTextColor(Color.parseColor("#035994"));
				holder.titlecode.setTextColor(Color.parseColor("#035994"));
				holder.titlecode.setTextColor(Color.parseColor("#035994"));

			} else {
				
				if(db_AcctNo != null && !db_AcctNo.isEmpty()){
					double amt = SOTDatabase.getExpenseAmt(db_AcctNo);
					Log.d("Adapteramount", "->"+amt);
					holder.titlevalue.setText(amt+"");
					convertView.setBackgroundResource(drawable.list_item_selected_bg);
				}else{
					convertView.setBackgroundResource(drawable.list_item_odd_bg);
					holder.titlevalue.setText("0");
				}

				holder.titlecode.setTextColor(Color.parseColor("#646464"));
				holder.titlename.setTextColor(Color.parseColor("#646464"));
				holder.titlevalue.setTextColor(Color.parseColor("#035994"));
			}
			
			
			return convertView;
		}

		class ViewHolder {
			TextView titlecode;
			TextView titlename;
			TextView titlevalue;

		}
	}
		
		/** Adpater End **/
	
	public void alertDialogExpenseSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(ExpenseAdd.this);
		final EditText editText = new EditText(ExpenseAdd.this);
		final ListView listview = new ListView(ExpenseAdd.this);
		LinearLayout layout = new LinearLayout(ExpenseAdd.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Expense");
		editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,0, 0);
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

		customAdapter = new CustomAlertAdapterSupp(ExpenseAdd.this, expenselist);
		listview.setAdapter(customAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				myalertDialog.dismiss();
				getExpenseArraylsit = customAdapter.getArrayList();
				HashMap<String, String> datavalue = getExpenseArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String mkey = (String) mapEntry.getKey();
					String mValues = (String) mapEntry.getValue();
					mapEntry.getValue();
					exp_code.setText(mkey);
					exp_desc.setText(mValues);
					exp_amt.requestFocus();
					
//					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.showSoftInputFromInputMethod(exp_amt.getWindowToken(), 0);
					
					((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
			        .showSoftInput(exp_amt, 2);
					
//					exp_desc.addTextChangedListener(new TextWatcher() {
//						@Override
//						public void afterTextChanged(Editable s) {
//
//						}
//
//						@Override
//						public void beforeTextChanged(CharSequence s,
//								int start, int count, int after) {
//						}
//
//						@Override
//						public void onTextChanged(CharSequence s, int start,
//								int before, int count) {
//							textlength = exp_desc.getText().length();
//						}
//					});
				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(expenselist);
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
				for (int i = 0; i < expenselist.size(); i++) {
					String supplierName = expenselist.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(expenselist.get(i));
					}
				}

				customAdapter = new CustomAlertAdapterSupp(ExpenseAdd.this, searchResults);
				listview.setAdapter(customAdapter);
			}
		});
			myDialog.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
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
			alertDialog.setMessage("Expense will clear. Do you want to proceed");
			alertDialog.setIcon(R.mipmap.ic_exit);
			alertDialog.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent i = new Intent(ExpenseAdd.this, ExpenseHeader.class);
							startActivity(i);
							ExpenseAdd.this.finish();
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
	
	
	public void loadprogress(){
		spinnerLayout = new LinearLayout(ExpenseAdd.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(expenseadd_layout, false);
		progressBar = new ProgressBar(ExpenseAdd.this);
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

	@Override
	public void onBackPressed() {	
		
		Cursor cursor = SOTDatabase.getExpenseCursor();
		int count = cursor.getCount();
		if(count>0){
			alertDialog();
		}else{
			 Intent i = new Intent(ExpenseAdd.this, ExpenseHeader.class);
			 startActivity(i);
			 ExpenseAdd.this.finish();
		}
	}

	private class AsyncCallWSSaleOrder extends AsyncTask<Void, Void, Void>{

		@Override
		protected void onPreExecute() {
			al.clear();
//			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... voids) {
//			serverdate = DateWebservice.getDateService("fncGetServerDate");
//			currencycode = SalesOrderWebService
//					.getAllCurrency("fncGetCurrency");

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
//						hashmap.putAll(customerhm);
//						aldelivery.add(customercode);

					}

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
		protected void onPostExecute(Void aVoid) {

			AsyncTax asyncTax = new AsyncTax();
			asyncTax.execute();

		}
	}

	private class AsyncTax extends AsyncTask<Void, Void, Void>{
		int slno=0;
		@Override
		protected void onPreExecute() {
			Customer_taxCode_List.clear();
			taxcode_jsonString="";
//			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... voids) {
//			serverdate = DateWebservice.getDateService("fncGetServerDate");
//			currencycode = SalesOrderWebService
//					.getAllCurrency("fncGetCurrency");

//			SoapObject request = new SoapObject(NAMESPACE, webMethNames);
//			PropertyInfo companyCode = new PropertyInfo();
//
//			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
//
//			companyCode.setName("CompanyCode");
//			companyCode.setValue(cmpnyCode);
//			companyCode.setType(String.class);
//			request.addProperty(companyCode);
//
//			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
//					SoapEnvelope.VER11);
//			envelope.dotNet = true;
//			envelope.bodyOut = request;
//			envelope.setOutputSoapObject(request);
//			HttpTransportSE androidHttpTransport = new HttpTransportSE(
//					valid_url);
//			try {
//				androidHttpTransport.call(SOAP_ACTION + webMethNames, envelope);
//				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
//				suppTxt = response.toString();
//				cusResult = " { Tax : " + suppTxt + "}";
//				JSONObject jsonResponse;
//				try {
//					jsonResponse = new JSONObject(cusResult);
//					JSONArray jsonMainNode = jsonResponse
//							.optJSONArray("Tax");
//					int lengthJsonArr = jsonMainNode.length();
//					for (int i = 0; i < lengthJsonArr; i++) {
//						JSONObject jsonChildNode = jsonMainNode
//								.getJSONObject(i);
//
//						String taxCode = jsonChildNode.optString(
//								"TaxCode").toString();
//						String taxName = jsonChildNode.optString(
//								"TaxName").toString();
//						String taxPerc = jsonChildNode.optString(
//								"TaxPercentage").toString();
//						String taxType = jsonChildNode.optString(
//								"TaxType").toString();
//						HashMap<String, String> customerhm = new HashMap<String, String>();
//						customerhm.put(taxPerc, taxName);
//						taxAl.add(customerhm);
//////						hashmap.putAll(customerhm);
//////						aldelivery.add(customercode);
//
//						Cursor mCursor = SOTDatabase.getTaxCursor();
//						Log.d("mCursor", "" + mCursor.getCount());
//						int count = mCursor.getCount();
//						slno = count + 1;
//						HashMap<String, String> hmvalues = new HashMap<String, String>();
//						hmvalues.put("SlNo", slno + "");
//						hmvalues.put("taxcode", taxCode);
//						hmvalues.put("taxname", taxName);
//						hmvalues.put("taxperc", taxPerc);
//						hmvalues.put("tax_type", taxType);
//
//
//						SOTDatabase.storeTax(hmvalues);
//
//						TaxData taxData = new TaxData();
//						taxData.setTaxCode(taxCode);
//						taxData.setTaxName(taxName);
//						taxData.setTaxPerc(taxPerc);
//						taxData.setTaxType(taxType);
//						Customer_taxCode_List.add(taxData);
//
//					}
//
//				} catch (JSONException e) {
//
//					e.printStackTrace();
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//				suppTxt = "Error occured";
//			}

			taxcode_jsonString =WebServiceClass
					.URLService("fncGetTax");
			try {
				taxcode_jsonResponse= new JSONObject(taxcode_jsonString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			taxcode_jsonMainNode =taxcode_jsonResponse
					.optJSONArray("JsonArray");

			TaxData taxData=new TaxData();
			taxData.setTaxName("Select");
			taxData.setTaxCode("Select");
			taxData.setTaxPerc("0.00");
			taxData.setTaxType("S");
			Customer_taxCode_List.add(taxData);

			int lengthJsonArr5 = taxcode_jsonMainNode.length();
			Log.d("lengthJsonArr5",""+lengthJsonArr5);
			for (int i = 0; i < lengthJsonArr5; i++) {
				/****** Get Object for each JSON node. ***********/
				JSONObject taxcode_jsonChildNode;
				try {

					taxcode_jsonChildNode = taxcode_jsonMainNode
							.getJSONObject(i);

					TaxData taxDatas=new TaxData();

					String tax_Code = taxcode_jsonChildNode.optString(
							"TaxCode").toString();
					String tax_Name =taxcode_jsonChildNode.optString("TaxName").toString();
					String tax_perc = taxcode_jsonChildNode.optString("TaxPercentage").toString();
					String tax_type = taxcode_jsonChildNode.optString("TaxType").toString();

					Log.d("taxtype","-->"+tax_type);

					taxDatas.setTaxCode(tax_Code);
					taxDatas.setTaxName(tax_Name);
					taxDatas.setTaxPerc(tax_perc);
					taxDatas.setTaxType(tax_type);

					Customer_taxCode_List.add(taxDatas);


				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			Log.d("Customer_taxCode_List",""+Customer_taxCode_List.size());

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			Log.d("Customer_taxCode_List123","-->"+Customer_taxCode_List.size());
			if (Customer_taxCode_List.size() != 0) {
				tax_desc.setAdapter(new CustomAdapter(ExpenseAdd.this,
								R.layout.row, Customer_taxCode_List));
			}

			for (int i = 0; i < Customer_taxCode_List.size(); i++) {
				Log.d("getTaxType","-->"+Customer_taxCode_List.get(i).getTaxType());
				if (Customer_taxCode_List.get(i).getTaxType().matches("Z")) {
					Log.d("taxcodecheck",Customer_taxCode_List.get(i).getTaxName());
					select_pos_tax_code = i;
				}
			}
			tax_desc.setSelection(select_pos_tax_code);

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(expenseadd_layout, true);
		}
	}

	public class CustomAdapter extends ArrayAdapter<TaxData> {

		ArrayList<TaxData> adapterList = new ArrayList<TaxData>();

		public CustomAdapter(Context context, int textViewResourceId,
							 ArrayList<TaxData> objects) {
			super(context,textViewResourceId,objects);
			this.adapterList.clear();
			this.adapterList = objects;

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
			TaxData taxData = adapterList.get(position);
			TextView label = (TextView) row.findViewById(R.id.locationspinner);
			ImageView icon = (ImageView) row.findViewById(R.id.spinnericon);
			Log.d("getTaxnameDta",""+taxData.getTaxName());
			label.setText(taxData.getTaxName());
			icon.setVisibility(View.GONE);
			return row;
		}
	}

	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}
}
