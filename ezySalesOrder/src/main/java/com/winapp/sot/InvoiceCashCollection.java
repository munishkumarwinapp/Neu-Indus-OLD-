package com.winapp.sot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.model.Product;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineCustomerSync;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.printcube.printcube.BluetoothService;
import com.winapp.printcube.printcube.CubePrint;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.PrinterZPL;
import com.winapp.printer.ReceiptPrintPreview;
import com.winapp.printer.UIHelper;
import com.winapp.sotdetails.CustomerListActivity;
import com.winapp.sotdetails.CustomerSetterGetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

public class InvoiceCashCollection extends Activity implements LocationListener {

    private EditText bnkName, bnkCodesp, chkDate, chkNo, csh_custcode, csh_custname, csh_amount, csh_credit, csh_netamt,
            csh_tot_outstanding, csh_tot_paid, csh_tot_credit, csh_tot_balance, csh_returnamt, csh_tot_salesreturn, csh_remaining_outstanding;
    private TextView btn_spinner, stnumber, srTotalAmount, srTotalCredit;
    private Button csh_bt_split, csh_bt_invoice, csh_bt_total, stupButton, stdownButton, totalpaid_btn, totalcredit_btn;
    private ImageButton csh_add, back, csh_bt_save;
    private ImageView salesreturn_menu, sm_sign_icon, sm_signature;
    private CheckBox csh_checkall, credit_checkall, enableprint;
    private ListView csh_list, credit_list;
    private LinearLayout cashcollection_layout, csh_listheader,
            noofprint_layout, salesreturn_linear, linear_tot_salesreturn, linear_remain_outstanding, signature_linear, spinnerLayout;
    private ProgressBar progressBar;
    private ScrollView total_layout;

    private Invoice_customAdapter Adapter;
    private MyCustomAdapter spinnerAdapterSupp;
    private CustomAlertAdapterSupp arrayAdapterSupp;
    private CustomListAdapter customAdapter;
    private AlertDialog myalertDialog = null, SpinneralertDialog = null,mAlertDialog=null;
    private Dialog mDialog;
    private Intent intent;
    private Handler mHandler;
    private UIHelper helper;
    private Context context = this;
    private Calendar myCalendar = Calendar.getInstance();

    private List<String> sort = new ArrayList<String>();
    private List<String> printsortHeader = new ArrayList<String>();
    private List<ProdDetails> product,mSRDetail;
    private List<ProductDetails> productdet, footerArr;
    private ArrayList<In_Cash> ListArray = new ArrayList<In_Cash>();
    private ArrayList<In_Cash> mOnlineInvoicePrintOfflineArr;
    private ArrayList<String> sortproduct = new ArrayList<String>();
    private ArrayList<HashMap<String, String>> SaveArray = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> salesReturnArray = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> bnkal = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> getArraylsitbnk = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> salesreturn_al = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> mList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> searchResults;
    private HashMap<String, String> hashValue = new HashMap<String, String>();
    private HashMap<String, String> mhashmap = new HashMap<String, String>();
    private HashMap<String, String> cushashValue = new HashMap<String, String>();
    private HashMap<String, String> hm = new HashMap<String, String>();
    private HashMap<String, String> hashVl = new HashMap<String, String>();
    private HashSet<String> hs = new HashSet<String>();
    private Set<String> st = new HashSet<String>();
    private ArrayList<Product> sngleArray = new ArrayList<Product>();

    private JSONObject jsonResponse, salesreturnjsonResponse, paymentjsonResponse, bnkjsonResponse,
            jsonResp, custjsonResp, jsonRespfooter,rdjsonResp;
    private JSONArray jsonMainNode, salesreturnjsonMainNode, paymentjsonMainNode, bnkjsonMainNode,
            jsonSecNode, custjsonMainNode, jsonSecNodefooter,rdjsonSecNode;
    private String jsonString = null, salesreturnjsonString = null, paymentjsonString = null, bnkjsonString = null,
            jsonStr = null, serverdate, InvNo, customerCode, gnrlStngs, custjsonStr = null, jsonSt,
            catCode, catName, SpinnerSelectionValue = "Cash", saveResult = "", valid_url = "", header,
            previewno, dialogResult,rdjsonStr=null;
    private int listPosition = -1, list_size = 0, Pos = -1, textlength = 0, stwght = 1, srlist_size = 0;
    private double total = 0.00, outstanding = 0.00, returnAmount = 0.00, creditAmount = 0.00,
            netAmount = 0.00, total_outstanding = 0.00, total_salesreturn = 0.00, totla_remianing_outstanding = 0.00, total_paid = 0.00, total_credit = 0.00,
            total_balance = 0.00, srAmttotal = 0.00, srCrdttotal = 0.00;
    private String[] arr;
    private static final int SIGNATURE_ACTIVITY = 2;

    //Offline
    private LinearLayout offlineLayout;
    private OfflineDatabase offlineDatabase;
    private boolean checkOffline;
    private String onlineMode, provider, signature_img, product_img, address1 = "", address2 = "";
    private OfflineCommon offlineCommon;
    private OfflineSettingsManager offlinemanager;
    private OfflineCustomerSync offlinecustomerSynch;
    private double setLatitude, setLongitude;

    // get location
    private LocationManager locationManager;
    private boolean isGPSEnabled = false, isNetworkEnabled = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    RadioGroup radio_group;
    String radioButtonTxt="",mobileHaveOfflineMode="",receiptAutoCreditAmt="";
    private ArrayList<ProductDetails> mSRHeaderDetailArr;
    double autoCredit = 0.0,calculate = 0.00;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.invoice_cash_collection);

        GlobalData.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        GlobalData.mService = new BluetoothService(this, mHandler1);
        // @Offline
        onlineMode = OfflineDatabase.getOnlineMode();
        offlineDatabase = new OfflineDatabase(InvoiceCashCollection.this);
        offlineCommon = new OfflineCommon(InvoiceCashCollection.this);
        checkOffline = OfflineCommon.isConnected(InvoiceCashCollection.this);
        OfflineDatabase.init(InvoiceCashCollection.this);
        new OfflineSalesOrderWebService(InvoiceCashCollection.this);
        offlinemanager = new OfflineSettingsManager(InvoiceCashCollection.this);
        offlinecustomerSynch = new OfflineCustomerSync(InvoiceCashCollection.this);

        offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);

        salesreturn_linear = (LinearLayout) findViewById(R.id.salesreturn_linear);
        total_layout = (ScrollView) findViewById(R.id.total_layout);
        cashcollection_layout = (LinearLayout) findViewById(R.id.cashcollection_layout);
        csh_listheader = (LinearLayout) findViewById(R.id.csh_listheader);
        linear_tot_salesreturn = (LinearLayout) findViewById(R.id.linear_tot_salesreturn);
        linear_remain_outstanding = (LinearLayout) findViewById(R.id.linear_remain_outstanding);

        srTotalAmount = (TextView) findViewById(R.id.srTotalAmount);
        srTotalCredit = (TextView) findViewById(R.id.srTotalCredit);
        credit_checkall = (CheckBox) findViewById(R.id.credit_checkall);
        credit_list = (ListView) findViewById(R.id.credit_list);

        csh_custcode = (EditText) findViewById(R.id.csh_custcode);
        csh_custname = (EditText) findViewById(R.id.csh_custname);
        csh_amount = (EditText) findViewById(R.id.csh_amount);
        csh_credit = (EditText) findViewById(R.id.csh_credit);
        csh_netamt = (EditText) findViewById(R.id.csh_netamt);
        csh_tot_salesreturn = (EditText) findViewById(R.id.csh_tot_salesreturn);
        csh_remaining_outstanding = (EditText) findViewById(R.id.csh_remaining_outstanding);

//		csh_total = (EditText) findViewById(R.id.csh_total);
//		csh_outstanding = (EditText) findViewById(R.id.csh_outstanding);

        csh_tot_outstanding = (EditText) findViewById(R.id.csh_tot_outstanding);
        csh_tot_paid = (EditText) findViewById(R.id.csh_tot_paid);
        csh_tot_credit = (EditText) findViewById(R.id.csh_tot_credit);
        csh_tot_balance = (EditText) findViewById(R.id.csh_tot_balance);
        csh_returnamt = (EditText) findViewById(R.id.csh_returnamt);

        totalpaid_btn= (Button) findViewById(R.id.totalpaid_btn);
        totalcredit_btn= (Button) findViewById(R.id.totalcredit_btn);

        csh_returnamt = (EditText) findViewById(R.id.csh_returnamt);
        csh_bt_save = (ImageButton) findViewById(R.id.save_invoice);
        csh_bt_split = (Button) findViewById(R.id.csh_bt_split);
        csh_bt_invoice = (Button) findViewById(R.id.csh_bt_invoice);
        csh_bt_total = (Button) findViewById(R.id.csh_bt_total);
        csh_checkall = (CheckBox) findViewById(R.id.csh_checkall);
        csh_list = (ListView) findViewById(R.id.csh_list);

        back = (ImageButton) findViewById(R.id.back);
        csh_add = (ImageButton) findViewById(R.id.csh_add);
        salesreturn_menu = (ImageView) findViewById(R.id.salesreturn_menu);

        btn_spinner = (TextView) findViewById(R.id.btn_spinner);

        sm_sign_icon = (ImageView) findViewById(R.id.sm_sign_icon);
        sm_signature = (ImageView) findViewById(R.id.sm_signature);
        signature_linear = (LinearLayout) findViewById(R.id.signature_linear);
        mOnlineInvoicePrintOfflineArr = new ArrayList<>();

        mSRDetail = new ArrayList<>();
        product = new ArrayList<ProdDetails>();
        productdet = new ArrayList<ProductDetails>();
        FWMSSettingsDatabase.init(InvoiceCashCollection.this);
        valid_url = FWMSSettingsDatabase.getUrl();
        new WebServiceClass(valid_url);
        new SOTSummaryWebService(valid_url);
        new DateWebservice(valid_url, InvoiceCashCollection.this);
        customAdapter = new CustomListAdapter();
        footerArr = new ArrayList<ProductDetails>();
        mSRHeaderDetailArr = new ArrayList<>();
        csh_bt_save.setVisibility(View.GONE);
        mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
        if (onlineMode.matches("True")) {
            offlineLayout.setVisibility(View.GONE);
        } else if (onlineMode.matches("False")) {
            offlineLayout.setVisibility(View.VISIBLE);
        }
//        SalesOrderSetGet.setReceiptAutoCreditAmount("0.52");
        receiptAutoCreditAmt = SalesOrderSetGet.getReceiptAutoCreditAmount();
        Log.d("receiptAutoCreditAmt","rr "+receiptAutoCreditAmt);
        if(receiptAutoCreditAmt!=null && !receiptAutoCreditAmt.isEmpty()){
            autoCredit = Double.parseDouble(receiptAutoCreditAmt);
        }else{
            receiptAutoCreditAmt="0";
        }

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("extras", "Extra NULL");
        } else {
            InvNo = extras.getString("invNo");
            In_Cash.setInvoiceNo(InvNo);
            customerCode = extras.getString("customerCode");
        }
        helper = new UIHelper(InvoiceCashCollection.this);
        csh_list.setClickable(true);
        bnkal.clear();
        header = SalesOrderSetGet.getHeader_flag();
        In_Cash in_empty = new In_Cash();
        in_empty.setIn_Position(-1);
        in_empty.setIn_Date("");
        in_empty.setIn_Netvalue("");
        in_empty.setIn_Credit("");
        in_empty.setIn_Paid("");
        in_empty.setIn_Bal("");
        in_empty.setIn_Status("");
        in_empty.setAdd_bal("");
        in_empty.setAdd_paid("");
        in_empty.setAdd_credit("");
        in_empty.setIn_Listsize(-1);
        in_empty.setIn_Total(0.00);
        in_empty.setSelected(false);

        In_Cash.setBank_code("");
        In_Cash.setBank_Name("");
        In_Cash.setCheck_No("");
        In_Cash.setCheck_Date("");
        In_Cash.setPay_Mode("Cash");
        In_Cash.setCust_Code("");

        btn_spinner.setText(SpinnerSelectionValue);

        AsyncCallWSSalesOrder asynCall = new AsyncCallWSSalesOrder();
        asynCall.execute();

        InvoiceWebservice webservice = new InvoiceWebservice();
        webservice.execute();

        AsyncGeneralSettings asyncgs = new AsyncGeneralSettings();
        asyncgs.execute();

        totalpaid_btn.setText(getResources().getString(R.string.total_paid)+" : " + "0.00" + "");
        totalcredit_btn.setText(getResources().getString(R.string.total_credit)+" : " +"0.00" + "");

        try {


            // Getting LocationManager object
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // // Creating an empty criteria object
            // Criteria criteria = new Criteria();
            //
            // // Getting the name of the provider that meets the criteria
            // provider = locationManager.getBestProvider(criteria, false);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 20000, 0, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        Location location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            Log.d("location", "not null");
                            if (onlineMode.matches("True")) {
                                if (checkOffline == true) {
                                } else { // online
                                    onLocationChanged(location);
                                }
                            }
                        }
                    }
                } else if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, 20000, 0, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        Location location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Log.d("location", "not null");
                            if (onlineMode.matches("True")) {
                                if (checkOffline == true) {
                                } else { // online
                                    onLocationChanged(location);
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        sm_sign_icon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(InvoiceCashCollection.this, CaptureSignature.class);
                startActivityForResult(i, SIGNATURE_ACTIVITY);

            }
        });

        salesreturn_menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InvoiceCashCollection.this.openOptionsMenu();
            }

        });


        csh_checkall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;

                if (checkBox.isChecked()) {
                    csh_bt_save.setVisibility(View.VISIBLE);
                    Adapter.selectAll(true);
//					csh_total.setText(String.valueOf(total));
                    list_size = ListArray.size();
                    outstanding = 0.00;
                    returnAmount = 0.00;
                    // uncheck salesreturn value if select all invoice
                    srlist_size = 0;
                    for (int i = 0; i < mList.size(); i++) {
                        HashMap<String, String> data = mList.get(i);
                        data.put("CreditAmount", "0.00");
                    }
                    // creditSplit();
                    customAdapter.notifyDataSetChanged();
                } else {
                    csh_bt_save.setVisibility(View.GONE);
                    Adapter.selectAll(false);
                    csh_amount.setText("");
                    csh_credit.setText("");
                    list_size = 0;
                    listPosition = -1;
                    Pos = -1;
                    total = 0.00;
                    outstanding = 0.00;
                    returnAmount = 0.00;

                    Log.d("getSelected()","-->"+Adapter.getSelected());
                    int selectedItem = Adapter.checkLastSelectedItem();
                    if(selectedItem == 0){
                        srlist_size = 0;
                        for (int i = 0; i < mList.size(); i++) {
                            HashMap<String, String> data = mList.get(i);
                            data.put("CreditAmount", "0.00");
                        }
                        // creditSplit();
                        customAdapter.notifyDataSetChanged();
                    }
//					csh_total.setText(String.valueOf(total));
                }

            }
        });

        credit_checkall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                srAmttotal = 0.00;
                srCrdttotal = 0.00;
                if (checkBox.isChecked()) {
                    srlist_size = mList.size();
                    for (int i = 0; i < mList.size(); i++) {
                        HashMap<String, String> data = mList.get(i);
                        data.put("CreditAmount", data.get("BalanceAmount"));
                    }

                } else {
                    srlist_size = 0;
                    for (int i = 0; i < mList.size(); i++) {
                        HashMap<String, String> data = mList.get(i);
                        data.put("CreditAmount", "0.00");
                    }
                }
                creditSplit();
                customAdapter.notifyDataSetChanged();
            }
        });


        credit_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long arg3) {

                Log.d("credit_list position ", "--> " + position);
                CheckBox cBox = (CheckBox) parent.getChildAt(position - credit_list.getFirstVisiblePosition()).findViewById(R.id.chckbox_item);
                TextView crtTxt = (TextView) parent.getChildAt(position - credit_list.getFirstVisiblePosition()).findViewById(R.id.item4);
                TextView amtTxt = (TextView) parent.getChildAt(position - credit_list.getFirstVisiblePosition()).findViewById(R.id.item3);

//				Log.d("checkBox.isChecked()", "ddd-> "+cBox.isSelected());
                TextView srnumber_txt = (TextView) v.findViewById(R.id.item1);

                if (cBox.isChecked()) {

                    srAmttotal = 0.00;
                    srCrdttotal = 0.00;

                    String crtStr = crtTxt.getText().toString();
                    String amtStr = amtTxt.getText().toString();
                    showPrintDialog(crtStr, amtStr,srnumber_txt.getText().toString());
                    String crtEd = showDialog();
                    HashMap<String, String> data = mList.get(position);
                    data.put("CreditAmount", crtEd);
                    Log.d("crtEd ", "--> " + crtEd);
                    customAdapter.notifyDataSetChanged();
                }

            }
        });


        csh_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1,
                                    int position, long arg3) {

                TextView paidValue_list = (TextView) parent.getChildAt(
                        position - csh_list.getFirstVisiblePosition())
                        .findViewById(R.id.in_paid);

                TextView creditValue_list = (TextView) parent.getChildAt(
                        position - csh_list.getFirstVisiblePosition())
                        .findViewById(R.id.in_credit);

                String crt = "";

                if (!creditValue_list.getText().toString().matches("0.00")) {
                    crt = creditValue_list.getText().toString();
                }

                In_Cash in_so = Adapter.getItem(position);

                if (in_so.isSelected()) {

                    if (listPosition == position) {

                        listPosition = position;
                        Pos = position;
                        csh_amount.setText(paidValue_list.getText().toString());
                        csh_amount.requestFocus();
                        csh_amount.setSelection(csh_amount.length());
                        csh_credit.setText(crt);

                    } else {
                        listPosition = position;
                        Pos = position;
                        csh_amount.setText(paidValue_list.getText().toString());
                        csh_amount.requestFocus();
                        csh_amount.setSelection(csh_amount.length());
                        csh_credit.setText(crt);
                        netAmount = Double.parseDouble(in_so.getIn_Bal()
                                .toString());
                    }

                    boolean ischecked = customAdapter.salesreturn_isChecked();

                    if(!ischecked){
                        csh_credit.setText("");
                    }

                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(csh_amount,
                            InputMethodManager.SHOW_IMPLICIT);

                } else {

                }
            }

        });
        registerForContextMenu(csh_list);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(InvoiceCashCollection.this,
						InvoiceHeader.class);
				startActivity(i);*/

                Log.d("header_flag","-->"+header);

                if (header.matches("CustomerHeader")) {
                    intent = new Intent(InvoiceCashCollection.this, CustomerListActivity.class);
                } else if (header.matches("RouteHeader")) {
                    intent = new Intent(InvoiceCashCollection.this,
                            RouteHeader.class);
                }else if (header.matches("ConsignmentHeader")) {
                    intent = new Intent(InvoiceCashCollection.this,
                            ConsignmentHeader.class);
                } else {
                    intent = new Intent(InvoiceCashCollection.this, InvoiceHeader.class);
                }
                startActivity(intent);
                InvoiceCashCollection.this.finish();
            }
        });

        csh_bt_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                csh_bt_save.setVisibility(View.GONE);
                saveAlertDialog();
            }

        });

        csh_bt_total.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

//				if (outstanding == 0 && returnAmount == 0) {
//					csh_total.setText(String.valueOf(total));
//				} else {
//
//				}
//
//				csh_outstanding.setText(String.valueOf(outstanding));

                Adapter.getTotalValue();
                csh_returnamt.setText(String.valueOf(returnAmount));

                csh_list.setVisibility(View.GONE);
                csh_listheader.setVisibility(View.GONE);
                total_layout.setVisibility(View.VISIBLE);
                salesreturn_linear.setVisibility(View.GONE);
                signature_linear.setVisibility(View.GONE);

            }
        });

        csh_bt_split.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                try {
                    if (csh_netamt.getText().toString().matches("")) {

                    } else {

                        netAmount = Double.parseDouble(csh_netamt.getText()
                                .toString());

                        // added by saravana 26.07.2017

                        int selected = Adapter.getSelected();
                        if (selected > 0) {
                            // csh_checkall.setChecked(false);
                            csh_bt_save.setVisibility(View.GONE);
                            csh_amount.setText("");
                            csh_credit.setText("");
                            list_size = 0;
                            listPosition = -1;
                            Pos = -1;
                            total = 0.00;
                            outstanding = 0.00;
                            returnAmount = 0.00;
                            Adapter.splitSelectedBalance(netAmount);
                            Adapter.notifyDataSetChanged();

                            if (total >= netAmount) {
                                double finalValue = total - netAmount;
                                outstanding = Math.round(finalValue * 100.0) / 100.0;
                                returnAmount = 0.00;
                                csh_amount.setText("");
                                csh_credit.setText("");
                                csh_netamt.setText("");
                                csh_returnamt.setText(String.valueOf(returnAmount));
                            } else {
                                csh_amount.setText("");
                                csh_credit.setText("");
                                csh_netamt.setText("");
                                double finalValue = netAmount - total;
                                returnAmount = Math.round(finalValue * 100.0) / 100.0;
                                outstanding = 0.00;
                                csh_returnamt.setText(String.valueOf(returnAmount));
                            }

                        } else {

                            csh_checkall.setChecked(false);
                            csh_bt_save.setVisibility(View.GONE);
                            Adapter.selectAll(false);
                            csh_amount.setText("");
                            csh_credit.setText("");
                            list_size = 0;
                            listPosition = -1;
                            Pos = -1;
                            total = 0.00;
                            outstanding = 0.00;
                            returnAmount = 0.00;
//						csh_total.setText(String.valueOf(total));

                            Adapter.splitBalance(netAmount);
                            Adapter.notifyDataSetChanged();

						/*Log.d("list_size", "" + list_size);
						if (list_size == 0) {
						} else {
							Adapter.splitBalance(netAmount);
						}*/

                            if (total >= netAmount) {
                                double finalValue = total - netAmount;
                                outstanding = Math.round(finalValue * 100.0) / 100.0;
                                returnAmount = 0.00;
                                csh_amount.setText("");
                                csh_credit.setText("");
                                csh_netamt.setText("");
//							csh_total.setText(String.valueOf(netAmount));
//							csh_outstanding.setText(String.valueOf(outstanding));
                                csh_returnamt.setText(String.valueOf(returnAmount));
                            } else {
                                csh_amount.setText("");
                                csh_credit.setText("");
                                csh_netamt.setText("");
                                double finalValue = netAmount - total;
                                returnAmount = Math.round(finalValue * 100.0) / 100.0;
                                outstanding = 0.00;
//							csh_total.setText(String.valueOf(netAmount));
//							csh_outstanding.setText(String.valueOf(outstanding));
                                csh_returnamt.setText(String.valueOf(returnAmount));
                            }

                        }
                    }
                } catch (Exception e) {
                }

                csh_list.setVisibility(View.VISIBLE);
                csh_listheader.setVisibility(View.VISIBLE);
                total_layout.setVisibility(View.GONE);
                salesreturn_linear.setVisibility(View.GONE);
                signature_linear.setVisibility(View.GONE);

            }
        });

        csh_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                try {


                    int Position = Pos;
                    if (Position == -1) {
                        Toast.makeText(InvoiceCashCollection.this,
                                "Please Select an Invoice", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        In_Cash in_so = Adapter.getItem(Pos);

                        if (in_so.isSelected()) {
                            if (csh_amount.getText().toString().matches("")) {
                                Toast.makeText(InvoiceCashCollection.this,
                                        "Please Enter the Amount",
                                        Toast.LENGTH_LONG).show();
                            } else {

                                String credit = csh_credit.getText().toString();
                                String inBal = in_so.getIn_Bal();
                                double creditAmt = 0.0,balaneAmt = 0.00;

                                if (!credit.matches("")) {
                                    creditAmt = Double.parseDouble(credit);
                                }

                                double pd = Double.parseDouble(csh_amount
                                        .getText().toString());
                                Log.d("pd",""+pd);
                                Log.d("creditAmt",""+creditAmt);
                                double pd_total = pd + creditAmt;
                                balaneAmt = Double.parseDouble(inBal);
                                double blnc = Double.parseDouble(in_so
                                        .getIn_Bal());

                                Log.d("balaneAmt",""+balaneAmt);

                                DecimalFormat twoDForm = new DecimalFormat("#.##");
                                pd_total =  Double.valueOf(twoDForm.format(pd_total));
                                Log.d("pd_total",""+pd_total);

                                if(balaneAmt>=pd_total){
                                    Adapter.paidBalance(Pos, pd, creditAmt, blnc);
                                    csh_netamt.setText("");
                                    csh_amount.setText("");
                                    csh_credit.setText("");
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(
                                            csh_amount.getWindowToken(), 0);
                                }else{
                                    Toast.makeText(InvoiceCashCollection.this,
                                            "Balance Amount is "+ balaneAmt +" \nPaid amount and Credit amount must be less than or equal to Balance amount",
                                            Toast.LENGTH_LONG).show();
                                }

                              //  Adapter.paidBalance(Pos, pd, creditAmt, blnc);
//								csh_total.setText(pd_total+"");
                                //sales return check

                               /* boolean ischecked = customAdapter.salesreturn_isChecked();
                                Log.d("SR ischecked",""+ischecked);
                                if(ischecked){
                                    addCreditCall(0,creditAmt);
                                }*/
                            }
                        } else {
                            Toast.makeText(InvoiceCashCollection.this,
                                    "Please Select Invoice", Toast.LENGTH_LONG)
                                    .show();
                        }
                      //  csh_netamt.setText("");
                     //   csh_amount.setText("");
                      //  csh_credit.setText("");
                        csh_list.setVisibility(View.VISIBLE);
                        csh_listheader.setVisibility(View.VISIBLE);
                        total_layout.setVisibility(View.GONE);
                        salesreturn_linear.setVisibility(View.GONE);
                        signature_linear.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                }

            }
        });

        csh_bt_invoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                salesreturn_linear.setVisibility(View.GONE);
                signature_linear.setVisibility(View.GONE);
                csh_list.setVisibility(View.VISIBLE);
                csh_listheader.setVisibility(View.VISIBLE);
                total_layout.setVisibility(View.GONE);

            }
        });

        btn_spinner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                SpinnerAlertView();

            }
        });
    }


    public void addCreditCall(double edamt,double edcredit){

            if (edamt < edcredit) {
                Toast.makeText(InvoiceCashCollection.this, "Entered amount exceeds", Toast.LENGTH_SHORT).show();
            } else {
//                hideKeyboard(ed_credit);
                String dCredit = twoDecimalPoint(edcredit);
                srAmttotal = 0.00;
                srCrdttotal = 0.00;
                Log.d("srAmttotal Clear ", "-->" + srAmttotal);
                Log.d("srCrdttotal Clear ", "-->" + srCrdttotal);
//                endDialog(dCredit);

                int selected = Adapter.getSelected();

                if (selected == 1) {
                    Adapter.splitSelectedCreditBalance(edcredit);
                } else {
                    Adapter.splitBalance(edcredit);
                }

                Adapter.notifyDataSetChanged();

//                mDialog.cancel();
            }
        
    }

    public void getAddress(double latitude, double longitude) throws Exception {
        Log.d("getaddress", "gps " + latitude);
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        setLatitude = latitude;
        setLongitude = longitude;

        addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if (addresses != null && addresses.size() > 0) {

            address1 = addresses.get(0).getAddressLine(0);
            address2 = addresses.get(0).getAddressLine(1);

//			sm_location.setText(address1 + "," + address2);
            locationManager.removeUpdates(this);
        }
    }


    private void chequeDate() {

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        chkDate.setText(sdf.format(myCalendar.getTime()));
    }

    public void bankalertDialogSearch() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(
                InvoiceCashCollection.this);
        final EditText editText = new EditText(InvoiceCashCollection.this);
        final ListView listview = new ListView(InvoiceCashCollection.this);
        LinearLayout layout = new LinearLayout(InvoiceCashCollection.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        myDialog.setTitle("Bank");
        editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,
                0, 0);
        layout.addView(editText);
        layout.addView(listview);
        myDialog.setView(layout);
        arrayAdapterSupp = new CustomAlertAdapterSupp(
                InvoiceCashCollection.this, bnkal);
        listview.setAdapter(arrayAdapterSupp);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                myalertDialog.dismiss();
                getArraylsitbnk = arrayAdapterSupp.getArrayList();
                HashMap<String, String> datavalue = getArraylsitbnk
                        .get(position);
                Set<Entry<String, String>> keys = datavalue.entrySet();
                Iterator<Entry<String, String>> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    @SuppressWarnings("rawtypes")
                    Entry mapEntry = iterator.next();
                    String keyValues = (String) mapEntry.getKey();
                    String values = (String) mapEntry.getValue();
                    bnkCodesp.setText(keyValues);
                    bnkName.setText(values);
                    chkNo.requestFocus();
                    bnkCodesp.addTextChangedListener(new TextWatcher() {
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

                            textlength = bnkCodesp.getText().length();
                            bnkName.setText("");

                        }
                    });
                }
            }

        });
        searchResults = new ArrayList<HashMap<String, String>>(bnkal);
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
                for (int i = 0; i < bnkal.size(); i++) {
                    String supplierName = bnkal.get(i).toString();
                    if (textlength <= supplierName.length()) {
                        if (supplierName.toLowerCase().contains(
                                editText.getText().toString().toLowerCase()
                                        .trim()))
                            searchResults.add(bnkal.get(i));
                    }
                }

                arrayAdapterSupp = new CustomAlertAdapterSupp(
                        InvoiceCashCollection.this, searchResults);
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

    public void SpinnerAlertView() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(
                InvoiceCashCollection.this);
        final ListView listview = new ListView(InvoiceCashCollection.this);
        LinearLayout layout = new LinearLayout(InvoiceCashCollection.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listview);
        myDialog.setView(layout);
        spinnerAdapterSupp = new MyCustomAdapter(InvoiceCashCollection.this,
                R.layout.row, arr);
        listview.setAdapter(spinnerAdapterSupp);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long arg3) {

                SpinneralertDialog.dismiss();
                TextView textview1 = (TextView) v
                        .findViewById(R.id.locationspinner);
                SpinnerSelectionValue = textview1.getText().toString().trim();
                Log.d("textview1", textview1.getText().toString().trim());
                btn_spinner.setText(SpinnerSelectionValue);

                bnkCodesp = new EditText(context);
                bnkName = new EditText(context);
                chkNo = new EditText(context);
                chkDate = new EditText(context);

                if (SpinnerSelectionValue.equalsIgnoreCase("Cheque")) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Cheque Detail");

                    bnkCodesp.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            drawable.search, 0);
                    chkDate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.mipmap.cal, 0);
                    bnkName.setFocusable(false);
                    bnkName.setEnabled(false);
                    if (In_Cash.getBank_code().matches("")) {
                        bnkCodesp.setHint("Bank Code");
                        bnkName.setHint("Bank Name");
                        chkDate.setHint("Check Date");
                        chkNo.setHint("Check No");
                    } else {
                        bnkCodesp.setText(In_Cash.getBank_code());
                        bnkName.setText(In_Cash.getBank_Name());
                        chkNo.setText(In_Cash.getCheck_No());
                        chkDate.setText(In_Cash.getCheck_Date());
                    }

                    chkNo.setInputType(InputType.TYPE_CLASS_NUMBER
                            | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                    LinearLayout ll = new LinearLayout(context);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.addView(bnkCodesp);
                    ll.addView(bnkName);
                    ll.addView(chkNo);
                    ll.addView(chkDate);
                    alert.setView(ll);

                    bnkCodesp
                            .setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
                                    bnkCodesp) {
                                @Override
                                public boolean onDrawableClick() {

                                    bankalertDialogSearch();
                                    return true;
                                }
                            });

                    chkDate.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (MotionEvent.ACTION_UP == event.getAction())
                                new DatePickerDialog(
                                        InvoiceCashCollection.this, checkDate,
                                        myCalendar.get(Calendar.YEAR),
                                        myCalendar.get(Calendar.MONTH),
                                        myCalendar.get(Calendar.DAY_OF_MONTH))
                                        .show();
                            return false;
                        }
                    });

                    alert.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {

                                    String srt1 = bnkCodesp.getEditableText()
                                            .toString();
                                    String srt2 = bnkName.getEditableText()
                                            .toString();
                                    String srt3 = chkDate.getEditableText()
                                            .toString();
                                    String srt4 = chkNo.getEditableText()
                                            .toString();

                                    In_Cash.setBank_code(srt1);
                                    In_Cash.setBank_Name(srt2);
                                    In_Cash.setCheck_Date(srt3);
                                    In_Cash.setCheck_No(srt4);

                                }
                            });
                    alert.setNegativeButton("CANCEL",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {

                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();

                } else if (SpinnerSelectionValue.equalsIgnoreCase("Cash")) {

                }
                //
            }

            final DatePickerDialog.OnDateSetListener checkDate = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {

                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    chequeDate();
                }
            };

        });

        SpinneralertDialog = myDialog.show();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        In_Cash in_so = Adapter.getItem(info.position);
        previewno = in_so.getIn_InvNo();

        menu.setHeaderTitle(previewno);
        menu.add(0, v.getId(), 0, getResources().getString(R.string.print_preview));
        menu.add(1, v.getId(), 1, getResources().getString(R.string.extra_amount));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        In_Cash mCash = Adapter.getItem(info.position);
        if (item.getTitle() == getResources().getString(R.string.print_preview)) {
            ReceiptPreview task = new ReceiptPreview();
            task.execute();

        }  else if (item.getTitle() == getResources().getString(R.string.extra_amount)) {
            dialogAddExtra(mCash);
        }
        else {
            return false;
        }
        return true;
    }
    // created on 24/02/17  by saravana
    public void dialogAddExtra(final In_Cash mCash ){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Extra Amount");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);
        String extraAmount = mCash.getExtraAmount();
        if(extraAmount!=null && !extraAmount.isEmpty()){
            double extraAmt = Double.valueOf(extraAmount);
            if(extraAmt>0){
                input.setText(mCash.getExtraAmount());
                input.setSelection(input.getText().length());
            }
        }
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String addExtraAmount = input.getText().toString();
                if(addExtraAmount!=null && !addExtraAmount.isEmpty()){
                    mCash.setExtraAmount(addExtraAmount);
                }else{
                    mCash.setExtraAmount("0.00");
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create();
        mAlertDialog = builder.show();
    }
    private class AsyncCallWSSalesOrder extends AsyncTask<Void, Void, Void> {
        String dialogStatus;

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            spinnerLayout = new LinearLayout(InvoiceCashCollection.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            addContentView(spinnerLayout, new LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(cashcollection_layout, false);
            progressBar = new ProgressBar(InvoiceCashCollection.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);
            dialogStatus = checkInternetStatus();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // Offline
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
            String finalDate = timeFormat.format(new Date());
            System.out.println(finalDate);

            if (onlineMode.matches("True")) {
                if (checkOffline == true) {
                    Log.d("DialogStatus", "" + dialogStatus);

                    if (dialogStatus.matches("true")) {
                        Log.d("CheckOffline Alert -->", "True");
                        catCode = customerCode;
                        catName = offlineDatabase.getCustomerName(customerCode);
                        serverdate = finalDate;
                    } else {
                        Log.d("CheckOffline Alert -->", "False");
                        if(mobileHaveOfflineMode.matches("1")){
                            finish();
                        }
                    }

                } else {
                    Log.d("checkOffline Status -->", "False");
                    String cmpnyCode = SupplierSetterGetter.getCompanyCode();
                    cushashValue.put("CompanyCode", cmpnyCode);
                    cushashValue.put("CustomerCode", customerCode);

                    jsonString = WebServiceClass.parameterService(cushashValue,
                            "fncGetCustomerForSearch");
                    serverdate = DateWebservice.getDateService("fncGetServerDate");

                    try {

                        jsonResponse = new JSONObject(jsonString);
                        jsonMainNode = jsonResponse.optJSONArray("JsonArray");

                        int lengthJsonArr = jsonMainNode.length();
                        for (int i = 0; i < lengthJsonArr; i++) {

                            JSONObject jsonChildNode;

                            jsonChildNode = jsonMainNode.getJSONObject(i);
                            catCode = jsonChildNode.optString("CustomerCode")
                                    .toString();
                            catName = jsonChildNode.optString("CustomerName")
                                    .toString();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } else if (onlineMode.matches("False")) {
                Log.d("Customer Online", onlineMode);

                catCode = customerCode;
                catName = offlineDatabase.getCustomerName(customerCode);
                serverdate = finalDate;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            csh_custcode.setText(catCode);
            csh_custname.setText(catName);
        }
    }

    private class SaveAsyncCall extends AsyncTask<Void, Void, Void> {
        // do offline
        String dialogStatus;

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            spinnerLayout = new LinearLayout(InvoiceCashCollection.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            addContentView(spinnerLayout, new LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(cashcollection_layout, false);
            progressBar = new ProgressBar(InvoiceCashCollection.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);
            dialogStatus = checkInternetStatus();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String jsonString = "";
            JSONArray jsonMainNode = null;

            // Offline
            SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
            String finalDate = timeFormat.format(new Date());
            System.out.println(finalDate);

            String comapanyCode = offlinemanager.getCompanyType();
            Log.d("comapanyCode--->", "cc" + comapanyCode);

            if (onlineMode.matches("True")) {
                if (checkOffline == true) {
                    Log.d("DialogStatus", "" + dialogStatus);

                    if (dialogStatus.matches("true")) {
                        Log.d("CheckOffline Alert -->", "True");
                        serverdate = finalDate;
                        jsonString = OfflineSalesOrderWebService.saveReceiptOffline(SaveArray, comapanyCode, serverdate, "1");
                    } else {
                        Log.d("CheckOffline Alert -->", "False");
                        if(mobileHaveOfflineMode.matches("1")){
                            finish();
                        }
                    }

                } else {
                    Log.d("checkOffline Status -->", "False");
                    serverdate = DateWebservice.getDateService("fncGetServerDate");
                    jsonString = WebServiceClass.AddInvoiceService(SaveArray, "fncSaveReceipt", serverdate, salesReturnArray);

                    try {

                        JSONObject jsonResponse = new JSONObject(jsonString);
                        jsonMainNode = jsonResponse.optJSONArray("JsonArray");

                        int lengthJsonArr = jsonMainNode.length();
                        for (int i = 0; i < lengthJsonArr; i++) {
                            JSONObject jsonChildNode;
                            jsonChildNode = jsonMainNode.getJSONObject(i);
                            String sResult = jsonChildNode.optString("Result").toString();
                            if (!sResult.matches("")) {
                                OfflineSalesOrderWebService.saveReceiptOffline(SaveArray, comapanyCode, serverdate, "0");

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } else if (onlineMode.matches("False")) {
                Log.d("Customer Online", onlineMode);
                serverdate = finalDate;
                jsonString = OfflineSalesOrderWebService.saveReceiptOffline(SaveArray, comapanyCode, serverdate, "1");
            }

            try {

                JSONObject jsonResponse = new JSONObject(jsonString);
                jsonMainNode = jsonResponse.optJSONArray("JsonArray");

                int lengthJsonArr = jsonMainNode.length();
                for (int i = 0; i < lengthJsonArr; i++) {

                    JSONObject jsonChildNode;

                    jsonChildNode = jsonMainNode.getJSONObject(i);
                    saveResult = jsonChildNode.optString("Result").toString();
                }

            } catch (JSONException e) {
                saveResult = "";
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(cashcollection_layout, true);
            if (!saveResult.matches("")) {

                if (onlineMode.matches("True")) {
                    if (checkOffline == true) {
                    } else { // online
                        offlinecustomerSynch.synchNextRunningNo(valid_url);
                    }
                }

                signature_img = SOTDatabase.getSignatureImage();
                product_img = SOTDatabase.getProductImage();

                if (signature_img == null) {
                    signature_img = "";
                }
                if (product_img == null) {
                    product_img = "";
                }

                if (onlineMode.matches("True")) {
                    if (checkOffline == true) {
                    } else { // online
                        String imgResult = SOTSummaryWebService.saveSignatureImage(
                                saveResult, "" + setLatitude, "" + setLongitude,
                                signature_img, product_img, "fncSaveInvoiceImages", "RE", address1, address2);

                        Log.d("fncSaveInvoiceImages", "" + saveResult + " "
                                + setLatitude + " " + setLongitude + " RE" + address1 + address2 + "signature_img "
                                + signature_img + "product_img " + product_img);

                        if (!imgResult.matches("")) {
                            Log.d("Cap Image", "Saved");
                        } else {
                            Log.d("Cap Image", "Not Saved");
                        }
                    }
                }

                Toast.makeText(InvoiceCashCollection.this,
                        "Saved Successfully", Toast.LENGTH_LONG).show();

                if (enableprint.isChecked()) {

                    if (FWMSSettingsDatabase
                            .getPrinterAddress()
                            .matches(
                                    "[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$")) {

                        if (!ListArray.isEmpty()) {
                            //helper.showProgressDialog(R.string.generating_receipt);
                            //InvoiceProductWebservice task = new InvoiceProductWebservice();
                            //task.execute();
                            if (onlineMode.matches("True")) {
                                if (checkOffline == true) {
                                    //Offline
                                    helper.showProgressDialog(R.string.generating_receipt);
                                    new OfflineInvoicePrintCall().execute();

                                } else { // online

                                    helper.showProgressDialog(R.string.generating_receipt);
                                    new OnlineInvoicePrintCall().execute();

                                }

                            } else if (onlineMode.matches("False")) { // perman offline

                                helper.showProgressDialog(R.string.generating_receipt);
                                new OfflineInvoicePrintCall().execute();

                            }
                        }

                    } else {
                        helper.showLongToast(R.string.error_configure_printer);
						/*Intent i = new Intent(InvoiceCashCollection.this,
								InvoiceHeader.class);
						startActivity(i);
						InvoiceCashCollection.this.finish();*/
                        routeView();
                    }
                } else {

					/*Intent i = new Intent(InvoiceCashCollection.this,
							InvoiceHeader.class);
					startActivity(i);
					InvoiceCashCollection.this.finish();
*/
                    routeView();
                }
            } else {

                Toast.makeText(InvoiceCashCollection.this, "Failed",
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    private void routeView() {

        if (header.matches("InvoiceHeader")) {
            intent = new Intent(InvoiceCashCollection.this,
                    InvoiceHeader.class);
        } else if (header.matches("CustomerHeader")) {
            intent = new Intent(InvoiceCashCollection.this,
                    CustomerListActivity.class);
        } else if (header.matches("RouteHeader")) {
            intent = new Intent(InvoiceCashCollection.this,
                    RouteHeader.class);
        } else if (header.matches("ConsignmentHeader")) {
            intent = new Intent(InvoiceCashCollection.this,
                    ConsignmentHeader.class);
        }else {
            intent = new Intent(InvoiceCashCollection.this,
                    LandingActivity.class);
        }
        startActivity(intent);
        InvoiceCashCollection.this.finish();
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

    private class InvoiceWebservice extends AsyncTask<Void, Void, Void> {
        String dialogStatus;

        @Override
        protected void onPreExecute() {
            total_salesreturn = 0.00;
            total_outstanding = 0.00;
            ListArray.clear();
            salesreturn_al.clear();
            bnkal.clear();
            In_Cash.setBank_code("");
            In_Cash.setBank_Name("");
            In_Cash.setCheck_No("");
            In_Cash.setCheck_Date("");
            In_Cash.setPay_Mode("Cash");
            In_Cash.setCust_Code("");
            dialogStatus = checkInternetStatus();
            jsonString = "";
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                String cmpnyCode = SupplierSetterGetter.getCompanyCode();
                String username = SupplierSetterGetter.getUsername();
                hashValue.put("CompanyCode", cmpnyCode);
                hashValue.put("CustomerCode", customerCode);

                mhashmap.put("CompanyCode", cmpnyCode);
                mhashmap.put("CustomerCode", customerCode);

                int modeid = FWMSSettingsDatabase.getInvoiceuserModeId();
                if (modeid == 1) {
                    Log.d("getusername","true");
                    Log.d("username",username);
                    FWMSSettingsDatabase.updateInvoiceuserMode(1);
                    mhashmap.put("User", username);
                } else {
                    Log.d("getusername","false");
                    FWMSSettingsDatabase.updateInvoiceuserMode(0);
                    mhashmap.put("User", "");
                }

                // Offline

                if (onlineMode.matches("True")) {
                    if (checkOffline == true) {
                        Log.d("DialogStatus", "" + dialogStatus);

                        if (dialogStatus.matches("true")) {
                            Log.d("CheckOffline Alert -->", "True");
                            jsonString = offlineDatabase.getInvoiceHeaderCashCollection(customerCode);
                            salesreturnjsonString = "";
                            paymentjsonString = offlineDatabase.getPaymode();
                            bnkjsonString = offlineDatabase.getBank();
                        } else {
                            Log.d("CheckOffline Alert -->", "False");
                            if(mobileHaveOfflineMode.matches("1")){
                                finish();
                            }
                        }

                    } else {
                        Log.d("checkOffline Status -->", "False");
                        jsonString = WebServiceClass.parameterService(mhashmap, "fncGetInvoiceHeaderOnlyHaveBalance");
                        salesreturnjsonString = WebServiceClass.parameterService(hashValue, "fncGetSalesReturnHeader");
                        paymentjsonString = WebServiceClass.URLService("fncGetPaymode");
                        bnkjsonString = WebServiceClass.URLService("fncGetBank");

                        salesreturnjsonResponse = new JSONObject(salesreturnjsonString);
                        salesreturnjsonMainNode = salesreturnjsonResponse.optJSONArray("JsonArray");
                    }

                } else if (onlineMode.matches("False")) {
                    Log.d("Customer Online", onlineMode);
                    jsonString = offlineDatabase.getInvoiceHeaderCashCollection(customerCode);
                    salesreturnjsonString = "";
                    paymentjsonString = offlineDatabase.getPaymode();
                    bnkjsonString = offlineDatabase.getBank();
                }

                try {

                    jsonResponse = new JSONObject(jsonString);
                    jsonMainNode = jsonResponse.optJSONArray("JsonArray");

//				salesreturnjsonResponse = new JSONObject(salesreturnjsonString);
//				salesreturnjsonMainNode = salesreturnjsonResponse.optJSONArray("JsonArray");

                    paymentjsonResponse = new JSONObject(paymentjsonString);
                    paymentjsonMainNode = paymentjsonResponse.optJSONArray("JsonArray");

                    bnkjsonResponse = new JSONObject(bnkjsonString);
                    bnkjsonMainNode = bnkjsonResponse.optJSONArray("JsonArray");


                    int lengthArr = paymentjsonMainNode.length();
                    for (int i = 0; i < lengthArr; i++) {
                        JSONObject PayjsonChildNode;
                        try {
                            PayjsonChildNode = paymentjsonMainNode.getJSONObject(i);
                            String locationname = PayjsonChildNode.optString(
                                    "Description").toString();
                            st.add(locationname);
                            System.out.print("name" + st);
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }

                    int lengthbnkArr = bnkjsonMainNode.length();
                    for (int i = 0; i < lengthbnkArr; i++) {
                        JSONObject jsonChildNode;
                        try {

                            jsonChildNode = bnkjsonMainNode.getJSONObject(i);

                            String bnkcode = jsonChildNode.optString("Code").toString();
                            String bnkname = jsonChildNode.optString("Description")
                                    .toString();

                            HashMap<String, String> customerhm = new HashMap<String, String>();
                            customerhm.put(bnkcode, bnkname);
                            bnkal.add(customerhm);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    int lengthJsonArr = jsonMainNode.length();
                    for (int i = 0; i < lengthJsonArr; i++) {

                        JSONObject jsonChildNode;
                        try {
                            jsonChildNode = jsonMainNode.getJSONObject(i);
                            String dateTime = jsonChildNode.optString("InvoiceDate")
                                    .toString();
                            StringTokenizer tokens = new StringTokenizer(dateTime, " ");
                            String invDate = tokens.nextToken();
                            String invNet = jsonChildNode.optString("NetTotal")
                                    .toString();

                            String invBal = jsonChildNode.optString("BalanceAmount")
                                    .toString();
                            String invNo = jsonChildNode.optString("InvoiceNo")
                                    .toString();

                            String CreditAmount = jsonChildNode.optString("CreditAmount")
                                    .toString();
                            String PaidAmount = jsonChildNode.optString("PaidAmount")
                                    .toString();

                            In_Cash in_so = new In_Cash();
                            in_so.setIn_Position(i);
                            in_so.setIn_Date(invDate);
                            in_so.setIn_Netvalue(invNet);
//                            in_so.setIn_Credit(invCredit);
                            in_so.setIn_Credit("0.00");  // -arun- 10.02.2017 requested by thariq - credit must be zero at first time
                            in_so.setIn_Paid("0.00");
                            in_so.setIn_Bal(invBal);
                            in_so.setIn_InvNo(invNo);
                            in_so.setExtraAmount("0.00");
                            in_so.setAdd_bal("0.00");
                            in_so.setAdd_credit("0.00");
                            in_so.setAdd_paid("0.00");

                            in_so.setOldCreditAmount(CreditAmount); // -arun- 16.02.2017 requested by thariq - old credit amount and paid amount
                            in_so.setOldPaidAmount(PaidAmount);

                            total_outstanding += Double.parseDouble(invBal);
                            ListArray.add(in_so);

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }

                    int lengthSRArr = salesreturnjsonMainNode.length();
                    for (int i = 0; i < lengthSRArr; i++) {

                        JSONObject jsonChildNode;
                        try {
                            jsonChildNode = salesreturnjsonMainNode.getJSONObject(i);

                            String dateTime = jsonChildNode.optString("SalesReturnDate").toString();
                            StringTokenizer tokens = new StringTokenizer(dateTime, " ");
                            String SRDate = tokens.nextToken();
                            String SRNo = jsonChildNode.optString("SalesReturnNo").toString();
                            String SRCredit = jsonChildNode.optString("CreditAmount").toString();
                            String SRBal = jsonChildNode.optString("BalanceAmount").toString();

                            double srbl = 0.00;
                            if (SRBal != null && !SRBal.isEmpty()) {
                                srbl = Double.parseDouble(SRBal);
                            }

                            if (srbl > 0) {
                                HashMap<String, String> hmvalue = new HashMap<String, String>();
                                hmvalue.put("SalesReturnNo", SRNo);
                                hmvalue.put("SalesReturnDate", SRDate);
                                hmvalue.put("BalanceAmount", SRBal);
                                hmvalue.put("CreditAmount", SRCredit);
                                hmvalue.put("Flag", "false");
                                total_salesreturn += Double.parseDouble(SRBal);
                                salesreturn_al.add(hmvalue);
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }


            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            total_outstanding = Math.round(total_outstanding * 100.0) / 100.0;
            total_salesreturn = Math.round(total_salesreturn * 100.0) / 100.0;

            Log.d("Cashcollection List", ListArray.toString());
            Log.d("Sales return List", salesreturn_al.toString());
            Log.d("total_outstanding", "->" + total_outstanding);
            Log.d("total_salesreturn", "->" + total_salesreturn);

            arr = st.toArray(new String[st.size()]);
            Log.d("dropdown", Arrays.toString(arr));

            if (ListArray.isEmpty()) {
                Toast.makeText(InvoiceCashCollection.this, "No data found",
                        Toast.LENGTH_LONG).show();
                csh_checkall.setVisibility(View.INVISIBLE);
            } else {

                csh_tot_outstanding.setText(total_outstanding + "");
                csh_tot_salesreturn.setText(total_salesreturn + "");
                Adapter = new Invoice_customAdapter(InvoiceCashCollection.this,
                        ListArray);
                csh_list.setAdapter(Adapter);

            }

            if (salesreturn_al.isEmpty()) {
//			    Toast.makeText(InvoiceCashCollection.this, "No data found",Toast.LENGTH_LONG).show();
                credit_checkall.setVisibility(View.INVISIBLE);
                linear_tot_salesreturn.setVisibility(View.GONE);
                linear_remain_outstanding.setVisibility(View.GONE);
            } else {
                linear_tot_salesreturn.setVisibility(View.VISIBLE);
                linear_remain_outstanding.setVisibility(View.VISIBLE);
                customAdapter = new CustomListAdapter(InvoiceCashCollection.this, R.layout.customlist, salesreturn_al);
                credit_list.setAdapter(customAdapter);

            }

            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(cashcollection_layout, true);
        }
    }


    public class Invoice_customAdapter extends BaseAdapter {

        private ArrayList<In_Cash> listarray = new ArrayList<In_Cash>();
        LayoutInflater mInflater;
        private int selectedPosition = -1;
        double d1, d2 = 0.00;

        public Invoice_customAdapter(Context context,
                                     ArrayList<In_Cash> productsList) {
            listarray.clear();
            this.listarray = productsList;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listarray.size();
        }

        @Override
        public In_Cash getItem(int position) {
            return listarray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final InvoiceHolder holder;
            final In_Cash in_cash = listarray.get(position);

            if (convertView == null) {
                holder = new InvoiceHolder();
                convertView = mInflater.inflate(
                        R.layout.invoice_collection_listitem, null);
                holder.inDate = (TextView) convertView
                        .findViewById(R.id.in_date);
                holder.inNetvalue = (TextView) convertView
                        .findViewById(R.id.in_netvalue);
                holder.inCredit = (TextView) convertView
                        .findViewById(R.id.in_credit);
                holder.inPaid = (TextView) convertView
                        .findViewById(R.id.in_paid);
                holder.inBalance = (TextView) convertView
                        .findViewById(R.id.in_balance);
                holder.inCheckbox = (CheckBox) convertView
                        .findViewById(R.id.in_checkbox);

                holder.in_oldCreditAmount = (TextView) convertView
                        .findViewById(R.id.in_oldCreditAmount); // added new
                holder.in_oldPaidAmount = (TextView) convertView
                        .findViewById(R.id.in_oldPaidAmount);
                holder.in_no = (TextView) convertView
                        .findViewById(R.id.in_no);

                convertView.setTag(holder);
            } else {
                holder = (InvoiceHolder) convertView.getTag();
            }

            holder.inCheckbox.setChecked(in_cash.isSelected());
            holder.inCheckbox.setId(position);
            holder.inCheckbox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;

                    In_Cash in_cash = listarray.get(v.getId());

                    if (checkBox.isChecked()) {
                        csh_bt_save.setVisibility(View.VISIBLE);
                        csh_checkall.setChecked(false);
                        in_cash.setSelected(true);
                        list_size = list_size + 1;
                        selectedPosition = v.getId();
                        in_cash.setSplit_Position(selectedPosition);
                        Log.d("selectedPosition", "" + selectedPosition);
                        System.out.println("IN :" + list_size);
                        System.out.println(listarray.size());

                        ((View) checkBox.getParent())
                                .setBackgroundResource(drawable.list_grey_select);

                        holder.inPaid.setText(in_cash.getIn_Bal());
                        holder.inBalance.setText(in_cash.getIn_Paid());
                        holder.inCredit.setText(in_cash.getIn_Credit());
                        in_cash.setAdd_paid(in_cash.getIn_Bal());
                        in_cash.setAdd_bal(in_cash.getIn_Paid());
                        in_cash.setAdd_credit(in_cash.getIn_Credit());

                        d1 = Double.parseDouble(in_cash.getIn_Bal());
                        total = d1 + total;
                        total = Math.round(total * 100.0) / 100.0;
                        outstanding = 0.00;
                        returnAmount = 0.00;
                        Log.d("1... d2", String.valueOf(d1));
                        Log.d("1... total", String.valueOf(total));

                        Log.d("checked add credit", in_cash.getAdd_credit());
                        Log.d("checked in credit", in_cash.getIn_Credit());

                        getTotalValue();

                        /*// auto credit option 29.08.2017 by arunkumar
                        String paid = csh_tot_paid.getText().toString();
                        double paidAmt=0;
                        if(paid!=null){
                            paidAmt = Double.parseDouble(paid);
                        }

                        int decimal = (int) paidAmt; // you have 12345
                        double fractional = Double.parseDouble(roundOffTo2DecPlaces(paidAmt - decimal)); // you have 0.6789

                        if(fractional<=autoCredit){
                            holder.inPaid.setText(""+decimal);
                            holder.inCredit.setText(""+fractional);
                        }else{
                            holder.inPaid.setText(in_cash.getIn_Bal());
                            holder.inCredit.setText(in_cash.getIn_Credit());
                        }

                        Log.d("csh_tot_paid", paid);
                        Log.d("paid", ""+decimal);
                        Log.d("credit",""+ fractional);*/
                    } else {

                        csh_checkall.setChecked(false);

                        in_cash.setSelected(false);
                        if (v.getId() == listPosition) {
                            Pos = -1;
                            csh_amount.setText("");
                            csh_credit.setText("");
                            listPosition = -1;
                        }
                        list_size = list_size - 1;
                        selectedPosition = -1;
                        in_cash.setSplit_Position(selectedPosition);

                        /*if (position % 2 == 0) {
                            ((View) checkBox.getParent())
                                    .setBackgroundResource(drawable.list_item_even_bg);
                        } else {
                            ((View) checkBox.getParent())
                                    .setBackgroundResource(drawable.list_item_odd_bg);
                        }*/
                        ((View) checkBox.getParent())
                                .setBackgroundResource(drawable.list_grey_bg);

                        Log.d("unchecked add credit", in_cash.getAdd_credit());
                        Log.d("unchecked in credit", in_cash.getIn_Credit());


                        holder.inPaid.setText(in_cash.getIn_Paid());
                        holder.inBalance.setText(in_cash.getIn_Bal());
                        holder.inCredit.setText(in_cash.getIn_Credit());
                        in_cash.setAdd_paid(in_cash.getIn_Paid());
                        in_cash.setAdd_bal(in_cash.getIn_Paid());
                        in_cash.setAdd_credit(in_cash.getIn_Credit());
                        total = total - Double.parseDouble(in_cash.getIn_Bal());
                        total = Math.round(total * 100.0) / 100.0;
                        outstanding = 0.00;
                        returnAmount = 0.00;
                        Log.d("2... d2", in_cash.getIn_Bal());
                        Log.d("2... total", String.valueOf(total));


                        //created on 27/02/2016 by saravana
                        //set All credit amount zero in sales return adapter
                        Log.d("getSelected()","-->"+getSelected());
                        int selectedItem = checkLastSelectedItem();
                        if(selectedItem == 0){
                            srlist_size = 0;
                            for (int i = 0; i < mList.size(); i++) {
                                HashMap<String, String> data = mList.get(i);
                                data.put("CreditAmount", "0.00");
                            }
                            // creditSplit();
                            customAdapter.notifyDataSetChanged();
                        }

                        getTotalValue();
                    }
                    System.out.println("OUT :" + list_size);

                    if (list_size == listarray.size()) {
                        csh_checkall.setChecked(true);

                    } else if (list_size == 0) {
                        csh_bt_save.setVisibility(View.GONE);
                    }
                }
            });

            holder.inDate.setText(in_cash.getIn_Date());
            holder.inNetvalue.setText(in_cash.getIn_Netvalue());
            holder.inCredit.setText(in_cash.getIn_Credit());
            holder.inPaid.setText(in_cash.getIn_Paid());
            holder.inBalance.setText(in_cash.getIn_Bal());

            holder.in_oldCreditAmount.setText(in_cash.getOldCreditAmount()); // old credit amount 16.02.2017
            holder.in_oldPaidAmount.setText(in_cash.getOldPaidAmount());
            holder.in_no.setText(in_cash.getIn_InvNo());

            if (position % 2 == 0) {

                if (in_cash.isSelected()) {
                    csh_bt_save.setVisibility(View.VISIBLE);
                    convertView
                            .setBackgroundResource(drawable.list_grey_select);
                    int spt_pos = in_cash.getSplit_Position();
                    holder.inDate.setText(in_cash.getIn_Date());
                    holder.inNetvalue.setText(in_cash.getIn_Netvalue());
                    //holder.inCredit.setText(in_cash.getIn_Credit());
                    Log.d("Positon", "" + Pos);
                    Log.d("Split_int", "" + spt_pos);

                    if (position == Pos || position == spt_pos) {
                        holder.inPaid.setText(in_cash.getAdd_paid());
                        holder.inBalance.setText(in_cash.getAdd_bal());
                        holder.inCredit.setText(in_cash.getAdd_credit());
                    } else {
                        holder.inCredit.setText(in_cash.getIn_Credit());
                        holder.inPaid.setText(in_cash.getIn_Bal());
                        holder.inBalance.setText(in_cash.getIn_Paid());
                    }
                } else {
                    convertView
                            .setBackgroundResource(drawable.list_grey_bg);
                }


            } else {

                if (in_cash.isSelected()) {
                    csh_bt_save.setVisibility(View.VISIBLE);
                    convertView
                            .setBackgroundResource(drawable.list_grey_select);
                    int spt_pos = in_cash.getSplit_Position();
                    holder.inDate.setText(in_cash.getIn_Date());
                    holder.inNetvalue.setText(in_cash.getIn_Netvalue());
                    holder.inCredit.setText(in_cash.getIn_Credit());
                    Log.d("Positon", "" + Pos);
                    Log.d("Split_int", "" + spt_pos);

                    if (position == Pos || position == spt_pos) {
                        holder.inPaid.setText(in_cash.getAdd_paid());
                        holder.inBalance.setText(in_cash.getAdd_bal());
                        holder.inCredit.setText(in_cash.getAdd_credit());
                    } else {
                        holder.inCredit.setText(in_cash.getIn_Credit());
                        holder.inPaid.setText(in_cash.getIn_Bal());
                        holder.inBalance.setText(in_cash.getIn_Paid());
                    }
                } else {
                    convertView
                            .setBackgroundResource(drawable.list_grey_bg);
                }



            }

            return convertView;
        }



        // added by saravana 26.07.2017
        public void splitSelectedBalance(double netVlaue){

            double check_netamount = netVlaue;
            // int j = listarray.size();
            for (In_Cash in_cash : ListArray) {
                if (in_cash.isSelected()) {
                    double check_total = Double.parseDouble(in_cash.getIn_Bal());
                    Log.d("totalcheck_netamoun", check_total
                            + " , " + check_netamount);
                    if (check_netamount > 0.00) {
                        csh_bt_save.setVisibility(View.VISIBLE);
                        if (check_total >= check_netamount) {
                            in_cash.setSelected(true);
                            list_size = list_size + 1;
                            selectedPosition = in_cash.getIn_Position();
                            in_cash.setSplit_Position(selectedPosition);

                            d1 = Double.parseDouble(in_cash.getIn_Bal());
                            total = d1 + total;
                            total = Math.round(total * 100.0) / 100.0;
                            outstanding = 0.00;
                            returnAmount = 0.00;

                            in_cash.setAdd_paid(twoDecimalPoint(check_netamount));
                            double finalValue = check_total - check_netamount;
                            check_netamount = 0.00;
                            in_cash.setAdd_bal(twoDecimalPoint(finalValue));
                            in_cash.setAdd_credit(in_cash.getIn_Credit());
                            Log.d("total>=check_netamount",
                                    finalValue + " , " + check_total + " , "
                                            + check_netamount);
                        } else if (check_total < check_netamount) {
                            in_cash.setSelected(true);
                            list_size = list_size + 1;
                            selectedPosition = in_cash.getIn_Position();
                            in_cash.setSplit_Position(selectedPosition);

                            d1 = Double.parseDouble(in_cash.getIn_Bal());
                            total = d1 + total;
                            total = Math.round(total * 100.0) / 100.0;
                            outstanding = 0.00;
                            returnAmount = 0.00;

                            double finalValue = check_netamount - check_total;
                            check_netamount = Math.round(finalValue * 100.0) / 100.0;
                            in_cash.setAdd_bal(in_cash.getIn_Paid());
                            in_cash.setAdd_paid(twoDecimalPoint(check_total));
                            in_cash.setAdd_credit(in_cash.getIn_Credit());
                            Log.d("total < check_netamount",
                                    finalValue + " , " + check_total + " , "
                                            + check_netamount);
                        }
                    }
                }
            }

            if (list_size == listarray.size()) {
                csh_checkall.setChecked(true);

            }

            getTotalValue();

        }

        public void splitBalance(double netVlaue) {

            double check_netamount = netVlaue;
            int j = listarray.size();
            for (int i = j; i > 0; i--) {
                Log.d("i value", "" + i);
                final In_Cash in_cash = Adapter.getItem(i - 1);
                //if (in_cash.isSelected()) {

                double check_total = Double.parseDouble(in_cash.getIn_Bal());
                Log.d("totalcheck_netamoun", check_total
                        + " , " + check_netamount);
                if (check_netamount > 0.00) {
                    csh_bt_save.setVisibility(View.VISIBLE);
                    if (check_total >= check_netamount) {
                        in_cash.setSelected(true);
                        list_size = list_size + 1;
                        selectedPosition = in_cash.getIn_Position();
                        in_cash.setSplit_Position(selectedPosition);

                        d1 = Double.parseDouble(in_cash.getIn_Bal());
                        total = d1 + total;
                        total = Math.round(total * 100.0) / 100.0;
                        outstanding = 0.00;
                        returnAmount = 0.00;

                        in_cash.setAdd_paid(twoDecimalPoint(check_netamount));
                        double finalValue = check_total - check_netamount;
                        check_netamount = 0.00;
                        in_cash.setAdd_bal(twoDecimalPoint(finalValue));
                        in_cash.setAdd_credit(in_cash.getIn_Credit());
                        Log.d("total>=check_netamount",
                                finalValue + " , " + check_total + " , "
                                        + check_netamount);
                        //notifyDataSetChanged();
                    } else if (check_total < check_netamount) {
                        in_cash.setSelected(true);
                        list_size = list_size + 1;
                        selectedPosition = in_cash.getIn_Position();
                        in_cash.setSplit_Position(selectedPosition);

                        d1 = Double.parseDouble(in_cash.getIn_Bal());
                        total = d1 + total;
                        total = Math.round(total * 100.0) / 100.0;
                        outstanding = 0.00;
                        returnAmount = 0.00;

                        double finalValue = check_netamount - check_total;
                        check_netamount = Math.round(finalValue * 100.0) / 100.0;
                        in_cash.setAdd_bal(in_cash.getIn_Paid());
                        in_cash.setAdd_paid(twoDecimalPoint(check_total));
                        in_cash.setAdd_credit(in_cash.getIn_Credit());
                        Log.d("total < check_netamount",
                                finalValue + " , " + check_total + " , "
                                        + check_netamount);
                        //notifyDataSetChanged();
                    }
					/*} else {
						in_cash.setAdd_paid(in_cash.getIn_Bal());
						in_cash.setAdd_bal(in_cash.getIn_Paid());
					}*/
                }
            }
            //notifyDataSetChanged();
            if (list_size == listarray.size()) {
                csh_checkall.setChecked(true);

            }

            getTotalValue();
        }

        public void splitSelectedCreditBalance(double creditVlaue) {
            double check_creditamount = creditVlaue;
            int j = listarray.size();
            for (int i = j; i > 0; i--) {
                Log.d("i value", "" + i);
                final In_Cash in_cash = Adapter.getItem(i - 1);
				/*if (in_cash.isSelected()) {
					Log.d("SeleCreditBala checked","checked");
				}else{
					Log.d("SeleCreditBalunchecked","unchecked");
				}*/
                if (in_cash.isSelected()) {
                    double check_total = Double.parseDouble(in_cash.getIn_Bal());
                    //	in_cash.setIn_Credit(String.valueOf(check_creditamount));
                    Log.d("total check_netamount", check_total
                            + " , " + check_creditamount);
                    if (check_creditamount > 0.00) {
                        csh_bt_save.setVisibility(View.VISIBLE);
                        if (check_total >= check_creditamount) {
                            in_cash.setSelected(true);
                            list_size = list_size + 1;
                            selectedPosition = in_cash.getIn_Position();
                            in_cash.setSplit_Position(selectedPosition);

                            d1 = Double.parseDouble(in_cash.getIn_Bal());
                            total = d1 + total;
                            total = Math.round(total * 100.0) / 100.0;
                            outstanding = 0.00;
                            returnAmount = 0.00;
                            Log.d("splitSeleCreditBalance", "splitSelectedCreditBalance");

                            /*String totalcredit = srTotalCredit.getText().toString();
                            double totcred= 0;
                            if(totalcredit != null && !totalcredit.isEmpty()){
                                totcred = Double.parseDouble(totalcredit);
                            }
                            Log.d("totalcredit in dialog",""+totcred);*/

                            in_cash.setAdd_credit(twoDecimalPoint(check_creditamount));
                            double finalValue = check_total - check_creditamount;
                            check_creditamount = 0.00;
//                            in_cash.setAdd_bal(twoDecimalPoint(finalValue));
//                            in_cash.setAdd_paid(in_cash.getIn_Paid());
                            in_cash.setAdd_paid(twoDecimalPoint(finalValue)); // added on 23.03.2017
                            in_cash.setAdd_bal(in_cash.getIn_Paid());

                            Log.d("check_creditamount", ">=" + check_creditamount);
                            //	in_cash.setIn_Credit(String.valueOf(check_creditamount));

                            Log.d("in_cash.getIn_Credit", ">=" + in_cash.getIn_Credit());

                            Log.d("checktotal>=check_net", finalValue + " , " + check_total + " , " + check_creditamount);
                            //notifyDataSetChanged();
                        } else if (check_total < check_creditamount) {
                            in_cash.setSelected(true);
                            list_size = list_size + 1;
                            selectedPosition = in_cash.getIn_Position();
                            in_cash.setSplit_Position(selectedPosition);

                            d1 = Double.parseDouble(in_cash.getIn_Bal());
                            total = d1 + total;
                            total = Math.round(total * 100.0) / 100.0;
                            outstanding = 0.00;
                            returnAmount = 0.00;
                            Log.d("splitSelecCreditBalance", "splitSelectedCreditBalance");
                            double finalValue = check_creditamount - check_total;
                            check_creditamount = Math.round(finalValue * 100.0) / 100.0;
                            in_cash.setAdd_bal("0.00");
                            in_cash.setAdd_paid(in_cash.getIn_Paid());
                            in_cash.setAdd_credit(twoDecimalPoint(check_total));

                            Log.d("check_creditamount", "<" + check_creditamount);
                            Log.d("in_cash.getIn_Credit", "<" + in_cash.getIn_Credit());
                            Log.d("total < check_netamo", finalValue + " , " + check_total + " , " + check_creditamount);
                        }
                    } else {
                        in_cash.setSelected(false);
                    }
                }
            }

//			if (list_size == listarray.size()) {
//				csh_checkall.setChecked(true);
//
//			}

            getTotalValue();
        }

        public void splitCreditBalance(double creditVlaue) {

            double check_creditamount = creditVlaue;
            int j = listarray.size();
            for (int i = j; i > 0; i--) {
                Log.d("i value", "" + i);
                final In_Cash in_cash = Adapter.getItem(i - 1);
               /* if (in_cash.isSelected()) {
                    Log.d("splitCreditBala checked", "checked");
                } else {

                    Log.d("splitCreditBalunchecked", "unchecked");
                }
*/
                double check_total = Double.parseDouble(in_cash.getIn_Bal());
                Log.d("total check_netamount", check_total + " , " + check_creditamount);
                //in_cash.setIn_Credit(String.valueOf(check_creditamount));
                if (check_creditamount > 0.00) {
                    csh_bt_save.setVisibility(View.VISIBLE);
                    if (check_total >= check_creditamount) {
                        in_cash.setSelected(true);
                        list_size = list_size + 1;
                        selectedPosition = in_cash.getIn_Position();
                        in_cash.setSplit_Position(selectedPosition);

                        d1 = Double.parseDouble(in_cash.getIn_Bal());
                        total = d1 + total;
                        total = Math.round(total * 100.0) / 100.0;
                        outstanding = 0.00;
                        returnAmount = 0.00;
                        Log.d("splitCreditBalance", "splitCreditBalance");

                        in_cash.setAdd_credit(twoDecimalPoint(check_creditamount));
                        double finalValue = check_total - check_creditamount;
                        check_creditamount = 0.00;
                        in_cash.setAdd_bal(twoDecimalPoint(finalValue));
                        in_cash.setAdd_paid(in_cash.getIn_Paid());
                        Log.d("check_creditamount", "<" + check_creditamount);


                        Log.d("in_cash.getIn_Credit", "<" + in_cash.getIn_Credit());

                        Log.d("checktotal>=check_net", finalValue + " , " + check_total + " , " + check_creditamount);
                        //notifyDataSetChanged();
                    } else if (check_total < check_creditamount) {
                        in_cash.setSelected(true);
                        list_size = list_size + 1;
                        selectedPosition = in_cash.getIn_Position();
                        in_cash.setSplit_Position(selectedPosition);

                        d1 = Double.parseDouble(in_cash.getIn_Bal());
                        total = d1 + total;
                        total = Math.round(total * 100.0) / 100.0;
                        outstanding = 0.00;
                        returnAmount = 0.00;
                        Log.d("splitCreditBalance", "splitCreditBalance");
                        //in_cash.setIn_Credit(String.valueOf(check_creditamount));
                        double finalValue = check_creditamount - check_total;
                        check_creditamount = Math.round(finalValue * 100.0) / 100.0;
                        in_cash.setAdd_bal("0.00");
                        in_cash.setAdd_paid(in_cash.getIn_Paid());
                        in_cash.setAdd_credit(twoDecimalPoint(check_total));
                        Log.d("check_creditamount", "<" + check_creditamount);


                        Log.d("in_cash.getIn_Credit", "<" + in_cash.getIn_Credit());
                        Log.d("total < check_netamo", finalValue + " , " + check_total + " , " + check_creditamount);
                    }
                }
            }

            if (list_size == listarray.size()) {
                csh_checkall.setChecked(true);

            }

            getTotalValue();
        }


        public void paidBalance(int pos, double pd, double cd, double blnc) {

            double pcd = 0.0, pd_blnc = 0.00, blnc_pd = 0.00, finalValue1 = 0.00, finalValue2 = 0.00;
            In_Cash in_so = Adapter.getItem(pos);

            pcd = pd + cd;
            if (pcd >= blnc) {
                in_so.setAdd_credit(String.valueOf(cd));
                in_so.setAdd_bal(in_so.getIn_Paid());
                in_so.setAdd_paid(String.valueOf(pd));
                pd_blnc = pcd - blnc;
                finalValue1 = Math.round(pd_blnc * 100.0) / 100.0;
                returnAmount = finalValue1;
                outstanding = 0.00;
            } else {
                blnc_pd = blnc - pcd;
                finalValue2 = Math.round(blnc_pd * 100.0) / 100.0;
                in_so.setAdd_credit(String.valueOf(cd));
                in_so.setAdd_paid(String.valueOf(pd));
                in_so.setAdd_bal(String.valueOf(finalValue2));
                outstanding = finalValue2;
                returnAmount = 0.00;
            }
            notifyDataSetChanged();
            getTotalValue();
        }

        public void remove(In_Cash item) {
            listarray.remove(item);
        }

        public boolean isAllSelected() {
            for (In_Cash in_Cash : listarray) {
                if (!in_Cash.isSelected()) {
                    return false;
                }
            }
            return true;
        }

        public double getTotal() {
            double total = 0.00;
            for (In_Cash in_Cash : listarray) {
                if (in_Cash.isSelected()) {
                    try {
                        String credit = in_Cash.getAdd_credit();
                        if (credit != null && !credit.isEmpty()) {
                            total += Double.valueOf(credit);
                        }
                    } catch (NumberFormatException e) {
                        total = 0.00;
                    }
                }
            }
            Log.d("total", "---?" + total);
            return total;
        }
        //created on 27/02/2016 by saravana
        public int checkLastSelectedItem(){
            int select = 0;
            In_Cash in_Cash = listarray.get(listarray.size()-1);
            if (in_Cash.isSelected()) {
                select = select + 1;
            }
            return select;
        }

        public double getBalance() {
            double total = 0.00;
            for (In_Cash in_Cash : listarray) {
                if (in_Cash.isSelected()) {
                    Log.d("In balance", "true" + "true");
                    try {
                        String balance = in_Cash.getIn_Bal(); // total balance
                        String balance1 = in_Cash.getAdd_bal(); // balance after paid
                      Log.d("In balance", "---?" + balance);
                       Log.d("Ad balance1", "---?" + balance1);
                        if (balance1 != null && !balance1.isEmpty()) {
                            total = Double.valueOf(balance1);
                          //  Log.d("balance", "---?" + total);
                        }
                    } catch (NumberFormatException e) {
                        total = 0.00;
                    }
                }
            }
          //  Log.d("balance", "---?" + total);
            return total;
        }

        public double getTotalBalance() {
            double balanceTot = 0.00,balanceTot1=0.00,balan=0.00;
            for (In_Cash in_Cash : listarray) {
                if (in_Cash.isSelected()) {
                    Log.d("etTotalBalance", "true" + "true");
                    try {
                       // String balance = in_Cash.getIn_Bal();
                        String addBalance= in_Cash.getAdd_bal();

                      //  String inCredit = in_Cash.getIn_Credit();
                        //String addCredit = in_Cash.getAdd_credit();

                        Log.d("In tot addBalance", "---?" + addBalance);
                       // Log.d("Ad tot balance1", "---?" + balance1);

                       // Log.d("In tot inCredit", "---?" + inCredit);
                       // Log.d("Ad tot addCredit", "---?" + addCredit);

                        if (/*addCredit != null && !addCredit.isEmpty() && */addBalance != null && !addBalance.isEmpty()) {
                            balanceTot = Double.valueOf(addBalance);
                          //  balanceTot1 = Double.valueOf(addBalance);
                          //  balan = balanceTot - balanceTot1;
                             Log.d("balan", "---" + balan);
                        }
                    } catch (NumberFormatException e) {
                        balan = 0.00;
                    }
                }
            }
              Log.d("getTotalBalance", "--->" + balan);
            return balan;
        }

        public int getSelected() {
            int select = 0;
            for (In_Cash in_Cash : listarray) {
                if (in_Cash.isSelected()) {
                    select = select + 1;
                    Log.d("SR inner select", ""+select);
                }
            }
            Log.d("SR select", ""+select);
            return select;
        }

        public void selectAll(boolean select) {
            double temp = 0.0;
            for (In_Cash in_Cash : listarray) {

                if (select == true) {
                    in_Cash.setSelected(select);
                    selectedPosition = in_Cash.getIn_Position();
                    in_Cash.setSplit_Position(selectedPosition);
                    temp += Double.parseDouble(in_Cash.getIn_Bal());
                    Log.d("True selectedPosition",
                            "" + String.valueOf(selectedPosition));
                    Log.d("True TEMP", String.valueOf(temp));
                    in_Cash.setAdd_paid(in_Cash.getIn_Bal());
                    in_Cash.setAdd_bal(in_Cash.getIn_Paid());
                    in_Cash.setAdd_credit("0.00");
                } else if (select == false) {
                    in_Cash.setSelected(select);
                    selectedPosition = -1;
                    in_Cash.setSplit_Position(-1);
                    temp += Double.parseDouble(in_Cash.getIn_Bal());
                    Log.d("False selectedPosition",
                            String.valueOf(selectedPosition));
                    Log.d("False TEMP ", String.valueOf(temp));
                    in_Cash.setAdd_paid("0.00");
                    in_Cash.setAdd_bal("0.00");


              //      in_Cash.setIn_Credit("0.00");
                  //  Log.d("unchecked add credit", in_cash.getAdd_credit());
                 //   Log.d("unchecked in credit", in_cash.getIn_Credit());
                }

            }
            total = Math.round(temp * 100.0) / 100.0;
            notifyDataSetChanged();
            getTotalValue();
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public void setSelectedPosition(int selectedPosition) {
            this.selectedPosition = selectedPosition;
            notifyDataSetChanged();
        }

        public void getTotalValue() {
            total_paid = 0.00;
            total_credit = 0.00;
            total_balance = 0.00;
            totla_remianing_outstanding = 0.00;
            int size = listarray.size();
            if (size > 0) {
                for (int position = 0; position < size; position++) {
                    Log.d("position value", "" + position);
                    In_Cash cash = Adapter.getItem(position);
                    int spt_pos = cash.getSplit_Position();

                    if (position == Pos || position == spt_pos) {
                        total_paid += Double.parseDouble(cash.getAdd_paid());
                        total_credit += Double.parseDouble(cash.getAdd_credit());
//					total_balance += Double.parseDouble(cash.getAdd_bal());
                    } else {
                        total_paid += Double.parseDouble(cash.getIn_Paid());
                        total_credit += Double.parseDouble(cash.getIn_Credit());
//					total_balance += Double.parseDouble(cash.getAdd_bal());
                    }

                }
            }

//			total_outstanding = Math.round(total_outstanding * 100.0) / 100.0;
//			total_salesreturn = Math.round(total_salesreturn * 100.0) / 100.0;
            total_paid = Math.round(total_paid * 100.0) / 100.0;
            total_credit = Math.round(total_credit * 100.0) / 100.0;
//			total_balance = total_outstanding - (total_paid+total_credit);
            totla_remianing_outstanding = (total_outstanding - total_salesreturn);
            total_balance = totla_remianing_outstanding - (total_paid);
            totla_remianing_outstanding = Math.round(totla_remianing_outstanding * 100.0) / 100.0;

            total_balance = Math.round(total_balance * 100.0) / 100.0;
            csh_tot_outstanding.setText(total_outstanding + "");
            csh_tot_salesreturn.setText(total_salesreturn + "");
            csh_tot_paid.setText(total_paid + "");
            csh_tot_credit.setText(total_credit + "");
            csh_tot_balance.setText(total_balance + "");
            csh_remaining_outstanding.setText(totla_remianing_outstanding + "");

            totalpaid_btn.setText(getResources().getString(R.string.total_paid)+" : " + total_paid + "");
            totalcredit_btn.setText(getResources().getString(R.string.total_credit)+" : " +total_credit + "");

        }

        final class InvoiceHolder {
            TextView inDate;
            TextView inNetvalue;
            TextView inCredit;
            TextView inPaid;
            TextView inBalance;
            CheckBox inCheckbox;

            TextView in_oldCreditAmount; // added new
            TextView in_oldPaidAmount;
            TextView in_no;

        }
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);

        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.locationspinner);
            ImageView icon = (ImageView) row.findViewById(R.id.spinnericon);
            label.setText(arr[position]);
            icon.setVisibility(View.GONE);
            return row;
        }
    }

    private class OfflineInvoicePrintCall extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            product.clear();
            productdet.clear();
            Log.d("pre", "s" + "preexecute");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Log.d("size", "s" + ListArray.size());

            String showcartonloose = SalesOrderSetGet
                    .getCartonpriceflag();

            for (int i = 0; i < ListArray.size(); i++) {
                final In_Cash in_cash = Adapter.getItem(i);

                if (in_cash.isSelected()) {

                    String str4 = in_cash.getIn_InvNo();
                    Log.d("InvoiceNo", str4);


                    String offlinePrintStatus = OfflineDatabase.getPrintStatus("tblGetInvoiceDetail", "InvoiceNo", str4);
                    Log.d("offlinePrintStatus", "" + offlinePrintStatus);
                    if (offlinePrintStatus != null && !offlinePrintStatus.isEmpty()) {

                        String cmpnyCode = SupplierSetterGetter.getCompanyCode();
                        hm.put("CompanyCode", cmpnyCode);
                        hm.put("InvoiceNo", str4);

                        HashMap<String, String> custhash = new HashMap<String, String>();
                        custhash.put("CompanyCode", cmpnyCode);
                        custhash.put("CustomerCode", customerCode);

                        if (onlineMode.matches("True")) {
                            if (checkOffline == true) {
                                // Offline
//							jsonString = offlineDatabase
//									.getInvoiceHeaderDetail(hm);

                                if (showcartonloose.equalsIgnoreCase("1")) {
                                    jsonString = offlineDatabase
                                            .getInvoiceDetailPrint(hm);

                                } else {
                                    jsonString = offlineDatabase
                                            .getInvoiceHeaderDetail(hm);
                                }

                                jsonStr = offlineDatabase.getInvoiceHeader(hm);
                                custjsonStr = offlineDatabase
                                        .getCustomerDetail(custhash);
                            } else { // online
                            }
                        } else if (onlineMode.matches("False")) { // perman offline
                            // Offline
//						jsonString = offlineDatabase.getInvoiceHeaderDetail(hm);

                            if (showcartonloose.equalsIgnoreCase("1")) {
                                jsonString = offlineDatabase
                                        .getInvoiceDetailPrint(hm);

                            } else {
                                jsonString = offlineDatabase
                                        .getInvoiceHeaderDetail(hm);
                            }

                            jsonStr = offlineDatabase.getInvoiceHeader(hm);
                            custjsonStr = offlineDatabase
                                    .getCustomerDetail(custhash);

                        }

                        Log.d("jsonString ", jsonString);
                        Log.d("jsonStr ", jsonStr);
                        Log.d("custjsonStr ", custjsonStr);

                        try {

                            jsonResponse = new JSONObject(jsonString);
                            jsonMainNode = jsonResponse.optJSONArray("SODetails");

                            jsonResp = new JSONObject(jsonStr);
                            jsonSecNode = jsonResp.optJSONArray("SODetails");

                            custjsonResp = new JSONObject(custjsonStr);
                            custjsonMainNode = custjsonResp.optJSONArray("SODetails");

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        int custJsonArr = custjsonMainNode.length();
                        for (int j = 0; j < custJsonArr; j++) {

                            JSONObject jsonChildNode;

                            try {
                                jsonChildNode = custjsonMainNode.getJSONObject(j);

                                String CustomerCode = jsonChildNode.optString(
                                        "CustomerCode").toString();
                                String CustomerName = jsonChildNode.optString(
                                        "CustomerName").toString();
                                String Address1 = jsonChildNode.optString(
                                        "Address1").toString();
                                String Address2 = jsonChildNode.optString(
                                        "Address2").toString();
                                String Address3 = jsonChildNode.optString(
                                        "Address3").toString();
                                String PhoneNo = jsonChildNode.optString(
                                        "PhoneNo").toString();
                                String HandphoneNo = jsonChildNode.optString(
                                        "HandphoneNo").toString();
                                String Email = jsonChildNode.optString(
                                        "Email").toString();
                                String TermName = jsonChildNode.optString(
                                        "TermName").toString();
                                String OutstandingAmount = jsonChildNode.optString(
                                        "OutstandingAmount").toString();
                                String TaxValue = jsonChildNode.optString("TaxValue")
                                        .toString();
                                CustomerSetterGetter.setCustomerCode(CustomerCode);
                                CustomerSetterGetter.setCustomerName(CustomerName);
                                CustomerSetterGetter.setCustomerAddress1(Address1);
                                CustomerSetterGetter.setCustomerAddress2(Address2);
                                CustomerSetterGetter.setCustomerAddress3(Address3);
                                CustomerSetterGetter.setCustomerPhone(PhoneNo);
                                CustomerSetterGetter.setCustomerHP(HandphoneNo);
                                CustomerSetterGetter.setCustomerEmail(Email);
                                CustomerSetterGetter.setCustomerTerms(TermName);
                                CustomerSetterGetter.setTotalOutstanding(OutstandingAmount);
                                SalesOrderSetGet.setCustomerTaxPerc(TaxValue);
                                Log.d("mobile settings Cus", CustomerSetterGetter.getCustomerCode());

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }


                        /*********** Process each JSON Node ************/
                        int lengJsonArr = jsonSecNode.length();
                        for (int j = 0; j < lengJsonArr; j++) {
                            /****** Get Object for each JSON node. ***********/
                            JSONObject jsonChildNode;
                            ProductDetails productdetail = new ProductDetails();
                            try {
                                jsonChildNode = jsonSecNode.getJSONObject(j);
                                productdetail.setItemno(jsonChildNode.optString(
                                        "InvoiceNo").toString());
                                String dateTime = jsonChildNode.optString(
                                        "InvoiceDate").toString();
                                StringTokenizer tokens = new StringTokenizer(
                                        dateTime, " ");
                                String invDate = tokens.nextToken();
                                productdetail.setItemdate(invDate);
                                productdetail.setItemdisc(jsonChildNode.optString(
                                        "ItemDiscount").toString());
                                productdetail.setBilldisc(jsonChildNode.optString(
                                        "BillDIscount").toString());
                                productdetail.setSubtotal(jsonChildNode.optString(
                                        "SubTotal").toString());
                                productdetail.setTax(twoDecimalPoint(jsonChildNode
                                        .optDouble("Tax")));
                                productdetail.setTax(twoDecimalPoint(jsonChildNode
                                        .optDouble("Tax")));
                                productdetail.setNettot(jsonChildNode.optString(
                                        "NetTotal").toString());
                                productdetail.setPaidamount(jsonChildNode
                                        .optString("PaidAmount").toString());
                                productdetail.setCreditAmount(jsonChildNode.optString(
                                        "CreditAmount").toString());
                                productdetail.setRemarks(jsonChildNode.optString(
                                        "Remarks").toString());
                                productdetail.setTotaloutstanding(jsonChildNode
                                        .optString("TotalBalance").toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            /*********** Process each JSON Node ************/
                            int lengthJsonAr = jsonMainNode.length();

                            if (showcartonloose.equalsIgnoreCase("1")) {

                                for (int k = 0; k < lengthJsonAr; k++) {
                                    /*** Get Object for each JSON node. *****/
                                    ProdDetails proddetails = new ProdDetails();
                                    try {
                                        JSONObject jsonChildNodes = jsonMainNode
                                                .getJSONObject(k);
                                        String transType = jsonChildNodes.optString(
                                                "TranType").toString();
                                        int s = k + 1;
                                        proddetails.setSno(String.valueOf(s));

                                        proddetails.setItemnum(jsonChildNodes.optString(
                                                "InvoiceNo").toString());
                                        proddetails.setItemcode(jsonChildNodes.optString(
                                                "ProductCode").toString());

                                        proddetails.setDescription(jsonChildNodes
                                                .optString("ProductName").toString());

                                        proddetails.setUOMCode(jsonChildNodes
                                                .optString("UOMCode").toString());

                                        String uomCode = jsonChildNodes
                                                .optString("UOMCode").toString();

                                        if (uomCode != null && !uomCode.isEmpty()) {

                                        } else {
                                            uomCode = "";
                                        }

                                        if (uomCode.matches("null")) {
                                            uomCode = "";
                                        }

                                        Log.d("uomCode", "u " + uomCode);

                                        if (transType.equalsIgnoreCase("Ctn")) {
                                            String cQty = jsonChildNodes.optString("CQty")
                                                    .toString();

                                            String cPrice = jsonChildNodes.optString(
                                                    "CartonPrice").toString();

                                            if (cQty != null && !cQty.isEmpty()
                                                    && cPrice != null && !cPrice.isEmpty()) {
                                                proddetails.setQty(cQty.split("\\.")[0]);
                                                proddetails.setPrice(twoDecimalPoint(Double.valueOf(cPrice)));
                                                double cqty = Double.valueOf(cQty);
                                                double cprice = Double.valueOf(cPrice);
                                                double total = cqty * cprice;
                                                String tot = twoDecimalPoint(total);
                                                proddetails.setTotal(tot);
                                            } else {
                                                proddetails.setQty("0");
                                                proddetails.setPrice("0.00");
                                                proddetails.setTotal("0.00");
                                            }

                                        } else if (transType.equalsIgnoreCase("Loose")) {

                                            String lQty = jsonChildNodes.optString("LQty")
                                                    .toString();
                                            String lPrice = jsonChildNodes.optString(
                                                    "Price").toString();

                                            if (lQty != null && !lQty.isEmpty()
                                                    && lPrice != null && !lPrice.isEmpty()) {
                                                proddetails.setQty(lQty.split("\\.")[0] + " " + uomCode);
                                                proddetails.setPrice(twoDecimalPoint(Double.valueOf(lPrice)));
                                                double lqty = Double.valueOf(lQty);
                                                double lprice = Double.valueOf(lPrice);
                                                double total = lqty * lprice;
                                                String tot = twoDecimalPoint(total);
                                                proddetails.setTotal(tot);
                                            } else {
                                                proddetails.setQty("0" + " " + uomCode);
                                                proddetails.setPrice("0.00");
                                                proddetails.setTotal("0.00");
                                            }

                                        } else if (transType.equalsIgnoreCase("FOC")) {
                                            String focQty = jsonChildNodes.optString(
                                                    "FOCQty").toString();
                                            if (focQty != null && !focQty.isEmpty()) {
                                                proddetails.setQty(focQty.split("\\.")[0]);
                                            } else {
                                                proddetails.setQty("0" + " " + uomCode);
                                            }
                                            proddetails.setPrice("0.00");
                                            proddetails.setTotal("0.00");

                                        } else if (transType.equalsIgnoreCase("Exc")) {
                                            String excQty = jsonChildNodes.optString(
                                                    "ExchangeQty").toString();
                                            if (excQty != null && !excQty.isEmpty()) {
                                                proddetails.setQty(excQty.split("\\.")[0]);
                                            } else {
                                                proddetails.setQty("0" + " " + uomCode);
                                            }
                                            proddetails.setPrice("0.00");
                                            proddetails.setTotal("0.00");

                                        }

                                        if (gnrlStngs.matches("C")) {
                                            proddetails.setSortproduct(jsonChildNodes
                                                    .optString("CategoryCode").toString());
                                        } else if (gnrlStngs.matches("S")) {
                                            proddetails.setSortproduct(jsonChildNodes
                                                    .optString("SubCategoryCode")
                                                    .toString());
                                        } else if (gnrlStngs.matches("N")) {
                                            proddetails.setSortproduct("");
                                        } else {
                                            proddetails.setSortproduct("");
                                        }
                                        proddetails.setTax(jsonChildNodes.optString("Tax").toString());
                                        proddetails.setTaxType(jsonChildNodes.optString("TaxType").toString());
                                        proddetails.setTaxPerc(jsonChildNodes.optString("TaxPerc").toString());
                                        proddetails.setSubtotal(jsonChildNodes.optString("SubTotal").toString());
                                        product.add(proddetails);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else {

                                for (int k = 0; k < lengthJsonAr; k++) {
                                    /****** Get Object for each JSON node. ***********/
                                    JSONObject jsonChildNodes;
                                    ProdDetails proddetails = new ProdDetails();
                                    try {
                                        jsonChildNodes = jsonMainNode.getJSONObject(k);
                                        int s = k + 1;
                                        proddetails.setSno(String.valueOf(s));
                                        proddetails.setItemnum(jsonChildNodes
                                                .optString("InvoiceNo").toString());
                                        proddetails.setItemcode(jsonChildNodes
                                                .optString("ProductCode").toString());
                                        proddetails.setDescription(jsonChildNodes
                                                .optString("ProductName").toString());

                                        String cshqty = jsonChildNodes.optString("Qty")
                                                .toString();
                                        if (cshqty.contains(".")) {
                                            StringTokenizer tokens = new StringTokenizer(
                                                    cshqty, ".");
                                            String qty = tokens.nextToken();
                                            proddetails.setQty(qty);
                                        } else {
                                            proddetails.setQty(cshqty);
                                        }

                                        proddetails.setPrice(jsonChildNodes.optString(
                                                "Price").toString());
                                        proddetails.setTotal(jsonChildNodes.optString(
                                                "Total").toString());

                                        String focvalue = jsonChildNodes.optString("FOCQty").toString().split("\\.")[0];

                                        String ExchangeQty = jsonChildNodes.optString("ExchangeQty").toString().split("\\.")[0];

                                        Log.d("foc", focvalue);

                                        Log.d("exchange", ExchangeQty);

                                        if (focvalue != null && !focvalue.isEmpty()) {
                                            proddetails.setFocqty(Integer.valueOf(focvalue));
                                        } else {
                                            proddetails.setFocqty(0);
                                        }

                                        if (ExchangeQty != null && !ExchangeQty.isEmpty()) {
                                            proddetails.setExchangeqty(Integer.valueOf(ExchangeQty));
                                        } else {
                                            proddetails.setExchangeqty(0);
                                        }

                                        if (gnrlStngs.matches("C")) {
                                            proddetails.setSortproduct(jsonChildNodes
                                                    .optString("CategoryCode")
                                                    .toString());
                                        } else if (gnrlStngs.matches("S")) {
                                            proddetails.setSortproduct(jsonChildNodes
                                                    .optString("SubCategoryCode")
                                                    .toString());
                                        } else if (gnrlStngs.matches("N")) {
                                            proddetails.setSortproduct("");
                                        } else {
                                            proddetails.setSortproduct("");
                                        }

                                        proddetails.setTax(jsonChildNodes.optString("Tax").toString());
                                        proddetails.setTaxType(jsonChildNodes.optString("TaxType").toString());
                                        proddetails.setTaxPerc(jsonChildNodes.optString("TaxPerc").toString());
                                        proddetails.setSubtotal(jsonChildNodes.optString("SubTotal").toString());

                                        product.add(proddetails);
                                    } catch (JSONException e) {

                                        e.printStackTrace();
                                    }
                                }
                            }
                            productdetail.setProductsDetails(product);
                            productdet.add(productdetail);
                        }

                    }
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            hm.clear();
            if (enableprint.isChecked()) {
                try {
                    Log.d("print product size",""+productdet.size());
                    if (productdet.size() > 0) {
                        sortCatagory();
                        prints();
                    } else {

                        OnlineInvoiceOfflinePrint();
					/*	helper.dismissProgressDialog();
						Toast.makeText(InvoiceCashCollection.this,
								"Online Invoice Cash Collection cannot be print", Toast.LENGTH_SHORT)
								.show();

						if(header.matches("InvoiceHeader")){
						      intent = new Intent(InvoiceCashCollection.this,
						       InvoiceHeader.class);

						     }
						     else if(header.matches("CustomerHeader")){
						      intent = new Intent(InvoiceCashCollection.this,
						        CustomerListActivity.class);

						     }else if (header.matches("RouteHeader")) {
									intent = new Intent(InvoiceCashCollection.this,
											RouteHeader.class);
								}
						     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						     startActivity(intent);
						InvoiceCashCollection.this.finish();*/
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class OnlineInvoicePrintCall extends AsyncTask<Void, Void, Void> {
        // Do Offline
        @Override
        protected void onPreExecute() {
            product.clear();
            productdet.clear();
            footerArr.clear();
            sngleArray.clear();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String showcartonloose = SalesOrderSetGet.getCartonpriceflag();
            String cmpnyCode = SupplierSetterGetter.getCompanyCode();
            hashVl.put("CompanyCode", cmpnyCode);

            Log.d("size", "s" + ListArray.size());
            for (int i = 0; i < ListArray.size(); i++) {
                final In_Cash in_cash = Adapter.getItem(i);

                if (in_cash.isSelected()) {
                    String str4 = in_cash.getIn_InvNo();
                    Log.d("InvoiceNo", str4);

                    hm.put("CompanyCode", cmpnyCode);
                    hm.put("InvoiceNo", str4);


                    /********
                     * Show Default carton,loose,foc,exchange qty and price
                     * Based On General settings
                     *********/
                    if (showcartonloose.equalsIgnoreCase("1")) {
                        jsonString = SalesOrderWebService.getSODetail(
                                hm, "fncGetInvoiceDetailWithCarton");

                    } else {
                        jsonString = SalesOrderWebService.getSODetail(hm,
                                "fncGetInvoiceDetail");
                    }

                    HashMap<String, String> rdhashmap = new HashMap<String, String>();
                    rdhashmap.put("CompanyCode", cmpnyCode);
                    rdhashmap.put("ReceiptNo", saveResult);

                    /*********** Receipt Detail ************/
                    rdjsonStr = SalesOrderWebService.getSODetail(rdhashmap,
                            "fncGetReceiptDetail");

                    jsonStr = SalesOrderWebService.getSODetail(hm,
                            "fncGetInvoiceHeaderByInvoiceNo");
                    jsonSt = SalesOrderWebService.getSODetail(hashVl, "fncGetMobilePrintFooter");

                    HashMap<String, String> custhash = new HashMap<String, String>();

                    custhash.put("CompanyCode", cmpnyCode);
                    custhash.put("CustomerCode", customerCode);

                    custjsonStr = SalesOrderWebService.getSODetail(custhash,
                            "fncGetCustomer");

                    Log.d("jsonString ", jsonString);
                    Log.d("jsonStr ", jsonStr);
                    Log.d("custjsonStr ", custjsonStr);
                    Log.d("jsonSt", "" + jsonSt);
                    Log.d("rdjsonStr", "" + rdjsonStr);
                    try {

                        jsonResponse = new JSONObject(jsonString);
                        jsonMainNode = jsonResponse.optJSONArray("SODetails");

                        jsonResp = new JSONObject(jsonStr);
                        jsonSecNode = jsonResp.optJSONArray("SODetails");

                        custjsonResp = new JSONObject(custjsonStr);
                        custjsonMainNode = custjsonResp.optJSONArray("SODetails");

                        jsonRespfooter = new JSONObject(jsonSt);
                        jsonSecNodefooter = jsonRespfooter.optJSONArray("SODetails");

                        rdjsonResp = new JSONObject(rdjsonStr);
                        rdjsonSecNode = rdjsonResp.optJSONArray("SODetails");

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    int custJsonArr = custjsonMainNode.length();
                    for (int j = 0; j < custJsonArr; j++) {

                        JSONObject jsonChildNode;

                        try {
                            jsonChildNode = custjsonMainNode.getJSONObject(j);

                            String CustomerCode = jsonChildNode.optString(
                                    "CustomerCode").toString();
                            String CustomerName = jsonChildNode.optString(
                                    "CustomerName").toString();
                            String Address1 = jsonChildNode.optString(
                                    "Address1").toString();
                            String Address2 = jsonChildNode.optString(
                                    "Address2").toString();
                            String Address3 = jsonChildNode.optString(
                                    "Address3").toString();
                            String PhoneNo = jsonChildNode.optString(
                                    "PhoneNo").toString();
                            String HandphoneNo = jsonChildNode.optString(
                                    "HandphoneNo").toString();
                            String Email = jsonChildNode.optString(
                                    "Email").toString();
                            String TermName = jsonChildNode.optString(
                                    "TermName").toString();
                            String OutstandingAmount = jsonChildNode.optString(
                                    "OutstandingAmount").toString();
                            String TaxValue = jsonChildNode.optString("TaxValue")
                                    .toString();


                            CustomerSetterGetter.setCustomerCode(CustomerCode);
                            CustomerSetterGetter.setCustomerName(CustomerName);
                            CustomerSetterGetter.setCustomerAddress1(Address1);
                            CustomerSetterGetter.setCustomerAddress2(Address2);
                            CustomerSetterGetter.setCustomerAddress3(Address3);
                            CustomerSetterGetter.setCustomerPhone(PhoneNo);
                            CustomerSetterGetter.setCustomerHP(HandphoneNo);
                            CustomerSetterGetter.setCustomerEmail(Email);
                            CustomerSetterGetter.setCustomerTerms(TermName);
                            CustomerSetterGetter.setTotalOutstanding(OutstandingAmount);
                            SalesOrderSetGet.setCustomerTaxPerc(TaxValue);
                            Log.d("mobile settings C", CustomerSetterGetter.getCustomerCode());

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                    String invNo="";
                    /*********** Process each JSON Node ************/
                    int lengJsonArr = jsonSecNode.length();
                    for (int j = 0; j < lengJsonArr; j++) {
                        /****** Get Object for each JSON node. ***********/
                        JSONObject jsonChildNode;
                        ProductDetails productdetail = new ProductDetails();
                        try {
                            jsonChildNode = jsonSecNode.getJSONObject(j);

                            invNo = jsonChildNode.optString(
                                    "InvoiceNo").toString();
                            productdetail.setItemno(invNo);
                            String dateTime = jsonChildNode.optString(
                                    "InvoiceDate").toString();
                            StringTokenizer tokens = new StringTokenizer(
                                    dateTime, " ");
                            String invDate = tokens.nextToken();
                            productdetail.setItemdate(invDate);
                            productdetail.setItemdisc(jsonChildNode.optString(
                                    "ItemDiscount").toString());
                            productdetail.setBilldisc(jsonChildNode.optString(
                                    "BillDIscount").toString());
                            productdetail.setSubtotal(jsonChildNode.optString(
                                    "SubTotal").toString());
                            productdetail.setTax(twoDecimalPoint(jsonChildNode
                                    .optDouble("Tax")));
//                            productdetail.setTax(twoDecimalPoint(jsonChildNode
//                                    .optDouble("Tax")));
                            productdetail.setNettot(jsonChildNode.optString(
                                    "NetTotal").toString());
                            productdetail.setPaidamount(jsonChildNode
                                    .optString("PaidAmount").toString());
                            productdetail.setCreditAmount(jsonChildNode.optString(
                                    "CreditAmount").toString());
                            productdetail.setRemarks(jsonChildNode.optString(
                                    "Remarks").toString());
                            productdetail.setTotaloutstanding(jsonChildNode
                                    .optString("TotalBalance").toString());
                            productdetail.setBalanceAmount(jsonChildNode
                                    .optString("BalanceAmount").toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        /*********** Receipt Detail ************/
                        int lengArr = rdjsonSecNode.length();

                            for (int a = 0; a < lengArr; a++) {
                                /****** Get Object for each JSON node. ***********/
                                JSONObject jsonNode;
//                            ProductDetails proddet = new ProductDetails();
                                try {
                                    jsonNode = rdjsonSecNode.getJSONObject(a);
                                    String recinvno = jsonNode.optString(
                                            "InvoiceNo").toString();

                                    if (invNo.matches(recinvno)) {
                                        productdetail.setPaidamount(jsonNode.optString(
                                                "PaidAmount").toString());
                                        productdetail.setCreditAmount(jsonNode.optString(
                                                "CreditAmount").toString());
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        /****** End *******/


                        /******
                         * Show Default carton,loose,foc,exchange qty and price
                         * Based On General settings
                         *******/
                        /*** Process each JSON Node *****/
                        int lengthJsonArr = jsonMainNode.length();
                        if (showcartonloose.equalsIgnoreCase("1")) {

                            for (int k = 0; k < lengthJsonArr; k++) {
                                /*** Get Object for each JSON node. *****/
                                ProdDetails proddetails = new ProdDetails();
                                try {
                                    JSONObject jsonChildNodes = jsonMainNode
                                            .getJSONObject(k);
                                    String transType = jsonChildNodes.optString(
                                            "TranType").toString();
                                    int s = k + 1;
                                    proddetails.setSno(String.valueOf(s));

                                    proddetails.setItemnum(jsonChildNodes.optString(
                                            "InvoiceNo").toString());
                                    proddetails.setItemcode(jsonChildNodes.optString(
                                            "ProductCode").toString());

                                    proddetails.setDescription(jsonChildNodes
                                            .optString("ProductName").toString());

                                    proddetails.setUOMCode(jsonChildNodes
                                            .optString("UOMCode").toString());

                                    String uomCode = jsonChildNodes
                                            .optString("UOMCode").toString();

                                    if (uomCode != null && !uomCode.isEmpty()) {

                                    } else {
                                        uomCode = "";
                                    }

                                    if (uomCode.matches("null")) {
                                        uomCode = "";
                                    }

                                    Log.d("uomCode", "u " + uomCode);

                                    if (transType.equalsIgnoreCase("Ctn")) {
                                        String cQty = jsonChildNodes.optString("CQty")
                                                .toString();

                                        String cPrice = jsonChildNodes.optString(
                                                "CartonPrice").toString();

                                        if (cQty != null && !cQty.isEmpty()
                                                && cPrice != null && !cPrice.isEmpty()) {
                                            proddetails.setQty(cQty.split("\\.")[0]);
                                            proddetails.setPrice(twoDecimalPoint(Double.valueOf(cPrice)));
                                            double cqty = Double.valueOf(cQty);
                                            double cprice = Double.valueOf(cPrice);
                                            double total = cqty * cprice;
                                            String tot = twoDecimalPoint(total);
                                            proddetails.setTotal(tot);
                                        } else {
                                            proddetails.setQty("0");
                                            proddetails.setPrice("0.00");
                                            proddetails.setTotal("0.00");
                                        }

                                    } else if (transType.equalsIgnoreCase("Loose")) {

                                        String lQty = jsonChildNodes.optString("LQty")
                                                .toString();
                                        String lPrice = jsonChildNodes.optString(
                                                "Price").toString();

                                        if (lQty != null && !lQty.isEmpty()
                                                && lPrice != null && !lPrice.isEmpty()) {
                                            proddetails.setQty(lQty.split("\\.")[0] + " " + uomCode);
                                            proddetails.setPrice(twoDecimalPoint(Double.valueOf(lPrice)));
                                            double lqty = Double.valueOf(lQty);
                                            double lprice = Double.valueOf(lPrice);
                                            double total = lqty * lprice;
                                            String tot = twoDecimalPoint(total);
                                            proddetails.setTotal(tot);
                                        } else {
                                            proddetails.setQty("0" + " " + uomCode);
                                            proddetails.setPrice("0.00");
                                            proddetails.setTotal("0.00");
                                        }

                                    } else if (transType.equalsIgnoreCase("FOC")) {
                                        String focQty = jsonChildNodes.optString(
                                                "FOCQty").toString();
                                        if (focQty != null && !focQty.isEmpty()) {
                                            proddetails.setQty(focQty.split("\\.")[0]);
                                        } else {
                                            proddetails.setQty("0" + " " + uomCode);
                                        }
                                        proddetails.setPrice("0.00");
                                        proddetails.setTotal("0.00");

                                    } else if (transType.equalsIgnoreCase("Exc")) {
                                        String excQty = jsonChildNodes.optString(
                                                "ExchangeQty").toString();
                                        if (excQty != null && !excQty.isEmpty()) {
                                            proddetails.setQty(excQty.split("\\.")[0]);
                                        } else {
                                            proddetails.setQty("0" + " " + uomCode);
                                        }
                                        proddetails.setPrice("0.00");
                                        proddetails.setTotal("0.00");

                                    }

                                    if (gnrlStngs.matches("C")) {
                                        proddetails.setSortproduct(jsonChildNodes
                                                .optString("CategoryCode").toString());
                                    } else if (gnrlStngs.matches("S")) {
                                        proddetails.setSortproduct(jsonChildNodes
                                                .optString("SubCategoryCode")
                                                .toString());
                                    } else if (gnrlStngs.matches("N")) {
                                        proddetails.setSortproduct("");
                                    } else {
                                        proddetails.setSortproduct("");
                                    }
                                    proddetails.setTax(jsonChildNodes.optString("Tax").toString());
                                    proddetails.setTaxType(jsonChildNodes.optString("TaxType").toString());
                                    proddetails.setTaxPerc(jsonChildNodes.optString("TaxPerc").toString());
                                    proddetails.setSubtotal(jsonChildNodes.optString("SubTotal").toString());
                                    product.add(proddetails);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {


                            for (int k = 0; k < lengthJsonArr; k++) {
                                /****** Get Object for each JSON node. ***********/
                                JSONObject jsonChildNodes;
                                ProdDetails proddetails = new ProdDetails();
                                try {
                                    jsonChildNodes = jsonMainNode.getJSONObject(k);
                                    int s = k + 1;
                                    proddetails.setSno(String.valueOf(s));
                                    proddetails.setItemnum(jsonChildNodes
                                            .optString("InvoiceNo").toString());
                                    proddetails.setItemcode(jsonChildNodes
                                            .optString("ProductCode").toString());
                                    proddetails.setDescription(jsonChildNodes
                                            .optString("ProductName").toString());

                                    String cshqty = jsonChildNodes.optString("Qty")
                                            .toString();
                                    if (cshqty.contains(".")) {
                                        StringTokenizer tokens = new StringTokenizer(
                                                cshqty, ".");
                                        String qty = tokens.nextToken();
                                        proddetails.setQty(qty);
                                    } else {
                                        proddetails.setQty(cshqty);
                                    }

                                    proddetails.setPrice(jsonChildNodes.optString(
                                            "Price").toString());
                                    proddetails.setTotal(jsonChildNodes.optString(
                                            "Total").toString());

                                    String focvalue = jsonChildNodes.optString("FOCQty").toString().split("\\.")[0];

                                    String ExchangeQty = jsonChildNodes.optString("ExchangeQty").toString().split("\\.")[0];

                                    Log.d("foc", focvalue);

                                    Log.d("exchange", ExchangeQty);

                                    if (focvalue != null && !focvalue.isEmpty()) {
                                        proddetails.setFocqty(Integer.valueOf(focvalue));
                                    } else {
                                        proddetails.setFocqty(0);
                                    }

                                    if (ExchangeQty != null && !ExchangeQty.isEmpty()) {
                                        proddetails.setExchangeqty(Integer.valueOf(ExchangeQty));
                                    } else {
                                        proddetails.setExchangeqty(0);
                                    }

                                    if (gnrlStngs.matches("C")) {
                                        proddetails.setSortproduct(jsonChildNodes
                                                .optString("CategoryCode")
                                                .toString());
                                    } else if (gnrlStngs.matches("S")) {
                                        proddetails.setSortproduct(jsonChildNodes
                                                .optString("SubCategoryCode")
                                                .toString());
                                    } else if (gnrlStngs.matches("N")) {
                                        proddetails.setSortproduct("");
                                    } else {
                                        proddetails.setSortproduct("");
                                    }
                                    proddetails.setTax(jsonChildNodes.optString("Tax").toString());
                                    proddetails.setTaxType(jsonChildNodes.optString("TaxType").toString());
                                    proddetails.setTaxPerc(jsonChildNodes.optString("TaxPerc").toString());
                                    proddetails.setSubtotal(jsonChildNodes.optString("SubTotal").toString());
                                    product.add(proddetails);
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }
                            }
                        }
                        productdetail.setProductsDetails(product);
                        productdet.add(productdetail);
                    }
                }
            }

            /*********** Print Footer  ************/
            int lengJsonArr1 = jsonSecNodefooter.length();
            for (int i = 0; i < lengJsonArr1; i++) {
                /****** Get Object for each JSON node. ***********/
                JSONObject jsonChildNode;
                ProductDetails productdetail = new ProductDetails();
                try {
                    jsonChildNode = jsonSecNodefooter.getJSONObject(i);

                    String ReceiptMessage = jsonChildNode.optString(
                            "ReceiptMessage").toString();
                    String SortOrder = jsonChildNode.optString(
                            "SortOrder").toString();

                    productdetail.setReceiptMessage(ReceiptMessage);
                    productdetail.setSortOrder(SortOrder);
                    footerArr.add(productdetail);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            hashVl.clear();

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            hm.clear();
            if (enableprint.isChecked()) {
                try {
                    sortCatagory();
                    final ArrayList<HashMap<String, String>> salesreturnArr = getSalesReturnValue();
                    if(salesreturnArr.size()>0){
                        new SalesReturnDetail().execute();
                    }else{
                        prints();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class SalesReturnDetail extends AsyncTask<Void, Void,Void>{

        @Override
        protected void onPreExecute() {
            mSRHeaderDetailArr.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String companyCode = SupplierSetterGetter.getCompanyCode();
                hm.clear();
                hm.put("CompanyCode", companyCode);
                Log.d("salesReturnArray","-->"+salesReturnArray.size());
                for (int i = 0; i < salesReturnArray.size(); i++) {
                    String SRNumber = salesReturnArray.get(i).get("SalesReturnNo");
                    String balanceAmount = salesReturnArray.get(i).get("BalanceAmount");
                    String creditAmount = salesReturnArray.get(i).get("Total");

                    Log.d("SRNumber","-->"+SRNumber);
                    hm.put("SRNumber",SRNumber);

                    jsonStr = SalesOrderWebService.getSODetail(hm, "fncGetSalesReturnHeaderByInvoiceNo");
                    jsonResp = new JSONObject(jsonStr);
                    jsonSecNode = jsonResp.optJSONArray("SODetails");
                    int lengthSRHeaderJsonArr = jsonSecNode.length();


                    jsonString = SalesOrderWebService.getSODetail(hm, "fncGetSalesReturnDetail");
                    jsonResponse = new JSONObject(jsonString);
                    jsonMainNode = jsonResponse.optJSONArray("SODetails");
                    int lengthSRDetailJsonArr = jsonMainNode.length();

                    try {
                        for (int j = 0; j < lengthSRHeaderJsonArr; j++) {
                            /****** Get Object for each JSON node. ***********/
                            JSONObject jsonChildNode = jsonSecNode.getJSONObject(j);
                            ProductDetails productdetail = new ProductDetails();
                            String salesReturnNo = jsonChildNode.optString("SalesReturnNo").toString();
                            productdetail.setItemno(salesReturnNo);
                            productdetail.setItemdate(jsonChildNode.optString("SalesReturnDate").toString().split("\\ ")[0]);
                            productdetail.setItemdisc(jsonChildNode.optString("ItemDiscount").toString());
                            productdetail.setBilldisc(jsonChildNode.optString("BillDIscount").toString());
                            productdetail.setSubtotal(jsonChildNode.optString("SubTotal").toString());
                            productdetail.setTax(twoDecimalPoint(jsonChildNode.optDouble("Tax")));
                            productdetail.setNettot(jsonChildNode.optString("NetTotal").toString());
                            productdetail.setPaidamount(jsonChildNode.optString("PaidAmount").toString());
                            // productdetail.setCreditAmount(jsonChildNode.optString("CreditAmount").toString());
                            if(salesReturnNo.equals(SRNumber)){
                                productdetail.setCreditAmount(creditAmount);
                                productdetail.setBalanceAmount(balanceAmount);
                            }

                            productdetail.setRemarks(jsonChildNode.optString("Remarks").toString());
                            productdetail.setTotaloutstanding(jsonChildNode.optString("TotalBalance").toString());

                            try {
                                for (int k = 0; k < lengthSRDetailJsonArr; k++) {
                                    /****** Get Object for each JSON node. ***********/
                                    ProdDetails proddetails = new ProdDetails();
                                    JSONObject jsonChildNodes = jsonMainNode.getJSONObject(k);
                                    int s = k + 1;
                                    proddetails.setSno(String.valueOf(s));
                                    proddetails.setItemnum(jsonChildNodes.optString("SalesReturnNo").toString());
                                    proddetails.setItemcode(jsonChildNodes.optString("ProductCode").toString());
                                    proddetails.setDescription(jsonChildNodes.optString("ProductName").toString());
                                    String qty = jsonChildNodes.optString("Qty").toString().toString().split("\\.")[0];
                                    proddetails.setQty(qty);
                                    proddetails.setPrice(jsonChildNodes.optString("Price").toString());
                                    proddetails.setTotal(jsonChildNodes.optString("Total").toString());
                                    String focvalue = jsonChildNodes.optString("FOCQty").toString().split("\\.")[0];
                                    String ExchangeQty = jsonChildNodes.optString("ExchangeQty").toString().split("\\.")[0];
                                    if (focvalue != null && !focvalue.isEmpty()) {
                                        proddetails.setFocqty(Integer.valueOf(focvalue));
                                    } else {
                                        proddetails.setFocqty(0);
                                    }
                                    if (ExchangeQty != null && !ExchangeQty.isEmpty()) {
                                        proddetails.setExchangeqty(Integer.valueOf(ExchangeQty));
                                    } else {
                                        proddetails.setExchangeqty(0);
                                    }
                                    proddetails.setTax(jsonChildNodes.optString("Tax").toString());
                                    proddetails.setTaxType(jsonChildNodes.optString("TaxType").toString());
                                    proddetails.setTaxPerc(jsonChildNodes.optString("TaxPerc").toString());
                                    proddetails.setSubtotal(jsonChildNodes.optString("SubTotal").toString());
                                    mSRDetail.add(proddetails);

                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            productdetail.setProductsDetails(mSRDetail);
                            mSRHeaderDetailArr.add(productdetail);
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            try {
                prints();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReceiptPreview extends AsyncTask<Void, Void, Void> {
        // Do Offline
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            spinnerLayout = new LinearLayout(InvoiceCashCollection.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            addContentView(spinnerLayout, new LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(cashcollection_layout, false);
            progressBar = new ProgressBar(InvoiceCashCollection.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);
            product.clear();
            productdet.clear();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String showcartonloose = SalesOrderSetGet.getCartonpriceflag();
            String cmpnyCode = SupplierSetterGetter.getCompanyCode();
            hm.put("CompanyCode", cmpnyCode);
            hm.put("InvoiceNo", previewno);
            if (showcartonloose.equalsIgnoreCase("1")) {
                jsonString = SalesOrderWebService.getSODetail(
                        hm, "fncGetInvoiceDetailWithCarton");
            } else {
                jsonString = SalesOrderWebService.getSODetail(hm,
                        "fncGetInvoiceDetail");
            }
            jsonStr = SalesOrderWebService.getSODetail(hm,
                    "fncGetInvoiceHeaderByInvoiceNo");

            try {

                Log.d("customerCode","-->"+customerCode);
                SalesOrderWebService.getCustomerTax(customerCode, "fncGetCustomer");

                jsonResponse = new JSONObject(jsonString);
                jsonMainNode = jsonResponse.optJSONArray("SODetails");
                jsonResp = new JSONObject(jsonStr);
                jsonSecNode = jsonResp.optJSONArray("SODetails");

            } catch (JSONException e) {

                e.printStackTrace();
            }

            int lengJsonArr = jsonSecNode.length();
            for (int j = 0; j < lengJsonArr; j++) {

                JSONObject jsonChildNode;
                ProductDetails productdetail = new ProductDetails();
                try {
                    jsonChildNode = jsonSecNode.getJSONObject(j);
                    productdetail.setItemno(jsonChildNode
                            .optString("InvoiceNo").toString());
                    String dateTime = jsonChildNode.optString("InvoiceDate")
                            .toString();
                    StringTokenizer tokens = new StringTokenizer(dateTime, " ");
                    String invDate = tokens.nextToken();
                    productdetail.setItemdate(invDate);
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
                    productdetail.setPaidamount(jsonChildNode.optString(
                            "PaidAmount").toString());
                    productdetail.setCreditAmount(jsonChildNode.optString(
                            "CreditAmount").toString());
                    productdetail.setCreditAmount(jsonChildNode.optString(
                            "CreditAmount").toString());

                    productdetail.setRemarks(jsonChildNode.optString("Remarks")
                            .toString());
                    productdetail.setTotaloutstanding(jsonChildNode.optString(
                            "TotalBalance").toString());

                } catch (JSONException e) {

                    e.printStackTrace();
                }
                /********
                 * Show Default carton,loose,foc,exchange qty and price
                 * Based On General settings
                 *********/
                /**** Process each JSON Node *****/
                int lengthJsonArr = jsonMainNode.length();
                if (showcartonloose.equalsIgnoreCase("1")) {

                    for (int k = 0; k < lengthJsonArr; k++) {
                        /**** Get Object for each JSON node. *****/
                        ProdDetails proddetails = new ProdDetails();
                        try {
                            JSONObject jsonChildNodes = jsonMainNode
                                    .getJSONObject(k);
                            String transType = jsonChildNodes.optString(
                                    "TranType").toString();
                            int s = k + 1;
                            proddetails.setSno(String.valueOf(s));

                            proddetails.setItemnum(jsonChildNodes.optString(
                                    "InvoiceNo").toString());
                            proddetails.setItemcode(jsonChildNodes.optString(
                                    "ProductCode").toString());

                            proddetails.setDescription(jsonChildNodes
                                    .optString("ProductName").toString());

                            proddetails.setUOMCode(jsonChildNodes
                                    .optString("UOMCode").toString());

                            String uomCode = jsonChildNodes
                                    .optString("UOMCode").toString();

                            if (transType.equalsIgnoreCase("Ctn")) {
                                String cQty = jsonChildNodes.optString("CQty")
                                        .toString();

                                String cPrice = jsonChildNodes.optString(
                                        "CartonPrice").toString();

                                if (cQty != null && !cQty.isEmpty()
                                        && cPrice != null && !cPrice.isEmpty()) {
                                    proddetails.setQty(cQty.split("\\.")[0]);
                                    proddetails.setPrice(twoDecimalPoint(Double.valueOf(cPrice)));
                                    double cqty = Double.valueOf(cQty);
                                    double cprice = Double.valueOf(cPrice);
                                    double total = cqty * cprice;
                                    String tot = twoDecimalPoint(total);
                                    proddetails.setTotal(tot);
                                } else {
                                    proddetails.setQty("0");
                                    proddetails.setPrice("0.00");
                                    proddetails.setTotal("0.00");
                                }

                            } else if (transType.equalsIgnoreCase("Loose")) {

                                String lQty = jsonChildNodes.optString("LQty")
                                        .toString();
                                String lPrice = jsonChildNodes.optString(
                                        "Price").toString();

                                if (lQty != null && !lQty.isEmpty()
                                        && lPrice != null && !lPrice.isEmpty()) {
                                    proddetails.setQty(lQty.split("\\.")[0] + " " + uomCode);
                                    proddetails.setPrice(twoDecimalPoint(Double.valueOf(lPrice)));
                                    double lqty = Double.valueOf(lQty);
                                    double lprice = Double.valueOf(lPrice);
                                    double total = lqty * lprice;
                                    String tot = twoDecimalPoint(total);
                                    proddetails.setTotal(tot);
                                } else {
                                    proddetails.setQty("0" + " " + uomCode);
                                    proddetails.setPrice("0.00");
                                    proddetails.setTotal("0.00");
                                }

                            } else if (transType.equalsIgnoreCase("FOC")) {
                                String focQty = jsonChildNodes.optString(
                                        "FOCQty").toString();
                                if (focQty != null && !focQty.isEmpty()) {
                                    proddetails.setQty(focQty.split("\\.")[0]);
                                } else {
                                    proddetails.setQty("0" + " " + uomCode);
                                }
                                proddetails.setPrice("0.00");
                                proddetails.setTotal("0.00");

                            } else if (transType.equalsIgnoreCase("Exc")) {
                                String excQty = jsonChildNodes.optString(
                                        "ExchangeQty").toString();
                                if (excQty != null && !excQty.isEmpty()) {
                                    proddetails.setQty(excQty.split("\\.")[0]);
                                } else {
                                    proddetails.setQty("0" + " " + uomCode);
                                }
                                proddetails.setPrice("0.00");
                                proddetails.setTotal("0.00");

                            }

                            if (gnrlStngs.matches("C")) {
                                proddetails.setSortproduct(jsonChildNodes
                                        .optString("CategoryCode").toString());
                            } else if (gnrlStngs.matches("S")) {
                                proddetails.setSortproduct(jsonChildNodes
                                        .optString("SubCategoryCode")
                                        .toString());
                            } else if (gnrlStngs.matches("N")) {
                                proddetails.setSortproduct("");
                            } else {
                                proddetails.setSortproduct("");
                            }
                            proddetails.setTax(jsonChildNodes.optString("Tax").toString());
                            proddetails.setSubtotal(jsonChildNodes.optString("SubTotal").toString());
                            proddetails.setTaxPerc(jsonChildNodes.optString("TaxPerc").toString());
                            proddetails.setTaxType(jsonChildNodes.optString("TaxType").toString());
                            product.add(proddetails);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {

                    for (int k = 0; k < lengthJsonArr; k++) {
                        JSONObject jsonChildNodes;
                        ProdDetails proddetails = new ProdDetails();
                        try {
                            jsonChildNodes = jsonMainNode.getJSONObject(k);
                            int s = k + 1;
                            proddetails.setSno(String.valueOf(s));
                            proddetails.setItemnum(jsonChildNodes.optString(
                                    "InvoiceNo").toString());
                            proddetails.setItemcode(jsonChildNodes.optString(
                                    "ProductCode").toString());
                            proddetails.setDescription(jsonChildNodes.optString(
                                    "ProductName").toString());
                            String cshqty = jsonChildNodes.optString("Qty")
                                    .toString();
                            if (cshqty.contains(".")) {
                                StringTokenizer tokens = new StringTokenizer(
                                        cshqty, ".");
                                String qty = tokens.nextToken();
                                proddetails.setQty(qty);
                            } else {
                                proddetails.setQty(cshqty);
                            }

                            proddetails.setPrice(jsonChildNodes.optString("Price")
                                    .toString());
                            proddetails.setTotal(jsonChildNodes.optString("Total")
                                    .toString());

                            if (gnrlStngs.matches("C")) {
                                proddetails.setSortproduct(jsonChildNodes
                                        .optString("CategoryCode").toString());
                            } else if (gnrlStngs.matches("S")) {
                                proddetails.setSortproduct(jsonChildNodes
                                        .optString("SubCategoryCode").toString());
                            } else if (gnrlStngs.matches("N")) {
                                proddetails.setSortproduct("");
                            } else {
                                proddetails.setSortproduct("");
                            }
                            proddetails.setTax(jsonChildNodes.optString("Tax").toString());
                            proddetails.setSubtotal(jsonChildNodes.optString("SubTotal").toString());
                            proddetails.setTaxPerc(jsonChildNodes.optString("TaxPerc").toString());
                            proddetails.setTaxType(jsonChildNodes.optString("TaxType").toString());
                            product.add(proddetails);
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }
                productdetail.setProductsDetails(product);
                productdet.add(productdetail);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            hm.clear();
            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(cashcollection_layout, true);
            PreviewPojo.setReceiptproducts(product);
            PreviewPojo.setProductsDetails(productdet);
            Intent i = new Intent(InvoiceCashCollection.this,
                    ReceiptPrintPreview.class);
            i.putExtra("customerCode", csh_custcode.getText().toString());
            i.putExtra("customerName", csh_custname.getText().toString());
            i.putExtra("Key", "InvoiceCashCollection");
            i.putExtra("no", "");
            i.putExtra("date", "");
            i.putExtra("sort", sortproduct);
            i.putExtra("gnrlStngs", gnrlStngs);
            startActivity(i);
        }

    }

    public void sortCatagory() {
        for (int i = 0; i < sortproduct.size(); i++) {
            String catagory = sortproduct.get(i).toString();
            for (ProdDetails products : product) {

                Log.d("catagory", catagory);
                Log.d("products.getSort", products.getSortproduct());

                if (catagory.matches(products.getSortproduct())) {

                    sort.add(catagory);
                }
            }
        }
        hs.addAll(sort);
        printsortHeader.clear();
        printsortHeader.addAll(hs);
    }

    public void saveAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.mipmap.ic_save);
        alertDialog.setTitle("Save");
        alertDialog.setMessage("Do you want to Save");
        LayoutInflater adbInflater = LayoutInflater.from(this);
        final View eulaLayout = adbInflater.inflate(R.layout.print_checkbox,
                null);
        noofprint_layout = (LinearLayout) eulaLayout
                .findViewById(R.id.noofcopieslblLayout);
        enableprint = (CheckBox) eulaLayout.findViewById(R.id.checkbox);
        enableprint.setText("Print Receipt");
        final LinearLayout receipt_layout = (LinearLayout)  eulaLayout.findViewById(R.id.receipt_layout);
        radio_group= (RadioGroup) eulaLayout.findViewById(R.id.receipt_radio_group);

        int selectedId = radio_group.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        RadioButton  radioButton = (RadioButton) radio_group.findViewById(selectedId);
        radioButtonTxt = radioButton.getText().toString();

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if(null!=rb && checkedId > -1){
//                    Toast.makeText(Settings.this, rb.getText(), Toast.LENGTH_SHORT).show();

                    String rdbtn = rb.getText().toString();

                    if(rdbtn.matches("Receipt Summary")){
                        radioButtonTxt = "Receipt Summary";
                    }else{
                        radioButtonTxt = "Receipt Detail";
                    }


                }else{

                }
            }
        });

        enableprint.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked == true) {
                    receipt_layout.setVisibility(View.VISIBLE);
                    noofprint_layout.setVisibility(View.VISIBLE);
                    stnumber = (TextView) eulaLayout
                            .findViewById(R.id.stnumber);
                    stupButton = (Button) eulaLayout.findViewById(R.id.stupBtn);
                    if (!PreviewPojo.getNofcopies().matches("")) {
                        stnumber.setText("" + PreviewPojo.getNofcopies());
                        stwght = Integer.valueOf(PreviewPojo.getNofcopies());
                    }
                    stupButton.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            stdownButton
                                    .setBackgroundResource(R.mipmap.numpicker_down_normal);
                            stupButton
                                    .setBackgroundResource(R.mipmap.numpicker_up_pressed);
                            if (stwght < 3) {
                                stnumber.setText("" + ++stwght);
                            }

                        }
                    });

                    stdownButton = (Button) eulaLayout
                            .findViewById(R.id.stdownBtn);
                    stdownButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            stdownButton
                                    .setBackgroundResource(R.mipmap.numpicker_down_pressed);
                            stupButton
                                    .setBackgroundResource(R.mipmap.numpicker_up_normal);
                            if (stwght > 1) {
                                stnumber.setText(--stwght + "");
                            }
                        }
                    });
                } else if (isChecked == false) {
                    noofprint_layout.setVisibility(View.GONE);
                    receipt_layout.setVisibility(View.GONE);
                }
            }
        });

        alertDialog.setView(eulaLayout);
        final int modeid = FWMSSettingsDatabase.getModeId();
        if (modeid == 1) {
            FWMSSettingsDatabase.updateMode(1);
            enableprint.setChecked(true);
        } else {
            FWMSSettingsDatabase.updateMode(0);
            enableprint.setChecked(false);
        }
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int indexSelected) {
                        SaveArray.clear();
                        salesReturnArray.clear();
                        In_Cash.setCust_Code(csh_custcode.getText().toString());
                        String btn_spn = btn_spinner.getText().toString();
                        Log.d("test", btn_spn);
                        In_Cash.setPay_Mode(btn_spinner.getText().toString());

                        int count=1;
                        double invcredittotal = 0.00;
                        for (int i = 0; i < ListArray.size(); i++) {
                            HashMap<String, String> hm = new HashMap<String, String>();

                            final In_Cash in_cash = Adapter.getItem(i);
                            if (in_cash.isSelected()) {

                                String str3;
                                String str1 = in_cash.getIn_Netvalue();
                                String str2 = in_cash.getIn_Credit();
                                String str4 = in_cash.getIn_InvNo();
                                str3 = in_cash.getAdd_paid();
                                Log.d("credit before", str2);
								/*if (i == Pos) {
									str2 = in_cash.getAdd_credit();
//									str3 = in_cash.getAdd_paid();
									Log.d("credit after",str2);
								} else {
									// str3=in_cash.getIn_Bal();
								}*/
                                str2 = in_cash.getAdd_credit();
                                Log.d("credit after", str2);

                                Log.d("pos value", "" + Pos);
                                Log.d("i value", "" + i);
                                Log.d("receiptAutoCreditAmt2","rr "+receiptAutoCreditAmt);

                                if(receiptAutoCreditAmt.matches("0")) {
                                    Log.d("receiptauto","0");
                                }else{
                                    Log.d("receiptauto",">0");
                                    if (count == 1) {
                                        // auto credit option 29.08.2017 by arunkumar
                                    String paid = csh_tot_paid.getText().toString();
                                    double paidAmt = 0;
                                    if (paid != null) {
                                        paidAmt = Double.parseDouble(paid);
                                    }

                                    int decimal = (int) paidAmt; // you have 12345
                                    double fractional = Double.parseDouble(roundOffTo2DecPlaces(paidAmt - decimal)); // you have 0.6789

                                    if (fractional <= autoCredit) {
                                        str3 = String.valueOf(Double.parseDouble(str3)-fractional);
                                        str2 = String.valueOf(fractional);
                                    } else {
                                        Log.d("fractional", ""+fractional);
                                    }
                                    Log.d("csh_tot_paid", paid);
                                    Log.d("paid", "pp " + str3);
                                    Log.d("credit", "cc " + str2);
                                }
                                }
                                count++;

                                hm.put("PaidValue", str3);
                                hm.put("CreditValue", str2);
                                hm.put("NetValue", str1);
                                hm.put("Invoice", str4);
                                SaveArray.add(hm);
                                double dCrdt = 0.0;
                                if (str2 != null && !str2.isEmpty()) {
                                    dCrdt = Double.parseDouble(str2);
                                }
                                invcredittotal = invcredittotal + dCrdt;
                            }
                            Log.d("invcredittotal", "-->" + invcredittotal);
                        }
                        try {
                            //SparseBooleanArray checked = credit_list.getCheckedItemPositions();


                            for (int i = 0; i < mList.size(); i++) {
                                String flag = mList.get(i).get("Flag");
                                if (flag.equals("true")) {
                                    HashMap<String, String> hashMap = new HashMap<String, String>();
                                    hashMap.put("SalesReturnNo", mList.get(i).get("SalesReturnNo"));
                                    hashMap.put("SalesReturnDate", mList.get(i).get("SalesReturnDate"));
                                    hashMap.put("BalanceAmount", mList.get(i).get("BalanceAmount"));
                                    hashMap.put("Total", mList.get(i).get("CreditAmount"));
                                    hashMap.put("ReturnType", "SR");

                                    salesReturnArray.add(hashMap);
                                }
                            }

					/*		int size = credit_list.getAdapter().getCount();
							Log.d("getAdapter().getCount()", "-->" + size);
							for (int i = 0; i < size; i++) {
								if (credit_list.isItemChecked(i)) {
									Log.d("isItemChecked", "-->" + size);
									HashMap<String, String> hm  = (HashMap<String, String>)credit_list.getAdapter().getItem(i);
									Log.d("SalesReturnNo", "-->" +  hm.get("SalesReturnNo"));
								*//*String salesReturnNo = mList.get(i).get("SalesReturnNo");
								String salesReturnDate = mList.get(i).get("SalesReturnDate");
								String balanceAmount = mList.get(i).get("BalanceAmount");
								String creditAmount = mList.get(i).get("CreditAmount");*//*
									HashMap<String, String> hashMap = new HashMap<String, String>();
									hashMap.put("SalesReturnNo", hm.get("SalesReturnNo"));
									hashMap.put("SalesReturnDate", hm.get("SalesReturnDate"));
									hashMap.put("BalanceAmount", hm.get("BalanceAmount"));
									hashMap.put("Total", hm.get("CreditAmount"));
									hashMap.put("ReturnType", "SR");

									salesReturnArray.add(hashMap);
								}
							}*/


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (!SaveArray.isEmpty()) {


                            if (srCrdttotal > 0) {
                                Log.d("invcredittotal", "-->" + invcredittotal);
                                Log.d("srCrdttotal", "-->" + srCrdttotal);
//								if(srCrdttotal<=invcredittotal){
                                SaveAsyncCall Saveservice = new SaveAsyncCall();
                                Saveservice.execute();
//								}else{
//									Toast.makeText(InvoiceCashCollection.this,
//											"Please make credit amount should be greater than sales return used amount", Toast.LENGTH_LONG)
//											.show();
//								}
                            } else {
                                SaveAsyncCall Saveservice = new SaveAsyncCall();
                                Saveservice.execute();
                            }
                        }
                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        csh_bt_save.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    String roundOffTo2DecPlaces(double val)
    {
        return String.format("%.2f", val);
    }

    private void OnlineInvoiceOfflinePrint() throws IOException {
        mOnlineInvoicePrintOfflineArr.clear();
        HashMap<String, String> hashmap = new HashMap<String, String>();
        String paidMode = In_Cash.getPay_Mode();
        String customerName = offlineDatabase.getCustomerName(customerCode);
        Log.d("paidMode", "-->" + paidMode);
        Log.d("customerName", "-->" + customerName);
        for (int i = 0; i < ListArray.size(); i++) {
            final In_Cash in_cash = Adapter.getItem(i);
            if (in_cash.isSelected()) {
                mOnlineInvoicePrintOfflineArr.add(in_cash);
            }
        }
        hashmap.put("ReceiptNo", saveResult);
        hashmap.put("ReceiptDate", serverdate);
        hashmap.put("CustomerName", customerName);
        hashmap.put("PayMode", paidMode);
        helper.dismissProgressDialog();

        String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
        String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        try {
            if(printertype.matches("Zebra iMZ320")) {
                Printer printer = new Printer(InvoiceCashCollection.this, macaddress);
                printer.setOnCompletionListener(new Printer.OnCompletionListener() {
                    @Override
                    public void onCompleted() {

                        finish();

                        if (header.matches("InvoiceHeader")) {
                            intent = new Intent(InvoiceCashCollection.this,
                                    InvoiceHeader.class);

                        } else if (header.matches("CustomerHeader")) {
                            intent = new Intent(InvoiceCashCollection.this,
                                    CustomerListActivity.class);

                        } else if (header.matches("RouteHeader")) {
                            intent = new Intent(InvoiceCashCollection.this,
                                    RouteHeader.class);
                        }
                        else if (header.matches("ConsignmentHeader")) {
                            intent = new Intent(InvoiceCashCollection.this,
                                    ConsignmentHeader.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        InvoiceCashCollection.this.finish();
                    }
                });
                printer.printOnlineInvoiceInOffline(hashmap, mOnlineInvoicePrintOfflineArr, footerArr);
            }else if(printertype.matches("4 Inch Bluetooth")){

                helper.showProgressDialog(InvoiceCashCollection.this.getString(R.string.print),
                        InvoiceCashCollection.this.getString(R.string.creating_file_for_printing));

                if (BluetoothAdapter.checkBluetoothAddress(FWMSSettingsDatabase.getPrinterAddress()))
                {
                    BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(FWMSSettingsDatabase.getPrinterAddress());
                    // Attempt to connect to the device
                    GlobalData.mService.setHandler(mHandler1);
                    GlobalData.mService.connect(device,true);
                }
                helper.dismissProgressDialog();

            }
            else if(printertype.matches("3 Inch Bluetooth Generic")){
                helper.dismissProgressDialog();
                final int nofcopies = Integer.parseInt(stnumber.getText().toString());
                try {
                    final CubePrint print = new CubePrint(InvoiceCashCollection.this,macaddress);
                    print.initGenericPrinter();
                    print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
                        @Override
                        public void initCompleted() {
                            // OnlineInvoiceOfflinePrint
                            mOnlineInvoicePrintOfflineArr.clear();
                            HashMap<String, String> hashmap = new HashMap<String, String>();
                            String paidMode = In_Cash.getPay_Mode();
                            String customerName = offlineDatabase.getCustomerName(customerCode);
                            Log.d("paidMode", "-->" + paidMode);
                            Log.d("customerName", "-->" + customerName);
                            for (int i = 0; i < ListArray.size(); i++) {
                                final In_Cash in_cash = Adapter.getItem(i);
                                if (in_cash.isSelected()) {
                                    mOnlineInvoicePrintOfflineArr.add(in_cash);
                                }
                            }
                            hashmap.put("ReceiptNo", saveResult);
                            hashmap.put("ReceiptDate", serverdate);
                            hashmap.put("CustomerCode", customerCode);
                            hashmap.put("CustomerName", customerName);
                            hashmap.put("PayMode", paidMode);

                            print.printOnlineInvoiceInOffline(hashmap, mOnlineInvoicePrintOfflineArr,nofcopies, footerArr);

                            print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
                                @Override
                                public void onCompleted() {
                                    helper.showLongToast(R.string.printed_successfully);
                                    finish();
                                    if (header.matches("InvoiceHeader")) {
                                        intent = new Intent(InvoiceCashCollection.this,
                                                InvoiceHeader.class);

                                    } else if (header.matches("CustomerHeader")) {
                                        intent = new Intent(InvoiceCashCollection.this,
                                                CustomerListActivity.class);

                                    } else if (header.matches("RouteHeader")) {
                                        intent = new Intent(InvoiceCashCollection.this,
                                                RouteHeader.class);
                                    }else if (header.matches("ConsignmentHeader")) {
                                        intent = new Intent(InvoiceCashCollection.this,
                                                ConsignmentHeader.class);
                                    }
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    InvoiceCashCollection.this.finish();
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        } catch (IllegalArgumentException e) {
            helper.showLongToast(R.string.error_configure_printer);
        }
    }
    public  ArrayList<HashMap<String, String>> getSalesReturnValue(){
        ArrayList<HashMap<String, String>> salesreturnArr = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            HashMap<String, String> data = mList.get(i);
            String flag = data.get("Flag");
            if (flag.equals("true")) {
                salesreturnArr.add(data);
            }
        }

        return  salesreturnArr;
    }
    private void prints() throws IOException {
        final int nofcopies = Integer.parseInt(stnumber.getText().toString());
        In_Cash in_SO = Adapter.getItem(Adapter.getSelectedPosition());
        final String receiptDate = in_SO.getIn_Date().toString();
        System.out.println("receiptDate" + receiptDate);
        final ArrayList<HashMap<String, String>> salesreturnArr = getSalesReturnValue();
        String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
        try {

            if(printertype.matches("Zebra iMZ320")) {
                helper.dismissProgressDialog();
                Printer printer = new Printer(InvoiceCashCollection.this,
                        macaddress);
                printer.setOnCompletionListener(new Printer.OnCompletionListener() {

                    @Override
                    public void onCompleted() {

                        finish();
					/*Intent i = new Intent(InvoiceCashCollection.this,
							InvoiceHeader.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);*/
                        if (header.matches("InvoiceHeader")) {
                            intent = new Intent(InvoiceCashCollection.this,
                                    InvoiceHeader.class);

                        } else if (header.matches("CustomerHeader")) {
                            intent = new Intent(InvoiceCashCollection.this,
                                    CustomerListActivity.class);

                        } else if (header.matches("RouteHeader")) {
                            intent = new Intent(InvoiceCashCollection.this,
                                    RouteHeader.class);
                        }else if (header.matches("ConsignmentHeader")) {
                            intent = new Intent(InvoiceCashCollection.this,
                                    ConsignmentHeader.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        InvoiceCashCollection.this.finish();
                    }
                });

                if(radioButtonTxt.matches("Receipt Summary")){
                    printer.printReceiptSummary(/*csh_custcode.getText().toString(),
                            csh_custname.getText().toString(),*/ saveResult, receiptDate,
                            productdet, /*printsortHeader, gnrlStngs, nofcopies, true,*/ footerArr,salesreturnArr,mSRHeaderDetailArr);
                }else {

                        printer.printReceipt(csh_custcode.getText().toString(),
                                csh_custname.getText().toString(), saveResult, receiptDate,
                                productdet, printsortHeader, gnrlStngs, nofcopies, true, footerArr, salesreturnArr, mSRHeaderDetailArr);

                }
            }else if(printertype.matches("4 Inch Bluetooth")){
                helper.showProgressDialog(InvoiceCashCollection.this.getString(R.string.print),
                        InvoiceCashCollection.this.getString(R.string.creating_file_for_printing));
                if (BluetoothAdapter.checkBluetoothAddress(FWMSSettingsDatabase.getPrinterAddress()))
                {
                    BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(FWMSSettingsDatabase.getPrinterAddress());
                    // Attempt to connect to the device
                    GlobalData.mService.setHandler(mHandler1);
                    GlobalData.mService.connect(device,true);

                }
                helper.dismissProgressDialog();
            }
            else if(printertype.matches("3 Inch Bluetooth Generic")){
                final int noofInvoice = Adapter.getSelected();
                helper.dismissProgressDialog();
                try {
                    final CubePrint print = new CubePrint(InvoiceCashCollection.this,macaddress);
                    print.initGenericPrinter();
                    print.setInitCompletionListener(new CubePrint.InitCompletionListener() {
                        @Override
                        public void initCompleted() {
                            if(radioButtonTxt.matches("Receipt Summary")) {
                                print.ReceiptSummmaryPrint(csh_custcode.getText().toString(),
                                        csh_custname.getText().toString(), saveResult, receiptDate,
                                        productdet, printsortHeader, gnrlStngs, nofcopies, true, footerArr, noofInvoice);
                            }else{
                                print.printReceipt(csh_custcode.getText().toString(),
                                        csh_custname.getText().toString(), saveResult, receiptDate,
                                        productdet, printsortHeader, gnrlStngs, nofcopies, true, footerArr, 1,salesreturnArr);
                            }
                            print.setOnCompletedListener(new CubePrint.OnCompletedListener() {
                                @Override
                                public void onCompleted() {
                                    helper.showLongToast(R.string.printed_successfully);
                                    finish();
                                    if (header.matches("InvoiceHeader")) {
                                        intent = new Intent(InvoiceCashCollection.this,
                                                InvoiceHeader.class);

                                    } else if (header.matches("CustomerHeader")) {
                                        intent = new Intent(InvoiceCashCollection.this,
                                                CustomerListActivity.class);

                                    } else if (header.matches("RouteHeader")) {
                                        intent = new Intent(InvoiceCashCollection.this,
                                                RouteHeader.class);
                                    }else if (header.matches("ConsignmentHeader")) {
                                        intent = new Intent(InvoiceCashCollection.this,
                                                ConsignmentHeader.class);
                                    }
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    InvoiceCashCollection.this.finish();
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if(printertype.matches("Zebra iMZ320 4 Inch")){

                helper.dismissProgressDialog();
                PrinterZPL printer = new PrinterZPL(InvoiceCashCollection.this,
                        macaddress);
                printer.setOnCompletionListener(new PrinterZPL.OnCompletionListener() {

                    @Override
                    public void onCompleted() {

                        finish();
					/*Intent i = new Intent(InvoiceCashCollection.this,
							InvoiceHeader.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);*/
                        if (header.matches("InvoiceHeader")) {
                            intent = new Intent(InvoiceCashCollection.this,
                                    InvoiceHeader.class);

                        } else if (header.matches("CustomerHeader")) {
                            intent = new Intent(InvoiceCashCollection.this,
                                    CustomerListActivity.class);

                        } else if (header.matches("RouteHeader")) {
                            intent = new Intent(InvoiceCashCollection.this,
                                    RouteHeader.class);
                        }else if (header.matches("ConsignmentHeader")) {
                            intent = new Intent(InvoiceCashCollection.this,
                                    ConsignmentHeader.class);
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        InvoiceCashCollection.this.finish();
                    }
                });

                if(radioButtonTxt.matches("Receipt Summary")){
                    printer.printReceiptSummary(csh_custcode.getText().toString(),
                            csh_custname.getText().toString(), saveResult, receiptDate,
                            productdet, /*printsortHeader, gnrlStngs, nofcopies, true,*/ footerArr);
                }else{
                    printer.printReceipt(csh_custcode.getText().toString(),
                            csh_custname.getText().toString(), saveResult, receiptDate,
                            productdet, printsortHeader, gnrlStngs, nofcopies, true, footerArr,salesreturnArr);
                }
            }
        } catch (IllegalArgumentException e) {
            helper.showLongToast(R.string.error_configure_printer);
        }

    }
    private class AsyncGeneralSettings extends AsyncTask<Void, Void, Void> {
        String dialogStatus;

        @Override
        protected void onPreExecute() {
            dialogStatus = checkInternetStatus();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (onlineMode.matches("True")) {
                if (checkOffline == true) {
                    Log.d("DialogStatus", "" + dialogStatus);

                    if (dialogStatus.matches("true")) {
                        Log.d("CheckOffline Alert -->", "True");

                        String result = offlineDatabase
                                .getGeneralSettingsValue();
                        jsonString = " { JsonArray: " + result + "}";
                    } else {
                        Log.d("CheckOffline Alert -->", "False");
                        if(mobileHaveOfflineMode.matches("1")){
                            finish();
                        }
                    }

                } else {
                    Log.d("checkOffline Status -->", "False");
                    jsonString = WebServiceClass
                            .URLService("fncGetGeneralSettings");

                }

            } else if (onlineMode.matches("False")) {
                Log.d("Customer Online", onlineMode);
                String result = offlineDatabase.getGeneralSettingsValue();
                jsonString = " { JsonArray: " + result + "}";

            }

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

                    String SettingID = jsonChildNode.optString("SettingID")
                            .toString();

                    if (SettingID.matches("APPPRINTGROUP")) {
                        String settingValue = jsonChildNode.optString(
                                "SettingValue").toString();

                        if (settingValue.matches("C")) {
                            gnrlStngs = settingValue;
                            Log.d("result ", gnrlStngs);
                        } else if (settingValue.matches("S")) {
                            gnrlStngs = settingValue;
                            Log.d("result ", gnrlStngs);
                        } else if (settingValue.matches("N")) {
                            gnrlStngs = settingValue;
                            Log.d("result ", gnrlStngs);
                        } else {
                            gnrlStngs = settingValue;
                            Log.d("result ", gnrlStngs);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            APPPrintGroup apprintgroup = new APPPrintGroup();
            apprintgroup.execute();
        }

    }

    private class APPPrintGroup extends AsyncTask<Void, Void, Void> {
        // Do offline
        @Override
        protected void onPreExecute() {
            sortproduct.clear();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (gnrlStngs.matches("C")) {
                //jsonString = WebServiceClass.URLService("fncGetCategory");
                if (onlineMode.matches("True")) {
                    if (checkOffline == true) {
                        //Offline
                        String result = offlineDatabase.getCategory();
                        jsonString = " { JsonArray: " + result + "}";


                    } else { // online

                        jsonString = WebServiceClass.URLService("fncGetCategory");
                    }

                } else if (onlineMode.matches("False")) { // perman offline

                    //Offline
                    String result = offlineDatabase.getCategory();
                    jsonString = " { JsonArray: " + result + "}";


                }
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

                        sortproduct.add(categorycode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else if (gnrlStngs.matches("S")) {
                //jsonStr = WebServiceClass.URLService("fncGetSubCategory");
                if (onlineMode.matches("True")) {
                    if (checkOffline == true) {
                        //Offline
                        String result = offlineDatabase.getSubCategory();
                        jsonStr = " { JsonArray: " + result + "}";
                    } else {  //Online

                        jsonStr = WebServiceClass.URLService("fncGetSubCategory");

                    }
                } else if (onlineMode.matches("False")) { // perman offline
                    String result = offlineDatabase.getSubCategory();
                    jsonStr = " { JsonArray: " + result + "}";
                }
                Log.d("jsonStr ", jsonStr);

                try {

                    jsonResponse = new JSONObject(jsonStr);
                    jsonSecNode = jsonResponse.optJSONArray("JsonArray");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /*********** Process each JSON Node ************/
                int lengJsonArr = jsonSecNode.length();
                for (int i = 0; i < lengJsonArr; i++) {
                    /****** Get Object for each JSON node. ***********/
                    JSONObject jsonChildNode;

                    try {
                        jsonChildNode = jsonSecNode.getJSONObject(i);

                        String subcategorycode = jsonChildNode.optString(
                                "SubCategoryCode").toString();

                        sortproduct.add(subcategorycode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tax = df.format(d);
        return tax;
    }

    @Override
    public void onBackPressed() {
//		Intent i = new Intent(InvoiceCashCollection.this, InvoiceHeader.class);
//		startActivity(i);
        if (header.matches("CustomerHeader")) {
            intent = new Intent(InvoiceCashCollection.this, CustomerListActivity.class);
        } else if (header.matches("RouteHeader")) {
            intent = new Intent(InvoiceCashCollection.this,
                    RouteHeader.class);
        } else if (header.matches("ConsignmentHeader")) {
            intent = new Intent(InvoiceCashCollection.this,
                    ConsignmentHeader.class);
        }else {
            intent = new Intent(InvoiceCashCollection.this, InvoiceHeader.class);
        }
        startActivity(intent);
        InvoiceCashCollection.this.finish();
    }

    public class CustomListAdapter extends BaseAdapter {

        LayoutInflater mInflater;
        Context mContext;
        private int mResource;
        HashMap<String, String> mData = new HashMap<String, String>();
        ViewHolder holder;

        public CustomListAdapter(){

        }

        public CustomListAdapter(Activity context, int resource, ArrayList<HashMap<String, String>> list) {
            mList.clear();
            mList = list;
            this.mResource = resource;
            mContext = context;
            this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            OfflineDatabase.init(context);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            int pos = position;

            final HashMap<String, String> data = mList.get(position);
            if (row == null) {
                holder = new ViewHolder();
//	    row = mInflater.inflate(resourceId, null);
                row = mInflater.inflate(mResource, parent, false);
                holder.textView1 = (TextView) row.findViewById(R.id.item1);
                holder.textView2 = (TextView) row.findViewById(R.id.item2);
                holder.textView3 = (TextView) row.findViewById(R.id.item3);
                holder.textView4 = (TextView) row.findViewById(R.id.item4);
                holder.checkBox = (CheckBox) row.findViewById(R.id.chckbox_item);

            } else {
                holder = (ViewHolder) row.getTag();
            }

            row.setTag(holder);
            row.setId(position);
            holder.textView1.setText(data.get("SalesReturnNo"));
            holder.textView2.setText(data.get("SalesReturnDate"));
            holder.textView3.setText(data.get("BalanceAmount"));
            holder.textView4.setText(data.get("CreditAmount"));
            //   Log.d("text 1", data.get("SalesReturnNo"));
            // holder.checkBox.setId(pos);

            holder.checkBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("checkBox", "POS : " + position);
                    CheckBox checkBox = (CheckBox) v;
                    // int position = v.getId();
                    srAmttotal = 0.00;
                    srCrdttotal = 0.00;

                    if (checkBox.isChecked()) {
                        srlist_size = srlist_size + 1;
                        double amt = salesreturn_getCreditAmount();
                        //	data.put("CreditAmount", twoDecimalPoint(amount));
                        data.put("CreditAmount", data.get("BalanceAmount"));
                        data.put("Flag", "true");
                        creditSplit();
                        Log.d("CreditAmount", "balanot ->: " + data.get("CreditAmount"));
                     //   double balanceTot = Adapter.getTotalBalance();
                      double balance = Adapter.getBalance();
                        double amount = Adapter.getTotal();
                      Log.d("balance", "balance ->: " + balance);
                        Log.d("amount", "amount ->: " + amount);
                      //  Log.d("balanceTot", "balanot ->: " + balanceTot);


                        double amtTot = amount - amt;
                        Log.d("new amout" ,"bbb "+ amt);
                        Log.d("new amtTot" ,"aaa "+ amtTot);
                       if(balance>amount){
                            data.put("CreditAmount", data.get("CreditAmount"));
                        }else if(balance<amount){
                            data.put("CreditAmount", twoDecimalPoint(amtTot));
                        }else{
                            data.put("CreditAmount", twoDecimalPoint(amount));
                       }


                        // HashMap<String, String> data = mList.get(position);

                    } else {
                        credit_checkall.setChecked(false);
                        srlist_size = srlist_size - 1;
                        data.put("CreditAmount", "0.00");
                        data.put("Flag", "false");
                        creditSplit();
                    }

                    notifyDataSetChanged();
                }
            });

            if (srlist_size == mList.size()) {
                credit_checkall.setChecked(true);
                holder.checkBox.setChecked(true);
//	    data.put("CreditAmount", data.get("BalanceAmount"));

            } else if (srlist_size == 0) {
                credit_checkall.setChecked(false);
                holder.checkBox.setChecked(false);
//	    data.put("CreditAmount", "0.00");
            }

            holder.textView4.setText(data.get("CreditAmount"));
            getCreditTotal();

//	   if (holder.checkBox.isChecked()) {
//
//	     double dBal = 0.00, dCrdt = 0.00;
//	     String mBalAmt =  holder.textView3.getText().toString();
//	     String mCrtAmt = holder.textView4.getText().toString();
//	     if(mBalAmt != null && !mBalAmt.isEmpty()){
//	      dBal = Double.parseDouble(mBalAmt);
//	     }
//	     if(mCrtAmt != null && !mCrtAmt.isEmpty()){
//	      dCrdt = Double.parseDouble(mCrtAmt);
//	     }
//	     srAmttotal = srAmttotal+dBal;
//	     srCrdttotal = srCrdttotal+dCrdt;
//
//	     Log.d("srAmttotal tt ", "--> "+srAmttotal);
//		 Log.d("srCrdttotal tt", "--> "+srCrdttotal);
//	   }
//
//	   String totamt = twoDecimalPoint(srAmttotal);
//	   String totcred = twoDecimalPoint(srCrdttotal);
//
//	   srAmttotal = Double.parseDouble(totamt);
//	   srCrdttotal = Double.parseDouble(totcred);
//
//	   srTotalAmount.setText(totamt);
//	   srTotalCredit.setText(totcred);


            return row;
        }

        class ViewHolder {

            TextView textView1;
            TextView textView2;
            TextView textView3;
            TextView textView4;
            CheckBox checkBox;
        }

        public boolean salesreturn_isChecked() {
            for (int i = 0; i < mList.size(); i++) {
                HashMap<String, String> data = mList.get(i);
                String flag = data.get("Flag");
                if (flag.equals("true")) {
                    return true;
                }
            }
            return false;
        }

        public double salesreturn_getCreditAmount() {
            double amount=0.0;
            for (int i = 0; i < mList.size(); i++) {
                HashMap<String, String> data = mList.get(i);
                String flag = data.get("Flag");
                if (flag.equals("true")) {
                    String amt = data.get("CreditAmount");
                    if(amt!=null && !amt.isEmpty()){
                        amount += Double.parseDouble(amt);
                    }
                }
            }
            return amount;
        }
    }


    public void getCreditTotal() {

        srAmttotal = 0.00;
        srCrdttotal = 0.00;
        for (int i = 0; i < mList.size(); i++) {
            HashMap<String, String> data = mList.get(i);
            double dBal = 0.00, dCrdt = 0.00;
            String mBalAmt = data.get("BalanceAmount");
            String mCrtAmt = data.get("CreditAmount");
            if (mBalAmt != null && !mBalAmt.isEmpty()) {
                dBal = Double.parseDouble(mBalAmt);
                srAmttotal = srAmttotal + dBal;
            }
            if (mCrtAmt != null && !mCrtAmt.isEmpty()) {
                dCrdt = Double.parseDouble(mCrtAmt);
                srCrdttotal = srCrdttotal + dCrdt;
            }

            Log.d("srAmttotal tt ", "--> " + srAmttotal);
            Log.d("srCrdttotal tt", "--> " + srCrdttotal);
        }

        String totamt = twoDecimalPoint(srAmttotal);
        String totcred = twoDecimalPoint(srCrdttotal);

        srTotalAmount.setText(totamt);
        srTotalCredit.setText(totcred);

    }

    public void creditSplit() {

        try {

            creditAmount = 0.00;
            for (int i = 0; i < mList.size(); i++) {
                HashMap<String, String> data = mList.get(i);
                double dCrdt = 0.00;
                String mCrtAmt = data.get("CreditAmount");
                if (mCrtAmt != null && !mCrtAmt.isEmpty()) {
                    dCrdt = Double.parseDouble(mCrtAmt);
                    creditAmount = creditAmount + dCrdt;
                }
            }
            Log.d("split creditAmount", "--> " + creditAmount);

            int selected = Adapter.getSelected();

            if (selected == 1) {
                csh_bt_save.setVisibility(View.GONE);
                csh_amount.setText("");
                csh_credit.setText("");
                list_size = 0;
                listPosition = -1;
                Pos = -1;
                total = 0.00;
                outstanding = 0.00;
                returnAmount = 0.00;
                Adapter.splitSelectedCreditBalance(creditAmount);
                Adapter.notifyDataSetChanged();
            } else {
                csh_checkall.setChecked(false);
                csh_bt_save.setVisibility(View.GONE);
                Adapter.selectAll(false);
                csh_amount.setText("");
                csh_credit.setText("");
                list_size = 0;
                listPosition = -1;
                Pos = -1;
                total = 0.00;
                outstanding = 0.00;
                returnAmount = 0.00;
//				csh_total.setText(String.valueOf(total));

                Adapter.splitCreditBalance(creditAmount);
                Adapter.notifyDataSetChanged();
            }

            if (total >= creditAmount) {
                double finalValue = total - creditAmount;
                outstanding = Math.round(finalValue * 100.0) / 100.0;
                returnAmount = 0.00;
                csh_amount.setText("");
                csh_credit.setText("");
                csh_netamt.setText("");
//					csh_total.setText(String.valueOf(netAmount));
//					csh_outstanding.setText(String.valueOf(outstanding));
                csh_returnamt.setText(String.valueOf(returnAmount));
            } else {
                csh_amount.setText("");
                csh_credit.setText("");
                csh_netamt.setText("");
                double finalValue = creditAmount - total;
                returnAmount = Math.round(finalValue * 100.0) / 100.0;
                outstanding = 0.00;
//					csh_total.setText(String.valueOf(netAmount));
//					csh_outstanding.setText(String.valueOf(outstanding));
                csh_returnamt.setText(String.valueOf(returnAmount));
            }


        } catch (Exception e) {
        }
    }


    public void showPrintDialog(final String crtValue, final String amtValue,final String srnumber_txt) {
        mDialog = new Dialog(this);
        // dialog.setTitle("Filter");
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_credit);

        mDialog.setCancelable(false);
        mDialog.show();

        final EditText ed_credit = (EditText) mDialog.findViewById(R.id.ed_credit);
        ImageView close = (ImageView) mDialog.findViewById(R.id.close);
        Button addcredit = (Button) mDialog.findViewById(R.id.bt_addcredit);
        ed_credit.setText(crtValue);
        ed_credit.requestFocus(ed_credit.getText().length());
        showKeyboard(ed_credit);

        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                hideKeyboard(ed_credit);
                srAmttotal = 0.00;
                srCrdttotal = 0.00;
                endDialog(crtValue);
                mDialog.cancel();

            }
        });


        addcredit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String ed = ed_credit.getText().toString();

                if (ed != null && !ed.isEmpty()) {

                    double edcredit = Double.parseDouble(ed);
                    double edamt = Double.parseDouble(amtValue);

                    if (edamt < edcredit) {
                        Toast.makeText(InvoiceCashCollection.this, "Entered amount exceeds", Toast.LENGTH_SHORT).show();
                    } else {
                        hideKeyboard(ed_credit);
                        String dCredit = twoDecimalPoint(edcredit);
                        srAmttotal = 0.00;
                        srCrdttotal = 0.00;
                        Log.d("srAmttotal Clear ", "-->" + srAmttotal);
                        Log.d("srCrdttotal Clear ", "-->" + srCrdttotal);
                        endDialog(dCredit);

                        double srCrdttot = 0.00;
                        for (int i = 0; i < mList.size(); i++) {
                            HashMap<String, String> data = mList.get(i);
                            double  dCrdt = 0.00;
                            String mCrtAmt = data.get("CreditAmount");
                            String mSalesReturnno = data.get("SalesReturnNo");

                            if (mCrtAmt != null && !mCrtAmt.isEmpty()) {
                                dCrdt = Double.parseDouble(mCrtAmt);

                                if(!srnumber_txt.matches(mSalesReturnno)){
                                    srCrdttot = srCrdttot + dCrdt;
                                }
                            }
                        }

                        srCrdttot = srCrdttot + edcredit;

                        Log.d("totalcredit in dialog", "--> " + srCrdttot);
                        int selected = Adapter.getSelected();

                        if (selected == 1) {
                            /* String totalcredit = srTotalCredit.getText().toString();
                            double totcred= 0;
                            if(totalcredit != null && !totalcredit.isEmpty()){
                                totcred = Double.parseDouble(totalcredit);
                            }
                            Log.d("totalcredit in dialog",""+totcred);*/

//                            Adapter.splitSelectedCreditBalance(edcredit);
                            Adapter.splitSelectedCreditBalance(srCrdttot);
                        } else {
                            Adapter.splitBalance(edcredit);
                        }

                        Adapter.notifyDataSetChanged();

                        mDialog.cancel();
                    }
                } else {
                    Toast.makeText(InvoiceCashCollection.this, "Please enter value", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public String getDialogResult() {
        return dialogResult;
    }

    public String setDialogResult(String dialogResult) {
        return this.dialogResult = dialogResult;
    }


    public void endDialog(String result) {
        setDialogResult(result);
        Message m = mHandler.obtainMessage();
        mHandler.sendMessage(m);
    }


    public String showDialog() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                // process incoming messages here
                // super.handleMessage(msg);
                throw new RuntimeException();
            }
        };
        // super.show();
        try {
            Looper.getMainLooper();
            Looper.loop();
        } catch (RuntimeException e2) {
        }
        return dialogResult;
    }

    /**
     * Signature
     **/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {

                case SIGNATURE_ACTIVITY:
                    if (resultCode == RESULT_OK) {
                        //Bundle extras = data.getExtras();
                        byte[] bytes = data.getByteArrayExtra("status");
                        if (bytes != null) {
                            //Bitmap photo = extras.getParcelable("status");

                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 240, 80, true);

                            sm_signature.setImageBitmap(bitmap);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] bitMapData = stream.toByteArray();
                            String signature_image = Base64.encodeToString(
                                    bitMapData, Base64.DEFAULT);
                            SOTDatabase.init(InvoiceCashCollection.this);
                            Cursor ImgCursor = SOTDatabase.getImageCursor();
                            if (ImgCursor.getCount() > 0) {
                                String camera_image = SOTDatabase.getProductImage();
                                SOTDatabase.updateImage(1, signature_image,
                                        camera_image);
                            } else {
                                SOTDatabase.storeImage(1, signature_image, "");
                            }

                            Log.d("Signature Image", "Sig" + signature_image);
                        }
                    }
                    break;
            }
        }
    }

    private String checkInternetStatus() {
        String internetStatus = "";
        String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
        if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
            if(mobileHaveOfflineMode.matches("1")){
        checkOffline = OfflineCommon.isConnected(InvoiceCashCollection.this);
//        String internetStatus = "";
        if (onlineMode.matches("True")) {
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
            if (checkOffline == true) {
                if (internetStatus.matches("true")) {
                    offlineLayout.setVisibility(View.VISIBLE);
                    offlineLayout.setBackgroundResource(drawable.temp_offline_pattern_bg);
                }
            }

        } else if (onlineMode.matches("False")) {
            offlineLayout.setVisibility(View.VISIBLE);
        }
            }else{
                internetStatus = "false";
            }
        }else{
            internetStatus = "false";
        }
        return internetStatus;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.return_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_returns:

                salesreturn_linear.setVisibility(View.VISIBLE);
                signature_linear.setVisibility(View.GONE);
                csh_list.setVisibility(View.GONE);
                csh_listheader.setVisibility(View.GONE);
                total_layout.setVisibility(View.GONE);
                return true;

            case R.id.option_sign:

                salesreturn_linear.setVisibility(View.GONE);
                signature_linear.setVisibility(View.VISIBLE);
                csh_list.setVisibility(View.GONE);
                csh_listheader.setVisibility(View.GONE);
                total_layout.setVisibility(View.GONE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void showKeyboard(EditText editText) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard(EditText edittext) {
        InputMethodManager inputmethodmanager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmethodmanager
                .hideSoftInputFromWindow(edittext.getWindowToken(), 0);

    }

    @Override
    public void onLocationChanged(Location location) {
        // Getting reference to TextView tv_longitude
        Log.d("Location", "gps " + location.getLatitude());
        try {

            if (onlineMode.matches("True")) {
                if (checkOffline == true) {
                } else { // online
                    getAddress(location.getLatitude(), location.getLongitude());
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    /* Request updates at startup */
    @Override
    public void onResume()
    {
        super.onResume();
        //if(D) Log.e(TAG, "--- onResume ---");

    }
    @Override
    public void onStart() {
        super.onStart();
        //if(D) Log.e(TAG, "--- onStart ---");
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        String printertype = FWMSSettingsDatabase.getPrinterTypeStr();

      if(printertype.matches("4 Inch Bluetooth")) {
            if (!GlobalData.mBluetoothAdapter.isEnabled()) {
                GlobalData.mBluetoothAdapter.enable();
                //Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivity(enableIntent);*/
                // Otherwise, setup the chat session
            } else {
                if (GlobalData.mService == null) {
                    GlobalData.mService = new BluetoothService(this, mHandler1);
                }
            }
       }
    }
    protected void onDestroy(){
        super.onDestroy();
        if(GlobalData.mService!=null){
            GlobalData.mService.stop();
        }
    }
    public void reconnectDialog(String msg){
        final String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        final Dialog dialog = new Dialog(InvoiceCashCollection.this);

        dialog.setContentView(R.layout.dialog_reconnect);
        dialog.setTitle(msg);
        ImageView reconnect = (ImageView) dialog.findViewById(R.id.reconnect);

        reconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BluetoothAdapter.checkBluetoothAddress(macaddress))
                {
                    BluetoothDevice device = GlobalData.mBluetoothAdapter.getRemoteDevice(macaddress);
                    // Attempt to connect to the device
                    GlobalData.mService.setHandler(mHandler1);
                    GlobalData.mService.connect(device,true);

                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case GlobalData.MESSAGE_STATE_CHANGE:
                    Log.d("case","MESSAGE_STATE_CHANGE");
                    //if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case GlobalData.STATE_CONNECTED:
                            //mTitle.setText(R.string.title_connected_to);
                            //mTitle.append(mConnectedDeviceName);
                            //Intent intent = new Intent(BluetoothActivity.this,PrintActivity.class);
                            //intent.putExtra("COMM", 0);//0-BLUETOOTH
                            // Set result and finish this Activity
                            // startActivity(intent);
                            Log.d("case","STATE_CONNECTED");
                            print4Inch();
                            break;
                        case GlobalData.STATE_CONNECTING:
                            //mTitle.setText(R.string.title_connecting);
                            Log.d("case","STATE_CONNECTING");

                            break;
                        case GlobalData.STATE_LISTEN:
                            Log.d("case","STATE_LISTEN");
                            break;
                        case GlobalData.STATE_NONE:
                            Log.d("case","STATE_NONE");
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
                    reconnectDialog(msg.getData().getString("toast"));
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();
                   /* helper.dismissProgressDialog();
                    finish();
     *//*Intent i = new Intent(InvoiceCashCollection.this,
       InvoiceHeader.class);
     i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
     startActivity(i);*//*
                    if (header.matches("InvoiceHeader")) {
                        intent = new Intent(InvoiceCashCollection.this,
                                InvoiceHeader.class);

                    } else if (header.matches("CustomerHeader")) {
                        intent = new Intent(InvoiceCashCollection.this,
                                CustomerListActivity.class);

                    } else if (header.matches("RouteHeader")) {
                        intent = new Intent(InvoiceCashCollection.this,
                                RouteHeader.class);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    InvoiceCashCollection.this.finish();*/
                    break;
            }

        }
    };
    public void print4Inch(){
        int noofInvoice = Adapter.getSelected();
        int nofcopies = Integer.parseInt(stnumber.getText().toString());
        In_Cash in_SO = Adapter.getItem(Adapter.getSelectedPosition());
        String receiptDate = in_SO.getIn_Date().toString();
        final ArrayList<HashMap<String, String>> salesreturnArr = getSalesReturnValue();
        CubePrint mPrintCube = new CubePrint(InvoiceCashCollection.this,FWMSSettingsDatabase.getPrinterAddress());
        mPrintCube.setOnCompletedListener(new CubePrint.OnCompletedListener() {
            @Override
            public void onCompleted() {
                //	helper.dismissProgressDialog();
                helper.dismissProgressDialog();
                helper.showLongToast(R.string.printed_successfully);
                finish();
					/*Intent i = new Intent(InvoiceCashCollection.this,
							InvoiceHeader.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);*/
                if (header.matches("InvoiceHeader")) {
                    intent = new Intent(InvoiceCashCollection.this,
                            InvoiceHeader.class);

                } else if (header.matches("CustomerHeader")) {
                    intent = new Intent(InvoiceCashCollection.this,
                            CustomerListActivity.class);

                } else if (header.matches("RouteHeader")) {
                    intent = new Intent(InvoiceCashCollection.this,
                            RouteHeader.class);
                }else if (header.matches("ConsignmentHeader")) {
                    intent = new Intent(InvoiceCashCollection.this,
                            ConsignmentHeader.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                InvoiceCashCollection.this.finish();
            }
        });

        Log.d("productdet.size()",""+productdet.size());
        if (productdet.size() > 0) {

            if(radioButtonTxt.matches("Receipt Summary")){

                mPrintCube.ReceiptSummmaryPrint(csh_custcode.getText().toString(),
                        csh_custname.getText().toString(), saveResult, receiptDate,
                        productdet, printsortHeader, gnrlStngs, nofcopies, true, footerArr,noofInvoice);
            }else{
                mPrintCube.printReceipt(csh_custcode.getText().toString(),
                        csh_custname.getText().toString(), saveResult, receiptDate,
                        productdet, printsortHeader, gnrlStngs, nofcopies, true, footerArr,1,salesreturnArr);
            }


        }else{

            // OnlineInvoiceOfflinePrint
            mOnlineInvoicePrintOfflineArr.clear();
            HashMap<String, String> hashmap = new HashMap<String, String>();
            String paidMode = In_Cash.getPay_Mode();
            String customerName = offlineDatabase.getCustomerName(customerCode);
            Log.d("paidMode", "-->" + paidMode);
            Log.d("customerName", "-->" + customerName);
            for (int i = 0; i < ListArray.size(); i++) {
                final In_Cash in_cash = Adapter.getItem(i);
                if (in_cash.isSelected()) {
                    mOnlineInvoicePrintOfflineArr.add(in_cash);
                }
            }
            hashmap.put("ReceiptNo", saveResult);
            hashmap.put("ReceiptDate", serverdate);
            hashmap.put("CustomerCode", customerCode);
            hashmap.put("CustomerName", customerName);
            hashmap.put("PayMode", paidMode);

            mPrintCube.printOnlineInvoiceInOffline(hashmap, mOnlineInvoicePrintOfflineArr,nofcopies, footerArr);
        }
    }
    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

}
