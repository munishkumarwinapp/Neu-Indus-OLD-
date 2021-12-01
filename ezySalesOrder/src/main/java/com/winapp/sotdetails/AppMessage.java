package com.winapp.sotdetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import android.app.Activity;
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
import android.widget.ImageView;
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
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SlideMenuFragment;

public class AppMessage extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace {
	private SlidingMenu menu;
	private ProgressBar progressBar;
	private LinearLayout spinnerLayout, message_parent;
	private ImageButton search_icon, add_icon;
	private ListView message_list;
	private String valid_url = "", companyCode="";
	private MessageAdapter messageAdapter;
	private ArrayList<HashMap<String, String>> msgArrList = new ArrayList<HashMap<String, String>>();
	private OfflineSettingsManager spManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.activity_appmessage);
		
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Internal Message");
		search_icon = (ImageButton) customNav.findViewById(R.id.search_img);
		add_icon = (ImageButton) customNav.findViewById(R.id.custcode_img);
		search_icon.setVisibility(View.INVISIBLE);
		add_icon.setVisibility(View.GONE);

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
		
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,AppMessage.this);
		new SalesOrderWebService(valid_url);
		spManager = new OfflineSettingsManager(AppMessage.this);
		companyCode = spManager.getCompanyType();
		
		message_parent = (LinearLayout) findViewById(R.id.message_parent);
		message_list = (ListView) findViewById(R.id.message_list);
		
		AppMessageAsyncCall common = new AppMessageAsyncCall();
		common.execute();	
		
		message_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				
					Intent in = new Intent(AppMessage.this,  AppMessageDetail.class);
					in.putExtra("msgArrList", msgArrList);
					in.putExtra("select_position", position);
					startActivity(in);
					AppMessage.this.finish();
					
			}
		});
		
	}
	
	 /** AsyncTask Strat  **/
	
	private class AppMessageAsyncCall extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			loadprogress();
			msgArrList.clear();		
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				
				String loginUsername = SupplierSetterGetter.getUsername();
	
				HashMap<String, String> hmvalue = new HashMap<String, String>();
				hmvalue.put("CompanyCode", companyCode);
				hmvalue.put("UserName", loginUsername);
				
				msgArrList = SalesOrderWebService.getAppMessage(hmvalue, "fncGetAppMessage");
			
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("Array Result", msgArrList.toString());
			
			try {
				headerCustCode();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(message_parent, true);
		}
	}
	
	public void headerCustCode() throws ParseException {
		
		messageAdapter = new MessageAdapter(AppMessage.this, R.layout.message_list_item, msgArrList);
		message_list.setAdapter(messageAdapter);
	
}
	
	 /** AsyncTask End**/
	
/** Adapter Class Start  **/
	
	public class MessageAdapter extends BaseAdapter {
		Context ctx;
		ArrayList<HashMap<String, String>> listarray = new ArrayList<HashMap<String, String>>();
		private LayoutInflater mInflater = null;
		int resource;

		public MessageAdapter(Activity activty, int resource, ArrayList<HashMap<String, String>> list) {
			this.listarray.clear();
			this.ctx = activty;
			if(!list.isEmpty()){
				this.listarray = list;
			}	
			this.resource = resource;
			mInflater = activty.getLayoutInflater();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listarray.size();
		}

		@Override
		public HashMap<String, String> getItem(int position) {
			// TODO Auto-generated method stub
			return listarray.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final HashMap<String, String> so = listarray.get(position);
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(resource, null);
				holder.list_linear = (LinearLayout) convertView.findViewById(R.id.list_linear);
				holder.list_title = (TextView) convertView.findViewById(R.id.list_title);
				holder.list_msg = (TextView) convertView.findViewById(R.id.list_msg);
				holder.list_date = (TextView) convertView.findViewById(R.id.list_date);
				holder.list_tranno = (TextView) convertView.findViewById(R.id.list_tranno);
				holder.list_seqno = (TextView) convertView.findViewById(R.id.list_seqno);
				holder.img_notify = (ImageView) convertView.findViewById(R.id.img_notify);
				convertView.setTag(holder);
				convertView.setId(position);
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			
			String date = so.get("TranDate");
			   Log.d("date", " -> "+date);
			   SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
			   SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
			   Date mDate=null;
			   String tranDate = "";
			   try {
			       mDate = input.parse(date);                 // parse input 
			       tranDate = output.format(mDate);    // format output
			       Log.d("tranDate", " -> "+tranDate);
			   } catch (ParseException e) {
			       e.printStackTrace();
			   }
			   
			holder.list_msg.setText(so.get("Message"));
			holder.list_date.setText(tranDate);
			
			String mStatus = so.get("Status");
			if(mStatus.matches("0")){
				
				holder.list_linear.setBackgroundResource(R.color.list_item_bg_unread);
				holder.list_date.setTextColor(Color.parseColor("#0682c2"));
				holder.img_notify.setVisibility(View.VISIBLE);
			}else{
				holder.list_linear.setBackgroundResource(R.color.list_item_bg_read);
				holder.list_date.setTextColor(Color.parseColor("#000000"));
				holder.img_notify.setVisibility(View.GONE);
			}
			
			
			return convertView;
		}

		class ViewHolder {
			LinearLayout list_linear;
			TextView list_title, list_msg, list_date, list_tranno, list_seqno;
			ImageView img_notify;
		}
	}
	
	/** Adapter Class End   **/
	
	public void loadprogress(){
		spinnerLayout = new LinearLayout(AppMessage.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(message_parent, false);
		progressBar = new ProgressBar(AppMessage.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(drawable.greenprogress));
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
		 Intent i = new Intent(AppMessage.this, LandingActivity.class);
		 startActivity(i);
		 AppMessage.this.finish();
	}
	
}
