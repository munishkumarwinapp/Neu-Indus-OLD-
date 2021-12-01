package com.winapp.fwms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winapp.SFA.R;
import com.winapp.sot.SalesOrderSetGet;

public class CustomAlertAdapterProd extends BaseAdapter {

	Context ctx;
	String datavalue;
	ArrayList<HashMap<String, String>> listarray = new ArrayList<HashMap<String, String>>();
	private LayoutInflater mInflater = null;

	public CustomAlertAdapterProd(Activity activty,
			ArrayList<HashMap<String, String>> al) {

		this.listarray.clear();
		this.ctx = activty;
		mInflater = activty.getLayoutInflater();
		this.listarray = al;

	}

	@Override
	public int getCount() {

		return listarray.size();
	}

	@Override
	public Object getItem(int position) {
		return listarray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.alertlistrow, null);
			holder.titlename = (TextView) convertView
					.findViewById(R.id.textView_titllename);
			holder.titlevalue = (TextView) convertView
					.findViewById(R.id.textView_titlevalue);
			holder.textView_reflocation = (TextView) convertView
					.findViewById(R.id.textView_reflocation);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String mobileShowCodeOnSearch = SalesOrderSetGet.getMobileShowCodeOnSearch();

		if(mobileShowCodeOnSearch!=null && !mobileShowCodeOnSearch.matches("")) {
			if (mobileShowCodeOnSearch.matches("1")) {
				holder.textView_reflocation.setVisibility(View.GONE);
				holder.titlename.setVisibility(View.VISIBLE);
				holder.titlevalue.setVisibility(View.VISIBLE);
			}else{
				holder.textView_reflocation.setVisibility(View.VISIBLE);
				holder.titlename.setVisibility(View.GONE);
				holder.titlevalue.setVisibility(View.GONE);
			}
		}else{
			holder.textView_reflocation.setVisibility(View.VISIBLE);
			holder.titlename.setVisibility(View.GONE);
			holder.titlevalue.setVisibility(View.GONE);
		}


		
		HashMap<String, String> datavalue = listarray.get(position);
		Set<Entry<String, String>> keys = datavalue.entrySet();
		Iterator<Entry<String, String>> iterator = keys.iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Entry mapEntry = iterator.next();
			String keyValue = (String) mapEntry.getKey();
			String value = (String) mapEntry.getValue();
			
			holder.titlename.setText(keyValue);
			holder.titlevalue.setText(value);
			holder.textView_reflocation.setText(value);

		}
		return convertView;
	}

	static class ViewHolder {
		TextView titlename;
		TextView titlevalue;
		TextView textView_reflocation;
	}

	public ArrayList<HashMap<String, String>> getArrayList() {
		ArrayList<HashMap<String, String>> getAl = new ArrayList<HashMap<String, String>>();

		for (int i = 0; i < listarray.size(); i++) {
			HashMap<String, String> hashmaps = new HashMap<String, String>();
			HashMap<String, String> datavalue = listarray.get(i);
			Set<Entry<String, String>> keys = datavalue.entrySet();
			Iterator<Entry<String, String>> iterator = keys.iterator();
			while (iterator.hasNext()) {
				@SuppressWarnings("rawtypes")
				Entry mapEntry = iterator.next();
				String keyValue = (String) mapEntry.getKey();
				String value = (String) mapEntry.getValue();
				hashmaps.put(keyValue, value);
				getAl.add(hashmaps);

			}

		}

		return getAl;

	}

}