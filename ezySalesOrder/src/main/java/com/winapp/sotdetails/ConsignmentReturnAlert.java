package com.winapp.sotdetails;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.winapp.SFA.R;

/**
 * Created by Sathish on 03-12-2018.
 */

public class ConsignmentReturnAlert extends DialogFragment {
    TextView pro_code,cus_name,pcs_per_carton;
    Button split_btn;
    EditText split;
    ListView mListView;
    AlertDataAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = inflater
                .inflate(R.layout.alert_return_popup, container, false);
        pro_code =(TextView)dialogView.findViewById(R.id.pro_code);
        cus_name =(TextView)dialogView.findViewById(R.id.cust_name);
        pcs_per_carton=(TextView)dialogView.findViewById(R.id.pcs_qty);
        split_btn =(Button)dialogView.findViewById(R.id.deli_addProduct);
        split =(EditText)dialogView.findViewById(R.id.total_split_qty);
        mListView =(ListView)dialogView.findViewById(R.id.listview);



    return dialogView;
    }

    private class AlertDataAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.alert_return_multi_summary, null);
                holder.con_no =(TextView)convertView.findViewById(R.id.con_no);
                holder.con_date =(TextView)convertView.findViewById(R.id.con_date);
                holder.exp_date = (TextView)convertView.findViewById(R.id.exp_date);
                holder.carton_qty =(TextView) convertView.findViewById(R.id.carton_qty);
                holder.loose_qty =(TextView)convertView.findViewById(R.id.loose_qty);
                holder.bal_qty =(EditText)convertView.findViewById(R.id.qty);
                holder.ret_qty =(EditText)convertView.findViewById(R.id.qty1);
                holder.ret_carton =(TextView)convertView.findViewById(R.id.carton_qty1);
                holder.ret_loose =(TextView)convertView.findViewById(R.id.loose_qty1);
                holder.total=(TextView)convertView.findViewById(R.id.total);
                holder.cPrice =(TextView)convertView.findViewById(R.id.cprice);
                holder.price =(TextView)convertView.findViewById(R.id.price);
                holder.tax =(TextView)convertView.findViewById(R.id.tax);
                holder.net_total =(TextView)convertView.findViewById(R.id.net_total);
                convertView.setTag(holder);
            }
            return convertView;
        }
    }

    public class ViewHolder {
        TextView con_no,con_date,exp_date,carton_qty,loose_qty,ret_carton,ret_loose,total,
        cPrice,price,tax,net_total;
        EditText bal_qty,ret_qty;

    }
}
