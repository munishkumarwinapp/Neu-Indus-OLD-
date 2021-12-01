package com.winapp.crm;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.winapp.SFA.R;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double InLatitude = 0, InLongitude = 0, OutLatitude = 0, OutLongitude = 0;
    private MarkerOptions markerIn,markerOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crm_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            InLatitude = b.getDouble("InLatitude");
            InLongitude = b.getDouble("InLongitude");
            OutLatitude = b.getDouble("OutLatitude");
            OutLongitude = b.getDouble("OutLongitude");
        }


        Log.d("InTime InLatitude", "-->" + InLatitude);
        Log.d("InTime InLongitude", "-->" + InLongitude);
        Log.d("OutTime OutLatitude", "-->" + OutLatitude);
        Log.d("OutTime OutLongitude", "-->" + OutLongitude);

      findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              finish();
          }
      });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        try {
            mMap = googleMap;

            // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
            // Changing map type
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //  googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

            // Showing / hiding your current location
            googleMap.setMyLocationEnabled(false);

            // Enable / Disable zooming controls
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            // Enable / Disable my location button
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            googleMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            googleMap.getUiSettings().setZoomGesturesEnabled(true);

            //Disable Map Toolbar:
            googleMap.getUiSettings().setMapToolbarEnabled(false);

            // Adding a marker
            if(InLatitude>0&&InLongitude>0){
               markerIn = new MarkerOptions().position(
                        new LatLng(InLatitude, InLongitude))
                        .title("InTime");
                markerIn.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }

            if(OutLatitude>0&&OutLongitude>0) {
                markerOut = new MarkerOptions().position(
                        new LatLng(OutLatitude, OutLongitude))
                        .title("OutTime");

                markerOut.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    if(InLatitude>0&&InLongitude>0){
                        googleMap.addMarker(markerIn).showInfoWindow();
                    }
                    if(OutLatitude>0&&OutLongitude>0) {
                        googleMap.addMarker(markerOut).showInfoWindow();
                    }


                }
            });

            CameraPosition camPos = new CameraPosition.Builder()
                    .target(getCenterCoordinate())
                    .zoom(17)
                    .build();
            CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
            mMap.animateCamera(camUpd3);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LatLng getCenterCoordinate() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if(InLatitude>0&&InLongitude>0){
            builder.include(new LatLng(InLatitude, InLongitude));
        }
        if(OutLatitude>0&&OutLongitude>0) {
            builder.include(new LatLng(OutLatitude, OutLongitude));
        }
        LatLngBounds bounds = builder.build();
        return bounds.getCenter();
    }
}
