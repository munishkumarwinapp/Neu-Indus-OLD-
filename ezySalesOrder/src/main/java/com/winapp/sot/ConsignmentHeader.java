package com.winapp.sot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.adapter.DOHeaderAdapter;
import com.winapp.adapter.PopWindowAdapter;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.helper.DateTime;
import com.winapp.helper.XMLParser;
import com.winapp.model.Product;
import com.winapp.model.Schedule;
import com.winapp.offline.OfflineCommon;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printer.CommonPreviewPrint;
import com.winapp.printer.InvoicePrintPreview;
import com.winapp.printer.PDFActivity;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.PrinterZPL;
import com.winapp.printer.UIHelper;
import com.winapp.util.XMLAccessTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class ConsignmentHeader extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace, LocationListener,Constants {
    private ExpandableListView mExpandableListView;
    String valid_url, serverdate, sDate, eDate, cuscode, deleteResult, user,
            cmpnyCode,locationCode="";
    int textlength = 0;
    int month, day, year, printid;
    EditText edCustomerCode, starteditTextDate, endeditTextDate,sl_namefield,locationcode_filter;
    Button btcstmrsrch;
    ListView do_lv;
    ProgressBar progressBar;
    LinearLayout spinnerLayout,status_layout;
    ArrayList<HashMap<String, String>> searchResults,locationArrHm;
    ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
    CustomAlertAdapterSupp arrayAdapterSupp;
    private AlertDialog myalertDialog = null;
    LinearLayout deliveryO_parent, searchCstmrlayout, codelayout,mSignatureLayout,mCameraLayout;
    Calendar startCalendar, endCalendar;
    ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
    ArrayList<SO> searchCstCdArr = new ArrayList<SO>();
    ArrayList<HashMap<String, String>> doDetailsArr = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> doHeaderArr = new ArrayList<HashMap<String, String>>();
    static String headeresult;
    private static String SOAP_ACTION = "http://tempuri.org/";
    private static String webMethName = "fncGetConsignmentHeader";
    private static String webMethName1 = "fncGetDOHeaderScheduled";
    private static String NAMESPACE = "http://tempuri.org/";
    private ArrayList<SO> mDoHeaderChildList;
    private ArrayList<Schedule> mDoHeaderData;
    //	private DOAdapter mDOAdapter;
    ArrayList<SO> list = new ArrayList<SO>();
    // DOAdapter doadapter;
    DOHeaderAdapter doadapter;
    String durationDays;

    private UIHelper helper;
    ArrayList<ProductDetails> product,product_batchArr,footerArr;
    ArrayList<ProductDetails> productdet;
    String sosno, sodate, socustomercode, socustomername, soamount, sostatus,loccode,
            cusCode,select_van="",soNo="",status;
    String jsonString = null, jsonStr = null;
    JSONObject jsonResponse, jsonResp;
    JSONArray jsonMainNode, jsonSecNode;
    HashMap<String, String> hashValue = new HashMap<String, String>();
    Cursor cursor;
    ImageButton search, back, printer, addnew;
    private TextView tv_do_sno, tv_do_cutomer;
    private ImageView iv_arrow;
    private boolean arrowflag = true;
    private SlidingMenu menu;
    private SOTDatabase sotdb;
    private ArrayList<HashMap<String, String>> DoDetailsArr;
    private ArrayList<HashMap<String, String>> DoHeadersArr;
    private ArrayList<HashMap<String, String>> DoBarcodeArr;
    AlertDialog.Builder myDialog;
    private String cartonOrLoose="";
    String invoiceStr, getSignatureimage = "", getPhotoimage="",receiptsStr;
    boolean isDeliveryPrint;
    private HashMap<String, String> params;
    ImageView mChangeSignBtn,mSignature,prodphoto,sm_camera_iv;
    private static final int PICK_FROM_CAMERA = 1;
    public static final int SIGNATURE_ACTIVITY = 2;
    private double mLatitude=0, mLongitude=0;
    private LocationManager locationManager;
    private LinkedHashSet<String> lhs;
    //	private ArrayList<String> mDateList = new ArrayList<String>();
//	private ArrayList<String> assigndateArr = new ArrayList<String>();
    private ArrayList<String> uniqueAssignDateArr;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private String mCurrentgetPath="",mCurrentPhotoPath="";
    Spinner spinner_status,invoice_spinner;
    private Button consign_btn,return_btn,invoice_btn;
    String tran_type="",decimalpts = ".00",statusvalue = "",status_value;
    List<String> printsortHeader = new ArrayList<String>();
    ArrayList<String> sortproduct = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(drawable.ic_menu);

        SalesOrderSetGet.setHeader_flag("ConsignmentHeader");

        Mint.initAndStartSession(ConsignmentHeader.this, "29088aa0");
        setContentView(R.layout.activity_consignment);
        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        View customNav = LayoutInflater.from(this).inflate(
                R.layout.slidemenu_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        txt.setText("Consignment");
        search = (ImageButton) customNav.findViewById(R.id.search_img);
        printer = (ImageButton) customNav.findViewById(R.id.printer);
//        search.setVisibility(View.GONE);
        addnew = (ImageButton) customNav.findViewById(R.id.custcode_img);
        locationcode_filter = (EditText) findViewById(R.id.locationcode_filter);
        sl_namefield= (EditText) findViewById(R.id.sl_namefield);
        spinner_status = (Spinner) findViewById(R.id.invoice_status);
        invoice_spinner = (Spinner) findViewById(R.id.invoice);
        consign_btn =(Button) findViewById(R.id.tab_notpaid);
        return_btn =(Button) findViewById(R.id.tab_overdue);
        invoice_btn =(Button) findViewById(R.id.tab_paid);

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

        cartonOrLoose = SalesOrderSetGet.getCartonpriceflag();
        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView1);
        edCustomerCode = (EditText) findViewById(R.id.deliOCustCode);
        status_layout = (LinearLayout) findViewById(R.id.status_layout);
        starteditTextDate = (EditText) findViewById(R.id.starteditTextDate);
        endeditTextDate = (EditText) findViewById(R.id.endeditTextDate);
        btcstmrsrch = (Button) findViewById(R.id.deliO_btsearch);
        do_lv = (ListView) findViewById(R.id.deliO_listView1);
        deliveryO_parent = (LinearLayout) findViewById(R.id.deliOrder_parent);
        searchCstmrlayout = (LinearLayout) findViewById(R.id.delisearchlayout);
        codelayout = (LinearLayout) findViewById(R.id.codelayout);
        iv_arrow = (ImageView) findViewById(R.id.sm_arrow);
        tv_do_sno = (TextView) findViewById(R.id.deliOSno);
        tv_do_cutomer = (TextView) findViewById(R.id.deliOCustomer);
        status_layout.setVisibility(View.VISIBLE);

        spinnerValue();
        spinnerstatus();

//        open_tab = (Button) findViewById(R.id.open_tab);
//        completed_tab = (Button) findViewById(R.id.completed_tab);
//        all_tab = (Button) findViewById(R.id.all_tab);
//        tag_header = (LinearLayout) findViewById(R.id.tag_header);

//        searchCstmrlayout.setVisibility(View.GONE);
        product = new ArrayList<ProductDetails>();
        productdet = new ArrayList<ProductDetails>();
        product_batchArr=new ArrayList<>();
        footerArr =new ArrayList<>();
        sotdb = new SOTDatabase(ConsignmentHeader.this);
        DoDetailsArr = new ArrayList<HashMap<String, String>>();
        DoHeadersArr = new ArrayList<HashMap<String, String>>();
        DoBarcodeArr = new ArrayList<HashMap<String, String>>();

        doadapter = new DOHeaderAdapter(ConsignmentHeader.this);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        al.clear();
        searchCstCdArr.clear();
        DoDetailsArr.clear();
        DoHeadersArr.clear();
        DoBarcodeArr.clear();
        helper = new UIHelper(ConsignmentHeader.this);
        FWMSSettingsDatabase.init(ConsignmentHeader.this);
        valid_url = FWMSSettingsDatabase.getUrl();
        new SOTSummaryWebService(valid_url);
        new DateWebservice(valid_url,ConsignmentHeader.this);
        new SalesOrderWebService(valid_url);
        new WebServiceClass(valid_url);
        SOTDatabase.init(ConsignmentHeader.this);
        tv_do_sno.setVisibility(View.GONE);
        tv_do_cutomer.setText("CustName");

        user = SupplierSetterGetter.getUsername();
        cmpnyCode = SupplierSetterGetter.getCompanyCode();
        locationCode = SalesOrderSetGet.getLocationcode();
        select_van = SOTDatabase.getVandriver();
        uniqueAssignDateArr = new ArrayList<String>();
//		SalesOrderSetGet.setSchedulingType("");
        locationArrHm = new ArrayList<>();
        searchCstmrlayout.setVisibility(View.GONE);


//        if(SalesOrderSetGet.getSchedulingType()!=null && !SalesOrderSetGet.getSchedulingType().isEmpty()) {
//            if (SalesOrderSetGet.getSchedulingType().matches("DO")) {
//                tag_header.setVisibility(View.VISIBLE);
//            }else{
//                tag_header.setVisibility(View.GONE);
//            }
//        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String invnumber = getIntent().getStringExtra("InvoiceNo");
            String companyCode = SupplierSetterGetter.getCompanyCode();
            if(invnumber!=null && !invnumber.isEmpty()){
                new PDFActivity(ConsignmentHeader.this,valid_url+"/"+FNCA4INVOICEGENERATE+"?CompanyCode="+companyCode+"&sInvoiceNo="+invnumber, "report.pdf").execute();
            }
        }




        boolean showalllocatoin = FormSetterGetter.isShowAllLocation();
        loccode = SalesOrderSetGet.getLocationcode();
        //showalllocatoin = true;
        if (showalllocatoin == true) {
            locationcode_filter.setFocusableInTouchMode(true);
            locationcode_filter.setBackgroundResource(drawable.edittext_bg);
            locationcode_filter.setCursorVisible(true);
        }else{
            loccode = SalesOrderSetGet.getLocationcode();
            String locName = SalesOrderSetGet.getLocationname();
            locationcode_filter.setText(locName);
            locationcode_filter.setEnabled(false);
            locationcode_filter.setFocusableInTouchMode(false);
            locationcode_filter.setBackgroundResource(drawable.labelbg);
            locationcode_filter.setCursorVisible(false);
            locationcode_filter.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        }
        if(select_van!=null && !select_van.isEmpty()){
        }else{
            select_van="";
        }
        /******** Based on GetGeneralSettings ShowPriceDO Amount will Gone or Visible *********/
        if(MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase("true")){
            ((TextView) findViewById(R.id.deliOAmount)).setVisibility(View.VISIBLE);

        }else{
            ((TextView) findViewById(R.id.deliOAmount)).setVisibility(View.GONE);
        }

//        if(SalesOrderSetGet.getFlag().matches("Returns")){
//            tran_type="COR";
//            SalesOrderSetGet.setTranType(tran_type);
//            new AsyncCallWSSalesOrder(tran_type).execute();
//        }else{
//            tran_type="O";
//            SalesOrderSetGet.setTranType(tran_type);
//            new AsyncCallWSSalesOrder(tran_type).execute();
//        }


        /********Based on Scheduling Type GetGeneralSettings  will Gone or Visible *********/
        if(SalesOrderSetGet.getSchedulingType()!=null && !SalesOrderSetGet.getSchedulingType().isEmpty()){
            if(SalesOrderSetGet.getSchedulingType().matches("CG")){
                ((LinearLayout) findViewById(R.id.codelayout)).setVisibility(View.GONE);
                starteditTextDate.setVisibility(View.GONE);
                locationcode_filter.setVisibility(View.GONE);
            }else{
                ((LinearLayout) findViewById(R.id.codelayout)).setVisibility(View.VISIBLE);
                starteditTextDate.setVisibility(View.VISIBLE);
                locationcode_filter.setVisibility(View.VISIBLE);
            }
        }
        else{
            ((LinearLayout) findViewById(R.id.codelayout)).setVisibility(View.VISIBLE);
            starteditTextDate.setVisibility(View.VISIBLE);
            locationcode_filter.setVisibility(View.VISIBLE);
        }

        //	AsyncCallWSSalesOrder delivOAC = new AsyncCallWSSalesOrder();
        //	delivOAC.execute();
        locationcode_filter
                .setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
                        locationcode_filter) {
                    @Override
                    public boolean onDrawableClick() {
                        if(locationArrHm.size()>0){
                            alertDialogLocation();
                        }else{
                            new ShowAllLocation().execute();
                        }
                        return true;
                    }
                });

        do_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long id) {

//                if(SalesOrderSetGet.getTranType().matches("COI")){
//                    SO so = adapter.getItem(position);
//                    sosno = so.getSno().toString();
//                    socustomername = so.getCustomerName().toString();
//                    SalesOrderSetGet.setCustomername(socustomername);
//                }else{
                    SO so = doadapter.getItem(position);
                    sosno = so.getSno().toString();
                    socustomername = so.getCustomerName().toString();
                    SalesOrderSetGet.setCustomername(socustomername);
//                }

                Toast.makeText(ConsignmentHeader.this, socustomername,
                        Toast.LENGTH_LONG).show();
            }
        });
        registerForContextMenu(do_lv);
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

        starteditTextDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    new DatePickerDialog(ConsignmentHeader.this, startDate,
                            startCalendar.get(Calendar.YEAR), startCalendar
                            .get(Calendar.MONTH), startCalendar
                            .get(Calendar.DAY_OF_MONTH)).show();
                return false;
            }
        });

        endeditTextDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    new DatePickerDialog(ConsignmentHeader.this, endDate,
                            endCalendar.get(Calendar.YEAR), endCalendar
                            .get(Calendar.MONTH), endCalendar
                            .get(Calendar.DAY_OF_MONTH)).show();
                return false;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                InputMethodManager inputMethodManager = (InputMethodManager) ConsignmentHeader.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

                spinnerValue();
                spinnerstatus();

                if (searchCstmrlayout.getVisibility() == View.VISIBLE) {
                    searchCstmrlayout.setVisibility(View.GONE);
                    inputMethodManager.hideSoftInputFromWindow(
                            codelayout.getWindowToken(), 0);
                } else {
                    searchCstmrlayout.setVisibility(View.VISIBLE);
                    edCustomerCode.requestFocus();
                    inputMethodManager.toggleSoftInputFromWindow(
                            codelayout.getApplicationWindowToken(),
                            InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });


        iv_arrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (arrowflag) {

                    arrowflag = false;
                    iv_arrow.setImageResource(drawable.ic_arrow_left);
                    tv_do_sno.setVisibility(View.VISIBLE);
                    tv_do_cutomer.setText("CustCode");
                    doadapter.showAll(true);
                } else if (arrowflag == false) {

                    iv_arrow.setImageResource(drawable.ic_arrow_right);
                    tv_do_sno.setVisibility(View.GONE);
                    tv_do_cutomer.setText("CustName");
                    arrowflag = true;
                    doadapter.showAll(false);
                }
            }
        });
        btcstmrsrch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                status_layout.setVisibility(View.GONE);
                cuscode = edCustomerCode.getText().toString();
                sDate = starteditTextDate.getText().toString();
                eDate = endeditTextDate.getText().toString();

                spinnerValue();
                Log.d("SalesOrderType",SalesOrderSetGet.getTranType());
                if (SalesOrderSetGet.getTranType().matches("COI")) {

                    Log.d("FromDate", "" + sDate);
                    Log.d("ToDate", "" + eDate);
                    Log.d("VanCode", "" + select_van);
                    Log.d("LocationCode", "-->" + loccode);
                    Log.d("TranType", "--->" + SalesOrderSetGet.getTranType());

                    searchCstCdArr = SalesOrderWebService.SearchIOCustCode(
                            cuscode, sDate, eDate, status, select_van,loccode,
                            "fncGetConsignmentInvoiceHeader");


                    if (!searchCstCdArr.isEmpty()) {
                        try {
                            searchCustCode();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        searchCstCdArr.clear();
                        try {
                            searchCustCode();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(ConsignmentHeader.this, "No matches found",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    new AsyncCallWSSearchCustCode().execute();
                }




            }
        });
//        search.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                InputMethodManager inputMethodManager = (InputMethodManager) ConsignmentHeader.this
//                        .getSystemService(Context.INPUT_METHOD_SERVICE);
//
//                if (searchCstmrlayout.getVisibility() == View.VISIBLE) {
//                    searchCstmrlayout.setVisibility(View.GONE);
//                    inputMethodManager.hideSoftInputFromWindow(
//                            codelayout.getWindowToken(), 0);
//                } else {
//                    searchCstmrlayout.setVisibility(View.VISIBLE);
//                    if(SalesOrderSetGet.getSchedulingType().matches("CG")){
//                        inputMethodManager.hideSoftInputFromWindow(
//                                codelayout.getWindowToken(), 0);
//                    }else{
//                        edCustomerCode.requestFocus();
//                        inputMethodManager.toggleSoftInputFromWindow(
//                                codelayout.getApplicationWindowToken(),
//                                InputMethodManager.SHOW_FORCED, 0);
//                    }
//
//                }
//            }
//        });

        printer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                printid = v.getId();
                SOTDatabase.deleteImage();
                cursor = FWMSSettingsDatabase.getPrinter();
                if (cursor.getCount() != 0) {
                    if (RowItem.getPrintoption().equals("True")) {
//                        if(SalesOrderSetGet.getTranType().matches("COI")){
//                            SO so = adapter.getItem(adapter
//                                    .getSelectedPosition());
//                            sosno = so.getSno().toString();
//                        }else{
                            SO so = doadapter.getItem(doadapter
                                    .getSelectedPosition());
                            sosno = so.getSno().toString();
//                        }


                        String cmpy = SupplierSetterGetter.getCompanyCode();
                        params = new HashMap<String, String>();
                        params.put("CompanyCode", cmpy);
                        params.put("InvoiceNo", sosno);
                        params.put("TranType", "CG");
                        Log.d("sosno", ""+sosno);
                        new XMLAccessTask(ConsignmentHeader.this, valid_url,
                                "fncGetInvoiceSignature", params, false,
                                new GetDeliverySignatureforReprint()).execute();

                        helper.showProgressDialog(R.string.generating_consignment);
                        AsyncPrintCall task = new AsyncPrintCall();
                        task.execute();
                    } else {
                        Toast.makeText(ConsignmentHeader.this,
                                "Please Enable CheckBox", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(ConsignmentHeader.this,
                            "Please Configure the printer", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                clearSetterGetter();
                Intent i = new Intent(ConsignmentHeader.this,
                        ConsignmentCustomer.class);
               SalesOrderSetGet.setType("");
                startActivity(i);
                ConsignmentHeader.this.finish();
            }

        });
        Log.d("SalesType","-->"+SalesOrderSetGet.getTranType());
        if(SalesOrderSetGet.getTranType().matches("")){
            consign_btn.setBackgroundResource(R.mipmap.tab_blue_bg_select);
            return_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
            invoice_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
                tran_type="O";
                SalesOrderSetGet.setTranType(tran_type);
            new AsyncCallWSSalesOrder(tran_type).execute();
            SalesOrderSetGet.setTranType(tran_type);
        }else if(SalesOrderSetGet.getTranType().matches("O")){
                consign_btn.setBackgroundResource(R.mipmap.tab_blue_bg_select);
                return_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
                invoice_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
                tran_type="O";
            SalesOrderSetGet.setTranType(tran_type);
                new AsyncCallWSSalesOrder(tran_type).execute();
                SalesOrderSetGet.setTranType(tran_type);
        }else if (SalesOrderSetGet.getTranType().matches("COR")){
                consign_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
                return_btn.setBackgroundResource(R.mipmap.tab_blue_bg_select);
                invoice_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
                tran_type="COR";
                SalesOrderSetGet.setTranType(tran_type);
                new AsyncCallWSSalesOrder(tran_type).execute();
                SalesOrderSetGet.setTranType(tran_type);
        }else if (SalesOrderSetGet.getTranType().matches("COI")) {
                consign_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
                return_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
                invoice_btn.setBackgroundResource(R.mipmap.tab_blue_bg_select);
                tran_type="COI";
            SalesOrderSetGet.setTranType(tran_type);
                SalesOrderSetGet.setTranType(tran_type);
                new AsyncCallWSSalesOrder(tran_type).execute();
            }




        consign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consign_btn.setBackgroundResource(R.mipmap.tab_blue_bg_select);
                return_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
                invoice_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
                tran_type="O";
                SalesOrderSetGet.setTranType(tran_type);
                new AsyncCallWSSalesOrder(tran_type).execute();
                SalesOrderSetGet.setTranType(tran_type);
            }
        });
        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consign_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
                return_btn.setBackgroundResource(R.mipmap.tab_blue_bg_select);
                invoice_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
                tran_type="COR";
                SalesOrderSetGet.setTranType(tran_type);
                new AsyncCallWSSalesOrder(tran_type).execute();
                SalesOrderSetGet.setTranType(tran_type);
            }
        });

        invoice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consign_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
                return_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
                invoice_btn.setBackgroundResource(R.mipmap.tab_blue_bg_select);
                tran_type="COI";
                SalesOrderSetGet.setTranType(tran_type);
                SalesOrderSetGet.setTranType(tran_type);
                new AsyncCallWSSalesOrder(tran_type).execute();
            }
        });

//        completed_tab.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//
//                open_tab.setBackgroundResource(R.mipmap.tab_green_unselect);
//                completed_tab.setBackgroundResource(drawable.tab_green);
//                all_tab.setBackgroundResource(R.mipmap.tab_green_unselect);
//
//                if(SalesOrderSetGet.getSchedulingType()!=null && !SalesOrderSetGet.getSchedulingType().isEmpty()) {
//                    if (SalesOrderSetGet.getSchedulingType().matches("DO")) {
//                        startProgressBar();
//                        new LoadDOHeaderScheduled("1").execute();
//                    }
//                }
//            }
//
//        });
//
//        open_tab.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//
//                open_tab.setBackgroundResource(drawable.tab_green);
//                completed_tab.setBackgroundResource(R.mipmap.tab_green_unselect);
//                all_tab.setBackgroundResource(R.mipmap.tab_green_unselect);
//
//                if(SalesOrderSetGet.getSchedulingType()!=null && !SalesOrderSetGet.getSchedulingType().isEmpty()) {
//                    if (SalesOrderSetGet.getSchedulingType().matches("DO")) {
//                        startProgressBar();
//                        new LoadDOHeaderScheduled("0").execute();
//                    }
//                }
//            }
//
//        });
//
//        all_tab.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//
//                open_tab.setBackgroundResource(R.mipmap.tab_green_unselect);
//                completed_tab.setBackgroundResource(R.mipmap.tab_green_unselect);
//                all_tab.setBackgroundResource(drawable.tab_green);
//
//                if(SalesOrderSetGet.getSchedulingType()!=null && !SalesOrderSetGet.getSchedulingType().isEmpty()) {
//                    if (SalesOrderSetGet.getSchedulingType().matches("DO")) {
//                        startProgressBar();
//                        new LoadDOHeaderScheduled("").execute();
//                    }
//                }
//            }
//
//        });
    }

    private void spinnerValue() {
        statusvalue = spinner_status.getSelectedItem().toString();
        Log.d("valueCHekn",statusvalue);
        if(statusvalue.matches("Consignment")){
            consign_btn.setBackgroundResource(R.mipmap.tab_blue_bg_select);
            return_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
            invoice_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
            tran_type="O";
            SalesOrderSetGet.setTranType(tran_type);
        }else if( statusvalue.matches("Consignment Return")){
            consign_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
            return_btn.setBackgroundResource(R.mipmap.tab_blue_bg_select);
            invoice_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
            tran_type="COR";
            SalesOrderSetGet.setTranType(tran_type);
        }else if (statusvalue.matches("Consignment Invoice")){
            consign_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
            return_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
            invoice_btn.setBackgroundResource(R.mipmap.tab_blue_bg_select);
            tran_type="COI";
            SalesOrderSetGet.setTranType(tran_type);
            status_layout.setVisibility(View.VISIBLE);
            spinnerstatus();
        }else {
            consign_btn.setBackgroundResource(R.mipmap.tab_blue_bg_select);
            return_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
            invoice_btn.setBackgroundResource(R.mipmap.tab_blue_bg);
            tran_type="O";
            SalesOrderSetGet.setTranType(tran_type);
        }
    }

    private void spinnerstatus() {
        if(SalesOrderSetGet.getTranType().matches("COI")) {
            status_value = invoice_spinner.getSelectedItem().toString();
            if (status_value.matches(getResources().getString(R.string.not_paid))) {
                status = "0";
                Log.e("searchstatus", status);
            } else if (status_value.matches(getResources().getString(R.string.paid))) {
                status = "1";
                Log.e("searchstatus", status);

            } else if (status_value.matches(getResources().getString(R.string.all))) {
                status = "2";
                Log.e("searchstatus", status);

            } else if (status_value.matches(getResources().getString(R.string.pod_pending))) {
                status = "3";
                Log.e("searchstatus", status);
//            ((TextView) findViewById(R.id.txt_totaloutstanding)).setText(getResources().getString(R.string.totaloutstanding));
            } else {
                status = "0";
            }
        }else{
            status = "0";
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            SO so = doadapter.getItem(info.position);
            sosno = so.getSno().toString();
            sodate = so.getDate().toString();
            socustomercode = so.getCustomerCode().toString();
            socustomername = so.getCustomerName().toString();
            SalesOrderSetGet.setCustomername(socustomername);
            SalesOrderSetGet.setCustomercode(socustomercode);



        ConvertToSetterGetter.setDoNo("");
        ConvertToSetterGetter.setSoNo("");
        ConvertToSetterGetter.setEdit_do_no("");
        menu.setHeaderTitle(sosno);
//        double bal_amt =Double.parseDouble(SO.getBal_amt());
//        double netAmt = Double.parseDouble(SO.getNetTotal());
//        double paidAmt =Double.parseDouble(SO.getPaidAmt());
//        if((bal_amt==netAmt)|| (paidAmt>0))  {

//        }
        if(SalesOrderSetGet.getTranType().matches("COI")) {
            if (so.getBalanceamount().matches(so.getNettotal()) && so.getStatus().matches("0")) {
                menu.add(0, v.getId(), 0, "Edit CG");
                menu.add(0, v.getId(), 0, "Delete CG");
            }else{

            }
        }
        else{
            menu.add(0, v.getId(), 0, "Edit CG");
            menu.add(0, v.getId(), 0, "Delete CG");
        }

//        invoiceStr = FormSetterGetter.getInvoice();
//        if (invoiceStr.matches("Invoice")) {
//            menu.add(0, v.getId(), 0, "Convert To Invoice");
//        }
        menu.add(0, v.getId(), 0, "Print Preview");
        menu.add(0, v.getId(), 0, "Image/Signature");
        receiptsStr = FormSetterGetter.getReceipts();
        tran_type = SalesOrderSetGet.getTranType();
        if(tran_type.matches("COI")) {
            if (receiptsStr.matches("Receipts")) {
                Log.d("tranTypeVerify","-->"+tran_type +"receiptsStr :"+receiptsStr);
                menu.add(0, v.getId(), 0, "Cash Collection");
            }
        }

//        if(tran_type.matches("O")){
//            menu.add(0, v.getId(), 0, "Closed");
//        }
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {

        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();

            SO so = doadapter.getItem(adapterInfo.position);
            sosno = so.getSno().toString();
            cusCode = so.getCustomerCode().toString();
            soNo = so.getSoNo().toString();
            sodate = so.getDate().toString();
            socustomername = so.getCustomerName().toString();


        Log.d("sodate", sodate);
        Log.d("socustomername", socustomername);
        if (item.getTitle() == "Edit CG") {
            SalesOrderSetGet.setSoRemarks("");
            SalesOrderSetGet.setSoAdditionalInfo("");
            SOTDatabase.deleteImage();
            Cursor cursor =SOTDatabase.getConsignmentStock();
            Log.d("cursorValuesEdit","-->"+cursor.getCount());
            for (HashMap<String, String> hashMap : al) {
                for (String key : hashMap.keySet()) {
                    if (key.equals(socustomercode)) {
                        System.out.println(hashMap.get(key));
                        socustomername = hashMap.get(key);
                    }
                }
            }

            SalesOrderSetGet.setCustname(socustomername);
            sotdb.truncateTables();
            ConvertToSetterGetter.setSoNo(soNo);
            ConvertToSetterGetter.setEdit_do_no(sosno);
            new AsyncCallWSGetSignature(false).execute();

        }
        if (item.getTitle() == "Delete CG") {
            deleteDialog();
        }

        if (item.getTitle() == "Cash Collection") {
            Log.d("CashCollectioN","CashCollectioN"+"CashCollectioN");
            SOTDatabase.deleteImage();
            Intent i = new Intent(ConsignmentHeader.this,
                    InvoiceCashCollection.class);
            i.putExtra("invNo", sosno);
            i.putExtra("customerCode", socustomercode);
            startActivity(i);
            SalesOrderSetGet.setHeader_flag("ConsignmentHeader");
            ConsignmentHeader.this.finish();

        }
        /*if (item.getTitle() == "Convert To Invoice") {
            SalesOrderSetGet.setSoRemarks("");
            SalesOrderSetGet.setSoAdditionalInfo("");
            SOTDatabase.deleteImage();
            SOTDatabase.deleteBarcode();
            ConvertToSetterGetter.setEdit_inv_no("");
            for (HashMap<String, String> hashMap : al) {
                for (String key : hashMap.keySet()) {
                    if (key.equals(cusCode)) {
                        System.out.println(hashMap.get(key));
                        socustomername = hashMap.get(key);
                        SalesOrderSetGet.setCustname(socustomername);
                        Log.d("customer Name", "abcde" + socustomername);
                    }
                }
            }

            SalesOrderSetGet.setCustname(socustomername);

            AsyncCallWSDODetail task = new AsyncCallWSDODetail();
            task.execute();

        }*/

        if (item.getTitle() == "Image/Signature") {
            SOTDatabase.deleteImage();
            isDeliveryPrint = false;
            new AsyncCallWSGetSignature(true).execute();
        }

        if(item.getTitle()== "Returns"){
            SalesOrderSetGet.setCustname(socustomername);
            sotdb.truncateTables();
            ConvertToSetterGetter.setSoNo(soNo);
            ConvertToSetterGetter.setEdit_do_no(sosno);
            String flag="Returns";
            SalesOrderSetGet.setTranType("COR");
            SalesOrderSetGet.setFlag(flag);

            Intent i =new Intent(ConsignmentHeader.this,ConsignmentSummary.class);
            startActivity(i);
            ConsignmentHeader.this.finish();


        }


        if(item.getTitle()== "Convert To Invoice"){
            SalesOrderSetGet.setCustname(socustomername);
            sotdb.truncateTables();
            ConvertToSetterGetter.setSoNo(soNo);
            ConvertToSetterGetter.setEdit_do_no(sosno);
            String flag="Invoice";
            SalesOrderSetGet.setFlag(flag);
            Intent i =new Intent(ConsignmentHeader.this,InvoiceSummary.class);
            startActivity(i);
            ConsignmentHeader.this.finish();


        }

        if(item.getTitle()== "Returns"){
            SalesOrderSetGet.setCustname(socustomername);
            sotdb.truncateTables();
            ConvertToSetterGetter.setSoNo(soNo);
            ConvertToSetterGetter.setEdit_do_no(sosno);
            String flag="Returns";

            Intent i =new Intent(ConsignmentHeader.this,ConsignmentSummary.class);
            startActivity(i);
            ConsignmentHeader.this.finish();


        }

        if (item.getTitle() == "Print Preview") {
            helper.showProgressDialog(R.string.consignment_printpreview);
            new AsyncPrintCall().execute();
            for (HashMap<String, String> hashMap : al) {
                for (String key : hashMap.keySet()) {
                    if (key.equals(socustomercode)) {
                        System.out.println(hashMap.get(key));
                        socustomername = hashMap.get(key);
                    }
                }

            }

        }

        if(item.getTitle() == "Closed"){

        }



        else {
            return false;
        }
        return true;
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

    private class AsyncCallWSEditDO extends AsyncTask<Void, Void, Void> {
        String appType = LogOutSetGet.getApplicationType();
        boolean doQtyValidateWithSo;
        String customer_code="",sales_prodCode="";
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {

            // SalesOrderSetGet.setBalanceAmount("0");
            // SalesOrderSetGet.setNetTotal("0");
            DoDetailsArr.clear();
            DoHeadersArr.clear();
            DoBarcodeArr.clear();

//			spinnerLayout = new LinearLayout(DeliveryOrderHeader.this);
//			spinnerLayout.setGravity(Gravity.CENTER);
//			DeliveryOrderHeader.this.addContentView(spinnerLayout,
//					new LayoutParams(
//							android.view.ViewGroup.LayoutParams.FILL_PARENT,
//							android.view.ViewGroup.LayoutParams.FILL_PARENT));
//			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
//			enableViews(deliveryO_parent, false);
//			progressBar = new ProgressBar(DeliveryOrderHeader.this);
//			progressBar.setProgress(android.R.attr.progressBarStyle);
//			progressBar.setIndeterminateDrawable(getResources().getDrawable(
//					drawable.greenprogress));
//			spinnerLayout.addView(progressBar);

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Log.d("Invoice No:", sosno);
            try {
                Log.d("getDoQtyValidateWithSo","-->"+SalesOrderSetGet.getDoQtyValidateWithSo() +sosno);
                // Get data for checking qty must not exceed doqty
                if(SalesOrderSetGet.getDoQtyValidateWithSo().matches("0")){
                    if(soNo!=null && !soNo.isEmpty()){
                        Log.d("SoNo No:", soNo);
                        hashValue.put("CompanyCode", cmpnyCode);
                        hashValue.put("SoNo", soNo);
                        hashValue.put("LocationCode", locationCode);

                        jsonString = SalesOrderWebService.getSODetail(hashValue, "fncGetSODetail");
                        Log.d("jsonString ",""+jsonString);

                        jsonResponse = new JSONObject(jsonString);
                        jsonMainNode = jsonResponse.optJSONArray("SODetails");
                        int length = jsonMainNode.length();
                        Log.d("jsonMainNode","-->"+length);
                        if (length > 0) {
                            for (int i = 0; i < length; i++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                JSONObject jsonChildNode = jsonMainNode
                                        .getJSONObject(i);
                                String slNo = jsonChildNode.optString("slNo")
                                        .toString();
                                String productCode = jsonChildNode.optString(
                                        "ProductCode").toString();
                                String dQty = jsonChildNode.optString("Qty")
                                        .toString().split("\\.")[0];
                                String doQty = jsonChildNode.optString("DOQty")
                                        .toString();
                                if (dQty == null || dQty.trim().equals("")) {
                                    dQty = "0";
                                }
                                if (doQty == null || doQty.trim().equals("")) {
                                    doQty = "0";
                                }
                                Double dqty = new Double(dQty);
                                Double ddeliveryqty = new Double(doQty);
                                int quantity = dqty.intValue();
                                int deliveryQty = ddeliveryqty.intValue();
                                quantity = quantity - deliveryQty;
                                String qty = Integer.toString(quantity);
                                Log.d("qty", "" + qty);
                                hm.put("slNo", slNo);
                                hm.put("ProductCode", productCode);
                                hm.put("Qty", qty);
                                SOTDatabase.storeSODetailProduct(hm);

                            }
                        }
                        else{
                            //json return empty so detail so insert zero for identifying edit do with so number and edit do without so number
                            HashMap<String,String> hm = new HashMap<String, String>();
                            hm.put("slNo", "0");
                            hm.put("ProductCode", "0");
                            hm.put("Qty", "0");
                            SOTDatabase.storeSODetailProduct(hm);

                        }
                    }
                    else{
                        //SoNo is null so insert zero for identifying edit do with so number and edit do without so number
                        HashMap<String,String> hm = new HashMap<String, String>();
                        hm.put("slNo", "0");
                        hm.put("ProductCode", "0");
                        hm.put("Qty", "0");
                        SOTDatabase.storeSODetailProduct(hm);
                    }
                }
                DoDetailsArr = SalesOrderWebService.getConsignmentDetails(sosno,SalesOrderSetGet.getTranType(),
                        "fncGetConsignmentDetail");
                DoHeadersArr = SalesOrderWebService.getConsignmentHeader(sosno,SalesOrderSetGet.getTranType(),
                        "fncGetConsignmentHeaderByConsignmentNo");

                if(SalesOrderSetGet.getTranType().matches("COR")||SalesOrderSetGet.getTranType().matches("COI")) {
                    if (DoDetailsArr != null) {
                        for (int i = 0; i < DoDetailsArr.size(); i++) {
                            sales_prodCode = DoDetailsArr.get(i).get("ProductCode");
                        }
                    }
                    if (DoHeadersArr != null) {
                        for (int i = 0; i < DoHeadersArr.size(); i++) {
                            customer_code = DoHeadersArr.get(i).get("CustomerCode");
                            SalesOrderSetGet.setCustomercode(customer_code);
                        }
                    }

                    Log.d("CustomerCode", "-->" + customer_code + "-->" + sales_prodCode);



//
//                    DoBarcodeArr = SalesOrderWebService.
//                            getStockConsignmentArrs("fncGetConsignmentProductStock", customer_code, sales_prodCode);
                }

                SalesOrderWebService.getCustomerTax(cuscode, "fncGetCustomer");

//                if (appType.matches("W")) {
//                    DoBarcodeArr = SalesOrderWebService.getProductBarcode(sosno,
//                            "DONo", "fncGetDOCartonDetail");
//                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

	/*		String doqtyValidate = SalesOrderSetGet.getDoQtyValidateWithSo();

			if(doqtyValidate!=null && !doqtyValidate.isEmpty() && doqtyValidate.matches("1")){

			if(doQtyValidateWithSo==true){

				Intent i = new Intent(DeliveryOrderHeader.this,
						DeliverySummary.class);
				i.putExtra("DODetails", DoDetailsArr);
				i.putExtra("DOHeader", DoHeadersArr);
				if (appType.matches("W")) {
					i.putExtra("DOBarcode", DoBarcodeArr);
				}
				i.putExtra("getSignatureimage", getSignatureimage);
				startActivity(i);
				SalesOrderSetGet.setHeader_flag("DeliveryOrderHeader");
				DeliveryOrderHeader.this.finish();
			}else{
				Toast.makeText(DeliveryOrderHeader.this, "No quantity to edit DO", Toast.LENGTH_SHORT).show();
			}
			}else{*/
            Intent i = new Intent(ConsignmentHeader.this,
                    ConsignmentSummary.class);
            i.putExtra("flagData","ConsignmentHeader");
            i.putExtra("DODetails", DoDetailsArr);
            i.putExtra("DOHeader", DoHeadersArr);
//            if (SalesOrderSetGet.getTranType().matches("COR")||SalesOrderSetGet.getTranType().matches("COI")) {
//                Log.d("DoBarcodeArr","-->"+DoBarcodeArr.size());
//                i.putExtra("DOBarcode", DoBarcodeArr);
//            }

            i.putExtra("getSignatureimage", getSignatureimage);
            i.putExtra("getPhotoimage", getPhotoimage);
            startActivity(i);
            SalesOrderSetGet.setHeader_flag("ConsignmentHeader");
            ConsignmentHeader.this.finish();
            //	}


            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(deliveryO_parent, true);

        }
    }

    public void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ConsignmentHeader.this);
        builder.setTitle("Confirm Delete");
        builder.setIcon(R.mipmap.delete);
        builder.setMessage("Do you want to delete?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                new AsyncCallWSSalesDelete().execute();
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

    private class AsyncCallWSGetSignature extends AsyncTask<Void, Void, Void> {

        boolean isPrintCall;
        public AsyncCallWSGetSignature(boolean printCall) {
            isPrintCall = printCall;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            spinnerLayout = new LinearLayout(ConsignmentHeader.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            ConsignmentHeader.this.addContentView(spinnerLayout, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(deliveryO_parent, false);
            progressBar = new ProgressBar(ConsignmentHeader.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);
            getPhotoimage = "";
            getSignatureimage = "";
            getCurrentlocation();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String cmpy = SupplierSetterGetter.getCompanyCode();
            params = new HashMap<String, String>();
            params.put("CompanyCode", cmpy);
            params.put("InvoiceNo", sosno);
            params.put("TranType", "CG");
            Log.d("sosno", ""+sosno);

            new XMLAccessTask(ConsignmentHeader.this, valid_url,
                    "fncGetInvoicePhoto", params, false,
                    new GetDeliveryImage()).execute();

            new XMLAccessTask(ConsignmentHeader.this, valid_url,
                    "fncGetInvoiceSignature", params, false,
                    new GetDeliverySignature(isPrintCall)).execute();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
		/*	if(isPrintCall){
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(salesO_parent, true);
				printCallDialog();
			}else{
				AsyncCallWSInvoceDetail task = new AsyncCallWSInvoceDetail();
				task.execute();
			}*/
            if(!isPrintCall){
                Log.d("!isPrintCall", "!isPrintCall");
                new AsyncCallWSEditDO().execute();
            }


        }
    }

    public class GetDeliveryImage implements XMLAccessTask.CallbackInterface {
        public GetDeliveryImage() {
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

    public class GetDeliverySignature implements XMLAccessTask.CallbackInterface {
        boolean isPrintCall;
        public GetDeliverySignature(boolean printCall) {
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
            Log.d("getPhotoimage", "getPhotoimage" + getPhotoimage);
            if(isPrintCall){

                SOTDatabase.storeImage(1, getSignatureimage, getPhotoimage);
                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(deliveryO_parent, true);
                printCallDialog();
            }
        }

        @Override
        public void onFailure(XMLAccessTask.ErrorType error) {

            if(isPrintCall){
                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(deliveryO_parent, true);
                printCallDialog();
            }
            onError(error);
        }

    }

    public class GetDeliverySignatureforReprint implements XMLAccessTask.CallbackInterface {

        @Override
        public void onSuccess(NodeList nl) {

            getSignatureimage = "";
            for (int i = 0; i < nl.getLength(); i++) {

                Element e = (Element) nl.item(i);
                getSignatureimage = XMLParser.getValue(e, "RefSignature");
            }

            Log.d("getSignatureimage", "getSignatureimage" + getSignatureimage);

            SOTDatabase.storeImage(1, getSignatureimage, "");


        }

        @Override
        public void onFailure(XMLAccessTask.ErrorType error) {

            onError(error);
        }

    }

    private class AsyncCallWSSalesDelete extends AsyncTask<Void, Void, Void> {
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            spinnerLayout = new LinearLayout(ConsignmentHeader.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            ConsignmentHeader.this.addContentView(spinnerLayout,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(deliveryO_parent, false);
            progressBar = new ProgressBar(ConsignmentHeader.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Log.d("getTranType","-->"+SalesOrderSetGet.getTranType());

            HashMap<String, String> hashValue = new HashMap<String, String>();
            hashValue.put("CompanyCode", cmpnyCode);
            hashValue.put("ConsignmentNo", sosno);
            hashValue.put("TranType",SalesOrderSetGet.getTranType());
            hashValue.put("User", user);



            String jsonString = WebServiceClass.parameterService(hashValue,
                    "fncDeleteConsignment");

            try {

                jsonResponse = new JSONObject(jsonString);
                jsonMainNode = jsonResponse.optJSONArray("JsonArray");

                int lengthJsonArr = jsonMainNode.length();

                for (int i = 0; i < lengthJsonArr; i++) {

                    JSONObject jsonChildNode;
                    jsonChildNode = jsonMainNode.getJSONObject(i);
                    deleteResult = jsonChildNode.optString("Result").toString();
                    Log.d("deleteResult","-->"+deleteResult);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (deleteResult.matches("True")) {

                //new AsyncCallWSSOHeader().execute();
                new LoadConsignmentHeader().execute();

                Toast.makeText(ConsignmentHeader.this, "Delete Successfully",
                        Toast.LENGTH_LONG).show();

            } else {

                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(deliveryO_parent, true);

                Toast.makeText(ConsignmentHeader.this, "Failed",
                        Toast.LENGTH_LONG).show();

            }

        }
    }

    public class AsyncCallWSSalesOrder extends AsyncTask<Void, Void, Void> {
        String  tran_type;
        public AsyncCallWSSalesOrder(String tran_type) {
            this.tran_type=tran_type;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            al.clear();

            spinnerLayout = new LinearLayout(ConsignmentHeader.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            ConsignmentHeader.this.addContentView(spinnerLayout,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(deliveryO_parent, false);
            progressBar = new ProgressBar(ConsignmentHeader.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);
            boolean check= isNetworkConnected();
            if(check == false){
                 finish();
            }
        }
        @Override
        protected Void doInBackground(Void... arg0) {

            String companyCode = SupplierSetterGetter.getCompanyCode();

            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("CompanyCode", companyCode);
            hm.put("VanCode", select_van);
            hm.put("TranType",tran_type);


            al = SalesOrderWebService.getWholeCustomer(hm, "fncGetCustomer");
            serverdate = DateWebservice.getDateService("fncGetServerDate");

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (serverdate != null) {
//				starteditTextDate.setText(DateTime.date(serverdate));
//				endeditTextDate.setText(serverdate);

                starteditTextDate.setText("");  // added new
                endeditTextDate.setText(serverdate);

                if(SalesOrderSetGet.getSchedulingType()!=null && !SalesOrderSetGet.getSchedulingType().isEmpty()){
                    if(SalesOrderSetGet.getSchedulingType().matches("CG")){
//                        new LoadDOHeaderScheduled("0").execute();
                    }else{
                        new LoadConsignmentHeader().execute();
                    }
                }else{
                    new LoadConsignmentHeader().execute();
                }
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void alertDialogSearch() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(
                ConsignmentHeader.this);
        final EditText editText = new EditText(ConsignmentHeader.this);
        final ListView listview = new ListView(ConsignmentHeader.this);
        LinearLayout layout = new LinearLayout(ConsignmentHeader.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        myDialog.setTitle("Customer");
        editText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.search, 0,
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

        arrayAdapterSupp = new CustomAlertAdapterSupp(ConsignmentHeader.this,
                al);
        listview.setAdapter(arrayAdapterSupp);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub
                myalertDialog.dismiss();
                getArraylsit = arrayAdapterSupp.getArrayList();
                HashMap<String, String> datavalue = getArraylsit.get(position);
                Set<Map.Entry<String, String>> keys = datavalue.entrySet();
                Iterator<Map.Entry<String, String>> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    @SuppressWarnings("rawtypes")
                    Map.Entry mapEntry = iterator.next();
                    String keyValues = (String) mapEntry.getKey();
                    String name = (String) mapEntry.getValue();

                    edCustomerCode.setText(keyValues);
                    sl_namefield.setText(name);
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
                            sl_namefield.setText("");
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
                        ConsignmentHeader.this, searchResults);
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

    public class AsyncCallWSSearchCustCode extends AsyncTask<Void, Void, Void> {
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            spinnerLayout = new LinearLayout(ConsignmentHeader.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            ConsignmentHeader.this.addContentView(spinnerLayout,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(deliveryO_parent, false);
            progressBar = new ProgressBar(ConsignmentHeader.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);

            uniqueAssignDateArr.clear();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String tranType = SalesOrderSetGet.getTranType();
            Log.d("searchtranType",tranType);
            searchCstCdArr = SalesOrderWebService.SearchConsignment(cuscode,
                    sDate, eDate,select_van,loccode, "fncGetConsignmentHeader",tranType);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (!searchCstCdArr.isEmpty()) {
                try {
                    searchCustCode();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                searchCstCdArr.clear();
                try {
                    searchCustCode();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Toast.makeText(ConsignmentHeader.this, "No matches found",
                        Toast.LENGTH_SHORT).show();
            }

            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(deliveryO_parent, true);
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

    public void searchCustCode() throws ParseException {
        doadapter = new DOHeaderAdapter(ConsignmentHeader.this,
                R.layout.delivery_list_item, null, searchCstCdArr, uniqueAssignDateArr,SalesOrderSetGet.getTranType());
        do_lv.setAdapter(doadapter);
    }
    /**************DOHeader Call ********************/
    public class LoadConsignmentHeader extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            list.clear();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

                tran_type = SalesOrderSetGet.getTranType();

                Log.d("getTranType","-->"+tran_type);

                if (tran_type.matches("COI")) {

                    Log.d("FromDate", "" + sDate);
                    Log.d("ToDate", "" + eDate);
                    Log.d("VanCode", "" + select_van);
                    Log.d("LocationCode", "-->" + loccode);
                    Log.d("TranType", "--->" + SalesOrderSetGet.getTranType());

                    list = SalesOrderWebService.SearchIOCustCode(
                            cuscode, sDate, eDate, "0", select_van,loccode,
                            "fncGetConsignmentInvoiceHeader");
                } else if(tran_type.matches("COR")||tran_type.matches("O")){
                    try {
                    String fromdate = DateTime.date(serverdate);
                    hashValue.put("CompanyCode", cmpnyCode);
                    hashValue.put("FromDate", fromdate);
                    hashValue.put("ToDate", serverdate);
                    hashValue.put("VanCode", select_van);
                    hashValue.put("LocationCode", loccode);
                    hashValue.put("TranType", SalesOrderSetGet.getTranType());

                    Log.d("FromDate", "" + fromdate);
                    Log.d("ToDate", "" + serverdate);
                    Log.d("VanCode", "" + select_van);
                    Log.d("LocationCode", "-->" + loccode);
                    Log.d("TranType", "--->" + SalesOrderSetGet.getTranType());
                    // Start
                    jsonString = SalesOrderWebService.getSODetail(
                            hashValue, webMethName);
                    Log.d("jsonString ", "" + jsonString);
                    Cursor cursor = SOTDatabase.getConsignmentStock();
                    Log.d("cursorValues", "-->" + cursor.getCount());

                    jsonResponse = new JSONObject(jsonString);
                    jsonMainNode = jsonResponse.optJSONArray("SODetails");

                    int length = jsonMainNode.length();

                    for (int i = 0; i < length; i++) {

                        JSONObject jsonChildNode = jsonMainNode
                                .getJSONObject(i);
                        // int sno=1+i;
                        String ccDno = jsonChildNode.optString("ConsignmentNo")
                                .toString();
//                    String ccSno = jsonChildNode.optString("SoNo")
//                            .toString();
                        String ccDate = jsonChildNode.optString("ConsignmentDate")
                                .toString();
                        String customerCode = jsonChildNode.optString(
                                "CustomerCode").toString();
                        String customerName = jsonChildNode.optString(
                                "CustomerName").toString();
                        String amount = jsonChildNode.optString("NetTotal")
                                .toString();
//                    String bal_amt = jsonChildNode.optString("BalanceAmount").toString();
//                    String paidAmount =jsonChildNode.optString("PaidAmount").toString();
                        String DelCustomerName = jsonChildNode.optString("DelCustomerName")
                                .toString();
                        String gotSignatureOnDo = jsonChildNode.optString("GotSignatureOnDO")
                                .toString();

                        Log.d("gotSignatureOnDo", "" + gotSignatureOnDo);

                        SO so = new SO();
                        so.setSno(ccDno);
                        so.setSoNo("0");
                        so.setCustomerCode(customerCode);
                        so.setCustomerName(customerName);
                        so.setNettotal(amount);
                        so.setDelCustomerName(DelCustomerName);
                        //so.setGotSignatureOnDO(gotSignatureOnDo);
                        if (gotSignatureOnDo != null && !gotSignatureOnDo.isEmpty()) {
                            so.setGotSignatureOnDO(gotSignatureOnDo);
                        } else {
                            so.setGotSignatureOnDO("False");
                        }
                        SO.setNetTotal(amount);
                        if (ccDate != null) {
                            StringTokenizer tokens = new StringTokenizer(ccDate, " ");
                            String date = tokens.nextToken();
                            so.setDate(date);
                        } else {
                            so.setDate(ccDate);
                        }
                        list.add(so);
                    }
                    // End
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            tran_type = SalesOrderSetGet.getTranType();
            if(!list.isEmpty()) {
                try {
                    headerCustCode();
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else{
                list.clear();
                try {
                    headerCustCode();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Toast.makeText(ConsignmentHeader.this, "No matches found",
                        Toast.LENGTH_SHORT).show();
            }

            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(deliveryO_parent, true);

        }

    }
    /**************DOHeaderScheduled  Call ********************/
//    public class LoadDOHeaderScheduled extends AsyncTask<Void, Void, Void> {
//        String type = "";
//        ArrayList<String> tempArr = new ArrayList<String>();
//        public LoadDOHeaderScheduled(String gotsignaturetype){
//            type = gotsignaturetype;
//        }
//        @Override
//        protected void onPreExecute() {
//            list.clear();
//            uniqueAssignDateArr.clear();
//        }
//
//        @Override
//        protected Void doInBackground(Void... arg0) {
//            try {
//                String dateAssign = endeditTextDate.getText().toString();
//                hashValue.put("CompanyCode", cmpnyCode);
//                hashValue.put("AssignUser", user);
//                hashValue.put("AssignDate",dateAssign);
//                hashValue.put("GotSignatureOnDO",type);
//
//                // Start
//                jsonString = SalesOrderWebService.getSODetail(
//                        hashValue, webMethName1);
//                Log.d("jsonString ",""+jsonString);
//
//                jsonResponse = new JSONObject(jsonString);
//                jsonMainNode = jsonResponse.optJSONArray("SODetails");
//
//                int length = jsonMainNode.length();
//
//                for (int i = 0; i < length; i++) {
//
//                    JSONObject jsonChildNode = jsonMainNode
//                            .getJSONObject(i);
//                    // int sno=1+i;
//                    String ccDno = jsonChildNode.optString("DoNo")
//                            .toString();
//                    String ccSno = jsonChildNode.optString("SoNo")
//                            .toString();
//                    String ccDate = jsonChildNode.optString("DoDate")
//                            .toString();
//                    String customerCode = jsonChildNode.optString(
//                            "CustomerCode").toString();
//                    String customerName = jsonChildNode.optString(
//                            "CustomerName").toString();
//                    String amount = jsonChildNode.optString("NetTotal")
//                            .toString();
//                    String assignUser = jsonChildNode.optString("AssignUser")
//                            .toString();
//                    String assignDate = jsonChildNode.optString("AssignDate")
//                            .toString();
//                    String gotSignatureOnDo = jsonChildNode.optString("GotSignatureOnDO")
//                            .toString();
//                    String DelCustomerName = jsonChildNode.optString("DelCustomerName")
//                            .toString();
//
//                    SO so = new SO();
//                    so.setSno(ccDno);
//                    so.setSoNo(ccSno);
//                    so.setCustomerCode(customerCode);
//                    so.setCustomerName(customerName);
//                    so.setNettotal(amount);
//
//                    so.setAssignUser(assignUser);
//
//                    if(gotSignatureOnDo!=null&& !gotSignatureOnDo.isEmpty()){
//                        so.setGotSignatureOnDO(gotSignatureOnDo);
//                    }else{
//                        so.setGotSignatureOnDO("False");
//                    }
//                    so.setDelCustomerName(DelCustomerName);
//                    if (ccDate != null) {
//                        StringTokenizer tokens = new StringTokenizer(ccDate, " ");
//                        String date = tokens.nextToken();
//                        so.setDate(date);
//                    } else {
//                        so.setDate(ccDate);
//                    }
//                    list.add(so);
//
//                    if (assignDate != null && !assignDate.isEmpty()) {
//                        StringTokenizer tokens = new StringTokenizer(assignDate, " ");
//                        String date = tokens.nextToken();
//                        so.setAssignDate(date);
//
//                    } else {
//                        so.setAssignDate(assignDate);
//                    }
//
//                }
//                // End
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            if (ConsignmentHeader.this != null) {
//                try {
//                    if(list.size()>0){
//                        loadScheduleData();
//                    }
//
//                    headerCustCode();
//                } catch (ParseException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                progressBar.setVisibility(View.GONE);
//                spinnerLayout.setVisibility(View.GONE);
//                enableViews(deliveryO_parent, true);
//
//            }
//        }
//    }
    private void loadScheduleData(){
        try {

            final DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Collections.sort(list, new Comparator<SO>() {
                @Override
                public int compare(SO so1, SO so2) {
                    Date date1=null,date2=null;
                    try {
                        date1 = sdf.parse(so1.getAssignDate());
                        date2 = sdf.parse(so2.getAssignDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return date2.compareTo(date1);
                }
            });

            List<String> uniqueDateArr = new ArrayList<String>();

            uniqueDateArr.add(list.get(0).getAssignDate());
            uniqueAssignDateArr.add(list.get(0).getAssignDate());
            System.out.println("list "+list);

            System.out.println("uniqueDate "+uniqueDateArr.size());
            System.out.println("uniqueDate1 "+uniqueAssignDateArr.size());
            int i = 0;
            for (SO so : list) {
                boolean flag = false;
                for (String uniqueDate : uniqueDateArr) {
                    if (so.getAssignDate().equals(uniqueDate)) {
                        i++;
                        flag = true;
                    }
                }
                if(!flag){
                    uniqueDateArr.add(so.getAssignDate());
                    uniqueAssignDateArr.add(so.getAssignDate());
                }else{
                    if(i!=1){
                        uniqueAssignDateArr.add("0");
                    }
                }


            }
            System.out.println("uniqueDate1 new"+uniqueAssignDateArr.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startProgressBar(){
        spinnerLayout = new LinearLayout(ConsignmentHeader.this);
        spinnerLayout.setGravity(Gravity.CENTER);
        ConsignmentHeader.this.addContentView(spinnerLayout,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT));
        spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
        enableViews(deliveryO_parent, false);
        progressBar = new ProgressBar(ConsignmentHeader.this);
        progressBar.setProgress(android.R.attr.progressBarStyle);
        progressBar.setIndeterminateDrawable(getResources().getDrawable(
                drawable.greenprogress));
        spinnerLayout.addView(progressBar);
    }

    public void headerCustCode() throws ParseException {

        // doadapter = new DOAdapter(DeliveryOrderHeader.this, list);
        Log.d("searchCstCdArrSize","-->"+list.size()+"tran_type :"+tran_type);
        if (ConsignmentHeader.this != null) {
            doadapter = new DOHeaderAdapter(ConsignmentHeader.this,
                    R.layout.delivery_list_item, null, list, uniqueAssignDateArr,SalesOrderSetGet.getTranType());
            do_lv.setAdapter(doadapter);
        }else{
            list.clear();
            Toast.makeText(getApplicationContext(),"No data found!!",Toast.LENGTH_LONG).show();
        }
    }

    public void clearSetterGetter() {
        SalesOrderSetGet.setHeader_flag("ConsignmentHeader");
        SalesOrderSetGet.setCustomercode("");
        SalesOrderSetGet.setCustomername("");
        SalesOrderSetGet.setSaleorderdate("");
        SalesOrderSetGet.setCurrencycode("");
        SalesOrderSetGet.setCurrencyname("");
        SalesOrderSetGet.setCurrencyrate("");
        SalesOrderSetGet.setRemarks("");
        SOTDatabase.deleteBarcode();
        SOTDatabase.init(ConsignmentHeader.this);
        SOTDatabase.deleteAllProduct();
        SOTDatabase.deleteBillDisc();
        SOTDatabase.deleteallbatch();
        SOTDatabase.deleteImage();
        SOTDatabase.deleteSODetailQuantity();
        SOTDatabase.deleteallConsignment();
        ConvertToSetterGetter.setSoNo("");
        ConvertToSetterGetter.setEdit_do_no("");
    }

    private class AsyncPrintCall extends
            AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            product.clear();
            productdet.clear();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... arg0) {
            String tranType =SalesOrderSetGet.getTranType();
            Log.d("gettranType","-->"+tran_type +"sosno :"+sosno);
            String showcartonloose = SalesOrderSetGet
                    .getCartonpriceflag();
//            String showcartonloose = "0";
            Log.d("showcartonloose","-->"+showcartonloose);

            String cmpnyCode = SupplierSetterGetter.getCompanyCode();
            hashValue.put("CompanyCode", cmpnyCode);
            hashValue.put("ConsignmentNo", sosno);
            hashValue.put("TranType",tran_type);

            if (showcartonloose.equalsIgnoreCase("1")) {
                jsonString = SalesOrderWebService.getSODetail(
                        hashValue, "fncGetConsignmentInvoiceDetailWithCarton");
            } else {
                jsonString = SalesOrderWebService.getSODetail(
                        hashValue, "fncGetConsignmentDetail");
            }
            jsonStr = SalesOrderWebService.getSODetail(hashValue,
                    "fncGetConsignmentHeaderByConsignmentNo");

            Log.d("jsonString", jsonString);
            Log.d("jsonStr ", jsonStr);
            try {
                SalesOrderWebService.getCustomerTax(cusCode, "fncGetCustomer");

                jsonResponse = new JSONObject(jsonString);
                jsonMainNode = jsonResponse.optJSONArray("SODetails");

                jsonResp = new JSONObject(jsonStr);
                jsonSecNode = jsonResp.optJSONArray("SODetails");

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /*********** Process each JSON Node ************/
            int lengthJsonArr = jsonMainNode.length();
            if (showcartonloose.equalsIgnoreCase("1")) {
                for (int i = 0; i < lengthJsonArr; i++) {
                    /****** Get Object for each JSON node. ***********/
                    ProductDetails productdetail = new ProductDetails();
                    try {
                        JSONObject jsonChildNode = jsonMainNode
                                .getJSONObject(i);
                        String transType = jsonChildNode.optString("TranType")
                                .toString();
                        int s = i + 1;
                        productdetail.setSno(String.valueOf(s));
                        String slNo = jsonChildNode.optString("slNo")
                                .toString();
                        String productCode = jsonChildNode.optString(
                                "ProductCode").toString();
                        productdetail.setItemcode(productCode);

                        productdetail.setDescription(jsonChildNode.optString(
                                "ProductName").toString());
                        productdetail.setUOMCode(jsonChildNode.optString(
                                "UOMCode").toString());

                        String uomCode = jsonChildNode.optString("UOMCode")
                                .toString();

                        if (uomCode != null && !uomCode.isEmpty()) {

                        } else {
                            uomCode = "";
                        }

                        if (uomCode.matches("null")) {
                            uomCode = "";
                        }

                        Log.d("uomCode", "u " + uomCode);

                        if (transType.equalsIgnoreCase("Ctn")) {
                            String cQty = jsonChildNode.optString("CQty")
                                    .toString();

                            String cPrice = jsonChildNode.optString(
                                    "CartonPrice").toString();

                            Log.d("cPrice", "-->" + cPrice);

                            if (cQty != null && !cQty.isEmpty()
                                    && cPrice != null && !cPrice.isEmpty()) {
                                productdetail.setQty(cQty.split("\\.")[0]);
                                productdetail.setPrice(twoDecimalPoint(Double
                                        .valueOf(cPrice)));
                                double cqty = Double.valueOf(cQty);
                                double cprice = Double.valueOf(cPrice);
                                double total = cqty * cprice;
                                String tot = twoDecimalPoint(total);
                                productdetail.setTotal(tot);
                            } else {
                                productdetail.setQty("0");
                                productdetail.setPrice("0.00");
                                productdetail.setTotal("0.00");
                            }

                        } else if (transType.equalsIgnoreCase("Loose")) {

                            String lQty = jsonChildNode.optString("LQty")
                                    .toString();
                            String lPrice = jsonChildNode.optString("Price")
                                    .toString();

                            Log.d("lPrice", "--->" + lPrice);

                            if (lQty != null && !lQty.isEmpty()
                                    && lPrice != null && !lPrice.isEmpty()) {
                                productdetail.setQty(lQty.split("\\.")[0] + " "
                                        + uomCode);
                                productdetail.setPrice(twoDecimalPoint(Double
                                        .valueOf(lPrice)));
                                double lqty = Double.valueOf(lQty);
                                double lprice = Double.valueOf(lPrice);
                                double total = lqty * lprice;
                                String tot = twoDecimalPoint(total);
                                productdetail.setTotal(tot);
                            } else {
                                productdetail.setQty("0" + " " + uomCode);
                                productdetail.setPrice("0.00");
                                productdetail.setTotal("0.00");
                            }

                        } else if (transType.equalsIgnoreCase("FOC")) {
                            String focQty = jsonChildNode.optString("FOCQty")
                                    .toString();
                            if (focQty != null && !focQty.isEmpty()) {
                                productdetail.setQty(focQty.split("\\.")[0]);
                            } else {
                                productdetail.setQty("0" + " " + uomCode);
                            }
                            productdetail.setPrice("0.00");
                            productdetail.setTotal("0.00");

                        } else if (transType.equalsIgnoreCase("Exc")) {
                            String excQty = jsonChildNode.optString(
                                    "ExchangeQty").toString();
                            if (excQty != null && !excQty.isEmpty()) {
                                productdetail.setQty(excQty.split("\\.")[0]);
                            } else {
                                productdetail.setQty("0" + " " + uomCode);
                            }
                            productdetail.setPrice("0.00");
                            productdetail.setTotal("0.00");

                        }
                        productdetail.setTax(jsonChildNode.optString("Tax").toString());
                        productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
                        productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
                        productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
                        String consignmentNo = jsonChildNode.optString("ConsignmentNo").toString();
                        Log.d("consignmentNoCheck",""+consignmentNo);
                        productdetail.setConsignmentNumber(consignmentNo);
                        product.add(productdetail);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }else{
                /******** Print qty and price *********/
                for (int i = 0; i < lengthJsonArr; i++) {
                    /****** Get Object for each JSON node. ***********/
                    JSONObject jsonChildNode;
                    String pricevalue;
                    ProductDetails productdetail = new ProductDetails();
                    try {
                        jsonChildNode = jsonMainNode.getJSONObject(i);
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

                        pricevalue = jsonChildNode.optString("Price")
                                .toString();
                        Log.d("pricevalue", "--->" + pricevalue);

                        if(pricevalue.matches("0.000")) {
                            pricevalue = jsonChildNode.optString("CartonPrice")
                                    .toString();
                        }
                        String totalvalve = jsonChildNode.optString("SubTotal")
                                .toString();
                        productdetail.setFocqty(jsonChildNode
                                .optDouble("FOCQty"));

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

                        Log.d("totalvalue&price","->"+pricevalue+totalvalve);


                        productdetail.setTax(jsonChildNode.optString("Tax").toString());
                        productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
                        productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
                        productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
                        String consignmentNo = jsonChildNode.optString("ConsignmentNo").toString();
                        Log.d("consignmentNoChecks",""+consignmentNo);
                        productdetail.setConsignmentNumber(consignmentNo);
                        product.add(productdetail);

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            }
            /*********** Process each JSON Node ************/
            int lengJsonArr = jsonSecNode.length();
            for (int i = 0; i < lengJsonArr; i++) {
                /****** Get Object for each JSON node. ***********/
                JSONObject jsonChildNode;
                ProductDetails productdetail = new ProductDetails();
                try {
                    jsonChildNode = jsonSecNode.getJSONObject(i);
                    productdetail.setItemdisc("0.00");
                    productdetail.setBilldisc(jsonChildNode.optString(
                            "BillDiscountPercentage").toString());
                    productdetail.setSubtotal(jsonChildNode.optString(
                            "SubTotal").toString());
                    productdetail.setTax(jsonChildNode.optString("Tax")
                            .toString());
                    productdetail.setNettot(jsonChildNode.optString("NetTotal")
                            .toString());
                    productdetail.setTotaloutstanding(jsonChildNode.optString(
                            "TotalBalance").toString());
                    durationDays = jsonChildNode.optString(
                            "DurationInDays").toString();
                    Log.d("durationInDayscheck",durationDays);
                    productdet.add(productdetail);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            hashValue.clear();
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (printid == R.id.printer) {
                try {
                    printid = 0;
                    print(tran_type);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                tran_type  = SalesOrderSetGet.getTranType();
                if(tran_type.matches("COR")||tran_type.matches("O")) {
                    helper.dismissProgressDialog();
                    Log.d("tran_type","-->"+tran_type);
                    Intent i = new Intent(ConsignmentHeader.this,
                            CommonPreviewPrint.class);
                    if (tran_type.matches("COR")) {
                        i.putExtra("title", "Consignment Return");
                    } else {
                        i.putExtra("title", "Consignment");
                    }
                    i.putExtra("No", sosno);
                    i.putExtra("Date", sodate);
                    i.putExtra("cus_remarks", "");
                    i.putExtra("customerCode", socustomercode);
                    i.putExtra("customerName", socustomername);
                    i.putExtra("trantype",tran_type);
                    i.putExtra("duration",durationDays);
                    i.putExtra("Invoicetype", "Consignment");
                    PreviewPojo.setProducts(product);
                    PreviewPojo.setProductsDetails(productdet);
                    startActivity(i);
                }else{
                    Log.d("InvoicePrintPreview","-->"+tran_type);
                    Intent i = new Intent(ConsignmentHeader.this,
                            InvoicePrintPreview.class);
                    i.putExtra("invNo", sosno);
                    i.putExtra("invDate", sodate);
                    i.putExtra("customerCode", socustomercode);
                    i.putExtra("customerName", socustomername);
                    i.putExtra("sort", sortproduct);
                    i.putExtra("gnrlStngs", "N");
                    i.putExtra("tranType","COI");
                    i.putExtra("Invoicetype", "Invoice");
                    i.putExtra("duration",durationDays);
                    i.putExtra("dono","");
                    PreviewPojo.setProducts(product);
                    PreviewPojo.setProductsDetails(productdet);
                    startActivity(i);
                }
            }
        }

    }

    /*private void print() throws IOException {

        helper.dismissProgressDialog();
        String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        try {
            Printer printer = new Printer(DeliveryOrderHeader.this, macaddress);
            if (doadapter.getSelectedPosition() != -1) {
                SO so = doadapter.getItem(doadapter.getSelectedPosition());
                // helper.showProgressDialog(R.string.generating_do);
                sosno = so.getSno().toString();
                sodate = so.getDate().toString();
                socustomercode = so.getCustomerCode().toString();
                soamount = so.getNettotal().toString();
                for (HashMap<String, String> hashMap : al) {
                    for (String key : hashMap.keySet()) {
                        if (key.equals(socustomercode)) {
                            System.out.println(hashMap.get(key));
                            socustomername = hashMap.get(key);
                        }
                    }
                }
                // printer.printReport(sosno,sodate,socustomercode,soamount,sostatus);
                printer.printDeliveryOrder(sosno, sodate, socustomercode,
                        socustomername, product, productdet, 1);
            }
        } catch (IllegalArgumentException e) {
            helper.showLongToast(R.string.error_configure_printer);
        }
    }*/
    private void print(String tran_type) throws IOException {
        Log.d("print-->","print()"+tran_type);
        String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
        try {
                if (doadapter.getSelectedPosition() != -1) {
                    SO so = doadapter.getItem(doadapter.getSelectedPosition());
                    // helper.showProgressDialog(R.string.generating_do);
                    sosno = so.getSno().toString();
                    sodate = so.getDate().toString();
                    socustomercode = so.getCustomerCode().toString();
                    soamount = so.getNettotal().toString();
                    for (HashMap<String, String> hashMap : al) {
                        for (String key : hashMap.keySet()) {
                            if (key.equals(socustomercode)) {
                                System.out.println(hashMap.get(key));
                                socustomername = hashMap.get(key);
                            }
                        }
                    }
                }

            if (printertype.matches("Zebra iMZ320")) {
                Log.d("printertype-->","print()-->" +printertype);
                helper.dismissProgressDialog();
                Printer printer = new Printer(ConsignmentHeader.this, macaddress);
                if(tran_type.matches("COR")||tran_type.matches("O")){
                    printer.printConsignmentOrder(sosno, sodate, socustomercode,
                            socustomername, product, productdet, 1, tran_type,durationDays);
                }else{
                    printer.printInvoice(sosno, sodate, socustomercode, socustomername,
                            product, productdet, printsortHeader, "N", 1,
                            product_batchArr, footerArr,"",tran_type,durationDays,"");
                }

            }else if(printertype.matches("Zebra iMZ320 4 Inch")){
                Log.d("printertype-->","print()-->" +printertype);
                helper.dismissProgressDialog();
                PrinterZPL printerZPL = new PrinterZPL(ConsignmentHeader.this, macaddress);
                printerZPL.printConsignmentOrder(sosno, sodate, socustomercode,
                        socustomername, product, productdet, 1);
            }
            else if(printertype.matches("3 Inch Bluetooth Generic")){
                final CubePrint print = new CubePrint(ConsignmentHeader.this,macaddress);
                print.initGenericPrinter();
                print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
                    @Override
                    public void initCompleted() {
                        print.printConsignment(sosno, sodate, socustomercode,
                                socustomername, product, productdet, 1);
                        print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
                            @Override
                            public void onCompleted() {
                                helper.dismissProgressDialog();
                                helper.showLongToast(R.string.printed_successfully);
                            }
                        });
                    }
                });
            }
        } catch (IllegalArgumentException e) {
            helper.showLongToast(R.string.error_configure_printer);
        }
    }
    private class AsyncCallWSDODetail extends AsyncTask<Void, Void, Void> {

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            // getActionBar().setHomeButtonEnabled(false);

            spinnerLayout = new LinearLayout(ConsignmentHeader.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            ConsignmentHeader.this.addContentView(spinnerLayout,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(deliveryO_parent, false);
            progressBar = new ProgressBar(ConsignmentHeader.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            doDetailsArr = SalesOrderWebService.getConsignmentDetails(sosno,SalesOrderSetGet.getTranType(),
                    "fncGetConsignmentDetail");
            doHeaderArr = SalesOrderWebService.getConsignmentHeader(sosno,SalesOrderSetGet.getTranType(),
                    "fncGetConsignmentHeaderByConsignmentNo");
            try {
                SalesOrderWebService.getCustomerTax(cusCode, "fncGetCustomer");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            boolean remarks = false;
            if (doDetailsArr != null) {
                for (int i = 0; i < doDetailsArr.size(); i++) {
                    String ItemRemarks = doDetailsArr.get(i).get("ItemRemarks");

                    Log.d("ItemRemarks", "re" + ItemRemarks);

                    if (!ItemRemarks.matches("")) {
                        Log.d("Itemremarks", "not null");
                        remarks = true;
                        break;
                    }
                }

            }

            if (remarks == true) {
                initiatePopupWindow();
            } else {

                Intent i = new Intent(ConsignmentHeader.this,
                        InvoiceSummary.class);
                i.putExtra("DODetails", doDetailsArr);
                i.putExtra("DOHeader", doHeaderArr);
                startActivity(i);
                SalesOrderSetGet.setHeader_flag("ConsignmentHeader");
                ConsignmentHeader.this.finish();
            }
            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(deliveryO_parent, true);

        }
    }

    PopupWindow popupWindow;

    public void initiatePopupWindow() {

        myDialog = new AlertDialog.Builder(ConsignmentHeader.this);
        LayoutInflater li = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.popupwindow, null, false);
        myDialog.setView(v);

        LinearLayout popup_qty_layout = (LinearLayout) v
                .findViewById(R.id.popup_qty_layout);
        LinearLayout popup_cqty_layout = (LinearLayout) v
                .findViewById(R.id.popup_cqty_layout);
        LinearLayout popup_lqty_layout = (LinearLayout) v
                .findViewById(R.id.popup_lqty_layout);

        ListView lv_popup = (ListView) v.findViewById(R.id.popupList);

        PopWindowAdapter popupAdapter = new PopWindowAdapter(
                ConsignmentHeader.this, doDetailsArr);
        lv_popup.setAdapter(popupAdapter);

        myDialog.setNeutralButton(R.string.invoice_summary,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(ConsignmentHeader.this,
                                InvoiceSummary.class);
                        i.putExtra("DODetails", doDetailsArr);
                        i.putExtra("DOHeader", doHeaderArr);
                        startActivity(i);
                        SalesOrderSetGet.setHeader_flag("ConsignmentHeader");
                        ConsignmentHeader.this.finish();
                    }
                });

        if (!cartonOrLoose.matches("")) {
            if (cartonOrLoose.matches("1")) {
                popup_qty_layout.setVisibility(View.GONE);
                popup_cqty_layout.setVisibility(View.VISIBLE);
                popup_lqty_layout.setVisibility(View.VISIBLE);
            }
            if (cartonOrLoose.matches("0")) {
                popup_qty_layout.setVisibility(View.VISIBLE);
                popup_cqty_layout.setVisibility(View.GONE);
                popup_lqty_layout.setVisibility(View.GONE);
            }
        }

        myDialog.show();

    }

    private void printCallDialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.mipmap.ic_menu_print);
        alertDialog.setTitle("Save");
        alertDialog.setMessage("Do you want to save");
        LayoutInflater adbInflater = LayoutInflater.from(this);
        final View eulaLayout = adbInflater.inflate(R.layout.print_checkbox,
                null);

        mSignatureLayout = (LinearLayout) eulaLayout
                .findViewById(R.id.signatureLayout);
        mCameraLayout = (LinearLayout) eulaLayout
                .findViewById(R.id.cameraLayout);

        mChangeSignBtn = (ImageView) eulaLayout.findViewById(R.id.changeSign);
        mSignature = (ImageView) eulaLayout.findViewById(R.id.signature);

        sm_camera_iv = (ImageView) eulaLayout.findViewById(R.id.sm_camera_iv);
        prodphoto = (ImageView) eulaLayout.findViewById(R.id.prod_photo);

        CheckBox print = (CheckBox) eulaLayout.findViewById(R.id.checkbox);
        mCameraLayout.setVisibility(View.VISIBLE);

        print.setVisibility(View.GONE);

        mSignatureLayout.setVisibility(View.VISIBLE);

        loadSignatureImage();

        mChangeSignBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(ConsignmentHeader.this,
                        CaptureSignature.class);
                startActivityForResult(i, SIGNATURE_ACTIVITY);
            }
        });

        sm_camera_iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                CameraAction(PICK_FROM_CAMERA);
            }
        });

        alertDialog.setView(eulaLayout);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int indexSelected) {
                        printid = R.id.printer;
//							cursor = FWMSSettingsDatabase.getPrinter();
//							if (cursor.getCount() != 0) {
                        isDeliveryPrint = false;

                        if(sosno!=null&& !sosno.isEmpty()){

                            Log.d("Lat ", mLatitude +" Long " + mLongitude);
                            String signature_img = SOTDatabase.getSignatureImage();
                            //String camera_image = SOTDatabase.getProductImage();
                            mCurrentgetPath = Product.getHeaderpath();
                            //Log.d("pathfind",mCurrentgetPath);
                            String camera_image = ImagePathToBase64Str(mCurrentgetPath);
                            String signatureResult = SOTSummaryWebService.saveSignatureImage(
                                    sosno, "" + mLatitude, "" + mLongitude,
                                    signature_img, camera_image, "fncSaveInvoiceImages","CG","","" + "^" + "" + "^" + "");

                            Log.d("signatureResult-->", ""+signatureResult);

//									String signature_img = SOTDatabase.getSignatureImage();
//									String signatureResult =SOTSummaryWebService.saveSignatureImage(
//											sosno, "" + "", "" + "",
//											signature_img, "", "fncSaveInvoiceImages","DO","","");
//
//									Log.d("signatureResult-->", ""+signatureResult);

                            if(!signatureResult.matches("")){
                                Toast.makeText(ConsignmentHeader.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ConsignmentHeader.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }

//
//							} else {
//								Toast.makeText(DeliveryOrderHeader.this,
//										"Please Configure the printer", Toast.LENGTH_SHORT)
//										.show();
//							}

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

    public String ImagePathToBase64Str(String path) {
        Log.d("ImagePathToBase64Str",""+path);
        String base64Image ="";
        try {
            // Decode deal_image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 128;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            base64Image = BitMapToString(BitmapFactory.decodeFile(path, o2));
            return base64Image;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
    public String BitMapToString(Bitmap bitmap){
        Log.d("BitMapToString",""+bitmap);
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    private void loadSignatureImage(){
        try{
            String photo_img = SOTDatabase.getProductImage();
            String signature_img = SOTDatabase.getSignatureImage();

            if (photo_img != null && !photo_img.isEmpty()) {
                byte[] encodePhotoByte;

                byte[] encodePhotoByte1 = Base64.decode(photo_img,
                        Base64.DEFAULT);

                String s;
                try {
                    s = new String(encodePhotoByte1, "UTF-8");
                    encodePhotoByte = Base64.decode(s, Base64.DEFAULT);
                } catch (Exception e) {
                    encodePhotoByte = encodePhotoByte1;
                }

                Bitmap photo = BitmapFactory.decodeByteArray(encodePhotoByte, 0,encodePhotoByte.length);
                photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
                prodphoto.setImageBitmap(photo);
            }

            if (signature_img != null && !signature_img.isEmpty()) {
                byte[] encodeByte;

                byte[] encodeByte1 = Base64.decode(signature_img,
                        Base64.DEFAULT);

                String s;
                try {
                    s = new String(encodeByte1, "UTF-8");
                    encodeByte = Base64.decode(s, Base64.DEFAULT);
                } catch (Exception e) {
                    encodeByte = encodeByte1;
                }

                Bitmap sign = BitmapFactory.decodeByteArray(encodeByte, 0,encodeByte.length);
                sign = Bitmap.createScaledBitmap(sign, 240, 80, true);
                mSignature.setImageBitmap(sign);



            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//	   public void CameraAction() {
//			/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//			intent.putExtra("crop", "true");
//			intent.putExtra("aspectX", 0);
//			intent.putExtra("aspectY", 0);
//			intent.putExtra("outputX", 200);
//			intent.putExtra("outputY", 150);
//			try {
//				intent.putExtra("return-data", true);
//				startActivityForResult(intent, PICK_FROM_CAMERA);
//			} catch (ActivityNotFoundException e) {
//			}*/
//
//		   try {
//				 Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		         if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//		             startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
//		         }
//
//			} catch (ActivityNotFoundException e) {
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

    private void CameraAction(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;
        try {
            f = setUpPhotoFile();

            mCurrentPhotoPath = f.getAbsolutePath();


            Log.d("mCurrentPhotoPath", "dispatchTakePictureIntent--->"+mCurrentPhotoPath);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }
        startActivityForResult(takePictureIntent, actionCode);
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;
        String folder_main = "SFA";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir= new File(Environment.getExternalStorageDirectory()+ "/" + folder_main, "Image");
            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//			if (data != null) {
        switch (requestCode) {

            case SIGNATURE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    //Bundle extras = data.getExtras();
                    byte[] bytes = data.getByteArrayExtra("status");
                    if (bytes != null) {
                        //Bitmap photo = extras.getParcelable("status");

                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 240, 80, true);

                        mSignature.setImageBitmap(bitmap);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] bitMapData = stream.toByteArray();
                        String signature_image = Base64.encodeToString(
                                bitMapData, Base64.DEFAULT);
                        SOTDatabase.init(ConsignmentHeader.this);
                        Cursor ImgCursor = SOTDatabase.getImageCursor();
                        if (ImgCursor.getCount() > 0) {
                            String camera_image = SOTDatabase.getProductImage();
                            SOTDatabase.updateImage(1, signature_image,
                                    camera_image);
                        } else {
                            SOTDatabase.storeImage(1, signature_image, "");
                        }

                        Log.d("Signature Image", "Sig" + signature_image);
                    }
                }
                break;

            case PICK_FROM_CAMERA:
                if (requestCode == PICK_FROM_CAMERA) {
//						Bundle extras = data.getExtras();
//						if (extras != null) {
                    getRightAngleImage(mCurrentPhotoPath);
                    handleCameraPhoto(PICK_FROM_CAMERA);
//							Bitmap photo = extras.getParcelable("data");
//							photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
//							prodphoto.setImageBitmap(photo);
//
//							ByteArrayOutputStream stream = new ByteArrayOutputStream();
//							photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
//							byte[] bitMapData = stream.toByteArray();
//							String camera_image = Base64.encodeToString(bitMapData,
//									Base64.DEFAULT);
//							SOTDatabase.init(DeliveryOrderHeader.this);
//
//							Cursor ImgCursor = SOTDatabase.getImageCursor();
//							if (ImgCursor.getCount() > 0) {
//								String signature_image = SOTDatabase
//										.getSignatureImage();
//								SOTDatabase.updateImage(1, signature_image,
//										camera_image);
//							} else {
//								SOTDatabase.storeImage(1, "", camera_image);
//							}
//
//							Log.d("Camera Image", "cam" + camera_image);
                    //}
                }
                break;
        }
//			}
    }

    private void handleCameraPhoto(int actionCode) {
        Log.d("mCurrentPhotoPath", "mCurrentPhotoPath-->"+mCurrentPhotoPath);
        if (mCurrentPhotoPath != null) {

            switch (actionCode) {
                case PICK_FROM_CAMERA: {
                    Product.setHeaderpath(mCurrentPhotoPath);
                    decodeImagePathFile(mCurrentPhotoPath,prodphoto);
                    break;
                }
            }

            //	galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    public void decodeBase64File(String imageString,ImageView imageView) {
        try{
            byte[] encodeByte = Base64.decode(imageString, Base64.DEFAULT);

            /* There isn't enough memory to open up more than a couple camera photos */
            /* So pre-scale the target bitmap into which the file is decoded */

            /* Get the size of the ImageView */
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();

            /* Get the size of the image */
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            //BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,encodeByte.length, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            /* Figure out which way needs to be reduced less */
            int scaleFactor = 1;
            if ((targetW > 0) || (targetH > 0)) {
                scaleFactor = Math.min(photoW/targetW, photoH/targetH);
            }

            /* Set bitmap options to scale the image decode target */
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            /* Decode the JPEG file into a Bitmap */
            //	Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length, bmOptions);

//			Bitmap bitmaprst = rotate(bitmap,90);

            imageView.setImageBitmap(bitmap);

//			ByteArrayOutputStream stream = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//						byte[] bitMapData = stream.toByteArray();
//						String camera_image = Base64.encodeToString(bitMapData,
//								Base64.DEFAULT);
//						SOTDatabase.init(InvoiceHeader.this);
//
//						Cursor ImgCursor = SOTDatabase.getImageCursor();
//						if (ImgCursor.getCount() > 0) {
//							String signature_image = SOTDatabase
//									.getSignatureImage();
//							SOTDatabase.updateImage(1, signature_image,
//									camera_image);
//						} else {
//							SOTDatabase.storeImage(1, "", camera_image);
//						}
//						Log.d("Camera Image", "cam" + camera_image);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }
    }

    public void decodeImagePathFile(String imageString,ImageView imageView) {
        try {

            /* There isn't enough memory to open up more than a couple camera photos */
            /* So pre-scale the target bitmap into which the file is decoded */

            /* Get the size of the ImageView */
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();

            /* Get the size of the image */
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageString, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            /* Figure out which way needs to be reduced less */
            int scaleFactor = 1;
            if ((targetW > 0) || (targetH > 0)) {
                scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            }

            /* Set bitmap options to scale the image decode target */
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            /* Decode the JPEG file into a Bitmap */
            Bitmap bitmap = BitmapFactory.decodeFile(imageString, bmOptions);

//		Bitmap bitmaprst = rotate(bitmap,90);

            imageView.setImageBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitMapData = stream.toByteArray();
            String camera_image = Base64.encodeToString(bitMapData,
                    Base64.DEFAULT);
            SOTDatabase.init(ConsignmentHeader.this);

            Cursor ImgCursor = SOTDatabase.getImageCursor();
            if (ImgCursor.getCount() > 0) {
                Log.d("if","if");
                String signature_image = SOTDatabase
                        .getSignatureImage();
                SOTDatabase.updateImage(1, signature_image,
                        camera_image);
            } else {
                Log.d("else","else");
                SOTDatabase.storeImage(1, "", camera_image);
            }
            Log.d("Camera Image", "cam" + camera_image);

        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }

    }

    private String getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 90;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree,photoPath);

        } catch (Exception e) {
            e.printStackTrace();
            helper.dismissProgressView(deliveryO_parent);
        }

        return photoPath;
    }

    private String rotateImage(int degree, String imagePath){
        Log.d("degree", "-->"+degree);
        if(degree<=0){
            return imagePath;
        }
        try{
            Bitmap b= BitmapFactory.decodeFile(imagePath);
            Matrix matrix = new Matrix();
            if(b.getWidth()>b.getHeight()){
                matrix.setRotate(degree);
//				b.recycle();
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            }else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        }catch (Exception e){
            e.printStackTrace();
            helper.dismissProgressView(deliveryO_parent);
        }
        return imagePath;
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

    /** Location Start   **/

    public void getCurrentlocation() {

        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, 20000, 0, this);
                    Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
                        Location location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Log.d("location", "not null");
                            onLocationChanged(location);
                        }else{
                            Log.d("location", "null");
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, this);
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 20000, 0, this);
                    Log.d("GPS", "GPS Enabled");
                    if (locationManager != null) {
                        Location location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            Log.d("location", "not null");
                            onLocationChanged(location);
                        }else{
                            Log.d("location", "null");
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, this);
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

            Log.d("Lat ", mLatitude +" Long " + mLongitude);

            String cityName = "";
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;

            addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);

            if (addresses.size() > 0) {
                cityName = addresses.get(0).getAddressLine(0);
            }

            locationManager.removeUpdates(this);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    /** Location End    **/

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

    private class ShowAllLocation extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hashValue.clear();
            helper.showProgressView(deliveryO_parent);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                hashValue.put("CompanyCode", cmpnyCode);
                String jsonString = SalesOrderWebService.getSODetail(hashValue, "fncGetLocation");
                if (jsonString != null && !jsonString.isEmpty()) {
                    jsonResponse = new JSONObject(jsonString);
                    jsonMainNode = jsonResponse.optJSONArray("SODetails");
                    int lengJsonArray = jsonMainNode.length();
                    if (lengJsonArray > 0) {
                        for (int i = 0; i < lengJsonArray; i++) {
                            JSONObject jsonObject = jsonMainNode.getJSONObject(i);
                            String locationCode = jsonObject.getString("LocationCode");
                            String locationName = jsonObject.getString("LocationName");
                            HashMap<String, String> locationhm = new HashMap<String, String>();
                            locationhm.put(locationCode, locationName);
                            locationArrHm.add(locationhm);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("locationArrHm","-->"+locationArrHm.size());
            helper.dismissProgressView(deliveryO_parent);
            alertDialogLocation();
        }
    }
    public void alertDialogLocation() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(
                ConsignmentHeader.this);
        final EditText editText = new EditText(ConsignmentHeader.this);
        final ListView listview = new ListView(ConsignmentHeader.this);
        LinearLayout layout = new LinearLayout(ConsignmentHeader.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        myDialog.setTitle(getResources().getString(R.string.location));
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

        arrayAdapterSupp = new CustomAlertAdapterSupp(ConsignmentHeader.this,
                locationArrHm);
        listview.setAdapter(arrayAdapterSupp);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                myalertDialog.dismiss();
                getArraylsit = arrayAdapterSupp.getArrayList();
                HashMap<String, String> datavalue = getArraylsit.get(position);
                Set<Map.Entry<String, String>> keys = datavalue.entrySet();
                Iterator<Map.Entry<String, String>> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    @SuppressWarnings("rawtypes")
                    Map.Entry mapEntry = iterator.next();
                    loccode = (String) mapEntry.getKey();
                    String locationName = (String) mapEntry.getValue();
                    locationcode_filter.setText(locationName);

                    locationcode_filter.addTextChangedListener(new TextWatcher() {
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
                            textlength = locationcode_filter.getText().length();
                        }
                    });
                }
            }
        });

        searchResults = new ArrayList<HashMap<String, String>>(locationArrHm);
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
                for (int i = 0; i < locationArrHm.size(); i++) {
                    String supplierName = locationArrHm.get(i).toString();
                    if (textlength <= supplierName.length()) {
                        if (supplierName.toLowerCase().contains(
                                editText.getText().toString().toLowerCase()
                                        .trim()))
                            searchResults.add(locationArrHm.get(i));
                    }
                }

                arrayAdapterSupp = new CustomAlertAdapterSupp(
                        ConsignmentHeader.this, searchResults);
                listview.setAdapter(arrayAdapterSupp);
            }
        });
        myDialog.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        myalertDialog = myDialog.show();
    }

    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tax = df.format(d);
        return tax;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ConsignmentHeader.this, LandingActivity.class);
        startActivity(i);
        ConsignmentHeader.this.finish();
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
}

