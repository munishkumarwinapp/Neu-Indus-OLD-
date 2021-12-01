package com.winapp.googlemaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.sot.WebServiceClass;
import com.winapp.sotdetails.CustomerAddress;
import com.winapp.sotdetails.CustomerListActivity;

public class MapActivity extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace, LocationListener {

	AutoCompleteTextView atvPlaces;
	DownloadTask placesDownloadTask;
	DownloadTask placeDetailsDownloadTask;
	ParserTask placesParserTask;
	ParserTask placeDetailsParserTask;
	GoogleMap googleMap;
	SlidingMenu menu;
	final int PLACES = 0;
	final int PLACES_DETAILS = 1;
	TextView title;
	ImageButton save_location,current_location_btn,location_icon;
	AutoCompleteTextView actv_search;
	HashMap<String, String> customerLocation = new HashMap<String, String>();
	HashMap<String, String> CustomerOutletAddress = new HashMap<String, String>();
	double mLatitude, mLongitude;
	String CustomerCode = "", CustomerName = "", DeliveryCode="";
	LinearLayout locationaddress_layout,map_layout;
	private LocationManager locationManager;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(R.drawable.ic_menu);
		setContentView(R.layout.map_activity);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.customer_list_title, null);
		title = (TextView) customNav.findViewById(R.id.customer_Title);
		save_location = (ImageButton) customNav.findViewById(R.id.search);
		
		actv_search = (AutoCompleteTextView) customNav
				.findViewById(R.id.auto_edit_search);
		location_icon = (ImageButton) customNav.findViewById(R.id.newcustomerbutton);
		map_layout= (LinearLayout)findViewById(R.id.map_layout);
		actv_search.setVisibility(View.GONE);
		location_icon.setVisibility(View.GONE);
		
		title.setText("Customer Location");

		save_location.setImageResource(R.mipmap.save_ic);
//		location_icon.setImageResource(R.drawable.ic_location);

		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayHomeAsUpEnabled(false);
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		// menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);

		// Getting reference to the SupportMapFragment of the activity_main.xml
		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		// Getting GoogleMap from SupportMapFragment
		googleMap = fm.getMap();

		// Getting a reference to the AutoCompleteTextView
		atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
		atvPlaces.setThreshold(3);

		customerLocation.clear();
		CustomerOutletAddress.clear();
		
		current_location_btn = (ImageButton) findViewById(R.id.current_location_btn);
		locationaddress_layout= (LinearLayout) findViewById(R.id.locationaddress_layout);
		
		
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Log.e("extras", "Extra NULL");
			Toast.makeText(MapActivity.this, "No Location Found",
					Toast.LENGTH_SHORT).show();
		} else {
			customerLocation = (HashMap<String, String>) getIntent()
					.getSerializableExtra("CustomerLocation");
			CustomerOutletAddress = (HashMap<String, String>) getIntent()
					.getSerializableExtra("CustomerOutletAddress");
			
			
			if (customerLocation!=null && !customerLocation.isEmpty()) {

				CustomerCode = customerLocation.get("CustomerCode");
				CustomerName = customerLocation.get("CustomerName");
				String Latitude = customerLocation.get("Latitude");
				String Longitude = customerLocation.get("Longitude");

				title.setTextSize(16);
				title.setText(""+CustomerName);
				
				if (Latitude != null && !Latitude.isEmpty()
						&& Longitude != null && !Longitude.isEmpty()) {

					mLatitude = Double.parseDouble(Latitude);
					mLongitude = Double.parseDouble(Longitude);

					try {
						getCustomerLocation(mLatitude, mLongitude);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					Toast.makeText(MapActivity.this, "No Location Found",
							Toast.LENGTH_SHORT).show();
				}
			}else if(CustomerOutletAddress!=null && !CustomerOutletAddress.isEmpty()){
				CustomerCode = CustomerOutletAddress.get("CustomerCode");
				CustomerName = CustomerOutletAddress.get("CustomerName");
				DeliveryCode = CustomerOutletAddress.get("DeliveryCode");
				String Latitude = CustomerOutletAddress.get("Latitude");
				String Longitude = CustomerOutletAddress.get("Longitude");

				title.setText(""+CustomerName);
				
				if (Latitude != null && !Latitude.isEmpty()
						&& Longitude != null && !Longitude.isEmpty()) {

					mLatitude = Double.parseDouble(Latitude);
					mLongitude = Double.parseDouble(Longitude);

					try {
						getCustomerLocation(mLatitude, mLongitude);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					Toast.makeText(MapActivity.this, "No Location Found",
							Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(MapActivity.this, "No Location Found",
						Toast.LENGTH_SHORT).show();
			}

		}

		current_location_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getCurrentlocation();
			}
		});

		save_location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SaveLocation task = new SaveLocation();
				task.execute();

			}
		});
		
//		location_icon.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				InputMethodManager inputMethodManager = (InputMethodManager) MapActivity.this
//						.getSystemService(Context.INPUT_METHOD_SERVICE);
//
//				if (locationaddress_layout.getVisibility() == View.VISIBLE) {
//					locationaddress_layout.setVisibility(View.GONE);
//					
//				} else {
//					locationaddress_layout.setVisibility(View.VISIBLE);
//				}
//				
//			}
//		});

		// Adding textchange listener
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
		atvPlaces.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long id) {

				map_layout.setVisibility(View.VISIBLE);
				
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
	}

	public void getCurrentlocation() {

		map_layout.setVisibility(View.VISIBLE);
		
		atvPlaces.setText("");

		googleMap.clear();

		googleMap.setMyLocationEnabled(true);
		
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

	public void getCustomerLocation(double latitude, double longitude)
			throws IOException {
		mLatitude = latitude;
		mLongitude = longitude;
		
		if (mLatitude>0 && mLongitude>0) {
			map_layout.setVisibility(View.VISIBLE);
			String cityName = "";
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(mLatitude,
					mLongitude, 1);

			if (addresses.size() > 0) {
				cityName = addresses.get(0).getAddressLine(0);
				// String stateName = addresses.get(0).getAddressLine(1);
				// String countryName = addresses.get(0).getAddressLine(2);
			}
			googleMap.clear();
			LatLng latLng = new LatLng(mLatitude, mLongitude);
			googleMap.addMarker(new MarkerOptions().position(latLng)
					.position(latLng)
					.title((addresses.size() > 0) ? cityName : "Position")
					.snippet("Latitude:" + mLatitude + ",Longitude:" + mLongitude));
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
		}else{
			map_layout.setVisibility(View.GONE);
			Toast.makeText(MapActivity.this, "No Location Found",
					Toast.LENGTH_SHORT).show();
		}	
	}

	@Override
	public void onLocationChanged(Location location) {
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		
		Log.d("Lat ", mLatitude +" Long " + mLongitude);
		try {
			String cityName = "";
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> addresses;

			addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);

			if (addresses.size() > 0) {
				cityName = addresses.get(0).getAddressLine(0);
			}

			LatLng latLng = new LatLng(mLatitude, mLongitude);
			googleMap.clear();
			googleMap.addMarker(new MarkerOptions()
					.position(latLng)
					.position(latLng)
					.title((addresses.size() > 0) ? cityName : "Position")
					.snippet(
							"Latitude:" + mLatitude + ",Longitude:"
									+ mLongitude));
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
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
				mLatitude = Double.parseDouble(hm.get("lat"));

				// Getting longitude from the parsed data
				mLongitude = Double.parseDouble(hm.get("lng"));

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

				googleMap.clear();
				
				String cityName = "";
				try {
					Geocoder geocoder = new Geocoder(getBaseContext(),
							Locale.getDefault());
					List<Address> addresses;

					addresses = geocoder.getFromLocation(mLatitude, mLongitude,
							1);

					if (addresses.size() > 0) {
						cityName = addresses.get(0).getAddressLine(0);
					}

					LatLng latLng = new LatLng(mLatitude, mLongitude);
					googleMap.addMarker(new MarkerOptions()
							.position(latLng)
							.position(latLng)
							.title((addresses.size() > 0) ? cityName
									: "Position")
							.snippet(
									"Latitude:" + mLatitude + ",Longitude:"
											+ mLongitude));
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
					googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
	}

	private class SaveLocation extends AsyncTask<String, Void, Void> {
		String mResult;
		HashMap<String, String> mParam = new HashMap<String, String>();
		JSONArray jsonArray;

		@Override
		protected void onPreExecute() {
			mParam.clear();
			jsonArray = null;
			mResult = "";
		}

		@Override
		protected Void doInBackground(String... params) {
			try {

				String username = SupplierSetterGetter.getUsername();
				String companycode = SupplierSetterGetter.getCompanyCode();

				mParam.put("CompanyCode", companycode);
				mParam.put("CustomerCode", CustomerCode);
				if(CustomerOutletAddress!=null && !CustomerOutletAddress.isEmpty()){
				mParam.put("DeliveryCode", DeliveryCode);
				}
				mParam.put("Latitude", "" + mLatitude);
				mParam.put("Longitude", "" + mLongitude);
				mParam.put("User", username);

				if(CustomerOutletAddress!=null && !CustomerOutletAddress.isEmpty()){
					jsonArray = WebServiceClass.parameterWebservice(mParam,
							"fncSaveCustomerAddressLocation");
				}else{
					jsonArray = WebServiceClass.parameterWebservice(mParam,
							"fncSaveCustomerLocation");
				}
				
				int lengthJsonArr = jsonArray.length();
				if (lengthJsonArr > 0) {
					for (int i = 0; i < lengthJsonArr; i++) {
						JSONObject jsonChildNode = jsonArray.getJSONObject(i);
						mResult = jsonChildNode.optString("Result").toString();

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (mResult.matches("True")) {
				Toast.makeText(MapActivity.this, "Location Saved",
						Toast.LENGTH_SHORT).show();
				
				if(CustomerOutletAddress!=null && !CustomerOutletAddress.isEmpty()){
					
					Intent in = new Intent(MapActivity.this, CustomerAddress.class);
					in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					in.putExtra("CustomerCode", CustomerCode);
					in.putExtra("CustomerName", CustomerName);
					startActivity(in);
					MapActivity.this.finish();
				
				}else{
					Intent i = new Intent(MapActivity.this,
							CustomerListActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					MapActivity.this.finish();
				}
			
			} else {
				Toast.makeText(getApplicationContext(), "Location Not Saved",
						Toast.LENGTH_SHORT).show();
			}

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
		if (customerLocation!=null && !customerLocation.isEmpty()) {
//			Intent i = new Intent(MapActivity.this, CustomerListActivity.class);
//			startActivity(i);
			MapActivity.this.finish();
		}else if(CustomerOutletAddress!=null && !CustomerOutletAddress.isEmpty()){
			MapActivity.this.finish();
		}
		
	
	}
}