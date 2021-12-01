package com.winapp.catalog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.TransactionTooLargeException;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.winapp.adapter.Attribute;
import com.winapp.adapter.SearchAdapter;
import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.helper.Catalog;
import com.winapp.helper.Constants;
import com.winapp.helper.XMLParser;
import com.winapp.model.Customer;
import com.winapp.model.Product;
import com.winapp.offline.OfflineDatabase;
import com.winapp.printer.UIHelper;
import com.winapp.sot.ColorAttributeDialog;
import com.winapp.sot.FilterCS;
import com.winapp.sot.FilterCS.OnFilterCompletionListener;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesProductWebService;
import com.winapp.sotdetails.DBCatalog;
import com.winapp.util.EndlessScrollListener;
import com.winapp.util.XMLAccessTask;
import com.winapp.util.XMLAccessTask.CallbackInterface;
import com.winapp.util.XMLAccessTask.ErrorType;

import static com.winapp.SFA.R.id.sl_qty;

public class CatalogProductFragment extends BaseFragment {

	private GridView mGridView;
	private ArrayList<Product> productList, loadmoreProduct;
	// private ProgressBar load_more;
	private ProductMainImageAdapter mainImageAdapter;
	// XML node keys
	private static final String KEY_PRODUCTCODE = "ProductCode";
	private static final String KEY_PRODUCTNAME = "ProductName";
	private static final String KEY_CATEGORYCODE = "CategoryCode";
	private static final String KEY_SUBCATEGORYCODE = "SubCategoryCode";
	private static final String KEY_UOMCODE = "UomCode";
	private static final String KEY_PCSPERCARTON = "PcsPerCarton";
	private static final String KEY_WHOLESALEPRICE = "WholeSalePrice";
	private static final String KEY_PRODUCTIMAGE = "ProductImage";
	private static final String KEY_RETAILPRICE = "RetailPrice";
	private static final String KEY_SPECIFICATION = "Specification";
	private static final String KEY_HAVEATTRIBUTE = "HaveAttribute";

	private HashMap<String, String> params;
	private int mPosition = 0, orientation, totalItem, currentPage = -1,
			pageNo = 0;

	private String mkeyActionDoneStr = "", mCategory = "",
			mChangingView = "ListView", mSubcategory = "", valid_url = "",
			mCustomerGroupCode = "", jsonString = null, mDialogStatus = "",mCustomerCode = "",mProductName = "";
	private ViewPager mPager;
	private View view, keyViewActionDone;
//	private ImageButton mChangeView, mCartIcon, mFilterIcon, mSearchIcon,
//			mClose, mBack;
	private double screenInches;
	private CarouselFragment mCarouselFragment;
	private OfflineDatabase offlineDatabase;
	private JSONObject jsonResponse;
	private JSONArray jsonMainNode;
	private EditText mEditValue;
	private FilterCS filtercs;
	private CatalogProductDetailFragment catalogproductdetailfragment;
	private LinearLayout mCatalogLayout, load_more, mGridLayout, mSearchLayout;
	private ArrayList<Product> mProductList;
	//private SearchAdapter mSearchAdapter;
	private ListView mListView;
	private boolean isScrollListener = false;
	private String catalogSaveType="",mobileHaveOfflineMode="";
	private UIHelper helper;
    public static boolean bExecuted,isSearch;
	private ArrayList<Attribute> mAttributeArr;
	public CatalogProductFragment() {
		// Required empty public constructor
	}


	public static CatalogProductFragment newInstance(boolean isLoading/*HashMap<String, ImageButton> mHashMapIcon, HashMap<String, TextView> mHashMapTxtV,HashMap<String, EditText> mHashMapEdt,ArrayList<Product> productList*/) {
		CatalogProductFragment frag = new CatalogProductFragment();

	    Bundle args = new Bundle();
		args.putBoolean("isLoading", isLoading);
//		args.putSerializable("CatalogTextV", mHashMapTxtV);
//		args.putSerializable("CatalogEdtV", mHashMapEdt);
//		args.putSerializable("ProductObjArr", productList);
		frag.setArguments(args);
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
		view = inflater.inflate(R.layout.fragment_catalog, container, false);
        bExecuted = getArguments().getBoolean("isLoading");

		mCatalogLayout = (LinearLayout) view.findViewById(R.id.catalog_layout);
		mGridView = (GridView) view.findViewById(R.id.gridViewCustom);
		mListView = (ListView) view.findViewById(R.id.listView);
		// Object Initialization
		helper = new UIHelper(getActivity());
		params = new HashMap<String, String>();
		productList = new ArrayList<Product>();
		filtercs = new FilterCS(getActivity());
		mainImageAdapter = new ProductMainImageAdapter();
		mCarouselFragment = new CarouselFragment();
		// DB Initialization
		FWMSSettingsDatabase.init(getActivity());
		// Get URL from DB
		valid_url = FWMSSettingsDatabase.getUrl();

		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();

		// Get CustomerCode from Pojo class
		mCustomerGroupCode = Catalog.getCustomerGroupCode();
		offlineDatabase = new OfflineDatabase(getActivity());

		mDialogStatus = mCarouselFragment.checkInternetStatus(getActivity());
		Log.d("checkInternetStatus", "" + mDialogStatus);
		params.put("ProductCode", "");
		params.put("CategoryCode", mCategory);
		params.put("SubCategoryCode", mSubcategory);
		params.put("CustomerGroupCode", mCustomerGroupCode);
		params.put("PageNo", String.valueOf(pageNo));
		params.put("CustomerCode", mCustomerCode);
		params.put("ProductName", mProductName);
		params.put("TranType", catalogSaveType);

		try {
			if (CarouselFragment.onlineMode.matches("True")) {
				if (CarouselFragment.checkOffline == true) {
					if (mDialogStatus.matches("true")) { // Temp Offline
						helper.showProgressView(mCatalogLayout);
						loadProductMainImageOffline(params);
					} else {
						Log.d("CheckOffline Alert -->", "False");
						if(mobileHaveOfflineMode.matches("1")) {
							getActivity().finish();
						}
					}
				} else { // Online
					helper.showProgressView(mCatalogLayout);
					Log.d("loadProductMainImage >", "1");
					loadProductMainImage(params);
				}
			} else if (CarouselFragment.onlineMode.matches("False")) { // Permnt
																		// Offline
				helper.showProgressView(mCatalogLayout);
				loadProductMainImageOffline(params);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}



		return view;
	}

	@Override
	public void onResume() {
		// View ID
		mGridView = (GridView) view.findViewById(R.id.gridViewCustom);
		load_more = (LinearLayout) view.findViewById(R.id.load_more);
		mPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
		mListView = (ListView) view.findViewById(R.id.listView);
		mCatalogLayout = (LinearLayout) view.findViewById(R.id.catalog_layout);
		mGridLayout = (LinearLayout) view.findViewById(R.id.gridView_layout);
		mSearchLayout = (LinearLayout) view.findViewById(R.id.search_layout);
		// Object Initialization

		params = new HashMap<String, String>();
		mAttributeArr = new ArrayList<>();
        if(hasExecuted()){
            helper = new UIHelper(getActivity());
        }
		// DB Initialization
		FWMSSettingsDatabase.init(getActivity());
		// Get URL from DB
		valid_url = FWMSSettingsDatabase.getUrl();
		// new SalesOrderWebService(valid_url);

		String catalogType = FWMSSettingsDatabase.getCatalogTypeStr();
		Log.d("catalogSaveType", catalogType);

		if(catalogType.matches("Invoice")){
			catalogSaveType = "IN";
		}else{
			catalogSaveType="SO";
		}

		// Get CustomerCode from Pojo class
		mCustomerGroupCode = Catalog.getCustomerGroupCode();
		mCustomerCode = Catalog.getCustomerCode();

		offlineDatabase = new OfflineDatabase(getActivity());

		mDialogStatus = mCarouselFragment.checkInternetStatus(getActivity());

		SOCatalogActivity.mFilterIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				filtercs.filterDialog();
				filtercs.OnFilterCompletionListener(new OnFilterCompletionListener() {
					@Override
					public void onFilterCompleted(String category,
							String subcategory) {
						helper.showProgressView(mCatalogLayout);
						pageNo = 0;
						currentPage = 0;
						mCategory = category;
						mSubcategory = subcategory;
						mDialogStatus = mCarouselFragment
								.checkInternetStatus(getActivity());
						params.put("ProductCode", "");
						params.put("CategoryCode", mCategory);
						params.put("SubCategoryCode", mSubcategory);
						params.put("CustomerGroupCode", mCustomerGroupCode);
						params.put("PageNo", String.valueOf(pageNo));
						params.put("CustomerCode", mCustomerCode);
						params.put("TranType", catalogSaveType);
						params.put("ProductName", mProductName);

						try {
							if (CarouselFragment.onlineMode.matches("True")) {
								if (CarouselFragment.checkOffline == true) {
									if (mDialogStatus.matches("true")) { // Temp
																			// Offline
										loadProductMainImageOffline(params);
									} else {
										Log.d("CheckOffline Alert -->", "False");
										if(mobileHaveOfflineMode.matches("1")) {
											getActivity().finish();
										}
									}
								} else { // Online
									loadProductMainImage(params);
								}
							} else if (CarouselFragment.onlineMode
									.matches("False")) { // Permnt Offline
								loadProductMainImageOffline(params);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});
			}
		});

		SOCatalogActivity.mChangeView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				screenInches = displayMetrics();
				orientation = getResources().getConfiguration().orientation;
				if (mChangingView.matches("GridView")) {
					SOCatalogActivity.mChangeView.setImageResource(R.mipmap.grid_2);
					mChangingView = "ListView";
					if (screenInches > 7) {
						if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
							// Landscape
							mGridView.setNumColumns(4);
						} else {
							// Portrait
							mGridView.setNumColumns(2);
						}
					} else {
						mGridView.setNumColumns(2);
					}
					mainImageAdapter = new ProductMainImageAdapter(
							getActivity(), R.layout.cataloggriditem,
							productList);
				} else if (mChangingView.matches("ListView")) {
					SOCatalogActivity.mChangeView.setImageResource(R.mipmap.grid_1);
					mChangingView = "GridView";
					if (screenInches > 7) {
						if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
							// Landscape
							mGridView.setNumColumns(2);
						} else {
							// Portrait
							mGridView.setNumColumns(1);
						}
					} else {
						mGridView.setNumColumns(1);
					}
					mainImageAdapter = new ProductMainImageAdapter(
							getActivity(), R.layout.cataloglistitem,
							productList);
				}
				mGridView.setAdapter(mainImageAdapter);
				mGridView.setSelection(totalItem);
			}
		});

	/*	mGridView.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public boolean onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your
				// AdapterView
				if (isScrollListener) {
					pageNo = page;
					load_more.setVisibility(View.VISIBLE);
					params.put("PageNo", String.valueOf(pageNo));
					params.put("ProductCode", "");
					params.put("CategoryCode", mCategory);
					params.put("SubCategoryCode", mSubcategory);
					params.put("CustomerGroupCode", mCustomerGroupCode);
					params.put("CustomerCode", mCustomerCode);
					params.put("TranType", catalogSaveType);
					params.put("ProductName", mProductName);
					loadMoreProducts(params);
				}
				// or customLoadMoreDataFromApi(totalItemsCount);
				return true; // ONLY if more data is actually being loaded;
								// false otherwise.
			}
		});*/

		SOCatalogActivity.mSearchEd.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					Log.i("TAG","Enter pressed");
					searchProduct(SOCatalogActivity.mSearchEd.getText().toString());
				}
				return false;
			}
		});
		SOCatalogActivity.mCartIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(2);
			}
		});
		SOCatalogActivity.mSearchIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isScrollListener = false;
				Catalog.setSearchVisible(true);
				SOCatalogActivity.mCustomKeyboard.setVisibility(View.GONE);
				SOCatalogActivity.mBack.setVisibility(View.VISIBLE);
				SOCatalogActivity.mTitle.setVisibility(View.GONE);
				SOCatalogActivity.mCartIcon.setVisibility(View.GONE);
				SOCatalogActivity.mCartText.setVisibility(View.GONE);
				SOCatalogActivity.mChangeView.setVisibility(View.GONE);
				SOCatalogActivity.mSearchIcon.setVisibility(View.GONE);
				SOCatalogActivity.mFilterIcon.setVisibility(View.GONE);
				SOCatalogActivity.mSearchEd.setVisibility(View.VISIBLE);
				mGridLayout.setVisibility(View.GONE);
				mSearchLayout.setVisibility(View.VISIBLE);
				SOCatalogActivity.mClose.setVisibility(View.VISIBLE);
				SOCatalogActivity.mSearchEd.setText("");
				SOCatalogActivity.mSearchEd.requestFocus();
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
				//mSearchAdapter.filter(SOCatalogActivity.mSearchEd.getText().toString());

			}
		});
		SOCatalogActivity.mClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String searchTxt = SOCatalogActivity.mSearchEd.getText().toString();
				if (searchTxt.length() == 0) {
					hideKeyboard();
					helper.showProgressView(mCatalogLayout);
					mProductName = "";
					pageNo = 0;
					currentPage = 0;
					params.put("ProductCode", "");
					params.put("CategoryCode", "");
					params.put("SubCategoryCode", "");
					params.put("CustomerGroupCode", mCustomerGroupCode);
					params.put("PageNo", String.valueOf(pageNo));
					params.put("CustomerCode", mCustomerCode);
					params.put("TranType", catalogSaveType);
					params.put("ProductName", mProductName);
					try {
						if (CarouselFragment.onlineMode.matches("True")) {
							if (CarouselFragment.checkOffline == true) {
								if (mDialogStatus.matches("true")) { // Temp
																		// Offline
									loadProductMainImageOffline(params);
								} else {
									Log.d("CheckOffline Alert -->", "False");
									if(mobileHaveOfflineMode.matches("1")) {
										getActivity().finish();
									}
								}
							} else { // Online
								loadProductMainImage(params);
							}
						} else if (CarouselFragment.onlineMode.matches("False")) { // Permnt
																					// Offline
							loadProductMainImageOffline(params);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					showView();
					animCartIcon();
				} else {
					SOCatalogActivity.mSearchEd.setText("");
					//mSearchAdapter.filter(SOCatalogActivity.mSearchEd.getText().toString());
				}

			}
		});
		SOCatalogActivity.mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mProductName = "";
				SOCatalogActivity.mSearchEd.setText("");
				hideKeyboard();
				helper.showProgressView(mCatalogLayout);
				pageNo = 0;
				currentPage = 0;
				params.put("ProductCode", "");
				params.put("CategoryCode", "");
				params.put("SubCategoryCode", "");
				params.put("CustomerGroupCode", mCustomerGroupCode);
				params.put("PageNo", String.valueOf(pageNo));
				params.put("CustomerCode", mCustomerCode);
				params.put("TranType", catalogSaveType);
				params.put("ProductName", mProductName);

				try {
					if (CarouselFragment.onlineMode.matches("True")) {
						if (CarouselFragment.checkOffline == true) {
							if (mDialogStatus.matches("true")) { // Temp Offline
								loadProductMainImageOffline(params);
							} else {
								Log.d("CheckOffline Alert -->", "False");
								if(mobileHaveOfflineMode.matches("1")) {
									getActivity().finish();
								}
							}
						} else { // Online
							loadProductMainImage(params);
						}
					} else if (CarouselFragment.onlineMode.matches("False")) { // Permnt
																				// Offline
						loadProductMainImageOffline(params);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				showView();
				animCartIcon();
			}
		});
		SOCatalogActivity.mKeyBtnOne.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				customKeyboardBtn("1");
			}
		});
		SOCatalogActivity.mKeyBtnTwo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				customKeyboardBtn("2");
			}
		});

		SOCatalogActivity.mKeyBtnThree
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						customKeyboardBtn("3");
					}
				});
		SOCatalogActivity.mKeyBtnFour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				customKeyboardBtn("4");
			}
		});

		SOCatalogActivity.mKeyBtnFive.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				customKeyboardBtn("5");
			}
		});
		SOCatalogActivity.mKeyBtnSix.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				customKeyboardBtn("6");
			}
		});
		SOCatalogActivity.mKeyBtnSeven
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						customKeyboardBtn("7");
					}
				});
		SOCatalogActivity.mKeyBtnEight
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						customKeyboardBtn("8");
					}
				});
		SOCatalogActivity.mKeyBtnNine.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				customKeyboardBtn("9");
			}
		});
		SOCatalogActivity.mKeyBtnZero.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				customKeyboardBtn("0");
			}
		});
		SOCatalogActivity.mKeyBtnDot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				customKeyboardBtn(".");
			}
		});
		SOCatalogActivity.mKeyBtnClear
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						customKeyboardClearBtn();
					}
				});

		params.put("ProductCode", "");
		params.put("CategoryCode", mCategory);
		params.put("SubCategoryCode", mSubcategory);
		params.put("CustomerGroupCode", mCustomerGroupCode);
		params.put("PageNo", String.valueOf(pageNo));

		params.put("CustomerCode", mCustomerCode);
		params.put("TranType", catalogSaveType);
		params.put("ProductName", mProductName);

		mDialogStatus = mCarouselFragment.checkInternetStatus(getActivity());
		Log.d("checkInternetStatus", "" + mDialogStatus);
		//helper.showProgressView(mCatalogLayout);
		//loadProductMainImage(params);
		try {
			if (CarouselFragment.onlineMode.matches("True")) {
				if (CarouselFragment.checkOffline == true) {
					if (mDialogStatus.matches("true")) { // Temp Offline
						if (Catalog.isUpdatedPrice()) {
							helper.showProgressView(mCatalogLayout);
							loadProductMainImageOffline(params);
							Catalog.setUpdatedPrice(false);
							showView();
						} else {

							if (CarouselFragment.updateonlinemode
									.matches("true")) {
								helper.showProgressView(mCatalogLayout);
								loadProductMainImageOffline(params);
							} else {
								mainImageAdapter.notifyDataSetChanged();
								animCartIcon();
							}

						}
					} else {
						Log.d("CheckOffline Alert -->", "False");
						if(mobileHaveOfflineMode.matches("1")) {
							getActivity().finish();
						}
					}
				} else { // Online
					if (Catalog.isUpdatedPrice()) {
						showView();
						pageNo = 0;
						currentPage = -1;
						Log.d("isUpdatedPrice", "isUpdatedPrice");
						helper.showProgressView(mCatalogLayout);
						loadProductMainImage(params);
						Catalog.setUpdatedPrice(false);
						mainImageAdapter.notifyDataSetChanged();
					} else {
						if (CarouselFragment.updateonlinemode.matches("true")) {
							helper.showProgressView(mCatalogLayout);
							Log.d("isUpdatedPrice", "true");
							loadProductMainImage(params);
						} else {
							Log.d("-------------", "---test---");
							mainImageAdapter.notifyDataSetChanged();
							animCartIcon();
						}
					}
				}
			} else if (CarouselFragment.onlineMode.matches("False")) { // Permnt
																		// Offline
				if (Catalog.isUpdatedPrice()) {
					helper.showProgressView(mCatalogLayout);
					loadProductMainImageOffline(params);
					Catalog.setUpdatedPrice(false);
					showView();
				} else {

					if (CarouselFragment.updateonlinemode.matches("true")) {
						helper.showProgressView(mCatalogLayout);
						loadProductMainImageOffline(params);
					} else {
						mainImageAdapter.notifyDataSetChanged();
						animCartIcon();
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d("mGridView.getCount()", "-->"+mGridView.getCount());
		mGridView.setOnScrollListener(new InfiniteScrollListener(mGridView.getCount()) {
			@Override
			public void loadMore(int page, int totalItemsCount) {
				Log.d("totalItemsCount",""+totalItemsCount);
				String searchCheck = SOCatalogActivity.mSearchEd.getText().toString();
				Log.d("searchCheck",searchCheck);
				if(searchCheck.matches("")) {
					if (isScrollListener) {
						Log.d(".....More.....", "...........");
						load_more.setVisibility(View.VISIBLE);
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("PageNo", String.valueOf(page));
						params.put("ProductCode", "");
						params.put("CategoryCode", mCategory);
						params.put("SubCategoryCode", mSubcategory);
						params.put("CustomerGroupCode", mCustomerGroupCode);
						params.put("CustomerCode", mCustomerCode);
						params.put("TranType", catalogSaveType);
						params.put("ProductName", mProductName);
						loadMoreProducts(params);
					}
				}else{
					params.put("ProductCode", "");
					params.put("CategoryCode", "");
					params.put("SubCategoryCode", "");
					params.put("CustomerGroupCode", mCustomerGroupCode);
					params.put("PageNo", String.valueOf(page));
					params.put("CustomerCode", mCustomerCode);
					params.put("TranType", catalogSaveType);
					params.put("ProductName", searchCheck);
					loadMoreProducts(params);
				}
			}

		});

		/*mGridView.setOnScrollListener(new AbsListView.OnScrollListener(){
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				if(firstVisibleItem + visibleItemCount >= totalItemCount){
					// End has been reached
					if (isScrollListener) {
						Log.d(".....More.....","...........");
						load_more.setVisibility(View.VISIBLE);
						HashMap<String,String> params = new HashMap<String, String>();
						params.put("PageNo", String.valueOf(page));
						params.put("ProductCode", "");
						params.put("CategoryCode", mCategory);
						params.put("SubCategoryCode", mSubcategory);
						params.put("CustomerGroupCode", mCustomerGroupCode);
						params.put("CustomerCode", mCustomerCode);
						params.put("TranType", catalogSaveType);
						params.put("ProductName", mProductName);
						loadMoreProducts(params);
					}
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState){

			}
		});*/

		/*mGridView.setOnScrollListener(new InfiniteScrollListener(mGridView.getCount()) {
			@Override
			public void loadMore(int page, int totalItemsCount) {
				if (isScrollListener) {
					Log.d(".....More.....","...........");
					load_more.setVisibility(View.VISIBLE);
					params.put("PageNo", String.valueOf(page));
					params.put("ProductCode", "");
					params.put("CategoryCode", mCategory);
					params.put("SubCategoryCode", mSubcategory);
					params.put("CustomerGroupCode", mCustomerGroupCode);
					params.put("CustomerCode", mCustomerCode);
					params.put("TranType", catalogSaveType);
					params.put("ProductName", mProductName);
					loadMoreProducts(params);
				}
			}

		});*/
		/*
		 * if(Catalog.isUpdatedPrice()){ params.put("ProductCode", "");
		 * params.put("CategoryCode", ""); params.put("SubCategoryCode", "");
		 * params.put("CustomerGroupCode", mCustomerGroupCode);
		 * params.put("PageNo", String.valueOf(pageNo));
		 * loadProductMainImage(params); Catalog.setUpdatedPrice(false); }else{
		 * mainImageAdapter.notifyDataSetChanged(); animCartIcon(); }
		 */

		super.onResume();
	}

	private void searchProduct(String productName){
        try {
		pageNo = 0;
		currentPage = 0;
		params.put("ProductCode", "");
		params.put("CategoryCode", "");
		params.put("SubCategoryCode", "");
		params.put("CustomerGroupCode", mCustomerGroupCode);
		params.put("PageNo", String.valueOf(pageNo));
		params.put("CustomerCode", mCustomerCode);
		params.put("TranType", catalogSaveType);
		params.put("ProductName", productName);
		hideKeyboard();
			if (CarouselFragment.onlineMode.matches("True")) {
				if (CarouselFragment.checkOffline == true) {
					if (mDialogStatus.matches("true")) { // Temp
						// Offline
                        helper.showProgressView(mCatalogLayout);
						loadProductMainImageOffline(params);
					} else {
						Log.d("CheckOffline Alert -->", "False");
						if(mobileHaveOfflineMode.matches("1")) {
							getActivity().finish();
						}
					}
				} else { // Online
                    helper.showProgressView(mCatalogLayout);
					Log.d("product load", "<<<<loadProductMainImage");
					loadProductMainImage(params);
				}
			} else if (CarouselFragment.onlineMode.matches("False")) { // Permnt
				// Offline
                helper.showProgressView(mCatalogLayout);
				loadProductMainImageOffline(params);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//showView();
		//animCartIcon();

	}

	public void showView() {
		SOCatalogActivity.mBack.setVisibility(View.GONE);
		SOCatalogActivity.mCartIcon.setVisibility(View.VISIBLE);
		SOCatalogActivity.mChangeView.setVisibility(View.VISIBLE);
		SOCatalogActivity.mSearchIcon.setVisibility(View.VISIBLE);
		SOCatalogActivity.mFilterIcon.setVisibility(View.VISIBLE);
		SOCatalogActivity.mSearchEd.setVisibility(View.GONE);
		mGridLayout.setVisibility(View.VISIBLE);
		mSearchLayout.setVisibility(View.GONE);
		SOCatalogActivity.mClose.setVisibility(View.GONE);
		SOCatalogActivity.mTitle.setVisibility(View.VISIBLE);
		isScrollListener = true;
		Catalog.setSearchVisible(false);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		screenInches = helper.displayMetrics();
		orientation = getResources().getConfiguration().orientation;
		// Checks the orientation of the screen
		if (screenInches > 7) {
			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// Landscape
				if (mChangingView.matches("GridView")) {
					mGridView.setNumColumns(2);
				} else if (mChangingView.matches("ListView")) {
					mGridView.setNumColumns(4);
				}

			} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
				// Portrait
				if (mChangingView.matches("GridView")) {
					mGridView.setNumColumns(1);
				} else if (mChangingView.matches("ListView")) {
					mGridView.setNumColumns(2);
				}
			}

			mCarouselFragment.checkOrientation(orientation, screenInches);
		}
	}

	public void animCartIcon() {
		Cursor cursor = DBCatalog.getCursor();
		int cartSize = cursor.getCount();
		if (cartSize > 0) {
			if (Catalog.isSearchVisible()) {
				SOCatalogActivity.mCartIcon.setVisibility(View.GONE);
				SOCatalogActivity.mCartText.setVisibility(View.GONE);
			} else {
				SOCatalogActivity.mCartIcon.setVisibility(View.VISIBLE);
				SOCatalogActivity.mCartText.setVisibility(View.VISIBLE);
				ObjectAnimator anim = ObjectAnimator.ofFloat(SOCatalogActivity.mCartText,
						"rotationY", 0.0f, 360.0f);
				anim.setDuration(1000);
				anim.start();
				SOCatalogActivity.mCartIcon.setVisibility(View.VISIBLE);
				SOCatalogActivity.mCartText.setText(cartSize + "");
			}
		} else {
			SOCatalogActivity.mCartText.setVisibility(View.GONE);
			SOCatalogActivity.mCartText.setText("");
		}
	}

	// Custom keyboard setting fields
	private void customKeyboardBtn(String numbers) {
		if (mEditValue != null) {
			mEditValue.requestFocus();
			mEditValue.append(numbers);
			if (mkeyActionDoneStr.matches("Qty")) {
				mainImageAdapter.qtyActionDone(keyViewActionDone);
			} else if (mkeyActionDoneStr.matches("CQty")) {
				mainImageAdapter.cartonActionDone(keyViewActionDone);
			}
		}
	}

	// Custom keyboard Clear fields
	private void customKeyboardClearBtn() {
		if (mEditValue != null) {
			int slength = mEditValue.length();
			if (slength > 0) {
				String qty = mEditValue.getText().toString();
				mEditValue.setText(qty.substring(0, qty.length() - 1));
				mEditValue.setSelection(mEditValue.getText().length());
				if (mkeyActionDoneStr.matches("Qty")) {
					mainImageAdapter.qtyActionDone(keyViewActionDone);
				} else if (mkeyActionDoneStr.matches("CQty")) {

					mainImageAdapter.cartonActionDone(keyViewActionDone);
				}
			}
		}
	}


	/*
	 * @Override public void onConfigurationChanged(Configuration newConfig) {
	 * super.onConfigurationChanged(newConfig); screenInches = displayMetrics();
	 * // Checks the orientation of the screen if (newConfig.orientation ==
	 * Configuration.ORIENTATION_LANDSCAPE) { if (screenInches > 7) { //
	 * Landscape mGridView.setNumColumns(4); } } else if (newConfig.orientation
	 * == Configuration.ORIENTATION_PORTRAIT){ if (screenInches > 7) { //
	 * Portrait mGridView.setNumColumns(2); } } }
	 */
	/*
	 * public void animCartIcon() { Cursor cursor = DBCatalog.getCursor(); int
	 * cartSize = cursor.getCount(); if (cartSize > 0) {
	 * mCartTxt.setVisibility(View.VISIBLE); ObjectAnimator anim =
	 * ObjectAnimator.ofFloat(mCartTxt, "rotationY", 0.0f, 360.0f);
	 * anim.setDuration(1000); anim.start(); mCartTxt.setText(cartSize + ""); }
	 * else { mCartTxt.setVisibility(View.GONE); mCartTxt.setText(""); } }
	 */
	public Double displayMetrics() {
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int dens = dm.densityDpi;
		double wi = (double) width / (double) dens;
		double hi = (double) height / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		return screenInches = Math.sqrt(x + y);
	}

	private void loadProductMainImage(HashMap<String, String> params) {
		productList.clear();
		Log.d("product load", "loadProductMainImage");
		new XMLAccessTask(getActivity(), valid_url, "fncGetProductMainImage",
				params, false, new CallbackInterface() {

					@Override
					public void onSuccess(NodeList nl) {
						isScrollListener = true;
						//Log.d("catalog load more", "more more");

						for (int i = 0; i < nl.getLength(); i++) {

						//	Log.d("product load", "loading");

							// creating new HashMap
							Element e = (Element) nl.item(i);
							Product product = new Product();
							// adding each child node to Product Pojo class =>
							// value
						/*	Log.d("product p",
									"pp"
											+ XMLParser.getValue(e,
													KEY_PRODUCTCODE));*/
							product.setProductCode(XMLParser.getValue(e,
									KEY_PRODUCTCODE));
							product.setProductName(XMLParser.getValue(e,
									KEY_PRODUCTNAME));
							product.setCategoryCode(XMLParser.getValue(e,
									KEY_CATEGORYCODE));
							product.setSubCategoryCode(XMLParser.getValue(e,
									KEY_SUBCATEGORYCODE));
							product.setUom(XMLParser.getValue(e, KEY_UOMCODE));
							String pcspercarton = XMLParser.getValue(e,
									KEY_PCSPERCARTON);
							product.setPcspercarton(pcspercarton);
							// product.setPcspercarton(parser.getValue(e,
							// KEY_PCSPERCARTON));
							product.setWholesalePrice(XMLParser.getValue(e,
									KEY_WHOLESALEPRICE));
//							product.setProductImage(XMLParser.getValue(e,
//									KEY_PRODUCTIMAGE));
							product.setSpecification(XMLParser.getValue(e,
									KEY_SPECIFICATION));
							String base = XMLParser.getValue(e,KEY_PRODUCTIMAGE);

							if(base!=null && !base.isEmpty()){
								product.setProductImage(base);
							}else{
								product.setProductImage(null);
							}



//							if(base!=null && !base.isEmpty()){
//								byte[] imageAsBytes = Base64.decode(base.getBytes(),Base64.DEFAULT);
//								Bitmap bmp = decodeSampledBitmapFromResource(imageAsBytes, 80,80);
//								product.setBitmap(bmp);
//
//							}else{
//								product.setBitmap(null);
//							}
							product.setHaveAttribute(XMLParser.getValue(e,KEY_HAVEATTRIBUTE));

							String retailPrice = null;
							retailPrice = XMLParser
									.getValue(e, KEY_RETAILPRICE);
							if (retailPrice != null && !retailPrice.isEmpty()) {
								product.setRetailPrice(XMLParser.getValue(e,
										KEY_RETAILPRICE));
							} else {
								product.setRetailPrice("0");
							}

							if (pcspercarton != null && !pcspercarton.isEmpty()) {
								if (Double.valueOf(pcspercarton) > 1) {

									double pcs = Double.valueOf(pcspercarton);
									double carton = pcs * 1;
									product.setQty(String.valueOf(carton));
								} else {
									product.setQty("1");

								}
							}
							product.setCqty("1");
							product.setLqty("0");

							// adding all childnode to ArrayList
							productList.add(product);


						}
						Log.d("product size", "-->>>>" + productList.size());
						helper.dismissProgressView(mCatalogLayout);
						Log.d("product size", "<<<--" + productList.size());
						if (productList.size() > 0) {

							mainImageAdapter = new ProductMainImageAdapter(
									getActivity(), R.layout.cataloggriditem,
									productList);
							mGridView.setAdapter(mainImageAdapter);
//							isScrollListener = true;
							mainImageAdapter.notifyDataSetChanged();
							screenInches = displayMetrics();
							if (screenInches > 7) {
								// Check orientation
								orientation = getResources().getConfiguration().orientation;
								if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
									// Landscape
									mGridView.setNumColumns(4);
								} else {
									// Portrait
									mGridView.setNumColumns(2);
								}
							} else {
								mGridView.setNumColumns(2);
							}
							mGridLayout.setVisibility(View.VISIBLE);
							mSearchLayout.setVisibility(View.GONE);

						} else {
							// helper.showLongToast(R.string.no_data_found);
							Log.d("No product found", "No product found");

						}

					}

					@Override
					public void onFailure(ErrorType error) {
						onError(error);
						helper.dismissProgressView(mCatalogLayout);
					}

				}).execute();
	}

	private void loadProductMainImageOffline(HashMap<String, String> params) {
		productList.clear();
		Log.d("productList.size() main", "" + productList.size());

		try {
			String productStr = offlineDatabase.getProductMainImage(params);
			jsonString = " { SODetails : " + productStr + "}";
			Log.d("product Details", ":" + jsonString);
			jsonResponse = new JSONObject(jsonString);
			jsonMainNode = jsonResponse.optJSONArray("SODetails");

			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode;

				Product product = new Product();
				String productcode = "", productname = "", category = "", subcategory = "", uom = "", pcspercarton = "", wholesaleprice = "", productimage = "", specification = "", retailPrice = "";
				jsonChildNode = jsonMainNode.getJSONObject(i);
				productcode = jsonChildNode.optString(KEY_PRODUCTCODE)
						.toString();
				productname = jsonChildNode.optString(KEY_PRODUCTNAME)
						.toString();
				category = jsonChildNode.optString(KEY_CATEGORYCODE).toString();
				subcategory = jsonChildNode.optString(KEY_SUBCATEGORYCODE)
						.toString();
				uom = jsonChildNode.optString(KEY_UOMCODE).toString();
				pcspercarton = jsonChildNode.optString(KEY_PCSPERCARTON)
						.toString();
				wholesaleprice = jsonChildNode.optString(KEY_WHOLESALEPRICE)
						.toString();
				productimage = jsonChildNode.optString(KEY_PRODUCTIMAGE)
						.toString();
				specification = jsonChildNode.optString(KEY_SPECIFICATION)
						.toString();
				retailPrice = jsonChildNode.optString(KEY_RETAILPRICE)
						.toString();
				product.setHaveAttribute(jsonChildNode.optString(KEY_HAVEATTRIBUTE));
				// adding each child node to Product Pojo class => value
				product.setProductCode(productcode);
				product.setProductName(productname);
				product.setCategoryCode(category);
				product.setSubCategoryCode(subcategory);
				product.setUom(uom);
				// String pcspercarton = pcs(XMLParser.getValue(e,
				// KEY_PCSPERCARTON));
				product.setPcspercarton(pcspercarton);
				product.setWholesalePrice(wholesaleprice);
				product.setProductImage(productimage);
				product.setSpecification(specification);
				if (retailPrice != null && !retailPrice.isEmpty()) {
					product.setRetailPrice(retailPrice);
				} else {
					product.setRetailPrice("0");
				}

				if (pcspercarton != null && !pcspercarton.isEmpty()) {
					if (Double.valueOf(pcspercarton) > 1) {
						// product.setCqty("1");
						double pcs = Double.valueOf(pcspercarton);
						double carton = pcs * 1;
						product.setQty(String.valueOf(carton));
					} else {
						product.setQty("1");
						// product.setCqty("0");
					}
				}
				product.setCqty("1");
				product.setLqty("0");
				Log.d("getLqty", "lqty : " + product.getLqty());
				// adding all childnode to ArrayList
				productList.add(product);
			}

			if (productList.size() > 0) {
				mainImageAdapter = new ProductMainImageAdapter(getActivity(),
						R.layout.cataloggriditem, productList);
				mGridView.setAdapter(mainImageAdapter);
				mainImageAdapter.notifyDataSetChanged();
				mGridView.setNumColumns(2);

			} else {
				// helper.showLongToast(R.string.no_data_found);
			}
			if(helper!=null){
			helper.dismissProgressView(mCatalogLayout);
			}
		} catch (Exception e) {
			if(helper!=null){
			helper.dismissProgressView(mCatalogLayout);
			}
			e.printStackTrace();
		}

	}

	private void loadSearchProductItemOffline(ArrayList<String> searchlist) {
		productList.clear();
		for (int s = 0; s < searchlist.size(); s++) {

			if (searchlist.size() > 0) {
				Log.d("ProductCode", "" + searchlist.get(s));

				try {
					params.clear();
					params.put("ProductCode", searchlist.get(s));
					params.put("CategoryCode", "");
					params.put("SubCategoryCode", "");
					params.put("CustomerGroupCode", mCustomerGroupCode);
					params.put("PageNo", "");
					params.put("CustomerCode", mCustomerCode);
					params.put("TranType", catalogSaveType);
					params.put("ProductName", mProductName);

					String productStr = offlineDatabase
							.getProductMainImage(params);
					jsonString = " { SODetails : " + productStr + "}";
					Log.d("product Details", ":" + jsonString);
					jsonResponse = new JSONObject(jsonString);
					jsonMainNode = jsonResponse.optJSONArray("SODetails");

					int lengthJsonArr = jsonMainNode.length();
					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode;

						Product product = new Product();
						String productcode = "", productname = "", category = "", subcategory = "", uom = "", pcspercarton = "", wholesaleprice = "", productimage = "", specification = "", retailPrice = "";
						jsonChildNode = jsonMainNode.getJSONObject(i);
						productcode = jsonChildNode.optString(KEY_PRODUCTCODE)
								.toString();
						productname = jsonChildNode.optString(KEY_PRODUCTNAME)
								.toString();
						category = jsonChildNode.optString(KEY_CATEGORYCODE)
								.toString();
						subcategory = jsonChildNode.optString(
								KEY_SUBCATEGORYCODE).toString();
						uom = jsonChildNode.optString(KEY_UOMCODE).toString();
						pcspercarton = jsonChildNode.optString(
								KEY_PCSPERCARTON).toString();
						wholesaleprice = jsonChildNode.optString(
								KEY_WHOLESALEPRICE).toString();
						productimage = jsonChildNode
								.optString(KEY_PRODUCTIMAGE).toString();
						specification = jsonChildNode.optString(
								KEY_SPECIFICATION).toString();
						retailPrice = jsonChildNode.optString(KEY_RETAILPRICE)
								.toString();

						// adding each child node to Product Pojo class => value
						product.setProductCode(productcode);
						product.setProductName(productname);
						product.setCategoryCode(category);
						product.setSubCategoryCode(subcategory);
						product.setUom(uom);
						product.setPcspercarton(pcspercarton);
						product.setWholesalePrice(wholesaleprice);
						product.setProductImage(productimage);
						product.setSpecification(specification);
						if (retailPrice != null && !retailPrice.isEmpty()) {
							product.setRetailPrice(retailPrice);
						} else {
							product.setRetailPrice("0");
						}

						if (pcspercarton != null && !pcspercarton.isEmpty()) {
							if (Double.valueOf(pcspercarton) > 1) {
								// product.setCqty("1");
								double pcs = Double.valueOf(pcspercarton);
								double carton = pcs * 1;
								product.setQty(String.valueOf(carton));
							} else {
								product.setQty("1");
								// product.setCqty("0");
							}
						}
						product.setCqty("1");
						product.setLqty("0");
						Log.d("getLqty", "lqty : " + product.getLqty());
						// adding all childnode to ArrayList
						productList.add(product);
						mainImageAdapter.notifyDataSetChanged();
					}

					screenInches = helper.displayMetrics();
					if (screenInches > 7) {
						// Check orientation
						orientation = getResources().getConfiguration().orientation;
						if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
							// Landscape
							mGridView.setNumColumns(4);
						} else {
							// Portrait
							mGridView.setNumColumns(2);
						}
						mCarouselFragment.checkOrientation(orientation,
								screenInches);
					} else {
						mGridView.setNumColumns(2);
					}
					helper.dismissProgressView(mCatalogLayout);
				} catch (Exception e) {
					helper.dismissProgressView(mCatalogLayout);
					e.printStackTrace();
				}
			}
		}

		helper.dismissProgressView(mCatalogLayout);
		SOCatalogActivity.mBack.setVisibility(View.VISIBLE);
		SOCatalogActivity.mCartIcon.setVisibility(View.GONE);
		SOCatalogActivity.mChangeView.setVisibility(View.GONE);
		SOCatalogActivity.mSearchIcon.setVisibility(View.GONE);
		SOCatalogActivity.mFilterIcon.setVisibility(View.GONE);
		SOCatalogActivity.mSearchEd.setVisibility(View.VISIBLE);
		mGridLayout.setVisibility(View.VISIBLE);
		mSearchLayout.setVisibility(View.GONE);

	}

	private void loadMoreProducts(HashMap<String, String> params) {
		loadmoreProduct = new ArrayList<Product>();
		loadmoreProduct.clear();
		new XMLAccessTask(getActivity(), valid_url, "fncGetProductMainImage",
				params, false, new CallbackInterface() {

					@Override
					public void onSuccess(NodeList nl) {
						try {
							for (int i = 0; i < nl.getLength(); i++) {
								// creating new HashMap
								Element e = (Element) nl.item(i);
								Product product = new Product();
								// adding each child node to Product Pojo class
								// => value
								product.setProductCode(XMLParser.getValue(e,
										KEY_PRODUCTCODE));
								product.setProductName(XMLParser.getValue(e,
										KEY_PRODUCTNAME));
								product.setCategoryCode(XMLParser.getValue(e,
										KEY_CATEGORYCODE));
								product.setSubCategoryCode(XMLParser.getValue(
										e, KEY_SUBCATEGORYCODE));
								product.setUom(XMLParser.getValue(e,
										KEY_UOMCODE));
								String pcspercarton = XMLParser.getValue(e,
										KEY_PCSPERCARTON);
								product.setPcspercarton(pcspercarton);
								// product.setPcspercarton(parser.getValue(e,
								// KEY_PCSPERCARTON));
								product.setWholesalePrice(XMLParser.getValue(e,
										KEY_WHOLESALEPRICE));
//								product.setProductImage(XMLParser.getValue(e,
//										KEY_PRODUCTIMAGE));
								product.setSpecification(XMLParser.getValue(e,
										KEY_SPECIFICATION));
								product.setRetailPrice(XMLParser.getValue(e,
										KEY_RETAILPRICE));
								product.setHaveAttribute(XMLParser.getValue(e,
										KEY_HAVEATTRIBUTE));
								Log.d("retail",
										XMLParser.getValue(e, KEY_RETAILPRICE));

								String base = XMLParser.getValue(e,KEY_PRODUCTIMAGE);

								if(base!=null && !base.isEmpty()){
									product.setProductImage(base);
								}else{
									product.setProductImage(null);
								}

//								if(base!=null && !base.isEmpty()){
//									byte[] imageAsBytes = Base64.decode(base.getBytes(),Base64.DEFAULT);
//									Bitmap bmp = decodeSampledBitmapFromResource(imageAsBytes, 80,80);
//									product.setBitmap(bmp);
//
//								}else{
//									product.setBitmap(null);
//								}

								if (pcspercarton != null
										&& !pcspercarton.isEmpty()) {
									if (Double.valueOf(pcspercarton) > 1) {
										// product.setCqty("1");
										double pcs = Double.valueOf(pcspercarton);
										double carton = pcs * 1;
										product.setQty(String.valueOf(carton));
									} else {
										product.setQty("1");
										// product.setCqty("0");
									}
								}
								product.setCqty("1");
								product.setLqty("0");

								// adding all childnode to ArrayList
								loadmoreProduct.add(product);
							}
							load_more.setVisibility(View.GONE);
							productList.addAll(loadmoreProduct);
							mainImageAdapter.notifyDataSetChanged();


						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(ErrorType error) {
						onError(error);
					}

				}).execute();

	}

	private void onError(final ErrorType error) {
		new Thread() {
			@Override
			public void run() {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {

							if (error == ErrorType.NETWORK_UNAVAILABLE) {
								helper.showLongToast(R.string.error_showing_image_no_network_connection);
							} else {
								// helper.showLongToast(R.string.error_showing_image);

								load_more.setVisibility(View.GONE);
								mainImageAdapter.notifyDataSetChanged();
							}
						}
					});
				}
			}
		}.start();
	}



	public class ProductMainImageAdapter extends BaseAdapter implements
			Constants {

		private Activity mContext;
		private Product product;
		private LayoutInflater inflater;
		private List<Product> mProductList;
		private int resourceId;
		private String pcspercarton, priceflag = "";
		private ViewHolder holder;
		private DBCatalog dbhelper;
		private HashMap<String, String> productvalues;
		private double qtyFlag = 0, cartonFlag = 0;

		public ProductMainImageAdapter() {
		}

		public ProductMainImageAdapter(Activity context, int resourceId,
				List<Product> productList) {
			mContext = context;
			dbhelper = new DBCatalog(context);
			this.mProductList = new ArrayList<Product>();
			this.productvalues = new HashMap<String, String>();
			mProductList.clear();
			this.mProductList = productList;

			this.resourceId = resourceId;
			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			priceflag = SalesOrderSetGet.getCartonpriceflag();

		}

		@Override
		public int getCount() {
			return mProductList.size();
		}

		@Override
		public Product getItem(int position) {
			return mProductList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				holder = new ViewHolder();
				row = inflater.inflate(resourceId, parent, false);


				holder.productCode = (TextView) row
						.findViewById(R.id.productCode);
				holder.productName = (TextView) row
						.findViewById(R.id.productName);
				holder.wholesalePrice = (TextView) row
						.findViewById(R.id.wholeSalePrice);
				holder.cartonPrice = (TextView) row
						.findViewById(R.id.cartonPrice);
				holder.productImage = (ImageView) row
						.findViewById(R.id.productImage);
				holder.qty = (EditText) row.findViewById(R.id.qty);
				holder.qtyMinus = (ImageView) row.findViewById(R.id.qty_minus);
				holder.qtyPlus = (ImageView) row.findViewById(R.id.qty_plus);

				holder.carton = (EditText) row.findViewById(R.id.carton);
				holder.cartonMinus = (ImageView) row
						.findViewById(R.id.carton_minus);
				holder.cartonPlus = (ImageView) row
						.findViewById(R.id.carton_plus);

				holder.add = (Button) row.findViewById(R.id.add);
				holder.carton_lbl = (TextView) row
						.findViewById(R.id.carton_lbl);
				holder.qty_lbl = (TextView) row.findViewById(R.id.qty_lbl);
				holder.loose = (TextView) row.findViewById(R.id.loose);
				holder.pcspercarton = (TextView) row
						.findViewById(R.id.pcspercarton);
				holder.ordered_qty = (TextView) row.findViewById(R.id.ordered_qty);
				holder.mOrderLayout = (LinearLayout) row.findViewById(R.id.orderLayout);
				holder.mAddToCartLayout = (LinearLayout) row.findViewById(R.id.addToCartLayout);
				holder.price = (TextView) row.findViewById(R.id.price);
				holder.order = (Button) row.findViewById(R.id.order);
				row.setTag(holder);
			} else {
				holder = (ViewHolder) row.getTag();
			}

			product = getItem(position);
			row.setId(position);

			holder.qty.setId(position);
			holder.qtyMinus.setId(position);
			holder.qtyPlus.setId(position);

			holder.carton.setId(position);
			holder.cartonMinus.setId(position);
			holder.cartonPlus.setId(position);

			holder.add.setId(position);
			holder.productImage.setId(position);

			holder.order.setId(position);


			holder.productCode.setText(product.getProductCode());
			holder.productName.setText(product.getProductName());
			String price = product.getWholesalePrice();
			double dPrice=0.00,dCPrice = 0.00;
			if(price!=null && !price.isEmpty()){
				dPrice = Double.valueOf(price);
			}else{
				dPrice = 0.00;
			}
			holder.wholesalePrice.setText(twoDecimalPoint(dPrice));
			String retailprice = product.getRetailPrice();
			if(retailprice!=null && !retailprice.isEmpty()){
				dCPrice  = Double.valueOf(retailprice);
			}else{
				dCPrice = 0.00;
			}

			if (priceflag.matches("1")) {
				holder.price.setText("$ "+retailprice);
			}else{
				holder.price.setText("$ 0.00");
			}
			holder.cartonPrice.setText(twoDecimalPoint(dCPrice));
			holder.carton.setText(product.getCqty());
			holder.carton.setSelection(holder.carton.getText().length());
			holder.loose.setText(product.getLqty());
			holder.qty.setText(product.getQty());
			holder.qty.setSelection(holder.qty.getText().length());

			// taxValue = product.getTaxPerc();

			holder.pcspercarton.setText("PcsPerCarton :"
					+ product.getPcspercarton());


			pcspercarton = product.getPcspercarton();
			if (pcspercarton != null && !pcspercarton.isEmpty()) {
				if (Double.valueOf(pcspercarton) > 1) {
					holder.carton.setVisibility(View.VISIBLE);
					holder.carton_lbl.setVisibility(View.VISIBLE);
					holder.cartonMinus.setVisibility(View.VISIBLE);
					holder.cartonPlus.setVisibility(View.VISIBLE);
					holder.qty_lbl.setVisibility(View.VISIBLE);

				} else {
					holder.carton.setVisibility(View.GONE);
					holder.carton_lbl.setVisibility(View.GONE);
					holder.cartonMinus.setVisibility(View.GONE);
					holder.cartonPlus.setVisibility(View.GONE);
				}
			}
			String showProductCode = SalesOrderSetGet.getSelfOrderShowProductCode();
			if (showProductCode != null && !showProductCode.isEmpty()) {
				if(showProductCode.matches("1")){
					holder.productCode.setVisibility(View.VISIBLE);
				}else{
					holder.productCode.setVisibility(View.GONE);
				}
			}
			else{
				holder.productCode.setVisibility(View.GONE);
			}
			String productCode = DBCatalog.getProductCodeValue(product
					.getProductCode());
			if (productCode != null && !productCode.isEmpty()) {

				holder.add.setBackgroundResource(R.drawable.add_cart_selected);
				holder.add.setText("Added");
				holder.add.setTextColor(Color.parseColor("#FFFFFF"));
			} else {

				holder.add.setBackgroundResource(R.drawable.add_cart);
				holder.add.setText("Add");
				holder.add.setTextColor(Color.parseColor("#000000"));
			}

			String showAddToCart = SalesOrderSetGet.getShowAddToCart();
			if(showAddToCart!=null && !showAddToCart.isEmpty()){
				if(showAddToCart.equalsIgnoreCase("0")){
					holder.wholesalePrice.setVisibility(View.GONE);
					holder.mOrderLayout.setVisibility(View.VISIBLE);
					holder.mAddToCartLayout.setVisibility(View.GONE);
					if(productCode != null && !productCode.isEmpty()){
						holder.ordered_qty.setVisibility(View.VISIBLE);
						holder.ordered_qty.setText(product.getQty());
					}else{
						holder.ordered_qty.setVisibility(View.INVISIBLE);
					}
				}else{
					holder.mOrderLayout.setVisibility(View.GONE);
					holder.wholesalePrice.setVisibility(View.VISIBLE);
					holder.mAddToCartLayout.setVisibility(View.VISIBLE);
				}
			}else{
				if(productCode != null && !productCode.isEmpty()){
					holder.ordered_qty.setVisibility(View.VISIBLE);
					holder.ordered_qty.setText(product.getQty());
				}else{
					holder.ordered_qty.setVisibility(View.INVISIBLE);
				}
				holder.wholesalePrice.setVisibility(View.GONE);
				holder.mOrderLayout.setVisibility(View.VISIBLE);
				holder.mAddToCartLayout.setVisibility(View.GONE);
			}
			if (screenInches > 7) {
				if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
					if (mPosition == position) {
						if (mkeyActionDoneStr.matches("Qty")) {
							holder.qty
									.setBackgroundResource(R.drawable.edittext_focused);
							holder.carton
									.setBackgroundResource(R.drawable.edittext_normal);
						} else if (mkeyActionDoneStr.matches("CQty")) {
							holder.carton
									.setBackgroundResource(R.drawable.edittext_focused);
							holder.qty
									.setBackgroundResource(R.drawable.edittext_normal);
						}
					} else {
						holder.qty
								.setBackgroundResource(R.drawable.edittext_normal);
						holder.carton
								.setBackgroundResource(R.drawable.edittext_normal);
					}

				}
			}
			/*String base = product.getProductImage();

			if (base != null && !base.isEmpty()) {
				try {
					*//*byte[] imageAsBytes = Base64.decode(base.getBytes(),
							Base64.DEFAULT);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 8;
					Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0,
							imageAsBytes.length, options);
					holder.productImage.setImageBitmap(bmp);*//*
					decodeBase64File(base,holder.productImage);

				} catch (OutOfMemoryError e) {
					*//*byte[] imageAsBytes = Base64.decode(base.getBytes(),
							Base64.DEFAULT);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 8;
					Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0,
							imageAsBytes.length, options);
					holder.productImage.setImageBitmap(bmp);*//*

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				holder.productImage.setImageResource(R.mipmap.no_image);
			}*/

			String merchandiseImage = product.getProductImage();

			//Log.d("merchandiseImage",merchandiseImage);

			if(merchandiseImage!=null){
				decodeBase64File(merchandiseImage,holder.productImage);
			}else{
				holder.productImage.setImageResource(R.mipmap.no_image);
			}



//			Bitmap bitmap = product.getBitmap();
//			if(bitmap!=null){
//				holder.productImage.setImageBitmap(bitmap);
//			}else{
//				holder.productImage.setImageResource(R.mipmap.no_image);
//			}

			holder.qty.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent motionevent) {
					EditText edQty = (EditText) v;
					orientation = getResources().getConfiguration().orientation;
					if (screenInches > 7) {
						if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
							mPosition = v.getId();
							hideKeyboard(edQty);
							mEditValue = edQty;
							mkeyActionDoneStr = "Qty";
							keyViewActionDone = v;
							edQty.setFocusable(false);
							edQty.setFocusableInTouchMode(false);
							notifyDataSetChanged();
						} else if (Configuration.ORIENTATION_PORTRAIT == orientation) {
							Log.d("PORTRAIT", "PORTRAIT");
							edQty.requestFocus();
							showKeyboard(edQty);
						}
					} else {
						Log.d("mobile", "mobile");
						edQty.requestFocus();
						showKeyboard(edQty);
					}
					return false;
				}
			});
			holder.qty.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						qtyActionDone(v);
						return true;
					}
					return false;
				}
			});
			holder.qtyMinus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					qtyMinusAction(v);
				}
			});
			holder.qtyPlus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					qtyPlusAction(v);

				}
			});
			holder.qty.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent motionevent) {
					EditText edQty = (EditText) v;
					edQty.requestFocus();
					showKeyboard(edQty);
					edQty.addTextChangedListener(new QtyWatcher(v,edQty));

					return false;
				}
			});
			/*holder.carton.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent motionevent) {
					EditText edCarton = (EditText) v;
					edCarton.requestFocus();
					showKeyboard(edCarton);
					edCarton.addTextChangedListener(new CartonQtyWatcher(v,edCarton));

					return false;
				}
			});*/
			holder.loose.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent motionevent) {
					EditText edLoose = (EditText) v;
					edLoose.requestFocus();
					showKeyboard(edLoose);
					edLoose.addTextChangedListener(new LooseQtyWatcher(v,edLoose));

					return false;
				}
			});
			holder.carton.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent motionevent) {
					EditText edCarton = (EditText) v;
					orientation = getResources().getConfiguration().orientation;
					if (screenInches > 7) {
						if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
							mPosition = v.getId();
							hideKeyboard(edCarton);
							mEditValue = edCarton;
							mkeyActionDoneStr = "CQty";
							keyViewActionDone = v;
							edCarton.setFocusable(false);
							edCarton.setFocusableInTouchMode(false);
							notifyDataSetChanged();
						} else if (Configuration.ORIENTATION_PORTRAIT == orientation) {
							edCarton.setFocusable(true);
							edCarton.setFocusableInTouchMode(true);
							edCarton.requestFocus();
							edCarton.addTextChangedListener(new CartonQtyWatcher(v,edCarton));
							showKeyboard(edCarton);

						}
					} else {
						edCarton.requestFocus();
						showKeyboard(edCarton);
						edCarton.addTextChangedListener(new CartonQtyWatcher(v,edCarton));
					}
					return false;
				}
			});
			holder.carton
					.setOnEditorActionListener(new OnEditorActionListener() {

						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_DONE) {
								cartonActionDone(v);
								return true;
							}
							return false;
						}
					});

			holder.cartonMinus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					cartonMinusAction(v);
				}
			});

			holder.cartonPlus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					cartonPlusAction(v);
				}
			});
			holder.order.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					addToCart(v,false);
				}
			});
			holder.add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {

					addToCart(v,true);
				}
			});

			holder.productImage.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
					try {
						String haveAttributeFlag = SalesOrderSetGet.getHaveAttribute();
						if (haveAttributeFlag.matches("2")) {
							int position = v.getId();
							product = getItem(position);
							String haveAttribute = product.getHaveAttribute();
							if (haveAttribute.equalsIgnoreCase("1")) {
								String productCode = DBCatalog
										.getProductCodeValue(product.getProductCode());
								if (productCode != null && !productCode.isEmpty()) {
									Toast.makeText(
											mContext,
											"This product is already added to the cart",
											Toast.LENGTH_SHORT).show();
								} else {
									String code = product.getProductCode();
									String name = product.getProductName();
									String productImage = product.getProductImage();
									new GetProductAttribute(v, false, code, name, productImage).execute();
								}
							} else {
								launchActivity(v);
							}
						}else {
							launchActivity(v);
						}
					} catch (TransactionTooLargeException e) {
						e.printStackTrace();
					}

				}
			});
			String haveAttributeFlag = SalesOrderSetGet.getHaveAttribute();
				if (haveAttributeFlag.matches("2")) {
					String haveAttribute = product.getHaveAttribute();
					if(!haveAttribute.equalsIgnoreCase("0")){
						holder.carton.setEnabled(false);
						holder.loose.setEnabled(false);
						holder.qty.setEnabled(false);
						holder.cartonMinus.setImageResource(R.mipmap.inactive_minus);
						holder.cartonPlus.setImageResource(R.mipmap.inactive_plus);
						holder.qtyPlus.setImageResource(R.mipmap.inactive_plus);
						holder.qtyMinus.setImageResource(R.mipmap.inactive_minus);
						holder.cartonMinus.setEnabled(false);
						holder.cartonPlus.setEnabled(false);
						holder.qtyPlus.setEnabled(false);
						holder.qtyMinus.setEnabled(false);
					}
				}



			return row;

		}

		class ViewHolder {
			private TextView productCode,productName, carton_lbl, wholesalePrice,
					cartonPrice, qty_lbl, loose, pcspercarton,ordered_qty,price;

			private ImageView productImage, qtyMinus, qtyPlus, cartonMinus,
					cartonPlus;
			private EditText qty, carton;
			// private ImageButton qtyMinus, qtyPlus, cartonMinus, cartonPlus;
			private LinearLayout mOrderLayout,mAddToCartLayout;
			private Button add,order;

		}
      private void addToCart(View v,boolean isAddBtn){
		  String customercode = Catalog.getCustomerCode();

		  if (customercode != null && !customercode.isEmpty()) {

			  final Button b = (Button) v;
			  int position = v.getId();
			  product = getItem(position);

			  String productCode = DBCatalog
					  .getProductCodeValue(product.getProductCode());
			  if (productCode != null && !productCode.isEmpty()) {
				  Toast.makeText(
						  mContext,
						  "This product is already added to the cart",
						  Toast.LENGTH_SHORT).show();
			  } else {
                 String haveAttributeFlag = SalesOrderSetGet.getHaveAttribute();
				  if(haveAttributeFlag!=null && !haveAttributeFlag.isEmpty()){
					  if(haveAttributeFlag.equalsIgnoreCase("2")){
						  String haveAttribute = product.getHaveAttribute();
						  if(haveAttribute!=null && !haveAttribute.isEmpty()){
							  if(haveAttribute.equalsIgnoreCase("1")){
								  String code = product.getProductCode();
								  String name = product.getProductName();
								  String productImage = product.getProductImage();
								  new GetProductAttribute(v,isAddBtn,code,name,productImage).execute();
							  }else{
								  String showAddToCart = SalesOrderSetGet.getShowAddToCart();
								  if(showAddToCart!=null && !showAddToCart.isEmpty()) {
									  if (showAddToCart.equalsIgnoreCase("0")) {
										  try {
											  launchActivity(v);
										  } catch (Exception e) {
											  e.printStackTrace();
										  }

									  }else{
										  if ((product.getCqty() != null && !product
												  .getCqty().isEmpty())
												  && (product.getQty() != null && !product
												  .getQty().isEmpty())) {

											  if ((Double.valueOf(product.getCqty()) > 0)
													  || (0 < Double.valueOf(product
													  .getQty()))) {

												  totalCalc(v);
												  animCartIcon();
												  if(isAddBtn){
													  b.setText("Added");
													  b.setBackgroundResource(R.drawable.add_cart_selected);
													  b.setTextColor(Color.parseColor("#FFFFFF"));
												  }


											  } else {

												  Log.d("product.getCqty()",
														  "" + product.getCqty());

												  Log.d("product.getQty()",
														  "" + product.getQty());
												  Toast.makeText(mContext,
														  "Please Enter the Value",
														  Toast.LENGTH_SHORT).show();
											  }
										  } else {
											  Log.d("product.getCqty()",
													  "null" + product.getCqty());

											  Log.d("product.getQty()",
													  "null" + product.getQty());

											  Toast.makeText(mContext,
													  "Please Enter the Value",
													  Toast.LENGTH_SHORT).show();
										  }

									  }
								  }else{
									  try {
										  launchActivity(v);
									  } catch (Exception e) {
										  e.printStackTrace();
									  }
								  }
							  }
						  }else{
							  if ((product.getCqty() != null && !product
									  .getCqty().isEmpty())
									  && (product.getQty() != null && !product
									  .getQty().isEmpty())) {

								  if ((Double.valueOf(product.getCqty()) > 0)
										  || (0 < Double.valueOf(product
										  .getQty()))) {

									  totalCalc(v);
									  animCartIcon();
									  if(isAddBtn){
										  b.setText("Added");
										  b.setBackgroundResource(R.drawable.add_cart_selected);
										  b.setTextColor(Color.parseColor("#FFFFFF"));
									  }


								  } else {

									  Log.d("product.getCqty()",
											  "" + product.getCqty());

									  Log.d("product.getQty()",
											  "" + product.getQty());
									  Toast.makeText(mContext,
											  "Please Enter the Value",
											  Toast.LENGTH_SHORT).show();
								  }
							  } else {
								  Log.d("product.getCqty()",
										  "null" + product.getCqty());

								  Log.d("product.getQty()",
										  "null" + product.getQty());

								  Toast.makeText(mContext,
										  "Please Enter the Value",
										  Toast.LENGTH_SHORT).show();
							  }

						  }
					  }else{
						  if ((product.getCqty() != null && !product
								  .getCqty().isEmpty())
								  && (product.getQty() != null && !product
								  .getQty().isEmpty())) {

							  if ((Double.valueOf(product.getCqty()) > 0)
									  || (0 < Double.valueOf(product
									  .getQty()))) {

								  totalCalc(v);
								  animCartIcon();
								  if(isAddBtn){
									  b.setText("Added");
									  b.setBackgroundResource(R.drawable.add_cart_selected);
									  b.setTextColor(Color.parseColor("#FFFFFF"));
								  }


							  } else {

								  Log.d("product.getCqty()",
										  "" + product.getCqty());

								  Log.d("product.getQty()",
										  "" + product.getQty());
								  Toast.makeText(mContext,
										  "Please Enter the Value",
										  Toast.LENGTH_SHORT).show();
							  }
						  } else {
							  Log.d("product.getCqty()",
									  "null" + product.getCqty());

							  Log.d("product.getQty()",
									  "null" + product.getQty());

							  Toast.makeText(mContext,
									  "Please Enter the Value",
									  Toast.LENGTH_SHORT).show();
						  }

					  }
				  }else{
					  if ((product.getCqty() != null && !product
							  .getCqty().isEmpty())
							  && (product.getQty() != null && !product
							  .getQty().isEmpty())) {

						  if ((Double.valueOf(product.getCqty()) > 0)
								  || (0 < Double.valueOf(product
								  .getQty()))) {

							  totalCalc(v);
							  animCartIcon();
							  if(isAddBtn){
								  b.setText("Added");
								  b.setBackgroundResource(R.drawable.add_cart_selected);
								  b.setTextColor(Color.parseColor("#FFFFFF"));
							  }


						  } else {

							  Log.d("product.getCqty()",
									  "" + product.getCqty());

							  Log.d("product.getQty()",
									  "" + product.getQty());
							  Toast.makeText(mContext,
									  "Please Enter the Value",
									  Toast.LENGTH_SHORT).show();
						  }
					  } else {
						  Log.d("product.getCqty()",
								  "null" + product.getCqty());

						  Log.d("product.getQty()",
								  "null" + product.getQty());

						  Toast.makeText(mContext,
								  "Please Enter the Value",
								  Toast.LENGTH_SHORT).show();
					  }

				  }




			  }

		  } else {
			  mPager.setCurrentItem(0);
		  }

	  }
		private class GetProductAttribute extends AsyncTask<String, String, String> {
			private ArrayList<HashMap<String, String>> colorArr;
			private View v;
			private String result = "",productCode = "",productName="",productImage="";
			private boolean isAddBtn;
			public GetProductAttribute(View v,boolean isAddBtn,String productCode,String productName,String productImage){
				mAttributeArr.clear();
				this.v = v;
				this.isAddBtn = isAddBtn;
				this.productCode = productCode;
				this.productName = productName;
				this.productImage = productImage;
				this.colorArr = new ArrayList<HashMap<String, String>>();
			}
			@Override
			protected void onPreExecute() {
				helper.showProgressView(mCatalogLayout);
			}
			@Override
			protected String doInBackground(String... params) {
				colorArr = SalesProductWebService.getProductAttribute(
						productCode, "fncGetProductAttribute");
				return result;
			}


			@Override
			protected void onPostExecute(String result) {
				helper.dismissProgressView(mCatalogLayout);
              if(colorArr.size()>0){
				  ColorAttributeDialog  productModifierDialog = new ColorAttributeDialog();
				  productModifierDialog.setProductImage(productImage);
				  productModifierDialog.initiatePopupWindow(
						  getActivity(),productCode,productName,colorArr,null,0);
				  productModifierDialog.show(getActivity().getFragmentManager(), "dialog");

				  productModifierDialog.setOnCompletionListener(new ColorAttributeDialog.OnCompletionListener() {
					  @Override
					  public void onCompleted(String qty,ArrayList<Attribute> attributeArr) {
						 attributeQtyCalc(v,qty,isAddBtn,attributeArr);

					  }
				  });
			  }

			}
		}
		private void attributeQtyCalc(View v,String quantity,boolean isAddBtn,ArrayList<Attribute> attributeArr){

			double cqty = 0, qty = 0, pcs = 0, lqty = 0;
			mAttributeArr = attributeArr;
			Log.d("mProductAttributeArr","-->"+mAttributeArr.size());
			product.setQty(quantity);
			String pcspercarton = product.getPcspercarton();
			try {
				if (pcspercarton != null && !pcspercarton.isEmpty()){
					pcs = Double.valueOf(pcspercarton);
				}
				if (quantity != null && !quantity.isEmpty()){
					qty = Double.valueOf(quantity);
				}
				if (qty > 0) {

					cqty = qty / pcs;
					lqty = qty % pcs;

					product.setLqty(String.valueOf(lqty));
					product.setCqty(String.valueOf(cqty).split("\\.")[0]);

				}
				totalCalc(v);
				animCartIcon();
				if(isAddBtn){
					Button b = (Button) v;
					b.setText(R.string.added);
				}
				helper.showLongToast(R.string.added);
				notifyDataSetChanged();

			} catch (Exception e) {
				e.printStackTrace();
			} catch (StackOverflowError e) {
				e.printStackTrace();
			}

		}
		private class QtyWatcher implements TextWatcher{
			EditText edQty;
			private View view;
			private QtyWatcher(View view,EditText edQty ) {
				this.view = view;
				this.edQty = edQty;
			}

			@Override
			public void afterTextChanged(Editable s) {
				try{
					String qtyString = s.toString().trim();
				//	String qtyStr = s.toString().trim().split("\\.")[0];
					double quantity = qtyString.equals("") ? 0:Double.valueOf(qtyString);
					int position = view.getId();
					product = getItem(position);
					double qty = product.getQty().equals("") ? 0:Double.valueOf(product.getQty());
					if(quantity!=qty){
						product.setQty(String.valueOf(quantity));
						notifyDataSetChanged();
						priceflag = SalesOrderSetGet.getCartonpriceflag();
						if (priceflag.matches("1")) {
							qtyCalc(view);
						}else{

						}



					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
										  int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub

			}

		}
		private class CartonQtyWatcher implements TextWatcher{
			EditText edCQty;
			private View view;
			private CartonQtyWatcher(View view,EditText edCQty) {
				this.view = view;
				this.edCQty = edCQty;
			}

			@Override
			public void afterTextChanged(Editable s) {
				try{
					String cqtyString = s.toString().trim();
					double cquantity = cqtyString.equals("") ? 0:Double.valueOf(cqtyString);
					int position = view.getId();
					product = getItem(position);
					double cqty = product.getCqty().equals("") ? 0:Double.valueOf(product.getCqty());
					if(cquantity!=cqty){
						product.setCqty(String.valueOf(cquantity).split("\\.")[0]);
						notifyDataSetChanged();
						cQtyCalc(view);

					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
										  int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub

			}

		}
		private class LooseQtyWatcher implements TextWatcher{
			EditText edLQty;
			private View view;
			private LooseQtyWatcher(View view,EditText edLQty) {
				this.view = view;
				this.edLQty = edLQty;
			}

			@Override
			public void afterTextChanged(Editable s) {
				try{
					String lqtyString = s.toString().trim();
					double lquantity = lqtyString.equals("") ? 0:Double.valueOf(lqtyString);
					int position = view.getId();
					product = getItem(position);
					double lqty = product.getLqty().equals("") ? 0:Double.valueOf(product.getLqty());
					if(lquantity!=lqty){
						product.setLqty(String.valueOf(lquantity));
						notifyDataSetChanged();
						cQtyCalc(view);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
										  int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub

			}

		}
		public void decodeBase64File(String imageString,ImageView imageView) {
			try{
				byte[] encodeByte = Base64.decode(imageString, Base64.DEFAULT);

			/* There isn't enough memory to open up more than a couple camera photos */
			/* So pre-scale the target bitmap into which the file is decoded */

			/* Get the size of the ImageView */
				int targetW = imageView.getWidth();
				int targetH = imageView.getHeight();

			/* Get the size of the image */
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inJustDecodeBounds = true;
				//BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
				Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,encodeByte.length, bmOptions);
				int photoW = bmOptions.outWidth;
				int photoH = bmOptions.outHeight;

			/* Figure out which way needs to be reduced less */
				int scaleFactor = 1;
				if ((targetW > 0) || (targetH > 0)) {
					scaleFactor = Math.min(photoW/targetW, photoH/targetH);
				}

			/* Set bitmap options to scale the image decode target */
				bmOptions.inJustDecodeBounds = false;
				bmOptions.inSampleSize = scaleFactor;
				bmOptions.inPurgeable = true;

			/* Decode the JPEG file into a Bitmap */
				//	Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
				bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
						encodeByte.length, bmOptions);

//			Bitmap bitmaprst = rotate(bitmap,90);

				imageView.setImageBitmap(bitmap);
			}catch (OutOfMemoryError e){
				e.printStackTrace();
			}
		}

		public void launchActivity(View v) throws TransactionTooLargeException {
			if (Catalog.getCustomerCode() != null
					&& !Catalog.getCustomerCode().isEmpty()
					&& Catalog.getCustomerName() != null
					&& !Catalog.getCustomerName().isEmpty()) {


//				HashMap<String ,ImageButton> mHashMapIcon = new HashMap<>();
//				mHashMapIcon.put("CartIcon",mCartIcon);
//
//				HashMap<String, TextView> mHashMapTextView = new HashMap<>();
//				mHashMapTextView.put("CartText",mCartTxt);

				int position = v.getId();
				product = getItem(position);
				CatalogProductDetails.setProductCode(product.getProductCode());
				CatalogProductDetails.setProductName(product.getProductName());
				CatalogProductDetails
						.setCategoryCode(product.getCategoryCode());
				CatalogProductDetails.setSubCategoryCode(product
						.getSubCategoryCode());
				CatalogProductDetails.setUom(product.getUom());
				CatalogProductDetails
						.setPcsPerCarton(product.getPcspercarton());
				CatalogProductDetails.setWholesalePrice(product
						.getWholesalePrice());
				CatalogProductDetails
						.setProductImage(product.getProductImage());
				CatalogProductDetails.setSpecification(product
						.getSpecification());
				CatalogProductDetails.setRetailPrice(product.getRetailPrice());
				CatalogProductDetails.setHaveAttribute(product.getHaveAttribute());
				catalogproductdetailfragment = CatalogProductDetailFragment.newInstance(/*
						mHashMapIcon, mHashMapTextView*/);
				FragmentTransaction transaction = getChildFragmentManager()
						.beginTransaction();
				// Store the Fragment in stack
				transaction.addToBackStack(null);
				transaction.replace(R.id.fragment_mainLayout,
						catalogproductdetailfragment).commit();
				SOCatalogActivity.mChangeView.setVisibility(View.GONE);
				SOCatalogActivity.mFilterIcon.setVisibility(View.GONE);
				SOCatalogActivity.mSearchIcon.setVisibility(View.GONE);
				SOCatalogActivity.mBack.setVisibility(View.GONE);
				SOCatalogActivity.mClose.setVisibility(View.GONE);
				SOCatalogActivity.mSearchEd.setVisibility(View.GONE);
				Catalog.setChildFragmentVisible(true);
			} else {
				mPager.setCurrentItem(0);
			}

		}

		private void qtyPlusAction(View v) {
			int position = v.getId();
			mPosition = position;
			product = getItem(position);
			if (product.getQty() != null && !product.getQty().isEmpty()) {
				qtyFlag = Double.valueOf(product.getQty().toString());
			} else {
				qtyFlag = 0;
			}
			product.setQty(String.valueOf(++qtyFlag));
			mkeyActionDoneStr = "Qty";
			notifyDataSetChanged();
			qtyCalc(v);
		}

		private void qtyMinusAction(View v) {
			int position = v.getId();
			mPosition = position;
			product = getItem(position);
			if (product.getQty() != null && !product.getQty().isEmpty()) {
				qtyFlag = Double.valueOf(product.getQty().toString());
				if (qtyFlag > 1) {
					product.setQty(String.valueOf(--qtyFlag));
				} else {
					product.setQty("1");
				}
			}
			mkeyActionDoneStr = "Qty";
			notifyDataSetChanged();
			qtyCalc(v);
		}

		private void qtyActionDone(View v) {
			EditText edQty = (EditText) v;
			int position = v.getId();
			mPosition = position;
			product = getItem(position);
			product.setQty(edQty.getText().toString());

			notifyDataSetChanged();
			qtyCalc(v);
			hideKeyboard(edQty);
		}

		private void cartonPlusAction(View v) {
			int position = v.getId();
			mPosition = position;
			product = getItem(position);
			if (product.getCqty() != null && !product.getCqty().isEmpty()) {
				cartonFlag = Double.valueOf(product.getCqty().toString());
			} else {
				cartonFlag = 1;
			}
			product.setCqty(String.valueOf(++cartonFlag));
			mkeyActionDoneStr = "CQty";
			notifyDataSetChanged();
			cQtyCalc(v);
		}

		private void cartonMinusAction(View v) {
			int position = v.getId();
			mPosition = position;
			product = getItem(position);
			if (product.getCqty() != null && !product.getCqty().isEmpty()) {
				cartonFlag = Double.valueOf(product.getCqty().toString());
				if (cartonFlag > 1) {
					product.setCqty(String.valueOf(--cartonFlag));
				} else {
					product.setCqty("1");
				}
			}
			mkeyActionDoneStr = "CQty";
			notifyDataSetChanged();
			cQtyCalc(v);
		}

		private void cartonActionDone(View v) {
			EditText edCarton = (EditText) v;
			int position = v.getId();
			mPosition = position;
			product = getItem(position);
			product.setCqty(edCarton.getText().toString());

			notifyDataSetChanged();

			cQtyCalc(v);
			hideKeyboard(edCarton);
		}

		private void qtyCalc(View v) {
			double cqty = 0, qty = 0, pcs = 0, lqty = 0;
			int position = v.getId();
			product = getItem(position);

			String quantity = product.getQty();
			String pcspercarton = product.getPcspercarton();

			try {

				if ((pcspercarton != null && !pcspercarton.isEmpty() && !pcspercarton
						.equals("0"))
						&& (quantity != null && !quantity.isEmpty())) {

					pcs = Double.valueOf(pcspercarton);

					qty = Double.valueOf(quantity);

					if (qty > 0) {
						cqty = (int) (qty / pcs);
						lqty = qty % pcs;

						String ctn = twoDecimalPoint(cqty);
						String loose = twoDecimalPoint(lqty);

						product.setLqty(String.valueOf(loose));
						product.setCqty(String.valueOf(ctn).split("\\.")[0]);

						Log.d("lQtyCalc", "" + loose);
						Log.d("cQtyCalc", "" + ctn);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} catch (StackOverflowError e) {
				e.printStackTrace();
			}
			notifyDataSetChanged();
		}

		// cQtyCalc Calculation
		private void cQtyCalc(View v) {

			double cqty = 0, pcs = 1, lqty = 0, qty = 0;
			int position = v.getId();
			product = getItem(position);

			String cartonQty = product.getCqty();
			String pcspercarton = product.getPcspercarton();

			try {
				if ((cartonQty != null && !cartonQty.isEmpty())
						&& (pcspercarton != null && !pcspercarton.isEmpty())) {
					cqty = Double.valueOf(cartonQty);

					pcs = Double.valueOf(pcspercarton);

					if (cqty > 0) {
						qty = cqty * pcs;
					}

					String quantity = twoDecimalPoint(qty);

					Log.d("cQtyCalc", "" + quantity);
					product.setQty(""+quantity);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} catch (StackOverflowError e) {
				e.printStackTrace();

			}
			notifyDataSetChanged();
		}

		// TotalCalc Calculation
		private void totalCalc(View v) {
			double dTotal = 0.00, dQty = 0.00, dCQty = 0.00, dLQty = 0.00, dPrice = 0.00, dCPrice = 0.00;
			int position = v.getId();
			product = getItem(position);
			String quantity = product.getQty();
			String cqty = product.getCqty();
			String lqty = product.getLqty();
			String wholesaleprice = product.getWholesalePrice();
			String cPrice = product.getRetailPrice();

			if (wholesaleprice != null && !wholesaleprice.isEmpty()) {

			} else {
				wholesaleprice = "0";
			}

			if (cPrice != null && !cPrice.isEmpty()) {

			} else {
				cPrice = "0";
			}

			if (cqty != null && !cqty.isEmpty()) {

			} else {
				cqty = "0";
			}

			if (lqty != null && !lqty.isEmpty()) {

			} else {
				lqty = "0";
			}

			if (quantity != null && !quantity.isEmpty()) {

			} else {
				quantity = "0";
			}

			try {

				priceflag = SalesOrderSetGet.getCartonpriceflag();
				if (priceflag.matches("1")) {

					dPrice = Double.parseDouble(wholesaleprice);
					dCPrice = Double.parseDouble(cPrice);

					dCQty = Double.parseDouble(cqty);
					dLQty = Double.parseDouble(lqty);

					dTotal = (dCQty * dCPrice) + (dLQty * dPrice);

				} else {
					dQty = Double.valueOf(quantity);
					dPrice = Double.valueOf(wholesaleprice);
					Log.d("dQty", "" + dQty);
					Log.d("dPrice", "" + dPrice);
					dTotal = dQty * dPrice;
				}

			} catch (Exception e) {
				e.printStackTrace();

			}
			Log.d("dTotal", "--" + dTotal);
			taxCalc(position, dTotal);
		}

		public void taxCalc(int position, double dTotal) {
			product = getItem(position);
			double dSubTotal = 0.00, dTaxValue = 0.00, dTaxAmount = 0.00, dNetTotal = 0.00;
			String total = "0.00", tax = "0.000", netTotal = "0.00", subTotal = "0.00";

			String taxType = SalesOrderSetGet.getCompanytax();
			String taxValue = SalesOrderSetGet.getTaxValue();

			if (taxType != null && !taxType.isEmpty()) {

			} else {
				taxType = "Z";
			}
			if (taxValue != null && !taxValue.isEmpty()) {

			} else {
				taxValue = "0.0";
			}

			Log.d("taxType", taxType);
			Log.d("taxValue adapter", taxValue);

			try {

				dSubTotal = dTotal;

				if ((taxType != null && !taxType.isEmpty())
						&& (taxValue != null && !taxValue.isEmpty())) {

					dTaxValue = Double.parseDouble(taxValue);

					if (taxType.matches("E")) {

						dTaxAmount = (dTotal * dTaxValue) / 100;
						tax = fourDecimalPoint(dTaxAmount);

						dNetTotal = dTotal + dTaxAmount;
						netTotal = twoDecimalPoint(dNetTotal);
					} else if (taxType.matches("I")) {

						dTaxAmount = (dTotal * dTaxValue) / (100 + dTaxValue);
						tax = fourDecimalPoint(dTaxAmount);

						// dNetTotal = dTotal + dTaxAmount;
						dNetTotal = dTotal;
						netTotal = twoDecimalPoint(dNetTotal);

					} else if (taxType.matches("Z")) {

						// dNetTotal = dTotal + dTaxAmount;
						dNetTotal = dTotal;
						netTotal = twoDecimalPoint(dNetTotal);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			total = twoDecimalPoint(dTotal);
			subTotal = twoDecimalPoint(dSubTotal);
			// netTotal = twoDecimalPoint(dNetTotal);

			Log.d("total", "" + total);
			Log.d("subTotal", "" + subTotal);
			Log.d("taxType", "" + taxType);
			Log.d("taxValue", "" + taxValue);
			Log.d("tax", "" + tax);
			Log.d("netTotal", "" + netTotal);

			Log.d("getCqty", "" + product.getCqty());
			Log.d("getLqty", "" + product.getLqty());
			Log.d("getQty", "" + product.getQty());

			int slNo = dbhelper.getCount();
			Log.d("dbhelper.getCount()", "" + slNo);

			String sno = String.valueOf(++slNo);
			productvalues.put(COLUMN_PRODUCT_SLNO, sno);
			productvalues.put(COLUMN_PRODUCT_CODE, product.getProductCode());
			productvalues.put(COLUMN_PRODUCT_NAME, product.getProductName());
			productvalues.put(COLUMN_CARTON_QTY, product.getCqty());
			productvalues.put(COLUMN_LOOSE_QTY, product.getLqty());
			productvalues.put(COLUMN_QUANTITY, product.getQty());
			productvalues.put(COLUMN_PRICE, product.getWholesalePrice());
			productvalues.put(COLUMN_WHOLESALEPRICE,
					product.getWholesalePrice());
			productvalues.put(COLUMN_UOMCODE, product.getUom());
			productvalues.put(COLUMN_PCSPERCARTON, product.getPcspercarton());
			productvalues.put(COLUMN_TAXTYPE, taxType);
			productvalues.put(COLUMN_TAXVALUE, taxValue);
			productvalues.put(COLUMN_TAX, tax);
			productvalues.put(COLUMN_TOTAL, total);
			productvalues.put(COLUMN_SUB_TOTAL, subTotal);
			productvalues.put(COLUMN_NETTOTAL, netTotal);
			productvalues.put(COLUMN_PRODUCT_IMAGE, product.getProductImage());

			productvalues.put(COLUMN_FOC, "0");
			productvalues.put(COLUMN_ITEM_DISCOUNT, "0");
			productvalues.put(COLUMN_CARTONPRICE, product.getRetailPrice());
			productvalues.put(COLUMN_EXCHANGEQTY, "0");

			DBCatalog.storeCatalog(productvalues);
			Log.d("mProductAttributeArr","dn-->"+mAttributeArr.size());

			if(mAttributeArr.size()>0){
				SOTDatabase.storeAttribute(sno,product.getProductCode(), product.getProductName(),mAttributeArr);
			}

		}

		public void bitmap() {

		}

		protected void showKeyboard(EditText editText) {
			InputMethodManager inputManager = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(editText,
					InputMethodManager.SHOW_IMPLICIT);
		}

		public void hideKeyboard(EditText edittext) {
			InputMethodManager inputmethodmanager = (InputMethodManager) mContext
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmethodmanager.hideSoftInputFromWindow(
					edittext.getWindowToken(), 0);

		}

		protected String twoDecimalPoint(double d) {
			DecimalFormat df = new DecimalFormat("#.##");
			df.setMinimumFractionDigits(2);
			String tot = df.format(d);
			return tot;
		}

		protected String fourDecimalPoint(double d) {
			DecimalFormat df = new DecimalFormat("#.####");
			df.setMinimumFractionDigits(4);
			String tot = df.format(d);
			return tot;
		}
	}

//	public String pcs(String slCartonPerQty) {
//
//		String returnpcs = "";
//
//		if (slCartonPerQty.matches("0") || slCartonPerQty.matches("null")) {
//			returnpcs = "1";
//		} else {
//			String part1 = "", part2 = "";
//			try {
//				Log.d("point", "...");
//				StringTokenizer tokens = new StringTokenizer(slCartonPerQty,
//						".");
//				part1 = tokens.nextToken(); // 12
//				part2 = tokens.nextToken(); // 000
//
//				if (part2.matches("0") || part2.matches("00")
//						|| part2.matches("000") || part2.matches("0000")
//						|| part2.matches("00000")) {
//
//					returnpcs = part1;
//
//					Log.d("part1", "" + part1);
//				} else {
//
//					returnpcs = slCartonPerQty;
//
//					Log.d("part2", "" + slCartonPerQty);
//				}
//			} catch (Exception e) {
//				Log.d("no point", "" + slCartonPerQty);
//				Log.d("part1", "" + part1);
//				e.printStackTrace();
//				returnpcs = part1;
//
//			}
//
//		}
//		return returnpcs;
//	}
private void loadSearchProductItem(ArrayList<String> searchlist) {
	productList.clear();
	for (int s = 0; s < searchlist.size(); s++) {

		if (searchlist.size() > 0) {
			Log.d("ProductCode", "" + searchlist.get(s));

			new XMLAccessTask(getActivity(), valid_url,
					"fncGetProductMainImage", searchlist.get(s),
					mCustomerGroupCode, false, new CallbackInterface() {

				@Override
				public void onSuccess(NodeList nl) {

					for (int i = 0; i < nl.getLength(); i++) {
						// creating new HashMap
						Element e = (Element) nl.item(i);
						Product product = new Product();
						// adding each child node to Product Pojo
						// class =>
						// value
						product.setProductCode(XMLParser.getValue(
								e, KEY_PRODUCTCODE));
						product.setProductName(XMLParser.getValue(
								e, KEY_PRODUCTNAME));
						product.setCategoryCode(XMLParser.getValue(
								e, KEY_CATEGORYCODE));
						product.setSubCategoryCode(XMLParser
								.getValue(e, KEY_SUBCATEGORYCODE));
						product.setUom(XMLParser.getValue(e,
								KEY_UOMCODE));
						String pcspercarton = XMLParser
								.getValue(e, KEY_PCSPERCARTON);
						product.setPcspercarton(pcspercarton);
						// product.setPcspercarton(parser.getValue(e,
						// KEY_PCSPERCARTON));
						product.setWholesalePrice(XMLParser
								.getValue(e, KEY_WHOLESALEPRICE));
						product.setProductImage(XMLParser.getValue(
								e, KEY_PRODUCTIMAGE));
						product.setSpecification(XMLParser
								.getValue(e, KEY_SPECIFICATION));
						product.setHaveAttribute(XMLParser.getValue(e,
								KEY_HAVEATTRIBUTE));
						String retailPrice = null;
						retailPrice = XMLParser.getValue(e,
								KEY_RETAILPRICE);
						if (retailPrice != null
								&& !retailPrice.isEmpty()) {
							product.setRetailPrice(XMLParser
									.getValue(e, KEY_RETAILPRICE));
						} else {
							product.setRetailPrice("0");
						}

						if (pcspercarton != null
								&& !pcspercarton.isEmpty()) {
							if (Double.valueOf(pcspercarton) > 1) {

								double pcs = Double
										.valueOf(pcspercarton);
								double carton = pcs * 1;
								product.setQty(String
										.valueOf(carton));
							} else {
								product.setQty("1");
							}
						}
						product.setCqty("1");
						product.setLqty("0");

						// adding all childnode to ArrayList
						productList.add(product);
						mainImageAdapter.notifyDataSetChanged();
					}

					screenInches = helper.displayMetrics();
					if (screenInches > 7) {
						// Check orientation
						orientation = getResources()
								.getConfiguration().orientation;
						if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
							// Landscape
							mGridView.setNumColumns(4);
						} else {
							// Portrait
							mGridView.setNumColumns(2);
						}
						mCarouselFragment.checkOrientation(
								orientation, screenInches);
					} else {
						mGridView.setNumColumns(2);
					}

				}

				@Override
				public void onFailure(ErrorType error) {
					onError(error);
					helper.dismissProgressView(mCatalogLayout);
				}

			}).execute();

		}

	}

	helper.dismissProgressView(mCatalogLayout);
	SOCatalogActivity.mBack.setVisibility(View.VISIBLE);
	SOCatalogActivity.mCartIcon.setVisibility(View.GONE);
	SOCatalogActivity.mChangeView.setVisibility(View.GONE);
	SOCatalogActivity.mSearchIcon.setVisibility(View.GONE);
	SOCatalogActivity.mFilterIcon.setVisibility(View.GONE);
	SOCatalogActivity.mSearchEd.setVisibility(View.VISIBLE);
	mGridLayout.setVisibility(View.VISIBLE);
	mSearchLayout.setVisibility(View.GONE);

}
	public abstract class InfiniteScrollListener implements
			AbsListView.OnScrollListener {
		private int bufferItemCount = 12;

		private int itemCount = 0,mScrollState;
		private boolean isLoading = true;

		public InfiniteScrollListener(int bufferItemCount) {
			this.bufferItemCount = bufferItemCount;
		}

		public abstract void loadMore(int page, int totalItemsCount);

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			mScrollState = scrollState;
			if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
				SOCatalogActivity.mChangeView.setClickable(true);
			}
			InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			if (getActivity().getWindow().getCurrentFocus() != null) {
				inputManager.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
							 int visibleItemCount, int totalItemCount) {

			totalItem = firstVisibleItem;

			if (totalItemCount < itemCount) {
				this.itemCount = totalItemCount;
				if (totalItemCount == 0) {
					this.isLoading = true;
				}
			}

			if (isLoading && (totalItemCount > itemCount)) {
				isLoading = false;
				itemCount = totalItemCount;
				currentPage++;

			}

			if (!isLoading
					&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + bufferItemCount)) {
				int pageNo = currentPage + 1;
				//	Log.d("loadMore","-->"+page);
				//	Log.d("loadMore","--> true");
				loadMore(pageNo, totalItemCount);
				isLoading = true;
			}

			if (mScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
				SOCatalogActivity.mChangeView.setClickable(true);
			}
			else{
				SOCatalogActivity.mChangeView.setClickable(false);
			}
		}
	}
	private void hideKeyboard(){
		InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(SOCatalogActivity.mSearchEd.getWindowToken(), 0);
	}

	public static Bitmap decodeSampledBitmapFromResource(byte[] data, int   reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		//options.inPurgeable = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}


	public static int calculateInSampleSize(BitmapFactory.Options options,
											int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 2;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
}
