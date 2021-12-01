package com.winapp.sot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Constants;
import com.winapp.model.StockCount;
import com.winapp.offline.OfflineDatabase;
import com.winapp.zxing.SmallCaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.winapp.sot.ManualStockDialog.*;
import static com.winapp.sot.SOTSummaryWebService.context;

public class ManualStockSummary extends SherlockFragmentActivity implements ManualStockDialog.ManualStockListener,
        SlideMenuFragment.MenuClickInterFace,Constants {
    LinearLayout customer_layout;
    private SlidingMenu menu;
    TextView customer_screen,listing_screen,add_product_screen,summary_screen;
    ImageButton save, back, barcode_edit;
    EditText sl_codefield;
    private String cmpnyCode, locCode,jsonString,mBarcodeJsonString="",mjsonString;
    private HashMap<String, String> mparam;
    private HashMap<String, String> param;
    private JSONObject jsonResponse,mBarcodeJsonObject,mjsonResponse;
    private JSONArray jsonMainNode,mBarcodeJsonArray,mjsonMainNode;
    private ArrayList<StockCount> alstockCount;
    private HashMap<String, String> hmprodCodeName;
    private ArrayList<HashMap<String, String>> msearchResults, malhmProducts,
            alhmsearchProduct, malhmgetal, alhmbarcode;
    private ArrayList<String> malprodcode;
    private LinearLayout mspinnerLayout;
    private ProgressBar mprogressBar;
    private HashMap<String, String> mHashMap;
    private String sales_prodCode = "",values = "", slPrice = "", slUomCode = "", slCartonPerQty = "",taxValue = "",Weight = ""
            , LocationCode = "", stock,product_stock_jsonString = null,taxType = "",ss_Cqty = "",beforeLooseQty,str_ssupdate;
    private ArrayList<String> getSalesProdArr = new ArrayList<String>();
    ListView listView;
    ManualListAdapter adapter;
    String codefield,namefield;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_menu);
        setContentView(R.layout.activity_manual_stock_summary);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(false);

        View customNav = LayoutInflater.from(this).inflate(R.layout.summary_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        txt.setText("Product Summary");
        back = (ImageButton) customNav.findViewById(R.id.back);
        barcode_edit = (ImageButton) customNav.findViewById(R.id.barcode_edit);
        save = (ImageButton) customNav.findViewById(R.id.save);
        barcode_edit.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);
        SOTDatabase.init(context);
        ab.setCustomView(customNav);
        ab.setDisplayShowCustomEnabled(true);
        sl_codefield = (EditText)findViewById(R.id.st_codefield);
        malprodcode = new ArrayList<>();
        mHashMap = new HashMap<>();

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slidemenufragment);
        menu.setSlidingEnabled(false);
        back.setVisibility(View.INVISIBLE);
        cmpnyCode = SupplierSetterGetter.getCompanyCode();
        locCode = SalesOrderSetGet.getLocationcode();
        String getNo = SalesOrderSetGet.getCon_no();
        Log.d("getNocheck",getNo);
        mparam = new HashMap<String, String>();
        param = new HashMap<String,String>();
        hmprodCodeName = new HashMap<String, String>();
        malhmProducts = new ArrayList<>();

        customer_screen = (TextView)findViewById(R.id.customer_screen);
        listing_screen = (TextView)findViewById(R.id.listing_screen);
        add_product_screen = (TextView)findViewById(R.id.addProduct_screen);
        summary_screen = (TextView)findViewById(R.id.sum_screen);
        customer_screen.setVisibility(View.GONE);
        customer_layout = (LinearLayout)findViewById(R.id.customer_layout);
        customer_layout.setVisibility(View.GONE);
        listView = (ListView)findViewById(R.id.listview);
//        registerForContextMenu(listView);

        new GetStockCountDetailByNo().execute();

        cursor = SOTDatabase.getManualStockCursor();
        Log.d("cursor.getCount()","-->"+cursor.getCount());

        if(cursor.getCount() > 0){
            save.setVisibility(View.VISIBLE);
        }else{
            save.setVisibility(View.INVISIBLE);
        }


        adapter = new ManualListAdapter(ManualStockSummary.this,cursor);
        listView.setAdapter(adapter);

        summary_screen.setTextColor(Color.parseColor("#FFFFFF"));
        summary_screen.setBackgroundResource(R.drawable.tab_select);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String code = ((TextView) view.findViewById(R.id.ss_prodcode)).getText().toString();
                Log.d("ss_prodcode",code);
                Cursor cursor = SOTDatabase.getManualStockCursors(code);
                Log.d("cursorCount","-->"+cursor.getCount());
                if (cursor != null && cursor.getCount() > 0) {

                    if (cursor.moveToFirst()) {
                        do {
                            String codefield = cursor.getString(cursor
                                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
                            String namefield = cursor.getString(cursor
                                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
                            String qty = cursor.getString(cursor
                                    .getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
                            String pcs = cursor.getString(cursor
                                        .getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY));
                            String cQty = cursor.getString(cursor
                                    .getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY));
                            String lQty = cursor.getString(cursor
                                    .getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY));
                            Log.d("quantitycheck",""+qty+"pcs:"+pcs);

                            ManualStockDialog dialogFragment = newInstance(codefield,namefield,SalesOrderSetGet.getCon_no(),qty,pcs,cQty,lQty);
                            dialogFragment.show(getFragmentManager(), "dialog");

                        } while (cursor.moveToNext());
                    }
                }
            }
        });

        listing_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManualStockSummary.this,ManualStockHeader.class);
                startActivity(intent);
                finish();
            }
        });

        add_product_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManualStockSummary.this,ManualAddStockTake.class);
                startActivity(intent);
                finish();
            }
        });

        sl_codefield.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard();


                    Log.d("mproductCode_ed1","-->"+sl_codefield.getText().toString());

                    String prodcode = sl_codefield.getText().toString();

                    if(!prodcode.matches("")){
                        new GetBarCode(prodcode).execute();
                    }else{
                       /* Log.d("prodcode","null");
                        mtblRow1.setVisibility(View.GONE);
                        System.out.println("Text [" + sl_codefield.getText().toString() + "]");
                        stockAdapter.setFilterType("Product");
                        stockAdapter.getFilter().filter(sl_codefield.getText().toString());*/
                    }

//					if(!prodcode.matches("")){
//						Log.d("prodcode1","null");
//						mtblRow1.setVisibility(View.GONE);
//						System.out.println("Text [" + prodcode + "]");
//						stockAdapter.setFilterType("Product");
//						stockAdapter.getFilter().filter(prodcode);
//						progressBarOpen();
//					}

                    return true;
                }
				return false;
        }
    });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                save.setVisibility(View.GONE);
                saveAlertDialog();

            }
        });

    }

   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        View TargetV = info.targetView;
        TextView tvProdCode = (TextView) TargetV.findViewById(R.id.ss_prodcode);
        String strProdCode = tvProdCode.getText().toString();


        Log.d("code", strProdCode);
        menu.add(0, v.getId(), 0, "Edit");

    }
    @SuppressWarnings("deprecation")
    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();

        if (item.getTitle() == "Edit") {
            String code = ((TextView) info.targetView
                    .findViewById(R.id.ss_prodcode)).getText().toString();
            Log.d("ss_prodcode",code);
            Cursor cursor = SOTDatabase.getManualStockCursors(code);
            Log.d("cursorCount","-->"+cursor.getCount());
            if (cursor != null && cursor.getCount() > 0) {

                if (cursor.moveToFirst()) {
                    do {
                        String codefield = cursor.getString(cursor
                                .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));
                        String namefield = cursor.getString(cursor
                                .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME));
                        String qty = cursor.getString(cursor
                                .getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
                        Log.d("quantitycheck",qty);

                        ManualStockDialog dialogFragment = newInstance(codefield,namefield,SalesOrderSetGet.getCon_no(),qty);
                        dialogFragment.show(getFragmentManager(), "dialog");

                    } while (cursor.moveToNext());
                }
            }

        }
        return true;
    }*/
    private void saveAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.mipmap.ic_save);
        alertDialog.setTitle("Save");
        alertDialog.setMessage("Do you want to Save");
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int indexSelected) {
                save.setVisibility(View.INVISIBLE);
//                saveData("0");
            }
        });



        alertDialog.show();
    }

    @Override
    public void refreshAdapter(String prod) {
        Log.d("pcode",prod);
        Cursor cursor = SOTDatabase.getManualStockCursor();
        adapter = new ManualListAdapter(ManualStockSummary.this,cursor);
        listView.setAdapter(adapter);
        Log.d("cursor.getCount","-->"+cursor.getCount());
        cursor.requery();
    }

    private class GetBarCode extends AsyncTask<Void, Void, Void> {
        String barcode = "", productbarcode = "", productCode = "";

        private GetBarCode(String productCode)
        {
            this.productCode = productCode;

        }
        @Override
        protected void onPreExecute() {

            progressBarOpen();
            barcode = sl_codefield.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            mparam.put("CompanyCode", cmpnyCode);
            mparam.put("ProductCode", "");
            mparam.put("Barcode", barcode);
            jsonString = SalesOrderWebService.getSODetail(mparam,
                    FNC_GETPRODUCTBARCODE);

            Log.d("jsonString ", "" + jsonString);

            if (jsonString != null && !jsonString.isEmpty()) {
                try {

                    jsonResponse = new JSONObject(jsonString);
                    jsonMainNode = jsonResponse.optJSONArray("SODetails");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    int lengthJsonArr = jsonMainNode.length();
                    for (int i = 0; i < lengthJsonArr; i++) {

                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                        productbarcode = jsonChildNode.optString(
                                PRODUCT_CODE).toString();
                        String barcode = jsonChildNode.optString(PRODUCT_BARCODE)
                                .toString();
                        Log.d("productbarcodecheck",productbarcode);
//					HashMap<String, String> barcodehm = new HashMap<String, String>();
//					barcodehm.put(productbarcode, barcode);
//					alhmbarcode.add(barcodehm);
//					malbarcode.add(barcode);


                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mparam.clear();
            }else{
                Log.d("eecutingtask",sales_prodCode);
                sales_prodCode = sl_codefield.getText().toString();


                getSalesProdArr = SalesProductWebService.getGraProduct(
                        sales_prodCode, "fncGetProduct");

                slCartonPerQty = getSalesProdArr.get(2);

                String codefield = getSalesProdArr.get(6);

                String namefield = getSalesProdArr.get(7);
                String slno = getSalesProdArr.get(12);

                Log.d("code&name1","-->"+codefield+"-->"+namefield+"-->"+slno);
                Log.d("slCartonPerQty",slCartonPerQty);
                sl_codefield.setText("");
                ManualStockDialog dialogFragment = newInstance(codefield,namefield,SalesOrderSetGet.getCon_no(),"",slCartonPerQty,"","");
                dialogFragment.show(getFragmentManager(), "dialog");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            progressBarClose();
            sl_codefield.requestFocus();
            showKeyboard(sl_codefield);

            if(!productbarcode.matches("")){

                sl_codefield.setText(""+productbarcode);
            }
            sales_prodCode = sl_codefield.getText().toString();
            Log.d("sales_prodCode",sales_prodCode);

//            new AsyncGetProduct(sales_prodCode).execute();

           getSalesProdArr = SalesProductWebService.getGraProduct(
                   sales_prodCode, "fncGetProduct");

           if(getSalesProdArr.size()>0){
               slCartonPerQty = getSalesProdArr.get(2);

               String codefield = getSalesProdArr.get(6);
               String namefield = getSalesProdArr.get(7);

               String slno = getSalesProdArr.get(12);

               Log.d("code&name","-->"+codefield+"-->"+namefield+"-->"+slno);
               sl_codefield.setText("");
               ManualStockDialog dialogFragment = newInstance(codefield,namefield,SalesOrderSetGet.getCon_no(),"",slCartonPerQty,"","");
               dialogFragment.show(getFragmentManager(), "dialog");
           }else{
               Toast.makeText(getApplicationContext(),"Product Code Not Exist",Toast.LENGTH_SHORT).show();
           }




//            mtblRow1.setVisibility(View.GONE);
//            System.out.println("Text [" + sl_codefield.getText().toString() + "]");
//            stockAdapter.setFilterType("Product");
//            stockAdapter.getFilter().filter(sl_codefield.getText().toString());
//
////			productbarcode = "";
//            Log.d("show","keyboard --"+stockAdapter.getCount());
//			if(stockAdapter.getCount()==1){
//				Log.d("show","keyboard");
//				stockAdapter.qtyCount(0);
//				stockAdapter.notifyDataSetChanged();
//			}
        }
    }

    protected void showKeyboard(EditText editText) {
        Log.d("showKeyboard", "Show");
        InputMethodManager inputManager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
        editText.requestFocus();
    }
    protected void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    private class CheckProductBarcode extends AsyncTask<Void, Void, Void> {

        String ed_code = "";

        @Override
        protected void onPreExecute() {
            mBarcodeJsonString = "";
            progressBarOpen();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ed_code = sl_codefield.getText().toString();
            Log.d("barcodecheck",ed_code);
            mHashMap.clear();
            mHashMap.put("CompanyCode", cmpnyCode);
            mHashMap.put("Barcode", ed_code);

            try {
                mBarcodeJsonString = SalesOrderWebService.getSODetail(mHashMap,
                        "fncGetProductBarCode");
                Log.d("mBarcodeJsonString", "-->" + mBarcodeJsonString);
                mBarcodeJsonObject = new JSONObject(mBarcodeJsonString);
                mBarcodeJsonArray = mBarcodeJsonObject
                        .optJSONArray("SODetails");
                int lengthJsonBarcodeArr = mBarcodeJsonArray.length();

                if (lengthJsonBarcodeArr > 0) {
                    JSONObject jsonChildNode = mBarcodeJsonArray
                            .getJSONObject(0);
                    String productbarcode = jsonChildNode.optString(
                            PRODUCT_CODE).toString();
                    String barcode = jsonChildNode.optString(PRODUCT_BARCODE)
                            .toString();
                    sales_prodCode = productbarcode;
                    Log.d("productbarcode",productbarcode);
                } else {
                    mBarcodeJsonString = "";
                }
            } catch (JSONException e) {
                mBarcodeJsonString = "";
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

		/*	String ed_code = sl_codefield.getText().toString();
			if(!ed_code.matches("")){*/
            boolean flag = false;
            if (mBarcodeJsonString != null && !mBarcodeJsonString.isEmpty()) {
                for (int i = 0; i < alstockCount.size(); i++) {
                    if (alstockCount.get(i).getProductCode().equals(sales_prodCode)) {
                        Log.d("if", "if");
                        flag = true;
                        break;

                    } else {
                        Log.d("else", "else");
                        flag = false;
                        //Toast.makeText(StockTakeAddProduct.this, "No Product Found", Toast.LENGTH_SHORT).show();
                    }
                }

                if(!flag){
                    Toast.makeText(ManualStockSummary.this, "No Product Found", Toast.LENGTH_SHORT).show();
                }else{
                    new AsyncCallSaleProduct("false").execute();
                }
            }else{
                sales_prodCode = ed_code;
                new AsyncCallSaleProduct("false").execute();
            }
            //}
        }

    }

    private class AsyncCallSaleProduct extends AsyncTask<Void, Void, Void> {
        String filterClick="", codefield = "" ;

        public AsyncCallSaleProduct(String fromFilterClick) {
            filterClick = fromFilterClick;
        }

        @Override
        protected void onPreExecute() {

            getSalesProdArr.clear();
//            alBatchStock.clear();
            slPrice = "";
            slUomCode = "";
            slCartonPerQty = "";
            taxValue = "";
//            sl_cartonQty.setText("");
//            sl_looseQty.setText("");
//            sl_qty.setText("");
//            sl_uom.setText("");
//            sl_stock.setText("");
//            sl_cartonPerQty.setText("");
//            sl_total.setText("0");
//            sl_tax.setText("0");
//            sl_netTotal.setText("0");

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub

            getSalesProdArr = SalesProductWebService.getGraProduct(
                    sales_prodCode, "fncGetProduct");

			/*alBatchStock = SalesProductWebService.getProductBatchStock(
					sales_prodCode, "fncGetProductBatchStock");*/

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

//            new ProductStockAsyncCall().execute();

           /* if (!getSalesProdArr.isEmpty()) {

//                carton_layout.setVisibility(View.VISIBLE);
//                if (!searchProductArr.isEmpty()) {
//                    grid_layout.setVisibility(View.VISIBLE);
//                }

                Log.d("getSalesProdArr", getSalesProdArr.toString());
                slPrice = getSalesProdArr.get(0);
                slUomCode = getSalesProdArr.get(1);
                slCartonPerQty = getSalesProdArr.get(2);
                taxValue = getSalesProdArr.get(3);

//				sl_uom.setText(slUomCode);
//				sl_cartonPerQty.setText(slCartonPerQty);

                String haveBatch = getSalesProdArr.get(4);
                String haveExpiry = getSalesProdArr.get(5);
                codefield = getSalesProdArr.get(6);
                String namefield = getSalesProdArr.get(7);
                Weight = getSalesProdArr.get(8);


                Log.d("haveBatch s", haveBatch);
                Log.d("haveExpiry s", haveExpiry);

                sl_codefield.setText(codefield);
                sl_namefield.setText(namefield);

                HashMap<String, EditText> hm = new HashMap<String, EditText>();

                hm.put("Productcode", sl_codefield);
                hm.put("Productname", sl_namefield);
                hm.put("Cartonqty", sl_cartonQty);
                hm.put("Looseqty", sl_looseQty);
                hm.put("Qty", sl_qty);
                hm.put("Uom", sl_uom);
                hm.put("Stock", sl_stock);
                hm.put("Cartonperqty", sl_cartonPerQty);

                String haveBatchOnTransfer = SalesOrderSetGet
                        .getHaveBatchOnTransfer();
                String haveBatchOnTransferToLocation = SalesOrderSetGet
                        .getHaveBatchOnTransferToLocation();
                if (haveBatchOnTransfer != null
                        && !haveBatchOnTransfer.isEmpty()
                        && haveBatchOnTransfer.matches("True")
                        || haveBatchOnTransferToLocation != null
                        && !haveBatchOnTransferToLocation.isEmpty()
                        && haveBatchOnTransferToLocation.matches("True")) {
                    Log.d("haveBatchOnStockIn", haveBatchOnTransfer);
                    if (haveBatch.matches("True") || haveExpiry.matches("True")) {
                        Log.d("haveBatch", "haveExpiry");
                        String code = sl_codefield.getText().toString();
                        String name = "";
						*//*if (mflag.equalsIgnoreCase("ReceiveStock")) {
							name = sl_namereceivefield.getText().toString();
						}else{*//*
                        name = sl_namefield.getText().toString();
                        //	}

                        Log.d("alBatchStock d",
                                "batch click" + alBatchStock.toString());

						*//*if (!alBatchStock.isEmpty()) {
							transferBatchDialog.initiateBatchPopupWindow(
									TransferAddProduct.this, haveBatch,
									haveExpiry, code, name, slCartonPerQty,
									slPrice, hm, alBatchStock);
						} else {*//*
                        noBatchvalue();
                        //}

                    } else {
                        Log.d("no haveBatch", "no haveExpiry");
                        noBatch();
                    }

                } else {
                    Log.d("no haveBatchOnStockIn", "no haveBatchOnStockIn");
                    noBatch();
                }
            } else {
                sl_codefield.requestFocus();
                Toast.makeText(getApplicationContext(), " Invalid Code ",
                        Toast.LENGTH_SHORT).show();
                sl_codefield.setText("");
                sl_namefield.setText("");
            }

            if(filterClick!=null && !filterClick.isEmpty() && filterClick.matches("true")){
                String db_pcode = SOTDatabase.getProductCode(codefield);
                Log.d("db_pcode", "->"+db_pcode);

                if(db_pcode != null && !db_pcode.isEmpty()){
                    double pqty = SOTDatabase.getProductQty(db_pcode);
                    sl_qty.setText(""+pqty);
                    double cqty = SOTDatabase.getCartonQty(db_pcode);
                    sl_cartonQty.setText(""+cqty);
                    double lqty = SOTDatabase.getLooseQty(db_pcode);
                    sl_looseQty.setText(""+lqty);
                    sl_qty.requestFocus();
                    sl_qty.setSelection(sl_qty.length());
                }
            }*/
            progressBarClose();
        }
    }



    public void progressBarOpen() {
        getActionBar().setHomeButtonEnabled(false);
        menu.setSlidingEnabled(false);
        mspinnerLayout = new LinearLayout(ManualStockSummary.this);
        mspinnerLayout.setGravity(Gravity.CENTER);
        addContentView(mspinnerLayout, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        mspinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
//        enableViews(mtblLayout, false);
        mprogressBar = new ProgressBar(ManualStockSummary.this);
        mprogressBar.setProgress(android.R.attr.progressBarStyle);
        mprogressBar.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.greenprogress));

        mspinnerLayout.addView(mprogressBar);
    }

    public void progressBarClose() {
        mprogressBar.setVisibility(View.GONE);
        mspinnerLayout.setVisibility(View.GONE);
//        enableViews(mtblLayout, true);
        getActionBar().setHomeButtonEnabled(true);
        menu.setSlidingEnabled(true);
    }

    public void scanMarginScanner(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(SmallCaptureActivity.class);
        integrator.initiateScan();
    }

    @Override
    public void onListItemClick(String item) {

    }


    private class GetStockCountDetailByNo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mparam.clear();
            alstockCount = new ArrayList<>();
            alstockCount.clear();
            malhmProducts.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mparam.put("CompanyCode", cmpnyCode);
            mparam.put("LocationCode", locCode);

            try {
                jsonString = SalesOrderWebService.getSODetail(mparam, "fncGetProduct");

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
                    Log.d("productcodesCheck",productcodes);
                    stockcount.setProductCode(productcodes);
                    stockcount.setProductName(productnames);

                    alstockCount.add(stockcount);

                    HashMap<String, String> producthm = new HashMap<String, String>();
                    producthm.put(productcodes, productnames);
                    malhmProducts.add(producthm);
                    hmprodCodeName.putAll(producthm);
                    malprodcode.add(productcodes);

                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }


    private class ManualListAdapter extends ResourceCursorAdapter {

        public ManualListAdapter(Context context, Cursor cursor) {
            super(context,R.layout.activity_adapter,cursor);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView ss_sl_name = (TextView) view.findViewById(R.id.ss_name);
            ss_sl_name.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME)));

            TextView ss_sl_code = (TextView) view.findViewById(R.id.ss_prodcode);
            ss_sl_code.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE)));

            TextView ss_c_qty = (TextView) view.findViewById(R.id.ss_c_qty);
            ss_c_qty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));

            TextView ss_l_qty = (TextView) view.findViewById(R.id.ss_l_qty);
            ss_l_qty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));

            TextView ss_sl_qty = (TextView) view.findViewById(R.id.ss_qty);
            ss_sl_qty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ManualStockSummary.this,ManualStockHeader.class);
        startActivity(intent);
        finish();
    }

    private class AsyncGetProduct extends AsyncTask<Void, Void, Void>{
        String prod_code;

        public AsyncGetProduct(String sales_prodCode) {
            this.prod_code = sales_prodCode;
        }


        @Override
        protected void onPreExecute() {
//            progressBarOpen();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            param.put("CompanyCode", cmpnyCode);
            param.put("ProductCode", prod_code);

            mjsonString = SalesOrderWebService.getSODetail(param,
                    "fncGetProduct");

            Log.d("jsonString ", "" + mjsonString);


                try {

                    mjsonResponse = new JSONObject(mjsonString);
                    mjsonMainNode = mjsonResponse.optJSONArray("SODetails");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    int lengthJsonArr = mjsonMainNode.length();
                    for (int i = 0; i < lengthJsonArr; i++) {

                        JSONObject jsonChildNode = mjsonMainNode.getJSONObject(i);

                        codefield = jsonChildNode.optString(
                                PRODUCT_CODE).toString();
                        namefield = jsonChildNode.optString(PRODUCT_NAME)
                                .toString();
                        Log.d("productbarcodecheck",namefield);

                        Log.d("code&name","-->"+codefield+"-->"+namefield+"-->"+""+(i+1));
//
                        ManualStockDialog dialogFragment = newInstance(codefield,namefield,SalesOrderSetGet.getCon_no(),""+(i+1),"","","");
                        dialogFragment.show(getFragmentManager(), "dialog");

                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                param.clear();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            progressBarClose();
            sl_codefield.setText("");
        }
    }
}
