package com.winapp.MapModules;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.winapp.adapter.UserAdapter;
import com.winapp.SFA.R;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.User;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.WebServiceClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsHome extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener , GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private DrawerLayout mDrawerLayout;
    private  ListView listView;
    private View mapView;
    private ArrayList<User> itemList;
    private UserAdapter adapter;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;
    String lat, lon;
    private LocationManager locationManager;
    private boolean isGPSEnabled = false, isNetworkEnabled = false;
    private String address1 = "", address2 = "";
    double mLatitude, mLongitude;
    private HashMap<String, String> param = new HashMap<String, String>();
    private JSONArray jsonArray=null;
    private String fncGetTrackingLastLocation = "fncGetTrackingLastLocation";
    private String fncGetTrackingRoute = "fncGetTrackingRoute";
    private String fncGetActivityRoute = "fncGetActivityRoute";
    private ArrayList<MarkerData> currentLocationArr = new ArrayList<MarkerData>();
    private ArrayList<MarkerData> trakingLocationArr = new ArrayList<MarkerData>();
    private ArrayList<MarkerData> activityLocationArr = new ArrayList<MarkerData>();
    private LinearLayout current_tab,route_tab,activity_tab;
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.maps_home_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listView = (ListView) findViewById(R.id.listview);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etDestination = (EditText) findViewById(R.id.etDestination);

        current_tab = (LinearLayout) findViewById(R.id.current_tab);
        route_tab = (LinearLayout) findViewById(R.id.route_tab);
        activity_tab  = (LinearLayout) findViewById(R.id.activity_tab);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        mapView = mapFragment.getView();
        mMap = mapFragment.getMap();

        //myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        //or myMap.getUiSettings().setAllGesturesEnabled(true);
//        mMap.setOnMarkerClickListener(this);

        mMap.setBuildingsEnabled(true);

        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(10,480,580,0);
        //or myMap.getUiSettings().setAllGesturesEnabled(true);
//        mMap.setOnMarkerClickListener(this);  // jump marker

        mMap.setBuildingsEnabled(true);
        // Getting LocationManager object
        ((TextView)findViewById(R.id.location_lbl)).setTextColor(Color.parseColor("#6aa447"));

        new fncGetTrackingLastLocation().execute();

        setWidget();

        findViewById(R.id.user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

        current_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new fncGetTrackingLastLocation().execute();
            }
        });

        route_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new fncGetTrackingRoute().execute();
            }
        });

        activity_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new fncGetActivityRoute().execute();
            }
        });

    }
    public void setWidget() {
        //Adding data to ArrayList
        itemList = new ArrayList<User>();
        itemList.add(new User("aa", false));
//        itemList.add(new User("Arun", false));
//        itemList.add(new User("John", false));
//        itemList.add(new User("Gokul", false));
//        itemList.add(new User("Vasim", false));
//        itemList.add(new User("Natarajan", false));
//        itemList.add(new User("Barakath", false));
//        itemList.add(new User("Gnana", false));
//        itemList.add(new User("Raj", false));
//        itemList.add(new User("Kumar", false));
        //Setup Adapter for Listview
        adapter = new UserAdapter(MapsHome.this, itemList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                //Item Selected from list
                adapter.setCheckBox(position);

            }
        });

    }

    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this,this, origin, destination).execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

      //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        /*mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition cp) {
                try {
                    mMap.clear();
                    LatLng latvalue = cp.target;
                    double lat = latvalue.latitude;
                    double lng = latvalue.longitude;
                    // mMap.addMarker(new MarkerOptions().position(cp.target));
                    mMap.addMarker(new MarkerOptions().position(cp.target)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_icon)));

                    Geocoder geocoder = new Geocoder(MapsHome.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(lat,lng, 1);
                    if (addresses.size() > 0) {
                        String  cityName = addresses.get(0).getAddressLine(0);
                        String  stateName = addresses.get(0).getAddressLine(1);

                        ((TextView) findViewById(R.id.addresss)).setText(cityName+""+"\n"+stateName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 12));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.DKGRAY).
                    width(5);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        //Make the marker bounce
        final Handler handler = new Handler();

        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;

        Projection proj = mMap.getProjection();
        final LatLng markerLatLng = marker.getPosition();
        Point startPoint = proj.toScreenLocation(markerLatLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });

        //return false; //have not consumed the event
        return true; //have consumed the event
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
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
//                new getServerDateTime(lati, longi, "1").execute();

                updateUI(lati,longi);
            }else {
                Log.d("gpsLocation", " null ");
//                new getServerDateTime(0, 0, "0").execute();
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

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        try {
//            getCustomerLocation(location.getLatitude(), location.getLongitude());
        }catch (Exception e){
            e.printStackTrace();
        }
        //Place current location marker
        /*LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }*/


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

    public void getCurrentLocation(double latitude, double longitude)
            throws IOException {
        mLatitude = latitude;
        mLongitude = longitude;

        MarkerOptions marker = new MarkerOptions();

        if (mLatitude>0 && mLongitude>0) {
//            map_layout.setVisibility(View.VISIBLE);
            String cityName = "",stateName="";
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(mLatitude,
                    mLongitude, 1);

            if (addresses.size() > 0) {
                cityName = addresses.get(0).getAddressLine(0);
                stateName = addresses.get(0).getAddressLine(1);
                // String countryName = addresses.get(0).getAddressLine(2);
            }
//            mMap.clear();
            LatLng latLng = new LatLng(mLatitude, mLongitude);
            mMap.addMarker(marker.position(latLng)
                    .position(latLng)
                    .title((addresses.size() > 0) ? cityName : "Position")
//                    .snippet("Latitude:" + mLatitude + ",Longitude:" + mLongitude));
                    .snippet(stateName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(20));

        }else{
//            map_layout.setVisibility(View.GONE);
            Toast.makeText(MapsHome.this, "No Location Found",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void getRouteLocation(double latitude, double longitude)
            throws IOException {
        mLatitude = latitude;
        mLongitude = longitude;
        MarkerOptions markerOptions = new MarkerOptions();

        if (mLatitude>0 && mLongitude>0) {
//            map_layout.setVisibility(View.VISIBLE);
            String cityName = "",stateName="";
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(mLatitude,
                    mLongitude, 1);

            if (addresses.size() > 0) {
                cityName = addresses.get(0).getAddressLine(0);
                stateName = addresses.get(0).getAddressLine(1);
                // String countryName = addresses.get(0).getAddressLine(2);
            }
//            mMap.clear();
            LatLng latLng = new LatLng(mLatitude, mLongitude);
            Marker marker = mMap.addMarker(markerOptions.position(latLng)
                    .position(latLng)
                    .title((addresses.size() > 0) ? cityName : "Position")
//                    .snippet("Latitude:" + mLatitude + ",Longitude:" + mLongitude));
                    .snippet(stateName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(25));

            // Instantiating the class PolylineOptions to plot polyline in the map
            PolylineOptions polylineOptions = new PolylineOptions();
            // Setting the color of the polyline
            polylineOptions.color(Color.GREEN);
            // Setting the width of the polyline
            polylineOptions.width(3);
            // Adding the taped point to the ArrayList
            points.add(latLng);
            // Setting points of polyline
            polylineOptions.addAll(points);
            // Adding the polyline to the map
            mMap.addPolyline(polylineOptions);
            // Adding the marker to the map
            mMap.addMarker(markerOptions);

//            animateMarker(marker,latLng,true);
        }else{
//            map_layout.setVisibility(View.GONE);
            Toast.makeText(MapsHome.this, "No Location Found",
                    Toast.LENGTH_SHORT).show();
        }
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

    private class fncGetTrackingLastLocation extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            param.clear();
            jsonArray=null;
            currentLocationArr.clear();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub

            try {
                String serverdate = SalesOrderSetGet.getServerDate();
                param.put("TrackDate","14/03/2017");

                jsonArray= WebServiceClass.parameterWebservice(param, fncGetTrackingLastLocation);
                if(jsonArray.length()>0){
                    int lengthJsonArr = jsonArray.length();
                    for (int i = 0; i < lengthJsonArr; i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        String UserCode = jsonobject.getString("UserCode");
                        String TrackDate = jsonobject.getString("TrackDate");
                        String Latitude = jsonobject.getString("Latitude");
                        String Longitude = jsonobject.getString("Longitude");

                        MarkerData pojo = new MarkerData();
                        pojo.setUserCode(UserCode);
                        pojo.setTrackDate(TrackDate);
                        pojo.setLatitude(Latitude);
                        pojo.setLongitude(Longitude);

                        currentLocationArr.add(pojo);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            try{
                mMap.clear();
                points.clear();
            for(int i = 0 ; i < currentLocationArr.size() ; i++ ) {

                String lat = currentLocationArr.get(i).getLatitude();
                String log = currentLocationArr.get(i).getLongitude();
                double Dlatitude=0,Dlongitude=0;
                if(lat!=null && !lat.isEmpty()){
                    Dlatitude = Double.parseDouble(lat);
                }

                if(log!=null && !log.isEmpty()){
                    Dlongitude = Double.parseDouble(log);
                }

                getCurrentLocation(Dlatitude, Dlongitude);/*, currentLocationArr.get(i).getTitle(), markersArray.get(i).getSnippet(), markersArray.get(i).getIconResID();*/
            }

        }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private class fncGetTrackingRoute extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            param.clear();
            jsonArray=null;
            trakingLocationArr.clear();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub

            try {
                String serverdate = SalesOrderSetGet.getServerDate();
                param.put("TrackDate","14/03/2017");
                param.put("UserCode","aa");

                jsonArray= WebServiceClass.parameterWebservice(param, fncGetTrackingRoute);
                if(jsonArray.length()>0){
                    int lengthJsonArr = jsonArray.length();
                    for (int i = 0; i < lengthJsonArr; i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        String UserCode = jsonobject.getString("UserCode");
                        String TrackDate = jsonobject.getString("TrackDate");
                        String Latitude = jsonobject.getString("Latitude");
                        String Longitude = jsonobject.getString("Longitude");

                        MarkerData pojo = new MarkerData();
                        pojo.setUserCode(UserCode);
                        pojo.setTrackDate(TrackDate);
                        pojo.setLatitude(Latitude);
                        pojo.setLongitude(Longitude);

                        trakingLocationArr.add(pojo);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            try{
                mMap.clear();
                points.clear();
                for(int i = 0 ; i < trakingLocationArr.size() ; i++ ) {

                    String lat = trakingLocationArr.get(i).getLatitude();
                    String log = trakingLocationArr.get(i).getLongitude();
                    double Dlatitude=0,Dlongitude=0;
                    if(lat!=null && !lat.isEmpty()){
                        Dlatitude = Double.parseDouble(lat);
                    }

                    if(log!=null && !log.isEmpty()){
                        Dlongitude = Double.parseDouble(log);
                    }

                    getRouteLocation(Dlatitude, Dlongitude);/*, currentLocationArr.get(i).getTitle(), markersArray.get(i).getSnippet(), markersArray.get(i).getIconResID();*/
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class fncGetActivityRoute extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            param.clear();
            jsonArray=null;
            activityLocationArr.clear();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub

            try {
                String companycode = SupplierSetterGetter.getCompanyCode();
                String serverdate = SalesOrderSetGet.getServerDate();
                param.put("CompanyCode",companycode);
                param.put("TrackDate","14/03/2017");
                param.put("UserCode","aa");

                jsonArray= WebServiceClass.parameterWebservice(param, fncGetActivityRoute);
                if(jsonArray.length()>0){
                    int lengthJsonArr = jsonArray.length();
                    for (int i = 0; i < lengthJsonArr; i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        String TranType = jsonobject.getString("TranType");
                        String InvoiceNo = jsonobject.getString("InvoiceNo");
                        String ModifyUser = jsonobject.getString("ModifyUser");
                        String Modifydate = jsonobject.getString("Modifydate");
                        String CustomerCode = jsonobject.getString("CustomerCode");
                        String CustomerName = jsonobject.getString("CustomerName");

                        String Latitude = jsonobject.getString("Latitude");
                        String Longitude = jsonobject.getString("Longitude");

                        MarkerData pojo = new MarkerData();
                        pojo.setTranType(TranType);
                        pojo.setInvoiceNo(InvoiceNo);
                        pojo.setModifyUser(ModifyUser);
                        pojo.setModifydate(Modifydate);
                        pojo.setCustomerCode(CustomerCode);
                        pojo.setCustomerName(CustomerName);
                        pojo.setLatitude(Latitude);
                        pojo.setLongitude(Longitude);

                        activityLocationArr.add(pojo);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            try{
                mMap.clear();
                points.clear();
                for(int i = 0 ; i < activityLocationArr.size() ; i++ ) {

                    String lat = activityLocationArr.get(i).getLatitude();
                    String log = activityLocationArr.get(i).getLongitude();
                    double Dlatitude=0,Dlongitude=0;
                    if(lat!=null && !lat.isEmpty()){
                        Dlatitude = Double.parseDouble(lat);
                    }

                    if(log!=null && !log.isEmpty()){
                        Dlongitude = Double.parseDouble(log);
                    }

                    getRouteLocation(Dlatitude, Dlongitude);/*, currentLocationArr.get(i).getTitle(), markersArray.get(i).getSnippet(), markersArray.get(i).getIconResID();*/
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {

        Intent i = new Intent(MapsHome.this, LandingActivity.class);
        startActivity(i);
        MapsHome.this.finish();

    }
}
