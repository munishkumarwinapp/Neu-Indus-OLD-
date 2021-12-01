package com.winapp.sot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.fwms.GetUserPermission;
import com.winapp.fwms.SupplierSetterGetter;

public class TransferHeaderDetail extends Activity {
	EditText stockreq_edTxtRemarks, stockreq_edDate;
	Spinner tospinner, frspinner;
	Button stockreq_addProduct;
	ImageButton back;
	LinearLayout stockreq_froLoclbl,customer_layout;
	Calendar myCalendar;
	String tolocationname, tolocation_code, fromlocationname;
	String[] str_arr_loc;
	String[] srt_arrF_loc;
	Set<String> hashset_loc;
	ArrayList<String> arr_loc = new ArrayList<String>();
	ArrayList<String> fromarraylist = new ArrayList<String>();
	ArrayList<String> toarraylist = new ArrayList<String>();
	int select_pos;
	TextView listing_screen, customer_screen, addProduct_screen,summary_screen;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.transfer_detail);

		stockreq_edTxtRemarks = (EditText) findViewById(R.id.stockreq_edTxtRemarks);
		stockreq_edDate = (EditText) findViewById(R.id.stockreq_edDate);

		stockreq_froLoclbl = (LinearLayout) findViewById(R.id.stockreq_froLoclbl);

		frspinner = (Spinner) findViewById(R.id.stockreq_spfromLoc);
		tospinner = (Spinner) findViewById(R.id.stockreq_spToLoc);
		back = (ImageButton) findViewById(R.id.back);
		stockreq_addProduct = (Button) findViewById(R.id.stockreq_addProduct);
		
		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen = (TextView) findViewById(R.id.sum_screen);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);
		customer_layout.setVisibility(View.GONE);
		
		customer_screen.setText("Location");
		
		customer_screen.setTextColor(Color.parseColor("#FFFFFF"));
		customer_screen.setBackgroundResource(R.drawable.tab_select);
		
		arr_loc = new ArrayList<String>();
		arr_loc.clear();
		myCalendar = Calendar.getInstance();
		hashset_loc = new HashSet<String>();
		stockreq_edDate.setText(SalesOrderSetGet.getSaleorderdate());
		arr_loc = SalesOrderSetGet.getFrom_location();

		if (!SalesOrderSetGet.getTransferchangefromloc().matches("")) {

			if (SalesOrderSetGet.getTransferchangefromloc().matches("1")) {
				stockreq_froLoclbl.setVisibility(View.VISIBLE);
				fromarraylist.addAll(arr_loc);
				toarraylist.addAll(arr_loc);
				frspinner
						.setAdapter(new SpinnerAdapter(
								TransferHeaderDetail.this, R.layout.row,
								fromarraylist));
				for (int i = 0; i < fromarraylist.size(); i++) {
					if (fromarraylist.get(i).contains(
							SupplierSetterGetter.getLocationcode().toString())) {
						select_pos = i;

					}
				}
				frspinner.setSelection(select_pos);
				tospinner.setAdapter(new SpinnerAdapter(
						TransferHeaderDetail.this, R.layout.row, toarraylist));

			} else if (SalesOrderSetGet.getTransferchangefromloc().matches("0")) {

				arr_loc = SalesOrderSetGet.getFrom_location();

				for (int i = 0; i < arr_loc.size(); i++) {
					if (arr_loc.get(i).contains(
							SupplierSetterGetter.getLocationcode().toString())) {
						arr_loc.remove(SupplierSetterGetter.getLocationcode()
								.toString());
					}
				}
				tospinner.setAdapter(new SpinnerAdapter(
						TransferHeaderDetail.this, R.layout.row, arr_loc));

			}
		} else {

			arr_loc = SalesOrderSetGet.getFrom_location();
			Log.d("list", arr_loc.toString());

			for (int i = 0; i < arr_loc.size(); i++) {
				if (arr_loc.get(i).contains(
						SupplierSetterGetter.getLocationcode().toString())) {
					arr_loc.remove(SupplierSetterGetter.getLocationcode()
							.toString());
				}
			}
			tospinner.setAdapter(new SpinnerAdapter(TransferHeaderDetail.this,
					R.layout.row, arr_loc));
		}
		
		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Cursor cursor = SOTDatabase.getCursor();
				int count = cursor.getCount();
				if (count > 0) {
					alertDialog();
				} else {

					Intent i = new Intent(TransferHeaderDetail.this,
							TransferHeader.class);
					startActivity(i);
					TransferHeaderDetail.this.finish();

				}

			}
		});

		addProduct_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SOTDatabase.init(TransferHeaderDetail.this);
				SOTDatabase.deleteAllProduct();
				SOTDatabase.deleteBillDisc();
				SOTDatabase.deleteallbatch();
				if (SalesOrderSetGet.getTransferchangefromloc().matches("1")) {
					String fsp = frspinner.getSelectedItem().toString();
					String tsp = tospinner.getSelectedItem().toString();

					//if ((!fsp.equals(tsp))) {
					  if((!fsp.equals(tsp)) && (tsp != "Select Location")){
						  onSetGet();
						  String haveBatchOnTransfer = SalesOrderSetGet.getHaveBatchOnTransfer();
							 Log.d("HaveBatchOnTransfer FROM location",haveBatchOnTransfer);
							 
							 if(haveBatchOnTransfer!=null && !haveBatchOnTransfer.isEmpty()){
									
								}else{
									haveBatchOnTransfer="";
								}
							 
							if (haveBatchOnTransfer.matches("False")){
							
								new AsyncCallBatchLocation().execute();
							}else{
								Intent i = new Intent(TransferHeaderDetail.this, TransferAddProduct.class);
								startActivity(i);
								TransferHeaderDetail.this.finish();
							}
//						Intent calllanding = new Intent(
//								TransferHeaderDetail.this,
//								TransferAddProduct.class);
//						startActivity(calllanding);
						//TransferHeaderDetail.this.finish();
						
					} else {
						Toast.makeText(TransferHeaderDetail.this,
								"Please Select Different Location",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					onSetGet();
					 
					String haveBatchOnTransfer = SalesOrderSetGet.getHaveBatchOnTransfer();
					 Log.d("HaveBatchOnTransfer FROM location",haveBatchOnTransfer);
					 if(haveBatchOnTransfer!=null && !haveBatchOnTransfer.isEmpty()){
							
						}else{
							haveBatchOnTransfer="";
						}
					 
					if (haveBatchOnTransfer.matches("False")){
					
						new AsyncCallBatchLocation().execute();
					}else{
						Intent i = new Intent(TransferHeaderDetail.this, TransferAddProduct.class);
						startActivity(i);
						TransferHeaderDetail.this.finish();
					}
					
				}
			}
		});

		summary_screen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent i = new Intent(TransferHeaderDetail.this, TransferSummary.class);
				startActivity(i);
				TransferHeaderDetail.this.finish();
			}
		});
		
		stockreq_addProduct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SOTDatabase.init(TransferHeaderDetail.this);
				SOTDatabase.deleteAllProduct();
				SOTDatabase.deleteBillDisc();
				SOTDatabase.deleteallbatch();
				if (SalesOrderSetGet.getTransferchangefromloc().matches("1")) {
					String fsp = frspinner.getSelectedItem().toString();
					String tsp = tospinner.getSelectedItem().toString();

					//if ((!fsp.equals(tsp))) {
					  if((!fsp.equals(tsp)) && (tsp != "Select Location")){
						  onSetGet();
						  String haveBatchOnTransfer = SalesOrderSetGet.getHaveBatchOnTransfer();
						  if(haveBatchOnTransfer!=null && !haveBatchOnTransfer.isEmpty()){
								
							}else{
								haveBatchOnTransfer="";
							}
							 Log.d("HaveBatchOnTransfer FROM location",haveBatchOnTransfer);
							if (haveBatchOnTransfer.matches("False")){
							
								new AsyncCallBatchLocation().execute();
							}else{
								Intent i = new Intent(TransferHeaderDetail.this, TransferAddProduct.class);
								startActivity(i);
								TransferHeaderDetail.this.finish();
							}
//						Intent calllanding = new Intent(
//								TransferHeaderDetail.this,
//								TransferAddProduct.class);
//						startActivity(calllanding);
						//TransferHeaderDetail.this.finish();
						
					} else {
						Toast.makeText(TransferHeaderDetail.this,
								"Please Select Different Location",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					onSetGet();
					 
					String haveBatchOnTransfer = SalesOrderSetGet.getHaveBatchOnTransfer();
					if(haveBatchOnTransfer!=null && !haveBatchOnTransfer.isEmpty()){
						
					}else{
						haveBatchOnTransfer="";
					}
					 Log.d("HaveBatchOnTransfer FROM location",haveBatchOnTransfer);
					if (haveBatchOnTransfer.matches("False")){
					
						new AsyncCallBatchLocation().execute();
					}else{
						Intent i = new Intent(TransferHeaderDetail.this, TransferAddProduct.class);
						startActivity(i);
						TransferHeaderDetail.this.finish();
					}
					
				}
			}
		});
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(TransferHeaderDetail.this, TransferHeader.class);
				startActivity(i);
				TransferHeaderDetail.this.finish();
			}
		});
		final DatePickerDialog.OnDateSetListener indate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				stockRequestDate();
			}
		};

		stockreq_edDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(TransferHeaderDetail.this, indate,
							myCalendar.get(Calendar.YEAR), myCalendar
									.get(Calendar.MONTH), myCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
	}

	private void stockRequestDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		stockreq_edDate.setText(sdf.format(myCalendar.getTime()));

	}

	public class SpinnerAdapter extends ArrayAdapter<String> {

		ArrayList<String> data = new ArrayList<String>();

		public SpinnerAdapter(Context context, int textViewResourceId,
				ArrayList<String> objects) {

			super(context, textViewResourceId, objects);
			data.clear();
			this.data = objects;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {

			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {

			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);
			TextView label = (TextView) row.findViewById(R.id.locationspinner);
			ImageView icon = (ImageView) row.findViewById(R.id.spinnericon);
			label.setText(data.get(position));
			icon.setVisibility(View.GONE);

			return row;
		}
	}

	public void onSetGet() {
		tolocationname = tospinner.getSelectedItem().toString();

		Log.d("locationname", tolocationname);
		if (tolocationname.matches("Select Location")) {
			tolocation_code = "";
		} else {
			HashMap<String, String> location_code_name = new HashMap<String, String>();
			location_code_name = SupplierSetterGetter.getLoc_code_name();
			tolocation_code = location_code_name.get(tolocationname);
		}
		SalesOrderSetGet.setSaleorderdate(stockreq_edDate.getText().toString());
		SalesOrderSetGet.setRemarks(stockreq_edTxtRemarks.getText().toString());
		SupplierSetterGetter.setLocCode(tolocation_code);
		if (SalesOrderSetGet.getTransferchangefromloc().matches("1")) {
			fromlocationname = frspinner.getSelectedItem().toString();
			SalesOrderSetGet.setTransferfromloc(fromlocationname);
		}
//		TransferHeaderDetail.this.finish();
	}
	
	private class AsyncCallBatchLocation extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			
			
				try {
					GetUserPermission.getLocationHaveBatchTransfer("fncGetLocation", tolocation_code);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Intent calllanding = new Intent(TransferHeaderDetail.this,
					TransferAddProduct.class);
			startActivity(calllanding);
			TransferHeaderDetail.this.finish();
		}
	}
	
	 public void alertDialog() {

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle("Deleting");
			alertDialog
					.setMessage("Products will clear. Do you want to proceed");
			alertDialog.setIcon(R.mipmap.ic_exit);
			alertDialog.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent i = new Intent(TransferHeaderDetail.this,
									TransferHeader.class);
							startActivity(i);
							TransferHeaderDetail.this.finish();

						}
					});

			alertDialog.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();
						}
					});
			alertDialog.show();
		}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(TransferHeaderDetail.this, TransferHeader.class);
		startActivity(i);
		TransferHeaderDetail.this.finish();
	}
}
