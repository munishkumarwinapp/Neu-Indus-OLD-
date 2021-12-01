package com.winapp.trackuser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.WebServiceClass;

public class AppLocationService extends Service implements  GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

	// get location
	Location mLastLocation;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	String lat, lon;
	private LocationManager locationManager;

	private boolean isGPSEnabled = false, isNetworkEnabled = false;
	private long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 5 meters
	private long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	private long TIME_INTERVEL; 
	private String address1 = "", address2 = "", mServerDateTime="", currentDate="", currentTime="", dayName="",valid_url="";
	private int interval;
	private JSONArray jsonArray=null;
	private HashMap<String, String> param = new HashMap<String, String>();
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d("Service", "onStartCommand");
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Getting LocationManager object
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		FWMSSettingsDatabase.init(AppLocationService.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new WebServiceClass(valid_url);
		SOTDatabase.init(AppLocationService.this);
		String db_interval = SOTDatabase.getTrackingInterval("1");
		if(db_interval != null && !db_interval.isEmpty()){
			double dintervel = Double.parseDouble(db_interval);
			interval = (int) dintervel;
			Log.d("interval", "->"+interval);
			TIME_INTERVEL = 1000 * 60 * interval;
			doWifiScan();
		}
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d("Service", "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.d("Service", "onCreate");
	}

	TimerTask scanTask;
	final Handler handler = new Handler();
	Timer t = new Timer();

	public void doWifiScan() {

		scanTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Log.d("TIMER", "Timer set");
						// appLocationService = new
						// AppLocationService(AndroidLocationActivity.this);
						/*Location gpsLocation = getLocation();
						if (gpsLocation != null) {
							double latitude = gpsLocation.getLatitude();
							double longitude = gpsLocation.getLongitude();
							// Log.d("Latitude", "->"+latitude);
							// Log.d("Longitude", "->"+longitude);

							boolean interntConnection = isNetworkConnected();
							if (interntConnection == true) {
								
								new getServerDateTime(latitude, longitude).execute();
								
							} else {
//								Toast.makeText(getApplicationContext(),
//										"No Internet Connection",
//										Toast.LENGTH_LONG).show();
							}
						}else{
							Log.d("Location", " null ");
//							turnGPSOn();
//							Toast.makeText(getApplicationContext(),
//									"GPS is OFF. Please turn ON",
//									Toast.LENGTH_LONG).show();
						}*/

						buildGoogleApiClient();

//						if (!mGoogleApiClient.isConnected())
//							mGoogleApiClient.connect();
					}
				});
			}
		};

		t.schedule(scanTask, 0, TIME_INTERVEL); // n minute

	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d("onConnected", "onConnected");

		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(100); // Update location every second
		int coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
		int fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

		if (coarseLocationPermission != PackageManager.PERMISSION_GRANTED || fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
			// We don't have permission so prompt the user
		}
		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);

		double lati=0,longi=0;

		if (mLastLocation != null) {
			lat = String.valueOf(mLastLocation.getLatitude());
			lon = String.valueOf(mLastLocation.getLongitude());


			if(lat!=null && !lat.isEmpty()){
				lati = mLastLocation.getLatitude();
			}

			if(lon!=null && !lon.isEmpty()){
				longi = mLastLocation.getLongitude();
			}
		}

		boolean interntConnection = isNetworkConnected();
		if (interntConnection == true) {

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if (isGPSEnabled == true) {
				Log.d("isGPSEnabled", ""+isGPSEnabled);
				new getServerDateTime(lati, longi, "1").execute();

				updateUI(lati,longi);
			}else {
				Log.d("gpsLocation", " null ");
				new getServerDateTime(0, 0, "0").execute();
				updateUI(0,0);
//					Toast.makeText(getApplicationContext(),
//							"GPS is OFF. Please turn ON",
//							Toast.LENGTH_LONG).show();
			}

		} else {
//			Toast.makeText(getApplicationContext(),
//					"No Internet Connection",
//					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onLocationChanged(Location location) {
		lat = String.valueOf(location.getLatitude());
		lon = String.valueOf(location.getLongitude());
//		updateUI();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		buildGoogleApiClient();
	}

	synchronized void buildGoogleApiClient() {
		try {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();

		if (!mGoogleApiClient.isConnected())
			mGoogleApiClient.connect();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	void updateUI(double lat,double lon) {
		try {
//			getAddress(lat, lon);
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	public void getAddress(double latitude, double longitude) throws Exception {
		address1 = "";
		address2 = "";
		Geocoder geocoder;
		List<Address> addresses;
		try {
			boolean interntConnection = isNetworkConnected();
			if (interntConnection == true) {
				geocoder = new Geocoder(this, Locale.getDefault());

				addresses = geocoder.getFromLocation(latitude, longitude, 1);
				if (addresses != null && addresses.size() > 0) {

					address1 = addresses.get(0).getAddressLine(0);
					address2 = addresses.get(0).getAddressLine(1);

					/*Address address = addresses.get(0);
					ArrayList<String> addressFragments = new ArrayList<String>();

					// Fetch the address lines using getAddressLine,
					// join them, and send them to the thread.
					for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
						addressFragments.add(address.getAddressLine(i));
					}*/

				}

//				Toast.makeText(
//						getApplicationContext(),
//						"Mobile Location : " + "\nLatitude: "
//								+ latitude + "\nLongitude: "
//								+ longitude + "\nAddress 1: "
//								+ address1 + "\nAddress 2: "
//								+ address2, Toast.LENGTH_LONG)
//						.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class getServerDateTime extends AsyncTask<String, Void, String> {
		String gpsStatus;
		double latitude, longitude;
		public getServerDateTime(double lat,double lon, String gps) {
			this.latitude = lat;
			this.longitude = lon;
			this.gpsStatus=gps;
	    }
		
	 	@Override
        protected void onPreExecute() {
	 		jsonArray=null;
	 		dayName="";
	 		mServerDateTime="";
	 		
        }
	 	
        @Override
        protected String doInBackground(String... params) {
                try {
                	param.clear();
                	OfflineSettingsManager offlinemanager = new OfflineSettingsManager(AppLocationService.this);
            		String comapanyCode = offlinemanager.getCompanyType();
                	
                	param.put("CompanyCode", comapanyCode);
                	jsonArray = WebServiceClass.parameterWebservice(param, "fncGetServerDateTime");
                	if(jsonArray.length()>0){
	                	JSONObject jsonobject = jsonArray.getJSONObject(0);
	                	mServerDateTime = jsonobject.getString("ServerDate");
	                	Log.d("mServerDateTime","->" +mServerDateTime);
						if(mServerDateTime != null && !mServerDateTime.isEmpty()){
//			        		 SimpleDateFormat dateformat = new SimpleDateFormat("dd/mm/yyyy HH:mm");  //  26/02/2016 15:02
			        		 StringTokenizer tokens = new StringTokenizer(mServerDateTime, " ");
			        		 currentDate = tokens.nextToken();
			        		 String myTime = tokens.nextToken();
			        		 
							 currentTime = new SimpleDateFormat("HH:mm:ss").format(new SimpleDateFormat("HH:mm").parse(myTime));
			        		 		 
							SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"); 
							Date myDate = datetimeFormat.parse(datetimeFormat.format(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(mServerDateTime)));
			             	dayName = new SimpleDateFormat("EE").format(myDate);
			             	Log.d("Current Day","str ->" +dayName);
						}
                	}
                	
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        	 if(dayName != null && !dayName.isEmpty()){
        		 Cursor mCursor = SOTDatabase.getuserTrackingMasterCursor();
        		 Log.d("mCursor.getCount() ","->" +mCursor.getCount());
        		 if (mCursor != null) {
        			    if (mCursor.moveToFirst()) {

               			 String CompanyCode="", UserName="", CurrentLocationTracking="", RouteTracking="", TrackingInterval="", Mon="",
               				     Tue="", Wed="", Thu="", Fri="", Sat="", Sun="", MonFromTime="", MonToTime="", TueFromTime="", TueToTime="",
               					 WedFromTime="", WedToTime="", ThuFromTime="", ThuToTime="", FriFromTime="", FriToTime="", SatFromTime="",
               					 SatToTime ="", SunFromTime="", SunToTime="", currentDay = "", fromTime ="", toTime ="";
               			 
               			 CompanyCode = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_COMPANYCODE)));
               			 UserName = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_USERNAME)));
               			 CurrentLocationTracking = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_CURRENTLOCATIONTRACKING)));
//               			 RouteTracking = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_ROUTETRACKING)));
               			 TrackingInterval = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_TRACKINGINTERVAL)));
               			 Mon = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_MON)));
               			 Tue = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_TUE)));
               			 Wed = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_WED)));
               			 Thu = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_THU)));
               			 Fri = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_FRI)));
               			 Sat = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_SAT)));
               			 Sun = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_SUN)));
               			 MonFromTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_MONFROMTIME)));
               			 MonToTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_MONTOTIME)));
               			 TueFromTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_TUEFROMTIME)));
               			 TueToTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_TUETOTIME)));
               			 WedFromTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_WEDFROMTIME)));
               			 WedToTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_WEDTOTIME)));
               			 ThuFromTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_THUFROMTIME)));
               			 ThuToTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_THUTOTIME)));
               			 FriFromTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_FRIFROMTIME)));
               			 FriToTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_FRITOTIME)));
               			 SatFromTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_SATFROMTIME)));
               			 SatToTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_SATTOTIME)));
               			 SunFromTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_SUNFROMTIME)));
               			 SunToTime = strValidate(mCursor.getString(mCursor.getColumnIndex(SOTDatabase.COLUMN_SUNTOTIME)));
               			 
               			 param.clear();
               			 param.put("CompanyCode", CompanyCode);
               			 param.put("UserCode", UserName);
               			 param.put("TrackDate", mServerDateTime);
               			 param.put("Latitude", latitude+"");
               			 param.put("Longitude", longitude+"");
						 param.put("GPSStatus", gpsStatus+"");

               			 if(CurrentLocationTracking.matches("True")){
               				 if(dayName.matches(SOTDatabase.COLUMN_MON)) {
               					 if(Mon.matches("True")){
               						 currentDay = "True";
               						 fromTime = MonFromTime;
               						 toTime = MonToTime;
               					 }else{
               						 currentDay="";
               					 }
               				 } else if(dayName.matches(SOTDatabase.COLUMN_TUE)) {
               					 if(Tue.matches("True")){
               						 currentDay = "True";
               						 fromTime = TueFromTime;
               						 toTime = TueToTime;
               					 }else{
               						 currentDay="";
               					 } 
               				 } else if(dayName.matches(SOTDatabase.COLUMN_WED)) {
       	     					if(Wed.matches("True")){
       	     						currentDay = "True";
       	       						 fromTime = WedFromTime;
       	       						 toTime = WedToTime;
       	     					} else{
       	     						currentDay="";
       	     					} 
       		 				 } else if(dayName.matches(SOTDatabase.COLUMN_THU)) {
       		 					if(Thu.matches("True")){
       		 						currentDay = "True";
       	       						 fromTime = ThuFromTime;
       	       						 toTime = ThuToTime;
       		 					} else{
       	     						currentDay="";
       	     					} 
       						 } else if(dayName.matches(SOTDatabase.COLUMN_FRI)) {
       							 if(Fri.matches("True")){
       								 currentDay = "True";
               						 fromTime = FriFromTime;
               						 toTime = FriToTime;
               					 } else{
        	     						currentDay="";
        	     					 } 
       						 } else if(dayName.matches(SOTDatabase.COLUMN_SAT)) {
       							 if(Sat.matches("True")){
       								 currentDay = "True";
               						 fromTime = SatFromTime;
               						 toTime = SatToTime;
               					 } else{
        	     						currentDay="";
        	     					 }  
       						 } else if(dayName.matches(SOTDatabase.COLUMN_SUN)) {
       							 if(Sun.matches("True")){
       								 currentDay = "True";
               						 fromTime = SunFromTime;
               						 toTime = SunToTime;
               					 } else{
        	     						currentDay="";
        	     					 } 
       						 }
               				 
               				 if(currentDay != null && !currentDay.isEmpty()){
       	        				 if((fromTime != null && !fromTime.isEmpty()) && (toTime != null && !toTime.isEmpty())){
       								 boolean withinTime =timeIntervel(fromTime, toTime, currentTime);
       								 if(withinTime){
       									 Log.d("within interval? ","-> " +withinTime);
       									 new saveTrackUser(param).execute();
       								 }else{
       									 Log.d("within interval? ","-> " +withinTime);
       								 }
       							 }else{
       								 new saveTrackUser(param).execute();
       							 } 
               				 }
               				  
               			 }
               			
               		 
        			    }
        			    mCursor.close();
        			}
        	 }
        }
    }
	
	private String strValidate(String value){
		String mValue = "";
		
		if(value != null && !value.isEmpty()){
			mValue= value;
		}
		return mValue;
	}
	
	private class saveTrackUser extends AsyncTask<String, Void, String> {
		HashMap<String, String> mParam = new HashMap<String, String>();
		String mResult="";
		public saveTrackUser(HashMap<String, String> mparam) {
			this.mParam = mparam;
	    }
		
	 	@Override
        protected void onPreExecute() {
	 		jsonArray=null;
        }
	 	
        @Override
        protected String doInBackground(String... params) {
                try {
                	
                	jsonArray=WebServiceClass.parameterWebservice(mParam, "fncTrackUserSave");
                	if(jsonArray.length()>0){
                    	JSONObject jsonobject = jsonArray.getJSONObject(0);
                    	mResult = jsonobject.getString("Result");
            		}
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        		if(mResult.matches("True")){
        			Log.d(" fncTrackUserSave ","Location saved ");
        		}else{
        			Log.d(" fncTrackUserSave ","Not Location saved ");
        		}
        	
//        	Toast.makeText(
//					getApplicationContext(),
//					"Mobile Location : " + "\nLatitude: "
//							+ latitude + "\nLongitude: "
//							+ longitude + "\nAddress 1: "
//							+ address1 + "\nAddress 2: "
//							+ address2, Toast.LENGTH_LONG)
//					.show();
        }

    }
	
	/*public Location getLocation() {

		try {
			// Getting LocationManager object
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				Log.d("provider", " null ");
				// no network provider is enabled
			} else {

				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, this);
					Log.d("GPS Enabled", "GPS Enabled");
					if (locationManager != null) {
						
						Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							Log.d("location", "not null");
							onLocationChanged(location);
							return location;
						}else{
							locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);
						}
					}
				} else if(isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 20000, 0, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						Location location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							Log.d("location", "not null");
							onLocationChanged(location);
							return location;
						}else{
							locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
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

	@Override
	public void onLocationChanged(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		try {
			getAddress(latitude, longitude);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void getAddress(double latitude, double longitude) throws Exception {
		address1 = "";
		address2 = "";
		Geocoder geocoder;
		List<Address> addresses;
		try {
			boolean interntConnection = isNetworkConnected();
			if (interntConnection == true) {
				geocoder = new Geocoder(this, Locale.getDefault());

				addresses = geocoder.getFromLocation(latitude, longitude, 1);
				if (addresses != null && addresses.size() > 0) {

					address1 = addresses.get(0).getAddressLine(0);
					address2 = addresses.get(0).getAddressLine(1);

				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}*/
	
	private boolean timeIntervel(String d1, String d2, String d3){
//		String from = "09:00:00";
//	    String to = "04:00:00";
//	    String current = "03:00:00";
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	    
	    boolean isSplit = false, isWithin = false;

	    Date from = null, to = null,  current = null;
	    try {
	    	from = sdf.parse(d1);
	    	to = sdf.parse(d2);
	    	current = sdf.parse(d3);
			isSplit = (to.compareTo(from) <= 0);
		    System.out.println("[split]: " +isSplit);

		    if (isSplit){
		        isWithin = (current.after(from) || current.before(to) || current.equals(from) || current.equals(to));
		    }else {
		        isWithin = (current.after(from) && current.before(to) || current.equals(from) || current.equals(to));
		    }
		    
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	    return isWithin;
	}

	private void turnGPSOn() {
//		String provider = Settings.Secure.getString(getContentResolver(),
//				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//
//		if (!provider.contains("gps")) { // if gps is disabled
//			final Intent poke = new Intent();
//			poke.setClassName("com.android.settings",
//					"com.android.settings.widget.SettingsAppWidgetProvider");
//			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
//			poke.setData(Uri.parse("3"));
//			sendBroadcast(poke);
//		}

		// //Enable GPS
		// Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		// intent.putExtra("enabled", true);
		// sendBroadcast(intent);
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	@Override
	public void onDestroy() {
		mGoogleApiClient.disconnect();
		Log.d("Service", "onDestroy");
		t.cancel();
		if(scanTask!=null){
			scanTask.cancel();
		}
		
	}

}
