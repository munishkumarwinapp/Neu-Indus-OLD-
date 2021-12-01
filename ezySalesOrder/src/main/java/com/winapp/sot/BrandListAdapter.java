package com.winapp.sot;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.model.Product;

import java.util.List;

/**
 * Created by USER on 12/12/2017.
 */

public class BrandListAdapter extends RecyclerView.Adapter<BrandListAdapter.ViewHolder> {

    private List<Product> brandList;
    private Activity activity;
    public int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public BrandListAdapter(Activity activity , List<Product> machineDataList) {
        this.activity = activity;
        this.brandList = machineDataList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand_data,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Product mdata = brandList.get(position);
        holder.productname.setText(mdata.getProductName());
        if(mdata.getProductCode().equals("")){
            holder.productcode.setText("-");
        }else{
            holder.productcode.setText(mdata.getProductCode());
        }
//        holder.layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("positioncheck", String.valueOf(position));
//                setPosition(position);
//                activity.openContextMenu(view);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView productname,productcode;
        public ViewHolder(View view){
            super(view);
            productname = (TextView) view.findViewById(R.id.product_name);
            productcode = (TextView) view.findViewById(R.id.product_code);

        }

    }
}


