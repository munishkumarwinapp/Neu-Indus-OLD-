package com.winapp.sot;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.GetUserPermission;
import com.winapp.fwms.LoginWebService;
import com.winapp.fwms.SupplierSetterGetter;

public class SOTAdmin {
	AlertDialog.Builder builder;
	Activity activity;
	EditText username_edt, password_edt, priceTag_edt,cpriceTag_edt;
	String username, password, loginResult, userGroup, isActive, formName,
			valid_url, groupUser;
	ArrayList<String> loginArr = new ArrayList<String>();
	ArrayList<String> userGroupCodeArr = new ArrayList<String>();
	LinearLayout spinnerLayout;
	ProgressBar progressBar;
	LinearLayout admin_parent;

	public SOTAdmin(Activity activity, EditText priceTag,
			LinearLayout product_layout) {
		this.activity = activity;
		this.priceTag_edt = priceTag;
		this.admin_parent = product_layout;
		FWMSSettingsDatabase.init(activity);
		valid_url = FWMSSettingsDatabase.getUrl();
		new LoginWebService(valid_url);
		new GetUserPermission(valid_url);
		adminDialog();
	}
	
	public SOTAdmin(Activity activity, EditText priceTag,EditText cpriceTag,
			LinearLayout product_layout) {
		this.activity = activity;
		this.priceTag_edt = priceTag;
		this.cpriceTag_edt = cpriceTag;
		this.admin_parent = product_layout;
		FWMSSettingsDatabase.init(activity);
		valid_url = FWMSSettingsDatabase.getUrl();
		new LoginWebService(valid_url);
		new GetUserPermission(valid_url);
		adminDialog();
	}
	

	private void adminDialog() {
		builder = new AlertDialog.Builder(activity);
		builder.setTitle("Admin");
		LayoutInflater adbInflater = LayoutInflater.from(activity);
		View adminview = adbInflater.inflate(R.layout.sotadmin_dialog, null);

		username_edt = (EditText) adminview.findViewById(R.id.sl_username);
		password_edt = (EditText) adminview.findViewById(R.id.sl_password);

		builder.setView(adminview);

		builder.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.setPositiveButton("Login",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						username = username_edt.getText().toString();
						password = password_edt.getText().toString();
						if (username.length() != 0 && username != "") {
							if (password.length() != 0 && password != "") {
								new AsyncCallWSAdmin().execute(username,
										password);

							} else {
								Toast.makeText(activity,
										"Username and password mismatched",
										Toast.LENGTH_SHORT).show();
							}
						}

						else {
							Toast.makeText(activity,
									"Username and password mismatched",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}

	private class AsyncCallWSAdmin extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			try{
			String userid = params[0];
			String passwrd = params[1];
			Log.d("userid", userid);
			Log.d("passwrd", passwrd);
			loginArr = LoginWebService.loginWS(userid, passwrd,
					"fncCheckUserNameAndPassword");

			loginResult = loginArr.get(0);
			userGroup = loginArr.get(1);
			isActive = loginArr.get(2);
			Log.d("userGroup", userGroup);
				String companyCode = SupplierSetterGetter.getCompanyCode();
				userGroupCodeArr = GetUserPermission.userGroupCode(userGroup,companyCode,
						"fncGetUserPermission");
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			try{
			printResult(loginResult, isActive);
			}catch(Exception e){
				e.printStackTrace();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(admin_parent, true);
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(activity);
			spinnerLayout.setGravity(Gravity.CENTER);
			activity.addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(admin_parent, false);
			progressBar = new ProgressBar(activity);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(activity.getResources()
					.getDrawable(drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}
	}

	private void printResult(String loginResult, String isActive) {

		groupUser = "False";

		for (int i = 0; i < userGroupCodeArr.size(); i++) {
			formName = userGroupCodeArr.get(i);
			if (formName.matches("Edit Price")) {
				groupUser = "True";
			}
		}
		if (loginResult.matches("True") && isActive.matches("True")
				&& groupUser.matches("True")) {
			priceTag_edt.setEnabled(true);
			priceTag_edt.setFocusable(true);
			priceTag_edt.setFocusableInTouchMode(true);
			priceTag_edt.setBackgroundResource(drawable.edittext_bg);
			
			String priceflag = SalesOrderSetGet.getCartonpriceflag();
			
			if(priceflag.matches("1")){
				cpriceTag_edt.setEnabled(true);
				cpriceTag_edt.setFocusable(true);
				cpriceTag_edt.setFocusableInTouchMode(true);
				cpriceTag_edt.setBackgroundResource(drawable.edittext_bg);
			}else{
				
			}
						
		} else if (loginResult.matches("True") && isActive.matches("True")
				&& groupUser.matches("False")) {
			Toast.makeText(activity, "Invalid Username and Password",
					Toast.LENGTH_SHORT).show();
			adminDialog();
		} else if (loginResult.matches("False") && isActive.matches("False")
				&& groupUser.matches("False")) {
			Toast.makeText(activity, "Invalid Username and Password",
					Toast.LENGTH_SHORT).show();
			adminDialog();
		} else if (loginResult.matches("True") && isActive.matches("0")) {
			Toast.makeText(activity, "User Status is inactive.Login Failed",
					Toast.LENGTH_SHORT).show();
			adminDialog();
		} else {
			Toast.makeText(activity, "Invalid Username and Password",
					Toast.LENGTH_SHORT).show();
			adminDialog();
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
}
