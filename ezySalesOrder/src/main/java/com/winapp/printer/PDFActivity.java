package com.winapp.printer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


public class PDFActivity extends AsyncTask<Void, Void, String>{
    private ProgressDialog p;
    private static final int  MEGABYTE = 1024;
    int totalSize =0,downloadedSize = 0;
    Activity activity;
    String fileUrl,fileName;
    public PDFActivity(Activity activity,String fileurl,String fileName){    	
    	this.activity = activity;
    	this.fileUrl = fileurl;
    	this.fileName= fileName;
        this.p=new ProgressDialog(activity);
        Log.d("fileurl",fileurl);
    }
  //  private class DownloadFile extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            p.setMessage("Downloading Report Pdf");
            p.setIndeterminate(false);
            p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            p.setCancelable(false);

            p.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            p.show();
        }
        @Override
        protected String doInBackground(Void... param) {
           // String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
           // String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "SFA");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);
            try{
                pdfFile.createNewFile();
                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.connect();
                // getting file length
                totalSize = urlConnection.getContentLength();
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        p.setMax(100);
                    }
                });

               InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);
                byte[] buffer = new byte[MEGABYTE];
                int bufferLength = 0;
                while((bufferLength = inputStream.read(buffer))>0 ){
                    fileOutputStream.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    // update the progressbar //
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            p.setProgress(downloadedSize);
                        }
                    });
                }
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            p.dismiss();
            Toast.makeText(activity, "Download complete", Toast.LENGTH_SHORT).show();

            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/SFA/" + "report.pdf");  // -> filename = maven.pdf
            Uri path = Uri.fromFile(pdfFile);
            Log.d("path","-->"+path.toString());

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {

            }
        }

   // }


}