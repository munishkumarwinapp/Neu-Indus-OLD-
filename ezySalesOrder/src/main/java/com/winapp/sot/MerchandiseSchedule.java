package com.winapp.sot;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.Schedule;
import com.winapp.model.ScheduleDate;
import com.winapp.printer.UIHelper;

public class MerchandiseSchedule extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace, OnClickListener,OnChildClickListener,OnGroupClickListener {

	private MerchandiseScheduleDateAdapter adapter;
	private ViewPager pager;
	private ArrayList<ScheduleDate> dates;
	private SlidingMenu menu;
	private ActionBar mActionBar;
	private View customActionBarView;
	private TextView mTitle;
	private Intent mIntent;
	private ImageButton mPrinterImgBtn, mAddImgBtn, mSearchImgBtn;
	private RelativeLayout mMechandiseScheduleLayout;
	private UIHelper helper;
	private String mCompanyCodeStr = "", mValidUrl = "",mSelectedVan="",
			mServerDateJsonString = "", mServerDate = "",mUerIdStr="",
			mSchedulingJsonString = "",mSaveSchedulingDeleteJsonString="",todayDate="";
	private HashMap<String, String> mHashMap;
	private JSONObject mServerDateJsonObject, mSchedulingJsonObject,mSaveSchedulingDeleteJsonObject;
	private JSONArray mServerDateJsonArray, mSchedulingJsonArray,mSaveSchedulingDeleteJsonArray;
	private SimpleDateFormat mDateFormat1, mDateFormat2;
	private boolean isServerDateCall = true;
	private ExpandableListView mExpandableListView;
	private MerchandiseScheduleAdapter mMerchandiseScheduleAdapter;
	private ArrayList<Schedule> mScheduleUserChild;
	private ArrayList<SO> mScheduleArrList,scheduleChildList;
	private ArrayList<String> mScheduleUserArrList;
	private TextView mNoSchedule;
   // private LinearLayout mExpandableHeader;
    private LinkedHashSet<String> lhs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(R.drawable.ic_menu);
		setContentView(R.layout.activity_merchandise_schedule);
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
		mTitle.setText("Merchandise Schedule");
		// Custom ActionBarView IB
		mPrinterImgBtn = (ImageButton) customActionBarView
				.findViewById(R.id.printer);
		mAddImgBtn = (ImageButton) customActionBarView
				.findViewById(R.id.custcode_img);
		mSearchImgBtn = (ImageButton) customActionBarView
				.findViewById(R.id.search_img);
		mPrinterImgBtn.setVisibility(View.GONE);
		mSearchImgBtn.setVisibility(View.GONE);
		mAddImgBtn.setVisibility(View.VISIBLE);

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
		pager = (ViewPager) findViewById(R.id.pager);
		mMechandiseScheduleLayout = (RelativeLayout) findViewById(R.id.mechandiseSchedule);
		mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView1);
		mNoSchedule = (TextView) findViewById(R.id.no_schedule);
		//mExpandableHeader = (LinearLayout) findViewById(R.id.header);

		// Object Initialization
		dates = new ArrayList<ScheduleDate>();
		mIntent = new Intent();
		mHashMap = new HashMap<String, String>();
		helper = new UIHelper(MerchandiseSchedule.this);		
		mScheduleArrList = new ArrayList<SO>();
		mScheduleUserArrList = new ArrayList<String>();

		// Get URL from DB
		FWMSSettingsDatabase.init(MerchandiseSchedule.this);
		mValidUrl = FWMSSettingsDatabase.getUrl();
		new SalesOrderWebService(mValidUrl);
		mSelectedVan = SOTDatabase.getVandriver();
		// Get CompanyCode from Pojo class
		mCompanyCodeStr = SupplierSetterGetter.getCompanyCode();
		
		// Get UserId from Pojo class
		mUerIdStr = SupplierSetterGetter.getUsername();

		mDateFormat1 = new SimpleDateFormat("dd/MMMM/yyyy");
		mDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

		// OnClickListener
		mAddImgBtn.setOnClickListener(this);
		mExpandableListView.setOnChildClickListener(this);
		mExpandableListView.setOnGroupClickListener(this);
		
		
		new GetSchedulingData().execute();
		}

	private void initViewPager(String serverDate) {
		adapter = new MerchandiseScheduleDateAdapter(this, getDates());
		int position = getCurrentDateSelection(serverDate);
		adapter.setCurrentItem(position);
		pager.setAdapter(adapter);
		if(position>0){
			pager.setCurrentItem(position-2);
		}
        if(pager.getVisibility()==View.INVISIBLE){
        	pager.setVisibility(View.VISIBLE);
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
			System.out.println(date);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.custcode_img:
			mIntent.setClass(MerchandiseSchedule.this,
					MerchandiseAddSchedule.class);
			startActivity(mIntent);
            finish();
			break;

		default:
			break;
		}

	}

	// Handle View Pager click to show selected Date:
	public void onPagerItemClick(View view, int index) {
		System.out.println("" + index);
		try {
			ScheduleDate merchandiseScheduleDate = this.dates
					.get(index);
			String selectedDate = merchandiseScheduleDate.getDate() + "/"
					+ merchandiseScheduleDate.getMonth() + "/"
					+ merchandiseScheduleDate.getYear();

			Log.d("selectedDate", "/->" + selectedDate);
			adapter.setCurrentItem(index);
			adapter.notifyDataSetChanged();

			String reformattedDate = mDateFormat2.format(mDateFormat1.parse(selectedDate));
			if (!isServerDateCall) {
				new GetSchedulingData().execute(reformattedDate);
				Log.d("reformattedDate", "false/->" + reformattedDate);
			} else {
				isServerDateCall = false;
				Log.d("reformattedDate", "true /->" + reformattedDate);
			}
			//Set Merchandise Schedule Date
			SalesOrderSetGet.setServerDate(reformattedDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// pager.setAdapter(adapter);
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

	private class GetSchedulingData extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {	
			//mScheduleUserChild.clear();
			//mScheduleUserHeader.clear();
			mScheduleArrList.clear();						
			mScheduleUserArrList.clear();		
			helper.showProgressView(mMechandiseScheduleLayout);
		}

		@Override
		protected Void doInBackground(String... params) {

			mHashMap.put("CompanyCode", mCompanyCodeStr);
			if (isServerDateCall) {
				try {
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
					todayDate = mServerDate;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				mServerDate = params[0];
			}
			mHashMap.put("ScheduleDate", mServerDate);
			mHashMap.put("VanCode", mSelectedVan);			
			// Call GetScheduling
			try {
				mSchedulingJsonString = SalesOrderWebService.getSODetail(mHashMap, "fncGetScheduling");				
				mSchedulingJsonObject = new JSONObject(mSchedulingJsonString);
				mSchedulingJsonArray = mSchedulingJsonObject.optJSONArray("SODetails");
				int lengthSchedulingJsonArr = mSchedulingJsonArray.length();
				if (lengthSchedulingJsonArr > 0) {
					for (int i = 0; i < lengthSchedulingJsonArr; i++) {
						JSONObject jsonChildNode = mSchedulingJsonArray
								.getJSONObject(i);
						SO so = new SO();
						String scheduleNo = jsonChildNode.optString(
								"ScheduleNo").toString();
						String scheduleUser = jsonChildNode.optString(
								"ScheduleUser").toString();
						String scheduleDate = jsonChildNode.optString(
								"ScheduleDate").toString().split("\\ ")[0];
						String customerCode = jsonChildNode.optString(
								"CustomerCode").toString();
						String customerName = jsonChildNode.optString(
								"CustomerName").toString();
						String custAddress1 = jsonChildNode.optString(
								"CustAddress1").toString();
						String deliveryCode = jsonChildNode.optString(
								"DeliveryCode").toString();
						String outletName = jsonChildNode.optString(
								"OutletName").toString();
						String outletAddress = jsonChildNode.optString("OutAddress1").toString();
						String sortOrder = jsonChildNode.optString("SortOrder")
								.toString();
			     so.setIsJobClosed(jsonChildNode.optString(
					        "IsJobClosed").toString());
						so.setScheduleNo(scheduleNo);
						so.setScheduleUser(scheduleUser);
						so.setSortOrder(sortOrder);
                        so.setDate(scheduleDate);
                        so.setCustomerName(customerName);
                        so.setAddress(custAddress1);
                        so.setOutLetName(outletName);
                        so.setOutletAddress(outletAddress);						
						so.setCustomerCode(customerCode);
						so.setDeliveryCode(deliveryCode);						
						mScheduleArrList.add(so);						
						mScheduleUserArrList.add(scheduleUser);						
						
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void arg0) {
			helper.dismissProgressView(mMechandiseScheduleLayout);
			// Set Server
			if (isServerDateCall) {
				try {
					String reformattedServerDate = mDateFormat1
							.format(mDateFormat2.parse(mServerDate));
					Log.d("reformattedDate", "->" + reformattedServerDate);
					initViewPager(reformattedServerDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}	
			if (mScheduleArrList.size() > 0) {
				loadScheduleData();
				mExpandableListView.setVisibility(View.VISIBLE);
				mNoSchedule.setVisibility(View.GONE);
			}else{
				mExpandableListView.setVisibility(View.GONE);
				mNoSchedule.setVisibility(View.VISIBLE);
			}			
		}

	
private void loadScheduleData(){	 
	// Creating LinkedHashSet
    lhs = new LinkedHashSet<String>();
    
    /* Adding ArrayList elements to the LinkedHashSet
     * in order to remove the duplicate elements and 
     * to preserve the insertion order.
     */
    lhs.addAll(mScheduleUserArrList);
 
    // Removing ArrayList elements
    mScheduleUserArrList.clear();

    // Adding LinkedHashSet elements to the ArrayList
    mScheduleUserArrList.addAll(lhs);

    // Displaying ArrayList elements
    System.out.println("ArrayList contains: "+mScheduleUserArrList);    
   
    mScheduleUserChild = new ArrayList<Schedule>();
    for(String scheduleUser : mScheduleUserArrList){
    	 scheduleChildList = new ArrayList<SO>();
    	for(SO so : mScheduleArrList){     		
        	if(scheduleUser.matches(so.getScheduleUser())){        		
        		scheduleChildList.add(so);
        	} 
        }    	
    	Schedule schedule = new Schedule(scheduleUser,scheduleChildList);
		mScheduleUserChild.add(schedule);
    }	
		// create the adapter by passing your ArrayList data
		mMerchandiseScheduleAdapter = new MerchandiseScheduleAdapter(MerchandiseSchedule.this, mScheduleUserChild);
		// attach the adapter to the list
		mExpandableListView.setAdapter(mMerchandiseScheduleAdapter);
		// expand all Groups
		 expandAll();
	
}
//method to expand all groups
	private void expandAll() {
		int count = mMerchandiseScheduleAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			mExpandableListView.expandGroup(i);
		}
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
					Log.d("selectedDate", "/->" + selectedDate);
					String reformattedDate = mDateFormat2.format(mDateFormat1.parse(selectedDate));					
					if(reformattedDate.matches(todayDate)){
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

	public class MerchandiseScheduleAdapter extends BaseExpandableListAdapter {

		private Context context;
		private ArrayList<Schedule> scheduleList;
		private boolean isEmptyOutletAddress = false,isEmptyOutletName = false;
		public MerchandiseScheduleAdapter(Context context,
				ArrayList<Schedule> scheduleArrList) {
			this.context = context;
			scheduleList = new ArrayList<Schedule>();
			this.scheduleList = scheduleArrList;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			ArrayList<SO> productList = scheduleList.get(groupPosition)
					.getScheduleList();
			return productList.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View view, ViewGroup parent) {
//			View row = view;
			SO detailInfo = (SO) getChild(groupPosition, childPosition);
			if (view == null) {
				LayoutInflater infalInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = infalInflater.inflate(
						R.layout.exp_child_item_merchandise_schedule, null);
			}

			LinearLayout mer_layout = (LinearLayout) view.findViewById(R.id.mer_layout);
			TextView customerName = (TextView) view.findViewById(R.id.customerName);
			TextView customerAddress = (TextView) view.findViewById(R.id.customerAddress);
			TextView outletName = (TextView) view.findViewById(R.id.outletName);
			TextView outletAddress = (TextView) view.findViewById(R.id.outletAddress);
			TextView orderNo = (TextView) view.findViewById(R.id.orderNo);
			TextView outlet = (TextView) view.findViewById(R.id.outlet);			
			customerName.setText(detailInfo.getCustomerName().trim());
		 String isJobClosed = detailInfo.getIsJobClosed();

   		if (isJobClosed != null && !isJobClosed.isEmpty()) {
   		 if (isJobClosed.equalsIgnoreCase("0")) {
   		  //holder.status.setText("Open");
			 mer_layout.setBackgroundResource(R.drawable.listbg_patch);
   			 } else if (isJobClosed.equalsIgnoreCase("1")) {
   			  //holder.status.setText("In Progress");
			 mer_layout.setBackgroundColor(Color.parseColor("#f4cf6d"));
  			  } else if (isJobClosed.matches("2")) {
   				  //holder.status.setText("Closed");
			 mer_layout.setBackgroundColor(Color.parseColor("#88e5ad"));
   			 }
  			 } else {
  			  //holder.status.setText("Open");
			mer_layout.setBackgroundResource(R.drawable.listbg_patch);
  			 }
			if(detailInfo.getAddress()!=null && !detailInfo.getAddress().isEmpty()){
				customerAddress.setText(detailInfo.getAddress().trim());
				customerAddress.setVisibility(View.GONE);
			}else{
				customerAddress.setText("-");
				customerAddress.setVisibility(View.GONE);
			}
			if(detailInfo.getOutLetName()!=null && !detailInfo.getOutLetName().isEmpty()){
				outletName.setText(detailInfo.getOutLetName().trim());
				outletName.setVisibility(View.VISIBLE);
				isEmptyOutletName = false;
			}else{
				outletName.setText("-");
				outletName.setVisibility(View.INVISIBLE);
				isEmptyOutletName = true;
			}
			if(detailInfo.getOutletAddress()!=null && !detailInfo.getOutletAddress().isEmpty()){
				outletAddress.setText(detailInfo.getOutletAddress().trim());
				outletAddress.setVisibility(View.VISIBLE);
				isEmptyOutletAddress = false;
			}else{
				outletAddress.setText("-");
				outletAddress.setVisibility(View.INVISIBLE);
				isEmptyOutletAddress = true;
			}
		    if(isEmptyOutletName && isEmptyOutletAddress){
		    	outlet.setVisibility(View.INVISIBLE);
		    }		    
		    else{
		    	outlet.setVisibility(View.VISIBLE);
		    }
 			orderNo.setText(detailInfo.getSortOrder().trim());
		
			return view;
		}

		@Override
		public int getChildrenCount(int groupPosition) {

			ArrayList<SO> schList = scheduleList.get(groupPosition)
					.getScheduleList();
			return schList.size();

		}

		@Override
		public Object getGroup(int groupPosition) {
			return scheduleList.get(groupPosition);		
		}

		@Override
		public int getGroupCount() {
			return scheduleList.size();
			//return  (null != scheduleList ? scheduleList.size() : 0);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isLastChild,
				View view, ViewGroup parent) {

			Schedule headerInfo = (Schedule) getGroup(groupPosition);
			if (view == null) {
				LayoutInflater inf = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inf.inflate(R.layout.exp_group_row_merchandise_schedule,
						null);
			}

			TextView heading = (TextView) view.findViewById(R.id.heading);
			heading.setText(headerInfo.getName().trim());

			return view;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public void remove(int groupPosition, int childPosition) {
			ArrayList<SO> productList = scheduleList.get(groupPosition).getScheduleList();
			//Child Remove
			productList.remove(childPosition);	
			
			int childCount = getChildrenCount(groupPosition);
			Log.d("childCount", "-->"+childCount);
			if(childCount == 0){
				Schedule headerInfo = (Schedule) getGroup(groupPosition);
				scheduleList.remove(headerInfo);
			}
			
			notifyDataSetChanged();
			
		}		
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, final View v,
			final int groupPosition, final int childPosition, long id) {

		
		 final CharSequence[] items = {"Edit", "Delete"};

	        AlertDialog.Builder builder = new AlertDialog.Builder(MerchandiseSchedule.this);
	        builder.setTitle("Make your selection");
	        builder.setItems(items, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	               if(items[item].equals("Edit")){
	            	   alertEditDialog(groupPosition, childPosition);
	               }else if(items[item].equals("Delete")){
	            	   alertDeleteDialog(groupPosition, childPosition);
	            		//mMerchandiseScheduleAdapter.showPopup(v,groupPosition,childPosition); 
	            		
	               }
	                
	            }
	        });
	        AlertDialog alert = builder.create();
	        alert.show();	
		return false;
	
	}
	public void alertDeleteDialog(final int groupPosition, final int childPosition) {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Delete");
		alertDialog.setMessage("Do you want to Delete");
		alertDialog.setIcon(R.mipmap.delete);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SO so = (SO) mMerchandiseScheduleAdapter.getChild(groupPosition, childPosition);
						String scheduleNo = so.getScheduleNo();
						new SaveSchedulingDelete(groupPosition,childPosition).execute(scheduleNo);
						
					
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
	public void alertEditDialog(final int groupPosition, final int childPosition) {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Edit");
		alertDialog.setMessage("Do you want to Edit");
		alertDialog.setIcon(R.mipmap.ic_edit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						HashMap<String, String> hm = new HashMap<String, String>();
						SO so = (SO) mMerchandiseScheduleAdapter.getChild(groupPosition, childPosition);
						String scheduleNo = so.getScheduleNo();
						String scheduleDate = so.getDate();
						String scheduleUser = so.getScheduleUser();
						String customerCode= so.getCustomerCode();
						String customerName= so.getCustomerName();
						String customerAddress= so.getAddress();
						String outletName= so.getOutLetName();
						String outletAddress= so.getOutletAddress();
						String deliveryCode= so.getDeliveryCode();
						String sortOrder = so.getSortOrder();						
						
						hm.put("ScheduleNo", scheduleNo);
						hm.put("ScheduleDate", scheduleDate);
						hm.put("ScheduleUser", scheduleUser);
						hm.put("CustomerCode", customerCode);
						hm.put("CustomerName", customerName);
						hm.put("CustomerAddress", customerAddress);
						hm.put("OutletName", outletName);
						hm.put("OutletAddress", outletAddress);
						hm.put("DeliveryCode", deliveryCode);
						hm.put("SortOrder", sortOrder);
											
						mIntent.setClass(MerchandiseSchedule.this,
								MerchandiseAddSchedule.class);
						mIntent.putExtra("Values", hm);						
						startActivity(mIntent);					
						
						
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
	private class SaveSchedulingDelete extends
			AsyncTask<String, String, String> {
		private int groupPosition, childPosition;
		private String result = "", scheduleUser = "";

		public SaveSchedulingDelete(int groupPosition, int childPosition) {
			this.groupPosition = groupPosition;
			this.childPosition = childPosition;
		}

		protected void onPreExecute() {

			helper.showProgressView(mMechandiseScheduleLayout);
		}

		@Override
		protected String doInBackground(String... params) {
			scheduleUser = params[0];
			mHashMap.put("CompanyCode", mCompanyCodeStr);
			mHashMap.put("ScheduleNo", scheduleUser);
			mHashMap.put("User", mUerIdStr);

			// Call SaveSchedulingDelete
			try {
				mSaveSchedulingDeleteJsonString = SalesOrderWebService
						.getSODetail(mHashMap, "fncSaveSchedulingDelete");
				mSaveSchedulingDeleteJsonObject = new JSONObject(
						mSaveSchedulingDeleteJsonString);
				mSaveSchedulingDeleteJsonArray = mSaveSchedulingDeleteJsonObject
						.optJSONArray("SODetails");

				int lengthSaveSchedulingDeleteJsonArr = mSaveSchedulingDeleteJsonArray
						.length();
				Log.d("lengthSchedulingJsonArr", ""
						+ lengthSaveSchedulingDeleteJsonArr);
				if (lengthSaveSchedulingDeleteJsonArr > 0) {
					JSONObject jsonChildNode = mSaveSchedulingDeleteJsonArray
							.getJSONObject(0);
					result = jsonChildNode.optString("Result").toString();

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null && !result.isEmpty()) {
				mMerchandiseScheduleAdapter
						.remove(groupPosition, childPosition);
				Toast.makeText(MerchandiseSchedule.this,
						"Deleted Successfully", Toast.LENGTH_SHORT).show();

			} else {
				Toast.makeText(MerchandiseSchedule.this, "failed",
						Toast.LENGTH_SHORT).show();
			}

			helper.dismissProgressView(mMechandiseScheduleLayout);

		}
	}
	@Override
	public void onBackPressed() {			
		mIntent.setClass(MerchandiseSchedule.this, LandingActivity.class);
		startActivity(mIntent);
		finish();
		
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		// TODO Auto-generated method stub
		return true; // This way the expander cannot be collapsed
	}
}
