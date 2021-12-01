package com.winapp.sot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.model.Schedule;
import com.winapp.model.StockCount;

@SuppressLint("ResourceAsColor")
public class StockTakeSummary extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace, OnClickListener,
		OnEditorActionListener, OnTouchListener, Constants, LocationListener{

	private SlidingMenu menu;
	private ImageButton mBack, mSave, mClear;
	private ListView mListView;
	private TextView mEmpty, mcurrentQty, mpcsperQty, mAddProduct, mDetail,
			mListing, mSummary;
	private ProductListAdapter adapter;
	private Cursor cursor;
	private LinearLayout /*
						 * sm_total_layout,sm_price_layout,sm_itemdisc_layout,
						 * sm_subtotal_layout, sm_tax_layout,sm_nettotal_layout,
						 */summary_header_layout,/*
												 * sm_arrow_layout,sm_foc_layout,
												 */slcamera_layout,
			noofprint_layout, mspinnerLayout, slsummary_layout, sltotal_layout,
			slqty_layout, sldiscount_layout, sm_header_layout, customer_layout;
	private CheckBox mcCheckBox;
	private ProgressBar mprogressBar;
	private String jsonString, batch_json = "", detail_json = "", header_json = "", companyCode,
			valid_url, locationCode, productCode, productName, qty, cQty, lQty,
			oldQty, pcsperCarton, stockDate, userName, mstockResult, productId, productSno,
			stockTakeNo, signature_img, product_img;
	private HashMap<String, String> mparam;
	private JSONObject jsonResponse;
	private JSONArray jsonMainNode;
	private ImageView arrowUpDown, mCamera, mSignature, mLocation, mPhoto,
			mSign;
	private EditText mCQty, mLQty, mTotalQty, mproductCode, mproductName,
			mcarton, mloose, mqty, sm_location;
	private boolean cameraflag = true;
	private ArrayList<String> mSNOCount;
	private static final int MENU0 = 0;
	private static final int MENU1 = 1;
	private static final int MENU2 = 2;
	private static final int PICK_FROM_CAMERA = 1;
	public static final int SIGNATURE_ACTIVITY = 2;
	private Intent mIntent;
	private Button mbt_DialogAction1, mbt_DialogAction2;
	private Dialog dialog;
	private GPSTracker gps;
	private double latitude, longitude;
	private Bundle mbundle;
	private ArrayList<StockCount> mStockCount;
	private int sl_no = 0;
	private ActionBar ab;
	BatchDialog batchDialog;

	// get location
	private LocationManager locationManager;
	private boolean isGPSEnabled = false, isNetworkEnabled = false;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
			
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);

		setContentView(R.layout.stocktake_summary);

		// Actionbar
		ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(false);

		// ActionBarCustom Layout
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.summary_actionbar_title, null);

		// Custom TextView
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("StockTake Summary");

		// Custom ImageButton
		mBack = (ImageButton) customNav.findViewById(R.id.back);
		mBack.setVisibility(View.INVISIBLE);
		mSave = (ImageButton) customNav.findViewById(R.id.save);

		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);

		// SlidingMenu
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);

		// ImageView
		mCamera = (ImageView) findViewById(R.id.sm_camera_iv);
		mSignature = (ImageView) findViewById(R.id.sm_sign_iv);
		mLocation = (ImageView) findViewById(R.id.sm_loc_iv);
		mPhoto = (ImageView) findViewById(R.id.prod_photo);
		mSign = (ImageView) findViewById(R.id.sm_signature);
		arrowUpDown = (ImageView) findViewById(R.id.arrow_image);

		// LinearLayout
		summary_header_layout = (LinearLayout) findViewById(R.id.summary_header_layout);
		slsummary_layout = (LinearLayout) findViewById(R.id.slsummary_layout);
		slcamera_layout = (LinearLayout) findViewById(R.id.slcamera_layout);
		sltotal_layout = (LinearLayout) findViewById(R.id.sltotal_layout);
		sm_header_layout = (LinearLayout) findViewById(R.id.sm_header_layout);
		slqty_layout = (LinearLayout) findViewById(R.id.slqty_layout);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);
		sldiscount_layout = (LinearLayout) findViewById(R.id.sldiscount_layout);

		// EditText
		sm_location = (EditText) findViewById(R.id.sm_location);
		mCQty = (EditText) findViewById(R.id.sm_cartonQty);
		mLQty = (EditText) findViewById(R.id.sm_looseQty);
		mTotalQty = (EditText) findViewById(R.id.sm_totalQty);

		// TextView
		mEmpty = (TextView) findViewById(android.R.id.empty);
		mAddProduct = (TextView) findViewById(R.id.addProduct_screen);
		mSummary = (TextView) findViewById(R.id.sum_screen);
		mDetail = (TextView) findViewById(R.id.customer_screen);
		mDetail.setVisibility(View.GONE);   // hide customer in stocktake
		mListing = (TextView) findViewById(R.id.listing_screen);
		// ListView
		mListView = (ListView) findViewById(android.R.id.list);

		batchDialog = new BatchDialog();
		
		mIntent = new Intent();
		mparam = new HashMap<String, String>();
		mSNOCount = new ArrayList<String>();
		mStockCount = new ArrayList<StockCount>();

		// ArrayList Clear
		mStockCount.clear();
		mparam.clear();
		mSNOCount.clear();

		// Init DB
		SOTDatabase.init(StockTakeSummary.this);
		cursor = SOTDatabase.getCursor();

		// Init Url
		FWMSSettingsDatabase.init(StockTakeSummary.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SalesOrderWebService(valid_url);

		companyCode = SupplierSetterGetter.getCompanyCode();
		locationCode = SalesOrderSetGet.getLocationcode();
		stockDate = SalesOrderSetGet.getSaleorderdate();
		userName = SupplierSetterGetter.getUsername();
		stockTakeNo = SalesOrderSetGet.getStockTakeNo();

		arrowUpDown.setImageResource(drawable.arrow_up);
//		mSummary.setBackgroundColor(Color.parseColor("#00AFF0"));
		
		mSummary.setTextColor(Color.parseColor("#FFFFFF"));
		mSummary.setBackgroundResource(drawable.tab_select_right);

		// OnClickListener
		mSave.setOnClickListener(this);
		arrowUpDown.setOnClickListener(this);
		mDetail.setOnClickListener(this);
		mAddProduct.setOnClickListener(this);
		mListing.setOnClickListener(this);
		mCamera.setOnClickListener(this);
		mSignature.setOnClickListener(this);
		mLocation.setOnClickListener(this);

		mDetail.setText("Detail");

		// Getting Total Cqty,LQTy And Qty
		if (cursor != null && cursor.getCount() > 0) {
			sm_header_layout.setVisibility(View.VISIBLE);
			mCQty.setText(String.format("%.2f", SOTDatabase.getTotalCqty()));
			mLQty.setText(String.format("%.2f", SOTDatabase.getTotalLqty()));
			mTotalQty.setText(String.format("%.2f", SOTDatabase.getTotalQty()));
			mEmpty.setVisibility(View.GONE);
		} else {
			sm_header_layout.setVisibility(View.GONE);
			mEmpty.setVisibility(View.VISIBLE);
		}

		// Visible Layout
		summary_header_layout.setVisibility(View.VISIBLE);
		slqty_layout.setVisibility(View.VISIBLE);

		// Invisible Layout
		sltotal_layout.setVisibility(View.GONE);
		sldiscount_layout.setVisibility(View.GONE);
		customer_layout.setVisibility(View.GONE);
		slcamera_layout.setVisibility(View.GONE);

		// Calling ProductListAdapter
		adapter = new ProductListAdapter(this, cursor);
		mListView.setAdapter(adapter);
		registerForContextMenu(mListView);

		mStockCount = Schedule.getStockcount();

		// Bundle
		mbundle = getIntent().getExtras();
		if (mbundle != null) {

//			mStockCount = mbundle
//					.getParcelableArrayList(ACTIVITY_STOCK_TAKE_PRODUCT);
//			mStockCount = (ArrayList<StockCount>) getIntent().getSerializableExtra(
//					ACTIVITY_STOCK_TAKE_PRODUCT);

		}
		signature_img = SOTDatabase.getSignatureImage();
		product_img = SOTDatabase.getProductImage();

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
							LocationManager.GPS_PROVIDER, 20000, 0, this);
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
		
		if (signature_img != null && !signature_img.isEmpty()) {
			Log.d("invoice sum sign if ", signature_img);
			byte[] encodeByte;

			byte[] encodeByte1 = Base64.decode(signature_img, Base64.DEFAULT);

			String s;
			try {
				s = new String(encodeByte1, "UTF-8");
				encodeByte = Base64.decode(s, Base64.DEFAULT);
			} catch (Exception e) {
				encodeByte = encodeByte1;
			}

			Bitmap photo = BitmapFactory.decodeByteArray(encodeByte, 0,
					encodeByte.length);
			photo = Bitmap.createScaledBitmap(photo, 240, 80, true);
			mSign.setImageBitmap(photo);

		}
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			loadSaveStockCount();
			break;
		case R.id.arrow_image:
			arrowUpDownAction();
			break;
		case R.id.clear_btn:
			clearViews();
			break;
		case R.id.addProduct_screen:
			mIntent.setClass(StockTakeSummary.this, StockTakeAddProduct.class);
			startActivity(mIntent);
			finish();
			break;
		case R.id.customer_screen:
//			mIntent.setClass(StockTakeSummary.this, StockTakeHeaderDetail.class);
//			mIntent.putExtra(ACTIVITY_STOCK_TAKE_PRODUCT, mStockCount);
//			startActivity(mIntent);
//			finish();
			break;

		case R.id.listing_screen:
			Cursor cursor = SOTDatabase.getCursor();
			int count = cursor.getCount();
			if(count>0){
				alertDialog();
			}else{

				mIntent.setClass(StockTakeSummary.this, StockTakeHeader.class);
				startActivity(mIntent);
				finish();
			}
			
			break;
		case R.id.mbt_DialogAction1:
			mbt_DialogAction1.setBackgroundColor(R.color.list_header_color);
			mbt_DialogAction1.setTextColor(Color.WHITE);
			dialog.dismiss();
			break;
		case R.id.mbt_DialogAction2:
			mbt_DialogAction2.setBackgroundColor(R.color.list_header_color);
			mbt_DialogAction2.setTextColor(Color.WHITE);
			updateStockCount();
			break;
		case R.id.sm_camera_iv:
			CameraAction();
			break;
		case R.id.sm_sign_iv:
			mIntent.setClass(StockTakeSummary.this, CaptureSignature.class);
			startActivityForResult(mIntent, SIGNATURE_ACTIVITY);
			break;
		case R.id.sm_loc_iv:
			if (!gps.canGetLocation()) {
				gps.showSettingsAlert();
			}
			break;
		default:
			break;
		}
	}

	private void arrowUpDownAction() {
		if (cameraflag) {
			cameraflag = false;
			arrowUpDown.setImageResource(drawable.arrow_up);
			slcamera_layout.setVisibility(View.GONE);

		} else if (cameraflag == false) {
			cameraflag = true;
			arrowUpDown.setImageResource(drawable.arrow_down);
			slcamera_layout.setVisibility(View.VISIBLE);

		}
	}

	private void clearViews() {
		mcarton.setText("");
		mloose.setText("");
		mqty.setText("");
	}

	TextWatcher clqtywatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			qtyCalc();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	TextWatcher qtywatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			qtyReverseCalc();
			/*
			 * mcarton.removeTextChangedListener(clqtywatcher);
			 * mcarton.setText("");
			 * mcarton.addTextChangedListener(clqtywatcher);
			 * 
			 * mloose.removeTextChangedListener(clqtywatcher);
			 * mloose.setText(""); mloose.addTextChangedListener(clqtywatcher);
			 */
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	public void CameraAction() {
		/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

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
						mPhoto.setImageBitmap(photo);

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] bitMapData = stream.toByteArray();
						String camera_image = Base64.encodeToString(bitMapData,
								Base64.DEFAULT);

						Cursor ImgCursor = SOTDatabase.getImageCursor();
						if (ImgCursor.getCount() > 0) {
							String signature_image = SOTDatabase
									.getSignatureImage();
							SOTDatabase.updateImage(1, signature_image,
									camera_image);
						} else {
							SOTDatabase.storeImage(1, "", camera_image);
						}

						Log.d("Camera Image", "cam" + camera_image);
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
						
						mSign.setImageBitmap(bitmap);
						
						 ByteArrayOutputStream stream = new ByteArrayOutputStream();
							bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] bitMapData = stream.toByteArray();
						String signature_image = Base64.encodeToString(
								bitMapData, Base64.DEFAULT);
						SOTDatabase.init(StockTakeSummary.this);
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
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		View TargetV = info.targetView;
		productId = ((TextView) TargetV.findViewById(R.id.ss_slid)).getText()
				.toString();
		productSno = ((TextView) info.targetView.findViewById(R.id.ss_slno))
				.getText().toString();
		productCode = ((TextView) TargetV.findViewById(R.id.ss_prodcode))
				.getText().toString();
		productName = ((TextView) TargetV.findViewById(R.id.ss_name)).getText()
				.toString();
		qty = ((TextView) TargetV.findViewById(R.id.ss_qty)).getText()
				.toString();
		cQty = ((TextView) TargetV.findViewById(R.id.ss_c_qty)).getText()
				.toString();
		lQty = ((TextView) TargetV.findViewById(R.id.ss_l_qty)).getText()
				.toString();
		oldQty = ((TextView) TargetV.findViewById(R.id.ss_current_qty))
				.getText().toString();
		pcsperCarton = ((TextView) TargetV.findViewById(R.id.ss_price))
				.getText().toString();
		menu.setHeaderTitle(productCode);
		
		String havebatch = SOTDatabase.getprodcodefrombatch(productCode);
		if (!havebatch.matches("")) {
			menu.add(Menu.NONE, MENU0, 0, "Edit Batch");
		}else{
			menu.add(Menu.NONE, MENU1, 1, "Edit");
		}
		
		
		menu.add(Menu.NONE, MENU2, 2, "Delete");
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case MENU0:
			mEditBatchProductStock();
			return true;
		case MENU1:
			mEditProductStock();
			return true;
		case MENU2:
			mDeleteProductStock();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}
	
	private void mEditBatchProductStock(){
		
			ArrayList<String> batchHave = new ArrayList<String>();
			batchHave = SOTDatabase.getBatchHave(productCode);

			String haveBatch = batchHave.get(0);
			String haveExpiry = batchHave.get(1);
			String slCartonPerQty = batchHave.get(2);

			String haveBatchOnStockIn = SalesOrderSetGet.getHaveBatchOnStockIn();
			if (haveBatchOnStockIn.matches("True")) {
				Log.d("haveBatchOnStockIn", haveBatchOnStockIn);
				if (haveBatch.matches("True") || haveExpiry.matches("True")) {
					Log.d("haveBatch", "haveExpiry");

					HashMap<String, EditText> hm = new HashMap<String, EditText>();
				 
					 hm.put("sm_cqty", mCQty);
					 hm.put("sm_lqty", mLQty);
					 hm.put("sm_qty", mTotalQty);

					batchDialog.initiateBatchPopupWindow(StockTakeSummary.this, productId, productSno,
							haveBatch, haveExpiry, productCode, productName, slCartonPerQty,
							cursor, hm,"");
					
				} else {
					Log.d("no haveBatch", "no haveExpiry");

				}

			} else {
				Log.d("no haveBatchOnStockIn", "no haveBatchOnStockIn");

			}

		
	}

	private void mEditProductStock() {
		dialog = new Dialog(StockTakeSummary.this);
		dialog.setTitle(productCode);
		dialog.setContentView(R.layout.edit_stock_dialog);

		mproductCode = (EditText) dialog.findViewById(R.id.st_codefield);
		mproductName = (EditText) dialog.findViewById(R.id.st_namefield);
		mcarton = (EditText) dialog.findViewById(R.id.st_cartonQty);
		mloose = (EditText) dialog.findViewById(R.id.st_looseQty);
		mqty = (EditText) dialog.findViewById(R.id.st_qty);
		mcurrentQty = (TextView) dialog.findViewById(R.id.st_currentQty);
		mpcsperQty = (TextView) dialog.findViewById(R.id.st_pcsperQty);
		mClear = (ImageButton) dialog.findViewById(R.id.clear_btn);
		mbt_DialogAction1 = (Button) dialog
				.findViewById(R.id.mbt_DialogAction1);
		mbt_DialogAction2 = (Button) dialog
				.findViewById(R.id.mbt_DialogAction2);
		mbt_DialogAction1.setText(R.string.cncl);
		mbt_DialogAction2.setText(R.string.update);

		mproductCode.setText(productCode);
		mproductName.setText(productName);

		mloose.setText(lQty);

		mcurrentQty.setText(oldQty);
		mpcsperQty.setText(pcsperCarton);

		mClear.setOnClickListener(this);
		mbt_DialogAction1.setOnClickListener(this);
		mbt_DialogAction2.setOnClickListener(this);

		mcarton.setOnEditorActionListener(this);
		mloose.setOnEditorActionListener(this);
		mqty.setOnEditorActionListener(this);

		mcarton.setOnTouchListener(this);
		mloose.setOnTouchListener(this);
		mqty.setOnTouchListener(this);

		mcarton.addTextChangedListener(clqtywatcher);
		mloose.addTextChangedListener(clqtywatcher);
		mqty.addTextChangedListener(qtywatcher);

		if (Integer.valueOf(pcsperCarton) > 2) {

			mcarton.setEnabled(true);
			mloose.setEnabled(true);
			mcarton.setText(Integer.valueOf(cQty) == 0 ? "" : cQty);
			mqty.setText(qty);
			if (Integer.valueOf(cQty) > 0) {
				mcarton.setSelection(cQty.length());
			}

			mcarton.requestFocus();
			showKeyboard(mcarton);

		} else {
			mcarton.setBackgroundColor(Color.parseColor("#F0F9FF"));
			mloose.setBackgroundColor(Color.parseColor("#F0F9FF"));
			mcarton.setEnabled(false);
			mloose.setEnabled(false);
			mcarton.setText(cQty);
			mqty.setText(Integer.valueOf(qty) == 0 ? "" : qty);
			if (Integer.valueOf(qty) > 0) {
				mqty.setSelection(qty.length());
			}
			mqty.requestFocus();
			showKeyboard(mqty);
		}
		dialog.setCancelable(false);
		dialog.show();
	}

	private void updateStockCount() {

		if ((mcarton.getText().toString() != null && !mcarton.getText()
				.toString().isEmpty())
				&& (mloose.getText().toString() != null && !mloose.getText()
						.toString().isEmpty())
				&& (mqty.getText().toString() != null && !mqty.getText()
						.toString().isEmpty())) {

			SOTDatabase.updateStockPrductDetail(productId, mproductCode
					.getText().toString(), mcarton.getText().toString(), mloose
					.getText().toString(), mqty.getText().toString());

			cursor.requery();
			adapter.notifyDataSetChanged();
			mCQty.setText(String.format("%.2f", SOTDatabase.getTotalCqty()));
			mLQty.setText(String.format("%.2f", SOTDatabase.getTotalLqty()));
			mTotalQty.setText(String.format("%.2f", SOTDatabase.getTotalQty()));
			Toast.makeText(getBaseContext(), "Updated", Toast.LENGTH_SHORT)
					.show();
			dialog.dismiss();
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.st_cartonQty:
			if (mcarton.getText().toString().equals("0")) {
				mcarton.setText("");
			} else {
				mcarton.setSelection(mcarton.getText().length());
			}
			break;
		case R.id.st_looseQty:
			if (mloose.getText().toString().equals("0")) {
				mloose.setText("");
			} else {
				mloose.setSelection(mloose.getText().length());
			}
			break;
		case R.id.st_qty:
			if (mqty.getText().toString().equals("0")) {
				mqty.setText("");
			} else {
				mqty.setSelection(mqty.getText().length());
			}
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		switch (v.getId()) {
		case R.id.st_cartonQty:
			if (actionId == EditorInfo.IME_ACTION_NEXT) {
				if (mloose.getText().toString().equals("0")) {
					mloose.setText("");
				} else {
					mloose.setSelection(mloose.getText().length());
				}
				mloose.requestFocus();
				return true;
			}
			break;
		case R.id.st_looseQty:
			if (actionId == EditorInfo.IME_ACTION_NEXT) {
				if (mqty.getText().toString().equals("0")) {
					mqty.setText("");
				} else {
					mqty.setSelection(mqty.getText().length());
				}
				mqty.requestFocus();
				return true;
			}
			break;
		case R.id.st_qty:
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				hideKeyboard();
				return true;
			}
			break;

		default:
			break;
		}
		return false;
	}

	public void mDeleteProductStock() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Confirm Delete");
		builder.setIcon(R.mipmap.delete);
		builder.setMessage("Do you want to delete?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				SOTDatabase.deleteStockProduct(productId, productCode);
				SOTDatabase.deleteBatchProductSR(productCode,productSno);
				// Update serial number
				mSNOCount = SOTDatabase.snoCountID();
				for (int i = 0; i < mSNOCount.size(); i++) {
					int sno = 1 + i;
					String old_slno = SOTDatabase.getProductSno(mSNOCount.get(i));
					HashMap<String, String> queryValues = new HashMap<String, String>();
					queryValues.put("_id", "" + mSNOCount.get(i));
					queryValues.put("snum", "" + sno);
					SOTDatabase.updateSNO(queryValues);
					SOTDatabase.updateBatchSR_slno(old_slno,"" + sno);
				}

				cursor.requery();
				adapter.notifyDataSetChanged();
				Toast.makeText(getBaseContext(), "Deleted", Toast.LENGTH_SHORT).show();
				if (cursor != null && cursor.getCount() > 0) {
					sm_header_layout.setVisibility(View.VISIBLE);
					mEmpty.setVisibility(View.GONE);
				} else {
					sm_header_layout.setVisibility(View.GONE);
					mEmpty.setVisibility(View.VISIBLE);
				}

				mCQty.setText(String.format("%.2f", SOTDatabase.getTotalCqty()));
				mLQty.setText(String.format("%.2f", SOTDatabase.getTotalLqty()));
				mTotalQty.setText(String.format("%.2f",
						SOTDatabase.getTotalQty()));
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void qtyCalc() {
		if (!mproductCode.getText().toString().equals("")) {
			int cqty = 0, lqty = 0, pcs = 0, qty;

			String cartonQty = mcarton.getText().toString();
			String looseQty = mloose.getText().toString();
			String pcsprcarton = mpcsperQty.getText().toString();

			mqty.removeTextChangedListener(qtywatcher);
			try {
				if (cartonQty != null && !cartonQty.isEmpty()) {
					cqty = Integer.valueOf(cartonQty);
				}
				if (looseQty != null && !looseQty.isEmpty()) {
					lqty = Integer.valueOf(looseQty);

				}
				if (pcsprcarton != null && !pcsprcarton.isEmpty()) {
					pcs = Integer.valueOf(pcsprcarton);
				}

				if (lqty > 0) {

					qty = cqty * pcs + lqty;
				} else {
					qty = cqty * pcs;

				}

				mqty.setText(String.valueOf(qty));

			} catch (Exception e) {
				e.printStackTrace();
			} catch (StackOverflowError e) {
				e.printStackTrace();
			}
			mqty.addTextChangedListener(qtywatcher);
		}
	}

	private void qtyReverseCalc() {
		if (!mproductCode.getText().toString().equals("")) {
			int cqty = 0, lqty = 0, pcs = 1, qty = 0;

			String pcsprcarton = mpcsperQty.getText().toString();
			String quantity = mqty.getText().toString();

			mcarton.removeTextChangedListener(clqtywatcher);
			mloose.removeTextChangedListener(clqtywatcher);

			try {

				if (pcsprcarton != null && !pcsprcarton.isEmpty()
						&& !pcsprcarton.equals("0")) {

					pcs = Integer.valueOf(pcsprcarton);
				}
				if (quantity != null && !quantity.isEmpty()) {
					qty = Integer.valueOf(quantity);

					if (qty > 0) {
						cqty = qty / pcs;
						lqty = qty % pcs;

						mcarton.setText("" + cqty);
						mloose.setText("" + lqty);

					}
				}
			} catch (ArithmeticException e) {
				e.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();
			} catch (StackOverflowError e) {
				e.printStackTrace();
			}
			mcarton.addTextChangedListener(clqtywatcher);
			mloose.addTextChangedListener(clqtywatcher);
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

	private void loadSaveStockCount() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setIcon(R.mipmap.ic_save);
		alertDialog.setTitle("Save");
		alertDialog.setMessage("Do you want to Save");
		LayoutInflater adbInflater = LayoutInflater.from(this);
		final View eulaLayout = adbInflater.inflate(R.layout.print_checkbox,
				null);
		noofprint_layout = (LinearLayout) eulaLayout
				.findViewById(R.id.noofcopieslblLayout);
		mcCheckBox = (CheckBox) eulaLayout.findViewById(R.id.checkbox);
		noofprint_layout.setVisibility(View.GONE);
		mcCheckBox.setVisibility(View.GONE);
		alertDialog.setView(eulaLayout);

		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int indexSelected) {

						loadStockCount();
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

	private void enableViews(View v, boolean enabled) {
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				enableViews(vg.getChildAt(i), enabled);
			}
		}
		v.setEnabled(enabled);
	}

	@SuppressWarnings("deprecation")
	private void progressBarOpen() {
		mspinnerLayout = new LinearLayout(StockTakeSummary.this);
		mspinnerLayout.setGravity(Gravity.CENTER);
		addContentView(mspinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		mspinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(slsummary_layout, false);
		mprogressBar = new ProgressBar(StockTakeSummary.this);
		mprogressBar.setProgress(android.R.attr.progressBarStyle);
		mprogressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		mspinnerLayout.addView(mprogressBar);
	}

	private void progressBarClose() {
		mprogressBar.setVisibility(View.GONE);
		mspinnerLayout.setVisibility(View.GONE);
		enableViews(slsummary_layout, true);
	}

	/************************** FNC_SAVESTOCKCOUNT ************************/
	private class GetSaveStockCount extends AsyncTask<Object, Void, Void> {
		@Override
		protected void onPreExecute() {

			progressBarOpen();
		}

		@Override
		protected Void doInBackground(Object... param) {
			String header = (String) param[0];
			String detail = (String) param[1];
			String batch = (String) param[2];

			 Log.d("header-->", header);
			 Log.d("detail-->", detail);
			 Log.d("batch-->", batch);

			mparam.put("CompanyCode", companyCode);
			mparam.put("sHeader", header);
			mparam.put("sDetail", detail);
			mparam.put("sBatchDetail", batch);

			jsonString = SalesOrderWebService.getSODetail(mparam, FNC_SAVESTOCKCOUNT);
			Log.d("jsonString ", "" + jsonString);

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {
					JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

					mstockResult = jsonChildNode.getString("Result");
				}

			} catch (JSONException e) {

				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mparam.clear();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBarClose();
			if ((mstockResult != null && !mstockResult.isEmpty())) {
				Toast.makeText(StockTakeSummary.this, "Saved Successfully",
						Toast.LENGTH_SHORT).show();
				mIntent.setClass(StockTakeSummary.this, StockTakeHeader.class);
				startActivity(mIntent);
				finish();
				SOTDatabase.deleteAllProduct();

				Schedule.setStockcount(null);
			} else {
				Toast.makeText(StockTakeSummary.this, "Failed",
						Toast.LENGTH_SHORT).show();
				cursor = SOTDatabase.getCursor();
				adapter = new ProductListAdapter(StockTakeSummary.this, cursor);
				mListView.setAdapter(adapter);
			}

		}
	}

	private void storeStockCount(String header, String detail, String batch) { // store
																	// carton,loose,qty(qty
																	// data is
																	// greater
																	// than 0
																	// and not
																	// equal to
																	// zero )and
																	// productcode
																	// are not
																	// exist in
																	// the db
		if (cursor != null && cursor.getCount() > 0) {
			// sl_no = cursor.getCount();
			// sl_no++;
			if (mStockCount != null && !mStockCount.isEmpty()) {

				for (StockCount stockcount : mStockCount) {

					String product_Code = SOTDatabase.getProductCode(stockcount
							.getProductCode());

					if ((product_Code != null && !product_Code.isEmpty())) {

						// existing product code in the db
						Log.d("productCode-->", "" + product_Code);
					} else {
						String product_Qty = String.valueOf(stockcount
								.getCountQty());
						if (product_Qty != null && !product_Qty.isEmpty()
								&& !product_Qty.equals("0")
								&& !product_Qty.equals("0.0")
								&& !product_Qty.equals("0.00")) {

							// Not existing product code in the db
							Log.d("productCode",
									"" + stockcount.getProductCode());
							Log.d("product_Qty", "" + product_Qty);

							// SOTDatabase.storeProduct(sl_no,
							// stockcount.getProductCode(),
							// stockcount.getProductName(),
							// stockcount.getCountCQty(),
							// stockcount.getCountLQty(),
							// stockcount.getCountQty(),
							// stockcount.getQty(), 0.0, 0.0, "",
							// stockcount.getPcsPerCarton(), 0.0, 0.0, "", "",
							// "", "",
							// "", "", "", "", "");

							detail += stockcount.getProductCode() + "^"
									+ stockcount.getQty() + "^"
									+ stockcount.getPcsPerCarton() + "^"
									+ stockcount.getCountCQty() + "^"
									+ stockcount.getCountLQty() + "^"
									+ stockcount.getCountQty() + "!";

							// sl_no++;

						}
					}

				}
			}
			// loadStockCount();
			detail = detail.substring(0, detail.length() - 1);
//			Log.d("Final Header", "" + header);
//			Log.d("Final Detail", "" + detail);
//			Log.d("Final Batch", "->" + batch);
			/************ Call GetSaveStockCount *****************/
			new GetSaveStockCount().execute(header, detail, batch);
		}
	}

	/************ Loading Header and Detail Json ******************/
	private void loadStockCount() {

		detail_json = "";
		header_json = "";
		cursor = SOTDatabase.getCursor();
		if (cursor != null && cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					
					String pSno= cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
					String pCode = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
					String cQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
					String lQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
					String qty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
					String oldQty = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_FOC));

					String pcsPerCarton = cursor.getString(cursor
							.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));

					detail_json += pCode + "^" + oldQty + "^" + pcsPerCarton
							+ "^" + cQty + "^" + lQty + "^" + qty + "!";
					
					Cursor batch_cursor = SOTDatabase.getBatchCursorWithSR(pCode, pSno+"");
					
					if (batch_cursor != null && batch_cursor.getCount() > 0) {

						if (batch_cursor.moveToFirst()) {
							do {
								
								String bpCode = batch_cursor.getString(batch_cursor
										.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
								
//								String batchSlNo = SOTDatabase.getProdSlno(slNo+"");					
								String bNo = batch_cursor.getString(batch_cursor
										.getColumnIndex(SOTDatabase.COLUMN_BATCH_NO));	
								String exDate = batch_cursor.getString(batch_cursor
										.getColumnIndex(SOTDatabase.COLUMN_EXPIRY_DATE));						
								String bcQty = batch_cursor.getString(batch_cursor
										.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
								String blQty = batch_cursor.getString(batch_cursor
										.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
								String bqty = batch_cursor.getString(batch_cursor
										.getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
//								String bfoc = batch_cursor.getString(batch_cursor
//										.getColumnIndex(SOTDatabase.COLUMN_FOC));
//								String bprice = batch_cursor.getString(batch_cursor
//										.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE));
									
								if(exDate!=null && !exDate.isEmpty()){
									
								}else{
									exDate= "01/01/1900";
								}
//								
//								batch_json += pSno + "^" + bpCode + "^" + bNo + "^" + exDate + "^"+ "01/01/1900" + "^" + "" + "^"
//										+ bcQty + "^" + blQty + "^" + bqty + "^" + "" + "^"
//										+ "" + "!";
								
								batch_json +=  bpCode + "^" + bNo + "^" + exDate + "^"+ "01/01/1900" + "^" + oldQty + "^"
										+ bcQty + "^" + blQty + "^" + bqty +  "!";
								
							} while (batch_cursor.moveToNext());
						} 
					}

				} while (cursor.moveToNext());
			}
			
			if(batch_json.matches("")){
			}else{
				 batch_json = batch_json.substring(0, batch_json.length() - 1);
			}
			
			header_json = stockTakeNo + "^" + stockDate + "^" + locationCode
					+ "^" + userName;
			
			 Log.d("DB Header", ""+header_json);
			 Log.d("DB Detail", ""+detail_json);
			 Log.d("DB Batch", "->"+batch_json);

			storeStockCount(header_json, detail_json, batch_json);
		}
		// else{
		// Toast.makeText(StockTakeSummary.this,
		// "No data found", Toast.LENGTH_SHORT).show();
		// }
	}

	// ProductListAdatpter
	private class ProductListAdapter extends ResourceCursorAdapter {
		@SuppressWarnings("deprecation")
		public ProductListAdapter(Context context, Cursor cursor) {
			super(context, R.layout.stocktake_summary_list, cursor);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			LinearLayout ss_total_layout = (LinearLayout) view
					.findViewById(R.id.ss_total_layout);
			LinearLayout ss_itemdisc_layout = (LinearLayout) view
					.findViewById(R.id.ss_itemdisc_layout);
			LinearLayout ss_tax_layout = (LinearLayout) view
					.findViewById(R.id.ss_tax_layout);
			LinearLayout ss_netTotal_layout = (LinearLayout) view
					.findViewById(R.id.ss_netTotal_layout);
			LinearLayout ss_subTotal_layout = (LinearLayout) view
					.findViewById(R.id.ss_subTotal_layout);

			LinearLayout ss_price_layout = (LinearLayout) view
					.findViewById(R.id.ss_price_layout);

			LinearLayout ss_currentQty_layout = (LinearLayout) view
					.findViewById(R.id.ss_currentQty_layout);
			LinearLayout ss_foc_layout = (LinearLayout) view
					.findViewById(R.id.ss_foc_layout);

			ss_total_layout.setVisibility(View.GONE);
			ss_itemdisc_layout.setVisibility(View.GONE);
			ss_tax_layout.setVisibility(View.GONE);
			ss_netTotal_layout.setVisibility(View.GONE);
			ss_price_layout.setVisibility(View.GONE);
			ss_subTotal_layout.setVisibility(View.GONE);
			ss_foc_layout.setVisibility(View.GONE);
			ss_currentQty_layout.setVisibility(View.VISIBLE);

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

			TextView ss_c_qty = (TextView) view.findViewById(R.id.ss_c_qty);
			ss_c_qty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));

			TextView ss_l_qty = (TextView) view.findViewById(R.id.ss_l_qty);
			ss_l_qty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));

			TextView ss_old_qty = (TextView) view
					.findViewById(R.id.ss_current_qty);
			ss_old_qty.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_FOC)));

			TextView ss_pcspercarton = (TextView) view
					.findViewById(R.id.ss_price);
			ss_pcspercarton.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY)));

		}
	}

	protected void showKeyboard(EditText editText) {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(editText, 0);
	}

	protected void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (getCurrentFocus() != null) {
			inputManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Deleting");
		alertDialog
				.setMessage("Stock Take products will clear. Do you want to proceed");
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						mIntent.setClass(StockTakeSummary.this, StockTakeHeader.class);
						startActivity(mIntent);
						finish();
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

	@Override
	public void onResume() {
		super.onResume();
	}
	
	 /* Request updates at startup */
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	  }

	@Override
	public void onBackPressed() {
		mIntent.setClass(StockTakeSummary.this, StockTakeAddProduct.class);
		startActivity(mIntent);
		finish();
	}
}
