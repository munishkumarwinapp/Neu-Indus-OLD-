package com.winapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winapp.SFA.R;

import java.util.ArrayList;

/**
 * Created by USER on 16/5/2017.
 */

public class ColorModifierAdapter extends BaseAdapter
{

    public interface OnCompletionListener {
        public void onCompleted(int position);

    }

    private OnCompletionListener listener;
    private Context context;
    private ArrayList<Attribute> color;
    private ArrayList<Attribute> colorArrList;
    private ArrayList<Attribute> size;
    private int colorPosition = 0;
    private String colorflag = "",colorCode = "";

    public void setOnCompletionListener(OnCompletionListener listener) {
        this.listener = listener;
    }

    public ColorModifierAdapter(Activity context, ArrayList<Attribute> color, ArrayList<Attribute> size) {
        this.color = new ArrayList<>();
        this.size = new ArrayList<>();
        this.context = context;
        this.color = color;
        this.size=size;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
        LinearLayout griditem_layout;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_attribute_color, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.qty);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.name);
            holder.griditem_layout = (LinearLayout) convertView.findViewById(R.id.griditem_layout);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Attribute rowItem = getItem(position);

        String qty = rowItem.getColorQty();
        if(qty.equals("0")){
            holder.txtDesc.setVisibility(View.INVISIBLE);
        }else{
            holder.txtDesc.setVisibility(View.VISIBLE);
        }
        holder.txtDesc.setText(rowItem.getColorQty());
        holder.txtTitle.setText(rowItem.getName());

        if(rowItem.isSelected()){
            holder.txtTitle.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.txtTitle.setBackgroundResource(R.drawable.color_select);


        }else{
            holder.txtTitle.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.txtTitle.setBackgroundResource(R.drawable.unselect);
        }
        holder.griditem_layout.setId(position);
        holder.griditem_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unSelectAll();
                int position = view.getId();
                Attribute rowItem = getItem(position);
                if(rowItem.isSelected){
                    rowItem.setSelected(false);
                }else{
                    rowItem.setSelected(true);
                }
                colorflag = rowItem.getName();
                colorCode = rowItem.getCode();
                colorPosition = position;

                ((Activity) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onCompleted(colorPosition);
                        }
                    }
                });
                notifyDataSetChanged();



            }
        });
        return convertView;
    }
    public void unSelectAll(){
        for(Attribute rowItem : color){
            rowItem.setSelected(false);
        }
        notifyDataSetChanged();
    }
    public int getColorPositon(){
        return colorPosition;
    }
    public String getColorFlag(){
        return colorflag;
    }
    public String getColorCode(){
        return colorCode;
    }
    public int getSize(){
        return color.size();
    }

    public void setQty(String qty){
        if(color!=null) {
            Log.d("rowItem-->", "" + color.size());
            for (int i = 0; i < color.size(); i++) {
                if (i == colorPosition) {
                    color.get(i).setColorQty(qty);
                }
            }
        }
        notifyDataSetChanged();
    }
    public int getQty(){
        int qty=0;
        for(Attribute rowItem :color ){
            int colorQty = rowItem.getColorQty().equals("") ? 0 : Integer.valueOf(rowItem.getColorQty());
            qty += colorQty;


        }
        return qty;
    }
    @Override
    public int getCount() {
        return color.size();
    }

    @Override
    public Attribute getItem(int position) {
        return color.get(position);
    }

    @Override
    public long getItemId(int position) {
        return color.indexOf(getItem(position));
    }

}