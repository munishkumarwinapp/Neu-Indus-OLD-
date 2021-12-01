package com.winapp.sot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.model.StockCount;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.sotdetails.AddCustomer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ManualAddStockTake extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace,Constants {
    SlidingMenu menu;
    Spinner stock_take_ed;
    Button add_product;
    String stock_jsonString = null;
    JSONObject stock_jsonResponse;
    JSONArray stock_jsonMainNode;
    ArrayList<String> stock_take_no_List = new ArrayList<String>();
    boolean checkOffline;
    String onlineMode, offlineDialogStatus;
    private OfflineSettingsManager offlinemanager;
    private OfflineCommon offlineCommon;
    private OfflineDatabase offlineDatabase;
    LinearLayout customer_layout;
    TextView customer_screen,listing_screen,add_product_screen,summary_screen;
    String stockTakeNo="";
    private ArrayList<StockCount> alstockCount;
    private HashMap<String, String> mparam;
    String cmpnyCode,locCode,jsonString;
    private JSONObject jsonResponse;
    private JSONArray jsonMainNode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_menu);
        setContentView(R.layout.activity_manual_add_stock_take);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        View customNav = LayoutInflater.from(this).inflate(
                R.layout.addproduct_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        ImageButton pricetag = (ImageButton) customNav
                .findViewById(R.id.priceTag);
        ImageButton filter = (ImageButton) customNav.findViewById(R.id.filter);
        txt.setText("Add Stock");
        stock_take_ed = (Spinner)findViewById(R.id.stock_take_ed);
        add_product = (Button)findViewById(R.id.so_addProduct);
        onlineMode = OfflineDatabase.getOnlineMode();
        offlineDatabase = new OfflineDatabase(this);
        offlineCommon = new OfflineCommon(this);
        checkOffline = OfflineCommon.isConnected(this);
        Log.d("Customer checkOffline ", "-->" + checkOffline);
        customer_layout = (LinearLayout)findViewById(R.id.customer_layout);
        customer_layout.setVisibility(View.GONE);
        customer_screen = (TextView)findViewById(R.id.customer_screen);
        listing_screen = (TextView)findViewById(R.id.listing_screen);
        add_product_screen = (TextView)findViewById(R.id.addProduct_screen);
        summary_screen = (TextView)findViewById(R.id.sum_screen);
        customer_screen.setVisibility(View.GONE);
        stock_take_no_List.clear();
        alstockCount = new ArrayList<>();
        mparam = new HashMap<>();
        cmpnyCode = SupplierSetterGetter.getCompanyCode();
        locCode = SalesOrderSetGet.getLocationcode();

        StockTakeProduct savecustomerservice = new StockTakeProduct();
        savecustomerservice.execute();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            stockTakeNo = getIntent().getStringExtra("stckTakeNo");
            Log.d("stocktakeNo",stockTakeNo);
        }

        listing_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManualAddStockTake.this,ManualStockHeader.class);
                startActivity(intent);
                finish();
            }
        });

        add_product.setTextColor(Color.parseColor("#FFFFFF"));
        add_product.setBackgroundResource(R.drawable.tab_select);

        summary_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManualAddStockTake.this,ManualStockSummary.class);
                startActivity(intent);
                SalesOrderSetGet.setCon_no(stockTakeNo);
                finish();
            }
        });

        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManualAddStockTake.this,ManualStockSummary.class);
                startActivity(intent);
                SalesOrderSetGet.setCon_no(stockTakeNo);
                finish();
            }
        });



        ab.setCustomView(customNav);
        ab.setDisplayShowCustomEnabled(true);

        ab.setDisplayHomeAsUpEnabled(false);
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slidemenufragment);
        menu.setSlidingEnabled(false);




        stock_take_ed
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView,
                                               View selectedItemView, int position, long id) {

                        parentView.getItemAtPosition(position).toString();
                        String cgrpcode = stock_take_ed.getSelectedItem().toString();
                        SalesOrderSetGet.setCon_no(cgrpcode);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    @Override
    public void onListItemClick(String item) {
       menu.toggle();
    }

    private class StockTakeProduct extends AsyncTask<Void, Void, Void> {
        String dialogStatus;
        @Override
        protected void onPreExecute() {
            dialogStatus = checkInternetStatus();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (onlineMode.matches("True")) {
                stock_jsonString = WebServiceClass
                        .URLServices("fncGetStockCountHeader");

                try {
                    stock_jsonResponse = new JSONObject(stock_jsonString);
                    stock_jsonMainNode = stock_jsonResponse
                            .optJSONArray("JsonArray");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                stock_take_no_List.add("Select");

                int lengthJsonArr1 = stock_jsonMainNode.length();
                for (int i = 0; i < lengthJsonArr1; i++) {
                    /****** Get Object for each JSON node. ***********/
                    JSONObject grpcode_jsonChildNode;
                    try {

                        grpcode_jsonChildNode = stock_jsonMainNode
                                .getJSONObject(i);

                        String grp_Code = grpcode_jsonChildNode.optString(
                                "StockTakeNo").toString();
                        Log.d("grp_Code",grp_Code);

                        stock_take_no_List.add(grp_Code);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("stock_take_no_List1","-->"+stock_take_no_List.size());
            if (stock_take_no_List.size() != 0) {
                stock_take_ed
                        .setAdapter(new MyCustomAdapter(ManualAddStockTake.this,
                                R.layout.row, stock_take_no_List));
            }

//            Log.d("stockTakeNo",stockTakeNo);

            int select_pos_stock=0;
            if(!stockTakeNo.matches("")){
                if (stockTakeNo.matches("-1") || stockTakeNo.matches("null")
                        || stockTakeNo.matches("")) {
                    stockTakeNo = "Select";
                }

                for (int i = 0; i < stock_take_no_List.size(); i++) {
                    if (stock_take_no_List.get(i).matches(stockTakeNo)) {
                        select_pos_stock = i;

                    }
                }
                stock_take_ed.setSelection(select_pos_stock);
                new GetStockCountDetailByNo().execute(stockTakeNo);
            }


        }
    }

    private String checkInternetStatus() {
        checkOffline = OfflineCommon.isConnected(ManualAddStockTake.this);
        String internetStatus = "";
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
//        if (onlineMode.matches("True")) {
//            offlineLayout.setVisibility(View.GONE);
//        } else if (onlineMode.matches("False")) {
//            offlineLayout.setVisibility(View.VISIBLE);
//        }

        return internetStatus;
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {

        ArrayList<String> adapterList = new ArrayList<String>();

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            adapterList.clear();
            adapterList = objects;
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
            label.setText(adapterList.get(position));
            icon.setVisibility(View.GONE);
            return row;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ManualAddStockTake.this,ManualStockHeader.class);
        startActivity(intent);
        finish();
    }

    private class GetStockCountDetailByNo extends AsyncTask<Object, Void, Void>{
        String ppc,quantity,ccqty,lqty,ctqty;
        @Override
        protected void onPreExecute() {
            alstockCount.clear();
        }

        @Override
        protected Void doInBackground(Object... objects) {
            String stockTake = (String) objects[0];

            Log.d("stockTakeCheck",stockTake);

            mparam.put("CompanyCode", cmpnyCode);
            mparam.put("LocationCode", locCode);
            mparam.put("StockTakeNo", stockTake);
            try {
                jsonString = SalesOrderWebService.getSODetail(mparam, FNC_GETSTOCKCOUNTDETAILBYNO);

                Log.d("jsonString ", "" + jsonString);

                jsonResponse = new JSONObject(jsonString);
                jsonMainNode = jsonResponse.optJSONArray("SODetails");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                int lengthJsonArr = jsonMainNode.length();
                for (int i = 0; i < lengthJsonArr; i++) {
                    StockCount stockcount = new StockCount();
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    String productcodes = jsonChildNode.getString(PRODUCT_CODE);
                    String productnames = jsonChildNode.getString(PRODUCT_NAME);
                    stockcount.setProductCode(productcodes);
                    stockcount.setProductName(productnames);
                    stockcount.setCategoryCode(jsonChildNode.getString(CATEGORYCODE));
                    stockcount.setSubcategoryCode(jsonChildNode.getString(SUBCATEGORYCODE));

                    stockcount.setHavebatch(jsonChildNode.getString(HAVEBATCH));
                    stockcount.setHavexpiry(jsonChildNode.getString(HAVEEXPIRY));

                    String pcspercarton = jsonChildNode.getString(PCSPERCARTON);

                    if (pcspercarton != null && !pcspercarton.isEmpty()) {
                         ppc = String.valueOf(pcspercarton).split("\\.")[0];

                        stockcount.setPcsPerCarton(Integer.valueOf(ppc));
                    } else {
                        stockcount.setPcsPerCarton(0);
                    }
                    String noofcarton = jsonChildNode.getString(NOOFCARTON);
                    if (noofcarton != null && !noofcarton.isEmpty()) {
                        String nofcarton = String.valueOf(noofcarton).split(
                                "\\.")[0];
                        stockcount.setNoOfCarton(Integer.valueOf(nofcarton));
                    } else {
                        stockcount.setNoOfCarton(0);
                    }
                    String qty = jsonChildNode.getString(QTY);
                    if (qty != null && !qty.isEmpty()) {
                         quantity = String.valueOf(qty).split("\\.")[0];
                        stockcount.setQty(Integer.valueOf(quantity));
                    } else {
                        stockcount.setQty(0);
                    }
                    String countlqty = jsonChildNode.getString(COUNTLQTY);
                    if (countlqty != null && !countlqty.isEmpty()) {
                         lqty = String.valueOf(countlqty).split("\\.")[0];
                        stockcount.setCountLQty(Integer.valueOf(lqty));
                    } else {
                        stockcount.setCountLQty(0);
                    }
                    String countqty = jsonChildNode.getString(COUNTQTY);
                    if (countqty != null && !countqty.isEmpty()) {
                         ctqty = String.valueOf(countqty).split("\\.")[0];
                        stockcount.setCountQty(Integer.valueOf(ctqty));
                    } else {
                        stockcount.setCountQty(0);
                    }

                    String countcqty = jsonChildNode.getString(COUNTCQTY);
                    if (countcqty != null && !countcqty.isEmpty()) {
                        ccqty = String.valueOf(countcqty).split("\\.")[0];
                        stockcount.setCountCQty(Integer.valueOf(ccqty));
                    } else {
                        stockcount.setCountCQty(0);
                    }
                    SOTDatabase.storeManualStock("","",productcodes,productnames,ccqty,lqty,ppc,ctqty,"");
                    alstockCount.add(stockcount);
                }

            } catch (JSONException e) {

                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mparam.clear();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
}
