package com.winapp.trackwithmap;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.SFA.R;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.googlemaps.PlaceDetailsJSONParser;
import com.winapp.googlemaps.PlaceJSONParser;
import com.winapp.helper.Constants;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SlideMenuFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by user on 11-Sep-17.
 */

public class RouteMapActivity extends SherlockFragmentActivity implements DialogInterface.OnCancelListener,OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,View.OnClickListener,SlideMenuFragment.MenuClickInterFace,Constants {
    private static final String TAG = RouteMapActivity.class.getSimpleName();
    private GoogleMap mMap;
    ImageButton route_btn,printer,search,im_date;
    private List<LatLng> latLngList/*, latLngMoveList*/;
    private TextView distanceValue,durationValue,fromAddressValue,toAddressValue;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private LocationListener listener;
    private long UPDATE_INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 3000;

    private double latitudeValue = 0.0, longitudeValue = 0.0,pickupLatitudeValue = 0.0,pickupLongitudeValue = 0.0
    ,destinationLatitudeValue = 0.0,destinationLongitudeValue = 0.0,destlat,destlng;
    private MarkerOptions locationMarker;
    private Location mLastLocation;

    private String sPickUpAddress = "",sPickUpPostalCode="",sDestinationPostalCode="",destendpoint="0.00";
    private ScheduleData schedule = null;
    private Geocoder geoCoder = null;
    private ImageView back;
    private Marker mCurrLocationMarker;
    private RouteBroadCastReceiver routeReceiver;
//    private SettingsManager settingsManager ;
    private Button pickUpComplete,finishJob;

//    private JsonObjReq jsonRequest;
    private Map<String, String> params;
    private RequestQueue mQueue;
//    private UIHelper helper;
//    private com.sg.winapp.ambulance.driver.helper.ProgressDialog mProgressDialog;
    private Animation mAnimFadeIn;
    public static final int PERMISSION_LOCATION_REQUEST_CODE = 100;
    private boolean isPickUpCompleted = false,isPickupBtnClick= false;
    private SlidingMenu menu;
    private LinearLayout address_layout;
    private LatLng destination,latLng;
    private LinearLayout locationaddress_layout;
    AutoCompleteTextView atvPlaces;
    DownloadTask placesDownloadTask;
    DownloadTask placeDetailsDownloadTask;
    ParserTask placesParserTask;
    ParserTask placeDetailsParserTask;
    final int PLACES = 0;
    final int PLACES_DETAILS = 1;
    private static final LatLng CHENNAI = new LatLng(13.094993,80.282142);
    private static final LatLng SALEM = new LatLng(11.825964, 78.020043);
    private static final LatLng MADURAI = new LatLng(9.912227, 78.128314);
    private static final LatLng DESTINATION = new LatLng(11.921570, 79.821117);
    private ArrayList<ScheduleDataNew> LatLngArr;
    private String flag = "";
//    double mLatitude, mLongitude;
    LatLng current_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
          //  setContentView(R.layout.activity_route_map);
//            settingsManager = new SettingsManager(RouteMapActivity.this);
            //Button
            getActionBar().setIcon(R.drawable.ic_menu);
            Mint.initAndStartSession(RouteMapActivity.this, "29088aa0");
            setContentView(R.layout.activity_route_map);
            ActionBar ab = getSupportActionBar();
            ab.setHomeButtonEnabled(true);
            View customNav = LayoutInflater.from(this).inflate(
                    R.layout.slidemenu_actionbar_title, null);
            TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
            txt.setText("Route Map");
            search = (ImageButton) customNav.findViewById(R.id.search_img);
            im_date = (ImageButton) customNav.findViewById(R.id.date_image);
            printer = (ImageButton) customNav.findViewById(R.id.printer);
            route_btn = (ImageButton) customNav.findViewById(R.id.custcode_img);
            search.setVisibility(View.VISIBLE);
            route_btn.setVisibility(View.VISIBLE);
            route_btn.setImageResource(R.mipmap.ic_summ_location_press);
            search.setImageResource(R.mipmap.search_round);

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

            pickUpComplete = (Button) findViewById(R.id.pickUpCompleteBtn);
            finishJob = (Button) findViewById(R.id.finishJobBtn);
            //ImageView
            back = (ImageView) findViewById(R.id.back);
            //TextView
            fromAddressValue = (TextView) findViewById(R.id.fromAddress);
            toAddressValue = (TextView) findViewById(R.id.toAddress);
            distanceValue = (TextView) findViewById(R.id.distance_value);
            durationValue = (TextView) findViewById(R.id.duration_value);
            address_layout = (LinearLayout) findViewById(R.id.address_layout);
            locationaddress_layout = (LinearLayout) findViewById(R.id.locationaddress_layout);
            latLngList = new ArrayList<LatLng>();
            LatLngArr = new ArrayList<>();

//            mQueue = VolleyRequestQueue.getInstance(RouteMapActivity.this).getRequestQueue();
//            helper = new UIHelper(RouteMapActivity.this);
            //   latLngMoveList = new ArrayList<>();

            //OnClickListener
            back.setOnClickListener(this);
            pickUpComplete.setOnClickListener(this);
            finishJob.setOnClickListener(this);
            schedule = new ScheduleData();
            schedule.setStatus("SCHEDULE");
            Bundle b = getIntent().getExtras();
            if (b != null) {
                Log.d("bundle","bundle");
              //  schedule = b.getParcelable("SCHEDULE");
                //     status = b.getInt(STATUS);
                flag = b.getString("flagname");
                destlat = b.getDouble("destlat");
                destlng = b.getDouble("destlng");

                destendpoint = b.getString("destendpoint");
                LatLngArr = (ArrayList<ScheduleDataNew>) getIntent().getSerializableExtra("LatLngArr");

              //  Log.d("LatLngArrsize",""+LatLngArr.size());

                Log.d("latvalue",""+destlat);
                Log.d("lonvalue",""+destlng);
            }
//            if (schedule != null) {
//                sPickUpAddress = schedule.getPickupAddress1();
//                sPickUpPostalCode = schedule.getPickupPostalCode();
//                sDestinationPostalCode = schedule.getDesignationPostalCode();
//            }
//            if (schedule.getStatus().matches("ONBOARD")) {
//                pickUpComplete.setEnabled(false);
//                pickUpComplete.setBackgroundResource(R.mipmap.done_btn_disable);
//                isPickUpCompleted = true;
//                isPickupBtnClick = true;
//            }
            //fromAddressValue.setText(sPickUpAddress);
            toAddressValue.setText(sPickUpAddress);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                Log.d("above","above");
//                //  ActivityCompat.requestPermissions(RouteMapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
//                int hasLocationPermission = ActivityCompat.checkSelfPermission(RouteMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
//                ActivityCompat.requestPermissions((Activity) RouteMapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//                }else{
//                Log.d("else","else");
//            }

            checkLocation(); //check whether location service is enable or not in your  phone

            //showPermissionAlert();

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//
//                // Should we show an explanation?
//
//                ActivityCompat.requestPermissions(RouteMapActivity.this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        100);
//                ActivityCompat.requestPermissions(RouteMapActivity.this,
//                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                        200);
//
//                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
//                // app-defined int constant that should be quite unique
//            }
//        }

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    new AlertDialog.Builder(this)
                            .setTitle("Location")
                            .setMessage("Enable?")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(RouteMapActivity.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            100);
                                }
                            })
                            .create()
                            .show();


                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            100);
                }

            } else {

            }


//            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addApi(LocationServices.API)
                        .build();
//            }

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            mLocationRequest = createLocationRequest();
            routeReceiver = new RouteBroadCastReceiver();

            atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
            atvPlaces.setThreshold(3);

            geoCoder = new Geocoder(RouteMapActivity.this, Locale.getDefault());

            route_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(destendpoint.matches("0.00") || destlat == 0.0 && destlng == 0.0){
                        Toast.makeText(RouteMapActivity.this,"No destination address found. Please search the destination address above",Toast.LENGTH_SHORT).show();
                        locationaddress_layout.setVisibility(View.VISIBLE);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(atvPlaces, InputMethodManager.SHOW_IMPLICIT);
                        atvPlaces.requestFocus();
                    }else{
                        if(latLng != null) {
                            address_layout.setVisibility(View.VISIBLE);
                            String directionApiPath = Helper.getUrl(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude),
                                    String.valueOf(destination.latitude), String.valueOf(destination.longitude));
                          //  String directionApiPath = getMapsApiDirectionsUrl();
                          //  String directionApiPath = "https://maps.googleapis.com/maps/api/directions/json?origin=13.094993,80.282142&waypoints=optimize:true|13.048139,80.225694|13.117688,79.837991|&destination=12.969004,80.190911&sensor=false";
                            Log.d(TAG, "Path " + directionApiPath);

                            mMap.addMarker(new MarkerOptions()
                                    .title("Current Location")
                                    .position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                            getDirectionFromDirectionApiServer(directionApiPath, mMap);
                            showDestAddress(destlat, destlng);
                        }
                    }
                }
            });

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(locationaddress_layout.getVisibility() == View.VISIBLE){
                        locationaddress_layout.setVisibility(View.GONE);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);
                    }else{
                        locationaddress_layout.setVisibility(View.VISIBLE);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    }
                }
            });
            //getPickupAddressFromLatLog();
          //  getDestinationAddressFromLatLog();
          //  startRouteService();

            atvPlaces.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    // Creating a DownloadTask to download Google Places matching
                    // "s"
                    placesDownloadTask = new DownloadTask(PLACES);

                    // Getting url to the Google Places Autocomplete api
                    String url;
                    try {
                        url = getAutoCompleteUrl(s.toString());

                        // Start downloading Google Places
                        // This causes to execute doInBackground() of DownloadTask
                        // class
                        placesDownloadTask.execute(url);
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                }
            });

            // Setting an item click listener for the AutoCompleteTextView dropdown
            // list
            atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                        long id) {

                  //  map_layout.setVisibility(View.VISIBLE);

                    ListView lv = (ListView) arg0;
                    SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

                    HashMap<String, String> hm = (HashMap<String, String>) adapter
                            .getItem(index);

                    // Creating a DownloadTask to download Places details of the
                    // selected place
                    placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

                    // Getting url to the Google Places details api
                    String url = getPlaceDetailsUrl(hm.get("reference"));

                    // Start downloading Google Place Details
                    // This causes to execute doInBackground() of DownloadTask class
                    placeDetailsDownloadTask.execute(url);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);

                    adapter.notifyDataSetChanged();

                }
            });


            atvPlaces.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(atvPlaces.getText().length()>0){
                        atvPlaces
                                .setCompoundDrawablesWithIntrinsicBounds(
                                        0, 0,
                                        R.mipmap.ic_clear_btn, 0);

                        atvPlaces
                                .setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
                                        atvPlaces) {
                                    @Override
                                    public boolean onDrawableClick() {

                                        SOTDatabase.deleteVan();
                                        atvPlaces.setText("");
                                        atvPlaces
                                                .setCompoundDrawablesWithIntrinsicBounds(
                                                        0, 0,
                                                        0, 0);
                                        return true;

                                    }

                                });
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*private void updateJobStart(String bookingNo,String status){
        try {
            showProgressBar();
            params = new HashMap<>();
            params.put(COMPANYCODE_PARAM, "1");
            params.put(DRIVERSLNO_PARAM, "1");
            params.put(BOOKINGNO_PARAM, bookingNo);
            params.put(STATUS_PARAM, status);
            Uri mUrl = Helper.buildURI(URL, SAVEORUPDATEJOB, params);
            Log.d(BOTTOMSHEET_FRAGMENT_TAG, "URL: " + mUrl.toString());
            jsonRequest = new JsonObjReq(new JSONObject(), Request.Method.GET, mUrl.toString(), getResponseListener(status), getErrorListener()) {
            };
            jsonRequest.setTag(BOTTOMSHEET_FRAGMENT_TAG);
            mQueue.add(jsonRequest);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private Response.Listener<JSONObject> getResponseListener(final String status){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(BOTTOMSHEET_FRAGMENT_TAG, "Response: " + response.toString());
                    int length = response.length();
                    if(length>0){
                        String result = response.getString("Result");
                        if(result.equalsIgnoreCase("true")){

                            if(status.matches(ONBOARD)){
                                helper.showCrouton("Pickup Job is completed");
                                pickUpComplete.setEnabled(false);
                                pickUpComplete.setBackgroundResource(R.mipmap.done_btn_disable);
                                isPickUpCompleted = true;
                                refreshMap(mMap);
                                assignLocationValues();
                            }else if(status.matches(CLOSED)){
                                stopRouteService();
                                helper.showCrouton("Job is completed");
                                Intent i = new Intent(RouteMapActivity.this,PaymentActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }
                        }else{
                            helper.showCrouton("Unable to update Job Status");
                        }
                    }

                    hideProgressBar();
                }catch (Exception e){
                    e.printStackTrace();
                    hideProgressBar();
                    helper.showCrouton(getResources().getString(R.string.unable_to_update_job_status));
                }
            };
        };
    }
    private Response.ErrorListener getErrorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressBar();
                Log.d(BOTTOMSHEET_FRAGMENT_TAG, "Error: " + error.getMessage());
                helper.showCrouton(Helper.getErrorType(RouteMapActivity.this,error));
            };
        };
    }*/


  /*  public void showProgressBar(){
        mProgressDialog = com.sg.winapp.ambulance.driver.helper.ProgressDialog.show(RouteMapActivity.this,"Loading...", true,true,this);
        mProgressDialog.setCancelable(false);
        mAnimFadeIn = AnimationUtils.loadAnimation(RouteMapActivity.this, R.anim.fade_in);
        mAnimFadeIn = new AlphaAnimation(1.0f, 0.0f);
        mAnimFadeIn.setDuration(1000);
        mAnimFadeIn.setStartOffset(3000);
    }*/

    /*public void hideProgressBar(){
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }*/
    @Override
    public void onCancel(DialogInterface dialog) {
//        mProgressDialog.dismiss();
    }
    @Override
    public void onClick(View v) {
        String bookingNo = schedule.getBookingNo();
        switch (v.getId()){
            case R.id.back:
                Intent i = new Intent(RouteMapActivity.this, DeliveryOrderNewHeader.class);
                startActivity(i);
                RouteMapActivity.this.finish();
                break;
            case R.id.pickUpCompleteBtn:
              //  stopRouteService();
                isPickupBtnClick = true;
                if(bookingNo!=null && !bookingNo.isEmpty()){
//                    updateJobStart(bookingNo,ONBOARD);
                }else{
                    Toast.makeText(RouteMapActivity.this,"Unable to update the job status because booking No is Empty",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.finishJobBtn:
                if(isPickupBtnClick){
                    if(bookingNo!=null && !bookingNo.isEmpty()){
//                        updateJobStart(bookingNo,CLOSED);
                    }else{
                        Toast.makeText(RouteMapActivity.this,"Unable to update the job status because booking No is Empty",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RouteMapActivity.this,"Please complete the pickup complete job",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
    private void getPickupAddressFromLatLog(){
        List<Address> address = null;

        if (geoCoder != null) {
            try {
                address = geoCoder.getFromLocationName(sPickUpPostalCode, 10);
                if (address.size() > 0) {
                    Address first = address.get(0);
                    pickupLatitudeValue = first.getLatitude();
                    pickupLongitudeValue = first.getLongitude();
                    Log.d("lat","sPickUpPostal-->"+pickupLatitudeValue);
                    Log.d("lon","sPickUpPostal-->"+pickupLongitudeValue);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    private void getDestinationAddressFromLatLog(){
        List<Address> address = null;

        if (geoCoder != null) {
            try {
                address = geoCoder.getFromLocationName(sDestinationPostalCode, 10);
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

    private void startRouteService(){

        if(schedule.getStatus().matches("BOOKED")){
            Log.d(TAG, "SERVICE STARTED");
//            settingsManager.setServiceState(true);
            Intent intent = new Intent(RouteMapActivity.this, RouteMapService.class);
            intent.putExtra("SCHEDULE",schedule);
            startService(intent);
        }
    }
    private void stopRouteService(){
//            settingsManager.setServiceState(false);
            Intent intent = new Intent(RouteMapActivity.this, RouteMapService.class);
            stopService(intent);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
//    private void markStartingLocationOnMap(GoogleMap mapObject, LatLng location){
//        mapObject.addMarker(new MarkerOptions().position(location).title("Current location"));
//        mapObject.moveCamera(CameraUpdateFactory.newLatLng(location));
//    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("onConnected","onConnected");
        try {
            Log.d(TAG, "Connection method has been called");
//            if (isPickUpCompleted) {
//                Log.d("if","if");
//                assignLocationValues();
//            } else {
                Log.d("else","else");
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            Log.d("result", String.valueOf(result));
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(@NonNull LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        Log.d("status", String.valueOf(status));
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                Log.d("switch", "switch");
                                if (Build.VERSION.SDK_INT >= 23) {
                                    Log.d("aboveversion","aboveversion");
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                            && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                        if (mLastLocation != null) {
                                            latitudeValue = mLastLocation.getLatitude();
                                            longitudeValue = mLastLocation.getLongitude();
                                            Log.d(TAG, "Latitude 4: " + latitudeValue + " Longitude 4: " + longitudeValue);
                                            refreshMap(mMap);
                                            // markStartingLocationOnMap(mMap, new LatLng(latitudeValue, longitudeValue));
                                            // startPolyline(mMap, new LatLng(latitudeValue, longitudeValue));
                                            assignLocationValues(mLastLocation);
                                        }
                                    } else {
                                        Log.d("noper","noper");
                                        ActivityCompat.requestPermissions(RouteMapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
                                    }
                                } else {
                                    Log.d("lowversion","lowversion");
                                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                    if (mLastLocation != null) {
                                        latitudeValue = mLastLocation.getLatitude();
                                        longitudeValue = mLastLocation.getLongitude();
                                        Log.d(TAG, "Latitude 4: " + latitudeValue + " Longitude 4: " + longitudeValue);
                                        refreshMap(mMap);
                                        // markStartingLocationOnMap(mMap, new LatLng(latitudeValue, longitudeValue));
                                        // startPolyline(mMap, new LatLng(latitudeValue, longitudeValue));
                                        assignLocationValues(mLastLocation);
                                    }
                                }
                                break;

                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the user
                                // a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    status.startResolutionForResult(
                                            RouteMapActivity.this,
                                            // An arbitrary constant to disambiguate activity results.
                                            1);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Log.d("CHANGE_UNAVAILABLE","CHANGE_UNAVAILABLE");
                                break;
                        }
                    }
                });
           // }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 1:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made

                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if (mLastLocation != null) {
                            latitudeValue = mLastLocation.getLatitude();
                            longitudeValue = mLastLocation.getLongitude();
                            Log.d(TAG, "Latitude 4: " + latitudeValue + " Longitude 4: " + longitudeValue);
                            refreshMap(mMap);
                            // markStartingLocationOnMap(mMap, new LatLng(latitudeValue, longitudeValue));
                            // startPolyline(mMap, new LatLng(latitudeValue, longitudeValue));
                            assignLocationValues(mLastLocation);
                        }

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        if (!states.isGpsUsable()) {
                            // Degrade gracefully depending on what is available

                        }
                        break;
                    default:
                        break;
                }
                break;
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
    }

    private void assignLocationValues(Location currentLocation) {
        try {
            if (currentLocation != null) {
                latitudeValue = currentLocation.getLatitude();
                longitudeValue = currentLocation.getLongitude();
                latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                latLngList.add(latLng);
                Log.d(TAG, "Latitude: " + latitudeValue + " Longitude: " + longitudeValue);
                if(flag.equalsIgnoreCase("FullRoute")){
                    search.setVisibility(View.INVISIBLE);
                    route_btn.setVisibility(View.INVISIBLE);
                    address_layout.setVisibility(View.VISIBLE);
                    String directionApiPath = "";
                    if(LatLngArr.size()>1){
                        directionApiPath = getMapsApiDirectionsUrl();
                        showCurrentAddress(latLng.latitude, latLng.longitude);
                    }else{
                        Log.d("arraysize","low");
                        directionApiPath = Helper.getUrl(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude),
                                String.valueOf(LatLngArr.get(0).getLatitude()), String.valueOf(LatLngArr.get(0).getLongitude()));
                        LatLng latLngsource = new LatLng(latLng.latitude, latLng.longitude);
                        showCurrentAddress(latLng.latitude, latLng.longitude);
                        mMap.addMarker(new MarkerOptions()
                                .title("A")
                                .position(latLngsource).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        showDestAddress(LatLngArr.get(0).getLatitude(), LatLngArr.get(0).getLongitude());
                        LatLng latLng = new LatLng(LatLngArr.get(0).getLatitude(), LatLngArr.get(0).getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .title("B")
                                .position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    }
                    //  String directionApiPath = "https://maps.googleapis.com/maps/api/directions/json?origin=13.094993,80.282142&waypoints=optimize:true|13.048139,80.225694|13.117688,79.837991|&destination=12.969004,80.190911&sensor=false";
                    Log.d(TAG, "Path " + directionApiPath);
                    final LatLng currentlocation = new LatLng(LatLngArr.get(0).getLatitude(), LatLngArr.get(0).getLongitude());
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
//                            Toast.makeText(getApplicationContext(), "Latitude: " + latitudeValue + " Longitude: " + longitudeValue, Toast.LENGTH_SHORT).show();
                            handler.postDelayed(this, 60000);
                            float zoom = mMap.getCameraPosition().zoom;
                            Log.d("zoom level","zoom : "+zoom);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, zoom));
                        }
                    }, 60000);

                    getDirectionFromDirectionApiServer(directionApiPath, mMap);

                }else if(flag.equalsIgnoreCase("Normal")){
                    showCurrentAddress(latitudeValue, longitudeValue);
                    markStartingLocationOnMap(mMap, latLng);

                    if(destlat == 0.0 && destlng == 0.0) {
                        Log.d("destendpoint","0.00");
                    }else{
                        Log.d("Current location","updates");
                        current_location = new LatLng(latitudeValue, longitudeValue);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
//                            Toast.makeText(getApplicationContext(), "Latitude: " + latitudeValue + " Longitude: " + longitudeValue, Toast.LENGTH_SHORT).show();
                                handler.postDelayed(this, 60000);
                                float zoom = mMap.getCameraPosition().zoom;
                                Log.d("zoom level","zoom : "+zoom);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, zoom));
                            }
                        }, 60000);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void assignLocationValues() {
        try {
            latLngList.clear();
            latitudeValue = pickupLatitudeValue;
            longitudeValue = pickupLongitudeValue;
            latLng = new LatLng(latitudeValue, longitudeValue);
            latLngList.add(latLng);
            Log.d(TAG, "Latitude: " + latitudeValue + " Longitude: " + longitudeValue);
            showCurrentAddress(latitudeValue, longitudeValue);
            markStartingLocationOnMap(mMap, latLng);
          //  addCameraToMap(latLng);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void addCameraToMap(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(50)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
    public void showCurrentAddress(double lat,double lon){
        try {
            geoCoder = new Geocoder(RouteMapActivity.this, Locale.getDefault());
            if (geoCoder != null) {
                List<Address> addressList = null;
                addressList = geoCoder.getFromLocation(lat, lon, 1);
                if (addressList != null && addressList.size() > 0) {
                    Address mAddress = addressList.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mAddress.getMaxAddressLineIndex(); i++) {
                        sb.append(mAddress.getAddressLine(i)).append(",");
                    }
                    sb.append(mAddress.getLocality()).append(",");
                    sb.append(mAddress.getPostalCode()).append(",");
                    sb.append(mAddress.getCountryName());
                    fromAddressValue.setText(sb.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void markStartingLocationOnMap(GoogleMap mapObject, final LatLng location) {
        try {
//            mapObject.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            // mapObject.moveCamera(CameraUpdateFactory.newLatLng(location));

            Log.d("long size initial", "-->" + latLngList.size());
//            if (isPickUpCompleted) {
//                latLngList.add(new LatLng(destinationLatitudeValue, destinationLongitudeValue));
//            } else {
//                latLngList.add(new LatLng(pickupLatitudeValue, pickupLongitudeValue));
//            }
//            destlat = 13.011414;
//            destlng = 80.212481;
//            if (isPickUpCompleted) {
//                latLngList.add(new LatLng(destlat, destlng));
//            } else {
                Log.d("enter","enter");
                latLngList.add(new LatLng(destlat, destlng));
        //    }
            Log.d("long size", "-->" + latLngList.size());
            destination = latLngList.get(1);
            showDestAddress(destlat, destlng);

            if(destendpoint.matches("0.00") || destlat == 0.0 && destlng == 0.0){
                LatLng dest = new LatLng(1.347025, 103.866903);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dest, 10));
                Toast.makeText(RouteMapActivity.this,"No Address Found",Toast.LENGTH_SHORT).show();
            }else{
                if(destendpoint.matches("0.00")){
                    search.setVisibility(View.VISIBLE);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 10));
                }else{
                    search.setVisibility(View.INVISIBLE);
                    mMap.addMarker(new MarkerOptions()
                            .title("Delivery Point")
                            .position(destination)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 12));
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showDestAddress(double lat, double lon) {
        try {
            geoCoder = new Geocoder(RouteMapActivity.this, Locale.getDefault());
            if (geoCoder != null) {
                List<Address> addressList = null;
                addressList = geoCoder.getFromLocation(lat, lon, 1);
                if (addressList != null && addressList.size() > 0) {
                    Address mAddress = addressList.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mAddress.getMaxAddressLineIndex(); i++) {
                        sb.append(mAddress.getAddressLine(i)).append(",");
                    }
                    sb.append(mAddress.getLocality()).append(",");
                    sb.append(mAddress.getPostalCode()).append(",");
                    sb.append(mAddress.getCountryName());
                    toAddressValue.setText(sb.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getDirectionFromDirectionApiServer(String url, final GoogleMap mapObject) {
        //Showing a dialog till we get the route
        final ProgressDialog loading = ProgressDialog.show(this, "Getting Route", "Please wait...", false, false);
        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,createRequestSuccessListener(loading,mapObject),createRequestErrorListener(loading));
        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String getMapsApiDirectionsUrl() {
        int pos = LatLngArr.size()-1;
        int alphabet = 66;
        Log.d("pos", String.valueOf(pos));
        String detail_json = "";
        String origin = "origin=" + latLng.latitude + "," + latLng.longitude;
        LatLng latLngsource = new LatLng(latLng.latitude, latLng.longitude);
        mMap.addMarker(new MarkerOptions()
                .title("A")
                .position(latLngsource).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        String waypoints = "waypoints=optimize:true|";
        for (int i =0;i<LatLngArr.size()-1;i++) {
            detail_json +=  LatLngArr.get(i).getLatitude() + "," + LatLngArr.get(i).getLongitude() + "|" ;
            LatLng latLng = new LatLng(LatLngArr.get(i).getLatitude(), LatLngArr.get(i).getLongitude());
            String str = String.valueOf((char)alphabet);
            mMap.addMarker(new MarkerOptions()
                    .title(str)
                    .position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            alphabet++;
        }
        detail_json = detail_json.substring(0, detail_json.length() - 1);
        Log.d("Detail", detail_json);
        String destination = "destination=" + LatLngArr.get(pos).getLatitude() + "," + LatLngArr.get(pos).getLongitude();
        showDestAddress(LatLngArr.get(pos).getLatitude(), LatLngArr.get(pos).getLongitude());
        LatLng latLng = new LatLng(LatLngArr.get(pos).getLatitude(), LatLngArr.get(pos).getLongitude());
        String st = String.valueOf((char)alphabet);
        mMap.addMarker(new MarkerOptions()
                .title(st)
                .position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        String sensor = "sensor=false";
        String params = origin + "&" + waypoints + detail_json + "&"  + destination + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }

    private String getUrl() {
        String origin = "origin=" + CHENNAI.latitude + "," + CHENNAI.longitude;
        String waypoints = "waypoints=optimize:true|" + SALEM.latitude + "," + SALEM.longitude + "|"+ DESTINATION.latitude + "," + DESTINATION.longitude + "|";
        String destination = "destination=" + CHENNAI.latitude + "," + CHENNAI.longitude;
        String sensor = "sensor=false";
        String params = origin + "&" + waypoints + "&"  + destination + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }

    private Response.Listener<String> createRequestSuccessListener(final ProgressDialog loading,final GoogleMap mapObject) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    loading.dismiss();
                    //Calling the method drawPath to draw the path
                    Log.d("JSON Response", response.toString());
                    //  if (response.getStatus().equals("OK")) {
                    final JSONObject json = new JSONObject(response);
                    JSONArray routeArray = json.getJSONArray("routes");

                    JSONObject routes = routeArray.getJSONObject(0);
                    JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                    String encodedString = overviewPolylines.getString("points");

                    JSONArray legsArray = routes.getJSONArray("legs");
                    JSONObject legs = legsArray.getJSONObject(0);
                    JSONObject distance = legs.getJSONObject("distance");
                    String textDistance = distance.getString("text");
                    Log.d("distanceValue", "-->" + textDistance);
                    JSONObject duration = legs.getJSONObject("duration");
                    String textDuration = duration.getString("text");
                    Log.d("textDuration", "-->" + textDuration);

                    List<LatLng> list = decodePoly(encodedString);
                    drawRouteOnMap(mapObject,list);
                    setRouteDistanceAndDuration(textDistance,textDuration);

                    //   createMarker(latLng, latLngList.size());
                    //     mMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_end)));


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
    }
    private Response.ErrorListener createRequestErrorListener(final ProgressDialog loading) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                error.printStackTrace();
            }
        };
    }
    private void setRouteDistanceAndDuration(String distance, String duration) {
        distanceValue.setText(distance);
        durationValue.setText(duration);
    }
    private class RouteBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String local = intent.getExtras().getString("RESULT_CODE");
                double result_latitude = intent.getExtras().getDouble("RESULT_LATITUDE");
                double result_longitude = intent.getExtras().getDouble("RESULT_LONGITUDE");

                String msg = "Updated Location: " +
                        Double.toString(result_latitude) + "," +
                        Double.toString(result_longitude);

                Toast.makeText(RouteMapActivity.this, msg, Toast.LENGTH_SHORT).show();
                // You can now create a LatLng Object for use with maps
                LatLng latLng = new LatLng(result_latitude, result_longitude);


                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                MarkerOptions options = new MarkerOptions();
                options.position(getCoords(result_latitude, result_longitude));
                options.icon(BitmapDescriptorFactory.fromBitmap(getBitmap(result_latitude, result_longitude)));
                mCurrLocationMarker = mMap.addMarker(options);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                startPolyline(mMap, new LatLng(latitudeValue, longitudeValue));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void startPolyline(GoogleMap map, LatLng location){

        if(map == null){
            Log.d(TAG, "Map object is not null");
            return;
        }
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.add(location);
        Polyline polyline = map.addPolyline(options);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(12)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions){ // draw line

        PolylineOptions options = new PolylineOptions().width(8).color(Color.RED).geodesic(true);
        options.addAll(positions);
        Polyline polyline = map.addPolyline(options);
       /* polyline.(
                new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow),
                        16));*/
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(positions.get(0).latitude, positions.get(0).longitude)) // Sets the center of the map to current location
                .zoom(12)
//                .bearing(40) // Sets the orientation of the camera to east
                .tilt(0) // Sets the tilt of the camera to 0 degrees
                .build(); // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
    private void refreshMap(GoogleMap mapInstance){
        mapInstance.clear();
    }
    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(routeReceiver == null){
            routeReceiver = new RouteBroadCastReceiver();
        }
        IntentFilter filter = new IntentFilter(RouteMapService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(routeReceiver, filter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(routeReceiver);
    }
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    private static final double EARTH_RADIUS = 6378100.0;
    private int offset;

// 2. convert meters to pixels between 2 points in current zoom:

    private int convertMetersToPixels(double lat, double lng, double radiusInMeters) {

        double lat1 = radiusInMeters / EARTH_RADIUS;
        double lng1 = radiusInMeters / (EARTH_RADIUS * Math.cos((Math.PI * lat / 180)));

        double lat2 = lat + lat1 * 180 / Math.PI;
        double lng2 = lng + lng1 * 180 / Math.PI;

        Point p1 = mMap.getProjection().toScreenLocation(new LatLng(lat, lng));
        Point p2 = mMap.getProjection().toScreenLocation(new LatLng(lat2, lng2));

        return Math.abs(p1.x - p2.x);
    }
    private Bitmap getBitmap(double lat, double lng) {

        // fill color
        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setColor(0x110000FF);
        paint1.setStyle(Paint.Style.FILL);

        // stroke color
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(0xFF0000FF);
        paint2.setStyle(Paint.Style.STROKE);

        // icon
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_current);

        // circle radius - 200 meters
        int radius = offset = convertMetersToPixels(lat, lng, 200);

        // if zoom too small
        if (radius < icon.getWidth() / 2) {

            radius = icon.getWidth() / 2;
        }

        // create empty bitmap
        Bitmap b = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        // draw blue area if area > icon size
        if (radius != icon.getWidth() / 2) {

            c.drawCircle(radius, radius, radius, paint1);
            c.drawCircle(radius, radius, radius, paint2);
        }

        // draw icon
        c.drawBitmap(icon, radius - icon.getWidth() / 2, radius - icon.getHeight() / 2, new Paint());

        return b;
    }

// 4. calculate image offset:

    private LatLng getCoords(double lat, double lng) {

        LatLng latLng = new LatLng(lat, lng);

        Projection proj = mMap.getProjection();
        Point p = proj.toScreenLocation(latLng);
        p.set(p.x, p.y + offset);

        return proj.fromScreenLocation(p);
    }
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // permission was denied, show alert to explain permission
                    showPermissionAlert();
                } else {
                    //permission is granted now start a background service
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                          mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if (mLastLocation != null) {
                            latitudeValue = mLastLocation.getLatitude();
                            longitudeValue = mLastLocation.getLongitude();
                            Log.d(TAG, "Latitude 1: " + latitudeValue + " Longitude 1: " + longitudeValue);
                            assignLocationValues(mLastLocation);
                        }

                    }
                }
            }
        }
    }
    private void showPermissionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_request_title);
        builder.setMessage(R.string.app_permission_notice);
        builder.create();
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ActivityCompat.checkSelfPermission(RouteMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(RouteMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RouteMapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(RouteMapActivity.this, R.string.permission_refused, Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }
    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }
    private boolean isLocationEnabled() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private String getAutoCompleteUrl(String place)
            throws UnsupportedEncodingException {

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=" +getResources().getString(R.string.google_map_key);;

        // place to be be searched
        String input = "input=" + URLEncoder.encode(place, "utf8");

        // // place type to be searched
        // String types = "types=geocode";
        //
        // // Sensor enabled
        // String sensor = "sensor=false";

        // Building the parameters to the web service
        // String parameters = input+"&"+types+"&"+sensor+"&"+key;

        String parameters = input + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
                + output + "?" + parameters;

        return url;
    }

    private String getPlaceDetailsUrl(String ref) {

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key="+ getResources().getString(R.string.google_map_key);

        // reference of place
        String reference = "reference=" + ref;

        // // Sensor enabled
        // String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = reference + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/details/"
                + output + "?" + parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        private int downloadType = 0;

        // Constructor
        public DownloadTask(int type) {
            this.downloadType = type;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            switch (downloadType) {
                case PLACES:
                    // Creating ParserTask for parsing Google Places
                    placesParserTask = new ParserTask(PLACES);

                    // Start parsing google places json data
                    // This causes to execute doInBackground() of ParserTask class
                    placesParserTask.execute(result);

                    break;

                case PLACES_DETAILS:
                    // Creating ParserTask for parsing Google Places
                    placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

                    // Starting Parsing the JSON string
                    // This causes to execute doInBackground() of ParserTask class
                    placeDetailsParserTask.execute(result);
            }
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends
            AsyncTask<String, Integer, List<HashMap<String, String>>> {

        int parserType = 0;

        public ParserTask(int type) {
            this.parserType = type;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> list = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                switch (parserType) {
                    case PLACES:
                        PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeJsonParser.parse(jObject);
                        break;
                    case PLACES_DETAILS:
                        PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeDetailsJsonParser.parse(jObject);
                }

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            switch (parserType) {
                case PLACES:
                    String[] from = new String[] { "description" };
                    int[] to = new int[] { android.R.id.text1 };

                    // Creating a SimpleAdapter for the AutoCompleteTextView
                    SimpleAdapter adapter = new SimpleAdapter(getBaseContext(),
                            result, android.R.layout.simple_list_item_1, from, to);

                    // Setting the adapter
                    atvPlaces.setAdapter(adapter);

                    adapter.notifyDataSetChanged();

                    break;
                case PLACES_DETAILS:
                    HashMap<String, String> hm = result.get(0);

                    // Getting latitude from the parsed data
                    destlat = Double.parseDouble(hm.get("lat"));

                    // Getting longitude from the parsed data
                    destlng = Double.parseDouble(hm.get("lng"));

                    destendpoint="1";

                    locationaddress_layout.setVisibility(View.GONE);

                    // // Getting reference to the SupportMapFragment of the
                    // activity_main.xml
                    // SupportMapFragment fm = (SupportMapFragment)
                    // getSupportFragmentManager().findFragmentById(R.id.map);
                    //
                    // // Getting GoogleMap from SupportMapFragment
                    // googleMap = fm.getMap();

                    // LatLng point = new LatLng(mLatitude, mLongitude);
                    //
                    // CameraUpdate cameraPosition = CameraUpdateFactory
                    // .newLatLng(point);
                    // CameraUpdate cameraZoom = CameraUpdateFactory.zoomBy(4);
                    //
                    // // Showing the user input location in the Google Map
                    // googleMap.moveCamera(cameraPosition);
                    // googleMap.animateCamera(cameraZoom);
                    //
                    // MarkerOptions options = new MarkerOptions();
                    //
                    // googleMap.clear();
                    //
                    // options.position(point);
                    // options.title("Position");
                    // options.snippet("Latitude:" + mLatitude + ",Longitude:"
                    // + mLongitude);
                    //
                    // // Adding the marker in the Google Map
                    // googleMap.addMarker(options);

                    mMap.clear();

//                    mMap.addMarker(new MarkerOptions().position(latLng).
//                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    String cityName = "";
                    try {
                        Geocoder geocoder = new Geocoder(getBaseContext(),
                                Locale.getDefault());
                        List<Address> addresses;

                        addresses = geocoder.getFromLocation(destlat, destlng,
                                1);

                        if (addresses.size() > 0) {
                            cityName = addresses.get(0).getAddressLine(0);
                        }


                        destination = new LatLng(destlat, destlng);
                        mMap.addMarker(new MarkerOptions()
                                .title("Delivery Location" + "\n" + cityName)
                                .position(destination).
                                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                                .position(destination)
//                                .title((addresses.size() > 0) ? cityName
//                                        : "Position"));
//                                .snippet(
//                                        "Latitude:" + destlat + ",Longitude:"
//                                                + destlng))

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 15));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//                        String directionApiPath = Helper.getUrl(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude),
//                                String.valueOf(destination.latitude), String.valueOf(destination.longitude));
//                        Log.d(TAG, "Path " + directionApiPath);
//                        getDirectionFromDirectionApiServer(directionApiPath, mMap);
                        showDestAddress(destlat,destlng);
                        distanceValue.setText("");
                        durationValue.setText("");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(RouteMapActivity.this, DeliveryOrderNewHeader.class);
        startActivity(i);
        RouteMapActivity.this.finish();
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
