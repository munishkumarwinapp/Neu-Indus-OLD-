package com.winapp.fwms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.helper.DateTime;
import com.winapp.sot.SalesOrderSetGet;

public class StockInActivity extends Activity implements OnItemClickListener {
	ImageView back, search,newsupplier;
	Button btsearch;
	Set<String> suppliernames;
	ArrayList<HashMap<String, String>> stockinheader;
	String refid;
	int supplength;
	Random myRandom;
	Date d = new Date();
	CharSequence s;
	ListView lv;
	Intent callAddSupplier, calllogout, calllanding;
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetStockInHeader";
	private static String webMethGetSupplier = "fncGetSupplier";
	static String supresult;
	String refNo, refDate, suppliercode;
	static ArrayList<HashMap<String, String>> stockin = new ArrayList<HashMap<String, String>>();
	String valid_url;
	ArrayList<String> alsupp = new ArrayList<String>();
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	LinearLayout stockin_parent, searchlayout,codelayout;
	EditText supcodefield, endeditTextDate, starteditTextDate;
	String timedate, datetime;

	CustomAlertAdapterSupp arrayAdapterSupp;
	int textlength = 0;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> searchResults;
	private AlertDialog myalertDialog = null;
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	String endDate;
	Date searchStartDate;
	String supCode, sDate, eDate, searchTime;
	ArrayList<HashMap<String, String>> searchStockArr = new ArrayList<HashMap<String, String>>();
	int month, day, year;

	Calendar startCalendar,endCalendar = Calendar.getInstance();
	
	String suppTxt, splrcode, splrname;
	HashMap<String, String> hashmap = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.stockin_activity);
		codelayout=(LinearLayout)findViewById(R.id.codelayout);
		stockin_parent = (LinearLayout) findViewById(R.id.stockin_parent);
		searchlayout = (LinearLayout) findViewById(R.id.searchlayout);

		search = (ImageView) findViewById(R.id.search);
		supcodefield = (EditText) findViewById(R.id.supcodefield);
		endeditTextDate = (EditText) findViewById(R.id.endeditTextDate);
		starteditTextDate = (EditText) findViewById(R.id.starteditTextDate);
		newsupplier = (ImageView) findViewById(R.id.add_img);
		back = (ImageView) findViewById(R.id.back);
		lv = (ListView) findViewById(R.id.listView1);
		btsearch = (Button) findViewById(R.id.btsearch);

		searchlayout.setVisibility(View.GONE);
		searchStockArr.clear();
		startCalendar = Calendar.getInstance();
		endCalendar = Calendar.getInstance();
		FWMSSettingsDatabase.init(StockInActivity.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new AddSupplierWebService(valid_url);
		new SearchStockInWebService(valid_url);
		new DateWebservice(valid_url,StockInActivity.this);

		AsyncCallWSGetSup stockin = new AsyncCallWSGetSup();
		stockin.execute();

		AsyncCallWSADDSUP task = new AsyncCallWSADDSUP();
		task.execute();

		final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				startCalendar.set(Calendar.YEAR, year);
				startCalendar.set(Calendar.MONTH, monthOfYear);
				startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				strtDate();
			}
		};

		final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				endCalendar.set(Calendar.YEAR, year);
				endCalendar.set(Calendar.MONTH, monthOfYear);
				endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				edDate();
			}
		};

		starteditTextDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(StockInActivity.this, startDate,
							startCalendar.get(Calendar.YEAR), startCalendar
									.get(Calendar.MONTH), startCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});

		endeditTextDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(StockInActivity.this, endDate,
							endCalendar.get(Calendar.YEAR), endCalendar
									.get(Calendar.MONTH), endCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});

		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (searchlayout.getVisibility() == View.VISIBLE) {
					// Its visible
					searchlayout.setVisibility(View.GONE);
					inputMethodManager.hideSoftInputFromWindow(
							codelayout.getWindowToken(), 0);
				} else {
					searchlayout.setVisibility(View.VISIBLE);
					// Either gone or invisible
					supcodefield.requestFocus();
					inputMethodManager.toggleSoftInputFromWindow(
							codelayout.getApplicationWindowToken(),
							InputMethodManager.SHOW_FORCED, 0);
				}

			}
		});

		btsearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				supCode = supcodefield.getText().toString();
				sDate = starteditTextDate.getText().toString();
				eDate = endeditTextDate.getText().toString();

				if (sDate.matches("")) {
					Toast.makeText(StockInActivity.this, "Enter Start Date",
							Toast.LENGTH_SHORT).show();
				} else if (eDate.matches("")) {
					Toast.makeText(StockInActivity.this, "Enter End Date",
							Toast.LENGTH_SHORT).show();
				} else {
					AsyncCallWSSearchStockIn searchStock = new AsyncCallWSSearchStockIn();
					searchStock.execute();
				}

			}
		});

		newsupplier.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SalesOrderSetGet.setCustomercode("");
				SalesOrderSetGet.setCustomername("");
				SalesOrderSetGet.setRemarks("");
				SalesOrderSetGet.setSaleorderdate("");
				callAddSupplier = new Intent(StockInActivity.this,
						AddSupplier.class);
				startActivity(callAddSupplier);
				StockInActivity.this.finish();
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				calllanding = new Intent(StockInActivity.this,
						LandingActivity.class);
				startActivity(calllanding);
				StockInActivity.this.finish();
			}
		});

		supcodefield
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						supcodefield) {
					@Override
					public boolean onDrawableClick() {
						// RIGHT drawable image...
						alertDialogSearch();
						return true;

					}

				});
	}

	private void strtDate() {

		String myFormat = "dd/MM/yyyy"; 
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		starteditTextDate.setText(sdf.format(startCalendar.getTime()));
	}

	private void edDate() {

		String myFormat = "dd/MM/yyyy"; 
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		endeditTextDate.setText(sdf.format(endCalendar.getTime()));
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

	private class AsyncCallWSStockIn extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			stockin.clear();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String fromdate=DateTime.date(endDate);
			SoapObject request = new SoapObject(NAMESPACE, webMethName);

			PropertyInfo sDate = new PropertyInfo();
			PropertyInfo eDate = new PropertyInfo();

			sDate.setName("FromDate");
			sDate.setValue(fromdate);
			sDate.setType(String.class);
			request.addProperty(sDate);

			eDate.setName("ToDate");
			eDate.setValue(endDate);
			eDate.setType(String.class);
			request.addProperty(eDate);

			String suppTxt = null;
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(
					valid_url);
			try {
				androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				suppTxt = response.toString();
				supresult = " { StockInHeader : " + suppTxt + "}";
				JSONObject jsonResponse;

				try {
					jsonResponse = new JSONObject(supresult);
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("StockInHeader");

					int lengthJsonArr = jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);
						refNo = jsonChildNode.optString("RefNo").toString();
						refDate = jsonChildNode.optString("RefDate").toString();
						suppliercode = jsonChildNode.optString("SupplierCode")
								.toString();

						HashMap<String, String> stockinhm = new HashMap<String, String>();
						stockinhm.put("refNo", refNo);

						if (refDate != null) {
							 StringTokenizer tokens = new StringTokenizer(refDate, " ");
								String date = tokens.nextToken();
								stockinhm.put("refDate", date);
						} else {
							stockinhm.put("refDate", refDate);
						}
						
						stockinhm.put("suppliercode", suppliercode);

						stockin.add(stockinhm);
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				suppTxt = "Error occured";
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				stockInHeader();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(stockin_parent, true);
		}
	}

	private class AsyncCallWSGetSup extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(StockInActivity.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(stockin_parent, false);
			progressBar = new ProgressBar(StockInActivity.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			endDate = DateWebservice.getDateService("fncGetServerDate");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (endDate != null) {
				starteditTextDate.setText(DateTime.date(endDate));
				endeditTextDate.setText(endDate);

				AsyncCallWSStockIn task = new AsyncCallWSStockIn();
				task.execute();
			}
		}
	}

	private class AsyncCallWSSearchStockIn extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(StockInActivity.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(stockin_parent, false);
			progressBar = new ProgressBar(StockInActivity.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			searchStockArr = SearchStockInWebService.SearchStockInService(
					supCode, sDate, eDate, "fncGetStockInHeader");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (!searchStockArr.isEmpty()) {
				try {
					searchStockList();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				searchStockArr.clear();
				try {
					searchStockList();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(getApplicationContext(), "No matches found",
						Toast.LENGTH_SHORT).show();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(stockin_parent, true);
		}
	}

	public void alertDialogSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				StockInActivity.this);
		final EditText editText = new EditText(StockInActivity.this);
		final ListView listview = new ListView(StockInActivity.this);
		LinearLayout layout = new LinearLayout(StockInActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Supplier");
		editText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.search, 0,
				0, 0);
		layout.addView(editText);
		layout.addView(listview);
		myDialog.setView(layout);

		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					myalertDialog
							.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		arrayAdapterSupp = new CustomAlertAdapterSupp(StockInActivity.this, al);
		listview.setAdapter(arrayAdapterSupp);
		listview.setOnItemClickListener(StockInActivity.this);

		searchResults = new ArrayList<HashMap<String, String>>(al);
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				textlength = editText.getText().length();
				searchResults.clear();
				for (int i = 0; i < al.size(); i++) {
					String supplierName = al.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(al.get(i));
					}
				}

				arrayAdapterSupp = new CustomAlertAdapterSupp(
						StockInActivity.this, searchResults);
				listview.setAdapter(arrayAdapterSupp);
			}
		});
		myDialog.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		myalertDialog = myDialog.show();
	}

	public void searchStockList() throws ParseException {

		SimpleAdapter adapter = new StockListAdapter(StockInActivity.this,
				searchStockArr, R.layout.addsupplier_list_item, new String[] {
						"RefNo", "SupplierCode", "RefDate" }, new int[] {
						R.id.refcode, R.id.suppliername, R.id.supplydate });
		lv.setAdapter(adapter);

	}

	public void stockInHeader() throws ParseException {

		if (stockin == null) {
			Toast.makeText(getApplicationContext(), "server problem",
					Toast.LENGTH_LONG).show();
		} else {

			SimpleAdapter adapter = new StockListAdapter(StockInActivity.this,
					stockin, R.layout.addsupplier_list_item, new String[] {
							"refNo", "suppliercode", "refDate" }, new int[] {
							R.id.refcode, R.id.suppliername, R.id.supplydate });
			lv.setAdapter(adapter);

		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		myalertDialog.dismiss();
		getArraylsit = arrayAdapterSupp.getArrayList();
		HashMap<String, String> datavalue = getArraylsit.get(position);
		Set<Entry<String, String>> keys = datavalue.entrySet();
		Iterator<Entry<String, String>> iterator = keys.iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Entry mapEntry = iterator.next();
			String keyValues = (String) mapEntry.getKey();
			supcodefield.setText(keyValues);

			supcodefield.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable s) {

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {

					textlength = supcodefield.getText().length();

				}
			});
		}
	}

	private class AsyncCallWSADDSUP extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			al.clear();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			SoapObject request = new SoapObject(NAMESPACE, webMethGetSupplier);
			
			PropertyInfo companyCode = new PropertyInfo();
			
			String cmpnyCode=SupplierSetterGetter.getCompanyCode();
			
			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(
					valid_url);
			try {
				androidHttpTransport.call(SOAP_ACTION + webMethGetSupplier,
						envelope);
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				suppTxt = response.toString();
				supresult = " { SupplierDetails : " + suppTxt + "}";
				JSONObject jsonResponse;
				try {

					jsonResponse = new JSONObject(supresult);

					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("SupplierDetails");
					int lengthJsonArr = jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {
						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);

						splrcode = jsonChildNode.optString("SupplierCode")
								.toString();
						splrname = jsonChildNode.optString("SupplierName")
								.toString();

						HashMap<String, String> supplierhm = new HashMap<String, String>();
						supplierhm.put(splrcode, splrname);
						al.add(supplierhm);
						hashmap.putAll(supplierhm);
						alsupp.add(suppliercode);

					}
				} catch (JSONException e) {

					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
				suppTxt = "Error occured";
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
		}
	}

	public class StockListAdapter extends SimpleAdapter {

		  ArrayList<HashMap<String, String>> stockin;
		
		  public StockListAdapter(Context context, 
		    ArrayList<HashMap<String, String>> stockin, 
		          int resource, 
		          String[] from, 
		          int[] to) {
		    super(context, stockin, resource, from, to);
		    this.stockin = stockin;
		  }

		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		    View view = super.getView(position, convertView, parent);
		    TextView refcode = (TextView) view.findViewById(R.id.refcode);
		    TextView suppliername = (TextView) view.findViewById(R.id.suppliername);
		    TextView supplydate = (TextView) view.findViewById(R.id.supplydate);
		    
		    if(position % 2 == 0){

		     view.setBackgroundResource(drawable.list_item_even_bg);
		    
		      refcode.setTextColor(Color.parseColor("#035994")); 
		    suppliername.setTextColor(Color.parseColor("#035994"));
		    supplydate.setTextColor(Color.parseColor("#035994"));
		          
		   }else {
		    
		    view.setBackgroundResource(drawable.list_item_odd_bg);
		    refcode.setTextColor(Color.parseColor("#646464")); 
		    suppliername.setTextColor(Color.parseColor("#646464")); 
		    supplydate.setTextColor(Color.parseColor("#646464")); 
		   }
		    return view;
		  }
		  

		  }
	
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(StockInActivity.this, LandingActivity.class);
		startActivity(i);
		StockInActivity.this.finish();
	}
}