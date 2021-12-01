package com.winapp.catalog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.Catalog;
import com.winapp.model.Customer;
import com.winapp.model.Product;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDataDownloader;
import com.winapp.offline.OfflineDatabase;
import com.winapp.printer.UIHelper;
import com.winapp.sot.OfflineSalesOrderWebService;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sotdetails.DBCatalog;

import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

// A simple {@link Fragment} subclass.

public class CarouselFragment extends BaseFragment implements OnClickListener {

	protected ViewPager mPager;
	private ViewPagerAdapter adapter;
	/*private static ImageButton mListingSearchIcon, mCartSaveIcon,
			mDownloadIcon, mChangeView, mCartIcon, mFilterIcon, mSearchIcon,
			mCartClearAll, mClose, mBack;*/
	private View view;
	public static TextView mTitle, mTab1, mTab2, mTab3, mTab4;
	public static LinearLayout offlineLayout;
	public static boolean checkOffline;
	public static String onlineMode = "", updateonlinemode = "";
	private OfflineCommon offlineCommon;
	private JSONObject mCustomerJSONObject, mServerDateJsonObject,
			mProductJSONObject;
	private JSONArray mCustomerJSONArray, mServerDateJsonArray,
			mProductJSONArray;
	private ArrayList<Customer> mCustomerArrList;
	private String mVanStr = "",mobileHaveOfflineMode="", mServerDate, mCompanyCodeStr = "",
			mCurrencyCode = "", mCustomerJsonString = "", mValidUrl,
			mProductJsonString, mServerDateJsonString, mDialogStatus = "";
	private ArrayList<HashMap<String, String>> mCustomerArrHm;
	private HashMap<String, String> mHashMap;
	private ArrayList<String> mCurrencyArrList;
	private static EditText mSearchEd;
	private RelativeLayout mCarouselLayout;
	private UIHelper helper;
	private Cursor cursor;
	private OfflineDatabase offlineDatabase;
	private CatalogProductDetailFragment catalogProductDetail;
	//public static RelativeLayout mCustomKeyboard;
	private ArrayList<Product> mProductList;
	private HashMap<String, ImageButton> mHashMapBtn;
	private OfflineDataDownloader imageDownloader;

	public CarouselFragment() {
		// Required empty public constructor
	}

	public static CarouselFragment newInstance(/*HashMap<String, TextView> hashMapTextV,HashMap<String, EditText> hashMapEditText,HashMap<String, RelativeLayout> hashMapLayout,
											   HashMap<String, ImageButton> hashMapIcon*/) {
		CarouselFragment frag = new CarouselFragment();
//		Bundle args = new Bundle();
//		args.putSerializable("CarouselTextV", hashMapTextV);
//		//args.putSerializable("CarouselEditText", hashMapEditText);
//		args.putSerializable("CarouselLayout", hashMapLayout);
//		args.putSerializable("CarouselIcon", hashMapIcon);
//		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_carousel, container, false);

//		HashMap<String ,ImageButton> hashMapIcon= (HashMap<String ,ImageButton>) getArguments().getSerializable("CarouselIcon");
//		mDownloadIcon = hashMapIcon.get("DownloadIcon");
//		mChangeView = hashMapIcon.get("ChangeView");
//		mCartIcon = hashMapIcon.get("CartIcon");
//		mFilterIcon = hashMapIcon.get("FilterIcon");
//		mSearchIcon = hashMapIcon.get("SearchIcon");
//		mCartSaveIcon = hashMapIcon.get("CartSaveIcon");
//		mListingSearchIcon = hashMapIcon.get("ListingSearchIcon");
//		mCartClearAll = hashMapIcon.get("CartClearAll");
//		mClose = hashMapIcon.get("Close");
//		mBack = hashMapIcon.get("Back");

//		HashMap<String ,RelativeLayout> hashMapLayout = (HashMap<String ,RelativeLayout>) getArguments().getSerializable("CarouselLayout");
//		mCustomKeyboard = hashMapLayout.get("RelativeLayout");

//		HashMap<String ,TextView> hashMapTextV = (HashMap<String ,TextView>) getArguments().getSerializable("CarouselTextV");
//		mCartText = hashMapTextV.get("CartText");
//		mTitle = hashMapTextV.get("TitleText");

		//HashMap<String ,EditText> hashMapEditText = (HashMap<String ,EditText>) getArguments().getSerializable("CarouselEditText");
		//mSearchEd = hashMapEditText.get("SearchEdt");

		// View ID
		mCarouselLayout = (RelativeLayout) view
				.findViewById(R.id.layout_carousel);
		mPager = (ViewPager) view.findViewById(R.id.viewpager);
		// Offline ID
		offlineLayout = (LinearLayout) view
				.findViewById(R.id.inv_offlineLayout);
		mPager.setOnPageChangeListener(onPageChangeListener);

		mTab1 = (TextView) view.findViewById(R.id.listing_screen_tab);
		mTab2 = (TextView) view.findViewById(R.id.customer_screen_tab);
		mTab3 = (TextView) view.findViewById(R.id.addProduct_screen_tab);
		mTab4 = (TextView) view.findViewById(R.id.sum_screen_tab);
		// Tab Default Selection
		view.findViewById(R.id.customer_screen_tab).setBackgroundResource(
				R.drawable.tab_select);
		// object Initialization
		helper = new UIHelper(getActivity());
		catalogProductDetail = new CatalogProductDetailFragment();
		// Default Action Bar Title
		SOCatalogActivity.mTitle.setText("Catalog");

		// onClick Listener
		mTab1.setOnClickListener(this);
		mTab2.setOnClickListener(this);
		mTab3.setOnClickListener(this);
		mTab4.setOnClickListener(this);

		mTab1.setText("Customer");
		mTab2.setText("Catalog");
		mTab3.setText("View Cart");
		mTab4.setText("Listing");

		view.findViewById(R.id.linear_layout1).setVisibility(View.GONE);
		view.findViewById(R.id.linear_layout2).setVisibility(View.VISIBLE);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Variable Object Initialization
		mCustomerArrList = new ArrayList<Customer>();
		mHashMap = new HashMap<String, String>();
		mCurrencyArrList = new ArrayList<String>();
		mCustomerArrHm = new ArrayList<HashMap<String, String>>();
		mProductList = new ArrayList<Product>();
		mHashMapBtn = new HashMap<String, ImageButton>();
		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();


		//checkInternetStatus(getActivity());

		// DB Initialization
		SOTDatabase.init(getActivity());
		DBCatalog.init(getActivity());
		OfflineDatabase.init(getActivity());
		offlineDatabase = new OfflineDatabase(getActivity());
		// Get URL from DB
		mValidUrl = FWMSSettingsDatabase.getUrl();
		new SalesOrderWebService(mValidUrl);
		// Get VanCode from DB
		mVanStr = SOTDatabase.getVandriver();

	//	new GetServerDate().execute();

		loadViewPagerAdapter();

	}

	private void loadViewPagerAdapter() {
//		mHashMapBtn.put("DownloadIcon", mDownloadIcon);
//		mHashMapBtn.put("ChangeView", mChangeView);
//		mHashMapBtn.put("CartIcon", mCartIcon);
//		mHashMapBtn.put("FilterIcon", mFilterIcon);
//		mHashMapBtn.put("SearchIcon", mSearchIcon);
//		mHashMapBtn.put("CartSaveIcon", mCartSaveIcon);
//		mHashMapBtn.put("CartClearAll", mCartClearAll);
//		mHashMapBtn.put("ListingSearchIcon", mListingSearchIcon);
//		mHashMapBtn.put("Close", mClose);
//		mHashMapBtn.put("Back", mBack);
		// Note that we are passing childFragmentManager, not FragmentManager

		adapter = new ViewPagerAdapter(getChildFragmentManager()/*
				mCustomerArrHm, mCustomerArrList, mProductList, mCartText,
				mSearchEd, mHashMapBtn*/);
		mPager.setAdapter(adapter);
		mPager.setCurrentItem(1);
		mPager.setOffscreenPageLimit(4);
	}

	// Capture ViewPager page swipes
	private ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			super.onPageSelected(position);
			int orientation = getActivity().getResources().getConfiguration().orientation;
			double screenInches = helper.displayMetrics();
			Fragment fragment = ((ViewPagerAdapter) mPager.getAdapter())
					.getRegisteredFragment(position);
			if (position == 0) {
				CatalogCustomerFragment customerFragment = (CatalogCustomerFragment)adapter.getItem(position);
				customerFragment.sethasExecuted(true);
				mTab1.setBackgroundResource(R.drawable.tab_select_left);
				mTab2.setBackgroundResource(R.drawable.rounded_tab_center);
				mTab3.setBackgroundResource(R.drawable.rounded_tab_center);
				mTab4.setBackgroundResource(R.drawable.rounded_tab_right);

				Log.d("mobileHaveOfflineMode","-->"+mobileHaveOfflineMode);
				if(mobileHaveOfflineMode.matches("1")){
					SOCatalogActivity.mDownloadIcon.setVisibility(View.VISIBLE);
				}else{
					SOCatalogActivity.mDownloadIcon.setVisibility(View.GONE);
				}
				SOCatalogActivity.mChangeView.setVisibility(View.GONE);
				SOCatalogActivity.mCartIcon.setVisibility(View.GONE);
				SOCatalogActivity.mFilterIcon.setVisibility(View.GONE);
				SOCatalogActivity.mSearchIcon.setVisibility(View.GONE);
				SOCatalogActivity.mCartText.setVisibility(View.GONE);
				SOCatalogActivity.mCartSaveIcon.setVisibility(View.GONE);
				SOCatalogActivity.mListingSearchIcon.setVisibility(View.GONE);
				SOCatalogActivity.mCartClearAll.setVisibility(View.GONE);
				SOCatalogActivity.mCustomKeyboard.setVisibility(View.GONE);
				SOCatalogActivity.mSearchEd.setVisibility(View.GONE);
				SOCatalogActivity.mClose.setVisibility(View.GONE);
				SOCatalogActivity.mBack.setVisibility(View.GONE);
				SOCatalogActivity.mTitle.setVisibility(View.VISIBLE);
				SOCatalogActivity.mTitle.setText("Customer");
				refreshAction(fragment);
			} else if (position == 1) {
				CatalogProductFragment productFragment = (CatalogProductFragment)adapter.getItem(position);
				productFragment.sethasExecuted(true);
				mTab1.setBackgroundResource(R.drawable.rounded_tab_left);
				mTab2.setBackgroundResource(R.drawable.tab_select);
				mTab3.setBackgroundResource(R.drawable.rounded_tab_center);
				mTab4.setBackgroundResource(R.drawable.rounded_tab_right);
				SOCatalogActivity.mDownloadIcon.setVisibility(View.GONE);
				SOCatalogActivity.mTitle.setText("Catalog");
				refreshViewAction(fragment);
				checkOrientation(orientation, screenInches);
			} else if (position == 2) {
				mTab1.setBackgroundResource(R.drawable.rounded_tab_left);
				mTab2.setBackgroundResource(R.drawable.rounded_tab_center);
				mTab3.setBackgroundResource(R.drawable.tab_select);
				mTab4.setBackgroundResource(R.drawable.rounded_tab_right);
				SOCatalogActivity.mDownloadIcon.setVisibility(View.GONE);
				SOCatalogActivity.mChangeView.setVisibility(View.GONE);
				SOCatalogActivity.mCartIcon.setVisibility(View.GONE);
				SOCatalogActivity.mFilterIcon.setVisibility(View.GONE);
				SOCatalogActivity.mSearchIcon.setVisibility(View.GONE);
				SOCatalogActivity.mCartText.setVisibility(View.GONE);
				SOCatalogActivity.mCartClearAll.setVisibility(View.VISIBLE);
				SOCatalogActivity.mCartSaveIcon.setVisibility(View.VISIBLE);
				SOCatalogActivity.mListingSearchIcon.setVisibility(View.GONE);
				SOCatalogActivity.mCustomKeyboard.setVisibility(View.GONE);
				SOCatalogActivity.mSearchEd.setVisibility(View.GONE);
				SOCatalogActivity.mClose.setVisibility(View.GONE);
				SOCatalogActivity.mBack.setVisibility(View.GONE);
				SOCatalogActivity.mTitle.setText("View Cart");
				SOCatalogActivity.mTitle.setVisibility(View.VISIBLE);
				refreshAction(fragment);

			} else if (position == 3) {
				CatalogListingFragment listingFragment = (CatalogListingFragment)adapter.getItem(position);
				listingFragment.sethasExecuted(true);
				mTab1.setBackgroundResource(R.drawable.rounded_tab_left);
				mTab2.setBackgroundResource(R.drawable.rounded_tab_center);
				mTab3.setBackgroundResource(R.drawable.rounded_tab_center);
				mTab4.setBackgroundResource(R.drawable.tab_select_right);
				SOCatalogActivity.mDownloadIcon.setVisibility(View.GONE);
				SOCatalogActivity.mChangeView.setVisibility(View.GONE);
				SOCatalogActivity.mCartIcon.setVisibility(View.GONE);
				SOCatalogActivity.mFilterIcon.setVisibility(View.GONE);
				SOCatalogActivity.mSearchIcon.setVisibility(View.GONE);
				SOCatalogActivity.mCartText.setVisibility(View.GONE);
				SOCatalogActivity.mCartClearAll.setVisibility(View.GONE);
				SOCatalogActivity.mCustomKeyboard.setVisibility(View.GONE);
				SOCatalogActivity.mCartSaveIcon.setVisibility(View.GONE);
				SOCatalogActivity.mSearchEd.setVisibility(View.GONE);
				SOCatalogActivity.mClose.setVisibility(View.GONE);
				SOCatalogActivity.mBack.setVisibility(View.GONE);
				SOCatalogActivity.mListingSearchIcon.setVisibility(View.VISIBLE);
				SOCatalogActivity.mTitle.setText("Listing");
				SOCatalogActivity.mTitle.setVisibility(View.VISIBLE);

				refreshAction(fragment);

			}
		}
	};

	private void refreshAction(Fragment fragment) {
		if (fragment != null) {
			fragment.onResume();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.listing_screen_tab:
			mPager.setCurrentItem(0);
			break;
		case R.id.customer_screen_tab:
			mPager.setCurrentItem(1);
			break;
		case R.id.addProduct_screen_tab:
			mPager.setCurrentItem(2);
			break;
		case R.id.sum_screen_tab:
			mPager.setCurrentItem(3);
			break;
		default:
			break;

		}
	}

	public void refreshViewAction(Fragment fragment) {
		cursor = DBCatalog.getCursor();
		int cartSize = cursor.getCount();
		if (Catalog.isChildFragmentVisible()) {
			SOCatalogActivity.mDownloadIcon.setVisibility(View.GONE);
			SOCatalogActivity.mChangeView.setVisibility(View.GONE);
			SOCatalogActivity.mCartIcon.setVisibility(View.VISIBLE);
			SOCatalogActivity.mFilterIcon.setVisibility(View.GONE);
			SOCatalogActivity.mSearchIcon.setVisibility(View.GONE);
			SOCatalogActivity.mCartSaveIcon.setVisibility(View.GONE);
			SOCatalogActivity.mListingSearchIcon.setVisibility(View.GONE);
			SOCatalogActivity.mCartClearAll.setVisibility(View.GONE);
			SOCatalogActivity.mSearchEd.setVisibility(View.GONE);
			SOCatalogActivity.mClose.setVisibility(View.GONE);
			SOCatalogActivity.mBack.setVisibility(View.GONE);
			catalogProductDetail.onResume();
			/*
			 * if (cartSize > 0) { if(Catalog.isSearchVisible()){
			 * mCartText.setVisibility(View.GONE); }else{
			 * mCartText.setVisibility(View.VISIBLE); }
			 * 
			 * } else { mCartText.setVisibility(View.GONE); }
			 */
			if (Catalog.isUpdatedPrice()) {
				onBackPressed();
			}
		} else {
			if (Catalog.isSearchVisible()) {
				SOCatalogActivity.mChangeView.setVisibility(View.GONE);
				SOCatalogActivity.mCartIcon.setVisibility(View.GONE);
				SOCatalogActivity.mFilterIcon.setVisibility(View.GONE);
				SOCatalogActivity.mSearchIcon.setVisibility(View.GONE);
				SOCatalogActivity.mClose.setVisibility(View.VISIBLE);
				SOCatalogActivity.mBack.setVisibility(View.VISIBLE);
				SOCatalogActivity.mSearchEd.setVisibility(View.VISIBLE);
				SOCatalogActivity.mTitle.setVisibility(View.GONE);
				SOCatalogActivity.mCartText.setVisibility(View.GONE);
			} else {
				SOCatalogActivity.mChangeView.setVisibility(View.VISIBLE);
				SOCatalogActivity.mCartIcon.setVisibility(View.VISIBLE);
				SOCatalogActivity.mFilterIcon.setVisibility(View.VISIBLE);
				SOCatalogActivity.mSearchIcon.setVisibility(View.VISIBLE);
				SOCatalogActivity.mClose.setVisibility(View.GONE);
				SOCatalogActivity.mBack.setVisibility(View.GONE);
				SOCatalogActivity.mTitle.setVisibility(View.VISIBLE);
				// if cart size greater than zero
				if (cartSize > 0) {
					SOCatalogActivity.mCartText.setVisibility(View.VISIBLE);
				} else {
					SOCatalogActivity.mCartText.setVisibility(View.GONE);
				}
			}

			SOCatalogActivity.mDownloadIcon.setVisibility(View.GONE);
			SOCatalogActivity.mCartSaveIcon.setVisibility(View.GONE);
			SOCatalogActivity.mListingSearchIcon.setVisibility(View.GONE);
			SOCatalogActivity.mCartClearAll.setVisibility(View.GONE);

			if (fragment != null) {
				fragment.onResume();
			}

		}

	}

	public void checkOrientation(int orientation, double screenInches) {

		// custom keyboard is visible if Screen Inches greater than seven
		if (screenInches > 7) {
			if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				if (Catalog.isSearchVisible()) {
					SOCatalogActivity.mCustomKeyboard.setVisibility(View.GONE);
					SOCatalogActivity.mTitle.setVisibility(View.GONE);
				} else {
					SOCatalogActivity.mCustomKeyboard.setVisibility(View.VISIBLE);
					SOCatalogActivity.mTitle.setVisibility(View.GONE);
				}

			} else {
				if (Catalog.isSearchVisible()) {
					SOCatalogActivity.mCustomKeyboard.setVisibility(View.GONE);
					SOCatalogActivity.mTitle.setVisibility(View.GONE);
				} else {
					SOCatalogActivity.mCustomKeyboard.setVisibility(View.GONE);
					SOCatalogActivity.mTitle.setVisibility(View.VISIBLE);
				}
			}
		} else {
			SOCatalogActivity.mCustomKeyboard.setVisibility(View.GONE);
			SOCatalogActivity.mTitle.setVisibility(View.VISIBLE);
		}
	}

	/*
	 * private void refreshViewAction(Fragment fragment){ cursor =
	 * DBCatalog.getCursor(); int cartSize = cursor.getCount();
	 * if(Catalog.isChildFragmentVisible()){
	 * mChangeView.setVisibility(View.GONE);
	 * mCartIcon.setVisibility(View.VISIBLE);
	 * mFilterIcon.setVisibility(View.GONE);
	 * mSearchIcon.setVisibility(View.GONE);
	 * mCartSaveIcon.setVisibility(View.GONE);
	 * mListingSearchIcon.setVisibility(View.GONE);
	 * 
	 * catalogProductDetail.onResume(); if(Catalog.isUpdatedPrice()){
	 * onBackPressed(); } }else{ mChangeView.setVisibility(View.VISIBLE);
	 * mCartIcon.setVisibility(View.VISIBLE);
	 * mFilterIcon.setVisibility(View.VISIBLE);
	 * mSearchIcon.setVisibility(View.GONE);
	 * mCartSaveIcon.setVisibility(View.GONE);
	 * mListingSearchIcon.setVisibility(View.GONE); if (fragment != null) {
	 * fragment.onResume(); } } if (cartSize > 0) {
	 * mCartText.setVisibility(View.VISIBLE); } else {
	 * mCartText.setVisibility(View.GONE); }
	 * 
	 * }
	 */

	private class GetServerDate extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			helper.showProgressView(mCarouselLayout);
			mDialogStatus = "";
			mDialogStatus = checkInternetStatus(getActivity());
			mServerDate = "";
			mHashMap.clear();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			mHashMap.put("CompanyCode", mCompanyCodeStr);

			SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
			String finalDate = timeFormat.format(new Date());
			System.out.println(finalDate);
			try {

				if (onlineMode.matches("True")) {
					if (checkOffline == true) { // temp_offline

						if (mDialogStatus.matches("true")) {
							Log.d("temp_offline ", "temp_offline");
							mServerDate = finalDate;
						} else {
							getActivity().finish();
						}

					} else { // Online
						Log.d("Onlline ", "Onlline");
						// Call GetServerDate
						mServerDateJsonString = SalesOrderWebService
								.getSODetail(mHashMap, "fncGetServerDate");
						Log.d("mServerDateJsonString ", ""
								+ mServerDateJsonString);
						mServerDateJsonObject = new JSONObject(
								mServerDateJsonString);
						mServerDateJsonArray = mServerDateJsonObject
								.optJSONArray("SODetails");
						JSONObject jsonobject = mServerDateJsonArray
								.getJSONObject(0);
						mServerDate = jsonobject.getString("ServerDate");
					}

				} else if (onlineMode.matches("False")) { // permanent_offline
					Log.d("permanent_offline ", "permanent_offline");
					mServerDate = finalDate;
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				/*new Thread() {
					@Override
					public void run() {

						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									// @Offline image download
									if (onlineMode.matches("True")) {
										if (checkOffline == false) {

											Cursor mainImageCursor = OfflineDatabase
													.getProductMainImage();
											Cursor subImageCursor = OfflineDatabase
													.getProductSubImage();

											if (mainImageCursor.getCount() > 0) {
												final String modifydate = OfflineDatabase
														.getCatalogModifyDate();
												imageDownloader = new OfflineDataDownloader(
														getActivity(),
														mValidUrl);
												imageDownloader
														.startImageDownload(
																true,
																"Updating image from server",
																mServerDate,
																modifydate);
											} else {
												imageDownloader = new OfflineDataDownloader(
														getActivity(),
														mValidUrl);
												imageDownloader
														.startImageDownload(
																true,
																"Downloading image from server",
																mServerDate, "");
											}
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}
				}.start();*/

			}

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {

			try {
				// Set Server
				SalesOrderSetGet.setServerDate(mServerDate);

				// Get CompanyCode from Pojo class
				mCompanyCodeStr = SupplierSetterGetter.getCompanyCode();
				// Get Customer Data
				new GetCustomerData().execute();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// /
	private class GetCustomerData extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// helper.showProgressView(mCarouselLayout);
			mDialogStatus = "";
			mDialogStatus = checkInternetStatus(getActivity());
			// mServerDate="";
			mHashMap.clear();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			mHashMap.put("CompanyCode", mCompanyCodeStr);

			SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
			String finalDate = timeFormat.format(new Date());
			System.out.println(finalDate);
			try {

				if (onlineMode.matches("True")) {
					if (checkOffline == true) { // temp_offline

						if (mDialogStatus.matches("true")) {
							Log.d("temp_offline ", "temp_offline");
							// mServerDate = finalDate;
							// General Settings Call
							mCurrencyCode = OfflineSalesOrderWebService
									.generalSettingsServiceOffline();
							// Currency Call
							mCurrencyArrList = OfflineSalesOrderWebService
									.getCurrencyValuesOffline(mCurrencyCode);
							// Get Product
							String result = offlineDatabase.getProduct();
							mProductJsonString = " { SODetails: "
									+ result.toString() + "}";
							mProductJSONObject = new JSONObject(
									mProductJsonString);
							mProductJSONArray = mProductJSONObject
									.optJSONArray("SODetails");

							// Customer Call
							mHashMap.put("VanCode", mVanStr);
							mCustomerJsonString = getCustomerOffline(mHashMap);
							Log.d("mCustomerJsonString ", ""
									+ mCustomerJsonString);
							mCustomerJSONObject = new JSONObject(
									mCustomerJsonString);
							mCustomerJSONArray = mCustomerJSONObject
									.optJSONArray("JsonArray");
						} else {
							getActivity().finish();
						}

					} else { // Onlline
						Log.d("Onlline ", "Onlline");
						// // Call GetServerDate
						// mServerDateJsonString =
						// SalesOrderWebService.getSODetail(mHashMap,
						// "fncGetServerDate");
						// Log.d("mServerDateJsonString ", "" +
						// mServerDateJsonString);
						// mServerDateJsonObject = new
						// JSONObject(mServerDateJsonString);
						// mServerDateJsonArray =
						// mServerDateJsonObject.optJSONArray("SODetails");
						// JSONObject jsonobject =
						// mServerDateJsonArray.getJSONObject(0);
						// mServerDate = jsonobject.getString("ServerDate");

						// General Settings Call
						mCurrencyCode = SalesOrderWebService
								.generalSettingsService("fncGetGeneralSettings");
						// Currency Call
						mCurrencyArrList = SalesOrderWebService
								.getCurrencyValues("fncGetCurrency",
										mCurrencyCode);

						// Call Get Product
						mProductJsonString = SalesOrderWebService.getSODetail(
								mHashMap, "fncGetProductForSearch");
						mProductJSONObject = new JSONObject(mProductJsonString);
						mProductJSONArray = mProductJSONObject
								.optJSONArray("SODetails");

						// Customer CALL
						mHashMap.put("VanCode", mVanStr);
						mCustomerJsonString = SalesOrderWebService.getSODetail(
								mHashMap, "fncGetCustomer");
						Log.d("mCustomerJsonString ", "" + mCustomerJsonString);
						mCustomerJSONObject = new JSONObject(
								mCustomerJsonString);
						mCustomerJSONArray = mCustomerJSONObject
								.optJSONArray("SODetails");
					}

				} else if (onlineMode.matches("False")) { // permanent_offline
					Log.d("permanent_offline ", "permanent_offline");
					// mServerDate = finalDate;
					// General Settings Call
					mCurrencyCode = OfflineSalesOrderWebService
							.generalSettingsServiceOffline();
					mCurrencyArrList = OfflineSalesOrderWebService
							.getCurrencyValuesOffline(mCurrencyCode);
					// Get Products
					String result = offlineDatabase.getProduct();
					mProductJsonString = " { SODetails: " + result.toString()
							+ "}";
					mProductJSONObject = new JSONObject(mProductJsonString);
					mProductJSONArray = mProductJSONObject
							.optJSONArray("SODetails");

					// Customer Call
					mHashMap.put("VanCode", mVanStr);
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

				int lengthProductJsonArr = mProductJSONArray.length();

				if (lengthProductJsonArr > 0) {
					for (int i = 0; i < lengthProductJsonArr; i++) {
						JSONObject jsonChildNode;
						try {
							Product product = new Product();
							jsonChildNode = mProductJSONArray.getJSONObject(i);
							product.setProductCode(jsonChildNode.optString(
									"ProductCode").toString());
							product.setProductName(jsonChildNode.optString(
									"ProductName").toString());
							mProductList.add(product);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}

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

							HashMap<String, String> customerhm = new HashMap<String, String>();
							if (referenceLocation != null
									&& !referenceLocation.isEmpty()) {
								customerhm.put(customercode, customername + "/"
										+ referenceLocation);
							} else {
								customerhm.put(customercode, customername);
							}

							mCustomerArrHm.add(customerhm);

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

							mCustomerArrList.add(customer);

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
				Log.d("mCurrencyArrList ", "" + mCurrencyArrList.toString());
				// set Currency Code/
				SalesOrderSetGet.setCurrencycode(mCurrencyCode);
				// set Currency Rate/
				SalesOrderSetGet.setCurrencyrate(mCurrencyArrList.get(1));
				// //Set Server
				// SalesOrderSetGet.setServerDate(mServerDate);

			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}

			loadViewPagerAdapter();

			helper.dismissProgressView(mCarouselLayout);
		}
	}
	protected String checkInternetStatus(Activity mActivity) {
		String internetStatus = "";
		OfflineDatabase.init(mActivity);
		new OfflineSalesOrderWebService(mActivity);
		onlineMode = OfflineDatabase.getOnlineMode();
		String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			if(mobileHaveOfflineMode.matches("1")){
		Log.d("Offline onlineMode", "" + onlineMode);
		Log.d("Offline checkOffline", "" + checkOffline);
		offlineCommon = new OfflineCommon(mActivity);
		checkOffline = OfflineCommon.isConnected(mActivity);

		if (onlineMode.matches("True")) {
			if (checkOffline == true) {
				String Off_dialog = OfflineDatabase
						.getInternetMode("OfflineDialog");
				if (Off_dialog.matches("true")) {
					internetStatus = "true";
					updateonlinemode = "false";
				} else {
					offlineCommon.OfflineAlertDialog();
					Boolean dialogStatus = offlineCommon.showDialog();
					OfflineDatabase.updateInternetMode("OfflineDialog",
							dialogStatus + "");
					Log.d("Offline DialogStatus", "" + dialogStatus);
					internetStatus = "" + dialogStatus;
					updateonlinemode = "true";
				}
			} else if (checkOffline == false) {
				String on_dialog = OfflineDatabase
						.getInternetMode("OnlineDialog");
				if (on_dialog.matches("true")) {
					internetStatus = "true";
					updateonlinemode = "false";
				} else {
					offlineCommon.onlineAlertDialog();
					boolean dialogStatus = offlineCommon.showDialog();
					OfflineDatabase.updateInternetMode("OnlineDialog",
							dialogStatus + "");
					Log.d("Online DialogStatus", "" + dialogStatus);
					internetStatus = "" + dialogStatus;
					updateonlinemode = "true";
				}
			}
		}
		onlineMode = OfflineDatabase.getOnlineMode();
		if (onlineMode.matches("True")) {
			CarouselFragment.offlineLayout.setVisibility(View.GONE);
			if (checkOffline == true) {
				if (internetStatus.matches("true")) {
					CarouselFragment.offlineLayout.setVisibility(View.VISIBLE);
					CarouselFragment.offlineLayout
							.setBackgroundResource(R.drawable.temp_offline_pattern_bg);
				}
			}

		} else if (onlineMode.matches("False")) {
			CarouselFragment.offlineLayout.setVisibility(View.VISIBLE);
		}

			}else{
				internetStatus = "false";
			}
		}else{
			internetStatus = "false";
		}
		return internetStatus;
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



	/**
	 * Retrieve the currently visible Tab Fragment and propagate the
	 * onBackPressed callback
	 * 
	 * @return true = if this fragment and/or one of its associates Fragment can
	 *         handle the backPress
	 **/
	@Override
	public boolean onBackPressed() {
		try {
			// currently visible tab Fragment
			OnBackPressListener currentFragment = (OnBackPressListener) adapter
					.getRegisteredFragment(mPager.getCurrentItem());
			if (currentFragment != null) {
				int orientation = getActivity().getResources()
						.getConfiguration().orientation;
				double screenInches = helper.displayMetrics();
				// lets see if the currentFragment or any of its childFragment
				// can
				Fragment fragment = ((ViewPagerAdapter) mPager.getAdapter())
						.getRegisteredFragment(1);
				Catalog.setChildFragmentVisible(false);
				refreshViewAction(fragment);
				checkOrientation(orientation, screenInches);
				// handle onBackPressed
				return currentFragment.onBackPressed();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// this Fragment couldn't handle the onBackPressed call
		return false;
	}
	/*
	 * public boolean onBackPressed() { // currently visible tab Fragment
	 * OnBackPressListener currentFragment = (OnBackPressListener) adapter
	 * .getRegisteredFragment(mPager.getCurrentItem()); if (currentFragment !=
	 * null) { // lets see if the currentFragment or any of its childFragment
	 * can Fragment fragment = ((ViewPagerAdapter)
	 * mPager.getAdapter()).getRegisteredFragment(1);
	 * Catalog.setChildFragmentVisible(false); refreshViewAction(fragment); //
	 * handle onBackPressed return currentFragment.onBackPressed(); } // this
	 * Fragment couldn't handle the onBackPressed call return false; }
	 */

}
