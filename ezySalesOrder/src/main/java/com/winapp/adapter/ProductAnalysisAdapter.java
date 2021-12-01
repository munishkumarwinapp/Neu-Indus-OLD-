package com.winapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.model.Attendance;
import com.winapp.model.Product;

import java.util.ArrayList;

/**
 * Created by user on 20-Jul-17.
 */

public class ProductAnalysisAdapter extends RecyclerView.Adapter<ProductAnalysisAdapter.ViewHolder> {

    private ArrayList<Product> productItems;
    private Context mContext;
    private String tranType= "";
    public ProductAnalysisAdapter(Context context, ArrayList<Product> productItems,String tranType) {
        this.productItems = productItems;
        this.mContext = context;
        this.tranType = tranType;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_stock_analysis,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product sProduct = productItems.get(position);
        if(tranType.equalsIgnoreCase("Sales")){
        holder.sDate.setText(sProduct.getTranDate());
        holder.sQty.setText(sProduct.getSalesQty());
        holder.sTotal.setText(sProduct.getSalesSubTotal());
        holder.sCost.setText(sProduct.getCost());
        }else if(tranType.equalsIgnoreCase("Purchase")){
         holder.sDate.setText(sProduct.getTranDate());
         holder.sQty.setText(sProduct.getPurchaseQty());
         holder.sTotal.setText(sProduct.getPurchaseSubTotal());
         holder.sCost.setText(sProduct.getCost());
        }
    }

    @Override
    public int getItemCount() {
        return productItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView sDate,sQty,sTotal,sCost;
        public ViewHolder(View v){
            super(v);
            sDate = (TextView) v.findViewById(R.id.date);
            sQty = (TextView) v.findViewById(R.id.qty);
            sTotal = (TextView) v.findViewById(R.id.total);
            sCost = (TextView) v.findViewById(R.id.cost);
        }
    }
}
