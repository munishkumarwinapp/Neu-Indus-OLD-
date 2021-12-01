package com.winapp.sotdetails;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SlideMenuFragment;

public class OutstandingSummary extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace {
	private SlidingMenu menu;
	private ProgressBar progressBar;
	private LinearLayout spinnerLayout, outstanding_parent;
	private ImageButton searchIcon, closeIcon;
	private ListView outstanding_list;
	
	private String valid_url="";
	private ArrayList<HashMap<String, String>> outstandingArraylist = new ArrayList<HashMap<String, String>>();
	private CustomAdapter customAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.activity_outstandingsummary);

		ActionBar ab = getSupportActionBar();
		ab.setIcon(android.R.color.transparent);
		ab.setHomeButtonEnabled(false);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Outstanding Summary");
		searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
		closeIcon = (ImageButton) customNav.findViewById(R.id.custcode_img);
		closeIcon.setImageResource(R.mipmap.batch_clear);
		
		searchIcon.setVisibility(View.GONE);
		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);

		ab.setDisplayHomeAsUpEnabled(false);
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);
		
		outstandingArraylist.clear();
		FWMSSettingsDatabase.init(OutstandingSummary.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SalesOrderWebService(valid_url);
		
		outstanding_parent = (LinearLayout) findViewById(R.id.outstanding_parent);
		outstanding_list = (ListView) findViewById(R.id.outstanding_list);
		
		CommonAsyncCall commonAsync = new CommonAsyncCall();
		commonAsync.execute();
		
		closeIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				 Intent i = new Intent(OutstandingSummary.this, CombinedChartActivity.class);
//				 startActivity(i);
				 OutstandingSummary.this.finish();
			}
		});
		
		outstanding_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				TextView code = (TextView) v .findViewById(R.id.text1);
				TextView name = (TextView) v .findViewById(R.id.text2);

				String customercode = code.getText().toString();
				String CustomerName = name.getText().toString();
				Intent in = new Intent(OutstandingSummary.this,OutstandingDetails.class);
				in.putExtra("CustomerCode", customercode);
				in.putExtra("CustomerName", CustomerName);
				startActivity(in);
//				OutstandingSummary.this.finish();
			}
		});
		
	}
	
	/***   AsyncTask Start    ***/
	
	private class CommonAsyncCall extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			outstandingArraylist.clear();	
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				
				outstandingArraylist = SalesOrderWebService.getOutstandingSummary("fncGetOutstandingSummary");
			
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("listArray Result", outstandingArraylist.toString());
			try {
				if (!outstandingArraylist.isEmpty()) {
					headerCustCode();
				}else{
					outstandingArraylist.clear();
					headerCustCode();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(outstanding_parent, true);
		}
	}
	
	public void headerCustCode() throws ParseException {
			customAdapter = new CustomAdapter(OutstandingSummary.this, outstandingArraylist);
			outstanding_list.setAdapter(customAdapter);
	}
	
	/***   AsyncTask End    ***/	
	
	
	
	/** Adapter Start   ***/
	
		public class CustomAdapter extends BaseAdapter {
		
		private ArrayList<HashMap<String, String>> listarray = new ArrayList<HashMap<String, String>>();
		LayoutInflater mInflater;
		Holder holder = new Holder();
		
		public CustomAdapter(Context context, ArrayList<HashMap<String, String>> mList) {
			listarray.clear();
			this.listarray = mList;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return listarray.size();
		}
		
		@Override
		public HashMap<String, String> getItem(int position) {
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
			HashMap<String, String> value;
			if (row == null) {
				holder = new Holder();
				row = mInflater.inflate(R.layout.common_list_item, null);
				holder.text1 = (TextView) row.findViewById(R.id.text1);
				holder.text2 = (TextView) row.findViewById(R.id.text2);
				holder.text3 = (TextView) row.findViewById(R.id.text3);
				holder.text4 = (TextView) row.findViewById(R.id.text4);
				holder.text5 = (TextView) row.findViewById(R.id.text5);
				row.setTag(holder);
			} else {
				holder = (Holder) row.getTag();
			}
		

			holder.text1.setVisibility(View.GONE);
			holder.text4.setVisibility(View.GONE);
			holder.text5.setVisibility(View.GONE);
			
			value = getItem(position);
			holder.text1.setText(value.get("Customercode"));
			holder.text2.setText(value.get("CustomerName"));
			holder.text3.setText(value.get("BalanceAmount"));
		
			if (position % 2 == 0) {
		
				row.setBackgroundResource(drawable.list_item_even_bg);
				holder.text1.setTextColor(Color.parseColor("#035994"));
				holder.text2.setTextColor(Color.parseColor("#035994"));
				holder.text3.setTextColor(Color.parseColor("#035994"));
				holder.text4.setTextColor(Color.parseColor("#035994"));
				holder.text5.setTextColor(Color.parseColor("#035994"));
		
			} else {
		
				row.setBackgroundResource(drawable.list_item_odd_bg);
				holder.text1.setTextColor(Color.parseColor("#646464"));
				holder.text2.setTextColor(Color.parseColor("#646464"));
				holder.text3.setTextColor(Color.parseColor("#646464"));
				holder.text4.setTextColor(Color.parseColor("#646464"));
				holder.text5.setTextColor(Color.parseColor("#646464"));
			}
		
			return row;
		}
		
		final class Holder {
		
			TextView text1, text2, text3, text4, text5;
		
		}
			}


/** Adapter End   **/
	
	
	public void loadprogress(){
		spinnerLayout = new LinearLayout(OutstandingSummary.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(outstanding_parent, false);
		progressBar = new ProgressBar(OutstandingSummary.this);
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
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(String item) {
		// TODO Auto-generated method stub
		menu.toggle();
	}

	@Override
	public void onBackPressed() {		
//		 Intent i = new Intent(OutstandingSummary.this, CombinedChartActivity.class);
//		 startActivity(i);
		 OutstandingSummary.this.finish();
	}

}
