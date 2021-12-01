package com.winapp.sot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.fwms.SupplierSetterGetter;

public class StockRequestHeaderDetail extends Activity {
	EditText stockreq_edTxtRemarks, stockreq_edDate;
	Spinner spinner;
	Button stockreq_addProduct;
	ImageButton back;

	Calendar myCalendar;

	String locationname, tolocation_code;
	ArrayList<String> arr_loc;
	String[] str_arr_loc;
	Set<String> hashset_loc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.stockrequest_detail);

		stockreq_edTxtRemarks = (EditText) findViewById(R.id.stockreq_edTxtRemarks);
		stockreq_edDate = (EditText) findViewById(R.id.stockreq_edDate);

		spinner = (Spinner) findViewById(R.id.stockreq_spToLoc);
		back = (ImageButton) findViewById(R.id.back);
		stockreq_addProduct = (Button) findViewById(R.id.stockreq_addProduct);

		myCalendar = Calendar.getInstance();
		arr_loc=new ArrayList<String>();
		hashset_loc = new HashSet<String>();
		arr_loc.clear();
		stockreq_edDate.setText(SalesOrderSetGet.getSaleorderdate());

				 
		
		 arr_loc = SalesOrderSetGet.getFrom_location();
		
		
	for (int i = 0; i < arr_loc.size(); i++) {
			if (arr_loc.get(i).contains(SupplierSetterGetter.getLocationcode().toString())) {
				
				Log.d("list", "hEADER"+SupplierSetterGetter.getLocationcode().toString());
				
				arr_loc.remove(SupplierSetterGetter.getLocationcode().toString());
				Log.d("list", "STOCK arr_loc"+arr_loc.toString());
			}
		}
			spinner.setAdapter(new SpinnerAdapter(
					StockRequestHeaderDetail.this, R.layout.row,
					arr_loc));
			
			Log.d("list", "STOCK arr_loc"+arr_loc.toString());

		stockreq_addProduct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SOTDatabase.init(StockRequestHeaderDetail.this);
				SOTDatabase.deleteAllProduct();
				SOTDatabase.deleteBillDisc();
				if(spinner.getSelectedItem().toString().matches("Select Location")){
					 Toast.makeText(getApplicationContext(),"Please Select Location",Toast.LENGTH_SHORT).show();
				}
				else{
					Intent calllanding = new Intent(StockRequestHeaderDetail.this,
							StockRequestAddProduct.class);
					startActivity(calllanding);
					
					onSetGet();
				}
				

			}
		});
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent calllanding = new Intent(StockRequestHeaderDetail.this,
						StockRequestHeader.class);
				startActivity(calllanding);
				StockRequestHeaderDetail.this.finish();
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
					new DatePickerDialog(StockRequestHeaderDetail.this, indate,
							myCalendar.get(Calendar.YEAR), myCalendar
									.get(Calendar.MONTH), myCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
	}

	private void stockRequestDate() {

		String myFormat = "dd/MM/yyyy"; // In which you need put here
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		stockreq_edDate.setText(sdf.format(myCalendar.getTime()));

	}

	public class SpinnerAdapter extends ArrayAdapter<String> {
		 List<String> arrlist=new ArrayList<String>();
		public SpinnerAdapter(Context context, int textViewResourceId,
				List<String> arr_loc) {
			super(context, textViewResourceId, arr_loc);
			// TODO Auto-generated constructor stub
			arrlist.clear();
			arrlist=arr_loc;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);
			TextView label = (TextView) row.findViewById(R.id.locationspinner);
			ImageView icon = (ImageView) row.findViewById(R.id.spinnericon);
			label.setText(arrlist.get(position));
			icon.setVisibility(View.GONE);
			if (position == spinner.getSelectedItemPosition()) {
				locationname = spinner.getSelectedItem().toString();
				// Toast.makeText(StockRequestHeaderDetail.this, locationname,
				// Toast.LENGTH_SHORT).show();
			}
			return row;
		}
	}

	public void onSetGet() {
		HashMap<String, String> location_code_name = new HashMap<String, String>();
		SalesOrderSetGet.setSaleorderdate(stockreq_edDate.getText().toString());
		SalesOrderSetGet.setRemarks(stockreq_edTxtRemarks.getText().toString());
		location_code_name = SupplierSetterGetter.getLoc_code_name();
		tolocation_code = location_code_name.get(locationname);
		Log.d("tolocation_code",""+tolocation_code);
		SupplierSetterGetter.setLocCode(tolocation_code);
		StockRequestHeaderDetail.this.finish();	
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(StockRequestHeaderDetail.this,
				StockRequestHeader.class);
		startActivity(i);
		StockRequestHeaderDetail.this.finish();
	}
}
