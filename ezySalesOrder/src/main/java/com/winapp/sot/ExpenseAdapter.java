package com.winapp.sot;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.sotdetails.ExpenseHeader;

import java.util.ArrayList;

/**
 * Created by Sathish on 30-10-2019.
 */

public class ExpenseAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<SO> listarray = new ArrayList<SO>();
    private LayoutInflater mInflater = null;
    private int selectedPosition = -1;
    int resource;

    public ExpenseAdapter(Activity activty, int resource, String o, ArrayList<SO> list) {
        this.listarray.clear();
        this.ctx = activty;
        if(!list.isEmpty()) {
            this.listarray = list;
        }
//		this.listarray = list;
        Log.d("listarray","Empty"+listarray.size() +ctx);
        this.resource = resource;
        mInflater = activty.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return listarray.size();
    }

    @Override
    public SO getItem(int position) {
        return listarray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SO so = listarray.get(position);
        final ViewHolder holder;
        holder = new ViewHolder();
        convertView = mInflater.inflate(resource, null);
        holder.so_sno = (TextView) convertView.findViewById(R.id.sno);
        holder.so_date = (TextView) convertView.findViewById(R.id.date);
        holder.so_custcode = (TextView) convertView.findViewById(R.id.custcode);
        holder.so_amount = (TextView) convertView.findViewById(R.id.amount);
        holder.so_status = (TextView) convertView.findViewById(R.id.status);
        holder.list_item_layout = (LinearLayout) convertView
                .findViewById(R.id.list_item_layout);

        holder.balance_amount = (TextView) convertView
                .findViewById(R.id.balance_amount);

        holder.so_custcode.setText(so.getCustomerName());
        holder.so_sno.setText(so.getSno());
        holder.so_date.setText(so.getDate());
        holder.so_amount.setText(so.getNettotal());
        holder.balance_amount.setText(so.getSubTotal());
        holder.so_status.setText(so.getStatus());

        holder.checkBox = (CheckBox) convertView
                .findViewById(R.id.checkbox);
        convertView.setTag(holder);
        convertView.setId(position);
        holder.checkBox.setChecked(so.isSelected());
        holder.checkBox.setId(position);
        Log.d("ListArrayItemCheck","-->"+listarray.size());

        final View finalConvertView = convertView;
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                SO so = listarray.get(v.getId());
                if (checkBox.isChecked()) {
                    selectAll(false);
                    so.setSelected(true);
                    selectedPosition = v.getId();
                    holder.checkBox.setVisibility(View.VISIBLE);
                        Log.d("isCheckedValue","-->"+checkBox.isChecked()+selectedPosition);
                        ((View) holder.list_item_layout.getParent())
                                .setBackgroundResource(R.drawable.list_grey_select);

                }else{
                    int id = v.getId();
                    so.setSelected(false);
                    selectedPosition = -1;

                    if (id % 2 == 0) {
                        ((View) checkBox.getParent())
                                .setBackgroundResource(R.drawable.list_item_even_bg);
                    } else {
                        ((View) checkBox.getParent())
                                .setBackgroundResource(R.drawable.list_item_odd_bg);
                    }

                     if(ctx instanceof ExpenseHeader)
                    {
                        ((View) holder.list_item_layout.getParent()).setBackgroundResource(R.drawable.list_grey_bg);
                    }
                }

                if (ctx instanceof ExpenseHeader) {
                    ((ExpenseHeader) ctx).showViews(checkBox.isChecked(),
                            R.id.printer);
                }
                if (so.isSelected()) {
                    finalConvertView
                            .setBackgroundResource(R.drawable.list_grey_select);
                } else {
                    finalConvertView
                            .setBackgroundResource(R.drawable.list_grey_bg);
                }


                if(ctx instanceof ExpenseHeader)
                {
                    finalConvertView.setBackgroundResource(R.drawable.list_grey_bg);

                    if (so.isSelected()) {
                        finalConvertView
                                .setBackgroundResource(R.drawable.list_grey_select);
                    } else {
                        finalConvertView
                                .setBackgroundResource(R.drawable.list_grey_bg);
                    }
                }
            }
            });

        return convertView;
    }

    public void selectAll(boolean select) {
        Log.d("selectAll","-->"+select);
        for (SO so : listarray) {
            so.setSelected(select);
        }
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView so_sno,so_date,so_custcode,so_amount,so_status;
        LinearLayout list_item_layout;
        TextView balance_amount;


        CheckBox checkBox;
    }
}
