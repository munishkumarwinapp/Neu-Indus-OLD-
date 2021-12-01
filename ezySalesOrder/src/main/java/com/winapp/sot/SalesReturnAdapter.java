package com.winapp.sot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.sotdetails.DeliveryVerificationHeader;
import com.winapp.sotdetails.ExpenseHeader;
import com.winapp.sotdetails.PackingHeader;

import java.util.ArrayList;

public class SalesReturnAdapter extends BaseAdapter
{
    Context ctx;
    String datavalue, status, paid = "1", notpaid = "0", all = "2",podpending = "3",overdue = "4",
            clssnm = "SalesOrder",showView = "true", verified = "Verified", notverified = "Not Verified";
    ArrayList<SO> listarray = new ArrayList<SO>();
    private LayoutInflater mInflater = null;
    private int selectedPosition = -1;

    boolean isSaleOrder;
    int resource;
    View finalConvertView;

    public SalesReturnAdapter(Activity activty, int resource, String status,
                         ArrayList<SO> list) {
        this.listarray.clear();
        this.ctx = activty;

        if(!list.isEmpty()) {
            this.listarray = list;
        }
//		this.listarray = list;
        Log.d("listarray","Empty"+listarray.size() +ctx);
        this.resource = resource;
        this.status = status;
        mInflater = activty.getLayoutInflater();

        Log.d("status","st "+status);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listarray.size();
    }

    @Override
    public SO getItem(int position) {
        // TODO Auto-generated method stub
        return listarray.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {
            final SO so = listarray.get(position);
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.invoice_list_item_latest, null);
                holder.so_sno = (TextView) convertView.findViewById(R.id.sno);
                holder.so_date = (TextView) convertView.findViewById(R.id.date);
                holder.so_custcode = (TextView) convertView.findViewById(R.id.custcode);
                holder.so_amount = (TextView) convertView.findViewById(R.id.amount);
                holder.so_status = (TextView) convertView.findViewById(R.id.status);

                holder.so_layout2 = (LinearLayout) convertView
                        .findViewById(R.id.layout2);
                holder.so_delCustomerName = (TextView) convertView
                        .findViewById(R.id.DelCustomerName);
                holder.list_item_layout = (LinearLayout) convertView
                        .findViewById(R.id.list_item_layout);
                holder.balance_amount = (TextView) convertView
                        .findViewById(R.id.balance_amount);
                holder.overdue_txt = (TextView) convertView
                        .findViewById(R.id.overdue_txt);
                holder.invoice_signed = (TextView) convertView
                        .findViewById(R.id.invoice_signed);

                holder.checkBox = (CheckBox) convertView
                        .findViewById(R.id.checkbox);

                holder.so_sno.setVisibility(View.VISIBLE);
                holder.balance_amount = (TextView) convertView.findViewById(R.id.balance_amount);
                holder.balance_amount.setText(so.getBalanceamount());
                holder.so_status = (TextView) convertView.findViewById(R.id.status);
                holder.so_status.setText(so.getSalesType());
                holder.so_status.setVisibility(View.GONE);
                holder.so_custcode.setText(so.getCustomerName());

                convertView.setTag(holder);
                convertView.setId(position);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkBox.setChecked(so.isSelected());
            holder.checkBox.setId(position);
            Log.d("ListArrayItemCheck","-->"+listarray.size());

            finalConvertView = convertView;
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    SO so = listarray.get(v.getId());
                    if (checkBox.isChecked()) {
                        Log.d("checkboxChecked","-->"+checkBox.isChecked());
                        selectAll(false);
                        so.setSelected(true);
                        selectedPosition = v.getId();
                        Log.d("selectedPosition","-->"+selectedPosition+position);
                        holder.checkBox.setVisibility(View.VISIBLE);

                        for(int i=0; i<listarray.size(); i++)
                        {
                            holder.list_item_layout.setBackgroundResource(R.drawable.list_grey_bg);
                            Log.e("Checked", "Checked");
                        }
                        if(selectedPosition == position)
                        {
                            //holder.list_item_layout.setBackgroundResource(R.drawable.list_grey_select);
                            ((View)holder.list_item_layout.getParent()).setBackgroundResource(R.drawable.list_grey_select);
                        }

                        //holder.list_item_layout.setBackgroundResource(R.drawable.list_grey_select);
//                        ((View)holder.list_item_layout.getParent()).setBackgroundResource(R.drawable.list_grey_select);
                    } else {
                        so.setSelected(false);
                        selectedPosition = -1;
                        Log.d("elseselectedPosition","-->"+selectedPosition);
                        //holder.list_item_layout.setBackgroundResource(R.drawable.list_grey_bg);
                        ((View)holder.list_item_layout.getParent()).setBackgroundResource(R.drawable.list_grey_select);
                    }

                        ((SalesReturnHeader) ctx).showViews(checkBox.isChecked(),
                                R.id.printer);

                        ((SalesReturnHeader) ctx).showViews(checkBox.isChecked(), R.id.printer);

                    if (holder.checkBox.isChecked()) {
                        RowItem.setPrintoption("True");

                    } else {
                        RowItem.setPrintoption("False");
                    }
                }
            });


            holder.so_sno.setText(so.getSno());
            holder.so_date.setText(so.getDate());

            if(ctx instanceof SalesReturnHeader)
            {
                convertView.setBackgroundResource(R.drawable.list_grey_bg);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    public void remove(SO item) {
        listarray.remove(item);
    }

    public boolean isAllSelected() {
        for (SO so : listarray) {
            if (!so.isSelected()) {
                return false;
            }
        }
        return true;
    }

    public void selectAll(boolean select) {
        Log.d("selected","-->"+select);
        for (SO so : listarray) {
            so.setSelected(select);
        }
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    class ViewHolder {
        CheckBox checkBox;
        TextView so_sno,remarks,txt_fromloc,txt_toloc,txt_remarks;
        TextView so_date;
        TextView so_custcode;
        TextView so_amount;
        TextView so_status;
        LinearLayout so_layout2;
        TextView so_delCustomerName;
        TextView balance_amount;
        LinearLayout list_item_layout,status_layout;
        TextView overdue_txt;
        TextView invoice_signed;
        TextView IsPosted_status;
        TextView IsClosed_status;
    }

    public void showAll(boolean show) {
        // TODO Auto-generated method stub
        if(show == true){
            showView="false";
        }
        else if(show == false){
            showView="true";
        }
        notifyDataSetChanged();
    }
}
