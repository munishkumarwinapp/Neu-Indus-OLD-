package com.winapp.fwms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.adapter.AreaAdapter;
import com.winapp.SFA.BuildConfig;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.helper.SharedPreference;
import com.winapp.model.UrlList;
import com.winapp.offline.OfflineCommon;
import com.winapp.offline.OfflineCustomerSync;
import com.winapp.offline.OfflineDataView;
import com.winapp.offline.OfflineDatabase;
import com.winapp.printer.DeviceListActivity;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.PrinterFinder;
import com.winapp.sot.Area;
import com.winapp.sot.Company;
import com.winapp.sot.OfflineSalesOrderWebService;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SalesOrderSetGet;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.sot.WebServiceClass;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("NewApi")
public class Settings extends SherlockFragmentActivity implements
		SlideMenuFragment.MenuClickInterFace,OnItemClickListener {
	// Request to get the bluetooth device
	private static final int REQUEST_GET_DEVICE = 0;
	String url, selectedArea = "";
	EditText ed_url, printeraddress,editText;
	ImageButton st_save;
	Button st_validate, stupButton, stdownButton, bt_area_search,offlineDataBtn,van_bt;
	String vl_result;
	String vldUrl;
	ProgressDialog progressDialog;
	    
	LinearLayout offlineLayout,settings_parent,vanidLayout,printeraddress_layout;
	ProgressBar progressBar;
	LinearLayout spinnerLayout, printerlayout, enableprintlayout,
			noofcopieslblLayout, companyLayout, locationLayout, useridLayout,
			areaLayout,offlineDataLayout,defaultLocationLayout,enableDOprinterLayout,pod_pending_Layout,Invoiceaddproducttab_layout,languageLayout,catalogLayout;
	Spinner settinglocSpinner,mLanguageSpinner;
	String macaddress, intentprinter, setMacaddress, username, companyname,
			areacode_jsonString = null, locationame,areaCode="";
	Cursor cursor;
	boolean mode,stockmode,checkOffline,DOprint,pod,invtab,invoiceusermode;
	private Switch enableprint,enableDOprinterswitch,pod_pending,invoiceaddproduct_tab,enablestocktake,enableinvoiceuser;
	private static final int SEARCH_PRINTER = 5000;
	Bundle extra;
	SlidingMenu menu;
	int stuprange = 3, stdownrange = 1, stwght = 1;
	TextView stnumber, cmpyanme, userid, txv_area, locationname,vanid, version_code;
	JSONObject areacode_jsonResponse;
	JSONArray area_jsonMainNode;
	ArrayList<Area> al_area, al_dbArea, al_checkedArea;
	AreaAdapter boxAdapter;
	
	String mobileLoginPage="",onlineMode,offlineDialogStatus,dialogStatus;
	ArrayList<String> arr_loc = new ArrayList<String>();
	HashMap<String, String> loccode = new HashMap<String, String>();
	ArrayList<String> toLocArraylist = new ArrayList<String>();
	private OfflineCommon offlineCommon;
	private OfflineDatabase offlineDatabase;
	private OfflineCustomerSync offlinecustomerSynch;
	
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String Name = "USERNAME";
	public static final String Password = "PASSWORD";
	int textlength = 0;
	SharedPreferences sharedpreferences;
	CustomAlertAdapterProd arrayAdapterProd;
	ArrayList<HashMap<String, String>> searchResults;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	AlertDialog myalertDialog = null;
	ArrayList<HashMap<String, String>> getArraylist = new ArrayList<HashMap<String, String>>();
	private static String NAMESPACE = "http://tempuri.org/";
	private static String SOAP_ACTION = "http://tempuri.org/";
	boolean version_click = true;
	private RadioGroup radio_group,catalog_radio_group;
	private SharedPreference mPreference;
	ArrayList<UrlList> urlArrayList=new ArrayList<>();
	CustomURLAdapter urlAdapter;
	private Button st_addurl;
	public boolean var=false,checkfirsttimeclick=false;
	private String get_id;
	private String get_url="";
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.settings_screen);
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);

		View customNav = LayoutInflater.from(this).inflate(
				R.layout.settings_screen_actionbar_title, null);
		TextView title = (TextView) customNav.findViewById(R.id.page_title);
		st_save = (ImageButton) customNav.findViewById(R.id.st_save);
		title.setText(getResources().getString(R.string.settings));

		getSupportActionBar().setCustomView(customNav);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		ab.setDisplayHomeAsUpEnabled(false);
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		// menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slidemenufragment);
		menu.setSlidingEnabled(false);

		catalogLayout  = (LinearLayout) findViewById(R.id.catalogLayout);
		languageLayout = (LinearLayout) findViewById(R.id.languageLayout);
		settings_parent = (LinearLayout) findViewById(R.id.settings_parent);
		printerlayout = (LinearLayout) findViewById(R.id.printerLayout);
		enableprintlayout = (LinearLayout) findViewById(R.id.enableprinterLayout);
		noofcopieslblLayout = (LinearLayout) findViewById(R.id.noofcopieslblLayout);
		companyLayout = (LinearLayout) findViewById(R.id.companyLayout);
		locationLayout = (LinearLayout) findViewById(R.id.locationLayout);
		useridLayout = (LinearLayout) findViewById(R.id.useridLayout);
		areaLayout = (LinearLayout) findViewById(R.id.areaLayout);
		
		enableDOprinterLayout = (LinearLayout) findViewById(R.id.enableDOprinterLayout);
		defaultLocationLayout = (LinearLayout) findViewById(R.id.defaultlocationLayout);	
		offlineDataLayout = (LinearLayout) findViewById(R.id.offlineDataLayout);		
		vanidLayout = (LinearLayout) findViewById(R.id.vanidLayout);	
		pod_pending_Layout = (LinearLayout) findViewById(R.id.pod_pending_Layout);	
		Invoiceaddproducttab_layout = (LinearLayout) findViewById(R.id.Invoiceaddproducttab_layout);
		
		offlineDataBtn = (Button) findViewById(R.id.offlineDataBtn);

		mLanguageSpinner = (Spinner) findViewById(R.id.languageSpinner);

		settinglocSpinner = (Spinner) findViewById(R.id.setting_toLoc);
		ed_url = (EditText) findViewById(R.id.ed_url);
		st_validate = (Button) findViewById(R.id.st_validate);
		printeraddress = (EditText) findViewById(R.id.printeraddress);
		txv_area = (TextView) findViewById(R.id.areafield);
		enableprint = (Switch) findViewById(R.id.enableprinterswitch);
		enablestocktake = (Switch) findViewById(R.id.enablestocktake);
		enableinvoiceuser = (Switch) findViewById(R.id.enableinvoiceuser);
		enableDOprinterswitch = (Switch) findViewById(R.id.enableDOprinterswitch);
		pod_pending = (Switch) findViewById(R.id.pod_pending);
		invoiceaddproduct_tab = (Switch) findViewById(R.id.invoiceaddproduct_tab);
		cmpyanme = (TextView) findViewById(R.id.cmpyanme);
		userid = (TextView) findViewById(R.id.userid);
		locationname = (TextView) findViewById(R.id.locationname);
		
		vanid = (TextView) findViewById(R.id.vanid);
		
		bt_area_search = (Button) findViewById(R.id.area_search_bt);
		van_bt = (Button) findViewById(R.id.van_bt);

		radio_group= (RadioGroup) findViewById(R.id.radio_group);
		catalog_radio_group= (RadioGroup) findViewById(R.id.catalog_radio_group);

		printeraddress_layout= (LinearLayout) findViewById(R.id.printeraddress_layout);

		version_code = (TextView) findViewById(R.id.version_code);

		st_addurl=(Button)findViewById(R.id.st_addurl);
		st_addurl.setVisibility(View.INVISIBLE);

		int versionCode = BuildConfig.VERSION_CODE;
		final String versionName = BuildConfig.VERSION_NAME;

		ArrayList<UrlList>urlList=FWMSSettingsDatabase.getTempUrl();
		Log.d("Empty",""+urlList.size());
		if(urlList.isEmpty()) {
			UrlList def_url = new UrlList();
			def_url.setUrl(url);
			def_url.setId("1");
			def_url.setDef_name("default");
			urlList.add(def_url);
			FWMSSettingsDatabase.storeTempUrl(url);
			Log.d("Def_URL", "" + urlList.size());
		}else{

		}

		version_code.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(version_click){
					version_code.setText("DevV"+ versionName);
					version_click = false;
				}else{
					version_code.setText(R.string.sfa_version);
					version_click = true;
				}

			}
		});
		mPreference = new SharedPreference(Settings.this);
		SOTDatabase.init(Settings.this);
		al_area = new ArrayList<Area>();
		al_dbArea = new ArrayList<Area>();
		al_checkedArea = new ArrayList<Area>();
		al_area.clear();
		al_dbArea.clear();
		al_checkedArea.clear();
		
		arr_loc.clear();
		loccode.clear();
		
		sharedpreferences = getApplicationContext().getSharedPreferences(
				MyPREFERENCES, Context.MODE_PRIVATE);
		
		// @Offline
			onlineMode = OfflineDatabase.getOnlineMode();
			offlineDatabase = new OfflineDatabase(Settings.this);
			offlineCommon = new OfflineCommon(Settings.this);
			checkOffline = OfflineCommon.isConnected(Settings.this);
			OfflineDatabase.init(Settings.this);
			new OfflineSalesOrderWebService(Settings.this);
			offlinecustomerSynch = new OfflineCustomerSync(Settings.this);
			offlineLayout = (LinearLayout) findViewById(R.id.offlineLayout);
		
		checkInternetStatus();
		
		ed_url.setBackgroundColor(Color.parseColor("#626776"));
		ed_url.setCursorVisible(false);
		ed_url.setTextColor(Color.parseColor("#FFFFFF"));

		st_validate.setVisibility(View.INVISIBLE);
		st_save.setVisibility(View.VISIBLE);
		printeraddress.setEnabled(true);
		extra = getIntent().getExtras();
		FWMSSettingsDatabase.init(Settings.this);
		SOTDatabase.init(Settings.this);
		
		url = FWMSSettingsDatabase.getUrl();
		ed_url.setText(url);
		
		new LoginWebService(url);
		new SalesOrderWebService(url);
		
		AsyncCallWSVan task = new AsyncCallWSVan();
		task.execute();
		
		String vandriver = SOTDatabase.getVandriver();
		Log.d("van", "aa "+vandriver);
		vanid.setText(""+vandriver);
		
		username = SupplierSetterGetter.getUsername();
		companyname = Company.getCompanyName();
		locationame = SalesOrderSetGet.getLocationname();
		loccode = SupplierSetterGetter.getLoc_code_name();
		Log.d("Login locationame", "-->" + locationame);
		arr_loc = SalesOrderSetGet.getFrom_location();
		
		for (int i = 0; i < arr_loc.size(); i++) {
			if (arr_loc.get(i).contains("Select Location")) {
				arr_loc.remove("Select Location");
			}else if (arr_loc.get(i).contains("")) {
				arr_loc.remove("");
			}
		}
		
		toLocArraylist.addAll(arr_loc);
		
		mobileLoginPage = SalesOrderSetGet.getMobileloginpage();
		
		if(mobileLoginPage.matches("M")){
			locationname.setVisibility(View.GONE);
			settinglocSpinner.setVisibility(View.VISIBLE);
		}else{
			locationname.setVisibility(View.VISIBLE);
			settinglocSpinner.setVisibility(View.GONE);
		}
				
		if (locationame != null && !locationame.isEmpty()) {
			settinglocSpinner.setAdapter(new SpinnerAdapter(Settings.this,R.layout.row, toLocArraylist));
			int spinnerPosition = -1;
			for (int i = 0; i < toLocArraylist.size(); i++) {
				if (toLocArraylist.get(i).contains(
						SupplierSetterGetter.getLocationcode().toString())) {
					spinnerPosition = i;

				}
			}
			Log.d("Location spinner", "-->" + spinnerPosition);
			settinglocSpinner.setSelection(spinnerPosition);

		} else {
			settinglocSpinner.setAdapter(new SpinnerAdapter(Settings.this,R.layout.row, toLocArraylist));

		}
		String [] languageList = getResources().getStringArray(R.array.select_language);
          /*Array to ArrayList conversion*/
        List<String> langArrList = Arrays.asList(languageList);

        mLanguageSpinner.setAdapter(new SpinnerAdapter(Settings.this,R.layout.row,langArrList));
		
		if (username != null) {
			userid.setText(username);
		}
		if (companyname != null) {
			cmpyanme.setText(companyname);
		}
		if (locationame != null && !locationame.isEmpty()) {
			locationname.setText(locationame);
		}
		
		cursor = FWMSSettingsDatabase.getPrinter();
		if (extra != null) {
			setMacaddress = extra.getString("MACAddress");

			if (!setMacaddress.matches("")) {

				printeraddress.setText(setMacaddress);
			}
		} else {
			printeraddress.setText(FWMSSettingsDatabase.getPrinterAddress());
		}
		setSpnrLangSelection();
		mLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 String spnrSelectedLanguage = mLanguageSpinner.getSelectedItem().toString();
               setlanguage(spnrSelectedLanguage);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if(null!=rb && checkedId > -1){
//                    Toast.makeText(Settings.this, rb.getText(), Toast.LENGTH_SHORT).show();
             
                	String rdbtn = rb.getText().toString();
                	
                	if(rdbtn.matches("Zebra iMZ320") || rdbtn.matches("4 Inch Bluetooth") || rdbtn.matches("3 Inch Bluetooth Generic") ||
							rdbtn.matches("Zebra iMZ320 4 Inch")){
                		printeraddress_layout.setVisibility(View.VISIBLE);
					}else{
						printeraddress_layout.setVisibility(View.GONE);
					}
                	
                	
                }else{
                	
                }

            }
        });

		catalog_radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton rb = (RadioButton) group.findViewById(checkedId);
				if(null!=rb && checkedId > -1){
//                    Toast.makeText(Settings.this, rb.getText(), Toast.LENGTH_SHORT).show();

					String rdbtn = rb.getText().toString();

					if(rdbtn.matches("Invoice")){

					}else{

					}


				}else{

				}

			}
		});
		
		van_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				alertDialogSearch();
			}
		});
		
		vanid.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(vanid.getText().length()>0){
					vanid
					.setCompoundDrawablesWithIntrinsicBounds(
							0, 0,
							R.mipmap.ic_clear_btn, 0);
					
					vanid
					.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
							vanid) {
						@Override
						public boolean onDrawableClick() {

							SOTDatabase.deleteVan();
							vanid.setText("");
							vanid
							.setCompoundDrawablesWithIntrinsicBounds(
									0, 0,
									0, 0);
							return true;

						}

					});
				}
			}
		});
		
		stnumber = (TextView) findViewById(R.id.stnumber);
		stupButton = (Button) findViewById(R.id.stupBtn);
		stupButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stdownButton
						.setBackgroundResource(R.mipmap.numpicker_down_normal);
				stupButton
						.setBackgroundResource(R.mipmap.numpicker_up_pressed);
				if (stwght < 3) {
					stnumber.setText("" + ++stwght);
				}
			}
		});

		stdownButton = (Button) findViewById(R.id.stdownBtn);
		stdownButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stdownButton
						.setBackgroundResource(R.mipmap.numpicker_down_pressed);
				stupButton
						.setBackgroundResource(R.mipmap.numpicker_up_normal);
				if (stwght > 1) {
					stnumber.setText(--stwght + "");
				}
			}
		});
		if (!PreviewPojo.getNofcopies().matches("")) {
			stwght = Integer.valueOf(PreviewPojo.getNofcopies());
			stnumber.setText(PreviewPojo.getNofcopies());

		}
		String enablefilter = FWMSSettingsDatabase.getMode();
		if (enablefilter==null || enablefilter.isEmpty()) {
			FWMSSettingsDatabase.setMode(0);
			enableprint.setChecked(false);
			mode = false;

		} else {
			int modeid = FWMSSettingsDatabase.getModeId();
			Log.d("print mode", ""+modeid);
			if (modeid == 1) {
				FWMSSettingsDatabase.updateMode(1);
				enableprint.setChecked(true);
				mode = true;
			} else {
				FWMSSettingsDatabase.updateMode(0);
				enableprint.setChecked(false);
				mode = false;

			}
		}

		String enablestock = FWMSSettingsDatabase.getStockMode();
		if (enablestock==null || enablestock.isEmpty()) {
			FWMSSettingsDatabase.setStockMode(0);
			enablestocktake.setChecked(false);
			stockmode = false;
		} else {
			int modeid = FWMSSettingsDatabase.getStockModeId();
			Log.d("print mode", ""+modeid);
			if (modeid == 1) {
				FWMSSettingsDatabase.updateStockMode(1);
				enablestocktake.setChecked(true);
				stockmode = true;
			} else {
				FWMSSettingsDatabase.updateStockMode(0);
				enablestocktake.setChecked(false);
				stockmode = false;
			}
		}

		String enableuser = FWMSSettingsDatabase.getInvoiceuserMode();
		if (enableuser==null || enableuser.isEmpty()) {
			FWMSSettingsDatabase.setInvoiceuserMode(0);
			enableinvoiceuser.setChecked(false);
			invoiceusermode = false;
		} else {
			int modeid = FWMSSettingsDatabase.getInvoiceuserModeId();
			Log.d("print mode", ""+modeid);
			if (modeid == 1) {
				FWMSSettingsDatabase.updateInvoiceuserMode(1);
				enableinvoiceuser.setChecked(true);
				invoiceusermode = true;
			} else {
				FWMSSettingsDatabase.updateInvoiceuserMode(0);
				enableinvoiceuser.setChecked(false);
				invoiceusermode = false;
			}
		}

		selectedArea = SOTDatabase.getAreaCode();
		if (selectedArea.matches("")) {
			txv_area.setText("");
		} else {
			txv_area.setText(selectedArea);
		}
		
		//doprint
		Cursor cursor = FWMSSettingsDatabase.getDOPrintCursor();
		
		if(cursor.getCount()>0){
			
		}else{
			FWMSSettingsDatabase.saveDOPrintMode(0);
		}
		
		String doIninvoiceprint = FWMSSettingsDatabase.getDOPrintMode();
		
		if(doIninvoiceprint!=null && !doIninvoiceprint.isEmpty()){
			if(doIninvoiceprint.matches("1")){
		    	enableDOprinterswitch.setChecked(true);
		    	DOprint = true;
			}else{
				enableDOprinterswitch.setChecked(false);
				DOprint = false;
			}
		}else{
			enableDOprinterswitch.setChecked(false);
			DOprint = false;
		}
		
		//Printer Type
				Cursor cursorprintertype = FWMSSettingsDatabase.getPrinterType();
				
				if(cursorprintertype.getCount()>0){
					String printtype = FWMSSettingsDatabase.getPrinterTypeStr();
					
					if(printtype!=null && !printtype.isEmpty()){
						if(printtype.matches("Zebra iMZ320")){
							radio_group.check(R.id.zebra_radio);
						}else if(printtype.matches("4 Inch Bluetooth")){
							radio_group.check(R.id.fourinch_radio);
						}else if(printtype.matches("3 Inch Bluetooth Generic")){
							radio_group.check(R.id.three_inch_bluetooth_radio);
						}else if(printtype.matches("Zebra iMZ320 4 Inch")){
							radio_group.check(R.id.zebra_radio_4inch);
						}else{
							radio_group.check(R.id.a4wifi_radio);
						}
					}
					
				}else{
					
				}
//Catalog Type
		Cursor cursorcatalogtype = FWMSSettingsDatabase.getCatalogType();

		if(cursorcatalogtype.getCount()>0){
			String cattype = FWMSSettingsDatabase.getCatalogTypeStr();

			if(cattype!=null && !cattype.isEmpty()){
				if(cattype.matches("Invoice")){
					catalog_radio_group.check(R.id.invoice_radio);
				}else{
					catalog_radio_group.check(R.id.so_radio);
				}
			}

		}else{

		}

		//pod pending
		Cursor cursor1 = FWMSSettingsDatabase.getPODPendingCursor();
		
		if(cursor1.getCount()>0){
			
		}else{
			FWMSSettingsDatabase.savePODPending(0);
		}
		
		String podpend = FWMSSettingsDatabase.getPODPending();
		
		if(podpend!=null && !podpend.isEmpty()){
			if(podpend.matches("1")){
		    	pod_pending.setChecked(true);
		    	pod = true;
			}else{
				pod_pending.setChecked(false);
				pod = false;
			}
		}else{
			pod_pending.setChecked(false);
			pod = false;
		}
		
		//invoice add product tab
				Cursor cursor2 = FWMSSettingsDatabase.getInvoiceaddproducttabCursor();
				
				if(cursor2.getCount()>0){
					
				}else{
					FWMSSettingsDatabase.saveInvoiceaddproducttab(0);
				}
				
				String invoicetab = FWMSSettingsDatabase.getInvoiceaddproducttab();
				
				if(invoicetab!=null && !invoicetab.isEmpty()){
					if(invoicetab.matches("1")){
				    	invoiceaddproduct_tab.setChecked(true);
				    	invtab = true;
					}else{
						invoiceaddproduct_tab.setChecked(false);
						invtab = false;
					}
				}else{
					invoiceaddproduct_tab.setChecked(false);
					invtab = false;
				}

		st_addurl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				checkfirsttimeclick = true;

				ArrayList<UrlList>urlList=FWMSSettingsDatabase.getTempUrl();
				Log.d("DataSize",""+urlList.size());
				if(urlList.isEmpty()){
					UrlList def_url=new UrlList();
					def_url.setUrl(url);
					def_url.setId("1");
					def_url.setDef_name("default");
					urlList.add(def_url);
					FWMSSettingsDatabase.storeTempUrl(url);
					Log.d("Add",""+urlList.size());
					alertDialogSearchURL();
				}else{
					urlArrayList.clear();
					alertDialogSearchURL();
				}

			}
		});

		ed_url.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (onlineMode.matches("True")) {
					if (checkOffline == true)    //Temporary Offline
					{
						ed_url.setEnabled(false);
						
 
					} else // online
					{						
						ed_url.setBackgroundColor(Color.parseColor("#626776"));
						ed_url.setCursorVisible(true);
						ed_url.setTextColor(Color.BLACK);
						st_validate.setVisibility(View.VISIBLE);
						printerlayout.setVisibility(View.GONE);
						enableprintlayout.setVisibility(View.GONE);
						noofcopieslblLayout.setVisibility(View.GONE);
						useridLayout.setVisibility(View.GONE);
						companyLayout.setVisibility(View.GONE);
						locationLayout.setVisibility(View.GONE);
						areaLayout.setVisibility(View.GONE);
						st_save.setVisibility(View.INVISIBLE);
						defaultLocationLayout.setVisibility(View.GONE);
						offlineDataLayout.setVisibility(View.GONE);	
						enableDOprinterLayout.setVisibility(View.GONE);
						vanidLayout.setVisibility(View.GONE);
						pod_pending_Layout.setVisibility(View.GONE);
						Invoiceaddproducttab_layout.setVisibility(View.GONE);
						languageLayout.setVisibility(View.GONE);
						catalogLayout.setVisibility(View.GONE);

						st_addurl.setVisibility(View.VISIBLE);
						
					}

				} else if (onlineMode.matches("False")) // permanent offline	
				{
					ed_url.setEnabled(false);					
					
				}		
			}
		});
		offlineDataBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Settings.this,OfflineDataView.class);
				startActivity(i);
				finish();
			}
		});
		bt_area_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new AreaAsyncCall().execute();
			}
		});

		st_validate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				vl_result = null;

				vldUrl = ed_url.getText().toString();

				if (vldUrl.matches("")) {
					Toast.makeText(Settings.this, "Invalid Domain URL",
							Toast.LENGTH_SHORT).show();
				} else {
					AsyncCallWSValidate task = new AsyncCallWSValidate();
					task.execute();
				}

			}
		});

		st_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				urlArrayList=FWMSSettingsDatabase.getTempUrl();
				for(int i=0;i<urlArrayList.size();i++){
					get_id=urlArrayList.get(i).getId();
					Log.d("UpdateDatas",""+get_id);
				}

				String uptUrl = ed_url.getText().toString();
				PreviewPojo.setNofcopies(stnumber.getText().toString());
				FWMSSettingsDatabase.init(Settings.this);
				FWMSSettingsDatabase.updateUrl(uptUrl);

				FWMSSettingsDatabase.updateTempUrl(uptUrl,get_id);
				Log.d("UpdateData",""+ uptUrl +get_id);

				 // get selected radio button from radioGroup
				int selectedId = radio_group.getCheckedRadioButtonId();

				// find the radiobutton by returned id
				RadioButton  radioButton = (RadioButton) findViewById(selectedId);
				
				String radioButtonTxt =radioButton.getText().toString();
				
				Log.d("radioButton", ""+radioButton.getText().toString());
				
				if(radioButtonTxt!=null && !radioButtonTxt.isEmpty()){
					if (FWMSSettingsDatabase.getPrinterType().getCount() == 0) {
						FWMSSettingsDatabase.storePrinterType(radioButtonTxt);
					} else {
						FWMSSettingsDatabase
								.updatePrinterType(radioButtonTxt);
					}
				}

				// catalog save type
				int selectId = catalog_radio_group.getCheckedRadioButtonId();

				// find the radiobutton by returned id
				RadioButton  radioBtn = (RadioButton) findViewById(selectId);

				String radioBtnTxt =radioBtn.getText().toString();

				Log.d("Catalog save radio", ""+radioBtnTxt);

				if(radioBtnTxt!=null && !radioBtnTxt.isEmpty()){
					if (FWMSSettingsDatabase.getCatalogType().getCount() == 0) {
						FWMSSettingsDatabase.storeCatalogType(radioBtnTxt);
					} else {
						FWMSSettingsDatabase
								.updateCatalogType(radioBtnTxt);
					}
				}
				
				if (TextUtils.isEmpty(printeraddress.getText())
						|| printeraddress
								.getText()
								.toString()
								.matches(
										"[0-9A-Fa-f]{2}([-:])[0-9A-Fa-f]{2}(\\1[0-9A-Fa-f]{2}){4}$")) {
					
					Log.d("step", "--> 1");
					if(mobileLoginPage.matches("M")){
						AsyncCallSaveLastLocation asynws = new AsyncCallSaveLastLocation();
						asynws.execute();
					}
					Log.d("step", "--> 2");
					if (FWMSSettingsDatabase.getPrinterCount() == 0) {
						FWMSSettingsDatabase.printerAddress(printeraddress
								.getText().toString());
					} else {
						FWMSSettingsDatabase
								.updatePrinterAddress(printeraddress.getText()
										.toString());
					}
					setlanguage();
					Log.d("step", "--> 3");
					if (!url.matches(ed_url.getText().toString())) {
						Intent i = new Intent(Settings.this, SplashScreen.class);
						startActivity(i);
						Settings.this.finish();
					} else {
						Intent i = new Intent(Settings.this,
								LandingActivity.class);
						startActivity(i);
						Settings.this.finish();
					}

					Log.d("step", "--> 4");
					if (mode == true) {
						FWMSSettingsDatabase.updateMode(1);
					} else if (mode == false) {
						FWMSSettingsDatabase.updateMode(0);
					}
					
					Log.d("step", "--> 5");
					if (DOprint == true) {
						FWMSSettingsDatabase.updateDOPrintMode(1);
					} else if (DOprint == false) {
						FWMSSettingsDatabase.updateDOPrintMode(0);
					}
					
					Log.d("step", "--> 6");
					if (pod == true) {
						FWMSSettingsDatabase.updatePODPending(1);
					} else if (pod == false) {
						FWMSSettingsDatabase.updatePODPending(0);
					}
					
					Log.d("step", "--> 7");
					if (invtab == true) {
						FWMSSettingsDatabase.updateInvoiceaddproducttab(1);
					} else if (invtab == false) {
						FWMSSettingsDatabase.updateInvoiceaddproducttab(0);
					}

					if (stockmode == true) {
						FWMSSettingsDatabase.updateStockMode(1);
					} else if (stockmode == false) {
						FWMSSettingsDatabase.updateStockMode(0);
					}

					if (invoiceusermode == true) {
						FWMSSettingsDatabase.updateInvoiceuserMode(1);
					} else if (invoiceusermode == false) {
						FWMSSettingsDatabase.updateInvoiceuserMode(0);
					}

					Toast.makeText(Settings.this,
							"Settings Saved Successfully", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(Settings.this, "Enter Valid MacAddress",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		printeraddress
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						printeraddress) {
					@Override
					public boolean onDrawableClick() {
						printeraddress.setEnabled(false);
						Intent i = new Intent(Settings.this,DeviceListActivity.class);
						startActivity(i);
						//startActivityForResult(i, SEARCH_PRINTER);
						Settings.this.finish();
						return true;
					}
				});
		enableprint.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					mode = true;
				} else {
					mode = false;
				}
			}
		});

		enablestocktake.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {

				if (isChecked) {
					stockmode = true;
				} else {
					stockmode = false;
				}
			}
		});

		enableinvoiceuser.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {

				if (isChecked) {
					invoiceusermode = true;
				} else {
					invoiceusermode = false;
				}
			}
		});
		
		enableDOprinterswitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					DOprint = true;
				} else {
					DOprint = false;
				}
			}
		});	
		
		pod_pending.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					pod = true;
				} else {
					pod = false;
				}
			}
		});	
		
		invoiceaddproduct_tab.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					invtab = true;
				} else {
					invtab = false;
				}
			}
		});	

	}
	private void setSpnrLangSelection(){
		String languageName = mPreference.getLanguage();
		if(languageName!=null && !languageName.isEmpty()){
			if(languageName.equalsIgnoreCase("English")){
				mLanguageSpinner.setSelection(0);
			}else{
				mLanguageSpinner.setSelection(1);
			}
		}else{
			mLanguageSpinner.setSelection(0);
		}

	}
   private void setlanguage(String selectedLanguage){
	   if(selectedLanguage.equalsIgnoreCase("English")){
		   mPreference.putLanguage("English");
	   }else if(selectedLanguage.equalsIgnoreCase("Chinese Simplified (中文 简)")){
		   mPreference.putLanguage("中文（简体）");
	   }
   }
   private void setlanguage(){
	   Resources res = getResources();
	   DisplayMetrics dm = res.getDisplayMetrics();
	   Configuration conf = res.getConfiguration();
	   conf.locale = mPreference.getLanguageLocale();
	   res.updateConfiguration(conf,dm);
   }
	private class AsyncCallWSValidate extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {

				vl_result = ValidateWebService.validateURLService(vldUrl,
						"fncValidateURL");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.e("Domain url result", result.toString());

			if (vl_result != null) {
				if (vl_result.matches("True")) {
					ed_url.setBackgroundColor(Color.parseColor("#626776"));
					ed_url.setCursorVisible(false);
					st_validate.setVisibility(View.INVISIBLE);
					st_save.setVisibility(View.VISIBLE);
					printerlayout.setVisibility(View.VISIBLE);
					enableprintlayout.setVisibility(View.VISIBLE);
					noofcopieslblLayout.setVisibility(View.VISIBLE);
					useridLayout.setVisibility(View.VISIBLE);
					companyLayout.setVisibility(View.VISIBLE);
					locationLayout.setVisibility(View.VISIBLE);
					defaultLocationLayout.setVisibility(View.VISIBLE);
					offlineDataLayout.setVisibility(View.VISIBLE);
					areaLayout.setVisibility(View.VISIBLE);
					languageLayout.setVisibility(View.VISIBLE);
					catalogLayout.setVisibility(View.VISIBLE);

					String validateUrl=ed_url.getText().toString();
					String field = validateUrl.replaceAll("\\s+", "");
					if(var == true){
//				FWMSSettingsDatabase.updateTempUrl(field,get_id);
					}else {
						if (get_url.matches(field)) {
							Log.d("MatchURL", "-->" + field);
//						FWMSSettingsDatabase.updateTempUrl(field,get_id);
						} else {
							if(checkfirsttimeclick==false){

							}else{
								FWMSSettingsDatabase.storeTempUrl(field);
							}

						}
					}

					Toast.makeText(Settings.this, "URL Validation Succeed",
							Toast.LENGTH_SHORT).show();
				} else if (vl_result.matches("Error")) {
					Toast.makeText(Settings.this, "Invalid Domain URL",
							Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(Settings.this, "Invalid Domain URL",
						Toast.LENGTH_SHORT).show();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(settings_parent, true);
		}
	}

	private class AreaAsyncCall extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			al_area.clear();
			al_checkedArea.clear();
			al_dbArea.clear();
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			if (onlineMode.matches("True")) {
				if (checkOffline == true)    //Temporary Offline
				{
					areacode_jsonString = OfflineDatabase.getArea(areaCode);
					

				} else // online
				{
					areacode_jsonString = WebServiceClass.URLService("fncGetArea");
					
				}

			} else if (onlineMode.matches("False")) // permanent offline	
			{
				
				areacode_jsonString = OfflineDatabase.getArea(areaCode);
				
			}			
			
			

			try {
				areacode_jsonResponse = new JSONObject(areacode_jsonString);
				area_jsonMainNode = areacode_jsonResponse.optJSONArray("JsonArray");

			} catch (JSONException e) {
				e.printStackTrace();
			}

			int lengthJsonArr = area_jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {

				JSONObject jsonChildNode;
				try {

					jsonChildNode = area_jsonMainNode.getJSONObject(i);
					String area_code = jsonChildNode.optString("Code")
							.toString();
					String area_Name = jsonChildNode.optString("Description")
							.toString();

					Area area = new Area(area_code, area_Name, false);
					Log.d("area_code,area_Name,", area_code + "," + area_Name);

					al_area.add(area);
				} catch (JSONException e) {

					e.printStackTrace();
				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(settings_parent, true);
			// areaDialog();
			areaViewDialog();
		}
	}

	public void alertDialogSearch() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				Settings.this);
		editText = new EditText(Settings.this);
		final ListView listview = new ListView(Settings.this);
		LinearLayout layout = new LinearLayout(Settings.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle(getResources().getString(R.string.van_driver));
		editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,
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
		arrayAdapterProd = new CustomAlertAdapterProd(Settings.this,
				al);
		listview.setAdapter(arrayAdapterProd);
		listview.setOnItemClickListener(Settings.this);

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

				arrayAdapterProd = new CustomAlertAdapterProd(
						Settings.this, searchResults);
				listview.setAdapter(arrayAdapterProd);
			}
		});
		myDialog.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		myalertDialog = myDialog.show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		myalertDialog.dismiss();
		getArraylist = arrayAdapterProd.getArrayList();

		HashMap<String, String> datavalue = getArraylist.get(position);
		Set<Entry<String, String>> keys = datavalue.entrySet();
		Iterator<Entry<String, String>> iterator = keys.iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Entry mapEntry = iterator.next();
			String van_code = (String) mapEntry.getKey();
			String desc = (String) mapEntry.getValue();

//			sl_codefield.setText(sales_prodCode);
			vanid.setText(van_code);
			
			SOTDatabase.storeVandriver(van_code, desc);
			
			vanid.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable s) {

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {

					textlength = vanid.getText().length();
//					vanid.setText("");

				}
			});
		}

	}
	
	private class AsyncCallWSVan extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			al.clear();
			getActionBar().setHomeButtonEnabled(false);
			menu.setSlidingEnabled(false);
			loadprogress();
			
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			try {				
				if (onlineMode.matches("True")) {
					if (checkOffline == true){    //Temporary Offline
						al = OfflineDatabase.getVan();
					} else { // online
						al = SalesOrderWebService.getAllVan("fncGetVan");
					}
				} else if (onlineMode.matches("False")) { // permanent offline	
					al = OfflineDatabase.getVan();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d("Van List", " : "+al.toString());
			String vandriver = SOTDatabase.getVandriver();
			Log.d("van", vandriver);
			vanid.setText(""+vandriver);
			
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(settings_parent, true);
			getActionBar().setHomeButtonEnabled(true);
			menu.setSlidingEnabled(true);
		}

	}

	
	private void areaViewDialog() {

		AlertDialog.Builder myDialog = new AlertDialog.Builder(Settings.this);
		ListView listview = new ListView(Settings.this);
		LinearLayout layout = new LinearLayout(Settings.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle(getResources().getString(R.string.select_area));
		layout.addView(listview);
		myDialog.setView(layout);
		al_dbArea = SOTDatabase.checkedArea();

		boxAdapter = new AreaAdapter(Settings.this,
				R.layout.area_dialog_listitem, al_area, al_dbArea);
		listview.setAdapter(boxAdapter);
		myDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				SOTDatabase.deleteArea();

				ArrayList<Area> unchecked = new ArrayList<Area>();
				unchecked = boxAdapter.getBox();
				if (unchecked.isEmpty()) {
//					SOTDatabase.storeArea("", "", "");
					txv_area.setText("");
				} else {

					for (Area p : boxAdapter.getBox()) {
						if (p.box) {
							String areacode = p.getAreacode();
							String areaname = p.getAreaname();

							SOTDatabase.storeArea(areacode, areaname, "true");
						}
					}

					selectedArea = SOTDatabase.getAreaCode();
					txv_area.setText(selectedArea);

				}
			}
		});
		myDialog.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		myDialog.show();
	}
	
	public class SpinnerAdapter extends ArrayAdapter<String> {
		List<String> selectArray = new ArrayList<String>();

		public SpinnerAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			selectArray.clear();
			selectArray = objects;
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

			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);
			TextView label = (TextView) row.findViewById(R.id.locationspinner);
			ImageView icon = (ImageView) row.findViewById(R.id.spinnericon);
			label.setText(selectArray.get(position));
			icon.setVisibility(View.GONE);

			return row;
		}
	}
	
	
	private class AsyncCallSaveLastLocation extends AsyncTask<Void, Void, Void> {

		String mResult = "", sp="", locationcode= "";
		@Override
		protected void onPreExecute() {
			sp = settinglocSpinner.getSelectedItem().toString();
			locationcode = loccode.get(sp);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			String password = sharedpreferences.getString(Password, null);
			Log.d("password-->", ""+password);
			try {
				if (onlineMode.matches("True")) {
					
					if (checkOffline == true)    //Temporary Offline
					{												
						mResult = OfflineDatabase.getLastLoginLocation(username,password);
						
					} else // online
					{
						mResult = LoginWebService.lastLoginLocationWS("fncSaveLastLoginLocation", username, locationcode);
					}

				} else if (onlineMode.matches("False")) // permanent offline	
				{
					mResult =  OfflineDatabase.getLastLoginLocation(username,password);
				}				

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			if(mResult.matches("True")){
				SupplierSetterGetter.setLocationcode(sp);
				SalesOrderSetGet.setLocationcode(locationcode);
				SalesOrderSetGet.setLocationname(sp);
				Log.d("setting getLocationcod)", "-->"+SupplierSetterGetter.getLocationcode());
				Log.d("setting getLocationcod)", "---->"+SalesOrderSetGet.getLocationcode());
				Log.d("setting getLocationnam)", "---->"+SalesOrderSetGet.getLocationname());
			}else{
				Toast.makeText(Settings.this, "Error! Location not updated",Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void alertDialogSearchURL() {
		urlArrayList.clear();
		FWMSSettingsDatabase.init(Settings.this);
		urlArrayList = FWMSSettingsDatabase.getTempUrl();

		final AlertDialog.Builder myDialog = new AlertDialog.Builder(
				Settings.this);
		final EditText editText = new EditText(Settings.this);
		final ListView listView = new ListView(Settings.this);
		LinearLayout layout = new LinearLayout(Settings.this);

		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("URL");
		editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,
				0, 0);
		layout.addView(editText);
		layout.addView(listView);
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


		urlAdapter = new CustomURLAdapter(Settings.this, urlArrayList,myalertDialog);
		listView.setAdapter(urlAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				var=true;
				ed_url.setText(urlArrayList.get(position).getUrl());
				myalertDialog.dismiss();
				get_id=urlArrayList.get(position).getId();

				Log.d("particular_Id",""+get_id+var);

			}
		});


		/*myDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				if(editText.getText().toString().equals("")){
					Log.d("EditText",""+"Empty");

				}else {
					editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,0, 0);
					String validateUrl = editText.getText().toString();
					String field = validateUrl.replaceAll("\\s+", "");
					ed_url.setText(field);
				}

			}
		});*/
		myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				dialogInterface.dismiss();
			}
		});

		myDialog.setNeutralButton("Add", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {


				myalertDialog.dismiss();
				String get_url=editText.getText().toString();
				if(get_url.equals("")){
					ed_url.setText(url);
					final AlertDialog alertDialog = new AlertDialog.Builder(Settings.this).create();
					alertDialog.setTitle("Error");
					alertDialog.setMessage("Please Enter the URL");
					alertDialog.setIcon(R.mipmap.ic_info);
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							alertDialog.cancel();
						} });
					alertDialog.show();
				}else {
					ed_url.setText(get_url);
					var=false;

				}
			}
		});

		/*myDialog.setNegativeButton("Add", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				editText.setVisibility(View.VISIBLE);

				if(editText.getText().toString().equals("")){
					Log.d("EditText",""+"Empty");

				}else {
					String validateUrl = editText.getText().toString();
					String field = validateUrl.replaceAll("\\s+", "");
					ed_url.setText(field);
					var = false;

				}
			}
		});

		myDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				dialogInterface.dismiss();
			}
		});*/

		myalertDialog = myDialog.show();

	}

	public class CustomURLAdapter extends BaseAdapter {
		AlertDialog alertDialog=null;
		ArrayList<UrlList> urlArrayList;
		Context context;
		ArrayList<String> get_temp_url=null;
		private LayoutInflater mInflater;

		public CustomURLAdapter(Settings settings, ArrayList<UrlList> urlArrayList, AlertDialog myalertDialog) {
			this.context=settings;
			this.urlArrayList=urlArrayList;
			mInflater = LayoutInflater.from(context);
			this.alertDialog=myalertDialog;
		}

		@Override
		public int getCount() {
			return urlArrayList.size();
		}

		@Override
		public Object getItem(int i) {
			return urlArrayList.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(final int i, View view, ViewGroup viewGroup) {
			ViewHolder holder;
			FWMSSettingsDatabase.init(context);
			urlArrayList = FWMSSettingsDatabase.getTempUrl();

			if (view == null) {
				holder = new ViewHolder();
				view = mInflater.inflate(R.layout.alerturllayout, null);
				holder.url = (TextView) view.findViewById(R.id.textview);
				holder.delete = (ImageView) view.findViewById(R.id.delete);
				holder.setting_id  = (TextView) view.findViewById(R.id.setting_id);

				view.setTag(holder);
				view.setId(i);

			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.url.setText(urlArrayList.get(i).getUrl());
			holder.setting_id.setText(urlArrayList.get(i).getId());
			Log.d("URlListItem", "" + urlArrayList.get(i).toString());

			get_url=urlArrayList.get(i).getUrl();
			Log.d("GetURL",""+get_url);

			int arraySize=urlArrayList.size();
			if(arraySize==1){
				holder.delete.setVisibility(View.GONE);
			}else {
				holder.delete.setVisibility(View.VISIBLE);
				holder.delete.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						deleteAlertDialog(urlArrayList.get(i).getId());

					}
				});
			}
			return view;
		}

		private void deleteAlertDialog(final String getId) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					context);

			builder.setTitle("Confirm Delete");
			builder.setMessage("Do you want to delete?");

			builder.setCancelable(false);
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					FWMSSettingsDatabase.deleteTempUrl(getId);
					Log.d("ExistingUrl",""+getId);
					myalertDialog.dismiss();
					urlArrayList.remove(getId);
					FWMSSettingsDatabase.getTempUrl().clear();
					notifyDataSetChanged();
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					dialogInterface.dismiss();
				}
			});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
		}

		private class ViewHolder {
			TextView url,setting_id;
			ImageView delete;
		}
	}

	private String checkInternetStatus() {
		String internetStatus = "";
		String mobileHaveOfflineMode = SalesOrderSetGet.getMobileHaveOfflineMode();
		if(mobileHaveOfflineMode!=null && !mobileHaveOfflineMode.isEmpty()){
			if(mobileHaveOfflineMode.matches("1")){
		 checkOffline = OfflineCommon.isConnected(Settings.this);
//			String internetStatus = "";
			if (onlineMode.matches("True")) {
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
						Log.d("Offline DialogStatus", "" + dialogStatus);
						internetStatus = "" + dialogStatus;
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
			}
			
			onlineMode = OfflineDatabase.getOnlineMode();
			if (onlineMode.matches("True")) {
				offlineLayout.setVisibility(View.GONE);
				offlineDataLayout.setVisibility(View.GONE);
				
				if (checkOffline == true) {
					if (internetStatus.matches("true")) {
					offlineLayout.setVisibility(View.VISIBLE);
					offlineDataLayout.setVisibility(View.VISIBLE);
				    offlineLayout.setBackgroundResource(drawable.temp_offline_pattern_bg);
					}
				}
				
			} else if (onlineMode.matches("False")) {
				offlineLayout.setVisibility(View.VISIBLE);
				offlineDataLayout.setVisibility(View.VISIBLE);
			}
			}else{
				internetStatus = "false";
			}
		}else{
			internetStatus = "false";
		}
			return internetStatus;
		}	
	
	public void loadprogress(){
		spinnerLayout = new LinearLayout(Settings.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(settings_parent, false);
		progressBar = new ProgressBar(Settings.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
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
		Intent i = new Intent(Settings.this, LandingActivity.class);
		startActivity(i);
		Settings.this.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		menu.toggle();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(String item) {
		// TODO Auto-generated method stub
		menu.toggle();
	}
}