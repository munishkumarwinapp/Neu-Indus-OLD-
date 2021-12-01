package com.winapp.fwms;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.winapp.MapModules.MapsHome;
import com.winapp.catalog.SOCatalogActivity;
import com.winapp.crm.AttendanceActivity;
import com.winapp.SFA.BuildConfig;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.helper.Catalog;
import com.winapp.helper.SharedPreference;
import com.winapp.model.Product;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineDataDownloader;
import com.winapp.offline.OfflineDataUploader;
import com.winapp.offline.OfflineDatabase;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.printcube.printcube.MainActivity;
import com.winapp.printer.UIHelper;
import com.winapp.sot.CashInvoiceHeader;
import com.winapp.sot.Company;
import com.winapp.sot.ConsignmentHeader;
import com.winapp.sot.ConsignmentStockHeader;
import com.winapp.sot.DeliveryOrderHeader;
import com.winapp.sot.GraHeader;
import com.winapp.sot.InvoiceHeader;
import com.winapp.sot.ManualStockHeader;
import com.winapp.sot.MerchandiseBrand;
import com.winapp.sot.MerchandiseHeader;
import com.winapp.sot.MerchandiseSchedule;
import com.winapp.sot.MobileSettingsSetterGetter;
import com.winapp.sot.QuickTransferHeader;
import com.winapp.sot.ReceiptHeader;
import com.winapp.sot.RouteHeader;
import com.winapp.sot.SalesOrderHeader;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SalesReturnHeader;
import com.winapp.sot.SettlementAddDenomination;
import com.winapp.sot.SettlementHeader;
import com.winapp.sot.SettlementMainHeader;
import com.winapp.sot.StockAdjustmentHeader;
import com.winapp.sot.StockRequestHeader;
import com.winapp.sot.StockTakeHeader;
import com.winapp.sot.TransferHeader;
import com.winapp.sotdetails.AppMessage;
import com.winapp.sotdetails.CRMTaskActivity;
import com.winapp.sotdetails.DashboardActivity;
import com.winapp.sotdetails.CustomerListActivity;
import com.winapp.sotdetails.DBCatalog;
import com.winapp.sotdetails.DeliveryVerificationHeader;
import com.winapp.sotdetails.ExpenseHeader;
import com.winapp.sotdetails.OverdueHeader;
import com.winapp.sotdetails.PackingHeader;
import com.winapp.sotdetails.ProductAnalysisActivity;
import com.winapp.sotdetails.ProductStockActivity;
import com.winapp.trackuser.AppLocationService;
import com.winapp.trackwithmap.DeliveryOrderNewHeader;
import com.winapp.util.ErrorLog;

public class LandingActivity extends Activity {
	private SharedPreference preference;
	Set<String> suppliernames;
	String refid, valid_url;
	int supplength;
	Random myRandom;
	Date d;
	CharSequence s;
	Button routeMaster, goodsReceive, deliveryOrder, salesOrder, salesReturn, productList,
	customerList, catalog, receipts, settings, invoice,cashinvoice, stockrequest,
	transfer, stockTake, switch_to_online, switch_to_offline, upload, stockAdjustment, expense, overdue,task,merchandise,merchandiseSchedule,
	deliveryVerification, packed,printcube, common_map_btn,attendance_btn,merchandisebrand_btn,productanalysis,
			manualStock,quicktransfer;
	ImageView Logout, logo, img_overdue,img_dashboard,img_overflow;

	LinearLayout offlineLayout,overdueimg_layout,dashboardimg_layout,appmsg_layout;
//	,overduealert;

	LinearLayout routeMasterLayout, goodsReceiveLayout, deliveryOrderLayout, salesOrderLayout,
	salesReturnLayout, productListLayout, customerListLayout,merchandisebrand_layout,
	catalogLayout, receiptsLayout, settingsLayout, invoiceLayout,cashinvoiceLayout,
	stockrequestLayout, transferLayout, stockTakeLayout,
	switch_to_offlineLayout, switch_to_onlineLayout, upload_Layout,stockAdjustmentLayout, expenseLayout, overdue_Layout, task_Layout,merchandiseLayout,merchandiseScheduleLayout
	, deliveryVerificationLayout, packedLayout,attendance_layout,productanalysisLayout,maualStockLayout,quicktransfer_space;

	LinearLayout routeMaster_space, goodsReceive_space, deliveryOrder_space, salesOrder_space,
	salesReturn_space, productList_space, customerList_space,
	invoice_space,cashinvoice_space, catalog_space, receipts_space, stockrequest_space,
	transfer_space, stockTake_space, switch_to_offline_space,
	switch_to_online_space, upload_space, stockAdjustment_space, expense_space, overdue_space, task_space,merchandise_space,merchandise_schedule_space,
	deliveryVerification_space, packed_space,manualStcok_space,quicktransferLayout,settlementLayout,settlement_spae;

	String routeMasterStr, goodsReceiveStr, deliveryOrderStr,consignmentStr, salesOrderStr, salesReturnStr,
	productListStr,productAnalysisStr,customerListStr, catalogStr, receiptsStr,
	settingsStr, invoiceStr, stockrequestStr, transferStr,cashinvoiceStr,consignmentReturnStr,consignmentStockStr,consignmentStockTakeStr,
	stockTakeStr, swtichtoofflinestr, swtichtoonlinestr, uploadStr, stockAdjustmentStr, expenseStr, overdueStr,
			taskStr, offlineModeStr,merchandiseStr,
            merchandiseScheduleStr,manualStockStr,quicktransferstr,settlementStr,
	deliveryVerificationStr, packedStr;
	TextView pageTitle,msg_count;
	ImageButton img_notification;
	
	// int dpWidth,dpheight;
	UIHelper helper;
	double screenInches;
	ArrayList<String> landingMenuArr = new ArrayList<String>();
	OfflineDataUploader offlineUploader;
	String onlineMode,mobileHaveOfflineMode;
	private OfflineDataDownloader dataDownloader;
	private OfflineCommon offlineCommon;
	boolean checkOffline;
//	private Crouton mCrouton;
	OfflineSettingsManager offlinemanager;
	ProgressBar progressBar;
	LinearLayout spinnerLayout, landingLayout;
	private ErrorLog errorLog;
    private boolean showOverDue,dashboardStr;
	Button deliveryOrderNew;
	LinearLayout deliveryOrderNewLayout;
	Button consignment_btn,consignment_return_btn,consignment_stock_btn,consignment_stock_take_btn,settlement_btn;
	LinearLayout consignmentLayout,consignmentReturnLayout,consignmentStockLayout,consignmentStockTakeLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.landing_screen);

		helper = new UIHelper(LandingActivity.this);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int dens = dm.densityDpi;
		double wi = (double) width / (double) dens;
		double hi = (double) height / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		screenInches = Math.sqrt(x + y);
		errorLog = new ErrorLog();

		Log.d("Display Inche", "" + screenInches);
		offlineLayout = (LinearLayout) findViewById(R.id.offlineLayout);
		landingLayout = (LinearLayout) findViewById(R.id.landingLayout);
		pageTitle = (TextView) findViewById(R.id.pageTitle);
		goodsReceiveLayout = (LinearLayout) findViewById(R.id.goodsReceiveLayout);
		deliveryOrderLayout = (LinearLayout) findViewById(R.id.deliveryOrderLayout);
		salesOrderLayout = (LinearLayout) findViewById(R.id.salesOrderLayout);
		salesReturnLayout = (LinearLayout) findViewById(R.id.salesReturnLayout);
		productListLayout = (LinearLayout) findViewById(R.id.productListLayout);
		customerListLayout = (LinearLayout) findViewById(R.id.customerListLayout);
		catalogLayout = (LinearLayout) findViewById(R.id.catalogLayout);
		receiptsLayout = (LinearLayout) findViewById(R.id.receiptsLayout);
		settingsLayout = (LinearLayout) findViewById(R.id.settingsLayout);
		invoiceLayout = (LinearLayout) findViewById(R.id.invoiceLayout);
		stockrequestLayout = (LinearLayout) findViewById(R.id.stockrequestLayout);
		transferLayout = (LinearLayout) findViewById(R.id.transferLayout);
		stockTakeLayout = (LinearLayout) findViewById(R.id.stockTakeLayout);
		stockAdjustmentLayout= (LinearLayout) findViewById(R.id.stockAdjustmentLayout);
		switch_to_offlineLayout = (LinearLayout) findViewById(R.id.switch_to_offlineLayout);
		switch_to_onlineLayout = (LinearLayout) findViewById(R.id.switch_to_onlineLayout);
		upload_Layout = (LinearLayout) findViewById(R.id.upload_Layout);
		routeMasterLayout = (LinearLayout) findViewById(R.id.routeMasterLayout);
		expenseLayout = (LinearLayout) findViewById(R.id.expenseLayout);
		overdue_Layout = (LinearLayout) findViewById(R.id.overdue_Layout);
		overdueimg_layout = (LinearLayout) findViewById(R.id.overdueimg_layout);
		task_Layout = (LinearLayout) findViewById(R.id.task_Layout);
//		overduealert = (LinearLayout) findViewById(R.id.alternate_view_group);
		dashboardimg_layout = (LinearLayout) findViewById(R.id.dashboardimg_layout);
		merchandiseLayout = (LinearLayout) findViewById(R.id.merchandiseLayout);
		merchandiseScheduleLayout = (LinearLayout) findViewById(R.id.merchandiseScheduleLayout);
		productanalysisLayout = (LinearLayout) findViewById(R.id.productanalysisLayout);
		deliveryVerificationLayout = (LinearLayout) findViewById(R.id.deliveryVerificationLayout);
		packedLayout = (LinearLayout) findViewById(R.id.packedLayout);
		consignmentLayout=(LinearLayout)findViewById(R.id.consignmentLayout);
        consignmentReturnLayout=(LinearLayout)findViewById(R.id.consignmentReturnLayout);
		consignmentStockLayout =(LinearLayout) findViewById(R.id.consignmentStockLayout);
		consignmentStockTakeLayout = (LinearLayout) findViewById(R.id.consignmentStockTakeLayout);
		consignment_btn=(Button) findViewById(R.id.consignment);
        consignment_return_btn=(Button)findViewById(R.id.consignmentReturn);
        consignment_stock_btn =(Button)findViewById(R.id.consignmentStock);
		consignment_stock_take_btn =(Button)findViewById(R.id.consignmentStockTake);
        cashinvoiceLayout = (LinearLayout) findViewById(R.id.cashinvoiceLayout);
		maualStockLayout = (LinearLayout)findViewById(R.id.manualStockLayout);
        quicktransfer_space = (LinearLayout)findViewById(R.id.quickTransfer_space);
        quicktransferLayout = (LinearLayout)findViewById(R.id.quicktransferLayout);
		settlementLayout = (LinearLayout)findViewById(R.id.settlementLayout);
		settlement_spae = (LinearLayout)findViewById(R.id.settlement_space);
		settlement_btn = (Button)findViewById(R.id.settlement);

		goodsReceive_space = (LinearLayout) findViewById(R.id.goodsReceive_space);
		deliveryOrder_space = (LinearLayout) findViewById(R.id.deliveryOrder_space);
		salesReturn_space = (LinearLayout) findViewById(R.id.salesReturn_space);
		salesOrder_space = (LinearLayout) findViewById(R.id.salesOrder_space);
		productList_space = (LinearLayout) findViewById(R.id.productList_space);
		customerList_space = (LinearLayout) findViewById(R.id.customerList_space);
		catalog_space = (LinearLayout) findViewById(R.id.catalog_space);
		receipts_space = (LinearLayout) findViewById(R.id.receipts_space);
		invoice_space = (LinearLayout) findViewById(R.id.invoice_space);
		stockrequest_space = (LinearLayout) findViewById(R.id.stockrequest_space);
		transfer_space = (LinearLayout) findViewById(R.id.transfer_space);
		stockTake_space = (LinearLayout) findViewById(R.id.stockTake_space);
		stockAdjustment_space= (LinearLayout) findViewById(R.id.stockAdjustment_space);
		switch_to_offline_space = (LinearLayout) findViewById(R.id.switch_to_offline_space);
		switch_to_online_space = (LinearLayout) findViewById(R.id.switch_to_online_space);
		upload_space = (LinearLayout) findViewById(R.id.upload_space);
		routeMaster_space = (LinearLayout) findViewById(R.id.routeMaster_space);
		expense_space = (LinearLayout) findViewById(R.id.expense_space);
		overdue_space = (LinearLayout) findViewById(R.id.overdue_space);
		task_space = (LinearLayout) findViewById(R.id.task_space);
		merchandise_space = (LinearLayout) findViewById(R.id.merchandise_space);
		merchandise_schedule_space = (LinearLayout) findViewById(R.id.merchandiseSchedule_space);
		deliveryVerification_space = (LinearLayout) findViewById(R.id.deliveryVerification_space); 
		packed_space = (LinearLayout) findViewById(R.id.packed_space);
		merchandisebrand_layout = (LinearLayout) findViewById(R.id.merchandiseBrandLayout);
		merchandisebrand_btn = (Button) findViewById(R.id.merchandiseBrand);
		manualStcok_space = (LinearLayout)findViewById(R.id.manualStcok_space);
		merchandisebrand_layout.setVisibility(View.GONE);

		cashinvoice_space = (LinearLayout) findViewById(R.id.cashinvoice_space);

		attendance_layout  = (LinearLayout) findViewById(R.id.attendance_Layout);

		goodsReceive = (Button) findViewById(R.id.goodsReceive);
		deliveryOrder = (Button) findViewById(R.id.deliveryOrder);
		salesOrder = (Button) findViewById(R.id.salesOrder);
		salesReturn = (Button) findViewById(R.id.salesReturn);
		productList = (Button) findViewById(R.id.productList);
		customerList = (Button) findViewById(R.id.customerList);
		catalog = (Button) findViewById(R.id.catalog);
		receipts = (Button) findViewById(R.id.receipts);
		settings = (Button) findViewById(R.id.settings);
		invoice = (Button) findViewById(R.id.invoice);
		stockrequest = (Button) findViewById(R.id.stockrequest);
		transfer = (Button) findViewById(R.id.transfer);
		stockTake = (Button) findViewById(R.id.stockTake);
		stockAdjustment = (Button) findViewById(R.id.stockAdjustment);
		switch_to_online = (Button) findViewById(R.id.switch_to_online);
		switch_to_offline = (Button) findViewById(R.id.switch_to_offline);
		upload = (Button) findViewById(R.id.upload);
		routeMaster = (Button) findViewById(R.id.routeMaster);
		manualStock = (Button) findViewById(R.id.manualStock);
		expense = (Button) findViewById(R.id.expense);
		overdue = (Button) findViewById(R.id.overdue);
		task = (Button) findViewById(R.id.task);
		merchandise = (Button) findViewById(R.id.merchandise);
		merchandiseSchedule = (Button) findViewById(R.id.merchandiseSchedule);
		deliveryVerification = (Button) findViewById(R.id.deliveryVerification);
		packed = (Button) findViewById(R.id.packed);
		productanalysis = (Button) findViewById(R.id.productanalysis);
		quicktransfer = (Button)findViewById(R.id.quickTransfer);

		cashinvoice = (Button) findViewById(R.id.cashinvoice);

		common_map_btn = (Button) findViewById(R.id.common_map_btn);
		attendance_btn = (Button) findViewById(R.id.attendance_btn);

		appmsg_layout = (LinearLayout) findViewById(R.id.appmsg_layout);
		img_notification = (ImageButton) findViewById(R.id.img_notification);
		msg_count = (TextView) findViewById(R.id.msg_count);

		logo = (ImageView) findViewById(R.id.imageView1);
		img_overdue = (ImageView) findViewById(R.id.img_overdue);
		img_dashboard = (ImageView) findViewById(R.id.img_dashboard);

		img_overflow = (ImageView) findViewById(R.id.img_overflow);

		deliveryOrderNew = (Button) findViewById(R.id.deliveryOrdernew);
		deliveryOrderNewLayout = (LinearLayout) findViewById(R.id.deliveryOrdernewLayout);
		deliveryOrderNewLayout.setVisibility(View.GONE);
		preference = new SharedPreference(LandingActivity.this);

		landingMenuArr.clear();


		/*int versionCode = BuildConfig.VERSION_CODE;

		String app_versioncode = String.valueOf(versionCode);

		String androidVersionSFA = Company.getAndroidVersion_SFA();

		if(androidVersionSFA!=null && !androidVersionSFA.isEmpty()){

		}else{
			androidVersionSFA="";
		}

		if(!androidVersionSFA.matches(app_versioncode)){
			final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
			}
		}*/

		cashinvoiceStr = SalesOrderSetGet.getCustomerHaveCashbill();

		Log.d("cashinvoiceStr","cc "+cashinvoiceStr);

		if(cashinvoiceStr!=null && !cashinvoiceStr.isEmpty()){

		}else{
			cashinvoiceStr="";
		}

		deliveryOrderNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent i = new Intent(LandingActivity.this,
//						DeliveryOrderNewHeader.class);
//				startActivity(i);
//				LandingActivity.this.finish();
			}
		});


		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String dbUsertracking = (String) getIntent().getSerializableExtra("dbUsertracking");
			Log.d("dbUsertracking", "-> "+ dbUsertracking);
			if(dbUsertracking != null && !dbUsertracking.isEmpty()){
        		startService(new Intent(LandingActivity.this, AppLocationService.class));
			}
		}

		FWMSSettingsDatabase.init(LandingActivity.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		offlineCommon = new OfflineCommon(this,valid_url);
		offlineCommon = new OfflineCommon(this);
		new SalesOrderWebService(valid_url);
		OfflineDatabase.init(LandingActivity.this);
		DBCatalog.init(LandingActivity.this);
		checkOffline = OfflineCommon.isConnected(this);
		Log.d("checkOffline", " :"+checkOffline);
		String logotitlename = SalesOrderSetGet.getGeneralsetting();
		Bitmap bitmap = LogOutSetGet.getBitmap();
		try {
			if (logotitlename.matches("SOT")) {

				if (bitmap != null) {
					logo.setImageBitmap(bitmap);
				} else {
//					helper.showLongToast(R.string.error_showing_image);
				}

				pageTitle.setText("ezySalesOrder");
			}
			if (logotitlename.matches("WareHouse")) {

				if (bitmap != null) {
					logo.setImageBitmap(bitmap);
				} else {
					logo.setImageResource(R.mipmap.logo);
				}

				pageTitle.setText("Frozen Management");
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
		}

		mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
//		mobileHaveOfflineMode="0";

		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){

		}else{
			mobileHaveOfflineMode="";
		}

//		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			if(mobileHaveOfflineMode.matches("1")){
				loadingMenus();
			}else{
				loadingOnlineMenus();
			}
//		}else {
//			loadingOnlineMenus();
//		}
		
		Logout = (ImageView) findViewById(R.id.img_logout);
		Logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

//				LogOutSetGet.setActive(true);
//				Intent i = new Intent(LandingActivity.this, LoginActivity.class);
//				startActivity(i);
//				LandingActivity.this.finish();
				
				alertDialog();
			}
		});
		merchandiseSchedule.setOnClickListener(new OnClickListener() {
			   @Override
			   public void onClick(View arg0) {
			     Intent i = new Intent(LandingActivity.this, MerchandiseSchedule.class);
				   Product.setPath("");
				   Product.setHeaderpath("");
			     startActivity(i);
			     LandingActivity.this.finish();
			   }
			  });
		merchandise.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
					Intent i = new Intent(LandingActivity.this, MerchandiseHeader.class);
				    Product.setPath("");
				    Product.setHeaderpath("");
					startActivity(i);
					LandingActivity.this.finish();
			}
		});

		merchandisebrand_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(LandingActivity.this, MerchandiseBrand.class);
				Product.setPath("");
				Product.setHeaderpath("");
				startActivity(i);
				LandingActivity.this.finish();
			}
		});

		consignment_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(LandingActivity.this,ConsignmentHeader.class);
				Product.setPath("");
				Product.setHeaderpath("");
				startActivity(i);
				finish();
			}
		});


		consignment_stock_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(LandingActivity.this,ConsignmentStockHeader.class);
				Product.setPath("");
				Product.setHeaderpath("");
				startActivity(i);
				finish();
			}
		});


		
		img_overdue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(LandingActivity.this, OverdueHeader.class);
				startActivity(i);
				LandingActivity.this.finish();
			
			}
		});
		img_overflow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopup(v);
			}
		});

		img_notification.setOnClickListener(new OnClickListener() {
			   @Override
			   public void onClick(View arg0) {

			    Intent i = new Intent(LandingActivity.this, AppMessage.class);
			    startActivity(i);
			    LandingActivity.this.finish();
			    
			   }
			  });
		
		routeMaster.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
					Intent i = new Intent(LandingActivity.this, RouteHeader.class);
					startActivity(i);
					LandingActivity.this.finish();
			}
		});

		manualStock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LandingActivity.this, ManualStockHeader.class);
				startActivity(i);
				LandingActivity.this.finish();
			}
		});

        quicktransfer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LandingActivity.this, QuickTransferHeader.class);
                startActivity(i);
                LandingActivity.this.finish();
            }
        });

        settlement_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LandingActivity.this, SettlementMainHeader.class);
				startActivity(i);
				LandingActivity.this.finish();
			}
		});
		
		goodsReceive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				String generalsetting = SalesOrderSetGet.getGeneralsetting();
//				if (generalsetting.matches("WareHouse")) {
//					Intent i = new Intent(LandingActivity.this,
//							StockInActivity.class);
//					startActivity(i);
//					LandingActivity.this.finish();
//				}
//				if (generalsetting.matches("SOT")) {
					Intent i = new Intent(LandingActivity.this, GraHeader.class);
					startActivity(i);
					LandingActivity.this.finish();
//				}
			}
		});

		settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this, Settings.class);
				startActivity(i);
				LandingActivity.this.finish();

			}
		});


		salesOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				shareImage();

				Intent i = new Intent(LandingActivity.this,
						SalesOrderHeader.class);
				Product.setPath("");
				Product.setHeaderpath("");
				startActivity(i);
				LandingActivity.this.finish();

			}
		});
		
		packed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(LandingActivity.this,
						PackingHeader.class);
				startActivity(i);
				LandingActivity.this.finish();

			}
		});
		
		deliveryVerification.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(LandingActivity.this,
						DeliveryVerificationHeader.class);
				startActivity(i);
				LandingActivity.this.finish();

			}
		});
		
		deliveryOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				String mobileDoShowRoute = SalesOrderSetGet.getMobileDoShowRoute();

				if(mobileDoShowRoute!=null && !mobileDoShowRoute.isEmpty()){

				}else{
					mobileDoShowRoute="";
				}

				if(mobileDoShowRoute.matches("1")){
					Intent i = new Intent(LandingActivity.this,
							DeliveryOrderNewHeader.class);
					startActivity(i);
					LandingActivity.this.finish();
				}else{
					Intent i = new Intent(LandingActivity.this,
							DeliveryOrderHeader.class);
					Product.setPath("");
					Product.setHeaderpath("");
					startActivity(i);
					LandingActivity.this.finish();
				}
			}
		});
		invoice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this, InvoiceHeader.class);
				Product.setPath("");
				Product.setHeaderpath("");
				startActivity(i);
				LandingActivity.this.finish();

			}
		});

		cashinvoice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this, CashInvoiceHeader.class);
				Product.setPath("");
				Product.setHeaderpath("");
				startActivity(i);
				LandingActivity.this.finish();
			}
		});

		salesReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this,
						SalesReturnHeader.class);
				Product.setPath("");
				Product.setHeaderpath("");
				startActivity(i);
				LandingActivity.this.finish();

			}
		});

		productList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this,
						ProductStockActivity.class);
				startActivity(i);
				LandingActivity.this.finish();

			}
		});

		productanalysis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this,
						ProductAnalysisActivity.class);
				startActivity(i);
				LandingActivity.this.finish();

			}
		});
		receipts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this, ReceiptHeader.class);
				startActivity(i);
				LandingActivity.this.finish();

			}
		});

		catalog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Catalog.setCustomerCode("");
				Catalog.setCustomerName("");
				Catalog.setCustomerGroupCode("");
				DBCatalog.deleteAllProduct();
				Intent i = new Intent(LandingActivity.this,SOCatalogActivity.class);
				startActivity(i);
				LandingActivity.this.finish();
				/*if (screenInches > 7) {
					Log.d("CatalogLandActivity", "CatalogLandActivity");
					Intent i = new Intent(LandingActivity.this,
							CatalogTabMainActivity.class);
					startActivity(i);
					// LandingActivity.this.finish();
				} else {
					Log.d("CatalogActivity", "CatalogActivity");
					Intent i = new Intent(LandingActivity.this,
							CatalogActivity.class);
					startActivity(i);
					LandingActivity.this.finish();
				}*/

			}
		});

		customerList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this,
						CustomerListActivity.class);
				startActivity(i);
				LandingActivity.this.finish();
			}
		});

		stockrequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this,
						StockRequestHeader.class);

				startActivity(i);

			}
		});

		transfer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this,
						TransferHeader.class);

				startActivity(i);
				// LandingActivity.this.finish();
			}
		});
		stockTake.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this,
						StockTakeHeader.class);

				startActivity(i);
				finish();
			}
		});
		
		stockAdjustment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this,
						StockAdjustmentHeader.class);

				startActivity(i);
				finish();
			}
		});
		
		expense.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this,
						ExpenseHeader.class);

				startActivity(i);
				finish();
			}
		});
		
		overdue.setOnClickListener(new OnClickListener() {

			   @Override
			   public void onClick(View v) {
			    // TODO Auto-generated method stub
			    Intent i = new Intent(LandingActivity.this,OverdueHeader.class);
			    startActivity(i);
			    finish();
			   }
			  });
		
		task.setOnClickListener(new OnClickListener() {

			   @Override
			   public void onClick(View v) {
			    // TODO Auto-generated method stub
			    Intent i = new Intent(LandingActivity.this,CRMTaskActivity.class);
			    startActivity(i);
			    finish();
			   }
			  });
		
		
		img_dashboard.setOnClickListener(new OnClickListener() {

			   @Override
			   public void onClick(View v) {
			    // TODO Auto-generated method stub
			    Intent i = new Intent(LandingActivity.this,DashboardActivity.class);
			    startActivity(i);
			    finish();
			   }
			  });

		common_map_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this,MapsHome.class);
				startActivity(i);
				finish();
			}
		});

		attendance_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LandingActivity.this,AttendanceActivity.class);
				startActivity(i);
				finish();
			}
		});

		upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkOffline = OfflineCommon.isConnected(LandingActivity.this);
				if (checkOffline == false) {
					offlineCommon.uploadSyncAlertDialog();
					boolean dialogStatus = offlineCommon.showDialog();
					Log.d("DialogStatus", "" + dialogStatus);
					
				}else{
					Toast.makeText(LandingActivity.this, "No Internet",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		switch_to_offline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				checkOffline = OfflineCommon.isConnected(LandingActivity.this);
				if (checkOffline == false) {
					offlineCommon.switchto_OfflineAlertDialog();
					boolean dialogStatus = offlineCommon.showDialog();
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus == true) {
						loadingMenus();
					} else {
						Log.d("False", "False");
					}
				}else{
					Toast.makeText(LandingActivity.this, "No Internet",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		switch_to_online.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				checkOffline = OfflineCommon.isConnected(LandingActivity.this);
				if (checkOffline == false) {
					offlineCommon.switchto_OnlineAlertDialog();
					boolean dialogStatus = offlineCommon.showDialog();
					Log.d("DialogStatus", "" + dialogStatus);

					if (dialogStatus == true) {
						OfflineDatabase.updateInternetMode("OfflineDialog","false");
						OfflineDatabase.updateInternetMode("OnlineDialog","true");
						loadingMenus();
					} else {
						Log.d("False", "False");
					}
				}else{
					Toast.makeText(LandingActivity.this, "No Internet",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private void shareImage() {

		Intent share = new Intent(Intent.ACTION_SEND);

		// If you want to share a png image only, you can do:
		// setType("image/png"); OR for jpeg: setType("image/jpeg");
		share.setType("image/*");

		// Make sure you put example png image named myImage.png in your
		// directory
		String imagePath = Environment.getExternalStorageDirectory()
				+ "/myImage.png";

		File imageFileToShare = new File(imagePath);

		Uri uri = Uri.fromFile(imageFileToShare);
		share.putExtra(Intent.EXTRA_STREAM, uri);

		startActivity(Intent.createChooser(share, "Share Image!"));
	}


	// Display anchored popup menu based on view selected
	private void showPopup(View v) {
		//ContextThemeWrapper ctw = new ContextThemeWrapper(this, R.style.popupMenuStyle);
		//	PopupMenu popup = new PopupMenu(ctw, v);
		PopupMenu popup = new PopupMenu(this, v);
		// Inflate the menu from xml
		popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

		// Force icons to show
		Object menuHelper;
		Class[] argTypes;
		try {
			Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
			fMenuHelper.setAccessible(true);
			menuHelper = fMenuHelper.get(popup);
			argTypes = new Class[] { boolean.class };
			menuHelper.getClass()
					.getDeclaredMethod("setForceShowIcon", argTypes)
					.invoke(menuHelper, true);
		} catch (Exception e) {

			Log.w("TAG", "error forcing menu icons to show", e);
			popup.show();
			return;
		}

		MenuItem overDue = popup.getMenu().findItem(R.id.overDue);
		//MenuItem language = popup.getMenu().findItem(R.id.language);
        if(showOverDue){

			String overdueStr =  FormSetterGetter.getOverdue();
			if (overdueStr.matches("Overdue Invoices")) {
				overDue.setVisible(true);
			}else{
				overDue.setVisible(false);
			}

        }else{
			overDue.setVisible(false);
        }

		// Setup menu item selection
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.overDue:
						Intent i = new Intent(LandingActivity.this,OverdueHeader.class);
						startActivity(i);
						finish();
						return true;
					case R.id.language:
						languageDialog();
						return true;


					default:
						return false;
				}
			}

		});
		// Handle dismissal with: popup.setOnDismissListener(...);
		// Show the menu
		popup.show();
	}

	private void languageDialog() {
		final Dialog mDialog = new Dialog(LandingActivity.this);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setContentView(R.layout.dialog_language);

		mDialog.setCancelable(false);
		mDialog.show();
		Button	mOK = (Button) mDialog.findViewById(R.id.btnok);
		ImageView	mClose = (ImageView) mDialog.findViewById(R.id.close);
		final RadioGroup radioGroup = (RadioGroup) mDialog.findViewById(R.id.rdbGp1);
		RadioButton radiobtn1 = (RadioButton) mDialog.findViewById(R.id.rdb1);
		RadioButton radiobtn2 = (RadioButton) mDialog.findViewById(R.id.rdb2);
		mClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		mOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int selected = radioGroup.getCheckedRadioButtonId();
				RadioButton b = (RadioButton) mDialog.findViewById(selected);
				String mLanguage = b.getText().toString();
				preference.putLanguage(mLanguage);
				Resources res = getResources();
				DisplayMetrics dm = res.getDisplayMetrics();
				Configuration conf = res.getConfiguration();
				conf.locale = preference.getLanguageLocale();
				res.updateConfiguration(conf, dm);
				Intent mIntent = getIntent();
				finish();
				startActivity(mIntent);
			}
		});

		if (getResources().getString(R.string.english).equals(
				preference.getLanguage())) {

			radiobtn1.setChecked(true);
			radiobtn2.setChecked(false);

		} else if (getResources().getString(R.string.chinese).equals(
				preference.getLanguage())) {

			radiobtn1.setChecked(false);
			radiobtn2.setChecked(true);
		} else {
			radiobtn1.setChecked(true);
		}
	}

	public void loadingOnlineMenus(){
		landingMenuArr.clear();
		//
		merchandiseScheduleStr =FormSetterGetter.getMerchandiseSchedule();
		merchandiseStr =FormSetterGetter.getMerchandise();
		routeMasterStr = FormSetterGetter.getRouteMaster();
		goodsReceiveStr = FormSetterGetter.getGoodsReceive();
		salesOrderStr = FormSetterGetter.getSalesOrder();
		packedStr = "Packing";
		deliveryVerificationStr = "Delivery Verification";
		deliveryOrderStr = FormSetterGetter.getDeliveryOrder();
		consignmentStr=FormSetterGetter.getConsignment();
        consignmentReturnStr=FormSetterGetter.getConsignmentReturn();
		consignmentStockStr=FormSetterGetter.getConsignmentStock();
		consignmentStockTakeStr =FormSetterGetter.getConsignmentStockTake();
		invoiceStr = FormSetterGetter.getInvoice();
		salesReturnStr = FormSetterGetter.getSalesReturn();
		receiptsStr = FormSetterGetter.getReceipts();
		customerListStr = FormSetterGetter.getCustomerList();
		productListStr = FormSetterGetter.getProductList();
		productAnalysisStr = FormSetterGetter.getProductAnalysis();
		catalogStr = FormSetterGetter.getCatalog();
		settingsStr = FormSetterGetter.getSettings();
		stockrequestStr = FormSetterGetter.getStockRequest();
		transferStr = FormSetterGetter.getTransfer();
		stockTakeStr = FormSetterGetter.getStockTake();
		stockAdjustmentStr = FormSetterGetter.getStockAdjustment();
		expenseStr = FormSetterGetter.getExpense();
		overdueStr =  FormSetterGetter.getOverdue();
		taskStr =FormSetterGetter.getTasks();
		manualStockStr = FormSetterGetter.getManualStock();
        quicktransferstr = FormSetterGetter.getQuickTransfer();
		settlementStr = FormSetterGetter.getSettlement();
		Log.d("manualStockStr","-->"+manualStockStr+"quicktransferstr:"+quicktransferstr);

		//online
		switch_to_offlineLayout.setVisibility(View.GONE);
		switch_to_offline_space.setVisibility(View.GONE);
		switch_to_onlineLayout.setVisibility(View.GONE);
		switch_to_online_space.setVisibility(View.GONE);
		upload_Layout.setVisibility(View.GONE);
		upload_space.setVisibility(View.GONE);

		//created on 15/09/17 by arunkumar
		onlineMode = OfflineDatabase.getOnlineMode();
		offlineModeStr =FormSetterGetter.getOfflineMode();
//		offlineModeStr = "";
        if(mobileHaveOfflineMode.matches("1")) {
            if (offlineModeStr.matches("Offline Mode")) {

                if (onlineMode.matches("True")) {
                    switch_to_offlineLayout.setVisibility(View.VISIBLE);
                    switch_to_offline_space.setVisibility(View.VISIBLE);
                } else {
                    switch_to_offlineLayout.setVisibility(View.GONE);
                    switch_to_offline_space.setVisibility(View.GONE);
                }
//			landingMenuArr.add("Offline Mode");
            } else {
                switch_to_offlineLayout.setVisibility(View.GONE);
                switch_to_offline_space.setVisibility(View.GONE);
            }
        }else{
            switch_to_offlineLayout.setVisibility(View.GONE);
            switch_to_offline_space.setVisibility(View.GONE);
        }

		if (merchandiseScheduleStr.matches("Merchandise Schedule")) {
			String haveMerchandising = SalesOrderSetGet.getHaveMerchandising();
			if(haveMerchandising.matches("1")){
				merchandiseScheduleLayout.setVisibility(View.VISIBLE);
				merchandise_schedule_space.setVisibility(View.VISIBLE);
				landingMenuArr.add("Merchandise Schedule");
			} else{
				merchandiseScheduleLayout.setVisibility(View.GONE);
				merchandise_schedule_space.setVisibility(View.GONE);
			}

		} else{
			merchandiseScheduleLayout.setVisibility(View.GONE);
			merchandise_schedule_space.setVisibility(View.GONE);
		}
		if (merchandiseStr.matches("Merchandise")) {
			String haveMerchandising = SalesOrderSetGet.getHaveMerchandising();
			if(haveMerchandising.matches("1")){
				merchandiseLayout.setVisibility(View.VISIBLE);
				merchandise_space.setVisibility(View.VISIBLE);
				landingMenuArr.add("Merchandise");
			} else{
				merchandiseLayout.setVisibility(View.GONE);
				merchandise_space.setVisibility(View.GONE);
			}

		} else{
			merchandiseLayout.setVisibility(View.GONE);
			merchandise_space.setVisibility(View.GONE);
		}

		if (routeMasterStr.matches("Route")) {
//			String routePermission = SalesOrderSetGet.getRoutepermission();
//			if(routePermission.matches("True")){
//				routeMasterLayout.setVisibility(View.VISIBLE);
//				routeMaster_space.setVisibility(View.VISIBLE);
//				landingMenuArr.add("Route");
//			}else{
////				routeMasterLayout.setVisibility(View.GONE);
////				routeMaster_space.setVisibility(View.GONE);
//
//				routeMasterLayout.setVisibility(View.VISIBLE);
//				routeMaster_space.setVisibility(View.VISIBLE);
//			}
			routeMasterLayout.setVisibility(View.VISIBLE);
			routeMaster_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Route");
		}
		else{
			routeMasterLayout.setVisibility(View.GONE);
			routeMaster_space.setVisibility(View.GONE);
		}



		if (goodsReceiveStr.matches("Goods Receive")) {
			goodsReceiveLayout.setVisibility(View.VISIBLE);
			goodsReceive_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Goods Receive");
		} else{
			goodsReceiveLayout.setVisibility(View.GONE);
			goodsReceive_space.setVisibility(View.GONE);
		}

		if (salesOrderStr.matches("Sales Order")) {
			salesOrderLayout.setVisibility(View.VISIBLE);
			salesOrder_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Sales Order");
		}else{
			salesOrderLayout.setVisibility(View.GONE);
			salesOrder_space.setVisibility(View.GONE);
		}

		if (packedStr.matches("Packing")) {
			String haveDeliveryVerification = MobileSettingsSetterGetter.getHaveDeliveryVerification();
			if(haveDeliveryVerification.matches("True")){
				packedLayout.setVisibility(View.VISIBLE);
				packed_space.setVisibility(View.VISIBLE);
				landingMenuArr.add("Packing");
			}else{
				packedLayout.setVisibility(View.GONE);
				packed_space.setVisibility(View.GONE);
			}

		}else{
			packedLayout.setVisibility(View.GONE);
			packed_space.setVisibility(View.GONE);
		}

		if (deliveryVerificationStr.matches("Delivery Verification")) {
			String haveDeliveryVerification = MobileSettingsSetterGetter.getHaveDeliveryVerification();
			if(haveDeliveryVerification.matches("True")){
				deliveryVerificationLayout.setVisibility(View.VISIBLE);
				deliveryVerification_space.setVisibility(View.VISIBLE);
				landingMenuArr.add("Delivery Verification");
			}else{
				deliveryVerificationLayout.setVisibility(View.GONE);
				deliveryVerification_space.setVisibility(View.GONE);
			}

		}else{
			deliveryVerificationLayout.setVisibility(View.GONE);
			deliveryVerification_space.setVisibility(View.GONE);
		}

		if (deliveryOrderStr.matches("Delivery Order")) {
			deliveryOrderLayout.setVisibility(View.VISIBLE);
			deliveryOrder_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Delivery Order");
		}else{
			deliveryOrderLayout.setVisibility(View.GONE);
			deliveryOrder_space.setVisibility(View.GONE);
		}

		if (invoiceStr.matches("Invoice")) {
			invoiceLayout.setVisibility(View.VISIBLE);
			invoice_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Invoice");
		}else{
			invoiceLayout.setVisibility(View.GONE);
			invoice_space.setVisibility(View.GONE);
		}
		if (salesReturnStr.matches("Sales Return")) {
			salesReturnLayout.setVisibility(View.VISIBLE);
			salesReturn_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Sales Return");
		}else{
			salesReturnLayout.setVisibility(View.GONE);
			salesReturn_space.setVisibility(View.GONE);
		}

		if (receiptsStr.matches("Receipts")) {
			receiptsLayout.setVisibility(View.VISIBLE);
			receipts_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Receipts");
		}else{
			receiptsLayout.setVisibility(View.GONE);
			receipts_space.setVisibility(View.GONE);
		}

		if (customerListStr.matches("Customer List")) {
			customerListLayout.setVisibility(View.VISIBLE);
			customerList_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Customer List");
		}else{
			customerListLayout.setVisibility(View.GONE);
			customerList_space.setVisibility(View.GONE);
		}

		if (productListStr.matches("Product Stock")) {
			productListLayout.setVisibility(View.VISIBLE);
			productList_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Product Stock");
		}else{
			productListLayout.setVisibility(View.GONE);
			productList_space.setVisibility(View.GONE);
		}

		if (productAnalysisStr.matches("Product Analysis")) {
			productanalysisLayout.setVisibility(View.VISIBLE);
			landingMenuArr.add("Product Analysis");
		}else{
			productanalysisLayout.setVisibility(View.GONE);
		}

		if (stockrequestStr.matches("Stock Request")) {
			stockrequestLayout.setVisibility(View.VISIBLE);
			stockrequest_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Stock Request");
		}else{
			stockrequestLayout.setVisibility(View.GONE);
			stockrequest_space.setVisibility(View.GONE);
		}

		if (transferStr.matches("Transfer")) {
			transferLayout.setVisibility(View.VISIBLE);
			transfer_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Transfer");
		}else{
			transferLayout.setVisibility(View.GONE);
			transfer_space.setVisibility(View.GONE);
		}

		if (stockTakeStr.matches("Stock Take")) {
			stockTakeLayout.setVisibility(View.VISIBLE);
			stockTake_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Stock Take");
		}else{
			stockTakeLayout.setVisibility(View.GONE);
			stockTake_space.setVisibility(View.GONE);
		}

		if (stockAdjustmentStr.matches("Stock Adjustment")) {
			stockAdjustmentLayout.setVisibility(View.VISIBLE);
			stockAdjustment_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Stock Adjustment");
		}else{
			stockAdjustmentLayout.setVisibility(View.GONE);
			stockAdjustment_space.setVisibility(View.GONE);
		}

		if (expenseStr.matches("Expense")) {
			expenseLayout.setVisibility(View.VISIBLE);
			expense_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Expense");
		}else{
			expenseLayout.setVisibility(View.GONE);
			expense_space.setVisibility(View.GONE);
		}

		if (overdueStr.matches("Overdue Invoices")) {
			overdue_Layout.setVisibility(View.VISIBLE);
			overdue_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Overdue Invoices");

		}else{
			overdue_Layout.setVisibility(View.GONE);
			overdue_space.setVisibility(View.GONE);
		}

		dashboardStr = FormSetterGetter.isShowDashboard();
		if (dashboardStr==true) {
			dashboardimg_layout.setVisibility(View.VISIBLE);
		}else{
			dashboardimg_layout.setVisibility(View.GONE);
		}

		if (taskStr.matches("Tasks")) {
			task_Layout.setVisibility(View.VISIBLE);
			task_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Task");
		}else{
			task_Layout.setVisibility(View.GONE);
			task_space.setVisibility(View.GONE);
		}

		if (catalogStr.matches("Catalog")) {
			catalogLayout.setVisibility(View.VISIBLE);
			catalog_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Catalog");
		}else{
			catalogLayout.setVisibility(View.GONE);
			catalog_space.setVisibility(View.GONE);
		}

		if (settingsStr.matches("Settings")) {
			settingsLayout.setVisibility(View.VISIBLE);
			landingMenuArr.add("Settings");
		}else{
			settingsLayout.setVisibility(View.GONE);
		}
		landingMenuArr.add("Exit");
		if(consignmentStr.matches("Consignment")){
			consignmentLayout.setVisibility(View.VISIBLE);
			consignment_btn.setVisibility(View.VISIBLE);
			landingMenuArr.add("Consignment");
		}else{
			consignmentLayout.setVisibility(View.GONE);
			consignment_btn.setVisibility(View.GONE);
		}
		if(consignmentReturnStr.matches("ConsignmentReturn")){
			consignmentReturnLayout.setVisibility(View.VISIBLE);
			consignment_return_btn.setVisibility(View.VISIBLE);
//			landingMenuArr.add("ConsignmentReturn");
		}else{
			consignmentReturnLayout.setVisibility(View.GONE);
			consignment_return_btn.setVisibility(View. GONE);
		}


		if(consignmentStockStr.matches("Consignment Stock")){
			consignmentStockLayout.setVisibility(View.VISIBLE);
			consignment_stock_btn.setVisibility(View.VISIBLE);
//			landingMenuArr.add("ConsignmentStock");
		}else{
			consignmentStockLayout.setVisibility(View.GONE);
			consignment_stock_btn.setVisibility(View.GONE);
		}
		if(consignmentStockTakeStr.matches("ConsignmentStockTake")){
			consignmentStockTakeLayout.setVisibility(View.VISIBLE);
			consignment_stock_take_btn.setVisibility(View.VISIBLE);
//			landingMenuArr.add("ConsignmentStockTake");
		}else{
			consignmentStockTakeLayout.setVisibility(View.GONE);
			consignment_stock_take_btn.setVisibility(View.GONE);
		}
		if (cashinvoiceStr.matches("1")) {
			cashinvoiceLayout.setVisibility(View.VISIBLE);
			cashinvoice_space.setVisibility(View.VISIBLE);
//			landingMenuArr.add("Cash Invoice");
		}else{
			cashinvoiceLayout.setVisibility(View.GONE);
			cashinvoice_space.setVisibility(View.GONE);
		}

		if(settlementStr.matches("Settlement")){
			settlementLayout.setVisibility(View.VISIBLE);
			settlement_spae.setVisibility(View.VISIBLE);
			landingMenuArr.add("Settlement");
		}else {
			settlementLayout.setVisibility(View.GONE);
			settlement_spae.setVisibility(View.GONE);
		}

		if(manualStockStr.matches("Manual Stock")){
			manualStcok_space.setVisibility(View.VISIBLE);
			maualStockLayout.setVisibility(View.VISIBLE);
			landingMenuArr.add("Manual Stock");
		}else{
			manualStcok_space.setVisibility(View.GONE);
			maualStockLayout.setVisibility(View.GONE);
		}

		if(quicktransferstr.matches("Quick Transfer")){
			quicktransfer_space.setVisibility(View.VISIBLE);
			quicktransferLayout.setVisibility(View.VISIBLE);
			landingMenuArr.add("Quick Transfer");
		}else{
			quicktransfer_space.setVisibility(View.GONE);
			quicktransferLayout.setVisibility(View.GONE);
		}


		SalesOrderSetGet.setLandingMenuArr(landingMenuArr);
		Log.d("UserGroup", "" + landingMenuArr.toString());
	}
	public void loadingMenus(){
		
		onlineMode = OfflineDatabase.getOnlineMode();
		if (onlineMode.matches("True")) {
			offlineLayout.setVisibility(View.GONE);
			String internetMode = checkInternetStatus();
			Log.d("LandingActivity", "checkOffline :"+checkOffline);
				if (checkOffline == true) { //temp offline
					Log.d("LandingActivity", "internetMode :"+internetMode);
					if (internetMode.matches("true")) {
						offlineLayout.setVisibility(View.VISIBLE);
						offlineLayout.setBackgroundResource(drawable.temp_offline_pattern_bg);
						overdueimg_layout.setVisibility(View.GONE);
						dashboardimg_layout.setVisibility(View.GONE);
						showOverDue = false;
						appmsg_layout.setVisibility(View.GONE);
					}else{
						finish();
					}
				}else{ // online
					offlinemanager = new OfflineSettingsManager(LandingActivity.this);
					appmsg_layout.setVisibility(View.VISIBLE);
					  String showOverdue = offlinemanager.getOverdue();
					   Log.d("showOverdue","-->"+showOverdue);

					   if(showOverdue.matches("true")){
						   overdueimg_layout.setVisibility(View.GONE);
						   dashboardimg_layout.setVisibility(View.VISIBLE);
						   showOverDue = true;
					   }else if(showOverdue.matches("false")){
						   overdueimg_layout.setVisibility(View.GONE);
						   dashboardimg_layout.setVisibility(View.GONE);
						   showOverDue = false;
					   }
					   
				        AppMessageAsyncCall expense = new AppMessageAsyncCall();
				        expense.execute();
				}
		}else{ // perm offline
			overdueimg_layout.setVisibility(View.GONE);
			dashboardimg_layout.setVisibility(View.GONE);
			showOverDue = false;
			offlineLayout.setVisibility(View.VISIBLE);
			appmsg_layout.setVisibility(View.GONE);
			offlineLayout.setBackgroundResource(drawable.offline_pattern_bg);
		}
				
		landingMenuArr.clear();
		//Created on 20/02/17 by saravana
		offlineModeStr =FormSetterGetter.getOfflineMode();
		//
		merchandiseScheduleStr =FormSetterGetter.getMerchandiseSchedule();
		merchandiseStr =FormSetterGetter.getMerchandise();
		routeMasterStr = FormSetterGetter.getRouteMaster();
		goodsReceiveStr = FormSetterGetter.getGoodsReceive();
		salesOrderStr = FormSetterGetter.getSalesOrder();
		packedStr = "Packing";
		deliveryVerificationStr = "Delivery Verification";
		deliveryOrderStr = FormSetterGetter.getDeliveryOrder();
		consignmentStr=FormSetterGetter.getConsignment();
		consignmentReturnStr=FormSetterGetter.getConsignmentReturn();
		consignmentStockStr =FormSetterGetter.getConsignmentStock();
		consignmentStockTakeStr =FormSetterGetter.getConsignmentStockTake();
		invoiceStr = FormSetterGetter.getInvoice();
		salesReturnStr = FormSetterGetter.getSalesReturn();
		receiptsStr = FormSetterGetter.getReceipts();
		customerListStr = FormSetterGetter.getCustomerList();
		productListStr = FormSetterGetter.getProductList();
		productAnalysisStr = FormSetterGetter.getProductAnalysis();
		catalogStr = FormSetterGetter.getCatalog();
		settingsStr = FormSetterGetter.getSettings();
		stockrequestStr = FormSetterGetter.getStockRequest();
		transferStr = FormSetterGetter.getTransfer();
		stockTakeStr = FormSetterGetter.getStockTake();
		stockAdjustmentStr = FormSetterGetter.getStockAdjustment();
		expenseStr = FormSetterGetter.getExpense();
		overdueStr =  FormSetterGetter.getOverdue();
		taskStr =FormSetterGetter.getTasks();
		settlementStr = FormSetterGetter.getSettlement();
		manualStockStr = FormSetterGetter.getManualStock();
		quicktransferstr = FormSetterGetter.getQuickTransfer();
		onlineMode = OfflineDatabase.getOnlineMode();
		if (onlineMode.matches("True")) {
			if (checkOffline == false) { //online

				switch_to_offlineLayout.setVisibility(View.VISIBLE);
				switch_to_offline_space.setVisibility(View.VISIBLE);
				switch_to_onlineLayout.setVisibility(View.GONE);
				switch_to_online_space.setVisibility(View.GONE);
				upload_Layout.setVisibility(View.GONE);
				upload_space.setVisibility(View.GONE);
				
				//crm task
//				task_Layout.setVisibility(View.VISIBLE);
//				task_space.setVisibility(View.VISIBLE);
			} else { //temp
				switch_to_offlineLayout.setVisibility(View.GONE);
				switch_to_offline_space.setVisibility(View.GONE);
				switch_to_onlineLayout.setVisibility(View.VISIBLE);
				switch_to_online_space.setVisibility(View.VISIBLE);
				upload_Layout.setVisibility(View.VISIBLE);
				upload_space.setVisibility(View.VISIBLE);
				
				//crm task
				task_Layout.setVisibility(View.GONE);
				task_space.setVisibility(View.GONE);
				
				merchandiseScheduleStr ="";
				merchandiseStr ="";
				routeMasterStr = "";
				goodsReceiveStr = "";
//				salesOrderStr = "";
				packedStr="";
				deliveryOrderStr = "";
				consignmentStr = "";
				consignmentReturnStr ="";
				consignmentStockStr = "";
				consignmentStockTakeStr="";
//				invoiceStr = "";
				salesReturnStr = "";
//				receiptsStr = "";
				productListStr = "";
				productAnalysisStr = "";
//				catalogStr = "";
			//	settingsStr = "";
				stockrequestStr = "";
				transferStr = "";
				stockTakeStr = "";
				stockAdjustmentStr = "";
				expenseStr="";
				overdueStr="";
				taskStr="";
				deliveryVerificationStr="";
				settlementStr = "";
			}

		} else if (onlineMode.matches("False")) { //perm
			offlineLayout.setVisibility(View.VISIBLE);

			switch_to_offlineLayout.setVisibility(View.GONE);
			switch_to_offline_space.setVisibility(View.GONE);
			switch_to_onlineLayout.setVisibility(View.VISIBLE);
			switch_to_online_space.setVisibility(View.VISIBLE);
			upload_Layout.setVisibility(View.VISIBLE);
			upload_space.setVisibility(View.VISIBLE);

			//crm task
			task_Layout.setVisibility(View.GONE);
			task_space.setVisibility(View.GONE);
			
			merchandiseScheduleStr ="";
			merchandiseStr ="";
			routeMasterStr = "";
			goodsReceiveStr = "";
//			salesOrderStr = "";
			packedStr="";
			deliveryOrderStr = "";
			consignmentStr = "";
			consignmentReturnStr="";
			consignmentStockStr = "";
			consignmentStockTakeStr="";
//			invoiceStr = "";
			salesReturnStr = "";
//			receiptsStr = "";
			productListStr = "";
			productAnalysisStr = "";
//			catalogStr = "";
	//		settingsStr = "";
			stockrequestStr = "";
			transferStr = "";
			stockTakeStr = "";
			stockAdjustmentStr = "";
			expenseStr="";
			overdueStr="";
			taskStr="";
			deliveryVerificationStr="";
			settlementStr = "";
		}
		//created on 28/02/17 by saravana
		if(mobileHaveOfflineMode.matches("1")) {
			if (offlineModeStr.matches("Offline Mode")) {

				if (onlineMode.matches("True")) {
					switch_to_offlineLayout.setVisibility(View.VISIBLE);
					switch_to_offline_space.setVisibility(View.VISIBLE);
				} else {
					switch_to_offlineLayout.setVisibility(View.GONE);
					switch_to_offline_space.setVisibility(View.GONE);
				}

			} else {
				switch_to_offlineLayout.setVisibility(View.GONE);
				switch_to_offline_space.setVisibility(View.GONE);
			}
		}else {
			switch_to_offlineLayout.setVisibility(View.GONE);
			switch_to_offline_space.setVisibility(View.GONE);
		}

		if (merchandiseScheduleStr.matches("Merchandise Schedule")) {
			   String haveMerchandising = SalesOrderSetGet.getHaveMerchandising();
			   if(haveMerchandising.matches("1")){
			       merchandiseScheduleLayout.setVisibility(View.VISIBLE);
			       merchandise_schedule_space.setVisibility(View.VISIBLE);
			       landingMenuArr.add("Merchandise Schedule");
			   } else{
			    merchandiseScheduleLayout.setVisibility(View.GONE);
			    merchandise_schedule_space.setVisibility(View.GONE);
			   }
			      
			  } else{
			   merchandiseScheduleLayout.setVisibility(View.GONE);
			   merchandise_schedule_space.setVisibility(View.GONE);
			  }
		if (merchandiseStr.matches("Merchandise")) {
			String haveMerchandising = SalesOrderSetGet.getHaveMerchandising();
			if(haveMerchandising.matches("1")){
				   merchandiseLayout.setVisibility(View.VISIBLE);
				   merchandise_space.setVisibility(View.VISIBLE);
				   landingMenuArr.add("Merchandise");
			} else{
					merchandiseLayout.setVisibility(View.GONE);
					merchandise_space.setVisibility(View.GONE);
			}
			   
		} else{
			   merchandiseLayout.setVisibility(View.GONE);
			   merchandise_space.setVisibility(View.GONE);
		}
		
		if (routeMasterStr.matches("Route")) {
//			String routePermission = SalesOrderSetGet.getRoutepermission();
//			if(routePermission.matches("True")){
//				routeMasterLayout.setVisibility(View.VISIBLE);
//				routeMaster_space.setVisibility(View.VISIBLE);
//				landingMenuArr.add("Route");
//			}else{
//				routeMasterLayout.setVisibility(View.GONE);
//				routeMaster_space.setVisibility(View.GONE);
//
//				routeMasterLayout.setVisibility(View.VISIBLE);
//				routeMaster_space.setVisibility(View.VISIBLE);
//			}
			routeMasterLayout.setVisibility(View.VISIBLE);
			routeMaster_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Route");
		}
		else{
			routeMasterLayout.setVisibility(View.GONE);
				routeMaster_space.setVisibility(View.GONE);
		}
		
		if (goodsReceiveStr.matches("Goods Receive")) {
			goodsReceiveLayout.setVisibility(View.VISIBLE);
			goodsReceive_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Goods Receive");
		} else{
			goodsReceiveLayout.setVisibility(View.GONE);
			goodsReceive_space.setVisibility(View.GONE);
		}

		if (salesOrderStr.matches("Sales Order")) {
			salesOrderLayout.setVisibility(View.VISIBLE);
			salesOrder_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Sales Order");
		}else{
			salesOrderLayout.setVisibility(View.GONE);
			salesOrder_space.setVisibility(View.GONE);
		}
		
		if (packedStr.matches("Packing")) {
			String haveDeliveryVerification = MobileSettingsSetterGetter.getHaveDeliveryVerification();
			if(haveDeliveryVerification.matches("True")){
				packedLayout.setVisibility(View.VISIBLE);
				packed_space.setVisibility(View.VISIBLE);
				landingMenuArr.add("Packing");
			}else{
				packedLayout.setVisibility(View.GONE);
				packed_space.setVisibility(View.GONE);
			}
			
		}else{
			packedLayout.setVisibility(View.GONE);
			packed_space.setVisibility(View.GONE);
		}
		
		if (deliveryVerificationStr.matches("Delivery Verification")) {
			String haveDeliveryVerification = MobileSettingsSetterGetter.getHaveDeliveryVerification();
			if(haveDeliveryVerification.matches("True")){
				deliveryVerificationLayout.setVisibility(View.VISIBLE);
				deliveryVerification_space.setVisibility(View.VISIBLE);
				landingMenuArr.add("Delivery Verification");
			}else{
				deliveryVerificationLayout.setVisibility(View.GONE);
				deliveryVerification_space.setVisibility(View.GONE);
			}
			
		}else{
			deliveryVerificationLayout.setVisibility(View.GONE);
			deliveryVerification_space.setVisibility(View.GONE);
		}

		if (deliveryOrderStr.matches("Delivery Order")) {
			deliveryOrderLayout.setVisibility(View.VISIBLE);
			deliveryOrder_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Delivery Order");
		}else{
			deliveryOrderLayout.setVisibility(View.GONE);
			deliveryOrder_space.setVisibility(View.GONE);
		}
		if (invoiceStr.matches("Invoice")) {
			invoiceLayout.setVisibility(View.VISIBLE);
			invoice_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Invoice");
		}else{
			invoiceLayout.setVisibility(View.GONE);
			invoice_space.setVisibility(View.GONE);
		}
		if (salesReturnStr.matches("Sales Return")) {
			salesReturnLayout.setVisibility(View.VISIBLE);
			salesReturn_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Sales Return");
		}else{
			salesReturnLayout.setVisibility(View.GONE);
			salesReturn_space.setVisibility(View.GONE);
		}

		if (receiptsStr.matches("Receipts")) {
			receiptsLayout.setVisibility(View.VISIBLE);
			receipts_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Receipts");
		}else{
			receiptsLayout.setVisibility(View.GONE);
			receipts_space.setVisibility(View.GONE);
		}

		if (customerListStr.matches("Customer List")) {
			customerListLayout.setVisibility(View.VISIBLE);
			customerList_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Customer List");
		}else{
			customerListLayout.setVisibility(View.GONE);
			customerList_space.setVisibility(View.GONE);
		}

		if (productListStr.matches("Product Stock")) {
			productListLayout.setVisibility(View.VISIBLE);
			productList_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Product Stock");
		}else{
			productListLayout.setVisibility(View.GONE);
			productList_space.setVisibility(View.GONE);
		}

		if (productAnalysisStr.matches("Product Analysis")) {
			productanalysisLayout.setVisibility(View.VISIBLE);
			landingMenuArr.add("Product Analysis");
		}else{
			productanalysisLayout.setVisibility(View.GONE);
		}

		if (stockrequestStr.matches("Stock Request")) {
			stockrequestLayout.setVisibility(View.VISIBLE);
			stockrequest_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Stock Request");
		}else{
			stockrequestLayout.setVisibility(View.GONE);
			stockrequest_space.setVisibility(View.GONE);
		}

		if (transferStr.matches("Transfer")) {
			transferLayout.setVisibility(View.VISIBLE);
			transfer_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Transfer");
		}else{
			transferLayout.setVisibility(View.GONE);
			transfer_space.setVisibility(View.GONE);
		}

		if (stockTakeStr.matches("Stock Take")) {
			stockTakeLayout.setVisibility(View.VISIBLE);
			stockTake_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Stock Take");
		}else{
			stockTakeLayout.setVisibility(View.GONE);
			stockTake_space.setVisibility(View.GONE);
		}
		
		if (stockAdjustmentStr.matches("Stock Adjustment")) {
			stockAdjustmentLayout.setVisibility(View.VISIBLE);
			stockAdjustment_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Stock Adjustment");
		}else{
			stockAdjustmentLayout.setVisibility(View.GONE);
			stockAdjustment_space.setVisibility(View.GONE);
		}
		
		if (expenseStr.matches("Expense")) {
			expenseLayout.setVisibility(View.VISIBLE);
			expense_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Expense");
		}else{
			expenseLayout.setVisibility(View.GONE);
			expense_space.setVisibility(View.GONE);
		}
		
		if (overdueStr.matches("Overdue Invoices")) {
			   overdue_Layout.setVisibility(View.VISIBLE);
			   overdue_space.setVisibility(View.VISIBLE);
			   landingMenuArr.add("Overdue Invoices");
			  }else{
			   overdue_Layout.setVisibility(View.GONE);
			   overdue_space.setVisibility(View.GONE);
			  }

		dashboardStr = FormSetterGetter.isShowDashboard();
		if (dashboardStr==true) {
			dashboardimg_layout.setVisibility(View.VISIBLE);
		}else{
			dashboardimg_layout.setVisibility(View.GONE);
		}

		if (taskStr.matches("Tasks")) {
			   task_Layout.setVisibility(View.VISIBLE);
			   task_space.setVisibility(View.VISIBLE);
			   landingMenuArr.add("Task");
			  }else{
				  task_Layout.setVisibility(View.GONE);
				  task_space.setVisibility(View.GONE);
			  }
		
		if (catalogStr.matches("Catalog")) {
			catalogLayout.setVisibility(View.VISIBLE);
			catalog_space.setVisibility(View.VISIBLE);
			landingMenuArr.add("Catalog");
		}else{
			catalogLayout.setVisibility(View.GONE);
			catalog_space.setVisibility(View.GONE);
		}

		if (settingsStr.matches("Settings")) {
			settingsLayout.setVisibility(View.VISIBLE);
			landingMenuArr.add("Settings");
		}else{
			settingsLayout.setVisibility(View.GONE);
		}

		landingMenuArr.add("Exit");

		if(consignmentStr.matches("Consignment")){
			consignmentLayout.setVisibility(View.VISIBLE);
			consignment_btn.setVisibility(View.VISIBLE);
			landingMenuArr.add("Consignment");
		}else{
			consignmentLayout.setVisibility(View.GONE);
			consignment_btn.setVisibility(View.GONE);
		}

		if(consignmentReturnStr.matches("ConsignmentReturn")){
			consignmentReturnLayout.setVisibility(View.VISIBLE);
			consignment_return_btn.setVisibility(View.VISIBLE);
//			landingMenuArr.add("ConsignmentReturn");
		}else{
			consignmentReturnLayout.setVisibility(View.GONE);
			consignment_return_btn.setVisibility(View.GONE);
		}

		if(consignmentStockStr.matches("Consignment Stock")){
			consignmentStockLayout.setVisibility(View.VISIBLE);
			consignment_stock_btn.setVisibility(View.VISIBLE);
			//landingMenuArr.add("ConsignmentStock");
		}else{
			consignmentStockLayout.setVisibility(View.GONE);
			consignment_stock_btn.setVisibility(View.GONE);
		}

		if(consignmentStockTakeStr.matches("ConsignmentStockTake")){
			consignmentStockTakeLayout.setVisibility(View.VISIBLE);
			consignment_stock_take_btn.setVisibility(View.VISIBLE);
//			landingMenuArr.add("ConsignmentStockTake");
		}else{
			consignmentStockTakeLayout.setVisibility(View.VISIBLE);
			consignment_stock_take_btn.setVisibility(View.GONE);
		}

		if (cashinvoiceStr.matches("1")) {
			cashinvoiceLayout.setVisibility(View.VISIBLE);
			cashinvoice_space.setVisibility(View.VISIBLE);
//			landingMenuArr.add("Cash Invoice");
		}else{
			cashinvoiceLayout.setVisibility(View.GONE);
			cashinvoice_space.setVisibility(View.GONE);
		}

		if(settlementStr.matches("Settlement")){
			settlementLayout.setVisibility(View.VISIBLE);
			settlement_spae.setVisibility(View.VISIBLE);
			landingMenuArr.add("Settlement");
		}else {
			settlementLayout.setVisibility(View.GONE);
			settlement_spae.setVisibility(View.GONE);
		}

		if(manualStockStr.matches("Manual Stock")){
			manualStcok_space.setVisibility(View.VISIBLE);
			maualStockLayout.setVisibility(View.VISIBLE);
			landingMenuArr.add("Manual Stock");
		}else{
			manualStcok_space.setVisibility(View.GONE);
			maualStockLayout.setVisibility(View.GONE);
		}

		if(quicktransferstr.matches("Quick Transfer")){
			quicktransfer_space.setVisibility(View.VISIBLE);
			quicktransferLayout.setVisibility(View.VISIBLE);
			landingMenuArr.add("Quick Transfer");
		}else{
			quicktransfer_space.setVisibility(View.GONE);
			quicktransferLayout.setVisibility(View.GONE);
		}
		SalesOrderSetGet.setLandingMenuArr(landingMenuArr);
		Log.d("UserGroup", "" + landingMenuArr.toString());
	}

	public void alertDialog() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(getResources().getString(R.string.exit));
		alertDialog.setMessage(getResources().getString(R.string.Do_you_want_to_exit));
		alertDialog.setIcon(R.mipmap.ic_exit);
		alertDialog.setPositiveButton(getResources().getString(R.string.yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						LandingActivity.this.finish();
					}
				});

		alertDialog.setNegativeButton(getResources().getString(R.string.no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});


		alertDialog.show();
	}

	public String checkInternetStatus() {
		checkOffline = OfflineCommon.isConnected(LandingActivity.this);
		String internetStatus = "";
		if (onlineMode.matches("True")) {
			checkOffline = OfflineCommon.isConnected(this);
			if (checkOffline == true) {
				String Off_dialog = OfflineDatabase
						.getInternetMode("OfflineDialog");
				if (Off_dialog.matches("true")) {
					internetStatus = "true";
				} else {
					offlineCommon.OfflineAlertDialog();
					Boolean dialogStatus = offlineCommon.showDialog();
					OfflineDatabase.updateInternetMode("OfflineDialog",
							dialogStatus + "");
					Log.d("OfflineDialogStatus", "" + dialogStatus);
					internetStatus = "" + dialogStatus;
					Toast.makeText(getApplicationContext(),"No Internet Connection!",Toast.LENGTH_SHORT).show();
				}
			} else if (checkOffline == false) {
				String on_dialog = OfflineDatabase
						.getInternetMode("OnlineDialog");
				if (on_dialog.matches("true")) { 
					internetStatus = "true";
				} else {
					offlineCommon.onlineAlertDialog();
					boolean dialogStatus = offlineCommon.showDialog();
					OfflineDatabase.updateInternetMode("OnlineDialog",
							dialogStatus + "");
					Log.d("Online DialogStatus", "" + dialogStatus);
					internetStatus = "" + dialogStatus;
				}
			}
		}else{
			Toast.makeText(getApplicationContext(),"No Internet Connection!",Toast.LENGTH_SHORT).show();
		}
		
		return internetStatus;
	}
	
	private class AppMessageAsyncCall extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {

				String cmpnyCode = SupplierSetterGetter.getCompanyCode();
				String loginUsername = SupplierSetterGetter.getUsername();

				HashMap<String, String> hmvalue = new HashMap<String, String>();
				hmvalue.put("CompanyCode", cmpnyCode);
				hmvalue.put("UserName", loginUsername);
				SalesOrderWebService.getAppMessage(hmvalue, "fncGetAppMessage");

			} catch (Exception e) {
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() + " , " + "Error : " + e.getMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				int msgCount = LogOutSetGet.getMsgCount();
				Log.d("App Message", "count : " + msgCount);

				msg_count.setText(msgCount + "");

				if (msgCount > 0) {
					msg_count.setVisibility(View.VISIBLE);
				} else {
					msg_count.setVisibility(View.GONE);
				}
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(landingLayout, true);
			}catch (Exception e){
				e.printStackTrace();
				errorLog.write("Class Name : " + getClass().getName() +" , "+"Error : " + e.getMessage());
			}
			}

		 }
	
	public void loadprogress(){
		  spinnerLayout = new LinearLayout(LandingActivity.this);
		  spinnerLayout.setGravity(Gravity.CENTER);
		  addContentView(spinnerLayout, new LayoutParams(
		    LayoutParams.FILL_PARENT,
		    LayoutParams.FILL_PARENT));
		  spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		  enableViews(landingLayout, false);
		  progressBar = new ProgressBar(LandingActivity.this);
		  progressBar.setProgress(android.R.attr.progressBarStyle);
		  progressBar.setIndeterminateDrawable(getResources().getDrawable(drawable.greenprogress));
		  spinnerLayout.addView(progressBar);
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
	
	@Override
	public void onBackPressed() {

		alertDialog();
	}
	
	@Override
	public void onResume(){
		super.onResume();
			
			Log.d("onresume", "onresume");
			
			//Crouton.makeText(LandingActivity.this, "No internet connection", Style.INFO).show();
//			Configuration configuration = new Configuration.Builder().setDuration(
//	                Configuration.DURATION_INFINITE).build();
//			Style infinite = new Style.Builder()
//			.setBackgroundColorValue(Style.holoRedLight)
//	                .setHeight(80)
//	                .setGravity(Gravity.CENTER_HORIZONTAL)	                
//	                .setTextSize(18)
//	               .setConfiguration(configuration)
//	                .build();
//	
//
//	       mCrouton = Crouton.makeText(this, "Check Overdue Amount", infinite,R.id.alternate_view_group);
//	       mCrouton.show();
//	       /*   mCrouton.setConfiguration(configuration);*/
//	        
//			mCrouton.setOnClickListener(new View.OnClickListener() {
//			  @Override
//			  public void onClick(View v) {
//			    Crouton.hide(mCrouton);
//			    
//			    Intent i = new Intent(LandingActivity.this,OverdueHeader.class);
//			    startActivity(i);
//			    finish();
//		 		
//			  }
//			});
	        	    	 
	}
	@Override
	protected void onPause(){
		super.onPause();
		Log.d("onPause", "onPause");
//		mCrouton.cancel();
	}
}
