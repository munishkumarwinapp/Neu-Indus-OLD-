package com.winapp.sot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.XMLParser;
import com.winapp.model.Customer;
import com.winapp.model.Product;
import com.winapp.printer.UIHelper;
import com.winapp.util.HorizontalListView;
import com.winapp.util.XMLAccessTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MerchandiseBrandPhotoScreen extends SherlockFragmentActivity implements
        SlideMenuFragment.MenuClickInterFace, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final int TAKE_PICTURE_BEFORE_JOB = 1;
    private static final int TAKE_PICTURE_AFTER_JOB = 2;
    private static final int TAKE_REFERENCE_PICTURE = 3;
    private static final int TAKE_SIGNATURE_IMAGE = 4;
    private static final int TAKE_PICTURE_POSM = 5;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private ActionBar mActionBar;
    private View customActionBarView;
    private TextView mOutLetNameTxtv,mCustomerCodeTxtv,mTitle, mSign, mTakeBeforePicTxtv, mTakeAfterPicTxtv,
            mTakeReferenceImageTxtv, mAddress1Txtv,
            mDeliveryCodeTxtv,
            mAddress2Txtv, mAddress3Txtv, mAttentionTxtv, mPhoneNoTxtv,
            mHandphoneNoTxtv,mCustomerNameTxtv;
    private ImageButton mPrinterImgBtn, mCompleteImgBtn, mHoldImgBtn;
    private SlidingMenu menu;
    private ImageView mAdditionalRemarks,mSaveReferenceImg, mSaveAfterTakePicImg,
            mSaveBeforeTakePicImg, mSignature, mAddSignature, mAddBeforePic,
            mAddAfterPic, mAddReferencePic,maddposm, mTakeBeforePic, mTakeAfterPic,
            mTakeReferencePic, mClearSignatureImage, mSaveSignatureImage;

    private Bundle mBundle;
    private String remarks = "", mSaveMerchandiseStartJsonString = "",selectedStatus="",
            mGetCustomerAddressJsonString = "", mDetailFlag = "",
            mSaveMerchandiseImageDeleteJsonString = "",
            mSaveMerchandiseHoldOrCompleteJsonString = "", mResult = "",
            mSaveMerchandiseImagesJsonString, mUerIdStr, mValidUrl, mVanStr,
            mDeviceId, mCompanyCodeStr, mCustomerJsonString,mStatuJsonString,mProductJsonString,
            mCustomerCode = "", mCustomerName = "", mOutletName = "",
            mDeliveryCode = "", mStatus = "", mRefNo = "", mRefDate = "",
            mRemarks1 = "", mRemarks2 = "", mAddress1, address2, mAddress3,
            mAttention, mPhoneNo, mHandphoneNo, mFlag = "",mCurrentPhotoPath="";
    private EditText mRemark1Edt, mRemark2Edt;
    private Cursor mCursor;

    private int slNo = 0, mTextlength = 0;
    private HorizontalListView mBeforeTakePicListView, mAfterTakePicListView,
            mReferenceTakePicListView,horizontalListViewposm;
    private ArrayList<Integer> mSNo;
    private MerchandiseImageAdapter mMerchandiseImageAdapter;
    private Intent mIntent;
    private CustomAlertAdapterSupp mCustomerAdapter;
    private ArrayList<Product> mBeforePicArrList, mAfterPicArrList,
            mReferencePicArr,mPosmPicArr,mSignPicArr,mBrandlistArr;
    private ArrayList<HashMap<String, String>> customerArrHm,
            customerSearchArrHm, customerDataArrHm;
    private ArrayList<String> brandnamelist;
    private AlertDialog mCustomerDialog = null;
    private JSONObject mGetCustomerAddressJsonObject,
            mSaveMerchandiseImageDeleteJsonObject,
            mSaveMerchandiseHoldOrCompleteJsonObject, mCustomerJSONObject,mStatuJSONObject,mProductJSONObject,
            mSaveMerchandiseImagesJsonObject, mSaveMerchandiseStartJsonObject;

    private JSONArray mGetCustomerAddressJsonArray,
            mSaveMerchandiseImageDeleteJsonArray,
            mSaveMerchandiseHoldOrCompleteJsonArray, mCustomerJSONArray,mStatuJSONArray,mProductJSONArray,
            mSaveMerchandiseStartJsonArray, mSaveMerchandiseImagesJsonArray;
    private HashMap<String, String> mHashMap;
    private UIHelper helper;
   // private ScrollView mPictureScrollView;
    private Spinner mMerchandiseStatus;
    private Button mBrandBtnTab, mPictureBtnTab;
    private LinearLayout mSignLayout,brand_layout,picture_layout;
    private RecyclerView brand_lv;
    private FrameLayout mDetailLayout ;
    //	private LocationManager locationManager;
    private boolean isGPSEnabled = false, isNetworkEnabled = false,isBundleValue = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private double mLatitude, mLongitude;
    private ArrayList<Customer> mCustomer;
    private ArrayList<SO> mStatusValue;
    private AlertDialog myalertDialog = null;
    private ImageView mImageView;
//	private GPSTracker gps;// GPSTracker class

    // get location
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;
    private LocationManager locationManager;
    double setLatitude, setLongitude;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private ImageView close_ic;
    private LinearLayout zoomLayout;
    private LinearLayout imagelayout,posm_layout;
    private SubsamplingScaleImageView expandedImageView ;
    private BrandListAdapter recyclerAdapter;
    private TextView back_btn,next_btn;
    private TextView brandname,customerscreen,merchandise_screen,balancestock_screen,placeorder_screen;
    private CheckBox checkposm;
    int i = 0;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_menu);
        setContentView(R.layout.activity_merchandise_brand_photo_screen);
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
        mTitle.setText("Merchandise");

        // Custom ActionBarView ID
        mPrinterImgBtn = (ImageButton) customActionBarView
                .findViewById(R.id.printer);
        mCompleteImgBtn = (ImageButton) customActionBarView
                .findViewById(R.id.custcode_img);
        mHoldImgBtn = (ImageButton) customActionBarView
                .findViewById(R.id.search_img);
        mPrinterImgBtn.setVisibility(View.GONE);
        mHoldImgBtn.setVisibility(View.INVISIBLE);
        mCompleteImgBtn.setVisibility(View.INVISIBLE);

        mHoldImgBtn.setImageResource(R.mipmap.ic_hold);
        mCompleteImgBtn.setImageResource(R.mipmap.ic_complete);
        // Set customActionView
        mActionBar.setCustomView(customActionBarView);

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

        // object Initialization
        helper = new UIHelper(MerchandiseBrandPhotoScreen.this);
        mIntent = new Intent();
        mHashMap = new HashMap<String, String>();
        mBeforePicArrList = new ArrayList<Product>();
        mAfterPicArrList = new ArrayList<Product>();
        mReferencePicArr = new ArrayList<Product>();
        mPosmPicArr = new ArrayList<>();
        mSignPicArr  = new ArrayList<Product>();
        mBrandlistArr = new ArrayList<>();
        mSNo = new ArrayList<Integer>();
        brandnamelist = new ArrayList<>();
        customerDataArrHm = new ArrayList<HashMap<String, String>>();
        customerArrHm = new ArrayList<HashMap<String, String>>();
        customerSearchArrHm = new ArrayList<HashMap<String, String>>();
        mCustomer = new ArrayList<Customer>();
//		gps = new GPSTracker(MerchandiseBrandPhotoScreen.this);
        mStatusValue = new ArrayList<SO>();
        // DB Init
        FWMSSettingsDatabase.init(MerchandiseBrandPhotoScreen.this);
        SOTDatabase.init(MerchandiseBrandPhotoScreen.this);

        // View ID
        // Textview
        mSign = (TextView) findViewById(R.id.addSign);
        mTakeBeforePicTxtv = (TextView) findViewById(R.id.takeBeforePicLbl);
        mTakeAfterPicTxtv = (TextView) findViewById(R.id.takeAfterPicLbl);
        mTakeReferenceImageTxtv = (TextView) findViewById(R.id.takeReferenceImageLbl);

        mAdditionalRemarks = (ImageView) findViewById(R.id.additionalRemarks);
        expandedImageView = (SubsamplingScaleImageView) findViewById(
                R.id.expanded_image);

        // mDeliveryAddressEdt = (EditText) findViewById(R.id.deliveryAddress);

        mAddress1Txtv = (TextView) findViewById(R.id.address1);
        mAddress2Txtv = (TextView) findViewById(R.id.address2);
        mAddress3Txtv = (TextView) findViewById(R.id.address3);
        mAttentionTxtv = (TextView) findViewById(R.id.attention);
        mPhoneNoTxtv = (TextView) findViewById(R.id.phoneNo);
        mHandphoneNoTxtv = (TextView) findViewById(R.id.handphoneNo);
        // EditText
        mRemark1Edt = (EditText) findViewById(R.id.remarks1);
        mRemark2Edt = (EditText) findViewById(R.id.remarks2);
        mOutLetNameTxtv = (TextView) findViewById(R.id.outLetName);
        mDeliveryCodeTxtv = (TextView) findViewById(R.id.deliveryCode);
        mCustomerCodeTxtv = (TextView) findViewById(R.id.customerCode);
        mCustomerNameTxtv = (TextView) findViewById(R.id.customerName);

        // ImageView
        mTakeBeforePic = (ImageView) findViewById(R.id.takeBeforePic);
        mTakeAfterPic = (ImageView) findViewById(R.id.takeAfterPic);
        mTakeReferencePic = (ImageView) findViewById(R.id.takeReferenceImage);
        mClearSignatureImage = (ImageView) findViewById(R.id.clearSignatureImage);
        mSaveSignatureImage = (ImageView) findViewById(R.id.saveSignatureImage);
        mAddBeforePic = (ImageView) findViewById(R.id.addBeforePic);
        mAddAfterPic = (ImageView) findViewById(R.id.addAfterPic);
        mAddReferencePic = (ImageView) findViewById(R.id.addReferencePic);
        maddposm = (ImageView)findViewById(R.id.addposm);
        mAddSignature = (ImageView) findViewById(R.id.addSignature);
        mSignature = (ImageView) findViewById(R.id.signature);
        mSaveBeforeTakePicImg = (ImageView) findViewById(R.id.saveBeforeImage);
        mSaveAfterTakePicImg = (ImageView) findViewById(R.id.saveAfterImage);
        mSaveReferenceImg = (ImageView) findViewById(R.id.saveReferenceImage);
        close_ic = (ImageView) findViewById(R.id.close_ic);
        back_btn = (TextView) findViewById(R.id.back_btn);
        next_btn = (TextView)findViewById(R.id.next_btn);
        brandname = (TextView)findViewById(R.id.brandname);
        zoomLayout = (LinearLayout) findViewById(R.id.zoomLayout);
        imagelayout = (LinearLayout) findViewById(R.id.imagelayout);
        posm_layout = (LinearLayout)findViewById(R.id.posm_layout);
        checkposm = (CheckBox)findViewById(R.id.checkposm);

        // HorizontalListView
        mBeforeTakePicListView = (HorizontalListView) findViewById(R.id.horizontalListViewTakeBeforePic);
        mAfterTakePicListView = (HorizontalListView) findViewById(R.id.horizontalListViewTakeAfterPic);
        mReferenceTakePicListView = (HorizontalListView) findViewById(R.id.horizontalListViewReferenceImage);
        horizontalListViewposm = (HorizontalListView) findViewById(R.id.horizontalListViewposm);

        customerscreen = (TextView)findViewById(R.id.customerscreen);
        merchandise_screen = (TextView) findViewById(R.id.merchandise_btn);
        balancestock_screen = (TextView) findViewById(R.id.balance_stock);
        placeorder_screen = (TextView) findViewById(R.id.placeorder_btn);

        merchandise_screen.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.merchandise_icenable,0,0);
        merchandise_screen.setTextColor(Color.parseColor("#ffffff"));
        merchandise_screen.setBackgroundResource(R.drawable.bottomtab_select);

        // Button
        mBrandBtnTab = (Button) findViewById(R.id.brandBtnTab);
        mPictureBtnTab = (Button) findViewById(R.id.pictureBtnTab);

        // ScrollView
        brand_layout = (LinearLayout)findViewById(R.id.brand_layout);
       // mPictureScrollView = (ScrollView) findViewById(R.id.pictueScrollView);
        picture_layout = (LinearLayout)findViewById(R.id.picture_layout);
        brand_lv = (RecyclerView)findViewById(R.id.brand_lv);

        brand_lv.setLayoutManager(new LinearLayoutManager(MerchandiseBrandPhotoScreen.this));

        // LinearLayout
        mDetailLayout = (FrameLayout) findViewById(R.id.detailLayout);
        mSignLayout = (LinearLayout) findViewById(R.id.signLayout);

        // Spinner
        mMerchandiseStatus = (Spinner) findViewById(R.id.merchandiseStatus);

        // onClickListener
        mAddBeforePic.setOnClickListener(this);
        mAddAfterPic.setOnClickListener(this);
        mAddReferencePic.setOnClickListener(this);
        maddposm.setOnClickListener(this);
        mAddSignature.setOnClickListener(this);
        mSaveBeforeTakePicImg.setOnClickListener(this);
        mSaveAfterTakePicImg.setOnClickListener(this);
        mSaveReferenceImg.setOnClickListener(this);
        mTakeBeforePic.setOnClickListener(this);
        mTakeAfterPic.setOnClickListener(this);
        mTakeReferencePic.setOnClickListener(this);
        mClearSignatureImage.setOnClickListener(this);
        mSaveSignatureImage.setOnClickListener(this);
        mBrandBtnTab.setOnClickListener(this);
        mPictureBtnTab.setOnClickListener(this);
        mHoldImgBtn.setOnClickListener(this);
        mCompleteImgBtn.setOnClickListener(this);
        mSign.setOnClickListener(this);
        mAdditionalRemarks.setOnClickListener(this);
        mTakeBeforePicTxtv.setOnClickListener(this);
        mTakeAfterPicTxtv.setOnClickListener(this);
        mTakeReferenceImageTxtv.setOnClickListener(this);
        zoomLayout.setOnClickListener(this);
        customerscreen.setOnClickListener(this);
        merchandise_screen.setOnClickListener(this);
        balancestock_screen.setOnClickListener(this);
        placeorder_screen.setOnClickListener(this);

        // Get URL from DB
        mValidUrl = FWMSSettingsDatabase.getUrl();
        new SalesOrderWebService(mValidUrl);

        // Get CompanyCode from Pojo class
        mCompanyCodeStr = SupplierSetterGetter.getCompanyCode();

        // Get UserId from Pojo class
        mUerIdStr = SupplierSetterGetter.getUsername();

        // Get VanCode from DB
        mVanStr = SOTDatabase.getVandriver();

        // Get DeviceId from Pojo class
        mDeviceId = RowItem.getDeviceID();

        mBundle = getIntent().getExtras();

        mImageView = (ImageView) findViewById(R.id.imageView3);

        // Getting LocationManager object
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        buildGoogleApiClient();

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        // Bundle
        if (mBundle != null) {
            @SuppressWarnings("unchecked")
            HashMap<String, String> hashMap = (HashMap<String, String>) mBundle
                    .getSerializable("HashMap");

            mCustomerCode = hashMap.get("CustomerCode");
            mCustomerName = hashMap.get("CustomerName");
            mOutletName = hashMap.get("OutletName");
            mDeliveryCode = hashMap.get("DeliveryCode");
            mStatus = hashMap.get("Status");
            mRefNo = hashMap.get("RefNo");
            mRefDate = hashMap.get("RefDate");
            mFlag = hashMap.get("Flag");
            mRemarks1 = hashMap.get("Remarks1");
            mRemarks2 = hashMap.get("Remarks2");
            mAddress1 = hashMap.get("Address1");
            address2 = hashMap.get("Address2");
            mAddress3 = hashMap.get("Address3");
            mAttention = hashMap.get("Attention");
            mPhoneNo = hashMap.get("PhoneNo");
            mHandphoneNo = hashMap.get("HandphoneNo");

            mOutLetNameTxtv.setText(mOutletName);
            mRemark1Edt.setText(mRemarks1);
            mRemark2Edt.setText(mRemarks2);
            mCustomerCodeTxtv.setText(mCustomerCode);
            mCustomerNameTxtv.setText(mCustomerName);
            mAddress1Txtv.setText(mAddress1);
            mAddress2Txtv.setText(address2);
            mAddress3Txtv.setText(mAddress3);
            mAttentionTxtv.setText(mAttention);
            mPhoneNoTxtv.setText(mPhoneNo);
            mHandphoneNoTxtv.setText(mHandphoneNo);

            mDeliveryCodeTxtv.setText(mDeliveryCode);
            mCustomerCodeTxtv.setCompoundDrawablesWithIntrinsicBounds(0, // left
                    0, // top
                    0, // right
                    0);// bottom


            Log.d("mStatus", ""+mStatus);

            Log.d("mRefNo", "-->" + mRefNo);

            if (mRefNo != null && !mRefNo.isEmpty()) {
                mBeforePicArrList.clear();
                mAfterPicArrList.clear();
                mReferencePicArr.clear();
                mPosmPicArr.clear();
                helper.showProgressView(mDetailLayout);
                mHashMap.put("RefNo", mRefNo);
                new XMLAccessTask(MerchandiseBrandPhotoScreen.this,mValidUrl,
                        "fncGetMerchandiseDetail", mHashMap,false,
                        new mGetMerchandiseDetail()).execute();
            }

            brand_layout.setVisibility(View.GONE);
            picture_layout.setVisibility(View.VISIBLE);

            // Default Selection
            mPictureBtnTab.setBackgroundResource(R.drawable.tab_select_right);

            // Default Button Flag Tab
            mDetailFlag = "Picture";

            isBundleValue = true;

        } else {

//			getLocation();
            //getLongitudeAndLatitude();

            mCustomerCodeTxtv
                    .setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
                            mCustomerCodeTxtv) {
                        @Override
                        public boolean onDrawableClick() {
                            mCustomerCodeTxtv.requestFocus();
                            customerDialogAction();
                            return true;
                        }
                    });



         //   new GetCustomerData().execute();
            new GetProduct().execute();

            brand_layout.setVisibility(View.VISIBLE);
            picture_layout.setVisibility(View.GONE);

            // Default Selection
            mBrandBtnTab.setBackgroundResource(R.drawable.tab_select_left);

            // Default Button Flag Tab
            mDetailFlag = "CustomerDetail";

            mOutLetNameTxtv
                    .setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
                            mOutLetNameTxtv) {
                        @Override
                        public boolean onDrawableClick() {
                            String customerCode = mCustomerCodeTxtv.getText()
                                    .toString();
                            if (customerCode != null && !customerCode.isEmpty()) {
                                if (mCustomer != null && !mCustomer.isEmpty()) {
                                    customerAddressDialog();
                                }else{
                                    Toast.makeText(MerchandiseBrandPhotoScreen.this,
                                            "No Outlet Name for this customer",
                                            Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(MerchandiseBrandPhotoScreen.this,
                                        "Please select Customer",
                                        Toast.LENGTH_SHORT).show();
                            }

                            return true;
                        }
                    });

            isBundleValue = false;
        }

        mCustomerCodeTxtv
                .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            mCustomerCodeTxtv
                                    .setCompoundDrawablesWithIntrinsicBounds(0, // left
                                            0, // top
                                            R.mipmap.ic_search_active, // right
                                            0);// bottom
                        } else {
                            mCustomerCodeTxtv
                                    .setCompoundDrawablesWithIntrinsicBounds(0, // left
                                            0, // top
                                            R.mipmap.ic_search_inactive, // right
                                            0);// bottom
                        }
                    }
                });

        mOutLetNameTxtv
                .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            mOutLetNameTxtv
                                    .setCompoundDrawablesWithIntrinsicBounds(0, // left
                                            0, // top
                                            R.mipmap.ic_search_active, // right
                                            0);// bottom
                        } else {
                            mOutLetNameTxtv
                                    .setCompoundDrawablesWithIntrinsicBounds(0, // left
                                            0, // top
                                            R.mipmap.ic_search_inactive, // right
                                            0);// bottom
                        }
                    }
                });

        // GetStatus values
      //  new GetStatus().execute();

        mMerchandiseStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (mStatusValue.size() > 0) {
                    int pos = mMerchandiseStatus.getSelectedItemPosition();
                    SO so = mStatusValue.get(pos);
                    selectedStatus = so.getStatusCode();

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                if(i == brandnamelist.size()){
                    i = brandnamelist.size() - 1;
                }
               Log.d("brandnamenext", String.valueOf(i));
                brandname.setText(brandnamelist.get(i));
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i--;
                if(i < 0){
                    i = 0;
                }
                Log.d("brandnameback", String.valueOf(i));
                brandname.setText(brandnamelist.get(i));
            }
        });
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        Log.d("checked", String.valueOf(checked));

       if(checked){
           posm_layout.setVisibility(View.VISIBLE);
        }else{
           posm_layout.setVisibility(View.GONE);
       }
    }

    @Override
    public void onClick(View v) {

        String customerCode ;
        switch (v.getId()) {
            case R.id.additionalRemarks:
                remarksDialog();
                break;
            case R.id.addBeforePic:
                dispatchTakePictureIntent(TAKE_PICTURE_BEFORE_JOB);
                //cameraAction(TAKE_PICTURE_BEFORE_JOB);
                break;
            case R.id.addAfterPic:
                dispatchTakePictureIntent(TAKE_PICTURE_AFTER_JOB);
                break;
            case R.id.addReferencePic:
                remarks();
                break;
            case R.id.addposm:
                dispatchTakePictureIntent(TAKE_PICTURE_POSM);
                break;
            case R.id.addSignature:
                SignatureAction(TAKE_SIGNATURE_IMAGE);
                break;
            case R.id.custcode_img:
                customerCode = mCustomerCodeTxtv.getText().toString();
                if (customerCode != null && !customerCode.isEmpty()) {

                    if(mRefNo !=null && !mRefNo.isEmpty() ){
                        helper.showProgressView(mDetailLayout);
                        new SaveMerchandiseHoldOrComplete().execute("2");
                    }else{
                        helper.showProgressView(mDetailLayout);
                        new SaveMerchandiseStart("","2","","","").execute();
                    }

                }
                break;
            case R.id.search_img:
                customerCode = mCustomerCodeTxtv.getText().toString();
                if (customerCode != null && !customerCode.isEmpty()) {
                    if(mRefNo !=null && !mRefNo.isEmpty() ){
                        helper.showProgressView(mDetailLayout);
                        new SaveMerchandiseHoldOrComplete().execute("1");
                    }else{
                        helper.showProgressView(mDetailLayout);
                        new SaveMerchandiseStart("","1","","","").execute();
                    }

                }
                break;
            case R.id.takeBeforePic:
                dispatchTakePictureIntent(TAKE_PICTURE_BEFORE_JOB);
                break;
            case R.id.takeBeforePicLbl:
                dispatchTakePictureIntent(TAKE_PICTURE_BEFORE_JOB);
                break;
            case R.id.takeAfterPic:
                dispatchTakePictureIntent(TAKE_PICTURE_AFTER_JOB);
                break;
            case R.id.takeAfterPicLbl:
                dispatchTakePictureIntent(TAKE_PICTURE_AFTER_JOB);
                break;
            case R.id.takeReferenceImage:
                remarks();
                break;
            case R.id.takeReferenceImageLbl:
                remarks();
                break;

            case R.id.addSign:
                SignatureAction(TAKE_SIGNATURE_IMAGE);
                break;
            case R.id.brandBtnTab:
                mDetailFlag = "CustomerDetail";
                mBrandBtnTab.setBackgroundResource(R.drawable.tab_select_left);
                mPictureBtnTab.setBackgroundResource(R.drawable.rounded_tab_right);
                brand_layout.setVisibility(View.VISIBLE);
                picture_layout.setVisibility(View.GONE);
                mHoldImgBtn.setVisibility(View.INVISIBLE);
                mCompleteImgBtn.setVisibility(View.INVISIBLE);
                break;

            case R.id.pictureBtnTab:

                    mDetailFlag = "Picture";
                    mBrandBtnTab
                            .setBackgroundResource(R.drawable.rounded_tab_left);
                    mPictureBtnTab
                            .setBackgroundResource(R.drawable.tab_select_right);
                    brand_layout.setVisibility(View.GONE);
                picture_layout.setVisibility(View.VISIBLE);
                mHoldImgBtn.setVisibility(View.VISIBLE);
                mCompleteImgBtn.setVisibility(View.VISIBLE);

                break;
            case R.id.customerscreen:
                Intent intent = new Intent(MerchandiseBrandPhotoScreen.this,MerchandiseBrand.class);
                Product.setMerchandiseflag("");
                startActivity(intent);
                MerchandiseBrandPhotoScreen.this.finish();
                break;
            case R.id.balance_stock:

                Intent i = new Intent(MerchandiseBrandPhotoScreen.this,MerchandiseAddProduct.class);
                Product.setMerchandiseflag("BalanceStock");
                startActivity(i);
                MerchandiseBrandPhotoScreen.this.finish();

                break;
            case R.id.placeorder_btn:

                Intent in = new Intent(MerchandiseBrandPhotoScreen.this,MerchandiseAddProduct.class);
                Product.setMerchandiseflag("PlaceOrder");
                startActivity(in);
                MerchandiseBrandPhotoScreen.this.finish();
                break;
            case R.id.imagelayout:

                break;
            default:
                break;
        }

    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;
        try {
            f = setUpPhotoFile();

            mCurrentPhotoPath = f.getAbsolutePath();

            Log.d("mCurrentPhotoPath", "dispatchTakePictureIntent--->"+mCurrentPhotoPath);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }
        startActivityForResult(takePictureIntent, actionCode);
    }

	/*public static Bitmap rotateImage(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
				matrix, true);
	}*/

    public int getCorrectCameraOrientation(Camera.CameraInfo info, Camera camera) {

        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch(rotation){
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;

        }

        int result;
        if(info.facing==Camera.CameraInfo.CAMERA_FACING_FRONT){
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        }else{
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

    private void SignatureAction(int SIGNATURE_ACTION) {
        mIntent.setClass(MerchandiseBrandPhotoScreen.this, CaptureSignature.class);
        startActivityForResult(mIntent, SIGNATURE_ACTION);
    }

	/*public void CameraAction(int CAMERA_ACTION) {

		try {
			 Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	         if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	             startActivityForResult(takePictureIntent, CAMERA_ACTION);
	         }

		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	/*public void cameraAction(int actionCode) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}
		startActivityForResult(takePictureIntent, actionCode);
	}*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        slNo++;
        Log.d("slNo", "--->" + slNo);
        switch (requestCode) {
            case TAKE_PICTURE_BEFORE_JOB:
                if (resultCode == RESULT_OK) {
//					setCapturedImage(mCurrentPhotoPath,requestCode);
                    getRightAngleImage(mCurrentPhotoPath);
                    handleCameraPhoto(TAKE_PICTURE_BEFORE_JOB);
                }
                break;
            case TAKE_PICTURE_AFTER_JOB:
                if (resultCode == RESULT_OK) {
                    getRightAngleImage(mCurrentPhotoPath);
                    handleCameraPhoto(TAKE_PICTURE_AFTER_JOB);
                }
                break;
            case TAKE_REFERENCE_PICTURE:
                if (resultCode == RESULT_OK) {
                    getRightAngleImage(mCurrentPhotoPath);
                    handleCameraPhoto(TAKE_REFERENCE_PICTURE);
                }
                break;
            case TAKE_PICTURE_POSM:
                if (resultCode == RESULT_OK) {
                    getRightAngleImage(mCurrentPhotoPath);
                    handleCameraPhoto(TAKE_PICTURE_POSM);
                }
                break;
            case TAKE_SIGNATURE_IMAGE:
                if (resultCode == RESULT_OK) {
                    byte[] bytes = data.getByteArrayExtra("status");
                    if (bytes != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                                bytes.length);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 240, 80,
                                true);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        mSignature.setImageBitmap(bitmap);
                        byte[] bitMapData = stream.toByteArray();
                        String signature_image = Base64.encodeToString(
                                bitMapData, Base64.DEFAULT);


                        if(mRefNo !=null && !mRefNo.isEmpty() ){
                            helper.showProgressView(mDetailLayout);
                            new SaveMerchandiseImages().execute(String.valueOf(slNo),"S",signature_image,"");
                        }else{
                            helper.showProgressView(mDetailLayout);
                            new SaveMerchandiseStart(String.valueOf(slNo),"0","S",signature_image,"").execute();
                        }
                    }
                }
                break;

        }
    }
    private void setCapturedImage(final String imagePath,final int requestCode){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected void onPreExecute() {
                helper.showProgressView(mDetailLayout);
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    return getRightAngleImage(imagePath);
                }catch (Exception e){

                    e.printStackTrace();
                }
                return imagePath;
            }

            @Override
            protected void onPostExecute(String imagePath) {
                super.onPostExecute(imagePath);
                Log.d("imagePath-->","---->"+imagePath);

                //	galleryAddPic();
                loadCameraImage(requestCode,imagePath);
                helper.dismissProgressView(mDetailLayout);
            }
        }.execute();
    }

    public void loadCameraImage(int requestCode,String imagePath){
        switch (requestCode) {
            case TAKE_PICTURE_BEFORE_JOB:
//			setBeforePicture(imagePath);

                setBeforePicture();
                break;
            case TAKE_PICTURE_AFTER_JOB:
                setAfterPicture();
                break;
            case TAKE_REFERENCE_PICTURE:
                setReferencePicture();
                break;
        }
    }
    private String getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 90;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree,photoPath);

        } catch (Exception e) {
            e.printStackTrace();
            helper.dismissProgressView(mDetailLayout);
        }

        return photoPath;
    }

    private String rotateImage(int degree, String imagePath){
        Log.d("degree", "-->"+degree);
        if(degree<=0){
            return imagePath;
        }
        try{
            Bitmap b= BitmapFactory.decodeFile(imagePath);
            Matrix matrix = new Matrix();
            if(b.getWidth()>b.getHeight()){
                matrix.setRotate(degree);
//				b.recycle();
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            }else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        }catch (Exception e){
            e.printStackTrace();
            helper.dismissProgressView(mDetailLayout);
        }
        return imagePath;
    }

	/*public String getImagePath() {
		return mCurrentPhotoPath;
	}*/

    private void remarks(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MerchandiseBrandPhotoScreen.this);
        alert.setTitle("Remarks");
        final EditText input = new EditText(MerchandiseBrandPhotoScreen.this);
        alert.setView(input);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                remarks = input.getEditableText().toString();
                if(remarks!=null && !remarks.isEmpty()){
                    hideKeyboard(input);

                    dispatchTakePictureIntent(TAKE_REFERENCE_PICTURE);
                    dialog.cancel();
                }else{
                    Toast.makeText(MerchandiseBrandPhotoScreen.this,"Please Enter Remarks",Toast.LENGTH_LONG).show();
                }
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.setNeutralButton("SKIP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                remarks="";
                dispatchTakePictureIntent(TAKE_REFERENCE_PICTURE);
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    public class RemarksListAdapter extends ArrayAdapter<Product> {
        Context context;
        int layoutResourceId;
        ArrayList<Product> data=new ArrayList<Product>();
        public RemarksListAdapter(Context context, int layoutResourceId, ArrayList<Product> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ImageHolder holder = null;

            if(row == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ImageHolder();
                holder.txtTitle = (TextView)row.findViewById(R.id.textView1);
                holder.imgIcon = (ImageView)row.findViewById(R.id.imageView1);
                row.setTag(holder);
            }
            else
            {
                holder = (ImageHolder)row.getTag();
            }

            Product product = data.get(position);
            holder.txtTitle.setText(product.getRemarks());
            if(product.isFlag()){
                decodeBase64File(product.getProductImage(),holder.imgIcon);
            }
            else{
                decodeImagePathFile(product.getProductImage(),holder.imgIcon);
            }

            return row;

        }

        class ImageHolder
        {
            ImageView imgIcon;
            TextView txtTitle;
        }
    }
    public void remarksDialog() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(
                MerchandiseBrandPhotoScreen.this);
        final ListView listview = new ListView(MerchandiseBrandPhotoScreen.this);
        LinearLayout layout = new LinearLayout(MerchandiseBrandPhotoScreen.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listview);
        myDialog.setView(layout);
        myDialog.setTitle("REMARKS");
        RemarksListAdapter adapter = new RemarksListAdapter( MerchandiseBrandPhotoScreen.this, R.layout.list_item_remarks,
                mReferencePicArr);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long id) {

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
    private void customerDialogAction() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        final ListView listview = new ListView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        myDialog.setTitle("Customer");
        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0,
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

        mCustomerAdapter = new CustomAlertAdapterSupp(this, customerDataArrHm);
        listview.setAdapter(mCustomerAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub

                customerArrHm = mCustomerAdapter.getArrayList();
                HashMap<String, String> datavalue = customerArrHm.get(position);
                Set<Map.Entry<String, String>> keys = datavalue.entrySet();
                Iterator<Map.Entry<String, String>> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    @SuppressWarnings("rawtypes")
                    Map.Entry mapEntry = iterator.next();
                    String keyValues = (String) mapEntry.getKey();
                    String Values = (String) mapEntry.getValue();
                    mCustomerCodeTxtv.setText(keyValues);
                    mCustomerNameTxtv.setText(Values);
                    mDeliveryCodeTxtv.setText("");
                    mOutLetNameTxtv.setText("");
                    mAddress1Txtv.setText("");
                    mAddress2Txtv.setText("");
                    mAddress3Txtv.setText("");
                    mAttentionTxtv.setText("");
                    mPhoneNoTxtv.setText("");
                    mHandphoneNoTxtv.setText("");
                    mCustomerCodeTxtv.addTextChangedListener(new TextWatcher() {
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
                            mTextlength = mCustomerCodeTxtv.getText().length();
                        }
                    });
                }

                String customerCode = mCustomerCodeTxtv.getText().toString();
                Log.d("customerCode", "list click -->" + customerCode);
                hideKeyboard(editText);
                mCustomerDialog.dismiss();
                new GetCustomerAddress().execute(customerCode);
            }
        });

        customerSearchArrHm = new ArrayList<HashMap<String, String>>(
                customerDataArrHm);
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
                        if (customerName.toLowerCase(Locale.getDefault())
                                .contains(
                                        editText.getText()
                                                .toString()
                                                .toLowerCase(
                                                        Locale.getDefault())
                                                .trim()))
                            customerSearchArrHm.add(customerDataArrHm.get(i));
                    }
                }

                mCustomerAdapter = new CustomAlertAdapterSupp(
                        MerchandiseBrandPhotoScreen.this, customerSearchArrHm);
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
	/*public void getLocation(){
	     if(gps.canGetLocation()){
	      mLatitude = gps.getLatitude();
	      mLongitude = gps.getLongitude();
	   Log.d("latitude","-->"+mLatitude);
	   Log.d("longitude","-->"+mLongitude);
	  // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + mLatitude + "\nLong: " + mLongitude, Toast.LENGTH_LONG).show();
	        }else{
	         // can't get location
	         // GPS or Network is not enabled
	         // Ask user to enable GPS/network in settings
	     //    gps.showSettingsAlert();
	        }
	    }
	private void getLongitudeAndLatitude() {
		try {
			// Getting LocationManager object
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			// // Creating an empty criteria object
			// Criteria criteria = new Criteria();
			//
			// // Getting the name of the provider that meets the criteria
			// provider = locationManager.getBestProvider(criteria, false);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {

				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 20000, 0, this);
					if (locationManager != null) {
						Location location = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {

							onLocationChanged(location);

						}else{
			                   Log.d("location", "null");
			                   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 0, this);
						}
					}
				} else if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 20000, 0, this);
					if (locationManager != null) {
						Location location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							onLocationChanged(location);

						}else{
			                   Log.d("location", "null");
			                   locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, this);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onLocationChanged(Location location) {
		try {
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();

			Log.d("latitude","-->"+mLatitude);
			Log.d("longitude","-->"+mLongitude);

			locationManager.removeUpdates(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}*/

    private class SaveMerchandiseStart extends AsyncTask<Void, Void, Void> {
        String longitude = "", latitude = "", refDate = "",value="",type="",slno="",refImage="",remarks="";

        public SaveMerchandiseStart(String slno,String value,String type,String refImage,String remarks) {
            this.value = value;
            this.type = type;
            this.slno = slno;
            this.refImage = refImage;
            this.remarks = remarks;
        }

        @Override
        protected void onPreExecute() {
            mHashMap.clear();
            mCustomerCode = mCustomerCodeTxtv.getText().toString();
            mDeliveryCode = mDeliveryCodeTxtv.getText().toString();
            // helper.showProgressView(mDetailLayout);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                refDate = SalesOrderSetGet.getServerDate();


                longitude = String.valueOf(mLongitude);
                latitude = String.valueOf(mLatitude);

                Log.d("longitude", "" + longitude);
                Log.d("latitude", "" + latitude);
                Log.d("mCompanyCodeStr", "" + mCompanyCodeStr);
                Log.d("refDate", "" + refDate);
                Log.d("mCustomerCode", "" + mCustomerCode);
                Log.d("mDeliveryCode", "" + mDeliveryCode);
                Log.d("mDeviceId", "" + mDeviceId);
                Log.d("mUerIdStr", "" + mUerIdStr);

                mHashMap.put("CompanyCode", mCompanyCodeStr);
                mHashMap.put("RefNo", "");
                mHashMap.put("RefDate", refDate);
                mHashMap.put("CustomerCode", mCustomerCode);
                mHashMap.put("DeliveryCode", mDeliveryCode);
                mHashMap.put("JobStartTime", "");
                mHashMap.put("Latitude", latitude);
                mHashMap.put("Longitude", longitude);
                mHashMap.put("DeviceID", mDeviceId);
                mHashMap.put("User", mUerIdStr);
                mHashMap.put("IsScheduledJob", "0");
                mHashMap.put("ScheduleJobNo", "");
                // Call GetServerDate
                mSaveMerchandiseStartJsonString = SalesOrderWebService
                        .getSODetail(mHashMap, "fncSaveMerchandiseStart");
                Log.d("mSaveStartJsonString", ""
                        + mSaveMerchandiseStartJsonString);
                mSaveMerchandiseStartJsonObject = new JSONObject(
                        mSaveMerchandiseStartJsonString);
                mSaveMerchandiseStartJsonArray = mSaveMerchandiseStartJsonObject
                        .optJSONArray("SODetails");
                int lengthJsonArray = mSaveMerchandiseStartJsonArray.length();
                if (lengthJsonArray > 0) {
                    JSONObject jsonobject = mSaveMerchandiseStartJsonArray
                            .getJSONObject(0);

                    Log.d("jsonobject", "" + jsonobject.toString());
                    mResult = jsonobject.getString("Result");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void arg0) {
            if (mResult != null && !mResult.isEmpty()) {

                mRefNo = mResult;

                if(value.matches("0")){
                    new SaveMerchandiseImages().execute(String.valueOf(slno),type,refImage,remarks);
                }else {
                    new SaveMerchandiseHoldOrComplete().execute(value);
                }

            } else {
                Toast.makeText(MerchandiseBrandPhotoScreen.this,
                        "Start Job Failed", Toast.LENGTH_SHORT).show();

                helper.dismissProgressView(mDetailLayout);

            }


        }
    }

    private class GetCustomerAddress extends AsyncTask<String, Void, Void> {

        String outletname = "", address1 = "", address2 = "", address3 = "",
                attention = "", phoneNo = "", handphoneNo = "",
                deliveryCode = "";

        @Override
        protected void onPreExecute() {
            mHashMap.clear();
            mCustomer.clear();
            helper.showProgressView(mDetailLayout);
        }

        @Override
        protected Void doInBackground(String... params) {
            String customerCode = params[0];
            try {
                mHashMap.put("CompanyCode", mCompanyCodeStr);
                mHashMap.put("CustomerCode", customerCode);

                Log.d("mCompanyCodeStr", "" + mCompanyCodeStr);
                Log.d("customerCode", customerCode);

                mGetCustomerAddressJsonString = SalesOrderWebService
                        .getSODetail(mHashMap, "fncGetCustomerAddress");
                Log.d("mGetCustomerAddress", ""
                        + mGetCustomerAddressJsonString);
                mGetCustomerAddressJsonObject = new JSONObject(
                        mGetCustomerAddressJsonString);
                mGetCustomerAddressJsonArray = mGetCustomerAddressJsonObject
                        .optJSONArray("SODetails");

                int lengthJsonArray = mGetCustomerAddressJsonArray.length();
                if (lengthJsonArray > 0) {
                    for (int i = 0; i < lengthJsonArray; i++) {
                        JSONObject jsonobject = mGetCustomerAddressJsonArray
                                .getJSONObject(i);
                        Customer customer = new Customer();
                        outletname = jsonobject.getString("CompanyName");
                        address1 = jsonobject.getString("Address1");
                        address2 = jsonobject.getString("Address2");
                        address3 = jsonobject.getString("Address3");
                        phoneNo = jsonobject.getString("PhoneNo");
                        handphoneNo = jsonobject.getString("HandphoneNo");
                        deliveryCode = jsonobject.getString("DeliveryCode");
                        attention = jsonobject.getString("Attention");
                        customer.setOutletName(outletname);
                        customer.setAddress1(address1);
                        customer.setAddress2(address2);
                        customer.setAddress3(address3);
                        customer.setPhoneNo(phoneNo);
                        customer.setHandphoneNo(handphoneNo);
                        customer.setDeliveryCode(deliveryCode);
                        customer.setAttention(attention);
                        mCustomer.add(customer);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void arg0) {

            helper.dismissProgressView(mDetailLayout);
        }
    }

    public void customerAddressDialog() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(
                MerchandiseBrandPhotoScreen.this);
        final ListView listview = new ListView(MerchandiseBrandPhotoScreen.this);
        LinearLayout layout = new LinearLayout(MerchandiseBrandPhotoScreen.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        myDialog.setTitle("Make your selection");
        layout.addView(listview);
        myDialog.setView(layout);
        final CustomAlertAdapter arrayAdapter = new CustomAlertAdapter(
                MerchandiseBrandPhotoScreen.this, mCustomer);
        listview.setAdapter(arrayAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long id) {
                Customer customer = arrayAdapter.getItem(position);
                mDeliveryCodeTxtv.setText(customer.getDeliveryCode());
                mOutLetNameTxtv.setText(customer.getOutletName());
                mAddress1Txtv.setText(customer.getAddress1());
                mAddress2Txtv.setText(customer.getAddress2());
                mAddress3Txtv.setText(customer.getAddress3());
                mAttentionTxtv.setText(customer.getAttention());
                mPhoneNoTxtv.setText(customer.getPhoneNo());
                mHandphoneNoTxtv.setText(customer.getHandphoneNo());
                myalertDialog.dismiss();
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

    public class CustomAlertAdapter extends BaseAdapter {

        private ArrayList<Customer> listarray = new ArrayList<Customer>();
        LayoutInflater mInflater;
        CustomHolder holder = new CustomHolder();
        Customer customer;

        public CustomAlertAdapter(Activity context,
                                  ArrayList<Customer> customerList) {
            listarray.clear();
            this.listarray = customerList;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listarray.size();
        }

        @Override
        public Customer getItem(int position) {
            return listarray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return listarray.get(position).hashCode();
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            View row = convertView;
            holder = null;

            if (row == null) {
                row = mInflater.inflate(R.layout.customer_list_item, null);
                holder = new CustomHolder();
                holder.custCode = (TextView) row
                        .findViewById(R.id.in_cust_code);
                holder.custName = (TextView) row
                        .findViewById(R.id.in_cust_name);
                row.setTag(holder);
            } else {
                holder = (CustomHolder) row.getTag();
            }

            customer = getItem(position);
            holder.custCode.setText(customer.getOutletName());
            holder.custName.setText(customer.getAddress1());

            holder.custName.setTag(position);

            return row;
        }

        final class CustomHolder {

            TextView custCode;
            TextView custName;

        }

    }

    public class MerchandiseImageAdapter extends BaseAdapter {
        ArrayList<Product> mTakePicArr;
        Activity mActivity;
        int resource;
        LayoutInflater inflater;
        boolean isMerchandiseEdit;
        public MerchandiseImageAdapter() {
        }

        public MerchandiseImageAdapter(Activity activity,
                                       ArrayList<Product> takePicArr) {

            Log.d("takePicArr",""+takePicArr.size());
            mActivity = activity;
            this.mTakePicArr = new ArrayList<Product>();
            this.mTakePicArr.addAll(takePicArr);
            inflater = LayoutInflater.from(mActivity);
        }

        @Override
        public int getCount() {
            return mTakePicArr.size();
        }

        @Override
        public Product getItem(int positon) {
            return mTakePicArr.get(positon);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Product product = getItem(position);
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(
                        R.layout.merchandise_image_listitem, null);
                holder.merchandiseImage = (ImageView) convertView
                        .findViewById(R.id.imageView1);
                holder.closeImage = (ImageView) convertView
                        .findViewById(R.id.imageView2);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            convertView.setTag(holder);
            holder.closeImage.setId(position);
            holder.merchandiseImage.setId(position);

            String merchandiseImage = product.getProductImage();

            Log.d("merchandiseImage",merchandiseImage);

            if(product.isFlag()){
                decodeBase64File(merchandiseImage,holder.merchandiseImage);
            }
            else{
                decodeImagePathFile(merchandiseImage,holder.merchandiseImage);
            }
            holder.closeImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    deleteDialog(v);
                }
            });
            holder.merchandiseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = view.getId();
                    Product product = getItem(position);
                    String merchandiseImage = product.getProductImage();
                    Boolean flag = product.isFlag();
                    Log.d("merchandiseImage",merchandiseImage);
                    Log.d("flag", String.valueOf(flag));

                    zoomImageFromThumb(holder.merchandiseImage, merchandiseImage,flag);

                    //zoomAlertDialog(merchandiseImage,flag);
                }
            });

            mShortAnimationDuration = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
            return convertView;
        }

        class ViewHolder {
            ImageView merchandiseImage;
            ImageView closeImage;

        }

        private void remove(Product product) {
            mTakePicArr.remove(product);
            notifyDataSetChanged();
        }

        private void deleteDialog(final View v) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog.setTitle("Confirm Delete...");
            alertDialog.setMessage("Are you sure you want delete this?");
            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int position = v.getId();
                            Product product = getItem(position);
                            String slno = product.getSlNo();
                            String type = product.getType();
                            new SaveMerchandiseImageDelete(product,slno,type).execute(slno);
                            remove(product);
                        }
                    });

            alertDialog.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();

        }
    }


    private void zoomImageFromThumb(final View thumbView, String imageResId ,Boolean flag) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        Bitmap bitmap = null;
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        if(flag){
            try{
                byte[] encodeByte = Base64.decode(imageResId, Base64.DEFAULT);

			/* There isn't enough memory to open up more than a couple camera photos */
			/* So pre-scale the target bitmap into which the file is decoded */

			/* Get the size of the ImageView */
                int targetW = expandedImageView.getWidth();
                int targetH = expandedImageView.getHeight();

			/* Get the size of the image */
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                //BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,encodeByte.length, bmOptions);
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

                expandedImageView.setImage(ImageSource.bitmap(bitmap));
            }catch (OutOfMemoryError e){
                e.printStackTrace();
            }
        }
        else {

            try {
                int targetW = expandedImageView.getWidth();
                int targetH = expandedImageView.getHeight();

		/* Get the size of the image */
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imageResId, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
                int scaleFactor = 1;
                if ((targetW > 0) || (targetH > 0)) {
                    scaleFactor = Math.min(photoW / targetW, photoH / targetH);
                }

		/* Set bitmap options to scale the image decode target */
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
                bitmap = BitmapFactory.decodeFile(imageResId, bmOptions);

                Log.d("bitmap", String.valueOf(bitmap));

                expandedImageView.setImage(ImageSource.bitmap(bitmap));
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

        }

        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();


        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.detailLayout)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        final float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        zoomLayout.setVisibility(View.VISIBLE);
        //expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;

        close_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        //  expandedImageView.setVisibility(View.GONE);
                        zoomLayout.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        //expandedImageView.setVisibility(View.GONE);
                        zoomLayout.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
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

    public void decodeImagePathFile(String imageString,ImageView imageView) {
        try {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();

		/* Get the size of the image */
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageString, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
            int scaleFactor = 1;
            if ((targetW > 0) || (targetH > 0)) {
                scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            }

		/* Set bitmap options to scale the image decode target */
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
            Bitmap bitmap = BitmapFactory.decodeFile(imageString, bmOptions);

//		Bitmap bitmaprst = rotate(bitmap,90);

            imageView.setImageBitmap(bitmap);

        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }

    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    private class SaveMerchandiseImageDelete extends
            AsyncTask<String, Void, Void> {
        private Product product;
        private String mSlNo="",mType ="";
        public SaveMerchandiseImageDelete(Product prod, String slno, String type) {
            product = prod;
            mSlNo = slno;
            mType = type;
        }

        @Override
        protected void onPreExecute() {
            mHashMap.clear();
            helper.showProgressView(mDetailLayout);
        }

        @Override
        protected Void doInBackground(String... params) {
            String slno = params[0];
            try {
                mHashMap.put("CompanyCode", mCompanyCodeStr);
                mHashMap.put("RefNo", mRefNo);
                mHashMap.put("slNo", slno);
                mHashMap.put("User", mUerIdStr);

                Log.d("mCompanyCodeStr", "" + mCompanyCodeStr);
                Log.d("RefNo", mRefNo);
                Log.d("slno", "" + slno);
                Log.d("mUerIdStr", "" + mUerIdStr);

                mSaveMerchandiseImageDeleteJsonString = SalesOrderWebService
                        .getSODetail(mHashMap, "fncSaveMerchandiseImageDelete");
                Log.d("ImageDeleteJsonString", ""
                        + mSaveMerchandiseImageDeleteJsonString);
                mSaveMerchandiseImageDeleteJsonObject = new JSONObject(
                        mSaveMerchandiseImageDeleteJsonString);
                mSaveMerchandiseImageDeleteJsonArray = mSaveMerchandiseImageDeleteJsonObject
                        .optJSONArray("SODetails");

                int lengthJsonArr = mSaveMerchandiseImageDeleteJsonArray
                        .length();
                if (lengthJsonArr > 0) {
                    JSONObject jsonobject = mSaveMerchandiseImageDeleteJsonArray
                            .getJSONObject(0);

                    mResult = jsonobject.getString("Result");
                    Log.d("jsonobject", "" + jsonobject.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void arg0) {
            if (mResult != null && !mResult.isEmpty()) {
                Toast.makeText(MerchandiseBrandPhotoScreen.this, "Deleted",
                        Toast.LENGTH_SHORT).show();
                try{

                    //mMerchandiseImageAdapter.remove(product);
                    //	mMerchandiseImageAdapter.notifyDataSetChanged();


                    if(mType.matches("A")){
                        loop : for(int i=0;i<mAfterPicArrList.size();i++){
                            String sno = mAfterPicArrList.get(i).getSlNo();
                            if(mSlNo.matches(sno)){
                                mAfterPicArrList.remove(i);
                                break loop;
                            }
                        }
                    }else if(mType.matches("B")){
                        loop: for(int i=0;i<mBeforePicArrList.size();i++){
                            String sno = mBeforePicArrList.get(i).getSlNo();
                            if(mSlNo.matches(sno)){
                                mBeforePicArrList.remove(i);
                                break loop;
                            }
                        }
                    } else if(mType.matches("R")){
                        loop: for(int i=0;i<mReferencePicArr.size();i++){
                            String sno = mReferencePicArr.get(i).getSlNo();
                            if(mSlNo.matches(sno)){
                                mReferencePicArr.remove(i);
                                break loop;
                            }
                        }
                    }
                    else if(mType.matches("P")){
                        loop: for(int i=0;i<mPosmPicArr.size();i++){
                            String sno = mPosmPicArr.get(i).getSlNo();
                            if(mSlNo.matches(sno)){
                                mPosmPicArr.remove(i);
                                break loop;
                            }
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MerchandiseBrandPhotoScreen.this, "Failed",
                        Toast.LENGTH_SHORT).show();
            }
            helper.dismissProgressView(mDetailLayout);
        }
    }

    private class GetProduct extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mBrandlistArr.clear();
            helper.showProgressView(mDetailLayout);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            mHashMap.put("CompanyCode", mCompanyCodeStr);
            mHashMap.put("CategoryCode", "NUTS");

            try {
                // Customer CALL
                mProductJsonString = SalesOrderWebService.getSODetail(
                        mHashMap, "fncGetProductForSearch");
                Log.d("mProductJsonString ", "" + mProductJsonString);
                mProductJSONObject = new JSONObject(mProductJsonString);
                mProductJSONArray = mProductJSONObject
                        .optJSONArray("SODetails");

                int lengthJsonArr = mProductJSONArray.length();
                Log.d("fncGetProductForSearch ", "-->" + lengthJsonArr);
                if (lengthJsonArr > 0) {
                    for (int i = 0; i < lengthJsonArr; i++) {

                        JSONObject jsonChildNode = mProductJSONArray
                                .getJSONObject(i);
                        String pcode = jsonChildNode.optString(
                                "ProductCode").toString();
                        String pname = jsonChildNode.optString(
                                "ProductName").toString();

                        Product product = new Product();
                        product.setProductName(pname);
                        product.setProductCode(pcode);
                        mBrandlistArr.add(product);

                        brandnamelist.add(pname);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            recyclerAdapter = new BrandListAdapter(MerchandiseBrandPhotoScreen.this, mBrandlistArr);
            brand_lv.setAdapter(recyclerAdapter);
            recyclerAdapter.notifyDataSetChanged();

            brandname.setText(brandnamelist.get(0));

            helper.dismissProgressView(mDetailLayout);
        }
    }

    private class GetCustomerData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            helper.showProgressView(mDetailLayout);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            mHashMap.put("CompanyCode", mCompanyCodeStr);
            mHashMap.put("VanCode", mVanStr);

            try {
                // Customer CALL
                mCustomerJsonString = SalesOrderWebService.getSODetail(
                        mHashMap, "fncGetCustomerForSearch");
                Log.d("mCustomerJsonString ", "" + mCustomerJsonString);
                mCustomerJSONObject = new JSONObject(mCustomerJsonString);
                mCustomerJSONArray = mCustomerJSONObject
                        .optJSONArray("SODetails");

                int lengthJsonArr = mCustomerJSONArray.length();
                Log.d("fncGetCustomer ", "-->" + lengthJsonArr);
                if (lengthJsonArr > 0) {
                    for (int i = 0; i < lengthJsonArr; i++) {

                        JSONObject jsonChildNode = mCustomerJSONArray
                                .getJSONObject(i);
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
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            helper.dismissProgressView(mDetailLayout);
        }
    }
    private class GetStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mStatusValue.clear();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            mHashMap.put("CompanyCode", mCompanyCodeStr);

            try {
                // Customer CALL
                mStatuJsonString = SalesOrderWebService.getSODetail(
                        mHashMap, "fncGetStatus");
                Log.d("mStatuJsonString ", "" + mStatuJsonString);
                mStatuJSONObject = new JSONObject(mStatuJsonString);
                mStatuJSONArray = mStatuJSONObject
                        .optJSONArray("SODetails");

                int lengthJsonArr = mStatuJSONArray.length();
                if (lengthJsonArr > 0) {
                    for (int i = 0; i < lengthJsonArr; i++) {
                        SO so = new SO();
                        JSONObject jsonChildNode = mStatuJSONArray
                                .getJSONObject(i);
                        String statusCode = jsonChildNode.optString(
                                "StatusCode").toString();
                        String statusName = jsonChildNode.optString(
                                "StatusName").toString();
                        so.setStatusCode(statusCode);
                        so.setStatusName(statusName);
                        mStatusValue.add(so);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mMerchandiseStatus.setAdapter(new StatusSpinnerAdapter(
                    MerchandiseBrandPhotoScreen.this, R.layout.spinner_item,
                    mStatusValue));

            if(isBundleValue){
                if (mStatus != null && !mStatus.isEmpty())
                {
                    for(SO so :mStatusValue){
                        if(so.getStatusCode().matches(mStatus)){
                            mMerchandiseStatus.setSelection(getIndex(mStatusValue,so.getStatusName()));
                            Log.d("status", "-->"+so.getStatusName());
                        }
                    }
                }
                else
                {
                    mMerchandiseStatus.setSelection(getIndex(mStatusValue,"Open"));
                }
            }else{

                mMerchandiseStatus.setSelection(getIndex(mStatusValue,"Closed"));
            }
        }
    }
    private int getIndex(ArrayList<SO> spinnerArr, String myString)
    {
        Log.d("status", ">>>>"+myString);

        int index = 0;

        for(int i =0 ;i<spinnerArr.size();i++){
            String statusName = spinnerArr.get(i).getStatusName();
            if(statusName.equalsIgnoreCase(myString)){

                index = i;
                break;
            }
        }



        return index;
    }


    private class mGetMerchandiseDetail implements XMLAccessTask.CallbackInterface {

        public void onSuccess(NodeList nl) {

            for (int i = 0; i < nl.getLength(); i++) {
                // creating new HashMap
                Element e = (Element) nl.item(i);

                mRefNo = XMLParser.getValue(e, "RefNo");
                String slno = XMLParser.getValue(e, "SlNo");
                String refType = XMLParser.getValue(e, "RefType");
                String refImage = XMLParser.getValue(e, "RefImage");
                String referenceRemarks = XMLParser.getValue(e, "Remarks");

                mSNo.add(slno.equals("") ? 0
                        : Integer.valueOf(slno));
                if(refType.matches("B")){
                    Product product = new Product();
                    product.setSlNo(slno);
                    product.setType(refType);
                    product.setStatus("1");
                    product.setProductImage(refImage);
                    product.setRemarks("");
                    product.setFlag(true);
                    mBeforePicArrList.add(product);
                }else if(refType.matches("A")){
                    Product product = new Product();
                    product.setSlNo(slno);
                    product.setType(refType);
                    product.setStatus("1");
                    product.setProductImage(refImage);
                    product.setRemarks("");
                    product.setFlag(true);
                    mAfterPicArrList.add(product);
                }else if(refType.matches("R")){
                    Product product = new Product();
                    product.setSlNo(slno);
                    product.setType(refType);
                    product.setStatus("1");
                    product.setProductImage(refImage);
                    product.setRemarks(referenceRemarks);
                    product.setFlag(true);
                    mReferencePicArr.add(product);
                }else if(refType.matches("P")){
                Product product = new Product();
                product.setSlNo(slno);
                product.setType(refType);
                product.setStatus("1");
                product.setProductImage(refImage);
                product.setRemarks(referenceRemarks);
                product.setFlag(true);
                    mPosmPicArr.add(product);
                } else if(refType.matches("S")){
                    Product product = new Product();
                    product.setSlNo(slno);
                    product.setType(refType);
                    product.setStatus("1");
                    product.setProductImage(refImage);
                    product.setRemarks("");
                    product.setFlag(true);
                    mSignPicArr.add(product);
                }
            }
            helper.dismissProgressView(mDetailLayout);
            loadData();

        }

        @Override
        public void onFailure(XMLAccessTask.ErrorType error) {
            if (error == XMLAccessTask.ErrorType.NETWORK_UNAVAILABLE) {
                helper.showLongToast(R.string.error_showing_image_no_network_connection);
            } else {
                // helper.showLongToast(R.string.error_showing_image);
            }
            helper.dismissProgressView(mDetailLayout);
        }
    }




    private void loadData() {
        mMerchandiseImageAdapter = new MerchandiseImageAdapter(
                MerchandiseBrandPhotoScreen.this, mBeforePicArrList);
        mBeforeTakePicListView.setAdapter(mMerchandiseImageAdapter);


        mMerchandiseImageAdapter = new MerchandiseImageAdapter(
                MerchandiseBrandPhotoScreen.this, mAfterPicArrList);
        mAfterTakePicListView.setAdapter(mMerchandiseImageAdapter);


        mMerchandiseImageAdapter = new MerchandiseImageAdapter(
                MerchandiseBrandPhotoScreen.this, mReferencePicArr);
        mReferenceTakePicListView.setAdapter(mMerchandiseImageAdapter);

        mMerchandiseImageAdapter = new MerchandiseImageAdapter(
                MerchandiseBrandPhotoScreen.this, mPosmPicArr);
        horizontalListViewposm.setAdapter(mMerchandiseImageAdapter);

        slNo = getMax(mSNo);

        if(mSignPicArr.size()>0){
            String sign = mSignPicArr.get(0).getProductImage();

            byte[] imageAsBytes = Base64.decode(sign.getBytes(), Base64.DEFAULT);
            mSignature.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes,
                    0, imageAsBytes.length));

            mSign.setEnabled(false);
            mSign.setClickable(false);
            mAddSignature.setVisibility(View.INVISIBLE);

        }


    }

    private class SaveMerchandiseHoldOrComplete extends
            AsyncTask<String, Void, Void> {
        String remarks1,remarks2;
        @Override
        protected void onPreExecute() {
            mHashMap.clear();
            //	helper.showProgressView(mDetailLayout);
            remarks1 = mRemark1Edt.getText().toString();
            remarks2 = mRemark2Edt.getText().toString();
            mDeliveryCode = mDeliveryCodeTxtv.getText().toString();
        }

        @Override
        protected Void doInBackground(String... params) {
            String isJobClosed = params[0];
            try {

                String date = SalesOrderSetGet.getServerDate();

                Log.d("CompanyCode","-->"+ mCompanyCodeStr);
                Log.d("RefNo","-->"+ mRefNo);
                Log.d("RefDate", "-->"+ date);
                Log.d("CustomerCode","-->"+ mCustomerCode);
                Log.d("DeliveryCode","-->"+ mDeliveryCode);
                Log.d("JobEndTime","-->"+ "");
                Log.d("IsJobClosed","-->"+isJobClosed);
                Log.d("StatusCode","-->"+ selectedStatus);
                Log.d("Remarks1","-->"+ remarks1);
                Log.d("Remarks2","-->"+ remarks2);
                Log.d("User","-->"+ mUerIdStr);

                mHashMap.put("CompanyCode", mCompanyCodeStr);
                mHashMap.put("RefNo", mRefNo);
                mHashMap.put("RefDate", date);
                mHashMap.put("CustomerCode", mCustomerCode);
                mHashMap.put("DeliveryCode", mDeliveryCode);
                mHashMap.put("JobEndTime", "");
                mHashMap.put("IsJobClosed",isJobClosed);
                mHashMap.put("StatusCode", selectedStatus);
                mHashMap.put("Remarks1", remarks1);
                mHashMap.put("Remarks2", remarks2);
                mHashMap.put("User", mUerIdStr);

                mSaveMerchandiseHoldOrCompleteJsonString = SalesOrderWebService
                        .getSODetail(mHashMap,
                                "fncSaveMerchandiseHoldOrComplete");
                Log.d("HoldOrCompleteJson", ""
                        + mSaveMerchandiseHoldOrCompleteJsonString);
                mSaveMerchandiseHoldOrCompleteJsonObject = new JSONObject(
                        mSaveMerchandiseHoldOrCompleteJsonString);
                mSaveMerchandiseHoldOrCompleteJsonArray = mSaveMerchandiseHoldOrCompleteJsonObject
                        .optJSONArray("SODetails");

                int lengthJsonArr = mSaveMerchandiseHoldOrCompleteJsonArray
                        .length();
                if (lengthJsonArr > 0) {
                    JSONObject jsonobject = mSaveMerchandiseHoldOrCompleteJsonArray
                            .getJSONObject(0);

                    Log.d("jsonobject", "" + jsonobject.toString());
                    mResult = jsonobject.getString("Result");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void arg0) {

            if (mResult != null && !mResult.isEmpty()) {
                Toast.makeText(MerchandiseBrandPhotoScreen.this,
                        "Successfully Saved", Toast.LENGTH_SHORT).show();
                mIntent.setClass(MerchandiseBrandPhotoScreen.this,
                        MerchandiseHeader.class);
                mIntent.putExtra("Flag", mFlag);
                startActivity(mIntent);
                finish();
            } else {
                Toast.makeText(MerchandiseBrandPhotoScreen.this, "Failed",
                        Toast.LENGTH_SHORT).show();
            }

            helper.dismissProgressView(mDetailLayout);
        }
    }

    private File getAlbumDir() {
        File storageDir = null;
        String folder_main = "SFA";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir= new File(Environment.getExternalStorageDirectory()+ "/" + folder_main, "Image");
            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }
    private void handleCameraPhoto(int actionCode) {
        Log.d("mCurrentPhotoPath", "mCurrentPhotoPath-->"+mCurrentPhotoPath);
        if (mCurrentPhotoPath != null) {

            switch (actionCode) {
                case TAKE_PICTURE_BEFORE_JOB: {
                    setBeforePicture();
                    break;
                }

                case TAKE_PICTURE_AFTER_JOB: {
                    setAfterPicture();
                    break;
                }

                case TAKE_REFERENCE_PICTURE: {
                    setReferencePicture();
                    break;
                }

                case TAKE_PICTURE_POSM: {
                    setPosmPicture();
                    break;
                }
            }

            //	galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

    }


    private void setBeforePicture(String imagePath){
        Product product = new Product();
        product.setSlNo(String.valueOf(slNo));
        product.setType("B");
        product.setStatus("0");
        product.setProductImage(imagePath);
        product.setFlag(false);
        mBeforePicArrList.add(product);


        mMerchandiseImageAdapter = new MerchandiseImageAdapter(this, mBeforePicArrList);
        mBeforeTakePicListView.setAdapter(mMerchandiseImageAdapter);

			/*if(mRefNo !=null && !mRefNo.isEmpty() ){
				helper.showProgressView(mDetailLayout);
				new SaveMerchandiseImages().execute(String.valueOf(slNo),"B",imagePath,"");
			}else{
				helper.showProgressView(mDetailLayout);
				new SaveMerchandiseStart(String.valueOf(slNo),"0","B",imagePath,"").execute();
			}*/
    }
    private void setBeforePicture(){
        Product product = new Product();
        product.setSlNo(String.valueOf(slNo));
        product.setType("B");
        product.setStatus("0");
        product.setProductImage(mCurrentPhotoPath);
        product.setFlag(false);
        mBeforePicArrList.add(product);

        mMerchandiseImageAdapter = new MerchandiseImageAdapter(this, mBeforePicArrList);
        mBeforeTakePicListView.setAdapter(mMerchandiseImageAdapter);

        if(mRefNo !=null && !mRefNo.isEmpty() ){
            helper.showProgressView(mDetailLayout);
            new SaveMerchandiseImages().execute(String.valueOf(slNo),"B",mCurrentPhotoPath,"");
        }else{
            helper.showProgressView(mDetailLayout);
            new SaveMerchandiseStart(String.valueOf(slNo),"0","B",mCurrentPhotoPath,"").execute();
        }
    }
    private void setReferencePicture(){
        Product product = new Product();
        product.setSlNo(String.valueOf(slNo));
        product.setType("R");
        product.setStatus("0");
        product.setProductImage(mCurrentPhotoPath);
        product.setFlag(false);
        product.setRemarks(remarks);
        mReferencePicArr.add(product);

        mMerchandiseImageAdapter = new MerchandiseImageAdapter(this, mReferencePicArr);
        mReferenceTakePicListView.setAdapter(mMerchandiseImageAdapter);

        if(mRefNo !=null && !mRefNo.isEmpty() ){
            helper.showProgressView(mDetailLayout);
            new SaveMerchandiseImages().execute(String.valueOf(slNo),"R",mCurrentPhotoPath,remarks);
        }else{
            helper.showProgressView(mDetailLayout);
            new SaveMerchandiseStart(String.valueOf(slNo),"0","R",mCurrentPhotoPath,remarks).execute();
        }
    }
    private void setAfterPicture(){
        Product product = new Product();
        product.setSlNo(String.valueOf(slNo));
        product.setType("A");
        product.setStatus("0");
        product.setProductImage(mCurrentPhotoPath);
        product.setFlag(false);
        mAfterPicArrList.add(product);


        mMerchandiseImageAdapter = new MerchandiseImageAdapter(this, mAfterPicArrList);
        mAfterTakePicListView.setAdapter(mMerchandiseImageAdapter);

        if(mRefNo !=null && !mRefNo.isEmpty() ){
            helper.showProgressView(mDetailLayout);
            new SaveMerchandiseImages().execute(String.valueOf(slNo),"A",mCurrentPhotoPath,"");
        }else{
            helper.showProgressView(mDetailLayout);
            new SaveMerchandiseStart(String.valueOf(slNo),"0","A",mCurrentPhotoPath,"").execute();
        }
    }

    private void setPosmPicture(){
        Product product = new Product();
        product.setSlNo(String.valueOf(slNo));
        product.setType("P");
        product.setStatus("0");
        product.setProductImage(mCurrentPhotoPath);
        product.setFlag(false);
        mPosmPicArr.add(product);

        mMerchandiseImageAdapter = new MerchandiseImageAdapter(this, mPosmPicArr);
        horizontalListViewposm.setAdapter(mMerchandiseImageAdapter);

        if(mRefNo !=null && !mRefNo.isEmpty() ){
            helper.showProgressView(mDetailLayout);
            new SaveMerchandiseImages().execute(String.valueOf(slNo),"P",mCurrentPhotoPath,"");
        }else{
            helper.showProgressView(mDetailLayout);
            new SaveMerchandiseStart(String.valueOf(slNo),"0","P",mCurrentPhotoPath,"").execute();
        }
    }
    public int getMax(ArrayList<Integer> list){
        int max = Integer.MIN_VALUE;
        for(int i=0; i<list.size(); i++){
            if(list.get(i) > max){
                max = list.get(i);
            }
        }
        return max;
    }
    public boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isFile()) {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }
    private class SaveMerchandiseImages extends AsyncTask<String, String, String> {
        String type="";
        @Override
        protected void onPreExecute() {
            mHashMap.clear();
        }

        @Override
        protected String doInBackground(String... params) {
            String slno = params[0];
            String refType = params[1];
            String refImage = params[2];
            String refRemarks = params[3];

            type = refType;
            Log.d("slno", ""+slno);
            Log.d("refType", ""+refType);
            Log.d("refImage", "-->"+refImage);
            Log.d("refRemarks", ""+refRemarks);

            try {

                if(!refType.matches("S")){
                    refImage = ImagePathToBase64Str(refImage);
                }
                Log.d("refImage",refImage);
                if(refImage!=null && !refImage.isEmpty()){

                    mHashMap.put("CompanyCode", mCompanyCodeStr);
                    mHashMap.put("RefNo", mRefNo);
                    mHashMap.put("SlNo", slno);
                    mHashMap.put("RefType", refType);
                    mHashMap.put("RefImage", refImage);
                    mHashMap.put("Remarks", refRemarks);
                    mHashMap.put("User", mUerIdStr);

                    // Call GetServerDate
                    mSaveMerchandiseImagesJsonString = SalesOrderWebService
                            .getSODetail(mHashMap,"fncSaveMerchandiseImages");
                    Log.d("ImagesJsonString", ""+ mSaveMerchandiseImagesJsonString);
                    mSaveMerchandiseImagesJsonObject = new JSONObject(mSaveMerchandiseImagesJsonString);
                    mSaveMerchandiseImagesJsonArray = mSaveMerchandiseImagesJsonObject.optJSONArray("SODetails");
                    int lengthJsonArr = mSaveMerchandiseImagesJsonArray.length();
                    if (lengthJsonArr > 0) {
                        JSONObject jsonobject = mSaveMerchandiseImagesJsonArray
                                .getJSONObject(0);
                        mResult = jsonobject.getString("Result");
                        Log.d("jsonobject", "" + jsonobject.toString());
                    }else{
                        mResult ="";
                    }
                }else{
                    mResult ="";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return mResult;

        }

        @Override
        protected void onPostExecute(String mResult) {
            if (mResult != null && !mResult.isEmpty()) {
                Toast.makeText(MerchandiseBrandPhotoScreen.this, "Saved",
                        Toast.LENGTH_SHORT).show();
                if(type.matches("S")){
                    mSign.setEnabled(false);
                    mSign.setClickable(false);
                    mAddSignature.setVisibility(View.INVISIBLE);
                }
                //	deleteDirectory(new File(Environment.getExternalStorageDirectory()+ "/" +"SFA","Image"));
            } else {
                Toast.makeText(MerchandiseBrandPhotoScreen.this, "Failed",
                        Toast.LENGTH_SHORT).show();
            }

            helper.dismissProgressView(mDetailLayout);
        }
    }
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    private String ImagePathToBase64(String imagePath) {
        String base64Image ="";
        try
        {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();

		/* Get the size of the image */
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, bmOptions);
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
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);

            // base64Image = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
            base64Image = BitMapToString(bitmap);

        }catch (OutOfMemoryError e){
            base64Image ="";
            e.printStackTrace();
        }
        return base64Image;
    }

    public String ImagePathToBase64Str(String path) {

        String base64Image ="";
        try {
            // Decode deal_image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 150;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            base64Image = BitMapToString(BitmapFactory.decodeFile(path, o2));
            return base64Image;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        long lengthbmp = b.length;
        Log.d("lengthbmp", String.valueOf(lengthbmp));
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public class StatusSpinnerAdapter extends ArrayAdapter<SO> {

        ArrayList<SO> data = new ArrayList<SO>();
        int resourceId;

        public StatusSpinnerAdapter(Context context, int textViewResourceId,
                                    ArrayList<SO> mData) {

            super(context, textViewResourceId, mData);
            data.clear();
            this.data = mData;
            this.resourceId = textViewResourceId;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            SO so = data.get(position);
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_item, parent, false);
            TextView name = (TextView) row.findViewById(R.id.textView1);
            name.setText(so.getStatusName());
            return row;
        }
    }

    public void hideKeyboard(EditText edittext) {
        InputMethodManager inputmethodmanager = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmethodmanager
                .hideSoftInputFromWindow(edittext.getWindowToken(), 0);

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("onConnected", "onConnected");

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(100); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

//		double lati=0,longi=0;

        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());

            if(lat!=null && !lat.isEmpty()){
                setLatitude = mLastLocation.getLatitude();
            }

            if(lon!=null && !lon.isEmpty()){
                setLongitude = mLastLocation.getLongitude();
            }
        }

        boolean interntConnection = isNetworkConnected();
        if (interntConnection == true) {

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isGPSEnabled == true) {
                Log.d("isGPSEnabled", ""+isGPSEnabled);
//				new getServerDateTime(lati, longi, "1").execute();

                updateUI(setLatitude,setLongitude);
            }else {
//				Log.d("gpsLocation", " null ");
//				new getServerDateTime(0, 0, "0").execute();
                updateUI(0,0);
//					Toast.makeText(getApplicationContext(),
//							"GPS is OFF. Please turn ON",
//							Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(),
                    "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());
//		updateUI();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    void updateUI(double lat,double lon) {
        try {
            Log.d("lat",""+lat);
            Log.d("lon",""+lon);

            mLatitude = lat;
            mLongitude = lon;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    @Override
    public void onListItemClick(String item) {
        // TODO Auto-generated method stub
        menu.toggle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        menu.toggle();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mIntent.setClass(MerchandiseBrandPhotoScreen.this, MerchandiseBrand.class);
        Product.setMerchandiseflag("");
        startActivity(mIntent);
        finish();
    }


    public void onPause()
    {
        super.onPause();

        Log.d("onPause", "onPause");

        System.gc();
    }

}

