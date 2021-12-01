package com.winapp.adapter;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.helper.Catalog;
import com.winapp.model.Customer;

public class CustomerAdapter extends BaseAdapter {

	private ArrayList<Customer> listarray = new ArrayList<Customer>();
	private ArrayList<Customer> mOriginalValues = new ArrayList<Customer>();
	LayoutInflater mInflater;
	CustomHolder holder = new CustomHolder();
	Customer customer;
	public CustomerAdapter() {
		
	}

	public CustomerAdapter(Activity context,
			ArrayList<Customer> productsList) {
		listarray.clear();
		mOriginalValues.clear();
		this.listarray = productsList;
		this.mOriginalValues.addAll(productsList);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listarray.size();
	}

	@Override
	public Customer getItem(int position) {
		return listarray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listarray.get(position).hashCode();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View row = convertView;
		holder = null;

		if (row == null) {
			row = mInflater.inflate(R.layout.customer_list_item, null);

			holder = new CustomHolder();
			holder.custCode = (TextView) row.findViewById(R.id.in_cust_code);
			holder.custName = (TextView) row.findViewById(R.id.in_cust_name);
			row.setTag(holder);
		} else {
			holder = (CustomHolder) row.getTag();
		}

		customer = getItem(position);
		holder.custCode.setText(customer.getCustomerCode());
		holder.custName.setText(customer.getCustomerName());

		holder.custName.setTag(position);

		if (Catalog.getCustomerCode() != null
				&& !Catalog.getCustomerCode().isEmpty()) {

			if (Catalog.getCustomerCode().matches(customer.getCustomerCode())) {
				row.setBackgroundResource(R.color.list_item_bg_selected);
			} else {

				if (position % 2 == 0) {

					row.setBackgroundResource(R.drawable.list_item_even_bg);
					holder.custCode.setTextColor(Color.parseColor("#035994"));
					holder.custName.setTextColor(Color.parseColor("#035994"));

				} else {

					row.setBackgroundResource(R.drawable.list_item_odd_bg);
					holder.custCode.setTextColor(Color.parseColor("#646464"));
					holder.custName.setTextColor(Color.parseColor("#646464"));
				}

			}
		} else {
			if (position % 2 == 0) {

				row.setBackgroundResource(R.drawable.list_item_even_bg);
				holder.custCode.setTextColor(Color.parseColor("#035994"));
				holder.custName.setTextColor(Color.parseColor("#035994"));

			} else {

				row.setBackgroundResource(R.drawable.list_item_odd_bg);
				holder.custCode.setTextColor(Color.parseColor("#646464"));
				holder.custName.setTextColor(Color.parseColor("#646464"));
			}
		}

		return row;
	}

	final class CustomHolder {

		TextView custCode;
		TextView custName;

	}
	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		listarray.clear();
		if (charText.length() == 0) {
			listarray.addAll(mOriginalValues);
		} 
		else 
		{
			for (Customer customer : mOriginalValues) 
			{
				if ((customer.getCustomerCode().toLowerCase(Locale.getDefault()).contains(charText))||(customer.getCustomerName().toLowerCase(Locale.getDefault()).contains(charText)))
				{
					listarray.add(customer);
				}
			}
		}
		notifyDataSetChanged();
	}
}
