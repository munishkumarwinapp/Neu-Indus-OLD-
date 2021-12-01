package com.winapp.offline;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.winapp.printer.UIHelper;


public class SoapAccessTask extends AsyncTask<Void, Void, JSONArray> {
    private final String NAMESPACE = "http://tempuri.org/";
    private String mWebserviceUrl, mMethodName, mSoapAction;
    private JSONArray jsonArray;
    private CallbackInterface mCallbackInterface;
    private List<PropertyInfo> mParams;
    private Context mContext;
    private ErrorType mError;
    private UIHelper helper;
    private String progressDialogTitle, progressDialogMessage;
    private Exception exception =null;
    private int TimeOut=200000;
    public enum ErrorType {
        NETWORK_UNAVAILABLE, IO_ERROR, XML_PARSE_ERROR, JSON_PARSE_ERROR
    };

    public interface CallbackInterface {
        public void onSuccess(JSONArray jsonArray);

        public void onFailure(Exception error);
    }

    public SoapAccessTask(Context context, String methodName, List<PropertyInfo> params,
            CallbackInterface callbackInterface) {
        mMethodName = methodName;
        mSoapAction = NAMESPACE + methodName;
        mParams = params;
        mCallbackInterface = callbackInterface;
        mContext = context;
        helper = new UIHelper(mContext);
    }

    public SoapAccessTask(Context context, String webserviceUrl, String methodName, List<PropertyInfo> params,
            CallbackInterface callbackInterface) {
        mMethodName = methodName;
        mSoapAction = NAMESPACE + methodName;
        mParams = params;
        mCallbackInterface = callbackInterface;
        mContext = context;
        mWebserviceUrl = webserviceUrl;
    }

    /**
     * Sets the progress dialog message. The progress dialog is shown if and
     * only if the showProgressDialog(String message) or
     * showProgressDialog(String title, String message) is called before
     * execute()
     * 
     * @param messageId
     *            messageId to set in the progress dialog
     * @return returns this object to make chain calls
     */
    public SoapAccessTask showProgressDialog(int messageId) {
        return showProgressDialog(null, mContext.getString(messageId));
    }

    /**
     * Sets the progress dialog message. The progress dialog is shown if and
     * only if the showProgressDialog(String message) or
     * showProgressDialog(String title, String message) is called before
     * execute()
     * 
     * @param message
     *            message to set in the progress dialog
     * @return returns this object to make chain calls
     */
    public SoapAccessTask showProgressDialog(String message) {
        return showProgressDialog(null, message);
    }

    /**
     * Sets the progress dialog title and message. The progress dialog is shown
     * if and only if the showProgressDialog(String message) or
     * showProgressDialog(String title, String message) is called before
     * execute()
     * 
     * @param messageId
     *            message to set in the progress dialog
     * @return returns this object to make chain calls
     */
    public SoapAccessTask showProgressDialog(int titleId, int messageId) {
        progressDialogTitle = mContext.getString(titleId);
        progressDialogMessage = mContext.getString(messageId);
        return this;
    }

    /**
     * Sets the progress dialog title and message. The progress dialog is shown
     * if and only if the showProgressDialog(String message) or
     * showProgressDialog(String title, String message) is called before
     * execute()
     * 
     * @param message
     *            message to set in the progress dialog
     * @return returns this object to make chain calls
     */
    public SoapAccessTask showProgressDialog(String title, String message) {
        progressDialogTitle = title;
        progressDialogMessage = message;
        return this;
    }
    
    @Override
    protected void onPreExecute() {
    	jsonArray = null;
        if (progressDialogTitle != null && progressDialogMessage != null) {
          helper.showProgressDialog(progressDialogTitle, progressDialogMessage);
        } else if (progressDialogMessage != null) {
         helper.showProgressDialog(progressDialogMessage);
        }
    }

    @Override
    protected JSONArray doInBackground(Void... params) {

        try {
        	Log.d("Download mMethodName", "-> "+mMethodName);
            if (mParams != null) {
                Log.d("Download mParams", "-> " + mParams.toString());
            }
        SoapObject request = new SoapObject(NAMESPACE, mMethodName);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(mWebserviceUrl,TimeOut);
        httpTransport.debug = true;
  
            if (mParams != null) {
                for (PropertyInfo propertyInfo : mParams) {
                    request.addProperty(propertyInfo);
                }
            }
            httpTransport.call(mSoapAction, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            jsonArray = new JSONArray(result.toString());

            Log.d("soapjsonArray", "-> "+jsonArray.toString());
            
        } catch (IOException e) {
        	exception = e;
            mError = ErrorType.IO_ERROR;
            e.printStackTrace();
            return null;
        } catch (XmlPullParserException e) {
        	exception = e;
            mError = ErrorType.XML_PARSE_ERROR;
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
        	exception = e;
            mError = ErrorType.JSON_PARSE_ERROR;
            e.printStackTrace();
            return null;
        } catch (Exception e) {
        	exception = e;
            mError = ErrorType.IO_ERROR;
            e.printStackTrace();
            return null;
        }
        return jsonArray;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        if (jsonArray != null) {
            mCallbackInterface.onSuccess(jsonArray);
        } else {
        	
        mCallbackInterface.onFailure(exception);
 
        }
 
    }

    private boolean isNetworkReachable() {
        ConnectivityManager mManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = mManager.getActiveNetworkInfo();
        if (current == null) {
            return false;
        }
        return (current.getState() == NetworkInfo.State.CONNECTED);
    }
}
