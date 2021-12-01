package com.winapp.catalog;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.catalog.CarouselFragment;
import com.winapp.SFA.R;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.LandingActivity;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDataDownloader;
import com.winapp.offline.OfflineDatabase;
import com.winapp.printcube.printcube.GlobalData;
import com.winapp.sot.InvoiceSummary;
import com.winapp.sot.OfflineSalesOrderWebService;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SlideMenuFragment;

import org.w3c.dom.Text;


public class SOCatalogActivity extends  SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace {

	public static boolean checkOffline;
	public static String onlineMode = "", updateonlinemode = "";
	private OfflineCommon offlineCommon;

	  private SlidingMenu menu;
	  private ActionBar mActionBar;
	  private View customView;
	  public static TextView mTitle,mCartText;
	  public static ImageButton mDownloadIcon, mChangeView,mCartIcon,mFilterIcon,mSearchIcon,mCartSaveIcon,mListingSearchIcon,mCartClearAll,mClose,mBack;
	  public static EditText mSearchEd;
      private CarouselFragment carouselFragment;
	public static RelativeLayout mCustomKeyboard;
      public static Button mKeyBtnOne, mKeyBtnTwo, mKeyBtnThree, mKeyBtnFour,
		mKeyBtnFive, mKeyBtnSix, mKeyBtnSeven, mKeyBtnEight, mKeyBtnNine,
		mKeyBtnZero, mKeyBtnDot, mKeyBtnClear;
      private HashMap<String, ImageButton> mHashMap;
	private HashMap<String, TextView> mHashMapTextV;
	private HashMap<String, EditText> mHashMapEdtV;
	private HashMap<String, RelativeLayout> mHashMapLayout;
	private OfflineDatabase offlineDatabase;
	private OfflineDataDownloader imageDownloader;

	private String mValidUrl= "",mServerDate = "",mobileHaveOfflineMode="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_menu);
        setContentView(R.layout.activity_catalog);	
        mHashMap = new HashMap<String, ImageButton>();
		mHashMapTextV = new HashMap<String, TextView>();
		mHashMapEdtV = new HashMap<String, EditText>();
		mHashMapLayout = new HashMap<String, RelativeLayout>();
		mActionBar = getSupportActionBar();		
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.header_bg));		   
		customView = LayoutInflater.from(this).inflate(R.layout.custom_tab_title_bar, null);
		mTitle = (TextView) customView.findViewById(R.id.title);
		
		mDownloadIcon = (ImageButton) customView.findViewById(R.id.right_button5);
		mChangeView = (ImageButton) customView.findViewById(R.id.right_right_image_button);
		mCartIcon = (ImageButton) customView.findViewById(R.id.right_button);
		mCartText = (TextView) customView.findViewById(R.id.cart_txt);		
		mFilterIcon = (ImageButton) customView.findViewById(R.id.right_left_image_button);		
		mSearchIcon = (ImageButton) customView.findViewById(R.id.middle_button);
		
		mCartSaveIcon = (ImageButton) customView.findViewById(R.id.right_button1);
		mListingSearchIcon = (ImageButton) customView.findViewById(R.id.right_button2);
		mCartClearAll = (ImageButton) customView.findViewById(R.id.right_button3);
		mClose = (ImageButton) customView.findViewById(R.id.right_button4);
		mBack = (ImageButton) customView.findViewById(R.id.back);
		mSearchEd = (EditText)customView.findViewById(R.id.search_edt);
		mCustomKeyboard = (RelativeLayout) customView.findViewById(R.id.customKeyboard);
		mKeyBtnOne = (Button) customView.findViewById(R.id.key_one);
		mKeyBtnTwo = (Button) customView.findViewById(R.id.key_two);
		mKeyBtnThree = (Button) customView.findViewById(R.id.key_three);
		mKeyBtnFour = (Button) customView.findViewById(R.id.key_four);
		mKeyBtnFive = (Button) customView.findViewById(R.id.key_five);
		mKeyBtnSix = (Button) customView.findViewById(R.id.key_six);
		mKeyBtnSeven = (Button) customView.findViewById(R.id.key_seven);
		mKeyBtnEight = (Button) customView.findViewById(R.id.key_eight);
		mKeyBtnNine = (Button) customView.findViewById(R.id.key_nine);
		mKeyBtnZero = (Button) customView.findViewById(R.id.key_zero);
		mKeyBtnDot = (Button) customView.findViewById(R.id.key_dot);
		mKeyBtnClear = (Button) customView.findViewById(R.id.key_clear);

		mActionBar.setCustomView(customView);
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);
		checkInternetStatus(SOCatalogActivity.this);
		OfflineDatabase.init(SOCatalogActivity.this);
		offlineDatabase = new OfflineDatabase(SOCatalogActivity.this);
		//Get Server Date form pojo class
		mServerDate = SalesOrderSetGet.getServerDate();
        mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();

		// DB Initialization
		FWMSSettingsDatabase.init(SOCatalogActivity.this);
		// Get URL from DB
		mValidUrl = FWMSSettingsDatabase.getUrl();

		mTitle.setText(getResources().getString(R.string.catalog));
        if(mobileHaveOfflineMode.matches("0")){
            mDownloadIcon.setVisibility(View.GONE);
        }
	//	imageDownload();
		initScreen();
		}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//No call for super(). Bug on API Level > 11.
		super.onSaveInstanceState(outState);
	}
    private void initScreen() {


        // Creating the ViewPager container fragment once
		carouselFragment = new CarouselFragment();
		final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, carouselFragment)
                .commitAllowingStateLoss();


    } 
	private void imageDownload(){
		new Thread() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
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
												imageDownloader = new OfflineDataDownloader(SOCatalogActivity.this,
														mValidUrl);
												imageDownloader
														.startImageDownload(
																true,
																"Updating image from server",
																mServerDate,
																modifydate);
											} else {
												imageDownloader = new OfflineDataDownloader(
														SOCatalogActivity.this,
														mValidUrl);
												imageDownloader
														.startImageDownload(
																true,
																"Downloading image from server",
																mServerDate, "");
											}
										}
									}

										imageDownloader.setOnDownloadCompletionListener(new OfflineDataDownloader.OnDownloadCompletionListener() {
										@Override
										public void onCompleted() {
											initScreen();
										}
									});
									imageDownloader.setOnDownloadFailednListener(new OfflineDataDownloader.OnDownloadFailedListener() {
										@Override
										public void onFailed() {
											reconnectDialog();
										}
									});

								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}
				}.start();

	}
	public void reconnectDialog(){

		final Dialog dialog = new Dialog(SOCatalogActivity.this);

		dialog.setContentView(R.layout.dialog_reconnect);
		dialog.setTitle("Retry");
		ImageView reconnect = (ImageView) dialog.findViewById(R.id.reconnect);

		reconnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				imageDownload();

			}
		});
		dialog.show();
	}
	protected String checkInternetStatus(Activity mActivity) {
		String internetStatus = "";
		String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			if(mobileHaveOfflineMode.matches("1")){
		OfflineDatabase.init(mActivity);
		new OfflineSalesOrderWebService(mActivity);
		onlineMode = OfflineDatabase.getOnlineMode();
		offlineCommon = new OfflineCommon(mActivity);
		checkOffline = OfflineCommon.isConnected(mActivity);

		Log.d("Offline onlineMode", "" + onlineMode);
		Log.d("Offline checkOffline", "" + checkOffline);


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
		/*onlineMode = OfflineDatabase.getOnlineMode();
		if (onlineMode.matches("True")) {
			CarouselFragment.offlineLayout.setVisibility(View.GONE);
			if (checkOffline == true) {
				if (internetStatus.matches("true")) {
					CarouselFragment.offlineLayout.setVisibility(View.VISIBLE);
					CarouselFragment.offlineLayout.setBackgroundResource(R.drawable.temp_offline_pattern_bg);
				}
			}

		} else if (onlineMode.matches("False")) {
			CarouselFragment.offlineLayout.setVisibility(View.VISIBLE);
		}*/


			}else{
				internetStatus = "false";
			}
		}else{
			internetStatus = "false";
		}
		return internetStatus;

	}
    public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//		alertDialog.setTitle("Exit");
		alertDialog.setMessage("Do you want to exit from catalog");
//		alertDialog.setIcon(R.drawable.ic_exit);
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Intent i = new Intent(SOCatalogActivity.this, LandingActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			    		startActivity(i);    		
			    		finish();
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
    
    @Override
    public void onBackPressed() {
try {
			if (!carouselFragment.onBackPressed()) {
//				CatalogProductFragment.isScrollListener = false;
				alertDialog();

			} else {
				// carousel handled the back pressed task
				// do not call super
			}
		}catch (Exception e){

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
}
