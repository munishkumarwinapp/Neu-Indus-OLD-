package com.winapp.sot;

import java.util.ArrayList;
import java.util.List;

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

import com.winapp.catalog.SOCatalogActivity;
import com.winapp.SFA.R;

public class GraAdapter extends BaseAdapter {
	private List<SO> mData = new ArrayList<>();
	private int mResource;
	private LayoutInflater mInflater;
	private Context mContext;

	public GraAdapter(Context context, int resource, List<SO> data) {
		mData.clear();
		mContext = context;
		mData = data;
		mResource = resource;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mData.size();
	}
 
	@Override
	public SO getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		Log.d("Size ", ""+mData.size());
		final SO so = mData.get(position);
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(mResource, parent, false);
			holder.sno = (TextView) convertView.findViewById(R.id.sno);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.custcode = (TextView) convertView
					.findViewById(R.id.custcode);
			holder.amount = (TextView) convertView.findViewById(R.id.amount);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			holder.customeraddresslayout = (LinearLayout) convertView.findViewById(R.id.customeraddresslayout);
			holder.customeraddresslayout.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.sno.setText(so.getSno());
		holder.date.setText(so.getDate());
		if (mContext instanceof StockRequestHeader) {
			holder.custcode.setText(so.getFromlocation());
			holder.amount.setText(so.getTolocation());
		}else if (mContext instanceof SOCatalogActivity) {
			holder.custcode.setText(so.getCustomerName());
			holder.amount.setText(so.getNettotal());
		}
		else {
			holder.custcode.setText(so.getCustomerName());
			holder.amount.setText(so.getNettotal());
		}


		if(mContext instanceof GraHeader){
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
			holder.balance_amount = (TextView) convertView.findViewById(R.id.balance_amount);

			holder.checkbox.setVisibility(View.INVISIBLE);
			holder.balance_amount.setVisibility(View.INVISIBLE);

			String stat = so.getStatus();
			double bal_amt =so.getBalance_amt();
			String nt_tot =so.getNettotal();
			double netTotal =Double.parseDouble(nt_tot);

			Log.d("netTotal","-->"+netTotal+"bal"+bal_amt);

			if(bal_amt==netTotal){
				stat = "Not Paid";
				holder.status.setTextColor(Color
						.parseColor("#F34945"));
			}else if(bal_amt<netTotal){
				stat = "Partially Paid";
				holder.status.setTextColor(Color
						.parseColor("#FF7F50"));
			}else {
				stat = "Paid";
				holder.status.setTextColor(Color
						.parseColor("#2BDF73"));
			}
			holder.status.setText(stat);

//			if(stat!=null && !so.getStatus().isEmpty()){
//
//				if(stat.matches("0")){
//					stat = "Not Paid";
//					holder.status.setTextColor(Color
//							.parseColor("#F34945"));
//				}else if(stat.matches("1")){
//					stat = "Paid";
//					holder.status.setTextColor(Color
//							.parseColor("#2BDF73"));
//				}else{
//					stat = "";
//				}
//
//			}else{
//				stat = "";
//			}


//			holder.custcode.setText(so.getCustomerName());

			convertView
						.setBackgroundResource(R.drawable.list_grey_bg);
		}else{
			if(position % 2 == 0){

				convertView.setBackgroundResource(R.drawable.list_item_even_bg);

				holder.sno.setTextColor(Color.parseColor("#035994"));
				holder.date.setTextColor(Color.parseColor("#035994"));
				holder.custcode.setTextColor(Color.parseColor("#035994"));
				holder.amount.setTextColor(Color.parseColor("#035994"));
				holder.status.setTextColor(Color.parseColor("#035994"));

			}else {

				convertView.setBackgroundResource(R.drawable.list_item_odd_bg);
				holder.sno.setTextColor(Color.parseColor("#646464"));
				holder.date.setTextColor(Color.parseColor("#646464"));
				holder.custcode.setTextColor(Color.parseColor("#646464"));
				holder.amount.setTextColor(Color.parseColor("#646464"));
				holder.status.setTextColor(Color.parseColor("#646464"));
			}

			holder.status.setText(so.getStatus());

		}


		return convertView;
	}

	class ViewHolder {
		TextView sno;
		TextView date;
		TextView custcode;
		TextView amount;
		TextView status;
		CheckBox checkbox;
		TextView balance_amount;
		LinearLayout customeraddresslayout;
	}
}
