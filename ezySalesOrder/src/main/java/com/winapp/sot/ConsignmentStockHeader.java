package com.winapp.sot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.SFA.R;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.sotdetails.ConsignmentDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConsignmentStockHeader extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace {
    private SlidingMenu menu;
    EditText cust_code,cust_name;
    Button search;
    ListView listView;
    StockAdapter adapter;
    private AlertDialog myalertDialog = null;
    CustomAlertAdapterSupp arrayAdapterSupp;
    ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
    int textlength = 0;
    ArrayList<HashMap<String, String>> searchResults;
    String keyValues ="",cust_code_value ="",select_van ="",valid_url="",serverdate,settingvalue;
    ArrayList<HashMap<String, String>> currencycode = new ArrayList<HashMap<String, String>>();
    private static String NAMESPACE = "http://tempuri.org/";
    private static String SOAP_ACTION = "http://tempuri.org/";
    private static String webMethName = "fncGetCustomer";
    LinearLayout spinnerLayout,delivery_layout;
    ArrayList<HashMap<String, String>> stockConsignmentArr;
    ProgressBar progressBar;
    static String custresult;
    String suppTxt = null;
    ArrayList<String> aldelivery = new ArrayList<String>();
    HashMap<String, String> hashmap = new HashMap<String, String>();

    ImageButton back, save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_menu);
        Mint.initAndStartSession(ConsignmentStockHeader.this, "29088aa0");
        setContentView(R.layout.activity_consignment_stock_header);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(false);
        View customNav = LayoutInflater.from(this).inflate(
                R.layout.summary_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        txt.setText("Consignment Stock");

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


        back = (ImageButton) customNav.findViewById(R.id.back);
        save = (ImageButton) customNav.findViewById(R.id.save);
        back.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);
        cust_code = (EditText)findViewById(R.id.deliOCustCode);
        cust_name = (EditText)findViewById(R.id.sl_namefield);
        listView = (ListView)findViewById(R.id.deliO_listView1);
        search =(Button)findViewById(R.id.deliO_btsearch);
        delivery_layout = (LinearLayout) findViewById(R.id.deliOrder_parent);

        AsyncCallWSSaleOrder inAcws = new AsyncCallWSSaleOrder();
        inAcws.execute();


        stockConsignmentArr = new ArrayList<HashMap<String, String>>();

        valid_url = FWMSSettingsDatabase.getUrl();
        new DateWebservice(valid_url,ConsignmentStockHeader.this);
        new SalesOrderWebService(valid_url);

        SOTDatabase.init(ConsignmentStockHeader.this);
        select_van = SOTDatabase.getVandriver();

        if(select_van!=null && !select_van.isEmpty()){
        }else{
            select_van="";
        }


        cust_code.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(cust_code) {
                    @Override
                    public boolean onDrawableClick() {
                        custalertDialogSearch();
                        return true;

                    }

                });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cust_code_value=cust_code.getText().toString();
               new AsyncStockTask(cust_code_value).execute();
            }
        });

        cust_code
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId,
                                                  KeyEvent event) {

                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                            boolean res = false;
                            for (String alphabet : aldelivery) {
                                if (alphabet.toLowerCase().equals(
                                        cust_code.getText().toString()
                                                .toLowerCase())) {
                                    res = true;
                                    break;
                                }
                            }
                            if (res == true) {
                                Set<Map.Entry<String, String>> keys = hashmap
                                        .entrySet();
                                Iterator<Map.Entry<String, String>> iterator = keys
                                        .iterator();
                                while (iterator.hasNext()) {
                                    @SuppressWarnings("rawtypes")
                                    Map.Entry mapEntry = iterator.next();
                                    String keyValue = (String) mapEntry
                                            .getKey();
                                    String value = (String) mapEntry.getValue();
                                    if (cust_code.getText().toString()
                                            .toLowerCase()
                                            .equals(keyValue.toLowerCase())) {
                                        cust_code.setText(value);
                                    }
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Invalid Customer Code",
                                        Toast.LENGTH_SHORT).show();
                                cust_code.setText("");
                                cust_name.setText("");
                            }
                            cust_code
                                    .addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }

                                        @Override
                                        public void beforeTextChanged(
                                                CharSequence s, int start,
                                                int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(
                                                CharSequence s, int start,
                                                int before, int count) {

                                            textlength = cust_code
                                                    .getText().length();
                                            cust_name.setText("");
                                        }
                                    });

                        }

                        return false;
                    }
                });

    }


    private class AsyncCallWSSaleOrder extends AsyncTask<Void, Void, Void> {
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            al.clear();

            spinnerLayout = new LinearLayout(ConsignmentStockHeader.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            addContentView(spinnerLayout, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(delivery_layout, false);
            progressBar = new ProgressBar(ConsignmentStockHeader.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    R.drawable.greenprogress));
            spinnerLayout.addView(progressBar);
            boolean check= isNetworkConnected();
            if(check == false){
                finish();
            }

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            serverdate = DateWebservice.getDateService("fncGetServerDate");
            currencycode = SalesOrderWebService
                    .getAllCurrency("fncGetCurrency");

            SoapObject request = new SoapObject(NAMESPACE, webMethName);
//			PropertyInfo companyCode = new PropertyInfo();
//
            String cmpnyCode = SupplierSetterGetter.getCompanyCode();
//
//			companyCode.setName("CompanyCode");
//			companyCode.setValue(cmpnyCode);
//			companyCode.setType(String.class);
//			request.addProperty(companyCode);

            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("CompanyCode", cmpnyCode);
            hm.put("VanCode", select_van);

            for (Map.Entry<String, String> entry : hm.entrySet()) {

                PropertyInfo prodCode = new PropertyInfo();

                prodCode.setName(entry.getKey());
                prodCode.setValue(entry.getValue());
                prodCode.setType(String.class);
                request.addProperty(prodCode);

                System.out.printf("%s", entry.getKey());
                System.out.printf("%s%n", entry.getValue());
            }


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
                custresult = " { SaleOCustomer : " + suppTxt + "}";
                JSONObject jsonResponse;
                try {
                    jsonResponse = new JSONObject(custresult);
                    JSONArray jsonMainNode = jsonResponse
                            .optJSONArray("SaleOCustomer");
                    int lengthJsonArr = jsonMainNode.length();
                    for (int i = 0; i < lengthJsonArr; i++) {
                        JSONObject jsonChildNode = jsonMainNode
                                .getJSONObject(i);

                        String customercode = jsonChildNode.optString(
                                "CustomerCode").toString();
                        String customername = jsonChildNode.optString(
                                "CustomerName").toString();

                        String ReferenceLocation = jsonChildNode.optString(
                                "ReferenceLocation").toString();

                        Log.d("custNameAndCode", ""+customercode +" "+customername);

                        HashMap<String, String> customerhm = new HashMap<String, String>();
                        if(ReferenceLocation!=null && !ReferenceLocation.isEmpty()){
                            customerhm.put(customercode, customername+"~"+ReferenceLocation);
                        }else{
                            customerhm.put(customercode, customername);
                        }

//						HashMap<String, String> customerhm = new HashMap<String, String>();
//						customerhm.put(customercode, customername);
                        al.add(customerhm);
                        hashmap.putAll(customerhm);
                        aldelivery.add(customercode);

                    }
                    settingvalue = SalesOrderWebService
                            .generalSettingsService("fncGetGeneralSettings");


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
            gettingSetGet();
            /*
             * delivedcodefield.setText(SalesOrderSetGet.getCustomercode());
             * delivednamefield.setText(SalesOrderSetGet.getCustomername());
             * edtdate.setText(serverdate); edcrrncycd.setText(settingvalue);
             * edcrrncynm.setText(crrncynm); edcrrncyrt.setText(crrncyrt);
             */
            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(delivery_layout, true);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void gettingSetGet() {
        cust_code.setText(SalesOrderSetGet.getCustomercode());
        cust_name.setText(SalesOrderSetGet.getCustomername());
    }

    public void custalertDialogSearch() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(
                ConsignmentStockHeader.this);
        final EditText editText = new EditText(ConsignmentStockHeader.this);
        final ListView listview = new ListView(ConsignmentStockHeader.this);
        LinearLayout layout = new LinearLayout(ConsignmentStockHeader.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        myDialog.setTitle("Customer");
        editText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.search, 0,
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
        arrayAdapterSupp = new CustomAlertAdapterSupp(ConsignmentStockHeader.this, al);
        listview.setAdapter(arrayAdapterSupp);
        // listview.setOnItemClickListener(NewCustomer.this);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                myalertDialog.dismiss();
                getArraylsit = arrayAdapterSupp.getArrayList();
                HashMap<String, String> datavalue = getArraylsit.get(position);
                Set<Map.Entry<String, String>> keys = datavalue.entrySet();
                Iterator<Map.Entry<String, String>> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    @SuppressWarnings("rawtypes")
                    Map.Entry mapEntry = iterator.next();
                     keyValues = (String) mapEntry.getKey();
                    String values = (String) mapEntry.getValue();

                    Log.d("onclick_loc", ""+values);

                    if(values.contains("~")){
                        String[] parts = values.split("~");
                        String name = parts[0];
//						String location = parts[1];

                        cust_code.setText(keyValues);
                        cust_name.setText(name);
                    }else{
                        cust_code.setText(keyValues);
                        cust_name.setText(values);
                    }

//					delivedcodefield.setText(keyValues);
//					delivednamefield.setText(values);

                    cust_code.addTextChangedListener(new TextWatcher() {
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

                            textlength = cust_code.getText().length();
                            cust_name.setText("");

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
                        ConsignmentStockHeader.this, searchResults);
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

    @Override
    public void onListItemClick(String item) {
        menu.toggle();
    }

    private class StockAdapter extends BaseAdapter {
        Context context;
        ArrayList<HashMap<String, String>> array_list;
        private LayoutInflater mInflater = null;
        String calCarton ="";
        String cust_code ="";
        public StockAdapter(ConsignmentStockHeader consignmentStockHeader, ArrayList<HashMap<String, String>> array, String cust_code_value) {
            mInflater = LayoutInflater.from(consignmentStockHeader);
            this.context=consignmentStockHeader;
            this.array_list =array;
            this.cust_code =cust_code_value;
            Log.d("array_list",""+array_list.size());

        }

        @Override
        public int getCount() {
            return array_list.size();
        }

        @Override
        public Object getItem(int position) {
            return array_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.activity_consignment_stock_adapter, null);
                holder.slNo =(TextView)convertView.findViewById(R.id.ss_slno);
                holder.pCode =(TextView) convertView.findViewById(R.id.ss_prodcode);
                holder.pName =(TextView)convertView.findViewById(R.id.ss_name);
                holder.cQty =(TextView)convertView.findViewById(R.id.ss_cqty);
                holder.lQty =(TextView)convertView.findViewById(R.id.ss_lqty);
                holder.cqtyLayout =(LinearLayout)convertView.findViewById(R.id.cqty_layout);
                holder.lqtyLayout =(LinearLayout)convertView.findViewById(R.id.lQty_layout);
                holder.qty =(TextView)convertView.findViewById(R.id.ss_qty);
                holder.edit =(ImageView)convertView.findViewById(R.id.edit);
                holder.pcsperCarton =(TextView)convertView.findViewById(R.id.ss_pcs_carton);

                calCarton = LogOutSetGet.getCalcCarton();
                holder.slNo.setText(array_list.get(position).get("SlNo"));
                holder.pCode.setText(array_list.get(position).get("productCode"));
                holder.pName.setText(array_list.get(position).get("productName"));
                String pcsPerCarton = array_list.get(position).get("pcsPerCarton");

                final String pCode =array_list.get(position).get("productCode");
                final String pName =array_list.get(position).get("productName");
                final String qty =array_list.get(position).get("qty");

                holder.qty.setText(array_list.get(position).get("qty"));

                Log.d("quantity","-->"+array_list.get(position).get("qty"));

                double ppcarton =Double.parseDouble(pcsPerCarton);
                int ppc =(int) Math.round(ppcarton);
                String pcc=String.valueOf(ppc);
//                int ppc = 4;
                holder.pcsperCarton.setText(pcc);



                if(ppc<=1){
                    holder.cqtyLayout.setVisibility(View.INVISIBLE);
                    holder.lqtyLayout.setVisibility(View.INVISIBLE);
                    holder.qty.setText(array_list.get(position).get("qty"));
                }else {
                    holder.cQty.setText(array_list.get(position).get("cQty").split("\\.")[0]);
                    holder.lQty.setText(array_list.get(position).get("lqty").split("\\.")[0]);
                    holder.qty.setText(array_list.get(position).get("qty"));

                }


                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConsignmentDialogFragment dialogFragment = ConsignmentDialogFragment.newInstance(ConsignmentStockHeader.this, pCode,cust_code,pName,qty,"ConsignmentStockHeader");
                        dialogFragment.show(getFragmentManager(), "dialog");
                    }
                });



            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            return convertView;
        }

        private class ViewHolder {
            TextView slNo;
            TextView pName;
            TextView qty;
            TextView pCode;
            TextView cQty;
            TextView lQty,pcsperCarton;
            LinearLayout cqtyLayout,lqtyLayout;
            ImageView edit;

        }
    }

    public class AsyncStockTask extends AsyncTask<Void, Void, Void>{
        String code;
        public AsyncStockTask(String cust_code_value) {
            this.code=cust_code_value;
        }

        @Override
        protected void onPreExecute() {

            spinnerLayout = new LinearLayout(ConsignmentStockHeader.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            addContentView(spinnerLayout, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(delivery_layout, false);
            progressBar = new ProgressBar(ConsignmentStockHeader.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    R.drawable.greenprogress));
            spinnerLayout.addView(progressBar);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            String cust_codes=cust_code.getText().toString();
            Log.d("cust_codes",""+cust_codes);
            stockConsignmentArr=SalesOrderWebService.
                    getStockConsignmentArrs("fncGetConsignmentProductStock",cust_codes, "");
            Log.d("stockConsignmentArr","->"+stockConsignmentArr.size());
            if(stockConsignmentArr.size()==0){
//                Toast.makeText(getApplicationContext(),"No Data Found",Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (ConsignmentStockHeader.this != null) {
                headerCustCode(code);
                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(delivery_layout, true);

            }
        }


    }

    private void headerCustCode(String code) {
        adapter=new StockAdapter(ConsignmentStockHeader.this,stockConsignmentArr,code);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        Intent i =new Intent(ConsignmentStockHeader.this, LandingActivity.class);
        startActivity(i);
        ConsignmentStockHeader.this.finish();
    }
}
