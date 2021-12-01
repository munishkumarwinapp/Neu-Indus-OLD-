package com.winapp.sot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.adapter.SearchAdapter.ViewHolder;
import com.winapp.SFA.R;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.DateTime;
import com.winapp.model.Product;
import com.winapp.printer.UIHelper;
import com.winapp.sot.MerchandiseHeader.MerchandiseAdapter;
import com.winapp.util.CustomCalendar;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MerchandiseAddSchedule extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace, OnClickListener {

	private SlidingMenu menu;
	private ActionBar mActionBar;
	private View customActionBarView;
	private TextView mTitle,mVanName;
	private Intent mIntent;
	private ImageButton mPrinterImgBtn, mAddImgBtn, mSearchImgBtn;
	private Calendar mCalendar;
	private SimpleDateFormat mSimpleDateFormat;
	private DatePickerDialog.OnDateSetListener mDatePicker;
	private String vanName="",dateFormat = "dd/MM/yyyy",mScheduleNo="", mValidUrl = "", mScheduleDate = "",
			mCompanyCodeStr = "", mSaveSchedulingJsonString="",mScheduleUser="",mUserIdStr="",
			mUserJsonString = "", mCustomerAddressJsonString = "",mVanJsonString="",selectedVan="";
	private EditText mDateEdt, mUserEdt,mCustomerNameFilterEdt,mUserVanFilterEdt;
	private LinearLayout mMerchandiseMainLayout;
	private UIHelper helper;
	private HashMap<String, String> mHashMap;
	private JSONObject  mUserJsonObject,mSaveSchedulingJsonObject,
			mCustomerAddressJsonObject,mVanJsonObject;
	private JSONArray mVanJsonArray, mUserJsonArray,mSaveSchedulingJsonArray,
			mCustomerAddressJsonArray;
	private AlertDialog myalertDialog = null;
	private ArrayList<HashMap<String, String>> getArraylsit, searchResults,
			userArrHm,vanArrHm,getVanArraylsit;
	private CustomAlertAdapterSupp arrayAdapterSupp;
	private int textlength = 0;
	private CustomerAddressAdapter mCustomerAddressAdapter;
	private ArrayList<SO> mCustomerAddressData;
	private ListView mListView;
	private Bundle mBundle;
    CustomCalendar customCalendar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(R.drawable.ic_menu);
		setContentView(R.layout.activity_merchandise_add_schedule);
		setUpView();
	}

	private void setUpView() {
		// ActionBar
		mActionBar = getSupportActionBar();
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayHomeAsUpEnabled(false);
		mActionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.header_bg));
		// Custom ActionBar View
		customActionBarView = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		mTitle = (TextView) customActionBarView.findViewById(R.id.pageTitle);
		mTitle.setText("Merchandise Add Schedule");
		// Custom ActionBarView IB
		mPrinterImgBtn = (ImageButton) customActionBarView
				.findViewById(R.id.printer);
		mAddImgBtn = (ImageButton) customActionBarView
				.findViewById(R.id.custcode_img);
		mSearchImgBtn = (ImageButton) customActionBarView
				.findViewById(R.id.search_img);
		mPrinterImgBtn.setVisibility(View.GONE);
		mSearchImgBtn.setVisibility(View.GONE);
		mAddImgBtn.setVisibility(View.VISIBLE);

		mAddImgBtn.setImageResource(R.mipmap.save_ic);
		mSearchImgBtn.setImageResource(R.mipmap.search_ic);

		// Set customActionView
		mActionBar.setCustomView(customActionBarView);

		// View ID
		mVanName = (TextView) findViewById(R.id.van_name);
		mDateEdt = (EditText) findViewById(R.id.date);
		mUserEdt = (EditText) findViewById(R.id.user);		
		mUserVanFilterEdt = (EditText) findViewById(R.id.user_van_filter);
		mCustomerNameFilterEdt = (EditText) findViewById(R.id.customerName_filter);
		mListView = (ListView) findViewById(R.id.listView);
		
		mMerchandiseMainLayout = (LinearLayout) findViewById(R.id.merchandiseMainLayout);

		// Sherlock SlideMenu
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);

		// Object Initialization
		mIntent = new Intent();
		helper = new UIHelper(MerchandiseAddSchedule.this);
		mSimpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
		mHashMap = new HashMap<String, String>();
		getArraylsit = new ArrayList<HashMap<String, String>>();
		userArrHm = new ArrayList<HashMap<String, String>>();
		vanArrHm = new ArrayList<HashMap<String, String>>();
		mCustomerAddressData = new ArrayList<SO>();
		getVanArraylsit = new ArrayList<HashMap<String, String>>();
		// Get URL from DB
		FWMSSettingsDatabase.init(MerchandiseAddSchedule.this);
		mValidUrl = FWMSSettingsDatabase.getUrl();
		new SalesOrderWebService(mValidUrl);

		// Get CompanyCode from Pojo class
		mCompanyCodeStr = SupplierSetterGetter.getCompanyCode();
		
		
		// Get UserId from Pojo class
		mUserIdStr = SupplierSetterGetter.getUsername();

		mCalendar = Calendar.getInstance();
		
		
		mBundle = getIntent().getExtras();
		if(mBundle!=null){
			@SuppressWarnings("unchecked")
			HashMap<String,String> hm =  (HashMap<String, String>) mBundle.getSerializable("Values");				
			loadBundleValue(hm);
			
		}else{
			new LoadMerchandiseAddScheduleData(false).execute();
		}
		
		// get Merchandise Schedule Date
		mScheduleDate = SalesOrderSetGet.getServerDate();
		mDateEdt.setText(mScheduleDate);

		// OnClickListener
		mAddImgBtn.setOnClickListener(this);
		
		
		mListView.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
		
		
		selectedVan = SOTDatabase.getVandriver();
		
		
		if(selectedVan!=null && !selectedVan.isEmpty()){
			mUserVanFilterEdt.setText(selectedVan);
			mUserVanFilterEdt.setEnabled(false);
			mUserVanFilterEdt.setFocusable(false);
			mUserVanFilterEdt.setFocusableInTouchMode(false);
			mUserVanFilterEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0,0,0);
			mUserVanFilterEdt.setBackgroundResource(R.drawable.labelbg);
			//Get VanName from DB
			   vanName = SOTDatabase.getVandriverName(selectedVan);
			   mVanName.setText(vanName);
			   mUserVanFilterEdt.setVisibility(View.GONE);
			   mVanName.setVisibility(View.VISIBLE);
		}
		else{
			mUserVanFilterEdt.setBackgroundResource(R.drawable.edittext_bg);
			mUserVanFilterEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_search,0);			
			mUserVanFilterEdt.setEnabled(true);
			mUserVanFilterEdt.setFocusable(true);
			mUserVanFilterEdt.setFocusableInTouchMode(true);
			mUserVanFilterEdt.setVisibility(View.GONE);
			mVanName.setVisibility(View.GONE);
		}

		mDateEdt.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(MerchandiseAddSchedule.this, mDatePicker,
							mCalendar.get(Calendar.YEAR), mCalendar
									.get(Calendar.MONTH), mCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		
     	mUserEdt.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				mUserEdt.requestFocus();
				mUserEdt.setFocusable(true);
				mUserEdt.setCursorVisible(mUserEdt.hasFocus());
				return false;
			}
		});
     	mCustomerNameFilterEdt.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				mCustomerNameFilterEdt.requestFocus();
				mCustomerNameFilterEdt.setFocusable(true);
				mCustomerNameFilterEdt.setCursorVisible(mCustomerNameFilterEdt.hasFocus());
				
				return false;
			}
		});
     	mUserVanFilterEdt.setOnTouchListener(new OnTouchListener() {
	
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		mUserVanFilterEdt.setFocusable(true);
		return false;
	}
});
		mDatePicker = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				mCalendar.set(Calendar.YEAR, year);
				mCalendar.set(Calendar.MONTH, monthOfYear);
				mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				mDateEdt.setText(mSimpleDateFormat.format(mCalendar.getTime()));
			}
		};
		mUserEdt.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
				mUserEdt) {
			@Override
			public boolean onDrawableClick() {
				alertDialogUser();
				return true;
			}
		});
		mUserVanFilterEdt.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
				mUserVanFilterEdt) {
			@Override
			public boolean onDrawableClick() {
				alertDialogVan();
				return true;
			}
		});
		  mCustomerNameFilterEdt.addTextChangedListener(new TextWatcher() {

			  public void afterTextChanged(Editable s) {
			  }

			  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			  }

			  public void onTextChanged(CharSequence s, int start, int before, int count) {
				  String vanfilter = mUserVanFilterEdt.getText().toString();
				  String customerfilter = s.toString();
				  if(vanfilter!=null && !vanfilter.isEmpty() && customerfilter!=null && !customerfilter.isEmpty()){
					  mCustomerAddressAdapter.setFilterType("Customer && Van");
					  mCustomerAddressAdapter.getFilter().filter(customerfilter + "," + vanfilter);
					  
					  Log.d("Customer && Van1", "Customer && Van");
				  }else if(customerfilter!=null && !customerfilter.isEmpty()){
					  mCustomerAddressAdapter.setFilterType("Customer");
					  mCustomerAddressAdapter.getFilter().filter(customerfilter);
					  Log.d("Customer1", "Customer");
				  }else if(vanfilter!=null && !vanfilter.isEmpty()){
					  mCustomerAddressAdapter.setFilterType("Van");
					  mCustomerAddressAdapter.getFilter().filter(vanfilter);
					  Log.d("Van1", "Van");
				  }
				  else if (customerfilter.length() == 0 && vanfilter.length() == 0) {
					  mCustomerAddressAdapter.setFilterType("");
					  mCustomerAddressAdapter.getFilter().filter(null);
					}			 
			  }
			  });
		  
		  	mUserVanFilterEdt.addTextChangedListener(new TextWatcher() {

			  public void afterTextChanged(Editable s) {
			  }

			  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			  }

			  public void onTextChanged(CharSequence s, int start, int before, int count) {
				  String customerfilter = mCustomerNameFilterEdt.getText().toString();
				  String vanfilter = s.toString();
				  if(customerfilter!=null && !customerfilter.isEmpty() && vanfilter!=null && !vanfilter.isEmpty()){
					  mCustomerAddressAdapter.setFilterType("Customer && Van");
					  mCustomerAddressAdapter.getFilter().filter(customerfilter + "," + vanfilter);
					  Log.d("Customer && Van2", "Customer && Van");
				  }else if(vanfilter!=null && !vanfilter.isEmpty()){
					  mCustomerAddressAdapter.setFilterType("Van");
					  mCustomerAddressAdapter.getFilter().filter(vanfilter);
					  Log.d("Van2", "Van");
				  }else if(customerfilter!=null && !customerfilter.isEmpty()){
					  mCustomerAddressAdapter.setFilterType("Customer");
					  mCustomerAddressAdapter.getFilter().filter(customerfilter);
					  Log.d("Customer2", "Customer");
				  }else if (customerfilter.length() == 0 && vanfilter.length() == 0) {
					  mCustomerAddressAdapter.setFilterType("");
					  mCustomerAddressAdapter.getFilter().filter(null);
					}	
			  }
			  });
		  
			
	}

	private void loadBundleValue(HashMap<String, String> hm) {
		mScheduleNo = hm.get("ScheduleNo");
		String scheduleDate = hm.get("ScheduleDate");
		String scheduleUser = hm.get("ScheduleUser");
		String customerCode = hm.get("CustomerCode");
		String customerName = hm.get("CustomerName");
		String customerAddress = hm.get("CustomerAddress");
		String outletName = hm.get("OutletName");
		String outletAddress = hm.get("OutletAddress");
		String deliveryCode = hm.get("DeliveryCode");
		String sortOrder = hm.get("SortOrder");		
		int orderNo = sortOrder.equals("") ? 0 : Integer.valueOf(sortOrder);
		SO so = new SO();
		so.setCustomerCode(customerCode);
		so.setCustomerName(customerName);
		so.setAddress(customerAddress);
		so.setOutLetName(outletName);
		so.setOutletAddress(outletAddress);
		so.setDeliveryCode(deliveryCode);		
		so.setVanCode("");
		so.setOrderNo(orderNo);
		mCustomerAddressData.add(so);
		mUserEdt.setText(scheduleUser);
		mDateEdt.setText(scheduleDate);
		
		new LoadMerchandiseAddScheduleData(true).execute();
		//mDateEdt.setEnabled(false);
		//mUserEdt.setEnabled(false);
		/*mCustomerAddressAdapter = new CustomerAddressAdapter(
				MerchandiseAddSchedule.this,
				R.layout.list_item_merchandise_add_schedule,
				mCustomerAddressData);
		mListView.setAdapter(mCustomerAddressAdapter);*/

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.custcode_img:		
			mScheduleUser = mUserEdt.getText().toString();
			if(mScheduleUser!=null && !mScheduleUser.isEmpty()){
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
				alertDialog.setTitle("Save");
				alertDialog.setMessage("Do you want to Save");
				alertDialog.setIcon(R.mipmap.save_ic);
				alertDialog.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mCustomerAddressAdapter.saveScheduling();
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
				
			}else{
				Toast.makeText(MerchandiseAddSchedule.this, "Please Select the User", Toast.LENGTH_SHORT).show();
			}
			
			
			break;
		/*case R.id.date:				
			try{
			customCalendar = new CustomCalendar(MerchandiseAddSchedule.this);
			 customCalendar.showCalendarView();
			 boolean mstatus = customCalendar.showDialog();
			 Log.d("mstatus", mstatus+"");
			 if (mstatus == true) {
				 String sDate = customCalendar.getSelectDate();
				 mDateEdt.setText(sDate);
				} else {
					Log.d("False", "-->" + "False");
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			break;*/
		default:
			break;
		}

	}

	public void alertDialogUser() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				MerchandiseAddSchedule.this);
		final EditText editText = new EditText(MerchandiseAddSchedule.this);
		final ListView listview = new ListView(MerchandiseAddSchedule.this);
		LinearLayout layout = new LinearLayout(MerchandiseAddSchedule.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("User");
		editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0,
				0, 0);
		layout.addView(editText);
		layout.addView(listview);
		myDialog.setView(layout);

		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(
				MerchandiseAddSchedule.this, userArrHm);
		listview.setAdapter(arrayAdapterSupp);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				hideKeyboard(editText);
				myalertDialog.dismiss();
				getArraylsit = arrayAdapterSupp.getArrayList();
				HashMap<String, String> datavalue = getArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					//mapEntry.getKey();
					String Values = (String) mapEntry.getValue();
					
					mUserEdt.setText(Values);
					mUserEdt.addTextChangedListener(new TextWatcher() {
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
							textlength = mUserEdt.getText().length();
						}
					});
				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(userArrHm);
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
				for (int i = 0; i < userArrHm.size(); i++) {
					String supplierName = userArrHm.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(userArrHm.get(i));
					}
				}

				arrayAdapterSupp = new CustomAlertAdapterSupp(
						MerchandiseAddSchedule.this, searchResults);
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
	public void alertDialogVan() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				MerchandiseAddSchedule.this);
		final EditText editText = new EditText(MerchandiseAddSchedule.this);
		final ListView listview = new ListView(MerchandiseAddSchedule.this);
		LinearLayout layout = new LinearLayout(MerchandiseAddSchedule.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Van");
		editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0,
				0, 0);
		layout.addView(editText);
		layout.addView(listview);
		myDialog.setView(layout);

		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
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

		arrayAdapterSupp = new CustomAlertAdapterSupp(
				MerchandiseAddSchedule.this, vanArrHm);
		listview.setAdapter(arrayAdapterSupp);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				hideKeyboard(editText);
				myalertDialog.dismiss();
				getVanArraylsit = arrayAdapterSupp.getArrayList();
				HashMap<String, String> datavalue = getVanArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
				   // mapEntry.getKey();
					String Values = (String) mapEntry.getValue();

					mUserVanFilterEdt.setText(Values);
					mUserVanFilterEdt.addTextChangedListener(new TextWatcher() {
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
							textlength = mUserVanFilterEdt.getText().length();
						}
					});
				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(vanArrHm);
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
				for (int i = 0; i < vanArrHm.size(); i++) {
					String supplierName = vanArrHm.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(vanArrHm.get(i));
					}
				}

				arrayAdapterSupp = new CustomAlertAdapterSupp(
						MerchandiseAddSchedule.this, searchResults);
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
	private class LoadMerchandiseAddScheduleData extends
			AsyncTask<Void, Void, Void> {		
		private boolean isEdit;
		private LoadMerchandiseAddScheduleData(boolean edit){
			isEdit = edit;
		}
		@Override
		protected void onPreExecute() {
			userArrHm.clear();
			vanArrHm.clear();
			if(!isEdit){
			mCustomerAddressData.clear();
			}
			helper.showProgressView(mMerchandiseMainLayout);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			mHashMap.put("CompanyCode", mCompanyCodeStr);
			/*try {
				// Call GetServerDate
				mServerDateJsonString = SalesOrderWebService.getSODetail(
						mHashMap, "fncGetServerDate");
				mServerDateJsonObject = new JSONObject(mServerDateJsonString);
				mServerDateJsonArray = mServerDateJsonObject
						.optJSONArray("SODetails");
				JSONObject jsonobject = mServerDateJsonArray.getJSONObject(0);
				mServerDate = jsonobject.getString("ServerDate");

			} catch (Exception e) {
				e.printStackTrace();
			}*/
			// Call GetUser
			try {
				mUserJsonString = SalesOrderWebService.getSODetail(mHashMap,
						"testGetUser");
				mUserJsonObject = new JSONObject(mUserJsonString);
				mUserJsonArray = mUserJsonObject.optJSONArray("SODetails");

				int lengthUserJsonArr = mUserJsonArray.length();

				if (lengthUserJsonArr > 0) {
					for (int i = 0; i < lengthUserJsonArr; i++) {

						HashMap<String, String> hm = new HashMap<String, String>();
						JSONObject jsonChildNode = mUserJsonArray
								.getJSONObject(i);
						String userGroupCode = jsonChildNode.optString(
								"UserGroupCode").toString();
						String userName = jsonChildNode.optString("UserName")
								.toString();
						hm.put(userGroupCode, userName);
						userArrHm.add(hm);

					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(selectedVan!=null && !selectedVan.isEmpty()){
				//Do Nothing
			}else{
			// Call GetVan
			try {
				mVanJsonString = SalesOrderWebService.getSODetail(mHashMap,"fncGetVan");
				mVanJsonObject = new JSONObject(mVanJsonString);
				mVanJsonArray = mVanJsonObject.optJSONArray("SODetails");

				int lengthVanJsonArr = mVanJsonArray.length();

				if (lengthVanJsonArr > 0) {
					for (int i = 0; i < lengthVanJsonArr; i++) {

						HashMap<String, String> hm = new HashMap<String, String>();
						JSONObject jsonChildNode = mVanJsonArray.getJSONObject(i);
						String code = jsonChildNode.optString(
								"Code").toString();
						String description = jsonChildNode.optString("Description")
								.toString();
						hm.put(description,code);
						vanArrHm.add(hm);

					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			}
			if(!isEdit){
			// Call GetCustomerAddress
			try {
				mCustomerAddressJsonString = SalesOrderWebService.getSODetail(
						mHashMap, "fncGetCustomerWithAddress");
				mCustomerAddressJsonObject = new JSONObject(
						mCustomerAddressJsonString);
				mCustomerAddressJsonArray = mCustomerAddressJsonObject
						.optJSONArray("SODetails");

				int lengthCustomerAddressJsonArr = mCustomerAddressJsonArray
						.length();

				if (lengthCustomerAddressJsonArr > 0) {
					for (int i = 0; i < lengthCustomerAddressJsonArr; i++) {
						SO so = new SO();
						JSONObject jsonChildNode = mCustomerAddressJsonArray
								.getJSONObject(i);
						
						String customerCode = jsonChildNode.optString("CustomerCode").toString();						
						String customerName = jsonChildNode.optString("CustomerName").toString();					
						String address1 = jsonChildNode.optString("Address1").toString();	
						String vanCode = jsonChildNode.optString("VanCode").toString();	
						String deliveryCode = jsonChildNode.optString("DeliveryCode").toString();						
						String outletName = jsonChildNode.optString("OutletName").toString();
						String outletAddress1 = jsonChildNode.optString("OutletAddress1").toString();					
					
						if(selectedVan!=null && !selectedVan.isEmpty()){
						if(vanCode.matches(selectedVan)){
							so.setCustomerName(customerName);
							so.setAddress(address1);
							so.setOutLetName(outletName);
							so.setOutletAddress(outletAddress1);
							so.setCustomerCode(customerCode);
							so.setDeliveryCode(deliveryCode);
							so.setVanCode(vanCode);
							so.setOrderNo(0);
							mCustomerAddressData.add(so);
						}
						}else{
							so.setCustomerName(customerName);
							so.setAddress(address1);
							so.setOutLetName(outletName);
							so.setOutletAddress(outletAddress1);
							so.setCustomerCode(customerCode);
							so.setDeliveryCode(deliveryCode);
							so.setVanCode(vanCode);
							so.setOrderNo(0);
							mCustomerAddressData.add(so);
						}
						
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void arg0) {		
			mCustomerAddressAdapter = new CustomerAddressAdapter(
					MerchandiseAddSchedule.this,
					R.layout.list_item_merchandise_add_schedule,
					mCustomerAddressData);
			mListView.setAdapter(mCustomerAddressAdapter);
			if(!isEdit){
			if(selectedVan!=null && !selectedVan.isEmpty()){
				mUserVanFilterEdt.setText(selectedVan);				
			}
			}
			helper.dismissProgressView(mMerchandiseMainLayout);
		}
	}

	private class CustomerAddressAdapter extends ArrayAdapter<SO> {

		private ArrayList<SO> customerAddressList;
		 private ArrayList<SO> originalList;
		private ViewHolder holder = null;
		private CustomerAddressFilter filter;
		private String filterType=""; 
		public CustomerAddressAdapter(Context context, int textViewResourceId,
				ArrayList<SO> customerAddressList) {
			super(context, textViewResourceId, customerAddressList);
			this.customerAddressList = new ArrayList<SO>();
			this.customerAddressList.addAll(customerAddressList);
			this.originalList = new ArrayList<SO>();
			 this.originalList.addAll(customerAddressList);
		}

		  @Override
		  public Filter getFilter() {
		   if (filter == null){
		    filter  = new CustomerAddressFilter();
		   }
		   return filter;
		  }
		  public void setFilterType(String filterType) {
				this.filterType = filterType;
			}

			public String getFilterType() {
				return filterType;

			}
		public class ViewHolder {
			TextView customerName;
			TextView customerAddress;
			TextView outletName;
			TextView outletAddress;
			EditText orderNo;
			CheckBox schedule;
			LinearLayout addScheduleLayout;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			SO so = customerAddressList.get(position);

			if (view == null) {
				holder = new ViewHolder();
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.list_item_merchandise_add_schedule,
						null);

				holder.customerName = (TextView) view
						.findViewById(R.id.customerName);
				holder.customerAddress = (TextView) view
						.findViewById(R.id.customerAddress);
				holder.outletName = (TextView) view
						.findViewById(R.id.outletName);
				holder.outletAddress = (TextView) view
						.findViewById(R.id.outletAddress);
				holder.orderNo = (EditText) view.findViewById(R.id.orderNo);	
				holder.schedule = (CheckBox) view.findViewById(R.id.schedule);

				holder.addScheduleLayout = (LinearLayout) view.findViewById(R.id.add_schedule_layout);	
				
				// attach the TextWatcher listener to the EditText
				holder.orderNo.addTextChangedListener(new OrderNoTextWatcher(view));
				/*if (position % 2 == 0) {
					holder.addScheduleLayout.setBackgroundColor(Color.rgb(250,250,250));
				} else {
					holder.addScheduleLayout.setBackgroundColor(Color.rgb(255,255,255));
					
				}*/
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.orderNo.setTag(so);
			holder.schedule.setTag(so);
			holder.customerName.setText(so.getCustomerName());
			
			if (so.getAddress() != null && !so.getAddress().isEmpty()) {
				holder.customerAddress.setText(so.getAddress());
				holder.customerAddress.setVisibility(View.GONE);
			} else {
				holder.customerAddress.setVisibility(View.GONE);
			}
			
			if (so.getOutLetName() != null && !so.getOutLetName().isEmpty()) {
				holder.outletName.setText(so.getOutLetName());
				holder.outletName.setVisibility(View.VISIBLE);
			} else {
				holder.outletName.setVisibility(View.INVISIBLE);
			}
			if (so.getOutletAddress() != null && !so.getOutletAddress().isEmpty()) {
				holder.outletAddress.setText(so.getOutletAddress());
				holder.outletAddress.setVisibility(View.VISIBLE);
			} else {
				holder.outletAddress.setVisibility(View.INVISIBLE);
			}
			
			holder.schedule.setChecked(so.isSelected());
			
			if(so.isSelected()){
				holder.orderNo.requestFocus();
				holder.orderNo.setFocusable(true);
				holder.orderNo.setFocusableInTouchMode(true);
				holder.orderNo.setCursorVisible(true);	
				holder.orderNo.setSelection(holder.orderNo.getText().length());
				//showKeyboard(holder.orderNo);
			}
			
			
			if (so.getOrderNo() != 0) {
				holder.orderNo.setText(String.valueOf(so.getOrderNo()));				
			} else {
				holder.orderNo.setText("");
			}
				
			holder.orderNo.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
				
					EditText orderNo = (EditText) v;
					orderNo.requestFocus();
					orderNo.setFocusable(true);
					orderNo.setCursorVisible(orderNo.hasFocus());
					orderNo.setSelection(orderNo.getText().length());
					showKeyboard(orderNo);
					return false;
				}
			});
			holder.schedule.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					 CheckBox cb = (CheckBox) v;
					 SO so = (SO) cb.getTag();
			         so.setSelected(cb.isChecked());
			         if(so.isSelected()){
						 so.setOrderNo(1);
					}else{
						 so.setOrderNo(0);
					}	
			     	//hideKeyboard(mUserEdt);
			     	//mCustomerNameFilterEdt.setFocusable(false);
			     	//mUserVanFilterEdt.setFocusable(false);
					notifyDataSetChanged();
					
				}
			});
			return view;

		}

		  private class CustomerAddressFilter extends Filter
		  {

		   @Override
		   protected FilterResults performFiltering(CharSequence constraint) {
				String customer = null, van = null;		   
		    FilterResults result = new FilterResults();
		    if(constraint != null && constraint.toString().length() > 0)
		    {
		    constraint = constraint.toString().toLowerCase();
		    ArrayList<SO> filteredItems = new ArrayList<SO>();
		    if (getFilterType().matches("Customer && Van")) {
				StringTokenizer tokens = new StringTokenizer(constraint.toString(), ",");
				customer = tokens.nextToken();
				van = tokens.nextToken();
			}
		    for(int i = 0, l = originalList.size(); i < l; i++)
		    {
		    SO so = originalList.get(i);		    
		    if (getFilterType().matches("Customer")) {				
		    	 if(so.getCustomerName().toString().toLowerCase().contains(constraint)){
			    	   filteredItems.add(so);
			     }	
			}else if (getFilterType().matches("Van")) {	
				 if(so.getVanCode().toString().toLowerCase().contains(constraint)){
			    	   filteredItems.add(so);
			     }	
			}else if (getFilterType().matches("Customer && Van")) {
				Log.d("customer", "--->"+customer);
				Log.d("van", "--->"+van);
				if(customer!=null && !customer.isEmpty() && van!=null && !van.isEmpty()){
					if(so.getCustomerName().toString().toLowerCase().contains(customer) && (so.getVanCode().toString().toLowerCase().contains(van))){
				    	   filteredItems.add(so);
				     }		
				}
				 
			}
		    }
		    result.count = filteredItems.size();
		    result.values = filteredItems;
		    }
		    else
		    {		    	
		    	
		     synchronized(this)
		     {
		      result.values = originalList;
		      result.count = originalList.size();
		     }
		    }
		    return result;
		   }

		   @SuppressWarnings("unchecked")
		   @Override
		   protected void publishResults(CharSequence constraint, 
		     FilterResults results) {

			   customerAddressList = (ArrayList<SO>)results.values;
		    notifyDataSetChanged();
		    clear();
		    for(int i = 0, l = customerAddressList.size(); i < l; i++)
		     add(customerAddressList.get(i));
		    notifyDataSetInvalidated();
		   }
		  }
		  
		  public class uploadToSaveScheduling extends
			AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			helper.showProgressView(mMerchandiseMainLayout);
		}

		@Override
		protected String doInBackground(String... params) {
			String result ="";
			String sDetail = params[0];			
			
			mHashMap.put("CompanyCode", mCompanyCodeStr);
			mHashMap.put("ScheduleNo", mScheduleNo);
			mHashMap.put("ScheduleDate", mDateEdt.getText().toString());
			mHashMap.put("ScheduleUser", mScheduleUser);			
			mHashMap.put("sDetail",sDetail);			
			mHashMap.put("User", mUserIdStr);
			
			
			try {
				// Call GetServerDate
				mSaveSchedulingJsonString = SalesOrderWebService.getSODetail(
						mHashMap, "fncSaveScheduling");
				mSaveSchedulingJsonObject = new JSONObject(mSaveSchedulingJsonString);
				mSaveSchedulingJsonArray = mSaveSchedulingJsonObject
						.optJSONArray("SODetails");
				JSONObject jsonobject = mSaveSchedulingJsonArray.getJSONObject(0);
				result = jsonobject.getString("Result");

			} catch (Exception e) {
				e.printStackTrace();
			}
		

			return result;
		}

		@Override
		protected void onPostExecute(String result) {		
              if(result!=null && !result.isEmpty()){
            	  Toast.makeText(MerchandiseAddSchedule.this, "Saved Successfully", Toast.LENGTH_SHORT).show();            	  
            	  mIntent.setClass(MerchandiseAddSchedule.this, MerchandiseSchedule.class);
				  startActivity(mIntent);
				  finish();
              }else{
            	  Toast.makeText(MerchandiseAddSchedule.this, "Failed", Toast.LENGTH_SHORT).show();
              }
			helper.dismissProgressView(mMerchandiseMainLayout);
		}
	}
		    public void saveScheduling(){
		    	try{
		    	String sDetail="";
		    	int size = 0; 		    	
		    		loop: for (int i = 0; i < originalList.size(); i++) {
					SO so = originalList.get(i);					
					int orderno = so.getOrderNo();
					if(orderno>0){
						String customercode = so.getCustomerCode();
						String deliverycode = so.getDeliveryCode();
						String ordernum = String.valueOf(so.getOrderNo());
						sDetail += customercode + "^" + deliverycode + "^" + ordernum + "!";						
					}
					size++;
					if(size == originalList.size()){
						Log.d("size", "-->"+size);
						Log.d("originalList.size()", "-->"+originalList.size());
						 Log.d("sDetail", "-->"+sDetail);
						 if(sDetail!=null && !sDetail.isEmpty()){
							 sDetail = sDetail.substring(0, sDetail.length()-1);
							 new uploadToSaveScheduling().execute(sDetail);
						 }
						 else{
							 Toast.makeText(MerchandiseAddSchedule.this, "Order No is empty", Toast.LENGTH_SHORT).show();
						 }
						break loop;
						}
					}
		    	
		    }
		    catch(Exception e){
		    	e.printStackTrace();
		    }
	}
	}
	private class OrderNoTextWatcher implements TextWatcher {

		private View view;

		private OrderNoTextWatcher(View view) {
			this.view = view;
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// do nothing
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// do nothing
		}

		public void afterTextChanged(Editable s) {

			String orderNoString = s.toString().trim();
			int orderNo = orderNoString.equals("") ? 0 : Integer
					.valueOf(orderNoString);

			EditText orderNoView = (EditText) view.findViewById(R.id.orderNo);
			SO so = (SO) orderNoView.getTag();
			
			// Log.d("orderNo", "--->"+orderNo);

			if (so.getOrderNo() != orderNo) {

				so.setOrderNo(orderNo);

				if (so.getOrderNo() != 0) {
					 Log.d("so.getOrderNo()", "--->"+so.getOrderNo());
					
					orderNoView.setText(String.valueOf(so.getOrderNo()));
					orderNoView.setSelection(orderNoView.getText().length());					
				} else {
					orderNoView.setText("");
				}

			}

			return;
		}
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
	 public void hideKeyboard(EditText edittext) {
	        InputMethodManager inputmethodmanager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputmethodmanager
	                .hideSoftInputFromWindow(edittext.getWindowToken(), 0);

	    }
	protected void showKeyboard(EditText editText) {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}
	@Override
	public void onBackPressed() {			
		mIntent.setClass(MerchandiseAddSchedule.this, MerchandiseSchedule.class);
		startActivity(mIntent);
		finish();
		
	}
}
