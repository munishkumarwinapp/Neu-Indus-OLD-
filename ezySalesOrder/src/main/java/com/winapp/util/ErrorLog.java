package com.winapp.util;

/**
 * Created by Winapp on 25-Apr-17.
 */

import android.os.Environment;
import android.util.Log;

import com.winapp.SFA.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.util.Date;

public class ErrorLog {

    String folder_main = "SFA";
    String fpath = Environment.getExternalStorageDirectory()+ "/" + folder_main + "/errorlog.txt";

    public File ErrorLog() {

        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir= new File(Environment.getExternalStorageDirectory()+ "/" + folder_main, "/errorlog.txt");
            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("error log", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v("file creation", "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    public Boolean write(String fcontent) {
        try {
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            File file = new File(fpath);
//            RandomAccessFile randomfile = new RandomAccessFile(file,"rw");
            // If file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.newLine();
            bw.append("\n\r");
            bw.write(currentDateTimeString + " : " + fcontent);
            bw.close();

            Log.d("Suceess", "Sucesss");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String read() {

        BufferedReader br = null;
        String response = null;

        try {

            StringBuffer output = new StringBuffer();

            br = new BufferedReader(new FileReader(fpath));
            String line = "";
            while ((line = br.readLine()) != null) {
                output.append(line + "n");
            }
            response = output.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }
        return response;

    }
}
