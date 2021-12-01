package com.winapp.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by Winapp on 21-Jul-16.
 */
public class AskPermission {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static final int REQUEST_EXTERNAL_LOCATION = 2;
/*    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };*/

//    private static String[] PERMISSIONS_LOCATION = {
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION
//    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        /*Log.d("askStoragePermission", "-> askStoragePermission");
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int coarseLocationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED
                || coarseLocationPermission != PackageManager.PERMISSION_GRANTED || fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }*/
    }

//    public static void verifyLocationPermissions(Activity activity) {
//        // Check if we have read or write permission
//        Log.d("askLocationPermission", "-> askLocationPermission");
//        int coarseLocationPermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION);
//        int fineLocationPermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION);
//
//        if (coarseLocationPermission != PackageManager.PERMISSION_GRANTED || fineLocationPermission != PackageManager.PERMISSION_GRANTED)  {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(activity,
//                    PERMISSIONS_LOCATION,
//                    REQUEST_EXTERNAL_LOCATION
//            );
//        }
//    }

}
