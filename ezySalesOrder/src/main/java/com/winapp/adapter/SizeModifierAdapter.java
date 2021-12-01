package com.winapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.sot.SOTDatabase;

import java.util.ArrayList;

/**
 * Created by USER on 16/5/2017.
 */

public class SizeModifierAdapter extends BaseAdapter {

    public interface OnCompletionListener {
        public void onCompleted(int rowItem);
    }
    Context context;
    ArrayList<Attribute> rowItems,rowSizeItems;
    private OnCompletionListener listener;
    private static int sizePosition = 0;
    private static String color ="";
    private static String sizeName = "",sizecode="";
    private ArrayList<RowItemHeader> rowItemHeaderList;

    public void setOnCompletionListener(OnCompletionListener listener) {
        this.listener = listener;
    }

    public SizeModifierAdapter(){
        rowItems = new ArrayList<>();
    }
    public SizeModifierAdapter(Context context, ArrayList<Attribute> items) {
        rowItems = new ArrayList<>();
        rowSizeItems = new ArrayList<>();
        rowItemHeaderList = new ArrayList<RowItemHeader>();
        this.context = context;
        this.rowItems = items;
        SOTDatabase.init(context);
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtTitle;
        TextView txtDesc;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_attribute_size, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.qty);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Attribute rowItem = (Attribute) getItem(position);

        String qty = rowItem.getSizeQty();

        if(qty.equals("0")){
            holder.txtDesc.setVisibility(View.INVISIBLE);
        }else{
            holder.txtDesc.setVisibility(View.VISIBLE);
        }

        holder.txtTitle.setText(rowItem.getSizename());

        if(rowItem.isSelected()){
            holder.txtTitle.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.txtTitle.setBackgroundResource(R.drawable.size_select);
        }else{
            holder.txtTitle.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.txtTitle.setBackgroundResource(R.drawable.unselect);
        }

        Log.d("rowItem.getColor()","rr "+rowItem.getColor());
         if(color.equals(rowItem.getColor())){
            holder.txtDesc.setText(rowItem.getSizeQty());
        }else{
            holder.txtDesc.setText("0");
        }

        holder.txtTitle.setId(position);
        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unSelectAll();
                int position = view.getId();
               final Attribute rowItem = (Attribute) getItem(position);
                if(rowItem.isSelected){
                    rowItem.setSelected(false);
                }else{
                    rowItem.setSelected(true);
                }
                sizePosition = position;
                sizeName = rowItem.getSizename();
                sizecode=rowItem.getSizecode();
                ((Activity) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onCompleted(sizePosition);
                        }
                    }
                });
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
    public static String gets(){
        return sizeName;
    }
    public void unSelectAll(){
        for(Attribute rowItem : rowItems){
            rowItem.setSelected(false);
        }
        notifyDataSetChanged();
    }
    public void setColor(String color) {
        this.color = color;
       if(rowItemHeaderList!=null) {
           for (int i = 0; i < rowItemHeaderList.size(); i++) {

               ArrayList<Attribute> rowList = rowItemHeaderList.get(i).getRowItemList();
               String colorName = rowItemHeaderList.get(i).getColorName();

               if (color.equals(colorName)) {
                   Log.d("colorName", "h-->" + colorName);
                   for (int j = 0; j < rowList.size(); j++) {
                       String colr = rowList.get(j).getColor();
                       if (colr.equals(colorName)) {
                           String sizeQty = rowList.get(j).getSizeQty();
                           String sizeName = rowList.get(j).getSizename();
                           int position = rowList.get(j).getPosition();
                           Log.d("sizeName", "-->" + sizeName);
                           Log.d("sizeQty", "-->" + sizeQty);
                           for (int k = 0; k < rowItems.size(); k++) {
                               String size_Color = rowItems.get(k).getColor();
                               String size_Name = rowItems.get(k).getName();
                               Log.d("size_Color", "-->" + size_Color);
                               if (k == position) {
                                   rowItems.get(position).setColor(color);
                                   rowItems.get(position).setSizeQty(sizeQty);
                               }

                           }

                       }

                   }
               }
           }
       }

        notifyDataSetChanged();
    }
    public void setQty(String qty,String color){
    try {
        if(rowItems!=null) {
            if (rowItems.size() > 0) {
                for (int i = 0; i < rowItems.size(); i++) {
                    if (i == sizePosition) {
                        rowItems.get(i).setSizeQty(qty);
                        rowItems.get(i).setColor(color);
                        Attribute rowItem = new Attribute();
                        rowItem.setName(sizeName);
                        rowItem.setSizeQty(qty);
                        rowItem.setColor(color);
                        rowItem.setPosition(i);
                        rowSizeItems.add(rowItem);
                        RowItemHeader rowItemHeader = new RowItemHeader(color, rowSizeItems);
                        rowItemHeaderList.add(rowItemHeader);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }catch (Exception e){
        e.printStackTrace();
    }
    }

    public void setSizeQtyAll(String selectedColorName, String sizeQty,boolean isSelectAll){
        try {
            this.color = selectedColorName;
            Log.d("selectedColorCode", "sr " + selectedColorName);
            for (Attribute size : rowItems) {
                size.setSizeQty(sizeQty);
                size.setColor(selectedColorName);
                size.setSelected(isSelectAll);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        /*for (int i = 0; i < rowItems.size(); i++) {
            String colorCode = rowItems.get(i).getCode();
            if (selectedColorName.equalsIgnoreCase(colorCode)) {
                rowItems.get(i).setSizeQty(sizeQty);
            }
        }*/
        notifyDataSetChanged();
    }

    public int getQty(String color){
        int qty=0;
        if(rowItems!=null) {
            for (Attribute rowItem : rowItems) {
                if (rowItem.getColor() != null && !rowItem.getColor().isEmpty()) {
                    if (rowItem.getColor().equals(color)) {
                        int sizeQty = rowItem.getSizeQty().equals("") ? 0 : Integer.valueOf(rowItem.getSizeQty());
                        qty += sizeQty;
                    }
                }

            }
        }
        return qty;
    }
    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

}
