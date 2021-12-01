package com.winapp.catalog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.winapp.adapter.CustomerAdapter;
import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Catalog;
import com.winapp.model.Customer;
import com.winapp.model.Product;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDataDownloader;
import com.winapp.offline.OfflineDataUploader;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineDataUploader.OnUploadCompletionListener;
import com.winapp.printer.UIHelper;
import com.winapp.sot.OfflineSalesOrderWebService;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sotdetails.CustomerSetterGetter;
import com.winapp.sotdetails.DBCatalog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CatalogCustomerFragment extends BaseFragment {
	private EditText mCustomerCodeEd;
	private TextView mDateTxt;
	private ListView mListView;	
	private CustomerAdapter mCustomerAdapter;
	private ArrayList<Customer> mCustomerArrList;
	private Calendar mCalendar;
	private DatePickerDialog.OnDateSetListener datePickerDialog;
	private ViewPager mPager;
	private String mCustomerNameStr="",mCurrencyCode = "",mCustomerCodeStr="", mValidUrl="",  mDialogStatus="",mCustomerJsonString="",mCompanyCode="",mVanCode="";
	private ImageView mSkip;
	//private ImageButton mDownloadIcon;
	private DBCatalog dbcatalog;
	private OfflineDatabase offlineDatabase;
	private OfflineDataDownloader imageDownloader;
	public static boolean checkOffline,bExecuted;
	private CarouselFragment mCarouselFragment;

	public static LinearLayout offlineLayout;
	public static String onlineMode = "", updateonlinemode = "",mobileHaveOfflineMode="";
	private OfflineCommon offlineCommon;
	private JSONObject mCustomerJSONObject;
	private JSONArray mCustomerJSONArray;
	private View view;
	private UIHelper helper;
	private HashMap<String, String> mHashMap;
	private FrameLayout sMainLayout;
	private View customView;
    private ArrayList<String> mCurrencyArrList;
	public CatalogCustomerFragment() {
		// Required empty public constructor
	}
	/*public CatalogCustomerFragment(ImageButton mDownloadIcon, ArrayList<Customer> customerArrList) {
		mCustomerArrList = new ArrayList<Customer>();
		this.mDownloadIcon = mDownloadIcon;
		this.mCustomerArrList = customerArrList;
	}*/
	public void setView(View customView) {
		this.customView = customView;
	}
	public static CatalogCustomerFragment newInstance(/*HashMap<String, ImageButton> mHashMapIcon, ArrayList<Customer> customerArrList*/) {
		CatalogCustomerFragment frag = new CatalogCustomerFragment();
//		Bundle args = new Bundle();
//		args.putSerializable("DownloadIcon", mHashMapIcon);
//		args.putSerializable("CustomerObjArr", customerArrList);
//		frag.setArguments(args);
		return frag;

	}
	public boolean hasExecuted(){
		return this.bExecuted;
	}
	public void sethasExecuted(boolean bExecuted){
		this.bExecuted = bExecuted;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_customer, container, false);

//		HashMap<String ,ImageButton> hm = (HashMap<String ,ImageButton>) getArguments().getSerializable("DownloadIcon");
//		mCustomerArrList = (ArrayList<Customer>)  getArguments().getSerializable("CustomerObjArr");
		//mDownloadIcon = hm.get("DownloadIcon");
//		mDownloadIcon = (ImageButton) customView.findViewById(R.id.right_button5);

		return view;
	}
	@Override
	public void onResume(){
		// ID
		sMainLayout = (FrameLayout) view.findViewById(R.id.fragment_mainLayout);
		mCustomerCodeEd = (EditText) view.findViewById(R.id.customerCode);
		mSkip = (ImageView) view.findViewById(R.id.skip);
		mDateTxt = (TextView) view.findViewById(R.id.date);
		mListView = (ListView) view.findViewById(R.id.listView);
		mPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
		mCustomerArrList = new ArrayList<Customer>();


		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();

		// DB Initialization
		DBCatalog.init(getActivity());
		dbcatalog = new DBCatalog(getActivity());
		FWMSSettingsDatabase.init(getActivity());
		OfflineDatabase.init(getActivity());
		offlineDatabase = new OfflineDatabase(getActivity());
		helper = new UIHelper(getActivity());
		mHashMap =new HashMap<>();
        mCurrencyArrList = new ArrayList<>();


		// Object Initialization
		mCustomerAdapter = new CustomerAdapter();
		mCalendar = Calendar.getInstance();
		mCustomerAdapter.notifyDataSetChanged();
		mCarouselFragment = new CarouselFragment();
		mDialogStatus = mCarouselFragment.checkInternetStatus(getActivity());


		// DB Initialization
		FWMSSettingsDatabase.init(getActivity());
		OfflineDatabase.init(getActivity());
		offlineDatabase = new OfflineDatabase(getActivity());
		// Get URL from DB
		mValidUrl = FWMSSettingsDatabase.getUrl();

		SOCatalogActivity.mDownloadIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog();
			}
		});

		//Get CompanyCode from Pojo class
		mCompanyCode = SupplierSetterGetter.getCompanyCode();

		//Get VanCode from DB
		mVanCode = SOTDatabase.getVandriver();

		// Get URL from DB
		mValidUrl = FWMSSettingsDatabase.getUrl();

		//ListView OnItemClickListener
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
									long id) {
				loadCustomerTaxValue(position);
			}
		});
		mSkip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(1);
			}
		});
		//CustomerCode EditText  TextChangedListener
		mCustomerCodeEd.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				mCustomerAdapter.filter(s.toString());
			}
		});
		//Date Picker Dialog
		datePickerDialog = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
								  int dayOfMonth) {
				mCalendar.set(Calendar.YEAR, year);
				mCalendar.set(Calendar.MONTH, monthOfYear);
				mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				date();
			}
		};

		mDateTxt.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(getActivity(), datePickerDialog,
							mCalendar.get(Calendar.YEAR), mCalendar
							.get(Calendar.MONTH), mCalendar
							.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		mDateTxt.setText(SalesOrderSetGet.getServerDate());


		if(hasExecuted()) {

			// Get Customer Data
			new GetCustomerData().execute();
			bExecuted = false;
			if (CarouselFragment.onlineMode.matches("True")) {
				if (CarouselFragment.checkOffline == false) {
                    if(mobileHaveOfflineMode.matches("1")){
                        SOCatalogActivity.mDownloadIcon.setVisibility(View.VISIBLE);
                    }else{
                        SOCatalogActivity.mDownloadIcon.setVisibility(View.GONE);
                    }
				}else{
					SOCatalogActivity.mDownloadIcon.setVisibility(View.GONE);
				}
			}else{
				SOCatalogActivity.mDownloadIcon.setVisibility(View.GONE);
			}
		}


		super.onResume();
	}


	private void date() {
		String myFormat = "dd/MM/yyyy"; // In which you need put here
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		mDateTxt.setText(sdf.format(mCalendar.getTime()));
		SalesOrderSetGet.setSaleorderdate(mDateTxt.getText().toString());	
	}
	private class GetCustomerData extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			helper.showProgressView(sMainLayout);
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

                            // General Settings Call
                            mCurrencyCode = OfflineSalesOrderWebService
                                    .generalSettingsServiceOffline();
                            // Currency Call
                            mCurrencyArrList = OfflineSalesOrderWebService
                                    .getCurrencyValuesOffline(mCurrencyCode);
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

                        // General Settings Call
                        mCurrencyCode = SalesOrderWebService
                                .generalSettingsService("fncGetGeneralSettings");
                        // Currency Call
                        mCurrencyArrList = SalesOrderWebService
                                .getCurrencyValues("fncGetCurrency",
                                        mCurrencyCode);

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

                    // General Settings Call
                    mCurrencyCode = OfflineSalesOrderWebService
                            .generalSettingsServiceOffline();
                    mCurrencyArrList = OfflineSalesOrderWebService
                            .getCurrencyValuesOffline(mCurrencyCode);

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
				if(mCustomerJSONArray!=null) {
					int lengthJsonArr = mCustomerJSONArray.length();
					Log.d("fncGetCustomer ", "-->" + lengthJsonArr);
					if (lengthJsonArr > 0) {
						for (int i = 0; i < lengthJsonArr; i++) {

							JSONObject jsonChildNode;
							try {
								Customer customer = new Customer();
								jsonChildNode = mCustomerJSONArray.getJSONObject(i);
								String customercode = jsonChildNode.optString(
										"CustomerCode").toString();
								String customername = jsonChildNode.optString(
										"CustomerName").toString();
								String custgroup = jsonChildNode.optString(
										"CustomerGroupCode").toString();
								String referenceLocation = jsonChildNode.optString(
										"ReferenceLocation").toString();
								String address1 = jsonChildNode.optString(
										"Address1").toString();
								String address2 = jsonChildNode.optString(
										"Address2").toString();
								String address3 = jsonChildNode.optString(
										"Address3").toString();
								String phoneNo = jsonChildNode.optString("PhoneNo")
										.toString();
								String handphoneNo = jsonChildNode.optString(
										"HandphoneNo").toString();
								String email = jsonChildNode.optString("Email")
										.toString();
								String termName = jsonChildNode.optString(
										"TermName").toString();
								String outstandingAmount = jsonChildNode.optString(
										"OutstandingAmount").toString();
								String haveTax = jsonChildNode.optString("HaveTax")
										.toString();
								String taxType = jsonChildNode.optString("TaxType")
										.toString();
								String taxValue = jsonChildNode.optString(
										"TaxValue").toString();
								String TaxCode = jsonChildNode.optString(
										"TaxCode").toString();
								String DiscountPercentage = jsonChildNode.optString("DiscountPercentage").toString();
								Log.d("DiscountPercent","-->"+DiscountPercentage);
							/*HashMap<String, String> customerhm = new HashMap<String, String>();
							if (referenceLocation != null
									&& !referenceLocation.isEmpty()) {
								customerhm.put(customercode, customername + "/"
										+ referenceLocation);
							} else {
								customerhm.put(customercode, customername);
							}

							mCustomerArrHm.add(customerhm);*/

								customer.setCustomerCode(customercode);
								customer.setCustomerName(customername);
								customer.setCustomerGroupCode(custgroup);
								customer.setAddress1(address1);
								customer.setAddress2(address2);
								customer.setAddress3(address3);
								customer.setPhoneNo(phoneNo);
								customer.setHandphoneNo(handphoneNo);
								customer.setEmail(email);
								customer.setTermName(termName);
								customer.setOutstandingAmount(outstandingAmount);
								customer.setHaveTax(haveTax);
								customer.setTaxType(taxType);
								customer.setTaxValue(taxValue);
								customer.setTaxCode(TaxCode);
								customer.setDiscountPerc(DiscountPercentage);
								mCustomerArrList.add(customer);

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {

			try {
				helper.dismissProgressView(sMainLayout);
                   if(mCurrencyArrList.size()>0) {
					   Log.d("mCurrencyArrList ", "" + mCurrencyArrList.toString());
					   // set Currency Code/
					   SalesOrderSetGet.setCurrencycode(mCurrencyCode);
					   // set Currency Rate/
					   SalesOrderSetGet.setCurrencyrate(mCurrencyArrList.get(1));
					   // //Set Server
					   // SalesOrderSetGet.setServerDate(mServerDate);

				   }
				if(mCustomerArrList.size()>0){
					mCustomerAdapter = new CustomerAdapter(getActivity(),mCustomerArrList);
					mListView.setAdapter(mCustomerAdapter);
				}

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
	private void loadCustomerTaxValue(int position) {
		if(mCustomerArrList.size()>0){
		Customer customer = mCustomerAdapter.getItem(position);
		mCustomerCodeStr = customer.getCustomerCode();
		mCustomerNameStr = customer.getCustomerName();
		String HaveTax = customer.getHaveTax();
		String TaxType = customer.getTaxType();
		String TaxValue = customer.getTaxValue();
		String DiscountPercentage = customer.getDiscountPerc();
		Log.d("DiscountPercentage","-->"+DiscountPercentage);
        String customerGroupCode = customer.getCustomerGroupCode();
		CustomerSetterGetter.setCustomerAddress1(customer.getAddress1());
		CustomerSetterGetter.setCustomerAddress2(customer.getAddress2());
		CustomerSetterGetter.setCustomerAddress3(customer.getAddress3());
		CustomerSetterGetter.setCustomerPhone(customer.getPhoneNo());
		CustomerSetterGetter.setCustomerHP(customer.getHandphoneNo());
		CustomerSetterGetter.setCustomerEmail(customer.getEmail());
		CustomerSetterGetter.setCustomerTerms(customer.getTermName());
		CustomerSetterGetter.setTotalOutstanding(customer.getOutstandingAmount());
		CustomerSetterGetter.setTaxCode(customer.getTaxCode());
		CustomerSetterGetter.setTaxPerc(TaxValue);
		CustomerSetterGetter.setTaxType(TaxType);
		CustomerSetterGetter.setDiscountPercentage(DiscountPercentage);

		if (HaveTax.matches("True") || HaveTax.matches("true")) {
			SalesOrderSetGet.setTaxValue(TaxValue);
			SalesOrderSetGet.setCompanytax(TaxType);
		} else {
			SalesOrderSetGet.setTaxValue("");
			SalesOrderSetGet.setCompanytax("Z");
		}
		Catalog.setCustomerCode(mCustomerCodeStr);
		Catalog.setCustomerName(mCustomerNameStr);	
		Catalog.setCustomerGroupCode(customerGroupCode);		
		Catalog.setUpdatedPrice(true);
		Catalog.setSearchVisible(false);
		//High light list view color
		mCustomerAdapter.notifyDataSetChanged();
		DBCatalog.deleteAllProduct();
		dbcatalog.truncateTables();		
		mPager.setCurrentItem(1);
		
		}
		
	}


	public void alertDialog() {

		Builder dialog = new Builder(getActivity());
		dialog.setTitle("Download")
				.setMessage("Do you want to synchronize image from server for offline")
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
						
								if (CarouselFragment.onlineMode.matches("True")) {
									if (CarouselFragment.checkOffline == false) {	
										offlineDatabase.dropProductMainImages();
										final String date = SalesOrderSetGet.getServerDate();
										imageDownloader = new OfflineDataDownloader(getActivity(),mValidUrl);
										imageDownloader.startImageDownload(true, "Downloading from server", date, "");
									}
								}
							}
						})

				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
		dialog.show();

	}
}
