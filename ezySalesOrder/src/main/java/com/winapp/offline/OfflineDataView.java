package com.winapp.offline;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.Settings;
import com.winapp.helper.DateTime;
import com.winapp.sot.OfflineSalesOrderWebService;
import com.winapp.sot.SO;

public class OfflineDataView extends Activity{
	private TextView mFromDate,mToDate,mNo,mDate;
	private Button mSearchBtn;
	private Calendar mFromCalendar,mToCalendar;
	private	DatePickerDialog.OnDateSetListener mFromDatePicker,mToDatePicker; 
	private String simpledateFormat = "dd/MM/yyyy",regatedateFormat = "yyyy-MM-dd",currentDate,jsonString,onlineMode;
	private SimpleDateFormat simpleDateFormat,regateDateFormat;
	private HashMap<String, String> param;
	private Spinner mSpinnerData,mSpinnerDataStatus;
	private JSONObject jsonResponse;
	private JSONArray jsonMainNode;
	private ArrayList<SO> mOfflineData;
	private OfflineDataAdapter mOfflineDataAdapter;
	private ListView mListView;
	private HorizontalScrollView mHorizontalScrollView;
	private LinearLayout mEmpty, mDateLayout, mNoHeaderLayout,
			mDateHeaderLayout,mOfflineSearchlayout, spinnerLayout,mParentLayout,mOfflineStatusLayout,/*mCustCodeHeaderLayout, mCustNameHeaderLayout,*/
			mAmountHeaderLayout;
	private ImageButton mSearch,mBack;
	private Intent mIntent;
	private ProgressBar progressBar;
	//private OfflineCommon offlineCommon;
	//private OfflineDatabase offlineDatabase;
	//private OfflineCustomerSync offlinecustomerSynch;
	private boolean checkOffline;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Layout
		setContentView(R.layout.activity_offline_data_view);
		
		//Id
		/***************TextView ************************/
		mFromDate = (TextView) findViewById(R.id.fromDate);		
		mToDate = (TextView) findViewById(R.id.toDate);	
		
		mNo = (TextView) findViewById(R.id.no);	
		mDate = (TextView) findViewById(R.id.date);	
		
		mEmpty = (LinearLayout) findViewById(R.id.empty_txt);
		
		mDateLayout = (LinearLayout) findViewById(R.id.datelayout);
		
		mNoHeaderLayout = (LinearLayout) findViewById(R.id.noheaderLayout);
		mDateHeaderLayout = (LinearLayout) findViewById(R.id.dateheaderLayout);
		//mCustCodeHeaderLayout = (LinearLayout) findViewById(R.id.custcodeheaderLayout);
		//mCustNameHeaderLayout = (LinearLayout) findViewById(R.id.custNameheaderLayout);
		mAmountHeaderLayout = (LinearLayout) findViewById(R.id.amountheaderLayout);
		
		mOfflineSearchlayout = (LinearLayout) findViewById(R.id.offlineSearchlayout);
		
		mOfflineStatusLayout = (LinearLayout) findViewById(R.id.offlineStatusLayout);
		mParentLayout = (LinearLayout) findViewById(R.id.parentLayout);
		
		
		mSearch = (ImageButton) findViewById(R.id.search);
		mBack = (ImageButton) findViewById(R.id.back);
		
		/***************Button ************************/
		mSearchBtn  = (Button) findViewById(R.id.btnSearch);
		
		
		/***************Spinner ************************/
		mSpinnerData  = (Spinner) findViewById(R.id.offline_data);
		mSpinnerDataStatus  = (Spinner) findViewById(R.id.offline_status);
		
		mListView = (ListView) findViewById(android.R.id.list);
		
		mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalscrollview);
		
		
		
		param = new HashMap<String, String>();	
		mOfflineData = new ArrayList<SO>();
		simpleDateFormat  = new SimpleDateFormat(simpledateFormat, Locale.US);
		regateDateFormat  = new SimpleDateFormat(regatedateFormat, Locale.US);
		mFromCalendar = Calendar.getInstance();
		mToCalendar = Calendar.getInstance();
		OfflineDatabase.init(OfflineDataView.this);
		// @Offline
					onlineMode = OfflineDatabase.getOnlineMode();
					//offlineDatabase = new OfflineDatabase(OfflineDataView.this);
					//offlineCommon = new OfflineCommon(OfflineDataView.this);
					checkOffline = OfflineCommon.isConnected(OfflineDataView.this);
					new OfflineSalesOrderWebService(OfflineDataView.this);
					//offlinecustomerSynch = new OfflineCustomerSync(OfflineDataView.this);
		/***************Method ********************/
		setUpUI();
		loadDataContent();
	}
	public void setUpUI(){
		//FromDatePicker
		mFromDatePicker = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				mFromCalendar.set(Calendar.YEAR, year);
				mFromCalendar.set(Calendar.MONTH, monthOfYear);
				mFromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				mFromDate.setText(simpleDateFormat.format(mFromCalendar.getTime()));
			}			
		};
		//ToDatePicker
		mToDatePicker = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				mToCalendar.set(Calendar.YEAR, year);
				mToCalendar.set(Calendar.MONTH, monthOfYear);
				mToCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				mToDate.setText(simpleDateFormat.format(mToCalendar.getTime()));
			}
		};
		//FromDate TextView Touch Listener
		mFromDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(OfflineDataView.this, mFromDatePicker,
							mFromCalendar.get(Calendar.YEAR), mFromCalendar
									.get(Calendar.MONTH), mFromCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		//ToDate TextView Touch Listener
		mToDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(OfflineDataView.this, mToDatePicker,
							mToCalendar.get(Calendar.YEAR), mToCalendar
									.get(Calendar.MONTH), mToCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		//Search Button Click
		mSearchBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				new offlineData().execute();
			}
		});
		mSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				
				if(mOfflineSearchlayout.getVisibility()==View.GONE){
					mOfflineSearchlayout.setVisibility(View.VISIBLE);
				}else{
					mOfflineSearchlayout.setVisibility(View.GONE);
				}
			}
		});
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				mIntent = new Intent(OfflineDataView.this,Settings.class);
				startActivity(mIntent);
				finish();
			}
		});
		mSpinnerData.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
					int position, long id) {
				String spinnerData = mSpinnerData.getSelectedItem().toString();		
				if(spinnerData.matches("Customer")||spinnerData.matches("Products")){
					mDateLayout.setVisibility(View.GONE);
				}else{
					mDateLayout.setVisibility(View.VISIBLE);
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}
	
	public void loadDataContent(){
		currentDate = simpleDateFormat.format(new Date());
		mFromDate.setText(DateTime.date(currentDate));
		mToDate.setText(currentDate);
		
		onlineMode = OfflineDatabase.getOnlineMode();
		if (onlineMode.matches("True")) {
			mOfflineStatusLayout.setVisibility(View.GONE);
			
			if (checkOffline == true) {
				
				mOfflineStatusLayout.setVisibility(View.VISIBLE);
				mOfflineStatusLayout.setBackgroundResource(drawable.temp_offline_pattern_bg);
				
			}
			
		} else if (onlineMode.matches("False")) {
			mOfflineStatusLayout.setVisibility(View.VISIBLE);
		}
		new offlineData().execute();
	}
	private class offlineData extends AsyncTask<Void, Void, Void> {
		String spinnerData;
		@Override
		protected void onPreExecute() {
			
	spinnerLayout = new LinearLayout(OfflineDataView.this);
	spinnerLayout.setGravity(Gravity.CENTER);
	addContentView(spinnerLayout, new LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT));
	spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
	enableViews(mParentLayout, false);
	progressBar = new ProgressBar(OfflineDataView.this);
	progressBar.setProgress(android.R.attr.progressBarStyle);
	progressBar.setIndeterminateDrawable(getResources().getDrawable(
			drawable.greenprogress));
	spinnerLayout.addView(progressBar);
		}
		@Override
		protected Void doInBackground(Void... arg0) {

			
			mOfflineData.clear();
			try {
				Date startDate  = simpleDateFormat.parse(mFromDate.getText().toString());
				Date endDate = simpleDateFormat.parse(mToDate.getText().toString());							
				 
				String from = regateDateFormat.format(startDate)+" 00:00:00"; 
				String to = regateDateFormat.format(endDate)+" 24:00:00";
				
					
				
				 spinnerData = mSpinnerData.getSelectedItem().toString();			
				String spinnerDataStatus = mSpinnerDataStatus.getSelectedItem().toString();
				
				if(spinnerData.matches("Invoice")){
					param.put("TableName","tblGetInvoiceHeader");
					param.put("No", "InvoiceNo");
					param.put("Date", "InvoiceDate");
					param.put("Flag", "Invoice");
					param.put("FromDate",from);
					param.put("ToDate",to);	
						
				}
				else if(spinnerData.matches("Receipt")){
					param.put("TableName","tblGetReceiptHeader");
					param.put("No", "ReceiptNo");
					param.put("Date", "ReceiptDate");
					param.put("Flag", "Receipt");
					param.put("FromDate",from);
					param.put("ToDate",to);	
				}
				else if(spinnerData.matches("Customer")){
					param.put("TableName","tblGetCustomer");
					param.put("No", null);
					param.put("Date", null);
					param.put("Flag", "Customer");
					param.put("FromDate",null);
					param.put("ToDate",null);	
				}
				else if(spinnerData.matches("SalesOrder")){
					param.put("TableName","tblGetSOHeader");
					param.put("No", "SoNo");
					param.put("Date", "SoDate");
					param.put("Flag", "SalesOrder");
					param.put("FromDate",from);
					param.put("ToDate",to);	
				}
				else if(spinnerData.matches("Products")){
					param.put("TableName","tblGetProduct");
					param.put("No", null);
					param.put("Date", null);
					param.put("Flag", "Products");
					param.put("FromDate",null);
					param.put("ToDate",null);	
				}
				if(spinnerDataStatus.matches("Pending To Upload")){
					param.put("DownStatus", "1");
				}
				else {
					param.put("DownStatus", null);
				}			
					
					
				jsonString = 	OfflineDatabase.getHeaderData(param);
				
				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");
			
			
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					/****** Get Object for each JSON node. ***********/
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
					SO so = new SO();
					if(spinnerData.matches("Customer")){
						so.setCustomerCode(jsonChildNode.optString("CustomerCode").toString());
						so.setCustomerName(jsonChildNode.optString("CustomerName").toString());				
						
					}
					else if(spinnerData.matches("Products")){
						so.setProductCode(jsonChildNode.optString("ProductCode").toString());
						so.setProductName(jsonChildNode.optString("ProductName").toString());
					}	
					
					else{
						so.setSno(jsonChildNode.optString("No").toString());
						so.setCustomerCode(jsonChildNode.optString("CustomerCode").toString());
					    so.setCustomerName(jsonChildNode.optString("CustomerName").toString());
						String date = jsonChildNode.optString("Date").toString();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						Date myDate = dateFormat.parse(date);
						SimpleDateFormat timeFormat = new SimpleDateFormat(
								"dd/MM/yyyy");
						String finalDate = timeFormat.format(myDate);
						StringTokenizer tokens = new StringTokenizer(finalDate, " ");
						String dateStr = tokens.nextToken();
						
						
						so.setDate(dateStr);
						
						so.setNettotal(jsonChildNode.optString("NetTotal").toString());
					}
				
					mOfflineData.add(so);
				}
				
				
				
				
			} catch (ParseException e) {
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			if(mOfflineData.size()>0){
				mOfflineDataAdapter = new OfflineDataAdapter(OfflineDataView.this, R.layout.activity_offline_data_view_list_item, mOfflineData,spinnerData);
				mListView.setAdapter(mOfflineDataAdapter);
				mOfflineDataAdapter.notifyDataSetChanged();
								
				if(spinnerData.matches("Customer")){
					mNoHeaderLayout.setVisibility(View.GONE); 
					mDateHeaderLayout.setVisibility(View.GONE); 			
					mAmountHeaderLayout.setVisibility(View.GONE);
				}
				else if(spinnerData.matches("Products")){					
					((TextView) findViewById(R.id.code)).setText("Product Code");
					((TextView) findViewById(R.id.name)).setText("Product Name");
					mNoHeaderLayout.setVisibility(View.GONE); 
					mDateHeaderLayout.setVisibility(View.GONE); 			
					mAmountHeaderLayout.setVisibility(View.GONE);
				}
				else{
					mNoHeaderLayout.setVisibility(View.VISIBLE); 
					mDateHeaderLayout.setVisibility(View.VISIBLE); 			
					mAmountHeaderLayout.setVisibility(View.VISIBLE); 
					if(spinnerData.matches("Invoice")){			
						mNo.setText("Invoice No");
						mDate.setText("Invoice Date"); 
					}
					else if(spinnerData.matches("Receipt")){
						mNo.setText("Receipt No");
						mDate.setText("Receipt Date"); 
					}
					else if(spinnerData.matches("SalesOrder")){
						mNo.setText("SalesOrder No");
						mDate.setText("SalesOrder Date"); 
					}
				}
				mEmpty.setVisibility(View.GONE);
				mHorizontalScrollView.setVisibility(View.VISIBLE);
				
				
				
			}else{
				mEmpty.setVisibility(View.VISIBLE);
				mHorizontalScrollView.setVisibility(View.GONE);
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(mParentLayout, true);
		}
	}
/*	@SuppressWarnings("deprecation")
	private void loadSearchData() {
		
		mOfflineData = new ArrayList<SO>();
		try {
			Date startDate  = simpleDateFormat.parse(mFromDate.getText().toString());
			Date endDate = simpleDateFormat.parse(mToDate.getText().toString());							
			 
			String from = regateDateFormat.format(startDate)+" 00:00:00"; 
			String to = regateDateFormat.format(endDate)+" 24:00:00";
			
				
			
			String spinnerData = mSpinnerData.getSelectedItem().toString();			
			String spinnerDataStatus = mSpinnerDataStatus.getSelectedItem().toString();
			
			if(spinnerData.matches("Invoice")){
				param.put("TableName","tblGetInvoiceHeader");
				param.put("No", "InvoiceNo");
				param.put("Date", "InvoiceDate");
				param.put("Flag", "Invoice");
				param.put("FromDate",from);
				param.put("ToDate",to);	
					
			}
			else if(spinnerData.matches("Receipt")){
				param.put("TableName","tblGetReceiptHeader");
				param.put("No", "ReceiptNo");
				param.put("Date", "ReceiptDate");
				param.put("Flag", "Receipt");
				param.put("FromDate",from);
				param.put("ToDate",to);	
			}
			else if(spinnerData.matches("Customer")){
				param.put("TableName","tblGetCustomer");
				param.put("No", null);
				param.put("Date", null);
				param.put("Flag", "Customer");
				param.put("FromDate",null);
				param.put("ToDate",null);	
			}
			
			if(spinnerDataStatus.matches("Pending")){
				param.put("DownStatus", "1");
			}
			else {
				param.put("DownStatus", null);
			}			
				
				
			jsonString = 	OfflineDatabase.getHeaderData(param);
			
			jsonResponse = new JSONObject(jsonString);
			jsonMainNode = jsonResponse.optJSONArray("SODetails");
		
		
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				*//****** Get Object for each JSON node. ***********//*
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				SO so = new SO();
				if(!spinnerData.matches("Customer")){
					so.setCustomerCode(jsonChildNode.optString("CustomerCode").toString());
					so.setCustomerName(jsonChildNode.optString("CustomerName").toString());				
					
				}else{
					so.setSno(jsonChildNode.optString("No").toString());
					so.setDate(jsonChildNode.optString("Date").toString());
					
					so.setNettotal(jsonChildNode.optString("NetTotal").toString());
				}
			so.setCustomerCode(jsonChildNode.optString("CustomerCode").toString());
			so.setCustomerName(jsonChildNode.optString("CustomerName").toString());
				
				mOfflineData.add(so);
			}
			
			if(mOfflineData.size()>0){
				mOfflineDataAdapter = new OfflineDataAdapter(OfflineDataView.this, R.layout.activity_offline_data_view_list_item, mOfflineData,spinnerData);
				mListView.setAdapter(mOfflineDataAdapter);
				mOfflineDataAdapter.notifyDataSetChanged();
								
				if(spinnerData.matches("Customer")){
					mNoHeaderLayout.setVisibility(View.GONE); 
					mDateHeaderLayout.setVisibility(View.GONE); 			
					mAmountHeaderLayout.setVisibility(View.GONE);
				}else{
					mNoHeaderLayout.setVisibility(View.VISIBLE); 
					mDateHeaderLayout.setVisibility(View.VISIBLE); 			
					mAmountHeaderLayout.setVisibility(View.VISIBLE); 
					if(spinnerData.matches("Invoice")){			
						mNo.setText("Invoice No");
						mDate.setText("Invoice Date"); 
					}
					else if(spinnerData.matches("Receipt")){
						mNo.setText("Receipt No");
						mDate.setText("Receipt Date"); 
					}
				}
				mEmpty.setVisibility(View.GONE);
				mHorizontalScrollView.setVisibility(View.VISIBLE);
				
				
				
			}else{
				mEmpty.setVisibility(View.VISIBLE);
				mHorizontalScrollView.setVisibility(View.GONE);
			}
			
			
		} catch (ParseException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
	}*/
	private void enableViews(View v, boolean enabled) {
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				enableViews(vg.getChildAt(i), enabled);
			}
		}
		v.setEnabled(enabled);
	}

	public class OfflineDataAdapter extends BaseAdapter{
		private LayoutInflater mLayoutInflater;
		
		private ArrayList<SO> mOfflineData;
		private int mResourceId;
		private String spinnerFlag,highlight;
		public OfflineDataAdapter(Context context,int resourceId,ArrayList<SO> mData,String flag){
			mOfflineData = new ArrayList<SO>();
			mOfflineData = mData;
			mResourceId = resourceId;
			spinnerFlag = flag;
			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			
		}
		@Override
		public int getCount() {
		
			return mOfflineData.size();
		}

		@Override
		public SO getItem(int position) {
			
			return mOfflineData.get(position);
		}

		@Override
		public long getItemId(int id) {
			
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			SO so = getItem(position);
			final ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = mLayoutInflater.inflate(mResourceId, parent,false);
				holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
				holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
				holder.textView3 = (TextView) convertView.findViewById(R.id.textView3);
				holder.textView4 = (TextView) convertView.findViewById(R.id.textView4);
				holder.textView5 = (TextView) convertView.findViewById(R.id.textView5);
				
				holder.linearLayout1 = (LinearLayout) convertView.findViewById(R.id.LinearLayout1);
				holder.linearLayout2 = (LinearLayout) convertView.findViewById(R.id.LinearLayout2);
				holder.linearLayout3 = (LinearLayout) convertView.findViewById(R.id.LinearLayout3);
				holder.linearLayout4 = (LinearLayout) convertView.findViewById(R.id.LinearLayout4);
				holder.linearLayout5 = (LinearLayout) convertView.findViewById(R.id.LinearLayout5);
				
				 
				convertView.setTag(holder);
				convertView.setId(position);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
		
			if(spinnerFlag.matches("Customer")){
				holder.textView3.setText(so.getCustomerCode());
				holder.textView4.setText(so.getCustomerName());
				holder.linearLayout1.setVisibility(View.GONE); 
				holder.linearLayout2.setVisibility(View.GONE);				
				holder.linearLayout5.setVisibility(View.GONE);
				
				 highlight = OfflineDatabase.getHeaderDataStatus("tblGetCustomer","CustomerCode",so.getCustomerCode());
				 Log.d("highlight", "c"+highlight);
				if(highlight.matches("1")){
					holder.textView3.setTextColor(Color.parseColor("#F34945"));
					holder.textView4.setTextColor(Color.parseColor("#F34945"));
				} else if(highlight.matches("0")){
					holder.textView3.setTextColor(Color.parseColor("#000000"));
					holder.textView4.setTextColor(Color.parseColor("#000000"));
				}

			}else if(spinnerFlag.matches("Products")){
				holder.textView3.setText(so.getProductCode());
				holder.textView4.setText(so.getProductName());
				holder.linearLayout1.setVisibility(View.GONE); 
				holder.linearLayout2.setVisibility(View.GONE);				
				holder.linearLayout5.setVisibility(View.GONE);
				
				 highlight = OfflineDatabase.getHeaderDataStatus("tblGetProduct","ProductCode",so.getProductCode());
				 Log.d("highlight", "c"+highlight);
				if(highlight.matches("1")){
					holder.textView3.setTextColor(Color.parseColor("#F34945"));
					holder.textView4.setTextColor(Color.parseColor("#F34945"));
				} else if(highlight.matches("0")){
					holder.textView3.setTextColor(Color.parseColor("#000000"));
					holder.textView4.setTextColor(Color.parseColor("#000000"));
				}

			}
			else{
				holder.textView1.setText(so.getSno());
				holder.textView2.setText(so.getDate());
				holder.textView3.setText(so.getCustomerCode());
				holder.textView4.setText(so.getCustomerName());
				holder.textView5.setText(so.getNettotal());				
				holder.linearLayout1.setVisibility(View.VISIBLE); 
				holder.linearLayout2.setVisibility(View.VISIBLE);
				holder.linearLayout5.setVisibility(View.VISIBLE);					
				
				if(spinnerFlag.matches("Invoice")){
					highlight = OfflineDatabase.getHeaderDataStatus("tblGetInvoiceHeader","InvoiceNo",so.getSno());
				}
				else if(spinnerFlag.matches("Receipt")){
					highlight = OfflineDatabase.getHeaderDataStatus("tblGetReceiptHeader","ReceiptNo",so.getSno());
				}
				else if(spinnerFlag.matches("SalesOrder")){
					highlight = OfflineDatabase.getHeaderDataStatus("tblGetSOHeader","SoNo",so.getSno());
				}				
				Log.d("highlight", "ir"+highlight);
				if(highlight.matches("1")){
					holder.textView1.setTextColor(Color.parseColor("#F34945"));
					holder.textView2.setTextColor(Color.parseColor("#F34945"));
					holder.textView3.setTextColor(Color.parseColor("#F34945"));
					holder.textView4.setTextColor(Color.parseColor("#F34945"));
					holder.textView5.setTextColor(Color.parseColor("#F34945"));					
					
				} else if(highlight.matches("0")){
					holder.textView1.setTextColor(Color.parseColor("#000000"));
					holder.textView2.setTextColor(Color.parseColor("#000000"));
					holder.textView3.setTextColor(Color.parseColor("#000000"));
					holder.textView4.setTextColor(Color.parseColor("#000000"));
					holder.textView5.setTextColor(Color.parseColor("#000000"));				
				
				}
				
			}
			
						
			return convertView;
		}
		class ViewHolder{
			TextView textView1;
			TextView textView2;
			TextView textView3;
			TextView textView4;
			TextView textView5;
			LinearLayout linearLayout1;
			LinearLayout linearLayout2;
			LinearLayout linearLayout3;
			LinearLayout linearLayout4;
			LinearLayout linearLayout5;
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(OfflineDataView.this, Settings.class);
		startActivity(i);
		finish();
	}
}
