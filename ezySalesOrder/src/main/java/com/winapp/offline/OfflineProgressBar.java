package com.winapp.offline;

import android.app.ProgressDialog;
import android.content.Context;

public class OfflineProgressBar {
	
	 private Context context;
	    private ProgressDialog progressDialog;
	   public OfflineProgressBar(Context context) {
	        this.context = context;
	    }
	   public void dismissProgressDialog() {
	        if (progressDialog != null) {
	            progressDialog.dismiss();
	            progressDialog = null;
	        }
	    }
	   public void OfflineProgressBar(String title, String message) {
	    	  
			// prepare for a progress bar dialog
			progressDialog = new ProgressDialog(context);
			progressDialog.setCancelable(false);
			progressDialog.setTitle(title);
			progressDialog.setMessage(message);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); 
//			progressDialog.setProgress(0);
			progressDialog.setMax(100);
			progressDialog.show();
	//
//			//reset progress bar status
//			progressBarStatus = 0;
	//
//			//reset filesize
//			fileSize = 0;
	//
//			new Thread(new Runnable() {
//			  public void run() {
//				while (progressBarStatus < 100) {
	//
//				  // process some tasks
//				  progressBarStatus = doSomeTasks();
	//
//				  // your computer is too fast, sleep 1 second
//				  try {
//					Thread.sleep(1000);
//				  } catch (InterruptedException e) {
//					e.printStackTrace();
//				  }
	//
//				  // Update the progress bar
//				  progressBarHandler.post(new Runnable() {
//					public void run() {
//						progressDialog.setProgress(progressBarStatus);
//					}
//				  });
//				}
	//
//				// ok, file is downloaded,
//				if (progressBarStatus >= 100) {
	//
//					// sleep 2 seconds, so that you can see the 100%
//					try {
//						Thread.sleep(2000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
	//
//					// close the progress bar dialog
//					progressDialog.dismiss();
//				}
//			  }
//		       }).start();
//	    }
	//
//				// file download simulator... a really simple
//				public int doSomeTasks() {
//					
//					while (fileSize <= 1000000) {
//				
//						fileSize++;
//				
//						if (fileSize == 100000) {
//							return 10;
//						} else if (fileSize == 200000) {
//							return 20;
//						} else if (fileSize == 300000) {
//							return 30;
//						} else if (fileSize == 400000) {
//							return 40;
//						} else if (fileSize == 500000) {
//							return 50;
//						}
//						else if (fileSize == 600000) {
//							return 60;
//						}
//						else if (fileSize == 700000) {
//							return 70;
//						}
//						else if (fileSize == 800000) {
//							return 80;
//						}
//						else if (fileSize == 900000) {
//							return 90;
//						}
//					}
//				
//					return 100;
//				
//				}
	    	
	    	 // Start lengthy operation in a background thread
	        /*new Thread(new Runnable() {
	            public void run() {

	                while (mProgressStatus < 100) {
	                    mProgressStatus += 1;
	                    // Update the progress bar
	                    progressBarHandler.post(new Runnable() {
	                        public void run() {
	                        	if(progressDialog!=null){
	                        	progressDialog.setProgress(mProgressStatus); 
	                        	}
	                        }
	                    });
	                    try { 
	                        //Display progress slowly
	                        Thread.sleep(250);
	                    } catch (InterruptedException e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	            
	        }).start();*/
	    }
}
