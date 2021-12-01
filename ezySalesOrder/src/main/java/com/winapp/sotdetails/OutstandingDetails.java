package com.winapp.sotdetails;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SlideMenuFragment;

public class OutstandingDetails extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace {
	
	private SlidingMenu menu;
	private ProgressBar progressBar;
	private LinearLayout spinnerLayout, outstanding_parent;
	private ImageButton searchIcon, closeIcon;
	private ListView outstanding_detail_list;
	
	private OfflineSettingsManager spManager;
	private ArrayList<Integer> mColor = new ArrayList<Integer>();
	private ArrayList<HashMap<String, String>> detailsArraylist = new ArrayList<HashMap<String, String>>();
	private String valid_url="", companyCode="", customerCode ="", customerName ="";
	private CustomAdapter customAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.activity_outstandingdetail);
		
		ActionBar ab = getSupportActionBar();
		ab.setIcon(android.R.color.transparent);
		ab.setHomeButtonEnabled(false);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Outstanding Details");
		searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
		closeIcon = (ImageButton) customNav.findViewById(R.id.custcode_img);
		searchIcon.setVisibility(View.GONE);
		closeIcon.setImageResource(R.mipmap.batch_clear);
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
		
		detailsArraylist.clear();
		FWMSSettingsDatabase.init(OutstandingDetails.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SalesOrderWebService(valid_url);
		spManager = new OfflineSettingsManager(OutstandingDetails.this);
		companyCode = spManager.getCompanyType();
		
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Log.e("extras", "Extra NULL");
		} else {
			customerCode = extras.getString("CustomerCode");
			customerName = extras.getString("CustomerName");
		}
		
		outstanding_parent = (LinearLayout) findViewById(R.id.outstanding_parent);
		outstanding_detail_list = (ListView) findViewById(R.id.outstanding_detail_list);
		
		  mColor.clear();
		  mColor.add(R.color.list_1);
		  mColor.add(R.color.list_2);
		  mColor.add(R.color.list_3);
		  mColor.add(R.color.list_4);
		
		  closeIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					 Intent i = new Intent(OutstandingDetails.this, OutstandingSummary.class);
//					 startActivity(i);
					 OutstandingDetails.this.finish();
				}
			});
		  
		 commonAsyncCall commonAsync = new commonAsyncCall();
		commonAsync.execute();
	}
	
/** AsyncTask Strat**/
	
	public class commonAsyncCall extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			detailsArraylist.clear();
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("CompanyCode", companyCode);
			hm.put("CustomerCode", customerCode);
			
			detailsArraylist = SalesOrderWebService.getOutstandingDetails(hm, "fncGetOutstandingDetail");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			Log.d("detailsArraylist Result", detailsArraylist.toString());
			
			if (!detailsArraylist.isEmpty()) {
				try {
					headerCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				detailsArraylist.clear();
				try {
					headerCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Toast.makeText(OutstandingDetails.this, "No matches found",Toast.LENGTH_SHORT).show();
			}
			
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(outstanding_parent, true);
		}
	}
	
	public void headerCustCode() throws ParseException {
		customAdapter = new CustomAdapter(OutstandingDetails.this, R.layout.overdue_list_item, detailsArraylist);
		outstanding_detail_list.setAdapter(customAdapter);
	} 
	
	/***   AsyncTask End    ***/	
	
	
	
	/** Adapter Start   ***/
	
		public class CustomAdapter extends BaseAdapter {
			
			private Context ctx;
			private ArrayList<HashMap<String, String>> listarray = new ArrayList<HashMap<String, String>>();
			private LayoutInflater mInflater = null;
			int resource;
		
		public CustomAdapter(Activity activty, int resource, ArrayList<HashMap<String, String>> list) {
			this.listarray.clear();
			if(!list.isEmpty()){
				this.listarray = list;
			}	
			this.resource = resource;
			mInflater = activty.getLayoutInflater();
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
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final HashMap<String, String> so = listarray.get(position);
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(resource, null);
				holder.so_custcode = (TextView) convertView.findViewById(R.id.customer_code);
				holder.so_custname = (TextView) convertView.findViewById(R.id.customer_name);
				holder.so_invno = (TextView) convertView.findViewById(R.id.invoice_no);
				holder.so_invdate = (TextView) convertView.findViewById(R.id.invoice_date);
				holder.so_balamount = (TextView) convertView.findViewById(R.id.overdue_amount);
				holder.so_pending = (TextView) convertView.findViewById(R.id.pending);
				holder.slide_img = (ImageView) convertView.findViewById(R.id.slide_img);
				convertView.setTag(holder);
				convertView.setId(position);
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.so_custname.setVisibility(View.GONE);
			holder.so_invno.setText(so.get("InvoiceNo"));
			holder.so_invdate.setText(so.get("InvoiceDate"));
			holder.so_balamount.setText(so.get("BalanceAmount"));
			holder.so_pending.setText(so.get("OverdueDays"));
			
			
			int count = mColor.size();
			   
			   if(position>=mColor.size()){
			    int pos = position % count ;
			   
			    int color = mColor.get(pos);
			   
			    holder.slide_img.setBackgroundResource(color);
			   }else{
			   
			    int color = mColor.get(position);
			   
			    holder.slide_img.setBackgroundResource(color);
			   }
			
			return convertView;
		}

		class ViewHolder {
			TextView so_custcode;
			TextView so_custname;
			TextView so_invno;
			TextView so_invdate;
			TextView so_balamount;
			TextView so_pending;
			ImageView slide_img;
		}

	}

/** Adapter End   **/
	
	public void loadprogress(){
		spinnerLayout = new LinearLayout(OutstandingDetails.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(outstanding_parent, false);
		progressBar = new ProgressBar(OutstandingDetails.this);
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
//		 Intent i = new Intent(OutstandingDetails.this, OutstandingSummary.class);
//		 startActivity(i);
		 OutstandingDetails.this.finish();
	}

}
