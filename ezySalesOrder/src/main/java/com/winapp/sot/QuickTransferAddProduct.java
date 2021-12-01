package com.winapp.sot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.winapp.adapter.CatSubAdapter;
import com.winapp.SFA.R;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.model.LocationGetSet;
import com.winapp.model.Locationadd;
import com.winapp.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.winapp.sot.SOTSummaryWebService.context;

public class QuickTransferAddProduct extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace,Constants {

    SlidingMenu menu;
    ImageButton searchIcon, custsearchIcon, printer,swipe,save;
    double screenInches;
    int orientation;
    RecyclerView recyclerView,recycler;
    List<LocationGetSet> locationList;
    String LocationCode = "", valid_url = "", cmpnyCode = "", userId = "";
    JSONObject jsonResponse;
    JSONArray jsonMainNode;
    LocationAdapter adapter;
    List<LocationGetSet> productStockList;
    HashMap<String, String> stockHashValue = new HashMap<String, String>();
    String stock_jsonString = null;
    JSONObject stock_jsonResponse;
    JSONArray stock_jsonMainNode,json_main_Node;
    StockCustomAdapter adapter_recycler;
    HashMap<String,String> pStockLocatin =  new HashMap<>();
    LinearLayout row2;
    Stockadapter adapter_stock;
    String element,code,lctn,total,summaryResult,lctnCode,category_jsonString;
    AlertDialog.Builder builder;
    EditText productSearch_ed;
    Button searchBtns;
    TextView login_lctn;
    private AutoCompleteTextView mCategoryAutoCompleteText;
    private JSONObject category_jsonResponse;
    private JSONArray category_jsonMainNode;
    private ArrayList<Product> mCategoryArr;
    private CatSubAdapter mCatSubAdapter;
    TextView mCategoryText;
    EditText mEditValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_menu);
        Mint.initAndStartSession(QuickTransferAddProduct.this, "29088aa0");
        setContentView(R.layout.activity_quick_transfer_add_product);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        View customNav = LayoutInflater.from(this).inflate(
                R.layout.slidemenu_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        txt.setText("Add Product");
        searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
        printer = (ImageButton) customNav.findViewById(R.id.printer);
        swipe = (ImageButton) customNav.findViewById(R.id.swipe);
        save = (ImageButton) customNav.findViewById(R.id.save);
        custsearchIcon = (ImageButton) customNav
                .findViewById(R.id.custcode_img);
        login_lctn = (TextView)customNav.findViewById(R.id.login_loctn);
        login_lctn.setVisibility(View.VISIBLE);
        lctnCode = SalesOrderSetGet.getLocationcode();
        login_lctn.setText(lctnCode);
        searchIcon.setVisibility(View.INVISIBLE);
        custsearchIcon.setVisibility(View.INVISIBLE);

        save.setVisibility(View.VISIBLE);
        SOTDatabase.init(context);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

    /*    recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true))*/;


        ab.setCustomView(customNav);
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

        screenInches = displayMetrics();
        Log.d("Display Inche", "" + screenInches);
        LocationCode = SalesOrderSetGet.getLocationcode();
        userId = SupplierSetterGetter.getUsername();
        cmpnyCode = SupplierSetterGetter.getCompanyCode();
        row2 = (LinearLayout)findViewById(R.id.row2);
        FWMSSettingsDatabase.init(QuickTransferAddProduct.this);
        SOTDatabase.init(QuickTransferAddProduct.this);
        valid_url = FWMSSettingsDatabase.getUrl();
        new SOTSummaryWebService(valid_url);
        productSearch_ed = (EditText)findViewById(R.id.productSearch_ed);
        searchBtns = (Button) findViewById(R.id.searchBtns);
        mCategoryAutoCompleteText = (AutoCompleteTextView)findViewById(R.id.categorySearch_act);
        mCategoryArr = new ArrayList<>();
        mCategoryText =(TextView)findViewById(R.id.category_tvs);


        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            Log.d("orientation","-->"+orientation);
            new ShowLocation().execute();
            new AsyncLocation().execute();
        } else {
            new ShowAllLocation().execute();
            new AsyncAllLocation("").execute();
        }

//        new getCategory().execute();

        mCategoryAutoCompleteText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEditValue = mCategoryAutoCompleteText;
                return false;
            }
        });




       /* if (screenInches > 7) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            orientation = QuickTransferAddProduct.this.getResources()
                    .getConfiguration().orientation;

            Log.d("orienation L", "oo " + orientation);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        swipe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                orientation = getResources().getConfiguration().orientation;
                Log.d("orientation",""+orientation+"-->"+Configuration.ORIENTATION_LANDSCAPE);

                if (screenInches > 7) {
                    if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
                        // orderFocQty = 0;
                        // orderExchQty = 0;
                        // mNetTotalItem.setText("0");
                        // Portrait
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        // ab.setBackgroundDrawable(getResources().getDrawable(
                        // R.drawable.home_bg));
//                        findViewById(R.id.verticalScrollView).setVisibility(
//                                View.VISIBLE);
//                        findViewById(R.id.sl_total_layout).setVisibility(
//                                View.VISIBLE);
//                        findViewById(R.id.label_total_layout).setVisibility(
//                                View.VISIBLE);
//                        findViewById(R.id.relativeLayout).setVisibility(
//                                View.GONE);

                        swipe.setVisibility(View.VISIBLE);
                    } else {
                        // Landscape
                        // ab.setBackgroundDrawable(getResources().getDrawable(
                        // R.drawable.header_bg));
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                        findViewById(R.id.verticalScrollView).setVisibility(
//                                View.GONE);
//                        findViewById(R.id.sl_total_layout).setVisibility(
//                                View.GONE);
//                        findViewById(R.id.label_total_layout).setVisibility(
//                                View.GONE);
//                        findViewById(R.id.relativeLayout).setVisibility(
//                                View.VISIBLE);

                        swipe.setVisibility(View.VISIBLE);

                    }
                }else{
                    if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
                        // orderFocQty = 0;
                        // orderExchQty = 0;
                        // mNetTotalItem.setText("0");
                        // Portrait
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        // ab.setBackgroundDrawable(getResources().getDrawable(
                        // R.drawable.home_bg));
//                        findViewById(R.id.verticalScrollView).setVisibility(
//                                View.VISIBLE);
//                        findViewById(R.id.sl_total_layout).setVisibility(
//                                View.VISIBLE);
//                        findViewById(R.id.label_total_layout).setVisibility(
//                                View.VISIBLE);
//                        findViewById(R.id.relativeLayout).setVisibility(
//                                View.GONE);

                        swipe.setVisibility(View.VISIBLE);
                    } else {
                        // Landscape
                        // ab.setBackgroundDrawable(getResources().getDrawable(
                        // R.drawable.header_bg));
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                        findViewById(R.id.verticalScrollView).setVisibility(
//                                View.GONE);
//                        findViewById(R.id.sl_total_layout).setVisibility(
//                                View.GONE);
//                        findViewById(R.id.label_total_layout).setVisibility(
//                                View.GONE);
//                        findViewById(R.id.relativeLayout).setVisibility(
//                                View.VISIBLE);

                        swipe.setVisibility(View.VISIBLE);

                    }

                }

            }
        });
    }*/

       save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               builder = new AlertDialog.Builder(QuickTransferAddProduct.this);
               //Setting message manually and performing action on button click
               builder.setMessage("Do you want to save?")
                       .setCancelable(false)
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               dialog.cancel();
                               Log.d("SaveProductStock","-->"+productStockList.size());
                               AsyncCallWSSummary task = new AsyncCallWSSummary(productStockList,locationList);
                               task.execute();
                           }
                       })
                       .setNegativeButton("No", new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               //  Action for 'NO' Button
                               dialog.cancel();

                           }
                       });
               //Creating dialog box
               AlertDialog alert = builder.create();
               //Setting the title manually
               alert.setTitle("Save Alert!!");
               alert.show();
           }
       });

        searchBtns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    String searchStr = productSearch_ed.getText().toString();
                    adapter_stock.getFilter().filter(searchStr);
                }else{
                    String searchStr = productSearch_ed.getText().toString();
                    adapter_recycler.getFilter().filter(searchStr);
                }
                String searchStr1 = mCategoryAutoCompleteText.getText().toString();
                Log.d("searchstr1",searchStr1);
                if(!searchStr1.matches("")){
                   new AsyncAllLocation(searchStr1).execute();
//                    adapter_recycler.getFilters().filter(searchStr1);
                }


//
            }
        });




        productSearch_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (productSearch_ed.getText().length() > 0) {

                    productSearch_ed.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_clear_btn, 0);
                    productSearch_ed.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(productSearch_ed) {
                        @Override
                        public boolean onDrawableClick() {
                            Log.d("TExtFilter","-->"+productSearch_ed.getText().toString());
                            productSearch_ed.setText("");
                            String searchStr = productSearch_ed.getText().toString();

                                int orientation = getResources().getConfiguration().orientation;
                                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                    adapter_stock.getFilter().filter(searchStr);
                                }else{
                                    adapter_recycler.getFilter().filter(searchStr);
                                }
                            return true;

                        }

                    });

                } else {
                    productSearch_ed.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });
    }
    public Double displayMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dens = dm.densityDpi;
        double wi = (double) width / (double) dens;
        double hi = (double) height / (double) dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        return screenInches = Math.sqrt(x + y);
    }


    @Override
    public void onListItemClick(String item) {
        menu.toggle();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QuickTransferAddProduct.this,QuickTransferHeader.class);
        startActivity(intent);
        finish();
    }

    private class ShowAllLocation extends AsyncTask<String,String,String>{
        HashMap<String, String> hashValue = new HashMap<String, String>();
        @Override
        protected void onPreExecute() {
            locationList = new ArrayList<>();
            locationList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {

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
                            String mainLocation = jsonObject.getString("isMainLocation");
                            Log.d("locationCodeCheck",locationCode);
                            LocationGetSet locationGetSet = new LocationGetSet();
                            if (mainLocation.matches("True")) {
                                LocationGetSet.setIsMainLocation(locationCode);
                            }
                            locationGetSet.setLocatn(locationCode);
                            locationList.add(locationGetSet);
							/*HashMap<String, String> locationhm = new HashMap<String, String>();
							locationhm.put(locationCode, locationName);
							locationArrHm.add(locationhm);*/
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
            LocationCode = SalesOrderSetGet.getLocationcode();
            TextView view1 = new TextView(QuickTransferAddProduct.this);
            view1.setWidth(60);
            view1.setGravity(Gravity.CENTER);
            view1.setText("SNo");
//            view1.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            TextView view2 = new TextView(QuickTransferAddProduct.this);
            view2.setWidth(250);
            view2.setMaxLines(2);
            view2.setGravity(Gravity.CENTER);
//            view2.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));

            view2.setText("Product Name");
            TextView view3 = new TextView(QuickTransferAddProduct.this);
            view3.setText("Available Qty");
//            view3.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 4f));
            view3.setWidth(200);

            row2.addView(view1);
            row2.addView(view2);
            view1.setGravity(Gravity.CENTER);
            view1.setTextColor(Color.WHITE);
//            view1.setPadding(10,10,10,10);
            view1.setTypeface(view1.getTypeface(), Typeface.BOLD);

            view2.setGravity(Gravity.CENTER);
            view2.setTextColor(Color.WHITE);
//            view2.setPadding(10,10,10,10);
            view2.setTypeface(view2.getTypeface(), Typeface.BOLD);

            view3.setGravity(Gravity.CENTER);
            view3.setTextColor(Color.WHITE);
//            view3.setPadding(10,10,10,10);
            view3.setTypeface(view3.getTypeface(), Typeface.BOLD);
            float sizes = (float)locationList.size();
            for(int i=0;i<locationList.size();i++){

                TextView view = new TextView(QuickTransferAddProduct.this);
                view.setGravity(Gravity.CENTER);
                view.setTextColor(Color.WHITE);
                view.setWidth(100);
//                view.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, sizes));
				view.setPadding(10,10,10,10);
                String Locations = locationList.get(i).getLocatn();
                Log.e("Locations", Locations);
                view.setText(locationList.get(i).getLocatn());
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                view1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                view2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                view3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                view.setTypeface(view.getTypeface(), Typeface.BOLD);
                row2.addView(view);
            }
            row2.addView(view3);



        }
    }

    private class LocationAdapter extends RecyclerView.Adapter<MyViewHolder> {
        Context context;
        List<LocationGetSet> list;
        LayoutInflater inflater;
        public LocationAdapter(Context context, List<LocationGetSet> locationList) {
            Log.d("listCheck",""+locationList.size());
            this.context=context;
            this.list=locationList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.location_txt, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            LocationGetSet locationGetSet = list.get(position);

            Log.d("listData","-->"+locationGetSet.getLocation_code().toString());
            String pos = locationGetSet.getLocation_code().toString();
            Log.d("pocCheck",pos);
            holder.loctnText.setText(pos);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        TextView loctnText;
        public MyViewHolder(View itemView) {
            super(itemView);
            loctnText = (TextView) itemView.findViewById(R.id.txt);
        }
    }

    private class AsyncAllLocation extends AsyncTask<Void, Void, Void>{
        String categoryCode;
        public AsyncAllLocation(String searchStr1) {
            Log.d("CategoryCodeChek","-->"+searchStr1);
            this.categoryCode = searchStr1;
        }

        @Override
        protected void onPreExecute() {
            productStockList = new ArrayList<>();
            pStockLocatin = new HashMap<>();
            pStockLocatin.clear();
            mCategoryArr.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            stockHashValue.put("CompanyCode",cmpnyCode);
            stockHashValue.put("CategoryCode",categoryCode);
//			stockHashValue.put("ProductCode","0000004");
            stock_jsonString = WebServiceClass.parameterService(stockHashValue, "fncGetAllLocationProductStock");
            try {
                stock_jsonResponse = new JSONObject(
                        stock_jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            stock_jsonMainNode = stock_jsonResponse.optJSONArray("JsonArray");

            try {
                /*********** Process each JSON Node ************/
                int lengthJsonArr = stock_jsonMainNode.length();

                for (int i = 0; i < lengthJsonArr; i++) {
                    /****** Get Object for each JSON node. ***********/
                    JSONObject jsonChildNode;

                    jsonChildNode = stock_jsonMainNode.getJSONObject(i);

                    String lctn_code = jsonChildNode.optString("LocationCode").toString();
                    json_main_Node = jsonChildNode.optJSONArray("LocationCode");

                    for(int j=0;j<json_main_Node.length();j++){
                        Log.d("json_main_Node","-->"+json_main_Node.get(j).toString());
                    }

                    String pro_Code = jsonChildNode.optString("ProductCode").toString();
                    String Total = jsonChildNode.optString("Total").toString();

                    String pro_name = jsonChildNode.optString("ProductName").toString();

                    String ppc = jsonChildNode.optString("PcsPerCarton").toString();

                    String categoryCode = jsonChildNode.optString("CategoryName").toString();

                    String categoryName = jsonChildNode.optString("CategoryName").toString();

                    Log.e("QuickTransferval", pro_Code+", "+Total);

                    Product product = new Product();

                    product.setCode(categoryCode);
                    product.setDescription(categoryName);

                    mCategoryArr.add(product);

                    LocationGetSet productStockGetSet = new LocationGetSet();
                    productStockGetSet.setSlno(String.valueOf(i + 1));
                    productStockGetSet.setLocation_code(json_main_Node);
                    productStockGetSet.setPro_code(pro_Code);
                    productStockGetSet.setTotal(Total);
                    productStockGetSet.setPro_name(pro_name);
                    productStockGetSet.setPcsperCarton(ppc);
                    productStockGetSet.setCategoryCode(categoryCode);
                    productStockGetSet.setCategoryName(categoryName);
                    productStockList.add(productStockGetSet);
                    Log.d("productStockListCheck", "" + productStockList.size());
                }

            }catch(Exception e){
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter_recycler = new StockCustomAdapter(QuickTransferAddProduct.this,productStockList,locationList,mCategoryArr);
            recycler.setAdapter(adapter_recycler);

            mCatSubAdapter = new CatSubAdapter(QuickTransferAddProduct.this,
                    R.layout.autotext_listitem, R.id.textView_titlevalue,
                    mCategoryArr);
            mCategoryAutoCompleteText.setAdapter(mCatSubAdapter);




        }
    }


    public class StockCustomAdapter extends RecyclerView.Adapter<CheckViewHolder> implements Filterable{
        Context context;
        List<LocationGetSet> lists;
        LayoutInflater inflater;
        HashMap<String,String> locatnwithQty;
        List<LocationGetSet> lctnlist;
        LocationShowAdapter adapter;
        double count_check ;
        boolean flag= true;
        Filter sampleFilter,catFilter;
        List<LocationGetSet> filterLists = new ArrayList<>();
        List<LocationGetSet> filterList = new ArrayList<>();
        double displaydo;
        private String filterType;
        ArrayList<Product> categoryArr;
        public StockCustomAdapter(Context productStockActivity, List<LocationGetSet> productStockList, List<LocationGetSet> locationList, ArrayList<Product> mCategoryArr) {
            this.context= productStockActivity;
            this.lists = productStockList;
            this.lctnlist = locationList;
            this.filterLists = productStockList;
            this.categoryArr = mCategoryArr;
            this.filterList = productStockList;
            Log.d("ListCountCheck",""+lists.size()+"lctnlist :"+lctnlist.size()+"-->"+filterList.size());
        }

        @Override
        public CheckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.stock_adapters, null);
            return new CheckViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CheckViewHolder holder, final int position) {

            final LocationGetSet productStockGetSet = lists.get(position);
            final JSONArray json_node = productStockGetSet.getLocation_code();
            LocationCode = SalesOrderSetGet.getLocationcode();

            TextView view1 = new TextView(context);
            view1.setWidth(60);
//            view1.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            if(view1.getText().toString().matches("")) {
                view1.setText(productStockGetSet.getSlno());
            } else {

            }
            TextView view2 = new TextView(context);
            view2.setWidth(250);
//            view2.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
            if(view2.getText().toString().matches("")) {
                view2.setText(productStockGetSet.getPro_name());
            } else {

            }

            holder.rows2.addView(view1);
            holder.rows2.addView(view2);
            view1.setGravity(Gravity.CENTER);
            view1.setTextColor(Color.BLACK);
            view1.setPadding(10,10,10,10);

            view2.setGravity(Gravity.LEFT);
            view2.setSingleLine(true);
            view2.setTextColor(Color.BLACK);
            view2.setPadding(10,10,10,10);


            final TextView view3 = new TextView(context);
            view3.setWidth(200);
            if(view3.getText().toString().matches("")) {
                view3.setText(productStockGetSet.getTotal());
            } else {


            }

            for(int j = 0;j<json_node.length();j++){

                final EditText view = new EditText(context);
                view.setGravity(Gravity.CENTER);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
                }
                view.setId(j);
                view.setTextColor(Color.BLACK);
                view.setWidth(100);
                view.setHeight(120);
                view.setPadding(10,10,10,10);
                if(view.getId()==0){
                    view.setFocusable(false);
                    view.setEnabled(false);
                    view.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                    view.setClickable(false);
                }

                view.setInputType(InputType.TYPE_CLASS_PHONE);
                float lctns = lctnlist.size();
                for(int i=0;i<lctnlist.size();i++){

                    Log.d("lctnlistChck","-->"+lctnlist.get(i).getLocatn());
                    if(LocationGetSet.getIsMainLocation().matches(lctnlist.get(i).getLocatn())){
                        Log.d("checkMainValues","-->"+lctnlist.get(i).getLocatn());
                    }

                }
                try {
                    view.setText(json_node.get(j).toString());
                    Log.d("checkAdaptervalues","--?"+json_node.get(j).toString()+"productCode:"+productStockGetSet.getPro_code());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                view.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, lctns));
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                view1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                view2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

                //view.setTypeface(view.getTypeface(), Typeface.BOLD);
                holder.rows2.addView(view);

                final int finalJ = j;
               view.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            Log.d("getZero", "-->" + json_node.get(0).toString() + "-->" + view.getId());
                            String jsonnode = json_node.get(0).toString();
                            double jsonnodedo = Double.parseDouble(jsonnode);
                            if (finalJ != 0) {
                                Log.d("getIdPosition", "-->" + view.getId() + "==" + finalJ);
                                if (view.getId() == finalJ) {
                                    double check=0.0;
                                    for(int j = 1;j<json_node.length();j++){
                                        if(j!=finalJ){
                                            check += json_node.getDouble(j);
                                        }else{
                                          check += Double.parseDouble(view.getText().toString());
                                          json_node.put(j,Double.parseDouble(view.getText().toString()));
                                        }
                                    }

                                    productStockGetSet.setLocation_code(json_node);
                                    /*try {
                                       *//* if (!json_node.get(finalJ).toString().matches("")) {
                                            if (!view.getText().toString().matches("")) {
                                                Log.d("checkviewvalues", view.getText().toString() + "," + json_node.get(finalJ).toString());
                                                count_check = count_check + Double.parseDouble(view.getText().toString());
                                                Log.d("countCheckvalues", "" + count_check);
                                                Locationadd.setLocation(view.getText().toString());
                                            } else {
                                                flag = false;
                                                view.setText("0");
                                                count_check = count_check - Double.parseDouble(Locationadd.getLocation());
                                                Log.d("countCheckminusvalues", "" + count_check);
                                            }
                                        }*//*

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }*/
                                    displaydo = jsonnodedo - check;
                                    view3.setText("" + displaydo);
//                                    view3.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 4f));
                                    productStockGetSet.setTotal(""+displaydo);
                                    Log.e("displaydoval", "" + displaydo);

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });



            }

            view3.setGravity(Gravity.CENTER);
            view3.setTextColor(Color.BLACK);
            view3.setPadding(10,10,10,10);
            //view3.setTypeface(view3.getTypeface(), Typeface.BOLD);
            view3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            holder.rows2.addView(view3);
            holder.setIsRecyclable(false);

        }

        @Override
        public int getItemCount() {
            return lists.size();
        }

        @Override
        public Filter getFilter() {
            Log.d("startsExecuting","--?"+"");
            if (sampleFilter == null)
                sampleFilter = new SamplesFilter();

            return sampleFilter;

        }


        public Filter getFilters() {
            if (catFilter == null)
                catFilter = new CategoryFilter();

            return catFilter;
        }

        private class SamplesFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d("constraint","-->"+constraint);
                FilterResults results = new FilterResults();
                // We implement here the filter logic
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = filterLists;
                    results.count = filterLists.size();
                } else {

                    // We perform filtering operation
                    ArrayList<LocationGetSet> FilteredArrList = new ArrayList<LocationGetSet>();

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < filterLists.size(); i++) {
                        String data = filterLists.get(i).getPro_name();
                        String code = filterLists.get(i).getPro_code();
//						String barcode = mOriginalValues.get(i).getBarcode();
                        if (data.toLowerCase().contains(constraint.toString()) || code.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(new LocationGetSet(
                                    filterLists.get(i).getPro_name(),
                                    filterLists.get(i).getPro_code(),
                                    filterLists.get(i)
                                            .getSlno(),
                                    filterLists.get(i)
                                            .getLocatn(),
                                    filterLists.get(i).getPcsperCarton(),
                                    filterLists.get(i).getLocation_code(),
                                    filterLists.get(i).getTotal()));
                        }


                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;

                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results.count == 0) {
                    lists = (ArrayList<LocationGetSet>) results.values;
                    adapter_recycler.notifyDataSetChanged();
                } else {
                    lists = (ArrayList<LocationGetSet>) results.values;
                    adapter_recycler.notifyDataSetChanged();
                }
            }
        }

        private class CategoryFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d("constraintCat","-->"+constraint);
                FilterResults results = new FilterResults();
                // We implement here the filter logic
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = filterList;
                    results.count = filterList.size();
                } else {

                    // We perform filtering operation
                    ArrayList<LocationGetSet> FilteredArrList = new ArrayList<LocationGetSet>();

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < filterList.size(); i++) {
                        String data = filterList.get(i).getCategoryCode();
                        String code = filterList.get(i).getCategoryName();

                        if (data.toLowerCase().contains(constraint.toString()) || code.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(new LocationGetSet(
                                    filterList.get(i).getCategoryCode(),
                                    filterList.get(i).getCategoryName()));
                        }


                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;

                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0) {
                    lists = (ArrayList<LocationGetSet>) results.values;
                    adapter_recycler.notifyDataSetChanged();
                } else {
                    lists = (ArrayList<LocationGetSet>) results.values;
                    adapter_recycler.notifyDataSetChanged();
                }
            }
        }
    }

    private class CheckViewHolder extends RecyclerView.ViewHolder {
        TextView slno,pname,total,locationCode;
        RecyclerView recyclerview;
        LinearLayout rows2;
        public CheckViewHolder(View itemView) {
            super(itemView);
            rows2 = (LinearLayout)itemView.findViewById(R.id.rows2);
			/*slno = (TextView) itemView.findViewById(R.id.ss_prodcode);
			pname = (TextView) itemView.findViewById(R.id.ss_name);
			total = (TextView) itemView.findViewById(R.id.ss_c_qty);
			locationCode = (TextView)itemView.findViewById(R.id.locationCode);
			recyclerview = (RecyclerView) itemView.findViewById(R.id.recyclerView);*/
        }
    }

    private class LocationShowAdapter extends RecyclerView.Adapter<OurViewHolder>{
        Context context;
        List<LocationGetSet> lctnlist;
        LayoutInflater inflater;
        String productCode;
        public LocationShowAdapter(Context context, List<LocationGetSet> locationList, String pro_code) {
            this.context=context;
            this.lctnlist=locationList;
            this.productCode= pro_code;
            Log.d("locationListChck","-->"+lctnlist.size()+"-->"+productCode);
        }

        @Override
        public OurViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.location_txts, null);
            return new OurViewHolder(view);
        }

        @Override
        public void onBindViewHolder(OurViewHolder holder, int position) {
            final LocationGetSet locationGetSet = lctnlist.get(position);
            Log.d("listDataChecking","-->"+locationGetSet.getLocation_code());
            String pos = locationGetSet.getLocation_code().toString();
            Log.d("pocCheckchecking",pos);
            holder.loctnText.setText(pos);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QuickDialogFragment dialog = QuickDialogFragment.newInstatnt(locationGetSet.getLocatn());
                    dialog.show(getFragmentManager(),"dialogFragment");
                }
            });
        }

        @Override
        public int getItemCount() {
            return lctnlist.size();
        }
    }

    private class OurViewHolder extends RecyclerView.ViewHolder {
        TextView loctnText;
        public OurViewHolder(View itemView) {
            super(itemView);
            loctnText = (TextView) itemView.findViewById(R.id.txts);
        }
    }

    private class AsyncLocation extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            productStockList = new ArrayList<>();
            pStockLocatin = new HashMap<>();
            pStockLocatin.clear();


        }

        @Override
        protected Void doInBackground(Void... voids) {
            stockHashValue.put("CompanyCode",cmpnyCode);
//            stockHashValue.put("TopCount","3");
//			stockHashValue.put("ProductCode","0000004");
            stock_jsonString = WebServiceClass.parameterService(
                    stockHashValue, "fncGetAllLocationProductStock");
            try {
                stock_jsonResponse = new JSONObject(
                        stock_jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            stock_jsonMainNode = stock_jsonResponse.optJSONArray("JsonArray");

            try {
                /*********** Process each JSON Node ************/
                int lengthJsonArr = stock_jsonMainNode.length();

                for (int i = 0; i < lengthJsonArr; i++) {
                    /****** Get Object for each JSON node. ***********/
                    JSONObject jsonChildNode;

                    jsonChildNode = stock_jsonMainNode.getJSONObject(i);

                    String lctn_code = jsonChildNode.optString("LocationCode")
                            .toString();
                    json_main_Node = jsonChildNode.optJSONArray("LocationCode");

                    for(int j=0;j<json_main_Node.length();j++){
                        Log.d("json_main_Node","-->"+json_main_Node.get(j).toString());
                    }

                    String pro_Code = jsonChildNode.optString("ProductCode")
                            .toString();

                    String Total = jsonChildNode.optString("Total")
                            .toString();

                    String pro_name = jsonChildNode.optString("ProductName").toString();

                    String ppc = jsonChildNode.optString("PcsPerCarton").toString();

                    String categoryCode = jsonChildNode.optString("CategoryName").toString();

                    String categoryName = jsonChildNode.optString("CategoryName").toString();


                    LocationGetSet productStockGetSet = new LocationGetSet();
                    productStockGetSet.setSlno(String.valueOf(i + 1));
                    productStockGetSet.setLocation_code(json_main_Node);
                    productStockGetSet.setPro_code(pro_Code);
                    productStockGetSet.setTotal(Total);
                    productStockGetSet.setPro_name(pro_name);
                    productStockGetSet.setPcsperCarton(ppc);
                    productStockGetSet.setCategoryCode(categoryCode);
                    productStockGetSet.setCategoryName(categoryName);
                    productStockList.add(productStockGetSet);
                    Log.d("productStockListCheck", "" + productStockList.size());


                }

            }catch(Exception e){
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter_stock = new Stockadapter(QuickTransferAddProduct.this,productStockList);
            recycler.setAdapter(adapter_stock);
        }
    }

    private class Stockadapter extends RecyclerView.Adapter<HViewHolder> implements Filterable{
        Context context;
        List<LocationGetSet> lists;
        LayoutInflater inflater;
        JSONArray json_node;
        double displaydo;
        List<LocationGetSet> filterLists = new ArrayList<>();
        private Filter sampleFilter;
        public Stockadapter(Context productStockActivity, List<LocationGetSet> productStockList) {
            this.context = productStockActivity;
            this.lists = productStockList;
            this.filterLists = productStockList;
        }

        @Override
        public HViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.stock_adapters, null);
            return new HViewHolder(view);		}

        @Override
        public void onBindViewHolder(HViewHolder holder, int position) {
            final LocationGetSet productStockGetSet = lists.get(position);
            final JSONArray json_node = productStockGetSet.getLocation_code();
            LocationCode = SalesOrderSetGet.getLocationcode();

            TextView view1 = new TextView(context);
            view1.setWidth(100);

            if(view1.getText().toString().matches("")) {
                view1.setText(productStockGetSet.getSlno());
            } else {

            }
            TextView view2 = new TextView(context);
            view2.setWidth(350);
            view2.setHeight(120);

            if(view2.getText().toString().matches("")) {
                view2.setText(productStockGetSet.getPro_name());
            } else {

            }

            holder.rows2.addView(view1);
            holder.rows2.addView(view2);
            view1.setGravity(Gravity.CENTER);
            view1.setTextColor(Color.BLACK);
            view1.setPadding(10,10,10,10);

            view2.setGravity(Gravity.LEFT);
            view2.setSingleLine(true);
            view2.setTextColor(Color.BLACK);
            view2.setPadding(10,10,10,10);


            final TextView view3 = new TextView(context);

            view3.setWidth(300);

            if(view3.getText().toString().matches("")) {
                view3.setText(productStockGetSet.getTotal());
            } else {


            }

            for(int j = 0;j<json_node.length();j++){

                final EditText view = new EditText(context);
                view.setGravity(Gravity.CENTER);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(getResources().getDrawable(R.drawable.edittext_bg));
                }
                view.setId(j);
                view.setTextColor(Color.BLACK);
                view.setWidth(160);
                view.setHeight(180);
                if(view.getId()==0){
                    view.setFocusable(false);
                    view.setEnabled(false);
                    view.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                    view.setClickable(false);
                }

                view.setInputType(InputType.TYPE_CLASS_PHONE);

//					view.setPadding(10,10,10,10);
                /*for(int i=0;i<lctnlist.size();i++){
                    Log.d("lctnlistChck","-->"+lctnlist.get(i).getLocatn());
                    if(LocationGetSet.getIsMainLocation().matches(lctnlist.get(i).getLocatn())){
                        Log.d("checkMainValues","-->"+lctnlist.get(i).getLocatn());
                    }

                }*/
                try {
                    view.setText(json_node.get(j).toString());
                    Log.d("checkAdaptervalues","--?"+json_node.get(j).toString()+"productCode:"+productStockGetSet.getPro_code());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                view1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                view2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

                //view.setTypeface(view.getTypeface(), Typeface.BOLD);
                holder.rows2.addView(view);

                final int finalJ = j;
                view.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            Log.d("getZero", "-->" + json_node.get(0).toString() + "-->" + view.getId());
                            String jsonnode = json_node.get(0).toString();
                            double jsonnodedo = Double.parseDouble(jsonnode);
                            if (finalJ != 0) {
                                Log.d("getIdPosition", "-->" + view.getId() + "==" + finalJ);
                                if (view.getId() == finalJ) {
                                    double check=0.0;
                                    for(int j = 1;j<json_node.length();j++){
                                        if(j!=finalJ){
                                            check += json_node.getDouble(j);
                                        }else{
                                            check += Double.parseDouble(view.getText().toString());
                                            json_node.put(j,Double.parseDouble(view.getText().toString()));
                                        }
                                    }

                                    productStockGetSet.setLocation_code(json_node);

                                    displaydo = jsonnodedo - check;
                                    view3.setText("" + displaydo);
                                    productStockGetSet.setTotal(""+displaydo);
                                    Log.e("displaydoval", "" + displaydo);

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });



            }

            view3.setGravity(Gravity.CENTER);
            view3.setTextColor(Color.BLACK);
            view3.setPadding(10,10,10,10);
            //view3.setTypeface(view3.getTypeface(), Typeface.BOLD);
            view3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            holder.rows2.addView(view3);
            holder.setIsRecyclable(false);
        }


        @Override
        public int getItemCount() {
            return lists.size();
        }

       @Override
        public Filter getFilter() {
            Log.d("startsExecuting","--?"+"");
            if (sampleFilter == null)
                sampleFilter = new SamplesFilter();

            return sampleFilter;

        }

        private class SamplesFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d("constraint","-->"+constraint);
                FilterResults results = new FilterResults();
                // We implement here the filter logic
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = filterLists;
                    results.count = filterLists.size();
                } else {
                    // We perform filtering operation
                    ArrayList<LocationGetSet> FilteredArrList = new ArrayList<LocationGetSet>();
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < filterLists.size(); i++) {
                        String data = filterLists.get(i).getPro_name();
                        String code = filterLists.get(i).getPro_code();
//						String barcode = mOriginalValues.get(i).getBarcode();
                        if (data.toLowerCase().contains(constraint.toString()) || code.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(new LocationGetSet(
                                    filterLists.get(i).getPro_name(),
                                    filterLists.get(i).getPro_code(),
                                    filterLists.get(i)
                                            .getSlno(),
                                    filterLists.get(i)
                                            .getLocatn(),
                                    filterLists.get(i).getPcsperCarton(),
                                    filterLists.get(i).getLocation_code(),
                                    filterLists.get(i).getTotal()));
                        }


                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;

                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results.count == 0) {
                    lists = (ArrayList<LocationGetSet>) results.values;
                    adapter_stock.notifyDataSetChanged();
                } else {
                    lists = (ArrayList<LocationGetSet>) results.values;
                    adapter_stock.notifyDataSetChanged();
                }
            }
        }
    }

    private class HViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rows2;
        public HViewHolder(View itemView) {
            super(itemView);
            rows2 = (LinearLayout)itemView.findViewById(R.id.rows2);
        }
    }

    private class ShowLocation extends AsyncTask<String,String,String> {
        HashMap<String, String> hashValue = new HashMap<String, String>();
        @Override
        protected void onPreExecute() {
            locationList = new ArrayList<>();
            locationList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {

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
                            String mainLocation = jsonObject.getString("isMainLocation");
                            Log.d("locationCodeCheck",locationCode);
                            LocationGetSet locationGetSet = new LocationGetSet();
                            if (mainLocation.matches("True")) {
                                LocationGetSet.setIsMainLocation(locationCode);
                            }
                            locationGetSet.setLocatn(locationCode);
                            locationList.add(locationGetSet);
							/*HashMap<String, String> locationhm = new HashMap<String, String>();
							locationhm.put(locationCode, locationName);
							locationArrHm.add(locationhm);*/
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
            LocationCode = SalesOrderSetGet.getLocationcode();
            TextView view1 = new TextView(QuickTransferAddProduct.this);
            view1.setWidth(100);
            view1.setGravity(Gravity.CENTER);
            view1.setText("SNo");
            TextView view2 = new TextView(QuickTransferAddProduct.this);
            view2.setWidth(300);
            view2.setGravity(Gravity.CENTER);
            view2.setText("Product Name");
            TextView view3 = new TextView(QuickTransferAddProduct.this);
            view3.setText("Available Qty");
            row2.addView(view1);
            row2.addView(view2);
            view1.setGravity(Gravity.CENTER);
            view1.setTextColor(Color.WHITE);
            view1.setPadding(10,10,10,10);
            view1.setTypeface(view1.getTypeface(), Typeface.BOLD);

            view2.setGravity(Gravity.CENTER);
            view2.setTextColor(Color.WHITE);
            view2.setPadding(10,10,10,10);
            view2.setTypeface(view2.getTypeface(), Typeface.BOLD);

            view3.setGravity(Gravity.CENTER);
            view3.setTextColor(Color.WHITE);
            view3.setPadding(10,10,10,10);
            view3.setTypeface(view3.getTypeface(), Typeface.BOLD);
            view3.setWidth(320);
//            row2.setWeightSum(3f);
            for(int i=0;i<locationList.size();i++){
                TextView view = new TextView(QuickTransferAddProduct.this);
                view.setGravity(Gravity.CENTER);
                view.setTextColor(Color.WHITE);
                view.setWidth(180);
				view.setPadding(10,10,10,10);
                view.setText(locationList.get(i).getLocatn());
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                view1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                view2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                view3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                view.setTypeface(view.getTypeface(), Typeface.BOLD);
                row2.addView(view);
            }
            row2.addView(view3);


        }
    }

    private class AsyncCallWSSummary extends AsyncTask<Void, Void, Void>{
        List<LocationGetSet> stockList;
        List<LocationGetSet> lctnList;
        public AsyncCallWSSummary(List<LocationGetSet> productStockList, List<LocationGetSet> locationList) {
            this.stockList=productStockList;
            this.lctnList = locationList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            /*for(int i=0;i<stockList.size();i++){
                JSONArray array = stockList.get(i).getLocation_code();
                Log.d("productStockListSize","-->"+array.length());
                code  = stockList.get(i).getPro_code();
                total = stockList.get(i).getTotal();
                for (int j = 0; j < array.length(); j++) {
                    try {
                        element = array.get(j).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("elemntChecking", "-->" + element);
                    for (int k = 0; k <= j; k++) {
                        lctn = lctnList.get(k).getLocatn();
                    }

                    Log.d("checkCredentials","-->"+code+"lctn->"+lctn+"elements :"+element+"total:"+total);
                }
            }*/


            try {
                summaryResult = SOTSummaryWebService.summaryQuickTransfer("fncSaveQuickTransfer",stockList,lctnList,LocationGetSet.getIsMainLocation());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (summaryResult.matches("")) {

                Toast.makeText(QuickTransferAddProduct.this, "Failed",
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(QuickTransferAddProduct.this, "Saved Successfully!!",
                        Toast.LENGTH_SHORT).show();
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    // In landscape
                    Log.d("orientation","-->"+orientation);
//                    new ShowLocation().execute();
                    new AsyncLocation().execute();
                } else {
//                    new ShowAllLocation().execute();
                    new AsyncAllLocation("").execute();
                }

            }
        }
    }

    private class getCategory extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            category_jsonString = WebServiceClass
                    .URLService("fncGetCategory");
            try {
                category_jsonResponse = new JSONObject(category_jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            category_jsonMainNode = category_jsonResponse
                    .optJSONArray("JsonArray");

            /*********** Process each JSON Node ************/
            int lengJsonArr = category_jsonMainNode.length();
            for (int i = 0; i < lengJsonArr; i++) {
                /****** Get Object for each JSON node. ***********/
                JSONObject jsonChildNode = null;
                try {
                    jsonChildNode = category_jsonMainNode
                            .getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Product product = new Product();
                String categorycode = jsonChildNode.optString(
                        "CategoryCode").toString();

                String description = jsonChildNode.optString("Description")
                        .toString();
                product.setCode(categorycode);
                product.setDescription(description);

                mCategoryArr.add(product);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCatSubAdapter = new CatSubAdapter(QuickTransferAddProduct.this,
                    R.layout.autotext_listitem, R.id.textView_titlevalue,
                    mCategoryArr);
            mCategoryAutoCompleteText.setAdapter(mCatSubAdapter);


        }
    }
}
