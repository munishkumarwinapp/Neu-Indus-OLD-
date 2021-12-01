package com.winapp.sot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.model.CurrencyDeno;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.printer.CommonPreviewPrint;
import com.winapp.printer.PDFActivity;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.SettlementPrintPreview;
import com.winapp.printer.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.winapp.sot.SOTSummaryWebService.context;

public class SettlementMainHeader extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace,Constants {
    SlidingMenu menu;
    ImageButton searchIcon, custsearchIcon, printer,save;
    List<CurrencyDeno> listData;
    String cmpnyCode,valid_url,jsonString ="",jsonStr = "";
    private OfflineDatabase offlineDatabase;
    boolean checkOffline;
    String onlineMode, offlineDialogStatus;
    private OfflineCommon offlineCommon;
    LinearLayout offlineLayout,searchlayout;
    SettlementAdapter adapter;
    ListView listview;
    String settlementNo,sdate,sTotal,lctn,user;
    private UIHelper helper;
    ArrayList<CurrencyDeno> product;
    ArrayList<CurrencyDeno> productdet;
    HashMap<String, String> hashValue = new HashMap<String, String>();
    JSONObject jsonResponse,jsonResp;
    JSONArray jsonMainNode,jsonSecNode;
    int printid;
    Cursor cursor;
    private HashMap<String, String> params;
    ImageButton print;
    EditText edprintDate,edprintDate1,edprintUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_menu);
        Mint.initAndStartSession(SettlementMainHeader.this, "29088aa0");
        setContentView(R.layout.activity_settlement_main_header);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        View customNav = LayoutInflater.from(this).inflate(
                R.layout.slidemenu_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        txt.setText("Settlement Header");
        searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
        printer = (ImageButton) customNav.findViewById(R.id.printer);
        custsearchIcon = (ImageButton) customNav
                .findViewById(R.id.custcode_img);
        save = (ImageButton)customNav.findViewById(R.id.save);
        searchIcon.setVisibility(View.VISIBLE);
        save.setVisibility(View.GONE);
        SOTDatabase.init(context);
        cmpnyCode = SupplierSetterGetter.getCompanyCode();
        helper = new UIHelper(SettlementMainHeader.this);
//        printer.setVisibility(View.VISIBLE);

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
        onlineMode = OfflineDatabase.getOnlineMode();
        offlineDatabase = new OfflineDatabase(SettlementMainHeader.this);
        offlineCommon = new OfflineCommon(SettlementMainHeader.this);
        checkOffline = OfflineCommon.isConnected(SettlementMainHeader.this);
        OfflineDatabase.init(SettlementMainHeader.this);
        offlineLayout = (LinearLayout) findViewById(R.id.inv_offlineLayout);
        FWMSSettingsDatabase.init(SettlementMainHeader.this);
        SOTDatabase.init(SettlementMainHeader.this);
        valid_url = FWMSSettingsDatabase.getUrl();
        new SalesOrderWebService(valid_url);
        searchlayout = (LinearLayout)findViewById(R.id.searchlayout);
        searchlayout.setVisibility(View.GONE);
        listview = (ListView)findViewById(R.id.saleO_listView1);
        SOTDatabase.deleteSettlement();
        print = (ImageButton)findViewById(R.id.invoiceProductPrint);

        registerForContextMenu(listview);

        AsyncCallWSSalesOrder salesOAC = new AsyncCallWSSalesOrder();
        salesOAC.execute();

        custsearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettlementMainHeader.this,SettlementAddDenomination.class);
                startActivity(intent);
                finish();

            }
        });
        searchIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                InputMethodManager inputMethodManager = (InputMethodManager) SettlementMainHeader.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

                if (searchlayout.getVisibility() == View.VISIBLE) {
                    searchlayout.setVisibility(View.GONE);

                } else {
                    searchlayout.setVisibility(View.VISIBLE);

                }
            }
        });

        printer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printid = v.getId();

                cursor = FWMSSettingsDatabase.getPrinter();

                String printertype = FWMSSettingsDatabase.getPrinterTypeStr();
                Log.d("printertype", printertype);

                String cmpy = SupplierSetterGetter.getCompanyCode();
                if(printertype.matches("Zebra iMZ320")||(printertype.matches("4 Inch Bluetooth"))||(printertype.matches("3 Inch Bluetooth Generic"))||
                        printertype.matches("Zebra iMZ320 4 Inch")) {
                    loadPrintData(R.string.generating_settlement);
//                    if (cursor.getCount() != 0) {
//
//                    }else {
//                        Toast.makeText(SettlementMainHeader.this,
//                                "Please Configure the printer", Toast.LENGTH_SHORT)
//                                .show();
//                    }
                }
                else{
                    Log.d("PDFActivity", "PDFActivity");

                    CurrencyDeno deno = adapter.getItem(adapter
                            .getSelectedPosition());
                    settlementNo = deno.getSettlementNo().toString();
                    new PDFActivity(SettlementMainHeader.this,valid_url+"/"+FNCA4INVOICEGENERATE+"?CompanyCode="+cmpy+"&SettlementNo="+settlementNo, "report.pdf").execute();

//new DownloadFile().execute("http://SFA.com:81/SFAAPI.asmx/fncA4InvoiceGenerate?CompanyCode=1&sInvoiceNo=HQ2016-000366", "report.pdf");
                }
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settlementDatePrintDialog();
            }
        });

    }

    private void settlementDatePrintDialog() {
        cursor = FWMSSettingsDatabase.getPrinter();
        if (cursor.getCount() != 0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    SettlementMainHeader.this);

            TextView textView0 = new TextView(SettlementMainHeader.this);
            TextView textview = new TextView(SettlementMainHeader.this);
            TextView textview1 = new TextView(SettlementMainHeader.this);
            edprintUser = new EditText(SettlementMainHeader.this);
            edprintDate = new EditText(SettlementMainHeader.this);
            edprintDate1 = new EditText(SettlementMainHeader.this);

            LinearLayout layout = new LinearLayout(SettlementMainHeader.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            alert.setTitle("Print");
            alert.setCancelable(true);
            layout.setPadding(15, 15, 15, 15);
            textView0.setPadding(0, 15, 0, 0);

            textView0.setText("Enter from date");
            textview.setText("Enter to date");
            textview1.setText("User");

            edprintDate.setBackgroundResource(R.drawable.edittext_bg);
            edprintDate1.setBackgroundResource(R.drawable.edittext_bg);

            edprintUser.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_search, 0);
            edprintUser.setCursorVisible(false);
            edprintUser.setText(SupplierSetterGetter.getUsername());

            String dates = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
            Log.d("Datecheck","--?>"+dates);

            edprintDate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.mipmap.ic_calendar, 0);
            edprintDate.setCursorVisible(false);
            edprintDate.setText(dates);

            edprintDate1.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.mipmap.ic_calendar, 0);
            edprintDate1.setCursorVisible(false);
            edprintDate1.setText(dates);

            layout.addView(textView0);
            layout.addView(edprintDate);
            layout.addView(textview);
            layout.addView(edprintDate1);
            layout.addView(textview1);
            layout.addView(edprintUser);
            alert.setView(layout);

        }
    }

    private void loadPrintData(int generating_settlement) {
        AsyncPrintCall task = new AsyncPrintCall();
        task.execute();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        CurrencyDeno deno = adapter.getItem(info.position);
        settlementNo = deno.getSettlementNo().toString();
        sdate = deno.getSettlementDate().toString();
        sTotal = deno.getTotlAmt().toString();
//        lctn = deno.getLocationcode().toString();
//        user = deno.getSettlementBy().toString();
        menu.setHeaderTitle(settlementNo);

        menu.add(0, v.getId(), 0, "Print Preview");

        menu.add(0, v.getId(), 0, "Print");

    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {

        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        CurrencyDeno deno = adapter.getItem(adapterInfo.position);
        settlementNo = deno.getSettlementNo().toString();
        sdate = deno.getSettlementDate().toString();
        sTotal = deno.getTotlAmt().toString();
//        lctn = deno.getLocationcode().toString();
//        user = deno.getSettlementBy().toString();
        if (item.getTitle() == "Print Preview") {
            helper.showProgressDialog(R.string.generating_settlement);

            // temp
            new AsyncPrintCall().execute();
        }

        if(item.getTitle() == "Print"){
            helper.showProgressDialog(R.string.generating_settlement);
            printid = -1;

            new AsyncPrintCall().execute();
        }


        return true;
    }

    @Override
    public void onListItemClick(String item) {
        menu.toggle();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        menu.toggle();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettlementMainHeader.this,LandingActivity.class);
        startActivity(intent);
        finish();
    }

    private class AsyncCallWSSalesOrder extends AsyncTask<Void, Void, Void> {
        String dialogStatus;
        @Override
        protected void onPreExecute() {
            dialogStatus = checkInternetStatus();
            listData = new ArrayList<>();
            listData.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HashMap<String, String> hm = new HashMap<String, String>();

            hm.put("CompanyCode", cmpnyCode);

                Log.d("logindata","-->"+"Executed");
                try {
                    listData = SalesOrderWebService.getSettlementHeaderList(hm, "fncGetSettlementHeader");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
          Log.d("listDataCount",""+listData.size());
            adapter = new SettlementAdapter(SettlementMainHeader.this,listData);
            listview.setAdapter(adapter);
        }
    }

    private String checkInternetStatus() {
        String internetStatus = "";
        String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
        if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
            if(mobileHaveOfflineMode.matches("1")){

                checkOffline = OfflineCommon.isConnected(SettlementMainHeader.this);
//		String internetStatus = "";
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
                            offlineLayout.setBackgroundResource(R.drawable.temp_offline_pattern_bg);
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

    private class SettlementAdapter extends BaseAdapter {
        Context context;
        List<CurrencyDeno> denoList;
        LayoutInflater mInflater;
        private int selectedPosition = -1;
        public SettlementAdapter(Context context, List<CurrencyDeno> listData) {
            this.context = context;
            this.denoList = listData;
            mInflater =LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return denoList.size();
        }

        @Override
        public CurrencyDeno getItem(int position) {
            return denoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.settlemnt_adapter, null);
                holder.settlementNo = (TextView)convertView.findViewById(R.id.settlementNo);
                holder.date = (TextView)convertView.findViewById(R.id.date);
                holder.total = (TextView)convertView.findViewById(R.id.totalAmt);

                CurrencyDeno deno = denoList.get(position);
                holder.settlementNo.setText(deno.getSettlementNo());
                holder.date.setText(deno.getSettlementDate());
                holder.total.setText(deno.getTotlAmt());

            }

            return convertView;
        }
        class ViewHolder {
            TextView settlementNo,date,total;
        }
        public int getSelectedPosition() {
            return selectedPosition;
        }

        public void setSelectedPosition(int selectedPosition) {
            this.selectedPosition = selectedPosition;
            notifyDataSetChanged();
        }

    }

    private class AsyncPrintCall extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            product = new ArrayList<>();
            productdet = new ArrayList<>();
            product.clear();
            productdet.clear();
            jsonString = "";
            jsonStr ="";
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            String cmpnyCode = SupplierSetterGetter.getCompanyCode();
            String locationcode = SalesOrderSetGet.getLocationcode();
            Log.d("settlementNo","-->"+settlementNo);

            hashValue.put("CompanyCode", cmpnyCode);
            hashValue.put("SettlementNo", settlementNo);

            jsonString = SalesOrderWebService.getSettlementDetail(hashValue,"fncGetSettlementDetail");
            jsonStr = SalesOrderWebService.getSettlementDetail(hashValue,"fncGetSettlementHeaderBySettlementNo");


            Log.d("jsonString ", jsonString);
            Log.d("jsonStr ", jsonStr);

            try {

                if(jsonString != null && !jsonString.isEmpty()){

                    jsonResponse = new JSONObject(jsonString);
                    jsonMainNode = jsonResponse.optJSONArray("SODetails");


                    /*********** Process each JSON Node ************/
                    int lengthJsonArr = jsonMainNode.length();
                    for (int i = 0; i < lengthJsonArr; i++) {
                        /****** Get Object for each JSON node. ***********/
                        JSONObject jsonChildNode;
                        CurrencyDeno productdetail = new CurrencyDeno();
                        try {
                            jsonChildNode = jsonMainNode.getJSONObject(i);

                            productdetail.setSlno(jsonChildNode.optString(
                                    "SlNo").toString());
                            productdetail.setCurency(jsonChildNode.optString(
                                    "Denomination").toString());

                            String salesOrderqty = jsonChildNode.optString("DenominationCount")
                                    .toString();

                            productdetail.setDenomination(salesOrderqty);

                            productdetail.setTotal(jsonChildNode.optString("Total")
                                    .toString());
                            Log.d("TotalChecl","-->"+jsonChildNode.optString("Total")
                                    .toString());
                            product.add(productdetail);
                            Log.d("SettlementDetail ", ":" + product.size());

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }

                if(jsonStr != null && !jsonStr.isEmpty()){

                    jsonResp = new JSONObject(jsonStr);
                    jsonSecNode = jsonResp.optJSONArray("SODetails");

                    int lengJsonArr = jsonSecNode.length();
                    for (int i = 0; i < lengJsonArr; i++) {

                        JSONObject jsonChildNode;
                        CurrencyDeno productdetail = new CurrencyDeno();
                        try {
                            jsonChildNode = jsonSecNode.getJSONObject(i);
                            productdetail.setSettlementNo(jsonChildNode.optString(
                                    "SettlementNo").toString());
                            productdetail.setSettlementDate(jsonChildNode.optString(
                                    "SettlementDate").toString());
                            productdetail.setTotlAmt(jsonChildNode.optString(
                                    "TotalAmount").toString());
                            productdetail.setSettlementBy(jsonChildNode.optString("SettlementBy")
                                    .toString());
                            productdetail.setLocationcode(jsonChildNode.optString("LocationCode")
                                    .toString());



                            productdet.add(productdetail);

                            Log.d("SettlementHeader ", ":" + productdet.toString());

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
            hashValue.clear();
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            if (printid == -1) {
                try {
                    printid = 0;
                    print();
                } catch (Exception e) {

                    e.printStackTrace();
                }
            } else {
                Intent i = new Intent(SettlementMainHeader.this,
                        SettlementPrintPreview.class);

                i.putExtra("No", settlementNo);
                i.putExtra("Date", sdate);
                i.putExtra("Total", sTotal);
                i.putExtra("location",lctn);
                i.putExtra("user",user);
                Log.d("productCount123","-->"+product.size());
                PreviewPojo.setSettlementDetail(product);
                PreviewPojo.setSettlementHeader(productdet);
                startActivity(i);
            }
        }
    }

    private void print() {
        helper.dismissProgressDialog();
        String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        Log.d("macaddressSearch","--?"+macaddress);
        try {
            Printer printer = new Printer(SettlementMainHeader.this, macaddress);
//            if (adapter.getSelectedPosition() != -1) {
                /*CurrencyDeno so = adapter.getItem(adapter.getSelectedPosition());

                settlementNo = so.getSettlementNo().toString();
                sdate = so.getSettlementDate().toString();
                sTotal = so.getTotlAmt().toString();*/

            for(int i =0;i<productdet.size();i++){
                lctn=productdet.get(i).getLocationcode();
                user =productdet.get(i).getSettlementBy();
            }


                Log.d("printSettlement","-->"+settlementNo);
                printer.printSettlement(settlementNo, sdate, sTotal,lctn,user, product, productdet, 1);

//            }
        }catch (IllegalArgumentException e) {
            helper.showLongToast(R.string.error_configure_printer);
        }
    }
}
