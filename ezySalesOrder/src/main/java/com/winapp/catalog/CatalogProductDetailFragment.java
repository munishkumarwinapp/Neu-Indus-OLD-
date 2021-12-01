package com.winapp.catalog;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ViewSwitcher.ViewFactory;

import com.winapp.adapter.Attribute;
import com.winapp.adapter.GalleryImageAdapter;
import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.FormSetterGetter;
import com.winapp.helper.Catalog;
import com.winapp.helper.Constants;
import com.winapp.helper.SettingsManager;
import com.winapp.helper.XMLParser;
import com.winapp.model.Product;
import com.winapp.offline.OfflineDatabase;
import com.winapp.printer.UIHelper;
import com.winapp.sot.ColorAttributeDialog;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesProductWebService;
import com.winapp.sotdetails.DBCatalog;
import com.winapp.util.XMLAccessTask;
import com.winapp.util.XMLAccessTask.CallbackInterface;
import com.winapp.util.XMLAccessTask.ErrorType;
/**
 * A simple {@link Fragment} subclass.
 *
 */
public class CatalogProductDetailFragment extends BaseFragment implements Constants,
		OnClickListener {

	private String mTitle;
	private ImageView selectedImageView, leftArrowImageView,
			rightArrowImageView, priceTag;
	private Gallery gallery;
	private int selectedImagePosition = 0,focQtyFlag=0,exQtyFlag=0;

	private List<Product> productSubImagesList;
	private GalleryImageAdapter subImageAdapter;

	private ImageButton mQty_plus, mQty_minus, mCqty_plus,
			mCqty_minus, mLqty_plus, mLqty_minus,mFqty_plus,mFqty_minus,mExqty_plus,mExqty_minus;
	private Intent mIntent;
	private TextView mPcsPerCarton,  mProductName, mSpecification;
	private static TextView mProductCode;
	private Spinner mSpinner;
	private EditText  mEditValue,mfoc_ed,mLPrice,mexchange_ed, mCprice, mcarton_ed, mloose_ed, mqty_ed;

	private String productCode, productName, categoryCode, subCategoryCode,
			uomCode, pcsPerCarton, wholesalePrice, productImage, specification,
			retailPrice, customerCode = "", customerGroupCode = "",haveAttribute="",
			ppsPrice = "", mDialogStatus="";
	private UIHelper helper;
	private HashMap<String, String> paramshm;
	private XMLParser parser;
	private DBCatalog dbhelper;
	private HashMap<String, String> productvalues;
	private ArrayList<PropertyInfo> params;
	private SettingsManager settings;
	private static Button mAdd,mBackBtn;
	private ImageSwitcher Switcher;
	private String valid_url = "", priceflag = "";
	public static final String FUNC_GET_PRODUCT_SUB_IMAGE = "fncGetProductSubImages";
	private LinearLayout drawer_layout;
	private View view;
	private ViewPager viewpage;
	private CarouselFragment mCarouselFragment;
	private OfflineDatabase offlineDatabase;
	private String jsonString = "",mobileHaveOfflineMode="";
	private JSONObject jsonResponse=null;
	private JSONArray jsonMainNode =null;
	private double screenInches,qtyFlag = 1, cartonFlag = 1,
			looseFlag = 1;
	private int orientation;
	private double dPrice = 0.00,dcprice =0.00;
	private ArrayList<Attribute>  mProductAttributeArr;
	public CatalogProductDetailFragment() {
		// Required empty public constructor
	}
	/*public CatalogProductDetailFragment(ImageButton mcartIcon,TextView mcartText) {
        mCartIcon = mcartIcon ;
        mCartTxt = mcartText ;
    }*/
	public static CatalogProductDetailFragment newInstance(/*HashMap<String, ImageButton> mHashMapIcon, HashMap<String, TextView> mHashMapTxtV*/) {
		CatalogProductDetailFragment frag = new CatalogProductDetailFragment();
//		Bundle args = new Bundle();
//		args.putSerializable("CatalogIcon", mHashMapIcon);
//		args.putSerializable("CatalogTextV", mHashMapTxtV);
//		frag.setArguments(args);
		return frag;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_catalog_detail, container, false);
//
//		HashMap<String ,ImageButton> hmIcon = (HashMap<String ,ImageButton>) getArguments().getSerializable("CatalogIcon");
//		HashMap<String ,TextView> hmTextV = (HashMap<String ,TextView>) getArguments().getSerializable("CatalogTextV");
//		mCartIcon = hmIcon.get("CartIcon");
//		mCartTxt =  hmTextV.get("CartText");

		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		helper = new UIHelper(getActivity());
		//DisplayMetrics
		screenInches = helper.displayMetrics();
		viewpage = (ViewPager) getActivity().findViewById(R.id.viewpager);
		// mTitle = (TextView) view.findViewById(R.id.product_Title);
		mBackBtn = (Button) view.findViewById(R.id.back);
		//mCartTxt = (TextView) view.findViewById(R.id.cart_txt);
		// showViews(true, R.id.cart_txt);

		mAdd = (Button) view.findViewById(R.id.add);
		selectedImageView = (ImageView) view
				.findViewById(R.id.selected_imageview);
		leftArrowImageView = (ImageView) view
				.findViewById(R.id.left_arrow_imageview);
		rightArrowImageView = (ImageView) view
				.findViewById(R.id.right_arrow_imageview);
		gallery = (Gallery) view.findViewById(R.id.gallery);
		//  		priceTag = (ImageButton) view.findViewById(R.id.pricetag);
		mcarton_ed = (EditText) view.findViewById(R.id.cqty_ed);
		mloose_ed = (EditText) view.findViewById(R.id.lqty_ed);
		mqty_ed = (EditText) view.findViewById(R.id.qty_ed);
		mfoc_ed = (EditText) view.findViewById(R.id.focqty);
		mexchange_ed = (EditText) view.findViewById(R.id.exchangeqty);
		mFqty_plus = (ImageButton) view.findViewById(R.id.fqty_plus);
		mFqty_minus = (ImageButton) view.findViewById(R.id.fqty_minus);

		mExqty_plus = (ImageButton) view.findViewById(R.id.exqty_plus);
		mExqty_minus = (ImageButton) view.findViewById(R.id.exqty_minus);

		mSpinner = (Spinner) view.findViewById(R.id.spinner);
		// mTitle.setText(R.string.catalog_details);
		mPcsPerCarton = (TextView) view.findViewById(R.id.pcspercarton);

		mLPrice = (EditText) view.findViewById(R.id.lprice);
		mCprice = (EditText) view.findViewById(R.id.cprice);
		mProductCode = (TextView) view.findViewById(R.id.productCode);
		mProductName = (TextView) view.findViewById(R.id.productName);
		mSpecification = (TextView) view.findViewById(R.id.specification);

		drawer_layout = (LinearLayout) view.findViewById(R.id.drawer_layout);

		//view.findViewById(R.id.header_layout).setVisibility(view.GONE);

		// horizontalListView = (HorizontalListView)
		// findViewById(R.id.listView);
		mQty_plus = (ImageButton) view.findViewById(R.id.qty_plus);
		mQty_minus = (ImageButton) view.findViewById(R.id.qty_minus);
		mCqty_plus = (ImageButton) view.findViewById(R.id.cqty_plus);
		mCqty_minus = (ImageButton) view.findViewById(R.id.cqty_minus);
		mLqty_plus = (ImageButton) view.findViewById(R.id.lqty_plus);
		mLqty_minus = (ImageButton) view.findViewById(R.id.lqty_minus);

		Switcher = (ImageSwitcher) view.findViewById(R.id.switcher);

		valid_url = FWMSSettingsDatabase.getUrl();
		dbhelper = new DBCatalog(getActivity());
		settings = new SettingsManager(getActivity());
		parser = new XMLParser();
		mIntent = new Intent();
		paramshm = new HashMap<String, String>();
		mCarouselFragment = new CarouselFragment();
		offlineDatabase = new OfflineDatabase(getActivity());

		productvalues = new HashMap<String, String>();
		mProductAttributeArr =new ArrayList<>();
		productSubImagesList = new ArrayList<Product>();
		productSubImagesList.clear();

		//	mBackBtn.setOnClickListener(this);
		//	mCartIcon.setOnClickListener(this);
		mAdd.setOnClickListener(this);
		leftArrowImageView.setOnClickListener(this);
		rightArrowImageView.setOnClickListener(this);

		mQty_plus.setOnClickListener(this);
		mQty_minus.setOnClickListener(this);
		mCqty_plus.setOnClickListener(this);
		mCqty_minus.setOnClickListener(this);
		mLqty_plus.setOnClickListener(this);
		mLqty_minus.setOnClickListener(this);
		mFqty_plus.setOnClickListener(this);
		mFqty_minus.setOnClickListener(this);
		mExqty_plus.setOnClickListener(this);
		mExqty_minus.setOnClickListener(this);
		String[] cartonQty = getResources().getStringArray(
				R.array.carton_status);

		productCode = CatalogProductDetails.getProductCode();
		productName = CatalogProductDetails.getProductName();
		categoryCode = CatalogProductDetails.getCategoryCode();
		subCategoryCode = CatalogProductDetails.getSubCategoryCode();
		uomCode = CatalogProductDetails.getUom();
		pcsPerCarton = CatalogProductDetails.getPcsPerCarton();
		wholesalePrice = CatalogProductDetails.getWholesalePrice();
		productImage = CatalogProductDetails.getProductImage();
		specification = CatalogProductDetails.getSpecification();
		retailPrice = CatalogProductDetails.getRetailPrice();
		haveAttribute  = CatalogProductDetails.getHaveAttribute();
		if (productImage != null && !productImage.isEmpty()) {

			Product product1 = new Product();
			product1.setProductImage(productImage);
			productSubImagesList.add(0, product1);

		} else {

			showViews(false, R.id.switcher);
			// view.findViewById(R.id.switcher).setVisibility(View.GONE);
			// view.findViewById(R.id.selected_imageview).setVisibility(View.VISIBLE);
			showViews(true, R.id.selected_imageview);
			selectedImageView.setImageResource(R.mipmap.no_image);
		}
		String showProductCode = SalesOrderSetGet.getSelfOrderShowProductCode();
		if (showProductCode != null && !showProductCode.isEmpty()) {
			if(showProductCode.matches("1")){
				mProductCode.setVisibility(View.VISIBLE);
			}else{
				mProductCode.setVisibility(View.GONE);
			}
		}
		else{
			mProductCode.setVisibility(View.GONE);
		}
		mPcsPerCarton.setText(pcsPerCarton);
		Log.d("wholesalePrice", "" + wholesalePrice);

		if (wholesalePrice != null && !wholesalePrice.isEmpty()) {
			dPrice = Double.valueOf(wholesalePrice);
		} else {
			dPrice = 0.00;
		}
		mLPrice.setText(twoDecimalPoint(dPrice));

		if (retailPrice != null && !retailPrice.isEmpty()) {
			dcprice = Double.valueOf(retailPrice);
		} else {
			dcprice = 0.00;
		}
		mCprice.setText(twoDecimalPoint(dcprice));
		mProductCode.setText(productCode);
		mProductName.setText(productName);
		mSpecification.setText(specification);




		priceflag = SalesOrderSetGet.getCartonpriceflag();
		// priceflag="1";
		if (priceflag.matches("1")) {
			showViews(true, R.id.cartonPrice_ll);
			showViews(true, R.id.Price_ll);
			// view.findViewById(R.id.cartonPrice_ll).setVisibility(View.VISIBLE);
			// view.findViewById(R.id.Price_ll).setVisibility(View.VISIBLE);
		} else {
			showViews(false, R.id.cartonPrice_ll);
			showViews(true, R.id.Price_ll);

			// view.findViewById(R.id.cartonPrice_ll).setVisibility(View.GONE);
			// view.findViewById(R.id.Price_ll).setVisibility(View.VISIBLE);

		}

		// showViews(false, R.id.cartonPrice_ll);

		stringToBitmap();
		// getCustomergroupCode();
//     		loadProductSubImages();
		mDialogStatus = mCarouselFragment.checkInternetStatus(getActivity());
		Log.d("checkInternetStatus", "" + mDialogStatus);
		try {
			if (CarouselFragment.onlineMode.matches("True")) {
				if (CarouselFragment.checkOffline == true) {
					if (mDialogStatus.matches("true")) { // Temp Offline
						loadProductSubImagesOffline();
					} else {
						Log.d("CheckOffline Alert -->", "False");
						if(mobileHaveOfflineMode.matches("1")) {
							getActivity().finish();
						}
					}
				} else { // Online
					loadProductSubImages();
				}
			} else if (CarouselFragment.onlineMode.matches("False")) { // Permnt Offline
				loadProductSubImagesOffline();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Switcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
				android.R.anim.fade_in));
		Switcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
				android.R.anim.fade_out));

		Switcher.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				ImageView imageView = new ImageView(getActivity());
				imageView.setBackgroundColor(0xFFFFFFFF);
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				imageView.setPadding(10, 0, 10, 0);
				imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
				return imageView;
			}
		});

		if (FormSetterGetter.isEditPrice()) {
			//		priceTag.setVisibility(View.INVISIBLE);
			mLPrice.setEnabled(true);
			mLPrice.setBackgroundResource(R.drawable.edittext_bg);
			mLPrice.setFocusableInTouchMode(true);

			mCprice.setEnabled(true);
			mCprice.setBackgroundResource(R.drawable.edittext_bg);
			mCprice.setFocusableInTouchMode(true);
		} else {
			//		priceTag.setVisibility(View.VISIBLE);

			mLPrice.setEnabled(false);
			mLPrice.setFocusable(false);

			mLPrice.setBackgroundResource(R.drawable.labelbg);
			mLPrice.setFocusableInTouchMode(false);

			mCprice.setEnabled(false);
			mCprice.setFocusable(false);
			mCprice.setFocusableInTouchMode(false);

			mCprice.setBackgroundResource(R.drawable.labelbg);
		}

     	/*	priceTag.setOnClickListener(new OnClickListener() {

     			@Override
     			public void onClick(View arg0) {
     				// TODO Auto-generated method stub
     				new SOTAdmin(getActivity(), mLPrice, drawer_layout);
     			}
     		});*/

		SOCatalogActivity.mCartSaveIcon.setVisibility(View.GONE);
		SOCatalogActivity.mCartIcon.setVisibility(View.VISIBLE);
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				orientation = getActivity().getResources().getConfiguration().orientation;
				screenInches = helper.displayMetrics();
				Fragment fragment = ((ViewPagerAdapter) viewpage.getAdapter()).getRegisteredFragment(1);
				new BackPressImpl(getParentFragment()).onBackPressed();
				Catalog.setChildFragmentVisible(false);
				mCarouselFragment.refreshViewAction(fragment);
				mCarouselFragment.checkOrientation(orientation,screenInches);
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

		SOCatalogActivity.mKeyBtnThree.setOnClickListener(new OnClickListener() {

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
		SOCatalogActivity.mKeyBtnSeven.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				customKeyboardBtn("7");
			}
		});
		SOCatalogActivity.mKeyBtnEight.setOnClickListener(new OnClickListener() {

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
		SOCatalogActivity.mKeyBtnClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				customKeyboardClearBtn();
			}
		});

		SOCatalogActivity.mCartIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewpage.setCurrentItem(2);
			}
		});
		mcarton_ed.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				orientation = getResources().getConfiguration().orientation;
				if (screenInches > 7) {
					if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
						if (pcsPerCarton != null && !pcsPerCarton.isEmpty()) {
							if (Double.valueOf(pcsPerCarton) > 1) {
								mcarton_ed.setBackgroundResource(R.drawable.edittext_focused);
							}else{
								mcarton_ed.setBackgroundResource(R.drawable.text_disable);
							}
						}else{
							mcarton_ed.setBackgroundResource(R.drawable.text_disable);
						}

						mloose_ed.setBackgroundResource(R.drawable.edittext_normal);
						mexchange_ed.setBackgroundResource(R.drawable.edittext_normal);
						mqty_ed.setBackgroundResource(R.drawable.edittext_normal);
						mfoc_ed.setBackgroundResource(R.drawable.edittext_normal);
						mCprice.setBackgroundResource(R.drawable.edittext_normal);
						mLPrice.setBackgroundResource(R.drawable.edittext_normal);
						mEditValue = mcarton_ed;
						mcarton_ed.setFocusable(false);
						mcarton_ed.setFocusableInTouchMode(false);
						hideKeyboard(v);
					}else if(Configuration.ORIENTATION_PORTRAIT == orientation){
						mcarton_ed.setFocusable(true);
						mcarton_ed.setFocusableInTouchMode(true);
						showKeyboard(mcarton_ed);
					}
				}
				String carton = mcarton_ed.getText().toString();
				if(carton!=null && !carton.isEmpty()){
					double cartonqty = Double.valueOf(carton);
					if(cartonqty == 0){
						mcarton_ed.setText("");
					}
				}



				return false;
			}

		});
		mloose_ed.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				orientation = getResources().getConfiguration().orientation;
				if (screenInches > 7) {
					if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
						if (pcsPerCarton != null && !pcsPerCarton.isEmpty()) {
							if (Double.valueOf(pcsPerCarton) > 1) {
								mloose_ed.setBackgroundResource(R.drawable.edittext_focused);
							}else{
								mloose_ed.setBackgroundResource(R.drawable.text_disable);
							}
						}else{
							mloose_ed.setBackgroundResource(R.drawable.text_disable);
						}

						mcarton_ed.setBackgroundResource(R.drawable.edittext_normal);
						mexchange_ed.setBackgroundResource(R.drawable.edittext_normal);
						mqty_ed.setBackgroundResource(R.drawable.edittext_normal);
						mfoc_ed.setBackgroundResource(R.drawable.edittext_normal);
						mCprice.setBackgroundResource(R.drawable.edittext_normal);
						mLPrice.setBackgroundResource(R.drawable.edittext_normal);
						mloose_ed.setFocusable(false);
						mloose_ed.setFocusableInTouchMode(false);
						mEditValue = mloose_ed;
						hideKeyboard(v);
					}else if(Configuration.ORIENTATION_PORTRAIT == orientation){
						mloose_ed.setFocusable(true);
						mloose_ed.setFocusableInTouchMode(true);
						showKeyboard(mloose_ed);
					}
				}
				String loose = mloose_ed.getText().toString();
				if(loose!=null && !loose.isEmpty()){
					double looseqty = Double.valueOf(loose);
					if(looseqty == 0){
						mloose_ed.setText("");
					}
				}
				return false;
			}
		});
		mqty_ed.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				orientation = getResources().getConfiguration().orientation;
				if (screenInches > 7) {
					if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
						mEditValue = mqty_ed;
						mqty_ed.setFocusable(false);
						mqty_ed.setFocusableInTouchMode(false);
						mqty_ed.setCursorVisible(true);
						if (pcsPerCarton != null && !pcsPerCarton.isEmpty()) {
							if (Double.valueOf(pcsPerCarton) > 1) {
								mcarton_ed.setBackgroundResource(R.drawable.edittext_normal);
								mloose_ed.setBackgroundResource(R.drawable.edittext_normal);
							}else{
								mcarton_ed.setBackgroundResource(R.drawable.text_disable);
								mloose_ed.setBackgroundResource(R.drawable.text_disable);
							}
						}else{
							mcarton_ed.setBackgroundResource(R.drawable.text_disable);
							mloose_ed.setBackgroundResource(R.drawable.text_disable);
						}
						mqty_ed.setBackgroundResource(R.drawable.edittext_focused);
						mfoc_ed.setBackgroundResource(R.drawable.edittext_normal);
						mexchange_ed.setBackgroundResource(R.drawable.edittext_normal);
						mCprice.setBackgroundResource(R.drawable.edittext_normal);
						mLPrice.setBackgroundResource(R.drawable.edittext_normal);
						hideKeyboard(v);

					}else if(Configuration.ORIENTATION_PORTRAIT == orientation){
						mqty_ed.setFocusable(true);
						mqty_ed.setFocusableInTouchMode(true);
						showKeyboard(mqty_ed);
					}
				}
				String qty= mqty_ed.getText().toString();
				if(qty!=null && !qty.isEmpty()){
					double quantity = Double.valueOf(qty);
					if(quantity == 0){
						mqty_ed.setText("");
					}
				}
				return false;
			}
		});
		mfoc_ed.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				orientation = getResources().getConfiguration().orientation;
				if (screenInches > 7) {
					if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
						mEditValue = mfoc_ed;
						if (pcsPerCarton != null && !pcsPerCarton.isEmpty()) {
							if (Double.valueOf(pcsPerCarton) > 1) {
								mcarton_ed.setBackgroundResource(R.drawable.edittext_normal);
								mloose_ed.setBackgroundResource(R.drawable.edittext_normal);
							}else{
								mcarton_ed.setBackgroundResource(R.drawable.text_disable);
								mloose_ed.setBackgroundResource(R.drawable.text_disable);
							}
						}else{
							mcarton_ed.setBackgroundResource(R.drawable.text_disable);
							mloose_ed.setBackgroundResource(R.drawable.text_disable);
						}
						mfoc_ed.setBackgroundResource(R.drawable.edittext_focused);
						mCprice.setBackgroundResource(R.drawable.edittext_normal);
						mLPrice.setBackgroundResource(R.drawable.edittext_normal);
						mqty_ed.setBackgroundResource(R.drawable.edittext_normal);
						mexchange_ed.setBackgroundResource(R.drawable.edittext_normal);

						mfoc_ed.setFocusable(false);
						mfoc_ed.setFocusableInTouchMode(false);
						hideKeyboard(v);

					}else if(Configuration.ORIENTATION_PORTRAIT == orientation){
						mfoc_ed.setFocusable(true);
						mfoc_ed.setFocusableInTouchMode(true);
						showKeyboard(mfoc_ed);
					}
				}
				String focqty= mfoc_ed.getText().toString();
				if(focqty!=null && !focqty.isEmpty()){
					int focQty = Integer.valueOf(focqty);
					if(focQty == 0){
						mfoc_ed.setText("");
					}
				}
				return false;
			}
		});
		mexchange_ed.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				orientation = getResources().getConfiguration().orientation;
				if (screenInches > 7) {
					if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
						mEditValue = mexchange_ed;
						if (pcsPerCarton != null && !pcsPerCarton.isEmpty()) {
							if (Double.valueOf(pcsPerCarton) > 1) {
								mcarton_ed.setBackgroundResource(R.drawable.edittext_normal);
								mloose_ed.setBackgroundResource(R.drawable.edittext_normal);
							}else{
								mcarton_ed.setBackgroundResource(R.drawable.text_disable);
								mloose_ed.setBackgroundResource(R.drawable.text_disable);
							}
						}else{
							mcarton_ed.setBackgroundResource(R.drawable.text_disable);
							mloose_ed.setBackgroundResource(R.drawable.text_disable);
						}
						mexchange_ed.setBackgroundResource(R.drawable.edittext_focused);
						mCprice.setBackgroundResource(R.drawable.edittext_normal);
						mLPrice.setBackgroundResource(R.drawable.edittext_normal);
						mqty_ed.setBackgroundResource(R.drawable.edittext_normal);
						mfoc_ed.setBackgroundResource(R.drawable.edittext_normal);

						mexchange_ed.setFocusable(false);
						mexchange_ed.setFocusableInTouchMode(false);
						hideKeyboard(v);
					}else if(Configuration.ORIENTATION_PORTRAIT == orientation){
						mexchange_ed.setFocusable(true);
						mexchange_ed.setFocusableInTouchMode(true);
						showKeyboard(mexchange_ed);
					}
				}
				String exchangeqty= mexchange_ed.getText().toString();
				if(exchangeqty!=null && !exchangeqty.isEmpty()){
					int exchangeQty = Integer.valueOf(exchangeqty);
					if(exchangeQty == 0){
						mexchange_ed.setText("");
					}
				}
				return false;
			}
		});
		mLPrice.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				orientation = getResources().getConfiguration().orientation;
				if (screenInches > 7) {
					if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
						mEditValue = mLPrice;
						if (pcsPerCarton != null && !pcsPerCarton.isEmpty()) {
							if (Double.valueOf(pcsPerCarton) > 1) {
								mcarton_ed.setBackgroundResource(R.drawable.edittext_normal);
								mloose_ed.setBackgroundResource(R.drawable.edittext_normal);
							}else{
								mcarton_ed.setBackgroundResource(R.drawable.text_disable);
								mloose_ed.setBackgroundResource(R.drawable.text_disable);
							}
						}else{
							mcarton_ed.setBackgroundResource(R.drawable.text_disable);
							mloose_ed.setBackgroundResource(R.drawable.text_disable);
						}
						mLPrice.setBackgroundResource(R.drawable.edittext_focused);
						mexchange_ed.setBackgroundResource(R.drawable.edittext_normal);
						mCprice.setBackgroundResource(R.drawable.edittext_normal);
						mqty_ed.setBackgroundResource(R.drawable.edittext_normal);
						mfoc_ed.setBackgroundResource(R.drawable.edittext_normal);

						mLPrice.setFocusable(false);
						mLPrice.setFocusableInTouchMode(false);
						hideKeyboard(v);
					}else if(Configuration.ORIENTATION_PORTRAIT == orientation){

						mLPrice.setFocusable(true);
						mLPrice.setFocusableInTouchMode(true);
						showKeyboard(mLPrice);
					}
				}
				String price= mLPrice.getText().toString();
				if(price!=null && !price.isEmpty()){
					double lprice = Double.valueOf(price);
					if(lprice == 0){
						mLPrice.setText("");
					}
				}
				return false;
			}
		});
		mCprice.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				orientation = getResources().getConfiguration().orientation;
				if (screenInches > 7) {
					if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
						mEditValue = mCprice;
						if (pcsPerCarton != null && !pcsPerCarton.isEmpty()) {
							if (Double.valueOf(pcsPerCarton) > 1) {
								mcarton_ed.setBackgroundResource(R.drawable.edittext_normal);
								mloose_ed.setBackgroundResource(R.drawable.edittext_normal);
							}else{
								mcarton_ed.setBackgroundResource(R.drawable.text_disable);
								mloose_ed.setBackgroundResource(R.drawable.text_disable);
							}
						}else{
							mcarton_ed.setBackgroundResource(R.drawable.text_disable);
							mloose_ed.setBackgroundResource(R.drawable.text_disable);
						}
						mCprice.setBackgroundResource(R.drawable.edittext_focused);
						mexchange_ed.setBackgroundResource(R.drawable.edittext_normal);
						mLPrice.setBackgroundResource(R.drawable.edittext_normal);
						mqty_ed.setBackgroundResource(R.drawable.edittext_normal);
						mfoc_ed.setBackgroundResource(R.drawable.edittext_normal);

						mCprice.setFocusable(false);
						mCprice.setFocusableInTouchMode(false);
						hideKeyboard(v);
					}else if(Configuration.ORIENTATION_PORTRAIT == orientation){
						mCprice.setFocusable(true);
						mCprice.setFocusableInTouchMode(true);
						showKeyboard(mCprice);
					}
				}
				String cPrice= mCprice.getText().toString();
				if(cPrice!=null && !cPrice.isEmpty()){
					double cprice = Double.valueOf(cPrice);
					if(cprice == 0){
						mCprice.setText("");
					}
				}
				return false;
			}
		});
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int pos, long id) {

				selectedImagePosition = pos;

				if (selectedImagePosition > 0
						&& selectedImagePosition < productSubImagesList.size() - 1) {

					leftArrowImageView.setImageDrawable(getResources()
							.getDrawable(R.mipmap.arrow_left_enabled));
					rightArrowImageView.setImageDrawable(getResources()
							.getDrawable(R.mipmap.arrow_right_enabled));

				} else if (selectedImagePosition == 0) {

					leftArrowImageView.setImageDrawable(getResources()
							.getDrawable(R.mipmap.arrow_left_disabled));

				} else if (selectedImagePosition == productSubImagesList.size() - 1) {

					rightArrowImageView.setImageDrawable(getResources()
							.getDrawable(R.mipmap.arrow_right_disabled));
				}

				changeBorderForSelectedImage(selectedImagePosition);
				setSelectedImage(selectedImagePosition);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

		Log.d("subimage", "" + productSubImagesList.size());

		if (productSubImagesList.size() > 0) {

			gallery.setSelection(selectedImagePosition, false);

		}

		if (productSubImagesList.size() <= 1) {

			rightArrowImageView.setImageDrawable(getResources().getDrawable(
					R.mipmap.arrow_right_disabled));
		} else {
			rightArrowImageView.setImageDrawable(getResources().getDrawable(
					R.mipmap.arrow_right_enabled));
		}

		if (pcsPerCarton != null && !pcsPerCarton.isEmpty()) {

			if (Double.valueOf(pcsPerCarton) > 1) {

				mcarton_ed.setBackgroundResource(R.drawable.edittext_bg);
				mloose_ed.setBackgroundResource(R.drawable.edittext_bg);

				mcarton_ed.setEnabled(true);
				mloose_ed.setEnabled(true);
				mcarton_ed.requestFocus();

				// showViews(true, R.id.cqty_ll);

			} else {
				// showViews(false, R.id.cqty_ll);

				mcarton_ed.setBackgroundResource(R.drawable.text_disable);
				mloose_ed.setBackgroundResource(R.drawable.text_disable);

				mcarton_ed.setEnabled(false);
				mloose_ed.setEnabled(false);
				mqty_ed.requestFocus();

			}
		}
		String haveAttributeFlag = SalesOrderSetGet.getHaveAttribute();
		if(haveAttributeFlag!=null && !haveAttributeFlag.isEmpty() ) {
			if (haveAttributeFlag.matches("2")) {
				if(!haveAttribute.equalsIgnoreCase("0")){
					mcarton_ed.setEnabled(false);
					mloose_ed.setEnabled(false);
					mqty_ed.setEnabled(false);
					mCqty_minus.setImageResource(R.mipmap.inactive_minus);
					mCqty_plus.setImageResource(R.mipmap.inactive_plus);
					mLqty_minus.setImageResource(R.mipmap.inactive_minus);
					mLqty_plus.setImageResource(R.mipmap.inactive_plus);
					mQty_minus.setImageResource(R.mipmap.inactive_minus);
					mQty_plus.setImageResource(R.mipmap.inactive_plus);
					mQty_plus.setEnabled(false);
					mQty_minus.setEnabled(false);
				}

			}
		}


		animCartIcon();
		getProductCode();

		mcarton_ed.addTextChangedListener(clqtywatcher);
		mloose_ed.addTextChangedListener(clqtywatcher);
		mqty_ed.addTextChangedListener(qtywatcher);

		return view;
	}
	private void hideKeyboard(View v){
		v = getActivity().getCurrentFocus();
		if (v != null) {
			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}
	private void showKeyboard(EditText mEditText){
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm .showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);

	}
	// Custom keyboard setting fields
	private void customKeyboardBtn(String numbers) {
		if(mEditValue!=null){
			mEditValue.requestFocus();
			mEditValue.append(numbers);

		}

	}

	// Custom keyboard Clear fields
	private void customKeyboardClearBtn() {
		if(mEditValue!=null){
			int slength = mEditValue.length();
			if (slength > 0) {
				String qty = mEditValue.getText().toString();
				mEditValue.setText(qty.substring(0, qty.length() - 1));
				mEditValue.setSelection(mEditValue.getText().length());
			}
		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		screenInches = helper.displayMetrics();
		orientation = getResources().getConfiguration().orientation;
		// Checks the orientation of the screen
		if(screenInches > 7){
			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// Landscape
				hideKeyboard(view);
			}else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
				// Portrait

			}

			mCarouselFragment.checkOrientation(orientation, screenInches);
		}
	}
	private void loadProductSubImages() {

		if (productCode != null && !productCode.isEmpty()) {

			showViews(false, R.id.image_Layout);
			showViews(false, R.id.detail_layout);
			showViews(true, R.id.load);

			String locationCode = SalesOrderSetGet.getLocationcode();
			String customerGroupCode = Catalog.getCustomerGroupCode();
			paramshm.put("LocationCode", locationCode);
			paramshm.put("ProductCode", productCode);
			paramshm.put("CategoryCode", "");
			paramshm.put("SubCategoryCode", "");
			paramshm.put("CustomerGroupCode", customerGroupCode);

			Log.d("LocationCode", "=>" + paramshm.get("LocationCode"));
			Log.d("ProductCode", "=>" + paramshm.get("ProductCode"));
			Log.d("CategoryCode", "=>" + paramshm.get("CategoryCode"));
			Log.d("SubCategoryCode", "=>" + paramshm.get("SubCategoryCode"));
			Log.d("CustomerGroupCode", "=>" + paramshm.get("CustomerGroupCode"));

			new XMLAccessTask(getActivity(), valid_url,
					FUNC_GET_PRODUCT_SUB_IMAGE, paramshm, true,
					new CallbackInterface() {
						@SuppressWarnings("static-access")
						@Override
						public void onSuccess(NodeList nl) {
							Log.d("no of productimages", "->" + nl.getLength());
							for (int i = 0; i < nl.getLength(); i++) {
								Element e = (Element) nl.item(i);
								Product product = new Product();

								String prodImg = XMLParser.getValue(e,
										"ProductImage");

								if (prodImg != null && !prodImg.isEmpty()) {
									product.setProductImage(prodImg);
									Log.d("productimage",
											"->"
													+ XMLParser.getValue(e,
													"ProductImage"));
									productSubImagesList.add(product);

								}

							}

							Log.d("productSubImagesList", ""
									+ productSubImagesList.size());

							if (productSubImagesList.size() <= 1) {

								rightArrowImageView
										.setImageDrawable(getResources()
												.getDrawable(
														R.mipmap.arrow_right_disabled));
							} else {
								rightArrowImageView
										.setImageDrawable(getResources()
												.getDrawable(
														R.mipmap.arrow_right_enabled));
							}

							if (productSubImagesList.isEmpty()) {
//								helper.showLongToast(R.string.no_sub_image_for_particular_product);
								showViews(false, R.id.gallery_Layout);

								showViews(false, R.id.switcher);
								showViews(true, R.id.selected_imageview);
								selectedImageView.setImageResource(R.mipmap.no_image);

							} else {

								subImageAdapter = new GalleryImageAdapter(
										getActivity(), productSubImagesList);
								gallery.setAdapter(subImageAdapter);

								showViews(true, R.id.switcher);
								showViews(false, R.id.selected_imageview);

							}
							showViews(true, R.id.image_Layout);
							showViews(true, R.id.detail_layout);
							showViews(false, R.id.load);
						}

						@Override
						public void onFailure(ErrorType error) {
							if (error == ErrorType.NETWORK_UNAVAILABLE) {
								helper.showLongToast(R.string.no_network);
							} else {
//								helper.showLongToast(R.string.no_sub_image_for_particular_product);
							}
							// selectedImageView
							// .setImageResource(R.drawable.no_image_cart);
							showViews(true, R.id.image_Layout);
							showViews(true, R.id.detail_layout);
							showViews(false, R.id.load);
							showViews(false, R.id.gallery_Layout);

							if (productImage != null && !productImage.isEmpty()) {

								showViews(true, R.id.switcher);
								showViews(false, R.id.selected_imageview);

							} else {

								showViews(false, R.id.switcher);
								showViews(true, R.id.selected_imageview);
								selectedImageView.setImageResource(R.mipmap.no_image);
							}

						}

					}).execute();

		} else {
			helper.showLongToast(R.string.invalid_product_code);
		}

	}

	private void loadProductSubImagesOffline() {
		jsonString="";
		if (productCode != null && !productCode.isEmpty()) {

			showViews(false, R.id.image_Layout);
			showViews(false, R.id.detail_layout);
			showViews(true, R.id.load);
			paramshm.clear();
			String locationCode = SalesOrderSetGet.getLocationcode();
			String customerGroupCode = Catalog.getCustomerGroupCode();
			paramshm.put("LocationCode", locationCode);
			paramshm.put("ProductCode", productCode);
			paramshm.put("CategoryCode", "");
			paramshm.put("SubCategoryCode", "");
			paramshm.put("CustomerGroupCode", customerGroupCode);

			Log.d("LocationCode", "=>" + paramshm.get("LocationCode"));
			Log.d("ProductCode", "=>" + paramshm.get("ProductCode"));
			Log.d("CategoryCode", "=>" + paramshm.get("CategoryCode"));
			Log.d("SubCategoryCode", "=>" + paramshm.get("SubCategoryCode"));
			Log.d("CustomerGroupCode", "=>" + paramshm.get("CustomerGroupCode"));

			try {
				String productStr = offlineDatabase.getProductSubImages(paramshm);
				jsonString = " { SODetails : " + productStr	+ "}";
				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");

				int lengthJsonArr = jsonMainNode.length();
				for (int i = 0; i < lengthJsonArr; i++) {

					JSONObject jsonChildNode;

					Product product = new Product();
					String productimage = "";
					jsonChildNode = jsonMainNode.getJSONObject(i);
					productimage = jsonChildNode.optString("ProductImage").toString();

					if(productimage !=null && !productimage.isEmpty()){
						product.setProductImage(productimage);
						Log.d("productimage","->"+productimage);
						productSubImagesList.add(product);

					}
				}

				Log.d("productSubImagesList", ""
						+ productSubImagesList.size());

				if (productSubImagesList.size() <= 1) {

					rightArrowImageView
							.setImageDrawable(getResources()
									.getDrawable(
											R.mipmap.arrow_right_disabled));
				} else {
					rightArrowImageView
							.setImageDrawable(getResources()
									.getDrawable(
											R.mipmap.arrow_right_enabled));
				}

				if (productSubImagesList.isEmpty()) {
//				helper.showLongToast(R.string.no_sub_image_for_particular_product);
					showViews(false, R.id.gallery_Layout);

					showViews(false, R.id.switcher);
					showViews(true, R.id.selected_imageview);
					selectedImageView.setImageResource(R.mipmap.no_image);

				} else {

					subImageAdapter = new GalleryImageAdapter(
							getActivity(), productSubImagesList);
					gallery.setAdapter(subImageAdapter);

					showViews(true, R.id.switcher);
					showViews(false, R.id.selected_imageview);

				}
				showViews(true, R.id.image_Layout);
				showViews(true, R.id.detail_layout);
				showViews(false, R.id.load);

			} catch(Exception e){

//			if (error == ErrorType.NETWORK_UNAVAILABLE) {
//				helper.showLongToast(R.string.no_network);
//			} else {
//				helper.showLongToast(R.string.no_sub_image_for_particular_product);
//			}
				showViews(true, R.id.image_Layout);
				showViews(true, R.id.detail_layout);
				showViews(false, R.id.load);
				showViews(false, R.id.gallery_Layout);

				if (productImage != null && !productImage.isEmpty()) {

					showViews(true, R.id.switcher);
					showViews(false, R.id.selected_imageview);

				} else {

					showViews(false, R.id.switcher);
					showViews(true, R.id.selected_imageview);
					selectedImageView.setImageResource(R.mipmap.no_image);
				}

			}


		} else {
			helper.showLongToast(R.string.invalid_product_code);
		}

	}

	private void stringToBitmap(){
		try {
			byte [] encodeByte=Base64.decode(productImage,Base64.DEFAULT);
			Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
			selectedImageView.setImageBitmap(bitmap);
		} catch(Exception e) {
			e.getMessage();
		}
	}

//	private void stringToBitmap() {
//		if (productImage != null && !productImage.isEmpty()) {
//
//			try {
//				byte[] imageAsBytes = Base64.decode(productImage.getBytes(),
//						Base64.DEFAULT);
//
//				BitmapFactory.Options options = new BitmapFactory.Options();
//				options.inSampleSize = 2;
//				Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0,
//						imageAsBytes.length, options);
//				selectedImageView.setImageBitmap(bmp);
//			} catch (OutOfMemoryError e) {
//				// e.printStackTrace();
//				helper.showErrorDialog(Log.getStackTraceString(e));
//			} catch (Exception e) {
//				// e.printStackTrace();
//				helper.showErrorDialog(Log.getStackTraceString(e));
//			}
//
//		} else {
//			selectedImageView.setImageResource(R.mipmap.no_image);
//		}
//	}

//	private void stringToBitmap() {
//		if (productImage != null && !productImage.isEmpty()) {
//
//			try{
//				byte[] encodeByte = Base64.decode(productImage, Base64.DEFAULT);
//
//			/* There isn't enough memory to open up more than a couple camera photos */
//			/* So pre-scale the target bitmap into which the file is decoded */
//
//			/* Get the size of the ImageView */
//				int targetW = selectedImageView.getWidth();
//				int targetH = selectedImageView.getHeight();
//
//			/* Get the size of the image */
//				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//				bmOptions.inJustDecodeBounds = true;
//				//BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//				Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,encodeByte.length, bmOptions);
//				int photoW = bmOptions.outWidth;
//				int photoH = bmOptions.outHeight;
//
//			/* Figure out which way needs to be reduced less */
//				int scaleFactor = 1;
//				if ((targetW > 0) || (targetH > 0)) {
//					scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//				}
//
//			/* Set bitmap options to scale the image decode target */
//				bmOptions.inJustDecodeBounds = false;
//				bmOptions.inSampleSize = scaleFactor;
//				bmOptions.inPurgeable = true;
//
//			/* Decode the JPEG file into a Bitmap */
//				//	Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//				bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
//						encodeByte.length, bmOptions);
//
////			Bitmap bitmaprst = rotate(bitmap,90);
//
//				selectedImageView.setImageBitmap(bitmap);
//			}catch (OutOfMemoryError e){
//				e.printStackTrace();
//			}
//
//		} else {
//			selectedImageView.setImageResource(R.mipmap.no_image);
//		}
//	}

	@Override
	public void onClick(View v) {
		String pcspercarton = mPcsPerCarton.getText().toString();
		switch (v.getId()) {

		/*case R.id.cart_img:

			 * mIntent.setClass(getActivity(), ShoppingCartActivity.class);
			 * startActivity(mIntent);


			break;*/
			case R.id.add:

				String customercode = Catalog.getCustomerCode();

				if (customercode != null && !customercode.isEmpty()) {

					String productCode = DBCatalog.getProductCodeValue(mProductCode
							.getText().toString());
					String qty = mqty_ed.getText().toString();
					String carton = mcarton_ed.getText().toString();
					if (productCode != null && !productCode.isEmpty()) {
						mAdd.setText("Added");
						mAdd.setBackgroundResource(R.drawable.add_cart_selected);
						mAdd.setTextColor(Color.parseColor("#FFFFFF"));
						Toast.makeText(getActivity(),
								"This Product already added to the cart",
								Toast.LENGTH_SHORT).show();
					} else {

						if ((qty != null && !qty.isEmpty())
								&& (carton != null && !carton.isEmpty())) {

							Log.d("haveAttribute","ss-->"+haveAttribute);
							if(haveAttribute.matches("0")){
								totalCalc();
							}else{
								String haveAttributeFlag = SalesOrderSetGet.getHaveAttribute();
								if(haveAttributeFlag!=null && !haveAttributeFlag.isEmpty() ) {
									if (haveAttributeFlag.matches("2")) {
										String prodCode = mProductCode.getText().toString();
										String prodName = mProductName.getText().toString();
										new GetProductAttribute(prodCode,prodName).execute();
									} else {
										totalCalc();
									}
								}else{
									totalCalc();
								}


							}
							//totalCalc();

						} else {
							Toast.makeText(getActivity(), "Please Enter the Value",
									Toast.LENGTH_SHORT).show();
						}
					}

				} else {
					viewpage.setCurrentItem(0);
				}

				break;
			case R.id.left_arrow_imageview:

				if (selectedImagePosition > 0) {
					--selectedImagePosition;

				}
				gallery.setSelection(selectedImagePosition, false);

				break;
			case R.id.right_arrow_imageview:

				if (selectedImagePosition < productSubImagesList.size() - 1) {
					++selectedImagePosition;
				}
				gallery.setSelection(selectedImagePosition, false);

				break;

			case R.id.qty_plus:
				// mcarton_ed.removeTextChangedListener(clqtywatcher);
				// mloose_ed.removeTextChangedListener(clqtywatcher);

				String qtyPlus = mqty_ed.getText().toString();
				if (qtyPlus != null && !qtyPlus.isEmpty()) {
					qtyFlag = Double.valueOf(qtyPlus);
				} else {
					qtyFlag = 0;
				}

				mqty_ed.setText(String.valueOf(++qtyFlag));
				mqty_ed.setSelection(mqty_ed.length());

				break;

			case R.id.qty_minus:

				String qtyMinus = mqty_ed.getText().toString();
				if (qtyMinus != null && !qtyMinus.isEmpty()) {
					qtyFlag = Double.valueOf(qtyMinus);
					if (qtyFlag > 1) {
						mqty_ed.setText(String.valueOf(--qtyFlag));
					} else {
						mqty_ed.setText("1");
					}
					mqty_ed.setSelection(mqty_ed.length());

				}

				break;
			case R.id.cqty_plus:

				// mqty_ed.removeTextChangedListener(qtywatcher);
				if (pcspercarton != null && !pcspercarton.isEmpty()) {
					if (Double.valueOf(pcspercarton) > 1) {

						String cQtyPlus = mcarton_ed.getText().toString();
						if (cQtyPlus != null && !cQtyPlus.isEmpty()) {
							cartonFlag = Double.valueOf(cQtyPlus);
						} else {
							cartonFlag = 0;
						}
						mcarton_ed.setText(String.valueOf(++cartonFlag));
						mcarton_ed.setSelection(mcarton_ed.length());
						// mqty_ed.addTextChangedListener(qtywatcher);
						// qtyCalc();
					}
				}

				break;

			case R.id.cqty_minus:
				// mqty_ed.removeTextChangedListener(qtywatcher);
				if (pcspercarton != null && !pcspercarton.isEmpty()) {
					if (Double.valueOf(pcspercarton) > 1) {
						String cQtyMinus = mcarton_ed.getText().toString();
						if (cQtyMinus != null && !cQtyMinus.isEmpty()) {
							cartonFlag = Double.valueOf(cQtyMinus);
							if (cartonFlag > 1) {
								mcarton_ed.setText(String.valueOf(--cartonFlag));
							} else {
								mcarton_ed.setText("1");
							}
							mcarton_ed.setSelection(mcarton_ed.length());
						}

					}
				}

				break;
			case R.id.lqty_plus:
				// mqty_ed.removeTextChangedListener(qtywatcher);
				if (pcspercarton != null && !pcspercarton.isEmpty()) {
					if (Double.valueOf(pcspercarton) > 1) {
						String lQtyPlus = mloose_ed.getText().toString();
						if (lQtyPlus != null && !lQtyPlus.isEmpty()) {
							looseFlag = Double.valueOf(lQtyPlus);
						} else {
							looseFlag = 0;
						}
						mloose_ed.setText(String.valueOf(++looseFlag));
						mloose_ed.setSelection(mloose_ed.length());
						// mqty_ed.addTextChangedListener(qtywatcher);

						// qtyCalc();
					}
				}

				break;
			case R.id.lqty_minus:
				// mqty_ed.removeTextChangedListener(qtywatcher);
				if (pcspercarton != null && !pcspercarton.isEmpty()) {
					if (Double.valueOf(pcspercarton) > 1) {
						String lQtyMinus = mloose_ed.getText().toString();
						if (lQtyMinus != null && !lQtyMinus.isEmpty()) {
							looseFlag = Double.valueOf(lQtyMinus);
							if (looseFlag > 0) {
								mloose_ed.setText(String.valueOf(--looseFlag));
							} else {
								mloose_ed.setText("0");
							}
							mloose_ed.setSelection(mloose_ed.length());
							// mqty_ed.addTextChangedListener(qtywatcher);
						}

						// qtyCalc();
					}
				}
				// mcarton_ed.removeTextChangedListener(clqtywatcher);
				// mloose_ed.removeTextChangedListener(clqtywatcher);
				// mqty_ed.removeTextChangedListener(qtywatcher);
				break;
			case R.id.fqty_plus:
				String focQtyPlus = mfoc_ed.getText().toString();
				if (focQtyPlus != null && !focQtyPlus.isEmpty()) {
					focQtyFlag = Integer.valueOf(focQtyPlus);
				} else {
					focQtyFlag = 0;
				}
				mfoc_ed.setText(String.valueOf(++focQtyFlag));
				mfoc_ed.setSelection(mfoc_ed.length());


				break;
			case R.id.fqty_minus:

				String focQtyMinus = mfoc_ed.getText().toString();
				if (focQtyMinus != null && !focQtyMinus.isEmpty()) {
					focQtyFlag = Integer.valueOf(focQtyMinus);
					if (focQtyFlag > 0) {
						mfoc_ed.setText(String.valueOf(--focQtyFlag));
					} else {
						mfoc_ed.setText("0");
					}
					mfoc_ed.setSelection(mfoc_ed.length());
				}


				break;
			case R.id.exqty_plus:
				String exQtyPlus = mexchange_ed.getText().toString();
				if (exQtyPlus != null && !exQtyPlus.isEmpty()) {
					exQtyFlag = Integer.valueOf(exQtyPlus);
				} else {
					exQtyFlag = 0;
				}
				mexchange_ed.setText(String.valueOf(++exQtyFlag));
				mexchange_ed.setSelection(mexchange_ed.length());
				break;
			case R.id.exqty_minus:
				String exQtyMinus = mexchange_ed.getText().toString();
				if (exQtyMinus != null && !exQtyMinus.isEmpty()) {
					exQtyFlag = Integer.valueOf(exQtyMinus);
					if (exQtyFlag > 0) {
						mexchange_ed.setText(String.valueOf(--exQtyFlag));
					} else {
						mexchange_ed.setText("0");
					}
					mexchange_ed.setSelection(mexchange_ed.length());
				}

				break;
			default:
				break;
		}
	}



	private class GetProductAttribute extends AsyncTask<String, String, String> {
		private ArrayList<HashMap<String, String>> colorArr;
		private View v;
		private String result = "",productCode = "",productName="";
		private boolean isAddBtn;
		public GetProductAttribute(String productCode,String productName){
			mProductAttributeArr.clear();
			this.v = v;
			this.isAddBtn = isAddBtn;
			this.productCode = productCode;
			this.productName = productName;
			this.colorArr = new ArrayList<HashMap<String, String>>();
		}
		@Override
		protected void onPreExecute() {
			//helper.showProgressView(mCatalogLayout);
		}
		@Override
		protected String doInBackground(String... params) {
			colorArr = SalesProductWebService.getProductAttribute(
					productCode, "fncGetProductAttribute");
			return result;
		}


		@Override
		protected void onPostExecute(String result) {
			//	helper.dismissProgressView(mCatalogLayout);
			if(colorArr.size()>0){
				ColorAttributeDialog productModifierDialog = new ColorAttributeDialog();
				productModifierDialog.initiatePopupWindow(
						getActivity(),productCode,productName, colorArr,null,0);
				productModifierDialog.show(getActivity().getFragmentManager(), "dialog");

				productModifierDialog.setOnCompletionListener(new ColorAttributeDialog.OnCompletionListener() {
					@Override
					public void onCompleted(String qty,ArrayList<Attribute> attributeArr) {
						//attributeQtyCalc(v,qty,isAddBtn,attributeArr);
						mqty_ed.setText(qty);
						mProductAttributeArr = attributeArr;
						totalCalc();
					}
				});
			}

		}
	}
	private void changeBorderForSelectedImage(int selectedItemPos) {

		int count = gallery.getChildCount();

		for (int i = 0; i < count; i++) {

			ImageView imageView = (ImageView) gallery.getChildAt(i);
			// imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.image_border));
			imageView.setPadding(1, 1, 1, 1);

		}

		ImageView imageView = (ImageView) gallery.getSelectedView();
		// imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.selected_image_border));
		imageView.setPadding(1, 1, 1, 1);
	}

	private void setSelectedImage(int selectedImagePosition) {

		Product product = productSubImagesList.get(selectedImagePosition);
		String productSubImages = product.getProductImage();
		if (productSubImages != null && !productSubImages.isEmpty()) {
			try {
				byte[] imageAsBytes = Base64.decode(
						productSubImages.getBytes(), Base64.DEFAULT);
				// holder.productImage.setImageBitmap(
				// BitmapFactory.decodeByteArray(imageAsBytes, 0,
				// imageAsBytes.length));

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0,
						imageAsBytes.length, options);
				// selectedImageView.setImageBitmap(bmp);
				// selectedImageView.setScaleType(ScaleType.FIT_XY);

				Drawable drawable = new BitmapDrawable(bmp);
				Switcher.setImageDrawable(drawable);
				Switcher.setId(0); // change
				subImageAdapter.notifyDataSetChanged();

			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		} else {
			// selectedImageView.setImageResource(R.drawable.no_image_cart);
		}

	}

	TextWatcher clqtywatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
								  int count) {
			qtyCalc();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	TextWatcher qtywatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
								  int count) {

			qtyReverseCalc();

			if (mqty_ed.length() == 0) {

				mcarton_ed.removeTextChangedListener(clqtywatcher);
				mcarton_ed.setText("");
				mcarton_ed.addTextChangedListener(clqtywatcher);

				mloose_ed.removeTextChangedListener(clqtywatcher);
				mloose_ed.setText("");
				mloose_ed.addTextChangedListener(clqtywatcher);

			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	private void qtyCalc() {
		if (!mProductCode.getText().toString().equals("")) {
			double cqty = 0, lqty = 0, pcs = 0, qty;

			String cartonQty = mcarton_ed.getText().toString();
			String looseQty = mloose_ed.getText().toString();
			String pcsprcarton = mPcsPerCarton.getText().toString();

			mqty_ed.removeTextChangedListener(qtywatcher);
			try {
				if (cartonQty != null && !cartonQty.isEmpty()) {
					cqty = Double.valueOf(cartonQty);
				}
				if (looseQty != null && !looseQty.isEmpty()) {
					lqty = Double.valueOf(looseQty);

				}
				if (pcsprcarton != null && !pcsprcarton.isEmpty()) {
					pcs = Double.valueOf(pcsprcarton);
				}

				if (lqty > 0) {

					qty = cqty * pcs + lqty;
				} else {
					qty = cqty * pcs;

				}

				String q = twoDecimalPoint(qty);

				Log.d("qty", "->" + q);
				mqty_ed.setText(String.valueOf(q));
				mqty_ed.setSelection(mqty_ed.length());

			} catch (Exception e) {
				e.printStackTrace();
			} catch (StackOverflowError e) {
				e.printStackTrace();
			}
			mqty_ed.addTextChangedListener(qtywatcher);
		}
	}

	private void qtyReverseCalc() {
		if (!mProductCode.getText().toString().equals("")) {
			double cqty = 0, lqty = 0, pcs = 1, qty = 0;

			String pcsprcarton = mPcsPerCarton.getText().toString();
			String quantity = mqty_ed.getText().toString();

			mcarton_ed.removeTextChangedListener(clqtywatcher);
			mloose_ed.removeTextChangedListener(clqtywatcher);

			try {

				if (pcsprcarton != null && !pcsprcarton.isEmpty()
						&& !pcsprcarton.equals("0")) {

					pcs = Double.valueOf(pcsprcarton);
				}
				if (quantity != null && !quantity.isEmpty()) {
					qty = Double.valueOf(quantity);

					if (qty > 0) {
						cqty = (int) (qty / pcs);
						lqty = qty % pcs;

						String ctn = twoDecimalPoint(cqty);
						String loose = twoDecimalPoint(lqty);

						mcarton_ed.setText("" + ctn);
						mloose_ed.setText("" + loose);

					}
				}
			} catch (ArithmeticException e) {
				e.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();
			} catch (StackOverflowError e) {
				e.printStackTrace();
			}
			mcarton_ed.addTextChangedListener(clqtywatcher);
			mloose_ed.addTextChangedListener(clqtywatcher);
		}
	}

	// TotalCalc Calculation
	private void totalCalc() {

		double dTotal = 0.00, dQty = 0.00, dCQty = 0.00, dLQty = 0.00, dPrice = 0.00, dCPrice = 0.00;
		String quantity = mqty_ed.getText().toString();
		String cqty = mcarton_ed.getText().toString();
		String lqty = mloose_ed.getText().toString();
		String wholesaleprice = mLPrice.getText().toString();
		String cPrice = mCprice.getText().toString();

		try {
			if (priceflag.matches("1")) {

				if (wholesaleprice.matches("")) {
					wholesaleprice = "0";
				}

				if (cPrice.matches("")) {
					cPrice = "0";
				}

				if (cqty.matches("")) {
					cqty = "0";
				}

				if (lqty.matches("")) {
					lqty = "0";
				}

				dPrice = Double.parseDouble(wholesaleprice);
				dCPrice = Double.parseDouble(cPrice);

				dCQty = Double.parseDouble(cqty);
				dLQty = Double.parseDouble(lqty);

				dTotal = (dCQty * dCPrice) + (dLQty * dPrice);

			} else {
				if ((wholesaleprice != null && !wholesaleprice.isEmpty())
						&& (quantity != null && !quantity.isEmpty())) {
					dQty = Double.valueOf(quantity);
					dPrice = Double.valueOf(wholesaleprice);
					Log.d("dQty", "" + dQty);
					Log.d("dPrice", "" + dPrice);
					if ((dQty > 0) && (0 < dPrice)) {

						dTotal = (dQty * dPrice);

						Log.d("dTotal", "" + dTotal);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		taxCalc(dTotal);
	}

	public void animCartIcon() {
		int cartSize = dbhelper.getCount();
		if (cartSize > 0) {
			SOCatalogActivity.mCartText.setVisibility(View.VISIBLE);

			SOCatalogActivity.mCartText.setText(cartSize + "");
		} else {
			SOCatalogActivity.mCartText.setVisibility(View.GONE);
			SOCatalogActivity.mCartText.setText("");
		}
	}

	public void getProductCode() {
		Log.d("mProductCode", "on resume   "+mProductCode.getText().toString());
		String productCode = DBCatalog.getProductCodeValue(mProductCode
				.getText().toString());
		if (productCode != null && !productCode.isEmpty()) {
			mAdd.setText("Added");
			mAdd.setBackgroundResource(R.drawable.add_cart_selected);
			mAdd.setTextColor(Color.parseColor("#FFFFFF"));
		} else {
			mAdd.setText("Add");
			mAdd.setBackgroundResource(R.drawable.add_cart);
			mAdd.setTextColor(Color.parseColor("#000000"));
		}
	}

	public void taxCalc(double dTotal) {

		double dSubTotal = 0.00, dTaxValue = 0.00, dTaxAmount = 0.00, dNetTotal = 0.00;
		String total = "0.00", tax = "0.000", netTotal = "0.00", subTotal = "0.00";
		String taxType = SalesOrderSetGet.getCompanytax();
		String taxValue = SalesOrderSetGet.getTaxValue();

		if (taxType != null && !taxType.isEmpty()) {
			// taxType = companyDetails.get(COLUMN_TAXTYPE);
		} else {
			taxType = "Z";
		}
		if (taxValue != null && !taxValue.isEmpty()) {
			// taxValue = companyDetails.get(COLUMN_TAXVALUE);
		} else {
			taxValue = "0.0";
		}

		Log.d("taxType", taxType);
		Log.d("taxValue", taxValue);

		try {

			dSubTotal = dTotal;

			// sl_total.setText("" + sbTtl);

			if ((taxType != null && !taxType.isEmpty())
					&& (taxValue != null && !taxValue.isEmpty())) {

				dTaxValue = Double.parseDouble(taxValue);

				if (taxType.matches("E")) {

					dTaxAmount = (dTotal * dTaxValue) / 100;
					tax = fourDecimalPoint(dTaxAmount);
					// sl_tax.setText("" + prodTax);

					dNetTotal = dTotal + dTaxAmount;
					netTotal = twoDecimalPoint(dNetTotal);
					// sl_netTotal.setText("" + ProdNetTotal);
					// }

				} else if (taxType.matches("I")) {

					dTaxAmount = (dTotal * dTaxValue) / (100 + dTaxValue);
					tax = fourDecimalPoint(dTaxAmount);
					// sl_tax.setText("" + prodTax);

					// dNetTotal = dTotal + dTaxAmount;
					dNetTotal = dTotal;
					netTotal = twoDecimalPoint(dNetTotal);
					// sl_netTotal.setText("" + ProdNetTotal);
					// }

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

		Log.d("total", "" + total);
		Log.d("subTotal", "" + subTotal);
		Log.d("taxType", "" + taxType);
		Log.d("taxValue", "" + taxValue);
		Log.d("tax", "" + tax);
		Log.d("netTotal", "" + netTotal);

		int slNo = dbhelper.getCount();

		String sno = String.valueOf(++slNo);
		productvalues.put(COLUMN_PRODUCT_SLNO, sno);
		productvalues.put(COLUMN_PRODUCT_CODE, mProductCode.getText()
				.toString());
		productvalues.put(COLUMN_PRODUCT_NAME, mProductName.getText()
				.toString());
		String carton = mcarton_ed.getText().toString();
		if(carton!=null && !carton.isEmpty()){
			//if Action
		}else{
			carton ="0";
		}
		String loose = mloose_ed.getText().toString();
		if(loose!=null && !loose.isEmpty()){
			//if Action
		}else{
			loose ="0";
		}
		String qty = mqty_ed.getText().toString();
		if(qty!=null && !qty.isEmpty()){
			//if Action
		}else{
			qty = "0";
		}
		String foc = mfoc_ed.getText().toString();
		if(foc!=null && !foc.isEmpty()){
			//if Action
		}else{
			foc = "0";
		}
		String exchange = mexchange_ed.getText().toString();
		if(exchange!=null && !exchange.isEmpty()){
			//if Action
		}else{
			exchange = "0";
		}
		String lPrice = mLPrice.getText().toString();
		if(lPrice!=null && !lPrice.isEmpty()){
			//if Action
		}else{
			lPrice = "0";
		}

		String cprice = mCprice.getText().toString();
		if(cprice!=null && !cprice.isEmpty()){
			//if Action
		}else{
			cprice = "0";
		}

		productvalues.put(COLUMN_CARTON_QTY, carton);
		productvalues.put(COLUMN_LOOSE_QTY, loose);
		productvalues.put(COLUMN_QUANTITY, qty);
		productvalues.put(COLUMN_PRICE, lPrice);
		productvalues.put(COLUMN_PRICE, mLPrice.getText().toString());
		productvalues.put(COLUMN_WHOLESALEPRICE, wholesalePrice);
		productvalues.put(COLUMN_UOMCODE, uomCode);
		productvalues.put(COLUMN_PCSPERCARTON, mPcsPerCarton.getText()
				.toString());
		productvalues.put(COLUMN_TAXTYPE, taxType);
		productvalues.put(COLUMN_TAXVALUE, taxValue);
		productvalues.put(COLUMN_TAX, tax);
		productvalues.put(COLUMN_TOTAL, total);
		productvalues.put(COLUMN_SUB_TOTAL, subTotal);
		productvalues.put(COLUMN_NETTOTAL, netTotal);
		productvalues.put(COLUMN_PRODUCT_IMAGE, productImage);

		productvalues.put(COLUMN_FOC, mfoc_ed.getText().toString());
		productvalues.put(COLUMN_ITEM_DISCOUNT, "0");
		productvalues.put(COLUMN_CARTONPRICE, cprice);
		productvalues.put(COLUMN_EXCHANGEQTY, exchange);

		// productvalues.put(COLUMN_PRODUCT_SLNO, categoryCode);
		// productvalues.put(COLUMN_PRODUCT_SLNO, subCategoryCode);
		DBCatalog.storeCatalog(productvalues);
		if(mProductAttributeArr.size()>0){
			SOTDatabase.storeAttribute(sno,mProductCode.getText()
					.toString(), mProductName.getText()
					.toString(),mProductAttributeArr);
		}
		// Log.d("id", ""+id);
		ObjectAnimator anim = ObjectAnimator.ofFloat(SOCatalogActivity.mCartText, "rotationY",
				0.0f, 360.0f);
		anim.setDuration(1000);
		anim.start();
		animCartIcon();
		mAdd.setText("Added");
//		helper.showLongToast(R.string.added);
		mAdd.setBackgroundResource(R.drawable.add_cart_selected);
		mAdd.setTextColor(Color.parseColor("#FFFFFF"));


	}

	public void showViews(boolean show, int... resId) {
		int visibility = show ? View.VISIBLE : View.GONE;
		for (int id : resId) {
			view.findViewById(id).setVisibility(visibility);
		}
	}

	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tot = df.format(d);

		return tot;
	}

	public String fourDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setMinimumFractionDigits(4);
		String tot = df.format(d);

		return tot;
	}

	@Override
	public void onResume() {
		dbhelper = new DBCatalog(getActivity());
		animCartIcon();
		getProductCode();
		super.onResume();
	}

}
