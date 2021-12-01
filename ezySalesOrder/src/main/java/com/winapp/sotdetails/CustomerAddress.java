package com.winapp.sotdetails;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.googlemaps.MapActivity;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.sot.WebServiceClass;

public class CustomerAddress extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace {

	SlidingMenu menu;
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	ImageButton search, newcustomerbutton;
	LinearLayout customeraddress_layout;
	TextView title;
	ListView customeraddress_list;
	ArrayList<HashMap<String, String>> customerOutletAddress = new ArrayList<HashMap<String, String>>();
	AddressAdapter Adapter;
	String customerCode="", customerName="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.customer_address);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.customer_list_title, null);
		title = (TextView) customNav.findViewById(R.id.customer_Title);
		search = (ImageButton) customNav.findViewById(R.id.search);
		newcustomerbutton = (ImageButton) customNav
				.findViewById(R.id.newcustomerbutton);

		search.setVisibility(View.GONE);
		newcustomerbutton.setVisibility(View.GONE);

		title.setText("Customer Address");

		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayHomeAsUpEnabled(false);
		ab.setDisplayShowTitleEnabled(false);
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		// menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);

		customeraddress_layout = (LinearLayout) findViewById(R.id.customeraddress_layout);
		customeraddress_list = (ListView) findViewById(R.id.customeraddress_list);

		
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			customerCode = extras.getString("CustomerCode");
			customerName = extras.getString("CustomerName");
			title.setText(""+customerName);
			
			GetOutletAddress task = new GetOutletAddress();
			task.execute();
			
		}else{
			Log.e("extras", "Extra NULL");
		}
		
		customeraddress_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
			
				HashMap<String, String> hash= new HashMap<String, String>();
				
				TextView customercode = (TextView) v.findViewById(R.id.customer_code);
				TextView customername = (TextView) v.findViewById(R.id.customer_name);
				TextView companyname = (TextView) v.findViewById(R.id.company_name);
				TextView address1 = (TextView) v.findViewById(R.id.customer_address1);
				TextView address2 = (TextView) v.findViewById(R.id.customer_address2);
				TextView lat = (TextView) v.findViewById(R.id.lat);
				TextView lng = (TextView) v.findViewById(R.id.lng);
				TextView DeliveryCode = (TextView) v.findViewById(R.id.deliverycode);
				
				hash.put("CustomerCode", customercode.getText().toString());
				hash.put("CustomerName", customername.getText().toString());
				hash.put("CompanyName", companyname.getText().toString());
				hash.put("Address1", address1.getText().toString());
				hash.put("Address2", address2.getText().toString());
				hash.put("DeliveryCode", DeliveryCode.getText().toString());
				hash.put("Latitude", lat.getText().toString());
				hash.put("Longitude", lng.getText().toString());
		
				Intent in = new Intent(CustomerAddress.this, MapActivity.class);
				in.putExtra("CustomerOutletAddress", hash);
				startActivity(in);
//				CustomerAddress.this.finish();
			}
		});

	}

	public class AddressAdapter extends BaseAdapter {

		ArrayList<HashMap<String,String>> data;
		Context context;
		LayoutInflater layoutInflater;

		public AddressAdapter(Context context, ArrayList<HashMap<String,String>> data) {
			super();
			this.data = data;
			this.context = context;
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {

			return data.size();
		}

		@Override
		public HashMap<String,String> getItem(int position) {

			return data.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			
			final HashMap<String,String> hashmap = data.get(position);
			final Holder holder;		
			if (convertView == null) {
				holder = new Holder();
				convertView = layoutInflater.inflate(R.layout.customeraddress_list_item, null);
				holder.customercode = (TextView) convertView.findViewById(R.id.customer_code);
				holder.customername = (TextView) convertView.findViewById(R.id.customer_name);
				holder.companyname = (TextView) convertView.findViewById(R.id.company_name);
				holder.address1 = (TextView) convertView.findViewById(R.id.customer_address1);
				holder.address2 = (TextView) convertView.findViewById(R.id.customer_address2);
				holder.lat = (TextView) convertView.findViewById(R.id.lat);
				holder.lng = (TextView) convertView.findViewById(R.id.lng);
				holder.deliverycode= (TextView) convertView.findViewById(R.id.deliverycode);
				convertView.setTag(holder);
				convertView.setId(position);
				
			} else {
				holder = (Holder) convertView.getTag();
			}
		
			holder.customercode.setText(hashmap.get("CustomerCode"));
			holder.customername.setText(hashmap.get("CustomerName"));
			holder.companyname.setText(hashmap.get("CompanyName"));
			holder.address1.setText(hashmap.get("Address1"));
			holder.address2.setText(hashmap.get("Address2"));
			holder.lat.setText(hashmap.get("Latitude"));
			holder.lng.setText(hashmap.get("Longitude"));
			holder.deliverycode.setText(hashmap.get("DeliveryCode"));

			return convertView;
		}

		public class Holder{
			TextView customername,customercode,companyname,address1,address2,lat,lng,deliverycode;
		}
		
	}
	
	public class GetOutletAddress extends AsyncTask<Void, Void, Void> {
		 HashMap<String, String> hm= new HashMap<String, String>();
		 JSONArray jsonarray = null;
			@Override
			protected void onPreExecute() {
				hm.clear();
				customerOutletAddress.clear();
				loadProgress();
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				try {
				String cmpnyCode = SupplierSetterGetter.getCompanyCode();
				
				hm.put("CompanyCode", cmpnyCode);
				hm.put("CustomerCode", customerCode);
							
				jsonarray = WebServiceClass.parameterWebservice(hm, "fncGetCustomerAddress");
				
				Log.d("jsonarray", ""+jsonarray.toString());
				
				if(jsonarray.length()>0){
				
					for(int i=0 ; i<jsonarray.length(); i++){
						JSONObject jsonobject;
						HashMap<String, String> hash= new HashMap<String, String>();
						
						jsonobject = jsonarray.getJSONObject(i);
						
						String CompanyName = jsonobject.optString("CompanyName")
								.toString();
						String Address1 = jsonobject.optString("Address1")
								.toString();	
						String Address2 = jsonobject.optString("Address2")
								.toString();	
						String Latitude = jsonobject.optString("Latitude")
								.toString();					
						String Longitude = jsonobject.optString("Longitude")
								.toString();
						String DeliveryCode = jsonobject.optString("DeliveryCode")
								.toString();
						
						hash.put("CompanyName", CompanyName);
						hash.put("Address1", Address1);
						hash.put("Address2", Address2);
						hash.put("CustomerName", customerName);
						hash.put("CustomerCode", customerCode);
						hash.put("Latitude", Latitude);
						hash.put("Longitude", Longitude);
						hash.put("DeliveryCode", DeliveryCode);					
						customerOutletAddress.add(hash);
						
					}
					
				}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				
				Log.d("customerOutletAddress", ""+customerOutletAddress.toString());
				
				if (customerOutletAddress.size()>0) {
					Adapter = new AddressAdapter(CustomerAddress.this, customerOutletAddress);
					customeraddress_list.setAdapter(Adapter);
					
				}else{
					Log.d("No matches"," found");
					Toast.makeText(CustomerAddress.this, "No data Found",
						       Toast.LENGTH_SHORT).show();
				}
	
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(customeraddress_layout, true);
			}
		}
	
	private void loadProgress(){
		spinnerLayout = new LinearLayout(CustomerAddress.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(customeraddress_layout, false);
		progressBar = new ProgressBar(CustomerAddress.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		spinnerLayout.addView(progressBar);
	}
	
	private void enableViews(View v, boolean enabled) {
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				enableViews(vg.getChildAt(i), enabled);
			}
		}
		v.setEnabled(enabled);
	}

	@Override
	public void onListItemClick(String item) {
		// TODO Auto-generated method stub
		menu.toggle();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
//		Intent i = new Intent(CustomerAddress.this, CustomerListActivity.class);
//		startActivity(i);
		CustomerAddress.this.finish();
	}
	
}
