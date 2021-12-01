package com.winapp.sotdetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SlideMenuFragment;

public class AppMessageDetail extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace {
	
	private SlidingMenu menu;
	private ProgressBar progressBar;
	private LinearLayout spinnerLayout, message_parent;
	private ImageButton search_icon, add_icon;
	private TextView txtTitle, txtMessage, txtDate;
	private ImageView imgLeft, imgRight;
	private String valid_url="", companyCode="", Title="", TranNo="",SeqNo="", TranDate="", Message="", status="";
	private int selected_position =-1, current_position=-1;
	private OfflineSettingsManager spManager;
	private ArrayList<HashMap<String, String>> msgArrList = new ArrayList<HashMap<String, String>>();
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.activity_appmessage_detail);
		
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
		new DateWebservice(valid_url,AppMessageDetail.this);
		new SalesOrderWebService(valid_url);
		spManager = new OfflineSettingsManager(AppMessageDetail.this);
		companyCode = spManager.getCompanyType();
		
		message_parent = (LinearLayout) findViewById(R.id.message_parent);
		txtTitle = (TextView) findViewById(R.id.list_title);
		txtMessage = (TextView) findViewById(R.id.list_msg);
		txtDate = (TextView) findViewById(R.id.list_date);
		imgLeft = (ImageView) findViewById(R.id.img_left);
		imgRight = (ImageView) findViewById(R.id.img_right);
		
		msgArrList.clear();
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Log.e("extras", "Extra NULL");
		} else {
		
			msgArrList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("msgArrList");
			Log.d("msgArrList size", " -> "+msgArrList.size());
			selected_position = extras.getInt("select_position");
			Log.d("selected_position", " -> "+selected_position);
			current_position = selected_position;
			setValue(selected_position);
		}
		
		
		imgLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				current_position = current_position-1;
				Log.d("Left arrow current_position", " -> "+current_position);
				if (current_position != -1 && current_position<msgArrList.size()) {				
					setValue(current_position);
				} else {
					current_position = current_position+1;
					Log.d("Left arrow current_position", " else -> "+current_position);
				}
			
			}
		});
		
		imgRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				current_position = current_position+1;
				Log.d("right arrow current_position", " -> "+current_position);
				if (current_position != -1 && current_position<msgArrList.size()) {
					setValue(current_position);
				} else {
					current_position = current_position-1;
					Log.d("Right arrow current_position", " else -> "+current_position);
				}
			
			}
		});
		
		
	}
	
	private void setValue(int pos){
		
		if(pos == 0){
			imgLeft.setImageResource(R.mipmap.leftgrey);
			imgRight.setImageResource(R.mipmap.rightblue);
		}else if(pos == (msgArrList.size()-1)){
			imgLeft.setImageResource(R.mipmap.leftblue);
			imgRight.setImageResource(R.mipmap.rightgrey);
		}else{
			imgLeft.setImageResource(R.mipmap.leftblue);
			imgRight.setImageResource(R.mipmap.rightblue);
		}
		
		SeqNo = msgArrList.get(pos).get("SeqNo");
		TranNo = msgArrList.get(pos).get("TranNo");
		TranDate = msgArrList.get(pos).get("TranDate");
	    Message = msgArrList.get(pos).get("Message");
		status = msgArrList.get(pos).get("Status");
		
		if(status.matches("0")){
			msgUpdateAsyncCall common = new msgUpdateAsyncCall();
			common.execute();	
		}
		
		   SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
		   SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
		   Date mDate=null;
		   String tranDate = "";
		   try {
		       mDate = input.parse(TranDate);                 // parse input 
		       tranDate = output.format(mDate);    // format output
		       Log.d("tranDate", " -> "+tranDate);
		   } catch (ParseException e) {
		       e.printStackTrace();
		   }
		   
		txtDate.setText(tranDate);
		txtMessage.setText(Message);
	}
	
	 /** AsyncTask Strat  **/
	
		private class msgUpdateAsyncCall extends AsyncTask<Void, Void, Void> {
			@Override
			protected void onPreExecute() {
				loadprogress();	
			}

			@Override
			protected Void doInBackground(Void... arg0) {

				try {
					
					HashMap<String, String> hmvalue = new HashMap<String, String>();
					hmvalue.put("CompanyCode", companyCode);
					hmvalue.put("TranNo", TranNo);
					hmvalue.put("SeqNo", SeqNo);
					String result = SalesOrderWebService.updateAppMessage(hmvalue, "fncUpdateInternalMessageStatus");
					
					if(result!=null && !result.isEmpty()){
						msgArrList.get(current_position).put("Status" , "1");
					}
						
				
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(message_parent, true);
			}
		}
		
	 /** AsyncTask End**/
		
	
	public void loadprogress(){
		spinnerLayout = new LinearLayout(AppMessageDetail.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(message_parent, false);
		progressBar = new ProgressBar(AppMessageDetail.this);
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
		 Intent i = new Intent(AppMessageDetail.this, AppMessage.class);
		 startActivity(i);
		 AppMessageDetail.this.finish();
	}

}
