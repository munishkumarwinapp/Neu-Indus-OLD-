package com.winapp.offline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.util.Log;

import com.winapp.SFA.R;
import com.winapp.helper.XMLParser;
import com.winapp.offline.OfflineXMLAccessTask.XMLCallbackInterface;
import com.winapp.offline.OfflineXMLAccessTask.XMLErrorType;
import com.winapp.offline.SoapAccessTask.CallbackInterface;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.util.ErrorLog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OfflineDataDownloader {



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
//	private ArrayList<String> CompanyCodeArr;
    private ErrorLog errorLog;
    private boolean isDownloadingImage = false;
    public OfflineDataDownloader(Activity activity, String valid_url) {
        // TODO Auto-generated constructor stub
        this.webserviceUrl = valid_url;
        this.activity = activity;
        new OfflineDatabase(activity);
        this.helper = new OfflineHelper(activity);
        OfflineDatabase.init(activity);
        this.offlinedb = new OfflineDatabase(activity);
        errorLog =new ErrorLog();

    }

    public OfflineDataDownloader(Activity activity, String valid_url,
                                 String cmpnyCode, String userName) {
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
//	public void startDownload(final boolean switchToOffline, String status) {
//		mStatus = status;
//		//helper.showProgressDialog(R.string.downloading_usermaster);
//		helper.showProgressDialog(mStatus+".");
//		this.switchToOffline = switchToOffline;
//		new Thread() {
//			@Override
//			public void run() {
////				offlinedb.truncateTables();
//				offlinedb.dropUserMaster();
//				offlinedb.dropProductBarCode();
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						
//						helper.updateProgressDialog(mStatus+"..");
//						
//						new SoapAccessTask(activity, webserviceUrl,
//								"fncGetUserMaster", null, new GetUserMaster())
//								.execute();
//						
//					}
//				});
//			}
//		}.start();
//	}
public void downloadImage(boolean isDownloadingImage){
    this.isDownloadingImage = isDownloadingImage;
    Cursor mainImageCursor = OfflineDatabase.getProductMainImage();
    if (mainImageCursor.getCount() > 0) {
        modifydate = OfflineDatabase .getCatalogModifyDate();
    } else {
        modifydate = "";
    }
    Log.d("isDownloadingImage","-->"+isDownloadingImage);
    Log.d("modifydate","-->"+modifydate);
}
    public void startDownload(final boolean switchToOffline, String status) {
        mCount = 0;
        mStatus = status;
        exception = null;
        helper.showProgressDialog(mStatus + ".");

        Log.d("Download Thread ", "Thread ");

        try {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    helper.updateProgressDialog(mStatus + "..");


                    offlinedb.dropUserMaster();
                    offlinedb.dropProductBarCode();
                    offlinedb.dropProductSubImages();

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
            helper.dismissProgressDialog();
            helper.showErrorDialog(Log.getStackTraceString(exception));
            errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
        }
    }

    public void startImageDownload(final boolean switchToOffline, String status, final String currentdate, final String modifydate) {
        mCount = 0;
        mStatus = status;
        exception = null;
        helper.showProgressDialog(mStatus + ".");
        Log.d("Download Thread ", "Thread ");

        try {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    helper.updateProgressDialog(mStatus + "..");
                    offlinedb.dropProductSubImages();
                    loadProductImage(currentdate, modifydate);
                }
            });

        } catch (Exception e) {
            exception = e;
            Log.d("Final ", "exception not null");
            helper.dismissProgressDialog();
            helper.showErrorDialog(Log.getStackTraceString(exception));
            errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
        }
    }

    /**  **/

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
                        helper.updateProgressDialog(mStatus + "...");
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
                            helper.dismissProgressDialog();
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
                helper.dismissProgressDialog();
                helper.showErrorDialog(Log.getStackTraceString(exception));
            }

        }
    }

	/*private class GetUserPermission implements CallbackInterface {

		String gCode;

		public GetUserPermission(String grpCode) {
			this.gCode = grpCode;
		}

		@Override
		public void onSuccess(JSONArray jsonArray) {
			if (exception == null) {
			
			try {
				
				OfflineDatabase.store_userpermission(gCode, jsonArray);
				
			} catch (Exception e) {
				Log.d("GetUserPermission ", "exception catch");
				e.printStackTrace();
			}
			}
		}

		@Override
		public void onFailure(Exception error) {
			if(exception == null){
				exception = error;
				Log.d("GetUserPermission ", "exception not null");
				helper.dismissProgressDialog();
				helper.showErrorDialog(Log.getStackTraceString(exception));
			}
		}
	}*/


    private class GetRunningNextNumber implements CallbackInterface {
        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetRunningNextNumber Download", "Start... ");
            try {

                OfflineDatabase.store_NextRunningNumber(jsonArray);

                String datetime = OfflineDatabase.getServerDate();
                if (datetime != null && !datetime.isEmpty()) {

                } else {
                    datetime = "";
                }

                /*************Downloading GetCustomerPrice From WebService**************/
                params = new ArrayList<PropertyInfo>();
                params.clear();
                params.add(newPropertyInfo("ModifyDate", ""));
                new SoapAccessTask(activity, webserviceUrl, "fncGetCustomerPrice",
                        params, new GetCustomerPrice(datetime)).execute();


            } catch (Exception e) {
                e.printStackTrace();

                if (exception == null) {
                    exception = e;
                } else {
                    Log.d("Getrunningnumber trycatch ", "exception not null");
                    helper.dismissProgressDialog();
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
                helper.dismissProgressDialog();
                helper.showErrorDialog(Log.getStackTraceString(exception));
            }
        }
    }

    public class GetCustomerPrice implements CallbackInterface {

        String companycode, datetime;

        public GetCustomerPrice(String datetime) {
            // TODO Auto-generated constructor stub
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetCustomerPrice Download", "Start... ");
            try {

                Log.d("store_customerprice jsonArray", jsonArray.toString());

                OfflineDatabase.store_customerprice(datetime, jsonArray);

                helper.updateProgressDialog(mStatus + ".");
                new SoapAccessTask(activity, webserviceUrl, "fncGetCompany",
                        null, new GetCompany()).execute();

            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
            }
        }

        @Override
        public void onFailure(Exception error) {
            if (exception == null) {
                exception = error;
                Log.d("GetCustomerPrice ", "exception not null");
                helper.dismissProgressDialog();
                helper.showErrorDialog(Log.getStackTraceString(exception));
            }
        }
    }

    public class GetCompany implements CallbackInterface {
        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetCompany Download", "Start... ");
            ArrayList<String> CompanyCodeArr = new ArrayList<String>();
            CompanyCodeArr.clear();
            try {

                OfflineDatabase.store_company(jsonArray);

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
                        helper.dismissProgressDialog();
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
                helper.dismissProgressDialog();
                helper.showErrorDialog(Log.getStackTraceString(exception));
            }
        }
    }


    /**
     * Loop Start
     **/

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

    private class GetUserPermission implements CallbackInterface {

        String companycode = "";

        public GetUserPermission(String companyCode) {
            this.companycode = companyCode;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            if (exception == null) {


                Log.d("GetUserPermission Download", "Start... " + companycode);
                try {
                    int len = jsonArray.length();

                    Log.d("GetUserPermission", "GetUserPermission");
                    for (int i = 0; i < len; i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        OfflineDatabase.store_userpermission(companycode, object.getString("Usergroupcode"), jsonArray);
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
                Log.d("GetUserPermission ", "exception not null");
                helper.dismissProgressDialog();
                helper.showErrorDialog(Log.getStackTraceString(exception));
            }
        }
    }

    public class GetGeneralSettings implements CallbackInterface {

        String companycode = "";

        public GetGeneralSettings(String companyCode) {
            this.companycode = companyCode;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetGeneralSettings Download", "Start... " + companycode);
            try {
                OfflineDatabase.store_generalsetting(companycode, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetLocation implements CallbackInterface {

        String companycode = "";

        public GetLocation(String companyCode) {
            this.companycode = companyCode;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetLocation Download", "Start... " + companycode);
            try {
                OfflineDatabase.store_location(companycode, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetCurrency implements CallbackInterface {
        String companycode, datetime;

        public GetCurrency(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;

        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetCurrency Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_currency(companycode, datetime, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetCompanyLogo implements XMLCallbackInterface {
        String companycode, datetime;

        public GetCompanyLogo(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(NodeList nl) {
            Log.d("GetCompanyLogo Download", "Start... " + companycode);
            try {
                for (int i = 0; i < nl.getLength(); i++) {

                    Element e = (Element) nl.item(i);
                    String cmpyLogo = XMLParser.getValue(e, COMPANYLOGO);
                    OfflineDatabase.store_companylogo(companycode, cmpyLogo);
                }

            } catch (Exception e) {
                if (exception == null) {
                    exception = e;
                }
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
            }
        }

        @Override
        public void onFailure(XMLErrorType error) {
            if (exception == null) {
//					exception = error;
                if (!isNetworkReachable()) {
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetArea implements CallbackInterface {
        String companycode, datetime;

        public GetArea(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;

        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetArea Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_area(companycode, datetime, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetTerms implements CallbackInterface {
        String companycode, datetime;

        public GetTerms(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;

        }

        @Override
        public void onSuccess(JSONArray jsonArray) {

            Log.d("GetTerms Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_terms(companycode, datetime, jsonArray);

            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
            }

        }

        @Override
        public void onFailure(Exception error) {
            Log.d("GetTerms Download", "onFailure... " + companycode);
            if (exception == null) {
                exception = error;
                if (!isNetworkReachable()) {
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetTax implements CallbackInterface {
        String companycode, datetime;

        public GetTax(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;

        }

        @Override
        public void onSuccess(JSONArray jsonArray) {

            Log.d("GetTaxDownload", "onSuccess... " + companycode);
            try {

                OfflineDatabase.store_tax(companycode, datetime, jsonArray);

            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
            }

        }

        @Override
        public void onFailure(Exception error) {
            Log.d("GetTaxDownload", "onFailure... " + companycode);
            if (exception == null) {
                exception = error;
                if (!isNetworkReachable()) {
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetCustomerGroup implements CallbackInterface {
        String companycode, datetime;

        public GetCustomerGroup(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetCustomerGroup Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_customergroup(companycode, datetime, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetInvoiceHeader implements CallbackInterface {

        String companycode, datetime;

        public GetInvoiceHeader(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetInvoiceHeader", "Start... " + companycode);
            try {

                OfflineDatabase.store_invoiceheader(companycode, datetime, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetInvoiceHeaderWithoutDOSign implements CallbackInterface {

        String companycode, datetime;

        public GetInvoiceHeaderWithoutDOSign(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }


        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetInvoiceHeaderWithoutDOSign Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_invoiceheaderwithoutDOsign(companycode, datetime, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }


    public class GetCategory implements CallbackInterface {
        String companycode, datetime;

        public GetCategory(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetCategory Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_category(companycode, datetime, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetSubCategory implements CallbackInterface {
        String companycode, datetime;

        public GetSubCategory(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetSubCategory Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_subcategory(companycode, datetime, jsonArray);

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
                    helper.dismissProgressDialog();
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

                OfflineDatabase.store_product(companycode, datetime, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetProductBarcode implements CallbackInterface {
        String companycode, datetime;

        public GetProductBarcode(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetProductBarcode Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_productbarcode(companycode, datetime, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetBank implements CallbackInterface {
        String companycode, datetime;

        public GetBank(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetBank Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_bank(companycode, jsonArray);

            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
            }
        }

        @Override
        public void onFailure(Exception error) {
            if (exception == null) {
                exception = error;
            }
        }
    }

    public class GetPayMode implements CallbackInterface {
        String companycode, datetime;

        public GetPayMode(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetPayMode Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_paymode(companycode, jsonArray);


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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetVan implements CallbackInterface {
        String companycode, datetime;

        public GetVan(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;

        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetVan Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_van(companycode, datetime, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetSOHeader implements CallbackInterface {

        String companycode, datetime;

        public GetSOHeader(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetSOHeader Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_soheader(companycode, datetime, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    // Sales return header
    public class GetSRHeader implements CallbackInterface {

        String companycode, datetime;

        public GetSRHeader(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetSRHeader Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_SRheader(companycode, datetime, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }


    public class GetProductMainImage implements XMLCallbackInterface {

        String companycode, datetime;

        public GetProductMainImage(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(NodeList nl) {
            Log.d("ProductMainImage", "Start... " + companycode);
            try {

                OfflineDatabase.store_productmainimage(companycode, datetime, nl);

            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
            }

        }

        @Override
        public void onFailure(XMLErrorType error) {
            if (exception == null) {
                // exception = error;
                if (!isNetworkReachable()) {
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }

    public class GetProductSubImages implements XMLCallbackInterface {

        String companycode, datetime;

        public GetProductSubImages(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(NodeList nl) {
            Log.d("GetProductSubImages Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_productsubimages(companycode, datetime, nl);

            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
            }
        }

        @Override
        public void onFailure(XMLErrorType error) {
            if (exception == null) {
                if (!isNetworkReachable()) {
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }

    }

    public class GetProductSubImagesAll implements XMLCallbackInterface {
        String companycode, datetime;

        public GetProductSubImagesAll(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(NodeList nl) {
            Log.d("GetProductSubImages Download", "Start... " + companycode);
            try {
                mCount = mCount + 1;
                Log.d("mCount", "... " + mCount);

                OfflineDatabase.store_productsubimages(companycode, datetime, nl);

                Cursor CCodeArr = OfflineDatabase.getCompanyCount();
                Log.d("Final", "CCodeArr.getCount()... " + CCodeArr.getCount());
                Log.d("Final", "mCount... " + mCount);
                if (CCodeArr.getCount() == mCount) {

                    if (exception == null) {
                        Log.d("Final ", "exception null ");
                        Log.d("Final ", "listener ");
                        if (listener != null) {
                            Log.d("OfflineImageDownloader ", "listener ");
                            listener.onCompleted();
                        }
                        helper.dismissProgressDialog();
                        helper.showLongToast(R.string.completed);
                        OfflineDatabase.store_downloadstatus();
                    } else {
                        Log.d("Final ", "exception not null");
                        helper.dismissProgressDialog();
                        helper.showErrorDialog(Log.getStackTraceString(exception));
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
            }
        }

        @Override
        public void onFailure(XMLErrorType error) {
            if (exception == null) {
                if (!isNetworkReachable()) {
                    helper.dismissProgressDialog();

                    if(failed ==0){
                        helper.showLongToast("Internet Connection Problem");
                        if (failedListener != null) {
                            Log.d("OfflineImageDownloader ", "sub failed listener ");
                            failedListener.onFailed();
                        }
                        failed = failed + 1;
                    }
                }
            }
        }

    }

    public class GetProductStock implements CallbackInterface {
        String companycode, datetime;

        public GetProductStock(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.d("GetProductStock Download", "Start... " + companycode);
            try {

                OfflineDatabase.store_productstock(companycode, jsonArray);

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
                    helper.dismissProgressDialog();
                    helper.showLongToast("Internet Connection Problem");
                }
            }
        }
    }


    public class GetMobileSettings implements CallbackInterface {
        String companycode, datetime;

        public GetMobileSettings(String companycode, String datetime) {
            // TODO Auto-generated constructor stub
            this.companycode = companycode;
            this.datetime = datetime;
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onSuccess(JSONArray jsonArray) {
            Log.e("Mobile Settings Data", jsonArray.toString());
            Log.d("GetMobileSettings Download", "Start... " + companycode);
            mCount = mCount + 1;
            Log.d("mCount", "... " + mCount);
            try {
                OfflineDatabase.store_mobilesettings(companycode, jsonArray);

                Cursor CCodeArr = OfflineDatabase.getCompanyCount();
                Log.d("Final", "CCodeArr.getCount()... " + CCodeArr.getCount());
                Log.d("Final", "mCount... " + mCount);
                if (CCodeArr.getCount() == mCount) {

                    if (exception == null) {
                        Log.d("Final ", "exception null ");
                        Log.d("Final ", "listener ");
                        if (listener != null) {
                            Log.d("OfflineDataDownloader ", "listener ");
                            listener.onCompleted();
                        }
                        helper.dismissProgressDialog();
                        helper.showLongToast(R.string.data_downloaded_successfully);
                        OfflineDatabase.store_downloadstatus();
                    } else {
                        Log.d("Final ", "exception not null");
                        helper.dismissProgressDialog();
                        helper.showErrorDialog(Log.getStackTraceString(exception));
                    }

                }


//					/*************Downloading InvoiceDetails From WebService**************/
//					params = new ArrayList<PropertyInfo>();
//					params.clear();
//					params.add(newPropertyInfo("CompanyCode", companycode));
//					params.add(newPropertyInfo("InvoiceNo", ""));
//					helper.updateProgressDialog(R.string.downloading3);
//					new SoapAccessTask(activity,webserviceUrl ,"fncGetInvoiceDetail", params,
//							new GetInvoiceDetail(companycode,datetime)).execute();


            } catch (Exception e) {

                if (exception == null) {
                    exception = e;
                }
                errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
//		    	  helper.dismissProgressDialog();
//		       	helper.showErrorDialog(Log.getStackTraceString(e));
//		       activity.finish();
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


//	public class GetInvoiceDetail implements CallbackInterface {
//	
//	 String companycode,datetime;
//	ArrayList<HashMap<String, String>> offlineInvioceDetails = new ArrayList<HashMap<String, String>>();
//	public GetInvoiceDetail(String companycode, String datetime) {
//		// TODO Auto-generated constructor stub
//		this.companycode = companycode;
//		this.datetime = datetime;
//	}
//
//	@Override
//	public void onSuccess(JSONArray jsonArray) {
//	//	helper.updateProgressDialog(R.string.downloading_get_invoicedetail);
//		try {
//			int len = jsonArray.length();
//			
//			for (int i = 0; i < len; i++) 
//			{
//				HashMap<String, String> productvalues = new HashMap<String, String>();
//				JSONObject object = jsonArray.getJSONObject(i);
//				productvalues.put("CompanyCode", companycode);
//				productvalues.put("DateTime", datetime);
//				productvalues.put("InvoiceNo", object.getString("InvoiceNo"));
//				productvalues.put("slNo",object.getString("slNo"));
//				productvalues.put("ProductCode",object.getString("ProductCode"));
//				productvalues.put("ProductName", object.getString("ProductName"));
//				productvalues.put("CQty",object.getString("CQty"));
//				productvalues.put("Lqty", object.getString("LQty"));
//				productvalues.put("Qty",object.getString("Qty"));
//				productvalues.put("FOCQty",object.getString("FOCQty"));
//				productvalues.put("PcsPerCarton", object.getString("PcsPerCarton"));
//				productvalues.put("RetailPrice", object.getString("RetailPrice"));
//				productvalues.put("Price",object.getString("Price"));
//				productvalues.put("Total",object.getString("Total"));
//				//productvalues.put("total","");
//				productvalues.put("ItemDiscount",object.getString("ItemDiscount"));
//				productvalues.put("BillDiscount",object.getString("BillDiscount"));
//				productvalues.put("TotalDiscount",object.getString("TotalDiscount"));
//				productvalues.put("SubTotal",object.getString("SubTotal"));
//				productvalues.put("Tax",object.getString("Tax"));
//				productvalues.put("NetTotal",object.getString("NetTotal"));
//				productvalues.put("TaxType",object.getString("TaxType"));
//				productvalues.put("TaxPerc",object.getString("TaxPerc"));
//				productvalues.put("AverageCost",object.getString("AverageCost"));
//				productvalues.put("CategoryCode",object.getString("CategoryCode"));
//				productvalues.put("SubCategoryCode",object.getString("SubCategoryCode"));
//				productvalues.put("ExchangeQty",object.getString("ExchangeQty"));
//				productvalues.put("CartonPrice",object.getString("CartonPrice"));
//				productvalues.put("CreateUser","");
//				productvalues.put("CreateDate","");
//				productvalues.put("ModifyUser","");
//				productvalues.put("ModifyDate","");
//				productvalues.put("DownloadStatus","0");
//				offlineInvioceDetails.add(productvalues);
//				
//			}
//			//System.out.println("offlineInvioceDetails..."+offlineInvioceDetails.toString());
//			OfflineDatabase.store_invoicedetail(offlineInvioceDetails);
//			
//			/*************Downloading InvoiceCartonDetail From WebService**************/
//				params = new ArrayList<PropertyInfo>();
//				params.clear();
//				params.add(newPropertyInfo("CompanyCode", companycode));
//				params.add(newPropertyInfo("InvoiceNo", ""));
//				helper.updateProgressDialog(R.string.downloading1);
//				new SoapAccessTask(activity,webserviceUrl ,"fncGetInvoiceCartonDetail", params,
//						new GetInvoiceCartonDetail(companycode,datetime)).execute();
//				
//			//	}
//				
//		} catch (JSONException e) {
//				helper.showErrorDialog(Log.getStackTraceString(e));
//			activity.finish();
//		}
//	}
//
//	@Override
//	public void onFailure(ErrorType error) {
//		onError(error);
//	}
//}
//
//public class GetInvoiceCartonDetail implements CallbackInterface {
//
//	String companycode, datetime;
//
//	public GetInvoiceCartonDetail(String companycode, String datetime) {
//		// TODO Auto-generated constructor stub
//		this.companycode = companycode;
//		this.datetime = datetime;
//	}
//
//	@Override
//	public void onSuccess(JSONArray jsonArray) {
//		// helper.updateProgressDialog(R.string.downloading_get_invoicecartondetail);
//		try {
//			int len = jsonArray.length();
//			HashMap<String, String> productvalues = new HashMap<String, String>();
//			for (int i = 0; i < len; i++) {
//				JSONObject object = jsonArray.getJSONObject(i);
//				productvalues.put("companycode", companycode);
//				productvalues.put("datetime", datetime);
//				productvalues.put("invoiceno",
//						object.getString("InvoiceNo"));
//				// productvalues.put("invoicedate",object.getString("InvoiceDate"));
//				productvalues.put("slno", object.getString("slNo"));
//				productvalues.put("productcode",
//						object.getString("ProductCode"));
//				productvalues.put("productname",
//						object.getString("ProductName"));
//				productvalues.put("seqno", object.getString("SeqNo"));
//				productvalues.put("weightbarcode",
//						object.getString("WeightBarcode"));
//				productvalues.put("weight", object.getString("Weight"));
//				OfflineDatabase.store_invoicecartondetail(productvalues);
//
//			}
//				  /*************Downloading ReceiptHeader From WebService**************/
//			    params = new ArrayList<PropertyInfo>();
//			    params.clear();
//			    params.add(newPropertyInfo("CompanyCode", companycode));
//			    params.add(newPropertyInfo("CustomerCode", ""));
//			    params.add(newPropertyInfo("FromDate", "28/04/2014"));
//			    params.add(newPropertyInfo("ToDate", datetime));
//			    params.add(newPropertyInfo("User", ""));
//			    helper.updateProgressDialog(R.string.downloading1);
//			    new SoapAccessTask(activity, webserviceUrl, "fncGetReceiptHeader",
//			      params, new GetReceiptHeader(companycode, datetime)).execute();
//
//		} catch (JSONException e) {
//				helper.showErrorDialog(Log.getStackTraceString(e));
//			activity.finish();
//		}
//	}
//
//	@Override
//	public void onFailure(ErrorType error) {
//		onError(error);
//	}
//}
//	
//	public class GetReceiptHeader implements CallbackInterface {
//		
//		  String companycode,datetime;
//			public GetReceiptHeader(String companycode,String datetime) {
//				// TODO Auto-generated constructor stub
//				this.companycode = companycode;
//				this.datetime = datetime;
//			}
//
//			
//			@Override
//			public void onSuccess(JSONArray jsonArray) {
//				Log.d("GetReceiptHeader Download", "Start... "+companycode);
//				try {
//					int len = jsonArray.length();
//					HashMap<String, String> productvalues = new HashMap<String, String>();
//				 
//					for (int i = 0; i < len; i++) 
//					{
//						JSONObject object = jsonArray.getJSONObject(i);
//						productvalues.put("CompanyCode", companycode);
//						productvalues.put("DateTime", datetime);
//						productvalues.put("ReceiptNo", object.getString("ReceiptNo"));					
//				     	String dateStr = object.getString("ReceiptDate");
//				     	
//				     	 SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
//						 
//						 Date myDate  = dateFormat.parse(dateStr);
//							 
//						 SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
//						 String finalDate = timeFormat.format(myDate);
//						 //System.out.println(finalDate);
//																	
//						productvalues.put("ReceiptDate",finalDate);					
//						productvalues.put("CustomerCode", object.getString("CustomerCode"));
//						productvalues.put("CustomerName",object.getString("CustomerName"));
//						productvalues.put("PaidAmount",object.getString("PaidAmount"));
//						productvalues.put("CreditAmount",object.getString("CreditAmount"));
//						productvalues.put("Paymode",object.getString("Paymode"));
//						productvalues.put("BankCode",object.getString("BankCode"));
//						productvalues.put("ChequeNo",object.getString("ChequeNo"));
//						productvalues.put("ChequeDate",object.getString("ChequeDate"));
//						OfflineDatabase.store_receiptheader(productvalues);
//						
//					}
//					/*************Downloading InvoiceDetails From WebService**************/
//					params = new ArrayList<PropertyInfo>();
//					params.clear();
//					params.add(newPropertyInfo("CompanyCode", companycode));
//					params.add(newPropertyInfo("ReceiptNo", ""));
//					helper.updateProgressDialog(R.string.downloading2);
//					new SoapAccessTask(activity,webserviceUrl ,"fncGetReceiptDetail", params,
//							new GetReceiptDetail(companycode,datetime)).execute();
//					
//					//}
//				} catch (JSONException e) {
//						helper.showErrorDialog(Log.getStackTraceString(e));
//					activity.finish();
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//						helper.showErrorDialog(Log.getStackTraceString(e));
//				}
//			}
//
//			@Override
//			public void onFailure(ErrorType error) {
//				onError(error);
//			}
//		}
//	public class GetReceiptDetail implements CallbackInterface {
//		
//		 String companycode,datetime;
//		ArrayList<HashMap<String, String>> offlineInvioceDetails = new ArrayList<HashMap<String, String>>();
//		public GetReceiptDetail(String companycode, String datetime) {
//			// TODO Auto-generated constructor stub
//			this.companycode = companycode;
//			this.datetime = datetime;
//		}
//
//		@Override
//		public void onSuccess(JSONArray jsonArray) {
//			Log.d("GetReceiptDetail Download", "Start... "+companycode);
//			try {
//				int len = jsonArray.length();
//				
//				for (int i = 0; i < len; i++) 
//				{
//					HashMap<String, String> productvalues = new HashMap<String, String>();
//					JSONObject object = jsonArray.getJSONObject(i);
//					productvalues.put("CompanyCode", companycode);
//					productvalues.put("DateTime", datetime);
//					productvalues.put("ReceiptNo", object.getString("ReceiptNo"));
//					productvalues.put("InvoiceNo", object.getString("InvoiceNo"));
//					productvalues.put("NetTotal", object.getString("NetTotal"));
//					productvalues.put("PaidAmount", object.getString("PaidAmount"));
//					productvalues.put("CreditAmount", object.getString("CreditAmount"));
//					offlineInvioceDetails.add(productvalues);
//					
//				}
//				OfflineDatabase.store_receiptdetail(offlineInvioceDetails);
//				
//				   helper.dismissProgressDialog();
//					helper.showLongToast(R.string.data_downloaded_successfully);				
//					OfflineDatabase.store_downloadstatus();
//					
//			} catch (JSONException e) {
//					helper.showErrorDialog(Log.getStackTraceString(e));
//				activity.finish();
//			}
//		}
//
//		@Override
//		public void onFailure(ErrorType error) {
//			onError(error);
//		}
//	}


    /**
     * ---------------------------------------------------------
     **/

    private void loadCompanyBasedData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                try {

                    activity.runOnUiThread(new Runnable() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void run() {

                            ArrayList<String> CompanyCodeArr = new ArrayList<String>();
                            CompanyCodeArr.clear();
                            CompanyCodeArr = OfflineDatabase.getCompanyCode();
                            Log.d("CompanyCodeArr size", "------" + CompanyCodeArr.size());
                            final String datetime = OfflineDatabase.getServerDate();


                            for (int i = 0; i < CompanyCodeArr.size(); i++) {
                                final String companycode = CompanyCodeArr.get(i);
                                Log.d("companycode", "------->" + companycode);


                                Log.d("-----------  Server Date", "-----------");
                                params = new ArrayList<PropertyInfo>();
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                helper.updateProgressDialog(mStatus + ".");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetServerDateTime", params, new ServerDateTime(companycode)).execute();

                                Log.d("-----------  UserPermission", "-----------");
                                params = new ArrayList<PropertyInfo>();
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                helper.updateProgressDialog(mStatus + ".");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetUserPermission", params, new GetUserPermission(companycode)).execute();

                                Log.d("-----------  General Setting", "-----------");
                                params = new ArrayList<PropertyInfo>();
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                helper.updateProgressDialog(mStatus + "..");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetGeneralSettings", params, new GetGeneralSettings(companycode)).execute();

                                Log.d("-----------  Location", "-----------");
                                params = new ArrayList<PropertyInfo>();
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("LocationCode", ""));
                                helper.updateProgressDialog(mStatus + "..");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetLocation", params, new GetLocation(companycode)).execute();

                                Log.d("-----------  Customer", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("CustomerCode", ""));

                                helper.updateProgressDialog(mStatus + "...");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetCustomer", params, new GetCustomer(companycode, datetime)).execute();

                                Log.d("-----------  Currency", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("CurrencyCode", ""));
                                helper.updateProgressDialog(mStatus + ".");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetCurrency", params, new GetCurrency(companycode, datetime)).execute();

                                Log.d("-----------  CompanyLogo", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                helper.updateProgressDialog(mStatus + "..");
                                new OfflineXMLAccessTask(activity, webserviceUrl, "fncGetCompanyLogo", companycode, new GetCompanyLogo(companycode, datetime)).execute();

                                Log.d("-----------  Area", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("AreaCode", ""));
                                helper.updateProgressDialog(mStatus + "...");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetArea", params, new GetArea(companycode, datetime)).execute();

                                Log.d("-----------  Terms", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("TermCode", ""));
                                helper.updateProgressDialog(mStatus + ".");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetTerms", params, new GetTerms(companycode, datetime)).execute();

                                Log.d("-----------  TaxCode", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("TaxCode", ""));
                                params.add(newPropertyInfo("TaxName", ""));
                                helper.updateProgressDialog(mStatus + ".");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetTax", params, new GetTax(companycode, datetime)).execute();


                                Log.d("-----------  CustomerGroup", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("CustomerGroupCode", ""));
                                helper.updateProgressDialog(mStatus + "..");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetCustomerGroup", params, new GetCustomerGroup(companycode, datetime))
                                        .execute();

                                Log.d("-----------  InvoiceHeader", "-----------");

                                List<PropertyInfo> para1 = new ArrayList<PropertyInfo>();
                                para1.clear();
                                para1.add(newPropertyInfo("CompanyCode", companycode));
                                para1.add(newPropertyInfo("CustomerCode", ""));
                                para1.add(newPropertyInfo("FromDate", ""));
                                para1.add(newPropertyInfo("ToDate", ""));
                                para1.add(newPropertyInfo("BalanceFlag", "0"));
                                helper.updateProgressDialog(mStatus + "...");

                                new SoapAccessTask(activity, webserviceUrl, "fncGetInvoiceHeader", para1, new GetInvoiceHeader(
                                        companycode, datetime)).execute();

                                Log.d("InvoiceHeaderWithoutDOSign", "--------");
                                List<PropertyInfo> para2 = new ArrayList<PropertyInfo>();
                                para2.clear();
                                para2.add(newPropertyInfo("CompanyCode", companycode));
                                para2.add(newPropertyInfo("CustomerCode", ""));
                                para2.add(newPropertyInfo("FromDate", ""));
                                para2.add(newPropertyInfo("ToDate", ""));
                                para2.add(newPropertyInfo("BalanceFlag", "0"));
                                helper.updateProgressDialog(mStatus + "...");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetInvoiceHeaderWithoutDOSign", para2, new GetInvoiceHeaderWithoutDOSign(
                                        companycode, datetime)).execute();

                                Log.d("-----------  Category", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("CategoryCode", ""));
                                helper.updateProgressDialog(mStatus + ".");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetCategory", params, new GetCategory(companycode, datetime)).execute();

                                Log.d("--------  SubCategory", "---------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("SubCategoryCode", ""));
                                helper.updateProgressDialog(mStatus + "..");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetSubCategory", params, new GetSubCategory(companycode, datetime)).execute();

                                Log.d("-----------  Product", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("ProductCode", ""));
                                params.add(newPropertyInfo("CategoryCode", ""));
                                params.add(newPropertyInfo("SubCategoryCode", ""));
                                helper.updateProgressDialog(mStatus + "...");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetProduct", params, new GetProduct(companycode, datetime)).execute();

                                Log.d("-----------  ProductBarCode", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("ProductCode", ""));
                                params.add(newPropertyInfo("Barcode", ""));
                                helper.updateProgressDialog(mStatus + ".");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetProductBarCode", params, new GetProductBarcode(companycode, datetime)).execute();

                                Log.d("-----------  Bank", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("BankCode", ""));
                                helper.updateProgressDialog(mStatus + "..");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetBank", params, new GetBank(companycode, datetime)).execute();

                                Log.d("-----------  Paymode", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("PaymodeCode", ""));
                                helper.updateProgressDialog(mStatus + "...");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetPaymode", params, new GetPayMode(companycode, datetime)).execute();

                                Log.d("-----------  Van", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("VanCode", ""));
                                helper.updateProgressDialog(mStatus + ".");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetVan", params, new GetVan(companycode, datetime)).execute();

                                Log.d("----------- SOHeader", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("CustomerCode", ""));
                                params.add(newPropertyInfo("FromDate", ""));
                                params.add(newPropertyInfo("ToDate", ""));
                                helper.updateProgressDialog(mStatus + "..");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetSOHeader", params, new GetSOHeader(companycode, datetime)).execute();

                                Log.d("----------- SRHeader", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("CustomerCode", ""));
                                params.add(newPropertyInfo("FromDate", ""));
                                params.add(newPropertyInfo("ToDate", ""));
                                helper.updateProgressDialog(mStatus + "..");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetSalesReturnHeader", params, new GetSRHeader(companycode, datetime)).execute();

			    /*if(mStatus!=null && !mStatus.isEmpty() && !mStatus.matches("Downloading from server")){
			        
			        Log.d("----------- ProductMainImage", "-----------");
			         hmParams.clear();
			         hmParams.put("CompanyCode", companycode);
			         hmParams.put("ModifyDate", "");
//			         hmParams.put("PageNo", "");
//			         hmParams.put("ProductCode", "");
//			         hmParams.put("CategoryCode", "Aroma1");
//			         hmParams.put("SubCategoryCode", "");
//			         hmParams.put("CustomerGroupCode", "");
			         helper.updateProgressDialog(mStatus+"...");
			         new OfflineXMLAccessTask(activity, webserviceUrl, "fncGetProductMainImageAll", hmParams, new GetProductMainImage(companycode, datetime)).execute(); 
//			         new OfflineXMLAccessTask(activity, webserviceUrl, "fncGetProductMainImageAll", companycode, false, new GetProductMainImage(companycode, datetime)).execute(); 
			         
			         Log.d("----------- ProductSubImages", "-----------");
			         hmParams.clear();
			         hmParams.put("CompanyCode", companycode);
			         hmParams.put("ModifyDate", "");
//			         hmParams.put("LocationCode", "HQ");
//			        hmParams.put("ProductCode", "");
//			        hmParams.put("CategoryCode", "");
//			        hmParams.put("SubCategoryCode", "");
			         helper.updateProgressDialog(mStatus+".");
			         new OfflineXMLAccessTask(activity, webserviceUrl, "fncGetProductSubImagesAll", hmParams, new GetProductSubImages(companycode, datetime)).execute();  
			         
			        }*/

                                Log.d("-----------  ProductStock", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                params.add(newPropertyInfo("LocationCode", ""));
                                params.add(newPropertyInfo("ProductCode", ""));
                                helper.updateProgressDialog(mStatus + "..");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetProductStock", params, new GetProductStock(companycode, datetime)).execute();

                                Log.d("-----------  MobileSettings", "-----------");
                                params.clear();
                                params.add(newPropertyInfo("CompanyCode", companycode));
                                helper.updateProgressDialog(mStatus + "...");
                                new SoapAccessTask(activity, webserviceUrl, "fncGetMobileSettings", params, new GetMobileSettings(companycode, datetime)).execute();

                                if(isDownloadingImage){
                                    Log.d("----------- ProductMainImage", "-----------");
                                    hmParams = new HashMap<String, String>();
                                    hmParams.clear();
                                    hmParams.put("CompanyCode", companycode);
                                    hmParams.put("ModifyDate", modifydate);
//       hmParams.put("PageNo", "");
//       hmParams.put("ProductCode", "");
//       hmParams.put("CategoryCode", "Aroma1");
//       hmParams.put("SubCategoryCode", "");
//       hmParams.put("CustomerGroupCode", "");
                                    helper.updateProgressDialog(mStatus + "...");
                                    new OfflineXMLAccessTask(activity, webserviceUrl, "fncGetProductMainImageAll", hmParams, new GetProductMainImage(companycode, datetime)).execute();

                                    Log.d("----------- ProductSubImages", "-----------");
                                    hmParams.clear();
                                    hmParams.put("CompanyCode", companycode);
                                    hmParams.put("ModifyDate", modifydate);
//       hmParams.put("LocationCode", "HQ");
//      hmParams.put("ProductCode", "");
//      hmParams.put("CategoryCode", "");
//      hmParams.put("SubCategoryCode", "");
                                    helper.updateProgressDialog(mStatus + ".");
                                    new OfflineXMLAccessTask(activity, webserviceUrl, "fncGetProductSubImagesAll", hmParams, new GetProductSubImagesAll(companycode, datetime)).execute();


                                }


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


    private void loadProductImage(final String currentdate, final String mdate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                try {

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            ArrayList<String> CompanyCodeArr = new ArrayList<String>();
                            CompanyCodeArr.clear();
                            CompanyCodeArr = OfflineDatabase.getCompanyCode();
                            Log.d("CompanyCodeArr size", "------" + CompanyCodeArr.size());

                            final String datetime = currentdate;
                            final String ModifyDate;
                            if (mdate != null) {
                                ModifyDate = mdate.split("\\ ")[0];
                            }else{
                                ModifyDate="";
                            }

                            Log.d("datetime", "------" + datetime);
                            Log.d("modifydate", "------" + ModifyDate);

                            for (int i = 0; i < CompanyCodeArr.size(); i++) {
                                final String companycode = CompanyCodeArr.get(i);
                                Log.d("companycode", "------->" + companycode);

                                Log.d("----------- ProductMainImage", "-----------");
                                hmParams = new HashMap<String, String>();
                                hmParams.clear();
                                hmParams.put("CompanyCode", companycode);
                                hmParams.put("ModifyDate", ModifyDate);
//			    hmParams.put("PageNo", "");
//			    hmParams.put("ProductCode", "");
//			    hmParams.put("CategoryCode", "Aroma1");
//			    hmParams.put("SubCategoryCode", "");
//			    hmParams.put("CustomerGroupCode", "");
                                helper.updateProgressDialog(mStatus + "...");
                                new OfflineXMLAccessTask(activity, webserviceUrl, "fncGetProductMainImageAll", hmParams, new GetProductMainImage(companycode, datetime)).execute();

                                Log.d("----------- ProductSubImages", "-----------");
                                hmParams.clear();
                                hmParams.put("CompanyCode", companycode);
                                hmParams.put("ModifyDate", ModifyDate);
//			    hmParams.put("LocationCode", "HQ");
//			  	hmParams.put("ProductCode", "");
//			  	hmParams.put("CategoryCode", "");
//			  	hmParams.put("SubCategoryCode", "");
                                helper.updateProgressDialog(mStatus + ".");
                                new OfflineXMLAccessTask(activity, webserviceUrl, "fncGetProductSubImagesAll", hmParams, new GetProductSubImagesAll(companycode, datetime)).execute();
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


    /**
     * ---------------------------------------------------------
     **/


    public void onError(final Exception error) {
        new Thread() {
            @Override
            public void run() {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        exception = error;


                        //	helper.dismissProgressDialog();
					/*if (error == ErrorType.NETWORK_UNAVAILABLE) {							
							helper.showLongToast(R.string.error_downloading_data_no_network_connection);
						} else   {
							helper.showLongToast(R.string.error_downloading_data);
						}*/
                        //	helper.showErrorDialog(Log.getStackTraceString(error));

                        //Exception e = null;


//						activity.finish();
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
