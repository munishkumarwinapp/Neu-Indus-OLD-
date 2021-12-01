package com.winapp.catalog;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.winapp.SFA.R;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.DateTime;
import com.winapp.model.Customer;
import com.winapp.offline.OfflineDatabase;
import com.winapp.printer.UIHelper;
import com.winapp.sot.GraAdapter;
import com.winapp.sot.SO;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;



public class CatalogListingFragment extends BaseFragment {
	private String mSOHeaderJsonString, mCompanyCode,
			mValidUrl, mSpinnerStatus, mStatusValue = "0",
			mVanCode, mFromDateStr, mToDateStr,
			mCustomerCodeStr = "", mDialogStatus = "",mCustomerJsonString="",mobileHaveOfflineMode="";
	private HashMap<String, String> mHashMap;
	private JSONObject mSOHeaderJsonObject;
	private JSONArray mSOHeaderJSONArray;
	private ArrayList<SO> mHeaderList;
	private GraAdapter mGraAdapter;
	private ListView mListView;
	private Spinner mSpinner;
	private EditText mCustomerCodeEd, mFromDateEd, mToDateEd;
	private Button mSearchBtn;
	private int mTextlength = 0;
	private AlertDialog mCustomerDialog = null;
	private Calendar mFromCalendar, mToCalendar;
	private CustomAlertAdapterSupp mCustomerAdapter;
	private ArrayList<HashMap<String, String>> customerArrHm,
			customerSearchArrHm, customerDataArrHm;
	private DatePickerDialog.OnDateSetListener mFromDatePicker, mToDatePicker;
	//private ImageButton mListingSearchIcon;
	private LinearLayout mSearchLayout, mListingLayout;
	private UIHelper helper;
	private CarouselFragment mCarouselFragment;
	private OfflineDatabase offlineDatabase;
	private View view,customView;
	private static boolean bExecuted;
	private JSONObject mCustomerJSONObject;
	private JSONArray mCustomerJSONArray;
	public CatalogListingFragment() {
		// Required empty public constructor
	}

	/* public CatalogListingFragment(ImageButton mListingSearchIcon,ArrayList<HashMap<String, String>> mCustomerArrHm) {
         customerDataArrHm = new ArrayList<HashMap<String,String>>();
         this.mListingSearchIcon = mListingSearchIcon;
         this.customerDataArrHm = mCustomerArrHm;
     }*/
	public static CatalogListingFragment newInstance(/*HashMap<String, ImageButton> mHashMapIcon, ArrayList<HashMap<String, String>> mCustomerArrHm*/) {
		CatalogListingFragment frag = new CatalogListingFragment();
//		Bundle args = new Bundle();
//		args.putSerializable("ListingSearchIcon", mHashMapIcon);
//		args.putSerializable("CustomerArrHm", mCustomerArrHm);
//		frag.setArguments(args);
		return frag;
	}
	public void setView(View customView) {
		this.customView = customView;
	}
	public boolean hasExecuted(){
		return this.bExecuted;
	}
	public void sethasExecuted( boolean bExecuted){
		this.bExecuted = bExecuted;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_listing, container, false);
//		customerDataArrHm = new ArrayList<HashMap<String, String>>();
//		HashMap<String, ImageButton> hm = (HashMap<String, ImageButton>) getArguments().getSerializable("ListingSearchIcon");
//		customerDataArrHm = (ArrayList<HashMap<String, String>>) getArguments().getSerializable("CustomerArrHm");
//		//mListingSearchIcon = hm.get("ListingSearchIcon");
//		mListingSearchIcon = (ImageButton) customView.findViewById(R.id.right_button2);
		return view;
	}
	@Override
	public void onResume() {
		try {
			//View ID
			mListView = (ListView) view.findViewById(R.id.saleO_listView1);
			mSpinner = (Spinner) view.findViewById(R.id.stockrequest_status);
			mCustomerCodeEd = (EditText) view.findViewById(R.id.salesOCustCode);
			mFromDateEd = (EditText) view.findViewById(R.id.starteditTextDate);
			mToDateEd = (EditText) view.findViewById(R.id.endeditTextDate);
			mSearchBtn = (Button) view.findViewById(R.id.saleO_btsearch);
			mSearchLayout = (LinearLayout) view.findViewById(R.id.searchlayout);
			mListingLayout = (LinearLayout) view.findViewById(R.id.salesOrder_parent);
			// Object Initialization
			mFromCalendar = Calendar.getInstance();
			mToCalendar = Calendar.getInstance();
			mHashMap = new HashMap<String, String>();
			mHeaderList = new ArrayList<SO>();
			customerArrHm = new ArrayList<HashMap<String, String>>();
			customerSearchArrHm = new ArrayList<HashMap<String, String>>();
			helper = new UIHelper(getActivity());
			customerDataArrHm = new ArrayList<>();
			//DB Initialization
			SOTDatabase.init(getActivity());
			mCarouselFragment = new CarouselFragment();
			offlineDatabase = new OfflineDatabase(getActivity());

			//Get URL from DB
			mValidUrl = FWMSSettingsDatabase.getUrl();
			new SalesOrderWebService(mValidUrl);

            mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();

			//Get CompanyCode from Pojo class
			mCompanyCode = SupplierSetterGetter.getCompanyCode();

			//Get VanCode from DB
			mVanCode = SOTDatabase.getVandriver();

			//Get Server Date form pojo class
			mToDateStr = SalesOrderSetGet.getServerDate();

			//Show last one month date
			mFromDateStr = DateTime.date(mToDateStr);

			//Set Text
			mFromDateEd.setText(mFromDateStr);
			mToDateEd.setText(mToDateStr);


			Log.d("hasExecuted", "-onresume->" + hasExecuted());
			//Get SOHeader Values
			if (hasExecuted()) {
				new GetSOHeader().execute();
				bExecuted = false;
			}

			mFromDatePicker = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
									  int dayOfMonth) {
					mFromCalendar.set(Calendar.YEAR, year);
					mFromCalendar.set(Calendar.MONTH, monthOfYear);
					mFromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					fromDateAction();
				}
			};

			mToDatePicker = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
									  int dayOfMonth) {
					mToCalendar.set(Calendar.YEAR, year);
					mToCalendar.set(Calendar.MONTH, monthOfYear);
					mToCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					toDateAction();
				}
			};
			mFromDateEd.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (MotionEvent.ACTION_UP == event.getAction())
						new DatePickerDialog(getActivity(), mFromDatePicker,
								mFromCalendar.get(Calendar.YEAR), mFromCalendar
								.get(Calendar.MONTH), mFromCalendar
								.get(Calendar.DAY_OF_MONTH)).show();
					return false;
				}
			});

			mToDateEd.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (MotionEvent.ACTION_UP == event.getAction())
						new DatePickerDialog(getActivity(), mToDatePicker,
								mToCalendar.get(Calendar.YEAR), mToCalendar
								.get(Calendar.MONTH), mToCalendar
								.get(Calendar.DAY_OF_MONTH)).show();
					return false;
				}
			});
			mCustomerCodeEd.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
					mCustomerCodeEd) {
				@Override
				public boolean onDrawableClick() {
					if (customerDataArrHm.size() > 0) {
						customerDialogAction();
					} else {
						new GetCustomerData().execute();
					}
					return true;
				}


			});
			mSearchBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					mCustomerCodeStr = mCustomerCodeEd.getText().toString();
					mFromDateStr = mFromDateEd.getText().toString();
					mToDateStr = mToDateEd.getText().toString();
					spinnerstatus();

					if (mFromDateStr != null && !mFromDateStr.isEmpty() && mToDateStr != null && !mToDateStr.isEmpty()) {

						mHeaderList.clear();
						new GetSOHeader().execute();
					} else {
						Toast.makeText(getActivity(), "Please Enter Date",
								Toast.LENGTH_SHORT).show();
					}

				}
			});
			SOCatalogActivity.mListingSearchIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSearchLayout.getVisibility() == View.GONE) {
						mSearchLayout.setVisibility(View.VISIBLE);
					} else {
						mSearchLayout.setVisibility(View.GONE);
					}
				}
			});
		}catch ( Exception e){
			e.printStackTrace();
		}
		super.onResume();
	}

	private void spinnerstatus() {
		mSpinnerStatus = mSpinner.getSelectedItem().toString();
		Log.d("statusvalue", "-->" + mSpinnerStatus);
		if (mSpinnerStatus.matches("Open")) {
			mStatusValue = "0";
		} else if (mSpinnerStatus.matches("InProgress Invoice")) {
			mStatusValue = "2";
		} else if (mSpinnerStatus.matches("InProgress DO")) {
			mStatusValue = "1";
		} else if (mSpinnerStatus.matches("Closed")) {
			mStatusValue = "3";
		} else if (mSpinnerStatus.matches("All")) {
			mStatusValue = "5";
		}
	}

	private void fromDateAction() {
		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		mFromDateEd.setText(sdf.format(mFromCalendar.getTime()));
	}

	private void toDateAction() {
		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		mToDateEd.setText(sdf.format(mToCalendar.getTime()));
	}
	private class GetCustomerData extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			helper.showProgressView(mListingLayout);
			mDialogStatus = "";
			mDialogStatus = mCarouselFragment.checkInternetStatus(getActivity());
			mHashMap.clear();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			mHashMap.put("CompanyCode", mCompanyCode);

			SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
			String finalDate = timeFormat.format(new Date());
			System.out.println(finalDate);
			try {

				if (CarouselFragment.onlineMode.matches("True")) {
					if (CarouselFragment.checkOffline == true) { //temp_offline

						if (mDialogStatus.matches("true")) {
							Log.d("temp_offline ", "temp_offline");
							// Customer Call
							mHashMap.put("VanCode", mVanCode);
							mCustomerJsonString = getCustomerOffline(mHashMap);
							Log.d("mCustomerJsonString ", ""
									+ mCustomerJsonString);
							mCustomerJSONObject = new JSONObject(
									mCustomerJsonString);
							mCustomerJSONArray = mCustomerJSONObject
									.optJSONArray("JsonArray");
						} else {
                            if(mobileHaveOfflineMode.matches("1")) {
                                getActivity().finish();
                            }
						}

					} else { // Onlline
						Log.d("Onlline ", "Onlline");

						// Customer CALL
						mHashMap.put("VanCode", mVanCode);
						mCustomerJsonString = SalesOrderWebService.getSODetail(
								mHashMap, "fncGetCustomer");
						Log.d("mCustomerJsonString ", "" + mCustomerJsonString);
						mCustomerJSONObject = new JSONObject(
								mCustomerJsonString);
						mCustomerJSONArray = mCustomerJSONObject
								.optJSONArray("SODetails");
					}

				} else if (CarouselFragment.onlineMode.matches("False")) {  // permanent_offline
					Log.d("permanent_offline ", "permanent_offline");



					// Customer Call
					mHashMap.put("VanCode", mVanCode);
					mCustomerJsonString = getCustomerOffline(mHashMap);
					Log.d("mCustomerJsonString ", "" + mCustomerJsonString);
					mCustomerJSONObject = new JSONObject(mCustomerJsonString);
					mCustomerJSONArray = mCustomerJSONObject
							.optJSONArray("JsonArray");

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				int lengthJsonArr = mCustomerJSONArray.length();
				Log.d("fncGetCustomer ", "-->" + lengthJsonArr);
				if (lengthJsonArr > 0) {
					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode;
						try {
							jsonChildNode = mCustomerJSONArray.getJSONObject(i);
							String customercode = jsonChildNode.optString(
									"CustomerCode").toString();
							String customername = jsonChildNode.optString(
									"CustomerName").toString();

							String referenceLocation = jsonChildNode.optString(
									"ReferenceLocation").toString();

							HashMap<String, String> customerhm = new HashMap<String, String>();
							if (referenceLocation != null
									&& !referenceLocation.isEmpty()) {
								customerhm.put(customercode, customername + "/"
										+ referenceLocation);
							} else {
								customerhm.put(customercode, customername);
							}

							customerDataArrHm.add(customerhm);



						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {

			try {
				if(customerDataArrHm.size()>0){
					customerDialogAction();
				}
				helper.dismissProgressView(mListingLayout);
			} catch (Exception e) {
				e.printStackTrace();
			}



		}
	}
	private String getCustomerOffline(HashMap<String, String> hm) {
		String customer_jsonString = "";
		HashMap<String, String> customerhashValue = new HashMap<String, String>();

		customerhashValue.put("CompanyCode", hm.get("CompanyCode"));
		customerhashValue.put("CustomerCode", "");
		customerhashValue.put("NeedOutstandingAmount", "");
		customerhashValue.put("AreaCode", "");
		customerhashValue.put("VanCode", hm.get("VanCode"));

		customer_jsonString = OfflineDatabase
				.getCustomersList(customerhashValue);

		return customer_jsonString;
	}
	private void hideKeyboard(EditText text){
		InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(text.getWindowToken(), 0);
	}
	private void customerDialogAction() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
		final EditText editText = new EditText(getActivity());
		final ListView listview = new ListView(getActivity());
		LinearLayout layout = new LinearLayout(getActivity());
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
					mCustomerDialog
							.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		mCustomerAdapter = new CustomAlertAdapterSupp(getActivity(), customerDataArrHm);
		listview.setAdapter(mCustomerAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				// TODO Auto-generated method stub
				hideKeyboard(editText);
				mCustomerDialog.dismiss();
				customerArrHm = mCustomerAdapter.getArrayList();
				HashMap<String, String> datavalue = customerArrHm.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String keyValues = (String) mapEntry.getKey();
					mapEntry.getValue();
					mCustomerCodeEd.setText(keyValues);
					mCustomerCodeEd.addTextChangedListener(new TextWatcher() {
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
							mTextlength = mCustomerCodeEd.getText().length();
						}
					});
				}
			}
		});

		customerSearchArrHm = new ArrayList<HashMap<String, String>>(customerDataArrHm);
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
				mTextlength = editText.getText().length();
				customerSearchArrHm.clear();
				for (int i = 0; i < customerDataArrHm.size(); i++) {
					String customerName = customerDataArrHm.get(i).toString();
					if (mTextlength <= customerName.length()) {
						if (customerName.toLowerCase(Locale.getDefault()).contains(
								editText.getText().toString().toLowerCase(Locale.getDefault())
										.trim()))
							customerSearchArrHm.add(customerDataArrHm.get(i));
					}
				}

				mCustomerAdapter = new CustomAlertAdapterSupp(
						getActivity(), customerSearchArrHm);
				listview.setAdapter(mCustomerAdapter);
			}
		});
		myDialog.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		mCustomerDialog = myDialog.show();

	}

	private class GetSOHeader extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			mHeaderList.clear();
			helper.showProgressView(mListingLayout);
			mDialogStatus = "";
			mDialogStatus = mCarouselFragment.checkInternetStatus(getActivity());
			mHashMap.clear();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			mHashMap.put("CompanyCode", mCompanyCode);
			mHashMap.put("CustomerCode", mCustomerCodeStr);
			mHashMap.put("FromDate", mFromDateStr);
			mHashMap.put("ToDate", mToDateStr);
			mHashMap.put("VanCode", mVanCode);

			try {

				if (CarouselFragment.onlineMode.matches("True")) {
					if (CarouselFragment.checkOffline == true) { //temp_offline

						if (mDialogStatus.matches("true")) {
							Log.d("temp_offline ", "temp_offline");
							mSOHeaderJsonString = offlineDatabase.getAllSOHeaderJson(mHashMap);

						} else {
                            if(mobileHaveOfflineMode.matches("1")) {
                                getActivity().finish();
                            }
						}

					} else {  //Onlline
						Log.d("Onlline ", "Onlline");
						mSOHeaderJsonString = SalesOrderWebService.getSODetail(mHashMap, "fncGetSOHeader");
					}

				} else if (CarouselFragment.onlineMode.matches("False")) {  // permanent_offline
					Log.d("permanent_offline ", "permanent_offline");
					mSOHeaderJsonString = offlineDatabase.getAllSOHeaderJson(mHashMap);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				if(mSOHeaderJsonString!=null) {
					try {
						//Call GetSOHeader

						Log.d("mSOHeaderJsonString ", "" + mSOHeaderJsonString);
						mSOHeaderJsonObject = new JSONObject(mSOHeaderJsonString);
						mSOHeaderJSONArray = mSOHeaderJsonObject.optJSONArray("SODetails");
						int lengthJsonArr = mSOHeaderJSONArray.length();
						for (int i = 0; i < lengthJsonArr; i++) {
							SO so = new SO();
							JSONObject jsonChildNode = mSOHeaderJSONArray.getJSONObject(i);
							String ccSno = jsonChildNode.optString("SoNo").toString();
							String ccDate = jsonChildNode.optString("DeliveryDate").toString();
							String customerCode = jsonChildNode.optString("CustomerCode").toString();
							String customerName = jsonChildNode.optString("CustomerName").toString();
							String amount = jsonChildNode.optString("NetTotal").toString();
							String status = jsonChildNode.optString("Status").toString();
							Log.d("status", "" + status);
							// Based on status it show individual values
							if (mStatusValue.matches(status)) {
								so.setSno(ccSno);
								so.setCustomerCode(customerCode);
								so.setCustomerName(customerName);
								so.setNettotal(amount);
								if (status.matches("0")) {
									so.setStatus("open");
								} else if (status.matches("2")) {
									so.setStatus("InProgress Invoice");
								} else if (status.matches("1")) {
									so.setStatus("InProgress DO");
								} else if (status.matches("3")) {
									so.setStatus("closed");
								} else {
									so.setStatus("open");
								}
								if (ccDate != null && !ccDate.isEmpty()) {
									StringTokenizer tokens = new StringTokenizer(ccDate, " ");
									String date = tokens.nextToken();
									so.setDate(date);
								} else {
									so.setDate(ccDate);
								}
								mHeaderList.add(so);
							}
							// Show all values
							else if (mStatusValue.matches("5")) {
								so.setSno(ccSno);
								so.setCustomerCode(customerCode);
								so.setCustomerName(customerName);
								so.setNettotal(amount);
								if (status.matches("0")) {
									so.setStatus("open");
								} else if (status.matches("2")) {
									so.setStatus("InProgress Invoice");
								} else if (status.matches("1")) {
									so.setStatus("InProgress DO");
								} else if (status.matches("3")) {
									so.setStatus("closed");
								}
								if (ccDate != null && !ccDate.isEmpty()) {
									StringTokenizer tokens = new StringTokenizer(
											ccDate, " ");
									String date = tokens.nextToken();
									so.setDate(date);
								} else {
									so.setDate(ccDate);
								}
								mHeaderList.add(so);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (getActivity() != null) {
				mGraAdapter = new GraAdapter(getActivity(),
						R.layout.catalog_listing_listitem, mHeaderList);
				mListView.setAdapter(mGraAdapter);
			}

			helper.dismissProgressView(mListingLayout);
		}
	}
}

