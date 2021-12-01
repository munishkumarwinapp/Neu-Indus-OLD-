package com.winapp.fwms;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.sot.RowItem;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.WebServiceClass;
import com.winapp.util.ErrorLog;

public class SplashScreen extends Activity {

	ProgressBar spl_progressBar;
	String url;
	String vl_result;
	String gnrlStngs = "",dsplyStngs;
	private String DEVICE_ID;
	String validateDevice;
	EditText input, input1;
	TextView textview, textview1;
	String randomText;
	private static final int MAXIMUM_BIT_LENGTH = 75;
	private static final int RADIX = 32;
	Cursor cursor;
	String randomdeviceidno;
	ArrayList<HashMap<String, String>> cmpySpnArr;
	boolean checkOffline;
	String onlineMode = "";
	private OfflineCommon offlineCommon;
	private OfflineSettingsManager offlinemanager;
	AlertDialog mDialog;
	private ErrorLog errorLog;
    private boolean tripletap;
	private int numberOfTaps = 0;
	private ImageView splash_img;
	@SuppressLint("HardwareIds")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);

		spl_progressBar = (ProgressBar) findViewById(R.id.spl_progressBar);
		splash_img= (ImageView) findViewById(R.id.imageView1);

		FWMSSettingsDatabase.init(SplashScreen.this);
		url = FWMSSettingsDatabase.getUrl();
		cmpySpnArr = new ArrayList<HashMap<String, String>>();
		DEVICE_ID = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		RandomDeviceIDDataBase.init(SplashScreen.this);
		Log.d("DEVICE_ID", DEVICE_ID);
//		DEVICE_ID ="882e84da2e5be922";

		String model = Build.MODEL;
		String manufacturer = Build.MANUFACTURER;

		String device_name = manufacturer+" "+model;
		RowItem.setDeviceName(device_name);
		Log.d("DEVICE NAME",device_name);

		offlineCommon = new OfflineCommon(this, url);
		checkOffline = OfflineCommon.isConnected(this);
		OfflineDatabase.init(SplashScreen.this);
		offlinemanager = new OfflineSettingsManager(SplashScreen.this);

		errorLog = new ErrorLog();
		new WebServiceClass(url);

		Cursor cursor = OfflineDatabase.getOnlineModeCount();
		if (cursor != null && cursor.getCount() > 0) {
			Log.d("cursor.getCount", "" + cursor.getCount());
		} else {
			OfflineDatabase.store_onlinemode("True");
		}

		String deviceCursor = OfflineDatabase.getDeviceId();
		if (deviceCursor != null && deviceCursor.length() > 0) {
			Log.d("cursor.getCount", "" + deviceCursor.length());
		} else {
			OfflineDatabase.store_deviceid(DEVICE_ID);
		}

		Cursor catalogtypeCursor = FWMSSettingsDatabase.getCatalogType();
		if (catalogtypeCursor != null && catalogtypeCursor.getCount() > 0) {
			Log.d("catalogtypeCursor", "" + catalogtypeCursor.getCount());
		} else {
			FWMSSettingsDatabase.storeCatalogType("Invoice");
		}

		Cursor stockCursor = FWMSSettingsDatabase.getStockType();
		if (stockCursor != null && stockCursor.getCount() > 0) {
			Log.d("stockCursor", "" + stockCursor.getCount());
		} else {
			FWMSSettingsDatabase.setStockMode(1);
		}

		Cursor invoiceCursor = FWMSSettingsDatabase.getInvoiceuserType();
		if (invoiceCursor != null && invoiceCursor.getCount() > 0) {
			Log.d("invoiceCursor", "" + invoiceCursor.getCount());
		} else {
			FWMSSettingsDatabase.setInvoiceuserMode(0);
		}

		Cursor printertypecursor = FWMSSettingsDatabase.getPrinterType();
		if (printertypecursor != null && printertypecursor.getCount() > 0) {
			Log.d("catalogtypeCursor", "" + printertypecursor.getCount());
		} else {
			FWMSSettingsDatabase.storePrinterType("Zebra iMZ320");
		}

		Cursor internet_cursor = OfflineDatabase.getInternetModeCount();
		if (internet_cursor != null && internet_cursor.getCount() > 0) {
			Log.d("internet_cursor", "" + internet_cursor.getCount());
		} else {
			OfflineDatabase.store_internetmode();
		}

		onlineMode = OfflineDatabase.getOnlineMode();
		Log.d("SplashOnlineMode", onlineMode);

		int secondsDelayed = 1;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				boolean interntConnection = isNetworkConnected();

				if (interntConnection == true) {
					if (url != null) {
						AsyncCallWSValidate task = new AsyncCallWSValidate();
						task.execute();
					} else {
						Intent i = new Intent(SplashScreen.this,
								FWMSValidateURL.class);
						startActivity(i);
						SplashScreen.this.finish();
					}
				} else {

					if (url != null) {
						AsyncCallWSValidate task = new AsyncCallWSValidate();
						task.execute();
					} else {
						showAlertDialog(SplashScreen.this,
								"No Internet Connection",
								"You don't have internet connection.", false);
					}
				}

			}
		}, secondsDelayed * 2000);

		splash_img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchtooffline();
			}
		});

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

	@SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setCancelable(false); // not close dialog
		alertDialog.setIcon((status) ? R.mipmap.success : R.mipmap.fail);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SplashScreen.this.finish();
			}
		});
		alertDialog.show();
	}

	private class AsyncCallWSValidate extends AsyncTask<Void, Void, Void> {

		String dialogStatus;

		@Override
		protected void onPreExecute() {
			dialogStatus = checkInternetStatus();
			Log.d("dialogcheck",dialogStatus);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			try {

				String compycode = offlinemanager.getCompanyType();

				if (compycode != null && !compycode.isEmpty()) {
					if (compycode.matches("0")) {
						compycode = "1";
					}
				} else {
					compycode = "1";
				}

				// Offline
				onlineMode = OfflineDatabase.getOnlineMode();
				Log.d("SplashOnlineMode", onlineMode);
				if (onlineMode.matches("True")) {
					checkOffline = OfflineCommon.isConnected(SplashScreen.this);
					Log.d("chckofflinechck",""+checkOffline);
					if (checkOffline == true) {
						Log.d("DialogStatus", "" + dialogStatus);

						if (dialogStatus.matches("true")) {
							Log.d("CheckOffline Alert -->", "True");
							RowItem.setDeviceID(DEVICE_ID);
							vl_result = "True";
//							gnrlStngs = OfflineDatabase.getGeneralSettings();
//							validateDevice = "True";
						} else {
							Log.d("CheckOffline Alert -->", "False");
							vl_result = "false";
							// SplashScreen.this.finish();

						}

					} else {
						Log.d("checkOffline Status -->", "False");

//						HashMap<String,String> hm = new HashMap<String,String>();
//						hm.put("sData","1");
//						WebServiceClass.parameterWebservice(hm,"testLog");

						vl_result = ValidateWebService.validateURLService(url,
								"fncValidateURL");

//						HashMap<String,String> hm1 = new HashMap<String,String>();
//						hm1.put("sData","2"+vl_result);
//						WebServiceClass.parameterWebservice(hm1,"testLog");

//						gnrlStngs = ValidateWebService.generalSettingsService(
//								url, "fncGetGeneralSettings", compycode);

//						HashMap<String,String> hm3 = new HashMap<String,String>();
//						hm3.put("sData","3"+gnrlStngs);
//						WebServiceClass.parameterWebservice(hm3,"testLog");

						/*if (DEVICE_ID != null) {
							validateDevice = ValidateWebService
									.validateDeviceIDService(DEVICE_ID,
											"fncValidateDevice", url);
							Log.d("validateDeviceID", validateDevice);
							RowItem.setDeviceID(DEVICE_ID);

//							HashMap<String,String> hm4 = new HashMap<String,String>();
//							hm4.put("sData","4"+validateDevice);
//							WebServiceClass.parameterWebservice(hm4,"testLog");
						} else {
							cursor = RandomDeviceIDDataBase.getCursor();
							if (cursor != null && cursor.getCount() != 0) {
								randomdeviceidno = RandomDeviceIDDataBase
										.getDeviceId();
								validateDevice = ValidateWebService
										.validateDeviceIDService(
												randomdeviceidno,
												"fncValidateDevice", url);
								Log.d("dbdatabase", randomdeviceidno);
								RowItem.setDeviceID(randomdeviceidno);

							} else {
								randomText = getRandomText();
								RandomDeviceIDDataBase.deviceID(randomText);
								validateDevice = ValidateWebService
										.validateDeviceIDService(randomText,
												"fncValidateDevice", url);
								Log.d("randomdeviceid", randomText);
							}
						}*/

					}

				} else if (onlineMode.matches("False")) {
					RowItem.setDeviceID(DEVICE_ID);
					vl_result = "True";
//					gnrlStngs = OfflineDatabase.getGeneralSettings();
//					validateDevice = "True";
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
				if (vl_result != null) {

					if (vl_result.matches("True")) {

                            new AsyncCallWSGeneralSettings().execute();

					} else if (vl_result.matches("Error")) {

						Toast.makeText(SplashScreen.this, "Server Problem",
								Toast.LENGTH_LONG).show();
//						alertDialog();
					} else if (vl_result.matches("false")) {
						Toast.makeText(SplashScreen.this, "No Internet",
								Toast.LENGTH_LONG).show();
//						finish();
						alertDialog();
					}

				} else {
					Toast.makeText(SplashScreen.this, "Invalid Domain URL", Toast.LENGTH_LONG).show();
					Intent i = new Intent(SplashScreen.this,
							FWMSValidateURL.class);
					startActivity(i);
					SplashScreen.this.finish();
				}
			}

			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "
						+"Error : " + e.getMessage());
			}
		}
	}

    private class AsyncCallWSGeneralSettings extends AsyncTask<Void, Void, Void> {

        String dialogStatus;

        @Override
        protected void onPreExecute() {
            dialogStatus = checkInternetStatus();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub

            try {
                String compycode = offlinemanager.getCompanyType();

                if (compycode != null && !compycode.isEmpty()) {
                    if (compycode.matches("0")) {
                        compycode = "1";
                    }
                } else {
                    compycode = "1";
                }

                // Offline
                if (onlineMode.matches("True")) {

                    if (checkOffline == true) {
                        Log.d("DialogStatus", "" + dialogStatus);

                        if (dialogStatus.matches("true")) {
                            Log.d("CheckOffline Alert -->", "True");
                            RowItem.setDeviceID(DEVICE_ID);
//                            vl_result = "True";
                            gnrlStngs = OfflineDatabase.getGeneralSettings();
//                            validateDevice = "True";
                        } else {
                            Log.d("CheckOffline Alert -->", "False");
                            vl_result = "false";
                            // SplashScreen.this.finish();

                        }

                    } else {
                        Log.d("checkOffline Status -->", "False");

                        gnrlStngs = ValidateWebService.generalSettingsService(
                                url, "fncGetGeneralSettings", compycode);
						dsplyStngs = ValidateWebService.generalService(url, "fncGetTranDisplaySettings", compycode);


                        /*if (DEVICE_ID != null) {
                            validateDevice = ValidateWebService
                                    .validateDeviceIDService(DEVICE_ID,
                                            "fncValidateDevice", url);
                            Log.d("validateDeviceID", validateDevice);
                            RowItem.setDeviceID(DEVICE_ID);

                        } else {
                            cursor = RandomDeviceIDDataBase.getCursor();
                            if (cursor != null && cursor.getCount() != 0) {
                                randomdeviceidno = RandomDeviceIDDataBase
                                        .getDeviceId();
                                validateDevice = ValidateWebService
                                        .validateDeviceIDService(
                                                randomdeviceidno,
                                                "fncValidateDevice", url);
                                Log.d("dbdatabase", randomdeviceidno);
                                RowItem.setDeviceID(randomdeviceidno);

                            } else {
                                randomText = getRandomText();
                                RandomDeviceIDDataBase.deviceID(randomText);
                                validateDevice = ValidateWebService
                                        .validateDeviceIDService(randomText,
                                                "fncValidateDevice", url);
                                Log.d("randomdeviceid", randomText);
                            }
                        }*/

                    }

                } else if (onlineMode.matches("False")) {
                    RowItem.setDeviceID(DEVICE_ID);
//                    vl_result = "True";
                    gnrlStngs = OfflineDatabase.getGeneralSettings();
//                    validateDevice = "True";
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

            if(gnrlStngs!=null && !gnrlStngs.isEmpty()){
                new AsyncCallWSValidateDevice().execute();
            }else if(dsplyStngs!=null && !dsplyStngs.isEmpty()){
				new AsyncCallWSValidateDevice().execute();
			}else{
                errorLog.write("Generalsettings : " +" , "+"Error : " + "No value return");
                Toast.makeText(SplashScreen.this,"Unable to get fncgeneralsettings value",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private class AsyncCallWSValidateDevice extends AsyncTask<Void, Void, Void> {

        String dialogStatus;

        @Override
        protected void onPreExecute() {
            dialogStatus = checkInternetStatus();
			Log.d("dialogStatus",""+dialogStatus);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub

            try {

                String compycode = offlinemanager.getCompanyType();

                if (compycode != null && !compycode.isEmpty()) {
                    if (compycode.matches("0")) {
                        compycode = "1";
                    }
                } else {
                    compycode = "1";
                }

                // Offline
                if (onlineMode.matches("True")) {
					checkOffline = OfflineCommon.isConnected(SplashScreen.this);
                    if (checkOffline == true) {
                        Log.d("DialogStatus", "" + dialogStatus);

                        if (dialogStatus.matches("true")) {
                            Log.d("CheckOffline Alert -->", "True");
                            RowItem.setDeviceID(DEVICE_ID);
//                            vl_result = "True";
//                            gnrlStngs = OfflineDatabase.getGeneralSettings();
                            validateDevice = "True";
                        } else {
                            Log.d("CheckOffline Alert -->", "False");
                            vl_result = "false";
                            // SplashScreen.this.finish();

                        }

                    } else {
                        Log.d("checkOffline Status -->", "False");

                        if (DEVICE_ID != null) {
                            validateDevice = ValidateWebService
                                    .validateDeviceIDService(DEVICE_ID,
                                            "fncValidateDevice", url);
                            Log.d("validateDeviceID", validateDevice);
                            RowItem.setDeviceID(DEVICE_ID);

//							HashMap<String,String> hm4 = new HashMap<String,String>();
//							hm4.put("sData","4"+validateDevice);
//							WebServiceClass.parameterWebservice(hm4,"testLog");
                        } else {
                            cursor = RandomDeviceIDDataBase.getCursor();
                            if (cursor != null && cursor.getCount() != 0) {
                                randomdeviceidno = RandomDeviceIDDataBase
                                        .getDeviceId();
                                validateDevice = ValidateWebService
                                        .validateDeviceIDService(
                                                randomdeviceidno,
                                                "fncValidateDevice", url);
                                Log.d("dbdatabase", randomdeviceidno);
                                RowItem.setDeviceID(randomdeviceidno);

                            } else {
                                randomText = getRandomText();
                                RandomDeviceIDDataBase.deviceID(randomText);
                                validateDevice = ValidateWebService
                                        .validateDeviceIDService(randomText,
                                                "fncValidateDevice", url);
                                Log.d("randomdeviceid", randomText);
                            }
                        }

                    }

                } else if (onlineMode.matches("False")) {
                    RowItem.setDeviceID(DEVICE_ID);
//                    vl_result = "True";
//                    gnrlStngs = OfflineDatabase.getGeneralSettings();
                    validateDevice = "True";
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
                if (validateDevice.matches("True")) {
                    if (gnrlStngs.matches("S")) {
                        LogOutSetGet.setApplicationType("S");
                        Intent i = new Intent(SplashScreen.this,
								LoginActivity.class);
                        i.putExtra("GeneralSetting", "SOT");
                        startActivity(i);
                        SalesOrderSetGet.setGeneralsetting("SOT");
                        SplashScreen.this.finish();
                    } else if (gnrlStngs.matches("W")) {
                        LogOutSetGet.setApplicationType("W");
                        Intent i = new Intent(SplashScreen.this,
                                LoginActivity.class);
                        i.putExtra("GeneralSetting", "WareHouse");
                        startActivity(i);
                        SalesOrderSetGet.setGeneralsetting("WareHouse");
                    }else{
                        errorLog.write("Generalsetting : " +" , "+"Error : " + "No value return");
                        Toast.makeText(SplashScreen.this,"Unable to get fncgeneralsettings value",Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    showDeviceIDAlertDialog();
//					Intent i = new Intent(SplashScreen.this,
//							LoginActivity.class);
//					startActivity(i);
//					SalesOrderSetGet.setGeneralsetting("SOT");
//					SplashScreen.this.finish();
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
		AlertDialog.Builder dialog = new AlertDialog.Builder(SplashScreen.this);
		textview = new TextView(SplashScreen.this);
		textview1 = new TextView(SplashScreen.this);
		input = new EditText(SplashScreen.this);
		input1 = new EditText(SplashScreen.this);
		LinearLayout layout = new LinearLayout(SplashScreen.this);
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
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (TextUtils.isEmpty(input.getText())) {
							// showDeviceIDAlertDialog();
							Toast.makeText(SplashScreen.this,
									"Please enter the activation code",
									Toast.LENGTH_LONG).show();
						} else if (TextUtils.isEmpty(input1.getText())) {
							// showDeviceIDAlertDialog();
							Toast.makeText(SplashScreen.this,
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

				if (DEVICE_ID != null) {
					validateDevice = ValidateWebService
							.activateDeviceIDService(
									input.getText().toString(), input1
											.getText().toString(), DEVICE_ID,
									"fncDeviceActivation", url);
					Log.d("validateDeviceID", validateDevice);
					RowItem.setDeviceID(DEVICE_ID);
				} else {
					cursor = RandomDeviceIDDataBase.getCursor();
					if (cursor != null && cursor.getCount() != 0) {
						randomdeviceidno = RandomDeviceIDDataBase.getDeviceId();
						validateDevice = ValidateWebService
								.activateDeviceIDService(input.getText()
										.toString(), input1.getText()
										.toString(), randomdeviceidno,
										"fncDeviceActivation", url);
						Log.d("dbdatabase", randomdeviceidno);
						RowItem.setDeviceID(randomdeviceidno);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "
						+"Error : " + e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "
						+"Error : " + e.getMessage());
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "
						+"Error : " + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			try {
				if (validateDevice.matches("True")) {
					mDialog.dismiss();
					if (gnrlStngs.matches("S")) {
						LogOutSetGet.setApplicationType("S");
						Intent i = new Intent(SplashScreen.this,
								LoginActivity.class);
						startActivity(i);
						SalesOrderSetGet.setGeneralsetting("SOT");
						SplashScreen.this.finish();
					} else if (gnrlStngs.matches("W")) {
						LogOutSetGet.setApplicationType("W");
						Intent i = new Intent(SplashScreen.this,
								LoginActivity.class);
						startActivity(i);
						SalesOrderSetGet.setGeneralsetting("WareHouse");
					}else{
						errorLog.write("Generalsetting : " +" , "+"Error : " + "No value return");
						Toast.makeText(SplashScreen.this,"Unable to get fncgeneralsettings value",Toast.LENGTH_LONG).show();
						finish();
					}
				} else if (validateDevice.matches("False")) {
					Toast.makeText(SplashScreen.this, "Invalid code",
							Toast.LENGTH_LONG).show();
					mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
					//	showDeviceIDAlertDialog();
				}
			}catch (Exception e){
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "
						+"Error : " + e.getMessage());
			}
		}

	}

	@Override
	public void onBackPressed() {
		SplashScreen.this.finish();
	}

	public String getRandomText() {
		SecureRandom random = new SecureRandom();
		BigInteger bigInteger = new BigInteger(MAXIMUM_BIT_LENGTH, random);
		String randomText = bigInteger.toString(RADIX);

		return randomText;
	}

	public String checkInternetStatus() {
		String internetStatus = "";
		String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			if(mobileHaveOfflineMode.matches("1")){
		checkOffline = OfflineCommon.isConnected(SplashScreen.this);
//		String internetStatus = "";
				onlineMode = OfflineDatabase.getOnlineMode();
				Log.d("SplashOnlineMode", onlineMode);
		if (onlineMode.matches("True")) {
			if (checkOffline == true) {
				String Off_dialog = OfflineDatabase
						.getInternetMode("OfflineDialog");
				if (Off_dialog.matches("true")) {
					internetStatus = "true";
				} else {
					offlineCommon.OfflineAlertDialog();
					Boolean dialogStatus = offlineCommon.showDialog();
					OfflineDatabase.updateInternetMode("OfflineDialog",
							dialogStatus + "");
					Log.d("Offline DialogStatus", "" + dialogStatus);
					internetStatus = "" + dialogStatus;
				}
			} else if (checkOffline == false) {
				String on_dialog = OfflineDatabase
						.getInternetMode("OnlineDialog");
				if (on_dialog.matches("true")) {
					internetStatus = "true";
				} else {
					offlineCommon.onlineAlertDialog();
					boolean dialogStatus = offlineCommon.showDialog();
					OfflineDatabase.updateInternetMode("OnlineDialog",
							dialogStatus + "");
					Log.d("Online DialogStatus", "" + dialogStatus);
					internetStatus = "" + dialogStatus;
				}
			}
		}
			}else{
				internetStatus = "false";
			}
		}else{
			internetStatus = "false";
		}
		return internetStatus;
	}

	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Settings");
		alertDialog.setMessage("No Internet connection..Try to connect internet?");
//				.setMessage("Cannot able to connect server.Do you want to change settings?");
		alertDialog.setIcon(R.mipmap.ic_save);

		alertDialog.setPositiveButton("Retry",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						Intent i = new Intent(SplashScreen.this,
//								FWMSValidateURL.class);
//						startActivity(i);
//						SplashScreen.this.finish();
						boolean interntConnection= isNetworkConnected();

						Log.d("interconnectionCheck",""+interntConnection);

						if (interntConnection == true) {
							if (url != null) {
								Log.d("urlCheck",url);
								AsyncCallWSValidate task = new AsyncCallWSValidate();
								task.execute();
							} else {
								SplashScreen.this.finish();
							}
						} else {
							showAlertDialog(SplashScreen.this,
										"No Internet Connection",
										"You don't have internet connection.", false);
						}
					}
				});

		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SplashScreen.this.finish();
					}
				});
		alertDialog.show();
	}

	/*public void showAlertDialogForTesting(Context context, String title, String message,
								Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setCancelable(false); // not close dialog
		alertDialog.setIcon((status) ? R.mipmap.success : R.mipmap.fail);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		alertDialog.show();
	}*/

	public void switchtooffline(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
				numberOfTaps += 1;
				if(numberOfTaps==3) {
					alertDialogOffline();
				}

				Log.d("numberOfTaps",""+numberOfTaps);
            }
        }, 3000);
    }

	public void alertDialogOffline() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Switch to Offline");
		alertDialog.setMessage("Do you want Switch to Offline");
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton(getResources().getString(R.string.yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						OfflineDatabase.updateOnlineMode("False");
						OfflineSettingsManager offlinemanager = new OfflineSettingsManager(SplashScreen.this);
						String comapanyCode = offlinemanager.getCompanyType();
						SupplierSetterGetter.setCompanyCode(comapanyCode);
						Log.d("compcode", "offline dialog" + comapanyCode);
						OfflineDatabase.getGeneralSettings();
						OfflineDatabase.setMobileSettings(comapanyCode);

						Intent i = new Intent(SplashScreen.this,
								LoginActivity.class);
						startActivity(i);
						SplashScreen.this.finish();
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
