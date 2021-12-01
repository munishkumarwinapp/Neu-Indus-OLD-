package com.winapp.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.winapp.SFA.R;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesAddProduct;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProductSearchAdapter extends BaseAdapter {
 
	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<HashMap<String,String>> originalArraylist;
	private ArrayList<HashMap<String,String>> productList;	
	
	public ProductSearchAdapter(Context context, ArrayList<HashMap<String,String>> searchArrayList) {
			this.originalArraylist = new ArrayList<HashMap<String,String>>();
			this.productList = new  ArrayList<HashMap<String,String>>();			
			this.mContext = context;
			this.productList = searchArrayList;
			this.originalArraylist.addAll(searchArrayList);   
		    inflater = LayoutInflater.from(mContext);		
	}
 
	public class ViewHolder {
		TextView txt_code;
		TextView txt_name;
		TextView txt_price;
		TextView txt_qty;
	}	
	@Override
	public int getCount() {
		return productList.size();
	}
 
	@Override
	public  Object getItem(int position) {
		return productList.get(position);
	}
 
	@Override
	public long getItemId(int position) {
		return position;
	}
 
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		final HashMap<String,String> product = productList.get(position);
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.sale_productitem, null);			
			holder.txt_code = (TextView) view.findViewById(R.id.txt_code);
			holder.txt_name = (TextView) view.findViewById(R.id.txt_name);
			holder.txt_price = (TextView) view.findViewById(R.id.txt_price);
			holder.txt_qty = (TextView) view.findViewById(R.id.txt_qty);
			view.setTag(holder);		
			view.setId(position);			
		} else {
			holder = (ViewHolder) view.getTag();
		}		
		holder.txt_code.setText(product.get("ProductCode"));
		holder.txt_name.setText(product.get("ProductName"));
		holder.txt_price.setText(product.get("WholeSalePrice"));		
		if(mContext instanceof SalesAddProduct){
		    /******** Amount will Gone or Visible Based on Hide Price From GetUserPermission  *********/
	        if(FormSetterGetter.getHidePrice()!=null && !FormSetterGetter.getHidePrice().isEmpty()){	
				if(FormSetterGetter.getHidePrice().matches("Hide Price")){		
					holder.txt_price.setVisibility(View.GONE);
				}else{
					holder.txt_price.setVisibility(View.VISIBLE);
				}
	        }else{
	        	holder.txt_price.setVisibility(View.VISIBLE);
	        }
	        holder.txt_code.setVisibility(View.GONE);
	        holder.txt_qty.setVisibility(View.VISIBLE);
		}
		
		String pcode = product.get("ProductCode");
		String db_pcode = SOTDatabase.getProductCode(pcode);
		if (position % 2 == 0) {
			
			if(db_pcode != null && !db_pcode.isEmpty()){
				
				if(pcode.matches(db_pcode)){
					double pqty = SOTDatabase.getProductQty(db_pcode);
					int iqty = (int)pqty;
					holder.txt_qty.setText(iqty+"");
					view.setBackgroundResource(R.drawable.list_item_selected_bg);
						
				}
				else{
					view.setBackgroundResource(R.drawable.list_item_even_bg);
					holder.txt_qty.setText(product.get("Qty"));
				}
				
			}else{
				view.setBackgroundResource(R.drawable.list_item_even_bg);
				holder.txt_qty.setText(product.get("Qty"));
			}
			holder.txt_code.setTextColor(Color.parseColor("#035994"));
			holder.txt_name.setTextColor(Color.parseColor("#035994"));
			holder.txt_price.setTextColor(Color.parseColor("#035994"));
			holder.txt_qty.setTextColor(Color.parseColor("#035994"));
		} else {			
			if(db_pcode != null && !db_pcode.isEmpty()){				
				if(pcode.matches(db_pcode)){
					double pqty = SOTDatabase.getProductQty(db_pcode);
					int iqty = (int)pqty;
					holder.txt_qty.setText(iqty+"");
					view.setBackgroundResource(R.drawable.list_item_selected_bg);	
				}else{
					holder.txt_qty.setText(product.get("Qty"));
					view.setBackgroundResource(R.drawable.list_item_odd_bg);
				}
			}else{
				holder.txt_qty.setText(product.get("Qty"));
				view.setBackgroundResource(R.drawable.list_item_odd_bg);
			}

			holder.txt_code.setTextColor(Color.parseColor("#646464"));
			holder.txt_name.setTextColor(Color.parseColor("#646464"));
			holder.txt_price.setTextColor(Color.parseColor("#646464"));
			holder.txt_qty.setTextColor(Color.parseColor("#035994"));
		}
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
			for (int i=0;i<originalArraylist.size();i++) 
			{
				String ProductCode = originalArraylist.get(i).get("ProductCode");
				String ProductName = originalArraylist.get(i).get("ProductName");
				String WholeSalePrice = originalArraylist.get(i).get("WholeSalePrice");
				String Qty = originalArraylist.get(i).get("Qty");
				if ((ProductName.toLowerCase(Locale.getDefault()).contains(charText))||(ProductCode.toLowerCase(Locale.getDefault()).contains(charText)))
				{
					HashMap<String,String> hm = new HashMap<String, String>();
					hm.put("ProductCode", ProductCode);
					hm.put("ProductName", ProductName);
					hm.put("WholeSalePrice", WholeSalePrice);
					hm.put("Qty", Qty);					
					productList.add(hm);
				}
			}
		}
		notifyDataSetChanged();
	}
 
}