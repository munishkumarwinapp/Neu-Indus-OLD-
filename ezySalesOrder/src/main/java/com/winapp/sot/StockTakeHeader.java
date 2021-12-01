package com.winapp.sot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.helper.DateTime;
import com.winapp.model.Schedule;
import com.winapp.model.StockCount;

public class StockTakeHeader extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace, OnClickListener, OnTouchListener,Constants {

	private SlidingMenu menu;
	private ImageButton searchImgBtn, printerImgBtn, addImgBtn;
	private Intent mintent;
	private EditText stocktakefromDateEdtxt, stocktaketoDateEdtxt;
	private Calendar fromCalendar, toCalendar;
	private String dateformatStrng = "dd/MM/yyyy", serverdate,valid_url,jsonString="",cmpnyCode,locCode,stockTakeNo; 
	private SimpleDateFormat simpledateformat;
	private LinearLayout mspinnerLayout, mparentLayout;
	private ProgressBar mprogressBar;
	private TableRow mtableRow1,mtableRow2,mtableRow3;
	private HashMap<String, String> mparam;
	private JSONObject jsonResponse;
	private JSONArray jsonMainNode;
	private ArrayList<SO> mStockCount;
	private ArrayList<StockCount> mEditStockCount;
	private HeaderAdapter stockcountAdapter;
	private ListView mListView;
	private static final int MENU1 = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.stock_take_header);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
	
		//ActionBarCustom Layout
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
	
		//TextView
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Stock Take");
		
		//ImageButton
		searchImgBtn = (ImageButton) customNav.findViewById(R.id.search_img);
		printerImgBtn = (ImageButton) customNav.findViewById(R.id.printer);
		addImgBtn = (ImageButton) customNav.findViewById(R.id.custcode_img);
		addImgBtn.setVisibility(View.INVISIBLE);
		
		//EditText
		stocktakefromDateEdtxt = (EditText) findViewById(R.id.stocktakeFromDateEdtxt);
		stocktaketoDateEdtxt = (EditText) findViewById(R.id.stocktakeToDateEdtxt);
	
		//ListView
		mListView = (ListView) findViewById(R.id.stocktakeListView);
	
		//LinearLayout
		mparentLayout = (LinearLayout) findViewById(R.id.parentLayout);
	
		//TableRow
		mtableRow1 = (TableRow)findViewById(R.id.tableRow1);
		mtableRow2 = (TableRow)findViewById(R.id.tableRow2);
		mtableRow3 = (TableRow)findViewById(R.id.tableRow3);
		
		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayHomeAsUpEnabled(false);
		//SlideMenu
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);

		mintent = new Intent();
		mparam = new HashMap<String, String>();
		mStockCount = new ArrayList<SO>();
		mEditStockCount = new ArrayList<StockCount>();
		
		//ArrayList Clear
		mparam.clear();
		mStockCount.clear();
		mEditStockCount.clear();
		
		//Calendar Instance
		fromCalendar = Calendar.getInstance();
		toCalendar = Calendar.getInstance();
		
		//OnClickListener
		searchImgBtn.setOnClickListener(this);
		printerImgBtn.setOnClickListener(this);
//		addImgBtn.setOnClickListener(this);
		
		//OnTouchListener
		stocktakefromDateEdtxt.setOnTouchListener(this);
		stocktaketoDateEdtxt.setOnTouchListener(this);

		simpledateformat = new SimpleDateFormat(dateformatStrng, Locale.US);
		
		cmpnyCode = SupplierSetterGetter.getCompanyCode();
		locCode = SalesOrderSetGet.getLocationcode();
		
		//Init URL
		FWMSSettingsDatabase.init(StockTakeHeader.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,StockTakeHeader.this);
		new SalesOrderWebService(valid_url);
		
		//Call ServerDate
		new ServerDateAsyncWSCall().execute();
		
		//Init DB
		SOTDatabase.init(StockTakeHeader.this);
	
		//Context Menu
		registerForContextMenu(mListView);
		searchImgBtn.setVisibility(View.INVISIBLE);
	}

	private DatePickerDialog.OnDateSetListener mDateSetListenerFrom = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			fromCalendar.set(Calendar.YEAR, year);
			fromCalendar.set(Calendar.MONTH, monthOfYear);
			fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

			stocktakefromDateEdtxt.setText(simpledateformat.format(fromCalendar
					.getTime()));
		}
	};
	private DatePickerDialog.OnDateSetListener mDateSetListenerTo = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			toCalendar.set(Calendar.YEAR, year);
			toCalendar.set(Calendar.MONTH, monthOfYear);
			toCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

			stocktaketoDateEdtxt.setText(simpledateformat.format(toCalendar
					.getTime()));

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_img:

			if ((mtableRow1.getVisibility() == View.VISIBLE) && (mtableRow2.getVisibility() == View.VISIBLE) && (mtableRow3.getVisibility() == View.VISIBLE)) {
				// Its visible
				mtableRow1.setVisibility(View.GONE);
				mtableRow2.setVisibility(View.GONE);
				mtableRow3.setVisibility(View.GONE);
			} else {
				mtableRow1.setVisibility(View.VISIBLE);
				mtableRow2.setVisibility(View.VISIBLE);
				mtableRow3.setVisibility(View.VISIBLE);
				// Either gone or invisible
			}		
			break;
		case R.id.printer:
			
			break;
//		case R.id.custcode_img:
//			clearPojo();
//			mintent.setClass(StockTakeHeader.this, StockTakeHeaderDetail.class);
//			startActivity(mintent);	
//            finish();
//			break;
		default:
			break;
		}
	}
private void clearPojo(){
	SOTDatabase.deleteAllProduct();
	SOTDatabase.deleteImage();
	SalesOrderSetGet.setStockTakeNo("");
}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.stocktakeFromDateEdtxt:
			if (MotionEvent.ACTION_UP == event.getAction())
				new DatePickerDialog(this, mDateSetListenerFrom,
						fromCalendar.get(Calendar.YEAR),
						fromCalendar.get(Calendar.MONTH),
						fromCalendar.get(Calendar.DAY_OF_MONTH)).show();
			break;
		case R.id.stocktakeToDateEdtxt:
			if (MotionEvent.ACTION_UP == event.getAction())
				new DatePickerDialog(this, mDateSetListenerTo,
						toCalendar.get(Calendar.YEAR),
						toCalendar.get(Calendar.MONTH),
						toCalendar.get(Calendar.DAY_OF_MONTH)).show();
			break;
		default:
			break;
		}
		return false;
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		SO so = stockcountAdapter.getItem(info.position);
		stockTakeNo = so.getSno().toString();
        menu.setHeaderTitle(stockTakeNo);
		menu.add(Menu.NONE, MENU1, 1, "Continue to StockTake");
		
	}
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {	
		switch (item.getItemId()) {
		case MENU1:				
			mEditProductStock();			
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	private void mEditProductStock() {
		SalesOrderSetGet.setStockTakeNo(stockTakeNo);
		SOTDatabase.deleteAllProduct();
		SOTDatabase.deleteImage();
		Schedule.setStockcount(null);
		mintent.setClass(StockTakeHeader.this, StockTakeAddProduct.class);		
		startActivity(mintent);
		finish();
//		new GetStockCountDetailByNo().execute();
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

	private void enableViews(View v, boolean enabled) {
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				enableViews(vg.getChildAt(i), enabled);
			}
		}
		v.setEnabled(enabled);
	}

	@SuppressWarnings("deprecation")
	public void progressBarOpen() {
		mspinnerLayout = new LinearLayout(this);
		mspinnerLayout.setGravity(Gravity.CENTER);
		addContentView(mspinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		mspinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(mparentLayout, false);
		mprogressBar = new ProgressBar(this);
		mprogressBar.setProgress(android.R.attr.progressBarStyle);
		mprogressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		mspinnerLayout.addView(mprogressBar);
	}

	public void progressBarClose() {
		mprogressBar.setVisibility(View.GONE);
		mspinnerLayout.setVisibility(View.GONE);
		enableViews(mparentLayout, true);
	}
       /****************Web Service Call********************/
	private class ServerDateAsyncWSCall extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {

			progressBarOpen();
			boolean check= isNetworkConnected();
			if(check == false){
				finish();
			}
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			serverdate = DateWebservice.getDateService("fncGetServerDate");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (serverdate != null) {
				SalesOrderSetGet.setSaleorderdate(serverdate);
				stocktakefromDateEdtxt.setText(DateTime.date(serverdate));
				stocktaketoDateEdtxt.setText(serverdate);			
				}
			
			new GetStockCountHeader().execute(cmpnyCode,locCode);
			
		}
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
	}

	/********************** FNC_GETSTOCKCOUNTHEADER ************************/
	private class GetStockCountHeader extends AsyncTask<Object, Void, Void> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Object... param) {
			String companyCode = (String) param[0];
			String locationCode = (String) param[1];
			  
		/*	Log.d("companyCode-->", companyCode);
			Log.d("locationCode-->", locationCode);
			*/
			mparam.put("CompanyCode", companyCode);
	  		mparam.put("LocationCode", locationCode);
	  	   
			
			jsonString = SalesOrderWebService.getSODetail(mparam,
					FNC_GETSTOCKCOUNTHEADER);

			Log.d("jsonString ", "" + jsonString);
			
			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					
					SO so = new SO();
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);					
					
					so.setSno(jsonChildNode.getString("StockTakeNo"));
					
					StringTokenizer tokens = new StringTokenizer(jsonChildNode.getString("StockTakeDate"), " ");
					String date = tokens.nextToken();
					so.setDate(date);				
					
                    so.setCustomerCode(jsonChildNode.getString("LocationCode"));
					so.setNettotal(jsonChildNode.getString("ModifyUser"));
					mStockCount.add(so);
				}

			} catch (JSONException e) {

				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mparam.clear();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
		
		if(mStockCount.size()>0){
			stockcountAdapter = new HeaderAdapter(StockTakeHeader.this, R.layout.salesorder_list_item, null, mStockCount);
			mListView.setAdapter(stockcountAdapter);
			
		}
		progressBarClose();
		}
	}
	/********************** FNC_GETSTOCKCOUNTDETAILBYNO **********************/
//	private class GetStockCountDetailByNo extends AsyncTask<Void, Void, Void> {
//
//		@Override
//		protected void onPreExecute() {
//
//			progressBarOpen();
//		}
//
//		@Override
//		protected Void doInBackground(Void... param) {
//            
//            mparam.put("CompanyCode", cmpnyCode);
//  		    mparam.put("LocationCode", locCode);
//  		    mparam.put("StockTakeNo", stockTakeNo);
//  		    mparam.put("CategoryCode", "");
//			mparam.put("SubCategoryCode", "");
//  		    
//  		   
//			
//			jsonString = SalesOrderWebService.getSODetail(mparam,
//					FNC_GETSTOCKCOUNTDETAILBYNO);
//
//			Log.d("jsonString ", "" + jsonString);
//			try {
//
//				jsonResponse = new JSONObject(jsonString);
//				jsonMainNode = jsonResponse.optJSONArray("SODetails");
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			try {
//				int lengthJsonArr = jsonMainNode.length();
//				for (int i = 0; i < lengthJsonArr; i++) {
//					StockCount stockcount = new StockCount();
//					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);					
//					String stocktakeNo = jsonChildNode.getString(STOCK_TAKE_NO);
//					String countqty = jsonChildNode.getString(COUNTQTY);
//					if((stocktakeNo != null && !stocktakeNo.isEmpty())){
//					stockcount.setProductCode(jsonChildNode.getString(PRODUCT_CODE));
//					stockcount.setProductName(jsonChildNode.getString(PRODUCT_NAME));
//					stockcount.setCategoryCode(jsonChildNode.getString(CATEGORYCODE));
//					stockcount.setSubcategoryCode(jsonChildNode.getString(SUBCATEGORYCODE));
//					String pcspercarton =jsonChildNode.getString(PCSPERCARTON);					
//					
//					if(pcspercarton != null && !pcspercarton.isEmpty()){
//						 String ppc = String.valueOf(pcspercarton).split("\\.")[0];
//						
//						stockcount.setPcsPerCarton(Integer.valueOf(ppc));
//					}
//					else{
//						stockcount.setPcsPerCarton(0);
//					}
//					String noofcarton = jsonChildNode.getString(NOOFCARTON);
//					if (noofcarton != null && !noofcarton.isEmpty()) {
//						 String nofcarton = String.valueOf(noofcarton).split("\\.")[0];
//						stockcount.setNoOfCarton(Integer.valueOf(nofcarton));
//					} else {
//						stockcount.setNoOfCarton(0);
//					}
//					String qty = jsonChildNode.getString(QTY);
//					if (qty != null && !qty.isEmpty()) {
//						String quantity = String.valueOf(qty).split("\\.")[0];
//						stockcount.setQty(Integer.valueOf(quantity));
//					} else {
//						stockcount.setQty(0);
//					}
//					String countlqty = jsonChildNode.getString(COUNTLQTY);
//					if (countlqty != null && !countlqty.isEmpty()) {
//						String lqty = String.valueOf(countlqty).split("\\.")[0];
//						stockcount.setCountLQty(Integer.valueOf(lqty));
//					}
//					else{
//						stockcount.setCountLQty(0);
//					}
//					
//					if (countqty != null && !countqty.isEmpty()) {
//						String ctqty = String.valueOf(countqty).split("\\.")[0];
//						stockcount.setCountQty(Integer.valueOf(ctqty));
//					}else{
//						stockcount.setCountQty(0);
//					}
//
//					String countcqty = jsonChildNode.getString(COUNTCQTY);
//					if (countcqty != null && !countcqty.isEmpty()) {
//						String ccqty = String.valueOf(countcqty).split("\\.")[0];
//						stockcount.setCountCQty(Integer.valueOf(ccqty));
//					}
//					else{
//						stockcount.setCountCQty(0);
//					}
//					
//					mEditStockCount.add(stockcount);
//				}
//					}
//			
//
//			} catch (JSONException e) {
//
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			mparam.clear();
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//					
//			mintent.setClass(StockTakeHeader.this, StockTakeAddProduct.class);
//			mintent.putExtra(ACTIVITY_STOCK_TAKE_HEADER_EDIT,mEditStockCount);			
//			startActivity(mintent);
//			finish();
//			progressBarClose();	
//		}
//
//	}
	@Override
	public void onBackPressed() {
		mintent.setClass(StockTakeHeader.this, LandingActivity.class);
		startActivity(mintent);
		finish();
	}
}
