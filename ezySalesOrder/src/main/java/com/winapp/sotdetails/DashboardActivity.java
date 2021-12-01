package com.winapp.sotdetails;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.DashboardModel;
import com.winapp.helper.DateTime;
import com.winapp.printer.InvoicePrintPreview;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.ReceiptPrintPreview;
import com.winapp.sot.In_Cash;
import com.winapp.sot.InvoiceHeader;
import com.winapp.sot.ProdDetails;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.ReceiptHeader;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.util.ToolTip;

public class DashboardActivity extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace {

	private CombinedChart mInvoiceCollectionDayChart,mInvoiceCollectionMonthChart;
	private BarChart mIncomeExpenseDayChart,mIncomeExpenseMonthChart;
	private int itemcount = 10;
	private CombinedData data;
	private String companyCode,receiptNo,receiptDate,serverDate="", valid_url, mDays[], mMonths[];
	String value_txt;
	private ArrayList<String> sortproduct, dates,listarray;
	private JSONObject subcategory_jsonResponse, category_jsonResponse,
			general_settings_jsonResponse,
			invoice_header_by_invoice_no_jsonResponse,receipt_header_by_receipt_no_jsonResponse,receipt_detail_jsonResponse,
			invoice_detail_jsonResponse, serverdate_jsonResponse,
			dashboard_jsonResponse, dashboardinvoiceWeely_jsonResponse,
			dashboardinvoiceMonthly_jsonResponse, invoice_jsonResponse,
			receipt_jsonResponse ;
	private JSONArray subcategory_jsonMainNode, category_jsonMainNode,
			general_settings_jsonMainNode,receipt_header_by_receipt_no_jsonMainNode,receipt_detail_jsonMainNode,
			invoice_header_by_invoice_no_jsonMainNode,
			invoice_detail_jsonMainNode, serverdate_jsonMainNode,
			dashboard_jsonMainNode, dashboardinvoiceWeely_jsonMainNode,
			dashboardinvoiceMonthly_jsonMainNode, invoice_jsonMainNode,
			receipt_jsonMainNode;
	private String invoiceDate, customerCode, customerName, invoiceNo,
			decimalpts = ".00", subcategory_jsonString, category_jsonString,
			gnrlStngs, general_settings_jsonString,
			invoice_header_by_invoice_no_jsonString, invoice_detail_jsonString,receipt_detail_jsonString,receipt_header_by_receipt_no_jsonString,
			serverdate_jsonString, invoice_jsonString, dashboard_jsonString,
			receipt_jsonString, dashboardinvoiceWeely_jsonString,
			dashboardinvoiceMonthly_jsonString;
	private HashMap<String, String> hashValue;
	private ArrayList<DashboardModel> mTrans;
	private TextView mIncomeExpenseChartMonths, mIncomeExpenseChartDays,
			mInvoiceCollectionChartMonths, mInvoiceCollectionChartDays,
			mInvoiceDay, mInvoiceWeek, mInvoiceMonth, mCollectionDay,
			mCollectionWeek, mCollectionMonth, mOutstanding, mInvoice,
			minvoice, mInvoiceListEmpty, mPaymentListEmpty;
	private ImageButton searchIcon, custsearchIcon;
	private ArrayList<Float> mTraninvoiceWeelky, mTranCollectionWeelky,mTranPaymentExpenseWeelky,mTranPaymentExpenseMonth,
			mTraninvoiceMonthly, mTranCollectionMonthly;
	private SimpleAdapter mSimpleAdapter;
	private ScrollView mScrollView;
	private ToolTip mToolTip;
	private SlidingMenu menu;
	private ArrayList<ProductDetails> product, productdet;
	private ListView mInvoiceListView, mReceipListView;
	private ArrayList<HashMap<String, String>> mInvoiceArrhm, mReceiptArrhm;
	private Intent mIntent;
	private ArrayList<ProdDetails> proddetail;
    private LinearLayout mLinearLayout,spinnerLayout;
    private ProgressBar progressBar;
    private double screenInches;
	private List<String> sMonthsList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int dens = dm.densityDpi;
		double wi = (double) width / (double) dens;
		double hi = (double) height / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		screenInches = Math.sqrt(x + y);

		Log.d("Display Inche", "" + screenInches);
		
		if (screenInches > 7) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setContentView(R.layout.activity_combined_tab);
		}
		else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setContentView(R.layout.activity_combined);			
		}

		getActionBar().setIcon(drawable.ic_menu);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Dashboard");
		searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
		custsearchIcon = (ImageButton) customNav
				.findViewById(R.id.custcode_img);

		searchIcon.setVisibility(View.INVISIBLE);
		custsearchIcon.setVisibility(View.VISIBLE);
		
		custsearchIcon.setImageResource(R.mipmap.home);

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

		mInvoiceListEmpty =  (TextView) findViewById(R.id.invoiceListEmpty);
		mPaymentListEmpty = (TextView) findViewById(R.id.paymentListEmpty);
		mLinearLayout =  (LinearLayout) findViewById(R.id.linearlayout);
		mInvoiceDay = (TextView) findViewById(R.id.salesDay);
		mInvoiceWeek = (TextView) findViewById(R.id.salesWeek);
		mInvoiceMonth = (TextView) findViewById(R.id.salesMonth);
		mCollectionDay = (TextView) findViewById(R.id.invoiceDay);
		mCollectionWeek = (TextView) findViewById(R.id.invoiceWeek);
		mCollectionMonth = (TextView) findViewById(R.id.invoiceMonth);

		mInvoiceCollectionChartMonths = (TextView) findViewById(R.id.invoice_Collection_Month);
		mInvoiceCollectionChartDays = (TextView) findViewById(R.id.invoice_Collection_Day);
		
		
		
		mIncomeExpenseChartMonths = (TextView) findViewById(R.id.income_Expense_Month);
		mIncomeExpenseChartDays = (TextView) findViewById(R.id.income_Expense_Day);

		mInvoiceListView = (ListView) findViewById(R.id.invoiceListView);
		mReceipListView = (ListView) findViewById(R.id.paymentListView);
		mScrollView = (ScrollView) findViewById(R.id.scrollView);
		mOutstanding = (TextView) findViewById(R.id.totalOutstanding);
		mInvoice = (TextView) findViewById(R.id.totalCollection);
		minvoice = (TextView) findViewById(R.id.totalSales);
		
		
		mInvoiceCollectionDayChart = (CombinedChart) findViewById(R.id.invoice_Collection_DayChart);		
		mInvoiceCollectionMonthChart = (CombinedChart) findViewById(R.id.invoice_Collection_MonthChart);
		
		
		mIncomeExpenseDayChart = (BarChart) findViewById(R.id.income_Expense_DayChart);		
		mIncomeExpenseMonthChart = (BarChart) findViewById(R.id.income_Expense_MonthChart);
		
		
		// Invoice Collection Day Combined Chart 
		mInvoiceCollectionDayChart.setDescription("");
		mInvoiceCollectionDayChart.setGridBackgroundColor(Color.parseColor("#f9f9f9"));
//		mInvoiceCollectionDayChart.setBackgroundColor(Color.WHITE);
		mInvoiceCollectionDayChart.setDrawGridBackground(false); // chart background
		mInvoiceCollectionDayChart.setDrawBarShadow(false); // show bar
		
		// Invoice Collection Month Combined Chart 
		mInvoiceCollectionMonthChart.setDescription("");
		//Chart Grid Background Color
		mInvoiceCollectionMonthChart.setGridBackgroundColor(Color.parseColor("#f9f9f9"));
//		mInvoiceCollectionMonthChart.setBackgroundColor(Color.WHITE);
		mInvoiceCollectionMonthChart.setDrawGridBackground(false); // chart background
		mInvoiceCollectionMonthChart.setDrawBarShadow(false); // show bar
		
		
		// Income Expense Day Bar Chart
		mIncomeExpenseDayChart.setDescription("");
		mIncomeExpenseDayChart.setGridBackgroundColor(Color.parseColor("#f9f9f9"));
//		mIncomeExpenseDayChart.setBackgroundColor(Color.WHITE);
		mIncomeExpenseDayChart.setDrawGridBackground(false); // chart background
		mIncomeExpenseDayChart.setDrawBarShadow(false); // show bar
		
		// Income Expense Month Bar Chart
		mIncomeExpenseMonthChart.setDescription("");
		//Chart Grid Background Color
		mIncomeExpenseMonthChart.setGridBackgroundColor(Color.parseColor("#f9f9f9"));
//		mIncomeExpenseMonthChart.setBackgroundColor(Color.WHITE);
		mIncomeExpenseMonthChart.setDrawGridBackground(false); // chart background
		mIncomeExpenseMonthChart.setDrawBarShadow(false); // show bar
		
		
		
		// mChart.setDrawBorders(false); //Chart border

		/*
		 * mChart.getAxisLeft().setDrawAxisLine(false);
		 * mChart.getAxisLeft().setDrawGridLines(false);
		 * mChart.getAxisRight().setDrawAxisLine(false);
		 * mChart.getAxisRight().setDrawGridLines(false);
		 * mChart.getXAxis().setDrawAxisLine(false);
		 * mChart.getXAxis().setDrawGridLines(false);
		 */
		sMonthsList = new ArrayList<>();
		mMonths = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
				"Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

		mIntent = new Intent();
		
		
		proddetail = new ArrayList<ProdDetails>();
		sortproduct = new ArrayList<String>();
		listarray = new ArrayList<String>();
		product = new ArrayList<ProductDetails>();
		productdet = new ArrayList<ProductDetails>();
		hashValue = new HashMap<String, String>();
		mTrans = new ArrayList<DashboardModel>();
		dates = new ArrayList<String>();
		mTraninvoiceWeelky = new ArrayList<Float>();
		mTranCollectionWeelky = new ArrayList<Float>();
		mTraninvoiceMonthly = new ArrayList<Float>();
		mTranPaymentExpenseWeelky = new ArrayList<Float>();		
		mTranPaymentExpenseMonth = new ArrayList<Float>();
		mTranCollectionMonthly = new ArrayList<Float>();
		mInvoiceArrhm = new ArrayList<HashMap<String, String>>();
		mReceiptArrhm = new ArrayList<HashMap<String, String>>();
		FWMSSettingsDatabase.init(DashboardActivity.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SalesOrderWebService(valid_url);
		
		companyCode = SupplierSetterGetter.getCompanyCode();

		// draw bars behind lines
		mInvoiceCollectionDayChart.setDrawOrder(new DrawOrder[] { DrawOrder.BAR, DrawOrder.BUBBLE,
				DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER });
		
		
		mInvoiceCollectionMonthChart.setDrawOrder(new DrawOrder[] { DrawOrder.BAR, DrawOrder.BUBBLE,
				DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER });
		
		
		

		// create a custom MarkerView (extend MarkerView) and specify the layout
		// to use for it
		mToolTip = new ToolTip(this, R.layout.custom_tool_tip_view);
		mInvoiceCollectionDayChart.setMarkerView(mToolTip);
		mInvoiceCollectionMonthChart.setMarkerView(mToolTip);		
	
		mIncomeExpenseDayChart.setMarkerView(mToolTip);
		mIncomeExpenseMonthChart.setMarkerView(mToolTip);

		
		
		//Invoice Collection Day Chart
		YAxis rightAxisInvoiceCollectionDay = mInvoiceCollectionDayChart.getAxisRight();
		rightAxisInvoiceCollectionDay.setDrawGridLines(true);
		mInvoiceCollectionDayChart.getAxisRight().setEnabled(false);//disable right axis
		rightAxisInvoiceCollectionDay.setGridColor(Color.parseColor("#f0f0f0"));
		YAxis leftAxisInvoiceCollectionDay = mInvoiceCollectionDayChart.getAxisLeft();
		leftAxisInvoiceCollectionDay.setDrawGridLines(true);// horizontal line in chart
		leftAxisInvoiceCollectionDay.setGridColor(Color.parseColor("#f0f0f0"));
        XAxis xAxisInvoiceCollectionDay = mInvoiceCollectionDayChart.getXAxis();
		xAxisInvoiceCollectionDay.setPosition(XAxisPosition.BOTTOM);
		xAxisInvoiceCollectionDay.setGridColor(Color.parseColor("#f0f0f0"));		
		
		
		//Invoice Collection Month Chart
		YAxis rightAxis1 = mInvoiceCollectionMonthChart.getAxisRight();
		rightAxis1.setDrawGridLines(true);
		mInvoiceCollectionMonthChart.getAxisRight().setEnabled(false);//disable right axis
        YAxis leftAxisInvoiceCollectionMonth = mInvoiceCollectionMonthChart.getAxisLeft();
		leftAxisInvoiceCollectionMonth.setDrawGridLines(true);// horizontal line in chart
		leftAxisInvoiceCollectionMonth.setGridColor(Color.parseColor("#f0f0f0"));
		XAxis xAxisInvoiceCollectionMonth = mInvoiceCollectionMonthChart.getXAxis();
		xAxisInvoiceCollectionMonth.setPosition(XAxisPosition.BOTTOM);
		xAxisInvoiceCollectionMonth.setGridColor(Color.parseColor("#f0f0f0"));		
		
		//Income Expense Day Chart
		YAxis rightAxisIncomeExpenseDay = mIncomeExpenseDayChart.getAxisRight();
		rightAxisIncomeExpenseDay.setDrawGridLines(true);
		mIncomeExpenseDayChart.getAxisRight().setEnabled(false);//disable right axis
		rightAxisIncomeExpenseDay.setGridColor(Color.parseColor("#f0f0f0"));
		YAxis leftAxisIncomeExpenseDay = mIncomeExpenseDayChart.getAxisLeft();
		leftAxisIncomeExpenseDay.setGridColor(Color.parseColor("#f0f0f0"));
		XAxis xAxisIncomeExpenseDay = mIncomeExpenseDayChart.getXAxis();
		xAxisIncomeExpenseDay.setPosition(XAxisPosition.BOTTOM);
		xAxisIncomeExpenseDay.setGridColor(Color.parseColor("#f0f0f0"));
				
		// Income Expense Month Chart
		YAxis rightAxisIncomeExpenseMonth = mIncomeExpenseMonthChart.getAxisRight();
		rightAxisIncomeExpenseMonth.setDrawGridLines(true);
		mIncomeExpenseMonthChart.getAxisRight().setEnabled(false);// disable right axis
		YAxis leftAxisIncomeExpenseMonth = mIncomeExpenseMonthChart.getAxisLeft();
		leftAxisIncomeExpenseMonth.setDrawGridLines(true);// horizontal line in chart
		leftAxisIncomeExpenseMonth.setGridColor(Color.parseColor("#f0f0f0"));
		XAxis xAxisIncomeExpenseMonth = mIncomeExpenseMonthChart.getXAxis();
		xAxisIncomeExpenseMonth.setPosition(XAxisPosition.BOTTOM);
		xAxisIncomeExpenseMonth.setGridColor(Color.parseColor("#f0f0f0"));
		

		// load Dashboard Chart Data
		new LoadInvoiceChartData().execute();

		custsearchIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(mIntent.setClass(getApplicationContext(), LandingActivity.class));
				finish();
			}
		});
		
		mOutstanding.setOnClickListener(new OnClickListener() {

			   @Override
			   public void onClick(View v) {
			    startActivity(mIntent.setClass(getApplicationContext(), OutstandingSummary.class));
//			    finish();
			   }
			  });
		
		mInvoiceDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getinvoiceTransValue("DaySales");
				mInvoiceDay.setBackgroundResource(drawable.sales_selection);
				mInvoiceWeek.setBackgroundColor(Color.parseColor("#5b35a4"));
				mInvoiceMonth.setBackgroundColor(Color.parseColor("#5b35a4"));

			}
		});
		mInvoiceWeek.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getinvoiceTransValue("WeekSales");
				mInvoiceWeek.setBackgroundResource(drawable.sales_selection);
				mInvoiceDay.setBackgroundColor(Color.parseColor("#5b35a4"));
				mInvoiceMonth.setBackgroundColor(Color.parseColor("#5b35a4"));
			}
		});
		mInvoiceMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getinvoiceTransValue("MonthSales");
				mInvoiceMonth.setBackgroundResource(drawable.sales_selection);
				mInvoiceDay.setBackgroundColor(Color.parseColor("#5b35a4"));
				mInvoiceWeek.setBackgroundColor(Color.parseColor("#5b35a4"));
			}
		});
		mCollectionDay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getCollectionTransValue("DayCollection");
				mCollectionDay
						.setBackgroundResource(drawable.collection_selection);
				mCollectionWeek.setBackgroundColor(Color.parseColor("#24890f"));
				mCollectionMonth.setBackgroundColor(Color.parseColor("#24890f"));
			}
		});
		mCollectionWeek.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getCollectionTransValue("WeekCollection");
				mCollectionWeek
						.setBackgroundResource(drawable.collection_selection);
				mCollectionDay.setBackgroundColor(Color.parseColor("#24890f"));
				mCollectionMonth.setBackgroundColor(Color.parseColor("#24890f"));
			}
		});
		mCollectionMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getCollectionTransValue("MonthCollection");
				mCollectionMonth
						.setBackgroundResource(drawable.collection_selection);
				mCollectionWeek.setBackgroundColor(Color.parseColor("#24890f"));
				mCollectionDay.setBackgroundColor(Color.parseColor("#24890f"));
			}
		});
		findViewById(R.id.viewInvoiceList).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIntent.setClass(getApplicationContext(), InvoiceHeader.class);
				 mIntent.putExtra("dashboard", "dashboard");
				startActivity(mIntent);	
			//	finish();
			}
		});
		findViewById(R.id.viewReceiptList).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIntent.setClass(getApplicationContext(), ReceiptHeader.class);
				 mIntent.putExtra("dashboard", "dashboard");
				startActivity(mIntent);		
				//finish();
			}
		});		
	
		mInvoiceCollectionChartMonths.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//getMonths(mTraninvoiceMonthly, mTranCollectionMonthly);
				mInvoiceCollectionMonthChart.setVisibility(View.VISIBLE);				
				mInvoiceCollectionDayChart.setVisibility(View.GONE);
				
				
				mInvoiceCollectionChartMonths.setBackgroundResource(drawable.chart_selection);
				mInvoiceCollectionChartMonths.setTextColor(Color.parseColor("#ffffff"));

				mInvoiceCollectionChartDays.setBackgroundColor(Color.parseColor("#ffffff"));
				mInvoiceCollectionChartDays.setTextColor(Color.parseColor("#015FA0"));
				
				mInvoiceCollectionMonthChart.animateY(1500);
				//mInvoiceCollectionMonthChart.animateY(700, Easing.EasingOption.EaseInCubic);

			}
		});
		/*
		 * findViewById(R.id.week).setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { weeks.clear(); mWeeks = new
		 * String[0]; getWeek();
		 * 
		 * } });
		 */

		mInvoiceCollectionChartDays.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//dates.clear();
				//mDays = new String[0];
				
				//getDate(mTraninvoiceWeelky, mTranCollectionWeelky);
				mInvoiceCollectionDayChart.setVisibility(View.VISIBLE);
				
				mInvoiceCollectionMonthChart.setVisibility(View.GONE);
				
				
				mInvoiceCollectionChartDays.setBackgroundResource(drawable.chart_selection);
				mInvoiceCollectionChartDays.setTextColor(Color.parseColor("#ffffff"));

				mInvoiceCollectionChartMonths.setBackgroundColor(Color.parseColor("#ffffff"));
				mInvoiceCollectionChartMonths.setTextColor(Color.parseColor("#015FA0"));
				
				mInvoiceCollectionDayChart.animateY(1500);
				//mInvoiceCollectionDayChart.animateY(700, Easing.EasingOption.EaseInQuad);

			}
		});
		
		
////////////////////////
		
		mIncomeExpenseChartMonths.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				//getMonths(mTraninvoiceMonthly, mTranCollectionMonthly);
				mIncomeExpenseMonthChart.setVisibility(View.VISIBLE);				
				mIncomeExpenseDayChart.setVisibility(View.GONE);
				
				
				mIncomeExpenseChartMonths.setBackgroundResource(drawable.chart_selection);
				mIncomeExpenseChartMonths.setTextColor(Color.parseColor("#ffffff"));

				mIncomeExpenseChartDays.setBackgroundColor(Color.parseColor("#ffffff"));
				mIncomeExpenseChartDays.setTextColor(Color.parseColor("#015FA0"));
				
				mIncomeExpenseMonthChart.animateY(1500);
				//mInvoiceCollectionMonthChart.animateY(700, Easing.EasingOption.EaseInCubic);

			}
		});
		/*
		 * findViewById(R.id.week).setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { weeks.clear(); mWeeks = new
		 * String[0]; getWeek();
		 * 
		 * } });
		 */

		mIncomeExpenseChartDays.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//dates.clear();
				//mDays = new String[0];
				
				//getDate(mTraninvoiceWeelky, mTranCollectionWeelky);
				mIncomeExpenseDayChart.setVisibility(View.VISIBLE);
				
				mIncomeExpenseMonthChart.setVisibility(View.GONE);
				
				
				mIncomeExpenseChartDays.setBackgroundResource(drawable.chart_selection);
				mIncomeExpenseChartDays.setTextColor(Color.parseColor("#ffffff"));

				mIncomeExpenseChartMonths.setBackgroundColor(Color.parseColor("#ffffff"));
				mIncomeExpenseChartMonths.setTextColor(Color.parseColor("#015FA0"));
				
				mIncomeExpenseDayChart.animateY(1500);
				//mInvoiceCollectionDayChart.animateY(700, Easing.EasingOption.EaseInQuad);

			}
		});
		///////////
		mInvoiceListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				

				invoiceNo = mInvoiceArrhm.get(position).get("InvoiceNo");
				invoiceDate = mInvoiceArrhm.get(position).get("InvoiceDate");
				customerCode = mInvoiceArrhm.get(position).get("CustomerCode");
				customerName = mInvoiceArrhm.get(position).get("CustomerName");

				new LoadPrintPreviewData(false).execute();

			}
		});
		mReceipListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {	
				
				 receiptNo = mReceiptArrhm.get(position).get("ReceiptNo");				 
				 receiptDate = mReceiptArrhm.get(position).get("ReceiptDate");				 
				 customerCode = mReceiptArrhm.get(position).get("CustomerCode");
				 customerName = mReceiptArrhm.get(position).get("CustomerName");
				 
				
				new LoadPrintPreviewData(true).execute();
				// Toast.makeText(getApplicationContext(), receiptNo,
				// Toast.LENGTH_SHORT).show();
			}
		});
	/*	mInvoiceListView.setOnTouchListener(new ListView.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					// Disallow ScrollView to intercept touch events.
					v.getParent().requestDisallowInterceptTouchEvent(true);
					break;

				case MotionEvent.ACTION_UP:
					// Allow ScrollView to intercept touch events.
					v.getParent().requestDisallowInterceptTouchEvent(false);
					break;
				}

				// Handle ListView touch events.
				v.onTouchEvent(event);
				return true;
			}
		});
		mReceipListView.setOnTouchListener(new ListView.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					// Disallow ScrollView to intercept touch events.
					v.getParent().requestDisallowInterceptTouchEvent(true);
					break;

				case MotionEvent.ACTION_UP:
					// Allow ScrollView to intercept touch events.
					v.getParent().requestDisallowInterceptTouchEvent(false);
					break;
				}

				// Handle ListView touch events.
				v.onTouchEvent(event);
				return true;
			}
		});*/
		
		
	}

	private void getinvoiceTransValue(String transtype) {
		if (mTrans.size() > 0) {
			for (DashboardModel dashboardSales : mTrans) {
				String transType = dashboardSales.getTranType();
				String transValue = dashboardSales.getTranValue();
				if (transType.equalsIgnoreCase(transtype)) {
					minvoice.setText("$" + transValue);
					break;
				} else {
					minvoice.setText("$0.00");
				}
			}
		}
	}

	private void getCollectionTransValue(String transtype) {
		if (mTrans.size() > 0) {
			for (DashboardModel dashboardSales : mTrans) {
				String transType = dashboardSales.getTranType();
				String transValue = dashboardSales.getTranValue();
				if (transType.equalsIgnoreCase(transtype)) {
					mInvoice.setText("$" + transValue);
					break;
				} else {
					mInvoice.setText("$0.00");
				}
			}
		}
	}

	private void getOutStandingTransValue() {
		if (mTrans.size() > 0) {
			for (DashboardModel dashboardSales : mTrans) {
				String transType = dashboardSales.getTranType();
				String transValue = dashboardSales.getTranValue();
				if (transType.equalsIgnoreCase("Outstanding")) {
					mOutstanding.setText("$" + transValue);
					break;
				} else {
					mOutstanding.setText("$0.00");
				}
			}

		}

	}

	private class LoadPrintPreviewData extends AsyncTask<Void, Void, Void> {
   boolean isReceipt;
		public LoadPrintPreviewData(boolean flag) {
			isReceipt = flag;
		}

		@Override
		protected void onPreExecute() {
			showProgressView();
			product.clear();
			proddetail.clear();
			productdet.clear();
			listarray.clear();
		}

		@Override
		@SuppressLint("SimpleDateFormat")
		protected Void doInBackground(Void... arg0) {
			try {

				hashValue.put("CompanyCode", companyCode);
				/************** General Settings Call ********************/
				// Start
				general_settings_jsonString = SalesOrderWebService.getSODetail(
						hashValue, "fncGetGeneralSettings");
				Log.d("general_settings",
						general_settings_jsonString);

				general_settings_jsonResponse = new JSONObject(
						general_settings_jsonString);
				general_settings_jsonMainNode = general_settings_jsonResponse
						.optJSONArray("SODetails");

				int general_settings_length = general_settings_jsonMainNode
						.length();

				for (int i = 0; i < general_settings_length; i++) {

					JSONObject jsonChildNode = general_settings_jsonMainNode
							.getJSONObject(i);

					String SettingID = jsonChildNode.optString("SettingID")
							.toString();

					if (SettingID.matches("APPPRINTGROUP")) {
						String settingValue = jsonChildNode.optString(
								"SettingValue").toString();

						if (settingValue.matches("C")) {
							gnrlStngs = settingValue;
							Log.d("result ", gnrlStngs);
						} else if (settingValue.matches("S")) {
							gnrlStngs = settingValue;
							Log.d("result ", gnrlStngs);
						} else if (settingValue.matches("N")) {
							gnrlStngs = settingValue;
							Log.d("result ", gnrlStngs);
						} else {
							gnrlStngs = settingValue;
							Log.d("result ", gnrlStngs);
						}

					}

				}
				// End

				if (gnrlStngs.matches("C")) {
					category_jsonString = SalesOrderWebService.getSODetail(
							hashValue, "fncGetCategory");
					Log.d("category_jsonString ", category_jsonString);

					category_jsonResponse = new JSONObject(category_jsonString);
					category_jsonMainNode = category_jsonResponse
							.optJSONArray("SODetails");

					int category_length = category_jsonMainNode.length();

					for (int i = 0; i < category_length; i++) {
						/****** Get Object for each JSON node. ***********/
						try {
							JSONObject jsonChildNode = category_jsonMainNode
									.getJSONObject(i);

							String categorycode = jsonChildNode.optString(
									"CategoryCode").toString();

							sortproduct.add(categorycode);
						} catch (JSONException e) {

							e.printStackTrace();
						}
					}

				} else if (gnrlStngs.matches("S")) {

					subcategory_jsonString = SalesOrderWebService.getSODetail(
							hashValue, "fncGetSubCategory");
					Log.d("subcategory_jsonString ", subcategory_jsonString);

					subcategory_jsonResponse = new JSONObject(
							subcategory_jsonString);
					subcategory_jsonMainNode = subcategory_jsonResponse
							.optJSONArray("SODetails");

					int subcategory_length = subcategory_jsonMainNode.length();

					for (int i = 0; i < subcategory_length; i++) {
						/****** Get Object for each JSON node. ***********/
						try {
							JSONObject jsonChildNode = subcategory_jsonMainNode
									.getJSONObject(i);

							String categorycode = jsonChildNode.optString(
									"SubCategoryCode").toString();

							sortproduct.add(categorycode);
						} catch (JSONException e) {

							e.printStackTrace();
						}
					}

				}
				///////////////////////////				
				
				if(isReceipt){
					hashValue.put("ReceiptNo", receiptNo);
				receipt_detail_jsonString  = SalesOrderWebService.getSODetail(hashValue,
						"fncGetReceiptDetail");
				receipt_header_by_receipt_no_jsonString = SalesOrderWebService.getSODetail(hashValue,
						"fncGetReceiptHeaderByReceiptNo");
				
				Log.d("receipt_detail", receipt_detail_jsonString);
				Log.d("receipt_by_receipt_no",
						receipt_header_by_receipt_no_jsonString);
				
				
				receipt_detail_jsonResponse = new JSONObject(
						receipt_detail_jsonString);
				receipt_detail_jsonMainNode = receipt_detail_jsonResponse
						.optJSONArray("SODetails");

				receipt_header_by_receipt_no_jsonResponse = new JSONObject(
						receipt_header_by_receipt_no_jsonString);
				receipt_header_by_receipt_no_jsonMainNode = receipt_header_by_receipt_no_jsonResponse
						.optJSONArray("SODetails");
				
				
				int receipt_detail_length = receipt_detail_jsonMainNode.length();
				for (int i = 0; i < receipt_detail_length; i++) {

					try {
						JSONObject	jsonChildNode = receipt_detail_jsonMainNode.getJSONObject(i);

					
						String invoiceno  = jsonChildNode.optString("InvoiceNo").toString();
						listarray.add(invoiceno);
						Log.d("invoiceNo", "-->"+invoiceNo);
					} catch (JSONException e) {

						e.printStackTrace();
					}
				
				}
				
				int receipt_header_by_receipt_no_length = receipt_header_by_receipt_no_jsonMainNode.length();
				if(receipt_header_by_receipt_no_length>0) {
					try {
						JSONObject jsonChildNodes = receipt_header_by_receipt_no_jsonMainNode.getJSONObject(0);

						String paymode = jsonChildNodes.optString("Paymode")
								.toString();
						String bankcode = jsonChildNodes.optString("BankCode")
								.toString();
						String chequeno = jsonChildNodes.optString("ChequeNo")
								.toString();
						String datetime = jsonChildNodes.optString("ChequeDate")
								.toString();

						StringTokenizer tokens = new StringTokenizer(datetime, " ");
						String chequedate = tokens.nextToken();
						In_Cash.setPay_Mode(paymode);
						In_Cash.setBank_code(bankcode);
						In_Cash.setCheck_No(chequeno);
						In_Cash.setCheck_Date(chequedate);

					} catch (JSONException e) {

						e.printStackTrace();
					}
				}
				
				invoiceNo = listarray.get(0);
				
				Log.d("InvoiceNo", invoiceNo);
				}
				
////////////////////////////////////////////
				
				
				hashValue.put("InvoiceNo", invoiceNo);
				/************** Invoice Detail Call ********************/
				invoice_detail_jsonString = SalesOrderWebService.getSODetail(
						hashValue, "fncGetInvoiceDetail");
				/************** Invoice Header By Invoice No Call ********************/
				invoice_header_by_invoice_no_jsonString = SalesOrderWebService
						.getSODetail(hashValue,
								"fncGetInvoiceHeaderByInvoiceNo");

				Log.d("invoice_detail", invoice_detail_jsonString);
				Log.d("invoice by_invoice_no",
						invoice_header_by_invoice_no_jsonString);

				invoice_detail_jsonResponse = new JSONObject(
						invoice_detail_jsonString);
				invoice_detail_jsonMainNode = invoice_detail_jsonResponse
						.optJSONArray("SODetails");

				invoice_header_by_invoice_no_jsonResponse = new JSONObject(
						invoice_header_by_invoice_no_jsonString);
				invoice_header_by_invoice_no_jsonMainNode = invoice_header_by_invoice_no_jsonResponse
						.optJSONArray("SODetails");
				
				
				
				if(isReceipt){
					int invoice_header_by_invoice_no_length = invoice_header_by_invoice_no_jsonMainNode
							.length();
					for (int j = 0; j < invoice_header_by_invoice_no_length; j++) {

						ProductDetails productdetail = new ProductDetails();
						try {
							JSONObject jsonChildNode = invoice_header_by_invoice_no_jsonMainNode.getJSONObject(j);
							productdetail.setItemno(jsonChildNode
									.optString("InvoiceNo").toString());
							String dateTime = jsonChildNode.optString("InvoiceDate")
									.toString();
							StringTokenizer tokens = new StringTokenizer(dateTime, " ");
							String invDate = tokens.nextToken();
							productdetail.setItemdate(invDate);
							productdetail.setItemdisc(jsonChildNode.optString(
									"ItemDiscount").toString());
							productdetail.setBilldisc(jsonChildNode.optString(
									"BillDIscount").toString());
							productdetail.setSubtotal(jsonChildNode.optString(
									"SubTotal").toString());
							productdetail.setTax(twoDecimalPoint(jsonChildNode
									.optDouble("Tax")));
							productdetail.setNettot(jsonChildNode.optString("NetTotal")
									.toString());
							productdetail.setPaidamount(jsonChildNode.optString(
									"PaidAmount").toString());
							productdetail.setRemarks(jsonChildNode.optString("Remarks")
									.toString());
							productdetail.setTotaloutstanding(jsonChildNode.optString(
									"TotalBalance").toString());

						} catch (JSONException e) {

							e.printStackTrace();
						}

						int invoice_detail_length = invoice_detail_jsonMainNode.length();

						for (int k = 0; k < invoice_detail_length; k++) {

							 ;
							ProdDetails proddetails = new ProdDetails();
							try {
								JSONObject	jsonChildNodes = invoice_detail_jsonMainNode.getJSONObject(k);
								int s = k + 1;
								proddetails.setSno(String.valueOf(s));
								proddetails.setItemnum(jsonChildNodes.optString(
										"InvoiceNo").toString());
								proddetails.setItemcode(jsonChildNodes.optString(
										"ProductCode").toString());
								proddetails.setDescription(jsonChildNodes.optString(
										"ProductName").toString());

								String recqty = jsonChildNodes.optString("Qty")
										.toString();
								if (recqty.contains(".")) {
									StringTokenizer tokens = new StringTokenizer(
											recqty, ".");
									String qty = tokens.nextToken();
									proddetails.setQty(qty);
								} else {
									proddetails.setQty(recqty);
								}

								proddetails.setPrice(jsonChildNodes.optString("Price")
										.toString());
								proddetails.setTotal(jsonChildNodes.optString("Total")
										.toString());

								proddetails.setFocqty(jsonChildNodes
										.optDouble("FOCQty"));

								proddetails.setExchangeqty(jsonChildNodes
										.optDouble("ExchangeQty"));

								if (gnrlStngs.matches("C")) {
									proddetails.setSortproduct(jsonChildNodes
											.optString("CategoryCode").toString());
								} else if (gnrlStngs.matches("S")) {
									proddetails.setSortproduct(jsonChildNodes
											.optString("SubCategoryCode").toString());
								} else if (gnrlStngs.matches("N")) {
									proddetails.setSortproduct("");
								} else {
									proddetails.setSortproduct("");
								}
								proddetail.add(proddetails);
							} catch (JSONException e) {

								e.printStackTrace();
							}
						}
						productdetail.setProductsDetails(proddetail);
						productdet.add(productdetail);
					}
					
					
				}else{
				

				int invoice_detail_length = invoice_detail_jsonMainNode
						.length();

				for (int i = 0; i < invoice_detail_length; i++) {
					/****** Get Object for each JSON node. ***********/
					ProductDetails productdetail = new ProductDetails();
					try {
						JSONObject jsonChildNode = invoice_detail_jsonMainNode
								.getJSONObject(i);
						int s = i + 1;
						productdetail.setSno(String.valueOf(s));

						String slNo = jsonChildNode.optString("slNo")
								.toString();
						String productCode = jsonChildNode.optString(
								"ProductCode").toString();
						productdetail.setItemcode(productCode);

						productdetail.setDescription(jsonChildNode.optString(
								"ProductName").toString());

						String invoiceqty = jsonChildNode.optString("Qty")
								.toString();
						if (invoiceqty.contains(".")) {
							StringTokenizer tokens = new StringTokenizer(
									invoiceqty, ".");
							String qty = tokens.nextToken();
							productdetail.setQty(qty);
						} else {
							productdetail.setQty(invoiceqty);
						}

						String pricevalue = jsonChildNode.optString("Price")
								.toString();
						String totalvalve = jsonChildNode.optString("Total")
								.toString();

						String issueQty = jsonChildNode.optString("IssueQty")
								.toString();
						String returnQty = jsonChildNode.optString("ReturnQty")
								.toString();

						if (issueQty != null && !issueQty.isEmpty()) {

							productdetail.setIssueQty(issueQty.split("\\.")[0]);
						} else {
							productdetail.setIssueQty("0");
						}
						if (returnQty != null && !returnQty.isEmpty()) {

							productdetail
									.setReturnQty(returnQty.split("\\.")[0]);
						} else {
							productdetail.setReturnQty("0");
						}

						productdetail.setFocqty(jsonChildNode
								.optDouble("FOCQty"));

						productdetail.setExchangeqty(jsonChildNode
								.optDouble("ExchangeQty"));
						if (pricevalue.contains(".")) {
							productdetail.setPrice(pricevalue);
						} else {
							productdetail.setPrice(pricevalue + decimalpts);
						}
						if (totalvalve.contains(".")) {

							productdetail.setTotal(totalvalve);
						} else {

							productdetail.setTotal(totalvalve + decimalpts);
						}
						if (gnrlStngs.matches("C")) {
							productdetail.setSortproduct(jsonChildNode
									.optString("CategoryCode").toString());
						} else if (gnrlStngs.matches("S")) {
							productdetail.setSortproduct(jsonChildNode
									.optString("SubCategoryCode").toString());
						} else if (gnrlStngs.matches("N")) {
							productdetail.setSortproduct("");
						} else {
							productdetail.setSortproduct("");
						}

						product.add(productdetail);

					} catch (JSONException e) {

						e.printStackTrace();
					}
				}

				int invoice_header_by_invoice_no_length = invoice_header_by_invoice_no_jsonMainNode
						.length();

				for (int i = 0; i < invoice_header_by_invoice_no_length; i++) {

					ProductDetails productdetail = new ProductDetails();
					try {
						JSONObject jsonChildNode = invoice_header_by_invoice_no_jsonMainNode
								.getJSONObject(i);

						String itemdiscvalue = jsonChildNode.optString(
								"ItemDiscount").toString();
						String billdiscvalue = jsonChildNode.optString(
								"BillDIscount").toString();
						String subtotalvalue = jsonChildNode.optString(
								"SubTotal").toString();

						productdetail.setTax(twoDecimalPoint(jsonChildNode
								.optDouble("Tax")));
						String nettotalvalue = jsonChildNode.optString(
								"NetTotal").toString();

						if (itemdiscvalue.contains(".")
								&& billdiscvalue.contains(".")
								&& subtotalvalue.contains(".")
								&& nettotalvalue.contains(".")) {
							productdetail.setItemdisc(itemdiscvalue);
							productdetail.setBilldisc(billdiscvalue);
							productdetail.setSubtotal(subtotalvalue);
							productdetail.setNettot(nettotalvalue);
						} else {
							productdetail.setItemdisc(itemdiscvalue
									+ decimalpts);
							productdetail.setBilldisc(billdiscvalue
									+ decimalpts);
							productdetail.setSubtotal(subtotalvalue
									+ decimalpts);
							productdetail.setNettot(nettotalvalue + decimalpts);
						}
						productdetail.setRemarks(jsonChildNode.optString(
								"Remarks").toString());
						productdetail.setTotaloutstanding(jsonChildNode
								.optString("TotalBalance").toString());
						productdet.add(productdetail);

					} catch (JSONException e) {

						e.printStackTrace();
					}

				}
				
				
				}
				

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dismissProgressView();
			if(isReceipt){
				Intent i = new Intent(getApplicationContext(),
						ReceiptPrintPreview.class);
				i.putExtra("no", receiptNo);
				i.putExtra("date", receiptDate);
				i.putExtra("customerCode", customerCode);
				i.putExtra("customerName", customerName);
				i.putExtra("Key", "Receipt");
				i.putExtra("sort", sortproduct);
				i.putExtra("gnrlStngs", gnrlStngs);
				PreviewPojo.setReceiptproducts(proddetail);
				PreviewPojo.setProductsDetails(productdet);
				startActivity(i);
			}else{

			mIntent.setClass(getApplicationContext(), InvoicePrintPreview.class);
			mIntent.putExtra("invNo", invoiceNo);
			mIntent.putExtra("invDate", invoiceDate);
			mIntent.putExtra("customerCode", customerCode);
			mIntent.putExtra("customerName", customerName);
			mIntent.putExtra("sort", sortproduct);
			mIntent.putExtra("gnrlStngs", gnrlStngs);
			mIntent.putExtra("tranType","");
			mIntent.putExtra("dono","");
			PreviewPojo.setProducts(product);
			PreviewPojo.setProductsDetails(productdet);
			startActivity(mIntent);
			}
		}

	}

	private class LoadInvoiceChartData extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			showProgressView();
			mTranPaymentExpenseWeelky.clear();
			mScrollView.setEnabled(false);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			float mPaymentExpenseDay1 = 0.00f,mPaymentExpenseDay2 = 0.00f,
					mPaymentExpenseDay3 = 0.00f,mPaymentExpenseDay4 = 0.00f,
					mPaymentExpenseDay5 = 0.00f,mPaymentExpenseDay6 = 0.00f,
					mPaymentExpenseDay7 = 0.00f;
			
			float mPaymentExpenseMonth1 = 0.00f, mPaymentExpenseMonth2 = 0.00f, mPaymentExpenseMonth3 = 0.00f,
			mPaymentExpenseMonth4 = 0.00f, mPaymentExpenseMonth5 = 0.00f, mPaymentExpenseMonth6 = 0.00f, mPaymentExpenseMonth7 = 0.00f,
			mPaymentExpenseMonth8 = 0.00f, mPaymentExpenseMonth9 = 0.00f, mPaymentExpenseMonth10 = 0.00f, mPaymentExpenseMonth11 = 0.00f, mPaymentExpenseMonth12 = 0.00f;

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			Date date;
			try {
				hashValue.put("CompanyCode", companyCode);

				serverdate_jsonString = SalesOrderWebService.getSODetail(
						hashValue, "fncGetServerDate");
				Log.d("serverdate_jsonString ", serverdate_jsonString);
				serverdate_jsonResponse = new JSONObject(serverdate_jsonString);
				serverdate_jsonMainNode = serverdate_jsonResponse
						.optJSONArray("SODetails");

				JSONObject jsonobject = serverdate_jsonMainNode
						.getJSONObject(0);
				serverDate = jsonobject.getString("ServerDate");

				value_txt= serverDate.replace( '-' ,'/' );

				Log.d("ServerDatevalid","-->"+value_txt+"  "+serverDate );

				date = sdf.parse(value_txt);


				dashboard_jsonString = SalesOrderWebService.getSODetail(
						hashValue, "fncGetDashboardSalesSummary");
				dashboardinvoiceWeely_jsonString = SalesOrderWebService
						.getSODetail(hashValue, "fncGetDashboardSalesWeelky");
				dashboardinvoiceMonthly_jsonString = SalesOrderWebService
						.getSODetail(hashValue, "fncGetDashboardSalesMonthly");

				hashValue.put("FromDate", DateTime.date(value_txt));
				hashValue.put("ToDate", value_txt);

				invoice_jsonString = SalesOrderWebService.getSODetail(
						hashValue, "fncGetInvoiceHeader");

				receipt_jsonString = SalesOrderWebService.getSODetail(
						hashValue, "fncGetReceiptHeader");

				Log.d("dashboard_jsonString ", dashboard_jsonString);
				Log.d("dashboardinvoiceWeely",
						dashboardinvoiceWeely_jsonString);
				Log.d("dashboardinvoiceMonthly",
						dashboardinvoiceMonthly_jsonString);
				Log.d("invoice_jsonString ", invoice_jsonString);
				Log.d("receipt_jsonString ", receipt_jsonString);

				dashboard_jsonResponse = new JSONObject(dashboard_jsonString);
				dashboard_jsonMainNode = dashboard_jsonResponse
						.optJSONArray("SODetails");

				dashboardinvoiceWeely_jsonResponse = new JSONObject(
						dashboardinvoiceWeely_jsonString);
				dashboardinvoiceWeely_jsonMainNode = dashboardinvoiceWeely_jsonResponse
						.optJSONArray("SODetails");

				dashboardinvoiceMonthly_jsonResponse = new JSONObject(
						dashboardinvoiceMonthly_jsonString);
				dashboardinvoiceMonthly_jsonMainNode = dashboardinvoiceMonthly_jsonResponse
						.optJSONArray("SODetails");

				invoice_jsonResponse = new JSONObject(invoice_jsonString);
				invoice_jsonMainNode = invoice_jsonResponse
						.optJSONArray("SODetails");

				receipt_jsonResponse = new JSONObject(receipt_jsonString);
				receipt_jsonMainNode = receipt_jsonResponse
						.optJSONArray("SODetails");

				int dashboardLength = dashboard_jsonMainNode.length();
				for (int m = 0; m < dashboardLength; m++) {
					JSONObject object = dashboard_jsonMainNode.getJSONObject(m);
					DashboardModel dashboardSales = new DashboardModel();
					dashboardSales.setTranType(object.getString("TranType"));
					dashboardSales.setTranValue(object.getString("TranValue"));
					mTrans.add(dashboardSales);
				}

				int dashboardinvoiceWeelyLength = dashboardinvoiceWeely_jsonMainNode
						.length();
				for (int i = 0; i < dashboardinvoiceWeelyLength; i++) {
					JSONObject object = dashboardinvoiceWeely_jsonMainNode
							.getJSONObject(i);

					String tranType = object.getString("TranType");
					if (tranType.equalsIgnoreCase("Sales")) {

						mTraninvoiceWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day1")).floatValue());
						mTraninvoiceWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day2")).floatValue());
						mTraninvoiceWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day3")).floatValue());
						mTraninvoiceWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day4")).floatValue());
						mTraninvoiceWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day5")).floatValue());
						mTraninvoiceWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day6")).floatValue());
						mTraninvoiceWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day7")).floatValue());

					} else if (tranType.equalsIgnoreCase("Collection")) {

						mTranCollectionWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day1")).floatValue());
						mTranCollectionWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day2")).floatValue());
						mTranCollectionWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day3")).floatValue());
						mTranCollectionWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day4")).floatValue());
						mTranCollectionWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day5")).floatValue());
						mTranCollectionWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day6")).floatValue());
						mTranCollectionWeelky.add(BigDecimal.valueOf(
								object.getDouble("Day7")).floatValue());

					}
					else if (tranType.equalsIgnoreCase("Payments") || tranType.equalsIgnoreCase("Expense")) 
					{						
						float mDay1 = BigDecimal.valueOf(object.getDouble("Day1")).floatValue();
						float mDay2 = BigDecimal.valueOf(object.getDouble("Day2")).floatValue();
						float mDay3 = BigDecimal.valueOf(object.getDouble("Day3")).floatValue();
						float mDay4 = BigDecimal.valueOf(object.getDouble("Day4")).floatValue();
						float mDay5 = BigDecimal.valueOf(object.getDouble("Day5")).floatValue();
						float mDay6 = BigDecimal.valueOf(object.getDouble("Day6")).floatValue();
						float mDay7 = BigDecimal.valueOf(object.getDouble("Day7")).floatValue();
						
						mPaymentExpenseDay1+=mDay1;
						mPaymentExpenseDay2+=mDay2;
						mPaymentExpenseDay3+=mDay3;
						mPaymentExpenseDay4+=mDay4;
						mPaymentExpenseDay5+=mDay5;
						mPaymentExpenseDay6+=mDay6;
						mPaymentExpenseDay7+=mDay7;
						
					}					
				}
				
				mTranPaymentExpenseWeelky.add(mPaymentExpenseDay1);
				mTranPaymentExpenseWeelky.add(mPaymentExpenseDay2);
				mTranPaymentExpenseWeelky.add(mPaymentExpenseDay3);
				mTranPaymentExpenseWeelky.add(mPaymentExpenseDay4);
				mTranPaymentExpenseWeelky.add(mPaymentExpenseDay5);
				mTranPaymentExpenseWeelky.add(mPaymentExpenseDay6);
				mTranPaymentExpenseWeelky.add(mPaymentExpenseDay7);
				
				int dashboardinvoiceMonthlyLength = dashboardinvoiceMonthly_jsonMainNode
						.length();
				for (int j = 0; j < dashboardinvoiceMonthlyLength; j++) {
					JSONObject object = dashboardinvoiceMonthly_jsonMainNode
							.getJSONObject(j);

					String tranType = object.getString("TranType");
					if (tranType.equalsIgnoreCase("Sales")) {

						mTraninvoiceMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month1")).floatValue());
						mTraninvoiceMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month2")).floatValue());
						mTraninvoiceMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month3")).floatValue());
						mTraninvoiceMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month4")).floatValue());
						mTraninvoiceMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month5")).floatValue());
						mTraninvoiceMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month6")).floatValue());
						mTraninvoiceMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month7")).floatValue());
						mTraninvoiceMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month8")).floatValue());
						mTraninvoiceMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month9")).floatValue());
						mTraninvoiceMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month10")).floatValue());
						mTraninvoiceMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month11")).floatValue());
						mTraninvoiceMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month12")).floatValue());

					} else if (tranType.equalsIgnoreCase("Collection")) {

						mTranCollectionMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month1")).floatValue());
						mTranCollectionMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month2")).floatValue());
						mTranCollectionMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month3")).floatValue());
						mTranCollectionMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month4")).floatValue());
						mTranCollectionMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month5")).floatValue());
						mTranCollectionMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month6")).floatValue());
						mTranCollectionMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month7")).floatValue());
						mTranCollectionMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month8")).floatValue());
						mTranCollectionMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month9")).floatValue());
						mTranCollectionMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month10")).floatValue());
						mTranCollectionMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month11")).floatValue());
						mTranCollectionMonthly.add(BigDecimal.valueOf(
								object.getDouble("Month12")).floatValue());
						
					}
					else if (tranType.equalsIgnoreCase("Payments") || tranType.equalsIgnoreCase("Expense")) 
					{						
						float mMonth1 = BigDecimal.valueOf(object.getDouble("Month1")).floatValue();
						float mMonth2 = BigDecimal.valueOf(object.getDouble("Month2")).floatValue();
						float mMonth3 = BigDecimal.valueOf(object.getDouble("Month3")).floatValue();
						float mMonth4 = BigDecimal.valueOf(object.getDouble("Month4")).floatValue();
						float mMonth5 = BigDecimal.valueOf(object.getDouble("Month5")).floatValue();
						float mMonth6 = BigDecimal.valueOf(object.getDouble("Month6")).floatValue();
						float mMonth7 = BigDecimal.valueOf(object.getDouble("Month7")).floatValue();
						float mMonth8 = BigDecimal.valueOf(object.getDouble("Month8")).floatValue();
						float mMonth9 = BigDecimal.valueOf(object.getDouble("Month9")).floatValue();
						float mMonth10 = BigDecimal.valueOf(object.getDouble("Month10")).floatValue();
						float mMonth11= BigDecimal.valueOf(object.getDouble("Month11")).floatValue();
						float mMonth12= BigDecimal.valueOf(object.getDouble("Month12")).floatValue();
						
						mPaymentExpenseMonth1+=mMonth1;
						mPaymentExpenseMonth2+=mMonth2;
						mPaymentExpenseMonth3+=mMonth3;
						mPaymentExpenseMonth4+=mMonth4;
						mPaymentExpenseMonth5+=mMonth5;
						mPaymentExpenseMonth6+=mMonth6;
						mPaymentExpenseMonth7+=mMonth7;
						mPaymentExpenseMonth8+=mMonth8;
						mPaymentExpenseMonth9+=mMonth9;
						mPaymentExpenseMonth10+=mMonth10;
						mPaymentExpenseMonth11+=mMonth11;
						mPaymentExpenseMonth12+=mMonth12;
						
					}
				}
				mTranPaymentExpenseMonth.add(mPaymentExpenseMonth1);
				mTranPaymentExpenseMonth.add(mPaymentExpenseMonth2);
				mTranPaymentExpenseMonth.add(mPaymentExpenseMonth3);
				mTranPaymentExpenseMonth.add(mPaymentExpenseMonth4);
				mTranPaymentExpenseMonth.add(mPaymentExpenseMonth5);
				mTranPaymentExpenseMonth.add(mPaymentExpenseMonth6);
				mTranPaymentExpenseMonth.add(mPaymentExpenseMonth7);
				mTranPaymentExpenseMonth.add(mPaymentExpenseMonth8);
				mTranPaymentExpenseMonth.add(mPaymentExpenseMonth9);
				mTranPaymentExpenseMonth.add(mPaymentExpenseMonth10);
				mTranPaymentExpenseMonth.add(mPaymentExpenseMonth11);
				mTranPaymentExpenseMonth.add(mPaymentExpenseMonth12);
				
				int invoicelength = invoice_jsonMainNode.length();
				for (int k = 0; k < invoicelength; k++) {
					JSONObject object = invoice_jsonMainNode.getJSONObject(k);

					String invoiceNo = object.getString("InvoiceNo");
					String invoiceDate = object.getString("InvoiceDate");

					// String LocationCode = object.getString("LocationCode");
					 String customerCode = object.getString("CustomerCode");
					String customerName = object.getString("CustomerName");
					String netTotal = object.getString("NetTotal");
					// String PaidAmount = object.getString("PaidAmount");
					// String CreditAmount = object.getString("CreditAmount");
					// String BalanceAmount = object.getString("BalanceAmount");
					// String Remarks = object.getString("Remarks");
					// String TotalBalance = object.getString("TotalBalance");

					invoice: for (int day = 0; day < 30; day++) {
						Date newDate = DateTime.subtractDays(date, day);
						String dates = sdf.format(newDate) + " " + "00:00:00";
						if (mInvoiceArrhm.size() != 5) {
						if (!invoiceDate.matches(dates)) {

								Log.d("invoiceNo", "-->" + invoiceNo);
								Log.d("invoiceDate", "-->" + invoiceDate);
								//Log.d("dates", "-->" + dates);

								HashMap<String, String> hm = new HashMap<String, String>();
								hm.put("InvoiceNo", invoiceNo);
								hm.put("InvoiceDate", invoiceDate);
								hm.put("CustomerCode", customerCode);
								hm.put("CustomerName", customerName);
								hm.put("Amount", netTotal);
								mInvoiceArrhm.add(hm);
								// day+=1;
								Log.d("mInvoiceArrhm",
										"-->" + mInvoiceArrhm.toString());
							} /*else if (mInvoiceArrhm.size() == 5) {
								Log.d(" invoice break",
										"--> break invoice");
								day=30;
								
							}*/

						}else if (mInvoiceArrhm.size() == 5) {							
							
							break invoice;
						}
					}
				}

				int receiptlength = receipt_jsonMainNode.length();
				for (int l = 0; l < receiptlength; l++) {
					JSONObject object = receipt_jsonMainNode.getJSONObject(l);

					String receiptNo = object.getString("ReceiptNo");
					String receiptDate = object.getString("ReceiptDate");
					 String customerCode = object.getString("CustomerCode");
					String customerName = object.getString("CustomerName");
					String paidAmount = object.getString("PaidAmount");
					//String creditAmount = object.getString("CreditAmount");

					receipt: for (int day = 0; day < 30; day++) {
						Date newDate = DateTime.subtractDays(date, day);
						String dates = sdf.format(newDate) + " " + "00:00:00";

						if (mReceiptArrhm.size() != 5) {
						if (receiptDate.matches(dates)) {						
							

								Log.d("receiptNo", "-->" + receiptNo);
								Log.d("receiptDate", "-->" + receiptDate);
								//Log.d("dates", "-->" + dates);

								HashMap<String, String> hm = new HashMap<String, String>();
								hm.put("ReceiptNo", receiptNo);
								hm.put("ReceiptDate", receiptDate);
								hm.put("CustomerCode", customerCode);
								hm.put("CustomerName", customerName);
								hm.put("Amount", paidAmount);
								mReceiptArrhm.add(hm);
								// day+=1;
								Log.d("mReceiptArrhm",
										"-->" + mReceiptArrhm.toString());
							} /*else if (mReceiptArrhm.size() == 5) {
								day=30;
								
							}*/

						}else if (mReceiptArrhm.size() == 5) {
						
							break receipt;
						}

					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				dismissProgressView();
				if (value_txt != null && !value_txt.isEmpty()) {
					String serverMonth = value_txt.split("\\/")[1];
					Log.d("serverMonth", "-->" + serverMonth);
					String currentMonth = mMonths[Integer.valueOf(serverMonth) - 1];
					Log.d("month", "-->" + currentMonth);

					boolean isCurrentMonth = false;
					for (int i = mMonths.length - 1; i >= 0; i--) {
						if (isCurrentMonth) {
							sMonthsList.add(mMonths[i]);
						}
						String month = mMonths[i];
						if (month.matches(currentMonth)) {
							sMonthsList.add(mMonths[i]);
							isCurrentMonth = true;
						}
					}
					outerLoop:
					for (int j = mMonths.length - 1; j >= 0; j--) {
						innerLoop:
						for (int i = sMonthsList.size() - 1; i >= 0; i--) {
							String monthI = sMonthsList.get(i);
							String monthJ = mMonths[j];
							if (!monthI.matches(monthJ)) {
								sMonthsList.add(mMonths[j]);
								break innerLoop;
							}
						}
						if (sMonthsList.size() == 12) {
							break outerLoop;
						}
					}
				}
				getOutStandingTransValue();
				getinvoiceTransValue("DaySales");
				getCollectionTransValue("DayCollection");
				loadAdapterData();

				Log.d("PaymentExpenseWeelky", "" + mTranPaymentExpenseWeelky);
				// Default load date
				getDate(mTraninvoiceWeelky, mTranCollectionWeelky, mTranPaymentExpenseWeelky);
				getMonths(mTraninvoiceMonthly, mTranCollectionMonthly, mTranPaymentExpenseMonth);
				mInvoiceDay.setBackgroundResource(drawable.sales_selection);
				mCollectionDay.setBackgroundResource(drawable.collection_selection);
				//Default Invoice vs Collection chart selection
				mInvoiceCollectionChartDays.setBackgroundResource(drawable.chart_selection);
				mInvoiceCollectionChartDays.setTextColor(Color.parseColor("#ffffff"));
				//Default Income vs Expense chart selection
				mIncomeExpenseChartDays.setBackgroundResource(drawable.chart_selection);
				mIncomeExpenseChartDays.setTextColor(Color.parseColor("#ffffff"));
				mScrollView.setEnabled(true);

            /*mMonths = new String[] {  "Aug", "Sep", "Oct", "Nov", "Dec","Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul" };

            String month="";

		if(month.matches("Jan")){
            mMonths = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		}else if(month.matches("Feb")){
            mMonths = new String[] {"Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan" };
		}else if(month.matches("Mar")) {
            mMonths = new String[]{"Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan","Feb"};
        }else if(month.matches("Apr")) {
            mMonths = new String[]{"Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan","Feb","Mar"};
        }*/
			}catch (Exception e){
				e.printStackTrace();
			}
        }
	}

	private void loadAdapterData() {
		String[] from = new String[] { "CustomerName", "Amount" };
		int[] to = new int[] { R.id.item1, R.id.item2 };

		if(mInvoiceArrhm.size()>0){
			mSimpleAdapter = new SimpleAdapter(this, mInvoiceArrhm,
					R.layout.dashboard_list_item, from, to);
			mInvoiceListView.setAdapter(mSimpleAdapter);
			
			mInvoiceListEmpty.setVisibility(View.GONE);
			mInvoiceListView.setVisibility(View.VISIBLE);
		}else{
			mInvoiceListEmpty.setVisibility(View.VISIBLE);
			mInvoiceListView.setVisibility(View.GONE);
		}

		if(mReceiptArrhm.size()>0){
			mSimpleAdapter = new SimpleAdapter(this, mReceiptArrhm,
					R.layout.dashboard_list_item, from, to);
			mReceipListView.setAdapter(mSimpleAdapter);
		
			mPaymentListEmpty.setVisibility(View.GONE);
			mReceipListView.setVisibility(View.VISIBLE);
		}else{
			mPaymentListEmpty.setVisibility(View.VISIBLE);
			mReceipListView.setVisibility(View.GONE);
		}
		

		

		mScrollView.fullScroll(View.FOCUS_UP);

	}	
	public void getMonths(ArrayList<Float> invoiceMonthly,
			ArrayList<Float> collectionMonthly,ArrayList<Float> paymentExpenseMonthly) {
		ArrayList<BarDataSet> incomeExpenseDataSets = new ArrayList<BarDataSet>();
		ArrayList<BarEntry> incomeValueSet = new ArrayList<>();
		ArrayList<BarEntry> expenseValueSet = new ArrayList<>();
		try {
		
		//Invoice vs Collection Month Chart			
		/**starts**/
		data = new CombinedData(sMonthsList);
		data.setData(generateBarData(invoiceMonthly, "Monthly Invoice"));
		data.setData(generateLineData(collectionMonthly, "Monthly Collection"));		
		mInvoiceCollectionMonthChart.setData(data);
		mInvoiceCollectionMonthChart.invalidate();
		/**ends**/
		
		
		//Remove Invoice vs Collection Line Chart values
		for (DataSet<?> set : mInvoiceCollectionMonthChart.getData().getDataSets()) {
			if (set instanceof LineDataSet)
				set.setDrawValues(!set.isDrawValuesEnabled());
		}
       //Remove Invoice vs Collection Bar Chart values
		for (DataSet<?> set : mInvoiceCollectionMonthChart.getData().getDataSets()) {
			if (set instanceof BarDataSet)
				set.setDrawValues(!set.isDrawValuesEnabled());
		}
		
		
		//Income vs Expense Month Chart
		/**starts**/
		//Income Data Values
		float[] incomefloatList = new float[collectionMonthly.size()];
		
		for(int i = 0; i < collectionMonthly.size(); ++i) {
			incomefloatList[i] = collectionMonthly.get(i);
		    BarEntry barEntry = new BarEntry(incomefloatList[i], i); 
		    incomeValueSet.add(barEntry);
		}
		
		//Expense Data Values			
		float[] expensefloatList = new float[paymentExpenseMonthly.size()];			
		for(int i = 0; i < paymentExpenseMonthly.size(); ++i) {
			expensefloatList[i] = paymentExpenseMonthly.get(i);
		    BarEntry barEntry = new BarEntry(expensefloatList[i], i); 
		    expenseValueSet.add(barEntry);
		}			
		BarDataSet incomeBarDataSet = new BarDataSet(incomeValueSet, "Income");
		incomeBarDataSet.setColor(Color.rgb(71,165,51));
		
		BarDataSet expenseBarDataSet = new BarDataSet(expenseValueSet, "Expense");
		expenseBarDataSet.setColor(	Color.rgb(228,65,65));	
		incomeExpenseDataSets.add(incomeBarDataSet);
		incomeExpenseDataSets.add(expenseBarDataSet);
		
		
		BarData bardata = new BarData(sMonthsList, incomeExpenseDataSets);
		mIncomeExpenseMonthChart.setData(bardata);
		mIncomeExpenseMonthChart.setDescription("");
		mIncomeExpenseMonthChart.animateXY(2000, 2000);		
		mIncomeExpenseMonthChart.invalidate();
		/**ends**/
		
		//Remove Income vs Expense Month Bar Chart values
				for (DataSet<?> set : mIncomeExpenseMonthChart.getData().getDataSets()) {
					if (set instanceof BarDataSet)
						set.setDrawValues(!set.isDrawValuesEnabled());
				}
				
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	@SuppressLint("SimpleDateFormat")
	public void getDate(ArrayList<Float> invoiceWeekly,
			ArrayList<Float> collectionWeekly,ArrayList<Float> paymentExpenseWeekly) {		
		ArrayList<BarDataSet> incomeExpenseDataSets = new ArrayList<BarDataSet>();
		ArrayList<BarEntry> incomeValueSet = new ArrayList<>();
		ArrayList<BarEntry> expenseValueSet = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

		int day = 0;
		try {
			Date date = sdf.parse(value_txt);
			while (day < 7) {
				Date newDate = DateTime.subtractDays(date, day);
				System.out.println(sdf.format(newDate));
				dates.add(sdf.format(newDate));
				day += 1;
			}
			mDays = dates.toArray(new String[dates.size()]);
			
			//Invoice vs Collection Day Chart			
			/**starts**/
			data = new CombinedData(mDays);
			data.setData(generateBarData(invoiceWeekly, "Day Invoice"));
			data.setData(generateLineData(collectionWeekly, "Day Collection"));
			mInvoiceCollectionDayChart.setData(data);
			mInvoiceCollectionDayChart.animateY(1500);
			mInvoiceCollectionDayChart.invalidate();
			/**ends**/
			
			
			//Remove Invoice vs Collection Day Line Chart values
			for (DataSet<?> set : mInvoiceCollectionDayChart.getData().getDataSets()) {
				if (set instanceof LineDataSet)
					set.setDrawValues(!set.isDrawValuesEnabled());
			}
		
			
			//Remove Invoice vs Collection Day Bar chart values
			for (DataSet<?> set : mInvoiceCollectionDayChart.getData().getDataSets()) {
				if (set instanceof BarDataSet)
					set.setDrawValues(!set.isDrawValuesEnabled());
			}
		
			
			//Income vs Expense Day Chart
			/**starts**/
			//Income Data Values
			float[] incomefloatList = new float[collectionWeekly.size()];
			
			for(int i = 0; i < collectionWeekly.size(); ++i) {
				incomefloatList[i] = collectionWeekly.get(i);
			    BarEntry barEntry = new BarEntry(incomefloatList[i], i); 
			    incomeValueSet.add(barEntry);
			}
			
			//Expense Data Values			
			float[] expensefloatList = new float[paymentExpenseWeekly.size()];			
			for(int i = 0; i < paymentExpenseWeekly.size(); ++i) {
				expensefloatList[i] = paymentExpenseWeekly.get(i);
			    BarEntry barEntry = new BarEntry(expensefloatList[i], i); 
			    expenseValueSet.add(barEntry);
			}			
			BarDataSet incomeBarDataSet = new BarDataSet(incomeValueSet, "Income");
			incomeBarDataSet.setColor(Color.rgb(71,165,51));
			
			BarDataSet expenseBarDataSet = new BarDataSet(expenseValueSet, "Expense");
			expenseBarDataSet.setColor(Color.rgb(228,65,65));
			incomeExpenseDataSets.add(incomeBarDataSet);
			incomeExpenseDataSets.add(expenseBarDataSet);
			
			
			BarData bardata = new BarData(dates, incomeExpenseDataSets);
			mIncomeExpenseDayChart.setData(bardata);
			mIncomeExpenseDayChart.setDescription("");
			mIncomeExpenseDayChart.animateXY(2000, 2000);
			mIncomeExpenseDayChart.invalidate();
			/**ends**/
			
			//Remove Income vs Expense Day Bar Chart values
			for (DataSet<?> set : mIncomeExpenseDayChart.getData().getDataSets()) {
				if (set instanceof BarDataSet)
					set.setDrawValues(!set.isDrawValuesEnabled());
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	
	
	
	/*
	 * @SuppressLint("SimpleDateFormat") public void getWeek() {
	 * 
	 * SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
	 * GregorianCalendar cal = new GregorianCalendar(); sdf.setCalendar(cal);
	 * 
	 * System.out.println(1 + "- Week ," + '\n' + sdf.format(cal.getTime()));
	 * 
	 * weeks.add(1 + "- Week ," + '\n' + sdf.format(cal.getTime()));
	 * 
	 * for (int i = 0; i < 3; i++) { int week = i + 2;
	 * 
	 * //cal.set(GregorianCalendar.DAY_OF_WEEK, //
	 * GregorianCalendar.DAY_OF_WEEK);
	 * 
	 * cal.add(GregorianCalendar.DAY_OF_WEEK, -7); Date date = cal.getTime();
	 * System.out.println(week + "- Week ," + '\n' + sdf.format(date));
	 * weeks.add(week + "-Week ," + '\n' + sdf.format(date));
	 * 
	 * } mWeeks = weeks.toArray(new String[weeks.size()]); itemcount = 4; data =
	 * new CombinedData(mWeeks); data.setData(generateLineData());
	 * data.setData(generateBarData()); mChart.setData(data);
	 * mChart.invalidate();
	 * 
	 * }
	 */
	// Line Chart
	private LineData generateLineData(ArrayList<Float> lineChartData,
			String title) {

		LineData d = new LineData();

		ArrayList<Entry> entries = new ArrayList<Entry>();
		for (int index = 0; index < lineChartData.size(); index++) {
			// entries.add(new Entry(getRandom(15, 5), index)); //10
			entries.add(new Entry(lineChartData.get(index), index));
		}
		LineDataSet set = new LineDataSet(entries, title);
		set.setColor(Color.rgb(1, 95, 160));
		set.setLineWidth(3f);
		set.setCircleColor(Color.rgb(1, 95, 160));
		set.setCircleSize(5f);
		set.setFillColor(Color.rgb(1, 95, 160));
		set.setDrawCubic(true);
		set.setDrawValues(true);
		set.setValueTextSize(10f);
		set.setValueTextColor(Color.rgb(1, 95, 160));

		set.setAxisDependency(YAxis.AxisDependency.LEFT);

		d.addDataSet(set);

		return d;
	}

	// Bar Chart
	private BarData generateBarData(ArrayList<Float> barChartData, String title) {

		BarData d = new BarData();

		ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
		for (int index = 0; index < barChartData.size(); index++) {
			// entries.add(new BarEntry(getRandom(15, 15), index)); //30

			entries.add(new BarEntry(barChartData.get(index), index));
		}

		BarDataSet set = new BarDataSet(entries, title);
		// set.setColor(Color.rgb(60, 220, 78));
		set.setColors(ColorTemplate.JOYFUL_COLORS);
		set.setValueTextColor(Color.rgb(60, 220, 78));
		set.setValueTextSize(10f);
		set.setAxisDependency(YAxis.AxisDependency.LEFT);
		d.addDataSet(set);		

		return d;
	}

	// Bubble Chart
	protected BubbleData generateBubbleData() {

		BubbleData bd = new BubbleData();

		ArrayList<BubbleEntry> entries = new ArrayList<BubbleEntry>();
		int val = 0;
		for (int index = 0; index < itemcount; index++) {
			// float rnd = getRandom(20, 30);
			// entries.add(new BubbleEntry(index, rnd, rnd));
			val = index;
			if (val == 0) {
				entries.add(new BubbleEntry(val, 12.0f, 10.0f));
			} else if (val == 1) {
				entries.add(new BubbleEntry(val, 15.5f, 10.0f));
			} else if (val == 2) {
				entries.add(new BubbleEntry(val, 30.0f, 10.0f));
			} else if (val == 3) {
				entries.add(new BubbleEntry(val, 30.0f, 10.0f));
			} else if (val == 4) {
				entries.add(new BubbleEntry(val, 20.5f, 10.0f));
			} else if (val == 5) {
				entries.add(new BubbleEntry(val, 10.0f, 10.0f));
			} else if (val == 6) {
				entries.add(new BubbleEntry(val, 15.0f, 10.0f));
			} else if (val == 7) {
				entries.add(new BubbleEntry(val, 10.0f, 10.0f));
			} else if (val == 8) {
				entries.add(new BubbleEntry(val, 25.5f, 10.0f));
			} else if (val == 9) {
				entries.add(new BubbleEntry(val, 17.0f, 10.0f));
			} else if (val == 10) {
				entries.add(new BubbleEntry(val, 20.7f, 10.0f));
			} else if (val == 11) {
				entries.add(new BubbleEntry(val, 23.0f, 10.0f));
			} else {
			}
		}

		BubbleDataSet set = new BubbleDataSet(entries, "Bubble DataSet");
		set.setColors(ColorTemplate.VORDIPLOM_COLORS);
		set.setValueTextSize(10f);
		set.setValueTextColor(Color.WHITE);
		set.setHighlightCircleWidth(1.5f);
		set.setDrawValues(true);
		bd.addDataSet(set);

		return bd;
	}

	// Scatter Chart
	protected ScatterData Scatter() {

		ScatterData d = new ScatterData();

		ArrayList<Entry> entries = new ArrayList<Entry>();

		for (int index = 0; index < itemcount; index++)
			entries.add(new Entry(getRandom(20, 15), index));

		ScatterDataSet set = new ScatterDataSet(entries, "Scatter DataSet");
		set.setColor(Color.GREEN);
		set.setScatterShapeSize(7.5f);
		set.setDrawValues(false);
		set.setValueTextSize(10f);
		d.addDataSet(set);

		return d;
	}

	// Candle Chart
	protected CandleData generateCandleData() {

		CandleData d = new CandleData();

		ArrayList<CandleEntry> entries = new ArrayList<CandleEntry>();

		for (int index = 0; index < itemcount; index++)
			entries.add(new CandleEntry(index, 20f, 10f, 13f, 17f));

		CandleDataSet set = new CandleDataSet(entries, "Candle DataSet");
		set.setColor(Color.rgb(80, 80, 80));
		set.setBodySpace(0.3f);
		set.setValueTextSize(10f);
		set.setDrawValues(false);
		d.addDataSet(set);

		return d;
	}

	private float getRandom(float range, float startsfrom) {
		return (float) (Math.random() * range) + startsfrom;
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.combined, menu); return true; }
	 */

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case R.id.actionToggleLineValues: { for (DataSet<?>
	 * set : mChart.getData().getDataSets()) { if (set instanceof LineDataSet)
	 * set.setDrawValues(!set.isDrawValuesEnabled()); }
	 * 
	 * mChart.invalidate(); break; } case R.id.actionToggleBarValues: { for
	 * (DataSet<?> set : mChart.getData().getDataSets()) { if (set instanceof
	 * BarDataSet) set.setDrawValues(!set.isDrawValuesEnabled()); }
	 * 
	 * mChart.invalidate(); break; } case R.id.actionMonth: { itemcount = 12;
	 * data = new CombinedData(mMonths); data.setData(generateLineData());
	 * data.setData(generateBarData()); // data.setData(generateBubbleData());
	 * mChart.setData(data); mChart.invalidate(); break; }
	 * 
	 * case R.id.actionYear: { itemcount = 10; data = new CombinedData(mYears);
	 * data.setData(generateLineData()); data.setData(generateBarData());
	 * data.setData(generateBubbleData()); mChart.setData(data);
	 * mChart.invalidate(); break; }
	 * 
	 * } return true; }
	 */

	private ArrayList<BarDataSet> getDataSet() {
		ArrayList<BarDataSet> dataSets = null;

		ArrayList<BarEntry> valueSet1 = new ArrayList<>();
		BarEntry v1e1 = new BarEntry(110.000f, 0); // Jan
		valueSet1.add(v1e1);
		BarEntry v1e2 = new BarEntry(40.000f, 1); // Feb
		valueSet1.add(v1e2);
		BarEntry v1e3 = new BarEntry(60.000f, 2); // Mar
		valueSet1.add(v1e3);
		BarEntry v1e4 = new BarEntry(30.000f, 3); // Apr
		valueSet1.add(v1e4);
		BarEntry v1e5 = new BarEntry(90.000f, 4); // May
		valueSet1.add(v1e5);
		BarEntry v1e6 = new BarEntry(100.000f, 5); // Jun
		valueSet1.add(v1e6);

		ArrayList<BarEntry> valueSet2 = new ArrayList<>();
		BarEntry v2e1 = new BarEntry(150.000f, 0); // Jan
		valueSet2.add(v2e1);
		BarEntry v2e2 = new BarEntry(90.000f, 1); // Feb
		valueSet2.add(v2e2);
		BarEntry v2e3 = new BarEntry(120.000f, 2); // Mar
		valueSet2.add(v2e3);
		BarEntry v2e4 = new BarEntry(60.000f, 3); // Apr
		valueSet2.add(v2e4);
		BarEntry v2e5 = new BarEntry(20.000f, 4); // May
		valueSet2.add(v2e5);
		BarEntry v2e6 = new BarEntry(80.000f, 5); // Jun
		valueSet2.add(v2e6);

		BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Income");
		barDataSet1.setColor(Color.rgb(0, 155, 0));
		BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Expense");
		barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

		dataSets = new ArrayList<>();
		dataSets.add(barDataSet1);
		dataSets.add(barDataSet2);
		return dataSets;
	}

	private ArrayList<String> getXAxisValues() {
		ArrayList<String> xAxis = new ArrayList<>();
		xAxis.add("JAN");
		xAxis.add("FEB");
		xAxis.add("MAR");
		xAxis.add("APR");
		xAxis.add("MAY");
		xAxis.add("JUN");
		return xAxis;
	}

	@Override
	public void onListItemClick(String item) {
		menu.toggle();
	}
    public void showProgressView(){
    	spinnerLayout = new LinearLayout(DashboardActivity.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(mLinearLayout, false);
		progressBar = new ProgressBar(DashboardActivity.this);
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
    public void dismissProgressView(){
    	progressBar.setVisibility(View.GONE);
		spinnerLayout.setVisibility(View.GONE);
		enableViews(mLinearLayout, true);
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}
	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tax = df.format(d);
		return tax;
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(DashboardActivity.this, LandingActivity.class);
		startActivity(i);
		DashboardActivity.this.finish();
	}
}
