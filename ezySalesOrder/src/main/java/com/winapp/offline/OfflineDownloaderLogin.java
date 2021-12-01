package com.winapp.offline;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.util.Log;

import com.winapp.SFA.R;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.XMLParser;
import com.winapp.offline.OfflineXMLAccessTask.XMLCallbackInterface;
import com.winapp.offline.OfflineXMLAccessTask.XMLErrorType;
import com.winapp.offline.SoapAccessTask.CallbackInterface;
import com.winapp.util.ErrorLog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OfflineDownloaderLogin {

    public interface OnDownloadCompletionListener {
        public void onCompleted();
    }
    public interface OnDownloadFailedListener {
        public void onFailed();
    }

    private  int failed = 0;
    private String COMPANYLOGO = "Logo";
    private String webserviceUrl;
    private List<PropertyInfo> params;
    private HashMap<String, String> hmParams;
    private OfflineHelper helper;
    private Activity activity;
    boolean switchToOffline;
    private OfflineDatabase offlinedb;
    private String mStatus,modifydate;
    private OnDownloadFailedListener failedListener;
    private OnDownloadCompletionListener listener;
    private int mCount = 0;
    private Exception exception = null;
    private ErrorLog errorLog;
    private boolean isDownloadingImage = false;
    ArrayList<String> CompanyCodeArr = new ArrayList<String>();
    public OfflineDownloaderLogin(Activity activity, String valid_url) {
        // TODO Auto-generated constructor stub
        this.webserviceUrl = valid_url;
        this.activity = activity;
        new OfflineDatabase(activity);
        this.helper = new OfflineHelper(activity);
        OfflineDatabase.init(activity);
        this.offlinedb = new OfflineDatabase(activity);
        errorLog =new ErrorLog();

    }

    public void setOnDownloadCompletionListener(OnDownloadCompletionListener listener) {
        this.listener = listener;
    }
    public void setOnDownloadFailednListener(OnDownloadFailedListener listener) {
        this.failedListener = listener;
    }

    public void startDownload(String status) {
        mCount = 0;
        mStatus = status;
        exception = null;
//        helper.showProgressDialog(mStatus + ".");

        Log.d("Download Thread ", "Thread ");

        try {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    helper.updateProgressDialog(mStatus + "..");

                    offlinedb.dropUserMaster();

                    params = new ArrayList<PropertyInfo>();
                    params.clear();
                    params.add(newPropertyInfo("Code", "W!napp@!@#^"));

                    new SoapAccessTask(activity, webserviceUrl, "fncGetUserMaster",
                            params, new GetUserMaster()).execute();

                }
            });

        } catch (Exception e) {
            exception = e;
            Log.d("Final ", "exception not null");
//            helper.dismissProgressDialog();
            helper.showErrorDialog(Log.getStackTraceString(exception));
            errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
        }
    }

    public class GetUserMaster implements CallbackInterface {
        @Override
        public void onSuccess(JSONArray jsonArray) {
            try {
                Log.d("GetUserMaster Download", "Start... ");

                OfflineDatabase.store_usermaster(jsonArray);

            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());

            } finally {

                if (exception == null) {
                    try {

                        String deviceId = OfflineDatabase.getDeviceId();
                        /*************Downloading GetRunningNextNumber From WebService**************/
                        params = new ArrayList<PropertyInfo>();
                        params.clear();
                        params.add(newPropertyInfo("DeviceID", deviceId));
//                        helper.updateProgressDialog(mStatus + "...");
                        new SoapAccessTask(activity, webserviceUrl,
                                "fncGetNextRunningNumber", params,
                                new GetRunningNextNumber()).execute();

//						params = new ArrayList<PropertyInfo>();
//						params.clear();
//						helper.updateProgressDialog(mStatus + ".");
//						new SoapAccessTask(activity, webserviceUrl,"fncGetServerDateTime", params,new ServerDateTime()).execute();

                    } catch (Exception e) {
                        if (exception == null) {
                            exception = e;
                        } else {
                            Log.d("trycatch", "exception not null");
//                            helper.dismissProgressDialog();
                            helper.showErrorDialog(Log.getStackTraceString(exception));
                        }
                        errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
                    }
                }
            }
        }


        @Override
        public void onFailure(Exception error) {

            if (exception == null) {
                exception = error;
                Log.d("GetUserMaster ", "exception not null");
//                helper.dismissProgressDialog();
                helper.showErrorDialog(Log.getStackTraceString(exception));
            }

        }
    }

    private class ServerDateTime implements CallbackInterface {

        String companycode = "";

        public ServerDateTime(String companyCode) {
            this.companycode = companyCode;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            if (exception == null) {
                Log.d("ServerDateTime Download", "Start... " + companycode);
                try {
                    int len = jsonArray.length();

                    Log.d("ServerDate", "ServerDate");
                    for (int i = 0; i < len; i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        OfflineDatabase.store_datetime(object.getString("ServerDate"), companycode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Exception error) {
            if (exception == null) {
                exception = error;
                if (!isNetworkReachable()) {
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    private class GetRunningNextNumber implements CallbackInterface {
        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetRunningNextNumber", "Start... ");
            try {

                OfflineDatabase.store_NextRunningNumber(jsonArray);

                String datetime = OfflineDatabase.getServerDate();
                if (datetime != null && !datetime.isEmpty()) {

                } else {
                    datetime = "";
                }

                /*************Downloading GetCustomerPrice From WebService**************/
//                helper.updateProgressDialog(mStatus + ".");
                new SoapAccessTask(activity, webserviceUrl, "fncGetCompany",
                        null, new GetCompany()).execute();


            } catch (Exception e) {
                e.printStackTrace();

                if (exception == null) {
                    exception = e;
                } else {
                    Log.d("Getrunningnumber", "exception not null");
//                    helper.dismissProgressDialog();
                    helper.showErrorDialog(Log.getStackTraceString(exception));
                }
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
            }
        }

        @Override
        public void onFailure(Exception error) {
            if (exception == null) {
                exception = error;
                Log.d("GetRunningNextNumber ", "exception not null");
//                helper.dismissProgressDialog();
                helper.showErrorDialog(Log.getStackTraceString(exception));
            }
        }
    }

    public class GetCustomer implements CallbackInterface {
        String companycode, datetime;

        public GetCustomer(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;

        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetCustomer Download", "Start... " + companycode);
            try {
                OfflineDatabase.store_customer(companycode, datetime, jsonArray);
            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
            }
        }

        @Override
        public void onFailure(Exception error) {
            if (exception == null) {
                exception = error;
                if (!isNetworkReachable()) {
//                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetProduct implements CallbackInterface {
        String companycode, datetime;

        public GetProduct(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetProduct Download", "Start... " + companycode);
            try {
                mCount = mCount + 1;
                Log.d("mCount", "... " + mCount);
                OfflineDatabase.store_product(companycode, datetime, jsonArray);

//                Cursor CCodeArr = OfflineDatabase.getCompanyCount();
                Log.d("Final", "CCodeArr.getCount()... " + CompanyCodeArr.size());
                Log.d("Final", "mCount... " + mCount);
//                if (CCodeArr.getCount() == mCount) {
                if (CompanyCodeArr.size() == mCount) {
                    if (exception == null) {
                        Log.d("Final ", "exception null ");
                        Log.d("Final ", "listener ");
                        if (listener != null) {
                            Log.d("OfflineImageDownloader ", "listener ");
                            listener.onCompleted();
                        }
//                        helper.dismissProgressDialog();
//                        helper.showLongToast(R.string.completed);
                        OfflineDatabase.store_downloadstatus();
                    } else {
                        Log.d("Final ", "exception not null");
//                        helper.dismissProgressDialog();
                        helper.showErrorDialog(Log.getStackTraceString(exception));
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
            }
        }

        @Override
        public void onFailure(Exception error) {
            if (exception == null) {
                exception = error;
                if (!isNetworkReachable()) {
//                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetCompany implements CallbackInterface {
        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetCompany Download", "Start... ");
            String compycode = SupplierSetterGetter.getCompanyCode();
            CompanyCodeArr.clear();
            try {

//                CompanyCodeArr = OfflineDatabase.store_company(jsonArray);
                CompanyCodeArr.add(compycode);

            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
            } finally {
                try {
                    loadCompanyBasedData();
                } catch (Exception e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        Log.d("GetCompany trycatch ", "exception not null");
//                        helper.dismissProgressDialog();
                        helper.showErrorDialog(Log.getStackTraceString(exception));
                    }
                    errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
                }
            }
        }

        @Override
        public void onFailure(Exception error) {
            if (exception == null) {
                exception = error;
                Log.d("GetCompany ", "exception not null");
//                helper.dismissProgressDialog();
                helper.showErrorDialog(Log.getStackTraceString(exception));
            }
        }
    }

    private void loadCompanyBasedData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                try {

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

//                            ArrayList<String> CompanyCodeArr = new ArrayList<String>();
//                            CompanyCodeArr.clear();
//                            CompanyCodeArr = OfflineDatabase.getCompanyCode();
                            Log.d("CompanyCodeArr size", "------" + CompanyCodeArr.size());
                            String datetime="";


                            for (int i = 0; i < CompanyCodeArr.size(); i++) {
                                final String companycode = CompanyCodeArr.get(i);
                                Log.d("companycode", "------->" + companycode);

                                Log.d("-----  Server Date", "-------");
                                params = new ArrayList<PropertyInfo>();
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                helper.updateProgressDialog(mStatus + ".");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetServerDateTime", params, new ServerDateTime(companycode)).execute();

                                datetime = OfflineDatabase.getServerDate();

                                Log.d("modifydate",datetime);
                                Log.d("-----------  Customer", "-----------");
                                List<PropertyInfo> param1 = new ArrayList<PropertyInfo>();
                                param1.clear();
                                param1.add(newPropertyInfo("CompanyCode", companycode));
                                param1.add(newPropertyInfo("CustomerCode", ""));
                                param1.add(newPropertyInfo("ModifyDate", datetime));

//                                helper.updateProgressDialog(mStatus + "...");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetCustomer", param1, new GetCustomer(companycode, datetime)).execute();

                                Log.d("-----------  Product", "-----------");
                                List<PropertyInfo> param2 = new ArrayList<PropertyInfo>();
                                param2.clear();
                                param2.add(newPropertyInfo("CompanyCode", companycode));
                                param2.add(newPropertyInfo("ProductCode", ""));
                                param2.add(newPropertyInfo("CategoryCode", ""));
                                param2.add(newPropertyInfo("SubCategoryCode", ""));
                                param2.add(newPropertyInfo("ModifyDate", datetime));
//                                helper.updateProgressDialog(mStatus + "...");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetProduct", param2, new GetProduct(companycode, datetime)).execute();

                            }
                        }
                    });

                } catch (Exception e) {
                    errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
                    Log.d("-----------  for", "-----------");
                    Looper.myLooper().quit();
                }
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();

    }




    public void onError(final Exception error) {
        new Thread() {
            @Override
            public void run() {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        exception = error;
                    }
                });
            }
        }.start();
    }

    public void onXMLError(final XMLErrorType error) {
        new Thread() {
            @Override
            public void run() {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        helper.dismissProgressDialog();
                        if (error == XMLErrorType.NETWORK_UNAVAILABLE) {
                            helper.showLongToast(R.string.error_downloading_data_no_network_connection);
                        } else {
                            helper.showLongToast(R.string.error_downloading_data);
                        }
                        activity.finish();
                    }
                });
            }
        }.start();
    }

    public static PropertyInfo newPropertyInfo(String name, String value) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName(name);
        propertyInfo.setValue(value);
        return propertyInfo;
    }

    private boolean isNetworkReachable() {
        ConnectivityManager mManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = mManager.getActiveNetworkInfo();
        if (current == null) {
            return false;
        }
        return (current.getState() == NetworkInfo.State.CONNECTED);
    }

}
