package com.winapp.sot;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.winapp.SFA.R;

public class AddSimpleAdapter extends SimpleAdapter {

    Activity mActivity;
    ArrayList<HashMap<String, String>> mData;
    private String flag = "";

    public AddSimpleAdapter(Activity context,
                            ArrayList<HashMap<String, String>> stockin, int resource, String flag,
                            String[] from, int[] to) {
        super(context, stockin, resource, from, to);
        this.mData = new ArrayList<HashMap<String, String>>();
        this.mActivity = context;
        this.mData.clear();
        this.mData = stockin;
        this.flag = flag;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        // View view = super.getView(position, convertView, parent);
        TextView txt_code = (TextView) view.findViewById(R.id.txt_code);
        TextView txt_name = (TextView) view.findViewById(R.id.txt_name);
        TextView txt_price = (TextView) view.findViewById(R.id.txt_price);
        TextView txt_qty = (TextView) view.findViewById(R.id.txt_qty);
        TextView txt_oldqty = (TextView) view.findViewById(R.id.txt_oldqty);

        if (mActivity instanceof InvoiceAddProduct || mActivity instanceof SalesAddProduct) {
            txt_code.setVisibility(View.GONE);
            txt_qty.setVisibility(View.VISIBLE);
        }

        if ((mActivity instanceof TransferAddProduct) && (flag.equalsIgnoreCase("ReceiveStock"))) {
            txt_oldqty.setVisibility(View.VISIBLE);
            String qty = mData.get(position).get("Qty");
            double dqty = Double.valueOf(qty);
            int iqty = (int) dqty;
            Log.d("oldqty", "->" + iqty);
            txt_oldqty.setText(iqty + "");
        }

        if (mActivity instanceof StockTakeAddProduct){
            txt_oldqty.setVisibility(View.VISIBLE);
        }

        String pcode = mData.get(position).get("ProductCode");
        String db_pcode = SOTDatabase.getProductCode(pcode);
        Log.d("AddSimple db_pcode", "->" + db_pcode);
        if (position % 2 == 0) {

            if (db_pcode != null && !db_pcode.isEmpty()) {
                double pqty = SOTDatabase.getProductQty(db_pcode);
                int iqty = (int) pqty;
                Log.d("dbqty", "->" + iqty);
                txt_qty.setText(iqty + "");
                if ((mActivity instanceof StockTakeAddProduct)) {
                    double cqty = SOTDatabase.getCartonQty(db_pcode);
                    int c_iqty = (int) cqty;
                    Log.d("dbqty", "->" + c_iqty);
                    txt_price.setText(c_iqty + "");
                    double lqty = SOTDatabase.getLooseQty(db_pcode);
                    int l_iqty = (int) lqty;
                    Log.d("dbqty", "->" + l_iqty);
                    txt_oldqty.setText(l_iqty + "");

                }
                String qty = mData.get(position).get("Qty");
                double dqty = Double.valueOf(qty);
                int i_qty = (int) dqty;
                Log.d("i_qty", String.valueOf(i_qty));
                if ((mActivity instanceof TransferAddProduct) && (flag.equalsIgnoreCase("ReceiveStock"))) {
                    if (iqty == i_qty) {
                        view.setBackgroundResource(R.drawable.list_item_selected_bg);
                    } else {
                        view.setBackgroundResource(R.drawable.list_item_red_bg);
                    }
                } else {
                    view.setBackgroundResource(R.drawable.list_item_selected_bg);
                }
            } else {
                view.setBackgroundResource(R.drawable.list_item_even_bg);
                if ((mActivity instanceof StockTakeAddProduct)) {
                    String qty = mData.get(position).get("Qty");
                    double dqty = Double.valueOf(qty);
                    int iqty = (int) dqty;
                    Log.d("old_qty2", "->" + iqty);
                    txt_qty.setText(iqty + "");
                    String cqty = mData.get(position).get("Carton");
                    double c_dqty = Double.valueOf(cqty);
                    int c_iqty = (int) c_dqty;
                    Log.d("carton_qty2", "->" + c_iqty);
                    txt_price.setText(c_iqty + "");
                    String lqty = mData.get(position).get("Loose");
                    double l_dqty = Double.valueOf(lqty);
                    int l_iqty = (int) l_dqty;
                    Log.d("loose_qty2", "->" + l_iqty);
                    txt_oldqty.setText(l_iqty + "");
                } else {
                    txt_qty.setText("0");
                }
            }

            txt_code.setTextColor(Color.parseColor("#035994"));
            txt_name.setTextColor(Color.parseColor("#035994"));
            txt_price.setTextColor(Color.parseColor("#035994"));
            txt_qty.setTextColor(Color.parseColor("#035994"));

        } else {

            if (db_pcode != null && !db_pcode.isEmpty()) {
                double pqty = SOTDatabase.getProductQty(db_pcode);
                int iqty = (int) pqty;
                txt_qty.setText(iqty + "");
                if ((mActivity instanceof StockTakeAddProduct)) {
                    double cqty = SOTDatabase.getCartonQty(db_pcode);
                    int c_iqty = (int) cqty;
                    Log.d("dbqty", "->" + c_iqty);
                    txt_price.setText(c_iqty + "");
                    double lqty = SOTDatabase.getLooseQty(db_pcode);
                    int l_iqty = (int) lqty;
                    Log.d("dbqty", "->" + l_iqty);
                    txt_oldqty.setText(l_iqty + "");

                }
                String qty = mData.get(position).get("Qty");
                double dqty = Double.valueOf(qty);
                int i_qty = (int) dqty;
                if ((mActivity instanceof TransferAddProduct) && (flag.equalsIgnoreCase("ReceiveStock"))) {
                    if (iqty == i_qty) {
                        view.setBackgroundResource(R.drawable.list_item_selected_bg);
                    } else {
                        view.setBackgroundResource(R.drawable.list_item_red_bg);
                    }
                } else {
                    view.setBackgroundResource(R.drawable.list_item_selected_bg);
                }
            } else {
                if ((mActivity instanceof StockTakeAddProduct)) {
                    String qty = mData.get(position).get("Qty");
                    double dqty = Double.valueOf(qty);
                    int iqty = (int) dqty;
                    Log.d("old_qty1", "->" + iqty);
                    txt_qty.setText(iqty + "");
                    String cqty = mData.get(position).get("Carton");
                    double c_dqty = Double.valueOf(cqty);
                    int c_iqty = (int) c_dqty;
                    Log.d("carton_qty1", "->" + c_iqty);
                    txt_price.setText(c_iqty + "");
                    String lqty = mData.get(position).get("Loose");
                    double l_dqty = Double.valueOf(lqty);
                    int l_iqty = (int) l_dqty;
                    Log.d("loose_qty1", "->" + l_iqty);
                    txt_oldqty.setText(l_iqty + "");
                } else {
                    txt_qty.setText("0");
                }
                view.setBackgroundResource(R.drawable.list_item_odd_bg);
            }

            txt_code.setTextColor(Color.parseColor("#646464"));
            txt_name.setTextColor(Color.parseColor("#646464"));
            txt_price.setTextColor(Color.parseColor("#646464"));
            txt_qty.setTextColor(Color.parseColor("#035994"));
        }
        return view;
    }

}
