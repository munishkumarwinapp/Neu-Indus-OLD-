package com.winapp.sot;

import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;

import java.text.DecimalFormat;

import static com.winapp.sot.SOTSummaryWebService.context;

/**
 * Created by Sathish on 12/31/2019.
 */

public class ManualStockDialog extends DialogFragment {
    static String productCode;
    String productName,codefield;
    String stockTakeNo,slno,crtnperQty,cqty,lqty;
    String qty;
    int flag=0;
    TextView pCode,pName,stockInHand,pcspercarton;
    EditText quantity;
    EditText cQty,lQty;
    ImageView close,ok;
    ManualStockListener manualStockListener;
    private TextWatcher ccQty;

    public static ManualStockDialog newInstance(String codefield, String namefield, String con_no,String qty,String pcs,String cQty,String lQty) {
        ManualStockDialog frag = new ManualStockDialog();
        Log.d("consignmentNoCheck","-->"+codefield+","+namefield+"--->"+qty+"-->"+pcs+"stockTakeNo:"+con_no);
        Bundle args = new Bundle();
        args.putString("pcode", codefield);
        args.putString("pName",namefield);
        args.putString("conNo",con_no);
        args.putString("quantity",qty);
        args.putString("pcsper",pcs);
        args.putString("cQty",cQty);
        args.putString("lQty",lQty);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = inflater.inflate(R.layout.manual_stock, container, false);
        productCode = getArguments().getString("pcode");
        productName =  getArguments().getString("pName");
        stockTakeNo = getArguments().getString("conNo");
        slno = getArguments().getString("quantity");
        crtnperQty = getArguments().getString("pcsper");
        cqty = getArguments().getString("cQty");
        lqty = getArguments().getString("lQty");
        Log.d("quantityCheck1",""+slno+"-->"+crtnperQty);
        SOTDatabase.init(context);

        pCode = (TextView)dialogView.findViewById(R.id.code);
        pName = (TextView)dialogView.findViewById(R.id.productName);
//        description = (EditText)dialogView.findViewById(R.id.product);
        quantity = (EditText)dialogView.findViewById(R.id.sl_qty);
        stockInHand = (TextView)dialogView.findViewById(R.id.stockOnhand);
        close = (ImageView)dialogView.findViewById(R.id.close);
        pcspercarton = (TextView)dialogView.findViewById(R.id.pcspercarton);
        ok = (ImageView)dialogView.findViewById(R.id.ok);
        cQty = (EditText) dialogView.findViewById(R.id.sl_cartonQty);
        lQty = (EditText) dialogView.findViewById(R.id.sl_looseQty);
        quantity.requestFocus();

        ok.setOnClickListener(mUpdateOnClickListener);
        close.setOnClickListener(mDismissOnClickListener);

        if(slno.matches("")){
            pCode.setText(productCode);
            pName.setText(productName);
            pcspercarton.setText(crtnperQty);
            if(stockTakeNo.matches("")){
                SOTDatabase.storeManualStock("", "", productCode, productName, cQty.getText().toString(), lQty.getText().toString(), pcspercarton.getText().toString(), qty, "");
            }
        }else{
            pCode.setText(productCode);
            pName.setText(productName);
            pcspercarton.setText(crtnperQty);
            quantity.setText(slno);
            cQty.setText(cqty);
            lQty.setText(lqty);
        }




        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clQty();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(quantity.getText().toString().matches("")){
                    cQty.setText("");
                    lQty.setText("");
                }
            }
        });
        return dialogView;



    }


    private View.OnClickListener mUpdateOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            Log.d("pcodeCheck",productCode);
            qty = quantity.getText().toString();
            if(qty.matches("")){
                Toast.makeText(getActivity(),"Please Enter Quantity!!",Toast.LENGTH_SHORT).show();
            }else {
                Log.d("qtycheck", qty);

                Cursor cursor = SOTDatabase.getManualStockCursors(productCode);
                Log.d("cursorCount","-->"+cursor.getCount());
                if (cursor != null && cursor.getCount() > 0) {

                    if (cursor.moveToFirst()) {
                        do {
                            codefield = cursor.getString(cursor
                                    .getColumnIndex(SOTDatabase.COLUMN_PRODUCT_CODE));

                            Log.d("codefield",codefield);
                            if(productCode.matches(codefield)){
                                SOTDatabase.updateManualStock(productCode, productName, cQty.getText().toString(), lQty.getText().toString(), pcspercarton.getText().toString(), qty, "0");
                            }

                        } while (cursor.moveToNext());
                    }

                }
                manualStockListener = (ManualStockListener) getActivity();
                manualStockListener.refreshAdapter(productCode);

            }
            dismiss();
        }
    };

    private void clQty() {
        try{
            String qty = quantity.getText().toString();

            Log.d("qty recalc", ""+qty);


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

                        q = (int)(qty_nt / pcs_nt);
                        r = (qty_nt % pcs_nt);

                        Log.d("cqty", "" + q);
                        Log.d("lqty", "" + r);

                        String ctn = twoDecimalPoint(q);
                        String loose = twoDecimalPoint(r);


                        cQty.setText("" + ctn.split("\\.")[0]);
//                        cQty.addTextChangedListener(cqtyTW);

//                        lQty.removeTextChangedListener(lqtyTW);
                        lQty.setText("" + loose.split("\\.")[0]);
//                        lQty.addTextChangedListener(lqtyTW);

//

                    } catch (ArithmeticException e) {
                        System.out.println("Err: Divided by Zero");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }catch(NumberFormatException e){
            e.printStackTrace();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String twoDecimalPoint(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        String tot = df.format(d);

        return tot;
    }

    private View.OnClickListener mDismissOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            dismiss();
        }
    };

    public interface ManualStockListener {
        void refreshAdapter(String prod);
    }
}
