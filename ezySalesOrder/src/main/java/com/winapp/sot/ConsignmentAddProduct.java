package com.winapp.sot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.fwms.CustomAlertAdapterProd;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.NewProductWebService;
import com.winapp.fwms.SupplierSetterGetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.winapp.SFA.R.drawable;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSetterGetter;
import com.winapp.sotdetails.ConsignmentDialogFragment;
import com.winapp.sotdetails.CustomerListActivity;

public class ConsignmentAddProduct extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace, AdapterView.OnItemClickListener {


    private static String webMethName = "fncGetProduct";
    private static String webMethNamebar = "fncGetProductBarCode";
    private static final String PRODUCT_CODE = "ProductCode";
    private static final String PRODUCT_NAME = "ProductName";
    private static final String PRODUCTNAME_BARCODE = "ProductCode";
    private static final String PRODUCT_BARCODE = "Barcode";
    private String mProductJsonString = "", mBarcodeJsonString = "";
    private JSONObject product_stock_jsonResponse = null,
            mProductJsonObject = null, mBarcodeJsonObject = null;
    private JSONArray product_stock_jsonMainNode = null,
            mProductJsonArray = null, mBarcodeJsonArray = null;
    private HashMap<String, String> mHashMap;
    SlidingMenu menu;
    TextView textView1;
    EditText editText1, sl_stock;
    ArrayList<HashMap<String, String>> searchProductArr = new ArrayList<HashMap<String, String>>();
    Button sl_addProduct, sl_summary;
    EditText sl_codefield, sl_namefield, sl_cartonQty, sl_looseQty, sl_qty,
            sl_foc, sl_price, sl_itemDiscount, sl_uom, sl_cartonPerQty,
            sl_total, sl_tax, sl_netTotal, sl_cprice, sl_exchange,sl_total_inclusive;
    AlertDialog myalertDialog = null;
    ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
    EditText editText;
    CustomAlertAdapterProd arrayAdapterProd;
    ArrayList<HashMap<String, String>> searchResults;
    int textlength = 0;
    ArrayList<HashMap<String, String>> getArraylists = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> getArraylist = new ArrayList<HashMap<String, String>>();
    ProgressBar progressBar;
    LinearLayout spinnerLayout, foc_layout, customer_layout,
            delivery_carton_header, pcs_txt_layout, pcs_layout;
    LinearLayout salesproduct_layout, foc_header_layout, price_header_layout,
            grid_layout, carton_layout;
    String valid_url, productTxt, productresult, barcoderesult, barcodeTxt;
    String slPrice = "", slUomCode = "", slCartonPerQty = "";
    ArrayList<String> getSalesProdArr = new ArrayList<String>();
    String keyValues = "", values = "";
    static ArrayList<String> companyArr = new ArrayList<String>();
    String taxType = "", taxValue = "", sales_prodCode,
            MinimumSellingPrice = "", sl_delivery_qty = "", slNo = "",
            requestQty = "",MinimumCartonSellingPrice="",id="";
    Cursor cursor;
    String beforeLooseQty, beforeFoc;
    ListView productFilterList;
    String catStr = "", subCatStr = "", cstcode = "", cstgrpcd = "",
            newPrice = "", cprice = "", newcprice = "", priceflag = "",
            calCarton = "";
    String ss_Cqty = "",oldQty;
    int sl_no = 1;
    double itmDisc = 0;
    double netTtal = 0, taxAmount = 0;
    String str_ssupdate, str_sscancel, str_sssno;
    double tt;
    ArrayList<String> priceArr = new ArrayList<String>();
    ImageView expand;
    TextView price_txt,txt_stock,txt_price;
    TextWatcher cqtyTW, lqtyTW, qtyTW;
    TextView listing_screen, customer_screen, addProduct_screen,
            summary_screen,txt_exqty;
    String intentString = "";
    // FilterSearch filterSearch;
    private FilterCS filtercs;
    HashMap<String, String> producthashValue = new HashMap<String, String>();
    String product_stock_jsonString = null;
    ArrayList<HashMap<String, String>> stockConsignmentArr=new ArrayList<>();
    String cmpnyCode = "", LocationCode = "", stock,Weight="",customer_code="",quantity="";

    SimpleAdapter adapter;
    LinearLayout carton_loose_layout;
    ConsignmentDialogFragment dialogFragment;
    ConsignmentStockDialog consignmentStockDialog;
    boolean prdcode = false;
    ArrayList<String> productcode = new ArrayList<String>();
    boolean flag=false;
   static  EditText  sm_total, sm_total_new, sm_itemDisc, sm_subTotal, sm_tax, sm_netTotal, sm_subTotal_inclusive;
    static HashMap<String, EditText> hashMap =new HashMap<>();
    String prices_txt,carton_txt,loose_txt,pcs_txt,qty_txt;

    public ConsignmentAddProduct() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_menu);
        setContentView(R.layout.activity_consignment_add_product);

        SalesOrderSetGet.setHeader_flag("ConsignmentAddProduct");

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        View customNav = LayoutInflater.from(this).inflate(
                R.layout.addproduct_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        ImageButton pricetag = (ImageButton) customNav
                .findViewById(R.id.priceTag);
        ImageButton filter = (ImageButton) customNav.findViewById(R.id.filter);
        txt.setText("Add Product");

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

        // filterSearch = new FilterSearch(this);
        filtercs = new FilterCS(ConsignmentAddProduct.this);

        salesproduct_layout = (LinearLayout) findViewById(R.id.salesproduct_layout);
        foc_layout = (LinearLayout) findViewById(R.id.foc_layout);

        pcs_txt_layout = (LinearLayout) findViewById(R.id.pcs_txt_layout);
        pcs_layout = (LinearLayout) findViewById(R.id.pcs_layout);

        foc_header_layout = (LinearLayout) findViewById(R.id.delivery_foc_header_layout);
        price_header_layout = (LinearLayout) findViewById(R.id.delivery_price_header_layout);
        grid_layout = (LinearLayout) findViewById(R.id.delivery_grid_layout);
        carton_layout = (LinearLayout) findViewById(R.id.delivery_carton_layout);

        // back = (ImageView) findViewById(R.id.back);
        sl_summary = (Button) findViewById(R.id.sl_summary);
        sl_addProduct = (Button) findViewById(R.id.sl_addProduct);

        sl_codefield = (EditText) findViewById(R.id.sl_codefield);
        sl_namefield = (EditText) findViewById(R.id.sl_namefield);
        sl_cartonQty = (EditText) findViewById(R.id.sl_cartonQty);
        sl_looseQty = (EditText) findViewById(R.id.sl_looseQty);
        sl_qty = (EditText) findViewById(R.id.sl_qty);
        sl_foc = (EditText) findViewById(R.id.sl_foc);
        sl_price = (EditText) findViewById(R.id.sl_price);
        sl_itemDiscount = (EditText) findViewById(R.id.sl_itemDiscount);
        sl_uom = (EditText) findViewById(R.id.sl_uom);
        sl_cartonPerQty = (EditText) findViewById(R.id.sl_cartonPerQty);

        sl_cprice = (EditText) findViewById(R.id.sl_cprice);
        sl_exchange = (EditText) findViewById(R.id.sl_exchange);
        price_txt = (TextView) findViewById(R.id.delivery_pricetxt);
        expand = (ImageView) findViewById(R.id.expand);

        sl_stock = (EditText) findViewById(R.id.sl_stock);
        sl_total_inclusive = (EditText) findViewById(R.id.sl_total_inclusive);
        sl_total = (EditText) findViewById(R.id.sl_total);
        sl_tax = (EditText) findViewById(R.id.sl_tax);
        sl_netTotal = (EditText) findViewById(R.id.sl_netTotal);

        txt_price= (TextView) findViewById(R.id.txt_price);
        listing_screen = (TextView) findViewById(R.id.listing_screen);
        customer_screen = (TextView) findViewById(R.id.customer_screen);
        addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
        summary_screen = (TextView) findViewById(R.id.sum_screen);
        customer_layout = (LinearLayout) findViewById(R.id.customer_layout);
        txt_exqty =(TextView)findViewById(R.id.txt_exqty);
        txt_stock =(TextView)findViewById(R.id.txt_stock);
        productFilterList = (ListView) findViewById(R.id.productFilterList);
        consignmentStockDialog =new ConsignmentStockDialog();

        delivery_carton_header = (LinearLayout) findViewById(R.id.delivery_carton_header);

        customer_layout.setVisibility(View.GONE);
        // addProduct_screen.setBackgroundColor(Color.parseColor("#00AFF0"));
        addProduct_screen.setTextColor(Color.parseColor("#FFFFFF"));
        addProduct_screen.setBackgroundResource(drawable.tab_select);
        mHashMap = new HashMap<String, String>();
        valid_url = FWMSSettingsDatabase.getUrl();
        new SalesProductWebService(valid_url);
        new SalesOrderWebService(valid_url);
        new NewProductWebService(valid_url);
        calCarton = LogOutSetGet.getCalcCarton();
        cmpnyCode = SupplierSetterGetter.getCompanyCode();
        sl_itemDiscount.setRawInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL);


        priceflag = SalesOrderSetGet.getCartonpriceflag();
        // priceflag= "1";
        if (priceflag.matches("null") || priceflag.matches("")) {
            priceflag = "0";
        }

		/*if (priceflag.matches("1")) {
			sl_cprice.setVisibility(View.VISIBLE);
			price_txt.setVisibility(View.GONE);
			price_header_layout.setVisibility(View.VISIBLE);
		} else {
			sl_cprice.setVisibility(View.GONE);
			price_txt.setVisibility(View.VISIBLE);
			price_header_layout.setVisibility(View.GONE);
		}*/
        /******** Based on ShowPriceDO Amount will Gone or Visible *********/
        if (MobileSettingsSetterGetter.getShowPriceOnDO().equalsIgnoreCase(
                "true")) {
            ((LinearLayout) findViewById(R.id.total_txt_layout))
                    .setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.total_edt_layout))
                    .setVisibility(View.VISIBLE);
            sl_price.setVisibility(View.VISIBLE);
            expand.setVisibility(View.VISIBLE);

            if (priceflag.matches("1")) {
                sl_cprice.setVisibility(View.VISIBLE);
                price_txt.setVisibility(View.GONE);
                price_header_layout.setVisibility(View.VISIBLE);
            } else {
                sl_cprice.setVisibility(View.GONE);
                price_txt.setVisibility(View.VISIBLE);
                price_header_layout.setVisibility(View.GONE);
            }

        } else {
            ((LinearLayout) findViewById(R.id.total_txt_layout))
                    .setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.total_edt_layout))
                    .setVisibility(View.GONE);
			/*sl_price.setVisibility(View.INVISIBLE);
			expand.setVisibility(View.INVISIBLE);
			price_txt.setVisibility(View.INVISIBLE);*/

            sl_price.setVisibility(View.INVISIBLE);
            expand.setVisibility(View.INVISIBLE);
            price_txt.setVisibility(View.INVISIBLE);
            sl_cprice.setVisibility(View.GONE);
            price_header_layout.setVisibility(View.GONE);

        }
        SOTDatabase.init(ConsignmentAddProduct.this);

        Bundle extras = getIntent().getExtras();
     
        if (extras != null) {

            if (SalesOrderSetGet.getTranType().matches("O"))
                {

                    oldQty = "0";

                    intentString = extras.getString("Invoice");

                    sales_prodCode = extras.getString("SOT_ssproductcode");

                    ProductStockAsyncCall task = new ProductStockAsyncCall();
                    task.execute();

                    sl_codefield.setText(extras.getString("SOT_ssproductcode"));
                    sl_namefield.setText(extras.getString("SOT_str_ssprodname"));
                    sl_cartonQty.setText(extras.getString("SOT_str_sscq"));
                    sl_looseQty.setText(extras.getString("SOT_str_sslq"));
                    sl_qty.setText(extras.getString("SOT_str_ssqty"));

                    oldQty = extras.getString("SOT_str_ssqty");

                    sl_foc.setText(extras.getString("SOT_str_ssfoc"));
                    sl_price.setText(extras.getString("SOT_str_ssprice"));
                    sl_itemDiscount.setText(extras.getString("SOT_str_ssdisc"));
                    sl_uom.setText(extras.getString("SOT_str_ssuom"));
                    sl_cartonPerQty.setText(extras.getString("SOT_st_sscpqty"));
                    sl_total.setText(extras.getString("SOT_st_sstotal"));

                    String netTotal = extras.getString("SOT_st_ssnettot");
                    String tax = extras.getString("SOT_st_sstax");
                    taxType = extras.getString("SOT_str_sstaxtype");
                    // Added New 13.04.2017
                    if (taxType != null && !taxType.isEmpty()) {
                        if (taxType.matches("I")) {
                            double dTax = tax.equals("") ? 0 : Double.valueOf(tax);
                            double dNetTotal = netTotal.equals("") ? 0 : Double.valueOf(netTotal);
                            double dTotalIncl = dNetTotal - dTax;
                            String totIncl = twoDecimalPoint(dTotalIncl);
                            sl_total_inclusive.setText(totIncl);
                        }else{
                            sl_total_inclusive.setText(extras.getString("SOT_st_sstotal"));
                        }
                    }else{
                        sl_total_inclusive.setText(extras.getString("SOT_st_sstotal"));
                    }

                    sl_tax.setText(tax);

                    //sl_tax.setText(extras.getString("SOT_st_sstax"));

                    sl_cprice.setText(extras.getString("SOT_str_sscprice"));
                    sl_exchange.setText(extras.getString("SOT_str_ssexchqty"));

                    taxType = extras.getString("SOT_str_sstaxtype");
                    taxValue = extras.getString("SOT_st_sstaxvalue");
                    slCartonPerQty = extras.getString("SOT_st_sscpqty");

                    MinimumSellingPrice = extras.getString("SOT_str_minselling_price");

                    MinimumCartonSellingPrice = extras.getString("SOT_str_minCartonselling_price");

                    Log.d("MinimumSellingPrice", "MinimumSellingPrice"
                            + MinimumSellingPrice);

                    Log.d("MinCartonSePrice", "MinCartonSelgPrice"
                            + MinimumCartonSellingPrice);

                    sl_netTotal.setText(netTotal);
                    //sl_netTotal.setText(extras.getString("SOT_st_ssnettot"));
                    str_sssno = extras.getString("SOT_ssno");
                    str_ssupdate = extras.getString("SOT_ssupdate");
                    str_sscancel = extras.getString("SOT_sscancel");
                    if ((str_ssupdate != null) || (str_sscancel != null)) {
                        sl_addProduct.setText(str_ssupdate);
                        sl_summary.setText(str_sscancel);
                    }
                    if (intentString != null && !intentString.isEmpty()) {

//                        invoice_carton_header.setVisibility(View.GONE);
                    } else {
//                        invoice_carton_header.setVisibility(View.VISIBLE);

                        if (calCarton.matches("0")) {

                        } else {
                            if (slCartonPerQty.matches("1")
                                    || slCartonPerQty.matches("0")
                                    || slCartonPerQty.matches("")) {

                                sl_cartonQty.setFocusable(false);
                                sl_cartonQty.setBackgroundResource(drawable.labelbg);

                                sl_looseQty.setFocusable(false);
                                sl_looseQty.setBackgroundResource(drawable.labelbg);

                            } else {
                                sl_cartonQty.setFocusableInTouchMode(true);
                                sl_cartonQty
                                        .setBackgroundResource(drawable.edittext_bg);

                                sl_looseQty.setFocusableInTouchMode(true);
                                sl_looseQty
                                        .setBackgroundResource(drawable.edittext_bg);
                            }
                        }
                    }
                    Log.d("Carton per Qty", "" + slCartonPerQty);

                    // Edit product with batch
                    String pCode = extras.getString("SOT_ssproductcode");
                    Cursor pCursor = SOTDatabase.getBatchCursor(pCode);
                    int pCount = pCursor.getCount();
                    if (pCount > 0) {
                        sl_cartonQty.setFocusable(false);
                        sl_cartonQty.setBackgroundResource(drawable.labelbg);

                        sl_looseQty.setFocusable(false);
                        sl_looseQty.setBackgroundResource(drawable.labelbg);

                        sl_qty.setFocusable(false);
                        sl_qty.setBackgroundResource(drawable.labelbg);

                        sl_foc.setFocusable(false);
                        sl_foc.setBackgroundResource(drawable.labelbg);

                    }

            } else {

            oldQty = "0";

//            intentString = extras.getString("Consignment");

            sales_prodCode = extras.getString("SOT_ssproductcode");

            Log.d("sales_prodCode", "-->" + sales_prodCode);

            ProductStockAsyncCall task = new ProductStockAsyncCall();
            task.execute();
            id = extras.getString("SOT_ssno");
            slNo = extras.getString("SOT_slNo");

            prices_txt = extras.getString("SOT_str_ssprice");
            carton_txt = extras.getString("SOT_str_sscq");
            loose_txt = extras.getString("SOT_str_sslq");
            qty_txt = extras.getString("SOT_str_ssqty");
            pcs_txt = extras.getString("SOT_st_sscpqty");

            Log.d("cartonTxt", " -->" + carton_txt + " Loose" + loose_txt + " qty" + extras.getString("SOT_str_ssqty") + "pcs" + pcs_txt);

            sl_codefield.setText(extras.getString("SOT_ssproductcode"));
            sl_namefield.setText(extras.getString("SOT_str_ssprodname"));
            sl_cartonQty.setText(extras.getString("SOT_str_sscq"));
            sl_looseQty.setText(extras.getString("SOT_str_sslq"));
            sl_qty.setText(extras.getString("SOT_str_ssqty"));
            sl_foc.setText(extras.getString("SOT_str_ssfoc"));
            oldQty = extras.getString("SOT_str_ssqty");
            sl_price.setText(extras.getString("SOT_str_ssprice"));
            sl_itemDiscount.setText(extras.getString("SOT_str_ssdisc"));
            sl_uom.setText(extras.getString("SOT_str_ssuom"));
            sl_cartonPerQty.setText(extras.getString("SOT_st_sscpqty"));
            sl_total.setText(extras.getString("SOT_st_sstotal"));
            sl_tax.setText(extras.getString("SOT_st_sstax"));
            String netTotal = extras.getString("SOT_st_ssnettot");
            String tax = extras.getString("SOT_st_sstax");
            taxType = extras.getString("SOT_str_sstaxtype");
//            flag=true;


//            hashMap =(HashMap<String,EditText>) getIntent().getExtras().get("Hashmap");

            sl_tax.setText(tax);

            sl_cprice.setText(extras.getString("SOT_str_sscprice"));
            sl_exchange.setText(extras.getString("SOT_str_ssexchqty"));

            taxType = extras.getString("SOT_str_sstaxtype");
            taxValue = extras.getString("SOT_st_sstaxvalue");
            slCartonPerQty = extras.getString("SOT_st_sscpqty");

            MinimumSellingPrice = extras.getString("SOT_str_minselling_price");
            MinimumCartonSellingPrice = extras.getString("SOT_str_minCartonselling_price");

            sl_delivery_qty = extras.getString("SOT_str_deli_qty");

            Log.d("sl_delivery_qty", "--->" + sl_delivery_qty);

            Log.d("MinimumSellingPrice", "MinimumSellingPrice"
                    + MinimumSellingPrice);

            sl_netTotal.setText(netTotal);
            //sl_netTotal.setText(extras.getString("SOT_st_ssnettot"));
            str_sssno = extras.getString("SOT_ssno");
            str_ssupdate = extras.getString("SOT_ssupdate");
            str_sscancel = extras.getString("SOT_sscancel");
            Log.d("str_ssupdate", "-->" + str_ssupdate);

            if ((str_ssupdate != null) || (str_sscancel != null)) {
                sl_addProduct.setText(str_ssupdate);
                sl_summary.setText(str_sscancel);
            }

            // Added New 13.04.2017
            if (taxType != null && !taxType.isEmpty()) {
                if (taxType.matches("I")) {
                    double dTax = tax.equals("") ? 0 : Double.valueOf(tax);
                    double dNetTotal = netTotal.equals("") ? 0 : Double.valueOf(netTotal);
                    double dTotalIncl = dNetTotal - dTax;
                    String totIncl = twoDecimalPoint(dTotalIncl);
                    sl_total_inclusive.setText(totIncl);
                } else {
                    sl_total_inclusive.setText(extras.getString("SOT_st_sstotal"));
                }
            } else {
                sl_total_inclusive.setText(extras.getString("SOT_st_sstotal"));
            }


//            HashMap<String,EditText> hm = (HashMap<String,EditText>) getIntent().getExtras().get("hashMapKey");
//            this.sm_total=extras.getParcelable("sm_total");
//            this.sm_total_new=extras.getParcelable("sm_total_new");
//            this.sm_itemDisc=extras.getParcelable("sm_itemDisc");
//            this.sm_subTotal=extras.getParcelable("sm_subTotal");
//            this.sm_tax=extras.getParcelable("sm_tax");
//            this.sm_netTotal=extras.getParcelable("sm_netTotal");
//            this.sm_subTotal_inclusive=extras.getParcelable("sm_subTotal_inclusive");


            flag = true;

            new AsyncCallSaleProduct().execute();

            if (intentString != null && !intentString.isEmpty()) {

                delivery_carton_header.setVisibility(View.GONE);

            } else {

                delivery_carton_header.setVisibility(View.VISIBLE);
                if (calCarton.matches("0")) {

                } else {
                    if (slCartonPerQty.matches("1")
                            || slCartonPerQty.matches("0")
                            || slCartonPerQty.matches("")) {

                        sl_cartonQty.setFocusable(false);
                        sl_cartonQty.setBackgroundResource(drawable.labelbg);

                        sl_looseQty.setFocusable(false);
                        sl_looseQty.setBackgroundResource(drawable.labelbg);

                    } else {
                        sl_cartonQty.setFocusableInTouchMode(true);
                        sl_cartonQty
                                .setBackgroundResource(drawable.edittext_bg);

                        sl_looseQty.setFocusableInTouchMode(true);
                        sl_looseQty
                                .setBackgroundResource(drawable.edittext_bg);
                    }
                }
            }
            if ((str_ssupdate != null) || (str_sscancel != null)) {
                sl_codefield.setEnabled(false);
                sl_codefield.setFocusable(false);
                sl_codefield.setFocusableInTouchMode(false);
                sl_codefield
                        .setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            // Edit product with stock
            String pCode = extras.getString("SOT_ssproductcode");
            Cursor pCursor = SOTDatabase.getConsignmentStocks(pCode);
            Log.d("pCursorSize", "-->" + pCursor.getCount());
            int pCount = pCursor.getCount();
            if (pCount > 0) {
                sl_cartonQty.setFocusable(false);
                sl_cartonQty.setBackgroundResource(drawable.labelbg);

                sl_looseQty.setFocusable(false);
                sl_looseQty.setBackgroundResource(drawable.labelbg);

                sl_qty.setFocusable(false);
                sl_qty.setBackgroundResource(drawable.labelbg);

                sl_foc.setFocusable(false);
                sl_foc.setBackgroundResource(drawable.labelbg);

            }

        }
        }

        getSalesProdArr.clear();
        companyArr.clear();
        searchProductArr.clear();

        Log.d("DOTaxT", SalesOrderSetGet.getCompanytax());
        taxType = SalesOrderSetGet.getCompanytax();

        cstcode = SalesOrderSetGet.getSuppliercode();

        cstgrpcd = SalesOrderSetGet.getSuppliergroupcode();
        // Added New 13.04.2017
        if (taxType != null && !taxType.isEmpty()) {
            if (taxType.matches("I")) {
                sl_total.setVisibility(View.GONE);
                sl_total_inclusive.setVisibility(View.VISIBLE);
            }else{
                sl_total.setVisibility(View.VISIBLE);
                sl_total_inclusive.setVisibility(View.GONE);
            }
        }else{
            sl_total.setVisibility(View.VISIBLE);
            sl_total_inclusive.setVisibility(View.GONE);
        }
		/*
		 * AsyncCallGetCompany cmyTask = new AsyncCallGetCompany();
		 * cmyTask.execute();
		 */
		/*
		 * AsyncCallWSADDPRD proTask = new AsyncCallWSADDPRD();
		 * proTask.execute(); AsyncCallBARCODE barcodetask = new
		 * AsyncCallBARCODE(); barcodetask.execute();
		 */
        new GetProduct().execute();

        if (FormSetterGetter.isEditPrice()) {
            pricetag.setVisibility(View.GONE);
            sl_price.setEnabled(true);
            sl_price.setBackgroundResource(drawable.edittext_bg);
            sl_price.setFocusableInTouchMode(true);

            sl_cprice.setEnabled(true);
            sl_cprice.setBackgroundResource(drawable.edittext_bg);
            sl_cprice.setFocusableInTouchMode(true);
        } else {
            pricetag.setVisibility(View.VISIBLE);
            // sl_price.setKeyListener(null);
            sl_price.setEnabled(false);
            sl_price.setFocusable(false);
            sl_price.setFocusableInTouchMode(false);
            sl_price.setGravity(Gravity.CENTER);
            sl_price.setBackgroundResource(drawable.labelbg);

            sl_cprice.setEnabled(false);
            sl_cprice.setFocusable(false);
            sl_cprice.setFocusableInTouchMode(false);
            sl_cprice.setGravity(Gravity.CENTER);
            sl_cprice.setBackgroundResource(drawable.labelbg);
        }

        filter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                filtercs.filterDialog();

                filtercs.OnFilterCompletionListener(new FilterCS.OnFilterCompletionListener() {

                    @Override
                    public void onFilterCompleted(String category,
                                                  String subcategory) {

                        catStr = category;
                        subCatStr = subcategory;

                        Log.d("catStr", "----->" + catStr);
                        Log.d("subCatStr", "-->" + subCatStr);

                        new AsyncCallWSSearchProduct().execute();
                    }
                });

            }
        });

        listing_screen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Cursor cursor = SOTDatabase.getCursor();
                int count = cursor.getCount();
                if (count > 0) {
                    alertDialog();
                } else {

                    Intent i = new Intent(ConsignmentAddProduct.this,
                            ConsignmentHeader.class);
                    startActivity(i);
                    ConsignmentAddProduct.this.finish();

                }

            }
        });

        customer_screen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(ConsignmentAddProduct.this,
                        ConsignmentCustomer.class);
                startActivity(i);
                ConsignmentAddProduct.this.finish();

            }
        });

        summary_screen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(ConsignmentAddProduct.this,
                        ConsignmentSummary.class);
                startActivity(i);

                ConsignmentAddProduct.this.finish();

            }
        });

        pricetag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                new SOTAdmin(ConsignmentAddProduct.this, sl_price,
                        salesproduct_layout);
            }
        });
        productFilterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long id) {

                sales_prodCode = searchProductArr.get(position).get(
                        "ProductCode");
                sl_codefield.setText(sales_prodCode);
                loadProgress();
                new AsyncCallSaleProduct().execute();
            }
        });

        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (foc_layout.getVisibility() == View.VISIBLE) {
                    // Its visible
                    foc_layout.setVisibility(View.GONE);
                    foc_header_layout.setVisibility(View.GONE);
                    pcs_txt_layout.setVisibility(View.GONE);
                    pcs_layout.setVisibility(View.GONE);
                } else {
                    foc_layout.setVisibility(View.VISIBLE);
                    foc_header_layout.setVisibility(View.VISIBLE);
                    pcs_txt_layout.setVisibility(View.VISIBLE);
                    pcs_layout.setVisibility(View.VISIBLE);
                    // Either gone or invisible
                }

            }
        });

        sl_addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {



                if ((str_ssupdate != null) || (str_sscancel != null)) {
                    Log.d("MinimumSellingPrice", "" + MinimumSellingPrice+" "+MinimumCartonSellingPrice);
                    double slprice = 0, minSellingPrice = 0,miniCartonSellingPrice=0,slcprice=0,piecePerCarton=0;
                    String price = sl_price.getText().toString();
                    String cprice = sl_cprice.getText().toString();

                    if (!price.matches("")) {
                        slprice = Double.parseDouble(price);
                    }
//                    if(MinimumSellingPrice.equals(null)){
//                        minSellingPrice=0.00;
//                    }
//                    else if (MinimumSellingPrice.matches("0.00")) {
//                        minSellingPrice = Double
//                                .parseDouble(MinimumSellingPrice);
//                    }else{
//                        minSellingPrice = Double
//                                .parseDouble(MinimumSellingPrice);
//                    }
//                   if (MinimumCartonSellingPrice.matches("0.00")) {
//                        miniCartonSellingPrice = Double
//                                .parseDouble(MinimumCartonSellingPrice);
//                    }else if(!MinimumCartonSellingPrice.matches("")){
//                        miniCartonSellingPrice = Double
//                                .parseDouble(MinimumCartonSellingPrice);
//                    }else{
//                       miniCartonSellingPrice =0.00;
//                   }
                    if (!cprice.matches("")) {
                        slcprice = Double.parseDouble(cprice);
                    }
                    if (slCartonPerQty!=null && !slCartonPerQty.isEmpty() ) {
                        piecePerCarton = Double.parseDouble(slCartonPerQty);
                    }
                    Log.d("slcprice", "cp "+slcprice);

                    if (priceflag.matches("1")) {
					/*	if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
								|| slCartonPerQty.matches("")) {
							if (minSellingPrice > slcprice) {
								Toast.makeText(
										InvoiceAddProduct.this,
										"Price must be greater than minimum selling price $ "
												+ MinimumSellingPrice + " pp" + slcprice,
										Toast.LENGTH_LONG).show();
							} else {
								storeInDatabase();
							}
						}else{
							storeInDatabase();
						}*/
                        if (miniCartonSellingPrice > slcprice) {
                            sl_cprice.requestFocus();
                            Toast.makeText(
                                    ConsignmentAddProduct.this,
                                    "Carton Price must be greater than Minimum carton selling price $ "
                                            + MinimumCartonSellingPrice,
                                    Toast.LENGTH_LONG).show();
                        } else if (piecePerCarton>1){
                            if(minSellingPrice > slprice) {
                                sl_price.requestFocus();
                                Toast.makeText(
                                        ConsignmentAddProduct.this,
                                        "Price must be greater than minimum selling price $ "
                                                + MinimumSellingPrice,
                                        Toast.LENGTH_LONG).show();
                            }else{
                                storeInDatabase();
								/*Toast.makeText(InvoiceAddProduct.this, "Updated ",
										Toast.LENGTH_LONG).show();
								Intent i = new Intent(InvoiceAddProduct.this,
										InvoiceSummary.class);
								startActivity(i);
								InvoiceAddProduct.this.finish();*/
                            }
                        }

                        else {
                            storeInDatabase();
							/*Toast.makeText(InvoiceAddProduct.this, "Updated ",
									Toast.LENGTH_LONG).show();
							Intent i = new Intent(InvoiceAddProduct.this,
									InvoiceSummary.class);
							startActivity(i);
							InvoiceAddProduct.this.finish();*/
                        }
                    } else {
                        if (minSellingPrice > slprice) {
                            Toast.makeText(
                                    ConsignmentAddProduct.this,
                                    "Price must be greater than minimum selling price $ "
                                            + MinimumSellingPrice,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            storeInDatabase();
							/*Toast.makeText(InvoiceAddProduct.this, "Updated ",
									Toast.LENGTH_LONG).show();
							Intent i = new Intent(InvoiceAddProduct.this,
									InvoiceSummary.class);
							startActivity(i);
							InvoiceAddProduct.this.finish();*/

                        }
                    }


                } else {
                    Log.d("MinimumSellingPrices", MinimumSellingPrice);
                    double slprice = 0,piecePerCarton=0, minSellingPrice = 0,slcprice=0,miniCartonSellingPrice=0;
                    String price = sl_price.getText().toString();
                    String cprice = sl_cprice.getText().toString();
                    if (!price.matches("")) {
                        slprice = Double.parseDouble(price);
                    }
                    if (!MinimumSellingPrice.matches("")) {
                        minSellingPrice = Double
                                .parseDouble(MinimumSellingPrice);
                    }
                    if (!cprice.matches("")) {
                        slcprice = Double.parseDouble(cprice);
                    }
                    if (!MinimumCartonSellingPrice.matches("")) {
                        miniCartonSellingPrice = Double
                                .parseDouble(MinimumCartonSellingPrice);
                    }
                    if (slCartonPerQty!=null && !slCartonPerQty.isEmpty() ) {
                        piecePerCarton = Double.parseDouble(slCartonPerQty);
                    }
                    if (priceflag.matches("1")) {
						/*if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
								|| slCartonPerQty.matches("")) {
							if (minSellingPrice > slcprice) {
								Toast.makeText(
										InvoiceAddProduct.this,
										"Price must be greater than minimum selling price $ "
												+ MinimumSellingPrice,
										Toast.LENGTH_LONG).show();
							} else {
								storeInDatabase();
							}
						}else{
							storeInDatabase();
						}*/
                        if (miniCartonSellingPrice > slcprice) {
                            sl_cprice.requestFocus();
                            Toast.makeText(
                                    ConsignmentAddProduct.this,
                                    "Carton Price must be greater than Minimum carton selling price $ "
                                            + MinimumCartonSellingPrice,
                                    Toast.LENGTH_LONG).show();
                        } else if (piecePerCarton>1){
                            if(minSellingPrice > slprice) {
                                sl_price.requestFocus();
                                Toast.makeText(
                                        ConsignmentAddProduct.this,
                                        "Price must be greater than minimum selling price $ "
                                                + MinimumSellingPrice,
                                        Toast.LENGTH_LONG).show();
                            }else{
                                storeInDatabase();
                            }
                        }

                        else {
                            storeInDatabase();
                        }
                    } else {
                        if (minSellingPrice > slprice) {
                            Toast.makeText(
                                    ConsignmentAddProduct.this,
                                    "Price must be greater than minimum selling price $ "
                                            + MinimumSellingPrice,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            storeInDatabase();

                        }
                    }
                }

            }
        });

        sl_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent i = new Intent(ConsignmentAddProduct.this,
                        ConsignmentSummary.class);
                startActivity(i);
                ConsignmentAddProduct .this.finish();
            }
        });

        sl_codefield
                .setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
                        sl_codefield) {
                    @Override
                    public boolean onDrawableClick() {
                        // TODO Auto-generated method stub
                        alertDialogSearch();
                        return true;

                    }
                });

        sl_codefield.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // TODO Auto-generated method stub

                    new CheckProductBarcode().execute();
                    return true;
                }
                return false;
            }
        });


        sl_cartonQty.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (sl_cartonQty.getText().toString().equals("0.00")
                        || sl_cartonQty.getText().toString().equals("0")
                        || sl_cartonQty.getText().toString().equals("0.0")
                        || sl_cartonQty.getText().toString().equals(".0")
                        || sl_cartonQty.getText().toString().equals("0.000")
                        || sl_cartonQty.getText().toString().equals("0.0000")
                        || sl_cartonQty.getText().toString().equals("0.00000")) {
                    sl_cartonQty.setText("");
                }
                return false;
            }
        });

        sl_cartonQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    slPrice = sl_price.getText().toString();

                    if (calCarton.matches("0")) {
                        if (priceflag.matches("0")) {
                            cartonQtyPcsOne(0);
                        } else if (priceflag.matches("1")) {
//							cartonQtyNew();
                            cartonQtyPcsOne(1);
                        }
                    } else {
                        if (priceflag.matches("0")) {
                            cartonQty();
                        } else if (priceflag.matches("1")) {
                            cartonQtyNew();
                        }
                    }

                    sl_looseQty.requestFocus();
                    return true;
                }
                return false;
            }
        });

        cqtyTW = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                ss_Cqty = sl_cartonQty.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                slPrice = sl_price.getText().toString();

                if (calCarton.matches("0")) {
                    if (priceflag.matches("0")) {
                        cartonQtyPcsOne(0);
                    } else if (priceflag.matches("1")) {
//						cartonQtyNew();
                        cartonQtyPcsOne(1);
                    }

                    int length = sl_cartonQty.length();
                    if (length == 0) {
                        if (priceflag.matches("0")) {
                            cartonQtyPcsOne(0);
                        } else if (priceflag.matches("1")) {
//						cartonQtyNew();
                            cartonQtyPcsOne(1);
                        }
                    }
                } else {
                    if (priceflag.matches("0")) {
                        cartonQty();
                    } else if (priceflag.matches("1")) {
                        cartonQtyNew();
                    }

                    int length = sl_cartonQty.length();
                    if (length == 0) {
                        if (calCarton.matches("0")) {

                        } else {
                            String lqty = sl_looseQty.getText().toString();

                            if (lqty.matches("")) {
                                lqty = "0";
                            }

                            if (!lqty.matches("")) {
                                sl_qty.removeTextChangedListener(qtyTW);
                                sl_qty.setText(lqty);
                                sl_qty.addTextChangedListener(qtyTW);

                                if (sl_qty.length() != 0) {
                                    sl_qty.setSelection(sl_qty.length());
                                }

                                double lsQty = Double.parseDouble(lqty);

                                if (priceflag.matches("0")) {
                                    productTotal(lsQty);
                                } else if (priceflag.matches("1")) {
                                    // String cprice =
                                    // sl_price.getText().toString();
                                    productTotalNew();
                                }
                            }
                        }
                    }
                }
            }
        };

        sl_looseQty.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (sl_looseQty.getText().toString().equals("0.00")
                        || sl_looseQty.getText().toString().equals("0")
                        || sl_looseQty.getText().toString().equals("0.0")
                        || sl_looseQty.getText().toString().equals(".0")
                        || sl_looseQty.getText().toString().equals("0.000")
                        || sl_looseQty.getText().toString().equals("0.0000")
                        || sl_looseQty.getText().toString().equals("0.00000")) {
                    sl_looseQty.setText("");
                }
                return false;
            }
        });

        sl_looseQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    slPrice = sl_price.getText().toString();

                    if (calCarton.matches("0")) {

                        if (priceflag.matches("0")) {
                            looseQtyCalcPcsOne(0);
                        } else if (priceflag.matches("1")) {
//							looseQtyCalcNew();
                            looseQtyCalcPcsOne(1);
                        }
                    } else {

                        if (priceflag.matches("0")) {
                            looseQtyCalc();
                        } else if (priceflag.matches("1")) {
                            looseQtyCalcNew();
                        }
                    }

                    sl_qty.requestFocus();

                    return true;
                }
                return false;
            }
        });

        lqtyTW = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                beforeLooseQty = sl_looseQty.getText().toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                slPrice = sl_price.getText().toString();

                if (calCarton.matches("0")) {
                    if (priceflag.matches("0")) {
                        looseQtyCalcPcsOne(0);
                    } else if (priceflag.matches("1")) {
//						looseQtyCalcNew();
                        looseQtyCalcPcsOne(1);
                    }
                } else {
                    if (priceflag.matches("0")) {
                        looseQtyCalc();
                    } else if (priceflag.matches("1")) {
                        looseQtyCalcNew();
                    }

                    int length = sl_looseQty.length();
                    if (length == 0) {

                        if (calCarton.matches("0")) {

                        } else {
                            String qty = sl_qty.getText().toString();
                            if (!beforeLooseQty.matches("") && !qty.matches("")) {

                                double qtyCnvrt = Double.parseDouble(qty);
                                double lsCnvrt = Double
                                        .parseDouble(beforeLooseQty);
                                sl_qty.removeTextChangedListener(qtyTW);
                                sl_qty.setText("" + (qtyCnvrt - lsCnvrt));
                                sl_qty.addTextChangedListener(qtyTW);

                                if (sl_qty.length() != 0) {
                                    sl_qty.setSelection(sl_qty.length());
                                }

                                if (priceflag.matches("0")) {
                                    looseQtyCalc();
                                } else if (priceflag.matches("1")) {
                                    looseQtyCalcNew();
                                }
                            }
                        }
                    }
                }
            }

        };

        sl_qty.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (sl_qty.getText().toString().equals("0.00")
                        || sl_qty.getText().toString().equals("0")
                        || sl_qty.getText().toString().equals("0.0")
                        || sl_qty.getText().toString().equals(".0")
                        || sl_qty.getText().toString().equals("0.000")
                        || sl_qty.getText().toString().equals("0.0000")
                        || sl_qty.getText().toString().equals("0.00000")) {
                    sl_qty.setText("");
                }
                return false;
            }
        });

        sl_qty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    slPrice = sl_price.getText().toString();
                    String qty = sl_qty.getText().toString();
                    if (!qty.matches("")) {
                        double qtyCalc = Double.parseDouble(qty);
                        // productTotal(qtyCalc);
                        if (calCarton.matches("0")) {
                            String pcsPerCarton = sl_cartonPerQty.getText()
                                    .toString();
                            if (!pcsPerCarton.matches("")) {
                                double pcsPerCartonCalc = Double
                                        .parseDouble(pcsPerCarton);
                                if (pcsPerCartonCalc == 1) {
                                    productTotal(qtyCalc);
                                }
                            }

                        } else {
                            clQty();
                        }
                    }

                    if (priceflag.matches("1")) {

                        if (sl_cprice.getText().toString().equals("0.00")
                                || sl_cprice.getText().toString().equals("0")
                                || sl_cprice.getText().toString().equals("0.0")
                                || sl_cprice.getText().toString().equals(".0")
                                || sl_cprice.getText().toString()
                                .equals("0.000")
                                || sl_cprice.getText().toString()
                                .equals("0.0000")
                                || sl_cprice.getText().toString()
                                .equals("0.00000")) {
                            sl_cprice.setText("");
                        }
                        sl_cprice.requestFocus();

                    } else {
                        if (sl_price.getText().toString().equals("0.00")
                                || sl_price.getText().toString().equals("0")
                                || sl_price.getText().toString().equals("0.0")
                                || sl_price.getText().toString().equals(".0")
                                || sl_price.getText().toString()
                                .equals("0.000")
                                || sl_price.getText().toString()
                                .equals("0.0000")
                                || sl_price.getText().toString()
                                .equals("0.00000")) {
                            sl_price.setText("");
                        }
                        sl_price.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });

        qtyTW = new TextWatcher() {

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

                slPrice = sl_price.getText().toString();
                String qty = sl_qty.getText().toString();
//                String qty ="3.00";
                if (qty.matches("")) {
                    qty = "0";
                }

                if (!qty.matches("")) {

                    if (calCarton.matches("0")) {

                        if (priceflag.matches("0")) {

                            Log.d("priceflag", "" + priceflag);

                            if (!qty.matches("")) {
                                double quantity = Double.parseDouble(qty);
                                productTotal(quantity);

                                int length = sl_qty.length();
                                if (length == 0) {
                                    if (calCarton.matches("0")) {
                                        productTotal(0);
                                    } else {
                                        productTotal(0);

                                        sl_cartonQty
                                                .removeTextChangedListener(cqtyTW);
                                        sl_cartonQty.setText("");
                                        sl_cartonQty
                                                .addTextChangedListener(cqtyTW);

                                        sl_looseQty
                                                .removeTextChangedListener(lqtyTW);
                                        sl_looseQty.setText("");
                                        sl_looseQty
                                                .addTextChangedListener(lqtyTW);
                                    }
                                }
                            }
                        } else if (priceflag.matches("1")) {
                            String pcsPerCarton = sl_cartonPerQty.getText()
                                    .toString();
                            if (!pcsPerCarton.matches("")) {
                                double pcsPerCartonCalc = Double
                                        .parseDouble(pcsPerCarton);
                                if (pcsPerCartonCalc == 1) {

                                    if (!qty.matches("")) {
                                        double quantity = Double
                                                .parseDouble(qty);
                                        productTotal(quantity);
                                    }

                                }
                            }

                            int length = sl_qty.length();
                            if (length == 0) {
                                if (calCarton.matches("0")) {

                                } else {
                                    productTotal(0);

                                    sl_cartonQty
                                            .removeTextChangedListener(cqtyTW);
                                    sl_cartonQty.setText("");
                                    sl_cartonQty.addTextChangedListener(cqtyTW);

                                    sl_looseQty
                                            .removeTextChangedListener(lqtyTW);
                                    sl_looseQty.setText("");
                                    sl_looseQty.addTextChangedListener(lqtyTW);
                                }
                            }

                        }

                    } else {

                        Log.d("elseWhere", "" + qty);

                        if (!qty.matches("")) {
                            clQty();
                        }

                        int length = sl_qty.length();
                        if (length == 0) {
                            if (calCarton.matches("0")) {

                            } else {
                                productTotal(0);

                                sl_cartonQty.removeTextChangedListener(cqtyTW);
                                sl_cartonQty.setText("");
                                sl_cartonQty.addTextChangedListener(cqtyTW);

                                sl_looseQty.removeTextChangedListener(lqtyTW);
                                sl_looseQty.setText("");
                                sl_looseQty.addTextChangedListener(lqtyTW);
                            }
                        }
                    }

                }

            }

        };

        sl_foc.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            // public boolean onEditorAction(TextView v, int actionId,
            // KeyEvent event) {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    sl_exchange.requestFocus();
                    return true;
                }
                return false;
            }
        });

        sl_foc.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // beforeFoc = sl_foc.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // focCalc();

                // int length = sl_foc.length();
                // if (length == 0) {
                // String qty = sl_qty.getText().toString();
                // if (beforeFoc.matches("") && !qty.matches("")) {
                //
                // int focCnvrt = Integer.parseInt(beforeFoc);
                // int qtyCnvrt = Integer.parseInt(qty);
                // sl_foc.setText("" + (qtyCnvrt - focCnvrt));
                //
                // }
                //
                // }
            }

        });

        sl_price.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    String qty = sl_qty.getText().toString();
                    if (calCarton.matches("0")) {
                        if (priceflag.matches("0")) {
                            if (!qty.matches("")) {
                                int quantity = Integer.parseInt(qty);
                                productTotal(quantity);
                            }
                        } else if (priceflag.matches("1")) {
                            productTotalNew();
                        }
                    } else {
                        if (!qty.matches("")) {
                            double qtyCalc = Double.parseDouble(qty);
                            slPrice = sl_price.getText().toString();
                            if (priceflag.matches("0")) {
                                productTotal(qtyCalc);
                            } else if (priceflag.matches("1")) {
                                productTotalNew();
                            }
                        }
                    }
                    sl_foc.requestFocus();
                    return true;
                }
                return false;
            }
        });
        sl_price.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (sl_price.getText().toString().equals("0.00")
                        || sl_price.getText().toString().equals("0")
                        || sl_price.getText().toString().equals("0.0")
                        || sl_price.getText().toString().equals(".0")
                        || sl_price.getText().toString().equals("0.000")
                        || sl_price.getText().toString().equals("0.0000")
                        || sl_price.getText().toString().equals("0.00000")) {
                    sl_price.setText("");
                }
                return false;
            }
        });
        sl_price.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                slPrice = sl_price.getText().toString();
                String qty = sl_qty.getText().toString();
                if (calCarton.matches("0")) {
                    if (priceflag.matches("0")) {
                        if (!qty.matches("")) {

                            int quantity = Integer.parseInt(qty);
                            productTotal(quantity);
                        }
                    } else if (priceflag.matches("1")) {
                        productTotalNew();
                    }
                } else {
                    if (!qty.matches("")) {
                        double qtyCalc = Double.parseDouble(qty);
                        slPrice = sl_price.getText().toString();
                        if (priceflag.matches("0")) {
                            productTotal(qtyCalc);
                        } else if (priceflag.matches("1")) {
                            productTotalNew();
                        }
                    }
                }
            }

        });

        sl_cprice.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    String qty = sl_qty.getText().toString();

                    if (sl_price.getText().toString().equals("0.00")
                            || sl_price.getText().toString().equals("0")
                            || sl_price.getText().toString().equals("0.0")
                            || sl_price.getText().toString().equals(".0")
                            || sl_price.getText().toString().equals("0.000")
                            || sl_price.getText().toString().equals("0.0000")
                            || sl_price.getText().toString().equals("0.00000")) {
                        sl_price.setText("");
                    }
                    if (calCarton.matches("0")) {
                        if (priceflag.matches("0")) {

                        } else if (priceflag.matches("1")) {
                            productTotalNew();
                        }
                    } else {
                        if (!qty.matches("")) {
                            double qtyCalc = Double.parseDouble(qty);
                            String cPrice = sl_cprice.getText().toString();

                            if (priceflag.matches("0")) {
                                productTotal(qtyCalc);
                            } else if (priceflag.matches("1")) {
                                productTotalNew();
                            }
                        }
                    }
                    sl_price.requestFocus();
                    return true;
                }
                return false;
            }
        });
        sl_cprice.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (sl_cprice.getText().toString().equals("0.00")
                        || sl_cprice.getText().toString().equals("0")
                        || sl_cprice.getText().toString().equals("0.0")
                        || sl_cprice.getText().toString().equals(".0")
                        || sl_cprice.getText().toString().equals("0.000")
                        || sl_cprice.getText().toString().equals("0.0000")
                        || sl_cprice.getText().toString().equals("0.00000")) {
                    sl_cprice.setText("");
                }
                return false;
            }
        });
        sl_cprice.addTextChangedListener(new TextWatcher() {

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

                String qty = sl_qty.getText().toString();

                if (calCarton.matches("0")) {
                    if (priceflag.matches("0")) {

                    } else if (priceflag.matches("1")) {
                        productTotalNew();
                    }
                } else {

                    if (!qty.matches("")) {
                        double qtyCalc = Double.parseDouble(qty);
                        String cPrice = sl_cprice.getText().toString();

                        if (priceflag.matches("0")) {
                            productTotal(qtyCalc);
                        } else if (priceflag.matches("1")) {
                            productTotalNew();
                        }
                    }
                }
            }

        });

        sl_itemDiscount.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_DONE) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            sl_itemDiscount.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        sl_itemDiscount.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                slPrice = sl_price.getText().toString();
                if (priceflag.matches("0")) {
                    itemDiscountCalc();
                } else if (priceflag.matches("1")) {
                    itemDiscountCalcNew();
                }

            }
        });

        sl_cartonQty.addTextChangedListener(cqtyTW);
        sl_looseQty.addTextChangedListener(lqtyTW);
        sl_qty.addTextChangedListener(qtyTW);
    }

    public void doQtyValidateWithSo(Intent i) {
        if (ConvertToSetterGetter.getSoNo() != null
                && !ConvertToSetterGetter.getSoNo().isEmpty()
                || ConvertToSetterGetter.getEdit_do_no() != null
                && !ConvertToSetterGetter.getEdit_do_no().isEmpty()) {

            if (SalesOrderSetGet.getDoQtyValidateWithSo().matches("1")) {

                int deliveryQty = 0, quantity = 0, currentQty = 0, remainingQty = 0;
                Log.d("slNo", "" + slNo);
                Log.d("productCode", "" + sl_codefield.getText().toString());

                requestQty = sl_qty.getText().toString();

                Cursor cursorEditDO = SOTDatabase.getSODetailQuantity();
                // From DO Edit
                if (cursorEditDO != null && cursorEditDO.getCount() > 0) {

                    String productCode = SOTDatabase.checkDoQtyWithSo("0", "0");
                    if (productCode.matches("0")) {
                        // From DO Edit With Null SoNo
                        deliveryQty = 0;

                        Log.d("deliveryQty", "From DO Edit With Null SoNo-->"
                                + deliveryQty);
                    } else {
                        // From DO Edit With SoNo
                        remainingQty = SOTDatabase.getQuantity(slNo,
                                sl_codefield.getText().toString());
                        currentQty = sl_delivery_qty.equals("") ? 0 : Integer
                                .valueOf(sl_delivery_qty);
                        deliveryQty = remainingQty + currentQty;
                        Log.d("remainingQty", "From DO Edit With SoNo-->"
                                + remainingQty);
                        Log.d("currentQty", "From DO Edit With SoNo-->"
                                + currentQty);

                        Log.d("deliveryQty", "From DO Edit With SoNo-->"
                                + deliveryQty);
                    }

                } else {
                    // Convert SO To Do
                    deliveryQty = sl_delivery_qty.equals("") ? 0 : Integer
                            .valueOf(sl_delivery_qty);

                    Log.d("deliveryQty", "Convert SO To Do-->" + deliveryQty);
                }

                quantity = requestQty.equals("") ? 0 : Integer
                        .valueOf(requestQty);

                Log.d("quantity", "" + quantity);
                if (quantity != 0) {
                    if (deliveryQty != 0) {
                        if (quantity <= deliveryQty) {

                            storeInDatabase();
                            Toast.makeText(ConsignmentAddProduct.this, "Updated ",
                                    Toast.LENGTH_LONG).show();
                            startActivity(i);
                            ConsignmentAddProduct.this.finish();
                        } else {
                            if (deliveryQty < 0) {
                                Toast.makeText(
                                        ConsignmentAddProduct.this,
                                        "Request Quantity is exceed,No more Order Quantity",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(
                                        ConsignmentAddProduct.this,
                                        "Quantity must be less than equal to  "
                                                + deliveryQty,
                                        Toast.LENGTH_LONG).show();

                            }
                            sl_qty.setText("");
                        }
                    } else {
                        if (cursorEditDO != null && cursorEditDO.getCount() > 0) {
                            String productCode = SOTDatabase.checkDoQtyWithSo(
                                    slNo, sl_codefield.getText().toString());
                            if (productCode != null && !productCode.isEmpty()) {
                                Toast.makeText(
                                        ConsignmentAddProduct.this,
                                        "Request Quantity is finished,No more Order Quantity",
                                        Toast.LENGTH_LONG).show();
                                sl_qty.setText("");
                            } else {
                                storeInDatabase();
                                Toast.makeText(ConsignmentAddProduct.this,
                                        "Updated ", Toast.LENGTH_LONG).show();
                                startActivity(i);
                                ConsignmentAddProduct.this.finish();
                            }
                        }
                    }
                } else {
                    Toast.makeText(ConsignmentAddProduct.this,
                            "Please Enter the quantity", Toast.LENGTH_LONG)
                            .show();
                }

            } else {
                storeInDatabase();
                Toast.makeText(ConsignmentAddProduct.this, "Updated ",
                        Toast.LENGTH_LONG).show();
                startActivity(i);
                ConsignmentAddProduct.this.finish();
            }
        } else {
            storeInDatabase();
            Toast.makeText(ConsignmentAddProduct.this, "Updated ",
                    Toast.LENGTH_LONG).show();
            startActivity(i);
            ConsignmentAddProduct.this.finish();
        }
    }

    public void cartonQtyPcsOne(int priceFlag) {
        try{

            String crtnQty = sl_cartonQty.getText().toString();

            if (crtnQty.matches("")){
                crtnQty="0";
            }

            if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

                double cartonQtyCalc = Double.parseDouble(crtnQty);
                double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
                double qty = 0;
                double ProductWeight=0,lsQtyCnvrt=0;
                String lsQty = sl_looseQty.getText().toString();

		/*	if (!lsQty.matches("")) {
				double lsQtyCnvrt = Double.parseDouble(lsQty);
				qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

			} else {
				qty = cartonQtyCalc * cartonPerQtyCalc;
			}*/

                if (!Weight.matches("")) {
                    ProductWeight = Double.parseDouble(Weight);
                }else{
                    ProductWeight=0;
                }

                if (!lsQty.matches("")) {
                    lsQtyCnvrt = Double.parseDouble(lsQty);
                }else{
                    lsQtyCnvrt=0;
                }

                if (cartonPerQtyCalc > 1) {

                    if (ProductWeight > 0) {
                        qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
                    }else{
                        qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
                    }

                }
                else {
                    if (ProductWeight > 0) {
                        qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
                    } else {

                    }
                }


                String quantity = twoDecimalPoint(qty);

                sl_qty.removeTextChangedListener(qtyTW);
                sl_qty.setText("" + quantity);
                sl_qty.addTextChangedListener(qtyTW);

                if (sl_qty.length() != 0) {
                    sl_qty.setSelection(sl_qty.length());
                }

                if(priceFlag==0){
                    productTotal(qty);
                }else{
                    productTotalNew();
                }

            }
        }catch(NumberFormatException e){
            e.printStackTrace();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void looseQtyCalcPcsOne(int priceFlag) {
        try{
            String crtnQty = sl_cartonQty.getText().toString();
            String lsQty = sl_looseQty.getText().toString();

            if (lsQty.matches("")) {
                lsQty = "0";
            }

            if (!slCartonPerQty.matches("") && !crtnQty.matches("")
                    && !lsQty.matches("")) {

                double cartonQtyCalc = Double.parseDouble(crtnQty);
                double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
                double looseQtyCalc = Double.parseDouble(lsQty);
                double qty=0;
                double ProductWeight=0,lsQtyCnvrt=0;
//			qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

                if (!Weight.matches("")) {
                    ProductWeight = Double.parseDouble(Weight);
                }else{
                    ProductWeight=0;
                }

                if (!lsQty.matches("")) {
                    lsQtyCnvrt = Double.parseDouble(lsQty);
                }else{
                    lsQtyCnvrt=0;
                }

                if (cartonPerQtyCalc > 1) {
                    if (ProductWeight > 0) {
                        qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
                    }else{
                        qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
                    }
                }
                else {
                    if (ProductWeight > 0) {
                        qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
                    } else {

                    }
                }

                String quantity = twoDecimalPoint(qty);

                sl_qty.removeTextChangedListener(qtyTW);
                sl_qty.setText("" + quantity);
                sl_qty.addTextChangedListener(qtyTW);

                if (sl_qty.length() != 0) {
                    sl_qty.setSelection(sl_qty.length());
                }

                if(priceFlag==0){
                    productTotal(qty);
                }else{
                    productTotalNew();
                }
            }

            if (!lsQty.matches("")) {


                double looseQtyCalc = Double.parseDouble(lsQty);
                double qty=0;
                double ProductWeight=0,lsQtyCnvrt=0;

			/*if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
				double cartonQtyCalc = Double.parseDouble(crtnQty);
				double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);

				qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
			} else {
				qty = looseQtyCalc;
			}*/

                if (!Weight.matches("")) {
                    ProductWeight = Double.parseDouble(Weight);
                }else{
                    ProductWeight=0;
                }

                if (!lsQty.matches("")) {
                    lsQtyCnvrt = Double.parseDouble(lsQty);
                }else{
                    lsQtyCnvrt=0;
                }

                if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
                    double cartonQtyCalc = Double.parseDouble(crtnQty);
                    double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);


                    if (cartonPerQtyCalc > 1) {

                        if (ProductWeight > 0) {
                            qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
                        }else{
                            qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
                        }

                    }
                    else {
                        if (ProductWeight > 0) {
                            qty = (cartonQtyCalc * cartonPerQtyCalc) + (lsQtyCnvrt * ProductWeight);
                        } else {

                        }
                    }
                }else{
                    qty = looseQtyCalc* ProductWeight;
                }
                String quantity = twoDecimalPoint(qty);

                sl_qty.removeTextChangedListener(qtyTW);
                sl_qty.setText("" + quantity);
                sl_qty.addTextChangedListener(qtyTW);

                if (sl_qty.length() != 0) {
                    sl_qty.setSelection(sl_qty.length());
                }

                if(priceFlag==0){
                    productTotal(qty);
                }else{
                    productTotalNew();
                }
            }
        }catch(NumberFormatException e){
            e.printStackTrace();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void cartonQty() {
        try {
            String crtnQty = sl_cartonQty.getText().toString();

            if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

                double cartonQtyCalc = Double.parseDouble(crtnQty);
                double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
                double qty = 0;

                String lsQty = sl_looseQty.getText().toString();

                if (!lsQty.matches("")) {
                    double lsQtyCnvrt = Double.parseDouble(lsQty);
                    qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

                } else {
                    qty = cartonQtyCalc * cartonPerQtyCalc;
                }

                String quantity = twoDecimalPoint(qty);

                sl_qty.removeTextChangedListener(qtyTW);
                sl_qty.setText("" + quantity);
                sl_qty.addTextChangedListener(qtyTW);

                if (sl_qty.length() != 0) {
                    sl_qty.setSelection(sl_qty.length());
                }

                productTotal(qty);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void looseQtyCalc() {
        try {
            String crtnQty = sl_cartonQty.getText().toString();
            String lsQty = sl_looseQty.getText().toString();

            if (lsQty.matches("")) {
                lsQty = "0";
            }

            if (!slCartonPerQty.matches("") && !crtnQty.matches("")
                    && !lsQty.matches("")) {

                double cartonQtyCalc = Double.parseDouble(crtnQty);
                double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
                double looseQtyCalc = Double.parseDouble(lsQty);
                double qty;

                qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

                String quantity = twoDecimalPoint(qty);

                sl_qty.removeTextChangedListener(qtyTW);
                sl_qty.setText("" + quantity);
                sl_qty.addTextChangedListener(qtyTW);

                if (sl_qty.length() != 0) {
                    sl_qty.setSelection(sl_qty.length());
                }

                productTotal(qty);
            }

            if (!lsQty.matches("")) {

                double looseQtyCalc = Double.parseDouble(lsQty);
                double qty;

                if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
                    double cartonQtyCalc = Double.parseDouble(crtnQty);
                    double cartonPerQtyCalc = Double
                            .parseDouble(slCartonPerQty);

                    qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
                } else {
                    qty = looseQtyCalc;
                }

                String quantity = twoDecimalPoint(qty);

                sl_qty.removeTextChangedListener(qtyTW);
                sl_qty.setText("" + quantity);
                sl_qty.addTextChangedListener(qtyTW);

                if (sl_qty.length() != 0) {
                    sl_qty.setSelection(sl_qty.length());
                }

                productTotal(qty);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void itemDiscountCalc() {

        try {
            String itmDscnt = sl_itemDiscount.getText().toString();
            String qty = sl_qty.getText().toString();
            String prc = sl_price.getText().toString();

            if (itmDscnt.matches("")) {
                itmDscnt = "0";
            }

            if (!itmDscnt.matches("") && !qty.matches("") && !prc.matches("")) {

                // double itemDiscountCalc = Double.parseDouble(itmDscnt);

                double itemDiscountCalc = 0.0;

                itemDiscountCalc = Double.parseDouble(itmDscnt);

                int quantityCalc = Integer.parseInt(qty);
                double priceCalc = Double.parseDouble(prc);

                tt = (quantityCalc * priceCalc) - itemDiscountCalc;
                Log.d("ttl", "" + tt);
                String Prodtotal = twoDecimalPoint(tt);
                sl_total.setText("" + Prodtotal);
                sl_total_inclusive.setText("" + Prodtotal);

                double taxAmount = 0.0, netTotal = 0.0;
                if (!taxType.matches("") && !taxValue.matches("")) {

                    double taxValueCalc = Double.parseDouble(taxValue);

                    if (taxType.matches("E")) {

                        taxAmount = (tt * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_tax.setText("" + prodTax);

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);

                    } else if (taxType.matches("I")) {

                        taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_tax.setText("" + prodTax);

                        // netTotal = tt + taxAmount;
                        netTotal = tt;

                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);

                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        sl_total_inclusive.setText(totalIncl);

                    } else if (taxType.matches("Z")) {

                        sl_tax.setText("0.0");

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);

                    } else {
                        sl_tax.setText("0.0");
                        sl_netTotal.setText("" + Prodtotal);
                    }
                } else if (taxValue.matches("")) {
                    sl_tax.setText("0.0");
                    sl_netTotal.setText("" + Prodtotal);
                } else {
                    sl_tax.setText("0.0");
                    sl_netTotal.setText("" + Prodtotal);
                }
            }

        } catch (Exception e) {
            // Toast.makeText(SalesAddProduct.this, "Error",
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void productTotal(double qty) {

        try {
            double taxAmount = 0.0, netTotal = 0.0;
            double taxAmount1 = 0.0, netTotal1 = 0.0;

            if (slPrice.matches("")) {
                slPrice = "0";
            }

            if (!slPrice.matches("")) {

                double slPriceCalc = Double.parseDouble(slPrice);

                String itmDscnt = sl_itemDiscount.getText().toString();
                if (!itmDscnt.matches("")) {

                    tt = (qty * slPriceCalc);

                } else {
                    tt = qty * slPriceCalc;
                }

                String Prodtotal = twoDecimalPoint(tt);

                double subTotal = 0.0;

                String itemDisc = sl_itemDiscount.getText().toString();
                if (!itemDisc.matches("")) {
                    itmDisc = Double.parseDouble(itemDisc);
                    subTotal = tt - itmDisc;
                } else {
                    subTotal = tt;
                }

                String sbTtl = twoDecimalPoint(subTotal);

                sl_total.setText("" + sbTtl);
                sl_total_inclusive.setText("" + sbTtl);
                if (!taxType.matches("") && !taxValue.matches("")) {

                    double taxValueCalc = Double.parseDouble(taxValue);

                    if (taxType.matches("E")) {

                        if (!itemDisc.matches("")) {
                            taxAmount1 = (subTotal * taxValueCalc) / 100;
                            String prodTax = fourDecimalPoint(taxAmount1);
                            sl_tax.setText("" + prodTax);

                            netTotal1 = subTotal + taxAmount1;
                            String ProdNetTotal = twoDecimalPoint(netTotal1);
                            sl_netTotal.setText("" + ProdNetTotal);
                        } else {

                            taxAmount = (tt * taxValueCalc) / 100;
                            String prodTax = fourDecimalPoint(taxAmount);
                            sl_tax.setText("" + prodTax);

                            netTotal = tt + taxAmount;
                            String ProdNetTotal = twoDecimalPoint(netTotal);
                            sl_netTotal.setText("" + ProdNetTotal);
                        }

                    } else if (taxType.matches("I")) {
                        if (!itemDisc.matches("")) {
                            taxAmount1 = (subTotal * taxValueCalc)
                                    / (100 + taxValueCalc);
                            String prodTax = fourDecimalPoint(taxAmount1);
                            sl_tax.setText("" + prodTax);

                            // netTotal1 = subTotal + taxAmount;
                            netTotal1 = subTotal;
                            String ProdNetTotal = twoDecimalPoint(netTotal1);
                            sl_netTotal.setText("" + ProdNetTotal);

                            double dTotalIncl = netTotal1 - taxAmount1;
                            String totalIncl = twoDecimalPoint(dTotalIncl);
                            Log.d("totalIncl", "" + totalIncl);
                            sl_total_inclusive.setText(totalIncl);
                        } else {
                            taxAmount = (tt * taxValueCalc)
                                    / (100 + taxValueCalc);
                            String prodTax = fourDecimalPoint(taxAmount);
                            sl_tax.setText("" + prodTax);

                            // netTotal = tt + taxAmount;
                            netTotal = tt + taxAmount;
                            String ProdNetTotal = twoDecimalPoint(netTotal);
                            sl_netTotal.setText("" + ProdNetTotal);

                            double dTotalIncl = netTotal - taxAmount;
                            String totalIncl = twoDecimalPoint(dTotalIncl);
                            Log.d("totalIncl", "" + totalIncl);
                            sl_total_inclusive.setText(totalIncl);
                        }

                    } else if (taxType.matches("Z")) {

                        sl_tax.setText("0.0");
                        if (!itemDisc.matches("")) {
                            // netTotal1 = subTotal + taxAmount;
                            netTotal1 = subTotal;
                            String ProdNetTotal = twoDecimalPoint(netTotal1);
                            sl_netTotal.setText("" + ProdNetTotal);
                        } else {
                            // netTotal = tt + taxAmount;
                            netTotal = tt;
                            String ProdNetTotal = twoDecimalPoint(netTotal);
                            sl_netTotal.setText("" + ProdNetTotal);
                        }

                    } else {
                        sl_tax.setText("0.0");
                        sl_netTotal.setText("" + Prodtotal);
                    }

                } else if (taxValue.matches("")) {
                    sl_tax.setText("0.0");
                    sl_netTotal.setText("" + Prodtotal);
                } else {
                    sl_tax.setText("0.0");
                    sl_netTotal.setText("" + Prodtotal);
                }
            }
        } catch (Exception e) {
            // Toast.makeText(SalesAddProduct.this, "Error",
            // Toast.LENGTH_SHORT).show();
        }
    }

	/*
	 * public void clQty(){ String qty = sl_qty.getText().toString(); String
	 * crtnperQty = sl_cartonPerQty.getText().toString(); int q = 0, r = 0;
	 *
	 * if(crtnperQty.matches("0") || crtnperQty.matches("null") ||
	 * crtnperQty.matches("0.00")){ crtnperQty="1"; }
	 *
	 * if (!crtnperQty.matches("")) { if (!qty.matches("")) { try { int qty_nt =
	 * Integer.parseInt(qty); int pcs_nt = Integer.parseInt(crtnperQty);
	 *
	 * Log.d("qty_nt", ""+qty_nt); Log.d("pcs_nt", ""+pcs_nt);
	 *
	 * q = qty_nt / pcs_nt; r = qty_nt % pcs_nt;
	 *
	 * Log.d("cqty", ""+q); Log.d("lqty", ""+r);
	 *
	 * sl_cartonQty.setText("" + q); sl_looseQty.setText("" + r);
	 *
	 * } catch (ArithmeticException e) {
	 * System.out.println("Err: Divided by Zero");
	 *
	 * } catch (Exception e) { e.printStackTrace(); } }
	 *
	 * } }
	 *
	 * public void cartonQtyNew() { String crtnQty =
	 * sl_cartonQty.getText().toString();
	 *
	 * if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {
	 *
	 * int cartonQtyCalc = Integer.parseInt(crtnQty); int cartonPerQtyCalc =
	 * Integer.parseInt(slCartonPerQty); int qty = 0;
	 *
	 * String lsQty = sl_looseQty.getText().toString();
	 *
	 * if (!lsQty.matches("")) { int lsQtyCnvrt = Integer.parseInt(lsQty); qty =
	 * (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;
	 *
	 * } else { qty = cartonQtyCalc * cartonPerQtyCalc; }
	 *
	 * sl_qty.removeTextChangedListener(qtyTW); sl_qty.setText("" + qty);
	 * sl_qty.addTextChangedListener(qtyTW);
	 *
	 * if(sl_qty.length()!=0){ sl_qty.setSelection(sl_qty.length()); }
	 *
	 * productTotalNew(); } }
	 *
	 * public void looseQtyCalcNew() { String crtnQty =
	 * sl_cartonQty.getText().toString(); String lsQty =
	 * sl_looseQty.getText().toString();
	 *
	 * if (lsQty.matches("")) { lsQty = "0"; }
	 *
	 * if (!slCartonPerQty.matches("") && !crtnQty.matches("") &&
	 * !lsQty.matches("")) {
	 *
	 * int cartonQtyCalc = Integer.parseInt(crtnQty); int cartonPerQtyCalc =
	 * Integer.parseInt(slCartonPerQty); int looseQtyCalc =
	 * Integer.parseInt(lsQty);
	 *
	 * int qty; qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
	 *
	 * sl_qty.removeTextChangedListener(qtyTW); sl_qty.setText("" + qty);
	 * sl_qty.addTextChangedListener(qtyTW);
	 *
	 * if(sl_qty.length()!=0){ sl_qty.setSelection(sl_qty.length()); }
	 *
	 * productTotalNew(); }
	 *
	 * if (!lsQty.matches("")) {
	 *
	 * int looseQtyCalc = Integer.parseInt(lsQty); int qty;
	 *
	 * if (!crtnQty.matches("") && !slCartonPerQty.matches("")) { int
	 * cartonQtyCalc = Integer.parseInt(crtnQty); int cartonPerQtyCalc =
	 * Integer.parseInt(slCartonPerQty);
	 *
	 * qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc; } else { qty =
	 * looseQtyCalc; }
	 *
	 * sl_qty.removeTextChangedListener(qtyTW); sl_qty.setText("" + qty);
	 * sl_qty.addTextChangedListener(qtyTW);
	 *
	 * if(sl_qty.length()!=0){ sl_qty.setSelection(sl_qty.length()); }
	 *
	 * productTotalNew(); } }
	 */

    public void clQty() {
        try {
            String qty = sl_qty.getText().toString();
//            String qty ="3.00";
            Log.d("qty recalc", "" + qty);

            String crtnperQty = sl_cartonPerQty.getText().toString();
//            String crtnperQty = "3.00";
            double q = 0, r = 0;

            if (crtnperQty.matches("0") || crtnperQty.matches("null")
                    || crtnperQty.matches("0.00")) {
                crtnperQty = "1";
            }

            if (!crtnperQty.matches("")) {
                if (!qty.matches("")) {
                    try {
                        double qty_nt = Double.parseDouble(qty);
                        double pcs_nt = Double.parseDouble(crtnperQty);

                        Log.d("qty_nt", "" + qty_nt);
                        Log.d("pcs_nt", "" + pcs_nt);

                        q = (int) (qty_nt / pcs_nt);
                        r = (qty_nt % pcs_nt);



                        String ctn = twoDecimalPoint(q);
                        String loose = twoDecimalPoint(r);

                        Log.d("cqty", "" + q+" "+ctn);
                        Log.d("lqty", "" + r+" "+loose);

                        sl_cartonQty.removeTextChangedListener(cqtyTW);
                        sl_cartonQty.setText("" + ctn);
                        sl_cartonQty.addTextChangedListener(cqtyTW);

                        sl_looseQty.removeTextChangedListener(lqtyTW);
                        sl_looseQty.setText("" + loose);
                        sl_looseQty.addTextChangedListener(lqtyTW);

//						productTotal(qty_nt);

                        if (priceflag.matches("0")){
                            productTotal(qty_nt);
                        }else{
                            productTotalNew();
                        }

                    } catch (ArithmeticException e) {
                        System.out.println("Err: Divided by Zero");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cartonQtyNew() {
        try {
            String crtnQty = sl_cartonQty.getText().toString();

            if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

                double cartonQtyCalc = Double.parseDouble(crtnQty);
                double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
                double qty = 0;

                String lsQty = sl_looseQty.getText().toString();

                if (!lsQty.matches("")) {
                    double lsQtyCnvrt = Double.parseDouble(lsQty);
                    qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

                } else {
                    qty = cartonQtyCalc * cartonPerQtyCalc;
                }

                String quantity = twoDecimalPoint(qty);

                sl_qty.removeTextChangedListener(qtyTW);
                sl_qty.setText("" + quantity);
                sl_qty.addTextChangedListener(qtyTW);

                if (sl_qty.length() != 0) {
                    sl_qty.setSelection(sl_qty.length());
                }

                productTotalNew();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void looseQtyCalcNew() {
        try {
            String crtnQty = sl_cartonQty.getText().toString();
            String lsQty = sl_looseQty.getText().toString();

            if (lsQty.matches("")) {
                lsQty = "0";
            }

            if (!slCartonPerQty.matches("") && !crtnQty.matches("")
                    && !lsQty.matches("")) {

                double cartonQtyCalc = Double.parseDouble(crtnQty);
                double cartonPerQtyCalc = Double.parseDouble(slCartonPerQty);
                double looseQtyCalc = Double.parseDouble(lsQty);

                double qty;
                qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

                String quantity = twoDecimalPoint(qty);

                sl_qty.removeTextChangedListener(qtyTW);
                sl_qty.setText("" + quantity);
                sl_qty.addTextChangedListener(qtyTW);

                if (sl_qty.length() != 0) {
                    sl_qty.setSelection(sl_qty.length());
                }

                productTotalNew();
            }

            if (!lsQty.matches("")) {

                double looseQtyCalc = Double.parseDouble(lsQty);
                double qty;

                if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
                    double cartonQtyCalc = Double.parseDouble(crtnQty);
                    double cartonPerQtyCalc = Double
                            .parseDouble(slCartonPerQty);

                    qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
                } else {
                    qty = looseQtyCalc;
                }

                String quantity = twoDecimalPoint(qty);

                sl_qty.removeTextChangedListener(qtyTW);
                sl_qty.setText("" + quantity);
                sl_qty.addTextChangedListener(qtyTW);

                if (sl_qty.length() != 0) {
                    sl_qty.setSelection(sl_qty.length());
                }

                productTotalNew();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void itemDiscountCalcNew() {

        try {
            String itmDscnt = sl_itemDiscount.getText().toString();
            // String qty = sl_qty.getText().toString();
            // String prc = sl_price.getText().toString();
            if (itmDscnt.matches("")) {
                itmDscnt = "0";
            }

            String lPrice = sl_price.getText().toString();
            String cPrice = sl_cprice.getText().toString();
            String cqty = sl_cartonQty.getText().toString();
            String lqty = sl_looseQty.getText().toString();

            if (lPrice.matches("")) {
                lPrice = "0";
            }

            if (cPrice.matches("")) {
                cPrice = "0";
            }

            if (cqty.matches("")) {
                cqty = "0";
            }

            if (lqty.matches("")) {
                lqty = "0";
            }

            if (!itmDscnt.matches("")) {
                double itemDiscountCalc = 0.0;
                itemDiscountCalc = Double.parseDouble(itmDscnt);

                double lPriceCalc = Double.parseDouble(lPrice);
                double cPriceCalc = Double.parseDouble(cPrice);

                double cqtyCalc = Double.parseDouble(cqty);
                double lqtyCalc = Double.parseDouble(lqty);

                tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc)
                        - itemDiscountCalc;

                Log.d("ttl", "" + tt);
                String Prodtotal = twoDecimalPoint(tt);
                sl_total.setText("" + Prodtotal);
                sl_total_inclusive.setText("" + Prodtotal);

                double taxAmount = 0.0, netTotal = 0.0;
                if (!taxType.matches("") && !taxValue.matches("")) {

                    double taxValueCalc = Double.parseDouble(taxValue);

                    if (taxType.matches("E")) {

                        taxAmount = (tt * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_tax.setText("" + prodTax);

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);

                    } else if (taxType.matches("I")) {

                        taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_tax.setText("" + prodTax);

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);


                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        sl_total_inclusive.setText(totalIncl);

                    } else if (taxType.matches("Z")) {

                        sl_tax.setText("0.0");

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);

                    } else {
                        sl_tax.setText("0.0");
                        sl_netTotal.setText("" + Prodtotal);
                    }
                } else if (taxValue.matches("")) {
                    sl_tax.setText("0.0");
                    sl_netTotal.setText("" + Prodtotal);
                } else {
                    sl_tax.setText("0.0");
                    sl_netTotal.setText("" + Prodtotal);
                }
            }

        } catch (Exception e) {

        }
    }

    public void productTotalNew() {

        try {
            double taxAmount = 0.0, netTotal = 0.0;
            double taxAmount1 = 0.0, netTotal1 = 0.0;

            String lPrice = sl_price.getText().toString();
            String cPrice = sl_cprice.getText().toString();
            String cqty = sl_cartonQty.getText().toString();
            String lqty = sl_looseQty.getText().toString();

            if (lPrice.matches("")) {
                lPrice = "0";
            }

            if (cPrice.matches("")) {
                cPrice = "0";
            }

            if (cqty.matches("")) {
                cqty = "0";
            }

            if (lqty.matches("")) {
                lqty = "0";
            }

            double lPriceCalc = Double.parseDouble(lPrice);
            double cPriceCalc = Double.parseDouble(cPrice);

            double cqtyCalc = Double.parseDouble(cqty);
            double lqtyCalc = Double.parseDouble(lqty);

            tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc);

            String Prodtotal = twoDecimalPoint(tt);

            double subTotal = 0.0;

            String itemDisc = sl_itemDiscount.getText().toString();
            if (!itemDisc.matches("")) {
                itmDisc = Double.parseDouble(itemDisc);
                subTotal = tt - itmDisc;
            } else {
                subTotal = tt;
            }

            String sbTtl = twoDecimalPoint(subTotal);

            sl_total.setText("" + sbTtl);
            sl_total_inclusive.setText("" + sbTtl);

            if (!taxType.matches("") && !taxValue.matches("")) {

                double taxValueCalc = Double.parseDouble(taxValue);

                if (taxType.matches("E")) {

                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subTotal * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount1);
                        sl_tax.setText("" + prodTax);

                        netTotal1 = subTotal + taxAmount1;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        sl_netTotal.setText("" + ProdNetTotal);
                    } else {

                        taxAmount = (tt * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_tax.setText("" + prodTax);

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);
                    }

                } else if (taxType.matches("I")) {
                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subTotal * taxValueCalc)
                                / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount1);
                        sl_tax.setText("" + prodTax);

                        // netTotal1 = subTotal + taxAmount1;
                        netTotal1 = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        sl_netTotal.setText("" + ProdNetTotal);


                        double dTotalIncl = netTotal1 - taxAmount1;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        sl_total_inclusive.setText(totalIncl);
                    } else {
                        taxAmount = (tt * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_tax.setText("" + prodTax);

                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);


                        double dTotalIncl = netTotal - taxAmount;
                        String totalIncl = twoDecimalPoint(dTotalIncl);
                        Log.d("totalIncl", "" + totalIncl);
                        sl_total_inclusive.setText(totalIncl);
                    }

                } else if (taxType.matches("Z")) {

                    sl_tax.setText("0.0");
                    if (!itemDisc.matches("")) {
                        // netTotal1 = subTotal + taxAmount;
                        netTotal1 = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        sl_netTotal.setText("" + ProdNetTotal);
                    } else {
                        // netTotal = tt + taxAmount;
                        netTotal = tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);
                    }

                } else {
                    sl_tax.setText("0.0");
                    sl_netTotal.setText("" + Prodtotal);
                }

            } else if (taxValue.matches("")) {
                sl_tax.setText("0.0");
                sl_netTotal.setText("" + Prodtotal);
            } else {
                sl_tax.setText("0.0");
                sl_netTotal.setText("" + Prodtotal);
            }

        } catch (Exception e) {

        }
    }

    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tot = df.format(d);

        return tot;
    }

    public String fourDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setMinimumFractionDigits(4);
        String tot = df.format(d);

        return tot;
    }

    public void storeInDatabase() {

        Log.d("Total value ", "" + tt);

        cursor = SOTDatabase.getCursor();

        if(SalesOrderSetGet.getTranType().matches("COR")||SalesOrderSetGet.getTranType().matches("COI")){
            cursor=SOTDatabase.getConsignmentStocks(sales_prodCode);
            Log.d("ConsignmentCursor","-->"+cursor.getCount());
            if(cursor!=null && cursor.getCount()>0){
                if(cursor.moveToFirst()){
                    sl_no =  cursor.getInt(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO));
                    Log.d("SalesorderSLNO","-->"+sl_no);
                }
            }


        }else{
            if (cursor != null && cursor.getCount() > 0) {
                sl_no = cursor.getCount();
                sl_no++;
            }
        }




        String codeStr = sl_codefield.getText().toString();
        String nameStr = sl_namefield.getText().toString();
        String cartonQtyStr = sl_cartonQty.getText().toString();
        String looseQtyStr = sl_looseQty.getText().toString();
        String qtyStr = sl_qty.getText().toString();
        String focStr = sl_foc.getText().toString();
        String priceStr = sl_price.getText().toString();
        String dicountStr = sl_itemDiscount.getText().toString();
        String uomStr = sl_uom.getText().toString();
        String cartonPerQtyStr = sl_cartonPerQty.getText().toString();
        String totalStr = sl_total.getText().toString();
        String taxStr = sl_tax.getText().toString();
        String netTotalStr = sl_netTotal.getText().toString();
        String cpriceStr = sl_cprice.getText().toString();
        String exQtyStr = sl_exchange.getText().toString();

        String stock = sl_stock.getText().toString();

        Log.d("totalStr","-->"+totalStr+qtyStr);

        int exch=0;
        double price = 0, discount = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0, update_qty = 0, update_looseQty = 0,
                cartonQty = 0, looseQty = 0, qty = 0,cartonPerQty = 0,foc = 0;
        String sbTtl = "";
        String netT = "";

        double stock_avail = 0, qty_entered = 0,tot_qty=0;
        if (stock == null && stock.isEmpty()) {
            stock_avail = 0.00;
        }else{
            try{
                int value =Integer.parseInt(stock);
                stock_avail = value;
            }catch (NumberFormatException e){
                e.printStackTrace();
            }


        }
        if (qtyStr != null && !qtyStr.isEmpty()) {
            qty_entered = Double.parseDouble(qtyStr);
        }
        if (focStr!= null &&!focStr.isEmpty()) {
            foc = Double.parseDouble(focStr);
        }

        if (exQtyStr!= null &&!exQtyStr.isEmpty()) {
            exch = Integer.parseInt(exQtyStr);
        }

        Log.d("stock_avail", "" + stock_avail);
        Log.d("qty_entered", "" + qty_entered);

        tot_qty = qty_entered + foc + exch;

        Log.d("tot_qty", "" + tot_qty);

        String tranblocknegativestock = SalesOrderSetGet
                .getTranblocknegativestock();
        // tranblocknegativestock="1";
        if (codeStr.matches("")) {
            Toast.makeText(ConsignmentAddProduct.this, "Select product code",
                    Toast.LENGTH_SHORT).show();
        } else if (calCarton.matches("1") && qtyStr.matches("")
                && focStr.matches("") && exQtyStr.matches("")) {
            Toast.makeText(ConsignmentAddProduct.this, "Enter the quantity",
                    Toast.LENGTH_SHORT).show();

        } else if (calCarton.matches("0") && cartonQtyStr.matches("")
                && looseQtyStr.matches("") && qtyStr.matches("")) {
            Toast.makeText(ConsignmentAddProduct.this, "Enter the carton/quantity",
                    Toast.LENGTH_SHORT).show();
        }

        else if (tranblocknegativestock != null
                && !tranblocknegativestock.isEmpty()
                && tranblocknegativestock.matches("1")
                && stock_avail < tot_qty) {

            Toast.makeText(ConsignmentAddProduct.this, "Low Stock " + stock,
                    Toast.LENGTH_SHORT).show();

        } else {
            if (!searchProductArr.isEmpty()) {
                carton_layout.setVisibility(View.GONE);
            }

			/*if (!cartonQtyStr.matches("")) {
				cartonQty = Integer.parseInt(cartonQtyStr);
			}*/
            if (!cartonQtyStr.matches("")) {
                cartonQty = Double.parseDouble(cartonQtyStr);
            }
            if (!looseQtyStr.matches("")) {

                if ((str_ssupdate != null) || (str_sscancel != null)) {
                    update_looseQty = Double.parseDouble(looseQtyStr);
                } else {
//					looseQty = Integer.parseInt(looseQtyStr);
                    looseQty = Double.parseDouble(looseQtyStr);
                }

            }
            if (!qtyStr.matches("")) {

                if ((str_ssupdate != null) || (str_sscancel != null)) {
                    update_qty = Double.parseDouble(qtyStr);
                } else {
//					qty = Integer.parseInt(qtyStr);
                    qty = Double.parseDouble(qtyStr);
                }

            }
            qty = Double.parseDouble(qtyStr);

			/*if (!cartonPerQtyStr.matches("")) {
				cartonPerQty = Integer.parseInt(cartonPerQtyStr);
			}*/
            if (!cartonPerQtyStr.matches("")) {
                cartonPerQty = Double.parseDouble(cartonPerQtyStr);
            }
            if (!priceStr.matches("")) {
                price = Double.parseDouble(priceStr);
            }

            if (cpriceStr.matches("")) {
                cpriceStr = "0.00";
            }

            if (priceflag.matches("0")) {
                cpriceStr = "0.00";
            }

            if (exQtyStr.matches("")) {
                exQtyStr = "0";
            }

            if (!totalStr.matches("")) {
                total = Double.parseDouble(totalStr);

                String itemDisc = sl_itemDiscount.getText().toString();
                if (!itemDisc.matches("")) {
                    itmDisc = Double.parseDouble(itemDisc);
                    subTotal = total;
                } else {
                    subTotal = total;
                }

                sbTtl = twoDecimalPoint(subTotal);

            }
            if (!taxStr.matches("")) {
                tax = Double.parseDouble(taxStr);
            }

            if (!netTotalStr.matches("")) {

                if (taxType != null && !taxType.isEmpty()) {
                    if (taxType.matches("I") || taxType.matches("Z")) {
                        ntTot = subTotal;
                    } else {
                        ntTot = subTotal + tax;
                    }
                } else {
                    ntTot = subTotal + tax;
                }

                netT = twoDecimalPoint(ntTot);
            }
            if (newPrice.matches("") || newPrice == null) {
                newPrice = "0";
            }
            if (taxValue.matches("") || taxValue == null) {
                taxValue = "0";
            }

            if (priceflag.matches("0")) {
                itemDiscountCalc();
            } else if (priceflag.matches("1")) {
                itemDiscountCalcNew();
            }

            String disctStr = sl_itemDiscount.getText().toString();
            if (!disctStr.matches("")) {
                discount = Double.parseDouble(disctStr);
            }
            String totl = twoDecimalPoint(tt);
            Log.d("total" + tt, totl);

            double dis = 0.0;
            if (!dicountStr.matches("")) {
                dis = Double.parseDouble(dicountStr);
            }
            if ((str_ssupdate != null) || (str_sscancel != null)) {
                int i_sssno = Integer.parseInt(str_sssno);

                if (!totalStr.matches("")) {
                    SOTDatabase.updateBillDisc(i_sssno, codeStr, sbTtl);
                }

//                SOTDatabase.updateProductForInvoice(codeStr, nameStr,
//                        cartonQty, update_looseQty, update_qty, foc, price,
//                        discount, uomStr, cartonPerQty, tt + dis, tax, sbTtl,
//                        netT, i_sssno, cpriceStr, exQtyStr);

                SOTDatabase.updateProduct(codeStr, nameStr, cartonQty,
                        looseQty, qty, foc, price, discount, uomStr,
                        cartonPerQty, tt + dis, tax, sbTtl, netT, i_sssno,
                        cpriceStr, exQtyStr);

                // check offline pending

                double oldQ = 0.0;

                if (!oldQty.matches("")) {
                    oldQ = Double.parseDouble(oldQty);
                }

//                String locationCode = SalesOrderSetGet.getLocationcode();
//
//                Log.d("locationCode", locationCode);
//
//                double getQty = OfflineDatabase.GetQty(locationCode, codeStr);
//                //
//                Log.d("getQty", "qty aa " + getQty);
//
//                double excQty = 0, stockQty = 0;
//
//                if (!exQtyStr.matches("")) {
//                    excQty = Double.parseDouble(exQtyStr);
//                }
//
//                stockQty = (getQty + oldQ) - (update_qty + foc + excQty);
//
//                Log.d("stockQty", "stockqty aa " + stockQty);
//
//                String editinvoiceno = ConvertToSetterGetter.getEdit_inv_no();
//
//                if (!editinvoiceno.matches("")) {
//
//                    HashMap<String, String> hash = new HashMap<String, String>();
//
//                    Log.d("procode", codeStr);
//
//                    hash.put("InvoiceNo", editinvoiceno);
//                    hash.put("ProductCode", codeStr);
//                    hash.put("FOCQty", "" + 0);
//                    hash.put("Qty", "" + stockQty);
//                    hash.put("ExchangeQty", "" + 0);
//
//                    invoiceEditArr = OfflineSetterGetter.getStockUpdateArr();
//
//                    Log.d("invoiceEdit array",
//                            "" + invoiceEditArr.size());
//
//                    if (invoiceEditArr.size() > 0) {
//                        for (int i = 0; i < invoiceEditArr.size(); i++) {
//                            HashMap<String, String> datavalue = invoiceEditArr
//                                    .get(i);
//
//                            String pcode = datavalue.get("ProductCode");
//                            String sQty = datavalue.get("Qty");
//                            Log.d("sQty", " ss " + sQty);
//                            double dsQty = 0.00;
//
//                            if (!sQty.matches("")) {
//                                dsQty = Double.parseDouble(sQty);
//                            }
//
//                            if (pcode.matches(codeStr)) {
//                                double stkQty = 0.0;
//                                stkQty = (dsQty + oldQ)
//                                        - (update_qty + foc + excQty);
//                                Log.d("stkQty", " aa " + stkQty);
//                                datavalue.remove("Qty");
//                                datavalue.put("Qty", stkQty + "");
//                                break;
//                            } else {
//                                int size = (invoiceEditArr.size() - 1);
//                                Log.d("i, size", i + ".." + size);
//                                if (size == i) {
//                                    Log.d("stockQty", " bb " + stockQty);
//                                    invoiceEditArr.add(hash);
//                                    break;
//                                }
//                            }
//                        }
//
//                    } else {
//                        invoiceEditArr.add(hash);
//                    }
//                    OfflineSetterGetter.setStockUpdateArr(invoiceEditArr);
//
//                    Log.d("invoiceEditArr",
//                            "" + OfflineSetterGetter.getStockUpdateArr());
//
//                    // OfflineDatabase.updateProductStock(invoiceDetails);
//
//                }

            } else {
                if (SOTDatabase.getCursor() != null
                        && SOTDatabase.getCursor().getCount() > 0) {

                    productcode = SOTDatabase.getProductCode();
                    Log.d("ProductCode", "" + productcode);
                    for (String prodcode : productcode) {
                        if (prodcode.matches(sl_codefield.getText().toString())) {
                            prdcode = true;
                            Log.d("ProductCode", prodcode);
                            break;
                        }
                    }
                    if (prdcode == true) {

//                        productCodeFilter(sl_no, codeStr, nameStr, cartonQty,
//                                looseQty, qty, foc, price, discount, uomStr,
//                                cartonPerQty, tt + dis, tax, netT, taxType,
//                                taxValue, newPrice, sbTtl, cpriceStr, exQtyStr);
                        Log.d("sotdb2", "matches");
                        prdcode = false;

                    } else {

                        if (!totalStr.matches("")) {
                            SOTDatabase.storeBillDisc(sl_no, codeStr, sbTtl);
                        }

                        SOTDatabase.storeProduct(sl_no, codeStr, nameStr,
                                cartonQty, looseQty, qty, foc, price, discount,
                                uomStr, cartonPerQty, tt + dis, tax, netT,
                                taxType, taxValue, newPrice, sbTtl, cpriceStr,
                                exQtyStr, MinimumSellingPrice, "", "", "",MinimumCartonSellingPrice,"","");
                        Log.d("sotdb3", "original"+tt+dis );
                    }
                } else {

                    if (!totalStr.matches("")) {
                        SOTDatabase.storeBillDisc(sl_no, codeStr, sbTtl);
                    }
                    SOTDatabase.storeProduct(sl_no, codeStr, nameStr,
                            cartonQty, looseQty, qty, foc, price, discount,
                            uomStr, cartonPerQty, tt + dis, tax, netT, taxType,
                            taxValue, newPrice, sbTtl, cpriceStr, exQtyStr,
                            MinimumSellingPrice, "", "", "",MinimumCartonSellingPrice,"","");
                    Log.d("sotdb4", "original"+tt+dis );
                }

                Log.d("CPRICE", cpriceStr);

                sl_codefield.setText("");
                sl_namefield.setText("");
                sl_cartonQty.setText("");
                sl_looseQty.setText("");
                sl_qty.setText("");
                sl_foc.setText("");
                sl_itemDiscount.setText("");
                sl_total.setText("0");
                sl_total_inclusive.setText("0");
                sl_tax.setText("0");
                sl_netTotal.setText("0");

                sl_price.setText("");
                sl_uom.setText("");
                sl_stock.setText("");
                sl_cartonPerQty.setText("");

                sl_cprice.setText("");
                sl_exchange.setText("");

                sl_codefield.requestFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sl_codefield.getWindowToken(), 0);
                // imm.showSoftInput(sl_codefield,
                // InputMethodManager.SHOW_IMPLICIT);

                sl_cartonQty.setEnabled(true);
                sl_cartonQty.setFocusable(true);
                sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

                sl_looseQty.setEnabled(true);
                sl_looseQty.setFocusable(true);
                sl_looseQty.setBackgroundResource(drawable.edittext_bg);
                // Toast.makeText(SalesAddProduct.this, "Stored in database",
                // Toast.LENGTH_LONG).show();
            }

            if (!searchProductArr.isEmpty()) {
                adapter.notifyDataSetChanged();
            }

            if ((str_ssupdate != null) || (str_sscancel != null)) {
                Toast.makeText(ConsignmentAddProduct.this, "Updated ",
                        Toast.LENGTH_LONG).show();
                Intent i = new Intent(ConsignmentAddProduct.this,
                        ConsignmentSummary.class);
                startActivity(i);
                ConsignmentAddProduct.this.finish();
            }
        }
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

    /*
     * public void editCodeField() {
     *
     * if (sl_codefield.getText().toString() != "" &&
     * sl_codefield.getText().length() != 0) {
     *
     * ArrayList<String> mergeResult = new ArrayList<String>();
     * mergeResult.addAll(alprodcode); mergeResult.addAll(albar); boolean res =
     * false; for (String alphabet : mergeResult) { if
     * (alphabet.toLowerCase().equals(
     * sl_codefield.getText().toString().toLowerCase()) ||
     * alphabet.toLowerCase() .equals(sl_codefield.getText().toString()
     * .toLowerCase())) { res = true; break; } }
     *
     * if (res == true) { sl_cartonQty.requestFocus(); InputMethodManager imm =
     * (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
     * imm.showSoftInput(sl_cartonQty, InputMethodManager.SHOW_IMPLICIT);
     *
     * Iterator<Entry<String, String>> a = hashmap.entrySet() .iterator(); while
     * (a.hasNext()) {
     *
     * @SuppressWarnings("rawtypes") Map.Entry amapEntry = a.next(); String
     * keyValue = (String) amapEntry.getKey(); String value = (String)
     * amapEntry.getValue(); //
     * (edcodefield.getText().toString().equals(keyValue)) if
     * (sl_codefield.getText().toString().toLowerCase()
     * .contains(keyValue.toLowerCase())) {
     *
     * sl_namefield.setText(value);
     *
     * sales_prodCode = keyValue; } for (int i = 0; i < albarcode.size(); i++) {
     * HashMap<String, String> barhm = new HashMap<String, String>(); barhm =
     * albarcode.get(i); Iterator<Entry<String, String>> b = barhm.entrySet()
     * .iterator(); while (b.hasNext()) {
     *
     * @SuppressWarnings("rawtypes") Map.Entry bmapEntry = b.next(); String
     * keybar = (String) bmapEntry.getKey(); String valuebar = (String)
     * bmapEntry.getValue(); if (sl_codefield.getText().toString()
     * .equals(valuebar)) { sl_codefield.setText(keybar);
     * sl_namefield.setText(value); sales_prodCode = keybar; } } } }
     *
     * AsyncCallSaleProduct task = new AsyncCallSaleProduct(); task.execute(); }
     *
     * else { sl_namefield.requestFocus();
     * Toast.makeText(getApplicationContext(), " Invalid Code ",
     * Toast.LENGTH_SHORT).show(); sl_codefield.setText("");
     * sl_namefield.setText("");
     *
     * }
     *
     * sl_codefield.addTextChangedListener(new TextWatcher() {
     *
     * @Override public void afterTextChanged(Editable s) {
     *
     * }
     *
     * @Override public void beforeTextChanged(CharSequence s, int start, int
     * count, int after) {
     *
     * }
     *
     * @Override public void onTextChanged(CharSequence s, int start, int
     * before, int count) { textlength = sl_codefield.getText().length();
     * sl_namefield.setText(""); } }); } else { sl_namefield.requestFocus();
     * Toast.makeText(getApplicationContext(), " Enter Code ",
     * Toast.LENGTH_SHORT).show(); }
     *
     * }
     */
    public void alertDialogSearch() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(
                ConsignmentAddProduct.this);
        editText = new EditText(ConsignmentAddProduct.this);
        final ListView listview = new ListView(ConsignmentAddProduct.this);
        LinearLayout layout = new LinearLayout(ConsignmentAddProduct.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        myDialog.setTitle("Product");
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
        arrayAdapterProd = new CustomAlertAdapterProd(ConsignmentAddProduct.this,
                al);
        listview.setAdapter(arrayAdapterProd);
        listview.setOnItemClickListener(ConsignmentAddProduct.this);

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

                arrayAdapterProd = new CustomAlertAdapterProd(
                        ConsignmentAddProduct.this, searchResults);
                listview.setAdapter(arrayAdapterProd);
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
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {

        myalertDialog.dismiss();
        getArraylist = arrayAdapterProd.getArrayList();
        // Log.d("callhashmap", getArraylsit.toString());
        HashMap<String, String> datavalue = getArraylist.get(position);
        Set<Map.Entry<String, String>> keys = datavalue.entrySet();
        Iterator<Map.Entry<String, String>> iterator = keys.iterator();
        while (iterator.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry mapEntry = iterator.next();
            sales_prodCode = (String) mapEntry.getKey();
            values = (String) mapEntry.getValue();

            sl_codefield.setText(sales_prodCode);
            sl_namefield.setText(values);
            loadProgress();
            new AsyncCallSaleProduct().execute();

            sl_codefield.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                    textlength = sl_codefield.getText().length();
                    sl_namefield.setText("");

                    sl_price.setText("");
                    sl_uom.setText("");
                    sl_stock.setText("");
                    sl_cartonPerQty.setText("");
                }
            });
        }

    }

    public static  void cretaeMethod(HashMap<String, EditText> hm) {
        sm_total = hm.get("sm_total");
        sm_total_new = hm.get("sm_total_new");
        sm_itemDisc = hm.get("sm_itemDisc");
        sm_subTotal = hm.get("sm_subTotal");
        sm_tax = hm.get("sm_tax");
        sm_netTotal = hm.get("sm_netTotal");
        sm_subTotal_inclusive = hm.get("sm_subTotal_inclusive");
//        this.hashMap=hm;


           hashMap =hm;

        hashMap.put("sm_total", sm_total);
        hashMap.put("sm_total_new", sm_total_new);
        hashMap.put("sm_itemDisc", sm_itemDisc);
        hashMap.put("sm_subTotal", sm_subTotal);
        hashMap.put("sm_tax", sm_tax);
        hashMap.put("sm_netTotal", sm_netTotal);
        hashMap.put("sm_subTotal_inclusive", sm_subTotal_inclusive);

        Log.d("EditTextInvoke","-->"+sm_netTotal.getText().toString()+" -->"+hashMap.size());
    }



    /*
     * private class AsyncCallWSADDPRD extends AsyncTask<Void, Void, Void> {
     *
     * @SuppressWarnings("deprecation")
     *
     * @Override protected void onPreExecute() { al.clear();
     * getActionBar().setHomeButtonEnabled(false);
     * menu.setSlidingEnabled(false); spinnerLayout = new
     * LinearLayout(DeliveryAddProduct.this);
     * spinnerLayout.setGravity(Gravity.CENTER); addContentView(spinnerLayout,
     * new LayoutParams( android.view.ViewGroup.LayoutParams.FILL_PARENT,
     * android.view.ViewGroup.LayoutParams.FILL_PARENT));
     * spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
     * enableViews(salesproduct_layout, false); progressBar = new
     * ProgressBar(DeliveryAddProduct.this);
     * progressBar.setProgress(android.R.attr.progressBarStyle);
     * progressBar.setIndeterminateDrawable(getResources().getDrawable(
     * drawable.greenprogress)); //
     * progressBar.setBackgroundColor(Color.parseColor("#FFFFFF"));
     * spinnerLayout.addView(progressBar); }
     *
     * @Override protected Void doInBackground(Void... arg0) { // TODO
     * Auto-generated method stub SoapObject request = new SoapObject(NAMESPACE,
     * webMethName); PropertyInfo companyCode = new PropertyInfo();
     *
     * String cmpnyCode = SupplierSetterGetter.getCompanyCode();
     *
     * companyCode.setName("CompanyCode"); companyCode.setValue(cmpnyCode);
     * companyCode.setType(String.class); request.addProperty(companyCode);
     * SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
     * SoapEnvelope.VER11); envelope.dotNet = true; envelope.bodyOut = request;
     * envelope.setOutputSoapObject(request); HttpTransportSE
     * androidHttpTransport = new HttpTransportSE( valid_url); try {
     *
     * androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
     * SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
     * productTxt = response.toString(); productresult = " { ProductDetails : "
     * + productTxt + "}"; JSONObject jsonResponse; try { jsonResponse = new
     * JSONObject(productresult); JSONArray jsonMainNode = jsonResponse
     * .optJSONArray("ProductDetails");
     *
     * int lengthJsonArr = jsonMainNode.length(); for (int i = 0; i <
     * lengthJsonArr; i++) {
     *
     * JSONObject jsonChildNode = jsonMainNode .getJSONObject(i);
     *
     * String productcodes = jsonChildNode.optString( PRODUCT_CODE).toString();
     * String productnames = jsonChildNode.optString( PRODUCT_NAME).toString();
     *
     * HashMap<String, String> producthm = new HashMap<String, String>();
     * producthm.put(productcodes, productnames); al.add(producthm);
     * hashmap.putAll(producthm); HashMap<String, String> productsplithm = new
     * HashMap<String, String>(); albarsplit.add(productsplithm);
     * hmsplitbc.putAll(productsplithm); alprodcode.add(productcodes);
     *
     * }
     *
     * } catch (JSONException e) {
     *
     * e.printStackTrace(); }
     *
     * } catch (Exception e) { e.printStackTrace();
     *
     * }
     *
     * return null; }
     *
     * @Override protected void onPostExecute(Void result) {
     * progressBar.setVisibility(View.GONE);
     * spinnerLayout.setVisibility(View.GONE); enableViews(salesproduct_layout,
     * true); getActionBar().setHomeButtonEnabled(true);
     * menu.setSlidingEnabled(true); }
     *
     * }
     *
     * private class AsyncCallBARCODE extends AsyncTask<Void, Void, Void> {
     *
     * @Override protected void onPreExecute() {
     *
     * }
     *
     * @Override protected Void doInBackground(Void... arg0) { // TODO
     * Auto-generated method stub SoapObject request = new SoapObject(NAMESPACE,
     * webMethNamebar); PropertyInfo companyCode = new PropertyInfo();
     *
     * String cmpnyCode = SupplierSetterGetter.getCompanyCode();
     *
     * companyCode.setName("CompanyCode"); companyCode.setValue(cmpnyCode);
     * companyCode.setType(String.class); request.addProperty(companyCode);
     * SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
     * SoapEnvelope.VER11); envelope.dotNet = true; envelope.bodyOut = request;
     * envelope.setOutputSoapObject(request); HttpTransportSE
     * androidHttpTransport = new HttpTransportSE( valid_url); try {
     *
     * androidHttpTransport.call(SOAP_ACTION + webMethNamebar, envelope);
     * SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
     * barcodeTxt = response.toString(); barcoderesult = " { BarCodeDetails : "
     * + barcodeTxt + "}";
     *
     * JSONObject jsonResponse; try {
     *
     * jsonResponse = new JSONObject(barcoderesult);
     *
     * JSONArray jsonMainNode = jsonResponse .optJSONArray("BarCodeDetails");
     *
     * int lengthJsonArr = jsonMainNode.length();
     *
     * for (int i = 0; i < lengthJsonArr; i++) {
     *
     * JSONObject jsonChildNode = jsonMainNode .getJSONObject(i);
     *
     * String productbarcode = jsonChildNode.optString(
     * PRODUCTNAME_BARCODE).toString(); String barcode =
     * jsonChildNode.optString( PRODUCT_BARCODE).toString(); HashMap<String,
     * String> barcodehm = new HashMap<String, String>();
     * barcodehm.put(productbarcode, barcode); albarcode.add(barcodehm);
     * albar.add(barcode);
     *
     * }
     *
     * } catch (JSONException e) {
     *
     * e.printStackTrace(); }
     *
     * } catch (Exception e) { e.printStackTrace();
     *
     * }
     *
     * return null; }
     *
     * @Override protected void onPostExecute(Void result) {
     *
     * } }
     */
    private class CheckProductBarcode extends AsyncTask<Void, Void, Void> {

        String ed_code = "";

        @Override
        protected void onPreExecute() {
            mBarcodeJsonString = "";
            loadProgress();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ed_code = sl_codefield.getText().toString();
            mHashMap.clear();
            mHashMap.put("CompanyCode", cmpnyCode);
            mHashMap.put("Barcode", ed_code);

            try {
                mBarcodeJsonString = SalesOrderWebService.getSODetail(mHashMap,
                        webMethNamebar);
                Log.d("mBarcodeJsonString", "-->" + mBarcodeJsonString);
                mBarcodeJsonObject = new JSONObject(mBarcodeJsonString);
                mBarcodeJsonArray = mBarcodeJsonObject
                        .optJSONArray("SODetails");
                int lengthJsonBarcodeArr = mBarcodeJsonArray.length();

                if (lengthJsonBarcodeArr > 0) {
                    JSONObject jsonChildNode = mBarcodeJsonArray
                            .getJSONObject(0);
                    String productbarcode = jsonChildNode.optString(
                            PRODUCTNAME_BARCODE).toString();
                    String barcode = jsonChildNode.optString(PRODUCT_BARCODE)
                            .toString();
                    sales_prodCode = productbarcode;
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
            if (mBarcodeJsonString != null && !mBarcodeJsonString.isEmpty()) {
                new AsyncCallSaleProduct().execute();
            } else {
                sales_prodCode = ed_code;
                new AsyncCallSaleProduct().execute();
            }

        }

    }

    private class GetProduct extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            al.clear();
            loadProgress();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            mHashMap.put("CompanyCode", cmpnyCode);
            try {

                mProductJsonString = SalesOrderWebService.getSODetail(mHashMap,
                        "fncGetProductForSearch");
                mProductJsonObject = new JSONObject(mProductJsonString);
                mProductJsonArray = mProductJsonObject
                        .optJSONArray("SODetails");

                int lengthJsonProductArr = mProductJsonArray.length();

                if (lengthJsonProductArr > 0) {
                    for (int i = 0; i < lengthJsonProductArr; i++) {

                        JSONObject jsonChildNode = mProductJsonArray
                                .getJSONObject(i);

                        String productcodes = jsonChildNode.optString(
                                PRODUCT_CODE).toString();
                        String productnames = jsonChildNode.optString(
                                PRODUCT_NAME).toString();

                        HashMap<String, String> producthm = new HashMap<String, String>();
                        producthm.put(productcodes, productnames);
                        al.add(producthm);
                    }
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(salesproduct_layout, true);
        }
    }

    private class AsyncCallSaleProduct extends AsyncTask<Void, Void, Void> {

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {

            getSalesProdArr.clear();
            stockConsignmentArr.clear();
            newPrice = "";
            slPrice = "";
            slUomCode = "";
            slCartonPerQty = "";
            taxValue = "";

            sl_cartonQty.setText("");
            sl_looseQty.setText("");
            sl_qty.setText("");
            sl_foc.setText("");
            sl_itemDiscount.setText("");
            sl_price.setText("");
            sl_uom.setText("");
            sl_stock.setText("");
            sl_cartonPerQty.setText("");
            sl_total.setText("0");
            sl_total_inclusive.setText("0");
            sl_tax.setText("0");
            sl_netTotal.setText("0");

            sl_cprice.setText("");
            sl_exchange.setText("");

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub

            sales_prodCode=sl_codefield.getText().toString();

            getSalesProdArr = SalesProductWebService.getSaleProduct(
                    sales_prodCode, webMethName, "");

            Log.d("SalesProdcode","-->"+sales_prodCode+" -->"+cstcode);

            if (cstgrpcd.matches("-1")) {
                cstgrpcd = "";
            }

            priceArr = SalesProductWebService
                    .getProductPrice(cstgrpcd, cstcode, sales_prodCode,
                            "fncGetProductPriceForSales", "CG");

                stockConsignmentArr=SalesOrderWebService.
                        getStockConsignmentArr("fncGetConsignmentProductStock",cstcode,sales_prodCode);






            newPrice = priceArr.get(0);
            newcprice = priceArr.get(1);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            ProductStockAsyncCall task = new ProductStockAsyncCall();
            task.execute();

            if (!getSalesProdArr.isEmpty()) {

                carton_layout.setVisibility(View.VISIBLE);
                if (!searchProductArr.isEmpty()) {
                    grid_layout.setVisibility(View.VISIBLE);
                }

                Log.d("getSalesProdArr", getSalesProdArr.toString());

                slPrice = getSalesProdArr.get(0);
                slUomCode = getSalesProdArr.get(1);
                slCartonPerQty = getSalesProdArr.get(2);
                cprice = getSalesProdArr.get(4);
                MinimumSellingPrice = getSalesProdArr.get(5);
                // taxValue = SalesOrderSetGet.getTaxValue();
                // Log.d("taxValue", taxValue);
                taxValue = getSalesProdArr.get(3);

                String codefield = getSalesProdArr.get(8);
                String namefield = getSalesProdArr.get(9);
                Weight = getSalesProdArr.get(10);

                Log.d("Weight",""+ Weight);

                sl_codefield.setText(codefield);
                sl_namefield.setText(namefield);

                if (FormSetterGetter.isEditPrice()) {

                    sl_price.setBackgroundResource(drawable.edittext_bg);
                    sl_cprice.setBackgroundResource(drawable.edittext_bg);
                } else {
                    sl_price.setEnabled(false);
                    sl_price.setFocusable(false);
                    sl_price.setGravity(Gravity.CENTER);
                    sl_price.setBackgroundResource(drawable.labelbg);
                    sl_price.setFocusableInTouchMode(false);

                    sl_cprice.setEnabled(false);
                    sl_cprice.setFocusable(false);
                    sl_cprice.setGravity(Gravity.CENTER);
                    sl_cprice.setBackgroundResource(drawable.labelbg);
                    sl_cprice.setFocusableInTouchMode(false);
                }

                if (newPrice.matches("0")) {
                    sl_price.setText(slPrice);
                } else {
                    sl_price.setText(newPrice);
                }

                if (newcprice.matches("0")) {
                    sl_cprice.setText(cprice);
                } else {

                    sl_cprice.setText(newcprice);
                }

                sl_uom.setText(slUomCode);

                if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
                        || slCartonPerQty.matches("")) {

                    if (calCarton.matches("0")) {
                        sl_cartonQty.requestFocus();
                    } else {
                        sl_cartonQty.setFocusable(false);
                        sl_cartonQty.setBackgroundResource(drawable.labelbg);

                        // sl_looseQty.setEnabled(false);
                        sl_looseQty.setFocusable(false);
                        sl_looseQty.setBackgroundResource(drawable.labelbg);

                        sl_qty.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(sl_qty,
                                InputMethodManager.SHOW_IMPLICIT);
                    }

                } else {
                    sl_cartonQty.setFocusableInTouchMode(true);
                    sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

                    sl_looseQty.setFocusableInTouchMode(true);
                    sl_looseQty.setBackgroundResource(drawable.edittext_bg);

                    sl_cartonQty.requestFocus();
                }

                sl_cartonPerQty.setText(slCartonPerQty);

                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(salesproduct_layout, true);

                sl_cartonQty.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(sl_cartonQty,
                        InputMethodManager.SHOW_IMPLICIT);
            } else {
                sl_codefield.requestFocus();
                Toast.makeText(getApplicationContext(), " Invalid Code ",
                        Toast.LENGTH_SHORT).show();
                sl_codefield.setText("");
                sl_namefield.setText("");
            }




            HashMap<String, EditText> hm = new HashMap<String, EditText>();

            hm.put("Productcode", sl_codefield);
            hm.put("Productname", sl_namefield);
            hm.put("Cartonqty", sl_cartonQty);
            hm.put("Looseqty", sl_looseQty);
            hm.put("Qty", sl_qty);
            hm.put("Uom", sl_uom);
            hm.put("Foc", sl_foc);
            hm.put("Stock", sl_stock);
            hm.put("Cartonperqty", sl_cartonPerQty);
            hm.put("Price", sl_price);
            hm.put("CPrice", sl_cprice);


            sm_total = hm.get("sm_total");
            sm_total_new = hm.get("sm_total_new");
            sm_itemDisc = hm.get("sm_itemDisc");
            sm_subTotal = hm.get("sm_subTotal");
            sm_tax = hm.get("sm_tax");
            sm_netTotal = hm.get("sm_netTotal");
            sm_subTotal_inclusive = hm.get("sm_subTotal_inclusive");

            hashMap.put("sm_total", sm_total);
            hashMap.put("sm_total_new", sm_total_new);
            hashMap.put("sm_itemDisc", sm_itemDisc);
            hashMap.put("sm_subTotal", sm_subTotal);
            hashMap.put("sm_tax", sm_tax);
            hashMap.put("sm_netTotal", sm_netTotal);
            hashMap.put("sm_subTotal_inclusive", sm_subTotal_inclusive);

            Log.d("sl_price", "pppp " + slPrice);
            Log.d("SalesOrderSetGet","-->"+stockConsignmentArr.size());
            if(SalesOrderSetGet.getTranType().matches("COR")||SalesOrderSetGet.getTranType().matches("COI")) {
                String code = sl_codefield.getText().toString();
                String name = sl_namefield.getText().toString();
                Cursor cursor = SOTDatabase.getCursor();
                customer_code = SalesOrderSetGet.getSuppliercode();
                Log.d("cust_codes", "" + customer_code + " " + sales_prodCode +"---> "+flag);
//
                if(!stockConsignmentArr.isEmpty()){
                    if(flag==false){
                        consignmentStockDialog.initiateBatchPopupWindow(
                                ConsignmentAddProduct.this, code, name, slCartonPerQty,
                                slPrice, hm, stockConsignmentArr, customer_code,flag);
                        noBatchvalue();
                    }else{
//                        consignmentStockDialog.initiateBatchPopupWindow(ConsignmentAddProduct.this, id, slNo, code, customer_code, cursor,
//                                prices_txt, hashMap, carton_txt, loose_txt, qty_txt,pcs_txt,flag,hm);
                         Log.d("initiateValue","-->"+sl_qty.getText().toString());
                        consignmentStockDialog.initiateBatchPopupWindow(
                                ConsignmentAddProduct.this, id, slNo,code,name, pcs_txt, cursor,
                                prices_txt, hashMap,SalesOrderSetGet.getCustomercode(),hm);
                    }

                }else{
//                    if(flag==true){
//                        consignmentStockDialog.initiateBatchPopupWindow(ConsignmentAddProduct.this, id, slNo, code, customer_code, cursor,
//                                prices_txt, hashMap, carton_txt, loose_txt, qty_txt,pcs_txt,flag,hm);
//                    }else{
                        Toast.makeText(getApplicationContext(),
                            "No Stock data", Toast.LENGTH_SHORT)
                            .show();
                    sl_codefield.setText(" ");
                    sl_namefield.setText("");
//                    }
                }

//                if (!stockConsignmentArr.isEmpty()) {
//
//                    Log.d("EditTextValue","-->"+hashMap.size());
//
//                    if(flag==true){
//
//                        Log.d("cust_codes", "" + customer_code + " " + sales_prodCode +"---> "+flag);
//
//                        Log.d("initiateBatchWindow","--<"+prices_txt +"-->"+carton_txt
//                        +"//"+loose_txt+"--<"+qty_txt+"=="+pcs_txt+"flag"+flag);
//
//                        consignmentStockDialog.initiateBatchPopupWindow(ConsignmentAddProduct.this, id, slNo, code, customer_code, cursor,
//                                prices_txt, hashMap, carton_txt, loose_txt, qty_txt,pcs_txt,flag,hm);
//
//
//                    }else{
//
//                        consignmentStockDialog.initiateBatchPopupWindow(
//                                ConsignmentAddProduct.this, code, name, slCartonPerQty,
//                                slPrice, hm, stockConsignmentArr, customer_code,flag);
//                        noBatchvalue();
//                    }
//
//
//
//                } else {
//
//                    Toast.makeText(getApplicationContext(),
//                            "No Stock data", Toast.LENGTH_SHORT)
//                            .show();
//                    sl_codefield.setText(" ");
//                    sl_namefield.setText("");
//                }
            }


//            if(SalesOrderSetGet.getTranType().matches("COR")||SalesOrderSetGet.getTranType().matches("COI")) {
//                new AsyncStockTask().execute();
//            }

            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(salesproduct_layout, true);

        }
    }

    private void noBatchvalue() {

        Log.d("newPrice", "new p "+newPrice+" " +newcprice+" "+slCartonPerQty);

        Log.d("slPrice", "new sl "+slPrice +" "+cprice);


        if (newPrice.matches("0")) {
            sl_price.setText(slPrice);
        } else {
            sl_price.setText(newPrice);
        }

        if (newcprice.matches("0")) {
            sl_cprice.setText(cprice);
        } else {
            sl_cprice.setText(newcprice);
        }

        sl_uom.setText(slUomCode);


        if (priceflag.matches("1")) {

            if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
                    || slCartonPerQty.matches("")) {
                sl_price.setText("");
                sl_price.setVisibility(View.INVISIBLE);
                txt_price.setVisibility(View.GONE);
            }else{
                sl_price.setVisibility(View.VISIBLE);
                txt_price.setVisibility(View.VISIBLE);
            }

        } else {
            sl_price.setVisibility(View.VISIBLE);
            txt_price.setVisibility(View.VISIBLE);
        }

        if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
                || slCartonPerQty.matches("")) {

            if (calCarton.matches("0")) {
                Log.d("sl_cartonQty","-->"+sl_cartonQty);
                sl_cartonQty.setFocusableInTouchMode(true);
                sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

                sl_looseQty.setFocusableInTouchMode(true);
                sl_looseQty.setBackgroundResource(drawable.edittext_bg);

                sl_qty.setFocusableInTouchMode(true);
                sl_qty.setBackgroundResource(drawable.edittext_bg);

                sl_foc.setFocusableInTouchMode(true);
                sl_foc.setBackgroundResource(drawable.edittext_bg);

                sl_cartonQty.requestFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(sl_cartonQty,
                        InputMethodManager.SHOW_IMPLICIT);

            } else {
                Log.d("sl_qty","-->"+sl_qty);

                sl_cartonQty.setFocusable(false);
                sl_cartonQty.setBackgroundResource(drawable.labelbg);

                sl_looseQty.setFocusable(false);
                sl_looseQty.setBackgroundResource(drawable.labelbg);

                sl_qty.setFocusableInTouchMode(false);
                sl_qty.setBackgroundResource(drawable.labelbg);

                sl_foc.setFocusableInTouchMode(false);
                sl_foc.setBackgroundResource(drawable.labelbg);

                sl_qty.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(sl_qty, InputMethodManager.SHOW_IMPLICIT);
            }

        } else {
            Log.d("sl_cartonQty","-->"+sl_cartonQty);
            sl_cartonQty.setFocusableInTouchMode(true);
            sl_cartonQty.setBackgroundResource(drawable.edittext_bg);

            sl_looseQty.setFocusableInTouchMode(true);
            sl_looseQty.setBackgroundResource(drawable.edittext_bg);

            sl_qty.setFocusableInTouchMode(true);
            sl_qty.setBackgroundResource(drawable.edittext_bg);

            sl_foc.setFocusableInTouchMode(true);
            sl_foc.setBackgroundResource(drawable.edittext_bg);

            sl_cartonQty.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(sl_cartonQty, InputMethodManager.SHOW_IMPLICIT);
        }

        sl_cartonPerQty.setText(slCartonPerQty);
        sl_uom.setText(slUomCode);
        sl_cartonQty.setText(sl_cartonQty.getText().toString());

        progressBar.setVisibility(View.GONE);
        spinnerLayout.setVisibility(View.GONE);
        enableViews(salesproduct_layout, true);
    }

    private class AsyncCallWSSearchProduct extends AsyncTask<Void, Void, Void> {

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            loadProgress();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub

            searchProductArr = NewProductWebService.searchProduct(catStr,
                    subCatStr, "fncGetProduct");

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (!searchProductArr.isEmpty()) {

                carton_layout.setVisibility(View.GONE);
                grid_layout.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(getApplicationContext(), "No matches found",
                        Toast.LENGTH_SHORT).show();
            }

            try {
                searchProductList();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(salesproduct_layout, true);
        }
    }

    public void searchProductList() throws ParseException {

        adapter = new AddSimpleAdapter(ConsignmentAddProduct.this,
                searchProductArr, R.layout.sale_productitem,"", new String[] {
                "ProductCode", "ProductName", "WholeSalePrice" },
                new int[] { R.id.txt_code, R.id.txt_name, R.id.txt_price })

        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(R.id.txt_price);
                if (MobileSettingsSetterGetter.getShowPriceOnDO()
                        .equalsIgnoreCase("true")) {
                    text.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.txt3))
                            .setVisibility(View.VISIBLE);
                } else {
                    text.setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.txt3))
                            .setVisibility(View.GONE);
                }

                return view;
            }
        };

        productFilterList.setAdapter(adapter);
    }

    private void loadProgress() {
        spinnerLayout = new LinearLayout(ConsignmentAddProduct.this);
        spinnerLayout.setGravity(Gravity.CENTER);
        addContentView(spinnerLayout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT));
        spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
        enableViews(salesproduct_layout, false);
        progressBar = new ProgressBar(ConsignmentAddProduct.this);
        progressBar.setProgress(android.R.attr.progressBarStyle);
        progressBar.setIndeterminateDrawable(getResources().getDrawable(
                drawable.greenprogress));
        // progressBar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        spinnerLayout.addView(progressBar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        menu.toggle();
        if (menu.isMenuShowing()) {
            // enableViews(salesproduct_layout, false);

        } else {

            // enableViews(salesproduct_layout, true);
        }

        return super.onOptionsItemSelected(item);
    }

    private class ProductStockAsyncCall extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            cmpnyCode = SupplierSetterGetter.getCompanyCode();
            LocationCode = SalesOrderSetGet.getLocationcode();

            producthashValue.put("CompanyCode", cmpnyCode);
            producthashValue.put("LocationCode", LocationCode);
            producthashValue.put("ProductCode", sales_prodCode);
            product_stock_jsonString = WebServiceClass.parameterService(
                    producthashValue, "fncGetProductWithStock");

            try {

                product_stock_jsonResponse = new JSONObject(
                        product_stock_jsonString);
                product_stock_jsonMainNode = product_stock_jsonResponse
                        .optJSONArray("JsonArray");

                /*********** Process each JSON Node ************/
                int lengthJsonArr = product_stock_jsonMainNode.length();
                for (int i = 0; i < lengthJsonArr; i++) {
                    /****** Get Object for each JSON node. ***********/
                    JSONObject jsonChildNode;

                    jsonChildNode = product_stock_jsonMainNode.getJSONObject(i);

                    stock = jsonChildNode.optString("Qty").toString();

                    Log.d("stockQty", stock);

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
            if(stock==null){
                sl_stock.setText(" " + 0);
            }else{
                sl_stock.setText(" " + stock);
            }

        }
    }

    @Override
    public void onBackPressed() {

        if ((str_ssupdate != null) || (str_sscancel != null)) {
            Intent i = new Intent(ConsignmentAddProduct.this,
                    ConsignmentSummary.class);
            startActivity(i);
            ConsignmentAddProduct.this.finish();
        } else {

            if (intentString.matches("DeliveryOrder")) {

                Intent i = new Intent(ConsignmentAddProduct.this,
                        CustomerListActivity.class);
                startActivity(i);
                ConsignmentAddProduct.this.finish();

            } else if (intentString.matches("Route DeliveryOrder")) {

                Intent i = new Intent(ConsignmentAddProduct.this,
                        RouteHeader.class);
                startActivity(i);
                ConsignmentAddProduct.this.finish();

            } else {
                Intent i = new Intent(ConsignmentAddProduct.this,
                        ConsignmentCustomer.class);
                startActivity(i);
                ConsignmentAddProduct.this.finish();
            }

        }
    }

    @Override
    public void onListItemClick(String item) {
        menu.toggle();

    }

    public void alertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Deleting");
        alertDialog
                .setMessage("Consignment products will clear. Do you want to proceed");
        alertDialog.setIcon(R.mipmap.ic_exit);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(ConsignmentAddProduct.this,
                                ConsignmentHeader.class);
                        startActivity(i);
                        ConsignmentAddProduct.this.finish();
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


}