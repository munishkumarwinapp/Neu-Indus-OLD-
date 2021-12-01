package com.winapp.sotdetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.sot.MerchandiseHeader;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.sot.WebServiceClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ProductAnalysisActivity extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace {

    SlidingMenu menu;
    TextView product_Title;
    AutoCompleteTextView actv_search;
    ImageButton newprodbutton, product_search, printIcon, mClose,filter;
    private CheckBox mCheckBox;
    ListView product_list;
    ProductCustomAdapter Adapter;
    ArrayList<ProductStockGetSet> ProductListArray;
    LinearLayout spinnerLayout;
    ProgressBar progressBar;
    LinearLayout productanalysis_layout;
    HashMap<String, String> producthashValue = new HashMap<String, String>();
    String product_stock_jsonString = null;
    JSONObject product_stock_jsonResponse;
    JSONArray product_stock_jsonMainNode;
    String LocationCode = "", valid_url = "", cmpnyCode = "", userId = "",
            saveResult = "",sServerDate = "",fromdate = "",todate = "",loadtype = "PR",costtype = "Unit Cost",supplier_code = "",sortby = "Sales Quantity";
    private FilterAnalysis filterAnalysis;
    private TextView total_profit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_menu);
        setContentView(R.layout.activity_product_analysis);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);

        View customNav = LayoutInflater.from(this).inflate(
                R.layout.productstock_title, null);
        product_Title = (TextView) customNav.findViewById(R.id.product_Title);
        actv_search = (AutoCompleteTextView) customNav
                .findViewById(R.id.auto_edit_search);
        product_Title.setText("Product Analysis");
        newprodbutton = (ImageButton) customNav
                .findViewById(R.id.newprodbutton);
        mCheckBox = (CheckBox) findViewById(R.id.checkBox1);
        // ed_product = (ImageButton) customNav.findViewById(R.id.ed_product);
        // //unused
        product_search = (ImageButton) customNav.findViewById(R.id.search);
        printIcon = (ImageButton) customNav.findViewById(R.id.print_icon);
        mClose = (ImageButton) customNav.findViewById(R.id.close);
        filter = (ImageButton) customNav.findViewById(R.id.filter);
        total_profit = (TextView) findViewById(R.id.total_profit);
        newprodbutton.setVisibility(View.GONE);
        product_search.setVisibility(View.GONE);
        filter.setVisibility(View.VISIBLE);
        getSupportActionBar().setCustomView(customNav);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

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

        FWMSSettingsDatabase.init(ProductAnalysisActivity.this);
        SOTDatabase.init(ProductAnalysisActivity.this);
        valid_url = FWMSSettingsDatabase.getUrl();
        new WebServiceClass(valid_url);

        LocationCode = SalesOrderSetGet.getLocationcode();
        cmpnyCode = SupplierSetterGetter.getCompanyCode();
        sServerDate = SalesOrderSetGet.getServerDate();
        fromdate = sServerDate;
        todate = sServerDate;
        filterAnalysis = new FilterAnalysis(ProductAnalysisActivity.this);

        product_list = (ListView) findViewById(R.id.product_list);
        productanalysis_layout = (LinearLayout) findViewById(R.id.productanalysis_layout);
        ProductListArray = new ArrayList<ProductStockGetSet>();

        ProductAnalysisAsyncCall Productservice = new ProductAnalysisAsyncCall();
        Productservice.execute();

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterAnalysis.filterDialog();

                filterAnalysis.OnFilterCompletionListener(new FilterAnalysis.OnFilterCompletionListener() {

                    @Override
                    public void onFilterCompleted(String load_type,
                                                  String cost_type,String from_date,String to_date,String sort_by,String suppliercode) {

                       /* catStr = category;
                        subCatStr = subcategory;*/
                        fromdate = from_date;
                        todate = to_date;
                        costtype = cost_type;
                        supplier_code = suppliercode;
                        sortby = sort_by;
                        if(load_type.equalsIgnoreCase("By Supplier Product")){
                            loadtype ="PR";
                        }else if(load_type.equalsIgnoreCase("By Supplier Purchase")){
                            loadtype ="PU";
                        }else if(load_type.equalsIgnoreCase("By Purchase Invoice No")){
                            loadtype ="GRA";
                        }else if(load_type.equalsIgnoreCase("Sales")){
                            loadtype ="INV";
                        }else if(load_type.equalsIgnoreCase("All the products")){
                            loadtype ="ALL";
                        }

                        ProductAnalysisAsyncCall Productservice = new ProductAnalysisAsyncCall();
                        Productservice.execute();

                        Log.d("loadtype", "----->" + loadtype);
                        Log.d("costtype", "-->" + costtype);
                        Log.d("fromdate", "----->" + fromdate);
                        Log.d("todate", "-->" + todate);
                        Log.d("sortby", "-->" + sortby);
                        Log.d("suppliercode", "-->" + supplier_code);

                    }
                });
            }
        });
    }

    private class ProductAnalysisAsyncCall extends AsyncTask<Void, Void, Void> {
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {

            ProductListArray.clear();
            spinnerLayout = new LinearLayout(ProductAnalysisActivity.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            addContentView(spinnerLayout, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(productanalysis_layout, false);
            progressBar = new ProgressBar(ProductAnalysisActivity.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    R.drawable.greenprogress));
            spinnerLayout.addView(progressBar);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            producthashValue.put("CompanyCode", cmpnyCode);
            producthashValue.put("SupplierCode", supplier_code);
            producthashValue.put("FromDate", fromdate);
            producthashValue.put("ToDate", todate);
            producthashValue.put("LoadType", loadtype);
            producthashValue.put("LocationCode", LocationCode);
            product_stock_jsonString = WebServiceClass.parameterService(
                    producthashValue, "fncGetPurchaseSalesComparision");

            try {
                product_stock_jsonResponse = new JSONObject(
                        product_stock_jsonString);
                product_stock_jsonMainNode = product_stock_jsonResponse
                        .optJSONArray("JsonArray");

            } catch (JSONException e) {

                e.printStackTrace();
            } catch (Exception e) {

            }

            try {
                /*********** Process each JSON Node ************/
                int lengthJsonArr = product_stock_jsonMainNode.length();
                for (int i = 0; i < lengthJsonArr; i++) {
                    /****** Get Object for each JSON node. ***********/
                    JSONObject jsonChildNode;

                    jsonChildNode = product_stock_jsonMainNode.getJSONObject(i);

                    String pro_Name = jsonChildNode.optString("ProductName")
                            .toString();
                    String pro_Code = jsonChildNode.optString("ProductCode")
                            .toString();
                    String purchase_Quantity = jsonChildNode.optString("PurchaseQty")
                            .toString();
                    String pro_PcsPerCarton = jsonChildNode.optString(
                            "PcsPerCarton").toString();
                    String AveragePurchaseQty = jsonChildNode.optString(
                            "AveragePurchaseQty").toString();
                    String PurchaseAmount = jsonChildNode.optString(
                            "PurchaseAmount").toString();
                    String AveragePurchaseCost = jsonChildNode.optString(
                            "AveragePurchaseCost").toString();
                    String SalesQty = jsonChildNode.optString(
                            "SalesQty").toString();
                    String AverageSalesQty = jsonChildNode.optString(
                            "AverageSalesQty").toString();
                    String SalesAmount = jsonChildNode.optString(
                            "SalesAmount").toString();
                    String ProfitAmount = jsonChildNode.optString(
                            "ProfitAmount").toString();
                    String OutletPrice = jsonChildNode.optString(
                            "OutletPrice").toString();
                    String BalanceQty = jsonChildNode.optString(
                            "BalanceQty").toString();
                    String AverageCost = jsonChildNode.optString(
                            "AverageCost").toString();
                    String UnitCost = jsonChildNode.optString(
                            "UnitCost").toString();

                    ProductStockGetSet pro_set = new ProductStockGetSet();
                    pro_set.setProduct_Name(pro_Name);
                    pro_set.setProduct_Code(pro_Code);
                    pro_set.setPurchase_Quantity(purchase_Quantity);
                    pro_set.setProduct_PcsPerCarton(pro_PcsPerCarton);
                    pro_set.setAveragePurchaseQty(AveragePurchaseQty);
                    pro_set.setPurchaseAmount(PurchaseAmount);
                    pro_set.setAveragePurchaseCost(AveragePurchaseCost);
                    pro_set.setSalesQty(SalesQty);
                    pro_set.setAverageSalesQty(AverageSalesQty);
                    pro_set.setSalesAmount(SalesAmount);
                    pro_set.setProfitAmount(ProfitAmount);
                    pro_set.setOutletPrice(OutletPrice);
                    pro_set.setBalanceQty(BalanceQty);
                    pro_set.setAverageCost(AverageCost);
                    pro_set.setUnitCost(UnitCost);


                    ProductListArray.add(pro_set);

                }
            } catch (JSONException e) {

                e.printStackTrace();
            } catch (Exception e) {

            }

            /******* Fetch node values **********/

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Log.d("ProductListArray Result", ""+ProductListArray.size());

            ProductAnalysisArrayCall Productservice = new ProductAnalysisArrayCall();
            Productservice.execute();
        }
    }

    private class ProductAnalysisArrayCall extends AsyncTask<Void, Void, Void> {
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            /******* Fetch node values **********/
            try{

            for(ProductStockGetSet obj : ProductListArray){
                String salesqty = obj.getSalesQty();
                if(salesqty!=null && !salesqty.isEmpty()){

                }else{
                    salesqty = "0";
                }

                String balanceqty = obj.getBalanceQty();
                if(balanceqty!=null && !balanceqty.isEmpty()){

                }else{
                    balanceqty = "0";
                }
                double balance_qty = Double.parseDouble(balanceqty);
                if(balance_qty < 0){
                    balance_qty =0;
                }
                obj.setBalanceQty(String.valueOf(balance_qty));

                String purchaseqty = obj.getPurchase_Quantity();
                if(purchaseqty!=null && !purchaseqty.isEmpty()){

                }else{
                    purchaseqty = "0";
                }
                double purchase_qty = Double.parseDouble(purchaseqty);
                if(purchase_qty < 0){
                    purchase_qty =0;
                }
                obj.setPurchase_Quantity(String.valueOf(purchase_qty));

                String unitcost = obj.getUnitCost();
                if(unitcost!=null && !unitcost.isEmpty()){

                }else{
                    unitcost = "0";
                }

                String averageCost = obj.getAverageCost();
                if(averageCost!=null && !averageCost.isEmpty()){

                }else{
                    averageCost = "0";
                }

                String salesAmount = obj.getSalesAmount();
                if(salesAmount!=null && !salesAmount.isEmpty()){

                }else{
                    salesAmount = "0";
                }

                String salesprice = obj.getOutletPrice();
                if(salesprice!=null && !salesprice.isEmpty()){

                }else{
                    salesprice = "0";
                }

                String averagePurchaseCost = obj.getAveragePurchaseCost();
                if(averagePurchaseCost!=null && !averagePurchaseCost.isEmpty()){

                }else{
                    averagePurchaseCost = "0";
                }

                String purchaseAmount = obj.getPurchaseAmount();
                if(purchaseAmount!=null && !purchaseAmount.isEmpty()){

                }else{
                    purchaseAmount = "0";
                }

                double sales_qty = Double.parseDouble(salesqty);
                if(sales_qty < 0){
                    sales_qty =0;
                }
                obj.setSalesQty(String.valueOf(sales_qty));
                double unit_cost = Double.parseDouble(unitcost);
                if(unit_cost < 0){
                    unit_cost =0;
                }
                obj.setUnitCost(String.valueOf(unit_cost));
                double avg_cost = Double.parseDouble(averageCost);
                if(avg_cost < 0){
                    avg_cost =0;
                }
                double sales_amount = Double.parseDouble(salesAmount);
                if(sales_amount < 0){
                    sales_amount =0;
                }
                obj.setSalesAmount(String.valueOf(sales_amount));
                double sales_price = Double.parseDouble(salesprice);
                if(sales_price < 0){
                    sales_price =0;
                }
                obj.setOutletPrice(String.valueOf(sales_price));
                double avg_purchase_cost = Double.parseDouble(averagePurchaseCost);
                if(avg_purchase_cost < 0){
                    avg_purchase_cost =0;
                }
                double purchase_amount = 0;
                double margin_perc;
                if(costtype.equalsIgnoreCase("Unit Cost")){
                    purchase_amount = sales_qty * unit_cost;
                }else if(costtype.equalsIgnoreCase("Average Cost")){
                    purchase_amount = sales_qty * avg_cost;
                }else if(costtype.equalsIgnoreCase("Purchase Average Cost")){
                    purchase_amount = Double.parseDouble(purchaseAmount);
                }
                double profit_amount = sales_amount - purchase_amount ;
                if(purchase_amount < 0){
                    purchase_amount =0;
                }
                String purchaseamount = twoDecimalPoint(purchase_amount);
                obj.setPurchaseAmount(purchaseamount);
                if(profit_amount < 0){
                    profit_amount =0;
                }
                String profitamount = twoDecimalPoint(profit_amount);
                obj.setProfitAmount(profitamount);
                String avgcost = twoDecimalPoint(avg_cost);
                obj.setAverageCost(avgcost);
                String avg_purchasecost = twoDecimalPoint(avg_purchase_cost);
                obj.setAveragePurchaseCost(avg_purchasecost);
              //  holder.purchase_amount.setText(""+purchase_amount);
              //  holder.profit_amount.setText(""+profit_amount);
                if(avg_purchase_cost == 0){
                    margin_perc =0;
                }else{
                    margin_perc = (sales_price - avg_purchase_cost) / (avg_purchase_cost * 100);
                }
                if(margin_perc < 0){
                    margin_perc =0;
                }
                String marginperc = twoDecimalPoint(margin_perc);
                obj.setMargin_perc(marginperc);
               // holder.margin_perc.setText(marginperc);

            }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Log.d("ProductListArray", ""+ProductListArray.size());


            if(sortby.equalsIgnoreCase("Product Name")){
                Collections.sort(ProductListArray, ProductStockGetSet.productnameComparator);
            }else if(sortby.equalsIgnoreCase("Purchase Quantity")){
                Collections.sort(ProductListArray, ProductStockGetSet.PurchaseqtyComp);
            }else if(sortby.equalsIgnoreCase("Sales Quantity")){
                Collections.sort(ProductListArray, ProductStockGetSet.SalesqtyComp);
            }else if(sortby.equalsIgnoreCase("Balance Quantity")){
                Collections.sort(ProductListArray, ProductStockGetSet.BalanceqtyComp);
            }else if(sortby.equalsIgnoreCase("Profit Amount")){
                Collections.sort(ProductListArray, ProductStockGetSet.ProfitamountComp);
            }else if(sortby.equalsIgnoreCase("Margin Percentage")){
                Collections.sort(ProductListArray, ProductStockGetSet.MarginpercComp);
            }

            Adapter = new ProductCustomAdapter(ProductAnalysisActivity.this,
                    ProductListArray);
            product_list.setAdapter(Adapter);
            Adapter.notifyDataSetChanged();

            double totalprofit = totalProfit();
            String tot_profit = twoDecimalPoint(totalprofit);
            Log.d("totalprofit", tot_profit);

            total_profit.setText(tot_profit);

            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(productanalysis_layout, true);

            product_search.setEnabled(true);
        }
    }

    public double totalProfit() {
        double totalCharge = 0;
        for (ProductStockGetSet data : ProductListArray) {
            String profitamount = data.getProfitAmount();
            double pamt = Double.parseDouble(profitamount);
            if(pamt != 0){
               /* int qty = data.getQuantity();*/
                totalCharge += pamt;
            }
        }
        return  totalCharge;
    }

    public class ProductCustomAdapter extends BaseAdapter  {

        private ArrayList<ProductStockGetSet> listarray = new ArrayList<ProductStockGetSet>();
        LayoutInflater mInflater;
        CustomHolder holder = new CustomHolder();
        ProductStockGetSet user;

        public ProductCustomAdapter(Context context,
                                    ArrayList<ProductStockGetSet> productsList) {
            listarray.clear();
            this.listarray = productsList;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listarray.size();
        }

        @Override
        public ProductStockGetSet getItem(int position) {
            return listarray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return listarray.get(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            View row = convertView;
            holder = null;

            if (row == null) {
                row = mInflater.inflate(R.layout.productanalysis_list_item, null);
                holder = new CustomHolder();
                holder.productName = (TextView) row
                        .findViewById(R.id.productName);
                holder.productPCS = (TextView) row.findViewById(R.id.pcs);
                holder.purchase_qty = (TextView) row.findViewById(R.id.purchase_qty);
                holder.sales_qty = (TextView) row.findViewById(R.id.sales_qty);
                holder.balance_qty = (TextView) row.findViewById(R.id.balance_qty);
                holder.purchase_amount = (TextView) row.findViewById(R.id.purchase_amount);
                holder.profit_amount = (TextView) row.findViewById(R.id.profit_amount);
                holder.sales_amount = (TextView) row.findViewById(R.id.sales_amount);
                holder.avg_purchase_cost = (TextView) row.findViewById(R.id.avg_purchase_cost);
                holder.avg_cost = (TextView) row.findViewById(R.id.avg_cost);
                holder.unit_cost = (TextView) row.findViewById(R.id.unit_cost);
                holder.selling_price = (TextView) row.findViewById(R.id.selling_price);
                holder.margin_perc = (TextView) row.findViewById(R.id.margin_perc);
                holder.arrow_image = (ImageView) row.findViewById(R.id.arrow_image);
                holder.detail_layout = (LinearLayout) row.findViewById(R.id.detail_layout);

                row.setTag(holder);
            } else {
                holder = (CustomHolder) row.getTag();
            }
            row.setId(position);
            user = getItem(position);
            holder.productName.setText(user.getProduct_Name());
            holder.productPCS.setText(user.getProduct_PcsPerCarton());

            holder.purchase_qty.setText(user.getPurchase_Quantity());
            holder.sales_qty.setText(user.getSalesQty());
            holder.balance_qty.setText(user.getBalanceQty());
            holder.purchase_amount.setText(user.getPurchaseAmount());
            holder.profit_amount.setText(user.getProfitAmount());
            holder.sales_amount.setText(user.getSalesAmount());
            holder.avg_purchase_cost.setText(user.getAveragePurchaseCost());
            holder.avg_cost.setText(user.getAverageCost());
            holder.unit_cost.setText(user.getUnitCost());
            holder.selling_price.setText(user.getOutletPrice());
            holder.margin_perc.setText(user.getMargin_perc());

            final View finalRow = row;
            holder.arrow_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder = (CustomHolder) finalRow.getTag();
                    if(holder.detail_layout.getVisibility() == View.VISIBLE){
                        holder.detail_layout.setVisibility(View.GONE);
                        holder.arrow_image.setImageResource(R.drawable.arrow_down);
                    }else{
                        holder.detail_layout.setVisibility(View.VISIBLE);
                        holder.arrow_image.setImageResource(R.drawable.arrow_up);
                    }
                }
            });

         /*   String salesqty = user.getSalesQty();
            if(salesqty!=null && !salesqty.isEmpty()){

            }else{
                salesqty = "0";
            }

            String unitcost = user.getUnitCost();
            if(unitcost!=null && !unitcost.isEmpty()){

            }else{
                unitcost = "0";
            }

            String averageCost = user.getAverageCost();
            if(averageCost!=null && !averageCost.isEmpty()){

            }else{
                averageCost = "0";
            }

            String salesAmount = user.getSalesAmount();
            if(salesAmount!=null && !salesAmount.isEmpty()){

            }else{
                salesAmount = "0";
            }

            String salesprice = user.getOutletPrice();
            if(salesprice!=null && !salesprice.isEmpty()){

            }else{
                salesprice = "0";
            }

            String averagePurchaseCost = user.getAveragePurchaseCost();
            if(averagePurchaseCost!=null && !averagePurchaseCost.isEmpty()){

            }else{
                averagePurchaseCost = "0";
            }

            String purchaseAmount = user.getPurchaseAmount();
            if(purchaseAmount!=null && !purchaseAmount.isEmpty()){

            }else{
                purchaseAmount = "0";
            }

            double sales_qty = Double.parseDouble(salesqty);
            double unit_cost = Double.parseDouble(unitcost);
            double avg_cost = Double.parseDouble(averageCost);
            double sales_amount = Double.parseDouble(salesAmount);
            double sales_price = Double.parseDouble(salesprice);
            double avg_purchase_cost = Double.parseDouble(averagePurchaseCost);
            double purchase_amount = 0;
            double margin_perc;
            if(costtype.equalsIgnoreCase("Unit Cost")){
                purchase_amount = sales_qty * unit_cost;
            }else if(costtype.equalsIgnoreCase("Average Cost")){
                purchase_amount = sales_qty * avg_cost;
            }else if(costtype.equalsIgnoreCase("Purchase Average Cost")){
                purchase_amount = Double.parseDouble(purchaseAmount);
            }
            double profit_amount = sales_amount - purchase_amount ;
            holder.purchase_amount.setText(""+purchase_amount);
            holder.profit_amount.setText(""+profit_amount);
            margin_perc = (sales_price - avg_purchase_cost) / (avg_purchase_cost * 100);
            String marginperc = twoDecimalPoint(margin_perc);
            holder.margin_perc.setText(marginperc);*/

            // holder.weight.setText(user.getWeight());

          //  holder.productCode.setTag(position);

            return row;
        }

        final class CustomHolder {
            TextView productName;
            TextView productPCS;
            TextView purchase_qty,sales_qty,balance_qty,purchase_amount,profit_amount,sales_amount,avg_purchase_cost,avg_cost,
                     unit_cost,selling_price,margin_perc;
            ImageView arrow_image;
            LinearLayout detail_layout;

        }
    }

    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tot = df.format(d);

        return tot;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        menu.toggle();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(String item) {
        // TODO Auto-generated method stub
        menu.toggle();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProductAnalysisActivity.this, LandingActivity.class);
        startActivity(intent);
        finish();
    }
}
