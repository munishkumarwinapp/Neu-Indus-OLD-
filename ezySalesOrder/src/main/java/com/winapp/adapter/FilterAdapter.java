package com.winapp.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.model.Filter;


public class FilterAdapter extends BaseAdapter {
 
	// Declare Variables
	Context mContext;
	LayoutInflater inflater;
	private List<Filter> originalArraylist;
	private ArrayList<Filter> filterlist;
	private int selectedPosition = -1;
	private boolean flag = false;
	public FilterAdapter(Context context, List<Filter> filterArrayList) {
		
		mContext = context;
		this.originalArraylist = new ArrayList<Filter>();
		this.filterlist = new ArrayList<Filter>();
		originalArraylist.clear();
		filterlist.clear();
		this.originalArraylist = filterArrayList;
		this.filterlist.addAll(filterArrayList);
		inflater = LayoutInflater.from(mContext);
	}
 
	public class ViewHolder {
		TextView code;
		TextView description;
		CheckBox checkBox;
	}
 
	@Override
	public int getCount() {
		return originalArraylist.size();
	}
 
	@Override
	public Filter getItem(int position) {
		return originalArraylist.get(position);
	}
 
	@Override
	public long getItemId(int position) {
		return position;
	}
 
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		final Filter filter = originalArraylist.get(position);
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.filter_listitem, null);
			// Locate the TextViews in listview_item.xml
			holder.code = (TextView) view.findViewById(R.id.code);
			holder.description = (TextView) view.findViewById(R.id.description);
			holder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
			view.setTag(holder);	
		
			view.setId(position);
			
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.checkBox.setId(position);
		
//		
//		if(selectedPosition==position && flag){
//			Log.d("selected pos", "ss " + selectedPosition);
//			Log.d("position", "ss " + position);
//			Filter filters = originalArraylist.get(position);
//			if (holder.checkBox.isChecked()) {	
//				Log.d("selected pos", "isChecked" + selectedPosition);				
//				filters.setSelected(false);
//				selectedPosition = -1;
//			} else {
//				selectAll(false);
//				Log.d("selected pos", "unChecked " + selectedPosition);
//				filters.setSelected(true);				
//				selectedPosition = position;
//			}
//			
//			
//		}
		
		holder.checkBox.setChecked(filter.isSelected());
		
		// Set the results into TextViews
		holder.code.setText(filter.getCode());
		holder.description.setText(filter.getDescription());
		
		
		holder.checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = false;
				CheckBox checkBox = (CheckBox) v;
				Filter filter = originalArraylist.get(v.getId());
				if (checkBox.isChecked()) {
					selectAll(false);
					filter.setSelected(true);
					selectedPosition = v.getId();
				} else {
					int id = v.getId();
					filter.setSelected(false);
					selectedPosition = -1;
				}
				}
			});
		return view;
	}
	/*public boolean isAllSelected() {
		for (Filter filter : originalArraylist) {
			if (!filter.isSelected()) {
				return false;
			}
		}
		return true;
	}*/

	
	public void selectAll(boolean select) {
		for (Filter filter : filterlist) {
			filter.setSelected(select);
		}
		notifyDataSetChanged();
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition,boolean flag) {
		this.flag = flag;
		this.selectedPosition = selectedPosition;
		notifyDataSetChanged();
	}
	
	/** Checkbox selection from list **/
	public void setCheckPosition(int position) {
		this.selectedPosition = position;
		
		Filter filters = originalArraylist.get(selectedPosition);
		
		if(filters.isSelected()){
			Log.d("selected pos", "isChecked" + selectedPosition);				
			filters.setSelected(false);
			selectedPosition = -1;
		}else{
			selectAll(false);
			Log.d("selected pos", "unChecked " + selectedPosition);
			filters.setSelected(true);				
			selectedPosition = position;
		}
		
		notifyDataSetChanged();
	}
	
	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		originalArraylist.clear();
		if (charText.length() == 0) {
			originalArraylist.addAll(filterlist);
		} 
		else 
		{
			for (Filter fltr : filterlist) 
			{
				if ((fltr.getCode().toLowerCase(Locale.getDefault()).contains(charText))||(fltr.getDescription().toLowerCase(Locale.getDefault()).contains(charText)))
				{
					originalArraylist.add(fltr);
				}
			}
		}
		notifyDataSetChanged();
	}
 
}