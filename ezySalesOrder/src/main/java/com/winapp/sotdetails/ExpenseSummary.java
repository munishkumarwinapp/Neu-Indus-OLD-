package com.winapp.sotdetails;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.acl.Group;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.model.Product;
import com.winapp.printcube.printcube.BluetoothService;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.printer.Printer;
import com.winapp.printer.PrinterZPL;
import com.winapp.printer.UIHelper;
import com.winapp.sot.CaptureSignature;
import com.winapp.sot.ConvertToSetterGetter;
import com.winapp.sot.InvoiceSummary;
import com.winapp.sot.ProductDetails;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SOTSummaryWebService;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.sot.TransferSummary;
import com.winapp.sot.WebServiceClass;

public class ExpenseSummary extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace , GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
	SlidingMenu menu;
	ProgressBar progressBar;
	LinearLayout spinnerLayout, expense_summary_layout, customer_layout,
			smry_header_layout, noofprint_layout;
	ImageButton back, save;
	TextView listing_screen, customer_screen, addProduct_screen,
			summary_screen, mEmpty;
	ListView expensesummary_list;
	EditText expense_totalAmt, dialodAmt, sl_remarks, sm_location,sm_netTotal,sm_tax,sm_subTotal;
	CheckBox enableprint;

	String valid_url = "", totalAmt = "", summaryResult = "",subTotalAmt = "", netTotalAmt = "" ,taxAmt = "" ;
	ArrayList<HashMap<String, String>> summarylist = new ArrayList<HashMap<String, String>>();
	Cursor mCursor = null;
	private ArrayList<HashMap<String, String>> eoHeaderArr, eoDetailsArr;
	ListAdapter adapter;
	private UIHelper helper;
	int nofcopies = 1;
	ArrayList<ProductDetails> product = new ArrayList<ProductDetails>();
	String date, macaddress ,  title;
	private ImageView camera, signature, location, prodphoto, sosign;
	private static final int PICK_FROM_CAMERA = 1;
	public static final int SIGNATURE_ACTIVITY = 2;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private String lat, lon,printStr="";
	private LocationManager locationManager;
	private Location mLastLocation;
	private String mCurrentgetPath="",mCurrentPhotoPath="",slNo;
	private String signature_img, product_img,address1 = "", address2 = "",soNo = "",taxtype = "";
	private double setLatitude, setLongitude;
	private boolean isGPSEnabled = false;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private double tax,subTot,NetTot;
	String getSignatureimage= "",getPhotoimage = "",gropNo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.expense_summary);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.summary_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Expense Summary");
		back = (ImageButton) customNav.findViewById(R.id.back);
		save = (ImageButton) customNav.findViewById(R.id.save);

		back.setVisibility(View.INVISIBLE);

		GlobalData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		GlobalData.mService = new BluetoothService(this, mHandler);

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

		FWMSSettingsDatabase.init(ExpenseSummary.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,ExpenseSummary.this);
		new WebServiceClass(valid_url);
		new SOTSummaryWebService(valid_url);
		SOTDatabase.init(ExpenseSummary.this);
		helper = new UIHelper(ExpenseSummary.this);

		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen = (TextView) findViewById(R.id.sum_screen);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);
		customer_screen.setVisibility(View.GONE);
		customer_layout.setVisibility(View.GONE);
//		summary_screen.setBackgroundColor(Color.parseColor("#626776"));
		summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
		summary_screen.setBackgroundResource(drawable.tab_select);

		smry_header_layout = (LinearLayout) findViewById(R.id.smry_header_layout);
		expense_summary_layout = (LinearLayout) findViewById(R.id.expense_summary_layout);
		expense_totalAmt = (EditText) findViewById(R.id.expense_totalAmt);
		expensesummary_list = (ListView) findViewById(android.R.id.list);
		mEmpty = (TextView) findViewById(android.R.id.empty);
		sm_netTotal = (EditText) findViewById(R.id.sm_netTotal);
		sm_tax = (EditText) findViewById(R.id.sm_tax);
		sm_subTotal = (EditText) findViewById(R.id.sm_subTotal);
		eoHeaderArr = new ArrayList<HashMap<String, String>>();
		eoDetailsArr =new ArrayList<HashMap<String, String>>();

		camera = (ImageView) findViewById(R.id.sm_camera_iv);
		signature = (ImageView) findViewById(R.id.sm_sign_iv);
		location = (ImageView) findViewById(R.id.sm_loc_iv);
		prodphoto = (ImageView) findViewById(R.id.prod_photo);
		sosign = (ImageView) findViewById(R.id.sm_signature);
		sm_location = (EditText) findViewById(R.id.sm_location);
		sl_remarks = (EditText) findViewById(R.id.sl_remarks);

		totalAmt = SOTDatabase.getExpenseTotalAmt();
		if (totalAmt != null && !totalAmt.isEmpty()) {
			expense_totalAmt.setText(totalAmt);
		} else {
			expense_totalAmt.setText("0.00");
		}

		subTotalAmt = SOTDatabase.getExpenseTotalSubTot();
		if (subTotalAmt != null && !subTotalAmt.isEmpty()) {
			sm_subTotal.setText(subTotalAmt);
		} else {
			sm_subTotal.setText("0.00");
		}

		netTotalAmt = SOTDatabase.getExpenseTotalNetTot();
		if (netTotalAmt != null && !netTotalAmt.isEmpty()) {
			sm_netTotal.setText(netTotalAmt);
		} else {
			sm_netTotal.setText("0.00");
		}

		taxAmt = SOTDatabase.getExpenseTotalTax();
		if (taxAmt != null && !taxAmt.isEmpty()) {
			sm_tax.setText(taxAmt);
		} else {
			sm_tax.setText("0.00");
		}



		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		buildGoogleApiClient();

		if (!mGoogleApiClient.isConnected())
			mGoogleApiClient.connect();

		signature_img = SOTDatabase.getSignatureImage();
		product_img = SOTDatabase.getProductImage();

		Bundle extras = getIntent().getExtras();

		if (extras != null) {

			eoDetailsArr.clear();

			eoDetailsArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("SODetails");
			eoHeaderArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("SOHeader");

			Log.d("eoDetailsArr",eoDetailsArr.toString());
			Log.d("eoHeaderArr",eoHeaderArr.toString());

//			getSignatureimage = (String) getIntent().getSerializableExtra(
//					"getSignatureimage");
//			getPhotoimage = (String) getIntent().getSerializableExtra(
//					"getPhotoimage");

//			SOTDatabase.storeImage(1, getSignatureimage, getPhotoimage);

			Log.d("eoHeaderArr", "" + eoHeaderArr);
			Log.d("eoDetailsArr", "" + eoDetailsArr);

			if (eoHeaderArr != null) {
				for (int i = 0; i < eoHeaderArr.size(); i++) {

					soNo = eoHeaderArr.get(i).get("SoNo");


					String TranDate = eoHeaderArr.get(i)
							.get("TranDate").split("\\ ")[0];
					String LocationCode = eoHeaderArr.get(i)
							.get("LocationCode");
					String TranType = eoHeaderArr.get(i)
							.get("TranType");
					String ReferenceNo = eoHeaderArr.get(i).get("ReferenceNo");
					String PayTo = eoHeaderArr.get(i)
							.get("PayTo");
					String GroupNo = eoHeaderArr.get(i)
							.get("GroupNo");
					String GroupName = eoHeaderArr.get(i)
							.get("GroupName");
					String Remarks = eoHeaderArr.get(i).get("Remarks");
					String SubTotal = eoHeaderArr.get(i).get("SubTotal");
					String Tax = eoHeaderArr.get(i)
							.get("Tax");
					String NetTotal = eoHeaderArr.get(i)
							.get("NetTotal");

					sm_netTotal.setText(NetTotal);
					sm_subTotal.setText(SubTotal);
					sm_tax.setText(Tax);

					// SalesOrderSetGet.setSaleorderdate(SoDate);
					 SalesOrderSetGet.setDeliverydate(TranDate);
					SalesOrderSetGet.setLocationcode(LocationCode);
					SalesOrderSetGet.setSuppliercode(GroupNo);
					SalesOrderSetGet.setSuppliergroupcode(GroupName);
					SalesOrderSetGet.setPayTo(PayTo);
					SalesOrderSetGet.setRemarks(Remarks);
				}
			}

			if (eoDetailsArr != null) {
				for (int i = 0; i < eoDetailsArr.size(); i++) {

					String slNo = eoDetailsArr.get(i).get("slNo");
					String TranNo = eoDetailsArr.get(i).get("TranNo");
					String GroupNo = eoDetailsArr.get(i).get("GroupNo");
					String GroupName = eoDetailsArr.get(i).get("GroupName");
					String Price = eoDetailsArr.get(i).get("Price");
					String SubTotal = eoDetailsArr.get(i).get("SubTotal");
					String Tax = eoDetailsArr.get(i).get("Tax");
					String NetTotal = eoDetailsArr.get(i).get("NetTotal");
					String TaxCode = eoDetailsArr.get(i).get("TaxCode");
					String TaxPerc = eoDetailsArr.get(i).get("TaxPerc");
					String TranType = eoDetailsArr.get(i).get("TranType");

					Log.d("CheckGroupNo", GroupName + "," + GroupNo);

					if (TranType.matches("I")) {
						taxtype = "Tax Inclusive";
					} else if (TranType.matches("E")) {
						taxtype = "Standard Rate";
					} else if (TranType.matches("Z")) {
						taxtype = "Zero Rate";
					}

					HashMap<String, String> hmvalues = new HashMap<String, String>();
					hmvalues.put("SlNo", slNo + "");
					hmvalues.put("AccountNo", GroupNo);
					hmvalues.put("Description", GroupName);
					hmvalues.put("Amount", Price);
					hmvalues.put("tax_type", TranType);
					hmvalues.put("tax_value", TaxPerc);
					hmvalues.put("subTotal", SubTotal);
					hmvalues.put("tax", Tax);
					hmvalues.put("netTotal", NetTotal);
					hmvalues.put("tax_name",taxtype);
					hmvalues.put("tax_code", TaxCode);
					SOTDatabase.storeExpanse(hmvalues);
				}

				Cursor cursor = SOTDatabase.getExpenseCursor();
				Log.d("cursorCount",""+cursor.getCount());
				if (cursor != null && cursor.getCount() > 0) {
					if (cursor.moveToFirst()) {
						do {
							String accountNo = cursor.getString(cursor
									.getColumnIndex(SOTDatabase.COLUMN_ACCOUNTNO));

							Log.d("COLUMN_ACCOUNTNO",accountNo);

						} while (cursor.moveToNext());
						cursor.requery();
					}

				}
				adapter = new ListAdapter(this, cursor);
				expensesummary_list.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		}


		mCursor = SOTDatabase.getExpenseCursor();

		if (mCursor.getCount() > 0) {
			mEmpty.setVisibility(View.GONE);
			smry_header_layout.setVisibility(View.VISIBLE);
			expensesummary_list.setVisibility(View.VISIBLE);
			adapter = new ListAdapter(this, mCursor);
			expensesummary_list.setAdapter(adapter);
			save.setVisibility(View.VISIBLE);
		} else {
			mEmpty.setVisibility(View.VISIBLE);
			smry_header_layout.setVisibility(View.GONE);
			expensesummary_list.setVisibility(View.GONE);
			save.setVisibility(View.INVISIBLE);
		}
		registerForContextMenu(expensesummary_list);

		if (product_img != null && !product_img.isEmpty()) {
			Log.d("invoice sum photo if ", product_img);
			try {

				mCurrentgetPath = Product.getPath();

//    }
				byte[] encodePhotoByte = Base64.decode(product_img, Base64.DEFAULT);
				Bitmap photo = BitmapFactory.decodeByteArray(encodePhotoByte, 0,
						encodePhotoByte.length);
				//photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.PNG, 100, stream);

//
				prodphoto.setImageBitmap(photo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

		}

		if (signature_img != null && !signature_img.isEmpty()) {
			Log.d("invoice sum sign if ", signature_img);
			try {
				byte[] encodeByte = Base64
						.decode(signature_img, Base64.DEFAULT);

				// String s;
				// try {
				// s = new String(encodeByte1, "UTF-8");
				// encodeByte = Base64.decode(s, Base64.DEFAULT);
				//
				//
				// } catch (Exception e) {
				// e.printStackTrace();
				// encodeByte = encodeByte1;
				// }

				Bitmap sign = BitmapFactory.decodeByteArray(encodeByte, 0,
						encodeByte.length);
				sign = Bitmap.createScaledBitmap(sign, 240, 80, true);
				sosign.setImageBitmap(sign);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {

		}




		String remarks = SalesOrderSetGet.getRemarks();

		if(remarks!=null && !remarks.isEmpty()){

		}else{
			remarks="";
		}

		sl_remarks.setText(remarks);

		/** -- OnClick Start -- **/

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				saveAlertDialog();
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(ExpenseSummary.this, ExpenseAdd.class);
				startActivity(i);
				ExpenseSummary.this.finish();
			}
		});

		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Cursor cursor = SOTDatabase.getExpenseCursor();
				int count = cursor.getCount();
				if (count > 0) {
					deleteAlertDialog();
				} else {

					Intent i = new Intent(ExpenseSummary.this,
							ExpenseHeader.class);
					startActivity(i);
					ExpenseSummary.this.finish();
				}

			}
		});

		camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CameraAction(PICK_FROM_CAMERA);
			}
		});

		signature.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent i = new Intent(ExpenseSummary.this,
						CaptureSignature.class);
				startActivityForResult(i, SIGNATURE_ACTIVITY);

			}
		});

		addProduct_screen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent i = new Intent(ExpenseSummary.this, ExpenseAdd.class);
				startActivity(i);
				ExpenseSummary.this.finish();
			}
		});


		/** OnClick End **/

	}

	/** Context Menu Start **/

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

			String id = ((TextView) info.targetView.findViewById(R.id.exp_slid))
					.getText().toString();
			String sno = ((TextView) info.targetView
					.findViewById(R.id.exp_slno)).getText().toString();
			String code = ((TextView) info.targetView
					.findViewById(R.id.expense_code)).getText().toString();
			String desc = ((TextView) info.targetView
					.findViewById(R.id.expense_desc)).getText().toString();
			String amount = ((TextView) info.targetView
					.findViewById(R.id.expense_amt)).getText().toString();

			editAlertDialog(id, desc, amount);

		} else if (item.getTitle() == "Delete") {
			String id = ((TextView) info.targetView.findViewById(R.id.exp_slid))
					.getText().toString();
			SOTDatabase.deleteExpense(id);
			mCursor.requery();
			ArrayList<String> snoCount = new ArrayList<String>();
			snoCount = SOTDatabase.expenseIdcount();
			Log.d("snocount", "" + snoCount);
			for (int i = 0; i < snoCount.size(); i++) {
				int sno = 1 + i;
				HashMap<String, String> queryValues = new HashMap<String, String>();
				queryValues.put("_id", "" + snoCount.get(i));
				queryValues.put("SlNo", "" + sno);
				SOTDatabase.updateExpenseSlNO(queryValues);
			}
			if (mCursor != null && mCursor.getCount() > 0) {
				mCursor.requery();
				Toast.makeText(ExpenseSummary.this, "Deleted",
						Toast.LENGTH_LONG).show();
				mEmpty.setVisibility(View.GONE);
			} else {
				mEmpty.setVisibility(View.VISIBLE);
				save.setVisibility(View.INVISIBLE);
				smry_header_layout.setVisibility(View.GONE);
				expensesummary_list.setVisibility(View.GONE);
			}

			totalAmt = SOTDatabase.getExpenseTotalAmt();
			if (totalAmt != null && !totalAmt.isEmpty()) {
				expense_totalAmt.setText(totalAmt);
			} else {
				expense_totalAmt.setText("0.00");
			}

			subTotalAmt = SOTDatabase.getExpenseTotalSubTot();
			if (subTotalAmt != null && !subTotalAmt.isEmpty()) {
				sm_subTotal.setText(subTotalAmt);
			} else {
				sm_subTotal.setText("0.00");
			}

			netTotalAmt = SOTDatabase.getExpenseTotalNetTot();
			if (netTotalAmt != null && !netTotalAmt.isEmpty()) {
				sm_netTotal.setText(netTotalAmt);
			} else {
				sm_netTotal.setText("0.00");
			}

			taxAmt = SOTDatabase.getExpenseTotalTax();
			if (taxAmt != null && !taxAmt.isEmpty()) {
				sm_tax.setText(taxAmt);
			} else {
				sm_tax.setText("0.00");
			}

		} else {
			return false;
		}
		return true;

	}


	/** Context Menu End **/

	/** AsyncTask Start **/
	private class AsyncCallExpenseSummary extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			loadprogress();
			summaryResult = "";
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {

				summaryResult = SOTSummaryWebService
						.summaryExpenseService("fncSaveAccVoucher ",soNo);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d("summary Result", summaryResult);
try {
	progressBar.setVisibility(View.GONE);
	spinnerLayout.setVisibility(View.GONE);
	enableViews(expense_summary_layout, true);

	if (summaryResult.matches("failed")) {
		Toast.makeText(ExpenseSummary.this, "Failed",
				Toast.LENGTH_SHORT).show();
	} else {

		signature_img = SOTDatabase.getSignatureImage();
		//product_img = SOTDatabase.getProductImage();

		mCurrentgetPath = Product.getPath();
//		Log.d("pathfind", mCurrentgetPath);

		if(mCurrentgetPath!=null && !mCurrentgetPath.isEmpty()){
			product_img = ImagePathToBase64Str(mCurrentgetPath);
		}else{
			mCurrentgetPath="";
		}



		if (signature_img == null) {
			signature_img = "";
		}
		if (product_img == null) {
			product_img = "";
		}

		// online
		String imgResult = SOTSummaryWebService
				.saveSignatureImage(summaryResult, ""
								+ setLatitude, "" + setLongitude,
						signature_img, product_img,
						"fncSaveInvoiceImages", "IN", address1,
						address2);

		Log.d("fncSaveInvoiceImages", "" + summaryResult + " "
				+ setLatitude + " " + setLongitude + " IN"
				+ address1 + address2 + "signature_img "
				+ signature_img + "product_img " + product_img);

		if (!imgResult.matches("")) {
			Log.d("Cap Image", "Saved");
			deleteDirectory(new File(Environment.getExternalStorageDirectory() + "/" + "SFA", "Image"));
		} else {
			Log.d("Cap Image", "Not Saved");
		}


		Toast.makeText(ExpenseSummary.this, "Saved Successfully",
				Toast.LENGTH_SHORT).show();

		if (enableprint.isChecked()) {
			if (FWMSSettingsDatabase
					.getPrinterAddress()
					.matches(
							"[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$")) {
				try {
					helper.showProgressDialog(R.string.generating_transfer);
					print();
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				helper.showLongToast(R.string.error_configure_printer);
				clearView();
			}
		} else {
			clearView();
		}

	}
}catch (Exception e){
	e.printStackTrace();
}
		}
	}

	/** AsyncTask end **/

	/** Adapter Start **/
	private class ListAdapter extends ResourceCursorAdapter {

		@SuppressWarnings("deprecation")
		public ListAdapter(Context context, Cursor cursor) {
			super(context, R.layout.expense_summary_list, cursor);
			Log.d("AdapterCount",""+cursor.getCount());
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			TextView exp_slid = (TextView) view.findViewById(R.id.exp_slid);
			exp_slid.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_PRODUCT_ID)));

			Log.d("COLUMN_ACCOUNTNO", cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_SNO)));

			TextView exp_slno = (TextView) view.findViewById(R.id.exp_slno);
			exp_slno.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_SNO)));

			TextView expense_code = (TextView) view
					.findViewById(R.id.expense_code);
			expense_code.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_ACCOUNTNO)));



			TextView expense_desc = (TextView) view
					.findViewById(R.id.expense_desc);
			expense_desc.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_DESC)));

			TextView expense_amt = (TextView) view
					.findViewById(R.id.expense_amt);
			expense_amt.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_AMOUNT)));

			TextView expense_sub = (TextView) view
					.findViewById(R.id.subtotal);
			expense_sub.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL)));

			TextView expense_tax = (TextView) view
					.findViewById(R.id.tax);
			expense_tax.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_TAX)));

			TextView expense_netTotal = (TextView) view
					.findViewById(R.id.netTotal);
			expense_netTotal.setText(cursor.getString(cursor
					.getColumnIndex(SOTDatabase.COLUMN_NETTOTAL)));

		}

	}

	/** Adapter End **/

	/** Print Start **/

	private void print() throws IOException {
		String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
		title = "Expense";
		// int nofcopies = Integer.parseInt(stnumber.getText().toString());

		String soDate = SalesOrderSetGet.getSaleorderdate();
		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		product = SOTDatabase.requestExpenseProducts();
		helper.dismissProgressDialog();
		try {
			if (printertype.matches("Zebra iMZ320")) {
			Printer printer = new Printer(ExpenseSummary.this, macaddress);
			printer.setOnCompletionListener(new Printer.OnCompletionListener() {

				@Override
				public void onCompleted() {
					// TODO Auto-generated method stub
					finish();
					Intent i = new Intent(ExpenseSummary.this,
							ExpenseHeader.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					ExpenseSummary.this.finish();
				}
			});
			printer.printExpense(summaryResult, soDate, product, title,
					nofcopies);
			} else if (printertype.matches("4 Inch Bluetooth")) {
                /*helper.showProgressDialog(InvoiceHeader.this.getString(R.string.print),
						InvoiceHeader.this.getString(R.string.creating_file_for_printing));*/
				helper.updateProgressDialog(ExpenseSummary.this.getString(R.string.creating_file_for_printing));
				if (BluetoothAdapter.checkBluetoothAddress(macaddress)) {
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device, true);

				}
				helper.dismissProgressDialog();
				//helper.dismissProgressDialog();
			}
			else if(printertype.matches("3 Inch Bluetooth Generic")) {
				helper.dismissProgressDialog();
				try {
					final CubePrint print = new CubePrint(ExpenseSummary.this, macaddress);
					print.initGenericPrinter();
					print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
						@Override
						public void initCompleted() {
							try {
								print.printExpense(summaryResult, date, product, title, nofcopies);
							}catch (Exception e){
								e.printStackTrace();
							}
							print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
								@Override
								public void onCompleted() {
									helper.showLongToast(R.string.printed_successfully);
								}
							});
						}
					});

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


			else if(printertype.matches("Zebra iMZ320 4 Inch")){
				PrinterZPL printer = new PrinterZPL(ExpenseSummary.this, macaddress);
				printer.setOnCompletionListener(new PrinterZPL.OnCompletionListener() {

					@Override
					public void onCompleted() {
						// TODO Auto-generated method stub
						finish();
						Intent i = new Intent(ExpenseSummary.this,
								ExpenseHeader.class);
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
						ExpenseSummary.this.finish();
					}
				});
				printer.printExpense(summaryResult, soDate, product, title,
						nofcopies);
			}
		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}

		mEmpty.setVisibility(View.VISIBLE);
		save.setVisibility(View.INVISIBLE);
		SOTDatabase.deleteallexpense();
		SalesOrderSetGet.setRemarks("");

	}

	/** Print End **/

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
					noofprint_layout.setVisibility(View.GONE);
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

						String remr = sl_remarks.getText().toString();

						SalesOrderSetGet.setRemarks(remr);

						if (mCursor != null && mCursor.getCount() > 0) {
							AsyncCallExpenseSummary task = new AsyncCallExpenseSummary();
							task.execute();
						} else {
							Toast.makeText(ExpenseSummary.this,
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

	public void editAlertDialog(final String id, String desc, String amount) {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(desc);
		 final LayoutParams lparams = new LayoutParams(150,80); // Width , height
		 lparams.setMargins(0, 10, 0, 10);
		 
		 TextView txtAmt = new TextView(this);
		 txtAmt.setLayoutParams(lparams);
		 txtAmt.setGravity(Gravity.CENTER);
		 txtAmt.setText("Amount");
		 
		dialodAmt = new EditText(this);
		dialodAmt.setLayoutParams(lparams);
		dialodAmt.setGravity(Gravity.CENTER);
		dialodAmt.setInputType(InputType.TYPE_CLASS_NUMBER);
		dialodAmt.setText(amount);
		dialodAmt.requestFocus(dialodAmt.getText().length());
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(dialodAmt,InputMethodManager.SHOW_IMPLICIT);
		
		 
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setGravity(Gravity.CENTER);
		ll.addView(txtAmt);
		ll.addView(dialodAmt);
		
		alertDialog.setView(ll);
		alertDialog.setCancelable(false);
		
		
		alertDialog.setPositiveButton("Update",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String mAmt = dialodAmt.getText().toString();

						if (mAmt != null && !mAmt.isEmpty()) {
							String txType = SOTDatabase.getColumnTaxtype(id);
							String txValue = SOTDatabase .getColumnTaxValue(id);
							Log.d("txType&Value","->"+txType+","+txValue);
							double taxvalue = Double.parseDouble(txValue);
							double amt = Double .parseDouble(mAmt);
							if(txType.matches("I")){
								tax = (amt / (100+taxvalue) ) * taxvalue;
								subTot = amt - tax;
								NetTot = amt;
							}else if(txType.matches("E")){
								tax = (amt /100) * taxvalue ;
								NetTot = amt + tax ;
								subTot = amt;
							}else if(txType.matches("Z")){
								NetTot = amt ;
								tax = 0.00 ;
								subTot = amt ;

							}
							String taxAmt = String.valueOf(twoDecimalPoint(tax));
							String netty = String.valueOf(twoDecimalPoint(NetTot));
							String subToty = String.valueOf(twoDecimalPoint(subTot));

							SOTDatabase.updateExpenseAmount(id, mAmt);
							SOTDatabase.updateMultiValues(id,taxAmt,netty,subToty,mAmt);

							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									dialodAmt.getWindowToken(), 0);
							
							mCursor.requery();
							totalAmt = SOTDatabase.getExpenseTotalAmt();
							if (totalAmt != null && !totalAmt.isEmpty()) {
								expense_totalAmt.setText(totalAmt);
							} else {
								expense_totalAmt.setText("0.00");
							}


							subTotalAmt = SOTDatabase.getExpenseTotalSubTot();
							if (subTotalAmt != null && !subTotalAmt.isEmpty()) {
								sm_subTotal.setText(subTotalAmt);
							} else {
								sm_subTotal.setText("0.00");
							}

							netTotalAmt = SOTDatabase.getExpenseTotalNetTot();
							if (netTotalAmt != null && !netTotalAmt.isEmpty()) {
								sm_netTotal.setText(netTotalAmt);
							} else {
								sm_netTotal.setText("0.00");
							}

							taxAmt = SOTDatabase.getExpenseTotalTax();
							if (taxAmt != null && !taxAmt.isEmpty()) {
								sm_tax.setText(taxAmt);
							} else {
								sm_tax.setText("0.00");
							}

							dialog.dismiss();
						}

					}
				});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								dialodAmt.getWindowToken(), 0);
						dialog.dismiss();
					}
				});
		alertDialog.show();
	}

	public void deleteAlertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Deleting");
		alertDialog.setMessage("Expense will clear. Do you want to proceed");
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(ExpenseSummary.this,
								ExpenseHeader.class);
						SOTDatabase.deleteallexpense();
						startActivity(i);
						ExpenseSummary.this.finish();
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
	public void clearView() {

		save.setVisibility(View.INVISIBLE);
		SOTDatabase.deleteallexpense();
		SalesOrderSetGet.setRemarks("");
		mEmpty.setVisibility(View.VISIBLE);

		Intent i = new Intent(ExpenseSummary.this, ExpenseHeader.class);
		startActivity(i);
		ExpenseSummary.this.finish();
	}

	public void loadprogress() {
		spinnerLayout = new LinearLayout(ExpenseSummary.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(expense_summary_layout, false);
		progressBar = new ProgressBar(ExpenseSummary.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		spinnerLayout.addView(progressBar);
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
	public void onStart() {
		super.onStart();
		//if(D) Log.e(TAG, "--- onStart ---");
		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

		if (printertype.matches("4 Inch Bluetooth")) {
			if (!GlobalData.mBluetoothAdapter.isEnabled()) {
				GlobalData.mBluetoothAdapter.enable();
				//Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				//startActivity(enableIntent);*/
				// Otherwise, setup the chat session
			} else {
				if (GlobalData.mService == null) {
					GlobalData.mService = new BluetoothService(this, mHandler);
				}
			}
		}
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case GlobalData.MESSAGE_STATE_CHANGE:
					Log.d("case", "MESSAGE_STATE_CHANGE");
					//if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
					switch (msg.arg1) {
						case GlobalData.STATE_CONNECTED:
							//mTitle.setText(R.string.title_connected_to);
							//mTitle.append(mConnectedDeviceName);
							//Intent intent = new Intent(BluetoothActivity.this,PrintActivity.class);
							//intent.putExtra("COMM", 0);//0-BLUETOOTH
							// Set result and finish this Activity
							//	startActivity(intent);
							Log.d("case", "STATE_CONNECTED");
							print4Inch();
							//helper.dismissProgressDialog();
							break;
						case GlobalData.STATE_CONNECTING:
							//mTitle.setText(R.string.title_connecting);
							Log.d("case", "STATE_CONNECTING");

							break;
						case GlobalData.STATE_LISTEN:
							Log.d("case", "STATE_LISTEN");
							break;
						case GlobalData.STATE_NONE:
							Log.d("case", "STATE_NONE");
							//mTitle.setText(R.string.title_not_connected);
							break;
					}
					break;
				case GlobalData.MESSAGE_DEVICE_NAME:
					// save the connected device's name
					String mConnectedDeviceName = msg.getData().getString("device_name");
					Toast.makeText(getApplicationContext(), "Connected to "
							+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
					break;
				case GlobalData.MESSAGE_TOAST:

					//String macaddress = FWMSSettingsDatabase.getPrinterAddress();
					Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
							Toast.LENGTH_SHORT).show();

					reconnectDialog(msg.getData().getString("toast"));
					/*if (BluetoothAdapter.checkBluetoothAddress(macaddress))
					{
						BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
						// Attempt to connect to the device
						GlobalData.mService.setHandler(mHandler);
						GlobalData.mService.connect(device,true);

						//print4Inch();
					}*/
					//	helper.dismissProgressDialog();
					break;
			}
			//helper.dismissProgressDialog();
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		if (GlobalData.mService != null) {
			GlobalData.mService.stop();
		}
	}

	public void reconnectDialog(String msg) {
		final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		final Dialog dialog = new Dialog(ExpenseSummary.this);

		dialog.setContentView(R.layout.dialog_reconnect);
		dialog.setTitle(msg);
		ImageView reconnect = (ImageView) dialog.findViewById(R.id.reconnect);

		reconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (BluetoothAdapter.checkBluetoothAddress(macaddress)) {
					BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
					// Attempt to connect to the device
					GlobalData.mService.setHandler(mHandler);
					GlobalData.mService.connect(device, true);

				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void print4Inch() {
		CubePrint mPrintCube = new CubePrint(ExpenseSummary.this, FWMSSettingsDatabase.getPrinterAddress());
		mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
			@Override
			public void onCompleted() {
				helper.showLongToast(R.string.printed_successfully);

			}
		});

		try {
			mPrintCube.printExpense(summaryResult, date, product, title, nofcopies);
		}catch (Exception e){
			e.printStackTrace();
		}
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
//					Bundle extras = data.getExtras();
//					if (extras != null) {
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

						sosign.setImageBitmap(bitmap);

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] bitMapData = stream.toByteArray();
						String signature_image = Base64.encodeToString(
								bitMapData, Base64.DEFAULT);
						SOTDatabase.init(ExpenseSummary.this);
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

	private void handleCameraPhoto(int actionCode) {
		Log.d("mCurrentPhotoPath", "mCurrentPhotoPath-->"+mCurrentPhotoPath);
		if (mCurrentPhotoPath != null) {
			switch (actionCode) {
				case PICK_FROM_CAMERA: {
					Product.setPath(mCurrentPhotoPath);
					decodeImagePathFile(mCurrentPhotoPath,prodphoto);
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
			SOTDatabase.init(ExpenseSummary.this);

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
		if(bitmap!=null) {
			ByteArrayOutputStream baos=new  ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] b = baos.toByteArray();
			temp = Base64.encodeToString(b, Base64.DEFAULT);
		}
		return temp;
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

	@Override
	public void onConnected(Bundle bundle) {
		Log.d("onConnected", "onConnected");

		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(100); // Update location every second

		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);

//		double lati=0,longi=0;

		if (mLastLocation != null) {
			lat = String.valueOf(mLastLocation.getLatitude());
			lon = String.valueOf(mLastLocation.getLongitude());

			if(lat!=null && !lat.isEmpty()){
				setLatitude = mLastLocation.getLatitude();
			}

			if(lon!=null && !lon.isEmpty()){
				setLongitude = mLastLocation.getLongitude();
			}
		}

		boolean interntConnection = isNetworkConnected();
		if (interntConnection == true) {

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if (isGPSEnabled == true) {
				Log.d("isGPSEnabled", ""+isGPSEnabled);
//				new getServerDateTime(lati, longi, "1").execute();

				updateUI(setLatitude,setLongitude);
			}else {
//				Log.d("gpsLocation", " null ");
//				new getServerDateTime(0, 0, "0").execute();
				updateUI(0,0);
//					Toast.makeText(getApplicationContext(),
//							"GPS is OFF. Please turn ON",
//							Toast.LENGTH_LONG).show();
			}

		} else {
			/*Toast.makeText(getApplicationContext(),
					"No Internet Connection",
					Toast.LENGTH_LONG).show();*/
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
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();


	}

	void updateUI(double lat,double lon) {
		try {

//			if (onlineMode.matches("True")) {
//				if (checkOffline == true) {
//				} else { // online
					getAddress(lat, lon);
//				}
//			}


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

					sm_location.setText(address1 + "," + address2);

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

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
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

	@Override
	public void onBackPressed() {
		Intent i = new Intent(ExpenseSummary.this, ExpenseAdd.class);
		startActivity(i);
		ExpenseSummary.this.finish();
	}

	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}

}
