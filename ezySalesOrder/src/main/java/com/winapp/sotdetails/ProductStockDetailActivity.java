package com.winapp.sotdetails;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.LineDataSet;
import com.theappguruz.imagezoom.ImageViewTouch;
import com.winapp.adapter.ProductAnalysisAdapter;
import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FWMSValidateURL;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.DateTime;
import com.winapp.helper.XMLParser;
import com.winapp.model.Product;
import com.winapp.printcube.utils.Utils;
import com.winapp.printer.UIHelper;
import com.winapp.sot.GraHeader;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.util.ToolTip;
import com.winapp.util.Validate;
import com.winapp.util.XMLAccessTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import static com.winapp.SFA.R.id.back;
import static com.winapp.SFA.R.id.endDate;
import static com.winapp.SFA.R.id.fieldsLayout;

/**
 * Created by user on 20-Jul-17.
 */

public class ProductStockDetailActivity extends Activity {
    private ImageView sBack;
    private ArrayList<String> sGroupByArr;
    private RecyclerView sPurchaseRecyclerView,sSalesRecyclerView;
    private UIHelper helper;
    private LinearLayout sDetailLayout,sProductDetailLayout,sProductAnalysisLayout,sDataLayout,sChartLayout,sSearchLayout,sDateSearchLayout;
    private HashMap<String,String> sParams;
    private ProductAnalysisAdapter sProductAnalysisAdapter;
    private String sCompanyCode = "",sProductCode="",sFromDate="",sToDate="",sUrl="",sProductName="",sServerDate="";
    private ArrayList<Product> sSalesArrList,sPurchaseArrList;

    private TextView sSalesQtyTxtV,sSalesTotalTxtV,sSalesCostTxtV,sProductNameTxtV,/*sProductCodeTxtV,*/
            sPurchaseQtyTxtV,sPurchaseTotalTxtV,sPurchaseCostTxtV,sProfitTxtV, sUnitCostTxtV,text_unitcost,
    sAverageCostTxtV ,sCartonPriceTxtV ,sWholesalePriceTxtV,sTitle,
    sRetailPriceTxtV , sPcsPerCartonTxtV ,sCategoryTxtV , sSubCategoryTxtV ,sData,sChart,sGroupBy,
    sSupplierTxtV ,
    sUomTxtV, sMarginCartonPriceTxtV ,
    sMarginWholesalePriceTxtV,
    sMarginRetailPriceTxtV;
    private Button mDetailBtn,mAnalysisBtn,mSearchBtn;
    private RadioButton sRadioBtnUnitCost,sRadioBtnAvgCost;
    private ImageButton sSearchBtn;
    private BarChart sPurchaseSaleDayChart;
    private ToolTip sToolTip;
    private EditText mFromDate,mToDate;
    private Calendar startCalendar,endCalendar;
    EditText product_name, product_code;
    ListView stockList;
    String prod_Code = "", value = "", valid_url, result,ProdCode="", ProdName="";
    ProgressBar progressBar;
    LinearLayout stock_layout;
    LinearLayout spinnerLayout;
    private static String NAMESPACE = "http://tempuri.org/";
    private static String SOAP_ACTION = "http://tempuri.org/";
    ArrayList<SOTdetailsGetSet> stockArr = new ArrayList<SOTdetailsGetSet>();
    private LinearLayout mProductStockLayout;
    public CustomerListCustomAdapter Adapter;
    private Button mViewOtherLocation;
    ImageButton back;
    private Button bt_DialogAction1, bt_DialogAction2, bt_DialogAction3,
            bt_DialogAction4;
    private int divierId;
    private View divider;
    private ImageView img_progress;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int SELECT_PICTURE = 2;
    private static final int VIEW_IMAGE = 3;
    private static final int CROP_PIC = 4;
    private Uri picUri;
    private ArrayList<String> submenuArra;
    String jsonString=null;
    private Button viewimage;
    TextView sLastUnitCost,sFLastUnitCost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_product_detail);
        mSearchBtn = (Button) findViewById(R.id.searchBtn);
        sSearchBtn = (ImageButton) findViewById(R.id.search);
        sDataLayout  = (LinearLayout) findViewById(R.id.dataLayout);
        sChartLayout  = (LinearLayout) findViewById(R.id.chartLayout);
        sProductDetailLayout  = (LinearLayout) findViewById(R.id.productDetailLayout);

        sGroupBy = (TextView) findViewById(R.id.groupBy);
        mFromDate = (EditText) findViewById(R.id.starteditTextDate);
        mToDate = (EditText) findViewById(R.id.endeditTextDate);

        sDateSearchLayout  = (LinearLayout) findViewById(R.id.datelayout);
        sSearchLayout  = (LinearLayout) findViewById(R.id.search_btn_layout);

        sProductDetailLayout  = (LinearLayout) findViewById(R.id.productDetailLayout);
        sProductAnalysisLayout  = (LinearLayout) findViewById(R.id.productAnalysisLayout);
        sDetailLayout = (LinearLayout) findViewById(R.id.sDetailLayout);
        sPurchaseRecyclerView = (RecyclerView) findViewById(R.id.purchaseRecyclerView);
        sSalesRecyclerView = (RecyclerView) findViewById(R.id.salesRecyclerView);
        sBack = (ImageView) findViewById(R.id.back);
        sSalesQtyTxtV = (TextView) findViewById(R.id.salesQty);
        sSalesTotalTxtV = (TextView) findViewById(R.id.salesTotal);
        sSalesCostTxtV = (TextView) findViewById(R.id.salesCost);
        sPurchaseQtyTxtV = (TextView) findViewById(R.id.purchaseQty);
        sPurchaseTotalTxtV = (TextView) findViewById(R.id.purchaseTotal);
        sPurchaseCostTxtV = (TextView) findViewById(R.id.purchaseCost);
        sProfitTxtV = (TextView) findViewById(R.id.profit);
        sProductNameTxtV = (TextView) findViewById(R.id.prodName);
       // sProductCodeTxtV = (TextView) findViewById(R.id.prodCode);
        sTitle = (TextView) findViewById(R.id.page_Title);

        mDetailBtn = (Button) findViewById(R.id.detailBtnTab);
        mAnalysisBtn = (Button) findViewById(R.id.analysisBtnTab);

        mAnalysisBtn = (Button) findViewById(R.id.analysisBtnTab);

        sRadioBtnUnitCost = (RadioButton) findViewById(R.id.radioBtnUnitCost);
        sRadioBtnAvgCost = (RadioButton) findViewById(R.id.radioBtnAvgCost);


        sMarginCartonPriceTxtV = (TextView) findViewById(R.id.marginCartonPrice);
        sMarginWholesalePriceTxtV = (TextView) findViewById(R.id.marginWholesalePrice);
        sMarginRetailPriceTxtV = (TextView) findViewById(R.id.marginRetailPrice);
        sFLastUnitCost=(TextView)findViewById(R.id.fprice);
        sLastUnitCost=(TextView)findViewById(R.id.fPrice_Txt);

        sPurchaseSaleDayChart = (BarChart) findViewById(R.id.purchase_sales_chart);

        mProductStockLayout=(LinearLayout) findViewById(R.id.stock_layout);
        submenuArra=new ArrayList<>();
        mViewOtherLocation=(Button)findViewById(R.id.locationBtnTab);
      //  sPurchaseSalesMonthChart = (BarChart) findViewById(R.id.purchase_sales_MonthChart);
      //  sPurchaseSalesYearChart = (BarChart) findViewById(R.id.purchase_sales_YearChart);


        sData  = (TextView) findViewById(R.id.data);
        sChart  = (TextView) findViewById(R.id.chart);
        sUnitCostTxtV = (TextView) findViewById(R.id.unitCost);
        text_unitcost = (TextView) findViewById(R.id.text_unitcost);
        sAverageCostTxtV = (TextView) findViewById(R.id.averageCost);
        sCartonPriceTxtV = (TextView) findViewById(R.id.cartonPrice);
        sWholesalePriceTxtV = (TextView) findViewById(R.id.WholesalePrice);
        sRetailPriceTxtV = (TextView) findViewById(R.id.retailPrice);
        sPcsPerCartonTxtV = (TextView) findViewById(R.id.pcspercarton);
        sCategoryTxtV = (TextView) findViewById(R.id.category);
        sSubCategoryTxtV = (TextView) findViewById(R.id.subcategory);
        sSupplierTxtV = (TextView) findViewById(R.id.supplier);
        sUomTxtV = (TextView) findViewById(R.id.uom);
        viewimage = (Button) findViewById(R.id.viewimage);

        stock_layout = (LinearLayout) findViewById(R.id.stock_layout);

        product_code = (EditText) findViewById(R.id.prod_code);
        product_name = (EditText) findViewById(R.id.prod_name);
        back = (ImageButton) findViewById(R.id.back);
        stockList = (ListView) findViewById(R.id.stock_list);

        sGroupByArr = new ArrayList<>();

        helper = new UIHelper(ProductStockDetailActivity.this);
        sParams = new HashMap<>();
        sSalesArrList = new ArrayList<>();
        sPurchaseArrList = new ArrayList<>();

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        valid_url = FWMSSettingsDatabase.getUrl();

        sSalesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sPurchaseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sCompanyCode = SupplierSetterGetter.getCompanyCode();
        FWMSSettingsDatabase.init(ProductStockDetailActivity.this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sProductCode = extras.getString("valProductCode");
            sProductName = extras.getString("ProductName");

        }
        sUrl = FWMSSettingsDatabase.getUrl();
        new SalesOrderWebService(sUrl);

        sServerDate = SalesOrderSetGet.getServerDate();
        Log.d("serverDate","-->"+sServerDate);
        //Show last one month date
        mFromDate.setText(DateTime.date(sServerDate));
        mToDate.setText(sServerDate);

      /*  String showunitcost = SalesOrderSetGet.getShowUnitCostStockTake();
        Log.d("showunitcost",showunitcost);
        if(showunitcost.equalsIgnoreCase("0")){
            text_unitcost.setVisibility(View.GONE);
            sUnitCostTxtV.setVisibility(View.GONE);
        }else if(showunitcost.equalsIgnoreCase("1")){
            text_unitcost.setVisibility(View.VISIBLE);
            sUnitCostTxtV.setVisibility(View.VISIBLE);
        }else{
            text_unitcost.setVisibility(View.GONE);
            sUnitCostTxtV.setVisibility(View.GONE);
        }*/

        String showunitAndshowCost =SalesOrderSetGet.getIsShowCost();
//        String showunitAndshowCost ="False";
        Log.d("showunitAndshowCost","-->"+showunitAndshowCost);

        if(showunitAndshowCost.matches("True")){
            text_unitcost.setVisibility(View.VISIBLE);
            sUnitCostTxtV.setVisibility(View.VISIBLE);
            sLastUnitCost.setVisibility(View.VISIBLE);
            sFLastUnitCost.setVisibility(View.VISIBLE);
        }   else if(showunitAndshowCost.matches("False")){
            text_unitcost.setVisibility(View.GONE);
            sUnitCostTxtV.setVisibility(View.GONE);
            sLastUnitCost.setVisibility(View.GONE);
            sFLastUnitCost.setVisibility(View.GONE);
        } else {
            text_unitcost.setVisibility(View.GONE);
            sUnitCostTxtV.setVisibility(View.GONE);
            sLastUnitCost.setVisibility(View.GONE);
            sFLastUnitCost.setVisibility(View.GONE);
        }

        sParams.put("CompanyCode",sCompanyCode);
        sParams.put("ProductCode",sProductCode);
        new GetProduct().execute(sParams);

      //  sParams.put("FromDate",mFromDate.getText().toString());
      //  sParams.put("ToDate",mToDate.getText().toString());
      //  sParams.put("sGroupBy","d");


        // Purchase Sales Day Bar Chart
        sPurchaseSaleDayChart.setDescription("");
        sPurchaseSaleDayChart.setGridBackgroundColor(Color.parseColor("#f9f9f9"));
//		sPurchaseSaleDayChart.setBackgroundColor(Color.WHITE);
        sPurchaseSaleDayChart.setDrawGridBackground(false); // chart background
        sPurchaseSaleDayChart.setDrawBarShadow(false); // show bar

        // Purchase Sales Month Bar Chart
   //     sPurchaseSalesMonthChart.setDescription("");
        //Chart Grid Background Color
 //       sPurchaseSalesMonthChart.setGridBackgroundColor(Color.parseColor("#f9f9f9"));
//		sPurchaseSalesMonthChart.setBackgroundColor(Color.WHITE);
  //      sPurchaseSalesMonthChart.setDrawGridBackground(false); // chart background
 //       sPurchaseSalesMonthChart.setDrawBarShadow(false); // show bar

        // Purchase Sales Year Bar Chart
  //      sPurchaseSalesYearChart.setDescription("");
        //Chart Grid Background Color
 //       sPurchaseSalesYearChart.setGridBackgroundColor(Color.parseColor("#f9f9f9"));
//		sPurchaseSalesYearChart.setBackgroundColor(Color.WHITE);
 //       sPurchaseSalesYearChart.setDrawGridBackground(false); // chart background
 //       sPurchaseSalesYearChart.setDrawBarShadow(false); // show bar

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        sToolTip = new ToolTip(this, R.layout.custom_tool_tip_view);
        sPurchaseSaleDayChart.setMarkerView(sToolTip);
       // sPurchaseSalesMonthChart.setMarkerView(sToolTip);
      //  sPurchaseSalesYearChart.setMarkerView(sToolTip);


        //Purchase Sales Day Chart
        YAxis rightAxisPurchaseSalesDay = sPurchaseSaleDayChart.getAxisRight();
        rightAxisPurchaseSalesDay.setDrawGridLines(true);
        sPurchaseSaleDayChart.getAxisRight().setEnabled(false);//disable right axis
        rightAxisPurchaseSalesDay.setGridColor(Color.parseColor("#f0f0f0"));
        YAxis leftAxisIncomeExpenseDay = sPurchaseSaleDayChart.getAxisLeft();
        leftAxisIncomeExpenseDay.setGridColor(Color.parseColor("#f0f0f0"));
        XAxis xAxisPurchaseSalesDay = sPurchaseSaleDayChart.getXAxis();
        xAxisPurchaseSalesDay.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisPurchaseSalesDay.setGridColor(Color.parseColor("#f0f0f0"));

        // Purchase Sales Month Chart
//        YAxis rightAxisPurchaseSalesMonth = sPurchaseSalesMonthChart.getAxisRight();
//        rightAxisPurchaseSalesMonth.setDrawGridLines(true);
//        sPurchaseSalesMonthChart.getAxisRight().setEnabled(false);// disable right axis
//        YAxis leftAxisPurchaseSalesMonth = sPurchaseSalesMonthChart.getAxisLeft();
//        leftAxisPurchaseSalesMonth.setDrawGridLines(true);// horizontal line in chart
//        leftAxisPurchaseSalesMonth.setGridColor(Color.parseColor("#f0f0f0"));
//        XAxis xAxisPurchaseSalesMonth = sPurchaseSalesMonthChart.getXAxis();
//        xAxisPurchaseSalesMonth.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxisPurchaseSalesMonth.setGridColor(Color.parseColor("#f0f0f0"));


        // Purchase Sales Month Chart
//        YAxis rightAxisPurchaseSalesYear = sPurchaseSalesYearChart.getAxisRight();
//        rightAxisPurchaseSalesMonth.setDrawGridLines(true);
//        sPurchaseSalesYearChart.getAxisRight().setEnabled(false);// disable right axis
//        YAxis leftAxisPurchaseSalesYear = sPurchaseSalesYearChart.getAxisLeft();
//        leftAxisPurchaseSalesMonth.setDrawGridLines(true);// horizontal line in chart
//        leftAxisPurchaseSalesMonth.setGridColor(Color.parseColor("#f0f0f0"));
//        XAxis xAxisPurchaseSalesYear = sPurchaseSalesYearChart.getXAxis();
//        xAxisPurchaseSalesMonth.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxisPurchaseSalesMonth.setGridColor(Color.parseColor("#f0f0f0"));

        String productCode = "<font color='#939393'>"+" ("+sProductCode+")"+"</font>";
        sProductNameTxtV.setText(Html.fromHtml(sProductName + productCode));

       // sProductCodeTxtV.setText(" ("+sProductCode+")");
        // new GetProductAnalysisByProductCode().execute(sParams);
        sTitle.setText(sProductName);
        mDetailBtn.setBackgroundResource(R.drawable.select_tab);
        mAnalysisBtn.setBackgroundResource(R.drawable.unselect_tab);
        mViewOtherLocation.setBackgroundResource(R.drawable.unselect_tab);
        mAnalysisBtn.setTextColor(Color.parseColor("#ffffff"));
        mViewOtherLocation.setTextColor(Color.parseColor("#ffffff"));
        mDetailBtn.setTextColor(Color.parseColor("#333b50"));
        sProductDetailLayout.setVisibility(View.VISIBLE);
        sProductAnalysisLayout.setVisibility(View.GONE);
        mProductStockLayout.setVisibility(View.GONE);
        sData.setBackgroundResource(R.drawable.small_btn_select);
        sData.setTextColor(Color.parseColor("#ffffff"));

        viewimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sParams.put("CompanyCode", sCompanyCode);
                sParams.put("PageNo", "");
                sParams.put("ProductCode", sProductCode);
                sParams.put("CategoryCode", "");
                sParams.put("SubCategoryCode", "");
                sParams.put("CustomerGroupCode", "");
                sParams.put("CustomerCode", "");
                sParams.put("TranType", "");
                sParams.put("ProductName", "");
                Log.d("CompanyProductCode",""+sCompanyCode  +sProductCode);
                new XMLAccessTask(ProductStockDetailActivity.this, valid_url, "fncGetProductMainImage",
                        sParams, false, new GetProductStockImage()).execute();
            }
        });



        sChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.purchaseLbl).setVisibility(View.INVISIBLE);
                findViewById(R.id.salesLbl).setVisibility(View.INVISIBLE);
                sDataLayout.setVisibility(View.GONE);
                sChartLayout.setVisibility(View.VISIBLE);
                sData.setBackgroundResource(R.drawable.small_btn_unselect);
                sData.setTextColor(Color.parseColor("#000000"));

                sChart.setBackgroundResource(R.drawable.small_btn_select);
                sChart.setTextColor(Color.parseColor("#ffffff"));

            }
        });
        sData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sDataLayout.setVisibility(View.VISIBLE);
                sChartLayout.setVisibility(View.GONE);
                findViewById(R.id.purchaseLbl).setVisibility(View.VISIBLE);
                findViewById(R.id.salesLbl).setVisibility(View.VISIBLE);
                sChart.setBackgroundResource(R.drawable.small_btn_unselect);
                sChart.setTextColor(Color.parseColor("#000000"));

                sData.setBackgroundResource(R.drawable.small_btn_select);
                sData.setTextColor(Color.parseColor("#ffffff"));
            }
        });
        sSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sDateSearchLayout.getVisibility()==View.GONE){
                    sDateSearchLayout.setVisibility(View.VISIBLE);
                }else{
                    sDateSearchLayout.setVisibility(View.GONE);
                }


            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupBy = sGroupBy.getText().toString();
               sParams.put("CompanyCode",sCompanyCode);
                sParams.put("ProductCode",sProductCode);
                sParams.put("FromDate",mFromDate.getText().toString());
                sParams.put("ToDate",mToDate.getText().toString());
                if(groupBy.equalsIgnoreCase(getResources().getString(R.string.date))){
                    sParams.put("sGroupBy","d");
                }else if(groupBy.equalsIgnoreCase(getResources().getString(R.string.month))){
                    sParams.put("sGroupBy","m");
                }else if(groupBy.equalsIgnoreCase(getResources().getString(R.string.year))){
                    sParams.put("sGroupBy","y");
                }

                new GetProductAnalysisByProductCode(groupBy).execute(sParams);

            }
        });
        sGroupBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
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

        mFromDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    new DatePickerDialog(ProductStockDetailActivity.this, startDate,
                            startCalendar.get(Calendar.YEAR), startCalendar
                            .get(Calendar.MONTH), startCalendar
                            .get(Calendar.DAY_OF_MONTH)).show();
                return false;
            }
        });

        mToDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    new DatePickerDialog(ProductStockDetailActivity.this, endDate, endCalendar
                            .get(Calendar.YEAR), endCalendar
                            .get(Calendar.MONTH), endCalendar
                            .get(Calendar.DAY_OF_MONTH)).show();
                return false;
            }
        });
        sBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductStockDetailActivity.this,ProductStockActivity.class);
                startActivity(i);
                finish();
            }
        });
        sRadioBtnUnitCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sRadioBtnUnitCost.setChecked(true);
                sRadioBtnAvgCost.setChecked(false);
                String unitCost = sUnitCostTxtV.getText().toString();
                marginCalc(unitCost);
            }
        });
        sRadioBtnAvgCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sRadioBtnUnitCost.setChecked(false);
                sRadioBtnAvgCost.setChecked(true);
                String avgCost = sAverageCostTxtV.getText().toString();
                marginCalc(avgCost);
            }
        });
        mDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sSearchLayout.setVisibility(View.GONE);
                mDetailBtn.setBackgroundResource(R.drawable.select_tab);
                mAnalysisBtn.setBackgroundResource(R.drawable.unselect_tab);
                mViewOtherLocation.setBackgroundResource(R.drawable.unselect_tab);
                mAnalysisBtn.setTextColor(Color.parseColor("#ffffff"));
                mViewOtherLocation.setTextColor(Color.parseColor("#ffffff"));
                mDetailBtn.setTextColor(Color.parseColor("#333b50"));

                sProductDetailLayout.setVisibility(View.VISIBLE);
                sProductAnalysisLayout.setVisibility(View.GONE);
                mProductStockLayout.setVisibility(View.GONE);

            }
        });
        mAnalysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupBy = sGroupBy.getText().toString();
                sSearchLayout.setVisibility(View.VISIBLE);
                mDetailBtn.setBackgroundResource(R.drawable.unselect_tab);
                mAnalysisBtn.setBackgroundResource(R.drawable.select_tab);
                mViewOtherLocation.setBackgroundResource(R.drawable.unselect_tab);
                mAnalysisBtn.setTextColor(Color.parseColor("#333b50"));
                mDetailBtn.setTextColor(Color.parseColor("#ffffff"));
                mViewOtherLocation.setTextColor(Color.parseColor("#ffffff"));
                mProductStockLayout.setVisibility(View.GONE);
                sProductDetailLayout.setVisibility(View.GONE);
                sProductAnalysisLayout.setVisibility(View.VISIBLE);
                sParams.put("CompanyCode",sCompanyCode);
                sParams.put("ProductCode",sProductCode);
                sParams.put("FromDate",mFromDate.getText().toString());
                sParams.put("ToDate",mToDate.getText().toString());
                if(groupBy.equalsIgnoreCase(getResources().getString(R.string.date))){
                    sParams.put("sGroupBy","d");
                }else if(groupBy.equalsIgnoreCase(getResources().getString(R.string.month))){
                    sParams.put("sGroupBy","m");
                }else if(groupBy.equalsIgnoreCase(getResources().getString(R.string.year))){
                    sParams.put("sGroupBy","y");
                }


              new GetProductAnalysisByProductCode(groupBy).execute(sParams);
            }
        });


        mViewOtherLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sSearchLayout.setVisibility(View.GONE);
                sProductDetailLayout.setVisibility(View.GONE);
                sProductAnalysisLayout.setVisibility(View.GONE);
                mProductStockLayout.setVisibility(View.VISIBLE);
                mViewOtherLocation.setBackgroundResource(R.drawable.select_tab);
                mDetailBtn.setBackgroundResource(R.drawable.unselect_tab);
                mAnalysisBtn.setBackgroundResource(R.drawable.unselect_tab);

                mViewOtherLocation.setTextColor(Color.parseColor("#333b50"));
                mDetailBtn.setTextColor(Color.parseColor("#ffffff"));
                mAnalysisBtn.setTextColor(Color.parseColor("#ffffff"));

                ProductStockAsyncCall task = new ProductStockAsyncCall();
                task.execute();

            }
        });
    }
    private void strtDate() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mFromDate.setText(sdf.format(startCalendar.getTime()));
    }

    private void edDate() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mToDate.setText(sdf.format(endCalendar.getTime()));
    }
    private void showPopup(View v){
        PopupMenu popup = new PopupMenu(this,v);
        popup.getMenuInflater().inflate(R.menu.goup_by,popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.date:
                      sGroupBy.setText(getResources().getString(R.string.date));
                        return true;
                    case R.id.month:
                        sGroupBy.setText(getResources().getString(R.string.month));
                        return true;
                    case R.id.year:
                        sGroupBy.setText(getResources().getString(R.string.year));
                        return true;
                    default:
                        return false;
                }

            }
        });
        popup.show();
    }
    private void marginCalc(String cost) {
        try {
        double dCost = 0.00,dCartonPrice=0.00,dWholesalePrice=0.00,dRetailPrice=0.00,dMarginCartonPrice=0.00,dMarginWholesalePrice=0.00,dMarginRetailPrice=0.00;
        String cartonPrice = sCartonPriceTxtV.getText().toString();
        String wholesalePrice = sWholesalePriceTxtV.getText().toString();
        String retailPrice = sRetailPriceTxtV.getText().toString();
            if(cost!=null && !cost.isEmpty()){
               dCost = Double.valueOf(cost);
            }
            if(cartonPrice!=null && !cartonPrice.isEmpty()){
                dCartonPrice = Double.valueOf(cartonPrice);
            }
            if(wholesalePrice!=null && !wholesalePrice.isEmpty()){
                dWholesalePrice = Double.valueOf(wholesalePrice);
            }
            if(retailPrice!=null && !retailPrice.isEmpty()){
                dRetailPrice = Double.valueOf(retailPrice);
            }

            dMarginCartonPrice = ((dCartonPrice - dCost)/dCost)*100;
            dMarginWholesalePrice = ((dWholesalePrice - dCost)/dCost)*100;
            dMarginRetailPrice = ((dRetailPrice - dCost)/dCost)*100;
            Log.d("dMarginCartonPrice","-->"+dMarginCartonPrice);
            Log.d("dMarginWholesalePrice","-->"+dMarginWholesalePrice);
            Log.d("dMarginRetailPrice","-->"+dMarginRetailPrice);
            if(dMarginCartonPrice>0){
                sMarginCartonPriceTxtV.setText(Validate.twoDecimalPoint(dMarginCartonPrice).split("\\.")[0]);
            }else{
                sMarginCartonPriceTxtV.setText("0");
            }
            if(dMarginWholesalePrice>0){
                sMarginWholesalePriceTxtV.setText(Validate.twoDecimalPoint(dMarginWholesalePrice).split("\\.")[0]);
            }else{
                sMarginWholesalePriceTxtV.setText("0");
            }
            if(dMarginRetailPrice>0){
                sMarginRetailPriceTxtV.setText(Validate.twoDecimalPoint(dMarginRetailPrice).split("\\.")[0]);
            }else{
                sMarginRetailPriceTxtV.setText("0");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }




   /* public void getDate(
                        ArrayList<Float> collectionWeekly,ArrayList<Float> paymentExpenseWeekly) {

        datesArr.clear();
        ArrayList<BarDataSet> purchaseSalesDataSets = new ArrayList<BarDataSet>();
        ArrayList<BarEntry> purchaseValueSet = new ArrayList<>();
        ArrayList<BarEntry> salesValueSet = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        try {

           String fromDate = mFromDate.getText().toString();
            String toDate = mToDate.getText().toString();
           if(fromDate!=null && !fromDate.isEmpty() && toDate!=null && !toDate.isEmpty()){

               Date dateFrom = sdf.parse(fromDate);
               Date dateTo= sdf.parse(toDate);

               Calendar start = Calendar.getInstance();
               start.setTime(dateFrom);
               Calendar end = Calendar.getInstance();
               end.setTime(dateTo);

               while(!start.after(end))
               {
                   int year = start.get(Calendar.YEAR);
                   int month = start.get(Calendar.MONTH) + 1;
                   int days = start.get(Calendar.DAY_OF_MONTH);

                   String dateCal ="",monthCal="";
                   if (days < 10) {
                       NumberFormat f = new DecimalFormat("00");
                       dateCal = String.valueOf(f.format(days));
                   }
                   else{
                       dateCal = String.valueOf(days);
                   }

                   if (month < 10) {
                       NumberFormat f = new DecimalFormat("00");
                       monthCal = String.valueOf(f.format(month));
                   }
                   else{
                       monthCal = String.valueOf(month);
                   }
                   datesArr.add(dateCal+"/"+monthCal+"/"+year);
                 //  Log.d("Date",""+dateCal+"/"+monthCal+"/"+year);
                  // System.out.printf("%d/%d/%d\n", dateCal, monthCal, year);



                   //System.out.printf("%d.%d.%d\n", days, month, year);
                  start.add(Calendar.DATE, 1);
               }

           }
            for(String years : yearsArr){
               Log.d("yearss",""+years);
            }
           *//* for(String months : monthsArr){
                Log.d("monthss",""+months);
            }*//*
            //Income vs Expense Day Chart
            /*//**starts**//*/
            //Income Data Values
//            float[] incomefloatList = new float[collectionWeekly.size()];

//            for(int i = 0; i < collectionWeekly.size(); ++i) {
//                incomefloatList[i] = collectionWeekly.get(i);
//                BarEntry barEntry = new BarEntry(incomefloatList[i], i);
//                incomeValueSet.add(barEntry);
//            }
//
//            //Expense Data Values
//            float[] salesfloatList = new float[paymentExpenseWeekly.size()];
//            for(int i = 0; i < paymentExpenseWeekly.size(); ++i) {
//                expensefloatList[i] = paymentExpenseWeekly.get(i);
//                BarEntry barEntry = new BarEntry(expensefloatList[i], i);
//                expenseValueSet.add(barEntry);
//            }

            float[] purchasefloatList = new float[datesArr.size()];
               for(int i = 0; i < datesArr.size(); ++i) {
                   purchasefloatList[i] = 100;
                BarEntry barEntry = new BarEntry(purchasefloatList[i], i);
                purchaseValueSet.add(barEntry);
            }
            float[] salesfloatList = new float[datesArr.size()];
               for(int i = 0; i < datesArr.size(); ++i) {
                   salesfloatList[i] = 50;
                BarEntry barEntry = new BarEntry(salesfloatList[i], i);
               salesValueSet.add(barEntry);
            }

            BarDataSet purchaseBarDataSet = new BarDataSet(purchaseValueSet, "Purchase");
            purchaseBarDataSet.setColor(Color.rgb(71,165,51));

            BarDataSet salesBarDataSet = new BarDataSet(salesValueSet, "Sales");
            salesBarDataSet.setColor(Color.rgb(228,65,65));
            purchaseSalesDataSets.add(purchaseBarDataSet);
            purchaseSalesDataSets.add(salesBarDataSet);


            BarData bardata = new BarData(datesArr, purchaseSalesDataSets);
            sPurchaseSaleDayChart.setData(bardata);
            sPurchaseSaleDayChart.setDescription("");
            sPurchaseSaleDayChart.animateXY(2000, 2000);
            sPurchaseSaleDayChart.invalidate();
            /*//**ends**//*/

            //Remove Income vs Expense Day Bar Chart values
            for (DataSet<?> set : sPurchaseSaleDayChart.getData().getDataSets()) {
                if (set instanceof BarDataSet)
                    set.setDrawValues(!set.isDrawValuesEnabled());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

*/
//            public void getYears(
//            ArrayList<Float> collectionWeekly,ArrayList<Float> paymentExpenseWeekly) {
//
//        yearsArr.clear();
//        ArrayList<BarDataSet> purchaseSalesDataSets = new ArrayList<BarDataSet>();
//        ArrayList<BarEntry> purchaseValueSet = new ArrayList<>();
//        ArrayList<BarEntry> salesValueSet = new ArrayList<>();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
//        try {
//
//            String fromDate = mFromDate.getText().toString();
//            String toDate = mToDate.getText().toString();
//            if(fromDate!=null && !fromDate.isEmpty() && toDate!=null && !toDate.isEmpty()){
//
//                Date dateFrom = sdf.parse(fromDate);
//                Date dateTo= sdf.parse(toDate);
//
//                Calendar start = Calendar.getInstance();
//                start.setTime(dateFrom);
//                Calendar end = Calendar.getInstance();
//                end.setTime(dateTo);
//
//                while(!start.after(end))
//                {
//                    int year = start.get(Calendar.YEAR);
//                    Log.d("year","-->"+year);
//                    yearsArr.add(""+year);
//                    start.add(Calendar.YEAR, 1);
//                }
//
//            }
//            for(String years : yearsArr){
//                Log.d("yearss",""+years);
//            }
//            float[] purchasefloatList = new float[yearsArr.size()];
//            for(int i = 0; i < yearsArr.size(); ++i) {
//                purchasefloatList[i] = 100;
//                BarEntry barEntry = new BarEntry(purchasefloatList[i], i);
//                purchaseValueSet.add(barEntry);
//            }
//            float[] salesfloatList = new float[yearsArr.size()];
//            for(int i = 0; i < yearsArr.size(); ++i) {
//                salesfloatList[i] = 50;
//                BarEntry barEntry = new BarEntry(salesfloatList[i], i);
//                salesValueSet.add(barEntry);
//            }
//
//            BarDataSet purchaseBarDataSet = new BarDataSet(purchaseValueSet, "Purchase");
//            purchaseBarDataSet.setColor(Color.rgb(71,165,51));
//
//            BarDataSet salesBarDataSet = new BarDataSet(salesValueSet, "Sales");
//            salesBarDataSet.setColor(Color.rgb(228,65,65));
//            purchaseSalesDataSets.add(purchaseBarDataSet);
//            purchaseSalesDataSets.add(salesBarDataSet);
//
//
//            BarData bardata = new BarData(yearsArr, purchaseSalesDataSets);
//            sPurchaseSaleDayChart.setData(bardata);
//            sPurchaseSaleDayChart.setDescription("");
//            sPurchaseSaleDayChart.animateXY(2000, 2000);
//            sPurchaseSaleDayChart.invalidate();
//            //**ends**//
//
//            //Remove Income vs Expense Day Bar Chart values
//            for (DataSet<?> set : sPurchaseSaleDayChart.getData().getDataSets()) {
//                if (set instanceof BarDataSet)
//                    set.setDrawValues(!set.isDrawValuesEnabled());
//            }
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    public void getMonths(
//            ArrayList<Float> collectionWeekly,ArrayList<Float> paymentExpenseWeekly) {
//
//        monthsArr.clear();
//        ArrayList<BarDataSet> purchaseSalesDataSets = new ArrayList<BarDataSet>();
//        ArrayList<BarEntry> purchaseValueSet = new ArrayList<>();
//        ArrayList<BarEntry> salesValueSet = new ArrayList<>();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
//        try {
//
//            String fromDate = mFromDate.getText().toString();
//            String toDate = mToDate.getText().toString();
//            if(fromDate!=null && !fromDate.isEmpty() && toDate!=null && !toDate.isEmpty()){
//
//                Date dateFrom = sdf.parse(fromDate);
//                Date dateTo= sdf.parse(toDate);
//
//                Calendar start = Calendar.getInstance();
//                start.setTime(dateFrom);
//                Calendar end = Calendar.getInstance();
//                end.setTime(dateTo);
//
//                while(!start.after(end))
//                {
//                    String monthCal="";
//                    int year = start.get(Calendar.YEAR);
//                    int month = start.get(Calendar.MONTH) + 1;
//                    if (month < 10) {
//                        NumberFormat f = new DecimalFormat("00");
//                        monthCal = String.valueOf(f.format(month));
//                    }
//                    else{
//                        monthCal = String.valueOf(month);
//                    }
//                    Log.d("monthCal","-->"+monthCal);
//
//                    monthsArr.add(monthCal+"/"+year);
//                    start.add(Calendar.MONTH, 1);
//                }
//
//            }
//            for(String months : monthsArr){
//                Log.d("months",""+months);
//            }
//            float[] purchasefloatList = new float[monthsArr.size()];
//            for(int i = 0; i < monthsArr.size(); ++i) {
//                purchasefloatList[i] = 100;
//                BarEntry barEntry = new BarEntry(purchasefloatList[i], i);
//                purchaseValueSet.add(barEntry);
//            }
//            float[] salesfloatList = new float[monthsArr.size()];
//            for(int i = 0; i < monthsArr.size(); ++i) {
//                salesfloatList[i] = 50;
//                BarEntry barEntry = new BarEntry(salesfloatList[i], i);
//                salesValueSet.add(barEntry);
//            }
//
//            BarDataSet purchaseBarDataSet = new BarDataSet(purchaseValueSet, "Purchase");
//            purchaseBarDataSet.setColor(Color.rgb(71,165,51));
//
//            BarDataSet salesBarDataSet = new BarDataSet(salesValueSet, "Sales");
//            salesBarDataSet.setColor(Color.rgb(228,65,65));
//            purchaseSalesDataSets.add(purchaseBarDataSet);
//            purchaseSalesDataSets.add(salesBarDataSet);
//
//
//            BarData bardata = new BarData(monthsArr, purchaseSalesDataSets);
//            sPurchaseSaleDayChart.setData(bardata);
//            sPurchaseSaleDayChart.setDescription("");
//            sPurchaseSaleDayChart.animateXY(2000, 2000);
//            sPurchaseSaleDayChart.invalidate();
//            //**ends**//
//
//            //Remove Income vs Expense Day Bar Chart values
//            for (DataSet<?> set : sPurchaseSaleDayChart.getData().getDataSets()) {
//                if (set instanceof BarDataSet)
//                    set.setDrawValues(!set.isDrawValuesEnabled());
//            }
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private boolean getUniqueMonth(String month){
//       for(String months : monthsArr){
//           if(!months.equals(month)){
//               return true;
//           }
//       }
//        return false;
//
//    }
//    private boolean getUniqueYear(String year){
//        for(String years : yearsArr){
//            if(!years.equals(year)){
//                return true;
//            }
//        }
//        return false;
//
//    }
    private class GetProduct extends AsyncTask<HashMap<String, String>,String,String>{
        String sResult = "",sOutLetPrice = "", sPcsPerCarton = "",sUOMCode = "",sCategoryCode="",sFlastCost="", sUnitCost = "",sSubCategoryCode="",sSupplierCode="", sAverageCost = "", sRetailPrice = "" ,sWholeSalePrice = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            helper.showProgressView(sDetailLayout);
        }
        @Override
        protected String doInBackground(HashMap... params) {
            try{
                String jsonString = SalesOrderWebService.getSODetail(params[0], "fncGetProduct");
                Log.d("jsonString", ""+ jsonString);
                JSONObject sJsonObject = new JSONObject(jsonString);
                JSONArray sJsonArray = sJsonObject.optJSONArray("SODetails");
                int lengthJsonArray = sJsonArray.length();
                if (lengthJsonArray > 0) {
                        JSONObject jsonobject = sJsonArray.getJSONObject(0);
                         sOutLetPrice = jsonobject.getString("OutletPrice");
                         sPcsPerCarton = jsonobject.getString("PcsPerCarton");
                         sUOMCode = jsonobject.getString("UOMCode");
                         sAverageCost = jsonobject.getString("AverageCost");
                         sRetailPrice = jsonobject.getString("RetailPrice");
                         sWholeSalePrice = jsonobject.getString("WholeSalePrice");
                         sCategoryCode = jsonobject.getString("CategoryCode");
                        sSubCategoryCode= jsonobject.getString("SubCategoryCode");
                        sSupplierCode= jsonobject.getString("SupplierCode");
                    sUnitCost = jsonobject.getString("LastUnitCost");
                    sFlastCost = jsonobject.getString("FLastUnitCost");
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            helper.dismissProgressView(sDetailLayout);
            if(sUnitCost!=null&& !sUnitCost.isEmpty()){
                double dUnitCost = Double.valueOf(sUnitCost);
                sUnitCost = Validate.twoDecimalPoint(dUnitCost);
                sUnitCostTxtV.setText(sUnitCost);
            }else{
                sUnitCostTxtV.setText("0.00");
            }
            if(sAverageCost!=null&& !sAverageCost.isEmpty()){
                double dAverageCost = Double.valueOf(sAverageCost);
                sAverageCost = Validate.twoDecimalPoint(dAverageCost);
                sAverageCostTxtV.setText(sAverageCost);
            }else{
                sAverageCostTxtV.setText("0.00");
            }
            if(sRetailPrice!=null&& !sRetailPrice.isEmpty()){
                double dRetailPrice = Double.valueOf(sRetailPrice);
                sRetailPrice = Validate.twoDecimalPoint(dRetailPrice);
                sCartonPriceTxtV.setText(sRetailPrice);
            }else{
                sCartonPriceTxtV.setText("0.00");
            }

            if(sOutLetPrice!=null&& !sOutLetPrice.isEmpty()){
                double dOutLetPrice = Double.valueOf(sOutLetPrice);
                sOutLetPrice = Validate.twoDecimalPoint(dOutLetPrice);
                sRetailPriceTxtV.setText(sOutLetPrice);
            }else{
                sRetailPriceTxtV.setText("0.00");
            }

            if(sWholeSalePrice!=null&& !sWholeSalePrice.isEmpty()){
                double dWholeSalePrice = Double.valueOf(sWholeSalePrice);
                sWholeSalePrice = Validate.twoDecimalPoint(dWholeSalePrice);
                sWholesalePriceTxtV.setText(sWholeSalePrice);
            }else{
                sWholesalePriceTxtV.setText("0.00");
            }
            if(sPcsPerCarton!=null&& !sPcsPerCarton.isEmpty()){
                sPcsPerCartonTxtV.setText(sPcsPerCarton);
            }else{
                sPcsPerCartonTxtV.setText("0");
            }

            if(sCategoryCode!=null&& !sCategoryCode.isEmpty()){
                sCategoryTxtV.setText(sCategoryCode);
            }else{
                sCategoryTxtV.setText("-");
            }

            if(sSubCategoryCode!=null&& !sSubCategoryCode.isEmpty()){
                sSubCategoryTxtV.setText(sSubCategoryCode);
            }else{
                sSubCategoryTxtV.setText("-");
            }
            if(sSupplierCode!=null&& !sSupplierCode.isEmpty()){
                sSupplierTxtV.setText(sSupplierCode);
            }else{
                sSupplierTxtV.setText("-");
            }
            if(sUOMCode!=null&& !sUOMCode.isEmpty()){
                sUomTxtV.setText(sUOMCode);
            }else{
                sUomTxtV.setText("-");
            }
            if(sFlastCost!=null && !sFlastCost.isEmpty()){
                double dFlastCost = Double.valueOf(sFlastCost);
                sFlastCost = Validate.twoDecimalPoint(dFlastCost);
                sFLastUnitCost.setText(sFlastCost);
            }else {
                sFLastUnitCost.setText("0.00");
            }
        }


    }

    private class GetProductAnalysisByProductCode extends AsyncTask<HashMap<String, String>,String,String>{
        String sResult = "",groupBy="";
        private double dQtySales = 0.00,dTotalSales = 0.00,dQtyPurchase =0.00,dTotalPurchase =0.00;
        private GetProductAnalysisByProductCode(String groupBy){
            this.groupBy = groupBy;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            helper.showProgressView(sDetailLayout);
            sPurchaseArrList.clear();
            sSalesArrList.clear();
            sGroupByArr.clear();
        }
        @Override
        protected String doInBackground(HashMap... params) {
            try{
                String jsonString = SalesOrderWebService.getSODetail(params[0], "fncGetProductAnalysisByProductCode");
                Log.d("jsonString", ""+ jsonString);
                JSONObject sJsonObject = new JSONObject(jsonString);
                JSONArray sJsonArray = sJsonObject.optJSONArray("SODetails");
                int lengthJsonArray = sJsonArray.length();
                if (lengthJsonArray > 0) {
                    for(int i=0;i<lengthJsonArray;i++){
                        Product salesProduct = new Product();
                        Product purchaseProduct = new Product();
                        JSONObject jsonobject = sJsonArray.getJSONObject(i);
                        String sTranDate = "";
                        if(groupBy.equalsIgnoreCase(getResources().getString(R.string.date))){
                            SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
                            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yy");
                            try {
                                sTranDate = jsonobject.getString("TranDate").split("\\ ")[0];
                                sTranDate = dateFormat2.format(dateFormat1.parse(sTranDate));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }else if(groupBy.equalsIgnoreCase(getResources().getString(R.string.month))){
                            sTranDate = jsonobject.getString("TranDate");
                        }else if(groupBy.equalsIgnoreCase(getResources().getString(R.string.year))){
                            sTranDate = jsonobject.getString("TranDate");
                        }
                        sGroupByArr.add(sTranDate);
                        String sSalesQty = jsonobject.getString("SalesQty").split("\\.")[0];
                        String sSalesSubTotal = jsonobject.getString("SalesSubTotal");
                        String sPurchaseQty = jsonobject.getString("PurchaseQty").split("\\.")[0];
                        String sPurchaseSubTotal = jsonobject.getString("PurchaseSubTotal");


                        //Sales
                        double dSalesQty =  sSalesQty.equals("") ? 0: Double.valueOf(sSalesQty);
                        dQtySales += dSalesQty;
                        double dSalesSubTotal =  sSalesSubTotal.equals("") ? 0: Double.valueOf(sSalesSubTotal);
                        dTotalSales += dSalesSubTotal;
                        double dSalesCost= dSalesSubTotal/dSalesQty;
                        if(dSalesCost>0){
                            String  sSalesCost =  Validate.twoDecimalPoint(dSalesCost);
                            salesProduct.setCost(sSalesCost);
                        }else{
                            salesProduct.setCost("0.00");
                        }
                        salesProduct.setTranDate(sTranDate);
                        salesProduct.setSalesQty(sSalesQty);
                        salesProduct.setSalesSubTotal(sSalesSubTotal);
                        sSalesArrList.add(salesProduct);

                        //purchase
                        double dPurchaseQty =  sPurchaseQty.equals("") ? 0: Double.valueOf(sPurchaseQty);
                        dQtyPurchase += dPurchaseQty;
                        double dPurchaseSubTotal =  sPurchaseSubTotal.equals("") ? 0: Double.valueOf(sPurchaseSubTotal);
                        dTotalPurchase +=dPurchaseSubTotal;
                        double dPurchaseCost = dPurchaseSubTotal/dPurchaseQty;
                        if(dPurchaseCost>0){
                            String sPurchaseCost =  Validate.twoDecimalPoint(dPurchaseCost);
                            purchaseProduct.setCost(sPurchaseCost);
                        }else{
                            purchaseProduct.setCost("0.00");
                        }
                        purchaseProduct.setTranDate(sTranDate);
                        purchaseProduct.setPurchaseQty(sPurchaseQty);
                        purchaseProduct.setPurchaseSubTotal(sPurchaseSubTotal);
                        sPurchaseArrList.add(purchaseProduct);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            helper.dismissProgressView(sDetailLayout);
            sProductAnalysisAdapter = new ProductAnalysisAdapter(ProductStockDetailActivity.this,sSalesArrList,"Sales");
            sSalesRecyclerView.setAdapter(sProductAnalysisAdapter);

            sProductAnalysisAdapter = new ProductAnalysisAdapter(ProductStockDetailActivity.this,sPurchaseArrList,"Purchase");
            sPurchaseRecyclerView.setAdapter(sProductAnalysisAdapter);

            sSalesQtyTxtV.setText(Validate.twoDecimalPoint(dQtySales).split("\\.")[0]);
            sSalesTotalTxtV.setText(Validate.twoDecimalPoint(dTotalSales));
            double dSalesCost = dTotalSales/dQtySales;
            if(dSalesCost>0){
                sSalesCostTxtV.setText(Validate.twoDecimalPoint(dSalesCost));
            }else {
                sSalesCostTxtV.setText("0");
            }
            sPurchaseQtyTxtV.setText(Validate.twoDecimalPoint(dQtyPurchase).split("\\.")[0]);
            sPurchaseTotalTxtV.setText(Validate.twoDecimalPoint(dTotalPurchase));
            double dPurchaseCost = dTotalPurchase/dQtyPurchase;
            if(dPurchaseCost>0){
                sPurchaseCostTxtV.setText(Validate.twoDecimalPoint(dPurchaseCost));
            }else {
                sPurchaseCostTxtV.setText("0");
            }
            double dProfit = dTotalSales - dTotalPurchase;
            sProfitTxtV.setText(Validate.twoDecimalPoint(dProfit));

          /*  if(groupBy.equalsIgnoreCase(getResources().getString(R.string.date))){
                getDate(null,null);
            }else if(groupBy.equalsIgnoreCase(getResources().getString(R.string.month))){
                getMonths(null,null);
            }else if(groupBy.equalsIgnoreCase(getResources().getString(R.string.year))){
                getYears(null,null);
            }*/
            showGraph();

        }


    }
    public void showGraph() {
        ArrayList<BarDataSet> purchaseSalesDataSets = new ArrayList<BarDataSet>();
        ArrayList<BarEntry> purchaseValueSet = new ArrayList<>();
        ArrayList<BarEntry> salesValueSet = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        try {


            float[] purchasefloatList = new float[sPurchaseArrList.size()];
            for(int i = 0; i < sPurchaseArrList.size(); ++i) {
                purchasefloatList[i] = Float.valueOf(sPurchaseArrList.get(i).getPurchaseSubTotal());
                BarEntry barEntry = new BarEntry(purchasefloatList[i], i);
                purchaseValueSet.add(barEntry);
            }
            float[] salesfloatList = new float[sSalesArrList.size()];
            for(int i = 0; i < sSalesArrList.size(); ++i) {
                salesfloatList[i] = Float.valueOf(sSalesArrList.get(i).getSalesSubTotal());
                BarEntry barEntry = new BarEntry(salesfloatList[i], i);
                salesValueSet.add(barEntry);
            }

            BarDataSet purchaseBarDataSet = new BarDataSet(purchaseValueSet, "Purchase");
            purchaseBarDataSet.setColor(Color.rgb(71,165,51));

            BarDataSet salesBarDataSet = new BarDataSet(salesValueSet, "Sales");
            salesBarDataSet.setColor(Color.rgb(228,65,65));
            purchaseSalesDataSets.add(purchaseBarDataSet);
            purchaseSalesDataSets.add(salesBarDataSet);


            BarData bardata = new BarData(sGroupByArr, purchaseSalesDataSets);
            sPurchaseSaleDayChart.setData(bardata);
            sPurchaseSaleDayChart.setDescription("");
            sPurchaseSaleDayChart.animateXY(2000, 2000);
            sPurchaseSaleDayChart.invalidate();
            //**ends**//

            //Remove Income vs Expense Day Bar Chart values
            for (DataSet<?> set : sPurchaseSaleDayChart.getData().getDataSets()) {
                if (set instanceof BarDataSet)
                    set.setDrawValues(!set.isDrawValuesEnabled());
            }

        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ProductStockDetailActivity.this,
                ProductStockActivity.class);
        startActivity(i);
      finish();
    }

    private class ProductStockAsyncCall extends AsyncTask<Void, Void, Void> {
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {

            spinnerLayout = new LinearLayout(ProductStockDetailActivity.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            addContentView(spinnerLayout, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(mProductStockLayout, false);
            progressBar = new ProgressBar(ProductStockDetailActivity.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    R.drawable.greenprogress));
            spinnerLayout.addView(progressBar);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            stockArr.clear();

            SoapObject request = new SoapObject(NAMESPACE,
                    "fncGetProductWithStock");
            PropertyInfo prodCode = new PropertyInfo();
            PropertyInfo companyCode = new PropertyInfo();

            String cmpnyCode = SupplierSetterGetter.getCompanyCode();
            companyCode.setName("CompanyCode");
            companyCode.setValue(cmpnyCode);
            companyCode.setType(String.class);
            request.addProperty(companyCode);

            prodCode.setName("ProductCode");
            prodCode.setValue(sProductCode);
            prodCode.setType(String.class);
            request.addProperty(prodCode);

            try {
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.bodyOut = request;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(
                        valid_url);
                androidHttpTransport.call(SOAP_ACTION
                        + "fncGetProductWithStock", envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

                value = response.toString();
                Log.d("Stock Value", value);
                result = " { Stock : " + value + "}";

                JSONObject jsonResponse;
                try {
                    jsonResponse = new JSONObject(result);

                    JSONArray jsonMainNode = jsonResponse.optJSONArray("Stock");

                    int lengthJsonArr = jsonMainNode.length();
                    for (int i = 0; i < lengthJsonArr; i++) {

                        JSONObject jsonChildNode = jsonMainNode
                                .getJSONObject(i);

                        String ProductCode = jsonChildNode.optString(
                                "ProductCode").toString();
                        String ProductName = jsonChildNode.optString(
                                "ProductName").toString();

                        String LocationCode = jsonChildNode.optString(
                                "LocationCode").toString();
                        String NoOfCarton = jsonChildNode.optString(
                                "PcsPerCarton").toString();
                        String Qty = jsonChildNode.optString("Qty").toString();

                        int cartonqty = (int) (Double.parseDouble(Qty) / Double
                                .parseDouble(NoOfCarton));
                        int lqty = (int) (Double.parseDouble(Qty) % Double
                                .parseDouble(NoOfCarton));
                        String looseqty = twoDecimalPoint(lqty);

                        SOTdetailsGetSet stock_Set_Get = new SOTdetailsGetSet();

                        ProdCode = ProductCode;
                        ProdName = ProductName;

                        stock_Set_Get.setStock_prod_code(ProductCode);
                        stock_Set_Get.setStock_prod_name(ProductName);
                        stock_Set_Get.setStock_cqty(""+cartonqty);
                        stock_Set_Get.setStock_lqty(""+looseqty);
                        stock_Set_Get.setStock_qty(Qty);
                        stock_Set_Get.setStock_location(LocationCode);

                        stockArr.add(stock_Set_Get);
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Log.d("Stock Result", stockArr.toString());

            product_code.setText(ProdCode);
            product_name.setText(ProdName);

            Adapter = new CustomerListCustomAdapter(ProductStockDetailActivity.this,
                    stockArr);
            stockList.setAdapter(Adapter);

            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(mProductStockLayout, true);
        }
    }
    private void enableViews(View stock_layout, boolean b) {
        if (stock_layout instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) stock_layout;
            for (int i = 0; i < vg.getChildCount(); i++) {
                enableViews(vg.getChildAt(i), b);
            }
        }
        stock_layout.setEnabled(b);
    }
    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tot = df.format(d);

        return tot;
    }
    public class CustomerListCustomAdapter extends BaseAdapter {

        private ArrayList<SOTdetailsGetSet> listarray = new ArrayList<SOTdetailsGetSet>();

        LayoutInflater mInflater;
        CustomHolder holder = new CustomHolder();
        SOTdetailsGetSet user;

        public CustomerListCustomAdapter(Context context, ArrayList<SOTdetailsGetSet> productsList) {
            listarray.clear();
            this.listarray = productsList;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listarray.size();
        }

        @Override
        public SOTdetailsGetSet getItem(int position) {
            return listarray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return listarray.get(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            View row = convertView;
            holder = null;

            if (row == null) {
                row = mInflater.inflate(R.layout.stock_listitem, null);
                holder = new CustomHolder();

                holder.location = (TextView) row.findViewById(R.id.location);
                holder.CQty = (TextView) row.findViewById(R.id.CQty);
                holder.LQty = (TextView) row.findViewById(R.id.LQty);
                holder.Qty = (TextView) row.findViewById(R.id.Qty);

                row.setTag(holder);
            } else {
                holder = (CustomHolder) row.getTag();
            }

            user = getItem(position);

            Log.d("Stock Qty", user.getStock_qty());

            if (user.getStock_qty().matches(".00")
                    || user.getStock_qty().matches("0.00")
                    || user.getStock_qty().matches("0.0")) {
                holder.Qty.setText("0");
            } else {

                StringTokenizer tokens = new StringTokenizer(user.getStock_qty(),".");
                String qty = tokens.nextToken();

                holder.Qty.setText(qty);
            }

            if (user.getStock_lqty().matches(".00")
                    || user.getStock_lqty().matches("0.00")
                    || user.getStock_lqty().matches("0.0")) {
                holder.LQty.setText("0");
            } else {
                holder.LQty.setText(user.getStock_lqty());
            }

            holder.CQty.setText(user.getStock_cqty());
            holder.location.setText(user.getStock_location());

            if (position % 2 == 0) {

                row.setBackgroundResource(R.drawable.list_item_even_bg);
                holder.location.setTextColor(Color.parseColor("#035994"));
                holder.CQty.setTextColor(Color.parseColor("#035994"));
                holder.LQty.setTextColor(Color.parseColor("#035994"));
                holder.Qty.setTextColor(Color.parseColor("#035994"));

            } else {

                row.setBackgroundResource(R.drawable.list_item_odd_bg);
                holder.location.setTextColor(Color.parseColor("#646464"));
                holder.CQty.setTextColor(Color.parseColor("#646464"));
                holder.LQty.setTextColor(Color.parseColor("#646464"));
                holder.Qty.setTextColor(Color.parseColor("#646464"));
            }

            return row;
        }

        final class CustomHolder {
            TextView location;
            TextView CQty;
            TextView LQty;
            TextView Qty;
        }
    }

    private class GetProductStockImage implements XMLAccessTask.CallbackInterface {
        @Override
        public void onSuccess(NodeList nl) {
            String productstockimage = null;
            for (int i = 0; i < nl.getLength(); i++) {

                Element e = (Element) nl.item(i);
                productstockimage = XMLParser.getValue(e, "ProductImage");
                Log.d("productstockimage",""+productstockimage);
            }

            // dialogSubMenu(productstockimage);

          /*  progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);*/
            enableViews(mProductStockLayout, true);
            Log.d("onSuccess","onSuccess");
            if (productstockimage != null && !productstockimage.isEmpty()) {

                // submenuArra.add("View Image");
                zoomViews(productstockimage, VIEW_IMAGE);
            } else {
                dialogSubMenu();
            }
        }

        @Override
        public void onFailure(XMLAccessTask.ErrorType error) {
            onError(error);
            enableViews(mProductStockLayout, true);

        }
    }

    private void zoomViews(final String imagestring, final int resultCode) {

        byte[] encodeByte;
        Bitmap myBitmap;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 2;

        byte[] encodeByte1 = Base64.decode(imagestring, Base64.DEFAULT);

        Log.d("Firsttimeimg", imagestring);

        String s;
        try {
            s = new String(encodeByte1, "UTF-8");
//			Log.d("s-->", ""+s);
            encodeByte = Base64.decode(s, Base64.DEFAULT);

        } catch (Exception e) {

            encodeByte = encodeByte1;

        }

        final Dialog dialog = new Dialog(ProductStockDetailActivity.this);
        dialog.setTitle(sProductName);
        dialog.setContentView(R.layout.image_dialog);
        ImageViewTouch imageView1 = (ImageViewTouch) dialog
                .findViewById(R.id.imageView2);
        bt_DialogAction1 = (Button) dialog.findViewById(R.id.button1);
        bt_DialogAction2 = (Button) dialog.findViewById(R.id.button2);
        bt_DialogAction3 = (Button) dialog.findViewById(R.id.button3);
        bt_DialogAction4 = (Button) dialog.findViewById(R.id.button4);

        divierId = dialog.getContext().getResources()
                .getIdentifier("android:id/titleDivider", null, null);
        divider = dialog.findViewById(divierId);
        img_progress = (ImageView) dialog.findViewById(R.id.imgProgress);
        AnimationDrawable frameAnimation = (AnimationDrawable) img_progress
                .getDrawable();
        frameAnimation.setCallback(img_progress);
        frameAnimation.setVisible(true, true);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

//		Bitmap myBitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
//				encodeByte.length, options);

        if (options.outWidth > 3000 || options.outHeight > 2000) {
            options.inSampleSize = 4;
        } else if (options.outWidth > 2000 || options.outHeight > 1500) {
            options.inSampleSize = 3;
        } else if (options.outWidth > 1000 || options.outHeight > 1000) {
            options.inSampleSize = 2;
        }

        options.inJustDecodeBounds = false;
        myBitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                encodeByte.length, options);

//		myBitmap = reduceImageSize(imageView1,imagestring);

        // imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
        // imageView1.setAdjustViewBounds(true);
        imageView1.setImageBitmapReset(myBitmap, 0, true);

        // imageView1.setImageBitmap(myBitmap);
        bt_DialogAction1.setText(R.string.camera);
        bt_DialogAction2.setText(R.string.gallery);
        bt_DialogAction3.setText(R.string.save);
        bt_DialogAction4.setText(R.string.cncl);

        if (resultCode == VIEW_IMAGE) {
            bt_DialogAction1.setVisibility(View.VISIBLE);
            bt_DialogAction2.setVisibility(View.VISIBLE);
            bt_DialogAction3.setVisibility(View.GONE);
            bt_DialogAction4.setVisibility(View.VISIBLE);
        } else if (resultCode == SELECT_PICTURE) {

            bt_DialogAction1.setVisibility(View.GONE);
            bt_DialogAction2.setVisibility(View.GONE);
            bt_DialogAction3.setVisibility(View.VISIBLE);
            bt_DialogAction4.setVisibility(View.VISIBLE);
        }
        bt_DialogAction1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CameraAction();
                dialog.dismiss();
            }
        });
        bt_DialogAction2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                GalleryAction();
                dialog.dismiss();

            }
        });
        bt_DialogAction3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                divider.setBackgroundColor(getResources().getColor(
                        android.R.color.transparent));
                img_progress.setVisibility(View.VISIBLE);
                bt_DialogAction1.setEnabled(false);
                new AsyncSaveProductImage(dialog).execute(imagestring);

            }
        });
        bt_DialogAction4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });
        dialog.setCancelable(false);
        dialog.show();

    }


    private void GalleryAction() {

        Log.d("EXTERNAL_CONTENT_URI",""+"EXTERNAL_CONTENT_URI");

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PICTURE);

//        if(Build.VERSION.SDK_INT>23){
//            Intent intent = new Intent(Intent.ACTION_PICK,
//                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(intent, SELECT_PICTURE);
//        }else{
//            Log.d("EXTERNAL_CONTENT_URI",""+"EXTERNAL_CONTENT_URI");
//            Intent intent = new Intent();
//            // call android default gallery
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            // ******** code for crop image
//            intent.putExtra("crop", "true");
//            intent.putExtra("aspectX", 0);
//            intent.putExtra("aspectY", 0);
//            try {
//                intent.putExtra("return-data", true);
//                startActivityForResult(intent, SELECT_PICTURE);
//
//            } catch (ActivityNotFoundException e) {
//
//            }
//        }

    }

    private void CameraAction() {
        try {

            if(Build.VERSION.SDK_INT>21){
                Log.d("PICK_FROM_CAMERA",""+PICK_FROM_CAMERA);
                Intent pictureIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE
                );
                if(pictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(pictureIntent,
                            PICK_FROM_CAMERA);
                }
            }else{

                Intent captureIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                // we will handle the returned data in onActivityResult
                startActivityForResult(captureIntent, PICK_FROM_CAMERA);
            }
            // use standard intent to capture an image


        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {

                case PICK_FROM_CAMERA:
                    if (requestCode == PICK_FROM_CAMERA) {
                        Log.d("PICK_FROM_CAMERA",""+PICK_FROM_CAMERA);
                        if(Build.VERSION.SDK_INT>21){
                            if (requestCode == PICK_FROM_CAMERA &&
                                    resultCode == RESULT_OK) {
                                if (data != null && data.getExtras() != null) {
                                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                                    Log.d("imageBitmap", ""+imageBitmap);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90,
                                            baos);
                                    byte[] b = baos.toByteArray();

                                    Log.d("byteArray", ""+b);

                                    String imageEncoded = Base64.encodeToString(b,
                                            Base64.DEFAULT);

                                    Log.d("imgEncode", imageEncoded);

                                    zoomViews(imageEncoded, SELECT_PICTURE);
                                }
                            }
                        }else{
                            picUri = data.getData();
                            performCrop();
                        }



                    }
                    break;
                case CROP_PIC:
                    // user is returning from cropping the image
                    if (requestCode == CROP_PIC) {
                        Log.d("imageBitmap", "--->" + data.getData());
                        if (data != null) {

                            Bitmap imageBitmap = null;
                            try {
                                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90,
                                    baos);
                            byte[] b = baos.toByteArray();

                            Log.d("byteArray", "" + b);

                            String imageEncoded = Base64.encodeToString(b,
                                    Base64.DEFAULT);

                            Log.d("imgEncode", imageEncoded);

                            zoomViews(imageEncoded, SELECT_PICTURE);
                        }
                    }

                    break;
                case SELECT_PICTURE:
                    if (requestCode == SELECT_PICTURE) {
                        Log.d("imageBitmap", "--->" +data);
//                        Uri contentURI = data.getData();
                        Log.d("imageBitmap", "--->" + data.getData());
                            if (data != null) {

                                Bitmap imageBitmap = null;
                                try {
                                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90,
                                        baos);
                                byte[] b = baos.toByteArray();

                                Log.d("byteArray", "" + b);

                                String imageEncoded = Base64.encodeToString(b,
                                        Base64.DEFAULT);

                                Log.d("imgEncode", imageEncoded);

                                zoomViews(imageEncoded, SELECT_PICTURE);
                            }

//                        if(Build.VERSION.SDK_INT>23){
//                            Uri contentURI = data.getData();
//                            if (data != null && data.getExtras() != null) {
//
//                                Bitmap imageBitmap = null;
//                                try {
//                                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                                    Log.d("imageBitmap", "--->"+imageBitmap);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90,
//                                        baos);
//                                byte[] b = baos.toByteArray();
//
//                                Log.d("byteArray", ""+b);
//
//                                String imageEncoded = Base64.encodeToString(b,
//                                        Base64.DEFAULT);
//
//                                Log.d("imgEncode", imageEncoded);
//
//                                zoomViews(imageEncoded, SELECT_PICTURE);
//                            }
//                        }else {
//
//
//                            //if (resultCode == RESULT_OK) {
//                            Bundle extras = data.getExtras();
//                            Log.d("imagedata", "-->" + extras);
//                            if (extras != null) {
//                                Bitmap bp = (Bitmap) data.getExtras().get("data");
//                                Log.d("galllerybp", "" + bp);
//
//                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                                bp.compress(Bitmap.CompressFormat.JPEG, 90,
//                                        baos);
//                                byte[] b = baos.toByteArray();
//
//                                Log.d("galllery byte array", "" + b);
//
//                                String imageEncoded = Base64.encodeToString(b,
//                                        Base64.DEFAULT);
//                                zoomViews(imageEncoded, SELECT_PICTURE);
//                            }
//                        }
						/*Log.d("data gallery","dd "+data.getData());

						try {
							Uri selectedImageUri = data.getData();
							String selectedImagePath = getPath(selectedImageUri);
							Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
							Bitmap bp = BitmapFactory.decodeFile(selectedImagePath);

							 Log.d("selectedImagePath", ""+selectedImagePath);
						     Log.d("bitmap String ", ""+bitmap);

							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							bp.compress(Bitmap.CompressFormat.JPEG, 90, baos);
							byte[] b = baos.toByteArray();

							Log.d("byte array", Arrays.toString(b));
							Log.d("byte length", ""+b.length);

							String imageEncoded = Base64.encodeToString(b,
									Base64.DEFAULT);
							zoomView(imageEncoded, SELECT_PICTURE);


						} catch (Exception e) {
							e.printStackTrace();
						}*/
					/*} else if (resultCode == RESULT_CANCELED) {

						// user cancelled Image capture
						Toast.makeText(getApplicationContext(),
								"User cancelled image capture",
								Toast.LENGTH_SHORT).show();

					} else {
						// failed to capture image
						Toast.makeText(getApplicationContext(),
								"Sorry! Failed to capture image",
								Toast.LENGTH_SHORT).show();
					}*/

                    }
                    break;
            }
        }
    }

    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 0);
            cropIntent.putExtra("aspectY", 0);
            // indicate output X and Y
            //cropIntent.putExtra("outputX", 256);
            //cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void dialogSubMenu() {
        submenuArra = new ArrayList<String>();
        submenuArra.clear();

        // Create dialog
        final Dialog dialog = new Dialog(ProductStockDetailActivity.this);
        dialog.setTitle("Image");
        dialog.setContentView(R.layout.image_dialog);

        LinearLayout imageLayout = (LinearLayout) dialog
                .findViewById(R.id.image_layout);
        ListView listview = (ListView) dialog.findViewById(R.id.listView1);

        imageLayout.setVisibility(View.GONE);
        listview.setVisibility(View.VISIBLE);
        /*
         * if(productstockimage != null && !productstockimage.isEmpty()){
         *
         * submenuArra.add("View Image"); } else{
         * submenuArra.add("Take a Picture"); submenuArra.add("Gallery"); }
         */
        submenuArra.add("Take a Picture");
        submenuArra.add("Gallery");

        ArrayAdapter<String> submenuAdaptet = new ArrayAdapter<String>(
                ProductStockDetailActivity.this, android.R.layout.select_dialog_item,
                submenuArra);
        listview.setAdapter(submenuAdaptet);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long id) {

                /*
                 * if(productstockimage != null &&
                 * !productstockimage.isEmpty()){ if(position == 0){ //
                 * Toast.makeText(getApplicationContext(), "View Image",
                 * Toast.LENGTH_SHORT).show();
                 * zoomView(productstockimage,VIEW_IMAGE); dialog.dismiss(); }
                 *
                 * }
                 */
                // else{
                if (position == 0) {
                    // Toast.makeText(getApplicationContext(), "Take a picture",
                    // Toast.LENGTH_SHORT).show();
                    CameraAction();
                    dialog.dismiss();

                } else if (position == 1) {
                    // Toast.makeText(getApplicationContext(), "Gallery",
                    // Toast.LENGTH_SHORT).show();
                    GalleryAction();
                    dialog.dismiss();
                }
                // }

            }
        });

        // Show the dialog
        dialog.show();
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

    private class AsyncSaveProductImage extends AsyncTask<String, Void, String> {
        Dialog dialog;
        String resTxt="";
        public AsyncSaveProductImage(Dialog dialog) {
            // TODO Auto-generated constructor stub
            this.dialog = dialog;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            String strProductImage = params[0];

            Log.d("", "" + strProductImage);


            try {

                SoapObject request = new SoapObject(NAMESPACE, "fncUpdateProductImage");

                PropertyInfo CompanyCode = new PropertyInfo();
                PropertyInfo ProductCode = new PropertyInfo();
                PropertyInfo FileName = new PropertyInfo();
                PropertyInfo ImagesBytes = new PropertyInfo();

                CompanyCode.setName("CompanyCode");
                CompanyCode.setValue(sCompanyCode);
                CompanyCode.setType(String.class);
                request.addProperty(CompanyCode);

                ProductCode.setName("ProductCode");
                ProductCode.setValue(sProductCode);
                ProductCode.setType(String.class);
                request.addProperty(ProductCode);

                FileName.setName("FileName");
                FileName.setValue("product.png");
                FileName.setType(String.class);
                request.addProperty(FileName);

                ImagesBytes.setName("ImagesBytes");
                ImagesBytes.setValue(strProductImage);
                ImagesBytes.setType(Byte.class);
                request.addProperty(ImagesBytes);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                envelope.bodyOut = request;
                HttpTransportSE androidHttpTransport = new HttpTransportSE(valid_url);
                androidHttpTransport.call(SOAP_ACTION + "fncUpdateProductImage", envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                resTxt = response.toString();
                jsonString = " { SODetails : " + resTxt + "}";

            } catch (Exception e) {
                e.printStackTrace();

            }

            Log.d("jsonStr ", "" + jsonString);

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            img_progress.setVisibility(View.GONE);
            if ((resTxt != null && !resTxt.isEmpty())) {

                if (resTxt.matches("true")) {
                    Toast.makeText(getApplication(), "Saved Successfully",
                            Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(ProductStockDetailActivity.this, "Failed",
                            Toast.LENGTH_SHORT).show();
                }
            }

            else {
                Toast.makeText(ProductStockDetailActivity.this, "Failed",
                        Toast.LENGTH_SHORT).show();
            }
            divider.setBackgroundColor(getResources().getColor(R.color.blue));
            bt_DialogAction1.setEnabled(true);
            dialog.dismiss();
        }

    }
}
