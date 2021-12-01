package com.winapp.sot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.offline.SoapAccessTask;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.UIHelper;

public class StockRequestSummary extends SherlockFragmentActivity implements
SlideMenuFragment.MenuClickInterFace, LocationListener{
	Cursor cursor;
	ImageView camera, signature, location, prodphoto, sosign;
	EditText sm_total, sm_netTotal, sm_tax, sm_location,sm_cQty,sm_lQty,sm_totalQty;
	ImageButton save, back;
	double total = 0, smTax = 0;
	Button stupButton, stdownButton;;
	LinearLayout sm_header_layout, slsummary_layout, noofprint_layout;
	String valid_url = "", summaryResult = "";
	double taxAmt = 0;
	String subTot = "", totTax = "", netTot = "", tot = "";
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	private static final int PICK_FROM_CAMERA = 1;
	public static final int SIGNATURE_ACTIVITY = 2;
	double itemDisc = 0, sbTtl = 0, netTotal = 0;
	GPSTracker gps;
	String beforeChange = "",calCarton="",stockReqNo = "";
	double tota = 0;
	private ListView mListView;
	private UIHelper helper;
	CheckBox enableprint;
	int stuprange = 3, stdownrange = 1, stwght = 1;
	private TextView stnumber,mEmpty;
	private SlidingMenu menu;
	private ProductListAdapter adapter;
	
	// get location
		private LocationManager locationManager;
		private boolean isGPSEnabled = false, isNetworkEnabled = false;
		private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
		private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	ArrayList<HashMap<String, String>> stockRequestDetailsArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> stockRequestHeaderArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> stockRequestBatchDetailArr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		Mint.initAndStartSession(StockRequestSummary.this, "29088aa0");
		setContentView(R.layout.stockrequest_summary);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(false);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.summary_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Stock Request Summary");
		back = (ImageButton) customNav.findViewById(R.id.back);
		
		save = (ImageButton) customNav.findViewById(R.id.save);

		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);

		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);

		back.setVisibility(View.INVISIBLE);
		
	//	back = (ImageButton) findViewById(R.id.back);
	//	save = (ImageButton) findViewById(R.id.save);
		sm_total = (EditText) findViewById(R.id.sm_total);
		sm_netTotal = (EditText) findViewById(R.id.sm_netTotal);
		sm_tax = (EditText) findViewById(R.id.sm_tax);
		sm_location = (EditText) findViewById(R.id.sm_location);
		
		sm_cQty = (EditText) findViewById(R.id.sm_cQty);
        sm_lQty = (EditText) findViewById(R.id.sm_lQty);
        sm_totalQty = (EditText) findViewById(R.id.sm_totalQty);
		
        camera = (ImageView) findViewById(R.id.sm_camera_iv);
		signature = (ImageView) findViewById(R.id.sm_sign_iv);
		location = (ImageView) findViewById(R.id.sm_loc_iv);
		prodphoto = (ImageView) findViewById(R.id.prod_photo);
		sosign = (ImageView) findViewById(R.id.sm_signature);

		sm_header_layout = (LinearLayout) findViewById(R.id.sm_header_layout);
		slsummary_layout = (LinearLayout) findViewById(R.id.slsummary_layout);
		mListView = (ListView) findViewById(android.R.id.list);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		FWMSSettingsDatabase.init(StockRequestSummary.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SOTSummaryWebService(valid_url);
		helper = new UIHelper(StockRequestSummary.this);
		SOTDatabase.init(StockRequestSummary.this);
		cursor = SOTDatabase.getCursor();
		stockRequestBatchDetailArr = new ArrayList<HashMap<String, String>>();
		stockRequestBatchDetailArr.clear();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			SOTDatabase.deleteAllProduct();
			SOTDatabase.deleteBillDisc();

			stockRequestDetailsArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("StockReqDetails");
			stockRequestHeaderArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("StockReqHeader");

			stockRequestBatchDetailArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("StockReqBatchDetail");
			Log.d("SR details", "" + stockRequestDetailsArr);
			Log.d("SR Header", "" + stockRequestHeaderArr);

			if(stockRequestBatchDetailArr !=null){
				ConvertToSetterGetter.setStockRequestBatchDetailArr(stockRequestBatchDetailArr);
			}

			if (stockRequestHeaderArr != null) {
				for (int i = 0; i < stockRequestHeaderArr.size(); i++) {

					stockReqNo = stockRequestHeaderArr.get(i).get("StockReqNo");

					ConvertToSetterGetter.setEdit_stockreq_no(stockReqNo);

					String StockReqDate = stockRequestHeaderArr.get(i).get(
							"StockReqDate");
					String FromLocation = stockRequestHeaderArr.get(i).get(
							"FromLocation");
					String ToLocation = stockRequestHeaderArr.get(i).get(
							"ToLocation");
					String Remarks = stockRequestHeaderArr.get(i)
							.get("Remarks");
					String Status = stockRequestHeaderArr.get(i).get("Status");

					SalesOrderSetGet.setSaleorderdate(StockReqDate);
					SalesOrderSetGet.setFromLoc(ToLocation);
					SalesOrderSetGet.setToLoc(FromLocation);
					SalesOrderSetGet.setRemarks(Remarks);

				}
			}

			if (stockRequestDetailsArr != null) {
				for (int i = 0; i < stockRequestDetailsArr.size(); i++) {

					String slNo = stockRequestDetailsArr.get(i).get("slNo");
					String ProductCode = stockRequestDetailsArr.get(i).get(
							"ProductCode");
					String ProductName = stockRequestDetailsArr.get(i).get(
							"ProductName");
					String CQty = stockRequestDetailsArr.get(i).get("CQty");
					String LQty = stockRequestDetailsArr.get(i).get("LQty");
					String Qty = stockRequestDetailsArr.get(i).get("Qty");
					String PcsPerCarton = stockRequestDetailsArr.get(i).get(
							"PcsPerCarton");
					String Price = stockRequestDetailsArr.get(i).get("Price");
					String TransferQty = stockRequestDetailsArr.get(i).get(
							"TransferQty");

					Double dcQty = new Double(CQty);
					Double dlQty = new Double(LQty);
					Double dqty = new Double(Qty);
					Double tqty = new Double(TransferQty);
					Double dpcsPerCarton = new Double(PcsPerCarton);
					double price = Double.parseDouble(Price);

					int slno = Integer.parseInt(slNo);
					int cQty = dcQty.intValue();
					int lQty = dlQty.intValue();
					int qty = dqty.intValue();
					int pcsPerCarton = dpcsPerCarton.intValue();
					int transferqty = tqty.intValue();

					if (calCarton.matches("0")) {

						int reqQty = cQty - transferqty;

						if (reqQty > 0) {

							SOTDatabase.storeProductForTransfer(slno,
									ProductCode, ProductName, reqQty, lQty,
									qty, price, pcsPerCarton);

						}

					} else {

						int reqQty = qty - transferqty;

						cQty = reqQty / pcsPerCarton;
						lQty = reqQty % pcsPerCarton;

						Log.d("cqty", "" + cQty);
						Log.d("lqty", "" + lQty);

						if (reqQty > 0) {

							SOTDatabase.storeProductForTransfer(slno,
									ProductCode, ProductName, cQty, lQty,
									reqQty, price, pcsPerCarton);

						}

					}

					/*String cmpnyCode = SupplierSetterGetter.getCompanyCode();

					ArrayList<PropertyInfo> params = new ArrayList<PropertyInfo>();
					params.add(newPropertyInfo("CompanyCode", cmpnyCode));
					params.add(newPropertyInfo("ProductCode", ProductCode));

					String haveBatchOnTransfer = SalesOrderSetGet
							.getHaveBatchOnTransfer();
					String haveBatchOnTransferToLocation = SalesOrderSetGet
							.getHaveBatchOnTransferToLocation();

					if (haveBatchOnTransferToLocation != null
							&& !haveBatchOnTransferToLocation.isEmpty()) {

						if (haveBatchOnTransfer.matches("True")
								|| haveBatchOnTransferToLocation
								.matches("True")) {
							Log.d("haveBatchOnTransfer", haveBatchOnTransfer);
							new SoapAccessTask(StockRequestSummary.this, valid_url,
									"fncGetProduct", params, new GetProduct(
									ProductCode,slNo)).execute();
						}
					}*/
				}
			}

		}

		if (cursor != null && cursor.getCount() > 0) {

			sm_header_layout.setVisibility(View.VISIBLE);
			save.setVisibility(View.VISIBLE);
			tota = SOTDatabase.getTotal();
			
			smTax = SOTDatabase.getTax();
			String ProdTax = fourDecimalPoint(smTax);
			sm_tax.setText("" + ProdTax);
			sbTtl = SOTDatabase.getSubTotal();
			String sub = twoDecimalPoint(sbTtl);
			sm_total.setText("" + sub);
			netTotal = sbTtl + smTax;
			String ProdNettotal = twoDecimalPoint(netTotal);
			sm_netTotal.setText("" + ProdNettotal);
			
			sm_cQty.setText(twoDecimalPoint(SOTDatabase.getTotalCqty()));
			   sm_lQty.setText(twoDecimalPoint(SOTDatabase.getTotalLqty()));
			   sm_totalQty.setText(twoDecimalPoint(SOTDatabase.getTotalQty()));
			   
			   mEmpty.setVisibility(View.GONE);
			
		} else {
			mEmpty.setVisibility(View.VISIBLE);
			save.setVisibility(View.INVISIBLE);
			sm_header_layout.setVisibility(View.GONE);
						
			sm_cQty.setText(twoDecimalPoint(SOTDatabase.getTotalCqty()));
			   sm_lQty.setText(twoDecimalPoint(SOTDatabase.getTotalLqty()));
			   sm_totalQty.setText(twoDecimalPoint(SOTDatabase.getTotalQty()));
		}
		adapter = new ProductListAdapter(this, cursor);
		mListView.setAdapter(adapter);
		//setListAdapter(new ProductListAdapter(this, cursor));
		registerForContextMenu(mListView);

		try {

			// Getting LocationManager object
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			// // Creating an empty criteria object
			// Criteria criteria = new Criteria();
			//
			// // Getting the name of the provider that meets the criteria
			// provider = locationManager.getBestProvider(criteria, false);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {

				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 20000, 0, this);;
					Log.d("GPS Enabled", "GPS Enabled");
					if (locationManager != null) {
						Location location = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							Log.d("location", "not null");
							// if (onlineMode.matches("True")) {
							// if (checkOffline == true) {
							// } else { // online
							onLocationChanged(location);
							// }
							// }
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
							// if (onlineMode.matches("True")) {
							// if (checkOffline == true) {
							// } else { // online
							onLocationChanged(location);
							// }
							// }
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CameraAction();
			}
		});

		signature.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent i = new Intent(StockRequestSummary.this,
						CaptureSignature.class);
				startActivityForResult(i, SIGNATURE_ACTIVITY);

			}
		});
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				saveAlertDialog();
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(StockRequestSummary.this,
						StockRequestAddProduct.class);
				startActivity(i);
				StockRequestSummary.this.finish();
			}
		});

	}

	public void getAddress(double latitude, double longitude) throws Exception {
		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(this, Locale.getDefault());
		addresses = geocoder.getFromLocation(latitude, longitude, 1);

		if (addresses != null && addresses.size() > 0) {

			String address1 = addresses.get(0).getAddressLine(0);
			String address2 = addresses.get(0).getAddressLine(1);
	
			sm_location.setText(address1 + "," + address2);
			locationManager.removeUpdates(this);
		}
	}

	@SuppressWarnings("deprecation")
	public void taxCalc() {

		taxAmt = 0;
		int sl_no = 1;
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					double net_tot = 0;
					double subTot = 0, tx = 0, txVl = 0, tax = 0, net = 0;

					String total = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TOTAL));

					String taxValue = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));

					double sbTotal = SOTDatabase.getsubTotal(sl_no);
					subTot = sbTotal;

					if (!taxValue.matches("")) {
						txVl = Double.parseDouble(taxValue);
						tx = (subTot * txVl) / 100;
					}

					Log.d("tx", "" + tx);

					String updTx = fourDecimalPoint(tx);

					if (!updTx.matches("")) {
						tax = Double.parseDouble(updTx);

						if (!total.matches("")) {

							net_tot = subTot + tax;

							String ntTtl = twoDecimalPoint(net_tot);

							net = Double.parseDouble(ntTtl);
							String subtotal = twoDecimalPoint(subTot);
							SOTDatabase.updateSummary(tax,
									Double.parseDouble(subtotal), net, sl_no);
						}
					}

					sl_no++;
					taxAmt = taxAmt + tx;

				} while (cursor.moveToNext());
				cursor.requery();
			}
		}
	}

	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}

	public String fourDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setMinimumFractionDigits(4);
		String tot = df.format(d);

		return tot;
	}

	private class ProductListAdapter extends ResourceCursorAdapter {

		@SuppressWarnings("deprecation")
		public ProductListAdapter(Context context, Cursor cursor) {
			super(context, R.layout.stockrequest_list_item, cursor);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			TextView ss_sl_id = (TextView) view.findViewById(R.id.ss_slid);
			ss_sl_id.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_ID)));

			TextView ss_sl_no = (TextView) view.findViewById(R.id.ss_slno);
			ss_sl_no.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO)));

			TextView ss_prodcode = (TextView) view
					.findViewById(R.id.ss_prodcode);
			ss_prodcode.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE)));

			TextView ss_name = (TextView) view.findViewById(R.id.ss_name);
			ss_name.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME)));

			TextView ss_qty = (TextView) view.findViewById(R.id.ss_qty);
			ss_qty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));

			TextView ss_net_total = (TextView) view
					.findViewById(R.id.ss_net_total);
			ss_net_total.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_NETTOTAL)));

			TextView ss_c_qty = (TextView) view.findViewById(R.id.ss_c_qty);
			ss_c_qty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));

			TextView ss_l_qty = (TextView) view.findViewById(R.id.ss_l_qty);
			ss_l_qty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));

			TextView ss_total = (TextView) view.findViewById(R.id.ss_total);
			ss_total.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_TOTAL)));

			TextView ss_tax = (TextView) view.findViewById(R.id.ss_tax);
			ss_tax.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_TAX)));
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

	private class AsyncCallWSSummary extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(StockRequestSummary.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(slsummary_layout, false);
			progressBar = new ProgressBar(StockRequestSummary.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			// progressBar.setBackgroundColor(Color.parseColor("#FFFFFF"));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {

				if (ConvertToSetterGetter.getEdit_stockreq_no() == null
						|| ConvertToSetterGetter.getEdit_stockreq_no().matches(
						"")) {
					stockReqNo = "";
				} else {
					stockReqNo = ConvertToSetterGetter.getEdit_stockreq_no();
				}

				summaryResult = SOTSummaryWebService
						.StockRequestSummary("fncSaveStockRequest",stockReqNo);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(slsummary_layout, true);

			if (summaryResult.matches("failed")) {

				Toast.makeText(StockRequestSummary.this, "Failed",
						Toast.LENGTH_SHORT).show();

			} else {

				Toast.makeText(StockRequestSummary.this, "Saved Successfully",
						Toast.LENGTH_SHORT).show();

				if (enableprint.isChecked()) {

					if (FWMSSettingsDatabase
							.getPrinterAddress()
							.matches(
									"[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$")) {

						try {
							helper.showProgressDialog(R.string.generating_sr);
							print();
						} catch (IOException e) {
							e.printStackTrace();
						}

					} else {
						helper.showLongToast(R.string.error_configure_printer);
						clearView();
					}
				}

				else {
					clearView();
				}
			}

		}
	}

	@SuppressWarnings("deprecation")
	public void clearView() {
		mEmpty.setVisibility(View.VISIBLE);
		save.setVisibility(View.INVISIBLE);
		SOTDatabase.deleteAllProduct();
		mListView.setAdapter(null);
		cursor.requery();

		sm_total.setText("");
		sm_tax.setText("");
		sm_header_layout.setVisibility(View.GONE);
		sm_netTotal.setText("");

		SOTDatabase.deleteBillDisc();

		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");

		Intent i = new Intent(StockRequestSummary.this,
				StockRequestHeader.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		StockRequestSummary.this.finish();

	}

	public void CameraAction() {
		/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 0);
		intent.putExtra("aspectY", 0);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 150);
		try {
			intent.putExtra("return-data", true);
			startActivityForResult(intent, PICK_FROM_CAMERA);
		} catch (ActivityNotFoundException e) {
		}*/
		
		try {				
			 Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	         if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	             startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
	         }
	         
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {

			case PICK_FROM_CAMERA:
				if (requestCode == PICK_FROM_CAMERA) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						Bitmap photo = extras.getParcelable("data");
						photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
						prodphoto.setImageBitmap(photo);

					}
				}
				break;

			case SIGNATURE_ACTIVITY:
				if (resultCode == RESULT_OK) {
					//Bundle extras = data.getExtras();
					 byte[] bytes = data.getByteArrayExtra("status");
					if (bytes != null) {
						//Bitmap photo = extras.getParcelable("status");
								
						Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
						bitmap = Bitmap.createScaledBitmap(bitmap, 240, 80, true);							
						
						sosign.setImageBitmap(bitmap);
						
						 ByteArrayOutputStream stream = new ByteArrayOutputStream();
							bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] bitMapData = stream.toByteArray();
						String signature_image = Base64.encodeToString(
								bitMapData, Base64.DEFAULT);
						SOTDatabase.init(StockRequestSummary.this);
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
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), 0, "Edit");
		menu.add(0, v.getId(), 0, "Delete");

	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getTitle() == "Edit") {

			Intent i = new Intent(StockRequestSummary.this,
					StockRequestAddProduct.class);
			i.putExtra("SOT_ssno", ((TextView) info.targetView
					.findViewById(R.id.ss_slid)).getText().toString());
			i.putExtra("SOT_ssproductcode", ((TextView) info.targetView
					.findViewById(R.id.ss_prodcode)).getText().toString());
			i.putExtra("SOT_str_ssprodname", ((TextView) info.targetView
					.findViewById(R.id.ss_name)).getText().toString());
			i.putExtra("SOT_str_sscq", ((TextView) info.targetView
					.findViewById(R.id.ss_c_qty)).getText().toString());
			i.putExtra("SOT_str_sslq", ((TextView) info.targetView
					.findViewById(R.id.ss_l_qty)).getText().toString());
			i.putExtra("SOT_str_ssqty", ((TextView) info.targetView
					.findViewById(R.id.ss_qty)).getText().toString());

			String id = ((TextView) info.targetView.findViewById(R.id.ss_slid))
					.getText().toString();
			String taxValue = SOTDatabase.getTaxValue(id);
			String uom = SOTDatabase.getUOM(id);
			String pieceperqty = SOTDatabase.getPiecePerQty(id);
			String taxtype = SOTDatabase.getTaxType(id);

			String pric = SOTDatabase.getPrice(id);

			i.putExtra("SOT_str_ssuom", uom);
			i.putExtra("SOT_st_sscpqty", pieceperqty);
			i.putExtra("SOT_st_sstaxvalue", taxValue);
			i.putExtra("SOT_str_sstaxtype", taxtype);
			i.putExtra("SOT_str_ssprice", pric);
			i.putExtra("SOT_st_sstotal", ((TextView) info.targetView
					.findViewById(R.id.ss_total)).getText().toString());
			i.putExtra("SOT_st_sstax", ((TextView) info.targetView
					.findViewById(R.id.ss_tax)).getText().toString());
			i.putExtra("SOT_st_ssnettot", ((TextView) info.targetView
					.findViewById(R.id.ss_net_total)).getText().toString());
			i.putExtra("SOT_ssupdate", "Update");
			i.putExtra("SOT_sscancel", "Cancel");
			startActivity(i);
			StockRequestSummary.this.finish();
		} else if (item.getTitle() == "Delete") {
			String id = ((TextView) info.targetView.findViewById(R.id.ss_slid))
					.getText().toString();

			Log.d("id", id);
			SOTDatabase.deleteProduct(id);
			SOTDatabase.deleteBillDiscount(id);
			cursor.requery();
			ArrayList<String> snoCount = new ArrayList<String>();
			snoCount = SOTDatabase.snoCountID();
			Log.d("snocount", "" + snoCount);
			for (int i = 0; i < snoCount.size(); i++) {
				int sno = 1 + i;
				HashMap<String, String> queryValues = new HashMap<String, String>();
				queryValues.put("_id", "" + snoCount.get(i));
				queryValues.put("snum", "" + sno);
				SOTDatabase.updateSNO(queryValues);
				SOTDatabase.updateBillSNO(queryValues);
			}
			if (cursor != null && cursor.getCount() > 0) {
				cursor.requery();
				tota = SOTDatabase.getTotal();
				smTax = SOTDatabase.getTax();
				String ProdTax = fourDecimalPoint(smTax);
				sm_tax.setText("" + ProdTax);

				sbTtl = SOTDatabase.getSubTotal();
				String sub = twoDecimalPoint(sbTtl);
				sm_total.setText("" + sub);

				netTotal = sbTtl + smTax;
				String ProdNettotal = twoDecimalPoint(netTotal);
				sm_netTotal.setText("" + ProdNettotal);
				
				sm_cQty.setText(twoDecimalPoint(SOTDatabase.getTotalCqty()));
				   sm_lQty.setText(twoDecimalPoint(SOTDatabase.getTotalLqty()));
				   sm_totalQty.setText(twoDecimalPoint(SOTDatabase.getTotalQty()));
				
				Toast.makeText(StockRequestSummary.this, "Deleted",
						Toast.LENGTH_LONG).show();
				
				mEmpty.setVisibility(View.GONE);
			} else {
				sm_header_layout.setVisibility(View.GONE);
				mEmpty.setVisibility(View.VISIBLE);
				save.setVisibility(View.INVISIBLE);
				sm_total.setText("");
				sm_tax.setText("");
				sm_netTotal.setText("");
				
				sm_cQty.setText(twoDecimalPoint(SOTDatabase.getTotalCqty()));
				   sm_lQty.setText(twoDecimalPoint(SOTDatabase.getTotalLqty()));
				   sm_totalQty.setText(twoDecimalPoint(SOTDatabase.getTotalQty()));
			}

		} else {
			return false;
		}
		return true;

	}

	public void saveAlertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setIcon(R.mipmap.ic_save);
		alertDialog.setTitle("Save");
		alertDialog.setMessage("Do you want to Save");
		LayoutInflater adbInflater = LayoutInflater.from(this);
		final View eulaLayout = adbInflater.inflate(R.layout.print_checkbox,
				null);
		noofprint_layout = (LinearLayout) eulaLayout
				.findViewById(R.id.noofcopieslblLayout);
		enableprint = (CheckBox) eulaLayout.findViewById(R.id.checkbox);
		enableprint.setText("Print");
		enableprint.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked == true) {
					noofprint_layout.setVisibility(View.VISIBLE);
					stnumber = (TextView) eulaLayout
							.findViewById(R.id.stnumber);
					stupButton = (Button) eulaLayout.findViewById(R.id.stupBtn);
					if (!PreviewPojo.getNofcopies().matches("")) {
						stnumber.setText("" + PreviewPojo.getNofcopies());
						stwght = Integer.valueOf(PreviewPojo.getNofcopies());
					}
					stupButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							stdownButton
									.setBackgroundResource(R.mipmap.numpicker_down_normal);
							stupButton
									.setBackgroundResource(R.mipmap.numpicker_up_pressed);
							if (stwght < 3) {
								stnumber.setText("" + ++stwght);
							}

						}
					});

					stdownButton = (Button) eulaLayout
							.findViewById(R.id.stdownBtn);
					stdownButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							stdownButton
									.setBackgroundResource(R.mipmap.numpicker_down_pressed);
							stupButton
									.setBackgroundResource(R.mipmap.numpicker_up_normal);
							if (stwght > 1) {
								stnumber.setText(--stwght + "");
							}
						}
					});
				} else if (isChecked == false) {
					noofprint_layout.setVisibility(View.GONE);
				}
			}
		});

		alertDialog.setView(eulaLayout);
		int modeid = FWMSSettingsDatabase.getModeId();
		if (modeid == 1) {
			FWMSSettingsDatabase.updateMode(1);
			enableprint.setChecked(true);
		} else {
			FWMSSettingsDatabase.updateMode(0);
			enableprint.setChecked(false);
		}
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int indexSelected) {

						totTax = sm_tax.getText().toString();
						netTot = sm_netTotal.getText().toString();
						tot = sm_total.getText().toString();

						if (cursor != null && cursor.getCount() > 0) {
							AsyncCallWSSummary task = new AsyncCallWSSummary();
							task.execute();
						} else {
							Toast.makeText(StockRequestSummary.this,
									"No data found", Toast.LENGTH_SHORT).show();
						}
					}
				});
		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		alertDialog.show();
	}

	@SuppressWarnings("deprecation")
	private void print() throws IOException {

		String title = "STOCK REQUEST";
		int nofcopies = Integer.parseInt(stnumber.getText().toString());
		String fromlocationcode = SupplierSetterGetter.getLocationcode();
		HashMap<String, String> location_code_name = new HashMap<String, String>();
		location_code_name = SupplierSetterGetter.getLoc_code_name();
		String fromlocation_code = location_code_name.get(fromlocationcode);
		String tolocationcode = SupplierSetterGetter.getLocCode();

		ArrayList<ProductDetails> product = new ArrayList<ProductDetails>();
		String soDate = SalesOrderSetGet.getSaleorderdate();
		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		product = SOTDatabase.requestproducts();
		String remark =SalesOrderSetGet.getRemarks();
		helper.dismissProgressDialog();
		try {
			Printer printer = new Printer(StockRequestSummary.this, macaddress);
			printer.setOnCompletionListener(new Printer.OnCompletionListener() {

				@Override
				public void onCompleted() {
					// TODO Auto-generated method stub
					finish();
					Intent i = new Intent(StockRequestSummary.this,
							StockRequestHeader.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					StockRequestSummary.this.finish();
				}
			});
			printer.printStockRequest(summaryResult, soDate, fromlocation_code,
					tolocationcode, product, title, nofcopies, remark);
		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}

		save.setVisibility(View.INVISIBLE);
		mEmpty.setVisibility(View.VISIBLE);
		SOTDatabase.deleteAllProduct();
		mListView.setAdapter(null);
		cursor.requery();

		sm_total.setText("");
		sm_tax.setText("");
		sm_header_layout.setVisibility(View.GONE);
		sm_netTotal.setText("");

		SOTDatabase.deleteBillDisc();

		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");
	}

	public void navigate() {

		if (RowItem.isAfterPrint() == true) {
			RowItem.setAfterPrint(false);
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(String item) {
		menu.toggle();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// Getting reference to TextView tv_longitude
		Log.d("Location", "gps "+location.getLatitude());
		try {
			getAddress(location.getLatitude(), location.getLongitude());
		} catch (Exception e) {
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
	
	
	 /* Request updates at startup */
	  @Override
	  protected void onResume() {
	    super.onResume();
//	    locationManager.requestLocationUpdates(provider, 1000, 1, this);
	  }

	  /* Remove the locationlistener updates when Activity is paused */
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	  }

	public static PropertyInfo newPropertyInfo(String name, String value) {
		PropertyInfo propertyInfo = new PropertyInfo();
		propertyInfo.setName(name);
		propertyInfo.setValue(value);
		return propertyInfo;
	}

	private class GetProduct implements SoapAccessTask.CallbackInterface {

		String prod_code = "",SR_slNo="";

		public GetProduct(String productCode,String SR_slNo) {
			this.prod_code = productCode;
			this.SR_slNo = SR_slNo;
		}

		@Override
		public void onSuccess(JSONArray jsonArray) {
			try {
				int len = jsonArray.length();
				for (int i = 0; i < len; i++) {
					JSONObject object = jsonArray.getJSONObject(i);

					String haveBatch = object.getString("HaveBatch");
					String haveExpiry = object.getString("HaveExpiry");
					String prodName = object.getString("ProductName");

					Log.d("thaveBatch", haveBatch);
					Log.d("thaveExpiry", haveExpiry);
					Log.d("tprod_code", prod_code);

					SOTDatabase.updateproductbatch(prod_code, haveBatch,
							haveExpiry);

					if (haveBatch.matches("True") || haveExpiry.matches("True")) {

						String cmpnyCode = SupplierSetterGetter
								.getCompanyCode();
						// String locCode = SalesOrderSetGet.getFromLoc();
						String locCode = SalesOrderSetGet.getLocationcode();

						ArrayList<PropertyInfo> params = new ArrayList<PropertyInfo>();
						params.add(newPropertyInfo("CompanyCode", cmpnyCode));
						params.add(newPropertyInfo("LocationCode", locCode));
						params.add(newPropertyInfo("ProductCode", prod_code));

						Log.d("tcmpnyCode", cmpnyCode);
						Log.d("tlocCode", locCode);

						new SoapAccessTask(StockRequestSummary.this, valid_url,
								"fncGetProductBatchStock", params,
								new GetBatchStock(haveBatch, haveExpiry,
										prodName, prod_code, SR_slNo)).execute();
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(Exception error) {

		}
	}

	private class GetBatchStock implements SoapAccessTask.CallbackInterface {

		String havebatch = "", haveexpiry = "", prodName = "", prodCode = "", SR_slNo="";

		public GetBatchStock(String haveBatch, String haveExpiry,
							 String prodName, String prod_code,String SR_slNo) {
			this.havebatch = haveBatch;
			this.haveexpiry = haveExpiry;
			this.prodName = prodName;
			this.prodCode = prod_code;
			this.SR_slNo = SR_slNo;
		}

		@Override
		public void onSuccess(JSONArray jsonArray) {
			try {

				int len = jsonArray.length();
				for (int i = 0; i < len; i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					int qty_nt = 0;
					int q = 0, r = 0;
					String cqty = "", lqty = "";

					String qty = object.getString("Qty");
					String cartonPerQty = object.getString("PcsPerCarton");
					String NoOfCarton = object.getString("NoOfCarton");
					double dQty = Double.parseDouble(qty);
					Log.d("qty", "" + qty);
					Log.d("cartonPerQty", "" + cartonPerQty);
					if (!cartonPerQty.matches("")) {
						if (!qty.matches("")) {
							try {
								qty_nt = (int) dQty;
								int pcs_nt = Integer.parseInt(cartonPerQty);

								Log.d("qty_nt", "" + qty_nt);
								Log.d("pcs_nt", "" + pcs_nt);

								q = qty_nt / pcs_nt;
								r = qty_nt % pcs_nt;

								cqty = "" + q;
								lqty = "" + r;

							} catch (ArithmeticException e) {
								System.out.println("Err: Divided by Zero");

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}

					calCarton = LogOutSetGet.getCalcCarton();
					if (calCarton.matches("0")) {
						cqty = NoOfCarton;
						lqty = "0";

					}

					double cartonqty = 0;

					if (calCarton.matches("0")) {

						if (!cqty.matches("")) {
							cartonqty = Double.parseDouble(cqty);

							if (cartonqty > 0 || qty_nt > 0) {
								int batchSlNo = i + 1;

								HashMap<String, String> hmValue = new HashMap<String, String>();
								// hmValue.put("id", bat_id);
								hmValue.put("slNo", batchSlNo + "");
								hmValue.put("ProductCode", prodCode);
								hmValue.put("ProductName", prodName);
								hmValue.put("BatchNo",
										object.getString("BatchNo"));
								hmValue.put("ExpiryDate",
										object.getString("ExpiryDate"));
								hmValue.put("AvailCQty", cqty);
								hmValue.put("AvailLQty", lqty);
								hmValue.put("AvailQty", "" + qty_nt);
								hmValue.put("CQty", "0");
								hmValue.put("LQty", "0");
								hmValue.put("Qty", "0");
								hmValue.put("PcsPerCarton", cartonPerQty);
								hmValue.put("HaveBatch", havebatch);
								hmValue.put("HaveExpiry", haveexpiry);
								hmValue.put("SR_Slno", SR_slNo);

								Log.d("hmValue", "" + hmValue.toString());

								SOTDatabase.storeBatch(hmValue);
							}
						}

					} else {
						if (qty_nt > 0) {
							int batchSlNo = i + 1;

							HashMap<String, String> hmValue = new HashMap<String, String>();
							// hmValue.put("id", bat_id);
							hmValue.put("slNo", batchSlNo + "");
							hmValue.put("ProductCode", prodCode);
							hmValue.put("ProductName", prodName);
							hmValue.put("BatchNo", object.getString("BatchNo"));
							hmValue.put("ExpiryDate",
									object.getString("ExpiryDate"));
							hmValue.put("AvailCQty", cqty);
							hmValue.put("AvailLQty", lqty);
							hmValue.put("AvailQty", "" + qty_nt);
							hmValue.put("CQty", "0");
							hmValue.put("LQty", "0");
							hmValue.put("Qty", "0");
							hmValue.put("PcsPerCarton", cartonPerQty);
							hmValue.put("HaveBatch", havebatch);
							hmValue.put("HaveExpiry", haveexpiry);
							hmValue.put("SR_Slno", SR_slNo);

							Log.d("hmValue", "" + hmValue.toString());

							SOTDatabase.storeBatch(hmValue);
						}
					}

				}

			} catch (JSONException e) {
			}
		}

		@Override
		public void onFailure(Exception error) {

		}
	}


	@Override
	public void onBackPressed() {

		Intent i = new Intent(StockRequestSummary.this,
				StockRequestAddProduct.class);
		startActivity(i);
		StockRequestSummary.this.finish();
	}
}