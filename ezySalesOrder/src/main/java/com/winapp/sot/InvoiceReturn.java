package com.winapp.sot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LogOutSetGet;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.Printer;
import com.winapp.printer.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

public class InvoiceReturn extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace {

    SlidingMenu menu;

    LinearLayout spinnerLayout, foc_layout, invoice_carton_header, pcs_txt_layout, pcs_layout, noofprint_layout,
            invoice_total_text_layout, invoice_total_layout, invoice_return_total_layout;//,customer_layout
    LinearLayout invoive_return_layout, price_header_layout, grid_layout, carton_layout, return_summary_layout;
    HorizontalScrollView return_listview_layout;
    Button sl_addProduct, sl_summary;
    ListView productList;
    ImageView save, expand, save_return, summary_return;
    EditText sl_codefield, sl_namefield, sl_cartonQty, sl_looseQty, sl_qty, rtn_cartonQty, rtn_looseQty, rtn_qty, sl_price, sl_itemDiscount, sl_uom, sl_cartonPerQty,
            sl_total, sl_tax, sl_netTotal, sl_return_subtotal, sl_return_tax, sl_return_netTotal, sl_cprice, sl_stock,
            sm_total, sm_subTotal, sm_netTotal, sm_billDisc, sm_tax,
            sm_total_new, sm_itemDisc, sm_return_subTotal, sm_return_tax, sm_return_netTotal, original_qty;
    TextView price_txt;
    TextWatcher cqtyTW, lqtyTW, qtyTW, returnCqtyTW, returnLqtyTW, returnQtyTW;
    ProgressBar progressBar;
    Cursor cursor, billCursor;
    ProductListAdapter adapter;
    String valid_url, priceflag = "", calCarton = "", StrinvNo = "",CustCode="", invNo = "", inv_slno = "";
    String slNo = "";
    double tota = 0;
    ArrayList<HashMap<String, String>> InvoiceHeaderArr = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> InvoiceDetailsArr = new ArrayList<HashMap<String, String>>();
    String strCQty = "", strLQty = "", strQty = "", ss_slid = "", StrOriginalQty = "";
    String slCartonPerQty = "", slPrice = "", taxType = "", taxValue = "", beforeLooseQty = "", ss_Cqty = "", rtn_beforeLooseQty = "", rtn_Cqty = "";
    double tt, itmDisc = 0;
    double return_tt, return_itmDisc = 0;
    String beforeChange = "";
    String summaryResult = "";
    double billDiscount = 0, smTax = 0, sbTtl = 0, taxAmt = 0;

    //print
    HashMap<String, String> hm = new HashMap<String, String>();
    String jsonString = "", totalbalance, gnrlStngs, jsonStr = null;
    JSONObject jsonResponse, jsonResp;
    JSONArray jsonMainNode, jsonSecNode;
    int stuprange = 3, stdownrange = 1, stwght = 1;
    CheckBox enableprint, cash_checkbox;
    private UIHelper helper;
    ArrayList<String> sortproduct = new ArrayList<String>();
    List<String> sort = new ArrayList<String>();
    HashSet<String> hs = new HashSet<String>();
    List<String> printsortHeader = new ArrayList<String>();
    HashMap<String, String> hashValue;
    ArrayList<ProductDetails> product;
    Button stupButton, stdownButton;
    TextView stnumber;
    ArrayList<ProductDetails> productdet;
    String subTot = "", totTax = "", netTot = "", tot = "";

    private ArrayList<HashMap<String, String>> alBatchStock = new ArrayList<HashMap<String, String>>();
    private String haveBatchOnStockIn = "";
    private InvoiceBatchDialog invoiceBatchDialog;
    private String haveBatch="",haveExpiry="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(drawable.ic_menu);
        setContentView(R.layout.invoice_return);

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);

        View customNav = LayoutInflater.from(this).inflate(
                R.layout.invoice_return_actionbar_title, null);
        TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
        txt.setText("Invoice Return");

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

        invoive_return_layout = (LinearLayout) findViewById(R.id.salesproduct_layout);
        foc_layout = (LinearLayout) findViewById(R.id.foc_layout);

        pcs_txt_layout = (LinearLayout) findViewById(R.id.pcs_txt_layout);
        pcs_layout = (LinearLayout) findViewById(R.id.pcs_layout);

        price_header_layout = (LinearLayout) findViewById(R.id.invoice_price_header_layout);
        grid_layout = (LinearLayout) findViewById(R.id.invoice_grid_layout);
        carton_layout = (LinearLayout) findViewById(R.id.invoice_carton_layout);
        invoice_carton_header = (LinearLayout) findViewById(R.id.invoice_carton_header);
        invoice_total_text_layout = (LinearLayout) findViewById(R.id.invoice_total_text_layout);
        invoice_total_layout = (LinearLayout) findViewById(R.id.invoice_total_layout);
        invoice_return_total_layout = (LinearLayout) findViewById(R.id.invoice_return_total_layout);

        return_summary_layout = (LinearLayout) findViewById(R.id.return_summary_layout);
        return_listview_layout = (HorizontalScrollView) findViewById(R.id.return_listview_layout);

        productList = (ListView) findViewById(android.R.id.list);

        sl_summary = (Button) findViewById(R.id.sl_summary);
        sl_addProduct = (Button) findViewById(R.id.sl_addProduct);

        sl_stock = (EditText) findViewById(R.id.sl_stock);
        sl_codefield = (EditText) findViewById(R.id.sl_codefield);
        sl_namefield = (EditText) findViewById(R.id.sl_namefield);
        sl_cartonQty = (EditText) findViewById(R.id.sl_cartonQty);
        sl_looseQty = (EditText) findViewById(R.id.sl_looseQty);
        sl_qty = (EditText) findViewById(R.id.sl_qty);
        original_qty = (EditText) findViewById(R.id.original_qty);
        rtn_cartonQty = (EditText) findViewById(R.id.sl_returncartonQty);
        rtn_looseQty = (EditText) findViewById(R.id.sl_returnlooseQty);
        rtn_qty = (EditText) findViewById(R.id.sl_returnqty);
        //sl_foc = (EditText) findViewById(R.id.sl_foc);
        sl_price = (EditText) findViewById(R.id.inv_ret_price);
        sl_itemDiscount = (EditText) findViewById(R.id.sl_itemDiscount);
        sl_uom = (EditText) findViewById(R.id.sl_uom);
        sl_cartonPerQty = (EditText) findViewById(R.id.sl_cartonPerQty);

        sl_total = (EditText) findViewById(R.id.sl_total);
        sl_tax = (EditText) findViewById(R.id.sl_tax);
        sl_netTotal = (EditText) findViewById(R.id.sl_netTotal);

        sl_return_subtotal = (EditText) findViewById(R.id.sl_return_total);
        sl_return_tax = (EditText) findViewById(R.id.sl_return_tax);
        sl_return_netTotal = (EditText) findViewById(R.id.sl_return_netTotal);

        sm_return_subTotal = (EditText) findViewById(R.id.sm_return_subTotal);
        sm_return_tax = (EditText) findViewById(R.id.sm_return_tax);
        sm_return_netTotal = (EditText) findViewById(R.id.sm_return_netTotal);

        sm_total = (EditText) findViewById(R.id.sm_total);
        sm_subTotal = (EditText) findViewById(R.id.sm_subTotal);
        sm_netTotal = (EditText) findViewById(R.id.sm_netTotal);
        sm_billDisc = (EditText) findViewById(R.id.sm_billDisc);
        sm_tax = (EditText) findViewById(R.id.sm_tax);
        sm_total_new = (EditText) findViewById(R.id.sm_total_new);
        sm_itemDisc = (EditText) findViewById(R.id.sm_itemDisc);

        sl_cprice = (EditText) findViewById(R.id.sl_cprice);
        //sl_exchange = (EditText) findViewById(R.id.sl_exchange);
        price_txt = (TextView) findViewById(R.id.invoice_pricetxt);
        expand = (ImageView) findViewById(R.id.expand);
        save_return = (ImageView) findViewById(R.id.save_return);
        summary_return = (ImageView) findViewById(R.id.summary_return);
        //summary_return.setVisibility(View.INVISIBLE);
        invoiceBatchDialog = new InvoiceBatchDialog();

        valid_url = FWMSSettingsDatabase.getUrl();
        new SalesProductWebService(valid_url);
        new SOTSummaryWebService(valid_url);

        product = new ArrayList<ProductDetails>();
        productdet = new ArrayList<ProductDetails>();
        hashValue = new HashMap<String, String>();
        helper = new UIHelper(InvoiceReturn.this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
//		header = SalesOrderSetGet.getHeader_flag();

        sl_itemDiscount.setRawInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        priceflag = SalesOrderSetGet.getCartonpriceflag();
        calCarton = LogOutSetGet.getCalcCarton();

//		calCarton="1";

        if (priceflag.matches("null") || priceflag.matches("")) {
            priceflag = "0";
        }

        if (priceflag.matches("1")) {
            sl_cprice.setVisibility(View.VISIBLE);
            price_txt.setVisibility(View.GONE);
            price_header_layout.setVisibility(View.VISIBLE);
        } else {
            sl_cprice.setVisibility(View.GONE);
            price_txt.setVisibility(View.VISIBLE);
            price_header_layout.setVisibility(View.GONE);
        }

        SOTDatabase.init(InvoiceReturn.this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            StrinvNo = extras.getString("invNo");
            CustCode = extras.getString("CustomerCode");
            Log.d("StrinvNo", "" + StrinvNo);

            cursor = SOTDatabase.getCursor();
            int count = cursor.getCount();
            Log.d("ursor count", "" + count);

            AsyncCallWSInvoice task = new AsyncCallWSInvoice();
            task.execute();

        }


        sl_addProduct.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                storeInDatabase();

            }
        });

        save_return.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (cursor != null && cursor.getCount() > 0) {

                    saveAlertDialog();

//					AsyncCallWSSummary task = new AsyncCallWSSummary();
//					task.execute();
                } else {
                    Toast.makeText(InvoiceReturn.this, "No data found",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        productList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int pos,
                                    long arg3) {
                // TODO Auto-generated method stub

                ss_slid = ((TextView) v.findViewById(R.id.ss_slid)).getText().toString();
                inv_slno = ((TextView) v.findViewById(R.id.ss_slno)).getText().toString();

                slCartonPerQty = ((TextView) v.findViewById(R.id.ss_pcspercarton)).getText().toString();
                slPrice = ((TextView) v.findViewById(R.id.ss_price)).getText().toString();
                Log.d("slPrice", "" + slPrice);
                Log.d("ss_slid", "" + ss_slid);

                sl_codefield.setText(((TextView) v.findViewById(R.id.ss_prodcode)).getText().toString());
                sl_namefield.setText(((TextView) v.findViewById(R.id.ss_name)).getText().toString());

                sl_cartonPerQty.setText(slCartonPerQty);
                sl_price.setText(slPrice);
                sl_itemDiscount.setText(((TextView) v.findViewById(R.id.ss_item_disc))
                        .getText().toString());
                sl_cprice.setText(((TextView) v.findViewById(R.id.ss_cprice))
                        .getText().toString());

                taxType = ((TextView) v.findViewById(R.id.ss_tax_type)).getText().toString();
                taxValue = ((TextView) v.findViewById(R.id.ss_tax_value)).getText().toString();

                strCQty = ((TextView) v.findViewById(R.id.ss_c_qty))
                        .getText().toString();
                strLQty = ((TextView) v.findViewById(R.id.ss_l_qty))
                        .getText().toString();
                strQty = ((TextView) v.findViewById(R.id.ss_qty))
                        .getText().toString();
                StrOriginalQty = ((TextView) v.findViewById(R.id.ss_original_qty))
                        .getText().toString();

                String strReturnCQty = ((TextView) v.findViewById(R.id.ss_return_c_qty))
                        .getText().toString();
                String strReturnLQty = ((TextView) v.findViewById(R.id.ss_return_l_qty))
                        .getText().toString();
                String strReturnQty = ((TextView) v.findViewById(R.id.ss_return_qty))
                        .getText().toString();

                sl_cartonQty.setText(strCQty);
                sl_looseQty.setText(strLQty);
                sl_qty.setText(strQty);
                original_qty.setText(StrOriginalQty);

                if (strReturnCQty.equals("0")) {
                    rtn_cartonQty.setText("");
                } else {
                    rtn_cartonQty.setText(strReturnCQty);
                }
                if (strReturnLQty.equals("0")) {
                    rtn_looseQty.setText("");
                } else {
                    rtn_looseQty.setText(strReturnLQty);
                }
                if (strReturnQty.equals("0")) {
                    rtn_qty.setText("");
                } else {
                    rtn_qty.setText(strReturnQty);
                }

                sl_itemDiscount.setText(((TextView) v.findViewById(R.id.ss_item_disc))
                        .getText().toString());

                if (slCartonPerQty.matches("1") || slCartonPerQty.matches("0")
                        || slCartonPerQty.matches("")) {

                    rtn_cartonQty.setFocusable(false);
                    rtn_cartonQty.setBackgroundResource(drawable.labelbg);

                    rtn_looseQty.setFocusable(false);
                    rtn_looseQty.setBackgroundResource(drawable.labelbg);

                    rtn_qty.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(rtn_qty, InputMethodManager.SHOW_IMPLICIT);

                } else {
                    rtn_cartonQty.setFocusableInTouchMode(true);
                    rtn_cartonQty.setBackgroundResource(drawable.edittext_bg);

                    rtn_looseQty.setFocusableInTouchMode(true);
                    rtn_looseQty.setBackgroundResource(drawable.edittext_bg);

                    rtn_cartonQty.requestFocus();
                }

                new AsyncCallWSGetProduct().execute();

            }

        });

        summary_return.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (return_listview_layout.getVisibility() == View.VISIBLE) {
                    // Its visible
                    totalTab();
                    return_listview_layout.setVisibility(View.GONE);
                    return_summary_layout.setVisibility(View.VISIBLE);

                    invoice_total_text_layout.setVisibility(View.GONE);
                    invoice_total_layout.setVisibility(View.GONE);
//					invoice_return_total_layout.setVisibility(View.GONE);


                } else {
                    return_listview_layout.setVisibility(View.VISIBLE);
                    return_summary_layout.setVisibility(View.GONE);

                    invoice_total_text_layout.setVisibility(View.VISIBLE);
                    invoice_total_layout.setVisibility(View.VISIBLE);
//					invoice_return_total_layout.setVisibility(View.VISIBLE);


                    // Either gone or invisible
                }

            }
        });

        expand.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (pcs_txt_layout.getVisibility() == View.VISIBLE) {
                    // Its visible
                    pcs_txt_layout.setVisibility(View.GONE);
                    pcs_layout.setVisibility(View.GONE);
                } else {
                    pcs_txt_layout.setVisibility(View.VISIBLE);
                    pcs_layout.setVisibility(View.VISIBLE);
                    // Either gone or invisible
                }

            }
        });

        rtn_cartonQty.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    slPrice = sl_price.getText().toString();


                    if (calCarton.matches("0")) {
                        if (priceflag.matches("0")) {

                        } else if (priceflag.matches("1")) {
                            productTotalNewReturn();
                        }

                    } else {
                        if (priceflag.matches("0")) {
                            cartonQtyReturn();
                        } else if (priceflag.matches("1")) {
                            cartonQtyNewReturn();
                        }
                    }

                    rtn_looseQty.requestFocus();
                    return true;
                }
                return false;
            }
        });

        returnCqtyTW = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                rtn_Cqty = rtn_cartonQty.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                slPrice = sl_price.getText().toString();

                if (calCarton.matches("0")) {

                    if (priceflag.matches("0")) {

                    } else if (priceflag.matches("1")) {
                        productTotalNewReturn();
                    }
                } else {

                    if (priceflag.matches("0")) {
                        cartonQtyReturn();
                    } else if (priceflag.matches("1")) {
                        cartonQtyNewReturn();
                    }

                    int length = rtn_cartonQty.length();
                    if (length == 0) {
                        String lqty = rtn_looseQty.getText().toString();
                        if (lqty.matches("")) {
                            lqty = "0";
                        }
                        if (!lqty.matches("")) {
                            rtn_qty.removeTextChangedListener(returnQtyTW);
                            rtn_qty.setText(lqty);
                            rtn_qty.addTextChangedListener(returnQtyTW);

                            if (rtn_qty.length() != 0) {
                                rtn_qty.setSelection(rtn_qty.length());
                            }
                            double lsQty = Double.parseDouble(lqty);
                            /////////////
                            if (priceflag.matches("0")) {
                                productTotalReturn(lsQty);
                            } else if (priceflag.matches("1")) {
                                //String cprice = sl_price.getText().toString();
                                productTotalNewReturn();
                            }
                        } else {
                            rtn_qty.setText("");
                        }
                    }
                }

            }
        };

        rtn_looseQty.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    slPrice = sl_price.getText().toString();

                    if (calCarton.matches("0")) {
                        if (priceflag.matches("0")) {

                        } else if (priceflag.matches("1")) {
                            productTotalNewReturn();
                        }

                    } else {
                        if (priceflag.matches("0")) {
                            looseQtyCalcReturn();
                        } else if (priceflag.matches("1")) {
                            looseQtyCalcNewReturn();
                        }
                    }

                    rtn_qty.requestFocus();
                    return true;
                }
                return false;
            }
        });

        returnLqtyTW = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                rtn_beforeLooseQty = rtn_looseQty.getText().toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                slPrice = sl_price.getText().toString();

                if (calCarton.matches("0")) {
                    if (priceflag.matches("0")) {

                    } else if (priceflag.matches("1")) {
                        productTotalNewReturn();
                    }

                } else {
                    if (priceflag.matches("0")) {
                        looseQtyCalcReturn();
                    } else if (priceflag.matches("1")) {
                        looseQtyCalcNewReturn();
                    }

                    int length = rtn_looseQty.length();
                    if (length == 0) {
                        String qty = rtn_qty.getText().toString();
                        if (!rtn_beforeLooseQty.matches("") && !qty.matches("")) {

                            int qtyCnvrt = Integer.parseInt(qty);
                            int lsCnvrt = Integer.parseInt(rtn_beforeLooseQty);

                            rtn_qty.removeTextChangedListener(returnQtyTW);
                            rtn_qty.setText("" + (qtyCnvrt - lsCnvrt));
                            rtn_qty.addTextChangedListener(returnQtyTW);

                            if (rtn_qty.length() != 0) {
                                rtn_qty.setSelection(rtn_qty.length());
                            }
                            if (priceflag.matches("0")) {
                                looseQtyCalcReturn();
                            } else if (priceflag.matches("1")) {
                                looseQtyCalcNewReturn();
                            }
                        }
                    }
                }
            }
        };

        rtn_qty.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    slPrice = sl_price.getText().toString();
                    String qty = rtn_qty.getText().toString();
                    if (!qty.matches("")) {
                        double qtyCalc = Double.parseDouble(qty);
                        //productTotal(qtyCalc);
                        if (calCarton.matches("0")) {
                            String pcsPerCarton = sl_cartonPerQty.getText().toString();
                            if (!pcsPerCarton.matches("")) {
                                int pcsPerCartonCalc = Integer.parseInt(pcsPerCarton);
                                if (pcsPerCartonCalc == 1) {
                                    productTotalReturn(qtyCalc);
                                }
                            }

                        } else {
                            clQtyReturn();
                        }
                    }

                    if (priceflag.matches("1")) {

                        if (sl_cprice.getText().toString().equals("0.00")
                                || sl_cprice.getText().toString().equals("0")
                                || sl_cprice.getText().toString().equals("0.0")
                                || sl_cprice.getText().toString().equals(".0")) {
                            sl_cprice.setText("");
                        }

                        //sl_cprice.requestFocus();
                    } else {
                        if (sl_price.getText().toString().equals("0.00")
                                || sl_price.getText().toString().equals("0")
                                || sl_price.getText().toString().equals("0.0")
                                || sl_price.getText().toString().equals(".0")) {
                            sl_price.setText("");
                        }

                        //sl_price.requestFocus();
                    }

                    return true;
                }
                return false;
            }
        });

        returnQtyTW = new TextWatcher() {

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
                String qty = rtn_qty.getText().toString();
                //String total_qty = sl_qty.getText().toString();

                Log.d("strQty", "" + StrOriginalQty);
                Log.d("qty", "" + qty);

                if (!StrOriginalQty.matches("")) {
                    int quantity = Integer.parseInt(StrOriginalQty);

                    if (!qty.matches("")) {
                        int i_rtnqty = Integer.parseInt(qty);

                        if (i_rtnqty > quantity) {
                            rtn_qty.setText("");
                            Toast.makeText(InvoiceReturn.this, "Return quantity " + i_rtnqty + " exceeded current quantity " + quantity, Toast.LENGTH_LONG).show();
                        } else {
                            sl_qty.setText("" + (quantity - i_rtnqty));
                        }
                    }

//					else{
//						sl_qty.setText(""+quantity);
//					}
                }

                if (calCarton.matches("0")) {

                    if (priceflag.matches("0")) {
                        if (!qty.matches("")) {
                            int quantity = Integer.parseInt(qty);
                            productTotalReturn(quantity);

                            int length = rtn_qty.length();
                            if (length == 0) {
                                productTotalReturn(0);
                                sl_qty.setText(StrOriginalQty);
                                rtn_cartonQty.removeTextChangedListener(returnCqtyTW);
                                rtn_cartonQty.setText("");
                                rtn_cartonQty.addTextChangedListener(returnCqtyTW);

                                rtn_looseQty.removeTextChangedListener(returnLqtyTW);
                                rtn_looseQty.setText("");
                                rtn_looseQty.addTextChangedListener(returnLqtyTW);
                            }
                        }
                    } else if (priceflag.matches("1")) {
                        String pcsPerCarton = sl_cartonPerQty.getText().toString();
                        if (!pcsPerCarton.matches("")) {
                            int pcsPerCartonCalc = Integer.parseInt(pcsPerCarton);
                            if (pcsPerCartonCalc == 1) {

                                if (!qty.matches("")) {
                                    int quantity = Integer.parseInt(qty);
                                    productTotalReturn(quantity);
                                }

                            }
                        }

                        int length = rtn_qty.length();
                        if (length == 0) {
                            productTotalReturn(0);
                            sl_qty.setText(StrOriginalQty);
                            rtn_cartonQty.removeTextChangedListener(returnCqtyTW);
                            rtn_cartonQty.setText("");
                            rtn_cartonQty.addTextChangedListener(returnCqtyTW);

                            rtn_looseQty.removeTextChangedListener(returnLqtyTW);
                            rtn_looseQty.setText("");
                            rtn_looseQty.addTextChangedListener(returnLqtyTW);
                        }

                    }

                } else {
                    if (!qty.matches("")) {
                        clQtyReturn();
                    }


                    int length = rtn_qty.length();
                    if (length == 0) {
                        if (calCarton.matches("0")) {

                        } else {
                            productTotalReturn(0);
                            sl_qty.setText(StrOriginalQty);
                            rtn_cartonQty.removeTextChangedListener(returnCqtyTW);
                            rtn_cartonQty.setText("");
                            rtn_cartonQty.addTextChangedListener(returnCqtyTW);

                            rtn_looseQty.removeTextChangedListener(returnLqtyTW);
                            rtn_looseQty.setText("");
                            rtn_looseQty.addTextChangedListener(returnLqtyTW);
                        }
                    }
                }
            }

        };

        sl_cartonQty.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    slPrice = sl_price.getText().toString();


                    if (calCarton.matches("0")) {
                        if (priceflag.matches("0")) {

                        } else if (priceflag.matches("1")) {
                            productTotalNew();
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

                    } else if (priceflag.matches("1")) {
                        productTotalNew();
                    }
                } else {

                    if (priceflag.matches("0")) {
                        cartonQty();
                    } else if (priceflag.matches("1")) {
                        cartonQtyNew();
                    }

                    int length = sl_cartonQty.length();
                    if (length == 0) {
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
                            /////////////
                            if (priceflag.matches("0")) {
                                productTotal(lsQty);
                            } else if (priceflag.matches("1")) {
                                //String cprice = sl_price.getText().toString();
                                productTotalNew();
                            }
                        }
                        ////////////
                    }
                }

            }
        };

        sl_looseQty.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    slPrice = sl_price.getText().toString();

                    if (calCarton.matches("0")) {
                        if (priceflag.matches("0")) {

                        } else if (priceflag.matches("1")) {
                            productTotalNew();
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

                    } else if (priceflag.matches("1")) {
                        productTotalNew();
                    }

                } else {
                    if (priceflag.matches("0")) {
                        looseQtyCalc();
                    } else if (priceflag.matches("1")) {
                        looseQtyCalcNew();
                    }

                    int length = sl_looseQty.length();
                    if (length == 0) {
                        String qty = sl_qty.getText().toString();
                        if (!beforeLooseQty.matches("") && !qty.matches("")) {

                            int qtyCnvrt = Integer.parseInt(qty);
                            int lsCnvrt = Integer.parseInt(beforeLooseQty);

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
        };


        sl_qty.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    slPrice = sl_price.getText().toString();
                    String qty = sl_qty.getText().toString();
                    if (!qty.matches("")) {
                        double qtyCalc = Double.parseDouble(qty);
                        //productTotal(qtyCalc);
                        if (calCarton.matches("0")) {
                            String pcsPerCarton = sl_cartonPerQty.getText().toString();
                            if (!pcsPerCarton.matches("")) {
                                int pcsPerCartonCalc = Integer.parseInt(pcsPerCarton);
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
                                || sl_cprice.getText().toString().equals(".0")) {
                            sl_cprice.setText("");
                        }

                        sl_cprice.requestFocus();
                    } else {
                        if (sl_price.getText().toString().equals("0.00")
                                || sl_price.getText().toString().equals("0")
                                || sl_price.getText().toString().equals("0.0")
                                || sl_price.getText().toString().equals(".0")) {
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

                if (calCarton.matches("0")) {

                    if (priceflag.matches("0")) {
                        if (!qty.matches("")) {
                            int quantity = Integer.parseInt(qty);
                            productTotal(quantity);

                            int length = sl_qty.length();
                            if (length == 0) {
                                productTotal(0);

                                sl_cartonQty.removeTextChangedListener(cqtyTW);
                                sl_cartonQty.setText("");
                                sl_cartonQty.addTextChangedListener(cqtyTW);

                                sl_looseQty.removeTextChangedListener(lqtyTW);
                                sl_looseQty.setText("");
                                sl_looseQty.addTextChangedListener(lqtyTW);
                            }
                        }
                    } else if (priceflag.matches("1")) {
                        String pcsPerCarton = sl_cartonPerQty.getText().toString();
                        if (!pcsPerCarton.matches("")) {
                            int pcsPerCartonCalc = Integer.parseInt(pcsPerCarton);
                            if (pcsPerCartonCalc == 1) {

                                if (!qty.matches("")) {
                                    int quantity = Integer.parseInt(qty);
                                    productTotal(quantity);
                                }

                            }
                        }

                        int length = sl_qty.length();
                        if (length == 0) {
                            productTotal(0);

                            sl_cartonQty.removeTextChangedListener(cqtyTW);
                            sl_cartonQty.setText("");
                            sl_cartonQty.addTextChangedListener(cqtyTW);

                            sl_looseQty.removeTextChangedListener(lqtyTW);
                            sl_looseQty.setText("");
                            sl_looseQty.addTextChangedListener(lqtyTW);
                        }

                    }

                } else {
                    if (!qty.matches("")) {
                        clQty();
                    }


                    int length = sl_qty.length();
                    if (length == 0) {
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
        };


//		sm_billDisc.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void afterTextChanged(Editable s) {
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				beforeChange = sm_billDisc.getText().toString();
//			}
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				try {
//
//					String billDisc = sm_billDisc.getText().toString();
//
//					if (sm_billDisc.length() == 0) {
//
//						billDiscount = 0;
//						taxCalc();
//
//						String ProdTax = fourDecimalPoint(smTax);
//						sm_tax.setText("" + ProdTax);
//
//						String sub = twoDecimalPoint(sbTtl);
//						sm_subTotal.setText("" + sub);
//
//						double netTotal = sbTtl + smTax;
//						String ProdNettotal = twoDecimalPoint(netTotal);
//						sm_netTotal.setText("" + ProdNettotal);
//					}
//
//					if (!billDisc.matches("")) {
//
//						double billDiscCalc = Double.parseDouble(billDisc);
//						double sbtot = SOTDatabase.getsumsubTot();
//						billDiscount = billDiscCalc / sbtot;
//						taxCalc();
//						String txAt = fourDecimalPoint(taxAmt);
//						sm_tax.setText("" + txAt);
//
//						String ta = sm_tax.getText().toString();
//						double sbt = SOTDatabase.getSubTotal();
//
//						if (!ta.matches("")) {
//							double taxAt = Double.parseDouble(ta);
//							double net = taxAt + sbt;
//
//							String netTo = twoDecimalPoint(net);
//							String subTo = twoDecimalPoint(sbt);
//
//							sm_netTotal.setText(netTo);
//							sm_subTotal.setText(subTo);
//
//						}
//
//					} else {
//
//					}
//				} catch (Exception e) {
//
//				}
//			}
//
//		});


        sl_cartonQty.addTextChangedListener(cqtyTW);
        sl_looseQty.addTextChangedListener(lqtyTW);
        sl_qty.addTextChangedListener(qtyTW);

        rtn_cartonQty.addTextChangedListener(returnCqtyTW);
        rtn_looseQty.addTextChangedListener(returnLqtyTW);
        rtn_qty.addTextChangedListener(returnQtyTW);

    }

    public void billDiscCalc() {
        try {

            String billDisc = sm_billDisc.getText().toString();

            if (sm_billDisc.length() == 0) {

                billDiscount = 0;

                taxCalc();


                String ProdTax = fourDecimalPoint(smTax);
                sm_tax.setText("" + ProdTax);

                String sub = twoDecimalPoint(sbTtl);
                sm_subTotal.setText("" + sub);

                double netTotal = sbTtl + smTax;
                String ProdNettotal = twoDecimalPoint(netTotal);
                sm_netTotal.setText("" + ProdNettotal);
            }

            if (!billDisc.matches("") || !billDisc.matches("0.00") || !billDisc.matches("0.0") || !billDisc.matches("0")) {

                double billDiscCalc = Double.parseDouble(billDisc);
                double sbtot = SOTDatabase.getsumsubTot();
                billDiscount = billDiscCalc / sbtot;

                taxCalc();


                String txAt = fourDecimalPoint(taxAmt);
                sm_tax.setText("" + txAt);

                String ta = sm_tax.getText().toString();
                double sbt = SOTDatabase.getSubTotal();

                if (!ta.matches("")) {
                    double taxAt = Double.parseDouble(ta);
                    double net = taxAt + sbt;

                    String netTo = twoDecimalPoint(net);
                    String subTo = twoDecimalPoint(sbt);

                    sm_netTotal.setText(netTo);
                    sm_subTotal.setText(subTo);

                }

            } else {

            }
        } catch (Exception e) {

        }
    }

    @SuppressWarnings("deprecation")
    public void taxCalc() {

        taxAmt = 0;
        int sl_no = 1;
        if (cursor != null && cursor.getCount() > 0) {

            if (cursor.moveToFirst()) {
                do {
                    double net_tot = 0;
                    double subTot = 0, tx = 0, txVl = 0, tax = 0, net = 0;

                    String total = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_TOTAL));

                    Log.d("bill total", total);

                    String taxValue = cursor.getString(cursor
                            .getColumnIndex(SOTDatabase.COLUMN_TAXVALUE));

                    billCursor = SOTDatabase.getBillCursor();

                    if (billCursor != null && billCursor.getCount() > 0) {

                        double sbTotal = SOTDatabase.getsubTotal(sl_no);

                        Log.d("bill sub total", "" + sbTotal);

                        // subTot = sbTotal - billDiscount;
                        double billDisc = sbTotal * billDiscount;
                        subTot = sbTotal - billDisc;
                        Log.d("after bill sub total", "" + subTot);

                    }

                    if (!taxValue.matches("")) {
                        txVl = Double.parseDouble(taxValue);
                        tx = (subTot * txVl) / 100;
                    }

                    Log.d("tx", "" + tx);

                    String updTx = fourDecimalPoint(tx);

                    if (!updTx.matches("")) {
                        tax = Double.parseDouble(updTx);

                        if (!total.matches("")) {

                            net_tot = subTot + tax;

                            String ntTtl = twoDecimalPoint(net_tot);

                            net = Double.parseDouble(ntTtl);
                            String subtotal = twoDecimalPoint(subTot);

                            if (taxType.matches("Z")) { // added new 09/08/2016

                            } else {
                                SOTDatabase.updateSummary(tax, Double.parseDouble(subtotal), net, sl_no);
                            }


                        }
                    }

                    sl_no++;
                    taxAmt = taxAmt + tx;

                } while (cursor.moveToNext());
                cursor.requery();
            }
        }
    }

    private class AsyncCallWSGetProduct extends AsyncTask<Void, Void, Void> {
        String prodcode="";
        ArrayList<String> productArr = new ArrayList<String>();
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {

            productArr.clear();

            prodcode = sl_codefield.getText().toString();
            spinnerLayout = new LinearLayout(InvoiceReturn.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            InvoiceReturn.this.addContentView(spinnerLayout, new LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(invoive_return_layout, false);
            progressBar = new ProgressBar(InvoiceReturn.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            productArr = SalesProductWebService.getGraProduct(prodcode,"fncGetProduct");

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if(productArr.size()>0){

                haveBatch = productArr.get(4);
                haveExpiry = productArr.get(5);

                Log.d("haveBatch","hh "+haveBatch);
                Log.d("haveExpiry","hhh "+haveExpiry);
            }

          new AsyncCallWSBatch().execute();

        }
    }

    private class AsyncCallWSBatch extends AsyncTask<Void, Void, Void> {

        String prodcode="";
        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            alBatchStock.clear();
            prodcode = sl_codefield.getText().toString();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Log.d("Invoice No:", StrinvNo);

            alBatchStock = SalesProductWebService
                    .getProductBatchReturn(prodcode,CustCode,StrinvNo,
                            "fncGetInvoiceBatchDetailByCustomer");

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            HashMap<String, EditText> hm = new HashMap<String, EditText>();

            hm.put("Productcode", sl_codefield);
            hm.put("Productname", sl_namefield);
            hm.put("Cartonqty", rtn_cartonQty);
            hm.put("Looseqty", rtn_looseQty);
            hm.put("Qty", rtn_qty);
            hm.put("Uom", sl_uom);
//            hm.put("Foc", sl_foc);
            hm.put("Stock", sl_stock);
            hm.put("Cartonperqty", sl_cartonPerQty);
            hm.put("Price", sl_price);
            hm.put("CPrice", sl_cprice);

            Log.d("sl_price", "pppp " + slPrice);

            haveBatchOnStockIn = SalesOrderSetGet.getHaveBatchOnStockIn();
//				if (offlineLayout.getVisibility() == View.GONE) {
            if (haveBatchOnStockIn.matches("True")) {
                Log.d("haveBatchOnStockIn", haveBatchOnStockIn);
                if (haveBatch.matches("True")
                        || haveExpiry.matches("True")) {
                    Log.d("haveBatch", "haveExpiry");
                    String code = sl_codefield.getText().toString();
                    String name = sl_namefield.getText().toString();

                    if (!alBatchStock.isEmpty()) {
                        invoiceBatchDialog.initiateBatchPopupWindow(
                                InvoiceReturn.this, haveBatch,
                                haveExpiry, code, name, slCartonPerQty,
                                slPrice, hm, alBatchStock);

                    } else {

                        Toast.makeText(getApplicationContext(),
                                "No Batch data", Toast.LENGTH_SHORT)
                                .show();
                    }

                } else {
                    Log.d("no haveBatch", "no haveExpiry");

                }

            } else {
                Log.d("no haveBatchOnStockIn", "no haveBatchOnStockIn");

            }

            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(invoive_return_layout, true);

        }
    }




    private class AsyncCallWSInvoice extends AsyncTask<Void, Void, Void> {

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {

            InvoiceDetailsArr.clear();
            InvoiceHeaderArr.clear();

            spinnerLayout = new LinearLayout(InvoiceReturn.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            InvoiceReturn.this.addContentView(spinnerLayout, new LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(invoive_return_layout, false);
            progressBar = new ProgressBar(InvoiceReturn.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Log.d("Invoice No:", StrinvNo);

            InvoiceDetailsArr = SalesOrderWebService.getInvoiceDetails(StrinvNo,
                    "fncGetInvoiceDetail");
            InvoiceHeaderArr = SalesOrderWebService.getInvoiceHeader(StrinvNo,
                    "fncGetInvoiceHeaderByInvoiceNo");

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            storeInvoice();

            progressBar.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            enableViews(invoive_return_layout, true);

        }
    }


    public void storeInvoice() {

        int sl_no;

        if (InvoiceHeaderArr != null) {
            for (int i = 0; i < InvoiceHeaderArr.size(); i++) {

                invNo = InvoiceHeaderArr.get(i).get("Invoive_No");
                //invoiceNumber = Inv_No;
                // ConvertToSetterGetter.setSoNo(Inv_No);
                ConvertToSetterGetter.setEdit_inv_no(invNo);

                String SoDate = SalesOrderSetGet.getCurrentdate();

                String LocationCode = InvoiceHeaderArr.get(i).get(
                        "LocationCode");
                String CustomerCode = InvoiceHeaderArr.get(i).get(
                        "CustomerCode");
                String Total = InvoiceHeaderArr.get(i).get("Total");
                String ItemDiscount = InvoiceHeaderArr.get(i).get(
                        "ItemDiscount");
                String BillDIscount = InvoiceHeaderArr.get(i).get(
                        "BillDIscount");
                String Remarks = InvoiceHeaderArr.get(i).get("Remarks");
                String CurrencyCode = InvoiceHeaderArr.get(i).get(
                        "CurrencyCode");
                String CurrencyRate = InvoiceHeaderArr.get(i).get(
                        "CurrencyRate");

                sm_billDisc.setText(BillDIscount);

                if (!Total.matches("")) {
                    double tot = Double.parseDouble(Total);
                    if (!ItemDiscount.matches("")) {
                        double itmDisc = Double.parseDouble(ItemDiscount);

                        double ttl = tot - itmDisc;
                        String totl = twoDecimalPoint(ttl);
                        sm_total.setText(totl);
                    } else {
                        sm_total.setText(Total);
                    }
                }

                SalesOrderSetGet.setCustomername(SalesOrderSetGet
                        .getCustname());
                SalesOrderSetGet.setSaleorderdate(SoDate);
                SalesOrderSetGet.setDeliverydate("");
                SalesOrderSetGet.setLocationcode(LocationCode);
                SalesOrderSetGet.setCustomercode(CustomerCode);
                SalesOrderSetGet.setRemarks(Remarks);
                SalesOrderSetGet.setCurrencycode(CurrencyCode);
                SalesOrderSetGet.setCurrencyrate(CurrencyRate);
                SalesOrderSetGet.setCurrencyname("");
            }
        }
        if (InvoiceDetailsArr != null) {
            for (int i = 0; i < InvoiceDetailsArr.size(); i++) {

                slNo = InvoiceDetailsArr.get(i).get("slNo");
                String ProductCode = InvoiceDetailsArr.get(i).get(
                        "ProductCode");
                String ProductName = InvoiceDetailsArr.get(i).get(
                        "ProductName");
                String CQty = InvoiceDetailsArr.get(i).get("CQty");
                String LQty = InvoiceDetailsArr.get(i).get("LQty");
                String Qty = InvoiceDetailsArr.get(i).get("Qty");
                String FOCQty = InvoiceDetailsArr.get(i).get("FOCQty");
                String PcsPerCarton = InvoiceDetailsArr.get(i).get(
                        "PcsPerCarton");
                String RetailPrice = InvoiceDetailsArr.get(i).get(
                        "RetailPrice");
                String Price = InvoiceDetailsArr.get(i).get("Price");

                String Total = InvoiceDetailsArr.get(i).get("Total");
                String ItemDiscount = InvoiceDetailsArr.get(i).get(
                        "ItemDiscount");
                String BillDiscount = InvoiceDetailsArr.get(i).get(
                        "BillDiscount");
                String SubTotal = InvoiceDetailsArr.get(i).get("SubTotal");
                String Tax = InvoiceDetailsArr.get(i).get("Tax");
                String NetTotal = InvoiceDetailsArr.get(i).get("NetTotal");
                String TaxType = InvoiceDetailsArr.get(i).get("TaxType");
                String TaxPerc = InvoiceDetailsArr.get(i).get("TaxPerc");

                String ExchangeQty = InvoiceDetailsArr.get(i).get(
                        "ExchangeQty");
                String CartonPrice = InvoiceDetailsArr.get(i).get(
                        "CartonPrice");
                String ItemRemarks = InvoiceDetailsArr.get(i).get("ItemRemarks");


                if (ItemRemarks.matches("null")) {
                    ItemRemarks = "";
                }

                sl_no = i + 1;

                if (!Total.matches("")) {
                    double tot = Double.parseDouble(Total);
                    if (!ItemDiscount.matches("")) {
                        double itmDisc = Double.parseDouble(ItemDiscount);

                        double ttl = tot - itmDisc;
                        String totl = twoDecimalPoint(ttl);
                        SOTDatabase.storeBillDisc(sl_no, ProductCode, totl);
                    } else {
                        SOTDatabase.storeBillDisc(sl_no, ProductCode,
                                SubTotal);
                    }
                }

                Log.d("ProductCode", "" + ProductCode);
                Log.d("ProductName", "" + ProductName);
                Log.d("TaxType", "" + TaxType);
                Log.d("TaxPerc", "" + TaxPerc);

                Log.d("RetailPrice", "" + RetailPrice);

                int slno = Integer.parseInt(slNo);

                Double dcQty = new Double(CQty);
                Double dlQty = new Double(LQty);
                Double dfocQty = new Double(FOCQty);
                Double dqty = new Double(Qty);
                Double dpcsPerCarton = 0.0;
                if (!PcsPerCarton.matches("")) {
                    dpcsPerCarton = new Double(PcsPerCarton);
                }

                int cQty = dcQty.intValue();
                int lQty = dlQty.intValue();
                // int qty = dqty.intValue();
                int focQty = dfocQty.intValue();
                int pcsPerCarton = dpcsPerCarton.intValue();

                Log.d("C Qty", "" + cQty);
                Log.d("L Qty", "" + lQty);
                // Log.d("Qty", "" + qty);
                Log.d("focQty", "" + focQty);
                Log.d("pcsPerCarton", "" + pcsPerCarton);

                double price = Double.parseDouble(Price);
                double itemDiscount = Double.parseDouble(ItemDiscount);
                double total = Double.parseDouble(Total);
                double tax = Double.parseDouble(Tax);

                Log.d("price", "" + price);
                Log.d("itemDiscount", "" + itemDiscount);
                Log.d("total", "" + total);
                Log.d("tax", "" + tax);

                HashMap<String, String> hv = new HashMap<String, String>();
                hv.put("slNo", slno + "");
                hv.put("ProductCode", ProductCode);
                hv.put("ProductName", ProductName);
                hv.put("CQty", cQty + "");
                hv.put("LQty", lQty + "");
                hv.put("Qty", dqty + "");
                hv.put("OriginalQty", dqty + "");
                hv.put("RCQty", "0");
                hv.put("RLQty", "0");
                hv.put("RQty", "0");
                hv.put("FOCQty", focQty + "");
                hv.put("PcsPerCarton", pcsPerCarton + "");
                hv.put("Price", price + "");
                hv.put("ItemDiscount", itemDiscount + "");
                hv.put("Total", total + "");
                hv.put("Tax", tax + "");
                hv.put("NetTotal", NetTotal);
                hv.put("SubTotal", SubTotal);
                hv.put("ReturnTax", "0.0");
                hv.put("ReturnNetTotal", "0.0");
                hv.put("ReturnSubTotal", "0.0");
                hv.put("TaxType", TaxType);
                hv.put("TaxPerc", TaxPerc);
                hv.put("RetailPrice", RetailPrice);
                hv.put("CartonPrice", CartonPrice);
                hv.put("ExchangeQty", ExchangeQty);
                hv.put("ItemRemarks", ItemRemarks);

                SOTDatabase.storeInvoiceReturn(hv);

                cursor = SOTDatabase.getCursor();
            }
        }

        int count = cursor.getCount();

        Log.d("cursor count", "" + count);
        if (count > 0) {
            adapter = new ProductListAdapter(this, cursor);
            productList.setAdapter(adapter);
            registerForContextMenu(productList);
        }

    }


    public void storeInDatabase() {

        String codeStr = sl_codefield.getText().toString();
        String nameStr = sl_namefield.getText().toString();
        String cartonQtyStr = sl_cartonQty.getText().toString();
        String looseQtyStr = sl_looseQty.getText().toString();
        String qtyStr = sl_qty.getText().toString();
        String rtn_cartonQtyStr = rtn_cartonQty.getText().toString();
        String rtn_looseQtyStr = rtn_looseQty.getText().toString();
        String rtn_qtyStr = rtn_qty.getText().toString();
        String priceStr = sl_price.getText().toString();
        String dicountStr = sl_itemDiscount.getText().toString();
        String uomStr = sl_uom.getText().toString();
        String cartonPerQtyStr = sl_cartonPerQty.getText().toString();
        String totalStr = sl_total.getText().toString();
        String taxStr = sl_tax.getText().toString();
        String netTotalStr = sl_netTotal.getText().toString();

        String return_subtotal = sl_return_subtotal.getText().toString();
        String return_tax = sl_return_tax.getText().toString();
        String return_netTotal = sl_return_netTotal.getText().toString();

        String cprice = sl_cprice.getText().toString();

        int slno = 0;
        if (codeStr.matches("")) {
            Toast.makeText(InvoiceReturn.this, "Select product code",
                    Toast.LENGTH_SHORT).show();
        } else if (rtn_qtyStr.matches("")) {
            Toast.makeText(InvoiceReturn.this, "Enter the return quantity",
                    Toast.LENGTH_SHORT).show();
        } else {

            int cartonQty = 0, looseQty = 0, qty = 0, foc = 0, cartonPerQty = 0;
            double price = 0, discount = 0, total = 0, tax = 0, subTotal = 0, ntTot = 0, total_old = 0;
            String sbTtl = "";
            String netT = "";
            if (!cartonQtyStr.matches("")) {
                cartonQty = Integer.parseInt(cartonQtyStr);
            } else {
                cartonQty = 0;
            }
            if (!looseQtyStr.matches("")) {
                looseQty = Integer.parseInt(looseQtyStr);
            } else {
                looseQty = 0;
            }

            if (rtn_cartonQtyStr.matches("")) {
                rtn_cartonQtyStr = "0";
            }

            if (rtn_looseQtyStr.matches("")) {
                rtn_looseQtyStr = "0";
            }

            if (!qtyStr.matches("")) {
                qty = Integer.parseInt(qtyStr);
            }

            if (!cartonPerQtyStr.matches("")) {
                cartonPerQty = Integer.parseInt(cartonPerQtyStr);
            }
            if (!priceStr.matches("")) {
                price = Double.parseDouble(priceStr);
            }

            if (!totalStr.matches("")) {
                total = Double.parseDouble(totalStr);

                String itemDisc = sl_itemDiscount.getText().toString();
                if (!itemDisc.matches("")) {
                    itmDisc = Double.parseDouble(itemDisc);
                    subTotal = total;

                    total_old = total - itmDisc;
                } else {
                    subTotal = total;

                    total_old = total;
                }

                sbTtl = twoDecimalPoint(subTotal);

                if (!ss_slid.matches("")) {
                    slno = Integer.parseInt(ss_slid);
                    SOTDatabase.updateBillDisc(slno, codeStr, sbTtl);
                }

            }

            Cursor bbillCursor = SOTDatabase.getBillCursor();

            if (bbillCursor != null && bbillCursor.getCount() > 0) {

                if (!ss_slid.matches("")) {
                    slno = Integer.parseInt(ss_slid);
                    double sbTotal = SOTDatabase.getsubTotal(slno);
                    Log.d("after update sub total", "" + sbTotal);
                }

            }

            if (!taxStr.matches("")) {
                tax = Double.parseDouble(taxStr);
            }
            if (!netTotalStr.matches("")) {
                ntTot = subTotal + tax;

                netT = twoDecimalPoint(ntTot);
            }
            if (cprice.matches("") || cprice == null) {
                cprice = "0";
            }
            if (taxValue.matches("") || taxValue == null) {
                taxValue = "0";
            }

            itemDiscountCalc();

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

            HashMap<String, String> hm = new HashMap<String, String>();
            Log.d("store..ss_slid", ss_slid);
            hm.put("ss_slid", ss_slid);
            hm.put("slNo", "" + slno);
            hm.put("ProductCode", codeStr);
            hm.put("ProductName", nameStr);
            hm.put("CQty", "" + cartonQty);
            hm.put("LQty", "" + looseQty);
            hm.put("Qty", "" + qty);
            hm.put("ReturnCQty", "" + rtn_cartonQtyStr);
            hm.put("ReturnLQty", "" + rtn_looseQtyStr);
            hm.put("ReturnQty", "" + rtn_qtyStr);
            hm.put("PcsPerCarton", "" + cartonPerQty);
            hm.put("Price", "" + price);
            hm.put("CPrice", "" + cprice);
            hm.put("Total", "" + total_old);
            hm.put("SubTotal", "" + sbTtl);
            hm.put("Tax", "" + tax);
            hm.put("NetTotal", "" + netT);

            Log.d("SubTotal tt", "" + sbTtl);
            Log.d("Tax tt", "" + tax);
            Log.d("NetTotal tt", "" + netT);

            hm.put("ReturnSubTotal", "" + return_subtotal);
            hm.put("ReturnTax", "" + return_tax);
            hm.put("ReturnNetTotal", "" + return_netTotal);

            hm.put("TaxType", "" + taxType);
            hm.put("TaxPerc", "" + taxValue);

            SOTDatabase.updateInvoiceReturn(hm);

            cursor.requery();
            int count = cursor.getCount();
            Log.d("after save cursor count", "" + count);

            billDiscCalc();
            totalTab();
            sl_codefield.setText("");
            sl_namefield.setText("");

            rtn_cartonQty.setText("");
            rtn_looseQty.setText("");
            rtn_qty.setText("");
            sl_cartonQty.setText("");
            sl_looseQty.setText("");

            sl_qty.removeTextChangedListener(qtyTW);
            sl_qty.setText("");
            sl_qty.addTextChangedListener(qtyTW);

            //sl_qty.setText("");

            sl_cprice.setText("");
            sl_itemDiscount.setText("");
            sl_total.setText("0");
            sl_tax.setText("0");
            sl_netTotal.setText("0");
            sl_price.setText("");
            sl_uom.setText("");
            sl_cartonPerQty.setText("");


            sl_return_subtotal.setText("0");
            sl_return_tax.setText("0");
            sl_return_netTotal.setText("0");

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rtn_qty.getWindowToken(), 0);

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


    private class ProductListAdapter extends ResourceCursorAdapter {
        @SuppressWarnings("deprecation")
        public ProductListAdapter(Context context, Cursor cursor) {
            super(context, R.layout.invoice_return_listitem, cursor);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView ss_sl_id = (TextView) view.findViewById(R.id.ss_slid);
            ss_sl_id.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_ID)));

            TextView ss_sl_no = (TextView) view.findViewById(R.id.ss_slno);
            ss_sl_no.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO)));

            TextView ss_prodcode = (TextView) view.findViewById(R.id.ss_prodcode);
            ss_prodcode.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE)));

            TextView ss_name = (TextView) view.findViewById(R.id.ss_name);
            ss_name.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME)));

            TextView ss_price = (TextView) view.findViewById(R.id.ss_price);

            Log.d("slPrice", "" + cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE)));

            ss_price.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_PRICE)));

            TextView ss_net_total = (TextView) view
                    .findViewById(R.id.ss_net_total);
            ss_net_total.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_NETTOTAL)));

            TextView ss_c_qty = (TextView) view.findViewById(R.id.ss_c_qty);
            ss_c_qty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));

            TextView ss_l_qty = (TextView) view.findViewById(R.id.ss_l_qty);
            ss_l_qty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));

            TextView ss_qty = (TextView) view.findViewById(R.id.ss_qty);
            ss_qty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));

            TextView ss_original_qty = (TextView) view.findViewById(R.id.ss_original_qty);
            ss_original_qty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_ORIGINAL_QTY)));

            TextView ss_return_cqty = (TextView) view.findViewById(R.id.ss_return_c_qty);
            ss_return_cqty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_RETURN_CARTON)));

            TextView ss_return_lqty = (TextView) view.findViewById(R.id.ss_return_l_qty);
            ss_return_lqty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_RETURN_LOOSE)));

            TextView ss_return_qty = (TextView) view.findViewById(R.id.ss_return_qty);
            ss_return_qty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_RETURN_QTY)));

            TextView ss_foc = (TextView) view.findViewById(R.id.ss_foc);
            ss_foc.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_FOC)));

            TextView ss_pcspercarton = (TextView) view.findViewById(R.id.ss_pcspercarton);
            ss_pcspercarton.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY)));

            TextView ss_item_disc = (TextView) view
                    .findViewById(R.id.ss_item_disc);
            ss_item_disc.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_ITEM_DISCOUNT)));

            TextView ss_total = (TextView) view.findViewById(R.id.ss_total);
            ss_total.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_TOTAL)));

            TextView ss_tax = (TextView) view.findViewById(R.id.ss_tax);
            ss_tax.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_TAX)));

            TextView ss_taxtype = (TextView) view.findViewById(R.id.ss_tax_type);
            ss_taxtype.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_TAXTYPE)));

            TextView ss_taxvalue = (TextView) view.findViewById(R.id.ss_tax_value);
            ss_taxvalue.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_TAXVALUE)));

            TextView ss_subTotal = (TextView) view
                    .findViewById(R.id.ss_subTotal);
            ss_subTotal.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_SUB_TOTAL)));

            TextView ss_cprice = (TextView) view.findViewById(R.id.ss_cprice);
            ss_cprice.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_CARTONPRICE)));

            TextView ss_exch_qty = (TextView) view
                    .findViewById(R.id.ss_exch_qty);
            ss_exch_qty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_EXCHANGEQTY)));

            TextView ss_minselling_price = (TextView) view
                    .findViewById(R.id.ss_minselling_price);
            ss_minselling_price.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_MINIMUM_SELLING_PRICE)));

            TextView itemRemarks = (TextView) view
                    .findViewById(R.id.ss_item_remarks);
            itemRemarks.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_ITEM_REMARKS)));


        }
    }

    public void totalTab() {

        double smTax, sbTtl, netTotal;

        //cursor = SOTDatabase.getCursor();

        int count = cursor.getCount();

        if (count > 0) {

            tota = SOTDatabase.getTotal();

            String smtotal = twoDecimalPoint(tota);
            double tot_item_disc = SOTDatabase.getTotalItemDisc();
            String tot_itemDisc = twoDecimalPoint(tot_item_disc);
            sm_itemDisc.setText(tot_itemDisc);
            sm_total_new.setText("" + smtotal);

            smTax = SOTDatabase.getTax();
            String ProdTax = fourDecimalPoint(smTax);
            sm_tax.setText("" + ProdTax);

            sbTtl = SOTDatabase.getSubTotal();
            String sub = twoDecimalPoint(sbTtl);
            sm_subTotal.setText("" + sub);

            netTotal = sbTtl + smTax;
            String ProdNettotal = twoDecimalPoint(netTotal);
            sm_netTotal.setText("" + ProdNettotal);

            double retSubTotal = SOTDatabase.getReturnSubTotal();
            double retTax = SOTDatabase.getReturnTax();
            double retNetTotal = SOTDatabase.getReturnNetTotal();

            String retsubTot = twoDecimalPoint(retSubTotal);
            String rettax = fourDecimalPoint(retTax);
            String retnetTot = twoDecimalPoint(retNetTotal);

            sm_return_subTotal.setText(retsubTot);
            sm_return_tax.setText(rettax);
            sm_return_netTotal.setText(retnetTot);

        }
    }


    public void clQty() {
        String qty = sl_qty.getText().toString();
        String crtnperQty = sl_cartonPerQty.getText().toString();
        int q = 0, r = 0;

        if (crtnperQty.matches("0") || crtnperQty.matches("null") || crtnperQty.matches("0.00")) {
            crtnperQty = "1";
        }

        if (!crtnperQty.matches("")) {
            if (!qty.matches("")) {
                try {
                    int qty_nt = Integer.parseInt(qty);
                    int pcs_nt = Integer.parseInt(crtnperQty);

                    Log.d("qty_nt", "" + qty_nt);
                    Log.d("pcs_nt", "" + pcs_nt);

                    q = qty_nt / pcs_nt;
                    r = qty_nt % pcs_nt;

                    Log.d("cqty", "" + q);
                    Log.d("lqty", "" + r);

                    sl_cartonQty.setText("" + q);
                    sl_looseQty.setText("" + r);

                } catch (ArithmeticException e) {
                    System.out.println("Err: Divided by Zero");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void cartonQty() {
        String crtnQty = sl_cartonQty.getText().toString();

        if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

            int cartonQtyCalc = Integer.parseInt(crtnQty);
            int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);
            int qty = 0;

            String lsQty = sl_looseQty.getText().toString();

            if (!lsQty.matches("")) {
                int lsQtyCnvrt = Integer.parseInt(lsQty);
                qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

            } else {
                qty = cartonQtyCalc * cartonPerQtyCalc;
            }

            sl_qty.removeTextChangedListener(qtyTW);
            sl_qty.setText("" + qty);
            sl_qty.addTextChangedListener(qtyTW);

            if (sl_qty.length() != 0) {
                sl_qty.setSelection(sl_qty.length());
            }

            productTotal(qty);
        }
    }

    public void cartonQtyNew() {
        String crtnQty = sl_cartonQty.getText().toString();

        if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

            int cartonQtyCalc = Integer.parseInt(crtnQty);
            int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);
            int qty = 0;

            String lsQty = sl_looseQty.getText().toString();

            if (!lsQty.matches("")) {
                int lsQtyCnvrt = Integer.parseInt(lsQty);
                qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

            } else {
                qty = cartonQtyCalc * cartonPerQtyCalc;
            }

            sl_qty.removeTextChangedListener(qtyTW);
            sl_qty.setText("" + qty);
            sl_qty.addTextChangedListener(qtyTW);

            if (sl_qty.length() != 0) {
                sl_qty.setSelection(sl_qty.length());
            }

            productTotalNew();
        }
    }

    public void looseQtyCalc() {
        String crtnQty = sl_cartonQty.getText().toString();
        String lsQty = sl_looseQty.getText().toString();

        if (lsQty.matches("")) {
            lsQty = "0";
        }

        if (!slCartonPerQty.matches("") && !crtnQty.matches("")
                && !lsQty.matches("")) {

            int cartonQtyCalc = Integer.parseInt(crtnQty);
            int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);
            int looseQtyCalc = Integer.parseInt(lsQty);

            int qty;
            qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
            sl_qty.removeTextChangedListener(qtyTW);
            sl_qty.setText("" + qty);
            sl_qty.addTextChangedListener(qtyTW);

            if (sl_qty.length() != 0) {
                sl_qty.setSelection(sl_qty.length());
            }

            productTotal(qty);
        }

        if (!lsQty.matches("")) {

            int looseQtyCalc = Integer.parseInt(lsQty);
            int qty;

            if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
                int cartonQtyCalc = Integer.parseInt(crtnQty);
                int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);

                qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
            } else {
                qty = looseQtyCalc;
            }

            sl_qty.removeTextChangedListener(qtyTW);
            sl_qty.setText("" + qty);
            sl_qty.addTextChangedListener(qtyTW);

            if (sl_qty.length() != 0) {
                sl_qty.setSelection(sl_qty.length());
            }

            productTotal(qty);
        }
    }

    public void looseQtyCalcNew() {
        String crtnQty = sl_cartonQty.getText().toString();
        String lsQty = sl_looseQty.getText().toString();

        if (lsQty.matches("")) {
            lsQty = "0";
        }

        if (!slCartonPerQty.matches("") && !crtnQty.matches("")
                && !lsQty.matches("")) {

            int cartonQtyCalc = Integer.parseInt(crtnQty);
            int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);
            int looseQtyCalc = Integer.parseInt(lsQty);

            int qty;
            qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

            sl_qty.removeTextChangedListener(qtyTW);
            sl_qty.setText("" + qty);
            sl_qty.addTextChangedListener(qtyTW);

            if (sl_qty.length() != 0) {
                sl_qty.setSelection(sl_qty.length());
            }

            productTotalNew();
        }

        if (!lsQty.matches("")) {

            int looseQtyCalc = Integer.parseInt(lsQty);
            int qty;

            if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
                int cartonQtyCalc = Integer.parseInt(crtnQty);
                int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);

                qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
            } else {
                qty = looseQtyCalc;
            }

            sl_qty.removeTextChangedListener(qtyTW);
            sl_qty.setText("" + qty);
            sl_qty.addTextChangedListener(qtyTW);

            if (sl_qty.length() != 0) {
                sl_qty.setSelection(sl_qty.length());
            }

            productTotalNew();
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
                double itemDiscountCalc = 0.0;
                itemDiscountCalc = Double.parseDouble(itmDscnt);

                int quantityCalc = Integer.parseInt(qty);
                double priceCalc = Double.parseDouble(prc);

                tt = (quantityCalc * priceCalc) - itemDiscountCalc;

                Log.d("ttl", "" + tt);
                String Prodtotal = twoDecimalPoint(tt);
                sl_total.setText("" + Prodtotal);

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

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);

                    } else if (taxType.matches("Z")) {

                        sl_tax.setText("0.0");

                        netTotal = tt + taxAmount;
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

    public void itemDiscountCalcNew() {

        try {
            String itmDscnt = sl_itemDiscount.getText().toString();
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

                tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc) - itemDiscountCalc;

                Log.d("ttl", "" + tt);
                String Prodtotal = twoDecimalPoint(tt);
                sl_total.setText("" + Prodtotal);

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

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);

                    } else if (taxType.matches("Z")) {

                        sl_tax.setText("0.0");

                        netTotal = tt + taxAmount;
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

                            netTotal1 = subTotal + taxAmount1;
                            String ProdNetTotal = twoDecimalPoint(netTotal1);
                            sl_netTotal.setText("" + ProdNetTotal);
                        } else {
                            taxAmount = (tt * taxValueCalc)
                                    / (100 + taxValueCalc);
                            String prodTax = fourDecimalPoint(taxAmount);
                            sl_tax.setText("" + prodTax);

                            netTotal = tt + taxAmount;
                            String ProdNetTotal = twoDecimalPoint(netTotal);
                            sl_netTotal.setText("" + ProdNetTotal);
                        }

                    } else if (taxType.matches("Z")) {

                        sl_tax.setText("0.0");
                        if (!itemDisc.matches("")) {
                            netTotal1 = subTotal + taxAmount;
                            String ProdNetTotal = twoDecimalPoint(netTotal1);
                            sl_netTotal.setText("" + ProdNetTotal);
                        } else {
                            netTotal = tt + taxAmount;
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

                        netTotal1 = subTotal + taxAmount1;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        sl_netTotal.setText("" + ProdNetTotal);
                    } else {
                        taxAmount = (tt * taxValueCalc)
                                / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_tax.setText("" + prodTax);

                        netTotal = tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_netTotal.setText("" + ProdNetTotal);
                    }

                } else if (taxType.matches("Z")) {

                    sl_tax.setText("0.0");
                    if (!itemDisc.matches("")) {
                        netTotal1 = subTotal + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        sl_netTotal.setText("" + ProdNetTotal);
                    } else {
                        netTotal = tt + taxAmount;
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

    /////////////////////////////////////////////////////////////////////////////////////////

    public void clQtyReturn() {
        String qty = rtn_qty.getText().toString();
        String crtnperQty = sl_cartonPerQty.getText().toString();
        int q = 0, r = 0;

        if (crtnperQty.matches("0") || crtnperQty.matches("null") || crtnperQty.matches("0.00")) {
            crtnperQty = "1";
        }

        if (!crtnperQty.matches("")) {
            if (!qty.matches("")) {
                try {
                    int qty_nt = Integer.parseInt(qty);
                    int pcs_nt = Integer.parseInt(crtnperQty);

                    Log.d("qty_nt", "" + qty_nt);
                    Log.d("pcs_nt", "" + pcs_nt);

                    q = qty_nt / pcs_nt;
                    r = qty_nt % pcs_nt;

                    Log.d("cqty", "" + q);
                    Log.d("lqty", "" + r);

                    rtn_cartonQty.setText("" + q);
                    rtn_looseQty.setText("" + r);

                } catch (ArithmeticException e) {
                    System.out.println("Err: Divided by Zero");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void cartonQtyReturn() {
        String crtnQty = rtn_cartonQty.getText().toString();

        if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

            int cartonQtyCalc = Integer.parseInt(crtnQty);
            int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);
            int qty = 0;

            String lsQty = rtn_looseQty.getText().toString();

            if (!lsQty.matches("")) {
                int lsQtyCnvrt = Integer.parseInt(lsQty);
                qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

            } else {
                qty = cartonQtyCalc * cartonPerQtyCalc;
            }

            //	rtn_qty.removeTextChangedListener(returnQtyTW);

            if (!StrOriginalQty.matches("")) {
                int quantity = Integer.parseInt(StrOriginalQty);

                if (qty > quantity) {
                    Toast.makeText(InvoiceReturn.this, "Return quantity " + qty + " exceeded current quantity " + quantity, Toast.LENGTH_LONG).show();
                    rtn_cartonQty.setText("");
                } else {
                    sl_qty.setText("" + (quantity - qty));
                    rtn_qty.removeTextChangedListener(returnQtyTW);
                    rtn_qty.setText("" + qty);
                    rtn_qty.addTextChangedListener(returnQtyTW);
                }
            }

//			rtn_qty.setText("" + qty);
            //rtn_qty.addTextChangedListener(returnQtyTW);

            if (rtn_qty.length() != 0) {
                rtn_qty.setSelection(rtn_qty.length());
            }

            productTotalReturn(qty);
        }
    }

    public void cartonQtyNewReturn() {
        String crtnQty = rtn_cartonQty.getText().toString();

        if (!slCartonPerQty.matches("") && !crtnQty.matches("")) {

            int cartonQtyCalc = Integer.parseInt(crtnQty);
            int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);
            int qty = 0;

            String lsQty = rtn_looseQty.getText().toString();

            if (!lsQty.matches("")) {
                int lsQtyCnvrt = Integer.parseInt(lsQty);
                qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

            } else {
                qty = cartonQtyCalc * cartonPerQtyCalc;
            }

//			rtn_qty.removeTextChangedListener(returnQtyTW);

            if (!StrOriginalQty.matches("")) {
                int quantity = Integer.parseInt(StrOriginalQty);

                if (qty > quantity) {
                    Toast.makeText(InvoiceReturn.this, "Return quantity " + qty + " exceeded current quantity " + quantity, Toast.LENGTH_LONG).show();
                    rtn_cartonQty.setText("");
                } else {
                    sl_qty.setText("" + (quantity - qty));
                    rtn_qty.removeTextChangedListener(returnQtyTW);
                    rtn_qty.setText("" + qty);
                    rtn_qty.addTextChangedListener(returnQtyTW);
                }
            }

            //rtn_qty.setText("" + qty);
//			rtn_qty.addTextChangedListener(returnQtyTW);

            if (rtn_qty.length() != 0) {
                rtn_qty.setSelection(rtn_qty.length());
            }

            productTotalNewReturn();
        }
    }

    public void looseQtyCalcReturn() {
        String crtnQty = rtn_cartonQty.getText().toString();
        String lsQty = rtn_looseQty.getText().toString();

        if (lsQty.matches("")) {
            lsQty = "0";
        }

        if (!slCartonPerQty.matches("") && !crtnQty.matches("")
                && !lsQty.matches("")) {

            int cartonQtyCalc = Integer.parseInt(crtnQty);
            int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);
            int looseQtyCalc = Integer.parseInt(lsQty);

            int qty;
            qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
//			rtn_qty.removeTextChangedListener(returnQtyTW);
//			rtn_qty.setText("" + qty);
//			rtn_qty.addTextChangedListener(returnQtyTW);

            if (!StrOriginalQty.matches("")) {
                int quantity = Integer.parseInt(StrOriginalQty);

                if (qty > quantity) {
                    Toast.makeText(InvoiceReturn.this, "Return quantity " + qty + " exceeded current quantity " + quantity, Toast.LENGTH_LONG).show();
                    rtn_cartonQty.setText("");
                } else {
                    sl_qty.setText("" + (quantity - qty));
                    rtn_qty.removeTextChangedListener(returnQtyTW);
                    rtn_qty.setText("" + qty);
                    rtn_qty.addTextChangedListener(returnQtyTW);
                }
            }

            if (rtn_qty.length() != 0) {
                rtn_qty.setSelection(rtn_qty.length());
            }

            productTotalReturn(qty);
        }

        if (!lsQty.matches("")) {

            int looseQtyCalc = Integer.parseInt(lsQty);
            int qty;

            if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
                int cartonQtyCalc = Integer.parseInt(crtnQty);
                int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);

                qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
            } else {
                qty = looseQtyCalc;
            }

//			rtn_qty.removeTextChangedListener(returnQtyTW);
//			rtn_qty.setText("" + qty);
//			rtn_qty.addTextChangedListener(returnQtyTW);

            if (!StrOriginalQty.matches("")) {
                int quantity = Integer.parseInt(StrOriginalQty);

                if (qty > quantity) {
                    Toast.makeText(InvoiceReturn.this, "Return quantity " + qty + " exceeded current quantity " + quantity, Toast.LENGTH_LONG).show();
                    rtn_cartonQty.setText("");
                } else {
                    sl_qty.setText("" + (quantity - qty));
                    rtn_qty.removeTextChangedListener(returnQtyTW);
                    rtn_qty.setText("" + qty);
                    rtn_qty.addTextChangedListener(returnQtyTW);
                }
            }

            if (rtn_qty.length() != 0) {
                rtn_qty.setSelection(rtn_qty.length());
            }

            productTotalReturn(qty);
        }
    }

    public void looseQtyCalcNewReturn() {
        String crtnQty = rtn_cartonQty.getText().toString();
        String lsQty = rtn_looseQty.getText().toString();

        if (lsQty.matches("")) {
            lsQty = "0";
        }

        if (!slCartonPerQty.matches("") && !crtnQty.matches("")
                && !lsQty.matches("")) {

            int cartonQtyCalc = Integer.parseInt(crtnQty);
            int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);
            int looseQtyCalc = Integer.parseInt(lsQty);

            int qty;
            qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

//			rtn_qty.removeTextChangedListener(returnQtyTW);
//			rtn_qty.setText("" + qty);
//			rtn_qty.addTextChangedListener(returnQtyTW);

            if (!StrOriginalQty.matches("")) {
                int quantity = Integer.parseInt(StrOriginalQty);

                if (qty > quantity) {
                    Toast.makeText(InvoiceReturn.this, "Return quantity " + qty + " exceeded current quantity " + quantity, Toast.LENGTH_LONG).show();
                    rtn_cartonQty.setText("");
                } else {
                    sl_qty.setText("" + (quantity - qty));
                    rtn_qty.removeTextChangedListener(returnQtyTW);
                    rtn_qty.setText("" + qty);
                    rtn_qty.addTextChangedListener(returnQtyTW);
                }
            }

            if (rtn_qty.length() != 0) {
                rtn_qty.setSelection(rtn_qty.length());
            }

            productTotalNewReturn();
        }

        if (!lsQty.matches("")) {

            int looseQtyCalc = Integer.parseInt(lsQty);
            int qty;

            if (!crtnQty.matches("") && !slCartonPerQty.matches("")) {
                int cartonQtyCalc = Integer.parseInt(crtnQty);
                int cartonPerQtyCalc = Integer.parseInt(slCartonPerQty);

                qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
            } else {
                qty = looseQtyCalc;
            }

//			rtn_qty.removeTextChangedListener(returnQtyTW);
//			rtn_qty.setText("" + qty);
//			rtn_qty.addTextChangedListener(returnQtyTW);

            if (!StrOriginalQty.matches("")) {
                int quantity = Integer.parseInt(StrOriginalQty);

                if (qty > quantity) {
                    Toast.makeText(InvoiceReturn.this, "Return quantity " + qty + " exceeded current quantity " + quantity, Toast.LENGTH_LONG).show();
                    rtn_cartonQty.setText("");
                } else {
                    sl_qty.setText("" + (quantity - qty));
                    rtn_qty.removeTextChangedListener(returnQtyTW);
                    rtn_qty.setText("" + qty);
                    rtn_qty.addTextChangedListener(returnQtyTW);
                }
            }

            if (rtn_qty.length() != 0) {
                rtn_qty.setSelection(rtn_qty.length());
            }

            productTotalNewReturn();
        }
    }

    public void itemDiscountCalcReturn() {

        try {
            String itmDscnt = sl_itemDiscount.getText().toString();
            String qty = rtn_qty.getText().toString();
            String prc = sl_price.getText().toString();
            if (itmDscnt.matches("")) {
                itmDscnt = "0";
            }

            if (!itmDscnt.matches("") && !qty.matches("") && !prc.matches("")) {
                double itemDiscountCalc = 0.0;
                itemDiscountCalc = Double.parseDouble(itmDscnt);

                int quantityCalc = Integer.parseInt(qty);
                double priceCalc = Double.parseDouble(prc);

                return_tt = (quantityCalc * priceCalc) - itemDiscountCalc;

                Log.d("ttl", "" + return_tt);
                String Prodtotal = twoDecimalPoint(return_tt);
                sl_return_subtotal.setText("" + Prodtotal);

                double taxAmount = 0.0, netTotal = 0.0;
                if (!taxType.matches("") && !taxValue.matches("")) {

                    double taxValueCalc = Double.parseDouble(taxValue);

                    if (taxType.matches("E")) {

                        taxAmount = (return_tt * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_return_tax.setText("" + prodTax);

                        netTotal = return_tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_return_netTotal.setText("" + ProdNetTotal);

                    } else if (taxType.matches("I")) {

                        taxAmount = (return_tt * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_return_tax.setText("" + prodTax);

                        netTotal = return_tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_return_netTotal.setText("" + ProdNetTotal);

                    } else if (taxType.matches("Z")) {

                        sl_return_tax.setText("0.0");

                        netTotal = return_tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_return_netTotal.setText("" + ProdNetTotal);

                    } else {
                        sl_return_tax.setText("0.0");
                        sl_return_netTotal.setText("" + Prodtotal);
                    }
                } else if (taxValue.matches("")) {
                    sl_return_tax.setText("0.0");
                    sl_return_netTotal.setText("" + Prodtotal);
                } else {
                    sl_return_tax.setText("0.0");
                    sl_return_netTotal.setText("" + Prodtotal);
                }
            }

        } catch (Exception e) {

        }
    }

    public void itemDiscountCalcNewReturn() {

        try {
            String itmDscnt = sl_itemDiscount.getText().toString();
            if (itmDscnt.matches("")) {
                itmDscnt = "0";
            }

            String lPrice = sl_price.getText().toString();
            String cPrice = sl_cprice.getText().toString();
            String cqty = rtn_cartonQty.getText().toString();
            String lqty = rtn_looseQty.getText().toString();

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

                return_tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc) - itemDiscountCalc;

                Log.d("ttl", "" + return_tt);
                String Prodtotal = twoDecimalPoint(return_tt);
                sl_return_subtotal.setText("" + Prodtotal);

                double taxAmount = 0.0, netTotal = 0.0;
                if (!taxType.matches("") && !taxValue.matches("")) {

                    double taxValueCalc = Double.parseDouble(taxValue);

                    if (taxType.matches("E")) {

                        taxAmount = (return_tt * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_return_tax.setText("" + prodTax);

                        netTotal = return_tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_return_netTotal.setText("" + ProdNetTotal);

                    } else if (taxType.matches("I")) {

                        taxAmount = (return_tt * taxValueCalc) / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_return_tax.setText("" + prodTax);

                        netTotal = return_tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_return_netTotal.setText("" + ProdNetTotal);

                    } else if (taxType.matches("Z")) {

                        sl_return_tax.setText("0.0");

                        netTotal = return_tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_return_netTotal.setText("" + ProdNetTotal);

                    } else {
                        sl_return_tax.setText("0.0");
                        sl_return_netTotal.setText("" + Prodtotal);
                    }
                } else if (taxValue.matches("")) {
                    sl_return_tax.setText("0.0");
                    sl_return_netTotal.setText("" + Prodtotal);
                } else {
                    sl_return_tax.setText("0.0");
                    sl_return_netTotal.setText("" + Prodtotal);
                }
            }

        } catch (Exception e) {

        }
    }

    public void productTotalReturn(double qty) {

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
                    return_tt = (qty * slPriceCalc);
                } else {
                    return_tt = qty * slPriceCalc;
                }

                String Prodtotal = twoDecimalPoint(return_tt);

                double subTotal = 0.0;

                String itemDisc = sl_itemDiscount.getText().toString();
                if (!itemDisc.matches("")) {
                    return_itmDisc = Double.parseDouble(itemDisc);
                    subTotal = return_tt - return_itmDisc;
                } else {
                    subTotal = return_tt;
                }

                String sbTtl = twoDecimalPoint(subTotal);

                sl_return_subtotal.setText("" + sbTtl);

                if (!taxType.matches("") && !taxValue.matches("")) {

                    double taxValueCalc = Double.parseDouble(taxValue);

                    if (taxType.matches("E")) {

                        if (!itemDisc.matches("")) {
                            taxAmount1 = (subTotal * taxValueCalc) / 100;
                            String prodTax = fourDecimalPoint(taxAmount1);
                            sl_return_tax.setText("" + prodTax);

                            netTotal1 = subTotal + taxAmount1;
                            String ProdNetTotal = twoDecimalPoint(netTotal1);
                            sl_return_netTotal.setText("" + ProdNetTotal);
                        } else {

                            taxAmount = (return_tt * taxValueCalc) / 100;
                            String prodTax = fourDecimalPoint(taxAmount);
                            sl_return_tax.setText("" + prodTax);

                            netTotal = return_tt + taxAmount;
                            String ProdNetTotal = twoDecimalPoint(netTotal);
                            sl_return_netTotal.setText("" + ProdNetTotal);
                        }

                    } else if (taxType.matches("I")) {
                        if (!itemDisc.matches("")) {
                            taxAmount1 = (subTotal * taxValueCalc)
                                    / (100 + taxValueCalc);
                            String prodTax = fourDecimalPoint(taxAmount1);
                            sl_return_tax.setText("" + prodTax);

//							netTotal1 = subTotal + taxAmount1;
                            netTotal1 = subTotal;
                            String ProdNetTotal = twoDecimalPoint(netTotal1);
                            sl_return_netTotal.setText("" + ProdNetTotal);
                        } else {
                            taxAmount = (return_tt * taxValueCalc)
                                    / (100 + taxValueCalc);
                            String prodTax = fourDecimalPoint(taxAmount);
                            sl_return_tax.setText("" + prodTax);

//							netTotal = return_tt + taxAmount;
                            netTotal = return_tt;
                            String ProdNetTotal = twoDecimalPoint(netTotal);
                            sl_return_netTotal.setText("" + ProdNetTotal);
                        }

                    } else if (taxType.matches("Z")) {

                        sl_return_tax.setText("0.0");
                        if (!itemDisc.matches("")) {
//							netTotal1 = subTotal + taxAmount;
                            netTotal1 = subTotal;
                            String ProdNetTotal = twoDecimalPoint(netTotal1);
                            sl_return_netTotal.setText("" + ProdNetTotal);
                        } else {
//							netTotal = return_tt + taxAmount;
                            netTotal = return_tt;
                            String ProdNetTotal = twoDecimalPoint(netTotal);
                            sl_return_netTotal.setText("" + ProdNetTotal);
                        }

                    } else {
                        sl_return_tax.setText("0.0");
                        sl_return_netTotal.setText("" + Prodtotal);
                    }

                } else if (taxValue.matches("")) {
                    sl_return_tax.setText("0.0");
                    sl_return_netTotal.setText("" + Prodtotal);
                } else {
                    sl_return_tax.setText("0.0");
                    sl_return_netTotal.setText("" + Prodtotal);
                }
            }
        } catch (Exception e) {

        }
    }

    public void productTotalNewReturn() {

        try {
            double taxAmount = 0.0, netTotal = 0.0;
            double taxAmount1 = 0.0, netTotal1 = 0.0;

            String lPrice = sl_price.getText().toString();
            String cPrice = sl_cprice.getText().toString();
            String cqty = rtn_cartonQty.getText().toString();
            String lqty = rtn_looseQty.getText().toString();

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

            return_tt = (cqtyCalc * cPriceCalc) + (lqtyCalc * lPriceCalc);

            String Prodtotal = twoDecimalPoint(return_tt);

            double subTotal = 0.0;

            String itemDisc = sl_itemDiscount.getText().toString();
            if (!itemDisc.matches("")) {
                return_itmDisc = Double.parseDouble(itemDisc);
                subTotal = return_tt - return_itmDisc;
            } else {
                subTotal = return_tt;
            }

            String sbTtl = twoDecimalPoint(subTotal);

            sl_return_subtotal.setText("" + sbTtl);

            if (!taxType.matches("") && !taxValue.matches("")) {

                double taxValueCalc = Double.parseDouble(taxValue);

                if (taxType.matches("E")) {

                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subTotal * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount1);
                        sl_return_tax.setText("" + prodTax);

                        netTotal1 = subTotal + taxAmount1;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        sl_return_netTotal.setText("" + ProdNetTotal);
                    } else {

                        taxAmount = (return_tt * taxValueCalc) / 100;
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_return_tax.setText("" + prodTax);

                        netTotal = return_tt + taxAmount;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_return_netTotal.setText("" + ProdNetTotal);
                    }

                } else if (taxType.matches("I")) {
                    if (!itemDisc.matches("")) {
                        taxAmount1 = (subTotal * taxValueCalc)
                                / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount1);
                        sl_return_tax.setText("" + prodTax);

//							netTotal1 = subTotal + taxAmount1;
                        netTotal1 = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        sl_return_netTotal.setText("" + ProdNetTotal);
                    } else {
                        taxAmount = (return_tt * taxValueCalc)
                                / (100 + taxValueCalc);
                        String prodTax = fourDecimalPoint(taxAmount);
                        sl_return_tax.setText("" + prodTax);

//							netTotal = return_tt + taxAmount;
                        netTotal = return_tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_return_netTotal.setText("" + ProdNetTotal);
                    }

                } else if (taxType.matches("Z")) {

                    sl_return_tax.setText("0.0");
                    if (!itemDisc.matches("")) {
//							netTotal1 = subTotal + taxAmount;
                        netTotal1 = subTotal;
                        String ProdNetTotal = twoDecimalPoint(netTotal1);
                        sl_return_netTotal.setText("" + ProdNetTotal);
                    } else {
//							netTotal = return_tt + taxAmount;
                        netTotal = return_tt;
                        String ProdNetTotal = twoDecimalPoint(netTotal);
                        sl_return_netTotal.setText("" + ProdNetTotal);
                    }

                } else {
                    sl_return_tax.setText("0.0");
                    sl_return_netTotal.setText("" + Prodtotal);
                }

            } else if (taxValue.matches("")) {
                sl_return_tax.setText("0.0");
                sl_return_netTotal.setText("" + Prodtotal);
            } else {
                sl_return_tax.setText("0.0");
                sl_return_netTotal.setText("" + Prodtotal);
            }

        } catch (Exception e) {

        }
    }


    private class AsyncCallWSSummary extends AsyncTask<Void, Void, Void> {

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            spinnerLayout = new LinearLayout(InvoiceReturn.this);
            spinnerLayout.setGravity(Gravity.CENTER);
            addContentView(spinnerLayout, new LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
            enableViews(invoive_return_layout, false);
            progressBar = new ProgressBar(InvoiceReturn.this);
            progressBar.setProgress(android.R.attr.progressBarStyle);
            progressBar.setIndeterminateDrawable(getResources().getDrawable(
                    drawable.greenprogress));
            spinnerLayout.addView(progressBar);
            summaryResult = "";
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            double discnt = 0.0;
            String bllDsc = sm_billDisc.getText().toString();
            String return_subTotal = sm_return_subTotal.getText().toString();
            String return_tax = sm_return_tax.getText().toString();
            String return_netTotal = sm_return_netTotal.getText().toString();

            String total = sm_total_new.getText().toString();
            String subTot = sm_subTotal.getText().toString();
            String netTot = sm_netTotal.getText().toString();
            String totTax = sm_tax.getText().toString();

            if (!bllDsc.matches("")) {
                discnt = Double.parseDouble(bllDsc);
            }

            try {
                summaryResult = SOTSummaryWebService.summaryInvoiceReturnService(
                        "fncSaveInvoiceReturn", discnt, return_subTotal, return_tax, return_netTotal, subTot, totTax, netTot,
                        total, StrinvNo);

//				summaryResult = StrinvNo;

                Log.d("sales return no", summaryResult);

                if (!summaryResult.matches("failed")) {

                    String cmpnyCode = SupplierSetterGetter.getCompanyCode();
                    hm.put("CompanyCode", cmpnyCode);

                    summaryResult = StrinvNo;

                    hm.put("InvoiceNo", summaryResult);
                    jsonString = SalesOrderWebService.getSODetail(hm,
                            "fncGetInvoiceHeaderByInvoiceNo");
                    jsonResponse = new JSONObject(jsonString);
                    jsonMainNode = jsonResponse.optJSONArray("SODetails");

                    int lengthJsonAr = jsonMainNode.length();

                    for (int k = 0; k < lengthJsonAr; k++) {
                        /****** Get Object for each JSON node. ***********/
                        JSONObject jsonChildNodes;

                        jsonChildNodes = jsonMainNode.getJSONObject(k);
                        totalbalance = jsonChildNodes.optString("TotalBalance")
                                .toString();
                        ProdDetails.setTotalbalance(totalbalance);
                    }
                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Void result) {

            if (summaryResult.matches("failed")) {

                Toast.makeText(InvoiceReturn.this, "Failed", Toast.LENGTH_SHORT)
                        .show();

                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(invoive_return_layout, true);

            } else {

                Toast.makeText(InvoiceReturn.this, "Saved Successfully",
                        Toast.LENGTH_SHORT).show();
//				Intent i = new Intent(InvoiceReturn.this, SalesReturnHeader.class);
//				startActivity(i);
//				InvoiceReturn.this.finish();

                progressBar.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                enableViews(invoive_return_layout, true);

                if (enableprint.isChecked()) {

                    if (FWMSSettingsDatabase
                            .getPrinterAddress()
                            .matches(
                                    "[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$")) {
                    /*
					 * helper.showProgressDialog(R.string.generating_invoice)
					 * ; AsyncGeneralSettings asyncgs = new
					 * AsyncGeneralSettings(); asyncgs.execute();
					 */
//					if (cash_checkbox.isChecked()) {
//						if (ReceiptNo.matches("null")
//								|| ReceiptNo.matches("")) {
//							Toast.makeText(InvoiceSummary.this,
//									" Receipt Failed", Toast.LENGTH_SHORT)
//									.show();
//						} else {
//							helper.showProgressDialog(R.string.generating_receipt);
//							new AsyncGeneralSettings().execute();
//
//						}
//					} else {
                        helper.showProgressDialog(R.string.generating_invoice);
                        new AsyncGeneralSettings().execute();
//					}

                    } else {
                        helper.showLongToast(R.string.error_configure_printer);
                        clearView();
                    }
                } else {
                    clearView();
                }
            }
        }
    }


    private class AsyncGeneralSettings extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            jsonString = WebServiceClass.URLService("fncGetGeneralSettings");

            Log.d("jsonString ", jsonString);

            try {

                jsonResponse = new JSONObject(jsonString);
                jsonMainNode = jsonResponse.optJSONArray("JsonArray");

            } catch (JSONException e) {
                // TODO Auto-generated catch block
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
                    // TODO Auto-generated catch block
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

        @Override
        protected void onPreExecute() {
            sortproduct.clear();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (gnrlStngs.matches("C")) {
                jsonString = WebServiceClass.URLService("fncGetCategory");
                Log.d("jsonString ", jsonString);

                try {

                    jsonResponse = new JSONObject(jsonString);
                    jsonMainNode = jsonResponse.optJSONArray("JsonArray");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
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
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            } else if (gnrlStngs.matches("S")) {
                jsonStr = WebServiceClass.URLService("fncGetSubCategory");
                Log.d("jsonStr ", jsonStr);

                try {

                    jsonResponse = new JSONObject(jsonStr);
                    jsonSecNode = jsonResponse.optJSONArray("JsonArray");

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
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            new AsyncInvoicePrintCall().execute();

        }

    }

    public void sortCatagory() {
        for (int i = 0; i < sortproduct.size(); i++) {
            String catagory = sortproduct.get(i).toString();
            for (ProductDetails products : product) {

                if (catagory.matches(products.getSortproduct())) {

                    sort.add(catagory);
                }
            }
        }
        hs.addAll(sort);
        printsortHeader.clear();
        printsortHeader.addAll(hs);
    }

    private class AsyncInvoicePrintCall extends
            AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            product.clear();
            productdet.clear();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... arg0) {

            String decimalpts = ".00";

            String cmpnyCode = SupplierSetterGetter.getCompanyCode();
            hashValue.put("CompanyCode", cmpnyCode);
            hashValue.put("InvoiceNo", summaryResult);

            jsonString = SalesOrderWebService.getSODetail(hashValue,
                    "fncGetInvoiceDetail");
            jsonStr = SalesOrderWebService.getSODetail(hashValue,
                    "fncGetInvoiceHeaderByInvoiceNo");

            Log.d("jsonString ", jsonString);
            Log.d("jsonStr ", jsonStr);

            try {

                jsonResponse = new JSONObject(jsonString);
                jsonMainNode = jsonResponse.optJSONArray("SODetails");

                jsonResp = new JSONObject(jsonStr);
                jsonSecNode = jsonResp.optJSONArray("SODetails");

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /*********** Process each JSON Node ************/
            int lengthJsonArr = jsonMainNode.length();
            for (int i = 0; i < lengthJsonArr; i++) {
                /****** Get Object for each JSON node. ***********/
                JSONObject jsonChildNode;
                ProductDetails productdetail = new ProductDetails();
                try {
                    jsonChildNode = jsonMainNode.getJSONObject(i);
                    int s = i + 1;
                    productdetail.setSno(String.valueOf(s));
                    productdetail.setItemcode(jsonChildNode.optString(
                            "ProductCode").toString());
                    productdetail.setDescription(jsonChildNode.optString(
                            "ProductName").toString());
                    // productdetail.setQty(jsonChildNode.optString("Qty").toString());

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
                    String totalvalve = jsonChildNode.optString("Total")
                            .toString();

                    String focvalue = jsonChildNode.optString("FOCQty")
                            .toString();
                    StringTokenizer tokens = new StringTokenizer(focvalue, ".");
                    String foc = tokens.nextToken();

                    if (!foc.matches("")) {
                        productdetail.setFocqty(Double.valueOf(foc));
                    }
                    if (totalvalve.contains(".") && pricevalue.contains(".")) {
                        productdetail.setPrice(pricevalue);
                        productdetail.setTotal(totalvalve);
                    } else {
                        productdetail.setPrice(pricevalue + decimalpts);
                        productdetail.setTotal(totalvalve + decimalpts);
                    }
                    if (gnrlStngs.matches("C")) {
                        productdetail.setSortproduct(jsonChildNode.optString(
                                "CategoryCode").toString());
                    } else if (gnrlStngs.matches("S")) {
                        productdetail.setSortproduct(jsonChildNode.optString(
                                "SubCategoryCode").toString());
                    } else if (gnrlStngs.matches("N")) {
                        productdetail.setSortproduct("");
                    } else {
                        productdetail.setSortproduct("");
                    }
                    product.add(productdetail);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            /*********** Process each JSON Node ************/
            int lengJsonArr = jsonSecNode.length();
            for (int i = 0; i < lengJsonArr; i++) {
                /****** Get Object for each JSON node. ***********/
                JSONObject jsonChildNode;
                ProductDetails productdetail = new ProductDetails();
                try {
                    jsonChildNode = jsonSecNode.getJSONObject(i);

                    String itemdiscvalue = jsonChildNode.optString(
                            "ItemDiscount").toString();
                    String billdiscvalue = jsonChildNode.optString(
                            "BillDIscount").toString();
                    String subtotalvalue = jsonChildNode.optString("SubTotal")
                            .toString();
                    productdetail.setTax(twoDecimalPoint(jsonChildNode
                            .optDouble("Tax")));
                    String nettotalvalue = jsonChildNode.optString("NetTotal")
                            .toString();

                    if (itemdiscvalue.contains(".")
                            && billdiscvalue.contains(".")
                            && subtotalvalue.contains(".")
                            && nettotalvalue.contains(".")) {
                        productdetail.setItemdisc(itemdiscvalue);
                        productdetail.setBilldisc(billdiscvalue);
                        productdetail.setSubtotal(subtotalvalue);
                        productdetail.setNettot(nettotalvalue);
                    } else {
                        productdetail.setItemdisc(itemdiscvalue + decimalpts);
                        productdetail.setBilldisc(billdiscvalue + decimalpts);
                        productdetail.setSubtotal(subtotalvalue + decimalpts);
                        productdetail.setNettot(nettotalvalue + decimalpts);
                    }
                    productdetail.setRemarks(jsonChildNode.optString("Remarks")
                            .toString());
                    productdetail.setTotaloutstanding(jsonChildNode.optString(
                            "TotalBalance").toString());
                    productdet.add(productdetail);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            hashValue.clear();
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {

            try {
                sortCatagory();
                print();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /////////////

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
        cash_checkbox = (CheckBox) eulaLayout.findViewById(R.id.cash_checkbox);
        cash_checkbox.setVisibility(View.GONE);
//		String receiptOnInvoice = SalesOrderSetGet.getReceiptoninvoice();

//		if (receiptOnInvoice.matches("1")) {
//			cash_checkbox.setChecked(true);
//			cash_checkbox.setClickable(false);
//			cash_checkbox.setTextColor(Color.parseColor("#cccccc"));
//		} else {
//			cash_checkbox.setChecked(false);
//		}

        enableprint = (CheckBox) eulaLayout.findViewById(R.id.checkbox);
//		cash_checkbox.setText("Cash Collected");
        enableprint.setText("Print");

//		SalesOrderSetGet.setsCollectCash("");

//		if (cash_checkbox.isChecked()) {
//			SalesOrderSetGet.setsCollectCash("1");
//		} else {
//			SalesOrderSetGet.setsCollectCash("0");
//		}

//		cash_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//
//				if (isChecked == true) {
//					SalesOrderSetGet.setsCollectCash("1");
//				} else {
//					SalesOrderSetGet.setsCollectCash("0");
//				}
//
//			}
//		});

        enableprint.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked == true) {
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
                }
            }
        });

        alertDialog.setView(eulaLayout);
        int modeid = FWMSSettingsDatabase.getModeId();
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

                        String billDisc = sm_billDisc.getText().toString();
                        subTot = sm_subTotal.getText().toString();
                        totTax = sm_tax.getText().toString();
                        netTot = sm_netTotal.getText().toString();
                        tot = sm_total.getText().toString();
                        if (!billDisc.matches("")) {
                            double billDiscCalc = Double.parseDouble(billDisc);
                            double sbtot = SOTDatabase.getsumsubTot();
                            billDiscount = billDiscCalc / sbtot;
                        }
                        if (cursor != null && cursor.getCount() > 0) {

                            AsyncCallWSSummary task = new AsyncCallWSSummary();
                            task.execute();

                        } else {
                            Toast.makeText(InvoiceReturn.this,
                                    "No data found", Toast.LENGTH_SHORT).show();
                        }
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

    @SuppressWarnings("deprecation")
    private void print() throws IOException {
        int nofcopies = Integer.parseInt(stnumber.getText().toString());
        String customerCode = SalesOrderSetGet.getCustomercode();
        String customerName = SalesOrderSetGet.getCustomername();
        String soDate = SalesOrderSetGet.getSaleorderdate();
        String bllDsc = sm_billDisc.getText().toString();
        if (!bllDsc.matches("")) {
        }
        helper.dismissProgressDialog();
        String macaddress = FWMSSettingsDatabase.getPrinterAddress();
        try {
            Printer printer = new Printer(InvoiceReturn.this, macaddress);
            printer.setOnCompletionListener(new Printer.OnCompletionListener() {

                @Override
                public void onCompleted() {
                    // TODO Auto-generated method stub
                    finish();

                    Intent intent = new Intent(InvoiceReturn.this,
                            SalesReturnHeader.class);


                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    InvoiceReturn.this.finish();
                    SOTDatabase.deleteImage();
                }
            });
            List<ProductDetails> product_batch = new ArrayList<ProductDetails>();
            List<ProductDetails> footer = new ArrayList<ProductDetails>();

            printer.printInvoice(summaryResult, soDate, customerCode,
                    customerName, product, productdet, printsortHeader,
                    gnrlStngs, nofcopies, product_batch, footer,"","","","");

        } catch (IllegalArgumentException e) {
            helper.showLongToast(R.string.error_configure_printer);
        } catch (Exception e) {
            helper.showLongToast("Error while printing");
        }

        SOTDatabase.deleteAllProduct();
        cursor.requery();
        //mEmpty.setVisibility(View.VISIBLE);
        sm_total.setText("");
        sm_billDisc.setText("");
        sm_billDisc.setFocusable(false);
        sm_tax.setText("");
        sm_return_subTotal.setText("");
        sm_return_tax.setText("");
        sm_return_netTotal.setText("");
        //sm_header_layout.setVisibility(View.GONE);
        sm_subTotal.setText("");
        sm_netTotal.setText("");
        save_return.setVisibility(View.INVISIBLE);
        summary_return.setVisibility(View.INVISIBLE);
        SOTDatabase.deleteBillDisc();
        SOTDatabase.deleteallbatch();
        SalesOrderSetGet.setCustomercode("");
        SalesOrderSetGet.setCustomername("");

    }


    @SuppressWarnings("deprecation")
    public void clearView() {

        SOTDatabase.deleteAllProduct();
        cursor.requery();
        //mEmpty.setVisibility(View.VISIBLE);
        sm_total.setText("");
        sm_billDisc.setText("");
        sm_billDisc.setFocusable(false);
        sm_tax.setText("");
        sm_return_subTotal.setText("");
        sm_return_tax.setText("");
        sm_return_netTotal.setText("");
        //sm_header_layout.setVisibility(View.GONE);
        sm_subTotal.setText("");
        sm_netTotal.setText("");
        save_return.setVisibility(View.INVISIBLE);
        summary_return.setVisibility(View.INVISIBLE);
        SOTDatabase.deleteBillDisc();
        SOTDatabase.deleteallbatch();
        SalesOrderSetGet.setCustomercode("");
        SalesOrderSetGet.setCustomername("");

        Intent intent = new Intent(InvoiceReturn.this, SalesReturnHeader.class);
        startActivity(intent);
        InvoiceReturn.this.finish();

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
    public void onBackPressed() {
        Intent i = new Intent(InvoiceReturn.this, InvoiceHeader.class);
        startActivity(i);
        InvoiceReturn.this.finish();
    }
}
