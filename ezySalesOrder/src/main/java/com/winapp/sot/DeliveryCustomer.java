package com.winapp.sot;

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

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;

public class DeliveryCustomer extends SherlockFragmentActivity implements
SlideMenuFragment.MenuClickInterFace {
	
	EditText delivedcodefield, delivednamefield, edtdate, edcrrncycd,
			edcrrncynm, edcrrncyrt, edremarks;
	CustomAlertAdapterSupp arrayAdapterSupp;
	int textlength = 0;
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	LinearLayout delivery_layout,customer_layout;
	Calendar myCalendar;
	String serverdate, settingvalue, crrncynm, crrncyrt, crrncycode,
			crrncyrate, currencyname, select_van="";
	String valid_url;
	private AlertDialog myalertDialog = null;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	ArrayList<String> aldelivery = new ArrayList<String>();
	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> getArraylsitcur = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> searchResults;
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetCustomer";
	static String custresult;
	String suppTxt = null;
	ArrayList<String> alclcrrncy = new ArrayList<String>();
	ArrayList<String> cstmrgrpcdal = new ArrayList<String>();
	ArrayList<HashMap<String, String>> currencycode = new ArrayList<HashMap<String, String>>();
	TextView listing_screen, customer_screen, addProduct_screen,summary_screen;
	HashMap<String, String> hashmap = new HashMap<String, String>();
	Button deliv_addProduct;
	private SlidingMenu menu;
	ImageButton search, back, printer,addnew;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(R.drawable.ic_menu);
		setContentView(R.layout.delivery_customer);
		
		 ActionBar ab = getSupportActionBar();
		  ab.setHomeButtonEnabled(true);
		  View customNav = LayoutInflater.from(this).inflate(
		  R.layout.slidemenu_actionbar_title, null);
		  TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		  txt.setText("Delivery Order Customer");
		  
		  search = (ImageButton) customNav.findViewById(R.id.search_img);
		  printer = (ImageButton) customNav.findViewById(R.id.printer);
		  addnew = (ImageButton) customNav.findViewById(R.id.custcode_img);
		  
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
		  
		  addnew.setVisibility(View.INVISIBLE);
		  search.setVisibility(View.GONE);
		  printer.setVisibility(View.GONE);
		
		delivery_layout = (LinearLayout) findViewById(R.id.delivOrderCust_layout);
		delivedcodefield = (EditText) findViewById(R.id.deli_codefield);
		delivednamefield = (EditText) findViewById(R.id.deli_namefield);
		edcrrncycd = (EditText) findViewById(R.id.deli_edCurcode);
		edtdate = (EditText) findViewById(R.id.editTextDate);

		edcrrncynm = (EditText) findViewById(R.id.deli_edCurlbl);
		edcrrncyrt = (EditText) findViewById(R.id.deli_edCurRate);
		edremarks = (EditText) findViewById(R.id.deli_editTextRemarks);
//		back = (ImageView) findViewById(R.id.back);
		deliv_addProduct = (Button) findViewById(R.id.deli_addProduct);
		
		listing_screen = (TextView) findViewById(R.id.listing_screen);
		customer_screen = (TextView) findViewById(R.id.customer_screen);
		addProduct_screen = (TextView) findViewById(R.id.addProduct_screen);
		summary_screen= (TextView) findViewById(R.id.sum_screen);
		customer_layout = (LinearLayout) findViewById(R.id.customer_layout);
		
		customer_layout.setVisibility(View.GONE);
//		customer_screen.setBackgroundColor(Color.parseColor("#00AFF0"));

		customer_screen.setTextColor(Color.parseColor("#FFFFFF"));
		customer_screen.setBackgroundResource(drawable.tab_select);
		
		myCalendar = Calendar.getInstance();
		al.clear();
		currencycode.clear();
		cstmrgrpcdal.clear();
		FWMSSettingsDatabase.init(DeliveryCustomer.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,DeliveryCustomer.this);
		new SalesOrderWebService(valid_url);
		
		SOTDatabase.init(DeliveryCustomer.this);
		select_van = SOTDatabase.getVandriver();
	 	
		if(select_van!=null && !select_van.isEmpty()){			
		}else{
			select_van="";
		}

//		String edit_do = ConvertToSetterGetter.getEdit_do_no();
		Cursor cursr = SOTDatabase.getCursor();
		int productCount = cursr.getCount();
		Log.d("productCount", ""+productCount);

		if(productCount>0){
			delivedcodefield.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			delivedcodefield.setBackgroundResource(R.drawable.labelbg);
			delivedcodefield.setFocusable(false);
		}else{
			delivedcodefield.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0);
			delivedcodefield.setBackgroundResource(R.drawable.edittext_bg);
			delivedcodefield.setFocusable(true);
		}

		AsyncCallWSSaleOrder inAcws = new AsyncCallWSSaleOrder();
		inAcws.execute();

		listing_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Cursor cursor = SOTDatabase.getCursor();
				int count = cursor.getCount();
				if(count>0){
					alertDialog();
				}else{
					
					Intent i = new Intent(DeliveryCustomer.this, DeliveryOrderHeader.class);
					startActivity(i);
					DeliveryCustomer.this.finish();

				}
				
				

			}
		});

		addProduct_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (delivedcodefield.getText().length() != 0
						&& delivedcodefield.getText().toString() != ""
						&& delivednamefield.getText().length() != 0
						&& delivednamefield.getText().toString() != "") {
					AsyncCallWSCustGrop task = new AsyncCallWSCustGrop();
					task.execute("Add Product");
					customerSetGet();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please Enter CustomerCode ", Toast.LENGTH_SHORT)
							.show();
				}
			

			}
		});

		summary_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (delivedcodefield.getText().length() != 0
						&& delivedcodefield.getText().toString() != ""
						&& delivednamefield.getText().length() != 0
						&& delivednamefield.getText().toString() != "") {
					AsyncCallWSCustGrop task = new AsyncCallWSCustGrop();
					task.execute("Summary");
					customerSetGet();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please Enter CustomerCode ", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		
//		back.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent i = new Intent(DeliveryCustomer.this,
//						DeliveryOrderHeader.class);
//				startActivity(i);
//				DeliveryCustomer.this.finish();
//			}
//		});

		deliv_addProduct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (delivedcodefield.getText().length() != 0
						&& delivedcodefield.getText().toString() != ""
						&& delivednamefield.getText().length() != 0
						&& delivednamefield.getText().toString() != "") {
					AsyncCallWSCustGrop task = new AsyncCallWSCustGrop();
					task.execute("Add Product");
					customerSetGet();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please Enter CustomerCode ", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		delivedcodefield
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						delivedcodefield) {
					@Override
					public boolean onDrawableClick() {
						custalertDialogSearch();
						return true;

					}

				});
		edcrrncycd
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						delivedcodefield) {
					@Override
					public boolean onDrawableClick() {
						currencyalertdialog();
						return true;

					}

				});

		delivedcodefield
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {

						if (actionId == EditorInfo.IME_ACTION_SEARCH
								|| actionId == EditorInfo.IME_ACTION_DONE
								|| event.getAction() == KeyEvent.ACTION_DOWN
								&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

							boolean res = false;
							for (String alphabet : aldelivery) {
								if (alphabet.toLowerCase().equals(
										delivedcodefield.getText().toString()
												.toLowerCase())) {
									res = true;
									break;
								}
							}
							if (res == true) {
								Set<Entry<String, String>> keys = hashmap
										.entrySet();
								Iterator<Entry<String, String>> iterator = keys
										.iterator();
								while (iterator.hasNext()) {
									@SuppressWarnings("rawtypes")
									Entry mapEntry = iterator.next();
									String keyValue = (String) mapEntry
											.getKey();
									String value = (String) mapEntry.getValue();
									if (delivedcodefield.getText().toString()
											.toLowerCase()
											.equals(keyValue.toLowerCase())) {
										delivednamefield.setText(value);
									}
								}
							} else {
								Toast.makeText(getApplicationContext(),
										"Invalid Customer Code",
										Toast.LENGTH_SHORT).show();
								delivedcodefield.setText("");
								delivednamefield.setText("");
							}
							delivedcodefield
									.addTextChangedListener(new TextWatcher() {
										@Override
										public void afterTextChanged(Editable s) {

										}

										@Override
										public void beforeTextChanged(
												CharSequence s, int start,
												int count, int after) {

										}

										@Override
										public void onTextChanged(
												CharSequence s, int start,
												int before, int count) {

											textlength = delivedcodefield
													.getText().length();
											delivednamefield.setText("");
										}
									});

						}

						return false;
					}
				});
		final DatePickerDialog.OnDateSetListener indate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				inDate();
			}
		};

		edtdate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(DeliveryCustomer.this, indate,
							myCalendar.get(Calendar.YEAR), myCalendar
									.get(Calendar.MONTH), myCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});

	}

	private void inDate() {

		String myFormat = "dd/MM/yyyy"; // In which you need put here
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		edtdate.setText(sdf.format(myCalendar.getTime()));

	}

	public void custalertDialogSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				DeliveryCustomer.this);
		final EditText editText = new EditText(DeliveryCustomer.this);
		final ListView listview = new ListView(DeliveryCustomer.this);
		LinearLayout layout = new LinearLayout(DeliveryCustomer.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Customer");
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
		arrayAdapterSupp = new CustomAlertAdapterSupp(DeliveryCustomer.this, al);
		listview.setAdapter(arrayAdapterSupp);
		// listview.setOnItemClickListener(NewCustomer.this);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

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

					Log.d("onclick loc", ""+values);
					
					if(values.contains("~")){
						String[] parts = values.split("~");
						String name = parts[0];
//						String location = parts[1];

						delivedcodefield.setText(keyValues);
						delivednamefield.setText(name);
					}else{
						delivedcodefield.setText(keyValues);
						delivednamefield.setText(values);
					}
					
//					delivedcodefield.setText(keyValues);
//					delivednamefield.setText(values);

					delivedcodefield.addTextChangedListener(new TextWatcher() {
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

							textlength = delivedcodefield.getText().length();
							delivednamefield.setText("");

						}
					});
				}
			}

		});
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
						DeliveryCustomer.this, searchResults);
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

	public void currencyalertdialog() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				DeliveryCustomer.this);
		final EditText editText = new EditText(DeliveryCustomer.this);
		final ListView listview = new ListView(DeliveryCustomer.this);
		LinearLayout layout = new LinearLayout(DeliveryCustomer.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Currency");
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
		arrayAdapterSupp = new CustomAlertAdapterSupp(DeliveryCustomer.this,
				currencycode);
		listview.setAdapter(arrayAdapterSupp);
		// listview.setOnItemClickListener(NewCustomer.this);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				myalertDialog.dismiss();
				getArraylsitcur = arrayAdapterSupp.getArrayList();
				HashMap<String, String> datavalue = getArraylsitcur
						.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String keyValues = (String) mapEntry.getKey();
					String values = (String) mapEntry.getValue();
					edcrrncycd.setText(keyValues);
					// edcrrncynm.setText(values);
					crrncycode = keyValues;
					currencyname = values;
					AsyncCallWSCurrcode taskcc = new AsyncCallWSCurrcode();
					taskcc.execute();
					edcrrncycd.addTextChangedListener(new TextWatcher() {
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

							textlength = edcrrncycd.getText().length();
							edcrrncynm.setText("");
							edcrrncyrt.setText("");

						}
					});
				}
			}
		});
		searchResults = new ArrayList<HashMap<String, String>>(currencycode);
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
				for (int i = 0; i < currencycode.size(); i++) {
					String custName = currencycode.get(i).toString();
					if (textlength <= custName.length()) {
						if (custName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(currencycode.get(i));
					}
				}

				arrayAdapterSupp = new CustomAlertAdapterSupp(
						DeliveryCustomer.this, searchResults);
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

	private class AsyncCallWSSaleOrder extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			al.clear();
			currencycode.clear();
			spinnerLayout = new LinearLayout(DeliveryCustomer.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(delivery_layout, false);
			progressBar = new ProgressBar(DeliveryCustomer.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			serverdate = DateWebservice.getDateService("fncGetServerDate");
			currencycode = SalesOrderWebService
					.getAllCurrency("fncGetCurrency");

			SoapObject request = new SoapObject(NAMESPACE, webMethName);
//			PropertyInfo companyCode = new PropertyInfo();
//
			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
//
//			companyCode.setName("CompanyCode");
//			companyCode.setValue(cmpnyCode);
//			companyCode.setType(String.class);
//			request.addProperty(companyCode);
			
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("CompanyCode", cmpnyCode);
			hm.put("VanCode", select_van);
			
			for (Entry<String, String> entry : hm.entrySet()) {

				PropertyInfo prodCode = new PropertyInfo();

				prodCode.setName(entry.getKey());
				prodCode.setValue(entry.getValue());
				prodCode.setType(String.class);
				request.addProperty(prodCode);

				System.out.printf("%s", entry.getKey());
				System.out.printf("%s%n", entry.getValue());
			}


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
				custresult = " { SaleOCustomer : " + suppTxt + "}";
				JSONObject jsonResponse;
				try {
					jsonResponse = new JSONObject(custresult);
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("SaleOCustomer");
					int lengthJsonArr = jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {
						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);

						String customercode = jsonChildNode.optString(
								"CustomerCode").toString();
						String customername = jsonChildNode.optString(
								"CustomerName").toString();

						String ReferenceLocation = jsonChildNode.optString(
								"ReferenceLocation").toString();
					
						Log.d("ReferenceLocation", ""+ReferenceLocation);
						
						HashMap<String, String> customerhm = new HashMap<String, String>();
						if(ReferenceLocation!=null && !ReferenceLocation.isEmpty()){
							customerhm.put(customercode, customername+"~"+ReferenceLocation);
						}else{
							customerhm.put(customercode, customername);	
						}
						
//						HashMap<String, String> customerhm = new HashMap<String, String>();
//						customerhm.put(customercode, customername);
						al.add(customerhm);
						hashmap.putAll(customerhm);
						aldelivery.add(customercode);

					}
					settingvalue = SalesOrderWebService
							.generalSettingsService("fncGetGeneralSettings");
					alclcrrncy = SalesOrderWebService.getCurrencyValues(
							"fncGetCurrency", settingvalue);
					crrncynm = alclcrrncy.get(0);
					crrncyrt = alclcrrncy.get(1);

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
			/*
			 * delivedcodefield.setText(SalesOrderSetGet.getCustomercode());
			 * delivednamefield.setText(SalesOrderSetGet.getCustomername());
			 * edtdate.setText(serverdate); edcrrncycd.setText(settingvalue);
			 * edcrrncynm.setText(crrncynm); edcrrncyrt.setText(crrncyrt);
			 */
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(delivery_layout, true);
		}
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

	private class AsyncCallWSCustGrop extends AsyncTask<String, Void, Void> {
		@SuppressWarnings("deprecation")
		String navigateTo="";
		@Override
		protected void onPreExecute() {
			cstmrgrpcdal.clear();
			spinnerLayout = new LinearLayout(DeliveryCustomer.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(delivery_layout, false);
			progressBar = new ProgressBar(DeliveryCustomer.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(String... arg) {
			
			navigateTo = arg[0]; 
			
			try {
				cstmrgrpcdal = SalesOrderWebService
						.getCustGroupCode(
								delivedcodefield.getText().toString(),
								"fncGetCustomer");
				SalesOrderWebService.getCustomerTax(delivedcodefield.getText()
						.toString(), "fncGetCustomer");
				Log.d("Customer values", "" + cstmrgrpcdal);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(delivery_layout, true);


			SalesOrderSetGet.setSuppliercode(cstmrgrpcdal.get(0));
			SalesOrderSetGet.setSuppliergroupcode(cstmrgrpcdal.get(1));

			if(navigateTo.matches("Add Product")){
				Intent i = new Intent(DeliveryCustomer.this,
						DeliveryAddProduct.class);
			
				startActivity(i);
				DeliveryCustomer.this.finish();
			}else if(navigateTo.matches("Summary")){
				Intent i = new Intent(DeliveryCustomer.this,
						DeliverySummary.class);
			
				startActivity(i);
				DeliveryCustomer.this.finish();
			}else{
				Intent i = new Intent(DeliveryCustomer.this,
						DeliveryAddProduct.class);
			
				startActivity(i);
				DeliveryCustomer.this.finish();
			}
		
		}
	}

	private class AsyncCallWSCurrcode extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(DeliveryCustomer.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(delivery_layout, false);
			progressBar = new ProgressBar(DeliveryCustomer.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				crrncyrate = SalesOrderWebService.getCrrncyRate(crrncycode,
						"fncGetCurrency");
				Log.d("crrncycode", crrncycode);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			edcrrncynm.setText(currencyname);
			edcrrncyrt.setText(crrncyrate);
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(delivery_layout, true);
		}
	}

	public void customerSetGet() {
		SalesOrderSetGet.setCurrencycode(edcrrncycd.getText().toString());
		SalesOrderSetGet.setSaleorderdate(edtdate.getText().toString());
		SalesOrderSetGet.setCustomercode(delivedcodefield.getText().toString());
		SalesOrderSetGet.setCustomername(delivednamefield.getText().toString());
		SalesOrderSetGet.setCurrencyrate(edcrrncyrt.getText().toString());
		SalesOrderSetGet.setRemarks(edremarks.getText().toString());
	}

	public void gettingSetGet() {
		delivedcodefield.setText(SalesOrderSetGet.getCustomercode());
		delivednamefield.setText(SalesOrderSetGet.getCustomername());
		edremarks.setText(SalesOrderSetGet.getRemarks());

		if (SalesOrderSetGet.getSaleorderdate().equals("")) {
			edtdate.setText(serverdate);
		} else {
			edtdate.setText(SalesOrderSetGet.getSaleorderdate());
		}

		if (SalesOrderSetGet.getCurrencycode().equals("")) {
			edcrrncycd.setText(settingvalue);
		} else {
			edcrrncycd.setText(SalesOrderSetGet.getCurrencycode());
		}
		if (SalesOrderSetGet.getCurrencyname().equals("")) {
			edcrrncynm.setText(crrncynm);
		} else {
			edcrrncynm.setText(SalesOrderSetGet.getCurrencyname());
		}
		if (SalesOrderSetGet.getCurrencyrate().equals("")) {
			edcrrncyrt.setText(crrncyrt);
		} else {
			edcrrncyrt.setText(SalesOrderSetGet.getCurrencyrate());
		}
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(DeliveryCustomer.this, DeliveryOrderHeader.class);
		startActivity(i);
		DeliveryCustomer.this.finish();
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
	
	 public void alertDialog() {

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle("Deleting");
			alertDialog.setMessage("Delivery Order products will clear. Do you want to proceed");
			alertDialog.setIcon(R.mipmap.ic_exit);
			alertDialog.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent i = new Intent(DeliveryCustomer.this, DeliveryOrderHeader.class);
							startActivity(i);
							DeliveryCustomer.this.finish();
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
	
}
