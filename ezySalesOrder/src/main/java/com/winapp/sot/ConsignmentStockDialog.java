package com.winapp.sot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;



import com.winapp.SFA.R;
import com.winapp.util.CustomCalendar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Sathish on 19-02-2019.
 */

public class ConsignmentStockDialog extends DialogFragment {
    AlertDialog.Builder myDialog;
    ConsignmentListAdapter consignmentListAdapter;
    Calendar mCalendar;

    String code = "";
    EditText expirydate, edtotalqty;
    EditText cons_codefield, cons_namefield, consno, cons_cartonQty,
            cons_looseQty, cons_qty, cons_cartonPerQty, sl_productcode, sl_name,
            sl_cartonqty, sl_looseqty, sl_qty, sl_uom, sl_stock,
            sl_pcspercarton, slPrice, sm_cartonqty, sm_looseqty, sm_qty, slCprice, sl_foc; // bat_foc,
    // sl_foc,
    ImageButton cons_add, cons_finish, cons_cancel;
    ListView batch_list;
    EditText sm_total, sm_total_new, sm_itemDisc, sm_subTotal, sm_tax, sm_netTotal, sm_subTotal_inclusive;
    TextWatcher cqtyTW, lqtyTW, qtyTW;
    String beforeLooseQty, beforeFoc;
    String ss_Cqty = "", calCarton = "";
    Activity mActivity;
    String conCode, conName, conProdCode, sl_price, qty, sl_carton, sl_loose, sl_qtys, sale_sl_no, Id, btCartonPerQty;
    double qty_nts, value;
    String[] Cmd = {"Delete"};
    Cursor cursor, sl_cursor,cursors;
    AlertDialog conDialog;
    ArrayList<String> products_arr = new ArrayList<String>();
    TextView serial_id;
    LinearLayout cons_text, consexpiry_text;
    String newDate = "", priceflag;
    int sl_no = 1;
    CustomCalendar customCalendar;
    EditText sl_StockinHand, sl_Total, sl_Tax, sl_Nettotal;
    Button sl_Addbtn, sl_Minusbtn;
    boolean isStockRequestBatchDetail = false;
    LinearLayout spinnerLayout;
    ProgressBar progressBar;
    ArrayList<HashMap<String, String>> stockConsignmentArr;
    public Handler mHandler;
    String strQty, qty_val,qty_value;
    private SOTDatabase sotdb;
    double tota = 0, smTax = 0, sbTtl = 0, netTotal;
    boolean flag;
    String batchSLNo="";


    public void initiateBatchPopupWindow(ConsignmentAddProduct context, String id, String sno, String code,
                                         String name, String btCalPerQty, Cursor cursor, String price,
                                         HashMap<String, EditText> hm,String cust_code,HashMap<String, EditText> hms) {



        this.mActivity = context;
        this.Id = id;
        this.sl_cursor = cursor;
        this.cursor =cursor;
        this.mCalendar = Calendar.getInstance();
        this.conProdCode = code;
        this.conName = name;
        this.btCartonPerQty = btCalPerQty;
        this.sl_price = price;
        this.sale_sl_no = sno;
        this.conCode=cust_code;
        Log.d("LetsBegin", "-->" + conProdCode +"name :"+name+"cursor Count :"+sl_cursor.getCount()+"Qty:"+ hms.get("Qty").getText().toString());
        this.sm_total = hm.get("sm_total");
        this.sm_total_new = hm.get("sm_total_new");
        this.sm_itemDisc = hm.get("sm_itemDisc");
        this.sm_subTotal = hm.get("sm_subTotal");
        this.sm_tax = hm.get("sm_tax");
        this.sm_netTotal = hm.get("sm_netTotal");
        this.sm_subTotal_inclusive = hm.get("sm_subTotal_inclusive");
        Log.d("add sl_price", ""+sl_price);
        Log.d("ID", "" + Id);

        this.sl_foc = hms.get("Foc");
        this.sl_productcode = hms.get("Productcode");
        this.sl_name = hms.get("Productname");
        this.sl_cartonqty = hms.get("Cartonqty");
        this.sl_looseqty = hms.get("Looseqty");
        this.sl_qty = hms.get("Qty");
        this.sl_uom = hms.get("Uom");
        this.sl_stock = hms.get("Stock");
        this.sl_pcspercarton = hms.get("Cartonperqty");
        this.slPrice = hms.get("Price");
        this.slCprice = hms.get("CPrice");
        this.conName= hms.get("Productname").getText().toString();



        stockDetail();


    }

    public void initiateBatchPopupWindow(Activity context, String id, String sno, String code,
                                         String name, Cursor cursor, String price, HashMap<String, EditText> hm,
                                         String cQty, String lQty, String qty, String btCalPerQty, boolean flag, HashMap<String, EditText> hms) {

        Log.d("id&sno", "-->" + sno + " " + code + " " + SalesOrderSetGet.getCustomercode() + " " + price + " " +
                cQty + " " + lQty + "Qty--> " + qty + btCalPerQty + " " + id);
        this.mActivity = context;
        this.mCalendar = Calendar.getInstance();
        this.conCode = name;
        this.conProdCode = code;
        this.sl_cursor = cursor;
        this.cursor = cursor;
        this.sl_price = price;
        this.Id = id;
        this.sale_sl_no = sno;
        this.qty_val = qty;
        this.btCartonPerQty = btCalPerQty;
        Log.d("ConsignmentAdapter", "-->" + conCode + " " + conProdCode + " -->" + flag);
//        this.cursor = SOTDatabase.getConsignmentStock(conCode, conProdCode);

        this.sl_carton = cQty;
        this.sl_loose = lQty;
        this.sl_qtys = qty;

        Cursor salcursor = SOTDatabase.getCursor();
        int cnt = salcursor.getCount();
        cnt = cnt + 1;
//        Log.d("saleCount", "" + cnt + " " + this.cursor.getCount());
        this.sale_sl_no = cnt + "";

        sm_total = hm.get("sm_total");
        sm_total_new = hm.get("sm_total_new");
        sm_itemDisc = hm.get("sm_itemDisc");
        sm_subTotal = hm.get("sm_subTotal");
        sm_tax = hm.get("sm_tax");
        sm_netTotal = hm.get("sm_netTotal");
        sm_subTotal_inclusive = hm.get("sm_subTotal_inclusive");


        this.sl_foc = hms.get("Foc");
        this.sl_productcode = hms.get("Productcode");
        this.sl_name = hms.get("Productname");
        this.sl_cartonqty = hms.get("Cartonqty");
        this.sl_looseqty = hms.get("Looseqty");
        this.sl_qty = hms.get("Qty");
        this.sl_uom = hms.get("Uom");
        this.sl_stock = hms.get("Stock");
        this.sl_pcspercarton = hms.get("Cartonperqty");
        this.slPrice = hms.get("Price");
        this.slCprice = hms.get("CPrice");
        this.conName= hms.get("Productname").getText().toString();


        Log.d("addsl_price", "" + hm.size() + " " + sm_netTotal.getText().toString());
        Log.d("ID", "" + Id);

        this.flag = flag;


        stockDetail();
    }

    public void initiateBatchPopupWindow(Activity context, String code, String name, String slCartonPerQty, String price, HashMap<String, EditText> hm, ArrayList<HashMap<String, String>> stockConsignmentArr, String cust_code, boolean flag) {
        this.mActivity = context;

        this.mCalendar = Calendar.getInstance();
        this.conProdCode = code;
        this.conName = name;
        this.sl_price = price;
        this.conCode = cust_code;
        this.btCartonPerQty = slCartonPerQty;

        this.sl_foc = hm.get("Foc");
        this.sl_productcode = hm.get("Productcode");
        this.sl_name = hm.get("Productname");
        this.sl_cartonqty = hm.get("Cartonqty");
        this.sl_looseqty = hm.get("Looseqty");
        this.sl_qty = hm.get("Qty");
        this.sl_uom = hm.get("Uom");
        this.sl_stock = hm.get("Stock");
        this.sl_pcspercarton = hm.get("Cartonperqty");
        this.slPrice = hm.get("Price");
        this.slCprice = hm.get("CPrice");
        this.stockConsignmentArr = stockConsignmentArr;
        this.flag = flag;
        this.qty_val = qty;
        this.sl_cursor=SOTDatabase.getCursor();

        Log.d("sum_sl_price", "--->" + sl_price + " -->" + this.flag);


        Cursor salcursor = SOTDatabase.getCursor();
        int cnt = salcursor.getCount();
        cnt = cnt + 1;
        Log.d("sale count", "" + cnt);
        this.sale_sl_no = cnt + "";


        //String batNo = alBatchStock.get(0).get("BatchNo");
        String dbPcode = SOTDatabase.getConsignmentStockDetail(conProdCode);

//		String dbPcode = SOTDatabase.getBatchNo(batCode);
        Log.d("alBatchStocks", "-->" + stockConsignmentArr.size() + " " + conProdCode + " " + sale_sl_no);
        Log.d("alBatchStock", "" + stockConsignmentArr.size() + " " + dbPcode);

        if (dbPcode.matches("")) {

            if (stockConsignmentArr.size() > 0) {

                for (int i = 0; i < stockConsignmentArr.size(); i++) {
                    int q = 0, r = 0;
                    String cqty = "", lqty = "", qty_val = "";

                    String qty = stockConsignmentArr.get(i).get("qty");
                    String cartonPerQty = stockConsignmentArr.get(i).get("pcsPerCarton");
                    double dQty = Double.parseDouble(qty);
                    double dPcs = Double.parseDouble(cartonPerQty);
                    Log.d("qty", "" + qty);
                    Log.d("cartonPerQty", "" + cartonPerQty);
                    if (!cartonPerQty.matches("")) {
                        if (!qty.matches("")) {
                            try {
                                int qty_nt = (int) dQty;
                                int pcs_nt = (int) dPcs;

                                Log.d("qty_nt", "" + qty_nt);
                                Log.d("pcs_nt", "" + pcs_nt);

                                q = qty_nt / pcs_nt;
                                r = qty_nt % pcs_nt;

                                Log.d("cqty", "" + q);
                                Log.d("lqty", "" + r);

                                cqty = "" + q;
                                lqty = "" + r;
                                qty_val = "" + qty_nt;
                                // bat_cartonQty.setText("" + q);
                                // bat_looseQty.setText("" + r);

                            } catch (ArithmeticException e) {
                                System.out.println("Err: Divided by Zero");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    int batchSlNo = i + 1;
                    Log.d("Details", "-->" + conName + " " + conProdCode + " " + conCode);

                    HashMap<String, String> currencyhm = new HashMap<String, String>();

                    Log.d("SlNoCheck","-->"+stockConsignmentArr.get(i).get("SlNo"));

                    currencyhm.put("SlNo", stockConsignmentArr.get(i).get("SlNo"));
                    currencyhm.put("productCode", conProdCode);
                    currencyhm.put("productName", conName);
                    currencyhm.put("pcsPerCarton", btCartonPerQty);
                    currencyhm.put("cQty", "0");
                    currencyhm.put("lqty", "0");
                    currencyhm.put("qty", "0");
                    currencyhm.put("con_no", stockConsignmentArr.get(i).get("con_no"));
                    currencyhm.put("con_name", stockConsignmentArr.get(i).get("con_name"));
                    currencyhm.put("duration", stockConsignmentArr.get(i).get("duration"));
                    currencyhm.put("con_expiry", stockConsignmentArr.get(i).get("con_expiry"));
                    currencyhm.put("cust_code", conCode);
                    currencyhm.put("exp_date", stockConsignmentArr.get(i).get("exp_date"));
                    currencyhm.put("mfgDate", stockConsignmentArr.get(i).get("mfgDate"));
                    currencyhm.put("uom", stockConsignmentArr.get(i).get("uom"));
                    currencyhm.put("AvailCQty", cqty);
                    currencyhm.put("AvailLQty", lqty);
                    currencyhm.put("AvailQty", qty_val);
                    currencyhm.put("BatchNo", stockConsignmentArr.get(i).get("BatchNo"));
                    currencyhm.put("SR_Slno",sale_sl_no);
                    Log.d("customerCodes123", "-->" + stockConsignmentArr.size());
                    SOTDatabase.storeConsignmentStock(currencyhm);
                    cursor=SOTDatabase.getConsignmentStocks(conProdCode);

                    Log.d("ADDProduct", "->"+"cursorValues: "+cursor.getCount()+"currencyhm :" + currencyhm);
                }

            }
        }

        stockDetail();


    }


    private void stockDetail() {
        myDialog = new AlertDialog.Builder(mActivity);

        LayoutInflater li = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.activity_consignment_stock, null, false);
        myDialog.setView(v);
        myDialog.setCancelable(false);
        sotdb = new SOTDatabase(mActivity);


        cons_codefield = (EditText) v.findViewById(R.id.batch_codefield);
        cons_namefield = (EditText) v.findViewById(R.id.batch_namefield);
        consno = (EditText) v.findViewById(R.id.batchno);
        expirydate = (EditText) v.findViewById(R.id.expirydate);
        cons_cartonQty = (EditText) v.findViewById(R.id.batch_cartonQty);
        cons_looseQty = (EditText) v.findViewById(R.id.batch_looseQty);
        cons_qty = (EditText) v.findViewById(R.id.batch_qty);
        batch_list = (ListView) v.findViewById(R.id.batch_list);
        edtotalqty = (EditText) v.findViewById(R.id.edtotalqty);
        cons_add = (ImageButton) v.findViewById(R.id.batch_add);
        cons_cancel = (ImageButton) v.findViewById(R.id.batch_cancel);
        cons_finish = (ImageButton) v.findViewById(R.id.batch_finish);
        serial_id = (TextView) v.findViewById(R.id.serial_id);

        consno.setBackgroundResource(R.drawable.labelbg);
        consno.setFocusable(false);

        Log.d("conName","-->"+conName+"Concode :"+conCode);

//        serial_id.setText("");
        cons_codefield.setText(conProdCode);
        cons_namefield.setText(conName);
        edtotalqty.setText(qty);

//        cursors =SOTDatabase.getConsignmentStock();
        cursor =SOTDatabase.getConsignmentStocks(conProdCode);
//        cursor=SOTDatabase.getConsignmentStock(conProdCode,sale_sl_no);
        Log.d("ConsignmentClass","-->"+cursor.getCount() );

        consignmentListAdapter = new ConsignmentListAdapter(mActivity, cursor);
        batch_list.setAdapter(consignmentListAdapter);
//        registerForContextMenu(batch_list);

//        if (mActivity instanceof InvoiceAddProduct) {
//            for (int i = 0; i < stockConsignmentArr.size(); i++) {
//                String quantity = stockConsignmentArr.get(i).get("qty");
//                Log.d("quan_value", "-->" + quantity);
//                qty_nts = Double.parseDouble(quantity);
//                value = value + qty_nts;
//
//
//            }
//            String quan_value = String.valueOf(value);
//            Log.d("quan_value", "-->" + quan_value);
//            headerCustCode(quan_value,flag);
//        } else {
//            Log.d("quantityValue", "-->" + qty_val+"-->"+flag);
//            headerCustCode(qty_val,flag);
//        }

//        new AsyncStockTask(conProdCode, conCode,stockConsignmentArr).execute();

        batch_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int pos,
                                    long arg3) {
                batchSLNo = ((TextView) v.findViewById(R.id.batch_sl_no))
                        .getText().toString();
                serial_id.setText(((TextView) v.findViewById(R.id.batch_sl_id))
                        .getText().toString());
                consno.setText(((TextView) v.findViewById(R.id.batch_no))
                        .getText().toString());
                String cons_no = ((TextView) v.findViewById(R.id.batch_no))
                        .getText().toString();
                Log.d("consno", "-->" + ((TextView) v.findViewById(R.id.batch_sl_no))
                        .getText().toString()+"batchSLNo :" +batchSLNo);
                SalesOrderSetGet.setCon_no(cons_no);


                expirydate.setText(((TextView) v
                        .findViewById(R.id.batch_expiryDate)).getText()
                        .toString());
                cons_qty.setText(((TextView) v
                        .findViewById(R.id.avl_Qty)).getText().toString());
                String qty = ((TextView) v
                        .findViewById(R.id.avl_Qty)).getText().toString();
                Log.d("cons_qty", "-->" + qty);
                cons_cartonQty.setText(((TextView) v
                        .findViewById(R.id.batch_cQty)).getText().toString());
                cons_looseQty.setText(((TextView) v
                        .findViewById(R.id.batch_lQty)).getText().toString());


//                bat_foc.setText(((TextView) v
//                        .findViewById(R.id.batch_fQty)).getText().toString());

                String strQty = ((TextView) v.findViewById(R.id.batch_Qty))
                        .getText().toString();
                String strcQty = ((TextView) v.findViewById(R.id.batch_cQty))
                        .getText().toString();
                String strlQty = ((TextView) v.findViewById(R.id.batch_lQty))
                        .getText().toString();

                if (strQty.matches("0")) {
                    cons_qty.setText("");
                    cons_qty.requestFocus();

                    ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                            .showSoftInput(cons_qty, 2);
                    cons_qty.setSelection(cons_qty.length());

                } else {
                    cons_qty.setText(((TextView) v.findViewById(R.id.batch_Qty))
                            .getText().toString());
                    cons_qty.setSelection(cons_qty.length());
                }

                if (strcQty.matches("0")) {
                    cons_cartonQty.setText("");
                }

                if (strlQty.matches("0")) {
                    cons_looseQty.setText("");
                }

                if (btCartonPerQty.matches("1") || btCartonPerQty.matches("0")
                        || btCartonPerQty.matches("")) {
                    cons_qty.requestFocus();
                    cons_qty.setSelection(cons_qty.length());
                } else {
                    cons_cartonQty.requestFocus();
                    cons_cartonQty.setSelection(cons_cartonQty.length());
                }

                if (calCarton.matches("0")) {
                    cons_cartonQty.requestFocus();
                    cons_cartonQty.setSelection(cons_cartonQty.length());
                }

                cons_add.setVisibility(View.VISIBLE);

            }
        });

        cons_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String batchNo = "", expiryDate = "", batchQty = "";

                batchNo = consno.getText().toString();
                expiryDate = expirydate.getText().toString();
                batchQty = cons_qty.getText().toString();
                String batchCarton = cons_cartonQty.getText().toString();

                if (batchNo != null && !batchNo.isEmpty()) {
                    if (expiryDate != null && !expiryDate.isEmpty()) {
                        if (batchQty != null && !batchQty.isEmpty()) {

                            storeBatch();

                        } else {

                            if (calCarton.matches("0")) {

                                if (batchCarton.matches("")) {
                                    Toast.makeText(mActivity, "Enter the carton/quantity",
                                            Toast.LENGTH_SHORT).show();
                                    cons_cartonQty.requestFocus();
                                } else {
                                    storeBatch();
                                }
                            } else {
                                Toast.makeText(mActivity, "Enter Qty",
                                        Toast.LENGTH_SHORT).show();
                                cons_qty.requestFocus();

                                ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                                        .showSoftInput(cons_qty, 2);
                                cons_qty.setSelection(cons_qty.length());
                            }

                        }
                    } else {

                    }
                } else {

                    if (expiryDate != null && !expiryDate.isEmpty()) {
                        if (batchQty != null && !batchQty.isEmpty()) {

                            storeBatch();

                        } else {
//							bat_qty.requestFocus();
//
//							((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
//			                .showSoftInput(bat_qty, 2);
//							bat_qty.setSelection(bat_qty.length());
//



//							Toast.makeText(mActivity, "Enter Qty",
//									Toast.LENGTH_SHORT).show();

                            if (calCarton.matches("0")) {

                                if (batchCarton.matches("")) {
                                    Toast.makeText(mActivity, "Enter the carton/quantity",
                                            Toast.LENGTH_SHORT).show();
                                    cons_cartonQty.requestFocus();
                                } else {
                                    storeBatch();
                                }
                            } else {
                                Toast.makeText(mActivity, "Enter Qty",
                                        Toast.LENGTH_SHORT).show();
                                cons_qty.requestFocus();

                                ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                                        .showSoftInput(cons_qty, 2);
                                cons_qty.setSelection(cons_qty.length());
                            }
                        }

                    } else {

                    }

                }

                cursor.requery();
//                sl_cursor.requery();

            }
        });


        expirydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                customCalendar = new CustomCalendar(mActivity);
                customCalendar.showCalendarView();
                boolean mstatus = customCalendar.showDialog();
                Log.d("mstatus", mstatus + "");
                if (mstatus == true) {
                    String sDate = customCalendar.getSelectDate();
                    expirydate.setText(sDate);
                } else {
                    Log.d("False", "-->" + "False");
                }
            }
        });

        cons_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String prodcode = cons_codefield.getText().toString();
                Log.d("conscode", "-->" + sale_sl_no + " " + prodcode);
                sl_cursor.requery();


//                int totQty = SOTDatabase.getTotalConsignmentStockQty(conProdCode, sale_sl_no);
//                int totCqty = SOTDatabase.getTotalConsignmentStockCQty(conProdCode, sale_sl_no);
//                int totLqty = SOTDatabase.getTotalConsignmentLQty(conProdCode, sale_sl_no);

                int totQty = SOTDatabase.getTotalConsignmentQty(conProdCode);
                int totCqty = SOTDatabase.getTotalConsignmentCQty(conProdCode);
                int totLqty = SOTDatabase.getTotalConsignmentLQty(conProdCode);
//                int totFocQty = SOTDatabase.getTotalConsignmentFocQty(conProdCode, sale_sl_no+"");

                Log.d("totQtys", "-->" + totQty + " " + totCqty + " " + totLqty );

                if(mActivity instanceof ConsignmentAddProduct){


                    sl_cartonqty.setText("" + totCqty);
                    sl_looseqty.setText("" + totLqty);

                    sl_qty.setText("" + totQty);
//                    sl_foc.setText(""+totFocQty);

                    sl_cartonqty.setFocusable(false);
                    sl_cartonqty.setBackgroundResource(R.drawable.labelbg);

                    sl_looseqty.setFocusable(false);
                    sl_looseqty.setBackgroundResource(R.drawable.labelbg);

                    sl_foc.setFocusable(false);
                    sl_foc.setBackgroundResource(R.drawable.labelbg);

                    sl_qty.setFocusable(false);
                    sl_qty.setBackgroundResource(R.drawable.labelbg);

                    slPrice.requestFocus();

                    ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                            .showSoftInput(slPrice, 2);

                  }  else if(mActivity instanceof ConsignmentSummary){
                    clQty(Id, "" + totCqty, "" + totLqty, "" + totQty, btCartonPerQty, prodcode,batchSLNo);

                    cursor = SOTDatabase.getCursor();
                    cursor.requery();
                    String  COLUMN_QUANTITY = "";
                    Log.d("SOTDatabase.getCursor","-->"+cursor.getCount());

                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        do {
                            COLUMN_QUANTITY = cursor.getString(cursor
                                    .getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
                        } while (cursor.moveToNext());
                    }
                    Log.d("COLUMNQUANTI","-->"+COLUMN_QUANTITY);

                    if (cursor != null && cursor.getCount() > 0) {

                        cursor.requery();
                        tota = SOTDatabase.getTotal();
                        Log.d("Tota", "" + tota);
                        String smtotal = twoDecimalPoint(tota);

                        smTax = SOTDatabase.getTax();
                        String ProdTax = fourDecimalPoint(smTax);
                        Log.d("ProdTax", "-->" + ProdTax);
                        sm_tax.setText("" + ProdTax);

                        sbTtl = SOTDatabase.getSubTotal();
                        String sub = twoDecimalPoint(sbTtl);
                        sm_subTotal.setText("" + sub);

                        double tot_item_disc = SOTDatabase.getTotalItemDisc();
                        String tot_itemDisc = twoDecimalPoint(tot_item_disc);

                        sm_itemDisc.setText(tot_itemDisc);

                        sm_total.setText("" + sub);

                        sm_total_new.setText("" + smtotal);

                        String taxType = SalesOrderSetGet.getCompanytax();
                        if (taxType != null && !taxType.isEmpty()) {
                            if (taxType.matches("I") || taxType.matches("Z")) {
                                netTotal = sbTtl;
                            } else {
                                netTotal = sbTtl + smTax;
                            }
                        } else {
                            netTotal = sbTtl + smTax;
                        }

                        String ProdNettotal = twoDecimalPoint(netTotal);
                        sm_netTotal.setText("" + ProdNettotal);

                        // Added New 12.04.2017
                        if (taxType != null && !taxType.isEmpty()) {
                            if (taxType.matches("I")) {
                                sm_subTotal.setVisibility(View.GONE);
                                sm_subTotal_inclusive.setVisibility(View.VISIBLE);
                                double subt = netTotal - smTax;
                                String subto = twoDecimalPoint(subt);
                                sm_subTotal_inclusive.setText("" + subto);
                            } else {
                                sm_subTotal.setVisibility(View.VISIBLE);
                                sm_subTotal_inclusive.setVisibility(View.GONE);
                            }
                        }
                        sl_cursor.requery();
                    }
                }
                if (totCqty > 0 || totQty > 0) {
                    ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(cons_qty.getWindowToken(), 0);
                    conDialog.dismiss();
                } else {
                    Toast.makeText(mActivity, "Please add something", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cons_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // sot.deletebatchbyprodcode(batch_codefield.getText().toString());
                Log.d("serial_id", "-->" + serial_id.getText().toString());
//                serial_id.setText("");
                if (mActivity instanceof ConsignmentAddProduct) {
                    consignmentListAdapter.notifyDataSetChanged();
                }else {
                    sl_cursor.requery();
                    cursor.requery();
//
                }

                ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(cons_qty.getWindowToken(), 0);
                Toast.makeText(mActivity, "Cancelled", Toast.LENGTH_SHORT).show();
                conDialog.dismiss();

            }
        });

        cons_cartonQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    cartonQtyBatch();

                    cons_looseQty.requestFocus();
                    return true;
                }
                return false;
            }
        });


        cons_looseQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    looseQtyCalcBatch();
                    cons_qty.requestFocus();
                    return true;
                }
                return false;
            }
        });


        cons_qty.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    String qty = cons_qty.getText().toString();
                    if (!qty.matches("")) {
                        double qtyCalc = Double.parseDouble(qty);
                        clQtyBatch();
                    }
//                    bat_foc.requestFocus();
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
                ss_Cqty = cons_cartonQty.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (calCarton.matches("0")) {

                } else {
                    cartonQtyBatch();
                }
                int length = cons_cartonQty.length();
                if (length == 0) {
                    if (calCarton.matches("0")) {

                    } else {

                        String lqty = cons_looseQty.getText().toString();

                        if (lqty.matches("")) {
                            lqty = "0";
                        }

                        if (!lqty.matches("")) {
                            cons_qty.removeTextChangedListener(qtyTW);
                            cons_qty.setText(lqty);
                            cons_qty.addTextChangedListener(qtyTW);

                            if (cons_qty.length() != 0) {
                                cons_qty.setSelection(cons_qty.length());
                            }
                            double lsQty = Double.parseDouble(lqty);
                        }
                        // productTotal(lsQty);
                    }

                }
            }

        };


        lqtyTW = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                beforeLooseQty = cons_looseQty.getText().toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (calCarton.matches("0")) {

                } else {
                    looseQtyCalcBatch();
                }

                int length = cons_looseQty.length();
                if (length == 0) {
                    if (calCarton.matches("0")) {

                    } else {
                        String qty = cons_qty.getText().toString();
                        if (!beforeLooseQty.matches("") && !qty.matches("")) {

                            int qtyCnvrt = Integer.parseInt(qty);
                            int lsCnvrt = Integer.parseInt(beforeLooseQty);

                            cons_qty.removeTextChangedListener(qtyTW);
                            cons_qty.setText("" + (qtyCnvrt - lsCnvrt));
                            cons_qty.addTextChangedListener(qtyTW);

                            if (cons_qty.length() != 0) {
                                cons_qty.setSelection(cons_qty.length());
                            }
                            if (calCarton.matches("0")) {

                            } else {
                                looseQtyCalcBatch();
                            }
                        }
                    }
                }
            }

        };

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

                String qty = cons_qty.getText().toString();
                if (!qty.matches("")) {
                    double qtyCalc = Double.parseDouble(qty);
                    Log.d("qty", "qq" + qtyCalc);
                    if (calCarton.matches("0")) {

                    } else {
                        clQtyBatch();
                    }
                    // productTotal(qtyCalc);
                }

                int length = cons_qty.length();
                if (length == 0) {
                    // productTotal(0);
                    if (calCarton.matches("0")) {

                    } else {

                        cons_cartonQty.removeTextChangedListener(cqtyTW);
                        cons_cartonQty.setText("");
                        cons_cartonQty.addTextChangedListener(cqtyTW);

                        cons_looseQty.removeTextChangedListener(lqtyTW);
                        cons_looseQty.setText("");
                        cons_looseQty.addTextChangedListener(lqtyTW);
                    }
                }
            }

        };

        cons_cartonQty.addTextChangedListener(cqtyTW);
        cons_looseQty.addTextChangedListener(lqtyTW);
        cons_qty.addTextChangedListener(qtyTW);

        // myDialog.show();
        conDialog = myDialog.show();

//        mActivity.runOnUiThread(new Runnable() {
//            public void run() {
//                conDialog = myDialog.create();
//                conDialog.show();
//            }
//        });
    }

    private void clQty(String id, String cQty, String lQty, String quantity, String pcspercarton, String prodcode,String batchSLNO) {

        Log.d("ConsignmentId", "-->" + batchSLNO);

        int cartonQty = 0;
        double looseQty = 0;
        products_arr.clear();
        String cprice, productprice, taxtype, taxvalue, itemdiscount, pieceperqty;

        products_arr = sotdb.getProducts(batchSLNO);

        // quantity = products_arr.get(0);
        Log.d("products_arr", "" + products_arr.size());

        cprice = products_arr.get(1);
        productprice = products_arr.get(2);

//        productprice =quantity;
        taxtype = products_arr.get(3);
        taxvalue = products_arr.get(4);
        pieceperqty = products_arr.get(5);
        itemdiscount = products_arr.get(6);

        Log.d("cprice", "" + cprice);
        Log.d("productprice", "" + productprice);
        Log.d("taxtype", "" + taxtype);
        Log.d("taxvalue", "" + taxvalue);
        Log.d("itemdiscount", "" + itemdiscount);

        try {
            double qty_nt = Double.parseDouble(quantity);
            int pcs_nt = Integer.parseInt(pieceperqty);

            Log.d("qty_nt", "" + qty_nt);
            Log.d("pcs_nt", "" + pcs_nt);

            cartonQty = (int) (qty_nt / pcs_nt);
            looseQty = qty_nt % pcs_nt;

            Log.d("cqty", "" + cartonQty);
            Log.d("lqty", "" + looseQty);

        } catch (ArithmeticException e) {
            System.out.println("Err: Divided by Zero");
        } catch (Exception e) {
            e.printStackTrace();
        }

        double taxAmount = 0.0, netTotal = 0.0;
        double taxAmount1 = 0.0, netTotal1 = 0.0;
        double itmDisc = 0, tt = 0, qty = Double.valueOf(quantity), cqtyCalc = Double.valueOf(cQty), lqtyCalc = Double.valueOf(lQty);
        String sbTtl = "", Prodtotal = "", prodTax = "0", ProdNetTotal = "";
        double subTotal = 0.0;
        try {
            if (!productprice.matches("")) {

                double slPriceCalc = Double.parseDouble(productprice);
                double slCpriceCalc = Double.parseDouble(cprice);

                priceflag = SalesOrderSetGet.getCartonpriceflag();
                Log.d("priceflag", "-->" + priceflag);

                if (priceflag.matches("null") || priceflag.matches("")) {
                    priceflag = "0";
                }

                if (priceflag.matches("0")) {
                    tt = qty * slPriceCalc;

                } else if (priceflag.matches("1")) {
                    tt = (cqtyCalc * slCpriceCalc) + (lqtyCalc * slPriceCalc);
                }

                Prodtotal = twoDecimalPoint(tt);

                if (!itemdiscount.matches("")) {
                    itmDisc = Double.parseDouble(itemdiscount);
                    subTotal = tt - itmDisc;
                } else {
                    subTotal = tt;
                }

                sbTtl = twoDecimalPoint(subTotal);

                if (!taxtype.matches("") && !taxvalue.matches("")) {

                    double taxValueCalc = Double.parseDouble(taxvalue);

                    if (taxtype.matches("E")) {

                        if (!itemdiscount.matches("")) {
                            taxAmount1 = (subTotal * taxValueCalc) / 100;
                            prodTax = fourDecimalPoint(taxAmount1);

                            netTotal1 = subTotal + taxAmount1;
                            ProdNetTotal = twoDecimalPoint(netTotal1);

                        } else {

                            taxAmount = (tt * taxValueCalc) / 100;
                            prodTax = fourDecimalPoint(taxAmount);


                            netTotal = tt + taxAmount;
                            ProdNetTotal = twoDecimalPoint(netTotal);

                        }


                    } else if (taxtype.matches("I")) {
                        if (!itemdiscount.matches("")) {
                            taxAmount1 = (subTotal * taxValueCalc)
                                    / (100 + taxValueCalc);
                            prodTax = fourDecimalPoint(taxAmount1);

//							netTotal1 = subTotal + taxAmount1;
                            netTotal1 = subTotal;
                            ProdNetTotal = twoDecimalPoint(netTotal1);

                        } else {
                            taxAmount = (tt * taxValueCalc)
                                    / (100 + taxValueCalc);
                            prodTax = fourDecimalPoint(taxAmount);

//							netTotal = tt + taxAmount;
                            netTotal = tt;
                            ProdNetTotal = twoDecimalPoint(netTotal);

                        }

                    } else if (taxtype.matches("Z")) {

                        if (!itemdiscount.matches("")) {
//							netTotal1 = subTotal + taxAmount;
                            netTotal1 = subTotal;
                            ProdNetTotal = twoDecimalPoint(netTotal1);

                        } else {
//							netTotal = tt + taxAmount;
                            netTotal = tt;
                            ProdNetTotal = twoDecimalPoint(netTotal);

                        }

                    } else {

                        taxvalue = "0";
                        netTotal = subTotal;
                        ProdNetTotal = twoDecimalPoint(netTotal);

                    }

                } else if (taxvalue.matches("")) {
                    taxvalue = "0";
                    netTotal = subTotal;
                    ProdNetTotal = twoDecimalPoint(netTotal);
                } else {
                    taxvalue = "0";
                    netTotal = subTotal;
                    ProdNetTotal = twoDecimalPoint(netTotal);
                }

            }
        } catch (Exception e) {

        }

        int totCqty, totLqty;

//        totCqty = SOTDatabase.getTotalConsignmentStockCQty(prodcode, sale_sl_no);
//        totLqty = SOTDatabase.getTotalConsignmentLQty(prodcode, sale_sl_no);

        totCqty =SOTDatabase.getTotalConsignmentCQty(prodcode);
        totLqty =SOTDatabase.getTotalBatchLQty(prodcode);

        Log.d("totCqty","-->"+totCqty+" totLqty :"+totLqty);


//        if (calCarton.matches("0")) {
            cartonQty = totCqty;
            looseQty = totLqty;
//        }

        Log.d("ConsignmentResult", "cartonQty " + cartonQty + "looseQty " + looseQty + "productprice " + Prodtotal
                + "sbTtl " + sbTtl + "prodTax " + prodTax + "ProdNetTotal "
                + ProdNetTotal+"quantity :"+quantity+" batchSLNO:"+batchSLNO);

        sotdb.updateProductForConsignment(batchSLNO, cartonQty, looseQty,
                productprice, itemdiscount, Prodtotal, sbTtl, prodTax,
                ProdNetTotal, quantity, "" );

        sotdb.updateBilldiscProductValues(batchSLNO, sbTtl);

//		sm_cartonqty.setText(twoDecimalPoint(SOTDatabase.getTotalCqty()));
//		sm_looseqty.setText(twoDecimalPoint(SOTDatabase.getTotalLqty()));
//		sm_qty.setText(twoDecimalPoint(SOTDatabase.getTotalQty()));

            sl_cursor.requery();
            cursor.requery();

    }


    private void storeInDatabase() {


        double dcartonQty = 0.0, dlooseQty = 0.0, dQty = 0.0, dprice = 0.0, dpcspercarton = 0.0;
        int cartonQty_int, looseQty_int, qty_int, pcspercarton_int;
        //delete
//		  cursor = SOTDatabase.getCursor();
//
//		  if (cursor != null && cursor.getCount() > 0) {
//		   sl_no = cursor.getCount();
//		   sl_no++;
//		  }
        String sale_sl_no = cons_codefield.getText().toString();
        sl_no = Integer.parseInt(sale_sl_no);

        String productcode = sl_productcode.getText().toString();
        String productname = sl_name.getText().toString();
        String uom = sl_uom.getText().toString();
        String pcspercarton = sl_pcspercarton.getText().toString();

        String sl_cartonQty = sl_cartonqty.getText().toString();
        String sl_looseQty = sl_looseqty.getText().toString();
        String sl_Qty = sl_qty.getText().toString();

        if (sl_cartonQty != null && !sl_cartonQty.isEmpty()) {
            dcartonQty = Double.parseDouble(sl_cartonQty);
        }
        if (sl_looseQty != null && !sl_looseQty.isEmpty()) {
            dlooseQty = Double.parseDouble(sl_looseQty);
        }
        if (sl_Qty != null && !sl_Qty.isEmpty()) {
            dQty = Double.parseDouble(sl_Qty);
        }
        if (pcspercarton != null && !pcspercarton.isEmpty()) {
            dpcspercarton = Double.parseDouble(pcspercarton);
        }
        if (sl_price != null && !sl_price.isEmpty()) {
            dprice = Double.parseDouble(sl_price);
        }

        cartonQty_int = (int) dcartonQty;
        looseQty_int = (int) dlooseQty;
        qty_int = (int) dQty;
        pcspercarton_int = (int) dpcspercarton;

        Log.d("sl_cartonQty", "" + sl_cartonQty);
        Log.d("sl_looseQty", "" + sl_looseQty);
        Log.d("sl_Qty", "" + sl_Qty);
        Log.d("Price", "" + dprice);

        String prodCode = SOTDatabase.getProductCodeSR(productcode, sale_sl_no);

        if (prodCode != null && !prodCode.isEmpty()) {
            if (prodCode.matches(productcode)) {
                SOTDatabase.updateProductForConsignmentStock(productcode,
                        cartonQty_int, looseQty_int, qty_int, sale_sl_no);
            } else {

                SOTDatabase.storeProduct(sl_no, productcode, productname,
                        cartonQty_int, looseQty_int, qty_int, 0, dprice, 0.0, uom,
                        pcspercarton_int, 0.0, 0.0, "", "", "", "", "", "0.00", "0",
                        "0", "", "", "", "", "", "");
            }
        } else {
            SOTDatabase.storeProduct(sl_no, productcode, productname,
                    cartonQty_int, looseQty_int, qty_int, 0, dprice, 0.0, uom,
                    pcspercarton_int, 0.0, 0.0, "", "", "", "", "", "0.00", "0",
                    "0", "", "", "", "", "", "");
        }

//            SOTDatabase.updateproductbatch(productcode,havebatch,haveexpiry);

//            sl_productcode.setText("");
//            sl_name.setText("");
//            sl_cartonqty.setText("");
//            sl_looseqty.setText("");
//            sl_qty.setText("");
//            sl_uom.setText("");
//            sl_uom.setText("");
//            sl_stock.setText("");
//            sl_pcspercarton.setText("");

        sl_productcode.requestFocus();

        InputMethodManager imm = (InputMethodManager) mActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(sl_productcode.getWindowToken(), 0);

        sl_cartonqty.setEnabled(true);
        sl_cartonqty.setFocusable(true);
        sl_cartonqty.setBackgroundResource(R.drawable.edittext_bg);

        sl_looseqty.setEnabled(true);
        sl_looseqty.setFocusable(true);
        sl_looseqty.setBackgroundResource(R.drawable.edittext_bg);

        sl_qty.setEnabled(true);
        sl_qty.setFocusable(true);
        sl_qty.setBackgroundResource(R.drawable.edittext_bg);

    }


    final DatePickerDialog.OnDateSetListener mDate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            strtDate();
        }
    };


    public void strtDate() {

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        expirydate.setText(sdf.format(mCalendar.getTime()));
    }

    private void clQtyBatch() {
        String qty = cons_qty.getText().toString();
        String crtnperQty = btCartonPerQty;
        int q = 0, r = 0;
        int qty_nt, pcs_nt;

        if (crtnperQty.matches("0") || crtnperQty.matches("null")
                || crtnperQty.matches("0.00")) {
            crtnperQty = "1";
        }

        if (!crtnperQty.matches("")) {
            if (!qty.matches("")) {
                try {
                    boolean check = isNumber(qty);
                    Log.d("check", "-->" + check);
                    if (check == true) {
                        double v = Double.parseDouble(qty);
                        double v1 = Double.parseDouble(crtnperQty);
                        qty_nt = (int) v;
                        pcs_nt = (int) v1;
                    } else {
                        qty_nt = Integer.parseInt(qty);
                        pcs_nt = Integer.parseInt(crtnperQty);
                    }


                    Log.d("qty_nt", "" + qty_nt);
                    Log.d("pcs_nt", "" + pcs_nt);

                    q = qty_nt / pcs_nt;
                    r = qty_nt % pcs_nt;

                    Log.d("cqty", "" + q);
                    Log.d("lqty", "" + r);

                    if (calCarton.matches("0")) {
                        q = 0;
                        r = 0;
                    }

                    cons_cartonQty.setText("" + q);
                    cons_looseQty.setText("" + r);

                    Log.d("bat_cartonQty", "qq" + q);

                } catch (ArithmeticException e) {
                    System.out.println("Err: Divided by Zero");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static boolean isNumber(String str) {
        try {
            double v = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException nfe) {
        }
        return false;
    }


    private void cartonQtyBatch() {
        int cartonQtyCalc = 0, cartonPerQtyCalc = 0, lsQtyCnvrt;
        String crtnQty = cons_cartonQty.getText().toString();
        String lsQty = cons_looseQty.getText().toString();

        if (!btCartonPerQty.matches("") && !crtnQty.matches("")) {
            boolean check = isNumber(crtnQty);
            Log.d("check", "-->" + lsQty);
            if (!lsQty.matches("")) {
                if (check == true) {
                    double v = Double.parseDouble(crtnQty);
                    double v1 = Double.parseDouble(btCartonPerQty);
                    double v2 = Double.parseDouble(lsQty);
                    cartonQtyCalc = (int) v;
                    cartonPerQtyCalc = (int) v1;
                    lsQtyCnvrt = (int) v2;

                } else {
                    cartonQtyCalc = Integer.parseInt(qty);
                    cartonPerQtyCalc = Integer.parseInt(btCartonPerQty);
                    lsQtyCnvrt = Integer.parseInt(lsQty);
                }
            } else {
                lsQtyCnvrt = 0;
            }


            int qty = 0;

            if (!lsQty.matches("")) {
                qty = (cartonQtyCalc * cartonPerQtyCalc) + lsQtyCnvrt;

            } else {
                qty = cartonQtyCalc * cartonPerQtyCalc;
            }
            cons_qty.removeTextChangedListener(qtyTW);
            cons_qty.setText("" + qty);
            cons_qty.addTextChangedListener(qtyTW);

            if (cons_qty.length() != 0) {
                cons_qty.setSelection(cons_qty.length());
            }

        }
    }

    public void looseQtyCalcBatch() {
        int cartonQtyCalc = 0, cartonPerQtyCalc = 0, looseQtyCalc = 0;
        String crtnQty = cons_cartonQty.getText().toString();
        String lsQty = cons_looseQty.getText().toString();

        if (lsQty.matches("")) {
            lsQty = "0";
        }

        if (!btCartonPerQty.matches("") && !crtnQty.matches("")
                && !lsQty.matches("")) {

            boolean check = isNumber(crtnQty);
            Log.d("check", "-->" + check);
            if (check == true) {
                double v = Double.parseDouble(crtnQty);
                double v1 = Double.parseDouble(btCartonPerQty);
                double v2 = Double.parseDouble(lsQty);
                cartonQtyCalc = (int) v;
                cartonPerQtyCalc = (int) v1;
                looseQtyCalc = (int) v2;
            } else {
                cartonQtyCalc = Integer.parseInt(qty);
                cartonPerQtyCalc = Integer.parseInt(btCartonPerQty);
                looseQtyCalc = Integer.parseInt(lsQty);
            }


            int qty;
            qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;

            cons_qty.removeTextChangedListener(qtyTW);
            cons_qty.setText("" + qty);
            cons_qty.addTextChangedListener(qtyTW);

            if (cons_qty.length() != 0) {
                cons_qty.setSelection(cons_qty.length());
            }

        }

        if (!lsQty.matches("")) {

            int qty;
            if (!crtnQty.matches("") && !btCartonPerQty.matches("")) {
//                cartonQtyCalc = Integer.parseInt(crtnQty);
//                cartonPerQtyCalc = Integer.parseInt(btCartonPerQty);
                qty = (cartonQtyCalc * cartonPerQtyCalc) + looseQtyCalc;
            } else {
                qty = looseQtyCalc;
            }
            cons_qty.removeTextChangedListener(qtyTW);
            cons_qty.setText("" + qty);
            cons_qty.addTextChangedListener(qtyTW);

            if (cons_qty.length() != 0) {
                cons_qty.setSelection(cons_qty.length());
            }

        }
    }

    private void storeBatch() {
        String avl_qty="",avl_cqty ="",batchQty="";

        String bat_id = serial_id.getText().toString();
        String batchNo = consno.getText().toString();
        String expiryDate = expirydate.getText().toString();
        String batchCqty = cons_cartonQty.getText().toString();
        String batchLqty = cons_looseQty.getText().toString();
        String conscode = cons_codefield.getText().toString();
        batchQty = cons_qty.getText().toString();


        Log.d("prodcode","-->"+conscode+batchNo);



//        if(SalesOrderSetGet.getHeader_flag().matches("ConsignmentAddProduct")){

//           avl_cqty = SOTDatabase.getConsignmentAvlcQty(bat_id, conProdCode);
//        }

//        else{
//            avl_qty =SalesOrderSetGet.getcQty();
//
//        }

        avl_qty = SOTDatabase.getConsignmentAvlQty(batchSLNo, conscode,batchNo);

        Log.d("avl_qty", "-->" + bat_id + " " +avl_qty);


        if (batchCqty != null && !batchCqty.isEmpty()) {
        } else {
            batchCqty = "0";
        }
        if (batchLqty != null && !batchLqty.isEmpty()) {
        } else {
            batchLqty = "0";
        }

        if (batchQty != null && !batchQty.isEmpty()) {
        } else {
            batchQty = "0";
        }

        double davl_Qty = Double.parseDouble(avl_qty);
        double d_qty = Double.parseDouble(batchQty);

////            if(calCarton.matches("0")){
//                davl_Qty = Double.parseDouble(avl_cqty);
//                d_qty = Double.parseDouble(batchCqty);
////            }


        Log.d("Avl_carton","-->"+davl_Qty+" -->"+batchQty);



        if (davl_Qty >= d_qty) {

            Cursor cursor_count = SOTDatabase.getConsignmentStock(bat_id, conProdCode);
            int count = cursor_count.getCount();
            count = count + 1;
            Log.d("cursorcount", "->" + batchSLNo);
            String batchSlNo = count + "";

            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("id", bat_id);
            hm.put("product",conProdCode);
            hm.put("slNo", batchSLNo);
            hm.put("BatchNo", batchNo);
            hm.put("ExpiryDate", expiryDate);
            hm.put("CQty", batchCqty);
            hm.put("LQty", batchLqty);
            hm.put("Qty", batchQty);

            Log.d("UpdateProduct", "->" + hm +"bat_id"+bat_id);
            SOTDatabase.updateConsignmentStock(hm);

            String totQty = SOTDatabase.getConsignmentStockQty(conProdCode, sale_sl_no);

//            String totQty =SOTDatabase.getConsignmentQty(conProdCode);

            Log.d("totQty", "-->" + totQty);

            //delete
			/*String totQty = "";
			if(isStockRequestBatchDetail){
				String slno = SOTDatabase.getProductSno(Id);
				totQty = SOTDatabase.getBatchQtySR(prodcode,slno);
			} else{
				totQty = SOTDatabase.getBatchQty(prodcode);
			}*/


            edtotalqty.setText(totQty);

            consno.setText("");
            expirydate.setText("");
            cons_cartonQty.setText("");
            cons_looseQty.setText("");
            cons_qty.setText("");
            serial_id.setText("");

            cons_add.setVisibility(View.GONE);
//			bat_qty.requestFocus();
//			((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
//            .showSoftInput(bat_qty, 2);

            InputMethodManager inputMethodManager = (InputMethodManager) mActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    cons_qty.getWindowToken(), 0);

            cons_qty.setSelection(cons_qty.length());

        } else {
//			bat_qty.requestFocus();


            Toast.makeText(mActivity,
                    "Quantity must be less than or equal to available quantiy",
                    Toast.LENGTH_LONG).show();


        }

    }




    public class ConsignmentListAdapter extends ResourceCursorAdapter {
        String quans,cQty,lQty;
        double value;
        ArrayList<Double> list = new ArrayList<>();
        boolean flag;

        public ConsignmentListAdapter(Activity mActivity, Cursor cursor) {
            super(mActivity, R.layout.consignment_stock_batch_item, cursor);

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            CheckBox batch_checkbox = (CheckBox) view
                    .findViewById(R.id.transfer_checkbox);
            batch_checkbox.setVisibility(View.VISIBLE);

            int pos = cursor.getInt(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_ID));
            Log.d("Position", "getView: no tag on " + pos);
            batch_checkbox.setTag(pos);

            TextView batch_sl_id = (TextView) view
                    .findViewById(R.id.batch_sl_id);


            batch_sl_id.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_ID)));


            TextView batch_sl_no = (TextView) view
                    .findViewById(R.id.batch_sl_no);
            Log.d("COLUMN_PRODUCT_SLNO","-->"+cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO)));
            batch_sl_no.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_SLNO)));

            TextView batch_prodcode = (TextView) view
                    .findViewById(R.id.batch_prodcode);
            batch_prodcode.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE)));

            TextView batch_prodname = (TextView) view
                    .findViewById(R.id.batch_prodname);
            batch_prodname.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_NAME)));

            TextView batch_no = (TextView) view.findViewById(R.id.batch_no);
            batch_no.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_CONSIGNMENT_NO)));


            TextView batch_expiryDate = (TextView) view
                    .findViewById(R.id.batch_expiryDate);
            batch_expiryDate.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_EXPIRY_DATE)));


            TextView batch_avail_cQty = (TextView) view
                    .findViewById(R.id.avl_cQty);
            batch_avail_cQty.setVisibility(View.VISIBLE);
            batch_avail_cQty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_CARTON)));

            Log.d("AVAILABLE_CARTON","-->"+cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_CARTON)));


            TextView batch_avail_lQty = (TextView) view
                    .findViewById(R.id.avl_lQty);
            batch_avail_lQty.setVisibility(View.VISIBLE);
            batch_avail_lQty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_LOOSE)));

            Log.d("AVAILABLE_LOOSE","-->"+cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_LOOSE)));


            TextView batch_avail_Qty = (TextView) view
                    .findViewById(R.id.avl_Qty);
            batch_avail_Qty.setVisibility(View.VISIBLE);
            batch_avail_Qty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_QTY)));


            Log.d("AVAILABLE_QTY","-->"+cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_QTY)));

            TextView batch_cQty = (TextView) view.findViewById(R.id.batch_cQty);
            batch_cQty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));

            Log.d("CARTON_QTY","-->"+cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));


            TextView batch_lQty = (TextView) view.findViewById(R.id.batch_lQty);
            batch_lQty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));

            Log.d("LOOSE_QTY","-->"+cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));

            final TextView batch_Qty = (TextView) view.findViewById(R.id.batch_Qty);
            batch_Qty.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));

            Log.d("QUANTITY","-->"+cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));

            TextView batch_pcsPerCarton = (TextView) view
                    .findViewById(R.id.batch_pcsPerCarton);
            batch_pcsPerCarton.setText(cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY)));
//            }



            String prodcode, avlQty="", qty="";
            prodcode = cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));


            avlQty = cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_QTY));
            qty = cursor.getString(cursor
                    .getColumnIndex(SOTDatabase.COLUMN_QUANTITY));

//            if(flag==true){
//                TextView batch_avail_cQty = (TextView) view
//                        .findViewById(R.id.avl_cQty);
//                batch_avail_cQty.setVisibility(View.VISIBLE);
//                batch_avail_cQty.setText(cursor.getString(cursor
//                        .getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));
//
//                Log.d("AVAILABLE_CARTON","-->"+cursor.getString(cursor
//                        .getColumnIndex(SOTDatabase.COLUMN_CARTON_QTY)));
//
//
//                TextView batch_avail_lQty = (TextView) view
//                        .findViewById(R.id.avl_lQty);
//                batch_avail_lQty.setVisibility(View.VISIBLE);
//                batch_avail_lQty.setText(cursor.getString(cursor
//                        .getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));
//
//                Log.d("AVAILABLE_LOOSE","-->"+cursor.getString(cursor
//                        .getColumnIndex(SOTDatabase.COLUMN_LOOSE_QTY)));
//
//
//                TextView batch_avail_Qty = (TextView) view
//                        .findViewById(R.id.avl_Qty);
//                batch_avail_Qty.setVisibility(View.VISIBLE);
//                batch_avail_Qty.setText(cursor.getString(cursor
//                        .getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));
//
//
//                Log.d("AVAILABLE_QTY","-->"+cursor.getString(cursor
//                        .getColumnIndex(SOTDatabase.COLUMN_QUANTITY)));
//
//                TextView batch_cQty = (TextView) view.findViewById(R.id.batch_cQty);
//                batch_cQty.setText(cursor.getString(cursor
//                        .getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_CARTON)));
//
//                Log.d("CARTON_QTY","-->"+cursor.getString(cursor
//                        .getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_CARTON)));
//
//
//                TextView batch_lQty = (TextView) view.findViewById(R.id.batch_lQty);
//                batch_lQty.setText(cursor.getString(cursor
//                        .getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_LOOSE)));
//
//                Log.d("LOOSE_QTY","-->"+cursor.getString(cursor
//                        .getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_LOOSE)));
//
//                final TextView batch_Qty = (TextView) view.findViewById(R.id.batch_Qty);
//                batch_Qty.setText(cursor.getString(cursor
//                        .getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_QTY)));
//
//                Log.d("QUANTITY","-->"+cursor.getString(cursor
//                        .getColumnIndex(SOTDatabase.COLUMN_AVAILABLE_QTY)));
//
//                TextView batch_pcsPerCarton = (TextView) view
//                        .findViewById(R.id.batch_pcsPerCarton);
//                batch_pcsPerCarton.setText(cursor.getString(cursor
//                        .getColumnIndex(SOTDatabase.COLUMN_PIECE_PERQTY)));
//
//            }else{


//                if(flag==true){
//                    avlQty = cursor.getString(cursor
//                            .getColumnIndex(SOTDatabase.COLUMN_QUANTITY));
//                    qty = quans;
//                }else{

//                }




         Log.d("avlQty&Qty","-->"+avlQty+"-->"+qty);

            if (avlQty.matches(qty)) {
                batch_checkbox.setChecked(true);
            } else {
                batch_checkbox.setChecked(false);
            }


            if (batch_checkbox.isChecked()) {
                Log.d("batch_checkbox", "-->" + batch_checkbox.isChecked());
                view.setBackgroundResource(R.drawable.list_item_selected_bg);
            } else {
                Log.d("batch", "-->" + batch_checkbox.isChecked());
                view.setBackgroundResource(R.drawable.list_item_even_bg);
            }


            Log.d("prodcode", "-->" + prodcode);
            String totQty = SOTDatabase.getConsignmentQty(prodcode);

            Log.d("totQty", "-->" + totQty);

            SalesOrderSetGet.setResult_value(totQty);

            edtotalqty.setText(totQty);


        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View tmpView = super.getView(position, convertView, parent);
            Log.d("getView:", "" + position);
            final CheckBox cBox = (CheckBox) tmpView
                    .findViewById(R.id.transfer_checkbox);
            int id = (int) cBox.getTag();
            Log.d("cBox id value:", "" + id);
            cBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    int id = (int) checkBox.getTag();
                    Log.d("checkBox id value:", "" + id);
                    if (checkBox.isChecked()) {
                        Log.d("CheckBox", "Checked!");
                        SOTDatabase.updateConsignmentQty(id, true);
                    } else {
                        Log.d("CheckBox", "NOT Checked!");
//                        SOTDatabase.updateBatchQty(id, false);
                        SOTDatabase.updateConsignmentQty(id, false);
                    }
                    cursor.requery();
                }
            });

            return tmpView;
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
}


//    private void headerCustCode(String quan_value, boolean flag) {
//        double cQ,lQ;
//        String cqty = "",lqty="",qty_val="";
//        String headerClass =SalesOrderSetGet.getHeader_flag();
//        Log.d("ConsignmentList","-->"+conCode+" "+conProdCode+"Project"+headerClass);
//
//
//            cursor =SOTDatabase.getConsignmentStocks(conProdCode);
////        cursor=SOTDatabase.getConsignmentStock(conProdCode,sale_sl_no);
//            Log.d("ConsignmentClass","-->"+cursor.getCount() +" "+quan_value+" "+flag);
//
////            if(flag==true){
////                consignmentListAdapter = new ConsignmentListAdapter(mActivity, cursor);
////                batch_list.setAdapter(consignmentListAdapter);
////            }else{
////                consignmentListAdapter = new
////
////                        ConsignmentListAdapter(mActivity, cursor,"","","",flag);
////                batch_list.setAdapter(consignmentListAdapter);
////            }
//
//
////        }else{
////
////            double dQty = Double.parseDouble(quan_value);
////            double dPcs = Double.parseDouble(btCartonPerQty);
////            Log.d("qty", "" + quan_value);
////            Log.d("cartonPerQty", "" + btCartonPerQty);
////            if (!btCartonPerQty.matches("")) {
////                if (!quan_value.matches("")) {
////                    try {
////                        int qty_nt = (int) dQty;
////                        int pcs_nt = (int) dPcs;
////
////                        Log.d("qty_nt", "" + qty_nt);
////                        Log.d("pcs_nt", "" + pcs_nt);
////
////                        cQ= qty_nt / pcs_nt;
////                        lQ = qty_nt % pcs_nt;
////
////                        Log.d("cqty", "" + cQ);
////                        Log.d("lqty", "" + lQ);
////
////                        cqty = "" + cQ;
////                        lqty = "" + lQ;
////                        qty_val = "" + qty_nt;
////                        // bat_cartonQty.setText("" + q);
////                        // bat_looseQty.setText("" + r);
////
////                    } catch (ArithmeticException e) {
////                        System.out.println("Err: Divided by Zero");
////
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                }
////
////            }
////
////            cursor =SOTDatabase.getConsignmentStocks(conProdCode);
//////        cursor=SOTDatabase.getConsignmentStock(conCode,conProdCode);
////            Log.d("ConsignmentListAdapter","-->"+cursor.getCount() +" "+quan_value);
////            consignmentListAdapter = new ConsignmentListAdapter(mActivity, cursor,qty_val,cqty,lqty,headerClass);
////            batch_list.setAdapter(consignmentListAdapter);
////
////        }
//        registerForContextMenu(batch_list);
//    }

