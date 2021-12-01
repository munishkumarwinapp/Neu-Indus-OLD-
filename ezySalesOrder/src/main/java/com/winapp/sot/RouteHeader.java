package com.winapp.sot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.MapModules.Route;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.model.Product;
import com.winapp.printer.PDFActivity;
import com.winapp.sotdetails.CustomerListActivity;
import com.winapp.sotdetails.SOTdetailsGetSet;

public class RouteHeader extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace,Constants, LocationListener {

	ListView list;
	ProgressBar progressBar;
	LinearLayout spinnerLayout, offlineLayout, routeheader_layout;
	SlidingMenu menu;

	String valid_url = "";
	String cust_id = "", intentString, serverdate = "", currencyCode = "",
			currencyName = "", currencyRate = "", custName = "",consignmentStr="";
	String deliveryOrderStr, salesOrderStr,  invoiceStr, receiptsStr;
	private ArrayList<String> cstmrgrpcdal = new ArrayList<String>();
	private ArrayList<String> alclcrrncy = new ArrayList<String>();
	ArrayList<SOTdetailsGetSet> ListArray = new ArrayList<SOTdetailsGetSet>();
	RouteAdapter Adapter;
	double setLatitude, setLongitude;

	// get location
	Location mLastLocation;
	private GoogleApiClient mGoogleApiClient;
	String lat, lon;
	String  address1 = "", address2 = "";
	private boolean isGPSEnabled = false, isNetworkEnabled = false;
	public static final int SIGNATURE_ACTIVITY = 2;
	private static final int PICK_FROM_CAMERA = 1;
	String sign_img="",product_img="";
	ImageView sig_image,cam_image;
	Handler handler;
	int count;
	EditText location_txt;
	private LocationRequest mLocationRequest;
	private LocationManager locationManager;
	private String mCurrentgetPath="",mCurrentPhotoPath="";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.route_header);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Route");

		valid_url = FWMSSettingsDatabase.getUrl();
		new SOTSummaryWebService(valid_url);

		ImageButton printer = (ImageButton) customNav.findViewById(R.id.printer);
		ImageButton custsearchIcon = (ImageButton) customNav.findViewById(R.id.custcode_img);
		ImageButton search_img = (ImageButton) customNav.findViewById(R.id.search_img);		
		printer.setVisibility(View.GONE);
		custsearchIcon.setVisibility(View.GONE);
		search_img.setVisibility(View.INVISIBLE);

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
		SOTDatabase.init(RouteHeader.this);
		sign_img = SOTDatabase.getSignatureImage();
		handler = new Handler();

//		alertVisitedDialog();


		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String invnumber = getIntent().getStringExtra("InvoiceNo");
			String companyCode = SupplierSetterGetter.getCompanyCode();
			if(invnumber!=null && !invnumber.isEmpty()){
				new PDFActivity(RouteHeader.this,valid_url+"/"+FNCA4INVOICEGENERATE+"?CompanyCode="+companyCode+"&sInvoiceNo="+invnumber, "report.pdf").execute();
			}
		}

		FWMSSettingsDatabase.init(RouteHeader.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new WebServiceClass(valid_url);
		new SalesProductWebService(valid_url);
		new SalesOrderWebService(valid_url);
		new DateWebservice(valid_url,RouteHeader.this);
		offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);
		routeheader_layout = (LinearLayout) findViewById(R.id.routeheader_layout);
		list = (ListView) findViewById(R.id.list);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//		buildGoogleApiClient();

//		if (!mGoogleApiClient.isConnected())
//			mGoogleApiClient.connect();
		
		RoutesyncCall routeservice = new RoutesyncCall();
		routeservice.execute();

		registerForContextMenu(list);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		SOTdetailsGetSet so = Adapter.getItem(info.position);

		cust_id = so.getCust_Code().toString();
		custName = so.getCust_Name().toString();
		menu.setHeaderTitle(custName);
		SalesOrderSetGet.setCustomercode(cust_id);
		ConvertToSetterGetter.setEdit_do_no("");
//		menu.add(0, v.getId(), 0, "Edit");

		salesOrderStr = FormSetterGetter.getSalesOrder();
		deliveryOrderStr = FormSetterGetter.getDeliveryOrder();
		invoiceStr = FormSetterGetter.getAddInvoice();
		receiptsStr = FormSetterGetter.getReceipts();
		consignmentStr = FormSetterGetter.getConsignment();
			if (salesOrderStr.matches("Sales Order")) {
				menu.add(0, v.getId(), 0, "Sales Order");
			}
			if (deliveryOrderStr.matches("Delivery Order")) {
				menu.add(0, v.getId(), 0, "Delivery Order");
			}
			if (invoiceStr.matches("Add Invoice")) {
				menu.add(0, v.getId(), 0, "Invoice");
			}
			if (receiptsStr.matches("Receipts")) {
				menu.add(0, v.getId(), 0, "Cash Collection");
			}
			if(consignmentStr.matches("Consignment")){
			menu.add(0, v.getId(), 0, "Consignment");
		}
			menu.add(0, v.getId(), 0, "Visited");
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		ConvertToSetterGetter.setDoNo("");
		ConvertToSetterGetter.setSoNo("");
		ConvertToSetterGetter.setEdit_do_no("");

		SOTDatabase.deleteallbatch();

		if (item.getTitle() == "Sales Order") {

			intentString = "SalesOrder";
			new GetSalesCustomer().execute();

		} else if (item.getTitle() == "Delivery Order") {
			SOTDatabase.deleteImage();
			SOTDatabase.deleteBarcode();
			SOTDatabase.deleteSODetailQuantity();
			intentString = "DeliveryOrder";
			new GetSalesCustomer().execute();

		} else if (item.getTitle() == "Invoice") {

			SalesOrderSetGet.setSoRemarks("");
			SalesOrderSetGet.setSoAdditionalInfo("");
			SOTDatabase.deleteImage();
			SOTDatabase.deleteBarcode();
			ConvertToSetterGetter.setEdit_inv_no("");

			intentString = "Invoice";

			new GetSalesCustomer().execute();
		} else if (item.getTitle() == "Cash Collection") {
			Intent i = new Intent(RouteHeader.this, InvoiceCashCollection.class);
			i.putExtra("customerCode", cust_id);
			startActivity(i);
			SalesOrderSetGet.setHeader_flag("RouteHeader");
			RouteHeader.this.finish();

		} else if(item.getTitle() == "Consignment"){
			intentString = "Consignment";
			new GetSalesCustomer().execute();
		}else if(item.getTitle() == "Visited"){

			alertVisitedDialog();
//            String signatureResult =SOTSummaryWebService.saveSignatureImage(
//                    "", "" + "", "" + "",
//                    signature_img, "", "fncSaveInvoiceImages","DO","","" + "^" + remarks + "^" + scheduleNo);

//            Log.d("signatureResult-->", ""+signatureResult);
		} else {
			return false;
			}
		return true;
	}

	private void alertVisitedDialog() {
		final Dialog dialog = new Dialog(RouteHeader.this);

		dialog.setContentView(R.layout.activity_alert);
		dialog.setTitle("Visited Dialog");
		final EditText remarks = (EditText) dialog.findViewById(R.id.editTextRemarks);
		Button close = (Button) dialog.findViewById(R.id.close_ic);
		ImageView signature = (ImageView) dialog.findViewById(R.id.sm_sign_iv);
		ImageView camera =(ImageView) dialog.findViewById(R.id.sm_camera_iv);
		cam_image =(ImageView) dialog.findViewById(R.id.prod_photo);
		sig_image =(ImageView)dialog.findViewById(R.id.sm_signature);
		ImageView location =(ImageView) dialog .findViewById(R.id.sm_loc_iv);
		location_txt =(EditText) dialog.findViewById(R.id.sm_location);
		Button save=(Button) dialog.findViewById(R.id.deli_addProduct);

//		if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
//
//		}

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

		getLocation();
//		if (ActivityCompat.checkSelfPermission(RouteHeader.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RouteHeader.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////					buildGoogleApiClient();
//		}

		camera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CameraAction(PICK_FROM_CAMERA);
			}
		});

		signature.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(RouteHeader.this,
						CaptureSignature.class);
				startActivityForResult(i, SIGNATURE_ACTIVITY);
			}
		});



		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String remark =remarks.getText().toString();

				sign_img = SOTDatabase.getSignatureImage();
				//product_img = SOTDatabase.getProductImage();

				mCurrentgetPath = Product.getPath();
				//Log.d("pathfind",mCurrentgetPath);
				product_img = ImagePathToBase64Str(mCurrentgetPath);

				if (sign_img == null) {
					sign_img = "";
				}
				if (product_img == null) {
					product_img = "";
				}

				Log.d("signature_img", ":"+sign_img);
				Log.d("product_img", ":"+product_img);

				Log.d("fncSaveInvoiceImages", "" +cust_id + " "
						+ setLatitude + " " + setLongitude + " VI" + address1
						+ address2 + "signature_img " + sign_img
						+ "product_img " + product_img);

				if (location_txt.getText().length() != 0
						&& location_txt.getText().toString() != "") {

					address1 = location_txt.getText().toString();
					Log.d("address1", "-->" + address1 + "-->"+location_txt.getText().length() );

					String imgResult = SOTSummaryWebService
							.saveSignatureImage(cust_id, ""
											+ setLatitude, "" + setLongitude,
									sign_img, product_img,
									"fncSaveInvoiceImages", "VI", address1,
									address2 + "^" + remark);

					Log.d("signatureResult-->", "" + imgResult);


					if (!imgResult.matches("")) {
						Toast.makeText(RouteHeader.this, "Saved Successfully",
								Toast.LENGTH_SHORT).show();
						Log.d("Cap Image", "Saved");

						deleteDirectory(new File(Environment.getExternalStorageDirectory() + "/" + "SFA", "Image"));
					} else {
						Log.d("Cap Image", "Not Saved");
					}
				}else{
					Toast.makeText(getApplicationContext(),
							"Waiting For Location!!!", Toast.LENGTH_SHORT)
							.show();
					getLocation();
				}


				RoutesyncCall routeservice = new RoutesyncCall();
				routeservice.execute();



				dialog.dismiss();
				Adapter = new RouteAdapter(RouteHeader.this, ListArray);
				list.setAdapter(Adapter);
				Adapter.notifyDataSetChanged();
			}
		});


		dialog.show();

		close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				Adapter = new RouteAdapter(RouteHeader.this, ListArray);
				list.setAdapter(Adapter);
				Adapter.notifyDataSetChanged();

			}
		});

	}



	private void getLocation() {
		try {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public boolean deleteDirectory(File path) {
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isFile()) {
					files[i].delete();
				}
			}
		}
		return( path.delete() );
	}


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
//		if (data != null) {
		switch (requestCode) {

			case PICK_FROM_CAMERA:
				if (requestCode == PICK_FROM_CAMERA) {

					getRightAngleImage(mCurrentPhotoPath);

					handleCameraPhoto(PICK_FROM_CAMERA);
//						Bitmap photo = extras.getParcelable("data");
//						photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
//						prodphoto.setImageBitmap(photo);
//
//						ByteArrayOutputStream stream = new ByteArrayOutputStream();
//						photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
//						byte[] bitMapData = stream.toByteArray();
//						String camera_image = Base64.encodeToString(bitMapData,
//								Base64.DEFAULT);
//						SOTDatabase.init(InvoiceSummary.this);
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
//
//						Log.d("Camera Image", "cam" + camera_image);
					//}
				}
				break;

			case SIGNATURE_ACTIVITY:
				if (resultCode == RESULT_OK) {
					// Bundle extras = data.getExtras();
					byte[] bytes = data.getByteArrayExtra("status");
					if (bytes != null) {
						// Bitmap photo = extras.getParcelable("status");

						Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
								bytes.length);
						bitmap = Bitmap.createScaledBitmap(bitmap, 240, 80,
								true);

						sig_image.setImageBitmap(bitmap);

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] bitMapData = stream.toByteArray();
						String signature_image = Base64.encodeToString(
								bitMapData, Base64.DEFAULT);
						SOTDatabase.init(RouteHeader.this);
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
		}
//		}
	}

//	@Override
//	public void onConnected(Bundle bundle) {
//		Log.d("onConnected", "onConnected");
//
//		mLocationRequest = LocationRequest.create();
//		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
////		mLocationRequest.setInterval(100);
////        mLocationRequest.setInterval(100); // Update location every second
//
//		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, RouteHeader.this);
//
//		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//					mGoogleApiClient);
//			Log.d("mLastLocation","--->"+mLastLocation);
//
//			if (mLastLocation != null) {
//			lat = String.valueOf(mLastLocation.getLatitude());
//			lon = String.valueOf(mLastLocation.getLongitude());
//
//
//			if(lat!=null && !lat.isEmpty()){
//				setLatitude = mLastLocation.getLatitude();
//			}
//
//			if(lon!=null && !lon.isEmpty()){
//				setLongitude = mLastLocation.getLongitude();
//			}
//		}	else {
//
////			Log.d("handler","-->"+handler);
////			handler.sendEmptyMessageDelayed(1, 200);
//		}
//
//
//
//			handler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				if (count < 2) {
//					mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//					if (mLastLocation == null) {
//						Log.d("getLongitude", "location not found");
//						handler.sendEmptyMessageDelayed(1, 200);
//					} else {
//						Log.d("getLatitude", "location not found");
//						lat = String.valueOf(mLastLocation.getLatitude());
//						lon = String.valueOf(mLastLocation.getLongitude());
//
//
//						if(lat!=null && !lat.isEmpty()){
//							setLatitude = mLastLocation.getLatitude();
//						}
//
//						if(lon!=null && !lon.isEmpty()){
//							setLongitude = mLastLocation.getLongitude();
//						}
//					}
//					count++;
//				} else {
//					Log.i("MyTag", "location not found");
//				}
//			}
//		};
//
////		double lati=0,longi=0;
//
//
//
//
//		boolean interntConnection = isNetworkConnected();
//		if (interntConnection == true) {
//
//			// getting GPS status
//			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//			if (isGPSEnabled == true) {
//				Log.d("isGPSEnabled", ""+isGPSEnabled);
////				new AppLocationService.getServerDateTime(lati, longi, "1").execute();
//
//				updateUI(setLatitude,setLongitude);
//			}else {
//				Log.d("gpsLocation", " null "+"isGPSOFf");
////				new AppLocationService.getServerDateTime(0, 0, "0").execute();
//				updateUI(0,0);
//					Toast.makeText(getApplicationContext(),
//							"GPS is OFF. Please turn ON",
//							Toast.LENGTH_LONG).show();
//			}
//
//		} else {
////			Toast.makeText(getApplicationContext(),
////					"No Internet Connection",
////					Toast.LENGTH_LONG).show();
//		}
//	}
//
//	@Override
//	public void onConnectionSuspended(int i) {
//
//	}

//	@Override
//	public void onConnectionFailed(ConnectionResult connectionResult) {
//		buildGoogleApiClient();
//	}
//
//	synchronized void buildGoogleApiClient() {
//
//		mGoogleApiClient = new GoogleApiClient.Builder(this)
//				.addConnectionCallbacks(this)
//				.addOnConnectionFailedListener(this)
//				.addApi(LocationServices.API)
//				.build();
//		Log.d("mGoogleApiClient","-->"+mGoogleApiClient);
//
//	}
//	void updateUI(double lat,double lon) {
//		try {
//			getAddress(lat, lon);
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//	public void getAddress(double latitude, double longitude) throws Exception {
//		address1 = "";
//		address2 = "";
//		Geocoder geocoder;
//		List<Address> addresses;
//		try {
//			boolean interntConnection = isNetworkConnected();
//			if (interntConnection == true) {
//				geocoder = new Geocoder(this, Locale.getDefault());
//
//				addresses = geocoder.getFromLocation(latitude, longitude, 1);
//				Log.d("addressesSize","--->"+addresses.size());
//				if (addresses != null && addresses.size() > 0) {
//
//					address1 = addresses.get(0).getAddressLine(0);
//					address2 = addresses.get(0).getAddressLine(1);
//					Log.d("address2","--->"+address1+"  "+address2);
//
//					location_txt.setText(address1 + "," + address2);
//
//					/*Address address = addresses.get(0);
//					ArrayList<String> addressFragments = new ArrayList<String>();
//
//					// Fetch the address lines using getAddressLine,
//					// join them, and send them to the thread.
//					for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//						addressFragments.add(address.getAddressLine(i));
//					}*/
//
//				}
//
////				Toast.makeText(
////						getApplicationContext(),
////						"Mobile Location : " + "\nLatitude: "
////								+ latitude + "\nLongitude: "
////								+ longitude + "\nAddress 1: "
////								+ address1 + "\nAddress 2: "
////								+ address2, Toast.LENGTH_LONG)
////						.show();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
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
			//	helper.dismissProgressView(salesO_parent);
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
			//helper.dismissProgressView(salesO_parent);
		}
		return imagePath;
	}


	private void handleCameraPhoto(int actionCode) {
		Log.d("mCurrentPhotoPath", "mCurrentPhotoPath-->"+mCurrentPhotoPath);
		if (mCurrentPhotoPath != null) {
			switch (actionCode) {
				case PICK_FROM_CAMERA: {
					Product.setPath(mCurrentPhotoPath);
					decodeImagePathFile(mCurrentPhotoPath,cam_image);
					break;
				}
			}
			//	galleryAddPic();
			mCurrentPhotoPath = null;
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

			String camera_image=ImagePathToBase64Str(imageString);

//			ByteArrayOutputStream stream = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//			byte[] bitMapData = stream.toByteArray();
//			String camera_image = Base64.encodeToString(bitMapData,
//					Base64.DEFAULT);
			SOTDatabase.init(RouteHeader.this);

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

	public String ImagePathToBase64Str(String path) {

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
		String temp="";
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] b = baos.toByteArray();
			temp = Base64.encodeToString(b, Base64.DEFAULT);
		}catch (Exception e){
			e.printStackTrace();
		}
		return temp;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d("LatitudeandLongitude","-->"+location.getLatitude()+"--"+location.getLongitude());
		String address="";
			Geocoder geocoder = new Geocoder(RouteHeader.this, Locale.getDefault());

		try {
			List<Address> addresses  = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);

			ArrayList<String> addressFragments = new ArrayList();

			for(int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {

				addressFragments.add(addresses.get(0).getAddressLine(i));
			}
			for(int j=0;j<addressFragments.size();j++){
				address =addressFragments.get(j);

			}
			Log.d("ListOfAddressCheck","-->"+address);

			setLatitude = location.getLatitude();
			setLongitude =location.getLongitude();

			location_txt.setText(address);

//			location_txt.setText(" ");

//			String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
			String city = addresses.get(0).getLocality();
			String state = addresses.get(0).getAdminArea();
			String country = addresses.get(0).getCountryName();
			String postalCode = addresses.get(0).getPostalCode();
			String knownName = addresses.get(0).getFeatureName();
//			location_txt.setText(address+city+country+postalCode);

		}catch(Exception e)
		{

		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(RouteHeader.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
	}


	private class  RoutesyncCall extends AsyncTask<Void, Void, Void> {
		String jsonString = "";

		@Override
		protected void onPreExecute() {
			Log.d("onPreExecute","-->"+"onPreExecute");
			ListArray.clear();
			spinnerLayout = new LinearLayout(RouteHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(routeheader_layout, false);
			progressBar = new ProgressBar(RouteHeader.this);
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
			String selectday = "";
			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			String user = SupplierSetterGetter.getUsername();
			String date = SalesOrderSetGet.getCurrentdate();

			SimpleDateFormat oldFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date mDate;
			try {
				mDate = oldFormat.parse(date);
				SimpleDateFormat dayFormat = new SimpleDateFormat(
						"EEE, dd MMM yyyy");
				String mday = dayFormat.format(mDate);

				StringTokenizer tokens = new StringTokenizer(mday, ",");
				String first = tokens.nextToken();
				Log.d("FirstDay","-->"+first +user) ;

				if (first.matches("Sun")) {

					selectday = "1";
				}else if (first.matches("Mon")) {
				selectday = "2";
			} else if (first.matches("Tue")) {
				selectday = "3";
			} else if (first.matches("Wed")) {
				selectday = "4";
			} else if (first.matches("Thu")) {
				selectday = "5";
			} else if (first.matches("Fri")) {
				selectday = "6";
			} else if (first.matches("Sat")) {
				selectday = "7";
			}
			System.out.println("day : " + selectday);
			
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Log.d("selectdayList",selectday);

			HashMap<String, String> value = new HashMap<String, String>();
			value.put("CompanyCode", cmpnyCode);
			value.put("AssignUser",user);
			value.put("RouteDay", selectday);

			jsonString = WebServiceClass.parameterService(value,
					"fncGetRouteMaster");
			JSONObject jsonResponse = null;
			JSONArray jsonMainNode;
			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("JsonArray");

				int lengthJsonArr = jsonMainNode.length();
				Log.d("customerlist size", "-->" + lengthJsonArr);
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode;

					jsonChildNode = jsonMainNode.getJSONObject(i);
					String cust_code = jsonChildNode.optString("CustomerCode")
							.toString();
					String cust_Name = jsonChildNode.optString("CustomerName")
							.toString();
					
					String sortOrder = jsonChildNode.optString("SortOrder")
							.toString();

					String status = jsonChildNode .optString("Status").toString();

					Log.d("statusHeader",status);

					SOTdetailsGetSet cust_set = new SOTdetailsGetSet();
					cust_set.setCust_Code(cust_code);
					cust_set.setCust_Name(cust_Name);
					cust_set.setSortOrder(sortOrder);
					cust_set.setStatus(status);
					
					ListArray.add(cust_set);
			
				}
				
				Collections.sort(ListArray, new CustomerSortComparator());

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("ListArrayResult", ListArray.toString());

			Adapter = new RouteAdapter(RouteHeader.this, ListArray);
			list.setAdapter(Adapter);
			Adapter.notifyDataSetChanged();

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
//			alertVisitedDialog();
			enableViews(routeheader_layout, true);
		}
	}

	class CustomerSortComparator implements Comparator<SOTdetailsGetSet> {
	     @Override
	     public int compare(SOTdetailsGetSet o1, SOTdetailsGetSet o2) {
	         return o1.getSortOrder().compareTo(o2.getSortOrder());
	     }
	 }
	
	private class GetSalesCustomer extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			cstmrgrpcdal.clear();
			alclcrrncy.clear();

			spinnerLayout = new LinearLayout(RouteHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(routeheader_layout, false);
			progressBar = new ProgressBar(RouteHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {

				Log.d("Customer values", "" + cstmrgrpcdal);

				serverdate = DateWebservice.getDateService("fncGetServerDate");
				currencyCode = SalesOrderWebService
						.generalSettingsService("fncGetGeneralSettings");
				alclcrrncy = SalesOrderWebService.getCurrencyValues(
						"fncGetCurrency", currencyCode);
				cstmrgrpcdal = SalesOrderWebService.getCustGroupCode(cust_id,
						"fncGetCustomer");
				SalesOrderWebService.getCustomerTax(cust_id, "fncGetCustomer");

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {

				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(routeheader_layout, true);

			SOTDatabase.init(RouteHeader.this);
			SOTDatabase.deleteAllProduct();
			SOTDatabase.deleteBillDisc();
			SOTDatabase.deleteImage();
			SOTDatabase.deleteBarcode();

			currencyName = alclcrrncy.get(0);
			currencyRate = alclcrrncy.get(1);

			SalesOrderSetGet.setSaleorderdate(serverdate);
			SalesOrderSetGet.setDeliverydate(serverdate);
			SalesOrderSetGet.setCustomercode(cust_id);
			SalesOrderSetGet.setCustomername(custName);
			SalesOrderSetGet.setCurrencyrate(currencyRate);
			SalesOrderSetGet.setCurrencycode(currencyCode);
			SalesOrderSetGet.setCurrencyname(currencyName);
			SalesOrderSetGet.setRemarks("");

			SalesOrderSetGet.setSuppliercode(cstmrgrpcdal.get(0));
			SalesOrderSetGet.setSuppliergroupcode(cstmrgrpcdal.get(1));

			if (intentString.matches("SalesOrder")) {

				Intent i = new Intent(RouteHeader.this, SalesAddProduct.class);
				i.putExtra("SalesOrder", "Route "+intentString);
				startActivity(i);
				SalesOrderSetGet.setHeader_flag("RouteHeader");
				RouteHeader.this.finish();

			} else if (intentString.matches("Invoice")) {

				Intent i = new Intent(RouteHeader.this, InvoiceAddProduct.class);
				i.putExtra("Invoice", "Route "+intentString);
				startActivity(i);
				SalesOrderSetGet.setHeader_flag("RouteHeader");
				RouteHeader.this.finish();
			} else if (intentString.matches("DeliveryOrder")) {

				Intent i = new Intent(RouteHeader.this,
						DeliveryAddProduct.class);
				i.putExtra("DeliveryOrder", "Route "+intentString);
				startActivity(i);
				SalesOrderSetGet.setHeader_flag("RouteHeader");
				RouteHeader.this.finish();
			}
			else if (intentString.matches("Consignment")) {

				Intent i = new Intent(RouteHeader.this,
						ConsignmentAddProduct.class);
				i.putExtra("Consignment", "Route "+intentString);
				startActivity(i);
				SalesOrderSetGet.setHeader_flag("RouteHeader");
				RouteHeader.this.finish();
			}
		}
	}

	public class RouteAdapter extends BaseAdapter implements Filterable {

		private ArrayList<SOTdetailsGetSet> listarray = new ArrayList<SOTdetailsGetSet>();
		private ArrayList<SOTdetailsGetSet> mOriginalValues = new ArrayList<SOTdetailsGetSet>();
		LayoutInflater mInflater;
		CustomHolder holder = new CustomHolder();
		SOTdetailsGetSet user;
		private Filter sampleFilter;

		public RouteAdapter(Context context,
				ArrayList<SOTdetailsGetSet> productsList) {
			listarray.clear();
			mOriginalValues.clear();
			this.listarray = productsList;
			this.mOriginalValues = productsList;
			mInflater = LayoutInflater.from(context);

		}

		@Override
		public int getCount() {
			return listarray.size();
		}

		@Override
		public SOTdetailsGetSet getItem(int position) {
			return listarray.get(position);
		}

		@Override
		public long getItemId(int position) {
			return listarray.get(position).hashCode();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View row = convertView;
			holder = null;

			if (row == null) {
				
				row = mInflater.inflate(R.layout.customer_list_item, null);
				holder = new CustomHolder();
				              
				holder.linear_cst_code = (LinearLayout) row
				.findViewById(R.id.linear_cst_code);
				holder.custCode = (TextView) row
						.findViewById(R.id.in_cust_code);
				holder.custName = (TextView) row
						.findViewById(R.id.in_cust_name);
				holder.address =(TextView) row
						.findViewById(R.id.address);
				holder.status = (TextView) row
						.findViewById(R.id.status);
				holder.linear_cst_code.setVisibility(View.VISIBLE);
				row.setTag(holder);
			} else {
				holder = (CustomHolder) row.getTag();
			}

			user = getItem(position);
			Log.d("statusCheck","-->"+user.getCust_Name()+user.getStatus());
			holder.custCode.setText(user.getCust_Code());
			holder.custName.setText(user.getCust_Name());

			holder.custName.setTag(position);

			if (position % 2 == 0) {
				row.setBackgroundResource(drawable.list_item_even_bg);
				holder.custCode.setTextColor(Color.parseColor("#035994"));
				holder.custName.setTextColor(Color.parseColor("#035994"));


			} else {

				row.setBackgroundResource(drawable.list_item_odd_bg);
				holder.custCode.setTextColor(Color.parseColor("#646464"));
				holder.custName.setTextColor(Color.parseColor("#646464"));

			}

			if(user.getStatus().equals("1"))
			{
				row.setBackgroundColor(Color.parseColor("#9ce9bb"));
			}

			return row;
		}

		final class CustomHolder {

			LinearLayout linear_cst_code;
			TextView custCode;
			TextView custName;
			TextView address;
			TextView status;

		}

		public void resetData() {
			listarray = mOriginalValues;
		}

		@Override
		public Filter getFilter() {
			if (sampleFilter == null)
				sampleFilter = new SampleFilter();

			return sampleFilter;
		}

		private class SampleFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				// We implement here the filter logic
				if (constraint == null || constraint.length() == 0) {
					// No filter implemented we return all the list
					results.values = mOriginalValues;
					results.count = mOriginalValues.size();
				} else {
					// We perform filtering operation
					ArrayList<SOTdetailsGetSet> FilteredArrList = new ArrayList<SOTdetailsGetSet>();
					constraint = constraint.toString().toLowerCase();
					for (int i = 0; i < mOriginalValues.size(); i++) {
						String data = mOriginalValues.get(i).getCust_Name();
						String data1 = mOriginalValues.get(i).getCust_Code();
						if (data.toLowerCase().contains(constraint.toString())
								|| data1.toLowerCase().contains(
										constraint.toString())) {
							FilteredArrList.add(new SOTdetailsGetSet(
									mOriginalValues.get(i).getCust_Code(),
									mOriginalValues.get(i).getCust_Name()));
						}

					}
					// set the Filtered result to return
					results.count = FilteredArrList.size();
					results.values = FilteredArrList;

				}
				return results;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				if (results.count == 0) {
					listarray = (ArrayList<SOTdetailsGetSet>) results.values;
					notifyDataSetChanged();
				} else {
					listarray = (ArrayList<SOTdetailsGetSet>) results.values;
					notifyDataSetChanged();
				}

			}
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

//	@Override
//	protected void onStart() {
//		super.onStart();
//		if (mGoogleApiClient != null) {
//			mGoogleApiClient.connect();
//		}
//	}

//	@Override
//	protected void onStop() {
//		super.onStop();
//		if (mGoogleApiClient.isConnected()) {
//			mGoogleApiClient.disconnect();
//		}
//	}

	@Override
	public void onListItemClick(String item) {
		// TODO Auto-generated method stub
		menu.toggle();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(RouteHeader.this, LandingActivity.class);
		startActivity(i);
		RouteHeader.this.finish();
	}

}
