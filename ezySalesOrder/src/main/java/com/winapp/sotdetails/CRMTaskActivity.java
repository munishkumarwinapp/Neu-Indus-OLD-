package com.winapp.sotdetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SlideMenuFragment;

public class CRMTaskActivity extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace {
	private SlidingMenu menu;
	private ProgressBar progressBar;
	private LinearLayout spinnerLayout, crmtask_parent;
	private ImageButton search_icon, printer_icon, done_icon;
	private ImageView add_icon;
	private ListView task_list;
	private String valid_url="", companyCode="";
	private OfflineSettingsManager spManager;
	private CustomAdapter customAdapter;
	
	private ArrayList<HashMap<String, String>> mArraylist = new ArrayList<HashMap<String, String>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.activity_crmtask);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("CRM Task");
		search_icon = (ImageButton) customNav.findViewById(R.id.search_img);
		printer_icon = (ImageButton) customNav.findViewById(R.id.printer);
		done_icon = (ImageButton) customNav.findViewById(R.id.custcode_img);
		search_icon.setVisibility(View.INVISIBLE);
		printer_icon.setVisibility(View.GONE);
		done_icon.setVisibility(View.GONE);
		
		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);
		ab.setBackgroundDrawable(getResources().getDrawable(
				drawable.task_header_bg));
		

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
		
		mArraylist.clear();
		FWMSSettingsDatabase.init(CRMTaskActivity.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SalesOrderWebService(valid_url);
		SOTDatabase.init(CRMTaskActivity.this);
		spManager = new OfflineSettingsManager(CRMTaskActivity.this);
		companyCode = spManager.getCompanyType();

		add_icon = (ImageView) findViewById(R.id.add_icon);
		crmtask_parent = (LinearLayout) findViewById(R.id.crmtask_parent); 
		task_list = (ListView) findViewById(R.id.task_list);
		
		CommonAsyncCall common = new CommonAsyncCall();
		common.execute();
		
		/** Onclick Start **/
		
		add_icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("Add Button", "--> Click");
				Intent i = new Intent(CRMTaskActivity.this, CRMTaskAdd.class);
				i.putExtra("CRMStatus", "TaskAdd");
				startActivity(i);
				CRMTaskActivity.this.finish();
			}
		});
		
		task_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,long id) {
				Log.d("List Item", "--> Click");
				TextView task_id = (TextView) v.findViewById(R.id.list_text_id);
				Log.d("task_id", "--> "+task_id.getText().toString());
				Intent i = new Intent(CRMTaskActivity.this, CRMTaskAdd.class);
				i.putExtra("CRMStatus", "TaskDetail");
				i.putExtra("TaskID", task_id.getText().toString());
				startActivity(i);
				CRMTaskActivity.this.finish();
			}
		});
		
		/** Onclick End **/
		
	}
	
/***   AsyncTask Start    ***/
	
	private class CommonAsyncCall extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			mArraylist.clear();	
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				
				HashMap<String, String> hmvalue = new HashMap<String, String>();
			    hmvalue.put("CompanyCode", companyCode);
			    hmvalue.put("TaskID", "");
			    hmvalue.put("TaskUser", "");
				mArraylist = SalesOrderWebService.getTaskList(hmvalue,"fncGetCRMTasks");
			
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("listArray Result", mArraylist.toString());
			try {
				if (!mArraylist.isEmpty()) {
					headerCustCode();
				}else{
					mArraylist.clear();
					headerCustCode();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(crmtask_parent, true);
		}
	}
	
	public void headerCustCode() throws ParseException {
			customAdapter = new CustomAdapter(CRMTaskActivity.this, mArraylist);
			task_list.setAdapter(customAdapter);
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
			row = mInflater.inflate(R.layout.task_list_item, null);
			
			holder.text_id = (TextView) row.findViewById(R.id.list_text_id);
			holder.text1 = (TextView) row.findViewById(R.id.list_text1);
			holder.text2 = (TextView) row.findViewById(R.id.list_text2);
			holder.text3 = (TextView) row.findViewById(R.id.list_text3);
			holder.text4 = (TextView) row.findViewById(R.id.list_text4);
			holder.image4 = (ImageView) row.findViewById(R.id.list_img4);
			row.setTag(holder);
		} else {
			holder = (Holder) row.getTag();
		}
	
		value = getItem(position);
		String task_status="", task_date = "";
		String date = value.get("TaskDate");
		String status = value.get("TaskStatus");
		Log.d("date", " -> "+date);
		SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
		SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy");
		Date mDate=null;
		   try {
		       mDate = input.parse(date);                 // parse input 
		       task_date = output.format(mDate);    // format output
		       Log.d("task_date", " -> "+task_date);
		   } catch (ParseException e) {
		       e.printStackTrace();
		   }
		
//		0 - Open , 1 - In Progress , 2 - Completed , 3 - Hold
		if(status.matches("0")){
			task_status = "Open";
			holder.text3.setTextColor(Color.parseColor("#66a51b"));
			holder.image4.setImageResource(R.mipmap.crm_open);
		}else if(status.matches("1")){
			task_status = "In Progress";
			holder.text3.setTextColor(Color.parseColor("#f8c939"));
			holder.image4.setImageResource(R.mipmap.crm_inprogress);
		}else if(status.matches("2")){
			task_status = "Completed";
			holder.text3.setTextColor(Color.parseColor("#7f7373"));
			holder.image4.setImageResource(R.mipmap.crm_closed);
		}else if(status.matches("3")){
			task_status = "Hold";
			holder.text3.setTextColor(Color.parseColor("#F75117"));
			holder.image4.setImageResource(R.mipmap.crm_hold);
		}
		
		holder.text_id.setText(value.get("TaskID"));
		holder.text1.setText(value.get("TaskName"));
		holder.text2.setText(value.get("TaskDescription"));
		holder.text3.setText(task_status);
		holder.text4.setText(task_date);
		
//		if (position % 2 == 0) {
//			row.setBackgroundResource(R.drawable.list_item_even_bg);
//			holder.text1.setTextColor(Color.parseColor("#035994"));
//			holder.text2.setTextColor(Color.parseColor("#035994"));
//			holder.text3.setTextColor(Color.parseColor("#035994"));
//			holder.text4.setTextColor(Color.parseColor("#035994"));
//	
//		} else {
//	
//			row.setBackgroundResource(R.drawable.list_item_odd_bg);
//			holder.text1.setTextColor(Color.parseColor("#646464"));
//			holder.text2.setTextColor(Color.parseColor("#646464"));
//			holder.text3.setTextColor(Color.parseColor("#646464"));
//			holder.text4.setTextColor(Color.parseColor("#646464"));
//		}
	
		return row;
	}
	
	final class Holder {
	
		TextView text_id, text1, text2, text3, text4, text5;
		ImageView image4;
	
	}
		}


/** Adapter End   **/
	
	
	public void loadprogress(){
		spinnerLayout = new LinearLayout(CRMTaskActivity.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(crmtask_parent, false);
		progressBar = new ProgressBar(CRMTaskActivity.this);
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
		Intent i = new Intent(CRMTaskActivity.this, LandingActivity.class);
		startActivity(i);
		CRMTaskActivity.this.finish();
	}

}
