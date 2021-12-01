package com.winapp.trackwithmap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.SFA.R;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.helper.DateTime;
import com.winapp.model.ScheduleDate;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.RoutePrintPreview;
import com.winapp.printer.UIHelper;
import com.winapp.sot.InvoiceHeader;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.ReceiptHeader;
import com.winapp.sot.SO;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.sotdetails.CustomerSetterGetter;
import com.winapp.util.CustomCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class DeliveryOrderNewHeader extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace,Constants {
    private SlidingMenu menu;
    ImageButton search,printer,addnew,im_date;
    EditText locationcode_filter;
    private Calendar taskdateCalendar,calendar;
    private ViewPager mViewPagerDate,mViewPagerTab;
    private ScheduleDateAdapter adapter;
    private String currentdate,flagname="All";
    private ArrayList<ScheduleDate> dates;
  //  private ArrayList<ScheduleDate> dates1;
    private SimpleDateFormat mDateFormat1, mDateFormat2;
    RecyclerView do_lv;
    LinearLayout tag_header,deliveryO_newparent;
    Button btcstmrsrch,all_tab,open_tab,closed_tab;
    private List<ScheduleDataNew> scheduleList,openArr,closeArr,settingsArr,routeArr;
    private ArrayList<ScheduleDataNew> LatLngArr;
    private ScheduleStatusAdapter recyclerAdapter;
    ProgressBar progressBar;
    LinearLayout spinnerLayout,username_layout;
    ArrayList<SO> list = new ArrayList<SO>();
    HashMap<String, String> hashValue = new HashMap<String, String>();
    JSONObject jsonResponse, jsonResp;
    JSONArray jsonMainNode, jsonSecNode;
    private static String webMethName = "fncGetRouteForDelivery",cmpnyCode="";
    String jsonString = null, jsonStr = null, custjsonStr = null,jsonSt,username;
    CustomCalendar customCalendar;
    ArrayList<ProductDetails> product;
    ArrayList<ProductDetails> productdet,footerArr;
    JSONObject custjsonResp, jsonRespfooter;
    JSONArray jsonSecNodefooter,custjsonMainNode;
    HashMap<String, String> hashVl = new HashMap<String, String>();
    private EditText assigndate;
    String postalcode,address1,invoiceno,invoicedate,cust_code,customername,address2,fulladdress,address3,destendpoint = "",countryname = "";
    private Geocoder geoCoder = null;
    private double destinationLatitudeValue = 0.0, destinationLongitudeValue = 0.0,latitude = 0.0,longitude = 0.0;
    private UIHelper helper;
    ArrayList<String> sort = new ArrayList<String>();
    private Button route_btn;
    private EditText receipt_userCode;
    private boolean receiptAll = false;
    private AlertDialog myalertDialog = null;
    CustomAlertAdapterSupp arrayAdapterSupp;
    ArrayList<HashMap<String, String>> searchResults;
    ArrayList<HashMap<String, String>> userArr = new ArrayList<HashMap<String, String>>();
    int textlength = 0;
    ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();

 // TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_delivery_order_new_header);
        getActionBar().setIcon(R.drawable.ic_menu);
        Mint.initAndStartSession(DeliveryOrderNewHeader.this, "29088aa0");
        setContentView(R.layout.activity_delivery_order_new_header);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        View customNav = LayoutInflater.from(this).inflate(
                R.layout.slidemenu_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        txt.setText("Delivery Order");
        search = (ImageButton) customNav.findViewById(R.id.search_img);
        im_date = (ImageButton) customNav.findViewById(R.id.date_image);
        printer = (ImageButton) customNav.findViewById(R.id.printer);
        addnew = (ImageButton) customNav.findViewById(R.id.custcode_img);
        locationcode_filter = (EditText) findViewById(R.id.locationcode_filter);

        all_tab = (Button) findViewById(R.id.all_tab);
        open_tab = (Button) findViewById(R.id.open_tab);
        closed_tab = (Button) findViewById(R.id.closed_tab);
        tag_header = (LinearLayout) findViewById(R.id.tag_header);
        deliveryO_newparent = (LinearLayout) findViewById(R.id.parent_layout);
        do_lv = (RecyclerView) findViewById(R.id.deliO_listView1);
        assigndate = (EditText) findViewById(R.id.date);
        route_btn = (Button) findViewById(R.id.route_btn);
        receipt_userCode = (EditText) findViewById(R.id.donew_userCode);
        username_layout = (LinearLayout) findViewById(R.id.username_layout);

        im_date.setVisibility(View.VISIBLE);
        addnew.setVisibility(View.GONE);
        search.setVisibility(View.VISIBLE);
        taskdateCalendar = Calendar.getInstance();
        calendar = Calendar.getInstance();
        scheduleList = new ArrayList<>();
        settingsArr = new ArrayList<>();
        openArr = new ArrayList<>();
        closeArr = new ArrayList<>();
        LatLngArr = new ArrayList<>();
        routeArr = new ArrayList<>();
        dates = new ArrayList<ScheduleDate>();

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
        mDateFormat1 = new SimpleDateFormat("dd/MMMM/yyyy", Locale.ENGLISH);
        mDateFormat2 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        do_lv.setLayoutManager(new LinearLayoutManager(DeliveryOrderNewHeader.this));
//        recyclerAdapter = new ScheduleStatusAdapter(DeliveryOrderNewHeader.this,scheduleList);
//        do_lv.setAdapter(recyclerAdapter);

        product = new ArrayList<ProductDetails>();
        productdet = new ArrayList<ProductDetails>();
        footerArr = new ArrayList<ProductDetails>();

        geoCoder = new Geocoder(DeliveryOrderNewHeader.this, Locale.getDefault());
        helper = new UIHelper(DeliveryOrderNewHeader.this);

        cmpnyCode = SupplierSetterGetter.getCompanyCode();

//        startProgressBar();
//        prepareScheduleData();
        username = SupplierSetterGetter.getUsername();
        receipt_userCode.setText(username);

        receiptAll = FormSetterGetter.isReceiptAll();

        if (receiptAll == true) {
            receipt_userCode.setFocusableInTouchMode(true);
            receipt_userCode.setBackgroundResource(R.drawable.edittext_bg);
            receipt_userCode.setCursorVisible(true);
        }

        mViewPagerDate = (ViewPager) findViewById(R.id.pager);
        String selectedDate = calendar.get(Calendar.DATE) + "/"
                + (calendar.get(Calendar.MONTH)+1) + "/"
                + calendar.get(Calendar.YEAR);
        Log.d("selectedDate",selectedDate);
        try {
            currentdate = mDateFormat1
                    .format(mDateFormat2.parse(selectedDate));
            Log.d("reformattedDate", "->" + currentdate);
           // initViewPager(reformattedServerDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        adapter = new ScheduleDateAdapter(DeliveryOrderNewHeader.this,getDatesmethod());
        int position = getCurrentDateSelection(currentdate);
        adapter.setCurrentItem(position);
        mViewPagerDate.setAdapter(adapter);
        if(position>0){
            mViewPagerDate.setCurrentItem(position-2);
        }
        onPagerItemClick(mViewPagerDate.getChildAt(position), position);
      //  mViewPagerDate.setAdapter(adapter);

        final DatePickerDialog.OnDateSetListener taskDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                taskdateCalendar.set(Calendar.YEAR, year);
                taskdateCalendar.set(Calendar.MONTH, monthOfYear);
                taskdateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
              //  taskDate();
              //  initViewPagerDateSlide();

               // calendar.setTime(new Date());
                String selectedDate = taskdateCalendar.get(Calendar.DATE) + "/"
                        + (taskdateCalendar.get(Calendar.MONTH)+6) + "/"
                        + taskdateCalendar.get(Calendar.YEAR);
                Log.d("selectedDate",selectedDate);
                try {
                    String reformattedServerDate = mDateFormat1
                            .format(mDateFormat2.parse(selectedDate));
                    Log.d("reformattedDate", "->" + reformattedServerDate);
                    initViewPager(reformattedServerDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

//        im_date.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (MotionEvent.ACTION_UP == event.getAction())
//                    new DatePickerDialog(DeliveryOrderNewHeader.this, taskDate,
//                            taskdateCalendar.get(Calendar.YEAR), taskdateCalendar
//                            .get(Calendar.MONTH), taskdateCalendar
//                            .get(Calendar.DAY_OF_MONTH)).show();
//                return false;
//            }
//        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) DeliveryOrderNewHeader.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

                if (username_layout.getVisibility() == View.VISIBLE) {
                    username_layout.setVisibility(View.GONE);
                    inputMethodManager.hideSoftInputFromWindow(
                            receipt_userCode.getWindowToken(), 0);
                } else {
                    username_layout.setVisibility(View.VISIBLE);
                }
            }
        });

        im_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customCalendar = new CustomCalendar(DeliveryOrderNewHeader.this);
                customCalendar.showCalendarView();
                boolean mstatus = customCalendar.showDialog();
                Log.d("mstatus", mstatus+"");
                if (mstatus == true) {
                    String sDate = customCalendar.getSelectDate();
                    DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
                    DateFormat outputFormat = new SimpleDateFormat("dd/MMM/yyyy",Locale.ENGLISH);
                    String inputDateStr = sDate;
                    Date date = null;
                    try {
                        date = inputFormat.parse(inputDateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String outputDateStr = outputFormat.format(date);
                    Log.d("reformattedDatechecknew",outputDateStr);
                    initViewPager(outputDateStr);
                } else {
                    Log.d("False", "-->" + "False");
                }
            }
        });

        receipt_userCode
                .setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
                        receipt_userCode) {
                    @Override
                    public boolean onDrawableClick() {

                        if (receiptAll == true) {
                            alertDialogUserSearch();
                        }

                        return true;
                    }
                });

       // initViewPagerAndTabs();

//        assigneddate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//            }
//        });

        registerForContextMenu(do_lv);


        all_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                all_tab.setBackgroundResource(R.mipmap.route_tab_select);
                open_tab.setBackgroundResource(R.mipmap.route_tab_unselect);
                closed_tab.setBackgroundResource(R.mipmap.route_tab_unselect);
                flagname="All";

              //  new LoadRouteHeader().execute();

                recyclerAdapter = new ScheduleStatusAdapter(DeliveryOrderNewHeader.this,scheduleList);
                do_lv.setAdapter(recyclerAdapter);
            }
        });

        open_tab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                all_tab.setBackgroundResource(R.mipmap.route_tab_unselect);
                open_tab.setBackgroundResource(R.mipmap.route_tab_select);
                closed_tab.setBackgroundResource(R.mipmap.route_tab_unselect);
                flagname="Open";

             //   new LoadRouteHeader().execute();
                recyclerAdapter = new ScheduleStatusAdapter(DeliveryOrderNewHeader.this,openArr);
                do_lv.setAdapter(recyclerAdapter);

//                scheduleList.clear();
//                startProgressBar();
//                recyclerAdapter = new ScheduleStatusAdapter(DeliveryOrderNewHeader.this,scheduleList);
//                do_lv.setAdapter(recyclerAdapter);
//                progressBar.setVisibility(View.GONE);
//                spinnerLayout.setVisibility(View.GONE);
//                enableViews(deliveryO_newparent, true);
//                recyclerAdapter.notifyDataSetChanged();
              //  prepareScheduleData();
            }

        });

        closed_tab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                all_tab.setBackgroundResource(R.mipmap.route_tab_unselect);
                open_tab.setBackgroundResource(R.mipmap.route_tab_unselect);
                closed_tab.setBackgroundResource(R.mipmap.route_tab_select);
                flagname="Closed";

               // new LoadRouteHeader().execute();

                recyclerAdapter = new ScheduleStatusAdapter(DeliveryOrderNewHeader.this,closeArr);
                do_lv.setAdapter(recyclerAdapter);

//                recyclerAdapter = new ScheduleStatusAdapter(DeliveryOrderNewHeader.this,scheduleList);
//                do_lv.setAdapter(recyclerAdapter);
//                startProgressBar();
//                prepareScheduleData();
            }

        });

        route_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("schedulelist",""+scheduleList.size());
                new GetLatLngArr().execute();

            }
        });
    }

    private DatePickerDialog createDialogWithoutDateField() {
        DatePickerDialog dpd = new DatePickerDialog(this, null, 2014, 1, 24);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                           // Log.d("dayPicker",dayPicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
        }
        return dpd;
    }

    private void initViewPager(String serverDate) {
       // mViewPagerDate = (ViewPager) findViewById(R.id.pager);
      //  mViewPagerDate.setVisibility(View.VISIBLE);
        adapter = new ScheduleDateAdapter(this, getDatesmethod());
        int position = getCurrentDateSelection(serverDate);
        adapter.setCurrentItem(position);
        mViewPagerDate.setAdapter(adapter);
        if(position>0){
            mViewPagerDate.setCurrentItem(position-2);
        }
       // if(mViewPagerDate.getVisibility()==View.INVISIBLE){
         //  mViewPagerDate.setVisibility(View.VISIBLE);
       // }
        // set intial position.
        onPagerItemClick(mViewPagerDate.getChildAt(position), position);
    }

    // Create ArrayList of these custom date objects to show in Activity
    private ArrayList<ScheduleDate> getDates() {
        Calendar calendar1 = Calendar.getInstance();/* new GregorianCalendar(); */
        Date pastDate = taskdateCalendar.getTime();

        calendar1.setTime(pastDate);
        calendar1.add(Calendar.MONTH, -6);
        // Date pastYear = calendar1.getTime();
        // System.out.format("future Year:  %s\n", pastYear);

        Calendar calendar2 = Calendar.getInstance();
        Date futureDate = calendar2.getTime();

        calendar2.setTime(futureDate);
        taskdateCalendar.add(Calendar.MONTH, +6);
        // Date futureYear = calendar2.getTime();
        // System.out.format("future Year:  %s\n", futureYear);

        for (Date date = calendar1.getTime(); calendar1.before(taskdateCalendar); calendar1
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
                    + (calendar1.get(Calendar.MONTH) - 6)
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
                    &&  month.substring(0, 3).matches(dates.get(i).getMonth().substring(0,3))
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

    public void onPagerItemClick(View view, int index) {
        Log.d("datesArr",""+dates.size());
        System.out.println("" + index);
        ScheduleDate merchandiseScheduleDate = this.dates
                .get(index);
        String selectedDate = merchandiseScheduleDate.getDate() + "/"
                + merchandiseScheduleDate.getMonth() + "/"
                + merchandiseScheduleDate.getYear();

        Log.d("selectedDate", "/->" + selectedDate);
        try {
            String reformattedDate = mDateFormat2.format(mDateFormat1.parse(selectedDate));
            assigndate.setText(reformattedDate);
            Log.d("reformattedDate",reformattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        adapter.setCurrentItem(index);
        adapter.notifyDataSetChanged();

//        if(flagname.equalsIgnoreCase("Open")){
//            scheduleList.clear();
//            recyclerAdapter = new ScheduleStatusAdapter(DeliveryOrderNewHeader.this,scheduleList);
//            do_lv.setAdapter(recyclerAdapter);
//            recyclerAdapter.notifyDataSetChanged();
//        }else{
////            prepareScheduleData();
//        }

        new LoadSettings().execute();
//            String reformattedDate = mDateFormat2.format(mDateFormat1.parse(selectedDate));
//            if (!isServerDateCall) {
//                new MerchandiseSchedule.GetSchedulingData().execute(reformattedDate);
//                Log.d("reformattedDate", "false/->" + reformattedDate);
//            } else {
//                isServerDateCall = false;
//                Log.d("reformattedDate", "true /->" + reformattedDate);
//            }
//            //Set Merchandise Schedule Date
//            SalesOrderSetGet.setServerDate(reformattedDate);

        // pager.setAdapter(adapter);
    }

    public void startProgressBar(){
        spinnerLayout = new LinearLayout(DeliveryOrderNewHeader.this);
        spinnerLayout.setGravity(Gravity.CENTER);
        DeliveryOrderNewHeader.this.addContentView(spinnerLayout,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT));
        spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
        enableViews(deliveryO_newparent, false);
        progressBar = new ProgressBar(DeliveryOrderNewHeader.this);
        progressBar.setProgress(android.R.attr.progressBarStyle);
        progressBar.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.greenprogress));
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

//    private void initViewPagerDateSlide(){
//        mViewPagerDate = (ViewPager) findViewById(R.id.pager);
//        mViewPagerDate.setVisibility(View.VISIBLE);
//        adapter = new ScheduleDateAdapter(DeliveryOrderNewHeader.this,getDates());
//        mViewPagerDate.setAdapter(adapter);
//    }
//
//    public void onPagerItemClick(View view, int index) {
//        System.out.println("" + index);
//        adapter.setCurrentItem(index);
//        adapter.notifyDataSetChanged();
//    }
    private ArrayList<ScheduleDate> getDatesmethod() {
        dates.clear();
        Calendar calendar1 = Calendar.getInstance();/* new GregorianCalendar(); */
        Date pastDate = calendar1.getTime();

        calendar1.setTime(pastDate);
        calendar1.add(Calendar.MONTH, -6);
        // Date pastYear = calendar1.getTime();
        // System.out.format("future Year:  %s\n", pastYear);

        Calendar calendar2 = Calendar.getInstance();
        Date futureDate = calendar2.getTime();

        calendar2.setTime(futureDate);
        calendar2.add(Calendar.MONTH, +6);
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
                    + (calendar1.get(Calendar.MONTH) - 6)
                    + "-"
                    + calendar1.get(Calendar.DATE));
            dates.add(merchandiseScheduleDate);
        }
        return dates;
    }
//
//    private String getDay(int index) {
//        switch (index) {
//            case Calendar.SUNDAY:
//                return "SUN";
//            case Calendar.MONDAY:
//                return "MON";
//            case Calendar.TUESDAY:
//                return "TUE";
//            case Calendar.WEDNESDAY:
//                return "WED";
//            case Calendar.THURSDAY:
//                return "THUR";
//            case Calendar.FRIDAY:
//                return "FRI";
//            case Calendar.SATURDAY:
//                return "SAT";
//        }
//        return "";
//    }
//
//    private String getMonth(int index) {
//        switch (index) {
//            case Calendar.JANUARY:
//                return "JANUARY";
//            case Calendar.FEBRUARY:
//                return "FEBRUARY";
//            case Calendar.MARCH:
//                return "MARCH";
//            case Calendar.APRIL:
//                return "APRIL";
//            case Calendar.MAY:
//                return "MAY";
//            case Calendar.JUNE:
//                return "JUNE";
//            case Calendar.JULY:
//                return "JULY";
//            case Calendar.AUGUST:
//                return "AUGUST";
//            case Calendar.SEPTEMBER:
//                return "SEPTEMBER";
//            case Calendar.OCTOBER:
//                return "OCTOBER";
//            case Calendar.NOVEMBER:
//                return "NOVEMBER";
//            case Calendar.DECEMBER:
//                return "DECEMBER";
//        }
//        return "";
//    }
//
//
//    private void taskDate() {
//        dates = new ArrayList<ScheduleDate>();
//        String myFormat = "dd/MM/yyyy";
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        ScheduleDate date = new ScheduleDate();
//        date.setFormattedDate(sdf.format(taskdateCalendar.getTime()));
//        Log.d("date",sdf.format(taskdateCalendar.getTime()));
//        dates.add(date);
//        initViewPagerDateSlide();
//       // task_date_ed.setText(sdf.format(taskdateCalendar.getTime()));
//    }

    /**************DOHeader Call ********************/
    public class LoadRouteHeader extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            scheduleList.clear();
            openArr.clear();
            closeArr.clear();

            for(int i=0;i<settingsArr.size();i++){
                if(settingsArr.get(i).getSettingID().equalsIgnoreCase("MAP_COUNTRY")){

                    ScheduleDataNew.setCountryname(settingsArr.get(i).getSettingValue());
                    Log.d("countryname",settingsArr.get(i).getSettingValue());

                }

                if(settingsArr.get(i).getSettingID().equalsIgnoreCase("VAN_ASSIGNMENT_BASEDON")){

                    ScheduleDataNew.setTrantype(settingsArr.get(i).getSettingValue());
                    Log.d("trantype",settingsArr.get(i).getSettingValue());

                }

            }

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Code", "W!napp@!@#^");
                userArr = SalesOrderWebService
                        .getAllUser("fncGetUserMaster",params);

              //  String date = SalesOrderSetGet.getServerDate();
                String date = assigndate.getText().toString();
//                String user = SupplierSetterGetter.getUsername();
//                String trantype = ScheduleDataNew.getTrantype();
                String trantype = "IN";

                username = receipt_userCode.getText().toString();

                Log.d("serverdate",date);
                Log.d("user",username);
                Log.d("trantype",trantype);

                String fromdate = DateTime.date(date);
                hashValue.put("CompanyCode", cmpnyCode);
             //   hashValue.put("CustomerCode","S1004");
                hashValue.put("TranType", trantype);
               // hashValue.put("AssignUser", user);
                hashValue.put("AssignDate", date);
                hashValue.put("AssignUser",username);

                jsonString = SalesOrderWebService.getSODetail(
                        hashValue, webMethName);
                Log.d("jsonString ",""+jsonString);

                jsonResponse = new JSONObject(jsonString);
                jsonMainNode = jsonResponse.optJSONArray("SODetails");

                int length = jsonMainNode.length();

                for (int i = 0; i < length; i++) {

                    JSONObject jsonChildNode = jsonMainNode
                            .getJSONObject(i);
                    // int sno=1+i;
                    String InvoiceNo = jsonChildNode.optString("InvoiceNo")
                            .toString();
                    String InvoiceDate = jsonChildNode.optString("InvoiceDate")
                            .toString();
                    String customerCode = jsonChildNode.optString(
                            "CustomerCode").toString();
                    String DelCustomerName = jsonChildNode.optString("DelCustomerName")
                            .toString();
                    String DelAddress1 = jsonChildNode.optString("DelAddress1")
                            .toString();
                    String DelAddress2 = jsonChildNode.optString(
                            "DelAddress2").toString();
                    String DelAddress3 = jsonChildNode.optString("DelAddress3")
                            .toString();
                    String DelPostalCode = jsonChildNode.optString("DelPostalCode")
                            .toString();
                    String Remarks = jsonChildNode.optString(
                            "Remarks").toString();
                    String AssignUser = jsonChildNode.optString("AssignUser")
                            .toString();
                    String AssignDate = jsonChildNode.optString("AssignDate")
                            .toString();
                    String GotSignature = jsonChildNode.optString("GotSignature")
                            .toString();
                    String GotSignatureDate = jsonChildNode.optString(
                            "GotSignatureDate").toString();
                    String EstimatedReachTime = jsonChildNode.optString("EstimatedReachTime")
                            .toString();
                    String KiloMeter = jsonChildNode.optString("KiloMeter")
                            .toString();
                    String RequestedDeliveryTime = jsonChildNode.optString("RequestedDeliveryTime")
                            .toString();
                    String StartTime = jsonChildNode.optString(
                            "StartTime").toString();
                    String TravelTimeInMinutes = jsonChildNode.optString("TravelTimeInMinutes")
                            .toString();
                    String DestEndPoint = jsonChildNode.optString("DestEndPoint")
                            .toString();


                        if(GotSignature.equalsIgnoreCase("true")){
                            ScheduleDataNew so = new ScheduleDataNew();
                            so.setInvoiceNo(InvoiceNo);
                            so.setCustomerCode(customerCode);
                            so.setDelCustomerName(DelCustomerName);
                            so.setDelAddress1(DelAddress1);
                            so.setDelAddress2(DelAddress2);
                            so.setDelAddress3(DelAddress3);
                            so.setDelPostalCode(DelPostalCode);
                            so.setRemarks(Remarks);
                            so.setAssignUser(AssignUser);
                            so.setAssignDate(AssignDate);
                            so.setGotSignatureDate(GotSignatureDate);
                            so.setEstimatedReachTime(EstimatedReachTime);
                            so.setKiloMeter(KiloMeter);
                            so.setStartTime(StartTime);
                            so.setTravelTimeInMinutes(TravelTimeInMinutes);
                            so.setDestendpoint(DestEndPoint);
                            so.setGotSignature(GotSignature);
                            if (InvoiceDate != null) {
                                InvoiceDate = InvoiceDate.split("\\ ")[0];
                                so.setInvoiceDate(InvoiceDate);
                            } else {
                                so.setInvoiceDate(InvoiceDate);
                            }

                            if (RequestedDeliveryTime != null && !RequestedDeliveryTime.isEmpty()) {
                                String[] splited1 = RequestedDeliveryTime.split("\\s+");

                                String split_one=splited1[0];
                                String startTime=splited1[1];
                                so.setRequestedDeliveryTime(startTime);
                            } else {
                                so.setRequestedDeliveryTime(RequestedDeliveryTime);
                            }
                            closeArr.add(so);

                        }else if(GotSignature.equalsIgnoreCase("")){
                            ScheduleDataNew so = new ScheduleDataNew();
                            so.setInvoiceNo(InvoiceNo);
                            so.setCustomerCode(customerCode);
                            so.setDelCustomerName(DelCustomerName);
                            so.setDelAddress1(DelAddress1);
                            so.setDelAddress2(DelAddress2);
                            so.setDelAddress3(DelAddress3);
                            so.setDelPostalCode(DelPostalCode);
                            so.setRemarks(Remarks);
                            so.setAssignUser(AssignUser);
                            so.setAssignDate(AssignDate);
                            so.setGotSignatureDate(GotSignatureDate);
                            so.setEstimatedReachTime(EstimatedReachTime);
                            so.setKiloMeter(KiloMeter);
                            so.setStartTime(StartTime);
                            so.setTravelTimeInMinutes(TravelTimeInMinutes);
                            so.setDestendpoint(DestEndPoint);
                            so.setGotSignature(GotSignature);
                            if (InvoiceDate != null) {
                                InvoiceDate = InvoiceDate.split("\\ ")[0];
                                so.setInvoiceDate(InvoiceDate);
                            } else {
                                so.setInvoiceDate(InvoiceDate);
                            }
                            if (RequestedDeliveryTime != null && !RequestedDeliveryTime.isEmpty()) {
                                String[] splited1 = RequestedDeliveryTime.split("\\s+");

                                String split_one=splited1[0];
                                String startTime=splited1[1];
                                so.setRequestedDeliveryTime(startTime);
                            } else {
                                so.setRequestedDeliveryTime(RequestedDeliveryTime);
                            }
                            openArr.add(so);
                        }

                        ScheduleDataNew so = new ScheduleDataNew();
                        so.setInvoiceNo(InvoiceNo);
                        so.setCustomerCode(customerCode);
                        so.setDelCustomerName(DelCustomerName);
                        so.setDelAddress1(DelAddress1);
                        so.setDelAddress2(DelAddress2);
                        so.setDelAddress3(DelAddress3);
                        so.setDelPostalCode(DelPostalCode);
                        so.setRemarks(Remarks);
                        so.setAssignUser(AssignUser);
                        so.setAssignDate(AssignDate);
                        so.setGotSignatureDate(GotSignatureDate);
                        so.setEstimatedReachTime(EstimatedReachTime);
                        so.setKiloMeter(KiloMeter);
                        so.setStartTime(StartTime);
                        so.setTravelTimeInMinutes(TravelTimeInMinutes);
                        so.setDestendpoint(DestEndPoint);
                        so.setGotSignature(GotSignature);
                        if (InvoiceDate != null) {
                            InvoiceDate = InvoiceDate.split("\\ ")[0];
                            so.setInvoiceDate(InvoiceDate);
                        } else {
                            so.setInvoiceDate(InvoiceDate);
                        }
                    if (RequestedDeliveryTime != null && !RequestedDeliveryTime.isEmpty()) {
                        String[] splited1 = RequestedDeliveryTime.split("\\s+");

                        String split_one=splited1[0];
                        String startTime=splited1[1];
                        so.setRequestedDeliveryTime(startTime);
                    } else {
                        so.setRequestedDeliveryTime(RequestedDeliveryTime);
                    }
                        scheduleList.add(so);

                }
                // End
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("openArr",""+openArr.size());
            Log.d("closeArr",""+closeArr.size());
            Log.d("scheduleList",""+scheduleList.size());
            if (DeliveryOrderNewHeader.this != null) {
                try {
                    headerCustCode();
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(deliveryO_newparent, true);

            }
        }
    }


    public void loadProgress(){
        spinnerLayout = new LinearLayout(DeliveryOrderNewHeader.this);
        spinnerLayout.setGravity(Gravity.CENTER);
        DeliveryOrderNewHeader.this.addContentView(spinnerLayout,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT));
        spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
        enableViews(deliveryO_newparent, false);
        progressBar = new ProgressBar(DeliveryOrderNewHeader.this);
        progressBar.setProgress(android.R.attr.progressBarStyle);
        progressBar.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.greenprogress));
        spinnerLayout.addView(progressBar);
    }

    public class LoadSettings extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            settingsArr.clear();

            loadProgress();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {

                hashValue.put("CompanyCode", cmpnyCode);

                jsonString = SalesOrderWebService.getSODetail(
                        hashValue, "fncGetRouteSettings");
                Log.d("jsonString ",""+jsonString);

                jsonResponse = new JSONObject(jsonString);
                jsonMainNode = jsonResponse.optJSONArray("SODetails");

                int length = jsonMainNode.length();

                for (int i = 0; i < length; i++) {

                    JSONObject jsonChildNode = jsonMainNode
                            .getJSONObject(i);
                    // int sno=1+i;
                    String SettingID = jsonChildNode.optString("SettingID")
                            .toString();
                    String SettingValue = jsonChildNode.optString("SettingValue")
                            .toString();

                    ScheduleDataNew so = new ScheduleDataNew();
                    so.setSettingID(SettingID);
                    so.setSettingValue(SettingValue);

                    settingsArr.add(so);

                }
                // End
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Log.d("settingsArr",""+settingsArr.size());

            new LoadRouteHeader().execute();

        }
    }


    public void headerCustCode() throws ParseException {

        Log.d("flagname",flagname);

        if(flagname.equalsIgnoreCase("All")){
            recyclerAdapter = new ScheduleStatusAdapter(DeliveryOrderNewHeader.this,scheduleList);
            do_lv.setAdapter(recyclerAdapter);

        }else if(flagname.equalsIgnoreCase("Open")){
            recyclerAdapter = new ScheduleStatusAdapter(DeliveryOrderNewHeader.this,openArr);
            do_lv.setAdapter(recyclerAdapter);

        }else if(flagname.equalsIgnoreCase("Closed")){
            recyclerAdapter = new ScheduleStatusAdapter(DeliveryOrderNewHeader.this,closeArr);
            do_lv.setAdapter(recyclerAdapter);

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ScheduleDataNew so = null;
        int position = recyclerAdapter.getPosition();
        Log.d("position", String.valueOf(position));
        if(flagname.equalsIgnoreCase("All")){
             so = scheduleList.get(position);
        }else if(flagname.equalsIgnoreCase("Open")){
            so = openArr.get(position);
        }else if(flagname.equalsIgnoreCase("Closed")){
            so = closeArr.get(position);
        }

        invoiceno = so.getInvoiceNo();
        menu.setHeaderTitle(invoiceno);
        postalcode = so.getDelPostalCode();
        address1 =  so.getDelAddress1();
        address2 = so.getDelAddress2();
        address3 = so.getDelAddress3();
        invoicedate = so.getInvoiceDate();
        cust_code = so.getCustomerCode();
        customername = so.getDelCustomerName();
        destendpoint = so.getDestendpoint();
        Log.d("postalcode",postalcode);
        Log.d("address1",address1);
        Log.d("invoiceno",invoiceno);
        Log.d("invoicedate",invoicedate);
        Log.d("cust_code",cust_code);
        Log.d("customername",customername);
        Log.d("destendpoint",destendpoint);
        countryname = ScheduleDataNew.getCountryname();

        fulladdress = countryname + " " + destendpoint ;

        Log.d("fulladdress",fulladdress);

        if(destendpoint != null && !destendpoint.isEmpty()){

        }else{
            destendpoint="0.00";
            destinationLatitudeValue = 0.0;
            destinationLongitudeValue = 0.0;
        }

        if(destendpoint.matches("0.00")){
            getDestinationLatLngFromAddress(countryname);
        }else{
            getDestinationLatLngFromAddress(fulladdress);
        }

        menu.add(0, v.getId(), 0, "Start");
        menu.add(0, v.getId(), 0, "Detail");

    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {

        if (item.getTitle() == "Start") {

            Log.d("lat",""+destinationLatitudeValue);
            Log.d("lon",""+destinationLongitudeValue);
            Intent i = new Intent(DeliveryOrderNewHeader.this,RouteMapActivity.class);
            i.putExtra("flagname","Normal");
            i.putExtra("destlat",destinationLatitudeValue);
            i.putExtra("destlng",destinationLongitudeValue);
            i.putExtra("destendpoint",destendpoint);
            startActivity(i);
            finish();

        } else if(item.getTitle() == "Detail"){
            helper.showProgressDialog(R.string.invoice_printpreview);
            AsyncPrintCall task = new AsyncPrintCall();
            task.execute();
        } else {
            return false;
        }
        return true;
    }

    private void getDestinationLatLngFromAddress(String addressvalue){
        List<Address> address = null;

        if (geoCoder != null) {
            try {
                address = geoCoder.getFromLocationName(addressvalue, 10);
                if (address.size() > 0) {
                    Address first = address.get(0);
                    destinationLatitudeValue = first.getLatitude();
                    destinationLongitudeValue = first.getLongitude();
                    Log.d("lat","sPickUpPostal-->"+destinationLatitudeValue);
                    Log.d("lon","sPickUpPostal-->"+destinationLongitudeValue);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void getLatLngFromAddress(String addressvalue){
        List<Address> address = null;

        if (geoCoder != null) {
            try {
                address = geoCoder.getFromLocationName(addressvalue, 10);
                if (address.size() > 0) {
                    Address first = address.get(0);
                    latitude = first.getLatitude();
                    longitude = first.getLongitude();
                    Log.d("lat","slatitude-->"+latitude);
                    Log.d("lon","slongitude-->"+longitude);
                    ScheduleDataNew mdata = new ScheduleDataNew();
                    mdata.setLatitude(latitude);
                    mdata.setLongitude(longitude);
                    LatLngArr.add(mdata);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private class AsyncPrintCall extends
            AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            product.clear();
            productdet.clear();
            footerArr.clear();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... arg0) {

            String decimalpts = ".00";
            String cmpnyCode = SupplierSetterGetter.getCompanyCode();
            String showcartonloose = SalesOrderSetGet
                    .getCartonpriceflag();

            hashValue.put("CompanyCode", cmpnyCode);
            hashValue.put("InvoiceNo", invoiceno);

            HashMap<String, String> custhash = new HashMap<String, String>();
            custhash.put("CompanyCode", cmpnyCode);
            custhash.put("CustomerCode", cust_code);

            hashVl.put("CompanyCode", cmpnyCode);

            if (showcartonloose.equalsIgnoreCase("1")) {
                jsonString = SalesOrderWebService.getSODetail(
                        hashValue, "fncGetInvoiceDetailWithCarton");
            } else {
                jsonString = SalesOrderWebService.getSODetail(
                        hashValue, "fncGetInvoiceDetail");
            }
            jsonStr = SalesOrderWebService.getSODetail(hashValue,
                    "fncGetInvoiceHeaderByInvoiceNo");
            custjsonStr = SalesOrderWebService.getSODetail(custhash,
                    "fncGetCustomer");

            jsonSt = SalesOrderWebService.getSODetail(hashVl,
                    "fncGetMobilePrintFooter");

            Log.d("jsonSt", jsonSt);
            try {
                jsonRespfooter = new JSONObject(jsonSt);
                jsonSecNodefooter = jsonRespfooter
                        .optJSONArray("SODetails");

                /*********** Print Footer ************/
                int lengJsonArr1 = jsonSecNodefooter.length();
                for (int i = 0; i < lengJsonArr1; i++) {
                    /****** Get Object for each JSON node. ***********/
                    JSONObject jsonChildNode;
                    ProductDetails productdetail = new ProductDetails();
                    try {
                        jsonChildNode = jsonSecNodefooter
                                .getJSONObject(i);

                        String ReceiptMessage = jsonChildNode
                                .optString("ReceiptMessage").toString();
                        String SortOrder = jsonChildNode.optString(
                                "SortOrder").toString();

                        productdetail.setReceiptMessage(ReceiptMessage);
                        productdetail.setSortOrder(SortOrder);
                        footerArr.add(productdetail);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
            }

            Log.d("jsonString ", "" + jsonString);
            Log.d("jsonStr ", "" + jsonStr);
            Log.d("custjsonStr ", "" + custjsonStr);

            try {

                jsonResponse = new JSONObject(jsonString);
                jsonMainNode = jsonResponse.optJSONArray("SODetails");

                jsonResp = new JSONObject(jsonStr);
                jsonSecNode = jsonResp.optJSONArray("SODetails");

                custjsonResp = new JSONObject(custjsonStr);
                custjsonMainNode = custjsonResp.optJSONArray("SODetails");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            /********
             * Show Default carton,loose,foc,exchange qty and price Based On
             * General settings
             *********/
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
                        /*if (onlineMode.matches("True")) {
                            if (checkOffline == true) {
                                // Offline
                            } else {
                                // Online
                                // Show batch number
                                String showbatch = MobileSettingsSetterGetter
                                        .getShowBatchDetails();
                                if (showbatch.matches("True")) {
                                    hashValue.put("slNo", slNo);
                                    hashValue.put("ProductCode", productCode);
                                    Log.d("hashValue ", "" + hashValue);
                                    batchjsonStr = SalesOrderWebService
                                            .getSODetail(hashValue,
                                                    "fncGetInvoiceBatchDetail");
                                    Log.d("batchjsonStr ", "bat test "
                                            + batchjsonStr);
                                    try {
                                        batch_jsonResp = new JSONObject(
                                                batchjsonStr);
                                        batch_jsonSecNode = batch_jsonResp
                                                .optJSONArray("SODetails");

                                    } catch (JSONException e) {

                                        e.printStackTrace();
                                    }
                                    int batch_lengthJsonArr = batch_jsonSecNode
                                            .length();
                                    for (int k = 0; k < batch_lengthJsonArr; k++) {
                                        *//****** Get Object for each JSON node. ***********//*
                                        ProductDetails prodBatch = new ProductDetails();
                                        try {
                                            JSONObject batch_jsonChildNode = batch_jsonSecNode
                                                    .getJSONObject(k);
                                            prodBatch
                                                    .setProduct_batchno(batch_jsonChildNode
                                                            .optString(
                                                                    "BatchNo")
                                                            .toString());
                                            prodBatch
                                                    .setBatch_productcode(batch_jsonChildNode
                                                            .optString(
                                                                    "ProductCode")
                                                            .toString());

                                            product_batchArr.add(prodBatch);

                                            Log.d("BatchNo",
                                                    batch_jsonChildNode
                                                            .optString(
                                                                    "BatchNo")
                                                            .toString());

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }
                            }
                        }*/
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
//                        if (gnrlStngs.matches("C")) {
//                            productdetail.setSortproduct(jsonChildNode
//                                    .optString("CategoryCode").toString());
//                        } else if (gnrlStngs.matches("S")) {
//                            productdetail.setSortproduct(jsonChildNode
//                                    .optString("SubCategoryCode").toString());
//                        } else if (gnrlStngs.matches("N")) {
//                            productdetail.setSortproduct("");
//                        } else {
//                            productdetail.setSortproduct("");
//                        }
                        productdetail.setTax(jsonChildNode.optString("Tax").toString());
                        productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
                        productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
                        productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
                        product.add(productdetail);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                /******** Print qty and price *********/
                for (int i = 0; i < lengthJsonArr; i++) {
                    /****** Get Object for each JSON node. ***********/
                    JSONObject jsonChildNode;
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

                        productdetail.setUOMCode(jsonChildNode.optString(
                                "UOMCode").toString());

                        /*if (onlineMode.matches("True")) {
                            if (checkOffline == true) {
                                // Offline
                            } else {
                                // Online
                                // Show batch number

                                String showbatch = MobileSettingsSetterGetter
                                        .getShowBatchDetails();

                                if (showbatch.matches("True")) {

                                    hashValue.put("slNo", slNo);
                                    hashValue.put("ProductCode", productCode);
                                    Log.d("hashValue ", "" + hashValue);
                                    batchjsonStr = SalesOrderWebService
                                            .getSODetail(hashValue,
                                                    "fncGetInvoiceBatchDetail");
                                    Log.d("batchjsonStr ", "bat test "
                                            + batchjsonStr);
                                    try {
                                        batch_jsonResp = new JSONObject(
                                                batchjsonStr);
                                        batch_jsonSecNode = batch_jsonResp
                                                .optJSONArray("SODetails");

                                    } catch (JSONException e) {

                                        e.printStackTrace();
                                    }

                                    int batch_lengthJsonArr = batch_jsonSecNode
                                            .length();
                                    for (int k = 0; k < batch_lengthJsonArr; k++) {
                                        *//****** Get Object for each JSON node. ***********//*
                                        ProductDetails prodBatch = new ProductDetails();
                                        try {
                                            JSONObject batch_jsonChildNode = batch_jsonSecNode
                                                    .getJSONObject(k);
                                            prodBatch
                                                    .setProduct_batchno(batch_jsonChildNode
                                                            .optString(
                                                                    "BatchNo")
                                                            .toString());
                                            prodBatch
                                                    .setBatch_productcode(batch_jsonChildNode
                                                            .optString(
                                                                    "ProductCode")
                                                            .toString());

                                            product_batchArr.add(prodBatch);

                                            Log.d("BatchNo",
                                                    batch_jsonChildNode
                                                            .optString(
                                                                    "BatchNo")
                                                            .toString());

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }
                            }
                        }*/

                        productdetail.setDescription(jsonChildNode.optString(
                                "ProductName").toString());

                        String invoiceqty = jsonChildNode.optString("Qty")
                                .toString();

                        if(invoiceqty!=null && !invoiceqty.isEmpty()){

                        }else{
                            invoiceqty="0";
                        }

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
                        Log.d("pricevalue", "--->" + pricevalue);

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
//                        if (gnrlStngs.matches("C")) {
//                            productdetail.setSortproduct(jsonChildNode
//                                    .optString("CategoryCode").toString());
//                        } else if (gnrlStngs.matches("S")) {
//                            productdetail.setSortproduct(jsonChildNode
//                                    .optString("SubCategoryCode").toString());
//                        } else if (gnrlStngs.matches("N")) {
//                            productdetail.setSortproduct("");
//                        } else {
//                            productdetail.setSortproduct("");
//                        }
                        productdetail.setTax(jsonChildNode.optString("Tax").toString());
                        productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
                        productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
                        productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
                        product.add(productdetail);

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            }

            int lengJsonArr = jsonSecNode.length();
            for (int i = 0; i < lengJsonArr; i++) {

                JSONObject jsonChildNode;
                ProductDetails productdetail = new ProductDetails();
                try {
                    jsonChildNode = jsonSecNode.getJSONObject(i);

                    String itemdiscvalue = jsonChildNode.optString(
                            "ItemDiscount").toString();
                    String billdiscvalue = jsonChildNode.optString(
                            "BillDIscount").toString();
                    String subtotalvalue = jsonChildNode.optString("SubTotal")
                            .toString();

                    productdetail.setTax(twoDecimalPoint(jsonChildNode
                            .optDouble("Tax")));

                    String nettotalvalue = jsonChildNode.optString("NetTotal")
                            .toString();

                    String paidamount = jsonChildNode.optString("PaidAmount")
                            .toString();

                    String BalanceAmount = jsonChildNode.optString("BalanceAmount")
                            .toString();

                    productdetail.setPaidamount(paidamount);

                    productdetail.setBalanceAmount(BalanceAmount);

                    if (itemdiscvalue.contains(".")
                            && billdiscvalue.contains(".")
                            && subtotalvalue.contains(".")
                            && nettotalvalue.contains(".")) {
                        productdetail.setItemdisc(itemdiscvalue);
                        productdetail.setBilldisc(billdiscvalue);
                        productdetail.setSubtotal(subtotalvalue);
                        productdetail.setNettot(nettotalvalue);
                    } else {
                        productdetail.setItemdisc(itemdiscvalue + decimalpts);
                        productdetail.setBilldisc(billdiscvalue + decimalpts);
                        productdetail.setSubtotal(subtotalvalue + decimalpts);
                        productdetail.setNettot(nettotalvalue + decimalpts);
                    }
                    productdetail.setRemarks(jsonChildNode.optString("Remarks")
                            .toString());
                    productdetail.setTotaloutstanding(jsonChildNode.optString(
                            "TotalBalance").toString());
                    productdetail.setCreateDate(jsonChildNode.optString(
                            "CreateDate").toString());

                    productdet.add(productdetail);

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

            int custJsonArr = custjsonMainNode.length();
            for (int i = 0; i < custJsonArr; i++) {

                JSONObject jsonChildNode;

                try {
                    jsonChildNode = custjsonMainNode.getJSONObject(i);

                    String CustomerCode = jsonChildNode.optString(
                            "CustomerCode").toString();
                    String CustomerName = jsonChildNode.optString(
                            "CustomerName").toString();
                    String Address1 = jsonChildNode.optString("Address1")
                            .toString();
                    String Address2 = jsonChildNode.optString("Address2")
                            .toString();
                    String Address3 = jsonChildNode.optString("Address3")
                            .toString();
                    String PhoneNo = jsonChildNode.optString("PhoneNo")
                            .toString();
                    String HandphoneNo = jsonChildNode.optString("HandphoneNo")
                            .toString();
                    String Email = jsonChildNode.optString("Email").toString();
                    String TermName = jsonChildNode.optString("TermName")
                            .toString();
                    String OutstandingAmount = jsonChildNode.optString(
                            "OutstandingAmount").toString();
                    String TaxValue = jsonChildNode.optString("TaxValue")
                            .toString();

                    CustomerSetterGetter.setCustomerCode(CustomerCode);
                    CustomerSetterGetter.setCustomerName(CustomerName);
                    CustomerSetterGetter.setCustomerAddress1(Address1);
                    CustomerSetterGetter.setCustomerAddress2(Address2);
                    CustomerSetterGetter.setCustomerAddress3(Address3);
                    CustomerSetterGetter.setCustomerPhone(PhoneNo);
                    CustomerSetterGetter.setCustomerHP(HandphoneNo);
                    CustomerSetterGetter.setCustomerEmail(Email);
                    CustomerSetterGetter.setCustomerTerms(TermName);
                    CustomerSetterGetter.setTotalOutstanding(OutstandingAmount);
                    SalesOrderSetGet.setCustomerTaxPerc(TaxValue);
					/*
					 * Log.d("mobile settings Customer code",
					 * CustomerSetterGetter.getCustomerCode());
					 */
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

            hashVl.clear();
            hashValue.clear();
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {

//
            Log.d("receiptprintpreview", "receiptprintpreview");
            Log.d("product", "" + product);

            // for(ProductDetails prod: product){
            // Log.d("price",""+prod.getPrice());
            // }

            Log.d("productdet", "" + productdet);
            helper.dismissProgressDialog();
            Intent i = new Intent(DeliveryOrderNewHeader.this,
                    RoutePrintPreview.class);
            i.putExtra("invNo", invoiceno);
            i.putExtra("invDate", invoicedate);
            i.putExtra("customerCode", cust_code);
            i.putExtra("customerName", customername);
            i.putExtra("sort", sort);
            i.putExtra("gnrlStngs", "");
            i.putExtra("destlat",destinationLatitudeValue);
            i.putExtra("destlng",destinationLongitudeValue);
            i.putExtra("address1",address1);
            i.putExtra("address2",address2);
            PreviewPojo.setProducts(product);
            PreviewPojo.setProductsDetails(productdet);
            startActivity(i);
            finish();

//            }
        }

    }

    public class GetLatLngArr extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            LatLngArr.clear();
            spinnerLayout = new LinearLayout(DeliveryOrderNewHeader.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            DeliveryOrderNewHeader.this.addContentView(spinnerLayout,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(deliveryO_newparent, false);
            progressBar = new ProgressBar(DeliveryOrderNewHeader.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    R.drawable.greenprogress));
            spinnerLayout.addView(progressBar);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {

                if(flagname.equalsIgnoreCase("All")){
                    routeArr = scheduleList;
                }else if(flagname.equalsIgnoreCase("Open")){
                    routeArr = openArr;
                }else if(flagname.equalsIgnoreCase("Closed")){
                    routeArr = closeArr;
                }

                Log.d("routeArr",""+routeArr.size());

                String countryname = ScheduleDataNew.getCountryname();
                if(routeArr.size()>0){
                for(int i=0;i<routeArr.size();i++) {
                    String destendpoint = routeArr.get(i).getDestendpoint();
                    String fulladdress = countryname + " " + destendpoint;

                    Log.d("fulladdressvalue", fulladdress);

                    if (destendpoint != null && !destendpoint.isEmpty()) {

                    } else {
                        destendpoint = "0.00";
                        latitude = 0.0;
                        longitude = 0.0;
                    }

                    if (destendpoint.matches("0.00")) {
                        getLatLngFromAddress(countryname);
                    } else {
                        getLatLngFromAddress(fulladdress);
                    }
                }}
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Log.d("LatLngArr",""+LatLngArr.size());
            if(LatLngArr.size()>0){
                destendpoint="0.00";
                destinationLatitudeValue = 0.0;
                destinationLongitudeValue = 0.0;

                Intent i = new Intent(DeliveryOrderNewHeader.this,RouteMapActivity.class);
                i.putExtra("flagname","FullRoute");
                i.putExtra("destlat",destinationLatitudeValue);
                i.putExtra("destlng",destinationLongitudeValue);
                i.putExtra("destendpoint",destendpoint);
                i.putExtra("LatLngArr",LatLngArr);
                //  i.putParcelableArrayListExtra("LatLngArr", LatLngArr);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(DeliveryOrderNewHeader.this,"No Route Found",Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(deliveryO_newparent, true);
        }
    }

    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tax = df.format(d);
        return tax;
    }

    public void alertDialogUserSearch() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(
                DeliveryOrderNewHeader.this);
        final EditText editText = new EditText(DeliveryOrderNewHeader.this);
        final ListView listview = new ListView(DeliveryOrderNewHeader.this);
        LinearLayout layout = new LinearLayout(DeliveryOrderNewHeader.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        myDialog.setTitle(getResources().getString(R.string.user));
        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0,
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

        arrayAdapterSupp = new CustomAlertAdapterSupp(DeliveryOrderNewHeader.this,
                userArr);
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
                    String keyValues = (String) mapEntry.getKey();
                    String name = (String) mapEntry.getValue();

                    receipt_userCode.setText(name);
                    loadProgress();
                    new LoadRouteHeader().execute();
//					sl_namefield.setText(name);
                    receipt_userCode.addTextChangedListener(new TextWatcher() {
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
                            textlength = receipt_userCode.getText().length();
//                            sl_namefield.setText("");
                        }
                    });
                }
            }
        });

        searchResults = new ArrayList<HashMap<String, String>>(userArr);
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
                for (int i = 0; i < userArr.size(); i++) {
                    String supplierName = userArr.get(i).toString();
                    if (textlength <= supplierName.length()) {
                        if (supplierName.toLowerCase().contains(
                                editText.getText().toString().toLowerCase()
                                        .trim()))
                            searchResults.add(userArr.get(i));
                    }
                }

                arrayAdapterSupp = new CustomAlertAdapterSupp(
                        DeliveryOrderNewHeader.this, searchResults);
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


    @Override
    public void onBackPressed() {
        Intent i = new Intent(DeliveryOrderNewHeader.this, LandingActivity.class);
        startActivity(i);
        DeliveryOrderNewHeader.this.finish();
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
