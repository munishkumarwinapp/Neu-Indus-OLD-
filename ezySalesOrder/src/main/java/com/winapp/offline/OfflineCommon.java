package com.winapp.offline;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.offline.OfflineDataDownloader.OnDownloadCompletionListener;
import com.winapp.offline.OfflineDataUploader.OnUploadCompletionListener;

public class OfflineCommon {
	
	boolean dialogResult;
	Handler mHandler;
	Activity mactivity;
	boolean status = false;

	private OfflineDatabase offlinedb;
	private OfflineDataDownloader dataDownloader;
	OfflineDataUploader offlineUploader;
	private static String URL;
	private OfflineDataSynch offlineDataSynch;
	
	public OfflineCommon(Activity context, String url) {
		mactivity = context;
		URL = url;
		//this.offlinedb = new OfflineDatabase(context);
	}

	
	public OfflineCommon(Activity context) {

		FWMSSettingsDatabase.init(context);
		String url = FWMSSettingsDatabase.getUrl();
		URL = url;
		mactivity = context;

	}

	public boolean getDialogResult() {
		return dialogResult;
	}

	public boolean setDialogResult(boolean dialogResult) {
		return this.dialogResult = dialogResult;
	}

	/** Called when the activity is first created. */


	public void OfflineAlertDialog(){

		Builder dialog = new Builder(mactivity);
		dialog.setTitle("Offline")
				.setMessage(R.string.offline_alert)
				.setCancelable(false)
				.setPositiveButton("Agree",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								status = false;
								endDialog(false);
								dialog.dismiss();


							}
						})

				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								status = false;
								endDialog(false);
								dialog.dismiss();
							}
						});
		dialog.show();

	}

	//Temporary online to offline
	public void OfflineAlertDialogs() {

		Builder dialog = new Builder(mactivity);
		dialog.setTitle(R.string.switch_to_offine)
				.setMessage(R.string.offline_status)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								OfflineDatabase.updateInternetMode("OnlineDialog","false");
								 OfflineSettingsManager offlinemanager = new OfflineSettingsManager(mactivity);
								 String comapanyCode = offlinemanager.getCompanyType();
								 SupplierSetterGetter.setCompanyCode(comapanyCode);

								 Log.d("compcode", "offline dialog" + comapanyCode);
								 OfflineDatabase.getGeneralSettings();
								 OfflineDatabase.setMobileSettings(comapanyCode);

								 OfflineReload off_reload = new OfflineReload();
								 off_reload.OfflineloadingMenus();

								status = true;
								endDialog(true);
							}
						})

				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								status = false;
								endDialog(false);
								dialog.dismiss();
							}
						});
		dialog.show();

	}


	
	//Temporary offline to online
	public void onlineAlertDialog() {

		Builder dialog = new Builder(mactivity);
		dialog.setTitle(R.string.switch_to_online)
				.setMessage(R.string.online_status)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								
								dialog.dismiss();
								offlineUploader = new OfflineDataUploader(URL,mactivity);
								OfflineSettingsManager offlinemanager = new OfflineSettingsManager(mactivity);
								String comapanyCode = offlinemanager.getCompanyType();
								SupplierSetterGetter.setCompanyCode(comapanyCode);
								offlineUploader.startUpload("switch_to_online","fncUploadCustomer",mactivity);
								
								offlineUploader.setOnUploadCompletionListener(new OnUploadCompletionListener() {
									@Override
									public void onCompleted() {
										 Log.d("Common ", "switchto_OnlineAlertDialog ");
										 OfflineDatabase.updateInternetMode("OfflineDialog","false");
										 OfflineSettingsManager offlinemanager = new OfflineSettingsManager(mactivity);
											String comapanyCode = offlinemanager.getCompanyType();												
											SupplierSetterGetter.setCompanyCode(comapanyCode);
											status = true;
											endDialog(true);
										
									}
								});
							}
						})

				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// status="False";
								OfflineDatabase.updateOnlineMode("False");
								endDialog(false);
								dialog.dismiss();
							}
						});
		dialog.show();

	}
	
	//Switch to offline button press
	public void switchto_OfflineAlertDialog() {
		final boolean[] downloadImage = {false};
		Builder dialog = new Builder(mactivity);
		dialog.setTitle(R.string.switch_to_offine)
				.setMessage(R.string.switch_to_offline_status)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
													
//								OfflineDatabase.updateOnlineMode("False");
								dialog.dismiss();
								FWMSSettingsDatabase.init(mactivity);
						        String valid_url = FWMSSettingsDatabase.getUrl();
								dataDownloader = new OfflineDataDownloader(mactivity,valid_url);
								dataDownloader.downloadImage(downloadImage[0]);
						        dataDownloader.startDownload(true, "Synchronizing data");
						        
						        dataDownloader.setOnDownloadCompletionListener(new OnDownloadCompletionListener() {     
								     @Override
								     public void onCompleted() {
								    	 Log.d("Common ", "switchto_OfflineAlertDialog ");
								        	OfflineDatabase.updateOnlineMode("False");
								        	 OfflineSettingsManager offlinemanager = new OfflineSettingsManager(mactivity);
											String comapanyCode = offlinemanager.getCompanyType();												
											SupplierSetterGetter.setCompanyCode(comapanyCode);
											Log.d("compcode", "offline dialog" + comapanyCode);
											OfflineDatabase.getGeneralSettings();
											OfflineDatabase.setMobileSettings(comapanyCode);	
											status = true;
											endDialog(true);
								     }
								    });
							}
						})

				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								status = false;
								endDialog(false);
								dialog.dismiss();
							}
						});

		if(mactivity instanceof LandingActivity){
			LayoutInflater adbInflater = LayoutInflater.from(mactivity);
			final View eulaLayout = adbInflater.inflate(R.layout.print_checkbox,null);
			CheckBox enableDownload = (CheckBox) eulaLayout.findViewById(R.id.checkbox);
			enableDownload.setText("Download Image For Catalog");
			enableDownload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					downloadImage[0] = true;
				}
			});

			dialog.setView(eulaLayout);
		}
		dialog.show();

	}
	
	//Switch to online button press
	public void switchto_OnlineAlertDialog() {

		Builder dialog = new Builder(mactivity);
		dialog.setTitle(R.string.switch_to_online)
				.setMessage(R.string.switch_to_online_status)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//OfflineDatabase.updateOnlineMode("True");
								dialog.dismiss();
								offlineUploader = new OfflineDataUploader(URL,mactivity);
								OfflineSettingsManager offlinemanager = new OfflineSettingsManager(mactivity);
								String comapanyCode = offlinemanager.getCompanyType();
								SupplierSetterGetter.setCompanyCode(comapanyCode);
								offlineUploader.startUpload("switch_to_online","fncUploadCustomer",mactivity);
								offlineUploader.setOnUploadCompletionListener(new OnUploadCompletionListener() {
									@Override
									public void onCompleted() {
										 Log.d("Common ", "switchto_OnlineAlertDialog ");
										 OfflineSettingsManager offlinemanager = new OfflineSettingsManager(mactivity);
											String comapanyCode = offlinemanager.getCompanyType();	
											SupplierSetterGetter.setCompanyCode(comapanyCode);
								    	 OfflineDatabase.updateOnlineMode("True");
											status = true;
											endDialog(true);
										
									}
								});
							}
						})

				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								status = false;
								endDialog(false);
								dialog.dismiss();
							}
						});
		dialog.show();

	}
	
	
	public void uploadSyncAlertDialog() {

		Builder dialog = new Builder(mactivity);
		dialog.setTitle(R.string.upload)
				.setMessage(R.string.upload_status)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								 OfflineSettingsManager offlinemanager = new OfflineSettingsManager(mactivity);
									String comapanyCode = offlinemanager.getCompanyType();												
									SupplierSetterGetter.setCompanyCode(comapanyCode);
								offlineUploader = new OfflineDataUploader(URL,mactivity);
								offlineUploader.startUpload("upload","fncUploadCustomer",mactivity);
								status = true;
								endDialog(true);
							}
						})

				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								status = false;
								endDialog(false);
								dialog.dismiss();
							}
						});
		dialog.show();

	}


	public void endDialog(boolean result) {

		setDialogResult(result);
		Message m = mHandler.obtainMessage();
		mHandler.sendMessage(m);
	}

	public boolean showDialog() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message mesg) {
				// process incoming messages here
				// super.handleMessage(msg);
				throw new RuntimeException();
			}
		};
		// super.show();
		try {
			Looper.getMainLooper();
			Looper.loop();
		} catch (RuntimeException e2) {
		}
		return dialogResult;
	}
	
	public static boolean isConnected(Context context){
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

	    if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
	    	
//	    	WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//	    	WifiInfo wifi = wifiManager.getConnectionInfo();
//	    	if (wifiInfo != null) {
//	    	    Integer linkSpeed = wifi.getLinkSpeed(); //measured using WifiInfo.LINK_SPEED_UNITS
//	    	    
//	    	    Log.d("linkSpeed", ""+linkSpeed);
//	    	}
	    	
	            return false;
	    }else{
	            return true;
	    }
	} 
	
	
}