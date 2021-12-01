package com.winapp.fwms;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winapp.SFA.R;

public class SupplyListAdapter extends BaseAdapter {
	Activity context;
	String codes[];
	String suppliers[];
	String supplydates[];

	public SupplyListAdapter(Activity context, String[] codes,
			String[] suppliers, String[] supplydates) {
		super();
		this.context = context;
		this.codes = codes;
		this.suppliers = suppliers;
		this.supplydates = supplydates;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return suppliers.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	private class ViewHolder {
		TextView suppliercode;
		TextView suppliername;
		TextView supplierdates;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.addproduct_list_item, null);
			holder = new ViewHolder();
			holder.suppliername = (TextView) convertView
					.findViewById(R.id.productname);
			holder.suppliercode = (TextView) convertView
					.findViewById(R.id.productcode);
			holder.supplierdates = (TextView) convertView
					.findViewById(R.id.productweight);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.suppliername.setText(codes[position]);
		holder.suppliercode.setText(suppliers[position]);
		holder.supplierdates.setText(supplydates[position]);

		return convertView;
	}

}
