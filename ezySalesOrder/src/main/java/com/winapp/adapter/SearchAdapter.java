package com.winapp.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.winapp.SFA.R;
import com.winapp.model.Product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {
 
	Context mContext;
	LayoutInflater inflater;
	private ArrayList<Product> originalArraylist;
	private List<Product> productList;	
	
	public SearchAdapter(Context context, List<Product> searchArrayList) {
		
		    mContext = context;
			this.originalArraylist = new ArrayList<Product>();
			this.productList = new ArrayList<Product>();
			originalArraylist.clear();
			productList.clear();
			this.productList = searchArrayList;
			this.originalArraylist.addAll(searchArrayList);   
		    inflater = LayoutInflater.from(mContext);		
	}
 
	public class ViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;
		TextView text4;
		TextView text5;
	}
 
	@Override
	public int getCount() {
		return productList.size();
	}
 
	@Override
	public Product getItem(int position) {
		return productList.get(position);
	}
 
	@Override
	public long getItemId(int position) {
		return position;
	}
 
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		final Product product = productList.get(position);
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.common_list_item, null);			
			holder.text1 = (TextView) view.findViewById(R.id.text1);
			holder.text2 = (TextView) view.findViewById(R.id.text2);
			holder.text3 = (TextView) view.findViewById(R.id.text3);
			holder.text4 = (TextView) view.findViewById(R.id.text4);
			holder.text5 = (TextView) view.findViewById(R.id.text5);		
			
			view.setTag(holder);	
		
			view.setId(position);
			
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		holder.text1.setText(product.getProductName());
		holder.text2.setVisibility(View.GONE);
		holder.text3.setVisibility(View.GONE);
		holder.text4.setVisibility(View.GONE);
		holder.text5.setVisibility(View.GONE);
		
			
		return view;
	}

	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		productList.clear();
		if (charText.length() == 0) {
			productList.addAll(originalArraylist);
		} 
		else 
		{
			for (Product fltr : originalArraylist) 
			{
				if ((fltr.getProductName().toLowerCase(Locale.getDefault()).contains(charText)))
				{
					productList.add(fltr);
				}
			}
		}
		notifyDataSetChanged();
	}
 
}