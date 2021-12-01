package com.winapp.sot;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.DateTime;
import com.winapp.model.ScheduleDate;
import com.winapp.printer.UIHelper;
import com.winapp.util.CustomCalendar;

public class MerchandiseHeader extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace, OnClickListener,
		OnItemClickListener, OnTouchListener ,GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener  {
	private ActionBar mActionBar;
	private View customActionBarView;
	private ImageButton mScheduleImgBtn, mAddImgBtn, mSearchImgBtn;
	private SlidingMenu menu;
	private Button mAssignedBtnTab, mActivityBtnTab, mCancelBtn, mStartJobBtn,
			mSearch;

	private LinearLayout mAssignedHeaderLayout, mActivityDateLayout,
			mMerchandiseMainLayout;
	private ListView mAssignedListView, mActivityListView;
	private EditText mFromDateTv, mToDateTv;
	private TextView mTitle, mEmptyAssigned, mEmptyActivity, mCustomerNameTv,
			mCustomerAddressLblTv, mOutletAddressLblTv,mOutLetNamelblTv, mAddressTv,
			mOutLetNameTv, mCustomerNameLbl, mCustomerCodeTv, mDeliveryCodeTv,
			mAddress1Tv, mAddress2Tv, mAddress3Tv, mPhoneNoTv;
	private Calendar mFromCalendar, mToCalendar, mCalendar;
	private DatePickerDialog.OnDateSetListener mFromDatePicker, mToDatePicker,
			mDatePicker;
	private String dateFormat = "dd/MM/yyyy", mCompanyCodeStr = "",
			mRemarks1 = "", mRemarks2 = "", mServerDateJsonString = "",
			mMerchandiseJsonString = "", mGetCustomerAddressJsonString = "",
			mSaveMerchandiseStartJsonString = "", outletname = "",
			address1 = "", address2 = "", address3 = "", attention = "",
			phoneNo = "", handphoneNo = "", mMerchandiseRouteJsonString = "",
			mServerDate = "", mResult = "", mValidUrl = "", mUerIdStr = "",
			mFlag = "", mDeviceId = "", mStatus = "", mRefNo = "",
			mFromDate = "", mToDate = "", mRefDate = "", mCustomerCode = "",
			mCustomerName = "", mAddress = "", mDeliveryCode = "",mScheduleNo="",
			mOutletName = "",mScheduleDate="";
	private SimpleDateFormat mSimpleDateFormat;
	private UIHelper helper;
	private HashMap<String, String> mHashMap;
	private JSONObject mGetCustomerAddressJsonObject, mServerDateJsonObject,
			mMerchandiseRouteJsonObject, mMerchandiseJsonObject,
			mSaveMerchandiseStartJsonObject;
	private JSONArray mGetCustomerAddressJsonArray, mServerDateJsonArray,
			mMerchandiseRouteJsonArray, mMerchandiseJsonArray,
			mSaveMerchandiseStartJsonArray;
	private ArrayList<SO> mAssignedArr, mActivityArr;
	private Intent mIntent;
	private Dialog mDialog;
//	private LocationManager locationManager;
	private boolean isGPSEnabled = false, isNetworkEnabled = false,isScheduleMerchandise= false;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	private MerchandiseAdapter mMerchandiseAdapter;
	private double mLatitude, mLongitude;
	private Bundle mBundle;
	private CustomCalendar mCustomCalendar;
	private FrameLayout mFramePagerLayout;
	private ViewPager pager;
	private ArrayList<ScheduleDate> dates;
	private MerchandiseScheduleDateAdapter adapter;
	private SimpleDateFormat mDateFormat1, mDateFormat2;	
//	private GPSTracker gps;// GPSTracker class

	// get location
	Location mLastLocation;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	String lat, lon;
	private LocationManager locationManager;
	double setLatitude, setLongitude;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(R.drawable.ic_menu);
		setContentView(R.layout.activity_merchandise_header);
		setUpView();
	}

	private void setUpView() {
		// ActionBar
		mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayHomeAsUpEnabled(false);
		mActionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.header_bg));
		// Custom ActionBar View
		customActionBarView = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		mTitle = (TextView) customActionBarView.findViewById(R.id.pageTitle);
		mTitle.setText("Merchandise");
		// Custom ActionBarView IB
		mScheduleImgBtn = (ImageButton) customActionBarView
				.findViewById(R.id.printer);
		mAddImgBtn = (ImageButton) customActionBarView
				.findViewById(R.id.custcode_img);
		mSearchImgBtn = (ImageButton) customActionBarView
				.findViewById(R.id.search_img);
		mScheduleImgBtn.setVisibility(View.GONE);
		mSearchImgBtn.setVisibility(View.GONE);
		mAddImgBtn.setVisibility(View.VISIBLE);

		// mScheduleImgBtn.setImageResource(R.drawable.ic_action_schedule);
		mAddImgBtn.setImageResource(R.mipmap.ic_add_new);
		mSearchImgBtn.setImageResource(R.mipmap.search_ic);

		// Set customActionView
		mActionBar.setCustomView(customActionBarView);

		// Sherlock SlideMenu
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);

		// View ID
		mAssignedListView = (ListView) findViewById(R.id.assigned_listView);
		mActivityListView = (ListView) findViewById(R.id.activity_listView);
		mAssignedBtnTab = (Button) findViewById(R.id.assignedBtnTab);
		mActivityBtnTab = (Button) findViewById(R.id.activityBtnTab);
		mFramePagerLayout = (FrameLayout) findViewById(R.id.frame_pager);
		mCustomerNameLbl = (TextView) findViewById(R.id.customerNameLbl);
		mFromDateTv = (EditText) findViewById(R.id.startDate);
		mToDateTv = (EditText) findViewById(R.id.endDate);
		// mDate = (TextView) findViewById(R.id.date);
		mEmptyAssigned = (TextView) findViewById(R.id.noRecordAssigned);
		mEmptyActivity = (TextView) findViewById(R.id.noRecordActivity);
		mSearch = (Button) findViewById(R.id.search);
		mAssignedHeaderLayout = (LinearLayout) findViewById(R.id.assigned_header_layout);
		pager = (ViewPager) findViewById(R.id.pager);
		mMerchandiseMainLayout = (LinearLayout) findViewById(R.id.merchandiseMainLayout);
		// mAssignedDateLayout = (LinearLayout)
		// findViewById(R.id.routePlanDatelayout);
		mActivityDateLayout = (LinearLayout) findViewById(R.id.merchandiseDatelayout);

		// DB init
		FWMSSettingsDatabase.init(MerchandiseHeader.this);
		SOTDatabase.init(MerchandiseHeader.this);
		// Object Initialization
		mFromCalendar = Calendar.getInstance();
		mToCalendar = Calendar.getInstance();
		mCalendar = Calendar.getInstance();
		helper = new UIHelper(MerchandiseHeader.this);
		mHashMap = new HashMap<String, String>();
		mAssignedArr = new ArrayList<SO>();
		mActivityArr = new ArrayList<SO>();
		mIntent = new Intent();
		dates = new ArrayList<ScheduleDate>();
		mSimpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
//		gps = new GPSTracker(MerchandiseHeader.this);
		// OnClickListener
		mAssignedBtnTab.setOnClickListener(this);
		mActivityBtnTab.setOnClickListener(this);
		mSearchImgBtn.setOnClickListener(this);
		mAddImgBtn.setOnClickListener(this);
		mFromDateTv.setOnTouchListener(this);
		mToDateTv.setOnTouchListener(this);
		// mDate.setOnTouchListener(this);
		mSearch.setOnClickListener(this);
		mAssignedListView.setOnItemClickListener(this);
		mActivityListView.setOnItemClickListener(this);
		// Get URL from DB
		mValidUrl = FWMSSettingsDatabase.getUrl();
		new SalesOrderWebService(mValidUrl);

		// Get CompanyCode from Pojo class
		mCompanyCodeStr = SupplierSetterGetter.getCompanyCode();

		// Get UserId from Pojo class
		mUerIdStr = SupplierSetterGetter.getUsername();
		// Get DeviceId from Pojo class
		mDeviceId = RowItem.getDeviceID();
		
		mDateFormat1 = new SimpleDateFormat("dd/MMMM/yyyy");
		mDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

		// Getting LocationManager object
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		buildGoogleApiClient();

		if (!mGoogleApiClient.isConnected())
			mGoogleApiClient.connect();

		mBundle = getIntent().getExtras();
		if (mBundle != null) {
			String flag = mBundle.getString("Flag");
			if (flag != null && !flag.isEmpty()) {
				if (flag.matches("Activity")) {
					// Default Selection
					mActivityBtnTab
							.setBackgroundResource(R.drawable.tab_select_right);
					// Default Button Flag Tab
					mFlag = "Activity";
					mFramePagerLayout.setVisibility(View.GONE);

				} else if (flag.matches("Assigned")) {
					// Default Selection
					mAssignedBtnTab
							.setBackgroundResource(R.drawable.tab_select_left);
					// Default Button Flag Tab
					mFlag = "Assigned";
					mCustomerNameLbl.setText("Customer/Outlet Name");
					//mFramePagerLayout.setVisibility(View.VISIBLE);
				}
			} else {
				// Default Selection
				mAssignedBtnTab
						.setBackgroundResource(R.drawable.tab_select_left);
				// Default Button Flag Tab
				mFlag = "Assigned";
				mCustomerNameLbl.setText("Customer/Outlet Name");
			//	mFramePagerLayout.setVisibility(View.VISIBLE);
			}

		} else {
			// Default Selection
			mAssignedBtnTab.setBackgroundResource(R.drawable.tab_select_left);
			// Default Button Flag Tab
			mFlag = "Assigned";
			mCustomerNameLbl.setText("Customer/Outlet Name");
			
		}

		mFromDatePicker = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				mFromCalendar.set(Calendar.YEAR, year);
				mFromCalendar.set(Calendar.MONTH, monthOfYear);
				mFromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				mFromDateTv.setText(mSimpleDateFormat.format(mFromCalendar
						.getTime()));
				mFromDate = mFromDateTv.getText().toString();
			}
		};

		mToDatePicker = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				mToCalendar.set(Calendar.YEAR, year);
				mToCalendar.set(Calendar.MONTH, monthOfYear);
				mToCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				mToDateTv.setText(mSimpleDateFormat.format(mToCalendar
						.getTime()));
				mToDate = mToDateTv.getText().toString();
			}
		};
		/*
		 * mDatePicker = new DatePickerDialog.OnDateSetListener() {
		 * 
		 * @Override public void onDateSet(DatePicker view, int year, int
		 * monthOfYear, int dayOfMonth) { mCalendar.set(Calendar.YEAR, year);
		 * mCalendar.set(Calendar.MONTH, monthOfYear);
		 * mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		 * mDate.setText(mSimpleDateFormat.format(mCalendar.getTime())); } };
		 */

		/*
		 * if(mFlag.matches("Assigned")) {
		 * mAssignedHeaderLayout.setVisibility(View.VISIBLE); }else{
		 * mAssignedHeaderLayout.setVisibility(View.GONE); }
		 */
		new LoadMerchandiseData(true,true,true).execute();
		
		//getLongitudeAndLatitude();
//           getLocation();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.assignedBtnTab:
			mFlag = "Assigned";
			mAssignedBtnTab.setBackgroundResource(R.drawable.tab_select_left);
			mActivityBtnTab.setBackgroundResource(R.drawable.rounded_tab_right);
			mActivityDateLayout.setVisibility(View.GONE);
			mActivityListView.setVisibility(View.GONE);
			mEmptyActivity.setVisibility(View.GONE);
			if (mAssignedArr.isEmpty()) {
				mAssignedListView.setVisibility(View.GONE);
				mEmptyAssigned.setVisibility(View.VISIBLE);
			} else {
				mEmptyAssigned.setVisibility(View.GONE);
				mAssignedListView.setVisibility(View.VISIBLE);
			}
			mSearchImgBtn.setVisibility(View.GONE);
			mCustomerNameLbl.setText("Customer/Outlet Name");
			mFramePagerLayout.setVisibility(View.VISIBLE);
			break;
		case R.id.activityBtnTab:
			mFlag = "Activity";
			mAssignedBtnTab.setBackgroundResource(R.drawable.rounded_tab_left);
			mActivityBtnTab.setBackgroundResource(R.drawable.tab_select_right);
			mActivityDateLayout.setVisibility(View.GONE);
			mAssignedListView.setVisibility(View.GONE);
			mEmptyAssigned.setVisibility(View.GONE);
			if (mActivityArr.isEmpty()) {
				mActivityListView.setVisibility(View.GONE);
				mEmptyActivity.setVisibility(View.VISIBLE);
			} else {
				mEmptyActivity.setVisibility(View.GONE);
				mActivityListView.setVisibility(View.VISIBLE);
			}
			mSearchImgBtn.setVisibility(View.VISIBLE);
			mCustomerNameLbl.setText("Customer Name");
			mFramePagerLayout.setVisibility(View.GONE);
			break;
		case R.id.search_img:
			if (mFlag.matches("Activity")) {

				if (mActivityDateLayout.getVisibility() == View.VISIBLE) {
					mActivityDateLayout.setVisibility(View.GONE);

				} else {
					mActivityDateLayout.setVisibility(View.VISIBLE);
				}

			}
			break;
		case R.id.custcode_img:
			mIntent.setClass(MerchandiseHeader.this,
					MerchandiseDetailHeader.class);
			startActivity(mIntent);
			finish();
			SOTDatabase.deleteAllMerchandiseImage();
			break;
		case R.id.search:
			new LoadMerchandiseData(false,false,true).execute();
			break;
		case R.id.startJob:			
			// SaveMerchandiseStart
			new SaveMerchandiseStart().execute();
			break;		
		case R.id.cancel:
			mDialog.dismiss();
			break;
		default:
			break;
		}

	}

	private void initViewPager(String serverDate) {
		adapter = new MerchandiseScheduleDateAdapter(this, getDates());
		int position = getCurrentDateSelection(serverDate);
		Log.d("position", "-->"+position);
		adapter.setCurrentItem(position);
		pager.setAdapter(adapter);
		if (position > 0) {
			pager.setCurrentItem(position - 2);
		}
		if (mFramePagerLayout.getVisibility() == View.GONE) {
			mFramePagerLayout.setVisibility(View.VISIBLE);
		}
		// set intial position.
		onPagerItemClick(pager.getChildAt(position), position);
	}
	// Create ArrayList of these custom date objects to show in Activity
	private ArrayList<ScheduleDate> getDates() {
		Calendar calendar1 = Calendar.getInstance();/* new GregorianCalendar(); */
		Date pastDate = calendar1.getTime();

		calendar1.setTime(pastDate);
		calendar1.add(Calendar.YEAR, -1);
		// Date pastYear = calendar1.getTime();
		// System.out.format("future Year:  %s\n", pastYear);

		Calendar calendar2 = Calendar.getInstance();
		Date futureDate = calendar2.getTime();

		calendar2.setTime(futureDate);
		calendar2.add(Calendar.YEAR, +1);
		// Date futureYear = calendar2.getTime();
		// System.out.format("future Year:  %s\n", futureYear);

		for (Date date = calendar1.getTime(); calendar1.before(calendar2); calendar1
				.add(Calendar.DATE, 1), date = calendar1.getTime()) {
			//System.out.println(date);
			ScheduleDate merchandiseScheduleDate = new ScheduleDate();
			int dayOfMonth = calendar1.get(Calendar.DATE);
			   String dateCal ="";
			   if (dayOfMonth < 10) {
			       NumberFormat f = new DecimalFormat("00");
			       dateCal = String.valueOf(f.format(dayOfMonth));
			   }
			   else{
			    dateCal = String.valueOf(dayOfMonth);
			   }
			   merchandiseScheduleDate.setDate(dateCal);
			//merchandiseScheduleDate.setDate("" + calendar1.get(Calendar.DATE));
			merchandiseScheduleDate.setDay(getDay(calendar1
					.get(Calendar.DAY_OF_WEEK)));
			merchandiseScheduleDate.setYear("" + calendar1.get(Calendar.YEAR));
			merchandiseScheduleDate.setMonth(""
					+ getMonth(calendar1.get(Calendar.MONTH)));
			merchandiseScheduleDate.setFormattedDate(calendar1
					.get(Calendar.YEAR)
					+ "-"
					+ (calendar1.get(Calendar.MONTH) - 1)
					+ "-"
					+ calendar1.get(Calendar.DATE));
			dates.add(merchandiseScheduleDate);
		}
		return dates;
	}
		private int getCurrentDateSelection(String ServerDate) {
			int position = 0;
			// String currentDate = "10/May/2016";
			String[] currentDateSplitted = ServerDate.split("/");
			String day = currentDateSplitted[0]; // day
			String month = currentDateSplitted[1]; // month
			String year = currentDateSplitted[2]; // year

			Log.d("Date", "/->" + day);
			Log.d("MONTH", "/->" + month);
			Log.d("YEAR", "/->" + year);
			Log.d("dates.size()", "/->" + dates.size());

			for (int i = 0; i < dates.size(); i++) {
				if (day.matches(dates.get(i).getDate())
						&& month.matches(dates.get(i).getMonth())
						&& year.matches(dates.get(i).getYear())) {
					Log.d("Date", "" + dates.get(i).getDate());
					Log.d("MONTH", "" + dates.get(i).getMonth());
					Log.d("YEAR", "" + dates.get(i).getYear());
					Log.d("position", "" + i);

					position = i;

				}
			}
			return position;
		}

		private String getDay(int index) {
			switch (index) {
			case Calendar.SUNDAY:
				return "SUN";
			case Calendar.MONDAY:
				return "MON";
			case Calendar.TUESDAY:
				return "TUE";
			case Calendar.WEDNESDAY:
				return "WED";
			case Calendar.THURSDAY:
				return "THUR";
			case Calendar.FRIDAY:
				return "FRI";
			case Calendar.SATURDAY:
				return "SAT";
			}
			return "";
		}

		private String getMonth(int index) {
			switch (index) {
			case Calendar.JANUARY:
				return "January";
			case Calendar.FEBRUARY:
				return "February";
			case Calendar.MARCH:
				return "March";
			case Calendar.APRIL:
				return "April";
			case Calendar.MAY:
				return "May";
			case Calendar.JUNE:
				return "June";
			case Calendar.JULY:
				return "July";
			case Calendar.AUGUST:
				return "August";
			case Calendar.SEPTEMBER:
				return "September";
			case Calendar.OCTOBER:
				return "October";
			case Calendar.NOVEMBER:
				return "November";
			case Calendar.DECEMBER:
				return "December";
			}
			return "";
		}
		// Handle View Pager click to show selected Date:
				public void onPagerItemClick(View view, int index) {
				//	System.out.println("" + index);
					try {
						ScheduleDate merchandiseScheduleDate = this.dates
								.get(index);
						String selectedDate = merchandiseScheduleDate.getDate() + "/"
								+ merchandiseScheduleDate.getMonth() + "/"
								+ merchandiseScheduleDate.getYear();

					//	Log.d("selectedDate", "/->" + selectedDate);
						adapter.setCurrentItem(index);
						adapter.notifyDataSetChanged();

						String reformattedDate = mDateFormat2.format(mDateFormat1.parse(selectedDate));
						mScheduleDate = reformattedDate;
						if (isScheduleMerchandise) {
							new LoadMerchandiseData(false,true,false).execute();
							Log.d("reformattedDate", "false/->" + reformattedDate);
						} else {
							isScheduleMerchandise = true;
							Log.d("reformattedDate", "true /->" + reformattedDate);
						}				
					} catch (ParseException e) {
						e.printStackTrace();
					}

					// pager.setAdapter(adapter);
				}
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (v.getId()) {
		case R.id.startDate:
			if (MotionEvent.ACTION_UP == event.getAction())
				new DatePickerDialog(this, mFromDatePicker,
						mFromCalendar.get(Calendar.YEAR),
						mFromCalendar.get(Calendar.MONTH),
						mFromCalendar.get(Calendar.DAY_OF_MONTH)).show();
			break;
		case R.id.endDate:
			if (MotionEvent.ACTION_UP == event.getAction())
				new DatePickerDialog(this, mToDatePicker,
						mToCalendar.get(Calendar.YEAR),
						mToCalendar.get(Calendar.MONTH),
						mToCalendar.get(Calendar.DAY_OF_MONTH)).show();
			break;
		/*
		 * case R.id.date: if (MotionEvent.ACTION_UP == event.getAction()) new
		 * DatePickerDialog(this, mDatePicker, mToCalendar.get(Calendar.YEAR),
		 * mToCalendar .get(Calendar.MONTH), mToCalendar
		 * .get(Calendar.DAY_OF_MONTH)).show(); break;
		 */
		default:
			break;
		}

		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long id) {
		switch (adapter.getId()) {
		case R.id.assigned_listView:
			SO soAssign = mAssignedArr.get(position);
			mCustomerCode = soAssign.getCustomerCode().toString();
			// mStatus = soAssign.getStatus().toString();
			mDeliveryCode = soAssign.getDeliveryCode().toString();
			new GetCustomerAddress(position).execute(mCustomerCode,
					mDeliveryCode);

			// singleClickDialog(position);

			break;
		case R.id.activity_listView:
			SO soActiv = mActivityArr.get(position);
			mCustomerCode = soActiv.getCustomerCode().toString();
			// mStatus = soActiv.getStatus().toString();
			mDeliveryCode = soActiv.getDeliveryCode().toString();
			new GetCustomerAddress(position).execute(mCustomerCode,
					mDeliveryCode);

			// singleClickDialog(position);
			break;
		default:
			break;
		}
	}

	@Override
	public void onListItemClick(String item) {
		// Slide onListItemClick
		menu.toggle();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	private void singleClickDialog(int position) {
		CharSequence[] items = null;

		if (mFlag.matches("Assigned")) {
			SO so = mAssignedArr.get(position);
			mScheduleNo = so.getScheduleNo().toString();
			mCustomerName = so.getCustomerName().toString();
			mCustomerCode = so.getCustomerCode().toString();
			mDeliveryCode = so.getDeliveryCode().toString();
			mStatus = so.getStatus().toString();
			mRefNo = so.getRefNo().toString().toString();
			mOutletName = so.getOutLetName().toString();
			mAddress = so.getAddress().toString();
			mRefDate = "";
			mRemarks1 = "";
			mRemarks2 = "";
			items = new CharSequence[] { "Activity" };
		} else if (mFlag.matches("Activity")) {
			SO so = mActivityArr.get(position);
			mCustomerName = so.getCustomerName().toString();
			mCustomerCode = so.getCustomerCode().toString();
			mDeliveryCode = so.getDeliveryCode().toString();
			mOutletName = so.getOutLetName().toString();
			mStatus = so.getStatus().toString().toString();
			mRefNo = so.getRefNo().toString().toString();
			mRefDate = so.getRefDate().toString();
			mRemarks1 = so.getRemarks1().toString();
			mRemarks2 = so.getRemarks2().toString();
			items = new CharSequence[] { "Edit" };
		} else {
			mCustomerName = "Make your selection";
		}
		SOTDatabase.deleteAllMerchandiseImage();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(mCustomerName);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {

				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("CustomerCode", mCustomerCode);
				hashMap.put("CustomerName", mCustomerName);
				hashMap.put("DeliveryCode", mDeliveryCode);
				hashMap.put("OutletName", mOutletName);
				hashMap.put("Status", mStatus);
				hashMap.put("RefNo", mRefNo);
				hashMap.put("RefDate", mRefDate);
				hashMap.put("Flag", mFlag);
				hashMap.put("Remarks1", mRemarks1);
				hashMap.put("Remarks2", mRemarks2);
				hashMap.put("Address1", address1);
				hashMap.put("Address2", address2);
				hashMap.put("Address3", address3);
				hashMap.put("Attention", attention);
				hashMap.put("PhoneNo", phoneNo);
				hashMap.put("HandphoneNo", handphoneNo);

				if (mFlag.matches("Assigned")) {
					if (mStatus != null && !mStatus.isEmpty()) {
						mIntent.setClass(MerchandiseHeader.this,
								MerchandiseDetailHeader.class);
						/*
						 * mIntent.putExtra("CustomerCode",mCustomerCode);
						 * mIntent.putExtra("DeliveryCode",mDeliveryCode);
						 * mIntent.putExtra("Status",mStatus);
						 * mIntent.putExtra("RefNo",mRefNo);
						 * mIntent.putExtra("RefDate",mRefDate);
						 * mIntent.putExtra("Flag",mFlag);
						 */
						mIntent.putExtra("HashMap", hashMap);
						startActivity(mIntent);
						finish();

					} else {
						startJob(mCustomerCode, mDeliveryCode);
					}
				} else if (mFlag.matches("Activity")) {
					mIntent.setClass(MerchandiseHeader.this,
							MerchandiseDetailHeader.class);
					/*
					 * mIntent.putExtra("CustomerCode",mCustomerCode);
					 * mIntent.putExtra("DeliveryCode",mDeliveryCode);
					 * mIntent.putExtra("Status",mStatus);
					 * mIntent.putExtra("RefNo",mRefNo);
					 * mIntent.putExtra("RefDate",mRefDate);
					 * mIntent.putExtra("Flag",mFlag);
					 */
					mIntent.putExtra("HashMap", hashMap);
					startActivity(mIntent);
					finish();
				}

				// if(mFlag.matches("Assigned")){
				// if(mStatus!=null && !mStatus.isEmpty()){
				/*
				 * mIntent.setClass(MerchandiseHeader.this,
				 * MerchandiseDetailHeader.class);
				 * mIntent.putExtra("CustomerCode",mCustomerCode);
				 * mIntent.putExtra("CustomerName",mCustomerName);
				 * mIntent.putExtra("DeliveryCode",mDeliveryCode);
				 * mIntent.putExtra("OutletName",mOutletName);
				 * mIntent.putExtra("Status",mStatus);
				 * mIntent.putExtra("RefNo",mRefNo);
				 * mIntent.putExtra("RefDate",mRefDate);
				 * mIntent.putExtra("Flag",mFlag);
				 * mIntent.putExtra("Remarks1",mRemarks1);
				 * mIntent.putExtra("Remarks2",mRemarks2);
				 * startActivity(mIntent); finish();
				 */
				// helper.showProgressView(mMerchandiseMainLayout);

				// new GetCustomerAddress(mRefNo,mFlag).execute(mCustomerCode);

				// }else{
				// startJob(mCustomerCode,mDeliveryCode);
				// }
				// }else if(mFlag.matches("Activity")){
				// helper.showProgressView(mMerchandiseMainLayout);
				// new GetCustomerAddress(mRefNo,mFlag).execute(mCustomerCode);
				// }

			}
		});
		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		alert.show();

	}

	@SuppressWarnings("deprecation")
	private void startJob(String mCustomerCode, String mDeliveryCode) {
		mDialog = new Dialog(MerchandiseHeader.this);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setContentView(R.layout.dialog_bottom_sheet);
		mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		mDialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		Window window = mDialog.getWindow();
		window.setGravity(Gravity.TOP);
		mDialog.show();

		// Dialog View ID
		mCustomerCodeTv = (TextView) mDialog.findViewById(R.id.customerCode);
		mCustomerNameTv = (TextView) mDialog.findViewById(R.id.customerName);
		mCustomerAddressLblTv = (TextView) mDialog
				.findViewById(R.id.customer_address);
		mOutletAddressLblTv = (TextView) mDialog
				.findViewById(R.id.outlet_address);
		
		mOutLetNamelblTv = (TextView) mDialog.findViewById(R.id.outLetNamelbl);
		mAddressTv = (TextView) mDialog.findViewById(R.id.address);

		mAddress1Tv = (TextView) mDialog.findViewById(R.id.address1);
		mAddress2Tv = (TextView) mDialog.findViewById(R.id.address2);
		mAddress3Tv = (TextView) mDialog.findViewById(R.id.address3);
		mPhoneNoTv = (TextView) mDialog.findViewById(R.id.phoneNo);

		mOutLetNameTv = (TextView) mDialog.findViewById(R.id.outLetName);
		mDeliveryCodeTv = (TextView) mDialog.findViewById(R.id.deliveryCode);
		mStartJobBtn = (Button) mDialog.findViewById(R.id.startJob);
		mCancelBtn = (Button) mDialog.findViewById(R.id.cancel);

		// OnClickListener
		mStartJobBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);

		// Set selected value
		mCustomerCodeTv.setText(mCustomerCode);
		mDeliveryCodeTv.setText(mDeliveryCode);
		mCustomerNameTv.setText(mCustomerName);
		
		
		
		
		
		if (mOutletName != null && !mOutletName.isEmpty()) {
			mOutLetNameTv.setText(mOutletName);
			mOutLetNameTv.setVisibility(View.VISIBLE);
			mOutLetNamelblTv.setVisibility(View.VISIBLE);
		} else {
			mOutLetNamelblTv.setVisibility(View.GONE);
			// mAddressTv.setText("-");
			mOutLetNameTv.setVisibility(View.GONE);
		}

		if (mAddress != null && !mAddress.isEmpty()) {
			mAddressTv.setText(mAddress);
			mCustomerAddressLblTv.setVisibility(View.VISIBLE);
		} else {
			mCustomerAddressLblTv.setVisibility(View.GONE);
			// mAddressTv.setText("-");
			mAddressTv.setVisibility(View.GONE);
		}

		if (address1 != null && !address1.isEmpty()) {
			mAddress1Tv.setText(address1);
		} else {
			mAddress1Tv.setVisibility(View.GONE);
		}

		if (address2 != null && !address2.isEmpty()) {
			mAddress2Tv.setText(address2);
		} else {
			mAddress2Tv.setVisibility(View.GONE);
		}

		if (address3 != null && !address3.isEmpty()) {
			mAddress3Tv.setText(address3);
		} else {
			mAddress3Tv.setVisibility(View.GONE);
		}

		if (address1 != null && !address1.isEmpty() && address2 != null
				&& !address2.isEmpty() && address3 != null
				&& !address3.isEmpty()) {
			mAddress1Tv.setVisibility(View.VISIBLE);
			mAddress2Tv.setVisibility(View.VISIBLE);
			mAddress3Tv.setVisibility(View.VISIBLE);
		} else {
			// mAddress1Tv.setText("-");
			mAddress1Tv.setVisibility(View.GONE);
			mAddress2Tv.setVisibility(View.GONE);
			mAddress3Tv.setVisibility(View.GONE);
			mOutletAddressLblTv.setVisibility(View.GONE);
		}

		if (handphoneNo != null && !handphoneNo.isEmpty()) {
			mPhoneNoTv.setText("Ph : " + handphoneNo);
		} else {
			mPhoneNoTv.setVisibility(View.GONE);
		}

	}
	/*public void getLocation(){
	     if(gps.canGetLocation()){        
	      mLatitude = gps.getLatitude();
	      mLongitude = gps.getLongitude();  
	   Log.d("latitude","-->"+mLatitude);
	   Log.d("longitude","-->"+mLongitude);
	  // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + mLatitude + "\nLong: " + mLongitude, Toast.LENGTH_LONG).show();
	        }else{
	         // can't get location
	         // GPS or Network is not enabled
	         // Ask user to enable GPS/network in settings
	     //    gps.showSettingsAlert();         
	        }
	    }*/

	/*private void getLongitudeAndLatitude() {
		try {
			// Getting LocationManager object
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			// // Creating an empty criteria object
			// Criteria criteria = new Criteria();
			//
			// // Getting the name of the provider that meets the criteria
			// provider = locationManager.getBestProvider(criteria, false);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {

				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 20000, 0, this);
					if (locationManager != null) {
						Location location = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {

							onLocationChanged(location);

						}else{
			                   Log.d("location", "null");
			                   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, this);
						}  
					}
				} else if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 20000, 0, this);
					if (locationManager != null) {
						Location location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							onLocationChanged(location);

						}else{
			                   Log.d("location", "null");
			                   locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, this);
						} 
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onLocationChanged(Location location) {
		try {
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();
			Log.d("latitude","-->"+mLatitude);
			Log.d("mLongitude","-->"+mLongitude);
			locationManager.removeUpdates(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}*/

	private class LoadMerchandiseData extends AsyncTask<Void, Void, Void> {
		private boolean isServerDate,isMerchandiseSchedule,isMerchandise;

		private LoadMerchandiseData(boolean serverDate,boolean merchandiseSchedule,boolean merchandise) {
			this.isServerDate = serverDate;
			this.isMerchandiseSchedule = merchandiseSchedule;
			this.isMerchandise = merchandise;
		}

		@Override
		protected void onPreExecute() {
			if (isMerchandiseSchedule) {
				mAssignedArr.clear();
			}
			if (isMerchandise) {
				mActivityArr.clear();
			}
			
			helper.showProgressView(mMerchandiseMainLayout);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			mHashMap.put("CompanyCode", mCompanyCodeStr);
			try {
				if (isServerDate) {
					// Call GetServerDate
					mServerDateJsonString = SalesOrderWebService.getSODetail(
							mHashMap, "fncGetServerDate");
					mServerDateJsonObject = new JSONObject(
							mServerDateJsonString);
					mServerDateJsonArray = mServerDateJsonObject
							.optJSONArray("SODetails");
					JSONObject jsonobject = mServerDateJsonArray
							.getJSONObject(0);
					mServerDate = jsonobject.getString("ServerDate");
					mScheduleDate = mServerDate;
					mFromDate = mServerDate;
					mToDate = mServerDate;
				}
				mHashMap.put("UserName", mUerIdStr);
				if (isMerchandiseSchedule) {
					mHashMap.put("ScheduleDate", mScheduleDate);
					// Call GetMerchandiseRoute
					mMerchandiseRouteJsonString = SalesOrderWebService
							.getSODetail(mHashMap, "fncGetMerchandiseSchedule");
					mMerchandiseRouteJsonObject = new JSONObject(
							mMerchandiseRouteJsonString);
					mMerchandiseRouteJsonArray = mMerchandiseRouteJsonObject
							.optJSONArray("SODetails");

					int lengthMerchandiseRouteJsonArr = mMerchandiseRouteJsonArray
							.length();

					if (lengthMerchandiseRouteJsonArr > 0) {
						for (int i = 0; i < lengthMerchandiseRouteJsonArr; i++) {
							try {
								SO so = new SO();
								JSONObject jsonChildNode = mMerchandiseRouteJsonArray
										.getJSONObject(i);
								so.setScheduleNo(jsonChildNode.optString("ScheduleNo")
										.toString());
								so.setRefNo(jsonChildNode.optString("RefNo")
										.toString());
								so.setCustomerCode(jsonChildNode.optString(
										"CustomerCode").toString());
								so.setCustomerName(jsonChildNode.optString(
										"CustomerName").toString());

								so.setAddress(jsonChildNode.optString(
										"Address1").toString());

								so.setIsJobClosed(jsonChildNode.optString(
										"IsJobClosed").toString());

								so.setOutletAddress(jsonChildNode.optString(
										"OutletAddress1").toString());

								String status = jsonChildNode.optString(
										"StatusCode").toString();
								so.setStatus(status);
								String companyName = jsonChildNode.optString(
										"CompanyName").toString();
								// if(companyName!=null &&
								// !companyName.isEmpty()){
								so.setOutLetName(companyName);
								// }else{
								// so.setOutLetName("-");
								// }
								so.setDeliveryCode(jsonChildNode.optString(
										"DeliveryCode").toString());
								mAssignedArr.add(so);

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}
				if (isMerchandise) {
				mHashMap.put("RefNo", "");
				mHashMap.put("FromDate", mFromDate);
				mHashMap.put("ToDate", mToDate);
				// Call GetMerchandise
				mMerchandiseJsonString = SalesOrderWebService.getSODetail(
						mHashMap, "fncGetMerchandise");
				mMerchandiseJsonObject = new JSONObject(mMerchandiseJsonString);
				mMerchandiseJsonArray = mMerchandiseJsonObject
						.optJSONArray("SODetails");

				int lengthMerchandiseJsonArr = mMerchandiseJsonArray.length();

				if (lengthMerchandiseJsonArr > 0) {
					for (int i = 0; i < lengthMerchandiseJsonArr; i++) {

						try {
							SO so = new SO();
							JSONObject jsonChildNode = mMerchandiseJsonArray
									.getJSONObject(i);
							so.setRefNo(jsonChildNode.optString("RefNo")
									.toString());
							so.setRefDate(jsonChildNode.optString("RefDate")
									.toString().split("\\ ")[0]);
							so.setCustomerCode(jsonChildNode.optString(
									"CustomerCode").toString());
							so.setCustomerName(jsonChildNode.optString(
									"CustomerName").toString());
							String status = jsonChildNode.optString(
									"StatusCode").toString();
							so.setStatus(status);
							String companyName = jsonChildNode.optString(
									"CompanyName").toString();
							so.setOutLetName(companyName);
							
							so.setDeliveryCode(jsonChildNode.optString(
									"DeliveryCode").toString());

							so.setIsJobClosed(jsonChildNode.optString(
									"IsJobClosed").toString());

							String jobStartTime = jsonChildNode
									.optString("JobStartTime").toString()
									.split("\\ ")[1];						
							
							
							String jobEndTime = jsonChildNode.optString("JobEndTime").toString();							
							String endJobDate = jobEndTime.split("\\ ")[0];
							
							String endJobTime = jobEndTime.split("\\ ")[1];
							
							if(endJobDate.matches("1/1/1900")){
								so.setJobEndTime("0:00:00");
							}
							else{
								so.setJobEndTime(endJobTime);
							}
							so.setAddress(jsonChildNode.optString(
									"Address1").toString());
							so.setOutletAddress(jsonChildNode.optString(
									"OutletAddress1").toString());
							so.setRemarks1(jsonChildNode.optString("Remarks1")
									.toString());
							so.setRemarks2(jsonChildNode.optString("Remarks2")
									.toString());

							so.setJobStartTime(jobStartTime);
							

							mActivityArr.add(so);
						} catch (JSONException e) {
							e.printStackTrace();
						
					}
					}
				}
			}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void arg0) {
			if(!isScheduleMerchandise){
				try {
					String reformattedServerDate = mDateFormat1
							.format(mDateFormat2.parse(mServerDate));
					Log.d("reformattedDate", "->" + reformattedServerDate);
					initViewPager(reformattedServerDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			// Set Server
			SalesOrderSetGet.setServerDate(mServerDate);

			// Show last one month date
			mFromDateTv.setText(mFromDate);
			mToDateTv.setText(mToDate);

			if (mAssignedArr != null && !mAssignedArr.isEmpty()) {
				mMerchandiseAdapter = new MerchandiseAdapter(MerchandiseHeader.this, mAssignedArr);
				mAssignedListView.setAdapter(mMerchandiseAdapter);
				if (mFlag.matches("Assigned")) {
					mAssignedListView.setVisibility(View.VISIBLE);
					mEmptyAssigned.setVisibility(View.GONE);
					if (mFramePagerLayout.getVisibility() == View.GONE) {
						mFramePagerLayout.setVisibility(View.VISIBLE);
					}
				}
			} else {
				if (mFlag.matches("Assigned")) {
					mAssignedListView.setVisibility(View.GONE);
					//mEmptyActivity.setVisibility(View.GONE);
					mEmptyAssigned.setVisibility(View.VISIBLE);
					if (mFramePagerLayout.getVisibility() == View.GONE) {
						mFramePagerLayout.setVisibility(View.VISIBLE);
					}
					//mAssignedHeaderLayout.setVisibility(View.GONE);
				}
			}
			

			if (mActivityArr != null && !mActivityArr.isEmpty()) {
				Log.d("mActivityArr.size()", "" + mActivityArr.size());
				mMerchandiseAdapter = new MerchandiseAdapter(
						MerchandiseHeader.this, mActivityArr);
				mActivityListView.setAdapter(mMerchandiseAdapter);
				if (mFlag.matches("Activity")) {
				//	mActivityListView.setVisibility(View.VISIBLE);
					mActivityListView.setVisibility(View.VISIBLE);
					mEmptyActivity.setVisibility(View.GONE);
					if (mFramePagerLayout.getVisibility() == View.VISIBLE) {
						mFramePagerLayout.setVisibility(View.GONE);
					}
				}
			} else {
				if (mFlag.matches("Activity")) {
					Log.d("mActivityArr.size()", "Activity");
					mActivityListView.setVisibility(View.GONE);
					mEmptyActivity.setVisibility(View.VISIBLE);
					//mEmptyAssigned.setVisibility(View.GONE);
					if (mFramePagerLayout.getVisibility() == View.VISIBLE) {
						mFramePagerLayout.setVisibility(View.GONE);
					}
				}
			}
						
			helper.dismissProgressView(mMerchandiseMainLayout);
		}
	}

	public class MerchandiseAdapter extends BaseAdapter {

		private ArrayList<SO> listArray;
		private ArrayList<SO> mOriginalValues;
		LayoutInflater mInflater;

		public MerchandiseAdapter() {

		}

		public MerchandiseAdapter(Activity context, ArrayList<SO> productsList) {
			listArray = new ArrayList<SO>();
			mOriginalValues = new ArrayList<SO>();
			listArray = productsList;
			mOriginalValues.addAll(productsList);
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return listArray.size();
		}

		@Override
		public SO getItem(int position) {
			return listArray.get(position);
		}

		@Override
		public long getItemId(int position) {
			return listArray.get(position).hashCode();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View row = convertView;
			ViewHolder holder;
			SO so = getItem(position);
			if (row == null) {
				row = mInflater.inflate(R.layout.merchandise_list_item, null);
				holder = new ViewHolder();

				holder.customerName = (TextView) row
						.findViewById(R.id.customerName);
				/*holder.customerName_assigned = (TextView) row
						.findViewById(R.id.customerName_assigned);*/
				holder.status = (TextView) row
						.findViewById(R.id.status);
				/*holder.status_assigned = (TextView) row
						.findViewById(R.id.status_assigned);*/
				holder.jobStartTimeLbl = (TextView) row
						.findViewById(R.id.textView1);
				holder.jobTimeStart = (TextView) row
						.findViewById(R.id.textView2);

				holder.jobEndTimeLbl = (TextView) row
						.findViewById(R.id.textView3);

				holder.jobTimeEnd = (TextView) row.findViewById(R.id.textView4);

				holder.outLetName = (TextView) row
						.findViewById(R.id.outletName);

				holder.address = (TextView) row.findViewById(R.id.address);
				
				
				
				holder.scheduleDateLayout = (LinearLayout) row
				.findViewById(R.id.scheduleDateLayout);

			/*	holder.outletName_layout = (LinearLayout) row
						.findViewById(R.id.outletName_layout);*/

				/*holder.customerAddressLayout = (LinearLayout) row
						.findViewById(R.id.address_layout);*/

				/*holder.date_layout = (LinearLayout) row
						.findViewById(R.id.date_layout);*/

				holder.outletAddress = (TextView) row
						.findViewById(R.id.outletAddress);

				/*holder.outletAddress_layout = (LinearLayout) row
						.findViewById(R.id.outletAddress_layout);*/
			/*	holder.linearLayout = (LinearLayout) row
						.findViewById(R.id.LinearLayout2);*/
/*
				holder.activity = (LinearLayout) row
						.findViewById(R.id.activity);

				holder.assigned = (LinearLayout) row
						.findViewById(R.id.assigned);*/
				
				holder.startTimeLayout = (LinearLayout) row
						.findViewById(R.id.startTimeLayout);
				
				holder.endTimeLayout = (LinearLayout) row
						.findViewById(R.id.endTimeLayout);
				holder.scheduleDate = (TextView) row.findViewById(R.id.textView6);
				row.setTag(holder);
			} else {
				holder = (ViewHolder) row.getTag();
			}
			String isJobClosed = so.getIsJobClosed();

			if (isJobClosed != null && !isJobClosed.isEmpty()) {
				if (isJobClosed.equalsIgnoreCase("0")) {
					holder.status.setText("Open");
					row.setBackgroundColor(Color.parseColor("#ffffff"));
				} else if (isJobClosed.equalsIgnoreCase("1")) {
					holder.status.setText("In Progress");
					row.setBackgroundColor(Color.parseColor("#f4cf6d"));
				} else if (isJobClosed.matches("2")) {
					holder.status.setText("Closed");
					row.setBackgroundColor(Color.parseColor("#88e5ad"));
				}
			} else {
				holder.status.setText("Open");
				row.setBackgroundColor(Color.parseColor("#ffffff"));
			}
			try {

				if (mFlag.matches("Activity")) {

					holder.customerName.setText(so.getCustomerName());
				//	holder.activity.setVisibility(View.VISIBLE);
				//	holder.assigned.setVisibility(View.GONE);
				//	holder.linearLayout.setVisibility(View.VISIBLE);
					holder.jobTimeStart.setText(so.getJobStartTime());
					holder.jobStartTimeLbl.setText("StartTime");
					holder.jobEndTimeLbl.setText("EndTime");
					holder.jobTimeEnd.setText(so.getJobEndTime());
					holder.scheduleDateLayout.setVisibility(View.VISIBLE);
					holder.scheduleDate.setText(so.getRefDate());
					//holder.customerAddressLayout.setVisibility(View.GONE);
				//	holder.outletName_layout.setVisibility(View.GONE);
					//holder.date_layout.setVisibility(View.VISIBLE);
				//	holder.outletAddress_layout.setVisibility(View.GONE);

				/*	String isJobClosed = so.getIsJobClosed();

					if (isJobClosed != null && !isJobClosed.isEmpty()) {
						if (isJobClosed.equalsIgnoreCase("0")) {
							holder.status_activity.setText("Open");
							row.setBackgroundColor(Color.parseColor("#ffffff"));
						} else if (isJobClosed.equalsIgnoreCase("1")) {
							holder.status_activity.setText("In Progress");
							row.setBackgroundColor(Color.parseColor("#f4cf6d"));
						} else if (isJobClosed.matches("2")) {
							holder.status_activity.setText("Closed");
							row.setBackgroundColor(Color.parseColor("#88e5ad"));
						}
					} else {
						holder.status_activity.setText("Open");
						row.setBackgroundColor(Color.parseColor("#ffffff"));
					}*/
					holder.startTimeLayout.setVisibility(View.VISIBLE);					
					holder.endTimeLayout.setVisibility(View.VISIBLE);
					holder.outLetName.setText(so.getOutLetName());
					holder.address.setText(so.getAddress());
					holder.outletAddress.setText(so.getOutletAddress());
				//	holder.outLetName.setText(so.getOutLetName());
				//	holder.address.setText(so.getAddress());
				//	holder.outletAddress.setText(so.getOutletAddress());
					
				} else {
					
					holder.startTimeLayout.setVisibility(View.GONE);					
					holder.endTimeLayout.setVisibility(View.GONE);
					holder.scheduleDateLayout.setVisibility(View.GONE);
					holder.customerName.setText(so.getCustomerName());
					//holder.activity.setVisibility(View.GONE);
					//holder.assigned.setVisibility(View.VISIBLE);
				//	holder.linearLayout.setVisibility(View.GONE);

					// holder.outLetNameLbl.setText("Outlet Name");
					// holder.customerAddressLayout.setVisibility(View.VISIBLE);
					// holder.outletName_layout.setVisibility(View.VISIBLE);
				//	holder.date_layout.setVisibility(View.GONE);

					//if (so.getAddress() != null && !so.getAddress().isEmpty()) {
						holder.address.setText(so.getAddress());
						/*holder.customerAddressLayout
								.setVisibility(View.VISIBLE);*/
					//} else {
					//	holder.customerAddressLayout.setVisibility(View.GONE);
					//}

					//if (so.getOutLetName() != null && !so.getOutLetName().isEmpty()) {
						holder.outLetName.setText(so.getOutLetName());
						//holder.outletName_layout.setVisibility(View.VISIBLE);
					//} else {
					//	holder.outletName_layout.setVisibility(View.GONE);
					//}

				//	if (so.getOutletAddress() != null && !so.getOutletAddress().isEmpty()) {
						//holder.outletAddress_layout.setVisibility(View.VISIBLE);
						holder.outletAddress.setText(so.getOutletAddress());
					//} else {
						//holder.outletAddress_layout.setVisibility(View.GONE);
					//}
					// holder.address.setText(so.getAddress());

					

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return row;
		}

		final class ViewHolder {

			TextView customerName;
			TextView status, scheduleDate/*status_assigned*/;
			TextView jobTimeStart, outLetName, address, outletAddress;
			TextView jobStartTimeLbl;
			LinearLayout  scheduleDateLayout,
					 
					startTimeLayout,endTimeLayout;

			TextView jobEndTimeLbl;

			TextView jobTimeEnd;

			TextView customerNameLbl;

			TextView statusLbl;

		}

	}
	public class MerchandiseScheduleDateAdapter extends PagerAdapter {

		private Context mContext;
		private ArrayList<ScheduleDate> dates;
		private LayoutInflater inflater;
		private int currentItemPos;

		public MerchandiseScheduleDateAdapter(Context context,
				ArrayList<ScheduleDate> objects) {
			this.mContext = context;
			this.dates = objects;

			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void setCurrentItem(int item) {
			this.currentItemPos = item;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ViewHolder holder;

			ScheduleDate merchandiseScheduleDate = this.dates
					.get(position);

			View convertView = inflater.inflate(
					R.layout.pager_item_mechandise_schedule, container, false);
			holder = new ViewHolder();

			holder.date = (TextView) convertView
					.findViewById(R.id.date);
			holder.day = (TextView) convertView
					.findViewById(R.id.day);
			/*holder.monthTextView = (TextView) convertView
					.findViewById(R.id.layout_date_month);
			holder.yearTextView = (TextView) convertView
					.findViewById(R.id.layout_date_year);*/			
			
			holder.outerLayout = (LinearLayout) convertView
					.findViewById(R.id.layout_date_day);

			convertView.setTag(Integer.valueOf(position));

			holder.date.setText(merchandiseScheduleDate.getDate());
			holder.day.setText(merchandiseScheduleDate.getDay());
		//	holder.monthTextView.setText(merchandiseScheduleDate.getMonth());
		//	holder.yearTextView.setText(merchandiseScheduleDate.getYear());
			
			//holder.monthTextView.setVisibility(View.GONE);
		//	holder.yearTextView.setVisibility(View.GONE);
			
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onPagerItemClick(v, (Integer) v.getTag());
				}
			});
			
			if (position == currentItemPos) {
				holder.date.setTextColor(Color.parseColor("#ffffff"));
				holder.day.setTextColor(Color.parseColor("#ffffff"));	
				holder.outerLayout.setBackgroundResource(R.drawable.rounded_edge_blue);
			} else {
				try {					
					String selectedDate = merchandiseScheduleDate.getDate() + "/" + merchandiseScheduleDate.getMonth() + "/" + merchandiseScheduleDate.getYear();
				//	Log.d("selectedDate", "/->" + selectedDate);
					String reformattedDate = mDateFormat2.format(mDateFormat1.parse(selectedDate));					
					if(reformattedDate.matches(mServerDate)){
						holder.date.setTextColor(Color.parseColor("#696969"));
						holder.day.setTextColor(Color.parseColor("#696969"));
						holder.outerLayout.setBackgroundResource(R.drawable.rounded_edge_dark_grey);					
					}else{
						holder.date.setTextColor(Color.parseColor("#696969"));
						holder.day.setTextColor(Color.parseColor("#696969"));
						holder.outerLayout.setBackgroundResource(R.drawable.rounded_edge_grey);
					
					}
				
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				

			}
			((ViewPager) container).addView(convertView);

			return convertView;
		}

		private class ViewHolder {
			//private TextView monthTextView;
			private TextView day;
			private TextView date;
		//	private TextView yearTextView;
			private LinearLayout outerLayout;
		}

		@Override
		public int getCount() {
			return dates.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			// TODO Auto-generated method stub
			return view == (object);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			((ViewPager) container).removeView(view);
			view = null;
		}

		public float getPageWidth(int position) {
			return 0.2f;
		}
	}
	private class SaveMerchandiseStart extends AsyncTask<Void, Void, Void> {
		String longitude = "", latitude = "", refDate = "";

		@Override
		protected void onPreExecute() {
			mHashMap.clear();
			mDialog.dismiss();
			helper.showProgressView(mMerchandiseMainLayout);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				refDate = SalesOrderSetGet.getServerDate();

				longitude = String.valueOf(mLongitude);
				latitude = String.valueOf(mLatitude);

				Log.d("longitude", "" + longitude);
				Log.d("latitude", "" + latitude);
				Log.d("mCompanyCodeStr", "" + mCompanyCodeStr);
				Log.d("refDate", "" + refDate);
				Log.d("mCustomerCode", "" + mCustomerCode);
				Log.d("mDeliveryCode", "" + mDeliveryCode);
				Log.d("mDeviceId", "" + mDeviceId);
				Log.d("mUerIdStr", "" + mUerIdStr);

				mHashMap.put("CompanyCode", mCompanyCodeStr);
				mHashMap.put("RefNo", "");
				mHashMap.put("RefDate", refDate);
				mHashMap.put("CustomerCode", mCustomerCode);
				mHashMap.put("DeliveryCode", mDeliveryCode);
				mHashMap.put("JobStartTime", "");
				mHashMap.put("Latitude", latitude);
				mHashMap.put("Longitude", longitude);
				mHashMap.put("DeviceID", mDeviceId);
				mHashMap.put("User", mUerIdStr);
				mHashMap.put("IsScheduledJob", "1");
				mHashMap.put("ScheduleJobNo",mScheduleNo);
				
				// Call GetServerDate
				mSaveMerchandiseStartJsonString = SalesOrderWebService
						.getSODetail(mHashMap, "fncSaveMerchandiseStart");
				Log.d("mSaveMerchandise", ""
						+ mSaveMerchandiseStartJsonString);
				mSaveMerchandiseStartJsonObject = new JSONObject(
						mSaveMerchandiseStartJsonString);
				mSaveMerchandiseStartJsonArray = mSaveMerchandiseStartJsonObject
						.optJSONArray("SODetails");

				int lengthJsonArr = mSaveMerchandiseStartJsonArray.length();
				if (lengthJsonArr > 0) {
					JSONObject jsonobject = mSaveMerchandiseStartJsonArray
							.getJSONObject(0);

					Log.d("jsonobject", "" + jsonobject.toString());
					mResult = jsonobject.getString("Result");

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void arg0) {
			if (mResult != null && !mResult.isEmpty()) {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("CustomerCode", mCustomerCode);
				hashMap.put("CustomerName", mCustomerName);
				hashMap.put("DeliveryCode", mDeliveryCode);
				hashMap.put("OutletName", mOutletName);
				hashMap.put("Status", mStatus);
				hashMap.put("RefNo", mResult);
				hashMap.put("RefDate", mRefDate);
				hashMap.put("Flag", mFlag);
				hashMap.put("Remarks1", mRemarks1);
				hashMap.put("Remarks2", mRemarks2);
				hashMap.put("Address1", address1);
				hashMap.put("Address2", address2);
				hashMap.put("Address3", address3);
				hashMap.put("Attention", attention);
				hashMap.put("PhoneNo", phoneNo);
				hashMap.put("HandphoneNo", handphoneNo);

				mIntent.setClass(MerchandiseHeader.this,
						MerchandiseDetailHeader.class);
				mIntent.putExtra("HashMap", hashMap);
				startActivity(mIntent);
				finish();

				// new GetMerchandise().execute(mResult);
			} else {
				Toast.makeText(MerchandiseHeader.this, "Start Job Failed",
						Toast.LENGTH_SHORT).show();

				helper.dismissProgressView(mMerchandiseMainLayout);
			}

		}
	}

	private class GetCustomerAddress extends AsyncTask<String, Void, Void> {

		/*
		 * String flag="",mRefNo="",outletname="" ,address1
		 * ="",address2="",address3 ="", attention="" , phoneNo="", handphoneNo
		 * =""; public GetCustomerAddress(String refNo, String mflag) { mRefNo =
		 * refNo; flag = mflag; }
		 */
		int mposition;

		public GetCustomerAddress(int position) {
			mposition = position;
		}

		@Override
		protected void onPreExecute() {
			helper.showProgressView(mMerchandiseMainLayout);
			mHashMap.clear();
		}

		@Override
		protected Void doInBackground(String... params) {
			String customerCode = params[0];
			String deliveryCode = params[1];
			try {
				mHashMap.put("CompanyCode", mCompanyCodeStr);
				mHashMap.put("CustomerCode", customerCode);
				mHashMap.put("DeliveryCode", deliveryCode);

				Log.d("mCompanyCodeStr", "" + mCompanyCodeStr);
				Log.d("customerCode", customerCode);
				Log.d("deliveryCode", deliveryCode);

				mGetCustomerAddressJsonString = SalesOrderWebService
						.getSODetail(mHashMap, "fncGetCustomerAddress");
				// Log.d("mGetCustomerAddressJsonString", ""+
				// mGetCustomerAddressJsonString);
				mGetCustomerAddressJsonObject = new JSONObject(
						mGetCustomerAddressJsonString);
				mGetCustomerAddressJsonArray = mGetCustomerAddressJsonObject
						.optJSONArray("SODetails");
				int lengthCustomerJsonArr = mGetCustomerAddressJsonArray
						.length();
				if (lengthCustomerJsonArr > 0) {
					JSONObject jsonobject = mGetCustomerAddressJsonArray
							.getJSONObject(0);
					outletname = jsonobject.getString("CompanyName");
					address1 = jsonobject.getString("Address1");
					address2 = jsonobject.getString("Address2");
					address3 = jsonobject.getString("Address3");
					attention = jsonobject.getString("Attention");
					phoneNo = jsonobject.getString("PhoneNo");
					handphoneNo = jsonobject.getString("HandphoneNo");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Void arg0) {

			singleClickDialog(mposition);

			helper.dismissProgressView(mMerchandiseMainLayout);

			/*
			 * if(mFlag.matches("Assigned")){ HashMap<String, String> hashMap =
			 * new HashMap<String, String>(); hashMap.put("Address1", address1);
			 * hashMap.put("Address2", address2); hashMap.put("Address3",
			 * address3); hashMap.put("Attention", attention);
			 * hashMap.put("PhoneNo", phoneNo); hashMap.put("HandphoneNo",
			 * handphoneNo);
			 * 
			 * new GetMerchandise(hashMap).execute(mRefNo); }else
			 * if(mFlag.matches("Activity")){
			 * 
			 * 
			 * HashMap<String, String> hashMap = new HashMap<String, String>();
			 * hashMap.put("CustomerCode", mCustomerCode);
			 * hashMap.put("CustomerName", mCustomerName);
			 * hashMap.put("DeliveryCode", mDeliveryCode);
			 * hashMap.put("OutletName", mOutletName); hashMap.put("Status",
			 * mStatus); hashMap.put("RefNo", mRefNo); hashMap.put("RefDate",
			 * mRefDate); hashMap.put("Flag", mFlag); hashMap.put("Remarks1",
			 * mRemarks1); hashMap.put("Remarks2", mRemarks2);
			 * hashMap.put("Address1", address1); hashMap.put("Address2",
			 * address2); hashMap.put("Address3", address3);
			 * hashMap.put("Attention", attention); hashMap.put("PhoneNo",
			 * phoneNo); hashMap.put("HandphoneNo", handphoneNo);
			 * 
			 * helper.dismissProgressView(mMerchandiseMainLayout);
			 * mIntent.setClass(MerchandiseHeader.this,
			 * MerchandiseDetailHeader.class); mIntent.putExtra("HashMap",
			 * hashMap); startActivity(mIntent); finish();
			 */
			/*
			 * mIntent.putExtra("CustomerCode",mCustomerCode);
			 * mIntent.putExtra("CustomerName",mCustomerName);
			 * mIntent.putExtra("DeliveryCode",mDeliveryCode);
			 * mIntent.putExtra("OutletName",mOutletName);
			 * mIntent.putExtra("Status",mStatus);
			 * mIntent.putExtra("RefNo",mRefNo);
			 * mIntent.putExtra("RefDate",mRefDate);
			 * mIntent.putExtra("Flag",mFlag);
			 * mIntent.putExtra("Remarks1",mRemarks1);
			 * mIntent.putExtra("Remarks2",mRemarks2);
			 */

			// }

		}
	}

	private class GetMerchandise extends AsyncTask<String, Void, Void> {
		HashMap<String, String> hashMap = new HashMap<String, String>();

		public GetMerchandise(HashMap<String, String> hashmap) {
			hashMap = hashmap;
		}

		@Override
		protected void onPreExecute() {
			mHashMap.clear();

		}

		@Override
		protected Void doInBackground(String... params) {
			String refNo = params[0];
			try {
				mHashMap.put("CompanyCode", mCompanyCodeStr);
				mHashMap.put("RefNo", refNo);
				mHashMap.put("User", mUerIdStr);

				Log.d("mCompanyCodeStr", "" + mCompanyCodeStr);
				Log.d("RefNo", refNo);
				Log.d("mUerIdStr", "" + mUerIdStr);

				// Call GetMerchandise
				mMerchandiseJsonString = SalesOrderWebService.getSODetail(
						mHashMap, "fncGetMerchandise");
				mMerchandiseJsonObject = new JSONObject(mMerchandiseJsonString);
				mMerchandiseJsonArray = mMerchandiseJsonObject
						.optJSONArray("SODetails");

				int lengthMerchandiseJsonArr = mMerchandiseJsonArray.length();

				if (lengthMerchandiseJsonArr > 0) {
					JSONObject jsonChildNode = mMerchandiseJsonArray
							.getJSONObject(0);
					mRefNo = jsonChildNode.optString("RefNo").toString();
					mRefDate = jsonChildNode.optString("RefDate").toString();
					mCustomerCode = jsonChildNode.optString("CustomerCode")
							.toString();
					mCustomerName = jsonChildNode.optString("CustomerName")
							.toString();
					mStatus = jsonChildNode.optString("StatusCode").toString();
					mOutletName = jsonChildNode.optString("CompanyName")
							.toString();
					mDeliveryCode = jsonChildNode.optString("DeliveryCode")
							.toString();
					mRemarks1 = jsonChildNode.optString("Remarks1").toString();
					mRemarks2 = jsonChildNode.optString("Remarks2").toString();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Void arg0) {

			String address1 = hashMap.get("Address1");
			String address2 = hashMap.get("Address2");
			String address3 = hashMap.get("Address3");
			String attention = hashMap.get("Attention");
			String phoneNo = hashMap.get("PhoneNo");
			String handphoneNo = hashMap.get("HandphoneNo");

			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("CustomerCode", mCustomerCode);
			hashMap.put("CustomerName", mCustomerName);
			hashMap.put("DeliveryCode", mDeliveryCode);
			hashMap.put("OutletName", mOutletName);
			hashMap.put("Status", mStatus);
			hashMap.put("RefNo", mRefNo);
			hashMap.put("RefDate", mRefDate);
			hashMap.put("Flag", mFlag);
			hashMap.put("Remarks1", mRemarks1);
			hashMap.put("Remarks2", mRemarks2);
			hashMap.put("Address1", address1);
			hashMap.put("Address2", address2);
			hashMap.put("Address3", address3);
			hashMap.put("Attention", attention);
			hashMap.put("PhoneNo", phoneNo);
			hashMap.put("HandphoneNo", handphoneNo);

			mIntent.putExtra("HashMap", hashMap);

			startActivity(mIntent);
			finish();
			helper.dismissProgressView(mMerchandiseMainLayout);
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d("onConnected", "onConnected");

		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(100); // Update location every second

		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);

//		double lati=0,longi=0;

		if (mLastLocation != null) {
			lat = String.valueOf(mLastLocation.getLatitude());
			lon = String.valueOf(mLastLocation.getLongitude());

			if(lat!=null && !lat.isEmpty()){
				setLatitude = mLastLocation.getLatitude();
			}

			if(lon!=null && !lon.isEmpty()){
				setLongitude = mLastLocation.getLongitude();
			}
		}

		boolean interntConnection = isNetworkConnected();
		if (interntConnection == true) {

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if (isGPSEnabled == true) {
				Log.d("isGPSEnabled", ""+isGPSEnabled);
//				new getServerDateTime(lati, longi, "1").execute();

				updateUI(setLatitude,setLongitude);
			}else {
//				Log.d("gpsLocation", " null ");
//				new getServerDateTime(0, 0, "0").execute();
				updateUI(0,0);
//					Toast.makeText(getApplicationContext(),
//							"GPS is OFF. Please turn ON",
//							Toast.LENGTH_LONG).show();
			}

		} else {
			Toast.makeText(getApplicationContext(),
					"No Internet Connection",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onLocationChanged(Location location) {
		lat = String.valueOf(location.getLatitude());
		lon = String.valueOf(location.getLongitude());
//		updateUI();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		buildGoogleApiClient();
	}

	synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();


	}

	void updateUI(double lat,double lon) {
		try {
			Log.d("lat",""+lat);
			Log.d("lon",""+lon);

			mLatitude = lat;
			mLongitude = lon;

		}catch (Exception e){
			e.printStackTrace();
		}
	}


	/*public void getAddress(double latitude, double longitude) throws Exception {
		address1 = "";
		address2 = "";
		Geocoder geocoder;
		List<Address> addresses;
		try {
			boolean interntConnection = isNetworkConnected();
			if (interntConnection == true) {
				geocoder = new Geocoder(this, Locale.getDefault());

				addresses = geocoder.getFromLocation(latitude, longitude, 1);
				if (addresses != null && addresses.size() > 0) {

					address1 = addresses.get(0).getAddressLine(0);
					address2 = addresses.get(0).getAddressLine(1);

//					sm_location.setText(address1 + "," + address2);

					*//*Address address = addresses.get(0);
					ArrayList<String> addressFragments = new ArrayList<String>();

					// Fetch the address lines using getAddressLine,
					// join them, and send them to the thread.
					for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
						addressFragments.add(address.getAddressLine(i));
					}*//*

				}

//				Toast.makeText(
//						getApplicationContext(),
//						"Mobile Location : " + "\nLatitude: "
//								+ latitude + "\nLongitude: "
//								+ longitude + "\nAddress 1: "
//								+ address1 + "\nAddress 2: "
//								+ address2, Toast.LENGTH_LONG)
//						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	@Override
	public void onBackPressed() {
		mIntent.setClass(MerchandiseHeader.this, LandingActivity.class);
		startActivity(mIntent);
		finish();

	}
}
