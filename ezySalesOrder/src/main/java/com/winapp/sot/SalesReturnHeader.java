package com.winapp.sot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.DateTime;
import com.winapp.helper.XMLParser;
import com.winapp.model.Product;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.printcube.printcube.BluetoothService;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.printer.CommonPreviewPrint;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.PrinterZPL;
import com.winapp.printer.UIHelper;
import com.winapp.util.XMLAccessTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

@SuppressLint("NewApi")
public class SalesReturnHeader extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace {

    String valid_url, serverdate, sDate, eDate, cuscode;
    int textlength = 0;
    int month, day, year, printid;
    EditText edCustomerCode, starteditTextDate, endeditTextDate, sl_namefield,locationcode_filter;
    Button btcstmrsrch;
    ListView so_lv;
    ProgressBar progressBar;
    LinearLayout spinnerLayout;
    ArrayList<HashMap<String, String>> searchResults,locationArrHm;
    ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
    CustomAlertAdapterSupp arrayAdapterSupp;
    private AlertDialog myalertDialog = null;
    LinearLayout salesO_parent, searchCstmrlayout, codelayout;

    Calendar startCalendar, endCalendar;
    ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
    ArrayList<SO> searchCstCdArr = new ArrayList<SO>();

    static String headeresult;
    private static String SOAP_ACTION = "http://tempuri.org/";
    private static String webMethName = "fncGetSalesReturnHeader";
    private static String NAMESPACE = "http://tempuri.org/";
    String sNo = "";
    boolean mnnbldsbl;
    CheckBox so_checkbox;
    ArrayList<SO> list;
    HeaderAdapter sradapter;
    String sosno, sodate, socustomercode, soamount, sostatus, socustomername = "", cmpnyCode = "", select_van = "",loccode="";
    private UIHelper helper;

    ArrayList<ProductDetails> product;
    ArrayList<ProductDetails> productdet;
    String jsonString = null, jsonStr = null;
    JSONObject jsonResponse, jsonResp;
    JSONArray jsonMainNode, jsonSecNode;
    HashMap<String, String> hashValue = new HashMap<String, String>();
    Cursor cursor;
    ImageButton search, back, printer, addnew;
    private SlidingMenu menu;
    private ArrayList<HashMap<String, String>> SRDetailsArr;
    private ArrayList<HashMap<String, String>> SRHeadersArr;
    private ArrayList<HashMap<String, String>> SRConsignmentArr;
    private HashMap<String, String> params;
    String getSignatureimage = "", getPhotoimage = "";
    // Offline
    LinearLayout offlineLayout;
    private OfflineDatabase offlineDatabase;
    boolean checkOffline;
    String onlineMode, offlineDialogStatus;
    private OfflineCommon offlineCommon;
    OfflineSettingsManager spManager;
    String companyCode="",appPrintGroup="",decimalpts = ".00",mobileHaveOfflineMode="",haveAttribute="";
    private TextView totaloutstanding;
    private ArrayList<String> sortCatSubCat;
    private List<String> sort;
    private HashSet<String> hs;
    private List<String> printsortHeader;
    ArrayList<Product> mGetSOAttributeDetail = new ArrayList<Product>();
    String tranType = "";
    String deleteSRResult="",remarks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(drawable.ic_menu);
        setContentView(R.layout.salesreturn_fragment);

        GlobalData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        GlobalData.mService = new BluetoothService(this, mHandler);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        View customNav = LayoutInflater.from(this).inflate(
                R.layout.slidemenu_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        txt.setText("Sales Return");
        search = (ImageButton) customNav.findViewById(R.id.search_img);
        printer = (ImageButton) customNav.findViewById(R.id.printer);
        addnew = (ImageButton) customNav.findViewById(R.id.custcode_img);

        ab.setCustomView(customNav);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayHomeAsUpEnabled(false);
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slidemenufragment);
        menu.setSlidingEnabled(false);
        locationcode_filter = (EditText) findViewById (R.id.locationcode_filter);
        totaloutstanding = (TextView) findViewById(R.id.totaloutstanding);
        edCustomerCode = (EditText) findViewById(R.id.salesOCustCode);
        starteditTextDate = (EditText) findViewById(R.id.starteditTextDate);
        endeditTextDate = (EditText) findViewById(R.id.endeditTextDate);
        btcstmrsrch = (Button) findViewById(R.id.saleO_btsearch);
        so_lv = (ListView) findViewById(R.id.saleO_listView1);
        salesO_parent = (LinearLayout) findViewById(R.id.salesOrder_parent);
        codelayout = (LinearLayout) findViewById(R.id.codelayout);
        searchCstmrlayout = (LinearLayout) findViewById(R.id.searchlayout);
        so_checkbox = (CheckBox) findViewById(R.id.checkbox);

        sl_namefield = (EditText) findViewById(R.id.sl_namefield);
        sortCatSubCat = new ArrayList<String>();
        sort = new ArrayList<String>();
        hs = new HashSet<String>();
        printsortHeader = new ArrayList<String>();
        product = new ArrayList<ProductDetails>();
        productdet = new ArrayList<ProductDetails>();
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        searchCstmrlayout.setVisibility(View.GONE);
        al.clear();
        searchCstCdArr.clear();
        helper = new UIHelper(SalesReturnHeader.this);
        FWMSSettingsDatabase.init(SalesReturnHeader.this);
        SOTDatabase.init(SalesReturnHeader.this);
        valid_url = FWMSSettingsDatabase.getUrl();
        new DateWebservice(valid_url, SalesReturnHeader.this);
        new SalesOrderWebService(valid_url);

        cmpnyCode = SupplierSetterGetter.getCompanyCode();
        select_van = SOTDatabase.getVandriver();
        appPrintGroup = SalesOrderSetGet.getAppPrintGroup();
        mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
        if (select_van != null && !select_van.isEmpty()) {
        } else {
            select_van = "";
        }
        locationArrHm = new ArrayList<>();
        SRDetailsArr = new ArrayList<HashMap<String, String>>();
        SRHeadersArr = new ArrayList<HashMap<String, String>>();
        SRConsignmentArr =new ArrayList<>();
        haveAttribute = SalesOrderSetGet.getHaveAttribute();

        if(haveAttribute!=null && !haveAttribute.isEmpty()){

        }else{
            haveAttribute="";
        }
        boolean showalllocatoin = FormSetterGetter.isShowAllLocation();
        //showalllocatoin = true;
        if (showalllocatoin == true) {
            locationcode_filter.setFocusableInTouchMode(true);
            locationcode_filter.setBackgroundResource(drawable.edittext_bg);
            locationcode_filter.setCursorVisible(true);
        }else{
            loccode = SalesOrderSetGet.getLocationcode();
            String locName = SalesOrderSetGet.getLocationname();
            locationcode_filter.setText(locName);
            locationcode_filter.setEnabled(false);
            locationcode_filter.setFocusableInTouchMode(false);
            locationcode_filter.setBackgroundResource(R.drawable.labelbg);
            locationcode_filter.setCursorVisible(false);
            locationcode_filter.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        }
// @Offline
        onlineMode = OfflineDatabase.getOnlineMode();
        offlineDatabase = new OfflineDatabase(SalesReturnHeader.this);
        offlineCommon = new OfflineCommon(SalesReturnHeader.this);
        checkOffline = OfflineCommon.isConnected(SalesReturnHeader.this);
        OfflineDatabase.init(SalesReturnHeader.this);
        offlineLayout = (LinearLayout) findViewById(R.id.sr_offlineLayout);
        spManager = new OfflineSettingsManager(SalesReturnHeader.this);
        companyCode = spManager.getCompanyType();

        // Offline
        if (onlineMode.matches("True")) {
            offlineLayout.setVisibility(View.GONE);
        } else if (onlineMode.matches("False")) {
            offlineLayout.setVisibility(View.VISIBLE);
        }

        AsyncCallWSSalesOrder salesOAC = new AsyncCallWSSalesOrder();
        salesOAC.execute();

        registerForContextMenu(so_lv);
        locationcode_filter
                .setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
                        locationcode_filter) {
                    @Override
                    public boolean onDrawableClick() {
                        if(locationArrHm.size()>0){
                            alertDialogLocation();
                        }else{
                            new ShowAllLocation().execute();
                        }
                        return true;
                    }
                });
        edCustomerCode
                .setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
                        edCustomerCode) {
                    @Override
                    public boolean onDrawableClick() {
                        alertDialogSearch();
                        return true;
                    }
                });
        final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, monthOfYear);
                startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                strtDate();
            }
        };

        final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, monthOfYear);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                edDate();
            }
        };

        starteditTextDate.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    new DatePickerDialog(SalesReturnHeader.this, startDate,
                            startCalendar.get(Calendar.YEAR), startCalendar
                            .get(Calendar.MONTH), startCalendar
                            .get(Calendar.DAY_OF_MONTH)).show();
                return false;
            }
        });

        endeditTextDate.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    new DatePickerDialog(SalesReturnHeader.this, endDate,
                            endCalendar.get(Calendar.YEAR), endCalendar
                            .get(Calendar.MONTH), endCalendar
                            .get(Calendar.DAY_OF_MONTH)).show();
                return false;
            }
        });
        btcstmrsrch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                cuscode = edCustomerCode.getText().toString();
                sDate = starteditTextDate.getText().toString();
                eDate = endeditTextDate.getText().toString();

//				if (sDate.matches("")) {
//					Toast.makeText(SalesReturnHeader.this, "Enter Start Date",
//							Toast.LENGTH_SHORT).show();
//				} else if (eDate.matches("")) {
//					Toast.makeText(SalesReturnHeader.this, "Enter End Date",
//							Toast.LENGTH_SHORT).show();
//				} else {
                AsyncCallWSSearchCustCode searchCustCode = new AsyncCallWSSearchCustCode();
                searchCustCode.execute();
//				}
            }
        });
        search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                InputMethodManager inputMethodManager = (InputMethodManager) SalesReturnHeader.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

                if (searchCstmrlayout.getVisibility() == View.VISIBLE) {
                    searchCstmrlayout.setVisibility(View.GONE);
                    inputMethodManager.hideSoftInputFromWindow(
                            codelayout.getWindowToken(), 0);
                } else {
                    searchCstmrlayout.setVisibility(View.VISIBLE);
                    edCustomerCode.requestFocus();
                    inputMethodManager.toggleSoftInputFromWindow(
                            codelayout.getApplicationWindowToken(),
                            InputMethodManager.SHOW_FORCED, 0);
                }
            }
        });

        printer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                printid = v.getId();
                SOTDatabase.deleteImage();

                String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
                Log.d("printertype", printertype);
                if (printertype.matches("Zebra iMZ320") || (printertype.matches("4 Inch Bluetooth")) ||
                        printertype.matches("Zebra iMZ320 4 Inch")) {
                    cursor = FWMSSettingsDatabase.getPrinter();
                    if (cursor.getCount() != 0) {
                        if (RowItem.getPrintoption().equals("True")) {
                            SO so = sradapter.getItem(sradapter
                                    .getSelectedPosition());
                            sosno = so.getSno().toString();
                            helper.showProgressDialog(R.string.generating_sr);
                          //  AsyncPrintCall task = new AsyncPrintCall();
                          //  task.execute();
                            new AsyncCatSub().execute();
                        } else {
                            Toast.makeText(SalesReturnHeader.this,
                                    "Please Enable CheckBox", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(SalesReturnHeader.this,
                                "Please Configure the printer", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(SalesReturnHeader.this,
                            "Please select zebra or 4 inch bluetooth in settings screen", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });

        addnew.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                clearSetterGetter();
                Intent i = new Intent(SalesReturnHeader.this,
                        SalesReturnCustomer.class);
                startActivity(i);
                SalesReturnHeader.this.finish();
            }

        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        SO so = sradapter.getItem(info.position);
        sosno = so.getSno().toString();
        sodate = so.getDate().toString();
        socustomercode = so.getCustomerCode().toString();
        tranType =so.getSalesType().toString();

        menu.setHeaderTitle(sosno);
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Print Preview");
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {

        if (item.getTitle() == "Print Preview") {
            helper.showProgressDialog(R.string.salesreturn_printpreview);
            new AsyncPrintCall().execute();
            for (HashMap<String, String> hashMap : al) {
                for (String key : hashMap.keySet()) {
                    if (key.equals(socustomercode)) {
                        System.out.println(hashMap.get(key));
                        socustomername = hashMap.get(key);
                    }
                }

            }

        } else if (item.getTitle() == "Edit") {
//            clearSetterGetter();
            SOTDatabase.deleteallbatch();
            SOTDatabase.deleteAttribute();
            for (HashMap<String, String> hashMap : al) {
                for (String key : hashMap.keySet()) {
                    if (key.equals(socustomercode)) {
                        System.out.println(hashMap.get(key));
                        socustomername = hashMap.get(key);
                    }
                }

            }
            SalesOrderSetGet.setCustname(socustomername);
            new AsyncCallWSGetSignature(true).execute();
        }
        else if(item.getTitle() == "Delete"){
            deleteAlertDialog();
        }
        else {
            return false;
        }
        return true;
    }

    private void deleteAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                SalesReturnHeader.this);
        builder.setTitle("Confirm Delete");
        builder.setIcon(R.mipmap.delete);
        builder.setMessage("Do you want to delete?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                new AsyncCallWSSalesDelete().execute();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private class AsyncCallWSGetSignature extends AsyncTask<Void, Void, Void> {

        boolean isPrintCall;

        public AsyncCallWSGetSignature(boolean printCall) {
            isPrintCall = printCall;
        }
        String dialogStatus="";
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            spinnerLayout = new LinearLayout(SalesReturnHeader.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            SalesReturnHeader.this.addContentView(spinnerLayout, new LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(salesO_parent, false);
            progressBar = new ProgressBar(SalesReturnHeader.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);
            getPhotoimage = "";
            getSignatureimage = "";
            dialogStatus = checkInternetStatus();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String cmpy = SupplierSetterGetter.getCompanyCode();
            params = new HashMap<String, String>();
            params.put("CompanyCode", cmpy);
            params.put("InvoiceNo", sosno);
            params.put("TranType", "SR");
            Log.d("sosno", "" + sosno);

            if (onlineMode.matches("True")) {
                if (checkOffline == true) { //temp_offline

                    if (dialogStatus.matches("true")) {
                        //need
                    } else {
                        if(mobileHaveOfflineMode.matches("1")){
                            finish();
                        }
                     //   finish();
                    }

                } else {  //Onllineine

            new XMLAccessTask(SalesReturnHeader.this, valid_url,
                    "fncGetInvoicePhoto", params, false,
                    new GetDeliveryImage()).execute();

            new XMLAccessTask(SalesReturnHeader.this, valid_url,
                    "fncGetInvoiceSignature", params, false,
                    new GetDeliverySignature(isPrintCall)).execute();

                }

            } else if (onlineMode.matches("False")) {  // permanent_offline

                //need
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            new AsyncCallWSEditSR().execute();

        }
    }

    public class GetDeliveryImage implements XMLAccessTask.CallbackInterface {
        public GetDeliveryImage() {
        }

        @Override
        public void onSuccess(NodeList nl) {

            getPhotoimage = "";
            for (int i = 0; i < nl.getLength(); i++) {

                Element e = (Element) nl.item(i);
                getPhotoimage = XMLParser.getValue(e, "RefPhoto");
            }

            Log.d("getPhotoimage", "getPhotoimage" + getPhotoimage);
        }

        @Override
        public void onFailure(XMLAccessTask.ErrorType error) {
            onError(error);
        }

    }

    public class GetDeliverySignature implements XMLAccessTask.CallbackInterface {
        boolean isPrintCall;

        public GetDeliverySignature(boolean printCall) {
            isPrintCall = printCall;
        }

        @Override
        public void onSuccess(NodeList nl) {

            getSignatureimage = "";
            for (int i = 0; i < nl.getLength(); i++) {

                Element e = (Element) nl.item(i);
                getSignatureimage = XMLParser.getValue(e, "RefSignature");
            }

            Log.d("getSignatureimage", "getSignatureimage" + getSignatureimage);
            Log.d("getPhotoimage", "getPhotoimage" + getPhotoimage);

            if (isPrintCall) {
                SOTDatabase.storeImage(1, getSignatureimage, getPhotoimage);
                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(salesO_parent, true);

            }
        }

        @Override
        public void onFailure(XMLAccessTask.ErrorType error) {

            if (isPrintCall) {
                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(salesO_parent, true);
            }
            onError(error);
        }

    }

    private void onError(final XMLAccessTask.ErrorType error) {
        new Thread() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (error == XMLAccessTask.ErrorType.NETWORK_UNAVAILABLE) {
                            helper.showLongToast(R.string.error_showing_image_no_network_connection);
                        } else {

                        }
                    }
                });
            }
        }.start();
    }

    private class AsyncCallWSEditSR extends AsyncTask<Void, Void, Void> {
        String dialogStatus="",sales_prodCode;
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            dialogStatus = checkInternetStatus();
            SRDetailsArr.clear();
            SRHeadersArr.clear();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Log.d("Invoice No:", sosno);
            try {

                if (onlineMode.matches("True")) {
                    if (checkOffline == true) { //temp_offline

                        if (dialogStatus.matches("true")) {
                            SRHeadersArr = offlineDatabase.getSRHeader(sosno);
                            SRDetailsArr = offlineDatabase.getSRDetails(sosno);
                        } else {
                            if(mobileHaveOfflineMode.matches("1")){
                                finish();
                            }
                            //finish();
                        }

                    } else {  //Online
                        SRDetailsArr = SalesOrderWebService.getSRDetails(sosno,
                                "fncGetSalesReturnDetail");
                        SRHeadersArr = SalesOrderWebService.getSRHeader(sosno,
                                "fncGetSalesReturnHeaderByInvoiceNo");

                        if(tranType.matches("C")){

                            if (SRDetailsArr != null) {
                                for (int i = 0; i < SRDetailsArr.size(); i++) {
                                    sales_prodCode = SRDetailsArr.get(i).get("ProductCode");
                                }
                            }
                            Log.d("CustomerCode", "-->"  + sales_prodCode);
                            SRConsignmentArr = SalesOrderWebService.
                                    getStockConsignmentArrs("fncGetConsignmentProductStock", socustomercode, sales_prodCode);
                        }

                        SalesOrderWebService.getCustomerTax(socustomercode, "fncGetCustomer");
                    }

                } else if (onlineMode.matches("False")) {  // permanent_offline
                    SRHeadersArr = offlineDatabase.getSRHeader(sosno);
                    SRDetailsArr = offlineDatabase.getSRDetails(sosno);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if(haveAttribute.equalsIgnoreCase("2")){
                new GetSOAttributeDetail().execute();
            }else {
                Log.d("getSalesType","-->"+tranType);
                if(tranType.matches("C")){
                    Intent i = new Intent(SalesReturnHeader.this,
                            ConsignmentSummary.class);
                    i.putExtra("SRDetails", SRDetailsArr);
                    i.putExtra("SRHeader", SRHeadersArr);
//                    i.putExtra("SRConsignment",SRConsignmentArr);
                    i.putExtra("getSignatureimage", getSignatureimage);
                    i.putExtra("getPhotoimage", getPhotoimage);
                    startActivity(i);
                    SalesOrderSetGet.setTranType("COR");
                    SalesReturnHeader.this.finish();
//                    progressBar.setVisibility(View.GONE);
//                    spinnerLayout.setVisibility(View.GONE);
//                    enableViews(salesO_parent, true);
                }else {

                    Intent i = new Intent(SalesReturnHeader.this,
                            SalesReturnSummary.class);
                    i.putExtra("SRDetails", SRDetailsArr);
                    i.putExtra("SRHeader", SRHeadersArr);
                    i.putExtra("getSignatureimage", getSignatureimage);
                    i.putExtra("getPhotoimage", getPhotoimage);
                    startActivity(i);
                    SalesOrderSetGet.setHeader_flag("SalesReturnHeader");
                    finish();
                }
                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(salesO_parent, true);
            }

        }
    }

    private class GetSOAttributeDetail extends  AsyncTask<Void, Void, Void> {
        String dialogStatus,mFlag="",cmpnyCode="",response="";
        HashMap<String, String> hm;
        public GetSOAttributeDetail() {
        }
        @Override
        protected void onPreExecute() {
            //mAttributeProdWithSno.clear();
            mGetSOAttributeDetail.clear();
            dialogStatus = checkInternetStatus();
            hm = new HashMap<String, String>();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                cmpnyCode = SupplierSetterGetter.getCompanyCode();
                hm.put("CompanyCode", cmpnyCode);
                hm.put("SalesReturnNo", sosno);
                if (onlineMode.matches("True")) {
                    if (checkOffline == true) { //temp_offline

                        if (dialogStatus.matches("true")) {

                        } else {

                            if(mobileHaveOfflineMode.matches("1")){
                                finish();
                            }
                        }

                    } else {  //Online
                        response = SalesOrderWebService.getSODetail(hm, "fncGetSalesReturnAttributeDetail");
                    }

                } else if (onlineMode.matches("False")) {  // permanent_offline

                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
		/*	ArrayList<Product> mDistinctAttribueArr = new ArrayList<>();
			mDistinctAttribueArr.add(mAttributeProdWithSno.get(0));
			for (Product Product : mAttributeProdWithSno) {
				boolean flag = false;
				for (Product productUnique : mDistinctAttribueArr) {
					if (productUnique.getSlNo().equals(Product.getSlNo()) && productUnique.getProductCode().equals(Product.getProductCode())) {
						flag = true;
					}
				}
				if (!flag)
					mDistinctAttribueArr.add(Product);

			}
			//set SlNo
			for (Product product : mDistinctAttribueArr) {
				for (Product productAttr : mGetSOAttributeDetail) {
					if (productAttr.getProductCode().equals(product.getProductCode())) {
						productAttr.setSlNo(product.getSlNo());
					}
				}
			}*/
		try {
            Log.d("SOAttributeDetail ", "-->" + response);
            if (response != null) {
                JSONObject mJsonObject = new JSONObject(response);
                JSONArray mJsonArray = mJsonObject.optJSONArray("SODetails");
                int jsonLength = mJsonArray.length();
                if (jsonLength > 0) {
                    for (int i = 0; i < jsonLength; i++) {
                        Product product = new Product();
                        mJsonObject = mJsonArray.getJSONObject(i);
                        String slno = mJsonObject.getString("slNo");
                        String productCode = mJsonObject.getString("ProductCode");
                        if (slno != null && !slno.isEmpty()) {
                        } else {
                            slno = "0";
                        }
                        product.setSlNo(slno);
                        product.setProductCode(productCode);
                        product.setColorCode(mJsonObject.getString("ColorCode"));
                        product.setColorName(mJsonObject.getString("ColorName"));
                        product.setSizeCode(mJsonObject.getString("SizeCode"));
                        product.setSizeName(mJsonObject.getString("SizeName"));
                        product.setcQty(mJsonObject.getString("CQty"));
                        product.setLqty(mJsonObject.getString("LQty"));
                        String qty = mJsonObject.getString("Qty");
                        if (qty != null && !qty.isEmpty()) {

                        } else {
                            qty = "0";
                        }
                        product.setQty(qty);
                        product.setPrice(mJsonObject.getString("Price"));
						/*if(slno!=null && !slno.isEmpty()){
							mAttributeProdWithSno.add(product);
						}*/
                        mGetSOAttributeDetail.add(product);
                    }
                }
            }
        }catch (Exception e){
                e.printStackTrace();
            }

            Log.d("Attribute", "-->" + mGetSOAttributeDetail.size());

            Log.d("getSalesType","-->"+tranType);
            if(tranType.matches("C")){
                Intent i = new Intent(SalesReturnHeader.this,
                        ConsignmentSummary.class);
                i.putExtra("SRDetails", SRDetailsArr);
                i.putExtra("SRHeader", SRHeadersArr);
//                i.putExtra("SRConsignment",SRConsignmentArr);
                i.putExtra("getSignatureimage", getSignatureimage);
                i.putExtra("getPhotoimage", getPhotoimage);
                startActivity(i);
                SalesOrderSetGet.setTranType("COR");
                SalesOrderSetGet.setHeader_flag("SalesReturnHeader");
                SalesReturnHeader.this.finish();

            }else {

                Intent i = new Intent(SalesReturnHeader.this,
                        SalesReturnSummary.class);
                i.putExtra("SRDetails", SRDetailsArr);
                i.putExtra("SRHeader", SRHeadersArr);
                i.putExtra("getSignatureimage", getSignatureimage);
                i.putExtra("getPhotoimage", getPhotoimage);
                startActivity(i);
                SalesOrderSetGet.setHeader_flag("SalesReturnHeader");
                finish();

            }
            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(salesO_parent, true);

        }

    }

    private void strtDate() {

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        starteditTextDate.setText(sdf.format(startCalendar.getTime()));
    }

    private void edDate() {

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        endeditTextDate.setText(sdf.format(endCalendar.getTime()));
    }

    private class AsyncCallWSSalesOrder extends AsyncTask<Void, Void, Void> {
        String dialogStatus;
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            al.clear();
            dialogStatus = checkInternetStatus();
            spinnerLayout = new LinearLayout(SalesReturnHeader.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            SalesReturnHeader.this.addContentView(spinnerLayout,
                    new LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(salesO_parent, false);
            progressBar = new ProgressBar(SalesReturnHeader.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // Offline
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
            String finalDate = timeFormat.format(new Date());
            System.out.println(finalDate);

            if (onlineMode.matches("True")) {
                if (checkOffline == true) { //temp_offline

                    if (dialogStatus.matches("true")) {
                        al = getCustomerOffline();
                        serverdate = finalDate;
                    } else {
                        if(mobileHaveOfflineMode.matches("1")){
                            finish();
                        }
                        finish();
                    }

                } else {  //Online
                    String companyCode = SupplierSetterGetter.getCompanyCode();
                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("CompanyCode", companyCode);
                    hm.put("VanCode", select_van);

                    al = SalesOrderWebService.getWholeCustomer(hm, "fncGetCustomerForSearch");
                    serverdate = DateWebservice.getDateService("fncGetServerDate");
                }

            } else if (onlineMode.matches("False")) {  // permanent_offline
                al = getCustomerOffline();
                serverdate = finalDate;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (serverdate != null) {
                starteditTextDate.setText("");  // added new
                endeditTextDate.setText("");
                AsyncCallWSSOHeader task = new AsyncCallWSSOHeader();
                task.execute();
            }else{
                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(salesO_parent, true);
            }
        }
    }

    public ArrayList<HashMap<String, String>> getCustomerOffline() {
        String customer_jsonString = "";
        HashMap<String, String> customerhashValue = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> custList = new ArrayList<HashMap<String, String>>();

        String cmpnyCode = SupplierSetterGetter.getCompanyCode();

        customerhashValue.put("CompanyCode", cmpnyCode);
        customerhashValue.put("CustomerCode", "");
        customerhashValue.put("NeedOutstandingAmount", "");
        customerhashValue.put("AreaCode", "");
        customerhashValue.put("VanCode", select_van);

        customer_jsonString = OfflineDatabase
                .getCustomersList(customerhashValue);

        try {

            JSONObject customer_jsonResponse = new JSONObject(
                    customer_jsonString);
            JSONArray customer_jsonMainNode = customer_jsonResponse
                    .optJSONArray("JsonArray");

            int lengthJsonArr = customer_jsonMainNode.length();
            Log.d("customerlist size", "-->" + lengthJsonArr);
            for (int i = 0; i < lengthJsonArr; i++) {

                JSONObject jsonChildNode;
                try {

                    jsonChildNode = customer_jsonMainNode.getJSONObject(i);
                    String cust_code = jsonChildNode.optString("CustomerCode")
                            .toString();
                    String cust_Name = jsonChildNode.optString("CustomerName")
                            .toString();

                    String ReferenceLocation = jsonChildNode.optString(
                            "ReferenceLocation").toString();

                    Log.d("ReferenceLocation", ""+ReferenceLocation);

                    HashMap<String, String> customerhm = new HashMap<String, String>();
                    if(ReferenceLocation!=null && !ReferenceLocation.isEmpty()){
                        customerhm.put(cust_code, cust_Name+"/"+ReferenceLocation);
                    }else{
                        customerhm.put(cust_code, cust_Name);
                    }

//					HashMap<String, String> customerhm = new HashMap<String, String>();
//					customerhm.put(cust_code, cust_Name);
                    custList.add(customerhm);

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return custList;
    }

    public void alertDialogSearch() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(
                SalesReturnHeader.this);
        final EditText editText = new EditText(SalesReturnHeader.this);
        final ListView listview = new ListView(SalesReturnHeader.this);
        LinearLayout layout = new LinearLayout(SalesReturnHeader.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        myDialog.setTitle("Customer");
        editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,
                0, 0);
        layout.addView(editText);
        layout.addView(listview);
        myDialog.setView(layout);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    myalertDialog
                            .getWindow()
                            .setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        arrayAdapterSupp = new CustomAlertAdapterSupp(SalesReturnHeader.this,
                al);
        listview.setAdapter(arrayAdapterSupp);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                myalertDialog.dismiss();
                getArraylsit = arrayAdapterSupp.getArrayList();
                HashMap<String, String> datavalue = getArraylsit.get(position);
                Set<Entry<String, String>> keys = datavalue.entrySet();
                Iterator<Entry<String, String>> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    @SuppressWarnings("rawtypes")
                    Entry mapEntry = iterator.next();
                    String keyValues = (String) mapEntry.getKey();
                    String name = (String) mapEntry.getValue();

                    edCustomerCode.setText(keyValues);
                    sl_namefield.setText(name);
                    edCustomerCode.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable s) {

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s,
                                                      int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {
                            textlength = edCustomerCode.getText().length();
                            sl_namefield.setText("");
                        }
                    });
                }
            }
        });

        searchResults = new ArrayList<HashMap<String, String>>(al);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                textlength = editText.getText().length();
                searchResults.clear();
                for (int i = 0; i < al.size(); i++) {
                    String supplierName = al.get(i).toString();
                    if (textlength <= supplierName.length()) {
                        if (supplierName.toLowerCase().contains(
                                editText.getText().toString().toLowerCase()
                                        .trim()))
                            searchResults.add(al.get(i));
                    }
                }

                arrayAdapterSupp = new CustomAlertAdapterSupp(
                        SalesReturnHeader.this, searchResults);
                listview.setAdapter(arrayAdapterSupp);
            }
        });
        myDialog.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        myalertDialog = myDialog.show();
    }

    private class AsyncCallWSSearchCustCode extends AsyncTask<Void, Void, Void> {
        @SuppressWarnings("deprecation")
        String dialogStatus;
        @Override
        protected void onPreExecute() {
            spinnerLayout = new LinearLayout(SalesReturnHeader.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            SalesReturnHeader.this.addContentView(spinnerLayout,
                    new LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(salesO_parent, false);
            progressBar = new ProgressBar(SalesReturnHeader.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);
            dialogStatus = checkInternetStatus();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (onlineMode.matches("True")) {
                if (checkOffline == true) { //temp_offline

                    if (dialogStatus.matches("true")) {
//                        list = offlineSRHeader(cuscode, sDate, eDate, status);
                    } else {
                        if(mobileHaveOfflineMode.matches("1")){
                            finish();
                        }
                     //   finish();
                    }

                } else {  //Online
                    searchCstCdArr = SalesOrderWebService.SearchSRCustCode(cuscode,
                            sDate, eDate, select_van,loccode, "fncGetSalesReturnHeader");
                }

            } else if (onlineMode.matches("False")) {  // permanent_offline

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (!searchCstCdArr.isEmpty()) {
                try {
                    searchCustCode();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                searchCstCdArr.clear();
                try {
                    searchCustCode();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Toast.makeText(SalesReturnHeader.this, "No matches found",
                        Toast.LENGTH_SHORT).show();
            }

            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(salesO_parent, true);
        }
    }


    /*public ArrayList<SO> offlineSRHeader(String customercode, String startmonthdate, String lastmonthdate) throws ParseException {

        ArrayList<SO> resultlist = new ArrayList<SO>();
        Log.d("Offline Search", "Offline Search");

        Date startDate=null,endDate=null;
        String from="",to="";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        if(startmonthdate!=null && !startmonthdate.isEmpty()){
            startDate  = dateFormat.parse(startmonthdate);
        }

        if(lastmonthdate!=null && !lastmonthdate.isEmpty()){
            endDate  = dateFormat.parse(lastmonthdate);
        }

        SimpleDateFormat regateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(startDate!=null){
            from = regateFormat.format(startDate)+" 00:00:00";
        }
        if(endDate!=null){
            to = regateFormat.format(endDate)+" 24:00:00";
        }

        Log.d("fromDate", from);
        Log.d("toDate", to);

        HashMap<String, String> hm = new HashMap<String, String>();
        hm.put("CustomerCode", customercode);
        hm.put("FromDate", from);
        hm.put("ToDate", to);

        resultlist = OfflineDatabase.getSRHeaderList(hm);

        Log.d("resultlist Size", resultlist.size()+"");

        return resultlist;
    }*/

    private void enableViews(View v, boolean enabled) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                enableViews(vg.getChildAt(i), enabled);
            }
        }
        v.setEnabled(enabled);
    }

    public void searchCustCode() throws ParseException {

        sradapter = new HeaderAdapter(SalesReturnHeader.this,
                R.layout.invoice_list_item_latest, null, searchCstCdArr);
        so_lv.setAdapter(sradapter);

        if (SO.getTotalamount() > 0) {
            totaloutstanding.setText(twoDecimalPoint(SO.getTotalamount()));
        } else {
            totaloutstanding.setText("0.00");
        }
    }
    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String pt = df.format(d);
        return pt;
    }
    private class AsyncCallWSSOHeader extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            double balanceAmount  = 0.00;
            String fromdate = DateTime.date(serverdate);
            String cmpnyCode = SupplierSetterGetter.getCompanyCode();

            SoapObject request = new SoapObject(NAMESPACE, webMethName);

            PropertyInfo sDate = new PropertyInfo();
            PropertyInfo eDate = new PropertyInfo();
            PropertyInfo companyCode = new PropertyInfo();
            PropertyInfo VanCode = new PropertyInfo();
            PropertyInfo LocationCode = new PropertyInfo();
            companyCode.setName("CompanyCode");
            companyCode.setValue(cmpnyCode);
            companyCode.setType(String.class);
            request.addProperty(companyCode);

            sDate.setName("FromDate");
            sDate.setValue(fromdate);
            sDate.setType(String.class);
            request.addProperty(sDate);

            eDate.setName("ToDate");
            eDate.setValue(serverdate);
            eDate.setType(String.class);
            request.addProperty(eDate);

            VanCode.setName("VanCode");
            VanCode.setValue(select_van);
            VanCode.setType(String.class);
            request.addProperty(VanCode);

            LocationCode.setName("LocationCode");
            LocationCode.setValue(loccode);
            LocationCode.setType(String.class);
            request.addProperty(LocationCode);

            String suppTxt = null;
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.bodyOut = request;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(
                    valid_url);
            try {
                androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                suppTxt = response.toString();
                headeresult = " { SRHeader : " + suppTxt + "}";
                JSONObject jsonResponse;

                try {
                    jsonResponse = new JSONObject(headeresult);
                    JSONArray jsonMainNode = jsonResponse
                            .optJSONArray("SRHeader");

                    int lengthJsonArr = jsonMainNode.length();
                    list = new ArrayList<SO>();
                    for (int i = 0; i < lengthJsonArr; i++) {

                        JSONObject jsonChildNode = jsonMainNode
                                .getJSONObject(i);

                        String ccSno = jsonChildNode.optString("SalesReturnNo")
                                .toString();
                        String ccDate = jsonChildNode.optString(
                                "SalesReturnDate").toString();
                        String customerCode = jsonChildNode.optString(
                                "CustomerCode").toString();
                        String customerName = jsonChildNode.optString(
                                "CustomerName").toString();
                        String amount = jsonChildNode.optString("NetTotal")
                                .toString();
                        String balanceamount = jsonChildNode.optString(
                                "BalanceAmount").toString();
                        String SalesType = jsonChildNode.optString(
                                "SalesType").toString();
                        SO so = new SO();
                        so.setSno(ccSno);
                        so.setCustomerCode(customerCode);
                        so.setNettotal(amount);
                        so.setBalanceamount(balanceamount);
                        so.setSalesType(SalesType);
                        so.setCustomerName(customerName);

                        if (ccDate != null) {
                            StringTokenizer tokens = new StringTokenizer(
                                    ccDate, " ");
                            String date = tokens.nextToken();
                            so.setDate(date);
                        } else {
                            so.setDate(ccDate);
                        }

                        if (balanceamount != null && !balanceamount.isEmpty()) {
                            double balanceAmt = Double.parseDouble(balanceamount);
                            if(balanceAmt>0){
                                balanceAmount = balanceAmount + balanceAmt;
                            }
                        }

                        list.add(so);
                    }
                    SO.setTotalamount(balanceAmount);
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                suppTxt = "Error occured";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            try {
                headerCustCode();
            } catch (ParseException e) {

                e.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(salesO_parent, true);

        }
    }

    public void headerCustCode() throws ParseException {
//        if (!list.isEmpty()) {
            sradapter = new HeaderAdapter(SalesReturnHeader.this,
                    R.layout.invoice_list_item_latest, null, list);
            so_lv.setAdapter(sradapter);

            if (SO.getTotalamount() > 0) {
                totaloutstanding.setText(twoDecimalPoint(SO.getTotalamount()));
            } else {
                totaloutstanding.setText("0.00");
            }
//        }

    }

    public void clearSetterGetter() {
        SalesOrderSetGet.setCustomercode("");
        SalesOrderSetGet.setCustomername("");
        SalesOrderSetGet.setSaleorderdate("");
        SalesOrderSetGet.setCurrencycode("");
        SalesOrderSetGet.setCurrencyname("");
        SalesOrderSetGet.setCurrencyrate("");
        SalesOrderSetGet.setRemarks("");
        SalesOrderSetGet.setSrinvoiceno("");

        SOTDatabase.init(SalesReturnHeader.this);
        SOTDatabase.deleteImage();
        SOTDatabase.deleteAttribute();
        SOTDatabase.deleteAllProduct();
        SOTDatabase.deleteBillDisc();
        ConvertToSetterGetter.setEdit_salesreturn_no("");
    }

    private class AsyncCatSub extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            sortCatSubCat.clear();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (appPrintGroup.matches("C")) {
                 jsonString = WebServiceClass.URLService("fncGetCategory");
                Log.d("jsonString ", jsonString);
                try {

                    jsonResponse = new JSONObject(jsonString);
                    jsonMainNode = jsonResponse.optJSONArray("JsonArray");

                } catch (JSONException e) {

                    e.printStackTrace();
                }
                /*********** Process each JSON Node ************/
                int lengthJsonArr = jsonMainNode.length();
                for (int i = 0; i < lengthJsonArr; i++) {
                    /****** Get Object for each JSON node. ***********/
                    JSONObject jsonChildNode;

                    try {
                        jsonChildNode = jsonMainNode.getJSONObject(i);

                        String categorycode = jsonChildNode.optString(
                                "CategoryCode").toString();

                        sortCatSubCat.add(categorycode);
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }

            } else if (appPrintGroup.matches("S")) {
                 jsonStr = WebServiceClass.URLService("fncGetSubCategory");
                Log.d("jsonStr ", jsonStr);

                try {

                    jsonResponse = new JSONObject(jsonStr);
                    jsonSecNode = jsonResponse.optJSONArray("JsonArray");

                    int lengJsonArr = jsonSecNode.length();
                    for (int i = 0; i < lengJsonArr; i++) {

                        JSONObject jsonChildNode;

                        try {
                            jsonChildNode = jsonSecNode.getJSONObject(i);

                            String subcategorycode = jsonChildNode.optString(
                                    "SubCategoryCode").toString();
                            sortCatSubCat.add(subcategorycode);
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        new AsyncPrintCall().execute();
        }

    }
    public void sortCatSubCat() {
        for (int i = 0; i < sortCatSubCat.size(); i++) {
            String catSub = sortCatSubCat.get(i).toString();
            for (ProductDetails products : product) {

                if (catSub.matches(products.getSortproduct())) {

                    sort.add(catSub);
                }
            }
        }
        hs.addAll(sort);
        printsortHeader.clear();
        printsortHeader.addAll(hs);
    }
    private class AsyncPrintCall extends
            AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            product.clear();
            productdet.clear();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... arg0) {

            String cmpnyCode = SupplierSetterGetter.getCompanyCode();
            String showcartonloose = SalesOrderSetGet
                    .getCartonpriceflag();
            hashValue.put("CompanyCode", cmpnyCode);
            hashValue.put("SRNumber", sosno);

            if (showcartonloose.equalsIgnoreCase("1")) {
                jsonString = SalesOrderWebService.getSODetail(
                        hashValue, "fncGetSalesReturnDetailWithCarton");
            } else {
                jsonString = SalesOrderWebService.getSODetail(
                        hashValue, "fncGetSalesReturnDetail");
            }

//            jsonString = SalesOrderWebService.getSODetail(hashValue,
//                    "fncGetSalesReturnDetail");
            jsonStr = SalesOrderWebService.getSODetail(hashValue,
                    "fncGetSalesReturnHeaderByInvoiceNo");

            Log.d("jsonString ", jsonString);
            Log.d("jsonStr ", jsonStr);

            try {
                SalesOrderWebService.getCustomerTax(socustomercode, "fncGetCustomer");

                jsonResponse = new JSONObject(jsonString);
                jsonMainNode = jsonResponse.optJSONArray("SODetails");

                jsonResp = new JSONObject(jsonStr);
                jsonSecNode = jsonResp.optJSONArray("SODetails");

            } catch (JSONException e) {

                e.printStackTrace();
            }

            int lengthJsonArr = jsonMainNode.length();
            if (showcartonloose.equalsIgnoreCase("1")) {
                for (int i = 0; i < lengthJsonArr; i++) {
                    /****** Get Object for each JSON node. ***********/
                    ProductDetails productdetail = new ProductDetails();
                    try {
                        JSONObject jsonChildNode = jsonMainNode
                                .getJSONObject(i);
                      String transType = jsonChildNode.optString("TranType")
                                .toString();
                        int s = i + 1;
                        productdetail.setSno(String.valueOf(s));
                        String slNo = jsonChildNode.optString("slNo")
                                .toString();
                        String productCode = jsonChildNode.optString(
                                "ProductCode").toString();
                        productdetail.setItemcode(productCode);

                        productdetail.setDescription(jsonChildNode.optString(
                                "ProductName").toString());
                        productdetail.setUOMCode(jsonChildNode.optString(
                                "UOMCode").toString());

                        String uomCode = jsonChildNode.optString("UOMCode")
                                .toString();

                        if (uomCode != null && !uomCode.isEmpty()) {

                        } else {
                            uomCode = "";
                        }

                        if (uomCode.matches("null")) {
                            uomCode = "";
                        }

                        Log.d("uomCode", "u " + uomCode);

                        if (transType.equalsIgnoreCase("Ctn")) {
                            String cQty = jsonChildNode.optString("CQty")
                                    .toString();

                            String cPrice = jsonChildNode.optString(
                                    "CartonPrice").toString();

                            Log.d("cPrice", "-->" + cPrice);

                            if (cQty != null && !cQty.isEmpty()
                                    && cPrice != null && !cPrice.isEmpty()) {
                                productdetail.setQty(cQty.split("\\.")[0]);
                                productdetail.setPrice(twoDecimalPoint(Double
                                        .valueOf(cPrice)));
                                double cqty = Double.valueOf(cQty);
                                double cprice = Double.valueOf(cPrice);
                                double total = cqty * cprice;
                                String tot = twoDecimalPoint(total);
                                productdetail.setTotal(tot);
                            } else {
                                productdetail.setQty("0");
                                productdetail.setPrice("0.00");
                                productdetail.setTotal("0.00");
                            }

                        } else if (transType.equalsIgnoreCase("Loose")) {

                            String lQty = jsonChildNode.optString("LQty")
                                    .toString();
                            String lPrice = jsonChildNode.optString("Price")
                                    .toString();

                            Log.d("lPrice", "--->" + lPrice);

                            if (lQty != null && !lQty.isEmpty()
                                    && lPrice != null && !lPrice.isEmpty()) {
                                productdetail.setQty(lQty.split("\\.")[0] + " "
                                        + uomCode);
                                productdetail.setPrice(twoDecimalPoint(Double
                                        .valueOf(lPrice)));
                                double lqty = Double.valueOf(lQty);
                                double lprice = Double.valueOf(lPrice);
                                double total = lqty * lprice;
                                String tot = twoDecimalPoint(total);
                                productdetail.setTotal(tot);
                            } else {
                                productdetail.setQty("0" + " " + uomCode);
                                productdetail.setPrice("0.00");
                                productdetail.setTotal("0.00");
                            }

                        } else if (transType.equalsIgnoreCase("FOC")) {
                            String focQty = jsonChildNode.optString("FOCQty")
                                    .toString();
                            if (focQty != null && !focQty.isEmpty()) {
                                productdetail.setQty(focQty.split("\\.")[0]);
                            } else {
                                productdetail.setQty("0" + " " + uomCode);
                            }
                            productdetail.setPrice("0.00");
                            productdetail.setTotal("0.00");

                        } else if (transType.equalsIgnoreCase("Exc")) {
                            String excQty = jsonChildNode.optString(
                                    "ExchangeQty").toString();
                            if (excQty != null && !excQty.isEmpty()) {
                                productdetail.setQty(excQty.split("\\.")[0]);
                            } else {
                                productdetail.setQty("0" + " " + uomCode);
                            }
                            productdetail.setPrice("0.00");
                            productdetail.setTotal("0.00");

                        }
//                        String issueQty = jsonChildNode.optString("IssueQty")
//                                .toString();
//                        String returnQty = jsonChildNode.optString("ReturnQty")
//                                .toString();

//                        if (issueQty != null && !issueQty.isEmpty()) {
//
//                            productdetail.setIssueQty(issueQty.split("\\.")[0]);
//                        } else {
//                            productdetail.setIssueQty("0");
//                        }
//                        if (returnQty != null && !returnQty.isEmpty()) {
//
//                            productdetail
//                                    .setReturnQty(returnQty.split("\\.")[0]);
//                        } else {
//                            productdetail.setReturnQty("0");
//                        }
                        if (appPrintGroup.matches("C")) {
                            productdetail.setSortproduct(jsonChildNode
                                    .optString("CategoryCode").toString());
                        } else if (appPrintGroup.matches("S")) {
                            productdetail.setSortproduct(jsonChildNode
                                    .optString("SubCategoryCode").toString());
                        } else if (appPrintGroup.matches("N")) {
                            productdetail.setSortproduct("");
                        } else {
                            productdetail.setSortproduct("");
                        }
                        productdetail.setTax(jsonChildNode.optString("Tax").toString());
                        productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
                        productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
                        productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
                        product.add(productdetail);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }else{
                /******** Print qty and price *********/
                for (int i = 0; i < lengthJsonArr; i++) {
                    /****** Get Object for each JSON node. ***********/
                    JSONObject jsonChildNode;
                    ProductDetails productdetail = new ProductDetails();
                    try {
                        jsonChildNode = jsonMainNode.getJSONObject(i);
                        int s = i + 1;
                        productdetail.setSno(String.valueOf(s));

                        String slNo = jsonChildNode.optString("slNo")
                                .toString();
                        String productCode = jsonChildNode.optString(
                                "ProductCode").toString();
                        productdetail.setItemcode(productCode);

                        productdetail.setDescription(jsonChildNode.optString(
                                "ProductName").toString());

                        String invoiceqty = jsonChildNode.optString("Qty")
                                .toString();
                        if (invoiceqty.contains(".")) {
                            StringTokenizer tokens = new StringTokenizer(
                                    invoiceqty, ".");
                            String qty = tokens.nextToken();
                            productdetail.setQty(qty);
                        } else {
                            productdetail.setQty(invoiceqty);
                        }

                        String pricevalue = jsonChildNode.optString("Price")
                                .toString();
                        Log.d("pricevalue", "--->" + pricevalue);

                        String totalvalve = jsonChildNode.optString("Total")
                                .toString();

//                        String issueQty = jsonChildNode.optString("IssueQty")
//                                .toString();
//                        String returnQty = jsonChildNode.optString("ReturnQty")
//                                .toString();

//                        if (issueQty != null && !issueQty.isEmpty()) {
//
//                            productdetail.setIssueQty(issueQty.split("\\.")[0]);
//                        } else {
//                            productdetail.setIssueQty("0");
//                        }
//                        if (returnQty != null && !returnQty.isEmpty()) {
//
//                            productdetail
//                                    .setReturnQty(returnQty.split("\\.")[0]);
//                        } else {
//                            productdetail.setReturnQty("0");
//                        }

                        productdetail.setFocqty(jsonChildNode
                                .optDouble("FOCQty"));

//                        productdetail.setExchangeqty(jsonChildNode
//                                .optDouble("ExchangeQty"));
                        if (pricevalue.contains(".")) {
                            productdetail.setPrice(pricevalue);
                        } else {
                            productdetail.setPrice(pricevalue + decimalpts);
                        }
                        if (totalvalve.contains(".")) {

                            productdetail.setTotal(totalvalve);
                        } else {

                            productdetail.setTotal(totalvalve + decimalpts);
                        }
                        if (appPrintGroup.matches("C")) {
                            productdetail.setSortproduct(jsonChildNode
                                    .optString("CategoryCode").toString());
                        } else if (appPrintGroup.matches("S")) {
                            productdetail.setSortproduct(jsonChildNode
                                    .optString("SubCategoryCode").toString());
                        } else if (appPrintGroup.matches("N")) {
                            productdetail.setSortproduct("");
                        } else {
                            productdetail.setSortproduct("");
                        }
                        productdetail.setTax(jsonChildNode.optString("Tax").toString());
                        productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());
                        productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
                        productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
                        product.add(productdetail);

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            }
        /*    for (int i = 0; i < lengthJsonArr; i++) {
                JSONObject jsonChildNode;
                ProductDetails productdetail = new ProductDetails();
                try {
                    jsonChildNode = jsonMainNode.getJSONObject(i);

                    productdetail.setItemcode(jsonChildNode.optString(
                            "ProductCode").toString());
                    productdetail.setDescription(jsonChildNode.optString(
                            "ProductName").toString());

                    String salesRetqty = jsonChildNode.optString("Qty")
                            .toString();
                    if (salesRetqty.contains(".")) {
                        StringTokenizer tokens = new StringTokenizer(
                                salesRetqty, ".");
                        String qty = tokens.nextToken();
                        productdetail.setQty(qty);
                    } else {
                        productdetail.setQty(salesRetqty);
                    }

                    productdetail.setPrice(jsonChildNode.optString("Price")
                            .toString());
                    productdetail.setTotal(jsonChildNode.optString("Total")
                            .toString());
                    productdetail.setSubtotal(jsonChildNode.optString("SubTotal")
                            .toString());
                    productdetail.setTax(jsonChildNode.optString("Tax")
                            .toString());
                    productdetail.setTaxType(jsonChildNode.optString("TaxType").toString());
                    productdetail.setTaxPerc(jsonChildNode.optString("TaxPerc").toString());


                    product.add(productdetail);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }*/

            int lengJsonArr = jsonSecNode.length();
            for (int i = 0; i < lengJsonArr; i++) {
                JSONObject jsonChildNode;
                ProductDetails productdetail = new ProductDetails();
                try {
                    jsonChildNode = jsonSecNode.getJSONObject(i);
                    productdetail.setItemdisc(jsonChildNode.optString(
                            "ItemDiscount").toString());
                    productdetail.setBilldisc(jsonChildNode.optString(
                            "BillDIscount").toString());
                    productdetail.setSubtotal(jsonChildNode.optString(
                            "SubTotal").toString());
                    productdetail.setTax(jsonChildNode.optString("Tax")
                            .toString());
                    productdetail.setNettot(jsonChildNode.optString("NetTotal")
                            .toString());
                    productdetail.setRemarks(jsonChildNode.optString("Remarks"));
                    remarks = jsonChildNode.optString("Remarks");
                    productdet.add(productdetail);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            hashValue.clear();
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (printid == R.id.printer) {
                try {
                    printid = 0;
                    sortCatSubCat();
                    print();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                helper.dismissProgressDialog();
                Intent i = new Intent(SalesReturnHeader.this,
                        CommonPreviewPrint.class);
                i.putExtra("title", "SalesReturn");
                i.putExtra("No", sosno);
                i.putExtra("Date", sodate);
                i.putExtra("cus_remarks","");
                i.putExtra("customerCode", socustomercode);
                i.putExtra("customerName", socustomername);
                i.putExtra("Invoicetype", "Consignment");
                i.putExtra("sort", sortCatSubCat);
                if(SalesOrderSetGet.getShortCode().matches("HEZOM")) {
                    i.putExtra("cus_remarks", remarks);
                }
                PreviewPojo.setProducts(product);
                PreviewPojo.setProductsDetails(productdet);
                startActivity(i);
            }
        }

    }

	/*private void print() throws IOException {

		helper.dismissProgressDialog();
		String macaddress = FWMSSettingsDatabase.getPrinterAddress();
		try {
			Printer printer = new Printer(SalesReturnHeader.this, macaddress);
			if (sradapter.getSelectedPosition() != -1) {
				SO so = sradapter.getItem(sradapter.getSelectedPosition());

				sosno = so.getSno().toString();
				sodate = so.getDate().toString();
				socustomercode = so.getCustomerCode().toString();
				soamount = so.getNettotal().toString();
				for (HashMap<String, String> hashMap : al) {
					for (String key : hashMap.keySet()) {
						if (key.equals(socustomercode)) {
							System.out.println(hashMap.get(key));
							socustomername = hashMap.get(key);
						}
					}
				}

				printer.printSalesReturn(sosno, sodate, socustomercode,
						socustomername, product, productdet, 1);
			}
		} catch (IllegalArgumentException e) {
			helper.showLongToast(R.string.error_configure_printer);
		}
	}*/

    private void print() throws IOException {

        String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        try {
            String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
            SO so = sradapter
                    .getItem(sradapter.getSelectedPosition());
            sosno = so.getSno().toString();
            sodate = so.getDate().toString();
            socustomercode = so.getCustomerCode().toString();
//			socustomername = so.getCustomerName().toString();
            soamount = so.getNettotal().toString();

            for (HashMap<String, String> hashMap : al) {
                for (String key : hashMap.keySet()) {
                    if (key.equals(socustomercode)) {
                        System.out.println(hashMap.get(key));
                        socustomername = hashMap.get(key);
                    }
                }
            }

            if (printertype.matches("Zebra iMZ320")) {

                helper.dismissProgressDialog();
                Printer printer = new Printer(SalesReturnHeader.this, macaddress);
                printer.printSalesReturn(sosno, sodate, socustomercode,
                        socustomername, product, productdet,printsortHeader,appPrintGroup, 1);
            } else if (printertype.matches("4 Inch Bluetooth")) {
                /*helper.showProgressDialog(InvoiceHeader.this.getString(R.string.print),
						InvoiceHeader.this.getString(R.string.creating_file_for_printing));*/
                helper.updateProgressDialog(SalesReturnHeader.this.getString(R.string.creating_file_for_printing));
                if (BluetoothAdapter.checkBluetoothAddress(macaddress)) {
                    BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
                    // Attempt to connect to the device
                    GlobalData.mService.setHandler(mHandler);
                    GlobalData.mService.connect(device, true);

                }
                helper.dismissProgressDialog();
                //helper.dismissProgressDialog();
            }
            else if(printertype.matches("3 Inch Bluetooth Generic")) {
                helper.dismissProgressDialog();
                try {
                    final CubePrint print = new CubePrint(SalesReturnHeader.this, macaddress);
                    print.initGenericPrinter();
                    print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
                        @Override
                        public void initCompleted() {
                            print.printSalesReturn(sosno, sodate, socustomercode,
                                    socustomername, product, productdet, 1);

                            print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
                                @Override
                                public void onCompleted() {
                                    helper.showLongToast(R.string.printed_successfully);
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (printertype.matches("Zebra iMZ320 4 Inch")) {
                helper.dismissProgressDialog();
                PrinterZPL printerZpl = new PrinterZPL(SalesReturnHeader.this, macaddress);

                printerZpl.printSalesReturn(sosno, sodate, socustomercode,
                        socustomername, product, productdet,printsortHeader,appPrintGroup,1);
            }
			/*
			 * for (HashMap<String, String> hashMap : al) { for (String key :
			 * hashMap.keySet()) { if (key.equals(socustomercode)) {
			 * System.out.println(hashMap.get(key)); socustomername =
			 * hashMap.get(key); } }
			 *
			 * }
			 */
        } catch (IllegalArgumentException e) {
            helper.showLongToast(R.string.error_configure_printer);
        }
    }

    public void showViews(boolean show, int... resId) {
        int visibility = show ? View.VISIBLE : View.GONE;
        for (int id : resId) {
            findViewById(id).setVisibility(visibility);
        }

    }

    public static void showViews(boolean show, View... views) {
        int visibility = show ? View.VISIBLE : View.GONE;
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        menu.toggle();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(String item) {
        // TODO Auto-generated method stub
        menu.toggle();
    }

    @Override
    public void onStart() {
        super.onStart();
        //if(D) Log.e(TAG, "--- onStart ---");
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

        if (printertype.matches("4 Inch Bluetooth")) {
            if (!GlobalData.mBluetoothAdapter.isEnabled()) {
                GlobalData.mBluetoothAdapter.enable();
                //Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivity(enableIntent);*/
                // Otherwise, setup the chat session
            } else {
                if (GlobalData.mService == null) {
                    GlobalData.mService = new BluetoothService(this, mHandler);
                }
            }
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case GlobalData.MESSAGE_STATE_CHANGE:
                    Log.d("case", "MESSAGE_STATE_CHANGE");
                    //if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case GlobalData.STATE_CONNECTED:
                            //mTitle.setText(R.string.title_connected_to);
                            //mTitle.append(mConnectedDeviceName);
                            //Intent intent = new Intent(BluetoothActivity.this,PrintActivity.class);
                            //intent.putExtra("COMM", 0);//0-BLUETOOTH
                            // Set result and finish this Activity
                            //	startActivity(intent);
                            Log.d("case", "STATE_CONNECTED");
                            print4Inch();
                            //helper.dismissProgressDialog();
                            break;
                        case GlobalData.STATE_CONNECTING:
                            //mTitle.setText(R.string.title_connecting);
                            Log.d("case", "STATE_CONNECTING");

                            break;
                        case GlobalData.STATE_LISTEN:
                            Log.d("case", "STATE_LISTEN");
                            break;
                        case GlobalData.STATE_NONE:
                            Log.d("case", "STATE_NONE");
                            //mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case GlobalData.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String mConnectedDeviceName = msg.getData().getString("device_name");
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case GlobalData.MESSAGE_TOAST:

                    //String macaddress = FWMSSettingsDatabase.getPrinterAddress();
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();

                    reconnectDialog(msg.getData().getString("toast"));
					/*if (BluetoothAdapter.checkBluetoothAddress(macaddress))
					{
						BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
						// Attempt to connect to the device
						GlobalData.mService.setHandler(mHandler);
						GlobalData.mService.connect(device,true);

						//print4Inch();
					}*/
                    //	helper.dismissProgressDialog();
                    break;
            }
            //helper.dismissProgressDialog();
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        if (GlobalData.mService != null) {
            GlobalData.mService.stop();
        }
    }

    public void reconnectDialog(String msg) {
        final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        final Dialog dialog = new Dialog(SalesReturnHeader.this);

        dialog.setContentView(R.layout.dialog_reconnect);
        dialog.setTitle(msg);
        ImageView reconnect = (ImageView) dialog.findViewById(R.id.reconnect);

        reconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BluetoothAdapter.checkBluetoothAddress(macaddress)) {
                    BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
                    // Attempt to connect to the device
                    GlobalData.mService.setHandler(mHandler);
                    GlobalData.mService.connect(device, true);

                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void print4Inch() {
        CubePrint mPrintCube = new CubePrint(SalesReturnHeader.this, FWMSSettingsDatabase.getPrinterAddress());
        mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
            @Override
            public void onCompleted() {
                helper.showLongToast(R.string.printed_successfully);

            }
        });
		/*mPrintCube.printSalesReturn(sosno, sodate, socustomercode, socustomername,
				product, productdet, printsortHeader, null, 1,
				null, footerArr);*/

        mPrintCube.printSalesReturn(sosno, sodate, socustomercode,
                socustomername, product, productdet, 1);
    }

    //offline
    private String checkInternetStatus() {
        String internetStatus = "";
        String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
        if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
            if(mobileHaveOfflineMode.matches("1")){
        checkOffline = OfflineCommon.isConnected(SalesReturnHeader.this);
//        String internetStatus = "";
        if (onlineMode.matches("True")) {
            if (checkOffline == true) {
                String Off_dialog = OfflineDatabase.getInternetMode("OfflineDialog");
                if (Off_dialog.matches("true")) {
                    internetStatus = "true";
                } else {
                    offlineCommon.OfflineAlertDialog();
                    Boolean dialogStatus = offlineCommon.showDialog();
                    OfflineDatabase.updateInternetMode("OfflineDialog",dialogStatus + "");
                    Log.d("Offline DialogStatus", "" + dialogStatus);
                    internetStatus = "" + dialogStatus;
                }
            } else if (checkOffline == false) {
                String on_dialog = OfflineDatabase.getInternetMode("OnlineDialog");
                if (on_dialog.matches("true")) {
                    internetStatus = "true";
                } else {
                    offlineCommon.onlineAlertDialog();
                    boolean dialogStatus = offlineCommon.showDialog();
                    OfflineDatabase.updateInternetMode("OnlineDialog",dialogStatus + "");
                    Log.d("Online DialogStatus", "" + dialogStatus);
                    internetStatus = "" + dialogStatus;
                }
            }
        }
        onlineMode = OfflineDatabase.getOnlineMode();
        if (onlineMode.matches("True")) {
            offlineLayout.setVisibility(View.GONE);
            if (checkOffline == true) {
                if (internetStatus.matches("true")) {
                    offlineLayout.setVisibility(View.VISIBLE);
                    offlineLayout.setBackgroundResource(drawable.temp_offline_pattern_bg);
                }
            }

        } else if (onlineMode.matches("False")) {
            offlineLayout.setVisibility(View.VISIBLE);
        }

        // if(offlineLayout.getVisibility() == View.GONE){
        // re_print.setVisibility(View.VISIBLE);
        //
        // }else{
        // re_print.setVisibility(View.INVISIBLE);
        // }

        String deviceId = RowItem.getDeviceID();

        Log.d("device id", "dev " + deviceId);
            }else{
                internetStatus = "false";
            }
        }else{
            internetStatus = "false";
        }
        return internetStatus;
    }


    /** Offline End **/
    private class ShowAllLocation extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hashValue.clear();
            helper.showProgressView(salesO_parent);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                hashValue.put("CompanyCode", cmpnyCode);
                String jsonString = SalesOrderWebService.getSODetail(hashValue, "fncGetLocation");
                if (jsonString != null && !jsonString.isEmpty()) {
                    jsonResponse = new JSONObject(jsonString);
                    jsonMainNode = jsonResponse.optJSONArray("SODetails");
                    int lengJsonArray = jsonMainNode.length();
                    if (lengJsonArray > 0) {
                        for (int i = 0; i < lengJsonArray; i++) {
                            JSONObject jsonObject = jsonMainNode.getJSONObject(i);
                            String locationCode = jsonObject.getString("LocationCode");
                            String locationName = jsonObject.getString("LocationName");
                            HashMap<String, String> locationhm = new HashMap<String, String>();
                            locationhm.put(locationCode, locationName);
                            locationArrHm.add(locationhm);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("locationArrHm","-->"+locationArrHm.size());
            helper.dismissProgressView(salesO_parent);
            alertDialogLocation();
        }
    }
    public void alertDialogLocation() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(
                SalesReturnHeader.this);
        final EditText editText = new EditText(SalesReturnHeader.this);
        final ListView listview = new ListView(SalesReturnHeader.this);
        LinearLayout layout = new LinearLayout(SalesReturnHeader.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        myDialog.setTitle(getResources().getString(R.string.location));
        editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,
                0, 0);
        layout.addView(editText);
        layout.addView(listview);
        myDialog.setView(layout);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    myalertDialog
                            .getWindow()
                            .setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        arrayAdapterSupp = new CustomAlertAdapterSupp(SalesReturnHeader.this,
                locationArrHm);
        listview.setAdapter(arrayAdapterSupp);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                myalertDialog.dismiss();
                getArraylsit = arrayAdapterSupp.getArrayList();
                HashMap<String, String> datavalue = getArraylsit.get(position);
                Set<Entry<String, String>> keys = datavalue.entrySet();
                Iterator<Entry<String, String>> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    @SuppressWarnings("rawtypes")
                    Entry mapEntry = iterator.next();
                    loccode = (String) mapEntry.getKey();
                    String locationName = (String) mapEntry.getValue();
                    locationcode_filter.setText(locationName);

                    locationcode_filter.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable s) {

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s,
                                                      int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {
                            textlength = locationcode_filter.getText().length();
                        }
                    });
                }
            }
        });

        searchResults = new ArrayList<HashMap<String, String>>(locationArrHm);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                textlength = editText.getText().length();
                searchResults.clear();
                for (int i = 0; i < locationArrHm.size(); i++) {
                    String supplierName = locationArrHm.get(i).toString();
                    if (textlength <= supplierName.length()) {
                        if (supplierName.toLowerCase().contains(
                                editText.getText().toString().toLowerCase()
                                        .trim()))
                            searchResults.add(locationArrHm.get(i));
                    }
                }

                arrayAdapterSupp = new CustomAlertAdapterSupp(
                        SalesReturnHeader.this, searchResults);
                listview.setAdapter(arrayAdapterSupp);
            }
        });
        myDialog.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        myalertDialog = myDialog.show();
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(SalesReturnHeader.this, LandingActivity.class);
        startActivity(i);
        SalesReturnHeader.this.finish();
    }

    private class AsyncCallWSSalesDelete extends AsyncTask<Void, Void, Void> {
        String dialogStatus, jsonString;
        @Override
        protected void onPreExecute() {
            loadprogress();
            jsonString ="";
            deleteSRResult = "";
            dialogStatus = checkInternetStatus();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("snono-->", sosno);
            String user = SupplierSetterGetter.getUsername();
            String cmpnyCode = SupplierSetterGetter.getCompanyCode();
            HashMap<String, String> hashValue = new HashMap<String, String>();
            hashValue.put("CompanyCode", cmpnyCode);
            hashValue.put("SalesReturnNo", sosno);
            hashValue.put("User", user);

            if (onlineMode.matches("True")) {
                if (checkOffline == true) { //temp_offline

                    if (dialogStatus.matches("true")) {
                        //need
                    } else {

                        if(mobileHaveOfflineMode.matches("1")){
                            finish();
                        }
                    }

                } else {  //Onllineine

                    jsonString = WebServiceClass.parameterService(hashValue, "fncDeleteSalesReturn");
                }

            } else if (onlineMode.matches("False")) {  // permanent_offline

                //need
            }
            if(jsonString != null && !jsonString.isEmpty()){

                try {

                    jsonResponse = new JSONObject(jsonString);
                    jsonMainNode = jsonResponse.optJSONArray("JsonArray");

                    int lengthJsonArr = jsonMainNode.length();

                    for (int i = 0; i < lengthJsonArr; i++) {

                        JSONObject jsonChildNode;
                        jsonChildNode = jsonMainNode.getJSONObject(i);
                        deleteSRResult = jsonChildNode.optString("Result").toString();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            hashValue.clear();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            if (!deleteSRResult.matches("")) {

                new AsyncCallWSSOHeader().execute();

                Toast.makeText(SalesReturnHeader.this, "Delete Successfully",
                        Toast.LENGTH_LONG).show();

            } else {

                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(salesO_parent, true);

                Toast.makeText(SalesReturnHeader.this, "Failed",
                        Toast.LENGTH_LONG).show();

            }
        }
    }

    private void loadprogress() {
        spinnerLayout = new LinearLayout(SalesReturnHeader.this);
        spinnerLayout.setGravity(Gravity.CENTER);
        SalesReturnHeader.this.addContentView(spinnerLayout,
                new LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT));
        spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
        enableViews(salesO_parent, false);
        progressBar = new ProgressBar(SalesReturnHeader.this);
        progressBar.setProgress(android.R.attr.progressBarStyle);
        progressBar.setIndeterminateDrawable(getResources().getDrawable(
                drawable.greenprogress));
        spinnerLayout.addView(progressBar);
    }
}