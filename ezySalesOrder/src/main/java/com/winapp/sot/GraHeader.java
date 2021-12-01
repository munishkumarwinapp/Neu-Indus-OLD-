package com.winapp.sot;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.winapp.SFA.R;
import com.winapp.SFA.R.drawable;
import com.winapp.fwms.CustomAlertAdapterSupp;
import com.winapp.fwms.DateWebservice;
import com.winapp.fwms.DrawableClickListener;
import com.winapp.fwms.FWMSSettingsDatabase;
import com.winapp.fwms.GetUserPermission;
import com.winapp.fwms.LandingActivity;
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.DateTime;
import com.winapp.helper.XMLParser;
import com.winapp.printer.CommonPreviewPrint;
import com.winapp.printer.PreviewPojo;
import com.winapp.printer.UIHelper;
import com.winapp.util.XMLAccessTask;
import com.winapp.util.XMLAccessTask.CallbackInterface;
import com.winapp.util.XMLAccessTask.ErrorType;

@SuppressLint("NewApi")
public class GraHeader extends SherlockFragmentActivity implements
SlideMenuFragment.MenuClickInterFace {

	String valid_url, serverdate, sDate, eDate, cuscode;
	int textlength = 0;
	int month, day, year;
	EditText edCustomerCode, starteditTextDate, endeditTextDate,sl_namefield;
	Button btnewsupplier, btcstmrsrch;
	ListView so_lv;
	ProgressBar progressBar;
	LinearLayout spinnerLayout;
	ArrayList<HashMap<String, String>> searchResults;
	ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
	CustomAlertAdapterSupp arrayAdapterSupp;
	private AlertDialog myalertDialog = null;
	LinearLayout salesO_parent, searchCstmrlayout, codelayout;

	Calendar startCalendar;
	Calendar endCalendar;

	ArrayList<HashMap<String, String>> getArraylsit = new ArrayList<HashMap<String, String>>();
	ArrayList<SO> searchCstCdArr = new ArrayList<SO>();
	ArrayList<String> cstmrgrpcdal = new ArrayList<String>();
	static String headeresult;
	private static String SOAP_ACTION = "http://tempuri.org/";
	private static String webMethName = "fncGetGRAHeader";
	private static String NAMESPACE = "http://tempuri.org/";
	boolean mnnbldsbl;
	ImageButton search, back, printer,addnew;
	// Cursor cursor;
	private UIHelper helper;
	String sosno, sodate, socustomercode, socustomername, soamount, sostatus;
	ArrayList<ProductDetails> product;
	ArrayList<ProductDetails> productdet;
	String jsonString = null, jsonStr = null;
	JSONObject jsonResponse, jsonResp;
	JSONArray jsonMainNode, jsonSecNode;
	HashMap<String, String> hashValue = new HashMap<String, String>();
	ArrayList<HashMap<String, String>> GraDetailsArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> GraHeadersArr = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> graBatchArr = new ArrayList<HashMap<String, String>>();
	GraAdapter gradapter;
	ArrayList<SO> list;
	String sno, date, customercode, amount, status,Deletestring = "", getPhotoimage = "", getSignatureimage="";
	private SlidingMenu menu;
	TextView totaloutstanding;
	private HashMap<String, String> params;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		  getActionBar().setIcon(drawable.ic_menu);
		  setContentView(R.layout.gra_activity);
		  
		  ActionBar ab = getSupportActionBar();
		  ab.setHomeButtonEnabled(true);
		  View customNav = LayoutInflater.from(this).inflate(
		    R.layout.slidemenu_actionbar_title, null);
		  TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		  txt.setText("GRA");
		  search = (ImageButton) customNav.findViewById(R.id.search_img);
		  printer = (ImageButton) customNav.findViewById(R.id.printer);
		  addnew = (ImageButton) customNav
		    .findViewById(R.id.custcode_img);
		totaloutstanding = (TextView)findViewById(R.id.totaloutstanding);

		  ab.setCustomView(customNav);
		  ab.setDisplayShowCustomEnabled(true);

		  ab.setDisplayHomeAsUpEnabled(false);
		  menu = new SlidingMenu(this);
		  menu.setMode(SlidingMenu.LEFT);
		  menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		  menu.setShadowWidthRes(R.dimen.shadow_width);
		  menu.setBehindOffsetRes(R.dimen.slidingmenu_width);
		  menu.setFadeDegree(0.35f);
		  menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		  menu.setMenu(R.layout.slidemenufragment);
		  menu.setSlidingEnabled(false);
		    
		  printer.setVisibility(View.GONE);
		  
		salesO_parent = (LinearLayout) findViewById(R.id.stockin_parent);
		searchCstmrlayout = (LinearLayout) findViewById(R.id.searchlayout);
		edCustomerCode = (EditText) findViewById(R.id.supcodefield);
		starteditTextDate = (EditText) findViewById(R.id.starteditTextDate);
		endeditTextDate = (EditText) findViewById(R.id.endeditTextDate);
	//	search = (ImageView) findViewById(R.id.search);
		codelayout = (LinearLayout) findViewById(R.id.codelayout);
		btnewsupplier = (Button) findViewById(R.id.button1);
		btcstmrsrch = (Button) findViewById(R.id.btsearch);
		//back = (ImageView) findViewById(R.id.back);
		so_lv = (ListView) findViewById(R.id.listView1);
		product = new ArrayList<ProductDetails>();
		productdet = new ArrayList<ProductDetails>();
		searchCstmrlayout.setVisibility(View.GONE);
		
		sl_namefield= (EditText) findViewById(R.id.sl_namefield);
		
		helper = new UIHelper(this);
		
		btnewsupplier.setVisibility(View.GONE);
		
		al.clear();
		searchCstCdArr.clear();
		startCalendar = Calendar.getInstance();
		endCalendar = Calendar.getInstance();
		FWMSSettingsDatabase.init(GraHeader.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new DateWebservice(valid_url,GraHeader.this);
		new SalesOrderWebService(valid_url);

		AsyncCallWSSalesOrder salesOAC = new AsyncCallWSSalesOrder();
		salesOAC.execute();

		totaloutstanding.setText(""+SO.getTotalamount());

		registerForContextMenu(so_lv);
		so_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {

			}
		});
		edCustomerCode
				.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
						edCustomerCode) {
					@Override
					public boolean onDrawableClick() {
						alertDialogSearch();
						return true;
					}
				});

		final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				startCalendar.set(Calendar.YEAR, year);
				startCalendar.set(Calendar.MONTH, monthOfYear);
				startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				strtDate();
			}
		};

		final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				endCalendar.set(Calendar.YEAR, year);
				endCalendar.set(Calendar.MONTH, monthOfYear);
				endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				edDate();
			}
		};

		starteditTextDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(GraHeader.this, startDate,
							startCalendar.get(Calendar.YEAR), startCalendar
									.get(Calendar.MONTH), startCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});

		endeditTextDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(GraHeader.this, endDate, endCalendar
							.get(Calendar.YEAR), endCalendar
							.get(Calendar.MONTH), endCalendar
							.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// searchlayout.setVisibility(1);
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (searchCstmrlayout.getVisibility() == View.VISIBLE) {
					// Its visible
					searchCstmrlayout.setVisibility(View.GONE);
					inputMethodManager.hideSoftInputFromWindow(
							codelayout.getWindowToken(), 0);
				} else {
					searchCstmrlayout.setVisibility(View.VISIBLE);
					// Either gone or invisible
					edCustomerCode.requestFocus();
					inputMethodManager.toggleSoftInputFromWindow(
							codelayout.getApplicationWindowToken(),
							InputMethodManager.SHOW_FORCED, 0);
				}

			}
		});

		btcstmrsrch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				cuscode = edCustomerCode.getText().toString();
				sDate = starteditTextDate.getText().toString();
				eDate = endeditTextDate.getText().toString();

//				if (sDate.matches("")) {
//					Toast.makeText(GraHeader.this, "Enter Start Date",
//							Toast.LENGTH_SHORT).show();
//				} else if (eDate.matches("")) {
//					Toast.makeText(GraHeader.this, "Enter End Date",
//							Toast.LENGTH_SHORT).show();
//				} else {
					AsyncCallWSSearchCustCode searchCustCode = new AsyncCallWSSearchCustCode();
					searchCustCode.execute();
//				}
			}
		});
		addnew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				clearSetterGetter();
				
				Intent callAddSupplier = new Intent(GraHeader.this,
						GraCustomer.class);
				startActivity(callAddSupplier);
				GraHeader.this.finish();
			}
		});
		/*back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent calllanding = new Intent(GraHeader.this,
						LandingActivity.class);
				startActivity(calllanding);
				GraHeader.this.finish();
			}
		});*/

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		SO so = gradapter.getItem(info.position);
		sosno = so.getSno().toString();
		sodate = so.getDate().toString();
		socustomercode = so.getCustomerCode().toString();
		menu.setHeaderTitle(sosno);
		menu.add(0, v.getId(), 0, "Print Preview");
		menu.add(0, v.getId(), 0, "Edit");
		menu.add(0, v.getId(), 0, "Delete");
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {

		if (item.getTitle() == "Print Preview") {
			helper.showProgressDialog(R.string.gra_printpreview);
			new AsyncPreviewCall().execute();
			for (HashMap<String, String> hashMap : al) {
				for (String key : hashMap.keySet()) {
					if (key.equals(socustomercode)) {
						System.out.println(hashMap.get(key));
						socustomername = hashMap.get(key);
						//SalesOrderSetGet.setCustname(socustomername);
					}
				}
			}
		}else if (item.getTitle() == "Edit") {
	
			for (HashMap<String, String> hashMap : al) {
				for (String key : hashMap.keySet()) {
					if (key.equals(socustomercode)) {
						System.out.println(hashMap.get(key));
						socustomername = hashMap.get(key);
					}
				}
			}
			
			SOTDatabase.init(GraHeader.this);
			SOTDatabase.deleteImage();
			SOTDatabase.deleteAllProduct();
			SOTDatabase.deleteBillDisc();
			SOTDatabase.deleteallbatch();
			
			ConvertToSetterGetter.setEdit_gra_no("");
			SalesOrderSetGet.setSuppliercode(socustomercode);
			SalesOrderSetGet.setCustname(socustomername);
			 
//			AsyncCallWSGRADetail task = new AsyncCallWSGRADetail();
//			task.execute();
			
			new AsyncCallWSGetSignature(false).execute();

		}else if (item.getTitle() == "Delete") {
			
			deleteAlertDialog();

		}else {
			return false;
		}
		return true;
	}
	
	
	private class AsyncCallWSGetSignature extends AsyncTask<Void, Void, Void> {

		boolean isPrintCall;
		public AsyncCallWSGetSignature(boolean printCall) {
			isPrintCall = printCall;
		}

		@Override
		protected void onPreExecute() {
			loadprogress();
			getPhotoimage = "";
			getSignatureimage="";
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String cmpy = SupplierSetterGetter.getCompanyCode();
			params = new HashMap<String, String>();
			params.put("CompanyCode", cmpy);
			params.put("InvoiceNo", sosno);
			params.put("TranType", "GRA");
			Log.d("sosno", "" + sosno);
//			
//			if (onlineMode.matches("True")) {
//				if (checkOffline == true) { //temp_offline
//					
//					if (dialogStatus.matches("true")) {
//						//need					
//					} else {
//						
//						finish();
//					}
//
//				} else {  //Onllineine
					
					new XMLAccessTask(GraHeader.this, valid_url,
							"fncGetInvoicePhoto", params, false,
							new GetSalesOrderImage()).execute();
					
					new XMLAccessTask(GraHeader.this, valid_url,
							"fncGetInvoiceSignature", params, false,
							new GetSalesOrderSignature(isPrintCall)).execute();
//				}
//
//			} else if (onlineMode.matches("False")) {  // permanent_offline
//				
//				//need
//			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			// if (!isPrintCall) {
			// Log.d("!isPrintCall", "!isPrintCall");
			new AsyncCallWSGRADetail().execute();
			// }

		}
	}
	
	public class GetSalesOrderImage implements CallbackInterface {
		public GetSalesOrderImage() {
		}
		@Override
		public void onSuccess(NodeList nl) {

			getPhotoimage = "";
			for (int i = 0; i < nl.getLength(); i++) {

				Element e = (Element) nl.item(i);
				getPhotoimage = XMLParser.getValue(e, "RefPhoto");
			}

			Log.d("getPhotoimage", "getPhotoimage" + getPhotoimage);
//			if(isPrintCall){
//				
//					SOTDatabase.storeImage(1, getSignatureimage, "");
//					progressBar.setVisibility(View.GONE);
//					spinnerLayout.setVisibility(View.GONE);
//					enableViews(deliveryO_parent, true);
//					printCallDialog();
//				}
		}

		@Override
		public void onFailure(ErrorType error) {
//			if(isPrintCall){
//				progressBar.setVisibility(View.GONE);
//				spinnerLayout.setVisibility(View.GONE);
//				enableViews(deliveryO_parent, true);
//				printCallDialog();
//			}
			onError(error);
		}

	}

	public class GetSalesOrderSignature implements CallbackInterface {
		boolean isPrintCall;

		public GetSalesOrderSignature(boolean printCall) {
			isPrintCall = printCall;
		}

		@Override
		public void onSuccess(NodeList nl) {

			getSignatureimage = "";
			for (int i = 0; i < nl.getLength(); i++) {

				Element e = (Element) nl.item(i);
				getSignatureimage = XMLParser.getValue(e, "RefSignature");
			}

			Log.d("getSignatureimage", "getSignatureimage" + getSignatureimage);
			// if (isPrintCall) {

			SOTDatabase.storeImage(1, getSignatureimage, getPhotoimage);
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);
			// printCallDialog();
			// }
		}

		@Override
		public void onFailure(ErrorType error) {

			// if (isPrintCall) {
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);
			// printCallDialog();
			// }
			onError(error);
		}

	}
	
	private class AsyncCallWSGRADetail extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			
			GraDetailsArr.clear();
			GraHeadersArr.clear();
			cstmrgrpcdal.clear();
			/*spinnerLayout = new LinearLayout(GraHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			GraHeader.this.addContentView(spinnerLayout, new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesO_parent, false);
			progressBar = new ProgressBar(GraHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);*/

		}
 
		@Override
		protected Void doInBackground(Void... arg0) {
			Log.d("Gra No:", sosno);
			
			GraDetailsArr = SalesOrderWebService.getGraDetails(sosno,
					"fncGetGRADetail");
			GraHeadersArr = SalesOrderWebService.getGraHeader(sosno,
					"fncGetGRAHeaderByGRANo");
			
			graBatchArr = SalesOrderWebService.getGraBatch(sosno,
					"fncGetGRABatchDetail");
			
			try {
				cstmrgrpcdal = SalesOrderWebService.getSuppTermCode(
						socustomercode, "fncGetSupplier"); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(cstmrgrpcdal.size()>0){
				SalesOrderSetGet.setSuppliergroupcode(cstmrgrpcdal.get(1));
			}else{
				SalesOrderSetGet.setSuppliergroupcode("");
			}
			
				Intent i = new Intent(GraHeader.this, GraSummary.class);
				i.putExtra("GraDetails", GraDetailsArr);
				i.putExtra("GraHeader", GraHeadersArr);
				i.putExtra("GraBatch", graBatchArr);
				i.putExtra("getSignatureimage", getSignatureimage);
				i.putExtra("getPhotoimage", getPhotoimage);
				startActivity(i);
				GraHeader.this.finish();
		
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);

		}
	}
	
	public void deleteAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				GraHeader.this);
		builder.setTitle("Confirm Delete");
		builder.setIcon(R.mipmap.delete);
		builder.setMessage("Do you want to delete?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				AsyncCallWSDelete delete = new AsyncCallWSDelete();
				delete.execute();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

		private class AsyncCallWSDelete extends AsyncTask<Void, Void, Void> {
			@SuppressWarnings("deprecation")
			@Override
			protected void onPreExecute() {
				spinnerLayout = new LinearLayout(GraHeader.this);
				spinnerLayout.setGravity(Gravity.CENTER);
				GraHeader.this.addContentView(spinnerLayout, new LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
				spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
				enableViews(salesO_parent, false);
				progressBar = new ProgressBar(GraHeader.this);
				progressBar.setProgress(android.R.attr.progressBarStyle);
				progressBar.setIndeterminateDrawable(getResources().getDrawable(
						drawable.greenprogress));
				spinnerLayout.addView(progressBar);

			}

			@Override
			protected Void doInBackground(Void... arg0) {

				String user = SupplierSetterGetter.getUsername();
				String cmpnyCode = SupplierSetterGetter.getCompanyCode();
				String LocationCode = SalesOrderSetGet.getLocationcode();
				HashMap<String, String> hashValue = new HashMap<String, String>();
				hashValue.put("CompanyCode", cmpnyCode);
				hashValue.put("LocationCode", LocationCode);
				hashValue.put("GraNo", sosno);
				hashValue.put("User", user);
				Log.d("getLocationcode", SalesOrderSetGet.getLocationcode());

				String jsonString = WebServiceClass.parameterService(hashValue,
						"fncDeleteGRA");

				try {

					jsonResponse = new JSONObject(jsonString);
					jsonMainNode = jsonResponse.optJSONArray("JsonArray");

					int lengthJsonArr = jsonMainNode.length();

					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode;
						jsonChildNode = jsonMainNode.getJSONObject(i);
						Deletestring = jsonChildNode.optString("Result").toString();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				if (!Deletestring.matches("")) {

					AsyncCallWSIOHeader task = new AsyncCallWSIOHeader();
					task.execute();

					Toast.makeText(GraHeader.this, "Delete Successfully",
							Toast.LENGTH_LONG).show();

				} else {

					progressBar.setVisibility(View.GONE);
					spinnerLayout.setVisibility(View.GONE);
					enableViews(salesO_parent, true);

					Toast.makeText(GraHeader.this, "Failed", Toast.LENGTH_LONG)
							.show();
					
				}

			}
		
	}
	
	private void strtDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		starteditTextDate.setText(sdf.format(startCalendar.getTime()));
	}

	private void edDate() {

		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		endeditTextDate.setText(sdf.format(endCalendar.getTime()));
	}

	private class AsyncCallWSSalesOrder extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			al.clear();

			spinnerLayout = new LinearLayout(GraHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesO_parent, false);
			progressBar = new ProgressBar(GraHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
			boolean check= isNetworkConnected();
			if(check == false){
				finish();
			}

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			al = SalesOrderWebService.grasuppwebservice("fncGetSupplier");
			serverdate = DateWebservice.getDateService("fncGetServerDate");
			
			  String companyCode = SupplierSetterGetter.getCompanyCode();
		         String locationCode = SalesOrderSetGet.getLocationcode();
		         try {
					GetUserPermission.getLocationHaveBatch("fncGetLocation",companyCode,locationCode);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (serverdate != null) {

//				starteditTextDate.setText(DateTime.date(serverdate));
//				endeditTextDate.setText(serverdate);
				
				starteditTextDate.setText("");  // added new
				endeditTextDate.setText("");

				AsyncCallWSIOHeader task = new AsyncCallWSIOHeader();
				task.execute();
			}

		}
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
	}
	public void alertDialogSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
		final EditText editText = new EditText(this);
		final ListView listview = new ListView(this);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Supplier");
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
		arrayAdapterSupp = new CustomAlertAdapterSupp(GraHeader.this, al);
		listview.setAdapter(arrayAdapterSupp);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				myalertDialog.dismiss();
				getArraylsit = arrayAdapterSupp.getArrayList();
				HashMap<String, String> datavalue = getArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String keyValues = (String) mapEntry.getKey();
					String name = (String) mapEntry.getValue();
					
					edCustomerCode.setText(keyValues);
					sl_namefield.setText(name);
					edCustomerCode.addTextChangedListener(new TextWatcher() {
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
							textlength = edCustomerCode.getText().length();
							sl_namefield.setText("");
						}
					});
				}
			}
		});

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

				arrayAdapterSupp = new CustomAlertAdapterSupp(GraHeader.this,
						searchResults);
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

	private class AsyncCallWSSearchCustCode extends AsyncTask<Void, Void, Void> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			spinnerLayout = new LinearLayout(GraHeader.this);
			spinnerLayout.setGravity(Gravity.CENTER);
			addContentView(spinnerLayout, new LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT));
			spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
			enableViews(salesO_parent, false);
			progressBar = new ProgressBar(GraHeader.this);
			progressBar.setProgress(android.R.attr.progressBarStyle);
			progressBar.setIndeterminateDrawable(getResources().getDrawable(
					drawable.greenprogress));
			spinnerLayout.addView(progressBar);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			searchCstCdArr = SalesOrderWebService.SearchGRACustCode(cuscode,
					sDate, eDate, "fncGetGRAHeader");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (!searchCstCdArr.isEmpty()) {
				try {
					searchCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				searchCstCdArr.clear();
				try {
					searchCustCode();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Toast.makeText(GraHeader.this, "No matches found",
						Toast.LENGTH_SHORT).show();
			}

			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);
		}
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

	public void searchCustCode() throws ParseException {

		if(!searchCstCdArr.isEmpty()){
			gradapter = new GraAdapter(GraHeader.this, R.layout.invoice_list_item_latest,
					searchCstCdArr);
			so_lv.setAdapter(gradapter);
		}
	}

	private class AsyncCallWSIOHeader extends AsyncTask<Void, Void, Void> {
		double totalamount = 0.00;
		@Override
		protected void onPreExecute() {
			list = new ArrayList<SO>();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			String fromdate = DateTime.date(serverdate);
			String cmpnyCode = SupplierSetterGetter.getCompanyCode();

			Log.d("fromdateValue","-->"+fromdate+"  -->"+serverdate);

			SoapObject request = new SoapObject(NAMESPACE, webMethName);

			PropertyInfo sDate = new PropertyInfo();
			PropertyInfo eDate = new PropertyInfo();
			PropertyInfo companyCode = new PropertyInfo();

			companyCode.setName("CompanyCode");
			companyCode.setValue(cmpnyCode);
			companyCode.setType(String.class);
			request.addProperty(companyCode);

			sDate.setName("FromDate");
			sDate.setValue(fromdate);
			sDate.setType(String.class);
			request.addProperty(sDate);

			eDate.setName("ToDate");
			eDate.setValue(serverdate);
			eDate.setType(String.class);
			request.addProperty(eDate);

			String suppTxt = null;
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(
					valid_url);
			try {
				androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
				SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
				suppTxt = response.toString();
				headeresult = " { GRAHeader : " + suppTxt + "}";
				JSONObject jsonResponse;
				Log.d("headeresult","-->"+headeresult);

				try {
					jsonResponse = new JSONObject(headeresult);
					JSONArray jsonMainNode = jsonResponse
							.optJSONArray("GRAHeader");
					double value = 0;
					int lengthJsonArr = jsonMainNode.length();
					Log.d("lengthJsonArr","--.>"+lengthJsonArr);
					for (int i = 0; i < lengthJsonArr; i++) {

						JSONObject jsonChildNode = jsonMainNode
								.getJSONObject(i);


						String ccSno = jsonChildNode.optString("GRANo")
								.toString();
						String ccDate = jsonChildNode.optString("GRADate")
								.toString();
						String customerCode = jsonChildNode.optString(
								"SupplierCode").toString();
						String SupplierName = jsonChildNode.optString(
								"SupplierName").toString();
						String amount = jsonChildNode.optString("NetTotal")
								.toString();
						String status = jsonChildNode.optString("Status")
								.toString();
						String bal_amount =jsonChildNode.optString("BalanceAmount").toString();
//						String bal_amount = null;
						Log.d("bal_amountValue","-->"+bal_amount);


						if(bal_amount.matches("0.00") ) {
							Log.d("bal_amount","-->"+bal_amount);
							bal_amount="0.00";
							value=Double.parseDouble(bal_amount);
						}else{
							value=Double.parseDouble(bal_amount);
						}


						SO so = new SO();
						so.setSno(ccSno);
						so.setCustomerCode(customerCode);
						so.setCustomerName(SupplierName);
						so.setNettotal(amount);
						so.setStatus(status);
						so.setBalance_amt(value);

						if (ccDate != null) {
							StringTokenizer tokens = new StringTokenizer(
									ccDate, " ");
							String date = tokens.nextToken();
							so.setDate(date);
						} else {
							so.setDate(ccDate);
						}
						double amt = Double.parseDouble(amount);
						Log.d("AmountCheck",""+amt);
						if(amt>0){
							totalamount = totalamount+amt;
						}

						SO.setTotalamount(Double.parseDouble(twoDecimalPoint(totalamount)));

						list.add(so);
					}

				} catch (JSONException e) {

					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				suppTxt = "Error occured";
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			try {
				headerCustCode();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(salesO_parent, true);

		}
	}

	public String twoDecimalPoint(double d) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setMinimumFractionDigits(2);
		String tax = df.format(d);
		return tax;
	}

	public void headerCustCode() throws ParseException {

		if(!list.isEmpty()){
//			gradapter = new GraAdapter(GraHeader.this, R.layout.gra_list_item, list);
			gradapter = new GraAdapter(GraHeader.this, R.layout.invoice_list_item_latest, list);
			so_lv.setAdapter(gradapter);
		}
		if (SO.getTotalamount() > 0) {
			totaloutstanding.setText(""+SO.getTotalamount());
		}else {
			totaloutstanding.setText("0.00");
		}
	}
 
	private class AsyncPreviewCall extends
			AsyncTask<Void, Void, ArrayList<String>> {
		@Override
		protected void onPreExecute() {
			product.clear();
			productdet.clear();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {

			String cmpnyCode = SupplierSetterGetter.getCompanyCode();
			hashValue.put("CompanyCode", cmpnyCode);
			hashValue.put("GRANo", sosno);

			jsonString = SalesOrderWebService.getSODetail(hashValue,
					"fncGetGRADetail");
			jsonStr = SalesOrderWebService.getSODetail(hashValue,
					"fncGetGRAHeaderByGRANo");

			Log.d("jsonString ", jsonString);
			Log.d("jsonStr ", jsonStr);

			try {

				jsonResponse = new JSONObject(jsonString);
				jsonMainNode = jsonResponse.optJSONArray("SODetails");

				jsonResp = new JSONObject(jsonStr);
				jsonSecNode = jsonResp.optJSONArray("SODetails");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// *********** Process each JSON Node ************//
			int lengthJsonArr = jsonMainNode.length();
			for (int i = 0; i < lengthJsonArr; i++) {
				// ****** Get Object for each JSON node. ***********//
				JSONObject jsonChildNode;
				ProductDetails productdetail = new ProductDetails();
				try {
					jsonChildNode = jsonMainNode.getJSONObject(i);

					productdetail.setItemcode(jsonChildNode.optString(
							"ProductCode").toString());
					productdetail.setDescription(jsonChildNode.optString(
							"ProductName").toString());
					productdetail.setQty(jsonChildNode.optString("Qty")
							.toString());
					productdetail.setPrice(jsonChildNode
							.optString("GrossPrice").toString());
					productdetail.setTotal(jsonChildNode.optString("Total")
							.toString());
					product.add(productdetail);

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

			int lengJsonArr = jsonSecNode.length();
			for (int i = 0; i < lengJsonArr; i++) {

				JSONObject jsonChildNode;
				ProductDetails productdetail = new ProductDetails();
				try {
					jsonChildNode = jsonSecNode.getJSONObject(i);
					productdetail.setItemdisc(jsonChildNode.optString(
							"ItemDiscount").toString());
					productdetail.setBilldisc(jsonChildNode.optString(
							"BillDIscount").toString());
					productdetail.setSubtotal(jsonChildNode.optString(
							"SubTotal").toString());
					productdetail.setTax(jsonChildNode.optString("Tax")
							.toString());
					productdetail.setNettot(jsonChildNode.optString("NetTotal")
							.toString());
					productdet.add(productdetail);

				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
			hashValue.clear();
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			helper.dismissProgressDialog();
			Intent i = new Intent(GraHeader.this, CommonPreviewPrint.class);
			i.putExtra("title", "GRA");
			i.putExtra("No", sosno);
			i.putExtra("Date", sodate);
			i.putExtra("cus_remarks","");
			i.putExtra("customerCode", socustomercode);
			i.putExtra("customerName", socustomername);
			i.putExtra("Invoicetype", "Consignment");
			PreviewPojo.setProducts(product);
			PreviewPojo.setProductsDetails(productdet);
			startActivity(i);

		}
	}

	public void clearSetterGetter() {
		SalesOrderSetGet.setCustomercode("");
		SalesOrderSetGet.setCustomername("");
		SalesOrderSetGet.setGradono("");
		SalesOrderSetGet.setGrainvoiceno("");
		SalesOrderSetGet.setGrainvoicedate("");
		SalesOrderSetGet.setGradodate("");
		SalesOrderSetGet.setSaleorderdate("");
		SalesOrderSetGet.setCurrencycode("");
		SalesOrderSetGet.setCurrencyname("");
		SalesOrderSetGet.setCurrencyrate("");
		SalesOrderSetGet.setRemarks("");
		SOTDatabase.init(GraHeader.this);
		SOTDatabase.deleteImage();
		SOTDatabase.deleteAllProduct();
		SOTDatabase.deleteBillDisc();
		SOTDatabase.deleteallbatch();
		SOTDatabase.deleteBarcode();

		ConvertToSetterGetter.setEdit_gra_no("");
	}
	
	public void loadprogress(){
		spinnerLayout = new LinearLayout(GraHeader.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(salesO_parent, false);
		progressBar = new ProgressBar(GraHeader.this);
		progressBar.setProgress(android.R.attr.progressBarStyle);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				drawable.greenprogress));
		spinnerLayout.addView(progressBar);
	}
	
	private void onError(final ErrorType error) {
		new Thread() {
			@Override
			public void run() {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						if (error == ErrorType.NETWORK_UNAVAILABLE) {
							helper.showLongToast(R.string.error_showing_image_no_network_connection);
						} else {

						}
					}
				});
			}
		}.start();
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(GraHeader.this, LandingActivity.class);
		startActivity(i);
		GraHeader.this.finish();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		menu.toggle();
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onListItemClick(String item) {
		// TODO Auto-generated method stub
		 menu.toggle();
	}
}
