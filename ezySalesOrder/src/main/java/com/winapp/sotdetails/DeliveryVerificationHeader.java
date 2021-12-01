package com.winapp.sotdetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.annotation.SuppressLint;
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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.printer.UIHelper;
import com.winapp.sot.HeaderAdapter;
import com.winapp.sot.SO;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SlideMenuFragment;

public class DeliveryVerificationHeader extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace {

	private int textlength = 0;
	private EditText edCustomerCode, starteditTextDate, endeditTextDate;
	private Button btcstmrsrch;
	private ListView mList;
	private ProgressBar progressBar;
	private LinearLayout spinnerLayout, deliveryVerification_parent, searchlayout, codelayout;
	private SlidingMenu menu;
	private ImageButton searchIcon, custsearchIcon, printer;
	
	private Calendar startCalendar, endCalendar;
	private AlertDialog myalertDialog = null;
	private CustomAlertAdapterSupp arrayAdapterSupp;
	
	private ArrayList<HashMap<String, String>> searchResults;
	private ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mDetailsArr = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mHeaderArr = new ArrayList<HashMap<String, String>>();

	private ArrayList<SO> list;
	private HeaderAdapter soadapter;
	private UIHelper helper;
	private Cursor cursor;
	private SOTDatabase sotdb;
	private String companyCode="", valid_url="", serverdate, sDate, eDate, cuscode="", select_van="", mSoNo="", mIsQtyVerified="";
	private HashMap<String, String> params;
	
	// Offline
		LinearLayout offlineLayout;
		private OfflineDatabase offlineDatabase;
		boolean checkOffline;
		String onlineMode, offlineDialogStatus;
		private OfflineCommon offlineCommon;
		OfflineSettingsManager spManager;
		
	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.delivery_verification_header);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Delivery Verification");
		searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
		printer = (ImageButton) customNav.findViewById(R.id.printer);
		custsearchIcon = (ImageButton) customNav
				.findViewById(R.id.custcode_img);
		
		custsearchIcon.setVisibility(View.GONE);

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

		edCustomerCode = (EditText) findViewById(R.id.customercode);
		starteditTextDate = (EditText) findViewById(R.id.starteditTextDate);
		endeditTextDate = (EditText) findViewById(R.id.endeditTextDate);
		btcstmrsrch = (Button) findViewById(R.id.btn_search);
		mList = (ListView) findViewById(R.id.listview);
		deliveryVerification_parent = (LinearLayout) findViewById(R.id.deliveryVerification_parent);
		codelayout = (LinearLayout) findViewById(R.id.codelayout);
		searchlayout = (LinearLayout) findViewById(R.id.searchlayout);

		list = new ArrayList<SO>();
		params = new HashMap<String, String>();
		
		searchlayout.setVisibility(View.GONE);
		al.clear();
		list.clear();
		startCalendar = Calendar.getInstance();
		endCalendar = Calendar.getInstance();
		helper = new UIHelper(DeliveryVerificationHeader.this);
		FWMSSettingsDatabase.init(DeliveryVerificationHeader.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,DeliveryVerificationHeader.this);
		new SalesOrderWebService(valid_url);
		sotdb = new SOTDatabase(DeliveryVerificationHeader.this);
		SOTDatabase.init(DeliveryVerificationHeader.this);
		
		// @Offline
				onlineMode = OfflineDatabase.getOnlineMode();
				offlineDatabase = new OfflineDatabase(DeliveryVerificationHeader.this);
				offlineCommon = new OfflineCommon(DeliveryVerificationHeader.this);
				checkOffline = OfflineCommon.isConnected(DeliveryVerificationHeader.this);
				OfflineDatabase.init(DeliveryVerificationHeader.this);
				offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);
				spManager = new OfflineSettingsManager(DeliveryVerificationHeader.this);
				companyCode = spManager.getCompanyType();
		
		
//		// Offline
//				if (onlineMode.matches("True")) {
//					offlineLayout.setVisibility(View.GONE);
//				} else if (onlineMode.matches("False")) {
//					offlineLayout.setVisibility(View.VISIBLE);
//				}

				select_van = SOTDatabase.getVandriver();
				
				if(select_van!=null && !select_van.isEmpty()){			
				}else{
					select_van="";
				}	
				
				mainAsyncCallWS task = new mainAsyncCallWS();
				task.execute();
				
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				clearSetterGetter();
				SO so = soadapter.getItem(position);
				mSoNo = so.getSno().toString();
				String socustomername = so.getCustomerName().toString();
				String sostatus = so.getStatus().toString();
				if (sostatus.matches("Verified")) {
					mIsQtyVerified = "1";
				} else {
					mIsQtyVerified = "0";
				}
				
				
				detailAsyncCallWS task = new detailAsyncCallWS();
				task.execute();
//				Toast.makeText(DeliveryVerificationHeader.this, sosno +" -> "+socustomername,
//						Toast.LENGTH_LONG).show();
			}
		});
		
		edCustomerCode
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						edCustomerCode) {
					@Override
					public boolean onDrawableClick() {
						alertDialogSearch();
						return true;
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
					new DatePickerDialog(DeliveryVerificationHeader.this, startDate,
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
					new DatePickerDialog(DeliveryVerificationHeader.this, endDate,
							endCalendar.get(Calendar.YEAR), endCalendar
									.get(Calendar.MONTH), endCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
	
		btcstmrsrch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				cuscode = edCustomerCode.getText().toString();
				sDate = starteditTextDate.getText().toString();
				eDate = endeditTextDate.getText().toString();
				if (sDate.matches("")) {
					Toast.makeText(DeliveryVerificationHeader.this, "Enter Start Date",
							Toast.LENGTH_SHORT).show();
				} else if (eDate.matches("")) {
					Toast.makeText(DeliveryVerificationHeader.this, "Enter End Date",
							Toast.LENGTH_SHORT).show();
				} else {

					loadprogress();
					headerAsyncCallWS searchCustCode = new headerAsyncCallWS();
					searchCustCode.execute();
				}
			}
		});
		searchIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				InputMethodManager inputMethodManager = (InputMethodManager) DeliveryVerificationHeader.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);

				if (searchlayout.getVisibility() == View.VISIBLE) {
					searchlayout.setVisibility(View.GONE);
					inputMethodManager.hideSoftInputFromWindow(
							codelayout.getWindowToken(), 0);
				} else {
					searchlayout.setVisibility(View.VISIBLE);
					edCustomerCode.requestFocus();
					inputMethodManager.toggleSoftInputFromWindow(
							codelayout.getApplicationWindowToken(),
							InputMethodManager.SHOW_FORCED, 0);
				}
			}
		});

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

	public void alertDialogSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				DeliveryVerificationHeader.this);
		final EditText editText = new EditText(DeliveryVerificationHeader.this);
		final ListView listview = new ListView(DeliveryVerificationHeader.this);
		LinearLayout layout = new LinearLayout(DeliveryVerificationHeader.this);
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(DeliveryVerificationHeader.this, al);
		listview.setAdapter(arrayAdapterSupp);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				myalertDialog.dismiss();
				getArraylsit = arrayAdapterSupp.getArrayList();
				HashMap<String, String> datavalue = getArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String keyValues = (String) mapEntry.getKey();
					mapEntry.getValue();
					edCustomerCode.setText(keyValues);
					edCustomerCode.addTextChangedListener(new TextWatcher() {
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
							textlength = edCustomerCode.getText().length();
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
						DeliveryVerificationHeader.this, searchResults);
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

	/** Asynctask Start **/       

	private class mainAsyncCallWS extends AsyncTask<Void, Void, Void> {
		String dialogStatus;
		@Override
		protected void onPreExecute() {
//			dialogStatus = checkInternetStatus();
			al.clear();
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
//			// Offline
//				SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
//				String finalDate = timeFormat.format(new Date());
//				System.out.println(finalDate);
//			
//			if (onlineMode.matches("True")) {
//				if (checkOffline == true) { //temp_offline
//					
//					if (dialogStatus.matches("true")) {
//						al = getCustomerOffline();
//						serverdate = finalDate;						
//					} else {
//						
//						finish();
//					}
//
//				} else {  //Onllineine
					
					params.clear();
					params.put("CompanyCode", companyCode);
					params.put("VanCode", select_van);		
		        	al = SalesOrderWebService.getWholeCustomer(params, "fncGetCustomer");		        	
					serverdate = DateWebservice.getDateService("fncGetServerDate");
//				}
//
//			} else if (onlineMode.matches("False")) {  // permanent_offline
//				al = getCustomerOffline();
//				serverdate = finalDate;			
//			}	
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (serverdate != null) {
				starteditTextDate.setText(serverdate);
				endeditTextDate.setText(serverdate);
				
				cuscode = edCustomerCode.getText().toString();
				sDate = starteditTextDate.getText().toString();
				eDate = endeditTextDate.getText().toString();

				headerAsyncCallWS task = new headerAsyncCallWS();
				task.execute();
			}else{
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(deliveryVerification_parent, true);
			}

		}
	}

	private class headerAsyncCallWS extends AsyncTask<Void, Void, Void> {
		String dialogStatus;
		@Override
		protected void onPreExecute() {
//			dialogStatus = checkInternetStatus();
			list.clear();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			params.clear();
			params.put("CompanyCode", companyCode);
			params.put("CustomerCode", cuscode);
			params.put("FromDate", sDate);
			params.put("ToDate", eDate);
			params.put("VanCode", "");
//			params.put("IsPacked", "1"); // put 1
			
//			if (onlineMode.matches("True")) {
//				if (checkOffline == true) { //temp_offline
//					
//					if (dialogStatus.matches("true")) {
//						
//					      try {
//					    	  list = offlineSOHeader(cuscode, sDate, eDate, status);		
//							} catch (ParseException e) {
//								e.printStackTrace();
//							}					
//					} else {						
//						finish();
//					}
//				} else {  //Online
					
					list = SalesOrderWebService.getDeliveryVerficationList(params, "fncGetInvoiceHeader");
//				}
//			} else if (onlineMode.matches("False")) {  // permanent_offline
//				
//				  try {
//			    	  list = offlineSOHeader(cuscode, sDate, eDate, status);		
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//			}
//			
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d("list.size()", "--"+list.size());
			if (!list.isEmpty()) {
				try {
					searchCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				list.clear();
				try {
					searchCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
//				Toast.makeText(DeliveryVerificationHeader.this, "No matches found",
//						Toast.LENGTH_SHORT).show();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(deliveryVerification_parent, true);
		}
	}
	
	private class detailAsyncCallWS extends AsyncTask<Void, Void, Void> {
		String dialogStatus;
		@Override
		protected void onPreExecute() {
//			dialogStatus = checkInternetStatus();
			loadprogress();
			mHeaderArr.clear();
			mDetailsArr.clear();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
//			// Offline
//				SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
//				String finalDate = timeFormat.format(new Date());
//				System.out.println(finalDate);
//			
//			if (onlineMode.matches("True")) {
//				if (checkOffline == true) { //temp_offline
//					
//					if (dialogStatus.matches("true")) {
			//do here
//					} else {
//						
//						finish();
//					}
//
//				} else {  //Onllineine

					mHeaderArr = SalesOrderWebService.getInvoiceHeaderDeliveryVerification(mSoNo, "fncGetInvoiceHeaderByInvoiceNo");
					mDetailsArr = SalesOrderWebService.getDeliveryVerificationDetails(mSoNo, "fncGetInvoiceDetail");
//				}
//
//			} else if (onlineMode.matches("False")) {  // permanent_offline		
					//do here
//			}	
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (mHeaderArr.size()>0) {
				Intent i = new Intent(DeliveryVerificationHeader.this, DeliveryVerificationDetails.class);
				i.putExtra("SOHeader", mHeaderArr);
				i.putExtra("SODetails", mDetailsArr);
				i.putExtra("isQtyVerified", mIsQtyVerified);
				startActivity(i);
				DeliveryVerificationHeader.this.finish();
			}else{
				
				Toast.makeText(DeliveryVerificationHeader.this, " No data found-> ", Toast.LENGTH_LONG).show();
			}
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(deliveryVerification_parent, true);
			
		}
	}
	
	/** Asynctask End **/
	
	public void searchCustCode() throws ParseException {

		soadapter = new HeaderAdapter(DeliveryVerificationHeader.this,R.layout.delivery_verification_list_item, null, list);
		mList.setAdapter(soadapter);

	}
	
	/** Offline Start   **/
	
//	private String checkInternetStatus() {
//		checkOffline = OfflineCommon.isConnected(DeliveryVerificationHeader.this);
//		String internetStatus = "";
//		if (onlineMode.matches("True")) {
//			if (checkOffline == true) {
//				String Off_dialog = OfflineDatabase.getInternetMode("OfflineDialog");
//				if (Off_dialog.matches("true")) {
//					internetStatus = "true";
//				} else {
//					offlineCommon.OfflineAlertDialog();
//					Boolean dialogStatus = offlineCommon.showDialog();
//					OfflineDatabase.updateInternetMode("OfflineDialog",dialogStatus + "");
//					Log.d("Offline DialogStatus", "" + dialogStatus);
//					internetStatus = "" + dialogStatus;
//				}
//			} else if (checkOffline == false) {
//				String on_dialog = OfflineDatabase.getInternetMode("OnlineDialog");
//				if (on_dialog.matches("true")) {
//					internetStatus = "true";
//				} else {
//					offlineCommon.onlineAlertDialog();
//					boolean dialogStatus = offlineCommon.showDialog();
//					OfflineDatabase.updateInternetMode("OnlineDialog",dialogStatus + "");
//					Log.d("Online DialogStatus", "" + dialogStatus);
//					internetStatus = "" + dialogStatus;
//				}
//			}
//		}
//		onlineMode = OfflineDatabase.getOnlineMode();
//		if (onlineMode.matches("True")) {
//			offlineLayout.setVisibility(View.GONE);
//			if (checkOffline == true) {
//				if (internetStatus.matches("true")) {
//					offlineLayout.setVisibility(View.VISIBLE);
//					offlineLayout.setBackgroundResource(R.drawable.temp_offline_pattern_bg);
//				}
//			}
//
//		} else if (onlineMode.matches("False")) {
//			offlineLayout.setVisibility(View.VISIBLE);
//		}
//
//		// if(offlineLayout.getVisibility() == View.GONE){
//		// re_print.setVisibility(View.VISIBLE);
//		//
//		// }else{
//		// re_print.setVisibility(View.INVISIBLE);
//		// }
//
//		String deviceId = RowItem.getDeviceID();
//
//		Log.d("device id", "dev " + deviceId);
//
//		return internetStatus;
//	}

	
	/** Offline End **/
	
	public void clearSetterGetter() {
		SalesOrderSetGet.setHeader_flag("SalesOrderHeader");
		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");
		SalesOrderSetGet.setSaleorderdate("");
		SalesOrderSetGet.setDeliverydate("");
		SalesOrderSetGet.setCurrencycode("");
		SalesOrderSetGet.setCurrencyname("");
		SalesOrderSetGet.setCurrencyrate("");
		SalesOrderSetGet.setRemarks("");

		SOTDatabase.init(DeliveryVerificationHeader.this);
		SOTDatabase.deleteAllProduct();
		SOTDatabase.deleteBillDisc();
		SOTDatabase.deleteImage();
		SOTDatabase.deleteallbatch();
	}
	
	public void loadprogress(){
		spinnerLayout = new LinearLayout(DeliveryVerificationHeader.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		DeliveryVerificationHeader.this.addContentView(spinnerLayout,
				new LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(deliveryVerification_parent, false);
		progressBar = new ProgressBar(DeliveryVerificationHeader.this);
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


	public void showViews(boolean show, int... resId) {
		int visibility = show ? View.VISIBLE : View.GONE;
		for (int id : resId) {
			findViewById(id).setVisibility(visibility);
		}

	}

	public static void showViews(boolean show, View... views) {
		int visibility = show ? View.VISIBLE : View.GONE;
		for (View view : views) {
			view.setVisibility(visibility);
		}
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

	@Override
	public void onBackPressed() {
		Intent i = new Intent(DeliveryVerificationHeader.this, LandingActivity.class);
		startActivity(i);
		DeliveryVerificationHeader.this.finish();
	}

}
