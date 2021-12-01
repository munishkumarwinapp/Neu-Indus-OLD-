package com.winapp.crm;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.adapter.StockCheckAdapter;
import com.winapp.SFA.R;
import com.winapp.fragment.CustomDialogFragment;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.helper.GPSTracker;
import com.winapp.model.Attendance;
import com.winapp.printer.UIHelper;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.sot.WebServiceClass;
import com.winapp.util.AskPermission;
import com.winapp.util.CustomCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class HistoryActivity extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace, Constants {
    private LinearLayout main_layout;
    private HashMap<String, String> mParam = new HashMap<String, String>();
    private String companycode = "", username = "", serverdate = "", address1 = "", address2 = "",
            get_url="", mUsername = "";;
    private double latitude = 0, longitude = 0;
    private GPSTracker gps;
    private CustomCalendar customCalendar;
    private Calendar mCalendar;
    private RecyclerView mRecyclerView;
    private ArrayList<Attendance> mAttendanceArr,mUserArr;
    private JSONArray mJSONPRODARRAY = null,mJSONCUSTARRAY = null;
    private UIHelper mHelper;
    public EditText username_search_ed,date_ed;
    private TextView mEmptyTxV;
    private StockCheckAdapter mStockCheckAdapter;
    private Button search_btn;
    private ImageButton search,printer,addnew;
    private SlidingMenu menu;
    private Button attendanceBtnTab,historyBtnTab;
    private LinearLayout mUserNameLayout,mDateLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setIcon(R.drawable.ic_menu);
        setContentView(R.layout.activity_history);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        View customNav = LayoutInflater.from(this).inflate(
                R.layout.slidemenu_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        txt.setText("History");
        search = (ImageButton) customNav.findViewById(R.id.search_img);
        printer = (ImageButton) customNav.findViewById(R.id.printer);
        addnew = (ImageButton) customNav
                .findViewById(R.id.custcode_img);

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

        printer.setVisibility(View.GONE);
        search.setVisibility(View.INVISIBLE);
        addnew.setVisibility(View.VISIBLE);
         addnew.setImageResource(R.mipmap.ic_find);

        main_layout = (LinearLayout) findViewById(R.id.main_layout);
        mUserNameLayout= (LinearLayout) findViewById(R.id.username_layout);
        mDateLayout= (LinearLayout) findViewById(R.id.date_layout);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        attendanceBtnTab = (Button) findViewById(R.id.attendanceBtnTab);
        historyBtnTab = (Button) findViewById(R.id.historyBtnTab);
        username_search_ed = (EditText) findViewById(R.id.username_search_ed);
        date_ed = (EditText) findViewById(R.id.date_ed);
        mEmptyTxV = (TextView) findViewById(R.id.empty);
        search_btn= (Button) findViewById(R.id.search_btn);

        mParam = new HashMap<>();
        mAttendanceArr = new ArrayList<>();
        mUserArr = new ArrayList<>();
        mStockCheckAdapter = new StockCheckAdapter();

        FWMSSettingsDatabase.init(HistoryActivity.this);
        get_url = FWMSSettingsDatabase.getUrl();
        new WebServiceClass(get_url);


        username = SupplierSetterGetter.getUsername();
        companycode = SupplierSetterGetter.getCompanyCode();

        mCalendar = Calendar.getInstance();
        mHelper = new UIHelper(HistoryActivity.this);
        new ProductStockAsync(true).execute(mUsername);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        username_search_ed.setText(username);
        historyBtnTab.setBackgroundResource(R.drawable.tab_select_right);

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserNameLayout.getVisibility() == View.VISIBLE && mDateLayout.getVisibility() == View.VISIBLE) {
                    mUserNameLayout.setVisibility(View.GONE);
                    mDateLayout.setVisibility(View.GONE);
                    search_btn.setVisibility(View.GONE);
                } else {
                    mUserNameLayout.setVisibility(View.VISIBLE);
                    mDateLayout.setVisibility(View.VISIBLE);
                    search_btn.setVisibility(View.VISIBLE);
                }

            }
        });
        attendanceBtnTab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(HistoryActivity.this,
                        AttendanceActivity.class);
                startActivity(i);
                HistoryActivity.this.finish();
            }
        });
        username_search_ed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogFragment f = new CustomDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("CustomerData",mUserArr);
                f.setArguments(bundle);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(f,"fragment_dialog");
                ft.commit();
                f.OnDialogCompletionListener(new CustomDialogFragment.OnDialogCompletionListener() {
                    @Override
                    public void OnDialogCompletionListener(String code,String name) {
                        Log.d("code","--->"+code);
//                        new ProductStockAsync(false).execute(code);
                        username_search_ed.setText(name);
                    }
                });
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = username_search_ed.getText().toString();
                new ProductStockAsync(false).execute(code);
            }
        });
     /*   mStockCheckAdapter.SetOnItemClickListener(new StockCheckAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                Log.d("click","click");
                LocationDialog f = new LocationDialog();
                FragmentTransaction ft = HistroyActivity.this.getSupportFragmentManager().beginTransaction();
                ft.add(f,"fragment_dialog");
                ft.commit();

            }
        });*/

        date_ed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                customCalendar = new CustomCalendar(HistoryActivity.this);
                customCalendar.showCalendarView();
                boolean mstatus = customCalendar.showDialog();
                Log.d("mstatus", mstatus+"");
                if (mstatus == true) {
                    String sDate = customCalendar.getSelectDate();
                    date_ed.setText(sDate);
                } else {
                    Log.d("False", "-->" + "False");
                }
            }
        });

       //AskPermission.verifyStoragePermissions(HistroyActivity.this);

        try {
            gps = new GPSTracker(HistoryActivity.this);
            // check if GPS enabled
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                Log.d("Summary latitude", "->" + latitude);
                Log.d("Summary longitude", "->" + longitude);
                Geocoder geocoder = new Geocoder(HistoryActivity.this, Locale.getDefault());
                List<Address> addresses;
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {

                    address1 = addresses.get(0).getAddressLine(0);
                    address2 = addresses.get(0).getAddressLine(1);

                    if (address2 != null && !address2.isEmpty()) {
//                        sm_location.setText(address1 + "," + address2);
                        Log.d("address1", address1);
                        Log.d("address2", address2);
                    } else {
                        Log.d("address1", address1);
//                        sm_location.setText(address1);
                    }
                    gps.stopUsingGPS();
                }
                // \n is for new line
//				Toast.makeText(HistroyActivity.this, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
//				gps.showSettingsAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class ProductStockAsync extends AsyncTask<String, String, String> {
        private boolean isCustomerCall;
        private String user="",date_ed_str="";
        public ProductStockAsync(boolean customerCall){
            isCustomerCall = customerCall;
        }
        protected void onPreExecute() {
            mAttendanceArr.clear();
            mHelper.showProgressView(main_layout);
            date_ed_str = date_ed.getText().toString();
        }

        @Override
        protected String doInBackground(String... params) {
            String code = params[0];
            try {
                mParam.put("CompanyCode", companycode);

                if(isCustomerCall) {
                    String getUser = WebServiceClass.parameterService(mParam, GETUSER);
                    JSONObject jsonResponse = new JSONObject(getUser);
                    mJSONCUSTARRAY = jsonResponse.optJSONArray("JsonArray");
                    int lengthJsonCustArr = mJSONCUSTARRAY.length();
                    if (lengthJsonCustArr > 0) {
                        for (int i = 0; i < lengthJsonCustArr; i++) {
                            JSONObject jsonChildNode = mJSONCUSTARRAY.getJSONObject(i);
                            Attendance mAttendance = new Attendance();
                            mAttendance.setCode(jsonChildNode.optString("UserGroupCode").toString());
                            mAttendance.setName(jsonChildNode.optString("UserName").toString());
                            mUserArr.add(mAttendance);
                        }
                    }
                }

                serverdate = WebServiceClass.parameterService(mParam, GETSERVERDATETIME);
                JSONObject jsonResponseDate = new JSONObject(serverdate);

                JSONArray jsonMainNodeDate = jsonResponseDate.optJSONArray("JsonArray");
                JSONObject jsonChildNodeDate = jsonMainNodeDate.getJSONObject(0);
                serverdate = jsonChildNodeDate.optString("ServerDate").toString();
                String dateStr = serverdate.split("\\ ")[0];
               // username = preference.getUserName();
                //  if(isCustomerCall){

                if(code!=null && !code.isEmpty()){
                    username = code;
                    dateStr = date_ed_str;
                    serverdate = date_ed_str;
                }
                //  }

                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("CompanyCode", companycode);
                hm.put("EmployeeCode", username);
                hm.put("FromDate", dateStr + " 00:00");
                hm.put("ToDate", serverdate);

                Log.d("companycode-->", "" + companycode);
                Log.d("EmployeeCode-->", "" + username);
                Log.d("FromDate-->", dateStr+ " 00:00");
                Log.d("ToDate-->", "" + serverdate);

                String getAttendance = WebServiceClass.parameterService(hm, GETATTENDANCE);
                JSONObject jsonResponse = new JSONObject(getAttendance);

                mJSONPRODARRAY = jsonResponse.optJSONArray("JsonArray");

                int lengthJsonArr = mJSONPRODARRAY.length();
                if (lengthJsonArr > 0) {
                    for (int i = 0; i < lengthJsonArr; i++) {
                        JSONObject jsonChildNode = mJSONPRODARRAY.getJSONObject(i);
                        Attendance mAttendance = new Attendance();
                        mAttendance.setCode(jsonChildNode.optString("EmployeeCode").toString());
                        mAttendance.setInTime(jsonChildNode.optString("InTime").toString());
                        mAttendance.setOutTime(jsonChildNode.optString("OutTime").toString());
                        mAttendance.setInLatitude(jsonChildNode.optDouble("InLatitude"));
                        mAttendance.setInLongitude(jsonChildNode.optDouble("InLongitude"));
                        mAttendance.setOutLatitude(jsonChildNode.optDouble("OutLatitude"));
                        mAttendance.setOutLongitude(jsonChildNode.optDouble("OutLongitude"));
                        mAttendanceArr.add(mAttendance);
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {

//            Log.d("mAttendanceArr","aa "+mAttendanceArr.get(0).getCode());
            if(isCustomerCall){
                date_ed.setText(serverdate.split("\\ ")[0]);
            }
            mHelper.dismissProgressView(main_layout);
            mStockCheckAdapter = new StockCheckAdapter(HistoryActivity.this, mAttendanceArr);
            mRecyclerView.setAdapter(mStockCheckAdapter);
            mStockCheckAdapter.notifyDataSetChanged();

        /*    mStockCheckAdapter.SetOnItemClickListener(new StockCheckAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.d("click","click");
              *//*      LocationDialog f = new LocationDialog();
                    FragmentTransaction ft = HistroyActivity.this.getSupportFragmentManager().beginTransaction();
                    ft.add(f,"fragment_dialog");
                    ft.commit();*//*



                }
            });*/
           /* if(mAttendanceArr.size()>0){
                Validate.showViews(true,mRecyclerView);
                Validate.showViews(false,mEmptyTxV);
            }else{
                Validate.showViews(false,mRecyclerView);
                Validate.showViews(true,mEmptyTxV);
            }*/
        }
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
    public void onBackPressed() {
            Intent i = new Intent(HistoryActivity.this, LandingActivity.class);
            startActivity(i);
            HistoryActivity.this.finish();
        }

    }

