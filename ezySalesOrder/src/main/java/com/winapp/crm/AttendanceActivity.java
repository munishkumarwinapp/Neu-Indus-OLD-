package com.winapp.crm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.winapp.helper.Constants;
import com.winapp.helper.GPSTracker;
import com.winapp.model.Attendance;
import com.winapp.printer.UIHelper;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SalesReturnHeader;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.sot.WebServiceClass;
import com.winapp.util.Validate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AttendanceActivity extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace ,Constants {
    ImageView in, out;
    private UIHelper helper;
    private LinearLayout main_layout;
    private HashMap<String, String> mParam = new HashMap<String, String>();
    private String companycode = "", username = "", serverdate = "", attendanceResult = "", address1 = "", address2 = "",
            get_url="";
    private double latitude = 0, longitude = 0;
    private GPSTracker gps;
    private TextView username_tv,current_time_tv,in_time_tv,out_time_tv,current_date_tv;
    private Button attendanceBtnTab,historyBtnTab;
    private ImageButton search,printer,addnew;
    private SlidingMenu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setIcon(R.drawable.ic_menu);
        setContentView(R.layout.activity_attendance);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        View customNav = LayoutInflater.from(this).inflate(
                R.layout.slidemenu_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        txt.setText("Attendance");
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
        addnew.setVisibility(View.INVISIBLE);

        attendanceBtnTab = (Button) findViewById(R.id.attendanceBtnTab);
        historyBtnTab = (Button) findViewById(R.id.historyBtnTab);
        in = (ImageView) findViewById(R.id.in);
        out = (ImageView) findViewById(R.id.out);
        main_layout = (LinearLayout) findViewById(R.id.main_layout);
        username_tv= (TextView) findViewById(R.id.username_tv);
        current_time_tv= (TextView) findViewById(R.id.current_time_tv);
        current_date_tv= (TextView) findViewById(R.id.current_date_tv);

        in_time_tv= (TextView) findViewById(R.id.in_time_tv);
        out_time_tv= (TextView) findViewById(R.id.out_time_tv);

        FWMSSettingsDatabase.init(AttendanceActivity.this);
        SOTDatabase.init(AttendanceActivity.this);
        get_url = FWMSSettingsDatabase.getUrl();
        new WebServiceClass(get_url);
        helper = new UIHelper(AttendanceActivity.this);

        username = SupplierSetterGetter.getUsername();
        companycode = SupplierSetterGetter.getCompanyCode();

        username_tv.setText(""+username);

        helper = new UIHelper(AttendanceActivity.this);
        attendanceBtnTab.setBackgroundResource(R.drawable.tab_select_left);
        getAttendance task = new getAttendance();
        task.execute("GET");

        in.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getAttendance task = new getAttendance();
                task.execute("SAVE");
            }
        });

        out.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getAttendance task = new getAttendance();
                task.execute("SAVE");
            }
        });
        historyBtnTab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(AttendanceActivity.this,
                        HistoryActivity.class);
                startActivity(i);
                AttendanceActivity.this.finish();
            }
        });
        //AskPermission.verifyStoragePermissions(AttendanceActivity.this);

        try {

            gps = new GPSTracker(AttendanceActivity.this);

            // check if GPS enabled
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                Log.d("Summary latitude", "->" + latitude);
                Log.d("Summary longitude", "->" + longitude);
                Geocoder geocoder = new Geocoder(AttendanceActivity.this, Locale.getDefault());
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
//				Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
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
    private class getAttendance extends AsyncTask<String, Void, String> {
        JSONArray jsonArray = null;
        String getattendanceResult = "", slno = "", isOpen = "", type ="",InTime="",OutTime="";

        @Override
        protected void onPreExecute() {
            helper.showProgressView(main_layout);
            mParam.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                type = params[0];

                mParam.put("CompanyCode", companycode);

                serverdate = WebServiceClass.parameterService(mParam, GETSERVERDATETIME);
                JSONObject jsonResponseDate = new JSONObject(serverdate);

                JSONArray jsonMainNodeDate = jsonResponseDate.optJSONArray("JsonArray");
                JSONObject jsonChildNodeDate = jsonMainNodeDate.getJSONObject(0);
                serverdate = jsonChildNodeDate.optString("ServerDate").toString();
                String dateStr = serverdate.split("\\ ")[0];

                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("CompanyCode", companycode);
                hm.put("EmployeeCode", username);
                hm.put("FromDate", dateStr + " 00:00");
                hm.put("ToDate", serverdate);

                Log.d("companycode-->", "" + companycode);
                Log.d("EmployeeCode-->", "" + username);
                Log.d("FromDate-->", dateStr+ " 00:00");
                Log.d("ToDate-->", "" + serverdate);

                getattendanceResult = WebServiceClass.parameterService(hm, GETATTENDANCE);


                JSONObject jsonResponse = new JSONObject(getattendanceResult);

                JSONArray jsonMainNode = jsonResponse.optJSONArray("JsonArray");

                int lengthJsonArr = jsonMainNode.length();

                for (int i = 0; i < lengthJsonArr; i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    slno = jsonChildNode.optString("Slno").toString();
                    isOpen = jsonChildNode.optString("IsOpen").toString();

                    InTime  = jsonChildNode.optString("InTime").toString();
                    OutTime  = jsonChildNode.optString("OutTime").toString();

                    Log.d("slno", "" + slno);
                    Log.d("isOpen", "" + isOpen);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            String date = serverdate.split("\\ ")[0];
            String time = Validate.conver24to12Format(serverdate.split("\\ ")[1]);

            current_date_tv.setText(""+date);
            current_time_tv.setText(" "+time);

            if (!getattendanceResult.matches("")) {

                if(isOpen.matches("False")){

                    if(type.matches("GET")){
                        in.setImageResource(R.mipmap.in_disable);
                        out.setImageResource(R.mipmap.out_icon);
                        in.setClickable(false);
                        out.setClickable(true);
                        Log.d("GET", "Disable IN Status False");

                        String in_time =Validate.conver24to12Format(InTime);
                        String out_time =Validate.conver24to12Format(OutTime);

                        in_time_tv.setText(""+in_time);
                        out_time_tv.setText(""+out_time);

                    }else{
                        SaveAttendance task = new SaveAttendance();
                        task.execute("O",slno);
                    }

                }else{

                    if(type.matches("GET")){
                        in.setImageResource(R.mipmap.in_icon);
                        out.setImageResource(R.mipmap.out_disable);
                        out.setClickable(false);
                        in.setClickable(true);

                        Log.d("GET", "Disable OUT");

                    }else{
                        SaveAttendance task = new SaveAttendance();
                        task.execute("I","");
                    }
                }

            }else{

                if(type.matches("GET")){
                    in.setImageResource(R.mipmap.in_icon);
                    out.setImageResource(R.mipmap.out_disable);
                    out.setClickable(false);
                    in.setClickable(true);

                    Log.d("GET", "Disable INStatus True");

                }else{
                    SaveAttendance task = new SaveAttendance();
                    task.execute("I","");
                }

            }

            helper.dismissProgressView(main_layout);

        }

        private class SaveAttendance extends AsyncTask<String, Void, String> {

            String type="",slno="";
            @Override
            protected void onPreExecute() {

                mParam.clear();
            }

            @Override
            protected String doInBackground(String... params) {
                try {

                    if(params.length>0) {
                        type = params[0];
                        slno = params[1];
                    }

                    //companycode = preference.getCompanyCode();
                    //username = preference.getUserName();

                    String dateStr = serverdate.split("\\ ")[0];

                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("CompanyCode", companycode);
                    hm.put("EmployeeCode", username);

                    if(slno!=null && !slno.isEmpty()){
                        hm.put("slNo", slno);
                    }

                    hm.put("AttendanceDate", dateStr);
                    hm.put("Type", type);
                    hm.put("Latitude", latitude + "");
                    hm.put("Longitude", longitude + "");
                    hm.put("User", username);

                    Log.d("companycode-->", "" + companycode);
                    Log.d("EmployeeCode-->", "" + username);
                    if(slno!=null && !slno.isEmpty()){
                        Log.d("slNo-->",""+ slno);
                    }
                    Log.d("AttendanceDate-->", "" + dateStr);
                    Log.d("Type-->", type);
                    Log.d("Latitude-->", "" + latitude);
                    Log.d("Longitude-->", "" + longitude);
                    Log.d("User-->", "" + username);

                    attendanceResult = WebServiceClass.parameterService(hm, SAVEATTENDANCE);
                    JSONObject jsonResponse = new JSONObject(attendanceResult);
                    JSONArray jsonMainNode = jsonResponse.optJSONArray("JsonArray");
                    int lengthJsonArr = jsonMainNode.length();
                    if(lengthJsonArr>0){
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                        attendanceResult = jsonChildNode.optString("Result").toString();
                    }

                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                if (!attendanceResult.matches("")) {

                    if(type.matches("I")){
//                        Toast.makeText(getActivity(), " Time : " + serverdate + " Logged IN", Toast.LENGTH_LONG).show();

                        in.setImageResource(R.mipmap.in_disable);
                        out.setImageResource(R.mipmap.out_icon);
                        in.setClickable(false);
                        out.setClickable(true);

                        alertDialog("Logged IN","Date & Time : " + serverdate);

                    }else{
//                        Toast.makeText(getActivity(), " Time : " + serverdate + " Logged OUT", Toast.LENGTH_LONG).show();

                        in.setImageResource(R.mipmap.in_icon);
                        out.setImageResource(R.mipmap.out_disable);
                        out.setClickable(false);
                        in.setClickable(true);

                        alertDialog("Logged OUT","Date & Time : " + serverdate);
                    }

                }else{
                    Toast.makeText(AttendanceActivity.this, " Failed ", Toast.LENGTH_LONG).show();
                }

                helper.dismissProgressView(main_layout);

            }

        }
    }
    public void alertDialog(String title,String result) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AttendanceActivity.this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(result);
//        alertDialog.setIcon(R.mipmap.ic_close_grey);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(AttendanceActivity.this,LandingActivity.class);
                        startActivity(i);
                       finish();
                    }
                });

//        alertDialog.setNegativeButton("NO",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        dialog.dismiss();
//                    }
//                });
        alertDialog.show();
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
        Intent i = new Intent(AttendanceActivity.this, LandingActivity.class);
        startActivity(i);
        AttendanceActivity.this.finish();
    }
}
