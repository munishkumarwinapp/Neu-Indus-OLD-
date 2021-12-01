package com.winapp.sotdetails;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SOTSummaryWebService;
import com.winapp.sot.SlideMenuFragment;

public class PackingDetails extends SherlockFragmentActivity
		implements SlideMenuFragment.MenuClickInterFace {

	private ProgressBar progressBar;
	private LinearLayout spinnerLayout, main_parent;
	private TextView customername_txt, customercode_txt, sono_txt;
	private SlidingMenu menu;
	private ImageButton searchIcon, saveIcon, printerIcon;
	private ListView mList;

	private ArrayList<HashMap<String, String>> mDetailsArr = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> mHeaderArr = new ArrayList<HashMap<String, String>>();
	// private ArrayList<HashMap<String, String>> editDetailsArr = new
	// ArrayList<HashMap<String, String>>();
	private customAdapter mCustomAdapter;
	private String valid_url = "", mSoNo = "", mLocationCode = "",
			mCustomerCode = "", mCustomerName = "", summaryResult = "";
	private double mBillDiscount = 0;
	private Cursor mCursor = null;

	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.packing_details);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("Packing Details");
		searchIcon = (ImageButton) customNav.findViewById(R.id.search_img);
		printerIcon = (ImageButton) customNav.findViewById(R.id.printer);
		saveIcon = (ImageButton) customNav.findViewById(R.id.custcode_img);

		saveIcon.setImageResource(R.mipmap.ic_save_img);
		searchIcon.setVisibility(View.GONE);
		printerIcon.setVisibility(View.GONE);
		saveIcon.setVisibility(View.VISIBLE);

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

		SOTDatabase.init(PackingDetails.this);
		FWMSSettingsDatabase.init(PackingDetails.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SOTSummaryWebService(valid_url);

		main_parent = (LinearLayout) findViewById(R.id.main_parent);
		customername_txt = (TextView) findViewById(R.id.customername_txt);
		customercode_txt = (TextView) findViewById(R.id.customercode_txt);
		sono_txt = (TextView) findViewById(R.id.sono_txt);
		mList = (ListView) findViewById(R.id.listview);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			mHeaderArr.clear();
			mDetailsArr.clear();
			SOTDatabase.deleteAllProduct();
			SOTDatabase.deleteBillDisc();

			mDetailsArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("SODetails");
			mHeaderArr = (ArrayList<HashMap<String, String>>) getIntent()
					.getSerializableExtra("SOHeader");
			
			Log.d("Delivery Verification details", "" + mDetailsArr);
			Log.d("Delivery Verification Header", "" + mHeaderArr);

			if (mHeaderArr != null) {
				for (int i = 0; i < mHeaderArr.size(); i++) {

					String SoDate = "", DeliveryDate = "", Total = "", BillDIscount = "", ItemDiscount = "", CurrencyCode = "", CurrencyRate = "", Remarks = "";

					mSoNo = mHeaderArr.get(i).get("InvoiceNo");
					SoDate = mHeaderArr.get(i).get("InvoiceDate").split("\\ ")[0];
//					DeliveryDate = mHeaderArr.get(i).get("DeliveryDate")
//							.split("\\ ")[0];
					mLocationCode = mHeaderArr.get(i).get("LocationCode");
					mCustomerCode = mHeaderArr.get(i).get("CustomerCode");
					mCustomerName = mHeaderArr.get(i).get("CustomerName");
					Total = mHeaderArr.get(i).get("Total");
					ItemDiscount = mHeaderArr.get(i).get("ItemDiscount");
					BillDIscount = mHeaderArr.get(i).get("BillDIscount");
					Remarks = mHeaderArr.get(i).get("Remarks");
					CurrencyCode = mHeaderArr.get(i).get("CurrencyCode");
					CurrencyRate = mHeaderArr.get(i).get("CurrencyRate");

					customername_txt.setText(mCustomerName);
					customercode_txt.setText(mCustomerCode);
					sono_txt.setText(mSoNo);

//					if (!BillDIscount.matches("")) {
//						mBillDiscount = Double.parseDouble(BillDIscount);
//					}
//
//					SalesOrderSetGet.setCustomername(mCustomerName);
//					SalesOrderSetGet.setSaleorderdate(SoDate);
//					SalesOrderSetGet.setDeliverydate(DeliveryDate);
//					SalesOrderSetGet.setLocationcode(mLocationCode);
//					SalesOrderSetGet.setCustomercode(mCustomerCode);
//					SalesOrderSetGet.setRemarks(Remarks);
//					SalesOrderSetGet.setCurrencycode(CurrencyCode);
//					SalesOrderSetGet.setCurrencyrate(CurrencyRate);
//					SalesOrderSetGet.setCurrencyname("");

				}
			}

			if (mDetailsArr != null) {
				Log.d("mDetailsArr.size()", "->" + mDetailsArr.size());
				if (mDetailsArr.size() > 0) {
					mCustomAdapter = new customAdapter(PackingDetails.this, mDetailsArr);
					mList.setAdapter(mCustomAdapter);
					
				}
			}
		}

		saveIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
					AsyncCallWSSummary task = new AsyncCallWSSummary();
					task.execute();

			}
		});

//		mList.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View targetView,
//					int position, long arg3) {
//				String id = ((TextView) targetView.findViewById(R.id.id))
//						.getText().toString();
//				String productcode = ((TextView) targetView
//						.findViewById(R.id.productcode)).getText().toString();
//				String productname = ((TextView) targetView
//						.findViewById(R.id.productname)).getText().toString();
//				String order_carton = ((TextView) targetView
//						.findViewById(R.id.order_carton)).getText().toString();
//				String order_weight = ((TextView) targetView
//						.findViewById(R.id.order_weight)).getText().toString();
//				String packed_carton = ((TextView) targetView
//						.findViewById(R.id.packed_carton)).getText().toString();
//				String packed_weight = ((TextView) targetView
//						.findViewById(R.id.packed_weight)).getText().toString();
//
//				double dCarton = 0, dWeight = 0;
//
//				if (packed_carton != null && !packed_carton.isEmpty()) {
//					dCarton = Double.parseDouble(packed_carton);
//				}
//
//				if (packed_weight != null && !packed_weight.isEmpty()) {
//					dWeight = Double.parseDouble(packed_weight);
//				}
//
//				editAlertDialog(id, productname, dCarton, dWeight);
//			}
//		});

	}

	// /** Context Menu Start **/
	//
	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// super.onCreateContextMenu(menu, v, menuInfo);
	// menu.add(0, v.getId(), 0, "Edit");
	//
	// }
	//
	// @SuppressWarnings("deprecation")
	// @Override
	// public boolean onContextItemSelected(android.view.MenuItem item) {
	// AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	// .getMenuInfo();
	// if (item.getTitle() == "Edit") {
	//
	// String id = ((TextView) info.targetView.findViewById(R.id.id))
	// .getText().toString();
	// String productcode = ((TextView) info.targetView
	// .findViewById(R.id.productcode)).getText().toString();
	// String productname = ((TextView) info.targetView
	// .findViewById(R.id.productname)).getText().toString();
	// String order_carton = ((TextView) info.targetView
	// .findViewById(R.id.order_carton)).getText().toString();
	// String order_weight = ((TextView) info.targetView
	// .findViewById(R.id.order_weight)).getText().toString();
	// String packed_carton = ((TextView) info.targetView
	// .findViewById(R.id.packed_carton)).getText().toString();
	// String packed_weight = ((TextView) info.targetView
	// .findViewById(R.id.packed_weight)).getText().toString();
	//
	// double dCarton=0, dWeight=0;
	//
	// if(packed_carton != null && !packed_carton.isEmpty()){
	// dCarton = Double.parseDouble(packed_carton);
	// }
	//
	// if(packed_weight != null && !packed_weight.isEmpty()){
	// dWeight = Double.parseDouble(packed_weight);
	// }
	//
	// editAlertDialog(id, productname, dCarton, dWeight);
	//
	// } else {
	// return false;
	// }
	// return true;
	//
	// }
	//
	// /** Context Menu End **/

	/** AsyncTask Start **/

	private class AsyncCallWSSummary extends AsyncTask<Void, Void, Void> {
		String dialogStatus="", detail_json="";

		@Override
		protected void onPreExecute() {
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				
				if (mDetailsArr.size() > 0) {

					for (HashMap<String, String> map : mDetailsArr) {

						String slNo = map.get("slNo");
						String ProductCode = map.get("ProductCode");
						String Packed = map.get("IsPacked");

						detail_json += slNo + "^" + ProductCode + "^" + Packed
								+ "!";

					}

					detail_json = detail_json.substring(0, detail_json.length() - 1);
					Log.d("Detail", detail_json);
				}
				
				summaryResult = SOTSummaryWebService.summaryPackingService( "fncSaveInvoicePacked", detail_json, mSoNo);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(main_parent, true);

			if (summaryResult.matches("failed")) {

				Toast.makeText(PackingDetails.this, "Failed",
						Toast.LENGTH_SHORT).show();

			} else {

				Toast.makeText(PackingDetails.this,
						"Saved Successfully", Toast.LENGTH_SHORT).show();
				clearView();
			}

		}
	}

	/** AsyncTask End **/

	/** Adapter Start **/

	public class customAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, String>> packdeDetailsArr = new ArrayList<HashMap<String, String>>();
		private Context mContext;
		private LayoutInflater mInflater = null;
		
		public customAdapter(Activity activty, ArrayList<HashMap<String, String>> list) {
			this.mContext = activty;
			this.mInflater = activty.getLayoutInflater();
			this.packdeDetailsArr = list;
		}

		@Override
		public int getCount() {

			return packdeDetailsArr.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View row, ViewGroup arg2) {
			final Holder holder;
			final HashMap<String, String> data = packdeDetailsArr.get(position);  //new HashMap<String, String>();
			if (row == null) {
				holder = new Holder();
				row = mInflater.inflate(R.layout.packing_detail_listitem, null);

				holder.productid_txt = (TextView) row.findViewById(R.id.id);
				holder.productcode_txt = (TextView) row
						.findViewById(R.id.productcode);
				holder.productname_txt = (TextView) row
						.findViewById(R.id.productname);
				holder.carton_txt = (TextView) row
						.findViewById(R.id.carton);
				holder.loose_txt = (TextView) row
						.findViewById(R.id.loose);
				holder.qty_txt = (TextView) row
						.findViewById(R.id.qty);
				holder.packed_checkbox = (CheckBox) row
						.findViewById(R.id.packed_checkbox);
				row.setTag(holder);
			} else {
				holder = (Holder) row.getTag();
			}
			row.setTag(holder);
			holder.packed_checkbox.setId(position);
			
//			holder.productid_txt.setText(data.get(""));
			holder.productcode_txt.setText(data.get("ProductCode"));
			holder.productname_txt.setText(data.get("ProductName"));
			holder.carton_txt.setText(data.get("CQty"));
			holder.loose_txt.setText(data.get("LQty"));
			holder.qty_txt.setText(data.get("Qty"));
			
			String packedStr = data.get("IsPacked");
			if(packedStr.matches("0")){
				holder.packed_checkbox.setChecked(false);
				row.setBackgroundResource(R.color.white);
			}else if(packedStr.matches("1")){
				holder.packed_checkbox.setChecked(true);
				row.setBackgroundResource(drawable.list_item_selected_bg);
			}
			
			holder.packed_checkbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox checkBox = (CheckBox) v;
					if (checkBox.isChecked()) {
						data.put("IsPacked", "1");
						((View) checkBox.getParent())
						.setBackgroundResource(drawable.list_item_selected_bg);
					}else{
						data.put("IsPacked", "0");
						((View) checkBox.getParent()).setBackgroundResource(R.color.white);
					}
					notifyDataSetChanged();
				}
			});
			
			
			
		
			return row;

		}

		class Holder {

			TextView productid_txt, productcode_txt, productname_txt, carton_txt, loose_txt, qty_txt;
			CheckBox packed_checkbox;
		}
		
		public void resetData() {
			notifyDataSetChanged();
		}

	}

	/** Adapter End **/

	

	public void clearView() {
		saveIcon.setVisibility(View.INVISIBLE);
		Intent intent = new Intent(PackingDetails.this,
				PackingHeader.class);
		startActivity(intent);
		PackingDetails.this.finish();

	}


	public void loadprogress() {
		spinnerLayout = new LinearLayout(PackingDetails.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		PackingDetails.this.addContentView(spinnerLayout,
				new LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(main_parent, false);
		progressBar = new ProgressBar(PackingDetails.this);
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

	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}

	public String fourDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setMinimumFractionDigits(4);
		String tot = df.format(d);

		return tot;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(String item) {
		menu.toggle();
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(PackingDetails.this,
				PackingHeader.class);
		startActivity(i);
		PackingDetails.this.finish();
	}

}
