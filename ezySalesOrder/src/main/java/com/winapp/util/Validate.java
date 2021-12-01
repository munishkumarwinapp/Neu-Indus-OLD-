package com.winapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Validate {


	public static String strValidate(String value) {
		String mValue = "";

		if (value != null && !value.isEmpty()) {
			mValue = value;
		}
		return mValue;
	}

	public static String strFormattedDate(String value) {
		String dateval = "";
		if (value != null && !value.isEmpty()) {
			StringTokenizer tokens = new StringTokenizer(value, " ");
			dateval = tokens.nextToken();

		} else {
			dateval = "";
		}

		return dateval;
	}
	
	/**  Hides the soft keyboard **/
	public static void hideSoftKeyboard(Activity mContext) {
	     if(mContext.getCurrentFocus()!=null) {
	         InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
	         inputMethodManager.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), 0);
	     }
	 }

	 /**  Shows the soft keyboard  **/
	 public static void showSoftKeyboard(View view, Context mContext) {
	     InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
	     view.requestFocus();
	     inputMethodManager.showSoftInput(view, 0);
	 }

	public static String twoDecimalPoint(double d) {
		  DecimalFormat df = new DecimalFormat("#.##");
		  df.setMinimumFractionDigits(2);
		  String tax = df.format(d);
		  return tax;
		 }
	public static String fourDecimalPoint(double d) {
		  DecimalFormat df = new DecimalFormat("#.####");
		  df.setMinimumFractionDigits(4);
		  String tot = df.format(d);

		  return tot;
		 }

	public static void showViews(boolean show, View... views) {
		int visibility = show ? View.VISIBLE : View.GONE;
		for (View view : views) {
			view.setVisibility(visibility);
		}
	}

	public static String conver24to12Format(String time) {
//		final String time = "23:15";
		SimpleDateFormat _24HourSDF=null,_12HourSDF=null;
		Date _24HourDt=null;
		String result="";
		try {

			if(time!=null && !time.isEmpty()){
				_24HourSDF = new SimpleDateFormat("HH:mm");
				_12HourSDF = new SimpleDateFormat("hh:mm a");
				_24HourDt = _24HourSDF.parse(time);
				System.out.println(_24HourDt);
				System.out.println(_12HourSDF.format(_24HourDt));
				result = _12HourSDF.format(_24HourDt);
			}

		} catch (final ParseException e) {
			e.printStackTrace();
		}

		return result;
	}
	public static int getApplicationVersionCode(Context context) {
		PackageManager packageManager = context.getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException ex) {
		} catch (Exception e) {
		}
		return 0;
	}
}
