package com.winapp.sotdetails;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import com.winapp.fwms.SupplierSetterGetter;
import com.winapp.helper.XMLParser;
import com.winapp.offline.OfflineSettingsManager;
import com.winapp.sot.CaptureSignature;
import com.winapp.sot.SOTDatabase;
import com.winapp.sot.SOTSummaryWebService;
import com.winapp.sot.SalesOrderWebService;
import com.winapp.sot.SlideMenuFragment;
import com.winapp.util.XMLAccessTask;
import com.winapp.util.XMLAccessTask.CallbackInterface;
import com.winapp.util.XMLAccessTask.ErrorType;

public class CRMTaskAdd extends SherlockFragmentActivity implements SlideMenuFragment.MenuClickInterFace, LocationListener{
	private SlidingMenu menu;
	private ProgressBar progressBar;
	private LinearLayout spinnerLayout, crmtask_parent, linear_gallery;
	private ScrollView linear_details;
	private ImageButton search_icon, printer_icon, addnew_icon;
	private TextView task_detail, task_gallery, done_icon, task_address_txt;
	private EditText task_id_ed, task_user_ed, task_customer_ed, task_name_ed, task_desc_ed, task_date_ed, task_duedate_ed;
	private ImageView camera_icon, camera_image, sign_icon, sign_image, map_icon; 
	private Spinner task_priority_ed, task_status_ed;
	private String valid_url="", companyCode="", loginUsername="", serverdate="", provider, task_id="", task_user="", task_customer="", task_name="", task_date="", task_desc="", task_duedate="", 
			task_priority="", task_status="", saveResult = "", address1="", address2="", signature_img="", product_img="", crm_status="";
	private double setLatitude, setLongitude;
	private int textlength = 0;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int SIGNATURE_ACTIVITY = 2;
	
	private OfflineSettingsManager spManager;
	private Calendar taskdateCalendar, duedateCalendar;
	private AlertDialog userAlertDialog = null, customertAlertDialog = null;
	private CustomAlertAdapterSupp userAdapter, customerAdapter;
	
	private ArrayList<HashMap<String, String>> mArraylist = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> getUserArraylsit = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> getCutomerArraylsit = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> userlist = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> customerlist = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> searchResults;
	
	// get location
	private LocationManager locationManager;
	private boolean isGPSEnabled = false, isNetworkEnabled = false;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(drawable.ic_menu);
		setContentView(R.layout.add_crmtask);

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		View customNav = LayoutInflater.from(this).inflate(
				R.layout.slidemenu_actionbar_title, null);
		TextView txt = (TextView) customNav.findViewById(R.id.pageTitle);
		txt.setText("CRM Task");
		
		search_icon = (ImageButton) customNav.findViewById(R.id.search_img);
		printer_icon = (ImageButton) customNav.findViewById(R.id.printer);
		addnew_icon = (ImageButton) customNav.findViewById(R.id.custcode_img);
		done_icon = (TextView) customNav.findViewById(R.id.done_icon); 
		search_icon.setVisibility(View.GONE);
		printer_icon.setVisibility(View.GONE);
		addnew_icon.setVisibility(View.GONE);
		done_icon.setVisibility(View.VISIBLE);
		
		ab.setCustomView(customNav);
		ab.setDisplayShowCustomEnabled(true);
		ab.setBackgroundDrawable(getResources().getDrawable(
				drawable.task_header_bg));
		
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
		
		crmtask_parent = (LinearLayout) findViewById(R.id.crmtask_parent); 
		linear_details = (ScrollView) findViewById(R.id.linear_details); 
		linear_gallery = (LinearLayout) findViewById(R.id.linear_gallery); 
		task_detail = (TextView) findViewById(R.id.task_detail_title); 
		task_gallery = (TextView) findViewById(R.id.task_gallery_title); 
		task_address_txt = (TextView) findViewById(R.id.task_address_txt);
		
		task_id_ed = (EditText) findViewById(R.id.task_id_ed); 
		task_user_ed = (EditText) findViewById(R.id.task_user_ed); 
		task_customer_ed = (EditText) findViewById(R.id.task_customer_ed); 
		task_name_ed = (EditText) findViewById(R.id.task_name_ed); 
		task_desc_ed = (EditText) findViewById(R.id.task_desc_ed); 
		task_date_ed = (EditText) findViewById(R.id.task_date_ed); 
		task_duedate_ed = (EditText) findViewById(R.id.task_duedate_ed); 
		
		task_priority_ed = (Spinner) findViewById(R.id.task_priority_ed); 
		task_status_ed = (Spinner) findViewById(R.id.task_status_ed); 
		
		camera_icon = (ImageView) findViewById(R.id.camera_icon);
		camera_image = (ImageView) findViewById(R.id.camera_image);
		sign_icon = (ImageView) findViewById(R.id.sign_icon);
		sign_image = (ImageView) findViewById(R.id.sign_image);
		map_icon = (ImageView) findViewById(R.id.map_icon);
		
		task_detail.setBackgroundResource(drawable.crm_tab_scrollbar);
		task_gallery.setBackgroundResource(R.color.white);
		task_gallery.setTextColor(Color.parseColor("#b2b1b5"));
		
		FWMSSettingsDatabase.init(CRMTaskAdd.this);
		valid_url = FWMSSettingsDatabase.getUrl();
		new SalesOrderWebService(valid_url);
		new SOTSummaryWebService(valid_url);
		new DateWebservice(valid_url,CRMTaskAdd.this);
		SOTDatabase.init(CRMTaskAdd.this);
		spManager = new OfflineSettingsManager(CRMTaskAdd.this);
		companyCode = spManager.getCompanyType();
		loginUsername = SupplierSetterGetter.getUsername();
		taskdateCalendar = Calendar.getInstance();
		duedateCalendar = Calendar.getInstance();

        Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Log.d("extras", "Extra NULL");		
		} else {			
			Log.d("extras", "Extra NOT NULL");
			crm_status = extras.getString("CRMStatus"); 
			Log.d("crm_status", "Extra "+crm_status);
			if (crm_status.matches("TaskAdd")) {
				viewAll(true);
				done_icon.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.crm_tick, 0, 0, 0);
				done_icon.setText("Done");
				Log.d("bundle edit task_id", "--> "+task_id);
				task_id = "";
				task_id_ed.setText("");
				task_user_ed.setText(loginUsername);
				
			}else if (crm_status.matches("TaskDetail")) {
				viewAll(false);
				done_icon.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.edit_ic, 0, 0, 0);
				done_icon.setText("Edit");
				task_id = extras.getString("TaskID");
				Log.d("bundle detail task_id", "--> "+task_id);
			}else{
				Log.d("crm_status", "else "+crm_status);
			}
		}
		
		task_status_ed.setSelection(2);
		
        UserAsyncCall userAsync = new UserAsyncCall();
		userAsync.execute();
		
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
					Log.d("GPS Enabled", "GPS Enabled");
					if (locationManager != null) {
						Location location = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							Log.d("location", "not null");
							// if (onlineMode.matches("True")) {
							// if (checkOffline == true) {
							// } else { // online
							onLocationChanged(location);
							// }
							// }
						}
					}
				} else if(isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 20000, 0, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						Location location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							Log.d("location", "not null");
							// if (onlineMode.matches("True")) {
							// if (checkOffline == true) {
							// } else { // online
							onLocationChanged(location);
							// }
							// }
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/** Onclick Start **/
		task_detail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				task_detail.setBackgroundResource(drawable.crm_tab_scrollbar);
				task_gallery.setBackgroundResource(R.color.white);
				task_gallery.setTextColor(Color.parseColor("#b2b1b5"));
				linear_details.setVisibility(View.VISIBLE);
				linear_gallery.setVisibility(View.GONE);
			}
		});
		
		task_gallery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				task_detail.setTextColor(Color.parseColor("#b2b1b5"));
				task_detail.setBackgroundResource(R.color.white);
				task_gallery.setBackgroundResource(drawable.crm_tab_scrollbar);
				linear_details.setVisibility(View.GONE);
				linear_gallery.setVisibility(View.VISIBLE);
			}
		});
		
		task_user_ed.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
				task_user_ed) {
			@Override
			public boolean onDrawableClick() {
				alertDialogUserSearch();
				return true;
			}
		});   
		
		task_customer_ed.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(
				task_customer_ed) {
			@Override
			public boolean onDrawableClick() {
				alertDialogCustomerSearch();
				return true;
			}
		});
		
		final DatePickerDialog.OnDateSetListener taskDate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				taskdateCalendar.set(Calendar.YEAR, year);
				taskdateCalendar.set(Calendar.MONTH, monthOfYear);
				taskdateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				taskDate();
			}
		};

		final DatePickerDialog.OnDateSetListener dueDate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				duedateCalendar.set(Calendar.YEAR, year);
				duedateCalendar.set(Calendar.MONTH, monthOfYear);
				duedateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				dueDate();
			}
		};

		task_date_ed.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(CRMTaskAdd.this, taskDate,
							taskdateCalendar.get(Calendar.YEAR), taskdateCalendar
									.get(Calendar.MONTH), taskdateCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});

		task_duedate_ed.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_UP == event.getAction())
					new DatePickerDialog(CRMTaskAdd.this, dueDate,
							duedateCalendar.get(Calendar.YEAR), duedateCalendar
									.get(Calendar.MONTH), duedateCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				return false;
			}
		});
		
		camera_icon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CameraAction();
			}
		});

		sign_icon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent i = new Intent(CRMTaskAdd.this, CaptureSignature.class);
				startActivityForResult(i, SIGNATURE_ACTIVITY);

			}
		});
		
		done_icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String mStatus = done_icon.getText().toString();
				if(mStatus.matches("Edit")){   // Edit button click
					viewAll(true);
					done_icon.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.crm_tick, 0, 0, 0);
					done_icon.setText("Done");
				}else if(mStatus.matches("Done")){   // Done button click
					
					task_user=""; task_name=""; task_date=""; task_desc=""; task_duedate=""; task_priority="";
					task_id = task_id_ed.getText().toString();
					task_user = task_user_ed.getText().toString();
					task_customer = task_customer_ed.getText().toString();
					task_name = task_name_ed.getText().toString();
					task_desc = task_desc_ed.getText().toString();
					task_date = task_date_ed.getText().toString();
					task_duedate = task_duedate_ed.getText().toString();
					prioritySpinnerselection();
					statusSpinnerselection();
					if (task_user.matches("")) {
						Toast.makeText(CRMTaskAdd.this, "Enter Task User", Toast.LENGTH_SHORT).show();
					} else if (task_name.matches("")) {
						Toast.makeText(CRMTaskAdd.this, "Enter Task Name", Toast.LENGTH_SHORT).show();
					} else if (task_desc.matches("")) {
						Toast.makeText(CRMTaskAdd.this, "Enter Description", Toast.LENGTH_SHORT).show();
					}  else {
						SaveAsyncCall save = new SaveAsyncCall();
						save.execute();
					}
					
				}
			}
		});
		
		/** Onclick End **/
		
	}
	
/***   AsyncTask Start    ***/
	
	public class UserAsyncCall extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			userlist.clear();
			customerlist.clear();
			serverdate="";
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("CompanyCode", companyCode);
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("Code", "W!napp@!@#^");
			
			customerlist = SalesOrderWebService.getWholeCustomer(hm, "fncGetCustomer");
			userlist = SalesOrderWebService.getAllUser("fncGetUserMaster",params);
			serverdate = DateWebservice.getDateService("fncGetServerDate");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (serverdate != null) {
				task_date_ed.setText(serverdate);
				task_duedate_ed.setText(serverdate);	
			} 
			
			Log.d("UserAsyncCall task_id", "--> "+task_id);
			
			if(task_id != null && !task_id.isEmpty()){
				CommonAsyncCall common = new CommonAsyncCall();
				common.execute();
			}else{
				progressBar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				enableViews(crmtask_parent, true);
			}
			
			
		}
	}
	
	private class CommonAsyncCall extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			mArraylist.clear();	
//			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				
				Log.d("CommonAsyncCall task_id", "--> "+task_id);
				
				HashMap<String, String> hmvalue = new HashMap<String, String>();
			    hmvalue.put("CompanyCode", companyCode);
			    hmvalue.put("TaskID", task_id);
			    hmvalue.put("TaskUser", "");
				mArraylist = SalesOrderWebService.getTaskList(hmvalue,"fncGetCRMTasks");
			
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("listArray Result", mArraylist.toString());
			try {
				if (!mArraylist.isEmpty()) {   
				HashMap<String, String> value = mArraylist.get(0);
					task_id_ed.setText(value.get("TaskID"));
					task_user_ed.setText(value.get("TaskUser")); 
					task_customer_ed.setText(value.get("CustomerCode")); 
					task_name_ed.setText(value.get("TaskName")); 
					task_desc_ed.setText(value.get("TaskDescription"));
					String tDate = value.get("TaskDate");
					String dDate = value.get("DueDate");
					
					SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
					SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy");
					Date mDate=null, nDate=null;
					String taskDate="", dueDate="";
					   try {
					       mDate = input.parse(tDate); // parse input 
					       nDate = input.parse(dDate); 
					       taskDate = output.format(mDate);    // format output
					       dueDate = output.format(nDate);
					   } catch (ParseException e) {
					       e.printStackTrace();
					   }
					
					task_date_ed.setText(taskDate);
					task_duedate_ed.setText(dueDate);
					
					int priority = Integer.parseInt(value.get("TaskPriority"));
					int status = Integer.parseInt(value.get("TaskStatus")); 
					
					task_priority_ed.setSelection(priority);
					task_status_ed.setSelection(status);
					
					SOTDatabase.deleteImage();

				 new AsyncCallWSGetSignature().execute();
					
					
				}else{
					progressBar.setVisibility(View.GONE);
					spinnerLayout.setVisibility(View.GONE);
					enableViews(crmtask_parent, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	
	private class SaveAsyncCall extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			saveResult="";
			loadprogress();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				Log.d("SaveAsyncCall task_id", "--> "+task_id);
				
				HashMap<String, String> hmvalue = new HashMap<String, String>();
			    hmvalue.put("CompanyCode", companyCode);
			    hmvalue.put("TaskID", task_id);
			    hmvalue.put("TaskDate", task_date);
			    hmvalue.put("TaskUser", task_user);
			    hmvalue.put("TaskName", task_name);
			    hmvalue.put("DueDate", task_duedate);
			    hmvalue.put("TaskPriority", task_priority);
			    hmvalue.put("TaskStatus", task_status);
			    hmvalue.put("TaskDescription", task_desc);
//			    hmvalue.put("Latitude", setLatitude+"");
//			    hmvalue.put("Longitude", setLongitude+"");
			    hmvalue.put("User", loginUsername);
			    hmvalue.put("CustomerCode", task_customer);
			    
			    saveResult = SalesOrderWebService.saveCrmTask(hmvalue,"fncSaveCRMTasks");
			
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			Log.d("save Result", "-->"+saveResult);
			try {
				
				if (saveResult.matches("failed")) {
					Toast.makeText(CRMTaskAdd.this, "Failed", Toast.LENGTH_LONG).show();
				}else{
					
					signature_img = SOTDatabase.getSignatureImage();
					product_img = SOTDatabase.getProductImage();
					
					if(signature_img==null){
						signature_img ="";
					}
					if(product_img==null){
						product_img="";
					}

//					if (onlineMode.matches("True")) {
//						if (checkOffline == true) {
//						} else { // online
							String imgResult = SOTSummaryWebService.saveSignatureImage(
									saveResult, "" + setLatitude, "" + setLongitude,
									signature_img, product_img, "fncSaveInvoiceImages", "TK", address1, address2);

							Log.d("fncSaveInvoiceImages", "" + saveResult + " "
									+ setLatitude + " " + setLongitude + "TK" + address1 + address2 +"signature_img "
									+ signature_img + "product_img " + product_img);

							if (!imgResult.matches("")) {
								Log.d("Cap Image", "Saved");
							} else {
								Log.d("Cap Image", "Not Saved");
							}
//						}
//					}
							
							clear();
							Intent i = new Intent(CRMTaskAdd.this, CRMTaskActivity.class);
							startActivity(i);
							CRMTaskAdd.this.finish();
				}
					
			} catch (Exception e) {
				e.printStackTrace();
			}
			progressBar.setVisibility(View.GONE);
			spinnerLayout.setVisibility(View.GONE);
			enableViews(crmtask_parent, true);
		}
	}
	
		private class AsyncCallWSGetSignature extends AsyncTask<Void, Void, Void> {
			
				@Override
				protected void onPreExecute() {
				}
		
			 	@Override
				protected Void doInBackground(Void... arg0) {
		
					HashMap<String, String> params = new HashMap<String, String>();			
					params.put("CompanyCode", companyCode);
					params.put("InvoiceNo", task_id);
					params.put("TranType", "TK");
					Log.d("task_id", ""+task_id);
					new XMLAccessTask(CRMTaskAdd.this, valid_url,
							"fncGetInvoiceSignature", params, false, new GetInvoiceSignature()).execute();
		
					return null;
				}
		
				@Override
				protected void onPostExecute(Void result) {
					
					
				
					progressBar.setVisibility(View.GONE);
					spinnerLayout.setVisibility(View.GONE);
					enableViews(crmtask_parent, true);
				}
			}
		
		public class GetInvoiceSignature implements CallbackInterface {
			
			@Override
			public void onSuccess(NodeList nl) {
		
				String getSignatureimage = "";
				for (int i = 0; i < nl.getLength(); i++) {
		
					Element e = (Element) nl.item(i);
					getSignatureimage = XMLParser.getValue(e, "RefSignature");
				}
		
				Log.d("getSignatureimage", "getSignatureimage" + getSignatureimage);
				SOTDatabase.storeImage(1, getSignatureimage, "");
				
				signature_img = SOTDatabase.getSignatureImage();
				product_img = SOTDatabase.getProductImage();
				
				if (signature_img != null && !signature_img.isEmpty()) {
					Log.d("invoice sum sign if ", signature_img);
					try {
					byte[] encodeByte = Base64.decode(signature_img,Base64.DEFAULT);
		
					Bitmap photo = BitmapFactory.decodeByteArray(encodeByte, 0,
							encodeByte.length);
					photo = Bitmap.createScaledBitmap(photo, 240, 80, true);
					sign_image.setImageBitmap(photo);
					
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				} else {
		
				}
				
					}

			@Override
			public void onFailure(ErrorType error) {
				onError(error);
			}
			}
		
			
	/***   AsyncTask End    ***/	
	
	private void clear(){
		task_id_ed.setText("");
		task_user_ed.setText("");
		task_customer_ed.setText("");
		task_name_ed.setText("");
		task_desc_ed.setText("");
		task_date_ed.setText("");
		task_duedate_ed.setText("");
		task_priority_ed.setSelection(0);
		task_status_ed.setSelection(0);
		SOTDatabase.deleteImage();
	}
	
	private void taskDate() {
		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		task_date_ed.setText(sdf.format(taskdateCalendar.getTime()));
	}

	private void dueDate() {
		String myFormat = "dd/MM/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
		task_duedate_ed.setText(sdf.format(duedateCalendar.getTime()));
	}
	
	private void statusSpinnerselection() {
		String status = task_status_ed.getSelectedItem().toString();
		
		if (status.matches("Open")) {
			task_status = "0";
		}
		if (status.matches("In Progress")) {
			task_status = "1";
		}
		if (status.matches("Completed")) {
			task_status = "2";
		}
		if (status.matches("Hold")) {
			task_status = "3";
		}
//		0 - Open , 1 - In Progress , 2 - Completed , 3 - Hold
	}
	
	private void prioritySpinnerselection() {
		String priority= task_priority_ed.getSelectedItem().toString();
		
		if (priority.matches("Low")) {
			task_priority = "0";
		}
		if (priority.matches("Medium")) {
			task_priority = "1";
		}
		if (priority.matches("High")) {
			task_priority = "2";
		}
	}
	
	private void alertDialogUserSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				CRMTaskAdd.this);
		final EditText editText = new EditText(CRMTaskAdd.this);
		final ListView listview = new ListView(CRMTaskAdd.this);
		LinearLayout layout = new LinearLayout(CRMTaskAdd.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("User");
		editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,
				0, 0);
		layout.addView(editText);
		layout.addView(listview);
		myDialog.setView(layout);

		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					userAlertDialog
							.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		userAdapter = new CustomAlertAdapterSupp(CRMTaskAdd.this, userlist);
		listview.setAdapter(userAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				userAlertDialog.dismiss();
				getUserArraylsit = userAdapter.getArrayList();
				HashMap<String, String> datavalue = getUserArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String keyValues = (String) mapEntry.getKey();
					mapEntry.getValue();
					task_user_ed.setText(keyValues);
					task_user_ed.addTextChangedListener(new TextWatcher() {
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
							textlength = task_user_ed.getText().length();
						}
					});
				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(userlist);
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
				for (int i = 0; i < userlist.size(); i++) {
					String supplierName = userlist.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(userlist.get(i));
					}
				}

				userAdapter = new CustomAlertAdapterSupp(CRMTaskAdd.this, searchResults);
				listview.setAdapter(userAdapter);
			}
		});
		myDialog.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		userAlertDialog = myDialog.show();
	}
	
	/** Display Customer List  **/
	
	private void alertDialogCustomerSearch() {
		AlertDialog.Builder myDialog = new AlertDialog.Builder(
				CRMTaskAdd.this);
		final EditText editText = new EditText(CRMTaskAdd.this);
		final ListView listview = new ListView(CRMTaskAdd.this);
		LinearLayout layout = new LinearLayout(CRMTaskAdd.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		myDialog.setTitle("Customer");
		editText.setCompoundDrawablesWithIntrinsicBounds(drawable.search, 0,
				0, 0);
		layout.addView(editText);
		layout.addView(listview);
		myDialog.setView(layout);

		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					customertAlertDialog
							.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		customerAdapter = new CustomAlertAdapterSupp(CRMTaskAdd.this, customerlist);
		listview.setAdapter(customerAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				customertAlertDialog.dismiss();
				getCutomerArraylsit = customerAdapter.getArrayList();
				HashMap<String, String> datavalue = getCutomerArraylsit.get(position);
				Set<Entry<String, String>> keys = datavalue.entrySet();
				Iterator<Entry<String, String>> iterator = keys.iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry mapEntry = iterator.next();
					String keyValues = (String) mapEntry.getKey();
					mapEntry.getValue();
					task_customer_ed.setText(keyValues);
					task_customer_ed.addTextChangedListener(new TextWatcher() {
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
							textlength = task_customer_ed.getText().length();
						}
					});
				}
			}
		});

		searchResults = new ArrayList<HashMap<String, String>>(customerlist);
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
				for (int i = 0; i < customerlist.size(); i++) {
					String supplierName = customerlist.get(i).toString();
					if (textlength <= supplierName.length()) {
						if (supplierName.toLowerCase().contains(
								editText.getText().toString().toLowerCase()
										.trim()))
							searchResults.add(customerlist.get(i));
					}
				}

				customerAdapter = new CustomAlertAdapterSupp(CRMTaskAdd.this, searchResults);
				listview.setAdapter(customerAdapter);
			}
		});
		myDialog.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		customertAlertDialog = myDialog.show();
	}
	
	public void CameraAction() {
		/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 0);
		intent.putExtra("aspectY", 0);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 150);
		try {
			intent.putExtra("return-data", true);
			startActivityForResult(intent, PICK_FROM_CAMERA);
		} catch (ActivityNotFoundException e) {
		}*/
		
		try {				
			 Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	         if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	             startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
	         }
	         
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {

			case PICK_FROM_CAMERA:
				if (requestCode == PICK_FROM_CAMERA) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						Bitmap photo = extras.getParcelable("data");
						photo = Bitmap.createScaledBitmap(photo, 95, 80, true);
						camera_image.setImageBitmap(photo);

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] bitMapData = stream.toByteArray();
						String camera_image = Base64.encodeToString(bitMapData,
								Base64.DEFAULT);
						SOTDatabase.init(CRMTaskAdd.this);

						Cursor ImgCursor = SOTDatabase.getImageCursor();
						if (ImgCursor.getCount() > 0) {
							String signature_image = SOTDatabase.getSignatureImage();
							SOTDatabase.updateImage(1, signature_image, camera_image);
						} else {
							SOTDatabase.storeImage(1, "", camera_image);
						}

						Log.d("Camera Image", "cam" + camera_image);
					}
				}
				break;

			case SIGNATURE_ACTIVITY:
				if (resultCode == RESULT_OK) {
					//Bundle extras = data.getExtras();
					 byte[] bytes = data.getByteArrayExtra("status");
					if (bytes != null) {
						//Bitmap photo = extras.getParcelable("status");
								
						Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
						bitmap = Bitmap.createScaledBitmap(bitmap, 240, 80, true);							
						
						sign_image.setImageBitmap(bitmap);
						
						 ByteArrayOutputStream stream = new ByteArrayOutputStream();
							bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] bitMapData = stream.toByteArray();
						String signature_image = Base64.encodeToString(
								bitMapData, Base64.DEFAULT);
						SOTDatabase.init(CRMTaskAdd.this);
						Cursor ImgCursor = SOTDatabase.getImageCursor();
						if (ImgCursor.getCount() > 0) {
							String camera_image = SOTDatabase.getProductImage();
							SOTDatabase.updateImage(1, signature_image,
									camera_image);
						} else {
							SOTDatabase.storeImage(1, signature_image, "");
						}

						Log.d("Signature Image", "Sig" + signature_image);
					}
				}
				break;
			}
		}
	}
	
	public void getAddress(double latitude, double longitude) throws Exception {
		Log.d("getaddress", "gps "+latitude);
		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(this, Locale.getDefault());
		
		setLatitude = latitude;		
		setLongitude = longitude;
		
		addresses = geocoder.getFromLocation(latitude, longitude, 1);
		if (addresses != null && addresses.size() > 0) {

			address1 = addresses.get(0).getAddressLine(0);
			address2 = addresses.get(0).getAddressLine(1);

			task_address_txt.setText(address1 + "," + address2);
			 locationManager.removeUpdates(this);

		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// Getting reference to TextView tv_longitude
		Log.d("Location", "gps "+location.getLatitude());
		try {
			
//			if (onlineMode.matches("True")) {
//				if (checkOffline == true) {
//				} else { // online
					getAddress(location.getLatitude(), location.getLongitude());
//				}
//			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub		
	}
	
	 /* Request updates at startup */
	  @Override
	  protected void onResume() {
	    super.onResume();
//	    locationManager.requestLocationUpdates(provider, 1000, 1, this);
	  }

	  /* Remove the locationlistener updates when Activity is paused */
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	  }
	  
	  private void viewAll(boolean value){
		  
		  Log.d("valueall", ""+value);
		  
		  if(value){	
			  Log.d("if valueall", ""+value);
			task_user_ed.setFocusableInTouchMode(true);
			task_customer_ed.setFocusableInTouchMode(true);
			task_name_ed.setFocusableInTouchMode(true);
			task_desc_ed.setFocusableInTouchMode(true);
			task_date_ed.setFocusableInTouchMode(true);
			task_duedate_ed.setFocusableInTouchMode(true);
			task_priority_ed.setFocusableInTouchMode(true);
			task_status_ed.setFocusableInTouchMode(true);
			
//			task_user_ed.setEnabled(true);
//			task_name_ed.setEnabled(true);
//			task_desc_ed.setEnabled(true);
//			task_date_ed.setEnabled(true);
//			task_duedate_ed.setEnabled(true);
//			task_priority_ed.setEnabled(true);
//			task_status_ed.setEnabled(true);
			
			task_user_ed.setBackgroundResource(drawable.crm_edittext);
			task_customer_ed.setBackgroundResource(drawable.crm_edittext);
			task_name_ed.setBackgroundResource(drawable.crm_edittext);
			task_desc_ed.setBackgroundResource(drawable.crm_edittext);
			task_date_ed.setBackgroundResource(drawable.crm_edittext);
			task_duedate_ed.setBackgroundResource(drawable.crm_edittext);
			task_priority_ed.setBackgroundResource(drawable.crm_spinner);
			task_status_ed.setBackgroundResource(drawable.crm_spinner);
			
			
			
		  }else{
			  Log.d("else valueall", ""+value);
			  
			task_user_ed.setBackgroundResource(drawable.crm_edittext_disable);
			task_customer_ed.setBackgroundResource(drawable.crm_edittext_disable);
			task_name_ed.setBackgroundResource(drawable.crm_edittext_disable);
			task_desc_ed.setBackgroundResource(drawable.crm_edittext_disable);
			task_date_ed.setBackgroundResource(drawable.crm_edittext_disable);
			task_duedate_ed.setBackgroundResource(drawable.crm_edittext_disable);
			task_priority_ed.setBackgroundResource(drawable.crm_edittext_disable);
			task_status_ed.setBackgroundResource(drawable.crm_edittext_disable);
			
			task_user_ed.setFocusable(false);
			task_customer_ed.setFocusable(false);
			task_name_ed.setFocusable(false);
			task_desc_ed.setFocusable(false);
			task_date_ed.setFocusable(false);
			task_duedate_ed.setFocusable(false);
			task_priority_ed.setFocusable(false);
			task_status_ed.setFocusable(false);
  
		  }
		  
	  }
	
	public void loadprogress(){
		spinnerLayout = new LinearLayout(CRMTaskAdd.this);
		spinnerLayout.setGravity(Gravity.CENTER);
		addContentView(spinnerLayout, new LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		spinnerLayout.setBackgroundColor(Color.parseColor("#80000000"));
		enableViews(crmtask_parent, false);
		progressBar = new ProgressBar(CRMTaskAdd.this);
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
	
	private void onError(final ErrorType error) {
		new Thread() {
			@Override
			public void run() {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						if (error == ErrorType.NETWORK_UNAVAILABLE) {
//							helper.showLongToast(R.string.error_showing_image_no_network_connection);
						} else {

						}
					}
				});
			}
		}.start();
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
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(CRMTaskAdd.this, CRMTaskActivity.class);
		startActivity(i);
		CRMTaskAdd.this.finish();
	}

}
