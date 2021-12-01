package com.winapp.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.helper.Catalog;
import com.winapp.sotdetails.SOTdetailsGetSet;

public class CustomerListCustomAdapter extends BaseAdapter implements
		Filterable {

	private ArrayList<SOTdetailsGetSet> listarray = new ArrayList<SOTdetailsGetSet>();
	private ArrayList<SOTdetailsGetSet> mOriginalValues = new ArrayList<SOTdetailsGetSet>();
	LayoutInflater mInflater;
	CustomHolder holder = new CustomHolder();
	SOTdetailsGetSet user;
	private Filter sampleFilter;

	public CustomerListCustomAdapter(Activity context,
			ArrayList<SOTdetailsGetSet> productsList) {
		listarray.clear();
		mOriginalValues.clear();
		this.listarray = productsList;
		this.mOriginalValues = productsList;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listarray.size();
	}

	@Override
	public SOTdetailsGetSet getItem(int position) {
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

		user = getItem(position);
		holder.custCode.setText(user.getCust_Code());
		holder.custName.setText(user.getCust_Name());

		holder.custName.setTag(position);

		if (Catalog.getCustomerCode() != null
				&& !Catalog.getCustomerCode().isEmpty()) {

			if (Catalog.getCustomerCode().matches(user.getCust_Code())) {
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

	public void resetData() {
		listarray = mOriginalValues;
	}

	@Override
	public Filter getFilter() {
		if (sampleFilter == null)
			sampleFilter = new SampleFilter();

		return sampleFilter;
	}

	private class SampleFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			// We implement here the filter logic
			if (constraint == null || constraint.length() == 0) {
				// No filter implemented we return all the list
				results.values = mOriginalValues;
				results.count = mOriginalValues.size();
			} else {
				// We perform filtering operation
				ArrayList<SOTdetailsGetSet> FilteredArrList = new ArrayList<SOTdetailsGetSet>();
				constraint = constraint.toString().toLowerCase();
				for (int i = 0; i < mOriginalValues.size(); i++) {
					String data = mOriginalValues.get(i).getCust_Name();
					String data1 = mOriginalValues.get(i).getCust_Code();
					if (data.toLowerCase().contains(constraint.toString())
							|| data1.toLowerCase().contains(
									constraint.toString())) {
						FilteredArrList.add(new SOTdetailsGetSet(
								mOriginalValues.get(i).getCust_Code(),
								mOriginalValues.get(i).getCust_Name()));
					}

				}
				// set the Filtered result to return
				results.count = FilteredArrList.size();
				results.values = FilteredArrList;

			}
			return results;
		}

		@Override
		@SuppressWarnings("unchecked")
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			if (results.count == 0) {
				listarray = (ArrayList<SOTdetailsGetSet>) results.values;
				notifyDataSetChanged();
			} else {
				listarray = (ArrayList<SOTdetailsGetSet>) results.values;
				notifyDataSetChanged();
			}

		}
	}
}
