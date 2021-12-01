package com.winapp.fwms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.sot.SalesOrderSetGet;

public class AddSupplier extends Activity implements OnItemClickListener {
	Button addProduct;
	ImageView back;
	Intent callAddProduct, calllogout, callstockin;
	EditText edtdate, edtcode, edsupplierfield, edremarks;
	Calendar myCalendar;
	ArrayList<HashMap<String, String>> suppal;
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetSupplier";
	private static final String SUPPLIER_CODE = "SupplierCode";
	private static final String SUPPLIER_NAME = "SupplierName";
	static String supresult;
	String suppTxt = null, suppliercode, suppliername, OutputData = "",
			productnamesplitted, productnameandcode, productnamereplaced,
			arraylistelement, newarraylistelement, serverdate, valid_url,
			datetime;
	String[] stringArray, finalproductnameandcode, str_array;
	CustomAlertAdapterSupp arrayAdapterSupp;
	int textlength = 0;
	private AlertDialog myalertDialog = null;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> searchResults;
	HashMap<String, String> hashmap = new HashMap<String, String>();
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<String> alsupp = new ArrayList<String>();

	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	LinearLayout addSupplier_layout;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addsupplier_screen);

		addSupplier_layout = (LinearLayout) findViewById(R.id.addSupplier_layout);
		back = (ImageView) findViewById(R.id.back);
		addProduct = (Button) findViewById(R.id.button1);
		edtdate = (EditText) findViewById(R.id.editTextDate);
		edtcode = (EditText) findViewById(R.id.codefield);
		edsupplierfield = (EditText) findViewById(R.id.namefield);
		edremarks = (EditText) findViewById(R.id.editTextRemarks);
		FWMSSettingsDatabase.init(AddSupplier.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,AddSupplier.this);

		myCalendar = Calendar.getInstance();
		AsyncCallWSADDSUP task = new AsyncCallWSADDSUP();
		task.execute();

		edtcode.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
				edtcode) {
			@Override
			public boolean onDrawableClick() {
				alertDialogSearch();
				return true;

			}

		});
		edtcode.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					boolean res = false;
					for (String alphabet : alsupp) {
						if (alphabet.toLowerCase().equals(
								edtcode.getText().toString().toLowerCase())) {
							res = true;
							break;
						}
					}
					if (res == true) {
						Set<Entry<String, String>> keys = hashmap.entrySet();
						Iterator<Entry<String, String>> iterator = keys
								.iterator();
						while (iterator.hasNext()) {
							@SuppressWarnings("rawtypes")
							Entry mapEntry = iterator.next();
							String keyValue = (String) mapEntry.getKey();
							String value = (String) mapEntry.getValue();

							if (edtcode.getText().toString().toLowerCase()
									.equals(keyValue.toLowerCase())) {
								edsupplierfield.setText(value);
							}
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"Invalid Supplier Code", Toast.LENGTH_SHORT)
								.show();
						edtcode.setText("");
						edsupplierfield.setText("");
					}
					edtcode.addTextChangedListener(new TextWatcher() {
						@Override
						public void afterTextChanged(Editable s) {

						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {

						}

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {

							textlength = edtcode.getText().length();
							edsupplierfield.setText("");
						}
					});

				}

				return false;
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				callstockin = new Intent(AddSupplier.this,
						StockInActivity.class);
				startActivity(callstockin);
				AddSupplier.this.finish();
			}
		});

		final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {

				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateLabel();
			}
		};

		edtdate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(AddSupplier.this, date, myCalendar
							.get(Calendar.YEAR),
							myCalendar.get(Calendar.MONTH), myCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});

		addProduct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (edtcode.getText().length() != 0
						&& edtcode.getText().toString() != ""
						&& edsupplierfield.getText().length() != 0
						&& edsupplierfield.getText().toString() != "") {
					String keyS = "FWMS";
					callAddProduct = new Intent(AddSupplier.this,
							AddProduct.class);
					callAddProduct.putExtra("key", keyS);
					startActivity(callAddProduct);
					AddSupplier.this.finish();
					getSupplierCR();
					supplierSetGet();
				} else {
					Toast.makeText(getApplicationContext(),
							" Please Enter Supplier Code ", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

	}

	public void alertDialogSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(AddSupplier.this);
		final EditText editText = new EditText(AddSupplier.this);
		final ListView listview = new ListView(AddSupplier.this);
		LinearLayout layout = new LinearLayout(AddSupplier.this);
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(AddSupplier.this, al);
		listview.setAdapter(arrayAdapterSupp);
		listview.setOnItemClickListener(AddSupplier.this);

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

				arrayAdapterSupp = new CustomAlertAdapterSupp(AddSupplier.this,
						searchResults);
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

	private void enableViews(View v, boolean enabled) {
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				enableViews(vg.getChildAt(i), enabled);
			}
		}
		v.setEnabled(enabled);
	}

	private class AsyncCallWSADDSUP extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			al.clear();

			spinnerLayout = new LinearLayout(AddSupplier.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(addSupplier_layout, false);
			progressBar = new ProgressBar(AddSupplier.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));

			spinnerLayout.addView(progressBar);

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			serverdate = DateWebservice.getDateService("fncGetServerDate");
			Log.d("serverdate", serverdate);

			SoapObject request = new SoapObject(NAMESPACE, webMethName);

			PropertyInfo companyCode = new PropertyInfo();

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

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
				androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
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

						suppliercode = jsonChildNode.optString(SUPPLIER_CODE)
								.toString();
						suppliername = jsonChildNode.optString(SUPPLIER_NAME)
								.toString();

						OutputData += "Node:" + i + ":" + suppliercode + "|"
								+ suppliername;
						HashMap<String, String> supplierhm = new HashMap<String, String>();
						supplierhm.put(suppliercode, suppliername);
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

			gettingSetGet();
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(addSupplier_layout, true);
		}
	}

	private void updateLabel() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		edtdate.setText(sdf.format(myCalendar.getTime()));
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
			String values = (String) mapEntry.getValue();
			edtcode.setText(keyValues);
			edsupplierfield.setText(values);
			edtcode.addTextChangedListener(new TextWatcher() {
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

					textlength = edtcode.getText().length();
					edsupplierfield.setText("");

				}
			});
		}
	}

	public void getSupplierCR() {
		String suppcode = edtcode.getText().toString();
		SupplierSetterGetter.setSuppliercode(suppcode);
		String getsupp = SupplierSetterGetter.getSuppliercode();
		System.out.println("Suppliercode" + getsupp);
		Log.d("Suppliercode", getsupp);

		String servdate = edtdate.getText().toString();

		SupplierSetterGetter.setDate(servdate);
		String suppdate = SupplierSetterGetter.getDate();
		System.out.println("suppdate" + suppdate);
		Log.d("suppdate", suppdate);

		String stremarks = edremarks.getText().toString();
		SupplierSetterGetter.setRemarks(stremarks);
		String getrek = SupplierSetterGetter.getRemarks();
		System.out.println("Remarks:" + getrek);
		Log.d("Remarks", getrek);

	}

	public void supplierSetGet() {

		SalesOrderSetGet.setSaleorderdate(edtdate.getText().toString());
		SalesOrderSetGet.setCustomercode(edtcode.getText().toString());
		SalesOrderSetGet.setCustomername(edsupplierfield.getText().toString());
		SalesOrderSetGet.setRemarks(edremarks.getText().toString());
	}

	public void gettingSetGet() {
		edtcode.setText(SalesOrderSetGet.getCustomercode());
		edsupplierfield.setText(SalesOrderSetGet.getCustomername());
		edtdate.setText(SalesOrderSetGet.getSaleorderdate());
		edremarks.setText(SalesOrderSetGet.getRemarks());

		if (SalesOrderSetGet.getSaleorderdate().equals("")) {
			edtdate.setText(serverdate);
		} else {
			edtdate.setText(SalesOrderSetGet.getSaleorderdate());
		}
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(AddSupplier.this, StockInActivity.class);
		startActivity(i);
		AddSupplier.this.finish();
	}
}
