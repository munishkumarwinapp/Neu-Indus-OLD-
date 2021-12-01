package com.winapp.fwms;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.sot.RowItem;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.util.ErrorLog;

public class FWMSValidateURL extends Activity {

	EditText ed_validate;
	Button bt_validate;
	String validate_url;
	String vl_result;
	String get_url;
	LinearLayout validate_parent;
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	String gnrlStngs = "",dsplyStngs;
	EditText input,input1;
	TextView textview,textview1;
	private String DEVICE_ID;
	String validateDevice;
	String randomText;
	private static final int MAXIMUM_BIT_LENGTH = 75;
    private static final int RADIX = 32;
    Cursor cursor;
    String randomdeviceidno;
    ArrayList<HashMap<String, String>> cmpySpnArr;
    AlertDialog mDialog;
	private ErrorLog errorLog;
	private ImageView img_online;
	private OfflineCommon offlineCommon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.validate);

		validate_parent = (LinearLayout) findViewById(R.id.validate_parent);
		ed_validate = (EditText) findViewById(R.id.ed_validate);
		bt_validate = (Button) findViewById(R.id.bt_validate);

		img_online = (ImageView) findViewById(R.id.img_online);

		cmpySpnArr = new ArrayList<HashMap<String, String>>();
		DEVICE_ID = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
		FWMSSettingsDatabase.init(FWMSValidateURL.this);
		get_url = FWMSSettingsDatabase.getUrl();
		SalesOrderSetGet.setGeneralsetting("");
		RandomDeviceIDDataBase.init(FWMSValidateURL.this);

		Cursor crsr = OfflineDatabase.getDownloadStatusCount();
		if (crsr.getCount() > 0) {
			img_online.setVisibility(View.VISIBLE);
		}else{
			img_online.setVisibility(View.GONE);
		}

		errorLog = new ErrorLog();
		OfflineDatabase.init(FWMSValidateURL.this);
		if (get_url != null) {
			ed_validate.setText(get_url);
		}
		
		bt_validate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				validate_url = ed_validate.getText().toString();

				if (validate_url.matches("")) {
					Toast.makeText(FWMSValidateURL.this, "Invalid Domain URL",
							Toast.LENGTH_SHORT).show();
				} else {
					AsyncCallWSValidate task = new AsyncCallWSValidate();
					task.execute();
				}

			}
		});

		img_online.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				alertDialog();

			}
		});

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

	private class AsyncCallWSValidate extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(FWMSValidateURL.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(validate_parent, false);
			progressBar = new ProgressBar(FWMSValidateURL.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {

//				if (get_url != null) {
					new SetStockInDetail(validate_url);
//					vl_result = ValidateWebService.validateURLService(get_url,
//							"fncValidateURL");
//					gnrlStngs = ValidateWebService.generalSettingsService(get_url,
//							"fncGetGeneralSettings","1");
//				} else {
					vl_result = ValidateWebService.validateURLService(
							validate_url, "fncValidateURL");

//					gnrlStngs = ValidateWebService.generalSettingsService(validate_url,
//							"fncGetGeneralSettings","1");
//				}
//				if(DEVICE_ID!=null)
//				{
//					validateDevice=ValidateWebService.validateDeviceIDService(DEVICE_ID, "fncValidateDevice",validate_url);
//					Log.d("validateDeviceID", validateDevice);
//					RowItem.setDeviceID(DEVICE_ID);
//				}
//				else
//				{
//				cursor=RandomDeviceIDDataBase.getCursor();
//				if(cursor!=null&&cursor.getCount()!=0){
//				randomdeviceidno=RandomDeviceIDDataBase.getDeviceId();
//				validateDevice=ValidateWebService.validateDeviceIDService(randomdeviceidno, "fncValidateDevice",validate_url);
//				Log.d("dbdatabase",randomdeviceidno);
//				RowItem.setDeviceID(randomdeviceidno);
//				}else
//				{
//				randomText = getRandomText();
//				RandomDeviceIDDataBase.deviceID(randomText);
//				validateDevice=ValidateWebService.validateDeviceIDService(randomText, "fncValidateDevice",validate_url);
//				Log.d("randomdeviceid",randomText);
//						}
//					}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			try{
			if (vl_result != null) {
				if (vl_result.matches("True")) {
					new AsyncCallWSGeneralSettings().execute();
					new AsyncCallDisplaySettings().execute();
				}else if (vl_result.matches("Error")) {
					Toast.makeText(FWMSValidateURL.this, "Invalid Domain URL",
							Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(FWMSValidateURL.this, "Invalid Domain URL",
						Toast.LENGTH_SHORT).show();
			}
			
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
			}
					
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(validate_parent, true);
		}

	}


	private class AsyncCallDisplaySettings extends AsyncTask<Void, Void, Void>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... voids) {
			try {
				dsplyStngs = ValidateWebService.generalService(validate_url,
						"fncGetTranDisplaySettings","1");

			} catch (Exception e) {
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "
						+"Error : " + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			if(dsplyStngs!=null && !dsplyStngs.isEmpty()){
				new AsyncCallWSValidateDevice().execute();
			}else{
				errorLog.write("Generalsetting : " +" , "+"Error : " + "No value return");
				Toast.makeText(FWMSValidateURL.this,"Unable to get fncgeneralsettings value",Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

	private class AsyncCallWSGeneralSettings extends AsyncTask<Void, Void, Void> {

		String dialogStatus;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			try {
				gnrlStngs = ValidateWebService.generalSettingsService(validate_url,
						"fncGetGeneralSettings","1");

			} catch (Exception e) {
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "
						+"Error : " + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if(gnrlStngs!=null && !gnrlStngs.isEmpty()){
				new AsyncCallWSValidateDevice().execute();
			}else{
				errorLog.write("Generalsetting : " +" , "+"Error : " + "No value return");
				Toast.makeText(FWMSValidateURL.this,"Unable to get fncgeneralsettings value",Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

	private class AsyncCallWSValidateDevice extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			try {
				if(DEVICE_ID!=null)
				{
					validateDevice=ValidateWebService.validateDeviceIDService(DEVICE_ID, "fncValidateDevice",validate_url);
					Log.d("validateDeviceID", validateDevice);
					RowItem.setDeviceID(DEVICE_ID);
				}
				else
				{
					cursor=RandomDeviceIDDataBase.getCursor();
					if(cursor!=null&&cursor.getCount()!=0){
						randomdeviceidno=RandomDeviceIDDataBase.getDeviceId();
						validateDevice=ValidateWebService.validateDeviceIDService(randomdeviceidno, "fncValidateDevice",validate_url);
						Log.d("dbdatabase",randomdeviceidno);
						RowItem.setDeviceID(randomdeviceidno);
					}else
					{
						randomText = getRandomText();
						RandomDeviceIDDataBase.deviceID(randomText);
						validateDevice=ValidateWebService.validateDeviceIDService(randomText, "fncValidateDevice",validate_url);
						Log.d("randomdeviceid",randomText);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "
						+"Error : " + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {

				if(validateDevice.matches("True")&&(validateDevice != null))
				{
					if (get_url != null) {
						FWMSSettingsDatabase.updateUrl(validate_url);

						if (gnrlStngs.matches("S")) {

							LogOutSetGet.setApplicationType("S");
							ed_validate.setBackgroundColor(Color.parseColor("#1378C0"));
							ed_validate.setTextColor(Color.parseColor("#FFFFFF"));
							Intent i = new Intent(FWMSValidateURL.this,
									LoginActivity.class);
							startActivity(i);
							Toast.makeText(FWMSValidateURL.this,
									"Validated Successfully", Toast.LENGTH_SHORT)
									.show();
							SalesOrderSetGet.setGeneralsetting("SOT");

						} else if (gnrlStngs.matches("W")) {

							LogOutSetGet.setApplicationType("W");
							ed_validate.setBackgroundColor(Color.parseColor("#1378C0"));
							ed_validate.setTextColor(Color.parseColor("#FFFFFF"));
							Intent i = new Intent(FWMSValidateURL.this,
									LoginActivity.class);
							startActivity(i);
							Toast.makeText(FWMSValidateURL.this,
									"Validated Successfully", Toast.LENGTH_SHORT)
									.show();

							SalesOrderSetGet.setGeneralsetting("WareHouse");
						}else{
							errorLog.write("Generalsetting : " +" , "+"Error : " + "No value return");
							Toast.makeText(FWMSValidateURL.this,"Unable to get fncgeneralsettings value",Toast.LENGTH_LONG).show();
							finish();
						}
					} else {
						FWMSSettingsDatabase.storeUrl(validate_url);
						FWMSSettingsDatabase.storeTempUrl(validate_url);
						if (gnrlStngs.matches("S")) {
							LogOutSetGet.setApplicationType("S");
							ed_validate.setBackgroundColor(Color.parseColor("#1378C0"));
							ed_validate.setTextColor(Color.parseColor("#FFFFFF"));
							Intent i = new Intent(FWMSValidateURL.this,
									LoginActivity.class);
							startActivity(i);
							Toast.makeText(FWMSValidateURL.this,
									"Validated Successfully", Toast.LENGTH_SHORT)
									.show();

							SalesOrderSetGet.setGeneralsetting("SOT");

						} else if (gnrlStngs.matches("W")) {
							LogOutSetGet.setApplicationType("W");
							ed_validate.setBackgroundColor(Color.parseColor("#1378C0"));
							ed_validate.setTextColor(Color.parseColor("#FFFFFF"));
							Intent i = new Intent(FWMSValidateURL.this,
									LoginActivity.class);
							startActivity(i);
							Toast.makeText(FWMSValidateURL.this,
									"Validated Successfully", Toast.LENGTH_SHORT)
									.show();

							SalesOrderSetGet.setGeneralsetting("WareHouse");
						}else{
							errorLog.write("Generalsetting : " +" , "+"Error : " + "No value return");
							Toast.makeText(FWMSValidateURL.this,"Unable to get fncgeneralsettings value",Toast.LENGTH_LONG).show();
							finish();
						}
					}
				}
				else
				{
//					Intent i = new Intent(FWMSValidateURL.this,
//							LoginActivity.class);
//					startActivity(i);
//					SalesOrderSetGet.setGeneralsetting("SOT");
//					FWMSValidateURL.this.finish();
					showDeviceIDAlertDialog();
				}

			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "
						+"Error : " + e.getMessage());
			}
		}
	}

	private void showDeviceIDAlertDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(FWMSValidateURL.this);
		textview = new TextView(FWMSValidateURL.this);
		textview1 = new TextView(FWMSValidateURL.this);
		input = new EditText(FWMSValidateURL.this);
		input1 = new EditText(FWMSValidateURL.this);
		LinearLayout layout = new LinearLayout(FWMSValidateURL.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		dialog.setTitle("Device Activation");
		dialog.setCancelable(true);

		layout.setPadding(15, 15, 15, 15);
		textview1.setPadding(0, 15, 0, 0);

		textview.setText("Enter the device activation code");
		textview1.setText("Enter the company name");

		layout.addView(textview);
		layout.addView(input);
		layout.addView(textview1);
		layout.addView(input1);
		dialog.setView(layout);

		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});

		dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});

		mDialog = dialog.create();
		mDialog.show();

		mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (TextUtils.isEmpty(input.getText())) {
							// showDeviceIDAlertDialog();
							Toast.makeText(FWMSValidateURL.this,
									"Please enter the activation code",
									Toast.LENGTH_LONG).show();
						} else if (TextUtils.isEmpty(input1.getText())) {
							// showDeviceIDAlertDialog();
							Toast.makeText(FWMSValidateURL.this,
									"Please enter the company name",
									Toast.LENGTH_LONG).show();
						} else {
							mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
							AsyncCallWSActivateDevice task = new AsyncCallWSActivateDevice();
							task.execute();
						}
					}
				});

	}
	private class AsyncCallWSActivateDevice extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			try {
				if(DEVICE_ID!=null)
				{
				validateDevice=ValidateWebService.activateDeviceIDService(input.getText().toString(),input1.getText().toString(),DEVICE_ID, "fncDeviceActivation",validate_url);
				Log.d("validateDeviceID", validateDevice);
				RowItem.setDeviceID(DEVICE_ID);
				}
				else
				{
				cursor=RandomDeviceIDDataBase.getCursor();
				if(cursor!=null&&cursor.getCount()!=0){
				randomdeviceidno=RandomDeviceIDDataBase.getDeviceId();
				validateDevice=ValidateWebService.activateDeviceIDService(input.getText().toString(),input1.getText().toString(),randomdeviceidno, "fncDeviceActivation",validate_url);
				Log.d("dbdatabase",randomdeviceidno);
				Log.d("dbvalidateDevice",validateDevice);
				RowItem.setDeviceID(randomdeviceidno);
				}
			}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				if (validateDevice.matches("True")) {
					if (get_url != null) {
						FWMSSettingsDatabase.updateUrl(validate_url);

						if (gnrlStngs.matches("S")) {
							LogOutSetGet.setApplicationType("S");
							bt_validate.setBackgroundColor(Color.parseColor("#1378C0"));
							ed_validate.setTextColor(Color.parseColor("#FFFFFF"));
							Intent i = new Intent(FWMSValidateURL.this,
									LoginActivity.class);
							startActivity(i);
							Toast.makeText(FWMSValidateURL.this,
									"Validated Successfully", Toast.LENGTH_SHORT)
									.show();

							SalesOrderSetGet.setGeneralsetting("SOT");
						} else if (gnrlStngs.matches("W")) {
							LogOutSetGet.setApplicationType("W");
							ed_validate.setBackgroundColor(Color.parseColor("#1378C0"));
							Intent i = new Intent(FWMSValidateURL.this,
									LoginActivity.class);
							startActivity(i);
							Toast.makeText(FWMSValidateURL.this,
									"Validated Successfully", Toast.LENGTH_SHORT)
									.show();

							SalesOrderSetGet.setGeneralsetting("WareHouse");
						}else{
                            errorLog.write("Generalsetting : " +" , "+"Error : " + "No value return");
                            Toast.makeText(FWMSValidateURL.this,"Unable to get fncgeneralsettings value",Toast.LENGTH_LONG).show();
                            finish();
                        }
					} else {
						FWMSSettingsDatabase.storeUrl(validate_url);
						FWMSSettingsDatabase.storeTempUrl(validate_url);
						if (gnrlStngs.matches("S")) {
							LogOutSetGet.setApplicationType("S");
							ed_validate.setBackgroundColor(Color.parseColor("#1378C0"));
							ed_validate.setTextColor(Color.parseColor("#FFFFFF"));
							Intent i = new Intent(FWMSValidateURL.this,
									LoginActivity.class);
							startActivity(i);
							Toast.makeText(FWMSValidateURL.this,
									"Validated Successfully", Toast.LENGTH_SHORT)
									.show();

							SalesOrderSetGet.setGeneralsetting("SOT");

						} else if (gnrlStngs.matches("W")) {
							LogOutSetGet.setApplicationType("W");
							ed_validate.setBackgroundColor(Color.parseColor("#1378C0"));
							ed_validate.setTextColor(Color.parseColor("#FFFFFF"));
							Intent i = new Intent(FWMSValidateURL.this,
									LoginActivity.class);
							startActivity(i);
							Toast.makeText(FWMSValidateURL.this,
									"Validated Successfully", Toast.LENGTH_SHORT)
									.show();

							SalesOrderSetGet.setGeneralsetting("WareHouse");
						}else{
                            errorLog.write("Generalsetting : " +" , "+"Error : " + "No value return");
                            Toast.makeText(FWMSValidateURL.this,"Unable to get fncgeneralsettings value",Toast.LENGTH_LONG).show();
                            finish();
                        }
					}
				}
				if (validateDevice.matches("False")) {
					Toast.makeText(FWMSValidateURL.this, "Invalid code",
							Toast.LENGTH_LONG).show();
					mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
				}
			}catch (Exception e){
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
			}
				}
}
	public String getRandomText() 
	 {
	        SecureRandom random = new SecureRandom();	      
	        BigInteger bigInteger = new BigInteger(MAXIMUM_BIT_LENGTH, random);
	        String randomText = bigInteger.toString(RADIX);
	         
	        return randomText;
	       
	  }

	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Switch to Offline");
		alertDialog.setMessage("Do you want Switch to Offline");
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton(getResources().getString(R.string.yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						OfflineDatabase.updateOnlineMode("False");
						OfflineSettingsManager offlinemanager = new OfflineSettingsManager(FWMSValidateURL.this);
						String comapanyCode = offlinemanager.getCompanyType();
						SupplierSetterGetter.setCompanyCode(comapanyCode);
						Log.d("compcode", "offline dialog" + comapanyCode);
						OfflineDatabase.getGeneralSettings();
						OfflineDatabase.setMobileSettings(comapanyCode);

						Intent i = new Intent(FWMSValidateURL.this,
								LoginActivity.class);
						startActivity(i);
						FWMSValidateURL.this.finish();
					}
				});

		alertDialog.setNegativeButton(getResources().getString(R.string.no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});


		alertDialog.show();
	}
}