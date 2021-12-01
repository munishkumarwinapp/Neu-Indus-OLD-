package com.winapp.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.sot.SalesOrderSetGet;

public class PopWindowAdapter extends BaseAdapter {

	ArrayList<HashMap<String, String>> soDetailsArr;
	Context mContext;
	private LayoutInflater mInflater = null;
	String cartonOrLoose="";
	public PopWindowAdapter(Activity activty,
			ArrayList<HashMap<String, String>> list) {
		this.mContext = activty;
		mInflater = activty.getLayoutInflater();
		this.soDetailsArr = list;
		cartonOrLoose = SalesOrderSetGet.getCartonpriceflag();
	}

	@Override
	public int getCount() {

		return soDetailsArr.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.popup_list_item, null);

			holder.popup_qty_layout = (LinearLayout) convertView.findViewById(R.id.popup_qty_layout);
		    holder.popup_cqty_layout = (LinearLayout) convertView.findViewById(R.id.popup_cqty_layout);
			holder.popup_lqty_layout = (LinearLayout) convertView.findViewById(R.id.popup_lqty_layout);
			
			holder.lblSno = (TextView) convertView.findViewById(R.id.lblSno);
			holder.lblName = (TextView) convertView.findViewById(R.id.lblName);
			holder.lblRemark = (TextView) convertView
					.findViewById(R.id.lblRemark);
			holder.lblqty = (TextView) convertView.findViewById(R.id.lblqty);
			holder.lblCqty = (TextView) convertView.findViewById(R.id.lblCqty);
			holder.lblLqty = (TextView) convertView.findViewById(R.id.lblLqty);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView.setTag(holder);
		
		if (!cartonOrLoose.matches("")) {
			   if (cartonOrLoose.matches("1")) {
			    holder.popup_qty_layout.setVisibility(View.GONE);
			    holder.popup_cqty_layout.setVisibility(View.VISIBLE);
			    holder.popup_lqty_layout.setVisibility(View.VISIBLE);
			   }
			   if (cartonOrLoose.matches("0")) {
			    holder.popup_qty_layout.setVisibility(View.VISIBLE);
			    holder.popup_cqty_layout.setVisibility(View.GONE);
			    holder.popup_lqty_layout.setVisibility(View.GONE);
			   }
			  }
		
		String qty=soDetailsArr.get(position).get("Qty");
		String cqty =soDetailsArr.get(position).get("CQty");
		String lqty= soDetailsArr.get(position).get("LQty");
		
		Double d = 5.25;
		Integer i = d.intValue();
		
		int Qty=0,CQty=0,LQty=0;
		if(!qty.matches("")){
			Double quantity = new Double(qty);
			Qty = quantity.intValue();
		}
		
		if(!cqty.matches("")){
			Double carton_quantity = new Double(cqty);
			CQty = carton_quantity.intValue();
		}
		
		if(!lqty.matches("")){
			Double loose_quantity = new Double(lqty);
			LQty = loose_quantity.intValue();
		}
		
		holder.lblSno.setText(soDetailsArr.get(position).get("slNo"));
		holder.lblName.setText(soDetailsArr.get(position).get("ProductName"));
		holder.lblRemark.setText(soDetailsArr.get(position).get("ItemRemarks"));
		holder.lblqty.setText(""+Qty);
		holder.lblCqty.setText(""+CQty);
		holder.lblLqty.setText(""+LQty);

		return convertView;

	}

	class ViewHolder {

		TextView lblSno, lblName, lblRemark, lblqty, lblCqty, lblLqty;
		LinearLayout popup_qty_layout, popup_cqty_layout, popup_lqty_layout;
	}
}
