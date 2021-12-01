package com.winapp.trackwithmap;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.winapp.helper.Constants;
import com.winapp.helper.SettingsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 11-Sep-17.
 */

public class RouteMapService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener ,Constants {
    private static final String TAG = RouteMapService.class.getSimpleName();
    public static final String ACTION = "com.sg.winapp.ambulance.driver.service.RouteService";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private double latitudeValue = 0.0,longitudeValue = 0.0;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 100;
//    private SettingsManager settingsManager;
    private long startTimeInMilliSeconds = 0L;
    private boolean isServiceRunning = false;
    private ScheduleData schedule = null;
    private int repeatInterval = 30000; // 10 sec
    private Timer repeatTask = new Timer();
    @Override
    public void onCreate() {
        super.onCreate();
//        settingsManager = new SettingsManager(getApplicationContext());
        if(isRouteTrackingOn()){
            startTimeInMilliSeconds = System.currentTimeMillis();
            Log.d(TAG, "Current time " + startTimeInMilliSeconds);
            Log.d(TAG, "Service is running");
        }

        mLocationRequest = createLocationRequest();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
        Log.d(TAG, "Service is Started");

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isServiceRunning = true;
        Toast.makeText(this, "Starting..", Toast.LENGTH_SHORT).show();
        schedule = intent.getParcelableExtra("SCHEDULE");
        Log.d("Booking No ", "Starting-->" + schedule.getBookingNo());
//        pushDriverLocality();
        return Service.START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        throw null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Log.d(TAG, "Connection method has been called");
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                if (mLastLocation != null) {
                                    latitudeValue = mLastLocation.getLatitude();
                                    longitudeValue = mLastLocation.getLongitude();
                                    Log.d(TAG, "Latitude 1: " + latitudeValue + " Longitude 1: " + longitudeValue);
                                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, RouteMapService.this);
                                }
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Log.d(TAG, "Latitude ! " + location.getLatitude() + " Longitude 1 " + location.getLongitude());
            Log.d(TAG, "SERVICE RUNNING " + isServiceRunning);
            if (isRouteTrackingOn() && startTimeInMilliSeconds == 0) {
                startTimeInMilliSeconds = System.currentTimeMillis();
            }
            if (isRouteTrackingOn() && startTimeInMilliSeconds > 0) {
                latitudeValue = location.getLatitude();
                longitudeValue = location.getLongitude();

                Log.d(TAG, "Latitude 2 " + latitudeValue + " Longitude 2: " + longitudeValue);
               // ClassExecutingTask executingTask = new ClassExecutingTask(latitudeValue,longitudeValue);
               // executingTask.start();


                // send local broadcast receiver to application components
                Intent localBroadcastIntent = new Intent(ACTION);
                localBroadcastIntent.putExtra("RESULT_CODE", "LOCAL");
                localBroadcastIntent.putExtra("RESULT_LATITUDE", latitudeValue);
                localBroadcastIntent.putExtra("RESULT_LONGITUDE", longitudeValue);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localBroadcastIntent);
                long timeoutTracking = 2 * 60 * 60 * 1000;
                if (System.currentTimeMillis() >= startTimeInMilliSeconds + timeoutTracking) {
                    //turn of the tracking
//                    settingsManager.setServiceState(false);
                    Log.d(TAG, "SERVICE HAS BEEN STOPPED");

                    this.stopSelf();
                }
            }
            if (!isRouteTrackingOn()) {
                Log.d(TAG, "SERVICE HAS BEEN STOPPED 1");
                isServiceRunning = false;
                Log.d(TAG, "SERVICE STOPPED " + isServiceRunning);

                this.stopSelf();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
   private boolean isRouteTrackingOn(){
//        Log.d(TAG, "SERVICE STATE " + settingsManager.getServiceState());
//        return settingsManager.getServiceState();
       return true;
    }
    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    /*private void pushDriverLocality(){
        repeatTask.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!isRouteTrackingOn()) {
                        if(repeatTask != null){
                            Log.d(TAG, "SERVICE <-> " + "STOP");
                            repeatTask.cancel();
                        }
                    }
                    System.out.println(" ----This message will print every 30 seconds.---");
                    if(latitudeValue>0 && longitudeValue>0) {
                        String latitude = String.valueOf(latitudeValue);
                        String longitude = String.valueOf(longitudeValue);
                        Log.d(TAG, "SERVICE latitude-< " + latitude);
                        Log.d(TAG, "SERVICE longitude--< " + longitude);
                        System.out.println("This message will print every 30 seconds.");
                    // Here do something
                    // This task will run every 30 sec repeat
                    try {
                        RequestQueue mQueue = VolleyRequestQueue.getInstance(RouteMapService.this).getRequestQueue();
                        HashMap params = new HashMap<>();
                        JSONObject json = new JSONObject();
                        json.put(COMPANYCODE_PARAM, "1");
                        json.put(DRIVERSLNO_PARAM, "1");
                        json.put(BOOKINGNO_PARAM, schedule.getBookingNo());
                        json.put(LATITUDE_PARAM, latitude);
                        json.put(LONGITUDE_PARAM, longitude);
                        json.put(COMMENTS_PARAM, "");
                        Log.d(PAYMENT_REQUEST_TAG, "json: " + json.toString());
                         params.put(DETAILS_PARAM,json.toString());
                        Uri mUrl = Helper.buildURI(URL, PUSHDRIVERLOCALITY, params);
                        Log.d(PAYMENT_REQUEST_TAG, "URL--: " +mUrl.toString());
                        JsonObjReq jsonRequest = new JsonObjReq(new JSONObject(), Request.Method.GET, mUrl.toString(), getResponseListener(), getErrorListener()) {
                        };
                        jsonRequest.setTag(ROUTEMAPSERVICE_REQUEST_TAG);
                        mQueue.add(jsonRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    }
                }
            }, 0, repeatInterval);




    }
    private Response.Listener<JSONObject> getResponseListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(ROUTEMAPSERVICE_REQUEST_TAG, "Response: " + response.toString());
                    String result = response.getString("Result");
                    if(result.equalsIgnoreCase("true")){
                        Log.d(ROUTEMAPSERVICE_REQUEST_TAG, "Success: " + result);
                    }else{
                        Log.d(ROUTEMAPSERVICE_REQUEST_TAG, "Failed: " + result);
                    }

                }catch (Exception e){
                    e.printStackTrace();

                }
            };
        };
    }
    private Response.ErrorListener getErrorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(ROUTEMAPSERVICE_REQUEST_TAG, "Error: " + error.getMessage());
            };
        };
    }*/

    public class ClassExecutingTask {

        long delay = 100 * 1000; // delay in milliseconds
        LoopTask task = new LoopTask();
        Timer timer = new Timer("TaskName");
        private double latitudeValue =0.0,longitudeValue=0.0;
        public ClassExecutingTask(double latitudeValue, double longitudeValue) {
            this.longitudeValue = longitudeValue;
            this.latitudeValue = latitudeValue;
        }

        public void start() {
            timer.cancel();
            timer = new Timer("TaskName");
            Date executionDate = new Date(); // no params = now
            timer.scheduleAtFixedRate(task, executionDate, delay);
        }

        private class LoopTask extends TimerTask {
            public void run() {
                System.out.println("This message will print every 10 seconds.");
                //pushDriverLocality(String.valueOf(latitudeValue),String.valueOf(longitudeValue));
            }
        }
    }
}
