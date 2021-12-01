package com.winapp.trackwithmap;

/**
 * Created by user on 23-May-17.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.winapp.SFA.R;

import java.util.Map;

public class Helper {
    private static final String DIRECTION_API = "https://maps.googleapis.com/maps/api/directions/json?origin=";
  // public static final String API_KEY = "AIzaSyAqBfC0mItHCr1F4YB0yGKlnepgGb53BL0";

//    public static final String API_KEY =  "AIzaSyCuh-pTsf-04BNpf32rtxIUiZD_fAl8VQI";
public static final String API_KEY =  "AIzaSyBO8j2jISVChj87YAs3ZflCnhUJBoD9x_g";
    public static final int MY_SOCKET_TIMEOUT_MS = 5000;
    public static String getUrl(String originLat, String originLon, String destinationLat, String destinationLon){
        return Helper.DIRECTION_API + originLat+","+originLon+"&destination="+destinationLat+","+destinationLon+"&key="+API_KEY;
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static Uri buildURI(String url,String methodName, Map<String, String> params) {
        // build url with parameters.
        Uri.Builder builder = Uri.parse(url+methodName).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }
   /* public static void showSnackBar(Context context, View view, String text) {
        Snackbar sb = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        sb.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        sb.show();
    }*/
    /*public static String getErrorType( Context context,Object error) {
        if (error instanceof TimeoutError) {
            return context.getResources().getString(R.string.generic_server_timeout);
        } else if (error instanceof ServerError) {
            return context.getResources().getString(R.string.generic_server_down);
        } else if (error instanceof AuthFailureError) {
            return context.getResources().getString(R.string.auth_failed);
        } else if (error instanceof NetworkError) {
            return context.getResources().getString(R.string.no_internet);
        } else if (error instanceof NoConnectionError) {
            return context.getResources().getString(R.string.no_network_connection);
        } else if (error instanceof ParseError) {
            return context.getResources().getString(R.string.parsing_failed);
        }
        return context.getResources().getString(R.string.generic_error);
    }*/
}