package com.winapp.offline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.fwms.LoginActivity;

public class OfflineHelper {
    private Activity context;
    private ProgressDialog progressDialog;
    private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();
	private int mProgressStatus = 0;
	private long fileSize = 0;
    /**
     * @param activity
     */
    public OfflineHelper(Activity context) {
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
    public void showAlertDialog(String title, String message, int icon, OnClickListener listener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(icon);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), listener);
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
    public void showAlertDialog(int titleResId, int messageResId, int icon, OnClickListener listener) {
        showAlertDialog(context.getString(titleResId), context.getString(messageResId), icon, listener);
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
                if(context instanceof LoginActivity){
                context.finish();}
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
    	 
    	offlineDownloadingProgressBar(title,message);
    }

    /**
     * Construct and display Progress Dialog with title "Please Wait"
     * 
     * @param message
     *            message to display
     */
    public void showProgressDialog(String message) {
        showProgressDialog(context.getString(R.string.title_please_wait), message);
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
        showProgressDialog(context.getString(titleResId), context.getString(messageResId));
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
    
    
    public void offlineDownloadingProgressBar(String title, String message) {
    	  
		// prepare for a progress bar dialog
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); 
//		progressDialog.setProgress(0);
		progressDialog.setMax(100);
		progressDialog.show();
//
//		//reset progress bar status
//		progressBarStatus = 0;
//
//		//reset filesize
//		fileSize = 0;
//
//		new Thread(new Runnable() {
//		  public void run() {
//			while (progressBarStatus < 100) {
//
//			  // process some tasks
//			  progressBarStatus = doSomeTasks();
//
//			  // your computer is too fast, sleep 1 second
//			  try {
//				Thread.sleep(1000);
//			  } catch (InterruptedException e) {
//				e.printStackTrace();
//			  }
//
//			  // Update the progress bar
//			  progressBarHandler.post(new Runnable() {
//				public void run() {
//					progressDialog.setProgress(progressBarStatus);
//				}
//			  });
//			}
//
//			// ok, file is downloaded,
//			if (progressBarStatus >= 100) {
//
//				// sleep 2 seconds, so that you can see the 100%
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//				// close the progress bar dialog
//				progressDialog.dismiss();
//			}
//		  }
//	       }).start();
//    }
//
//			// file download simulator... a really simple
//			public int doSomeTasks() {
//				
//				while (fileSize <= 1000000) {
//			
//					fileSize++;
//			
//					if (fileSize == 100000) {
//						return 10;
//					} else if (fileSize == 200000) {
//						return 20;
//					} else if (fileSize == 300000) {
//						return 30;
//					} else if (fileSize == 400000) {
//						return 40;
//					} else if (fileSize == 500000) {
//						return 50;
//					}
//					else if (fileSize == 600000) {
//						return 60;
//					}
//					else if (fileSize == 700000) {
//						return 70;
//					}
//					else if (fileSize == 800000) {
//						return 80;
//					}
//					else if (fileSize == 900000) {
//						return 90;
//					}
//				}
//			
//				return 100;
//			
//			}
    	
    	 // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            @Override
			public void run() {

                while (mProgressStatus < 100) {
                    mProgressStatus += 1;
                    // Update the progress bar
                    progressBarHandler.post(new Runnable() {
                        @Override
						public void run() {
                        	if(progressDialog!=null){
                        	progressDialog.setProgress(mProgressStatus); 
                        	}
                        }
                    });
                    try { 
                        //Display progress slowly
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            
        }).start();
    }
}
