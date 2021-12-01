package com.winapp.printer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;

public class UIHelper {
	private Context context;
	private ProgressDialog progressDialog;
	private LinearLayout spinnerLayout;
	private ProgressBar progressBar;

	/**
	 * @param activity
	 */
	public UIHelper(Context context) {
		this.context = context;		
	}

	/**
	 * Construct and display a simple Alert Dialog
	 * 
	 * @param title
	 *            Alert dialog title
	 * @param message
	 *            Alert message to display
	 * @param icon
	 *            Used to set icon
	 * @param listener
	 *            Listener to be invoked when the positive button of the dialog
	 *            is pressed
	 */
	public void showAlertDialog(String title, String message, int icon,
			OnClickListener listener) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setIcon(icon);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
				context.getString(R.string.ok), listener);
		alertDialog.show();
	}

	/**
	 * Construct and display a simple Alert Dialog
	 * 
	 * @param titleResId
	 *            Alert dialog title
	 * @param messageResId
	 *            Alert message to display
	 * @param icon
	 *            Used to set icon
	 * @param listener
	 *            Listener to be invoked when the positive button of the dialog
	 *            is pressed
	 */
	public void showAlertDialog(int titleResId, int messageResId, int icon,
			OnClickListener listener) {
		showAlertDialog(context.getString(titleResId),
				context.getString(messageResId), icon, listener);
	}

	/**
	 * Construct and display a simple Error Dialog
	 * 
	 * @param message
	 *            Alert message to display
	 */
	public void showErrorDialog(String message) {
		// AlertDialog.Builder builder = new AlertDialog.Builder(context);

		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.custom_error_dialog);
		dialog.setTitle("Error");
		dialog.setCancelable(true);

		TextView text = (TextView) dialog.findViewById(R.id.message);
		text.setText(message);

		// set up button
		Button button = (Button) dialog.findViewById(R.id.ok);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		// now that the dialog is set up, it's time to show it
		dialog.show();
		/*
		 * builder.setIcon(R.drawable.ic_error
		 * ).setTitle("Error").setMessage(message).setCancelable(false)
		 * .setPositiveButton(R.string.ok, null); builder.create().show();
		 */
	}

	/**
	 * Construct and display a simple Error Dialog
	 * 
	 * @param resId
	 *            Alert message string resource Id to display
	 */
	public void showErrorDialog(int resId) {
		showErrorDialog(context.getString(resId));
	}

	/**
	 * 
	 * Construct and display Progress Dialog
	 * 
	 * @param title
	 *            title to set
	 * @param message
	 *            message to display
	 */
	public void showProgressDialog(String title, String message) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.setIndeterminate(true);
		progressDialog.setIndeterminateDrawable(context.getResources()
				.getDrawable(R.drawable.progress_spinner));
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	/**
	 * Construct and display Progress Dialog with title "Please Wait"
	 * 
	 * @param message
	 *            message to display
	 */
	public void showProgressDialog(String message) {
		showProgressDialog(context.getString(R.string.title_please_wait),
				message);
	}

	/**
	 * Construct and display Progress Dialog with title "Please Wait"
	 * 
	 * @param message
	 *            message to display
	 */
	public void showProgressDialog(int messageResId) {
		showProgressDialog(R.string.title_please_wait, messageResId);
	}

	/**
	 * Construct and display Progress Dialog with title and message
	 * 
	 * @param titleResId
	 *            title to display
	 * @param messageResId
	 *            message to display
	 */
	public void showProgressDialog(int titleResId, int messageResId) {
		showProgressDialog(context.getString(titleResId),
				context.getString(messageResId));
	}

	/**
	 * Returns status of Progress Dialog
	 */
	public boolean isProgressDialogShown() {
		if (progressDialog != null && progressDialog.isShowing()) {
			return true;
		}
		return false;
	}

	/**
	 * Updates the Progress Dialog message
	 * 
	 * @param message
	 *            message to be updated on the progress dialog
	 * @return true only if progress dialog is updated
	 */
	public boolean updateProgressDialog(String message) {
		if (progressDialog != null) {
			progressDialog.setMessage(message);
			return true;
		}
		return false;
	}

	/**
	 * Updates the Progress Dialog message
	 * 
	 * @param messageResId
	 *            message to be updated on the progress dialog
	 * @return true only if progress dialog is updated
	 */
	public boolean updateProgressDialog(int messageResId) {
		return updateProgressDialog(context.getString(messageResId));
	}

	/**
	 * Dismiss Progress Dialog
	 */
	public void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * Constructs and shows a long Toast message
	 * 
	 * @param message
	 *            Message to display
	 */
	public void showLongToast(String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * Constructs and shows a short Toast message
	 * 
	 * @param message
	 *            Message to display
	 */
	public void showShortToast(String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Constructs and shows a Toast message for the specified duration
	 * 
	 * @param message
	 *            Message to display
	 * @param duration
	 *            time to display Toast in milliseconds
	 */
	public void showToast(String message, int duration) {
		Toast.makeText(context, message, duration).show();
	}

	/**
	 * Constructs and shows a long Toast message
	 * 
	 * @param resId
	 *            String resource id
	 */
	public void showLongToast(int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
	}

	/**
	 * Constructs and shows a short Toast message
	 * 
	 * @param resId
	 *            String resource id
	 */
	public void showShortToast(int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Constructs and shows a Toast message for the specified duration
	 * 
	 * @param resId
	 *            String resource id
	 * @param duration
	 *            time to display Toast in milliseconds
	 */
	public void showToast(int resId, int duration) {
		Toast.makeText(context, resId, duration).show();
	}

	public void showProgressView(View view) {
		progressBar = new ProgressBar(context);
		spinnerLayout = new LinearLayout(context);
		spinnerLayout.setGravity(Gravity.CENTER);
		((Activity) context).addContentView(spinnerLayout, new LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(view, false);
		
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(context.getResources()
				.getDrawable(R.drawable.greenprogress));

		spinnerLayout.addView(progressBar);
	}

	public void dismissProgressView(View view) {
		
		if(progressBar!=null && progressBar.isShown()){
		progressBar.setVisibility(View.GONE);
		spinnerLayout.setVisibility(View.GONE);
		enableViews(view, true);
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
	public Double displayMetrics() {
		double screenInches;
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int dens = dm.densityDpi;
		double wi = (double) width / (double) dens;
		double hi = (double) height / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		screenInches = Math.sqrt(x + y);
		return screenInches;
	}
}
