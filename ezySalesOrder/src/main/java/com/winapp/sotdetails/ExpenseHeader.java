package com.winapp.sotdetails;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.DateTime;
import com.winapp.helper.XMLParser;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.printcube.printcube.BluetoothService;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.printer.Printer;
import com.winapp.printer.PrinterZPL;
import com.winapp.printer.UIHelper;
import com.winapp.sot.ConvertToSetterGetter;
import com.winapp.sot.ExpenseAdapter;
import com.winapp.sot.HeaderAdapter;
import com.winapp.sot.OfflineSalesOrderWebService;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.RowItem;
import com.winapp.sot.SO;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderHeader;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.sot.WebServiceClass;
import com.winapp.util.XMLAccessTask;

public class ExpenseHeader extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace {
	SlidingMenu menu;
	ProgressBar progressBar;
	LinearLayout spinnerLayout, expense_parent, searchlayout, codelayout;
	EditText usercodefield, starteditTextDate, endeditTextDate;
	Button btsearch;
	ListView expense_list;
	ImageButton searchIcon, addIcon;
	
	ArrayList<SO> exapnselistArray = new ArrayList<SO>();
	ArrayList<SO> searchlistArray = new ArrayList<SO>();
	ArrayList<HashMap<String, String>> getUserArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> userlist = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> searchResults;
	
	private UIHelper helper;
	ExpenseAdapter expenseAdapter;
	CustomAlertAdapterSupp userAdapter;
	
	Calendar startCalendar, endCalendar;
	int textlength = 0;
	String expnseno="", expnsdate="", expnsamount="", expnremark="", serverdate = "", loginUsername = "", valid_url="", usercode="", sDate="", eDate="";
	private AlertDialog myalertDialog = null;
	static ArrayList<HashMap<String, String>> eoDetailsArr = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> eoHeadersArr = new ArrayList<HashMap<String, String>>();
	int nofcopies = 1;
	ArrayList<ProductDetails> product = new ArrayList<ProductDetails>();
	String date, macaddress ,  title;

	LinearLayout offlineLayout;
	private OfflineDatabase offlineDatabase;
	boolean checkOffline;
	String onlineMode, offlineDialogStatus;
	private OfflineCommon offlineCommon;
	OfflineSettingsManager spManager;
	String getSignatureimage= "",getPhotoimage,mobileHaveOfflineMode;
	private HashMap<String, String> params;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.expense_header);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Expense");
		searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
		addIcon = (ImageButton) customNav.findViewById(R.id.custcode_img);

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
		
		FWMSSettingsDatabase.init(ExpenseHeader.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,ExpenseHeader.this);
		new SalesOrderWebService(valid_url);
		new WebServiceClass(valid_url);
		SOTDatabase.init(ExpenseHeader.this);

		SalesOrderSetGet.setDeliverydate("");
		SalesOrderSetGet.setSuppliercode("");
		SalesOrderSetGet.setSuppliergroupcode("");
		SalesOrderSetGet.setPayTo("");

		GlobalData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		GlobalData.mService = new BluetoothService(this, mHandler);

		helper = new UIHelper(ExpenseHeader.this);
		onlineMode = OfflineDatabase.getOnlineMode();
		offlineDatabase = new OfflineDatabase(ExpenseHeader.this);
		offlineCommon = new OfflineCommon(ExpenseHeader.this);
		checkOffline = OfflineCommon.isConnected(ExpenseHeader.this);
		OfflineDatabase.init(ExpenseHeader.this);
		offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);
		expense_parent = (LinearLayout) findViewById(R.id.expense_parent);
		searchlayout = (LinearLayout) findViewById(R.id.searchlayout);
		codelayout = (LinearLayout) findViewById(R.id.codelayout);
		usercodefield = (EditText) findViewById(R.id.usercodefield);
		starteditTextDate = (EditText) findViewById(R.id.starteditTextDate);
		endeditTextDate = (EditText) findViewById(R.id.endeditTextDate);		
		btsearch = (Button) findViewById(R.id.btsearch);
		expense_list = (ListView) findViewById(R.id.expense_list);
		
		startCalendar = Calendar.getInstance();
		endCalendar = Calendar.getInstance();
		
		loginUsername = SupplierSetterGetter.getUsername();
		usercodefield.setText(loginUsername);

		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
//		expenseAll=FormSetterGetter.isExpenseAll();
//		
//		if(expenseAll==true){
//			usercodefield.setFocusableInTouchMode(true);
//			usercodefield.setBackgroundResource(R.drawable.edittext_bg);
//			usercodefield.setCursorVisible(true);
//		}
		
		UserAsyncCall userAsync = new UserAsyncCall();
		userAsync.execute();
		
		/**--  OnClick Start   --**/
		
		searchIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
//				InputMethodManager inputMethodManager = (InputMethodManager) ExpenseHeader.this
//						.getSystemService(Context.INPUT_METHOD_SERVICE);

				if (searchlayout.getVisibility() == View.VISIBLE) {
					searchlayout.setVisibility(View.GONE);
//					inputMethodManager.hideSoftInputFromWindow(codelayout.getWindowToken(), 0);
				} else {
					searchlayout.setVisibility(View.VISIBLE);
					usercodefield.requestFocus();
//					inputMethodManager.toggleSoftInputFromWindow(
//							codelayout.getApplicationWindowToken(),
//							InputMethodManager.SHOW_FORCED, 0);
				}
		
			}
		});
		
		
		addIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SOTDatabase.deleteallexpense();
				SOTDatabase.deleteImage();
				SalesOrderSetGet.setRemarks("");
				Intent i = new Intent(ExpenseHeader.this,ExpenseAdd.class);
				startActivity(i);
				ExpenseHeader.this.finish();
			}
		});
		
		usercodefield.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
				usercodefield) {
			@Override
			public boolean onDrawableClick() {
//				alertDialogUserSearch();
				return true;
			}
		});
		
		btsearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				usercode=""; 
				sDate=""; 
				eDate="";
				usercode = usercodefield.getText().toString();
				sDate = starteditTextDate.getText().toString();
				eDate = endeditTextDate.getText().toString();

				if (sDate.matches("")) {
					Toast.makeText(ExpenseHeader.this, "Enter Start Date",
							Toast.LENGTH_SHORT).show();
				} else if (eDate.matches("")) {
					Toast.makeText(ExpenseHeader.this, "Enter End Date",
							Toast.LENGTH_SHORT).show();
				} else {
					
					loadprogress();
					
					ExpenseSearchAsyncCall expense = new ExpenseSearchAsyncCall();
					expense.execute();
				}
			}
		});
		
		
		
		
		final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
		
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				startCalendar.set(Calendar.YEAR, year);
				startCalendar.set(Calendar.MONTH, monthOfYear);
				startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				strtDate();
			}
		};
		
		final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {
		
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				endCalendar.set(Calendar.YEAR, year);
				endCalendar.set(Calendar.MONTH, monthOfYear);
				endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				edDate();
			}
		};
		
		starteditTextDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(ExpenseHeader.this, startDate,
							startCalendar.get(Calendar.YEAR), startCalendar
									.get(Calendar.MONTH), startCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		
		endeditTextDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(ExpenseHeader.this, endDate,
							endCalendar.get(Calendar.YEAR), endCalendar
									.get(Calendar.MONTH), endCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		
		expense_list.setOnItemClickListener(new OnItemClickListener()
	     {
	         @Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	         {
//	        	 System.out.println("...on single click context menu...");
//	               view.showContextMenu();
	         }
	     });
		
		registerForContextMenu(expense_list);
		
		/**--  OnClick End   --**/
		
	}
	
	
	public void loadprogress(){
		spinnerLayout = new LinearLayout(ExpenseHeader.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(expense_parent, false);
		progressBar = new ProgressBar(ExpenseHeader.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		spinnerLayout.addView(progressBar);
	}

	@Override
	 public void onCreateContextMenu(ContextMenu menu, View v,
	   ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);

	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
	  SO so = expenseAdapter.getItem(info.position);
	  expnseno = so.getSno().toString();
	  expnsdate = so.getDate().toString();
	  expnsamount = so.getNettotal().toString();
	  expnremark= so.getStatus().toString();
	  menu.setHeaderTitle(expnseno);
        menu.add(0, v.getId(), 0, "Edit");
//	  menu.add(0, v.getId(), 0, "Delete");
//	  menu.add(0, v.getId(), 0, "Reprint");

	 }

	 @Override
	 public boolean onContextItemSelected(android.view.MenuItem item) {

	  AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo) item.getMenuInfo();
	  SO so = expenseAdapter.getItem(adapterInfo.position);
	  expnseno = so.getSno().toString();
	   
//	  ConvertToSetterGetter.setDoNo("");
//	  ConvertToSetterGetter.setSoNo("");
//	   ConvertToSetterGetter.setEdit_do_no("");

	   if(item.getTitle() == "Edit"){

//		   new AsyncCallWSGetSignature(false,"").execute();
		   Log.d("expenseNumber",expnseno);
		   AsyncCallWSExpenseDetail task = new AsyncCallWSExpenseDetail();
		   task.execute();
	   }
	   
	  if(item.getTitle() == "Delete"){
	   deleteAlertDialog();
	  }else if(item.getTitle() == "Reprint"){
		  SOTDatabase.deleteImage();
		  printAlertDialog();
//	   Toast.makeText(ExpenseHeader.this, " "+expnseno, Toast.LENGTH_SHORT).show();
	  }
	  
	  return true;
	 }

	
	public void alertDialogUserSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				ExpenseHeader.this);
		final EditText editText = new EditText(ExpenseHeader.this);
		final ListView listview = new ListView(ExpenseHeader.this);
		LinearLayout layout = new LinearLayout(ExpenseHeader.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("User");
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

		userAdapter = new CustomAlertAdapterSupp(ExpenseHeader.this, userlist);
		listview.setAdapter(userAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				myalertDialog.dismiss();
				getUserArraylsit = userAdapter.getArrayList();
				HashMap<String, String> datavalue = getUserArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String keyValues = (String) mapEntry.getKey();
					mapEntry.getValue();
					usercodefield.setText(keyValues);
					usercodefield.addTextChangedListener(new TextWatcher() {
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
							textlength = usercodefield.getText().length();
						}
					});
				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(userlist);
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
				for (int i = 0; i < userlist.size(); i++) {
					String supplierName = userlist.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(userlist.get(i));
					}
				}

				userAdapter = new CustomAlertAdapterSupp(ExpenseHeader.this, searchResults);
				listview.setAdapter(userAdapter);
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
	
	private void strtDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		starteditTextDate.setText(sdf.format(startCalendar.getTime()));
	}

	private void edDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		endeditTextDate.setText(sdf.format(endCalendar.getTime()));
	}

	public void showViews(boolean show, int... resId) {
		int visibility = show ? View.VISIBLE : View.GONE;
		for (int id : resId) {
			findViewById(id).setVisibility(visibility);
		}

	}


	/** AsyncTask Strat**/
	
	public class UserAsyncCall extends AsyncTask<Void, Void, Void> {
		String dialogstatus;
		@Override
		protected void onPreExecute() {
			dialogstatus = checkInternetStatus();
			userlist.clear();
			loadprogress();
			boolean check= isNetworkConnected();
			if(check == false){
				finish();
			}
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			if(onlineMode.matches("True")) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("Code", "W!napp@!@#^");

				userlist = SalesOrderWebService.getAllUser("fncGetUserMaster", params);
				serverdate = DateWebservice.getDateService("fncGetServerDate");

			}else{
				Toast.makeText(getApplicationContext(),"No Internet Connection!!",Toast.LENGTH_SHORT).show();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (serverdate != null) {
				starteditTextDate.setText(DateTime.date(serverdate));
				endeditTextDate.setText(serverdate);
				
				ExpenseAsyncCall expense = new ExpenseAsyncCall();
				expense.execute();
			}
		}
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
	}
	private class ExpenseAsyncCall extends AsyncTask<Void, Void, Void> {
		String expensejsonString = "",startdate="",enddate="";
		JSONObject expensejsonResponse = null;
		JSONArray expensejsonMainNode = null;
		String dialogStatus = "";
		@Override
		protected void onPreExecute() {
			dialogStatus = checkInternetStatus();
			exapnselistArray.clear();
			startdate = starteditTextDate.getText().toString();
			enddate = endeditTextDate.getText().toString();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			if(onlineMode.matches("True")) {
				try {

					String cmpnyCode = SupplierSetterGetter.getCompanyCode();

					HashMap<String, String> hmvalue = new HashMap<String, String>();
					hmvalue.put("CompanyCode", cmpnyCode);
					hmvalue.put("CreateUser", loginUsername);
					hmvalue.put("FromDate", "");
					hmvalue.put("ToDate", "");
					expensejsonString = WebServiceClass.parameterService(hmvalue, "fncGetAccVoucher");

					expensejsonResponse = new JSONObject(expensejsonString);
					expensejsonMainNode = expensejsonResponse.optJSONArray("JsonArray");

					int lengthJsonArr = expensejsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode;
						try {

							jsonChildNode = expensejsonMainNode.getJSONObject(i);
							String expenseno = jsonChildNode.optString("TranNo").toString();
							String date = jsonChildNode.optString("TranDate").toString();
							String remarks = jsonChildNode.optString("Remarks").toString();
							String expenseName = jsonChildNode.optString("GroupName").toString();
							String subTot = jsonChildNode.optString("SubTotal").toString();
							String tax = jsonChildNode.optString("Tax").toString();
							String netTotal = jsonChildNode.optString("NetTotal").toString();

							SO so = new SO();
							so.setSno(expenseno);
							if (date != null && !date.isEmpty()) {
								StringTokenizer tokens = new StringTokenizer(date, " ");
								String mdate = tokens.nextToken();
								so.setDate(mdate);
							} else {
								so.setDate(date);
							}

							if (remarks != null && !remarks.isEmpty()) {
								so.setStatus(remarks);
							} else {
								so.setStatus("-");
							}
							Log.d("exoenseName", expenseName);
							so.setCustomerName(expenseName);
							so.setSubTotal(subTot);
							so.setTax(tax);
							so.setNettotal(netTotal);
							exapnselistArray.add(so);

						} catch (JSONException e) {

							e.printStackTrace();
						}
						/******* Fetch node values **********/

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				Toast.makeText(getApplicationContext(),"No Internet Connection!!",Toast.LENGTH_SHORT).show();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("exapnselistArrayResult", exapnselistArray.toString());
			
			try {
				headerCustCode();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(expense_parent, true);
		}
	}
	
	private class ExpenseSearchAsyncCall extends AsyncTask<Void, Void, Void> {
		String expensejsonString = "";
		JSONObject expensejsonResponse = null;
		JSONArray expensejsonMainNode = null;
		@Override
		protected void onPreExecute() {
			searchlistArray.clear();		
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				
				String cmpnyCode = SupplierSetterGetter.getCompanyCode();
	
				HashMap<String, String> hmvalue = new HashMap<String, String>();
				hmvalue.put("CompanyCode", cmpnyCode);
				hmvalue.put("CreateUser", usercode);
				hmvalue.put("FromDate", sDate);
				hmvalue.put("ToDate", eDate);
				expensejsonString = WebServiceClass.parameterService(hmvalue, "fncGetAccVoucher");
			
				expensejsonResponse = new JSONObject(expensejsonString);
				expensejsonMainNode = expensejsonResponse.optJSONArray("JsonArray");

				int lengthJsonArr = expensejsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
	
					JSONObject jsonChildNode;
					try {
	
						jsonChildNode = expensejsonMainNode.getJSONObject(i);
						String expenseno = jsonChildNode.optString("TranNo").toString();
						String date = jsonChildNode.optString("TranDate").toString();
						String remarks = jsonChildNode.optString("Remarks").toString();
						String expenseName = jsonChildNode.optString("GroupName").toString();
						String subTot = jsonChildNode.optString("SubTotal").toString();
						String tax = jsonChildNode.optString("Tax").toString();
						String netTotal = jsonChildNode.optString("NetTotal").toString();

						SO so = new SO();
						so.setSno(expenseno);
						if (date != null && !date.isEmpty()) {
							StringTokenizer tokens = new StringTokenizer(date, " ");
							String mdate = tokens.nextToken();
							so.setDate(mdate);
						} else {
							so.setDate(date);
						}

						if (remarks != null && !remarks.isEmpty()) {
							so.setStatus(remarks);
						} else {
							so.setStatus("-");
						}
						so.setCustomerName(expenseName);
						so.setSubTotal(subTot);
						so.setTax(tax);
						so.setNettotal(netTotal);
						searchlistArray.add(so);
	
					} catch (JSONException e) {
	
						e.printStackTrace();
					}
					/******* Fetch node values **********/
	
				}
			
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("searchlistArray Result", searchlistArray.toString());
			
			if (!searchlistArray.isEmpty()) {
				try {
					searchCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				searchlistArray.clear();
				try {
					searchCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Toast.makeText(ExpenseHeader.this, "No matches found",
						Toast.LENGTH_SHORT).show();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(expense_parent, true);
		}
	}
	
	private class ExpenseDeleteAsyncCall extends AsyncTask<Void, Void, Void> {
		  String jsonString = "", deleteResult="";
		  JSONObject jsonResponse = null;
		  JSONArray jsonMainNode = null;
		  @Override
		  protected void onPreExecute() {
		   loadprogress(); 
		  }

		  @Override
		  protected Void doInBackground(Void... arg0) {
		            Log.d("expnseno-->", expnseno);
		   String cmpnyCode = SupplierSetterGetter.getCompanyCode();
		   HashMap<String, String> hashValue = new HashMap<String, String>();
		   hashValue.put("CompanyCode", cmpnyCode);
		   hashValue.put("ExpenseNo", expnseno);
		   hashValue.put("UserID", loginUsername);

		   jsonString = WebServiceClass.parameterService(hashValue,"fncDeleteExpense");

		   try {

		    jsonResponse = new JSONObject(jsonString);
		    jsonMainNode = jsonResponse.optJSONArray("JsonArray");

		    int lengthJsonArr = jsonMainNode.length();

		    for (int i = 0; i < lengthJsonArr; i++) {

		     JSONObject jsonChildNode;
		     jsonChildNode = jsonMainNode.getJSONObject(i);
		     deleteResult = jsonChildNode.optString("Result").toString();
		    }

		   } catch (JSONException e) {
		    deleteResult="";
		    e.printStackTrace();
		   } catch (Exception e) {
		    deleteResult="";
		    e.printStackTrace();
		   }
		   hashValue.clear();
		   return null;
		  }

		  @Override
		  protected void onPostExecute(Void result) {   
		   
		   if (!deleteResult.matches("")) {   

		    new ExpenseAsyncCall().execute();   
		    Toast.makeText(ExpenseHeader.this, "Delete Successfully", Toast.LENGTH_LONG).show();

		   } else {

		    progressBar.setVisibility(View.GONE);
		    spinnerLayout.setVisibility(View.GONE);
		    enableViews(expense_parent, true);   
		    Toast.makeText(ExpenseHeader.this, "Failed", Toast.LENGTH_LONG).show();
		    
		   }

		  }
		 }
		 
		 /** AsyncTask End**/
	
	
/** Print Start   **/
	
	private void print() throws IOException {
		String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
		title = "Expense";
//		int nofcopies = Integer.parseInt(stnumber.getText().toString());

		date = SalesOrderSetGet.getSaleorderdate();
		macaddress = FWMSSettingsDatabase.getPrinterAddress();
		product = SalesOrderWebService.getExpenseDetails(expnseno, "fncGetExpenseDetail");
		helper.dismissProgressDialog();
		try {

			if (printertype.matches("Zebra iMZ320")) {

				helper.dismissProgressDialog();
				Printer printer = new Printer(ExpenseHeader.this, macaddress);
				printer.printExpense(expnseno, date, product, title, nofcopies);
			} else if (printertype.matches("4 Inch Bluetooth")) {
                /*helper.showProgressDialog(InvoiceHeader.this.getString(R.string.print),
						InvoiceHeader.this.getString(R.string.creating_file_for_printing));*/
				helper.updateProgressDialog(ExpenseHeader.this.getString(R.string.creating_file_for_printing));
				if (BluetoothAdapter.checkBluetoothAddress(macaddress)) {
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device, true);

				}
				helper.dismissProgressDialog();
				//helper.dismissProgressDialog();
			}
			else if(printertype.matches("3 Inch Bluetooth Generic")) {
				helper.dismissProgressDialog();
				try {
					final CubePrint print = new CubePrint(ExpenseHeader.this, macaddress);
					print.initGenericPrinter();
					print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
						@Override
						public void initCompleted() {
							try {
								print.printExpense(expnseno, date, product, title, nofcopies);
							}catch (Exception e){
								e.printStackTrace();
							}
							print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
								@Override
								public void onCompleted() {
									helper.showLongToast(R.string.printed_successfully);
								}
							});
						}
					});

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(printertype.matches("Zebra iMZ320 4 Inch")){
				helper.dismissProgressDialog();
				PrinterZPL printer = new PrinterZPL(ExpenseHeader.this, macaddress);
				printer.printExpense(expnseno, date, product, title, nofcopies);
			}

		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}

	}
	
	/** Print End  **/
	
	public void headerCustCode() throws ParseException {
		if (!exapnselistArray.isEmpty()) {
			//expense_list
			expenseAdapter = new ExpenseAdapter(ExpenseHeader.this, R.layout.exoense_list_item, null, exapnselistArray);
			expense_list.setAdapter(expenseAdapter);
		}

	}
	
	public void searchCustCode() throws ParseException {

		if (ExpenseHeader.this != null) {
			expenseAdapter = new ExpenseAdapter(ExpenseHeader.this, R.layout.exoense_list_item, null, searchlistArray);
			expense_list.setAdapter(expenseAdapter);
		}
	}
	
	public void printAlertDialog() {
		  AlertDialog.Builder builder = new AlertDialog.Builder(
		    ExpenseHeader.this);
		  builder.setTitle("Confirm Print");
		  builder.setIcon(R.mipmap.ic_print_icon_pressed);
		  builder.setMessage("Do you want to print?");
		  builder.setCancelable(false);
		  builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		   @Override
		   public void onClick(DialogInterface dialog, int id) {
			   try {
					print();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  // Print
		    
		   }
		  });
		  builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
		   @Override
		   public void onClick(DialogInterface dialog, int id) {
		    dialog.cancel();
		   }
		  });
		  AlertDialog alertDialog = builder.create();
		  alertDialog.show();
		 }
	
	public void deleteAlertDialog() {
		  AlertDialog.Builder builder = new AlertDialog.Builder(
		    ExpenseHeader.this);
		  builder.setTitle("Confirm Delete");
		  builder.setIcon(R.mipmap.delete);
		  builder.setMessage("Do you want to delete?");
		  builder.setCancelable(false);
		  builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		   @Override
		   public void onClick(DialogInterface dialog, int id) {
		    
		     new ExpenseDeleteAsyncCall().execute();
		   }
		  });
		  builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
		   @Override
		   public void onClick(DialogInterface dialog, int id) {
		    dialog.cancel();
		   }
		  });
		  AlertDialog alertDialog = builder.create();
		  alertDialog.show();
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
	public void onStart() {
		super.onStart();
		//if(D) Log.e(TAG, "--- onStart ---");
		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

		if (printertype.matches("4 Inch Bluetooth")) {
			if (!GlobalData.mBluetoothAdapter.isEnabled()) {
				GlobalData.mBluetoothAdapter.enable();
				//Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				//startActivity(enableIntent);*/
				// Otherwise, setup the chat session
			} else {
				if (GlobalData.mService == null) {
					GlobalData.mService = new BluetoothService(this, mHandler);
				}
			}
		}
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case GlobalData.MESSAGE_STATE_CHANGE:
					Log.d("case", "MESSAGE_STATE_CHANGE");
					//if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
					switch (msg.arg1) {
						case GlobalData.STATE_CONNECTED:
							//mTitle.setText(R.string.title_connected_to);
							//mTitle.append(mConnectedDeviceName);
							//Intent intent = new Intent(BluetoothActivity.this,PrintActivity.class);
							//intent.putExtra("COMM", 0);//0-BLUETOOTH
							// Set result and finish this Activity
							//	startActivity(intent);
							Log.d("case", "STATE_CONNECTED");
							print4Inch();
							//helper.dismissProgressDialog();
							break;
						case GlobalData.STATE_CONNECTING:
							//mTitle.setText(R.string.title_connecting);
							Log.d("case", "STATE_CONNECTING");

							break;
						case GlobalData.STATE_LISTEN:
							Log.d("case", "STATE_LISTEN");
							break;
						case GlobalData.STATE_NONE:
							Log.d("case", "STATE_NONE");
							//mTitle.setText(R.string.title_not_connected);
							break;
					}
					break;
				case GlobalData.MESSAGE_DEVICE_NAME:
					// save the connected device's name
					String mConnectedDeviceName = msg.getData().getString("device_name");
					Toast.makeText(getApplicationContext(), "Connected to "
							+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
					break;
				case GlobalData.MESSAGE_TOAST:

					//String macaddress = FWMSSettingsDatabase.getPrinterAddress();
					Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
							Toast.LENGTH_SHORT).show();

					reconnectDialog(msg.getData().getString("toast"));
					/*if (BluetoothAdapter.checkBluetoothAddress(macaddress))
					{
						BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
						// Attempt to connect to the device
						GlobalData.mService.setHandler(mHandler);
						GlobalData.mService.connect(device,true);

						//print4Inch();
					}*/
					//	helper.dismissProgressDialog();
					break;
			}
			//helper.dismissProgressDialog();
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		if (GlobalData.mService != null) {
			GlobalData.mService.stop();
		}
	}

	public void reconnectDialog(String msg) {
		final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		final Dialog dialog = new Dialog(ExpenseHeader.this);

		dialog.setContentView(R.layout.dialog_reconnect);
		dialog.setTitle(msg);
		ImageView reconnect = (ImageView) dialog.findViewById(R.id.reconnect);

		reconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (BluetoothAdapter.checkBluetoothAddress(macaddress)) {
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device, true);

				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void print4Inch() {
		CubePrint mPrintCube = new CubePrint(ExpenseHeader.this, FWMSSettingsDatabase.getPrinterAddress());
		mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
			@Override
			public void onCompleted() {
				helper.showLongToast(R.string.printed_successfully);

			}
		});
		/*mPrintCube.printSalesReturn(sosno, sodate, socustomercode, socustomername,
				product, productdet, printsortHeader, null, 1,
				null, footerArr);*/

		try {
			mPrintCube.printExpense(expnseno, date, product, title, nofcopies);
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	@Override
	public void onBackPressed() {		
		 Intent i = new Intent(ExpenseHeader.this, LandingActivity.class);
		 startActivity(i);
		 ExpenseHeader.this.finish();
	}

	private class AsyncCallWSExpenseDetail extends AsyncTask<Void, Void, Void>{
		String dialogStatus = "";
		@Override
		protected void onPreExecute() {
			loadprogress();
			dialogStatus = checkInternetStatus();
			eoDetailsArr.clear();
			eoHeadersArr.clear();

		}

		@Override
		protected Void doInBackground(Void... voids) {

			if (onlineMode.matches("True")) {
				Log.d("detailNumber",expnseno);
				eoDetailsArr = SalesOrderWebService.getExpenseDetail(expnseno, "fncGetAccVoucherDetail");
				eoHeadersArr = SalesOrderWebService.getExpenseHeader(expnseno, "fncGetAccVoucherHeaderByTranNo");

			} else if (onlineMode.matches("False")) {  // permanent_offline

//				eoHeadersArr = offlineDatabase.getSOHeader(sosno);
//				eoDetailsArr = offlineDatabase.getSODetails(sosno);
//				OfflineSalesOrderWebService.getCustomerTaxOffline(cusCode);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			Intent i = new Intent(ExpenseHeader.this, ExpenseSummary.class);
			i.putExtra("SODetails", eoDetailsArr);
			i.putExtra("SOHeader", eoHeadersArr);
			i.putExtra("getSignatureimage", getSignatureimage);
			i.putExtra("getPhotoimage", getPhotoimage);
			startActivity(i);
			ExpenseHeader.this.finish();

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(expense_parent, true);
		}
	}

	private String checkInternetStatus() {
		String internetStatus = "";
		String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			if(mobileHaveOfflineMode.matches("1")){

				checkOffline = OfflineCommon.isConnected(ExpenseHeader.this);
//		String internetStatus = "";
				if (onlineMode.matches("True")) {
					if (checkOffline == true) {
						String Off_dialog = OfflineDatabase.getInternetMode("OfflineDialog");
						if (Off_dialog.matches("true")) {
							internetStatus = "true";
						} else {
							offlineCommon.OfflineAlertDialog();
							Boolean dialogStatus = offlineCommon.showDialog();
							OfflineDatabase.updateInternetMode("OfflineDialog",dialogStatus + "");
							Log.d("OfflineDialogStatus", "" + dialogStatus);
							internetStatus = "" + dialogStatus;
							Toast.makeText(getApplicationContext(),"No Internet Connection!",Toast.LENGTH_SHORT).show();
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
				}else{
					Toast.makeText(getApplicationContext(),"No Internet Connection!!",Toast.LENGTH_SHORT).show();
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
					Toast.makeText(getApplicationContext(),"No Internet Connection!!",Toast.LENGTH_SHORT).show();
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
				Toast.makeText(getApplicationContext(),"No Internet Connection!!",Toast.LENGTH_SHORT).show();
			}
		}else{
			internetStatus = "false";
			Toast.makeText(getApplicationContext(),"No Internet Connection!!",Toast.LENGTH_SHORT).show();
		}

		return internetStatus;
	}

	private class AsyncCallWSGetSignature extends AsyncTask<Void, Void, Void>{
		boolean isPrintCall;
		String dialogStatus,mFlag="";
		public AsyncCallWSGetSignature(boolean printCall, String flag) {
			isPrintCall = printCall;
			mFlag = flag;
		}

		@Override
		protected void onPreExecute() {
			loadprogress();
			dialogStatus = checkInternetStatus();
			getPhotoimage = "";
			getSignatureimage="";
		}

		@Override
		protected Void doInBackground(Void... voids) {

			String cmpy = SupplierSetterGetter.getCompanyCode();
			params = new HashMap<String, String>();
			params.put("CompanyCode", cmpy);
			params.put("InvoiceNo", expnseno);
			params.put("TranType", "SO");
			Log.d("sosno", "" + expnseno);

			if (onlineMode.matches("True")) {
				if (checkOffline == true) { //temp_offline

					if (dialogStatus.matches("true")) {
						//need
					} else {

						if(mobileHaveOfflineMode.matches("1")){
							finish();
						}
					}

				} else {  //Onllineine

					new XMLAccessTask(ExpenseHeader.this, valid_url,
							"fncGetInvoicePhoto", params, false,
							new GetSalesOrderImage()).execute();

					new XMLAccessTask(ExpenseHeader.this, valid_url,
							"fncGetInvoiceSignature", params, false,
							new GetSalesOrderSignature(isPrintCall)).execute();
				}

			} else if (onlineMode.matches("False")) {  // permanent_offline

				//need
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			AsyncCallWSExpenseDetail task = new AsyncCallWSExpenseDetail();
			task.execute();
		}
	}

	public class GetSalesOrderImage implements XMLAccessTask.CallbackInterface {
		public GetSalesOrderImage() {
		}
		@Override
		public void onSuccess(NodeList nl) {

			getPhotoimage = "";
			for (int i = 0; i < nl.getLength(); i++) {

				Element e = (Element) nl.item(i);
				getPhotoimage = XMLParser.getValue(e, "RefPhoto");
			}

			Log.d("getPhotoimage", "getPhotoimage" + getPhotoimage);
//			if(isPrintCall){
//
//					SOTDatabase.storeImage(1, getSignatureimage, "");
//					progressBar.setVisibility(View.GONE);
//					spinnerLayout.setVisibility(View.GONE);
//					enableViews(deliveryO_parent, true);
//					printCallDialog();
//				}
		}

		@Override
		public void onFailure(XMLAccessTask.ErrorType error) {
//			if(isPrintCall){
//				progressBar.setVisibility(View.GONE);
//				spinnerLayout.setVisibility(View.GONE);
//				enableViews(deliveryO_parent, true);
//				printCallDialog();
//			}
			onError(error);
		}

	}

	public class GetSalesOrderSignature implements XMLAccessTask.CallbackInterface {
		boolean isPrintCall;

		public GetSalesOrderSignature(boolean printCall) {
			isPrintCall = printCall;
		}

		@Override
		public void onSuccess(NodeList nl) {

			getSignatureimage = "";
			for (int i = 0; i < nl.getLength(); i++) {

				Element e = (Element) nl.item(i);
				getSignatureimage = XMLParser.getValue(e, "RefSignature");
			}

			Log.d("getSignatureimage", "getSignatureimage" + getSignatureimage);
			// if (isPrintCall) {

			SOTDatabase.storeImage(1, getSignatureimage, getPhotoimage);
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(expense_parent, true);
			// printCallDialog();
			// }
		}

		@Override
		public void onFailure(XMLAccessTask.ErrorType error) {

			// if (isPrintCall) {
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(expense_parent, true);
			// printCallDialog();
			// }
			onError(error);
		}

	}

	private void onError(final XMLAccessTask.ErrorType error) {
		new Thread() {
			@Override
			public void run() {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						if (error == XMLAccessTask.ErrorType.NETWORK_UNAVAILABLE) {
							helper.showLongToast(R.string.error_showing_image_no_network_connection);
						} else {

						}
					}
				});
			}
		}.start();
	}
}
