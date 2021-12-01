package com.winapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.crm.MapsActivity;
import com.winapp.SFA.R;
import com.winapp.model.Attendance;
import com.winapp.model.Product;
import com.winapp.util.Validate;

import java.util.ArrayList;

/**
 * Created by user on 19-Apr-17.
 */

public class StockCheckAdapter extends RecyclerView.Adapter<StockCheckAdapter.StockCheckViewHolder> {

    private ArrayList<Attendance> listItems;
    private Context mContext;
    // private OnItemClickListener mItemClickListener;

    public StockCheckAdapter() {
    }

    public StockCheckAdapter(Context context, ArrayList<Attendance> listItems) {
        this.listItems = listItems;
        this.mContext = context;

        // Log.d("listItems",listItems.toString());
    }

    @Override
    public StockCheckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock_check,parent,false);
        return new StockCheckViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StockCheckViewHolder holder, int position) {
        Attendance mAttendance = listItems.get(position);

        Log.d("(mProduct.getCode()","cc "+mAttendance.getCode());

        holder.tvCode.setText(mAttendance.getCode());

        String in_time = Validate.conver24to12Format(mAttendance.getInTime());
        String out_time =Validate.conver24to12Format(mAttendance.getOutTime());
        if(in_time!=null&& !in_time.isEmpty()){
            holder.tvInTimeLbl.setVisibility(View.VISIBLE);
        }else{
            holder.tvInTimeLbl.setVisibility(View.INVISIBLE);
        }
        if(out_time!=null&& !out_time.isEmpty()){
            holder.tvOutTimeLbl.setVisibility(View.VISIBLE);
        }else{
            holder.tvOutTimeLbl.setVisibility(View.INVISIBLE);
        }
        holder.tvInTime.setText(in_time);
        holder.tvOutTime.setText(out_time);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class StockCheckViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView tvInTime, tvOutTime, tvCode,tvInTimeLbl,tvOutTimeLbl;
        public StockCheckViewHolder(View view){
            super(view);
            this.tvCode = (TextView) view.findViewById(R.id.code);
            this.tvInTime = (TextView) view.findViewById(R.id.in_time);
            this.tvOutTime = (TextView) view.findViewById(R.id.out_time);
            this.tvInTimeLbl = (TextView) view.findViewById(R.id.inTimeLbl);
            this.tvOutTimeLbl = (TextView) view.findViewById(R.id.outTimeLbl);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Attendance mAttendance = listItems.get(getPosition());
            double InLatitude= mAttendance.getInLatitude();
            double InLongitude= mAttendance.getInLongitude();
            double OutLatitude= mAttendance.getOutLatitude();
            double OutLongitude = mAttendance.getOutLongitude();
            if(InLatitude>0 && InLongitude>0 || OutLatitude>0 && OutLongitude>0  ){
                Intent mIntent = new Intent(mContext,MapsActivity.class);
                mIntent.putExtra("InLatitude",InLatitude);
                mIntent.putExtra("InLongitude",InLongitude);
                mIntent.putExtra("OutLatitude",OutLatitude);
                mIntent.putExtra("OutLongitude",OutLongitude);
                mContext.startActivity(mIntent);
            }else{
                Toast.makeText(mContext,"No Location Found",Toast.LENGTH_SHORT).show();
            }
            /*if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }*/
        }
    }
}
