package com.winapp.fwms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.BuildConfig;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.helper.XMLParser;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineCustomerSync;
import com.winapp.offline.OfflineDataDownloader;
import com.winapp.offline.OfflineDataSynch;
import com.winapp.offline.OfflineDataUploader;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineDownloaderLogin;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.printer.UIHelper;
import com.winapp.sot.Company;
import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesProductWebService;
import com.winapp.sot.WebServiceClass;
import com.winapp.trackuser.AppLocationService;
import com.winapp.util.ErrorLog;
import com.winapp.util.Validate;
import com.winapp.util.XMLAccessTask;
import com.winapp.util.XMLAccessTask.CallbackInterface;
import com.winapp.util.XMLAccessTask.ErrorType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends Activity {

    LinearLayout offlineLayout;
    ImageView iLogo, img_online;
    Button btLogin;
    Spinner mySpinner;
    static Spinner cmpySpinner;
    EditText etuserName, etPassword;
    Intent callLanding;
    Set<String> wsvalue = new HashSet<String>();
    String username;
    String password;
    String res;
    CheckBox chkrememberme;
    ArrayList<String> al;
    String[] arr;
    ProgressDialog dialog = null;
    SharedPreferences sp;
    LinearLayout screen;
    String formName;
    private OfflineDataSynch offlineDataSynch;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Name = "USERNAME";
    public static final String Password = "PASSWORD";
    public static final String Location = "LOCATION";
    public static final String companyCode = "COMPANYCODE";
    public static final String companyName = "COMPANYNAME";
    public static final String CheckedState = "CHECKEDSTATE";

    ArrayList<String> loginArr = new ArrayList<String>();
    ArrayList<String> userGroupCodeArr = new ArrayList<String>();
    HashMap<String, String> loccode = new HashMap<String, String>();
    HashMap<String, String> cmpycodeHm = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> cmpySpnArr = new ArrayList<HashMap<String, String>>();
    String valid_url;
    LinearLayout spinnerLayout;
    ProgressBar progressBar;
    LinearLayout login_parent;
    String loginResult = "", isActive = "", CompanyCode = "", LastLoginLocation = "", userGroup = "", taxType = "", serverdate = "", cartonloose = "",
            LastLoginLocationName = "", CompanyName = "", LoginPhoneNo = "", serverdateTime = "";

    String locationvalue;
    String titlename;
    boolean logout;
    String sppos;
    ArrayList<String> locname_al;
    static ArrayList<String> companyArr = new ArrayList<String>();
    String jsonString = null, gnrlStngs = "";
    JSONObject jsonResponse;
    JSONArray jsonMainNode;
    SOTDatabase sotdb;
    int noofcompany;
    static final String COMPANYLOGO = "Logo";
    Bitmap bitmap;
    UIHelper helper;
    TextView pageTitle, version_code;

    private JSONArray userTrackingMasterArray = null;

    // @Offline
    private OfflineDataDownloader dataDownloader;
    private OfflineDownloaderLogin downloaderLogin;
    boolean checkOffline;
    String onlineMode, offlineDialogStatus;
    private OfflineSettingsManager offlinemanager;
    private OfflineCommon offlineCommon;
    OfflineDataUploader offlineUploader;
    //	ArrayList<SO> overduelistArray = new ArrayList<SO>();
    private String dialogStatus, mobileLoginPage = "", hosting_validation = "";
    boolean version_click = true;
    private ErrorLog errorLog;
    HashMap<String, String> hmBatch = new HashMap<String, String>();
    String login_gnrlStngs = "", companyHostingresult = "",mobileHaveOfflineMode="",login_dsplyStngs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_screen);

        offlineLayout = (LinearLayout) findViewById(R.id.offlineLayout);
        login_parent = (LinearLayout) findViewById(R.id.login_parent);
        img_online = (ImageView) findViewById(R.id.img_online);
        iLogo = (ImageView) findViewById(R.id.imageView1);
        etuserName = (EditText) findViewById(R.id.editText1);
        etPassword = (EditText) findViewById(R.id.editText2);
        chkrememberme = (CheckBox) findViewById(R.id.rem_checkbox);
        btLogin = (Button) findViewById(R.id.button1);
        mySpinner = (Spinner) findViewById(R.id.spinner1);
        cmpySpinner = (Spinner) findViewById(R.id.companySpinner);

        version_code = (TextView) findViewById(R.id.version_code);

        int versionCode = BuildConfig.VERSION_CODE;
        final String versionName = BuildConfig.VERSION_NAME;
        errorLog = new ErrorLog();
        SalesOrderSetGet.setClick("");
        version_code.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (version_click) {
                    version_code.setText("DevV" + versionName);
                    version_click = false;
                } else {
                    version_code.setText(R.string.sfa_version);
                    version_click = true;
                }

            }
        });

        helper = new UIHelper(LoginActivity.this);
        al = new ArrayList<String>();
        SOTDatabase.init(LoginActivity.this);
        SOTDatabase.getDatabase();
        sotdb = new SOTDatabase(LoginActivity.this);
        SalesOrderSetGet.setFlag("aaa");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // @Offline
        offlineCommon = new OfflineCommon(this);
        //

        String logotitlename = SalesOrderSetGet.getGeneralsetting();
        if (logotitlename.matches("SOT")) {
            iLogo.setImageResource(R.mipmap.logo_sot);
        }

        if (logotitlename.matches("WareHouse")) {
            iLogo.setImageResource(R.mipmap.logo);
        }

        onlineMode = OfflineDatabase.getOnlineMode();
        checkOffline = OfflineCommon.isConnected(this);
        OfflineDatabase.init(LoginActivity.this);
        offlinemanager = new OfflineSettingsManager(LoginActivity.this);
        //

        mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
//		mobileHaveOfflineMode="0";

        if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){

        }else{
            mobileHaveOfflineMode="";
        }

        FormSetterGetter.setGoodsReceive("");
        FormSetterGetter.setSalesOrder("");
        FormSetterGetter.setDeliveryOrder("");
        FormSetterGetter.setConsignment("");
        FormSetterGetter.setConsignmentReturn("");
        FormSetterGetter.setConsignmentStock("");
        FormSetterGetter.setConsignmentStockTake("");
        FormSetterGetter.setInvoice("");
        FormSetterGetter.setSalesReturn("");
        FormSetterGetter.setReceipts("");
        FormSetterGetter.setProductList("");
        FormSetterGetter.setProductAnalysis("");
        FormSetterGetter.setStockRequest("");
        FormSetterGetter.setCustomerList("");
        FormSetterGetter.setCatalog("");
        FormSetterGetter.setSettings("");
        FormSetterGetter.setStockTake("");
        FormSetterGetter.setStockAdjustment("");
        FormSetterGetter.setOverdue("");
        FormSetterGetter.setExpense("");
        FormSetterGetter.setTasks("");
        FormSetterGetter.setTransfer("");
        FormSetterGetter.setHidePrice("");
        FormSetterGetter.setMerchandise("");
        FormSetterGetter.setMerchandiseSchedule("");
        FormSetterGetter.setDeliveryVerification("");
        FormSetterGetter.setSettlement("");
        FormSetterGetter.setManualStock("");
        FormSetterGetter.setQuickTransfer("");

        FormSetterGetter.setOfflineMode("");

        FormSetterGetter.setEditPrice(false);
        FormSetterGetter.setProductAdd(false);
        FormSetterGetter.setCustomerAdd(false);
        FormSetterGetter.setEditInvoice(false);
        FormSetterGetter.setDeleteInvoice(false);
        FormSetterGetter.setDeleteReceipt(false);
        FormSetterGetter.setReceiptAll(false);
        FormSetterGetter.setEditTransactionDate(false);
        FormSetterGetter.setShowAllLocation(false);
        FormSetterGetter.setShowDashboard(false);

        MobileSettingsSetterGetter.setShowLogo("");
        MobileSettingsSetterGetter.setShowAddress1("");
        MobileSettingsSetterGetter.setShowAddress2("");
        MobileSettingsSetterGetter.setShowAddress3("");
        MobileSettingsSetterGetter.setShowCountryPostal("");
        MobileSettingsSetterGetter.setShowPhone("");
        MobileSettingsSetterGetter.setShowFax("");
        MobileSettingsSetterGetter.setShowEmail("");
        MobileSettingsSetterGetter.setShowTaxRegNo("");
        MobileSettingsSetterGetter.setShowBizRegNo("");
        MobileSettingsSetterGetter.setShowCustomerCode("");
        MobileSettingsSetterGetter.setShowCustomerName("");
        MobileSettingsSetterGetter.setShowCustomerAddress1("");
        MobileSettingsSetterGetter.setShowCustomerAddress2("");
        MobileSettingsSetterGetter.setShowCustomerAddress3("");
        MobileSettingsSetterGetter.setShowCustomerPhone("");
        MobileSettingsSetterGetter.setShowCustomerHP("");
        MobileSettingsSetterGetter.setShowCustomerEmail("");
        MobileSettingsSetterGetter.setShowCustomerTerms("");
        MobileSettingsSetterGetter.setShowProductFullName("");
        MobileSettingsSetterGetter.setShowTotalOutstanding("");
//        MobileSettingsSetterGetter.setShowFooter("");
        MobileSettingsSetterGetter.setShowBatchDetails("");
        MobileSettingsSetterGetter.setHaveDeliveryVerification("");
        MobileSettingsSetterGetter.setShowPriceOnDO("");
        SalesOrderSetGet.setHaveMerchandising("0");

        MobileSettingsSetterGetter.setShowUserPhoneNo("");
        MobileSettingsSetterGetter.setInvoiceDetailPrintUOM("");
        MobileSettingsSetterGetter.setInvoiceHeaderCaption("");
        MobileSettingsSetterGetter.setInvoiceTelCaption("");
        MobileSettingsSetterGetter.setInvoiceFaxCaption("");
        MobileSettingsSetterGetter.setInvoiceEmailCaption("");
        MobileSettingsSetterGetter.setInvoiceBizRegNoCaption("");
        MobileSettingsSetterGetter.setInvoiceTaxRegNoCaption("");
        MobileSettingsSetterGetter.setInvoiceSubTotalCaption("");
        MobileSettingsSetterGetter.setInvoiceTaxCaption("");
        MobileSettingsSetterGetter.setInvoiceNetTotalCaption("");
        MobileSettingsSetterGetter.setShowGST("");
        MobileSettingsSetterGetter.setCenterAlignCompanyName("");
        SalesOrderSetGet.setHostingValidation("");
        MobileSettingsSetterGetter.setPrintReceiptSummary_PrintInvoiceDetail("");
        MobileSettingsSetterGetter.setShowCreateTime("");
        MobileSettingsSetterGetter.setDecimalPoints("");

        companyArr.clear();
        cmpycodeHm.clear();
        cmpySpnArr.clear();
        loginArr.clear();
        userGroupCodeArr.clear();
        FWMSSettingsDatabase.init(LoginActivity.this);
        valid_url = FWMSSettingsDatabase.getUrl();
        offlineUploader = new OfflineDataUploader(valid_url, LoginActivity.this);
        new LoginWebService(valid_url);
        new GetUserPermission(valid_url);
        new SetStockInDetail(valid_url);
        new SalesProductWebService(valid_url);
        new WebServiceClass(valid_url);
        new DateWebservice(valid_url, LoginActivity.this);
        logout = LogOutSetGet.isActive();

        overridePendingTransition(R.anim.push_left_in, R.anim.push_up_out);

        SharedPreferences sp = getApplicationContext().getSharedPreferences(
                MyPREFERENCES, Context.MODE_PRIVATE);

        sotdb.truncateTables();

        mobileLoginPage = SalesOrderSetGet.getMobileloginpage();

        if (mobileLoginPage.matches("M")) {
            cmpySpinner.setVisibility(View.GONE);
            mySpinner.setVisibility(View.GONE);
        } else {
            cmpySpinner.setVisibility(View.VISIBLE);
            mySpinner.setVisibility(View.VISIBLE);
        }

        // @Offline
        Cursor crsr = OfflineDatabase.getDownloadStatusCount();
        if (crsr.getCount() > 0) {

            if (checkOffline == false) {
                offlineDataSynch = new OfflineDataSynch(LoginActivity.this);
//						offlineDataSynch.synchCustomerData();	
                offlineDataSynch.startSync(LoginActivity.this);

                Cursor masterCursor = OfflineDatabase.getUsermasterCount();
                Cursor companyCursor = OfflineDatabase.getCompanyCount();
//						Cursor dateCursor = OfflineDatabase.getDatetimeCount();
//						Cursor generalSettingsCursor = OfflineDatabase.getGeneralSettingsCount();

                OfflineCustomerSync offlineCustomerSync = new OfflineCustomerSync(LoginActivity.this);

                if (masterCursor.getCount() > 0) {

                } else {
                    Log.d("usermaster", "Update");

                    offlineCustomerSync.synchUsermaster(valid_url);
                }

                if (companyCursor.getCount() > 0) {

                } else {
                    Log.d("company", "Update");

                    offlineCustomerSync.synchcompany(valid_url); //added
                }

//						if(dateCursor.getCount() > 0){
//							
//						}else{
//							Log.d("datetime", "Update");
//										
//							offlineCustomerSync.synchdatetime(valid_url); //added
//						}	
//
//						if(generalSettingsCursor.getCount() > 0){
//							
//						}else{
//							Log.d("generalsettings", "Update");
//										
//							offlineCustomerSync.synchgeneralsettings(valid_url); //added
//						}
            }
        } else {
//					OfflineDatabase.store_downloadstatus();
            dataDownloader = new OfflineDataDownloader(LoginActivity.this, valid_url);
            dataDownloader.startDownload(true, "Downloading from server");
        }


        cmpySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            @SuppressWarnings("deprecation")
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int position, long id) {
                try {
                    int item = cmpySpinner.getSelectedItemPosition();
//					Log.d("CompanyCode item", "" + item);
                    String spinnerPositionCode = cmpySpnArr.get(position).get(
                            "CompanyCode");
                    String spinnerPositionName = cmpySpnArr.get(position).get(
                            "CompanyName");
                    String taxcode =cmpySpnArr.get(position).get(
                            "TaxCode");

                    Log.d("taxcode","value"+taxcode);

                    SupplierSetterGetter.setTaxCode(taxcode);

                    SupplierSetterGetter.setCompanyName(spinnerPositionName);
                    SupplierSetterGetter.setCompanyCode(spinnerPositionCode);

                    offlinemanager.setCompanyType(spinnerPositionCode);
                    OfflineDatabase.setcompany(LoginActivity.this);

                    spinnerLayout = new LinearLayout(LoginActivity.this);
                    spinnerLayout.setGravity(Gravity.CENTER);
                    addContentView(spinnerLayout, new LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT));
                    spinnerLayout.setBackgroundColor(Color
                            .parseColor("#80000000"));
                    enableViews(login_parent, false);
                    progressBar = new ProgressBar(LoginActivity.this);
                    progressBar.setProgress(android.R.attr.progressBarStyle);
                    progressBar.setIndeterminateDrawable(getResources()
                            .getDrawable(drawable.greenprogress));
                    spinnerLayout.addView(progressBar);
                    AsyncCallWSSP loctask = new AsyncCallWSSP();
                    loctask.execute(spinnerPositionCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        if (logout == true) {
            Editor editor = sp.edit();
            editor.clear();
            editor.commit();

        } else {
            if (sp.contains(Name)) {
                etuserName.setText(sp.getString(Name, null));

            }
            if (sp.contains(Password)) {
                etPassword.setText(sp.getString(Password, null));
            }
            if (sp.contains(CheckedState)) {
                if (sp.getBoolean(CheckedState, true) == true) {
                    chkrememberme.setChecked(true);
                } else {
                    chkrememberme.setChecked(false);
                }
            }
        }

        dialogStatus = checkInternetStatus();
        if (onlineMode.matches("True")) {
            Log.d("Login Online Mode", onlineMode);
            if (checkOffline == true) {

                if (dialogStatus.matches("true")) {
                    Log.d("CheckOffline Alert -->", "True");
                    OfflineDatabase.getGeneralSettings();

                    if (mobileLoginPage.matches("M")) {

                    } else {
                        companyAsyncCallWSSP task = new companyAsyncCallWSSP();
                        task.execute();
                    }

                } else {
                    Log.d("CheckOffline Alert -->", "False");
                    finish();
                }

            } else {
//				AsyncGeneralSettings asyncgs = new AsyncGeneralSettings();
//				asyncgs.execute();

                if (mobileLoginPage.matches("M")) {

                } else {
                    companyAsyncCallWSSP task = new companyAsyncCallWSSP();
                    task.execute();
                }
            }

        } else if (onlineMode.matches("False")) {

            Log.d("Login Online Mode", onlineMode);

            OfflineDatabase.getGeneralSettings();

            if (mobileLoginPage.matches("M")) {

            } else {
                companyAsyncCallWSSP task = new companyAsyncCallWSSP();
                task.execute();
            }
        }

        btLogin.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

				/*if (onlineMode.matches("True")) {

					if (checkOffline == false) {
						downloaderLogin = new OfflineDownloaderLogin(LoginActivity.this,valid_url);
						downloaderLogin.startDownload("Downloading from server");

					}
				}*/
                if (mobileLoginPage.matches("M")) {
                    loginbtnMobileClick();
                } else {
                    loginbtnClick();
                }

            }

        });

        img_online.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                checkOffline = OfflineCommon.isConnected(LoginActivity.this);
                if (checkOffline == false) {
                    offlineCommon.switchto_OnlineAlertDialog();
                    boolean dialogStatus = offlineCommon.showDialog();
                    Log.d("DialogStatus", "" + dialogStatus);

                    if (dialogStatus == true) {
                        OfflineDatabase.updateInternetMode("OfflineDialog", "false");
                        OfflineDatabase.updateInternetMode("OnlineDialog", "true");

                        offlineLayout.setVisibility(View.GONE);
                        img_online.setVisibility(View.GONE);
                    } else {
                        Log.d("False", "False");
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet", Toast.LENGTH_LONG).show();
                }
            }
        });

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

    public String showDialog() {

        return null;
    }

    private class companyAsyncCallWSSP extends AsyncTask<Void, Void, Void> {

        @Override
        @SuppressWarnings("deprecation")
        protected void onPreExecute() {
            cmpySpnArr.clear();
            spinnerLayout = new LinearLayout(LoginActivity.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            addContentView(spinnerLayout, new LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(login_parent, false);
            progressBar = new ProgressBar(LoginActivity.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            try {
                onlineMode = OfflineDatabase.getOnlineMode();
                if (onlineMode.matches("False")) {
                    cmpySpnArr = OfflineDatabase.getCompanyName();
                } else if (onlineMode.matches("True")) {
                    if (checkOffline == true) {
//						Log.d("DialogStatus", "" + dialogStatus);

                        if (dialogStatus.matches("true")) {
//							offlineLayout.setVisibility(View.VISIBLE);
//						      offlineLayout.setBackgroundResource(R.drawable.temp_offline_pattern_bg);
                            cmpySpnArr = OfflineDatabase.getCompanyName();
                        } else {
                            Log.d("CheckOffline Alert -->", "False");
                            finish();
                        }

                    } else {
                        Log.d("checkOffline Status -->", "False");
                        cmpySpnArr = SetStockInDetail.getCompany("fncGetCompany");

                    }

                }

            } catch (JSONException e) {

                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (cmpySpnArr.size() > 0) {
                    SharedPreferences sp = getApplicationContext()
                            .getSharedPreferences(MyPREFERENCES,
                                    Context.MODE_PRIVATE);
                    int spinnerPositionName = -1;

                    String myStringCode = sp.getString(companyCode, null);
                    String myStringName = sp.getString(companyName, null);

                    if (myStringCode != null && !myStringCode.equals("")
                            && myStringName != null && !myStringName.equals("")) {

                        cmpySpinner
                                .setAdapter(new cmpySpnAdapter(LoginActivity.this,
                                        R.layout.login_row, cmpySpnArr));

                        // Log.d("share ----->", "myStringCode"+ myStringCode);
                        // Log.d("myStringName ----->", "myStringName"+
                        // myStringName);

                        for (int c = 0; c < cmpySpnArr.size(); c++) {

                            String CompanyCode = cmpySpnArr.get(c).get(
                                    "CompanyCode");
                            String CompanyName = cmpySpnArr.get(c).get(
                                    "CompanyName");
                            String taxcode =cmpySpnArr.get(c).get(
                                    "TaxCode");
                            SupplierSetterGetter.setTaxCode(taxcode);
                            // Log.d("CompanyCode ----->", "itrate"+ CompanyCode);
                            // Log.d("CompanyName ----->", "itrate"+ CompanyName);

                            if (myStringCode.matches(CompanyCode)) {
                                spinnerPositionName = c;
//							Log.d("spinnerPosition ----->", "itrate" + c);
                            }

                        }

                        // Log.d("spinnerPosition---n-------->", "d-->"+
                        // spinnerPositionName);
                        cmpySpinner.setSelection(spinnerPositionName);

                    } else {

                        HashMap<String, String> tempArr = new HashMap<String, String>();
                        tempArr.put("CompanyCode", "0");
                        tempArr.put("CompanyName", "Select Company");
                        cmpySpnArr.add(tempArr);
                        // System.out.println("Before: " + cmpySpnArr);

                        Collections.sort(cmpySpnArr,
                                new Comparator<HashMap<String, String>>() {
                                    @Override
                                    public int compare(HashMap<String, String> one,
                                                       HashMap<String, String> two) {
                                        return one
                                                .values()
                                                .iterator()
                                                .next()
                                                .compareTo(
                                                        two.values().iterator()
                                                                .next());
                                    }
                                });

                        // System.out.println("After: " + cmpySpnArr);

                        // Log.d("cmpySpnArr", cmpySpnArr.toString());

                        cmpySpinner
                                .setAdapter(new cmpySpnAdapter(LoginActivity.this,
                                        R.layout.login_row, cmpySpnArr));
                    }

                    progressBar.setVisibility(View.GONE);
                    spinnerLayout.setVisibility(View.GONE);
                    enableViews(login_parent, true);

                }
            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            }
        }
    }

    private class AsyncCallWSSP extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            wsvalue.clear();
            loccode.clear();
            al.clear();
            al.add(0, "Select Location");
            companyArr.clear();
        }

        @Override
        protected Void doInBackground(String... params) {

            String spinnerCompanyText = params[0];

            try {

                // @Offline
                onlineMode = OfflineDatabase.getOnlineMode();
                if (onlineMode.matches("False")) {

                    loccode = OfflineDatabase.getLocation(spinnerCompanyText);
                    companyArr = OfflineDatabase.getCompanyDetail();

                    System.out.println("Using EntrySet");
                    for (Map.Entry<String, String> studentEntry : loccode
                            .entrySet()) {
                        System.out.println(studentEntry.getKey() + " :: "
                                + studentEntry.getValue());
                        al.add(studentEntry.getKey());
                    }
                } else if (onlineMode.matches("True")) {
                    if (checkOffline == true) {
                        Log.d("DialogStatus", "" + dialogStatus);

                        if (dialogStatus.matches("true")) {
//							offlineLayout.setVisibility(View.VISIBLE);
//						      offlineLayout.setBackgroundResource(R.drawable.temp_offline_pattern_bg);
                            loccode = OfflineDatabase.getLocation(spinnerCompanyText);
                            companyArr = OfflineDatabase.getCompanyDetail();

                            System.out.println("Using EntrySet");
                            for (Map.Entry<String, String> studentEntry : loccode
                                    .entrySet()) {
                                System.out.println(studentEntry.getKey() + " :: "
                                        + studentEntry.getValue());
                                al.add(studentEntry.getKey());
                            }
                        } else {
                            Log.d("CheckOffline Alert -->", "False");
                            finish();
                        }

                    } else {
                        Log.d("checkOffline Status -->", "False");
                        loccode = SetStockInDetail.getLocationcode(
                                "fncGetLocation", spinnerCompanyText);
                        companyArr = SalesProductWebService
                                .getCompany("fncGetCompany");
                        new XMLAccessTask(LoginActivity.this, valid_url,
                                "fncGetCompanyLogo", new GetCompanyLogo())
                                .execute();

                        System.out.println("Using EntrySet");
                        for (Map.Entry<String, String> studentEntry : loccode
                                .entrySet()) {
                            System.out.println(studentEntry.getKey() + " :: "
                                    + studentEntry.getValue());
                            al.add(studentEntry.getKey());
                        }
                    }
                }

            } catch (JSONException e) {

                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            }

            arr = al.toArray(new String[al.size()]);
            System.out.print("arr :" + arr.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                SharedPreferences sp = getApplicationContext()
                        .getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                if (sp.contains(Location)) {
//				Log.d("arr1", arr.toString());
                    mySpinner.setAdapter(new MyCustomAdapter(LoginActivity.this,
                            R.layout.row, arr));
                    String myString = sp.getString(Location, "HeadOffice");
//				Log.d("Location---n-------->", myString);

                    int spinnerPosition = Arrays.asList(arr).indexOf(myString);

//				Log.d("spinnerPosition---n-------->", "d-->" + spinnerPosition);

                    mySpinner.setSelection(spinnerPosition);

                } else {

                    arr = al.toArray(new String[al.size()]);
//				Log.d("arr2", arr.toString());
                    mySpinner.setAdapter(new MyCustomAdapter(LoginActivity.this,
                            R.layout.row, arr));

                }

                if (!companyArr.isEmpty()) {
//		Log.d("s onPostExecute companyArr", companyArr.toString());

                    taxType = companyArr.get(0);
                    SalesOrderSetGet.setCompanytax(taxType);
                    SalesOrderSetGet.setCompanytaxvalue(companyArr.get(1));
                    Company.setCompanyName(companyArr.get(2));
                    Company.setAddress1(companyArr.get(3));
                    Company.setAddress2(companyArr.get(4));
                    Company.setAddress3(companyArr.get(5));
                    Company.setCountry(companyArr.get(6));
                    Company.setZipCode(companyArr.get(7));
                    Company.setPhoneNo(companyArr.get(8));

                    Company.setFax(companyArr.get(9));
                    Company.setEmail(companyArr.get(10));
                    Company.setTaxRegNo(companyArr.get(11));
                    Company.setBusinessRegNo(companyArr.get(12));
                    Company.setAndroidVersion_SFA(companyArr.get(13));
                    Company.setAndroidVersion_SFA_CheckPlayStore(companyArr.get(14));
                    Log.d("TaxCodevALUE","-->"+companyArr.get(16));
                    Company.setTaxcode(companyArr.get(16));
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            }
            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(login_parent, true);

        }

    }

    private class AsyncCallCheckLogin extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            userTrackingMasterArray = null;
//            dialogStatus = checkInternetStatus();
//			overduelistArray.clear();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                // @Offline
                onlineMode = OfflineDatabase.getOnlineMode();
                if (onlineMode.matches("False")) {
                    String companycode = SupplierSetterGetter.getCompanyCode();
                    loginArr = OfflineDatabase.getUserMaster(username, password, companycode);
//                    serverdate = OfflineDatabase.getServerDate();
//                    SalesOrderSetGet.setCurrentdate(serverdate);
                    loginResult = loginArr.get(0);
                    userGroup = loginArr.get(1);
                    isActive = loginArr.get(2);
//                    userGroupCodeArr = OfflineDatabase.getUserPermission(companycode, userGroup);

//					 Log.d("GetCompnycode","-->"+SupplierSetterGetter.getCompanyCode());
//				     Log.d("GetLocationcode","-->"+SalesOrderSetGet.getLocationcode());
//                    String companyCode = SupplierSetterGetter.getCompanyCode();
//                    String locationCode = SalesOrderSetGet.getLocationcode();
//
//                    OfflineDatabase.getGeneralSettings();
//                    OfflineDatabase.setMobileSettings(companyCode);
//                    hmBatch = OfflineDatabase.getLocationHaveBatchOffline(locationCode);
//
////					OfflineDatabase.init(LoginActivity.this);
//                    String companyLogo = OfflineDatabase.getCompanyLogo();
////					Log.d("Login companyLogo", "companyLogo" + companyLogo);
//
//                    BitmapFactory.Options o = new BitmapFactory.Options();
//                    o.inSampleSize = 2;
//                    byte[] encodeByte = Base64.decode(companyLogo,
//                            Base64.DEFAULT);
//                    bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
//                            encodeByte.length, o);
//
//                    LogOutSetGet.setBitmap(bitmap);

                } else if (onlineMode.matches("True")) {
//                    if (checkOffline == true) {
//                        Log.d("DialogStatus", "" + dialogStatus);
//
//                        if (dialogStatus.matches("true")) {
//                            Log.d("CheckOffline Alert -->", "True");
////							offlineLayout.setVisibility(View.VISIBLE);
////						      offlineLayout.setBackgroundResource(R.drawable.temp_offline_pattern_bg);
//                            String companycode = SupplierSetterGetter.getCompanyCode();
//                            loginArr = OfflineDatabase
//                                    .getUserMaster(username, password, companycode);
//                            serverdate = OfflineDatabase.getServerDate();
//                            SalesOrderSetGet.setCurrentdate(serverdate);
//                            loginResult = loginArr.get(0);
//                            userGroup = loginArr.get(1);
//                            isActive = loginArr.get(2);
//                            userGroupCodeArr = OfflineDatabase
//                                    .getUserPermission(companycode, userGroup);
//
////							 Log.d("GetCompnycode","-->"+SupplierSetterGetter.getCompanyCode());
////						     Log.d("GetLocationcode","-->"+SalesOrderSetGet.getLocationcode());
//                            String companyCode = SupplierSetterGetter.getCompanyCode();
//                            String locationCode = SalesOrderSetGet.getLocationcode();
//
//                            OfflineDatabase.getGeneralSettings();
//                            OfflineDatabase.setMobileSettings(companyCode);
//                            hmBatch = OfflineDatabase.getLocationHaveBatchOffline(locationCode);
//
////							OfflineDatabase.init(LoginActivity.this);
//                            String companyLogo = OfflineDatabase.getCompanyLogo();
////							Log.d("Login companyLogo", "companyLogo" + companyLogo);
//
//                            BitmapFactory.Options o = new BitmapFactory.Options();
//                            o.inSampleSize = 2;
//                            byte[] encodeByte = Base64.decode(companyLogo,
//                                    Base64.DEFAULT);
//                            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
//                                    encodeByte.length, o);
//
//                            LogOutSetGet.setBitmap(bitmap);
//                        } else {
////							Log.d("CheckOffline Alert -->", "False");
//                            finish();
//                        }
//
//                    } else {
                        //
//                        String companyCode = SupplierSetterGetter.getCompanyCode();
//                        String locationCode = SalesOrderSetGet.getLocationcode();

                        loginArr = LoginWebService.loginWS(username, password, "fncCheckUserNameAndPassword");
//                        serverdate = DateWebservice
//                                .getDateService("fncGetServerDate");
//                        serverdateTime = DateWebservice
//                                .getDateService("fncGetServerDateTime");
//                        SalesOrderSetGet.setCurrentdate(serverdate);
                        loginResult = loginArr.get(0);
                        userGroup = loginArr.get(1);
                        isActive = loginArr.get(2);
                        LoginPhoneNo = loginArr.get(3);
//                        userGroupCodeArr = GetUserPermission.userGroupCode(userGroup, companyCode, "fncGetUserPermission");

//                        login_gnrlStngs = ValidateWebService.generalSettingsService(valid_url, "fncGetGeneralSettings", companyCode);

//                        HashMap<String, String> hmvalue = new HashMap<String, String>();
//                        hmvalue.put("CompanyCode", companyCode);
////				    overduelistArray = SalesProductWebService.getInvoiceOverdue(hmvalue, "fncGetInvoiceHeaderByTerms");
//                        //TrackUserMaster
//                        hmvalue.put("UserName", username);
//                        userTrackingMasterArray = WebServiceClass.parameterWebservice(hmvalue, "fncGetUserTrackingMaster");
//
//                        GetUserPermission.mobileSettings("fncGetMobileSettings");
//                        hmBatch = GetUserPermission.getLocationHaveBatch("fncGetLocation", companyCode, locationCode);
//                        GetUserPermission.getRouteMaster("fncGetRouteMaster", companyCode, username, serverdate);
//
//                        hosting_validation = SalesOrderSetGet.getHostingValidation();
//                        if (hosting_validation != null && !hosting_validation.isEmpty()) {
//
//                        } else {
//                            hosting_validation = "";
//                        }
//
////					hosting_validation="0";
//
//                        if (hosting_validation.matches("1")) {
//                            companyHostingresult = GetUserPermission.getCompanyHostingDetails(companyCode, "fncGetCompanyHostingDetails");
//                        } else {
//                            Log.d("hosting_validation", "0");
//                        }
                    }
//                }

            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            Log.d("loginResult", "rr " + loginResult);
//            if (hosting_validation.matches("1")) {
//                if (!companyHostingresult.matches("")) {
//
//                    JSONObject jsonResponse;
//                    try {
//                        jsonResponse = new JSONObject(companyHostingresult);
//                        JSONArray jsonMainNode = jsonResponse.optJSONArray("CompanyHostingDetails");
//                        int lengthJsonArr = jsonMainNode.length();
//                        if(lengthJsonArr>0){
//                            for (int i = 0; i < lengthJsonArr; i++) {
//                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
//                                String HostingRenewalDate = jsonChildNode.optString(
//                                        "HostingRenewalDate").toString();
//                                String HostingValidityInMonths = jsonChildNode.optString(
//                                        "HostingValidityInMonths").toString();
//                                String AlertBeforeNoOfDays = jsonChildNode.optString(
//                                        "AlertBeforeNoOfDays").toString();
//
////									 String HostingRenewalDate = "14/09/2017";
////									 String HostingValidityInMonths = "1";
////									 String AlertBeforeNoOfDays = "15";
//
//                                String MeggaseBeforeExpiry = jsonChildNode.optString(
//                                        "MeggaseBeforeExpiry").toString();
//                                String MessageAfterExpiry = jsonChildNode.optString(
//                                        "MessageAfterExpiry").toString();
//
//                                int hostinginmonths = 0, AlertBeforeExpiry = 0;
//                                if (HostingValidityInMonths != null && !HostingValidityInMonths.isEmpty()) {
//                                    hostinginmonths = Integer.parseInt(HostingValidityInMonths);
//                                    hostinginmonths = hostinginmonths * 30;
//                                }
//
//                                if (AlertBeforeNoOfDays != null && !AlertBeforeNoOfDays.isEmpty()) {
//                                    AlertBeforeExpiry = Integer.parseInt(AlertBeforeNoOfDays);
//                                }
//                                Log.d("Server date", "" + serverdate);
//                                String RenewalDate = HostingRenewalDate.split("// ")[0];
//                                Log.d("HostingRenewalDate", "" + RenewalDate);
////                                     getCalculatedDate(HostingRenewalDate,"",HostingValidityInMonths);
//
//                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                                Calendar c = Calendar.getInstance();
//                                c.setTime(sdf.parse(RenewalDate));
//                                c.add(Calendar.DATE, hostinginmonths);  // number of days to add
//                                String ExpiryDate = sdf.format(c.getTime());  // dt is now the new date
//                                Log.d("Hosting expiry date", "" + ExpiryDate);
//
//                                Calendar cal = Calendar.getInstance();
//                                cal.setTime(sdf.parse(ExpiryDate));
//                                cal.add(Calendar.DATE, -AlertBeforeExpiry);  // number of days to add
//                                String beforeExpiryDate = sdf.format(cal.getTime());  // dt is now the new date
//                                Log.d("Before expiry date", "" + beforeExpiryDate);
//
//                                Date date1 = sdf.parse(serverdate);
//                                Date date2 = sdf.parse(ExpiryDate);
//                                long diff = date2.getTime() - date1.getTime();
//                                long daysremaining = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
//                                System.out.println("Days: " + daysremaining);
//
//                                if (beforeExpiryDate.compareTo(serverdate) > 0) {
//                                    System.out.println("Date1 is after Date2");
//
//                                    if (ExpiryDate.compareTo(serverdate) > 0) {
//                                        Log.d("Alert before Expiry", "" + MeggaseBeforeExpiry);
//
//                                        if (AlertBeforeExpiry >= daysremaining) {
//                                            alertBeforeExpiry(ExpiryDate, "" + daysremaining, MeggaseBeforeExpiry);
//                                        } else {
//                                            loginpost(login_gnrlStngs, hmBatch);
//                                        }
//                                    } else {
//                                        loginpost(login_gnrlStngs, hmBatch);
//                                    }
//
//                                } else if (beforeExpiryDate.compareTo(serverdate) < 0) {
//
//                                    System.out.println("Date1 is before Date2");
//                                /*if (ExpiryDate.compareTo(serverdate) < 0) {
//									Log.d("Alert before Expiry", "" + MeggaseBeforeExpiry);
//									alertBeforeExpiry(ExpiryDate, "0", MeggaseBeforeExpiry);
//								}else*/
//                                    if (ExpiryDate.compareTo(serverdate) > 0) {
//                                        Log.d("Alert after Expiry", "" + MessageAfterExpiry);
//                                        alertAfterExpiry(ExpiryDate, MessageAfterExpiry);
//                                    } else {
//                                        loginpost(login_gnrlStngs, hmBatch);
//                                    }
//
//                                } else {
//                                    System.out.println("Date1 is equal to Date2");
//                                    loginpost(login_gnrlStngs, hmBatch);
//                                }
//
//                            }
//                        }else{
//                            loginpost(login_gnrlStngs, hmBatch);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.d("CompanyHostingDetails", "null");
//                    loginpost(login_gnrlStngs, hmBatch);
//                }
//            } else {
//                loginpost(login_gnrlStngs, hmBatch);
//            }

            printResult(loginResult, isActive);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    private class AsyncCallWS extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            userTrackingMasterArray = null;
            dialogStatus = checkInternetStatus();
//			overduelistArray.clear();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {


                // @Offline
                onlineMode = OfflineDatabase.getOnlineMode();
                if (onlineMode.matches("False")) {
                    String companycode = SupplierSetterGetter.getCompanyCode();
//                    loginArr = OfflineDatabase.getUserMaster(username, password, companycode);
                    serverdate = OfflineDatabase.getServerDate();
                    SalesOrderSetGet.setCurrentdate(serverdate);
//                    loginResult = loginArr.get(0);
//                    userGroup = loginArr.get(1);
//                    isActive = loginArr.get(2);
                    userGroupCodeArr = OfflineDatabase.getUserPermission(companycode, userGroup);

//					 Log.d("GetCompnycode","-->"+SupplierSetterGetter.getCompanyCode());
//				     Log.d("GetLocationcode","-->"+SalesOrderSetGet.getLocationcode());
                    String companyCode = SupplierSetterGetter.getCompanyCode();
                    String locationCode = SalesOrderSetGet.getLocationcode();

                    OfflineDatabase.getGeneralSettings();
                    OfflineDatabase.setMobileSettings(companyCode);
                    hmBatch = OfflineDatabase.getLocationHaveBatchOffline(locationCode);

//					OfflineDatabase.init(LoginActivity.this);
                    String companyLogo = OfflineDatabase.getCompanyLogo();
//					Log.d("Login companyLogo", "companyLogo" + companyLogo);

                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inSampleSize = 2;
                    byte[] encodeByte = Base64.decode(companyLogo,
                            Base64.DEFAULT);
                    bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                            encodeByte.length, o);

                    LogOutSetGet.setBitmap(bitmap);

                } else if (onlineMode.matches("True")) {
//                    if (checkOffline == true) {
//                        Log.d("DialogStatus", "" + dialogStatus);
//
//                        if (dialogStatus.matches("true")) {
//                            Log.d("CheckOffline Alert -->", "True");
////							offlineLayout.setVisibility(View.VISIBLE);
////						      offlineLayout.setBackgroundResource(R.drawable.temp_offline_pattern_bg);
//                            String companycode = SupplierSetterGetter.getCompanyCode();
//                            loginArr = OfflineDatabase
//                                    .getUserMaster(username, password, companycode);
//                            serverdate = OfflineDatabase.getServerDate();
//                            SalesOrderSetGet.setCurrentdate(serverdate);
//                            loginResult = loginArr.get(0);
//                            userGroup = loginArr.get(1);
//                            isActive = loginArr.get(2);
//                            userGroupCodeArr = OfflineDatabase
//                                    .getUserPermission(companycode, userGroup);
//
////							 Log.d("GetCompnycode","-->"+SupplierSetterGetter.getCompanyCode());
////						     Log.d("GetLocationcode","-->"+SalesOrderSetGet.getLocationcode());
//                            String companyCode = SupplierSetterGetter.getCompanyCode();
//                            String locationCode = SalesOrderSetGet.getLocationcode();
//
//                            OfflineDatabase.getGeneralSettings();
//                            OfflineDatabase.setMobileSettings(companyCode);
//                            hmBatch = OfflineDatabase.getLocationHaveBatchOffline(locationCode);
//
////							OfflineDatabase.init(LoginActivity.this);
//                            String companyLogo = OfflineDatabase.getCompanyLogo();
////							Log.d("Login companyLogo", "companyLogo" + companyLogo);
//
//                            BitmapFactory.Options o = new BitmapFactory.Options();
//                            o.inSampleSize = 2;
//                            byte[] encodeByte = Base64.decode(companyLogo,
//                                    Base64.DEFAULT);
//                            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
//                                    encodeByte.length, o);
//
//                            LogOutSetGet.setBitmap(bitmap);
//                        } else {
////							Log.d("CheckOffline Alert -->", "False");
//                            finish();
//                        }
//
//                    } else {
                        //
                        String companyCode = SupplierSetterGetter.getCompanyCode();
                        String locationCode = SalesOrderSetGet.getLocationcode();

//                        loginArr = LoginWebService.loginWS(username, password, "fncCheckUserNameAndPassword");
                        serverdate = DateWebservice
                                .getDateService("fncGetServerDate");
                        serverdateTime = DateWebservice
                                .getDateService("fncGetServerDateTime");
                        SalesOrderSetGet.setCurrentdate(serverdate);
//                        loginResult = loginArr.get(0);
//                        userGroup = loginArr.get(1);
//                        isActive = loginArr.get(2);
//                        LoginPhoneNo = loginArr.get(3);
                        userGroupCodeArr = GetUserPermission.userGroupCode(userGroup, companyCode, "fncGetUserPermission");

                        login_gnrlStngs = ValidateWebService.generalSettingsService(valid_url, "fncGetGeneralSettings", companyCode);

                        login_dsplyStngs = ValidateWebService.generalService(valid_url, "fncGetTranDisplaySettings", companyCode);
                        HashMap<String, String> hmvalue = new HashMap<String, String>();
                        hmvalue.put("CompanyCode", companyCode);
//				    overduelistArray = SalesProductWebService.getInvoiceOverdue(hmvalue, "fncGetInvoiceHeaderByTerms");
                        //TrackUserMaster
                        hmvalue.put("UserName", username);
                        userTrackingMasterArray = WebServiceClass.parameterWebservice(hmvalue, "fncGetUserTrackingMaster");

                        GetUserPermission.mobileSettings("fncGetMobileSettings");
                        hmBatch = GetUserPermission.getLocationHaveBatch("fncGetLocation", companyCode, locationCode);
                         GetUserPermission.getRouteMaster("fncGetRouteMaster", companyCode, username, serverdate);

                        hosting_validation = SalesOrderSetGet.getHostingValidation();
                        if (hosting_validation != null && !hosting_validation.isEmpty()) {

                        } else {
                            hosting_validation = "";
                        }

//					hosting_validation="0";

                        if (hosting_validation.matches("1")) {
                            companyHostingresult = GetUserPermission.getCompanyHostingDetails(companyCode, "fncGetCompanyHostingDetails");
                        } else {
                            Log.d("hosting_validation", "0");
                        }
//                    }
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("loginResult", "rr " + loginResult);
            if (hosting_validation.matches("1")) {
                if (!companyHostingresult.matches("")) {

                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(companyHostingresult);
                        JSONArray jsonMainNode = jsonResponse.optJSONArray("CompanyHostingDetails");
                        int lengthJsonArr = jsonMainNode.length();
                        if(lengthJsonArr>0){
                        for (int i = 0; i < lengthJsonArr; i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                            String HostingRenewalDate = jsonChildNode.optString(
                                    "HostingRenewalDate").toString();
                            String HostingValidityInMonths = jsonChildNode.optString(
                                    "HostingValidityInMonths").toString();
                            String AlertBeforeNoOfDays = jsonChildNode.optString(
                                    "AlertBeforeNoOfDays").toString();

//									 String HostingRenewalDate = "14/09/2017";
//									 String HostingValidityInMonths = "1";
//									 String AlertBeforeNoOfDays = "15";

                            String MeggaseBeforeExpiry = jsonChildNode.optString(
                                    "MeggaseBeforeExpiry").toString();
                            String MessageAfterExpiry = jsonChildNode.optString(
                                    "MessageAfterExpiry").toString();

                            int hostinginmonths = 0, AlertBeforeExpiry = 0;
                            if (HostingValidityInMonths != null && !HostingValidityInMonths.isEmpty()) {
                                hostinginmonths = Integer.parseInt(HostingValidityInMonths);
                                hostinginmonths = hostinginmonths * 30;
                            }

                            if (AlertBeforeNoOfDays != null && !AlertBeforeNoOfDays.isEmpty()) {
                                AlertBeforeExpiry = Integer.parseInt(AlertBeforeNoOfDays);
                            }



                            Log.d("ServerDate", "" + serverdate);
//                            if(HostingRenewalDate.contains("/")) {
                                String RenewalDate = HostingRenewalDate.split("//")[0];
                                Log.d("HostingRenewalDate", "" + RenewalDate);
//                                     getCalculatedDate(HostingRenewalDate,"",HostingValidityInMonths);

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar c = Calendar.getInstance();
                                c.setTime(sdf.parse(RenewalDate));
                                c.add(Calendar.DATE, hostinginmonths);  // number of days to add
                                String ExpiryDate = sdf.format(c.getTime());  // dt is now the new date
                                Log.d("Hosting expiry date", "" + ExpiryDate);

                                Calendar cal = Calendar.getInstance();
                                cal.setTime(sdf.parse(ExpiryDate));
                                cal.add(Calendar.DATE, -AlertBeforeExpiry);  // number of days to add
                                String beforeExpiryDate = sdf.format(cal.getTime());  // dt is now the new date
                                Log.d("Before expiry date", "" + beforeExpiryDate);

                                Date date1 = sdf.parse(serverdate);
                                Date date2 = sdf.parse(ExpiryDate);
                                long diff = date2.getTime() - date1.getTime();
                                long daysremaining = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                                System.out.println("Days: " + daysremaining);

                            if (beforeExpiryDate.compareTo(serverdate) > 0) {
                                System.out.println("Date1 is after Date2");

                                if (ExpiryDate.compareTo(serverdate) > 0) {
                                    Log.d("Alert before Expiry", "" + MeggaseBeforeExpiry);

                                    if (AlertBeforeExpiry >= daysremaining) {
                                        alertBeforeExpiry(ExpiryDate, "" + daysremaining, MeggaseBeforeExpiry);
                                    } else {
                                        loginpost(login_gnrlStngs, hmBatch);
                                        loginpost(login_dsplyStngs,hmBatch);
                                    }
                                } else {
                                    loginpost(login_gnrlStngs, hmBatch);
                                    loginpost(login_dsplyStngs,hmBatch);
                                }

                            } else if (beforeExpiryDate.compareTo(serverdate) < 0) {

                                System.out.println("Date1 is before Date2");
                                /*if (ExpiryDate.compareTo(serverdate) < 0) {
									Log.d("Alert before Expiry", "" + MeggaseBeforeExpiry);
									alertBeforeExpiry(ExpiryDate, "0", MeggaseBeforeExpiry);
								}else*/
                                if (ExpiryDate.compareTo(serverdate) > 0) {
                                    Log.d("Alert after Expiry", "" + MessageAfterExpiry);
                                    alertAfterExpiry(ExpiryDate, MessageAfterExpiry);
                                } else {
                                    loginpost(login_gnrlStngs, hmBatch);
                                    loginpost(login_dsplyStngs,hmBatch);
                                }

                            } else {
                                System.out.println("Date1 is equal to Date2");
                                loginpost(login_gnrlStngs, hmBatch);
                                loginpost(login_dsplyStngs,hmBatch);
                            }

                        }
                    }else{
                            loginpost(login_gnrlStngs, hmBatch);
                            loginpost(login_dsplyStngs,hmBatch);
                    }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("CompanyHostingDetails", "null");
                    loginpost(login_gnrlStngs, hmBatch);
                    loginpost(login_dsplyStngs,hmBatch);
                }
            } else {
                loginpost(login_gnrlStngs, hmBatch);
                loginpost(login_dsplyStngs,hmBatch);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void loginpost(String login_gnrlStngs, HashMap<String, String> hmBatch) {
        String dbUsertracking="";
        try {
            if (login_gnrlStngs != null && !login_gnrlStngs.isEmpty()) {
                if (login_gnrlStngs.matches("S")) {
                    LogOutSetGet.setApplicationType("S");
                    SalesOrderSetGet.setGeneralsetting("SOT");
                } else if (login_gnrlStngs.matches("W")) {
                    LogOutSetGet.setApplicationType("W");
                    SalesOrderSetGet.setGeneralsetting("WareHouse");
                }
            } else {
                LogOutSetGet.setApplicationType("S");
                SalesOrderSetGet.setGeneralsetting("SOT");
            }
// Set Server
            SalesOrderSetGet.setServerDate(serverdate);

            SalesOrderSetGet.setSaleorderdate(serverdate);
            SalesOrderSetGet.setServerDateTime(serverdateTime);
            SalesOrderSetGet.setLoginPhoneNo(LoginPhoneNo);
//			Log.d("OverdueArray Size", "-->" + overduelistArray.size());
//			if (overduelistArray.size() > 0) {
            offlinemanager.setOverdue("true");
//			} else {
//				offlinemanager.setOverdue("false");
//			}

            SalesOrderSetGet.setHaveBatchOnStockIn(hmBatch.get("HaveBatchOnStockIn"));
            SalesOrderSetGet.setHaveBatchOnTransfer(hmBatch.get("HaveBatchOnTransfer"));
            SalesOrderSetGet.setHaveBatchOnStockAdjustment(hmBatch.get("HaveBatchOnStockAdjustment"));
            SalesOrderSetGet.setHaveBatchOnStockOut(hmBatch.get("HaveBatchOnStockOut"));
            SalesOrderSetGet.setHaveBatchOnConsignmentOut(hmBatch.get("HaveBatchOnConsignmentOut"));

            Log.d("HaveBatchOnStockIn", "-->" + SalesOrderSetGet.getHaveBatchOnStockIn());
            Log.d("HaveBatchOnTransfer", "-->" + SalesOrderSetGet.getHaveBatchOnTransfer());
            Log.d("HaveBatchOnStockOut", "-->" + SalesOrderSetGet.getHaveBatchOnStockOut());
            Log.d("HaveBatchOnConsignmentOut", "-->" + SalesOrderSetGet.getHaveBatchOnConsignmentOut());
            Log.d("ROUTE PERMISSION ", "-->" + SalesOrderSetGet.getRoutepermission());
//            printResult(loginResult, isActive);

            String
            onlineMode = OfflineDatabase.getOnlineMode();
            if (onlineMode.matches("True")) {
                if (checkOffline == true) {

                } else {
                    String haveTracking = SalesOrderSetGet.getHaveTracking();

                    boolean isService = isMyServiceRunning(AppLocationService.class);
                    if (isService) {
                        stopService(new Intent(LoginActivity.this, AppLocationService.class));
                    }

                    if (haveTracking != null && !haveTracking.isEmpty() && haveTracking.matches("1")) {

                        if (userTrackingMasterArray != null && userTrackingMasterArray.length() > 0) {
                            dbUsertracking = SOTDatabase.store_usertracking(userTrackingMasterArray);
                            Log.d("dbUsertracking", "-> " + dbUsertracking);
//			        		startService(new Intent(LoginActivity.this, AppLocationService.class));
                        }
                    }
                }
            }


            Log.d("dbUsertracking", "-> " + dbUsertracking);
            checkForUpdate(dbUsertracking);
            userGroup();
            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(login_parent, true);
        } catch (Exception e) {
            errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
        }
    }


    public void alertBeforeExpiry(String expirydate, String remaining_days, String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(false);
        alertDialog
                .setMessage("License will expiry on " + expirydate + "\n" + "\n"
                        + "Only " + remaining_days + " days remaining" + "\n" + "\n"
                        + message);
        alertDialog.setIcon(R.mipmap.ic_save);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loginpost(login_gnrlStngs, hmBatch);
                        loginpost(login_dsplyStngs,hmBatch);
                    }
                });
        alertDialog.show();
    }

    public void alertAfterExpiry(String expirydate, String message) {
        progressBar.setVisibility(View.GONE);
        spinnerLayout.setVisibility(View.GONE);
        enableViews(login_parent, true);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(false);
        alertDialog
                .setMessage("License is expired on " + expirydate + "\n" + "\n"
                        + message);

        alertDialog.setIcon(R.mipmap.ic_save);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LoginActivity.this, "License is expired. So cannot able to login", Toast.LENGTH_LONG).show();
                    }
                });

        alertDialog.show();
    }

    public void printResult(String res, String isAct) {

        String dbUsertracking = "";
        try {
            if (res.matches("True") && isAct.matches("True")) {
                SharedPreferences sp = getApplicationContext()
                        .getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                Editor editor = sp.edit();

                String spinnerText = mySpinner.getSelectedItem().toString();
                editor.putString(Location, spinnerText);

                editor.putString(companyCode, SupplierSetterGetter.getCompanyCode());
                editor.putString(companyName, SupplierSetterGetter.getCompanyName());

                if (chkrememberme.isChecked()) {
                    editor.putBoolean(CheckedState, true);
                    editor.putString(Name, username);
                    editor.putString(Password, password);
                    editor.putString(companyCode,
                            SupplierSetterGetter.getCompanyCode());
                    editor.putString(companyName,
                            SupplierSetterGetter.getCompanyName());
                    editor.putString(Location, spinnerText);
                    editor.commit();
                } else {
                    editor.putString(Name, null);
                    editor.putString(Password, null);
                    editor.putString(companyCode, null);
                    editor.putString(companyName, null);
                    editor.putString(Location, null);
                    editor.putBoolean(CheckedState, false);
                    editor.commit();
                }

//                onlineMode = OfflineDatabase.getOnlineMode();
//                if (onlineMode.matches("True")) {
//                    if (checkOffline == true) {
//
//                    } else {
//                        String haveTracking = SalesOrderSetGet.getHaveTracking();
//
//                        boolean isService = isMyServiceRunning(AppLocationService.class);
//                        if (isService) {
//                            stopService(new Intent(LoginActivity.this, AppLocationService.class));
//                        }
//
//                        if (haveTracking != null && !haveTracking.isEmpty() && haveTracking.matches("1")) {
//
//                            if (userTrackingMasterArray != null && userTrackingMasterArray.length() > 0) {
//                                dbUsertracking = SOTDatabase.store_usertracking(userTrackingMasterArray);
//                                Log.d("dbUsertracking", "-> " + dbUsertracking);
////			        		startService(new Intent(LoginActivity.this, AppLocationService.class));
//                            }
//                        }
//                    }
//                }
//
//
//                Log.d("dbUsertracking", "-> " + dbUsertracking);

//                checkForUpdate(dbUsertracking);
                if(mobileHaveOfflineMode.matches("1")) {
                    downloaderLogin = new OfflineDownloaderLogin(LoginActivity.this, valid_url);
                    downloaderLogin.startDownload("Downloading from server");
                }

                new AsyncCallWS().execute();
            } else if (res.matches("False") && isAct.matches("0")) {
                Toast.makeText(LoginActivity.this, "Invalid Username and Password",
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(login_parent, true);
            } else if (res.matches("True") && isAct.matches("0")) {
                Toast.makeText(LoginActivity.this,
                        "User Status is inactive.Login Failed", Toast.LENGTH_SHORT)
                        .show();
                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(login_parent, true);
            } else {
                Toast.makeText(LoginActivity.this,
                        "Login Failed", Toast.LENGTH_SHORT)
                        .show();
                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(login_parent, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
        }
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.login_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.locationspinner);
            TextView code = (TextView) row.findViewById(R.id.code);
            code.setVisibility(View.GONE);
            label.setText(arr[position]);
            locationvalue = arr[position];
            ImageView icon = (ImageView) row.findViewById(R.id.spinnericon);
            if (position == mySpinner.getSelectedItemPosition()) {
                icon.setImageResource(R.mipmap.spinnerarrow);
            } else {
                icon.setImageResource(R.mipmap.map);
            }

            return row;
        }
    }

    public class cmpySpnAdapter extends BaseAdapter {

        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public cmpySpnAdapter(Context context, int textViewResourceId,
                              ArrayList<HashMap<String, String>> cmpySpnArr) {
            data = cmpySpnArr;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.login_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.locationspinner);
            TextView code = (TextView) row.findViewById(R.id.code);
            ImageView icon = (ImageView) row.findViewById(R.id.spinnericon);
            if (position == cmpySpinner.getSelectedItemPosition()) {
                icon.setImageResource(R.mipmap.spinnerarrow);
            } else {
                icon.setImageResource(R.mipmap.ic_company);
            }

            code.setVisibility(View.GONE);
            code.setText(data.get(position).get("CompanyCode"));
            label.setText(data.get(position).get("CompanyName"));

            return row;
        }

    }


    public void getlocation() {
        String sp = mySpinner.getSelectedItem().toString();
        // Location Name
        SupplierSetterGetter.setLocationcode(sp);
        String locationcode = loccode.get(sp);
        SalesOrderSetGet.setLocationcode(locationcode);
        SalesOrderSetGet.setLocationname(sp);

        String lg = etuserName.getText().toString();
        SupplierSetterGetter.setUsername(lg);
        String lgget = SupplierSetterGetter.getUsername();
        System.out.println("Username" + lgget);
//		Log.d("Username", lgget);

        /** Set Location **/
        SalesOrderSetGet.setFrom_location(al);

//		Log.d("loac------------------------->","l" + SalesOrderSetGet.getFrom_location());

        SupplierSetterGetter.setLoc_code_name(loccode);
    }

    public void getMobilelocation() {

        String sp_loccode = SalesOrderSetGet.getLocationcode();
        String sp_locname = SalesOrderSetGet.getLocationname();

        SupplierSetterGetter.setLocationcode(sp_locname);

//		  Log.d("loc SupplierSetterGetter.getLocationcode()-->", "--"+ SupplierSetterGetter.getLocationcode());
//		  Log.d("loc SalesOrderSetGet.getLocationcode()---->", "--"+ SalesOrderSetGet.getLocationcode());

        String lg = etuserName.getText().toString();
        SupplierSetterGetter.setUsername(lg);
        String lgget = SupplierSetterGetter.getUsername();
//		  Log.d("Username", "-->" + lgget);

        /** Set Location **/
        SalesOrderSetGet.setFrom_location(al);
//		  Log.d("loac------------------------->","l" + SalesOrderSetGet.getFrom_location());
        SupplierSetterGetter.setLoc_code_name(loccode);
    }

    public void userGroup() {
        for (int i = 0; i < userGroupCodeArr.size(); i++) {
            formName = userGroupCodeArr.get(i);
            switch (formName) {

                case "Tasks":
                    FormSetterGetter.setTasks(formName);
                    Log.d("Tasks", FormSetterGetter.getTasks());
                    break;

                case "Add Invoice":
                    FormSetterGetter.setAddInvoice(formName);
                    Log.d("Addinvoice", FormSetterGetter.getAddInvoice());
                    break;

                case "Overdue Invoices":
                    FormSetterGetter.setOverdue(formName);
                    Log.d("Overdue", FormSetterGetter.getOverdue());
                    break;

                case "Expense":
                    FormSetterGetter.setExpense(formName);
                    Log.d("Expense", FormSetterGetter.getExpense());
                    break;
                case "Route":
                    FormSetterGetter.setRouteMaster(formName);
                    Log.d("Route", FormSetterGetter.getRouteMaster());
                    break;
                case "Goods Receive":
                    FormSetterGetter.setGoodsReceive(formName);
                    Log.d("Goods Receive", FormSetterGetter.getGoodsReceive());
                    break;

                case "Merchandise":
                    FormSetterGetter.setMerchandise(formName);
                    Log.d("Merchandise", FormSetterGetter.getMerchandise());
                    break;

                case "Merchandise Schedule":
                    FormSetterGetter.setMerchandiseSchedule(formName);
                    Log.d("Merchandise Schedule", FormSetterGetter.getMerchandiseSchedule());
                    break;

                case "Settings":
                    FormSetterGetter.setSettings(formName);
                    break;
                case "Sales Order":
                    FormSetterGetter.setSalesOrder(formName);
                    break;
                case "Delivery Order":
                    FormSetterGetter.setDeliveryOrder(formName);
                    break;
                case "Consignment":
                    FormSetterGetter.setConsignment(formName);
                    Log.d("Consignment", FormSetterGetter.getConsignment());
                    break;
                case "Consignment Stock":
                    FormSetterGetter.setConsignmentStock(formName);
                    Log.d("Consignment Stock", FormSetterGetter.getConsignmentStock());
                    break;
                case "ConsignmentReturn":
                    FormSetterGetter.setConsignmentReturn(formName);
                    Log.d("ConsignmentReturn", FormSetterGetter.getConsignmentReturn());
                    break;
                case "ConsignmentStockTake":
                    FormSetterGetter.setConsignmentStockTake(formName);
                    Log.d("ConsignmentStockTake", FormSetterGetter.getConsignmentStockTake());
                    break;
                case "Invoice":
                    FormSetterGetter.setInvoice(formName);
                    break;
                case "Sales Return":
                    FormSetterGetter.setSalesReturn(formName);
                    break;
                case "Receipts":
                    FormSetterGetter.setReceipts(formName);
                    break;
                case "Product Stock":
                    FormSetterGetter.setProductList(formName);
                    break;
                case "Product Analysis":
                    FormSetterGetter.setProductAnalysis(formName);
                    break;
                case "Stock Request":
                    FormSetterGetter.setStockRequest(formName);
                    break;
                case "Transfer":
                    FormSetterGetter.setTransfer(formName);
                    break;
                case "Customer List":
                    FormSetterGetter.setCustomerList(formName);
                    break;
                case "Catalog":
                    FormSetterGetter.setCatalog(formName);
                    break;
                case "Stock Take":
                    FormSetterGetter.setStockTake(formName);
                    break;
                case "Stock Adjustment":
                    FormSetterGetter.setStockAdjustment(formName);
                    break;
                case "Hide Price":
                    FormSetterGetter.setHidePrice(formName);
                    break;
                case "Product Add":
                    FormSetterGetter.setProductAdd(true);
                    break;
                case "Customer Add":
                    FormSetterGetter.setCustomerAdd(true);
                    break;
                case "Edit Invoice":
                    FormSetterGetter.setEditInvoice(true);
                    break;
                case "Delete Invoice":
                    FormSetterGetter.setDeleteInvoice(true);
                    break;
                case "Delete Receipt":
                    FormSetterGetter.setDeleteReceipt(true);
                    break;
                case "Edit Price":
                    FormSetterGetter.setEditPrice(true);
                    break;
                case "Receipts Show All":
                    FormSetterGetter.setReceiptAll(true);
                    break;
                case "Edit Transaction Date":
                    FormSetterGetter.setEditTransactionDate(true);
                    break;
                case "Offline Mode":
                    FormSetterGetter.setOfflineMode(formName);
                    Log.d("Offline Mode1", FormSetterGetter.getOfflineMode());
                    break;
                case "Show All Location":
                    FormSetterGetter.setShowAllLocation(true);
                    Log.d("ShowAllLocation", "" + FormSetterGetter.isShowAllLocation());
                    break;
                case "Dashboard":
                    FormSetterGetter.setShowDashboard(true);
                    break;
                case "Show Product Analysis":
                    FormSetterGetter.setShowProductAnalysis(formName);
                    break;
                case "Quick Transfer" :
                    FormSetterGetter.setQuickTransfer(formName);
                    Log.d("Quicktransfer","-->"+FormSetterGetter.getQuickTransfer());
                    break;
                case "Manual Stock" :
                    FormSetterGetter.setManualStock(formName);
                    Log.d("ManualStock","-->"+FormSetterGetter.getManualStock());
                    break;
                case "Settlement" :
                      FormSetterGetter.setSettlement(formName);
                    Log.d("Settlement","-->"+FormSetterGetter.getSettlement());
                      break;

            }
        }
    }

    public class GetCompanyLogo implements CallbackInterface {

        @Override
        public void onSuccess(NodeList nl) {
            try {
                for (int i = 0; i < nl.getLength(); i++) {

                    Element e = (Element) nl.item(i);
                    String lo = XMLParser.getValue(e, COMPANYLOGO);

//				Log.d("logo", lo);

                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inSampleSize = 2;
                    byte[] encodeByte = Base64.decode(lo, Base64.DEFAULT);
                    bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                            encodeByte.length, o);

                    LogOutSetGet.setBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            }
        }

        @Override
        public void onFailure(ErrorType error) {
            onError(error);
        }

    }

    private void onError(final ErrorType error) {
        new Thread() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (error == ErrorType.NETWORK_UNAVAILABLE) {
                            helper.showLongToast(R.string.error_showing_image_no_network_connection);
                        } else {

                        }
                    }
                });
            }
        }.start();
    }

    public String checkInternetStatus() {
        checkOffline = OfflineCommon.isConnected(LoginActivity.this);
        String internetStatus = "";
        if (onlineMode.matches("True")) {
            checkOffline = OfflineCommon.isConnected(this);
            if (checkOffline == true) {
                String Off_dialog = OfflineDatabase
                        .getInternetMode("OfflineDialog");
                if (Off_dialog.matches("true")) {
                    internetStatus = "true";
                } else {
                    offlineCommon.OfflineAlertDialog();
                    Boolean dialogStatus = offlineCommon.showDialog();
                    OfflineDatabase.updateInternetMode("OfflineDialog",
                            dialogStatus + "");
                    Log.d("Offline DialogStatus", "" + dialogStatus);
                    internetStatus = "" + dialogStatus;
                }
            } else if (checkOffline == false) {
                String on_dialog = OfflineDatabase
                        .getInternetMode("OnlineDialog");
                if (on_dialog.matches("true")) {
                    internetStatus = "true";
                } else {
                    offlineCommon.onlineAlertDialog();
                    boolean dialogStatus = offlineCommon.showDialog();
                    OfflineDatabase.updateInternetMode("OnlineDialog",
                            dialogStatus + "");
                    Log.d("Online DialogStatus", "" + dialogStatus);
                    internetStatus = "" + dialogStatus;
                }
            }
        }

        onlineMode = OfflineDatabase.getOnlineMode();
        if (onlineMode.matches("True")) {
            offlineLayout.setVisibility(View.GONE);
            img_online.setVisibility(View.GONE);
            if (checkOffline == true) {
                if (internetStatus.matches("true")) {
                    img_online.setVisibility(View.VISIBLE);

                    offlineLayout.setVisibility(View.VISIBLE);
                    offlineLayout.setBackgroundResource(drawable.temp_offline_pattern_bg);
                }
            }

        } else if (onlineMode.matches("False")) {
            offlineLayout.setVisibility(View.VISIBLE);
            img_online.setVisibility(View.VISIBLE);
        }

        return internetStatus;
    }


    /**
     * mobile
     **/
    private class AsyncCallWSMobileCheck extends AsyncTask<String, Void, Void> {

        String loginM_gnrlStngs = "";

        @Override
        protected void onPreExecute() {
            dialogStatus = checkInternetStatus();
            loginArr.clear();
//			overduelistArray.clear();
            userTrackingMasterArray = null;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                // @Offline
                onlineMode = OfflineDatabase.getOnlineMode();
                if (onlineMode.matches("False")) {

                    loginArr = OfflineDatabase
                            .getUserMasterForMobileLogin(username, password);
                    serverdate = OfflineDatabase.getServerDate();
                    SalesOrderSetGet.setCurrentdate(serverdate);
                    loginResult = loginArr.get(0);
                    userGroup = loginArr.get(1);
                    isActive = loginArr.get(2);
                    CompanyCode = loginArr.get(3);
                    CompanyName = loginArr.get(4);
                    LastLoginLocation = loginArr.get(5);
                    LastLoginLocationName = loginArr.get(6);

                    if (loginResult.matches("True") && isActive.matches("True")) {
                        if (CompanyCode != null && !CompanyCode.isEmpty() && !CompanyCode.matches("null")) {
                            if (LastLoginLocation != null && !LastLoginLocation.isEmpty()) {
                                if (!LastLoginLocation.toString().matches("null")) {

                                    userGroupCodeArr = OfflineDatabase.getUserPermission(CompanyCode, userGroup);
                                    offlinemanager.setCompanyType(CompanyCode);
                                    OfflineDatabase.setcompany(LoginActivity.this);
                                    SupplierSetterGetter.setCompanyCode(CompanyCode);
                                    SalesOrderSetGet.setLocationcode(LastLoginLocation);
                                    SalesOrderSetGet.setLocationname(LastLoginLocationName);

                                    OfflineDatabase.getGeneralSettings();
                                    OfflineDatabase.setMobileSettings(CompanyCode);

//									OfflineDatabase.init(LoginActivity.this);
                                    String companyLogo = OfflineDatabase.getCompanyLogo();
//									Log.d("Login companyLogo", "companyLogo"+ companyLogo);

                                    BitmapFactory.Options o = new BitmapFactory.Options();
                                    o.inSampleSize = 2;
                                    byte[] encodeByte = Base64.decode(companyLogo, Base64.DEFAULT);
                                    bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, o);
                                    LogOutSetGet.setBitmap(bitmap);
                                }
                            }
                        }

                    }

                } else if (onlineMode.matches("True")) {
//                    if (checkOffline == true) {
//                        Log.d("DialogStatus", "" + dialogStatus);
//
//                        if (dialogStatus.matches("true")) {
//                            Log.d("CheckOffline Alert -->", "True");
//                            loginArr = OfflineDatabase.getUserMasterForMobileLogin(username, password);
//                            serverdate = OfflineDatabase.getServerDate();
//                            SalesOrderSetGet.setCurrentdate(serverdate);
//                            loginResult = loginArr.get(0);
//                            userGroup = loginArr.get(1);
//                            isActive = loginArr.get(2);
//                            CompanyCode = loginArr.get(3);
//                            CompanyName = loginArr.get(4);
//                            LastLoginLocation = loginArr.get(5);
//                            LastLoginLocationName = loginArr.get(6);
//
//                            if (loginResult.matches("True") && isActive.matches("True")) {
//                                if (CompanyCode != null && !CompanyCode.isEmpty() && !CompanyCode.matches("null")) {
//                                    if (LastLoginLocation != null && !LastLoginLocation.isEmpty()) {
//                                        if (!LastLoginLocation.toString().matches("null")) {
//
//                                            userGroupCodeArr = OfflineDatabase.getUserPermission(CompanyCode, userGroup);
//                                            offlinemanager.setCompanyType(CompanyCode);
//                                            OfflineDatabase.setcompany(LoginActivity.this);
//                                            SupplierSetterGetter.setCompanyCode(CompanyCode);
//                                            SalesOrderSetGet.setLocationcode(LastLoginLocation);
//                                            SalesOrderSetGet.setLocationname(LastLoginLocationName);
//
//                                            OfflineDatabase.getGeneralSettings();
//                                            OfflineDatabase.setMobileSettings(CompanyCode);
//
////											OfflineDatabase.init(LoginActivity.this);
//                                            String companyLogo = OfflineDatabase.getCompanyLogo();
////											Log.d("Login companyLogo","companyLogo" + companyLogo);
//
//                                            BitmapFactory.Options o = new BitmapFactory.Options();
//                                            o.inSampleSize = 2;
//                                            byte[] encodeByte = Base64.decode(companyLogo, Base64.DEFAULT);
//                                            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, o);
//                                            LogOutSetGet.setBitmap(bitmap);
//                                        }
//                                    }
//                                }
//
//                            }
//
//                        } else {
////							Log.d("CheckOffline Alert -->", "False");
//                            finish();
//                        }
//
//                    } else {
                        //
                        loginArr = LoginWebService.loginWS(username, password, "fncCheckUserNameAndPassword");
//                        serverdate = DateWebservice.getDateService("fncGetServerDate");
//                        SalesOrderSetGet.setCurrentdate(serverdate);
                        loginResult = loginArr.get(0);
                        userGroup = loginArr.get(1);
                        isActive = loginArr.get(2);
                        CompanyCode = loginArr.get(3);
                        CompanyName = loginArr.get(4);
                        LastLoginLocation = loginArr.get(5);
                        LastLoginLocationName = loginArr.get(6);
                        LoginPhoneNo = loginArr.get(7);
                        /*if (loginResult.matches("True")
                                && isActive.matches("True")) {
                            if (CompanyCode != null && !CompanyCode.isEmpty() && !CompanyCode.matches("null")) {
                                if (LastLoginLocation != null && !LastLoginLocation.isEmpty()) {
                                    if (!LastLoginLocation.toString().matches("null")) {

                                        offlinemanager.setCompanyType(CompanyCode);
                                        OfflineDatabase.setcompany(LoginActivity.this);
                                        SupplierSetterGetter.setCompanyCode(CompanyCode);
                                        SalesOrderSetGet.setLocationcode(LastLoginLocation);
                                        SalesOrderSetGet.setLocationname(LastLoginLocationName);
                                        SalesOrderSetGet.setLoginPhoneNo(LoginPhoneNo);

                                        loginM_gnrlStngs = ValidateWebService.generalSettingsService(valid_url, "fncGetGeneralSettings", CompanyCode);
                                        HashMap<String, String> hmvalue = new HashMap<String, String>();
                                        hmvalue.put("CompanyCode", CompanyCode);
//									     overduelistArray = SalesProductWebService.getInvoiceOverdue(hmvalue, "fncGetInvoiceHeaderByTerms");
                                        //TrackUserMaster
                                        hmvalue.put("UserName", username);
                                        userTrackingMasterArray = WebServiceClass.parameterWebservice(hmvalue, "fncGetUserTrackingMaster");

                                        try {
                                            new XMLAccessTask(LoginActivity.this, valid_url, "fncGetCompanyLogo", new GetCompanyLogo()).execute();
                                            userGroupCodeArr = GetUserPermission.userGroupCode(userGroup, CompanyCode, "fncGetUserPermission");

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
                                        }
                                    }

                                }
                            }

                        }*/

                    }
//                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (loginResult.matches("True") && isActive.matches("True")) {
                    if (CompanyCode != null && !CompanyCode.isEmpty() && !CompanyCode.matches("null")) {
                        if (LastLoginLocation != null && !LastLoginLocation.isEmpty()) {
                            if (!LastLoginLocation.toString().matches("null")) {
                                SalesOrderSetGet.setSaleorderdate(serverdate);

//								Log.d("OverdueArray Size", "-->" + overduelistArray.size());
//								if (overduelistArray.size() > 0) {
                                offlinemanager.setOverdue("true");
//								} else {
//									offlinemanager.setOverdue("false");
//								}

                                if (loginM_gnrlStngs != null && !loginM_gnrlStngs.isEmpty()) {
                                    if (loginM_gnrlStngs.matches("S")) {
                                        LogOutSetGet.setApplicationType("S");
                                        SalesOrderSetGet.setGeneralsetting("SOT");
                                    } else if (loginM_gnrlStngs.matches("W")) {
                                        LogOutSetGet.setApplicationType("W");
                                        SalesOrderSetGet.setGeneralsetting("WareHouse");
                                    }
                                } else {
                                    LogOutSetGet.setApplicationType("S");
                                    SalesOrderSetGet.setGeneralsetting("SOT");
                                }


                                // offlinemanager.setCompanyType();

                                MobileAsyncCallWS task = new MobileAsyncCallWS();
                                task.execute();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login atleast onetime on webportal to activate this id", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                spinnerLayout.setVisibility(View.GONE);
                                enableViews(login_parent, true);
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Login atleast onetime on webportal to activate this id", Toast.LENGTH_LONG).show();

                            progressBar.setVisibility(View.GONE);
                            spinnerLayout.setVisibility(View.GONE);
                            enableViews(login_parent, true);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login atleast onetime on webportal to activate this id", Toast.LENGTH_LONG).show();

                        progressBar.setVisibility(View.GONE);
                        spinnerLayout.setVisibility(View.GONE);
                        enableViews(login_parent, true);
                    }

                } else if (loginResult.matches("False") && isActive.matches("0")) {
                    Toast.makeText(LoginActivity.this, "Invalid Username and Password", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                    spinnerLayout.setVisibility(View.GONE);
                    enableViews(login_parent, true);

                } else if (loginResult.matches("True") && isActive.matches("0")) {
                    Toast.makeText(LoginActivity.this, "User Status is inactive.Login Failed", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                    spinnerLayout.setVisibility(View.GONE);
                    enableViews(login_parent, true);

                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            }
            // progressBar.setVisibility(View.GONE);
            // spinnerLayout.setVisibility(View.GONE);
            // enableViews(login_parent, true);
        }

    }


    /*private class MobileLogoAsyncCallWS extends AsyncTask<String, Void, Void> {

        String loginM_gnrlStngs = "";

        @Override
        protected void onPreExecute() {
            dialogStatus = checkInternetStatus();
            loginArr.clear();
//			overduelistArray.clear();
            userTrackingMasterArray = null;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                // @Offline
                onlineMode = OfflineDatabase.getOnlineMode();
                if (onlineMode.matches("False")) {

                    loginArr = OfflineDatabase
                            .getUserMasterForMobileLogin(username, password);
                    serverdate = OfflineDatabase.getServerDate();
                    SalesOrderSetGet.setCurrentdate(serverdate);
                    loginResult = loginArr.get(0);
                    userGroup = loginArr.get(1);
                    isActive = loginArr.get(2);
                    CompanyCode = loginArr.get(3);
                    CompanyName = loginArr.get(4);
                    LastLoginLocation = loginArr.get(5);
                    LastLoginLocationName = loginArr.get(6);

                    if (loginResult.matches("True") && isActive.matches("True")) {
                        if (CompanyCode != null && !CompanyCode.isEmpty() && !CompanyCode.matches("null")) {
                            if (LastLoginLocation != null && !LastLoginLocation.isEmpty()) {
                                if (!LastLoginLocation.toString().matches("null")) {

                                    userGroupCodeArr = OfflineDatabase.getUserPermission(CompanyCode, userGroup);
                                    offlinemanager.setCompanyType(CompanyCode);
                                    OfflineDatabase.setcompany(LoginActivity.this);
                                    SupplierSetterGetter.setCompanyCode(CompanyCode);
                                    SalesOrderSetGet.setLocationcode(LastLoginLocation);
                                    SalesOrderSetGet.setLocationname(LastLoginLocationName);

                                    OfflineDatabase.getGeneralSettings();
                                    OfflineDatabase.setMobileSettings(CompanyCode);

//									OfflineDatabase.init(LoginActivity.this);
                                    String companyLogo = OfflineDatabase.getCompanyLogo();
//									Log.d("Login companyLogo", "companyLogo"+ companyLogo);

                                    BitmapFactory.Options o = new BitmapFactory.Options();
                                    o.inSampleSize = 2;
                                    byte[] encodeByte = Base64.decode(companyLogo, Base64.DEFAULT);
                                    bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, o);
                                    LogOutSetGet.setBitmap(bitmap);
                                }
                            }
                        }

                    }

                } else if (onlineMode.matches("True")) {
                    if (checkOffline == true) {
                        Log.d("DialogStatus", "" + dialogStatus);

                        if (dialogStatus.matches("true")) {
                            Log.d("CheckOffline Alert -->", "True");
                            loginArr = OfflineDatabase.getUserMasterForMobileLogin(username, password);
                            serverdate = OfflineDatabase.getServerDate();
                            SalesOrderSetGet.setCurrentdate(serverdate);
                            loginResult = loginArr.get(0);
                            userGroup = loginArr.get(1);
                            isActive = loginArr.get(2);
                            CompanyCode = loginArr.get(3);
                            CompanyName = loginArr.get(4);
                            LastLoginLocation = loginArr.get(5);
                            LastLoginLocationName = loginArr.get(6);

                            if (loginResult.matches("True") && isActive.matches("True")) {
                                if (CompanyCode != null && !CompanyCode.isEmpty() && !CompanyCode.matches("null")) {
                                    if (LastLoginLocation != null && !LastLoginLocation.isEmpty()) {
                                        if (!LastLoginLocation.toString().matches("null")) {

                                            userGroupCodeArr = OfflineDatabase.getUserPermission(CompanyCode, userGroup);
                                            offlinemanager.setCompanyType(CompanyCode);
                                            OfflineDatabase.setcompany(LoginActivity.this);
                                            SupplierSetterGetter.setCompanyCode(CompanyCode);
                                            SalesOrderSetGet.setLocationcode(LastLoginLocation);
                                            SalesOrderSetGet.setLocationname(LastLoginLocationName);

                                            OfflineDatabase.getGeneralSettings();
                                            OfflineDatabase.setMobileSettings(CompanyCode);

//											OfflineDatabase.init(LoginActivity.this);
                                            String companyLogo = OfflineDatabase.getCompanyLogo();
//											Log.d("Login companyLogo","companyLogo" + companyLogo);

                                            BitmapFactory.Options o = new BitmapFactory.Options();
                                            o.inSampleSize = 2;
                                            byte[] encodeByte = Base64.decode(companyLogo, Base64.DEFAULT);
                                            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, o);
                                            LogOutSetGet.setBitmap(bitmap);
                                        }
                                    }
                                }

                            }

                        } else {
//							Log.d("CheckOffline Alert -->", "False");
                            finish();
                        }

                    } else {
                        //
                        loginArr = LoginWebService.loginWS(username, password, "fncCheckUserNameAndPassword");
                        serverdate = DateWebservice.getDateService("fncGetServerDate");
                        SalesOrderSetGet.setCurrentdate(serverdate);
                        loginResult = loginArr.get(0);
                        userGroup = loginArr.get(1);
                        isActive = loginArr.get(2);
                        CompanyCode = loginArr.get(3);
                        CompanyName = loginArr.get(4);
                        LastLoginLocation = loginArr.get(5);
                        LastLoginLocationName = loginArr.get(6);
                        LoginPhoneNo = loginArr.get(7);
                        if (loginResult.matches("True")
                                && isActive.matches("True")) {
                            if (CompanyCode != null && !CompanyCode.isEmpty() && !CompanyCode.matches("null")) {
                                if (LastLoginLocation != null && !LastLoginLocation.isEmpty()) {
                                    if (!LastLoginLocation.toString().matches("null")) {

                                        offlinemanager.setCompanyType(CompanyCode);
                                        OfflineDatabase.setcompany(LoginActivity.this);
                                        SupplierSetterGetter.setCompanyCode(CompanyCode);
                                        SalesOrderSetGet.setLocationcode(LastLoginLocation);
                                        SalesOrderSetGet.setLocationname(LastLoginLocationName);
                                        SalesOrderSetGet.setLoginPhoneNo(LoginPhoneNo);

                                        loginM_gnrlStngs = ValidateWebService.generalSettingsService(valid_url, "fncGetGeneralSettings", CompanyCode);
                                        HashMap<String, String> hmvalue = new HashMap<String, String>();
                                        hmvalue.put("CompanyCode", CompanyCode);
//									     overduelistArray = SalesProductWebService.getInvoiceOverdue(hmvalue, "fncGetInvoiceHeaderByTerms");
                                        //TrackUserMaster
                                        hmvalue.put("UserName", username);
                                        userTrackingMasterArray = WebServiceClass.parameterWebservice(hmvalue, "fncGetUserTrackingMaster");

                                        try {
                                            new XMLAccessTask(LoginActivity.this, valid_url, "fncGetCompanyLogo", new GetCompanyLogo()).execute();
                                            userGroupCodeArr = GetUserPermission.userGroupCode(userGroup, CompanyCode, "fncGetUserPermission");

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
                                        }
                                    }

                                }
                            }

                        }

                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (loginResult.matches("True") && isActive.matches("True")) {
                    if (CompanyCode != null && !CompanyCode.isEmpty() && !CompanyCode.matches("null")) {
                        if (LastLoginLocation != null && !LastLoginLocation.isEmpty()) {
                            if (!LastLoginLocation.toString().matches("null")) {
                                SalesOrderSetGet.setSaleorderdate(serverdate);

//								Log.d("OverdueArray Size", "-->" + overduelistArray.size());
//								if (overduelistArray.size() > 0) {
                                offlinemanager.setOverdue("true");
//								} else {
//									offlinemanager.setOverdue("false");
//								}

                                if (loginM_gnrlStngs != null && !loginM_gnrlStngs.isEmpty()) {
                                    if (loginM_gnrlStngs.matches("S")) {
                                        LogOutSetGet.setApplicationType("S");
                                        SalesOrderSetGet.setGeneralsetting("SOT");
                                    } else if (loginM_gnrlStngs.matches("W")) {
                                        LogOutSetGet.setApplicationType("W");
                                        SalesOrderSetGet.setGeneralsetting("WareHouse");
                                    }
                                } else {
                                    LogOutSetGet.setApplicationType("S");
                                    SalesOrderSetGet.setGeneralsetting("SOT");
                                }


                                // offlinemanager.setCompanyType();

                                MobileAsyncCallWS task = new MobileAsyncCallWS();
                                task.execute();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login atleast onetime on webportal to activate this id", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                spinnerLayout.setVisibility(View.GONE);
                                enableViews(login_parent, true);
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Login atleast onetime on webportal to activate this id", Toast.LENGTH_LONG).show();

                            progressBar.setVisibility(View.GONE);
                            spinnerLayout.setVisibility(View.GONE);
                            enableViews(login_parent, true);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login atleast onetime on webportal to activate this id", Toast.LENGTH_LONG).show();

                        progressBar.setVisibility(View.GONE);
                        spinnerLayout.setVisibility(View.GONE);
                        enableViews(login_parent, true);
                    }

                } else if (loginResult.matches("False") && isActive.matches("0")) {
                    Toast.makeText(LoginActivity.this, "Invalid Username and Password", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                    spinnerLayout.setVisibility(View.GONE);
                    enableViews(login_parent, true);

                } else if (loginResult.matches("True") && isActive.matches("0")) {
                    Toast.makeText(LoginActivity.this, "User Status is inactive.Login Failed", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                    spinnerLayout.setVisibility(View.GONE);
                    enableViews(login_parent, true);

                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            }
            // progressBar.setVisibility(View.GONE);
            // spinnerLayout.setVisibility(View.GONE);
            // enableViews(login_parent, true);
        }

    }*/


    private class MobileAsyncCallWS extends AsyncTask<Void, Void, Void> {
        HashMap<String, String> hmBatch = new HashMap<String, String>();
        String loginM_gnrlStngs = "",loginM_dsplySetngs = "";
        @Override
        protected void onPreExecute() {

            al.clear();
            al.add(0, "Select Location");
            loccode.clear();
            dialogStatus = checkInternetStatus();
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            try {
                String companyCode = SupplierSetterGetter.getCompanyCode();
                String locationCode = SalesOrderSetGet.getLocationcode();
                onlineMode = OfflineDatabase.getOnlineMode();
                if (onlineMode.matches("False")) {
                    hmBatch = OfflineDatabase.getLocationHaveBatchOffline(locationCode);
                    loccode = OfflineDatabase.getLocation(companyCode);
                    companyArr = OfflineDatabase.getCompanyDetail();
                } else if (onlineMode.matches("True")) {
                    if (checkOffline == true) {
                        if (dialogStatus.matches("true")) {

                            hmBatch = OfflineDatabase.getLocationHaveBatchOffline(locationCode);
                            loccode = OfflineDatabase.getLocation(companyCode);
                            companyArr = OfflineDatabase.getCompanyDetail();
                        } else {
//							Log.d("CheckOffline Alert -->", "False");
                            finish();
                        }

                    } else {

                        if (loginResult.matches("True")
                                && isActive.matches("True")) {
                            if (CompanyCode != null && !CompanyCode.isEmpty() && !CompanyCode.matches("null")) {
                                if (LastLoginLocation != null && !LastLoginLocation.isEmpty()) {
                                    if (!LastLoginLocation.toString().matches("null")) {

                                        offlinemanager.setCompanyType(CompanyCode);
                                        OfflineDatabase.setcompany(LoginActivity.this);
                                        SupplierSetterGetter.setCompanyCode(CompanyCode);
                                        SalesOrderSetGet.setLocationcode(LastLoginLocation);
                                        SalesOrderSetGet.setLocationname(LastLoginLocationName);
                                        SalesOrderSetGet.setLoginPhoneNo(LoginPhoneNo);

                                        loginM_gnrlStngs =ValidateWebService .generalSettingsService(valid_url, "fncGetGeneralSettings", CompanyCode);
                                        loginM_dsplySetngs = ValidateWebService .generalService(valid_url, "fncGetTranDisplaySettings", CompanyCode);
                                        HashMap<String, String> hmvalue = new HashMap<String, String>();
                                        hmvalue.put("CompanyCode", CompanyCode);
//									     overduelistArray = SalesProductWebService.getInvoiceOverdue(hmvalue, "fncGetInvoiceHeaderByTerms");
                                        //TrackUserMaster
                                        hmvalue.put("UserName", username);
                                        userTrackingMasterArray = WebServiceClass.parameterWebservice(hmvalue, "fncGetUserTrackingMaster");

                                        try {
                                            new XMLAccessTask(LoginActivity.this, valid_url, "fncGetCompanyLogo", new GetCompanyLogo()).execute();
                                            userGroupCodeArr = GetUserPermission.userGroupCode(userGroup, CompanyCode, "fncGetUserPermission");

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
                                        }
                                    }

                                }
                            }

                        }

//						Log.d("Online -->", "Online");
                        hmBatch = GetUserPermission.getLocationHaveBatch("fncGetLocation", companyCode, locationCode);
                        GetUserPermission.mobileSettings("fncGetMobileSettings");
                        GetUserPermission.getRouteMaster("fncGetRouteMaster", companyCode, username, serverdate);
                        loccode = SetStockInDetail.getLocationcode("fncGetLocation", CompanyCode);
                        companyArr = SalesProductWebService.getCompany("fncGetCompany");


                    }

                }

                System.out.println("Using EntrySet");
                for (Map.Entry<String, String> studentEntry : loccode
                        .entrySet()) {
                    System.out.println(studentEntry.getKey() + " :: "
                            + studentEntry.getValue());
                    al.add(studentEntry.getKey());
                }

            } catch (JSONException e) {

                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (!companyArr.isEmpty()) {
//				Log.d("PostExecute companyArr", companyArr.toString());

                    taxType = companyArr.get(0);
                    SalesOrderSetGet.setCompanytax(taxType);
                    SalesOrderSetGet.setCompanytaxvalue(companyArr.get(1));
                    Company.setCompanyName(companyArr.get(2));
                    Company.setAddress1(companyArr.get(3));
                    Company.setAddress2(companyArr.get(4));
                    Company.setAddress3(companyArr.get(5));
                    Company.setCountry(companyArr.get(6));
                    Company.setZipCode(companyArr.get(7));
                    Company.setPhoneNo(companyArr.get(8));
                    Company.setFax(companyArr.get(9));
                    Company.setEmail(companyArr.get(10));
                    Company.setTaxRegNo(companyArr.get(11));
                    Company.setBusinessRegNo(companyArr.get(12));

//				Log.d("Fax", "f " + companyArr.get(9));
                }

                getMobilelocation();
                LogOutSetGet.setActive(false);

                SalesOrderSetGet.setHaveBatchOnStockIn(hmBatch.get("HaveBatchOnStockIn"));
                SalesOrderSetGet.setHaveBatchOnTransfer(hmBatch.get("HaveBatchOnTransfer"));
                SalesOrderSetGet.setHaveBatchOnStockAdjustment(hmBatch.get("HaveBatchOnStockAdjustment"));
                SalesOrderSetGet.setHaveBatchOnStockOut(hmBatch.get("HaveBatchOnStockOut"));
                SalesOrderSetGet.setHaveBatchOnConsignmentOut(hmBatch.get("HaveBatchOnConsignmentOut"));


                Log.d("HaveBatchOnStockIn", "-->" + SalesOrderSetGet.getHaveBatchOnStockIn());
                Log.d("HaveBatchOnTransfer", "-->" + SalesOrderSetGet.getHaveBatchOnTransfer());
                Log.d("HaveStockAdjustment", "-->" + SalesOrderSetGet.getHaveBatchOnStockAdjustment());
                Log.d("HaveBatchOnStockOut", "-->" + SalesOrderSetGet.getHaveBatchOnStockOut());
                Log.d("HaveBatchOnConsignmentOut", "-->" + SalesOrderSetGet.getHaveBatchOnStockOut());

                Log.d("ROUTE PERMISSION ", "-->" + SalesOrderSetGet.getRoutepermission());
                userGroup();
                printMobileResult(loginResult, isActive);

                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(login_parent, true);
            } catch (Exception e) {
                e.printStackTrace();
                errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
            }
        }
    }


    public void printMobileResult(String res, String isAct) {
        String dbUsertracking = "";
        try {
            SharedPreferences sp = getApplicationContext()
                    .getSharedPreferences(MyPREFERENCES,
                            Context.MODE_PRIVATE);
            Editor editor = sp.edit();
            //
            // String spinnerText = mySpinner.getSelectedItem().toString();
            // editor.putString(Location, spinnerText);

            editor.putString(companyCode,
                    SupplierSetterGetter.getCompanyCode());
            editor.putString(companyName,
                    SupplierSetterGetter.getCompanyName());

            if (chkrememberme.isChecked()) {
                editor.putBoolean(CheckedState, true);
                editor.putString(Name, username);
                editor.putString(Password, password);
                editor.putString(companyCode,
                        SupplierSetterGetter.getCompanyCode());
                editor.putString(companyName,
                        SupplierSetterGetter.getCompanyName());
                // editor.putString(Location, spinnerText);
                editor.commit();
            } else {
                editor.putString(Name, null);
                editor.putString(Password, null);
                editor.putString(companyCode, null);
                editor.putString(companyName, null);
                editor.putString(Location, null);
                editor.putBoolean(CheckedState, false);
                editor.commit();
            }

            onlineMode = OfflineDatabase.getOnlineMode();
            if (onlineMode.matches("True")) {
                if (checkOffline == true) {

                } else {
                    String haveTracking = SalesOrderSetGet.getHaveTracking();
                    boolean isService = isMyServiceRunning(AppLocationService.class);
                    if (isService) {
                        stopService(new Intent(LoginActivity.this, AppLocationService.class));
                    }

                    if (haveTracking.matches("1")) {
                        Log.d("userTracki", "->" + userTrackingMasterArray.length());
                        if (userTrackingMasterArray != null && userTrackingMasterArray.length() > 0) {
                            dbUsertracking = SOTDatabase.store_usertracking(userTrackingMasterArray);
                            Log.d("dbUsertracking", "-> " + dbUsertracking);
//				        		startService(new Intent(LoginActivity.this, AppLocationService.class));
                        }
                    }
                }
            }

            Log.d("dbUsertrackin", "-> " + dbUsertracking);
            checkForUpdate(dbUsertracking);


        } catch (Exception e) {
            e.printStackTrace();
            errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
        }
    }

    private void loginbtnClick() {
        try {
            if (etuserName.getText().length() != 0
                    && etuserName.getText().toString() != "") {

                if (etPassword.getText().length() != 0
                        && etPassword.getText().toString() != "") {

                    sppos = mySpinner.getSelectedItem().toString();

                    if (!sppos.equals("Select Location")) {
                        username = etuserName.getText().toString();
                        password = etPassword.getText().toString();
                        spinnerLayout = new LinearLayout(LoginActivity.this);
                        spinnerLayout.setGravity(Gravity.CENTER);
                        addContentView(spinnerLayout, new LayoutParams(
                                ViewGroup.LayoutParams.FILL_PARENT,
                                ViewGroup.LayoutParams.FILL_PARENT));
                        spinnerLayout.setBackgroundColor(Color
                                .parseColor("#80000000"));
                        enableViews(login_parent, false);
                        progressBar = new ProgressBar(LoginActivity.this);
                        progressBar.setProgress(android.R.attr.progressBarStyle);
                        progressBar.setIndeterminateDrawable(getResources()
                                .getDrawable(drawable.greenprogress));
                        spinnerLayout.addView(progressBar);

                        AsyncCallCheckLogin task = new AsyncCallCheckLogin();
                        task.execute();
                        getlocation();
                        LogOutSetGet.setActive(false);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please Select Location", Toast.LENGTH_SHORT)
                                .show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(),
                            "Username and password mismatched", Toast.LENGTH_SHORT)
                            .show();
                }
            } else {

                Toast.makeText(getApplicationContext(),
                        "Username and password mismatched", Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
        }
    }

    private void loginbtnMobileClick() {
        try {
            if (etuserName.getText().length() != 0
                    && etuserName.getText().toString() != "") {

                if (etPassword.getText().length() != 0
                        && etPassword.getText().toString() != "") {

                    username = etuserName.getText().toString();
                    password = etPassword.getText().toString();
                    spinnerLayout = new LinearLayout(LoginActivity.this);
                    spinnerLayout.setGravity(Gravity.CENTER);
                    addContentView(spinnerLayout, new LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT));
                    spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
                    enableViews(login_parent, false);
                    progressBar = new ProgressBar(LoginActivity.this);
                    progressBar.setProgress(android.R.attr.progressBarStyle);
                    progressBar.setIndeterminateDrawable(getResources()
                            .getDrawable(drawable.greenprogress));
                    spinnerLayout.addView(progressBar);

                    AsyncCallWSMobileCheck task = new AsyncCallWSMobileCheck();
                    task.execute();


                } else {

                    Toast.makeText(getApplicationContext(),
                            "Username and password mismatched", Toast.LENGTH_SHORT)
                            .show();
                }
            } else {

                Toast.makeText(getApplicationContext(),
                        "Username and password mismatched", Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
        }
    }

    /**
     * Track User Service Start
     **/

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d("" + serviceClass.getName(), "->" + service.service.getClassName());
                return true;
            }
        }
        return false;
    }

    /**
     * Track User Service End
     **/
    private void checkForUpdate(final String dbUsertracking) {
        int latestVesion = Validate.getApplicationVersionCode(getApplicationContext());
        String androidVersionSFA = Company.getAndroidVersion_SFA();
        String mSFA_CheckPlayStore = Company.getAndroidVersion_SFA_CheckPlayStore();
        int mLatestVersion = -1;
        if (androidVersionSFA != null && !androidVersionSFA.isEmpty()) {
            mLatestVersion = Integer.valueOf(androidVersionSFA);
        }
        if (mSFA_CheckPlayStore != null && !mSFA_CheckPlayStore.isEmpty()) {
        } else {
            mSFA_CheckPlayStore = "False";
        }
        if (latestVesion < mLatestVersion) {
            if (mSFA_CheckPlayStore.equalsIgnoreCase("True")) {
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_update_version);
                dialog.setCancelable(false);
                TextView title = (TextView) dialog.findViewById(R.id.title);
                title.setText(R.string.update);
                TextView message = (TextView) dialog.findViewById(R.id.message);
                message.setText(R.string.do_you_want_to_update);
                // set up no
                Button yes = (Button) dialog.findViewById(R.id.yes);
                yes.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            intent.setData(Uri.parse("market://details?id="
                                    + getPackageName()));
                        } catch (ActivityNotFoundException anfe) {
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="
                                    + getPackageName()));
                        }
                        startActivity(intent);
                        finish();
                    }
                });
                // set up no
                Button no = (Button) dialog.findViewById(R.id.no);
                no.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        launchActivity(dbUsertracking);
                    }
                });
                // now that the dialog is set up, it's time to show it
                dialog.show();
            } else {
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_warning);
                dialog.setCancelable(false);
                TextView title = (TextView) dialog.findViewById(R.id.title);
                title.setText(R.string.warning);
                TextView message = (TextView) dialog.findViewById(R.id.message);
                message.setText(R.string.not_update_warning);
                // set up ok
                Button ok = (Button) dialog.findViewById(R.id.ok);
                ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        launchActivity(dbUsertracking);
                    }
                });

                // now that the dialog is set up, it's time to show it
                dialog.show();
            }

        } else if (mLatestVersion != -1 && (Validate.getApplicationVersionCode(getApplicationContext()) > mLatestVersion)) {
            final Dialog dialog = new Dialog(LoginActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);
            TextView title = (TextView) dialog.findViewById(R.id.title);
            title.setText(R.string.warning);
            TextView message = (TextView) dialog.findViewById(R.id.message);
            message.setText(R.string.update_warning);
            // set up ok
            Button ok = (Button) dialog.findViewById(R.id.ok);
            ok.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    launchActivity(dbUsertracking);
                }
            });

            // now that the dialog is set up, it's time to show it
            dialog.show();
        } else {
//			launchActivity(dbUsertracking);

            if (onlineMode.matches("True")) {

                if (checkOffline == false) {

                    if(mobileHaveOfflineMode.matches("1")) {
                        startProgrress();
                        downloaderLogin.setOnDownloadCompletionListener(new OfflineDownloaderLogin.OnDownloadCompletionListener() {
                            @Override
                            public void onCompleted() {

                                Log.d("Download", "Download completed");

                                progressBar.setVisibility(View.GONE);
                                spinnerLayout.setVisibility(View.GONE);
                                enableViews(login_parent, true);
//
                                callLanding = new Intent(LoginActivity.this, LandingActivity.class);
                                callLanding.putExtra("dbUsertracking", dbUsertracking);
                                startActivity(callLanding);
                                finish();
                            }
                        });
                    }else{
                        callLanding = new Intent(LoginActivity.this, LandingActivity.class);
                        callLanding.putExtra("dbUsertracking", dbUsertracking);
                        startActivity(callLanding);
                        finish();
                    }


                } else {
                    callLanding = new Intent(LoginActivity.this, LandingActivity.class);
                    callLanding.putExtra("dbUsertracking", dbUsertracking);
                    startActivity(callLanding);
                    finish();
                }
            } else {
                callLanding = new Intent(LoginActivity.this, LandingActivity.class);
                callLanding.putExtra("dbUsertracking", dbUsertracking);
                startActivity(callLanding);
                finish();
            }
        }
    }

    public void startProgrress() {
        spinnerLayout = new LinearLayout(LoginActivity.this);
        spinnerLayout.setGravity(Gravity.CENTER);
        addContentView(spinnerLayout, new LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
        enableViews(login_parent, false);
        progressBar = new ProgressBar(LoginActivity.this);
        progressBar.setProgress(android.R.attr.progressBarStyle);
        progressBar.setIndeterminateDrawable(getResources().getDrawable(
                drawable.greenprogress));
        spinnerLayout.addView(progressBar);
    }

    public void launchActivity(final String dbUsertracking) {

        if (onlineMode.matches("True")) {

            if (checkOffline == false) {
//				startProgrress();
//				downloaderLogin.setOnDownloadCompletionListener(new OfflineDownloaderLogin.OnDownloadCompletionListener() {
//					@Override
//					public void onCompleted() {

                Log.d("Download", "Download completed");

//						progressBar.setVisibility(View.GONE);
//						spinnerLayout.setVisibility(View.GONE);
//						enableViews(login_parent, true);

                callLanding = new Intent(LoginActivity.this, LandingActivity.class);
                callLanding.putExtra("dbUsertracking", dbUsertracking);
                startActivity(callLanding);
                finish();
//					}
//				});
            } else {
                callLanding = new Intent(LoginActivity.this, LandingActivity.class);
                callLanding.putExtra("dbUsertracking", dbUsertracking);
                startActivity(callLanding);
                finish();
            }
        } else {
            callLanding = new Intent(LoginActivity.this, LandingActivity.class);
            callLanding.putExtra("dbUsertracking", dbUsertracking);
            startActivity(callLanding);
            finish();
        }

    }
}